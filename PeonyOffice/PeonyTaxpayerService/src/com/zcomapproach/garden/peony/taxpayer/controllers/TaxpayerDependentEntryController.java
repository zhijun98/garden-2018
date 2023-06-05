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
import com.zcomapproach.garden.persistence.G02EntityValidator;
import com.zcomapproach.garden.persistence.constant.TaxpayerLengthOfLivingTogetherOption;
import com.zcomapproach.garden.persistence.constant.TaxpayerRelationship;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerInfo;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaText;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.resources.css.PeonyCss;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javax.swing.JOptionPane;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class TaxpayerDependentEntryController extends PeonyTaxpayerServiceController implements ITaxpayerProfile {
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
////    @FXML
////    private JFXTextField middleNameField;
    @FXML
    private JFXTextField lastNameField;
    @FXML
    private DatePicker birthdayDatePicker;
//    @FXML
//    private JFXTextField weChatTextField;
////    @FXML
////    private JFXTextField ageAsOfFirstDayField;
////    @FXML
////    private DatePicker deathDatePicker;
////    @FXML
////    private JFXTextField occupationField;
////    @FXML
////    private JFXTextField citizenshipField;
////    @FXML
////    private JFXComboBox<String> legallyBlindComboBox;
    @FXML
    private JFXComboBox<String> liveTogetherLengthComboBox;
    @FXML
    private JFXTextField educationCostField;
////    @FXML
////    private JFXTextField emailField;
////    @FXML
////    private JFXTextField mobilePhoneField;
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
////    @FXML
////    private Label middleNameLabel;
    @FXML
    private Label genderLabel;
    @FXML
    private Label birthdayLabel;
    @FXML
    private Label liveTogetherLengthLabel;
    @FXML
    private Label educationCostLabel;
    
////    private Button addDependentButton;
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
    
    private TitledPane ownerTitledPane;

    public TaxpayerDependentEntryController(PeonyTaxpayerCase targetPeonyTaxpayerCase, G02TaxpayerInfo targetTaxpayerInfo, 
                                       boolean saveBtnRequired, boolean deleteBtnRequired, boolean cancelBtnRequired) 
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
    }

    public void setOwnerTitledPane(TitledPane ownerTitledPane) {
        this.ownerTitledPane = ownerTitledPane;
    }

    public TitledPane getOwnerTitledPane() {
        return ownerTitledPane;
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

    @Override
    public void loadTargetTaxpayerInfo() throws ZcaEntityValidationException {
        targetTaxpayerInfo.setBirthday(ZcaCalendar.convertToDate(birthdayDatePicker.getValue()));
        targetTaxpayerInfo.setCreated(new Date());
        targetTaxpayerInfo.setEducationCost(educationCostField.getText());
        targetTaxpayerInfo.setEntityStatus(null);
        targetTaxpayerInfo.setFirstName(firstNameField.getText());
        targetTaxpayerInfo.setLastName(lastNameField.getText());
        targetTaxpayerInfo.setLengthOfLivingTogether(liveTogetherLengthComboBox.getValue());
////        targetTaxpayerInfo.setMemo(memoField.getText());
////        targetTaxpayerInfo.setMiddleName(middleNameField.getText());
        targetTaxpayerInfo.setRelationships(relationshipComboBox.getValue());
        targetTaxpayerInfo.setGender(genderComboBox.getValue());
        targetTaxpayerInfo.setSsn(taxIDField.getText());
        targetTaxpayerInfo.setTaxpayerCaseUuid(targetPeonyTaxpayerCase.getTaxpayerCase().getTaxpayerCaseUuid());
        if (ZcaValidator.isNullEmpty(targetTaxpayerInfo.getTaxpayerUserUuid())){
            targetTaxpayerInfo.setTaxpayerUserUuid(ZcaUtils.generateUUIDString());
        }
        //targetTaxpayerInfo.setUpdated(null);
////        targetTaxpayerInfo.setAgeAsOfFirstDay(ageAsOfFirstDayField.getText());
////        targetTaxpayerInfo.setCitizenship(citizenshipField.getText());
////        targetTaxpayerInfo.setDateOfDeath(ZcaCalendar.convertToDate(deathDatePicker.getValue()));
////        targetTaxpayerInfo.setEmail(emailField.getText());
////        targetTaxpayerInfo.setFax(faxNumberField.getText());
////        targetTaxpayerInfo.setHomePhone(homePhoneField.getText());
////        targetTaxpayerInfo.setLegallyBlind(legallyBlindComboBox.getValue());
////        targetTaxpayerInfo.setMobilePhone(mobilePhoneField.getText());
////        targetTaxpayerInfo.setOccupation(occupationField.getText());
////        targetTaxpayerInfo.setWorkPhone(workPhoneField.getText());
////        targetTaxpayerInfo.setSocialNetworkId(weChatTextField.getText());
        
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
        targetTaxpayerInfo.setUpdated(null);
        targetTaxpayerInfo.setWorkPhone(null);
        
        initializeDataEntryForTargetTaxpayerInfo();
    }
    
    private void initializeSaveButton(){
        if (Platform.isFxApplicationThread()){
            initializeSaveButtonHelper();
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    initializeSaveButtonHelper();
                }
            });
        }
    }
    
    private void initializeSaveButtonHelper(){
////        if (addDependentButton == null){
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
////////            saveButton.getStyleClass().add("peony-primary-small-button");
////            saveButton.setOnAction((ActionEvent actionEvent) -> {
////                saveProfile();
////            });
////            //display it or not...
////            if (isSaveBtnRequired()){
////                functionalHBox.getChildren().add(saveButton);
////            }else{
////                functionalHBox.getChildren().remove(saveButton);
////            }
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
            throw new ZcaEntityValidationException("Please select relationship for dependents before save this record.");
        }
        if (ZcaValidator.isNullEmpty(genderComboBox.getValue())){
            throw new ZcaEntityValidationException("Please select gender for dependents taxpayer before save this record.");
        }
        if (ZcaValidator.isNullEmpty(taxIDField.getText())){
            throw new ZcaEntityValidationException("Please input TaxID or SSN for dependents taxpayer before save this record.");
        }
        if (ZcaValidator.isNullEmpty(firstNameField.getText())){
            throw new ZcaEntityValidationException("Please input first name for dependents taxpayer before save this record.");
        }
        if (ZcaValidator.isNullEmpty(lastNameField.getText())){
            throw new ZcaEntityValidationException("Please input last name for dependents taxpayer before save this record.");
        }
    }
    
    private void initializeCancelButton(){
        if (cancelButton == null){
            cancelButton = new Button("Cancel");
            cancelButton.setPrefWidth(100.0);
            cancelButton.setPrefHeight(25.0);
            cancelButton.setStyle("-fx-font-size: 12");
////            cancelButton.getStyleClass().add("peony-primary-small-button");
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
////            deleteButton.getStyleClass().add("peony-primary-small-button");
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
////        PeonyFaceUtils.initializeTextField(middleNameField, targetTaxpayerInfo.getMiddleName(), null, "Middle name", this);
        PeonyFaceUtils.initializeTextField(lastNameField, targetTaxpayerInfo.getLastName(), null, "Last name", this);
        PeonyFaceUtils.initializeDatePicker(birthdayDatePicker, ZcaCalendar.convertToLocalDate(targetTaxpayerInfo.getBirthday()), 
                                            null, "Birthday", this);
        PeonyFaceUtils.initializeComboBox(liveTogetherLengthComboBox, TaxpayerLengthOfLivingTogetherOption.getEnumValueList(false), targetTaxpayerInfo.getLengthOfLivingTogether(), null, "Length of living with primary taxpayer", this);
        PeonyFaceUtils.initializeTextField(educationCostField, targetTaxpayerInfo.getEducationCost(), null, "Education cost for depenedents", this);
////        PeonyFaceUtils.initializeTextField(memoField, targetTaxpayerInfo.getMemo(), null, "Memo", this);
////        PeonyFaceUtils.initializeTextField(ageAsOfFirstDayField, targetTaxpayerInfo.getAgeAsOfFirstDay(), 
////                                           null, "Age as of first day of this year", this);
////        PeonyFaceUtils.initializeDatePicker(deathDatePicker, ZcaCalendar.convertToLocalDate(targetTaxpayerInfo.getDateOfDeath()), 
////                                            null, "Death date if taxpayer past away", this);
////        PeonyFaceUtils.initializeTextField(occupationField, targetTaxpayerInfo.getOccupation(), null, "Occupation", this);
////        PeonyFaceUtils.initializeTextField(citizenshipField, targetTaxpayerInfo.getCitizenship(), null, "Citizenship", this);
////        PeonyFaceUtils.initializeComboBox(legallyBlindComboBox, GardenBooleanValue.getEnumValueList(false), targetTaxpayerInfo.getLegallyBlind(), null, "Legally blind or not", this);
////        PeonyFaceUtils.initializeTextField(emailField, targetTaxpayerInfo.getEmail(), null, "Email", this);
////        PeonyFaceUtils.initializeTextField(mobilePhoneField, targetTaxpayerInfo.getMobilePhone(), null, "Mobile phone number", this);
////        PeonyFaceUtils.initializeTextField(homePhoneField, targetTaxpayerInfo.getHomePhone(), null, "Home phone number", this);
////        PeonyFaceUtils.initializeTextField(workPhoneField, targetTaxpayerInfo.getWorkPhone(), null, "Work phone number", this);
////        PeonyFaceUtils.initializeTextField(weChatTextField, targetTaxpayerInfo.getSocialNetworkId(), null, "WeChat ID", this);
////        PeonyFaceUtils.initializeTextField(faxNumberField, targetTaxpayerInfo.getFax(), null, "Fax number", this);
        
        initializeSaveButton();
        initializeCancelButton();
        initializeDeleteButton();
    }
    
    @Override
    protected Node getBadEntityField(EntityField entityFiled) {
        switch (entityFiled){
            case EducationCost:
                return educationCostField;
            case EntityStatus:
                return null;
            case FirstName:
                return firstNameField;
            case LastName:
                return lastNameField;
            case Gender:
                return genderComboBox;
            case LengthOfLivingTogether:
                return liveTogetherLengthComboBox;
////            case Memo:
////                return memoField;
////            case MiddleName:
////                return middleNameField;
            case Relationships:
                return relationshipComboBox;
            case SSN:
                return taxIDField;
//            case AgeAsOfFirstDay:
//                return ageAsOfFirstDayField;
//            case Citizenship:
//                return citizenshipField;
//            case Email:
//                return emailField;
//            case Fax:
//                return faxNumberField;
//            case HomePhone:
//                return homePhoneField;
//            case LegallyBlind:
//                return legallyBlindComboBox;
//            case MobilePhone:
//                return mobilePhoneField;
//            case Occupation:
//                return occupationField;
//            case SocialNetworkId:
//                return weChatTextField;
//            case SocialNetworkType:
//                return null;
//            case WorkPhone:
//                return workPhoneField;
            default:
                return null;
        }
    }

    @Override
    protected void resetDataEntryStyleHelper() {
        educationCostField.setStyle(null);
        firstNameField.setStyle(null);
        genderComboBox.setStyle(null);
        lastNameField.setStyle(null);
        liveTogetherLengthComboBox.setStyle(null);
////        memoField.setStyle(null);
////        middleNameField.setStyle(null);
        relationshipComboBox.setStyle(null);
        taxIDField.setStyle(null);
////        ageAsOfFirstDayField.setStyle(null);
////        citizenshipField.setStyle(null);
////        emailField.setStyle(null);
////        faxNumberField.setStyle(null);
////        homePhoneField.setStyle(null);
////        legallyBlindComboBox.setStyle(null);
////        mobilePhoneField.setStyle(null);
////        occupationField.setStyle(null);
////        weChatTextField.setStyle(null);
////        workPhoneField.setStyle(null);
    }
    
    private void updateTaxpayerInfoFieldLabel(EntityField entityField, G02TaxpayerInfo legacyTaxpayerInfo){
        switch (entityField){
            case Relationships:
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
////            case MiddleName:
////                if (ZcaText.compareTextValueIgnoreCase(targetTaxpayerInfo.getMiddleName(),  legacyTaxpayerInfo.getMiddleName())){
////                    middleNameLabel.setText("Middle Name");
////                    middleNameLabel.setTextFill(PeonyCss.BLACK);
////                }else{
////                    middleNameLabel.setText("Middle Name: " + legacyTaxpayerInfo.getMiddleName());
////                    middleNameLabel.setTextFill(PeonyCss.DARKRED);
////                }
////                break;
            case Gender:
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
            case LengthOfLivingTogether:
                if (ZcaText.compareTextValueIgnoreCase(targetTaxpayerInfo.getLengthOfLivingTogether(),  legacyTaxpayerInfo.getLengthOfLivingTogether())){
                    liveTogetherLengthLabel.setText("Living Together");
                    liveTogetherLengthLabel.setTextFill(PeonyCss.BLACK);
                    liveTogetherLengthLabel.getStyleClass().remove("peony-success-small-label");
                }else{
                    liveTogetherLengthLabel.setText("Living Together: " + legacyTaxpayerInfo.getLengthOfLivingTogether());
                    liveTogetherLengthLabel.setTextFill(PeonyCss.WHITE);
                    liveTogetherLengthLabel.getStyleClass().add("peony-success-small-label");
                }
                break;
            case EducationCost:
                if (ZcaText.compareTextValueIgnoreCase(targetTaxpayerInfo.getEducationCost(),  legacyTaxpayerInfo.getEducationCost())){
                    educationCostLabel.setText("Education Cost");
                    educationCostLabel.setTextFill(PeonyCss.BLACK);
                    educationCostLabel.getStyleClass().remove("peony-success-small-label");
                }else{
                    educationCostLabel.setText("Education Cost: " + legacyTaxpayerInfo.getEducationCost());
                    educationCostLabel.setTextFill(PeonyCss.WHITE);
                    educationCostLabel.getStyleClass().add("peony-success-small-label");
                }
                break;
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
        updateTaxpayerInfoFieldLabel(EntityField.MiddleName, legacyTaxpayerInfo);
        updateTaxpayerInfoFieldLabel(EntityField.Gender, legacyTaxpayerInfo);
        updateTaxpayerInfoFieldLabel(EntityField.Birthday, legacyTaxpayerInfo);
        updateTaxpayerInfoFieldLabel(EntityField.LengthOfLivingTogether, legacyTaxpayerInfo);
        updateTaxpayerInfoFieldLabel(EntityField.EducationCost, legacyTaxpayerInfo);
        
    }
}
