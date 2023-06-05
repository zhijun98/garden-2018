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

import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.persistence.GardenJpaUtils;
import com.zcomapproach.garden.persistence.constant.GardenAccountStatus;
import com.zcomapproach.garden.persistence.constant.GardenEntityStatus;
import com.zcomapproach.garden.persistence.constant.TaxFilingPeriod;
import com.zcomapproach.garden.persistence.constant.TaxFilingType;
import com.zcomapproach.garden.persistence.constant.BusinessContactorRole;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G01Account;
import com.zcomapproach.garden.persistence.entity.G01Bill;
import com.zcomapproach.garden.persistence.entity.G01ContactEntity;
import com.zcomapproach.garden.persistence.entity.G01ContactInfo;
import com.zcomapproach.garden.persistence.entity.G01ContactMessage;
import com.zcomapproach.garden.persistence.entity.G01Location;
import com.zcomapproach.garden.persistence.entity.G01Log;
import com.zcomapproach.garden.persistence.entity.G01Payment;
import com.zcomapproach.garden.persistence.entity.G01TaxFiling;
import com.zcomapproach.garden.persistence.entity.G01TaxFilingType;
import com.zcomapproach.garden.persistence.entity.G01TaxcorpCase;
import com.zcomapproach.garden.persistence.entity.G01TaxcorpCaseBk;
import com.zcomapproach.garden.persistence.entity.G01TaxcorpRepresentative;
import com.zcomapproach.garden.persistence.entity.G01User;
import com.zcomapproach.garden.persistence.peony.PeonyBillPaymentList;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.garden.rose.data.profile.EmployeeAccountProfile;
import com.zcomapproach.garden.rose.data.profile.RoseAccountProfile;
import com.zcomapproach.garden.rose.data.profile.RoseContactInfoProfile;
import com.zcomapproach.garden.rose.data.profile.RoseLocationProfile;
import com.zcomapproach.garden.rose.data.profile.TaxFilingProfile;
import com.zcomapproach.garden.rose.data.profile.TaxcorpCaseProfile;
import com.zcomapproach.garden.rose.data.profile.TaxFilingTypeProfile;
import com.zcomapproach.garden.rose.data.profile.TaxcorpBillBalanceProfile;
import com.zcomapproach.garden.rose.data.profile.TaxcorpFilingConciseProfile;
import com.zcomapproach.garden.rose.data.profile.TaxcorpRepresentativeProfile;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.garden.rose.util.RoseDataAgent;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
public class RoseTaxcorpEJB extends AbstractDataServiceEJB {
    
    /**
     * 
     * @param entityUuid
     * @return - always not NULL
     */
    public PeonyBillPaymentList findPeonyBillPaymentList(String entityUuid) {
        PeonyBillPaymentList result = new PeonyBillPaymentList();
        if (ZcaValidator.isNotNullEmpty(entityUuid)){
            EntityManager em = getEntityManager();
            try {
                result.setPeonyBillPaymentList(GardenJpaUtils.findPeonyBillPaymentListByEntityUuid(em, entityUuid));
            } finally {
                em.close();
            }
        }
        return result;
    }
    
    public List<G01TaxFilingType> findTaxFilingTypeEntityListByType(String taxcorpCaseUuid, TaxFilingType type) {
        List<G01TaxFilingType> result = new ArrayList<>();
        if (type != null){
            EntityManager em = getEntityManager();
            try {
                String sqlQuery = "SELECT g FROM G01TaxFilingType g WHERE g.g01TaxFilingTypePK.taxcorpCaseUuid = :taxcorpCaseUuid "
                        + "AND g.g01TaxFilingTypePK.taxFilingType = :taxFilingType";
                HashMap<String, Object> params = new HashMap<>();
                params.put("taxcorpCaseUuid", taxcorpCaseUuid);
                params.put("taxFilingType", type.value());
                result = GardenJpaUtils.findEntityListByQuery(em, G01TaxFilingType.class, sqlQuery, params);
            } finally {
                em.close();
            }
        }
        return result;
    }
    
    public List<TaxcorpCaseProfile> findTaxcorpCaseProfileListByCustomerUuid(String customerAccountUuid) {
        List<TaxcorpCaseProfile> result = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            String sqlQuery = "SELECT g FROM G01TaxcorpCase g WHERE g.customerAccountUuid = :customerAccountUuid and g.entityStatus IS NULL";
            HashMap<String, Object> params = new HashMap<>();
            params.put("customerAccountUuid", customerAccountUuid);
            List<G01TaxcorpCase> aG01TaxcorpCaseList = GardenJpaUtils.findEntityListByQuery(em, G01TaxcorpCase.class, sqlQuery, params);
            TaxcorpCaseProfile aTaxcorpCaseProfile;
            for (G01TaxcorpCase aG01TaxcorpCase : aG01TaxcorpCaseList){
                aTaxcorpCaseProfile = constructTaxcorpCaseProfileByTaxcorpCaseEntity(em, 
                        GardenJpaUtils.findById(em, G01TaxcorpCase.class, aG01TaxcorpCase.getTaxcorpCaseUuid()));
                if (aTaxcorpCaseProfile != null){
                    result.add(aTaxcorpCaseProfile);
                }
            }
        } finally {
            em.close();
        }
        return result;
    }

    public List<TaxcorpCaseProfile> findTaxcorpCaseProfileListByContactorUuid(String contactorUuid) {
        List<TaxcorpCaseProfile> result = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            String sqlQuery = "SELECT g FROM G01TaxcorpRepresentative g WHERE g.g01TaxcorpRepresentativePK.representativeUserUuid = :representativeUserUuid "
                    + "AND g.roleInCorp != :roleInCorp";
            HashMap<String, Object> params = new HashMap<>();
            params.put("representativeUserUuid", contactorUuid);
            params.put("roleInCorp", BusinessContactorRole.TAXCORP_OWNER.value());
            List<G01TaxcorpRepresentative> aG01TaxcorpRepresentativeList = GardenJpaUtils.findEntityListByQuery(em, G01TaxcorpRepresentative.class, sqlQuery, params);
            TaxcorpCaseProfile aTaxcorpCaseProfile;
            for (G01TaxcorpRepresentative aG01TaxcorpRepresentative : aG01TaxcorpRepresentativeList){
                aTaxcorpCaseProfile = constructTaxcorpCaseProfileByTaxcorpCaseEntity(em, 
                        GardenJpaUtils.findById(em, G01TaxcorpCase.class, aG01TaxcorpRepresentative.getG01TaxcorpRepresentativePK().getTaxcorpCaseUuid()));
                if (aTaxcorpCaseProfile != null){
                    result.add(aTaxcorpCaseProfile);
                }
            }
        } finally {
            em.close();
        }
        return result;
    }

    /**
     * Try to find them according to tax-filing-type, and then, try to find them directly from taxcorp-case
     * @param employeeUuid
     * @return 
     */
    public List<G01TaxcorpCase> findTaxcorpCaseTaskListByEmployeeUuid(String employeeUuid) {
        List<G01TaxcorpCase> result = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            result = findTaxcorpCaseEntityListByEmployeeUuidFromTaxFilingType(em, employeeUuid);
            TreeSet<String> filter = new TreeSet<>();
            if (!result.isEmpty()){
                for (G01TaxcorpCase aG01TaxcorpCase : result){
                    filter.add(aG01TaxcorpCase.getTaxcorpCaseUuid());
                }
            }
            List<G01TaxcorpCase> aG01TaxcorpCaseList = findTaxcorpCaseEntityListByEmployeeUuidHelper(em, employeeUuid);
            if (!aG01TaxcorpCaseList.isEmpty()){
                for (G01TaxcorpCase aG01TaxcorpCase : aG01TaxcorpCaseList){
                    if (!filter.contains(aG01TaxcorpCase.getTaxcorpCaseUuid())){
                        result.add(aG01TaxcorpCase);
                    }
                }
            }
        } finally {
            em.close();
        }
        return result;
    }

    private List<G01TaxcorpCase> findTaxcorpCaseEntityListByEmployeeUuidFromTaxFilingType(EntityManager em, String employeeUuid) {
        List<G01TaxcorpCase> result = new ArrayList<>();
        
        HashMap<String, Object> params = new HashMap<>();
        params.put("employeeAccountUuid", employeeUuid);
        List<G01TaxFilingType> aG01TaxFilingTypeList = GardenJpaUtils.findEntityListByNamedQuery(em, G01TaxFilingType.class, 
                "G01TaxFilingType.findByEmployeeAccountUuid", params);
        if (!aG01TaxFilingTypeList.isEmpty()){
            G01TaxcorpCase aG01TaxcorpCase;
            for (G01TaxFilingType aG01TaxFilingType : aG01TaxFilingTypeList){
                aG01TaxcorpCase = GardenJpaUtils.findById(em, G01TaxcorpCase.class, aG01TaxFilingType.getG01TaxFilingTypePK().getTaxcorpCaseUuid());
                if (aG01TaxcorpCase != null){
                    result.add(aG01TaxcorpCase);
                }
            }
        }
        return result;
    }

    private List<G01TaxcorpCase> findTaxcorpCaseEntityListByEmployeeUuidHelper(EntityManager em, String employeeUuid) {
        String sqlQuery = "SELECT g FROM G01TaxcorpCase g WHERE g.employeeAccountUuid = :employeeAccountUuid";
        HashMap<String, Object> params = new HashMap<>();
        params.put("employeeAccountUuid", employeeUuid);
        return GardenJpaUtils.findEntityListByQuery(em, G01TaxcorpCase.class, sqlQuery, params);
    }

    public List<TaxcorpCaseProfile> findTaxcorpCaseProfileListByEmployeeUuid(String employeeUuid) {
        List<TaxcorpCaseProfile> result = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            List<G01TaxcorpCase> aG01TaxcorpCaseList = findTaxcorpCaseEntityListByEmployeeUuidHelper(em, employeeUuid);
            TaxcorpCaseProfile aTaxcorpCaseProfile;
            for (G01TaxcorpCase aG01TaxcorpCase : aG01TaxcorpCaseList){
                aTaxcorpCaseProfile = constructTaxcorpCaseProfileByTaxcorpCaseEntity(em, 
                        GardenJpaUtils.findById(em, G01TaxcorpCase.class, aG01TaxcorpCase.getTaxcorpCaseUuid()));
                if (aTaxcorpCaseProfile != null){
                    result.add(aTaxcorpCaseProfile);
                }
            }
        } finally {
            em.close();
        }
        return result;
    }

    public TaxcorpCaseProfile findTaxcorpCaseProfileByTaxcorpCaseUuid(String requestedTaxcorpCaseUuid) {
        TaxcorpCaseProfile result = null;
        /**
         * Because of legacy data, remedy a new fake user profile for this account whis has no any user entity associated for unknown reason
         */
        G01TaxcorpCase aG01TaxcorpCase = this.findEntityByUuid(G01TaxcorpCase.class, requestedTaxcorpCaseUuid);
        if (aG01TaxcorpCase != null){
            G01User aG01User = findEntityByUuid(G01User.class, aG01TaxcorpCase.getCustomerAccountUuid());
            if (aG01User == null){
                aG01User = new G01User();
                if (ZcaValidator.isNullEmpty(aG01TaxcorpCase.getCustomerAccountUuid())){
                    aG01User.setUserUuid(GardenData.generateUUIDString());
                }else{
                    aG01User.setUserUuid(aG01TaxcorpCase.getCustomerAccountUuid());
                }
                aG01User.setFirstName("Legacy Data: Unknown First Name");
                aG01User.setLastName("Legacy Data: Unknown Last Name");
                try {
                    this.storeEntityByUuid(G01User.class, aG01User, aG01User.getUserUuid(), G01DataUpdaterFactory.getSingleton().getG01UserUpdater());
                } catch (Exception ex) {
                    Logger.getLogger(RoseTaxcorpEJB.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            G01Account aG01Account = findEntityByUuid(G01Account.class, aG01User.getUserUuid());
            if (aG01Account == null){
                aG01Account = new G01Account();
                aG01Account.setAccountUuid(aG01User.getUserUuid());
                aG01Account.setLoginName(GardenData.generateUUIDString());
                aG01Account.setEncryptedPassword(GardenData.generateUUIDString());
                aG01Account.setAccountStatus(GardenAccountStatus.Valid.name());
                aG01Account.setCreated(new Date());
                aG01Account.setUpdated(new Date());
                try {
                    this.storeEntityByUuid(G01Account.class, aG01Account, aG01Account.getAccountUuid(), G01DataUpdaterFactory.getSingleton().getG01AccountUpdater());
                } catch (Exception ex) {
                    Logger.getLogger(RoseTaxcorpEJB.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        EntityManager em = getEntityManager();
        try {
            result = constructTaxcorpCaseProfileByTaxcorpCaseEntity(em,aG01TaxcorpCase );
        } finally {
            em.close();
        }
        return result;
    }

    /**
     * 
     * @param em
     * @param aG01TaxcorpCase - an instance from the storage
     * @return 
     */
    private TaxcorpCaseProfile constructTaxcorpCaseProfileByTaxcorpCaseEntity(EntityManager em, G01TaxcorpCase aG01TaxcorpCase) {
        if (aG01TaxcorpCase == null){
            return null;
        }
        TaxcorpCaseProfile aTaxcorpCaseProfile = new TaxcorpCaseProfile();
        //taxcorpCaseEntity
        aTaxcorpCaseProfile.setTaxcorpCaseEntity(aG01TaxcorpCase);
        //latestLogEntity
        if (ZcaValidator.isNotNullEmpty(aG01TaxcorpCase.getLatestLogUuid())){
            G01Log log = GardenJpaUtils.findById(em, G01Log.class, aG01TaxcorpCase.getLatestLogUuid());
            if (log != null){
                aTaxcorpCaseProfile.setLatestLogEntity(log);
                aTaxcorpCaseProfile.setLatestLogEmployeeUserRecord(GardenJpaUtils.findById(em, G01User.class, log.getOperatorAccountUuid()));
            }
        }
        String taxcorpCaseUuid = aTaxcorpCaseProfile.getTaxcorpCaseEntity().getTaxcorpCaseUuid();
        //customerProfile
        RoseAccountProfile customerProfile = super.findRoseAccountProfileByAccountUserUuidHelper(em, aG01TaxcorpCase.getCustomerAccountUuid());
        if (customerProfile != null){
            aTaxcorpCaseProfile.setCustomerProfile(customerProfile);
        }
        try {
            //agentProfile
            EmployeeAccountProfile agentProfile = this.findEmployeeAccountProfileByAccountUuid(em, aG01TaxcorpCase.getEmployeeAccountUuid());
            if (agentProfile != null){
                aTaxcorpCaseProfile.setAgentProfile(agentProfile);
            }
        } catch (Exception ex) {
            //Logger.getLogger(RoseTaxcorpEJB.class.getName()).log(Level.SEVERE, null, ex);
        }
        //businessCaseBillProfileList
        aTaxcorpCaseProfile.setBusinessCaseBillProfileList(findBusinessCaseBillProfileListByFeaturedField("entityUuid", 
                                                                                                            taxcorpCaseUuid, 
                                                                                                            "G01Bill.findByEntityUuid"));
        //documentRequirementprofileList
        aTaxcorpCaseProfile.setDocumentRequirementprofileList(findRoseDocumentRequirementProfileListByFeaturedField("entityUuid", 
                                                                                                                taxcorpCaseUuid, 
                                                                                                                "G01DocumentRequirement.findByEntityUuid"));
        //uploadedArchivedDocumentList
        aTaxcorpCaseProfile.setUploadedArchivedDocumentList(findRoseArchivedDocumentProfileListByFeaturedField("entityUuid", 
                                                                                                                taxcorpCaseUuid, 
                                                                                                                "G01ArchivedDocument.findByEntityUuid"));
        //contactors and taxcorpOwners
        aTaxcorpCaseProfile.setTaxcorpRepresentativeProfileList(super.findTaxcorpRepresentativeProfileListByTaxcorpCaseUuid(em, taxcorpCaseUuid, null));
        
        //taxcorp tax filinf deadlines
        aTaxcorpCaseProfile.setPayrollTaxFilingEmployeeProfile(loadTaxFilingTypeEntities(em, aTaxcorpCaseProfile, TaxFilingType.PAYROLL_TAX));
        aTaxcorpCaseProfile.setSalesTaxFilingEmployeeProfile(loadTaxFilingTypeEntities(em, aTaxcorpCaseProfile, TaxFilingType.SALES_TAX));
        aTaxcorpCaseProfile.setTaxReturnFilingEmployeeProfile(loadTaxFilingTypeEntities(em, aTaxcorpCaseProfile, TaxFilingType.TAX_RETURN));
        
        //fix legancy data
        RoseDataAgent.fixTaxcorpLocationAndContactData(aTaxcorpCaseProfile);
        
        aTaxcorpCaseProfile.setBrandNew(false);
        return aTaxcorpCaseProfile;
    }
    
    private EmployeeAccountProfile loadTaxFilingTypeEntities(EntityManager em, TaxcorpCaseProfile aTaxcorpCaseProfile, TaxFilingType aTaxFilingType){
        List<TaxFilingTypeProfile> aTaxFilingTypeProfileList = super.findTaxFilingTypeProfileListByTaxcorpCaseUuid(em, aTaxcorpCaseProfile.getTaxcorpCaseEntity().getTaxcorpCaseUuid(), aTaxFilingType.value());
        EmployeeAccountProfile employee = null;
        if (!aTaxFilingTypeProfileList.isEmpty()){
            List<TaxFilingProfile> aTaxFilingProfileList;
            String taxFilingPeriod;
            for (TaxFilingTypeProfile aTaxFilingTypeProfile : aTaxFilingTypeProfileList){
                if ((employee == null) && (ZcaValidator.isNotNullEmpty(aTaxFilingTypeProfile.getTaxFilingTypeEntity().getEmployeeAccountUuid()))){
                    try {
                        employee = this.findEmployeeAccountProfileByAccountUuid(em, aTaxFilingTypeProfile.getTaxFilingTypeEntity().getEmployeeAccountUuid());
                    } catch (Exception ex) {
                        //Logger.getLogger(RoseTaxcorpEJB.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                aTaxFilingProfileList = aTaxFilingTypeProfile.getTaxFilingProfileList();
                if (!aTaxFilingProfileList.isEmpty()){
                    taxFilingPeriod = aTaxFilingTypeProfile.getTaxFilingTypeEntity().getG01TaxFilingTypePK().getTaxFilingPeriod();
                    switch(aTaxFilingType){
                        case PAYROLL_TAX:
                            aTaxcorpCaseProfile.getPayrollTaxFilingPeriods().add(taxFilingPeriod);
                            aTaxcorpCaseProfile.getPayrollTaxFilingProfileList().addAll(aTaxFilingProfileList);
                            break;
                        case SALES_TAX:
                            aTaxcorpCaseProfile.getSalesTaxFilingPeriods().add(taxFilingPeriod);
                            aTaxcorpCaseProfile.getSalesTaxFilingProfileList().addAll(aTaxFilingProfileList);
                            break;
                        case TAX_RETURN:
                            aTaxcorpCaseProfile.getTaxReturnFilingPeriods().add(taxFilingPeriod);
                            aTaxcorpCaseProfile.getTaxReturnFilingProfileList().addAll(aTaxFilingProfileList);
                            break;
                    }
                }
            }
        }
        if (employee == null){
            employee = aTaxcorpCaseProfile.getAgentProfile();
        }
        return employee;
    }

    public void storeTaxFilingTypeProfile(TaxFilingTypeProfile aTaxcorpTaxationProfile) throws IllegalStateException, 
                                                                                            SecurityException, 
                                                                                            SystemException, 
                                                                                            RollbackException, 
                                                                                            HeuristicMixedException, 
                                                                                            HeuristicRollbackException, 
                                                                                            NotSupportedException, 
                                                                                            ZcaEntityValidationException 
    {
        if (aTaxcorpTaxationProfile == null){
            return;
        }
        EntityManager em = null;
        UserTransaction utx = getUserTransaction();

        //delete associated entities members
        try {
            utx.begin();
            em = getEntityManager();
            //G01TaxFilingType
            GardenJpaUtils.storeEntity(em, G01TaxFilingType.class, aTaxcorpTaxationProfile.getTaxFilingTypeEntity(), 
                                    aTaxcorpTaxationProfile.getTaxFilingTypeEntity().getG01TaxFilingTypePK(), 
                                    G01DataUpdaterFactory.getSingleton().getG01TaxFilingTypeUpdater());
            //TaxFilingProfile List
            List<TaxFilingProfile> aTaxFilingProfileList = aTaxcorpTaxationProfile.getTaxFilingProfileList();
            if (aTaxFilingProfileList != null){
                for (TaxFilingProfile aTaxFilingProfile : aTaxFilingProfileList){
                    GardenJpaUtils.storeEntity(em, G01TaxFiling.class, aTaxFilingProfile.getTaxFilingEntity(), 
                                            aTaxFilingProfile.getTaxFilingEntity().getTaxFilingUuid(), 
                                            G01DataUpdaterFactory.getSingleton().getG01TaxFilingUpdater());
                }
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            //Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                //Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void markTaxcorpCaseRecordsEntityStatus(String taxcorpCaseUuid, GardenEntityStatus entityStatus) throws IllegalStateException, 
                                                                                                                    SecurityException, 
                                                                                                                    SystemException, 
                                                                                                                    RollbackException, 
                                                                                                                    HeuristicMixedException, 
                                                                                                                    HeuristicRollbackException, 
                                                                                                                    NotSupportedException, 
                                                                                                                    ZcaEntityValidationException 
    {
        if (ZcaValidator.isNullEmpty(taxcorpCaseUuid)){
            RoseJsfUtils.setGlobalErrorFacesMessage(RoseText.getText("NoData_T"));
            return;
        }
        EntityManager em = null;
        UserTransaction utx = getUserTransaction();
        //delete associated entities members
        try {
            utx.begin();
            em = getEntityManager();
            
            //aG01TaxcorpCase
            G01TaxcorpCase aG01TaxcorpCase = GardenJpaUtils.findById(em, G01TaxcorpCase.class, taxcorpCaseUuid);
            if (aG01TaxcorpCase != null){
                aG01TaxcorpCase.setEntityStatus(entityStatus.value());
                GardenJpaUtils.storeEntity(em, G01TaxcorpCase.class, aG01TaxcorpCase, aG01TaxcorpCase.getTaxcorpCaseUuid(), 
                        G01DataUpdaterFactory.getSingleton().getG01TaxcorpCaseUpdater());
                //taxcorpRepresentativeProfileList
                HashMap<String, Object> params = new HashMap<>();
                params.put("taxcorpCaseUuid", taxcorpCaseUuid);
                List<G01TaxcorpRepresentative> aG01TaxcorpRepresentativeList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                        G01TaxcorpRepresentative.class, "G01TaxcorpRepresentative.findByTaxcorpCaseUuid", params);
                G01User aG01User;
                for (G01TaxcorpRepresentative aG01TaxcorpRepresentative : aG01TaxcorpRepresentativeList){
                    aG01TaxcorpRepresentative.setEntityStatus(entityStatus.value());
                    GardenJpaUtils.storeEntity(em, G01TaxcorpRepresentative.class, aG01TaxcorpRepresentative, aG01TaxcorpRepresentative.getG01TaxcorpRepresentativePK(), 
                            G01DataUpdaterFactory.getSingleton().getG01TaxcorpRepresentativeUpdater());
                    /**
                    * NOTICE: every year, a customer do tax filing. And the taxpayer's information (e.g. the part stored by G01User) 
                    * might be changed. So, it demands a copy of user information every year. It is NOT necessary to check redundancy 
                    * here as long as, in the current year, the primary taxpayer is not rededunant, which is done by checkCaseUniqueness
                    */
                    aG01User = GardenJpaUtils.findById(em, G01User.class, aG01TaxcorpRepresentative.getG01TaxcorpRepresentativePK().getRepresentativeUserUuid());
                    if (aG01User != null){
                        aG01User.setEntityStatus(entityStatus.value());
                        GardenJpaUtils.storeEntity(em, G01User.class, aG01User, aG01User.getUserUuid(), 
                                G01DataUpdaterFactory.getSingleton().getG01UserUpdater());
                        //G01ContactInfo
                        params.clear();
                        params.put("entityUuid", aG01User.getUserUuid());
                        List<G01ContactInfo> aG01ContactInfoList = GardenJpaUtils.findEntityListByNamedQuery(em, G01ContactInfo.class, "G01ContactInfo.findByEntityUuid", params);
                        for (G01ContactInfo aG01ContactInfo : aG01ContactInfoList){
                            aG01ContactInfo.setEntityStatus(entityStatus.value());
                            GardenJpaUtils.storeEntity(em, G01ContactInfo.class, aG01ContactInfo, aG01ContactInfo.getContactInfoUuid(), 
                                    G01DataUpdaterFactory.getSingleton().getG01ContactInfoUpdater());
                        }
                        //G01Location
                        params.clear();
                        params.put("entityUuid", aG01User.getUserUuid());
                        List<G01Location> aG01LocationList = GardenJpaUtils.findEntityListByNamedQuery(em, G01Location.class, "G01Location.findByEntityUuid", params);
                        for (G01Location aG01Location : aG01LocationList){
                            aG01Location.setEntityStatus(entityStatus.value());
                            GardenJpaUtils.storeEntity(em, G01Location.class, aG01Location, aG01Location.getLocationUuid(), 
                                    G01DataUpdaterFactory.getSingleton().getG01LocationUpdater());
                        }
                    }
                }
                //TaxFilingTypeProfile
                params.clear();
                params.put("taxcorpCaseUuid", taxcorpCaseUuid);
                List<G01TaxFilingType> aG01TaxFilingTypeList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                        G01TaxFilingType.class, "G01TaxFilingType.findByTaxcorpCaseUuid", params);
                List<G01TaxFiling> aG01TaxFilingList;
                for (G01TaxFilingType aG01TaxFilingType : aG01TaxFilingTypeList){
                    aG01TaxFilingType.setEntityStatus(entityStatus.value());
                    GardenJpaUtils.storeEntity(em, G01TaxFilingType.class, aG01TaxFilingType, aG01TaxFilingType.getG01TaxFilingTypePK(), 
                            G01DataUpdaterFactory.getSingleton().getG01TaxFilingTypeUpdater());
                    aG01TaxFilingList = super.findTaxFilingEntityListByTaxFilingTypePkid(em, aG01TaxFilingType.getG01TaxFilingTypePK());
                    for (G01TaxFiling aG01TaxFiling : aG01TaxFilingList){
                        aG01TaxFiling.setEntityStatus(entityStatus.value());
                        GardenJpaUtils.storeEntity(em, G01TaxFiling.class, aG01TaxFiling, aG01TaxFiling.getTaxFilingUuid(), 
                                G01DataUpdaterFactory.getSingleton().getG01TaxFilingUpdater());
                    }
                }
            }
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            //Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                //Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void deleteTaxcorpCaseEntity(String taxcorpCaseUuid) throws Exception{
        if (ZcaValidator.isNullEmpty(taxcorpCaseUuid)){
            return;
        }
        EntityManager em = null;
        UserTransaction utx = getUserTransaction();
        //delete associated entities members
        try {
            utx.begin();
            em = getEntityManager();
            
            G01TaxcorpCase aG01TaxcorpCase = GardenJpaUtils.findById(em, G01TaxcorpCase.class, taxcorpCaseUuid);
            if (aG01TaxcorpCase != null){
                G01TaxcorpCaseBk aG01TaxcorpCaseBk = new G01TaxcorpCaseBk();
                aG01TaxcorpCaseBk.setAgreementSignature(aG01TaxcorpCase.getAgreementSignature());
                aG01TaxcorpCaseBk.setAgreementSignatureTimestamp(aG01TaxcorpCase.getAgreementSignatureTimestamp());
                aG01TaxcorpCaseBk.setAgreementUuid(aG01TaxcorpCase.getAgreementUuid());
                aG01TaxcorpCaseBk.setBusinessPurpose(aG01TaxcorpCase.getBusinessPurpose());
                aG01TaxcorpCaseBk.setBusinessStatus(aG01TaxcorpCase.getBusinessStatus());
                aG01TaxcorpCaseBk.setBusinessType(aG01TaxcorpCase.getBusinessType());
                aG01TaxcorpCaseBk.setCorporateName(aG01TaxcorpCase.getCorporateName());
                aG01TaxcorpCaseBk.setCorporateEmail(aG01TaxcorpCase.getCorporateEmail());
                aG01TaxcorpCaseBk.setCorporateFax(aG01TaxcorpCase.getCorporateFax());
                aG01TaxcorpCaseBk.setCorporatePhone(aG01TaxcorpCase.getCorporatePhone());
                aG01TaxcorpCaseBk.setCorporateWebPresence(aG01TaxcorpCase.getCorporateWebPresence());
                aG01TaxcorpCaseBk.setCustomerAccountUuid(aG01TaxcorpCase.getCustomerAccountUuid());
                aG01TaxcorpCaseBk.setEmployeeAccountUuid(aG01TaxcorpCase.getEmployeeAccountUuid());
                aG01TaxcorpCaseBk.setDosDate(aG01TaxcorpCase.getDosDate());
                aG01TaxcorpCaseBk.setEinNumber(aG01TaxcorpCase.getEinNumber());
                aG01TaxcorpCaseBk.setMemo(aG01TaxcorpCase.getMemo());
                aG01TaxcorpCaseBk.setLatestLogUuid(aG01TaxcorpCase.getLatestLogUuid());
                aG01TaxcorpCaseBk.setTaxcorpState(aG01TaxcorpCase.getTaxcorpState());
                aG01TaxcorpCaseBk.setEntityStatus(aG01TaxcorpCase.getEntityStatus());
                aG01TaxcorpCaseBk.setTaxcorpCaseUuid(aG01TaxcorpCase.getTaxcorpCaseUuid());
                aG01TaxcorpCaseBk.setCreated(aG01TaxcorpCase.getCreated());
                
                GardenJpaUtils.storeEntity(em, G01TaxcorpCaseBk.class, aG01TaxcorpCaseBk, aG01TaxcorpCaseBk.getTaxcorpCaseUuid(), 
                        G01DataUpdaterFactory.getSingleton().getG01TaxcorpCaseBkUpdater());
                
                GardenJpaUtils.deleteEntity(em, G01TaxcorpCase.class, taxcorpCaseUuid);
            }
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            //Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                //Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void deleteTaxFilingProfileList(List<TaxFilingProfile> aTaxFilingProfileList) throws Exception{
        if ((aTaxFilingProfileList == null) || (aTaxFilingProfileList.isEmpty())){
            return;
        }
        EntityManager em = null;
        UserTransaction utx = getUserTransaction();
        //delete associated entities members
        try {
            utx.begin();
            em = getEntityManager();
            
            for (TaxFilingProfile aTaxFilingProfile : aTaxFilingProfileList){
                GardenJpaUtils.deleteEntity(em, G01TaxFiling.class, aTaxFilingProfile.getTaxFilingEntity().getTaxFilingUuid());
            }
            
            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            //Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                //Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public List<G01TaxcorpCase> findG01TaxcorpCaseListByFeaturedDateRangeFields(String dateField, Date dateFrom, Date dateTo){
        List<G01TaxcorpCase> result = new ArrayList<>();
        if ((dateFrom != null) && (dateTo != null)){
            EntityManager em = getEntityManager();
            try {
                String sqlQuery = "SELECT g FROM G01TaxcorpCase g WHERE g." + dateField + " BETWEEN :dateFrom AND :dateTo";
                HashMap<String, Object> params = new HashMap<>();
                params.put("dateFrom", dateFrom);
                params.put("dateTo", dateTo);
                result = GardenJpaUtils.findEntityListByQuery(em, G01TaxcorpCase.class, sqlQuery, params);
            } finally {
                em.close();
            }
        }
        return result;
    }

    public List<TaxFilingProfile> findTaxcorpCaseProfileListByDeadlineDateRangeWithTypeAndPeriod(TaxFilingType type, 
                                                                                                 TaxFilingPeriod period, 
                                                                                                 Date dateFrom, 
                                                                                                 Date dateTo) 
    {
        List<TaxFilingProfile> result = new ArrayList<>();
        if ((period == null) || (TaxFilingPeriod.UNKNOWN.equals(period))){
            return findTaxcorpCaseProfileListByDeadlineDateRangeWithType(type, dateFrom, dateTo) ;
        }else{
            if ((type != null) && (dateFrom != null) && (dateTo != null)){
                /**
                 * Based on deadline
                 */
                String sqlQuery = "SELECT g FROM G01TaxFiling g WHERE g.taxFilingType = :taxFilingType "
                        + "AND g.taxFilingPeriod = :taxFilingPeriod "
                        + "AND (g.deadline BETWEEN :dateFrom AND :dateTo)";
                HashMap<String, Object> params = new HashMap<>();
                params.put("taxFilingType", type.value());
                params.put("taxFilingPeriod", period.value());
                params.put("dateFrom", dateFrom);
                params.put("dateTo", dateTo);
                result = findTaxFilingProfileListByDeadlineDateRangeHelper(sqlQuery, params);
                /**
                 * Based on extensionDate
                 */
                sqlQuery = "SELECT g FROM G01TaxFiling g WHERE g.taxFilingType = :taxFilingType "
                        + "AND g.taxFilingPeriod = :taxFilingPeriod "
                        + "AND (g.extensionDate BETWEEN :dateFrom AND :dateTo)";
                params.clear();
                params.put("taxFilingType", type.value());
                params.put("taxFilingPeriod", period.value());
                params.put("dateFrom", dateFrom);
                params.put("dateTo", dateTo);
                result.addAll(findTaxFilingProfileListByDeadlineDateRangeHelper(sqlQuery, params));
            }
        }
        return result;
    }

    public List<TaxFilingProfile> findTaxcorpCaseProfileListByDeadlineDateRangeWithType(TaxFilingType type, 
                                                                                          Date dateFrom, 
                                                                                          Date dateTo) 
    {
        List<TaxFilingProfile> result = new ArrayList<>();
        if ((type != null) && (dateFrom != null) && (dateTo != null)){
            /**
             * Based on deadline
             */
            String sqlQuery = "SELECT g FROM G01TaxFiling g WHERE g.taxFilingType = :taxFilingType AND (g.deadline BETWEEN :dateFrom AND :dateTo)";
            HashMap<String, Object> params = new HashMap<>();
            params.put("taxFilingType", type.value());
            params.put("dateFrom", dateFrom);
            params.put("dateTo", dateTo);
            result = findTaxFilingProfileListByDeadlineDateRangeHelper(sqlQuery, params);
            /**
             * Based on extensionDate
             */
            sqlQuery = "SELECT g FROM G01TaxFiling g WHERE g.taxFilingType = :taxFilingType AND (g.extensionDate BETWEEN :dateFrom AND :dateTo)";
            params.clear();
            params.put("taxFilingType", type.value());
            params.put("dateFrom", dateFrom);
            params.put("dateTo", dateTo);
            result.addAll(findTaxFilingProfileListByDeadlineDateRangeHelper(sqlQuery, params));
        }
        return result;
    }

    public List<TaxFilingProfile> findTaxcorpCaseProfileListByDeadlineDateRange(Date dateFrom, Date dateTo) {
        List<TaxFilingProfile> result = new ArrayList<>();
        if ((dateFrom != null) && (dateTo != null)){
            /**
             * Based on deadline
             */
            String sqlQuery = "SELECT g FROM G01TaxFiling g WHERE g.deadline BETWEEN :dateFrom AND :dateTo";
            HashMap<String, Object> params = new HashMap<>();
            params.put("dateFrom", dateFrom);
            params.put("dateTo", dateTo);
            result = findTaxFilingProfileListByDeadlineDateRangeHelper(sqlQuery, params);
            /**
             * Based on extensionDate
             */
            sqlQuery = "SELECT g FROM G01TaxFiling g WHERE g.extensionDate BETWEEN :dateFrom AND :dateTo";
            params.clear();
            params.put("dateFrom", dateFrom);
            params.put("dateTo", dateTo);
            result.addAll(findTaxFilingProfileListByDeadlineDateRangeHelper(sqlQuery, params));
        }
        return result;
    }

    private List<TaxFilingProfile> findTaxFilingProfileListByDeadlineDateRangeHelper(String sqlQuery, HashMap<String, Object> params) {
        List<TaxFilingProfile> result = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            List<G01TaxFiling> aG01TaxFilingList = GardenJpaUtils.findEntityListByQuery(em, G01TaxFiling.class, sqlQuery, params);
            G01TaxcorpCase aG01TaxcorpCase;
            TaxFilingProfile aTaxFilingProfile;
            for (G01TaxFiling aG01TaxFiling : aG01TaxFilingList){
                aG01TaxcorpCase = GardenJpaUtils.findById(em, G01TaxcorpCase.class, aG01TaxFiling.getTaxcorpCaseUuid());
                if (aG01TaxcorpCase != null){
                    aTaxFilingProfile = new TaxFilingProfile();
                    aTaxFilingProfile.setTaxFilingEntity(aG01TaxFiling);
                    aTaxFilingProfile.setTaxcorpCaseEntity(aG01TaxcorpCase);
                    aTaxFilingProfile.setTaxcorpRepresentativeProfileList(findTaxcorpRepresentativeProfileListByTaxcorpCaseUuid(em, aG01TaxFiling.getTaxcorpCaseUuid(), null));
                    result.add(aTaxFilingProfile);
                }
            }
        } finally {
            em.close();
        }
        
        if (!result.isEmpty()){
            Collections.sort(result, (TaxFilingProfile o1, TaxFilingProfile o2) -> o1.getTaxcorpCaseEntity().getCorporateName().compareTo(o2.getTaxcorpCaseEntity().getCorporateName()));
        }
        
        return result;
    }

    public List<TaxcorpFilingConciseProfile> findUrgentTaxcorpCaseProfileList(int daysBeforeDeadlineForTaxpayer) {
        List<TaxcorpFilingConciseProfile> result = new ArrayList<>();
        
        Date today = ZcaCalendar.covertDateToBeginning(new Date());
        Date deadline = ZcaCalendar.covertDateToEnding(ZcaCalendar.addDates(today, daysBeforeDeadlineForTaxpayer));
        
        String sqlQuery = "SELECT g FROM G01TaxFiling g WHERE g.deadline BETWEEN :dateFrom AND :dateTo";
        HashMap<String, Object> params = new HashMap<>();
        params.put("dateFrom", today);
        params.put("dateTo", deadline);
        EntityManager em = getEntityManager();
        try {
            List<G01TaxFiling> aG01TaxFilingList = GardenJpaUtils.findEntityListByQuery(em, G01TaxFiling.class, sqlQuery, params);
            TaxcorpFilingConciseProfile aTaxcorpFilingConciseProfile;
            G01TaxcorpCase aG01TaxcorpCase;
            for (G01TaxFiling aG01TaxFiling : aG01TaxFilingList){
                aG01TaxcorpCase = GardenJpaUtils.findById(em, G01TaxcorpCase.class, aG01TaxFiling.getTaxcorpCaseUuid());
                if (aG01TaxcorpCase != null){
                    aTaxcorpFilingConciseProfile = new TaxcorpFilingConciseProfile();
                    aTaxcorpFilingConciseProfile.setTaxcorpCase(aG01TaxcorpCase);
                    aTaxcorpFilingConciseProfile.setTaxFiling(aG01TaxFiling);
                    result.add(aTaxcorpFilingConciseProfile);
                }
            }
        } finally {
            em.close();
        }
        return result;
    }

    public List<TaxcorpBillBalanceProfile> findTaxcorpBillBalanceProfileListByDueRange(Date dueFrom, Date dueTo) {
        List<TaxcorpBillBalanceProfile> result = new ArrayList<>();
        if ((dueFrom != null) && (dueTo != null)){
            String sqlQuery = "SELECT g FROM G01Bill g WHERE g.entityType = :entityType AND g.billDatetime BETWEEN :dueFrom AND :dueTo";
            HashMap<String, Object> params = new HashMap<>();
            params.put("entityType", GardenEntityType.TAXCORP_CASE.name());
            params.put("dueFrom", dueFrom);
            params.put("dueTo", dueTo);
            EntityManager em = getEntityManager();
            try {
                List<G01Bill> aG01BillList = GardenJpaUtils.findEntityListByQuery(em, G01Bill.class, sqlQuery, params);
                if (aG01BillList != null){
                    TaxcorpBillBalanceProfile aTaxcorpBillBalanceProfile;
                    G01TaxcorpCase taxcorpCase;
                    List<G01Payment> aG01PaymentList;
                    for (G01Bill aG01Bill : aG01BillList){
                        taxcorpCase = GardenJpaUtils.findById(em, G01TaxcorpCase.class, aG01Bill.getEntityUuid());
                        if (taxcorpCase != null){
                            aTaxcorpBillBalanceProfile = new TaxcorpBillBalanceProfile();
                            aTaxcorpBillBalanceProfile.setBill(aG01Bill);
                            aTaxcorpBillBalanceProfile.setTaxcorpCase(taxcorpCase);
                            params.clear();
                            params.put("billUuid", aG01Bill.getBillUuid());
                            aG01PaymentList = GardenJpaUtils.findEntityListByNamedQuery(em, G01Payment.class, "G01Payment.findByBillUuid", params);
                            if (aG01PaymentList != null){
                                aTaxcorpBillBalanceProfile.setPaymentList(aG01PaymentList);
                            }
                            result.add(aTaxcorpBillBalanceProfile);
                        }
                    }//for-loop
                }
            } finally {
                em.close();
            }
        }
        return result;
    }

    public void storeTaxcorpRepresentativeProfileList(String taxcorpCaseUuid,
                                                      List<TaxcorpRepresentativeProfile> aTaxcorpRepresentativeProfileList) 
            throws  IllegalStateException, 
                    SecurityException, 
                    SystemException, 
                    RollbackException, 
                    HeuristicMixedException, 
                    HeuristicRollbackException, 
                    NotSupportedException, 
                    ZcaEntityValidationException 
    {
        EntityManager em = null;
        UserTransaction utx = getUserTransaction();
        //delete associated entities members
        try {
            utx.begin();
            em = getEntityManager();
    
            G01TaxcorpCase aG01TaxcorpCase = GardenJpaUtils.findById(em, G01TaxcorpCase.class, taxcorpCaseUuid);
            if (aG01TaxcorpCase == null){
                throw new ZcaEntityValidationException(RoseText.getText("TaxcorpEntityDemanded_T"));
            }
            
            HashMap<String, Object> params = new HashMap<>();
            //G01TaxcorpRepresentative
            params.put("taxcorpCaseUuid", taxcorpCaseUuid);
            List<G01TaxcorpRepresentative> aG01TaxcorpRepresentativeList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                    G01TaxcorpRepresentative.class, "G01TaxcorpRepresentative.findByTaxcorpCaseUuid", params);
            //Primary location
            List<G01Location> aG01LocationList;
            String userUuid;
            for (G01TaxcorpRepresentative aG01TaxcorpRepresentative : aG01TaxcorpRepresentativeList){
                /**
                 * NOTICE: every year, a customer do tax filing. And the taxcorp's information (e.g. the part stored by G01User) 
                 * might be changed. So, it demands a copy of user information every year. It is NOT necessary to check redundancy 
                 * here as long as, in the current year, the primary taxcorp is not rededunant, which is done by checkCaseUniqueness
                 */
                userUuid = aG01TaxcorpRepresentative.getG01TaxcorpRepresentativePK().getRepresentativeUserUuid();
                GardenJpaUtils.deleteEntity(em, G01User.class, userUuid);
                //G01Location
                params.clear();
                params.put("entityUuid", userUuid);
                aG01LocationList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                        G01Location.class, "G01Location.findByEntityUuid", params);
                for (G01Location aG01Location : aG01LocationList){
                    GardenJpaUtils.deleteEntity(em, G01Location.class, aG01Location.getLocationUuid());
                }
                //G01ContactInfo
                List<G01ContactInfo> aG01ContactInfoList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                        G01ContactInfo.class, "G01ContactInfo.findByEntityUuid", params);
                for (G01ContactInfo aG01ContactInfo : aG01ContactInfoList){
                    GardenJpaUtils.deleteEntity(em, G01ContactInfo.class, aG01ContactInfo.getContactInfoUuid());
                }
                GardenJpaUtils.deleteEntity(em, G01TaxcorpRepresentative.class, aG01TaxcorpRepresentative.getG01TaxcorpRepresentativePK());
            }
            
            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            //Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                //Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        utx = getUserTransaction();
        //delete associated entities members
        try {
            utx.begin();
            em = getEntityManager();
            //G01TaxcorpRepresentative
            G01TaxcorpRepresentative aG01TaxcorpRepresentative;
            G01User aG01User;
            List<RoseLocationProfile> aRoseLocationProfileList;
            List<RoseContactInfoProfile> aRoseContactInfoProfileList;
            for (TaxcorpRepresentativeProfile aTaxcorpRepresentativeProfile : aTaxcorpRepresentativeProfileList){
                //G01TaxcorpRepresentative
                aG01TaxcorpRepresentative = aTaxcorpRepresentativeProfile.getTaxcorpRepresentativeEntity();
                GardenJpaUtils.storeEntity(em, G01TaxcorpRepresentative.class, aG01TaxcorpRepresentative, 
                        aG01TaxcorpRepresentative.getG01TaxcorpRepresentativePK(), G01DataUpdaterFactory.getSingleton().getG01TaxcorpRepresentativeUpdater());
                //G01User
                aG01User = aTaxcorpRepresentativeProfile.getUserEntity();
                GardenJpaUtils.storeEntity(em, G01User.class, aG01User, aG01User.getUserUuid(), G01DataUpdaterFactory.getSingleton().getG01UserUpdater());
                //G01Location
                aRoseLocationProfileList = aTaxcorpRepresentativeProfile.getUserLocationProfileList();
                for (RoseLocationProfile aRoseLocationProfile : aRoseLocationProfileList){
                    GardenJpaUtils.storeEntity(em, G01Location.class, aRoseLocationProfile.getLocationEntity(), 
                            aRoseLocationProfile.getLocationEntity().getLocationUuid(), G01DataUpdaterFactory.getSingleton().getG01LocationUpdater());
                }
                //G01ContactInfo
                aRoseContactInfoProfileList = aTaxcorpRepresentativeProfile.getUserContactInfoProfileList();
                for (RoseContactInfoProfile aRoseContactInfoProfile : aRoseContactInfoProfileList){
                    GardenJpaUtils.storeEntity(em, G01ContactInfo.class, aRoseContactInfoProfile.getContactInfoEntity(), 
                            aRoseContactInfoProfile.getContactInfoEntity().getContactInfoUuid(), G01DataUpdaterFactory.getSingleton().getG01ContactInfoUpdater());
                }
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            //Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                //Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void storeTaxFilingContactMessage(G01ContactMessage aG01ContactMessage, List<G01ContactEntity> aG01ContactEntityList) throws Exception{
        if ((aG01ContactMessage == null) || (aG01ContactEntityList == null)){
            return;
        }
        EntityManager em = null;
        UserTransaction utx = getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            //G01ContactMessage
            GardenJpaUtils.storeEntity(em, G01ContactMessage.class, aG01ContactMessage, aG01ContactMessage.getContactMessageUuid(), 
                                    G01DataUpdaterFactory.getSingleton().getG01ContactMessageUpdater());
            //aG01ContactEntityList
            for (G01ContactEntity aG01ContactEntity : aG01ContactEntityList){
                GardenJpaUtils.storeEntity(em, G01ContactEntity.class, aG01ContactEntity, aG01ContactEntity.getG01ContactEntityPK(), 
                                        G01DataUpdaterFactory.getSingleton().getG01ContactEntityUpdater());
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            //Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                //Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

}
