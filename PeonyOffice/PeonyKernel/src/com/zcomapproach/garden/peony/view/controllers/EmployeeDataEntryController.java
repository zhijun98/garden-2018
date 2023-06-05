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
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.persistence.G02EntityValidator;
import com.zcomapproach.garden.persistence.constant.GardenEmploymentStatus;
import com.zcomapproach.garden.persistence.constant.GardenWorkTitle;
import com.zcomapproach.garden.persistence.entity.G02Employee;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.ZcaCalendar;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class EmployeeDataEntryController extends PeonyFaceController{
    @FXML
    private Label titleLabel;
    @FXML
    private JFXComboBox<String> workTitleComboBox;
    @FXML
    private DatePicker employedDatePicker;
    @FXML
    private JFXTextField emailField;
    @FXML
    private JFXTextField phoneField;
    @FXML
    private JFXTextField memoField;
    @FXML
    private JFXComboBox<String> workStatusComboBox;
    @FXML
    private JFXButton saveEmployeeButton;
    
    private final PeonyEmployee targetPeonyEmployee;

    public EmployeeDataEntryController(PeonyEmployee targetPeonyEmployee) {
        this.targetPeonyEmployee = targetPeonyEmployee;
    }
    
    public void setTitleLabel(String title){
        //titleLabel.setText("Primary Taxpayer's Address");
        if (Platform.isFxApplicationThread()){
            titleLabel.setText(title);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    titleLabel.setText(title);
                }
            });
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titleLabel.setText("Employee");
        saveEmployeeButton.setGraphic(PeonyGraphic.getImageView("database_save.png"));
        saveEmployeeButton.setOnAction((ActionEvent event) -> {
            saveTargetEmployee();
        });
        
        initializeEmployeeDataEntryPane();
    }
    
    @Override
    protected void resetDataEntryStyleHelper(){
        emailField.setStyle(null);
        phoneField.setStyle(null);
        workStatusComboBox.setStyle(null);
        workTitleComboBox.setStyle(null);
        employedDatePicker.setStyle(null);
        memoField.setStyle(null);
    }

    private void initializeEmployeeDataEntryPane() {
        G02Employee aG02Employee = targetPeonyEmployee.getEmployeeInfo();
        
        PeonyFaceUtils.initializeTextField(emailField, aG02Employee.getWorkEmail(), null, "Work email", this);
        PeonyFaceUtils.initializeTextField(phoneField, aG02Employee.getWorkPhone(), null, "Work phone", this);
        PeonyFaceUtils.initializeComboBox(workStatusComboBox, GardenEmploymentStatus.getEnumValueList(false), aG02Employee.getEmploymentStatus(), null, "Employment status", this);
        PeonyFaceUtils.initializeComboBox(workTitleComboBox, GardenWorkTitle.getEnumValueList(false), aG02Employee.getWorkTitle(), null, "Work title", this);
        PeonyFaceUtils.initializeDatePicker(employedDatePicker, ZcaCalendar.convertToLocalDate(aG02Employee.getEmployedDate()), null, "Employed date", this);
        PeonyFaceUtils.initializeTextField(memoField, aG02Employee.getMemo(), null, "Memo, max 450 characters", this);
    }
    
    public void collectTargetEmployeeData() throws ZcaEntityValidationException{
        G02Employee aG02Employee = targetPeonyEmployee.getEmployeeInfo();
        if (ZcaValidator.isNullEmpty(aG02Employee.getEmployeeAccountUuid())){
            throw new ZcaEntityValidationException("Tech: UUID is demanded to be ready before saving.");
        }
        aG02Employee.setWorkEmail(emailField.getText());
        aG02Employee.setWorkPhone(phoneField.getText());
        aG02Employee.setEmploymentStatus(workStatusComboBox.getValue());
        aG02Employee.setWorkTitle(workTitleComboBox.getValue());
        aG02Employee.setEmployedDate(ZcaCalendar.convertToDate(employedDatePicker.getValue()));
        aG02Employee.setMemo(memoField.getText());
    }

    private void saveTargetEmployee() {
        Task<G02Employee> saveG02EmployeeTask = new Task<G02Employee>(){
            @Override
            protected G02Employee call() throws Exception {
                try{
                    collectTargetEmployeeData();
                }catch (ZcaEntityValidationException ex){
                    highlightBadEntityField(ex);
                    updateMessage(ex.getMessage());
                    return null;
                }
                G02Employee aG02Employee = null;
                try{
                    G02EntityValidator.getSingleton().validate(targetPeonyEmployee.getEmployeeInfo());
                    aG02Employee = Lookup.getDefault().lookup(PeonyManagementService.class).getPeonyManagementRestClient()
                            .storeEntity_XML(G02Employee.class, GardenRestParams.Management.storeG02EmployeeRestParams(), targetPeonyEmployee.getEmployeeInfo());
                }catch (Exception ex){
                    updateMessage(ex.getMessage());
                }
                return aG02Employee;
            }

            @Override
            protected void succeeded() {
                try {
                    G02Employee aG02Employee = get();
                    if (aG02Employee == null){
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }else{
                        resetDataEntryStyle();
                        setDataEntryChanged(false);
                        PeonyFaceUtils.displayInformationMessageDialog("This employee is saved.");
                        //broadcastPeonyFaceEventHappened(new PeonyAccountSaved(targetPeonyAccountProfile, forNewEntity));
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("This saving-employee operation failed. " + ex.getMessage());
                }
            }
            
        };
        this.getCachedThreadPoolExecutorService().submit(saveG02EmployeeTask);
    }

}
