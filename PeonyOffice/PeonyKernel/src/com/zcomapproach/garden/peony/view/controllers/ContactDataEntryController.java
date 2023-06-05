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

package com.zcomapproach.garden.peony.view.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.view.events.ContactInfoEntityDeleted;
import com.zcomapproach.garden.peony.view.events.ContactInfoEntitySaved;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.persistence.G02EntityValidator;
import com.zcomapproach.garden.persistence.constant.GardenContactType;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G02ContactInfo;
import com.zcomapproach.garden.persistence.updater.G02DataUpdaterFactory;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.garden.util.GardenData;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javax.swing.JOptionPane;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class ContactDataEntryController extends PeonyFaceController{
    @FXML
    private Label titleLabel;
    @FXML
    private JFXComboBox<String> contactTypeComboBox;
    @FXML
    private JFXTextField contactInformationField;
    @FXML
    private JFXTextField memoField;
    /**
     * Functional HBox: save, cancel and delete buttons
     */
    @FXML
    private HBox functionalHBox;
    
    private JFXButton saveButton;   //used by this controller to save itself locally 
    private JFXButton cancelButton; //used for dialog
    private JFXButton deleteButton; //used by this controller to delete itself locally
    private final boolean saveBtnRequired;
    private final boolean deleteBtnRequired;
    private final boolean cancelBtnRequired;
    
    private final G02ContactInfo targetContactInfo;
    private final GardenEntityType entityType;
    private final String entityUuid;

    public ContactDataEntryController(G02ContactInfo targetContactInfo, 
                                    GardenEntityType entityType, 
                                    String entityUuid, 
                                    boolean saveBtnRequired, 
                                    boolean deleteBtnRequired, 
                                    boolean cancelBtnRequired) 
    {
        this.targetContactInfo = targetContactInfo;
        this.entityType = entityType;
        this.entityUuid = entityUuid;
        this.saveBtnRequired = saveBtnRequired;
        this.deleteBtnRequired = deleteBtnRequired;
        this.cancelBtnRequired = cancelBtnRequired;
    }
    
    private boolean isSaveBtnRequired() {
        return saveBtnRequired;
    }

    private boolean isDeleteBtnRequired() {
        return deleteBtnRequired;
    }

    private boolean isCancelBtnRequired() {
        return cancelBtnRequired;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titleLabel.setText("Contact Information");
        initializeContactDataFields();
        initializeSaveButton();
        initializeCancelButton();
        initializeDeleteButton();
    }

    void refreshDataEntry(final G02ContactInfo aG02ContactInfo) {
        if (Platform.isFxApplicationThread()){
            refreshDataEntryHelper(aG02ContactInfo);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    refreshDataEntryHelper(aG02ContactInfo);
                }
            });
        }
    }
    
    private void refreshDataEntryHelper(final G02ContactInfo aG02ContactInfo) {
        G02DataUpdaterFactory.getSingleton().getG02ContactInfoUpdater().cloneEntity(aG02ContactInfo, targetContactInfo);
        initializeContactDataFields();
    }
    
    private void initializeDeleteButton(){
        if (deleteButton == null){
            deleteButton = new JFXButton("Delete");
            deleteButton.getStyleClass().add("peony-primary-small-button");
            deleteButton.setOnAction((ActionEvent actionEvent) -> {
                if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to delete this contact information?", "Confirm") == JOptionPane.YES_OPTION){
                    deleteTargetLocation();
                }
            });
        }
        //display it or not...
        if (isDeleteBtnRequired()){
            functionalHBox.getChildren().add(deleteButton);
        }else{
            functionalHBox.getChildren().remove(deleteButton);
        }
    }

    private void deleteTargetLocation() {
        Task<Boolean> deleteContactInfoTask = new Task<Boolean>(){
            @Override
            protected Boolean call() throws Exception {
                try{
                    return (Lookup.getDefault().lookup(PeonyManagementService.class).getPeonyManagementRestClient()
                        .deleteEntity_XML(G02ContactInfo.class, 
                                        GardenRestParams.Management.deleteContactInfoRestParams(targetContactInfo.getContactInfoUuid())) != null);
                }catch (Exception ex){
                    updateMessage("Deletion failed. " + ex.getMessage());
                    return false;
                }
            }
            @Override
            protected void succeeded() {
                try {
                    boolean result = get();
                    if (result){
                        resetDataEntry();
                        resetDataEntryStyle();
                        setDataEntryChanged(false);
                        PeonyFaceUtils.displayInformationMessageDialog("Successfully deleted the contact information.");
                        broadcastPeonyFaceEventHappened(new ContactInfoEntityDeleted(targetContactInfo));
                        broadcastPeonyFaceEventHappened(new CloseDialogRequest(true));
                    }else{
                        PeonyFaceUtils.displayErrorMessageDialog("The server side cannot delete this record for some technical reason.");
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Technical exception raised. " + ex.getMessage());
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(deleteContactInfoTask);
    }
    
    private void initializeSaveButton(){
        if (saveButton == null){
            saveButton = new JFXButton("Save");
            saveButton.getStyleClass().add("peony-primary-small-button");
            saveButton.setOnAction((ActionEvent actionEvent) -> {
                saveTargetContactInfo();
            });
        }
        //display it or not...
        if (isSaveBtnRequired()){
            functionalHBox.getChildren().add(saveButton);
        }else{
            functionalHBox.getChildren().remove(saveButton);
        }
    }
    
    private void initializeCancelButton(){
        if (cancelButton == null){
            cancelButton = new JFXButton("Cancel");
            cancelButton.getStyleClass().add("peony-primary-small-button");
            cancelButton.setOnAction((ActionEvent actionEvent) -> {
                if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to close this contact dialog?") == JOptionPane.YES_OPTION){
                    broadcastPeonyFaceEventHappened(new CloseDialogRequest(true));
                }
            });
        }
        //display it or not...
        if (isCancelBtnRequired()){
            functionalHBox.getChildren().add(cancelButton);
        }else{
            functionalHBox.getChildren().remove(cancelButton);
        }
    }
    
    /**
     * Save the target location on the server side and then broadcast this event
     */
    private void saveTargetContactInfo() {
        Task<Boolean> saveTargetContactInfoTask = new Task<Boolean>(){
            @Override
            protected Boolean call() throws Exception {
                try {
                    validateTargetContactInfo();
                    return (Lookup.getDefault().lookup(PeonyManagementService.class)
                            .getPeonyManagementRestClient().storeEntity_XML(G02ContactInfo.class, 
                                    GardenRestParams.Management.storeContactInfoRestParams(), targetContactInfo) 
                            != null);
                } catch (Exception ex) {
                    if (ex instanceof ZcaEntityValidationException){
                        highlightBadEntityField((ZcaEntityValidationException)ex);
                    }
                    updateMessage("This operation failed. " + ex.getMessage());
                    return false;
                }
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayErrorMessageDialog("Failed to save it because of the technical issue. " + getMessage());
            }

            @Override
            protected void succeeded() {
                try {
                    boolean result = get();
                    if (result){
                        PeonyFaceUtils.displayInformationMessageDialog("Successfully saved this contact information.");
                        resetDataEntry();
                        resetDataEntryStyle();
                        setDataEntryChanged(false);
                        broadcastPeonyFaceEventHappened(new ContactInfoEntitySaved(targetContactInfo));
                        broadcastPeonyFaceEventHappened(new CloseDialogRequest(true));
                    }else{
                        PeonyFaceUtils.displayErrorMessageDialog("Failed to save this address location because of the server-side issue.");
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Failed to complete this operation." + ex.getMessage());
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(saveTargetContactInfoTask);
    }

    private void initializeContactDataFields() {
        PeonyFaceUtils.initializeTextField(contactInformationField, targetContactInfo.getContactInfo(), null, "Contact data corresponding to selected contact method", this);
        PeonyFaceUtils.initializeComboBox(contactTypeComboBox, GardenContactType.getEnumValueList(false), targetContactInfo.getContactType(), null, "Contact Method", this);
        PeonyFaceUtils.initializeTextField(memoField, targetContactInfo.getShortMemo(), null, "Memo, max 250 characters", this);
    }
    
    public void validateTargetContactInfo() throws ZcaEntityValidationException {
        targetContactInfo.setContactInfo(contactInformationField.getText());
        targetContactInfo.setContactType(contactTypeComboBox.getValue());
        targetContactInfo.setShortMemo(memoField.getText());
        targetContactInfo.setPreferencePriority(0);
        if (ZcaValidator.isNullEmpty(targetContactInfo.getContactInfoUuid())){
            targetContactInfo.setContactInfoUuid(GardenData.generateUUIDString());
        }
        targetContactInfo.setEntityUuid(entityUuid);
        targetContactInfo.setEntityType(entityType.name());
        try{
            G02EntityValidator.getSingleton().validate(targetContactInfo);
        }catch(ZcaEntityValidationException ex){
            highlightBadEntityField(ex);
            throw ex;
        }
    }

    @Override
    protected void resetDataEntryHelper() {
        contactInformationField.setText(null);
        memoField.setText(null);
        contactTypeComboBox.setValue(null);
    }

    @Override
    protected void resetDataEntryStyleHelper() {
        contactInformationField.setStyle(null);
        memoField.setStyle(null);
        contactTypeComboBox.setStyle(null);
    }

}
