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
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.data.constant.GardenAgreement;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.persistence.G02EntityValidator;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G02Location;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerCase;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.garden.peony.view.controllers.PublicBoardController;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.openide.util.Exceptions;

/**
 *
 * @author zhijun98
 */
public class TaxpayerTagsMemoController extends PeonyTaxpayerServiceController {
    @FXML
    private AnchorPane serviceTagEditorAnchorPane;
    @FXML
    private AnchorPane taxpayerMemoAnchorPane;
    
    private final PeonyTaxpayerCase targetPeonyTaxpayerCase;
    
    public TaxpayerTagsMemoController(PeonyTaxpayerCase targetPeonyTaxpayerCase) {
        super(targetPeonyTaxpayerCase);
        this.targetPeonyTaxpayerCase = targetPeonyTaxpayerCase;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        initializeTaxpayerServiceTagEditor();
        initializeTaxpayerMemoAnchorPane();
        
    }
    
////    @Override
////    protected void handleSaveTaxpayerCaseButtonWithCompareComboBoxImpl(){
////        saveTaxpayerBasicProfileData();
////    }
////    
////    private void saveTaxpayerBasicProfileData() {
////        Task<Pair<PeonyTaxpayerCaseTabPaneController, ZcaEntityValidationException>> storeTargetTaxpayerCaseTask = new Task<Pair<PeonyTaxpayerCaseTabPaneController, ZcaEntityValidationException>>(){
////            @Override
////            protected Pair<PeonyTaxpayerCaseTabPaneController, ZcaEntityValidationException> call() throws Exception {
////                //(1) load and validate targetTaxpayerCase
////                loadTaxpayerBasicProfileData();
////                //(2) save targetTaxpayerCase
////                try{
////                    PeonyTaxpayerCase result = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient()
////                        .storeEntity_XML(PeonyTaxpayerCase.class, 
////                                        GardenRestParams.Taxpayer.storePeonyTaxpayerCaseRestParams(),
////                                        targetPeonyTaxpayerCase);
////                    if (result == null){
////                        return new Pair<>(null, new ZcaEntityValidationException("Failed to save this taxpayer."));
////                    }
////                    //successfully
////                    return null;
////                }catch(Exception ex){
////                    return new Pair<>(null, new ZcaEntityValidationException("Failed to save this taxpayer. " + ex.getMessage()));
////                }
////            }
////
////            @Override
////            protected void failed() {
////                PeonyFaceUtils.displayErrorMessageDialog("Technical error was raised. Cannot save this taxpayer case.");
////            }
////
////            @Override
////            protected void succeeded() {
////                try {
////                    Pair<PeonyTaxpayerCaseTabPaneController, ZcaEntityValidationException> result = get();
////                    if (result == null){
////                        resetDataEntryStyle();
////                        setDataEntryChanged(false);
////                        PeonyFaceUtils.displayInformationMessageDialog("Successfully saved this taxpayer case.");
////
////                        G02Log log = createNewG02LogInstance(PeonyLogName.STORED_TAXPAYER_CASE);
////                        log.setLoggedEntityType(GardenEntityType.TAXPAYER_CASE.name());
////                        log.setLoggedEntityUuid(targetPeonyTaxpayerCase.getTaxpayerCase().getTaxpayerCaseUuid());
////                        PeonyProperties.getSingleton().log(log);
////                        
////                        populateLegacyPeonyTaxpayerCaseComparison(compareComboBox.getValue());
////                    }else{
////                        ZcaEntityValidationException aEntityValidationException = result.getValue();
////                        //display error message
////                        PeonyFaceUtils.displayErrorMessageDialog(aEntityValidationException.getMessage());
////                    }
////                } catch (InterruptedException | ExecutionException ex) {
////                    //Exceptions.printStackTrace(ex);
////                }
////            }
////        };
////        getCachedThreadPoolExecutorService().submit(storeTargetTaxpayerCaseTask);
////    }

    private void loadTaxpayerBasicProfileData() throws ZcaEntityValidationException {
        G02TaxpayerCase aG02TaxpayerCase = targetPeonyTaxpayerCase.getTaxpayerCase();
        if (ZcaValidator.isNullEmpty(aG02TaxpayerCase.getTaxpayerCaseUuid())){
            aG02TaxpayerCase.setTaxpayerCaseUuid(ZcaUtils.generateUUIDString());
        }
        aG02TaxpayerCase.setAgreementUuid(GardenAgreement.TaxpayerCaseAgreement.value());
        
        G02Location aG02Location = targetPeonyTaxpayerCase.getPrimaryLocation();
        if (ZcaValidator.isNullEmpty(aG02Location.getLocationUuid())){
            aG02Location.setLocationUuid(ZcaUtils.generateUUIDString());
        }
        aG02Location.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
        try{
            G02EntityValidator.getSingleton().validate(aG02TaxpayerCase);
            G02EntityValidator.getSingleton().validate(aG02Location);
        }catch(ZcaEntityValidationException ex){
            highlightBadEntityField(ex);
            throw ex;
        }
    
    }
    
    private void initializeTaxpayerMemoAnchorPane(){
        //create public board for taxcorp's memo
        Date today = new Date();
        initializePublicBoardController("Taxpayer Memo", targetPeonyTaxpayerCase.getPeonyMemoList(), today, today, targetPeonyTaxpayerCase);
        PublicBoardController aPublicBoardController = getPublicBoardController();
        if (aPublicBoardController != null){
            aPublicBoardController.decoratePublcBoardTitle("peony-regular-title-label");
            PeonyFaceUtils.populateOntoAnchorPane(aPublicBoardController, taxpayerMemoAnchorPane);
        }
    
    }

    private void initializeTaxpayerServiceTagEditor() {
        TaxpayerServiceTagPanelController taxpayerServiceTagPanelController = new TaxpayerServiceTagPanelController(targetPeonyTaxpayerCase);
        taxpayerServiceTagPanelController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
        try {
            Pane aPane = taxpayerServiceTagPanelController.loadFxml();
            PeonyFaceUtils.populateOntoAnchorPane(taxpayerServiceTagPanelController, serviceTagEditorAnchorPane);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
