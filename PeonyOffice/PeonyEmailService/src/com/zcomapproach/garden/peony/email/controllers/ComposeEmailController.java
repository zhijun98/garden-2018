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

package com.zcomapproach.garden.peony.email.controllers;

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.email.GardenEmailMessage;
import com.zcomapproach.garden.email.GardenEmailUtils;
import com.zcomapproach.garden.email.data.GardenEmailFolderName;
import com.zcomapproach.garden.email.data.GardenEmailMessageTag;
import com.zcomapproach.garden.peony.email.events.SendGardenEmailMessageRequest;
import com.zcomapproach.garden.peony.view.events.CloseTopComponentRequest;
import com.zcomapproach.garden.email.data.OfflineMessageStatus;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G02EmailTag;
import com.zcomapproach.garden.persistence.entity.G02OfflineEmail;
import com.zcomapproach.garden.persistence.peony.PeonyEmailTag;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmail;
import com.zcomapproach.garden.util.GardenData;
import com.zcomapproach.commons.nio.ZcaNio;
import com.zcomapproach.garden.peony.PeonyLauncher;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author zhijun98
 */
public class ComposeEmailController extends PeonyEmailServiceController {
    
    @FXML
    private TextField emailToListTextField;
    
    @FXML
    private TextField emailCcListTextField;
    
    @FXML
    private TextField emailBccListTextField;
    
    @FXML
    private TextField emailSubjectTextField;
    
    @FXML
    private HTMLEditor emailContentEditor;
    
    @FXML
    private Button attachButton;
    
    @FXML
    private Label attachLabel;
    
    @FXML
    private Button sendButton;
    
    @FXML
    private Button resetButton;
    
    private final JFileChooser attachmentFileChooser = new JFileChooser();
    /**
     * Current message instance displayed in the composeEmailTab. This instance is used to holde the message information 
     * for sending out to the outside
     */
    private final GardenEmailMessage targetGardenEmailMessage;
    
    private final OfflineMessageStatus purpose;
    
    private final PeonyOfflineEmail relatedPeonyOfflineEmail;

    public ComposeEmailController(GardenEmailMessage targetGardenEmailMessage, PeonyOfflineEmail relatedPeonyOfflineEmail, OfflineMessageStatus purpose) {
        this.targetGardenEmailMessage = targetGardenEmailMessage;
        this.relatedPeonyOfflineEmail = relatedPeonyOfflineEmail;
        this.purpose = purpose;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        initializeComposeEmailTab();
        
        initializeFunctionalButtons();
        
        initializeTargetMessageForPurpose();
    }

    private void initializeFunctionalButtons(){
        sendButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to send out this email?") != JOptionPane.YES_OPTION){
                    return;
                }
                try {
                    broadcastPeonyFaceEventHappened(createSendGardenEmailMessageRequest());
                    broadcastPeonyFaceEventHappened(new CloseTopComponentRequest());
                } catch (Exception ex) {
                    PeonyFaceUtils.displayErrorMessageDialog(ex.getMessage());
                }
            }
        });
        attachButton.setOnAction((ActionEvent event) -> {
            if (SwingUtilities.isEventDispatchThread()){
                selectAttachementFilesHelper();
            }else{
                SwingUtilities.invokeLater(new Runnable(){
                    @Override
                    public void run() {
                        selectAttachementFilesHelper();
                    }
                });
            }
        });
        
        resetButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to clear all the entries of this email?") != JOptionPane.YES_OPTION){
                    return;
                }
                initializeComposeEmailTab();
                targetGardenEmailMessage.reset();
            }
        });
    }

    private SendGardenEmailMessageRequest createSendGardenEmailMessageRequest() throws Exception {
        targetGardenEmailMessage.setReplyToList(GardenEmailUtils.convertRecipientTextToEmailAddressList(PeonyProperties.getSingleton().getOfflineEmailAddress()));
        targetGardenEmailMessage.setToList(GardenEmailUtils.convertRecipientTextToEmailAddressList(emailToListTextField.getText()));
        if (ZcaValidator.isNotNullEmpty(emailCcListTextField.getText())){
            targetGardenEmailMessage.setCcList(GardenEmailUtils.convertRecipientTextToEmailAddressList(emailCcListTextField.getText()));
        }
        if (ZcaValidator.isNotNullEmpty(emailBccListTextField.getText())){
            targetGardenEmailMessage.setBccList(GardenEmailUtils.convertRecipientTextToEmailAddressList(emailBccListTextField.getText()));
        }
        targetGardenEmailMessage.setSubject(emailSubjectTextField.getText());
        targetGardenEmailMessage.setHtmlContent(emailContentEditor.getHtmlText());
        targetGardenEmailMessage.setHtmlContentEmbedded(true);
        targetGardenEmailMessage.setPlainContentEmbedded(false);
        targetGardenEmailMessage.setFolderFullName(GardenEmailFolderName.GARDEN_SENT.value());
        
        targetGardenEmailMessage.setEmailMsgUid(GardenData.generateUUIDString());   //a new message UUID
        
        String employeeUuid = PeonyProperties.getSingleton().getCurrentLoginUserUuid();
        //Create its associated PeonyOfflineEmail instance
        PeonyOfflineEmail aPeonyOfflineEmail = new PeonyOfflineEmail();
        //G02OfflineEmail
        G02OfflineEmail aG02OfflineEmail = new G02OfflineEmail();
        aG02OfflineEmail.setOfflineEmailUuid(GardenData.generateUUIDString());
        aG02OfflineEmail.setMailboxAddress(PeonyProperties.getSingleton().getOfflineEmailAddress());
        aG02OfflineEmail.setMsgId(targetGardenEmailMessage.getEmailMsgUid());   //SENT-email's UUID
        aG02OfflineEmail.setOwnerUserUuid(employeeUuid);
        aG02OfflineEmail.setMessageFaceFolder(GardenEmailFolderName.GARDEN_SENT.value());
        aG02OfflineEmail.setMessagePath(targetGardenEmailMessage.generateEmailMessageSerializedFileFullPath(employeeUuid, 
                PeonyProperties.getSingleton().getOfflineEmailAddress(), PeonyProperties.getSingleton().getEmailSerializationFolder()).toAbsolutePath().toString());
        aG02OfflineEmail.setMsgFolder(GardenEmailFolderName.GARDEN_SENT.value());
        aPeonyOfflineEmail.setOfflineEmail(aG02OfflineEmail);
        
        PeonyOfflineEmail.initializeOfflineEmailAttachmentListForPeonyOfflineEmail(aPeonyOfflineEmail, 
                targetGardenEmailMessage, Paths.get(PeonyProperties.getSingleton().getEmailSerializationFolder()), false);
        
        //G02EmailTag to record this sent-email's relationship (e.g. reply) with other email which is represented by relatedPeonyOfflineEmail
        if (purpose != null){
            aG02OfflineEmail.setMessageStatus(purpose.value());
            if ((relatedPeonyOfflineEmail != null) && (ZcaValidator.isNotNullEmpty(relatedPeonyOfflineEmail.getOfflineEmail().getOfflineEmailUuid()))){
                List<PeonyEmailTag> peonyEmailTagList = aPeonyOfflineEmail.getPeonyEmailTagList();
                PeonyEmailTag aPeonyEmailTag = new PeonyEmailTag();
                G02EmailTag emailTag = new G02EmailTag();
                emailTag.setTagUuid(GardenData.generateUUIDString());
                emailTag.setCreated(new Date());
                emailTag.setEntityType(GardenEntityType.OFFLINE_EMAIL_MESSAGE.name());
                emailTag.setEntityUuid(relatedPeonyOfflineEmail.getOfflineEmail().getOfflineEmailUuid());
                emailTag.setTagMemo("Purpose - " + purpose.value() + ": " + GardenEmailMessageTag.SENT_OUT_EMAIL.value());
                emailTag.setTagName(GardenEmailMessageTag.SENT_OUT_EMAIL.name());
                emailTag.setOfflineEmailUuid(aG02OfflineEmail.getOfflineEmailUuid());
                aPeonyEmailTag.setEmailTag(emailTag);
                peonyEmailTagList.add(aPeonyEmailTag);
            }
        }
        
        return new SendGardenEmailMessageRequest(targetGardenEmailMessage, aPeonyOfflineEmail);
    }
    
    private void initializeComposeEmailTab(){
        emailToListTextField.textProperty().setValue("");
        emailCcListTextField.textProperty().setValue("");
        emailBccListTextField.textProperty().setValue("");
        emailSubjectTextField.textProperty().setValue("");
        emailContentEditor.setHtmlText("");
        attachLabel.textProperty().setValue("");
        sendButton.disableProperty().setValue(false);
        resetButton.disableProperty().setValue(false);
    }

    private void initializeTargetMessageForPurpose() {
        if (purpose == null){
            return;
        }
        switch (purpose){
            case REPLY:
                prepareForReply(targetGardenEmailMessage);
                break;
            case REPLY_ALL:
                prepareForReplyAll(targetGardenEmailMessage);
                break;
            case FORWARD:
                prepareForForward(targetGardenEmailMessage);
                break;
        }
    }
    
    /**
     * Reply to the primary recipients based on the information of peonyEmailMessage
     * @param peonyEmailMessage 
     */
    private void prepareForReply(GardenEmailMessage peonyEmailMessage) {
        
        prepareCommonsForForwardOrReplyStyledOperations(peonyEmailMessage);
        
        emailToListTextField.textProperty().setValue(GardenEmailUtils.convertAddressListToText(peonyEmailMessage.getReplyToList()));
        emailCcListTextField.textProperty().setValue("");
    }

    /**
     * Reply to all recipients based on the information of peonyEmailMessage
     * @param peonyEmailMessage 
     */
    private void prepareForReplyAll(GardenEmailMessage peonyEmailMessage) {
        
        prepareCommonsForForwardOrReplyStyledOperations(peonyEmailMessage);
        
        String toList = GardenEmailUtils.convertAddressListToText(peonyEmailMessage.getReplyToList());
        if (ZcaValidator.isNullEmpty(toList)){
            toList = GardenEmailUtils.convertAddressListToText(peonyEmailMessage.getToList());
        }else{
            toList += "; " + GardenEmailUtils.convertAddressListToText(peonyEmailMessage.getToList());
        }
        emailToListTextField.textProperty().setValue(toList);
        emailCcListTextField.textProperty().setValue(GardenEmailUtils.convertAddressListToText(peonyEmailMessage.getCcList()));
    }
    
    private void prepareForForward(GardenEmailMessage peonyEmailMessage){
        
        prepareCommonsForForwardOrReplyStyledOperations(peonyEmailMessage);
        
        emailToListTextField.textProperty().setValue("");
        emailCcListTextField.textProperty().setValue("");
        
        if ((peonyEmailMessage.getAttachmentFileMapData() != null) && (!peonyEmailMessage.getAttachmentFileMapData().isEmpty())){
            attachLabel.textProperty().setValue(peonyEmailMessage.retrieveAttachmentFileNames());
            peonyEmailMessage.setAttachmentFileMapData(peonyEmailMessage.getAttachmentFileMapData());
        }
    }
    
    private void prepareCommonsForForwardOrReplyStyledOperations(GardenEmailMessage peonyEmailMessage){
        emailBccListTextField.textProperty().setValue("");  //BCCs were always skipped
        emailSubjectTextField.textProperty().setValue(peonyEmailMessage.getSubject());
        String content = "&nbsp;<p/>&nbsp;<p/>&nbsp;<p/><p/><hr/><b>Original Email:</b>"
                + peonyEmailMessage.retrieveEmailHeadline()+"<hr/>"  
                + peonyEmailMessage.tryToGetHtmlEmailConent();
        String attachmentFileNames = peonyEmailMessage.retrieveAttachmentFileNames();
        if (ZcaValidator.isNotNullEmpty(attachmentFileNames)){
            content += "<hr/>Attachments:" + attachmentFileNames;
        }
        emailContentEditor.setHtmlText(content);
        attachLabel.textProperty().setValue("");
        sendButton.disableProperty().setValue(false);
        resetButton.disableProperty().setValue(false);
    }
    
    private void selectAttachementFilesHelper(){
        String attachmentFullPath = PeonyFaceUtils.getFileFullPathByFileChooser(attachmentFileChooser, PeonyLauncher.mainFrame, null);
        if (ZcaNio.isValidFile(attachmentFullPath)){
            final Path file = Paths.get(attachmentFullPath);
            final String fileName = file.getFileName().toString();
            
            targetGardenEmailMessage.getAttachmentFileMapData().put(fileName, attachmentFullPath);
            
            if (Platform.isFxApplicationThread()){
                updateAttachmentLabel(fileName);
            }else{
                Platform.runLater(() -> {
                    updateAttachmentLabel(fileName);
                });
            }
        }
    }
    
    private void updateAttachmentLabel(String fileName){
        String existingFileNames = attachLabel.getText();
        if (ZcaValidator.isNullEmpty(existingFileNames)){
            attachLabel.textProperty().setValue(fileName);
        }else{
            attachLabel.textProperty().setValue(existingFileNames + "; " + fileName);
        }
    }
}
