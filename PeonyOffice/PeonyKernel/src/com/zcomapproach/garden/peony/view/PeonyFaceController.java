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

package com.zcomapproach.garden.peony.view;

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.exception.EntityField;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.peony.kernel.services.PeonyBusinessService;
import com.zcomapproach.garden.peony.kernel.services.PeonySecurityService;
import com.zcomapproach.garden.peony.security.PeonyPrivilege;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.controllers.PublicBoardController;
import com.zcomapproach.garden.peony.view.events.PeonyDataEntrySaveDemanding;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.util.GardenThreadingUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.constant.GardenTaxFilingStatus;
import com.zcomapproach.garden.persistence.entity.G02Log;
import com.zcomapproach.garden.persistence.entity.G02TaxFilingCase;
import com.zcomapproach.garden.persistence.peony.PeonyMemo;
import com.zcomapproach.garden.persistence.peony.PeonyTaxFilingCase;
import com.zcomapproach.garden.persistence.peony.data.PeonyLogName;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.garden.util.GardenData;
import java.awt.Dimension;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 * 
 * @author zhijun98
 */
public abstract class PeonyFaceController implements Initializable {

    /**
     * This single thread keep the task-order
     */
    private final ExecutorService cachedThreadPoolExecutorService = Executors.newCachedThreadPool();
    private final ExecutorService singleExecutorService = Executors.newSingleThreadExecutor();
    
    private final List<PeonyFaceEventListener> peonyFaceEventListenerList = new ArrayList<>();
    
    private Pane rootPane;
    
    /**
     * The FXML file name for this controller. if no data is provided for this, the 
     * default-name will be used. The default name is the controller's class name plus 
     * ".fxml" file extendsion. This file name does not demand ".fxml" file extension
     */
    private String loadingFxmlFileNameWithoutExtension;
    
    /**
     * How to get the real size of the root pane after load is unknown so far. Here, 
     * it may use the following data-field to keep the predefined dimension of the 
     * root pane
     * @return 
     */
    private Dimension defaultRootPaneSize;
    
    private PublicBoardController publicBoardController;
    
    private boolean dataEntryChanged;

    public PeonyFaceController() {
    }
    
    public synchronized boolean isDataEntryChanged() {
        return dataEntryChanged;
    }
    protected synchronized void setDataEntryChanged(boolean dataEntryChanged) {
        this.dataEntryChanged = dataEntryChanged;
    }  
    
    protected PublicBoardController getPublicBoardController() {
        return publicBoardController;
    }
    
    protected void initializePublicBoardController(final String boardTitle, 
                                                    final List<PeonyMemo> peonyMemoList,
                                                    final Date fromDate, 
                                                    final Date toDate,
                                                    final Object targetOwner)
    {
        if (publicBoardController == null){
            publicBoardController = new PublicBoardController(boardTitle, peonyMemoList, fromDate, toDate, targetOwner);
            try {
                publicBoardController.loadFxml();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    protected void publishPeonyMemoOnBoard(PeonyMemo peonyMemo) {
        if (publicBoardController != null){
            publicBoardController.publishPeonyMemo(peonyMemo);
        }
    }
    
    public String getLoadingFxmlFileNameWithoutExtension() {
        return loadingFxmlFileNameWithoutExtension;
    }

    /**
     * 
     * @param loadingFxmlFileNameWithoutExtension - The FXML file name for this controller 
     * to load. if no data is provided for this, the default-name will be used. The default 
     * name is the controller's class name plus ".fxml" file extendsion. This file name 
     * does not demand ".fxml" file extension
     */
    protected void setLoadingFxmlFileNameWithoutExtension(String loadingFxmlFileNameWithoutExtension) {
        this.loadingFxmlFileNameWithoutExtension = loadingFxmlFileNameWithoutExtension;
    }
    
    public void publishPeonyDataEntrySaveDemandingStatus(boolean saveDemandingStatus) {
        setDataEntryChanged(saveDemandingStatus);
        broadcastPeonyFaceEventHappened(new PeonyDataEntrySaveDemanding(saveDemandingStatus));
    }

    /**
     * The root Pane of JavaFX GUI
     * @return 
     */
    public Pane getRootPane() {
        return rootPane;
    }
    
    private void setRootPane(Pane rootPane) {
        this.rootPane = rootPane;
    }

    public Dimension getDefaultRootPaneSize() {
        return defaultRootPaneSize;
    }

    public void setDefaultRootPaneSize(Dimension defaultRootPaneSize) {
        this.defaultRootPaneSize = defaultRootPaneSize;
    }

    public List<PeonyFaceEventListener> getPeonyFaceEventListenerList() {
        synchronized(peonyFaceEventListenerList){
            return new ArrayList<>(peonyFaceEventListenerList);
        }
    }
    
    public void addPeonyFaceEventListenerList(List<PeonyFaceEventListener> listeners){
        if ((listeners == null) || (listeners.isEmpty())){
            return;
        }
        synchronized(peonyFaceEventListenerList){
            for (PeonyFaceEventListener listener : listeners){
                addPeonyFaceEventListener(listener);
            }
        }
    }
    
    public void addPeonyFaceEventListener(PeonyFaceEventListener listener){
        synchronized(peonyFaceEventListenerList){
            peonyFaceEventListenerList.remove(listener);
            peonyFaceEventListenerList.add(listener);
        }
    }
    
    public void removePeonyFaceEventListener(PeonyFaceEventListener listener){
        synchronized(peonyFaceEventListenerList){
            peonyFaceEventListenerList.remove(listener);
        }
    }
    
    /**
     * Broadcase event to listners sequentially in a single thread service 
     * @param event 
     */
    protected void broadcastPeonyFaceEventHappened(PeonyFaceEvent event){
        synchronized(peonyFaceEventListenerList){
            for (int i = 0; i < peonyFaceEventListenerList.size(); i++){
                final PeonyFaceEventListener aPeonyFaceControllerEventListener = peonyFaceEventListenerList.get(i);
                cachedThreadPoolExecutorService.submit(new Runnable(){
                    @Override
                    public void run() {
                        aPeonyFaceControllerEventListener.peonyFaceEventHappened(event);
                    }
                });
            }//for
        }
    }

    public void close() {
        GardenThreadingUtils.shutdownExecutorService(cachedThreadPoolExecutorService, 15, TimeUnit.SECONDS);
    }
    
    /**
     * an instance of Executors.newSingleThreadExecutor()
     * @return - Notice that NOT every controll has a ExecutorService instance 
     */
    public final ExecutorService getCachedThreadPoolExecutorService(){
        return cachedThreadPoolExecutorService;
    }

    public ExecutorService getSingleExecutorService() {
        return singleExecutorService;
    }
    
    /**
     * USE loadFxml() instead of this method. In the netbeans-platform application, 
     * the controller and the FXMLLoader have to stay in the same module so as to 
     * successfully loadFxml FXML file. If the extended dialog's FXML file stays 
     * in the different module, this method has to be overridden in that module 
     * even if no any change in the implementation.
     * @param aFXMLLoader
     * @return
     * @throws IOException 
     */
//    protected abstract Pane loadFxmlHelper(FXMLLoader aFXMLLoader) throws IOException;
    protected Pane loadFxmlHelper(FXMLLoader aFXMLLoader) throws IOException{
        return (Pane)aFXMLLoader.load();
        
    }
    
    public Pane loadFxml() throws IOException {
        Class controllerClass = getClass();
        FXMLLoader aFXMLLoader;
        if (ZcaValidator.isNullEmpty(loadingFxmlFileNameWithoutExtension)){
            aFXMLLoader = new FXMLLoader(controllerClass.getResource(controllerClass.getSimpleName()+".fxml"));
        }else{
            aFXMLLoader = new FXMLLoader(controllerClass.getResource(loadingFxmlFileNameWithoutExtension+".fxml"));
        }
        aFXMLLoader.setController(this);

        setRootPane(loadFxmlHelper(aFXMLLoader));
        return getRootPane();
    }

    /**
     * This method helps JavaFX GUI control-field to request focus. This method 
     * is triggered by handleJFXPanelLoaded because "requestFocus" is effective 
     * only after controller is full-loaded. Note: if requestFocus is triggered 
     * in the initialized method of controllers, it does not really work because 
     * controller is not full-loaded yet.
     * 
     * @param scene 
     */
    public final void decoratePeonyFaceAfterLoading(final Scene scene){
        if (Platform.isFxApplicationThread()){
            decoratePeonyFaceAfterLoadingHelper(scene);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    decoratePeonyFaceAfterLoadingHelper(scene);
                }
            });
        }
    }
    
    /**
     * Concrete face-controllers demand to override getBadEntityField() method to 
     * complete highlighting objective
     * @param ex 
     */
    protected void highlightBadEntityField(ZcaEntityValidationException ex){
////        Node fieldNode = getBadEntityField(ex.getEntityValidatedFiled());
////        if (fieldNode != null){
////            fieldNode.setStyle("-fx-border-color: #ff0000;");
////        }
        PeonyFaceUtils.publishMessageOntoOutputWindow(ex.getMessage());
    }
    
    /**
     * 
     * @param field
     * @return 
     */
    protected Node getBadEntityField(EntityField field){
        return null;
    }
    
    /**
     * This method is executed in FxApplicationThread. Do nothing in this implementation. 
     * Override this method to customize "requestFocus" part for controllers
     * 
     * @param scene 
     */
    protected void decoratePeonyFaceAfterLoadingHelper(final Scene scene){
    }
    
    /**
     * @deprecated - memo edit is done by a popup windows to complete
     * @param aPeonyTaxFilingCase
     * @param newMemo 
     */
    protected void saveTaxFilingMemo(final PeonyTaxFilingCase aPeonyTaxFilingCase, final String newMemo) {
        if ((ZcaValidator.isNotNullEmpty(newMemo)) && (newMemo.length() > 400)){    //database is 450 but reserved 50 characters for system-usages
            PeonyFaceUtils.displayErrorMessageDialog("The memo is too lengthy. Its max-number of characters is 400.");
            return;
        }
        Task<G02TaxFilingCase> saveTaxFilingMemoTask = new Task<G02TaxFilingCase>(){
            @Override
            protected G02TaxFilingCase call() throws Exception {
                String oldMemo = aPeonyTaxFilingCase.getMemo();
                //update memo with newMemo
                aPeonyTaxFilingCase.setMemo(newMemo);
                //create log for old memo...
                G02Log log = new G02Log();
                log.setLogUuid(GardenData.generateUUIDString());
                log.setLogName(PeonyLogName.DELETED_TAX_FILING_MEMO.name());
                log.setLogMessage("Deleted memo : " + oldMemo);
                log.setOperatorAccountUuid(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
                log.setCreated(new Date());
                log.setLoggedEntityType(GardenEntityType.TAX_FILING_CASE.name());
                log.setLoggedEntityUuid(aPeonyTaxFilingCase.getTaxFilingCase().getTaxFilingUuid());
                log.setEntityType(GardenEntityType.convertEnumNameToType(aPeonyTaxFilingCase.getTaxFilingCase().getEntityType()).name());
                log.setEntityUuid(aPeonyTaxFilingCase.getTaxFilingCase().getEntityUuid());
                log.setEntityDescription(null);
                //save log
                Lookup.getDefault().lookup(PeonySecurityService.class).log(log);
                
                //create log for new memo...
                log = new G02Log();
                log.setLogUuid(GardenData.generateUUIDString());
                log.setLogName(PeonyLogName.UPDATE_TAX_FILING_MEMO.name());
                log.setLogMessage("Updated memo to be: " + newMemo);
                log.setOperatorAccountUuid(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
                log.setCreated(new Date());
                log.setLoggedEntityType(GardenEntityType.TAX_FILING_CASE.name());
                log.setLoggedEntityUuid(aPeonyTaxFilingCase.getTaxFilingCase().getTaxFilingUuid());
                log.setEntityType(GardenEntityType.convertEnumNameToType(aPeonyTaxFilingCase.getTaxFilingCase().getEntityType()).name());
                log.setEntityUuid(aPeonyTaxFilingCase.getTaxFilingCase().getEntityUuid());
                log.setEntityDescription(null);
                //save log
                Lookup.getDefault().lookup(PeonySecurityService.class).log(log);
                
                //Save the tax-filing- case
                return Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                                .storeEntity_XML(G02TaxFilingCase.class, GardenRestParams.Business.storeTaxFilingCaseRestParams(), aPeonyTaxFilingCase.getTaxFilingCase());
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayErrorMessageDialog("Failed to save extension date because of technical error. " + getMessage());
            }

            @Override
            protected void succeeded() {
                try {
                    get();
                    PeonyFaceUtils.publishMessageOntoOutputWindow(getMessage());
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(saveTaxFilingMemoTask);
    }
    
    protected void saveDeadlineDate(final PeonyTaxFilingCase aPeonyTaxFilingCase, final Date newDeadlineDate) {
        if (!PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.TAX_DEADLINE_UPDATE)){
            PeonyFaceUtils.displayPrivilegeErrorMessageDialog();
            return;
        }
        Task<G02TaxFilingCase> saveDeadlineTask = new Task<G02TaxFilingCase>(){
            @Override
            protected G02TaxFilingCase call() throws Exception {
                Date oldDeadlineDate = aPeonyTaxFilingCase.getDeadline();
                aPeonyTaxFilingCase.setDeadline(newDeadlineDate);
                //create log...
                G02Log log = new G02Log();
                log.setLogUuid(GardenData.generateUUIDString());
                if (newDeadlineDate == null){
                    log.setLogName(PeonyLogName.DELETED_DEADLINE.name());
                    updateMessage("Successfully deleted deadline. (" + ZcaCalendar.convertToMMddyyyyHHmmss(new Date(), "-", "@", ":") + ")");
                }else{
                    log.setLogName(PeonyLogName.STORED_DEADLINE.name());
                    updateMessage("Successfully saved deadline. (" + ZcaCalendar.convertToMMddyyyyHHmmss(new Date(), "-", "@", ":") + ")");
                }
                log.setLogMessage("Deadline updated. New date : "
                        + ZcaCalendar.convertToMMddyyyy(newDeadlineDate, "-") 
                        + "; Old date: " + ZcaCalendar.convertToMMddyyyy(oldDeadlineDate, "-"));
                log.setOperatorAccountUuid(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
                log.setCreated(new Date());
                log.setLoggedEntityType(GardenEntityType.TAX_FILING_CASE.name());
                log.setLoggedEntityUuid(aPeonyTaxFilingCase.getTaxFilingCase().getTaxFilingUuid());
                log.setEntityType(GardenEntityType.convertEnumNameToType(aPeonyTaxFilingCase.getTaxFilingCase().getEntityType()).name());
                log.setEntityUuid(aPeonyTaxFilingCase.getTaxFilingCase().getEntityUuid());
                log.setEntityDescription(null);
                //save log
                Lookup.getDefault().lookup(PeonySecurityService.class).log(log);
                
                return Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                                .storeEntity_XML(G02TaxFilingCase.class, GardenRestParams.Business.storeTaxFilingCaseRestParams(), aPeonyTaxFilingCase.getTaxFilingCase());
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayErrorMessageDialog("Failed to save deadline date because of technical error. " + getMessage());
            }

            @Override
            protected void succeeded() {
                try {
                    get();
                    PeonyFaceUtils.publishMessageOntoOutputWindow(getMessage());
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(saveDeadlineTask);
    }
    
    protected void saveDeadlineExtensionDate(final PeonyTaxFilingCase aPeonyTaxFilingCase, final Date newExtendedDate) {
        if (!PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.TAX_EXTENSION_UPDATE)){
            PeonyFaceUtils.displayPrivilegeErrorMessageDialog();
            return;
        }
        Task<G02TaxFilingCase> saveDeadlineExtensionTask = new Task<G02TaxFilingCase>(){
            @Override
            protected G02TaxFilingCase call() throws Exception {
                Date oldExtendedDate = aPeonyTaxFilingCase.getExtensionDate();
                aPeonyTaxFilingCase.setExtensionDate(newExtendedDate);
                //create log...
                G02Log log = new G02Log();
                log.setLogUuid(GardenData.generateUUIDString());
                if (newExtendedDate == null){
                    log.setLogName(PeonyLogName.DELETED_DEADLINE_EXTENSION.name());
                    updateMessage("Successfully deleted extension. (" + ZcaCalendar.convertToMMddyyyyHHmmss(new Date(), "-", "@", ":") + ")");
                }else{
                    log.setLogName(PeonyLogName.STORED_DEADLINE_EXTENSION.name());
                    updateMessage("Successfully saved extension. (" + ZcaCalendar.convertToMMddyyyyHHmmss(new Date(), "-", "@", ":") + ")");
                }
                log.setLogMessage("Extension updated. New date : "
                        + ZcaCalendar.convertToMMddyyyy(newExtendedDate, "-") 
                        + "; Old date: " + ZcaCalendar.convertToMMddyyyy(oldExtendedDate, "-"));
                log.setOperatorAccountUuid(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
                log.setCreated(new Date());
                log.setLoggedEntityType(GardenEntityType.TAX_FILING_CASE.name());
                log.setLoggedEntityUuid(aPeonyTaxFilingCase.getTaxFilingCase().getTaxFilingUuid());
                log.setEntityType(GardenEntityType.convertEnumNameToType(aPeonyTaxFilingCase.getTaxFilingCase().getEntityType()).name());
                log.setEntityUuid(aPeonyTaxFilingCase.getTaxFilingCase().getEntityUuid());
                log.setEntityDescription(null);
                //save log
                Lookup.getDefault().lookup(PeonySecurityService.class).log(log);
                
                return Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                                .storeEntity_XML(G02TaxFilingCase.class, GardenRestParams.Business.storeTaxFilingCaseRestParams(), aPeonyTaxFilingCase.getTaxFilingCase());
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayErrorMessageDialog("Failed to save extension date because of technical error. " + getMessage());
            }

            @Override
            protected void succeeded() {
                try {
                    get();
                    PeonyFaceUtils.publishMessageOntoOutputWindow(getMessage());
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(saveDeadlineExtensionTask);
    }

    protected void saveTaxFilingStatusDate(final PeonyTaxFilingCase aPeonyTaxFilingCase, final Date newDate, final GardenTaxFilingStatus gardenTaxFilingStatus) {
        Task<G02TaxFilingCase> saveG02TaxFilingCaseTask = new Task<G02TaxFilingCase>(){
            @Override
            protected G02TaxFilingCase call() throws Exception {
                Date oldDate = null;
                switch (gardenTaxFilingStatus){
                    case RECEIVED:
                        oldDate = aPeonyTaxFilingCase.getReceivedDate();
                        aPeonyTaxFilingCase.setReceivedDate(newDate);
                        break;
                    case PREPARED:
                        oldDate = aPeonyTaxFilingCase.getPreparedDate();
                        aPeonyTaxFilingCase.setPreparedDate(newDate);
                        break;
                    case COMPLETED:
                        oldDate = aPeonyTaxFilingCase.getCompletedDate();
                        aPeonyTaxFilingCase.setCompletedDate(newDate);
                        break;
                    case EXT_EFILE:
                        oldDate = aPeonyTaxFilingCase.getExtensionEFiledDate();
                        aPeonyTaxFilingCase.setExtensionEFiledDate(newDate);
                        break;
                    case PICKUP:
                        oldDate = aPeonyTaxFilingCase.getEFiledDate();
                        aPeonyTaxFilingCase.setEFiledDate(newDate);
                        break;
                }
                
                G02Log log = new G02Log();
                log.setLogUuid(GardenData.generateUUIDString());
                if (newDate == null){
                    log.setLogName(PeonyLogName.DELETED_TAX_FILING_STATUS.name());
                    updateMessage("Successfully delete " + gardenTaxFilingStatus.value() + " date.");
                }else{
                    log.setLogName(PeonyLogName.UPDATE_TAX_FILING_STATUS.name());
                    updateMessage("Successfully save " + gardenTaxFilingStatus.value() + " date.");
                }
                log.setLogMessage(gardenTaxFilingStatus.name() + " - New date: "
                        + ZcaCalendar.convertToMMddyyyy(newDate, "-") 
                        + "; Old date: " + ZcaCalendar.convertToMMddyyyy(oldDate, "-"));
                log.setOperatorAccountUuid(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
                log.setCreated(new Date());
                log.setLoggedEntityType(GardenEntityType.TAX_FILING_CASE.name());
                log.setLoggedEntityUuid(aPeonyTaxFilingCase.getTaxFilingCase().getTaxFilingUuid());
                log.setEntityType(GardenEntityType.convertEnumNameToType(aPeonyTaxFilingCase.getTaxFilingCase().getEntityType()).name());
                log.setEntityUuid(aPeonyTaxFilingCase.getTaxFilingCase().getEntityUuid());
                log.setEntityDescription(null);
                //save log
                Lookup.getDefault().lookup(PeonySecurityService.class).log(log);
                
                return Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                                .storeEntity_XML(G02TaxFilingCase.class, GardenRestParams.Business.storeTaxFilingCaseRestParams(), aPeonyTaxFilingCase.getTaxFilingCase());
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayErrorMessageDialog("Failed to save this date because of technical error. " + getMessage());
            }

            @Override
            protected void succeeded() {
                try {
                    get();
                    PeonyFaceUtils.publishMessageOntoOutputWindow(getMessage());
                } catch (InterruptedException | ExecutionException ex) {
                    PeonyFaceUtils.displayErrorMessageDialog("Cannot save this date because of technical error. " + ex.getMessage());
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(saveG02TaxFilingCaseTask);
    }

    public void resetDataEntry() {
        if (Platform.isFxApplicationThread()){
            resetDataEntryHelper();
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    resetDataEntryHelper();
                }
            });
        }
    }
    protected void resetDataEntryHelper(){
        //todo: define how to reset data entry here
    }
    
    public void resetDataEntryStyle() {
        if (Platform.isFxApplicationThread()){
            resetDataEntryStyleHelper();
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    resetDataEntryStyleHelper();
                }
            });
        }
    }
    
    protected void resetDataEntryStyleHelper(){
        //todo: define how to reset data entry's style here
    }
}
