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

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.peony.controls.PeonyButtonTableCell;
import com.zcomapproach.garden.peony.kernel.services.PeonyBusinessService;
import com.zcomapproach.garden.peony.security.PeonyPrivilege;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.data.validation.PeonyBillFieldRequirement;
import com.zcomapproach.garden.peony.view.dialogs.ConfirmDepositDialog;
import com.zcomapproach.garden.peony.view.dialogs.PeonyPaymentDialog;
import com.zcomapproach.garden.peony.view.events.PeonyBillPaymentDeletedEvent;
import com.zcomapproach.garden.peony.view.events.PeonyBillPaymentSavedEvent;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.events.PeonyPaymentStoredEvent;
import com.zcomapproach.garden.peony.view.events.PeonyPaymentDeletedEvent;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.constant.GardenDiscountType;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G02Bill;
import com.zcomapproach.garden.persistence.entity.G02Log;
import com.zcomapproach.garden.persistence.entity.G02Payment;
import com.zcomapproach.garden.persistence.peony.PeonyBillPayment;
import com.zcomapproach.garden.persistence.peony.PeonyPayment;
import com.zcomapproach.garden.persistence.peony.data.PeonyLogName;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.util.GardenData;
import com.zcomapproach.garden.util.GardenFaceX;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class PeonyBillAndPaymentsPaneController extends PeonyEntityOwnerFaceController implements PeonyFaceEventListener{
    @FXML
    private TextArea billContentTextArea;
    @FXML
    private TextField billPriceTextField;
    @FXML
    private TextField billDiscountTextField;
    @FXML
    private ComboBox<String> billDiscountTypeComboBox;
    @FXML
    private DatePicker billDueDatePicker;
    @FXML
    private Button saveBillButton;
    @FXML
    private Button deleteBillButton;
    @FXML
    private TableView<PeonyPayment> paymentsTableView;
////    @FXML
////    private Label discountedBillTotalLabel;
    @FXML
    private Label billBalanceLabel;
    @FXML
    private Button addPaymentButton;
    
    private final PeonyBillPayment targetPeonyBillPayment;

    public PeonyBillAndPaymentsPaneController(PeonyBillPayment targetPeonyBillPayment, Object targetOwner) {
        super(targetOwner);
        this.targetPeonyBillPayment = targetPeonyBillPayment;
    }

    public PeonyBillPayment getTargetPeonyBillPayment() {
        return targetPeonyBillPayment;
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof PeonyPaymentStoredEvent){
            handlePeonyPaymentAddedEvent((PeonyPaymentStoredEvent)event);
        }
    }

    private void handlePeonyPaymentAddedEvent(final PeonyPaymentStoredEvent event) {
        if (Platform.isFxApplicationThread()){
            handlePeonyPaymentAddedEventHelper(event);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    handlePeonyPaymentAddedEventHelper(event);
                }
            });
        }
    }
    
    private void handlePeonyPaymentAddedEventHelper(final PeonyPaymentStoredEvent event) {
        PeonyPayment thePeonyPayment = event.getPeonyPayment();
        if ((thePeonyPayment != null) && (thePeonyPayment.getPayment() != null)){
            List<PeonyPayment> aPeonyPaymentList = targetPeonyBillPayment.getPaymentList();
            PeonyPayment updatingPeonyPayment = null;
            for (PeonyPayment aPeonyPayment : aPeonyPaymentList){
                if (aPeonyPayment.getPayment().getPaymentUuid().equalsIgnoreCase(thePeonyPayment.getPayment().getPaymentUuid())){
                    updatingPeonyPayment = aPeonyPayment;
                    break;
                }
            }//for-loop
            if (updatingPeonyPayment != null){
                aPeonyPaymentList.remove(updatingPeonyPayment);
                paymentsTableView.getItems().remove(updatingPeonyPayment);
            }
            aPeonyPaymentList.add(thePeonyPayment);
            paymentsTableView.getItems().add(thePeonyPayment);
            paymentsTableView.refresh();
            billBalanceLabel.textProperty().setValue(targetPeonyBillPayment.getBillBalanceText());
////            discountedBillTotalLabel.textProperty().setValue(targetPeonyBillPayment.getBillDiscountedPriceText());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /**
         * The Bill
         */
        if (targetPeonyBillPayment.getBill().getBillTotal() == null){
            PeonyFaceUtils.initializeTextField(billPriceTextField, null, null, "Digit number, e.g. 80 or 80.00", this);
        }else{
            PeonyFaceUtils.initializeTextField(billPriceTextField, GardenData.formatDecimalPoints(targetPeonyBillPayment.getBill().getBillTotal().doubleValue(), 2), 
                                               null, "Digit number, e.g. 80 or 80.00", this);
        }
        if (targetPeonyBillPayment.getBill().getBillDiscount() == null){
            PeonyFaceUtils.initializeTextField(billDiscountTextField, null, null, "Digit number, e.g. 8 or 8.0", this);
        }else{
            PeonyFaceUtils.initializeTextField(billDiscountTextField, GardenData.formatDecimalPoints(targetPeonyBillPayment.getBill().getBillDiscount().doubleValue(), 2), 
                                               null, "Digit number, e.g. 8 or 8.0", this);
        }
        PeonyFaceUtils.initializeComboBox(billDiscountTypeComboBox, GardenDiscountType.getEnumValueList(false), 
                                          targetPeonyBillPayment.getBill().getBillDiscountType(), 
                                          GardenDiscountType.DOLLAR.value(), "Percentage(%) or dollar($)", this);
        PeonyFaceUtils.initializeDatePicker(billDueDatePicker, ZcaCalendar.convertToLocalDate(targetPeonyBillPayment.getBill().getBillDatetime()), 
                                            ZcaCalendar.convertToLocalDate(new Date()), "Due date when customers should pay off this bill.", this);
        PeonyFaceUtils.initializeTextArea(billContentTextArea, targetPeonyBillPayment.getBill().getBillContent(), 
                                          null, "Bill content, max 450 characters", this);
        saveBillButton.setOnAction((ActionEvent e) -> {
            saveTargetPeonyBillPayment();
        });
        deleteBillButton.setOnAction((ActionEvent e) -> {
            deleteTargetPeonyBillPayment();
        });
        /**
         * Payments for the bill
         */
        paymentsTableView.getColumns().clear();
        
        TableColumn<PeonyPayment, String> paymentTypeColumn = new TableColumn<>("Payment Type");
        paymentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("paymentType"));
        paymentTypeColumn.setPrefWidth(150.00);
        paymentsTableView.getColumns().add(paymentTypeColumn);
        
        TableColumn<PeonyPayment, String> paymentPriceColumn = new TableColumn<>("Paid");
        paymentPriceColumn.setCellValueFactory(new PropertyValueFactory<>("paymentPriceText"));
        paymentPriceColumn.setPrefWidth(100.00);
        paymentsTableView.getColumns().add(paymentPriceColumn);
        
        TableColumn<PeonyPayment, Date> paymentDateColumn = new TableColumn<>("Date");
        paymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentPriceDate"));
        paymentDateColumn.setPrefWidth(100.00);
        paymentsTableView.getColumns().add(paymentDateColumn);
        
        TableColumn<PeonyPayment, String> paymentSerialNumberColumn = new TableColumn<>("Serial #");
        paymentSerialNumberColumn.setCellValueFactory(new PropertyValueFactory<>("paymentSerialNumber"));
        paymentSerialNumberColumn.prefWidthProperty().setValue(150.00);
        paymentsTableView.getColumns().add(paymentSerialNumberColumn);
        
        TableColumn<PeonyPayment, String> paymentMemoColumn = new TableColumn<>("Memo");
        paymentMemoColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMemo"));
        paymentMemoColumn.prefWidthProperty().setValue(350.00);
        paymentsTableView.getColumns().add(paymentMemoColumn);
        
        TableColumn<PeonyPayment, String> paymentStatusColumn = new TableColumn<>("Deposited");
        paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("depositStatus"));
        paymentStatusColumn.setPrefWidth(100.00);
        paymentsTableView.getColumns().add(paymentStatusColumn);
        
        TableColumn editPaymentColumn = new TableColumn("");
        editPaymentColumn.setCellFactory(PeonyButtonTableCell.<PeonyPayment>callbackForTableColumn(
                "Edit", (PeonyPayment aPeonyPayment) -> {
                    editPeonyPayment(aPeonyPayment);
                    return aPeonyPayment;
                }));
        editPaymentColumn.setPrefWidth(75.00);
        paymentsTableView.getColumns().add(editPaymentColumn);
        
        TableColumn deletePaymentColumn = new TableColumn("");
        deletePaymentColumn.setCellFactory(PeonyButtonTableCell.<PeonyPayment>callbackForTableColumn(
                "Delete", (PeonyPayment aPeonyPayment) -> {
                    deletePeonyPayment(aPeonyPayment);
                    return aPeonyPayment;
                }));
        deletePaymentColumn.setPrefWidth(75.00);
        paymentsTableView.getColumns().add(deletePaymentColumn);
        
        if (PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.CONFIRM_PAYMENT_DEPOSIT)){
            TableColumn depositPaymentColumn = new TableColumn("");
            depositPaymentColumn.setCellFactory(PeonyButtonTableCell.<PeonyPayment>callbackForTableColumn(
                    "Confirm Deposit", (PeonyPayment aPeonyPayment) -> {
                        depositPeonyPayment(aPeonyPayment);
                        return aPeonyPayment;
                    }));
            depositPaymentColumn.setPrefWidth(150.00);
            paymentsTableView.getColumns().add(depositPaymentColumn);
        }
        
        paymentsTableView.setItems(FXCollections.observableArrayList(targetPeonyBillPayment.getPaymentList()));
        paymentsTableView.getSelectionModel().setCellSelectionEnabled(true);
        paymentsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        GardenFaceX.installCopyPasteHandler(paymentsTableView);
        
        addPaymentButton.setOnAction((ActionEvent e) -> {
            addNewPayment();
        });
        billBalanceLabel.textProperty().setValue(targetPeonyBillPayment.getBillBalanceText());
////        discountedBillTotalLabel.textProperty().setValue(targetPeonyBillPayment.getBillDiscountedPriceText());
    }

    private void depositPeonyPayment(PeonyPayment aPeonyPayment) {
        ConfirmDepositDialog aConfirmDepositDialog = new ConfirmDepositDialog(null, true);
        aConfirmDepositDialog.addPeonyFaceEventListener(this);
        aConfirmDepositDialog.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
        aConfirmDepositDialog.launchPeonyPaymentDialog("Payment Deposit Confirmation", aPeonyPayment, getTargetOwner());
    }

    private void editPeonyPayment(final PeonyPayment aPeonyPayment) {
        if (SwingUtilities.isEventDispatchThread()){
            editPeonyPaymentHelper(aPeonyPayment);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    editPeonyPaymentHelper(aPeonyPayment);
                }
            });
        }
    }
    
    private void editPeonyPaymentHelper(final PeonyPayment aPeonyPayment){
        PeonyPaymentDialog aPaymentDataEntryDialog = new PeonyPaymentDialog(null, true);
        aPaymentDataEntryDialog.addPeonyFaceEventListener(this);
        aPaymentDataEntryDialog.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
        aPaymentDataEntryDialog.launchPeonyPaymentDialog("Payment for " +  targetPeonyBillPayment.getBillTitle(), aPeonyPayment, true, getTargetOwner());
    }

    private void saveTargetPeonyBillPayment() {
        Task<PeonyBillPayment> savePeonyBillPaymentTask = new Task<PeonyBillPayment>(){
            @Override
            protected PeonyBillPayment call() throws Exception {
                //Bill - employee who create this bill
                targetPeonyBillPayment.setOperator(PeonyProperties.getSingleton().getCurrentLoginEmployee());
                G02Bill aG02Bill = targetPeonyBillPayment.getBill();
                aG02Bill.setEmployeeUuid(targetPeonyBillPayment.getOperator().getAccount().getAccountUuid());
                //Bill - UUID
                if (ZcaValidator.isNullEmpty(aG02Bill.getBillUuid())){
                    aG02Bill.setBillUuid(GardenData.generateUUIDString());
                }
                aG02Bill.setEmployeeUuid(targetPeonyBillPayment.getOperator().getAccount().getAccountUuid());
                //Bill - content
                if (ZcaValidator.isNullEmpty(billContentTextArea.getText()) || (billContentTextArea.getText().length() > 450)){
                    updateMessage(PeonyBillFieldRequirement.BillContent.name());
                    return null;
                }
                aG02Bill.setBillContent(billContentTextArea.getText());
                //Bill - due date
                if (billDueDatePicker.getValue() == null){
                    updateMessage(PeonyBillFieldRequirement.BillDueDate.name());
                    return null;
                }
                aG02Bill.setBillDatetime(ZcaCalendar.convertToDate(billDueDatePicker.getValue()));
                //Bill - price
                if (ZcaValidator.isNullEmpty(billPriceTextField.getText())){
                    updateMessage(PeonyBillFieldRequirement.BillPrice.name());
                    return null;
                }else{
                    try{
                        aG02Bill.setBillTotal(new BigDecimal(Double.parseDouble(billPriceTextField.getText())));
                    }catch(Exception ex){   //this use generic Exception on purpose for validation
                        updateMessage(PeonyBillFieldRequirement.BillPrice.name());
                        return null;
                    }
                }
                //Bill - discount
                if (ZcaValidator.isNullEmpty(billDiscountTextField.getText())){
                    billDiscountTextField.setText("0.00");
                }else{
                    try{
                        aG02Bill.setBillDiscount(Float.parseFloat(billDiscountTextField.getText()));
                    }catch(Exception ex){   //this use generic Exception on purpose for validation
                        billDiscountTextField.setText("0.00");
                        aG02Bill.setBillDiscount(0.0F);
                    }
                }
                //Bill - discount tpe
                aG02Bill.setBillDiscountType(billDiscountTypeComboBox.getSelectionModel().getSelectedItem());
                if (ZcaValidator.isNullEmpty(aG02Bill.getBillDiscountType())){
                    updateMessage(PeonyBillFieldRequirement.BillDueDate.name());
                    return null;
                }
                //Entity
                aG02Bill.setEntityType(getTargetEntityType().name());
                aG02Bill.setEntityUuid(getTargetEntityUuid());

                //Payments
                List<PeonyPayment> aPeonyPaymentList = targetPeonyBillPayment.getPaymentList();
                for (PeonyPayment aPeonyPayment : aPeonyPaymentList){
                    aPeonyPayment.getPayment().setBillUuid(aG02Bill.getBillUuid());
                }
                
                //save...
                PeonyBillPayment aPeonyBillPayment = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                                .storeEntity_XML(PeonyBillPayment.class, GardenRestParams.Business.storePeonyBillPaymentRestParams(), targetPeonyBillPayment);
                if (aPeonyBillPayment == null){
                    updateMessage("Failed to save this bill with payements.");
                    return null;
                }else{
                    updateMessage("Successfully saved this bill with payements.");
                    G02Log log = createNewG02LogInstance(PeonyLogName.STORED_BILL);
                    log.setLoggedEntityType(GardenEntityType.BILL.name());
                    log.setLoggedEntityUuid(aG02Bill.getBillUuid());
                    PeonyProperties.getSingleton().log(log);
                }
                return targetPeonyBillPayment;
            }

            @Override
            protected void succeeded() {
                //GUI...
                int msgType = JOptionPane.INFORMATION_MESSAGE;
                try {
                    PeonyBillPayment aPeonyBillPayment = get();
                    if (aPeonyBillPayment == null){
                        msgType = JOptionPane.ERROR_MESSAGE;
                    }else{
                        resetBillPaymentFieldStylesHelper();
                        broadcastPeonyFaceEventHappened(new PeonyBillPaymentSavedEvent(aPeonyBillPayment));
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }

                //display result information dialog
                String msg = getMessage();
                if (ZcaValidator.isNotNullEmpty(msg)){
                    PeonyBillFieldRequirement aPeonyBillFieldRequirement = PeonyBillFieldRequirement.convertEnumNameToType(msg);
                    if ((aPeonyBillFieldRequirement == null) || (PeonyBillFieldRequirement.UNKNOWN.equals(aPeonyBillFieldRequirement))){
                        PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, msg, "Save Bill", msgType);
                    }else{
                        /**
                         * Validation error: highlight the error field
                         */
                        Node aNode = null;
                        switch(aPeonyBillFieldRequirement){
                            case BillContent:
                                aNode = billContentTextArea;
                                break;
                            case BillPrice:
                                aNode = billPriceTextField;
                                break;
                            case BillDiscount:
                                aNode = billDiscountTextField;
                                break;
                            case BillDiscountType:
                                aNode = billDiscountTypeComboBox;
                                break;
                            case BillDueDate:
                                aNode = billDueDatePicker;
                                break;
                            
                        }
                        if (aNode != null){
                            aNode.setStyle("-fx-border-color: #ff0000;");
                        }
                        PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, aPeonyBillFieldRequirement.value(), "Save Bill", msgType);
                        billBalanceLabel.textProperty().setValue(targetPeonyBillPayment.getBillBalanceText());
////                        discountedBillTotalLabel.textProperty().setValue(targetPeonyBillPayment.getBillDiscountedPriceText());
                    }
                }
            }

            private void resetBillPaymentFieldStylesHelper() {
                billContentTextArea.setStyle(null);
                billPriceTextField.setStyle(null);
                billDiscountTextField.setStyle(null);
                billDiscountTypeComboBox.setStyle(null);
                billDueDatePicker.setStyle(null);
            }
        };
        getCachedThreadPoolExecutorService().submit(savePeonyBillPaymentTask);
    }

    private void deleteTargetPeonyBillPayment() {
        NotifyDescriptor aNotifyDescriptor = new NotifyDescriptor
                .Confirmation("Are you sure to delete this bill?");
        if (DialogDisplayer.getDefault().notify(aNotifyDescriptor) != DialogDescriptor.YES_OPTION){
            return;
        }
        Task<PeonyBillPayment> deletePeonyBillPaymentTask = new Task<PeonyBillPayment>(){
            @Override
            protected PeonyBillPayment call() throws Exception {
                if (!targetPeonyBillPayment.isNewEntity()){
                    try{
                        G02Bill aG02Bill = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                            .deleteEntity_XML(G02Bill.class, 
                                            GardenRestParams.Business.deleteBillRestParams(targetPeonyBillPayment.getBill().getBillUuid()));
                        if (aG02Bill == null){
                            updateMessage("The server side cannot delete this record for some technical reason.");
                        }else{
                            G02Log log = createNewG02LogInstance(PeonyLogName.DELETED_BILL);
                            log.setLoggedEntityType(GardenEntityType.BILL.name());
                            log.setLoggedEntityUuid(aG02Bill.getBillUuid());
                            PeonyProperties.getSingleton().log(log);
                        }
                    }catch (Exception ex){
                        updateMessage("Deletion failed. " + ex.getMessage());
                        return null;
                    }
                }
                return targetPeonyBillPayment;
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyBillPayment aPeonyBillPayment = get();
                    if (aPeonyBillPayment == null){
                        String msg = getMessage();
                        if (ZcaValidator.isNotNullEmpty(msg)){
                            PeonyFaceUtils.displayErrorMessageDialog(msg);
                        }
                    }else{
                        broadcastPeonyFaceEventHappened(new PeonyBillPaymentDeletedEvent(aPeonyBillPayment));
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(deletePeonyBillPaymentTask);
    }
    
    /**
     * This method assumes that the payment is brand-new but not existing one
     */
    private void addNewPayment(){
        Task<PeonyPayment> addNewPaymentTask = new Task<PeonyPayment>(){
            @Override
            protected PeonyPayment call() throws Exception {
                final String billUuid = targetPeonyBillPayment.getBill().getBillUuid();
                final PeonyPayment aPeonyPayment = new PeonyPayment();
                aPeonyPayment.assignEntityUuid();
                aPeonyPayment.setOperator(PeonyProperties.getSingleton().getCurrentLoginEmployee());
                G02Payment aG02Payment = aPeonyPayment.getPayment();
                aG02Payment.setCreated(new Date());
                aG02Payment.setEmployeeUuid(aPeonyPayment.getOperator().getAccount().getAccountUuid());
                aG02Payment.setBillUuid(billUuid);
                //check if the owner, i.e. G02Bill existed
                G02Bill aG02Bill = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                            .findEntity_XML(G02Bill.class, GardenRestParams.Business.findBillEntityRestParams(billUuid));
                if (aG02Bill == null){
                    updateMessage("Cannot add a new payment for now. Please create and save the bill content before request this operation.");
                    return null;
                }else{
                    return aPeonyPayment;
                }
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyPayment aPeonyPayment = get();
                    if (aPeonyPayment == null){
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }else{
                        SwingUtilities.invokeLater(new Runnable(){
                            @Override
                            public void run() {
                                PeonyPaymentDialog aPaymentDataEntryDialog = new PeonyPaymentDialog(null, true);
                                aPaymentDataEntryDialog.addPeonyFaceEventListener(PeonyBillAndPaymentsPaneController.this);
                                aPaymentDataEntryDialog.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
                                aPaymentDataEntryDialog.launchPeonyPaymentDialog("Payment for " 
                                        +  targetPeonyBillPayment.getBillTitle(), aPeonyPayment, true, getTargetOwner());
                            }
                        });
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        
        };
        getCachedThreadPoolExecutorService().submit(addNewPaymentTask);
    }
    
    private void deletePeonyPayment(PeonyPayment aPeonyPayment) {
        NotifyDescriptor aNotifyDescriptor = new NotifyDescriptor
                .Confirmation("Are you sure to delete this payment permenantly from the database?");
        if (DialogDisplayer.getDefault().notify(aNotifyDescriptor) != DialogDescriptor.YES_OPTION){
            return;
        }
        Task<PeonyPayment> deletePeonyPaymentTask = new Task<PeonyPayment>(){
            @Override
            protected PeonyPayment call() throws Exception {
                try{
                    G02Payment aG02Payment = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                        .deleteEntity_XML(G02Payment.class, 
                                        GardenRestParams.Business.deletePaymentRestParams(aPeonyPayment.getPayment().getPaymentUuid()));
                    if (aG02Payment == null){
                        updateMessage("The server side cannot delete this record for some technical reason.");
                    }else{
                        G02Log log = createNewG02LogInstance(PeonyLogName.DELETED_PAYMENT);
                        log.setLoggedEntityType(GardenEntityType.PAYMENT.name());
                        log.setLoggedEntityUuid(aPeonyPayment.getPayment().getPaymentUuid());
                        PeonyProperties.getSingleton().log(log);
                    }
                }catch (Exception ex){
                    updateMessage("Deletion failed. " + ex.getMessage());
                    return null;
                }
                return aPeonyPayment;
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyPayment result = get();
                    if (result == null){
                        String msg = getMessage();
                        if (ZcaValidator.isNotNullEmpty(msg)){
                            PeonyFaceUtils.displayErrorMessageDialog(msg);
                        }
                    }else{

                        ObservableList<PeonyPayment> aPeonyPaymentList = paymentsTableView.getItems();
                        PeonyPayment thePeonyPayment = null;
                        for (PeonyPayment aPeonyPayment : aPeonyPaymentList){
                            if (aPeonyPayment.getPayment().getPaymentUuid().equalsIgnoreCase(result.getPayment().getPaymentUuid())){
                                thePeonyPayment = result;
                                break;
                            }
                        }
                        paymentsTableView.getItems().remove(thePeonyPayment);
                        paymentsTableView.refresh();
                        targetPeonyBillPayment.getPaymentList().remove(result);
                        billBalanceLabel.textProperty().setValue(targetPeonyBillPayment.getBillBalanceText());
////                        discountedBillTotalLabel.textProperty().setValue(targetPeonyBillPayment.getBillDiscountedPriceText());
                        
                        broadcastPeonyFaceEventHappened(new PeonyPaymentDeletedEvent(thePeonyPayment));
                        
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(deletePeonyPaymentTask);
    }
    
}
