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

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.email.GardenEmailMessage;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.email.data.OfflineMessageStatus;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.kernel.data.PeonySmsContactor;
import com.zcomapproach.garden.peony.kernel.services.PeonyEmailService;
import com.zcomapproach.garden.peony.taxpayer.dialogs.TaxpayerCaseExportForPrintDialog;
import com.zcomapproach.garden.peony.taxpayer.events.StoreTargetPeonyTaxpayerCaseRequest;
import com.zcomapproach.garden.peony.utils.PeonyDataUtils;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.dialogs.PeonySmsDialog;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmail;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.garden.util.GardenData;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public abstract class PeonyTaxpayerCaseTabPaneController extends PeonyTaxpayerServiceController {
    
    private final PeonyTaxpayerCase targetPeonyTaxpayerCase;
    
    private final Tab ownerTab;
    
    public PeonyTaxpayerCaseTabPaneController(PeonyTaxpayerCase targetPeonyTaxpayerCase, Tab ownerTab) {
        super(targetPeonyTaxpayerCase);
        this.ownerTab = ownerTab;
        if (targetPeonyTaxpayerCase == null){
            targetPeonyTaxpayerCase = new PeonyTaxpayerCase();
        }
        if (ZcaValidator.isNullEmpty(targetPeonyTaxpayerCase.getTaxpayerCase().getTaxpayerCaseUuid())){
            /**
             * This is critical because some controls demand this information being ready in advance
             */
            targetPeonyTaxpayerCase.getTaxpayerCase().setTaxpayerCaseUuid(GardenData.generateUUIDString());
        }
        this.targetPeonyTaxpayerCase = targetPeonyTaxpayerCase;
    }
    
    /**
     * The title of the page-content displayed in this tab
     * @param pageTitle 
     */
    protected abstract void setPageTitle(String pageTitle);

    /**
     * programmatically select the tab from the TabPane  
     * @return - possibly NULL if this controller does not support a Tab
     */
    public Tab getOwnerTab() {
        return ownerTab;
    }

    protected PeonyTaxpayerCase getTargetPeonyTaxpayerCase() {
        return targetPeonyTaxpayerCase;
    }

    protected String getTargetPeonyTaxpayerCaseUuid() {
        return targetPeonyTaxpayerCase.getTaxpayerCase().getTaxpayerCaseUuid();
    }
    
    protected List<Button> constructCommonTaxpayerCaseFunctionalButtons(){
        List<Button> buttons = new ArrayList<>();
        
        Button btn = new Button("Email");
        btn.setPrefWidth(80.0);
        btn.setTooltip(new Tooltip("Email to taxpayers"));
        btn.setGraphic(PeonyGraphic.getImageView("mail_to_friend.png"));
        btn.setOnAction((ActionEvent e) -> {
            GardenEmailMessage aGardenEmailMessage = new GardenEmailMessage();
            aGardenEmailMessage.setToList(PeonyDataUtils.retrieveEmailAddressListFromTaxpayerInfoList(targetPeonyTaxpayerCase.getTaxpayerInfoList()));
            Lookup.getDefault().lookup(PeonyEmailService.class).displayComposeEmailTopComponent(aGardenEmailMessage, new PeonyOfflineEmail(), OfflineMessageStatus.SEND);
        });
        buttons.add(btn);
        
        btn = new Button("SMS");
        btn.setPrefWidth(80.0);
        btn.setTooltip(new Tooltip("Use mobile SMS to taxpayers"));
        btn.setGraphic(PeonyGraphic.getImageView("page_lightning.png"));
        btn.setOnAction((ActionEvent e) -> {
            smsTargetPeonyTaxpayerCase();
        });
        buttons.add(btn);
        
        btn = new Button("Export");
        btn.setPrefWidth(80.0);
        btn.setTooltip(new Tooltip("Export to Microsoft word file for print"));
        btn.setGraphic(PeonyGraphic.getImageView("printer.png"));
        btn.setOnAction((ActionEvent e) -> {
            TaxpayerCaseExportForPrintDialog aPreprintTaxpayerCaseDialog = new TaxpayerCaseExportForPrintDialog(null, true);
            aPreprintTaxpayerCaseDialog.launchTaxpayerCaseExportForPrintDialog("Preprint Selection:", targetPeonyTaxpayerCase);
        });
        buttons.add(btn);
        
        return buttons;
    }
    
    protected List<Button> constructAllTaxpayerCaseFunctionalButtons(){
        List<Button> buttons = new ArrayList<>();
        
        Button btn = new Button("Save");
        btn.setPrefWidth(80.0);
        btn.setTooltip(new Tooltip("Save taxpayer case"));
        btn.setGraphic(PeonyGraphic.getImageView("database_save.png"));
        btn.setOnAction((ActionEvent e) -> {
            broadcastPeonyFaceEventHappened(new StoreTargetPeonyTaxpayerCaseRequest());
        });
        buttons.add(btn);
        
        btn = new Button("Delete");
        btn.setPrefWidth(80.0);
        btn.setTooltip(new Tooltip("Delete taxpayer case"));
        btn.setGraphic(PeonyGraphic.getImageView("database_delete.png"));
        btn.setOnAction((ActionEvent e) -> {
            if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to delete the entire taxpayer case?") == JOptionPane.YES_OPTION){
////                deleteTargetPeonyTaxpayerCase();
            }
        });
        buttons.add(btn);
        
        buttons.addAll(constructCommonTaxpayerCaseFunctionalButtons());
        
        return buttons;
    }
    
    /**
     * Load tab-contained information into targetPeonyTaxpayerCase and do the validation. 
     * If anything wrong, it raises ZcaEntityValidationException and highlight the corresponding 
     * field.
     * 
     * @throws ZcaEntityValidationException 
     */
    protected abstract void loadTargetTaxpayerCaseFromTab() throws ZcaEntityValidationException;

    protected abstract void resetDataEntryStyleFromTab();

////    private void deleteTargetPeonyTaxpayerCase() {
////        this.getCachedThreadPoolExecutorService().submit(new Runnable(){
////            @Override
////            public void run() {
////                try {
////                    Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient()
////                            .deleteEntity_XML(PeonyTaxpayerCaseList.class, 
////                                    GardenRestParams.Taxpayer.deleteTaxpayerCaseRestParams(targetPeonyTaxpayerCase.getTaxpayerCase().getTaxpayerCaseUuid()));
////                    broadcastPeonyFaceEventHappened(new CloseTopComponentRequest());
////                    broadcastPeonyFaceEventHappened(new CloseDialogRequest());
////                } catch (Exception ex) {
////                    PeonyFaceUtils.displayErrorMessageDialog("Failed to delete this taxpayer case! Please try it later.");
////                    Exceptions.printStackTrace(ex);
////                }
////                
////            }
////        });
////    }

    private void smsTargetPeonyTaxpayerCase() {
        if (SwingUtilities.isEventDispatchThread()){
            smsTargetPeonyTaxpayerCaseHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    smsTargetPeonyTaxpayerCaseHelper();
                }
            });
        }
    }

    private void smsTargetPeonyTaxpayerCaseHelper() {
        List<PeonySmsContactor> aPeonySmsContactorList = PeonyDataUtils.retrievePeonySmsContactorListFromTaxpayerInfoList(targetPeonyTaxpayerCase.getTaxpayerInfoList());
        if ((aPeonySmsContactorList == null) || (aPeonySmsContactorList.isEmpty())){
            PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, "No contactor(s) qualified for SMS.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        PeonySmsDialog aPeonySmsDialog = new PeonySmsDialog(null, true);
        aPeonySmsDialog.launchPeonySmsDialog("Send SMS", aPeonySmsContactorList);
    }
}
