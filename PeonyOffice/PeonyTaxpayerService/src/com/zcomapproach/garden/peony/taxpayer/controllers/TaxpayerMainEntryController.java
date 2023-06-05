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

package com.zcomapproach.garden.peony.taxpayer.controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.data.constant.GardenGendar;
import com.zcomapproach.garden.exception.EntityField;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.TaxpayerInfoDataEntryDeletedEvent;
import com.zcomapproach.garden.peony.view.events.TaxpayerDataEntryDialogCloseRequest;
import com.zcomapproach.garden.peony.view.events.TaxpayerInfoDataEntrySavedEvent;
import com.zcomapproach.garden.persistence.G02EntityValidator;
import com.zcomapproach.garden.persistence.constant.TaxpayerRelationship;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerCase;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerInfo;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaText;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.resources.css.PeonyCss;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.taxpayer.dialogs.TaxpayerContactInfoDialog;
import com.zcomapproach.garden.peony.taxpayer.events.TaxpayerInfoContactChanged;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javax.swing.JOptionPane;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class TaxpayerMainEntryController extends PeonyTaxpayerServiceController implements PeonyFaceEventListener, ITaxpayerProfile{
    @FXML
    private Label titleLabel;
    @FXML
    private JFXTextField taxIDField;
    @FXML
    private JFXComboBox<String> relationshipComboBox;
    @FXML
    private JFXComboBox<String> genderComboBox;
    @FXML
    private JFXTextField firstNameField;
    //@FXML
    //private JFXTextField middleNameField;
    @FXML
    private JFXTextField lastNameField;
    @FXML
    private DatePicker birthdayDatePicker;
////    @FXML
////    private JFXTextField ageAsOfFirstDayField;
////    @FXML
////    private DatePicker deathDatePicker;
    @FXML
    private JFXTextField occupationField;
////    @FXML
////    private JFXTextField citizenshipField;
////    @FXML
////    private JFXComboBox<String> legallyBlindComboBox;
////    @FXML
////    private JFXComboBox<String> liveTogetherLengthComboBox;
////    @FXML
////    private JFXTextField educationCostField;
    @FXML
    private Label contactLabel;
    @FXML
    private Button editContactButton;
    //@FXML
    //private JFXTextField weChatTextField;
    //@FXML
    //private JFXTextField emailField;
    //@FXML
    //private JFXTextField mobilePhoneField;
////    @FXML
////    private JFXTextField homePhoneField;
////    @FXML
////    private JFXTextField workPhoneField;
////    @FXML
////    private JFXTextField faxNumberField;
////    @FXML
////    private JFXTextField memoField;
    /**
     * Functional HBox: save, cancel and delete buttons
     */
    @FXML
    private HBox functionalHBox;
    
    @FXML
    private Label relationshipLabel;
    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label middleNameLabel;
    @FXML
    private Label genderLabel;
    @FXML
    private Label birthdayLabel;
    @FXML
    private Label occupationLabel;
////    @FXML
////    private Label contactInforLabel;
////    @FXML
////    private Label emailLabel;
////    @FXML
////    private Label mobilePhoneLabel;
    
////    private Button addDependentButton;
    private final boolean addDependentBtnRequired;
    /**
     * Optional functional buttons
     */
////    private Button saveButton;
    private Button cancelButton;
    private Button deleteButton;
    private final boolean saveBtnRequired;
    private final boolean deleteBtnRequired;
    private final boolean cancelBtnRequired;
    
    private final G02TaxpayerInfo targetTaxpayerInfo;
    private final PeonyTaxpayerCase targetPeonyTaxpayerCase;
    //private final ComboBox<PeonyTaxpayerCase> compareComboBox;
    
    public TaxpayerMainEntryController(PeonyTaxpayerCase targetPeonyTaxpayerCase, G02TaxpayerInfo targetTaxpayerInfo,
                                       boolean saveBtnRequired, boolean deleteBtnRequired, boolean cancelBtnRequired, boolean addDependentBtnRequired) 
    {
        super(targetPeonyTaxpayerCase);
        if (targetTaxpayerInfo == null){
            targetTaxpayerInfo = new G02TaxpayerInfo();
        }
        this.targetTaxpayerInfo = targetTaxpayerInfo;
        this.targetPeonyTaxpayerCase = targetPeonyTaxpayerCase;
        this.saveBtnRequired = saveBtnRequired;
        this.deleteBtnRequired = deleteBtnRequired;
        this.cancelBtnRequired = cancelBtnRequired;
        this.addDependentBtnRequired = addDependentBtnRequired;
    }
    
    public boolean isSaveBtnRequired() {
        return saveBtnRequired;
    }

    public boolean isDeleteBtnRequired() {
        return deleteBtnRequired || (TaxpayerRelationship.SPOUSE_TAXPAYER.value().equalsIgnoreCase(targetTaxpayerInfo.getRelationships()));
    }

    public boolean isCancelBtnRequired() {
        return cancelBtnRequired;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        initializeDataEntryForTargetTaxpayerInfo();
    }
    
    private Task<Boolean> createDeleteTaxpayerInfoDataEntryTask(){
        return new Task<Boolean>(){
            @Override
            protected Boolean call() throws Exception {
                try{
                    Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient().deleteEntity_XML(
                                G02TaxpayerInfo.class, GardenRestParams.Taxpayer.deleteTaxpayerInfoRestParams(targetTaxpayerInfo.getTaxpayerUserUuid()));
                    updateMessage("Successfully deleted this taxpayer info data entry.");
                    return true;
                }catch (Exception ex){
                    updateMessage("Failed to deleted this taxpayer info data entry." + ex.getMessage());
                    return false;
                }
            }

            @Override
            protected void succeeded() {
                //GUI...
                try {
                    if (get()){
                        resetDataEntry();
                        resetDataEntryStyle();
                        broadcastPeonyFaceEventHappened(new TaxpayerInfoDataEntryDeletedEvent(targetTaxpayerInfo));
                        PeonyFaceUtils.displayInformationMessageDialog(getMessage());
                    }else{
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    //Exceptions.printStackTrace(ex);
                }
            }
        };
    }
    
    private Task<G02TaxpayerInfo> createSaveTaxpayerInfoDataEntryTask(){
        
        return new Task<G02TaxpayerInfo>(){
            @Override
            protected G02TaxpayerInfo call() throws Exception {
                try{
                    G02TaxpayerCase aG02TaxpayerCase;
                    if (ZcaValidator.isNullEmpty(targetTaxpayerInfo.getTaxpayerCaseUuid())){
                        aG02TaxpayerCase = null;
                    }else{
                        try{
                            loadTargetTaxpayerInfo();
                        }catch (ZcaEntityValidationException ex){
                            updateMessage(ex.getMessage());
                            return null;
                        }
                        aG02TaxpayerCase = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient().findEntity_XML(G02TaxpayerCase.class, 
                                GardenRestParams.Taxpayer.findTaxpayerCaseByTaxpayerCaseUuidRestParams(targetTaxpayerInfo.getTaxpayerCaseUuid()));
                    }
                    if (aG02TaxpayerCase == null){
                        updateMessage("This taxpayer info data entry is NOT saved. Please save Taxpayer case's basic information before save this data entry.");
                        return null;
                    }else{
                        G02TaxpayerInfo aG02TaxpayerInfo = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient().storeEntity_XML(
                                G02TaxpayerInfo.class, GardenRestParams.Taxpayer.storeTaxpayerInfoRestParams(), targetTaxpayerInfo);
                        if (aG02TaxpayerInfo == null){
                            updateMessage("Failed to save this data entry.");
                            return null;
                        }else{
                            updateMessage("Successfully saved this data entry.");
                        }
                    }
                    return targetTaxpayerInfo;
                }catch (Exception ex){
                    updateMessage("Technical error. " + ex.getMessage());
                    return null;
                }
            }

            @Override
            protected void succeeded() {
                //GUI...
                int msgType = JOptionPane.INFORMATION_MESSAGE;
                try {
                    G02TaxpayerInfo aG02TaxpayerInfo = get();
                    if (aG02TaxpayerInfo == null){
                        msgType = JOptionPane.ERROR_MESSAGE;
                    }else{
                        //saved taxpayer info data entry
                        broadcastPeonyFaceEventHappened(new TaxpayerInfoDataEntrySavedEvent(aG02TaxpayerInfo));
                        //close the data entry dialog
                        broadcastPeonyFaceEventHappened(new TaxpayerDataEntryDialogCloseRequest(aG02TaxpayerInfo.getTaxpayerUserUuid()));
                        
                        resetDataEntryStyle();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }

                //display result information dialog
                String msg = getMessage();
                if (ZcaValidator.isNotNullEmpty(msg)){
                    PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, msg, "Taxpayer Info", msgType);
                }
            }
        };
    }

    @Override
    public void loadTargetTaxpayerInfo() throws ZcaEntityValidationException {
        targetTaxpayerInfo.setBirthday(ZcaCalendar.convertToDate(birthdayDatePicker.getValue()));
        targetTaxpayerInfo.setCreated(new Date());
        targetTaxpayerInfo.setEntityStatus(null);
        targetTaxpayerInfo.setFirstName(firstNameField.getText());
        targetTaxpayerInfo.setLastName(lastNameField.getText());
////        targetTaxpayerInfo.setMemo(memoField.getText());
        //targetTaxpayerInfo.setMiddleName(middleNameField.getText());
        targetTaxpayerInfo.setOccupation(occupationField.getText());
        targetTaxpayerInfo.setRelationships(relationshipComboBox.getValue());
        targetTaxpayerInfo.setGender(genderComboBox.getValue());
        targetTaxpayerInfo.setSsn(taxIDField.getText());
        targetTaxpayerInfo.setTaxpayerCaseUuid(targetPeonyTaxpayerCase.getTaxpayerCase().getTaxpayerCaseUuid());
        if (ZcaValidator.isNullEmpty(targetTaxpayerInfo.getTaxpayerUserUuid())){
            targetTaxpayerInfo.setTaxpayerUserUuid(ZcaUtils.generateUUIDString());
        }
        //targetTaxpayerInfo.setEmail(emailField.getText());
        //targetTaxpayerInfo.setMobilePhone(mobilePhoneField.getText());
        //targetTaxpayerInfo.setSocialNetworkId(weChatTextField.getText());
////        targetTaxpayerInfo.setAgeAsOfFirstDay(ageAsOfFirstDayField.getText());
////        targetTaxpayerInfo.setCitizenship(citizenshipField.getText());
////        targetTaxpayerInfo.setDateOfDeath(ZcaCalendar.convertToDate(deathDatePicker.getValue()));
////        targetTaxpayerInfo.setEducationCost(educationCostField.getText());
////        targetTaxpayerInfo.setFax(faxNumberField.getText());
////        targetTaxpayerInfo.setHomePhone(homePhoneField.getText());
////        targetTaxpayerInfo.setLegallyBlind(legallyBlindComboBox.getValue());
////        targetTaxpayerInfo.setLengthOfLivingTogether(liveTogetherLengthComboBox.getValue());
        //targetTaxpayerInfo.setUpdated(null);
////        targetTaxpayerInfo.setWorkPhone(workPhoneField.getText());
        
        try{
            G02EntityValidator.getSingleton().validate(targetTaxpayerInfo);
        }catch(ZcaEntityValidationException ex){
            highlightBadEntityField(ex);
            throw ex;
        }
    }
    
    @Override
    protected void resetDataEntryHelper() {
        targetTaxpayerInfo.setAgeAsOfFirstDay(null);
        targetTaxpayerInfo.setBirthday(null);
        targetTaxpayerInfo.setCitizenship(null);
        targetTaxpayerInfo.setCreated(new Date());
        targetTaxpayerInfo.setDateOfDeath(null);
        targetTaxpayerInfo.setEducationCost(null);
        targetTaxpayerInfo.setEmail(null);
        targetTaxpayerInfo.setEntityStatus(null);
        targetTaxpayerInfo.setFax(null);
        targetTaxpayerInfo.setFirstName(null);
        targetTaxpayerInfo.setHomePhone(null);
        targetTaxpayerInfo.setLastName(null);
        targetTaxpayerInfo.setLegallyBlind(null);
        targetTaxpayerInfo.setLengthOfLivingTogether(null);
        targetTaxpayerInfo.setMemo(null);
        targetTaxpayerInfo.setMiddleName(null);
        targetTaxpayerInfo.setMobilePhone(null);
        targetTaxpayerInfo.setOccupation(null);
        targetTaxpayerInfo.setRelationships(null);
        targetTaxpayerInfo.setSsn(null);
        targetTaxpayerInfo.setTaxpayerCaseUuid(ZcaUtils.generateUUIDString());
        targetTaxpayerInfo.setTaxpayerUserUuid(null);
        targetTaxpayerInfo.setUpdated(null);
        targetTaxpayerInfo.setWorkPhone(null);
        
        initializeDataEntryForTargetTaxpayerInfo();
    }
    
    private void initializeSaveButton(){
////        if ((addDependentButton == null) && addDependentBtnRequired){
////            addDependentButton = new Button("Add Dependent");
////            addDependentButton.setPrefHeight(25.0);
////            addDependentButton.setStyle("-fx-font-size: 12");
////            addDependentButton.setOnAction((ActionEvent actionEvent) -> {
////                broadcastPeonyFaceEventHappened(new AddDependentRequest());
////            });
////            functionalHBox.getChildren().add(addDependentButton);
////        }
////        if (saveButton == null){
////            saveButton = new Button("Save");
////            saveButton.setPrefWidth(100.0);
////            saveButton.setPrefHeight(25.0);
////            saveButton.setStyle("-fx-font-size: 12");
////            //saveButton.getStyleClass().add("peony-primary-small-button");
////            saveButton.setOnAction((ActionEvent actionEvent) -> {
////                saveProfile();
////            });
////        }
////        //display it or not...
////        if (isSaveBtnRequired()){
////            functionalHBox.getChildren().remove(saveButton);
////            functionalHBox.getChildren().add(saveButton);
////        }else{
////            functionalHBox.getChildren().remove(saveButton);
////        }
    }

    @Override
    public G02TaxpayerInfo saveProfile() throws Exception {
        return Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient().storeEntity_XML(
                                G02TaxpayerInfo.class, GardenRestParams.Taxpayer.storeTaxpayerInfoRestParams(), targetTaxpayerInfo);
    }
    
    @Override
    public void validateProfileData() throws ZcaEntityValidationException{
        if (ZcaValidator.isNullEmpty(relationshipComboBox.getValue())){
            throw new ZcaEntityValidationException("Please select relationship for this primary/spouse taxpayer before save this record.");
        }
        if (ZcaValidator.isNullEmpty(genderComboBox.getValue())){
            throw new ZcaEntityValidationException("Please select gender for this primary/spouse taxpayer before save this record.");
        }
        if (ZcaValidator.isNullEmpty(taxIDField.getText())){
            throw new ZcaEntityValidationException("Please input TaxID or SSN for this primary/spouse taxpayer before save this record.");
        }
        if (ZcaValidator.isNullEmpty(firstNameField.getText())){
            throw new ZcaEntityValidationException("Please input first name for this primary/spouse taxpayer before save this record.");
        }
        if (ZcaValidator.isNullEmpty(lastNameField.getText())){
            throw new ZcaEntityValidationException("Please input last name for this primary/spouse taxpayer before save this record.");
        }
    }
    
    private void initializeCancelButton(){
        if (cancelButton == null){
            cancelButton = new Button("Cancel");
            cancelButton.setPrefWidth(100.0);
            cancelButton.setPrefHeight(25.0);
            cancelButton.setStyle("-fx-font-size: 12");
            //cancelButton.getStyleClass().add("peony-primary-small-button");
            cancelButton.setOnAction((ActionEvent actionEvent) -> {
                if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to close this dialog?") == JOptionPane.YES_OPTION){
                    broadcastPeonyFaceEventHappened(new TaxpayerDataEntryDialogCloseRequest(targetTaxpayerInfo.getTaxpayerUserUuid()));
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
    
    private void initializeDeleteButton(){
        if (deleteButton == null){
            deleteButton = new Button("Delete");
            deleteButton.setPrefWidth(100.0);
            deleteButton.setPrefHeight(25.0);
            deleteButton.setStyle("-fx-font-size: 12");
            //deleteButton.getStyleClass().add("peony-primary-small-button");
            deleteButton.setOnAction((ActionEvent actionEvent) -> {
                if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to delete this taxpayer profile?") == JOptionPane.YES_OPTION){
                    getCachedThreadPoolExecutorService().submit(createDeleteTaxpayerInfoDataEntryTask());
                }
            });
        }
        //display it or not...
        if (isDeleteBtnRequired()){
            functionalHBox.getChildren().remove(deleteButton);
            functionalHBox.getChildren().add(deleteButton);
        }else{
            functionalHBox.getChildren().remove(deleteButton);
        }
    }
    
    private void initializeDataEntryForTargetTaxpayerInfo() {
        
        PeonyFaceUtils.initializeTextField(taxIDField, targetTaxpayerInfo.getSsn(), null, "TaxID or SSN of taxpayers or dependents", this);
        
        List<String> relationshipList;
        if ((TaxpayerRelationship.PRIMARY_TAXPAYER.value().equalsIgnoreCase(targetTaxpayerInfo.getRelationships()))
                || (TaxpayerRelationship.SPOUSE_TAXPAYER.value().equalsIgnoreCase(targetTaxpayerInfo.getRelationships())))
        {
            //relationshipComboBox.disableProperty().setValue(false);
            relationshipList = new ArrayList<>();
            relationshipList.add(targetTaxpayerInfo.getRelationships());
        }else{
            //relationshipComboBox.disableProperty().setValue(true);
            relationshipList = TaxpayerRelationship.getTaxpayerDependantValueList();
        }
        if (ZcaValidator.isNullEmpty(targetTaxpayerInfo.getRelationships())){
            titleLabel.setText("Taxpayer's Dependent");
            relationshipComboBox.disableProperty().setValue(false);
        }else{
            titleLabel.setText(targetTaxpayerInfo.getRelationships());
        }
        PeonyFaceUtils.initializeComboBox(relationshipComboBox, 
                                          relationshipList,
                                          targetTaxpayerInfo.getRelationships(), null, 
                                          "Primaty taxpayer or relationship to the primary taxpayer", 
                                          this);
        PeonyFaceUtils.initializeComboBox(genderComboBox, 
                                          GardenGendar.getEnumValueList(false),
                                          targetTaxpayerInfo.getGender(), null, 
                                          "Gendar of taxpayer", 
                                          this);
        PeonyFaceUtils.initializeTextField(firstNameField, targetTaxpayerInfo.getFirstName(), null, "First name", this);
        //PeonyFaceUtils.initializeTextField(middleNameField, targetTaxpayerInfo.getMiddleName(), null, "Middle name", this);
        PeonyFaceUtils.initializeTextField(lastNameField, targetTaxpayerInfo.getLastName(), null, "Last name", this);
        PeonyFaceUtils.initializeDatePicker(birthdayDatePicker, ZcaCalendar.convertToLocalDate(targetTaxpayerInfo.getBirthday()), 
                                            null, "Birthday", this);
        PeonyFaceUtils.initializeTextField(occupationField, targetTaxpayerInfo.getOccupation(), null, "Occupation", this);
        //PeonyFaceUtils.initializeTextField(emailField, targetTaxpayerInfo.getEmail(), null, "Email", this);
        //PeonyFaceUtils.initializeTextField(mobilePhoneField, targetTaxpayerInfo.getMobilePhone(), null, "Mobile phone number", this);
        //PeonyFaceUtils.initializeTextField(weChatTextField, targetTaxpayerInfo.getSocialNetworkId(), null, "WeChat ID", this);
        contactLabel.setText(constructContactInfo(targetTaxpayerInfo));
////        PeonyFaceUtils.initializeTextField(memoField, targetTaxpayerInfo.getMemo(), null, "Memo", this);
        editContactButton.setText("");
        editContactButton.setGraphic(PeonyGraphic.getImageView("user_edit.png"));
        editContactButton.setOnAction((ActionEvent event) -> {
            TaxpayerContactInfoDialog aTaxpayerContactInfoDialog = new TaxpayerContactInfoDialog(null, true);
            aTaxpayerContactInfoDialog.addPeonyFaceEventListener(this);
            aTaxpayerContactInfoDialog.launchTaxpayerContactInfoDialog("Taxpayer Contact Information", targetTaxpayerInfo);
        });
        
        initializeSaveButton();
        initializeCancelButton();
        initializeDeleteButton();
    }

    boolean hasNoContactInformation() {
        return (PeonyTaxpayerServiceController.NO_CONTACT.equalsIgnoreCase(contactLabel.getText())) 
                || (ZcaValidator.isNullEmpty(contactLabel.getText()));
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof TaxpayerInfoContactChanged){
            handleTaxpayerInfoContactChanged((TaxpayerInfoContactChanged)event);
        }
    }

    private void handleTaxpayerInfoContactChanged(final TaxpayerInfoContactChanged taxpayerInfoContactChanged) {
        if (Platform.isFxApplicationThread()){
            handleTaxpayerInfoContactChangedHelper(taxpayerInfoContactChanged);
        }else{
            Platform.runLater(() -> {
                handleTaxpayerInfoContactChangedHelper(taxpayerInfoContactChanged);
            });
        }    
    }
    
    private void handleTaxpayerInfoContactChangedHelper(final TaxpayerInfoContactChanged taxpayerInfoContactChanged) {
        if (taxpayerInfoContactChanged.getTargetTaxpayerInfo() == null){
            return;
        }
        targetTaxpayerInfo.setEmail(taxpayerInfoContactChanged.getTargetTaxpayerInfo().getEmail());
        targetTaxpayerInfo.setMobilePhone(taxpayerInfoContactChanged.getTargetTaxpayerInfo().getMobilePhone());
        targetTaxpayerInfo.setSocialNetworkId(taxpayerInfoContactChanged.getTargetTaxpayerInfo().getSocialNetworkId());
        
        contactLabel.setText(constructContactInfo(targetTaxpayerInfo));
    }
    
    @Override
    protected Node getBadEntityField(EntityField entityFiled) {
        switch (entityFiled){
            //case Email:
            //    return emailField;
            //case MobilePhone:
            //    return mobilePhoneField;
            //case SocialNetworkId:
            //    return weChatTextField;
            case EntityStatus:
                return null;
            case FirstName:
                return firstNameField;
            case Gender:
                return genderComboBox;
            case LastName:
                return lastNameField;
////            case Memo:
////                return memoField;
            //case MiddleName:
            //    return middleNameField;
            case Occupation:
                return occupationField;
            case Relationships:
                return relationshipComboBox;
            case SocialNetworkType:
                return null;
            case SSN:
                return taxIDField;
////            case AgeAsOfFirstDay:
////                return ageAsOfFirstDayField;
////            case Citizenship:
////                return citizenshipField;
////            case EducationCost:
////                return educationCostField;
////            case Fax:
////                return faxNumberField;
////            case HomePhone:
////                return homePhoneField;
////            case LegallyBlind:
////                return legallyBlindComboBox;
////            case LengthOfLivingTogether:
////                return liveTogetherLengthComboBox;
////            case WorkPhone:
////                return workPhoneField;
            default:
                return null;
        }
    }

    @Override
    protected void resetDataEntryStyleHelper() {
        //emailField.setStyle(null);
        //mobilePhoneField.setStyle(null);
        //weChatTextField.setStyle(null);
        firstNameField.setStyle(null);
        genderComboBox.setStyle(null);
        lastNameField.setStyle(null);
////        memoField.setStyle(null);
        //middleNameField.setStyle(null);
        occupationField.setStyle(null);
        relationshipComboBox.setStyle(null);
        taxIDField.setStyle(null);
////        ageAsOfFirstDayField.setStyle(null);
////        citizenshipField.setStyle(null);
////        educationCostField.setStyle(null);
////        faxNumberField.setStyle(null);
////        homePhoneField.setStyle(null);
////        legallyBlindComboBox.setStyle(null);
////        liveTogetherLengthComboBox.setStyle(null);
////        workPhoneField.setStyle(null);
    }
    
    private void updateTaxpayerInfoFieldLabel(EntityField entityField, G02TaxpayerInfo legacyTaxpayerInfo){
        switch (entityField){
            case Relationships:
                /**
                 * because of old bug: location was shared by taxpayer cases. So, the legacy location may be null. After 2022, each case has its own location
                 */
                if (ZcaValidator.isNullEmpty(legacyTaxpayerInfo.getRelationships())){
                    break;
                }
                if (ZcaText.compareTextValueIgnoreCase(targetTaxpayerInfo.getRelationships(),  legacyTaxpayerInfo.getRelationships())){
                    relationshipLabel.setText("Relationship");
                    relationshipLabel.setTextFill(PeonyCss.BLACK);
                    relationshipLabel.getStyleClass().remove("peony-success-small-label");
                }else{
                    relationshipLabel.setText("Relationship: " + legacyTaxpayerInfo.getRelationships());
                    relationshipLabel.setTextFill(PeonyCss.WHITE);
                    relationshipLabel.getStyleClass().add("peony-success-small-label");
                }
                break;
            case FirstName:
                if (ZcaValidator.isNullEmpty(legacyTaxpayerInfo.getFirstName())){
                    break;
                }
                if (ZcaText.compareTextValueIgnoreCase(targetTaxpayerInfo.getFirstName(),  legacyTaxpayerInfo.getFirstName())){
                    firstNameLabel.setText("Frist Name");
                    firstNameLabel.setTextFill(PeonyCss.BLACK);
                    firstNameLabel.getStyleClass().remove("peony-success-small-label");
                }else{
                    firstNameLabel.setText("Frist Name: " + legacyTaxpayerInfo.getFirstName());
                    firstNameLabel.setTextFill(PeonyCss.WHITE);
                    firstNameLabel.getStyleClass().add("peony-success-small-label");
                }
                break;
            case LastName:
                if (ZcaValidator.isNullEmpty(legacyTaxpayerInfo.getLastName())){
                    break;
                }
                if (ZcaText.compareTextValueIgnoreCase(targetTaxpayerInfo.getLastName(),  legacyTaxpayerInfo.getLastName())){
                    lastNameLabel.setText("Last Name");
                    lastNameLabel.setTextFill(PeonyCss.BLACK);
                    lastNameLabel.getStyleClass().remove("peony-success-small-label");
                }else{
                    lastNameLabel.setText("Last Name: " + legacyTaxpayerInfo.getLastName());
                    lastNameLabel.setTextFill(PeonyCss.WHITE);
                    lastNameLabel.getStyleClass().add("peony-success-small-label");
                }
                break;
            case MiddleName:
                if (ZcaValidator.isNullEmpty(legacyTaxpayerInfo.getMiddleName())){
                    break;
                }
                if (ZcaText.compareTextValueIgnoreCase(targetTaxpayerInfo.getMiddleName(),  legacyTaxpayerInfo.getMiddleName())){
                    middleNameLabel.setText("Middle Name");
                    middleNameLabel.setTextFill(PeonyCss.BLACK);
                    middleNameLabel.getStyleClass().remove("peony-success-small-label");
                }else{
                    middleNameLabel.setText("Middle Name: " + legacyTaxpayerInfo.getMiddleName());
                    middleNameLabel.setTextFill(PeonyCss.WHITE);
                    middleNameLabel.getStyleClass().add("peony-success-small-label");
                }
                break;
            case Gender:
                if (ZcaValidator.isNullEmpty(legacyTaxpayerInfo.getGender())){
                    break;
                }
                if (ZcaText.compareTextValueIgnoreCase(targetTaxpayerInfo.getGender(),  legacyTaxpayerInfo.getGender())){
                    genderLabel.setText("Gender");
                    genderLabel.setTextFill(PeonyCss.BLACK);
                    genderLabel.getStyleClass().remove("peony-success-small-label");
                }else{
                    genderLabel.setText("Gender: " + legacyTaxpayerInfo.getGender());
                    genderLabel.setTextFill(PeonyCss.WHITE);
                    genderLabel.getStyleClass().add("peony-success-small-label");
                }
                break;
            case Birthday:
                if (legacyTaxpayerInfo.getBirthday() == null){
                    break;
                }
                if (ZcaText.compareDateValue(targetTaxpayerInfo.getBirthday(),  legacyTaxpayerInfo.getBirthday())){
                    birthdayLabel.setText("Birthday");
                    birthdayLabel.setTextFill(PeonyCss.BLACK);
                    birthdayLabel.getStyleClass().remove("peony-success-small-label");
                }else{
                    birthdayLabel.setText("Birthday: " + ZcaCalendar.convertToMMddyyyy(legacyTaxpayerInfo.getBirthday(), "-"));
                    birthdayLabel.setTextFill(PeonyCss.WHITE);
                    birthdayLabel.getStyleClass().add("peony-success-small-label");
                }
                break;
            case Occupation:
                if (ZcaValidator.isNullEmpty(legacyTaxpayerInfo.getOccupation())){
                    break;
                }
                if (ZcaText.compareTextValueIgnoreCase(targetTaxpayerInfo.getOccupation(),  legacyTaxpayerInfo.getOccupation())){
                    occupationLabel.setText("Occupation");
                    occupationLabel.setTextFill(PeonyCss.BLACK);
                    occupationLabel.getStyleClass().remove("peony-success-small-label");
                }else{
                    occupationLabel.setText("Occupation: " + legacyTaxpayerInfo.getOccupation());
                    occupationLabel.setTextFill(PeonyCss.WHITE);
                    occupationLabel.getStyleClass().add("peony-success-small-label");
                }
                break;
////            case SocialNetworkId:
////                if (ZcaText.compareTextValueIgnoreCase(targetTaxpayerInfo.getSocialNetworkId(),  legacyTaxpayerInfo.getSocialNetworkId())){
////                    weChatLabel.setText("WeChat ID");
////                    weChatLabel.setTextFill(PeonyCss.BLACK);
////                }else{
////                    weChatLabel.setText("WeChat ID: " + legacyTaxpayerInfo.getSocialNetworkId());
////                    weChatLabel.setTextFill(PeonyCss.DARKRED);
////                }
////                break;
////            case Email:
////                if (ZcaText.compareTextValueIgnoreCase(targetTaxpayerInfo.getEmail(),  legacyTaxpayerInfo.getEmail())){
////                    emailLabel.setText("Email");
////                    emailLabel.setTextFill(PeonyCss.BLACK);
////                }else{
////                    emailLabel.setText("Email: " + legacyTaxpayerInfo.getEmail());
////                    emailLabel.setTextFill(PeonyCss.DARKRED);
////                }
////                break;
////            case MobilePhone:
////                if (ZcaText.compareTextValueIgnoreCase(targetTaxpayerInfo.getMobilePhone(),  legacyTaxpayerInfo.getMobilePhone())){
////                    mobilePhoneLabel.setText("Mobile Phone");
////                    mobilePhoneLabel.setTextFill(PeonyCss.BLACK);
////                }else{
////                    mobilePhoneLabel.setText("Mobile Phone: " + legacyTaxpayerInfo.getMobilePhone());
////                    mobilePhoneLabel.setTextFill(PeonyCss.DARKRED);
////                }
////                break;
            default:
        }
    }

    @Override
    protected void populateLegacyPeonyTaxpayerCaseComparison(PeonyTaxpayerCase legacyPeonyTaxpayerCase) {
        if ((legacyPeonyTaxpayerCase == null) || (legacyPeonyTaxpayerCase.getTaxpayerCase() == null)){
            return;
        }
        G02TaxpayerInfo legacyTaxpayerInfo = legacyPeonyTaxpayerCase.retrieveTaxpayerInfoBySsn(targetTaxpayerInfo.getSsn());
        if (legacyTaxpayerInfo == null){
            return;
        }
        updateTaxpayerInfoFieldLabel(EntityField.Relationships, legacyTaxpayerInfo);
        updateTaxpayerInfoFieldLabel(EntityField.FirstName, legacyTaxpayerInfo);
        updateTaxpayerInfoFieldLabel(EntityField.LastName, legacyTaxpayerInfo);
        //updateTaxpayerInfoFieldLabel(EntityField.MiddleName, legacyTaxpayerInfo);
        updateTaxpayerInfoFieldLabel(EntityField.Gender, legacyTaxpayerInfo);
        updateTaxpayerInfoFieldLabel(EntityField.Birthday, legacyTaxpayerInfo);
        updateTaxpayerInfoFieldLabel(EntityField.Occupation, legacyTaxpayerInfo);
        updateTaxpayerInfoFieldLabel(EntityField.SocialNetworkId, legacyTaxpayerInfo);
        updateTaxpayerInfoFieldLabel(EntityField.Email, legacyTaxpayerInfo);
        updateTaxpayerInfoFieldLabel(EntityField.MobilePhone, legacyTaxpayerInfo);
        
    }
}
