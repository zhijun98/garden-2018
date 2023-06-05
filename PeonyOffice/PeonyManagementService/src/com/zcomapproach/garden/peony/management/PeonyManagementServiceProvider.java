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

package com.zcomapproach.garden.peony.management;

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.kernel.PeonyServiceProvider;
import com.zcomapproach.garden.peony.kernel.data.PeonySmsContactor;
import com.zcomapproach.garden.peony.kernel.rest.PeonyRestClient;
import com.zcomapproach.garden.peony.kernel.services.PeonyBusinessService;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import com.zcomapproach.garden.peony.management.dialogs.DailyReportHistoryDialog;
import com.zcomapproach.garden.peony.management.dialogs.PeonyDailyReportEditorDialog;
import com.zcomapproach.garden.peony.utils.PeonyDataUtils;
import com.zcomapproach.garden.peony.view.dialogs.PeonySmsDialog;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.dialogs.MemoDataEntryDialog;
import com.zcomapproach.garden.peony.view.dialogs.PeonyPaymentDialog;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.constant.GardenContactType;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G02BusinessContactor;
import com.zcomapproach.garden.persistence.entity.G02Memo;
import com.zcomapproach.garden.persistence.entity.G02Payment;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerInfo;
import com.zcomapproach.garden.persistence.peony.PeonyDailyReport;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.persistence.peony.PeonyEmployeeList;
import com.zcomapproach.garden.persistence.peony.PeonyLog;
import com.zcomapproach.garden.persistence.peony.PeonyMemo;
import com.zcomapproach.garden.persistence.peony.PeonyPayment;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.garden.rest.GardenWebResoureRoot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author zhijun98
 */
@ServiceProvider(service = PeonyManagementService.class)
public class PeonyManagementServiceProvider extends PeonyServiceProvider implements PeonyManagementService{
    
    private HashMap<String, PeonyEmployee> peonyEmployeeSet;

    private final PeonyRestClient peonyManagementRestClient;

    public PeonyManagementServiceProvider() {
        this.peonyManagementRestClient = new PeonyRestClient(GardenWebResoureRoot.MANAGEMENT);
    }

    @Override
    public PeonyRestClient getPeonyManagementRestClient() {
        return peonyManagementRestClient;
    }
    
    @Override
    public void popupSmsDialogForBusinessContactor(final G02BusinessContactor contactor) {
        List<G02BusinessContactor> contactors = new ArrayList<>();
        contactors.add(contactor);
        popupSmsDialogForBusinessContactors(contactors);
    }

    @Override
    public void popupSmsDialogForBusinessContactors(List<G02BusinessContactor> contactors) {
        if ((contactors == null) || (contactors.isEmpty())){
            return;
        }
        List<PeonySmsContactor> aPeonySmsContactorList = new ArrayList<>();
        PeonySmsContactor aPeonySmsContactor;
        for (G02BusinessContactor contactor : contactors){
            if (GardenContactType.MOBILE_PHONE.value().equalsIgnoreCase(contactor.getContactType())){
                aPeonySmsContactor = new PeonySmsContactor();
                aPeonySmsContactor.setContactorName(contactor.getLastName() + "," + contactor.getFirstName());
                aPeonySmsContactor.setEntityType(contactor.getEntityType());
                aPeonySmsContactor.setEntityUuid(contactor.getEntityUuid());
                aPeonySmsContactor.setMobileNumber(contactor.getContactInfo());
                
                aPeonySmsContactorList.add(aPeonySmsContactor);
            }
        }
        popupSmsDialog(aPeonySmsContactorList);
    }
    
    @Override
    public void popupSmsDialogForTaxpayers(List<G02TaxpayerInfo> contactors) {
        if ((contactors == null) || (contactors.isEmpty())){
            return;
        }
        popupSmsDialog(PeonyDataUtils.retrievePeonySmsContactorListFromTaxpayerInfoList(contactors));
    }

    private void popupSmsDialog(final List<PeonySmsContactor> aPeonySmsContactorList) {
        if (SwingUtilities.isEventDispatchThread()){
            popupSmsDialogHelper(aPeonySmsContactorList);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    popupSmsDialogHelper(aPeonySmsContactorList);
                }
            });
        }
    }

    private void popupSmsDialogHelper(List<PeonySmsContactor> aPeonySmsContactorList) {
        if ((aPeonySmsContactorList == null) || (aPeonySmsContactorList.isEmpty())){
            PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, "No contactor(s) qualified for SMS.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        PeonySmsDialog aPeonySmsDialog = new PeonySmsDialog(null, true);
        aPeonySmsDialog.launchPeonySmsDialog("Send SMS", aPeonySmsContactorList);
    }

    @Override
    public void displayMyDailyReportTopComponent() {
        if (SwingUtilities.isEventDispatchThread()){
            displayMyDailyReportTopComponentHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    displayMyDailyReportTopComponentHelper();
                }
            });
        }
    }
    
    private void displayMyDailyReportTopComponentHelper() {
        //PeonyFaceUtils.openExistingTopComponentSingleton("MyDailyReportTopComponent");
        Lookup.getDefault().lookup(PeonyManagementService.class).refreshMyDailyReport();
    }

    @Override
    public void displayEmployeeDataEntryTopComponent(final PeonyEmployee aPeonyEmployee) {
        if (SwingUtilities.isEventDispatchThread()){
            displayEmployeeDataEntryTopComponentHelper(aPeonyEmployee);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    displayEmployeeDataEntryTopComponentHelper(aPeonyEmployee);
                }
            });
        }
    }
    
    private void displayEmployeeDataEntryTopComponentHelper(PeonyEmployee aPeonyEmployee) {
        TopComponent aTopComponent = PeonyFaceUtils.getExistingTopComponentSingleton("PeonyManagementTopComponent");
        if (aTopComponent instanceof PeonyManagementTopComponent){
            ((PeonyManagementTopComponent)aTopComponent).displayEmployeeDataEntryTopComponent(aPeonyEmployee);
        }else{
            PeonyFaceUtils.publishMessageOntoOutputWindow("[TECH] Cannot open Peony employee profile window: " + aPeonyEmployee.getEmployeeInfo().getEmployeeAccountUuid());
        }
    }

    @Override
    public void displayEmployeeWorkLogsTopComponent(final PeonyEmployee aPeonyEmployee) {
        if (SwingUtilities.isEventDispatchThread()){
            displayEmployeeWorkLogsTopComponentHelper(aPeonyEmployee);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    displayEmployeeWorkLogsTopComponentHelper(aPeonyEmployee);
                }
            });
        }
    }
    
    private void displayEmployeeWorkLogsTopComponentHelper(final PeonyEmployee aPeonyEmployee) {
        EmployeeWorkLogsTopComponent aEmployeeWorkLogsTopComponent = new EmployeeWorkLogsTopComponent();
        aEmployeeWorkLogsTopComponent.launchEmployeeWorkLogsTopComponent(aPeonyEmployee);
        aEmployeeWorkLogsTopComponent.open();
        aEmployeeWorkLogsTopComponent.requestActive();
    }

    private void refreshPeonyEmployeeSet(){
        peonyEmployeeSet = new HashMap<>();
        try{
            PeonyEmployeeList peonyEmployeeList = getPeonyManagementRestClient().findEntity_XML(PeonyEmployeeList.class, 
                    GardenRestParams.Management.findPeonyEmployeeListRestParams());
            if (peonyEmployeeList != null){
                List<PeonyEmployee> aPeonyEmployeeList = peonyEmployeeList.getPeonyEmployeeList();
                for (PeonyEmployee aPeonyEmployee : aPeonyEmployeeList){
                    peonyEmployeeSet.put(aPeonyEmployee.getEmployeeInfo().getEmployeeAccountUuid(), aPeonyEmployee);
                }
            }
        }catch (Exception ex){
            peonyEmployeeSet = new HashMap<>();
        }
    }
    
    @Override
    public HashMap<String, PeonyEmployee> retrievePeonyEmployeeSet(){
        if ((peonyEmployeeSet == null) || (peonyEmployeeSet.isEmpty())){
            refreshPeonyEmployeeSet();
        }
        return peonyEmployeeSet;
    }
    @Override
    public List<PeonyEmployee> retrievePeonyEmployees(){
        if ((peonyEmployeeSet == null) || (peonyEmployeeSet.isEmpty())){
            refreshPeonyEmployeeSet();
        }
        return new ArrayList<>(peonyEmployeeSet.values());
    }

    @Override
    public PeonyEmployeeList retrievePeonyEmployeeList() {
        return new PeonyEmployeeList(retrievePeonyEmployees());
    }

    @Override
    public PeonyEmployee retrievePeonyEmployee(String employeeUuid) {
        if ((peonyEmployeeSet == null) || (peonyEmployeeSet.isEmpty())){
            refreshPeonyEmployeeSet();
        }
        return peonyEmployeeSet.get(employeeUuid);
    }

    @Override
    public String retrievePeonyEmployeeFullName(String employeeUuid) {
        PeonyEmployee employee = retrievePeonyEmployee(employeeUuid);
        if (employee == null){
            return "";
        }else{
            return employee.getPeonyUserFullName();
        }
    }

    @Override
    public String retrieveXmppAccountFullName(String xmppLoginName) {
        if (ZcaValidator.isNullEmpty(xmppLoginName)){
            return "";
        }
        List<PeonyEmployee> aPeonyEmployeeList = retrievePeonyEmployees();
        for (PeonyEmployee aPeonyEmployee : aPeonyEmployeeList){
            if (aPeonyEmployee.getXmppAccount() != null){
                if (xmppLoginName.equalsIgnoreCase(aPeonyEmployee.getXmppAccount().getLoginName())){
                    return aPeonyEmployee.getPeonyUserFullName();
                }
            }
        }
        return "";
    }

    @Override
    public void launchEntityOwnerWindow(PeonyLog aPeonyLog) {
        if ((aPeonyLog == null) || (ZcaValidator.isNullEmpty(aPeonyLog.getLog().getEntityUuid())) 
                || (GardenEntityType.convertEnumNameToType(aPeonyLog.getLog().getEntityType()).equals(GardenEntityType.UNKNOWN)))
        {
            PeonyFaceUtils.displayErrorMessageDialog("The entity information is not sufficient to support displaying its details.");
            return;
        }
        launchEntityOwnerWindowHelper(aPeonyLog);
    }

    private void launchEntityOwnerWindowHelper(PeonyLog aPeonyLog) {
        GardenEntityType entityType = GardenEntityType.convertEnumNameToType(aPeonyLog.getLog().getEntityType()); 
        String entityUuid = aPeonyLog.getLog().getEntityUuid();
        switch (entityType){
            case TAXPAYER_CASE:
                Lookup.getDefault().lookup(PeonyTaxpayerService.class).launchPeonyTaxpayerCaseTopComponentByTaxpayerCaseUuid(entityUuid);
                break;
            default:
                PeonyFaceUtils.displayInformationMessageDialog("Cannot display the related entity details involved in this log.");
        }
    }

    @Override
    public void launchLoggedEntityWindow(PeonyLog aPeonyLog) {
        if ((aPeonyLog == null) || (ZcaValidator.isNullEmpty(aPeonyLog.getLog().getLoggedEntityUuid())) 
                || (GardenEntityType.convertEnumNameToType(aPeonyLog.getLog().getLoggedEntityType()).equals(GardenEntityType.UNKNOWN)))
        {
            PeonyFaceUtils.displayErrorMessageDialog("The logged entity information is not sufficient to support displaying its details.");
            return;
        }
        launchLoggedEntityWindowHelper(aPeonyLog);
    }

    private void launchLoggedEntityWindowHelper(PeonyLog aPeonyLog) {
        GardenEntityType loggedEntityType = GardenEntityType.convertEnumNameToType(aPeonyLog.getLog().getLoggedEntityType()); 
        String loggedEntityUuid = aPeonyLog.getLog().getLoggedEntityUuid();
        switch (loggedEntityType){
            case PAYMENT:
                getExecutorCachedService().submit(new Runnable(){
                    @Override
                    public void run() {
                        launchWindowForPaymentHelper(loggedEntityUuid);
                    }
                });
                break;
            case MEMO:
                getExecutorCachedService().submit(new Runnable(){
                    @Override
                    public void run() {
                        launchWindowForMemoHelper(loggedEntityUuid);
                    }
                });
                break;
            case BILL:
                launchEntityOwnerWindowHelper(aPeonyLog);   //Bill's detail-panel is a part of its owner's window, e.g. taxpayer-case
                break;
            default:
                PeonyFaceUtils.displayInformationMessageDialog("Cannot display the related entity details involved in this log.");
        }
    }

    private void launchWindowForMemoHelper(String memoUuid) {
        try {
            G02Memo aG02Memo = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                    .findEntity_XML(G02Memo.class, GardenRestParams.Business.findMemoEntityRestParams(memoUuid));
            if (aG02Memo == null){
                throw new Exception("No memo record in the database.");
            }else{
                PeonyMemo aPeonyMemo = new PeonyMemo();
                aPeonyMemo.setMemo(aG02Memo);
                MemoDataEntryDialog aMemoDataEntryDialog = new MemoDataEntryDialog(null, true);
                aMemoDataEntryDialog.launchMemoDataEntryDialog("Memo: ", aPeonyMemo, false, false, true, null);
            }
        } catch (Exception ex) {
            //Exceptions.printStackTrace(ex);
            PeonyFaceUtils.displayInformationMessageDialog("No related entity information available for this log." + " " + ex.getMessage());
        }
    }

    private void launchWindowForPaymentHelper(String paymentUuid) {
        try {
            G02Payment aG02Payment = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                    .findEntity_XML(G02Payment.class, GardenRestParams.Business.findPaymentEntityRestParams(paymentUuid));
            if (aG02Payment == null){
                throw new Exception("No payment record in the database.");
            }else{
                PeonyPayment aPeonyPayment = new PeonyPayment();
                aPeonyPayment.setPayment(aG02Payment);
                PeonyPaymentDialog aPaymentDataEntryDialog = new PeonyPaymentDialog(null, true);
                aPaymentDataEntryDialog.launchPeonyPaymentDialog("Payment: ", aPeonyPayment, false, null);
            }
        } catch (Exception ex) {
            //Exceptions.printStackTrace(ex);
            PeonyFaceUtils.displayInformationMessageDialog("No related entity information available for this log." + " " + ex.getMessage());
        }
    }

    @Override
    public void launchEmployeeDailyReportTopComponent(final PeonyEmployee targetPeonyEmployee) {
        if (targetPeonyEmployee == null){
            PeonyFaceUtils.displayErrorMessageDialog("Cannot find the employee profile!");
            return;
        }
        if (SwingUtilities.isEventDispatchThread()){
            launchEmployeeDailyReportTopComponentHelper(targetPeonyEmployee);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    launchEmployeeDailyReportTopComponentHelper(targetPeonyEmployee);
                }
            });
        }
    }
    
    private void launchEmployeeDailyReportTopComponentHelper(final PeonyEmployee targetPeonyEmployee) {
        EmployeeDailyReportTopComponent aEmployeeDailyReportTopComponent = new EmployeeDailyReportTopComponent();
        aEmployeeDailyReportTopComponent.launchEmployeeDailyReportTopComponent(targetPeonyEmployee);
    }

    @Override
    public void refreshMyDailyReport() {
        if (SwingUtilities.isEventDispatchThread()){
            refreshMyDailyReportHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    refreshMyDailyReportHelper();
                }
            });
        }
    }
    
    private void refreshMyDailyReportHelper(){
        try{
            TopComponent tc = WindowManager.getDefault().findTopComponent("MyDailyReportTopComponent");
            if (tc instanceof MyDailyReportTopComponent){
                ((MyDailyReportTopComponent)tc).refreshMyDailyReport();
            }
        }catch(Exception ex){
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void displayPeonyDailyReportEditor(final PeonyDailyReport aPeonyDailyReport, final List<PeonyFaceEventListener> listeners) {
        if (SwingUtilities.isEventDispatchThread()){
            displayPeonyDailyReportEditorHelper(aPeonyDailyReport, listeners);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    displayPeonyDailyReportEditorHelper(aPeonyDailyReport, listeners);
                }
            });
        }
    }

    private void displayPeonyDailyReportEditorHelper(PeonyDailyReport aPeonyDailyReport, List<PeonyFaceEventListener> listeners) {
        PeonyDailyReportEditorDialog aPeonyDailyReportEditorDialog = new PeonyDailyReportEditorDialog(PeonyLauncher.mainFrame, true);
        if (listeners != null){
            aPeonyDailyReportEditorDialog.addPeonyFaceEventListenerList(listeners);
        }
        aPeonyDailyReportEditorDialog.launchPeonyDailyReportEditorDialog("My Daily Report", aPeonyDailyReport);
    }

    @Override
    public void displayDailyReportHistoryByJobAssignmentUuid(final String jobAssignmentUuid) {
        if (SwingUtilities.isEventDispatchThread()){
            displayDailyReportHistoryByJobAssignmentUuidHelper(jobAssignmentUuid);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    displayDailyReportHistoryByJobAssignmentUuidHelper(jobAssignmentUuid);
                }
            });
        }
    }
    private void displayDailyReportHistoryByJobAssignmentUuidHelper(final String jobAssignmentUuid) {
        DailyReportHistoryDialog aPeonyDailyReportEditorDialog = new DailyReportHistoryDialog(PeonyLauncher.mainFrame, true);
        aPeonyDailyReportEditorDialog.launchPeonyDailyReportEditorDialog("Historical Daily Reports", jobAssignmentUuid);
    }

    @Override
    public void closeService() {
        
    }
}
