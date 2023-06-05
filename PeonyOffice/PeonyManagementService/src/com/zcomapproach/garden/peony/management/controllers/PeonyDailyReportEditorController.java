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

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxcorpService;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import com.zcomapproach.garden.peony.kernel.services.PeonyWelcomeService;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.peony.view.events.PeonyDailyReportSaved;
import com.zcomapproach.garden.persistence.constant.DailyReportLocation;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.constant.JobAssignmentStatus;
import com.zcomapproach.garden.persistence.entity.G02DailyReport;
import com.zcomapproach.garden.persistence.entity.G02DailyReportPK;
import com.zcomapproach.garden.persistence.entity.G02JobAssignment;
import com.zcomapproach.garden.persistence.entity.G02Memo;
import com.zcomapproach.garden.persistence.peony.PeonyDailyReport;
import com.zcomapproach.garden.rest.GardenRestParams;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javax.swing.JOptionPane;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class PeonyDailyReportEditorController extends PeonyManagementServiceController{

    @FXML
    private JFXComboBox<String> locationComboBox;
    @FXML
    private JFXComboBox<Integer> progressComboBox;
    @FXML
    private JFXComboBox<Double> hoursComboBox;
    @FXML
    private JFXTextArea descirptionTextArea;
    @FXML
    private JFXButton saveFollowupButton;
    @FXML
    private JFXButton saveReportButton;
    @FXML
    private JFXButton closeButton;
    @FXML
    private VBox jobAssignmentVBox;
    
    private final PeonyDailyReport targetPeonyDailyReport;

    public PeonyDailyReportEditorController(PeonyDailyReport targetPeonyDailyReport) {
        this.targetPeonyDailyReport = targetPeonyDailyReport;
    }
    
    private int getLastDailyReportProgress(){
        if (!targetPeonyDailyReport.getHistoricalDailyReports().isEmpty()){
            return targetPeonyDailyReport.getSortedHistoricalDailyReports().get(0).getWorkingProgress();
        }
        return 0;
    }
    
    private boolean validateWorkingProgessHelper(){
        if (progressComboBox.getValue() == null){
            PeonyFaceUtils.displayErrorMessageDialog("You need input the working progress for this job.");
            return false;
        }
        if (progressComboBox.getValue() <= getLastDailyReportProgress()){
            return PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "The working progress is the same as the prevouse one you input before, are you sure to report it?") == JOptionPane.YES_OPTION;
        }
        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        locationComboBox.getItems().addAll(DailyReportLocation.getEnumNameList(false));
        locationComboBox.getSelectionModel().select(targetPeonyDailyReport.getDailyReport().getWorkingLocation());
        
        int progress = 5;
        for (int i = 0; i < 21; i++){
            progressComboBox.getItems().add(progress*i);
        }
        if (targetPeonyDailyReport.getDailyReport().getWorkingProgress() == null){
            progressComboBox.getSelectionModel().select(getLastDailyReportProgress());
        }else{
            progressComboBox.getSelectionModel().select(targetPeonyDailyReport.getDailyReport().getWorkingProgress());
        }
        
        double hours = 0.25;
        for (int i = 0; i < 50; i++){
            hoursComboBox.getItems().add(hours*i);
        }
        hoursComboBox.getSelectionModel().select(targetPeonyDailyReport.getDailyReport().getWorkingHours());
        
        descirptionTextArea.setText(targetPeonyDailyReport.getDailyReport().getWorkingDescription());
        
        saveFollowupButton.setOnAction((ActionEvent event) -> {
            if (validateWorkingProgessHelper()){
                //save job-assignment
                if (progressComboBox.getValue() == 100){
                    //save daily report
                    getCachedThreadPoolExecutorService().submit(new SaveDailyReportTask(JobAssignmentStatus.FOLLOW_UP));
                }else{
                    PeonyFaceUtils.displayErrorMessageDialog("If this job's progress is not 100% yet, you cannot use this option.");
                    return;
                }
            }
        });
        
        saveReportButton.setOnAction((ActionEvent event) -> {
            if (validateWorkingProgessHelper()){
                //save daily report
                if (progressComboBox.getValue() == 100){
                    getCachedThreadPoolExecutorService().submit(new SaveDailyReportTask(JobAssignmentStatus.COMPLETED));
                }else if (progressComboBox.getValue() > 0){
                    getCachedThreadPoolExecutorService().submit(new SaveDailyReportTask(JobAssignmentStatus.IN_PROGRESS));
                }else{
                    getCachedThreadPoolExecutorService().submit(new SaveDailyReportTask(JobAssignmentStatus.INITIAL));
                }
            }
        });
        
        closeButton.setOnAction((ActionEvent event) -> {
            broadcastPeonyFaceEventHappened(new CloseDialogRequest(true));
        });
        
        initializeJobAssignmentVBox();
    }

    private void initializeJobAssignmentVBox() {
        G02Memo targetG02JobMemo = targetPeonyDailyReport.getJobContent();
        Label memoLabel;
        if (targetG02JobMemo == null){
            memoLabel = new Label("Exception: cannot find the memo for this unknown job assignment.");
            memoLabel.setWrapText(true);
        }else{
            //title
            memoLabel = new Label(targetG02JobMemo.getMemoDescription());
            memoLabel.setStyle("-fx-font-weight: bolder; -fx-font-size: 14;");
            memoLabel.setWrapText(true);
            jobAssignmentVBox.getChildren().add(memoLabel);
            FlowPane flowPane = new FlowPane();
            flowPane.setHgap(5.0);
            flowPane.setVgap(2.0);
            //memo timestamp
            memoLabel = new Label(ZcaCalendar.convertToMMddyyyyHHmmss(targetG02JobMemo.getTimestamp(), "-", "@", ":"));
            memoLabel.setStyle("-fx-font-style: italic;");
            flowPane.getChildren().add(memoLabel);
            //Link
            JFXButton button = new JFXButton("[Details]");
            button.setStyle("-fx-text-fill: blue;");
            final String entityUuid = targetG02JobMemo.getEntityUuid();
            final String entityType = targetG02JobMemo.getEntityType();
            button.setOnAction((ActionEvent event) -> {
                if (GardenEntityType.TAXPAYER_CASE.name().equalsIgnoreCase(entityType)){
                    Lookup.getDefault().lookup(PeonyTaxpayerService.class).launchPeonyTaxpayerCaseTopComponentByTaxpayerCaseUuid(entityUuid);
                }else if (GardenEntityType.TAXCORP_CASE.name().equalsIgnoreCase(entityType)){
                    Lookup.getDefault().lookup(PeonyTaxcorpService.class).launchPeonyTaxcorpCaseTopComponentByTaxcorpCaseUuid(entityUuid);
                }
            });
            flowPane.getChildren().add(button);

            jobAssignmentVBox.getChildren().add(flowPane);

            jobAssignmentVBox.getChildren().add(new Separator());

            //memo
            memoLabel = new Label(targetG02JobMemo.getMemo());
            memoLabel.setStyle("-fx-font-size: 14;");
            memoLabel.setWrapText(true);
            jobAssignmentVBox.getChildren().add(memoLabel);
        }
    }
    
    private class SaveDailyReportTask extends Task<G02DailyReport>{
        
        private final JobAssignmentStatus status;

        public SaveDailyReportTask(JobAssignmentStatus status) {
            if (status == null){
                status = JobAssignmentStatus.INITIAL;
            }
            this.status = status;
        }

        @Override
        protected G02DailyReport call() throws Exception {
            G02DailyReport report = targetPeonyDailyReport.getDailyReport();
            String todayReportDate = ZcaCalendar.convertToMMddyyyy(new Date(), "-");
            if ((report == null) || (report.getG02DailyReportPK() == null)){
                report = new G02DailyReport();
                G02DailyReportPK pkid = new G02DailyReportPK();
                pkid.setReporterUuid(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
                pkid.setJobAssignmentUuid(targetPeonyDailyReport.getJobAssignment().getJobAssignmentUuid());
                pkid.setReportDate(todayReportDate);
                report.setG02DailyReportPK(pkid);
            }
            //description
            report.setWorkingDescription(descirptionTextArea.getText());
            //hours
            if (hoursComboBox.getValue() == null){
                this.updateMessage("Please input how many hours you spent on this job.");
                return null;
            }
            report.setWorkingHours(hoursComboBox.getValue());
            //location
            if (ZcaValidator.isNullEmpty(locationComboBox.getValue())){
                this.updateMessage("Please input where you did this job.");
                return null;
            }
            report.setWorkingLocation(locationComboBox.getValue());
            //progress
            report.setWorkingProgress(progressComboBox.getValue());
            
            G02DailyReport result = Lookup.getDefault().lookup(PeonyManagementService.class).getPeonyManagementRestClient()
                    .storeEntity_XML(G02DailyReport.class, GardenRestParams.Management.storeG02DailyReportRestParams(), report);
            //find my own job assignment
            targetPeonyDailyReport.getJobAssignment().setAssignmentStatus(status.value());
            targetPeonyDailyReport.setDailyReport(report);
            Lookup.getDefault().lookup(PeonyManagementService.class).getPeonyManagementRestClient()
                    .storeEntity_XML(G02JobAssignment.class, 
                            GardenRestParams.Management.storeG02JobAssignmentRestParams(), 
                            targetPeonyDailyReport.getJobAssignment());
            return result;
        }

        @Override
        protected void failed() {
            PeonyFaceUtils.displayErrorMessageDialog("Peony failed to save this daily report. " + getMessage());
        }

        @Override
        protected void succeeded() {
            try {
                G02DailyReport result = get();
                if (result == null){
                    PeonyFaceUtils.displayErrorMessageDialog("Peony cannot save this daily report right now. " + getMessage());
                }else{
                    PeonyFaceUtils.displayInformationMessageDialog("Peony succesfully saved this daily report. ");
                    
                    Lookup.getDefault().lookup(PeonyWelcomeService.class).refreshTodayJobPaneForDailyReportUpdated(result);
                    broadcastPeonyFaceEventHappened(new PeonyDailyReportSaved(targetPeonyDailyReport));
                    broadcastPeonyFaceEventHappened(new CloseDialogRequest());
                }
            } catch (InterruptedException | ExecutionException ex) {
                //Exceptions.printStackTrace(ex);
                PeonyFaceUtils.displayErrorMessageDialog("Peony cannot save this daily report right now. Please try it later. " + getMessage());
            }
        }
    }
}
