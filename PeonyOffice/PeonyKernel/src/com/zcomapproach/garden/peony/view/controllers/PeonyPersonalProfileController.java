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

package com.zcomapproach.garden.peony.view.controllers;

import com.jfoenix.controls.JFXButton;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxcorpService;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.data.PeonyCommAssignmentTreeItemData;
import com.zcomapproach.garden.peony.view.data.PeonyTaxCaseTreeItemData;
import com.zcomapproach.garden.peony.view.data.PeonyTreeItemData;
import com.zcomapproach.garden.persistence.entity.G02CommAssignmentTarget;
import com.zcomapproach.garden.persistence.peony.PeonyAccount;
import com.zcomapproach.garden.persistence.peony.PeonyCommAssignment;
import com.zcomapproach.garden.persistence.peony.PeonyCommAssignmentList;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.persistence.peony.TaxcorpCaseBrief;
import com.zcomapproach.garden.persistence.peony.TaxpayerCaseBrief;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.security.PeonyPrivilege;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 * It
 * @author zhijun98
 */
public class PeonyPersonalProfileController extends PeonyFaceController {
    @FXML
    private Label titleLabel;
    @FXML
    private TabPane profileTabPane;
    @FXML
    private Tab employeeTab;
    @FXML
    private AnchorPane employeeAnchorPane;
    @FXML
    private Tab basicInformationTab;
    @FXML
    private AnchorPane basicInformationAnchorPane;
    @FXML
    private Tab taxCaseTab;
    @FXML
    private HBox taxCaseFunctionalBox;
    @FXML
    private DatePicker cloneTaxpayerDeadlineDatePicker;
    @FXML
    private Button cloneAllTaxpayerCasesButton;
    @FXML
    private TreeView<PeonyTaxCaseTreeItemData> taxCaseTreeView;
    @FXML
    private Tab smsHistoryTab;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private JFXButton refreshSMSHistoryButton;
    @FXML
    private TreeView<PeonyCommAssignmentTreeItemData> smsHistoryTreeView;
    @FXML
    private JFXButton logsButton;
    @FXML
    private FlowPane buttonsFlowPane;

    private final TreeItem<PeonyTaxCaseTreeItemData> taxpayerRoot = new TreeItem<>(new PeonyTaxCaseTreeItemData("Taxpayer Case:"));
    private final TreeItem<PeonyTaxCaseTreeItemData> taxcorpRoot = new TreeItem<>(new PeonyTaxCaseTreeItemData("Taxcorp Case:"));
    
    private final PeonyAccount targetPeonyAccount;
    
    /**
     * 
     * @param targetPeonyAccountProfile - it could be employee-type
     */
    public PeonyPersonalProfileController(PeonyAccount targetPeonyAccountProfile) {
        this.targetPeonyAccount = targetPeonyAccountProfile;
    }

    private boolean isForEmployee(){
        return targetPeonyAccount instanceof PeonyEmployee;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (isForEmployee()){
            
            titleLabel.setText("Employee Profile: " + targetPeonyAccount.getPeonyUserFullName());
            
            logsButton.setGraphic(PeonyGraphic.getImageView("date_task.png"));
            logsButton.setOnAction((ActionEvent event) -> {
                Lookup.getDefault().lookup(PeonyManagementService.class).displayEmployeeWorkLogsTopComponent((PeonyEmployee)targetPeonyAccount);
            });
        }else{
            titleLabel.setText("Customer Profile: " + targetPeonyAccount.getPeonyUserFullName());
            buttonsFlowPane.getChildren().remove(logsButton);
            profileTabPane.getTabs().remove(employeeTab);
            profileTabPane.getTabs().remove(smsHistoryTab);
        }
        
        if (targetPeonyAccount instanceof PeonyEmployee){
            //Employee data entry part
            EmployeeDataEntryController employeeDataEntryController = new EmployeeDataEntryController((PeonyEmployee)targetPeonyAccount);
            try {
                Pane aPane = employeeDataEntryController.loadFxml();
                AnchorPane.setBottomAnchor(aPane, 0.0);
                AnchorPane.setLeftAnchor(aPane, 0.0);
                AnchorPane.setRightAnchor(aPane, 0.0);
                AnchorPane.setTopAnchor(aPane, 0.0);
                employeeAnchorPane.getChildren().add(aPane);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
        //Account data entry part
        PeonyAccountController aPeonyAccountController = new PeonyAccountController(targetPeonyAccount);
        try {
            Pane aPane = aPeonyAccountController.loadFxml();
            AnchorPane.setBottomAnchor(aPane, 0.0);
            AnchorPane.setLeftAnchor(aPane, 0.0);
            AnchorPane.setRightAnchor(aPane, 0.0);
            AnchorPane.setTopAnchor(aPane, 0.0);
            basicInformationAnchorPane.getChildren().add(aPane);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        initializeTaxCaseTreeView();
        
        initializeSmsHistoryViewTab();
        
        
        Date deadline = ZcaCalendar.createDate((new GregorianCalendar()).get(Calendar.YEAR), Calendar.APRIL, 15, 0, 0, 0);
        cloneTaxpayerDeadlineDatePicker.setValue(ZcaCalendar.convertToLocalDate(deadline));
        cloneAllTaxpayerCasesButton.setOnAction(evt -> {
            if (!PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.CENTRAL_ADMIN)){
                PeonyFaceUtils.displayErrorMessageDialog("Sorry, you have no privilege to do this operation.");
                return;
            }
            if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to clone all the taxpayer cases with deadline " + ZcaCalendar.convertToMMddyyyy(deadline, "-") + "?") == JOptionPane.YES_OPTION){
                this.getCachedThreadPoolExecutorService().submit(new Task<String>(){
                    @Override
                    protected String call() throws Exception {
                        Date deadline = ZcaCalendar.convertToDate(cloneTaxpayerDeadlineDatePicker.getValue());
                        return Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient() 
                            .findEntity_XML(String.class,
                                    GardenRestParams.Taxpayer.cloneAllTaxpayerCasesRestParams(String.valueOf(deadline.getTime())));
                    }

                    @Override
                    protected void succeeded() {
                        try {
                            PeonyFaceUtils.displayWarningMessageDialog(get());
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                });
            }
        });
    }

    private void initializeTaxCaseTreeView() {
        TreeItem<PeonyTaxCaseTreeItemData> peonyTaxCaseTreeRoot = new TreeItem<>(new PeonyTaxCaseTreeItemData());
        
        //TaxpayerCaseBrief...
        List<TaxpayerCaseBrief> aTaxpayerCaseBriefList = targetPeonyAccount.getTaxyerCaseBriefList();
        if (aTaxpayerCaseBriefList != null){
            for (TaxpayerCaseBrief aTaxpayerCaseBrief : aTaxpayerCaseBriefList){
                taxpayerRoot.getChildren().add(new TreeItem<>(new PeonyTaxCaseTreeItemData(aTaxpayerCaseBrief)));
            }
        }
        taxpayerRoot.setExpanded(true);
        peonyTaxCaseTreeRoot.getChildren().add(taxpayerRoot);
        
        //TaxcorpCaseBrief....
        List<TaxcorpCaseBrief> aTaxcorpCaseBriefList = targetPeonyAccount.getTaxcorpCaseBriefList();
        if (aTaxcorpCaseBriefList != null){
            for (TaxcorpCaseBrief aTaxcorpCaseBrief : aTaxcorpCaseBriefList){
                taxcorpRoot.getChildren().add(new TreeItem<>(new PeonyTaxCaseTreeItemData(aTaxcorpCaseBrief)));
            }
        }
        taxcorpRoot.setExpanded(true);
        peonyTaxCaseTreeRoot.getChildren().add(taxcorpRoot);
        
        peonyTaxCaseTreeRoot.setExpanded(true);
        taxCaseTreeView.setRoot(peonyTaxCaseTreeRoot);
        
        ContextMenu contextMenu = new ContextMenu();
        MenuItem viewMenuItem = new MenuItem("View Selected Item");
        viewMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                viewTaxCaseTreeItemData();
            }
        });
        contextMenu.getItems().add(viewMenuItem);
        
        taxCaseTreeView.setContextMenu(contextMenu);
    }
    
    private void initializeSmsHistoryViewTab(){
        Date toDate = ZcaCalendar.covertDateToEnding(new Date());
        Date fromDate = ZcaCalendar.addDates(toDate, -7);
        toDatePicker.setValue(ZcaCalendar.convertToLocalDate(toDate));
        fromDatePicker.setValue(ZcaCalendar.convertToLocalDate(fromDate));
        refreshSMSHistoryButton.setOnAction((ActionEvent actionEvent) -> {
            reloadSmsHistoryTreeView();
        });
        smsHistoryTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        reloadSmsHistoryTreeView();
    }

    private void viewTaxCaseTreeItemData() {
        if (SwingUtilities.isEventDispatchThread()){
            viewTaxCaseTreeItemDataHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    viewTaxCaseTreeItemDataHelper();
                }
            });
        }
    }
    
    private void viewTaxCaseTreeItemDataHelper(){
        TreeItem<PeonyTaxCaseTreeItemData> treeItemData = taxCaseTreeView.getSelectionModel().getSelectedItem();
        if ((treeItemData == null) || (treeItemData.getValue() == null) || (treeItemData.getValue().getTreeItemTitle() != null)){
            PeonyFaceUtils.displayErrorMessageDialog("Please select a taxpayer case or a taxcorp case.");
        }else if (treeItemData.getValue().getTaxpayer()!= null){
            Lookup.getDefault().lookup(PeonyTaxpayerService.class).launchPeonyTaxpayerCaseTopComponentByTaxpayerCaseUuid(treeItemData.getValue().getTaxpayer().getTaxpayerCaseUuid());
        }else if (treeItemData.getValue().getTaxcorp()!= null){
            Lookup.getDefault().lookup(PeonyTaxcorpService.class).launchPeonyTaxcorpCaseTopComponentByTaxcorpCaseUuid(treeItemData.getValue().getTaxcorp().getTaxcorpCaseUuid());
        }
    }
    
    private void reloadSmsHistoryTreeView() {
        Task<TreeItem<PeonyCommAssignmentTreeItemData>> constructSmsHistoryTreeViewTask = new Task<TreeItem<PeonyCommAssignmentTreeItemData>>(){
            @Override
            protected TreeItem<PeonyCommAssignmentTreeItemData> call() throws Exception {
                TreeItem<PeonyCommAssignmentTreeItemData> treeRootItem = new TreeItem<>();
                treeRootItem.setValue(new PeonyCommAssignmentTreeItemData(null, null, null));
                Date fromDate = ZcaCalendar.convertToDate(fromDatePicker.getValue());
                Date toDate = ZcaCalendar.convertToDate(toDatePicker.getValue());
                PeonyCommAssignmentList aPeonyCommAssignmentList = Lookup.getDefault().lookup(PeonyManagementService.class)
                        .getPeonyManagementRestClient().findEntity_XML(PeonyCommAssignmentList.class, 
                                GardenRestParams.Management.findSmsHistoryListByEmployeeByPeriodRestParams(
                                        targetPeonyAccount.getAccount().getAccountUuid(), 
                                        String.valueOf(fromDate.getTime()), String.valueOf(toDate.getTime())));
                if (aPeonyCommAssignmentList != null){
                    List<PeonyCommAssignment> peonyCommAssignments = aPeonyCommAssignmentList.getPeonyCommAssignmentList();
                    if (peonyCommAssignments != null){
                        List<G02CommAssignmentTarget> aG02CommAssignmentTargetList;
                        TreeItem<PeonyCommAssignmentTreeItemData> parentTreeItem;
                        TreeItem<PeonyCommAssignmentTreeItemData> childTreeItem;
                        for (PeonyCommAssignment aPeonyCommAssignment : peonyCommAssignments){
                            parentTreeItem = new TreeItem<>();
                            parentTreeItem.setValue(new PeonyCommAssignmentTreeItemData(aPeonyCommAssignment.getCommAssignment(), 
                                                                                        targetPeonyAccount, PeonyTreeItemData.Status.INITIAL));
                            aG02CommAssignmentTargetList = aPeonyCommAssignment.getCommAssignmentTargets();
                            if (aG02CommAssignmentTargetList != null){
                                for (G02CommAssignmentTarget aG02CommAssignmentTarget : aG02CommAssignmentTargetList){
                                    childTreeItem = new TreeItem<>();
                                    childTreeItem.setValue(new PeonyCommAssignmentTreeItemData(aG02CommAssignmentTarget, 
                                                                                                targetPeonyAccount, PeonyTreeItemData.Status.INITIAL));
                                    parentTreeItem.getChildren().add(childTreeItem);
                                    parentTreeItem.setExpanded(true);
                                }
                            }
                            treeRootItem.getChildren().add(parentTreeItem);
                            treeRootItem.setExpanded(true);
                        }//for
                    }
                }
                return treeRootItem;
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.publishMessageOntoOutputWindow("Cannot publish the history of SMS. " + getMessage());
            }

            @Override
            protected void succeeded() {
                try {
                    smsHistoryTreeView.setRoot(get());
                    
                    smsHistoryTreeView.refresh();
                } catch (InterruptedException | ExecutionException ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.publishMessageOntoOutputWindow("Cannot publish the history of SMS because of InterruptedException or ExecutionException. " + ex.getMessage());
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(constructSmsHistoryTreeViewTask);
    }
}
