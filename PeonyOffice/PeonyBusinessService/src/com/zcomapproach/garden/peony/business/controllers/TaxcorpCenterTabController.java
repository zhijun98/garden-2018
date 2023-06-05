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
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxcorpService;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.CalendarPeriodSelected;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.peony.TaxcorpCaseBriefList;
import com.zcomapproach.garden.persistence.peony.TaxcorpCaseBrief;
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
public class TaxcorpCenterTabController extends PeonyBusinessServiceController implements PeonyFaceEventListener{
    @FXML
    private Label billTotalLabel;
    @FXML
    private Label paymentTotalLabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private Button selectPeriodButton;
    @FXML
    private TableView<TaxcorpCaseBrief> taxcorpTableView;

    private Date fromDate;
    private Date toDate;
    
    public TaxcorpCenterTabController() {
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
        
        taxcorpTableView.getColumns().clear();
        
        TableColumn<TaxcorpCaseBrief, String> firstNameColumn = new TableColumn<>("Corporate");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("corporateName"));
        firstNameColumn.setPrefWidth(150.00);
        taxcorpTableView.getColumns().add(firstNameColumn);
        
        TableColumn<TaxcorpCaseBrief, String> einColumn = new TableColumn<>("EIN");
        einColumn.setCellValueFactory(new PropertyValueFactory<>("einNumber"));
        einColumn.setPrefWidth(80.00);
        taxcorpTableView.getColumns().add(einColumn);
        
        TableColumn<TaxcorpCaseBrief, String> lastNameColumn = new TableColumn<>("Type");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("businessType"));
        lastNameColumn.setPrefWidth(80.00);
        taxcorpTableView.getColumns().add(lastNameColumn);
        
        TableColumn<TaxcorpCaseBrief, String> federalFilingStatusColumn = new TableColumn<>("Corp. Email");
        federalFilingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("corporateEmail"));
        federalFilingStatusColumn.setPrefWidth(150.00);
        taxcorpTableView.getColumns().add(federalFilingStatusColumn);
        
        TableColumn<TaxcorpCaseBrief, String> deadlineColumn = new TableColumn<>("Corp. Phone");
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("corporatePhone"));
        deadlineColumn.setPrefWidth(150.00);
        taxcorpTableView.getColumns().add(deadlineColumn);
        
        TableColumn<TaxcorpCaseBrief, String> billTotalColumn = new TableColumn<>("Bill");
        billTotalColumn.setCellValueFactory(new PropertyValueFactory<>("billTotalText"));
        taxcorpTableView.getColumns().add(billTotalColumn);
        
        TableColumn<TaxcorpCaseBrief, String> paymentTotalColumn = new TableColumn<>("Payment");
        paymentTotalColumn.setCellValueFactory(new PropertyValueFactory<>("paymentTotalText"));
        taxcorpTableView.getColumns().add(paymentTotalColumn);
        
        TableColumn<TaxcorpCaseBrief, String> balanceTotalColumn = new TableColumn<>("Balance");
        balanceTotalColumn.setCellValueFactory(new PropertyValueFactory<>("balanceTotalText"));
        taxcorpTableView.getColumns().add(balanceTotalColumn);
        
        TableColumn viewColumn = new TableColumn("");
        viewColumn.setCellFactory(PeonyButtonTableCell.<TaxcorpCaseBrief>callbackForTableColumn(
                "View", FontAwesome.Glyph.PENCIL_SQUARE_ALT, Color.MEDIUMBLUE , new Tooltip("Edit this taxcorp profile"), 
                (TaxcorpCaseBrief aTaxcorpCaseBrief) -> {
                    Lookup.getDefault().lookup(PeonyTaxcorpService.class).launchPeonyTaxcorpCaseTopComponentByTaxcorpCaseUuid(aTaxcorpCaseBrief.getTaxcorpCaseUuid());
                    return aTaxcorpCaseBrief;
                }));
        viewColumn.setPrefWidth(100.00);
        taxcorpTableView.getColumns().add(viewColumn);
        
        billTotalLabel.setText("$0.0");
        paymentTotalLabel.setText("$0.0");
        balanceLabel.setText("$0.0");
        
        loadTaxcorpCaseBriefs();
        
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
        loadTaxcorpCaseBriefs();
    }

    private void loadTaxcorpCaseBriefs() {
        Task<TaxcorpCaseBriefList> loadTaxcorpCaseBriefsTask = new Task<TaxcorpCaseBriefList>(){
            @Override
            protected TaxcorpCaseBriefList call() throws Exception {
                try {
                    return Lookup.getDefault().lookup(PeonyTaxcorpService.class).getPeonyTaxcorpRestClient().findEntity_XML(TaxcorpCaseBriefList.class,
                            GardenRestParams.Taxcorp.findTaxcorpCaseBriefListByBillPeriodRestParams(String.valueOf(fromDate.getTime()), String.valueOf(toDate.getTime())));
                } catch (Exception ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Cannot find the taxcorp case!");
                }
                return null;
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayErrorMessageDialog("Failed to retrieve TaxcorpCaseBriefList because of technical errors. " + getMessage());
            }

            @Override
            protected void succeeded() {
                try {
                    TaxcorpCaseBriefList result = get();
                    if (result != null){
                        billTotalLabel.setText(result.getSumBillTotalText());
                        paymentTotalLabel.setText(result.getSumPaymentTotalText());
                        balanceLabel.setText(result.getSumBalanceTotalText());
                        
                        taxcorpTableView.setItems(FXCollections.observableArrayList(result.getTaxcorpCaseBriefList()));
                        taxcorpTableView.refresh();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception raised. " + ex.getMessage());
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(loadTaxcorpCaseBriefsTask);
    }
}
