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
import com.zcomapproach.garden.peony.view.events.TlcLicenseDeletedEvent;
import com.zcomapproach.garden.peony.view.events.TlcLicenseDialogCloseRequest;
import com.zcomapproach.garden.peony.view.events.TlcLicenseSavedEvent;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.PersonalPropertyDialogCloseRequest;
import com.zcomapproach.garden.persistence.G02EntityValidator;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerCase;
import com.zcomapproach.garden.persistence.entity.G02TlcLicense;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.util.GardenData;
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
public class TaxpayerTlcDriverController extends PeonyTaxpayerServiceController{
    @FXML
    private JFXTextField tlcLicenseField;
    @FXML
    private JFXTextField vehicleMakeField;
    @FXML
    private JFXTextField vehicleModelField;
    @FXML
    private DatePicker dateInServiceDatePicker;
    @FXML
    private JFXTextField numberOfSeatsField;
    @FXML
    private JFXTextField over600lbsField;
    @FXML
    private JFXTextField totalMilesField;
    @FXML
    private JFXTextField businessMilesField;
    @FXML
    private JFXTextField mileageRateField;
    @FXML
    private JFXTextField garageRentField;
    @FXML
    private JFXTextField tiresField;
    @FXML
    private JFXTextField tollsField;
    @FXML
    private JFXTextField serviceFeeField;
    @FXML
    private JFXTextField mealsField;
    @FXML
    private JFXTextField uniformField;
    @FXML
    private JFXTextField carWashField;
    @FXML
    private JFXTextField radioRepairField;
    @FXML
    private JFXTextField accessoriesField;
    @FXML
    private JFXTextField telephoneField;
    @FXML
    private JFXTextField parkingField;
    @FXML
    private JFXTextField insuranceField;
    @FXML
    private JFXTextField depreciationField;
    @FXML
    private JFXTextField leasePaymentField;
    @FXML
    private JFXTextField maintenanceField;
    @FXML
    private JFXTextField repairsField;
    @FXML
    private JFXTextField gasField;
    @FXML
    private JFXTextField oilField;
    @FXML
    private JFXTextField registerationFeeField;
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
    
    private final PeonyTaxpayerCase targetPeonyTaxpayerCase;
    private final G02TlcLicense targetTlcLicense;
    
    private final Tab ownerTab;

    public TaxpayerTlcDriverController(G02TlcLicense targetTlcLicense, 
                                        PeonyTaxpayerCase targetPeonyTaxpayerCase, 
                                        boolean saveBtnRequired, 
                                        boolean deleteBtnRequired, 
                                        boolean cancelBtnRequired,
                                        Tab ownerTab) 
    {
        super(targetPeonyTaxpayerCase);
        if (targetTlcLicense == null){
            targetTlcLicense = new G02TlcLicense();
        }
        this.targetTlcLicense = targetTlcLicense;
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
        initializeTlcLicenseDataEntry();
    }

    private void initializeTlcLicenseDataEntry() {
        PeonyFaceUtils.initializeTextField(tlcLicenseField, targetTlcLicense.getTlcLicense(), 
                null, "TLC license", this);
        PeonyFaceUtils.initializeTextField(vehicleMakeField, targetTlcLicense.getVehicleType(), 
                null, "Vehicle Make", this);
        PeonyFaceUtils.initializeTextField(vehicleModelField, targetTlcLicense.getVehicleModel(), 
                null, "Vehicle Model", this);
        PeonyFaceUtils.initializeDatePicker(dateInServiceDatePicker, ZcaCalendar.convertToLocalDate(targetTlcLicense.getDateInService()), 
                null, "Date on service", this);
        PeonyFaceUtils.initializeTextField(numberOfSeatsField, targetTlcLicense.getNumberOfSeats(), 
                null, "Number of seats", this);
        PeonyFaceUtils.initializeTextField(over600lbsField, targetTlcLicense.getOver600lbs(), 
                null, "Over 6000lbs or not", this);
        PeonyFaceUtils.initializeTextField(totalMilesField, targetTlcLicense.getTotalMiles(), 
                null, "Total miles", this);
        PeonyFaceUtils.initializeTextField(businessMilesField, targetTlcLicense.getBusinessMiles(), 
                null, "Business miles", this);
        PeonyFaceUtils.initializeTextField(mileageRateField, targetTlcLicense.getMileageRate(), 
                null, "Mileage rate", this);
        PeonyFaceUtils.initializeTextField(garageRentField, targetTlcLicense.getGarageRent(), 
                null, "Garage rent", this);
        PeonyFaceUtils.initializeTextField(tiresField, targetTlcLicense.getTires(), 
                null, "Tires", this);
        PeonyFaceUtils.initializeTextField(tollsField, targetTlcLicense.getTolls(), 
                null, "Tolls", this);
        PeonyFaceUtils.initializeTextField(serviceFeeField, targetTlcLicense.getServiceFee(), 
                null, "Service fee", this);
        PeonyFaceUtils.initializeTextField(mealsField, targetTlcLicense.getMeals(), 
                null, "Meals", this);
        PeonyFaceUtils.initializeTextField(uniformField, targetTlcLicense.getUniform(), 
                null, "Uniform", this);
        PeonyFaceUtils.initializeTextField(carWashField, targetTlcLicense.getCarWash(), 
                null, "Car wash", this);
        PeonyFaceUtils.initializeTextField(radioRepairField, targetTlcLicense.getRadioRepair(), 
                null, "Radio repair", this);
        PeonyFaceUtils.initializeTextField(accessoriesField, targetTlcLicense.getAccessories(), 
                null, "Accessories", this);
        PeonyFaceUtils.initializeTextField(telephoneField, targetTlcLicense.getTelephone(), 
                null, "Telephone", this);
        PeonyFaceUtils.initializeTextField(parkingField, targetTlcLicense.getParking(), 
                null, "Parking", this);
        PeonyFaceUtils.initializeTextField(insuranceField, targetTlcLicense.getInsurance(), 
                null, "Insurance", this);
        PeonyFaceUtils.initializeTextField(depreciationField, targetTlcLicense.getDepreciation(), 
                null, "Depreciation", this);
        PeonyFaceUtils.initializeTextField(leasePaymentField, targetTlcLicense.getLeasePayment(), 
                null, "Lease payment", this);
        PeonyFaceUtils.initializeTextField(maintenanceField, targetTlcLicense.getMaintenance(), 
                null, "Maintenance", this);
        PeonyFaceUtils.initializeTextField(repairsField, targetTlcLicense.getRepairs(), 
                null, "Repairs", this);
        PeonyFaceUtils.initializeTextField(gasField, targetTlcLicense.getGas(), 
                null, "Gas", this);
        PeonyFaceUtils.initializeTextField(oilField, targetTlcLicense.getOil(), 
                null, "Oil", this);
        PeonyFaceUtils.initializeTextField(registerationFeeField, targetTlcLicense.getRegistrationFee(), 
                null, "Registration fee", this);
        PeonyFaceUtils.initializeTextField(memoField, targetTlcLicense.getMemo(), 
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
                getCachedThreadPoolExecutorService().submit(createSaveTaxpayerTlcDriverTask());
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
                            new PersonalPropertyDialogCloseRequest(targetTlcLicense.getDriverUuid()));
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
                if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to delete this TLC entry?") == JOptionPane.YES_OPTION){
                    getCachedThreadPoolExecutorService().submit(createDeleteTaxpayerTlcDriverTask());
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
    
    private Task<Boolean> createDeleteTaxpayerTlcDriverTask(){
        return new Task<Boolean>(){
            @Override
            protected Boolean call() throws Exception {
                try{
                    Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient().deleteEntity_XML(
                                G02TlcLicense.class, GardenRestParams.Taxpayer.deleteTlcLicenseRestParams(targetTlcLicense.getDriverUuid()));
                    updateMessage("Successfully deleted this TLC entry.");
                    return true;
                }catch (Exception ex){
                    updateMessage("Failed to deleted this TLC entry." + ex.getMessage());
                    return false;
                }
            }

            @Override
            protected void succeeded() {
                //GUI...
                try {
                    if (get()){
                        broadcastPeonyFaceEventHappened(new TlcLicenseDeletedEvent(targetTlcLicense));
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
    
    private Task<G02TlcLicense> createSaveTaxpayerTlcDriverTask(){
        Task<G02TlcLicense> saveTaxpayerInfoDataEntryTask = new Task<G02TlcLicense>(){
            @Override
            protected G02TlcLicense call() throws Exception {
                try {
                    loadTargetTlcLicense();
                } catch (ZcaEntityValidationException ex) {
                    //Exceptions.printStackTrace(ex);
                    highlightBadEntityField(ex);
                    updateMessage(ex.getMessage());
                    return null;
                }
                try{
                    G02TaxpayerCase aG02TaxpayerCase;
                    if (ZcaValidator.isNullEmpty(targetTlcLicense.getTaxpayerCaseUuid())){
                        aG02TaxpayerCase = null;
                    }else{
                        aG02TaxpayerCase = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient().findEntity_XML(G02TaxpayerCase.class, 
                                GardenRestParams.Taxpayer.findTaxpayerCaseByTaxpayerCaseUuidRestParams(targetTlcLicense.getTaxpayerCaseUuid()));
                    }
                    if (aG02TaxpayerCase == null){
                        updateMessage("This taxpayer info data entry is NOT saved. Please save Taxpayer case's basic information before save this data entry.");
                    }else{
                        G02TlcLicense aG02TlcLicense = Lookup.getDefault().lookup(PeonyTaxpayerService.class)
                                .getPeonyTaxpayerRestClient().storeEntity_XML(G02TlcLicense.class, 
                                        GardenRestParams.Taxpayer.storeTlcLicenseRestParams(), targetTlcLicense);
                        if (aG02TlcLicense == null){
                            updateMessage("Failed to save this data entry.");
                            return null;
                        }else{
                            updateMessage("Successfully saved this data entry.");
                        }
                    }
                    return targetTlcLicense;
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
                    G02TlcLicense aG02TlcLicense = get();
                    if (aG02TlcLicense == null){
                        msgType = JOptionPane.ERROR_MESSAGE;
                    }else{
                        resetDataEntryStyle();
                        //saved taxpayer info data entry
                        broadcastPeonyFaceEventHappened(new TlcLicenseSavedEvent(aG02TlcLicense));
                        //close the data entry dialog
                        broadcastPeonyFaceEventHappened(new TlcLicenseDialogCloseRequest(aG02TlcLicense.getDriverUuid()));
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }

                //display result information dialog
                String msg = getMessage();
                if (ZcaValidator.isNotNullEmpty(msg)){
                    PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, msg, "TLC License", msgType);
                }
            }
        };
        return saveTaxpayerInfoDataEntryTask;
    }

    protected void loadTargetTlcLicense() throws ZcaEntityValidationException {
        targetTlcLicense.setTlcLicense(tlcLicenseField.getText());
        targetTlcLicense.setVehicleType(vehicleMakeField.getText());
        targetTlcLicense.setVehicleModel(vehicleModelField.getText());
        targetTlcLicense.setDateInService(ZcaCalendar.convertToDate(dateInServiceDatePicker.getValue()));
        targetTlcLicense.setNumberOfSeats(numberOfSeatsField.getText());
        targetTlcLicense.setOver600lbs(over600lbsField.getText());
        targetTlcLicense.setTotalMiles(totalMilesField.getText());
        targetTlcLicense.setBusinessMiles(businessMilesField.getText());
        targetTlcLicense.setMileageRate(mileageRateField.getText());
        targetTlcLicense.setGarageRent(garageRentField.getText());
        targetTlcLicense.setTires(tiresField.getText());
        targetTlcLicense.setTolls(tollsField.getText());
        targetTlcLicense.setServiceFee(serviceFeeField.getText());
        targetTlcLicense.setMeals(mealsField.getText());
        targetTlcLicense.setUniform(uniformField.getText());
        targetTlcLicense.setCarWash(carWashField.getText());
        targetTlcLicense.setRadioRepair(radioRepairField.getText());
        targetTlcLicense.setAccessories(accessoriesField.getText());
        targetTlcLicense.setTelephone(telephoneField.getText());
        targetTlcLicense.setParking(parkingField.getText());
        targetTlcLicense.setInsurance(insuranceField.getText());
        targetTlcLicense.setDepreciation(depreciationField.getText());
        targetTlcLicense.setLeasePayment(leasePaymentField.getText());
        targetTlcLicense.setMaintenance(maintenanceField.getText());
        targetTlcLicense.setRepairs(repairsField.getText());
        targetTlcLicense.setGas(gasField.getText());
        targetTlcLicense.setOil(oilField.getText());
        targetTlcLicense.setRegistrationFee(registerationFeeField.getText());
        targetTlcLicense.setMemo(memoField.getText());
        if (ZcaValidator.isNullEmpty(targetTlcLicense.getDriverUuid())){
            targetTlcLicense.setDriverUuid(GardenData.generateUUIDString());
        }
        targetTlcLicense.setTaxpayerCaseUuid(targetPeonyTaxpayerCase.getTaxpayerCase().getTaxpayerCaseUuid());
        try{
            G02EntityValidator.getSingleton().validate(targetTlcLicense);
        }catch(ZcaEntityValidationException ex){
            highlightBadEntityField(ex);
            throw ex;
        }
    }
    
    @Override
    protected Node getBadEntityField(EntityField entityFiled) {
        switch (entityFiled){
            case TlcLicense:
                return tlcLicenseField;
            case VehicleType:
                return vehicleMakeField;
            case VehicleModel:
                return vehicleModelField;
            case NumberOfSeats:
                return numberOfSeatsField;
            case Over600lbs:
                return over600lbsField;
            case TotalMiles:
                return totalMilesField;
            case BusinessMiles:
                return businessMilesField;
            case MileageRate:
                return mileageRateField;
            case GarageRent:
                return garageRentField;
            case Tires:
                return tiresField;
            case Tolls:
                return tollsField;
            case ServiceFee:
                return serviceFeeField;
            case Meals:
                return mealsField;
            case Uniform:
                return uniformField;
            case CarWash:
                return carWashField;
            case RadioRepair:
                return radioRepairField;
            case Accessories:
                return accessoriesField;
            case Telephone:
                return telephoneField;
            case Parking:
                return parkingField;
            case Insurance:
                return insuranceField;
            case Depreciation:
                return depreciationField;
            case LeasePayment:
                return leasePaymentField;
            case Maintenance:
                return maintenanceField;
            case Repairs:
                return repairsField;
            case Gas:
                return gasField;
            case Oil:
                return oilField;
            case RegistrationFee:
                return registerationFeeField;
            case Memo:
                return memoField;
            default:
                return null;
        }
    }
    
    @Override
    protected void resetDataEntryStyleHelper() {
        tlcLicenseField.setStyle(null);
        vehicleMakeField.setStyle(null);
        vehicleModelField.setStyle(null);
        numberOfSeatsField.setStyle(null);
        over600lbsField.setStyle(null);
        totalMilesField.setStyle(null);
        businessMilesField.setStyle(null);
        mileageRateField.setStyle(null);
        garageRentField.setStyle(null);
        tiresField.setStyle(null);
        tollsField.setStyle(null);
        serviceFeeField.setStyle(null);
        mealsField.setStyle(null);
        uniformField.setStyle(null);
        carWashField.setStyle(null);
        radioRepairField.setStyle(null);
        accessoriesField.setStyle(null);
        telephoneField.setStyle(null);
        parkingField.setStyle(null);
        insuranceField.setStyle(null);
        depreciationField.setStyle(null);
        leasePaymentField.setStyle(null);
        maintenanceField.setStyle(null);
        repairsField.setStyle(null);
        gasField.setStyle(null);
        oilField.setStyle(null);
        registerationFeeField.setStyle(null);
        memoField.setStyle(null);
    }
}
