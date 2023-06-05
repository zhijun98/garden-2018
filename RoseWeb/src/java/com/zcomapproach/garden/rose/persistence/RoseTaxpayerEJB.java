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
import com.zcomapproach.garden.persistence.constant.GardenEntityStatus;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.constant.GardenTaxpayerCaseStatus;
import com.zcomapproach.garden.persistence.constant.TaxpayerRelationship;
import com.zcomapproach.garden.persistence.entity.G01Account;
import com.zcomapproach.garden.persistence.entity.G01BusinessVehicle;
import com.zcomapproach.garden.persistence.entity.G01ContactInfo;
import com.zcomapproach.garden.persistence.entity.G01Location;
import com.zcomapproach.garden.persistence.entity.G01Log;
import com.zcomapproach.garden.persistence.entity.G01PersonalBusinessProperty;
import com.zcomapproach.garden.persistence.entity.G01PersonalProperty;
import com.zcomapproach.garden.persistence.entity.G01TaxpayerCase;
import com.zcomapproach.garden.persistence.entity.G01TaxpayerCaseBk;
import com.zcomapproach.garden.persistence.entity.G01TaxpayerInfo;
import com.zcomapproach.garden.persistence.entity.G01TlcLicense;
import com.zcomapproach.garden.persistence.entity.G01User;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.garden.rose.data.profile.PersonalBusinessPropertyProfile;
import com.zcomapproach.garden.rose.data.profile.PersonalPropertyProfile;
import com.zcomapproach.garden.rose.data.profile.RoseAccountProfile;
import com.zcomapproach.garden.rose.data.profile.RoseContactInfoProfile;
import com.zcomapproach.garden.rose.data.profile.RoseLocationProfile;
import com.zcomapproach.garden.rose.data.profile.RoseUserProfile;
import com.zcomapproach.garden.rose.data.profile.TaxpayerCaseProfile;
import com.zcomapproach.garden.rose.data.profile.TaxpayerCaseConciseProfile;
import com.zcomapproach.garden.rose.data.profile.TaxpayerInfoProfile;
import com.zcomapproach.garden.rose.data.profile.TlcLicenseProfile;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
public class RoseTaxpayerEJB extends AbstractDataServiceEJB {

    public void storeTargetTaxpayerCaseProfile(TaxpayerCaseProfile aTaxpayerCaseProfile) throws IllegalStateException, 
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
            
            String taxpayerCaseUuid = aTaxpayerCaseProfile.getTaxpayerCaseEntity().getTaxpayerCaseUuid();
            HashMap<String, Object> params = new HashMap<>();
            //G01TaxpayerInfo
            params.put("taxpayerCaseUuid", taxpayerCaseUuid);
            List<G01TaxpayerInfo> aG01TaxpayerInfoList = GardenJpaUtils.findEntityListByNamedQuery(em, G01TaxpayerInfo.class, "G01TaxpayerInfo.findByTaxpayerCaseUuid", params);
            for (G01TaxpayerInfo aG01TaxpayerInfo : aG01TaxpayerInfoList){
                GardenJpaUtils.deleteEntity(em, G01TaxpayerInfo.class, aG01TaxpayerInfo.getTaxpayerUserUuid());
                /**
                 * NOTICE: every year, a customer do tax filing. And the taxpayer's information (e.g. the part stored by G01User) 
                 * might be changed. So, it demands a copy of user information every year. It is NOT necessary to check redundancy 
                 * here as long as, in the current year, the primary taxpayer is not rededunant, which is done by checkCaseUniqueness
                 */
                if (GardenJpaUtils.findById(em, G01Account.class, aG01TaxpayerInfo.getTaxpayerUserUuid()) == null){   //this user has customer-account
                    GardenJpaUtils.deleteEntity(em, G01User.class, aG01TaxpayerInfo.getTaxpayerUserUuid()); //read the above comments
                }
            }
            //G01Location
            params.clear();
            params.put("entityUuid", taxpayerCaseUuid);
            List<G01Location> aG01LocationList = GardenJpaUtils.findEntityListByNamedQuery(em, G01Location.class, "G01Location.findByEntityUuid", params);
            for (G01Location aG01Location : aG01LocationList){
                GardenJpaUtils.deleteEntity(em, G01Location.class, aG01Location.getLocationUuid());
            }
            //G01ContactInfo
            params.clear();
            params.put("entityUuid", taxpayerCaseUuid);
            List<G01ContactInfo> aG01ContactInfoList = GardenJpaUtils.findEntityListByNamedQuery(em, G01ContactInfo.class, "G01ContactInfo.findByEntityUuid", params);
            for (G01ContactInfo aG01ContactInfo : aG01ContactInfoList){
                GardenJpaUtils.deleteEntity(em, G01ContactInfo.class, aG01ContactInfo.getContactInfoUuid());
            }
            //G01PersonalProperty
            params.clear();
            params.put("taxpayerCaseUuid", taxpayerCaseUuid);
            List<G01PersonalProperty> aG01PersonalPropertyList = GardenJpaUtils.findEntityListByNamedQuery(em, G01PersonalProperty.class, "G01PersonalProperty.findByTaxpayerCaseUuid", params);
            for (G01PersonalProperty aG01PersonalProperty : aG01PersonalPropertyList){
                GardenJpaUtils.deleteEntity(em, G01PersonalProperty.class, aG01PersonalProperty.getPersonalPropertyUuid());
            }
            //G01PersonalBusinessProperty
            params.clear();
            params.put("taxpayerCaseUuid", taxpayerCaseUuid);
            List<G01PersonalBusinessProperty> aG01PersonalBusinessPropertyList = GardenJpaUtils.findEntityListByNamedQuery(em, G01PersonalBusinessProperty.class, "G01PersonalBusinessProperty.findByTaxpayerCaseUuid", params);
            for (G01PersonalBusinessProperty aG01PersonalBusinessProperty : aG01PersonalBusinessPropertyList){
                GardenJpaUtils.deleteEntity(em, G01PersonalBusinessProperty.class, aG01PersonalBusinessProperty.getPersonalBusinessPropertyUuid());
            }
            //G01TlcLicense
            params.clear();
            params.put("taxpayerCaseUuid", taxpayerCaseUuid);
            List<G01TlcLicense> aG01TlcLicenseList = GardenJpaUtils.findEntityListByNamedQuery(em, G01TlcLicense.class, "G01TlcLicense.findByTaxpayerCaseUuid", params);
            for (G01TlcLicense aG01TlcLicense : aG01TlcLicenseList){
                GardenJpaUtils.deleteEntity(em, G01TlcLicense.class, aG01TlcLicense.getDriverUuid());
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
            //G01Log-Setup
            String sqlQuery = "SELECT g FROM G01Log g WHERE g.logMsg = :logMsg AND g.operatorAccountUuid = :operatorAccountUuid";
            HashMap<String, Object> params = new HashMap<>();
            params.put("logMsg", GardenTaxpayerCaseStatus.SETUP_TAXPAYER_CASE.value());
            params.put("operatorAccountUuid", aTaxpayerCaseProfile.getTaxpayerCaseEntity().getEmployeeAccountUuid());
            List<G01Log> setupLogList = GardenJpaUtils.findEntityListByQuery(em, G01Log.class, sqlQuery, params);
            if (setupLogList.isEmpty()){
                G01Log aG01Log = new G01Log();
                aG01Log.setLogUuid(GardenData.generateUUIDString());
                aG01Log.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
                aG01Log.setEntityUuid(aTaxpayerCaseProfile.getTaxpayerCaseEntity().getTaxpayerCaseUuid());
                aG01Log.setLogMsg(GardenTaxpayerCaseStatus.SETUP_TAXPAYER_CASE.value());
                aG01Log.setOperatorAccountUuid(aTaxpayerCaseProfile.getTaxpayerCaseEntity().getEmployeeAccountUuid());
                aG01Log.setTimestamp(new Date());
                
                GardenJpaUtils.storeEntity(em, G01Log.class, aG01Log, aG01Log.getLogUuid(), G01DataUpdaterFactory.getSingleton().getG01LogUpdater());
                
                aTaxpayerCaseProfile.getTaxpayerCaseEntity().setLatestLogUuid(aG01Log.getLogUuid());
            }
            //G01TaxpayerCase
            GardenJpaUtils.storeEntity(em, G01TaxpayerCase.class, aTaxpayerCaseProfile.getTaxpayerCaseEntity(), 
                    aTaxpayerCaseProfile.getTaxpayerCaseEntity().getTaxpayerCaseUuid(), G01DataUpdaterFactory.getSingleton().getG01TaxpayerCaseUpdater());
            //G01TaxpayerInfo with G01User - primary
            GardenJpaUtils.storeEntity(em, G01TaxpayerInfo.class, aTaxpayerCaseProfile.getPrimaryTaxpayerProfile().getTaxpayerInfoEntity(), 
                    aTaxpayerCaseProfile.getPrimaryTaxpayerProfile().getTaxpayerInfoEntity().getTaxpayerUserUuid(), G01DataUpdaterFactory.getSingleton().getG01TaxpayerInfoUpdater());
            GardenJpaUtils.storeEntity(em, G01User.class, aTaxpayerCaseProfile.getPrimaryTaxpayerProfile().getRoseUserProfile().getUserEntity(), 
                    aTaxpayerCaseProfile.getPrimaryTaxpayerProfile().getRoseUserProfile().getUserEntity().getUserUuid(), G01DataUpdaterFactory.getSingleton().getG01UserUpdater());
            //G01TaxpayerInfo with G01User - spouse
            if (aTaxpayerCaseProfile.isSpouseRequired()){
                GardenJpaUtils.storeEntity(em, G01TaxpayerInfo.class, aTaxpayerCaseProfile.getSpouseProfile().getTaxpayerInfoEntity(), 
                        aTaxpayerCaseProfile.getSpouseProfile().getTaxpayerInfoEntity().getTaxpayerUserUuid(), G01DataUpdaterFactory.getSingleton().getG01TaxpayerInfoUpdater());
                GardenJpaUtils.storeEntity(em, G01User.class, aTaxpayerCaseProfile.getSpouseProfile().getRoseUserProfile().getUserEntity(), 
                        aTaxpayerCaseProfile.getSpouseProfile().getRoseUserProfile().getUserEntity().getUserUuid(), G01DataUpdaterFactory.getSingleton().getG01UserUpdater());
            }
            //G01TaxpayerInfo with G01User - dependants
            List<TaxpayerInfoProfile> aTaxpayerInfoProfileList = aTaxpayerCaseProfile.getDependantProfileList();
            for (TaxpayerInfoProfile aTaxpayerInfoProfile : aTaxpayerInfoProfileList){
                GardenJpaUtils.storeEntity(em, G01TaxpayerInfo.class, aTaxpayerInfoProfile.getTaxpayerInfoEntity(), 
                        aTaxpayerInfoProfile.getTaxpayerInfoEntity().getTaxpayerUserUuid(), G01DataUpdaterFactory.getSingleton().getG01TaxpayerInfoUpdater());
                GardenJpaUtils.storeEntity(em, G01User.class, aTaxpayerInfoProfile.getRoseUserProfile().getUserEntity(), 
                        aTaxpayerInfoProfile.getRoseUserProfile().getUserEntity().getUserUuid(), G01DataUpdaterFactory.getSingleton().getG01UserUpdater());
            }
            //G01Location
            GardenJpaUtils.storeEntity(em, G01Location.class, aTaxpayerCaseProfile.getPrimaryLocationProfile().getLocationEntity(), 
                    aTaxpayerCaseProfile.getPrimaryLocationProfile().getLocationEntity().getLocationUuid(), G01DataUpdaterFactory.getSingleton().getG01LocationUpdater());
//            //G01ContactInfo
//            GardenJpaUtils.storeEntity(em, G01ContactInfo.class, aTaxpayerCaseProfile.getPrimaryContactInfoProfile().getContactInfoEntity(), 
//                    aTaxpayerCaseProfile.getPrimaryContactInfoProfile().getContactInfoEntity().getContactInfoUuid(), G01DataUpdaterFactory.getSingleton().getG01ContactInfoUpdater());
            //G01PersonalProperty
            List<PersonalPropertyProfile> personalPropertyProfileList = aTaxpayerCaseProfile.getPersonalPropertyProfileList();
            for (PersonalPropertyProfile aPersonalPropertyProfile : personalPropertyProfileList){
                GardenJpaUtils.storeEntity(em, G01PersonalProperty.class, aPersonalPropertyProfile.getPersonalPropertyEntity(), 
                        aPersonalPropertyProfile.getPersonalPropertyEntity().getPersonalPropertyUuid(), G01DataUpdaterFactory.getSingleton().getG01PersonalPropertyUpdater());
            }
            //G01PersonalBusinessProperty
            List<PersonalBusinessPropertyProfile> personalBusinessPropertyProfileList = aTaxpayerCaseProfile.getPersonalBusinessPropertyProfileList();
            for (PersonalBusinessPropertyProfile aPersonalBusinessPropertyProfile : personalBusinessPropertyProfileList){
                GardenJpaUtils.storeEntity(em, G01PersonalBusinessProperty.class, aPersonalBusinessPropertyProfile.getPersonalBusinessPropertyEntity(), 
                        aPersonalBusinessPropertyProfile.getPersonalBusinessPropertyEntity().getPersonalBusinessPropertyUuid(), G01DataUpdaterFactory.getSingleton().getG01PersonalBusinessPropertyUpdater());
            }
            //G01TlcLicense
            List<TlcLicenseProfile> tlcLicenseProfileList = aTaxpayerCaseProfile.getTlcLicenseProfileList();
            for (TlcLicenseProfile aTlcLicenseProfile : tlcLicenseProfileList){
                GardenJpaUtils.storeEntity(em, G01TlcLicense.class, aTlcLicenseProfile.getTlcLicenseEntity(), 
                        aTlcLicenseProfile.getTlcLicenseEntity().getDriverUuid(), G01DataUpdaterFactory.getSingleton().getG01TlcLicenseUpdater());
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

    public TaxpayerCaseProfile findTaxpayerCaseProfileByTaxpayerCaseUuid(String taxpayerCaseUuid) {
        TaxpayerCaseProfile result = null;
        EntityManager em = getEntityManager();
        try {
            result = constructTaxpayerCaseProfileByTaxpayerCaseEntity(em, GardenJpaUtils.findById(em, G01TaxpayerCase.class, taxpayerCaseUuid));
        } finally {
            em.close();
        }
        return result;
    }

    /**
     * 
     * @param em
     * @param aG01TaxpayerCase - an instance from the storage
     * @return 
     */
    private TaxpayerCaseProfile constructTaxpayerCaseProfileByTaxpayerCaseEntity(EntityManager em, G01TaxpayerCase aG01TaxpayerCase) {
        if (aG01TaxpayerCase == null){
            return null;
        }
        TaxpayerCaseProfile aTaxpayerCaseProfile = new TaxpayerCaseProfile();
        //taxpayerCaseEntity
        aTaxpayerCaseProfile.setTaxpayerCaseEntity(aG01TaxpayerCase);
        //latestLogEntity
        if (ZcaValidator.isNotNullEmpty(aG01TaxpayerCase.getLatestLogUuid())){
            G01Log log = GardenJpaUtils.findById(em, G01Log.class, aG01TaxpayerCase.getLatestLogUuid());
            if (log != null){
                aTaxpayerCaseProfile.setLatestLogEntity(log);
                aTaxpayerCaseProfile.setLatestLogEmployeeUserRecord(GardenJpaUtils.findById(em, G01User.class, log.getOperatorAccountUuid()));
            }
        }
        String taxpayerCaseUuid = aTaxpayerCaseProfile.getTaxpayerCaseEntity().getTaxpayerCaseUuid();
        //customerProfile
        RoseAccountProfile customerProfile = super.findRoseAccountProfileByAccountUserUuidHelper(em, aG01TaxpayerCase.getCustomerAccountUuid());
        if (customerProfile != null){
            aTaxpayerCaseProfile.setCustomerProfile(customerProfile);
        }
        //businessCaseBillProfileList
        aTaxpayerCaseProfile.setBusinessCaseBillProfileList(findBusinessCaseBillProfileListByFeaturedField("entityUuid", 
                                                                                                            taxpayerCaseUuid, 
                                                                                                            "G01Bill.findByEntityUuid"));
        //documentRequirementprofileList
        aTaxpayerCaseProfile.setDocumentRequirementprofileList(findRoseDocumentRequirementProfileListByFeaturedField("entityUuid", 
                                                                                                                taxpayerCaseUuid, 
                                                                                                                "G01DocumentRequirement.findByEntityUuid"));
        //uploadedArchivedDocumentList
        aTaxpayerCaseProfile.setUploadedArchivedDocumentList(findRoseArchivedDocumentProfileListByFeaturedField("entityUuid", 
                                                                                                                taxpayerCaseUuid, 
                                                                                                                "G01ArchivedDocument.findByEntityUuid"));
        //primaryLocationProfile
        RoseLocationProfile primaryLocationProfile = super.findRoseLocationProfileByEntityUuid(em, taxpayerCaseUuid);
        if (primaryLocationProfile != null){
            aTaxpayerCaseProfile.setPrimaryLocationProfile(primaryLocationProfile);
        }
        //primaryContactInfoProfile
        RoseContactInfoProfile primaryContactInfoProfile = super.findRoseContactInfoProfileByEntityUuid(em, taxpayerCaseUuid);
        if (primaryContactInfoProfile != null){
            aTaxpayerCaseProfile.setPrimaryContactInfoProfile(primaryContactInfoProfile);
        }
        //taxpayers with dependants
        loadTaxpayerInfoProfilesByTaxpayerCaseUuid(em, aTaxpayerCaseProfile);
        //personalBusinessPropertyProfileList
        aTaxpayerCaseProfile.setPersonalBusinessPropertyProfileList(findPersonalBusinessPropertyProfileListByTaxpayerCaseUuid(em, taxpayerCaseUuid));
        //personalPropertyProfileList
        aTaxpayerCaseProfile.setPersonalPropertyProfileList(findPersonalPropertyProfileListByTaxpayerCaseUuid(em, taxpayerCaseUuid));
        //tlcLicenseProfileList
        aTaxpayerCaseProfile.setTlcLicenseProfileList(findTlcLicenseProfileListByTaxpayerCaseUuid(em, taxpayerCaseUuid));
        return aTaxpayerCaseProfile;
    }

    public List<TaxpayerCaseProfile> findUrgentTaxpayerCaseProfileList() {
        List<TaxpayerCaseProfile> result = new ArrayList<>();
//        EntityManager em = getEntityManager();
//        try {
//            String sqlQuery = "SELECT g FROM G01TaxpayerCase g WHERE g.deadline BETWEEN :dateFrom AND :dateTo";
//            HashMap<String, Object> params = new HashMap<>();
//            Date now = new Date();
//            params.put("dateFrom", ZcaCalendar.calculateOffsetDate(now, -1));
//            params.put("dateTo", ZcaCalendar.calculateOffsetDate(now, 450));
//            List<G01TaxpayerCase> aG01TaxpayerCaseList = GardenJpaUtils.findEntityListByQuery(em, G01TaxpayerCase.class, sqlQuery, params);
//            for (G01TaxpayerCase aG01TaxpayerCase : aG01TaxpayerCaseList){
//                result.add(constructTaxpayerCaseProfileByTaxpayerCaseEntity(em, aG01TaxpayerCase));
//            }
//        } finally {
//            em.close();
//        }
        return result;
    }

    public List<TaxpayerCaseProfile> findInvolvedTaxpayerCaseProfileListByUserUuid(String userUuid) {
        List<TaxpayerCaseProfile> result = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            String sqlQuery = "SELECT g FROM G01TaxpayerInfo g WHERE g.taxpayerUserUuid = :taxpayerUserUuid AND (g.relationships != :relationships)";
            HashMap<String, Object> params = new HashMap<>();
            params.put("taxpayerUserUuid", userUuid);
            params.put("relationships", TaxpayerRelationship.PRIMARY_TAXPAYER.value());
            List<G01TaxpayerInfo> aG01TaxpayerInfoList = GardenJpaUtils.findEntityListByQuery(em, G01TaxpayerInfo.class, sqlQuery, params);
            TaxpayerCaseProfile aTaxpayerCaseProfile;
            for (G01TaxpayerInfo aG01TaxpayerInfo : aG01TaxpayerInfoList){
                aTaxpayerCaseProfile = constructTaxpayerCaseProfileByTaxpayerCaseEntity(em, GardenJpaUtils.findById(em, G01TaxpayerCase.class, aG01TaxpayerInfo.getTaxpayerCaseUuid()));
                if (aTaxpayerCaseProfile != null){
                    result.add(aTaxpayerCaseProfile);
                }
            }
        } finally {
            em.close();
        }
        return result;
    }

    public List<TaxpayerCaseProfile> findTaxpayerCaseProfileListByCustomerUuid(String customerUuid) {
        List<TaxpayerCaseProfile> result = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            String sqlQuery = "SELECT g FROM G01TaxpayerCase g WHERE g.customerAccountUuid = :customerAccountUuid and g.entityStatus IS NULL";
            HashMap<String, Object> params = new HashMap<>();
            params.put("customerAccountUuid", customerUuid);
            List<G01TaxpayerCase> aG01TaxpayerCaseList = GardenJpaUtils.findEntityListByQuery(em, G01TaxpayerCase.class, sqlQuery, params);
            for (G01TaxpayerCase aG01TaxpayerCase : aG01TaxpayerCaseList){
                result.add(constructTaxpayerCaseProfileByTaxpayerCaseEntity(em, aG01TaxpayerCase));
            }
        } finally {
            em.close();
        }
        return result;
    }

    private List<G01TaxpayerCase> findTaxpayerCaseEnityListByEmployeeUuidHelper(EntityManager em, String employeeUuid) {
        String sqlQuery = "SELECT g FROM G01TaxpayerCase g WHERE g.employeeAccountUuid = :employeeAccountUuid and g.entityStatus IS NULL";
        HashMap<String, Object> params = new HashMap<>();
        params.put("employeeAccountUuid", employeeUuid);
        return GardenJpaUtils.findEntityListByQuery(em, G01TaxpayerCase.class, sqlQuery, params);
    }

    public List<TaxpayerCaseProfile> findTaxpayerCaseProfileListByEmployeeUuid(String employeeUuid) {
        List<TaxpayerCaseProfile> result = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            List<G01TaxpayerCase> aG01TaxpayerCaseList = findTaxpayerCaseEnityListByEmployeeUuidHelper(em, employeeUuid);
            for (G01TaxpayerCase aG01TaxpayerCase : aG01TaxpayerCaseList){
                result.add(constructTaxpayerCaseProfileByTaxpayerCaseEntity(em, aG01TaxpayerCase));
            }
        } finally {
            em.close();
        }
        return result;
    }

    private void loadTaxpayerInfoProfilesByTaxpayerCaseUuid(EntityManager em, TaxpayerCaseProfile aTaxpayerCaseProfile) {
        String taxpayerCaseUuid = aTaxpayerCaseProfile.getTaxpayerCaseEntity().getTaxpayerCaseUuid();
        HashMap<String, Object> params = new HashMap<>();
        params.put("taxpayerCaseUuid", taxpayerCaseUuid);
        List<G01TaxpayerInfo> aG01TaxpayerInfoList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                G01TaxpayerInfo.class, "G01TaxpayerInfo.findByTaxpayerCaseUuid", params);
        TaxpayerInfoProfile aTaxpayerInfoProfile;
        RoseUserProfile aRoseUserProfile;
        for (G01TaxpayerInfo aG01TaxpayerInfo : aG01TaxpayerInfoList){
            aTaxpayerInfoProfile = new TaxpayerInfoProfile();
            aTaxpayerInfoProfile.setTaxpayerInfoEntity(aG01TaxpayerInfo);
            
            aRoseUserProfile = super.findRoseUserProfileByUserUuid(em, aG01TaxpayerInfo.getTaxpayerUserUuid());
            if (aRoseUserProfile != null){
                aTaxpayerInfoProfile.setRoseUserProfile(aRoseUserProfile);
                switch (TaxpayerRelationship.convertEnumValueToType(aG01TaxpayerInfo.getRelationships())){
                    case PRIMARY_TAXPAYER:
                        aTaxpayerCaseProfile.setPrimaryTaxpayerProfile(aTaxpayerInfoProfile);
                        break;
                    case SPOUSE_TAXPAYER:
                        aTaxpayerCaseProfile.setSpouseRequired(true);
                        aTaxpayerCaseProfile.setSpouseProfile(aTaxpayerInfoProfile);
                        break;
                    default:
                        aTaxpayerCaseProfile.getDependantProfileList().add(aTaxpayerInfoProfile);
                }
            }
        }
    }

    private List<TlcLicenseProfile> findTlcLicenseProfileListByTaxpayerCaseUuid(EntityManager em, String taxpayerCaseUuid) 
    {
        List<TlcLicenseProfile> result = new ArrayList<>();
        HashMap<String, Object> params = new HashMap<>();
        params.put("taxpayerCaseUuid", taxpayerCaseUuid);
        List<G01TlcLicense> aG01TlcLicenseList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                G01TlcLicense.class, "G01TlcLicense.findByTaxpayerCaseUuid", params);
        TlcLicenseProfile aTlcLicenseProfile;
        for (G01TlcLicense aG01TlcLicense : aG01TlcLicenseList){
            aTlcLicenseProfile = new TlcLicenseProfile();
            aTlcLicenseProfile.setTlcLicenseEntity(aG01TlcLicense);
            result.add(aTlcLicenseProfile);
        }
        
        return result;
    }

    private List<PersonalPropertyProfile> findPersonalPropertyProfileListByTaxpayerCaseUuid(EntityManager em, 
                                                                                            String taxpayerCaseUuid) 
    {
        List<PersonalPropertyProfile> result = new ArrayList<>();
        HashMap<String, Object> params = new HashMap<>();
        params.put("taxpayerCaseUuid", taxpayerCaseUuid);
        List<G01PersonalProperty> aG01PersonalPropertyList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                G01PersonalProperty.class, "G01PersonalProperty.findByTaxpayerCaseUuid", params);
        PersonalPropertyProfile aPersonalPropertyProfile;
        for (G01PersonalProperty aG01PersonalProperty : aG01PersonalPropertyList){
            aPersonalPropertyProfile = new PersonalPropertyProfile();
            aPersonalPropertyProfile.setPersonalPropertyEntity(aG01PersonalProperty);
            result.add(aPersonalPropertyProfile);
        }
        
        return result;
    }

    private List<PersonalBusinessPropertyProfile> findPersonalBusinessPropertyProfileListByTaxpayerCaseUuid(EntityManager em, 
                                                                                                            String taxpayerCaseUuid) 
    {
        List<PersonalBusinessPropertyProfile> result = new ArrayList<>();
        HashMap<String, Object> params = new HashMap<>();
        params.put("taxpayerCaseUuid", taxpayerCaseUuid);
        List<G01PersonalBusinessProperty> aG01PersonalBusinessPropertyList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                G01PersonalBusinessProperty.class, "G01PersonalBusinessProperty.findByTaxpayerCaseUuid", params);
        PersonalBusinessPropertyProfile aPersonalBusinessPropertyProfile;
        params.clear();
        for (G01PersonalBusinessProperty aG01PersonalBusinessProperty : aG01PersonalBusinessPropertyList){
            aPersonalBusinessPropertyProfile = new PersonalBusinessPropertyProfile();
            aPersonalBusinessPropertyProfile.setPersonalBusinessPropertyEntity(aG01PersonalBusinessProperty);
            params.put("businessPropertyUuid", aG01PersonalBusinessProperty.getPersonalBusinessPropertyUuid());
            aPersonalBusinessPropertyProfile.setBusinessVehicleEntityList(GardenJpaUtils.findEntityListByNamedQuery(em, 
                    G01BusinessVehicle.class, "G01BusinessVehicle.findByBusinessPropertyUuid", params));
            result.add(aPersonalBusinessPropertyProfile);
        }
        
        return result;
    }

    /**
     * Mark records deleted
     * 
     * @param taxpayerCaseUuid
     * @param entityStatus - if NULL, it means records are resumed to be active. GardenEntityStatus marks some special 
     * cases for the corresponding entity records.
     * 
     * @throws IllegalStateException
     * @throws SecurityException
     * @throws SystemException
     * @throws RollbackException
     * @throws HeuristicMixedException
     * @throws HeuristicRollbackException
     * @throws NotSupportedException 
     * @throws ZcaEntityValidationException 
     */
    public void markTaxpayerCaseRecordsEntityStatus(String taxpayerCaseUuid, GardenEntityStatus entityStatus) throws IllegalStateException, 
                                                                                                                SecurityException, 
                                                                                                                SystemException, 
                                                                                                                RollbackException, 
                                                                                                                HeuristicMixedException, 
                                                                                                                HeuristicRollbackException, 
                                                                                                                NotSupportedException, 
                                                                                                                ZcaEntityValidationException 
    {
        if (ZcaValidator.isNullEmpty(taxpayerCaseUuid)){
            RoseJsfUtils.setGlobalErrorFacesMessage(RoseText.getText("NoData_T"));
            return;
        }
        EntityManager em = null;
        UserTransaction utx = getUserTransaction();
        //delete associated entities members
        try {
            utx.begin();
            em = getEntityManager();
            
            //aG01TaxpayerCase
            G01TaxpayerCase aG01TaxpayerCase = GardenJpaUtils.findById(em, G01TaxpayerCase.class, taxpayerCaseUuid);
            if (aG01TaxpayerCase != null){
                aG01TaxpayerCase.setEntityStatus(entityStatus.value());
                GardenJpaUtils.storeEntity(em, G01TaxpayerCase.class, aG01TaxpayerCase, aG01TaxpayerCase.getTaxpayerCaseUuid(), 
                        G01DataUpdaterFactory.getSingleton().getG01TaxpayerCaseUpdater());
                //G01TaxpayerInfo
                HashMap<String, Object> params = new HashMap<>();
                params.put("taxpayerCaseUuid", taxpayerCaseUuid);
                List<G01TaxpayerInfo> aG01TaxpayerInfoList = GardenJpaUtils.findEntityListByNamedQuery(em, G01TaxpayerInfo.class, "G01TaxpayerInfo.findByTaxpayerCaseUuid", params);
                G01User aG01User;
                for (G01TaxpayerInfo aG01TaxpayerInfo : aG01TaxpayerInfoList){
                    aG01TaxpayerInfo.setEntityStatus(entityStatus.value());
                    GardenJpaUtils.storeEntity(em, G01TaxpayerInfo.class, aG01TaxpayerInfo, aG01TaxpayerInfo.getTaxpayerUserUuid(), 
                            G01DataUpdaterFactory.getSingleton().getG01TaxpayerInfoUpdater());
                    /**
                    * NOTICE: every year, a customer do tax filing. And the taxpayer's information (e.g. the part stored by G01User) 
                    * might be changed. So, it demands a copy of user information every year. It is NOT necessary to check redundancy 
                    * here as long as, in the current year, the primary taxpayer is not rededunant, which is done by checkCaseUniqueness
                    */
                    aG01User = GardenJpaUtils.findById(em, G01User.class, aG01TaxpayerInfo.getTaxpayerUserUuid());
                    if (aG01User != null){
                        aG01User.setEntityStatus(entityStatus.value());
                        GardenJpaUtils.storeEntity(em, G01User.class, aG01User, aG01User.getUserUuid(), 
                                G01DataUpdaterFactory.getSingleton().getG01UserUpdater());
                    }
                }
                //G01Location
                params.clear();
                params.put("entityUuid", taxpayerCaseUuid);
                List<G01Location> aG01LocationList = GardenJpaUtils.findEntityListByNamedQuery(em, G01Location.class, "G01Location.findByEntityUuid", params);
                for (G01Location aG01Location : aG01LocationList){
                    aG01Location.setEntityStatus(entityStatus.value());
                    GardenJpaUtils.storeEntity(em, G01Location.class, aG01Location, aG01Location.getLocationUuid(), 
                            G01DataUpdaterFactory.getSingleton().getG01LocationUpdater());
                }
                //G01ContactInfo
                params.clear();
                params.put("entityUuid", taxpayerCaseUuid);
                List<G01ContactInfo> aG01ContactInfoList = GardenJpaUtils.findEntityListByNamedQuery(em, G01ContactInfo.class, "G01ContactInfo.findByEntityUuid", params);
                for (G01ContactInfo aG01ContactInfo : aG01ContactInfoList){
                    aG01ContactInfo.setEntityStatus(entityStatus.value());
                    GardenJpaUtils.storeEntity(em, G01ContactInfo.class, aG01ContactInfo, aG01ContactInfo.getContactInfoUuid(), 
                            G01DataUpdaterFactory.getSingleton().getG01ContactInfoUpdater());
                }
                //G01PersonalProperty
                params.clear();
                params.put("taxpayerCaseUuid", taxpayerCaseUuid);
                List<G01PersonalProperty> aG01PersonalPropertyList = GardenJpaUtils.findEntityListByNamedQuery(em, G01PersonalProperty.class, "G01PersonalProperty.findByTaxpayerCaseUuid", params);
                for (G01PersonalProperty aG01PersonalProperty : aG01PersonalPropertyList){
                    aG01PersonalProperty.setEntityStatus(entityStatus.value());
                    GardenJpaUtils.storeEntity(em, G01PersonalProperty.class, aG01PersonalProperty, 
                            aG01PersonalProperty.getPersonalPropertyUuid(), G01DataUpdaterFactory.getSingleton().getG01PersonalPropertyUpdater());
                }
                //G01PersonalBusinessProperty
                params.clear();
                params.put("taxpayerCaseUuid", taxpayerCaseUuid);
                List<G01PersonalBusinessProperty> aG01PersonalBusinessPropertyList = GardenJpaUtils.findEntityListByNamedQuery(em, G01PersonalBusinessProperty.class, "G01PersonalBusinessProperty.findByTaxpayerCaseUuid", params);
                for (G01PersonalBusinessProperty aG01PersonalBusinessProperty : aG01PersonalBusinessPropertyList){
                    aG01PersonalBusinessProperty.setEntityStatus(entityStatus.value());
                    GardenJpaUtils.storeEntity(em, G01PersonalBusinessProperty.class, aG01PersonalBusinessProperty, 
                            aG01PersonalBusinessProperty.getPersonalBusinessPropertyUuid(), G01DataUpdaterFactory.getSingleton().getG01PersonalBusinessPropertyUpdater());
                }
                //G01TlcLicense
                params.clear();
                params.put("taxpayerCaseUuid", taxpayerCaseUuid);
                List<G01TlcLicense> aG01TlcLicenseList = GardenJpaUtils.findEntityListByNamedQuery(em, G01TlcLicense.class, "G01TlcLicense.findByTaxpayerCaseUuid", params);
                for (G01TlcLicense aG01TlcLicense : aG01TlcLicenseList){
                    aG01TlcLicense.setEntityStatus(entityStatus.value());
                    GardenJpaUtils.storeEntity(em, G01TlcLicense.class, aG01TlcLicense, 
                            aG01TlcLicense.getDriverUuid(), G01DataUpdaterFactory.getSingleton().getG01TlcLicenseUpdater());
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

    private List<TaxpayerCaseConciseProfile> findTaxpayerCaseConciseProfileListHelper(String sqlQuery, HashMap<String, Object> params) {
        List<TaxpayerCaseConciseProfile> result = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            List<G01TaxpayerCase> aG01TaxpayerCaseList = GardenJpaUtils.findEntityListByQuery(em, G01TaxpayerCase.class, sqlQuery, params);
            TaxpayerCaseConciseProfile aTaxpayerCaseSearchResultProfile;
            G01User aG01User;
            for (G01TaxpayerCase aG01TaxpayerCase : aG01TaxpayerCaseList){
                aG01User = GardenJpaUtils.findById(em, G01User.class, aG01TaxpayerCase.getCustomerAccountUuid());
                if (aG01User != null){
                    aTaxpayerCaseSearchResultProfile = new TaxpayerCaseConciseProfile();
                    aTaxpayerCaseSearchResultProfile.setTaxpayerCase(aG01TaxpayerCase);
                    aTaxpayerCaseSearchResultProfile.setSimilarityValue(1.0);
                    aTaxpayerCaseSearchResultProfile.setCustomer(aG01User);
                    result.add(aTaxpayerCaseSearchResultProfile);
                }
            }
        } finally {
            em.close();
        }
        return result;
    }

    public List<TaxpayerCaseConciseProfile> findTaxpayerCaseConciseProfileListByEmployeeUuid(String employeeAccountUuid) {
        List<TaxpayerCaseConciseProfile> result = new ArrayList<>();
        if (ZcaValidator.isNotNullEmpty(employeeAccountUuid)){
            String sqlQuery = "SELECT g FROM G01TaxpayerCase g WHERE g.employeeAccountUuid = :employeeAccountUuid ORDER BY g.deadline desc";
            HashMap<String, Object> params = new HashMap<>();
            params.put("employeeAccountUuid", employeeAccountUuid);
            result = findTaxpayerCaseConciseProfileListHelper(sqlQuery, params);
        }
        return result;
    }

    public List<TaxpayerCaseConciseProfile> findTaxpayerCaseConciseProfileListByDeadlineDateRange(Date dateFrom, Date dateTo) {
        List<TaxpayerCaseConciseProfile> result = new ArrayList<>();
        if ((dateFrom != null) && (dateTo != null)){
            String sqlQuery = "SELECT g FROM G01TaxpayerCase g WHERE g.deadline BETWEEN :dateFrom AND :dateTo ORDER BY g.updated desc";
            HashMap<String, Object> params = new HashMap<>();
            params.put("dateFrom", dateFrom);
            params.put("dateTo", dateTo);
            result = findTaxpayerCaseConciseProfileListHelper(sqlQuery, params);
        }
        return result;
    }

    public List<TaxpayerCaseConciseProfile> findG01TaxpayerCaseListByWorkStatus(String taxpayerCaseStatusValue, Date deadline) {
        List<TaxpayerCaseConciseProfile> result = new ArrayList<>();
        GardenTaxpayerCaseStatus logMsg = GardenTaxpayerCaseStatus.convertEnumValueToType(taxpayerCaseStatusValue, true);
        if ((logMsg != null) && (deadline != null)){
            String sqlQuery = "SELECT g FROM G01TaxpayerCase g, G01Log h WHERE (g.taxpayerCaseUuid = h.entityUuid) AND (g.deadline = :deadline) AND (h.logMsg = :logMsg)";
            HashMap<String, Object> params = new HashMap<>();
            params.put("logMsg", logMsg.value());
            params.put("deadline", deadline);
            result = findTaxpayerCaseConciseProfileListHelper(sqlQuery, params);
        }
        return result;
    }

    public List<TaxpayerCaseConciseProfile> findG01TaxpayerCaseListByFeaturedDateRangeFields(String dateField, Date dateFrom, Date dateTo){
        List<TaxpayerCaseConciseProfile> result = new ArrayList<>();
        if ((dateFrom != null) && (dateTo != null)){
            String sqlQuery = "SELECT g FROM G01TaxpayerCase g WHERE g." + dateField + " BETWEEN :dateFrom AND :dateTo";
            HashMap<String, Object> params = new HashMap<>();
            params.put("dateFrom", dateFrom);
            params.put("dateTo", dateTo);
            result = findTaxpayerCaseConciseProfileListHelper(sqlQuery, params);
        }
        return result;
    }

    public List<TaxpayerCaseConciseProfile> findUrgentTaxpayerCaseProfileList(int selectedDaysBeforeDeadlineForTaxpayer) {
        
        Date today = ZcaCalendar.covertDateToBeginning(new Date());
        Date deadline = ZcaCalendar.covertDateToEnding(ZcaCalendar.addDates(today, selectedDaysBeforeDeadlineForTaxpayer));
        
        String sqlQuery = "SELECT g FROM G01TaxpayerCase g WHERE g.deadline BETWEEN :dateFrom AND :dateTo";
        HashMap<String, Object> params = new HashMap<>();
        params.put("dateFrom", today);
        params.put("dateTo", deadline);
        
        return findTaxpayerCaseConciseProfileListHelper(sqlQuery, params);
    }

    public void deleteTaxpayerCaseEntity(String taxpayerCaseUuid) throws Exception{
        if (ZcaValidator.isNullEmpty(taxpayerCaseUuid)){
            return;
        }
        EntityManager em = null;
        UserTransaction utx = getUserTransaction();
        //delete associated entities members
        try {
            utx.begin();
            em = getEntityManager();
            
            G01TaxpayerCase aG01TaxpayerCase = GardenJpaUtils.findById(em, G01TaxpayerCase.class, taxpayerCaseUuid);
            if (aG01TaxpayerCase != null){
                G01TaxpayerCaseBk aG01TaxpayerCaseBk = new G01TaxpayerCaseBk();
                aG01TaxpayerCaseBk.setAgreementSignature(aG01TaxpayerCase.getAgreementSignature());
                aG01TaxpayerCaseBk.setAgreementSignatureTimestamp(aG01TaxpayerCase.getAgreementSignatureTimestamp());
                aG01TaxpayerCaseBk.setAgreementUuid(aG01TaxpayerCase.getAgreementUuid());
                aG01TaxpayerCaseBk.setBankAccountNumber(aG01TaxpayerCase.getBankAccountNumber());
                aG01TaxpayerCaseBk.setBankRoutingNumber(aG01TaxpayerCase.getBankRoutingNumber());
                aG01TaxpayerCaseBk.setDeadline(aG01TaxpayerCase.getDeadline());
                aG01TaxpayerCaseBk.setFederalFilingStatus(aG01TaxpayerCase.getFederalFilingStatus());
                aG01TaxpayerCaseBk.setEntityStatus(aG01TaxpayerCase.getEntityStatus());
                aG01TaxpayerCaseBk.setExtensionDate(aG01TaxpayerCase.getExtensionDate());
                aG01TaxpayerCaseBk.setExtensionMemo(aG01TaxpayerCase.getExtensionMemo());
                aG01TaxpayerCaseBk.setCustomerAccountUuid(aG01TaxpayerCase.getCustomerAccountUuid());
                aG01TaxpayerCaseBk.setEmployeeAccountUuid(aG01TaxpayerCase.getEmployeeAccountUuid());
                aG01TaxpayerCaseBk.setMemo(aG01TaxpayerCase.getMemo());
                aG01TaxpayerCaseBk.setLatestLogUuid(aG01TaxpayerCase.getLatestLogUuid());
                aG01TaxpayerCaseBk.setEntityStatus(aG01TaxpayerCase.getEntityStatus());
                aG01TaxpayerCaseBk.setTaxpayerCaseUuid(aG01TaxpayerCase.getTaxpayerCaseUuid());
                aG01TaxpayerCaseBk.setCreated(aG01TaxpayerCase.getCreated());
                
                GardenJpaUtils.storeEntity(em, G01TaxpayerCaseBk.class, aG01TaxpayerCaseBk, aG01TaxpayerCaseBk.getTaxpayerCaseUuid(), 
                        G01DataUpdaterFactory.getSingleton().getG01TaxpayerCaseBkUpdater());
                
                GardenJpaUtils.deleteEntity(em, G01TaxpayerCase.class, taxpayerCaseUuid);
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
