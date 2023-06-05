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

import com.jfoenix.controls.JFXButton;
import com.zcomapproach.commons.apache.poi.ZcaWordDocument;
import com.zcomapproach.commons.apache.poi.ZcaWordParagraph;
import com.zcomapproach.commons.apache.poi.ZcaWordTableCellContent;
import com.zcomapproach.garden.peony.exceptions.PeonyValidationException;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.persistence.entity.G02BusinessContactor;
import com.zcomapproach.garden.persistence.entity.G02TaxcorpCase;
import com.zcomapproach.garden.persistence.peony.PeonyBillPayment;
import com.zcomapproach.garden.persistence.peony.PeonyTaxcorpCase;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.garden.peony.PeonyLauncher;
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
import org.openide.util.Exceptions;

/**
 *
 * @author zhijun98
 */
public class TaxcorpCaseExportForPrintController extends PeonyTaxcorpServiceController{
    @FXML
    private CheckBox basicInformationCheckBox;
    @FXML
    private CheckBox taxcorpContactorCheckBox;
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

    private final PeonyTaxcorpCase targetTaxcorpCase;
    
    public TaxcorpCaseExportForPrintController(PeonyTaxcorpCase targetTaxcorpCase) {
        super(targetTaxcorpCase);
        this.targetTaxcorpCase = targetTaxcorpCase;
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
                    printInvoice(getPeonyBillPaymentFromSelectionMap(billPaymentComboBox.getValue()), targetTaxcorpCase.getTaxcorpTitle());
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
                    //System.out.println(docPath.toString());
                    ZcaWordDocument doc = constructGardenWordDocument();
                    boolean selected = false;
                    constructTitle(doc, targetTaxcorpCase.getTaxcorpCase().getCorporateName() + ": " + targetTaxcorpCase.getTaxcorpCase().getEinNumber());
                    if (basicInformationCheckBox.isSelected()){
                        constructBasicInformationPart(doc);
                        selected = true;
                    }
                    if (taxcorpContactorCheckBox.isSelected()){
                        constructTaxcorpContactorPart(doc);
                        selected = true;
                    }
                    if (billsAndPaymentsCheckBox.isSelected()){
                        PeonyBillPayment aPeonyBillPayment = getPeonyBillPaymentFromSelectionMap(billPaymentComboBox.getValue());
                        if (aPeonyBillPayment != null){
                            constructBillsAndPaymentsPart(doc, aPeonyBillPayment);
                        }
                        selected = true;
                    }
                    //constructClaimStatement(doc);
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
        
        G02TaxcorpCase aG02TaxcorpCase = targetTaxcorpCase.getTaxcorpCase();
        
        doc.addParagraphText(new ZcaWordParagraph("Basic Information: " + aG02TaxcorpCase.getCorporateName(), 12, false, true, 1, null, null));
        
        List<List<ZcaWordTableCellContent>> tableContent = new ArrayList<>();
        //ROW
        List<ZcaWordTableCellContent> rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Taxcorp EIN", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxcorpCase.getEinNumber(), 8, true, false, "15%"));
        rowContent.add(createCellContent("DOS ", 8, false, true, "10%"));
        if (aG02TaxcorpCase.getDosDate() == null) {
            rowContent.add(createCellContent("N/A", 8, true, false, "15%"));
        }else{
            rowContent.add(createCellContent(ZcaCalendar.convertToMMddyyyy(aG02TaxcorpCase.getDosDate(), "-"), 8, true, false, "15%"));
        }
        rowContent.add(createCellContent("Business Purpose", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxcorpCase.getBusinessPurpose(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Business Type", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxcorpCase.getBusinessType(), 8, true, false, "15%"));
        tableContent.add(rowContent);
        
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Bank Routing Number", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxcorpCase.getBankRoutingNumber(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Bank Account Number", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxcorpCase.getBankAccountNumber(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Corporate Email", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxcorpCase.getCorporateEmail(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Corporate Phone", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxcorpCase.getCorporatePhone(), 8, true, false, "15%"));
        tableContent.add(rowContent);
        
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Corporate Fax", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxcorpCase.getCorporateFax(), 8, true, false, "15%"));
        rowContent.add(createCellContent("Web Presence", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxcorpCase.getCorporateWebPresence(), 8, true, false, "15%"));
        rowContent.add(createCellContent("", 8, false, true, "10%"));
        rowContent.add(createCellContent("", 8, true, false, "15%"));
        rowContent.add(createCellContent("", 8, false, true, "10%"));
        rowContent.add(createCellContent("", 8, true, false, "15%"));
        tableContent.add(rowContent);
        
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Address", 8, false, true, "10%"));
        rowContent.add(createCellContent(aG02TaxcorpCase.getTaxcorpAddress() + " " + aG02TaxcorpCase.getTaxcorpCity() + " " + aG02TaxcorpCase.getTaxcorpStateCounty() 
                + " " + aG02TaxcorpCase.getTaxcorpState() + " " + aG02TaxcorpCase.getTaxcorpZip(), 8, true, false, "15%"));
        rowContent.add(createCellContent("", 8, false, true, "10%"));
        rowContent.add(createCellContent("", 8, true, false, "15%"));
        rowContent.add(createCellContent("", 8, false, true, "10%"));
        rowContent.add(createCellContent("", 8, true, false, "15%"));
        rowContent.add(createCellContent("", 8, false, true, "10%"));
        rowContent.add(createCellContent("", 8, true, false, "15%"));
        tableContent.add(rowContent);
        
        ZcaWordDocument.mergeCellHorizontally(doc.createTable(tableContent, 0), 3, 1, 7);
        
    }

    private void constructTaxcorpContactorPart(ZcaWordDocument doc) {
        doc.addParagraphText(new ZcaWordParagraph("Business Personnel & Contactors", 12, false, true, 1, null, null));
        
        List<List<ZcaWordTableCellContent>> tableContent = new ArrayList<>();
        //ROW
        List<ZcaWordTableCellContent> rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Full Name", 8, false, true, "10%"));
        rowContent.add(createCellContent("SSN", 8, false, true, "10%"));
        rowContent.add(createCellContent("Birthday", 8, false, true, "10%"));
        rowContent.add(createCellContent("Role", 8, false, true, "15%"));
        rowContent.add(createCellContent("Type", 8, false, true, "10%"));
        rowContent.add(createCellContent("Info.", 8, false, true, "15%"));
        rowContent.add(createCellContent("Memo", 8, false, true, "30%"));
        tableContent.add(rowContent);
        
        List<G02BusinessContactor> contactors = targetTaxcorpCase.getBusinessContactorList();
        for (G02BusinessContactor contactor : contactors){
            rowContent = new ArrayList<>();
            //CELLS
            rowContent.add(createCellContent(contactor.getFirstName() + " " + contactor.getLastName(), 8, true, false, "10%"));
            rowContent.add(createCellContent(contactor.getSsn(), 8, true, false, "10%"));
            rowContent.add(createCellContent(ZcaCalendar.convertToMMddyyyy(contactor.getBirthday(), "-"), 8, true, false, "10%"));
            rowContent.add(createCellContent(contactor.getRole(), 8, true, false, "15%"));
            rowContent.add(createCellContent(contactor.getContactType(), 8, true, false, "10%"));
            rowContent.add(createCellContent(contactor.getContactInfo(), 8, true, false, "15%"));
            rowContent.add(createCellContent(contactor.getMemo(), 8, true, false, "30%"));
            tableContent.add(rowContent);
        }
        
        doc.createTable(tableContent, 0);
    }

}
