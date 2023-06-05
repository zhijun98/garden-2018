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

package com.zcomapproach.garden.rose.bean;

import com.zcomapproach.garden.email.HostMonsterEmailSettings;
import com.zcomapproach.garden.email.GardenEmailUtils;
import com.zcomapproach.garden.email.rose.RoseEmailMessage;
import com.zcomapproach.garden.exception.NoAttachmentException;
import com.zcomapproach.garden.rose.RosePageName;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaText;
import com.zcomapproach.garden.util.GardenData;
import com.zcomapproach.commons.nio.ZcaNio;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author zhijun98
 */
@Named(value = "roseDocumentEmailBean")
@ViewScoped
public class RoseDocumentEmailBean extends RoseDocumentTransferBean{

    private String targetEmailFrom;
    private final String targetEmailTo = "zhijun98@gmail.com";
    private String targetSubject;
    private String targetContent;
    
    private String targetPageValue;

    protected void setEmailMessageForReply(RoseEmailMessage targetEmailMessage, String fromAddress) {
        targetEmailFrom = ZcaText.denullize(fromAddress);
        if (targetEmailMessage == null){
            //targetEmailTo = null;
            targetSubject = null;
            targetContent = null;
        }else{
            //targetEmailTo = targetEmailMessage.getFrom();
            targetSubject = "RE: " + targetEmailMessage.getSubject();
            targetContent = ZcaNio.lineSeparator() + ZcaNio.lineSeparator() 
                    + ZcaNio.lineSeparator() + ZcaNio.lineSeparator() 
                    + ZcaNio.lineSeparator() + ZcaNio.lineSeparator() 
                    + "---------------------------------------------------------" 
                    + ZcaNio.lineSeparator() 
                    + targetEmailMessage.getEmailContent();
        }
    }

    protected void setEmailMessageForForwardEmployee(RoseEmailMessage targetEmailMessage, String messageForEmployee, String fromAddress) {
        setEmailMessageForReply(targetEmailMessage, fromAddress);
        targetContent = ZcaNio.lineSeparator() + ZcaNio.lineSeparator() 
                + messageForEmployee
                + ZcaNio.lineSeparator() + ZcaNio.lineSeparator() 
                + "---------------------------------------------------------" 
                + ZcaNio.lineSeparator() 
                + targetEmailMessage.getEmailContent();
    }

    @Override
    public String getTopicIconAwesomeName() {
        return "email";
    }
    
    protected String getOriginalTargetEmailFrom(){
        return targetEmailFrom;
    }

    public String getTargetEmailFrom() {
        if (targetEmailFrom == null){
            targetEmailFrom = getRoseUserSession().getTargetAccountProfile().retrieveEmail();
        }
        return targetEmailFrom;
    }

    public void setTargetEmailFrom(String targetEmailFrom) {
        this.targetEmailFrom = targetEmailFrom;
    }

    public String getTargetSubject() {
        return targetSubject;
    }

    public void setTargetSubject(String targetSubject) {
        this.targetSubject = targetSubject;
    }

    public String getTargetContent() {
        return targetContent;
    }

    public void setTargetContent(String targetContent) {
        this.targetContent = targetContent;
    }

    public String getTargetEmailTo() {
        return targetEmailTo;
    }

    public void setTargetEmailTo(String targetEmailTo) {
        //this.targetEmailTo = targetEmailTo;
    }
    
    protected boolean sendTargetEmailHelper(){
        List<String> replyToList = new ArrayList<>();
        String replyToListString = "";
        replyToList.add(HostMonsterEmailSettings.ServiceEmail.value());
        replyToListString += HostMonsterEmailSettings.ServiceEmail.value();
        if (!HostMonsterEmailSettings.ServiceEmail.value().equalsIgnoreCase(getTargetEmailFrom())){
            replyToList.add(getTargetEmailFrom());
            replyToListString += ";"+getTargetEmailFrom();
        }
        String sentAttachmentFileName = null;
        try {
            UploadedFile uploadedFile = getUploadedFile();
            if ((uploadedFile != null) && validateUploadedFile(uploadedFile)){
                String fileLocation = getRoseSettings().getArchivedFileLocation();
                try{
                    sentAttachmentFileName = uploadedFile.getFileName();
                    //save the file onto the disk
                    File savedFile = saveUploadedFileOnServerSide(GardenData.generateUUIDString(), 
                            FilenameUtils.getExtension(sentAttachmentFileName), fileLocation);
                    List<String> emailTos = new ArrayList<>();
                    if (targetEmailTo.contains(";")){
                        emailTos.addAll(Arrays.asList(targetEmailTo.split(";")));
                    }else{
                        emailTos.add(targetEmailTo);
                    }
                    getRuntimeEJB().sendEncryptedEmailInBatchWithAttachment(targetEmailFrom, emailTos, replyToList, targetSubject, 
                            targetContent, savedFile, true, getRoseSettings().isEmailDisabled());
                }catch (Exception ex){
                    if (ex instanceof NoAttachmentException){
                        getRuntimeEJB().sendEncryptedEmail(targetEmailFrom, targetEmailTo, replyToList, targetSubject, targetContent, getRoseSettings().isEmailDisabled());
                    }else{
                        throw ex;
                    }
                }
            }else{
                getRuntimeEJB().sendEncryptedEmail(targetEmailFrom, targetEmailTo, replyToList, targetSubject, targetContent, getRoseSettings().isEmailDisabled());
            }
        } catch (Exception ex) {
            Logger.getLogger(RoseDocumentEmailBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalErrorFacesMessage(ex.getMessage());
            this.setBootstrapAlertMessage(ex.getMessage(), Level.SEVERE);
            return false;
        }
        /**
         * Serialize this sent-message
         */
        RoseEmailMessage aRoseEmailMessage = new RoseEmailMessage();
        //aRoseEmailMessage.setBccList(bccList);
        //aRoseEmailMessage.setCcList(ccList);
        aRoseEmailMessage.setContentLoaded(true);
        aRoseEmailMessage.setFrom(targetEmailFrom);
        aRoseEmailMessage.setFromPerson(targetEmailFrom);
        aRoseEmailMessage.setHtmlContent(targetContent);
        aRoseEmailMessage.setImap4Message(true);
        //aRoseEmailMessage.setMailFolderName(GardenEmailFolderName.GARDEN_SENT.value());
        //aRoseEmailMessage.setMessageNumber(0);
        //aRoseEmailMessage.setMsgUid(0);
        //aRoseEmailMessage.setPlainContent(targetContent);
        aRoseEmailMessage.setReceivedDate(null);
        aRoseEmailMessage.setReplyTo(replyToListString);
        aRoseEmailMessage.setSentDate(new Date());
        aRoseEmailMessage.setSubject(targetSubject);
        try {
            aRoseEmailMessage.setToList(GardenEmailUtils.convertRecipientTextToEmailAddressList(targetEmailTo));
        } catch (Exception ex) {
            Logger.getLogger(RoseDocumentEmailBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        aRoseEmailMessage.setSentAttachmentFileName(sentAttachmentFileName);
        aRoseEmailMessage.setUnread(false);
        getRoseEmailController().serializeSentEmailMessage(aRoseEmailMessage);
        /**
         * Clear target fields
         */
        //targetEmailTo = "";
        targetSubject = "";
        targetContent = "";
        this.setBootstrapAlertMessage(RoseText.getText("OperationSucceeded_T") + " @" + ZcaCalendar.getCurrentTimeStemp(), null);
        
        return true;
    }
    
    public String sendTargetEmail(){
        if (sendTargetEmailHelper()){
            RosePageName page = RosePageName.convertStringToType(targetPageValue);
            if (page != null){
                return page.name();
            }
        }
        return null;
    }

//    protected String sendEmailInBatch(List<String> toEmailList) {
//        if (toEmailList == null){
//            RoseJsfUtils.setGlobalErrorFacesMessage("No target email list was provide. Emailing is disabled.");
//        }else{
//            getRuntimeEJB().sendEncryptedEmailInBatch(targetEmailFrom, toEmailList, targetSubject, targetContent, getRoseSettings().isEmailDisabled());
//
//            RosePageName page = RosePageName.convertStringToType(targetPageValue);
//            if (page != null){
//                return page.name();
//            }
//        }
//        return null;
//    }

}
