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

import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.data.constant.GardenBooleanValue;
import com.zcomapproach.garden.peony.view.events.PeonyBillPaymentDeletedEvent;
import com.zcomapproach.garden.peony.view.events.PeonyBillPaymentSavedEvent;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.events.PeonyPaymentStoredEvent;
import com.zcomapproach.garden.peony.view.events.PeonyPaymentDeletedEvent;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.peony.PeonyBillPayment;
import com.zcomapproach.garden.persistence.peony.PeonyPayment;
import com.zcomapproach.garden.persistence.peony.PeonyTaxcorpCase;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import org.openide.util.Exceptions;

/**
 * @author zhijun98
 */
public class PeonyBillAndPaymentController extends PeonyEntityOwnerFaceController implements PeonyFaceEventListener{
    @FXML
    private TabPane billsAndPaymentsTabPane;
    @FXML
    private Button createNewBillButton;
    @FXML
    private ComboBox<PeonyBillPayment> billListComboBox;
    
    private final List<PeonyBillPayment> targetPeonyBillPaymentList;
    
    private final HashMap<String, PeonyBillAndPaymentsPaneController> peonyBillPaymentsPaneControllerStorage = new HashMap<>();

    public PeonyBillAndPaymentController(List<PeonyBillPayment> targetPeonyBillPaymentList, Object targetOwner) {
        super(targetOwner);
        this.targetPeonyBillPaymentList = targetPeonyBillPaymentList;
    }

    private PeonyBillPayment findTargetPeonyBillPayment(String billUuid){
        if (targetPeonyBillPaymentList != null){
            for (PeonyBillPayment aPeonyBillPayment : targetPeonyBillPaymentList){
                return aPeonyBillPayment;
            }
        }
        return null;
    }
    
    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof PeonyBillPaymentSavedEvent){
            handlePeonyBillPaymentSavedEvent((PeonyBillPaymentSavedEvent) event);
        }else if (event instanceof PeonyBillPaymentDeletedEvent){
            handlePeonyBillPaymentDeletedEvent((PeonyBillPaymentDeletedEvent) event);
        }else if (event instanceof PeonyPaymentDeletedEvent){
            handlePeonyPaymentDeletedEvent((PeonyPaymentDeletedEvent)event);
        }else if (event instanceof PeonyPaymentStoredEvent){
            handlePeonyPaymentAddedOrUpdatedEvent((PeonyPaymentStoredEvent)event);
        }
    }
    
    private void handlePeonyBillPaymentSavedEvent(final PeonyBillPaymentSavedEvent event){
        if ((event.getSavedPeonyBillPayment() == null) || (event.getSavedPeonyBillPayment().getBill() == null) 
                || (ZcaValidator.isNullEmpty(event.getSavedPeonyBillPayment().getBill().getBillUuid())))
        {
            return;
        }
        updateBillsAndPaymentsTabTitle(event.getSavedPeonyBillPayment().getBill().getBillUuid());
        updateBillListComboBoxForSave(event.getSavedPeonyBillPayment());
    }
    
    private void handlePeonyPaymentAddedOrUpdatedEvent(final PeonyPaymentStoredEvent event){
        PeonyPayment aPeonyPayment = event.getPeonyPayment();
        if ((aPeonyPayment == null) || (aPeonyPayment.getPayment() == null) || (ZcaValidator.isNullEmpty(aPeonyPayment.getPayment().getBillUuid()))){
            return;
        }
        updateBillsAndPaymentsTabTitle(aPeonyPayment.getPayment().getBillUuid());
        updateBillListComboBoxForSave(findTargetPeonyBillPayment(aPeonyPayment.getPayment().getBillUuid()));
    }
    
    private void handlePeonyPaymentDeletedEvent(final PeonyPaymentDeletedEvent event){
        PeonyPayment aPeonyPayment = event.getPeonyPayment();
        if ((aPeonyPayment == null) || (aPeonyPayment.getPayment() == null) || (ZcaValidator.isNullEmpty(aPeonyPayment.getPayment().getBillUuid()))){
            return;
        }
        updateBillsAndPaymentsTabTitle(aPeonyPayment.getPayment().getBillUuid());
        updateBillListComboBoxForSave(findTargetPeonyBillPayment(aPeonyPayment.getPayment().getBillUuid()));
    }
    
    private void updateBillsAndPaymentsTabTitle(final String billUuid){
        if (Platform.isFxApplicationThread()){
            updateBillsAndPaymentsTabTitleHelper(billUuid);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    updateBillsAndPaymentsTabTitleHelper(billUuid);
                }
            });
        }
    }
    
    private void updateBillsAndPaymentsTabTitleHelper(String billUuid){
        PeonyBillAndPaymentsPaneController aPeonyBillAndPaymentsPaneController = peonyBillPaymentsPaneControllerStorage.get(billUuid);
        if (aPeonyBillAndPaymentsPaneController != null){
            Tab aTab = findBillsAndPaymentsTab(billUuid);
            if (aTab != null){
                aTab.setText(aPeonyBillAndPaymentsPaneController.getTargetPeonyBillPayment().getBillTitle());
            }
        }
    }

    private void handlePeonyBillPaymentDeletedEvent(final PeonyBillPaymentDeletedEvent event) {
        PeonyBillPayment thePeonyBillPayment  = event.getDeletedPeonyBillPayment();
        if ((thePeonyBillPayment != null) && (thePeonyBillPayment.getBill() != null) 
                && (ZcaValidator.isNotNullEmpty(thePeonyBillPayment.getBill().getBillUuid())))
        {
            String billUuid = thePeonyBillPayment.getBill().getBillUuid();
            PeonyBillAndPaymentsPaneController aPeonyBillAndPaymentsPaneController = peonyBillPaymentsPaneControllerStorage.get(billUuid);
            if (aPeonyBillAndPaymentsPaneController != null){
                targetPeonyBillPaymentList.remove(findPeonyBillPayment(billUuid, targetPeonyBillPaymentList));
                if (Platform.isFxApplicationThread()){
                    billListComboBox.getItems().remove(thePeonyBillPayment);
                }else{
                    Platform.runLater(new Runnable(){
                        @Override
                        public void run() {
                            billListComboBox.getItems().remove(thePeonyBillPayment);
                        }
                    });
                }
                
                peonyBillPaymentsPaneControllerStorage.remove(billUuid);
                Tab theTab = findBillsAndPaymentsTab(billUuid);
                if (theTab != null){
                    handlePeonyBillPaymentDeletedEventHelper(theTab);
                }
            }
        }
    }

    private void updateBillListComboBoxForSave(final PeonyBillPayment savedPeonyBillPayment) {
        if (Platform.isFxApplicationThread()){
            updateBillListComboBoxForSaveHelper(savedPeonyBillPayment);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    updateBillListComboBoxForSaveHelper(savedPeonyBillPayment);
                }
            });
        }
    }
    
    private void updateBillListComboBoxForSaveHelper(final PeonyBillPayment savedPeonyBillPayment) {
        if (savedPeonyBillPayment == null){
            return;
        }
        if (!billListComboBox.getItems().contains(savedPeonyBillPayment)){
            billListComboBox.getItems().add(0, savedPeonyBillPayment);
            billListComboBox.getSelectionModel().select(savedPeonyBillPayment);
            
            /**
             * Support printing invoice report
             */
            Object targetOwner = this.getTargetOwner();
            List<PeonyBillPayment> aPeonyBillPaymentList = null;
            if (targetOwner instanceof PeonyTaxcorpCase){
                aPeonyBillPaymentList = ((PeonyTaxcorpCase)targetOwner).getPeonyBillPaymentList();
            }else if (targetOwner instanceof PeonyTaxpayerCase){
                aPeonyBillPaymentList = ((PeonyTaxpayerCase)targetOwner).getPeonyBillPaymentList();
            }
            if (aPeonyBillPaymentList != null){
                aPeonyBillPaymentList.add(savedPeonyBillPayment);
            }
        }
    }
    
    private Tab findBillsAndPaymentsTab(String billUuid){
        Tab theTab = null;
        List<Tab> tabs =  new ArrayList<>(billsAndPaymentsTabPane.getTabs());
        if (tabs.size() > 0){
            for (Tab tab : tabs){
                if (billUuid.equalsIgnoreCase(tab.getId())){
                    theTab = tab;
                    break;
                }
            }//for-loop
        }
        return theTab;
    }

    private void handlePeonyBillPaymentDeletedEventHelper(final Tab theTab) {
        if (Platform.isFxApplicationThread()){
            billsAndPaymentsTabPane.getTabs().remove(theTab);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    billsAndPaymentsTabPane.getTabs().remove(theTab);
                }
            });
        }
    }
    
    private PeonyBillPayment findPeonyBillPayment(String billUuid, List<PeonyBillPayment> aPeonyBillPaymentList){
        PeonyBillPayment deletedPeonyBillPayment = null;
        for (PeonyBillPayment aPeonyBillPayment : aPeonyBillPaymentList){
            if (aPeonyBillPayment.getBill().getBillUuid().equalsIgnoreCase(billUuid)){
                deletedPeonyBillPayment = aPeonyBillPayment;
                break;
            }
        }
        return deletedPeonyBillPayment;
    }
    
    private void initializeBillListComboBox(){
        if (!billListComboBox.getItems().isEmpty()){
            billListComboBox.getSelectionModel().select(0);
        }
        
        billListComboBox.valueProperty().addListener(new ChangeListener<PeonyBillPayment>() {
            @Override public void changed(ObservableValue observable, PeonyBillPayment oldValue, PeonyBillPayment newValue) {
                handleBillListComboBoxSelectionChanged(newValue);
            }    
        });
    }
    
    /**
     * This method demands FX-thread
     * @param newValue 
     */
    private void handleBillListComboBoxSelectionChanged(PeonyBillPayment selectedPeonyBillPayment){
        if (selectedPeonyBillPayment != null){
            String billUuid = selectedPeonyBillPayment.getBill().getBillUuid();
            Tab aTab = findBillsAndPaymentsTab(billUuid);
            if (aTab != null){
                billsAndPaymentsTabPane.getSelectionModel().select(aTab);
            }else{
                addTaxcorpBillPaymentTab(selectedPeonyBillPayment.getBillTitle(), selectedPeonyBillPayment, true);
            }
        }
    }
    
    private void initializeCreateNewBillButton(){
        //btn.setPrefWidth(150.0);
        createNewBillButton.setTooltip(new Tooltip("Create another bill for this taxcorp case"));
        createNewBillButton.setGraphic(PeonyGraphic.getImageView("add.png"));
        createNewBillButton.setOnAction((ActionEvent e) -> {
            addTaxcorpBillPaymentTab("New Bill for Customer:", new PeonyBillPayment(), false);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        billsAndPaymentsTabPane.getTabs().clear();
        //display tabs for every bill with its payments
        if ((targetPeonyBillPaymentList != null) && (!targetPeonyBillPaymentList.isEmpty())){
            for (PeonyBillPayment aPeonyBillPayment : targetPeonyBillPaymentList){
                billListComboBox.getItems().add(aPeonyBillPayment);
                if (isPeonyBillPaymentQualifiedForDisplay(aPeonyBillPayment)){
                    addTaxcorpBillPaymentTab(aPeonyBillPayment.getBillTitle(), aPeonyBillPayment, false);
                }
            }
        }
        
        initializeCreateNewBillButton();
        initializeBillListComboBox();
        
        if (billsAndPaymentsTabPane.getTabs().isEmpty()){
            if (!billListComboBox.getItems().isEmpty()){
                billListComboBox.getSelectionModel().select(0);
                handleBillListComboBoxSelectionChanged(billListComboBox.getSelectionModel().getSelectedItem());
            }
        }
    }
    
    public void polulateBillPayment(final PeonyBillPayment aPeonyBillPayment){
        if (Platform.isFxApplicationThread()){
            polulateBillPaymentHelper(aPeonyBillPayment);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    polulateBillPaymentHelper(aPeonyBillPayment);
                }
            });
        }
    }
    
    private void polulateBillPaymentHelper(final PeonyBillPayment aPeonyBillPayment){
        PeonyBillPayment thePeonyBillPayment = billListComboBox.getSelectionModel().getSelectedItem();
        billListComboBox.getItems().add(aPeonyBillPayment);
        addTaxcorpBillPaymentTab(aPeonyBillPayment.getBillTitle(), aPeonyBillPayment, true);
        if (thePeonyBillPayment != null){
            billListComboBox.getSelectionModel().select(thePeonyBillPayment);
            billsAndPaymentsTabPane.getSelectionModel().select(0);
        }
    }

    private void addTaxcorpBillPaymentTab(final String tabTitle, final PeonyBillPayment aPeonyBillPayment, final boolean closable) {
        if (Platform.isFxApplicationThread()){
            addTaxcorpBillPaymentTabHelper(tabTitle, aPeonyBillPayment, closable);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    addTaxcorpBillPaymentTabHelper(tabTitle, aPeonyBillPayment, closable);
                }
            });
        }
    }

    private void addTaxcorpBillPaymentTabHelper(String tabTitle, PeonyBillPayment aPeonyBillPayment, boolean closable) {
        /**
         * synchronize UUID here. todo zzj: is it really necessary?
         */
        String entityUuid = super.getTargetEntityUuid();
        aPeonyBillPayment.getBill().setEntityUuid(entityUuid);
        if (ZcaValidator.isNullEmpty(aPeonyBillPayment.getBill().getBillUuid())){
            //in this case, it is a brand new bill
            aPeonyBillPayment.getBill().setBillUuid(ZcaUtils.generateUUIDString());
        }
        String billUuid = aPeonyBillPayment.getBill().getBillUuid();
        List<PeonyPayment>aPeonyPaymentList = aPeonyBillPayment.getPaymentList();
        if (aPeonyPaymentList != null){
            for (PeonyPayment aPeonyPayment : aPeonyPaymentList){
                aPeonyPayment.getPayment().setBillUuid(billUuid);
            }
        }
        createTaxcorpBillPaymentTabHelper(tabTitle, billUuid, aPeonyBillPayment, closable);
    }

    private void createTaxcorpBillPaymentTabHelper(String tabTitle, String billUuid, PeonyBillPayment aPeonyBillPayment, boolean closable) {
        /**
         * Create a tab for this new bill
         */
        PeonyBillAndPaymentsPaneController aTaxcorpBillPaymentsPaneController = peonyBillPaymentsPaneControllerStorage.get(billUuid);
        if (aTaxcorpBillPaymentsPaneController == null){
            aTaxcorpBillPaymentsPaneController = new PeonyBillAndPaymentsPaneController(aPeonyBillPayment, super.getTargetOwner());
            aTaxcorpBillPaymentsPaneController.addPeonyFaceEventListener(this);
            aTaxcorpBillPaymentsPaneController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
            try {
                aTaxcorpBillPaymentsPaneController.loadFxml();
                peonyBillPaymentsPaneControllerStorage.put(billUuid, aTaxcorpBillPaymentsPaneController);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
                return;
            }
        }
        Tab tab = new Tab(tabTitle);
        tab.setId(billUuid);
        tab.setClosable(closable);
        tab.setContent(aTaxcorpBillPaymentsPaneController.getRootPane());
        billsAndPaymentsTabPane.getTabs().add(tab);
        billsAndPaymentsTabPane.getSelectionModel().select(tab);
    }

    private boolean isPeonyBillPaymentQualifiedForDisplay(PeonyBillPayment aPeonyBillPayment) {
        //The unpaid is qualified for display
        if (aPeonyBillPayment.getBillBalance() > 0){
            return true;
        }
        //The followings are the unpaid cases...
        //Paid and it was processed before 01/01/2020 is NOT qualified for display
        GregorianCalendar dueDay = new GregorianCalendar();
        dueDay.setTime(aPeonyBillPayment.getBillDueDate());
        GregorianCalendar keystoneDay = new GregorianCalendar();
        keystoneDay.set(Calendar.YEAR, 2019);
        keystoneDay.set(Calendar.MONTH, Calendar.DECEMBER);
        keystoneDay.set(Calendar.DAY_OF_MONTH, 31);
        if (dueDay.before(keystoneDay)){
            return false;
        }
        //if not confirmed, display it
        List<PeonyPayment> aPeonyPaymentList = aPeonyBillPayment.getPaymentList();
        boolean confirmed = true;
        for (PeonyPayment aPeonyPayment : aPeonyPaymentList){
            if (!GardenBooleanValue.Yes.name().equalsIgnoreCase(aPeonyPayment.getPayment().getDepositConfirmed())){
                confirmed = false;
                break;
            }
        }
        return !confirmed;
    }
}
