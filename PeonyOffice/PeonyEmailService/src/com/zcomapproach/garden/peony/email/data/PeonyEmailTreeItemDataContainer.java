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

package com.zcomapproach.garden.peony.email.data;

import com.zcomapproach.garden.email.GardenEmailMessage;
import com.zcomapproach.garden.email.data.GardenEmailFolderName;
import com.zcomapproach.garden.peony.settings.PeonySpamRuleManager;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.persistence.peony.PeonyEmailTag;
import com.zcomapproach.garden.persistence.peony.PeonyMemo;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmail;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

/**
 * 
 * @author zhijun98
 */
public class PeonyEmailTreeItemDataContainer {

    /**
     * VALUE: FolderNameMsgUid
     */
    private final HashMap<GardenEmailMessage, TreeItem<PeonyEmailTreeItemData>> loadedMessageSet = new HashMap<>();
    
    /**
     * key: mailFolderName;
     * value: TreeItem<PeonyEmailTreeItemData>
     */
    private final HashMap<String, TreeItem<PeonyEmailTreeItemData>> emailFolderItemSet = new HashMap<>();
    /**
     * This data structure helps quickly find aPeonyEmailTag
     * key: UUID of PeonyEmailTag or PeonyMemo
     * value: the corresponding TreeItem<PeonyEmailTreeItemData> instance which contains the email-message item that is the container of PeonyEmailTag or PeonyMemo
     */
    private final HashMap<String, TreeItem<PeonyEmailTreeItemData>> emailTagOrMemoItemSet = new HashMap<>();

    public PeonyEmailTreeItemDataContainer() {
    }

    public synchronized PeonyEmailTreeItemData findPeonyEmailTreeItemDataByOfflineEmailUuid(String offlineEmailUuid) {
        Collection<TreeItem<PeonyEmailTreeItemData>> aPeonyEmailTreeItemDataCollection = loadedMessageSet.values();
        PeonyOfflineEmail aPeonyOfflineEmail;
        for (TreeItem<PeonyEmailTreeItemData> aPeonyEmailTreeItem : aPeonyEmailTreeItemDataCollection){
            aPeonyOfflineEmail = aPeonyEmailTreeItem.getValue().getAssociatedPeonyOfflineEmail();
            if (aPeonyOfflineEmail != null){
                if (aPeonyOfflineEmail.getOfflineEmail().getOfflineEmailUuid().equalsIgnoreCase(offlineEmailUuid)){
                    return aPeonyEmailTreeItem.getValue();
                }
            }
        }//for
        return null;
    }

    public synchronized PeonyOfflineEmail getAssociatePeonyOfflineEmail(GardenEmailMessage aPeonyEmailMessage) {
        TreeItem<PeonyEmailTreeItemData> aPeonyEmailTreeItem = loadedMessageSet.get(aPeonyEmailMessage);
        if (aPeonyEmailTreeItem == null){
            return null;
        }
        return aPeonyEmailTreeItem.getValue().getAssociatedPeonyOfflineEmail();
    }
    
    /**
     * Create a tree item which represents aPeonyMemo, which will be added onto the children of emailTreeItemData
     * @param aPeonyMemo
     * @param emailTreeItemData
     * @return 
     */
    public synchronized TreeItem<PeonyEmailTreeItemData> constructOfflineEmailMemoTreeItem(PeonyMemo aPeonyMemo, TreeItem<PeonyEmailTreeItemData> emailTreeItemData) {
        PeonyEmailTreeItemData aPeonyEmailMemoTreeItemData = new PeonyEmailTreeItemData(aPeonyMemo, PeonyEmailTreeItemData.EmailItemType.NOTE);
        TreeItem<PeonyEmailTreeItemData> newEmaiLMemoItem = new TreeItem<>(aPeonyEmailMemoTreeItemData);
        newEmaiLMemoItem.setGraphic(new ImageView(PeonyGraphic.getJavaFxImage("note_edit.png")));
        emailTreeItemData.getChildren().add(newEmaiLMemoItem);
        emailTreeItemData.setExpanded(true);
        /**
         * Help to quickly find email based on email's tag UUID
         */
        emailTagOrMemoItemSet.put(aPeonyMemo.getMemo().getMemoUuid(), emailTreeItemData);
        
        return newEmaiLMemoItem;
    }

    /**
     * Create a tree item which represents aPeonyEmailTag, which will be added onto the children of emailTreeItemData
     * @param aPeonyEmailTag
     * @param emailTreeItemData 
     */
    public synchronized TreeItem<PeonyEmailTreeItemData> constructEmailTagTreeItem(PeonyEmailTag aPeonyEmailTag, TreeItem<PeonyEmailTreeItemData> emailTreeItemData) {
        if ((aPeonyEmailTag == null) || (emailTreeItemData == null)){
            return null;
        }
        PeonyEmailTreeItemData aPeonyEmailTagTreeItemData = new PeonyEmailTreeItemData(aPeonyEmailTag, PeonyEmailTreeItemData.EmailItemType.TAG);
        TreeItem<PeonyEmailTreeItemData> newEmaiLTagItem = new TreeItem<>(aPeonyEmailTagTreeItemData);
        newEmaiLTagItem.setGraphic(new ImageView(PeonyGraphic.getJavaFxImage("red_star.png")));
        emailTreeItemData.getChildren().add(newEmaiLTagItem);
        emailTreeItemData.setExpanded(true);
        /**
         * Help to quickly find email based on email's tag UUID
         */
        emailTagOrMemoItemSet.put(aPeonyEmailTag.getEmailTag().getTagUuid(), emailTreeItemData);
        
        return newEmaiLTagItem;
    }
    
    /**
     * Create a concrete TreeItem<PeonyEmailTreeItemData> instance which contains aPeonyEmailMessage.
     * 
     * @param aGardenEmailMessage
     * @param messageFolderItem - the folder-tree-item which contains this email
     * @param index
     * @return 
     */
    public synchronized TreeItem<PeonyEmailTreeItemData> constructEmailMessageTreeItem(GardenEmailMessage aGardenEmailMessage, TreeItem<PeonyEmailTreeItemData> messageFolderItem, int index) {
        //check if it was loaded
        if (loadedMessageSet.containsKey(aGardenEmailMessage)){
            return null;
        }
        
        if (messageFolderItem == null){
            return null;
        }
        
        //get the message item
        PeonyEmailTreeItemData aPeonyEmailTreeItemData = new PeonyEmailTreeItemData(aGardenEmailMessage, PeonyEmailTreeItemData.EmailItemType.EMAIL);
        TreeItem<PeonyEmailTreeItemData> newMessageItem = new TreeItem<>(aPeonyEmailTreeItemData);
        loadedMessageSet.put(aGardenEmailMessage, newMessageItem);
        
        if (aGardenEmailMessage.isSeenFlag()){
            if (aGardenEmailMessage.isSentMessage()){
                newMessageItem.setGraphic(new ImageView(PeonyGraphic.getJavaFxImage("sent_msg.png")));
            }else if (aGardenEmailMessage.isTaskMessage()){
                newMessageItem.setGraphic(new ImageView(PeonyGraphic.getJavaFxImage("urgent_msg.png")));
            }else{
                newMessageItem.setGraphic(new ImageView(PeonyGraphic.getJavaFxImage("checked_msg.png")));
            }

        }else{
            if (aGardenEmailMessage.isTaskMessage()){
                newMessageItem.setGraphic(new ImageView(PeonyGraphic.getJavaFxImage("urgent_msg.png")));
            }else{
                newMessageItem.setGraphic(new ImageView(PeonyGraphic.getJavaFxImage("unchecked_msg.png")));
            }
        }
        //handle attachments
        if (aGardenEmailMessage.hasEmailAttachments()){
            String attachmentsText = aGardenEmailMessage.retrieveAttachmentFileNames();

            TreeItem<PeonyEmailTreeItemData> attItem = null;

            //brute-force search the attachment tree-item...
            for (TreeItem<PeonyEmailTreeItemData> aPeonyEmailTreeItemDataItem : newMessageItem.getChildren()){
                if (attachmentsText.equalsIgnoreCase(aPeonyEmailTreeItemDataItem.getValue().getUuid())){
                    attItem = aPeonyEmailTreeItemDataItem;
                    break;
                }
            }//for

            if (attItem == null){
                if (aGardenEmailMessage.isSeenFlag() && (!aGardenEmailMessage.isSentMessage())){
                    attItem = new TreeItem<>(new PeonyEmailTreeItemData(attachmentsText, PeonyEmailTreeItemData.EmailItemType.ATTACHEMNT), 
                                             new ImageView(PeonyGraphic.getJavaFxImage("attach_green.png")));
                }else{
                    attItem = new TreeItem<>(new PeonyEmailTreeItemData(attachmentsText, PeonyEmailTreeItemData.EmailItemType.ATTACHEMNT), 
                                             new ImageView(PeonyGraphic.getJavaFxImage("attach_blue.png")));
                }
                newMessageItem.getChildren().add(attItem);
            }
            newMessageItem.setExpanded(true);
        }
        return newMessageItem;
    }

    public synchronized TreeItem<PeonyEmailTreeItemData> getEmailMessageItem(TreeItem<PeonyEmailTreeItemData> messageFolderRoot, GardenEmailMessage aPeonyEmailMessage) {
        for (TreeItem<PeonyEmailTreeItemData> aPeonyEmailTreeItemData : messageFolderRoot.getChildren()){
            if (aPeonyEmailTreeItemData.getValue().getSortingEmailMessageUid().equalsIgnoreCase(aPeonyEmailMessage.getEmailMsgUid())){
                return aPeonyEmailTreeItemData;
            }
        }
        return null;
    }

    /**
     * 
     * @param mailFolderName
     * @return - if it was not find in the existing folders, NULL returns
     */
    public synchronized TreeItem<PeonyEmailTreeItemData> findEmailMessageFolder(String mailFolderName) {
        if (mailFolderName == null){
            return null;
        }
        return emailFolderItemSet.get(mailFolderName.toLowerCase());
    }
    
    /**
     * Collect all the folder names which are valid for "Move to Folder" operation
     * @return 
     */
    public synchronized List<String> getEmailFolderNameListForMove(){
        List<String> result = new ArrayList<>();
        
        Set<String> folderNames = emailFolderItemSet.keySet();
        for (String folderName : folderNames){
            if ((GardenEmailFolderName.GARDEN_SENT.value().equalsIgnoreCase(folderName))
                    || (GardenEmailFolderName.GARDEN_GARBAGE.value().equalsIgnoreCase(folderName))
                    || (GardenEmailFolderName.GARDEN_ATTACHMENT.value().equalsIgnoreCase(folderName))
                    || (GardenEmailFolderName.UNKNOWN.value().equalsIgnoreCase(folderName)))
            {
                //skip them; they are not valid for "Move to Folder" operation
            }else{
                result.add(folderName);
            }
        }//for
        if (result.isEmpty()){
            result.add(GardenEmailFolderName.GARDEN_INBOX.value());
        }
        return result;
    }
    
    /**
     * Collect all the folder names which are valid for "Delete Folder" operation
     * @return 
     */
    public synchronized List<String> getEmailFolderNameListForDeletion(){
        List<String> result = new ArrayList<>();
        
        Set<String> folderNames = emailFolderItemSet.keySet();
        
        for (String folderName : folderNames){
            if (GardenEmailFolderName.convertEnumValueToType(folderName, true) == null){
                //the ones, which are not included in GardenEmailFolderName, are valid for "Delete Folder" operation
                result.add(folderName);
            }
        }//for
        return result;
    }

    public synchronized TreeItem<PeonyEmailTreeItemData> constructEmailMessageFolderTreeItem(String mailFolderName) {
        TreeItem<PeonyEmailTreeItemData> messageFolderItemData = new TreeItem<>(new PeonyEmailTreeItemData(mailFolderName, PeonyEmailTreeItemData.EmailItemType.FOLDER),
                                               new ImageView(PeonyGraphic.getJavaFxImage("closed_folder.png")));
        messageFolderItemData.setExpanded(true);
        
        emailFolderItemSet.put(mailFolderName.toLowerCase(), messageFolderItemData);
        
        return messageFolderItemData;
    }
    
    public synchronized void moveMessageItemsFromFolderToFolder(List<TreeItem<PeonyEmailTreeItemData>> movingItems,
                                                                TreeItem<PeonyEmailTreeItemData> fromFolder, 
                                                                TreeItem<PeonyEmailTreeItemData> toFolder)
    {
        if ((movingItems != null) && (!movingItems.isEmpty())){
            List<TreeItem<PeonyEmailTreeItemData>> fromItems = fromFolder.getChildren();
            List<TreeItem<PeonyEmailTreeItemData>> toItems = toFolder.getChildren();
            for (TreeItem<PeonyEmailTreeItemData> movingItem : movingItems){
                fromItems.remove(movingItem);
                toItems.add(movingItem);
            }//for-loop
        }
    }

    /**
     * Check INBOX and move any items, which violated the spam rule, into SPAM folder
     * @param peonySpamRule 
     */
    public synchronized List<TreeItem<PeonyEmailTreeItemData>> retrieveItemsBreakingPeonySpamRule(PeonySpamRuleManager.PeonySpamRule peonySpamRule, 
                                                                                                  GardenEmailFolderName emailFolderName) {
        TreeItem<PeonyEmailTreeItemData> folderItem = findEmailMessageFolder(emailFolderName.value());
        if (folderItem == null){
            return new ArrayList<>();
        }
        List<TreeItem<PeonyEmailTreeItemData>> messageItems = folderItem.getChildren();
        
        //grab spam-messages
        List<TreeItem<PeonyEmailTreeItemData>> movingItems = new ArrayList<>();
        for (TreeItem<PeonyEmailTreeItemData> messageItem : messageItems){
            if (peonySpamRule.isSpam(messageItem.getValue().emitPeonyEmailMessage())){
                movingItems.add(messageItem);
            }
        }//for-loop
        return movingItems;
    }

    /**
     * Check SPAM and move any items, which do not violate the spam rule anymore, back into INBOX folder
     * @param peonySpamRule 
     */
    public synchronized void withdrawPeonySpamRule(PeonySpamRuleManager.PeonySpamRule peonySpamRule) {
        TreeItem<PeonyEmailTreeItemData> spamFolder = findEmailMessageFolder(GardenEmailFolderName.GARDEN_SPAM.value());
        if (spamFolder == null){
            return;
        }
        List<TreeItem<PeonyEmailTreeItemData>> spamItems = spamFolder.getChildren();
        List<TreeItem<PeonyEmailTreeItemData>> movingItems = new ArrayList<>();
        //grab spam-messages
        for (TreeItem<PeonyEmailTreeItemData> spamItem : spamItems){
            if (peonySpamRule.isSpam(spamItem.getValue().emitPeonyEmailMessage())){
                movingItems.add(spamItem);
            }
        }//for-loop
        
        //move spam-messages into INBOX
        if (!movingItems.isEmpty()){
            TreeItem<PeonyEmailTreeItemData> inboxFolder = findEmailMessageFolder(GardenEmailFolderName.GARDEN_INBOX.value());
            if (inboxFolder == null){
                return;
            }
            List<TreeItem<PeonyEmailTreeItemData>> inboxItems = inboxFolder.getChildren();
            for (TreeItem<PeonyEmailTreeItemData> movingItem : movingItems){
                inboxItems.add(movingItem);
                spamItems.remove(movingItem);
            }//for-loop
        }
    }

    public synchronized GardenEmailMessage findGardenEmailMessageByEmailTagUuid(String emailTagUuid) {
        TreeItem<PeonyEmailTreeItemData> itemData = emailTagOrMemoItemSet.get(emailTagUuid);
        if (itemData != null){
            return itemData.getValue().emitPeonyEmailMessage();
        }
        return null;
    }
    
    /**
     * According to aPeonyEmailMessageType, filter out invalid TreeItem<PeonyEmailTreeItemData>
     * @param aPeonyEmailMessageType
     * @return - key: folderName; value: new children list
     */
    public synchronized HashMap<String, List<TreeItem<PeonyEmailTreeItemData>>> filterByPeonyEmailMessageType(PeonyEmailMessageType aPeonyEmailMessageType){
        HashMap<String, List<TreeItem<PeonyEmailTreeItemData>>> result = new HashMap<>();
        Set<GardenEmailMessage> keys = loadedMessageSet.keySet();
        Iterator<GardenEmailMessage> itr = keys.iterator();
        GardenEmailMessage emailKey;
        TreeItem<PeonyEmailTreeItemData> itemValue;
        String folderFaceName;
        List<TreeItem<PeonyEmailTreeItemData>> newChildren;
        //loop over every email message
        while (itr.hasNext()){
            emailKey = itr.next();
            itemValue = loadedMessageSet.get(emailKey);
            folderFaceName = itemValue.getValue().getAssociatedPeonyOfflineEmail().getOfflineEmail().getMessageFaceFolder();
            newChildren = result.get(folderFaceName);
            if (newChildren == null){
                newChildren = new ArrayList<>();
                result.put(folderFaceName, newChildren);
            }
            if (isQualifyByPeonyEmailMessageType(emailKey, itemValue, aPeonyEmailMessageType)){
                newChildren.add(itemValue);
            }
        }//while
        
        return result;
    }

    private boolean isQualifyByPeonyEmailMessageType(GardenEmailMessage aGardenEmailMessage, TreeItem<PeonyEmailTreeItemData> aPeonyEmailTreeItem, PeonyEmailMessageType aPeonyEmailMessageType) {
        switch (aPeonyEmailMessageType){
            case ALL:
                return true;
            case UNREAD:
                return !aGardenEmailMessage.isSeenFlag();
            case READ:
                return aGardenEmailMessage.isSeenFlag();
            case ASSIGNED:
                return hasBeenTagged(aPeonyEmailTreeItem.getChildren());
            case NOTED:
                return hasBeenNoted(aPeonyEmailTreeItem.getChildren());
            case ATTACHED:
                return hasBeenAttached(aPeonyEmailTreeItem.getChildren());
            default:
                return false;
        }
    }

    private boolean hasBeenTagged(ObservableList<TreeItem<PeonyEmailTreeItemData>> childrenItem) {
        if (childrenItem != null){
            PeonyEmailTreeItemData data;
            for (TreeItem<PeonyEmailTreeItemData> dataItem: childrenItem){
                data = dataItem.getValue();
                if (PeonyEmailTreeItemData.EmailItemType.TAG.equals(data.getEmailItemType())){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasBeenNoted(ObservableList<TreeItem<PeonyEmailTreeItemData>> childrenItem) {
        if (childrenItem != null){
            PeonyEmailTreeItemData data;
            for (TreeItem<PeonyEmailTreeItemData> dataItem: childrenItem){
                data = dataItem.getValue();
                if (PeonyEmailTreeItemData.EmailItemType.NOTE.equals(data.getEmailItemType())){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasBeenAttached(ObservableList<TreeItem<PeonyEmailTreeItemData>> childrenItem) {
        if (childrenItem != null){
            PeonyEmailTreeItemData data;
            for (TreeItem<PeonyEmailTreeItemData> dataItem: childrenItem){
                data = dataItem.getValue();
                if (PeonyEmailTreeItemData.EmailItemType.ATTACHEMNT.equals(data.getEmailItemType())){
                    return true;
                }
            }
        }
        return false;
    }
}
