/*
 * Copyright 2017 ZComApproach Inc.
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
import com.zcomapproach.commons.persistent.exception.NonUniqueEntityException;
import com.zcomapproach.garden.guard.RoseWebCipher;
import com.zcomapproach.garden.persistence.GardenJpaUtils;
import com.zcomapproach.garden.persistence.entity.G01Account;
import com.zcomapproach.garden.persistence.entity.G01ContactInfo;
import com.zcomapproach.garden.persistence.entity.G01Employee;
import com.zcomapproach.garden.persistence.entity.G01Location;
import com.zcomapproach.garden.persistence.entity.G01SecurityQna;
import com.zcomapproach.garden.persistence.entity.G01User;
import com.zcomapproach.garden.persistence.entity.G01XmppAccount;
import com.zcomapproach.garden.rose.data.profile.EmployeeAccountProfile;
import com.zcomapproach.garden.rose.data.profile.RoseAccountProfile;
import com.zcomapproach.garden.rose.data.profile.RoseContactInfoProfile;
import com.zcomapproach.garden.rose.data.profile.RoseLocationProfile;
import com.zcomapproach.garden.rose.data.profile.RoseUserProfile;
import com.zcomapproach.garden.rose.data.profile.SecurityQnaProfile;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.metamodel.EntityType;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import com.zcomapproach.garden.persistence.constant.GardenEntityStatus;
import com.zcomapproach.garden.persistence.entity.G01ArchivedDocument;
import com.zcomapproach.garden.persistence.entity.G01AuthorizedPrivilege;
import com.zcomapproach.garden.persistence.entity.G01Bill;
import com.zcomapproach.garden.persistence.entity.G01ContactEntity;
import com.zcomapproach.garden.persistence.entity.G01ContactMessage;
import com.zcomapproach.garden.persistence.entity.G01DocumentRequirement;
import com.zcomapproach.garden.persistence.entity.G01Payment;
import com.zcomapproach.garden.persistence.entity.G01TaxFiling;
import com.zcomapproach.garden.persistence.entity.G01TaxFilingType;
import com.zcomapproach.garden.persistence.entity.G01TaxFilingTypePK;
import com.zcomapproach.garden.persistence.entity.G01TaxcorpRepresentative;
import com.zcomapproach.garden.rose.data.profile.BusinessCaseBillProfile;
import com.zcomapproach.garden.rose.data.profile.BusinessCasePaymentProfile;
import com.zcomapproach.garden.rose.data.profile.ContactMessageProfile;
import com.zcomapproach.garden.rose.data.profile.DocumentRequirementProfile;
import com.zcomapproach.garden.rose.data.profile.TaxcorpFilingConciseProfile;
import com.zcomapproach.garden.rose.data.profile.RoseArchivedDocumentProfile;
import com.zcomapproach.garden.rose.data.profile.TaxFilingProfile;
import com.zcomapproach.garden.rose.data.profile.TaxFilingTypeProfile;
import com.zcomapproach.garden.rose.data.profile.TaxcorpContactProfile;
import com.zcomapproach.garden.rose.data.profile.TaxcorpRepresentativeProfile;
import com.zcomapproach.garden.rose.util.RoseDataAgent;
import com.zcomapproach.commons.ZcaValidator;
import java.util.Date;
import com.zcomapproach.commons.persistent.IZcaEntityUpdater;

/**
 * CRUD services for database except the special table GardenSystemLog
 * @author zhijun98
 */
@TransactionManagement(TransactionManagementType.BEAN)
public abstract class AbstractDataServiceEJB {
    
    @Resource
    private UserTransaction _utx;
    
    @PersistenceUnit(unitName="RosePU") 
    private EntityManagerFactory _emf;

    public AbstractDataServiceEJB() {
    }
    
    protected UserTransaction getUserTransaction() {
        return _utx;
    }

    public EntityManager getEntityManager() {
        return _emf.createEntityManager();
    }
    
    public void evictClassListFromPersistency(List<Class> classNameList){
        EntityManager em = getEntityManager();
        try {
            evictClassListFromPersistencyHelper(em, classNameList);
        } finally {
            em.close();
        }
    }
    
    public void evictClassFromPersistency(Class className){
        EntityManager em = getEntityManager();
        try {
            evictClassFromPersistencyHelper(em, className);
        } finally {
            em.close();
        }
    }
    
    protected void evictClassListFromPersistencyHelper(EntityManager em, List<Class> classNameList){
        for (Class className : classNameList){
            evictClassFromPersistencyHelper(em, className);
        }
    }
    
    protected void evictClassFromPersistencyHelper(EntityManager em, Class className){
        em.getEntityManagerFactory().getCache().evict(className);
    }
    
    public void evictEntityListFromPersistency(HashMap<Class, Object> entities){
        EntityManager em = getEntityManager();
        try {
            evictEntityListFromPersistencyHelper(em, entities);
        } finally {
            em.close();
        }
    }
    
    public void evictEntityFromPersistency(Entry<Class, Object> entity){
        EntityManager em = getEntityManager();
        try {
            evictEntityFromPersistencyHelper(em, entity);
        } finally {
            em.close();
        }
    }
    
    protected void evictEntityListFromPersistencyHelper(EntityManager em, HashMap<Class, Object> entities){
        Set<Entry<Class, Object>> entitySet = entities.entrySet();
        for (Entry<Class, Object> entity : entitySet){
            evictEntityFromPersistencyHelper(em, entity);
        }
    }
    
    protected void evictEntityFromPersistencyHelper(EntityManager em, Entry<Class, Object> entity){
        em.getEntityManagerFactory().getCache().evict(entity.getKey(), entity.getValue());
    }
    
    protected ArrayList<EntityType> getEntityTypeList() {
        ArrayList<EntityType> aEntityTypeList = new ArrayList<>(); 
        Set<EntityType<?>> entityTypes = _emf.getMetamodel().getEntities();
        for (EntityType entityType : entityTypes){
            aEntityTypeList.add(entityType);
        }
        return aEntityTypeList;
    }

    protected TreeSet<String> getEntityClassNameSet() {
        TreeSet<String> aEntityClassNameSet = new TreeSet<>();
        Set<EntityType<?>> entityTypes = _emf.getMetamodel().getEntities();
        for (EntityType entityType : entityTypes){
            aEntityClassNameSet.add(entityType.getName());
        }
        return aEntityClassNameSet;
    }
    
    public <T> T findEntityByNamedQuery(Class<T> className, String namedQuery, HashMap<String, Object> params) throws NonUniqueEntityException {
        EntityManager em = getEntityManager();
        T entity = null;
        try {
            entity = GardenJpaUtils.findEntityByNamedQuery(em, className, namedQuery, params);
        } finally {
            em.close();
        }
        return entity;
    }
    
    public <T> List<T> findEntityListByNamedQuery(Class<T> className, String namedQuery, HashMap<String, Object> params) {
        EntityManager em = getEntityManager();
        List<T> result = null;
        try {
            result = GardenJpaUtils.findEntityListByNamedQuery(em, className, namedQuery, params);
        } finally {
            em.close();
        }
        return result;
    }
    
    public <T> List<T> findEntityListByQuery(Class<T> className, String sqlQuery, HashMap<String, Object> params) {
        EntityManager em = getEntityManager();
        List<T> result = null;
        try {
            result = GardenJpaUtils.findEntityListByQuery(em, className, sqlQuery, params);
        } finally {
            em.close();
        }
        return result;
    }

    /**
     * 
     * @param <T>
     * @param className
     * @param id
     * @return - NULL if the ID does not exist
     */
    public <T> T findEntityByUuid(Class<T> className, Object id) {
        EntityManager em = getEntityManager();
        T entity = null;
        try {
            entity = GardenJpaUtils.findById(em, className, id);
        } finally {
            em.close();
        }
        return entity;
    }

    /**
     * 
     * @param <T>
     * @param className
     * @return a list of the results or empty list
     */
    public <T> List<T> findAll(Class<T> className) {
        List<T> result = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            //GardenAccount
            result = GardenJpaUtils.findAll(em, className);
            if (result == null){
                result = new ArrayList<>();
            }
        } finally {
            em.close();
        }
        return result;
    }

    public <T> void storeEntityList(Class<T> entityClass, List<T> entityObjList, IZcaEntityUpdater<T> entityUpdater) throws Exception {
        if ((entityObjList == null) || entityObjList.isEmpty()){
            return;
        }
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();

            for (T entityObj : entityObjList){
                GardenJpaUtils.storeEntity(em, entityClass, entityObj, entityUpdater.getEntityKey(entityObj), entityUpdater);
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
    }

    public <T> T storeEntityByUuid(Class<T> entityClass, T entityObj, Object entityKey, IZcaEntityUpdater<T> entityUpdater) throws Exception {
        T entity = null;
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();

            entity = GardenJpaUtils.storeEntity(em, entityClass, entityObj, entityKey, entityUpdater);

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
        return entity;
    }

    public <T> T deleteEntityByUuid(Class<T> className, Object entityKey) throws Exception {
        T entity = null;
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();

            entity = GardenJpaUtils.deleteEntity(em, className, entityKey);

            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
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
        return entity;
    }

    
    /**SAMPLE CODE WITHOUT UTX
        EntityManager em = getEntityManager();
        try {

            //do something here

        } finally {
            em.close();
        }
    **/
    
    /**SAMPLE CODE WITH UTX
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();

            //do something here

            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(CLASS_NAME.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(CLASS_NAME.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
     
    **/

    /**
     * 
     * @param em
     * @param accountUuid
     * @return - empty list returned if nothing found
     */
    List<SecurityQnaProfile> findAccountSecurityQnaProfileList(EntityManager em, String accountUuid) {
        List<SecurityQnaProfile> result = new ArrayList<>();
        
        HashMap<String, Object> params = new HashMap<>();
        params.put("accountUuid", accountUuid);
        List<G01SecurityQna> aG01SecurityQnaList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                G01SecurityQna.class, "G01SecurityQna.findByAccountUuid", params);
        SecurityQnaProfile aSecurityQnaProfile;
        for (G01SecurityQna aG01SecurityQna : aG01SecurityQnaList){
            aSecurityQnaProfile = new SecurityQnaProfile();
            aSecurityQnaProfile.initializeSecurityQnaProfile(aG01SecurityQna);
            result.add(aSecurityQnaProfile);
        }//for
        return result;
    }

    List<RoseContactInfoProfile> findRoseContactInfoProfileListByEntityUuid(EntityManager em, String entityUuid) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("entityUuid", entityUuid);
        List<G01ContactInfo> aG01ContactInfoList = GardenJpaUtils.findEntityListByNamedQuery(em, G01ContactInfo.class, 
                                                                                             "G01ContactInfo.findByEntityUuid", 
                                                                                             params);
        List<RoseContactInfoProfile> result = new ArrayList<>();
        RoseContactInfoProfile aRoseContactInfoProfile;
        for (G01ContactInfo aG01ContactInfo : aG01ContactInfoList){
            aRoseContactInfoProfile = new RoseContactInfoProfile();
            aRoseContactInfoProfile.setContactInfoEntity(aG01ContactInfo);
            result.add(aRoseContactInfoProfile);
        }
        return result;
    }

    RoseContactInfoProfile findRoseContactInfoProfileByEntityUuid(EntityManager em, String entityUuid) {
        List<RoseContactInfoProfile> aRoseContactInfoProfileList = findRoseContactInfoProfileListByEntityUuid(em, entityUuid);
        if (aRoseContactInfoProfileList.isEmpty()){
            return null;
        }else{
            Collections.sort(aRoseContactInfoProfileList, (RoseContactInfoProfile o1, RoseContactInfoProfile o2) -> o1.getContactInfoEntity().getUpdated().compareTo(o2.getContactInfoEntity().getUpdated())*(-1));
            return aRoseContactInfoProfileList.get(0);
        }
    }

    List<RoseLocationProfile> findRoseLocationProfileListByEntityUuid(EntityManager em, String entityUuid) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("entityUuid", entityUuid);
        List<G01Location> aG01LocationList = GardenJpaUtils.findEntityListByNamedQuery(em, G01Location.class, 
                                                                                       "G01Location.findByEntityUuid", 
                                                                                       params);
        List<RoseLocationProfile> result = new ArrayList<>();
        RoseLocationProfile aRoseLocationProfile;
        for (G01Location aG01Location : aG01LocationList){
            aRoseLocationProfile = new RoseLocationProfile();
            aRoseLocationProfile.setLocationEntity(aG01Location);
            result.add(aRoseLocationProfile);
        }
        return result;
    }

    RoseLocationProfile findRoseLocationProfileByEntityUuid(EntityManager em, String entityUuid) {
        List<RoseLocationProfile> aRoseLocationProfileList = findRoseLocationProfileListByEntityUuid(em, entityUuid);
        if (aRoseLocationProfileList.isEmpty()){
            return null;
        }else{
            Collections.sort(aRoseLocationProfileList, (RoseLocationProfile o1, RoseLocationProfile o2) -> o1.getLocationEntity().getUpdated().compareTo(o2.getLocationEntity().getUpdated())*(-1));
            return aRoseLocationProfileList.get(0);
        }
    }
    
    /**
     * 
     * @param em
     * @param userUuid
     * @return - NULL if nothing found
     */
    RoseUserProfile findRoseUserProfileByUserUuid(EntityManager em, String userUuid){
        G01User aG01User = GardenJpaUtils.findById(em, G01User.class, userUuid);
        if (aG01User != null){
            RoseUserProfile aRoseUserProfile = new RoseUserProfile();

            aRoseUserProfile.setUserEntity(aG01User);
            aRoseUserProfile.setUserContactInfoProfileList(findRoseContactInfoProfileListByEntityUuid(em, userUuid));
            aRoseUserProfile.setUserLocationProfileList(findRoseLocationProfileListByEntityUuid(em, userUuid));

            return aRoseUserProfile;
        }
        return null;
    }

    protected EmployeeAccountProfile findEmployeeAccountProfileByAccountUuid(EntityManager em, String employeeUuid) throws Exception{
        G01Account aG01Account = GardenJpaUtils.findById(em, G01Account.class, employeeUuid);
        if (aG01Account != null){
            RoseAccountProfile aRoseUserProfile = initializeRoseAccountProfileByAccountEntity(em, aG01Account);
            if (aRoseUserProfile instanceof EmployeeAccountProfile){
                return (EmployeeAccountProfile)aRoseUserProfile;
            }
        }
        return null;
    }

    /**
     * 
     * @param em
     * @param aG01Account
     * @return - it could be an employee, i.e. EmployeeAccountProfile
     * @throws Exception 
     */
    RoseAccountProfile initializeRoseAccountProfileByAccountEntity(EntityManager em, G01Account aG01Account) throws Exception {
        RoseUserProfile aRoseUserProfile = findRoseUserProfileByUserUuid(em, aG01Account.getAccountUuid());
        if ((aRoseUserProfile == null) || (aRoseUserProfile.getUserEntity() == null)){
            throw new Exception("Cannot find user profile record.");
        }

        //Try employee...
        RoseAccountProfile aRoseAccountProfile;
        G01Employee aG01Employee = GardenJpaUtils.findById(em, G01Employee.class, aG01Account.getAccountUuid());
        if (aG01Employee == null){
            aRoseAccountProfile = new RoseAccountProfile();
        }else{
            aRoseAccountProfile = new EmployeeAccountProfile();
            aRoseAccountProfile.getUserProfile().getUserEntity().setEntityStatus(GardenEntityStatus.EMPLOYEE.value());
        }
        if (ZcaValidator.isNotNullEmpty(aG01Account.getEncryptedPassword())){
            aG01Account.setPassword(RoseWebCipher.getSingleton().decrypt(aG01Account.getEncryptedPassword()));
        }
        aRoseAccountProfile.setAccountEntity(aG01Account);
        aRoseAccountProfile.setXmppAccountEntity(GardenJpaUtils.findById(em, G01XmppAccount.class, aG01Account.getAccountUuid()));
        aRoseAccountProfile.setAuthenticated(true);
        aRoseAccountProfile.setBrandNew(false);
        aRoseAccountProfile.setUserProfile(aRoseUserProfile);
        //security questions
        aRoseAccountProfile.loadSecurityQnaProfileList(findAccountSecurityQnaProfileList(em, aG01Account.getAccountUuid()));
        aRoseAccountProfile.authorizePrivilegeList(findG01AuthorizePrivilegeListByAccountUuid(em, aG01Account.getAccountUuid()));
        //it's employee
        if (aRoseAccountProfile instanceof EmployeeAccountProfile){
            ((EmployeeAccountProfile)aRoseAccountProfile).setEmployeeEntity(aG01Employee);
        }
        return aRoseAccountProfile;
    }
    
    public RoseAccountProfile findRoseAccountProfileByAccountUserUuid(String accountUserUuid){
        RoseAccountProfile result = null;
        EntityManager em = getEntityManager();
        try {
            result = findRoseAccountProfileByAccountUserUuidHelper(em, accountUserUuid);
        } finally {
            em.close();
        }
        
        return result;
    
    }
    
    public EmployeeAccountProfile findEmployeeAccountProfileByAccountUserUuid(String employeeAccountUserUuid){
        RoseAccountProfile result = null;
        EntityManager em = getEntityManager();
        try {
            result = findRoseAccountProfileByAccountUserUuidHelper(em, employeeAccountUserUuid);
        } finally {
            em.close();
        }
        if (result instanceof EmployeeAccountProfile){
            return (EmployeeAccountProfile)result;
        }else{
            return null;
        }
    }
    
    public RoseAccountProfile findRoseAccountProfileByAccountUserUuidHelper(EntityManager em, String accountUserUuid){
        RoseAccountProfile result = null;
        G01Account aG01Account;
        try {
            aG01Account = GardenJpaUtils.findById(em, G01Account.class, accountUserUuid);
            if (aG01Account != null){
                result = initializeRoseAccountProfileByAccountEntity(em, aG01Account);
            }//if
        } catch (Exception ex) {
            Logger.getLogger(AbstractDataServiceEJB.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalSystemErrorFacesMessage(ex.getMessage());
            result = null;
        }
        return result;
    }

    public List<TaxcorpContactProfile> findTaxcorpContactProfileListByTaxFilingProfileList(List<TaxFilingProfile> aTaxFilingProfileList) {
        HashMap<String, TaxcorpContactProfile> result = new HashMap<>();
        if ((aTaxFilingProfileList != null) && (!aTaxFilingProfileList.isEmpty())){
            EntityManager em = getEntityManager();
            try {
                TaxcorpContactProfile aTaxcorpContactProfile;
                for (TaxFilingProfile aTaxFilingProfile : aTaxFilingProfileList){
                    aTaxcorpContactProfile = result.get(aTaxFilingProfile.getTaxcorpCaseEntity().getTaxcorpCaseUuid());
                    if (aTaxcorpContactProfile == null){
                        aTaxcorpContactProfile = new TaxcorpContactProfile();
                        aTaxcorpContactProfile.setTaxcorpCase(aTaxFilingProfile.getTaxcorpCaseEntity());
                        aTaxcorpContactProfile.setContactMessageProfileList(findContactMessageProfileListByEntityUuid(em, aTaxFilingProfile.getTaxcorpCaseEntity().getTaxcorpCaseUuid()));
                        aTaxcorpContactProfile.getTaxcorpRepresentativeProfiles()
                                .addAll(findTaxcorpRepresentativeProfileListByTaxcorpCaseUuid(em, 
                                        aTaxFilingProfile.getTaxFilingEntity().getTaxcorpCaseUuid(), null));
                        result.put(aTaxFilingProfile.getTaxcorpCaseEntity().getTaxcorpCaseUuid(), aTaxcorpContactProfile);
                    }
                    aTaxcorpContactProfile.getTaxFilingList().add(aTaxFilingProfile.getTaxFilingEntity());
                }
            } finally {
                em.close();
            }
        }
        return new ArrayList<>(result.values());
    }

    private List<ContactMessageProfile> findContactMessageProfileListByEntityUuid(EntityManager em, String taxcorpCaseUuid) {
        List<ContactMessageProfile> result = new ArrayList<>();
        
        HashMap<String, Object> params = new HashMap<>();
        params.put("entityUuid", taxcorpCaseUuid);
        List<G01ContactEntity> aG01ContactEntityList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                G01ContactEntity.class, "G01ContactEntity.findByEntityUuid", params);
        if ((aG01ContactEntityList != null) && (!aG01ContactEntityList.isEmpty())){
            ContactMessageProfile aContactMessageProfile;
            G01ContactMessage aG01ContactMessage;
            for (G01ContactEntity aG01ContactEntity : aG01ContactEntityList){
                aG01ContactMessage = GardenJpaUtils.findById(em, G01ContactMessage.class, aG01ContactEntity.getG01ContactEntityPK().getContactMessageUuid());
                if (aG01ContactMessage != null){
                    aContactMessageProfile = new ContactMessageProfile();
                    aContactMessageProfile.setBrandNew(false);
                    aContactMessageProfile.setContactMessageEntity(aG01ContactMessage);
                    try {
                        aContactMessageProfile.setEmployeeUserEntity(GardenJpaUtils.findById(em, G01User.class, aG01ContactMessage.getEmployeeAccountUuid()));
                    } catch (Exception ex) {
                        //Logger.getLogger(AbstractDataServiceEJB.class.getName()).log(Level.SEVERE, null, ex);
                        aContactMessageProfile.setEmployeeUserEntity(new G01User());
                    }
                    result.add(aContactMessageProfile);
                }
            }//for
        }
        
        return result;
    }

    /**
     * 
     * @param aTaxFilingProfileList
     * @return - it guaranttee the items in the list are distinct 
     */
    public List<TaxcorpRepresentativeProfile> findTaxcorpRepresentativeProfileListByTaxFilingProfileList(List<TaxFilingProfile> aTaxFilingProfileList) {
        List<TaxcorpRepresentativeProfile> result = new ArrayList<>();
        if ((aTaxFilingProfileList != null) && (!aTaxFilingProfileList.isEmpty())){
            EntityManager em = getEntityManager();
            try {
                TreeSet<String> filter = new TreeSet<>();
                for (TaxFilingProfile aTaxFilingProfile : aTaxFilingProfileList){
                    result.addAll(findTaxcorpRepresentativeProfileListByTaxcorpCaseUuid(em, aTaxFilingProfile.getTaxFilingEntity().getTaxcorpCaseUuid(), filter));
                }
            } finally {
                em.close();
            }
        }
        return result;
    }

    public List<TaxcorpRepresentativeProfile> findTaxcorpRepresentativeProfileListByTaxcorpEntityList(List<TaxcorpFilingConciseProfile> aG01TaxcorpCaseSearchResultProfileList) {
        List<TaxcorpRepresentativeProfile> result = new ArrayList<>();
        if ((aG01TaxcorpCaseSearchResultProfileList != null) && (!aG01TaxcorpCaseSearchResultProfileList.isEmpty())){
            EntityManager em = getEntityManager();
            try {
                TreeSet<String> filter = new TreeSet<>();
                for (TaxcorpFilingConciseProfile aG01TaxcorpCaseSearchResultProfile : aG01TaxcorpCaseSearchResultProfileList){
                    result.addAll(findTaxcorpRepresentativeProfileListByTaxcorpCaseUuid(em, aG01TaxcorpCaseSearchResultProfile.getTaxcorpCase().getTaxcorpCaseUuid(), filter));
                }
            } finally {
                em.close();
            }
        }
        return result;
    }

    /**
     * 
     * @param em
     * @param taxcorpCaseUuid
     * @param blackUserUuid
     * @return  - it guaranttee the items in the list are distinct by the help of blackUserUuid
     */
    public List<TaxcorpRepresentativeProfile> findTaxcorpRepresentativeProfileListByTaxcorpCaseUuid(EntityManager em, String taxcorpCaseUuid, TreeSet<String> blackUserUuid) {
        List<TaxcorpRepresentativeProfile> result = new ArrayList<>();
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("taxcorpCaseUuid", taxcorpCaseUuid);
            List<G01TaxcorpRepresentative> aG01TaxcorpRepresentativeList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                    G01TaxcorpRepresentative.class, "G01TaxcorpRepresentative.findByTaxcorpCaseUuid", params);
            TaxcorpRepresentativeProfile aTaxcorpRepresentativeProfile;
            RoseUserProfile aRoseUserProfile;
            for (G01TaxcorpRepresentative aG01TaxcorpRepresentative : aG01TaxcorpRepresentativeList){
                aRoseUserProfile = findRoseUserProfileByUserUuid(em, aG01TaxcorpRepresentative.getG01TaxcorpRepresentativePK().getRepresentativeUserUuid());
                if (aRoseUserProfile != null){
                    aTaxcorpRepresentativeProfile = new TaxcorpRepresentativeProfile();
                    aTaxcorpRepresentativeProfile.setTaxcorpRepresentativeEntity(aG01TaxcorpRepresentative);
                    //copy user profile
                    RoseDataAgent.populateUserProfile(aTaxcorpRepresentativeProfile, aRoseUserProfile);
                    if (blackUserUuid == null){
                        result.add(aTaxcorpRepresentativeProfile);
                    }else{
                        if (!blackUserUuid.contains(aG01TaxcorpRepresentative.getG01TaxcorpRepresentativePK().getRepresentativeUserUuid())){
                            blackUserUuid.add(aG01TaxcorpRepresentative.getG01TaxcorpRepresentativePK().getRepresentativeUserUuid());
                            result.add(aTaxcorpRepresentativeProfile);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(AbstractDataServiceEJB.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalSystemErrorFacesMessage(ex.getMessage());
            result = null;
        }
        
        return result;
    }

    /**
     * 
     * @param em
     * @param taxcorpCaseUuid
     * @param taxFilingType
     * @return - ORDER BY g.updated DESC
     */
    public List<TaxFilingTypeProfile> findTaxFilingTypeProfileListByTaxcorpCaseUuid(EntityManager em, String taxcorpCaseUuid, String taxFilingType) {
        List<TaxFilingTypeProfile> result = new ArrayList<>();
        
        String sqlQuery = "SELECT g FROM G01TaxFilingType g WHERE g.g01TaxFilingTypePK.taxcorpCaseUuid = :taxcorpCaseUuid "
                + "AND g.g01TaxFilingTypePK.taxFilingType = :taxFilingType ORDER BY g.updated DESC";
        HashMap<String, Object> params = new HashMap<>();
        params.put("taxcorpCaseUuid", taxcorpCaseUuid);
        params.put("taxFilingType", taxFilingType);
        
        List<G01TaxFilingType> aG01TaxFilingTypeList = GardenJpaUtils.findEntityListByQuery(em, G01TaxFilingType.class, sqlQuery, params);
        TaxFilingTypeProfile aTaxFilingTypeProfile;
        for (G01TaxFilingType aG01TaxFilingType : aG01TaxFilingTypeList){
            aTaxFilingTypeProfile = new TaxFilingTypeProfile();
            aTaxFilingTypeProfile.setTaxFilingTypeEntity(aG01TaxFilingType);
            aTaxFilingTypeProfile.setTaxFilingProfileList(findTaxFilingProfileListByTaxFilingTypeUuid(em, aG01TaxFilingType.getG01TaxFilingTypePK()));
            result.add(aTaxFilingTypeProfile);
        }
        
        return result;
    }
    
    protected List<G01TaxFiling> findTaxFilingEntityListByTaxFilingTypePkid(EntityManager em, G01TaxFilingTypePK pkid){
        List<G01TaxFiling> result = new ArrayList<>();
        if (pkid != null){
            String sqlQuery = "SELECT g FROM G01TaxFiling g WHERE g.taxcorpCaseUuid = :taxcorpCaseUuid "
                    + "AND g.taxFilingType = :taxFilingType AND g.taxFilingPeriod = :taxFilingPeriod";
            HashMap<String, Object> params = new HashMap<>();
            params.put("taxcorpCaseUuid", pkid.getTaxcorpCaseUuid());
            params.put("taxFilingType", pkid.getTaxFilingType());
            params.put("taxFilingPeriod", pkid.getTaxFilingPeriod());
            result = GardenJpaUtils.findEntityListByQuery(em, G01TaxFiling.class, sqlQuery, params);
        }
        return result;
    }

    public List<TaxFilingProfile> findTaxFilingProfileListByTaxFilingTypeUuid(EntityManager em, G01TaxFilingTypePK pkid) {
        List<TaxFilingProfile> result = new ArrayList<>();
        List<G01TaxFiling> aG01TaxFilingList = findTaxFilingEntityListByTaxFilingTypePkid(em, pkid);
        TaxFilingProfile aTaxFilingProfile;
        for (G01TaxFiling aG01TaxFiling : aG01TaxFilingList){
            aTaxFilingProfile = new TaxFilingProfile();
            aTaxFilingProfile.setTaxFilingEntity(aG01TaxFiling);
            result.add(aTaxFilingProfile);
        }
        return result;
    }

    private List<G01AuthorizedPrivilege> findG01AuthorizePrivilegeListByAccountUuid(EntityManager em, String accountUuid) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("authorizedEntityUuid", accountUuid);
        return GardenJpaUtils.findEntityListByNamedQuery(em, G01AuthorizedPrivilege.class, 
                "G01AuthorizedPrivilege.findByAuthorizedEntityUuid", params);
    }
    
    public List<RoseArchivedDocumentProfile> findRoseArchivedDocumentProfileListByFeaturedField(String featuredParamKey,
                                                                                                 String featuredParamValue, 
                                                                                                 String featuredNamedQuery)
    {
        List<RoseArchivedDocumentProfile> result = new ArrayList<>();
        if (ZcaValidator.isNotNullEmpty(featuredParamValue)){
            EntityManager em = getEntityManager();
            try {
                HashMap<String, Object> params = new HashMap<>();
                params.put(featuredParamKey, featuredParamValue);
                List<G01ArchivedDocument> aG01ArchivedDocumentList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                        G01ArchivedDocument.class, featuredNamedQuery, params);
                Collections.sort(aG01ArchivedDocumentList, (G01ArchivedDocument o1, G01ArchivedDocument o2) -> o1.getUpdated().compareTo(o2.getUpdated()));
                RoseArchivedDocumentProfile aRoseArchivedDocumentProfile;
                for (G01ArchivedDocument aG01ArchivedDocument : aG01ArchivedDocumentList){
                    aRoseArchivedDocumentProfile = new RoseArchivedDocumentProfile();
                    aRoseArchivedDocumentProfile.setArchivedDocumentEntity(aG01ArchivedDocument);
                    aRoseArchivedDocumentProfile.setBrandNew(false);
                    result.add(aRoseArchivedDocumentProfile);
                }
            } finally {
                em.close();
            }
        }
        return result;
    }
    
    /**
     * 
     * @param billUuid - if this does not exist, NULL returned
     * @return 
     */
    public BusinessCaseBillProfile findBusinessCaseBillProfileByBillUuid(String billUuid) 
    {
        BusinessCaseBillProfile aBusinessCaseBillProfile = null;

        EntityManager em = getEntityManager();
        try {
            G01Bill aG01Bill = GardenJpaUtils.findById(em, G01Bill.class, billUuid);
            if (aG01Bill != null){
                aBusinessCaseBillProfile = new BusinessCaseBillProfile();
                aBusinessCaseBillProfile.setBillEntity(aG01Bill);
                try {
                    aBusinessCaseBillProfile.setAgent(findEmployeeAccountProfileByAccountUuid(em, aG01Bill.getEmployeeUuid()));
                } catch (Exception ex) {
                    //Logger.getLogger(AbstractDataServiceEJB.class.getName()).log(Level.SEVERE, null, ex);
                    aBusinessCaseBillProfile.setAgent(new EmployeeAccountProfile());
                }
                //businessCasePaymentProfileList
                aBusinessCaseBillProfile.setBusinessCasePaymentProfileList(findBusinessCasePaymentProfileListByFeaturedField("billUuid", 
                                                                                                                        aG01Bill.getBillUuid(), 
                                                                                                                        "G01Payment.findByBillUuid"));
                aBusinessCaseBillProfile.setBrandNew(false);
            }
        } finally {
            em.close();
        }
        
        return aBusinessCaseBillProfile;
    }
    
    public List<BusinessCaseBillProfile> findBusinessCaseBillProfileListByFeaturedField(String featuredParamKey,
                                                                                        String featuredParamValue, 
                                                                                        String featuredNamedQuery) 
    {
        List<BusinessCaseBillProfile> result = new ArrayList<>();
        if (ZcaValidator.isNotNullEmpty(featuredParamValue)){
            EntityManager em = getEntityManager();
            try {
                HashMap<String, Object> params = new HashMap<>();
                params.put(featuredParamKey, featuredParamValue);
                List<G01Bill> aG01BillList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                        G01Bill.class, featuredNamedQuery, params);
                Collections.sort(aG01BillList, (G01Bill o1, G01Bill o2) -> o1.getUpdated().compareTo(o2.getUpdated()));
                BusinessCaseBillProfile aBusinessCaseBillProfile;
                for (G01Bill aG01Bill : aG01BillList){
                    aBusinessCaseBillProfile = new BusinessCaseBillProfile();
                    aBusinessCaseBillProfile.setBillEntity(aG01Bill);
                    try {
                        aBusinessCaseBillProfile.setAgent(findEmployeeAccountProfileByAccountUuid(em, aG01Bill.getEmployeeUuid()));
                    } catch (Exception ex) {
                        //Logger.getLogger(AbstractDataServiceEJB.class.getName()).log(Level.SEVERE, null, ex);
                        aBusinessCaseBillProfile.setAgent(new EmployeeAccountProfile());
                    }
                    //businessCasePaymentProfileList
                    aBusinessCaseBillProfile.setBusinessCasePaymentProfileList(findBusinessCasePaymentProfileListByFeaturedField("billUuid", 
                                                                                                                            aG01Bill.getBillUuid(), 
                                                                                                                            "G01Payment.findByBillUuid"));
                    aBusinessCaseBillProfile.setBrandNew(false);
                    result.add(aBusinessCaseBillProfile);
                }
            } finally {
                em.close();
            }
        }
        return result;
    }
    
    public List<BusinessCasePaymentProfile> findBusinessCasePaymentProfileListByFeaturedField(String featuredParamKey,
                                                                                                 String featuredParamValue, 
                                                                                                 String featuredNamedQuery) 
    {
        List<BusinessCasePaymentProfile> result = new ArrayList<>();
        if (ZcaValidator.isNotNullEmpty(featuredParamValue)){
            EntityManager em = getEntityManager();
            try {
                HashMap<String, Object> params = new HashMap<>();
                params.put(featuredParamKey, featuredParamValue);
                List<G01Payment> aG01PaymentList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                        G01Payment.class, featuredNamedQuery, params);
                Collections.sort(aG01PaymentList, (G01Payment o1, G01Payment o2) -> o1.getUpdated().compareTo(o2.getUpdated()));
                BusinessCasePaymentProfile aBusinessCasePaymentProfile;
                for (G01Payment aG01Payment : aG01PaymentList){
                    aBusinessCasePaymentProfile = new BusinessCasePaymentProfile();
                    aBusinessCasePaymentProfile.setPaymentEntity(aG01Payment);
                    try {
                        aBusinessCasePaymentProfile.setAgent(findEmployeeAccountProfileByAccountUuid(em, aG01Payment.getEmployeeUuid()));
                    } catch (Exception ex) {
                        //Logger.getLogger(AbstractDataServiceEJB.class.getName()).log(Level.SEVERE, null, ex);
                        aBusinessCasePaymentProfile.setAgent(new EmployeeAccountProfile());
                    }
                    aBusinessCasePaymentProfile.setBrandNew(false);
                    result.add(aBusinessCasePaymentProfile);
                }
            } finally {
                em.close();
            }
        }
        return result;
    }
    
    public List<DocumentRequirementProfile> findRoseDocumentRequirementProfileListByFeaturedField(String featuredParamKey,
                                                                                                 String featuredParamValue, 
                                                                                                 String featuredNamedQuery) 
    {
        List<DocumentRequirementProfile> result = new ArrayList<>();
        if (ZcaValidator.isNotNullEmpty(featuredParamValue)){
            EntityManager em = getEntityManager();
            try {
                HashMap<String, Object> params = new HashMap<>();
                params.put(featuredParamKey, featuredParamValue);
                List<G01DocumentRequirement> aG01DocumentRequirementList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                        G01DocumentRequirement.class, featuredNamedQuery, params);
                Collections.sort(aG01DocumentRequirementList, (G01DocumentRequirement o1, G01DocumentRequirement o2) -> o1.getUpdated().compareTo(o2.getUpdated()));
                DocumentRequirementProfile aDocumentRequirementProfile;
                for (G01DocumentRequirement aG01DocumentRequirement : aG01DocumentRequirementList){
                    aDocumentRequirementProfile = new DocumentRequirementProfile();
                    aDocumentRequirementProfile.setDocumentRequirementEntity(aG01DocumentRequirement);
                    aDocumentRequirementProfile.setBrandNew(false);
                    result.add(aDocumentRequirementProfile);
                }
            } finally {
                em.close();
            }
        }
        return result;
    }
    
    public List<G01User> findG01UserListByFeaturedField(String featuredParamKey,
                                                        String featuredParamValue,
                                                        String featuredNamedQuery) 
    {
        List<G01User> result = new ArrayList<>();
        if (ZcaValidator.isNotNullEmpty(featuredParamValue)){
            EntityManager em = getEntityManager();
            try {
                HashMap<String, Object> params = new HashMap<>();
                params.put(featuredParamKey, featuredParamValue);
                result = GardenJpaUtils.findEntityListByNamedQuery(em, G01User.class, featuredNamedQuery, params);
            } finally {
                em.close();
            }
        }
        return result;
    }
    
    public List<G01User> findG01UserListByFeaturedDateRangeFields(String dateField, Date dateFrom, Date dateTo){
        List<G01User> result = new ArrayList<>();
        if ((dateFrom != null) && (dateTo != null)){
            EntityManager em = getEntityManager();
            try {
                String sqlQuery = "SELECT g FROM G01User g WHERE g." + dateField + " BETWEEN :dateFrom AND :dateTo";
                HashMap<String, Object> params = new HashMap<>();
                params.put("dateFrom", dateFrom);
                params.put("dateTo", dateTo);
                result = GardenJpaUtils.findEntityListByQuery(em, G01User.class, sqlQuery, params);
            } finally {
                em.close();
            }
        }
        return result;
    }
}
