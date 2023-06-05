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

import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.persistence.peony.PeonyDailyReport;
import com.zcomapproach.garden.persistence.peony.PeonyDailyReportList;
import com.zcomapproach.garden.persistence.peony.PeonyJobAssignment;
import com.zcomapproach.garden.rest.GardenRestParams;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class DailyReportHistoryController extends PeonyManagementServiceController{

    @FXML
    private Label jobTitleLabel;
    @FXML
    private Label jobTimestampLabel;
    @FXML
    private Label jobDescriptionLabel;
    @FXML
    private Label totalHoursLabel;
    @FXML
    private TableView<PeonyDailyReport> dailyReportTableView;
    
    private final String targetPeonyJobAssignmentUuid;
    
    public DailyReportHistoryController(String targetPeonyJobAssignmentUuid) {
        this.targetPeonyJobAssignmentUuid = targetPeonyJobAssignmentUuid;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
                    
        dailyReportTableView.getColumns().clear();
        
        TableColumn<PeonyDailyReport, String> reportDateColumn = new TableColumn<>("Report Date");
        reportDateColumn.setCellValueFactory(new PropertyValueFactory<>("jobReportDate"));
        reportDateColumn.setPrefWidth(100.0);
        dailyReportTableView.getColumns().add(reportDateColumn);
        
        TableColumn<PeonyDailyReport, String> jobWorkingLocationColumn = new TableColumn<>("Location");
        jobWorkingLocationColumn.setCellValueFactory(new PropertyValueFactory<>("jobWorkingLocation"));
        jobWorkingLocationColumn.setPrefWidth(100.0);
        dailyReportTableView.getColumns().add(jobWorkingLocationColumn);
        
        TableColumn<PeonyDailyReport, String> jobWorkingHoursColumn = new TableColumn<>("Spent-Time");
        jobWorkingHoursColumn.setCellValueFactory(new PropertyValueFactory<>("jobWorkingHours"));
        jobWorkingHoursColumn.setPrefWidth(100.0);
        dailyReportTableView.getColumns().add(jobWorkingHoursColumn);
        
        TableColumn<PeonyDailyReport, String> jobWorkingProgressColumn = new TableColumn<>("Progress");
        jobWorkingProgressColumn.setCellValueFactory(new PropertyValueFactory<>("jobWorkingProgress"));
        jobWorkingProgressColumn.setPrefWidth(100.0);
        dailyReportTableView.getColumns().add(jobWorkingProgressColumn);
        
        TableColumn<PeonyDailyReport, String> jobWorkingDescriptionColumn = new TableColumn<>("Description");
        jobWorkingDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("jobWorkingDescription"));
        dailyReportTableView.getColumns().add(jobWorkingDescriptionColumn);
        
        this.getSingleExecutorService().submit(new Task<PeonyDailyReportList>(){
            @Override
            protected PeonyDailyReportList call() throws Exception {
                try {
                    return Lookup.getDefault().lookup(PeonyManagementService.class)
                            .getPeonyManagementRestClient().findEntity_XML(PeonyDailyReportList.class, 
                                    GardenRestParams.Management.findPeonyDailyReportListByJobAssignmentUuidRestParams(targetPeonyJobAssignmentUuid));
                } catch (Exception ex) {
                    PeonyFaceUtils.publishMessageOntoOutputWindow("Failed to retrieve today's job assignment list. " + ex.getMessage());
                    return null;
                }
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayErrorMessageDialog("Failed to display daily reports for this job. " + getMessage());
                broadcastPeonyFaceEventHappened(new CloseDialogRequest(true));
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyDailyReportList result = get();
                    double totalHours = 0;
                    if (result != null){
                        PeonyJobAssignment aPeonyJobAssignment = result.getTargetPeonyJobAssignment();
                        jobTitleLabel.setText(aPeonyJobAssignment.getJobMemo().getMemoDescription());
                        jobTimestampLabel.setText("Assigned by: " 
                                + Lookup.getDefault().lookup(PeonyManagementService.class).retrievePeonyEmployeeFullName(aPeonyJobAssignment.getJobAssignment().getAssignFromUuid())+ 
                                ZcaCalendar.convertToMMddyyyyHHmmss(aPeonyJobAssignment.getJobMemo().getTimestamp(), "-", "@", ":"));
                        jobDescriptionLabel.setText(aPeonyJobAssignment.getJobMemo().getMemo());
                        dailyReportTableView.getItems().clear();
                        List<PeonyDailyReport> aPeonyDailyReportList = result.getPeonyDailyReportList();
                        if ((aPeonyDailyReportList != null) && (!aPeonyDailyReportList.isEmpty())){

                            for (PeonyDailyReport aPeonyDailyReport : aPeonyDailyReportList){
                                totalHours += aPeonyDailyReport.getDailyReport().getWorkingHours();
                            }       
                            dailyReportTableView.getItems().addAll(aPeonyDailyReportList);
                        }
                    }
                    totalHoursLabel.setText("Total Hours: " + totalHours);
                } catch (InterruptedException | ExecutionException ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Peony cannot display daily reports for this job right now. Please try it later. " + getMessage());
                }
            }
        });
    }

}
