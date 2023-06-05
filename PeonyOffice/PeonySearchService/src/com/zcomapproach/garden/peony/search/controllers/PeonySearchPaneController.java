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

package com.zcomapproach.garden.peony.search.controllers;

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.data.constant.SearchTaxcorpCriteria;
import com.zcomapproach.garden.data.constant.SearchTaxpayerCriteria;
import com.zcomapproach.garden.data.constant.SearchUserCriteria;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxcorpService;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.data.constant.TaxcorpEntityDateField;
import com.zcomapproach.garden.data.constant.TaxpayerEntityDateField;
import com.zcomapproach.garden.peony.kernel.services.PeonyCustomerService;
import com.zcomapproach.garden.peony.kernel.services.PeonySearchService;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import com.zcomapproach.garden.persistence.constant.TaxFilingPeriod;
import com.zcomapproach.garden.persistence.constant.TaxFilingType;
import com.zcomapproach.garden.persistence.peony.data.CustomerSearchResultList;
import com.zcomapproach.garden.persistence.peony.data.TaxcorpCaseSearchResultList;
import com.zcomapproach.garden.persistence.peony.data.TaxcorpTaxFilingCaseSearchResultList;
import com.zcomapproach.garden.persistence.peony.data.TaxpayerCaseSearchResult;
import com.zcomapproach.garden.persistence.peony.data.TaxpayerCaseSearchResultList;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.garden.data.constant.UState;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.view.events.RequestBusyMouseCursor;
import com.zcomapproach.garden.peony.view.events.RequestDefaultMouseCursor;
import com.zcomapproach.garden.persistence.constant.GardenTaxpayerCaseStatus;
import com.zcomapproach.garden.persistence.peony.data.TaxpayerCaseSearchCacheResultList;
import com.zcomapproach.garden.persistence.peony.data.TaxpayerCaseStatusCacheResultList;
import com.zcomapproach.garden.taxation.TaxationSettings;
import com.zcomapproach.garden.util.GardenSorter;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javax.swing.JOptionPane;
import org.controlsfx.control.CheckComboBox;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class PeonySearchPaneController extends PeonySearchServiceController{
    /**
     * Search customers by its basic fields which is not date
     */
    @FXML
    private ComboBox<String> customerFeatureComboBox;
    @FXML
    private TextField customerFeatureValueField;
    @FXML
    private CheckBox customerFeatureExactMatchCheckBox;
    @FXML
    private Button customerFeatureSearchButton;
    
    /**
     * Search taxcorp's tax filing records
     */
    @FXML
    private FlowPane searchTaxFilingFlowPane;
    @FXML
    private ComboBox<String> taxFilingTypeComboBox;
    //this is programmatically added into searchTaxFilingFlowPane
    private CheckComboBox<String> taxFilingPeriodComboBox;
    @FXML
    private DatePicker deadlineFromDatePicker;
    @FXML
    private DatePicker deadlineToDatePicker;
    @FXML
    private Button searchTaxcorpTaxFilingsButton;
    @FXML
    private Button searchFinalizedTaxcorpTaxFilingsButton;
    /**
     * Search taxcorp by its basic fields which is not date
     */
    @FXML
    private ComboBox<String> taxcorpFeatureComboBox;
    @FXML
    private TextField taxcorpFeatureValueField;
    @FXML
    private CheckBox taxcorpFeatureExactMatchCheckBox;
    @FXML
    private Button taxcorpFeatureSearchButton;
    /**
     * Search taxcorp by a date range
     */
    @FXML
    private ComboBox<String> taxcorpDateTypeComboBox;
    @FXML
    private DatePicker taxcorpFromDatePicker;
    @FXML
    private DatePicker taxcorpToDatePicker;
    @FXML
    private Button taxcorpDateRangeSearchButton;
    /**
     * Search taxpayer by its basic fields which is not date
     */
    @FXML
    private ComboBox<String> taxpayerFeatureComboBox;
    @FXML
    private TextField taxpayerFeatureValueField;
    @FXML
    private CheckBox taxpayerFeatureExactMatchCheckBox;
    @FXML
    private Button taxpayerFeatureSearchButton;
    @FXML
    private Label taxpayerCriteriaNoteLabel;
    /**
     * Search taxpayer by a date range
     */
////    @FXML
////    private ComboBox<String> taxpayerDateTypeComboBox;
    @FXML
    private DatePicker taxpayerFromDatePicker;
    @FXML
    private DatePicker taxpayerToDatePicker;
    @FXML
    private Button taxpayerDateRangeSearchButton;
    @FXML
    private Button taxpayerStatusSummaryButton;
    @FXML
    private Button lastYearTaxpayerSearchButton;
    /**
     * Search taxpayer by working status
     */
    @FXML
    private ComboBox<String> taxpayerWorkStatusTypeComboBox;
    @FXML
    private DatePicker taxpayerWorkStatusDeadlineDatePickerFrom;
    @FXML
    private DatePicker taxpayerWorkStatusDeadlineDatePickerTo;
    @FXML
    private Button taxpayerWorkStatusSearchButton;

    public PeonySearchPaneController() {
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeSearchCustomerFeaturePanel();
        initializeSearchTaxcorpTaxFilingsPanel();
        initializeSearchTaxcorpFeaturePanel();
        initializeSearchTaxcorpDateRangePanel();
        initializeSearchTaxpayerFeaturePanel();
        initializeSearchTaxpayerDateRangePanel();
        initializeSearchTaxpayerByWorkStatusPanel();
    }

    private void initializeSearchTaxcorpTaxFilingsPanel() {
        PeonyFaceUtils.initializeComboBox(taxFilingTypeComboBox, TaxFilingType.getEnumValueList(false), null, null, "Tax filing type", false, this);
        
        //initialize CheckComboBox
        taxFilingPeriodComboBox = new CheckComboBox();
        PeonyFaceUtils.initializeCheckComboBox(taxFilingPeriodComboBox, TaxFilingPeriod.getEnumValueList(false), null, null, "Tax filing period", false, this);
        FlowPane.setMargin(taxFilingPeriodComboBox, new Insets(0,1,0,1));
        searchTaxFilingFlowPane.getChildren().add(1, taxFilingPeriodComboBox);
        
        PeonyFaceUtils.initializeDatePicker(deadlineFromDatePicker, null, null, "Deadline from", false, this);
        PeonyFaceUtils.initializeDatePicker(deadlineToDatePicker, null, null, "Deadline to", false, this);
        searchTaxcorpTaxFilingsButton.setOnAction((ActionEvent event) -> {
            getCachedThreadPoolExecutorService().submit(createSearchTaxcorpTaxFilingCaseSearchResultListTask(false));
        });
        searchFinalizedTaxcorpTaxFilingsButton.setOnAction((ActionEvent event) -> {
            getCachedThreadPoolExecutorService().submit(createSearchTaxcorpTaxFilingCaseSearchResultListTask(true));
        });
    }
    
    private Task<TaxcorpTaxFilingCaseSearchResultList> createSearchTaxcorpTaxFilingCaseSearchResultListTask(final boolean isFinalized){
        return new Task<TaxcorpTaxFilingCaseSearchResultList>(){
            @Override
            protected void failed() {
                PeonyFaceUtils.displayErrorMessageDialog("Search failed. " + getMessage());
            }
            @Override
            protected TaxcorpTaxFilingCaseSearchResultList call() throws Exception {
                try{
                    broadcastPeonyFaceEventHappened(new RequestBusyMouseCursor());
                    TaxcorpTaxFilingCaseSearchResultList result = new TaxcorpTaxFilingCaseSearchResultList();
                    result.setFromDateCriteria(ZcaCalendar.convertToDate(deadlineFromDatePicker.getValue()));
                    result.setToDateCriteria(ZcaCalendar.convertToDate(deadlineToDatePicker.getValue()));
                    result.setTaxFilingTypeCriteria(TaxFilingType.convertEnumValueToType(taxFilingTypeComboBox.getValue(), false).name());
                    ObservableList<String> checkedPeriods = taxFilingPeriodComboBox.getCheckModel().getCheckedItems();
                    if ((checkedPeriods == null) || (checkedPeriods.isEmpty())){
                        throw new Exception("Please check which tax-filing periods for this search operation.");
                    }else{
                        for (String checkedPeriod : checkedPeriods){
                            if (ZcaValidator.isNullEmpty(result.getTaxFilingPeriodCriteria())){
                                result.setTaxFilingPeriodCriteria(checkedPeriod);
                            }else{
                                result.setTaxFilingPeriodCriteria(result.getTaxFilingPeriodCriteria() + ", " + checkedPeriod);
                            }
                            TaxcorpTaxFilingCaseSearchResultList aTaxcorpTaxFilingCaseSearchResultList; 
                            if (isFinalized){
                                aTaxcorpTaxFilingCaseSearchResultList = Lookup.getDefault().lookup(PeonyTaxcorpService.class).getPeonyTaxcorpRestClient()
                                    .findEntity_XML(TaxcorpTaxFilingCaseSearchResultList.class, GardenRestParams.Taxcorp
                                            .searchFinalizedTaxcorpTaxFilingCaseSearchResultListByDeadlineRangeRestParams(result.getTaxFilingTypeCriteria(), 
                                                                                                                TaxFilingPeriod.convertEnumValueToType(checkedPeriod, false).name(), 
                                                                                                                result.getFromDateCriteria(), 
                                                                                                                result.getToDateCriteria()));
                            }else{
                                aTaxcorpTaxFilingCaseSearchResultList = Lookup.getDefault().lookup(PeonyTaxcorpService.class).getPeonyTaxcorpRestClient()
                                    .findEntity_XML(TaxcorpTaxFilingCaseSearchResultList.class, GardenRestParams.Taxcorp
                                            .searchTaxcorpTaxFilingCaseSearchResultListByDeadlineRangeRestParams(result.getTaxFilingTypeCriteria(), 
                                                                                                                TaxFilingPeriod.convertEnumValueToType(checkedPeriod, false).name(), 
                                                                                                                result.getFromDateCriteria(), 
                                                                                                                result.getToDateCriteria()));
                            }
                            if (aTaxcorpTaxFilingCaseSearchResultList != null){
                                result.getTaxcorpTaxFilingCaseSearchResultList().addAll(aTaxcorpTaxFilingCaseSearchResultList.getTaxcorpTaxFilingCaseSearchResultList());
                            }
                        }//for-loop
                    }

                    GardenSorter.sortTaxcorpTaxFilingCaseSearchResultList(result.getTaxcorpTaxFilingCaseSearchResultList(), false);

                    return result;
                }catch (Exception ex){
                    Exceptions.printStackTrace(ex);
                    return null;
                }
            }

            @Override
            protected void succeeded() {
                //GUI...
                broadcastPeonyFaceEventHappened(new RequestDefaultMouseCursor());
                try {
                    Lookup.getDefault().lookup(PeonySearchService.class).openPeonySearchResultPane(get());
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
    }

    private void initializeSearchCustomerFeaturePanel() {
        PeonyFaceUtils.initializeComboBox(customerFeatureComboBox, SearchUserCriteria.getEnumValueList(false), null, null, "Customer feature", false, this);
        PeonyFaceUtils.initializeTextField(customerFeatureValueField, null, null, "Tax feature's search value", false, this);
        PeonyFaceUtils.initializeCheckBox(customerFeatureExactMatchCheckBox, false, "Exactly match search-value or not", false, this);
        customerFeatureSearchButton.setOnAction((ActionEvent event) -> {
            Task<CustomerSearchResultList> searchCustomerCaseSearchResultListTask = new Task<CustomerSearchResultList>(){
                @Override
                protected CustomerSearchResultList call() throws Exception {
                    try{
                        broadcastPeonyFaceEventHappened(new RequestBusyMouseCursor());
                        CustomerSearchResultList aCustomerCaseSearchResultList = Lookup.getDefault().lookup(PeonyCustomerService.class).getPeonyCustomerRestClient()
                            .findEntity_XML(CustomerSearchResultList.class, GardenRestParams.Customer
                                    .searchCustomerSearchResultListByFeatureRestParams(SearchUserCriteria.convertEnumValueToType(customerFeatureComboBox.getValue(), false).name(), 
                                                                                          customerFeatureValueField.getText(), customerFeatureExactMatchCheckBox.isSelected()));
                        if (aCustomerCaseSearchResultList == null){
                            updateMessage("This operation failed.");
                            return null;
                        }
                        
                        GardenSorter.sortCustomerSearchResultList(aCustomerCaseSearchResultList.getCustomerSearchResultList(), false);
                        
                        return aCustomerCaseSearchResultList;
                    }catch (Exception ex){
                        Exceptions.printStackTrace(ex);
                        return null;
                    }
                }

                @Override
                protected void succeeded() {
                    broadcastPeonyFaceEventHappened(new RequestDefaultMouseCursor());
                    //GUI...
                    int msgType = JOptionPane.INFORMATION_MESSAGE;
                    try {
                        CustomerSearchResultList aCustomerCaseSearchResultList = get();
                        if (aCustomerCaseSearchResultList == null){
                            msgType = JOptionPane.ERROR_MESSAGE;
                        }else{
                            Lookup.getDefault().lookup(PeonySearchService.class).openPeonySearchResultPane(aCustomerCaseSearchResultList);
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    
                    //display result information dialog
                    String msg = getMessage();
                    if (ZcaValidator.isNotNullEmpty(msg)){
                        PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, msg, "Search Customer By Features", msgType);
                    }
                }
            };
            getCachedThreadPoolExecutorService().submit(searchCustomerCaseSearchResultListTask);
        });
    }

    private void initializeSearchTaxcorpFeaturePanel() {
        PeonyFaceUtils.initializeComboBox(taxcorpFeatureComboBox, SearchTaxcorpCriteria.getEnumValueList(false), null, null, "Taxcorp feature", false, this);
        PeonyFaceUtils.initializeTextField(taxcorpFeatureValueField, null, null, "Tax feature's search value", false, this);
        PeonyFaceUtils.initializeCheckBox(taxcorpFeatureExactMatchCheckBox, false, "Exactly match search-value or not", false, this);
        taxcorpFeatureSearchButton.setOnAction((ActionEvent event) -> {
            Task<TaxcorpCaseSearchResultList> searchTaxcorpCaseSearchResultListTask = new Task<TaxcorpCaseSearchResultList>(){
                @Override
                protected TaxcorpCaseSearchResultList call() throws Exception {
                    try{
                        broadcastPeonyFaceEventHappened(new RequestBusyMouseCursor());
                        TaxcorpCaseSearchResultList aTaxcorpCaseSearchResultList = Lookup.getDefault().lookup(PeonyTaxcorpService.class).getPeonyTaxcorpRestClient()
                            .findEntity_XML(TaxcorpCaseSearchResultList.class, GardenRestParams.Taxcorp
                                    .searchTaxcorpCaseSearchResultListByFeatureRestParams(SearchTaxcorpCriteria.convertEnumValueToType(taxcorpFeatureComboBox.getValue(), false).name(), 
                                                                                          taxcorpFeatureValueField.getText(), taxcorpFeatureExactMatchCheckBox.isSelected()));
                        if (aTaxcorpCaseSearchResultList == null){
                            updateMessage("This operation failed.");
                            return null;
                        }
                        
                        GardenSorter.sortTaxcorpCaseSearchResultList(aTaxcorpCaseSearchResultList.getTaxcorpCaseSearchResultList(), false);
                        
                        return aTaxcorpCaseSearchResultList;
                    }catch (Exception ex){
                        Exceptions.printStackTrace(ex);
                        return null;
                    }
                }

                @Override
                protected void succeeded() {
                    broadcastPeonyFaceEventHappened(new RequestDefaultMouseCursor());
                    //GUI...
                    int msgType = JOptionPane.INFORMATION_MESSAGE;
                    try {
                        TaxcorpCaseSearchResultList aTaxcorpCaseSearchResultList = get();
                        if (aTaxcorpCaseSearchResultList == null){
                            msgType = JOptionPane.ERROR_MESSAGE;
                        }else{
                            Lookup.getDefault().lookup(PeonySearchService.class).openPeonySearchResultPane(aTaxcorpCaseSearchResultList);
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    
                    //display result information dialog
                    String msg = getMessage();
                    if (ZcaValidator.isNotNullEmpty(msg)){
                        PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, msg, "Search Taxcorp By Features", msgType);
                    }
                }
            };
            getCachedThreadPoolExecutorService().submit(searchTaxcorpCaseSearchResultListTask);
        });
    }

    private void initializeSearchTaxcorpDateRangePanel() {
        PeonyFaceUtils.initializeComboBox(taxcorpDateTypeComboBox, TaxcorpEntityDateField.getEnumValueList(false), 
                null, TaxcorpEntityDateField.DOS_DATE.value(), "Taxcorp date feature", false, this);
        PeonyFaceUtils.initializeDatePicker(taxcorpFromDatePicker, null, null, "Date from", false, this);
        PeonyFaceUtils.initializeDatePicker(taxcorpToDatePicker, null, null, "Date to", false, this);
        taxcorpDateRangeSearchButton.setOnAction((ActionEvent event) -> {
            Task<TaxcorpCaseSearchResultList> searchTaxcorpCaseSearchResultListTask = new Task<TaxcorpCaseSearchResultList>(){
                @Override
                protected TaxcorpCaseSearchResultList call() throws Exception {
                    try{
                        broadcastPeonyFaceEventHappened(new RequestBusyMouseCursor());
                        TaxcorpCaseSearchResultList aTaxcorpCaseSearchResultList = Lookup.getDefault().lookup(PeonyTaxcorpService.class).getPeonyTaxcorpRestClient()
                            .findEntity_XML(TaxcorpCaseSearchResultList.class, GardenRestParams.Taxcorp
                                    .searchTaxcorpCaseSearchResultListByDateRangeRestParams(TaxcorpEntityDateField.convertEnumValueToType(taxcorpDateTypeComboBox.getValue(), false).name(), 
                                                                                          ZcaCalendar.convertToDate(taxcorpFromDatePicker.getValue()), 
                                                                                          ZcaCalendar.convertToDate(taxcorpToDatePicker.getValue())));
                        if (aTaxcorpCaseSearchResultList == null){
                            updateMessage("This operation failed.");
                            return null;
                        }
                        
                        GardenSorter.sortTaxcorpCaseSearchResultList(aTaxcorpCaseSearchResultList.getTaxcorpCaseSearchResultList(), false);
                        
                        return aTaxcorpCaseSearchResultList;
                    }catch (Exception ex){
                        Exceptions.printStackTrace(ex);
                        return null;
                    }
                }

                @Override
                protected void succeeded() {
                    broadcastPeonyFaceEventHappened(new RequestDefaultMouseCursor());
                    //GUI...
                    int msgType = JOptionPane.INFORMATION_MESSAGE;
                    try {
                        TaxcorpCaseSearchResultList aTaxcorpCaseSearchResultList = get();
                        if (aTaxcorpCaseSearchResultList == null){
                            msgType = JOptionPane.ERROR_MESSAGE;
                        }else{
                            Lookup.getDefault().lookup(PeonySearchService.class).openPeonySearchResultPane(aTaxcorpCaseSearchResultList);
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    
                    //display result information dialog
                    String msg = getMessage();
                    if (ZcaValidator.isNotNullEmpty(msg)){
                        PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, msg, "Search Taxcorp By DateRanges", msgType);
                    }
                }
            };
            getCachedThreadPoolExecutorService().submit(searchTaxcorpCaseSearchResultListTask);
        });
    }

    private void initializeSearchTaxpayerFeaturePanel() {
        PeonyFaceUtils.initializeComboBox(taxpayerFeatureComboBox, SearchTaxpayerCriteria.getEnumValueList(false), null, null, "Taxpayer feature", false, this);
        PeonyFaceUtils.initializeTextField(taxpayerFeatureValueField, null, null, "Tax feature's search value", false, this);
        PeonyFaceUtils.initializeCheckBox(taxpayerFeatureExactMatchCheckBox, false, "Exactly match search-value or not", false, this);
        
        String note = "*Note: Full name means first and last name delimited by a space even if it's Chinese. e.g. 茵 芦 or Yin Lu.";
        taxpayerFeatureComboBox.setTooltip(new Tooltip(note));
        taxpayerCriteriaNoteLabel.setText(note);
        taxpayerCriteriaNoteLabel.setStyle("-fx-font-size: 12");
        FlowPane.setMargin(taxpayerCriteriaNoteLabel, new Insets(5, 0, 0, 0));
        
        taxpayerFeatureSearchButton.setOnAction((ActionEvent event) -> {
            Task<TaxpayerCaseSearchCacheResultList> searchTaxpayerCaseSearchCacheResultListTask = new Task<TaxpayerCaseSearchCacheResultList>(){
                @Override
                protected TaxpayerCaseSearchCacheResultList call() throws Exception {
                    try{
                        broadcastPeonyFaceEventHappened(new RequestBusyMouseCursor());
                        TaxpayerCaseSearchCacheResultList aTaxpayerCaseSearchCacheResultList = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient()
                            .findEntity_XML(TaxpayerCaseSearchCacheResultList.class, GardenRestParams.Taxpayer
                                    .searchTaxpayerCaseSearchCacheResultListByFeatureFromCacheRestParams(SearchTaxpayerCriteria.convertEnumValueToType(taxpayerFeatureComboBox.getValue(), false).name(), 
                                                                                          taxpayerFeatureValueField.getText(), taxpayerFeatureExactMatchCheckBox.isSelected()));
                        if (aTaxpayerCaseSearchCacheResultList == null){
                            updateMessage("This operation failed.");
                            return null;
                        }
                        
                        GardenSorter.sortTaxpayerCaseSearchCacheResultListBySsn(aTaxpayerCaseSearchCacheResultList.getTaxpayerCaseSearchCacheResultList(), false);
                        
                        return aTaxpayerCaseSearchCacheResultList;
                    }catch (Exception ex){
                        Exceptions.printStackTrace(ex);
                        return null;
                    }
                }

                @Override
                protected void succeeded() {
                    broadcastPeonyFaceEventHappened(new RequestDefaultMouseCursor());
                    //GUI...
                    int msgType = JOptionPane.INFORMATION_MESSAGE;
                    try {
                        TaxpayerCaseSearchCacheResultList aTaxpayerCaseSearchCacheResultList = get();
                        if (aTaxpayerCaseSearchCacheResultList == null){
                            msgType = JOptionPane.ERROR_MESSAGE;
                        }else{
                            Lookup.getDefault().lookup(PeonySearchService.class).openPeonySearchResultPane(aTaxpayerCaseSearchCacheResultList);
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    
                    //display result information dialog
                    String msg = getMessage();
                    if (ZcaValidator.isNotNullEmpty(msg)){
                        PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, msg, "Search Taxpayer By Features", msgType);
                    }
                }
            };
            getCachedThreadPoolExecutorService().submit(searchTaxpayerCaseSearchCacheResultListTask);
        });
    }

    private void initializeSearchTaxpayerByWorkStatusPanel(){
        PeonyFaceUtils.initializeComboBox(taxpayerWorkStatusTypeComboBox, GardenTaxpayerCaseStatus.getEnumValueList(false), 
                null, GardenTaxpayerCaseStatus.CPA_APPROVED_REVIEW.value(), "Select a work status for search...", false, this);
        Date deadline = TaxationSettings.getSingleton().getComingPersonalFamlityTaxReturnDeadline(UState.NY).getTime();
        PeonyFaceUtils.initializeDatePicker(taxpayerWorkStatusDeadlineDatePickerFrom, null, 
                ZcaCalendar.convertToLocalDate(ZcaCalendar.addDates(deadline, -15)), "Date from", false, this);
        PeonyFaceUtils.initializeDatePicker(taxpayerWorkStatusDeadlineDatePickerTo, null, 
                ZcaCalendar.convertToLocalDate(ZcaCalendar.addDates(deadline, 15)), "Date from", false, this);
        taxpayerWorkStatusSearchButton.setOnAction((ActionEvent event) -> {
            getCachedThreadPoolExecutorService().submit(new TaxpayerSearchByDateRangeTask(TaxpayerEntityDateField.DEADLINE,
                    ZcaCalendar.convertToDate(taxpayerWorkStatusDeadlineDatePickerFrom.getValue()), 
                    ZcaCalendar.convertToDate(taxpayerWorkStatusDeadlineDatePickerTo.getValue()), 
                    false, taxpayerWorkStatusTypeComboBox.getValue()));
        });
    }
    
    private void initializeSearchTaxpayerDateRangePanel() {
        Date deadline = TaxationSettings.getSingleton().getComingPersonalFamlityTaxReturnDeadline(UState.NY).getTime();
        PeonyFaceUtils.initializeDatePicker(taxpayerFromDatePicker, null, 
                ZcaCalendar.convertToLocalDate(ZcaCalendar.addDates(deadline, -15)), "Date from", false, this);
        PeonyFaceUtils.initializeDatePicker(taxpayerToDatePicker, null, 
                ZcaCalendar.convertToLocalDate(ZcaCalendar.addDates(deadline, 15)), "Date to", false, this);
        
        taxpayerDateRangeSearchButton.setOnAction((ActionEvent event) -> {
            getCachedThreadPoolExecutorService().submit(new TaxpayerSearchByDateRangeTask(TaxpayerEntityDateField.DEADLINE,
                    ZcaCalendar.convertToDate(taxpayerFromDatePicker.getValue()),
                    ZcaCalendar.convertToDate(taxpayerToDatePicker.getValue()),
                    false, null));
        });
        
        taxpayerStatusSummaryButton.setOnAction(evt -> {
            getCachedThreadPoolExecutorService().submit(new TaxpayerStatusByDateRangeTask(TaxpayerEntityDateField.DEADLINE,
                    ZcaCalendar.convertToDate(taxpayerFromDatePicker.getValue()),
                    ZcaCalendar.convertToDate(taxpayerToDatePicker.getValue()), null));
            //PeonyFaceUtils.displayWarningMessageDialog("Coming soon...");
        });
        
        lastYearTaxpayerSearchButton.setTooltip(new Tooltip("Only display last year's taxpayer cases which are not setup yet this year. "));
        lastYearTaxpayerSearchButton.setOnAction((ActionEvent event) -> {
            Task<TaxpayerCaseSearchResultList> searchTaxpayerCaseSearchResultListTask = new Task<TaxpayerCaseSearchResultList>(){
                @Override
                protected TaxpayerCaseSearchResultList call() throws Exception {
                    try{
                        broadcastPeonyFaceEventHappened(new RequestBusyMouseCursor());
                        
                        GregorianCalendar currentFrom = new GregorianCalendar();
                        currentFrom.set(Calendar.MONTH, Calendar.APRIL);
                        currentFrom.set(Calendar.DAY_OF_MONTH, 1);
                        GregorianCalendar currentTo = new GregorianCalendar();
                        currentTo.set(Calendar.MONTH, Calendar.MAY);
                        currentTo.set(Calendar.DAY_OF_MONTH, 1);
                        
                        int currentYear = currentFrom.get(Calendar.YEAR);
                        
                        GregorianCalendar lastFrom = new GregorianCalendar();
                        lastFrom.set(Calendar.YEAR, currentYear-1);
                        lastFrom.set(Calendar.MONTH, Calendar.APRIL);
                        lastFrom.set(Calendar.DAY_OF_MONTH, 1);
                        GregorianCalendar lastTo = new GregorianCalendar();
                        lastTo.set(Calendar.YEAR, currentYear-1);
                        lastTo.set(Calendar.MONTH, Calendar.MAY);
                        lastTo.set(Calendar.DAY_OF_MONTH, 1);
                        
                        TaxpayerCaseSearchResultList aTaxpayerCaseSearchResultList = new TaxpayerCaseSearchResultList();
                        //Last year's taxpayers
                        TaxpayerCaseSearchResultList lastTaxpayerCaseSearchResultList = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient()
                            .findEntity_XML(TaxpayerCaseSearchResultList.class, GardenRestParams.Taxpayer
                                    .searchTaxpayerCaseSearchResultListByDateRangeRestParams(TaxpayerEntityDateField.DEADLINE.name(), 
                                                                                          lastFrom.getTime(), lastTo.getTime()));
                        if (lastTaxpayerCaseSearchResultList == null){
                            updateMessage("This operation failed.");
                            return null;
                        }
                        //This year's taxpayers
                        TaxpayerCaseSearchResultList currentTaxpayerCaseSearchResultList = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient()
                            .findEntity_XML(TaxpayerCaseSearchResultList.class, GardenRestParams.Taxpayer
                                    .searchTaxpayerCaseSearchResultListByDateRangeRestParams(TaxpayerEntityDateField.DEADLINE.name(), 
                                                                                          currentFrom.getTime(), currentTo.getTime()));
                        if (currentTaxpayerCaseSearchResultList == null){
                            updateMessage("This operation failed.");
                            return null;
                        }
                        //Current taxpayers whose customers have been processed 
                        TreeSet<String> currentCustomerIDs = new TreeSet<>();
                        List<TaxpayerCaseSearchResult> taxpayerCaseSearchResults = currentTaxpayerCaseSearchResultList.getTaxpayerCaseSearchResultList();
                        for (TaxpayerCaseSearchResult aTaxpayerCaseSearchResult : taxpayerCaseSearchResults){
                            currentCustomerIDs.add(aTaxpayerCaseSearchResult.getTaxpayerCase().getCustomerAccountUuid());
                        }
                        //filter out the missing ones
                        taxpayerCaseSearchResults = lastTaxpayerCaseSearchResultList.getTaxpayerCaseSearchResultList();
                        List<TaxpayerCaseSearchResult> finalTaxpayerCaseSearchResults = aTaxpayerCaseSearchResultList.getTaxpayerCaseSearchResultList();
                        for (TaxpayerCaseSearchResult aTaxpayerCaseSearchResult : taxpayerCaseSearchResults){
                            if (!currentCustomerIDs.contains(aTaxpayerCaseSearchResult.getTaxpayerCase().getCustomerAccountUuid())){
                                finalTaxpayerCaseSearchResults.add(aTaxpayerCaseSearchResult);
                            }
                        }
                        return aTaxpayerCaseSearchResultList;
                    }catch (Exception ex){
                        Exceptions.printStackTrace(ex);
                        return null;
                    }
                }

                @Override
                protected void succeeded() {
                    broadcastPeonyFaceEventHappened(new RequestDefaultMouseCursor());
                    //GUI...
                    int msgType = JOptionPane.INFORMATION_MESSAGE;
                    try {
                        TaxpayerCaseSearchResultList aTaxpayerCaseSearchResultList = get();
                        if (aTaxpayerCaseSearchResultList == null){
                            msgType = JOptionPane.ERROR_MESSAGE;
                        }else{
                            aTaxpayerCaseSearchResultList.setForSearchByDataRange(true);
                            aTaxpayerCaseSearchResultList.setForWorkStatusSummary(false);
                            Lookup.getDefault().lookup(PeonySearchService.class).openPeonySearchResultPane(aTaxpayerCaseSearchResultList);
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    
                    //display result information dialog
                    String msg = getMessage();
                    if (ZcaValidator.isNotNullEmpty(msg)){
                        PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, msg, "Search Taxpayer By DateRanges", msgType);
                    }
                }
            };
            getCachedThreadPoolExecutorService().submit(searchTaxpayerCaseSearchResultListTask);
        });
    }
    
    private class TaxpayerSearchByDateRangeTask extends Task<TaxpayerCaseSearchCacheResultList>{
        private final TaxpayerEntityDateField aTaxpayerEntityDateField;
        private final Date deadlineFrom;
        private final Date deadlineTo;
        private final boolean forWorkStatusSummary;
        private final String workStatusForSearch;

        public TaxpayerSearchByDateRangeTask(TaxpayerEntityDateField aTaxpayerEntityDateField, Date deadlineFrom, Date deadlineTo, boolean forWorkStatusSummary, String workStatusForSearch) {
            this.aTaxpayerEntityDateField = aTaxpayerEntityDateField;
            this.deadlineFrom = deadlineFrom;
            this.deadlineTo = deadlineTo;
            this.forWorkStatusSummary = forWorkStatusSummary;
            this.workStatusForSearch = workStatusForSearch;
        }
        @Override
        protected TaxpayerCaseSearchCacheResultList call() throws Exception {
            try{
                broadcastPeonyFaceEventHappened(new RequestBusyMouseCursor());
                TaxpayerCaseSearchCacheResultList aTaxpayerCaseSearchCacheResultList;
                if (ZcaValidator.isNullEmpty(workStatusForSearch)){
                    aTaxpayerCaseSearchCacheResultList = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient()
                        .findEntity_XML(TaxpayerCaseSearchCacheResultList.class, GardenRestParams.Taxpayer
                                .retrieveTaxpayerCaseSearchCacheResultListByDateRangeRestParams(aTaxpayerEntityDateField.name(), deadlineFrom, deadlineTo));
                }else{
                    aTaxpayerCaseSearchCacheResultList = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient()
                        .findEntity_XML(TaxpayerCaseSearchCacheResultList.class, GardenRestParams.Taxpayer
                                .retrieveTaxpayerCaseSearchCacheResultListByDateRangeAndLatestWorkStatusRestParams(aTaxpayerEntityDateField.name(), deadlineFrom, deadlineTo, workStatusForSearch));
                }
                if (aTaxpayerCaseSearchCacheResultList == null){
                    updateMessage("This operation failed.");
                    return null;
                }
                return aTaxpayerCaseSearchCacheResultList;
            }catch (Exception ex){
                Exceptions.printStackTrace(ex);
                return null;
            }
        }

        @Override
        protected void succeeded() {
            //GUI...
            int msgType = JOptionPane.INFORMATION_MESSAGE;
            broadcastPeonyFaceEventHappened(new RequestDefaultMouseCursor());
            try {
                TaxpayerCaseSearchCacheResultList result = get();
                if (result == null){
                    msgType = JOptionPane.ERROR_MESSAGE;
                }else{
                    Lookup.getDefault().lookup(PeonySearchService.class).openPeonySearchResultPane(result);
                }
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }

            //display result information dialog
            String msg = getMessage();
            if (ZcaValidator.isNotNullEmpty(msg)){
                PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, msg, "Search Taxpayer By Date Ranges/Work Status", msgType);
            }
        }
    }
    
    private class TaxpayerStatusByDateRangeTask extends Task<TaxpayerCaseStatusCacheResultList>{
        private final TaxpayerEntityDateField aTaxpayerEntityDateField;
        private final Date deadlineFrom;
        private final Date deadlineTo;
        private final String workStatusForSearch;

        public TaxpayerStatusByDateRangeTask(TaxpayerEntityDateField aTaxpayerEntityDateField, Date deadlineFrom, Date deadlineTo, String workStatusForSearch) {
            this.aTaxpayerEntityDateField = aTaxpayerEntityDateField;
            this.deadlineFrom = deadlineFrom;
            this.deadlineTo = deadlineTo;
            this.workStatusForSearch = workStatusForSearch;
        }
        @Override
        protected TaxpayerCaseStatusCacheResultList call() throws Exception {
            try{
                broadcastPeonyFaceEventHappened(new RequestBusyMouseCursor());
                TaxpayerCaseStatusCacheResultList aTaxpayerCaseStatusCacheResultList = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient()
                        .findEntity_XML(TaxpayerCaseStatusCacheResultList.class, GardenRestParams.Taxpayer
                                .retrieveTaxpayerCaseStatusCacheResultListByDateRangeAndLatestWorkStatusRestParams(aTaxpayerEntityDateField.name(), deadlineFrom, deadlineTo, workStatusForSearch));
                if (aTaxpayerCaseStatusCacheResultList == null){
                    updateMessage("This operation failed.");
                    return null;
                }
                return aTaxpayerCaseStatusCacheResultList;
            }catch (Exception ex){
                Exceptions.printStackTrace(ex);
                return null;
            }
        }

        @Override
        protected void succeeded() {
            //GUI...
            int msgType = JOptionPane.INFORMATION_MESSAGE;
            broadcastPeonyFaceEventHappened(new RequestDefaultMouseCursor());
            try {
                TaxpayerCaseStatusCacheResultList result = get();
                if (result == null){
                    msgType = JOptionPane.ERROR_MESSAGE;
                }else{
                    Lookup.getDefault().lookup(PeonySearchService.class).openPeonySearchResultPane(result);
                }
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }

            //display result information dialog
            String msg = getMessage();
            if (ZcaValidator.isNotNullEmpty(msg)){
                PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, msg, "Search Taxpayer By Date Ranges/Work Status", msgType);
            }
        }
    }
}
