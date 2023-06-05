/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zcomapproach.garden.rose.scheduler;

import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.garden.data.constant.TaxpayerEntityDateField;
import com.zcomapproach.garden.data.constant.UState;
import com.zcomapproach.garden.persistence.entity.G02Log;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerCaseSearchCache;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerCaseStatusCache;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.persistence.peony.PeonyEmployeeList;
import com.zcomapproach.garden.persistence.peony.data.TaxpayerCaseSearchResult;
import com.zcomapproach.garden.persistence.peony.data.TaxpayerCaseSearchResultList;
import com.zcomapproach.garden.rose.persistence.RoseManagementEJB02;
import com.zcomapproach.garden.rose.persistence.RoseTaxpayerEJB02;
import com.zcomapproach.garden.taxation.TaxationSettings;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Startup;
import javax.ejb.Stateless;

/**
 *
 * @author zhijun98
 */
@Startup
@Stateless
@DependsOn("GardenCacheNonPersistentSchedulerLocker")
public class GardenCacheNonPersistentScheduler {
    @EJB
    private GardenCacheNonPersistentSchedulerLocker locker;
    
    @EJB
    private RoseTaxpayerEJB02 taxpayerEJB;
    
    @EJB
    private RoseManagementEJB02 managementEJB;

    @PostConstruct
    public void postConstruct() {
        
    }
    
    @Schedule(//second="*/1",
              minute = "*/5",
              hour = "*",
              info = "cacheTaxpayerCaseSearchResult",
              persistent = false)
    public void cacheTaxpayerCaseSearchResult() {
        if (locker.lockTaxpayerCaseSearchCache()){
            Logger.getLogger(GardenCacheNonPersistentScheduler.class.getName()).log(Level.INFO, ">>> start cacheTaxpayerCaseSearchResult....{0}", ZcaCalendar.convertToMMddyyyyHHmmss(new Date(), "-", "@", ":"));
            /**
             * if today is before the "taxation-deadline plus 15 days", then use taxation-deadline plus 15 days; otherwise, use "today"
             */
            Date toDeadlineDate = ZcaCalendar.addDates(TaxationSettings.getSingleton().getComingPersonalFamlityTaxReturnDeadline(UState.NY).getTime(), 15);
            Date today = new Date();
            if (today.after(toDeadlineDate)){
                toDeadlineDate = today;
            }
            Date fromDeadlineDate = ZcaCalendar.addDates(toDeadlineDate, -366);
            Logger.getLogger(GardenCacheNonPersistentScheduler.class.getName()).log(Level.INFO, "--- cacheTaxpayerCaseSearchResultHelper from {0} to {1}....", 
                    new Object[]{ZcaCalendar.convertToMMddyyyy(fromDeadlineDate, "-"), ZcaCalendar.convertToMMddyyyy(toDeadlineDate, "-")});
            cacheTaxpayerCaseSearchResultHelper(fromDeadlineDate, toDeadlineDate);
            Logger.getLogger(GardenCacheNonPersistentScheduler.class.getName()).log(Level.INFO, "--- complete cacheTaxpayerCaseSearchResult....{0}", ZcaCalendar.convertToMMddyyyyHHmmss(new Date(), "-", "@", ":"));
            locker.unlockTaxpayerCaseSearchCache();
        }else{
            Logger.getLogger(GardenCacheNonPersistentScheduler.class.getName()).log(Level.INFO, "--- skip cacheTaxpayerCaseSearchResult because of lock ....{0}", ZcaCalendar.convertToMMddyyyyHHmmss(new Date(), "-", "@", ":"));
        }
    }
        
////    @Schedule(second = "0",
////              minute = "30",
////              hour = "3",
////              dayOfWeek="*",
////              info = "cachePreviousYearTaxpayerCaseSearchResult",
////              persistent = false)
////    public void cachePreviousYearTaxpayerCaseSearchResult() {
////        if (locker.lockPreviousYearTaxpayerCaseSearchCache()){
////            Logger.getLogger(GardenCacheNonPersistentScheduler.class.getName()).log(Level.INFO, ">>> start cachePreviousYearTaxpayerCaseSearchResult....{0}", ZcaCalendar.convertToMMddyyyyHHmmss(new Date(), "-", "@", ":"));
////            /**
////             * if today is before the "taxation-deadline plus 15 days", then use taxation-deadline plus 15 days; otherwise, use "today"
////             */
////            Date toDeadlineDate = ZcaCalendar.addDates(TaxationSettings.getSingleton().getComingPersonalFamlityTaxReturnDeadline(UState.NY).getTime(), 15);
////            Date today = new Date();
////            if (today.after(toDeadlineDate)){
////                toDeadlineDate = today;
////            }
////            Date fromDeadlineDate = ZcaCalendar.addDates(toDeadlineDate, -366);
////            //push back one more year
////            toDeadlineDate = ZcaCalendar.addDates(toDeadlineDate, -366);
////            fromDeadlineDate = ZcaCalendar.addDates(fromDeadlineDate, -366);
////            Logger.getLogger(GardenCacheNonPersistentScheduler.class.getName()).log(Level.INFO, "--- cacheTaxpayerCaseSearchResultHelper from {0} to {1}....", 
////                    new Object[]{ZcaCalendar.convertToMMddyyyy(fromDeadlineDate, "-"), ZcaCalendar.convertToMMddyyyy(toDeadlineDate, "-")});
////            cacheTaxpayerCaseSearchResultHelper(fromDeadlineDate, toDeadlineDate);
////            Logger.getLogger(GardenCacheNonPersistentScheduler.class.getName()).log(Level.INFO, "--- complete cachePreviousYearTaxpayerCaseSearchResult....{0}", ZcaCalendar.convertToMMddyyyyHHmmss(new Date(), "-", "@", ":"));
////            locker.unlockPreviousYearTaxpayerCaseSearchCache();
////        }else{
////            Logger.getLogger(GardenCacheNonPersistentScheduler.class.getName()).log(Level.INFO, "--- skip cachePreviousYearTaxpayerCaseSearchResult because of lock ....{0}", ZcaCalendar.convertToMMddyyyyHHmmss(new Date(), "-", "@", ":"));
////        }
////    }

    private void cacheTaxpayerCaseSearchResultHelper(Date fromDeadlineDate, Date toDeadlineDate) {
        //get the taxpayer cases
        TaxpayerCaseSearchResultList taxpayerCaseSearchResultList = taxpayerEJB.searchTaxpayerCaseSearchResultListByDateRangeForCache(TaxpayerEntityDateField.DEADLINE.name(), fromDeadlineDate, toDeadlineDate);
        //get employees
        HashMap<String, PeonyEmployee> peonyEmployeeNameSet = new HashMap<>();
        PeonyEmployeeList peonyEmployeeList = managementEJB.findPeonyEmployeeList();
        if (peonyEmployeeList != null){
            List<PeonyEmployee> aPeonyEmployeeList = peonyEmployeeList.getPeonyEmployeeList();
            for (PeonyEmployee aPeonyEmployee : aPeonyEmployeeList){
                peonyEmployeeNameSet.put(aPeonyEmployee.getEmployeeInfo().getEmployeeAccountUuid(), aPeonyEmployee);
            }
        }
        if (taxpayerCaseSearchResultList != null){
            List<TaxpayerCaseSearchResult> aTaxpayerCaseSearchResultList = taxpayerCaseSearchResultList.getTaxpayerCaseSearchResultList();
            if (aTaxpayerCaseSearchResultList != null){
                List<G02TaxpayerCaseSearchCache> aG02TaxpayerCaseSearchCacheList = new ArrayList<>();
                List<G02TaxpayerCaseStatusCache> aG02TaxpayerCaseStatusCacheList = new ArrayList<>();
                G02TaxpayerCaseSearchCache aG02TaxpayerCaseSearchCache;
                G02TaxpayerCaseStatusCache aG02TaxpayerCaseStatusCache;
                G02Log latestLog;
                for (TaxpayerCaseSearchResult aTaxpayerCaseSearchResult : aTaxpayerCaseSearchResultList){
                    aTaxpayerCaseSearchResult.setPeonyEmployeeSet(peonyEmployeeNameSet);    //plugin employees
                    //G02TaxpayerCaseSearchCache...
                    aG02TaxpayerCaseSearchCache = new G02TaxpayerCaseSearchCache();
                    aG02TaxpayerCaseSearchCache.setBalanceTotalText(aTaxpayerCaseSearchResult.getBalanceTotalText());
                    aG02TaxpayerCaseSearchCache.setConfirmedDepositedText(aTaxpayerCaseSearchResult.getConfirmedDepositedText());
                    aG02TaxpayerCaseSearchCache.setContactInfo(aTaxpayerCaseSearchResult.getContactInfo());
                    aG02TaxpayerCaseSearchCache.setCreated(new Date());
                    aG02TaxpayerCaseSearchCache.setDeadline(aTaxpayerCaseSearchResult.getDeadline());
                    aG02TaxpayerCaseSearchCache.setExtension(aTaxpayerCaseSearchResult.getExtension());
                    aG02TaxpayerCaseSearchCache.setFederalFilingStatus(aTaxpayerCaseSearchResult.getFederalFilingStatus());
                    aG02TaxpayerCaseSearchCache.setLatestTaxpayerStatus(aTaxpayerCaseSearchResult.getLatestTaxpayerStatus());
                    latestLog = aTaxpayerCaseSearchResult.getLatestTaxpayerCaseStatus();
                    if (latestLog != null){
                        if (peonyEmployeeNameSet.get(latestLog.getOperatorAccountUuid()) == null){
                            aG02TaxpayerCaseSearchCache.setLatestTaxpayerStatusOperatorName("Ex-employee");
                        }else{
                            aG02TaxpayerCaseSearchCache.setLatestTaxpayerStatusOperatorName(peonyEmployeeNameSet.get(latestLog.getOperatorAccountUuid()).getPeonyUserFullName());
                        }
                    }
                    aG02TaxpayerCaseSearchCache.setLatestTaxpayerStatusTimestamp(aTaxpayerCaseSearchResult.getLatestTaxpayerStatusTimestamp());
                    aG02TaxpayerCaseSearchCache.setPrimaryTaxpayerSsn(aTaxpayerCaseSearchResult.getPrimaryTaxpayerSsn());
                    aG02TaxpayerCaseSearchCache.setSpouseName(aTaxpayerCaseSearchResult.getSpouseName());
                    aG02TaxpayerCaseSearchCache.setTaxpayerCaseUuid(aTaxpayerCaseSearchResult.getTaxpayerCaseUuid());
                    aG02TaxpayerCaseSearchCache.setTaxpayerName(aTaxpayerCaseSearchResult.getTaxpayerName());
                    aG02TaxpayerCaseSearchCacheList.add(aG02TaxpayerCaseSearchCache);
                    
                    //G02TaxpayerCaseStatusCache...
                    aG02TaxpayerCaseStatusCache = new G02TaxpayerCaseStatusCache();
                    aG02TaxpayerCaseStatusCache.setTaxpayerCaseUuid(aTaxpayerCaseSearchResult.getTaxpayerCaseUuid());
                    aG02TaxpayerCaseStatusCache.setDeadline(aTaxpayerCaseSearchResult.getDeadline());
                    aG02TaxpayerCaseStatusCache.setExtension(aTaxpayerCaseSearchResult.getExtension());
                    aG02TaxpayerCaseStatusCache.setPrimaryTaxpayerSsn(aTaxpayerCaseSearchResult.getPrimaryTaxpayerSsn());
                    aG02TaxpayerCaseStatusCache.setTaxpayerName(aTaxpayerCaseSearchResult.getTaxpayerName());
                    aG02TaxpayerCaseStatusCache.setSpouseName(aTaxpayerCaseSearchResult.getSpouseName());
                    aG02TaxpayerCaseStatusCache.setFederalFilingStatus(aTaxpayerCaseSearchResult.getFederalFilingStatus());
                    aG02TaxpayerCaseStatusCache.setBalanceTotalText(aTaxpayerCaseSearchResult.getBalanceTotalText());
                    aG02TaxpayerCaseStatusCache.setContactInfo(aTaxpayerCaseSearchResult.getContactInfo());
                    aG02TaxpayerCaseStatusCache.setTaxpayerResidencyMemo(aTaxpayerCaseSearchResult.getTaxpayerResidencyMemo());
                    //aG02TaxpayerCaseStatusCache.setTaxpayerCaseMemoHistory(aTaxpayerCaseSearchResult.getTaxpayerCaseMemoHistory());
                    aG02TaxpayerCaseStatusCache.setTaxpayerCaseMemoHistory(aTaxpayerCaseSearchResult.getTaxpayerCaseLatestMemo());
                    aG02TaxpayerCaseStatusCache.setCpaApprovedStatus(aTaxpayerCaseSearchResult.getCpaApprovedStatus());
                    aG02TaxpayerCaseStatusCache.setCustomerSignatureStatus(aTaxpayerCaseSearchResult.getCustomerSignatureStatus());
                    aG02TaxpayerCaseStatusCache.setEfileStatus(aTaxpayerCaseSearchResult.getEfileStatus());
                    aG02TaxpayerCaseStatusCache.setManangerReviewStatus(aTaxpayerCaseSearchResult.getCpaApprovedExtensionStatus());
                    aG02TaxpayerCaseStatusCache.setNotifySignatureAndPaymentStatus(aTaxpayerCaseSearchResult.getNotifySignatureAndPaymentStatus());
                    aG02TaxpayerCaseStatusCache.setPickupByCustomerStatus(aTaxpayerCaseSearchResult.getPickupByCustomerStatus());
                    aG02TaxpayerCaseStatusCache.setPrepareTaxMaterialStatus(aTaxpayerCaseSearchResult.getPrepareTaxMaterialStatus());
                    aG02TaxpayerCaseStatusCache.setReceiveTaxpayerCaseStatus(aTaxpayerCaseSearchResult.getReceiveTaxpayerCaseStatus());
                    aG02TaxpayerCaseStatusCache.setTaxpayerCaseScanStatus(aTaxpayerCaseSearchResult.getTaxpayerCaseScanStatus());
                    aG02TaxpayerCaseStatusCacheList.add(aG02TaxpayerCaseStatusCache);
                    
                }//for-loop
                
                taxpayerEJB.storeG02TaxpayerCaseSearchAndStatusCacheList(aG02TaxpayerCaseSearchCacheList, aG02TaxpayerCaseStatusCacheList);
            }//if
        }//if
    }
}
