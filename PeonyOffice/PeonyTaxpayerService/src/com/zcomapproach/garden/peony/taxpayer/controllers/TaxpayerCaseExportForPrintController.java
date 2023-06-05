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

package com.zcomapproach.garden.peony.taxpayer.controllers;

import com.jfoenix.controls.JFXButton;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.commons.apache.poi.ZcaWordDocument;
import com.zcomapproach.commons.apache.poi.ZcaWordParagraph;
import com.zcomapproach.commons.apache.poi.ZcaWordTableCellContent;
import com.zcomapproach.garden.peony.exceptions.PeonyValidationException;
import com.zcomapproach.garden.peony.utils.PeonyDataUtils;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.persistence.entity.G02Location;
import com.zcomapproach.garden.persistence.entity.G02PersonalBusinessProperty;
import com.zcomapproach.garden.persistence.entity.G02PersonalProperty;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerInfo;
import com.zcomapproach.garden.persistence.entity.G02TlcLicense;
import com.zcomapproach.garden.persistence.peony.PeonyBillPayment;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.persistence.constant.TaxpayerFederalFilingStatus;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javax.swing.JOptionPane;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.openide.util.Exceptions;

/**
 *
 * @author zhijun98
 */
public class TaxpayerCaseExportForPrintController extends PeonyTaxpayerServiceController{
    @FXML
    private CheckBox basicInformationCheckBox;
    @FXML
    private CheckBox taxpayerProfilesCheckBox;
    @FXML
    private CheckBox depenedentsAndOthersCheckBox;
    @FXML
    private CheckBox billsAndPaymentsCheckBox;
    @FXML
    private ComboBox<String> billPaymentComboBox;
    @FXML
    private JFXButton invoiceButton;
    
    @FXML
    private JFXButton printButton;
    @FXML
    private JFXButton cancelButton;

    private final PeonyTaxpayerCase targetTaxpayerCase;
    
    public TaxpayerCaseExportForPrintController(PeonyTaxpayerCase targetTaxpayerCase) {
        super(targetTaxpayerCase);
        this.targetTaxpayerCase = targetTaxpayerCase;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> selectionList;
        try {
            selectionList = getPeonyBillPaymentSelecionList();
            PeonyFaceUtils.initializeComboBox(billPaymentComboBox, selectionList, 
                    selectionList.get(0), null, "Select which bill you want to print...", false, this);
            invoiceButton.setDisable(false);
        } catch (PeonyValidationException ex) {
            //Exceptions.printStackTrace(ex);
            selectionList = new ArrayList<>();
            selectionList.add(ex.getMessage());
            PeonyFaceUtils.initializeComboBox(billPaymentComboBox, selectionList, 
                    selectionList.get(0), null, "Select which bill you want to print...", false, this);
            invoiceButton.setDisable(true);
        }
        
        invoiceButton.setOnAction((ActionEvent event) -> {
            if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Print Invoice: " + billPaymentComboBox.getValue() + "?") == JOptionPane.YES_OPTION){
                try {
                    printInvoice(getPeonyBillPaymentFromSelectionMap(billPaymentComboBox.getValue()), 
                                                                     targetTaxpayerCase.getTaxpayerCaseTitle(true));
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Tech Error. " + ex.getMessage());
                }
                broadcastPeonyFaceEventHappened(new CloseDialogRequest());
            }
        });
        
        printButton.setOnAction((ActionEvent event) -> {
            if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Confirm: this print-out is NOT for customers but only for internal usage. ") == JOptionPane.YES_OPTION){
                try {
                    ZcaWordDocument doc = constructGardenWordDocument();
                    boolean selected = false;
                    String customerName = targetTaxpayerCase.getCustomer().getPeonyUserFullName();
                    //String ssn = targetTaxpayerCase.getCustomer().getSsn();
                    G02TaxpayerInfo aG02TaxpayerInfo = targetTaxpayerCase.retrievePrimaryTaxpayerInfo();
                    if (("New Customer".equalsIgnoreCase(customerName) || ((customerName != null)&&(customerName.contains("N/A"))))){
                        if (ZcaValidator.isNotNullEmpty(aG02TaxpayerInfo.getFirstName()) && ZcaValidator.isNotNullEmpty(aG02TaxpayerInfo.getLastName())){
                            customerName = aG02TaxpayerInfo.getLastName() + ", " + aG02TaxpayerInfo.getFirstName();
                        }else if (ZcaValidator.isNotNullEmpty(aG02TaxpayerInfo.getFirstName())){
                            customerName = aG02TaxpayerInfo.getFirstName();
                        }else if (ZcaValidator.isNotNullEmpty(aG02TaxpayerInfo.getLastName())){
                            customerName = aG02TaxpayerInfo.getLastName();
                            customerName = "New Customer";                        }else{

                        }
                    }
                    String ssn;
                    if (aG02TaxpayerInfo == null){
                        ssn = targetTaxpayerCase.getCustomer().getSsn();
                    }else{
                        ssn = aG02TaxpayerInfo.getSsn();
                        if (ZcaValidator.isNullEmpty(ssn)){
                            ssn = targetTaxpayerCase.getCustomer().getSsn();
                        }
                    }
                    
                    //ssn = PeonyTaxpayerCase.hideSSN(ssn);
                    
                    constructTitle(doc, customerName + ": " + ssn);
                    if (basicInformationCheckBox.isSelected()){
                        constructBasicInformationPart(doc);
                        selected = true;
                    }
                    if (taxpayerProfilesCheckBox.isSelected()){
                        constructTaxpayerProfilePart(doc);
                        selected = true;
                    }
                    if (depenedentsAndOthersCheckBox.isSelected()){
                        constructDepenedentsAndOthersPart(doc);
                        selected = true;
                    }
                    if (billsAndPaymentsCheckBox.isSelected()){
                        PeonyBillPayment aPeonyBillPayment = getPeonyBillPaymentFromSelectionMap(billPaymentComboBox.getValue());
                        if (aPeonyBillPayment != null){
                            constructBillsAndPaymentsPart(doc, aPeonyBillPayment);
                        }
                        selected = true;
                    }
                    if (selected){
                        doc.writeToPhysicalFile();
                        PeonyFaceUtils.openFile(doc.getWordFilePath().toAbsolutePath().toFile());
                        broadcastPeonyFaceEventHappened(new CloseDialogRequest());
                    }else{
                        PeonyFaceUtils.displayErrorMessageDialog("Please select at least one part.");
                    }
                } catch (IOException ex) {
                    //Logger.getLogger(ZcaWordDocument.class.getName()).log(Level.SEVERE, null, ex);
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Tech Error. " + ex.getMessage());
                    broadcastPeonyFaceEventHappened(new CloseDialogRequest());
                }
            }
        });
        cancelButton.setOnAction((ActionEvent event) -> {
            broadcastPeonyFaceEventHappened(new CloseDialogRequest());
        });
    }

    private void constructBasicInformationPart(ZcaWordDocument doc) {
        doc.addParagraphText(new ZcaWordParagraph("Basic Information", 12, false, true, 1, null, null));
        constructBasicInformationTable(doc);
        
    }

    private void constructTaxpayerProfilePart(ZcaWordDocument doc) {
////        doc.addParagraphText(new ZcaWordParagraph("Primary Address", 12, false, true, 1, null, null));
////        constructPrimaryAddressTable(doc);
        doc.addParagraphText(new ZcaWordParagraph("Primary Taxpayer", 12, false, true, 1, null, null));
        constructMainTaxpayerTable(doc, targetTaxpayerCase.retrievePrimaryTaxpayerInfo());
        
        if ((!TaxpayerFederalFilingStatus.Single.value().equalsIgnoreCase(targetTaxpayerCase.getTaxpayerCase().getFederalFilingStatus()))
                && (!TaxpayerFederalFilingStatus.HeadOfHousehold.value().equalsIgnoreCase(targetTaxpayerCase.getTaxpayerCase().getFederalFilingStatus())))
        {
            doc.addParagraphText(new ZcaWordParagraph("Spouse Profile", 12, false, true, 1, null, null));
            constructMainTaxpayerTable(doc, targetTaxpayerCase.retrieveSpouseTaxpayerInfo());
        }
    }

    private void constructDepenedentsAndOthersPart(ZcaWordDocument doc) {
        if (!TaxpayerFederalFilingStatus.Single.value().equalsIgnoreCase(targetTaxpayerCase.getTaxpayerCase().getFederalFilingStatus())){
            doc.addParagraphText(new ZcaWordParagraph("Dependents", 12, false, true, 1, null, null));
            constructDependentProfileTables(doc);
        }
        doc.addParagraphText(new ZcaWordParagraph("Schedule C: Personal Business Property", 12, false, true, 1, null, null));
        constructScheduleCTables(doc);
        doc.addParagraphText(new ZcaWordParagraph("Schedule E: Personal Property", 12, false, true, 1, null, null));
        constructScheduleETables(doc);
        doc.addParagraphText(new ZcaWordParagraph("TLC License", 12, false, true, 1, null, null));
        constructTlcLicenseTables(doc);
        
    }

    private void constructBasicInformationTable(ZcaWordDocument doc) {
        
        List<List<ZcaWordTableCellContent>> tableContent = new ArrayList<>();
        //ROW-0
        List<ZcaWordTableCellContent> rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Taxpayer Case Status", 8, false, true, "20%"));
        if (targetTaxpayerCase.getLatestTaxpayerCaseStatus() == null){
            rowContent.add(createCellContent("N/A", 8, true, false, "30%"));
        }else{
            rowContent.add(createCellContent(targetTaxpayerCase.getLatestTaxpayerCaseStatus().getLogMessage(), 8, true, false, "30%"));
        }
        rowContent.add(createCellContent("Tax Return Deadline", 8, false, true, "20%"));
        rowContent.add(createCellContent(ZcaCalendar.convertToMMddyyyy(targetTaxpayerCase.getTaxpayerCase().getDeadline(), "-"), 8, true, false, "30%"));
        tableContent.add(rowContent);
        
        //ROW-1
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Federal Filing Status", 8, false, true, "20%"));
        rowContent.add(createCellContent(targetTaxpayerCase.getTaxpayerCase().getFederalFilingStatus(), 8, true, false, "30%"));
        rowContent.add(createCellContent("Extended Deadline", 8, false, true, "20%"));
        if (targetTaxpayerCase.getTaxpayerCase().getExtension() == null){
            rowContent.add(createCellContent("N/A", 8, true, false, "30%"));
        }else{
            rowContent.add(createCellContent(ZcaCalendar.convertToMMddyyyy(targetTaxpayerCase.getTaxpayerCase().getExtension(), "-"), 8, true, false, "30%"));
        }
        tableContent.add(rowContent);
        
        //ROW-2
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Bank Routing Number", 8, false, true, "20%"));
        rowContent.add(createCellContent(targetTaxpayerCase.getTaxpayerCase().getBankRoutingNumber(), 8, true, false, "30%"));
        rowContent.add(createCellContent("Bank Account Number", 8, false, true, "20%"));
        rowContent.add(createCellContent(targetTaxpayerCase.getTaxpayerCase().getBankAccountNumber(), 8, true, false, "30%"));
        tableContent.add(rowContent);
        
        //ROW-3
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Bank Note", 8, false, true, "20%"));
        rowContent.add(createCellContent(targetTaxpayerCase.getTaxpayerCase().getBankNote(), 8, true, false, "30%"));
        rowContent.add(createCellContent("Contact", 8, false, true, "20%"));
        rowContent.add(createCellContent(targetTaxpayerCase.getTaxpayerCase().getContact(), 8, true, false, "30%"));
        tableContent.add(rowContent);
        
        //ROW-4
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Tags", 8, false, true, "20%"));
        targetTaxpayerCase.setDocumentTagList(sortDocumentTagList(targetTaxpayerCase.getDocumentTagList()));   //sort tags in printing
        rowContent.add(createCellContent(targetTaxpayerCase.getTagReport(), 8, true, false, "30%"));
        rowContent.add(createCellContent("", 8, false, true, "20%"));
        rowContent.add(createCellContent("", 8, true, false, "30%"));
        tableContent.add(rowContent);
        
////        //ROW-5
////        rowContent = new ArrayList<>();
////        //CELLS
////        rowContent.add(createCellContent("Residency/Non-Residency", 8, false, true, "20%"));
////        rowContent.add(createCellContent(targetTaxpayerCase.getTaxpayerCase().getMemo(), 8, true, false, "30%"));
////        rowContent.add(createCellContent("", 8, false, true, "20%"));
////        rowContent.add(createCellContent("", 8, true, false, "30%"));
////        tableContent.add(rowContent);
        
        //ROW-5
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Primary Address", 8, false, true, "20%"));
        rowContent.add(createCellContent(PeonyDataUtils.generateDataTitle(targetTaxpayerCase.getPrimaryLocation()), 8, true, false, "30%"));
        rowContent.add(createCellContent("", 8, false, true, "20%"));
        rowContent.add(createCellContent("", 8, true, false, "30%"));
        tableContent.add(rowContent);
        
        XWPFTable table = doc.createTable(tableContent, 0);
        ZcaWordDocument.mergeCellHorizontally(table, 4, 1, 3);
        ZcaWordDocument.mergeCellHorizontally(table, 5, 1, 3);
////        ZcaWordDocument.mergeCellHorizontally(table, 6, 1, 3);
    }

    /**
     * @deprecated - moved into constructBasicInformationTable
     * @param doc 
     */
    private void constructPrimaryAddressTable(ZcaWordDocument doc) {
        G02Location location = targetTaxpayerCase.getPrimaryLocation();
        if (location == null){
            doc.addParagraphText(new ZcaWordParagraph("N/A", 10, false, false, 1, null, null));
        }else{
            List<List<ZcaWordTableCellContent>> tableContent = new ArrayList<>();
            //ROW
            List<ZcaWordTableCellContent> rowContent = new ArrayList<>();
            //CELLS
            rowContent.add(createCellContent("Address", 8, false, true, "20%"));
            rowContent.add(createCellContent(PeonyDataUtils.generateDataTitle(location), 8, true, false, "75%"));
            tableContent.add(rowContent);

            //ROW
            rowContent = new ArrayList<>();
            //CELLS
            rowContent.add(createCellContent("Memo", 8, false, true, "20%"));
            rowContent.add(createCellContent(location.getShortMemo(), 8, true, false, "75%"));
            tableContent.add(rowContent);

            doc.createTable(tableContent, 0);
        }
    }
    
    private void constructMainTaxpayerTable(ZcaWordDocument doc, G02TaxpayerInfo aG02TaxpayerInfo) {
        if (aG02TaxpayerInfo == null){
            doc.addParagraphText(new ZcaWordParagraph("N/A", 10, false, false, 1, null, null));
            return;
        }
        List<List<ZcaWordTableCellContent>> tableContent = new ArrayList<>();
        //ROW
        List<ZcaWordTableCellContent> rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Tax ID/SSN", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxpayerInfo.getSsn(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Relationship", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxpayerInfo.getRelationships(), 8, true, false, "15%"));
        rowContent.add(createCellContent("First Name", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxpayerInfo.getFirstName(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Last Name", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxpayerInfo.getLastName(), 8, true, false, "15%"));
        tableContent.add(rowContent);
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Gender", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxpayerInfo.getGender(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Birthday", 8, false, true, "10%"));
        if (aG02TaxpayerInfo.getBirthday() == null){
            rowContent.add(createCellContent("", 8, true, false, "15%"));
        }else{
            rowContent.add(createCellContent(ZcaCalendar.convertToMMddyy(aG02TaxpayerInfo.getBirthday(), "-"), 8, true, false, "15%"));
        }
        rowContent.add(createCellContent("Occupation", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxpayerInfo.getOccupation(), 8, true, false, "15%"));
        rowContent.add(createCellContent("", 8, false, true, "10%"));
        rowContent.add(createCellContent("", 8, true, false, "15%"));
        tableContent.add(rowContent);
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Contact", 8, false, true, "10%"));
        rowContent.add(createCellContent(constructContactInfo(aG02TaxpayerInfo), 8, true, false, "15%"));
        rowContent.add(createCellContent("", 8, false, true, "10%"));
        rowContent.add(createCellContent("", 8, true, false, "15%"));
        rowContent.add(createCellContent("", 8, false, true, "10%"));
        rowContent.add(createCellContent("", 8, true, false, "15%"));
        rowContent.add(createCellContent("", 8, false, true, "10%"));
        rowContent.add(createCellContent("", 8, true, false, "15%"));
        tableContent.add(rowContent);
        ZcaWordDocument.mergeCellHorizontally(doc.createTable(tableContent, 0), 2, 1, 7);
    }
    
    private void constructDependentProfileTables(ZcaWordDocument doc) {
        List<G02TaxpayerInfo> aG02TaxpayerInfoList = targetTaxpayerCase.retrieveDependentTaxpayerInfoList();
        for (G02TaxpayerInfo aG02TaxpayerInfo : aG02TaxpayerInfoList){
            constructDependentProfileTable(doc, aG02TaxpayerInfo);
        }
    }

    private void constructDependentProfileTable(ZcaWordDocument doc, G02TaxpayerInfo aG02TaxpayerInfo) {
        if (aG02TaxpayerInfo == null){
            doc.addParagraphText(new ZcaWordParagraph("N/A", 10, false, false, 1, null, null));
            return;
        }
        List<List<ZcaWordTableCellContent>> tableContent = new ArrayList<>();
        //ROW
        List<ZcaWordTableCellContent> rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Tax ID/SSN", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxpayerInfo.getSsn(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Relationship", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxpayerInfo.getRelationships(), 8, true, false, "15%"));
        rowContent.add(createCellContent("First Name", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxpayerInfo.getFirstName(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Last Name", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxpayerInfo.getLastName(), 8, true, false, "15%"));
        tableContent.add(rowContent);
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Gender", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxpayerInfo.getGender(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Birthday", 8, false, true, "10%"));
        if (aG02TaxpayerInfo.getBirthday() == null){
            rowContent.add(createCellContent("", 8, true, false, "15%"));
        }else{
            rowContent.add(createCellContent(ZcaCalendar.convertToMMddyy(aG02TaxpayerInfo.getBirthday(), "-"), 8, true, false, "15%"));
        }
        rowContent.add(createCellContent("Living Together", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxpayerInfo.getLengthOfLivingTogether(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Education Cost", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxpayerInfo.getEducationCost(), 8, true, false, "15%"));
        tableContent.add(rowContent);
        doc.createTable(tableContent, 0);
    }

    private void constructScheduleCTables(ZcaWordDocument doc) {
        List<G02PersonalBusinessProperty> aG02PersonalBusinessPropertyList = targetTaxpayerCase.getPersonalBusinessPropertyList();
        if ((aG02PersonalBusinessPropertyList == null) || (aG02PersonalBusinessPropertyList.isEmpty())){
            doc.addParagraphText(new ZcaWordParagraph("N/A", 10, false, false, 1, null, null));
            return;
        }
        for (G02PersonalBusinessProperty aG02PersonalBusinessProperty : aG02PersonalBusinessPropertyList){
            constructScheduleCTable(doc, aG02PersonalBusinessProperty);
        }
    }

    private void constructScheduleCTable(ZcaWordDocument doc, G02PersonalBusinessProperty aG02PersonalBusinessProperty) {
        if (aG02PersonalBusinessProperty == null){
            doc.addParagraphText(new ZcaWordParagraph("N/A", 10, false, false, 1, null, null));
            return;
        }
        List<List<ZcaWordTableCellContent>> tableContent = new ArrayList<>();
        //ROW
        List<ZcaWordTableCellContent> rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Business Name", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getBusinessPropertyName(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Business Owner", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getBusinessOwnership(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Business EIN", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getBusinessEin(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Business Address", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getBusinessAddress(), 8, true, false, "15%"));
        tableContent.add(rowContent);
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Business Description", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getBusinessDescription(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Gross Receipts or Sales", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getGrossReceiptsSales(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Cost of Goods Solde", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getCostOfGoodsSold(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Other Income", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getOtherIncome(), 8, true, false, "15%"));
        tableContent.add(rowContent);
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Advertising", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getExpenseAdvertising(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Office Expense", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getExpenseOffice(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Car & Truck", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getExpenseCarAndTruck(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Commissions", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getExpenseCommissions(), 8, true, false, "15%"));
        tableContent.add(rowContent);
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Rent & Lease", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getExpenseRentLease(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Insurance", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getExpenseInsurance(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Travel & Meals", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getExpenseTravelMeals(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Telephone", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getExpenseTelephone(), 8, true, false, "15%"));
        tableContent.add(rowContent);
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Cable & Internet", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getExpenseCableInternet(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Supplies", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getExpenseSupplies(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Repairs", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getExpenseTravelMeals(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Prof. Services", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getExpenseTelephone(), 8, true, false, "15%"));
        tableContent.add(rowContent);
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Utilies", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getExpenseUtilities(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Contract Labor", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getExpenseContractLabor(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Others", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getExpenseOthers(), 8, true, false, "15%"));
        rowContent.add(createCellContent("", 8, false, true, "10%"));
        rowContent.add(createCellContent("", 8, true, false, "15%"));
        tableContent.add(rowContent);
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Memo", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalBusinessProperty.getMemo(), 8, true, false, "15%"));
        rowContent.add(createCellContent("", 8, false, true, "10%"));
        rowContent.add(createCellContent("", 8, true, false, "15%"));
        rowContent.add(createCellContent("", 8, false, true, "10%"));
        rowContent.add(createCellContent("", 8, true, false, "15%"));
        rowContent.add(createCellContent("", 8, false, true, "10%"));
        rowContent.add(createCellContent("", 8, true, false, "15%"));
        tableContent.add(rowContent);
        
        ZcaWordDocument.mergeCellHorizontally(doc.createTable(tableContent, 0), 6, 1, 7);
    }

    private void constructScheduleETables(ZcaWordDocument doc) {
        List<G02PersonalProperty> aG02PersonalPropertyList = targetTaxpayerCase.getPersonalPropertyList();
        if ((aG02PersonalPropertyList == null) || (aG02PersonalPropertyList.isEmpty())){
            doc.addParagraphText(new ZcaWordParagraph("N/A", 10, false, false, 1, null, null));
            return;
        }
        for (G02PersonalProperty aG02PersonalProperty : aG02PersonalPropertyList){
            constructScheduleETable(doc, aG02PersonalProperty);
        }
    }

    private void constructScheduleETable(ZcaWordDocument doc, G02PersonalProperty aG02PersonalProperty) {
        if (aG02PersonalProperty == null){
            doc.addParagraphText(new ZcaWordParagraph("N/A", 10, false, false, 1, null, null));
            return;
        }
        List<List<ZcaWordTableCellContent>> tableContent = new ArrayList<>();
        //ROW
        List<ZcaWordTableCellContent> rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Property Type", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getPropertyType(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Property Address", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getPropertyAddress(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Purchase Price", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getPurchasePrice(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Date of Service", 8, false, true, "10%"));
        rowContent.add(createCellContent(ZcaCalendar.convertToMMddyy(aG02PersonalProperty.getDateOnService(), "-"), 8, true, false, "15%"));
        tableContent.add(rowContent);
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("% of Ownership", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getPercentageOfOwnership(), 8, true, false, "15%"));
        rowContent.add(createCellContent("% of Rental Use", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getPercentageOfRentalUse(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Income Rents Received", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getIncomeRentsReceieved(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Date of Improvement", 8, false, true, "10%"));
        rowContent.add(createCellContent(ZcaCalendar.convertToMMddyy(aG02PersonalProperty.getDateOnImprovement(), "-"), 8, true, false, "15%"));
        tableContent.add(rowContent);
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Improvement Cost", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getImprovementCost(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Advertising", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getExpenseAdvertising(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Auto/Travel", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getExpenseAutoAndTravel(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Cleaning", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getExpenseCleaning(), 8, true, false, "15%"));
        tableContent.add(rowContent);
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Commission", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getExpenseCommissions(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Depreciation", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getExpenseDepreciation(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Insurance", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getExpenseInsurance(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Management Fee", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getExpenseMgtFee(), 8, true, false, "15%"));
        tableContent.add(rowContent);
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Real Estate Taxes", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getExpenseReTaxes(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Morgage Interest", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getExpenseMorgageInterest(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Supplies", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getExpenseSupplies(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Repairs", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getExpenseRepairs(), 8, true, false, "15%"));
        tableContent.add(rowContent);
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Prof. Services", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getExpenseProfServices(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Utilities", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getExpenseUtilities(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Water Sewer", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getExpenseWaterSewer(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Other Expenses", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getExpenseOthers(), 8, true, false, "15%"));
        tableContent.add(rowContent);
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Memo", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02PersonalProperty.getMemo(), 8, true, false, "15%"));
        rowContent.add(createCellContent("", 8, false, true, "10%"));
        rowContent.add(createCellContent("", 8, true, false, "15%"));
        rowContent.add(createCellContent("", 8, false, true, "10%"));
        rowContent.add(createCellContent("", 8, true, false, "15%"));
        rowContent.add(createCellContent("", 8, false, true, "10%"));
        rowContent.add(createCellContent("", 8, true, false, "15%"));
        tableContent.add(rowContent);
        
        ZcaWordDocument.mergeCellHorizontally(doc.createTable(tableContent, 0), 6, 1, 7);
    }

    private void constructTlcLicenseTables(ZcaWordDocument doc) {
        List<G02TlcLicense> aG02TlcLicenseList = targetTaxpayerCase.getTlcLicenseList();
        if ((aG02TlcLicenseList == null) || (aG02TlcLicenseList.isEmpty())){
            doc.addParagraphText(new ZcaWordParagraph("N/A", 10, false, false, 1, null, null));
            return;
        }
        for (G02TlcLicense aG02TlcLicense : aG02TlcLicenseList){
            constructTlcLicenseTable(doc, aG02TlcLicense);
        }
    }

    private void constructTlcLicenseTable(ZcaWordDocument doc, G02TlcLicense aG02TlcLicense) {
        if (aG02TlcLicense == null){
            doc.addParagraphText(new ZcaWordParagraph("N/A", 10, false, false, 1, null, null));
            return;
        }
        List<List<ZcaWordTableCellContent>> tableContent = new ArrayList<>();
        //ROW
        List<ZcaWordTableCellContent> rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("TLC License", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getTlcLicense(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Vehicle Make", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getVehicleType(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Vehicle Model", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getVehicleModel(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Date in Service", 8, false, true, "10%"));
        rowContent.add(createCellContent(ZcaCalendar.convertToMMddyy(aG02TlcLicense.getDateInService(), "-"), 8, true, false, "15%"));
        tableContent.add(rowContent);
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("# of Seats", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getNumberOfSeats(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Over 6000 lbs", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getOver600lbs(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Total Miles", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getTotalMiles(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Business Miles", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getBusinessMiles(), 8, true, false, "15%"));
        tableContent.add(rowContent);
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Milage Rate", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getMileageRate(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Garage Rent", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getGarageRent(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Tires", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getTires(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Tolls", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getTolls(), 8, true, false, "15%"));
        tableContent.add(rowContent);
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Service Fee", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getServiceFee(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Meals", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getMeals(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Uniform", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getUniform(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Car Wash", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getCarWash(), 8, true, false, "15%"));
        tableContent.add(rowContent);
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Radio Repair", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getRadioRepair(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Accessories", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getAccessories(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Telephone", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getTelephone(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Parking", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getParking(), 8, true, false, "15%"));
        tableContent.add(rowContent);
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Insurance", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getInsurance(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Depreciation", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getDepreciation(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Lease Payment", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getLeasePayment(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Maintenance", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getMaintenance(), 8, true, false, "15%"));
        tableContent.add(rowContent);
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Repairs", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getRepairs(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Gas", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getGas(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Oil", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getOil(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Registration Fee", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getRegistrationFee(), 8, true, false, "15%"));
        tableContent.add(rowContent);
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Memo", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TlcLicense.getMemo(), 8, true, false, "15%"));
        rowContent.add(createCellContent("", 8, false, true, "10%"));
        rowContent.add(createCellContent("", 8, true, false, "15%"));
        rowContent.add(createCellContent("", 8, false, true, "10%"));
        rowContent.add(createCellContent("", 8, true, false, "15%"));
        rowContent.add(createCellContent("", 8, false, true, "10%"));
        rowContent.add(createCellContent("", 8, true, false, "15%"));
        tableContent.add(rowContent);
        
        ZcaWordDocument.mergeCellHorizontally(doc.createTable(tableContent, 0), 7, 1, 7);
    }
}
