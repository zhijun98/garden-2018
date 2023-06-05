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

import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.controls.PeonyButtonTableCell;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.management.events.PeonyEmployeeSaved;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.management.EmployeeDataEntryTopComponent;
import com.zcomapproach.garden.peony.management.EmployeeWorkLogsTopComponent;
import com.zcomapproach.garden.peony.management.dialogs.PeonyEmployeePrivilegeDialog;
import com.zcomapproach.garden.peony.management.events.PeonyEmployeeDeleted;
import com.zcomapproach.garden.peony.security.PeonyPrivilege;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.view.controllers.PeonyPersonalProfileController;
import com.zcomapproach.garden.persistence.entity.G02Employee;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.persistence.peony.PeonyEmployeeList;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.garden.util.GardenFaceX;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.controlsfx.glyphfont.FontAwesome;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;

/**
 *
 * @author zhijun98
 */
public class PeonyManagementPanelController extends PeonyManagementServiceController implements PeonyFaceEventListener{
    @FXML
    private TabPane managementTabPane;
    @FXML
    private Tab myAccountTab;
    @FXML
    private Tab employeeListTab;
    @FXML
    private VBox employeeListContentbox;
    @FXML
    private Label employeesLabel;
    @FXML
    private TableView<PeonyEmployee> employeesTableView;
    @FXML
    private Button addNewEmployeeButton;

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof PeonyEmployeeSaved){
            handlePeonyEmployeeSaved((PeonyEmployeeSaved)event);
        }
    }

    private void handlePeonyEmployeeSaved(final PeonyEmployeeSaved event) {
        if (!event.isNewEntity()){
            return;
        }
        if (Platform.isFxApplicationThread()){
            handlePeonyEmployeeSavedHelper(event);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    handlePeonyEmployeeSavedHelper(event);
                }
            });
        }
    }
    private void handlePeonyEmployeeSavedHelper(final PeonyEmployeeSaved event) {
        if (event.isNewEntity()){
            employeesTableView.getItems().add(0, event.getPeonyEmployee());
            employeesTableView.refresh();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        initializeMyAccountTab();
        
        //employeeListTab
        if (PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.MANAGE_EMPLOYEE_PROFILES)){
            initializeEmployeeListTab();
        }else{
            managementTabPane.getTabs().remove(employeeListTab);
        }
    }
    
    private void initializeEmployeeListTab() {
        
        employeesLabel.setGraphic(PeonyGraphic.getImageView("employees32.png"));
        
        employeesTableView.prefHeightProperty().bind(employeeListContentbox.heightProperty().add(-50));
        
        employeesTableView.getColumns().clear();
        
        TableColumn<PeonyEmployee, String> workTitleColumn = new TableColumn<>("Work Title");
        workTitleColumn.setCellValueFactory(new PropertyValueFactory<>("employeeWorkTitle"));
        employeesTableView.getColumns().add(workTitleColumn);
        
        TableColumn<PeonyEmployee, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameColumn.setPrefWidth(80.00);
        employeesTableView.getColumns().add(firstNameColumn);
        
        TableColumn<PeonyEmployee, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastNameColumn.setPrefWidth(80.00);
        employeesTableView.getColumns().add(lastNameColumn);
        
        TableColumn<PeonyEmployee, String> workStatusColumn = new TableColumn<>("Work Status");
        workStatusColumn.setCellValueFactory(new PropertyValueFactory<>("employmentStatus"));
        employeesTableView.getColumns().add(workStatusColumn);
        
        TableColumn<PeonyEmployee, String> emailColumn = new TableColumn<>("Work Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("workEmail"));
        emailColumn.setPrefWidth(200.00);
        employeesTableView.getColumns().add(emailColumn);
        
//        TableColumn<PeonyEmployee, String> phoneColumn = new TableColumn<>("Work Phone");
//        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("workPhone"));
//        employeesTableView.getColumns().add(phoneColumn);
        
        TableColumn<PeonyEmployee, String> loginNameColumn = new TableColumn<>("Login Name");
        loginNameColumn.setCellValueFactory(new PropertyValueFactory<>("loginName"));
        loginNameColumn.setPrefWidth(200.00);
        employeesTableView.getColumns().add(loginNameColumn);
        
        TableColumn<PeonyEmployee, Date> employedDateColumn = new TableColumn<>("Employed");
        employedDateColumn.setCellValueFactory(new PropertyValueFactory<>("employedDate"));
        employedDateColumn.setCellFactory(column -> {
            SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
            TableCell<PeonyEmployee, Date> aDateTableCell = new TableCell<PeonyEmployee, Date>(){
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if((item == null)||(empty)) {
                        setText(null);
                    }
                    else {
                        this.setText(format.format(item));
                    }
                }
            };
            return aDateTableCell;
        });
        employeesTableView.getColumns().add(employedDateColumn);
        
        if (PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.CONTROL_DAILY_REPORT)){
            TableColumn dailyReportEmployeeWorkStatusColumn = new TableColumn("");
            dailyReportEmployeeWorkStatusColumn.setCellFactory(PeonyButtonTableCell.<PeonyEmployee>callbackForTableColumn(
                    "Daily Report", FontAwesome.Glyph.TABLE, Color.CRIMSON , new Tooltip("View this employee's daily job report"), 
                    (PeonyEmployee aPeonyEmployee) -> {
                        Lookup.getDefault().lookup(PeonyManagementService.class).launchEmployeeDailyReportTopComponent(aPeonyEmployee);
                        return aPeonyEmployee;
                    }));
            dailyReportEmployeeWorkStatusColumn.setPrefWidth(125.00);
            employeesTableView.getColumns().add(dailyReportEmployeeWorkStatusColumn);
        }
        
        if (PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.VIEW_EMPLOYEE_WORK_LOGS)){
            TableColumn viewEmployeeWorkStatusColumn = new TableColumn("");
            viewEmployeeWorkStatusColumn.setCellFactory(PeonyButtonTableCell.<PeonyEmployee>callbackForTableColumn(
                    "Work Logs", FontAwesome.Glyph.LIST, Color.DARKVIOLET , new Tooltip("View this employee's daily work status"), 
                    (PeonyEmployee aPeonyEmployee) -> {
                        displayEmployeeWorkLogsTopComponent(aPeonyEmployee);
                        return aPeonyEmployee;
                    }));
            viewEmployeeWorkStatusColumn.setPrefWidth(100.00);
            employeesTableView.getColumns().add(viewEmployeeWorkStatusColumn);
        }
        
        if (PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.MANAGE_EMPLOYEE_PRIVILEGES)){
            TableColumn editEmployeePrivilegeColumn = new TableColumn("");
            editEmployeePrivilegeColumn.setCellFactory(PeonyButtonTableCell.<PeonyEmployee>callbackForTableColumn(
                    "Privileges", FontAwesome.Glyph.CHECK_CIRCLE_ALT, Color.DARKGREEN , new Tooltip("Assign privileges to this employee"), 
                    (PeonyEmployee aPeonyEmployee) -> {
                        popupEmployeePrivilegeDialog(aPeonyEmployee);
                        return aPeonyEmployee;
                    }));
            editEmployeePrivilegeColumn.setPrefWidth(100.00);
            employeesTableView.getColumns().add(editEmployeePrivilegeColumn);
        }
        
        TableColumn editEmployeeColumn = new TableColumn("");
        editEmployeeColumn.setCellFactory(PeonyButtonTableCell.<PeonyEmployee>callbackForTableColumn(
                "Edit", FontAwesome.Glyph.PENCIL_SQUARE_ALT, Color.MEDIUMBLUE , new Tooltip("Edit this employee profile"), 
                (PeonyEmployee aPeonyEmployee) -> {
                    popupEmployeeDataEntryTopComponent(aPeonyEmployee);
                    return aPeonyEmployee;
                }));
        editEmployeeColumn.setPrefWidth(100.00);
        employeesTableView.getColumns().add(editEmployeeColumn);
        
        TableColumn deleteEmployeeColumn = new TableColumn("");
        deleteEmployeeColumn.setCellFactory(PeonyButtonTableCell.<PeonyEmployee>callbackForTableColumn(
                "Delete", FontAwesome.Glyph.TIMES, Color.BROWN , new Tooltip("Delete this employee profile"), 
                (PeonyEmployee aPeonyEmployee) -> {
                    deletePeonyEmployeeDataEntry(aPeonyEmployee);
                    return aPeonyEmployee;
                }));
        deleteEmployeeColumn.setPrefWidth(100.00);
        employeesTableView.getColumns().add(deleteEmployeeColumn);
        
        employeesTableView.getSelectionModel().setCellSelectionEnabled(true);
        employeesTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        GardenFaceX.installCopyPasteHandler(employeesTableView);
        
        addNewEmployeeButton.setGraphic(PeonyFaceUtils.createFontAwesomeNode(FontAwesome.Glyph.PLUS, Color.BLUE));
        addNewEmployeeButton.setOnAction((ActionEvent event) -> {
            PeonyEmployee aPeonyEmployee = new PeonyEmployee();
            aPeonyEmployee.assignEntityUuid();
            popupEmployeeDataEntryTopComponent(aPeonyEmployee);
        });
        
        loadEmployees();
    }

    private void popupEmployeePrivilegeDialog(final PeonyEmployee aPeonyEmployee) {
        if (SwingUtilities.isEventDispatchThread()){
            popupEmployeePrivilegeDialogHelper(aPeonyEmployee);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    popupEmployeePrivilegeDialogHelper(aPeonyEmployee);
                }
            });
        }
    }
    
    private void popupEmployeePrivilegeDialogHelper(final PeonyEmployee aPeonyEmployee) {
        PeonyEmployeePrivilegeDialog aPeonyEmployeePrivilegeDialog = new PeonyEmployeePrivilegeDialog(PeonyLauncher.mainFrame, true);
        aPeonyEmployeePrivilegeDialog.launchEmployeePrivilegeDialog("Employee Privileges: " 
                + aPeonyEmployee.getPeonyUserFullName(), aPeonyEmployee);
    }

    public void displayEmployeeWorkLogsTopComponent(final PeonyEmployee aPeonyEmployee) {
        if (SwingUtilities.isEventDispatchThread()){
            displayEmployeeWorkLogsTopComponentHelper(aPeonyEmployee);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    displayEmployeeWorkLogsTopComponentHelper(aPeonyEmployee);
                }
            });
        }
    }
    
    private void displayEmployeeWorkLogsTopComponentHelper(final PeonyEmployee aPeonyEmployee) {
        EmployeeWorkLogsTopComponent aEmployeeWorkLogsTopComponent = new EmployeeWorkLogsTopComponent();
        aEmployeeWorkLogsTopComponent.launchEmployeeWorkLogsTopComponent(aPeonyEmployee);
        this.addPeonyFaceEventListener(aEmployeeWorkLogsTopComponent);
        aEmployeeWorkLogsTopComponent.open();
        aEmployeeWorkLogsTopComponent.requestActive();
    }

    public void popupEmployeeDataEntryTopComponent(final PeonyEmployee aPeonyEmployee) {
        if (SwingUtilities.isEventDispatchThread()){
            popupEmployeeDataEntryTopComponentHelper(aPeonyEmployee);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    popupEmployeeDataEntryTopComponentHelper(aPeonyEmployee);
                }
            });
        }
    }
    
    private void popupEmployeeDataEntryTopComponentHelper(final PeonyEmployee aPeonyEmployee) {
        EmployeeDataEntryTopComponent aEmployeeDataEntryTopComponent = new EmployeeDataEntryTopComponent();
        aEmployeeDataEntryTopComponent.launchEmployeeDataEntryTopComponent(aPeonyEmployee);
        this.addPeonyFaceEventListener(aEmployeeDataEntryTopComponent);
        aEmployeeDataEntryTopComponent.open();
        aEmployeeDataEntryTopComponent.requestActive();
    }

    private void deletePeonyEmployeeDataEntry(final PeonyEmployee aPeonyEmployee) {
        if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to delete this employee?") != JOptionPane.YES_OPTION){
            return;
        }
        
        Task<PeonyEmployee> deletePeonyEmployeeTask = new Task<PeonyEmployee>(){
            @Override
            protected PeonyEmployee call() throws Exception {
                G02Employee aG02Employee = Lookup.getDefault().lookup(PeonyManagementService.class).getPeonyManagementRestClient().deleteEntity_XML(G02Employee.class, 
                        GardenRestParams.Management.deleteEmployeeRestParams(aPeonyEmployee.getEmployeeInfo().getEmployeeAccountUuid()));
                if (aG02Employee == null){
                    updateMessage("Cannot delete the employee record right.");
                    return null;
                }else{
                    broadcastPeonyFaceEventHappened(new PeonyEmployeeDeleted(aPeonyEmployee));
                    return aPeonyEmployee;
                }
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyEmployee aPeonyEmployee = get();
                    if (aPeonyEmployee == null){
                        PeonyFaceUtils.displayErrorMessageDialog("This operation failed. " + getMessage());
                    }else{
                        employeesTableView.getItems().remove(aPeonyEmployee);
                        employeesTableView.refresh();
                        PeonyFaceUtils.displayInformationMessageDialog("This employee "+aPeonyEmployee.getPeonyUserFullName()+" is deleted. ");
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception raised. " + ex.getMessage());
                }
            }
        };
        this.getCachedThreadPoolExecutorService().submit(deletePeonyEmployeeTask);
    }

    private void loadEmployees() {
        Task<PeonyEmployeeList> loadEmployeesTask = new Task<PeonyEmployeeList>(){
            @Override
            protected PeonyEmployeeList call() throws Exception {
                return Lookup.getDefault().lookup(PeonyManagementService.class).retrievePeonyEmployeeList();
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyEmployeeList result = get();
                    employeesTableView.setItems(FXCollections.observableArrayList(result.getPeonyEmployeeList()));
                    employeesTableView.refresh();
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception raised. " + ex.getMessage());
                }
            }
        };
        this.getCachedThreadPoolExecutorService().submit(loadEmployeesTask);
    }

    private void initializeMyAccountTab() {
        try {
            PeonyPersonalProfileController aPeonyAccountProfileController = new PeonyPersonalProfileController(PeonyProperties.getSingleton().getCurrentLoginEmployee());
            myAccountTab.setContent(aPeonyAccountProfileController.loadFxml());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
