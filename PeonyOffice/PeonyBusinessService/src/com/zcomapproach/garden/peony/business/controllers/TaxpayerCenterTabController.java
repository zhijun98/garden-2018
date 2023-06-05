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

import com.zcomapproach.garden.peony.controls.PeonyButtonTableCell;
import com.zcomapproach.garden.peony.kernel.services.PeonyKernelService;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.CalendarPeriodSelected;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.peony.TaxpayerCaseBriefList;
import com.zcomapproach.garden.persistence.peony.TaxpayerCaseBrief;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.ZcaCalendar;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class TaxpayerCenterTabController extends PeonyBusinessServiceController implements PeonyFaceEventListener{
    @FXML
    private Label billTotalLabel;
    @FXML
    private Label paymentTotalLabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private Button selectPeriodButton;
    @FXML
    private TableView<TaxpayerCaseBrief> taxpayerTableView;

    private Date fromDate;
    private Date toDate;
    
    public TaxpayerCenterTabController() {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.MONTH, Calendar.JANUARY);
        gc.set(Calendar.DAY_OF_MONTH, 1);
        fromDate = ZcaCalendar.covertDateToEnding(gc.getTime());
        gc = new GregorianCalendar();
        gc.set(Calendar.MONTH, Calendar.DECEMBER);
        gc.set(Calendar.DAY_OF_MONTH, 31);
        toDate = ZcaCalendar.covertDateToEnding(gc.getTime());
        
        taxpayerTableView.getColumns().clear();
        
        TableColumn<TaxpayerCaseBrief, String> ssnColumn = new TableColumn<>("SSN");
        ssnColumn.setCellValueFactory(new PropertyValueFactory<>("customerSsn"));
        ssnColumn.setPrefWidth(80.00);
        taxpayerTableView.getColumns().add(ssnColumn);
        
        TableColumn<TaxpayerCaseBrief, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerFirstName"));
        firstNameColumn.setPrefWidth(80.00);
        taxpayerTableView.getColumns().add(firstNameColumn);
        
        TableColumn<TaxpayerCaseBrief, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerLastName"));
        lastNameColumn.setPrefWidth(80.00);
        taxpayerTableView.getColumns().add(lastNameColumn);
        
        TableColumn<TaxpayerCaseBrief, String> federalFilingStatusColumn = new TableColumn<>("Federal Filing Status");
        federalFilingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("federalFilingStatus"));
        federalFilingStatusColumn.setPrefWidth(150.00);
        taxpayerTableView.getColumns().add(federalFilingStatusColumn);
        
        TableColumn<TaxpayerCaseBrief, String> deadlineColumn = new TableColumn<>("Tax Return Deadline");
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadlineText"));
        federalFilingStatusColumn.setPrefWidth(150.00);
        taxpayerTableView.getColumns().add(deadlineColumn);
        
        TableColumn<TaxpayerCaseBrief, String> billTotalColumn = new TableColumn<>("Bill");
        billTotalColumn.setCellValueFactory(new PropertyValueFactory<>("billTotalText"));
        taxpayerTableView.getColumns().add(billTotalColumn);
        
        TableColumn<TaxpayerCaseBrief, String> paymentTotalColumn = new TableColumn<>("Payment");
        paymentTotalColumn.setCellValueFactory(new PropertyValueFactory<>("paymentTotalText"));
        taxpayerTableView.getColumns().add(paymentTotalColumn);
        
        TableColumn<TaxpayerCaseBrief, String> balanceTotalColumn = new TableColumn<>("Balance");
        balanceTotalColumn.setCellValueFactory(new PropertyValueFactory<>("balanceTotalText"));
        taxpayerTableView.getColumns().add(balanceTotalColumn);
        
        TableColumn viewColumn = new TableColumn("");
        viewColumn.setCellFactory(PeonyButtonTableCell.<TaxpayerCaseBrief>callbackForTableColumn(
                "View", FontAwesome.Glyph.PENCIL_SQUARE_ALT, Color.MEDIUMBLUE , new Tooltip("Edit this taxpayer profile"), 
                (TaxpayerCaseBrief aTaxpayerCaseBrief) -> {
                    Lookup.getDefault().lookup(PeonyTaxpayerService.class).launchPeonyTaxpayerCaseTopComponentByTaxpayerCaseUuid(aTaxpayerCaseBrief.getTaxpayerCaseUuid());
                    return aTaxpayerCaseBrief;
                }));
        viewColumn.setPrefWidth(100.00);
        taxpayerTableView.getColumns().add(viewColumn);
        
        billTotalLabel.setText("$0.0");
        paymentTotalLabel.setText("$0.0");
        balanceLabel.setText("$0.0");
        
        loadTaxpayerCaseBriefs();
        
        selectPeriodButton.setOnAction((ActionEvent event) -> {
            List<PeonyFaceEventListener> peonyFaceEventListeners = new ArrayList<>();
            peonyFaceEventListeners.add(this);
            Lookup.getDefault().lookup(PeonyKernelService.class).displayPeriodSelectionDialog("Select Period: ", peonyFaceEventListeners, fromDate, toDate);
        });
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
        loadTaxpayerCaseBriefs();
    }

    private void loadTaxpayerCaseBriefs() {
        Task<TaxpayerCaseBriefList> loadTaxpayerCaseBriefsTask = new Task<TaxpayerCaseBriefList>(){
            @Override
            protected TaxpayerCaseBriefList call() throws Exception {
                try {
                    return Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient().findEntity_XML(TaxpayerCaseBriefList.class,
                            GardenRestParams.Taxpayer.findTaxpayerCaseBriefListByPeriodRestParams(String.valueOf(fromDate.getTime()), String.valueOf(toDate.getTime())));
                } catch (Exception ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Cannot find the taxpayer case!");
                }
                return null;
            }

            @Override
            protected void succeeded() {
                try {
                    TaxpayerCaseBriefList result = get();
                    if (result != null){
                        billTotalLabel.setText(result.getSumBillTotalText());
                        paymentTotalLabel.setText(result.getSumPaymentTotalText());
                        balanceLabel.setText(result.getSumBalanceTotalText());
                        
                        taxpayerTableView.setItems(FXCollections.observableArrayList(result.getTaxpayerCaseBriefList()));
                        taxpayerTableView.refresh();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception raised. " + ex.getMessage());
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(loadTaxpayerCaseBriefsTask);
    }
}
