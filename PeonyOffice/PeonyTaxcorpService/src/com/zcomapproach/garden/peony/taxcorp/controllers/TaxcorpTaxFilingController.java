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

package com.zcomapproach.garden.peony.taxcorp.controllers;

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.peony.controls.PeonyButtonTableCell;
import com.zcomapproach.garden.peony.kernel.services.PeonyBusinessService;
import com.zcomapproach.garden.peony.kernel.services.PeonyKernelService;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxcorpService;
import com.zcomapproach.garden.peony.security.PeonyPrivilege;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.taxcorp.data.PeonyTaxFilingCaseDateEditingTableCell;
import com.zcomapproach.garden.peony.taxcorp.data.TaxFilingStatusDateEditingCell;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.dialogs.MemoDataEntryDialog;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.events.PeonyTaxFilingCaseListDeleted;
import com.zcomapproach.garden.peony.view.events.PeonyTaxFilingCaseUpdated;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.constant.GardenTaxFilingStatus;
import com.zcomapproach.garden.persistence.constant.TaxFilingPeriod;
import com.zcomapproach.garden.persistence.constant.TaxFilingType;
import com.zcomapproach.garden.persistence.entity.G02TaxFilingCase;
import com.zcomapproach.garden.persistence.peony.PeonyMemo;
import com.zcomapproach.garden.persistence.peony.PeonyTaxFilingCase;
import com.zcomapproach.garden.persistence.peony.PeonyTaxFilingCaseList;
import com.zcomapproach.garden.persistence.peony.PeonyTaxcorpCase;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.util.GardenData;
import com.zcomapproach.garden.util.GardenFaceX;
import com.zcomapproach.garden.util.GardenSorter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javax.swing.JOptionPane;
import org.controlsfx.glyphfont.FontAwesome;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class TaxcorpTaxFilingController extends PeonyTaxcorpServiceController implements PeonyFaceEventListener {
    @FXML
    private Label taxcorpTaxFilingTypeLabel;
    
    @FXML
    private FlowPane taxFilingPeriodFunctionalFlowPane;
    
    @FXML
    private TableView<PeonyTaxFilingCase> taxcorpTaxFilingTableView;
    
    private final HashMap<TaxFilingPeriod, CheckBox> taxFilingPeriodCheckBoxStorage = new HashMap<>();
    
    private final PeonyTaxcorpCase targetPeonyTaxcorpCase;

    private final TaxFilingType targetTaxFilingType;
    
    private DatePicker displayFrom;
    
    private DatePicker displayTo;
    
    private final String taxFilingMemosColumnHeaderName = "Memo List";

    public TaxcorpTaxFilingController(PeonyTaxcorpCase targetPeonyTaxcorpCase, TaxFilingType targetTaxFilingType) {
        super(targetPeonyTaxcorpCase);
        if (targetPeonyTaxcorpCase == null){
            targetPeonyTaxcorpCase = new PeonyTaxcorpCase();
            targetPeonyTaxcorpCase.getTaxcorpCase().setTaxcorpCaseUuid(GardenData.generateUUIDString());
        }
        this.targetPeonyTaxcorpCase = targetPeonyTaxcorpCase;
        this.targetTaxFilingType = targetTaxFilingType;
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof PeonyTaxFilingCaseListDeleted){
            handlePeonyTaxFilingCaseListDeleted((PeonyTaxFilingCaseListDeleted)event);
        }else if (event instanceof PeonyTaxFilingCaseUpdated){
            handlePeonyTaxFilingCaseUpdated((PeonyTaxFilingCaseUpdated)event);
        }
    }

    private void handlePeonyTaxFilingCaseListDeleted(final PeonyTaxFilingCaseListDeleted peonyTaxFilingCaseListDeleted) {
        if (Platform.isFxApplicationThread()){
            handlePeonyTaxFilingCaseListDeletedHelper(peonyTaxFilingCaseListDeleted);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    handlePeonyTaxFilingCaseListDeletedHelper(peonyTaxFilingCaseListDeleted);
                }
            });
        }
    }
    
    private void handlePeonyTaxFilingCaseListDeletedHelper(final PeonyTaxFilingCaseListDeleted peonyTaxFilingCaseListDeleted) {
        if ((peonyTaxFilingCaseListDeleted != null) && (peonyTaxFilingCaseListDeleted.getPeonyTaxFilingCaseList() != null)){
            List<PeonyTaxFilingCase> deletedPeonyTaxFilingCaseList = peonyTaxFilingCaseListDeleted.getPeonyTaxFilingCaseList().getPeonyTaxFilingCaseList();
            ObservableList<PeonyTaxFilingCase> aPeonyTaxFilingCaseList = taxcorpTaxFilingTableView.getItems();
            if (aPeonyTaxFilingCaseList != null){
                for (PeonyTaxFilingCase deletedPeonyTaxFilingCase : deletedPeonyTaxFilingCaseList){
                    taxcorpTaxFilingTableView.getItems().remove(deletedPeonyTaxFilingCase);
                }
            }
            taxcorpTaxFilingTableView.refresh();
        }
    }

    private void handlePeonyTaxFilingCaseUpdated(PeonyTaxFilingCaseUpdated event) {
        if (Platform.isFxApplicationThread()){
            taxcorpTaxFilingTableView.refresh();
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    taxcorpTaxFilingTableView.refresh();
                }
            });
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //taxcorpTaxFilingTypeLabel.setText(targetTaxFilingType.value());
        taxcorpTaxFilingTypeLabel.setText("");
        
        /**
         * Tax-filing period checkbox by authorization: generate or delete
         */
        if ((PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.TAX_FILING_GENERATE)) 
                || (PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.TAX_FILING_DELETE))){
            taxFilingPeriodFunctionalFlowPane.getChildren().addAll(constructTaxFilingPeriodCheckBoxList(targetTaxFilingType));
        }
        
        /**
         * Generate tax-filing button by authorization
         */
        if (PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.TAX_FILING_GENERATE)){
            Button generateButton = new Button("Generate");
            generateButton.setGraphic(PeonyGraphic.getImageView("database_gear.png"));
            generateButton.setOnAction((ActionEvent e) -> {
                if (PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.TAX_FILING_GENERATE)){
                    generateTaxcorpTaxFilingRecords();
                }else{
                    PeonyFaceUtils.displayPrivilegeErrorMessageDialog();
                }
            });
            taxFilingPeriodFunctionalFlowPane.getChildren().add(generateButton);
        }
        
        /**
         * Delete tax-filing button by authorization
         */
        if (PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.TAX_FILING_DELETE)){
            Button deleteButton = new Button("Delete");
            deleteButton.setGraphic(PeonyGraphic.getImageView("database_delete.png"));
            deleteButton.setOnAction((ActionEvent e) -> {
                if (PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.TAX_FILING_DELETE)){
                    displayTaxFilingRecordDeletionDialog();
                }else{
                    PeonyFaceUtils.displayPrivilegeErrorMessageDialog();
                }
            });
            FlowPane.setMargin(deleteButton, new Insets(0, 0, 0, 5));
            taxFilingPeriodFunctionalFlowPane.getChildren().add(deleteButton);
        }
        
        HBox aHBox = new HBox();
        //displayFrom
        displayFrom = new DatePicker();
        if (taxFilingPeriodFunctionalFlowPane.getChildren().isEmpty()){
            HBox.setMargin(displayFrom, new Insets(2, 2, 2, 2));
        }else{
            HBox.setMargin(displayFrom, new Insets(2, 2, 2, 25));
        }
        displayFrom.setValue(ZcaCalendar.convertToLocalDate(new Date()));
        displayFrom.setTooltip(new Tooltip("Display From"));
        aHBox.getChildren().add(displayFrom);
        //displayTo
        displayTo = new DatePicker();
        displayTo.setTooltip(new Tooltip("Display To"));
        HBox.setMargin(displayTo, new Insets(2, 2, 2, 2));
        displayTo.setValue(ZcaCalendar.convertToLocalDate(new Date()));
        aHBox.getChildren().add(displayTo);
        //displayButton
        Button refreshButton = new Button("Refresh");
        refreshButton.setTooltip(new Tooltip("Refresh table by selected period"));
        HBox.setMargin(refreshButton, new Insets(2, 2, 2, 2));
        refreshButton.setOnAction((ActionEvent e) -> {
            if ((displayFrom.getValue() == null) || (displayTo.getValue() == null)){
                PeonyFaceUtils.displayErrorMessageDialog("Please select a date-period to display records in such a period.");
                return;
            }
            refreshTaxFilingTableView();
        });
        aHBox.getChildren().add(refreshButton);
        taxFilingPeriodFunctionalFlowPane.getChildren().add(aHBox);
        
        initializeTaxFilingCaseTableView();
    }
    
    private void initializeTaxFilingCaseTableView() {
        taxcorpTaxFilingTableView.getColumns().clear();
        taxcorpTaxFilingTableView.setEditable(true);
        
        TableColumn<PeonyTaxFilingCase, String> taxFilingTypeColumn = new TableColumn<>("Tax Filing");
        taxFilingTypeColumn.setCellValueFactory(new PropertyValueFactory<>("taxFilingType"));
        taxcorpTaxFilingTableView.getColumns().add(taxFilingTypeColumn);
        
        TableColumn<PeonyTaxFilingCase, String> taxFilingPeriodColumn = new TableColumn<>("Period");
        taxFilingPeriodColumn.setCellValueFactory(new PropertyValueFactory<>("taxFilingPeriod"));
        taxcorpTaxFilingTableView.getColumns().add(taxFilingPeriodColumn);
        
        TableColumn<PeonyTaxFilingCase, Date> taxFilingDeadlineColumn = new TableColumn<>("Deadline");
        taxFilingDeadlineColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getDeadline()));
        taxFilingDeadlineColumn.setCellFactory((TableColumn<PeonyTaxFilingCase, Date> param) -> new PeonyTaxFilingCaseDateEditingTableCell());
        taxFilingDeadlineColumn.setOnEditCommit(
            (TableColumn.CellEditEvent<PeonyTaxFilingCase, Date> newDatePickingValue) -> {
                saveDeadlineDate(((PeonyTaxFilingCase)newDatePickingValue.getTableView().getItems()
                .get(newDatePickingValue.getTablePosition().getRow())), newDatePickingValue.getNewValue());
            });
        taxcorpTaxFilingTableView.getColumns().add(taxFilingDeadlineColumn);
        
        TableColumn<PeonyTaxFilingCase, Date> taxFilingExtensionColumn = new TableColumn<>("Extension");
        taxFilingExtensionColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getExtensionDate()));
        taxFilingExtensionColumn.setCellFactory((TableColumn<PeonyTaxFilingCase, Date> param) -> new PeonyTaxFilingCaseDateEditingTableCell());
        taxFilingExtensionColumn.setOnEditCommit(
            (TableColumn.CellEditEvent<PeonyTaxFilingCase, Date> newDatePickingValue) -> {
                saveDeadlineExtensionDate(((PeonyTaxFilingCase)newDatePickingValue.getTableView().getItems()
                .get(newDatePickingValue.getTablePosition().getRow())), newDatePickingValue.getNewValue());
            });
        taxcorpTaxFilingTableView.getColumns().add(taxFilingExtensionColumn);
        
        TableColumn<PeonyTaxFilingCase, Date> receivedColumn = new TableColumn<>("Received");
        receivedColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getReceivedDate()));
        receivedColumn.setCellFactory((TableColumn<PeonyTaxFilingCase, Date> param) -> new TaxFilingStatusDateEditingCell());
        receivedColumn.setOnEditCommit(
            (TableColumn.CellEditEvent<PeonyTaxFilingCase, Date> newDatePickingValue) -> {
                saveTaxFilingStatusDate(((PeonyTaxFilingCase)newDatePickingValue.getTableView().getItems()
                .get(newDatePickingValue.getTablePosition().getRow())), newDatePickingValue.getNewValue(), GardenTaxFilingStatus.RECEIVED);
            });
        taxcorpTaxFilingTableView.getColumns().add(receivedColumn);
        
        TableColumn<PeonyTaxFilingCase, Date> preparedColumn = new TableColumn<>("Prepared");
        preparedColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getPreparedDate()));
        preparedColumn.setCellFactory((TableColumn<PeonyTaxFilingCase, Date> param) -> new TaxFilingStatusDateEditingCell());
        preparedColumn.setOnEditCommit(
            (TableColumn.CellEditEvent<PeonyTaxFilingCase, Date> newDatePickingValue) -> {
                saveTaxFilingStatusDate(((PeonyTaxFilingCase)newDatePickingValue.getTableView().getItems()
                .get(newDatePickingValue.getTablePosition().getRow())), newDatePickingValue.getNewValue(), GardenTaxFilingStatus.PREPARED);
            });
        taxcorpTaxFilingTableView.getColumns().add(preparedColumn);
        
        TableColumn<PeonyTaxFilingCase, Date> completedColumn = new TableColumn<>("Completed");
        completedColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getCompletedDate()));
        completedColumn.setCellFactory((TableColumn<PeonyTaxFilingCase, Date> param) -> new TaxFilingStatusDateEditingCell());
        completedColumn.setOnEditCommit(
            (TableColumn.CellEditEvent<PeonyTaxFilingCase, Date> newDatePickingValue) -> {
                saveTaxFilingStatusDate(((PeonyTaxFilingCase)newDatePickingValue.getTableView().getItems()
                .get(newDatePickingValue.getTablePosition().getRow())), newDatePickingValue.getNewValue(), GardenTaxFilingStatus.COMPLETED);
            });
        taxcorpTaxFilingTableView.getColumns().add(completedColumn);
        
        TableColumn<PeonyTaxFilingCase, Date> eFiledColumn = new TableColumn<>("Tax eFiled");
        eFiledColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getEFiledDate()));
        eFiledColumn.setCellFactory((TableColumn<PeonyTaxFilingCase, Date> param) -> new TaxFilingStatusDateEditingCell());
        eFiledColumn.setOnEditCommit(
            (TableColumn.CellEditEvent<PeonyTaxFilingCase, Date> newDatePickingValue) -> {
                saveTaxFilingStatusDate(((PeonyTaxFilingCase)newDatePickingValue.getTableView().getItems()
                .get(newDatePickingValue.getTablePosition().getRow())), newDatePickingValue.getNewValue(), GardenTaxFilingStatus.PICKUP);
            });
        taxcorpTaxFilingTableView.getColumns().add(eFiledColumn);
        
        if (TaxFilingType.TAX_RETURN.equals(targetTaxFilingType)){
            TableColumn<PeonyTaxFilingCase, Date> extEFiledColumn = new TableColumn<>("Ext. eFiled");
            extEFiledColumn.setCellValueFactory(cellData -> new SimpleObjectProperty(cellData.getValue().getExtensionEFiledDate()));
            extEFiledColumn.setCellFactory((TableColumn<PeonyTaxFilingCase, Date> param) -> new TaxFilingStatusDateEditingCell());
            extEFiledColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<PeonyTaxFilingCase, Date> newDatePickingValue) -> {
                    saveTaxFilingStatusDate(((PeonyTaxFilingCase)newDatePickingValue.getTableView().getItems()
                    .get(newDatePickingValue.getTablePosition().getRow())), newDatePickingValue.getNewValue(), GardenTaxFilingStatus.EXT_EFILE);
                });
            taxcorpTaxFilingTableView.getColumns().add(extEFiledColumn);
        }
        
        TableColumn<PeonyTaxFilingCase, String> taxFilingMemosColumn = new TableColumn<>();
        taxFilingMemosColumn.setText(taxFilingMemosColumnHeaderName);
        taxFilingMemosColumn.setCellValueFactory(new PropertyValueFactory<>("memo"));
        taxFilingMemosColumn.prefWidthProperty().setValue(450.0);
        taxcorpTaxFilingTableView.getColumns().add(taxFilingMemosColumn);

        if (PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.TAX_FILING_DELETE)){
            TableColumn<PeonyTaxFilingCase, Button> deleteTableColumn = new TableColumn("");
            deleteTableColumn.setCellFactory(PeonyButtonTableCell.<PeonyTaxFilingCase>callbackForTableColumn("", 
                    FontAwesome.Glyph.TIMES_CIRCLE_ALT, Color.DARKBLUE, 
                    new Tooltip("Delete"), (PeonyTaxFilingCase aPeonyTaxFilingCase) -> {
                        if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to permentantly delete this specific record?") == JOptionPane.YES_OPTION){
                            deletePeonyTaxFilingCase(aPeonyTaxFilingCase);
                        }
                        return aPeonyTaxFilingCase;
                    }));
            deleteTableColumn.setPrefWidth(38.00);
            taxcorpTaxFilingTableView.getColumns().add(deleteTableColumn);
        }
        
        /**
         * Double-click memo field to popup memo-editor
         */
        taxcorpTaxFilingTableView.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2){
                ObservableList<TablePosition> posList = taxcorpTaxFilingTableView.getSelectionModel().getSelectedCells();
                if (posList != null){
                    for (TablePosition pos : posList){
                        TableColumn<PeonyTaxFilingCase, String> column = pos.getTableColumn();
                        if (taxFilingMemosColumnHeaderName.equalsIgnoreCase(column.getText())){
                            Object obj = taxcorpTaxFilingTableView.getSelectionModel().getSelectedItem();
                            if (obj instanceof PeonyTaxFilingCase){
                                PeonyTaxFilingCase aPeonyTaxFilingCase = ((PeonyTaxFilingCase)obj);
                                MemoDataEntryDialog aMemoDataEntryDialog = new MemoDataEntryDialog(null, true);
                                aMemoDataEntryDialog.addPeonyFaceEventListener(this);
                                PeonyMemo memo = new PeonyMemo();
                                memo.getMemo().setMemo(aPeonyTaxFilingCase.getMemo());
                                aMemoDataEntryDialog.launchMemoDataEntryDialog("Memo: ", memo, true, false, true, 
                                        aPeonyTaxFilingCase);
                            }
                            break;
                        }
                    }//for
                }
            }
        });
        
        taxcorpTaxFilingTableView.getSelectionModel().setCellSelectionEnabled(true);
        taxcorpTaxFilingTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        GardenFaceX.installCopyPasteHandler(taxcorpTaxFilingTableView);
        
        loadTaxFilingTableView();
    }

    private void deletePeonyTaxFilingCase(final PeonyTaxFilingCase aPeonyTaxFilingCase) {
        Task<PeonyTaxFilingCase> deletePeonyTaxFilingCaseTask = new Task<PeonyTaxFilingCase>(){
            @Override
            protected PeonyTaxFilingCase call() throws Exception {
                try{
                    G02TaxFilingCase aG02TaxFilingCase = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                                                            .deleteEntity_XML(G02TaxFilingCase.class, GardenRestParams.Business.deleteTaxFilingCaseByTaxFilingUuidRestParams(
                                                                aPeonyTaxFilingCase.getTaxFilingCase().getTaxFilingUuid()));
                    if (aG02TaxFilingCase == null){
                        return null;
                    }else{
                        taxcorpTaxFilingTableView.getItems().remove(aPeonyTaxFilingCase);
                        return aPeonyTaxFilingCase;
                    }
                }catch (Exception ex){
                    PeonyFaceUtils.publishMessageOntoOutputWindow("Tax file record deletion got technical errors. " + ex.getMessage());
                    return null;
                }
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyTaxFilingCase result = get();
                    if (result != null){
                        PeonyFaceUtils.displayInformationMessageDialog("The selected tax filing record is deleted.");
                    }
                    taxcorpTaxFilingTableView.refresh();
                } catch (InterruptedException | ExecutionException ex) {
                    PeonyFaceUtils.displayErrorMessageDialog("Technical error. " + ex.getMessage());
                }
            }
        };
        this.getCachedThreadPoolExecutorService().submit(deletePeonyTaxFilingCaseTask);
    }
    
    private TableCell<PeonyTaxFilingCase, Date> createDateTableCell(){
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        TableCell<PeonyTaxFilingCase, Date> aDateTableCell = new TableCell<PeonyTaxFilingCase, Date>(){
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
    }
    
    private void refreshTaxFilingTableView() {
        Task<List<PeonyTaxFilingCase>> refreshTaxFilingTableViewTask = new Task<List<PeonyTaxFilingCase>>(){
            @Override
            protected List<PeonyTaxFilingCase> call() throws Exception {
                Date fromDate = ZcaCalendar.convertToDate(displayFrom.getValue());
                Date toDate = ZcaCalendar.convertToDate(displayTo.getValue());
                
                PeonyTaxFilingCaseList peonyTaxFilingCaseList = Lookup.getDefault().lookup(PeonyBusinessService.class)
                        .getPeonyBusinessRestClient().findEntity_XML(PeonyTaxFilingCaseList.class, 
                                GardenRestParams.Business.findPeonyTaxFilingCaseListByTaxcorpCaseUuidAndTypeAndDateRangeRestParams(
                                        targetPeonyTaxcorpCase.getTaxcorpCase().getTaxcorpCaseUuid(), targetTaxFilingType.value(), 
                                        Long.toString(fromDate.getTime()), Long.toString(toDate.getTime())));
                if (peonyTaxFilingCaseList == null){
                    return new ArrayList<>();
                }else{
                    GardenSorter.sortPeonyTaxFilingCaseListByDeadline(peonyTaxFilingCaseList.getPeonyTaxFilingCaseList(), true);
                    return peonyTaxFilingCaseList.getPeonyTaxFilingCaseList();
                }
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayJfxTaskFailedErrorMessageDialog(getMessage());
            }

            @Override
            protected void succeeded() {
                try {
                    //set the current display-period for tax-filing records
                    List<PeonyTaxFilingCase> result = get();
                    if ((result == null) || (result.isEmpty())){
                        PeonyFaceUtils.displayWarningMessageDialog("No result for your date range selection.");
                    }else{
                        //Clear the existing ones if some there
                        taxcorpTaxFilingTableView.getItems().clear();
                        taxcorpTaxFilingTableView.setItems(FXCollections.observableArrayList(result));
                        taxcorpTaxFilingTableView.refresh();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(refreshTaxFilingTableViewTask);
    }
    
    private void loadTaxFilingTableView() {
        Task<List<PeonyTaxFilingCase>> loadTaxFilingRecordsTask = new Task<List<PeonyTaxFilingCase>>(){
            @Override
            protected List<PeonyTaxFilingCase> call() throws Exception {
                List<PeonyTaxFilingCase> result = new ArrayList<>();
                PeonyTaxFilingCaseList peonyTaxFilingCaseList = Lookup.getDefault().lookup(PeonyBusinessService.class)
                        .getPeonyBusinessRestClient().findEntity_XML(PeonyTaxFilingCaseList.class, 
                                GardenRestParams.Business.findPeonyTaxFilingCaseListByTaxcorpCaseUuidAndTypeRestParams(
                                        targetPeonyTaxcorpCase.getTaxcorpCase().getTaxcorpCaseUuid(), targetTaxFilingType.value()));
                if (peonyTaxFilingCaseList != null){
                    List<PeonyTaxFilingCase> aPeonyTaxFilingCaseList = peonyTaxFilingCaseList.getPeonyTaxFilingCaseList();
                    for (PeonyTaxFilingCase aPeonyTaxFilingCase : aPeonyTaxFilingCaseList){
                        if (TaxFilingType.TAX_RETURN.equals(targetTaxFilingType)){
                            //for tax-return, all the records are displayed
                            result.add(aPeonyTaxFilingCase);
                        }else{
                            if (displayTaxFilingByDefaultDateRangeRules(aPeonyTaxFilingCase.getTaxFilingCase())){
                                result.add(aPeonyTaxFilingCase);
                            }
                        }
                    }
                }
                GardenSorter.sortPeonyTaxFilingCaseListByDeadline(result, true);
                return result;
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayJfxTaskFailedErrorMessageDialog(getMessage());
            }

            @Override
            protected void succeeded() {
                try {
                    //set the current display-period for tax-filing records
                    List<PeonyTaxFilingCase> result = get();
                    try{
                        displayFrom.setValue(ZcaCalendar.convertToLocalDate(result.get(result.size()-1).getDeadline()));
                        displayTo.setValue(ZcaCalendar.convertToLocalDate(result.get(0).getDeadline()));
                    }catch (Exception ex){
                        displayFrom.setValue(ZcaCalendar.convertToLocalDate(new Date()));
                        displayTo.setValue(ZcaCalendar.convertToLocalDate(new Date()));
                    }
                    //Clear the existing ones if some there
                    taxcorpTaxFilingTableView.getItems().clear();
                    taxcorpTaxFilingTableView.setItems(FXCollections.observableArrayList(result));
                    taxcorpTaxFilingTableView.refresh();
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(loadTaxFilingRecordsTask);
    }
    
    /**
     * 
     * @param aG02TaxFilingCase
     * @return 
     */
    private boolean displayTaxFilingByDefaultDateRangeRules(G02TaxFilingCase aG02TaxFilingCase) {
        Date today = new Date();
        Date thresholdTop = new Date();
        Date thresholdBottom = new Date();
        TaxFilingPeriod aTaxFilingPeriod = TaxFilingPeriod.convertEnumValueToType(aG02TaxFilingCase.getTaxFilingPeriod());
        switch (aTaxFilingPeriod){
            case YEARLY:
                GregorianCalendar deadlineGc = new GregorianCalendar();
                deadlineGc.setTimeInMillis(aG02TaxFilingCase.getDeadline().getTime());
                GregorianCalendar day = new GregorianCalendar();
                day.set(Calendar.MONTH, deadlineGc.get(Calendar.MONTH));
                day.set(Calendar.DATE, deadlineGc.get(Calendar.DATE));
                thresholdTop = ZcaCalendar.addDates(day.getTime(), 366*2);
                thresholdBottom = ZcaCalendar.addDates(day.getTime(), -366*3);
                break;
            case FISCAL:
                thresholdTop = ZcaCalendar.addDates(today, 366*2);
                thresholdBottom = ZcaCalendar.addDates(today, -366*3);
                break;
            case QUARTERLY:
                thresholdTop = ZcaCalendar.addDates(today, 187);
                thresholdBottom = ZcaCalendar.addDates(today, -367);
                break;
            case MONTHLY:
            case MONTHLY_NY:
                thresholdTop = ZcaCalendar.addDates(today, 94);  //3 months
                thresholdBottom = ZcaCalendar.addDates(today, -367);
                break;
            case SEMI_MONTHLY:
                thresholdTop = ZcaCalendar.addDates(today, 94);
                thresholdBottom = ZcaCalendar.addDates(today, -367);
                break;
        }
        GregorianCalendar aGregorianCalendar = new GregorianCalendar();
        aGregorianCalendar.setTime(thresholdBottom);
        aGregorianCalendar.set(Calendar.MONTH, Calendar.JANUARY);
        aGregorianCalendar.set(Calendar.DAY_OF_MONTH, 1);
        thresholdBottom = ZcaCalendar.convertToDate(aGregorianCalendar);
        
        Date deadline = aG02TaxFilingCase.getDeadline();
        return (!deadline.after(thresholdTop)) && (deadline.after(thresholdBottom));
    }
    
    private Collection<CheckBox> constructTaxFilingPeriodCheckBoxList(TaxFilingType aTaxFilingType){
        switch (aTaxFilingType){
            case PAYROLL_TAX:
                taxFilingPeriodCheckBoxStorage.put(TaxFilingPeriod.QUARTERLY, new CheckBox(TaxFilingPeriod.QUARTERLY.value()));
                taxFilingPeriodCheckBoxStorage.put(TaxFilingPeriod.MONTHLY, new CheckBox(TaxFilingPeriod.MONTHLY.value()));
                taxFilingPeriodCheckBoxStorage.put(TaxFilingPeriod.MONTHLY_NY, new CheckBox(TaxFilingPeriod.MONTHLY_NY.value()));
                taxFilingPeriodCheckBoxStorage.put(TaxFilingPeriod.SEMI_MONTHLY, new CheckBox(TaxFilingPeriod.SEMI_MONTHLY.value()));
                break;
            case SALES_TAX:
                taxFilingPeriodCheckBoxStorage.put(TaxFilingPeriod.QUARTERLY, new CheckBox(TaxFilingPeriod.QUARTERLY.value()));
                taxFilingPeriodCheckBoxStorage.put(TaxFilingPeriod.MONTHLY, new CheckBox(TaxFilingPeriod.MONTHLY.value()));
                taxFilingPeriodCheckBoxStorage.put(TaxFilingPeriod.YEARLY, new CheckBox(TaxFilingPeriod.YEARLY.value()));
                break;
            case TAX_RETURN:
                taxFilingPeriodCheckBoxStorage.put(TaxFilingPeriod.YEARLY, new CheckBox(TaxFilingPeriod.YEARLY.value()));
                taxFilingPeriodCheckBoxStorage.put(TaxFilingPeriod.FISCAL, new CheckBox(TaxFilingPeriod.FISCAL.value()));
                break;
        }
        for (CheckBox aCheckBox : taxFilingPeriodCheckBoxStorage.values()){
            //aCheckBox.setPrefWidth(100);
            FlowPane.setMargin(aCheckBox, new Insets(2,5,2,5));
        }
        return taxFilingPeriodCheckBoxStorage.values();
    }

    private void displayTaxFilingRecordDeletionDialog() {
        List<PeonyFaceEventListener> peonyFaceEventListeners = new ArrayList<>();
        peonyFaceEventListeners.add(this);
        Lookup.getDefault().lookup(PeonyKernelService.class).displayTaxFilingRecordDeletionDialog("Delete Tax Filing Records",
                targetPeonyTaxcorpCase.getTaxcorpCase().getTaxcorpCaseUuid(), peonyFaceEventListeners);
    }

    private void generateTaxcorpTaxFilingRecords() {
        try {
            getCachedThreadPoolExecutorService().submit(createPeonyTaxFilingCaseListTask(targetTaxFilingType, displayGeneratedResult()));
        } catch (Exception ex) {
            //Exceptions.printStackTrace(ex);
        }
    }
    
    private boolean displayGeneratedResult() {
        final String msg ="Are you sure to generate new tax filing records? ";
        return PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, msg, "Confirm") == JOptionPane.YES_OPTION;
    }

    
    protected Task<Void> createPeonyTaxFilingCaseListTask(TaxFilingType aTaxFilingType, boolean displayResult){
        Task<Void> peonyTaxFilingCaseListGeneratorTask = new Task<Void>(){
            @Override
            protected Void call() throws Exception {
                List<String> taxFilingPeriodList = loadTaxFilingPeriods(aTaxFilingType);
                PeonyTaxFilingCaseList peonyTaxFilingCaseList;
                for (String taxFilingPeriod : taxFilingPeriodList){
                    peonyTaxFilingCaseList = requestGeneratePeonyTaxFilingCaseListForTaxcorp(aTaxFilingType, taxFilingPeriod);
                    if ((peonyTaxFilingCaseList != null) && (peonyTaxFilingCaseList.getPeonyTaxFilingCaseList() != null)
                            && (!peonyTaxFilingCaseList.getPeonyTaxFilingCaseList().isEmpty()) && displayResult)
                    {
                        List<PeonyTaxFilingCase> aPeonyTaxFilingCaseList = peonyTaxFilingCaseList.getPeonyTaxFilingCaseList();
                        GardenSorter.sortPeonyTaxFilingCaseListByDeadline(aPeonyTaxFilingCaseList, false);  //sort it from A to Z
                        for (PeonyTaxFilingCase aPeonyTaxFilingCase : aPeonyTaxFilingCaseList){
                            //GardenSorter.sortPeonyTaxFilingCaseListByDeadline(finalTypePeriodCaseList, true);
                            if (taxcorpTaxFilingTableView != null){
                                Platform.runLater(new Runnable() {
                                    @Override public void run() {
                                        taxcorpTaxFilingTableView.getItems().add(0, aPeonyTaxFilingCase);
                                        taxcorpTaxFilingTableView.refresh();
                                    }
                                });
                            }
                        }//for
                    }
                }//for
                if (displayResult){
                    updateMessage("Successully generated tax filing records which were stored in the database.");
                }
                return null;
            }

            @Override
            protected void succeeded() {
                String msg = getMessage();
                if (ZcaValidator.isNotNullEmpty(msg)){
                    PeonyFaceUtils.displayInformationMessageDialog(msg);
                }
            }
        };
        return peonyTaxFilingCaseListGeneratorTask;
    }
    
    private PeonyTaxFilingCaseList requestGeneratePeonyTaxFilingCaseListForTaxcorp(TaxFilingType aTaxFilingType, String taxFilingPeriod) throws Exception{
        return Lookup.getDefault().lookup(PeonyTaxcorpService.class).getPeonyTaxcorpRestClient()
                                .findEntity_XML(PeonyTaxFilingCaseList.class, GardenRestParams.Taxcorp.generatePeonyTaxFilingCaseListForTaxcorpRestParams(
                                        targetPeonyTaxcorpCase.getTaxcorpCase().getTaxcorpCaseUuid(), aTaxFilingType.value(), taxFilingPeriod));
    }
    
    private List<String> loadTaxFilingPeriods(TaxFilingType aTaxFilingType){
        switch (aTaxFilingType){
            case PAYROLL_TAX:
                return loadPayrollTaxFilingPeriods();
            case SALES_TAX:
                return loadSalesTaxFilingPeriods();
            case TAX_RETURN:
                return loadTaxReturnFilingPeriods();
            default:
                return new ArrayList<>();
        }
    }
    
    private List<TaxFilingPeriod> getSelectedTaxFilingTypesFromStorage(){
        List<TaxFilingPeriod> result = new ArrayList<>();
        Set<TaxFilingPeriod> keys = taxFilingPeriodCheckBoxStorage.keySet();
        Iterator<TaxFilingPeriod> itr = keys.iterator();
        TaxFilingPeriod key;
        CheckBox checkBox;
        while (itr.hasNext()){
            key = itr.next();
            checkBox = taxFilingPeriodCheckBoxStorage.get(key);
            if (checkBox.isSelected()){
                result.add(key);
            }
        }
        return result;
    }
    
    private boolean isTaxFilingPeriodSelected(TaxFilingPeriod aTaxFilingPeriod){
        CheckBox aCheckBox = taxFilingPeriodCheckBoxStorage.get(aTaxFilingPeriod);
        if (aCheckBox == null){
            return false;
        }
        return aCheckBox.isSelected();
    }
    
    private List<String> loadPayrollTaxFilingPeriods() {
        List<String> result = new ArrayList<>();
        if (isTaxFilingPeriodSelected(TaxFilingPeriod.QUARTERLY)){
            result.add(TaxFilingPeriod.QUARTERLY.value());
        }
        if (isTaxFilingPeriodSelected(TaxFilingPeriod.MONTHLY)){
            result.add(TaxFilingPeriod.MONTHLY.value());
        }
        if (isTaxFilingPeriodSelected(TaxFilingPeriod.MONTHLY_NY)){
            result.add(TaxFilingPeriod.MONTHLY_NY.value());
        }
        if (isTaxFilingPeriodSelected(TaxFilingPeriod.SEMI_MONTHLY)){
            result.add(TaxFilingPeriod.SEMI_MONTHLY.value());
        }
        return result;
    }

    private List<String> loadSalesTaxFilingPeriods() {
        List<String> result = new ArrayList<>();
        if (isTaxFilingPeriodSelected(TaxFilingPeriod.QUARTERLY)){
            result.add(TaxFilingPeriod.QUARTERLY.value());
        }
        if (isTaxFilingPeriodSelected(TaxFilingPeriod.MONTHLY)){
            result.add(TaxFilingPeriod.MONTHLY.value());
        }
        if (isTaxFilingPeriodSelected(TaxFilingPeriod.YEARLY)){
            result.add(TaxFilingPeriod.YEARLY.value());
        }
        return result;
    }

    private List<String> loadTaxReturnFilingPeriods() {
        List<String> result = new ArrayList<>();
        if (isTaxFilingPeriodSelected(TaxFilingPeriod.YEARLY)){
            result.add(TaxFilingPeriod.YEARLY.value());
        }
        if (isTaxFilingPeriodSelected(TaxFilingPeriod.FISCAL)){
            result.add(TaxFilingPeriod.FISCAL.value());
        }
        return result;
    }
}
