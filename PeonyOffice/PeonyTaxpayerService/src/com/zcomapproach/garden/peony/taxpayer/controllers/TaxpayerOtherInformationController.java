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

import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.events.PersonalBusinessPropertyDeletedEvent;
import com.zcomapproach.garden.peony.view.events.PersonalPropertyDeletedEvent;
import com.zcomapproach.garden.peony.view.events.TlcLicenseDeletedEvent;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.entity.G02PersonalBusinessProperty;
import com.zcomapproach.garden.persistence.entity.G02PersonalProperty;
import com.zcomapproach.garden.persistence.entity.G02TlcLicense;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.openide.util.Exceptions;

/**
 *
 * @author zhijun98
 */
public class TaxpayerOtherInformationController extends PeonyTaxpayerServiceController implements PeonyFaceEventListener{
    @FXML
    private HBox otherButtonsHBox;
    @FXML
    private TabPane othersTabPane;
    
    private final PeonyTaxpayerCase targetPeonyTaxpayerCase;
    
    private final HashMap<String, TaxpayerScheduleCController> taxpayerScheduleCControllerStorage = new HashMap<>();
    private final HashMap<String, TaxpayerScheduleEController> taxpayerScheduleEControllerStorage = new HashMap<>();
    private final HashMap<String, TaxpayerTlcDriverController> taxpayerTlcDriverControllerStorage = new HashMap<>();

    public TaxpayerOtherInformationController(PeonyTaxpayerCase targetPeonyTaxpayerCase) {
        super(targetPeonyTaxpayerCase);
        this.targetPeonyTaxpayerCase = targetPeonyTaxpayerCase;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //otherButtonsFlowPane
        otherButtonsHBox.getChildren().addAll(constructOtherFunctionalButtons());
        
        othersTabPane.getTabs().clear();
        
        //G02PersonalBusinessProperty
        List<G02PersonalBusinessProperty> aG02PersonalBusinessPropertyList = targetPeonyTaxpayerCase.getPersonalBusinessPropertyList();
        if ((aG02PersonalBusinessPropertyList != null) && (!aG02PersonalBusinessPropertyList.isEmpty())){
            for (G02PersonalBusinessProperty aG02PersonalBusinessProperty : aG02PersonalBusinessPropertyList){
                addPersonalBusinessPropertyTab(aG02PersonalBusinessProperty);
            }
        }
        
        //G02PersonalProperty
        List<G02PersonalProperty> aG02PersonalPropertyList = targetPeonyTaxpayerCase.getPersonalPropertyList();
        if ((aG02PersonalPropertyList != null) && (!aG02PersonalPropertyList.isEmpty())){
            for (G02PersonalProperty aG02PersonalProperty : aG02PersonalPropertyList){
                addPersonalPropertyTab(aG02PersonalProperty);
            }
        }
        
        //G02TlcLicense
        List<G02TlcLicense> aG02TlcLicenseList = targetPeonyTaxpayerCase.getTlcLicenseList();
        if ((aG02TlcLicenseList != null) && (!aG02TlcLicenseList.isEmpty())){
            for (G02TlcLicense aG02TlcLicense : aG02TlcLicenseList){
                addTlcLicenseTab(aG02TlcLicense);
            }
        }
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if(event instanceof PersonalBusinessPropertyDeletedEvent){
            removePersonalBusinessProperty(((PersonalBusinessPropertyDeletedEvent)event).getPersonalBusinessProperty());
        }else if(event instanceof TlcLicenseDeletedEvent){
            removeTlcLicense(((TlcLicenseDeletedEvent)event).getTlcLicense());
        }else if(event instanceof PersonalPropertyDeletedEvent){
            removePersonalProperty(((PersonalPropertyDeletedEvent)event).getPersonalProperty());
        }
    }

    private void removeTlcLicense(G02TlcLicense tlcLicense) {
        targetPeonyTaxpayerCase.removeTlcLicense(tlcLicense);
        try{
            removeTab(taxpayerTlcDriverControllerStorage.get(tlcLicense.getDriverUuid()).getOwnerTab());
            taxpayerTlcDriverControllerStorage.remove(tlcLicense.getDriverUuid());
        }catch (Exception ex){}
    }

    private void removePersonalBusinessProperty(G02PersonalBusinessProperty personalBusinessProperty) {
        targetPeonyTaxpayerCase.removePersonalBusinessProperty(personalBusinessProperty);
        try{
            removeTab(taxpayerScheduleCControllerStorage.get(personalBusinessProperty.getPersonalBusinessPropertyUuid()).getOwnerTab());
            taxpayerScheduleCControllerStorage.remove(personalBusinessProperty.getPersonalBusinessPropertyUuid());
        }catch (Exception ex){}
    }

    private void removePersonalProperty(G02PersonalProperty personalProperty) {
        targetPeonyTaxpayerCase.removePersonalProperty(personalProperty);
        try{
            removeTab(taxpayerScheduleEControllerStorage.get(personalProperty.getPersonalPropertyUuid()).getOwnerTab());
            taxpayerScheduleEControllerStorage.remove(personalProperty.getPersonalPropertyUuid());
        }catch (Exception ex){}
    }
    
    private void removeTab(final Tab tab) {
        if (Platform.isFxApplicationThread()){
            othersTabPane.getTabs().remove(tab);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    othersTabPane.getTabs().remove(tab);
                }
            });
        }
    }
    
    private List<Button> constructOtherFunctionalButtons(){
        List<Button> buttons = new ArrayList<>();
        
        Button btn = new Button("Schedule-C");
        btn.setPrefWidth(100.0);
        btn.setTooltip(new Tooltip("Add schedule C form"));
        btn.setGraphic(PeonyGraphic.getImageView("add.png"));
        btn.getStyleClass().add("peony-primary-button");
        btn.setOnAction((ActionEvent e) -> {
            addPersonalBusinessPropertyTab(null);
        });
        buttons.add(btn);
        
        btn = new Button("Schedule-E");
        btn.setPrefWidth(100.0);
        btn.setTooltip(new Tooltip("Add schedule-E form"));
        btn.setGraphic(PeonyGraphic.getImageView("add.png"));
        btn.getStyleClass().add("peony-primary-button");
        btn.setOnAction((ActionEvent e) -> {
            addPersonalPropertyTab(null);
        });
        buttons.add(btn);
        
        btn = new Button("TLC License");
        btn.setPrefWidth(100.0);
        btn.setTooltip(new Tooltip("Add TLC License"));
        btn.setGraphic(PeonyGraphic.getImageView("add.png"));
        btn.getStyleClass().add("peony-primary-button");
        btn.setOnAction((ActionEvent e) -> {
            addTlcLicenseTab(null);
        });
        buttons.add(btn);
        
        return buttons;
    }


    private void addPersonalBusinessPropertyTab(final G02PersonalBusinessProperty personalBusinessProperty) {
        if (Platform.isFxApplicationThread()){
            addPersonalBusinessPropertyPaneHelper(personalBusinessProperty);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    addPersonalBusinessPropertyPaneHelper(personalBusinessProperty);
                }
            });
        }
    }
    private void addPersonalBusinessPropertyPaneHelper(G02PersonalBusinessProperty personalBusinessProperty) {
        //this case is to create a brand new record for target taxpayer case
        if (personalBusinessProperty == null){
            personalBusinessProperty = new G02PersonalBusinessProperty();
            personalBusinessProperty.setPersonalBusinessPropertyUuid(ZcaUtils.generateUUIDString());
            personalBusinessProperty.setTaxpayerCaseUuid(getTargetPeonyTaxpayerCaseUuid());
            personalBusinessProperty.setBusinessPropertyName("");
            
            targetPeonyTaxpayerCase.getPersonalBusinessPropertyList().add(personalBusinessProperty);
        }
        try {
            Tab tab;
            if (ZcaValidator.isNullEmpty(personalBusinessProperty.getBusinessPropertyName())){
                tab = new Tab("Business Schedule C:");
            }else{
                tab = new Tab("Business Schedule C: " + personalBusinessProperty.getBusinessPropertyName());
            }
            TaxpayerScheduleCController aTaxpayerScheduleCController = new TaxpayerScheduleCController(
                    personalBusinessProperty, targetPeonyTaxpayerCase, true, true, false, tab);
            aTaxpayerScheduleCController.addPeonyFaceEventListenerList(this.getPeonyFaceEventListenerList());
            aTaxpayerScheduleCController.addPeonyFaceEventListener(this);
            
            Pane aPane = aTaxpayerScheduleCController.loadFxml();
            aPane.prefHeightProperty().bind(othersTabPane.heightProperty());
            aPane.prefWidthProperty().bind(othersTabPane.widthProperty());
            tab.setContent(aPane);
            
            othersTabPane.getTabs().add(tab);
            othersTabPane.getSelectionModel().select(tab);
            taxpayerScheduleCControllerStorage.put(personalBusinessProperty.getPersonalBusinessPropertyUuid(), aTaxpayerScheduleCController);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    private void addPersonalPropertyTab(final G02PersonalProperty personalProperty) {
        if (Platform.isFxApplicationThread()){
            addPersonalPropertyPaneHelper(personalProperty);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    addPersonalPropertyPaneHelper(personalProperty);
                }
            });
        }
    }
    
    private void addPersonalPropertyPaneHelper(G02PersonalProperty personalProperty) {
        //this case is to create a brand new record for target taxpayer case
        if (personalProperty == null){
            personalProperty = new G02PersonalProperty();
            personalProperty.setPersonalPropertyUuid(ZcaUtils.generateUUIDString());
            personalProperty.setTaxpayerCaseUuid(getTargetPeonyTaxpayerCaseUuid());
            personalProperty.setPropertyAddress("");
            
            targetPeonyTaxpayerCase.getPersonalPropertyList().add(personalProperty);
        }
        try {
            Tab tab;
            if (ZcaValidator.isNullEmpty(personalProperty.getPropertyAddress())){
                tab = new Tab("Personal Schedule E:");
            }else{
                tab = new Tab("Personal Schedule E: " + personalProperty.getPropertyAddress());
            }
            TaxpayerScheduleEController aTaxpayerScheduleEController = new TaxpayerScheduleEController(
                    personalProperty, targetPeonyTaxpayerCase, true, true, false, tab);
            aTaxpayerScheduleEController.addPeonyFaceEventListenerList(this.getPeonyFaceEventListenerList());
            aTaxpayerScheduleEController.addPeonyFaceEventListener(this);
            
            Pane aPane = aTaxpayerScheduleEController.loadFxml();
            aPane.prefHeightProperty().bind(othersTabPane.heightProperty());
            aPane.prefWidthProperty().bind(othersTabPane.widthProperty());
            tab.setContent(aPane);
            
            othersTabPane.getTabs().add(tab);
            othersTabPane.getSelectionModel().select(tab);
            taxpayerScheduleEControllerStorage.put(personalProperty.getPersonalPropertyUuid(), aTaxpayerScheduleEController);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    private void addTlcLicenseTab(final G02TlcLicense tlcLicense) {
        if (Platform.isFxApplicationThread()){
            addTlcLicensePaneHelper(tlcLicense);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    addTlcLicensePaneHelper(tlcLicense);
                }
            });
        }
    }
    private void addTlcLicensePaneHelper(G02TlcLicense tlcLicense) {
        //this case is to create a brand new record for target taxpayer case
        if (tlcLicense == null){
            tlcLicense = new G02TlcLicense();
            tlcLicense.setDriverUuid(ZcaUtils.generateUUIDString());
            tlcLicense.setTaxpayerCaseUuid(getTargetPeonyTaxpayerCaseUuid());
            tlcLicense.setTlcLicense("");
            
            targetPeonyTaxpayerCase.getTlcLicenseList().add(tlcLicense);
        }
        try {
            Tab tab;
            if (ZcaValidator.isNullEmpty(tlcLicense.getTlcLicense())){
                tab = new Tab("Taxpayer's TLC");
            }else{
                tab = new Tab("Taxpayer's TLC: " + tlcLicense.getTlcLicense());
            }
            TaxpayerTlcDriverController aTaxpayerTlcDriverController = new TaxpayerTlcDriverController(
                    tlcLicense, targetPeonyTaxpayerCase, true, true, false, tab);
            aTaxpayerTlcDriverController.addPeonyFaceEventListenerList(this.getPeonyFaceEventListenerList());
            aTaxpayerTlcDriverController.addPeonyFaceEventListener(this);
            
            Pane aPane = aTaxpayerTlcDriverController.loadFxml();
            aPane.prefHeightProperty().bind(othersTabPane.heightProperty());
            aPane.prefWidthProperty().bind(othersTabPane.widthProperty());
            tab.setContent(aPane);
            
            othersTabPane.getTabs().add(tab);
            othersTabPane.getSelectionModel().select(tab);
            taxpayerTlcDriverControllerStorage.put(tlcLicense.getDriverUuid(), aTaxpayerTlcDriverController);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    private String getTargetPeonyTaxpayerCaseUuid(){
        return targetPeonyTaxpayerCase.getTaxpayerCase().getTaxpayerCaseUuid();
    }
    
    protected void loadTargetTaxpayerCaseOtherInformation() throws ZcaEntityValidationException {
//        List<TaxpayerDataEntryController> aTaxpayerInfoDataEntryPaneControllerList = new ArrayList<>(taxpayerDataEntryControllerStorage.values());
//        for (TaxpayerDataEntryController aTaxpayerInfoDataEntryPaneController : aTaxpayerInfoDataEntryPaneControllerList){
//            try{
//                aTaxpayerInfoDataEntryPaneController.loadTargetTaxpayerInfo();
//            }catch (ZcaEntityValidationException ex){
//                othersTabPane.getSelectionModel().select(aTaxpayerInfoDataEntryPaneController.getOwnerTab());
//                throw ex;
//            }
//        }
        List<TaxpayerScheduleCController> aTaxpayerScheduleCControllerList = new ArrayList<>(taxpayerScheduleCControllerStorage.values());
        for (TaxpayerScheduleCController aTaxpayerScheduleCController : aTaxpayerScheduleCControllerList){
            try{
                aTaxpayerScheduleCController.loadTargetPersonalBusinessProperty();
            }catch (ZcaEntityValidationException ex){
                othersTabPane.getSelectionModel().select(aTaxpayerScheduleCController.getOwnerTab());
                throw ex;
            }
        }
        List<TaxpayerScheduleEController> aTaxpayerScheduleEControllerList = new ArrayList<>(taxpayerScheduleEControllerStorage.values());
        for (TaxpayerScheduleEController aTaxpayerScheduleEController : aTaxpayerScheduleEControllerList){
            try{
                aTaxpayerScheduleEController.loadTargetPersonalProperty();
            }catch (ZcaEntityValidationException ex){
                othersTabPane.getSelectionModel().select(aTaxpayerScheduleEController.getOwnerTab());
                throw ex;
            }
        }
        List<TaxpayerTlcDriverController> aTaxpayerTlcDriverControllerList = new ArrayList<>(taxpayerTlcDriverControllerStorage.values());
        for (TaxpayerTlcDriverController aTaxpayerTlcDriverController : aTaxpayerTlcDriverControllerList){
            try{
                aTaxpayerTlcDriverController.loadTargetTlcLicense();
            }catch (ZcaEntityValidationException ex){
                othersTabPane.getSelectionModel().select(aTaxpayerTlcDriverController.getOwnerTab());
                throw ex;
            }
        }
    }

    @Override
    protected void resetDataEntryStyleHelper() {
        List<TaxpayerScheduleCController> aTaxpayerScheduleCControllerList = new ArrayList<>(taxpayerScheduleCControllerStorage.values());
        for (TaxpayerScheduleCController aTaxpayerScheduleCController : aTaxpayerScheduleCControllerList){
            aTaxpayerScheduleCController.resetDataEntryStyle();
        }
        List<TaxpayerScheduleEController> aTaxpayerScheduleEControllerList = new ArrayList<>(taxpayerScheduleEControllerStorage.values());
        for (TaxpayerScheduleEController aTaxpayerScheduleEController : aTaxpayerScheduleEControllerList){
            aTaxpayerScheduleEController.resetDataEntryStyle();
        }
        List<TaxpayerTlcDriverController> aTaxpayerTlcDriverControllerList = new ArrayList<>(taxpayerTlcDriverControllerStorage.values());
        for (TaxpayerTlcDriverController aTaxpayerTlcDriverController : aTaxpayerTlcDriverControllerList){
            aTaxpayerTlcDriverController.resetDataEntryStyle();
        }
    }
}
