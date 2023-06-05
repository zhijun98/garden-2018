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

package com.zcomapproach.garden.peony.management.controllers;

import com.zcomapproach.garden.persistence.peony.data.PeonyLogTopic;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.commons.ZcaCalendar;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import org.openide.util.Exceptions;

/**
 *
 * @author zhijun98
 */
public class EmployeeWorkLogsController extends PeonyManagementServiceController {
    @FXML
    private TabPane peonyLogTopicsTabPane;
    
    private final HashMap<PeonyLogTopic, PeonyLogTopicPaneController> peonyLogTopicPaneControllers = new HashMap<>();
    
    private final PeonyEmployee targetPeonyEmployee;

    public EmployeeWorkLogsController(PeonyEmployee targetPeonyEmployee) {
        this.targetPeonyEmployee = targetPeonyEmployee;
    }

    @Override
    protected void decoratePeonyFaceAfterLoadingHelper(Scene scene) {
        super.decoratePeonyFaceAfterLoadingHelper(scene);
        Pane rootPane = this.getRootPane();
        List<PeonyLogTopicPaneController> aPeonyLogTopicPaneControllerList = new ArrayList<>(peonyLogTopicPaneControllers.values());
        for (PeonyLogTopicPaneController aPeonyLogTopicPaneController : aPeonyLogTopicPaneControllerList){
            aPeonyLogTopicPaneController.getRootPane().prefHeightProperty().bind(rootPane.heightProperty());
            aPeonyLogTopicPaneController.getRootPane().prefWidthProperty().bind(rootPane.widthProperty());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        peonyLogTopicsTabPane.getTabs().clear();
        
        List<PeonyLogTopic> aPeonyLogTopicList = PeonyLogTopic.getPeonyLogTopicList(false);
        for (PeonyLogTopic aPeonyLogTopic : aPeonyLogTopicList){
            peonyLogTopicsTabPane.getTabs().add(constructPeonyLogTopicTab(aPeonyLogTopic));
        }
    }

    private Tab constructPeonyLogTopicTab(PeonyLogTopic aPeonyLogTopic) {
        Tab tab = new Tab(aPeonyLogTopic.value());
        
        Date toDate = ZcaCalendar.covertDateToEnding(new Date());
        Date fromDate = ZcaCalendar.addDates(toDate, -7);
        PeonyLogTopicPaneController aPeonyLogTopicPaneController = new PeonyLogTopicPaneController(aPeonyLogTopic, targetPeonyEmployee, fromDate, toDate);
        aPeonyLogTopicPaneController.addPeonyFaceEventListenerList(this.getPeonyFaceEventListenerList());
        try {
            tab.setContent(aPeonyLogTopicPaneController.loadFxml());
            peonyLogTopicPaneControllers.put(aPeonyLogTopic, aPeonyLogTopicPaneController);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return tab;
    }

}
