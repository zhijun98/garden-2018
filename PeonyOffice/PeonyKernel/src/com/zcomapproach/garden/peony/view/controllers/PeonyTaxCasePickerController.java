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
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxcorpService;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.peony.view.events.PeonyTaxcorpCaseSelected;
import com.zcomapproach.garden.peony.view.events.PeonyTaxpayerCaseListSelected;
import com.zcomapproach.garden.persistence.peony.PeonyTaxcorpCase;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCaseList;
import com.zcomapproach.garden.rest.GardenRestParams;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class PeonyTaxCasePickerController extends PeonyFaceController{
    @FXML
    private TextField taxpayerSsnTextField;
    
    @FXML
    private TextField taxcorpEinTextfield;
    
    @FXML
    private Button getTaxCaseButton;
    
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        getTaxCaseButton.setOnAction((ActionEvent event) -> {
            boolean selected = false;
            if (ZcaValidator.isNotNullEmpty(taxpayerSsnTextField.getText())){
                try {
                    PeonyTaxpayerCaseList peonyTaxpayerCaseList = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient() 
                            .findEntity_XML(PeonyTaxpayerCaseList.class,
                                    GardenRestParams.Taxpayer.findPeonyTaxpayerCaseListByTaxpayerSsnRestParams(taxpayerSsnTextField.getText()));
                    if (peonyTaxpayerCaseList != null){
                        broadcastPeonyFaceEventHappened(new PeonyTaxpayerCaseListSelected(peonyTaxpayerCaseList.getPeonyTaxpayerCaseList()));
                    }
                    selected = true;
                } catch (Exception ex) {
                    PeonyFaceUtils.publishMessageOntoOutputWindow("Cannot find PeonyTaxpayerCaseList due to technical reasons. " + ex.getMessage());
                }
            }
            if (ZcaValidator.isNotNullEmpty(taxcorpEinTextfield.getText())){
                try {
                    PeonyTaxcorpCase peonyTaxcorpCase = Lookup.getDefault().lookup(PeonyTaxcorpService.class).getPeonyTaxcorpRestClient() 
                            .findEntity_XML(PeonyTaxcorpCase.class,
                                    GardenRestParams.Taxcorp.findPeonyTaxcorpCaseByEinNumberRestParams(taxcorpEinTextfield.getText()));
                    if (peonyTaxcorpCase != null){
                        broadcastPeonyFaceEventHappened(new PeonyTaxcorpCaseSelected(peonyTaxcorpCase));
                    }
                    selected = true;
                } catch (Exception ex) {
                    PeonyFaceUtils.publishMessageOntoOutputWindow("Cannot find PeonyTaxcorpCase due to technical reasons. " + ex.getMessage());
                }
            }
            if (selected){
                broadcastPeonyFaceEventHappened(new CloseDialogRequest(true));
            }else{
                PeonyFaceUtils.displayErrorMessageDialog("Cannot find corresponding tax cases based on your input SSN and/or EIN. Please try it again!");
            }
        });
        
        cancelButton.setOnAction((ActionEvent event) -> {
            broadcastPeonyFaceEventHappened(new CloseDialogRequest(true));
        });
    }

}
