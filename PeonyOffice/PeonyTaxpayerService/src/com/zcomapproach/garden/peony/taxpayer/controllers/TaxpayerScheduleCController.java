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

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.exception.EntityField;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.view.events.PersonalBusinessPropertyDeletedEvent;
import com.zcomapproach.garden.peony.view.events.PersonalBusinessPropertyDialogCloseRequest;
import com.zcomapproach.garden.peony.view.events.PersonalBusinessPropertySavedEvent;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.PersonalPropertyDialogCloseRequest;
import com.zcomapproach.garden.persistence.G02EntityValidator;
import com.zcomapproach.garden.persistence.entity.G02PersonalBusinessProperty;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerCase;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.garden.util.GardenData;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javax.swing.JOptionPane;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class TaxpayerScheduleCController extends PeonyTaxpayerServiceController{
    @FXML
    private JFXTextField businessPropertyNameField;
    @FXML
    private JFXTextField businessOwnershipField;
    @FXML
    private JFXTextField businessEinField;
    @FXML
    private JFXTextField businessAddressField;
    @FXML
    private JFXTextField businessDescriptionField;
    @FXML
    private JFXTextField grossReceiptsSalesField;
    @FXML
    private JFXTextField costOfGoodsSoldField;
    @FXML
    private JFXTextField otherIncomeField;
    @FXML
    private JFXTextField expenseAdvertisingField;
    @FXML
    private JFXTextField expenseOfficeField;
    @FXML
    private JFXTextField expenseCarAndTruckField;
    @FXML
    private JFXTextField expenseCommissionsField;
    @FXML
    private JFXTextField expenseRentLeaseField;
    @FXML
    private JFXTextField expenseInsuranceField;
    @FXML
    private JFXTextField expenseTravelMealsField;
    @FXML
    private JFXTextField expenseTelephoneField;
    @FXML
    private JFXTextField expenseCableInternetField;
    @FXML
    private JFXTextField expenseSuppliesField;
    @FXML
    private JFXTextField expenseRepairsField;
    @FXML
    private JFXTextField expenseProfServicesField;
    @FXML
    private JFXTextField expenseUtilitiesField;
    @FXML
    private JFXTextField expenseContractLaborField;
    @FXML
    private JFXTextField expenseOthersField;
    @FXML
    private JFXTextField memoField;
    /**
     * Functional FlowPane: save and delete buttons
     */
    @FXML
    private HBox functionalHBox;
    /**
     * Optional functional buttons
     */
    private JFXButton saveButton;
    private JFXButton cancelButton;
    private JFXButton deleteButton;
    private final boolean saveBtnRequired;
    private final boolean deleteBtnRequired;
    private final boolean cancelBtnRequired;
    
    private final G02PersonalBusinessProperty targetPersonalBusinessProperty;
    private final PeonyTaxpayerCase targetPeonyTaxpayerCase;
    
    private final Tab ownerTab;

    public TaxpayerScheduleCController(G02PersonalBusinessProperty personalBusinessProperty, 
                                        PeonyTaxpayerCase targetPeonyTaxpayerCase, 
                                        boolean saveBtnRequired, 
                                        boolean deleteBtnRequired, 
                                        boolean cancelBtnRequired,
                                        Tab ownerTab) 
    {
        super(targetPeonyTaxpayerCase);
        if (personalBusinessProperty == null){
            personalBusinessProperty = new G02PersonalBusinessProperty();
        }
        this.targetPersonalBusinessProperty = personalBusinessProperty;
        this.targetPeonyTaxpayerCase = targetPeonyTaxpayerCase;
        this.saveBtnRequired = saveBtnRequired;
        this.deleteBtnRequired = deleteBtnRequired;
        this.cancelBtnRequired = cancelBtnRequired;
        this.ownerTab = ownerTab;
    }

    /**
     * programmatically select the tab from the TabPane  
     * @return - possibly NULL if this controller does not support a Tab
     */
    public Tab getOwnerTab() {
        return ownerTab;
    }

    public boolean isSaveBtnRequired() {
        return saveBtnRequired;
    }

    public boolean isDeleteBtnRequired() {
        return deleteBtnRequired;
    }

    public boolean isCancelBtnRequired() {
        return cancelBtnRequired;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTaxpayerInfoScheduleCDataEntry();
    }
    private void initializeTaxpayerInfoScheduleCDataEntry() {
        PeonyFaceUtils.initializeTextField(businessPropertyNameField, targetPersonalBusinessProperty.getBusinessPropertyName(), 
                null, "Personal business name", this);
        PeonyFaceUtils.initializeTextField(businessOwnershipField, targetPersonalBusinessProperty.getBusinessOwnership(), 
                null, "Business owner", this);
        PeonyFaceUtils.initializeTextField(businessEinField, targetPersonalBusinessProperty.getBusinessEin(), 
                null, "EIN number", this);
        PeonyFaceUtils.initializeTextField(businessAddressField, targetPersonalBusinessProperty.getBusinessAddress(), 
                null, "Business address", this);
        PeonyFaceUtils.initializeTextField(businessDescriptionField, targetPersonalBusinessProperty.getBusinessDescription(), 
                null, "Business description", this);
        PeonyFaceUtils.initializeTextField(grossReceiptsSalesField, targetPersonalBusinessProperty.getGrossReceiptsSales(), 
                null, "Gross receipts sales", this);
        PeonyFaceUtils.initializeTextField(costOfGoodsSoldField, targetPersonalBusinessProperty.getCostOfGoodsSold(), 
                null, "Cost of goods sold", this);
        PeonyFaceUtils.initializeTextField(otherIncomeField, targetPersonalBusinessProperty.getOtherIncome(), 
                null, "Other income", this);
        PeonyFaceUtils.initializeTextField(expenseAdvertisingField, targetPersonalBusinessProperty.getExpenseAdvertising(), 
                null, "Expenses on advertising", this);
        PeonyFaceUtils.initializeTextField(expenseOfficeField, targetPersonalBusinessProperty.getExpenseOffice(), 
                null, "Expenses on office", this);
        PeonyFaceUtils.initializeTextField(expenseCarAndTruckField, targetPersonalBusinessProperty.getExpenseCarAndTruck(), 
                null, "Expenses on car and truck", this);
        PeonyFaceUtils.initializeTextField(expenseCommissionsField, targetPersonalBusinessProperty.getExpenseCommissions(), 
                null, "Expenses on commissions", this);
        PeonyFaceUtils.initializeTextField(expenseRentLeaseField, targetPersonalBusinessProperty.getExpenseRentLease(), 
                null, "Expenses on rent lease", this);
        PeonyFaceUtils.initializeTextField(expenseInsuranceField, targetPersonalBusinessProperty.getExpenseInsurance(), 
                null, "Expenses on insurance", this);
        PeonyFaceUtils.initializeTextField(expenseTravelMealsField, targetPersonalBusinessProperty.getExpenseTravelMeals(), 
                null, "Expenses on travel meals", this);
        PeonyFaceUtils.initializeTextField(expenseTelephoneField, targetPersonalBusinessProperty.getExpenseTelephone(), 
                null, "Expenses on telephone", this);
        PeonyFaceUtils.initializeTextField(expenseCableInternetField, targetPersonalBusinessProperty.getExpenseCableInternet(), 
                null, "Expenses on cable internet", this);
        PeonyFaceUtils.initializeTextField(expenseSuppliesField, targetPersonalBusinessProperty.getExpenseSupplies(), 
                null, "Expenses on supllies", this);
        PeonyFaceUtils.initializeTextField(expenseRepairsField, targetPersonalBusinessProperty.getExpenseRepairs(), 
                null, "Expenses on repairs", this);
        PeonyFaceUtils.initializeTextField(expenseProfServicesField, targetPersonalBusinessProperty.getExpenseProfServices(), 
                null, "Expenses on professional services", this);
        PeonyFaceUtils.initializeTextField(expenseUtilitiesField, targetPersonalBusinessProperty.getExpenseUtilities(), 
                null, "Expenses on utilities", this);
        PeonyFaceUtils.initializeTextField(expenseContractLaborField, targetPersonalBusinessProperty.getExpenseContractLabor(), 
                null, "Expenses on contarct labors", this);
        PeonyFaceUtils.initializeTextField(expenseOthersField, targetPersonalBusinessProperty.getExpenseOthers(), 
                null, "Expenses on others", this);
        PeonyFaceUtils.initializeTextField(memoField, targetPersonalBusinessProperty.getMemo(), null, "Memo", this);
        
        initializeSaveButton();
        initializeCancelButton();
        initializeDeleteButton();
        
    }
    
    private void initializeSaveButton(){
        if (saveButton == null){
            saveButton = new JFXButton("Save");
            saveButton.getStyleClass().add("peony-primary-small-button");
            saveButton.setOnAction((ActionEvent actionEvent) -> {
                getCachedThreadPoolExecutorService().submit(createSaveTaxpayerScheduleCTask());
            });
        }
        //display it or not...
        if (isSaveBtnRequired()){
            functionalHBox.getChildren().remove(saveButton);
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
                if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to close this dialog?") == JOptionPane.YES_OPTION){
                    broadcastPeonyFaceEventHappened(
                            new PersonalPropertyDialogCloseRequest(targetPersonalBusinessProperty.getPersonalBusinessPropertyUuid()));
                }
            });
        }
        //display it or not...
        if (isCancelBtnRequired()){
            functionalHBox.getChildren().remove(cancelButton);
            functionalHBox.getChildren().add(cancelButton);
        }else{
            functionalHBox.getChildren().remove(cancelButton);
        }
    }
    
    private void initializeDeleteButton(){
        if (deleteButton == null){
            deleteButton = new JFXButton("Delete");
            deleteButton.getStyleClass().add("peony-primary-small-button");
            deleteButton.setOnAction((ActionEvent actionEvent) -> {
                if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to delete this schedule-C form?") == JOptionPane.YES_OPTION){
                    getCachedThreadPoolExecutorService().submit(createDeleteTaxpayerScheduleCTask());
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
    
    private Task<Boolean> createDeleteTaxpayerScheduleCTask(){
        return new Task<Boolean>(){
            @Override
            protected Boolean call() throws Exception {
                try{
                    Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient().deleteEntity_XML(
                            G02PersonalBusinessProperty.class, GardenRestParams.Taxpayer.deletePersonalBusinessPropertyRestParams(targetPersonalBusinessProperty.getPersonalBusinessPropertyUuid()));
                    updateMessage("Successfully deleted this personal business property entry.");
                    return true;
                }catch (Exception ex){
                    updateMessage("Failed to deleted this personal business property entry.");
                    return false;
                }
            }

            @Override
            protected void succeeded() {
                //GUI...
                try {
                    if (get()){
                        broadcastPeonyFaceEventHappened(new PersonalBusinessPropertyDeletedEvent(targetPersonalBusinessProperty));
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
    
    private Task<G02PersonalBusinessProperty> createSaveTaxpayerScheduleCTask(){
        Task<G02PersonalBusinessProperty> saveTaxpayerInfoDataEntryTask = new Task<G02PersonalBusinessProperty>(){
            @Override
            protected G02PersonalBusinessProperty call() throws Exception {
                try {
                    loadTargetPersonalBusinessProperty();
                } catch (ZcaEntityValidationException ex) {
                    //Exceptions.printStackTrace(ex);
                    highlightBadEntityField(ex);
                    updateMessage(ex.getMessage());
                    return null;
                }
                try{
                    G02TaxpayerCase aG02TaxpayerCase;
                    if (ZcaValidator.isNullEmpty(targetPersonalBusinessProperty.getTaxpayerCaseUuid())){
                        aG02TaxpayerCase = null;
                    }else{
                        aG02TaxpayerCase = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient().findEntity_XML(G02TaxpayerCase.class, 
                                GardenRestParams.Taxpayer.findTaxpayerCaseByTaxpayerCaseUuidRestParams(targetPersonalBusinessProperty.getTaxpayerCaseUuid()));
                    }
                    if (aG02TaxpayerCase == null){
                        updateMessage("This personal business property entry is NOT saved. Please save Taxpayer case's basic information before save this data entry.");
                    }else{
                        G02PersonalBusinessProperty aG02PersonalBusinessProperty = Lookup.getDefault().lookup(PeonyTaxpayerService.class)
                                .getPeonyTaxpayerRestClient().storeEntity_XML(G02PersonalBusinessProperty.class, 
                                        GardenRestParams.Taxpayer.storePersonalBusinessPropertyRestParams(), targetPersonalBusinessProperty);
                        if (aG02PersonalBusinessProperty == null){
                            updateMessage("Failed to save this data entry.");
                            return null;
                        }else{
                            updateMessage("Successfully saved this data entry.");
                        }
                    }
                    return targetPersonalBusinessProperty;
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
                    G02PersonalBusinessProperty aG02PersonalBusinessProperty = get();
                    if (aG02PersonalBusinessProperty == null){
                        msgType = JOptionPane.ERROR_MESSAGE;
                    }else{
                        resetDataEntryStyle();
                        //saved taxpayer info data entry
                        broadcastPeonyFaceEventHappened(new PersonalBusinessPropertySavedEvent(aG02PersonalBusinessProperty));
                        //close the data entry dialog
                        broadcastPeonyFaceEventHappened(new PersonalBusinessPropertyDialogCloseRequest(aG02PersonalBusinessProperty.getPersonalBusinessPropertyUuid()));
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }

                //display result information dialog
                String msg = getMessage();
                if (ZcaValidator.isNotNullEmpty(msg)){
                    PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, msg, "Personal Business Property (Schedule-C)", msgType);
                }
            }
        };
        return saveTaxpayerInfoDataEntryTask;
    }

    protected void loadTargetPersonalBusinessProperty() throws ZcaEntityValidationException {
        targetPersonalBusinessProperty.setBusinessPropertyName(businessPropertyNameField.getText());
        targetPersonalBusinessProperty.setBusinessOwnership(businessOwnershipField.getText());
        targetPersonalBusinessProperty.setBusinessEin(businessEinField.getText());
        targetPersonalBusinessProperty.setBusinessAddress(businessAddressField.getText());
        targetPersonalBusinessProperty.setBusinessDescription(businessDescriptionField.getText());
        targetPersonalBusinessProperty.setGrossReceiptsSales(grossReceiptsSalesField.getText());
        targetPersonalBusinessProperty.setCostOfGoodsSold(costOfGoodsSoldField.getText());
        targetPersonalBusinessProperty.setOtherIncome(otherIncomeField.getText());
        targetPersonalBusinessProperty.setExpenseAdvertising(expenseAdvertisingField.getText());
        targetPersonalBusinessProperty.setExpenseOffice(expenseOfficeField.getText());
        targetPersonalBusinessProperty.setExpenseCarAndTruck(expenseCarAndTruckField.getText());
        targetPersonalBusinessProperty.setExpenseCommissions(expenseCommissionsField.getText());
        targetPersonalBusinessProperty.setExpenseRentLease(expenseRentLeaseField.getText());
        targetPersonalBusinessProperty.setExpenseInsurance(expenseInsuranceField.getText());
        targetPersonalBusinessProperty.setExpenseTravelMeals(expenseTravelMealsField.getText());
        targetPersonalBusinessProperty.setExpenseTelephone(expenseTelephoneField.getText());
        targetPersonalBusinessProperty.setExpenseCableInternet(expenseCableInternetField.getText());
        targetPersonalBusinessProperty.setExpenseSupplies(expenseSuppliesField.getText());
        targetPersonalBusinessProperty.setExpenseRepairs(expenseRepairsField.getText());
        targetPersonalBusinessProperty.setExpenseProfServices(expenseProfServicesField.getText());
        targetPersonalBusinessProperty.setExpenseUtilities(expenseUtilitiesField.getText());
        targetPersonalBusinessProperty.setExpenseContractLabor(expenseContractLaborField.getText());
        targetPersonalBusinessProperty.setExpenseOthers(expenseOthersField.getText());
        targetPersonalBusinessProperty.setMemo(memoField.getText());
        if (ZcaValidator.isNullEmpty(targetPersonalBusinessProperty.getPersonalBusinessPropertyUuid())){
            targetPersonalBusinessProperty.setPersonalBusinessPropertyUuid(GardenData.generateUUIDString());
        }
        targetPersonalBusinessProperty.setTaxpayerCaseUuid(targetPeonyTaxpayerCase.getTaxpayerCase().getTaxpayerCaseUuid());
        
        try{
            G02EntityValidator.getSingleton().validate(targetPersonalBusinessProperty);
        }catch(ZcaEntityValidationException ex){
            highlightBadEntityField(ex);
            throw ex;
        }
    }
    
    @Override
    protected Node getBadEntityField(EntityField entityField) {
        switch (entityField){
            case BusinessAddress:
                return businessAddressField;
            case BusinessDescription:
                return businessDescriptionField;
            case BusinessEin:
                return businessEinField;
            case BusinessOwnership:
                return businessOwnershipField;
            case BusinessPropertyName:
                return businessPropertyNameField;
            case CostOfGoodsSold:
                return costOfGoodsSoldField;
            case EntityStatus:
                return null;
            case ExpenseAdvertising:
                return expenseAdvertisingField;
            case ExpenseCableInternet:
                return expenseCableInternetField;
            case ExpenseCarAndTruck:
                return expenseCarAndTruckField;
            case ExpenseCommissions:
                return expenseCommissionsField;
            case ExpenseContractLabor:
                return expenseContractLaborField;
            case ExpenseInsurance:
                return expenseInsuranceField;
            case ExpenseOffice:
                return expenseOfficeField;
            case ExpenseOthers:
                return expenseOthersField;
            case ExpenseProfServices:
                return expenseProfServicesField;
            case ExpenseRentLease:
                return expenseRentLeaseField;
            case ExpenseRepairs:
                return expenseRepairsField;
            case ExpenseSupplies:
                return expenseSuppliesField;
            case ExpenseTelephone:
                return expenseTelephoneField;
            case ExpenseTravelMeals:
                return expenseTravelMealsField;
            case ExpenseUtilities:
                return expenseUtilitiesField;
            case GrossReceiptsSales:
                return grossReceiptsSalesField;
            case OtherIncome:
                return otherIncomeField;
            case Memo:
                return memoField;
            default:
                return null;
        }
    }
    
    @Override
    protected void resetDataEntryStyleHelper() {
        businessAddressField.setStyle(null);
        businessDescriptionField.setStyle(null);
        businessEinField.setStyle(null);
        businessOwnershipField.setStyle(null);
        businessPropertyNameField.setStyle(null);
        costOfGoodsSoldField.setStyle(null);
        expenseAdvertisingField.setStyle(null);
        expenseCableInternetField.setStyle(null);
        expenseCarAndTruckField.setStyle(null);
        expenseCommissionsField.setStyle(null);
        expenseContractLaborField.setStyle(null);
        expenseInsuranceField.setStyle(null);
        expenseOfficeField.setStyle(null);
        expenseOthersField.setStyle(null);
        expenseProfServicesField.setStyle(null);
        expenseRentLeaseField.setStyle(null);
        expenseRepairsField.setStyle(null);
        expenseSuppliesField.setStyle(null);
        expenseTelephoneField.setStyle(null);
        expenseTravelMealsField.setStyle(null);
        expenseUtilitiesField.setStyle(null);
        grossReceiptsSalesField.setStyle(null);
        otherIncomeField.setStyle(null);
        memoField.setStyle(null);
    }
}
