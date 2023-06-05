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

package com.zcomapproach.garden.peony.business.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import org.openide.util.Exceptions;

/**
 *
 * @author zhijun98
 */
public class PeonyBusinessCenterServiceController extends PeonyBusinessServiceController{
    
    @FXML
    private TabPane centerTabPane;
    
    private TaxpayerCenterTabController taxpayerCenterTabController;
    private TaxcorpCenterTabController taxcorpCenterTabController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        centerTabPane.getTabs().clear();
        centerTabPane.getTabs().add(createTaxpayerTab());
        centerTabPane.getTabs().add(createTaxcorpTab());
        
    }
    
    private Tab createTaxpayerTab(){
        Tab tab = new Tab("Taxpayer Summary");
        if (taxpayerCenterTabController == null){
            taxpayerCenterTabController = new TaxpayerCenterTabController();
            taxpayerCenterTabController.addPeonyFaceEventListenerList(this.getPeonyFaceEventListenerList());
            try {
                taxpayerCenterTabController.loadFxml();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        Pane rootPane = taxpayerCenterTabController.getRootPane();
        if (rootPane != null){
            rootPane.prefWidthProperty().bind(centerTabPane.widthProperty());
            rootPane.prefHeightProperty().bind(centerTabPane.heightProperty());
            tab.setContent(rootPane);
        }
        return tab;
    }
    
    private Tab createTaxcorpTab(){
        Tab tab = new Tab("Taxcorp Summary");
        if (taxcorpCenterTabController == null){
            taxcorpCenterTabController = new TaxcorpCenterTabController();
            taxcorpCenterTabController.addPeonyFaceEventListenerList(this.getPeonyFaceEventListenerList());
            try {
                taxcorpCenterTabController.loadFxml();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        Pane rootPane = taxcorpCenterTabController.getRootPane();
        if (rootPane != null){
            rootPane.prefWidthProperty().bind(centerTabPane.widthProperty());
            rootPane.prefHeightProperty().bind(centerTabPane.heightProperty());
            tab.setContent(rootPane);
        }
        return tab;
    }

}
