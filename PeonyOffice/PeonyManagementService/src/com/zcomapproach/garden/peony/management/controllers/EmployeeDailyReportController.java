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
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxcorpService;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import com.zcomapproach.garden.peony.management.data.DailyReportGroupType;
import com.zcomapproach.garden.peony.security.PeonyPrivilege;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.PeonyDailyReportSaved;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.peony.PeonyDailyReport;
import com.zcomapproach.garden.persistence.peony.PeonyDailyReportList;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.persistence.peony.PeonyEmployeeList;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.garden.util.GardenSorter;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.util.Callback;
import javax.swing.JOptionPane;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class EmployeeDailyReportController extends PeonyManagementServiceController implements PeonyFaceEventListener{
    
    @FXML
    private Label myDailyReportLabel;
    @FXML
    private ComboBox<PeonyEmployeeItem> employeesComboBox;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private ComboBox<String> groupTypeComboBox;
    @FXML
    private TreeTableView<PeonyDailyReport> dailyReportSummaryTreeTableView;

    private PeonyEmployee targetPeonyEmployee;
    
    private final String StartingRootTitle = "Daily Reports from ";
    private final String EndingHrs = " hrs.)";
    
    public EmployeeDailyReportController(PeonyEmployee targetPeonyEmployee) {
        this.targetPeonyEmployee = targetPeonyEmployee;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        GregorianCalendar today = new GregorianCalendar();
        today.set(Calendar.DAY_OF_MONTH, 1);
        fromDatePicker.setValue(ZcaCalendar.convertToLocalDate(ZcaCalendar.setTimePart(today.getTime(), 0, 0, 0)));
        fromDatePicker.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
                if (fromDatePicker.getValue().isAfter(toDatePicker.getValue())){
                    return;
                }
                if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Refresh for the new period?" ) == JOptionPane.YES_OPTION){
                    refreshEmployeeDailyReport(targetPeonyEmployee);
                }
            }
        });
        toDatePicker.setValue(ZcaCalendar.convertToLocalDate(ZcaCalendar.setTimePart(new Date(), 23, 59, 59)));
        toDatePicker.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
                if (fromDatePicker.getValue().isAfter(toDatePicker.getValue())){
                    return;
                }
                if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Refresh daily reports for the new selected period?" ) == JOptionPane.YES_OPTION){
                    refreshEmployeeDailyReport(targetPeonyEmployee);
                }
            }
        });
        
        PeonyFaceUtils.initializeComboBox(groupTypeComboBox, DailyReportGroupType.getEnumValueList(false), 
                DailyReportGroupType.GROUP_BY_DATE.value(), DailyReportGroupType.GROUP_BY_DATE.value(), 
                "Display daily reports grouped by the selected type.", false, this);
        groupTypeComboBox.setOnAction(evt -> {
            if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Refresh daily reports based on new type?" ) == JOptionPane.YES_OPTION){
                refreshEmployeeDailyReport(targetPeonyEmployee);
            }
        });
        
        employeesComboBox.setVisible(false);
        employeesComboBox.setTooltip(new Tooltip("Select a specific employee to show his/her daily reports."));
        if (PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.CONTROL_DAILY_REPORT)){
            this.getCachedThreadPoolExecutorService().submit(new Task<List<PeonyEmployeeItem>>(){
                @Override
                protected List<PeonyEmployeeItem> call() throws Exception {
                    PeonyEmployeeList aPeonyEmployeeList = Lookup.getDefault().lookup(PeonyManagementService.class).retrievePeonyEmployeeList();
                    List<PeonyEmployeeItem> result = null;
                    if (aPeonyEmployeeList != null){
                        List<PeonyEmployee> employees = aPeonyEmployeeList.getPeonyEmployeeList();
                        if (employees != null){
                            GardenSorter.sortPeonyEmployeeListByFullName(employees, false);
                            result = new ArrayList<>();
                            for (PeonyEmployee employee : employees){
                                result.add(new PeonyEmployeeItem(employee));
                            }
                        }
                    }
                    return result;
                }

                @Override
                protected void failed() {
                    PeonyFaceUtils.publishMessageOntoOutputWindow("Failed to display employee list due to technical reasons. " + getMessage());
                }

                @Override
                protected void succeeded() {
                    try {
                        List<PeonyEmployeeItem> result = get();
                        if (result == null){
                            PeonyFaceUtils.publishMessageOntoOutputWindow("Failed to display employee list because no records were found." + getMessage());
                        }else{
                            employeesComboBox.getItems().clear();
                            employeesComboBox.getItems().addAll(result);
                            employeesComboBox.setVisible(true);
                            
                            employeesComboBox.setOnAction(event -> {
                                employeesComboBox.setDisable(true);
                                PeonyEmployeeItem aPeonyEmployeeItem = employeesComboBox.getValue();
                                refreshEmployeeDailyReport(aPeonyEmployeeItem.getPeonyEmployee());
                            });
                            
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        //Exceptions.printStackTrace(ex);
                        PeonyFaceUtils.publishMessageOntoOutputWindow("InterruptedException or ExecutionException: " + getMessage());
                    }
                }
            });
        }
        
        initializeDailyReportSummaryTreeTableView();
        
        refreshEmployeeDailyReport(targetPeonyEmployee);
        
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof PeonyDailyReportSaved){
            refreshEmployeeDailyReport(targetPeonyEmployee);
        }
    }
    
    private boolean isValidReportDate(PeonyDailyReport aPeonyDailyReport){
        if (PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.CONTROL_DAILY_REPORT)){
            return true;
        }
        
        //reportDate
        String reportDateText = aPeonyDailyReport.getDailyReport().getG02DailyReportPK().getReportDate();
        if (ZcaValidator.isNullEmpty(reportDateText)){
            PeonyFaceUtils.publishMessageOntoOutputWindow("Tech error: daily report date has bad format.");
            return false;
        }
        reportDateText = reportDateText.replaceAll("-", "").trim();
        Date date = ZcaCalendar.parseMMddyyyyDate(reportDateText);
        if (date == null){
            PeonyFaceUtils.publishMessageOntoOutputWindow("Tech error: cannot parse out the date for daily report.");
            return false;
        }
        GregorianCalendar reportDate = new GregorianCalendar();
        reportDate.setTime(date);
        
        //today
        GregorianCalendar today = new GregorianCalendar();
        today.add(Calendar.DATE, -3);
        return today.before(reportDate);
    }
    
    private void initializeDailyReportSummaryTreeTableView(){
        dailyReportSummaryTreeTableView.getColumns().clear();
        
        TreeTableColumn<PeonyDailyReport, String> jobDescriptionColumn = new TreeTableColumn<>("Job");
        jobDescriptionColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("jobDescription"));
        jobDescriptionColumn.setPrefWidth(500.0);
        jobDescriptionColumn.setCellFactory(new Callback<TreeTableColumn<PeonyDailyReport, String>, TreeTableCell<PeonyDailyReport, String>>(){
            @Override
            public TreeTableCell<PeonyDailyReport, String> call(TreeTableColumn<PeonyDailyReport, String> param) {
                return new TreeTableCell<PeonyDailyReport, String>(){
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null){
                            if (item.startsWith(StartingRootTitle)){
                                setStyle("-fx-text-fill: #000088; -fx-font-size: 16; -fx-font-weight: bolder;");
                            }else if (item.endsWith(EndingHrs)){
                                setStyle("-fx-text-fill: #0000e8; -fx-font-size: 15; -fx-font-weight: bold;");
                            }
                        }
                        setText(item);
                    }
                };
            }
        });
        dailyReportSummaryTreeTableView.getColumns().add(jobDescriptionColumn);
        
        TreeTableColumn<PeonyDailyReport, String> reportDateColumn = new TreeTableColumn<>("Report Date");
        reportDateColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("jobReportDate"));
        reportDateColumn.setPrefWidth(100.0);
        dailyReportSummaryTreeTableView.getColumns().add(reportDateColumn);
        
        TreeTableColumn<PeonyDailyReport, String> jobWorkingLocationColumn = new TreeTableColumn<>("Location");
        jobWorkingLocationColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("jobWorkingLocation"));
        jobWorkingLocationColumn.setPrefWidth(100.0);
        dailyReportSummaryTreeTableView.getColumns().add(jobWorkingLocationColumn);
        
        TreeTableColumn<PeonyDailyReport, String> jobWorkingHoursColumn = new TreeTableColumn<>("Spent-Time");
        jobWorkingHoursColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("jobWorkingHours"));
        jobWorkingHoursColumn.setPrefWidth(100.0);
        dailyReportSummaryTreeTableView.getColumns().add(jobWorkingHoursColumn);
        
        TreeTableColumn<PeonyDailyReport, String> jobWorkingProgressColumn = new TreeTableColumn<>("Progress");
        jobWorkingProgressColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("jobWorkingProgress"));
        jobWorkingProgressColumn.setPrefWidth(100.0);
        dailyReportSummaryTreeTableView.getColumns().add(jobWorkingProgressColumn);
        
        TreeTableColumn<PeonyDailyReport, String> jobWorkingDescriptionColumn = new TreeTableColumn<>("Description");
        jobWorkingDescriptionColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("jobWorkingDescription"));
        jobWorkingDescriptionColumn.setPrefWidth(300.0);
        dailyReportSummaryTreeTableView.getColumns().add(jobWorkingDescriptionColumn);
        
        dailyReportSummaryTreeTableView.setRowFactory(new Callback<TreeTableView<PeonyDailyReport>, TreeTableRow<PeonyDailyReport>>(){
            @Override
            public TreeTableRow<PeonyDailyReport> call(TreeTableView<PeonyDailyReport> param) {
                final ContextMenu contextMenu = new ContextMenu();  
                final MenuItem editMenuItem = new MenuItem("Edit "); 
                final MenuItem detailsMenuItem = new MenuItem("Details ");
                contextMenu.getItems().add(editMenuItem);
                contextMenu.getItems().add(detailsMenuItem);
                
                final TreeTableRow<PeonyDailyReport> row = new TreeTableRow<PeonyDailyReport>(){
                    @Override
                    protected void updateItem(PeonyDailyReport item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty){
                            setContextMenu(null);
                        } else {
                            setContextMenu(contextMenu);
                        }
                    }
                };
                editMenuItem.setOnAction(evt -> {
                    PeonyDailyReport aPeonyDailyReport = row.getItem();
                    if (aPeonyDailyReport instanceof PeonyDailyReportDataItem){
                        return;
                    }
                    if (isValidReportDate(aPeonyDailyReport)){
                        List<PeonyFaceEventListener> listeners = new ArrayList<>();
                        listeners.add(EmployeeDailyReportController.this);
                        Lookup.getDefault().lookup(PeonyManagementService.class).displayPeonyDailyReportEditor(aPeonyDailyReport, listeners);
                    }else{
                        PeonyFaceUtils.displayErrorMessageDialog("You can only edit today's report. ");
                    }
                });
                detailsMenuItem.setOnAction(evt -> {
                    PeonyDailyReport aPeonyDailyReport = row.getItem();
                    if (aPeonyDailyReport instanceof PeonyDailyReportDataItem){
                        return;
                    }
                    if (aPeonyDailyReport != null){
                        if (GardenEntityType.TAXPAYER_CASE.name().equalsIgnoreCase(aPeonyDailyReport.getJobEntityType())){
                            Lookup.getDefault().lookup(PeonyTaxpayerService.class).launchPeonyTaxpayerCaseTopComponentByTaxpayerCaseUuid(aPeonyDailyReport.getJobEntityUuid());
                        }else if (GardenEntityType.TAXCORP_CASE.name().equalsIgnoreCase(aPeonyDailyReport.getJobEntityType())){
                            Lookup.getDefault().lookup(PeonyTaxcorpService.class).launchPeonyTaxcorpCaseTopComponentByTaxcorpCaseUuid(aPeonyDailyReport.getJobEntityUuid());
                        }
                    }
                });
                return row ;  
            }
        });
    }
    
    public void refreshEmployeeDailyReport(final PeonyEmployee targetPeonyEmployee) {
        if (Platform.isFxApplicationThread()){
            refreshEmployeeDailyReportHelper(targetPeonyEmployee);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    refreshEmployeeDailyReportHelper(targetPeonyEmployee);
                }
            });
        }
    }
    private void refreshEmployeeDailyReportHelper(final PeonyEmployee targetPeonyEmployee) {
        this.targetPeonyEmployee = targetPeonyEmployee;
        Task<TreeItem<PeonyDailyReport>> task;
        if (DailyReportGroupType.GROUP_BY_JOB.value().equalsIgnoreCase(groupTypeComboBox.getValue())){
            task = new PeonyDailyReportByJob();
        }else{
            task = new PeonyDailyReportByDate();
        }
        this.getCachedThreadPoolExecutorService().submit(task);
    }

    private abstract class PeonyDailyReportTask extends Task<TreeItem<PeonyDailyReport>> {
        
        protected abstract LinkedHashMap<String, List<PeonyDailyReport>> constructGroupedDailyReports(List<PeonyDailyReport> reports);
        
        @Override
        protected TreeItem<PeonyDailyReport> call() throws Exception {
            Date fromDate = ZcaCalendar.convertToDate(fromDatePicker.getValue());
            Date toDate = ZcaCalendar.convertToDate(toDatePicker.getValue());
            if ((fromDate == null) || (toDate == null)){
                updateMessage("Please select a date-range so as to display daily report summary.");
                return null;
            }
            PeonyDailyReportList reportList = Lookup.getDefault().lookup(PeonyManagementService.class)
                    .getPeonyManagementRestClient().findEntity_XML(PeonyDailyReportList.class, 
                            GardenRestParams.Management.findPeonyDailyReportListForEmployeeByPeriodRestParams(
                                    targetPeonyEmployee.getEmployeeInfo().getEmployeeAccountUuid(), 
                                    String.valueOf(fromDate.getTime()), String.valueOf(ZcaCalendar.addDates(toDate, 1).getTime())));
            List<PeonyDailyReport> reports = reportList.getPeonyDailyReportList();
            if ((reports == null) || (reports.isEmpty())){
                updateMessage("No records found to display daily report summary.");
                return null;
            }

            PeonyDailyReportDataItem rootPeonyDailyReportDataItem = new PeonyDailyReportDataItem(StartingRootTitle
                    + ZcaCalendar.convertToMMddyy(fromDate, "-") + " to " + ZcaCalendar.convertToMMddyy(toDate, "-"),
                    "", "", "", "", "");
            TreeItem<PeonyDailyReport> root = new TreeItem(rootPeonyDailyReportDataItem);
            root.setExpanded(true);
            //organized data
            Collections.sort(reports, new Comparator<PeonyDailyReport>(){
                @Override
                public int compare(PeonyDailyReport o1, PeonyDailyReport o2) {
                    return o1.getDailyReport().getCreated().compareTo(o2.getDailyReport().getCreated())*(-1);
                }
            });
            
            LinkedHashMap<String, List<PeonyDailyReport>> groupedDailyReports = constructGroupedDailyReports(reports);
            
            //build root
            HashMap<String, TreeItem<PeonyDailyReport>> reportDates = new HashMap<>();
            List<PeonyDailyReport> aPeonyDailyReportList;
            Set<String> keys = groupedDailyReports.keySet();
            Iterator<String> itr = keys.iterator();
            String key;
            TreeItem<PeonyDailyReport> reportDateItem;
            double periodTotalHours = 0;
            while (itr.hasNext()){
                key = itr.next();
                aPeonyDailyReportList = groupedDailyReports.get(key);
                if (aPeonyDailyReportList != null){
                    Collection<TreeItem<PeonyDailyReport>> peonyDailyReportTreeItems = new ArrayList<>();
                    double totalHours = 0;
                    for (PeonyDailyReport aPeonyDailyReport : aPeonyDailyReportList){
                        totalHours += aPeonyDailyReport.getDailyReport().getWorkingHours();
                        peonyDailyReportTreeItems.add(new TreeItem(aPeonyDailyReport));
                    }
                
                    reportDateItem = reportDates.get(key);
                    if (reportDateItem == null){
                        
                        periodTotalHours += totalHours;
                        
                        reportDateItem = new TreeItem(new PeonyDailyReportDataItem(key + " (Total: "+totalHours+EndingHrs, "", "", "", "", ""));
                        reportDateItem.setExpanded(true);
                        reportDates.put(key, reportDateItem);
                        root.getChildren().add(reportDateItem);
                    }
                    reportDateItem.getChildren().addAll(peonyDailyReportTreeItems);
                }
            }
            
            rootPeonyDailyReportDataItem.setJobDescription(rootPeonyDailyReportDataItem.getJobDescription() + " (Total: "+periodTotalHours+EndingHrs);
            return root;
        }

        @Override
        protected void failed() {
            PeonyFaceUtils.publishMessageOntoOutputWindow("Failed to display daily report summary. " + getMessage());
        }

        @Override
        protected void succeeded() {
            try {
                myDailyReportLabel.setText("Daily Report: " + targetPeonyEmployee.getPeonyUserFullName());
                dailyReportSummaryTreeTableView.setRoot(get());
                dailyReportSummaryTreeTableView.refresh();
                if (employeesComboBox.isVisible()){
                    employeesComboBox.setDisable(false);
                }
            } catch (InterruptedException | ExecutionException ex) {
                //Exceptions.printStackTrace(ex);
                PeonyFaceUtils.publishMessageOntoOutputWindow("InterruptedException or ExecutionException: " + getMessage());
            }
        }
    }

    private class PeonyDailyReportByDate extends PeonyDailyReportTask {

        @Override
        protected LinkedHashMap<String, List<PeonyDailyReport>> constructGroupedDailyReports(List<PeonyDailyReport> reports) {
            LinkedHashMap<String, List<PeonyDailyReport>> groupedReports = new LinkedHashMap<>();
            List<PeonyDailyReport> aPeonyDailyReportList;
            String reportDate;
            for (PeonyDailyReport report : reports){
                reportDate = report.getDailyReport().getG02DailyReportPK().getReportDate();
                aPeonyDailyReportList = groupedReports.get(reportDate);
                if (aPeonyDailyReportList == null){
                    aPeonyDailyReportList = new ArrayList<>();
                    groupedReports.put(reportDate, aPeonyDailyReportList);
                }
                aPeonyDailyReportList.add(report);
            }
            return groupedReports;
        }
    }

    private class PeonyDailyReportByJob extends PeonyDailyReportTask {

        @Override
        protected LinkedHashMap<String, List<PeonyDailyReport>> constructGroupedDailyReports(List<PeonyDailyReport> reports) {
            LinkedHashMap<String, List<PeonyDailyReport>> groupedReports = new LinkedHashMap<>();
            List<PeonyDailyReport> aPeonyDailyReportList;
            String reportJob;
            for (PeonyDailyReport report : reports){
                reportJob = report.getJobDescription();
                aPeonyDailyReportList = groupedReports.get(reportJob);
                if (aPeonyDailyReportList == null){
                    aPeonyDailyReportList = new ArrayList<>();
                    groupedReports.put(reportJob, aPeonyDailyReportList);
                }
                aPeonyDailyReportList.add(report);
            }
            return groupedReports;
        }
    }
    
    public class PeonyDailyReportDataItem extends PeonyDailyReport{
    
        private String jobDescription;
        private String jobReportDate;
        private String jobWorkingLocation;
        private String jobWorkingHours;
        private String jobWorkingProgress;
        private String jobWorkingDescription;

        public PeonyDailyReportDataItem(String jobDescription, String jobReportDate, String jobWorkingLocation, String jobWorkingHours, String jobWorkingProgress, String jobWorkingDescription) {
            this.jobDescription = jobDescription;
            this.jobReportDate = jobReportDate;
            this.jobWorkingLocation = jobWorkingLocation;
            this.jobWorkingHours = jobWorkingHours;
            this.jobWorkingProgress = jobWorkingProgress;
            this.jobWorkingDescription = jobWorkingDescription;
        }

        @Override
        public String getJobDescription() {
            return jobDescription;
        }

        public void setJobDescription(String jobDescription) {
            this.jobDescription = jobDescription;
        }

        @Override
        public String getJobReportDate() {
            return jobReportDate;
        }

        public void setJobReportDate(String jobReportDate) {
            this.jobReportDate = jobReportDate;
        }

        @Override
        public String getJobWorkingLocation() {
            return jobWorkingLocation;
        }

        public void setJobWorkingLocation(String jobWorkingLocation) {
            this.jobWorkingLocation = jobWorkingLocation;
        }

        @Override
        public String getJobWorkingHours() {
            return jobWorkingHours;
        }

        public void setJobWorkingHours(String jobWorkingHours) {
            this.jobWorkingHours = jobWorkingHours;
        }

        @Override
        public String getJobWorkingProgress() {
            return jobWorkingProgress;
        }

        public void setJobWorkingProgress(String jobWorkingProgress) {
            this.jobWorkingProgress = jobWorkingProgress;
        }

        @Override
        public String getJobWorkingDescription() {
            return jobWorkingDescription;
        }

        public void setJobWorkingDescription(String jobWorkingDescription) {
            this.jobWorkingDescription = jobWorkingDescription;
        }
        
    }

    private class PeonyEmployeeItem {
        
        private final PeonyEmployee peonyEmployee;

        public PeonyEmployeeItem(PeonyEmployee peonyEmployee) {
            this.peonyEmployee = peonyEmployee;
        }

        public PeonyEmployee getPeonyEmployee() {
            return peonyEmployee;
        }

        @Override
        public String toString() {
            return peonyEmployee.getPeonyUserFullName();
        }
    
    }
    
}
