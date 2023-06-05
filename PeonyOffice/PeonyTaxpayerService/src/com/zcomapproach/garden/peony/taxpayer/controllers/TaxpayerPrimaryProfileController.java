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
import com.zcomapproach.garden.data.constant.GardenAgreement;
import com.zcomapproach.garden.data.constant.UState;
import com.zcomapproach.garden.exception.EntityField;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import com.zcomapproach.garden.peony.security.PeonyPrivilege;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.peony.view.events.CloseTopComponentRequest;
import com.zcomapproach.garden.persistence.G02EntityValidator;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.constant.GardenTaxpayerCaseStatus;
import com.zcomapproach.garden.persistence.constant.TaxpayerFederalFilingStatus;
import com.zcomapproach.garden.persistence.entity.G02Location;
import com.zcomapproach.garden.persistence.entity.G02Log;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerCase;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCaseList;
import com.zcomapproach.garden.persistence.peony.data.PeonyLogName;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.garden.taxation.TaxationSettings;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaText;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.resources.css.PeonyCss;
import com.zcomapproach.garden.peony.taxpayer.events.AddDependentRequest;
import com.zcomapproach.garden.peony.taxpayer.events.TaxpayerCaseWorkStatusLoaded;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.events.TaxpayerInfoDataEntryDeletedEvent;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.constant.TaxpayerRelationship;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerInfo;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import javax.swing.JOptionPane;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class TaxpayerPrimaryProfileController extends PeonyTaxpayerServiceController implements PeonyFaceEventListener {
    @FXML
    private ScrollPane primaryProfileScrollPane;
    @FXML
    private VBox primaryProfileVBox;
    @FXML
    private Label federalFilingStatusLabel;
    @FXML
    private Label bankRoutingNumberLabel;
    @FXML
    private Label bankAccountNumberLabel;
////    @FXML
////    private Label bankNoteLabel;
////    @FXML
////    private Label contactInfoLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label cityLabel;
    @FXML
    private Label countyLabel;
    @FXML
    private Label stateLabel;
    @FXML
    private Label zipCodeLabel;
    @FXML
    private JFXComboBox<String> caseStatusComboBox;
    @FXML
    private DatePicker taxReturnDeadlineDatePicker;
    @FXML
    private DatePicker taxReturnExtensionDatePicker;
    @FXML
    private JFXComboBox<String> federalFilingStatusComboBox;
    @FXML
    private JFXTextField bankRoutingNumberField;
    @FXML
    private JFXTextField bankAccountNumberField;
////    @FXML
////    private JFXTextField bankNoteField;
////    @FXML
////    private JFXTextField contactInfoField;
    @FXML
    private JFXTextField addressField;
    @FXML
    private JFXTextField cityField;
    @FXML
    private JFXTextField countyField;
    @FXML
    private JFXComboBox<String> stateComboBox;
    @FXML
    private JFXTextField zipCodeField;
    //@FXML
    //private JFXTextField countryField;
////    @FXML
////    private JFXTextField residencyField;
    @FXML
    private Button saveButton;
    @FXML
    private Button deleteButton;
    
    private Button addDependentButton;
    
    private TaxpayerMainEntryController primaryTaxpayerDataEntryController = null;
    private TaxpayerMainEntryController spouseTaxpayerDataEntryController = null;
    
    private final HashMap<String, TaxpayerDependentEntryController> taxpayerDependentEntryControllerStorage = new HashMap<>();
    private final List<Pane> nonPrimaryPaneList = new ArrayList<>();    //help to hide these panes in the case of "SINGLE"
    
    private final PeonyTaxpayerCase targetPeonyTaxpayerCase;
    
    private final ComboBox<PeonyTaxpayerCase> compareLagacyComboBox;
    
    private Pane spousePane;
    
    /**
     * Always keep the latest log record for targetPeonyTaxpayerCase's wokr status 
     * in the database. It could be NULL.
     */
    private G02Log currentTargetPeonyTaxpayerCaseStatusLog;
    
    public TaxpayerPrimaryProfileController(PeonyTaxpayerCase targetPeonyTaxpayerCase, ComboBox<PeonyTaxpayerCase> compareComboBox) {
        super(targetPeonyTaxpayerCase);
        this.targetPeonyTaxpayerCase = targetPeonyTaxpayerCase;
        this.compareLagacyComboBox = compareComboBox;
        currentTargetPeonyTaxpayerCaseStatusLog = targetPeonyTaxpayerCase.getLatestTaxpayerCaseStatus();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addDependentButton = new Button("Add Dependent");
        addDependentButton.setPrefWidth(105.0);
        addDependentButton.setPrefHeight(25.0);
        //addDependentButton.setStyle("-fx-font-size: 12");
        addDependentButton.getStyleClass().add("peony-success-regular-button");
        addDependentButton.setOnAction((ActionEvent e) -> {
            handleAddDependentRequest();
        });
        
        saveButton.setPrefWidth(100.0);
        saveButton.setPrefHeight(25.0);
        saveButton.setStyle("-fx-font-size: 12");
        
        deleteButton.setPrefWidth(100.0);
        deleteButton.setPrefHeight(25.0);
        deleteButton.setStyle("-fx-font-size: 12");
        
        saveButton.setDisable(true);
        caseStatusComboBox.setDisable(true);
        loadWorkStatus();
        
        initializeBasicInformationPanelHelper();
        initializePrimaryAndSpouseDataEntryControllersHelper(TaxpayerRelationship.PRIMARY_TAXPAYER);
        initializePrimaryAndSpouseDataEntryControllersHelper(TaxpayerRelationship.SPOUSE_TAXPAYER);
        initializeDependantDataEntryControllers();
    }
    
    
    /**
     * Help load the FXML file for controllers
     * @param aFXMLLoader
     * @return
     * @throws IOException 
     */
    @Override
    protected Pane loadFxmlHelper(FXMLLoader aFXMLLoader) throws IOException{
        return (Pane)aFXMLLoader.load();
    }

    protected void launchCompareComboBoxInitialization(final PeonyTaxpayerCase targetPeonyTaxpayerCase, 
                                                       final Label newClientLabel,
                                                       final HBox topButtonsHBox) 
    {   
        compareLagacyComboBox.valueProperty().addListener((obs, oldValue, newValue) -> populateLegacyPeonyTaxpayerCaseComparison(newValue));
        Task<List<PeonyTaxpayerCase>> task = new Task<List<PeonyTaxpayerCase>>(){
            @Override
            protected List<PeonyTaxpayerCase> call() throws Exception {
                List<PeonyTaxpayerCase> legacyPeonyTaxpayerCaseList = targetPeonyTaxpayerCase.getLegacyPeonyTaxpayerCaseList();
                if (legacyPeonyTaxpayerCaseList == null){
                    legacyPeonyTaxpayerCaseList = new ArrayList<>();
                }
                if (legacyPeonyTaxpayerCaseList.isEmpty()){
                    if (targetPeonyTaxpayerCase.getCustomer() != null){
                        PeonyTaxpayerCaseList aPeonyTaxpayerCaseList = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient()
                                .findEntity_XML(PeonyTaxpayerCaseList.class, 
                                        GardenRestParams.Taxpayer.findBasicPeonyTaxpayerCaseListByPrimaryTaxpayerSsnRestParams(
                                                targetPeonyTaxpayerCase.getCustomer().getSsn()));
                        if ((aPeonyTaxpayerCaseList == null) || (aPeonyTaxpayerCaseList.getPeonyTaxpayerCaseList() == null)){
                            return new ArrayList<>();
                        }else{
                            return aPeonyTaxpayerCaseList.getPeonyTaxpayerCaseList();
                        }
                    }
                }
                        
                compareLagacyComboBox.getItems().addListener(new ListChangeListener<PeonyTaxpayerCase>(){
                    @Override
                    public void onChanged(ListChangeListener.Change<? extends PeonyTaxpayerCase> event) {
                        int fromIndex = event.getFrom();
                        int toIndex = event.getTo();
                        if (toIndex != fromIndex){
                            populateLegacyPeonyTaxpayerCaseComparison(compareLagacyComboBox.getItems().get(toIndex));
                        }
                    }
                });
                return legacyPeonyTaxpayerCaseList;
            }

            @Override
            protected void succeeded() {
                try {
                    List<PeonyTaxpayerCase> legacyPeonyTaxpayerCaseList = get();
                    if (legacyPeonyTaxpayerCaseList.isEmpty()){
                        topButtonsHBox.getChildren().remove(compareLagacyComboBox);
                        newClientLabel.setVisible(true);
                    }else{
                        Collections.sort(legacyPeonyTaxpayerCaseList, new Comparator<PeonyTaxpayerCase>(){
                            @Override
                            public int compare(PeonyTaxpayerCase o1, PeonyTaxpayerCase o2) {
                                return o1.getTaxpayerCase().getDeadline().compareTo(o2.getTaxpayerCase().getDeadline())*(-1);
                            }
                        });
                        Date targetDeadline = targetPeonyTaxpayerCase.getTaxpayerCase().getDeadline();
                        String targetEntityStatus = targetPeonyTaxpayerCase.getTaxpayerCase().getEntityStatus();
                        PeonyTaxpayerCase selectedPeonyTaxpayerCase = null; //saved by users before according to targetEntityStatus
                        for (PeonyTaxpayerCase legacyPeonyTaxpayerCase : legacyPeonyTaxpayerCaseList){
                            if (targetDeadline.compareTo(legacyPeonyTaxpayerCase.getTaxpayerCase().getDeadline()) != 0){
                                if ((targetEntityStatus != null) && (targetEntityStatus.equalsIgnoreCase(legacyPeonyTaxpayerCase.getTaxpayerCaseTitle(false)))){
                                    selectedPeonyTaxpayerCase = legacyPeonyTaxpayerCase;
                                }
                                compareLagacyComboBox.getItems().add(legacyPeonyTaxpayerCase);
                                if (isPreviousYear(targetPeonyTaxpayerCase, legacyPeonyTaxpayerCase)){
                                    publishPreviousYearBillPayments(legacyPeonyTaxpayerCase);
                                }
                            }
                        }
                        if (compareLagacyComboBox.getItems().isEmpty()){
                            topButtonsHBox.getChildren().remove(compareLagacyComboBox);
                            newClientLabel.setVisible(true);
                        }else{
                            topButtonsHBox.getChildren().remove(newClientLabel);
                            compareLagacyComboBox.setVisible(true);
                            if (selectedPeonyTaxpayerCase == null){
                                //default: compare to the previous year
                                selectedPeonyTaxpayerCase = selectPreviousYearTaxpayerCase(targetPeonyTaxpayerCase);
                            }
                            compareLagacyComboBox.getSelectionModel().select(selectedPeonyTaxpayerCase);
                            populateLegacyPeonyTaxpayerCaseComparison(selectedPeonyTaxpayerCase);
                        }
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Failed to initialize legacy taxpayer case list. " + ex.getMessage());
                }
            }

            private PeonyTaxpayerCase selectPreviousYearTaxpayerCase(PeonyTaxpayerCase targetPeonyTaxpayerCase) {
                int year = ZcaCalendar.parseCalendarField(targetPeonyTaxpayerCase.getTaxpayerCase().getDeadline(), Calendar.YEAR);
                Collection<PeonyTaxpayerCase> aPeonyTaxpayerCaseCollection = compareLagacyComboBox.getItems();
                PeonyTaxpayerCase result = null;
                int yearResult;
                for (PeonyTaxpayerCase aPeonyTaxpayerCase : aPeonyTaxpayerCaseCollection){
                    yearResult = ZcaCalendar.parseCalendarField(aPeonyTaxpayerCase.getTaxpayerCase().getDeadline(), Calendar.YEAR);
                    if (year - yearResult == 1){
                        result = aPeonyTaxpayerCase;
                        break;
                    }
                }//for-loop
                if (result == null){
                    return targetPeonyTaxpayerCase;
                }
                return result;
            }
        };
        this.getCachedThreadPoolExecutorService().submit(task);
    }

    private boolean isPreviousYear(PeonyTaxpayerCase targetPeonyTaxpayerCase, PeonyTaxpayerCase legacyPeonyTaxpayerCase) {
        int targetYear = ZcaCalendar.parseCalendarField(targetPeonyTaxpayerCase.getTaxpayerCase().getDeadline(), Calendar.YEAR);
        int legacyYear = ZcaCalendar.parseCalendarField(legacyPeonyTaxpayerCase.getTaxpayerCase().getDeadline(), Calendar.YEAR);
        return (targetYear - legacyYear) == 1;
    }
    
    private void handleAddDependentRequest() {
        addTaxpayerDependentEntryPane(null);
        getCachedThreadPoolExecutorService().submit(new Task<Void>(){
            @Override
            protected Void call() throws Exception {
                Thread.sleep(100);
                return null;
            }
            @Override
            protected void succeeded() {
                primaryProfileScrollPane.setVvalue(primaryProfileScrollPane.getVmax());
            }
        });
    }
    
    private void initializeDependantDataEntryControllers(){
        //display penels for dependents
        List<G02TaxpayerInfo> aG02TaxpayerInfoList = targetPeonyTaxpayerCase.retrieveDependentTaxpayerInfoList();
        if ((aG02TaxpayerInfoList != null) && (!aG02TaxpayerInfoList.isEmpty())){
            for (G02TaxpayerInfo aG02TaxpayerInfo : aG02TaxpayerInfoList){
                addTaxpayerDependentEntryPane(aG02TaxpayerInfo);
            }
        }
    }

    private void addTaxpayerDependentEntryPane(final G02TaxpayerInfo aG02TaxpayerInfo){
        if (Platform.isFxApplicationThread()){
            addTaxpayerDependentEntryPaneHelper(aG02TaxpayerInfo);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    addTaxpayerDependentEntryPaneHelper(aG02TaxpayerInfo);
                }
            });
        }
    }
    
    private void addTaxpayerDependentEntryPaneHelper(G02TaxpayerInfo aG02TaxpayerInfo) {
        //this case is to create a brand new record for target taxpayer case
        if (aG02TaxpayerInfo == null){
            aG02TaxpayerInfo = new G02TaxpayerInfo();
            aG02TaxpayerInfo.setTaxpayerUserUuid(ZcaUtils.generateUUIDString());
            aG02TaxpayerInfo.setSsn("");
            aG02TaxpayerInfo.setTaxpayerCaseUuid(getTargetPeonyTaxpayerCaseUuid());
            
            targetPeonyTaxpayerCase.getTaxpayerInfoList().add(aG02TaxpayerInfo);
        }
        try {
            //Tab tab = new Tab("Dependent SSN: " + aG02TaxpayerInfo.getSsn());
            TaxpayerDependentEntryController aTaxpayerDependentEntryController = new TaxpayerDependentEntryController(
                    targetPeonyTaxpayerCase, aG02TaxpayerInfo, true, true, false);
            aTaxpayerDependentEntryController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
            aTaxpayerDependentEntryController.addPeonyFaceEventListener(this);
            
            Pane aPane = aTaxpayerDependentEntryController.loadFxml();
            primaryProfileVBox.getChildren().add(aPane);
            nonPrimaryPaneList.add(aPane);
            displayNonPrimaryPaneHelper(aPane, targetPeonyTaxpayerCase.getTaxpayerCase().getFederalFilingStatus());
            
            taxpayerDependentEntryControllerStorage.put(aG02TaxpayerInfo.getTaxpayerUserUuid(), aTaxpayerDependentEntryController);
            listenToCompareLegacyComboBox(aTaxpayerDependentEntryController);
            
            pluginAddDependentButton();
        } catch (IOException ex) {
            //Exceptions.printStackTrace(ex);
        }
    }
    
    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if(event instanceof TaxpayerInfoDataEntryDeletedEvent){
            removeTaxpayerInfoDataEntry(((TaxpayerInfoDataEntryDeletedEvent)event).getTaxpayerInfo());
        }else if (event instanceof AddDependentRequest){
            handleAddDependentRequest();
        }
    }

    private void removeTaxpayerInfoDataEntry(final G02TaxpayerInfo taxpayerInfo) {
        if (Platform.isFxApplicationThread()){
            removeTaxpayerInfoDataEntryHelper(taxpayerInfo);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    removeTaxpayerInfoDataEntryHelper(taxpayerInfo);
                }
            });
        }
    }
    private void removeTaxpayerInfoDataEntryHelper(final G02TaxpayerInfo taxpayerInfo) {
        targetPeonyTaxpayerCase.removeTaxpayerInfo(taxpayerInfo);
        try{
            TaxpayerDependentEntryController aTaxpayerDependentEntryController = taxpayerDependentEntryControllerStorage.get(taxpayerInfo.getTaxpayerUserUuid());
            if (aTaxpayerDependentEntryController != null){
                Pane aPane = aTaxpayerDependentEntryController.getRootPane();
                removeTitledPane(aPane);
                nonPrimaryPaneList.remove(aPane);
                
                notListenToCompareLegacyComboBox(aTaxpayerDependentEntryController);
            }
            taxpayerDependentEntryControllerStorage.remove(taxpayerInfo.getTaxpayerUserUuid());
        }catch (Exception ex){}
    }
    
    private void removeTitledPane(final Pane titledPane) {
        if (Platform.isFxApplicationThread()){
            primaryProfileVBox.getChildren().remove(titledPane);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    primaryProfileVBox.getChildren().remove(titledPane);
                }
            });
        }
    }
    
    private void saveTaxpayerBasicProfileData() {
        Task<Pair<PeonyTaxpayerCaseTabPaneController, ZcaEntityValidationException>> storeTargetTaxpayerCaseTask = new Task<Pair<PeonyTaxpayerCaseTabPaneController, ZcaEntityValidationException>>(){
            @Override
            protected Pair<PeonyTaxpayerCaseTabPaneController, ZcaEntityValidationException> call() throws Exception {
                //(0)Validation...
                if (!targetPeonyTaxpayerCase.isNewEntity()){
                    validateProfileData();
                }
                //(1) load and validate targetTaxpayerCase & Taxpayer info
                loadTaxpayerProfileData();
                //(2) save targetTaxpayerCase
                try{
                    PeonyTaxpayerCase result = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient()
                        .storeEntity_XML(PeonyTaxpayerCase.class, 
                                        GardenRestParams.Taxpayer.storePeonyTaxpayerCaseRestParams(),
                                        targetPeonyTaxpayerCase);
                    if (result == null){
                        return new Pair<>(null, new ZcaEntityValidationException("Failed to save this taxpayer."));
                    }else{
                        saveTaxpayerInfoData();
                    }
                    //successfully
                    return null;
                }catch(Exception ex){
                    return new Pair<>(null, new ZcaEntityValidationException("Failed to save this taxpayer. " + ex.getMessage()));
                }
            }

            @Override
            protected void failed() {
                Throwable ex = this.getException();
                if (ex instanceof ZcaEntityValidationException){
                    PeonyFaceUtils.displayErrorMessageDialog(((ZcaEntityValidationException)ex).getMessage());
                }else{
                    PeonyFaceUtils.displayErrorMessageDialog("Technical error was raised. Cannot save this taxpayer case.");
                }
            }

            @Override
            protected void succeeded() {
                try {
                    Pair<PeonyTaxpayerCaseTabPaneController, ZcaEntityValidationException> result = get();
                    if (result == null){
                        resetDataEntryStyle();
                        setDataEntryChanged(false);
                        PeonyFaceUtils.displayInformationMessageDialog("Successfully saved this taxpayer case.");

                        G02Log log = createNewG02LogInstance(PeonyLogName.STORED_TAXPAYER_CASE);
                        log.setLoggedEntityType(GardenEntityType.TAXPAYER_CASE.name());
                        log.setLoggedEntityUuid(targetPeonyTaxpayerCase.getTaxpayerCase().getTaxpayerCaseUuid());
                        PeonyProperties.getSingleton().log(log);
                        
                        populateLegacyPeonyTaxpayerCaseComparison(compareLagacyComboBox.getValue());
                    }else{
                        ZcaEntityValidationException aEntityValidationException = result.getValue();
                        //display error message
                        PeonyFaceUtils.displayErrorMessageDialog(aEntityValidationException.getMessage());
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    //Exceptions.printStackTrace(ex);
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(storeTargetTaxpayerCaseTask);
    }


    private void validateProfileData() throws ZcaEntityValidationException{
        if (ZcaValidator.isNullEmpty(caseStatusComboBox.getValue())){
            throw new ZcaEntityValidationException("Please select a working status for this case before save this record.");
        }
        if (ZcaValidator.isNullEmpty(federalFilingStatusComboBox.getValue())){
            throw new ZcaEntityValidationException("Please input federal filing status for this case before save this record.");
        }
        if (ZcaValidator.isNullEmpty(bankRoutingNumberField.getText())){
            throw new ZcaEntityValidationException("Please input bank routing number for this case before save this record.");
        }
        if (ZcaValidator.isNullEmpty(bankAccountNumberField.getText())){
            throw new ZcaEntityValidationException("Please input bank account number for this case before save this record.");
        }
        
        if ((!TaxpayerFederalFilingStatus.Single.value().equalsIgnoreCase(federalFilingStatusComboBox.getValue()))
                && (!TaxpayerFederalFilingStatus.HeadOfHousehold.value().equalsIgnoreCase(federalFilingStatusComboBox.getValue()))){
            if (spouseTaxpayerDataEntryController != null){
                spouseTaxpayerDataEntryController.validateProfileData();
            }
        }
        
        if ((!TaxpayerFederalFilingStatus.Single.value().equalsIgnoreCase(federalFilingStatusComboBox.getValue()))){
            if ((taxpayerDependentEntryControllerStorage != null) && (!taxpayerDependentEntryControllerStorage.isEmpty())){
                List<TaxpayerDependentEntryController> aTaxpayerDependentEntryControllerList = new ArrayList<>(taxpayerDependentEntryControllerStorage.values());
                for (TaxpayerDependentEntryController aTaxpayerDependentEntryController : aTaxpayerDependentEntryControllerList){
                    aTaxpayerDependentEntryController.validateProfileData();
                }
            }
        }
        
        if (primaryTaxpayerDataEntryController != null){
            if (!targetPeonyTaxpayerCase.isNewEntity()){
                if (primaryTaxpayerDataEntryController.hasNoContactInformation()){
                    if ((spouseTaxpayerDataEntryController == null) || (spouseTaxpayerDataEntryController.hasNoContactInformation())){
                        throw new ZcaEntityValidationException("Please input contact information for this primary or spouse taxpayer before save this record.");
                    }
                }
            }
            primaryTaxpayerDataEntryController.validateProfileData();
        }
        
        //check if the legacy data was compared
        if (!compareLagacyComboBox.getItems().isEmpty()){
            if ((compareLagacyComboBox.getValue() == null)){
                compareLagacyComboBox.setStyle(PeonyFaceUtils.WARNING_STYLE);
                throw new ZcaEntityValidationException("You did not confirm this data entry with legacy year's data before you save it.");
            }else{
                String status = compareLagacyComboBox.getValue().getTaxpayerCaseTitle(false);
                targetPeonyTaxpayerCase.getTaxpayerCase().setEntityStatus(status);
            }
        }
        compareLagacyComboBox.setStyle(null);
    }
    private void loadTaxpayerProfileData() throws ZcaEntityValidationException {
        G02TaxpayerCase aG02TaxpayerCase = targetPeonyTaxpayerCase.getTaxpayerCase();
        aG02TaxpayerCase.setDeadline(ZcaCalendar.convertToDate(taxReturnDeadlineDatePicker.getValue()));
        aG02TaxpayerCase.setExtension(ZcaCalendar.convertToDate(taxReturnExtensionDatePicker.getValue()));
        aG02TaxpayerCase.setBankAccountNumber(bankAccountNumberField.getText());
        aG02TaxpayerCase.setBankRoutingNumber(bankRoutingNumberField.getText());
        aG02TaxpayerCase.setFederalFilingStatus(federalFilingStatusComboBox.getValue());
////        aG02TaxpayerCase.setBankNote(bankNoteField.getText());
////        aG02TaxpayerCase.setContact(contactInfoField.getText());
////        aG02TaxpayerCase.setMemo(residencyField.getText());
        if (ZcaValidator.isNullEmpty(aG02TaxpayerCase.getTaxpayerCaseUuid())){
            aG02TaxpayerCase.setTaxpayerCaseUuid(ZcaUtils.generateUUIDString());
        }
        aG02TaxpayerCase.setAgreementUuid(GardenAgreement.TaxpayerCaseAgreement.value());
        
        G02Location aG02Location = targetPeonyTaxpayerCase.getPrimaryLocation();
        if (ZcaValidator.isNullEmpty(aG02Location.getLocationUuid())){
            aG02Location.setLocationUuid(ZcaUtils.generateUUIDString());
        }
        
        aG02Location.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
        aG02Location.setEntityUuid(aG02TaxpayerCase.getTaxpayerCaseUuid());
        aG02Location.setLocalAddress(addressField.getText());
        aG02Location.setCityName(cityField.getText());
        aG02Location.setStateCounty(countyField.getText());
        aG02Location.setStateName(stateComboBox.getValue());
        aG02Location.setZipCode(zipCodeField.getText());
        //aG02Location.setCountry(countryField.getText());
        
        try{
            G02EntityValidator.getSingleton().validate(aG02TaxpayerCase);
            G02EntityValidator.getSingleton().validate(aG02Location);
        }catch(ZcaEntityValidationException ex){
            highlightBadEntityField(ex);
            throw ex;
        }
        
        if (primaryTaxpayerDataEntryController != null){
            primaryTaxpayerDataEntryController.loadTargetTaxpayerInfo();
        }
        
        if ((!TaxpayerFederalFilingStatus.Single.value().equalsIgnoreCase(federalFilingStatusComboBox.getValue()))
                && (!TaxpayerFederalFilingStatus.HeadOfHousehold.value().equalsIgnoreCase(federalFilingStatusComboBox.getValue()))){
            if (spouseTaxpayerDataEntryController != null){
                spouseTaxpayerDataEntryController.loadTargetTaxpayerInfo();
            }
        }
        
        if ((!TaxpayerFederalFilingStatus.Single.value().equalsIgnoreCase(federalFilingStatusComboBox.getValue()))){
            if ((taxpayerDependentEntryControllerStorage != null) && (!taxpayerDependentEntryControllerStorage.isEmpty())){
                List<TaxpayerDependentEntryController> aTaxpayerDependentEntryControllerList = new ArrayList<>(taxpayerDependentEntryControllerStorage.values());
                for (TaxpayerDependentEntryController aTaxpayerDependentEntryController : aTaxpayerDependentEntryControllerList){
                    aTaxpayerDependentEntryController.loadTargetTaxpayerInfo();
                }
            }
        }
    
    }
    
    private void saveTaxpayerInfoData() throws Exception{
        if (primaryTaxpayerDataEntryController != null){
            primaryTaxpayerDataEntryController.saveProfile();
        }
        
        if ((!TaxpayerFederalFilingStatus.Single.value().equalsIgnoreCase(federalFilingStatusComboBox.getValue()))
                && (!TaxpayerFederalFilingStatus.HeadOfHousehold.value().equalsIgnoreCase(federalFilingStatusComboBox.getValue()))){
            if (spouseTaxpayerDataEntryController != null){
                spouseTaxpayerDataEntryController.saveProfile();
            }
        }
        
        if ((!TaxpayerFederalFilingStatus.Single.value().equalsIgnoreCase(federalFilingStatusComboBox.getValue()))){
            if ((taxpayerDependentEntryControllerStorage != null) && (!taxpayerDependentEntryControllerStorage.isEmpty())){
                List<TaxpayerDependentEntryController> aTaxpayerDependentEntryControllerList = new ArrayList<>(taxpayerDependentEntryControllerStorage.values());
                for (TaxpayerDependentEntryController aTaxpayerDependentEntryController : aTaxpayerDependentEntryControllerList){
                    aTaxpayerDependentEntryController.saveProfile();
                }
            }
        }
    
    }

    private void deleteTargetPeonyTaxpayerCase() {
        this.getCachedThreadPoolExecutorService().submit(new Runnable(){
            @Override
            public void run() {
                try {
                    Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient()
                            .deleteEntity_XML(PeonyTaxpayerCaseList.class, 
                                    GardenRestParams.Taxpayer.deleteTaxpayerCaseRestParams(targetPeonyTaxpayerCase.getTaxpayerCase().getTaxpayerCaseUuid()));
                    broadcastPeonyFaceEventHappened(new CloseTopComponentRequest());
                    broadcastPeonyFaceEventHappened(new CloseDialogRequest());
                } catch (Exception ex) {
                    PeonyFaceUtils.displayErrorMessageDialog("Failed to delete this taxpayer case! Please try it later.");
                    Exceptions.printStackTrace(ex);
                }
                
            }
        });
    }
    
    @Override
    protected void resetDataEntryStyleHelper() {
        bankAccountNumberField.setStyle(null);
        bankRoutingNumberField.setStyle(null);
        federalFilingStatusComboBox.setStyle(null);
        taxReturnDeadlineDatePicker.setStyle(null);
        taxReturnExtensionDatePicker.setStyle(null);
        caseStatusComboBox.setStyle(null);
        cityField.setStyle(null);
        countyField.setStyle(null);
        addressField.setStyle(null);
////        residencyField.setStyle(null);
        countyField.setStyle(null);
        stateComboBox.setStyle(null);
        zipCodeField.setStyle(null);
        
        if (primaryTaxpayerDataEntryController != null){
            primaryTaxpayerDataEntryController.resetDataEntryStyle();
        }
        
        if ((!TaxpayerFederalFilingStatus.Single.value().equalsIgnoreCase(federalFilingStatusComboBox.getValue()))
                && (!TaxpayerFederalFilingStatus.HeadOfHousehold.value().equalsIgnoreCase(federalFilingStatusComboBox.getValue()))){
            if (spouseTaxpayerDataEntryController != null){
                spouseTaxpayerDataEntryController.resetDataEntryStyle();
            }
        }
        
        if ((!TaxpayerFederalFilingStatus.Single.value().equalsIgnoreCase(federalFilingStatusComboBox.getValue()))){
            if ((taxpayerDependentEntryControllerStorage != null) && (!taxpayerDependentEntryControllerStorage.isEmpty())){
                List<TaxpayerDependentEntryController> aTaxpayerDependentEntryControllerList = new ArrayList<>(taxpayerDependentEntryControllerStorage.values());
                for (TaxpayerDependentEntryController aTaxpayerDependentEntryController : aTaxpayerDependentEntryControllerList){
                    aTaxpayerDependentEntryController.resetDataEntryStyle();
                }
            }
        }
    }

    @Override
    protected void resetDataEntryHelper() {
////        contactInfoField.setText(null);
////        bankNoteField.setText(null);
        bankAccountNumberField.setText(null);
        bankRoutingNumberField.setText(null);
        federalFilingStatusComboBox.setValue(null);
        taxReturnDeadlineDatePicker.setValue(null);
        taxReturnExtensionDatePicker.setValue(null);
        caseStatusComboBox.setValue(null);
        cityField.setText(null);
        countyField.setText(null);
        addressField.setText(null);
////        residencyField.setText(null);
        countyField.setText(null);
        stateComboBox.setValue(null);
        zipCodeField.setText(null);
        
        if (primaryTaxpayerDataEntryController != null){
            primaryTaxpayerDataEntryController.resetDataEntry();
        }
        
        if ((!TaxpayerFederalFilingStatus.Single.value().equalsIgnoreCase(federalFilingStatusComboBox.getValue()))
                && (!TaxpayerFederalFilingStatus.HeadOfHousehold.value().equalsIgnoreCase(federalFilingStatusComboBox.getValue()))){
            if (spouseTaxpayerDataEntryController != null){
                spouseTaxpayerDataEntryController.resetDataEntry();
            }
        }
        
        if ((!TaxpayerFederalFilingStatus.Single.value().equalsIgnoreCase(federalFilingStatusComboBox.getValue()))){
            if ((taxpayerDependentEntryControllerStorage != null) && (!taxpayerDependentEntryControllerStorage.isEmpty())){
                List<TaxpayerDependentEntryController> aTaxpayerDependentEntryControllerList = new ArrayList<>(taxpayerDependentEntryControllerStorage.values());
                for (TaxpayerDependentEntryController aTaxpayerDependentEntryController : aTaxpayerDependentEntryControllerList){
                    aTaxpayerDependentEntryController.resetDataEntry();
                }
            }
        }
    }

    @Override
    protected Node getBadEntityField(EntityField entityFiled) {
        switch (entityFiled){
            case BankAccountNumber:
                return bankAccountNumberField;
            case BankRoutingNumber:
                return bankRoutingNumberField;
            case FederalFilingStatus:
                return federalFilingStatusComboBox;
            case TaxReturnDeadline:
                return taxReturnDeadlineDatePicker;
            case DeadlineExtension:
                return taxReturnExtensionDatePicker;
////            case Memo:
////                return residencyField;
            case CityName:
                return cityField;
            case Country:
                return countyField;
            case LocalAddress:
                return addressField;
            case StateCounty:
                return countyField;
            case StateName:
                return stateComboBox;
            case ZipCode:
                return zipCodeField;
////            case Contact:
////                return contactInfoField;
////            case BankNote:
////                return bankNoteField;
            default:
                return null;
        }
    }
    
    private void updateBasicInfoFieldLabel(EntityField entityField, PeonyTaxpayerCase legacyPeonyTaxpayerCase){
        G02TaxpayerCase targetTaxpayerCase = targetPeonyTaxpayerCase.getTaxpayerCase();
        G02TaxpayerCase legacyTaxpayerCase = legacyPeonyTaxpayerCase.getTaxpayerCase();
        switch (entityField){
            case FederalFilingStatus:
                /**
                 * because of old bug: location was shared by taxpayer cases. So, the legacy location may be null. After 2022, each case has its own location
                 */
                if (ZcaValidator.isNullEmpty(legacyTaxpayerCase.getFederalFilingStatus())){
                    break;
                }
                if (ZcaText.compareTextValueIgnoreCase(targetTaxpayerCase.getFederalFilingStatus(),  legacyTaxpayerCase.getFederalFilingStatus())){
                    federalFilingStatusLabel.setText("Federal Filing Status: ");
                    federalFilingStatusLabel.setTextFill(PeonyCss.BLACK);
                    federalFilingStatusLabel.getStyleClass().remove("peony-success-small-label");
                }else{
                    federalFilingStatusLabel.setText("Federal Filing Status: " + legacyTaxpayerCase.getFederalFilingStatus());
                    federalFilingStatusLabel.setTextFill(PeonyCss.WHITE);
                    federalFilingStatusLabel.getStyleClass().add("peony-success-small-label");
                }
                break;
            case BankRoutingNumber:
                if (ZcaValidator.isNullEmpty(legacyTaxpayerCase.getBankRoutingNumber())){
                    break;
                }
                if (ZcaText.compareTextValueIgnoreCase(targetTaxpayerCase.getBankRoutingNumber(),  legacyTaxpayerCase.getBankRoutingNumber())){
                    bankRoutingNumberLabel.setText("Bank Routing Number: ");
                    bankRoutingNumberLabel.setTextFill(PeonyCss.BLACK);
                    bankRoutingNumberLabel.getStyleClass().remove("peony-success-small-label");
                }else{
                    bankRoutingNumberLabel.setText("Bank Routing#: " + legacyTaxpayerCase.getBankRoutingNumber());
                    bankRoutingNumberLabel.setTextFill(PeonyCss.WHITE);
                    bankRoutingNumberLabel.getStyleClass().add("peony-success-small-label");
                }
                break;
            case BankAccountNumber:
                if (ZcaValidator.isNullEmpty(legacyTaxpayerCase.getBankAccountNumber())){
                    break;
                }
                if (ZcaText.compareTextValueIgnoreCase(targetTaxpayerCase.getBankAccountNumber(),  legacyTaxpayerCase.getBankAccountNumber())){
                    bankAccountNumberLabel.setText("Bank Account Number: ");
                    bankAccountNumberLabel.setTextFill(PeonyCss.BLACK);
                    bankAccountNumberLabel.getStyleClass().remove("peony-success-small-label");
                }else{
                    bankAccountNumberLabel.setText("Bank Account#: " + legacyTaxpayerCase.getBankAccountNumber());
                    bankAccountNumberLabel.setTextFill(PeonyCss.WHITE);
                    bankAccountNumberLabel.getStyleClass().add("peony-success-small-label");
                }
                break;
////            case Contact:
////                if (ZcaText.compareTextValueIgnoreCase(targetTaxpayerCase.getContact(),  legacyTaxpayerCase.getContact())){
////                    contactInfoLabel.setText("Contact Info: ");
////                    contactInfoLabel.setTextFill(PeonyCss.BLACK);
////                }else{
////                    contactInfoLabel.setText("Contact Info: " + legacyTaxpayerCase.getContact());
////                    contactInfoLabel.setTextFill(PeonyCss.DARKRED);
////                }
////                break;
////            case BankNote:
////                if (ZcaText.compareTextValueIgnoreCase(targetTaxpayerCase.getBankNote(),  legacyTaxpayerCase.getBankNote())){
////                    bankNoteLabel.setText("Bank Note: ");
////                    bankNoteLabel.setTextFill(PeonyCss.BLACK);
////                }else{
////                    bankNoteLabel.setText("Bank Note: " + legacyTaxpayerCase.getBankNote());
////                    bankNoteLabel.setTextFill(PeonyCss.DARKRED);
////                }
////                break;
            default:
        }
    }
    
    private void updatePrimaryLocationFieldLabel(EntityField entityFiled, PeonyTaxpayerCase legacyPeonyTaxpayerCase){
        G02Location targetPrimaryLocation = targetPeonyTaxpayerCase.getPrimaryLocation();
        G02Location legacyPrimaryLocation = legacyPeonyTaxpayerCase.getPrimaryLocation();
        switch (entityFiled){
            case CityName:
                /**
                 * because of old bug: location was shared by taxpayer cases. So, the legacy location may be null. After 2022, each case has its own location
                 */
                if (ZcaValidator.isNullEmpty(legacyPrimaryLocation.getCityName())){
                    break;
                }
                if (ZcaText.compareTextValueIgnoreCase(targetPrimaryLocation.getCityName(),  legacyPrimaryLocation.getCityName())){
                    cityLabel.setText("City");
                    cityLabel.setTextFill(PeonyCss.BLACK);
                    cityLabel.getStyleClass().remove("peony-success-small-label");
                }else{
                    cityLabel.setText("City: " + legacyPrimaryLocation.getCityName());
                    cityLabel.setTextFill(PeonyCss.WHITE);
                    cityLabel.getStyleClass().add("peony-success-small-label");
                }
                break;
            case LocalAddress:
                if (ZcaValidator.isNullEmpty(legacyPrimaryLocation.getLocalAddress())){
                    break;
                }
                if (ZcaText.compareTextValueIgnoreCase(targetPrimaryLocation.getLocalAddress(),  legacyPrimaryLocation.getLocalAddress())){
                    addressLabel.setText("Address");
                    addressLabel.setTextFill(PeonyCss.BLACK);
                    addressLabel.getStyleClass().remove("peony-success-small-label");
                }else{
                    addressLabel.setText("Address: " + legacyPrimaryLocation.getLocalAddress());
                    addressLabel.setTextFill(PeonyCss.WHITE);
                    addressLabel.getStyleClass().add("peony-success-small-label");
                }
                break;
            case StateCounty:
                if (ZcaValidator.isNullEmpty(legacyPrimaryLocation.getStateCounty())){
                    break;
                }
                if (ZcaText.compareTextValueIgnoreCase(targetPrimaryLocation.getStateCounty(),  legacyPrimaryLocation.getStateCounty())){
                    countyLabel.setText("State's County");
                    countyLabel.setTextFill(PeonyCss.BLACK);
                    countyLabel.getStyleClass().remove("peony-success-small-label");
                }else{
                    countyLabel.setText("State's County: " + legacyPrimaryLocation.getStateCounty());
                    countyLabel.setTextFill(PeonyCss.WHITE);
                    countyLabel.getStyleClass().add("peony-success-small-label");
                }
                break;
            case StateName:
                if (ZcaValidator.isNullEmpty(legacyPrimaryLocation.getStateName())){
                    break;
                }
                if (ZcaText.compareTextValueIgnoreCase(targetPrimaryLocation.getStateName(),  legacyPrimaryLocation.getStateName())){
                    stateLabel.setText("State");
                    stateLabel.setTextFill(PeonyCss.BLACK);
                    stateLabel.getStyleClass().remove("peony-success-small-label");
                }else{
                    stateLabel.setText("State: " + legacyPrimaryLocation.getStateName());
                    stateLabel.setTextFill(PeonyCss.WHITE);
                    stateLabel.getStyleClass().add("peony-success-small-label");
                }
                break;
            case ZipCode:
                if (ZcaValidator.isNullEmpty(legacyPrimaryLocation.getZipCode())){
                    break;
                }
                if (ZcaText.compareTextValueIgnoreCase(targetPrimaryLocation.getZipCode(),  legacyPrimaryLocation.getZipCode())){
                    zipCodeLabel.setText("Zip Code");
                    zipCodeLabel.setTextFill(PeonyCss.BLACK);
                    zipCodeLabel.getStyleClass().remove("peony-success-small-label");
                }else{
                    zipCodeLabel.setText("Zip Code: " + legacyPrimaryLocation.getZipCode());
                    zipCodeLabel.setTextFill(PeonyCss.WHITE);
                    zipCodeLabel.getStyleClass().add("peony-success-small-label");
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
        
        updateBasicInfoFieldLabel(EntityField.FederalFilingStatus, legacyPeonyTaxpayerCase);
        updateBasicInfoFieldLabel(EntityField.BankRoutingNumber, legacyPeonyTaxpayerCase);
        updateBasicInfoFieldLabel(EntityField.BankAccountNumber, legacyPeonyTaxpayerCase);
        updateBasicInfoFieldLabel(EntityField.BankNote, legacyPeonyTaxpayerCase);
        updateBasicInfoFieldLabel(EntityField.Contact, legacyPeonyTaxpayerCase);
        
        updatePrimaryLocationFieldLabel(EntityField.LocalAddress, legacyPeonyTaxpayerCase);
        updatePrimaryLocationFieldLabel(EntityField.CityName, legacyPeonyTaxpayerCase);
        updatePrimaryLocationFieldLabel(EntityField.StateCounty, legacyPeonyTaxpayerCase);
        updatePrimaryLocationFieldLabel(EntityField.StateName, legacyPeonyTaxpayerCase);
        updatePrimaryLocationFieldLabel(EntityField.ZipCode, legacyPeonyTaxpayerCase);
        updatePrimaryLocationFieldLabel(EntityField.CityName, legacyPeonyTaxpayerCase);
        
        if (primaryTaxpayerDataEntryController != null){
            primaryTaxpayerDataEntryController.populateLegacyPeonyTaxpayerCaseComparison(legacyPeonyTaxpayerCase);
        }
        
        if ((!TaxpayerFederalFilingStatus.Single.value().equalsIgnoreCase(federalFilingStatusComboBox.getValue()))
                && (!TaxpayerFederalFilingStatus.HeadOfHousehold.value().equalsIgnoreCase(federalFilingStatusComboBox.getValue()))){
            if (spouseTaxpayerDataEntryController != null){
                spouseTaxpayerDataEntryController.populateLegacyPeonyTaxpayerCaseComparison(legacyPeonyTaxpayerCase);
            }
        }
        
        if ((!TaxpayerFederalFilingStatus.Single.value().equalsIgnoreCase(federalFilingStatusComboBox.getValue()))){
            if ((taxpayerDependentEntryControllerStorage != null) && (!taxpayerDependentEntryControllerStorage.isEmpty())){
                List<TaxpayerDependentEntryController> aTaxpayerDependentEntryControllerList = new ArrayList<>(taxpayerDependentEntryControllerStorage.values());
                for (TaxpayerDependentEntryController aTaxpayerDependentEntryController : aTaxpayerDependentEntryControllerList){
                    aTaxpayerDependentEntryController.populateLegacyPeonyTaxpayerCaseComparison(legacyPeonyTaxpayerCase);
                }
            }
        }
    }
    
    private void displayNonPrimaryPaneList(final String taxpayerFederalFilingStatus){
        if (Platform.isFxApplicationThread()){
            displayNonPrimaryPaneListHelper(taxpayerFederalFilingStatus);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    displayNonPrimaryPaneListHelper(taxpayerFederalFilingStatus);
                }
            });
        }
    }
    private void displayNonPrimaryPaneListHelper(String taxpayerFederalFilingStatus){
        for (Pane aPane : nonPrimaryPaneList){
            displayNonPrimaryPaneHelper(aPane, taxpayerFederalFilingStatus);
        }
        if (spousePane != null){
            displayNonPrimaryPaneHelper(spousePane, taxpayerFederalFilingStatus);
        }
    }

    private void displayNonPrimaryPaneHelper(Pane aPane, String taxpayerFederalFilingStatus) {
        if (TaxpayerFederalFilingStatus.Single.value().equalsIgnoreCase(taxpayerFederalFilingStatus)){
            makeSpousePaneAvailable(false);
            aPane.setVisible(false);
        }else if (TaxpayerFederalFilingStatus.HeadOfHousehold.value().equalsIgnoreCase(taxpayerFederalFilingStatus)){
            makeSpousePaneAvailable(false);
        }else{
            makeSpousePaneAvailable(true);
            aPane.setVisible(true);
        }
    }
    
    private void makeSpousePaneAvailable(boolean available){
        if (spousePane != null){
            if (available){
                primaryProfileVBox.getChildren().remove(spousePane);
                primaryProfileVBox.getChildren().add(2, spousePane);
            }
            if (!available){
                primaryProfileVBox.getChildren().remove(spousePane);
            }
            pluginAddDependentButton();
        }
    }
    
    private void initializeBasicInformationPanelHelper() {
        primaryProfileVBox.prefWidthProperty().bind(primaryProfileScrollPane.widthProperty().add(-5));

        G02TaxpayerCase targetTaxpayerCaseEntity = targetPeonyTaxpayerCase.getTaxpayerCase();

        /**
         * This is used to force deadline to be 4/15. Amazon time-zone issue is puzzling. This method simply gives a patch.
         */
        TaxationSettings.getSingleton().modifyTaxpayerCaseDeadline(targetTaxpayerCaseEntity);        
        PeonyFaceUtils.initializeDatePicker(taxReturnDeadlineDatePicker, 
                ZcaCalendar.convertToLocalDate(targetTaxpayerCaseEntity.getDeadline()), 
                ZcaCalendar.convertToLocalDate(TaxationSettings.getSingleton().getComingPersonalFamlityTaxReturnDeadline(UState.NY).getTime()), 
                "Deadline of tax return for the taxpayer case", this);

        if (PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.TAX_DEADLINE_UPDATE)){
            taxReturnDeadlineDatePicker.setDisable(false);
        }else{
            taxReturnDeadlineDatePicker.setDisable(true);
        }

        PeonyFaceUtils.initializeDatePicker(taxReturnExtensionDatePicker, 
                ZcaCalendar.convertToLocalDate(targetTaxpayerCaseEntity.getExtension()), null, 
                "Extension for the taxpayer case's tax return", this);

        PeonyFaceUtils.initializeComboBox(federalFilingStatusComboBox, 
                                          TaxpayerFederalFilingStatus.getEnumValueList(false), 
                                          targetTaxpayerCaseEntity.getFederalFilingStatus(), 
                                          TaxpayerFederalFilingStatus.Single.value(), "Federal filing status", this);
        federalFilingStatusComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                displayNonPrimaryPaneList(newValue);
                pluginAddDependentButton();
            }
        );
        PeonyFaceUtils.initializeTextField(bankRoutingNumberField, 
                                           targetTaxpayerCaseEntity.getBankRoutingNumber(), null,
                                           "Bank routing number", this);
        PeonyFaceUtils.initializeTextField(bankAccountNumberField, 
                                           targetTaxpayerCaseEntity.getBankAccountNumber(), null, 
                                           "Bank account number", this);
////        PeonyFaceUtils.initializeTextField(residencyField, 
////                                           targetTaxpayerCaseEntity.getMemo(), null, 
////                                           "memo for this taxpayer case...(max 450 characters)", this);
        if (targetPeonyTaxpayerCase.getLatestTaxpayerCaseStatus() == null){
            PeonyFaceUtils.initializeComboBox(caseStatusComboBox, GardenTaxpayerCaseStatus.getEnumValueList(false), 
                    null, null, "Select current status...", this);
        }else{
            PeonyFaceUtils.initializeComboBox(caseStatusComboBox, GardenTaxpayerCaseStatus.getEnumValueList(false), 
                    targetPeonyTaxpayerCase.getLatestTaxpayerCaseStatus().getLogMessage(), null, "Select current status...", this);
        }
        G02Location targetLocation = targetPeonyTaxpayerCase.getPrimaryLocation();
        PeonyFaceUtils.initializeTextField(addressField, targetLocation.getLocalAddress(), null, "Address", this);
        PeonyFaceUtils.initializeTextField(cityField, targetLocation.getCityName(), null, "City", this);
        PeonyFaceUtils.initializeTextField(countyField, targetLocation.getStateCounty(), null, "County name in the state", this);
        PeonyFaceUtils.initializeComboBox(stateComboBox, UState.getEnumValueList(false), targetLocation.getStateName(), UState.NY.value(), "State", this);
        PeonyFaceUtils.initializeTextField(zipCodeField, targetLocation.getZipCode(), null, "Zip", this);
        //PeonyFaceUtils.initializeTextField(countryField, targetLocation.getCountry(), "USA", "Nationality", this);

        saveButton.setOnAction((ActionEvent e) -> {
            saveTaxpayerBasicProfileData();
        });

        deleteButton.setOnAction((ActionEvent e) -> {
            if (!PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.FINALIZE_TAXPAYER)){
                PeonyFaceUtils.displayPrivilegeErrorMessageDialog();
                return;
            }
            if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to delete the entire taxpayer case?") == JOptionPane.YES_OPTION){
                deleteTargetPeonyTaxpayerCase();
            }
        });
    }

    private void initializePrimaryAndSpouseDataEntryControllersHelper(TaxpayerRelationship taxpayerRelationship) {
        TaxpayerMainEntryController aTaxpayerMainEntryController;
        if (null == taxpayerRelationship){
            return;
        }else switch (taxpayerRelationship) {
            case PRIMARY_TAXPAYER:
                aTaxpayerMainEntryController = primaryTaxpayerDataEntryController;
                break;
            case SPOUSE_TAXPAYER:
                aTaxpayerMainEntryController = spouseTaxpayerDataEntryController;
                break;
            default:
                return;
        }
        
        if (aTaxpayerMainEntryController != null){
            return;
        }
        
        G02TaxpayerInfo aG02TaxpayerInfo = null;
        if (TaxpayerRelationship.PRIMARY_TAXPAYER.equals(taxpayerRelationship)){
            aG02TaxpayerInfo = targetPeonyTaxpayerCase.retrievePrimaryTaxpayerInfo();
        }if (TaxpayerRelationship.SPOUSE_TAXPAYER.equals(taxpayerRelationship)){
            aG02TaxpayerInfo = targetPeonyTaxpayerCase.retrieveSpouseTaxpayerInfo();
        }
        
        if (aG02TaxpayerInfo == null){
            aG02TaxpayerInfo = new G02TaxpayerInfo();
            aG02TaxpayerInfo.setTaxpayerUserUuid(ZcaUtils.generateUUIDString());
            aG02TaxpayerInfo.setSsn("");
            aG02TaxpayerInfo.setRelationships(taxpayerRelationship.value());
            aG02TaxpayerInfo.setTaxpayerCaseUuid(getTargetPeonyTaxpayerCaseUuid());
            targetPeonyTaxpayerCase.getTaxpayerInfoList().add(aG02TaxpayerInfo);
        }
        
        if (TaxpayerRelationship.PRIMARY_TAXPAYER.equals(taxpayerRelationship)){
            aTaxpayerMainEntryController = new TaxpayerMainEntryController(targetPeonyTaxpayerCase, aG02TaxpayerInfo, true, false, false, true);
            aTaxpayerMainEntryController.addPeonyFaceEventListener(this);
            primaryTaxpayerDataEntryController = aTaxpayerMainEntryController;
        }
        
        if (TaxpayerRelationship.SPOUSE_TAXPAYER.equals(taxpayerRelationship)){
            aTaxpayerMainEntryController = new TaxpayerMainEntryController(targetPeonyTaxpayerCase, aG02TaxpayerInfo, true, true, false, true);
            aTaxpayerMainEntryController.addPeonyFaceEventListener(this);
            spouseTaxpayerDataEntryController = aTaxpayerMainEntryController;
        }
        
        if (aTaxpayerMainEntryController == null){
            throw new RuntimeException("Tech Error: targetTaxpayerDataEntryController should be initialized.");
        }
        try {
            Pane aPane = aTaxpayerMainEntryController.loadFxml();
            if (TaxpayerRelationship.SPOUSE_TAXPAYER.equals(taxpayerRelationship)){
////                aPane.setId(TaxpayerRelationship.SPOUSE_TAXPAYER.value());
////                nonPrimaryPaneList.add(aPane);
                spousePane = aPane;
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        aTaxpayerMainEntryController.addPeonyFaceEventListenerList(this.getPeonyFaceEventListenerList());
        listenToCompareLegacyComboBox(aTaxpayerMainEntryController);
        
        Node rootNode = aTaxpayerMainEntryController.getRootPane();
        if (rootNode instanceof VBox){
            VBox rootBox = (VBox)rootNode;
            primaryProfileVBox.getChildren().add(rootBox);
        }
        if (spousePane != null){
            displayNonPrimaryPaneHelper(spousePane, targetPeonyTaxpayerCase.getTaxpayerCase().getFederalFilingStatus());
        }
        
        pluginAddDependentButton();
    }
    
    private String getTargetPeonyTaxpayerCaseUuid(){
        return targetPeonyTaxpayerCase.getTaxpayerCase().getTaxpayerCaseUuid();
    }

    private void pluginAddDependentButton() {
        try{
            primaryProfileVBox.getChildren().remove(this.addDependentButton);
            if (!TaxpayerFederalFilingStatus.Single.value().equalsIgnoreCase(federalFilingStatusComboBox.getValue())){
                if (TaxpayerFederalFilingStatus.HeadOfHousehold.value().equalsIgnoreCase(federalFilingStatusComboBox.getValue())){
                    primaryProfileVBox.getChildren().add(2, this.addDependentButton);
                }else{
                    primaryProfileVBox.getChildren().add(this.addDependentButton);
                }
            }
        }catch(IllegalArgumentException ex){
        }
    }

    private void loadWorkStatus() {
        //currentTargetPeonyTaxpayerCaseStatusLog
        this.getCachedThreadPoolExecutorService().submit(new Task<PeonyTaxpayerCase>(){
            @Override
            protected PeonyTaxpayerCase call() throws Exception {
                return Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient()
                            .storeEntity_XML(PeonyTaxpayerCase.class, GardenRestParams.Taxpayer.loadWorkStatusForPeonyTaxpayerCaseCaseParams(), targetPeonyTaxpayerCase);
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyTaxpayerCase result = get();
                    targetPeonyTaxpayerCase.setTaxpayerCaseWorkStatusList(result.getTaxpayerCaseWorkStatusList());
                    
                    currentTargetPeonyTaxpayerCaseStatusLog = result.getLatestTaxpayerCaseStatus();
                    if (currentTargetPeonyTaxpayerCaseStatusLog != null){
                        caseStatusComboBox.getSelectionModel().select(targetPeonyTaxpayerCase.getLatestTaxpayerCaseStatus().getLogMessage());
                    }
                    caseStatusComboBox.valueProperty().addListener(new ChangeListener(){
                        @Override
                        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                            if ((newValue == null) || (ZcaValidator.isNullEmpty(newValue.toString()))){
                                return;
                            }
                            if ((currentTargetPeonyTaxpayerCaseStatusLog == null) 
                                    || (!newValue.toString().equalsIgnoreCase(currentTargetPeonyTaxpayerCaseStatusLog.getLogMessage())))
                            {
                                //create a new currentTargetPeonyTaxpayerCaseStatusLog...which is a special log record
                                G02Log aG02Log = new G02Log();
                                aG02Log.setCreated(new Date());
                                aG02Log.setEntityDescription(getEntityDescription());
                                aG02Log.setEntityType(getTargetEntityType().name());
                                aG02Log.setEntityUuid(getTargetEntityUuid());
                                aG02Log.setLogMessage(caseStatusComboBox.getValue());
                                aG02Log.setLogName(PeonyLogName.UPDATED_TAXPAYER_CASE_STATUS.name());
                                aG02Log.setLogUuid(ZcaUtils.generateUUIDString());
                                aG02Log.setLoggedEntityType(getTargetEntityType().name());
                                aG02Log.setLoggedEntityUuid(getTargetEntityUuid());
                                aG02Log.setOperatorAccountUuid(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
                                //this will be saved by user clicking "Save Taxpayer Case" button
                                targetPeonyTaxpayerCase.setLatestTaxpayerCaseStatus(aG02Log);
                                targetPeonyTaxpayerCase.getTaxpayerCase().setLatestLogUuid(aG02Log.getLogUuid());

                                currentTargetPeonyTaxpayerCaseStatusLog = aG02Log;
                            }else{
                                //synchronze the data between targetPeonyTaxpayerCase and currentTargetPeonyTaxpayerCaseStatusLog
                                if (currentTargetPeonyTaxpayerCaseStatusLog != null){
                                    targetPeonyTaxpayerCase.setLatestTaxpayerCaseStatus(currentTargetPeonyTaxpayerCaseStatusLog);
                                    targetPeonyTaxpayerCase.getTaxpayerCase().setLatestLogUuid(currentTargetPeonyTaxpayerCaseStatusLog.getLogUuid());
                                }
                            }   
                        }
                    });
                    
                    saveButton.setDisable(false);
                    caseStatusComboBox.setDisable(false);
                    
                    broadcastPeonyFaceEventHappened(new TaxpayerCaseWorkStatusLoaded());
                    
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
    }

}
