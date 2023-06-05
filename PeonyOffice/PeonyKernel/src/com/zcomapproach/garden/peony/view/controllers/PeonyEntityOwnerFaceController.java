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
import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.commons.apache.poi.ZcaWordDocument;
import com.zcomapproach.commons.apache.poi.ZcaWordParagraph;
import com.zcomapproach.commons.apache.poi.ZcaWordTableCellContent;
import com.zcomapproach.garden.email.GardenEmailMessage;
import com.zcomapproach.garden.email.data.OfflineMessageStatus;
import com.zcomapproach.garden.peony.exceptions.PeonyImplementationDemanded;
import com.zcomapproach.garden.peony.exceptions.PeonyValidationException;
import com.zcomapproach.garden.peony.kernel.data.PeonySmsContactor;
import com.zcomapproach.garden.peony.kernel.services.PeonyEmailService;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyDataUtils;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.dialogs.DocumentTagDataEntryDialog;
import com.zcomapproach.garden.peony.view.dialogs.PeonySmsDialog;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G02BusinessContactor;
import com.zcomapproach.garden.persistence.entity.G02Log;
import com.zcomapproach.garden.persistence.entity.G02OfflineEmail;
import com.zcomapproach.garden.persistence.peony.PeonyArchivedFile;
import com.zcomapproach.garden.persistence.peony.PeonyBillPayment;
import com.zcomapproach.garden.persistence.peony.PeonyDocumentTag;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmail;
import com.zcomapproach.garden.persistence.peony.PeonyPayment;
import com.zcomapproach.garden.persistence.peony.PeonyTaxFilingCase;
import com.zcomapproach.garden.persistence.peony.PeonyTaxcorpCase;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.garden.persistence.peony.data.PeonyLogName;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaText;
import com.zcomapproach.commons.nio.ZcaNio;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.persistence.peony.PeonyMemo;
import com.zcomapproach.garden.util.GardenSorter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javax.swing.JOptionPane;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public abstract class PeonyEntityOwnerFaceController extends PeonyFaceController{

    /**
     * target entity which owns this controller, e.g targetPeonyTaxpayer owns a 
     * targetPeonyBillPayment. It could be null
     */
    private final Object targetOwner;
    
    private final HashMap<String, PeonyBillPayment> peonyBillPaymentSelectionMap = new HashMap<>();
    
    private PeonyBillAndPaymentController peonyBillAndPaymentController;

    public PeonyEntityOwnerFaceController(Object targetOwner) {
        this.targetOwner = targetOwner;
    }

    protected void intializeBillAndPaymentTab(TabPane taxCaseTabPane, Tab billAndPaymentTab, List<PeonyBillPayment> targetPeonyBillPaymentList, Object targetTaxCaseOwner) {
        peonyBillAndPaymentController = new PeonyBillAndPaymentController(targetPeonyBillPaymentList, targetTaxCaseOwner);
        peonyBillAndPaymentController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
        try {
            Pane aPane = peonyBillAndPaymentController.loadFxml();
            aPane.prefHeightProperty().bind(taxCaseTabPane.heightProperty());
            aPane.prefWidthProperty().bind(taxCaseTabPane.widthProperty());
            billAndPaymentTab.setContent(aPane);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    /**
     * Add previousYearPeonyBillPayments into peonyBillAndPaymentController
     * @param previousYearPeonyBillPayments 
     */
    protected void addPreviousYearBillPayments(List<PeonyBillPayment> previousYearPeonyBillPayments){
        if (previousYearPeonyBillPayments == null){
            return;
        }
        for (PeonyBillPayment aPeonyBillPayment : previousYearPeonyBillPayments){
            peonyBillAndPaymentController.polulateBillPayment(aPeonyBillPayment);
        }
    }

    protected void intializeRelatedCommunicationTab(TabPane taxCaseTabPane, Tab relatedCommunicationTab, 
                                                    List<G02OfflineEmail> targetOfflineEmailList, 
                                                    Object targetTaxCaseOner) 
    {
        PeonyRelatedCommunicationController taxcorpRelatedCommunicationController = new PeonyRelatedCommunicationController(targetOfflineEmailList, targetTaxCaseOner);
        taxcorpRelatedCommunicationController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
        try {
            Pane aPane = taxcorpRelatedCommunicationController.loadFxml();
            aPane.prefHeightProperty().bind(taxCaseTabPane.heightProperty());
            aPane.prefWidthProperty().bind(taxCaseTabPane.widthProperty());
            relatedCommunicationTab.setContent(aPane);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    protected void intializeArchivedDocumentsTab(TabPane taxCaseTabPane, Tab archivedDocumentsTab, 
                                                List<PeonyArchivedFile> targetPeonyArchivedFileList, 
                                                Object targetTaxCaseOner) {
        Path archivedFileRoot = Paths.get(PeonyProperties.getSingleton().getArchivedDocumentsFolder());
        try {
            if (Files.notExists(archivedFileRoot)){
                /**
                 * todo zzj: potentially here it may produce empty folder for 
                 * the brand-new taxcorp case. It may need garbage folder collection 
                 * in the future.
                 */
                ZcaNio.createFolder(archivedFileRoot);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }
        PeonyArchivedFilePaneController peonyArchivedFilePaneController = new PeonyArchivedFilePaneController(archivedFileRoot, targetPeonyArchivedFileList, targetTaxCaseOner);
        peonyArchivedFilePaneController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
        try {
            Pane aPane = peonyArchivedFilePaneController.loadFxml();
            aPane.prefHeightProperty().bind(taxCaseTabPane.heightProperty());
            aPane.prefWidthProperty().bind(taxCaseTabPane.widthProperty());
            archivedDocumentsTab.setContent(aPane);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    protected List<JFXButton> constructCommonTaxCaseFunctionalButtons(){
        List<JFXButton> buttons = new ArrayList<>();
        
        JFXButton btn = new JFXButton("Email");
        btn.getStyleClass().add("peony-primary-button");
        btn.setButtonType(JFXButton.ButtonType.RAISED);
        btn.setTooltip(new Tooltip("Email to taxcorps"));
        btn.setGraphic(PeonyGraphic.getImageView("mail_to_friend.png"));
        btn.setOnAction((ActionEvent e) -> {
            try {
                GardenEmailMessage aGardenEmailMessage = new GardenEmailMessage();
                aGardenEmailMessage.setToList(PeonyDataUtils.retrieveEmailAddressListFromBusinessContactorList(getBusinessContactorListOfTargetEntityCase()));
                Lookup.getDefault().lookup(PeonyEmailService.class).displayComposeEmailTopComponent(aGardenEmailMessage, new PeonyOfflineEmail(), OfflineMessageStatus.SEND);
            } catch (PeonyImplementationDemanded ex) {
                //Exceptions.printStackTrace(ex);
            }
        });
        buttons.add(btn);
        
        btn = new JFXButton("SMS");
        btn.getStyleClass().add("peony-primary-button");
        btn.setButtonType(JFXButton.ButtonType.RAISED);
        btn.setTooltip(new Tooltip("Use mobile SMS to taxcorps"));
        btn.setGraphic(PeonyGraphic.getImageView("page_lightning.png"));
        btn.setOnAction((ActionEvent e) -> {
            smsTargetPeonyTaxcorpCase();
        });
        buttons.add(btn);
        
        btn = new JFXButton("Print");
        btn.getStyleClass().add("peony-primary-button");
        btn.setButtonType(JFXButton.ButtonType.RAISED);
        btn.setTooltip(new Tooltip("Export to Microsoft word file for print"));
        btn.setGraphic(PeonyGraphic.getImageView("printer.png"));
        btn.setOnAction((ActionEvent e) -> {
            displayTaxCaseExportForPrintDialog();
        });
        buttons.add(btn);
        
        return buttons;
    }
    
    private void smsTargetPeonyTaxcorpCase() {
        try {
            List<PeonySmsContactor> aPeonySmsContactorList = PeonyDataUtils.retrievePeonySmsContactorListFromBusinessContactorList(
                    getBusinessContactorListOfTargetEntityCase(), this.getTargetEntityType(), this.getTargetEntityUuid());
            if ((aPeonySmsContactorList == null) || (aPeonySmsContactorList.isEmpty())){
                PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, "No contactor(s) qualified for SMS.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            PeonySmsDialog aPeonySmsDialog = new PeonySmsDialog(null, true);
            aPeonySmsDialog.launchPeonySmsDialog("Send SMS", aPeonySmsContactorList);
        } catch (PeonyImplementationDemanded ex) {
            //Exceptions.printStackTrace(ex);
        }
    }
    
    protected List<String> getPeonyBillPaymentSelecionList() throws PeonyValidationException{
        List<String> result = new ArrayList<>();
        
        List<PeonyBillPayment> aPeonyBillPaymentList = null;
        if (targetOwner instanceof PeonyTaxcorpCase){
            aPeonyBillPaymentList = ((PeonyTaxcorpCase)targetOwner).getPeonyBillPaymentList();
        }else if (targetOwner instanceof PeonyTaxpayerCase){
            aPeonyBillPaymentList = ((PeonyTaxpayerCase)targetOwner).getPeonyBillPaymentList();
        }
        
        if ((aPeonyBillPaymentList == null) || (aPeonyBillPaymentList.isEmpty())){
            throw new PeonyValidationException("No any bill & payemnts");
        }else{
            aPeonyBillPaymentList = GardenSorter.sortPeonyBillPaymentListByDueDate(aPeonyBillPaymentList, true);
            String key;
            for (PeonyBillPayment aPeonyBillPayment : aPeonyBillPaymentList){
                key = aPeonyBillPayment.getBillTitle();
                //key could be duplicated. It is solved by append "Counter" onto the key
                int counter = 1;
                while(peonyBillPaymentSelectionMap.containsKey(key)){
                    key = key + "("+counter+")";    //for example: key -> key (1), which may be duplicated in the map also
                    counter++;
                }
                result.add(key);
                peonyBillPaymentSelectionMap.put(key, aPeonyBillPayment);
            }
        }
        return result;
    }
    
    protected PeonyBillPayment getPeonyBillPaymentFromSelectionMap(String key){
        return peonyBillPaymentSelectionMap.get(key);
    }

    public Object getTargetOwner() {
        return targetOwner;
    }

    protected String getTargetEntityUuid() {
        if (targetOwner == null){
            return null;
        }
        if (targetOwner.getClass().isEnum()){
            return ((GardenEntityType)targetOwner).name();
        }
        if (targetOwner instanceof PeonyTaxpayerCase){
            return ((PeonyTaxpayerCase)targetOwner).getTaxpayerCase().getTaxpayerCaseUuid();
        }else if (targetOwner instanceof PeonyTaxcorpCase){
            return ((PeonyTaxcorpCase)targetOwner).getTaxcorpCase().getTaxcorpCaseUuid();
        }else if (targetOwner instanceof PeonyTaxFilingCase){
            return ((PeonyTaxFilingCase)targetOwner).getTaxFilingCase().getTaxFilingUuid();
        }else{
            return null;
        }
    }
    
    /**
     * Get a list of contactors fpr this entity, e.g. contactors of taxcorp case or taxpayer case
     * @return 
     */
    protected List<G02BusinessContactor> getBusinessContactorListOfTargetEntityCase() throws PeonyImplementationDemanded{
        if (targetOwner == null){
            return new ArrayList<>();
        }
        if (targetOwner.getClass().isEnum()){
            return new ArrayList<>();
        }
        if (targetOwner instanceof PeonyTaxpayerCase){
            return ((PeonyTaxpayerCase)targetOwner).getBusinessContactorList();
        }else if (targetOwner instanceof PeonyTaxcorpCase){
            return ((PeonyTaxcorpCase)targetOwner).getBusinessContactorList();
        }else if (targetOwner instanceof PeonyTaxFilingCase){
            return new ArrayList<>();
        }else{
            return new ArrayList<>();
        }
    }

    protected GardenEntityType getTargetEntityType() {
        if (targetOwner == null){
            return GardenEntityType.UNKNOWN;
        }
        if (targetOwner.getClass().isEnum()){
            return (GardenEntityType)targetOwner;
        }
        if (targetOwner instanceof PeonyTaxpayerCase){
            return GardenEntityType.TAXPAYER_CASE;
        }else if (targetOwner instanceof PeonyTaxcorpCase){
            return GardenEntityType.TAXCORP_CASE;
        }else if (targetOwner instanceof PeonyTaxFilingCase){
            return GardenEntityType.TAX_FILING_CASE;
        }else{
            return GardenEntityType.UNKNOWN;
        }
    }
    
    protected String getEntityDescription(){
        String entityDescription = null;
        if ((targetOwner != null) && (targetOwner.getClass().isEnum())){
            return ((GardenEntityType)targetOwner).value();
        }
        if (targetOwner instanceof PeonyTaxpayerCase){
            PeonyTaxpayerCase aPeonyTaxpayerCase = (PeonyTaxpayerCase)targetOwner;
            if ((ZcaValidator.isNullEmpty(aPeonyTaxpayerCase.retrievePrimaryTaxpayerInfo().getFirstName())) && 
                    ZcaValidator.isNullEmpty(aPeonyTaxpayerCase.retrievePrimaryTaxpayerInfo().getLastName())
                    && ZcaValidator.isNullEmpty(aPeonyTaxpayerCase.retrievePrimaryTaxpayerInfo().getSsn())
                    && (aPeonyTaxpayerCase.getCustomer() != null))
            {
                entityDescription = aPeonyTaxpayerCase.getCustomer().getPeonyUserFullName() 
                        + aPeonyTaxpayerCase.getCustomer().getUser().getSsn();
            }else{
                entityDescription = "Taxpayer Case: " + aPeonyTaxpayerCase.retrievePrimaryTaxpayerInfo().getFirstName()
                        + " " + aPeonyTaxpayerCase.retrievePrimaryTaxpayerInfo().getLastName()
                        + " - SSN " + aPeonyTaxpayerCase.retrievePrimaryTaxpayerInfo().getSsn();
            }
        }else if (targetOwner instanceof PeonyTaxcorpCase){
            entityDescription = ((PeonyTaxcorpCase)targetOwner).getTaxcorpCase().getCorporateName() 
                    + " - EIN " + ((PeonyTaxcorpCase)targetOwner).getTaxcorpCase().getEinNumber();
        }
        return entityDescription;
    }
    
    /**
     * This is a helper method by means of PeonyProperties.getSingleton().createNewG02LogInstance()
     * @param aPeonyLogName
     * @return 
     */
    protected G02Log createNewG02LogInstance(PeonyLogName aPeonyLogName){
        G02Log log = PeonyProperties.getSingleton().createNewG02LogInstance(aPeonyLogName);
        log.setEntityType(getTargetEntityType().name());
        log.setEntityUuid(getTargetEntityUuid());
        log.setEntityDescription(getEntityDescription());
        return log;
    }
    
    /**
     * Construct a standard MS-Word document with its title on the top of the document
     * @return
     * @throws IOException 
     */
    protected ZcaWordDocument constructGardenWordDocument() throws IOException {
        Path docFolderPath = PeonyProperties.getSingleton().getLocalUserTempPath();
        if (!docFolderPath.toFile().exists()){
            ZcaNio.createFolder(docFolderPath);
        }
        ZcaWordDocument doc = new ZcaWordDocument(docFolderPath, ZcaUtils.generateUUIDString() + ".docx", true);
        doc.addParagraphText(new ZcaWordParagraph("", 
                18, false, true, 0, null, ParagraphAlignment.CENTER));
        doc.addParagraphText(new ZcaWordParagraph("Address: ", 
                8, true, false, 0, null, ParagraphAlignment.CENTER));
        doc.addParagraphText(new ZcaWordParagraph("Tel: ; Fax: ", 
                8, true, false, 2, null, ParagraphAlignment.CENTER));
        return doc;
    }

    protected void constructTitle(ZcaWordDocument doc, String title) {
        doc.addParagraphText(new ZcaWordParagraph(title, 14, false, true, 1, null, ParagraphAlignment.LEFT));
    }
    
    /**
     * Helper functon to construct a table cell 
     * @param cellText
     * @param textSize
     * @param textItalic
     * @param textBold
     * @param cellWidth
     * @return 
     */
    protected ZcaWordTableCellContent createCellContent(String cellText, int textSize, boolean textItalic, boolean textBold, String cellWidth){
        ZcaWordTableCellContent cellContent = new ZcaWordTableCellContent();
        cellContent.setCellText(cellText);
        cellContent.setCellWidth(cellWidth);
        cellContent.setCellVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        cellContent.setTextBold(textBold);
        cellContent.setTextItalic(textItalic);
        cellContent.setTextSize(textSize);
        return cellContent;
    }
    
    protected void constructClaimStatement(ZcaWordDocument doc){
        doc.addParagraphText(new ZcaWordParagraph("可选的付费方式 Optional Payment Methods", 
                            8, false, true, 0, null, ParagraphAlignment.LEFT));
        doc.addParagraphText(new ZcaWordParagraph("1. You can CHASE-QUICK-PAY to us. Our CHASE-QUICK-PAY account is: yinlucpa@gmail.com", 
                            8, true, false, 0, null, ParagraphAlignment.LEFT));
        doc.addParagraphText(new ZcaWordParagraph("2. You can write a check with payable to 'YIN LU CPA PC' and email us photo copies with your check's front and back.", 
                            8, true, false, 2, null, ParagraphAlignment.LEFT));
        doc.addParagraphText(new ZcaWordParagraph("Thank you! 谢谢！", 
                            10, false, true, 0, null, ParagraphAlignment.LEFT));
    }

    protected void printInvoice(PeonyBillPayment aPeonyBillPayment, String invoiceForName) throws IOException {
        ZcaWordDocument doc = constructGardenWordDocument();
        //line...
        doc.addParagraphText(new ZcaWordParagraph("INVOICE - For:" + ZcaText.getWhiteSpaces(2) + invoiceForName, 
                            12, false, true, 1, UnderlinePatterns.SINGLE, ParagraphAlignment.LEFT));
        //line...
        List<ZcaWordParagraph> aZcaWordParagraphList = new ArrayList<>();
        aZcaWordParagraphList.add(new ZcaWordParagraph("Payable to:" + ZcaText.getWhiteSpaces(5),
                                10, false, true, 0, null, ParagraphAlignment.LEFT));
        aZcaWordParagraphList.add(new ZcaWordParagraph("YIN LU CPA, PC",
                10, true, false, 0, null, ParagraphAlignment.LEFT));
        doc.addParagraphTextSet(aZcaWordParagraphList);
        //line...
        aZcaWordParagraphList.clear();
        aZcaWordParagraphList.add(new ZcaWordParagraph("Due Date:" + ZcaText.getWhiteSpaces(7),
                                                       10, false, true, 0, null, ParagraphAlignment.LEFT));
        aZcaWordParagraphList.add(new ZcaWordParagraph(ZcaCalendar.convertToMMddyyyy(aPeonyBillPayment.getBillDueDate(), "-"), 
                                  10, true, false, 0, null, ParagraphAlignment.LEFT));
        aZcaWordParagraphList.add(new ZcaWordParagraph(ZcaText.getWhiteSpaces(10) + "Created Date:" + ZcaText.getWhiteSpaces(5),
                                                       10, false, true, 0, null, ParagraphAlignment.LEFT));
        aZcaWordParagraphList.add(new ZcaWordParagraph(ZcaCalendar.convertToMMddyyyy(aPeonyBillPayment.getBill().getCreated(), "-"), 
                                  10, true, false, 0, null, ParagraphAlignment.LEFT));
        doc.addParagraphTextSet(aZcaWordParagraphList);
        //line...
        aZcaWordParagraphList.clear();
        aZcaWordParagraphList.add(new ZcaWordParagraph("Price:" + ZcaText.getWhiteSpaces(5),
                                10, false, true, 0, null, ParagraphAlignment.LEFT));
        aZcaWordParagraphList.add(new ZcaWordParagraph(aPeonyBillPayment.getBillPriceText(),
                                10, true, false, 0, null, ParagraphAlignment.LEFT));
        aZcaWordParagraphList.add(new ZcaWordParagraph(ZcaText.getWhiteSpaces(10) + "Discount:" + ZcaText.getWhiteSpaces(5),
                                10, false, true, 0, null, ParagraphAlignment.LEFT));
        aZcaWordParagraphList.add(new ZcaWordParagraph(aPeonyBillPayment.getBillDiscountText(),
                                10, true, false, 0, null, ParagraphAlignment.LEFT));
        aZcaWordParagraphList.add(new ZcaWordParagraph(ZcaText.getWhiteSpaces(10) + "Total:" + ZcaText.getWhiteSpaces(5),
                                10, false, true, 0, null, ParagraphAlignment.LEFT));
        aZcaWordParagraphList.add(new ZcaWordParagraph(aPeonyBillPayment.getBillDiscountedPriceText(),
                                10, true, false, 0, null, ParagraphAlignment.LEFT));
        aZcaWordParagraphList.add(new ZcaWordParagraph(ZcaText.getWhiteSpaces(10) + "Balance:" + ZcaText.getWhiteSpaces(5),
                                10, false, true, 0, null, ParagraphAlignment.LEFT));
        aZcaWordParagraphList.add(new ZcaWordParagraph(aPeonyBillPayment.getBillBalanceText(),
                                10, true, false, 1, null, ParagraphAlignment.LEFT));
        doc.addParagraphTextSet(aZcaWordParagraphList);
        //line...
        doc.addParagraphText(new ZcaWordParagraph("CONTENT:" + ZcaText.getWhiteSpaces(5),
                                11, false, true, 1, null, ParagraphAlignment.LEFT));
        //line...description
        doc.addParagraphText(new ZcaWordParagraph(aPeonyBillPayment.getBillContent(),
                                10, true, false, 1, null, ParagraphAlignment.LEFT));
        //line...payments
        doc.addParagraphText(new ZcaWordParagraph("PAYMENTS:" + ZcaText.getWhiteSpaces(5),
                                11, false, true, 1, null, ParagraphAlignment.LEFT));
        List<PeonyPayment> aPeonyPaymentList = aPeonyBillPayment.getPaymentList();
        if ((aPeonyPaymentList == null) || (aPeonyPaymentList.isEmpty())){
            doc.addParagraphText(new ZcaWordParagraph("No any payment received yet. 此账单还没有收到任何付款。",
                                    10, true, false, 3, null, ParagraphAlignment.LEFT));
        }else{
            PeonyPayment aPeonyPayment;
            int max = aPeonyPaymentList.size();
            for (int i = 0; i < max; i++){
                aPeonyPayment = aPeonyPaymentList.get(i);
                if (i == (max-1)){
                    printPayment(doc, aPeonyPayment, i+1, 3);
                }else{
                    printPayment(doc, aPeonyPayment, i+1, 0);
                }
            }
        }
        
        //claim statement
        constructClaimStatement(doc);
        
        //save and open the file...
        doc.writeToPhysicalFile();
        PeonyFaceUtils.openFile(doc.getWordFilePath().toAbsolutePath().toFile());
    }

    private void printPayment(ZcaWordDocument doc, PeonyPayment aPeonyPayment, int index, int crNum) {
        List<ZcaWordParagraph> aZcaWordParagraphList = new ArrayList<>();
        aZcaWordParagraphList.add(new ZcaWordParagraph("Payment #" + index + ZcaText.getWhiteSpaces(3),
                                9, false, true, 0, null, ParagraphAlignment.LEFT));
        aZcaWordParagraphList.add(new ZcaWordParagraph(aPeonyPayment.getPaymentType(),
                                9, true, false, 0, null, ParagraphAlignment.LEFT));
        aZcaWordParagraphList.add(new ZcaWordParagraph(ZcaText.getWhiteSpaces(5) + "Paid:" + ZcaText.getWhiteSpaces(3),
                                9, false, true, 0, null, ParagraphAlignment.LEFT));
        aZcaWordParagraphList.add(new ZcaWordParagraph(aPeonyPayment.getPaymentPriceText(),
                                9, true, false, 0, null, ParagraphAlignment.LEFT));
        aZcaWordParagraphList.add(new ZcaWordParagraph(ZcaText.getWhiteSpaces(5) + "Date:" + ZcaText.getWhiteSpaces(3),
                                9, false, true, 0, null, ParagraphAlignment.LEFT));
        if (aPeonyPayment.getPayment().getPaymentDate() == null) {
            aZcaWordParagraphList.add(new ZcaWordParagraph("N/A", 9, true, false, 0, null, ParagraphAlignment.LEFT));
        }else{
            aZcaWordParagraphList.add(new ZcaWordParagraph(ZcaCalendar.convertToMMddyyyy(aPeonyPayment.getPayment().getPaymentDate(), "-"),
                                    9, true, false, 0, null, ParagraphAlignment.LEFT));
        }
        aZcaWordParagraphList.add(new ZcaWordParagraph(ZcaText.getWhiteSpaces(5) + "Notes:" + ZcaText.getWhiteSpaces(3),
                                8, false, true, 0, null, ParagraphAlignment.LEFT));
        aZcaWordParagraphList.add(new ZcaWordParagraph(aPeonyPayment.getPaymentMemo(),
                                8, true, false, crNum, null, ParagraphAlignment.LEFT));
        doc.addParagraphTextSet(aZcaWordParagraphList);
    }

    protected void constructBillsAndPaymentsPart(ZcaWordDocument doc, PeonyBillPayment aPeonyBillPayment) {
        
        doc.addParagraphText(new ZcaWordParagraph(aPeonyBillPayment.getBillTitle(), 10, false, false, 1, null, null));
        List<List<ZcaWordTableCellContent>> tableContent = new ArrayList<>();
        
        //ROW
        List<ZcaWordTableCellContent> rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Bill Price", 8, false, true, "10%"));
        rowContent.add(createCellContent(aPeonyBillPayment.getBillPriceText(), 8, true, false, "20%"));
        rowContent.add(createCellContent("Discount ", 8, false, true, "10%"));
        rowContent.add(createCellContent(aPeonyBillPayment.getBillDiscountText(), 8, true, false, "20%"));
        rowContent.add(createCellContent("Due Date ", 8, false, true, "10%"));
        if (aPeonyBillPayment.getBillDueDate() == null) {
            rowContent.add(createCellContent("N/A", 8, true, false, "30%"));
        }else{
            rowContent.add(createCellContent(ZcaCalendar.convertToMMddyyyy(aPeonyBillPayment.getBillDueDate(), "-"), 8, true, false, "30%"));
        }
        tableContent.add(rowContent);
        
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Bill Content", 8, false, true, "100%"));
        tableContent.add(rowContent);
        
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent(aPeonyBillPayment.getBillContent(), 8, true, false, "100%"));
        tableContent.add(rowContent);
        
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Payments for this bill:", 8, false, true, "100%"));
        tableContent.add(rowContent);
        
        
        //ROW
        rowContent = new ArrayList<>();
        //CELLS
        rowContent.add(createCellContent("Payment Type", 8, false, true, "15%"));
        rowContent.add(createCellContent("Paid", 8, false, true, "10%"));
        rowContent.add(createCellContent("Date", 8, false, true, "15%"));
        rowContent.add(createCellContent("Deposited", 8, false, true, "20%"));
        rowContent.add(createCellContent("Memo", 8, false, true, "10%"));
        rowContent.add(createCellContent("", 8, false, true, "30%"));
        tableContent.add(rowContent);
        
        List<PeonyPayment> paymentList = aPeonyBillPayment.getPaymentList();
        for (PeonyPayment aPeonyPayment : paymentList){
            rowContent = new ArrayList<>();
            //CELLS
            rowContent.add(createCellContent(aPeonyPayment.getPaymentType(), 8, true, false, "15%"));
            rowContent.add(createCellContent(aPeonyPayment.getPaymentPriceText(), 8, true, false, "10%"));
            if (aPeonyPayment.getPayment().getPaymentDate() == null) {
                rowContent.add(createCellContent("N/A", 8, true, false, "15%"));
            }else{
                rowContent.add(createCellContent(ZcaCalendar.convertToMMddyyyy(aPeonyPayment.getPayment().getPaymentDate(), "-"), 8, true, false, "15%"));
            }
            rowContent.add(createCellContent(aPeonyPayment.getDepositStatus(), 8, true, false, "20"));
            rowContent.add(createCellContent(aPeonyPayment.getPaymentMemo(), 8, true, false, "10%"));
            rowContent.add(createCellContent("", 8, false, true, "30%"));
            tableContent.add(rowContent);
        }
        
        
        XWPFTable table = doc.createTable(tableContent, 0);
        
        ZcaWordDocument.mergeCellHorizontally(table, 1, 0, 5);
        ZcaWordDocument.mergeCellHorizontally(table, 2, 0, 5);
        ZcaWordDocument.mergeCellHorizontally(table, 3, 4, 5);
        int count = 3;
        for (PeonyPayment aPeonyPayment : paymentList){
            count++;
            ZcaWordDocument.mergeCellHorizontally(table, count, 4, 5);
        }
        
    }

    protected void displayTaxCaseExportForPrintDialog() {
        PeonyFaceUtils.displayWarningMessageDialog("PeonyEntityOwnerFaceController: no dialog implementation available yet.");
    }
    
    protected void displayServiceTagEditorHelper(PeonyDocumentTag targetDocumentTag, PeonyFaceEventListener dialogListner, String dialogTitle){
        if (targetDocumentTag == null){
            PeonyFaceUtils.displayErrorMessageDialog("Please select a service tag from the list.");
            return;
        }
        DocumentTagDataEntryDialog aDocumentTagDataEntryDialog = new DocumentTagDataEntryDialog(null, true);
        aDocumentTagDataEntryDialog.addPeonyFaceEventListener(dialogListner);
        aDocumentTagDataEntryDialog.launchMemoDataEntryDialog(dialogTitle, targetDocumentTag);
    }

    public void publishPeonyMemo(PeonyMemo peonyMemo) {
        PublicBoardController aPublicBoardController = getPublicBoardController();
        if (aPublicBoardController != null){
            aPublicBoardController.publishPeonyMemo(peonyMemo);
        }
    }
}
