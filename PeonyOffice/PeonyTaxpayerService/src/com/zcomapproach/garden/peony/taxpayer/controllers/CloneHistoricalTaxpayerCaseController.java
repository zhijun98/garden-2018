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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class CloneHistoricalTaxpayerCaseController extends PeonyTaxpayerServiceController{
    @FXML
    private Label titleLabel;
    @FXML
    private ListView<String> historicalTaxpayerCaseListView;
    
    private final String expectedSsn;
    private final Date expectedDeadline;
    private final List<PeonyTaxpayerCase> legancyTaxReturnList;

    public CloneHistoricalTaxpayerCaseController(String expectedSsn, Date expectedDeadline, List<PeonyTaxpayerCase> legancyTaxReturnList) {
        super(null);
        this.expectedSsn = expectedSsn;
        this.expectedDeadline = expectedDeadline;
        this.legancyTaxReturnList = legancyTaxReturnList;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        titleLabel.setText("Primary SSN: " + expectedSsn + " - " + ZcaCalendar.convertToMMddyyyy(expectedDeadline, "-"));
        
        if (legancyTaxReturnList != null){
            Collections.sort(legancyTaxReturnList, new Comparator<PeonyTaxpayerCase>(){
                @Override
                public int compare(PeonyTaxpayerCase o1, PeonyTaxpayerCase o2) {
                    try {
                        return o1.getTaxpayerCase().getDeadline().compareTo(o2.getTaxpayerCase().getDeadline()) * (-1);
                    } catch (Exception ex) {
                        return 0;
                    }
                }
            });
        }
        
        for (PeonyTaxpayerCase aPeonyTaxpayerCase : legancyTaxReturnList){
            historicalTaxpayerCaseListView.getItems().add(aPeonyTaxpayerCase.getTaxpayerCaseTitle(false));
        }
        
        ContextMenu contextMenu = new ContextMenu();
        MenuItem cloneThisCaseMenuItem = new MenuItem("Create by cloning this case");
        cloneThisCaseMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (historicalTaxpayerCaseListView.getSelectionModel().getSelectedItem() == null){
                    PeonyFaceUtils.displayErrorMessageDialog("Please select a legancy tax return for case clone!");
                }else{
                    Lookup.getDefault().lookup(PeonyTaxpayerService.class).launchPeonyTaxpayerCaseTopComponent(
                            findPeonyTaxpayerCaseForClone(historicalTaxpayerCaseListView.getSelectionModel().getSelectedItem()));
                    broadcastPeonyFaceEventHappened(new CloseDialogRequest());
                }
            }
        });
        contextMenu.getItems().add(cloneThisCaseMenuItem);
        
        MenuItem createNewMenuItem = new MenuItem("Create by an brand new case");
        createNewMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Lookup.getDefault().lookup(PeonyTaxpayerService.class).launchPeonyTaxpayerCaseTopComponent(
                        PeonyTaxpayerCase.createNewPeonyTaxpayerCase(expectedSsn, expectedDeadline));
                broadcastPeonyFaceEventHappened(new CloseDialogRequest());
            }
        });
        contextMenu.getItems().add(createNewMenuItem);
        
        MenuItem cancelMenuItem = new MenuItem("Cancel clone");
        cancelMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                broadcastPeonyFaceEventHappened(new CloseDialogRequest());
            }
        });
        contextMenu.getItems().add(cancelMenuItem);
        historicalTaxpayerCaseListView.setContextMenu(contextMenu);
    }

    private PeonyTaxpayerCase findPeonyTaxpayerCaseForClone(String selectedPeonyTaxpayerCaseTitle) {
        for (PeonyTaxpayerCase aPeonyTaxpayerCase : legancyTaxReturnList){
            if (selectedPeonyTaxpayerCaseTitle.equalsIgnoreCase(aPeonyTaxpayerCase.getTaxpayerCaseTitle(false))){
                return preparePeonyTaxpayerCaseForClone(aPeonyTaxpayerCase);    //clone this taxpayer case
            }
        }
        throw new GardenRuntimeException("TECH: this is an impossible case for this logics");
    }

    private PeonyTaxpayerCase preparePeonyTaxpayerCaseForClone(PeonyTaxpayerCase aPeonyTaxpayerCase) {
        /**
         * todo zzj: this is not safe to clone: it should create a seperate brand-new instance of PeonyTaxpayerCase.
         */
        G02TaxpayerCase aG02TaxpayerCase = aPeonyTaxpayerCase.getTaxpayerCase();
        aPeonyTaxpayerCase.setNewEntity(true);
        aG02TaxpayerCase.setAgreementSignature(null);
        aG02TaxpayerCase.setAgreementSignatureTimestamp(null);
        aG02TaxpayerCase.setAgreementUuid(null);
        aG02TaxpayerCase.setCreated(new Date());
        aG02TaxpayerCase.setDeadline(expectedDeadline);
        aG02TaxpayerCase.setExtension(null);
        aG02TaxpayerCase.setEntityStatus(null);
        aG02TaxpayerCase.setLatestLogUuid(null);
        /**
         * change the UUID to make it be a new instance for clone
         */
        aG02TaxpayerCase.setTaxpayerCaseUuid(ZcaUtils.generateUUIDString());
        if (aPeonyTaxpayerCase.getPrimaryLocation() != null){
            aPeonyTaxpayerCase.getPrimaryLocation().setLocationUuid(ZcaUtils.generateUUIDString());
            aPeonyTaxpayerCase.getPrimaryLocation().setEntityUuid(aG02TaxpayerCase.getTaxpayerCaseUuid());
        }
        
        
        List<G02TaxpayerInfo> aG02TaxpayerInfoList = aPeonyTaxpayerCase.getTaxpayerInfoList();
        for (G02TaxpayerInfo aG02TaxpayerInfo : aG02TaxpayerInfoList){
            aG02TaxpayerInfo.setCreated(aG02TaxpayerCase.getCreated());
            aG02TaxpayerInfo.setTaxpayerCaseUuid(aG02TaxpayerCase.getTaxpayerCaseUuid());
            aG02TaxpayerInfo.setTaxpayerUserUuid(ZcaUtils.generateUUIDString());
        }
        
        List<G02PersonalProperty> aG02PersonalPropertyList = aPeonyTaxpayerCase.getPersonalPropertyList();
        for (G02PersonalProperty aG02PersonalProperty : aG02PersonalPropertyList){
            aG02PersonalProperty.setCreated(aG02TaxpayerCase.getCreated());
            aG02PersonalProperty.setTaxpayerCaseUuid(aG02TaxpayerCase.getTaxpayerCaseUuid());
            aG02PersonalProperty.setPersonalPropertyUuid(ZcaUtils.generateUUIDString());
        }
        
        List<G02PersonalBusinessProperty> aG02PersonalBusinessPropertyList = aPeonyTaxpayerCase.getPersonalBusinessPropertyList();
        for (G02PersonalBusinessProperty aG02PersonalBusinessProperty : aG02PersonalBusinessPropertyList){
            aG02PersonalBusinessProperty.setCreated(aG02TaxpayerCase.getCreated());
            aG02PersonalBusinessProperty.setTaxpayerCaseUuid(aG02TaxpayerCase.getTaxpayerCaseUuid());
            aG02PersonalBusinessProperty.setPersonalBusinessPropertyUuid(ZcaUtils.generateUUIDString());
        }
        
////        aPeonyTaxpayerCase.setDeadlineExtensionList(new ArrayList<>());
        aPeonyTaxpayerCase.setPeonyMemoList(new ArrayList<>());
        
        //todo zzj: here may involves other data fields... for example, extensions
        
        return aPeonyTaxpayerCase;
    }
}
