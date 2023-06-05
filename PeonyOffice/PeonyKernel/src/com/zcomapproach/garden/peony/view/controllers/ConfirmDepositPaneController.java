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
import com.zcomapproach.garden.data.constant.GardenBooleanValue;
import com.zcomapproach.garden.peony.kernel.services.PeonyBusinessService;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.peony.view.events.PeonyPaymentStoredEvent;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G02Log;
import com.zcomapproach.garden.persistence.peony.PeonyPayment;
import com.zcomapproach.garden.persistence.peony.data.PeonyLogName;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.ZcaCalendar;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 * LaunchTaxpayerCaseDialog uses this controller
 * @author zhijun98
 */
public class ConfirmDepositPaneController extends PeonyEntityOwnerFaceController{
    @FXML
    private Label paymentTypeLabel;
    @FXML
    private Label paidLabel;
    @FXML
    private Label paidDateLabel;
    @FXML
    private Label memoLabel;
    @FXML
    private ComboBox<String> confirmDepositComboBox;
    @FXML
    private DatePicker depositDatePicker;
    @FXML
    private TextField depositNoteTextField;
    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;
    
    private final PeonyPayment targetPeonyPayment;

    public ConfirmDepositPaneController(PeonyPayment targetPeonyPayment, Object targetOwner) {
        super(targetOwner);
        this.targetPeonyPayment = targetPeonyPayment;
    }

    @Override
    protected void decoratePeonyFaceAfterLoadingHelper(Scene scene) {
        depositNoteTextField.requestFocus();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        paymentTypeLabel.setText(targetPeonyPayment.getPaymentType());
        paidLabel.setText(targetPeonyPayment.getPaymentPriceText());
        paidDateLabel.setText(targetPeonyPayment.getPaymentPriceDate());
        memoLabel.setText(targetPeonyPayment.getPaymentMemo());
        
        PeonyFaceUtils.initializeComboBox(confirmDepositComboBox, 
                                        GardenBooleanValue.getEnumNameList(false), 
                                        targetPeonyPayment.getPayment().getDepositConfirmed(), 
                                        GardenBooleanValue.Yes.value(), 
                                        "Deposit status: Yes or No", this);
        
        if (targetPeonyPayment.getPayment().getDepositDate() == null){
            depositDatePicker.setValue(ZcaCalendar.convertToLocalDate(new Date()));
        }else{
            depositDatePicker.setValue(ZcaCalendar.convertToLocalDate(targetPeonyPayment.getPayment().getDepositDate()));
        }
        depositDatePicker.setTooltip(new Tooltip("Deposit date"));
        
        depositNoteTextField.setText(targetPeonyPayment.getPayment().getDepositNote());
        depositNoteTextField.setTooltip(new Tooltip("Short memo (max 150 characters) for this deposit"));
        depositNoteTextField.setOnKeyPressed((KeyEvent event) -> {
            if(event.getCode() == KeyCode.ENTER) {
                confirmTargetPaymentDeposit();
            }
        });
        
        cancelButton.setOnAction((ActionEvent actionEvent) -> {
            confirmButton.setDisable(false);
            broadcastPeonyFaceEventHappened(new CloseDialogRequest());
        });
        
        confirmButton.setOnAction((ActionEvent actionEvent) -> {
            confirmTargetPaymentDeposit();
        });
    }
    
    private void confirmTargetPaymentDeposit(){
        confirmButton.setDisable(true);
        //Launch Taxpayer Case...
        Task<PeonyPayment> confirmTargetPaymentDepositTask = new Task<PeonyPayment>(){
            @Override
            protected PeonyPayment call() throws Exception {
                targetPeonyPayment.getPayment().setDepositEmployeeUuid(targetPeonyPayment.getOperator().getAccount().getAccountUuid());
                //Deposit note...
                String note = depositNoteTextField.getText();
                if ((ZcaValidator.isNotNullEmpty(note)) && (note.length() > 150)){
                    confirmButton.setDisable(false);
                    updateMessage("Deposit note may have at most 150 characters.");
                    return null;
                }else{
                    targetPeonyPayment.getPayment().setDepositNote(note);
                }
                //Deposit date
                if (depositDatePicker.getValue() != null){
                    targetPeonyPayment.getPayment().setDepositDate(ZcaCalendar.convertToDate(depositDatePicker.getValue()));
                }
                
                targetPeonyPayment.getPayment().setDepositConfirmed(confirmDepositComboBox.getValue());
                
                try{
                    PeonyPayment aPeonyPayment = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                                .storeEntity_XML(PeonyPayment.class, GardenRestParams.Business.storePeonyPaymentRestParams(), targetPeonyPayment);
                    G02Log log = createNewG02LogInstance(PeonyLogName.CONFIRM_PAYEMENT_DEPOSIT);
                    log.setLoggedEntityType(GardenEntityType.PAYMENT.name());
                    log.setLoggedEntityUuid(aPeonyPayment.getPayment().getPaymentUuid());
                    PeonyProperties.getSingleton().log(log);
                    return aPeonyPayment;
                }catch (Exception ex){
                    updateMessage("Cannot process deposit-confirmation for now because of technical reasons.");
                    return null;
                }
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyPayment result = get();
                    if (result == null){
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }else{
                        PeonyFaceUtils.displayInformationMessageDialog("Deposit for the payment has been confirmed.");
                        //data entry mode
                        setDataEntryChanged(false);
                        //added or updated a payment
                        broadcastPeonyFaceEventHappened(new PeonyPaymentStoredEvent(targetPeonyPayment));
                        broadcastPeonyFaceEventHappened(new CloseDialogRequest());
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(confirmTargetPaymentDepositTask);
    }
}
