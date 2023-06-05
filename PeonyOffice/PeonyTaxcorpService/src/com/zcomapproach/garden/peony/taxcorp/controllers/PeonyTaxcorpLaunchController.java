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
import com.jfoenix.controls.JFXTextField;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxcorpService;
import com.zcomapproach.garden.peony.taxcorp.events.RequestToDisplayPeonyTaxcorpCaseList;
import com.zcomapproach.garden.peony.utils.PeonyDataUtils;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.RequestBusyMouseCursor;
import com.zcomapproach.garden.peony.view.events.RequestDefaultMouseCursor;
import com.zcomapproach.garden.persistence.peony.PeonyTaxcorpCase;
import com.zcomapproach.garden.persistence.peony.PeonyTaxcorpCaseList;
import com.zcomapproach.garden.rest.GardenRestParams;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class PeonyTaxcorpLaunchController extends PeonyTaxcorpCaseController{
    
    @FXML
    private JFXTextField taxcorpEinTextField;
    
    @FXML
    private JFXTextField corporateNameTextField;
    
    @FXML
    private JFXButton launchButton;
    
    @FXML
    private Label launchNoteLabel;
    
    public PeonyTaxcorpLaunchController() {
        super(null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        taxcorpEinTextField.setOnKeyPressed((KeyEvent event) -> {
            if(event.getCode() == KeyCode.ENTER) {
                String ein = taxcorpEinTextField.getText();
                if (ZcaValidator.isNotNullEmpty(ein)){
                    if (!PeonyDataUtils.isEinFormat(ein)){
                        PeonyFaceUtils.displayErrorMessageDialog("Please strictly follow the EIN format, e.g. 01-2345678");
                        return;
                    }
                }
                launchTaxcorpCase();
                corporateNameTextField.setText(null);
            }
        });
        corporateNameTextField.setOnKeyPressed((KeyEvent event) -> {
            if(event.getCode() == KeyCode.ENTER) {
                if (ZcaValidator.isNotNullEmpty(corporateNameTextField.getText())){
                    launchTaxcorpCase();
                    taxcorpEinTextField.setText(null);
                }
            }
        });
        launchButton.setOnAction((ActionEvent actionEvent) -> {
            launchTaxcorpCase();
        });
        launchNoteLabel.setText("Note: click to launch a window to edit existing taxcorp case(s) according to your "
                + "input, i.e., either EIN or corporate name. You may simply click to launch a new taxcorp case without any input.");
    }

    private void launchTaxcorpCase() {
        getCachedThreadPoolExecutorService().submit(createLaunchTaxcorpCaseTask());
    }

    private Task<PeonyTaxcorpCaseList> createLaunchTaxcorpCaseTask(){
        return new Task<PeonyTaxcorpCaseList>(){
            @Override
            protected PeonyTaxcorpCaseList call() throws Exception {
                broadcastPeonyFaceEventHappened(new RequestBusyMouseCursor(3));
                PeonyTaxcorpCaseList result = null;
                String ein = taxcorpEinTextField.getText();
                String corporateName = corporateNameTextField.getText();
                if (ZcaValidator.isNotNullEmpty(ein)){
                    corporateNameTextField.setText(null);
                    PeonyTaxcorpCase aPeonyTaxcorpCase = Lookup.getDefault().lookup(PeonyTaxcorpService.class).getPeonyTaxcorpRestClient()
                            .findEntity_XML(PeonyTaxcorpCase.class, 
                                    GardenRestParams.Taxcorp.findPeonyTaxcorpCaseByEinNumberRestParams(ein));
                    if (aPeonyTaxcorpCase != null){
                        result = new PeonyTaxcorpCaseList();
                        result.getPeonyTaxcorpCaseList().add(aPeonyTaxcorpCase);
                    }
                }else if (ZcaValidator.isNotNullEmpty(corporateName)){
                    result = Lookup.getDefault().lookup(PeonyTaxcorpService.class).getPeonyTaxcorpRestClient()
                            .findEntity_XML(PeonyTaxcorpCaseList.class, 
                                    GardenRestParams.Taxcorp.findPeonyTaxcorpCaseListByCorpNameRestParams(corporateName));

                }
                return result;
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayErrorMessageDialog("Cannot launch the taxcorp case because of technical reasons. " + getMessage());
                broadcastPeonyFaceEventHappened(new RequestDefaultMouseCursor());
            }

            @Override
            protected void succeeded() {
                broadcastPeonyFaceEventHappened(new RequestDefaultMouseCursor());
                String ein = taxcorpEinTextField.getText();
                String corporateName = corporateNameTextField.getText();
                //reset
                taxcorpEinTextField.setText(null);
                corporateNameTextField.setText(null);
                try {
                    /**
                     * Notice: this result could be changed by other parts, e.g. clone data will change the entity's UUID 
                     */
                    PeonyTaxcorpCaseList result = get();
                    if ((result == null) || (result.isEmpty())){
                        /**
                         * In this case, there is no any legancy case for this customer, initialize an empty data entry form for this customer.
                         */
                        result = new PeonyTaxcorpCaseList();
                        result.getPeonyTaxcorpCaseList().add(PeonyTaxcorpCase.createNewPeonyTaxcorpCase(corporateNameTextField.getText(), taxcorpEinTextField.getText()));
                        if ((ZcaValidator.isNotNullEmpty(ein)) || (ZcaValidator.isNotNullEmpty(corporateName))){
                            PeonyFaceUtils.displayWarningMessageDialog("There are no qualified taxcorp case matching with your input. "
                                    + "But you may start adding a new taxcorp case based on the data you provided.");
                        }
                    }else{
                        List<PeonyTaxcorpCase> targetPeonyTaxcorpCaseList = result.getPeonyTaxcorpCaseList();
                        if (targetPeonyTaxcorpCaseList.size() > 3){
                            PeonyFaceUtils.displayWarningMessageDialog("There are too many similiar taxcorp cases in the storage. "
                                    + "Please use SEARCH engine to launch the taxcorp case OR try it again.");
                            result.getPeonyTaxcorpCaseList().clear();   //make it empty
                        }else{
                            if (targetPeonyTaxcorpCaseList.size() != 1){
                                PeonyFaceUtils.displayWarningMessageDialog("Warnings: there are " + targetPeonyTaxcorpCaseList.size() 
                                        + " taxcorp cases which are similiar to your input. They will be displayed in seperated windows.");
                            }
                        }
                    }
                    if (!result.isEmpty()){
                        broadcastPeonyFaceEventHappened(new RequestToDisplayPeonyTaxcorpCaseList(result));
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Cannot launch the taxcorp case. " + ex.getMessage());
                }
            }
        };
    }

}
