/*
 * Copyright 2018 ZComApproach Inc.
 *
 * Licensed under multiple open source licenses involved in the project (the "Licenses");
 * you may not use this file except in compliance with the Licenses.
 * You may obtain copies of the Licenses at
 *
 *      http://www.zcomapproach.com/licenses
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zcomapproach.garden.rose.bean.state;

import com.zcomapproach.garden.email.GardenEmailMessage;
import com.zcomapproach.garden.email.GardenEmailSerializer;
import com.zcomapproach.garden.email.rose.RoseEmailMessage;
import com.zcomapproach.garden.email.rose.IRoseEmailStorage;
import com.zcomapproach.garden.email.HostMonsterEmailSettings;
import com.zcomapproach.garden.email.data.GardenEmailFolderName;
import com.zcomapproach.garden.email.rose.RoseEmailUtils;
import com.zcomapproach.garden.rose.data.profile.EmployeeAccountProfile;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenEnvironment;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread-safe
 * @author zhijun98
 */
public class RoseEmailStorage implements IRoseEmailStorage{
    
    private final EmployeeAccountProfile ownerAccountProfile;
    
    private final RoseEmailContainer emailContainer;
    
    private final String attachmentDownloadFileLocation;

    private boolean emailStorageLoaded;
    
    private boolean messageStorageLaunching;
    
    private boolean messageLoadingInterrupted;
    
    private boolean targetMessageContentLoading;

    private boolean messageDeleting;
    
    /**
     * a specific message target for display
     */
    private RoseEmailMessage targetEmailMessage;
    
    private RoseEmailMessage targetSentEmailMessage;
    
    public RoseEmailStorage(EmployeeAccountProfile ownerAccountProfile, String attachmentDownloadFileLocation) {
        this.ownerAccountProfile = ownerAccountProfile;
        this.emailContainer = new RoseEmailContainer();
        this.attachmentDownloadFileLocation = attachmentDownloadFileLocation;
        this.emailStorageLoaded = false;
    }

    @Override
    public synchronized String getAttachmentDownloadFileLocation() {
        return attachmentDownloadFileLocation;
    }

    public boolean isMessageMarkedForContentLoadingEmpty() {
        synchronized(emailContainer){
            return emailContainer.getMessageIdsForContentLoading().isEmpty();
        }
    }

    @Override
    public synchronized RoseEmailMessage getTargetEmailMessage() {
        if (targetEmailMessage == null){
            targetEmailMessage = new RoseEmailMessage();
        }
        return targetEmailMessage;
    }

    @Override
    public synchronized RoseEmailMessage getTargetSentEmailMessage() {
        if (targetSentEmailMessage == null){
            targetSentEmailMessage = new RoseEmailMessage();
        }
        return targetSentEmailMessage;
    }

    @Override
    public synchronized void setTargetSentEmailMessage(RoseEmailMessage aRoseEmailMessage) {
        targetSentEmailMessage = aRoseEmailMessage;
    }

    @Override
    public List<RoseEmailMessage> getMessagesForContentLoading() {
        List<RoseEmailMessage> result = new ArrayList<>();
        synchronized(emailContainer){
            for (Long msgUid : emailContainer.getMessageIdsForContentLoading()){
                result.add(this.retrieveGardenEmailMessageFromStore(msgUid));
            }
        }
        return result;
    }

    @Override
    public void clearMessagesForDeletion() {
        synchronized(emailContainer){
            emailContainer.getMessageIdsForDeletion().clear();
        }
    }

    @Override
    public void clearMessagesForContentLoading() {
        synchronized(emailContainer){
            emailContainer.getMessageIdsForContentLoading().clear();
        }
    }

    @Override
    public List<RoseEmailMessage> getMessagesForDeletion() {
        List<RoseEmailMessage> result = new ArrayList<>();
        synchronized(emailContainer){
            for (Long msgUid : emailContainer.getMessageIdsForDeletion()){
                result.add(this.retrieveGardenEmailMessageFromStore(msgUid));
            }
        }
        return result;
    }

    @Override
    public List<Long> getMessageIdsForContentLoading() {
        synchronized(emailContainer){
            return new ArrayList<>(emailContainer.getMessageIdsForContentLoading());
        }
    }

    @Override
    public List<Long> getMessageIdsForDeletion() {
        synchronized(emailContainer){
            return new ArrayList<>(emailContainer.getMessageIdsForDeletion());
        }
    }

    public boolean markTargetEmailMessageForContentLoading(long msgUid) {
        synchronized(emailContainer){
            targetEmailMessage = emailContainer.getEmailMessageStore().get(msgUid);
            if (targetEmailMessage == null){
                return false;
            }
            if (!targetEmailMessage.isContentLoaded()){
                emailContainer.getMessageIdsForContentLoading().add(msgUid);
            }
            return true;
        }
    }

    public void markMessageForDeletion(long msgUid) {
        synchronized(emailContainer){
            RoseEmailMessage aGardenEmailMessage = emailContainer.getEmailMessageStore().get(msgUid);
            if (aGardenEmailMessage != null){
                getEmailsFromFolder(GardenEmailFolderName.GARDEN_INBOX).remove(aGardenEmailMessage);
                getEmailsFromFolder(GardenEmailFolderName.GARDEN_GARBAGE).remove(aGardenEmailMessage);
                emailContainer.getMessageIdsForDeletion().remove(msgUid);
                emailContainer.getMessageIdsForDeletion().add(msgUid);
            }
        }
    }
    
    private void markMessageForDeletionHelper(RoseEmailMessage aGardenEmailMessage){
        synchronized(emailContainer){
            getEmailsFromFolder(GardenEmailFolderName.GARDEN_INBOX).remove(aGardenEmailMessage);
            getEmailsFromFolder(GardenEmailFolderName.GARDEN_GARBAGE).remove(aGardenEmailMessage);
            emailContainer.getMessageIdsForDeletion().remove(aGardenEmailMessage.getMsgUid());
            emailContainer.getMessageIdsForDeletion().add(aGardenEmailMessage.getMsgUid());
        }
    }

    public void markAllGarbageMessagesForDeletion() {
        synchronized(emailContainer){
            List<RoseEmailMessage> aGardenEmailMessageList = this.getRoseGarbageMessageList();
            for (RoseEmailMessage aGardenEmailMessage : aGardenEmailMessageList){
                markMessageForDeletionHelper(aGardenEmailMessage);
            }
        }
    }

    @Override
    public synchronized void clearEmailStorage(boolean clearAttachments) {
        synchronized(emailContainer){
            emailContainer.getMessageIdsForDeletion().clear();
            emailContainer.getMessageIdsForContentLoading().clear();
            for (List<RoseEmailMessage> aGardenEmailMessageList : emailContainer.getMessagesCategorizedByFolders().values()){
                //delete all the physical attachment files from the server
//                if (clearAttachments){
//                    for (RoseEmailMessage aGardenEmailMessage : aGardenEmailMessageList){
//                        aGardenEmailMessage.deleteAttachementFiles();
//                    }//for
//                }//if
                //clean up the space
                aGardenEmailMessageList.clear();
            }//for
            emailContainer.getEmailMessageStore().clear();
        }
        emailStorageLoaded = false;
        messageStorageLaunching = false;
        targetMessageContentLoading = false;
        messageDeleting = false;
        targetEmailMessage = null;
    }
    
    @Override
    public synchronized boolean isMessageDeleting(){
        return messageDeleting;
    }

    @Override
    public synchronized void setMessageDeleting(boolean messageDeleting){
        this.messageDeleting = messageDeleting;
    }

    @Override
    public synchronized boolean isTargetMessageContentLoading() {
        return targetMessageContentLoading;
    }

    @Override
    public synchronized void setTargetMessageContentLoading(boolean targetMessageContentLoading) {
        this.targetMessageContentLoading = targetMessageContentLoading;
    }

    @Override
    public synchronized boolean isMessageStorageLaunching() {
        return messageStorageLaunching;
    }

    @Override
    public synchronized void setMessageStorageLaunching(boolean messageStorageLaunching) {
        this.messageStorageLaunching = messageStorageLaunching;
    }

    @Override
    public synchronized boolean isMessageLoadingInterrupted() {
        return messageLoadingInterrupted;
    }

    @Override
    public synchronized void setMessageLoadingInterrupted(boolean messageLoadingInterrupted) {
        this.messageLoadingInterrupted = messageLoadingInterrupted;
    }

    @Override
    public synchronized boolean isEmailStorageLoaded() {
        return emailStorageLoaded;
    }

    @Override
    public synchronized void setEmailStorageLoaded(boolean emailStorageLoaded) {
        this.emailStorageLoaded = emailStorageLoaded;
    }

    @Override
    public String getOwnerEmployeeUuid() {
        return ownerAccountProfile.getEmployeeEntity().getEmployeeAccountUuid();
    }

    @Override
    public synchronized String getOwnerEmailAddress() {
        return ownerAccountProfile.getEmployeeEntity().getWorkEmail();
    }

    @Override
    public synchronized String getOwnerEmailPassword() {
        return HostMonsterEmailSettings.Hint.value();
    }

    /**
     * Try to get the email message from the memory container. if failed, it will 
     * try to get it from the persistent storage. If failed again, NULL returns.
     * @param msgUid
     * @return 
     */
    @Override
    public RoseEmailMessage retrieveGardenEmailMessageFromStore(long msgUid) {
        synchronized(emailContainer){
            return emailContainer.getEmailMessageStore().get(msgUid);
        }
    }

    @Override
    public RoseEmailMessage removeGardenEmailMessageFromStore(long msgUid) {
        synchronized(emailContainer){
            return emailContainer.getEmailMessageStore().remove(msgUid);
        }
    }

    @Override
    public void storeSentEmailMessage(RoseEmailMessage aGardenEmailMessage) {
        if (aGardenEmailMessage == null){
            return;
        }
        synchronized(emailContainer){
            getEmailsFromFolder(GardenEmailFolderName.GARDEN_SENT).remove(aGardenEmailMessage);
            getEmailsFromFolder(GardenEmailFolderName.GARDEN_SENT).add(0, aGardenEmailMessage);
        }
    }

    @Override
    public void storeGardenEmailMessageOnTop(RoseEmailMessage aGardenEmailMessage) {
        storeGardenEmailMessageHelper(aGardenEmailMessage, 0);
    }

    @Override
    public void storeGardenEmailMessageAtEnd(RoseEmailMessage aGardenEmailMessage) {
        storeGardenEmailMessageHelper(aGardenEmailMessage, -1);
    }
    
    private void storeGardenEmailMessageHelper(RoseEmailMessage aGardenEmailMessage, int index) {
        if (aGardenEmailMessage == null){
            return;
        }
        synchronized(emailContainer){
            if (emailContainer.getEmailMessageStore().containsKey(aGardenEmailMessage.getMsgUid())){
                getEmailsFromFolder(GardenEmailFolderName.GARDEN_GARBAGE).remove(aGardenEmailMessage);
                getEmailsFromFolder(GardenEmailFolderName.GARDEN_INBOX).remove(aGardenEmailMessage);
                if (RoseEmailUtils.isSpamEmail(aGardenEmailMessage)){
                    getEmailsFromFolder(GardenEmailFolderName.GARDEN_GARBAGE).add(aGardenEmailMessage);
                }else{
                    getEmailsFromFolder(GardenEmailFolderName.GARDEN_INBOX).add(aGardenEmailMessage);
                }
            }else{
                if (index < 0){
                    getEmailsFromFolder(GardenEmailFolderName.GARDEN_INBOX).add(aGardenEmailMessage);
                }else{
                    //fresh new message just loaded from the server
                    getEmailsFromFolder(GardenEmailFolderName.GARDEN_INBOX).add(index, aGardenEmailMessage);
                }
                emailContainer.getEmailMessageStore().put(aGardenEmailMessage.getMsgUid(), aGardenEmailMessage);
            }
        }
    }
    
    private List<RoseEmailMessage> getEmailsFromFolder(GardenEmailFolderName folder) {
        synchronized(emailContainer){
            List<RoseEmailMessage> resultList = emailContainer.getMessagesCategorizedByFolders().get(folder);
            if (resultList == null){
                resultList = new ArrayList<>();
                emailContainer.getMessagesCategorizedByFolders().put(folder, resultList);
            }
            return resultList;
        }
    }

    public List<RoseEmailMessage> getRoseInBoxMessageList() {
        synchronized(emailContainer){
            return new ArrayList<>(getEmailsFromFolder(GardenEmailFolderName.GARDEN_INBOX));
        }
    }

    public List<RoseEmailMessage> getRoseGarbageMessageList() {
        synchronized(emailContainer){
            return new ArrayList<>(getEmailsFromFolder(GardenEmailFolderName.GARDEN_GARBAGE));
        }
    }

    public List<RoseEmailMessage> getRoseSentMessageList() {
        synchronized(emailContainer){
            return new ArrayList<>(getEmailsFromFolder(GardenEmailFolderName.GARDEN_SENT));
        }
    }

    public void moveEmailIntoGarbage(long msgUid) {
        synchronized(emailContainer){
            RoseEmailMessage aGardenEmailMessage = emailContainer.getEmailMessageStore().get(msgUid);
            if (aGardenEmailMessage != null){
                getEmailsFromFolder(GardenEmailFolderName.GARDEN_INBOX).remove(aGardenEmailMessage);
                getEmailsFromFolder(GardenEmailFolderName.GARDEN_GARBAGE).add(aGardenEmailMessage);
            }
        }
    }

    public void rollbackEmailToInbox(long msgUid) {
        synchronized(emailContainer){
            RoseEmailMessage aGardenEmailMessage = emailContainer.getEmailMessageStore().get(msgUid);
            if (aGardenEmailMessage != null){
                getEmailsFromFolder(GardenEmailFolderName.GARDEN_INBOX).add(aGardenEmailMessage);
                getEmailsFromFolder(GardenEmailFolderName.GARDEN_GARBAGE).remove(aGardenEmailMessage);
            }
            emailContainer.getMessageIdsForDeletion().remove(msgUid);
        }
    }

    @Override
    public void serialize(RoseEmailMessage aGardenEmailMessage) {
        synchronized(emailContainer){
            if (aGardenEmailMessage == null){
                return;
            }
            if (ZcaValidator.isNullEmpty(aGardenEmailMessage.getMailFolderName())){
                aGardenEmailMessage.setMailFolderName(GardenEmailFolderName.GARDEN_INBOX.value());
            }
            try{
                //serializeSentAttachments(employeeUuid, aGardenEmailMessage, emailSerializationFolder);
                Path dataFilePath = GardenEmailMessage.generateEmailMessageSerializedFileFullPath(
                                        ownerAccountProfile.getEmployeeEntity().getEmployeeAccountUuid(), ownerAccountProfile.getEmployeeEntity().getWorkEmail(),
                                        aGardenEmailMessage.getMsgUid()+"", GardenEnvironment.retrieveRoseEmailStorageLocation(), aGardenEmailMessage.getMailFolderName());
                if (Files.isRegularFile(dataFilePath)){
                    Files.delete(dataFilePath); //this is for update-case: some serialieed file demands update itself 
                }
                try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dataFilePath.toFile()))){
                    out.writeObject(aGardenEmailMessage);
                }
            }catch(IOException ex){
                Logger.getLogger(GardenEmailSerializer.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    @Override
    public RoseEmailMessage deserialize(long msgUid, String folderName) {
        synchronized(emailContainer){
            RoseEmailMessage aRoseEmailMessage = null;
            try{
                Path messageDataFilePath = GardenEmailMessage.generateEmailMessageSerializedFileFullPath(
                                                ownerAccountProfile.getEmployeeEntity().getEmployeeAccountUuid(), ownerAccountProfile.getEmployeeEntity().getWorkEmail(),
                                                msgUid+"", GardenEnvironment.retrieveRoseEmailStorageLocation(), folderName);
                if (Files.isRegularFile(messageDataFilePath)){
                    try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(messageDataFilePath.toFile()))){
                        aRoseEmailMessage = (RoseEmailMessage)in.readObject();
                    }
                }
            }catch(IOException | ClassNotFoundException ex){
                aRoseEmailMessage = null;
            }

            return aRoseEmailMessage;
        }
    }

    public void serializeSentEmailMessage(RoseEmailMessage aRoseEmailMessage) {
        if (aRoseEmailMessage == null){
            return;
        }
        //make sure sent-folder
        aRoseEmailMessage.setMailFolderName(GardenEmailFolderName.GARDEN_SENT.value());
        //make sure its msgUid ready
        if (aRoseEmailMessage.getMsgUid() == 0){
            aRoseEmailMessage.setMsgUid(getNewSentMsgUid());
        }
        //if not serialized yet, serialize it
        if (!isLoadedSentMsgUidSetContained(aRoseEmailMessage.getMsgUid())){
            addIntoLoadedSentMsgUidSet(aRoseEmailMessage.getMsgUid());
            storeSentEmailMessage(aRoseEmailMessage);
            serialize(aRoseEmailMessage);
        }
    }
    
    /**
     * This data structured is synchronized by itself
     */
    private final TreeSet<Long> loadedSentMsgUidSet = new TreeSet<>();
    
    @Override
    public boolean isLoadedSentMsgUidSetContained(long messageUuid){
        synchronized(loadedSentMsgUidSet){
            return loadedSentMsgUidSet.contains(messageUuid);
        }
    }
    
    @Override
    public void addIntoLoadedSentMsgUidSet(long messageUuid){
        synchronized(loadedSentMsgUidSet){
            loadedSentMsgUidSet.add(messageUuid);
        }
    }
    
    private long getNewSentMsgUid(){
        synchronized(loadedSentMsgUidSet){
            if (loadedSentMsgUidSet.isEmpty()){
                return 1;
            }
            return loadedSentMsgUidSet.last() + 1;
        }
    }

}
