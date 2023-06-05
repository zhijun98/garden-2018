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

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.email.GardenEmailMessage;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyDataUtils;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.persistence.entity.G02OfflineEmail;
import com.zcomapproach.garden.persistence.entity.G02OfflineEmailAttachment;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmail;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineMailbox;
import com.zcomapproach.garden.persistence.peony.data.QueryNewOfflineEmailCriteria;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.garden.util.GardenData;
import com.zcomapproach.garden.util.GardenHttpClientUtils;
import com.zcomapproach.commons.nio.ZcaNio;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javafx.concurrent.Task;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class UserLocalEmailBoxLoadingTask extends Task<Void> {
    
    private final UserLocalEmailBox targetUserLocalEmailBox;
    
    /**
     * This data sturcture replaces the role of loadedSentMsgUidSet in the super 
     * class. Thus, in the case of offline mailbox, isLoadedSentMsgUidSetContained 
     * and addIntoLoadedSentMsgUidSet methods are not used in practice
     */
    private final QueryNewOfflineEmailCriteria queryNewOfflineEmailCriteria;

    public UserLocalEmailBoxLoadingTask(UserLocalEmailBox targetUserLocalEmailBox, QueryNewOfflineEmailCriteria queryNewOfflineEmailCriteria) {
        this.targetUserLocalEmailBox = targetUserLocalEmailBox;
        this.queryNewOfflineEmailCriteria = queryNewOfflineEmailCriteria;
        queryNewOfflineEmailCriteria.setEmployeeAccountUuid(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
        queryNewOfflineEmailCriteria.setEmployeeEmailAddress(PeonyProperties.getSingleton().getOfflineEmailAddress());
    }

    @Override
    protected Void call() throws Exception {
        /**
         * (1) retrieve all the offline emails which demand loading
         */
        PeonyOfflineMailbox aPeonyOfflineMailbox = Lookup.getDefault()
                .lookup(PeonyManagementService.class).getPeonyManagementRestClient()
                .storeEntity_XML(PeonyOfflineMailbox.class, 
                                GardenRestParams.Management.retrievePeonyOfflineMailboxRestParams(),
                                queryNewOfflineEmailCriteria);
        if (aPeonyOfflineMailbox != null){
            List<PeonyOfflineEmail> aPeonyOfflineEmailList = new ArrayList<>();
            /**
             * receivedPeonyOfflineEmailList
             */
            List<PeonyOfflineEmail> receivedPeonyOfflineEmailList = aPeonyOfflineMailbox.getReceivedOfflineEmailList();
            Collections.sort(receivedPeonyOfflineEmailList, new Comparator<PeonyOfflineEmail>(){
                @Override
                public int compare(PeonyOfflineEmail o1, PeonyOfflineEmail o2) {
                    try{
                        Long m1 = GardenData.convertToLong(o1.getOfflineEmail().getMsgId());
                        Long m2 = GardenData.convertToLong(o2.getOfflineEmail().getMsgId());
                        return m1.compareTo(m2)*(-1);
                    }catch(Exception ex){
                        return 0;
                    }catch(Throwable ex){
                        return 0;
                    }
                }
            });
            aPeonyOfflineEmailList.addAll(receivedPeonyOfflineEmailList);
            /**
             * sentPeonyOfflineEmailList
             */
            List<PeonyOfflineEmail> sentPeonyOfflineEmailList = aPeonyOfflineMailbox.getSentOfflineEmailList();
                Collections.sort(sentPeonyOfflineEmailList, new Comparator<PeonyOfflineEmail>(){
                    @Override
                    public int compare(PeonyOfflineEmail o1, PeonyOfflineEmail o2) {
                        try{
                            return o1.getOfflineEmail().getCreated().compareTo(o2.getOfflineEmail().getCreated())*(-1);
                        }catch(Exception ex){
                            return 0;
                        }
                    }
                });
            aPeonyOfflineEmailList.addAll(sentPeonyOfflineEmailList);

            GardenEmailMessage aGardenEmailMessage;
            Set<String> filterMsgId = queryNewOfflineEmailCriteria.getMsgIdFilterSet();
            for (PeonyOfflineEmail aPeonyOfflineEmail : aPeonyOfflineEmailList){
        
                /**
                 * (2) deserialize from the local storage for aGardenEmailMessage and if the aGardenEmailMessage is null, 
                 * it means that the corresponding data is ready on the local storage.
                 */
                aGardenEmailMessage = PeonyDataUtils.convertToGardenEmailMessageByDeserialization(aPeonyOfflineEmail);
                if (aGardenEmailMessage == null){
                    /**
                     * (3) if the previous step failed, clean up all the possible files in the local storage in case of 
                     * some corrupted files and then download the corresponding persistent data so as to have aGardenEmailMessage.
                     */
                    aGardenEmailMessage = deserializeGardenEmailMessageByDownload(aPeonyOfflineEmail);
                }
                if (aGardenEmailMessage != null){
                    if (ZcaValidator.isNotNullEmpty(aPeonyOfflineEmail.getOfflineEmail().getMessageFaceFolder())){
                        //respect aPeonyOfflineEmail.getOfflineEmail().getMessageFaceFolder()
                        aGardenEmailMessage.setFolderFullName(aPeonyOfflineEmail.getOfflineEmail().getMessageFaceFolder());
                    }
                    /**
                     * (4) broadcast to load the corresponding persistent data from the local storage
                     */
                    String msgIdText = aPeonyOfflineEmail.getOfflineEmail().getMsgId();
                    if (!filterMsgId.contains(msgIdText)){
                        filterMsgId.add(msgIdText);
                        targetUserLocalEmailBox.broadcastGardenEmailMessageLoaded(aPeonyOfflineEmail, aGardenEmailMessage);
                        Thread.sleep(50);
                    }
                }//if (aGardenEmailMessage != null){
            }
        }
        return null;
    }

    @Override
    protected void failed() {
        PeonyFaceUtils.publishMessageOntoOutputWindow("Peony local email lost connection to retrieve new emails. "
                + "If this problem persists, please contact a technical administrator. " + getMessage());
    }

    /**
     * download the corresponding persistent data and then deserialize it to create aGardenEmailMessage.
     * @param aPeonyOfflineEmail
     * @return 
     */
    private GardenEmailMessage deserializeGardenEmailMessageByDownload(PeonyOfflineEmail aPeonyOfflineEmail) {
        cleanupPossiblyCorruptedFiles(aPeonyOfflineEmail);
        
        G02OfflineEmail aG02OfflineEmail = aPeonyOfflineEmail.getOfflineEmail();
        String getUrl = PeonyProperties.getSingleton().getRoseDownloadGetUrl();
        String ownerUserUuid = aG02OfflineEmail.getOwnerUserUuid();
        try {
            GardenHttpClientUtils.downloadGmailFileFromRoseWeb(getUrl, ownerUserUuid, aG02OfflineEmail.getMailboxAddress(),
                    aG02OfflineEmail.getMsgId(), new File(aG02OfflineEmail.getMessagePath()));
        } catch (IOException ex) {
            //Exceptions.printStackTrace(ex);
            PeonyFaceUtils.publishMessageOntoOutputWindow("Peony cannot successfully download the email file from the server-side. "
                + "The msg-ID is " + aG02OfflineEmail.getMsgId() + ". " + getMessage());
            return null;
        }
        
        try {
            downloadAttachmentsInGardenEmailMessage(aPeonyOfflineEmail);
        } catch (IOException ex) {
            //Exceptions.printStackTrace(ex);
            PeonyFaceUtils.publishMessageOntoOutputWindow(ex.getMessage());
            return null;
        }
        
        return PeonyDataUtils.convertToGardenEmailMessageByDeserialization(aPeonyOfflineEmail);
    }

    /**
     * clean up all the possible files, whose information are contained in aPeonyOfflineEmail,
     * in the local storage because deserialization for aGardenEmailMessage may failed because 
     * of some corrupted files. In this case, the corrupted ones should be deleted
     * 
     * @param aPeonyOfflineEmail
     * @return 
     */
    private void cleanupPossiblyCorruptedFiles(PeonyOfflineEmail aPeonyOfflineEmail) {
        try {
            ZcaNio.deleteFile(aPeonyOfflineEmail.getOfflineEmail().getMessagePath());
        } catch (IOException ex) {}
        
        List<G02OfflineEmailAttachment> aG02OfflineEmailAttachmentList = aPeonyOfflineEmail.getOfflineEmailAttachmentList();
        if (aG02OfflineEmailAttachmentList != null){
            for (G02OfflineEmailAttachment aG02OfflineEmailAttachment : aG02OfflineEmailAttachmentList){
                try {
                    ZcaNio.deleteFile(aG02OfflineEmailAttachment.getFilePath());
                } catch (IOException ex) {}
            }//for-loop
        }
    }

    private void downloadAttachmentsInGardenEmailMessage(PeonyOfflineEmail aPeonyOfflineEmail) throws IOException {
        List<G02OfflineEmailAttachment> aG02OfflineEmailAttachmentList = aPeonyOfflineEmail.getOfflineEmailAttachmentList();
        if (aG02OfflineEmailAttachmentList != null){
            G02OfflineEmail aG02OfflineEmail = aPeonyOfflineEmail.getOfflineEmail();
            String getUrl = PeonyProperties.getSingleton().getRoseDownloadGetUrl();
            String ownerUserUuid = aG02OfflineEmail.getOwnerUserUuid();
            for (G02OfflineEmailAttachment aG02OfflineEmailAttachment : aG02OfflineEmailAttachmentList){
                try {
                    GardenHttpClientUtils.downloadGmailAttFileFromRoseWeb(getUrl, ownerUserUuid, aG02OfflineEmail.getMailboxAddress(),
                        aG02OfflineEmail.getMsgId(), aG02OfflineEmailAttachment.getAttachmentUuid(), new File(aG02OfflineEmailAttachment.getFilePath()));
                } catch (IOException ex) {
                    //Exceptions.printStackTrace(ex);
                    throw new IOException("Peony cannot successfully download the email attachment file from the server-side. "
                        + "The attachment-UUID is " + aG02OfflineEmailAttachment.getAttachmentUuid() + ". " + getMessage());
                }
            }//for-loop
        }
    }
}
