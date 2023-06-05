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

package com.zcomapproach.garden.peony.search.controllers;

import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import com.zcomapproach.garden.peony.search.events.TaxpayerCaseStatusUpdated;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.data.TaxpayerCaseStatusReportColumns;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G02Log;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerCaseStatusCache;
import com.zcomapproach.garden.persistence.peony.data.PeonyLogName;
import com.zcomapproach.garden.persistence.peony.data.TaxpayerCaseStatusCacheResult;
import com.zcomapproach.garden.rest.GardenRestParams;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class TaxpayerCaseStatusEditorController extends PeonySearchServiceController{

    @FXML
    private Label statusTitleLabel;
    @FXML
    private Label workStatusLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Button saveButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button closeButton;
    
    private final TaxpayerCaseStatusCacheResult targetTaxpayerCaseStatusCacheResult;
    private final TaxpayerCaseStatusReportColumns targetTaxpayerCaseStatusColumnType;

    public TaxpayerCaseStatusEditorController(TaxpayerCaseStatusCacheResult targetTaxpayerCaseStatusCacheResult, TaxpayerCaseStatusReportColumns targetTaxpayerCaseStatusColumnType) {
        this.targetTaxpayerCaseStatusCacheResult = targetTaxpayerCaseStatusCacheResult;
        this.targetTaxpayerCaseStatusColumnType = targetTaxpayerCaseStatusColumnType;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusTitleLabel.setText("Taxpayer Case: " + targetTaxpayerCaseStatusCacheResult.getTaxpayerName() + " (" + targetTaxpayerCaseStatusCacheResult.getPrimaryTaxpayerSsn() + ")");
        workStatusLabel.setText(TaxpayerCaseStatusReportColumns.getCorrespondingStatus(targetTaxpayerCaseStatusColumnType).value() 
                + " (" + PeonyProperties.getSingleton().getCurrentLoginEmployee().getPeonyUserFullName() + ")");
        descriptionLabel.setText(TaxpayerCaseStatusReportColumns.getParamNote(targetTaxpayerCaseStatusColumnType));
        
        saveButton.setOnAction((ActionEvent event) -> {
            updateTaxpayerCaseStatus(false);
        });
        
        deleteButton.setOnAction((ActionEvent event) -> {
            updateTaxpayerCaseStatus(true);
        });
        
        closeButton.setOnAction((ActionEvent event) -> {
            broadcastPeonyFaceEventHappened(new CloseDialogRequest());
        });
    }
    
    private void disableButtons(){
        if (Platform.isFxApplicationThread()){
            disableButtonsHelper();
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    disableButtonsHelper();
                }
            });
        }
    }
    private void disableButtonsHelper(){
        saveButton.setDisable(true);
        deleteButton.setDisable(true);
    }
    
    private void enableButtons(){
        if (Platform.isFxApplicationThread()){
            enableButtonsHelper();
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    enableButtonsHelper();
                }
            });
        }
    }
    private void enableButtonsHelper(){
        saveButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    private void updateTaxpayerCaseStatus(final boolean forDeletion) {
        
        this.getCachedThreadPoolExecutorService().submit(new Task<TaxpayerCaseStatusCacheResult>(){
            @Override
            protected TaxpayerCaseStatusCacheResult call() throws Exception {
                
                disableButtons();
                
                G02TaxpayerCaseStatusCache aG02TaxpayerCaseStatusCache = targetTaxpayerCaseStatusCacheResult.getTaxpayerCaseStatusCache();
                G02Log statusLog = new G02Log();
                statusLog.setEntityDescription("Taxpayer Case: " + aG02TaxpayerCaseStatusCache.getTaxpayerName() + " - SSN " + aG02TaxpayerCaseStatusCache.getPrimaryTaxpayerSsn());
                statusLog.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
                statusLog.setEntityUuid(aG02TaxpayerCaseStatusCache.getTaxpayerCaseUuid());
                statusLog.setLogMessage(workStatusLabel.getText());
                statusLog.setLogName(PeonyLogName.UPDATED_TAXPAYER_CASE_STATUS.name());
                statusLog.setLogUuid(ZcaUtils.generateUUIDString());
                statusLog.setLoggedEntityType(GardenEntityType.TAXPAYER_CASE.name());
                statusLog.setLoggedEntityUuid(aG02TaxpayerCaseStatusCache.getTaxpayerCaseUuid());
                statusLog.setOperatorAccountUuid(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
                statusLog.setCreated(new Date());
                targetTaxpayerCaseStatusCacheResult.setStatusLog(statusLog);
                
                String status;
                if (forDeletion){
                    status = null;
                }else{
                    status = PeonyProperties.getSingleton().getCurrentLoginEmployee().getPeonyUserFullName() + ": " + ZcaCalendar.convertToMMddyyyyHHmmss(statusLog.getCreated());
                }
                switch (targetTaxpayerCaseStatusColumnType){
                    case RECEIVE:
                        targetTaxpayerCaseStatusCacheResult.getTaxpayerCaseStatusCache().setReceiveTaxpayerCaseStatus(status);
                        break;
                    case SCAN:
                        targetTaxpayerCaseStatusCacheResult.getTaxpayerCaseStatusCache().setTaxpayerCaseScanStatus(status);
                        break;
                    case PREPARE:
                        targetTaxpayerCaseStatusCacheResult.getTaxpayerCaseStatusCache().setPrepareTaxMaterialStatus(status);
                        break;
                    case REVIEW:
                        targetTaxpayerCaseStatusCacheResult.getTaxpayerCaseStatusCache().setManangerReviewStatus(status);
                        break;
                    case CPA_REVIEW:
                        targetTaxpayerCaseStatusCacheResult.getTaxpayerCaseStatusCache().setCpaApprovedStatus(status);
                        break;
                    case NOTIFY:
                        targetTaxpayerCaseStatusCacheResult.getTaxpayerCaseStatusCache().setNotifySignatureAndPaymentStatus(status);
                        break;
                    case PICKUP:
                        targetTaxpayerCaseStatusCacheResult.getTaxpayerCaseStatusCache().setPickupByCustomerStatus(status);
                        break;
                    case SIGN:
                        targetTaxpayerCaseStatusCacheResult.getTaxpayerCaseStatusCache().setCustomerSignatureStatus(status);
                        break;
                    case EFILE:
                        targetTaxpayerCaseStatusCacheResult.getTaxpayerCaseStatusCache().setEfileStatus(status);
                        break;
                }
                
                try {
                    if (forDeletion){
                        return Lookup.getDefault().lookup(PeonyTaxpayerService.class)
                                .getPeonyTaxpayerRestClient().storeEntity_XML(TaxpayerCaseStatusCacheResult.class,
                                        GardenRestParams.Taxpayer.removeStatusLogFromTaxpayerCaseStatusCacheResultRestParams(), targetTaxpayerCaseStatusCacheResult);
                    }else{
                        return Lookup.getDefault().lookup(PeonyTaxpayerService.class)
                                .getPeonyTaxpayerRestClient().storeEntity_XML(TaxpayerCaseStatusCacheResult.class,
                                        GardenRestParams.Taxpayer.storeTaxpayerCaseStatusCacheResultWithStatusLogRestParams(), targetTaxpayerCaseStatusCacheResult);
                    }
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
                
                return null;
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayErrorMessageDialog("Cannot update this status because of technical failure.");
            }

            @Override
            protected void succeeded() {
                try {
                    broadcastPeonyFaceEventHappened(new TaxpayerCaseStatusUpdated(get()));
                    broadcastPeonyFaceEventHappened(new CloseDialogRequest());
                    enableButtons();
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Failed to update this status because of technical failure on the server-side");
                }
            }
        });
    }

}
