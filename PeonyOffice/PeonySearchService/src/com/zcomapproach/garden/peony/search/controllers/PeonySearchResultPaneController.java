package com.zcomapproach.garden.peony.search.controllers;

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


import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.exception.GardenRuntimeException;
import com.zcomapproach.garden.peony.controls.PeonyButtonTableCell;
import com.zcomapproach.garden.peony.kernel.services.PeonyBusinessService;
import com.zcomapproach.garden.peony.kernel.services.PeonyCustomerService;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxcorpService;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import com.zcomapproach.garden.peony.search.data.TaxcorpTaxFilingCaseSearchResultTableCell;
import com.zcomapproach.garden.peony.search.data.TaxFilingStatusDateEditingCellForSearch;
import com.zcomapproach.garden.peony.search.dialogs.PeonySearchResultFilterDialog;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.data.CustomerSearchResultColumns;
import com.zcomapproach.garden.peony.view.data.TaxcorpCaseSearchResultColumns;
import com.zcomapproach.garden.peony.view.data.TaxcorpTaxFilingCaseSearchResultColumns;
import com.zcomapproach.garden.peony.view.data.TaxpayerCaseSearchResultColumns;
import com.zcomapproach.garden.peony.view.dialogs.MemoDataEntryDialog;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.events.PeonyTaxFilingCaseUpdated;
import com.zcomapproach.garden.peony.view.events.SearchResultColumnDateCriteraCreated;
import com.zcomapproach.garden.peony.view.events.SearchResultColumnTextCriteraCreated;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.constant.GardenContactType;
import com.zcomapproach.garden.persistence.constant.GardenTaxFilingStatus;
import com.zcomapproach.garden.persistence.entity.G02BusinessContactor;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerInfo;
import com.zcomapproach.garden.persistence.peony.PeonyBillPayment;
import com.zcomapproach.garden.persistence.peony.PeonyBillPaymentList;
import com.zcomapproach.garden.persistence.peony.PeonyMemo;
import com.zcomapproach.garden.persistence.peony.data.CustomerSearchResult;
import com.zcomapproach.garden.persistence.peony.data.CustomerSearchResultList;
import com.zcomapproach.garden.persistence.peony.data.PeonySearchResult;
import com.zcomapproach.garden.persistence.peony.data.TaxcorpCaseSearchResult;
import com.zcomapproach.garden.persistence.peony.data.TaxcorpCaseSearchResultList;
import com.zcomapproach.garden.persistence.peony.data.TaxcorpTaxFilingCaseSearchResult;
import com.zcomapproach.garden.persistence.peony.data.TaxpayerCaseSearchResult;
import com.zcomapproach.garden.persistence.peony.data.TaxpayerCaseSearchResultList;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.garden.rest.data.GardenRestStringList;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.garden.util.GardenData;
import com.zcomapproach.garden.util.GardenFaceX;
import com.zcomapproach.commons.nio.ZcaNio;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.search.dialogs.TaxpayerCaseDataEditorDialog;
import com.zcomapproach.garden.peony.search.dialogs.TaxpayerCaseStatusEditorDialog;
import com.zcomapproach.garden.peony.search.events.TaxpayerCaseStatusUpdated;
import static com.zcomapproach.garden.peony.view.data.TaxcorpTaxFilingCaseSearchResultColumns.EXT_EFILED;
import com.zcomapproach.garden.peony.view.data.TaxpayerCaseSearchCacheResultColumns;
import com.zcomapproach.garden.peony.view.data.TaxpayerCaseStatusReportColumns;
import com.zcomapproach.garden.peony.view.data.TaxpayerWorkStatusSummaryColumns;
import com.zcomapproach.garden.persistence.constant.TaxFilingType;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerCaseStatusCache;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.persistence.peony.data.TaxcorpTaxFilingCaseSearchResultList;
import com.zcomapproach.garden.persistence.peony.data.TaxpayerCaseSearchCacheResult;
import com.zcomapproach.garden.persistence.peony.data.TaxpayerCaseSearchCacheResultList;
import com.zcomapproach.garden.persistence.peony.data.TaxpayerCaseStatusCacheResult;
import com.zcomapproach.garden.persistence.peony.data.TaxpayerCaseStatusCacheResultList;
import com.zcomapproach.garden.util.GardenSorter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.controlsfx.glyphfont.FontAwesome;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class PeonySearchResultPaneController extends PeonySearchServiceController implements PeonyFaceEventListener{
    
    private final static int rowsPerPage = 15;
    @FXML
    private Button textFilterButton;
    @FXML
    private Button dateFilterButton;
    @FXML
    private Button resetButton;
    @FXML
    private Button sendSmsButton;
    @FXML
    private Button exportButton;
    @FXML
    private BorderPane searchResultBorderPane;
    @FXML
    private Label totalLabel;
    
    private final TableView<CustomerSearchResult> customerSearchResultTable = new TableView();
    private final TableView<TaxcorpTaxFilingCaseSearchResult> taxcorpTaxFilingSearchResultTable = new TableView();
    private final TableView<TaxcorpCaseSearchResult> taxcorpSearchResultTable = new TableView();
    private final TableView<TaxpayerCaseSearchResult> taxpayerSearchResultTable = new TableView();
    private final TableView<TaxpayerCaseSearchCacheResult> taxpayerCacheSearchResultTable = new TableView();
    private final TableView<TaxpayerCaseStatusCacheResult> taxpayerStatusCacheSearchResultTable = new TableView();

    private CustomerSearchResultList customerSearchResultList;
    //todo zzj: replace it with real filter in the table
    private final List<CustomerSearchResult> filteredCustomerSearchResultList = new ArrayList<>();
    
    private TaxcorpTaxFilingCaseSearchResultList taxcorpTaxFilingCaseSearchResultList;
    //todo zzj: replace it with real filter in the table
    private final List<TaxcorpTaxFilingCaseSearchResult> filteredTaxcorpTaxFilingCaseSearchResultList = new ArrayList<>();

    private TaxcorpCaseSearchResultList taxcorpCaseSearchResultList;
    //todo zzj: replace it with real filter in the table
    private final List<TaxcorpCaseSearchResult> filteredTaxcorpCaseSearchResultList = new ArrayList<>();
    
    private TaxpayerCaseSearchResultList taxpayerCaseSearchResultList;
    //todo zzj: replace it with real filter in the table
    private final List<TaxpayerCaseSearchResult> filteredTaxpayerCaseSearchResultList = new ArrayList<>();
    
    private TaxpayerCaseSearchCacheResultList taxpayerCaseSearchCacheResultList;
    //todo zzj: replace it with real filter in the table
    private final List<TaxpayerCaseSearchCacheResult> filteredTaxpayerCaseSearchCacheResultList = new ArrayList<>();
    
    private TaxpayerCaseStatusCacheResultList taxpayerCaseStatusCacheResultList;
    //todo zzj: replace it with real filter in the table
    private final List<TaxpayerCaseStatusCacheResult> filteredTaxpayerCaseStatusCacheResultList = new ArrayList<>();
    
    public PeonySearchResultPaneController(PeonySearchResult peonySearchResult) {
        if (peonySearchResult instanceof TaxcorpTaxFilingCaseSearchResultList){
            taxcorpTaxFilingSearchResultTable.setEditable(true);
            this.taxcorpTaxFilingCaseSearchResultList = (TaxcorpTaxFilingCaseSearchResultList)peonySearchResult;
            //populateBalanceStatusForTaxcorpSearchResultList();
            populateBalanceStatusOneByOne();
        }else if (peonySearchResult instanceof TaxcorpCaseSearchResultList){
            taxcorpSearchResultTable.setEditable(true);
            this.taxcorpCaseSearchResultList = (TaxcorpCaseSearchResultList)peonySearchResult;
        }else if (peonySearchResult instanceof TaxpayerCaseSearchResultList){
            taxpayerSearchResultTable.setEditable(true);
            this.taxpayerCaseSearchResultList = (TaxpayerCaseSearchResultList)peonySearchResult;
        }else if (peonySearchResult instanceof TaxpayerCaseSearchCacheResultList){
            taxpayerCacheSearchResultTable.setEditable(true);
            this.taxpayerCaseSearchCacheResultList = (TaxpayerCaseSearchCacheResultList)peonySearchResult;
        }else if (peonySearchResult instanceof TaxpayerCaseStatusCacheResultList){
            taxpayerStatusCacheSearchResultTable.setEditable(true);
            this.taxpayerCaseStatusCacheResultList = (TaxpayerCaseStatusCacheResultList)peonySearchResult;
        }else if (peonySearchResult instanceof CustomerSearchResultList){
            customerSearchResultTable.setEditable(true);
            this.customerSearchResultList = (CustomerSearchResultList)peonySearchResult;
        }
        resetFilterSeachResult();
    }
    
    private void populateBalanceStatusOneByOne() {
        List<TaxcorpTaxFilingCaseSearchResult> aTaxcorpTaxFilingCaseSearchResultList = taxcorpTaxFilingCaseSearchResultList.getTaxcorpTaxFilingCaseSearchResultList();
        for (TaxcorpTaxFilingCaseSearchResult aTaxcorpTaxFilingCaseSearchResult : aTaxcorpTaxFilingCaseSearchResultList){
            PopulateBalanceStatusForTaxcorpSearchResultTask aPopulateBalanceStatusForTaxcorpSearchResultTask = new PopulateBalanceStatusForTaxcorpSearchResultTask(aTaxcorpTaxFilingCaseSearchResult);
            getCachedThreadPoolExecutorService().submit(aPopulateBalanceStatusForTaxcorpSearchResultTask);
        }
    }
    
    private class PopulateBalanceStatusForTaxcorpSearchResultTask extends Task<Void>{

        private final TaxcorpTaxFilingCaseSearchResult aTaxcorpTaxFilingCaseSearchResult;

        public PopulateBalanceStatusForTaxcorpSearchResultTask(TaxcorpTaxFilingCaseSearchResult aTaxcorpTaxFilingCaseSearchResult) {
            this.aTaxcorpTaxFilingCaseSearchResult = aTaxcorpTaxFilingCaseSearchResult;
        }
    
        @Override
        protected Void call() throws Exception {
            //prepare data structures
            GardenRestStringList aGardenRestStringList = new GardenRestStringList();
            aGardenRestStringList.getStringDataList().add(aTaxcorpTaxFilingCaseSearchResult.getTaxcorpCase().getTaxcorpCaseUuid());
            //query...
            PeonyBillPaymentList returnedPeonyBillPaymentListList = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                                                                        .storeEntity_XML(PeonyBillPaymentList.class, 
                                                                                GardenRestParams.Business.retrievePeonyBillPaymentListByEntityUuidListRestParams(), 
                                                                                aGardenRestStringList);
            if (returnedPeonyBillPaymentListList != null){
                List<PeonyBillPayment> returnedPeonyBillPayments = returnedPeonyBillPaymentListList.getPeonyBillPaymentList();
                if (returnedPeonyBillPayments != null) {
                    GardenSorter.sortPeonyBillPaymentListByDueDate(returnedPeonyBillPayments, true);
                    for (PeonyBillPayment returnedPeonyBillPayment : returnedPeonyBillPayments){
                        aTaxcorpTaxFilingCaseSearchResult.getPeonyBillPaymentList().add(returnedPeonyBillPayment);
                    }//for-loop
                }
            }
            return null;
        }

        @Override
        protected void succeeded() {
            try {
                get();
                taxcorpTaxFilingSearchResultTable.refresh();
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void populateBalanceStatusForTaxcorpSearchResultList() {
        Task<Void> populateBalanceStatusForTaxcorpSearchResultListTask = new Task<Void>(){
            @Override
            protected Void call() throws Exception {
                List<TaxcorpTaxFilingCaseSearchResult> aTaxcorpTaxFilingCaseSearchResultList = taxcorpTaxFilingCaseSearchResultList.getTaxcorpTaxFilingCaseSearchResultList();
                HashMap<String, TaxcorpTaxFilingCaseSearchResult> temp = new HashMap<>();
                String taxcorpCaseUuid;
                //prepare data structures
                GardenRestStringList aGardenRestStringList = new GardenRestStringList();
                for (TaxcorpTaxFilingCaseSearchResult aTaxcorpTaxFilingCaseSearchResult : aTaxcorpTaxFilingCaseSearchResultList){
                    taxcorpCaseUuid = aTaxcorpTaxFilingCaseSearchResult.getTaxcorpCase().getTaxcorpCaseUuid();
                    if (!temp.containsKey(taxcorpCaseUuid)){
                        //collect which taxcorps demanding balance status
                        aGardenRestStringList.getStringDataList().add(taxcorpCaseUuid);
                        
                        
                        if (aGardenRestStringList.getStringDataList().size() == 2){
                            break;
                        }
                    }
                    temp.put(taxcorpCaseUuid, aTaxcorpTaxFilingCaseSearchResult);
                }
                //query...
                PeonyBillPaymentList returnedPeonyBillPaymentListList = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                                                                            .storeEntity_XML(PeonyBillPaymentList.class, 
                                                                                    GardenRestParams.Business.retrievePeonyBillPaymentListByEntityUuidListRestParams(), 
                                                                                    aGardenRestStringList);
                if (returnedPeonyBillPaymentListList != null){
                    List<PeonyBillPayment> returnedPeonyBillPayments = returnedPeonyBillPaymentListList.getPeonyBillPaymentList();
                    if (returnedPeonyBillPayments != null) {
                        GardenSorter.sortPeonyBillPaymentListByDueDate(returnedPeonyBillPayments, true);
                        TaxcorpTaxFilingCaseSearchResult aTaxcorpTaxFilingCaseSearchResult;
                        for (PeonyBillPayment returnedPeonyBillPayment : returnedPeonyBillPayments){
                            aTaxcorpTaxFilingCaseSearchResult = temp.get(returnedPeonyBillPayment.getBill().getEntityUuid());
                            if (aTaxcorpTaxFilingCaseSearchResult != null){
                                aTaxcorpTaxFilingCaseSearchResult.getPeonyBillPaymentList().add(returnedPeonyBillPayment);
                            }
                        }//for-loop
                    }
                }
                return null;
            }

            @Override
            protected void succeeded() {
                try {
                    get();
                    taxcorpTaxFilingSearchResultTable.refresh();
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        this.getCachedThreadPoolExecutorService().submit(populateBalanceStatusForTaxcorpSearchResultListTask);
    }

    private void resetFilterSeachResult(){
        if (taxcorpTaxFilingCaseSearchResultList != null){
            List<TaxcorpTaxFilingCaseSearchResult> aTaxcorpTaxFilingCaseSearchResultList = taxcorpTaxFilingCaseSearchResultList.getTaxcorpTaxFilingCaseSearchResultList();
            for (TaxcorpTaxFilingCaseSearchResult aTaxcorpTaxFilingCaseSearchResult : aTaxcorpTaxFilingCaseSearchResultList){
                filteredTaxcorpTaxFilingCaseSearchResultList.add(aTaxcorpTaxFilingCaseSearchResult);
            }
        }else if (taxcorpCaseSearchResultList != null){
            List<TaxcorpCaseSearchResult> aTaxcorpCaseSearchResultList = taxcorpCaseSearchResultList.getTaxcorpCaseSearchResultList();
            for (TaxcorpCaseSearchResult aTaxcorpCaseSearchResult : aTaxcorpCaseSearchResultList){
                filteredTaxcorpCaseSearchResultList.add(aTaxcorpCaseSearchResult);
            }
        }else if (taxpayerCaseSearchResultList != null){
            List<TaxpayerCaseSearchResult> aTaxpayerCaseSearchResultList = taxpayerCaseSearchResultList.getTaxpayerCaseSearchResultList();
            for (TaxpayerCaseSearchResult aTaxpayerCaseSearchResult : aTaxpayerCaseSearchResultList){
                filteredTaxpayerCaseSearchResultList.add(aTaxpayerCaseSearchResult);
            }
        }else if (taxpayerCaseSearchCacheResultList != null){
            List<TaxpayerCaseSearchCacheResult> aTaxpayerCaseSearchCacheResultList = taxpayerCaseSearchCacheResultList.getTaxpayerCaseSearchCacheResultList();
            for (TaxpayerCaseSearchCacheResult aTaxpayerCaseSearchCacheResult : aTaxpayerCaseSearchCacheResultList){
                filteredTaxpayerCaseSearchCacheResultList.add(aTaxpayerCaseSearchCacheResult);
            }
        }else if (taxpayerCaseStatusCacheResultList != null){
            List<TaxpayerCaseStatusCacheResult> aTaxpayerCaseStatusCacheResultList = taxpayerCaseStatusCacheResultList.getTaxpayerCaseStatusCacheResultList();
            for (TaxpayerCaseStatusCacheResult aTaxpayerCaseStatusCacheResult : aTaxpayerCaseStatusCacheResultList){
                filteredTaxpayerCaseStatusCacheResultList.add(aTaxpayerCaseStatusCacheResult);
            }
        }else if (customerSearchResultList != null){
            List<CustomerSearchResult> aCustomerSearchResultList = customerSearchResultList.getCustomerSearchResultList();
            for (CustomerSearchResult aCustomerSearchResult : aCustomerSearchResultList){
                filteredCustomerSearchResultList.add(aCustomerSearchResult);
            }
        }else{
            
        }
    
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (taxcorpTaxFilingCaseSearchResultList != null){
            //resetFilterControls(TaxcorpTaxFilingCaseSearchResultColumns.getEnumValueList(false));
            initializeSearchResultTableForTaxcorpTaxFilingCaseSearchResultList();
        }else if (taxcorpCaseSearchResultList != null){
            //resetFilterControls(TaxcorpCaseSearchResultColumns.getEnumValueList(false));
            initializeSearchResultTableForTaxcorpCaseSearchResultList();
        }else if (taxpayerCaseSearchResultList != null){
            //resetFilterControls(TaxpayerCaseSearchResultColumns.getEnumValueList(false));
            initializeSearchResultTableForTaxpayerCaseSearchResultList();
        }else if (taxpayerCaseSearchCacheResultList != null){
            //resetFilterControls(TaxpayerCaseSearchCacheResultColumns.getEnumValueList(false));
            initializeSearchResultTableForTaxpayerCaseSearchCacheResultList();
        }else if (taxpayerCaseStatusCacheResultList != null){
            //resetFilterControls(TaxpayerCaseStatusReportColumns.getEnumValueList(false));
            initializeSearchResultTableForTaxpayerCaseStatusCacheResultList();
        }else if (customerSearchResultList != null){
            //resetFilterControls(CustomerSearchResultColumns.getEnumValueList(false));
            initializeSearchResultTableForCustomerSearchResultList();
        }
        textFilterButton.setOnAction((ActionEvent event) -> {
            PeonySearchResultFilterDialog aPeonySearchResultFilterDialog = new PeonySearchResultFilterDialog(null, true);
            aPeonySearchResultFilterDialog.addPeonyFaceEventListener(this);
            if (taxcorpTaxFilingCaseSearchResultList != null){
                aPeonySearchResultFilterDialog.launchPeonySearchResultFilterDialog("Tax Filing Search Result", 
                                                                                    "Filter for Tax Filing Search Result", 
                                                                                    TaxcorpTaxFilingCaseSearchResultColumns.getEnumTextColumnValueList(), false);
            }else if (taxcorpCaseSearchResultList != null){
                aPeonySearchResultFilterDialog.launchPeonySearchResultFilterDialog("Taxcorp Search Result", 
                                                                                    "Filter for Taxcorp Search Result", 
                                                                                    TaxcorpCaseSearchResultColumns.getEnumTextColumnValueList(), false);
            }else if (taxpayerCaseSearchResultList != null){
                if (taxpayerCaseSearchResultList.isForWorkStatusSummary()){
                    aPeonySearchResultFilterDialog.launchPeonySearchResultFilterDialog("Taxpayer Work Status Summary", 
                                                                                        "Filter for Taxpayer Work Status Summary", 
                                                                                        TaxpayerWorkStatusSummaryColumns.getEnumTextColumnValueList(), false);
                }else{
                    aPeonySearchResultFilterDialog.launchPeonySearchResultFilterDialog("Taxpayer Search Result", 
                                                                                        "Filter for Taxpayer Search Result", 
                                                                                        TaxpayerCaseSearchResultColumns.getEnumTextColumnValueList(), false);
                }
            }else if (taxpayerCaseSearchCacheResultList != null){
                aPeonySearchResultFilterDialog.launchPeonySearchResultFilterDialog("Taxpayer Search Result", 
                                                                                    "Filter for Taxpayer Search Result", 
                                                                                    TaxpayerCaseSearchCacheResultColumns.getEnumTextColumnValueList(), false);
            }else if (taxpayerCaseStatusCacheResultList != null){
                aPeonySearchResultFilterDialog.launchPeonySearchResultFilterDialog("Taxpayer Search Result", 
                                                                                    "Filter for Taxpayer Search Result", 
                                                                                    TaxpayerCaseSearchResultColumns.getEnumTextColumnValueList(), false);
            }else if (customerSearchResultList != null){
                aPeonySearchResultFilterDialog.launchPeonySearchResultFilterDialog("Customer Search Result", 
                                                                                    "Filter for Customer Search Result", 
                                                                                    CustomerSearchResultColumns.getEnumTextColumnValueList(), false);
            }else{
                aPeonySearchResultFilterDialog.launchPeonySearchResultFilterDialog("Search Result", 
                                                                                    "Filter for Search Result", 
                                                                                    new ArrayList<>(), false);
            }
            
            //filterSearchResultTable();
        });
        dateFilterButton.setOnAction((ActionEvent event) -> {
            PeonySearchResultFilterDialog aPeonySearchResultFilterDialog = new PeonySearchResultFilterDialog(null, true);
            aPeonySearchResultFilterDialog.addPeonyFaceEventListener(this);
            if (taxcorpTaxFilingCaseSearchResultList != null){
                aPeonySearchResultFilterDialog.launchPeonySearchResultFilterDialog("Tax Filing Search Result", 
                                                                                    "Filter for Tax Filing Search Result", 
                                                                                    TaxcorpTaxFilingCaseSearchResultColumns.getEnumDateColumnValueList(), true);
            }else if (taxcorpCaseSearchResultList != null){
                aPeonySearchResultFilterDialog.launchPeonySearchResultFilterDialog("Taxcorp Search Result", 
                                                                                    "Filter for Taxcorp Search Result", 
                                                                                    TaxcorpCaseSearchResultColumns.getEnumDateColumnValueList(), true);
            }else if (taxpayerCaseSearchResultList != null){
                if (taxpayerCaseSearchResultList.isForWorkStatusSummary()){
                    aPeonySearchResultFilterDialog.launchPeonySearchResultFilterDialog("Taxpayer Work Status Summary", 
                                                                                        "Filter for Taxpayer Work Status Summary", 
                                                                                        TaxpayerWorkStatusSummaryColumns.getEnumDateColumnValueList(), true);
                }else{
                    aPeonySearchResultFilterDialog.launchPeonySearchResultFilterDialog("Taxpayer Search Result", 
                                                                                        "Filter for Taxpayer Search Result", 
                                                                                        TaxpayerCaseSearchResultColumns.getEnumDateColumnValueList(), true);
                }
            }else if (taxpayerCaseSearchCacheResultList != null){
                aPeonySearchResultFilterDialog.launchPeonySearchResultFilterDialog("Taxpayer Search Result", 
                                                                                    "Filter for Taxpayer Search Result", 
                                                                                    TaxpayerCaseSearchCacheResultColumns.getEnumDateColumnValueList(), true);
            }else if (taxpayerCaseStatusCacheResultList != null){
                aPeonySearchResultFilterDialog.launchPeonySearchResultFilterDialog("Taxpayer Search Result", 
                                                                                    "Filter for Taxpayer Search Result", 
                                                                                    TaxpayerCaseSearchResultColumns.getEnumDateColumnValueList(), true);
            }else if (customerSearchResultList != null){
                aPeonySearchResultFilterDialog.launchPeonySearchResultFilterDialog("Customer Search Result", 
                                                                                    "Filter for Customer Search Result", 
                                                                                    CustomerSearchResultColumns.getEnumDateColumnValueList(), true);
            }else{
                aPeonySearchResultFilterDialog.launchPeonySearchResultFilterDialog("Search Result", 
                                                                                    "Filter for Search Result", 
                                                                                    new ArrayList<>(), true);
            }
            
            //filterSearchResultTable();
        });
        resetButton.setOnAction((ActionEvent event) -> {
            resetSearchResultTable();
        });
        
        sendSmsButton.setTooltip(new Tooltip("Send SMS-message to contactors in current filtered search result."));
        sendSmsButton.setOnAction((ActionEvent event) -> {
            List<G02BusinessContactor> contactors = new ArrayList<>();
            if (filteredTaxcorpTaxFilingCaseSearchResultList != null){
                for (TaxcorpTaxFilingCaseSearchResult aTaxcorpTaxFilingCaseSearchResult : filteredTaxcorpTaxFilingCaseSearchResultList){
                    contactors.addAll(aTaxcorpTaxFilingCaseSearchResult.getBusinessContactorList());
                }
                if (!contactors.isEmpty()){
                    popupSmsDialogForBusinessContactors(contactors);
                }
            }else if (filteredTaxcorpCaseSearchResultList != null){
                for (TaxcorpCaseSearchResult aTaxcorpCaseSearchResult : filteredTaxcorpCaseSearchResultList){
                    contactors.addAll(aTaxcorpCaseSearchResult.getBusinessContactorList());
                }
                if (!contactors.isEmpty()){
                    popupSmsDialogForBusinessContactors(contactors);
                }
            }else if (filteredTaxpayerCaseSearchResultList != null){
                List<G02TaxpayerInfo> taxpayerInfoList = new ArrayList<>();
                for (TaxpayerCaseSearchResult aTaxpayerCaseSearchResult : filteredTaxpayerCaseSearchResultList){
                    taxpayerInfoList.addAll(aTaxpayerCaseSearchResult.getTaxpayerInfoList());
                }
                if (!taxpayerInfoList.isEmpty()){
                    popupSmsDialogForTaxpayerInfos(taxpayerInfoList);
                }
            }else if (filteredTaxpayerCaseSearchCacheResultList != null){
////                List<G02TaxpayerInfo> taxpayerInfoList = new ArrayList<>();
////                for (TaxpayerCaseSearchCacheResult aTaxpayerCaseSearchCacheResult : filteredTaxpayerCaseSearchCacheResultList){
////                    taxpayerInfoList.addAll(aTaxpayerCaseSearchCacheResult.getTaxpayerInfoList());
////                }
////                if (!taxpayerInfoList.isEmpty()){
////                    popupSmsDialogForTaxpayerInfos(taxpayerInfoList);
////                }
            }else if (filteredCustomerSearchResultList != null){
                for (CustomerSearchResult aCustomerSearchResult : filteredCustomerSearchResultList){
                    contactors.add(aCustomerSearchResult.getBusinessContactor());
                }
                if (!contactors.isEmpty()){
                    popupSmsDialogForBusinessContactors(contactors);
                }
            }
        });
        
        exportButton.setTooltip(new Tooltip("Export current filtered search result into a TEXT file for print."));
        exportButton.setOnAction((ActionEvent event) -> {
            
            if (taxcorpTaxFilingCaseSearchResultList != null){
                exportToExcelFile(taxcorpTaxFilingSearchResultTable);
            }else if (customerSearchResultList != null){
                exportToExcelFile(customerSearchResultTable);
            }else if (taxcorpCaseSearchResultList != null){
                exportToExcelFile(taxcorpSearchResultTable);
            }else if (taxpayerCaseSearchResultList != null){
                exportToExcelFile(taxpayerSearchResultTable);
            }else if (taxpayerCaseSearchCacheResultList != null){
                exportToExcelFile(taxpayerCacheSearchResultTable);
            }else if (taxpayerCaseStatusCacheResultList != null){
                exportToExcelFile(taxpayerStatusCacheSearchResultTable);
            }
        });
    }
    
////    private void resetFilterControls(List<String> filterColumnValueList){
////        filterColumnComboBox.getItems().clear();
////        PeonyFaceUtils.initializeComboBox(filterColumnComboBox, filterColumnValueList, 
////                null, null, "Select which column for filter.", null);
////        PeonyFaceUtils.initializeTextField(filterValueField, null, null, "Keywords used for filiter", null);
////        PeonyFaceUtils.initializeDatePicker(filterFromDatePicker, null, null, "If selected-column is date, this is the starting date of a period", null);
////        PeonyFaceUtils.initializeDatePicker(filterToDatePicker, null, null, "If selected-column is date, thisis the ending date of a period", null);
////        PeonyFaceUtils.initializeCheckBox(exactMatchCheckBox, false, "Whether or not exactly match filter's keywords with the column value", null);
////    }

    private void resetSearchResultTable(){
        if (taxcorpTaxFilingCaseSearchResultList != null){
            resetTaxcorpTaxFilingCaseSearchResultList();
        }else if (customerSearchResultList != null){
            resetCustomerSearchResultList();
        }else if (taxcorpCaseSearchResultList != null){
            resetTaxcorpCaseSearchResultList();
        }else if (taxpayerCaseSearchResultList != null){
            resetTaxpayerCaseSearchResultList();
        }else if (taxpayerCaseSearchCacheResultList != null){
            resetTaxpayerCaseSearchCacheResultList();
        }else if (taxpayerCaseStatusCacheResultList != null){
            resetTaxpayerCaseStatusCacheResultList();
        }
    }
    
    private void initializeSearchResultTableForTaxcorpTaxFilingCaseSearchResultList(){
        
        taxcorpTaxFilingSearchResultTable.getColumns().clear();
        
        TableColumn detailsTableColumn = new TableColumn("");
        detailsTableColumn.setCellFactory(PeonyButtonTableCell.<TaxcorpTaxFilingCaseSearchResult>callbackForTableColumn("", 
                FontAwesome.Glyph.PENCIL_SQUARE_ALT, Color.DARKBLUE, 
                new Tooltip("Details"), (TaxcorpTaxFilingCaseSearchResult aTaxcorpTaxFilingCaseSearchResult) -> {
                    popupTaxcorpCaseSearchResultTab(aTaxcorpTaxFilingCaseSearchResult.getTaxcorpCase().getEinNumber());
                    return aTaxcorpTaxFilingCaseSearchResult;
                }));
        detailsTableColumn.setPrefWidth(50.00);
        taxcorpTaxFilingSearchResultTable.getColumns().add(detailsTableColumn);
        
        TableColumn smsTableColumn = new TableColumn("");
        smsTableColumn.setCellFactory(PeonyButtonTableCell.<TaxcorpTaxFilingCaseSearchResult>callbackForTableColumn(
                "", FontAwesome.Glyph.SEND_ALT, Color.DARKBLUE, new Tooltip("SMS"), 
                (TaxcorpTaxFilingCaseSearchResult aTaxcorpTaxFilingCaseSearchResult) -> {
                    popupSmsDialogForBusinessContactors(aTaxcorpTaxFilingCaseSearchResult.getBusinessContactorList());
                    return aTaxcorpTaxFilingCaseSearchResult;
                }));
        smsTableColumn.setPrefWidth(50.00);
        taxcorpTaxFilingSearchResultTable.getColumns().add(smsTableColumn);
        
        TableColumn<TaxcorpTaxFilingCaseSearchResult, Number> indexColumn = new TableColumn<>("#");
        indexColumn.setSortable(false);
        indexColumn.setCellValueFactory(column-> new ReadOnlyObjectWrapper<>(taxcorpTaxFilingSearchResultTable.getItems().indexOf(column.getValue())+1));
        taxcorpTaxFilingSearchResultTable.getColumns().add(indexColumn);
        
        TableColumn<TaxcorpTaxFilingCaseSearchResult, String> companyNameColumn = new TableColumn<>(TaxcorpTaxFilingCaseSearchResultColumns.COMPANY_NAME.value());
        companyNameColumn.setCellValueFactory(new PropertyValueFactory<>(TaxcorpTaxFilingCaseSearchResultColumns.getParamDescription(TaxcorpTaxFilingCaseSearchResultColumns.COMPANY_NAME)));
        companyNameColumn.setPrefWidth(200.00);
        taxcorpTaxFilingSearchResultTable.getColumns().add(companyNameColumn);
        
        TableColumn<TaxcorpTaxFilingCaseSearchResult, String> stateNameColumn = new TableColumn<>(TaxcorpTaxFilingCaseSearchResultColumns.TAX_STATE.value());
        stateNameColumn.setCellValueFactory(new PropertyValueFactory<>(TaxcorpTaxFilingCaseSearchResultColumns.getParamDescription(TaxcorpTaxFilingCaseSearchResultColumns.TAX_STATE)));
        taxcorpTaxFilingSearchResultTable.getColumns().add(stateNameColumn);
        
        TableColumn<TaxcorpTaxFilingCaseSearchResult, String> taxFilingMemoColumn = new TableColumn<>(TaxcorpTaxFilingCaseSearchResultColumns.TAX_FILING_MEMO.value());
        taxFilingMemoColumn.setCellValueFactory(new PropertyValueFactory<>(TaxcorpTaxFilingCaseSearchResultColumns.getParamDescription(TaxcorpTaxFilingCaseSearchResultColumns.TAX_FILING_MEMO)));
        taxFilingMemoColumn.prefWidthProperty().setValue(200.0);
        taxcorpTaxFilingSearchResultTable.getColumns().add(taxFilingMemoColumn);
        
////        searchResultTable.setRowFactory(row -> new TableRow<TaxcorpTaxFilingCaseSearchResult>(){
////            @Override
////            protected void updateItem(TaxcorpTaxFilingCaseSearchResult item, boolean empty) {
////                super.updateItem(item, empty);
////                if ((item != null) && (!empty) 
////                        && (((item.getEFiledDate()!= null) && (item.getExtensionDate()== null))
////                            || ((item.getExtensionDate()!= null) && (item.getExtensionEFiledDate()!= null))))
////                {
////                    getStyleClass().add("peony-eFiled-record-in-table");
////                }else{
////                    getStyleClass().remove("peony-eFiled-record-in-table");
////                }
////            }
////        });
        /**
         * Double-click memo field to popup memo-editor
         */
        taxcorpTaxFilingSearchResultTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2){
                ObservableList<TablePosition> posList = taxcorpTaxFilingSearchResultTable.getSelectionModel().getSelectedCells();
                if (posList != null){
                    for (TablePosition pos : posList){
                        TableColumn<TaxcorpTaxFilingCaseSearchResult, String> column = pos.getTableColumn();
                        if (TaxcorpTaxFilingCaseSearchResultColumns.TAX_FILING_MEMO.value().equalsIgnoreCase(column.getText())){
                            Object obj = taxcorpTaxFilingSearchResultTable.getSelectionModel().getSelectedItem();
                            if (obj instanceof TaxcorpTaxFilingCaseSearchResult){
                                TaxcorpTaxFilingCaseSearchResult aTaxcorpTaxFilingCaseSearchResult = ((TaxcorpTaxFilingCaseSearchResult)obj);
                                MemoDataEntryDialog aMemoDataEntryDialog = new MemoDataEntryDialog(null, true);
                                aMemoDataEntryDialog.addPeonyFaceEventListener(this);
                                PeonyMemo memo = new PeonyMemo();
                                memo.getMemo().setMemo(aTaxcorpTaxFilingCaseSearchResult.getPeonyTaxFilingCase().getTaxFilingCase().getMemo());
                                aMemoDataEntryDialog.launchMemoDataEntryDialog("Memo: ", memo, true, false, true, 
                                        aTaxcorpTaxFilingCaseSearchResult.getPeonyTaxFilingCase());
                            }
                            break;
                        }
                    }//for
                }
            }
        });
        
        TableColumn<TaxcorpTaxFilingCaseSearchResult, String> balanceColumn = new TableColumn<>(TaxcorpTaxFilingCaseSearchResultColumns.BALANCE.value());
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>(TaxcorpTaxFilingCaseSearchResultColumns.getParamDescription(TaxcorpTaxFilingCaseSearchResultColumns.BALANCE)));
        balanceColumn.setPrefWidth(125.00);
        taxcorpTaxFilingSearchResultTable.getColumns().add(balanceColumn);
        
        TableColumn<TaxcorpTaxFilingCaseSearchResult, String> statusColumn = new TableColumn<>(TaxcorpTaxFilingCaseSearchResultColumns.STATUS.value());
        statusColumn.setCellValueFactory(new PropertyValueFactory<>(TaxcorpTaxFilingCaseSearchResultColumns.getParamDescription(TaxcorpTaxFilingCaseSearchResultColumns.STATUS)));
        taxcorpTaxFilingSearchResultTable.getColumns().add(statusColumn);
        
        TableColumn<TaxcorpTaxFilingCaseSearchResult, Date> deadlineColumn = new TableColumn<>(TaxcorpTaxFilingCaseSearchResultColumns.DEADLINE.value());
        deadlineColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getDeadline()));
        deadlineColumn.setCellFactory((TableColumn<TaxcorpTaxFilingCaseSearchResult, Date> param) -> new TaxcorpTaxFilingCaseSearchResultTableCell());
        deadlineColumn.setOnEditCommit(
            (TableColumn.CellEditEvent<TaxcorpTaxFilingCaseSearchResult, Date> newDatePickingValue) -> {
                saveDeadlineDate(((TaxcorpTaxFilingCaseSearchResult)newDatePickingValue.getTableView().getItems()
                .get(newDatePickingValue.getTablePosition().getRow())).getPeonyTaxFilingCase(), newDatePickingValue.getNewValue());
            });
        taxcorpTaxFilingSearchResultTable.getColumns().add(deadlineColumn);
        
        TableColumn<TaxcorpTaxFilingCaseSearchResult, Date> extensionColumn = new TableColumn<>(TaxcorpTaxFilingCaseSearchResultColumns.EXTENSION.value());
        extensionColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getExtensionDate()));
        extensionColumn.setCellFactory((TableColumn<TaxcorpTaxFilingCaseSearchResult, Date> param) -> new TaxcorpTaxFilingCaseSearchResultTableCell());
        extensionColumn.setOnEditCommit(
            (TableColumn.CellEditEvent<TaxcorpTaxFilingCaseSearchResult, Date> newDatePickingValue) -> {
                saveDeadlineExtensionDate(((TaxcorpTaxFilingCaseSearchResult)newDatePickingValue.getTableView().getItems()
                .get(newDatePickingValue.getTablePosition().getRow())).getPeonyTaxFilingCase(), newDatePickingValue.getNewValue());
            });
        taxcorpTaxFilingSearchResultTable.getColumns().add(extensionColumn);
        
        TableColumn<TaxcorpTaxFilingCaseSearchResult, Date> receivedColumn = new TableColumn<>(TaxcorpTaxFilingCaseSearchResultColumns.RECEIEVED.value());
        receivedColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getReceivedDate()));
        receivedColumn.setCellFactory((TableColumn<TaxcorpTaxFilingCaseSearchResult, Date> param) -> new TaxFilingStatusDateEditingCellForSearch());
        receivedColumn.setOnEditCommit(
            (TableColumn.CellEditEvent<TaxcorpTaxFilingCaseSearchResult, Date> newDatePickingValue) -> {
                saveTaxFilingStatusDate(((TaxcorpTaxFilingCaseSearchResult)newDatePickingValue.getTableView().getItems()
                .get(newDatePickingValue.getTablePosition().getRow())).getPeonyTaxFilingCase(), newDatePickingValue.getNewValue(), GardenTaxFilingStatus.RECEIVED);
            });
        taxcorpTaxFilingSearchResultTable.getColumns().add(receivedColumn);
        
        TableColumn<TaxcorpTaxFilingCaseSearchResult, Date> preparedColumn = new TableColumn<>(TaxcorpTaxFilingCaseSearchResultColumns.PREPARED.value());
        preparedColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getPreparedDate()));
        preparedColumn.setCellFactory((TableColumn<TaxcorpTaxFilingCaseSearchResult, Date> param) -> new TaxFilingStatusDateEditingCellForSearch());
        preparedColumn.setOnEditCommit(
            (TableColumn.CellEditEvent<TaxcorpTaxFilingCaseSearchResult, Date> newDatePickingValue) -> {
                saveTaxFilingStatusDate(((TaxcorpTaxFilingCaseSearchResult)newDatePickingValue.getTableView().getItems()
                .get(newDatePickingValue.getTablePosition().getRow())).getPeonyTaxFilingCase(), newDatePickingValue.getNewValue(), GardenTaxFilingStatus.PREPARED);
            });
        taxcorpTaxFilingSearchResultTable.getColumns().add(preparedColumn);
        
        TableColumn<TaxcorpTaxFilingCaseSearchResult, Date> completedColumn = new TableColumn<>(TaxcorpTaxFilingCaseSearchResultColumns.COMPLETED.value());
        completedColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getCompletedDate()));
        completedColumn.setCellFactory((TableColumn<TaxcorpTaxFilingCaseSearchResult, Date> param) -> new TaxFilingStatusDateEditingCellForSearch());
        completedColumn.setOnEditCommit(
            (TableColumn.CellEditEvent<TaxcorpTaxFilingCaseSearchResult, Date> newDatePickingValue) -> {
                saveTaxFilingStatusDate(((TaxcorpTaxFilingCaseSearchResult)newDatePickingValue.getTableView().getItems()
                .get(newDatePickingValue.getTablePosition().getRow())).getPeonyTaxFilingCase(), newDatePickingValue.getNewValue(), GardenTaxFilingStatus.COMPLETED);
            });
        taxcorpTaxFilingSearchResultTable.getColumns().add(completedColumn);
        
        TableColumn<TaxcorpTaxFilingCaseSearchResult, Date> eFiledColumn = new TableColumn<>(TaxcorpTaxFilingCaseSearchResultColumns.EFILED.value());
        eFiledColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getEFiledDate()));
        eFiledColumn.setCellFactory((TableColumn<TaxcorpTaxFilingCaseSearchResult, Date> param) -> new TaxFilingStatusDateEditingCellForSearch());
        eFiledColumn.setOnEditCommit(
            (TableColumn.CellEditEvent<TaxcorpTaxFilingCaseSearchResult, Date> newDatePickingValue) -> {
                saveTaxFilingStatusDate(((TaxcorpTaxFilingCaseSearchResult)newDatePickingValue.getTableView().getItems()
                .get(newDatePickingValue.getTablePosition().getRow())).getPeonyTaxFilingCase(), newDatePickingValue.getNewValue(), GardenTaxFilingStatus.PICKUP);
            });
        taxcorpTaxFilingSearchResultTable.getColumns().add(eFiledColumn);
        if (TaxFilingType.TAX_RETURN.name().equalsIgnoreCase(taxcorpTaxFilingCaseSearchResultList.getTaxFilingTypeCriteria())){
            TableColumn<TaxcorpTaxFilingCaseSearchResult, Date> extEFiledColumn = new TableColumn<>(TaxcorpTaxFilingCaseSearchResultColumns.EXT_EFILED.value());
            extEFiledColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getExtensionEFiledDate()));
            extEFiledColumn.setCellFactory((TableColumn<TaxcorpTaxFilingCaseSearchResult, Date> param) -> new TaxFilingStatusDateEditingCellForSearch());
            extEFiledColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<TaxcorpTaxFilingCaseSearchResult, Date> newDatePickingValue) -> {
                    saveTaxFilingStatusDate(((TaxcorpTaxFilingCaseSearchResult)newDatePickingValue.getTableView().getItems()
                    .get(newDatePickingValue.getTablePosition().getRow())).getPeonyTaxFilingCase(), newDatePickingValue.getNewValue(), GardenTaxFilingStatus.EXT_EFILE);
                });
            taxcorpTaxFilingSearchResultTable.getColumns().add(extEFiledColumn);
        }
        
        TableColumn<TaxcorpTaxFilingCaseSearchResult, String> taxFilingColumn = new TableColumn<>(TaxcorpTaxFilingCaseSearchResultColumns.TAX_FILING_TEXT.value());
        taxFilingColumn.setCellValueFactory(new PropertyValueFactory<>(TaxcorpTaxFilingCaseSearchResultColumns.getParamDescription(TaxcorpTaxFilingCaseSearchResultColumns.TAX_FILING_TEXT)));
        taxcorpTaxFilingSearchResultTable.getColumns().add(taxFilingColumn);
        
        TableColumn<TaxcorpTaxFilingCaseSearchResult, String> contactInfoColumn = new TableColumn<>(TaxcorpTaxFilingCaseSearchResultColumns.CONTACT_INFORMATION.value());
        contactInfoColumn.setCellValueFactory(new PropertyValueFactory<>(TaxcorpTaxFilingCaseSearchResultColumns.getParamDescription(TaxcorpTaxFilingCaseSearchResultColumns.CONTACT_INFORMATION)));
        taxcorpTaxFilingSearchResultTable.getColumns().add(contactInfoColumn);
        
        taxcorpTaxFilingSearchResultTable.getSelectionModel().setCellSelectionEnabled(true);
        taxcorpTaxFilingSearchResultTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        GardenFaceX.installCopyPasteHandler(taxcorpTaxFilingSearchResultTable);
        
        setupSearchResultTable();
    }
    
    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof PeonyTaxFilingCaseUpdated){
            handlePeonyTaxFilingCaseUpdated((PeonyTaxFilingCaseUpdated)event);
        }else if (event instanceof SearchResultColumnTextCriteraCreated){
            handleSearchResultColumnTextCriteraCreated((SearchResultColumnTextCriteraCreated)event);
        }else if (event instanceof SearchResultColumnDateCriteraCreated){
            handleSearchResultColumnDateCriteraCreated((SearchResultColumnDateCriteraCreated)event);
        }else if (event instanceof TaxpayerCaseStatusUpdated){
            handleTaxpayerCaseStatusUpdated((TaxpayerCaseStatusUpdated)event);
        }
    }
    
    private void handlePeonyTaxFilingCaseUpdated(final PeonyTaxFilingCaseUpdated event){
        if (Platform.isFxApplicationThread()){
            taxcorpTaxFilingSearchResultTable.refresh();
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    taxcorpTaxFilingSearchResultTable.refresh();
                }
            });
        }
    }
    
//////    private TableCell<TaxcorpTaxFilingCaseSearchResult, Date> createDateTableCell(){
//////        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
//////        TableCell<TaxcorpTaxFilingCaseSearchResult, Date> aDateTableCell = new TableCell<TaxcorpTaxFilingCaseSearchResult, Date>(){
//////            @Override
//////            protected void updateItem(Date item, boolean empty) {
//////                super.updateItem(item, empty);
//////                if((item == null)||(empty)) {
//////                    setText(null);
//////                }
//////                else {
//////                    if (item != null){
//////                        this.setText(format.format(item));
//////                    }
//////                }
//////            }
//////        };
//////        return aDateTableCell;
//////    }
    
    private void setupSearchResultTable(){
        if (taxcorpTaxFilingCaseSearchResultList != null){
            setupSearchResultTableForTaxFilingCaseSearchResultList();
        }else if (customerSearchResultList != null){
            setupSearchResultTableForCustomerSearchResultList();
        }else if (taxcorpCaseSearchResultList != null){
            setupSearchResultTableForTaxcorpCaseSearchResultList();
        }else if (taxpayerCaseSearchResultList != null){
            setupSearchResultTableForTaxpayerCaseSearchResultList();
        }else if (taxpayerCaseSearchCacheResultList != null){
            setupSearchResultTableForTaxpayerCaseSearchCacheResultList();
        }else if (taxpayerCaseStatusCacheResultList != null){
            setupSearchResultTableForTaxpayerCaseStatusCacheResultList();
        }
    }

    private void filterTaxcorpTaxFilingCaseSearchResultListByText(final SearchResultColumnTextCriteraCreated criteria) {
        Task<List<TaxcorpTaxFilingCaseSearchResult>> filterTaxcorpTaxFilingCaseSearchResultListTask = new Task<List<TaxcorpTaxFilingCaseSearchResult>>(){
            @Override
            protected List<TaxcorpTaxFilingCaseSearchResult> call() throws Exception {
                filteredTaxcorpTaxFilingCaseSearchResultList.clear();
                List<TaxcorpTaxFilingCaseSearchResult> aTaxcorpTaxFilingCaseSearchResultList = taxcorpTaxFilingCaseSearchResultList.getTaxcorpTaxFilingCaseSearchResultList();
                try{
                    if (ZcaValidator.isNullEmpty(criteria.getSearchResultColumn())){
                        throw new Exception("Please select a column for filter.");
                    }
                    if (ZcaValidator.isNullEmpty(criteria.getSearchResultColumnCriteriaValue())){
                        throw new Exception("Please type into keywords for filter.");
                    }
                    for (TaxcorpTaxFilingCaseSearchResult aTaxcorpTaxFilingCaseSearchResult : aTaxcorpTaxFilingCaseSearchResultList){
                        if (GardenData.calculateSimilarityByJaroWinklerStrategy(getTargetValue(aTaxcorpTaxFilingCaseSearchResult), 
                                                                                criteria.getSearchResultColumnCriteriaValue(), criteria.isExactMatch(), 0.75) > 0.75)
                        {
                            filteredTaxcorpTaxFilingCaseSearchResultList.add(aTaxcorpTaxFilingCaseSearchResult);
                        }
                    }//for
                }catch (Exception ex){
                    updateMessage(ex.getMessage());
                    return null;
                }
                return filteredTaxcorpTaxFilingCaseSearchResultList;
            }

            @Override
            protected void succeeded() {
                try {
                    List<TaxcorpTaxFilingCaseSearchResult> result = get();
                    if (result == null){
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }else{
                        setupSearchResultTable();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception was raised. " + ex.getMessage());
                }
            }

            private String getTargetValue(TaxcorpTaxFilingCaseSearchResult aTaxcorpTaxFilingCaseSearchResult) {
                TaxcorpTaxFilingCaseSearchResultColumns column = TaxcorpTaxFilingCaseSearchResultColumns.convertEnumValueToType(criteria.getSearchResultColumn());
                switch (column){
                    case COMPANY_NAME:
                        return aTaxcorpTaxFilingCaseSearchResult.getCompanyName();
                    case TAX_STATE:
                        return aTaxcorpTaxFilingCaseSearchResult.getTaxState();
                    case TAX_FILING_TEXT:
                        return aTaxcorpTaxFilingCaseSearchResult.getTaxFilingText();
                    case TAX_FILING_MEMO:
                        return aTaxcorpTaxFilingCaseSearchResult.getTaxFilingMemo();
                    case CONTACT_INFORMATION:
                        return aTaxcorpTaxFilingCaseSearchResult.getContactInformation();
                    default:
                        return null;
                }
            }
        };
        this.getCachedThreadPoolExecutorService().submit(filterTaxcorpTaxFilingCaseSearchResultListTask);
    }

    private void filterTaxcorpTaxFilingCaseSearchResultListByDate(final SearchResultColumnDateCriteraCreated criteria) {
        Task<List<TaxcorpTaxFilingCaseSearchResult>> filterTaxcorpTaxFilingCaseSearchResultListTask = new Task<List<TaxcorpTaxFilingCaseSearchResult>>(){
            @Override
            protected List<TaxcorpTaxFilingCaseSearchResult> call() throws Exception {
                filteredTaxcorpTaxFilingCaseSearchResultList.clear();
                List<TaxcorpTaxFilingCaseSearchResult> aTaxcorpTaxFilingCaseSearchResultList = taxcorpTaxFilingCaseSearchResultList.getTaxcorpTaxFilingCaseSearchResultList();
                try{
                    if (ZcaValidator.isNullEmpty(criteria.getSearchResultColumn())){
                        throw new Exception("Please select a column for filter.");
                    }
                    if ((criteria.getFromDate() == null) || (criteria.getToDate()== null)){
                        throw new Exception("Please select a date range, from and to, for filter.");
                    }
                    Date from = ZcaCalendar.convertToDate(criteria.getFromDate());
                    Date to = ZcaCalendar.convertToDate(criteria.getToDate());
                    Date dateFieldValue;
                    for (TaxcorpTaxFilingCaseSearchResult aTaxcorpTaxFilingCaseSearchResult : aTaxcorpTaxFilingCaseSearchResultList){
                        dateFieldValue = getTargetDate(aTaxcorpTaxFilingCaseSearchResult, criteria);
                        if (dateFieldValue != null){
                            if ((dateFieldValue.equals(to)) || (dateFieldValue.equals(from)) 
                                    || (dateFieldValue.after(from) && dateFieldValue.before(to)))
                            {
                                filteredTaxcorpTaxFilingCaseSearchResultList.add(aTaxcorpTaxFilingCaseSearchResult);
                            }
                        }
                    }//for
                }catch (Exception ex){
                    updateMessage(ex.getMessage());
                    return null;
                }
                return filteredTaxcorpTaxFilingCaseSearchResultList;
            }

            @Override
            protected void succeeded() {
                try {
                    List<TaxcorpTaxFilingCaseSearchResult> result = get();
                    if (result == null){
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }else{
                        setupSearchResultTable();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception was raised. " + ex.getMessage());
                }
            }
            
            private Date getTargetDate(TaxcorpTaxFilingCaseSearchResult aTaxcorpTaxFilingCaseSearchResult, SearchResultColumnDateCriteraCreated criteria){
                
                TaxcorpTaxFilingCaseSearchResultColumns column = TaxcorpTaxFilingCaseSearchResultColumns.convertEnumValueToType(criteria.getSearchResultColumn());
                switch (column){
                    case COMPLETED:
                        return aTaxcorpTaxFilingCaseSearchResult.getCompletedDate();
                    case DEADLINE:
                        return aTaxcorpTaxFilingCaseSearchResult.getDeadline();
                    case EFILED:
                        return aTaxcorpTaxFilingCaseSearchResult.getEFiledDate();
                    case EXT_EFILED:
                        return aTaxcorpTaxFilingCaseSearchResult.getExtensionEFiledDate();
                    case EXTENSION:
                        return aTaxcorpTaxFilingCaseSearchResult.getExtensionDate();
                    case PREPARED:
                        return aTaxcorpTaxFilingCaseSearchResult.getPreparedDate();
                    case RECEIEVED:
                        return aTaxcorpTaxFilingCaseSearchResult.getReceivedDate();
                    default:
                        return null;
                }
            }
        };
        this.getCachedThreadPoolExecutorService().submit(filterTaxcorpTaxFilingCaseSearchResultListTask);
    }

    private void resetTaxcorpTaxFilingCaseSearchResultList() {
        Task<List<TaxcorpTaxFilingCaseSearchResult>> resetTaxcorpTaxFilingCaseSearchResultListTask = new Task<List<TaxcorpTaxFilingCaseSearchResult>>(){
            @Override
            protected List<TaxcorpTaxFilingCaseSearchResult> call() throws Exception {
                filteredTaxcorpTaxFilingCaseSearchResultList.clear();
                resetFilterSeachResult();
                return filteredTaxcorpTaxFilingCaseSearchResultList;
            }

            @Override
            protected void succeeded() {
                try {
                    List<TaxcorpTaxFilingCaseSearchResult> result = get();
                    if (result != null){
                        setupSearchResultTable();
                        //resetFilterControls(TaxcorpTaxFilingCaseSearchResultColumns.getEnumValueList(false));
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception was raised. " + ex.getMessage());
                }
                
            }
        
        };
        this.getCachedThreadPoolExecutorService().submit(resetTaxcorpTaxFilingCaseSearchResultListTask);
    }

    private void popupTaxcorpCaseSearchResultTab(String einNumber) {
        if (ZcaValidator.isNullEmpty(einNumber)){
            PeonyFaceUtils.displayErrorMessageDialog("Cannot display this taxcorp because of no valid EIN existing.");
            return;
        }
        Lookup.getDefault().lookup(PeonyTaxcorpService.class).launchPeonyTaxcorpCaseTopComponentByEinNumber(einNumber);
    }

    private void popupCustomerSearchResultTab(String uuid) {
        if (ZcaValidator.isNullEmpty(uuid)){
            PeonyFaceUtils.displayErrorMessageDialog("Cannot display this customer profile because of no valid ID.");
            return;
        }
        Lookup.getDefault().lookup(PeonyCustomerService.class).launchCustomerProfileWindowByAccountUuid(uuid);
    }

    /**
     * @param taxpayerCaseUuid 
     */
    private void popupTaxpayerCaseSearchResultTab(String taxpayerCaseUuid) {
        if (ZcaValidator.isNullEmpty(taxpayerCaseUuid)){
            throw new GardenRuntimeException("TECH: the demanded taxpayerCaseUuid is not provided");
        }
        Lookup.getDefault().lookup(PeonyTaxpayerService.class).launchPeonyTaxpayerCaseTopComponentByTaxpayerCaseUuid(taxpayerCaseUuid);
    }

    private void initializeSearchResultTableForCustomerSearchResultList(){
        
        customerSearchResultTable.getColumns().clear();
        
        TableColumn detailsTableColumn = new TableColumn("");
        detailsTableColumn.setCellFactory(PeonyButtonTableCell.<CustomerSearchResult>callbackForTableColumn(
                "", FontAwesome.Glyph.PENCIL_SQUARE_ALT, Color.DARKBLUE, new Tooltip("Details"), 
                (CustomerSearchResult aCustomerSearchResult) -> {
                    popupCustomerSearchResultTab(aCustomerSearchResult.getAccount().getAccountUuid());
                    return aCustomerSearchResult;
                }));
        detailsTableColumn.setPrefWidth(50.00);
        customerSearchResultTable.getColumns().add(detailsTableColumn);
        
        TableColumn smsTableColumn = new TableColumn("");
        smsTableColumn.setCellFactory(PeonyButtonTableCell.<CustomerSearchResult>callbackForTableColumn(
                "", FontAwesome.Glyph.SEND_ALT, Color.DARKBLUE, new Tooltip("SMS"), 
                (CustomerSearchResult aCustomerSearchResult) -> {
                    popupSmsDialogForBusinessContactor(aCustomerSearchResult.getBusinessContactor());
                    return aCustomerSearchResult;
                }));
        smsTableColumn.setPrefWidth(50.00);
        customerSearchResultTable.getColumns().add(smsTableColumn);
        
////        TableColumn<CustomerSearchResult, String> loginNameColumn = new TableColumn<>(CustomerSearchResultColumns.LOGIN_NAME.value());
////        loginNameColumn.setCellValueFactory(new PropertyValueFactory<>(CustomerSearchResultColumns.getParamDescription(CustomerSearchResultColumns.LOGIN_NAME)));
////        searchResultTable.getColumns().addAll(loginNameColumn);
        
        TableColumn<CustomerSearchResult, Number> indexColumn = new TableColumn<>("#");
        indexColumn.setSortable(false);
        indexColumn.setCellValueFactory(column-> new ReadOnlyObjectWrapper<>(customerSearchResultTable.getItems().indexOf(column.getValue())+1));
        customerSearchResultTable.getColumns().add(indexColumn);
        
        TableColumn<CustomerSearchResult, String> ssnColumn = new TableColumn<>(CustomerSearchResultColumns.SSN.value());
        ssnColumn.setCellValueFactory(new PropertyValueFactory<>(CustomerSearchResultColumns.getParamDescription(CustomerSearchResultColumns.SSN)));
        customerSearchResultTable.getColumns().add(ssnColumn);
        
        TableColumn<CustomerSearchResult, String> firstNameColumn = new TableColumn<>(CustomerSearchResultColumns.FIRST_NAME.value());
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>(CustomerSearchResultColumns.getParamDescription(CustomerSearchResultColumns.FIRST_NAME)));
        customerSearchResultTable.getColumns().add(firstNameColumn);
        
        TableColumn<CustomerSearchResult, String> lastNameColumn = new TableColumn<>(CustomerSearchResultColumns.LAST_NAME.value());
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>(CustomerSearchResultColumns.getParamDescription(CustomerSearchResultColumns.LAST_NAME)));
        customerSearchResultTable.getColumns().add(lastNameColumn);
        
////        TableColumn<CustomerSearchResult, String> accountEmailColumn = new TableColumn<>(CustomerSearchResultColumns.ACCOUNT_EMAIL.value());
////        accountEmailColumn.setCellValueFactory(new PropertyValueFactory<>(CustomerSearchResultColumns.getParamDescription(CustomerSearchResultColumns.ACCOUNT_EMAIL)));
////        searchResultTable.getColumns().add(accountEmailColumn);
        
        TableColumn<CustomerSearchResult, String> mobilePhoneColumn = new TableColumn<>(CustomerSearchResultColumns.MOBILE_PHONE.value());
        mobilePhoneColumn.setCellValueFactory(new PropertyValueFactory<>(CustomerSearchResultColumns.getParamDescription(CustomerSearchResultColumns.MOBILE_PHONE)));
        customerSearchResultTable.getColumns().add(mobilePhoneColumn);
        
        setupSearchResultTable();
    }
    
    private void initializeSearchResultTableForTaxcorpCaseSearchResultList() {
        
        taxcorpSearchResultTable.getColumns().clear();
        
        TableColumn detailsTableColumn = new TableColumn("");
        detailsTableColumn.setCellFactory(PeonyButtonTableCell.<TaxcorpCaseSearchResult>callbackForTableColumn(
                "", FontAwesome.Glyph.PENCIL_SQUARE_ALT, Color.DARKBLUE, new Tooltip("Details"), 
                (TaxcorpCaseSearchResult aTaxcorpCaseSearchResult) -> {
                    popupTaxcorpCaseSearchResultTab(aTaxcorpCaseSearchResult.getTaxcorpCase().getEinNumber());
                    return aTaxcorpCaseSearchResult;
                }));
        detailsTableColumn.setPrefWidth(50.00);
        taxcorpSearchResultTable.getColumns().add(detailsTableColumn);
        
        TableColumn smsTableColumn = new TableColumn("");
        smsTableColumn.setCellFactory(PeonyButtonTableCell.<TaxcorpCaseSearchResult>callbackForTableColumn(
                "", FontAwesome.Glyph.SEND_ALT, Color.DARKBLUE, new Tooltip("SMS"), 
                (TaxcorpCaseSearchResult aTaxcorpCaseSearchResult) -> {
                    popupSmsDialogForBusinessContactors(aTaxcorpCaseSearchResult.getBusinessContactorList());
                    return aTaxcorpCaseSearchResult;
                }));
        smsTableColumn.setPrefWidth(50.00);
        taxcorpSearchResultTable.getColumns().add(smsTableColumn);
        
        TableColumn<TaxcorpCaseSearchResult, Number> indexColumn = new TableColumn<>("#");
        indexColumn.setSortable(false);
        indexColumn.setCellValueFactory(column-> new ReadOnlyObjectWrapper<>(taxcorpSearchResultTable.getItems().indexOf(column.getValue())+1));
        taxcorpSearchResultTable.getColumns().add(indexColumn);
        
        TableColumn<TaxcorpCaseSearchResult, String> companyNameColumn = new TableColumn<>(TaxcorpCaseSearchResultColumns.COMPANY_NAME.value());
        companyNameColumn.setCellValueFactory(new PropertyValueFactory<>(TaxcorpCaseSearchResultColumns.getParamDescription(TaxcorpCaseSearchResultColumns.COMPANY_NAME)));
        taxcorpSearchResultTable.getColumns().add(companyNameColumn);
        
        TableColumn<TaxcorpCaseSearchResult, String> businessTypeColumn = new TableColumn<>(TaxcorpCaseSearchResultColumns.BUSINESS_TYPE.value());
        businessTypeColumn.setCellValueFactory(new PropertyValueFactory<>(TaxcorpCaseSearchResultColumns.getParamDescription(TaxcorpCaseSearchResultColumns.BUSINESS_TYPE)));
        taxcorpSearchResultTable.getColumns().add(businessTypeColumn);
        
        TableColumn<TaxcorpCaseSearchResult, String> einNumberColumn = new TableColumn<>(TaxcorpCaseSearchResultColumns.EIN_NUMBER.value());
        einNumberColumn.setCellValueFactory(new PropertyValueFactory<>(TaxcorpCaseSearchResultColumns.getParamDescription(TaxcorpCaseSearchResultColumns.EIN_NUMBER)));
        taxcorpSearchResultTable.getColumns().add(einNumberColumn);
        
        TableColumn<TaxcorpCaseSearchResult, String> dosDateColumn = new TableColumn<>(TaxcorpCaseSearchResultColumns.DOS_DATE.value());
        dosDateColumn.setCellValueFactory(new PropertyValueFactory<>(TaxcorpCaseSearchResultColumns.getParamDescription(TaxcorpCaseSearchResultColumns.DOS_DATE)));
        taxcorpSearchResultTable.getColumns().add(dosDateColumn);
        
        TableColumn<TaxcorpCaseSearchResult, String> stateNameColumn = new TableColumn<>(TaxcorpCaseSearchResultColumns.TAX_STATE.value());
        stateNameColumn.setCellValueFactory(new PropertyValueFactory<>(TaxcorpCaseSearchResultColumns.getParamDescription(TaxcorpCaseSearchResultColumns.TAX_STATE)));
        taxcorpSearchResultTable.getColumns().add(stateNameColumn);
        
        TableColumn<TaxcorpCaseSearchResult, String> contactInfoColumn = new TableColumn<>(TaxcorpCaseSearchResultColumns.CONTACT_INFORMATION.value());
        contactInfoColumn.setCellValueFactory(new PropertyValueFactory<>(TaxcorpCaseSearchResultColumns.getParamDescription(TaxcorpCaseSearchResultColumns.CONTACT_INFORMATION)));
        taxcorpSearchResultTable.getColumns().add(contactInfoColumn);
        
        TableColumn<TaxcorpCaseSearchResult, String> statusColumn = new TableColumn<>(TaxcorpCaseSearchResultColumns.STATUS.value());
        statusColumn.setCellValueFactory(new PropertyValueFactory<>(TaxcorpCaseSearchResultColumns.getParamDescription(TaxcorpCaseSearchResultColumns.STATUS)));
        taxcorpSearchResultTable.getColumns().add(statusColumn);
        //searchResultTable.getColumns().addAll(detailsTableColumn, smsTableColumn, companyNameColumn, businessTypeColumn, einNumberColumn, dosDateColumn, stateNameColumn, contactInfoColumn, statusColumn);
        
        setupSearchResultTable();
    }
    
    private void initializeSearchResultTableForTaxpayerCaseSearchResultList() {
////        if (taxpayerCaseSearchResultList.isForSearchByDataRange()){
////            initializeSearchResultTableForTaxpayerCaseSearchResultListForDateRange();
////        }else if (taxpayerCaseSearchResultList.isForWorkStatusSummary()){
////            initializeSearchResultTableForTaxpayerCaseSearchResultListForWorkStatusSummary();
////        }
        initializeSearchResultTableForTaxpayerCaseSearchResultListForDateRange();
    }
    
    private void initializeSearchResultTableForTaxpayerCaseStatusCacheResultList() {
        taxpayerStatusCacheSearchResultTable.getColumns().clear();
        
        TableColumn<TaxpayerCaseStatusCacheResult, Number> indexColumn = new TableColumn<>("#");
        indexColumn.setSortable(false);
        indexColumn.setCellValueFactory(column-> new ReadOnlyObjectWrapper<>(taxpayerStatusCacheSearchResultTable.getItems().indexOf(column.getValue())+1));
        taxpayerStatusCacheSearchResultTable.getColumns().add(indexColumn);
        
        TableColumn detailsTableColumn = new TableColumn("");
        detailsTableColumn.setCellFactory(PeonyButtonTableCell.<TaxpayerCaseStatusCacheResult>callbackForTableColumn(
                "", FontAwesome.Glyph.PENCIL_SQUARE_ALT, Color.DARKBLUE, new Tooltip("View details"), 
                (TaxpayerCaseStatusCacheResult aTaxpayerCaseStatusCacheResult) -> {
                    popupTaxpayerCaseSearchResultTab(aTaxpayerCaseStatusCacheResult.getTaxpayerCaseUuid());
                    return aTaxpayerCaseStatusCacheResult;
                }));
        detailsTableColumn.setPrefWidth(50.00);
        taxpayerStatusCacheSearchResultTable.getColumns().add(detailsTableColumn);
        
        TableColumn<TaxpayerCaseStatusCacheResult, String> ssnColumn = new TableColumn<>(TaxpayerCaseStatusReportColumns.SSN.value());
        ssnColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseStatusReportColumns.getParamDescription(TaxpayerCaseStatusReportColumns.SSN)));
        ssnColumn.setPrefWidth(100.00);
        Callback<TableColumn<TaxpayerCaseStatusCacheResult, String>, TableCell<TaxpayerCaseStatusCacheResult,String>> ssnExistingCellFactory  = ssnColumn.getCellFactory();
        ssnColumn.setCellFactory(c -> {
            TableCell<TaxpayerCaseStatusCacheResult, String> cell = ssnExistingCellFactory.call(c);
            Tooltip tooltip = new Tooltip(TaxpayerCaseStatusReportColumns.getParamNote(TaxpayerCaseStatusReportColumns.SSN));
            //tooltip.textProperty().bind(cell.itemProperty().asString());
            cell.setTooltip(tooltip);
            return cell ;
        });
        taxpayerStatusCacheSearchResultTable.getColumns().add(ssnColumn);
        
        TableColumn<TaxpayerCaseStatusCacheResult, String> taxpayerColumn = new TableColumn<>(TaxpayerCaseStatusReportColumns.TAXPAYER.value());
        taxpayerColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseStatusReportColumns.getParamDescription(TaxpayerCaseStatusReportColumns.TAXPAYER)));
        taxpayerColumn.setPrefWidth(100.00);
        Callback<TableColumn<TaxpayerCaseStatusCacheResult, String>, TableCell<TaxpayerCaseStatusCacheResult,String>> taxpayerExistingCellFactory  = taxpayerColumn.getCellFactory();
        taxpayerColumn.setCellFactory(c -> {
            TableCell<TaxpayerCaseStatusCacheResult, String> cell = taxpayerExistingCellFactory.call(c);
            Tooltip tooltip = new Tooltip(TaxpayerCaseStatusReportColumns.getParamNote(TaxpayerCaseStatusReportColumns.TAXPAYER));
            //tooltip.textProperty().bind(cell.itemProperty().asString());
            cell.setTooltip(tooltip);
            return cell ;
        });
        taxpayerStatusCacheSearchResultTable.getColumns().add(taxpayerColumn);
        
        TableColumn<TaxpayerCaseStatusCacheResult, String> spouseColumn = new TableColumn<>(TaxpayerCaseStatusReportColumns.SPOUSE.value());
        spouseColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseStatusReportColumns.getParamDescription(TaxpayerCaseStatusReportColumns.SPOUSE)));
        spouseColumn.setPrefWidth(100.00);
        Callback<TableColumn<TaxpayerCaseStatusCacheResult, String>, TableCell<TaxpayerCaseStatusCacheResult,String>> spouseExistingCellFactory  = spouseColumn.getCellFactory();
        spouseColumn.setCellFactory(c -> {
            TableCell<TaxpayerCaseStatusCacheResult, String> cell = spouseExistingCellFactory.call(c);
            Tooltip tooltip = new Tooltip(TaxpayerCaseStatusReportColumns.getParamNote(TaxpayerCaseStatusReportColumns.SPOUSE));
            //tooltip.textProperty().bind(cell.itemProperty().asString());
            cell.setTooltip(tooltip);
            return cell ;
        });
        taxpayerStatusCacheSearchResultTable.getColumns().add(spouseColumn);
        
        TableColumn<TaxpayerCaseStatusCacheResult, String> federalFilingStatusColumn = new TableColumn<>(TaxpayerCaseStatusReportColumns.FEDERAL_FILING_STATUS.value());
        federalFilingStatusColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseStatusReportColumns.getParamDescription(TaxpayerCaseStatusReportColumns.FEDERAL_FILING_STATUS)));
        Callback<TableColumn<TaxpayerCaseStatusCacheResult, String>, TableCell<TaxpayerCaseStatusCacheResult,String>> ffsExistingCellFactory  = federalFilingStatusColumn.getCellFactory();
        federalFilingStatusColumn.setCellFactory(c -> {
            TableCell<TaxpayerCaseStatusCacheResult, String> cell = ffsExistingCellFactory.call(c);
            Tooltip tooltip = new Tooltip(TaxpayerCaseStatusReportColumns.getParamNote(TaxpayerCaseStatusReportColumns.FEDERAL_FILING_STATUS));
            //tooltip.textProperty().bind(cell.itemProperty().asString());
            cell.setTooltip(tooltip);
            return cell ;
        });
        taxpayerStatusCacheSearchResultTable.getColumns().add(federalFilingStatusColumn);
        
        TableColumn<TaxpayerCaseStatusCacheResult, Date> deadlineColumn = new TableColumn<>(TaxpayerCaseStatusReportColumns.DEADLINE.value());
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseStatusReportColumns.getParamDescription(TaxpayerCaseStatusReportColumns.DEADLINE)));
        deadlineColumn.setCellFactory(column -> {
            SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
            TableCell<TaxpayerCaseStatusCacheResult, Date> aDateTableCell = new TableCell<TaxpayerCaseStatusCacheResult, Date>(){
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
        taxpayerStatusCacheSearchResultTable.getColumns().add(deadlineColumn);
        
        TableColumn<TaxpayerCaseStatusCacheResult, Date> extensionColumn = new TableColumn<>(TaxpayerCaseStatusReportColumns.EXTENSION.value());
        extensionColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseStatusReportColumns.getParamDescription(TaxpayerCaseStatusReportColumns.EXTENSION)));
        extensionColumn.setCellFactory(column -> {
            SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
            TableCell<TaxpayerCaseStatusCacheResult, Date> aDateTableCell = new TableCell<TaxpayerCaseStatusCacheResult, Date>(){
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
        taxpayerStatusCacheSearchResultTable.getColumns().add(extensionColumn);
        
        TableColumn<TaxpayerCaseStatusCacheResult, String> residencyColumn = new TableColumn<>(TaxpayerCaseStatusReportColumns.RESIDENCY.value());
        residencyColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseStatusReportColumns.getParamDescription(TaxpayerCaseStatusReportColumns.RESIDENCY)));
        residencyColumn.setPrefWidth(180.00);
        Callback<TableColumn<TaxpayerCaseStatusCacheResult, String>, TableCell<TaxpayerCaseStatusCacheResult,String>> residencyExistingCellFactory  = residencyColumn.getCellFactory();
        residencyColumn.setCellFactory(c -> {
            TableCell<TaxpayerCaseStatusCacheResult, String> cell = residencyExistingCellFactory.call(c);
            Tooltip tooltip = new Tooltip(TaxpayerCaseStatusReportColumns.getParamNote(TaxpayerCaseStatusReportColumns.RESIDENCY));
            //tooltip.textProperty().bind(cell.itemProperty().asString());
            cell.setTooltip(tooltip);
            return cell ;
        });
        taxpayerStatusCacheSearchResultTable.getColumns().add(residencyColumn);
        
        TableColumn<TaxpayerCaseStatusCacheResult, String> receiveColumn = new TableColumn<>(TaxpayerCaseStatusReportColumns.RECEIVE.value());
        receiveColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseStatusReportColumns.getParamDescription(TaxpayerCaseStatusReportColumns.RECEIVE)));
        receiveColumn.setPrefWidth(180.00);
        Callback<TableColumn<TaxpayerCaseStatusCacheResult, String>, TableCell<TaxpayerCaseStatusCacheResult,String>> receiveExistingCellFactory  = receiveColumn.getCellFactory();
        receiveColumn.setCellFactory(c -> {
            TableCell<TaxpayerCaseStatusCacheResult, String> cell = receiveExistingCellFactory.call(c);
            Tooltip tooltip = new Tooltip(TaxpayerCaseStatusReportColumns.getParamNote(TaxpayerCaseStatusReportColumns.RECEIVE));
            //tooltip.textProperty().bind(cell.itemProperty().asString());
            cell.setTooltip(tooltip);
            return cell ;
        });
        taxpayerStatusCacheSearchResultTable.getColumns().add(receiveColumn);
        
        TableColumn<TaxpayerCaseStatusCacheResult, String> paymentColumn = new TableColumn<>(TaxpayerCaseStatusReportColumns.PAYMENT.value());
        paymentColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseStatusReportColumns.getParamDescription(TaxpayerCaseStatusReportColumns.PAYMENT)));
        paymentColumn.setPrefWidth(180.00);
        Callback<TableColumn<TaxpayerCaseStatusCacheResult, String>, TableCell<TaxpayerCaseStatusCacheResult,String>> paymentExistingCellFactory  = paymentColumn.getCellFactory();
        paymentColumn.setCellFactory(c -> {
            TableCell<TaxpayerCaseStatusCacheResult, String> cell = paymentExistingCellFactory.call(c);
            Tooltip tooltip = new Tooltip(TaxpayerCaseStatusReportColumns.getParamNote(TaxpayerCaseStatusReportColumns.PAYMENT));
            //tooltip.textProperty().bind(cell.itemProperty().asString());
            cell.setTooltip(tooltip);
            return cell ;
        });
        taxpayerStatusCacheSearchResultTable.getColumns().add(paymentColumn);
        
        TableColumn<TaxpayerCaseStatusCacheResult, String> scanColumn = new TableColumn<>(TaxpayerCaseStatusReportColumns.SCAN.value());
        scanColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseStatusReportColumns.getParamDescription(TaxpayerCaseStatusReportColumns.SCAN)));
        scanColumn.setPrefWidth(180.00);
        Callback<TableColumn<TaxpayerCaseStatusCacheResult, String>, TableCell<TaxpayerCaseStatusCacheResult,String>> scanExistingCellFactory  = scanColumn.getCellFactory();
        scanColumn.setCellFactory(c -> {
            TableCell<TaxpayerCaseStatusCacheResult, String> cell = scanExistingCellFactory.call(c);
            Tooltip tooltip = new Tooltip(TaxpayerCaseStatusReportColumns.getParamNote(TaxpayerCaseStatusReportColumns.SCAN));
            //tooltip.textProperty().bind(cell.itemProperty().asString());
            cell.setTooltip(tooltip);
            return cell ;
        });
        taxpayerStatusCacheSearchResultTable.getColumns().add(scanColumn);
        
        TableColumn<TaxpayerCaseStatusCacheResult, String> prepareColumn = new TableColumn<>(TaxpayerCaseStatusReportColumns.PREPARE.value());
        prepareColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseStatusReportColumns.getParamDescription(TaxpayerCaseStatusReportColumns.PREPARE)));
        prepareColumn.setPrefWidth(180.00);
        Callback<TableColumn<TaxpayerCaseStatusCacheResult, String>, TableCell<TaxpayerCaseStatusCacheResult,String>> prepareExistingCellFactory  = prepareColumn.getCellFactory();
        prepareColumn.setCellFactory(c -> {
            TableCell<TaxpayerCaseStatusCacheResult, String> cell = prepareExistingCellFactory.call(c);
            Tooltip tooltip = new Tooltip(TaxpayerCaseStatusReportColumns.getParamNote(TaxpayerCaseStatusReportColumns.PREPARE));
            //tooltip.textProperty().bind(cell.itemProperty().asString());
            cell.setTooltip(tooltip);
            return cell ;
        });
        taxpayerStatusCacheSearchResultTable.getColumns().add(prepareColumn);
        
        TableColumn<TaxpayerCaseStatusCacheResult, String> reviewColumn = new TableColumn<>(TaxpayerCaseStatusReportColumns.REVIEW.value());
        reviewColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseStatusReportColumns.getParamDescription(TaxpayerCaseStatusReportColumns.REVIEW)));
        reviewColumn.setPrefWidth(180.00);
        Callback<TableColumn<TaxpayerCaseStatusCacheResult, String>, TableCell<TaxpayerCaseStatusCacheResult,String>> reviewExistingCellFactory  = reviewColumn.getCellFactory();
        reviewColumn.setCellFactory(c -> {
            TableCell<TaxpayerCaseStatusCacheResult, String> cell = reviewExistingCellFactory.call(c);
            Tooltip tooltip = new Tooltip(TaxpayerCaseStatusReportColumns.getParamNote(TaxpayerCaseStatusReportColumns.REVIEW));
            //tooltip.textProperty().bind(cell.itemProperty().asString());
            cell.setTooltip(tooltip);
            return cell ;
        });
        taxpayerStatusCacheSearchResultTable.getColumns().add(reviewColumn);
        
        TableColumn<TaxpayerCaseStatusCacheResult, String> cpaReviewColumn = new TableColumn<>(TaxpayerCaseStatusReportColumns.CPA_REVIEW.value());
        cpaReviewColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseStatusReportColumns.getParamDescription(TaxpayerCaseStatusReportColumns.CPA_REVIEW)));
        cpaReviewColumn.setPrefWidth(180.00);
        Callback<TableColumn<TaxpayerCaseStatusCacheResult, String>, TableCell<TaxpayerCaseStatusCacheResult,String>> cpaReviewExistingCellFactory  = cpaReviewColumn.getCellFactory();
        cpaReviewColumn.setCellFactory(c -> {
            TableCell<TaxpayerCaseStatusCacheResult, String> cell = cpaReviewExistingCellFactory.call(c);
            Tooltip tooltip = new Tooltip(TaxpayerCaseStatusReportColumns.getParamNote(TaxpayerCaseStatusReportColumns.CPA_REVIEW));
            //tooltip.textProperty().bind(cell.itemProperty().asString());
            cell.setTooltip(tooltip);
            return cell ;
        });
        taxpayerStatusCacheSearchResultTable.getColumns().add(cpaReviewColumn);
        
        TableColumn<TaxpayerCaseStatusCacheResult, String> notifyColumn = new TableColumn<>(TaxpayerCaseStatusReportColumns.NOTIFY.value());
        notifyColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseStatusReportColumns.getParamDescription(TaxpayerCaseStatusReportColumns.NOTIFY)));
        notifyColumn.setPrefWidth(180.00);
        Callback<TableColumn<TaxpayerCaseStatusCacheResult, String>, TableCell<TaxpayerCaseStatusCacheResult,String>> notifyExistingCellFactory  = notifyColumn.getCellFactory();
        notifyColumn.setCellFactory(c -> {
            TableCell<TaxpayerCaseStatusCacheResult, String> cell = notifyExistingCellFactory.call(c);
            Tooltip tooltip = new Tooltip(TaxpayerCaseStatusReportColumns.getParamNote(TaxpayerCaseStatusReportColumns.NOTIFY));
            //tooltip.textProperty().bind(cell.itemProperty().asString());
            cell.setTooltip(tooltip);
            return cell ;
        });
        taxpayerStatusCacheSearchResultTable.getColumns().add(notifyColumn);
        
        TableColumn<TaxpayerCaseStatusCacheResult, String> pickupColumn = new TableColumn<>(TaxpayerCaseStatusReportColumns.PICKUP.value());
        pickupColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseStatusReportColumns.getParamDescription(TaxpayerCaseStatusReportColumns.PICKUP)));
        pickupColumn.setPrefWidth(180.00);
        Callback<TableColumn<TaxpayerCaseStatusCacheResult, String>, TableCell<TaxpayerCaseStatusCacheResult,String>> pickupExistingCellFactory  = pickupColumn.getCellFactory();
        pickupColumn.setCellFactory(c -> {
            TableCell<TaxpayerCaseStatusCacheResult, String> cell = pickupExistingCellFactory.call(c);
            Tooltip tooltip = new Tooltip(TaxpayerCaseStatusReportColumns.getParamNote(TaxpayerCaseStatusReportColumns.PICKUP));
            //tooltip.textProperty().bind(cell.itemProperty().asString());
            cell.setTooltip(tooltip);
            return cell ;
        });
        taxpayerStatusCacheSearchResultTable.getColumns().add(pickupColumn);
        
        TableColumn<TaxpayerCaseStatusCacheResult, String> signColumn = new TableColumn<>(TaxpayerCaseStatusReportColumns.SIGN.value());
        signColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseStatusReportColumns.getParamDescription(TaxpayerCaseStatusReportColumns.SIGN)));
        signColumn.setPrefWidth(180.00);
        Callback<TableColumn<TaxpayerCaseStatusCacheResult, String>, TableCell<TaxpayerCaseStatusCacheResult,String>> signExistingCellFactory  = signColumn.getCellFactory();
        signColumn.setCellFactory(c -> {
            TableCell<TaxpayerCaseStatusCacheResult, String> cell = signExistingCellFactory.call(c);
            Tooltip tooltip = new Tooltip(TaxpayerCaseStatusReportColumns.getParamNote(TaxpayerCaseStatusReportColumns.SIGN));
            //tooltip.textProperty().bind(cell.itemProperty().asString());
            cell.setTooltip(tooltip);
            return cell ;
        });
        taxpayerStatusCacheSearchResultTable.getColumns().add(signColumn);
        
        TableColumn<TaxpayerCaseStatusCacheResult, String> efileColumn = new TableColumn<>(TaxpayerCaseStatusReportColumns.EFILE.value());
        efileColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseStatusReportColumns.getParamDescription(TaxpayerCaseStatusReportColumns.EFILE)));
        efileColumn.setPrefWidth(180.00);
        Callback<TableColumn<TaxpayerCaseStatusCacheResult, String>, TableCell<TaxpayerCaseStatusCacheResult,String>> efileExistingCellFactory  = efileColumn.getCellFactory();
        efileColumn.setCellFactory(c -> {
            TableCell<TaxpayerCaseStatusCacheResult, String> cell = efileExistingCellFactory.call(c);
            Tooltip tooltip = new Tooltip(TaxpayerCaseStatusReportColumns.getParamNote(TaxpayerCaseStatusReportColumns.EFILE));
            //tooltip.textProperty().bind(cell.itemProperty().asString());
            cell.setTooltip(tooltip);
            return cell ;
        });
        taxpayerStatusCacheSearchResultTable.getColumns().add(efileColumn);
        
        TableColumn<TaxpayerCaseStatusCacheResult, String> contactColumn = new TableColumn<>(TaxpayerCaseStatusReportColumns.CONTACT.value());
        contactColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseStatusReportColumns.getParamDescription(TaxpayerCaseStatusReportColumns.CONTACT)));
        contactColumn.setPrefWidth(180.00);
        Callback<TableColumn<TaxpayerCaseStatusCacheResult, String>, TableCell<TaxpayerCaseStatusCacheResult,String>> contactExistingCellFactory  = contactColumn.getCellFactory();
        contactColumn.setCellFactory(c -> {
            TableCell<TaxpayerCaseStatusCacheResult, String> cell = contactExistingCellFactory.call(c);
            Tooltip tooltip = new Tooltip(TaxpayerCaseStatusReportColumns.getParamNote(TaxpayerCaseStatusReportColumns.CONTACT));
            //tooltip.textProperty().bind(cell.itemProperty().asString());
            cell.setTooltip(tooltip);
            return cell ;
        });
        taxpayerStatusCacheSearchResultTable.getColumns().add(contactColumn);
        
        TableColumn<TaxpayerCaseStatusCacheResult, String> memoColumn = new TableColumn<>(TaxpayerCaseStatusReportColumns.MEMO.value());
        memoColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseStatusReportColumns.getParamDescription(TaxpayerCaseStatusReportColumns.MEMO)));
        memoColumn.setPrefWidth(180.00);
        Callback<TableColumn<TaxpayerCaseStatusCacheResult, String>, TableCell<TaxpayerCaseStatusCacheResult,String>> memoExistingCellFactory  = memoColumn.getCellFactory();
        memoColumn.setCellFactory(c -> {
            TableCell<TaxpayerCaseStatusCacheResult, String> cell = memoExistingCellFactory.call(c);
            Tooltip tooltip = new Tooltip(TaxpayerCaseStatusReportColumns.getParamNote(TaxpayerCaseStatusReportColumns.MEMO));
            //tooltip.textProperty().bind(cell.itemProperty().asString());
            cell.setTooltip(tooltip);
            return cell ;
        });
        taxpayerStatusCacheSearchResultTable.getColumns().add(memoColumn);
        
        taxpayerStatusCacheSearchResultTable.setRowFactory((tv -> {
            TableRow<TaxpayerCaseStatusCacheResult> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    TaxpayerCaseStatusCacheResult aTaxpayerCaseStatusCacheResult = row.getItem();
                    TablePosition tp = tv.getFocusModel().getFocusedCell();
                    if ((tp != null) && (aTaxpayerCaseStatusCacheResult != null)){
                        TaxpayerCaseStatusReportColumns statusColumnType = TaxpayerCaseStatusReportColumns.convertEnumValueToType(tp.getTableColumn().getText(), false);
                        switch (statusColumnType){
                            case RESIDENCY:
                                displayTaxpayerCaseDataEditor(aTaxpayerCaseStatusCacheResult, statusColumnType);
                                break;
                            case RECEIVE:
                            case SCAN:
                            case PREPARE:
                            case REVIEW:
                            case CPA_REVIEW:
                            case NOTIFY:
                            case PICKUP:
                            case SIGN:
                            case EFILE:
                                displayTaxpayerCaseStatusEditor(aTaxpayerCaseStatusCacheResult, statusColumnType);
                                break;
                            default:
                                if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "View the details of this taxpayer case for edit?") == JOptionPane.YES_OPTION){
                                    popupTaxpayerCaseSearchResultTab(aTaxpayerCaseStatusCacheResult.getTaxpayerCaseUuid());
                                }
                                break;
                        }//switch
                    }//if
                }//if
            });
            return row ;
        }));
        
        setupSearchResultTable();
    }
    
    private void displayTaxpayerCaseDataEditor(final TaxpayerCaseStatusCacheResult aTaxpayerCaseStatusCacheResult, final TaxpayerCaseStatusReportColumns statusColumnType) {
        if (Platform.isFxApplicationThread()){
            displayTaxpayerCaseDataEditorHelper(aTaxpayerCaseStatusCacheResult, statusColumnType);
        }else{
            Platform.runLater(() -> {
                displayTaxpayerCaseDataEditorHelper(aTaxpayerCaseStatusCacheResult, statusColumnType);
            });
        }
    }
    
    private void displayTaxpayerCaseDataEditorHelper(final TaxpayerCaseStatusCacheResult aTaxpayerCaseStatusCacheResult, final TaxpayerCaseStatusReportColumns statusColumnType) {
        TaxpayerCaseDataEditorDialog aTaxpayerCaseDataEditorDialog = new TaxpayerCaseDataEditorDialog(null, true);
        aTaxpayerCaseDataEditorDialog.addPeonyFaceEventListener(this);
        aTaxpayerCaseDataEditorDialog.launchTaxpayerCaseDataEditorDialog(aTaxpayerCaseStatusCacheResult, statusColumnType);
    }
    
    private void displayTaxpayerCaseStatusEditor(final TaxpayerCaseStatusCacheResult aTaxpayerCaseStatusCacheResult, final TaxpayerCaseStatusReportColumns statusColumnType) {
        if (Platform.isFxApplicationThread()){
            displayTaxpayerCaseStatusEditorHelper(aTaxpayerCaseStatusCacheResult, statusColumnType);
        }else{
            Platform.runLater(() -> {
                displayTaxpayerCaseStatusEditorHelper(aTaxpayerCaseStatusCacheResult, statusColumnType);
            });
        }
    }
    
    private void displayTaxpayerCaseStatusEditorHelper(final TaxpayerCaseStatusCacheResult aTaxpayerCaseStatusCacheResult, final TaxpayerCaseStatusReportColumns statusColumnType) {
        TaxpayerCaseStatusEditorDialog aTaxpayerCaseStatusEditorDialog = new TaxpayerCaseStatusEditorDialog(null, true);
        aTaxpayerCaseStatusEditorDialog.addPeonyFaceEventListener(this);
        aTaxpayerCaseStatusEditorDialog.launchTaxpayerCaseStatusEditorDialog(aTaxpayerCaseStatusCacheResult, statusColumnType);
    }
    
    private void initializeSearchResultTableForTaxpayerCaseSearchCacheResultList() {
        taxpayerCacheSearchResultTable.getColumns().clear();
        
        TableColumn<TaxpayerCaseSearchCacheResult, Number> indexColumn = new TableColumn<>("#");
        indexColumn.setSortable(false);
        indexColumn.setCellValueFactory(column-> new ReadOnlyObjectWrapper<>(taxpayerCacheSearchResultTable.getItems().indexOf(column.getValue())+1));
        taxpayerCacheSearchResultTable.getColumns().add(indexColumn);
        
        TableColumn detailsTableColumn = new TableColumn("");
        detailsTableColumn.setCellFactory(PeonyButtonTableCell.<TaxpayerCaseSearchCacheResult>callbackForTableColumn(
                "", FontAwesome.Glyph.PENCIL_SQUARE_ALT, Color.DARKBLUE, new Tooltip("View details"), 
                (TaxpayerCaseSearchCacheResult aTaxpayerCaseSearchCacheResult) -> {
                    popupTaxpayerCaseSearchResultTab(aTaxpayerCaseSearchCacheResult.getTaxpayerCaseUuid());
                    return aTaxpayerCaseSearchCacheResult;
                }));
        detailsTableColumn.setPrefWidth(50.00);
        taxpayerCacheSearchResultTable.getColumns().add(detailsTableColumn);
        
        TableColumn<TaxpayerCaseSearchCacheResult, Date> deadlineColumn = new TableColumn<>(TaxpayerCaseSearchCacheResultColumns.DEADLINE.value());
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchCacheResultColumns.getParamDescription(TaxpayerCaseSearchCacheResultColumns.DEADLINE)));
        deadlineColumn.setCellFactory(column -> {
            SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
            TableCell<TaxpayerCaseSearchCacheResult, Date> aDateTableCell = new TableCell<TaxpayerCaseSearchCacheResult, Date>(){
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
        taxpayerCacheSearchResultTable.getColumns().add(deadlineColumn);
        
        TableColumn<TaxpayerCaseSearchCacheResult, Date> extensionColumn = new TableColumn<>(TaxpayerCaseSearchCacheResultColumns.EXTENSION.value());
        extensionColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchCacheResultColumns.getParamDescription(TaxpayerCaseSearchCacheResultColumns.EXTENSION)));
        extensionColumn.setCellFactory(column -> {
            SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
            TableCell<TaxpayerCaseSearchCacheResult, Date> aDateTableCell = new TableCell<TaxpayerCaseSearchCacheResult, Date>(){
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
        taxpayerCacheSearchResultTable.getColumns().add(extensionColumn);
        
        TableColumn<TaxpayerCaseSearchCacheResult, String> federalFilingStatusColumn = new TableColumn<>(TaxpayerCaseSearchCacheResultColumns.FEDERAL_FILING_STATUS.value());
        federalFilingStatusColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchCacheResultColumns.getParamDescription(TaxpayerCaseSearchCacheResultColumns.FEDERAL_FILING_STATUS)));
        taxpayerCacheSearchResultTable.getColumns().add(federalFilingStatusColumn);
        
        TableColumn<TaxpayerCaseSearchCacheResult, String> ssnColumn = new TableColumn<>(TaxpayerCaseSearchCacheResultColumns.SSN.value());
        ssnColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchCacheResultColumns.getParamDescription(TaxpayerCaseSearchCacheResultColumns.SSN)));
        ssnColumn.setPrefWidth(100.00);
        taxpayerCacheSearchResultTable.getColumns().add(ssnColumn);
        
        TableColumn<TaxpayerCaseSearchCacheResult, String> taxpayerColumn = new TableColumn<>(TaxpayerCaseSearchCacheResultColumns.TAXPAYER.value());
        taxpayerColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchCacheResultColumns.getParamDescription(TaxpayerCaseSearchCacheResultColumns.TAXPAYER)));
        taxpayerColumn.setPrefWidth(100.00);
        taxpayerCacheSearchResultTable.getColumns().add(taxpayerColumn);
        
        TableColumn<TaxpayerCaseSearchCacheResult, String> spouseColumn = new TableColumn<>(TaxpayerCaseSearchCacheResultColumns.SPOUSE.value());
        spouseColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchCacheResultColumns.getParamDescription(TaxpayerCaseSearchCacheResultColumns.SPOUSE)));
        spouseColumn.setPrefWidth(100.00);
        taxpayerCacheSearchResultTable.getColumns().add(spouseColumn);
        
        TableColumn<TaxpayerCaseSearchCacheResult, String> conatctColumn = new TableColumn<>(TaxpayerCaseSearchCacheResultColumns.CONTACT.value());
        conatctColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchCacheResultColumns.getParamDescription(TaxpayerCaseSearchCacheResultColumns.CONTACT)));
        conatctColumn.setPrefWidth(180.00);
        taxpayerCacheSearchResultTable.getColumns().add(conatctColumn);
        
        TableColumn<TaxpayerCaseSearchCacheResult, String> statusColumn = new TableColumn<>(TaxpayerCaseSearchCacheResultColumns.STATUS.value());
        statusColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchCacheResultColumns.getParamDescription(TaxpayerCaseSearchCacheResultColumns.STATUS)));
        statusColumn.setPrefWidth(250.0);
        taxpayerCacheSearchResultTable.getColumns().add(statusColumn);
        
        TableColumn<TaxpayerCaseSearchCacheResult, Date> statusUpdatedColumn = new TableColumn<>(TaxpayerCaseSearchCacheResultColumns.STATUS_UPDATED.value());
        statusUpdatedColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchCacheResultColumns.getParamDescription(TaxpayerCaseSearchCacheResultColumns.STATUS_UPDATED)));
        statusUpdatedColumn.setCellFactory(column -> {
            SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
            TableCell<TaxpayerCaseSearchCacheResult, Date> aDateTableCell = new TableCell<TaxpayerCaseSearchCacheResult, Date>(){
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if((item == null)||(empty)) {
                        setText(null);
                    }
                    else {
                        setText(format.format(item));
                    }
                }
            };
            return aDateTableCell;
        });
        taxpayerCacheSearchResultTable.getColumns().add(statusUpdatedColumn);
        
        TableColumn<TaxpayerCaseSearchCacheResult, Date> operatorColumn = new TableColumn<>(TaxpayerCaseSearchCacheResultColumns.STATUS_OPERATOR.value());
        operatorColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchCacheResultColumns.getParamDescription(TaxpayerCaseSearchCacheResultColumns.STATUS_OPERATOR)));
        taxpayerCacheSearchResultTable.getColumns().add(operatorColumn);
        
        TableColumn<TaxpayerCaseSearchCacheResult, String> balanceAndTotalColumn = new TableColumn<>(TaxpayerCaseSearchCacheResultColumns.BALANCE_AND_TOTAL.value());
        balanceAndTotalColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchCacheResultColumns.getParamDescription(TaxpayerCaseSearchCacheResultColumns.BALANCE_AND_TOTAL)));
        balanceAndTotalColumn.setPrefWidth(90.00);
        taxpayerCacheSearchResultTable.getColumns().add(balanceAndTotalColumn);
        
        TableColumn<TaxpayerCaseSearchCacheResult, String> confirmedDepositColumn = new TableColumn<>(TaxpayerCaseSearchCacheResultColumns.CONFIRMED_AND_DEPOSIT.value());
        confirmedDepositColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchCacheResultColumns.getParamDescription(TaxpayerCaseSearchCacheResultColumns.CONFIRMED_AND_DEPOSIT)));
        confirmedDepositColumn.setPrefWidth(300.0);
        taxpayerCacheSearchResultTable.getColumns().add(confirmedDepositColumn);
        
        setupSearchResultTable();
    }
    
    private void initializeSearchResultTableForTaxpayerCaseSearchResultListForDateRange(){
        taxpayerSearchResultTable.getColumns().clear();
        
        TableColumn detailsTableColumn = new TableColumn("");
        detailsTableColumn.setCellFactory(PeonyButtonTableCell.<TaxpayerCaseSearchResult>callbackForTableColumn(
                "", FontAwesome.Glyph.PENCIL_SQUARE_ALT, Color.DARKBLUE, new Tooltip("View details"), 
                (TaxpayerCaseSearchResult aTaxpayerCaseSearchResult) -> {
                    popupTaxpayerCaseSearchResultTab(aTaxpayerCaseSearchResult.getTaxpayerCase().getTaxpayerCaseUuid());
                    return aTaxpayerCaseSearchResult;
                }));
        detailsTableColumn.setPrefWidth(50.00);
        
        TableColumn smsTableColumn = new TableColumn("");
        smsTableColumn.setCellFactory(PeonyButtonTableCell.<TaxpayerCaseSearchResult>callbackForTableColumn(
                "", FontAwesome.Glyph.SEND_ALT, Color.DARKBLUE, new Tooltip("Send SMS message"), 
                (TaxpayerCaseSearchResult aTaxpayerCaseSearchResult) -> {
                    popupSmsDialogForTaxpayerInfos(aTaxpayerCaseSearchResult.getTaxpayerInfoList());
                    return aTaxpayerCaseSearchResult;
                }));
        smsTableColumn.setPrefWidth(50.00);
        
        TableColumn<TaxpayerCaseSearchResult, Number> indexColumn = new TableColumn<>("#");
        indexColumn.setSortable(false);
        indexColumn.setCellValueFactory(column-> new ReadOnlyObjectWrapper<>(taxpayerSearchResultTable.getItems().indexOf(column.getValue())+1));
        taxpayerSearchResultTable.getColumns().add(indexColumn);
        
////        TableColumn<TaxpayerCaseSearchResult, String> taxpayerCaseUuidColumn = new TableColumn<>(TaxpayerCaseSearchResultColumns.TAXPAYER_CASE_UUID.value());
////        taxpayerCaseUuidColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchResultColumns.getParamDescription(TaxpayerCaseSearchResultColumns.TAXPAYER_CASE_UUID)));
        
        TableColumn<TaxpayerCaseSearchResult, Date> deadlineColumn = new TableColumn<>(TaxpayerCaseSearchResultColumns.DEADLINE.value());
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchResultColumns.getParamDescription(TaxpayerCaseSearchResultColumns.DEADLINE)));
        deadlineColumn.setCellFactory(column -> {
            SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
            TableCell<TaxpayerCaseSearchResult, Date> aDateTableCell = new TableCell<TaxpayerCaseSearchResult, Date>(){
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
        
        TableColumn<TaxpayerCaseSearchResult, String> federalFilingStatusColumn = new TableColumn<>(TaxpayerCaseSearchResultColumns.FEDERAL_FILING_STATUS.value());
        federalFilingStatusColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchResultColumns.getParamDescription(TaxpayerCaseSearchResultColumns.FEDERAL_FILING_STATUS)));
        
        TableColumn<TaxpayerCaseSearchResult, String> ssnColumn = new TableColumn<>(TaxpayerCaseSearchResultColumns.SSN.value());
        ssnColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchResultColumns.getParamDescription(TaxpayerCaseSearchResultColumns.SSN)));
        ssnColumn.setPrefWidth(100.00);
        
        TableColumn<TaxpayerCaseSearchResult, String> taxpayerColumn = new TableColumn<>(TaxpayerCaseSearchResultColumns.TAXPAYER.value());
        taxpayerColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchResultColumns.getParamDescription(TaxpayerCaseSearchResultColumns.TAXPAYER)));
        taxpayerColumn.setPrefWidth(100.00);
        
        TableColumn<TaxpayerCaseSearchResult, String> spouseColumn = new TableColumn<>(TaxpayerCaseSearchResultColumns.SPOUSE.value());
        spouseColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchResultColumns.getParamDescription(TaxpayerCaseSearchResultColumns.SPOUSE)));
        spouseColumn.setPrefWidth(100.00);
        
        TableColumn<TaxpayerCaseSearchResult, String> conatctColumn = new TableColumn<>(TaxpayerCaseSearchResultColumns.CONTACT.value());
        conatctColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchResultColumns.getParamDescription(TaxpayerCaseSearchResultColumns.CONTACT)));
        conatctColumn.setPrefWidth(180.00);
        
        TableColumn<TaxpayerCaseSearchResult, String> statusColumn = new TableColumn<>(TaxpayerCaseSearchResultColumns.STATUS.value());
        statusColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchResultColumns.getParamDescription(TaxpayerCaseSearchResultColumns.STATUS)));
        statusColumn.setPrefWidth(250.0);
        
        TableColumn<TaxpayerCaseSearchResult, Date> statusUpdatedColumn = new TableColumn<>(TaxpayerCaseSearchResultColumns.STATUS_UPDATED.value());
        statusUpdatedColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchResultColumns.getParamDescription(TaxpayerCaseSearchResultColumns.STATUS_UPDATED)));
        statusUpdatedColumn.setCellFactory(column -> {
            SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
            TableCell<TaxpayerCaseSearchResult, Date> aDateTableCell = new TableCell<TaxpayerCaseSearchResult, Date>(){
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if((item == null)||(empty)) {
                        setText(null);
                    }
                    else {
                        setText(format.format(item));
                    }
                }
            };
            return aDateTableCell;
        });
        
        TableColumn<TaxpayerCaseSearchResult, Date> operatorColumn = new TableColumn<>(TaxpayerCaseSearchResultColumns.STATUS_OPERATOR.value());
        operatorColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchResultColumns.getParamDescription(TaxpayerCaseSearchResultColumns.STATUS_OPERATOR)));
        
        TableColumn<TaxpayerCaseSearchResult, String> balanceAndTotalColumn = new TableColumn<>(TaxpayerCaseSearchResultColumns.BALANCE_AND_TOTAL.value());
        balanceAndTotalColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchResultColumns.getParamDescription(TaxpayerCaseSearchResultColumns.BALANCE_AND_TOTAL)));
        balanceAndTotalColumn.setPrefWidth(90.00);
        
        TableColumn<TaxpayerCaseSearchResult, String> confirmedDepositColumn = new TableColumn<>(TaxpayerCaseSearchResultColumns.CONFIRMED_AND_DEPOSIT.value());
        confirmedDepositColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerCaseSearchResultColumns.getParamDescription(TaxpayerCaseSearchResultColumns.CONFIRMED_AND_DEPOSIT)));
        confirmedDepositColumn.setPrefWidth(300.0);
        
        taxpayerSearchResultTable.getColumns().addAll(detailsTableColumn, smsTableColumn, deadlineColumn, federalFilingStatusColumn, ssnColumn, taxpayerColumn, spouseColumn, conatctColumn, 
                statusColumn, statusUpdatedColumn, operatorColumn, balanceAndTotalColumn, confirmedDepositColumn);
        
        setupSearchResultTable();
    }
    
    private void initializeSearchResultTableForTaxpayerCaseSearchResultListForWorkStatusSummary(){
        taxpayerSearchResultTable.getColumns().clear();
        
        TableColumn detailsTableColumn = new TableColumn("");
        detailsTableColumn.setCellFactory(PeonyButtonTableCell.<TaxpayerCaseSearchResult>callbackForTableColumn(
                "", FontAwesome.Glyph.PENCIL_SQUARE_ALT, Color.DARKBLUE, new Tooltip("View details"), 
                (TaxpayerCaseSearchResult aTaxpayerCaseSearchResult) -> {
                    popupTaxpayerCaseSearchResultTab(aTaxpayerCaseSearchResult.getTaxpayerCase().getTaxpayerCaseUuid());
                    return aTaxpayerCaseSearchResult;
                }));
        detailsTableColumn.setPrefWidth(50.00);
        taxpayerSearchResultTable.getColumns().add(detailsTableColumn);
        
        TableColumn smsTableColumn = new TableColumn("");
        smsTableColumn.setCellFactory(PeonyButtonTableCell.<TaxpayerCaseSearchResult>callbackForTableColumn(
                "", FontAwesome.Glyph.SEND_ALT, Color.DARKBLUE, new Tooltip("Send SMS message"), 
                (TaxpayerCaseSearchResult aTaxpayerCaseSearchResult) -> {
                    popupSmsDialogForTaxpayerInfos(aTaxpayerCaseSearchResult.getTaxpayerInfoList());
                    return aTaxpayerCaseSearchResult;
                }));
        smsTableColumn.setPrefWidth(50.00);
        taxpayerSearchResultTable.getColumns().add(smsTableColumn);
        
        TableColumn<TaxpayerCaseSearchResult, Number> indexColumn = new TableColumn<>("#");
        indexColumn.setSortable(false);
        indexColumn.setCellValueFactory(column-> new ReadOnlyObjectWrapper<>(taxpayerSearchResultTable.getItems().indexOf(column.getValue())+1));
        taxpayerSearchResultTable.getColumns().add(indexColumn);
        
        TableColumn<TaxpayerCaseSearchResult, String> taxpayerColumn = new TableColumn<>(TaxpayerWorkStatusSummaryColumns.TAXPAYER.value());
        taxpayerColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerWorkStatusSummaryColumns.getParamDescription(TaxpayerWorkStatusSummaryColumns.TAXPAYER)));
        taxpayerColumn.setPrefWidth(100.00);
        taxpayerSearchResultTable.getColumns().add(taxpayerColumn);
        
        TableColumn<TaxpayerCaseSearchResult, String> spouseColumn = new TableColumn<>(TaxpayerWorkStatusSummaryColumns.SPOUSE.value());
        spouseColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerWorkStatusSummaryColumns.getParamDescription(TaxpayerWorkStatusSummaryColumns.SPOUSE)));
        spouseColumn.setPrefWidth(100.00);
        taxpayerSearchResultTable.getColumns().add(spouseColumn);
        
        TableColumn<TaxpayerCaseSearchResult, String> ssnColumn = new TableColumn<>(TaxpayerWorkStatusSummaryColumns.SSN.value());
        ssnColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerWorkStatusSummaryColumns.getParamDescription(TaxpayerWorkStatusSummaryColumns.SSN)));
        ssnColumn.setPrefWidth(100.00);
        taxpayerSearchResultTable.getColumns().add(ssnColumn);
        
        TableColumn<TaxpayerCaseSearchResult, String> federalFilingStatusColumn = new TableColumn<>(TaxpayerWorkStatusSummaryColumns.FEDERAL_FILING_STATUS.value());
        federalFilingStatusColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerWorkStatusSummaryColumns.getParamDescription(TaxpayerWorkStatusSummaryColumns.FEDERAL_FILING_STATUS)));
        taxpayerSearchResultTable.getColumns().add(federalFilingStatusColumn);
        
        TableColumn<TaxpayerCaseSearchResult, String> receiveColumn = new TableColumn<>(TaxpayerWorkStatusSummaryColumns.RECEIVE.value());
        receiveColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerWorkStatusSummaryColumns.getParamDescription(TaxpayerWorkStatusSummaryColumns.RECEIVE)));
        receiveColumn.setPrefWidth(180.0);
        taxpayerSearchResultTable.getColumns().add(receiveColumn);
        
        TableColumn<TaxpayerCaseSearchResult, String> scanColumn = new TableColumn<>(TaxpayerWorkStatusSummaryColumns.SCAN.value());
        scanColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerWorkStatusSummaryColumns.getParamDescription(TaxpayerWorkStatusSummaryColumns.SCAN)));
        scanColumn.setPrefWidth(180.0);
        taxpayerSearchResultTable.getColumns().add(scanColumn);
        
        TableColumn<TaxpayerCaseSearchResult, String> prepareColumn = new TableColumn<>(TaxpayerWorkStatusSummaryColumns.PREPARE.value());
        prepareColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerWorkStatusSummaryColumns.getParamDescription(TaxpayerWorkStatusSummaryColumns.PREPARE)));
        prepareColumn.setPrefWidth(180.0);
        taxpayerSearchResultTable.getColumns().add(prepareColumn);
        
        TableColumn<TaxpayerCaseSearchResult, String> reviewColumn = new TableColumn<>(TaxpayerWorkStatusSummaryColumns.CPA_REVIEW.value());
        reviewColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerWorkStatusSummaryColumns.getParamDescription(TaxpayerWorkStatusSummaryColumns.CPA_REVIEW)));
        reviewColumn.setPrefWidth(180.0);
        taxpayerSearchResultTable.getColumns().add(reviewColumn);
        
        TableColumn<TaxpayerCaseSearchResult, String> pickupReadyColumn = new TableColumn<>(TaxpayerWorkStatusSummaryColumns.PICKUP_READY.value());
        pickupReadyColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerWorkStatusSummaryColumns.getParamDescription(TaxpayerWorkStatusSummaryColumns.PICKUP_READY)));
        pickupReadyColumn.setPrefWidth(180.0);
        taxpayerSearchResultTable.getColumns().add(pickupReadyColumn);
        
        TableColumn<TaxpayerCaseSearchResult, String> pickupColumn = new TableColumn<>(TaxpayerWorkStatusSummaryColumns.PICKUP_COMPLETED.value());
        pickupColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerWorkStatusSummaryColumns.getParamDescription(TaxpayerWorkStatusSummaryColumns.PICKUP_COMPLETED)));
        pickupColumn.setPrefWidth(180.0);
        taxpayerSearchResultTable.getColumns().add(pickupColumn);
        
        TableColumn<TaxpayerCaseSearchResult, String> signatureColumn = new TableColumn<>(TaxpayerWorkStatusSummaryColumns.SIGNATURE_AND_PAYMENT.value());
        signatureColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerWorkStatusSummaryColumns.getParamDescription(TaxpayerWorkStatusSummaryColumns.SIGNATURE_AND_PAYMENT)));
        signatureColumn.setPrefWidth(180.0);
        taxpayerSearchResultTable.getColumns().add(signatureColumn);
        
        TableColumn<TaxpayerCaseSearchResult, String> eFileReadyColumn = new TableColumn<>(TaxpayerWorkStatusSummaryColumns.EFILE_READY.value());
        eFileReadyColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerWorkStatusSummaryColumns.getParamDescription(TaxpayerWorkStatusSummaryColumns.EFILE_READY)));
        eFileReadyColumn.setPrefWidth(180.0);
        taxpayerSearchResultTable.getColumns().add(eFileReadyColumn);
        
        TableColumn<TaxpayerCaseSearchResult, String> eFileColumn = new TableColumn<>(TaxpayerWorkStatusSummaryColumns.EFILE_COMPLETED.value());
        eFileColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerWorkStatusSummaryColumns.getParamDescription(TaxpayerWorkStatusSummaryColumns.EFILE_COMPLETED)));
        eFileColumn.setPrefWidth(180.0);
        taxpayerSearchResultTable.getColumns().add(eFileColumn);
        
        TableColumn<TaxpayerCaseSearchResult, String> conatctColumn = new TableColumn<>(TaxpayerWorkStatusSummaryColumns.CONTACT.value());
        conatctColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerWorkStatusSummaryColumns.getParamDescription(TaxpayerWorkStatusSummaryColumns.CONTACT)));
        conatctColumn.setPrefWidth(180.00);
        taxpayerSearchResultTable.getColumns().add(conatctColumn);
        
        TableColumn<TaxpayerCaseSearchResult, String> balanceAndTotalColumn = new TableColumn<>(TaxpayerWorkStatusSummaryColumns.BALANCE_AND_TOTAL.value());
        balanceAndTotalColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerWorkStatusSummaryColumns.getParamDescription(TaxpayerWorkStatusSummaryColumns.BALANCE_AND_TOTAL)));
        balanceAndTotalColumn.setPrefWidth(90.00);
        taxpayerSearchResultTable.getColumns().add(balanceAndTotalColumn);
        
        TableColumn<TaxpayerCaseSearchResult, String> confirmedDepositColumn = new TableColumn<>(TaxpayerWorkStatusSummaryColumns.CONFIRMED_AND_DEPOSIT.value());
        confirmedDepositColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerWorkStatusSummaryColumns.getParamDescription(TaxpayerWorkStatusSummaryColumns.CONFIRMED_AND_DEPOSIT)));
        confirmedDepositColumn.setPrefWidth(300.0);
        taxpayerSearchResultTable.getColumns().add(confirmedDepositColumn);
        
        TableColumn<TaxpayerCaseSearchResult, String> memoColumn = new TableColumn<>(TaxpayerWorkStatusSummaryColumns.MEMO.value());
        memoColumn.setCellValueFactory(new PropertyValueFactory<>(TaxpayerWorkStatusSummaryColumns.getParamDescription(TaxpayerWorkStatusSummaryColumns.MEMO)));
        memoColumn.setPrefWidth(300.0);
        taxpayerSearchResultTable.getColumns().add(memoColumn);
        
        setupSearchResultTable();
    }

    private void popupSmsDialogForTaxpayerInfos(List<G02TaxpayerInfo> aG02TaxpayerInfoList) {
        List<G02TaxpayerInfo> contactors = new ArrayList<>(); 
        for (G02TaxpayerInfo aG02TaxpayerInfo : aG02TaxpayerInfoList){
            if (ZcaValidator.isNotNullEmpty(aG02TaxpayerInfo.getMobilePhone())){
                contactors.add(aG02TaxpayerInfo);
            }
        }//for
        if (contactors.isEmpty()){
            PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, "Contactor(s) have no qualified mobile for SMS.", "Error", JOptionPane.ERROR_MESSAGE);
        }else{
            Lookup.getDefault().lookup(PeonyManagementService.class).popupSmsDialogForTaxpayers(contactors);
        }
    }

    private void popupSmsDialogForBusinessContactor(G02BusinessContactor aG02BusinessContactor) {
        if ((aG02BusinessContactor == null) 
                || (!GardenContactType.MOBILE_PHONE.value().equalsIgnoreCase(aG02BusinessContactor.getContactType())))
        {
            PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, "The contactor is not qualified mobile for SMS.", "Error", JOptionPane.ERROR_MESSAGE);
        }else{
            Lookup.getDefault().lookup(PeonyManagementService.class).popupSmsDialogForBusinessContactor(aG02BusinessContactor);
        }
    }

    private void popupSmsDialogForBusinessContactors(List<G02BusinessContactor> aG02BusinessContactorList) {
        List<G02BusinessContactor> contactors = new ArrayList<>(); 
        for (G02BusinessContactor aG02BusinessContactor : aG02BusinessContactorList){
            if (GardenContactType.MOBILE_PHONE.value().equalsIgnoreCase(aG02BusinessContactor.getContactType())){
                contactors.add(aG02BusinessContactor);
            }
        }//for
        if (contactors.isEmpty()){
            PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, "Contactor(s) have no qualified mobile for SMS.", "Error", JOptionPane.ERROR_MESSAGE);
        }else{
            Lookup.getDefault().lookup(PeonyManagementService.class).popupSmsDialogForBusinessContactors(contactors);
        }
    }

    private void setupSearchResultTableForTaxFilingCaseSearchResultList() {
        totalLabel.setText("Total: 0/" + filteredTaxcorpTaxFilingCaseSearchResultList.size());
        taxcorpTaxFilingSearchResultTable.setItems(FXCollections.observableArrayList(filteredTaxcorpTaxFilingCaseSearchResultList));
//        Pagination pagination = new Pagination((filteredTaxcorpTaxFilingCaseSearchResultList.size()/ rowsPerPage + 1), 0);
//        pagination.setPageFactory(new Callback<Integer, Node>(){
//            @Override
//            public Node call(Integer pageIndex) {
//                int fromIndex = pageIndex * rowsPerPage;
//                int toIndex = Math.min(fromIndex + rowsPerPage, filteredTaxcorpTaxFilingCaseSearchResultList.size());
//                searchResultTable.setItems(FXCollections.observableArrayList(filteredTaxcorpTaxFilingCaseSearchResultList.subList(fromIndex, toIndex)));
//
//                return new BorderPane(searchResultTable);
//            }
//        });
//        searchResultBorderPane.setCenter(pagination);
        searchResultBorderPane.setCenter(taxcorpTaxFilingSearchResultTable);
    }

    private void setupSearchResultTableForCustomerSearchResultList() {
        totalLabel.setText("Total: " + filteredCustomerSearchResultList.size());
        customerSearchResultTable.setItems(FXCollections.observableArrayList(filteredCustomerSearchResultList));
        searchResultBorderPane.setCenter(customerSearchResultTable);
    }

    private void setupSearchResultTableForTaxcorpCaseSearchResultList() {
        totalLabel.setText("Total: " + filteredTaxcorpCaseSearchResultList.size());
        taxcorpSearchResultTable.setItems(FXCollections.observableArrayList(filteredTaxcorpCaseSearchResultList));
        searchResultBorderPane.setCenter(taxcorpSearchResultTable);
    }

    private void setupSearchResultTableForTaxpayerCaseSearchResultList() {
        totalLabel.setText("Total: 0/" + filteredTaxpayerCaseSearchResultList.size());
        taxpayerSearchResultTable.setItems(FXCollections.observableArrayList(filteredTaxpayerCaseSearchResultList));
        searchResultBorderPane.setCenter(taxpayerSearchResultTable);
        disableFilterButtons();
        this.getCachedThreadPoolExecutorService().submit(new LoadTaxpayerSearchResultDataTask());
    }

    private void setupSearchResultTableForTaxpayerCaseSearchCacheResultList() {
        disableFilterButtons();
        totalLabel.setText("Total: " + filteredTaxpayerCaseSearchCacheResultList.size() + " (Note: Cached data may be different from realtime data because of time-delay)");
        taxpayerCacheSearchResultTable.setItems(FXCollections.observableArrayList(filteredTaxpayerCaseSearchCacheResultList));
        searchResultBorderPane.setCenter(taxpayerCacheSearchResultTable);
        enableFilterButtons();
    }

    private void setupSearchResultTableForTaxpayerCaseStatusCacheResultList() {
        disableFilterButtons();
        totalLabel.setText("Total: " + filteredTaxpayerCaseStatusCacheResultList.size() + " (Note: Cached data may be different from realtime data because of time-delay)");
        taxpayerStatusCacheSearchResultTable.setItems(FXCollections.observableArrayList(filteredTaxpayerCaseStatusCacheResultList));
        searchResultBorderPane.setCenter(taxpayerStatusCacheSearchResultTable);
        enableFilterButtons();
    }

    private void disableFilterButtons() {
        textFilterButton.setDisable(true);
        dateFilterButton.setDisable(true);
        resetButton.setDisable(true);
        sendSmsButton.setDisable(true);
        exportButton.setDisable(true);
    }

    private void enableFilterButtons() {
        textFilterButton.setDisable(false);
        dateFilterButton.setDisable(false);
        resetButton.setDisable(false);
        sendSmsButton.setDisable(false);
        exportButton.setDisable(false);
    }

    private void handleTaxpayerCaseStatusUpdated(final TaxpayerCaseStatusUpdated taxpayerCaseStatusSaved) {
        if (Platform.isFxApplicationThread()){
            handleTaxpayerCaseStatusUpdatedHelper(taxpayerCaseStatusSaved);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    handleTaxpayerCaseStatusUpdatedHelper(taxpayerCaseStatusSaved);
                }
            });
        }
    }
    
    private void handleTaxpayerCaseStatusUpdatedHelper(final TaxpayerCaseStatusUpdated taxpayerCaseStatusUpdated) {
        if ((taxpayerCaseStatusUpdated == null) || (taxpayerCaseStatusUpdated.getTaxpayerCaseStatusCacheResult() == null)
                || (taxpayerCaseStatusUpdated.getTaxpayerCaseStatusCacheResult().getTaxpayerCaseStatusCache() == null))
        {
            return;
        }
        G02TaxpayerCaseStatusCache aTaxpayerCaseStatusCache = taxpayerCaseStatusUpdated.getTaxpayerCaseStatusCacheResult().getTaxpayerCaseStatusCache();
        List<TaxpayerCaseStatusCacheResult> aTaxpayerCaseStatusCacheResultList = taxpayerStatusCacheSearchResultTable.getItems();
        int index = 0;
        for (TaxpayerCaseStatusCacheResult aTaxpayerCaseStatusCacheResult : aTaxpayerCaseStatusCacheResultList){
            if (aTaxpayerCaseStatusCacheResult.getTaxpayerCaseUuid().equalsIgnoreCase(aTaxpayerCaseStatusCache.getTaxpayerCaseUuid())){
                break;
            }
            index++;
        }//for-loop
        taxpayerStatusCacheSearchResultTable.getItems().remove(index);
        taxpayerStatusCacheSearchResultTable.getItems().add(index, taxpayerCaseStatusUpdated.getTaxpayerCaseStatusCacheResult());
        taxpayerStatusCacheSearchResultTable.refresh();
    }
    
    private class LoadTaxpayerSearchResultDataTask extends Task<Integer>{

        public LoadTaxpayerSearchResultDataTask() {
        }
        
        @Override
        protected Integer call() throws Exception {
            HashMap<String, PeonyEmployee> peonyEmployeeSet = Lookup.getDefault().lookup(PeonyManagementService.class).retrievePeonyEmployeeSet();
            //help find the instance
            HashMap<String, TaxpayerCaseSearchResult> aTaxpayerCaseSearchResultMap = new HashMap<>();
            for (TaxpayerCaseSearchResult theTaxpayerCaseSearchResult : filteredTaxpayerCaseSearchResultList){
                theTaxpayerCaseSearchResult.setPeonyEmployeeSet(peonyEmployeeSet);  //plugin employee list
                aTaxpayerCaseSearchResultMap.put(theTaxpayerCaseSearchResult.getTaxpayerCase().getTaxpayerCaseUuid(), theTaxpayerCaseSearchResult);
            }
            int total = filteredTaxpayerCaseSearchResultList.size();
            int stepThreshold = 4;
            int stepCounter = 0;
            TaxpayerCaseSearchResult aTaxpayerCaseSearchResult;
            TaxpayerCaseSearchResultList theTaxpayerCaseSearchResultList = new TaxpayerCaseSearchResultList();
            for (int i = 0; i < total; i++){
                aTaxpayerCaseSearchResult = filteredTaxpayerCaseSearchResultList.get(i);
                theTaxpayerCaseSearchResultList.getTaxpayerCaseSearchResultList().add(aTaxpayerCaseSearchResult);
                if (stepCounter < stepThreshold){
                    stepCounter++;
                }else{
                    queryTaxpayerSearchRemainingData(theTaxpayerCaseSearchResultList, i, aTaxpayerCaseSearchResultMap);
                    stepCounter = 0;
                    theTaxpayerCaseSearchResultList.getTaxpayerCaseSearchResultList().clear();
                }
            }
            if (!theTaxpayerCaseSearchResultList.getTaxpayerCaseSearchResultList().isEmpty()){
                queryTaxpayerSearchRemainingData(theTaxpayerCaseSearchResultList, total, aTaxpayerCaseSearchResultMap);
            }

            return total;
        }

        private void queryTaxpayerSearchRemainingData(TaxpayerCaseSearchResultList theTaxpayerCaseSearchResultList, int counter,
                                                      HashMap<String, TaxpayerCaseSearchResult> aTaxpayerCaseSearchResultMap) 
        {
////            retrieveTaxpayerInfoListForTaxpayerCaseSearchResultList(theTaxpayerCaseSearchResultList, aTaxpayerCaseSearchResultMap);
////            updateValue(counter); //refresh the table
            retrieveWorkStatusLogListForTaxpayerCaseSearchResultListRest(theTaxpayerCaseSearchResultList, aTaxpayerCaseSearchResultMap);
            updateValue(counter); //refresh the table
            retrievePeonyBillPaymentListForTaxpayerCaseSearchResultList(theTaxpayerCaseSearchResultList, aTaxpayerCaseSearchResultMap);
            updateValue(counter); //refresh the table
////            updateValue(true); //refresh the table
////            retrieveTaxpayerMemoListForTaxpayerCaseSearchResultList(theTaxpayerCaseSearchResultList, aTaxpayerCaseSearchResultMap);
        }

        @Override
        protected void failed() {
            PeonyFaceUtils.publishMessageOntoOutputWindow("Cannot retrieve remaining information for search. " + getMessage());
        }
        
        private void updateProgressValue(final int counter){
            if (Platform.isFxApplicationThread()){
                updateProgressValueHelper(counter);
            }else{
                Platform.runLater(() -> {
                    updateProgressValueHelper(counter);
                });
            }
        }
        
        private void updateProgressValueHelper(final int counter){
            taxpayerSearchResultTable.refresh();
            if (counter < filteredTaxpayerCaseSearchResultList.size()){
                totalLabel.setText("Total: "+counter+"/" + filteredTaxpayerCaseSearchResultList.size() + " loading...");
            }else{
                totalLabel.setText("Total: "+counter+"/" + filteredTaxpayerCaseSearchResultList.size());
            }
        }

        @Override
        protected void updateValue(Integer counter) {
            updateProgressValue(counter);
        }

        @Override
        protected void succeeded() {
            try {
                
                updateProgressValue(get());

                enableFilterButtons();

            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void retrievePeonyBillPaymentListForTaxpayerCaseSearchResultList(TaxpayerCaseSearchResultList theTaxpayerCaseSearchResultList, 
                                                                             HashMap<String, TaxpayerCaseSearchResult> aTaxpayerCaseSearchResultMap) 
    {
        try{
            //System.out.println(">>> retrievePeonyBillPaymentListForTaxpayerCaseSearchResultList");
            TaxpayerCaseSearchResultList result = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient().retrieveEntity_XML(TaxpayerCaseSearchResultList.class, 
                    GardenRestParams.Taxpayer.retrievePeonyBillPaymentListForTaxpayerCaseSearchResultListRestParams(), theTaxpayerCaseSearchResultList);
            if ((result != null) && (!result.getTaxpayerCaseSearchResultList().isEmpty())){
                List<TaxpayerCaseSearchResult> aTaxpayerCaseSearchResultList = result.getTaxpayerCaseSearchResultList();
                TaxpayerCaseSearchResult theTaxpayerCaseSearchResult;
                for (TaxpayerCaseSearchResult aTaxpayerCaseSearchResult : aTaxpayerCaseSearchResultList){
                    theTaxpayerCaseSearchResult = aTaxpayerCaseSearchResultMap.get(aTaxpayerCaseSearchResult.getTaxpayerCase().getTaxpayerCaseUuid());
                    if (theTaxpayerCaseSearchResult != null){
                        theTaxpayerCaseSearchResult.setPeonyBillPaymentList(aTaxpayerCaseSearchResult.getPeonyBillPaymentList());
                    }
                }
            }
        }catch (Exception ex){
            PeonyFaceUtils.publishMessageOntoOutputWindow("Cannot retrieveData for TaxpayerCaseSearchResultList. " + ex.getMessage());
        }
    }

    private void retrieveWorkStatusLogListForTaxpayerCaseSearchResultListRest(TaxpayerCaseSearchResultList theTaxpayerCaseSearchResultList, 
                                                                            HashMap<String, TaxpayerCaseSearchResult> aTaxpayerCaseSearchResultMap) 
    {
        try{
            //System.out.println(">>> retrieveWorkStatusLogListForTaxpayerCaseSearchResultListRest");
            TaxpayerCaseSearchResultList result = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient().retrieveEntity_XML(TaxpayerCaseSearchResultList.class, 
                    GardenRestParams.Taxpayer.retrieveWorkStatusLogListForTaxpayerCaseSearchResultListRestParams(), theTaxpayerCaseSearchResultList);
            if ((result != null) && (!result.getTaxpayerCaseSearchResultList().isEmpty())){
                List<TaxpayerCaseSearchResult> aTaxpayerCaseSearchResultList = result.getTaxpayerCaseSearchResultList();
                TaxpayerCaseSearchResult theTaxpayerCaseSearchResult;
                for (TaxpayerCaseSearchResult aTaxpayerCaseSearchResult : aTaxpayerCaseSearchResultList){
                    theTaxpayerCaseSearchResult = aTaxpayerCaseSearchResultMap.get(aTaxpayerCaseSearchResult.getTaxpayerCase().getTaxpayerCaseUuid());
                    if (theTaxpayerCaseSearchResult != null){
                        theTaxpayerCaseSearchResult.setWorkStatusLogList(aTaxpayerCaseSearchResult.getWorkStatusLogList());
                    }
                }
            }
        }catch (Exception ex){
            PeonyFaceUtils.publishMessageOntoOutputWindow("Cannot retrieveData for TaxpayerCaseSearchResultList. " + ex.getMessage());
        }
    }

    private void filterCustomerSearchResultListByDate(final SearchResultColumnDateCriteraCreated criteria) {
        Task<List<CustomerSearchResult>> filterCustomerSearchResultListTask = new Task<List<CustomerSearchResult>>(){
            @Override
            protected List<CustomerSearchResult> call() throws Exception {
                filteredCustomerSearchResultList.clear();
                List<CustomerSearchResult> aCustomerSearchResultList = customerSearchResultList.getCustomerSearchResultList();
                try{
                    if (ZcaValidator.isNullEmpty(criteria.getSearchResultColumn())){
                        throw new Exception("Please select a column for filter.");
                    }
                    if ((criteria.getFromDate() == null) || (criteria.getToDate() == null)){
                        throw new Exception("Please select a date range, from and to, for filter.");
                    }
                    Date from = ZcaCalendar.convertToDate(criteria.getFromDate());
                    Date to = ZcaCalendar.convertToDate(criteria.getToDate());
                    Date dateFieldValue;
                    for (CustomerSearchResult aCustomerSearchResult : aCustomerSearchResultList){
                        dateFieldValue = getTargetDate(aCustomerSearchResult, CustomerSearchResultColumns.convertEnumValueToType(criteria.getSearchResultColumn()));
                        if (dateFieldValue != null){
                            if ((dateFieldValue.equals(to)) || (dateFieldValue.equals(from)) 
                                    || (dateFieldValue.after(from) && dateFieldValue.before(to)))
                            {
                                filteredCustomerSearchResultList.add(aCustomerSearchResult);
                            }
                        }
                    }//for
                }catch (Exception ex){
                    updateMessage(ex.getMessage());
                    return null;
                }
                return filteredCustomerSearchResultList;
            }

            @Override
            protected void succeeded() {
                try {
                    List<CustomerSearchResult> result = get();
                    if (result == null){
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }else{
                        setupSearchResultTable();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception was raised. " + ex.getMessage());
                }
            }
            
            private Date getTargetDate(CustomerSearchResult aCustomerSearchResult, CustomerSearchResultColumns aCustomerSearchResultColumns){
                switch (aCustomerSearchResultColumns){
                    case UPDATED:
                        return aCustomerSearchResult.getAccount().getUpdated();
                    default:
                        return aCustomerSearchResult.getAccount().getCreated();
                }
            }
        };
        this.getCachedThreadPoolExecutorService().submit(filterCustomerSearchResultListTask);
    }

    private void filterCustomerSearchResultListByText(final SearchResultColumnTextCriteraCreated criteria) {
        Task<List<CustomerSearchResult>> filterCustomerSearchResultListTask = new Task<List<CustomerSearchResult>>(){
            @Override
            protected List<CustomerSearchResult> call() throws Exception {
                filteredCustomerSearchResultList.clear();
                List<CustomerSearchResult> aCustomerSearchResultList = customerSearchResultList.getCustomerSearchResultList();
                try{
                    if (ZcaValidator.isNullEmpty(criteria.getSearchResultColumn())){
                        throw new Exception("Please select a column for filter.");
                    }
                    if (ZcaValidator.isNullEmpty(criteria.getSearchResultColumnCriteriaValue())){
                        throw new Exception("Please type into keywords for filter.");
                    }
                    for (CustomerSearchResult aCustomerSearchResult : aCustomerSearchResultList){
                        if (GardenData.calculateSimilarityByJaroWinklerStrategy(getTargetValue(aCustomerSearchResult), 
                                                                                criteria.getSearchResultColumnCriteriaValue(), 
                                                                                criteria.isExactMatch(), 0.75) > 0.75)
                        {
                            filteredCustomerSearchResultList.add(aCustomerSearchResult);
                        }
                    }//for
                }catch (Exception ex){
                    updateMessage(ex.getMessage());
                    return null;
                }
                return filteredCustomerSearchResultList;
            }

            @Override
            protected void succeeded() {
                try {
                    List<CustomerSearchResult> result = get();
                    if (result == null){
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }else{
                        setupSearchResultTable();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception was raised. " + ex.getMessage());
                }
            }

            private String getTargetValue(CustomerSearchResult aCustomerSearchResult) {
                CustomerSearchResultColumns column = CustomerSearchResultColumns.convertEnumValueToType(criteria.getSearchResultColumn());
                switch (column){
                    case SSN:
                        return aCustomerSearchResult.getSsn();
                    case LOGIN_NAME:
                        return aCustomerSearchResult.getLoginName();
                    case ACCOUNT_EMAIL:
                        return aCustomerSearchResult.getAccountEmail();
                    case FIRST_NAME:
                        return aCustomerSearchResult.getFirstName();
                    case LAST_NAME:
                        return aCustomerSearchResult.getLastName();
                    case MOBILE_PHONE:
                        return aCustomerSearchResult.getMobilePhone();
                    default:
                        return null;
                }
            }
        };
        this.getCachedThreadPoolExecutorService().submit(filterCustomerSearchResultListTask);
    }

    private void filterTaxcorpCaseSearchResultListByDate(final SearchResultColumnDateCriteraCreated criteria) {
        Task<List<TaxcorpCaseSearchResult>> filterTaxcorpCaseSearchResultListTask = new Task<List<TaxcorpCaseSearchResult>>(){
            @Override
            protected List<TaxcorpCaseSearchResult> call() throws Exception {
                filteredTaxcorpCaseSearchResultList.clear();
                List<TaxcorpCaseSearchResult> aTaxcorpCaseSearchResultList = taxcorpCaseSearchResultList.getTaxcorpCaseSearchResultList();
                try{
                    if (ZcaValidator.isNullEmpty(criteria.getSearchResultColumn())){
                        throw new Exception("Please select a column for filter.");
                    }
                    if ((criteria.getFromDate()== null) || (criteria.getToDate() == null)){
                        throw new Exception("Please select a date range, from and to, for filter.");
                    }
                    Date from = ZcaCalendar.convertToDate(criteria.getFromDate());
                    Date to = ZcaCalendar.convertToDate(criteria.getToDate());
                    Date dosDate;
                    for (TaxcorpCaseSearchResult aTaxcorpCaseSearchResult : aTaxcorpCaseSearchResultList){
                        dosDate = getTargetDate(aTaxcorpCaseSearchResult);
                        if (dosDate != null){
                            if ((dosDate.equals(to)) || (dosDate.equals(from)) 
                                    || (dosDate.after(from) && dosDate.before(to)))
                            {
                                filteredTaxcorpCaseSearchResultList.add(aTaxcorpCaseSearchResult);
                            }
                        }
                    }//for
                }catch (Exception ex){
                    updateMessage(ex.getMessage());
                    return null;
                }
                return filteredTaxcorpCaseSearchResultList;
            }

            @Override
            protected void succeeded() {
                try {
                    List<TaxcorpCaseSearchResult> result = get();
                    if (result == null){
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }else{
                        setupSearchResultTable();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception was raised. " + ex.getMessage());
                }
            }
            
            private Date getTargetDate(TaxcorpCaseSearchResult aTaxcorpCaseSearchResult){
                return aTaxcorpCaseSearchResult.getTaxcorpCase().getDosDate();
            }
        };
        this.getCachedThreadPoolExecutorService().submit(filterTaxcorpCaseSearchResultListTask);
    }

    private void filterTaxcorpCaseSearchResultListByText(final SearchResultColumnTextCriteraCreated criteria) {
        Task<List<TaxcorpCaseSearchResult>> filterTaxcorpCaseSearchResultListTask = new Task<List<TaxcorpCaseSearchResult>>(){
            @Override
            protected List<TaxcorpCaseSearchResult> call() throws Exception {
                filteredTaxcorpCaseSearchResultList.clear();
                List<TaxcorpCaseSearchResult> aTaxcorpCaseSearchResultList = taxcorpCaseSearchResultList.getTaxcorpCaseSearchResultList();
                try{
                    if (ZcaValidator.isNullEmpty(criteria.getSearchResultColumn())){
                        throw new Exception("Please select a column for filter.");
                    }
                    if (ZcaValidator.isNullEmpty(criteria.getSearchResultColumnCriteriaValue())){
                        throw new Exception("Please type into keywords for filter.");
                    }
                    for (TaxcorpCaseSearchResult aTaxcorpCaseSearchResult : aTaxcorpCaseSearchResultList){
                        if (GardenData.calculateSimilarityByJaroWinklerStrategy(getTargetValue(aTaxcorpCaseSearchResult), 
                                                                                criteria.getSearchResultColumnCriteriaValue(), 
                                                                                criteria.isExactMatch(), 0.75) > 0.75)
                        {
                            filteredTaxcorpCaseSearchResultList.add(aTaxcorpCaseSearchResult);
                        }
                    }//for
                }catch (Exception ex){
                    updateMessage(ex.getMessage());
                    return null;
                }
                return filteredTaxcorpCaseSearchResultList;
            }

            @Override
            protected void succeeded() {
                try {
                    List<TaxcorpCaseSearchResult> result = get();
                    if (result == null){
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }else{
                        setupSearchResultTable();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception was raised. " + ex.getMessage());
                }
            }

            private String getTargetValue(TaxcorpCaseSearchResult aTaxcorpCaseSearchResult) {
                TaxcorpCaseSearchResultColumns column = TaxcorpCaseSearchResultColumns.convertEnumValueToType(criteria.getSearchResultColumn());
                switch (column){
                    case COMPANY_NAME:
                        return aTaxcorpCaseSearchResult.getCompanyName();
                    case TAX_STATE:
                        return aTaxcorpCaseSearchResult.getTaxState();
                    case BUSINESS_TYPE:
                        return aTaxcorpCaseSearchResult.getBusinessType();
                    case EIN_NUMBER:
                        return aTaxcorpCaseSearchResult.getEinNumber();
                    case STATUS:
                        return aTaxcorpCaseSearchResult.getTaxcorpStatus();
                    case CONTACT_INFORMATION:
                        return aTaxcorpCaseSearchResult.getContactInformation();
                    default:
                        return null;
                }
            }
        };
        this.getCachedThreadPoolExecutorService().submit(filterTaxcorpCaseSearchResultListTask);
    }
    
    private void filterTaxpayerCaseSearchResultListByDate(final SearchResultColumnDateCriteraCreated criteria) {
        Task<List<TaxpayerCaseSearchResult>> filterTaxpayerCaseSearchResultListTask = new Task<List<TaxpayerCaseSearchResult>>(){
            @Override
            protected List<TaxpayerCaseSearchResult> call() throws Exception {
                filteredTaxpayerCaseSearchResultList.clear();
                List<TaxpayerCaseSearchResult> aTaxpayerCaseSearchResultList = taxpayerCaseSearchResultList.getTaxpayerCaseSearchResultList();
                try{
                    if (ZcaValidator.isNullEmpty(criteria.getSearchResultColumn())){
                        throw new Exception("Please select a column for filter.");
                    }
                    if ((criteria.getFromDate() == null) || (criteria.getToDate() == null)){
                        throw new Exception("Please select a date range, from and to, for filter.");
                    }
                    Date from = ZcaCalendar.convertToDate(criteria.getFromDate());
                    Date to = ZcaCalendar.convertToDate(criteria.getToDate());
                    Date dateFieldValue;
                    for (TaxpayerCaseSearchResult aTaxpayerCaseSearchResult : aTaxpayerCaseSearchResultList){
                        dateFieldValue = getTargetDate(aTaxpayerCaseSearchResult);
                        if (dateFieldValue != null){
                            if ((dateFieldValue.equals(to)) || (dateFieldValue.equals(from)) 
                                    || (dateFieldValue.after(from) && dateFieldValue.before(to)))
                            {
                                filteredTaxpayerCaseSearchResultList.add(aTaxpayerCaseSearchResult);
                            }
                        }
                    }//for
                }catch (Exception ex){
                    updateMessage(ex.getMessage());
                    return null;
                }
                return filteredTaxpayerCaseSearchResultList;
            }

            @Override
            protected void succeeded() {
                try {
                    List<TaxpayerCaseSearchResult> result = get();
                    if (result == null){
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }else{
                        setupSearchResultTable();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception was raised. " + ex.getMessage());
                }
            }
            
            private Date getTargetDate(TaxpayerCaseSearchResult aTaxpayerCaseSearchResult){
                if (TaxpayerCaseSearchResultColumns.convertEnumValueToType(criteria.getSearchResultColumn()).equals(TaxpayerCaseSearchResultColumns.STATUS_UPDATED)){
                    return aTaxpayerCaseSearchResult.getLatestTaxpayerStatusTimestamp();
                }
                return null;
            }
        };
        this.getCachedThreadPoolExecutorService().submit(filterTaxpayerCaseSearchResultListTask);
    }
    
    private void filterTaxpayerCaseSearchCacheResultListByDate(final SearchResultColumnDateCriteraCreated criteria) {
        Task<List<TaxpayerCaseSearchCacheResult>> filterTaxpayerCaseSearchCacheResultListTask = new Task<List<TaxpayerCaseSearchCacheResult>>(){
            @Override
            protected List<TaxpayerCaseSearchCacheResult> call() throws Exception {
                filteredTaxpayerCaseSearchCacheResultList.clear();
                List<TaxpayerCaseSearchCacheResult> aTaxpayerCaseSearchCacheResultList = taxpayerCaseSearchCacheResultList.getTaxpayerCaseSearchCacheResultList();
                try{
                    if (ZcaValidator.isNullEmpty(criteria.getSearchResultColumn())){
                        throw new Exception("Please select a column for filter.");
                    }
                    if ((criteria.getFromDate() == null) || (criteria.getToDate() == null)){
                        throw new Exception("Please select a date range, from and to, for filter.");
                    }
                    Date from = ZcaCalendar.convertToDate(criteria.getFromDate());
                    Date to = ZcaCalendar.convertToDate(criteria.getToDate());
                    Date dateFieldValue;
                    for (TaxpayerCaseSearchCacheResult aTaxpayerCaseSearchCacheResult : aTaxpayerCaseSearchCacheResultList){
                        dateFieldValue = getTargetDate(aTaxpayerCaseSearchCacheResult);
                        if (dateFieldValue != null){
                            if ((dateFieldValue.equals(to)) || (dateFieldValue.equals(from)) 
                                    || (dateFieldValue.after(from) && dateFieldValue.before(to)))
                            {
                                filteredTaxpayerCaseSearchCacheResultList.add(aTaxpayerCaseSearchCacheResult);
                            }
                        }
                    }//for
                }catch (Exception ex){
                    updateMessage(ex.getMessage());
                    return null;
                }
                return filteredTaxpayerCaseSearchCacheResultList;
            }

            @Override
            protected void succeeded() {
                try {
                    List<TaxpayerCaseSearchCacheResult> result = get();
                    if (result == null){
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }else{
                        setupSearchResultTable();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception was raised. " + ex.getMessage());
                }
            }
            
            private Date getTargetDate(TaxpayerCaseSearchCacheResult aTaxpayerCaseSearchCacheResult){
                if (TaxpayerCaseSearchCacheResultColumns.convertEnumValueToType(criteria.getSearchResultColumn()).equals(TaxpayerCaseSearchCacheResultColumns.STATUS_UPDATED)){
                    return aTaxpayerCaseSearchCacheResult.getLatestTaxpayerStatusTimestamp();
                }
                return null;
            }
        };
        this.getCachedThreadPoolExecutorService().submit(filterTaxpayerCaseSearchCacheResultListTask);
    }
    
    private void filterTaxpayerCaseStatusCacheResultListByDate(final SearchResultColumnDateCriteraCreated criteria) {
        Task<List<TaxpayerCaseStatusCacheResult>> filterTaxpayerCaseStatusCacheResultListTask = new Task<List<TaxpayerCaseStatusCacheResult>>(){
            @Override
            protected List<TaxpayerCaseStatusCacheResult> call() throws Exception {
                filteredTaxpayerCaseStatusCacheResultList.clear();
                List<TaxpayerCaseStatusCacheResult> aTaxpayerCaseStatusCacheResultList = taxpayerCaseStatusCacheResultList.getTaxpayerCaseStatusCacheResultList();
                try{
                    if (ZcaValidator.isNullEmpty(criteria.getSearchResultColumn())){
                        throw new Exception("Please select a column for filter.");
                    }
                    if ((criteria.getFromDate() == null) || (criteria.getToDate() == null)){
                        throw new Exception("Please select a date range, from and to, for filter.");
                    }
                    Date from = ZcaCalendar.convertToDate(criteria.getFromDate());
                    Date to = ZcaCalendar.convertToDate(criteria.getToDate());
                    Date dateFieldValue;
                    for (TaxpayerCaseStatusCacheResult aTaxpayerCaseStatusCacheResult : aTaxpayerCaseStatusCacheResultList){
                        dateFieldValue = getTargetDate(aTaxpayerCaseStatusCacheResult);
                        if (dateFieldValue != null){
                            if ((dateFieldValue.equals(to)) || (dateFieldValue.equals(from)) 
                                    || (dateFieldValue.after(from) && dateFieldValue.before(to)))
                            {
                                filteredTaxpayerCaseStatusCacheResultList.add(aTaxpayerCaseStatusCacheResult);
                            }
                        }
                    }//for
                }catch (Exception ex){
                    updateMessage(ex.getMessage());
                    return null;
                }
                return filteredTaxpayerCaseStatusCacheResultList;
            }

            @Override
            protected void succeeded() {
                try {
                    List<TaxpayerCaseStatusCacheResult> result = get();
                    if (result == null){
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }else{
                        setupSearchResultTable();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception was raised. " + ex.getMessage());
                }
            }
            
            private Date getTargetDate(TaxpayerCaseStatusCacheResult aTaxpayerCaseStatusCacheResult){
                if (TaxpayerCaseStatusReportColumns.convertEnumValueToType(criteria.getSearchResultColumn()).equals(TaxpayerCaseStatusReportColumns.DEADLINE)){
                    return aTaxpayerCaseStatusCacheResult.getDeadline();
                }else if (TaxpayerCaseStatusReportColumns.convertEnumValueToType(criteria.getSearchResultColumn()).equals(TaxpayerCaseStatusReportColumns.EXTENSION)){
                    return aTaxpayerCaseStatusCacheResult.getExtension();
                }
                return null;
            }
        };
        this.getCachedThreadPoolExecutorService().submit(filterTaxpayerCaseStatusCacheResultListTask);
    }

    private void filterTaxpayerCaseSearchResultListByText(final SearchResultColumnTextCriteraCreated criteria) {
        Task<List<TaxpayerCaseSearchResult>> filterTaxpayerCaseSearchResultListTask = new Task<List<TaxpayerCaseSearchResult>>(){
            @Override
            protected List<TaxpayerCaseSearchResult> call() throws Exception {
                filteredTaxpayerCaseSearchResultList.clear();
                List<TaxpayerCaseSearchResult> aTaxpayerCaseSearchResultList = taxpayerCaseSearchResultList.getTaxpayerCaseSearchResultList();
                try{
                    if (ZcaValidator.isNullEmpty(criteria.getSearchResultColumn())){
                        throw new Exception("Please select a column for filter.");
                    }
                    if (ZcaValidator.isNullEmpty(criteria.getSearchResultColumnCriteriaValue())){
                        throw new Exception("Please type into keywords for filter.");
                    }
                    for (TaxpayerCaseSearchResult aTaxpayerCaseSearchResult : aTaxpayerCaseSearchResultList){
                        if (GardenData.calculateSimilarityByJaroWinklerStrategy(getTargetValue(aTaxpayerCaseSearchResult), 
                                                                                criteria.getSearchResultColumnCriteriaValue(), 
                                                                                criteria.isExactMatch(), 0.75) > 0.75)
                        {
                            filteredTaxpayerCaseSearchResultList.add(aTaxpayerCaseSearchResult);
                        }
                    }//for
                }catch (Exception ex){
                    updateMessage(ex.getMessage());
                    return null;
                }
                return filteredTaxpayerCaseSearchResultList;
            }

            @Override
            protected void succeeded() {
                try {
                    List<TaxpayerCaseSearchResult> result = get();
                    if (result == null){
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }else{
                        setupSearchResultTable();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception was raised. " + ex.getMessage());
                }
            }

            private String getTargetValue(TaxpayerCaseSearchResult aTaxpayerCaseSearchResult) {
                TaxpayerCaseSearchResultColumns column = TaxpayerCaseSearchResultColumns.convertEnumValueToType(criteria.getSearchResultColumn());
                switch (column){
                    case TAXPAYER_CASE_UUID:
                        return aTaxpayerCaseSearchResult.getTaxpayerCaseUuid();
                    case FEDERAL_FILING_STATUS:
                        return aTaxpayerCaseSearchResult.getFederalFilingStatus();
                    case TAXPAYER_INFORMATION:
                        return aTaxpayerCaseSearchResult.getTaxpayerInformation();
                    case OTHER_DEPENDENTS:
                        return aTaxpayerCaseSearchResult.getDependentInformation();
                    case STATUS:
                        return aTaxpayerCaseSearchResult.getLatestTaxpayerStatus();
                    case STATUS_OPERATOR:
                        return aTaxpayerCaseSearchResult.getLatestTaxpayerStatusOperatorName();
                    case TAXPAYER:
                        return aTaxpayerCaseSearchResult.getTaxpayerName();
                    case SPOUSE:
                        return aTaxpayerCaseSearchResult.getSpouseName();
                    case BALANCE_AND_TOTAL:
                        return aTaxpayerCaseSearchResult.getBalanceTotalText();
                    case CONFIRMED_AND_DEPOSIT:
                        return aTaxpayerCaseSearchResult.getConfirmedDepositedText();
                    default:
                        return null;
                }
            }
        };
        this.getCachedThreadPoolExecutorService().submit(filterTaxpayerCaseSearchResultListTask);
    }

    private void filterTaxpayerCaseSearchCacheResultListByText(final SearchResultColumnTextCriteraCreated criteria) {
        Task<List<TaxpayerCaseSearchCacheResult>> filterTaxpayerCaseSearchCacheResultListTask = new Task<List<TaxpayerCaseSearchCacheResult>>(){
            @Override
            protected List<TaxpayerCaseSearchCacheResult> call() throws Exception {
                filteredTaxpayerCaseSearchCacheResultList.clear();
                List<TaxpayerCaseSearchCacheResult> aTaxpayerCaseSearchCacheResultList = taxpayerCaseSearchCacheResultList.getTaxpayerCaseSearchCacheResultList();
                try{
                    if (ZcaValidator.isNullEmpty(criteria.getSearchResultColumn())){
                        throw new Exception("Please select a column for filter.");
                    }
                    if (ZcaValidator.isNullEmpty(criteria.getSearchResultColumnCriteriaValue())){
                        throw new Exception("Please type into keywords for filter.");
                    }
                    for (TaxpayerCaseSearchCacheResult aTaxpayerCaseSearchCacheResult : aTaxpayerCaseSearchCacheResultList){
                        if (GardenData.calculateSimilarityByJaroWinklerStrategy(getTargetValue(aTaxpayerCaseSearchCacheResult), 
                                                                                criteria.getSearchResultColumnCriteriaValue(), 
                                                                                criteria.isExactMatch(), 0.75) > 0.75)
                        {
                            filteredTaxpayerCaseSearchCacheResultList.add(aTaxpayerCaseSearchCacheResult);
                        }
                    }//for
                }catch (Exception ex){
                    updateMessage(ex.getMessage());
                    return null;
                }
                return filteredTaxpayerCaseSearchCacheResultList;
            }

            @Override
            protected void succeeded() {
                try {
                    List<TaxpayerCaseSearchCacheResult> result = get();
                    if (result == null){
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }else{
                        setupSearchResultTable();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception was raised. " + ex.getMessage());
                }
            }

            private String getTargetValue(TaxpayerCaseSearchCacheResult aTaxpayerCaseSearchCacheResult) {
                TaxpayerCaseSearchCacheResultColumns column = TaxpayerCaseSearchCacheResultColumns.convertEnumValueToType(criteria.getSearchResultColumn());
                switch (column){
                    case FEDERAL_FILING_STATUS:
                        return aTaxpayerCaseSearchCacheResult.getFederalFilingStatus();
                    case STATUS:
                        return aTaxpayerCaseSearchCacheResult.getLatestTaxpayerStatus();
                    case STATUS_OPERATOR:
                        return aTaxpayerCaseSearchCacheResult.getLatestTaxpayerStatusOperatorName();
                    case TAXPAYER:
                        return aTaxpayerCaseSearchCacheResult.getTaxpayerName();
                    case SPOUSE:
                        return aTaxpayerCaseSearchCacheResult.getSpouseName();
                    case BALANCE_AND_TOTAL:
                        return aTaxpayerCaseSearchCacheResult.getBalanceTotalText();
                    case CONFIRMED_AND_DEPOSIT:
                        return aTaxpayerCaseSearchCacheResult.getConfirmedDepositedText();
                    default:
                        return null;
                }
            }
        };
        this.getCachedThreadPoolExecutorService().submit(filterTaxpayerCaseSearchCacheResultListTask);
    }

    private void filterTaxpayerCaseStatusCacheResultListByText(final SearchResultColumnTextCriteraCreated criteria) {
        Task<List<TaxpayerCaseStatusCacheResult>> filterTaxpayerCaseStatusCacheResultListTask = new Task<List<TaxpayerCaseStatusCacheResult>>(){
            @Override
            protected List<TaxpayerCaseStatusCacheResult> call() throws Exception {
                filteredTaxpayerCaseStatusCacheResultList.clear();
                List<TaxpayerCaseStatusCacheResult> aTaxpayerCaseStatusCacheResultList = taxpayerCaseStatusCacheResultList.getTaxpayerCaseStatusCacheResultList();
                try{
                    if (ZcaValidator.isNullEmpty(criteria.getSearchResultColumn())){
                        throw new Exception("Please select a column for filter.");
                    }
                    if (ZcaValidator.isNullEmpty(criteria.getSearchResultColumnCriteriaValue())){
                        throw new Exception("Please type into keywords for filter.");
                    }
                    for (TaxpayerCaseStatusCacheResult aTaxpayerCaseStatusCacheResult : aTaxpayerCaseStatusCacheResultList){
                        if (GardenData.calculateSimilarityByJaroWinklerStrategy(getTargetValue(aTaxpayerCaseStatusCacheResult), 
                                                                                criteria.getSearchResultColumnCriteriaValue(), 
                                                                                criteria.isExactMatch(), 0.75) > 0.75)
                        {
                            filteredTaxpayerCaseStatusCacheResultList.add(aTaxpayerCaseStatusCacheResult);
                        }
                    }//for
                }catch (Exception ex){
                    updateMessage(ex.getMessage());
                    return null;
                }
                return filteredTaxpayerCaseStatusCacheResultList;
            }

            @Override
            protected void succeeded() {
                try {
                    List<TaxpayerCaseStatusCacheResult> result = get();
                    if (result == null){
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }else{
                        setupSearchResultTable();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception was raised. " + ex.getMessage());
                }
            }

            private String getTargetValue(TaxpayerCaseStatusCacheResult aTaxpayerCaseStatusCacheResult) {
                TaxpayerCaseStatusReportColumns column = TaxpayerCaseStatusReportColumns.convertEnumValueToType(criteria.getSearchResultColumn());
                switch (column){
                    case FEDERAL_FILING_STATUS:
                        return aTaxpayerCaseStatusCacheResult.getFederalFilingStatus();
                    case TAXPAYER:
                        return aTaxpayerCaseStatusCacheResult.getTaxpayerName();
                    case SPOUSE:
                        return aTaxpayerCaseStatusCacheResult.getSpouseName();
                    case SSN:
                        return aTaxpayerCaseStatusCacheResult.getPrimaryTaxpayerSsn();
                    case RESIDENCY:
                        return aTaxpayerCaseStatusCacheResult.getTaxpayerResidencyMemo();
                    case RECEIVE:
                        return aTaxpayerCaseStatusCacheResult.getReceiveTaxpayerCaseStatus();
                    case PAYMENT:
                        return aTaxpayerCaseStatusCacheResult.getBalanceTotalText();
                    case SCAN:
                        return aTaxpayerCaseStatusCacheResult.getTaxpayerCaseScanStatus();
                    case PREPARE:
                        return aTaxpayerCaseStatusCacheResult.getPrepareTaxMaterialStatus();
                    case REVIEW:
                        return aTaxpayerCaseStatusCacheResult.getManangerReviewStatus();
                    case CPA_REVIEW:
                        return aTaxpayerCaseStatusCacheResult.getCpaApprovedStatus();
                    case NOTIFY:
                        return aTaxpayerCaseStatusCacheResult.getNotifySignatureAndPaymentStatus();
                    case PICKUP:
                        return aTaxpayerCaseStatusCacheResult.getPickupByCustomerStatus();
                    case SIGN:
                        return aTaxpayerCaseStatusCacheResult.getCustomerSignatureStatus();
                    case EFILE:
                        return aTaxpayerCaseStatusCacheResult.getEfileStatus();
                    case CONTACT:
                        return aTaxpayerCaseStatusCacheResult.getContactInfo();
                    case MEMO:
                        return aTaxpayerCaseStatusCacheResult.getTaxpayerCaseMemoHistory();
                    default:
                        return null;
                }
            }
        };
        this.getCachedThreadPoolExecutorService().submit(filterTaxpayerCaseStatusCacheResultListTask);
    }
    
    private void resetCustomerSearchResultList() {
        Task<List<CustomerSearchResult>> resetCustomerSearchResultListTask = new Task<List<CustomerSearchResult>>(){
            @Override
            protected List<CustomerSearchResult> call() throws Exception {
                filteredCustomerSearchResultList.clear();
                resetFilterSeachResult();
                return filteredCustomerSearchResultList;
            }

            @Override
            protected void succeeded() {
                try {
                    List<CustomerSearchResult> result = get();
                    if (result != null){
                        setupSearchResultTable();
                        //resetFilterControls(CustomerSearchResultColumns.getEnumValueList(false));
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception was raised. " + ex.getMessage());
                }
                
            }
        
        };
        this.getCachedThreadPoolExecutorService().submit(resetCustomerSearchResultListTask);
    }
    
    private void resetTaxcorpCaseSearchResultList() {
        Task<List<TaxcorpCaseSearchResult>> resetTaxcorpCaseSearchResultListTask = new Task<List<TaxcorpCaseSearchResult>>(){
            @Override
            protected List<TaxcorpCaseSearchResult> call() throws Exception {
                filteredTaxcorpCaseSearchResultList.clear();
                resetFilterSeachResult();
                return filteredTaxcorpCaseSearchResultList;
            }

            @Override
            protected void succeeded() {
                try {
                    List<TaxcorpCaseSearchResult> result = get();
                    if (result != null){
                        setupSearchResultTable();
                        //resetFilterControls(TaxcorpCaseSearchResultColumns.getEnumValueList(false));
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception was raised. " + ex.getMessage());
                }
                
            }
        
        };
        this.getCachedThreadPoolExecutorService().submit(resetTaxcorpCaseSearchResultListTask);
    }
    
    private void resetTaxpayerCaseSearchResultList() {
        Task<List<TaxpayerCaseSearchResult>> resetTaxpayerCaseSearchResultListTask = new Task<List<TaxpayerCaseSearchResult>>(){
            @Override
            protected List<TaxpayerCaseSearchResult> call() throws Exception {
                filteredTaxpayerCaseSearchResultList.clear();
                resetFilterSeachResult();
                return filteredTaxpayerCaseSearchResultList;
            }

            @Override
            protected void succeeded() {
                try {
                    List<TaxpayerCaseSearchResult> result = get();
                    if (result != null){
                        setupSearchResultTable();
                        //resetFilterControls(TaxpayerCaseSearchResultColumns.getEnumValueList(false));
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception was raised. " + ex.getMessage());
                }
                
            }
        
        };
        this.getCachedThreadPoolExecutorService().submit(resetTaxpayerCaseSearchResultListTask);
    }
    
    private void resetTaxpayerCaseSearchCacheResultList() {
        Task<List<TaxpayerCaseSearchCacheResult>> resetTaxpayerCaseSearchCacheResultListTask = new Task<List<TaxpayerCaseSearchCacheResult>>(){
            @Override
            protected List<TaxpayerCaseSearchCacheResult> call() throws Exception {
                filteredTaxpayerCaseSearchCacheResultList.clear();
                resetFilterSeachResult();
                return filteredTaxpayerCaseSearchCacheResultList;
            }

            @Override
            protected void succeeded() {
                try {
                    List<TaxpayerCaseSearchCacheResult> result = get();
                    if (result != null){
                        setupSearchResultTable();
                        //resetFilterControls(TaxpayerCaseSearchCacheResultColumns.getEnumValueList(false));
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception was raised. " + ex.getMessage());
                }
                
            }
        
        };
        this.getCachedThreadPoolExecutorService().submit(resetTaxpayerCaseSearchCacheResultListTask);
    }
    
    private void resetTaxpayerCaseStatusCacheResultList() {
        Task<List<TaxpayerCaseStatusCacheResult>> resetTaxpayerCaseStatusCacheResultListTask = new Task<List<TaxpayerCaseStatusCacheResult>>(){
            @Override
            protected List<TaxpayerCaseStatusCacheResult> call() throws Exception {
                filteredTaxpayerCaseStatusCacheResultList.clear();
                resetFilterSeachResult();
                return filteredTaxpayerCaseStatusCacheResultList;
            }

            @Override
            protected void succeeded() {
                try {
                    List<TaxpayerCaseStatusCacheResult> result = get();
                    if (result != null){
                        setupSearchResultTable();
                        //resetFilterControls(TaxpayerCaseStatusReportColumns.getEnumValueList(false));
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Exception was raised. " + ex.getMessage());
                }
                
            }
        
        };
        this.getCachedThreadPoolExecutorService().submit(resetTaxpayerCaseStatusCacheResultListTask);
    }

    private void exportToExcelFile(final TableView searchResultTable) {
        if (SwingUtilities.isEventDispatchThread()){
            exportToExcelFileHelper(searchResultTable);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    exportToExcelFileHelper(searchResultTable);
                }
            });
        }
    }
    
    private String requestExcelFileFullPath(){
    
        JFileChooser jfc = new JFileChooser();
        jfc.setDialogTitle("Save As");
        jfc.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel File", "xls");
        jfc.addChoosableFileFilter(filter);

        int returnValue = jfc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return jfc.getSelectedFile().getPath();
        }else{
            return null;
        }
    }
    
    private void exportToExcelFileHelper(final TableView searchResultTable){
        String filePath = requestExcelFileFullPath();
        if (filePath == null){
            return;
        }
        if (!filePath.endsWith(".xls")){
            filePath += ".xls";
        }
        if (ZcaNio.isValidFile(filePath)){
            if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Some file has the same name. Do you want to overwrite it?") == JOptionPane.YES_OPTION){
                try {
                    ZcaNio.deleteFile(filePath);
                } catch (IOException ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Cannot overwrite the existing file. Please choose a different file name.");
                    return;
                }
            }else{
                return;
            }
        }
        
        Workbook workbook = new HSSFWorkbook();
        Sheet spreadsheet = workbook.createSheet("Search Result");
        
        Row headerRow = spreadsheet.createRow(0);
        Font headerFont= workbook.createFont();
        headerFont.setFontHeightInPoints((short)10);
        headerFont.setFontName("Arial");
        //headerFont.setColor(IndexedColors.BLACK.getIndex());
        headerFont.setFontHeightInPoints((short)12);
        headerFont.setBold(true);
        headerFont.setItalic(false);
        
        CellStyle headerStyle= workbook.createCellStyle();
        //headerStyle.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
        //headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFont(headerFont);
        
        Cell aCell;
        Object tcObj;
        TableColumn tc;
        Object data;
        Row row;
        for (int i = 0; i < searchResultTable.getItems().size(); i++) {
            row = spreadsheet.createRow(i + 1);
            for (int j = 0; j < searchResultTable.getColumns().size(); j++) {
                tcObj = searchResultTable.getColumns().get(j);
                if (tcObj instanceof TableColumn){
                    tc = (TableColumn)tcObj;
                    data = tc.getCellData(i);
                    aCell = row.createCell(j);
                    if(data == null) { 
                        aCell.setCellValue("");
                    }else if (data instanceof Date){
                        aCell.setCellValue(ZcaCalendar.convertToMMddyyyy((Date)data, "-")); 
                    }else {
                        aCell.setCellValue(data.toString());
                    }
                    if (headerRow.getCell(j) == null){
                        aCell = headerRow.createCell(j);
                        aCell.setCellValue(tc.getText());    //header of the column
                        aCell.setCellStyle(headerStyle);
                        spreadsheet.autoSizeColumn(j);
                    }
                }
            }
        }//loop
        try {
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            Thread.sleep(250);
            PeonyFaceUtils.openFile(new File(filePath));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            PeonyFaceUtils.displayErrorMessageDialog(ex.getMessage());
        } catch (InterruptedException ex) {
            //Exceptions.printStackTrace(ex);
            PeonyFaceUtils.publishMessageOntoOutputWindow("Cannot open the file: " + filePath);
        }
    }

    private void handleSearchResultColumnDateCriteraCreated(SearchResultColumnDateCriteraCreated criteria){
        if (taxcorpTaxFilingCaseSearchResultList != null){
            filterTaxcorpTaxFilingCaseSearchResultListByDate(criteria);
        }else if (customerSearchResultList != null){
            filterCustomerSearchResultListByDate(criteria);
        }else if (taxcorpCaseSearchResultList != null){
            filterTaxcorpCaseSearchResultListByDate(criteria);
        }else if (taxpayerCaseSearchResultList != null){
            filterTaxpayerCaseSearchResultListByDate(criteria);
        }else if (taxpayerCaseSearchCacheResultList != null){
            filterTaxpayerCaseSearchCacheResultListByDate(criteria);
        }else if (taxpayerCaseStatusCacheResultList != null){
            filterTaxpayerCaseStatusCacheResultListByDate(criteria);
        }
    }
    
    private void handleSearchResultColumnTextCriteraCreated(SearchResultColumnTextCriteraCreated criteria) {
        if (taxcorpTaxFilingCaseSearchResultList != null){
            filterTaxcorpTaxFilingCaseSearchResultListByText(criteria);
        }else if (customerSearchResultList != null){
            filterCustomerSearchResultListByText(criteria);
        }else if (taxcorpCaseSearchResultList != null){
            filterTaxcorpCaseSearchResultListByText(criteria);
        }else if (taxpayerCaseSearchResultList != null){
            filterTaxpayerCaseSearchResultListByText(criteria);
        }else if (taxpayerCaseSearchCacheResultList != null){
            filterTaxpayerCaseSearchCacheResultListByText(criteria);
        }else if (taxpayerCaseStatusCacheResultList != null){
            filterTaxpayerCaseStatusCacheResultListByText(criteria);
        }
    }
}
