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

import com.jfoenix.controls.JFXButton;
import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.taxcorp.dialogs.TaxcorpCaseExportForPrintDialog;
import com.zcomapproach.garden.peony.taxcorp.events.PeonyTaxcorpCaseFinalized;
import com.zcomapproach.garden.peony.view.dialogs.PeonyJobAssignmentDialog;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.peony.view.events.CloseTopComponentRequest;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.events.PeonyJobSaved;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.constant.TaxFilingType;
import com.zcomapproach.garden.persistence.peony.PeonyJob;
import com.zcomapproach.garden.persistence.peony.PeonyMemo;
import com.zcomapproach.garden.persistence.peony.PeonyTaxcorpCase;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.openide.util.Exceptions;

/**
 * The base of taxcorp top-component
 * @author zhijun98
 */
public class PeonyTaxcorpCaseController extends PeonyTaxcorpServiceController implements PeonyFaceEventListener{
    
    @FXML
    private AnchorPane rootAnchorPane;
    
    @FXML
    private Label pageTitleLabel;
    
    @FXML
    private HBox topButtonsHBox;
    
    @FXML
    private TabPane taxcorpCaseTabPane;
    
    @FXML
    private Tab taxcorpProfileTab;
    
    @FXML
    private Tab payrollTaxTab;
    
    @FXML
    private Tab salesTaxTab;
    
    @FXML
    private Tab taxReturnTab;
    
    @FXML
    private Tab billAndPaymentTab;
    
    @FXML
    private Tab archivedDocumentsTab;
    
    @FXML
    private Tab relatedCommunicationTab;

    private final PeonyTaxcorpCase targetPeonyTaxcorpCase;
    private TaxcorpProfileTabController taxcorpProfileTabController;

    /**
     * if targetPeonyTaxcorpCase is empty-new, this controller will display the home page for taxcorps
     * @param targetPeonyTaxcorpCase
     */
    public PeonyTaxcorpCaseController(PeonyTaxcorpCase targetPeonyTaxcorpCase) {
        super(targetPeonyTaxcorpCase);
        if (targetPeonyTaxcorpCase == null){
            //add new taxcorp case
            targetPeonyTaxcorpCase = new PeonyTaxcorpCase();
            targetPeonyTaxcorpCase.getTaxcorpCase().setTaxcorpCaseUuid(ZcaUtils.generateUUIDString());
        }
        this.targetPeonyTaxcorpCase = targetPeonyTaxcorpCase;
    }
    
    private JFXButton constructTaxcorpJobAssignmentButton(){
        JFXButton btn = new JFXButton("Assign Job");
        btn.getStyleClass().add("peony-primary-button");
        btn.setButtonType(JFXButton.ButtonType.RAISED);
        btn.setTooltip(new Tooltip("Assign a job to others..."));
        btn.setGraphic(PeonyGraphic.getImageView("award_star_bronze.png"));
        btn.setOnAction((ActionEvent e) -> {
////            if (!PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.ASSIGN_TAXCORP_JOB)){
////                PeonyFaceUtils.displayErrorMessageDialog("You need to be authorized to do this operation.");
////                return;
////            }
            PeonyJobAssignmentDialog aPeonyJobAssignmentDialog = new PeonyJobAssignmentDialog(null, true);
            aPeonyJobAssignmentDialog.addPeonyFaceEventListener(this);
            aPeonyJobAssignmentDialog.launchPeonyJobAssignmentDialog("Assign Job", targetPeonyTaxcorpCase);
        });
        return btn;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pageTitleLabel.setText(targetPeonyTaxcorpCase.getTaxcorpCaseTitle());
        topButtonsHBox.getChildren().add(constructTaxcorpJobAssignmentButton());
        topButtonsHBox.getChildren().addAll(constructCommonTaxCaseFunctionalButtons());
        /**
         * initialize tabs
         */
        initializeTaxcorpProfileTab();
        intializePayrollTaxTab();
        intializeSalesTaxTab();
        intializeTaxReturnTab();
        intializeBillAndPaymentTab(taxcorpCaseTabPane, billAndPaymentTab, targetPeonyTaxcorpCase.getPeonyBillPaymentList(), targetPeonyTaxcorpCase);
        intializeArchivedDocumentsTab(taxcorpCaseTabPane, archivedDocumentsTab, targetPeonyTaxcorpCase.getPeonyArchivedFileList(), targetPeonyTaxcorpCase);
        intializeRelatedCommunicationTab(taxcorpCaseTabPane, relatedCommunicationTab, targetPeonyTaxcorpCase.getRelatedEmails(), targetPeonyTaxcorpCase);
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof PeonyTaxcorpCaseFinalized){
            broadcastPeonyFaceEventHappened(new CloseTopComponentRequest());
            broadcastPeonyFaceEventHappened(new CloseDialogRequest());
        }else if (event instanceof PeonyJobSaved){
            handlePeonyJobSaved(((PeonyJobSaved)event));
        }
    }

    private void intializePayrollTaxTab() {
        //TaxFilingType
        TaxcorpTaxFilingController taxcorpTaxFilingController = new TaxcorpTaxFilingController(targetPeonyTaxcorpCase, TaxFilingType.PAYROLL_TAX);
        taxcorpTaxFilingController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
        try {
            Pane aPane = taxcorpTaxFilingController.loadFxml();
            aPane.prefHeightProperty().bind(taxcorpCaseTabPane.heightProperty());
            aPane.prefWidthProperty().bind(taxcorpCaseTabPane.widthProperty());
            payrollTaxTab.setContent(aPane);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void intializeSalesTaxTab() {
        //TaxFilingType
        TaxcorpTaxFilingController taxcorpTaxFilingController = new TaxcorpTaxFilingController(targetPeonyTaxcorpCase, TaxFilingType.SALES_TAX);
        taxcorpTaxFilingController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
        try {
            Pane aPane = taxcorpTaxFilingController.loadFxml();
            aPane.prefHeightProperty().bind(taxcorpCaseTabPane.heightProperty());
            aPane.prefWidthProperty().bind(taxcorpCaseTabPane.widthProperty());
            salesTaxTab.setContent(aPane);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void intializeTaxReturnTab() {
        //TaxFilingType
        TaxcorpTaxFilingController taxcorpTaxFilingController = new TaxcorpTaxFilingController(targetPeonyTaxcorpCase, TaxFilingType.TAX_RETURN);
        taxcorpTaxFilingController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
        try {
            Pane aPane = taxcorpTaxFilingController.loadFxml();
            aPane.prefHeightProperty().bind(taxcorpCaseTabPane.heightProperty());
            aPane.prefWidthProperty().bind(taxcorpCaseTabPane.widthProperty());
            taxReturnTab.setContent(aPane);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    private void initializeTaxcorpProfileTab(){
        taxcorpProfileTabController = new TaxcorpProfileTabController(targetPeonyTaxcorpCase);
        taxcorpProfileTabController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
        try {
            Pane aPane = taxcorpProfileTabController.loadFxml();
            aPane.prefHeightProperty().bind(taxcorpCaseTabPane.heightProperty());
            aPane.prefWidthProperty().bind(taxcorpCaseTabPane.widthProperty());
            taxcorpProfileTab.setContent(aPane);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    protected void displayTaxCaseExportForPrintDialog() {
        TaxcorpCaseExportForPrintDialog aPreprintTaxcorpCaseDialog = new TaxcorpCaseExportForPrintDialog(null, true);
        aPreprintTaxcorpCaseDialog.launchTaxcorpCaseExportForPrintDialog("Preprint Selection:", targetPeonyTaxcorpCase);
    }

    private void handlePeonyJobSaved(PeonyJobSaved event) {
        
        PeonyJob peonyJob = event.getPeonyJob();
        
        PeonyMemo memo = new PeonyMemo();
        memo.setMemo(peonyJob.getJobContent());
        memo.setOperator(peonyJob.getJobCreator());
        targetPeonyTaxcorpCase.getPeonyMemoList().add(0, memo);
        taxcorpProfileTabController.publishPeonyMemo(memo);
        
        memo = new PeonyMemo();
        memo.setMemo(peonyJob.getJobAcceptorMemo());
        memo.setOperator(peonyJob.getJobCreator());
        targetPeonyTaxcorpCase.getPeonyMemoList().add(1, memo);
        taxcorpProfileTabController.publishPeonyMemo(memo);
        
        if (peonyJob.getJobNoteMemo() != null){
            memo = new PeonyMemo();
            memo.setMemo(peonyJob.getJobNoteMemo());
            memo.setOperator(peonyJob.getJobCreator());
            targetPeonyTaxcorpCase.getPeonyMemoList().add(2, memo);
            taxcorpProfileTabController.publishPeonyMemo(memo);
        }
    }

}
