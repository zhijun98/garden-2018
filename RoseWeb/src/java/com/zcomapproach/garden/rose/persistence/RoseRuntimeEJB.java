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
package com.zcomapproach.garden.rose.persistence;

import com.zcomapproach.garden.aws.GardenAwsSettings;
import com.zcomapproach.garden.aws.RoseSmsSender;
import com.zcomapproach.garden.email.rose.RoseEmailSearcher;
import com.zcomapproach.garden.email.rose.RoseEmailSender;
import com.zcomapproach.garden.email.HostMonsterEmailSettings;
import com.zcomapproach.garden.email.rose.RoseEmailSubjectMatcher;
import com.zcomapproach.garden.persistence.GardenJpaUtils;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G01ArchivedDocument;
import com.zcomapproach.garden.persistence.entity.G01ChatMessage;
import com.zcomapproach.garden.rose.data.profile.RoseArchivedDocumentProfile;
import com.zcomapproach.garden.rose.data.profile.RoseChatMessageProfile;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenDebug;
import com.zcomapproach.commons.nio.ZcaNio;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import org.apache.commons.io.FilenameUtils;
import com.zcomapproach.garden.rose.bean.state.RoseEmailManager;

/**
 *
 * @author zhijun98
 */
@Stateless
public class RoseRuntimeEJB extends AbstractDataServiceEJB{
    /**
     * 
     * @param aGardenArchivedDocumentProfile
     * @param savedFile
     * @param fromEmail
     * @param subject - this is the attachment file name
     * @param isProduction
     * @param isEmailDisabled 
     */
    @Asynchronous
    public void emailBackupArchivedDocumentAsynchronously(RoseArchivedDocumentProfile aGardenArchivedDocumentProfile, 
                                                          File savedFile, String fromEmail, String subject, 
                                                          boolean isProduction, boolean isEmailDisabled) 
    {
        if (isEmailDisabled){
            return;
        }
        if (aGardenArchivedDocumentProfile == null){
            Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, new Exception("TECH ERR: The uploaded file profile cannot be NULL"));
        }
        G01ArchivedDocument aGardenArchivedDocument = aGardenArchivedDocumentProfile.getArchivedDocumentEntity();
        if ((aGardenArchivedDocument != null) && (savedFile != null) && (savedFile.isFile())){
            String timeStamp = ZcaCalendar.convertToMMddyyyyHHmmss(new Date(), "-", " @ ", ":");
            if (!isProduction){
                subject = "DEV: " + subject;
            }
            subject = subject + " [" + timeStamp +"]";

            String content = "Uploaded File: " + FilenameUtils.getName(savedFile.getAbsolutePath());
            content = content + ZcaNio.lineSeparator() + ZcaNio.lineSeparator();
            content = content + "Archive Target: " + aGardenArchivedDocument.getEntityType();
            content = content + ZcaNio.lineSeparator() + ZcaNio.lineSeparator();
            content = content + "Archive Date/Time: " + timeStamp;
            content = content + ZcaNio.lineSeparator() + ZcaNio.lineSeparator();
            content = content + "This file may fulfill the following requirement(s): ";
            content = content + ZcaNio.lineSeparator() + ZcaNio.lineSeparator();
            content = content + "Memo: " + aGardenArchivedDocument.getMemo();
            content = content + ZcaNio.lineSeparator() + ZcaNio.lineSeparator();

            List<String> replyToList = new ArrayList<>();
            replyToList.add(HostMonsterEmailSettings.DeveloperEmail.value());
            sendEncryptedEmailWithAttachment(fromEmail, HostMonsterEmailSettings.ArchiveEmail.value(), replyToList, subject, content, savedFile, false, isEmailDisabled);
        }else{
            Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, new Exception("Cannot send uploaded files by email"));
        }
    }
    
    @Asynchronous
    public void sendEncryptedEmailWithAttachment(String fromEmail, String toEmail, List<String> replyToList, String subject, String content, File savedFile, boolean deleteFile, boolean emailDisabled) {
        if (emailDisabled){
            return;
        }
        if (ZcaValidator.isNullEmpty(toEmail)){
            return;
        }
        try {
            RoseEmailSender.sendEncryptedEmailWithAttachment(fromEmail,
                                                            toEmail,
                                                            replyToList,
                                                            subject,
                                                            content,
                                                            savedFile);
            if (deleteFile){
                if (ZcaNio.isValidFile(savedFile)){
                    ZcaNio.deleteFile(savedFile);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    };

    @Asynchronous
    public void fetchEmailAttachment(String archivedFilePath, String emailSubject) {
        RoseEmailSearcher.searchEmailForAttachmentDownload(HostMonsterEmailSettings.ArchiveEmail.value(), 
                                                           HostMonsterEmailSettings.Hint.value(), 
                                                           archivedFilePath, 
                                                           new RoseEmailSubjectMatcher(emailSubject));
    }

    @Asynchronous
    public void sendEncryptedEmailInBatchWithAttachment(String emailFrom, List<String> emailToList, List<String> replyToList,
            String emailSubject, String emailContent, File savedFile, boolean deleteFile, boolean emailDisabled) 
    {
        if ((emailDisabled) || (emailToList == null)){
            return;
        }
        //send email and attachment without file deletion
        for (String emailTo : emailToList){
            sendEncryptedEmailWithAttachment(emailFrom, emailTo, replyToList, emailSubject, emailContent, savedFile, false, emailDisabled);
        }
        //finally delete the file
        if (deleteFile){
            if (ZcaNio.isValidFile(savedFile)){
                try {
                    ZcaNio.deleteFile(savedFile);
                } catch (IOException ex) {
                    Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Asynchronous
    public void sendEncryptedEmailInBatch(String emailFrom, List<String> emailToList, List<String> replyToList, String emailSubject, String emailContent, boolean emailDisabled) {
        if ((emailDisabled) || (emailToList == null)){
            return;
        }
        for (String emailTo : emailToList){
            sendEncryptedEmail(emailFrom, emailTo, replyToList, emailSubject, emailContent, emailDisabled);
        }
    }
    
    @Asynchronous
    public void sendEncryptedEmail(String emailFrom, String emailTo, List<String> replyToList, String emailSubject, String emailContent, boolean emailDisabled) {
        if (emailDisabled){
            return;
        }
        try {
            RoseEmailSender.sendEncryptedEmail(emailFrom, emailTo, replyToList, emailSubject, emailContent);
        } catch (Exception ex) {
            Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Asynchronous
    public void sendEncryptedEmailToRecipients(String emailFrom, List<String> emailTos, List<String> replyToList, String emailSubject, String emailContent, boolean emailDisabled) {
        if (emailDisabled){
            return;
        }
        try {
            RoseEmailSender.sendEncryptedEmailToRecipients(emailFrom, emailTos, replyToList, emailSubject, emailContent);
        } catch (Exception ex) {
            Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Asynchronous
    public void shutdownEmailStorageAsynchronously(RoseEmailManager roseEmailManager) {
        if (roseEmailManager == null){
            return;
        }
        roseEmailManager.shutdownEmailStorageByManager(false);
    }

    @Asynchronous
    public void loadEmailStorageAsynchronously(RoseEmailManager roseEmailManager) {
        if (roseEmailManager == null){
            return;
        }
        roseEmailManager.loadEmailStorageByManager();
    }
    
    @Asynchronous
    public void loadSentEmailsAsynchronously(RoseEmailManager roseEmailManager) {
        if (roseEmailManager == null){
            return;
        }
        roseEmailManager.loadSentEmailsByManager();
    }

    @Asynchronous
    public void refreshEmailStorageAsynchronously(RoseEmailManager roseEmailManager) {
        if (roseEmailManager == null){
            return;
        }
        roseEmailManager.refreshEmailStorageByManager();
    }

    @Asynchronous
    public void refreshSentEmailsAsynchronously(RoseEmailManager roseEmailManager) {
        if (roseEmailManager == null){
            return;
        }
        roseEmailManager.refreshSentEmailsByManager();
    }
    
    @Asynchronous
    public void parseTargetEmailContentWithAttachmentsAsynchronously(RoseEmailManager roseEmailManager){
        if (roseEmailManager == null){
            return;
        }
        roseEmailManager.parseTargetEmailContentWithAttachments();
    }
    
    /**
     * Send rarely-happened and critical exception to the lead developer by email and SMS. It also use Logger to track it
     * @param ex 
     * @param isEmailDisabled 
     * @param isSmsDisabled 
     */
    @Asynchronous
    public void sendCriticalTechnicalException(Exception ex, boolean isEmailDisabled, boolean isSmsDisabled) {
        if (ex == null){
            return;
        }
        //logging...RoseBusinessEJB
        Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        
        if (!isEmailDisabled){
            //Email...
            sendCriticalTechnicalExceptionByEmailHelper(ex, isEmailDisabled);
        }
        
        if (!isSmsDisabled){
            //SMS...
            sendCriticalTechnicalExceptionBySmsHelper(ex, isSmsDisabled);
        }
    }

    /**
     * Send rarely-happened and critical exception to the lead developer by email. It also use Logger to track it
     * @param ex 
     * @param isSmsDisabled 
     */
    @Asynchronous
    public void sendCriticalTechnicalExceptionByEmail(Exception ex, boolean isSmsDisabled) {
        if (ex == null){
            return;
        }
        //logging...
        Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        
        if (isSmsDisabled){
            return;
        }
        sendCriticalTechnicalExceptionByEmailHelper(ex, isSmsDisabled);
    }
    
    private void sendCriticalTechnicalExceptionByEmailHelper(Exception ex, boolean isSmsDisabled) {
        if (isSmsDisabled){
            return;
        }
        String shortSmsText = "[Critical Exception] " + ex.getMessage();
        
        //email...
        try {
            List<String> replyToList = new ArrayList<>();
            replyToList.add(HostMonsterEmailSettings.DeveloperEmail.value());
            RoseEmailSender.sendEncryptedEmail(HostMonsterEmailSettings.WebmasterEmail.value(),
                                                HostMonsterEmailSettings.DeveloperEmail.value(),
                                                replyToList,
                                                shortSmsText, GardenDebug.getExceptionContent(ex));
        } catch (Exception ex1) {
            Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }
    

    /**
     * Send rarely-happened and critical exception to the lead developer by SMS. It also use Logger to track it
     * @param ex 
     * @param isSmsDisabled 
     */
    @Asynchronous
    public void sendCriticalTechnicalExceptionBySms(Exception ex, boolean isSmsDisabled) {
        if (ex == null){
            return;
        }
        //logging...
        Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        
        if (isSmsDisabled){
            return;
        }
        
        sendCriticalTechnicalExceptionBySmsHelper(ex, isSmsDisabled);
    }
    
    private void sendCriticalTechnicalExceptionBySmsHelper(Exception ex, boolean isSmsDisabled) {
        if (isSmsDisabled){
            return;
        }
        String shortSmsText = "[Critical Exception] " + ex.getMessage();
        //SMS...(Each SMS message can contain up to 140 bytes, and the character limit depends on the encoding scheme.)
        if (shortSmsText.length() > 135){
            shortSmsText = shortSmsText.substring(0, 135) + "...";
        }
        RoseSmsSender.sendSmsText(GardenAwsSettings.ROSE_DEVELOPER_MOBILE.value(), shortSmsText);
    }

    @Asynchronous
    public void sendSmsText(ArrayList<String> phoneNumberList, String message, boolean isSmsDisabled) {
        if ((isSmsDisabled) || (phoneNumberList == null) || (ZcaValidator.isNullEmpty(message))){
            return;
        }
        //SMS...(Each SMS message can contain up to 140 bytes, and the character limit depends on the encoding scheme.)
        if (message.length() > 135){
            message = message.substring(0, 135) + "...";
        }
        for (String phoneNumber : phoneNumberList){
            RoseSmsSender.sendSmsText(phoneNumber, message);
        }
    }

    @Asynchronous
    public void sendSmsText(String phoneNumber, String message, boolean isSmsDisabled) {
        if ((isSmsDisabled) || (ZcaValidator.isNullEmpty(phoneNumber)) || (ZcaValidator.isNullEmpty(message))){
            return;
        }
        //SMS...(Each SMS message can contain up to 140 bytes, and the character limit depends on the encoding scheme.)
        if (message.length() > 135){
            message = message.substring(0, 135) + "...";
        }
        RoseSmsSender.sendSmsText(phoneNumber, message);
    }

    /**
     * @param requestedTargetReplyMessageUuid
     * @return - if no G01ChatMessage exists, NULL returned
     */
    public RoseChatMessageProfile findGardenMessageProfileByUuid(String requestedTargetReplyMessageUuid) {
        RoseChatMessageProfile result = null;
        if (ZcaValidator.isNotNullEmpty(requestedTargetReplyMessageUuid)){
            EntityManager em = getEntityManager();
            try {
                G01ChatMessage aG01ChatMessage = GardenJpaUtils.findById(em, 
                        G01ChatMessage.class, requestedTargetReplyMessageUuid);
                if (aG01ChatMessage != null){
                    result = new RoseChatMessageProfile();
                    result.setChatMessageEntity(aG01ChatMessage);
                    result.setTalker(findRoseUserProfileByUserUuid(em, aG01ChatMessage.getTalkerUserUuid()));
                }
            } finally {
                em.close();
            }
        }
        return result;
    }

    public HashMap<String, RoseChatMessageProfile> findRoseChatMessageProfileMapByEnityType(GardenEntityType entityType) {
        if (entityType == null){
            return new HashMap<>();
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("entityType", entityType.name());
        String sqlQuery = "SELECT g FROM G01ChatMessage g WHERE g.entityType = :entityType";
        return findRoseChatMessageProfileMapHelper(sqlQuery, params);
    }

    public HashMap<String, RoseChatMessageProfile> findRoseChatMessageProfileMapByEnityTypeUuid(GardenEntityType entityType, String entityUuid) {
        if (ZcaValidator.isNullEmpty(entityUuid) || entityType == null){
            return new HashMap<>();
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("entityType", entityType.name());
        params.put("entityUuid", entityUuid);
        String sqlQuery = "SELECT g FROM G01ChatMessage g WHERE g.entityUuid = :entityUuid AND g.entityType = :entityType";
        return findRoseChatMessageProfileMapHelper(sqlQuery, params);
    }

    private HashMap<String, RoseChatMessageProfile> findRoseChatMessageProfileMapHelper(String sqlQuery, HashMap<String, Object> params) {
        HashMap<String, RoseChatMessageProfile> result = new HashMap<>();
        EntityManager em = getEntityManager();
        try {
            List<G01ChatMessage> aG01ChatMessageList = GardenJpaUtils.findEntityListByQuery(em, G01ChatMessage.class, sqlQuery, params);
            RoseChatMessageProfile aGardenMessageProfile;
            for (G01ChatMessage aG01ChatMessage : aG01ChatMessageList){
                aGardenMessageProfile = new RoseChatMessageProfile();
                aGardenMessageProfile.setChatMessageEntity(aG01ChatMessage);
                aGardenMessageProfile.setTalker(findRoseUserProfileByUserUuid(em, aG01ChatMessage.getTalkerUserUuid()));
                result.put(aGardenMessageProfile.getChatMessageEntity().getChatMessageUuid(), aGardenMessageProfile);
            }
        } finally {
            em.close();
        }
        return result;
    }
    
}
