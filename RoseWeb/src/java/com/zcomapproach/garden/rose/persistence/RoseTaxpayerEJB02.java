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

package com.zcomapproach.garden.rose.persistence;

import com.zcomapproach.garden.data.constant.SearchTaxpayerCriteria;
import com.zcomapproach.garden.data.constant.TaxpayerEntityDateField;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.persistence.G02EntityValidator;
import com.zcomapproach.garden.persistence.GardenJpaUtils;
import static com.zcomapproach.garden.persistence.GardenJpaUtils.findById;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.constant.TaxFilingPeriod;
import com.zcomapproach.garden.persistence.constant.TaxFilingType;
import com.zcomapproach.garden.persistence.constant.TaxpayerRelationship;
import com.zcomapproach.garden.persistence.entity.G02Account;
import com.zcomapproach.garden.persistence.entity.G02Bill;
import com.zcomapproach.garden.persistence.entity.G02Location;
import com.zcomapproach.garden.persistence.entity.G02Log;
import com.zcomapproach.garden.persistence.entity.G02Payment;
import com.zcomapproach.garden.persistence.entity.G02PersonalBusinessProperty;
import com.zcomapproach.garden.persistence.entity.G02PersonalProperty;
import com.zcomapproach.garden.persistence.entity.G02TaxFilingCase;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerCase;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerCaseBk;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerInfo;
import com.zcomapproach.garden.persistence.entity.G02TlcLicense;
import com.zcomapproach.garden.persistence.entity.G02User;
import com.zcomapproach.garden.persistence.peony.PeonyAccount;
import com.zcomapproach.garden.persistence.peony.PeonyTaxFilingCase;
import com.zcomapproach.garden.persistence.peony.PeonyTaxFilingCaseList;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCaseList;
import com.zcomapproach.garden.persistence.peony.TaxpayerCaseBrief;
import com.zcomapproach.garden.persistence.peony.TaxpayerCaseBriefList;
import com.zcomapproach.garden.persistence.peony.data.TaxpayerCaseSearchResult;
import com.zcomapproach.garden.persistence.peony.data.TaxpayerCaseSearchResultList;
import com.zcomapproach.garden.persistence.updater.G02DataUpdaterFactory;
import com.zcomapproach.garden.taxation.TaxationSettings;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.persistence.constant.GardenTaxpayerCaseStatus;
import com.zcomapproach.garden.persistence.entity.G02DocumentTag;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerCaseSearchCache;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerCaseStatusCache;
import com.zcomapproach.garden.persistence.peony.data.PeonyLogName;
import com.zcomapproach.garden.persistence.peony.data.TaxpayerCaseSearchCacheResult;
import com.zcomapproach.garden.persistence.peony.data.TaxpayerCaseSearchCacheResultList;
import com.zcomapproach.garden.persistence.peony.data.TaxpayerCaseStatusCacheResult;
import com.zcomapproach.garden.persistence.peony.data.TaxpayerCaseStatusCacheResultList;
import com.zcomapproach.garden.persistence.query.GardenTaxpayerQuery;
import com.zcomapproach.garden.persistence.query.GardenTaxpayerQuery.G02TaxpayerCaseWithG02Log;
import com.zcomapproach.garden.util.GardenData;
import com.zcomapproach.garden.util.GardenSorter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 *
 * @author zhijun98
 */
@Stateless
public class RoseTaxpayerEJB02 extends AbstractDataServiceEJB02 {
    
    public PeonyTaxFilingCaseList generatePeonyTaxReturnFilingCaseListForTaxpayer(String taxpayerUuid)
            throws Exception
    {
    
        PeonyTaxFilingCaseList result = new PeonyTaxFilingCaseList();
        if (ZcaValidator.isNullEmpty(taxpayerUuid)){
            return result;
        }
        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();

            G02TaxpayerCase aG02TaxpayerCase = GardenJpaUtils.findById(em, G02TaxpayerCase.class, taxpayerUuid);
            if (aG02TaxpayerCase != null){
                //prepare for calculating the starting deadline
                List<G02TaxFilingCase> aG02TaxFilingCaseList = GardenJpaUtils.findTaxFilingCaseListByEntityUuid(em, taxpayerUuid);
                GregorianCalendar theLastDeadlineDate = new GregorianCalendar();
                theLastDeadlineDate.set(Calendar.MONTH, TaxationSettings.getSingleton().getTaxMonth_Taxpayer());
                theLastDeadlineDate.set(Calendar.DAY_OF_MONTH, TaxationSettings.getSingleton().getTaxDayOfMonth_Taxpayer());
                theLastDeadlineDate.add(Calendar.YEAR, -1);
                if ((aG02TaxFilingCaseList != null) && (!aG02TaxFilingCaseList.isEmpty())){
                    GardenSorter.sortTaxFilingCaseListByDeadline(aG02TaxFilingCaseList, true);
                    theLastDeadlineDate = ZcaCalendar.convertToGregorianCalendar(aG02TaxFilingCaseList.get(0).getDeadline());
                }
                //generate new deadlines for tax filing...
                List<Date> deadlineList = new ArrayList<>();
                GregorianCalendar aGregorianCalendar;
                for (int i = 0; i < 5; i++){
                    aGregorianCalendar = new GregorianCalendar();
                    theLastDeadlineDate.add(Calendar.YEAR, 1);
                    aGregorianCalendar.setTimeInMillis(theLastDeadlineDate.getTimeInMillis());
                    deadlineList.add(aGregorianCalendar.getTime());
                }
                //save new-generated tax filing cases...
                PeonyTaxFilingCase aPeonyTaxFilingCase;
                G02TaxFilingCase aG02TaxFilingCase;
                for (Date deadline : deadlineList){
                    aG02TaxFilingCase = new G02TaxFilingCase();
                    aG02TaxFilingCase.setCreated(new Date());
                    aG02TaxFilingCase.setDeadline(deadline);
                    //deprecated: aG02TaxFilingCase.setEntityStatus(null);
                    //deprecated: aG02TaxFilingCase.setEntityStatusTimestamp(null);
                    aG02TaxFilingCase.setEntityType(GardenEntityType.TAXCORP_CASE.name());
                    aG02TaxFilingCase.setEntityUuid(taxpayerUuid);
                    aG02TaxFilingCase.setTaxFilingPeriod(TaxFilingPeriod.YEARLY.value());
                    aG02TaxFilingCase.setTaxFilingType(TaxFilingType.TAX_RETURN.value());
                    aG02TaxFilingCase.setTaxFilingUuid(GardenData.generateUUIDString());
                    aG02TaxFilingCase.setUpdated(new Date());
                    //save....
                    GardenJpaUtils.storeEntity(em, G02TaxFilingCase.class, aG02TaxFilingCase, aG02TaxFilingCase.getTaxFilingUuid(), G02DataUpdaterFactory.getSingleton().getG02TaxFilingCaseUpdater());
                    aPeonyTaxFilingCase = new PeonyTaxFilingCase();
                    aPeonyTaxFilingCase.setTaxFilingCase(aG02TaxFilingCase);
                    result.getPeonyTaxFilingCaseList().add(aPeonyTaxFilingCase);
                }//for
            }
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(AbstractDataServiceEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(AbstractDataServiceEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return result;
    }
        
    public TaxpayerCaseSearchCacheResultList searchTaxpayerCaseSearchCacheResultListByFeatureFromCache(String taxpayerFeature, 
                                                                                                        String taxpayerFeatureValue, 
                                                                                                        boolean exactMatch) 
    {
        TaxpayerCaseSearchCacheResultList result = new TaxpayerCaseSearchCacheResultList();
        result.setTaxpayerFeature(taxpayerFeature);
        result.setTaxpayerFeatureValue(taxpayerFeatureValue);
        if (ZcaValidator.isNullEmpty(taxpayerFeature) || ZcaValidator.isNullEmpty(taxpayerFeatureValue)){
            return result;
        }
        EntityManager em = getEntityManager();
        try {
            List<G02TaxpayerInfo> aG02TaxpayerInfoList = GardenJpaUtils.findAll(em, G02TaxpayerInfo.class);
            if (aG02TaxpayerInfoList != null){
                TaxpayerCaseSearchCacheResult aTaxpayerCaseSearchCacheResult;
                String persistentTaxpayerFeatureValue;
                double score;
                G02TaxpayerCaseSearchCache aG02TaxpayerCaseSearchCache;
                String taxpayerCaseUuid;
                for (G02TaxpayerInfo aG02TaxpayerInfo : aG02TaxpayerInfoList){
                    taxpayerCaseUuid = aG02TaxpayerInfo.getTaxpayerCaseUuid();
                    persistentTaxpayerFeatureValue = retrievePersistentTaxpayerFeatureValue(taxpayerFeature, aG02TaxpayerInfo);
                    if (ZcaValidator.isNotNullEmpty(persistentTaxpayerFeatureValue)){
                        score = GardenData.calculateSimilarityByJaroWinklerStrategy(persistentTaxpayerFeatureValue, taxpayerFeatureValue, exactMatch, 0.75);
                        if (score > 0.8){
                            aG02TaxpayerCaseSearchCache = GardenJpaUtils.findById(em, G02TaxpayerCaseSearchCache.class, taxpayerCaseUuid);
                            if (aG02TaxpayerCaseSearchCache != null){
                                aTaxpayerCaseSearchCacheResult = new TaxpayerCaseSearchCacheResult();
                                aTaxpayerCaseSearchCacheResult.setTaxpayerCaseSearchCache(aG02TaxpayerCaseSearchCache);
                                result.getTaxpayerCaseSearchCacheResultList().add(aTaxpayerCaseSearchCacheResult);
                            }
                        }
                    }
                    
                }//for-loop
            }
        } finally {
            em.close();
        }
        return result;
    }
    
    public TaxpayerCaseSearchResultList searchTaxpayerCaseSearchResultListByFeature(String taxpayerFeature, 
                                                                                String taxpayerFeatureValue, 
                                                                                boolean exactMatch) 
    {
        TaxpayerCaseSearchResultList result = new TaxpayerCaseSearchResultList();
        result.setTaxpayerFeature(taxpayerFeature);
        result.setTaxpayerFeatureValue(taxpayerFeatureValue);
        if (ZcaValidator.isNullEmpty(taxpayerFeature) || ZcaValidator.isNullEmpty(taxpayerFeatureValue)){
            return result;
        }
        EntityManager em = getEntityManager();
        try {
            List<G02TaxpayerInfo> aG02TaxpayerInfoList = GardenJpaUtils.findAll(em, G02TaxpayerInfo.class);
            if (aG02TaxpayerInfoList != null){
                TaxpayerCaseSearchResult aTaxpayerCaseSearchResult;
                String persistentTaxpayerFeatureValue;
                double score;
                HashMap<String, Object> params = new HashMap<>();
                HashMap<String, G02TaxpayerCase> taxpayerCasesMap = new HashMap<>();
                G02TaxpayerCase aG02TaxpayerCase;
                String taxpayerCaseUuid;
                for (G02TaxpayerInfo aG02TaxpayerInfo : aG02TaxpayerInfoList){
                    taxpayerCaseUuid = aG02TaxpayerInfo.getTaxpayerCaseUuid();
                    aG02TaxpayerCase = taxpayerCasesMap.get(taxpayerCaseUuid);
                    if (aG02TaxpayerCase == null){
                        persistentTaxpayerFeatureValue = retrievePersistentTaxpayerFeatureValue(taxpayerFeature, aG02TaxpayerInfo);
                        if (ZcaValidator.isNotNullEmpty(persistentTaxpayerFeatureValue)){
                            score = GardenData.calculateSimilarityByJaroWinklerStrategy(persistentTaxpayerFeatureValue, taxpayerFeatureValue, exactMatch, 0.75);
                            if (score > 0.8){
                                aG02TaxpayerCase = GardenJpaUtils.findById(em, G02TaxpayerCase.class, aG02TaxpayerInfo.getTaxpayerCaseUuid());
                                if (aG02TaxpayerCase != null){
                                    taxpayerCasesMap.put(taxpayerCaseUuid, aG02TaxpayerCase);
                                    //one result
                                    aTaxpayerCaseSearchResult = new TaxpayerCaseSearchResult();
                                    aTaxpayerCaseSearchResult.setTaxpayerCase(aG02TaxpayerCase);
                                    params.put("taxpayerCaseUuid", aG02TaxpayerInfo.getTaxpayerCaseUuid());
                                    aTaxpayerCaseSearchResult.setTaxpayerInfoList(GardenJpaUtils.findEntityListByNamedQuery(em, G02TaxpayerInfo.class, "G02TaxpayerInfo.findByTaxpayerCaseUuid", params));
////                                    if (ZcaValidator.isNotNullEmpty(aG02TaxpayerCase.getLatestLogUuid())){
////                                        aTaxpayerCaseSearchResult.setLatestTaxpayerCaseStatus(findById(em, G02Log.class, aG02TaxpayerCase.getLatestLogUuid()));
////                                    }
                                    result.getTaxpayerCaseSearchResultList().add(aTaxpayerCaseSearchResult);
                                    
                                    
                                }
                            }
                        }
                    }
                    
                }//for-loop
            }
        } finally {
            em.close();
        }
        return result;
    }
    
    public TaxpayerCaseSearchResultList retrieveTaxpayerInfoListForTaxpayerCaseSearchResultList(TaxpayerCaseSearchResultList taxpayerCaseSearchResultList){
        if ((taxpayerCaseSearchResultList != null) && (!taxpayerCaseSearchResultList.getTaxpayerCaseSearchResultList().isEmpty())){
            EntityManager em = getEntityManager();
            try {
                List<TaxpayerCaseSearchResult> aTaxpayerCaseSearchResultList = taxpayerCaseSearchResultList.getTaxpayerCaseSearchResultList();
                HashMap<String, Object> params = new HashMap<>();
                for (TaxpayerCaseSearchResult aTaxpayerCaseSearchResult : aTaxpayerCaseSearchResultList){
                    //taxpayerInfoList
                    params.put("taxpayerCaseUuid", aTaxpayerCaseSearchResult.getTaxpayerCaseUuid());
                    aTaxpayerCaseSearchResult.setTaxpayerInfoList(GardenJpaUtils.findEntityListByNamedQuery(em, G02TaxpayerInfo.class, "G02TaxpayerInfo.findByTaxpayerCaseUuid", params));
                }
            } finally {
                em.close();
            }
        }
        return taxpayerCaseSearchResultList;
    }
    
    public TaxpayerCaseSearchResultList retrievePeonyBillPaymentListForTaxpayerCaseSearchResultList(TaxpayerCaseSearchResultList taxpayerCaseSearchResultList){
        if ((taxpayerCaseSearchResultList != null) && (!taxpayerCaseSearchResultList.getTaxpayerCaseSearchResultList().isEmpty())){
            EntityManager em = getEntityManager();
            try {
                List<TaxpayerCaseSearchResult> aTaxpayerCaseSearchResultList = taxpayerCaseSearchResultList.getTaxpayerCaseSearchResultList();
                for (TaxpayerCaseSearchResult aTaxpayerCaseSearchResult : aTaxpayerCaseSearchResultList){
                    //peonyBillPaymentList
                    aTaxpayerCaseSearchResult.setPeonyBillPaymentList(GardenJpaUtils.findPeonyBillPaymentListByEntityUuid(em, aTaxpayerCaseSearchResult.getTaxpayerCaseUuid()));
                }
            } finally {
                em.close();
            }
        }
        return taxpayerCaseSearchResultList;
    }
    
    public TaxpayerCaseSearchResultList retrieveWorkStatusLogListForTaxpayerCaseSearchResultList(TaxpayerCaseSearchResultList taxpayerCaseSearchResultList){
        if ((taxpayerCaseSearchResultList != null) && (!taxpayerCaseSearchResultList.getTaxpayerCaseSearchResultList().isEmpty())){
            EntityManager em = getEntityManager();
            try {
                List<TaxpayerCaseSearchResult> aTaxpayerCaseSearchResultList = taxpayerCaseSearchResultList.getTaxpayerCaseSearchResultList();
                for (TaxpayerCaseSearchResult aTaxpayerCaseSearchResult : aTaxpayerCaseSearchResultList){
                    //workStatusLogList
                    aTaxpayerCaseSearchResult.setWorkStatusLogList(GardenJpaUtils.findWorkStatusLogListByTaxpayerCaseUuuid(em, aTaxpayerCaseSearchResult.getTaxpayerCaseUuid()));
                }
            } finally {
                em.close();
            }
        }
        return taxpayerCaseSearchResultList;
    }
    
    public TaxpayerCaseSearchResultList retrieveTaxpayerMemoListForTaxpayerCaseSearchResultList(TaxpayerCaseSearchResultList taxpayerCaseSearchResultList){
        if ((taxpayerCaseSearchResultList != null) && (!taxpayerCaseSearchResultList.getTaxpayerCaseSearchResultList().isEmpty())){
            EntityManager em = getEntityManager();
            try {
                List<TaxpayerCaseSearchResult> aTaxpayerCaseSearchResultList = taxpayerCaseSearchResultList.getTaxpayerCaseSearchResultList();
                for (TaxpayerCaseSearchResult aTaxpayerCaseSearchResult : aTaxpayerCaseSearchResultList){
                    //taxpayerMemoList
                    aTaxpayerCaseSearchResult.setTaxpayerMemoList(GardenJpaUtils.findG02MemoListByEntityUuid(em, aTaxpayerCaseSearchResult.getTaxpayerCaseUuid()));
                }
            } finally {
                em.close();
            }
        }
        return taxpayerCaseSearchResultList;
    }
    
    public TaxpayerCaseSearchResult retrieveDataForTaxpayerCaseSearchResultByTaxpayerCaseUuid(String taxpayerCaseUuid){
        if (ZcaValidator.isNullEmpty(taxpayerCaseUuid)){
            return null;
        }
        TaxpayerCaseSearchResult aTaxpayerCaseSearchResult = new TaxpayerCaseSearchResult();
        aTaxpayerCaseSearchResult.getTaxpayerCase().setTaxpayerCaseUuid(taxpayerCaseUuid);
        EntityManager em = getEntityManager();
        try {
            //taxpayerInfoList
            HashMap<String, Object> params = new HashMap<>();
            params.put("taxpayerCaseUuid", taxpayerCaseUuid);
            aTaxpayerCaseSearchResult.setTaxpayerInfoList(GardenJpaUtils.findEntityListByNamedQuery(em, G02TaxpayerInfo.class, "G02TaxpayerInfo.findByTaxpayerCaseUuid", params));
            //peonyBillPaymentList
            aTaxpayerCaseSearchResult.setPeonyBillPaymentList(GardenJpaUtils.findPeonyBillPaymentListByEntityUuid(em, taxpayerCaseUuid));
            //workStatusLogList
            aTaxpayerCaseSearchResult.setWorkStatusLogList(GardenJpaUtils.findWorkStatusLogListByTaxpayerCaseUuuid(em, taxpayerCaseUuid));
            //taxpayerMemoList
            aTaxpayerCaseSearchResult.setTaxpayerMemoList(GardenJpaUtils.findG02MemoListByEntityUuid(em, taxpayerCaseUuid));
        } finally {
            em.close();
        }
        return aTaxpayerCaseSearchResult;
    }
    
    /**
     * @deprecated - performance
     * @param taxpayerCaseSearchResultList - if this list is big, the performance would be bad
     * @return 
     */
    public TaxpayerCaseSearchResultList retrieveDataForTaxpayerCaseSearchResultList(TaxpayerCaseSearchResultList taxpayerCaseSearchResultList){
        if ((taxpayerCaseSearchResultList != null) && (!taxpayerCaseSearchResultList.getTaxpayerCaseSearchResultList().isEmpty())){
            EntityManager em = getEntityManager();
            try {
                List<TaxpayerCaseSearchResult> aTaxpayerCaseSearchResultList = taxpayerCaseSearchResultList.getTaxpayerCaseSearchResultList();
                HashMap<String, Object> params = new HashMap<>();
                String taxpayerCaseUuid;
                for (TaxpayerCaseSearchResult aTaxpayerCaseSearchResult : aTaxpayerCaseSearchResultList){
                    taxpayerCaseUuid = aTaxpayerCaseSearchResult.getTaxpayerCaseUuid();
                    //taxpayerInfoList
                    params.put("taxpayerCaseUuid", aTaxpayerCaseSearchResult.getTaxpayerCaseUuid());
                    aTaxpayerCaseSearchResult.setTaxpayerInfoList(GardenJpaUtils.findEntityListByNamedQuery(em, G02TaxpayerInfo.class, "G02TaxpayerInfo.findByTaxpayerCaseUuid", params));
                    //peonyBillPaymentList
                    aTaxpayerCaseSearchResult.setPeonyBillPaymentList(GardenJpaUtils.findPeonyBillPaymentListByEntityUuid(em, taxpayerCaseUuid));
                    //workStatusLogList
                    aTaxpayerCaseSearchResult.setWorkStatusLogList(GardenJpaUtils.findWorkStatusLogListByTaxpayerCaseUuuid(em, taxpayerCaseUuid));
                    //taxpayerMemoList
                    aTaxpayerCaseSearchResult.setTaxpayerMemoList(GardenJpaUtils.findG02MemoListByEntityUuid(em, taxpayerCaseUuid));
                }
            } finally {
                em.close();
            }
        }
        return taxpayerCaseSearchResultList;
    }
    
    public TaxpayerCaseSearchResultList searchTaxpayerCaseSearchResultListByDateRangeAndLatestWorkStatus(String taxpayerDateFeature, Date fromDate, Date toDate, String workStatusForSearch) {
        TaxpayerCaseSearchResultList result = new TaxpayerCaseSearchResultList();
        result.setTaxpayerFeature(taxpayerDateFeature);
        if (ZcaValidator.isNullEmpty(taxpayerDateFeature) || (fromDate == null) || (toDate == null)){
            return result;
        }
        result.setTaxpayerFeatureValue("From "+ZcaCalendar.convertToMMddyyyy(fromDate, "-")+" to "+ZcaCalendar.convertToMMddyyyy(toDate, "-"));
        
        EntityManager em = getEntityManager();
        try {
            List<G02TaxpayerCaseWithG02Log> aG02TaxpayerCaseWithG02LogList = GardenTaxpayerQuery.querySearchTaxpayerCaseListByDeadlineAndLatestWorkStatus(em, 
                    fromDate, toDate, PeonyLogName.UPDATED_TAXPAYER_CASE_STATUS, GardenTaxpayerCaseStatus.convertEnumValueToType(workStatusForSearch));
            if (aG02TaxpayerCaseWithG02LogList != null){
                HashMap<String, Object> params = new HashMap<>();
                TaxpayerCaseSearchResult aTaxpayerCaseSearchResult;
                for (G02TaxpayerCaseWithG02Log aG02TaxpayerCaseWithG02Log :aG02TaxpayerCaseWithG02LogList){
                    aTaxpayerCaseSearchResult = new TaxpayerCaseSearchResult();
                    aTaxpayerCaseSearchResult.setTaxpayerCase(aG02TaxpayerCaseWithG02Log.getTaxpayerCase());
                    aTaxpayerCaseSearchResult.getWorkStatusLogList().add(aG02TaxpayerCaseWithG02Log.getLatestWorkStatus());
                    //taxpayerInfoList
                    params.put("taxpayerCaseUuid", aG02TaxpayerCaseWithG02Log.getTaxpayerCase().getTaxpayerCaseUuid());
                    aTaxpayerCaseSearchResult.setTaxpayerInfoList(GardenJpaUtils.findEntityListByNamedQuery(em, G02TaxpayerInfo.class, "G02TaxpayerInfo.findByTaxpayerCaseUuid", params));
                    result.getTaxpayerCaseSearchResultList().add(aTaxpayerCaseSearchResult);
                }
            }
        } finally {
            em.close();
        }
        return result;
    }

    public TaxpayerCaseSearchResultList searchTaxpayerCaseSearchResultListByDateRange(String taxpayerDateFeature, Date fromDate, Date toDate) {
        TaxpayerCaseSearchResultList result = new TaxpayerCaseSearchResultList();
        result.setTaxpayerFeature(taxpayerDateFeature);
        if (ZcaValidator.isNullEmpty(taxpayerDateFeature) || (fromDate == null) || (toDate == null)){
            return result;
        }
        result.setTaxpayerFeatureValue("From "+ZcaCalendar.convertToMMddyyyy(fromDate, "-")+" to "+ZcaCalendar.convertToMMddyyyy(toDate, "-"));
        
        EntityManager em = getEntityManager();
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("fromDate", fromDate);
            params.put("toDate", toDate);
            List<G02TaxpayerCase> aG02TaxpayerCaseList = GardenJpaUtils.findEntityListByQuery(em, G02TaxpayerCase.class, 
                    "SELECT g FROM G02TaxpayerCase g WHERE g.deadline BETWEEN :fromDate AND :toDate", params);
            if (aG02TaxpayerCaseList != null){
                TaxpayerCaseSearchResult aTaxpayerCaseSearchResult;
                for (G02TaxpayerCase aG02TaxpayerCase : aG02TaxpayerCaseList){
                    aTaxpayerCaseSearchResult = new TaxpayerCaseSearchResult();
                    aTaxpayerCaseSearchResult.setTaxpayerCase(aG02TaxpayerCase);
                    //taxpayerInfoList
                    params.clear();
                    params.put("taxpayerCaseUuid", aG02TaxpayerCase.getTaxpayerCaseUuid());
                    aTaxpayerCaseSearchResult.setTaxpayerInfoList(GardenJpaUtils.findEntityListByNamedQuery(em, G02TaxpayerInfo.class, "G02TaxpayerInfo.findByTaxpayerCaseUuid", params));
                    result.getTaxpayerCaseSearchResultList().add(aTaxpayerCaseSearchResult);
                }//for-loop
            }
        } finally {
            em.close();
        }
        return result;
    }
    
    public TaxpayerCaseSearchCacheResultList retrieveTaxpayerCaseSearchCacheResultListByDateRangeForSpecificWorkStatus(String taxpayerDateFeature, Date fromDate, Date toDate, String workStatusForSearch) {
        TaxpayerCaseSearchCacheResultList result = new TaxpayerCaseSearchCacheResultList();
        EntityManager em = getEntityManager();
        try {

            HashMap<String, Object> params = new HashMap<>();
            params.put("fromDate", fromDate);
            params.put("toDate", toDate);
            params.put("extFromDate", fromDate);
            params.put("extToDate", toDate);
            String sqlQuery;
            if (ZcaValidator.isNullEmpty(workStatusForSearch)){
                sqlQuery = "SELECT g FROM G02TaxpayerCaseSearchCache g WHERE (g.deadline BETWEEN :fromDate AND :toDate) OR (g.extension BETWEEN :extFromDate AND :extToDate)";
            }else{
                params.put("latestTaxpayerStatus", workStatusForSearch);
                sqlQuery = "SELECT g FROM G02TaxpayerCaseSearchCache g WHERE g.latestTaxpayerStatus = :latestTaxpayerStatus AND ((g.deadline BETWEEN :fromDate AND :toDate) OR (g.extension BETWEEN :extFromDate AND :extToDate))";
            }
            
            List<G02TaxpayerCaseSearchCache> aG02TaxpayerCaseSearchCacheList = GardenJpaUtils.findEntityListByQuery(em, G02TaxpayerCaseSearchCache.class, sqlQuery, params);
            if (aG02TaxpayerCaseSearchCacheList != null){
                TaxpayerCaseSearchCacheResult aTaxpayerCaseSearchCacheResult;
                for (G02TaxpayerCaseSearchCache aG02TaxpayerCaseSearchCache : aG02TaxpayerCaseSearchCacheList){
                    aTaxpayerCaseSearchCacheResult = new TaxpayerCaseSearchCacheResult();
                    aTaxpayerCaseSearchCacheResult.setTaxpayerCaseSearchCache(aG02TaxpayerCaseSearchCache);
                    result.getTaxpayerCaseSearchCacheResultList().add(aTaxpayerCaseSearchCacheResult);
                }//for-loop
            }
        } finally {
            em.close();
        }
        return result;
    }
    
    public TaxpayerCaseStatusCacheResultList retrieveTaxpayerCaseStatusCacheResultListByDateRangeForSpecificWorkStatus(String taxpayerDateFeature, Date fromDate, Date toDate, String workStatusForSearch) {
        TaxpayerCaseStatusCacheResultList result = new TaxpayerCaseStatusCacheResultList();
        EntityManager em = getEntityManager();
        try {

            HashMap<String, Object> params = new HashMap<>();
            params.put("fromDate", fromDate);
            params.put("toDate", toDate);
            params.put("extFromDate", fromDate);
            params.put("extToDate", toDate);
            String sqlQuery;
            if (ZcaValidator.isNullEmpty(workStatusForSearch)){
                sqlQuery = "SELECT g FROM G02TaxpayerCaseStatusCache g WHERE (g.deadline BETWEEN :fromDate AND :toDate) OR (g.extension BETWEEN :extFromDate AND :extToDate)";
            }else{
                //params.put("latestTaxpayerStatus", workStatusForSearch);
                //todo zzj: no implementation yet
                //sqlQuery = "SELECT g FROM G02TaxpayerCaseStatusCache g WHERE g.latestTaxpayerStatus = :latestTaxpayerStatus AND ((g.deadline BETWEEN :fromDate AND :toDate) OR (g.extension BETWEEN :extFromDate AND :extToDate))";
                sqlQuery = "SELECT g FROM G02TaxpayerCaseStatusCache g WHERE (g.deadline BETWEEN :fromDate AND :toDate) OR (g.extension BETWEEN :extFromDate AND :extToDate)";
            }
            
            List<G02TaxpayerCaseStatusCache> aG02TaxpayerCaseStatusCacheList = GardenJpaUtils.findEntityListByQuery(em, G02TaxpayerCaseStatusCache.class, sqlQuery, params);
            if (aG02TaxpayerCaseStatusCacheList != null){
                TaxpayerCaseStatusCacheResult aTaxpayerCaseStatusCacheResult;
                for (G02TaxpayerCaseStatusCache aG02TaxpayerCaseStatusCache : aG02TaxpayerCaseStatusCacheList){
                    aTaxpayerCaseStatusCacheResult = new TaxpayerCaseStatusCacheResult();
                    aTaxpayerCaseStatusCacheResult.setTaxpayerCaseStatusCache(aG02TaxpayerCaseStatusCache);
                    result.getTaxpayerCaseStatusCacheResultList().add(aTaxpayerCaseStatusCacheResult);
                }//for-loop
            }
        } finally {
            em.close();
        }
        return result;
    }
    
    public TaxpayerCaseSearchCacheResultList retrieveTaxpayerCaseSearchCacheResultListByDateRange(String taxpayerDateFeature, Date fromDate, Date toDate) {
        return retrieveTaxpayerCaseSearchCacheResultListByDateRangeForSpecificWorkStatus(taxpayerDateFeature, fromDate, toDate, null);
    }

    /**
     * This method is very slow. It's only for cache on the server-side
     * @param taxpayerDateFeature
     * @param fromDate
     * @param toDate
     * @return 
     */
    public TaxpayerCaseSearchResultList searchTaxpayerCaseSearchResultListByDateRangeForCache(String taxpayerDateFeature, Date fromDate, Date toDate) {
        TaxpayerCaseSearchResultList result = new TaxpayerCaseSearchResultList();
        result.setTaxpayerFeature(taxpayerDateFeature);
        if (ZcaValidator.isNullEmpty(taxpayerDateFeature) || (fromDate == null) || (toDate == null)){
            return result;
        }
        result.setTaxpayerFeatureValue("From "+ZcaCalendar.convertToMMddyyyy(fromDate, "-")+" to "+ZcaCalendar.convertToMMddyyyy(toDate, "-"));
        
        EntityManager em = getEntityManager();
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("fromDate", fromDate);
            params.put("toDate", toDate);
            List<G02TaxpayerCase> aG02TaxpayerCaseList = GardenJpaUtils.findEntityListByQuery(em, G02TaxpayerCase.class, 
                    "SELECT g FROM G02TaxpayerCase g WHERE g.deadline BETWEEN :fromDate AND :toDate", params);
            if (aG02TaxpayerCaseList != null){
                TaxpayerCaseSearchResult aTaxpayerCaseSearchResult;
                String taxpayerCaseUuid;
                for (G02TaxpayerCase aG02TaxpayerCase : aG02TaxpayerCaseList){
                    taxpayerCaseUuid = aG02TaxpayerCase.getTaxpayerCaseUuid();
                    aTaxpayerCaseSearchResult = new TaxpayerCaseSearchResult();
                    aTaxpayerCaseSearchResult.setTaxpayerCase(aG02TaxpayerCase);
                    //taxpayerInfoList
                    params.clear();
                    params.put("taxpayerCaseUuid", taxpayerCaseUuid);
                    aTaxpayerCaseSearchResult.setTaxpayerInfoList(GardenJpaUtils.findEntityListByNamedQuery(em, G02TaxpayerInfo.class, "G02TaxpayerInfo.findByTaxpayerCaseUuid", params));
                    //PeonyBillPayment
                    aTaxpayerCaseSearchResult.setPeonyBillPaymentList(GardenJpaUtils.findPeonyBillPaymentListByEntityUuid(em, taxpayerCaseUuid));
                    //work status
                    aTaxpayerCaseSearchResult.setWorkStatusLogList(GardenJpaUtils.findWorkStatusLogListByTaxpayerCaseUuuid(em, taxpayerCaseUuid));
                    //memo
                    aTaxpayerCaseSearchResult.setTaxpayerMemoList(GardenJpaUtils.findG02MemoListByEntityUuid(em, taxpayerCaseUuid));
                    
                    result.getTaxpayerCaseSearchResultList().add(aTaxpayerCaseSearchResult);
                }//for-loop
            }
        } finally {
            em.close();
        }
        return result;
    }

    /**
     * @deprecated - only search by deadline's date range
     * @param taxpayerDateFeature
     * @param aG02TaxpayerCase
     * @return 
     */
    private Date retrievePersistentTaxpayerDateFeatureValue(String taxpayerDateFeature, G02TaxpayerCase aG02TaxpayerCase) {
        TaxpayerEntityDateField aTaxpayerEntityDateField = TaxpayerEntityDateField.convertEnumValueToType(taxpayerDateFeature);
        if (aTaxpayerEntityDateField != null){
            switch(aTaxpayerEntityDateField){
                case DEADLINE:
                    return aG02TaxpayerCase.getDeadline();
                case CREATED_DATE:
                    return aG02TaxpayerCase.getCreated();
                case UPDATED_DATE:
                    return aG02TaxpayerCase.getUpdated();
            }//switch
        }
        return null;
    }
    
    private String retrievePersistentTaxpayerFeatureValue(String taxpayerFeature, G02TaxpayerInfo aG02TaxpayerInfo) {
        SearchTaxpayerCriteria aCritera = SearchTaxpayerCriteria.convertEnumValueToType(taxpayerFeature);
        if (aCritera != null){
            switch (aCritera){
                case RELATIONSHIPS:
                    return aG02TaxpayerInfo.getRelationships();
                case SSN:
                    return aG02TaxpayerInfo.getSsn();
                case FIRST_NAME:
                    return aG02TaxpayerInfo.getFirstName();
                case LAST_NAME:
                    return aG02TaxpayerInfo.getLastName();
                case FULL_NAME:
                    return aG02TaxpayerInfo.getFirstName() + " " + aG02TaxpayerInfo.getLastName();
                case EMAIL:
                    return aG02TaxpayerInfo.getEmail();
                case MOBILE_PHONE:
                    return aG02TaxpayerInfo.getMobilePhone();
                case HOME_PHONE:
                    return aG02TaxpayerInfo.getHomePhone();
            }
        }
        return null;
    }

    /**
     * 
     * @param taxpayerCaseUuid
     * @return - if not found in both taxpayer table and taxpayer-bk, NULL returned
     */
    public G02TaxpayerCase findTaxpayerCaseByTaxpayerCaseUuid(String taxpayerCaseUuid) {
        if (ZcaValidator.isNullEmpty(taxpayerCaseUuid)){
            return null;
        }
        G02TaxpayerCase result = null;
        
        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.findById(em, G02TaxpayerCase.class, taxpayerCaseUuid);
        } finally {
            em.close();
        }
        return result;
    }
    
    public PeonyTaxpayerCase findPeonyTaxpayerCaseByTaxpayerCaseUuid(String taxpayerCaseUuid) {
        if (ZcaValidator.isNullEmpty(taxpayerCaseUuid)){
            return null;
        }
        PeonyTaxpayerCase result = null;
        
        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.findPeonyTaxpayerCaseByTaxpayerCaseUuid(em, taxpayerCaseUuid);
        } finally {
            em.close();
        }
        return result;
    }
    
    public PeonyTaxpayerCase findBasicPeonyTaxpayerCaseByTaxpayerCaseUuid(String taxpayerCaseUuid) {
        if (ZcaValidator.isNullEmpty(taxpayerCaseUuid)){
            return null;
        }
        PeonyTaxpayerCase result = null;
        
        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.findBasicPeonyTaxpayerCaseByTaxpayerCaseUuid(em, taxpayerCaseUuid);
        } finally {
            em.close();
        }
        return result;
    }

    public G02TaxpayerInfo findTaxpayerInfoBySsn(String ssn) {
        if (ZcaValidator.isNullEmpty(ssn)){
            return null;
        }
        G02TaxpayerInfo result = null;
        
        EntityManager em = getEntityManager();
        try {
            result = findG02TaxpayerInfoBySsn(em, ssn);
        } finally {
            em.close();
        }
        return result;
    }

    private G02TaxpayerInfo findG02TaxpayerInfoBySsn(EntityManager em, String ssn) {
        G02TaxpayerInfo result = null;
        HashMap<String, Object> params = new HashMap<>();
        params.put("ssn", ssn);
        List<G02TaxpayerInfo> aG02TaxpayerInfoList = GardenJpaUtils.findEntityListByNamedQuery(em, G02TaxpayerInfo.class, "G02TaxpayerInfo.findBySsn", params);
        if ((aG02TaxpayerInfoList != null) && (!aG02TaxpayerInfoList.isEmpty())){
            for (G02TaxpayerInfo aG02TaxpayerInfo : aG02TaxpayerInfoList){
                if (TaxpayerRelationship.PRIMARY_TAXPAYER.value().equalsIgnoreCase(aG02TaxpayerInfo.getRelationships())){
                    result = aG02TaxpayerInfo;
                    break;
                }
            }
        }
        return result;
    }
    
    public PeonyTaxpayerCaseList findPeonyTaxpayerCaseListByPrimaryTaxpayerSsn(String taxpayerSsn) {
        if (ZcaValidator.isNullEmpty(taxpayerSsn)){
            return null;
        }
        PeonyTaxpayerCaseList result = null;
        
        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.findPeonyTaxpayerCaseListByPrimaryTaxpayerSsn(em, taxpayerSsn);
        } finally {
            em.close();
        }
        return result;
    }
    
    public PeonyTaxpayerCaseList findBasicPeonyTaxpayerCaseListByPrimaryTaxpayerSsn(String taxpayerSsn) {
        if (ZcaValidator.isNullEmpty(taxpayerSsn)){
            return null;
        }
        PeonyTaxpayerCaseList result = null;
        
        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.findBasicPeonyTaxpayerCaseListByPrimaryTaxpayerSsn(em, taxpayerSsn);
        } finally {
            em.close();
        }
        return result;
    }
    
    public PeonyTaxpayerCase loadWorkStatusForPeonyTaxpayerCase(PeonyTaxpayerCase aPeonyTaxpayerCase)  {
        if (aPeonyTaxpayerCase == null){
            return null;
        }
        
        EntityManager em = getEntityManager();
        try {
            GardenJpaUtils.loadWorkStatusForPeonyTaxpayerCase(em, aPeonyTaxpayerCase);
        } finally {
            em.close();
        }
        return aPeonyTaxpayerCase;
    }
    
    public PeonyTaxpayerCase loadTagsAndMemosForPeonyTaxpayerCase(PeonyTaxpayerCase aPeonyTaxpayerCase)  {
        if (aPeonyTaxpayerCase == null){
            return null;
        }
        
        EntityManager em = getEntityManager();
        try {
            GardenJpaUtils.loadTagsAndMemosForPeonyTaxpayerCase(em, aPeonyTaxpayerCase);
        } finally {
            em.close();
        }
        return aPeonyTaxpayerCase;
    }
    
    public PeonyTaxpayerCase loadOtherInformationForPeonyTaxpayerCase(PeonyTaxpayerCase aPeonyTaxpayerCase)  {
        if (aPeonyTaxpayerCase == null){
            return null;
        }
        
        EntityManager em = getEntityManager();
        try {
            GardenJpaUtils.loadOtherInformationForPeonyTaxpayerCase(em, aPeonyTaxpayerCase);
        } finally {
            em.close();
        }
        return aPeonyTaxpayerCase;
    }
    
    public PeonyTaxpayerCase loadBillAndPaymentsForPeonyTaxpayerCase(PeonyTaxpayerCase aPeonyTaxpayerCase)  {
        if (aPeonyTaxpayerCase == null){
            return null;
        }
        
        EntityManager em = getEntityManager();
        try {
            GardenJpaUtils.loadBillAndPaymentsForPeonyTaxpayerCase(em, aPeonyTaxpayerCase);
        } finally {
            em.close();
        }
        return aPeonyTaxpayerCase;
    }
    
    public PeonyTaxpayerCase loadArchiveAndFilesForPeonyTaxpayerCase(PeonyTaxpayerCase aPeonyTaxpayerCase)  {
        if (aPeonyTaxpayerCase == null){
            return null;
        }
        
        EntityManager em = getEntityManager();
        try {
            GardenJpaUtils.loadArchiveAndFilesForPeonyTaxpayerCase(em, aPeonyTaxpayerCase);
        } finally {
            em.close();
        }
        return aPeonyTaxpayerCase;
    }
    
    public PeonyTaxpayerCaseList findPeonyTaxpayerCaseListByTaxpayerSsn(String taxpayerSsn) {
        if (ZcaValidator.isNullEmpty(taxpayerSsn)){
            return null;
        }
        PeonyTaxpayerCaseList result = null;
        
        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.findPeonyTaxpayerCaseListByTaxpayerSsn(em, taxpayerSsn);
        } finally {
            em.close();
        }
        return result;
    }

    public G02TaxpayerInfo storeTaxpayerInfo(G02TaxpayerInfo aG02TaxpayerInfo) throws Exception {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            G02TaxpayerCase aG02TaxpayerCase = GardenJpaUtils.findById(em, G02TaxpayerCase.class, aG02TaxpayerInfo.getTaxpayerCaseUuid());
            if (aG02TaxpayerCase == null){
                aG02TaxpayerInfo = null; //return NULL and give up this operation
            }else{
                GardenJpaUtils.storeEntity(em, G02TaxpayerInfo.class, aG02TaxpayerInfo, aG02TaxpayerInfo.getTaxpayerUserUuid(), 
                                            G02DataUpdaterFactory.getSingleton().getG02TaxpayerInfoUpdater());
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aG02TaxpayerInfo;
    }
    
    public G02PersonalProperty deletePersonalProperty(String personalPropertyUUid) throws Exception{
        G02PersonalProperty aG02PersonalProperty = null;
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            aG02PersonalProperty = GardenJpaUtils.deleteEntity(em, G02PersonalProperty.class, personalPropertyUUid);
            
            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aG02PersonalProperty;
    
    }
    
    public G02PersonalBusinessProperty deletePersonalBusinessProperty(String personalBusinessPropertyUUid) throws Exception{
        G02PersonalBusinessProperty aG02PersonalBusinessProperty = null;
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            aG02PersonalBusinessProperty = GardenJpaUtils.deleteEntity(em, G02PersonalBusinessProperty.class, personalBusinessPropertyUUid);
            
            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aG02PersonalBusinessProperty;
    
    }
    
    public G02TlcLicense deleteTlcLicense(String tlcLicenseUUid) throws Exception{
        G02TlcLicense aG02TlcLicense = null;
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            aG02TlcLicense = GardenJpaUtils.deleteEntity(em, G02TlcLicense.class, tlcLicenseUUid);
            
            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aG02TlcLicense;
    
    }

    public G02TaxpayerInfo deleteTaxpayerInfo(String taxpayerUserUuid) throws Exception {
        G02TaxpayerInfo aG02TaxpayerInfo = null;
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            aG02TaxpayerInfo = GardenJpaUtils.deleteEntity(em, G02TaxpayerInfo.class, taxpayerUserUuid);
            
            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aG02TaxpayerInfo;
    }
    
    /**
     * Store the main content of PeonyTaxpayerCase
     * @param aPeonyTaxpayerCase
     * @return
     * @throws Exception 
     */
    public PeonyTaxpayerCase storePeonyTaxpayerCase(PeonyTaxpayerCase aPeonyTaxpayerCase) throws Exception{
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            aPeonyTaxpayerCase = storePeonyTaxpayerCaseHelper(em, aPeonyTaxpayerCase);

            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        
        return aPeonyTaxpayerCase;
    
    }
    
    private PeonyTaxpayerCase storePeonyTaxpayerCaseHelper(EntityManager em, PeonyTaxpayerCase aPeonyTaxpayerCase) throws Exception{
            
        /**
         * PeonyAccount: check if this PeonyAccount is invalid. if it is invalid, 
         * it means this PeonyTaxpayerCase demand to create a customer instance. 
         * The system will create such a customer account for this case according 
         * to information provided by this taxpayer's case
         */
        GardenJpaUtils.prepareCustomerAccountForTaxpayerCase(em, aPeonyTaxpayerCase);

        //G02Location primaryLocation
        GardenJpaUtils.storeEntity(em, G02Location.class, aPeonyTaxpayerCase.getPrimaryLocation(), aPeonyTaxpayerCase.getPrimaryLocation().getLocationUuid(), 
                                    G02DataUpdaterFactory.getSingleton().getG02LocationUpdater());

        //G02TaxpayerCase taxpayerCase
        GardenJpaUtils.storeEntity(em, G02TaxpayerCase.class, aPeonyTaxpayerCase.getTaxpayerCase(), aPeonyTaxpayerCase.getTaxpayerCase().getTaxpayerCaseUuid(), 
                                    G02DataUpdaterFactory.getSingleton().getG02TaxpayerCaseUpdater());

        //G02Log taxpayerCaseStatus
        if ((aPeonyTaxpayerCase.getLatestTaxpayerCaseStatus() != null) && (ZcaValidator.isNotNullEmpty(aPeonyTaxpayerCase.getLatestTaxpayerCaseStatus().getLogUuid()))){
            GardenJpaUtils.storeEntity(em, G02Log.class, aPeonyTaxpayerCase.getLatestTaxpayerCaseStatus(), aPeonyTaxpayerCase.getLatestTaxpayerCaseStatus().getLogUuid(), 
                                        G02DataUpdaterFactory.getSingleton().getG02LogUpdater());
        }

        //List<G02TaxpayerInfo> taxpayerInfoList;
        List<G02TaxpayerInfo> aG02TaxpayerInfoList = aPeonyTaxpayerCase.getTaxpayerInfoList();
        for (G02TaxpayerInfo aG02TaxpayerInfo : aG02TaxpayerInfoList){
            GardenJpaUtils.storeEntity(em, G02TaxpayerInfo.class, aG02TaxpayerInfo, aG02TaxpayerInfo.getTaxpayerUserUuid(), 
                                        G02DataUpdaterFactory.getSingleton().getG02TaxpayerInfoUpdater());
        }

        //List<G02PersonalProperty> personalPropertyList;
        List<G02PersonalProperty> aG02PersonalPropertyList = aPeonyTaxpayerCase.getPersonalPropertyList();
        for (G02PersonalProperty aG02PersonalProperty : aG02PersonalPropertyList){
            GardenJpaUtils.storeEntity(em, G02PersonalProperty.class, aG02PersonalProperty, aG02PersonalProperty.getPersonalPropertyUuid(), 
                                        G02DataUpdaterFactory.getSingleton().getG02PersonalPropertyUpdater());
        }

        //List<G02PersonalBusinessProperty> personalBusinessPropertyList;
        List<G02PersonalBusinessProperty> aG02PersonalBusinessPropertyList = aPeonyTaxpayerCase.getPersonalBusinessPropertyList();
        for (G02PersonalBusinessProperty aG02PersonalBusinessProperty : aG02PersonalBusinessPropertyList){
            GardenJpaUtils.storeEntity(em, G02PersonalBusinessProperty.class, aG02PersonalBusinessProperty, 
                                       aG02PersonalBusinessProperty.getPersonalBusinessPropertyUuid(), 
                                       G02DataUpdaterFactory.getSingleton().getG02PersonalBusinessPropertyUpdater());
        }

        //List<G02TlcLicense> tlcLicenseList;
        List<G02TlcLicense> aG02TlcLicenseList = aPeonyTaxpayerCase.getTlcLicenseList();
        for (G02TlcLicense aG02TlcLicense : aG02TlcLicenseList){
            GardenJpaUtils.storeEntity(em, G02TlcLicense.class, aG02TlcLicense, aG02TlcLicense.getDriverUuid(), 
                                        G02DataUpdaterFactory.getSingleton().getG02TlcLicenseUpdater());
        }

        //List<G02DocumentTag> aG02DocumentTagList;
        List<G02DocumentTag> aG02DocumentTagList = aPeonyTaxpayerCase.getDocumentTagList();
        for (G02DocumentTag aG02DocumentTag : aG02DocumentTagList){
            GardenJpaUtils.storeEntity(em, G02DocumentTag.class, aG02DocumentTag, aG02DocumentTag.getDocumentTagUuid(), 
                                        G02DataUpdaterFactory.getSingleton().getG02DocumentTagUpdater());
        }
        
        return aPeonyTaxpayerCase;
    }

    public PeonyTaxpayerCase storePeonyTaxpayerCaseBasicInformation(PeonyTaxpayerCase aPeonyTaxpayerCase) throws Exception{
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();

            PeonyAccount customer = aPeonyTaxpayerCase.getCustomer();
            try{
                G02EntityValidator.getSingleton().validate(customer);
                GardenJpaUtils.storeEntity(em, G02Account.class, 
                                            customer.getAccount(), 
                                            customer.getAccount().getAccountUuid(), 
                                            G02DataUpdaterFactory.getSingleton().getG02AccountUpdater());
                GardenJpaUtils.storeEntity(em, G02User.class, 
                                            customer.getUser(), customer.getUser().getUserUuid(), 
                                            G02DataUpdaterFactory.getSingleton().getG02UserUpdater());
            }catch(Exception ex){
                //not a valid customer. skip it
            }
            
            GardenJpaUtils.storeEntity(em, G02Location.class, aPeonyTaxpayerCase.getPrimaryLocation(), aPeonyTaxpayerCase.getPrimaryLocation().getLocationUuid(), 
                                        G02DataUpdaterFactory.getSingleton().getG02LocationUpdater());
            
            GardenJpaUtils.storeEntity(em, G02TaxpayerCase.class, aPeonyTaxpayerCase.getTaxpayerCase(), aPeonyTaxpayerCase.getTaxpayerCase().getTaxpayerCaseUuid(), 
                                        G02DataUpdaterFactory.getSingleton().getG02TaxpayerCaseUpdater());
            
            GardenJpaUtils.storeEntity(em, G02TaxpayerInfo.class, aPeonyTaxpayerCase.retrievePrimaryTaxpayerInfo(), aPeonyTaxpayerCase.retrievePrimaryTaxpayerInfo().getTaxpayerUserUuid(), 
                                        G02DataUpdaterFactory.getSingleton().getG02TaxpayerInfoUpdater());
            
            GardenJpaUtils.storeEntity(em, G02TaxpayerInfo.class, aPeonyTaxpayerCase.retrieveSpouseTaxpayerInfo(), aPeonyTaxpayerCase.retrieveSpouseTaxpayerInfo().getTaxpayerUserUuid(), 
                                        G02DataUpdaterFactory.getSingleton().getG02TaxpayerInfoUpdater());

            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        
        return aPeonyTaxpayerCase;
    }

    public G02TaxpayerCase removeTaxpayerCase(String taxpayerCaseUuid) throws Exception{
        if (ZcaValidator.isNullEmpty(taxpayerCaseUuid)){
            throw new Exception("Taxpayer UUID is demanded for this operation.");
        }
        G02TaxpayerCase aG02TaxpayerCase = null;
        EntityManager em = null;
        UserTransaction utx = getUserTransaction();
        //delete associated entities members
        try {
            utx.begin();
            em = getEntityManager();
            
            aG02TaxpayerCase = GardenJpaUtils.findById(em, G02TaxpayerCase.class, taxpayerCaseUuid);
            if (aG02TaxpayerCase != null){
                G02TaxpayerCaseBk aG02TaxpayerCaseBk = new G02TaxpayerCaseBk();
                aG02TaxpayerCaseBk.setAgreementUuid(aG02TaxpayerCase.getAgreementUuid());
                aG02TaxpayerCaseBk.setAgreementSignature(aG02TaxpayerCase.getAgreementSignature());
                aG02TaxpayerCaseBk.setAgreementSignatureTimestamp(aG02TaxpayerCase.getAgreementSignatureTimestamp());
                aG02TaxpayerCaseBk.setBankAccountNumber(aG02TaxpayerCase.getBankAccountNumber());
                aG02TaxpayerCaseBk.setBankRoutingNumber(aG02TaxpayerCase.getBankRoutingNumber());
                aG02TaxpayerCaseBk.setCustomerAccountUuid(aG02TaxpayerCase.getCustomerAccountUuid());
                aG02TaxpayerCaseBk.setFederalFilingStatus(aG02TaxpayerCase.getFederalFilingStatus());
                aG02TaxpayerCaseBk.setBankNote(aG02TaxpayerCase.getBankNote());
                aG02TaxpayerCaseBk.setContact(aG02TaxpayerCase.getContact());
                aG02TaxpayerCaseBk.setDeadline(aG02TaxpayerCase.getDeadline());
                aG02TaxpayerCaseBk.setEntityStatus(aG02TaxpayerCase.getEntityStatus());
                aG02TaxpayerCaseBk.setTaxpayerCaseUuid(aG02TaxpayerCase.getTaxpayerCaseUuid());
                aG02TaxpayerCaseBk.setLatestLogUuid(aG02TaxpayerCase.getLatestLogUuid());
                aG02TaxpayerCaseBk.setExtension(aG02TaxpayerCase.getExtension());
                aG02TaxpayerCaseBk.setMemo(aG02TaxpayerCase.getMemo());
                aG02TaxpayerCaseBk.setCreated(aG02TaxpayerCase.getCreated());
                aG02TaxpayerCaseBk.setUpdated(aG02TaxpayerCase.getUpdated());
                
                GardenJpaUtils.storeEntity(em, G02TaxpayerCaseBk.class, aG02TaxpayerCaseBk, aG02TaxpayerCaseBk.getTaxpayerCaseUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02TaxpayerCaseBkUpdater());
                
                GardenJpaUtils.deleteEntity(em, G02TaxpayerCase.class, taxpayerCaseUuid);
            
                utx.commit();
                aG02TaxpayerCase = null; //signal of success
            }
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            //Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                //Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return aG02TaxpayerCase;
    }

    public void storeG02TaxpayerCaseSearchAndStatusCacheList(List<G02TaxpayerCaseSearchCache> aG02TaxpayerCaseSearchCacheList, List<G02TaxpayerCaseStatusCache> aG02TaxpayerCaseStatusCacheList) {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            if (aG02TaxpayerCaseSearchCacheList != null){
                for (G02TaxpayerCaseSearchCache aG02TaxpayerCaseSearchCache : aG02TaxpayerCaseSearchCacheList){
                    GardenJpaUtils.storeEntity(em, G02TaxpayerCaseSearchCache.class, aG02TaxpayerCaseSearchCache, aG02TaxpayerCaseSearchCache.getTaxpayerCaseUuid(), 
                                                G02DataUpdaterFactory.getSingleton().getG02TaxpayerCaseSearchCacheUpdater());
                }
            }
            if (aG02TaxpayerCaseStatusCacheList != null){
                for (G02TaxpayerCaseStatusCache aG02TaxpayerCaseStatusCache : aG02TaxpayerCaseStatusCacheList){
                    GardenJpaUtils.storeEntity(em, G02TaxpayerCaseStatusCache.class, aG02TaxpayerCaseStatusCache, aG02TaxpayerCaseStatusCache.getTaxpayerCaseUuid(), 
                                                G02DataUpdaterFactory.getSingleton().getG02TaxpayerCaseStatusCacheUpdater());
                }
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public TaxpayerCaseStatusCacheResult storeTaxpayerCaseStatusCacheResultWithStatusLog(TaxpayerCaseStatusCacheResult aTaxpayerCaseStatusCacheResult, boolean removeStatusLog) {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            G02TaxpayerCaseStatusCache aG02TaxpayerCaseStatusCache = aTaxpayerCaseStatusCacheResult.getTaxpayerCaseStatusCache();
            GardenJpaUtils.storeEntity(em, G02TaxpayerCaseStatusCache.class, aG02TaxpayerCaseStatusCache, aG02TaxpayerCaseStatusCache.getTaxpayerCaseUuid(), 
                                        G02DataUpdaterFactory.getSingleton().getG02TaxpayerCaseStatusCacheUpdater());
            
            G02Log statusLog = aTaxpayerCaseStatusCacheResult.getStatusLog();
            HashMap<String, Object> params = new HashMap<>();
            params.put("logName", statusLog.getLogName());
            params.put("logMessage", statusLog.getLogMessage());
            params.put("entityUuid", statusLog.getEntityUuid());
            String sqlQuery = "SELECT g FROM G02Log g WHERE g.logName = :logName AND g.logMessage = :logMessage AND g.entityUuid = :entityUuid";
            List<G02Log> aG02LogList = GardenJpaUtils.findEntityListByQuery(em, G02Log.class, sqlQuery, params);
            for (G02Log aG02Log : aG02LogList){
                GardenJpaUtils.deleteEntity(em, G02Log.class, aG02Log.getLogUuid());
            }
            if (!removeStatusLog){
                GardenJpaUtils.storeEntity(em, G02Log.class, statusLog, statusLog.getLogUuid(), G02DataUpdaterFactory.getSingleton().getG02LogUpdater());
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return aTaxpayerCaseStatusCacheResult;
    }

    public TaxpayerCaseStatusCacheResult storeTaxpayerCaseStatusCacheResult(TaxpayerCaseStatusCacheResult aTaxpayerCaseStatusCacheResult) {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            G02TaxpayerCaseStatusCache aG02TaxpayerCaseStatusCache = aTaxpayerCaseStatusCacheResult.getTaxpayerCaseStatusCache();
            GardenJpaUtils.storeEntity(em, G02TaxpayerCaseStatusCache.class, aG02TaxpayerCaseStatusCache, aG02TaxpayerCaseStatusCache.getTaxpayerCaseUuid(), 
                                        G02DataUpdaterFactory.getSingleton().getG02TaxpayerCaseStatusCacheUpdater());
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return aTaxpayerCaseStatusCacheResult;
    }
    
    public G02PersonalProperty storePersonalProperty(G02PersonalProperty aG02PersonalProperty) throws Exception {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            G02TaxpayerCase aG02TaxpayerCase = GardenJpaUtils.findById(em, G02TaxpayerCase.class, aG02PersonalProperty.getTaxpayerCaseUuid());
            if (aG02TaxpayerCase == null){
                aG02PersonalProperty = null; //return NULL and give up this operation
            }else{
                GardenJpaUtils.storeEntity(em, G02PersonalProperty.class, aG02PersonalProperty, aG02PersonalProperty.getPersonalPropertyUuid(), 
                                            G02DataUpdaterFactory.getSingleton().getG02PersonalPropertyUpdater());
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aG02PersonalProperty;
    }
    
    public G02PersonalBusinessProperty storePersonalBusinessProperty(G02PersonalBusinessProperty aG02PersonalBusinessProperty) throws Exception {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            G02TaxpayerCase aG02TaxpayerCase = GardenJpaUtils.findById(em, G02TaxpayerCase.class, aG02PersonalBusinessProperty.getTaxpayerCaseUuid());
            if (aG02TaxpayerCase == null){
                aG02PersonalBusinessProperty = null; //return NULL and give up this operation
            }else{
                GardenJpaUtils.storeEntity(em, G02PersonalBusinessProperty.class, aG02PersonalBusinessProperty, aG02PersonalBusinessProperty.getPersonalBusinessPropertyUuid(), 
                                            G02DataUpdaterFactory.getSingleton().getG02PersonalBusinessPropertyUpdater());
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aG02PersonalBusinessProperty;
    }
    
    public G02TlcLicense storeTlcLicense(G02TlcLicense aG02TlcLicense) throws Exception {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            G02TaxpayerCase aG02TaxpayerCase = GardenJpaUtils.findById(em, G02TaxpayerCase.class, aG02TlcLicense.getTaxpayerCaseUuid());
            if (aG02TaxpayerCase == null){
                aG02TlcLicense = null; //return NULL and give up this operation
            }else{
                GardenJpaUtils.storeEntity(em, G02TlcLicense.class, aG02TlcLicense, aG02TlcLicense.getDriverUuid(), 
                                            G02DataUpdaterFactory.getSingleton().getG02TlcLicenseUpdater());
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aG02TlcLicense;
    }

//    public PeonyTaxpayerCase storePeonyTaxpayerCaseDependentsAndOthers(PeonyTaxpayerCase aPeonyTaxpayerCase) throws Exception{
//        //store...        
//        EntityManager em = null;
//        UserTransaction utx =  getUserTransaction();
//        try {
//            utx.begin();
//            em = getEntityManager();
//            
//            List<G02PersonalProperty> aG02PersonalPropertyList = aPeonyTaxpayerCase.getPersonalPropertyList();
//            if (aG02PersonalPropertyList != null){
//                for (G02PersonalProperty aG02PersonalProperty : aG02PersonalPropertyList){
//                    GardenJpaUtils.storeEntity(em, G02PersonalProperty.class, aG02PersonalProperty, aG02PersonalProperty.getPersonalPropertyUuid(), 
//                                                G02DataUpdaterFactory.getSingleton().getG02PersonalPropertyUpdater());
//                }
//            }
//            
//            List<G02PersonalBusinessProperty> aG02PersonalBusinessPropertyList = aPeonyTaxpayerCase.getPersonalBusinessPropertyList();
//            if (aG02PersonalBusinessPropertyList != null){
//                for (G02PersonalBusinessProperty aG02PersonalBusinessProperty : aG02PersonalBusinessPropertyList){
//                    GardenJpaUtils.storeEntity(em, G02PersonalBusinessProperty.class, aG02PersonalBusinessProperty, aG02PersonalBusinessProperty.getPersonalBusinessPropertyUuid(), 
//                                                G02DataUpdaterFactory.getSingleton().getG02PersonalBusinessPropertyUpdater());
//                }
//            }
//            
//            List<G02TlcLicense> aG02TlcLicenseList = aPeonyTaxpayerCase.getTlcLicenseList();
//            if (aG02TlcLicenseList != null){
//                for (G02TlcLicense aG02TlcLicense : aG02TlcLicenseList){
//                    GardenJpaUtils.storeEntity(em, G02TlcLicense.class, aG02TlcLicense, aG02TlcLicense.getDriverUuid(), 
//                                                G02DataUpdaterFactory.getSingleton().getG02TlcLicenseUpdater());
//                }
//            }
//
//            utx.commit();
//        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
//            Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex);
//            try {
//                utx.rollback();
//            } catch (IllegalStateException | SecurityException | SystemException ex1) {
//                Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex1);
//                throw ex1;
//            }
//            throw ex;
//        } finally {
//            if (em != null) {
//                em.close();
//            }
//        }
//        
//        return aPeonyTaxpayerCase;
//    }

    /**
     * find TaxpayerCaseBriefList whose deadline is between fromDate and toDate
     * @param fromDate
     * @param toDate
     * @return 
     */
    public TaxpayerCaseBriefList findTaxpayerCaseBriefListByDeadlinePeriod(Date fromDate, Date toDate) {
        if ((fromDate == null) || (toDate == null)){
            /**
             * Reset fromDate and toDate to be the first date and the last date of this year
             */
            GregorianCalendar gc = new GregorianCalendar();
            gc.set(Calendar.MONTH, Calendar.JANUARY);
            gc.set(Calendar.DAY_OF_MONTH, 1);
            fromDate = ZcaCalendar.covertDateToEnding(gc.getTime());
            gc = new GregorianCalendar();
            gc.set(Calendar.MONTH, Calendar.DECEMBER);
            gc.set(Calendar.DAY_OF_MONTH, 31);
            toDate = ZcaCalendar.covertDateToEnding(gc.getTime());
        }
        TaxpayerCaseBriefList result = new TaxpayerCaseBriefList();
        
        EntityManager em = getEntityManager();
        try {
            String sqlQuery = "SELECT g FROM G02TaxpayerCase g WHERE g.deadline BETWEEN :fromDate AND :toDate ";
            HashMap<String, Object> params = new HashMap<>();
            params.put("fromDate", fromDate);
            params.put("toDate", toDate);
            List<G02TaxpayerCase> aG02TaxpayerCaseList = GardenJpaUtils.findEntityListByQuery(em, G02TaxpayerCase.class, sqlQuery, params);
            if (aG02TaxpayerCaseList != null){
                TaxpayerCaseBrief aTaxpayerCaseBrief;
                G02User aG02User;
                List<G02Bill> aG02BillList;
                List<G02Payment> aG02PaymentList;
                double sumBillTotal = 0.0;
                double sumPaymentTotal = 0.0;
                for (G02TaxpayerCase aG02TaxpayerCase : aG02TaxpayerCaseList){
                    aTaxpayerCaseBrief = new TaxpayerCaseBrief();
                    aTaxpayerCaseBrief.setTaxpayerCaseUuid(aG02TaxpayerCase.getTaxpayerCaseUuid());
                    aTaxpayerCaseBrief.setFederalFilingStatus(aG02TaxpayerCase.getFederalFilingStatus());
                    aTaxpayerCaseBrief.setBankNote(aG02TaxpayerCase.getBankNote());
                    aTaxpayerCaseBrief.setContact(aG02TaxpayerCase.getContact());
                    aTaxpayerCaseBrief.setDeadlineText(ZcaCalendar.convertToMMddyyyy(aG02TaxpayerCase.getDeadline(), "-"));
                    aG02User = findById(em, G02User.class, aG02TaxpayerCase.getCustomerAccountUuid());
                    if (aG02User != null){
                        aTaxpayerCaseBrief.setCustomerAccountUuid(aG02User.getUserUuid());
                        aTaxpayerCaseBrief.setCustomerFirstName(aG02User.getFirstName());
                        aTaxpayerCaseBrief.setCustomerLastName(aG02User.getLastName());
                        aTaxpayerCaseBrief.setCustomerSsn(aG02User.getSsn());
                    }
                    params.clear();
                    params.put("entityUuid", aG02TaxpayerCase.getTaxpayerCaseUuid());
                    aG02BillList = GardenJpaUtils.findEntityListByNamedQuery(em, G02Bill.class, "G02Bill.findByEntityUuid", params);
                    double billTotal = 0.0;
                    double paymentTotal = 0.0;
                    if (aG02BillList != null){
                        for (G02Bill aG02Bill : aG02BillList){
                            billTotal += aG02Bill.getBillTotal().doubleValue();
                            params.clear();
                            params.put("billUuid", aG02Bill.getBillUuid());
                            aG02PaymentList = GardenJpaUtils.findEntityListByNamedQuery(em, G02Payment.class, "G02Payment.findByBillUuid", params);
                            for (G02Payment aG02Payment : aG02PaymentList){
                                paymentTotal += aG02Payment.getPaymentPrice().doubleValue();
                            }
                        }//for
                        aTaxpayerCaseBrief.setBillTotalText("$" + billTotal);
                        aTaxpayerCaseBrief.setPaymentTotalText("$" + paymentTotal);
                        aTaxpayerCaseBrief.setBalanceTotalText("$" + (billTotal - paymentTotal));
                    }
                    sumBillTotal += billTotal;
                    sumPaymentTotal += paymentTotal;
                    result.getTaxpayerCaseBriefList().add(aTaxpayerCaseBrief);
                }//for
                result.setSumBillTotalText("$" + sumBillTotal);
                result.setSumPaymentTotalText("$" + sumPaymentTotal);
                result.setSumBalanceTotalText("$" + (sumBillTotal - sumPaymentTotal));
            }
        } catch (Exception ex) {
            Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
        } finally {
            em.close();
        }
        return result;
    }

    /**
     * @deprecated 
     * @param deadline
     * @return
     * @throws Exception 
     */
    public String cloneMissedLocationForTaxpayer(Date deadline) throws Exception {
        if (deadline == null){
            return "Cannot clone without deadline.";
        }
        int year = ZcaCalendar.parseCalendarField(deadline, Calendar.YEAR);
        String result = "Successfully cloned all the missed locations for taxpayer cases with deadline: " + ZcaCalendar.convertToMMddyyyy(deadline, "-");
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        int counter = 0;
        int total = 0;
        try {
            utx.begin();
            em = getEntityManager();
            
            System.out.println(">>> Step-01: findTargetMissedLocationMapByDeadlinePeriod...");
            HashMap<String, List<G02Location>> targetG02TaxpayerCaseLocationMap = findTargetMissedLocationMapByDeadlinePeriod(em, 
                    ZcaCalendar.createDate(year-1, Calendar.JANUARY, 1, 0, 0, 0), ZcaCalendar.createDate(year-1, Calendar.DECEMBER, 1, 0, 0, 0));
            System.out.println(">>> Step-02: findClonedLocationMapByDeadlinePeriod...");
            HashMap<PeonyTaxpayerCase, List<G02Location>> clonedG02TaxpayerCaseLocationMap = findClonedLocationMapByDeadlinePeriod(em, 
                    ZcaCalendar.createDate(year, Calendar.JANUARY, 1, 0, 0, 0), ZcaCalendar.createDate(year, Calendar.DECEMBER, 1, 0, 0, 0));
            total = clonedG02TaxpayerCaseLocationMap.size();
            Set<PeonyTaxpayerCase> keys = clonedG02TaxpayerCaseLocationMap.keySet();
            Iterator<PeonyTaxpayerCase> itr = keys.iterator();
            PeonyTaxpayerCase clonedPeonyTaxpayerCase;
            List<G02Location> clonedG02LocationList;
            List<G02Location> targetG02LocationList;
            G02Location missedLocation;
            System.out.println(">>> Step-03: clone missed location...");
            int counter_not_found = 0;
            int counter_manual_input = 0;
            while (itr.hasNext()){
                clonedPeonyTaxpayerCase = itr.next();
                clonedG02LocationList = clonedG02TaxpayerCaseLocationMap.get(clonedPeonyTaxpayerCase);
                if ((clonedG02LocationList == null) || (clonedG02LocationList.isEmpty())){
                    targetG02LocationList = targetG02TaxpayerCaseLocationMap.get(clonedPeonyTaxpayerCase.getPrimaryTaxpayerSsn());
                    if ((targetG02LocationList != null) && (!targetG02LocationList.isEmpty())){
                        System.out.println("-- cloning missed location..." + (counter++));
                        if (targetG02LocationList.size() == 1){
                            missedLocation = cloneG02Location(targetG02LocationList.get(0), clonedPeonyTaxpayerCase);
                        }else{
                            missedLocation = targetG02LocationList.get(1);
                            missedLocation.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
                            missedLocation.setEntityUuid(clonedPeonyTaxpayerCase.getTaxpayerCase().getTaxpayerCaseUuid());
                        }
                        GardenJpaUtils.storeEntity(em, G02Location.class, missedLocation, missedLocation.getLocationUuid(), G02DataUpdaterFactory.getSingleton().getG02LocationUpdater());
                    }else{
                        System.out.println("-- CANNOT find missed location in the previous year..." + (counter_not_found++));
                    }
                }else{
                    //do nothing, i.e. keep the existing location which was manually input
                    System.out.println("-- DO NOTHING. "+clonedPeonyTaxpayerCase.getPrimaryTaxpayerSsn()+"keep the existing location which was manually input..." + (counter_manual_input++));
                }
            }
            utx.commit();
            result += " (" + counter + "/" + total + " of missed-locations for taxpayer case was/were cloned.)";
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            result = "Technical error raised: " + counter + " of taxpayer case was/were cloned.";
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return result;
    }
    
    private HashMap<PeonyTaxpayerCase, List<G02Location>> findClonedLocationMapByDeadlinePeriod(EntityManager em, Date fromDate, Date toDate) {
        HashMap<PeonyTaxpayerCase, List<G02Location>> result = new HashMap<>();
        List<PeonyTaxpayerCase> aPeonyTaxpayerCaseList = findPeonyTaxpayerCaseListByDeadlinePeriod(em, fromDate, toDate);
        if (aPeonyTaxpayerCaseList != null){
            for (PeonyTaxpayerCase aPeonyTaxpayerCase : aPeonyTaxpayerCaseList){
                result.put(aPeonyTaxpayerCase, GardenJpaUtils.findG02LocationListByEntityUuid(em, aPeonyTaxpayerCase.getTaxpayerCase().getTaxpayerCaseUuid()));
            }
        }
        return result;
    
    }
    
    private HashMap<String, List<G02Location>> findTargetMissedLocationMapByDeadlinePeriod(EntityManager em, Date fromDate, Date toDate) {
        HashMap<String, List<G02Location>> result = new HashMap<>();
        List<PeonyTaxpayerCase> aPeonyTaxpayerCaseList = findPeonyTaxpayerCaseListByDeadlinePeriod(em, fromDate, toDate);
        if (aPeonyTaxpayerCaseList != null){
            for (PeonyTaxpayerCase aPeonyTaxpayerCase : aPeonyTaxpayerCaseList){
                result.put(aPeonyTaxpayerCase.getPrimaryTaxpayerSsn(), GardenJpaUtils.findG02LocationListByEntityUuid(em, aPeonyTaxpayerCase.getTaxpayerCase().getTaxpayerCaseUuid()));
            }
        }
        return result;
    
    }
    
    public String cloneAllTaxpayerCases(Date deadline) throws Exception {
        if (deadline == null){
            return "Cannot clone without deadline.";
        }
        int year = ZcaCalendar.parseCalendarField(deadline, Calendar.YEAR);
        String result = "Successfully cloned all the taxpayer cases with deadline: " + ZcaCalendar.convertToMMddyyyy(deadline, "-");
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        int counter = 0;
        int total = 0;
        try {
            utx.begin();
            em = getEntityManager();
            
            //step-01: get all the PeonyTaxpayerCases in the previous year for cloning
            System.out.println(">>> step-01: get all the PeonyTaxpayerCases in the previous year for cloning");
            List<PeonyTaxpayerCase> targetPeonyTaxpayerCaseList = findPeonyTaxpayerCaseListByDeadlinePeriod(em, ZcaCalendar.createDate(year-1, Calendar.JANUARY, 1, 0, 0, 0), ZcaCalendar.createDate(year-1, Calendar.DECEMBER, 1, 0, 0, 0));
            //step-02: get all the possible cloned case
            System.out.println(">>> step-02: get all the possible cloned case");
            TreeSet<String> clonedTaxpayerCaseTitleSet = findClonedTaxpayerCaseTitleSetByDeadlinePeriod(em, ZcaCalendar.createDate(year, Calendar.JANUARY, 1, 0, 0, 0), ZcaCalendar.createDate(year, Calendar.DECEMBER, 1, 0, 0, 0));
            //step-03: clone the instances which are NOT in the step-02...
            System.out.println(">>> step-03: clone and store the instances which are NOT in the step-02...");
            if (targetPeonyTaxpayerCaseList != null){
                total = targetPeonyTaxpayerCaseList.size();
                for (PeonyTaxpayerCase aPeonyTaxpayerCase : targetPeonyTaxpayerCaseList){
                    counter++;
                    if (clonedTaxpayerCaseTitleSet.contains(aPeonyTaxpayerCase.getPrimaryTaxpayerSsn())){
                        System.out.println(">>> skip the cloned-instance..." + counter + "/" + total);
                    }else{
                        System.out.println(">>> clone ans store the instance..." + counter + "/" + total);
                        aPeonyTaxpayerCase = clonePeonyTaxpayerCaseInstance(aPeonyTaxpayerCase, deadline);
                        storePeonyTaxpayerCaseHelper(em,aPeonyTaxpayerCase );
                    }
                }
            }
            utx.commit();
            result += " (" + counter + "/" + total + " of taxpayer case was/were cloned.)";
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseTaxpayerEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            result = "Technical error raised: " + counter + " of taxpayer case was/were cloned.";
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return result;
    }
    
    private List<PeonyTaxpayerCase> findPeonyTaxpayerCaseListByDeadlinePeriod(EntityManager em, Date fromDate, Date toDate) {
        List<PeonyTaxpayerCase> result = new ArrayList<>();
        String sqlQuery = "SELECT g FROM G02TaxpayerCase g WHERE g.deadline BETWEEN :fromDate AND :toDate ";
        HashMap<String, Object> params = new HashMap<>();
        params.put("fromDate", fromDate);
        params.put("toDate", toDate);
        List<G02TaxpayerCase> aG02TaxpayerCaseList = GardenJpaUtils.findEntityListByQuery(em, G02TaxpayerCase.class, sqlQuery, params);
        if (aG02TaxpayerCaseList != null){
            PeonyTaxpayerCase aPeonyTaxpayerCase;
            //int counter = 0;
            for (G02TaxpayerCase aG02TaxpayerCase : aG02TaxpayerCaseList){
                //System.out.println(" -Clone PeonyTaxpayerCase- " + (counter++));
                aPeonyTaxpayerCase = constructPeonyTaxpayerCaseByG02TaxpayerCaseForClone(em, aG02TaxpayerCase);
                if (aPeonyTaxpayerCase != null){
                    result.add(aPeonyTaxpayerCase);
                }
            }
        }
        return result;
    }

    public static PeonyTaxpayerCase constructPeonyTaxpayerCaseByG02TaxpayerCaseForClone(EntityManager em, G02TaxpayerCase aG02TaxpayerCase)  {
        PeonyTaxpayerCase result = new PeonyTaxpayerCase();
        //taxpayer case
        result.setTaxpayerCase(aG02TaxpayerCase);
        
        String taxpayerCaseUuid = aG02TaxpayerCase.getTaxpayerCaseUuid();
        HashMap<String, Object> params = new HashMap<>();
        //primary location
        String sqlQuery = "SELECT g FROM G02Location g WHERE g.entityUuid = :entityUuid AND g.entityType = :entityType";
        params.clear();
        params.put("entityUuid", taxpayerCaseUuid);
        params.put("entityType", GardenEntityType.TAXPAYER_CASE.name());
        List<G02Location> locationList = GardenJpaUtils.findEntityListByQuery(em, G02Location.class, sqlQuery, params);
        if ((locationList != null) && (!locationList.isEmpty())){
            GardenSorter.sortLocationByCreated(locationList, true);
            result.setPrimaryLocation(locationList.get(0));
        }
        //taxpayers-info
        params.clear();
        params.put("taxpayerCaseUuid", taxpayerCaseUuid);
        result.setTaxpayerInfoList(GardenJpaUtils.findEntityListByNamedQuery(em, G02TaxpayerInfo.class, "G02TaxpayerInfo.findByTaxpayerCaseUuid", params));
        //schedule-c
        params.clear();
        params.put("taxpayerCaseUuid", taxpayerCaseUuid);
        result.setPersonalBusinessPropertyList(GardenJpaUtils.findEntityListByNamedQuery(em, G02PersonalBusinessProperty.class, "G02PersonalBusinessProperty.findByTaxpayerCaseUuid", params));
        //schedule-e
        params.clear();
        params.put("taxpayerCaseUuid", taxpayerCaseUuid);
        result.setPersonalPropertyList(GardenJpaUtils.findEntityListByNamedQuery(em, G02PersonalProperty.class, "G02PersonalProperty.findByTaxpayerCaseUuid", params));
        //TLC
        params.clear();
        params.put("taxpayerCaseUuid", taxpayerCaseUuid);
        result.setTlcLicenseList(GardenJpaUtils.findEntityListByNamedQuery(em, G02TlcLicense.class, "G02TlcLicense.findByTaxpayerCaseUuid", params));
        //aG02DocumentTagList
        params.clear();
        params.put("fileUuid", taxpayerCaseUuid);
        result.setDocumentTagList(GardenJpaUtils.findEntityListByNamedQuery(em, G02DocumentTag.class, "G02DocumentTag.findByFileUuid", params));

////        //memo
////        result.setPeonyMemoList(findPeonyMemoListByEntityUuid(em, taxpayerCaseUuid));
////
////        //bill and payments list
////        result.setPeonyBillPaymentList(findPeonyBillPaymentListByEntityUuid(em, taxpayerCaseUuid));
////
////        //archived file list
////        result.setPeonyArchivedFileList(findPeonyArchivedFileListByEntityUuid(em, taxpayerCaseUuid));
////
////        //related email tags
////        result.setRelatedEmails(findeRelatedEmailListTaxcaseUuid(em, taxpayerCaseUuid));
            
////        }
        
        return result;
    }
    
    /**
     * Help avoid duplicated cloning
     * @param em
     * @param fromDate
     * @param toDate
     * @return 
     */
    private TreeSet<String> findClonedTaxpayerCaseTitleSetByDeadlinePeriod(EntityManager em, Date fromDate, Date toDate) {
        TreeSet<String> result = new TreeSet<>();
        
        List<PeonyTaxpayerCase> aPeonyTaxpayerCaseList = findPeonyTaxpayerCaseListByDeadlinePeriod(em, fromDate, toDate);
        if (aPeonyTaxpayerCaseList != null){
            for (PeonyTaxpayerCase aPeonyTaxpayerCase : aPeonyTaxpayerCaseList){
                result.add(aPeonyTaxpayerCase.getPrimaryTaxpayerSsn());
            }
        }
        
        return result;
    }
    
    private G02Location cloneG02Location(G02Location targetG02Location, PeonyTaxpayerCase clonedPeonyTaxpayerCase){
        G02Location clonedG02Location = clonedPeonyTaxpayerCase.getPrimaryLocation();
        if (clonedG02Location == null){
            clonedG02Location = new G02Location();
            clonedPeonyTaxpayerCase.setPrimaryLocation(clonedG02Location);
        }
        clonedG02Location.setCreated(clonedPeonyTaxpayerCase.getTaxpayerCase().getCreated());
        clonedG02Location.setEntityUuid(clonedPeonyTaxpayerCase.getTaxpayerCase().getTaxpayerCaseUuid());
        clonedG02Location.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
        clonedG02Location.setLocationUuid(ZcaUtils.generateUUIDString());
        //clone part
        clonedG02Location.setCountry(targetG02Location.getCountry());
        clonedG02Location.setLocalAddress(targetG02Location.getLocalAddress());
        clonedG02Location.setCityName(targetG02Location.getCityName());
        clonedG02Location.setStateCounty(targetG02Location.getStateCounty());
        clonedG02Location.setStateName(targetG02Location.getStateName());
        clonedG02Location.setZipCode(targetG02Location.getZipCode());
        clonedG02Location.setPreferencePriority(targetG02Location.getPreferencePriority());
        clonedG02Location.setShortMemo(targetG02Location.getShortMemo());
        return clonedG02Location;
    }

    private PeonyTaxpayerCase clonePeonyTaxpayerCaseInstance(PeonyTaxpayerCase targetPeonyTaxpayerCase, Date expectedDeadline) {
        PeonyTaxpayerCase clonedPeonyTaxpayerCase = new PeonyTaxpayerCase(); 
        G02TaxpayerCase targetG02TaxpayerCase = targetPeonyTaxpayerCase.getTaxpayerCase();
        
        //taxpayer case
        G02TaxpayerCase clonedG02TaxpayerCase = clonedPeonyTaxpayerCase.getTaxpayerCase();
        clonedG02TaxpayerCase.setAgreementSignature(null);
        clonedG02TaxpayerCase.setAgreementSignatureTimestamp(null);
        clonedG02TaxpayerCase.setAgreementUuid(null);
        clonedG02TaxpayerCase.setCreated(new Date());
        clonedG02TaxpayerCase.setDeadline(expectedDeadline);
        clonedG02TaxpayerCase.setExtension(null);
        clonedG02TaxpayerCase.setEntityStatus(null);
        clonedG02TaxpayerCase.setLatestLogUuid(null);
        clonedG02TaxpayerCase.setTaxpayerCaseUuid(ZcaUtils.generateUUIDString());
        //clone part
        clonedG02TaxpayerCase.setBankAccountNumber(targetG02TaxpayerCase.getBankAccountNumber());
        clonedG02TaxpayerCase.setBankNote(targetG02TaxpayerCase.getBankNote());
        clonedG02TaxpayerCase.setBankRoutingNumber(targetG02TaxpayerCase.getBankRoutingNumber());
        clonedG02TaxpayerCase.setContact(targetG02TaxpayerCase.getContact());
        clonedG02TaxpayerCase.setCustomerAccountUuid(targetG02TaxpayerCase.getCustomerAccountUuid());
        clonedG02TaxpayerCase.setFederalFilingStatus(targetG02TaxpayerCase.getFederalFilingStatus());
        clonedG02TaxpayerCase.setMemo(targetG02TaxpayerCase.getMemo());
        
        //primary location
        cloneG02Location(targetPeonyTaxpayerCase.getPrimaryLocation(), clonedPeonyTaxpayerCase);
        
        //taxpayers-info
        List<G02TaxpayerInfo> targetG02TaxpayerInfoList = targetPeonyTaxpayerCase.getTaxpayerInfoList();
        for (G02TaxpayerInfo targetG02TaxpayerInfo : targetG02TaxpayerInfoList){
            clonedPeonyTaxpayerCase.getTaxpayerInfoList().add(cloneG02TaxpayerInfo(clonedG02TaxpayerCase, targetG02TaxpayerInfo));
        }
        
        //schedule-e
        List<G02PersonalProperty> targetG02PersonalPropertyList = targetPeonyTaxpayerCase.getPersonalPropertyList();
        for (G02PersonalProperty targetG02PersonalProperty : targetG02PersonalPropertyList){
            clonedPeonyTaxpayerCase.getPersonalPropertyList().add(cloneG02PersonalProperty(clonedG02TaxpayerCase, targetG02PersonalProperty));
        }
        
        //schedule-c
        List<G02PersonalBusinessProperty> targetG02PersonalBusinessPropertyList = targetPeonyTaxpayerCase.getPersonalBusinessPropertyList();
        for (G02PersonalBusinessProperty targetG02PersonalBusinessProperty : targetG02PersonalBusinessPropertyList){
            clonedPeonyTaxpayerCase.getPersonalBusinessPropertyList().add(cloneG02PersonalBusinessProperty(clonedG02TaxpayerCase, targetG02PersonalBusinessProperty));
        }
        
        //TLC
        List<G02TlcLicense> targetG02TlcLicenseList = targetPeonyTaxpayerCase.getTlcLicenseList();
        for (G02TlcLicense targetG02TlcLicensey : targetG02TlcLicenseList){
            clonedPeonyTaxpayerCase.getTlcLicenseList().add(cloneG02TlcLicense(clonedG02TaxpayerCase, targetG02TlcLicensey));
        }
        
        //aG02DocumentTagList
        List<G02DocumentTag> targetG02DocumentTagList = targetPeonyTaxpayerCase.getDocumentTagList();
        for (G02DocumentTag targetG02DocumentTag : targetG02DocumentTagList){
            if (ZcaValidator.isNotNullEmpty(targetG02DocumentTag.getDocumentTagType())){//after 2020, every tag should have tag type
                clonedPeonyTaxpayerCase.getDocumentTagList().add(cloneG02DocumentTag(clonedG02TaxpayerCase, targetG02DocumentTag));
            }
        }
        
        clonedPeonyTaxpayerCase.setPeonyMemoList(new ArrayList<>());
        
        //todo zzj: here may involves other data fields... for example, extensions
        
        return clonedPeonyTaxpayerCase;
    }

    private G02TaxpayerInfo cloneG02TaxpayerInfo(G02TaxpayerCase clonedG02TaxpayerCase, G02TaxpayerInfo targetG02TaxpayerInfo) {
        G02TaxpayerInfo clonedG02TaxpayerInfo = new G02TaxpayerInfo();
        clonedG02TaxpayerInfo.setCreated(clonedG02TaxpayerCase.getCreated());
        clonedG02TaxpayerInfo.setTaxpayerUserUuid(ZcaUtils.generateUUIDString());
        clonedG02TaxpayerInfo.setTaxpayerCaseUuid(clonedG02TaxpayerCase.getTaxpayerCaseUuid());
        
        clonedG02TaxpayerInfo.setFirstName(targetG02TaxpayerInfo.getFirstName());
        clonedG02TaxpayerInfo.setMiddleName(targetG02TaxpayerInfo.getMiddleName());
        clonedG02TaxpayerInfo.setLastName(targetG02TaxpayerInfo.getLastName());
        clonedG02TaxpayerInfo.setGender(targetG02TaxpayerInfo.getGender());
        clonedG02TaxpayerInfo.setRelationships(targetG02TaxpayerInfo.getRelationships());
        clonedG02TaxpayerInfo.setOccupation(targetG02TaxpayerInfo.getOccupation());
        clonedG02TaxpayerInfo.setSsn(targetG02TaxpayerInfo.getSsn());
        clonedG02TaxpayerInfo.setCitizenship(targetG02TaxpayerInfo.getCitizenship());
        clonedG02TaxpayerInfo.setBirthday(targetG02TaxpayerInfo.getBirthday());
        clonedG02TaxpayerInfo.setAgeAsOfFirstDay(targetG02TaxpayerInfo.getAgeAsOfFirstDay());
        clonedG02TaxpayerInfo.setDateOfDeath(targetG02TaxpayerInfo.getDateOfDeath());
        clonedG02TaxpayerInfo.setLegallyBlind(targetG02TaxpayerInfo.getLegallyBlind());
        clonedG02TaxpayerInfo.setEducationCost(targetG02TaxpayerInfo.getEducationCost());
        clonedG02TaxpayerInfo.setLengthOfLivingTogether(targetG02TaxpayerInfo.getLengthOfLivingTogether());
        clonedG02TaxpayerInfo.setEmail(targetG02TaxpayerInfo.getEmail());
        clonedG02TaxpayerInfo.setMobilePhone(targetG02TaxpayerInfo.getMobilePhone());
        clonedG02TaxpayerInfo.setHomePhone(targetG02TaxpayerInfo.getHomePhone());
        clonedG02TaxpayerInfo.setWorkPhone(targetG02TaxpayerInfo.getWorkPhone());
        clonedG02TaxpayerInfo.setSocialNetworkId(targetG02TaxpayerInfo.getSocialNetworkId());
        clonedG02TaxpayerInfo.setSocialNetworkType(targetG02TaxpayerInfo.getSocialNetworkType());
        clonedG02TaxpayerInfo.setFax(targetG02TaxpayerInfo.getFax());
        clonedG02TaxpayerInfo.setMemo(targetG02TaxpayerInfo.getMemo());
        clonedG02TaxpayerInfo.setEntityStatus(targetG02TaxpayerInfo.getEntityStatus());
            
        return clonedG02TaxpayerInfo;
    }

    private G02PersonalProperty cloneG02PersonalProperty(G02TaxpayerCase clonedG02TaxpayerCase, G02PersonalProperty targetG02PersonalProperty) {
        G02PersonalProperty clonedG02PersonalProperty = new G02PersonalProperty();
        clonedG02PersonalProperty.setCreated(clonedG02TaxpayerCase.getCreated());
        clonedG02PersonalProperty.setTaxpayerCaseUuid(clonedG02TaxpayerCase.getTaxpayerCaseUuid());
        clonedG02PersonalProperty.setPersonalPropertyUuid(ZcaUtils.generateUUIDString());
        
        clonedG02PersonalProperty.setDateOnImprovement(targetG02PersonalProperty.getDateOnImprovement());
        clonedG02PersonalProperty.setDateOnService(targetG02PersonalProperty.getDateOnService());
        clonedG02PersonalProperty.setExpenseAdvertising(targetG02PersonalProperty.getExpenseAdvertising());
        clonedG02PersonalProperty.setExpenseAutoAndTravel(targetG02PersonalProperty.getExpenseAutoAndTravel());
        clonedG02PersonalProperty.setExpenseCleaning(targetG02PersonalProperty.getExpenseCleaning());
        clonedG02PersonalProperty.setExpenseCommissions(targetG02PersonalProperty.getExpenseCommissions());
        clonedG02PersonalProperty.setExpenseDepreciation(targetG02PersonalProperty.getExpenseDepreciation());
        clonedG02PersonalProperty.setExpenseInsurance(targetG02PersonalProperty.getExpenseInsurance());
        clonedG02PersonalProperty.setExpenseMgtFee(targetG02PersonalProperty.getExpenseMgtFee());
        clonedG02PersonalProperty.setExpenseMorgageInterest(targetG02PersonalProperty.getExpenseMorgageInterest());
        clonedG02PersonalProperty.setExpenseOthers(targetG02PersonalProperty.getExpenseOthers());
        clonedG02PersonalProperty.setExpenseProfServices(targetG02PersonalProperty.getExpenseProfServices());
        clonedG02PersonalProperty.setExpenseReTaxes(targetG02PersonalProperty.getExpenseReTaxes());
        clonedG02PersonalProperty.setExpenseRepairs(targetG02PersonalProperty.getExpenseRepairs());
        clonedG02PersonalProperty.setExpenseSupplies(targetG02PersonalProperty.getExpenseSupplies());
        clonedG02PersonalProperty.setExpenseUtilities(targetG02PersonalProperty.getExpenseUtilities());
        clonedG02PersonalProperty.setExpenseWaterSewer(targetG02PersonalProperty.getExpenseWaterSewer());
        clonedG02PersonalProperty.setImprovementCost(targetG02PersonalProperty.getImprovementCost());
        clonedG02PersonalProperty.setIncomeRentsReceieved(targetG02PersonalProperty.getIncomeRentsReceieved());
        clonedG02PersonalProperty.setPercentageOfOwnership(targetG02PersonalProperty.getPercentageOfOwnership());
        clonedG02PersonalProperty.setPercentageOfRentalUse(targetG02PersonalProperty.getPercentageOfRentalUse());
        clonedG02PersonalProperty.setPropertyAddress(targetG02PersonalProperty.getPropertyAddress());
        clonedG02PersonalProperty.setPropertyType(targetG02PersonalProperty.getPropertyType());
        clonedG02PersonalProperty.setPurchasePrice(targetG02PersonalProperty.getPurchasePrice());
        clonedG02PersonalProperty.setMemo(targetG02PersonalProperty.getMemo());
        clonedG02PersonalProperty.setEntityStatus(targetG02PersonalProperty.getEntityStatus());
        
        return clonedG02PersonalProperty;
    }

    private G02PersonalBusinessProperty cloneG02PersonalBusinessProperty(G02TaxpayerCase clonedG02TaxpayerCase, G02PersonalBusinessProperty targetG02PersonalBusinessProperty) {
        G02PersonalBusinessProperty clonedG02PersonalBusinessProperty = new G02PersonalBusinessProperty();
        clonedG02PersonalBusinessProperty.setCreated(clonedG02TaxpayerCase.getCreated());
        clonedG02PersonalBusinessProperty.setTaxpayerCaseUuid(clonedG02TaxpayerCase.getTaxpayerCaseUuid());
        clonedG02PersonalBusinessProperty.setPersonalBusinessPropertyUuid(ZcaUtils.generateUUIDString());

        clonedG02PersonalBusinessProperty.setBusinessDescription(targetG02PersonalBusinessProperty.getBusinessDescription());
        clonedG02PersonalBusinessProperty.setBusinessPropertyName(targetG02PersonalBusinessProperty.getBusinessPropertyName());
        clonedG02PersonalBusinessProperty.setBusinessAddress(targetG02PersonalBusinessProperty.getBusinessAddress());
        clonedG02PersonalBusinessProperty.setBusinessDescription(targetG02PersonalBusinessProperty.getBusinessDescription());
        clonedG02PersonalBusinessProperty.setBusinessEin(targetG02PersonalBusinessProperty.getBusinessEin());
        clonedG02PersonalBusinessProperty.setBusinessOwnership(targetG02PersonalBusinessProperty.getBusinessOwnership());
        clonedG02PersonalBusinessProperty.setGrossReceiptsSales(targetG02PersonalBusinessProperty.getGrossReceiptsSales());
        clonedG02PersonalBusinessProperty.setCostOfGoodsSold(targetG02PersonalBusinessProperty.getCostOfGoodsSold());
        clonedG02PersonalBusinessProperty.setOtherIncome(targetG02PersonalBusinessProperty.getOtherIncome());
        clonedG02PersonalBusinessProperty.setExpenseCableInternet(targetG02PersonalBusinessProperty.getExpenseCableInternet());
        clonedG02PersonalBusinessProperty.setExpenseCarAndTruck(targetG02PersonalBusinessProperty.getExpenseCarAndTruck());
        clonedG02PersonalBusinessProperty.setExpenseCommissions(targetG02PersonalBusinessProperty.getExpenseCommissions());
        clonedG02PersonalBusinessProperty.setExpenseContractLabor(targetG02PersonalBusinessProperty.getExpenseContractLabor());
        clonedG02PersonalBusinessProperty.setExpenseOffice(targetG02PersonalBusinessProperty.getExpenseOffice());
        clonedG02PersonalBusinessProperty.setExpenseRentLease(targetG02PersonalBusinessProperty.getExpenseRentLease());
        clonedG02PersonalBusinessProperty.setExpenseTelephone(targetG02PersonalBusinessProperty.getExpenseTelephone());
        clonedG02PersonalBusinessProperty.setExpenseTravelMeals(targetG02PersonalBusinessProperty.getExpenseTravelMeals());
        clonedG02PersonalBusinessProperty.setExpenseAdvertising(targetG02PersonalBusinessProperty.getExpenseAdvertising());
        clonedG02PersonalBusinessProperty.setExpenseInsurance(targetG02PersonalBusinessProperty.getExpenseInsurance());
        clonedG02PersonalBusinessProperty.setExpenseOthers(targetG02PersonalBusinessProperty.getExpenseOthers());
        clonedG02PersonalBusinessProperty.setExpenseProfServices(targetG02PersonalBusinessProperty.getExpenseProfServices());
        clonedG02PersonalBusinessProperty.setExpenseRepairs(targetG02PersonalBusinessProperty.getExpenseRepairs());
        clonedG02PersonalBusinessProperty.setExpenseSupplies(targetG02PersonalBusinessProperty.getExpenseSupplies());
        clonedG02PersonalBusinessProperty.setExpenseUtilities(targetG02PersonalBusinessProperty.getExpenseUtilities());
        clonedG02PersonalBusinessProperty.setMemo(targetG02PersonalBusinessProperty.getMemo());
        clonedG02PersonalBusinessProperty.setEntityStatus(targetG02PersonalBusinessProperty.getEntityStatus());
        
        return clonedG02PersonalBusinessProperty;
    }

    private G02DocumentTag cloneG02DocumentTag(G02TaxpayerCase clonedG02TaxpayerCase, G02DocumentTag targetG02DocumentTag) {
        G02DocumentTag clonedG02DocumentTag = new G02DocumentTag();
        clonedG02DocumentTag.setFileUuid(clonedG02TaxpayerCase.getTaxpayerCaseUuid());
        clonedG02DocumentTag.setDocumentTagUuid(ZcaUtils.generateUUIDString());
        
        clonedG02DocumentTag.setDocumentQuantity(targetG02DocumentTag.getDocumentQuantity());
        clonedG02DocumentTag.setDocumentTagName(targetG02DocumentTag.getDocumentTagName());
        clonedG02DocumentTag.setDocumentTagType(targetG02DocumentTag.getDocumentTagType());
        //existingEntityObj.setFileUuid(entityObj.getFileUuid());
        clonedG02DocumentTag.setMemo(targetG02DocumentTag.getMemo());
        
        return clonedG02DocumentTag;
    }

    private G02TlcLicense cloneG02TlcLicense(G02TaxpayerCase clonedG02TaxpayerCase, G02TlcLicense targetG02TlcLicensey) {
        G02TlcLicense clonedG02TlcLicensey = new G02TlcLicense();
        clonedG02TlcLicensey.setTaxpayerCaseUuid(clonedG02TaxpayerCase.getTaxpayerCaseUuid());
        clonedG02TlcLicensey.setCreated(clonedG02TaxpayerCase.getCreated());
        clonedG02TlcLicensey.setDriverUuid(ZcaUtils.generateUUIDString());
        
        clonedG02TlcLicensey.setAccessories(targetG02TlcLicensey.getAccessories());
        clonedG02TlcLicensey.setBusinessMiles(targetG02TlcLicensey.getBusinessMiles());
        clonedG02TlcLicensey.setCarWash(targetG02TlcLicensey.getCarWash());
        clonedG02TlcLicensey.setDateInService(targetG02TlcLicensey.getDateInService());
        clonedG02TlcLicensey.setDepreciation(targetG02TlcLicensey.getDepreciation());
        clonedG02TlcLicensey.setGarageRent(targetG02TlcLicensey.getGarageRent());
        clonedG02TlcLicensey.setGas(targetG02TlcLicensey.getGas());
        clonedG02TlcLicensey.setInsurance(targetG02TlcLicensey.getInsurance());
        clonedG02TlcLicensey.setLeasePayment(targetG02TlcLicensey.getLeasePayment());
        clonedG02TlcLicensey.setMaintenance(targetG02TlcLicensey.getMaintenance());
        clonedG02TlcLicensey.setMeals(targetG02TlcLicensey.getMeals());
        clonedG02TlcLicensey.setMileageRate(targetG02TlcLicensey.getMileageRate());
        clonedG02TlcLicensey.setNumberOfSeats(targetG02TlcLicensey.getNumberOfSeats());
        clonedG02TlcLicensey.setOil(targetG02TlcLicensey.getOil());
        clonedG02TlcLicensey.setOver600lbs(targetG02TlcLicensey.getOver600lbs());
        clonedG02TlcLicensey.setParking(targetG02TlcLicensey.getParking());
        clonedG02TlcLicensey.setRadioRepair(targetG02TlcLicensey.getRadioRepair());
        clonedG02TlcLicensey.setRegistrationFee(targetG02TlcLicensey.getRegistrationFee());
        clonedG02TlcLicensey.setRepairs(targetG02TlcLicensey.getRepairs());
        clonedG02TlcLicensey.setServiceFee(targetG02TlcLicensey.getServiceFee());
        clonedG02TlcLicensey.setTelephone(targetG02TlcLicensey.getTelephone());
        clonedG02TlcLicensey.setTires(targetG02TlcLicensey.getTires());
        clonedG02TlcLicensey.setTlcLicense(targetG02TlcLicensey.getTlcLicense());
        clonedG02TlcLicensey.setTolls(targetG02TlcLicensey.getTolls());
        clonedG02TlcLicensey.setTotalMiles(targetG02TlcLicensey.getTotalMiles());
        clonedG02TlcLicensey.setUniform(targetG02TlcLicensey.getUniform());
        clonedG02TlcLicensey.setVehicleModel(targetG02TlcLicensey.getVehicleModel());
        clonedG02TlcLicensey.setVehicleType(targetG02TlcLicensey.getVehicleType());
        clonedG02TlcLicensey.setMemo(targetG02TlcLicensey.getMemo());
        clonedG02TlcLicensey.setEntityStatus(targetG02TlcLicensey.getEntityStatus());
        
        return clonedG02TlcLicensey;
    }
}
