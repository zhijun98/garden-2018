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

import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.garden.exception.GardenRuntimeException;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.persistence.entity.G02PersonalBusinessProperty;
import com.zcomapproach.garden.persistence.entity.G02PersonalProperty;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerCase;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerInfo;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.commons.ZcaCalendar;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.openide.util.Lookup;

/**
 * @deprecated - replaced by CloneHistoricalTaxpayerCaseController
 * @author zhijun98
 */
public class CloneLegancyTaxpayerCaseController extends PeonyTaxpayerServiceController{
    @FXML
    private ComboBox legancyTaxReturnDeadlineComboBox;
    @FXML
    private Button launchButton;
    @FXML
    private Button cancelButton;
    
    private final Date expectedDeadline;
    private final List<PeonyTaxpayerCase> legancyTaxReturnList;

    public CloneLegancyTaxpayerCaseController(Date expectedDeadline, List<PeonyTaxpayerCase> legancyTaxReturnList) {
        super(null);
        this.expectedDeadline = expectedDeadline;
        this.legancyTaxReturnList = legancyTaxReturnList;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> deadlineList = new ArrayList<>();
        String deadline;
        for (PeonyTaxpayerCase aPeonyTaxpayerCase : legancyTaxReturnList){
            deadline = ZcaCalendar.convertToMMddyyyy(aPeonyTaxpayerCase.getTaxpayerCase().getDeadline(), "-");
            deadlineList.remove(deadline);
            deadlineList.add(deadline);
        }
        PeonyFaceUtils.initializeComboBox(legancyTaxReturnDeadlineComboBox, deadlineList, null, null, "Legancy tax returns' deadline for clone", this);
        
        cancelButton.setOnAction((ActionEvent actionEvent) -> {
            broadcastPeonyFaceEventHappened(new CloseDialogRequest());
        });
        
        launchButton.setOnAction((ActionEvent actionEvent) -> {
            if (legancyTaxReturnDeadlineComboBox.getSelectionModel().getSelectedItem() == null){
                PeonyFaceUtils.displayErrorMessageDialog("Please select a legancy tax return for case clone!");
            }else{
                Lookup.getDefault().lookup(PeonyTaxpayerService.class).launchPeonyTaxpayerCaseTopComponent(findPeonyTaxpayerCaseForClone());
            }
        });
    }

    private PeonyTaxpayerCase findPeonyTaxpayerCaseForClone() {
        String selectedDeadline = legancyTaxReturnDeadlineComboBox.getSelectionModel().getSelectedItem().toString();
        for (PeonyTaxpayerCase aPeonyTaxpayerCase : legancyTaxReturnList){
            if (selectedDeadline.equalsIgnoreCase(ZcaCalendar.convertToMMddyyyy(aPeonyTaxpayerCase.getTaxpayerCase().getDeadline(), "-"))){
                return PeonyTaxpayerCase.createPeonyTaxpayerCaseByClone(aPeonyTaxpayerCase, expectedDeadline);    //clone this taxpayer case
            }
        }
        throw new GardenRuntimeException("TECH: this is an impossible case for this logics");
    }
}
