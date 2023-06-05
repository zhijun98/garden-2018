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

import com.zcomapproach.garden.peony.controls.PeonyButtonTableCell;
import com.zcomapproach.garden.peony.kernel.services.PeonyKernelService;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.CalendarPeriodSelected;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.peony.data.PeonyLogTopic;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.persistence.peony.PeonyLog;
import com.zcomapproach.garden.persistence.peony.PeonyLogList;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.ZcaCalendar;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class PeonyLogTopicPaneController extends PeonyManagementServiceController implements PeonyFaceEventListener{
    @FXML
    private TableView<PeonyLog> logTableView;
    @FXML
    private Button displayLogsByPeriodButton;
    
    private final PeonyLogTopic targetPeonyLogTopic;
    private final PeonyEmployee targetPeonyEmployee;
    
    private Date fromDate;
    private Date toDate;

    public PeonyLogTopicPaneController(PeonyLogTopic targetPeonyLogTopic, 
                                       PeonyEmployee targetPeonyEmployee, 
                                       Date fromDate, 
                                       Date toDate) {
        this.targetPeonyLogTopic = targetPeonyLogTopic;
        this.targetPeonyEmployee = targetPeonyEmployee;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof CalendarPeriodSelected){
            handleCalendarPeriodSelected((CalendarPeriodSelected)event);
        }
    }
    
    private void handleCalendarPeriodSelected(final CalendarPeriodSelected event){
        fromDate = event.getFrom();
        toDate = event.getTo();
        PeonyFaceUtils.displayInformationMessageDialog("The period is changed: from " 
                + ZcaCalendar.convertToMMddyyyy(fromDate, "-") + " to " + ZcaCalendar.convertToMMddyyyy(toDate, "-"));
        loadLogTableView();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        displayLogsByPeriodButton.setGraphic(PeonyGraphic.getImageView("calendar.png"));
        displayLogsByPeriodButton.setOnAction((ActionEvent event) -> {
            List<PeonyFaceEventListener> peonyFaceEventListeners = new ArrayList<>();
            peonyFaceEventListeners.add(this);
            Lookup.getDefault().lookup(PeonyKernelService.class).displayPeriodSelectionDialog("Select Period: " + targetPeonyLogTopic.value(), peonyFaceEventListeners, fromDate, toDate);
        });
        
        logTableView.getColumns().clear();
        
        TableColumn<PeonyLog, String> employeeColumn = new TableColumn<>("Employee");
        employeeColumn.setCellValueFactory(new PropertyValueFactory<>("employeeFullName"));
        employeeColumn.setPrefWidth(150.0);
        logTableView.getColumns().add(employeeColumn);
        
        TableColumn<PeonyLog, String> logNameColumn = new TableColumn<>("Log Name");
        logNameColumn.setCellValueFactory(new PropertyValueFactory<>("logName"));
        logNameColumn.setPrefWidth(150.0);
        logTableView.getColumns().add(logNameColumn);
        
        TableColumn<PeonyLog, String> logMessageColumn = new TableColumn<>("Log Message");
        logMessageColumn.setCellValueFactory(new PropertyValueFactory<>("logMessage"));
        logMessageColumn.setPrefWidth(300.0);
        logTableView.getColumns().add(logMessageColumn);
        
        TableColumn<PeonyLog, Date> timestampColumn = new TableColumn<>("Timestamp");
        timestampColumn.setCellValueFactory(new PropertyValueFactory<>("logTimestamp"));
        timestampColumn.setPrefWidth(150.0);
        logTableView.getColumns().add(timestampColumn);
        
        TableColumn<PeonyLog, String> entityTypeColumn = new TableColumn<>("Description");
        entityTypeColumn.setCellValueFactory(new PropertyValueFactory<>("entityDescription"));
        entityTypeColumn.setPrefWidth(300.0);
        logTableView.getColumns().add(entityTypeColumn);
        
        TableColumn loggedDetailsColumn = new TableColumn("");
        loggedDetailsColumn.setCellFactory(PeonyButtonTableCell.<PeonyLog>callbackForTableColumn(
                "Logged Entity", (PeonyLog aPeonyLog) -> {
                    Lookup.getDefault().lookup(PeonyManagementService.class).launchLoggedEntityWindow(aPeonyLog);
                    return aPeonyLog;
                }));
        loggedDetailsColumn.setPrefWidth(100.00);
        logTableView.getColumns().add(loggedDetailsColumn);
        
        TableColumn targetDetailsColumn = new TableColumn("");
        targetDetailsColumn.setCellFactory(PeonyButtonTableCell.<PeonyLog>callbackForTableColumn(
                "Target Entity", (PeonyLog aPeonyLog) -> {
                    Lookup.getDefault().lookup(PeonyManagementService.class).launchEntityOwnerWindow(aPeonyLog);
                    return aPeonyLog;
                }));
        targetDetailsColumn.setPrefWidth(100.00);
        logTableView.getColumns().add(targetDetailsColumn);
        
        loadLogTableView();
        
    }

    private void loadLogTableView() {
        Task<PeonyLogList> retrievePeonyLogListTask = new Task<PeonyLogList>(){
            @Override
            protected PeonyLogList call() throws Exception {
                return Lookup.getDefault().lookup(PeonyManagementService.class)
                        .getPeonyManagementRestClient().findEntity_XML(PeonyLogList.class, 
                                GardenRestParams.Management.findPeonyLogsForTopicByEmployeeByPeriodRestParams(
                                        targetPeonyLogTopic.name(), targetPeonyEmployee.getAccount().getAccountUuid(), 
                                        String.valueOf(fromDate.getTime()), String.valueOf(toDate.getTime())));
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayErrorMessageDialog("Technical error was raised. Cannot complete this operation. " + getMessage());
            }
            
            @Override
            protected void succeeded() {
                try {
                    PeonyLogList aPeonyLogList = get();
                    logTableView.setItems(FXCollections.observableArrayList(aPeonyLogList.getLogs()));
                    logTableView.refresh();
                } catch (InterruptedException | ExecutionException ex) {
                    //Exceptions.printStackTrace(ex);
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(retrievePeonyLogListTask);
    }

}
