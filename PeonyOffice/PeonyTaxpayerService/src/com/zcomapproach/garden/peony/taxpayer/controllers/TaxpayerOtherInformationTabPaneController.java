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

import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;

/**
 * @deprecated - replaced by TaxpayerOtherInformationController
 * @author zhijun98
 */
public class TaxpayerOtherInformationTabPaneController extends PeonyTaxpayerCaseTabPaneController implements PeonyFaceEventListener{
////    @FXML
////    private Label pageTitleLabel;
////    @FXML
////    private VBox tabRootVBox;
////    @FXML
////    private GridPane topTitleGridPane;
    @FXML
    private HBox topButtonsHBox;
////    @FXML
////    private FlowPane otherButtonsFlowPane;
////    @FXML
////    private TabPane othersTabPane;
////    
////    private final HashMap<String, TaxpayerDataEntryController> taxpayerDataEntryControllerStorage = new HashMap<>();
////    private final HashMap<String, TaxpayerScheduleCController> taxpayerScheduleCControllerStorage = new HashMap<>();
////    private final HashMap<String, TaxpayerScheduleEController> taxpayerScheduleEControllerStorage = new HashMap<>();
////    private final HashMap<String, TaxpayerTlcDriverController> taxpayerTlcDriverControllerStorage = new HashMap<>();
    
    public TaxpayerOtherInformationTabPaneController(PeonyTaxpayerCase targetPeonyTaxpayerCase, Tab ownerTab) {
        super(targetPeonyTaxpayerCase, ownerTab);
    }

    @Override
    protected void setPageTitle(final String pageTitle) {
////        if (Platform.isFxApplicationThread()){
////            pageTitleLabel.setText(pageTitle);
////        }else{
////            Platform.runLater(() -> {
////                pageTitleLabel.setText(pageTitle);
////            });
////        }
    }
    
////    private List<Button> constructOtherFunctionalButtons(){
////        List<Button> buttons = new ArrayList<>();
////        
////        Button btn = new Button("Dependent");
////        btn.setPrefWidth(100.0);
////        btn.setTooltip(new Tooltip("Add dependents of taxpayers"));
////        btn.setGraphic(PeonyGraphic.getImageView("add.png"));
////        btn.setOnAction((ActionEvent e) -> {
////            addTaxpayerProfileTab(null);
////        });
////        buttons.add(btn);
////        
////        btn = new Button("Schedule-C");
////        btn.setPrefWidth(100.0);
////        btn.setTooltip(new Tooltip("Add schedule C form"));
////        btn.setGraphic(PeonyGraphic.getImageView("add.png"));
////        btn.setOnAction((ActionEvent e) -> {
////            addPersonalBusinessPropertyTab(null);
////        });
////        buttons.add(btn);
////        
////        btn = new Button("Schedule-E");
////        btn.setPrefWidth(100.0);
////        btn.setTooltip(new Tooltip("Add schedule-E form"));
////        btn.setGraphic(PeonyGraphic.getImageView("add.png"));
////        btn.setOnAction((ActionEvent e) -> {
////            addPersonalPropertyTab(null);
////        });
////        buttons.add(btn);
////        
////        btn = new Button("TLC License");
////        btn.setPrefWidth(100.0);
////        btn.setTooltip(new Tooltip("Add TLC License"));
////        btn.setGraphic(PeonyGraphic.getImageView("add.png"));
////        btn.setOnAction((ActionEvent e) -> {
////            addTlcLicenseTab(null);
////        });
////        buttons.add(btn);
////        
////        return buttons;
////    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
////        if(event instanceof PersonalBusinessPropertyDeletedEvent){
////            removePersonalBusinessProperty(((PersonalBusinessPropertyDeletedEvent)event).getPersonalBusinessProperty());
////        }else if(event instanceof TaxpayerInfoDataEntryDeletedEvent){
////            removeTaxpayerInfoDataEntry(((TaxpayerInfoDataEntryDeletedEvent)event).getTaxpayerInfo());
////        }else if(event instanceof TlcLicenseDeletedEvent){
////            removeTlcLicense(((TlcLicenseDeletedEvent)event).getTlcLicense());
////        }else if(event instanceof PersonalPropertyDeletedEvent){
////            removePersonalProperty(((PersonalPropertyDeletedEvent)event).getPersonalProperty());
////        }
    }

////    private void removeTaxpayerInfoDataEntry(G02TaxpayerInfo taxpayerInfo) {
////        getTargetPeonyTaxpayerCase().removeTaxpayerInfo(taxpayerInfo);
////        try{
////            removeTab(taxpayerDataEntryControllerStorage.get(taxpayerInfo.getTaxpayerUserUuid()).getOwnerTab());
////            taxpayerDataEntryControllerStorage.remove(taxpayerInfo.getTaxpayerUserUuid());
////        }catch (Exception ex){}
////    }
////
////    private void removeTlcLicense(G02TlcLicense tlcLicense) {
////        getTargetPeonyTaxpayerCase().removeTlcLicense(tlcLicense);
////        try{
////            removeTab(taxpayerTlcDriverControllerStorage.get(tlcLicense.getDriverUuid()).getOwnerTab());
////            taxpayerTlcDriverControllerStorage.remove(tlcLicense.getDriverUuid());
////        }catch (Exception ex){}
////    }
////
////    private void removePersonalBusinessProperty(G02PersonalBusinessProperty personalBusinessProperty) {
////        getTargetPeonyTaxpayerCase().removePersonalBusinessProperty(personalBusinessProperty);
////        try{
////            removeTab(taxpayerScheduleCControllerStorage.get(personalBusinessProperty.getPersonalBusinessPropertyUuid()).getOwnerTab());
////            taxpayerScheduleCControllerStorage.remove(personalBusinessProperty.getPersonalBusinessPropertyUuid());
////        }catch (Exception ex){}
////    }
////
////    private void removePersonalProperty(G02PersonalProperty personalProperty) {
////        getTargetPeonyTaxpayerCase().removePersonalProperty(personalProperty);
////        try{
////            removeTab(taxpayerScheduleEControllerStorage.get(personalProperty.getPersonalPropertyUuid()).getOwnerTab());
////            taxpayerScheduleEControllerStorage.remove(personalProperty.getPersonalPropertyUuid());
////        }catch (Exception ex){}
////    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //topButtonsHBox
        topButtonsHBox.getChildren().addAll(constructAllTaxpayerCaseFunctionalButtons());
////        //otherButtonsFlowPane
////        otherButtonsFlowPane.getChildren().addAll(constructOtherFunctionalButtons());
        
////        othersTabPane.getTabs().clear();
////        //display penels for dependents
////        List<G02TaxpayerInfo> aG02TaxpayerInfoList = getTargetPeonyTaxpayerCase().retrieveDependentTaxpayerInfoList();
////        if ((aG02TaxpayerInfoList != null) && (!aG02TaxpayerInfoList.isEmpty())){
////            for (G02TaxpayerInfo aG02TaxpayerInfo : aG02TaxpayerInfoList){
////                addTaxpayerProfileTab(aG02TaxpayerInfo);
////            }
////        }
////        
////        //G02PersonalBusinessProperty
////        List<G02PersonalBusinessProperty> aG02PersonalBusinessPropertyList = getTargetPeonyTaxpayerCase().getPersonalBusinessPropertyList();
////        if ((aG02PersonalBusinessPropertyList != null) && (!aG02PersonalBusinessPropertyList.isEmpty())){
////            for (G02PersonalBusinessProperty aG02PersonalBusinessProperty : aG02PersonalBusinessPropertyList){
////                addPersonalBusinessPropertyTab(aG02PersonalBusinessProperty);
////            }
////        }
////        
////        //G02PersonalProperty
////        List<G02PersonalProperty> aG02PersonalPropertyList = getTargetPeonyTaxpayerCase().getPersonalPropertyList();
////        if ((aG02PersonalPropertyList != null) && (!aG02PersonalPropertyList.isEmpty())){
////            for (G02PersonalProperty aG02PersonalProperty : aG02PersonalPropertyList){
////                addPersonalPropertyTab(aG02PersonalProperty);
////            }
////        }
////        
////        //G02TlcLicense
////        List<G02TlcLicense> aG02TlcLicenseList = getTargetPeonyTaxpayerCase().getTlcLicenseList();
////        if ((aG02TlcLicenseList != null) && (!aG02TlcLicenseList.isEmpty())){
////            for (G02TlcLicense aG02TlcLicense : aG02TlcLicenseList){
////                addTlcLicenseTab(aG02TlcLicense);
////            }
////        }
    }
    
////    private void addTaxpayerProfileTab(final G02TaxpayerInfo aG02TaxpayerInfo){
////        if (Platform.isFxApplicationThread()){
////            addTaxpayerProfilePaneHelper(aG02TaxpayerInfo);
////        }else{
////            Platform.runLater(new Runnable(){
////                @Override
////                public void run() {
////                    addTaxpayerProfilePaneHelper(aG02TaxpayerInfo);
////                }
////            });
////        }
////    }
////    
////    private void addTaxpayerProfilePaneHelper(G02TaxpayerInfo aG02TaxpayerInfo) {
////        //this case is to create a brand new record for target taxpayer case
////        if (aG02TaxpayerInfo == null){
////            aG02TaxpayerInfo = new G02TaxpayerInfo();
////            aG02TaxpayerInfo.setTaxpayerUserUuid(GardenData.generateUUIDString());
////            aG02TaxpayerInfo.setSsn("");
////            aG02TaxpayerInfo.setTaxpayerCaseUuid(getTargetPeonyTaxpayerCaseUuid());
////            
////            getTargetPeonyTaxpayerCase().getTaxpayerInfoList().add(aG02TaxpayerInfo);
////        }
////        try {
////            Tab tab = new Tab("Dependent SSN: " + aG02TaxpayerInfo.getSsn());
////            TaxpayerDataEntryController aTaxpayerInfoDataEntryPaneController = new TaxpayerDataEntryController(
////                    getTargetPeonyTaxpayerCase(), aG02TaxpayerInfo, false, true, false, tab);
////            aTaxpayerInfoDataEntryPaneController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
////            aTaxpayerInfoDataEntryPaneController.addPeonyFaceEventListener(this);
////            tab.setContent(aTaxpayerInfoDataEntryPaneController.loadFxml());
////            othersTabPane.getTabs().add(tab);
////            othersTabPane.getSelectionModel().select(tab);
////            taxpayerInfoDataEntryPaneControllerStorage.put(aG02TaxpayerInfo.getTaxpayerUserUuid(), aTaxpayerInfoDataEntryPaneController);
////        } catch (IOException ex) {
////            Exceptions.printStackTrace(ex);
////        }
////    }
////
////    private void addPersonalBusinessPropertyTab(final G02PersonalBusinessProperty personalBusinessProperty) {
////        if (Platform.isFxApplicationThread()){
////            addPersonalBusinessPropertyPaneHelper(personalBusinessProperty);
////        }else{
////            Platform.runLater(new Runnable(){
////                @Override
////                public void run() {
////                    addPersonalBusinessPropertyPaneHelper(personalBusinessProperty);
////                }
////            });
////        }
////    }
////    private void addPersonalBusinessPropertyPaneHelper(G02PersonalBusinessProperty personalBusinessProperty) {
////        //this case is to create a brand new record for target taxpayer case
////        if (personalBusinessProperty == null){
////            personalBusinessProperty = new G02PersonalBusinessProperty();
////            personalBusinessProperty.setPersonalBusinessPropertyUuid(GardenData.generateUUIDString());
////            personalBusinessProperty.setTaxpayerCaseUuid(getTargetPeonyTaxpayerCaseUuid());
////            personalBusinessProperty.setBusinessPropertyName("");
////            
////            getTargetPeonyTaxpayerCase().getPersonalBusinessPropertyList().add(personalBusinessProperty);
////        }
////        try {
////            Tab tab = new Tab("Schedule C: " + personalBusinessProperty.getBusinessPropertyName());
////            TaxpayerScheduleCController aTaxpayerScheduleCController = new TaxpayerScheduleCController(
////                    personalBusinessProperty, getTargetPeonyTaxpayerCase(), false, true, false, tab);
////            aTaxpayerScheduleCController.addPeonyFaceEventListenerList(this.getPeonyFaceEventListenerList());
////            aTaxpayerScheduleCController.addPeonyFaceEventListener(this);
////            tab.setContent(aTaxpayerScheduleCController.loadFxml());
////            othersTabPane.getTabs().add(tab);
////            othersTabPane.getSelectionModel().select(tab);
////            taxpayerScheduleCControllerStorage.put(personalBusinessProperty.getPersonalBusinessPropertyUuid(), aTaxpayerScheduleCController);
////        } catch (IOException ex) {
////            Exceptions.printStackTrace(ex);
////        }
////    }
////    private void addPersonalPropertyTab(final G02PersonalProperty personalProperty) {
////        if (Platform.isFxApplicationThread()){
////            addPersonalPropertyPaneHelper(personalProperty);
////        }else{
////            Platform.runLater(new Runnable(){
////                @Override
////                public void run() {
////                    addPersonalPropertyPaneHelper(personalProperty);
////                }
////            });
////        }
////    }
////    
////    private void addPersonalPropertyPaneHelper(G02PersonalProperty personalProperty) {
////        //this case is to create a brand new record for target taxpayer case
////        if (personalProperty == null){
////            personalProperty = new G02PersonalProperty();
////            personalProperty.setPersonalPropertyUuid(GardenData.generateUUIDString());
////            personalProperty.setTaxpayerCaseUuid(getTargetPeonyTaxpayerCaseUuid());
////            personalProperty.setPropertyAddress("");
////            
////            getTargetPeonyTaxpayerCase().getPersonalPropertyList().add(personalProperty);
////        }
////        try {
////            Tab tab = new Tab("Schedule E: " + personalProperty.getPropertyAddress());
////            TaxpayerScheduleEController aTaxpayerScheduleEController = new TaxpayerScheduleEController(
////                    personalProperty, getTargetPeonyTaxpayerCase(), false, true, false, tab);
////            aTaxpayerScheduleEController.addPeonyFaceEventListenerList(this.getPeonyFaceEventListenerList());
////            aTaxpayerScheduleEController.addPeonyFaceEventListener(this);
////            tab.setContent(aTaxpayerScheduleEController.loadFxml());
////            othersTabPane.getTabs().add(tab);
////            othersTabPane.getSelectionModel().select(tab);
////            taxpayerScheduleEControllerStorage.put(personalProperty.getPersonalPropertyUuid(), aTaxpayerScheduleEController);
////        } catch (IOException ex) {
////            Exceptions.printStackTrace(ex);
////        }
////    }
////    
////    private void addTlcLicenseTab(final G02TlcLicense tlcLicense) {
////        if (Platform.isFxApplicationThread()){
////            addTlcLicensePaneHelper(tlcLicense);
////        }else{
////            Platform.runLater(new Runnable(){
////                @Override
////                public void run() {
////                    addTlcLicensePaneHelper(tlcLicense);
////                }
////            });
////        }
////    }
////    private void addTlcLicensePaneHelper(G02TlcLicense tlcLicense) {
////        //this case is to create a brand new record for target taxpayer case
////        if (tlcLicense == null){
////            tlcLicense = new G02TlcLicense();
////            tlcLicense.setDriverUuid(GardenData.generateUUIDString());
////            tlcLicense.setTaxpayerCaseUuid(getTargetPeonyTaxpayerCaseUuid());
////            tlcLicense.setTlcLicense("");
////            
////            getTargetPeonyTaxpayerCase().getTlcLicenseList().add(tlcLicense);
////        }
////        try {
////            Tab tab = new Tab("TLC: " + tlcLicense.getTlcLicense());
////            TaxpayerTlcDriverController aTaxpayerTlcDriverController = new TaxpayerTlcDriverController(
////                    tlcLicense, getTargetPeonyTaxpayerCase(), false, true, false, tab);
////            aTaxpayerTlcDriverController.addPeonyFaceEventListenerList(this.getPeonyFaceEventListenerList());
////            aTaxpayerTlcDriverController.addPeonyFaceEventListener(this);
////            tab.setContent(aTaxpayerTlcDriverController.loadFxml());
////            othersTabPane.getTabs().add(tab);
////            othersTabPane.getSelectionModel().select(tab);
////            taxpayerTlcDriverControllerStorage.put(tlcLicense.getDriverUuid(), aTaxpayerTlcDriverController);
////        } catch (IOException ex) {
////            Exceptions.printStackTrace(ex);
////        }
////    }
////
////    private void removeTab(final Tab tab) {
////        if (Platform.isFxApplicationThread()){
////            othersTabPane.getTabs().remove(tab);
////        }else{
////            Platform.runLater(new Runnable(){
////                @Override
////                public void run() {
////                    othersTabPane.getTabs().remove(tab);
////                }
////            });
////        }
////    }

    @Override
    protected void loadTargetTaxpayerCaseFromTab() throws ZcaEntityValidationException {
////        String taxpayerCaseUuid = getTargetPeonyTaxpayerCaseUuid();
////        List<TaxpayerDataEntryController> aTaxpayerInfoDataEntryPaneControllerList = new ArrayList<>(taxpayerDataEntryControllerStorage.values());
////        for (TaxpayerDataEntryController aTaxpayerInfoDataEntryPaneController : aTaxpayerInfoDataEntryPaneControllerList){
////            try{
////                aTaxpayerInfoDataEntryPaneController.loadTargetTaxpayerInfo(taxpayerCaseUuid);
////            }catch (ZcaEntityValidationException ex){
////                othersTabPane.getSelectionModel().select(aTaxpayerInfoDataEntryPaneController.getOwnerTab());
////                throw ex;
////            }
////        }
////        List<TaxpayerScheduleCController> aTaxpayerScheduleCControllerList = new ArrayList<>(taxpayerScheduleCControllerStorage.values());
////        for (TaxpayerScheduleCController aTaxpayerScheduleCController : aTaxpayerScheduleCControllerList){
////            try{
////                aTaxpayerScheduleCController.loadTargetPersonalBusinessProperty();
////            }catch (ZcaEntityValidationException ex){
////                othersTabPane.getSelectionModel().select(aTaxpayerScheduleCController.getOwnerTab());
////                throw ex;
////            }
////        }
////        List<TaxpayerScheduleEController> aTaxpayerScheduleEControllerList = new ArrayList<>(taxpayerScheduleEControllerStorage.values());
////        for (TaxpayerScheduleEController aTaxpayerScheduleEController : aTaxpayerScheduleEControllerList){
////            try{
////                aTaxpayerScheduleEController.loadTargetPersonalProperty();
////            }catch (ZcaEntityValidationException ex){
////                othersTabPane.getSelectionModel().select(aTaxpayerScheduleEController.getOwnerTab());
////                throw ex;
////            }
////        }
////        List<TaxpayerTlcDriverController> aTaxpayerTlcDriverControllerList = new ArrayList<>(taxpayerTlcDriverControllerStorage.values());
////        for (TaxpayerTlcDriverController aTaxpayerTlcDriverController : aTaxpayerTlcDriverControllerList){
////            try{
////                aTaxpayerTlcDriverController.loadTargetTlcLicense();
////            }catch (ZcaEntityValidationException ex){
////                othersTabPane.getSelectionModel().select(aTaxpayerTlcDriverController.getOwnerTab());
////                throw ex;
////            }
////        }
    }

    @Override
    protected void resetDataEntryStyleFromTab() {
////        List<TaxpayerDataEntryController> aTaxpayerInfoDataEntryPaneControllerList = new ArrayList<>(taxpayerDataEntryControllerStorage.values());
////        for (TaxpayerDataEntryController aTaxpayerInfoDataEntryPaneController : aTaxpayerInfoDataEntryPaneControllerList){
////            aTaxpayerInfoDataEntryPaneController.resetDataEntryStyle();
////        }
////        List<TaxpayerScheduleCController> aTaxpayerScheduleCControllerList = new ArrayList<>(taxpayerScheduleCControllerStorage.values());
////        for (TaxpayerScheduleCController aTaxpayerScheduleCController : aTaxpayerScheduleCControllerList){
////            aTaxpayerScheduleCController.resetDataEntryStyle();
////        }
////        List<TaxpayerScheduleEController> aTaxpayerScheduleEControllerList = new ArrayList<>(taxpayerScheduleEControllerStorage.values());
////        for (TaxpayerScheduleEController aTaxpayerScheduleEController : aTaxpayerScheduleEControllerList){
////            aTaxpayerScheduleEController.resetDataEntryStyle();
////        }
////        List<TaxpayerTlcDriverController> aTaxpayerTlcDriverControllerList = new ArrayList<>(taxpayerTlcDriverControllerStorage.values());
////        for (TaxpayerTlcDriverController aTaxpayerTlcDriverController : aTaxpayerTlcDriverControllerList){
////            aTaxpayerTlcDriverController.resetDataEntryStyle();
////        }
    }
}
