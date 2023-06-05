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

import com.zcomapproach.garden.email.data.GardenEmailFolderName;
import com.zcomapproach.garden.email.rose.RoseEmailMessage;
import com.zcomapproach.garden.email.rose.RoseImap4Session;
import com.zcomapproach.garden.rose.data.profile.EmployeeAccountProfile;
import com.zcomapproach.commons.ZcaValidator;
import java.util.List;

/**
 *
 * @author zhijun98
 */
public class RoseEmailManager {
    
    private final RoseImap4Session roseImap4Loader;
    private final RoseImap4Session roseImap4Worker;
    private final RoseEmailStorage roseEmailStorage;
    
    public RoseEmailManager(EmployeeAccountProfile ownerAccountProfile, String attachmentDownloadFileLocation) throws Exception{
        if ((ownerAccountProfile == null) || (ZcaValidator.isNullEmpty(ownerAccountProfile.getTargetPersonUuid()))){
            throw new Exception("[TECH] A valid ownerAccountProfile is demanded by RoseEmailManager");
        }
        this.roseEmailStorage = new RoseEmailStorage(ownerAccountProfile, attachmentDownloadFileLocation);
        this.roseImap4Loader = new RoseImap4Session();
        this.roseImap4Worker = new RoseImap4Session();
    }
    
    public void loadSentEmailsByManager() {
        roseImap4Loader.loadSentEmailFromStorage(roseEmailStorage);
    }

    public void loadEmailStorageByManager() {
        roseImap4Loader.loadEmailStorageByImap4Session(roseEmailStorage);
    }

    public void refreshEmailStorageByManager() {
        roseImap4Loader.loadEmailStorageByImap4Session(roseEmailStorage);
        roseImap4Worker.deleteEmailMessages(roseEmailStorage);
    }

    public void refreshSentEmailsByManager() {
        roseImap4Loader.loadSentEmailFromStorage(roseEmailStorage);
    }

    public void shutdownEmailStorageByManager(boolean clearAttachments) {
        roseImap4Loader.shutdownEmailStorageByImap4Session(roseEmailStorage, clearAttachments);
        roseImap4Worker.shutdownEmailStorageByImap4Session(roseEmailStorage, clearAttachments);
    }

    public void parseTargetEmailContentWithAttachments() {
        roseImap4Worker.parseEmailContentWithAttachments(roseEmailStorage);
    }

    public boolean isEmailStorageLoaded() {
        return roseEmailStorage.isEmailStorageLoaded();
    }

    public List<RoseEmailMessage> getRoseInBoxMessageList() {
        return roseEmailStorage.getRoseInBoxMessageList();
    }

    public List<RoseEmailMessage> getRoseGarbageMessageList() {
        return roseEmailStorage.getRoseGarbageMessageList();
    }

    public List<RoseEmailMessage> getRoseSentMessageList() {
        return roseEmailStorage.getRoseSentMessageList();
    }

    public void moveEmailIntoGarbage(long msgUid) {
        roseEmailStorage.moveEmailIntoGarbage(msgUid);
    }

    public void rollbackEmailToInbox(long msgUid) {
        roseEmailStorage.rollbackEmailToInbox(msgUid);
    }

    public boolean isTargetMessageContentLoading() {
        return roseEmailStorage.isTargetMessageContentLoading();
    }

    public void setTargetMessageContentLoading(boolean targetMessageContentLoading) {
        roseEmailStorage.setTargetMessageContentLoading(targetMessageContentLoading);
    }

    public boolean isMessageDeleting() {
        return roseEmailStorage.isMessageDeleting();
    }

    public void setMessageDeleting(boolean messageDeleting) {
        roseEmailStorage.setMessageDeleting(messageDeleting);
    }

    public void setMessageStorageLaunching(boolean messageStorageLaunching) {
        roseEmailStorage.setMessageStorageLaunching(messageStorageLaunching);
    }

    public boolean isMessageStorageLaunching() {
        return roseEmailStorage.isMessageStorageLaunching();
    }

    public RoseEmailMessage getTargetEmailMessage() {
        return roseEmailStorage.getTargetEmailMessage();
    }

    public RoseEmailMessage getTargetSentEmailMessage() {
        return roseEmailStorage.getTargetSentEmailMessage();
    }

    public void setTargetSentEmailMessage(RoseEmailMessage aRoseEmailMessage) {
        roseEmailStorage.setTargetSentEmailMessage(aRoseEmailMessage);
    }

    public boolean isTargetEmailContentLoaded() {
        return roseEmailStorage.getTargetEmailMessage().isContentLoaded();
    }

    public boolean isMessageMarkedForContentLoadingEmpty() {
        return roseEmailStorage.isMessageMarkedForContentLoadingEmpty();
    }

    public boolean markTargetEmailMessageForContentLoading(long msgUid) {
        return roseEmailStorage.markTargetEmailMessageForContentLoading(msgUid);
    }

    public void markMessageForDeletion(long msgUid) {
        roseEmailStorage.markMessageForDeletion(msgUid);
    }

    public void markAllGarbageMessagesForDeletion() {
        roseEmailStorage.markAllGarbageMessagesForDeletion();
    }

    public void serializeSentEmailMessage(RoseEmailMessage aRoseEmailMessage) {
        roseEmailStorage.serializeSentEmailMessage(aRoseEmailMessage);
    }

    public RoseEmailMessage derializeSentEmailMessage(long msgUid) {
        return roseEmailStorage.deserialize(msgUid, GardenEmailFolderName.GARDEN_SENT.value());
    }
    
}
