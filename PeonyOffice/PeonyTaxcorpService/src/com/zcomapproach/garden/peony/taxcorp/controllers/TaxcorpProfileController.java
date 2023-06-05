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

package com.zcomapproach.garden.peony.taxcorp.controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.data.constant.GardenAgreement;
import com.zcomapproach.garden.data.constant.UState;
import com.zcomapproach.garden.exception.EntityField;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.guard.RoseWebCipher;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxcorpService;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.security.PeonyPrivilege;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyDataUtils;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.persistence.G02EntityValidator;
import com.zcomapproach.garden.persistence.constant.BusinessContactorRole;
import com.zcomapproach.garden.persistence.constant.GardenAccountStatus;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.constant.TaxcorpBusinessStatus;
import com.zcomapproach.garden.persistence.constant.TaxcorpBusinessType;
import com.zcomapproach.garden.persistence.entity.G02Account;
import com.zcomapproach.garden.persistence.entity.G02BusinessContactor;
import com.zcomapproach.garden.persistence.entity.G02Log;
import com.zcomapproach.garden.persistence.entity.G02TaxcorpCase;
import com.zcomapproach.garden.persistence.entity.G02User;
import com.zcomapproach.garden.persistence.peony.PeonyAccount;
import com.zcomapproach.garden.persistence.peony.PeonyTaxcorpCase;
import com.zcomapproach.garden.persistence.peony.data.PeonyLogName;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaText;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.util.GardenData;
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
import javax.swing.JOptionPane;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 * Display taxcorp profile information
 * @author zhijun98
 */
public class TaxcorpProfileController extends PeonyTaxcorpServiceController {
    @FXML
    private Label taxcorpStatusLabel;
    @FXML
    private JFXTextField einField;
    @FXML
    private JFXTextField corporateNameField;
    @FXML
    private JFXComboBox<String> businessTypeComboBox;
    @FXML
    private DatePicker dosDatePicker;
    @FXML
    private JFXTextField businessPurposeField;
    @FXML
    private JFXTextField bankRoutingNumberField;
    @FXML
    private JFXTextField bankAccountNumberField;
    @FXML
    private JFXTextField taxcorpMemoField;
    @FXML
    private JFXTextField corporateEmailField;
    @FXML
    private JFXTextField corporatePhoneField;
    @FXML
    private JFXTextField corporateFaxField;
    @FXML
    private JFXTextField corporateWebPresenceField;
    @FXML
    private JFXTextField taxcorpAddressField;
    @FXML
    private JFXTextField taxcorpCityField;
    @FXML
    private JFXTextField taxcorpStateCountyField;
    @FXML
    private JFXComboBox<String> taxcorpStateComboBox;
    @FXML
    private JFXTextField taxcorpZipField;
    @FXML
    private JFXTextField taxcorpCountryField;
    @FXML
    private JFXTextField taxcorpDbaField;
    @FXML
    private Button saveTaxcorpButton;
    @FXML
    private DatePicker finalizeDatePicker;
    @FXML
    private Button finalizeTaxcorpButton;
    @FXML
    private Button rollbackFinalizationButton;

    private final PeonyTaxcorpCase targetPeonyTaxcorpCase;

    public TaxcorpProfileController(PeonyTaxcorpCase targetPeonyTaxcorpCase) {
        super(targetPeonyTaxcorpCase);
        this.targetPeonyTaxcorpCase = targetPeonyTaxcorpCase;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTaxcorpProfileFields();
        initializeFunctionalButtons();
    }

    private void initializeTaxcorpProfileFields() {
        G02TaxcorpCase aG02TaxcorpCase = targetPeonyTaxcorpCase.getTaxcorpCase();
        //corporateNameField
        PeonyFaceUtils.initializeTextField(corporateNameField, aG02TaxcorpCase.getCorporateName(), null, "Corporate name", this);
        //einField
        PeonyFaceUtils.initializeTextField(einField, aG02TaxcorpCase.getEinNumber(), null, "Corporate EIN number", this);
        //businessTypeComboBox
        PeonyFaceUtils.initializeComboBox(businessTypeComboBox, TaxcorpBusinessType.getEnumValueList(false), 
                                          null, aG02TaxcorpCase.getBusinessType(), "Taxcorp type", this);
        //dosDatePicker
        PeonyFaceUtils.initializeDatePicker(dosDatePicker, ZcaCalendar.convertToLocalDate(aG02TaxcorpCase.getDosDate()), 
                ZcaCalendar.convertToLocalDate(new Date()), "The date when this corporate was set up in the official documentation.", this);
        //businessPurposeField
        PeonyFaceUtils.initializeTextField(businessPurposeField, aG02TaxcorpCase.getBusinessPurpose(), 
                null, "Describe what this business does...", this);
        //bankRoutingNumberField
        PeonyFaceUtils.initializeTextField(bankRoutingNumberField, aG02TaxcorpCase.getBankRoutingNumber(), 
                null, "Bank routing number", this);
        //bankAccountNumberField
        PeonyFaceUtils.initializeTextField(bankAccountNumberField, aG02TaxcorpCase.getBankAccountNumber(), 
                null, "Bank account number", this);
        //taxcorpMemoField
        PeonyFaceUtils.initializeTextField(taxcorpMemoField, aG02TaxcorpCase.getMemo(), 
                null, "Memo: " + ZcaText.denullize(aG02TaxcorpCase.getMemo(), "N/A"), this);
        //taxcorpDbaField
        PeonyFaceUtils.initializeTextField(taxcorpDbaField, aG02TaxcorpCase.getDba(), 
                null, "DBA: " + ZcaText.denullize(aG02TaxcorpCase.getDba(), "N/A"), this);
        //corporateEmailField
        PeonyFaceUtils.initializeTextField(corporateEmailField, aG02TaxcorpCase.getCorporateEmail(), 
                null, "This corporate's official email", this);
        //corporatePhoneField
        PeonyFaceUtils.initializeTextField(corporatePhoneField, aG02TaxcorpCase.getCorporatePhone(), 
                null, "This corporate's official phone", this);
        //corporateFaxField
        PeonyFaceUtils.initializeTextField(corporateFaxField, aG02TaxcorpCase.getCorporateFax(), 
                null, "This corporate's official fax", this);
        //corporateWebPresenceField
        PeonyFaceUtils.initializeTextField(corporateWebPresenceField, aG02TaxcorpCase.getCorporateWebPresence(), 
                null, "This corporate's official web presence, e.g. web-site address.", this);
        //taxcorpAddressField
        PeonyFaceUtils.initializeTextField(taxcorpAddressField, aG02TaxcorpCase.getTaxcorpAddress(), 
                null, "Official address of this corporate.", this);
        //taxcorpCityField
        PeonyFaceUtils.initializeTextField(taxcorpCityField, aG02TaxcorpCase.getTaxcorpCity(), 
                null, "City of the official address of this corporate.", this);
        //taxcorpStateCountyField
        PeonyFaceUtils.initializeTextField(taxcorpStateCountyField, aG02TaxcorpCase.getTaxcorpStateCounty(), 
                null, "State-county of the official address of this corporate.", this);
        //taxcorpStateComboBox
        PeonyFaceUtils.initializeComboBox(taxcorpStateComboBox, UState.getEnumValueList(false), 
                aG02TaxcorpCase.getTaxcorpState(), UState.NY.value(), "State of the official address of this corporate.", this);
        //taxcorpZipField
        PeonyFaceUtils.initializeTextField(taxcorpZipField, aG02TaxcorpCase.getTaxcorpZip(), 
                null, "Zip code of the official address of this corporate.", this);
        //taxcorpCountryField
        PeonyFaceUtils.initializeTextField(taxcorpCountryField, aG02TaxcorpCase.getTaxcorpCountry(), 
                null, "Nationality of the official address of this corporate.", this);
    }

    private void initializeFunctionalButtons() {
        
        //saveTaxcorpButton
        saveTaxcorpButton.setGraphic(PeonyGraphic.getImageView("database_save.png"));
        saveTaxcorpButton.setOnAction((ActionEvent event) -> {
            storeTargetTaxcorpCase();
        });
        
        //finalizeDatePicker
        PeonyFaceUtils.initializeDatePicker(finalizeDatePicker, null, null, "The date when this taxcorp finalized.", false, this);
        //finalizeTaxcorpButton
        finalizeTaxcorpButton.setGraphic(PeonyGraphic.getImageView("database_delete.png"));
        finalizeTaxcorpButton.setOnAction((ActionEvent event) -> {
            Date finalizeDate = ZcaCalendar.convertToDate(finalizeDatePicker.getValue());
            if (finalizeDate == null){
                PeonyFaceUtils.displayErrorMessageDialog("Please select the date when this taxcorp was finalized.");
                return;
            }
            finalizeTaxcorpDataEntry(finalizeDate);
        });
        
        rollbackFinalizationButton.setGraphic(PeonyGraphic.getImageView("restore.png"));
        rollbackFinalizationButton.setOnAction((ActionEvent event) -> {
            rollbackFinalizedDataEntry();
        });
        
        prepareGuiFinalizedTaxcorpDataEntry();
    }

    private void rollbackFinalizedDataEntry() {
        if (!PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.ROLLBACK_TAXCORP)){
            PeonyFaceUtils.displayPrivilegeErrorMessageDialog();
            return;
        }
        Task<G02TaxcorpCase> rollbackFinalizedDataEntryTask = new Task<G02TaxcorpCase>(){
            @Override
            protected G02TaxcorpCase call() throws Exception {
                try{
                    G02TaxcorpCase result = Lookup.getDefault().lookup(PeonyTaxcorpService.class).getPeonyTaxcorpRestClient()
                            .deleteEntity_XML(G02TaxcorpCase.class, 
                                    GardenRestParams.Taxcorp.rollbackFinalizedTaxcorpCaseRestParams(
                                            targetPeonyTaxcorpCase.getTaxcorpCase().getTaxcorpCaseUuid()));
                    if (result == null){
                        updateMessage("Failed to rolled back this case for technical reasons.");
                    }else{
                        updateMessage("This finalized taxcorp case is rolled back successfully.");
                    }
                    return result;
                }catch(Exception ex){
                    updateMessage("Finalizing this taxcorp failed. " + ex.getMessage());
                    return targetPeonyTaxcorpCase.getTaxcorpCase();
                }
            }

            @Override
            protected void succeeded() {
                int msgType = JOptionPane.INFORMATION_MESSAGE;
                String msg = getMessage();
                try {
                    G02TaxcorpCase result = get();
                    if (result == null){
                        msgType = JOptionPane.ERROR_MESSAGE;
                    }else{
                        targetPeonyTaxcorpCase.getTaxcorpCase().setBusinessStatus(TaxcorpBusinessStatus.ACTIVE.value());
                        prepareGuiFinalizedTaxcorpDataEntry();
                        
                        G02Log log = createNewG02LogInstance(PeonyLogName.ROLLBACK_FINALIZED_TAXCORP_CASE);
                        log.setLoggedEntityType(GardenEntityType.TAXCORP_CASE.name());
                        log.setLoggedEntityUuid(targetPeonyTaxcorpCase.getTaxcorpCase().getTaxcorpCaseUuid());
                        PeonyProperties.getSingleton().log(log);
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                //display information dialog
                if (ZcaValidator.isNotNullEmpty(msg)){
                    PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, msg, "Delete Taxcorp", msgType);
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(rollbackFinalizedDataEntryTask);
    }

    private void finalizeTaxcorpDataEntry(final Date finalizeDate) {
        if (!PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.FINALIZE_TAXCORP)){
            PeonyFaceUtils.displayPrivilegeErrorMessageDialog();
            return;
        }
        if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, 
                "Are you sure to finalize this taxcorp on "+ZcaCalendar.convertToMMddyyyy(finalizeDate, "-")+"?")
                != JOptionPane.YES_OPTION)
        {
            return;
        }
        Task<G02TaxcorpCase> deleteTaxcorpDataEntryTask = new Task<G02TaxcorpCase>(){
            @Override
            protected G02TaxcorpCase call() throws Exception {
                try{
                    G02TaxcorpCase result = Lookup.getDefault().lookup(PeonyTaxcorpService.class).getPeonyTaxcorpRestClient()
                            .deleteEntity_XML(G02TaxcorpCase.class, 
                                    GardenRestParams.Taxcorp.finalizeTaxcorpCaseRestParams(
                                            targetPeonyTaxcorpCase.getTaxcorpCase().getTaxcorpCaseUuid(), finalizeDate));
                    if (result == null){
                        updateMessage("Finalizing this taxcorp is successful.");
                    }else{
                        updateMessage("Finalizing this taxcorp failed.");
                    }
                    return result;
                }catch(Exception ex){
                    updateMessage("Finalizing this taxcorp failed. " + ex.getMessage());
                    return targetPeonyTaxcorpCase.getTaxcorpCase();
                }
            }

            @Override
            protected void succeeded() {
                int msgType = JOptionPane.INFORMATION_MESSAGE;
                String msg = getMessage();
                try {
                    G02TaxcorpCase result = get();
                    if (result == null){
                        targetPeonyTaxcorpCase.getTaxcorpCase().setBusinessStatus(TaxcorpBusinessStatus.FINALIZED.value());
                        prepareGuiFinalizedTaxcorpDataEntry();
                        
                        G02Log log = createNewG02LogInstance(PeonyLogName.DELETED_TAXCORP_CASE);
                        log.setLoggedEntityType(GardenEntityType.TAXCORP_CASE.name());
                        log.setLoggedEntityUuid(targetPeonyTaxcorpCase.getTaxcorpCase().getTaxcorpCaseUuid());
                        PeonyProperties.getSingleton().log(log);
                        
                    }else{
                        msgType = JOptionPane.ERROR_MESSAGE;
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                //display information dialog
                if (ZcaValidator.isNotNullEmpty(msg)){
                    PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, msg, "Delete Taxcorp", msgType);
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(deleteTaxcorpDataEntryTask);
    }
    /**
     * Decorate for finalized cases
     */
    private void prepareGuiFinalizedTaxcorpDataEntry() {
        if (Platform.isFxApplicationThread()){
            prepareGuiFinalizedTaxcorpDataEntryHelper();
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    prepareGuiFinalizedTaxcorpDataEntryHelper();
                }
            });
        }
    }
    
    private void prepareGuiFinalizedTaxcorpDataEntryHelper(){
        if (isFinalizedCase()){
            saveTaxcorpButton.setDisable(true);
            finalizeTaxcorpButton.setDisable(true);
            finalizeDatePicker.setDisable(true);
            finalizeDatePicker.setValue(ZcaCalendar.convertToLocalDate(targetPeonyTaxcorpCase.getTaxcorpCase().getUpdated()));
            taxcorpStatusLabel.setText("Finalized");
            taxcorpStatusLabel.setVisible(true);
            rollbackFinalizationButton.setVisible(true);
        }else{
            saveTaxcorpButton.setDisable(false);
            finalizeTaxcorpButton.setDisable(false);
            finalizeDatePicker.setDisable(false);
            finalizeDatePicker.setValue(ZcaCalendar.convertToLocalDate(new Date()));
            taxcorpStatusLabel.setVisible(false);
            rollbackFinalizationButton.setVisible(false);
        }
    }

    private boolean isFinalizedCase() {
        if (targetPeonyTaxcorpCase.getTaxcorpCase() == null){
            return false;
        }
        return TaxcorpBusinessStatus.FINALIZED.value().equalsIgnoreCase(targetPeonyTaxcorpCase.getTaxcorpCase().getBusinessStatus());
    }
    
    @Override
    protected Node getBadEntityField(EntityField entityFiled) {
        switch (entityFiled){
//            case AgreementSignature:
//            case AgreementUuid:
            case BusinessPurpose:
                return businessPurposeField;
//            case BusinessStatus:
            case BusinessType:
                return businessTypeComboBox;
            case BankAccountNumber:
                return bankAccountNumberField;
            case BankRoutingNumber:
                return bankRoutingNumberField;
            case Memo:
                return taxcorpMemoField;
            case Dba:
                return taxcorpDbaField;
            case CorporateName:
                return corporateNameField;
            case CorporateEmail:
                return corporateEmailField;
            case CorporateFax:
                return corporateFaxField;
            case CorporatePhone:
                return corporatePhoneField;
            case CorporateWebPresence:
                return corporateWebPresenceField;
//            case CustomerAccountUuid:
            case EinNumber:
                return einField;
            case TaxcorpCountry:
                return taxcorpCountryField;
            case TaxcorpAddress:
                return taxcorpAddressField;
            case TaxcorpCity:
                return taxcorpCityField;
            case TaxcorpStateCounty:
                return taxcorpStateCountyField;
            case TaxcorpState:
                return taxcorpStateComboBox;
            case TaxcorpZip:
                return taxcorpZipField;
//            case Memo:
//            case LatestLogUuid:
//            case EntityStatus:
            default:
                return null;
        }
    }
    
    @Override
    protected void resetDataEntryStyleHelper(){
        businessPurposeField.setStyle(null);
        businessTypeComboBox.setStyle(null);
        bankAccountNumberField.setStyle(null);
        bankRoutingNumberField.setStyle(null);
        taxcorpMemoField.setStyle(null);
        taxcorpDbaField.setStyle(null);
        corporateNameField.setStyle(null);
        corporateEmailField.setStyle(null);
        corporateFaxField.setStyle(null);
        corporatePhoneField.setStyle(null);
        corporateWebPresenceField.setStyle(null);
        einField.setStyle(null);
        taxcorpCountryField.setStyle(null);
        taxcorpAddressField.setStyle(null);
        taxcorpCityField.setStyle(null);
        taxcorpStateCountyField.setStyle(null);
        taxcorpStateComboBox.setStyle(null);
        taxcorpZipField.setStyle(null);
    }

    public void loadTargetPeonyTaxcorpCase() throws ZcaEntityValidationException{
        G02TaxcorpCase aG02TaxcorpCase = targetPeonyTaxcorpCase.getTaxcorpCase();
        //corporateNameField;
        aG02TaxcorpCase.setCorporateName(corporateNameField.getText());
        //einField
        if (ZcaValidator.isNotNullEmpty(einField.getText())){
            if (PeonyDataUtils.isEinFormat(einField.getText())){
                aG02TaxcorpCase.setEinNumber(einField.getText());
            }else{
                throw new ZcaEntityValidationException("Please strictly follow the corporate's EIN number format, e.g. 01-2345678");
            }
        }
        //businessTypeComboBox;
        aG02TaxcorpCase.setBusinessType(businessTypeComboBox.getSelectionModel().getSelectedItem());
        //dosDatePicker;
        aG02TaxcorpCase.setDosDate(ZcaCalendar.convertToDate(dosDatePicker.valueProperty().getValue()));
        //businessPurposeField;
        aG02TaxcorpCase.setBusinessPurpose(businessPurposeField.getText());
        //bankRoutingNumberField;
        aG02TaxcorpCase.setBankRoutingNumber(bankRoutingNumberField.getText());
        //bankAccountNumberField;
        aG02TaxcorpCase.setBankAccountNumber(bankAccountNumberField.getText());
        //taxcorpMemoField
        aG02TaxcorpCase.setMemo(ZcaText.denullize(taxcorpMemoField.getText(), "N/A"));
        //taxcorpDbaField
        aG02TaxcorpCase.setDba(ZcaText.denullize(taxcorpDbaField.getText(), "N/A"));
        //corporateEmailField;
        aG02TaxcorpCase.setCorporateEmail(corporateEmailField.getText());
        //corporatePhoneField;
        aG02TaxcorpCase.setCorporatePhone(corporatePhoneField.getText());
        //corporateFaxField;
        aG02TaxcorpCase.setCorporateFax(corporateFaxField.getText());
        //corporateWebPresenceField;
        aG02TaxcorpCase.setCorporateWebPresence(corporateWebPresenceField.getText());
        //taxcorpAddressField;
        aG02TaxcorpCase.setTaxcorpAddress(taxcorpAddressField.getText());
        //taxcorpCityField;
        aG02TaxcorpCase.setTaxcorpCity(taxcorpCityField.getText());
        //taxcorpStateCountyField;
        aG02TaxcorpCase.setTaxcorpStateCounty(taxcorpStateCountyField.getText());
        //taxcorpStateComboBox;
        aG02TaxcorpCase.setTaxcorpState(taxcorpStateComboBox.getSelectionModel().getSelectedItem());
        //taxcorpZipField;
        aG02TaxcorpCase.setTaxcorpZip(taxcorpZipField.getText());
        //taxcorpCountryField;
        aG02TaxcorpCase.setTaxcorpCountry(taxcorpCountryField.getText());
        
        aG02TaxcorpCase.setAgreementUuid(GardenAgreement.TaxcorpCaseAgreement.value());
        
        if (ZcaValidator.isNullEmpty(aG02TaxcorpCase.getTaxcorpCaseUuid())){
            aG02TaxcorpCase.setTaxcorpCaseUuid(ZcaUtils.generateUUIDString());
        }
        aG02TaxcorpCase.setEinNumber(targetPeonyTaxcorpCase.getTaxcorpCase().getEinNumber());
        
        try{
            G02EntityValidator.getSingleton().validate(targetPeonyTaxcorpCase.getTaxcorpCase());
        }catch(ZcaEntityValidationException ex){
            highlightBadEntityField(ex);
            throw ex;
        }
    }

    private void storeTargetTaxcorpCase() {
        Task<Boolean> storeTargetTaxcorpTask = new Task<Boolean>(){
            @Override
            protected Boolean call() throws Exception {
                //(1) load and validate targetTaxcorpCase...
                loadTargetPeonyTaxcorpCase();
                //(2) validation for saving...
                validateTargetPeonyTaxcorpCase();
                //(3) save targetTaxcorpCase...
                saveTargetPeonyTaxcorpCase();
                
                return true;
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayErrorMessageDialog("Technical error was raised. " + getMessage());
            }

            @Override
            protected void succeeded() {
                try {
                    get();
                    
                    resetDataEntryStyle();
                    
                    publishPeonyDataEntrySaveDemandingStatus(false);
                    
                    PeonyFaceUtils.displayInformationMessageDialog("Successfully saved this taxcorp case.");
                    
                } catch (InterruptedException | ExecutionException ex) {
                    //Exceptions.printStackTrace(ex);
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(storeTargetTaxcorpTask);
    }

        private void validateTargetPeonyTaxcorpCase() throws Exception {
            String taxcorpCaseUuid = targetPeonyTaxcorpCase.getTaxcorpCase().getTaxcorpCaseUuid();
            //validate the parameter
            List<G02BusinessContactor> aG02BusinessContactorList = targetPeonyTaxcorpCase.getBusinessContactorList();
            if (aG02BusinessContactorList == null){
                targetPeonyTaxcorpCase.setBusinessContactorList(new ArrayList<>());
            }
            /**
             * (1) hook contactor with taxcorp case; 
             * (2) validate taxcorp owner's SSN;
             * (3) make sure customer ready
             */
            for (G02BusinessContactor aG02BusinessContactor : aG02BusinessContactorList){
                aG02BusinessContactor.setEntityUuid(taxcorpCaseUuid);
                if (BusinessContactorRole.TAXCORP_OWNER.value().equalsIgnoreCase(aG02BusinessContactor.getRole())){
                    prepareCustomerForTarget(aG02BusinessContactor);
                }
                G02EntityValidator.getSingleton().validate(aG02BusinessContactor);
            }//for
            G02EntityValidator.getSingleton().validate(targetPeonyTaxcorpCase.getCustomer());
            G02EntityValidator.getSingleton().validate(targetPeonyTaxcorpCase.getTaxcorpCase());
        }

    private void prepareCustomerForTarget(G02BusinessContactor taxcorpOwner) throws Exception{
        PeonyAccount customer = targetPeonyTaxcorpCase.getCustomer();
        if ((customer == null) || (customer.getAccount() == null) 
                || (ZcaValidator.isNullEmpty(customer.getAccount().getAccountUuid()))
                || (ZcaValidator.isNullEmpty(customer.getUser().getUserUuid()))
                || (ZcaValidator.isNullEmpty(customer.getUser().getFirstName()))
                || (ZcaValidator.isNullEmpty(customer.getUser().getLastName())))
        {
            //prepare Customer from contactor(i.e. taxcorpOwner);
            customer = Lookup.getDefault().lookup(PeonyManagementService.class).getPeonyManagementRestClient()
                    .findEntity_XML(PeonyAccount.class, 
                                    GardenRestParams.Management.findPeonyUserBySsnRestParams(taxcorpOwner.getSsn()));
            if ((customer == null) || (customer.getAccount() == null) 
                    || (ZcaValidator.isNullEmpty(customer.getAccount().getAccountUuid())))
            {
                customer = new PeonyAccount();
                G02Account account = new G02Account();
                account.setAccountUuid(ZcaUtils.generateUUIDString());
                account.setLoginName(ZcaUtils.generateUUIDString());
                account.setEncryptedPassword(RoseWebCipher.getSingleton().encrypt(GardenData.generateRandomSecretCode(8)));
                account.setAccountStatus(GardenAccountStatus.Valid.name());
                G02User user =new G02User();
                user.setUserUuid(account.getAccountUuid());
                user.setFirstName(taxcorpOwner.getFirstName());
                user.setLastName(taxcorpOwner.getLastName());
                user.setSsn(taxcorpOwner.getSsn());
                user.setBirthday(taxcorpOwner.getBirthday());
                customer.setAccount(account);
                customer.setUser(user);
            }
            targetPeonyTaxcorpCase.setCustomer(customer);
            targetPeonyTaxcorpCase.getTaxcorpCase().setCustomerAccountUuid(customer.getAccount().getAccountUuid());
        }
    }

    private void saveTargetPeonyTaxcorpCase() throws Exception{
        PeonyTaxcorpCase result = Lookup.getDefault().lookup(PeonyTaxcorpService.class).getPeonyTaxcorpRestClient()
            .storeEntity_XML(PeonyTaxcorpCase.class, 
                            GardenRestParams.Taxcorp.storePeonyTaxcorpCaseBasicInformationAndPersonnelRestParams(),
                            targetPeonyTaxcorpCase);
        if (result == null){
            throw new Exception("Failed to save this taxcorp due to technocal problem.");
        }
        try{
            G02Log log = createNewG02LogInstance(PeonyLogName.STORED_TAXCORP_CASE);
            log.setLoggedEntityType(GardenEntityType.TAXCORP_CASE.name());
            log.setLoggedEntityUuid(targetPeonyTaxcorpCase.getTaxcorpCase().getTaxcorpCaseUuid());
            PeonyProperties.getSingleton().log(log);
        }catch (Exception ex){
            PeonyFaceUtils.publishMessageOntoOutputWindow("[WARNING] After successfully saved the taxcorp, the server-side system did not successfully save the log.");
        }
    }
}
