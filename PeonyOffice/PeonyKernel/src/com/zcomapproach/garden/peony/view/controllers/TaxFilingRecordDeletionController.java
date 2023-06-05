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

import com.zcomapproach.garden.peony.kernel.services.PeonyBusinessService;
import com.zcomapproach.garden.peony.security.PeonyPrivilege;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.peony.view.events.PeonyTaxFilingCaseListDeleted;
import com.zcomapproach.garden.persistence.constant.TaxFilingPeriod;
import com.zcomapproach.garden.persistence.constant.TaxFilingType;
import com.zcomapproach.garden.persistence.peony.PeonyTaxFilingCaseList;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.garden.peony.PeonyLauncher;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javax.swing.JOptionPane;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class TaxFilingRecordDeletionController extends PeonyFaceController{
    @FXML
    private VBox periodSelectionVBox;
    @FXML
    private GridPane topGridPane;
    @FXML
    private CheckBox forceDeletion;
    @FXML
    private ComboBox<String> taxFilingTypeComboBox;
    @FXML
    private ComboBox<String> taxFilingPeriodComboBox;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private Button deleteButton;
    @FXML
    private Button closeButton;

    private final String title;
    private final String targetOwnerEntityUuid;

    public TaxFilingRecordDeletionController(String title, String targetOwnerEntityUuid) {
        this.title = title;
        this.targetOwnerEntityUuid = targetOwnerEntityUuid;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        PeonyFaceUtils.initializeComboBox(taxFilingTypeComboBox, TaxFilingType.getEnumValueList(false), null, null, "Select the type of tax filing records", false, this);
        PeonyFaceUtils.initializeComboBox(taxFilingPeriodComboBox, TaxFilingPeriod.getEnumValueList(false), null, null, "Select the period type for tax filing records", false, this);
        
        deleteButton.setOnAction((ActionEvent event) -> {
            if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "If delete, all the related system-logs will be erased also, are you sure to complete this operation?") == JOptionPane.YES_OPTION){
                deleteTaxFilingRecords();
            }
        });
        
        closeButton.setOnAction((ActionEvent event) -> {
            broadcastPeonyFaceEventHappened(new CloseDialogRequest());
        });
        
        forceDeletion.setDisable(!PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.TAX_FILING_ERASE_ALL));
        forceDeletion.setTooltip(new Tooltip(PeonyPrivilege.getParamDescription(PeonyPrivilege.TAX_FILING_ERASE_ALL)));
    }

    private void deleteTaxFilingRecords() {
        Task<PeonyTaxFilingCaseList> deleteTaxFilingRecordsTask = new Task<PeonyTaxFilingCaseList>(){
            @Override
            protected PeonyTaxFilingCaseList call() throws Exception {
                Date fromDate = ZcaCalendar.convertToDate(fromDatePicker.getValue());
                Date toDate = ZcaCalendar.convertToDate(toDatePicker.getValue());
                if ((fromDate == null) || (toDate == null)){
                    PeonyFaceUtils.displayErrorMessageDialog("Please select date-from and date-to.");
                    return new PeonyTaxFilingCaseList();
                }
                TaxFilingType aTaxFilingType = TaxFilingType.convertEnumValueToType(taxFilingTypeComboBox.getValue());
                if ((aTaxFilingType == null) || (TaxFilingType.UNKNOWN.equals(aTaxFilingType))){
                    PeonyFaceUtils.displayErrorMessageDialog("Please select which type of tax filing records for deletion.");
                    return new PeonyTaxFilingCaseList();
                }

                TaxFilingPeriod aTaxFilingPeriod = TaxFilingPeriod.convertEnumValueToType(taxFilingPeriodComboBox.getValue());
                if ((aTaxFilingPeriod == null) || (TaxFilingPeriod.UNKNOWN.equals(aTaxFilingPeriod))){
                    PeonyFaceUtils.displayErrorMessageDialog("Please select which period type of tax filing records for deletion.");
                    return new PeonyTaxFilingCaseList();
                }

                try{
                    return Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                                                .deleteEntity_XML(PeonyTaxFilingCaseList.class, GardenRestParams.Business.deleteTaxFilingCasesRestParams(
                                                        targetOwnerEntityUuid, aTaxFilingType.value(),
                                                        aTaxFilingPeriod.value(), Long.toString(fromDate.getTime()), Long.toString(toDate.getTime()), forceDeletion.isSelected()));
                }catch (Exception ex){
                    PeonyFaceUtils.publishMessageOntoOutputWindow("Tax file record deletion got technical errors. " + ex.getMessage());
                    return new PeonyTaxFilingCaseList();
                }
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyTaxFilingCaseList aPeonyTaxFilingCaseList = get();
                    if ((aPeonyTaxFilingCaseList != null) || (!aPeonyTaxFilingCaseList.isEmpty())){
                        PeonyFaceUtils.displayInformationMessageDialog("The selected tax filing records are deleted.");
                    }
                    broadcastPeonyFaceEventHappened(new PeonyTaxFilingCaseListDeleted(aPeonyTaxFilingCaseList));
                } catch (InterruptedException | ExecutionException ex) {
                    PeonyFaceUtils.displayErrorMessageDialog("Technical error. " + ex.getMessage());
                }
            }
        };
        this.getCachedThreadPoolExecutorService().submit(deleteTaxFilingRecordsTask);
        broadcastPeonyFaceEventHappened(new CloseDialogRequest());
    }

}
