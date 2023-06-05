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

import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.controllers.PublicBoardController;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.peony.PeonyTaxcorpCase;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import org.openide.util.Exceptions;

/**
 * Display taxcorp basic information, contact list, and its memo.
 * @author zhijun98
 */
public class TaxcorpProfileTabController extends PeonyTaxcorpServiceController implements PeonyFaceEventListener {
    
    @FXML
    private AnchorPane taxcorpProfileAnchorPane;
    
    @FXML
    private AnchorPane taxcorpContactorListAnchorPane;
    
    @FXML
    private AnchorPane taxcorpMemoAnchorPane;
    
    private final PeonyTaxcorpCase targetPeonyTaxcorpCase;

    public TaxcorpProfileTabController(PeonyTaxcorpCase targetPeonyTaxcorpCase) {
        super(targetPeonyTaxcorpCase);
        this.targetPeonyTaxcorpCase = targetPeonyTaxcorpCase;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTaxcorpProfileAnchorPane();
        initializeTaxcorpContactorListAnchorPane();
        initializeTaxcorpMemoAnchorPane();
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        
    }
    
    private void initializeTaxcorpProfileAnchorPane() {
        TaxcorpProfileController aTaxcorpProfileController = new TaxcorpProfileController(targetPeonyTaxcorpCase);
        aTaxcorpProfileController.addPeonyFaceEventListenerList(this.getPeonyFaceEventListenerList());
        try {
            aTaxcorpProfileController.loadFxml();
            PeonyFaceUtils.populateOntoAnchorPane(aTaxcorpProfileController, taxcorpProfileAnchorPane);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void initializeTaxcorpContactorListAnchorPane() {
        //taxcorpContactorListAnchorPane
        TaxcorpContactorListController aTaxcorpContactorListController = new TaxcorpContactorListController(targetPeonyTaxcorpCase);
        aTaxcorpContactorListController.addPeonyFaceEventListenerList(this.getPeonyFaceEventListenerList());
        try {
            aTaxcorpContactorListController.loadFxml();
            PeonyFaceUtils.populateOntoAnchorPane(aTaxcorpContactorListController, taxcorpContactorListAnchorPane);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void initializeTaxcorpMemoAnchorPane() {
        //create public board for taxcorp's memo
        Date today = new Date();
        initializePublicBoardController("Taxcorp Memo", targetPeonyTaxcorpCase.getPeonyMemoList(), today, today, targetPeonyTaxcorpCase);
        PublicBoardController aPublicBoardController = getPublicBoardController();
        if (aPublicBoardController != null){
            aPublicBoardController.decoratePublcBoardTitle("peony-title-label");
            PeonyFaceUtils.populateOntoAnchorPane(aPublicBoardController, taxcorpMemoAnchorPane);
        }
    }

}
