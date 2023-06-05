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

import com.zcomapproach.garden.aws.RoseSmsSender;
import com.zcomapproach.garden.email.rose.RoseEmailSearcher;
import com.zcomapproach.garden.email.rose.RoseEmailSender;
import com.zcomapproach.garden.email.HostMonsterEmailSettings;
import com.zcomapproach.garden.email.rose.RoseEmailSubjectMatcher;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.persistence.GardenJpaUtils;
import com.zcomapproach.garden.persistence.entity.G02Log;
import com.zcomapproach.garden.persistence.entity.G02SystemSettings;
import com.zcomapproach.garden.persistence.peony.PeonyLog;
import com.zcomapproach.garden.persistence.peony.PeonyLogList;
import com.zcomapproach.garden.persistence.peony.data.PeonySystemSettings;
import com.zcomapproach.garden.persistence.updater.G02DataUpdaterFactory;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenDebug;
import com.zcomapproach.commons.nio.ZcaNio;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import com.zcomapproach.garden.rose.bean.state.RoseEmailManager;
import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 *
 * @author zhijun98
 */
@Stateless
public class RoseRuntimeEJB02 extends AbstractDataServiceEJB02{
    
    @Asynchronous
    public void sendEncryptedEmailWithAttachment(String fromEmail, String toEmail, List<String> replyToList, String subject, String content, File savedFile, boolean deleteFile, boolean emailDisabled) {
        if (emailDisabled){
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
            Logger.getLogger(RoseRuntimeEJB02.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(RoseRuntimeEJB02.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(RoseRuntimeEJB02.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(RoseRuntimeEJB02.class.getName()).log(Level.SEVERE, null, ex);
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
    public void refreshEmailStorageAsynchronously(RoseEmailManager roseEmailManager) {
        if (roseEmailManager == null){
            return;
        }
        roseEmailManager.refreshEmailStorageByManager();
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
        
////        if (!isSmsDisabled){
////            //SMS...
////            sendCriticalTechnicalExceptionBySmsHelper(ex, isSmsDisabled);
////        }
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
        
////        sendCriticalTechnicalExceptionBySmsHelper(ex, isSmsDisabled);
    }
    
////    private void sendCriticalTechnicalExceptionBySmsHelper(Exception ex, boolean isSmsDisabled) {
////        if (isSmsDisabled){
////            return;
////        }
////        String shortSmsText = "[Critical Exception] " + ex.getMessage();
////        //SMS...(Each SMS message can contain up to 140 bytes, and the character limit depends on the encoding scheme.)
////        if (shortSmsText.length() > 135){
////            shortSmsText = shortSmsText.substring(0, 135) + "...";
////        }
////        RoseSmsSender.sendSmsText(GardenAwsSettings.ROSE_DEVELOPER_MOBILE.value(), shortSmsText);
////    }

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
        if (message.length() > 140){
            message = message.substring(0, 135) + "...";
        }
        RoseSmsSender.sendSmsText(phoneNumber, message);
    }
    
    public PeonyLogList storePeonyLogList(PeonyLogList aPeonyLogList) throws Exception{
        if (aPeonyLogList == null){
            return aPeonyLogList;
        }
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            List<PeonyLog> logs = aPeonyLogList.getLogs();
            if (logs != null){
                for (PeonyLog log : logs){
                    GardenJpaUtils.storeEntity(em, G02Log.class, log.getLog(), log.getLog().getLogUuid(), G02DataUpdaterFactory.getSingleton().getG02LogUpdater());
                }
            }

            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        
        return aPeonyLogList;
    }
    
    public G02Log storeLogEntity(G02Log log) throws Exception{
        if (log == null){
            return null;
        }
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            GardenJpaUtils.storeEntity(em, G02Log.class, log, log.getLogUuid(), G02DataUpdaterFactory.getSingleton().getG02LogUpdater());

            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        
        return log;
    }

    
    /**
     * 
     * @param aPeonySystemSettings
     * @return
     * @throws Exception 
     */
    public PeonySystemSettings storePeonySystemSettings(PeonySystemSettings aPeonySystemSettings) throws Exception{
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();

            GardenJpaUtils.storePeonyEmployee(em, aPeonySystemSettings.getLoginEmployee());
            
            List<G02SystemSettings> aG02SystemSettingsList = aPeonySystemSettings.getSystemSettingsList();
            for (G02SystemSettings aG02SystemSettings : aG02SystemSettingsList){
                GardenJpaUtils.storeEntity(em, G02SystemSettings.class, 
                                            aG02SystemSettings, 
                                            aG02SystemSettings.getG02SystemSettingsPK(), 
                                            G02DataUpdaterFactory.getSingleton().getG02SystemSettingsUpdater());
            }

            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        
        return aPeonySystemSettings;
    }
    
    public PeonySystemSettings findPeonySystemSettings(String accountName, String password) {
        PeonySystemSettings result = null;
        
        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.findPeonySystemSettings(em, accountName, password);
        } catch (Exception ex) {
            Logger.getLogger(RoseRuntimeEJB02.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            em.close();
        }
        return result;
    }
    
}
