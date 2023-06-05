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

package com.zcomapproach.garden.peony.view.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.kernel.services.PeonyWelcomeService;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.peony.view.events.PeonyJobSaved;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.constant.JobAssignmentStatus;
import com.zcomapproach.garden.persistence.entity.G02Memo;
import com.zcomapproach.garden.persistence.entity.G02JobAssignment;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.persistence.peony.PeonyEmployeeList;
import com.zcomapproach.garden.persistence.peony.PeonyJob;
import com.zcomapproach.garden.persistence.peony.PeonyJobAssignment;
import com.zcomapproach.garden.persistence.peony.PeonyTaxcorpCase;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.garden.persistence.peony.data.AssignedJobType;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.garden.util.GardenSorter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class PeonyJobAssignmentController extends PeonyFaceController{

    @FXML
    private Label titleLabel;
    @FXML
    private VBox jobVBox;
    @FXML
    private Label noteLabel;
    @FXML
    private ScrollPane employeeCheckListScrollPane;
    @FXML
    private VBox employeeCheckListVBox;
    @FXML
    private JFXTextArea  jobContentTextArea;
    @FXML
    private ComboBox<String> selectNewJobComboBox;
    @FXML
    private JFXTextArea jobAssignmentNoteTextArea;
    @FXML
    private JFXButton jobAssignButton;
    
    private final HashMap<JFXCheckBox, PeonyEmployee> employeeCheckBoxStorage = new HashMap<>();
    
    private final PeonyTaxpayerCase targetPeonyTaxpayerCase;
    private final PeonyTaxcorpCase targetPeonyTaxcorpCase;
    
    private final JFXTextField jobTitleTextField;

    public PeonyJobAssignmentController(Object targetTaxcase) {
        if (targetTaxcase instanceof PeonyTaxpayerCase){
            targetPeonyTaxpayerCase = (PeonyTaxpayerCase)targetTaxcase;
            targetPeonyTaxcorpCase = null;
            jobTitleTextField = null;
        }else if (targetTaxcase instanceof PeonyTaxcorpCase){
            targetPeonyTaxpayerCase = null;
            targetPeonyTaxcorpCase = (PeonyTaxcorpCase)targetTaxcase;
            jobTitleTextField = null;
        }else{
            targetPeonyTaxpayerCase = null;
            targetPeonyTaxcorpCase = null;
            jobTitleTextField = new JFXTextField();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titleLabel.setText(getAssignedJobTitle());
        if (jobTitleTextField != null){
            jobTitleTextField.setPromptText("Job Title");
            jobTitleTextField.setLabelFloat(true);
            jobTitleTextField.setMinWidth(280.0);
            jobTitleTextField.setPrefWidth(280.0);
            jobTitleTextField.setMaxWidth(280.0);
            jobTitleTextField.setStyle("-fx-font-size: 16; -fx-text-fill: #0040ff;");
            VBox.setMargin(jobTitleTextField, new Insets(5, 0, 5, 0));
            jobVBox.getChildren().add(2, jobTitleTextField);
        }
        initializeEmployeeCheckList();
        initializeJobAssignmentDataEntry();
    }

    private void initializeEmployeeCheckList() {
        employeeCheckListVBox.prefWidthProperty().bind(employeeCheckListScrollPane.widthProperty().add(-5));
        employeeCheckBoxStorage.clear();
        Task<PeonyEmployeeList> loadEmployeesTask = new Task<PeonyEmployeeList>(){
            @Override
            protected PeonyEmployeeList call() throws Exception {
                return Lookup.getDefault().lookup(PeonyManagementService.class).retrievePeonyEmployeeList();
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyEmployeeList result = get();
                    List<PeonyEmployee> aPeonyEmployeeList = result.getPeonyEmployeeList();
                    if (aPeonyEmployeeList != null){
                        GardenSorter.sortPeonyEmployeeListByFullName(aPeonyEmployeeList, false);
                        JFXCheckBox employeeCheckBox;
                        for (PeonyEmployee aPeonyEmployee : aPeonyEmployeeList){
                            employeeCheckBox = new JFXCheckBox();
                            employeeCheckBox.setMaxWidth(260.0);
                            employeeCheckBox.setText(aPeonyEmployee.getTextLine());
                            employeeCheckBox.setOnAction((ActionEvent event) -> {
                                refreshNotePromptText();
                            });
                            employeeCheckListVBox.getChildren().add(employeeCheckBox);
                            employeeCheckBoxStorage.put(employeeCheckBox, aPeonyEmployee);
                        }
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception raised. " + ex.getMessage());
                }
            }

            private void refreshNotePromptText() {
                String noteText = "Assign to: ";
                Set<JFXCheckBox> keys =  employeeCheckBoxStorage.keySet();
                Iterator<JFXCheckBox> itr = keys.iterator();
                JFXCheckBox key;
                PeonyEmployee employee;
                while (itr.hasNext()){
                    key = itr.next();
                    employee = employeeCheckBoxStorage.get(key);
                    if (key.isSelected()){
                        noteText += employee.getShortTextLine() + "; ";
                    }
                }
                noteText = noteText.trim();
                if (noteText.endsWith(";")){
                    noteText = noteText.substring(0, noteText.length()-1);
                }
                noteLabel.setText(noteText);
            }
        };
        this.getCachedThreadPoolExecutorService().submit(loadEmployeesTask);
    }
    
    private List<PeonyEmployee> getToEmployeeListFromCheckBoxes() {
        List<PeonyEmployee> result = new ArrayList<>();
        Set<JFXCheckBox> keys =  employeeCheckBoxStorage.keySet();
        Iterator<JFXCheckBox> itr = keys.iterator();
        JFXCheckBox key;
        while (itr.hasNext()){
            key = itr.next();
            if (key.isSelected()){
                result.add(employeeCheckBoxStorage.get(key));
            }
        }
        return result;
    }

    private void initializeJobAssignmentDataEntry() {
        selectNewJobComboBox.getItems().clear();
        if (targetPeonyTaxpayerCase != null){
            selectNewJobComboBox.getItems().addAll(AssignedJobType.getTaxpayerEnumValueList());
        }else if (targetPeonyTaxcorpCase != null){
            selectNewJobComboBox.getItems().addAll(AssignedJobType.getTaxcorpEnumValueList());
        }else{
            selectNewJobComboBox.getItems().addAll(AssignedJobType.getCommonEnumValueList());
        }
        selectNewJobComboBox.setOnAction((ActionEvent event) -> {
            if (ZcaValidator.isNullEmpty(jobContentTextArea.getText())){
                jobContentTextArea.setText(AssignedJobType.getParamDescription(
                        AssignedJobType.convertEnumValueToType(
                                selectNewJobComboBox.getSelectionModel().getSelectedItem())));
            }
        });
        
        jobContentTextArea.setStyle("-fx-font-size: 16; -fx-text-fill: #0040ff;");
        jobAssignmentNoteTextArea.setStyle("-fx-font-size: 16; -fx-text-fill: #0040ff;");
        
        jobAssignButton.setOnAction((ActionEvent event) -> {
            if (selectNewJobComboBox.isDisabled()){
////                assignToExistingJob();
            }else{
                assignToNewJob();
            }
        });
    }

    private String getAssignedJobTitle() {
        if (targetPeonyTaxpayerCase != null){
            return targetPeonyTaxpayerCase.getTaxpayerCaseTitle(false);
        }else if (targetPeonyTaxcorpCase != null){
            return targetPeonyTaxcorpCase.getTaxcorpCaseTitle();
        }else{
            return GardenEntityType.JOB_ASSIGNMENT.value();
        }
    }

    private void assignToNewJob() {
        if (Platform.isFxApplicationThread()){
            assignToNewJobHelper();
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    assignToNewJobHelper();
                }
            });
        }
    }

    private void assignToNewJobHelper() {
////        if (!isPrivilegeAuthorized()){
////            PeonyFaceUtils.displayErrorMessageDialog("You need to be authorized to do this operation.");
////            return;
////        }
        
        List<PeonyEmployee> toPeonyEmployeeList = getToEmployeeListFromCheckBoxes();
        if (toPeonyEmployeeList.isEmpty()){
            PeonyFaceUtils.displayErrorMessageDialog("Please select at least one employee to whom you assign this job.");
            return;
        }
        String jobType = selectNewJobComboBox.getSelectionModel().getSelectedItem();
        if (ZcaValidator.isNullEmpty(jobType)){
            PeonyFaceUtils.displayErrorMessageDialog("Please select a new job or existing job for this assignment");
            return;
        }
        String jobContent = jobContentTextArea.getText();
        if (ZcaValidator.isNullEmpty(jobContent) || (jobContent.length() > 450)){
            PeonyFaceUtils.displayErrorMessageDialog("Please type in the job content whose length is at most 450 characters.");
            return;
        }
        String assignmentNote  = jobAssignmentNoteTextArea.getText();
        if (ZcaValidator.isNotNullEmpty(assignmentNote) && (assignmentNote.length() > 450)){
            PeonyFaceUtils.displayErrorMessageDialog("Please the job assignment note's length is at most 450 characters.");
            return;
        }
        String currentEmployeeUuuid = PeonyProperties.getSingleton().getCurrentLoginUserUuid();
        PeonyJob aPeonyJob = new PeonyJob();
        aPeonyJob.setJobCreator(PeonyProperties.getSingleton().getCurrentLoginEmployee());
        G02Memo job = aPeonyJob.getJobContent();
        job.setMemoUuid(ZcaUtils.generateUUIDString());
        job.setMemo(jobContent);
        job.setMemoType(GardenEntityType.JOB_ASSIGNMENT.name());
        job.setTimestamp(new Date());
        job.setOperatorAccountUuid(currentEmployeeUuuid);
        job.setEntityStatus(jobType);
        if (targetPeonyTaxpayerCase != null){
            job.setMemoDescription(targetPeonyTaxpayerCase.getTaxpayerCaseTitle(false));
            job.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
            job.setEntityUuid(targetPeonyTaxpayerCase.getTaxpayerCase().getTaxpayerCaseUuid());
        }else if (targetPeonyTaxcorpCase != null){
            job.setMemoDescription(targetPeonyTaxcorpCase.getTaxcorpCaseTitle());
            job.setEntityType(GardenEntityType.TAXCORP_CASE.name());
            job.setEntityUuid(targetPeonyTaxcorpCase.getTaxcorpCase().getTaxcorpCaseUuid());
        }else{
            if (jobTitleTextField == null){
                job.setMemoDescription(GardenEntityType.JOB_ASSIGNMENT.value());
            }else{
                if (ZcaValidator.isNullEmpty(jobTitleTextField.getText())){
                    PeonyFaceUtils.displayErrorMessageDialog("Job title is required for this assignment.");
                    return;
                }else{
                    job.setMemoDescription(jobTitleTextField.getText());
                }
            }
        }
        PeonyJobAssignment jobAssignment;
        G02JobAssignment assignment;
        String memoAssignTo = "Job Assigned to: "; 
        for (PeonyEmployee toPeonyEmployee : toPeonyEmployeeList){
            jobAssignment = new PeonyJobAssignment();
            jobAssignment.setFromEmployee(aPeonyJob.getJobCreator());
            jobAssignment.setToEmployee(toPeonyEmployee);
            assignment = jobAssignment.getJobAssignment();
            assignment.setJobAssignmentUuid(ZcaUtils.generateUUIDString());
            assignment.setAssignFromUuid(currentEmployeeUuuid);
            assignment.setAssignToUuid(toPeonyEmployee.getAccount().getAccountUuid());
            assignment.setAssignmentNote(assignmentNote);
            assignment.setAssignmentStatus(JobAssignmentStatus.INITIAL.value());
            assignment.setAssignmentTimestamp(new Date());
            assignment.setJobUuid(job.getMemoUuid());
            
            aPeonyJob.getJobAssignmentList().add(jobAssignment);
            
            memoAssignTo += toPeonyEmployee.getShortTextLine() + "; ";
        }
        memoAssignTo = memoAssignTo.trim();
        if (memoAssignTo.endsWith(";")){
            memoAssignTo = memoAssignTo.substring(0, memoAssignTo.length()-1);
        }
        /**
         * jobAcceptorMemo
         */
        G02Memo memo = new G02Memo();
        memo.setMemoUuid(ZcaUtils.generateUUIDString());
        memo.setMemo(memoAssignTo);
        memo.setMemoType(GardenEntityType.JOB_ASSIGNMENT_ACCEPTOR.name());
        memo.setOperatorAccountUuid(currentEmployeeUuuid);
        //jobAcceptorMemo.setEntityComboUuid();
        memo.setEntityStatus(job.getEntityStatus());
        memo.setEntityType(job.getEntityType());
        memo.setEntityUuid(job.getEntityUuid());
        memo.setInitialMemoUuid(job.getMemoUuid());
        memo.setTimestamp(new Date());
        aPeonyJob.setJobAcceptorMemo(memo);
        /**
         * jobNoteMemo
         */
        if (ZcaValidator.isNotNullEmpty(assignmentNote)){
            memo = new G02Memo();
            memo.setMemoUuid(ZcaUtils.generateUUIDString());
            memo.setMemo(assignmentNote);
            memo.setMemoType(GardenEntityType.JOB_ASSIGNMENT_NOTE.name());
            memo.setOperatorAccountUuid(currentEmployeeUuuid);
            //jobAcceptorMemo.setEntityComboUuid();
            memo.setEntityStatus(job.getEntityStatus());
            memo.setEntityType(job.getEntityType());
            memo.setEntityUuid(job.getEntityUuid());
            memo.setInitialMemoUuid(aPeonyJob.getJobAcceptorMemo().getMemoUuid());
            memo.setTimestamp(new Date());
            aPeonyJob.setJobNoteMemo(memo);
        }
        storePeonyJob(aPeonyJob);
    }
    
    private void storePeonyJob(final PeonyJob aPeonyJob){
        
        Task<PeonyJob> task = new Task<PeonyJob>(){
            @Override
            protected PeonyJob call() throws Exception {
                return Lookup.getDefault().lookup(PeonyManagementService.class).getPeonyManagementRestClient()
                        .storeEntity_XML(PeonyJob.class, GardenRestParams.Management.storePeonyJobRestParams(), aPeonyJob);
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayErrorMessageDialog("Cannot save the job assignment due to technical failure. " + getMessage());
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyJob aPeonyJob = get();
                    if (aPeonyJob != null){
                        broadcastPeonyFaceEventHappened(new PeonyJobSaved(aPeonyJob));
                        broadcastPeonyFaceEventHappened(new CloseDialogRequest());
                        List<PeonyEmployee> toPeonyEmployeeList = getToEmployeeListFromCheckBoxes();
                        for (PeonyEmployee toPeonyEmployee : toPeonyEmployeeList){
                            if (PeonyProperties.getSingleton().getCurrentLoginUserUuid().equalsIgnoreCase(toPeonyEmployee.getEmployeeInfo().getEmployeeAccountUuid())){
                                Lookup.getDefault().lookup(PeonyWelcomeService.class).refreshTodayJobPaneForJobAssignmentUpdated(aPeonyJob);
                                break;
                            }
                        }
                    }else{
                        PeonyFaceUtils.displayErrorMessageDialog("Cannot save this one.");
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (Exception ex){
                    PeonyFaceUtils.displayErrorMessageDialog("Cannot save the job assignment due to technical error. " + ex.getMessage());
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(task);
    
    }

////    private void assignToExistingJob() {
////        if (Platform.isFxApplicationThread()){
////            assignToExistingJobHelper();
////        }else{
////            Platform.runLater(new Runnable(){
////                @Override
////                public void run() {
////                    assignToExistingJobHelper();
////                }
////            });
////        }
////    }
////
////    private void assignToExistingJobHelper() {
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
////    }
////
////    private boolean isPrivilegeAuthorized() {
////        if (targetPeonyTaxpayerCase != null){
////            return PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.ASSIGN_TAXPAYER_JOB);
////        }else if (targetPeonyTaxcorpCase != null){
////            return PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.ASSIGN_TAXCORP_JOB);
////        }else{
////            return false;
////        }
////    }

}
