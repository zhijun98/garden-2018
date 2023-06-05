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
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.zcomapproach.garden.data.constant.UState;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import com.zcomapproach.garden.peony.taxpayer.dialogs.CloneLegancyTaxpayerCaseDialog;
import com.zcomapproach.garden.peony.taxpayer.events.RequestToDisplayPeonyTaxpayerCaseList;
import com.zcomapproach.garden.peony.utils.PeonyDataUtils;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCaseList;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.garden.taxation.TaxationSettings;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.garden.peony.view.events.RequestBusyMouseCursor;
import com.zcomapproach.garden.peony.view.events.RequestDefaultMouseCursor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class PeonyTaxpayerLaunchController extends PeonyTaxpayerCaseController {
    
    @FXML
    private JFXTextField taxpayerSsnTextField;
    
    @FXML
    private JFXComboBox<String> taxReturnYearComboBox;
    
    @FXML
    private JFXButton launchButton;
    
    @FXML
    private Label launchNoteLabel;

    public PeonyTaxpayerLaunchController() {
        super(null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        taxpayerSsnTextField.setTooltip(new Tooltip("Anyone's SSN, e.g. 012-34-567, involved in the taxpayer case"));
        taxpayerSsnTextField.setOnKeyPressed((KeyEvent event) -> {
            if(event.getCode() == KeyCode.ENTER) {
                launchTaxpayerCase();
            }
        });
        int year = ZcaCalendar.parseCalendarField(new Date(), Calendar.YEAR);
        for (int i = year; i > 2015; i--){
            taxReturnYearComboBox.getItems().add(String.valueOf(i));
        }
        taxReturnYearComboBox.setValue(null);
        launchButton.setOnAction((ActionEvent actionEvent) -> {
            launchTaxpayerCase();
        });
        launchNoteLabel.setText("Note: click to launch a window to edit existing taxpayer case(s) according to your input.");
    }

    
    private void launchTaxpayerCase(){
        launchButton.setDisable(true);
        //Validation...
        if (!PeonyDataUtils.isSsnFormat(taxpayerSsnTextField.getText())){
            PeonyFaceUtils.displayErrorMessageDialog("Please strictly follow the SSN format, e.g. 012-34-5678");
            launchButton.setDisable(false);
            return;
        }
        if (taxReturnYearComboBox.getValue() == null){
            PeonyFaceUtils.displayErrorMessageDialog("Please select the tax deadline for the launching taxpayer case");
            launchButton.setDisable(false);
            return;
        }
        
        //Launch Taxpayer Case...
        Task<PeonyTaxpayerCaseList> launchTaxpayerCaseTask = new Task<PeonyTaxpayerCaseList>(){
            @Override
            protected PeonyTaxpayerCaseList call() throws Exception {
                try{
                    broadcastPeonyFaceEventHappened(new RequestBusyMouseCursor(3));
                    return Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient()
                            .findEntity_XML(PeonyTaxpayerCaseList.class, 
                                    GardenRestParams.Taxpayer.findBasicPeonyTaxpayerCaseListByPrimaryTaxpayerSsnRestParams(
                                            taxpayerSsnTextField.getText()));
                }catch (Exception ex){
                    return null;
                }
            }

            @Override
            protected void succeeded() {
                PeonyTaxpayerCaseList launchingList = new PeonyTaxpayerCaseList();
                try {
                    broadcastPeonyFaceEventHappened(new RequestDefaultMouseCursor());
                    /**
                     * Notice: this result could be changed by other parts, e.g. clone data will change the entity's UUID 
                     */
                    PeonyTaxpayerCaseList existingPeonyTaxpayerCaseList = get();
                    if ((existingPeonyTaxpayerCaseList == null) || (existingPeonyTaxpayerCaseList.isEmpty())){
                        /**
                         * In this case, there is no any legancy case for this customer, initialize  
                         * an empty data entry form for this customer.
                         */
                        PeonyTaxpayerCase aPeonyTaxpayerCase = PeonyTaxpayerCase.createNewPeonyTaxpayerCase(taxpayerSsnTextField.getText(), getTaxReturnDeadline());
                        aPeonyTaxpayerCase.setNewEntity(true);
                        launchingList.getPeonyTaxpayerCaseList().add(aPeonyTaxpayerCase);
                    }else{
                        List<PeonyTaxpayerCase> targetPeonyTaxpayerCaseList = findTargetPeonyTaxpayerCaseListForExpectedDeadlineYear(existingPeonyTaxpayerCaseList);
                        if (targetPeonyTaxpayerCaseList.isEmpty()){
                            /**
                             * display a list of taxpayer cases to ask users to select which existing legancy one to be launched.
                             */
                            CloneLegancyTaxpayerCaseDialog aCloneLegancyTaxpayerCaseDialog = new CloneLegancyTaxpayerCaseDialog(null, true);
                            aCloneLegancyTaxpayerCaseDialog.launchCloneLegancyTaxpayerCaseDialog("Clone Legancy Taxpayer Case", taxpayerSsnTextField.getText(),
                                    getTaxReturnDeadline(), existingPeonyTaxpayerCaseList.getPeonyTaxpayerCaseList());
                        }else{
                            /**
                             * In this case, peony finds the existing one(s) based on the user's input
                             */
                            if (targetPeonyTaxpayerCaseList.size() == 1){
                                /**
                                 * display this existing target taxpayer cases for users 
                                 */
                                launchingList.getPeonyTaxpayerCaseList().add(targetPeonyTaxpayerCaseList.get(0));
                            }else{
                                /**
                                 * display multiple windows for a list of taxpayer cases with the required deadline for this customers
                                 */
                                Date taxDeadline = getTaxReturnDeadline();
                                PeonyFaceUtils.displayWarningMessageDialog("Warnings: there are "
                                        + targetPeonyTaxpayerCaseList.size() + " taxpayer cases with deadline " 
                                        + ZcaCalendar.convertToMMddyyyy(taxDeadline, "-") 
                                        + " to be displayed for customer with SSN " + taxpayerSsnTextField.getText());
                                for (PeonyTaxpayerCase targetPeonyTaxpayerCase : targetPeonyTaxpayerCaseList){
                                    launchingList.getPeonyTaxpayerCaseList().add(targetPeonyTaxpayerCase);
                                }
                            }
                        }
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("This operation failed. " + ex.getMessage());
                }
                broadcastPeonyFaceEventHappened(new RequestToDisplayPeonyTaxpayerCaseList(launchingList));
                /**
                 * Close the owner dialog eventually
                 */
                broadcastPeonyFaceEventHappened(new CloseDialogRequest());
            }

            /**
             * Find all the taxpayer cases whose deadline-year is the same as the user's looking for
             * @param result
             * @return 
             */
            private List<PeonyTaxpayerCase> findTargetPeonyTaxpayerCaseListForExpectedDeadlineYear(PeonyTaxpayerCaseList result) {
                List<PeonyTaxpayerCase> thePeonyTaxpayerCaseList = new ArrayList<>();
                Date taxDeadline = getTaxReturnDeadline();
                int year = ZcaCalendar.parseCalendarField(taxDeadline, Calendar.YEAR);
                List<PeonyTaxpayerCase> aPeonyTaxpayerCaseList = result.getPeonyTaxpayerCaseList();
                for (PeonyTaxpayerCase aPeonyTaxpayerCase : aPeonyTaxpayerCaseList){
                    if ((year > 0) && (year == ZcaCalendar.parseCalendarField(aPeonyTaxpayerCase.getTaxpayerCase().getDeadline(), Calendar.YEAR))){
                        thePeonyTaxpayerCaseList.add(aPeonyTaxpayerCase);
                    }
                }//for-loop
                return thePeonyTaxpayerCaseList;
            }
        };
        getCachedThreadPoolExecutorService().submit(launchTaxpayerCaseTask);
    }
    
    private Date getTaxReturnDeadline(){
        if (taxReturnYearComboBox.getValue() == null){
            return TaxationSettings.getSingleton().getComingPersonalFamlityTaxReturnDeadline(UState.NY).getTime();
        }else{
            return TaxationSettings.getSingleton().getComingPersonalFamlityTaxReturnDeadline(
                    Integer.parseInt(taxReturnYearComboBox.getValue()) + 1, UState.NY).getTime();
        }
    }
}
