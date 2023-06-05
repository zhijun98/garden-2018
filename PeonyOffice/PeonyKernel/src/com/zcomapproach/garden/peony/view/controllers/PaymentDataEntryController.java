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
import com.zcomapproach.garden.peony.kernel.services.PeonyBusinessService;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.peony.view.events.PeonyPaymentStoredEvent;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.constant.GardenPayemenType;
import com.zcomapproach.garden.persistence.entity.G02Log;
import com.zcomapproach.garden.persistence.entity.G02Payment;
import com.zcomapproach.garden.persistence.peony.PeonyPayment;
import com.zcomapproach.garden.persistence.peony.data.PeonyLogName;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.garden.peony.PeonyLauncher;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.swing.JOptionPane;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class PaymentDataEntryController extends PeonyEntityOwnerFaceController {
    @FXML
    private ComboBox<String> paymentTypeComboBox;
    @FXML
    private TextField paymentPriceField;
    @FXML
    private TextField serialNumberField;
    @FXML
    private TextArea paymentMemo;
    @FXML
    private DatePicker paymentDatePicker;
    @FXML
    private Button savePaymentButton;
    @FXML
    private Button cancelPaymentButton;
    
    private final PeonyPayment targetPeonyPayment;
    private final boolean saveBtnRequired;

    public PaymentDataEntryController(PeonyPayment targetPeonyPayment, boolean saveBtnRequired, Object targetOwner) {
        super(targetOwner);
        this.targetPeonyPayment = targetPeonyPayment;
        this.saveBtnRequired = saveBtnRequired;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        PeonyFaceUtils.initializeComboBox(paymentTypeComboBox, GardenPayemenType.getEnumValueList(false), 
                targetPeonyPayment.getPaymentType(), GardenPayemenType.CASH.value(), "Payment method type", this);
        PeonyFaceUtils.initializeTextField(paymentPriceField, targetPeonyPayment.getPaymentPriceText(), null, "Digit number, e.g. 21.8", this);
        PeonyFaceUtils.initializeTextArea(paymentMemo, targetPeonyPayment.getPaymentMemo(), null, "Payment memo, max 450 characters", this);
        PeonyFaceUtils.initializeDatePicker(paymentDatePicker, ZcaCalendar.convertToLocalDate(targetPeonyPayment.getPayment().getPaymentDate()), 
                ZcaCalendar.convertToLocalDate(new Date()), "The date when this payment was completed", this);
        
        if (saveBtnRequired){
            savePaymentButton.setOnAction((ActionEvent event) -> {
                savePayment();
            });
        }else{
            savePaymentButton.setVisible(false);
            paymentPriceField.setDisable(true);
            paymentMemo.setDisable(true);
            paymentDatePicker.setDisable(true);
        }
        
        cancelPaymentButton.setOnAction((ActionEvent event) -> {
            broadcastPeonyFaceEventHappened(new CloseDialogRequest());
        });
    }

    private void savePayment() {
        Task<PeonyPayment> savePeonyPaymentTask = new Task<PeonyPayment>(){
            @Override
            protected PeonyPayment call() throws Exception {
                try{
                    G02Payment aG02Payment = targetPeonyPayment.getPayment();
                    aG02Payment.setPaymentMemo(paymentMemo.textProperty().getValue());
                    try{
                        aG02Payment.setPaymentPrice(new BigDecimal(Double.parseDouble(paymentPriceField.getText())));
                    }catch(NumberFormatException ex){
                        updateMessage("Field: Paid - it demands a digit number");
                        return null;
                    }
                    String paymentType = paymentTypeComboBox.getValue();
                    if (ZcaValidator.isNullEmpty(paymentType)){
                        updateMessage("Field: payment - it demands to select a payment method");
                        return null;
                    }else{
                        if (GardenPayemenType.BUSINESS_CHECK_UPLOADED.value().equalsIgnoreCase(paymentType)
                                || GardenPayemenType.PERSONAL_CHECK_UPLOADED.value().equalsIgnoreCase(paymentType))
                        {
                            if (ZcaValidator.isNullEmpty(serialNumberField.getText())){
                                updateMessage("Field: serial# - it demands to record check-number for this payment type");
                                return null;
                            }else{
                                aG02Payment.setPaymentSerialNumber(serialNumberField.getText());
                            }
                        }
                        aG02Payment.setPaymentType(paymentType);
                        //
                    }
                    aG02Payment.setPaymentDate(ZcaCalendar.convertToDate(paymentDatePicker.getValue()));
                    if (aG02Payment.getPaymentDate() == null){
                        updateMessage("Field: Date - it demands a date when this payment was completed");
                        return null;
                    }

                    /**
                     * This assume that the owner, i.e. Bill entity exists in the database
                     */
                    targetPeonyPayment.setOperator(PeonyProperties.getSingleton().getCurrentLoginEmployee());
                    targetPeonyPayment.getPayment().setEmployeeUuid(targetPeonyPayment.getOperator().getAccount().getAccountUuid());
                    //save...
                    PeonyPayment aPeonyPayment = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                                .storeEntity_XML(PeonyPayment.class, GardenRestParams.Business.storePeonyPaymentRestParams(), targetPeonyPayment);
                    if (aPeonyPayment == null){
                        updateMessage("Failed to save this payment.");
                        return null;
                    }else{
                        updateMessage("Successfully saved this payment.");
                        G02Log log = createNewG02LogInstance(PeonyLogName.STORED_PAYMENT);
                        log.setLoggedEntityType(GardenEntityType.PAYMENT.name());
                        log.setLoggedEntityUuid(aPeonyPayment.getPayment().getPaymentUuid());
                        PeonyProperties.getSingleton().log(log);
                    }
                    return targetPeonyPayment;
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
                    PeonyPayment aPeonyPayment = get();
                    if (aPeonyPayment == null){
                        msgType = JOptionPane.ERROR_MESSAGE;
                    }else{
                        //data entry mode
                        setDataEntryChanged(false);
                        //added or updated a payment
                        broadcastPeonyFaceEventHappened(new PeonyPaymentStoredEvent(targetPeonyPayment));
                        //close the data entry dialog
                        broadcastPeonyFaceEventHappened(new CloseDialogRequest());
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }

                //display result information dialog
                String msg = getMessage();
                if (ZcaValidator.isNotNullEmpty(msg)){
                    PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, msg, "Save Payment", msgType);
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(savePeonyPaymentTask);
    }
}
