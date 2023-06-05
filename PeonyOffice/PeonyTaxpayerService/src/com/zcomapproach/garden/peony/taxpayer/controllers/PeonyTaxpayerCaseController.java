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
import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.taxpayer.dialogs.TaxpayerCaseExportForPrintDialog;
import com.zcomapproach.garden.peony.taxpayer.events.PeonyTaxpayerCaseFinalized;
import com.zcomapproach.garden.peony.taxpayer.events.TaxpayerCaseWorkStatusLoaded;
import com.zcomapproach.garden.peony.view.dialogs.PeonyJobAssignmentDialog;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.peony.view.events.CloseTopComponentRequest;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.events.PeonyJobSaved;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.peony.PeonyJob;
import com.zcomapproach.garden.persistence.peony.PeonyMemo;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.garden.rest.GardenRestParams;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class PeonyTaxpayerCaseController extends PeonyTaxpayerServiceController implements PeonyFaceEventListener { 
    
    @FXML
    private AnchorPane rootAnchorPane;
    
    @FXML
    private Label pageTitleLabel;
    
    @FXML
    private HBox topButtonsHBox;
    
    @FXML
    private Label newClientLabel;
    
    @FXML
    private TabPane taxpayerCaseTabPane;
    
    @FXML
    private Tab primaryProfileTab;
    
    @FXML
    private Tab basicProfileTab;
    
////    @FXML
////    private Tab taxpayerProfilesTab;
    
    @FXML
    private Tab otherInformationTab;
    
    @FXML
    private Tab billAndPaymentTab;
    
    @FXML
    private Tab archivedDocumentsTab;
    
    @FXML
    private Tab workStatusTab;
    
    @FXML
    private Tab relatedCommunicationTab;
    
    @FXML
    private ComboBox<PeonyTaxpayerCase> compareComboBox;

    private final PeonyTaxpayerCase targetPeonyTaxpayerCase;
    private TaxpayerPrimaryProfileController primaryProfileController;
    private TaxpayerTagsMemoController tagsMemoController;
    
    public PeonyTaxpayerCaseController(PeonyTaxpayerCase targetPeonyTaxpayerCase) {
        super(targetPeonyTaxpayerCase);
        if (targetPeonyTaxpayerCase == null){
            targetPeonyTaxpayerCase = new PeonyTaxpayerCase();
            targetPeonyTaxpayerCase.getTaxpayerCase().setTaxpayerCaseUuid(ZcaUtils.generateUUIDString());
        }
        this.targetPeonyTaxpayerCase = targetPeonyTaxpayerCase;
    }
    
    private JFXButton constructTaxpayerJobAssignmentButton(){
        JFXButton btn = new JFXButton("Assign Job");
        btn.getStyleClass().add("peony-primary-button");
        btn.setButtonType(JFXButton.ButtonType.RAISED);
        btn.setTooltip(new Tooltip("Assign a job to others..."));
        btn.setGraphic(PeonyGraphic.getImageView("award_star_bronze.png"));
        btn.setOnAction((ActionEvent e) -> {
////            if (!PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.ASSIGN_TAXPAYER_JOB)){
////                PeonyFaceUtils.displayErrorMessageDialog("You need to be authorized to do this operation.");
////                return;
////            }
            PeonyJobAssignmentDialog aPeonyJobAssignmentDialog = new PeonyJobAssignmentDialog(null, true);
            aPeonyJobAssignmentDialog.addPeonyFaceEventListener(this);
            aPeonyJobAssignmentDialog.launchPeonyJobAssignmentDialog("Assign Job", targetPeonyTaxpayerCase);
        });
        return btn;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pageTitleLabel.setText(targetPeonyTaxpayerCase.getTaxpayerCaseTitle(false));
        topButtonsHBox.getChildren().add(constructTaxpayerJobAssignmentButton());
        topButtonsHBox.getChildren().addAll(constructCommonTaxCaseFunctionalButtons());
        /**
         * initialize tabs
         */
        initializePrimaryProfileTab();
        initializeTagsMemoProfileTab();
        initializeOtherInformationTab();
        intializeBillAndPaymentTab();
        intializeArchivedDocumentsTab();
        
        intializeRelatedCommunicationTab(taxpayerCaseTabPane, relatedCommunicationTab, targetPeonyTaxpayerCase.getRelatedEmails(), targetPeonyTaxpayerCase);
        
        //make them invisible and then initialize compareComboBox
        newClientLabel.setVisible(false);
        compareComboBox.setVisible(false);
    }

    private void intializeBillAndPaymentTab() {
        this.getCachedThreadPoolExecutorService().submit(new Task<PeonyTaxpayerCase>(){
            @Override
            protected PeonyTaxpayerCase call() throws Exception {
                return Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient()
                            .storeEntity_XML(PeonyTaxpayerCase.class, GardenRestParams.Taxpayer.loadBillAndPaymentsForPeonyTaxpayerCaseRestParams(), targetPeonyTaxpayerCase);
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyTaxpayerCase result = get();
                    targetPeonyTaxpayerCase.setPeonyBillPaymentList(result.getPeonyBillPaymentList());
                    PeonyTaxpayerCaseController.super.intializeBillAndPaymentTab(taxpayerCaseTabPane, billAndPaymentTab, targetPeonyTaxpayerCase.getPeonyBillPaymentList(), targetPeonyTaxpayerCase);
                    
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
    }

    private void intializeArchivedDocumentsTab() {
        this.getCachedThreadPoolExecutorService().submit(new Task<PeonyTaxpayerCase>(){
            @Override
            protected PeonyTaxpayerCase call() throws Exception {
                return Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient()
                            .storeEntity_XML(PeonyTaxpayerCase.class, GardenRestParams.Taxpayer.loadArchiveAndFilesForPeonyTaxpayerCaseRestParams(), targetPeonyTaxpayerCase);
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyTaxpayerCase result = get();
                    targetPeonyTaxpayerCase.setPeonyArchivedFileList(result.getPeonyArchivedFileList());
                    PeonyTaxpayerCaseController.super.intializeArchivedDocumentsTab(taxpayerCaseTabPane, archivedDocumentsTab, targetPeonyTaxpayerCase.getPeonyArchivedFileList(), targetPeonyTaxpayerCase);
                    
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
        
    }
    
    /**
     * Publish bill with payments in the legacyPeonyTaxpayerCase into billAndPaymentTab
     * This method is invoked by launchCompareComboBoxInitialization in JavaFX event thread after intializeBillAndPaymentTab was invoked in advance.
     * @param legacyPeonyTaxpayerCase 
     */
    @Override
    protected void publishPreviousYearBillPayments(PeonyTaxpayerCase legacyPeonyTaxpayerCase){
        if (legacyPeonyTaxpayerCase == null){
            return;
        }
        addPreviousYearBillPayments(legacyPeonyTaxpayerCase.getPeonyBillPaymentList());
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof PeonyTaxpayerCaseFinalized){
            broadcastPeonyFaceEventHappened(new CloseTopComponentRequest());
            broadcastPeonyFaceEventHappened(new CloseDialogRequest());
        }else if (event instanceof PeonyJobSaved){
            handlePeonyJobSaved(((PeonyJobSaved)event));
        }else if (event instanceof TaxpayerCaseWorkStatusLoaded){
            intializeWorkStatusTab();
        }
    }

    private void handlePeonyJobSaved(PeonyJobSaved event) {
        
        PeonyJob peonyJob = event.getPeonyJob();
        
        PeonyMemo memo = new PeonyMemo();
        memo.setMemo(peonyJob.getJobContent());
        memo.setOperator(peonyJob.getJobCreator());
        targetPeonyTaxpayerCase.getPeonyMemoList().add(0, memo);
        tagsMemoController.publishPeonyMemo(memo);
        
        memo = new PeonyMemo();
        memo.setMemo(peonyJob.getJobAcceptorMemo());
        memo.setOperator(peonyJob.getJobCreator());
        targetPeonyTaxpayerCase.getPeonyMemoList().add(1, memo);
        tagsMemoController.publishPeonyMemo(memo);
        
        if (peonyJob.getJobNoteMemo() != null){
            memo = new PeonyMemo();
            memo.setMemo(peonyJob.getJobNoteMemo());
            memo.setOperator(peonyJob.getJobCreator());
            targetPeonyTaxpayerCase.getPeonyMemoList().add(2, memo);
            tagsMemoController.publishPeonyMemo(memo);
        }
    }
    
    private void initializePrimaryProfileTab(){
        if (primaryProfileController != null){
            return;
        }
        primaryProfileController = new TaxpayerPrimaryProfileController(targetPeonyTaxpayerCase, compareComboBox);
        primaryProfileController.addPeonyFaceEventListener(this);
        primaryProfileController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
        try {
            Pane aPane = primaryProfileController.loadFxml();
            aPane.prefHeightProperty().bind(taxpayerCaseTabPane.heightProperty());
            aPane.prefWidthProperty().bind(taxpayerCaseTabPane.widthProperty());
            primaryProfileTab.setContent(aPane);
            primaryProfileController.launchCompareComboBoxInitialization(targetPeonyTaxpayerCase, newClientLabel, topButtonsHBox);
            listenToCompareLegacyComboBox(primaryProfileController);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    private void initializeTagsMemoProfileTab() {
        this.getCachedThreadPoolExecutorService().submit(new Task<PeonyTaxpayerCase>(){
            @Override
            protected PeonyTaxpayerCase call() throws Exception {
                return Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient()
                            .storeEntity_XML(PeonyTaxpayerCase.class, GardenRestParams.Taxpayer.loadTagsAndMemosForPeonyTaxpayerCaseParams(), targetPeonyTaxpayerCase);
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyTaxpayerCase result = get();
                    targetPeonyTaxpayerCase.setDocumentTagList(result.getDocumentTagList());
                    targetPeonyTaxpayerCase.setPeonyMemoList(result.getPeonyMemoList());
                    
                    initializeTagsMemoProfileTabHelper();
                    
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
    }
    
    private void initializeTagsMemoProfileTabHelper(){
        if (tagsMemoController != null){
            return;
        }
        tagsMemoController = new TaxpayerTagsMemoController(targetPeonyTaxpayerCase);
        tagsMemoController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
        try {
            Pane aPane = tagsMemoController.loadFxml();
            aPane.prefHeightProperty().bind(taxpayerCaseTabPane.heightProperty());
            aPane.prefWidthProperty().bind(taxpayerCaseTabPane.widthProperty());
            basicProfileTab.setContent(aPane);
            listenToCompareLegacyComboBox(tagsMemoController);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void intializeWorkStatusTab() {
        if (Platform.isFxApplicationThread()){
            intializeWorkStatusTabHelper();
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    intializeWorkStatusTabHelper();
                }
            });
        }
    }
    private void intializeWorkStatusTabHelper(){
        TaxpayerWorkStatusController aTaxpayerWorkStatusController = new TaxpayerWorkStatusController(targetPeonyTaxpayerCase);
        aTaxpayerWorkStatusController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
        try {
            workStatusTab.setContent(aTaxpayerWorkStatusController.loadFxml());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    private void initializeOtherInformationTab() {
        this.getCachedThreadPoolExecutorService().submit(new Task<PeonyTaxpayerCase>(){
            @Override
            protected PeonyTaxpayerCase call() throws Exception {
                return Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient()
                            .storeEntity_XML(PeonyTaxpayerCase.class, GardenRestParams.Taxpayer.loadOtherInformationForPeonyTaxpayerCaseRestParams(), targetPeonyTaxpayerCase);
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyTaxpayerCase result = get();
                    targetPeonyTaxpayerCase.setPersonalBusinessPropertyList(result.getPersonalBusinessPropertyList());
                    targetPeonyTaxpayerCase.setPersonalPropertyList(result.getPersonalPropertyList());
                    targetPeonyTaxpayerCase.setTlcLicenseList(result.getTlcLicenseList());
                    
                    initializeOtherInformationTabHelper();
                    
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
    }
    private void initializeOtherInformationTabHelper(){
        TaxpayerOtherInformationController taxpayerOtherInformationTabPaneController = new TaxpayerOtherInformationController(targetPeonyTaxpayerCase);
        taxpayerOtherInformationTabPaneController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
        try {
            otherInformationTab.setContent(taxpayerOtherInformationTabPaneController.loadFxml());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    protected void displayTaxCaseExportForPrintDialog() {
        TaxpayerCaseExportForPrintDialog aPreprintTaxcorpCaseDialog = new TaxpayerCaseExportForPrintDialog(null, true);
        aPreprintTaxcorpCaseDialog.launchTaxpayerCaseExportForPrintDialog("Preprint Selection:", targetPeonyTaxpayerCase);
    }
    
    /**
     * Store the entire target taxpayer case
     * @throws ZcaEntityValidationException 
     */
////    public void storeTargetTaxpayerCase() {
////        Task<Pair<PeonyTaxpayerCaseTabPaneController, ZcaEntityValidationException>> storeTargetTaxpayerCaseTask = new Task<Pair<PeonyTaxpayerCaseTabPaneController, ZcaEntityValidationException>>(){
////            @Override
////            protected Pair<PeonyTaxpayerCaseTabPaneController, ZcaEntityValidationException> call() throws Exception {
////                //(1) load and validate targetTaxpayerCase
////                for (PeonyTaxpayerCaseTabPaneController aPeonyTaxpayerCaseTabPaneController : peonyTaxpayerCaseTabPaneControllerList){
////                    try {
////                        aPeonyTaxpayerCaseTabPaneController.loadTargetTaxpayerCaseFromTab();
////                    } catch (ZcaEntityValidationException ex) {
////                        //Exceptions.printStackTrace(ex);
////                        return new Pair<>(aPeonyTaxpayerCaseTabPaneController, ex);
////                    }
////                }
////                //(2) save targetTaxpayerCase
////                try{
////                    PeonyTaxpayerCase result = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient()
////                        .storeEntity_XML(PeonyTaxpayerCase.class, 
////                                        GardenRestParams.Taxpayer.storePeonyTaxpayerCaseRestParams(),
////                                        targetPeonyTaxpayerCase);
////                    if (result == null){
////                        return new Pair<>(null, new ZcaEntityValidationException("Failed to save this taxpayer."));
////                    }
////                    //successfully
////                    return null;
////                }catch(Exception ex){
////                    return new Pair<>(null, new ZcaEntityValidationException("Failed to save this taxpayer. " + ex.getMessage()));
////                }
////            }
////
////            @Override
////            protected void failed() {
////                PeonyFaceUtils.displayErrorMessageDialog("Technical error was raised. Cannot save this taxpayer case.");
////            }
////
////            @Override
////            protected void succeeded() {
////                try {
////                    Pair<PeonyTaxpayerCaseTabPaneController, ZcaEntityValidationException> result = get();
////                    if (result == null){
////                        for (PeonyTaxpayerCaseTabPaneController aPeonyTaxpayerCaseTabPaneController : peonyTaxpayerCaseTabPaneControllerList){
////                            aPeonyTaxpayerCaseTabPaneController.resetDataEntryStyleFromTab();
////                        }
////                        publishPeonyDataEntrySaveDemandingStatus(false);
////                        PeonyFaceUtils.displayInformationMessageDialog("Successfully saved this taxpayer case.");
////
////                        G02Log log = createNewG02LogInstance(PeonyLogName.STORED_TAXPAYER_CASE);
////                        log.setLoggedEntityType(GardenEntityType.TAXPAYER_CASE.name());
////                        log.setLoggedEntityUuid(targetPeonyTaxpayerCase.getTaxpayerCase().getTaxpayerCaseUuid());
////                        PeonyLocalSettings.getSingleton().log(log);
////                        
////                    }else{
////                        PeonyTaxpayerCaseTabPaneController aPeonyTaxpayerCaseTabPaneController = result.getKey();
////                        ZcaEntityValidationException aEntityValidationException = result.getValue();
////                        if (aPeonyTaxpayerCaseTabPaneController != null){
////                            //switch to the tab with troubles
////                            taxpayerCaseTabPane.getSelectionModel().select(aPeonyTaxpayerCaseTabPaneController.getOwnerTab());
////                        }
////                        //display error message
////                        PeonyFaceUtils.displayErrorMessageDialog(aEntityValidationException.getMessage());
////                    }
////                } catch (InterruptedException | ExecutionException ex) {
////                    //Exceptions.printStackTrace(ex);
////                }
////            }
////        };
////        getCachedThreadPoolExecutorService().submit(storeTargetTaxpayerCaseTask);
////    }
}
