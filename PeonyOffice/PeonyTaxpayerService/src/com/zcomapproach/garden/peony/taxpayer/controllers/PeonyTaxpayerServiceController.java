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

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.peony.view.controllers.PeonyEntityOwnerFaceController;
import com.zcomapproach.garden.persistence.entity.G02DocumentTag;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerInfo;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.garden.persistence.peony.data.PeonyPredefinedDocumentTag;
import com.zcomapproach.garden.persistence.peony.data.PeonyPredefinedDocumentTagType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

/**
 *
 * @author zhijun98
 */
public abstract class PeonyTaxpayerServiceController extends PeonyEntityOwnerFaceController{
    public static String NO_CONTACT = "No contact information yet.";
    
////    private ComboBox<PeonyTaxpayerCase> compareLagacyComboBox;
    /**
     * FXML controllers used in this master controller
     */
    private final List<PeonyTaxpayerServiceController> peonyTaxpayerServiceControllerList = new ArrayList<>();

    public PeonyTaxpayerServiceController(Object targetOwner) {
        super(targetOwner);
    }

////    public PeonyTaxpayerServiceController(Object targetOwner, ComboBox<PeonyTaxpayerCase> compareLagacyComboBox) {
////        super(targetOwner);
////        this.compareLagacyComboBox = compareLagacyComboBox;
////    }
    
    /**
     * Help load the FXML file for controllers
     * @param aFXMLLoader
     * @return
     * @throws IOException 
     */
    @Override
    protected Pane loadFxmlHelper(FXMLLoader aFXMLLoader) throws IOException{
        return (Pane)aFXMLLoader.load();
    }

////    protected void launchCompareComboBoxInitialization(final PeonyTaxpayerCase targetPeonyTaxpayerCase, 
////                                                       final ComboBox<PeonyTaxpayerCase> compareComboBox,
////                                                       final Label newClientLabel,
////                                                       final HBox topButtonsHBox) 
////    {   
////        this.compareLagacyComboBox = compareComboBox;
////        compareLagacyComboBox.valueProperty().addListener((obs, oldValue, newValue) -> populateLegacyPeonyTaxpayerCaseComparison(newValue));
////        Task<List<PeonyTaxpayerCase>> task = new Task<List<PeonyTaxpayerCase>>(){
////            @Override
////            protected List<PeonyTaxpayerCase> call() throws Exception {
////                List<PeonyTaxpayerCase> legacyPeonyTaxpayerCaseList = targetPeonyTaxpayerCase.getLegacyPeonyTaxpayerCaseList();
////                if (legacyPeonyTaxpayerCaseList == null){
////                    legacyPeonyTaxpayerCaseList = new ArrayList<>();
////                }
////                if (legacyPeonyTaxpayerCaseList.isEmpty()){
////                    if (targetPeonyTaxpayerCase.getCustomer() != null){
////                        PeonyTaxpayerCaseList aPeonyTaxpayerCaseList = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient()
////                                .findEntity_XML(PeonyTaxpayerCaseList.class, 
////                                        GardenRestParams.Taxpayer.findBasicPeonyTaxpayerCaseListByPrimaryTaxpayerSsnRestParams(
////                                                targetPeonyTaxpayerCase.getCustomer().getSsn()));
////                        if ((aPeonyTaxpayerCaseList == null) || (aPeonyTaxpayerCaseList.getPeonyTaxpayerCaseList() == null)){
////                            return new ArrayList<>();
////                        }else{
////                            return aPeonyTaxpayerCaseList.getPeonyTaxpayerCaseList();
////                        }
////                    }
////                }
////                        
////                compareLagacyComboBox.getItems().addListener(new ListChangeListener<PeonyTaxpayerCase>(){
////                    @Override
////                    public void onChanged(ListChangeListener.Change<? extends PeonyTaxpayerCase> event) {
////                        int fromIndex = event.getFrom();
////                        int toIndex = event.getTo();
////                        if (toIndex != fromIndex){
////                            populateLegacyPeonyTaxpayerCaseComparison(compareLagacyComboBox.getItems().get(toIndex));
////                        }
////                    }
////                });
////                return legacyPeonyTaxpayerCaseList;
////            }
////
////            @Override
////            protected void succeeded() {
////                try {
////                    List<PeonyTaxpayerCase> legacyPeonyTaxpayerCaseList = get();
////                    if (legacyPeonyTaxpayerCaseList.isEmpty()){
////                        topButtonsHBox.getChildren().remove(compareLagacyComboBox);
////                        newClientLabel.setVisible(true);
////                    }else{
////                        Collections.sort(legacyPeonyTaxpayerCaseList, new Comparator<PeonyTaxpayerCase>(){
////                            @Override
////                            public int compare(PeonyTaxpayerCase o1, PeonyTaxpayerCase o2) {
////                                return o1.getTaxpayerCase().getDeadline().compareTo(o2.getTaxpayerCase().getDeadline())*(-1);
////                            }
////                        });
////                        Date targetDeadline = targetPeonyTaxpayerCase.getTaxpayerCase().getDeadline();
////                        String targetEntityStatus = targetPeonyTaxpayerCase.getTaxpayerCase().getEntityStatus();
////                        PeonyTaxpayerCase selectedPeonyTaxpayerCase = null; //saved by users before according to targetEntityStatus
////                        for (PeonyTaxpayerCase legacyPeonyTaxpayerCase : legacyPeonyTaxpayerCaseList){
////                            if (targetDeadline.compareTo(legacyPeonyTaxpayerCase.getTaxpayerCase().getDeadline()) != 0){
////                                if ((targetEntityStatus != null) && (targetEntityStatus.equalsIgnoreCase(legacyPeonyTaxpayerCase.getTaxpayerCaseTitle()))){
////                                    selectedPeonyTaxpayerCase = legacyPeonyTaxpayerCase;
////                                }
////                                compareLagacyComboBox.getItems().add(legacyPeonyTaxpayerCase);
////                                if (isPreviousYear(targetPeonyTaxpayerCase, legacyPeonyTaxpayerCase)){
////                                    publishPreviousYearBillPayments(legacyPeonyTaxpayerCase);
////                                }
////                            }
////                        }
////                        if (compareLagacyComboBox.getItems().isEmpty()){
////                            topButtonsHBox.getChildren().remove(compareLagacyComboBox);
////                            newClientLabel.setVisible(true);
////                        }else{
////                            topButtonsHBox.getChildren().remove(newClientLabel);
////                            compareLagacyComboBox.setVisible(true);
////                            if (selectedPeonyTaxpayerCase == null){
////                                //default: compare to the previous year
////                                selectedPeonyTaxpayerCase = selectPreviousYearTaxpayerCase(targetPeonyTaxpayerCase);
////                            }
////                            compareLagacyComboBox.getSelectionModel().select(selectedPeonyTaxpayerCase);
////                            populateLegacyPeonyTaxpayerCaseComparison(selectedPeonyTaxpayerCase);
////                        }
////                    }
////                } catch (InterruptedException | ExecutionException ex) {
////                    //Exceptions.printStackTrace(ex);
////                    PeonyFaceUtils.displayErrorMessageDialog("Failed to initialize legacy taxpayer case list. " + ex.getMessage());
////                }
////            }
////
////            private PeonyTaxpayerCase selectPreviousYearTaxpayerCase(PeonyTaxpayerCase targetPeonyTaxpayerCase) {
////                int year = ZcaCalendar.parseCalendarField(targetPeonyTaxpayerCase.getTaxpayerCase().getDeadline(), Calendar.YEAR);
////                Collection<PeonyTaxpayerCase> aPeonyTaxpayerCaseCollection = compareLagacyComboBox.getItems();
////                PeonyTaxpayerCase result = null;
////                int yearResult;
////                for (PeonyTaxpayerCase aPeonyTaxpayerCase : aPeonyTaxpayerCaseCollection){
////                    yearResult = ZcaCalendar.parseCalendarField(aPeonyTaxpayerCase.getTaxpayerCase().getDeadline(), Calendar.YEAR);
////                    if (year - yearResult == 1){
////                        result = aPeonyTaxpayerCase;
////                        break;
////                    }
////                }//for-loop
////                if (result == null){
////                    return targetPeonyTaxpayerCase;
////                }
////                return result;
////            }
////        };
////        this.getCachedThreadPoolExecutorService().submit(task);
////    }
////
////    private boolean isPreviousYear(PeonyTaxpayerCase targetPeonyTaxpayerCase, PeonyTaxpayerCase legacyPeonyTaxpayerCase) {
////        int targetYear = ZcaCalendar.parseCalendarField(targetPeonyTaxpayerCase.getTaxpayerCase().getDeadline(), Calendar.YEAR);
////        int legacyYear = ZcaCalendar.parseCalendarField(legacyPeonyTaxpayerCase.getTaxpayerCase().getDeadline(), Calendar.YEAR);
////        return (targetYear - legacyYear) == 1;
////    }
    
    protected void publishPreviousYearBillPayments(PeonyTaxpayerCase legacyPeonyTaxpayerCase){
        //do nothing. But the related concrete class implement this method
    }
    
    protected void listenToCompareLegacyComboBox(PeonyTaxpayerServiceController aPeonyTaxpayerServiceController){
        if (aPeonyTaxpayerServiceController == null){
            return;
        }
        peonyTaxpayerServiceControllerList.remove(aPeonyTaxpayerServiceController);
        peonyTaxpayerServiceControllerList.add(aPeonyTaxpayerServiceController);
    }
    
    protected void notListenToCompareLegacyComboBox(PeonyTaxpayerServiceController aPeonyTaxpayerServiceController){
        if (aPeonyTaxpayerServiceController == null){
            return;
        }
        peonyTaxpayerServiceControllerList.remove(aPeonyTaxpayerServiceController);
    }

    /**
     * if the implementation class involves the comparison with legacyPeonyTaxpayerCase, 
     * it has to override this method. Otherwise, this method does nothing.
     * @param legacyPeonyTaxpayerCase 
     */
    protected void populateLegacyPeonyTaxpayerCaseComparison(PeonyTaxpayerCase legacyPeonyTaxpayerCase) {
        if (legacyPeonyTaxpayerCase == null) {
            return;
        }
        for (PeonyTaxpayerServiceController aPeonyTaxpayerServiceController : peonyTaxpayerServiceControllerList){
            aPeonyTaxpayerServiceController.populateLegacyPeonyTaxpayerCaseComparison(legacyPeonyTaxpayerCase);
        }
    }
    
////    protected void handleSaveTaxpayerCaseButtonWithCompareComboBox(PeonyTaxpayerCase targetPeonyTaxpayerCase, ComboBox<PeonyTaxpayerCase> compareComboBox){
////////        //check if the work-status was selected
////////        if (ZcaValidator.isNullEmpty(targetPeonyTaxpayerCase.getTaxpayerCase().getLatestLogUuid())){
////////            PeonyFaceUtils.displayErrorMessageDialog("You have to select current work status before save anything.");
////////            return;
////////        }else{
////////////            if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "需要改动work status吗? 如果不需要，点击【No】进入下一步存储") == JOptionPane.YES_OPTION){
////////////                return;
////////////            }
////////        }
////////        //check if the legacy data was compared
////////        if (!compareComboBox.getItems().isEmpty()){
////////            if ((compareComboBox.getValue() == null)){
////////                compareComboBox.setStyle(PeonyFaceUtils.WARNING_STYLE);
////////                PeonyFaceUtils.displayErrorMessageDialog("You did not confirm this data entry with legacy year's data before you save it.");
////////                return;
////////            }else{
////////                String status = compareComboBox.getValue().getTaxpayerCaseTitle();
////////                targetPeonyTaxpayerCase.getTaxpayerCase().setEntityStatus(status);
////////            }
////////        }
////////        compareComboBox.setStyle(null);
////        
////        handleSaveTaxpayerCaseButtonWithCompareComboBoxImpl();
////    }
////    
////    protected void handleSaveTaxpayerCaseButtonWithCompareComboBoxImpl(){
////    }
    
////    protected Task<G02TaxpayerInfo> createSaveTaxpayerInfoDataEntryTask(G02TaxpayerInfo targetTaxpayerInfo, ComboBox<PeonyTaxpayerCase> compareComboBox){
////        return new Task<G02TaxpayerInfo>(){
////            @Override
////            protected G02TaxpayerInfo call() throws Exception {
////                try{
////                    G02TaxpayerCase aG02TaxpayerCase;
////                    if (ZcaValidator.isNullEmpty(targetTaxpayerInfo.getTaxpayerCaseUuid())){
////                        aG02TaxpayerCase = null;
////                    }else{
////                        try{
////                            loadTargetTaxpayerInfo();
////                        }catch (ZcaEntityValidationException ex){
////                            updateMessage(ex.getMessage());
////                            return null;
////                        }
////                        aG02TaxpayerCase = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient().findEntity_XML(G02TaxpayerCase.class, 
////                                GardenRestParams.Taxpayer.findTaxpayerCaseByTaxpayerCaseUuidRestParams(targetTaxpayerInfo.getTaxpayerCaseUuid()));
////                    }
////                    if (aG02TaxpayerCase == null){
////                        updateMessage("This taxpayer info data entry is NOT saved. Please save Taxpayer case's basic information before save this data entry.");
////                        return null;
////                    }else{
////                        G02TaxpayerInfo aG02TaxpayerInfo = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient().storeEntity_XML(
////                                G02TaxpayerInfo.class, GardenRestParams.Taxpayer.storeTaxpayerInfoRestParams(), targetTaxpayerInfo);
////                        if (aG02TaxpayerInfo == null){
////                            updateMessage("Failed to save this data entry.");
////                            return null;
////                        }else{
////                            updateMessage("Successfully saved this data entry.");
////                        }
////                    }
////                    return targetTaxpayerInfo;
////                }catch (Exception ex){
////                    updateMessage("Technical error. " + ex.getMessage());
////                    return null;
////                }
////            }
////
////            @Override
////            protected void succeeded() {
////                //GUI...
////                int msgType = JOptionPane.INFORMATION_MESSAGE;
////                try {
////                    G02TaxpayerInfo aG02TaxpayerInfo = get();
////                    if (aG02TaxpayerInfo == null){
////                        msgType = JOptionPane.ERROR_MESSAGE;
////                    }else{
////                        //saved taxpayer info data entry
////                        broadcastPeonyFaceEventHappened(new TaxpayerInfoDataEntrySavedEvent(aG02TaxpayerInfo));
////                        //close the data entry dialog
////                        broadcastPeonyFaceEventHappened(new TaxpayerDataEntryDialogCloseRequest(aG02TaxpayerInfo.getTaxpayerUserUuid()));
////                        
////                        resetDataEntryStyle();
////                        
////                        populateLegacyPeonyTaxpayerCaseComparison(compareComboBox.getValue());
////                    }
////                } catch (InterruptedException | ExecutionException ex) {
////                    Exceptions.printStackTrace(ex);
////                }
////
////                //display result information dialog
////                String msg = getMessage();
////                if (ZcaValidator.isNotNullEmpty(msg)){
////                    PeonyFaceUtils.publishMessageOntoOutputWindow(msg);
////                }
////            }
////        };
////    }
    
    protected String constructContactInfo(G02TaxpayerInfo targetTaxpayerInfo) {
        String contact = "";
        if (ZcaValidator.isNotNullEmpty(targetTaxpayerInfo.getMobilePhone())){
            contact = targetTaxpayerInfo.getMobilePhone();
        }
        if (ZcaValidator.isNotNullEmpty(targetTaxpayerInfo.getEmail())){
            if (ZcaValidator.isNullEmpty(contact)){
                contact = targetTaxpayerInfo.getEmail();
            }else{
                contact += ";  " + targetTaxpayerInfo.getEmail();
            }
        }
        if (ZcaValidator.isNotNullEmpty(targetTaxpayerInfo.getSocialNetworkId())){
            if (ZcaValidator.isNullEmpty(contact)){
                contact = "WeChat: " + targetTaxpayerInfo.getSocialNetworkId();
            }else{
                contact += ";  WeChat: " + targetTaxpayerInfo.getSocialNetworkId();
            }
        }
        if (ZcaValidator.isNullEmpty(contact)){
            contact = NO_CONTACT;
        }
        return contact;
    }

    protected List<G02DocumentTag> sortDocumentTagList(List<G02DocumentTag> documentTagList) {
        List<G02DocumentTag> result = new ArrayList<>();
        LinkedHashMap<PeonyPredefinedDocumentTagType, List<G02DocumentTag>> resultMap = sortDocumentTagListHelper(documentTagList);
        Set<PeonyPredefinedDocumentTagType> keys = resultMap.keySet();
        Iterator<PeonyPredefinedDocumentTagType> itr = keys.iterator();
        while (itr.hasNext()){
            result.addAll(resultMap.get(itr.next()));
        }
        return result;
    }

    private LinkedHashMap<PeonyPredefinedDocumentTagType, List<G02DocumentTag>> sortDocumentTagListHelper(List<G02DocumentTag> documentTagList) {
        LinkedHashMap<PeonyPredefinedDocumentTagType, List<G02DocumentTag>> result = new LinkedHashMap<>();
        List<PeonyPredefinedDocumentTagType> aPeonyPredefinedDocumentTagTypeList = PeonyPredefinedDocumentTagType.getPeonyPredefinedDocumentTagTypeListByOrder();
        List<PeonyPredefinedDocumentTag> aPeonyPredefinedDocumentTagList;
        G02DocumentTag aDocumentTag;
        List<G02DocumentTag> tags;
        for (PeonyPredefinedDocumentTagType tagType : aPeonyPredefinedDocumentTagTypeList){
            if (!PeonyPredefinedDocumentTagType.CUSTOM.equals(tagType)){
                aPeonyPredefinedDocumentTagList = PeonyPredefinedDocumentTag.getPeonyPredefinedDocumentTagListByOrder(tagType);
                for (PeonyPredefinedDocumentTag tag : aPeonyPredefinedDocumentTagList){
                    aDocumentTag = findG02DocumentTag(documentTagList, tagType, tag);
                    if (aDocumentTag != null){
                        tags = result.get(tagType);
                        if (tags == null){
                            tags = new ArrayList<>();
                        }
                        result.put(tagType, tags);
                        tags.add(aDocumentTag);
                    }
                }
            }
        }
        
        result.put(PeonyPredefinedDocumentTagType.CUSTOM, findOthersDocumentTags(documentTagList));
        
        return result;
    }
    private List<G02DocumentTag> findOthersDocumentTags(List<G02DocumentTag> aG02DocumentTagList){
        List<G02DocumentTag> result = new ArrayList<>();
        for (G02DocumentTag aG02DocumentTag : aG02DocumentTagList){
            if ((PeonyPredefinedDocumentTagType.CUSTOM.value().equalsIgnoreCase(aG02DocumentTag.getDocumentTagType()))
                    && (PeonyPredefinedDocumentTag.OTHERS.value().equalsIgnoreCase(aG02DocumentTag.getDocumentTagName())))
            {
                result.add(aG02DocumentTag);
            }
        }
        return result;
    }
    private G02DocumentTag findG02DocumentTag(List<G02DocumentTag> aG02DocumentTagList, PeonyPredefinedDocumentTagType tagType, PeonyPredefinedDocumentTag tag){
        for (G02DocumentTag aG02DocumentTag : aG02DocumentTagList){
            if ((tagType.value().equalsIgnoreCase(aG02DocumentTag.getDocumentTagType()))
                    && (tag.value().equalsIgnoreCase(aG02DocumentTag.getDocumentTagName())))
            {
                return aG02DocumentTag;
            }
        }
        return null;
    }
}
