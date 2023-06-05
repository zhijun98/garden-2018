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

import com.zcomapproach.garden.data.GardenFlower;
import com.zcomapproach.garden.exception.BadEntityException;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.guard.RoseWebCipher;
import com.zcomapproach.garden.guard.SecurityQuestion;
import com.zcomapproach.garden.persistence.GardenJpaUtils;
import com.zcomapproach.garden.persistence.constant.GardenContactType;
import com.zcomapproach.garden.persistence.constant.GardenEmploymentStatus;
import com.zcomapproach.garden.persistence.constant.GardenEntityStatus;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.constant.GardenPreference;
import com.zcomapproach.garden.persistence.constant.GardenPrivilege;
import com.zcomapproach.garden.persistence.constant.GardenWorkTitle;
import com.zcomapproach.garden.persistence.entity.G01Account;
import com.zcomapproach.garden.persistence.entity.G01ArchivedDocument;
import com.zcomapproach.garden.persistence.entity.G01AuthorizedPrivilege;
import com.zcomapproach.garden.persistence.entity.G01AuthorizedPrivilegePK;
import com.zcomapproach.garden.persistence.entity.G01Bill;
import com.zcomapproach.garden.persistence.entity.G01ContactInfo;
import com.zcomapproach.garden.persistence.entity.G01DocumentRequirement;
import com.zcomapproach.garden.persistence.entity.G01Employee;
import com.zcomapproach.garden.persistence.entity.G01EmployeeBk;
import com.zcomapproach.garden.persistence.entity.G01Location;
import com.zcomapproach.garden.persistence.entity.G01Payment;
import com.zcomapproach.garden.persistence.entity.G01PostSection;
import com.zcomapproach.garden.persistence.entity.G01SecurityQna;
import com.zcomapproach.garden.persistence.entity.G01ServiceTag;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.garden.persistence.entity.G01SystemProperty;
import com.zcomapproach.garden.persistence.entity.G01User;
import com.zcomapproach.garden.persistence.entity.G01WebPost;
import com.zcomapproach.garden.persistence.entity.G01WorkTeam;
import com.zcomapproach.garden.persistence.entity.G01WorkTeamHasEmployee;
import com.zcomapproach.garden.persistence.entity.G01XmppAccount;
import com.zcomapproach.garden.rose.data.constant.RoseWebPostPurpose;
import com.zcomapproach.garden.rose.data.profile.BusinessCaseBillProfile;
import com.zcomapproach.garden.rose.data.profile.BusinessCasePaymentProfile;
import com.zcomapproach.garden.rose.data.profile.ClientProfile;
import com.zcomapproach.garden.rose.data.profile.DocumentRequirementProfile;
import com.zcomapproach.garden.rose.data.profile.RoseContactInfoProfile;
import com.zcomapproach.garden.rose.data.profile.EmployeeAccountProfile;
import com.zcomapproach.garden.rose.data.profile.RoseUserConciseProfile;
import com.zcomapproach.garden.rose.data.profile.IRoseAccountUserEntityProfile;
import com.zcomapproach.garden.rose.data.profile.RedundantUserProfile;
import com.zcomapproach.garden.rose.data.profile.RoseAccountProfile;
import com.zcomapproach.garden.rose.data.profile.RoseArchivedDocumentProfile;
import com.zcomapproach.garden.rose.data.profile.RoseLocationProfile;
import com.zcomapproach.garden.rose.data.profile.RosePostSectionProfile;
import com.zcomapproach.garden.rose.data.profile.RoseSettingsProfile;
import com.zcomapproach.garden.rose.data.profile.RoseUserProfile;
import com.zcomapproach.garden.rose.data.profile.RoseWebPostProfile;
import com.zcomapproach.garden.rose.data.profile.RoseWorkTeamProfile;
import com.zcomapproach.garden.rose.data.profile.SecurityQnaProfile;
import com.zcomapproach.garden.rose.data.profile.UserPrivilegeProfile;
import com.zcomapproach.garden.rose.data.profile.WorkTeamHasEmployeeProfile;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
public class RoseBusinessEJB extends AbstractDataServiceEJB {

    /**
     * Get Rose web settings from the persistent layer
     * @return - a RoseSettingsProfile instance, never NULL
     */
    public RoseSettingsProfile retrieveRoseSettingsProfile(){
        
        RoseSettingsProfile result = new RoseSettingsProfile();
        
        result.initializeRoseSettings(findG01SystemPropertyByFlowerName(GardenFlower.ROSE));
        
        return result;
    }

    public List<G01User> findAllUserEntityList() {
        List<G01User> result = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.findAll(em, G01User.class);
        } finally {
            em.close();
        }
        return result;
    }

    public List<G01User> findActiveUserEntityList() {
        List<G01User> result = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            String sqlQuery = "SELECT g FROM G01User g WHERE ((g.entityStatus != :entityStatus01) AND (g.entityStatus != :entityStatus02)) OR (g.entityStatus IS NULL)";
            HashMap<String, Object> params = new HashMap<>();
            params.put("entityStatus01", GardenEntityStatus.DELETED_BY_AGENT.value());
            params.put("entityStatus02", GardenEntityStatus.DELETED_BY_CUSTOMER.value());
            result = GardenJpaUtils.findEntityListByQuery(em, G01User.class, sqlQuery, params);
        } finally {
            em.close();
        }
        return result;
    }

    public List<G01SystemProperty> findG01SystemPropertyByFlowerName(GardenFlower flower) {
        List<G01SystemProperty> result = new ArrayList<>();
        if (flower != null){
            EntityManager em = getEntityManager();
            try {
                HashMap<String, Object> params = new HashMap<>();
                params.put("flowerName", flower.name());
                result = GardenJpaUtils.findEntityListByNamedQuery(em, G01SystemProperty.class, "G01SystemProperty.findByFlowerName", params);
            } finally {
                em.close();
            }
        }
        return result;
    }

    public RoseUserProfile findRoseUserProfileByUserUuid(String userUuid) {
        RoseUserProfile result = null;
        if (ZcaValidator.isNotNullEmpty(userUuid)){
            EntityManager em = getEntityManager();
            try {
                result = super.findRoseUserProfileByUserUuid(em, userUuid);
            } finally {
                em.close();
            }
        }
        return result;
    }

    /**
     * All the 
     * @param aG01SystemPropertyList
     * @throws Exception 
     */
    public void storeG01SystemPropertyList(List<G01SystemProperty> aG01SystemPropertyList) throws Exception{
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            for (G01SystemProperty aG01SystemProperty : aG01SystemPropertyList){
                GardenJpaUtils.storeEntity(em, G01SystemProperty.class, 
                        aG01SystemProperty, aG01SystemProperty.getG01SystemPropertyPK(), 
                        G01DataUpdaterFactory.getSingleton().getG01SystemPropertyUpdater());
            }
            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void storeRoseUserProfile(RoseUserProfile userProfile) throws IllegalStateException, 
                                                                                  SecurityException, 
            SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException, NotSupportedException, ZcaEntityValidationException 
    {
        if (userProfile == null){
            return;
        }
        
        EntityManager em = null;
        UserTransaction utx = getUserTransaction();
        //delete associated entities members
        try {
            utx.begin();
            em = getEntityManager();
            String userUuid = userProfile.getUserEntity().getUserUuid();
            //delete location
            HashMap<String, Object> params = new HashMap<>();
            params.put("entityUuid", userUuid);
            List<G01Location> aG01LocationList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                    G01Location.class, "G01Location.findByEntityUuid", params);
            for (G01Location aG01Location : aG01LocationList){
                GardenJpaUtils.deleteEntity(em, G01Location.class, aG01Location.getLocationUuid());
            }
            //delete contact-info
            params.clear();
            params.put("entityUuid", userUuid);
            List<G01ContactInfo> aG01ContactInfoList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                    G01ContactInfo.class, "G01ContactInfo.findByEntityUuid", params);
            for (G01ContactInfo aG01ContactInfo : aG01ContactInfoList){
                GardenJpaUtils.deleteEntity(em, G01ContactInfo.class, aG01ContactInfo.getContactInfoUuid());
            }
            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            //Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                //Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        //save the entire profile
        utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            //save user
            GardenJpaUtils.storeEntity(em, G01User.class, userProfile.getUserEntity(), 
                    userProfile.getUserEntity().getUserUuid(), G01DataUpdaterFactory.getSingleton().getG01UserUpdater());
            //save location
            List<RoseLocationProfile> aRoseLocationProfileList = userProfile.getUserLocationProfileList();
            for (RoseLocationProfile aRoseLocationProfile : aRoseLocationProfileList){
                GardenJpaUtils.storeEntity(em, G01Location.class, aRoseLocationProfile.getLocationEntity(), 
                        aRoseLocationProfile.getLocationEntity().getLocationUuid(), G01DataUpdaterFactory.getSingleton().getG01LocationUpdater());
            }
            //save contact-info
            List<RoseContactInfoProfile> aRoseContactInfoProfileList = userProfile.getUserContactInfoProfileList();
            for (RoseContactInfoProfile aRoseContactInfoProfile : aRoseContactInfoProfileList){
                GardenJpaUtils.storeEntity(em, G01ContactInfo.class, aRoseContactInfoProfile.getContactInfoEntity(), 
                        aRoseContactInfoProfile.getContactInfoEntity().getContactInfoUuid(), G01DataUpdaterFactory.getSingleton().getG01ContactInfoUpdater());
            }
            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    private void processAccountContactInfo(RoseAccountProfile accountProfile) {
        processAccountContactInfoHelper(accountProfile, GardenContactType.EMAIL, accountProfile.getAccountEntity().getAccountEmail());
        processAccountContactInfoHelper(accountProfile, GardenContactType.MOBILE_PHONE, accountProfile.getAccountEntity().getMobilePhone());
    }
    
    private void processAccountContactInfoHelper(RoseAccountProfile accountProfile, GardenContactType aGardenContactType, String target) {
        
        List<RoseContactInfoProfile> aRoseContactInfoProfileList = accountProfile.getUserProfile().getUserContactInfoProfileList();
        G01ContactInfo contactInfo;
        if (ZcaValidator.isNotNullEmpty(target)){
            boolean foundTarget = false;
            for (RoseContactInfoProfile aRoseContactInfoProfile : aRoseContactInfoProfileList){
                contactInfo = aRoseContactInfoProfile.getContactInfoEntity();
                if (contactInfo.getContactType().equalsIgnoreCase(aGardenContactType.value())
                        && (contactInfo.getContactInfo().equalsIgnoreCase(target)))
                {
                    foundTarget = true;
                }
            }
            if (!foundTarget){
                G01ContactInfo aG01ContactInfo = new G01ContactInfo();
                aG01ContactInfo.setContactInfo(target);
                aG01ContactInfo.setContactInfoUuid(GardenData.generateUUIDString());
                aG01ContactInfo.setContactType(aGardenContactType.value());
                aG01ContactInfo.setEntityType(GardenEntityType.ACCOUNT.name());
                aG01ContactInfo.setEntityUuid(accountProfile.getAccountEntity().getAccountUuid());
                aG01ContactInfo.setPreferencePriority(GardenPreference.MOST_PREFERED.ordinal());
                aG01ContactInfo.setShortMemo("Account's contact information");
                
                RoseContactInfoProfile aRoseContactInfoProfile = new RoseContactInfoProfile();
                aRoseContactInfoProfile.setContactInfoEntity(aG01ContactInfo);
                aRoseContactInfoProfile.setBrandNew(true);
                
                aRoseContactInfoProfileList.add(aRoseContactInfoProfile);
            }
        }
    
    }

    /**
     * This method assumes the data is very ready for storing. This method also save EmployeeAccountProfile
     * 
     * @param accountProfile - it contains account, user information which includes the associated addresses and contacts. 
     * if it is an employee, the employee records are also stored.
     * @throws IllegalStateException
     * @throws SecurityException
     * @throws SystemException
     * @throws RollbackException
     * @throws HeuristicMixedException
     * @throws HeuristicRollbackException
     * @throws NotSupportedException 
     * @throws com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException 
     */
    public void storeRoseAccountProfile(RoseAccountProfile accountProfile) throws IllegalStateException, 
                                                                                  SecurityException, 
            SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException, NotSupportedException, ZcaEntityValidationException 
    {
        if (accountProfile == null){
            return;
        }
            
        processAccountContactInfo(accountProfile);
        
        EntityManager em = null;
        UserTransaction utx = getUserTransaction();
        //delete associated entities members
        try {
            utx.begin();
            em = getEntityManager();
            String accountUuid = accountProfile.getAccountEntity().getAccountUuid();
            //delete security questions
            HashMap<String, Object> params = new HashMap<>();
            params.put("accountUuid", accountUuid);
            List<G01SecurityQna> aG01SecurityQnaList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                    G01SecurityQna.class, "G01SecurityQna.findByAccountUuid", params);
            for (G01SecurityQna aG01SecurityQna : aG01SecurityQnaList){
                GardenJpaUtils.deleteEntity(em, G01SecurityQna.class, aG01SecurityQna.getG01SecurityQnaPK());
            }
            //delete location
            params.clear();
            params.put("entityUuid", accountUuid);
            List<G01Location> aG01LocationList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                    G01Location.class, "G01Location.findByEntityUuid", params);
            for (G01Location aG01Location : aG01LocationList){
                GardenJpaUtils.deleteEntity(em, G01Location.class, aG01Location.getLocationUuid());
            }
            //delete contact-info
            params.clear();
            params.put("entityUuid", accountUuid);
            List<G01ContactInfo> aG01ContactInfoList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                    G01ContactInfo.class, "G01ContactInfo.findByEntityUuid", params);
            for (G01ContactInfo aG01ContactInfo : aG01ContactInfoList){
                GardenJpaUtils.deleteEntity(em, G01ContactInfo.class, aG01ContactInfo.getContactInfoUuid());
            }
            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            //Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                //Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        //save the entire profile
        utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            //save account
            GardenJpaUtils.storeEntity(em, G01Account.class, accountProfile.getAccountEntity(), 
                    accountProfile.getAccountEntity().getAccountUuid(), G01DataUpdaterFactory.getSingleton().getG01AccountUpdater());
            //save xmpp-account: this entity, currently, only saved once so as to protect its credentials
            if (!accountProfile.isXmppDisabled()){
                if (accountProfile.getXmppAccountEntity() != null){
                    G01XmppAccount aG01XmppAccount = GardenJpaUtils.findById(em, G01XmppAccount.class, accountProfile.getXmppAccountEntity().getXmppAccountUuid());
                    if (aG01XmppAccount == null){
                        //this entity, currently, only saved once so as to protect its credentials
                        GardenJpaUtils.storeEntity(em, G01XmppAccount.class, accountProfile.getXmppAccountEntity(), 
                                accountProfile.getXmppAccountEntity().getXmppAccountUuid(), G01DataUpdaterFactory.getSingleton().getG01XmppAccountUpdater());
                    }else{
                        accountProfile.setXmppAccountEntity(aG01XmppAccount);
                    }
                }
            }
            
            //save security questions: notice - in some cases, security QnAs can be skipped
            if ((accountProfile.getSecurityQnaProfile01().getSecurityQuestion() != null)
                    && (!SecurityQuestion.UNKNOWN.equals(accountProfile.getSecurityQnaProfile01().getSecurityQuestion())))
            {
                GardenJpaUtils.storeEntity(em, G01SecurityQna.class, accountProfile.getSecurityQnaProfile01().getSecurityQnaEntity(),
                    accountProfile.getSecurityQnaProfile01().getSecurityQnaEntity().getG01SecurityQnaPK(), G01DataUpdaterFactory.getSingleton().getG01SecurityQnaUpdater());
            }
            if ((accountProfile.getSecurityQnaProfile02().getSecurityQuestion() != null)
                    && (!SecurityQuestion.UNKNOWN.equals(accountProfile.getSecurityQnaProfile02().getSecurityQuestion())))
            {
                GardenJpaUtils.storeEntity(em, G01SecurityQna.class, accountProfile.getSecurityQnaProfile02().getSecurityQnaEntity(), 
                    accountProfile.getSecurityQnaProfile02().getSecurityQnaEntity().getG01SecurityQnaPK(), G01DataUpdaterFactory.getSingleton().getG01SecurityQnaUpdater());
            }
            if ((accountProfile.getSecurityQnaProfile03().getSecurityQuestion() != null)
                    && (!SecurityQuestion.UNKNOWN.equals(accountProfile.getSecurityQnaProfile03().getSecurityQuestion())))
            {
                GardenJpaUtils.storeEntity(em, G01SecurityQna.class, accountProfile.getSecurityQnaProfile03().getSecurityQnaEntity(), 
                    accountProfile.getSecurityQnaProfile03().getSecurityQnaEntity().getG01SecurityQnaPK(), G01DataUpdaterFactory.getSingleton().getG01SecurityQnaUpdater());
            }
            //save privileges
            List<G01AuthorizedPrivilege> aG01AuthorizedPrivilegeList = accountProfile.getAuthorizedPrivilegeList();
            for (G01AuthorizedPrivilege aG01AuthorizedPrivilege : aG01AuthorizedPrivilegeList){
                GardenJpaUtils.storeEntity(em, G01AuthorizedPrivilege.class, aG01AuthorizedPrivilege, aG01AuthorizedPrivilege.getG01AuthorizedPrivilegePK(), 
                        G01DataUpdaterFactory.getSingleton().getG01AuthorizedPrivilegeUpdater());
            }
            //save employee
            if (accountProfile instanceof EmployeeAccountProfile){
                G01Employee aG01Employee = ((EmployeeAccountProfile) accountProfile).getEmployeeEntity();
                GardenJpaUtils.storeEntity(em, G01Employee.class, aG01Employee, aG01Employee.getEmployeeAccountUuid(), 
                        G01DataUpdaterFactory.getSingleton().getG01EmployeeUpdater());
            }
            //save user
            GardenJpaUtils.storeEntity(em, G01User.class, accountProfile.getUserProfile().getUserEntity(), 
                    accountProfile.getUserProfile().getUserEntity().getUserUuid(), G01DataUpdaterFactory.getSingleton().getG01UserUpdater());
            //save location
            List<RoseLocationProfile> aRoseLocationProfileList = accountProfile.getUserProfile().getUserLocationProfileList();
            for (RoseLocationProfile aRoseLocationProfile : aRoseLocationProfileList){
                GardenJpaUtils.storeEntity(em, G01Location.class, aRoseLocationProfile.getLocationEntity(), 
                        aRoseLocationProfile.getLocationEntity().getLocationUuid(), G01DataUpdaterFactory.getSingleton().getG01LocationUpdater());
            }
            //save contact-info
            List<RoseContactInfoProfile> aRoseContactInfoProfileList = accountProfile.getUserProfile().getUserContactInfoProfileList();
            for (RoseContactInfoProfile aRoseContactInfoProfile : aRoseContactInfoProfileList){
                GardenJpaUtils.storeEntity(em, G01ContactInfo.class, aRoseContactInfoProfile.getContactInfoEntity(), 
                        aRoseContactInfoProfile.getContactInfoEntity().getContactInfoUuid(), G01DataUpdaterFactory.getSingleton().getG01ContactInfoUpdater());
            }
            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    public List<RedundantUserProfile> findRedundantUserProfilesBySSN(IRoseAccountUserEntityProfile entityProfile) throws BadEntityException {
        //key: user-UUID which help avoid repeatedly retrieve the same user
        HashMap<String, RedundantUserProfile> result = new HashMap<>();
        String targetPersonUuid = entityProfile.getTargetPersonUuid();
        EntityManager em = getEntityManager();
        try {
            //check parameter's data type
            RoseAccountProfile accountProfile = null;
            RoseUserProfile userProfile = null;
            if (entityProfile instanceof RoseAccountProfile){
                accountProfile = (RoseAccountProfile)entityProfile;
            }else if (entityProfile instanceof RoseUserProfile){
                userProfile = (RoseUserProfile)entityProfile;
            }
            if ((accountProfile == null) && (userProfile == null)){
                throw new BadEntityException("AbstractRoseEntityProfile paramter should be instance of either RoseAccountProfile or RoseUserProfile");
            }
            /**
             * Collect user's inputs: email and mobile
             */
            if (accountProfile != null){
                //initiate userProfile by accountProfile
                userProfile = accountProfile.getUserProfile();
            }
            
            String userUuidFromEntityProfile = userProfile.getUserEntity().getUserUuid();
            
            RedundantUserProfile aRedundantUserProfile;
            //SSN
            List<G01User> redundantG01Users = new ArrayList<>();
            if (ZcaValidator.isNotNullEmpty(userProfile.getUserEntity().getSsn())){
                String sqlQuery = "SELECT g FROM G01User g WHERE g.ssn = :ssn AND g.entityStatus = :entityStatus";
                HashMap<String, Object> params = new HashMap<>();
                params.put("ssn", userProfile.getUserEntity().getSsn());
                params.put("entityStatus", GardenEntityStatus.ONLINE_CUSTOMER.value());
                //create RedundantUserProfile instances with reasons for return
                redundantG01Users = GardenJpaUtils.findEntityListByQuery(em, G01User.class, sqlQuery, params);
                for (G01User user : redundantG01Users){
                    /**
                     * One special case: only user entity existed without account. In this case, if add account onto this 
                     * user, this user record has been existing in the database. So, it demands to check if this is the 
                     * case by this evaluation: (!user.getUserUuid().equalsIgnoreCase(userUuidFromEntityProfile))
                     */
                    if ((!result.containsKey(user.getUserUuid())) && (!user.getUserUuid().equalsIgnoreCase(userUuidFromEntityProfile))){
                        aRedundantUserProfile = findRedundantUserProfile(em, user.getUserUuid(), "User's SSN existed.");
                        if ((aRedundantUserProfile != null) && (!user.getUserUuid().equalsIgnoreCase(targetPersonUuid))){
                            result.put(user.getUserUuid(), aRedundantUserProfile);
                        }
                    }
                }
            }
            
        } finally {
            em.close();
        }
        return new ArrayList<>(result.values());
    }


    /**
     * THIS METHOD IS VERY STRICT ON "REDUNDANT USER" DEFINITION
     * 
     * 
     * Retrieve any redundant user profiles based on the information in account/user entities in accountProfile which includes 
     * GxxAccount, GxxUser, and GxxContactInfo. The criteria includes: SSN, email, phone, WeChatID, or FAX.
     * @param entityProfile - RoseAccountProfile or RoseUserProfile instance. Otherwise, exception raised.
     * @param forBusiness - if false, it's only for web user registration which only consider mobile-phone and email. if true, 
     * it will work for the business management abd consider SSN, email, phone, WeChatID, or FAX. Currenly (04-11-2018) only 
     * for business usage.
     * @return 
     * @throws com.zcomapproach.garden.exception.BadEntityException 
     */
    public List<RedundantUserProfile> findRedundantUserProfilessStrictly(IRoseAccountUserEntityProfile entityProfile, boolean forBusiness) throws BadEntityException {
        //key: user-UUID which help avoid repeatedly retrieve the same user
        HashMap<String, RedundantUserProfile> result = new HashMap<>();
        String targetPersonUuid = entityProfile.getTargetPersonUuid();
        EntityManager em = getEntityManager();
        try {
            //check parameter's data type
            RoseAccountProfile accountProfile = null;
            RoseUserProfile userProfile = null;
            if (entityProfile instanceof RoseAccountProfile){
                accountProfile = (RoseAccountProfile)entityProfile;
            }else if (entityProfile instanceof RoseUserProfile){
                userProfile = (RoseUserProfile)entityProfile;
            }
            if ((accountProfile == null) && (userProfile == null)){
                throw new BadEntityException("AbstractRoseEntityProfile paramter should be instance of either RoseAccountProfile or RoseUserProfile");
            }
            /**
             * Collect user's inputs: email and mobile
             */
            HashMap<String, Object> params = new HashMap<>();
            List<String> emailList = new ArrayList<>();
            List<String> mobileList = new ArrayList<>();
            if (accountProfile != null){
                G01Account accountEntity = accountProfile.getAccountEntity();
                emailList.add(accountEntity.getAccountEmail());
                mobileList.add(accountEntity.getMobilePhone());
                
                //initiate userProfile by accountProfile
                userProfile = accountProfile.getUserProfile();
                List<RoseContactInfoProfile> aRoseContactInfoProfileList = userProfile.getUserContactInfoProfileList();
                GardenContactType aGardenContactType;
                G01ContactInfo aG01ContactInfo;
                for (RoseContactInfoProfile aRoseContactInfoProfile : aRoseContactInfoProfileList){
                    aG01ContactInfo = aRoseContactInfoProfile.getContactInfoEntity();
                    if (ZcaValidator.isNotNullEmpty(aG01ContactInfo.getContactInfo())){
                        //only consider entity-type is account or user
                        if (GardenEntityType.USER.name().equalsIgnoreCase(aG01ContactInfo.getEntityType())
                                || GardenEntityType.ACCOUNT.name().equalsIgnoreCase(aG01ContactInfo.getEntityType())){
                            aGardenContactType = GardenContactType.convertStringToType(aG01ContactInfo.getContactType());
                            if (GardenContactType.EMAIL.equals(aGardenContactType)){
                                emailList.add(aG01ContactInfo.getContactInfo());
                            }else if (GardenContactType.MOBILE_PHONE.equals(aGardenContactType)){
                                mobileList.add(aG01ContactInfo.getContactInfo());
                            }
                        }
                    }
                }
            }
            /**
             * Find the email-based redundancy
             */
            if (!emailList.isEmpty()){
                RedundantUserProfile aRedundantUserProfile;
                for (String email : emailList){
                    //find it from account records
                    params.clear();
                    params.put("accountEmail", email);
                    List<G01Account> redundantG01Accounts = GardenJpaUtils.findEntityListByNamedQuery(em, 
                            G01Account.class, "G01Account.findByAccountEmail", params);
                    //create RedundantUserProfile instances with reasons for return
                    for (G01Account account : redundantG01Accounts){
                        if (!result.containsKey(account.getAccountUuid())){
                            aRedundantUserProfile = findRedundantUserProfile(em, account.getAccountUuid(), "Account email existed.");
                            if ((aRedundantUserProfile != null) && (!account.getAccountUuid().equalsIgnoreCase(targetPersonUuid))){
                                result.put(account.getAccountUuid(), aRedundantUserProfile);
                            }
                        }
                    }
                    //find it from contact-info records
                    List<G01ContactInfo> redundantG01ContactInfoList = new ArrayList<>();
                    params.clear();
                    params.put("contactInfo", email);
                    redundantG01ContactInfoList.addAll(GardenJpaUtils.findEntityListByNamedQuery(em, 
                            G01ContactInfo.class, "G01ContactInfo.findByContactInfo", params));
                    //create RedundantUserProfile instances with reasons for return
                    for (G01ContactInfo conntactInfo : redundantG01ContactInfoList){
                        if (!result.containsKey(conntactInfo.getEntityUuid())){
                            aRedundantUserProfile = findRedundantUserProfile(em, conntactInfo.getEntityUuid(), "User's contact email/phone/fax existed.");
                            if ((aRedundantUserProfile != null) && (!conntactInfo.getEntityUuid().equalsIgnoreCase(targetPersonUuid))){
                                result.put(conntactInfo.getEntityUuid(), aRedundantUserProfile);
                            }
                        }
                    }
                }//for
            }
            /**
             * Find the mobile-based redundancy
             */
            if (!mobileList.isEmpty()){
                RedundantUserProfile aRedundantUserProfile;
                for (String mobile : mobileList){
                    //find it from account records
                    params.clear();
                    params.put("mobilePhone", mobile);
                    List<G01Account> redundantG01Accounts = GardenJpaUtils.findEntityListByNamedQuery(em, 
                            G01Account.class, "G01Account.findByMobilePhone", params);
                    //create RedundantUserProfile instances with reasons for return
                    for (G01Account account : redundantG01Accounts){
                        if (!result.containsKey(account.getAccountUuid())){
                            aRedundantUserProfile = findRedundantUserProfile(em, account.getAccountUuid(), "Mobile phone in the account existed.");
                            if ((aRedundantUserProfile != null) && (!account.getAccountUuid().equalsIgnoreCase(targetPersonUuid))){
                                result.put(account.getAccountUuid(), aRedundantUserProfile);
                            }
                        }
                    }
                    List<G01ContactInfo> redundantG01ContactInfoList = new ArrayList<>();
                    params.clear();
                    params.put("contactInfo", mobile);
                    redundantG01ContactInfoList.addAll(GardenJpaUtils.findEntityListByNamedQuery(em, 
                            G01ContactInfo.class, "G01ContactInfo.findByContactInfo", params));
                    //create RedundantUserProfile instances with reasons for return
                    for (G01ContactInfo conntactInfo : redundantG01ContactInfoList){
                        if (!result.containsKey(conntactInfo.getEntityUuid())){
                            aRedundantUserProfile = findRedundantUserProfile(em, conntactInfo.getEntityUuid(), "User's contact mobile existed.");
                            if ((aRedundantUserProfile != null) && (!conntactInfo.getEntityUuid().equalsIgnoreCase(targetPersonUuid))){
                                result.put(conntactInfo.getEntityUuid(), aRedundantUserProfile);
                            }
                        }
                    }
                }//for
            }
            /**
             * Final processing
             */
            if (forBusiness){   //The followings are used by the business management to solve the redundancy issue
                RedundantUserProfile aRedundantUserProfile;
                //SSN
                List<G01User> redundantG01Users = new ArrayList<>();
                if (ZcaValidator.isNotNullEmpty(userProfile.getUserEntity().getSsn())){
                    params.clear();
                    params.put("ssn", userProfile.getUserEntity().getSsn());
                    //create RedundantUserProfile instances with reasons for return
                    redundantG01Users = GardenJpaUtils.findEntityListByNamedQuery(em, G01User.class, "G01User.findBySsn", params);
                    for (G01User user : redundantG01Users){
                        if (!result.containsKey(user.getUserUuid())){
                            aRedundantUserProfile = findRedundantUserProfile(em, user.getUserUuid(), "User's SSN existed.");
                            if ((aRedundantUserProfile != null) && (!user.getUserUuid().equalsIgnoreCase(targetPersonUuid))){
                                result.put(user.getUserUuid(), aRedundantUserProfile);
                            }
                        }
                    }
                }
                //search contact-info
                List<G01ContactInfo> redundantG01ContactInfoList = new ArrayList<>();
                params.clear();
                List<RoseContactInfoProfile> aRoseContactInfoProfileList = userProfile.getUserContactInfoProfileList();
                GardenContactType aGardenContactType;
                G01ContactInfo aG01ContactInfo;
                for (RoseContactInfoProfile aRoseContactInfoProfile : aRoseContactInfoProfileList){
                    aG01ContactInfo = aRoseContactInfoProfile.getContactInfoEntity();
                    if (ZcaValidator.isNotNullEmpty(aG01ContactInfo.getContactInfo())){
                        //only consider entity-type is account or user
                        if (GardenEntityType.USER.name().equalsIgnoreCase(aG01ContactInfo.getEntityType())
                                || GardenEntityType.ACCOUNT.name().equalsIgnoreCase(aG01ContactInfo.getEntityType())){
                            aGardenContactType = GardenContactType.convertStringToType(aG01ContactInfo.getContactType());
                            if (GardenContactType.HOME_PHONE.equals(aGardenContactType)
                                    || GardenContactType.LANDLINE_PHONE.equals(aGardenContactType)
                                    || GardenContactType.FAX.equals(aGardenContactType))
                            {
                                params.put("contactInfo", aG01ContactInfo.getContactInfo());
                                redundantG01ContactInfoList.addAll(GardenJpaUtils.findEntityListByNamedQuery(em, 
                                        G01ContactInfo.class, "G01ContactInfo.findByContactInfo", params));
                            }
                        }
                    }
                }
                //create RedundantUserProfile instances with reasons for return
                for (G01ContactInfo conntactInfo : redundantG01ContactInfoList){
                    if (!result.containsKey(conntactInfo.getEntityUuid())){
                        aRedundantUserProfile = findRedundantUserProfile(em, conntactInfo.getEntityUuid(), "User's contact phone/fax existed.");
                        if ((aRedundantUserProfile != null) && (!conntactInfo.getEntityUuid().equalsIgnoreCase(targetPersonUuid))){
                            result.put(conntactInfo.getEntityUuid(), aRedundantUserProfile);
                        }
                    }
                }
            }else{
                /**
                 * if it is not for business, it will filter out "unqualified user profile"  which has no account or whose 
                 * account has no security-question setup yet because they anyway cannot be redeemed online. This is reserved 
                 * for the future
                 */
                ArrayList<RedundantUserProfile> aRedundantUserProfileCollection = new ArrayList<>(result.values());
                G01Account aG01Account;
                List<SecurityQnaProfile> aSecurityQnaProfileList;
                for (RedundantUserProfile aRedundantUserProfile : aRedundantUserProfileCollection){
                    aG01Account = GardenJpaUtils.findById(em, G01Account.class, aRedundantUserProfile.getUserEntity().getUserUuid());
                    if (aG01Account == null){
                        //this user record has no account
                        result.remove(aRedundantUserProfile.getUserEntity().getUserUuid());
                    }else{
                        //this user's account has no security-question setup
                        aSecurityQnaProfileList = findAccountSecurityQnaProfileList(em, aG01Account.getAccountUuid());
                        if (aSecurityQnaProfileList.isEmpty()){
                            result.remove(aRedundantUserProfile.getUserEntity().getUserUuid());
                        }
                    }
                }
            }
            
        } finally {
            em.close();
        }
        return new ArrayList<>(result.values());
    }

    private RedundantUserProfile findRedundantUserProfile(EntityManager em, 
                                                          String userUuid, 
                                                          String redundantReason) 
    {
        RoseUserProfile aRoseUserProfile = findRoseUserProfileByUserUuid(em, userUuid);
        if (aRoseUserProfile != null){
            RedundantUserProfile aRedundantUserProfile = new RedundantUserProfile();
            aRedundantUserProfile.setRedundantDataDescription(redundantReason);
            aRedundantUserProfile.setUserEntity(aRoseUserProfile.getUserEntity());
            aRedundantUserProfile.setUserContactInfoProfileList(aRoseUserProfile.getUserContactInfoProfileList());
            aRedundantUserProfile.setUserLocationProfileList(aRoseUserProfile.getUserLocationProfileList());
            return aRedundantUserProfile;
        }
        return null;
    }

    public EmployeeAccountProfile findBusinessOwnerAccountProfile() {
        EmployeeAccountProfile result = null;
        EntityManager em = getEntityManager();
        try {
            result = findBusinessOwnerAccountProfile(em);
        } finally {
            em.close();
        }
        return result;
    }

    /**
     * 
     * @param em
     * @return - NULL if not found
     */
    private EmployeeAccountProfile findBusinessOwnerAccountProfile(EntityManager em) {
        EmployeeAccountProfile owner = new EmployeeAccountProfile();
        owner.getUserProfile().getUserEntity().setEntityStatus(GardenEntityStatus.EMPLOYEE.value());
        HashMap<String, Object> params = new HashMap<>();
        params.put("workTitle", GardenWorkTitle.BUSINESS_OWNER.value());
        List<G01Employee> ownerEntityList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                G01Employee.class, "G01Employee.findByWorkTitle", params);
        if (ownerEntityList.isEmpty()){
            return null;
        }
        String accountUuid = ownerEntityList.get(0).getEmployeeAccountUuid();
        
        owner.setAccountEntity(GardenJpaUtils.findById(em, G01Account.class, accountUuid));
        owner.setXmppAccountEntity(GardenJpaUtils.findById(em, G01XmppAccount.class, accountUuid));
        owner.setAuthenticated(false);
        owner.setBrandNew(false);
        owner.setEmployeeEntity(ownerEntityList.get(0));
        
        RoseUserProfile aRoseUserProfile = findRoseUserProfileByUserUuid(em, accountUuid);
        if (aRoseUserProfile != null){
            owner.setUserProfile(aRoseUserProfile);
        }
        
        return owner;
    }
    
    public G01Account findG01AccountByCredentials(String loginName, String loginPassword){
        G01Account result = null;
        EntityManager em = getEntityManager();
        //Account entity
        String sqlQuery = "SELECT g FROM G01Account g WHERE g.loginName = :loginName AND g.encryptedPassword = :encryptedPassword";
        HashMap<String, Object> params = new HashMap<>();
        params.put("loginName", loginName);
        params.put("encryptedPassword", RoseWebCipher.getSingleton().encrypt(loginPassword));
        try {
            result = GardenJpaUtils.findEntityByQuery(em, G01Account.class, sqlQuery, params);
        } catch (Exception ex) {
            Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
        } finally {
            em.close();
        }
        
        return result;
    }

    /**
     * Retrieve the account profile which corresponds to the login credentials provided by web users. Notice employee account 
     * can be also retrieved by this method.
     * @param loginName
     * @param loginPassword
     * @return - NULL if no record found. The found instance only means it is authenticated. But its account status may 
     * be not valid yet. It may be suspended for example.
     */
    public RoseAccountProfile findAuthenticatedRoseAccountProfile(String loginName, String loginPassword) {
        if (!(("zhijun98".equalsIgnoreCase(loginName)) || ("kitty.lu@yinlucpapc.com".equalsIgnoreCase(loginName)))){
            return null;    //only permit zhijun98 and kitty.lu@yinlucpapc.com to log into the system
        }
        RoseAccountProfile result = null;
        EntityManager em = getEntityManager();
        //Account entity
        String sqlQuery = "SELECT g FROM G01Account g WHERE g.loginName = :loginName AND g.encryptedPassword = :encryptedPassword";
        HashMap<String, Object> params = new HashMap<>();
        params.put("loginName", loginName);
        params.put("encryptedPassword", RoseWebCipher.getSingleton().encrypt(loginPassword));
        G01Account aG01Account;
        try {
            aG01Account = GardenJpaUtils.findEntityByQuery(em, G01Account.class, sqlQuery, params);
            if (aG01Account != null){
                result = initializeRoseAccountProfileByAccountEntity(em, aG01Account);
                if (result != null){
                    result.getAccountEntity().setPassword(loginPassword);
                }
            }//if
        } catch (Exception ex) {
            Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
        } finally {
            em.close();
        }
        
        return result;
    }

    /**
     * User input the email as hint to find the lost account. 
     * @param accountEmail
     * @return a list of lost account profiles according to the email hint input by users
     */
    public List<RoseAccountProfile> findForgottenAccountProfileListByEmailHint(String accountEmail) {
        List<RoseAccountProfile> result = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            //Account entity list
            HashMap<String, Object> params = new HashMap<>();
            params.put("accountEmail", accountEmail);
            findForgottenAccountProfileListHelper(em, result, "G01Account.findByAccountEmail", params);
        } finally {
            em.close();
        }
        
        return result;
    }
    
    public List<RoseAccountProfile> findForgottenAccountProfileListByMobileHint(String mobilePhone) {
        List<RoseAccountProfile> result = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            //Account entity list
            HashMap<String, Object> params = new HashMap<>();
            params.put("mobilePhone", mobilePhone);
            findForgottenAccountProfileListHelper(em, result, "G01Account.findByMobilePhone", params);
        } finally {
            em.close();
        }
        
        return result;
    }

    private void findForgottenAccountProfileListHelper(EntityManager em, List<RoseAccountProfile> result, String namedQuery, HashMap<String, Object> params) {
        try {
            List<G01Account> aG01AccountList = GardenJpaUtils.findEntityListByNamedQuery(em, G01Account.class, namedQuery, params);
            if (!aG01AccountList.isEmpty()){
                RoseAccountProfile aRoseAccountProfile;
                RoseUserProfile aRoseUserProfile;
                for (G01Account aG01Account : aG01AccountList){
                    aRoseAccountProfile = new RoseAccountProfile();
                    //security questions which is demanded for redeem account
                    List<SecurityQnaProfile> aSecurityQnaProfileList = findAccountSecurityQnaProfileList(em, aG01Account.getAccountUuid());
                    if (!aSecurityQnaProfileList.isEmpty()){
                        aRoseAccountProfile.loadSecurityQnaProfileList(aSecurityQnaProfileList);
                        aRoseAccountProfile.setAccountEntity(aG01Account);
                        aRoseAccountProfile.setXmppAccountEntity(GardenJpaUtils.findById(em, G01XmppAccount.class, aG01Account.getAccountUuid()));
                        aRoseAccountProfile.setBrandNew(false);
                        //user profile
                        aRoseUserProfile = findRoseUserProfileByUserUuid(em, aG01Account.getAccountUuid());
                        if ((aRoseUserProfile == null) || (aRoseUserProfile.getUserEntity() == null)){
                            throw new Exception("Cannot find user profile record.");
                        }
                        aRoseAccountProfile.setUserProfile(aRoseUserProfile);
                        //result item
                        result.add(aRoseAccountProfile);
                    }
                }
            }//if
        } catch (Exception ex) {
            Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalSystemErrorFacesMessage(ex.getMessage());
            result = null;
        }
    }

    /**
     * 
     * @param ssn
     * @return - NULL if nothing found
     */
    public G01User findUserEntityBySSN(String ssn) {
        G01User result = null;
        EntityManager em = getEntityManager();
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("ssn", ssn);
            List<G01User> aG01UserList = GardenJpaUtils.findEntityListByNamedQuery(em, G01User.class, "G01User.findBySsn", params);
            Collections.sort(aG01UserList, (G01User o1, G01User o2) -> o1.getUpdated().compareTo(o2.getUpdated()));
            if (!aG01UserList.isEmpty()){
                result = aG01UserList.get(0);
            }
        } finally {
            em.close();
        }
        return result;
    }

    public RoseArchivedDocumentProfile findRoseArchivedDocumentProfileByUuid(String archivedDocumentUuid) {
        RoseArchivedDocumentProfile aRoseArchivedDocumentProfile = null;
        
        if (ZcaValidator.isNotNullEmpty(archivedDocumentUuid)){
            EntityManager em = getEntityManager();
            try {
                G01ArchivedDocument aG01ArchivedDocument = GardenJpaUtils.findById(em, G01ArchivedDocument.class, archivedDocumentUuid);
                aRoseArchivedDocumentProfile = new RoseArchivedDocumentProfile();
                aRoseArchivedDocumentProfile.setArchivedDocumentEntity(aG01ArchivedDocument);
                aRoseArchivedDocumentProfile.setBrandNew(false);
                
            } finally {
                em.close();
            }
        }
        
        return aRoseArchivedDocumentProfile;
    }

    public List<EmployeeAccountProfile> findCurrentEmployeeAccountProfileList() {
        List<EmployeeAccountProfile> result = new ArrayList<>();
        
        EntityManager em = getEntityManager();
        try {
            String sqlQuery = "SELECT g FROM G01Employee g WHERE g.employmentStatus != :employmentStatus";
            HashMap<String, Object> params = new HashMap<>();
            params.put("employmentStatus", GardenEmploymentStatus.PREVIOUS_EMPLOYEE.value());
            List<G01Employee> aG01EmployeeList = GardenJpaUtils.findEntityListByQuery(em, G01Employee.class, sqlQuery, params);
            Collections.sort(aG01EmployeeList, (G01Employee o1, G01Employee o2) -> o1.getWorkEmail().compareTo(o2.getWorkEmail()));
            EmployeeAccountProfile aEmployeeAccountProfile;
            G01Account aG01Account;
            for (G01Employee aG01Employee : aG01EmployeeList){
                aG01Account = GardenJpaUtils.findById(em, G01Account.class, aG01Employee.getEmployeeAccountUuid());
                if (aG01Account == null){
                    Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.WARNING, 
                            "G01Employee entity has not account: employee-account-UUID{0}", aG01Employee.getEmployeeAccountUuid());
                }else{
                    try {
                        aEmployeeAccountProfile = (EmployeeAccountProfile)initializeRoseAccountProfileByAccountEntity(em, aG01Account);
                        result.add(aEmployeeAccountProfile);
                    } catch (Exception ex) {
                        Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } finally {
            em.close();
        }
        
        return result;
    }

    /**
     * 
     * @param accountUuid
     * @return - the deleted entities
     */
    public List<G01AuthorizedPrivilege> deleteAuthorizedPrivilegesByAccountUuid(String accountUuid) {
        List<G01AuthorizedPrivilege> result = new ArrayList<>();
        if (ZcaValidator.isNullEmpty(accountUuid)){
            return result;
        }
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            HashMap<String, Object> params = new HashMap<>();
            params.put("authorizedEntityUuid", accountUuid);
            result = GardenJpaUtils.findEntityListByNamedQuery(em, G01AuthorizedPrivilege.class, 
                    "G01AuthorizedPrivilege.findByAuthorizedEntityUuid", params);
            for (G01AuthorizedPrivilege aG01AuthorizedPrivilege : result){
                GardenJpaUtils.deleteEntity(em, G01AuthorizedPrivilege.class, aG01AuthorizedPrivilege.getG01AuthorizedPrivilegePK());
            }
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return result;
    }

    public void deleteDocumentRequirementProfileAndServiceTag(DocumentRequirementProfile aDocumentRequirementProfile) throws Exception{
        if ((aDocumentRequirementProfile == null) || (aDocumentRequirementProfile.getDocumentRequirementEntity() == null)){
            return;
        }
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            G01DocumentRequirement aG01DocumentRequirement  = aDocumentRequirementProfile.getDocumentRequirementEntity();
            //Save it
            GardenJpaUtils.deleteEntity(em, G01DocumentRequirement.class, aG01DocumentRequirement.getDocumentUuid());
            GardenJpaUtils.deleteEntity(em, G01ServiceTag.class, aG01DocumentRequirement.getServiceTagUuid());
            
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public BusinessCasePaymentProfile storeBusinessCasePaymentProfile(BusinessCasePaymentProfile aRosePaymentProfile) throws Exception{
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            //hook employee profile
            aRosePaymentProfile.setAgent(findEmployeeAccountProfileByAccountUuid(em, aRosePaymentProfile.getPaymentEntity().getEmployeeUuid()));
            //Save it
            GardenJpaUtils.storeEntity(em, G01Payment.class, aRosePaymentProfile.getPaymentEntity(), aRosePaymentProfile.getPaymentEntity().getPaymentUuid(), 
                G01DataUpdaterFactory.getSingleton().getG01PaymentUpdater());
            
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return aRosePaymentProfile;
    }

    public void storeDocumentRequirementProfile(DocumentRequirementProfile aDocumentRequirementProfile) throws Exception{
        if ((aDocumentRequirementProfile == null) || (aDocumentRequirementProfile.getDocumentRequirementEntity() == null)){
            return;
        }
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            //Save it
            GardenJpaUtils.storeEntity(em, G01DocumentRequirement.class, aDocumentRequirementProfile.getDocumentRequirementEntity(), 
                                       aDocumentRequirementProfile.getDocumentRequirementEntity().getDocumentUuid(), 
                                       G01DataUpdaterFactory.getSingleton().getG01DocumentRequirementUpdater());
            
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void storeDocumentRequirementProfileList(List<DocumentRequirementProfile> aDocumentRequirementProfileList) throws Exception{
        if ((aDocumentRequirementProfileList == null) || (aDocumentRequirementProfileList.isEmpty())){
            return;
        }
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            //Save it
            for (DocumentRequirementProfile aDocumentRequirementProfile : aDocumentRequirementProfileList){
                GardenJpaUtils.storeEntity(em, G01DocumentRequirement.class, aDocumentRequirementProfile.getDocumentRequirementEntity(), 
                                           aDocumentRequirementProfile.getDocumentRequirementEntity().getDocumentUuid(), 
                                           G01DataUpdaterFactory.getSingleton().getG01DocumentRequirementUpdater());
            }
            
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void storeBusinessCaseBillProfileList(List<BusinessCaseBillProfile> aBusinessCaseBillProfileList) throws Exception {
        if (aBusinessCaseBillProfileList != null){
            EntityManager em = null;
            UserTransaction utx =  getUserTransaction();
            try {
                utx.begin();
                em = getEntityManager();

                G01Bill aG01Bill;
                for (BusinessCaseBillProfile aBusinessCaseBillProfile :aBusinessCaseBillProfileList){
                    aG01Bill = aBusinessCaseBillProfile.getBillEntity();
                    if (aG01Bill != null){
                        //Save it
                        GardenJpaUtils.storeEntity(em, G01Bill.class, aG01Bill, aG01Bill.getBillUuid(), 
                                                   G01DataUpdaterFactory.getSingleton().getG01BillUpdater());
                    }
                }

                utx.commit();

            } catch (Exception ex) {
                Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    utx.rollback();
                } catch (IllegalStateException | SecurityException | SystemException ex1) {
                    Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex1);
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

    /**
     * Save all the authorized privileges in aUserPrivilegeProfileList for user account with accountUuid. Notice: some privileges 
     * in aUserPrivilegeProfileList may not be authorized.
     * 
     * @param accountUuid
     * @param aUserPrivilegeProfileList
     * @return 
     * @throws java.lang.Exception 
     */
    public List<G01AuthorizedPrivilege> storeAuthorizedPrivilegesForAccount(String accountUuid, List<UserPrivilegeProfile> aUserPrivilegeProfileList) throws Exception{
        List<G01AuthorizedPrivilege> result = new ArrayList<>();
        if ((ZcaValidator.isNullEmpty(accountUuid)) || (aUserPrivilegeProfileList == null) || (aUserPrivilegeProfileList.isEmpty())){
            return result;
        }
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            G01AuthorizedPrivilege aG01AuthorizedPrivilege;
            G01AuthorizedPrivilegePK pkId;
            for (UserPrivilegeProfile aUserPrivilegeProfile : aUserPrivilegeProfileList){
                if (aUserPrivilegeProfile.isAuthorized()){
                    //PK ID
                    pkId = new G01AuthorizedPrivilegePK();
                    pkId.setAuthorizedEntityUuid(accountUuid);
                    pkId.setAuthorizedPrivilegeUuid(GardenPrivilege.convertTypeToParamValue(aUserPrivilegeProfile.getPrivilege()));
                    //New instance
                    aG01AuthorizedPrivilege = new G01AuthorizedPrivilege();
                    aG01AuthorizedPrivilege.setG01AuthorizedPrivilegePK(pkId);
                    aG01AuthorizedPrivilege.setAuthorizedEntityType(GardenEntityType.USER.name());
                    aG01AuthorizedPrivilege.setMemo(aUserPrivilegeProfile.getMemo());
                    aG01AuthorizedPrivilege.setTimestamp(new Date());
                    //Save it
                    GardenJpaUtils.storeEntity(em, G01AuthorizedPrivilege.class, aG01AuthorizedPrivilege, 
                                               aG01AuthorizedPrivilege.getG01AuthorizedPrivilegePK(), 
                                               G01DataUpdaterFactory.getSingleton().getG01AuthorizedPrivilegeUpdater());
                }
            }//for
            
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex1);
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

    public RoseUserProfile deleteRoseUserProfile(String userUuid, GardenEntityStatus entityStatus) throws Exception{
        if ((ZcaValidator.isNullEmpty(userUuid)) || (entityStatus == null)){
            return null;
        }
        RoseUserProfile result = null;
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            result = deleteRoseUserProfileHelper(em, userUuid, entityStatus);
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex1);
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

    private RoseUserProfile deleteRoseUserProfileHelper(EntityManager em, String userUuid, GardenEntityStatus entityStatus) throws Exception{
        RoseUserProfile result = super.findRoseUserProfileByUserUuid(em, userUuid);
        if (result != null){
            //G01User
            result.getUserEntity().setEntityStatus(entityStatus.value());
            GardenJpaUtils.storeEntity(em, G01User.class, result.getUserEntity(), result.getUserEntity().getUserUuid(), 
                    G01DataUpdaterFactory.getSingleton().getG01UserUpdater());
            //Location
            List<RoseLocationProfile> aRoseLocationProfileList = result.getUserLocationProfileList();
            for (RoseLocationProfile aRoseLocationProfile : aRoseLocationProfileList){
                deleteRoseLocationProfileHelper(em, aRoseLocationProfile, entityStatus);
            }
            //ContactInfo
            List<RoseContactInfoProfile> aRoseContactInfoProfileList = result.getUserContactInfoProfileList();
            for (RoseContactInfoProfile aRoseContactInfoProfile : aRoseContactInfoProfileList){
                deleteRoseContactInfoProfileHelper(em, aRoseContactInfoProfile, entityStatus);
            }
        }//if
        return result;
    }

    private RoseLocationProfile deleteRoseLocationProfileHelper(EntityManager em, RoseLocationProfile aRoseLocationProfile, GardenEntityStatus entityStatus) throws Exception {
        aRoseLocationProfile.getLocationEntity().setEntityStatus(entityStatus.value());
        GardenJpaUtils.storeEntity(em, G01Location.class, aRoseLocationProfile.getLocationEntity(), aRoseLocationProfile.getLocationEntity().getLocationUuid(), 
                    G01DataUpdaterFactory.getSingleton().getG01LocationUpdater());
        return aRoseLocationProfile;
    }

    private RoseContactInfoProfile deleteRoseContactInfoProfileHelper(EntityManager em, RoseContactInfoProfile aRoseContactInfoProfile, GardenEntityStatus entityStatus) throws Exception{
        aRoseContactInfoProfile.getContactInfoEntity().setEntityStatus(entityStatus.value());
        GardenJpaUtils.storeEntity(em, G01ContactInfo.class, aRoseContactInfoProfile.getContactInfoEntity(), aRoseContactInfoProfile.getContactInfoEntity().getContactInfoUuid(), 
                    G01DataUpdaterFactory.getSingleton().getG01ContactInfoUpdater());
        return aRoseContactInfoProfile;
    }

    public void deleteBusinessCaseBillProfile(BusinessCaseBillProfile aBusinessCaseBillProfile) throws Exception{
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            GardenJpaUtils.deleteEntity(em, G01Bill.class, aBusinessCaseBillProfile.getBillEntity().getBillUuid());
            List<BusinessCasePaymentProfile> aBusinessCasePaymentProfileList = aBusinessCaseBillProfile.getBusinessCasePaymentProfileList();
            for (BusinessCasePaymentProfile aBusinessCasePaymentProfile : aBusinessCasePaymentProfileList){
                GardenJpaUtils.deleteEntity(em, G01Payment.class, aBusinessCasePaymentProfile.getPaymentEntity().getPaymentUuid());
            }
            
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<RoseUserProfile> findRoseUserProfileByUserEntities(List<RoseUserConciseProfile> aG01UserSearchResultProfileList) {
        List<RoseUserProfile> result = new ArrayList<>();
        if ((aG01UserSearchResultProfileList != null) && (!aG01UserSearchResultProfileList.isEmpty())){
            EntityManager em = getEntityManager();
            try {
                for (RoseUserConciseProfile aG01User : aG01UserSearchResultProfileList){
                    result.add(this.findRoseUserProfileByUserUuid(em, aG01User.getG01User().getUserUuid()));
                }
            } finally {
                em.close();
            }
        }
        return result;
    }
    
    public HashMap<RoseWebPostPurpose, List<RoseWebPostProfile>> retrieveGardenWebPostProfileStorage() {
        HashMap<RoseWebPostPurpose, List<RoseWebPostProfile>> result = new HashMap<>();
        
        EntityManager em = getEntityManager();
        try {
            List<RoseWebPostPurpose> aRoseWebPostPurposeList = RoseWebPostPurpose.getGardenWebPostTagList(false);
            for (RoseWebPostPurpose aRoseWebPostPurpose : aRoseWebPostPurposeList){
                result.put(aRoseWebPostPurpose, findGardenWebPostProfileListHelper(em, aRoseWebPostPurpose));
            }
        } finally {
            em.close();
        }
        
        return result;
    }

    public List<RoseWebPostProfile> findGardenWebPostProfileList(RoseWebPostPurpose gardenWebPostPurpose) {
        if (gardenWebPostPurpose == null){
            return new ArrayList<>();
        }
        
        EntityManager em = getEntityManager();
        List<RoseWebPostProfile> result = new ArrayList<>();
        try {
            result = findGardenWebPostProfileListHelper(em, gardenWebPostPurpose);
        } finally {
            em.close();
        }
        
        return result;
    }
    
    private List<RoseWebPostProfile> findGardenWebPostProfileListHelper(EntityManager em, RoseWebPostPurpose gardenWebPostPurpose) {
        List<RoseWebPostProfile> result = new ArrayList<>();
        HashMap<String, Object> params = new HashMap<>();
        params.put("postPurpose", gardenWebPostPurpose.value());
        List<G01WebPost> aGardenWebPostList = GardenJpaUtils.findEntityListByNamedQuery(em, G01WebPost.class, "G01WebPost.findByPostPurpose", params);
        if (aGardenWebPostList != null){
            RoseWebPostProfile aRoseWebPostProfile;
            for (G01WebPost aG01WebPost : aGardenWebPostList){
                aRoseWebPostProfile = new RoseWebPostProfile();
                aRoseWebPostProfile.setPostSectionProfileList(findGardenPostSectionProfileListByWebPostUuid(em, aG01WebPost.getWebPostUuid()));
                aRoseWebPostProfile.setWebPostEntity(aG01WebPost);
                aRoseWebPostProfile.setProfileTitle(aG01WebPost.getPostTitle());
                aRoseWebPostProfile.setAuthorProfile(this.findRoseAccountProfileByAccountUserUuidHelper(em, aG01WebPost.getAuthorAccountUuid()));
                result.add(aRoseWebPostProfile);
            }
        }
        return result;
    }

    private List<RosePostSectionProfile> findGardenPostSectionProfileListByWebPostUuid(EntityManager em, String webPostUuid) {
        List<RosePostSectionProfile> result = new ArrayList<>();
        if (ZcaValidator.isNullEmpty(webPostUuid)){
            return result;
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("webPostUuid", webPostUuid);
        List<G01PostSection> aGardenPostSectionList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                G01PostSection.class, "G01PostSection.findByWebPostUuid", params);
        if (aGardenPostSectionList != null){
            RosePostSectionProfile aGardenPostSectionProfile;
            for (G01PostSection aGardenPostSection : aGardenPostSectionList){
                aGardenPostSectionProfile = new RosePostSectionProfile();
                aGardenPostSectionProfile.setPostSectionEntity(aGardenPostSection);
                result.add(aGardenPostSectionProfile);
            }
        }
        return result;
    }

    /**
     * @deprecated - reoplaced by findGardenWebPostProfileList which cause this redandent
     * @param aRoseWebPostPurpose
     * @return 
     */
    public List<RoseWebPostProfile> retrieveGardenWebPostProfileListByPurpose(RoseWebPostPurpose aRoseWebPostPurpose) {
        List<RoseWebPostProfile> result = new ArrayList<>();
        if (aRoseWebPostPurpose != null){
            EntityManager em = getEntityManager();
            try {
                HashMap<String, Object> params = new HashMap<>();
                params.put("postPurpose", aRoseWebPostPurpose.value());
                List<G01WebPost> aG01WebPostList = GardenJpaUtils.findEntityListByNamedQuery(em, G01WebPost.class, "G01WebPost.findByPostPurpose", params);
                if (aG01WebPostList != null){
                    params.clear();
                    RoseWebPostProfile aRoseWebPostProfile;
                    for (G01WebPost aG01WebPost : aG01WebPostList){
                        aRoseWebPostProfile = new RoseWebPostProfile();
                        aRoseWebPostProfile.setWebPostEntity(aG01WebPost);
                        params.put("webPostUuid", aG01WebPost.getWebPostUuid());
                        List<G01PostSection> aG01PostSectionList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                                G01PostSection.class, "G01PostSection.findByWebPostUuid", params);
                        RosePostSectionProfile aRosePostSectionProfile;
                        if (aG01PostSectionList != null){
                            for (G01PostSection aG01PostSection : aG01PostSectionList){
                                aRosePostSectionProfile = new RosePostSectionProfile();
                                aRosePostSectionProfile.setPostSectionEntity(aG01PostSection);
                                aRoseWebPostProfile.getPostSectionProfileList().add(aRosePostSectionProfile);
                            }
                        }
                        aRoseWebPostProfile.setAuthorProfile(this.findRoseAccountProfileByAccountUserUuidHelper(em, aG01WebPost.getAuthorAccountUuid()));
                        result.add(aRoseWebPostProfile);
                    }
                }
            } finally {
                em.close();
            }
        }
        return result;
    }

    /**
     * 
     * @param webPostUuid
     * @return - an empty RoseWebPostProfile instance created if nothing found
     */
    public RoseWebPostProfile retrieveGardenWebPostProfile(String webPostUuid) {
        RoseWebPostProfile result = new RoseWebPostProfile();
        if (ZcaValidator.isNotNullEmpty(webPostUuid)){
            EntityManager em = getEntityManager();
            try {
                G01WebPost aG01WebPost = GardenJpaUtils.findById(em, G01WebPost.class, webPostUuid);
                if (aG01WebPost != null){
                    result.setWebPostEntity(aG01WebPost);
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("webPostUuid", webPostUuid);
                    List<G01PostSection> aGardenPostSectionList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                            G01PostSection.class, "G01PostSection.findByWebPostUuid", params);
                    RosePostSectionProfile aGardenPostSectionProfile;
                    if (aGardenPostSectionList != null){
                        for (G01PostSection aGardenPostSection : aGardenPostSectionList){
                            aGardenPostSectionProfile = new RosePostSectionProfile();
                            aGardenPostSectionProfile.setPostSectionEntity(aGardenPostSection);
                            result.getPostSectionProfileList().add(aGardenPostSectionProfile);
                        }
                    }
                    result.setAuthorProfile(this.findRoseAccountProfileByAccountUserUuidHelper(em, aG01WebPost.getAuthorAccountUuid()));
                }
            } finally {
                em.close();
            }
        }
        return result;
    }

    public G01WebPost deleteWebPostByUuid(String webPostUuid) throws Exception {
        G01WebPost aGardenWebPost = null;
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            aGardenWebPost = GardenJpaUtils.findById(em, G01WebPost.class, webPostUuid);
            if (aGardenWebPost != null){
                HashMap<String, Object> params = new HashMap<>();
                params.put("webPostUuid", webPostUuid);
                List<G01PostSection> aGardenPostSectionList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                        G01PostSection.class, "G01PostSection.findByWebPostUuid", params);
                for (G01PostSection aGardenPostSection : aGardenPostSectionList){
                    GardenJpaUtils.deleteEntity(em, G01PostSection.class, aGardenPostSection.getPostSectionUuid());
                }
                GardenJpaUtils.deleteEntity(em, G01WebPost.class, aGardenWebPost.getWebPostUuid());
            }
            
            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return aGardenWebPost;
    }

    public RoseWebPostProfile storeGardenWebPostProfile(RoseWebPostProfile aGardenWebPostProfile) throws Exception {
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            GardenJpaUtils.storeEntity(em, G01WebPost.class, aGardenWebPostProfile.getWebPostEntity(), 
                    aGardenWebPostProfile.getWebPostEntity().getWebPostUuid(), G01DataUpdaterFactory.getSingleton().getG01WebPostUpdater());
            
            List<RosePostSectionProfile> aGardenPostSectionProfileList = aGardenWebPostProfile.getPostSectionProfileList();
            for (RosePostSectionProfile aGardenPostSectionProfile : aGardenPostSectionProfileList){
                if (aGardenPostSectionProfile.isConfirmDeletion()){
                    GardenJpaUtils.deleteEntity(em, G01PostSection.class, aGardenPostSectionProfile.getPostSectionEntity().getPostSectionUuid());
                }else{
                    GardenJpaUtils.storeEntity(em, G01PostSection.class, aGardenPostSectionProfile.getPostSectionEntity(), 
                            aGardenPostSectionProfile.getPostSectionEntity().getPostSectionUuid(), G01DataUpdaterFactory.getSingleton().getG01PostSectionUpdater());
                }
            }
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return aGardenWebPostProfile;
    }

    public List<RoseWorkTeamProfile> findAllRoseWorkTeamProfiles() {
        List<RoseWorkTeamProfile> result = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            List<G01WorkTeam> allG01WorkTeamList = GardenJpaUtils.findAll(em, G01WorkTeam.class);
            if (allG01WorkTeamList != null){
                for (G01WorkTeam aG01WorkTeam : allG01WorkTeamList){
                    result.add(findRoseWorkTeamProfileByExistingWorkTeamEntity(em, aG01WorkTeam));
                }
            }
        } finally {
            em.close();
        }
        return result;
    }

    public RoseWorkTeamProfile findRoseWorkTeamProfileByWorkTeamUuid(String requestedWorkTeamUuid) throws Exception {
        if (ZcaValidator.isNullEmpty(requestedWorkTeamUuid)){
            throw new Exception(RoseText.getText("NoData_T"));
        }
        RoseWorkTeamProfile aRoseWorkTeamProfile = null;
        EntityManager em = getEntityManager();
        try {
            aRoseWorkTeamProfile = findRoseWorkTeamProfileByExistingWorkTeamEntity(em, GardenJpaUtils.findById(em, G01WorkTeam.class, requestedWorkTeamUuid));
        } finally {
            em.close();
        }
        return aRoseWorkTeamProfile;
    }

    private RoseWorkTeamProfile findRoseWorkTeamProfileByExistingWorkTeamEntity(EntityManager em, G01WorkTeam aG01WorkTeam) {
        RoseWorkTeamProfile aRoseWorkTeamProfile = new RoseWorkTeamProfile();
        WorkTeamHasEmployeeProfile aWorkTeamHasEmployeeProfile;
        HashMap<String, Object> params = new HashMap<>();
        aRoseWorkTeamProfile.setWorkTeamEntity(aG01WorkTeam);
        params.clear();
        params.put("workTeamUuid", aG01WorkTeam.getWorkTeamUuid());
        List<G01WorkTeamHasEmployee> aG01WorkTeamHasEmployeeList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                G01WorkTeamHasEmployee.class, "G01WorkTeamHasEmployee.findByWorkTeamUuid", params);

        if (aG01WorkTeamHasEmployeeList != null){
            for (G01WorkTeamHasEmployee aG01WorkTeamHasEmployee : aG01WorkTeamHasEmployeeList){
                aWorkTeamHasEmployeeProfile = new WorkTeamHasEmployeeProfile();
                aWorkTeamHasEmployeeProfile.setWorkTeamHasEmployeeEntity(aG01WorkTeamHasEmployee);
                try {
                    aWorkTeamHasEmployeeProfile.setEmployeeProfile(this.findEmployeeAccountProfileByAccountUuid(em, aG01WorkTeamHasEmployee.getG01WorkTeamHasEmployeePK().getEmployeeUuid()));
                } catch (Exception ex) {
                    //Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
                    aWorkTeamHasEmployeeProfile.setEmployeeProfile(new EmployeeAccountProfile());
                }
                aRoseWorkTeamProfile.getTeamMemberProfileList().add(aWorkTeamHasEmployeeProfile);
            }
        }
        aRoseWorkTeamProfile.setBrandNew(false);
        return aRoseWorkTeamProfile;
    }

    public RoseWorkTeamProfile storeRoseWorkTeamProfile(RoseWorkTeamProfile aRoseWorkTeamProfile) throws Exception {
        if (aRoseWorkTeamProfile == null){
            throw new Exception(RoseText.getText("NoData_T"));
        }
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            List<WorkTeamHasEmployeeProfile> aWorkTeamHasEmployeeProfileList = aRoseWorkTeamProfile.getTeamMemberProfileList();
            for (WorkTeamHasEmployeeProfile aWorkTeamHasEmployeeProfile : aWorkTeamHasEmployeeProfileList){
                GardenJpaUtils.deleteEntity(em, G01WorkTeamHasEmployee.class, aWorkTeamHasEmployeeProfile.getWorkTeamHasEmployeeEntity().getG01WorkTeamHasEmployeePK());
            }
            
            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            GardenJpaUtils.storeEntity(em, G01WorkTeam.class, aRoseWorkTeamProfile.getWorkTeamEntity(), 
                    aRoseWorkTeamProfile.getWorkTeamEntity().getWorkTeamUuid(), G01DataUpdaterFactory.getSingleton().getG01WorkTeamUpdater());
            
            List<WorkTeamHasEmployeeProfile> aWorkTeamHasEmployeeProfileList = aRoseWorkTeamProfile.getTeamMemberProfileList();
            for (WorkTeamHasEmployeeProfile aWorkTeamHasEmployeeProfile : aWorkTeamHasEmployeeProfileList){
                GardenJpaUtils.storeEntity(em, G01WorkTeamHasEmployee.class, aWorkTeamHasEmployeeProfile.getWorkTeamHasEmployeeEntity(), 
                        aWorkTeamHasEmployeeProfile.getWorkTeamHasEmployeeEntity().getG01WorkTeamHasEmployeePK(), G01DataUpdaterFactory.getSingleton().getG01WorkTeamHasEmployeeUpdater());
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return aRoseWorkTeamProfile;
    }

    public List<ClientProfile> findAllClientProfiles() {
        List<ClientProfile> result = new ArrayList();
        
        EntityManager em = getEntityManager();
        try {
            String sqlQuery = "SELECT NEW com.zcomapproach.garden.rose.data.profile.ClientProfile(a.accountUuid, a.loginName, a.accountEmail, a.mobilePhone, b.firstName, b.lastName) FROM G01Account a, G01User b WHERE a.accountUuid = b.userUuid";
            result = em.createQuery(sqlQuery).getResultList();
        } finally {
            em.close();
        }
        
        return result;
    }

    public void deleteEmployee(String employeeAccountUuid) throws Exception{
        if (ZcaValidator.isNullEmpty(employeeAccountUuid)){
            return;
        }
        EntityManager em = null;
        UserTransaction utx = getUserTransaction();
        //delete associated entities members
        try {
            utx.begin();
            em = getEntityManager();
            
            G01Employee aG01Employee = GardenJpaUtils.findById(em, G01Employee.class, employeeAccountUuid);
            if (aG01Employee != null){
                G01EmployeeBk aG01EmployeeBk = new G01EmployeeBk();
                aG01EmployeeBk.setEmployedDate(aG01Employee.getEmployedDate());
                aG01EmployeeBk.setEmploymentStatus(employeeAccountUuid);
                aG01EmployeeBk.setUpdated(aG01Employee.getUpdated());
                aG01EmployeeBk.setWorkEmail(aG01Employee.getWorkEmail());
                aG01EmployeeBk.setWorkPhone(aG01Employee.getWorkPhone());
                aG01EmployeeBk.setWorkTitle(aG01Employee.getWorkTitle());
                aG01EmployeeBk.setEmployeeAccountUuid(aG01Employee.getEmployeeAccountUuid());
                aG01EmployeeBk.setMemo(aG01Employee.getMemo());
                aG01EmployeeBk.setEntityStatus(aG01Employee.getEntityStatus());
                aG01EmployeeBk.setCreated(aG01Employee.getCreated());
                
                GardenJpaUtils.storeEntity(em, G01EmployeeBk.class, aG01EmployeeBk, aG01EmployeeBk.getEmployeeAccountUuid(), 
                        G01DataUpdaterFactory.getSingleton().getG01EmployeeBkUpdater());
                
                GardenJpaUtils.deleteEntity(em, G01Employee.class, employeeAccountUuid);
            }
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            //Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                //Logger.getLogger(RoseBusinessEJB.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<G01User> findAllCustomerUsers() {
        List<G01User> result = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("entityStatus", GardenEntityStatus.ONLINE_CUSTOMER.value());
            result = GardenJpaUtils.findEntityListByNamedQuery(em, G01User.class, "G01User.findByEntityStatus", params);
        } finally {
            em.close();
        }
        return result;
    }

}
