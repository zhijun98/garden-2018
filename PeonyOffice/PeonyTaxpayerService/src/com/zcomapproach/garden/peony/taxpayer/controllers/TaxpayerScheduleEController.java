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
import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.exception.EntityField;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.peony.view.events.PersonalPropertyDeletedEvent;
import com.zcomapproach.garden.peony.view.events.PersonalPropertyDialogCloseRequest;
import com.zcomapproach.garden.peony.view.events.PersonalPropertySavedEvent;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.persistence.G02EntityValidator;
import com.zcomapproach.garden.persistence.entity.G02PersonalProperty;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerCase;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.garden.peony.PeonyLauncher;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javax.swing.JOptionPane;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class TaxpayerScheduleEController extends PeonyTaxpayerServiceController{
    @FXML
    private JFXTextField propertyTypeField;
    @FXML
    private JFXTextField propertyAddressField;
    @FXML
    private JFXTextField purchasePriceField;
    @FXML
    private JFXTextField percentageOfOwnershipField;
    @FXML
    private JFXTextField percentageOfRentalUseField;
    @FXML
    private DatePicker dateOnServicePicker;
    @FXML
    private JFXTextField incomeRentsReceievedField;
    @FXML
    private DatePicker dateOnImprovementPicker;
    @FXML
    private JFXTextField improvementCostField;
    @FXML
    private JFXTextField expenseAdvertisingField;
    @FXML
    private JFXTextField expenseAutoAndTravelField;
    @FXML
    private JFXTextField expenseCleaningField;
    @FXML
    private JFXTextField expenseCommissionsField;
    @FXML
    private JFXTextField expenseDepreciationField;
    @FXML
    private JFXTextField expenseInsuranceField;
    @FXML
    private JFXTextField expenseMgtFeeField;
    @FXML
    private JFXTextField expenseReTaxesField;
    @FXML
    private JFXTextField expenseMorgageInterestField;
    @FXML
    private JFXTextField expenseSuppliesField;
    @FXML
    private JFXTextField expenseRepairsField;
    @FXML
    private JFXTextField expenseProfServicesField;
    @FXML
    private JFXTextField expenseUtilitiesField;
    @FXML
    private JFXTextField expenseWaterSewerField;
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
    
    private final G02PersonalProperty targetPersonalProperty;
    private final PeonyTaxpayerCase targetPeonyTaxpayerCase;
    
    private final Tab ownerTab;

    public TaxpayerScheduleEController(G02PersonalProperty targetPersonalProperty, 
                                        PeonyTaxpayerCase targetPeonyTaxpayerCase, 
                                        boolean saveBtnRequired, 
                                        boolean deleteBtnRequired, 
                                        boolean cancelBtnRequired,
                                        Tab ownerTab) 
    {
        super(targetPeonyTaxpayerCase);
        if (targetPersonalProperty == null){
            targetPersonalProperty = new G02PersonalProperty();
        }
        this.targetPersonalProperty = targetPersonalProperty;
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
        initializeTaxpayerInfoScheduleE();
    }
    private void initializeTaxpayerInfoScheduleE() {
        PeonyFaceUtils.initializeTextField(propertyTypeField, targetPersonalProperty.getPropertyType(), 
                null, "Personal property type", this);
        PeonyFaceUtils.initializeTextField(propertyAddressField, targetPersonalProperty.getPropertyAddress(), 
                null, "Property address", this);
        PeonyFaceUtils.initializeTextField(purchasePriceField, targetPersonalProperty.getPurchasePrice(), 
                null, "Purchase price of property", this);
        PeonyFaceUtils.initializeTextField(percentageOfOwnershipField, targetPersonalProperty.getPercentageOfOwnership(), 
                null, "Percentage of ownership", this);
        PeonyFaceUtils.initializeTextField(percentageOfRentalUseField, targetPersonalProperty.getPercentageOfRentalUse(), 
                null, "Percentage of rental use", this);
        PeonyFaceUtils.initializeDatePicker(dateOnServicePicker, ZcaCalendar.convertToLocalDate(targetPersonalProperty.getDateOnService()), 
                null, "Date on service", this);
        PeonyFaceUtils.initializeTextField(incomeRentsReceievedField, targetPersonalProperty.getIncomeRentsReceieved(), 
                null, "Income rents receieved", this);
        PeonyFaceUtils.initializeDatePicker(dateOnImprovementPicker, ZcaCalendar.convertToLocalDate(targetPersonalProperty.getDateOnImprovement()), 
                null, "Date on improvement", this);
        PeonyFaceUtils.initializeTextField(improvementCostField, targetPersonalProperty.getImprovementCost(), 
                null, "Improvement cost", this);
        PeonyFaceUtils.initializeTextField(expenseAdvertisingField, targetPersonalProperty.getExpenseAdvertising(), 
                null, "Expense on advertising", this);
        PeonyFaceUtils.initializeTextField(expenseAutoAndTravelField, targetPersonalProperty.getExpenseAutoAndTravel(), 
                null, "Expense on auto and travel", this);
        PeonyFaceUtils.initializeTextField(expenseCleaningField, targetPersonalProperty.getExpenseCleaning(), 
                null, "Expense on cleaning", this);
        PeonyFaceUtils.initializeTextField(expenseCommissionsField, targetPersonalProperty.getExpenseCommissions(), 
                null, "Expense on comminssions", this);
        PeonyFaceUtils.initializeTextField(expenseDepreciationField, targetPersonalProperty.getExpenseDepreciation(), 
                null, "Expense on depreciation", this);
        PeonyFaceUtils.initializeTextField(expenseInsuranceField, targetPersonalProperty.getExpenseInsurance(), 
                null, "Expense on insurance", this);
        PeonyFaceUtils.initializeTextField(expenseMgtFeeField, targetPersonalProperty.getExpenseMgtFee(), 
                null, "Expense on management fee", this);
        PeonyFaceUtils.initializeTextField(expenseReTaxesField, targetPersonalProperty.getExpenseReTaxes(), 
                null, "Expense on real estate taxes", this);
        PeonyFaceUtils.initializeTextField(expenseMorgageInterestField, targetPersonalProperty.getExpenseMorgageInterest(), 
                null, "Expense on morgage interest", this);
        PeonyFaceUtils.initializeTextField(expenseSuppliesField, targetPersonalProperty.getExpenseSupplies(), 
                null, "Expense on supplies", this);
        PeonyFaceUtils.initializeTextField(expenseRepairsField, targetPersonalProperty.getExpenseRepairs(), 
                null, "Expense on repairs", this);
        PeonyFaceUtils.initializeTextField(expenseProfServicesField, targetPersonalProperty.getExpenseProfServices(), 
                null, "Expense on professional services", this);
        PeonyFaceUtils.initializeTextField(expenseUtilitiesField, targetPersonalProperty.getExpenseUtilities(), 
                null, "Expense on utilities", this);
        PeonyFaceUtils.initializeTextField(expenseWaterSewerField, targetPersonalProperty.getExpenseWaterSewer(), 
                null, "Expense on water sewer", this);
        PeonyFaceUtils.initializeTextField(expenseOthersField, targetPersonalProperty.getExpenseOthers(), 
                null, "Expense on others", this);
        PeonyFaceUtils.initializeTextField(memoField, targetPersonalProperty.getMemo(), 
                null, "Memo", this);
        
        initializeSaveButton();
        initializeCancelButton();
        initializeDeleteButton();
    }
    
    private void initializeSaveButton(){
        if (saveButton == null){
            saveButton = new JFXButton("Save");
            saveButton.getStyleClass().add("peony-primary-small-button");
            saveButton.setOnAction((ActionEvent actionEvent) -> {
                getCachedThreadPoolExecutorService().submit(createSaveTaxpayerScheduleETask());
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
                            new PersonalPropertyDialogCloseRequest(targetPersonalProperty.getPersonalPropertyUuid()));
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
                if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to delete this schedule-E form?") == JOptionPane.YES_OPTION){
                    getCachedThreadPoolExecutorService().submit(createDeleteTaxpayerScheduleETask());
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
    
    private Task<Boolean> createDeleteTaxpayerScheduleETask(){
        return new Task<Boolean>(){
            @Override
            protected Boolean call() throws Exception {
                try{
                    Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient().deleteEntity_XML(
                            G02PersonalProperty.class, GardenRestParams.Taxpayer.deletePersonalPropertyRestParams(targetPersonalProperty.getPersonalPropertyUuid()));
                    updateMessage("Successfully deleted this personal property entry.");
                    return true;
                }catch (Exception ex){
                    updateMessage("Failed to deleted this personal property entry.");
                    return false;
                }
            }

            @Override
            protected void succeeded() {
                //GUI...
                try {
                    if (get()){
                        broadcastPeonyFaceEventHappened(new PersonalPropertyDeletedEvent(targetPersonalProperty));
                        PeonyFaceUtils.displayInformationMessageDialog(getMessage());
                    }else{
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
    }
    
    private Task<G02PersonalProperty> createSaveTaxpayerScheduleETask(){
        Task<G02PersonalProperty> saveTaxpayerInfoDataEntryTask = new Task<G02PersonalProperty>(){
            @Override
            protected G02PersonalProperty call() throws Exception {
                try {
                    loadTargetPersonalProperty();
                } catch (ZcaEntityValidationException ex) {
                    //Exceptions.printStackTrace(ex);
                    highlightBadEntityField(ex);
                    updateMessage(ex.getMessage());
                    return null;
                }
                try{
                    G02TaxpayerCase aG02TaxpayerCase;
                    if (ZcaValidator.isNullEmpty(targetPersonalProperty.getTaxpayerCaseUuid())){
                        aG02TaxpayerCase = null;
                    }else{
                        aG02TaxpayerCase = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient().findEntity_XML(G02TaxpayerCase.class, 
                                GardenRestParams.Taxpayer.findTaxpayerCaseByTaxpayerCaseUuidRestParams(targetPersonalProperty.getTaxpayerCaseUuid()));
                    }
                    if (aG02TaxpayerCase == null){
                        updateMessage("This personal property entry is NOT saved. Please save Taxpayer case's basic information before save this data entry.");
                    }else{
                        G02PersonalProperty aG02PersonalProperty = Lookup.getDefault().lookup(PeonyTaxpayerService.class)
                                .getPeonyTaxpayerRestClient().storeEntity_XML(G02PersonalProperty.class, 
                                        GardenRestParams.Taxpayer.storePersonalPropertyRestParams(), targetPersonalProperty);
                        if (aG02PersonalProperty == null){
                            updateMessage("Failed to save this data entry.");
                            return null;
                        }else{
                            updateMessage("Successfully saved this data entry.");
                        }
                    }
                    return targetPersonalProperty;
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
                    G02PersonalProperty aG02PersonalProperty = get();
                    if (aG02PersonalProperty == null){
                        msgType = JOptionPane.ERROR_MESSAGE;
                    }else{
                        resetDataEntryStyle();
                        //saved taxpayer info data entry
                        broadcastPeonyFaceEventHappened(new PersonalPropertySavedEvent(aG02PersonalProperty));
                        //close the data entry dialog
                        broadcastPeonyFaceEventHappened(new PersonalPropertyDialogCloseRequest(aG02PersonalProperty.getPersonalPropertyUuid()));
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }

                //display result information dialog
                String msg = getMessage();
                if (ZcaValidator.isNotNullEmpty(msg)){
                    PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, msg, "Personal Business Property (Schedule-E)", msgType);
                }
            }
        };
        return saveTaxpayerInfoDataEntryTask;
    }

    protected void loadTargetPersonalProperty() throws ZcaEntityValidationException{
        targetPersonalProperty.setPropertyType(propertyTypeField.getText());
        targetPersonalProperty.setPropertyAddress(propertyAddressField.getText());
        targetPersonalProperty.setPurchasePrice(purchasePriceField.getText());
        targetPersonalProperty.setPercentageOfOwnership(percentageOfOwnershipField.getText());
        targetPersonalProperty.setPercentageOfRentalUse(percentageOfRentalUseField.getText());
        targetPersonalProperty.setDateOnService(ZcaCalendar.convertToDate(dateOnServicePicker.getValue()));
        targetPersonalProperty.setIncomeRentsReceieved(incomeRentsReceievedField.getText());
        targetPersonalProperty.setDateOnImprovement(ZcaCalendar.convertToDate(dateOnImprovementPicker.getValue()));
        targetPersonalProperty.setImprovementCost(improvementCostField.getText());
        targetPersonalProperty.setExpenseAdvertising(expenseAdvertisingField.getText());
        targetPersonalProperty.setExpenseAutoAndTravel(expenseAutoAndTravelField.getText());
        targetPersonalProperty.setExpenseCleaning(expenseCleaningField.getText());
        targetPersonalProperty.setExpenseCommissions(expenseCommissionsField.getText());
        targetPersonalProperty.setExpenseDepreciation(expenseDepreciationField.getText());
        targetPersonalProperty.setExpenseInsurance(expenseInsuranceField.getText());
        targetPersonalProperty.setExpenseMgtFee(expenseMgtFeeField.getText());
        targetPersonalProperty.setExpenseReTaxes(expenseReTaxesField.getText());
        targetPersonalProperty.setExpenseMorgageInterest(expenseMorgageInterestField.getText());
        targetPersonalProperty.setExpenseSupplies(expenseSuppliesField.getText());
        targetPersonalProperty.setExpenseRepairs(expenseRepairsField.getText());
        targetPersonalProperty.setExpenseProfServices(expenseProfServicesField.getText());
        targetPersonalProperty.setExpenseUtilities(expenseUtilitiesField.getText());
        targetPersonalProperty.setExpenseWaterSewer(expenseWaterSewerField.getText());
        targetPersonalProperty.setExpenseOthers(expenseOthersField.getText());
        targetPersonalProperty.setMemo(memoField.getText());
        if (ZcaValidator.isNullEmpty(targetPersonalProperty.getPersonalPropertyUuid())){
            targetPersonalProperty.setPersonalPropertyUuid(ZcaUtils.generateUUIDString());
        }
        targetPersonalProperty.setTaxpayerCaseUuid(targetPeonyTaxpayerCase.getTaxpayerCase().getTaxpayerCaseUuid());
        try{
            G02EntityValidator.getSingleton().validate(targetPersonalProperty);
        }catch(ZcaEntityValidationException ex){
            highlightBadEntityField(ex);
            throw ex;
        }
    }
    
    @Override
    protected Node getBadEntityField(EntityField entityFiled) {
        switch (entityFiled){
            case EntityStatus:
                return null;
            case ExpenseAdvertising:
                return expenseAdvertisingField;
            case ExpenseAutoAndTravel:
                return expenseAutoAndTravelField;
            case ExpenseCleaning:
                return expenseCleaningField;
            case ExpenseCommissions:
                return expenseCommissionsField;
            case ExpenseDepreciation:
                return expenseDepreciationField;
            case ExpenseInsurance:
                return expenseInsuranceField;
            case ExpenseMgtFee:
                return expenseMgtFeeField;
            case ExpenseOthers:
                return expenseOthersField;
            case ExpenseProfServices:
                return expenseProfServicesField;
            case ExpenseMorgageInterest:
                return expenseMorgageInterestField;
            case ExpenseRepairs:
                return expenseRepairsField;
            case ExpenseSupplies:
                return expenseSuppliesField;
            case ExpenseReTaxes:
                return expenseReTaxesField;
            case ExpenseWaterSewer:
                return expenseWaterSewerField;
            case ExpenseUtilities:
                return expenseUtilitiesField;
            case Memo:
                return memoField;
            case ImprovementCost:
                return improvementCostField;
            case IncomeRentsReceieved:
                return incomeRentsReceievedField;
            case PercentageOfOwnership:
                return percentageOfOwnershipField;
            case PercentageOfRentalUse:
                return percentageOfRentalUseField;
            case PropertyAddress:
                return propertyAddressField;
            case PropertyType:
                return propertyTypeField;
            case PurchasePrice:
                return purchasePriceField;
            default:
                return null;
        }
    }
    
    @Override
    protected void resetDataEntryStyleHelper() {
        expenseAdvertisingField.setStyle(null);
        expenseAutoAndTravelField.setStyle(null);
        expenseCleaningField.setStyle(null);
        expenseCommissionsField.setStyle(null);
        expenseDepreciationField.setStyle(null);
        expenseInsuranceField.setStyle(null);
        expenseMgtFeeField.setStyle(null);
        expenseOthersField.setStyle(null);
        expenseProfServicesField.setStyle(null);
        expenseMorgageInterestField.setStyle(null);
        expenseRepairsField.setStyle(null);
        expenseSuppliesField.setStyle(null);
        expenseReTaxesField.setStyle(null);
        expenseWaterSewerField.setStyle(null);
        expenseUtilitiesField.setStyle(null);
        memoField.setStyle(null);
        improvementCostField.setStyle(null);
        incomeRentsReceievedField.setStyle(null);
        percentageOfOwnershipField.setStyle(null);
        percentageOfRentalUseField.setStyle(null);
        propertyAddressField.setStyle(null);
        propertyTypeField.setStyle(null);
        purchasePriceField.setStyle(null);
    }

}
