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

import com.zcomapproach.garden.data.constant.SearchTaxcorpCriteria;
import com.zcomapproach.garden.data.constant.TaxcorpEntityDateField;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.persistence.GardenEntityConverter;
import com.zcomapproach.garden.persistence.GardenJpaUtils;
import static com.zcomapproach.garden.persistence.GardenJpaUtils.findeRelatedEmailListTaxcaseUuid;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.constant.TaxFilingPeriod;
import com.zcomapproach.garden.persistence.constant.TaxFilingType;
import com.zcomapproach.garden.persistence.constant.TaxcorpBusinessStatus;
import com.zcomapproach.garden.persistence.entity.G02Bill;
import com.zcomapproach.garden.persistence.entity.G02BusinessContactor;
import com.zcomapproach.garden.persistence.entity.G02Payment;
import com.zcomapproach.garden.persistence.entity.G02TaxFilingCase;
import com.zcomapproach.garden.persistence.entity.G02TaxcorpCase;
import com.zcomapproach.garden.persistence.entity.G02TaxcorpCaseBk;
import com.zcomapproach.garden.persistence.entity.G02User;
import com.zcomapproach.garden.persistence.peony.PeonyBillPayment;
import com.zcomapproach.garden.persistence.peony.PeonyTaxFilingCase;
import com.zcomapproach.garden.persistence.peony.PeonyTaxFilingCaseList;
import com.zcomapproach.garden.persistence.peony.PeonyTaxcorpCase;
import com.zcomapproach.garden.persistence.peony.PeonyTaxcorpCaseList;
import com.zcomapproach.garden.persistence.peony.TaxcorpCaseBrief;
import com.zcomapproach.garden.persistence.peony.TaxcorpCaseBriefList;
import com.zcomapproach.garden.persistence.peony.data.TaxcorpCaseSearchResult;
import com.zcomapproach.garden.persistence.peony.data.TaxcorpCaseSearchResultList;
import com.zcomapproach.garden.persistence.peony.data.TaxcorpTaxFilingCaseSearchResult;
import com.zcomapproach.garden.persistence.peony.data.TaxcorpTaxFilingCaseSearchResultList;
import com.zcomapproach.garden.persistence.updater.G02DataUpdaterFactory;
import com.zcomapproach.garden.taxation.TaxationDeadline02;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import com.zcomapproach.garden.util.GardenSorter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
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
public class RoseTaxcorpEJB02 extends AbstractDataServiceEJB02 {
    
    public PeonyTaxFilingCaseList generatePeonyTaxFilingCaseListForTaxcorp(String taxcorpUuid, 
                                                                            String taxFilingType, 
                                                                            String taxFilingPeriod)
            throws Exception
    {
        PeonyTaxFilingCaseList result = new PeonyTaxFilingCaseList();
        if (ZcaValidator.isNullEmpty(taxcorpUuid)){
            return result;
        }
        TaxFilingType aTaxFilingType = TaxFilingType.convertEnumValueToType(taxFilingType);
        if (TaxFilingType.UNKNOWN.equals(aTaxFilingType)){
            return result;
        }
        TaxFilingPeriod aTaxFilingPeriod = TaxFilingPeriod.convertEnumValueToType(taxFilingPeriod);
        if (TaxFilingPeriod.UNKNOWN.equals(aTaxFilingPeriod)){
            return result;
        }
        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();

            G02TaxcorpCase aG02TaxcorpCase = GardenJpaUtils.findById(em, G02TaxcorpCase.class, taxcorpUuid);
            if (aG02TaxcorpCase != null){
                List<G02TaxFilingCase> aG02TaxFilingCaseList = GardenJpaUtils.findTaxFilingCaseListByTaxcorpCaseUuidAndTaxFilingType(em, taxcorpUuid, aTaxFilingType, aTaxFilingPeriod);
                GregorianCalendar theLastDeadlineDate = new GregorianCalendar();    //today
                theLastDeadlineDate.setTime(aG02TaxcorpCase.getDosDate()); //defined by DOS
                /**
                 * If aG02TaxcorpCase already generated aG02TaxFilingCaseList before, the last deadline is defined by the last one in such a list
                 */
                if ((aG02TaxFilingCaseList != null) && (!aG02TaxFilingCaseList.isEmpty())){
                    GardenSorter.sortTaxFilingCaseListByDeadline(aG02TaxFilingCaseList, true);
                    theLastDeadlineDate = ZcaCalendar.convertToGregorianCalendar(aG02TaxFilingCaseList.get(0).getDeadline());
                }
                List<Date> deadlineList = TaxationDeadline02.calculateForTaxcorp(aG02TaxcorpCase, aTaxFilingType, aTaxFilingPeriod, theLastDeadlineDate);
                if (deadlineList != null){
                    PeonyTaxFilingCase aPeonyTaxFilingCase;
                    G02TaxFilingCase aG02TaxFilingCase;
                    for (Date deadline : deadlineList){
                        aG02TaxFilingCase = new G02TaxFilingCase();
                        aG02TaxFilingCase.setCreated(new Date());
                        aG02TaxFilingCase.setDeadline(deadline);
                        //deprecated: not existing anymore//aG02TaxFilingCase.setEntityStatus(null);
                        //deprecated: not existing anymore//aG02TaxFilingCase.setEntityStatusTimestamp(null);
                        aG02TaxFilingCase.setEntityType(GardenEntityType.TAXCORP_CASE.name());
                        aG02TaxFilingCase.setEntityUuid(taxcorpUuid);
                        aG02TaxFilingCase.setTaxFilingPeriod(taxFilingPeriod);
                        aG02TaxFilingCase.setTaxFilingType(taxFilingType);
                        aG02TaxFilingCase.setTaxFilingUuid(GardenData.generateUUIDString());
                        aG02TaxFilingCase.setUpdated(new Date());
                        //save....
                        GardenJpaUtils.storeEntity(em, G02TaxFilingCase.class, aG02TaxFilingCase, aG02TaxFilingCase.getTaxFilingUuid(), G02DataUpdaterFactory.getSingleton().getG02TaxFilingCaseUpdater());
                        aPeonyTaxFilingCase = new PeonyTaxFilingCase();
                        aPeonyTaxFilingCase.setTaxFilingCase(aG02TaxFilingCase);
                        result.getPeonyTaxFilingCaseList().add(aPeonyTaxFilingCase);
                    }//for
                }
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

    /**
     * 
     * @param einNumber
     * @return - if not found in both taxcorp table and taxcorp-bk, NULL returned
     */
    public G02TaxcorpCase findTaxcorpCaseByEinNumber(String einNumber) {
        if (ZcaValidator.isNullEmpty(einNumber)){
            return null;
        }
        G02TaxcorpCase result = null;
        
        EntityManager em = getEntityManager();
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("einNumber", einNumber);
            List<G02TaxcorpCase> aG02TaxcorpCaseList = GardenJpaUtils.findEntityListByNamedQuery(em, G02TaxcorpCase.class, "G02TaxcorpCase.findByEinNumber", params);
            if ((aG02TaxcorpCaseList == null) || (aG02TaxcorpCaseList.isEmpty())){
                //todo zzj: is this neccessary? or just for top-manager?
                //result = findPeonyTaxcorpCaseByEinNumberFromBackup(em, einNumber);
            }else{
                result = aG02TaxcorpCaseList.get(0);
            }
        } finally {
            em.close();
        }
        return result;
    }
    
     public PeonyTaxcorpCase findPeonyTaxcorpCaseByTaxcorpCaseUuid(String taxcorpCaseUuid) {
        if (ZcaValidator.isNullEmpty(taxcorpCaseUuid)){
            return null;
        }
        PeonyTaxcorpCase result = null;
        
        EntityManager em = getEntityManager();
        try {
            //taxcorp case
            G02TaxcorpCase aG02TaxcorpCase = GardenJpaUtils.findById(em, G02TaxcorpCase.class, taxcorpCaseUuid);
            if (aG02TaxcorpCase == null){
                //todo zzj: try to retrieve it from its BK table?
            }else{
                result = constructPeonyTaxcorpCaseProfile(em, aG02TaxcorpCase);
            }
        } finally {
            em.close();
        }
        return result;
    }
    /**
     * Load all the relative information on aG02TaxcorpCase
     * @param aG02TaxcorpCase - it assume this instance has been found
     * @return 
     */
    private PeonyTaxcorpCase constructPeonyTaxcorpCaseProfile(EntityManager em, G02TaxcorpCase aG02TaxcorpCase) {
        
        PeonyTaxcorpCase result = new PeonyTaxcorpCase();
        result.setTaxcorpCase(aG02TaxcorpCase);
        //retrieve other information
        String taxcorpCaseUuid = aG02TaxcorpCase.getTaxcorpCaseUuid();
        //customer
        result.setCustomer(GardenJpaUtils.findPeonyAccountByAccountUuid(em, aG02TaxcorpCase.getCustomerAccountUuid()));
        //representatives
        result.setBusinessContactorList(GardenJpaUtils.findBusinessContactorListByEntityUuid(em, taxcorpCaseUuid));
////        //tax filing cases sorted by deadline
////        result.setTaxFilingCaseList(GardenJpaUtils.findTaxFilingCaseListByEntityUuid(em, taxcorpCaseUuid));
////        Collections.sort(result.getTaxFilingCaseList(), (G02TaxFilingCase o1, G02TaxFilingCase o2) -> {
////            try{
////                return o1.getDeadline().compareTo(o2.getDeadline())*(-1);
////            }catch(Exception ex){
////                return 0;
////            }
////        });
        //memo
        result.setPeonyMemoList(GardenJpaUtils.findPeonyMemoListByEntityUuid(em, taxcorpCaseUuid));
        //bill and payments list
        result.setPeonyBillPaymentList(GardenJpaUtils.findPeonyBillPaymentListByEntityUuid(em, taxcorpCaseUuid));
        //archived file list
        result.setPeonyArchivedFileList(GardenJpaUtils.findPeonyArchivedFileListByEntityUuid(em, taxcorpCaseUuid));
        //related email tags
        result.setRelatedEmails(findeRelatedEmailListTaxcaseUuid(em, taxcorpCaseUuid));
        
        return result;
    }

    public PeonyTaxcorpCaseList findPeonyTaxcorpCaseListByCorpName(String corpName) {
        PeonyTaxcorpCaseList result = new PeonyTaxcorpCaseList();
        if (ZcaValidator.isNullEmpty(corpName)){
            return result;
        }
        EntityManager em = getEntityManager();
        try {
            List<G02TaxcorpCase> aG02TaxcorpCaseList = GardenJpaUtils.findAll(em, G02TaxcorpCase.class);
            if (aG02TaxcorpCaseList != null){
                
                String persistentTaxcorpFeatureValue;
                double score;
                for (G02TaxcorpCase aG02TaxcorpCase : aG02TaxcorpCaseList){
                    persistentTaxcorpFeatureValue = retrievePersistentTaxcorpFeatureValue(SearchTaxcorpCriteria.corporate_name.value(), aG02TaxcorpCase);
                    if (ZcaValidator.isNotNullEmpty(persistentTaxcorpFeatureValue)){
                        score = GardenData.calculateSimilarityByJaroWinklerStrategy(persistentTaxcorpFeatureValue, corpName, false, 0.9);
                        if (score > 0.9){
                            result.getPeonyTaxcorpCaseList().add(constructPeonyTaxcorpCaseProfile(em, aG02TaxcorpCase));
                        }
                    }
                }//for-loop
            }
        } finally {
            em.close();
        }
        return result;
    }
    /**
     * 
     * @param einNumber
     * @return - if not found in both taxcorp table and taxcorp-bk, NULL returned
     */
    public PeonyTaxcorpCase findPeonyTaxcorpCaseByEinNumber(String einNumber) {
        if (ZcaValidator.isNullEmpty(einNumber)){
            return null;
        }
        PeonyTaxcorpCase result = null;
        
        EntityManager em = getEntityManager();
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("einNumber", einNumber);
            List<G02TaxcorpCase> aG02TaxcorpCaseList = GardenJpaUtils.findEntityListByNamedQuery(em, G02TaxcorpCase.class, "G02TaxcorpCase.findByEinNumber", params);
            if ((aG02TaxcorpCaseList == null) || (aG02TaxcorpCaseList.isEmpty())){
                //retrieve deleted/finalized cases
                result = findPeonyTaxcorpCaseByEinNumberFromBackup(em, einNumber);
            }else{
                result = constructPeonyTaxcorpCaseProfile(em, aG02TaxcorpCaseList.get(0));    //todo zzj: only the first one. Fix redandancy?
            }
        } finally {
            em.close();
        }
        return result;
    }
    
    public TaxcorpCaseSearchResultList searchTaxcorpCaseSearchResultListByFeature(String taxcorpFeature, 
                                                                                String taxcorpFeatureValue, 
                                                                                boolean exactMatch) 
    {
        TaxcorpCaseSearchResultList result = new TaxcorpCaseSearchResultList();
        result.setTaxcorpFeature(taxcorpFeature);
        result.setTaxcorpFeatureValue(taxcorpFeatureValue);
        if (ZcaValidator.isNullEmpty(taxcorpFeature) || ZcaValidator.isNullEmpty(taxcorpFeatureValue)){
            return result;
        }
        EntityManager em = getEntityManager();
        try {
            List<G02TaxcorpCase> aG02TaxcorpCaseList = GardenJpaUtils.findAll(em, G02TaxcorpCase.class);
            if (aG02TaxcorpCaseList != null){
                TaxcorpCaseSearchResult aTaxcorpCaseSearchResult;
                String persistentTaxcorpFeatureValue;
                double score;
                for (G02TaxcorpCase aG02TaxcorpCase : aG02TaxcorpCaseList){
                    persistentTaxcorpFeatureValue = retrievePersistentTaxcorpFeatureValue(taxcorpFeature, aG02TaxcorpCase);
                    if (ZcaValidator.isNotNullEmpty(persistentTaxcorpFeatureValue)){
                        score = GardenData.calculateSimilarityByJaroWinklerStrategy(persistentTaxcorpFeatureValue, taxcorpFeatureValue, exactMatch, 0.75);
                        if (score > 0.75){
                            aTaxcorpCaseSearchResult = new TaxcorpCaseSearchResult();
                            aTaxcorpCaseSearchResult.setTaxcorpCase(aG02TaxcorpCase);
                            aTaxcorpCaseSearchResult.setBusinessContactorList(GardenJpaUtils.findBusinessContactorListByTaxcorpCase(em, aG02TaxcorpCase));
                            result.getTaxcorpCaseSearchResultList().add(aTaxcorpCaseSearchResult);
                        }
                    }
                }//for-loop
            }
            List<G02TaxcorpCaseBk> aG02TaxcorpCaseBkList = GardenJpaUtils.findAll(em, G02TaxcorpCaseBk.class);
            if (aG02TaxcorpCaseBkList != null){
                TaxcorpCaseSearchResult aTaxcorpCaseSearchResult;
                String persistentTaxcorpFeatureValue;
                double score;
                G02TaxcorpCase aG02TaxcorpCase;
                for (G02TaxcorpCaseBk aG02TaxcorpCaseBk : aG02TaxcorpCaseBkList){
                    aG02TaxcorpCase = GardenEntityConverter.convertToG02TaxcorpCase(aG02TaxcorpCaseBk);
                    persistentTaxcorpFeatureValue = retrievePersistentTaxcorpFeatureValue(taxcorpFeature, aG02TaxcorpCase);
                    if (ZcaValidator.isNotNullEmpty(persistentTaxcorpFeatureValue)){
                        score = GardenData.calculateSimilarityByJaroWinklerStrategy(persistentTaxcorpFeatureValue, taxcorpFeatureValue, exactMatch, 0.75);
                        if (score > 0.75){
                            aTaxcorpCaseSearchResult = new TaxcorpCaseSearchResult();
                            aTaxcorpCaseSearchResult.setTaxcorpCase(aG02TaxcorpCase);
                            aTaxcorpCaseSearchResult.setBusinessContactorList(GardenJpaUtils.findBusinessContactorListByEntityUuid(em, aG02TaxcorpCase.getTaxcorpCaseUuid()));
                            result.getTaxcorpCaseSearchResultList().add(aTaxcorpCaseSearchResult);
                        }
                    }
                }//for-loop
            }
        } finally {
            em.close();
        }
        return result;
    }
    
    private Date retrievePersistentTaxcorpDateFeatureValue(String taxcorpDateFeature, G02TaxcorpCase aG02TaxcorpCase) {
        TaxcorpEntityDateField aTaxcorpEntityDateField = TaxcorpEntityDateField.convertEnumValueToType(taxcorpDateFeature);
        if (aTaxcorpEntityDateField != null){
            switch(aTaxcorpEntityDateField){
                case CREATED_DATE:
                    return aG02TaxcorpCase.getCreated();
                case UPDATED_DATE:
                    return aG02TaxcorpCase.getUpdated();
                case DOS_DATE:
                    return aG02TaxcorpCase.getDosDate();
            }//switch
        }
        return null;
    }
    
    private String retrievePersistentTaxcorpFeatureValue(String taxcorpFeature, G02TaxcorpCase aG02TaxcorpCase) {
        SearchTaxcorpCriteria aCritera = SearchTaxcorpCriteria.convertEnumValueToType(taxcorpFeature);
        if (aCritera != null){
            switch (aCritera){
                case ein_number:
                    return aG02TaxcorpCase.getEinNumber();
                case business_type:
                    return aG02TaxcorpCase.getBusinessType();
                case corporate_name:
                    return aG02TaxcorpCase.getCorporateName();
                case corporate_email:
                    return aG02TaxcorpCase.getCorporateEmail();
                case corporate_phone:
                    return aG02TaxcorpCase.getCorporatePhone();
                case corporate_fax:
                    return aG02TaxcorpCase.getCorporateFax();
                case corporate_web_presence:
                    return aG02TaxcorpCase.getCorporateWebPresence();
                case business_purpose:
                    return aG02TaxcorpCase.getBusinessPurpose();
            }
        }
        return null;
    }

    public TaxcorpCaseSearchResultList searchTaxcorpCaseSearchResultListByDateRange(String taxcorpDateFeature, Date fromDate, Date toDate) {
        TaxcorpCaseSearchResultList result = new TaxcorpCaseSearchResultList();
        result.setTaxcorpFeature(taxcorpDateFeature);
        if (ZcaValidator.isNullEmpty(taxcorpDateFeature) || (fromDate == null) || (toDate == null)){
            return result;
        }
        result.setTaxcorpFeatureValue("From "+ZcaCalendar.convertToMMddyyyy(fromDate, "-")+" to "+ZcaCalendar.convertToMMddyyyy(toDate, "-"));
        
        EntityManager em = getEntityManager();
        try {
            List<G02TaxcorpCase> aG02TaxcorpCaseList = GardenJpaUtils.findAll(em, G02TaxcorpCase.class);
            if (aG02TaxcorpCaseList != null){
                TaxcorpCaseSearchResult aTaxcorpCaseSearchResult;
                Date persistentDateFeatureValue;
                for (G02TaxcorpCase aG02TaxcorpCase : aG02TaxcorpCaseList){
                    persistentDateFeatureValue = retrievePersistentTaxcorpDateFeatureValue(taxcorpDateFeature, aG02TaxcorpCase);
                    if (persistentDateFeatureValue != null){
                        
                        if ((persistentDateFeatureValue.equals(fromDate)) || (persistentDateFeatureValue.equals(toDate))
                                || ((persistentDateFeatureValue.after(fromDate)) && (persistentDateFeatureValue.before(toDate))))
                        {
                            aTaxcorpCaseSearchResult = new TaxcorpCaseSearchResult();
                            aTaxcorpCaseSearchResult.setTaxcorpCase(aG02TaxcorpCase);
                            aTaxcorpCaseSearchResult.setBusinessContactorList(GardenJpaUtils.findBusinessContactorListByTaxcorpCase(em, aG02TaxcorpCase));
                            result.getTaxcorpCaseSearchResultList().add(aTaxcorpCaseSearchResult);
                        }
                    }
                }//for-loop
            }
            List<G02TaxcorpCaseBk> aG02TaxcorpCaseBkList = GardenJpaUtils.findAll(em, G02TaxcorpCaseBk.class);
            if (aG02TaxcorpCaseBkList != null){
                TaxcorpCaseSearchResult aTaxcorpCaseSearchResult;
                Date persistentDateFeatureValue;
                G02TaxcorpCase aG02TaxcorpCase;
                for (G02TaxcorpCaseBk aG02TaxcorpCaseBk : aG02TaxcorpCaseBkList){
                    aG02TaxcorpCase = GardenEntityConverter.convertToG02TaxcorpCase(aG02TaxcorpCaseBk);
                    persistentDateFeatureValue = retrievePersistentTaxcorpDateFeatureValue(taxcorpDateFeature, aG02TaxcorpCase);
                    if (persistentDateFeatureValue != null){
                        
                        if ((persistentDateFeatureValue.equals(fromDate)) || (persistentDateFeatureValue.equals(toDate))
                                || ((persistentDateFeatureValue.after(fromDate)) && (persistentDateFeatureValue.before(toDate))))
                        {
                            aTaxcorpCaseSearchResult = new TaxcorpCaseSearchResult();
                            aTaxcorpCaseSearchResult.setTaxcorpCase(aG02TaxcorpCase);
                            aTaxcorpCaseSearchResult.setBusinessContactorList(GardenJpaUtils.findBusinessContactorListByTaxcorpCase(em, aG02TaxcorpCase));
                            result.getTaxcorpCaseSearchResultList().add(aTaxcorpCaseSearchResult);
                        }
                    }
                }//for-loop
            }
        } finally {
            em.close();
        }
        return result;
    }
    
    /**
     * Delete all the records after finalizedDate
     * @param em
     * @param entityUuid
     * @param finalizedDate 
     */
    private void finalizeTaxFilingCase(EntityManager em, String entityUuid, Date finalizedDate){
        String sqlQuery = "SELECT g FROM G02TaxFilingCase g WHERE g.entityUuid = :entityUuid "
                    + "AND (g.deadline > :deadline)";
        
        HashMap<String, Object> params = new HashMap<>();
        params.put("entityUuid", entityUuid);
        params.put("deadline", finalizedDate);
        
        List<G02TaxFilingCase> aG02TaxFilingCaseList = GardenJpaUtils.findEntityListByQuery(em, G02TaxFilingCase.class, sqlQuery, params);
        if ((aG02TaxFilingCaseList != null) && (!aG02TaxFilingCaseList.isEmpty())){
            params.clear();
            for (G02TaxFilingCase aG02TaxFilingCase : aG02TaxFilingCaseList){
                params.put("taxFilingUuid", aG02TaxFilingCase.getTaxFilingUuid());
                GardenJpaUtils.deleteEntity(em, G02TaxFilingCase.class, aG02TaxFilingCase.getTaxFilingUuid());
            }//for-loop
        }
    }

////    /**
////     * @deprecated - this query is buggy: too many records returned because those-ANDs seem not to be applied 
////     * @param taxFilingType
////     * @param taxFilingPeriod
////     * @param dateFrom
////     * @param dateTo
////     * @return 
////     */
////    public TaxcorpTaxFilingCaseSearchResultList searchTaxcorpTaxFilingCasesByDeadlineRange(String taxFilingType, 
////                                                                                            String taxFilingPeriod, 
////                                                                                            Date dateFrom, 
////                                                                                            Date dateTo) 
////    {
////        TaxcorpTaxFilingCaseSearchResultList result = new TaxcorpTaxFilingCaseSearchResultList();
////        result.setFromDateCriteria(dateFrom);
////        result.setToDateCriteria(dateTo);
////        result.setTaxFilingPeriodCriteria(taxFilingPeriod);
////        result.setTaxFilingTypeCriteria(taxFilingType);
////        
////        EntityManager em = getEntityManager();
////        try {
////            List<PeonyTaxFilingCaseBasicData> aPeonyTaxFilingCaseBasicDataList = GardenTaxcorpQuery.queryPeonyTaxFilingCaseBasicData (em, taxFilingType, taxFilingPeriod, dateFrom, dateTo);
////            if (aPeonyTaxFilingCaseBasicDataList != null){
////                HashMap<String, TaxcorpTaxFilingCaseSearchResult> aTaxcorpTaxFilingCaseSearchResultMap = new HashMap<>();
////                List<TaxcorpAndContactors> aTaxcorpAndContactorsList;
////                TaxcorpTaxFilingCaseSearchResult aTaxcorpTaxFilingCaseSearchResult;
////                PeonyTaxFilingCase peonyTaxFilingCase;
////                for (PeonyTaxFilingCaseBasicData aPeonyTaxFilingCaseBasicData : aPeonyTaxFilingCaseBasicDataList){
////                    //try to get it from the cache first. if failed, create a brand new one...
////                    aTaxcorpTaxFilingCaseSearchResult = aTaxcorpTaxFilingCaseSearchResultMap.get(aPeonyTaxFilingCaseBasicData.getTaxFilingCase().getTaxFilingUuid());
////                    if (aTaxcorpTaxFilingCaseSearchResult == null){
////                        aTaxcorpTaxFilingCaseSearchResult = new TaxcorpTaxFilingCaseSearchResult();
////                        peonyTaxFilingCase = new PeonyTaxFilingCase();
////                        peonyTaxFilingCase.setTaxFilingCase(aPeonyTaxFilingCaseBasicData.getTaxFilingCase());
////                        aTaxcorpTaxFilingCaseSearchResult.setPeonyTaxFilingCase(peonyTaxFilingCase);
////                        //set up the associated taxcorp and contactors
////                        aTaxcorpAndContactorsList = GardenTaxcorpQuery.queryTaxcorpAndContactors (em, aPeonyTaxFilingCaseBasicData.getTaxFilingCase().getEntityUuid());
////                        if (aTaxcorpAndContactorsList != null){
////                            for (TaxcorpAndContactors aTaxcorpAndContactors : aTaxcorpAndContactorsList){
////                                aTaxcorpTaxFilingCaseSearchResult.setTaxcorpCase(aTaxcorpAndContactors.getTaxcorpCase());
////                                aTaxcorpTaxFilingCaseSearchResult.getBusinessContactorList().add(aTaxcorpAndContactors.getBusinessContactor());
////                            }//for-loop
////                        }
////                    }else{
////                        peonyTaxFilingCase = aTaxcorpTaxFilingCaseSearchResult.getPeonyTaxFilingCase();
////                    }
////                    //add the new status if it is not NULL
////                    if (aPeonyTaxFilingCaseBasicData.getTaxFilingStatus() != null){
////                        peonyTaxFilingCase.getTaxFilingStatusList().add(aPeonyTaxFilingCaseBasicData.getTaxFilingStatus());
////                    }
////                    aTaxcorpTaxFilingCaseSearchResultMap.put(aPeonyTaxFilingCaseBasicData.getTaxFilingCase().getTaxFilingUuid(), aTaxcorpTaxFilingCaseSearchResult);
////                }
////                result.getTaxcorpTaxFilingCaseSearchResultList().addAll(new ArrayList<>(aTaxcorpTaxFilingCaseSearchResultMap.values()));
////            }
////        } finally {
////            em.close();
////        }
////        return result;
////    
////    }
    
    public TaxcorpTaxFilingCaseSearchResultList searchTaxcorpTaxFilingCaseSearchResultListByDeadlineRange(String taxFilingType, 
                                                                                                        String taxFilingPeriod, 
                                                                                                        Date dateFrom, 
                                                                                                        Date dateTo,
                                                                                                        HashMap<String, PeonyTaxcorpCase> cache) 
    {
        TaxcorpTaxFilingCaseSearchResultList result = new TaxcorpTaxFilingCaseSearchResultList();
        result.setFromDateCriteria(dateFrom);
        result.setToDateCriteria(dateTo);
        result.setTaxFilingPeriodCriteria(taxFilingPeriod);
        result.setTaxFilingTypeCriteria(taxFilingType);
        
        EntityManager em = getEntityManager();
        try {
            String sqlQuery = "SELECT g FROM G02TaxFilingCase g WHERE g.taxFilingType = :taxFilingType "
                        + "AND g.taxFilingPeriod = :taxFilingPeriod "
                        + "AND g.entityType = :entityType "
                        + "AND ((g.deadline BETWEEN :dateFrom01 AND :dateTo01) OR (g.extension BETWEEN :dateFrom02 AND :dateTo02))";
            HashMap<String, Object> params = new HashMap<>();
            params.put("taxFilingType", taxFilingType);
            params.put("taxFilingPeriod", taxFilingPeriod);
            params.put("entityType", GardenEntityType.TAXCORP_CASE.name());
            params.put("dateFrom01", dateFrom);
            params.put("dateTo01", dateTo);
            params.put("dateFrom02", dateFrom);
            params.put("dateTo02", dateTo);
            
            List<G02TaxFilingCase> aG02TaxFilingCaseList = GardenJpaUtils.findEntityListByQuery(em, G02TaxFilingCase.class, sqlQuery, params);
            if (aG02TaxFilingCaseList != null){
                TaxcorpTaxFilingCaseSearchResult aTaxFilingCaseSearchResult;
                PeonyTaxcorpCase aPeonyTaxcorpCase;
                for (G02TaxFilingCase aG02TaxFilingCase : aG02TaxFilingCaseList){
                    aPeonyTaxcorpCase = cache.get(aG02TaxFilingCase.getEntityUuid());
                    if (aPeonyTaxcorpCase == null){
                        G02TaxcorpCase aG02TaxcorpCase = GardenJpaUtils.findById(em, G02TaxcorpCase.class, aG02TaxFilingCase.getEntityUuid());
////                        if (aG02TaxcorpCase == null){
////                            G02TaxcorpCaseBk aG02TaxcorpCaseBk = GardenJpaUtils.findById(em, G02TaxcorpCaseBk.class, aG02TaxFilingCase.getEntityUuid());
////                            if (aG02TaxcorpCaseBk != null){
////                                aG02TaxcorpCase = GardenEntityConverter.convertToG02TaxcorpCase(aG02TaxcorpCaseBk);
////                            }
////                        }
                        if (aG02TaxcorpCase != null){
                            aPeonyTaxcorpCase = new PeonyTaxcorpCase();
                            aPeonyTaxcorpCase.setTaxcorpCase(aG02TaxcorpCase);
                            aPeonyTaxcorpCase.setBusinessContactorList(GardenJpaUtils.findBusinessContactorListByTaxcorpCase(em, aG02TaxcorpCase));
                        }
                    }
                    if (aPeonyTaxcorpCase != null){
                        cache.put(aG02TaxFilingCase.getEntityUuid(), aPeonyTaxcorpCase);
                        aTaxFilingCaseSearchResult = new TaxcorpTaxFilingCaseSearchResult();
                        aTaxFilingCaseSearchResult.setTaxcorpCase(aPeonyTaxcorpCase.getTaxcorpCase());
                        aTaxFilingCaseSearchResult.setBusinessContactorList(aPeonyTaxcorpCase.getBusinessContactorList());
                        aTaxFilingCaseSearchResult.setPeonyTaxFilingCase(new PeonyTaxFilingCase(aG02TaxFilingCase));
                        
                        //The followings are very time-consuming
                        //aTaxFilingCaseSearchResult.setPeonyBillPaymentList(GardenJpaUtils.findPeonyBillPaymentListByEntityUuid(em, aPeonyTaxcorpCase.getTaxcorpCase().getTaxcorpCaseUuid()));
                        
                        result.getTaxcorpTaxFilingCaseSearchResultList().add(aTaxFilingCaseSearchResult);
                    }
                }//for-loop
            }
            
        } finally {
            em.close();
        }
        return result;
    }
    
    public TaxcorpTaxFilingCaseSearchResultList searchFinalizedTaxcorpTaxFilingCaseSearchResultListByDeadlineRange(String taxFilingType, 
                                                                                                        String taxFilingPeriod, 
                                                                                                        Date dateFrom, 
                                                                                                        Date dateTo,
                                                                                                        HashMap<String, PeonyTaxcorpCase> cache) 
    {   
        TaxcorpTaxFilingCaseSearchResultList result = new TaxcorpTaxFilingCaseSearchResultList();
        result.setFromDateCriteria(dateFrom);
        result.setToDateCriteria(dateTo);
        result.setTaxFilingPeriodCriteria(taxFilingPeriod);
        result.setTaxFilingTypeCriteria(taxFilingType);
        
        EntityManager em = getEntityManager();
        try {
            String sqlQuery = "SELECT g FROM G02TaxFilingCase g WHERE g.taxFilingType = :taxFilingType "
                        + "AND g.taxFilingPeriod = :taxFilingPeriod "
                        + "AND g.entityType = :entityType "
                        + "AND ((g.deadline BETWEEN :dateFrom01 AND :dateTo01) OR (g.extension BETWEEN :dateFrom02 AND :dateTo02))";
            HashMap<String, Object> params = new HashMap<>();
            params.put("taxFilingType", taxFilingType);
            params.put("taxFilingPeriod", taxFilingPeriod);
            params.put("entityType", GardenEntityType.TAXCORP_CASE.name());
            params.put("dateFrom01", dateFrom);
            params.put("dateTo01", dateTo);
            params.put("dateFrom02", dateFrom);
            params.put("dateTo02", dateTo);
            
            List<G02TaxFilingCase> aG02TaxFilingCaseList = GardenJpaUtils.findEntityListByQuery(em, G02TaxFilingCase.class, sqlQuery, params);
            if (aG02TaxFilingCaseList != null){
                TaxcorpTaxFilingCaseSearchResult aTaxFilingCaseSearchResult;
                PeonyTaxcorpCase aPeonyTaxcorpCase;
                for (G02TaxFilingCase aG02TaxFilingCase : aG02TaxFilingCaseList){
                    aPeonyTaxcorpCase = cache.get(aG02TaxFilingCase.getEntityUuid());
                    if (aPeonyTaxcorpCase == null){
                        G02TaxcorpCase aG02TaxcorpCase = null;
                        G02TaxcorpCaseBk aG02TaxcorpCaseBk = GardenJpaUtils.findById(em, G02TaxcorpCaseBk.class, aG02TaxFilingCase.getEntityUuid());
                        if (aG02TaxcorpCaseBk != null){
                            aG02TaxcorpCase = GardenEntityConverter.convertToG02TaxcorpCase(aG02TaxcorpCaseBk);
                        }
                        if (aG02TaxcorpCase != null){
                            aPeonyTaxcorpCase = new PeonyTaxcorpCase();
                            aPeonyTaxcorpCase.setTaxcorpCase(aG02TaxcorpCase);
                            aPeonyTaxcorpCase.setBusinessContactorList(GardenJpaUtils.findBusinessContactorListByTaxcorpCase(em, aG02TaxcorpCase));
                        }
                    }
                    if (aPeonyTaxcorpCase != null){
                        cache.put(aG02TaxFilingCase.getEntityUuid(), aPeonyTaxcorpCase);
                        aTaxFilingCaseSearchResult = new TaxcorpTaxFilingCaseSearchResult();
                        aTaxFilingCaseSearchResult.setTaxcorpCase(aPeonyTaxcorpCase.getTaxcorpCase());
                        aTaxFilingCaseSearchResult.setBusinessContactorList(aPeonyTaxcorpCase.getBusinessContactorList());
                        aTaxFilingCaseSearchResult.setPeonyTaxFilingCase(new PeonyTaxFilingCase(aG02TaxFilingCase));
                        
                        result.getTaxcorpTaxFilingCaseSearchResultList().add(aTaxFilingCaseSearchResult);
                    }
                }//for-loop
            }
            
        } finally {
            em.close();
        }
        return result;
    }
//
//    public TaxcorpTaxFilingCaseSearchResultList searchTaxcorpTaxFilingCaseSearchResultListByExtensionDateRange(String taxFilingType, 
//                                                                                                                String taxFilingPeriod, 
//                                                                                                                Date dateFrom, 
//                                                                                                                Date dateTo,
//                                                                                                                HashMap<String, PeonyTaxcorpCase> cache) 
//    {
//        TaxcorpTaxFilingCaseSearchResultList result = new TaxcorpTaxFilingCaseSearchResultList();
//        result.setFromDateCriteria(dateFrom);
//        result.setToDateCriteria(dateTo);
//        result.setTaxFilingPeriodCriteria(taxFilingPeriod);
//        result.setTaxFilingTypeCriteria(taxFilingType);
//        
//        EntityManager em = getEntityManager();
//        try {
//            String sqlQuery = "SELECT g FROM G02DeadlineExtension g, G02TaxFilingCase h WHERE (g.entityUuid = h.taxFilingUuid) "
//                    + "AND (g.extensionDate BETWEEN :dateFrom AND :dateTo) "
//                    + "AND (h.taxFilingType = :taxFilingType)"
//                    + "AND (h.taxFilingPeriod = :taxFilingPeriod) "
//                    + "AND (h.entityType = :entityType) ";
//            HashMap<String, Object> params = new HashMap<>();
//            params.put("taxFilingType", taxFilingType);
//            params.put("taxFilingPeriod", taxFilingPeriod);
//            params.put("entityType", GardenEntityType.TAXCORP_CASE.name());  //not TAXPAYER_CASE but TAXCORP_CASE
//            params.put("dateFrom", dateFrom);
//            params.put("dateTo", dateTo);
//            
//            List<G02DeadlineExtension> aG02DeadlineExtensionList = GardenJpaUtils.findEntityListByQuery(em, G02DeadlineExtension.class, sqlQuery, params);
//            if (aG02DeadlineExtensionList != null){
//                TaxcorpTaxFilingCaseSearchResult aTaxFilingCaseSearchResult;
//                G02TaxFilingCase aG02TaxFilingCase;
//                PeonyTaxcorpCase aPeonyTaxcorpCase;
//                for (G02DeadlineExtension aG02DeadlineExtension : aG02DeadlineExtensionList){
//                    aG02TaxFilingCase = GardenJpaUtils.findById(em, G02TaxFilingCase.class, aG02DeadlineExtension.getEntityUuid());
//                    if (aG02TaxFilingCase != null){
//                        aPeonyTaxcorpCase = cache.get(aG02TaxFilingCase.getEntityUuid());
//                        if (aPeonyTaxcorpCase == null){
//                            G02TaxcorpCase aG02TaxcorpCase = GardenJpaUtils.findById(em, G02TaxcorpCase.class, aG02TaxFilingCase.getEntityUuid());
//                            if (aG02TaxcorpCase != null){
//                                aPeonyTaxcorpCase = new PeonyTaxcorpCase();
//                                aPeonyTaxcorpCase.setTaxcorpCase(aG02TaxcorpCase);
//                                aPeonyTaxcorpCase.setBusinessContactorList(GardenJpaUtils.findBusinessContactorListByTaxcorpCase(em, aG02TaxcorpCase));
//                            }
//                        }
//                        if (aPeonyTaxcorpCase != null){
//                            cache.put(aG02TaxFilingCase.getEntityUuid(), aPeonyTaxcorpCase);
//                            aTaxFilingCaseSearchResult = new TaxcorpTaxFilingCaseSearchResult();
//                            aTaxFilingCaseSearchResult.setTaxcorpCase(aPeonyTaxcorpCase.getTaxcorpCase());
//                            aTaxFilingCaseSearchResult.setBusinessContactorList(aPeonyTaxcorpCase.getBusinessContactorList());
//                            aTaxFilingCaseSearchResult.setPeonyTaxFilingCase(GardenJpaUtils.findPeonyTaxFilingCaseHelper(em, aG02TaxFilingCase, false));
//                            result.getTaxcorpTaxFilingCaseSearchResultList().add(aTaxFilingCaseSearchResult);
//                        }
//                    }
//                }//for-loop
//            }
//            
//        } finally {
//            em.close();
//        }
//        return result;
//    }

    /**
     * 
     * @param taxcorpCaseUuid
     * @return - if not found in both taxcorp table and taxcorp-bk, NULL returned
     */
    public G02TaxcorpCase findTaxcorpCaseByTaxcorpCaseUuid(String taxcorpCaseUuid) {
        if (ZcaValidator.isNullEmpty(taxcorpCaseUuid)){
            return null;
        }
        G02TaxcorpCase result = null;
        
        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.findById(em, G02TaxcorpCase.class, taxcorpCaseUuid);
        } finally {
            em.close();
        }
        return result;
    }

    private PeonyTaxcorpCase findPeonyTaxcorpCaseByEinNumberFromBackup(EntityManager em, String einNumber) {
        if (ZcaValidator.isNullEmpty(einNumber)){
            return null;
        }
        PeonyTaxcorpCase result = null;
        HashMap<String, Object> params = new HashMap<>();
        params.put("einNumber", einNumber);
        List<G02TaxcorpCaseBk> aG02TaxcorpCaseBkList = GardenJpaUtils.findEntityListByNamedQuery(em, G02TaxcorpCaseBk.class, "G02TaxcorpCaseBk.findByEinNumber", params);
        if ((aG02TaxcorpCaseBkList != null) && (!aG02TaxcorpCaseBkList.isEmpty())){
            //taxcorp case
            G02TaxcorpCaseBk aG02TaxcorpCaseBk = aG02TaxcorpCaseBkList.get(0);    //todo zzj: only the first one. Fix redandancy?
            result = this.constructPeonyTaxcorpCaseProfile(em, GardenEntityConverter.convertToG02TaxcorpCase(aG02TaxcorpCaseBk));
        }
        return result;
    }
    
    /**
     * 
     * @param aPeonyTaxcorpCase
     * @return - if this operation failed, NULL returned
     * @throws Exception 
     */
    public PeonyTaxcorpCase storePeonyTaxcorpCaseBasicInformationAndPersonnel(PeonyTaxcorpCase aPeonyTaxcorpCase) throws Exception {
        //store PeonyTaxcorpCase's basic information and personnel list
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            
            
            /**
             * PeonyAccount: check if this PeonyAccount is invalid. if it is invalid, 
             * it means this PeonyTaxcorpCase demand to create a customer instance. 
             * The system will create such a customer account for this case according 
             * to information provided by this taxcorp's case
             */
            GardenJpaUtils.prepareCustomerAccountForTaxcorpCase(em, aPeonyTaxcorpCase);
            
            /**
             * All the entities in this table are active (BK-table is inactive)
             */
            aPeonyTaxcorpCase.getTaxcorpCase().setBusinessStatus(TaxcorpBusinessStatus.ACTIVE.value());

            //aG02TaxcorpCase
            GardenJpaUtils.storeEntity(em, G02TaxcorpCase.class, aPeonyTaxcorpCase.getTaxcorpCase(),
                                        aPeonyTaxcorpCase.getTaxcorpCase().getTaxcorpCaseUuid(), 
                                        G02DataUpdaterFactory.getSingleton().getG02TaxcorpCaseUpdater());
            //personnel
            List<G02BusinessContactor> aG02BusinessContactorList = aPeonyTaxcorpCase.getBusinessContactorList();
            for (G02BusinessContactor aG02BusinessContactor : aG02BusinessContactorList){
                GardenJpaUtils.storeEntity(em, G02BusinessContactor.class, aG02BusinessContactor,
                                            aG02BusinessContactor.getBusinessContactorUuid(),
                                            G02DataUpdaterFactory.getSingleton().getG02BusinessContactorUpdater());
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            aPeonyTaxcorpCase = null;
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

        return aPeonyTaxcorpCase;
    }

    /**
     * 
     * @param taxcorpCaseUuid
     * @param finalizedDate
     * @return - if deletion is successful, the entity will be NULL. Otherwise 
     * (i.e. failed), a G02TaxcorpCase instance whose UUID is returned
     * 
     * @throws Exception 
     */
    public G02TaxcorpCase finalizeTaxcorpCase(String taxcorpCaseUuid, Date finalizedDate) throws Exception{
        if (ZcaValidator.isNullEmpty(taxcorpCaseUuid)){
            throw new Exception("Taxcorp UUID is demanded for this operation.");
        }
        G02TaxcorpCase aG02TaxcorpCase = null;
        EntityManager em = null;
        UserTransaction utx = getUserTransaction();
        //delete associated entities members
        try {
            utx.begin();
            em = getEntityManager();
            
            aG02TaxcorpCase = GardenJpaUtils.findById(em, G02TaxcorpCase.class, taxcorpCaseUuid);
            
            //System.out.println(">>> finalize taxcorpCaseUuid = " + taxcorpCaseUuid);
            
            if (aG02TaxcorpCase != null){
                G02TaxcorpCaseBk aG02TaxcorpCaseBk = new G02TaxcorpCaseBk();
                aG02TaxcorpCaseBk.setAgreementUuid(aG02TaxcorpCase.getAgreementUuid());
                aG02TaxcorpCaseBk.setAgreementSignature(aG02TaxcorpCase.getAgreementSignature());
                aG02TaxcorpCaseBk.setAgreementSignatureTimestamp(aG02TaxcorpCase.getAgreementSignatureTimestamp());
                aG02TaxcorpCaseBk.setAgreementUuid(aG02TaxcorpCase.getAgreementUuid());
                aG02TaxcorpCaseBk.setBankAccountNumber(aG02TaxcorpCase.getBankAccountNumber());
                aG02TaxcorpCaseBk.setBankRoutingNumber(aG02TaxcorpCase.getBankRoutingNumber());
                aG02TaxcorpCaseBk.setBusinessPurpose(aG02TaxcorpCase.getBusinessPurpose());
                aG02TaxcorpCaseBk.setBusinessStatus(aG02TaxcorpCase.getBusinessStatus());
                aG02TaxcorpCaseBk.setBusinessType(aG02TaxcorpCase.getBusinessType());
                aG02TaxcorpCaseBk.setCorporateName(aG02TaxcorpCase.getCorporateName());
                aG02TaxcorpCaseBk.setCorporateEmail(aG02TaxcorpCase.getCorporateEmail());
                aG02TaxcorpCaseBk.setCorporateFax(aG02TaxcorpCase.getCorporateFax());
                aG02TaxcorpCaseBk.setCorporatePhone(aG02TaxcorpCase.getCorporatePhone());
                aG02TaxcorpCaseBk.setCorporateWebPresence(aG02TaxcorpCase.getCorporateWebPresence());
                aG02TaxcorpCaseBk.setCustomerAccountUuid(aG02TaxcorpCase.getCustomerAccountUuid());
                aG02TaxcorpCaseBk.setDosDate(aG02TaxcorpCase.getDosDate());
                aG02TaxcorpCaseBk.setEinNumber(aG02TaxcorpCase.getEinNumber());
                aG02TaxcorpCaseBk.setMemo(aG02TaxcorpCase.getMemo());
                aG02TaxcorpCaseBk.setDba(aG02TaxcorpCase.getDba());
                aG02TaxcorpCaseBk.setLatestLogUuid(aG02TaxcorpCase.getLatestLogUuid());
                aG02TaxcorpCaseBk.setTaxcorpState(aG02TaxcorpCase.getTaxcorpState());
                aG02TaxcorpCaseBk.setEntityStatus(aG02TaxcorpCase.getEntityStatus());
                aG02TaxcorpCaseBk.setTaxcorpAddress(aG02TaxcorpCase.getTaxcorpAddress());
                aG02TaxcorpCaseBk.setTaxcorpCity(aG02TaxcorpCase.getTaxcorpCity());
                aG02TaxcorpCaseBk.setTaxcorpStateCounty(aG02TaxcorpCase.getTaxcorpStateCounty());
                aG02TaxcorpCaseBk.setTaxcorpState(aG02TaxcorpCase.getTaxcorpState());
                aG02TaxcorpCaseBk.setTaxcorpZip(aG02TaxcorpCase.getTaxcorpZip());
                aG02TaxcorpCaseBk.setTaxcorpCountry(aG02TaxcorpCase.getTaxcorpCountry());
                aG02TaxcorpCaseBk.setTaxcorpCaseUuid(aG02TaxcorpCase.getTaxcorpCaseUuid());
                aG02TaxcorpCaseBk.setCreated(aG02TaxcorpCase.getCreated());
                aG02TaxcorpCaseBk.setUpdated(aG02TaxcorpCase.getUpdated());
                
                /**
                 * All the backup entities in the table are in-active
                 */
                aG02TaxcorpCaseBk.setBusinessStatus(TaxcorpBusinessStatus.FINALIZED.value());
                
                GardenJpaUtils.storeEntity(em, G02TaxcorpCaseBk.class, aG02TaxcorpCaseBk, aG02TaxcorpCaseBk.getTaxcorpCaseUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02TaxcorpCaseBkUpdater());
                
                finalizeTaxFilingCase(em, taxcorpCaseUuid, finalizedDate);
                
                GardenJpaUtils.deleteEntity(em, G02TaxcorpCase.class, taxcorpCaseUuid);
            
                utx.commit();
                aG02TaxcorpCase = null; //signal of success
            }
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseTaxcorpEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseTaxcorpEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return aG02TaxcorpCase;
    }
    
    public G02TaxcorpCase rollbackFinalizedTaxcorpCase(String taxcorpCaseUuid) throws Exception{
        if (ZcaValidator.isNullEmpty(taxcorpCaseUuid)){
            throw new Exception("Taxcorp UUID is demanded for this operation.");
        }
        G02TaxcorpCase aG02TaxcorpCase = null;
        EntityManager em = null;
        UserTransaction utx = getUserTransaction();
        //delete associated entities members
        try {
            utx.begin();
            em = getEntityManager();
            
            G02TaxcorpCaseBk aG02TaxcorpCaseBk = GardenJpaUtils.findById(em, G02TaxcorpCaseBk.class, taxcorpCaseUuid);
            if (aG02TaxcorpCaseBk != null){
                aG02TaxcorpCase = GardenEntityConverter.convertToG02TaxcorpCase(aG02TaxcorpCaseBk);
                /**
                 * All the backup entities in the table are in-active
                 */
                aG02TaxcorpCase.setBusinessStatus(TaxcorpBusinessStatus.ACTIVE.value());
                
                GardenJpaUtils.storeEntity(em, G02TaxcorpCase.class, aG02TaxcorpCase, aG02TaxcorpCase.getTaxcorpCaseUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02TaxcorpCaseUpdater());
                
                GardenJpaUtils.deleteEntity(em, G02TaxcorpCaseBk.class, taxcorpCaseUuid);
            
                utx.commit();
            }
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseTaxcorpEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseTaxcorpEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return aG02TaxcorpCase;
    }

    public TaxcorpCaseBriefList findTaxcorpCaseBriefListByBillPeriod(Date fromDate, Date toDate) {
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
        TaxcorpCaseBriefList result = new TaxcorpCaseBriefList();
        EntityManager em = getEntityManager();
        try {
            //key: taxcorpUuid; value: TaxcorpCaseBrief
            HashMap<String, TaxcorpCaseBrief> taxcorpCaseBriefs = new HashMap<>();
            String sqlQuery = "SELECT g FROM G02Bill g WHERE g.entityType = :entityType AND g.billDatetime BETWEEN :fromDate AND :toDate ";
            HashMap<String, Object> params = new HashMap<>();
            params.put("entityType", GardenEntityType.TAXCORP_CASE.name());
            params.put("fromDate", fromDate);
            params.put("toDate", toDate);
            List<G02Bill> aG02BillList = GardenJpaUtils.findEntityListByQuery(em, G02Bill.class, sqlQuery, params);
            double sumBillTotal = 0.0;
            double sumPaymentTotal = 0.0;
            if (aG02BillList != null){
                TaxcorpCaseBrief aTaxcorpCaseBrief;
                G02TaxcorpCase aG02TaxcorpCase;
                String taxcorpCaseUuid;
                G02User aG02User;
                List<G02Payment> aG02PaymentList;
                for (G02Bill aG02Bill : aG02BillList){
                    taxcorpCaseUuid = aG02Bill.getEntityUuid();
                    aTaxcorpCaseBrief = taxcorpCaseBriefs.get(taxcorpCaseUuid);
                    if (aTaxcorpCaseBrief == null){
                        aG02TaxcorpCase = GardenJpaUtils.findById(em, G02TaxcorpCase.class, taxcorpCaseUuid);
                        if (aG02TaxcorpCase != null){
                            aTaxcorpCaseBrief = new TaxcorpCaseBrief();
                            aTaxcorpCaseBrief.setBalanceTotalText("$0.0");
                            aTaxcorpCaseBrief.setBillTotalText("$0.0");
                            aTaxcorpCaseBrief.setPaymentTotalText("$0.0");
                            aTaxcorpCaseBrief.setTaxcorpCaseUuid(taxcorpCaseUuid);
                            aTaxcorpCaseBrief.setBusinessStatus(aG02TaxcorpCase.getBusinessStatus());
                            aTaxcorpCaseBrief.setBusinessType(aG02TaxcorpCase.getBusinessType());
                            aTaxcorpCaseBrief.setCorporateEmail(aG02TaxcorpCase.getCorporateEmail());
                            aTaxcorpCaseBrief.setCorporateName(aG02TaxcorpCase.getCorporateName());
                            aTaxcorpCaseBrief.setCorporatePhone(aG02TaxcorpCase.getCorporatePhone());
                            aTaxcorpCaseBrief.setEinNumber(aG02TaxcorpCase.getEinNumber());
                            aTaxcorpCaseBrief.setCustomerAccountUuid(aG02TaxcorpCase.getCustomerAccountUuid());
                            aG02User = GardenJpaUtils.findById(em, G02User.class, aG02TaxcorpCase.getCustomerAccountUuid());
                            if (aG02User != null){
                                aTaxcorpCaseBrief.setCustomerAccountUuid(aG02User.getUserUuid());
                                aTaxcorpCaseBrief.setCustomerFirstName(aG02User.getFirstName());
                                aTaxcorpCaseBrief.setCustomerLastName(aG02User.getLastName());
                                aTaxcorpCaseBrief.setCustomerSsn(aG02User.getSsn());
                            }
                            taxcorpCaseBriefs.put(taxcorpCaseUuid, aTaxcorpCaseBrief);
                        }
                    }
                    if (aTaxcorpCaseBrief != null){
                        double billTotal = Double.parseDouble(aTaxcorpCaseBrief.getBillTotalText().substring(1));
                        //calculate bill
                        billTotal += PeonyBillPayment.calculateBillDiscountedPrice(aG02Bill);
                        aTaxcorpCaseBrief.setBillTotalText("$"+billTotal);
                        //calculate payments for the bill
                        double paymentTotal = Double.parseDouble(aTaxcorpCaseBrief.getPaymentTotalText().substring(1));
                        params.clear();
                        params.put("billUuid", aG02Bill.getBillUuid());
                        aG02PaymentList = GardenJpaUtils.findEntityListByNamedQuery(em, G02Payment.class, "G02Payment.findByBillUuid", params);
                        for (G02Payment aG02Payment : aG02PaymentList){
                            paymentTotal += aG02Payment.getPaymentPrice().doubleValue();
                        }
                        aTaxcorpCaseBrief.setPaymentTotalText("$"+paymentTotal);
                        aTaxcorpCaseBrief.setBalanceTotalText("$"+(billTotal - paymentTotal));
                        //calculate the sum of billTotal and paymentTotal
                        sumBillTotal += billTotal;
                        sumPaymentTotal += paymentTotal;
                    }
                }//for
                result.setSumBillTotalText("$" + sumBillTotal);
                result.setSumPaymentTotalText("$" + sumPaymentTotal);
                result.setSumBalanceTotalText("$" + (sumBillTotal - sumPaymentTotal));
                result.getTaxcorpCaseBriefList().addAll(taxcorpCaseBriefs.values());
            }
        } catch (Exception ex) {
            Logger.getLogger(RoseTaxcorpEJB02.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
        } finally {
            em.close();
        }
        return result;
    }

}
