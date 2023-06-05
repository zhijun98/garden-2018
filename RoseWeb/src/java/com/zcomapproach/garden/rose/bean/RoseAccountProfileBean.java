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

package com.zcomapproach.garden.rose.bean;

import com.zcomapproach.garden.data.constant.GardenAgreement;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.commons.persistent.exception.NonUniqueEntityException;
import com.zcomapproach.garden.guard.RoseWebCipher;
import com.zcomapproach.garden.persistence.constant.GardenAccountStatus;
import com.zcomapproach.garden.persistence.constant.GardenEntityStatus;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.constant.GardenPrivilegeGroup;
import com.zcomapproach.garden.persistence.constant.GardenPropertyType;
import com.zcomapproach.garden.persistence.constant.TaxpayerFederalFilingStatus;
import com.zcomapproach.garden.persistence.constant.TaxpayerRelationship;
import com.zcomapproach.garden.persistence.entity.G01Account;
import com.zcomapproach.garden.persistence.entity.G01Bill;
import com.zcomapproach.garden.persistence.entity.G01DocumentRequirement;
import com.zcomapproach.garden.persistence.entity.G01Location;
import com.zcomapproach.garden.persistence.entity.G01PersonalBusinessProperty;
import com.zcomapproach.garden.persistence.entity.G01PersonalProperty;
import com.zcomapproach.garden.persistence.entity.G01TaxpayerCase;
import com.zcomapproach.garden.persistence.entity.G01TaxpayerInfo;
import com.zcomapproach.garden.persistence.entity.G01TlcLicense;
import com.zcomapproach.garden.persistence.entity.G01User;
import com.zcomapproach.garden.persistence.entity.G01XmppAccount;
import com.zcomapproach.garden.rose.RosePageName;
import com.zcomapproach.garden.rose.data.profile.BusinessCaseBillProfile;
import com.zcomapproach.garden.rose.data.profile.ClientProfile;
import com.zcomapproach.garden.rose.data.profile.DocumentRequirementProfile;
import com.zcomapproach.garden.rose.data.profile.EmployeeAccountProfile;
import com.zcomapproach.garden.rose.data.profile.PersonalBusinessPropertyProfile;
import com.zcomapproach.garden.rose.data.profile.PersonalPropertyProfile;
import com.zcomapproach.garden.rose.data.profile.RoseAccountProfile;
import com.zcomapproach.garden.rose.data.profile.RosePrivilegeProfile;
import com.zcomapproach.garden.rose.data.profile.RoseUserProfile;
import com.zcomapproach.garden.rose.data.profile.TaxcorpCaseProfile;
import com.zcomapproach.garden.rose.data.profile.TaxpayerCaseProfile;
import com.zcomapproach.garden.rose.data.profile.TaxpayerInfoProfile;
import com.zcomapproach.garden.rose.data.profile.TlcLicenseProfile;
import com.zcomapproach.garden.rose.data.profile.UserPrivilegeProfile;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.garden.rose.util.RoseWebUtils;
import com.zcomapproach.garden.rose.util.RoseXmppUtils;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import org.jivesoftware.smack.AbstractXMPPConnection;

/**
 *
 * @author zhijun98
 */
@Named(value = "accountProfileBean")
@ViewScoped
public class RoseAccountProfileBean extends RoseUserProfileBean{
    
    private String requestAccountUuid;
    
    private String requestUsersUuid;    //only has user information, without account information
    
    //current account
    private RoseAccountProfile targetAccountProfile;
    
    /**
     * key: privilegeGroup constants in GardenPrivilege
     */
    private HashMap<GardenPrivilegeGroup, List<UserPrivilegeProfile>> targetUserPrivilegeProfileMap;
    
    /**
     * Historical taxpayer cases for this customer
     */
    private final HashMap<String, TaxpayerCaseProfile> historicalTaxpayerCaseProfileStorage = new HashMap<>();
    
    /**
     * Historical taxcorp cases for this customer: key-> EIN number
     */
    private final HashMap<String, TaxcorpCaseProfile> historicalTaxcorpCaseProfileStorage = new HashMap<>();

    public RoseAccountProfileBean() {
    }

    public String getRequestUsersUuid() {
        return requestUsersUuid;
    }

    public void setRequestUsersUuid(String requestUsersUuid) {
        if (ZcaValidator.isNotNullEmpty(requestUsersUuid)){
            getTargetAccountProfile().getAccountEntity().setAccountUuid(requestUsersUuid);
            getTargetAccountProfile().setUserProfile(getBusinessEJB().findRoseUserProfileByUserUuid(requestUsersUuid));
        }
        this.requestUsersUuid = requestUsersUuid;
    }

    @Override
    public String getTargetPersonUuid() {
        return getTargetAccountProfile().getAccountEntity().getAccountUuid();
    }

    public String getRequestAccountUuid() {
        return requestAccountUuid;
    }

    public void setRequestAccountUuid(String requestAccountUuid) {
        if (ZcaValidator.isNotNullEmpty(requestAccountUuid)){
            if ((this.isForTaxcorpCase()) || (this.isForTaxpayerCase())){
                targetAccountProfile = new RoseAccountProfile();
                targetAccountProfile.setAccountEntity(getBusinessEJB().findEntityByUuid(G01Account.class, requestAccountUuid));
                if (targetAccountProfile.getAccountEntity() == null){
                    targetAccountProfile.setAccountEntity(new G01Account());
                    setWebMessage(RoseText.getText("UserWithoutAccount_T"));
                }
                targetAccountProfile.setUserProfile(getBusinessEJB().findRoseUserProfileByUserUuid(requestAccountUuid));
            }else {
                RoseAccountProfile aRoseAccountProfile = getBusinessEJB().findRoseAccountProfileByAccountUserUuid(requestAccountUuid);
                if (requestAccountUuid.equalsIgnoreCase(aRoseAccountProfile.getAccountEntity().getAccountUuid())){
                    targetAccountProfile = aRoseAccountProfile;
                    checkUserRedundancy(targetAccountProfile);
                }
            }
        }
        this.requestAccountUuid = requestAccountUuid;
    }

    @Override
    public void populateRedundantUserProfile(String userUuid) {
        if (!getRoseUserSession().isEmployed()){
            return;
        }
        this.ignoreRedundancyDuringRegistration();
        setRequestAccountUuid(userUuid);
    }
    
    public List<ClientProfile> getAllCustomerAccountEntityList(){
        //zzj todo: it should filter out non-customer's accounts
        return getBusinessEJB().findAllClientProfiles();
    }

    private HashMap<GardenPrivilegeGroup, List<UserPrivilegeProfile>> getTargetUserPrivilegeProfileMap() {
        if (targetUserPrivilegeProfileMap == null) {
            targetUserPrivilegeProfileMap = new HashMap<>();
            List<RosePrivilegeProfile> aRosePrivilegeProfileList = getRosePrivileges().getAllRosePrivilegeProfiles();
            List<UserPrivilegeProfile> userPrivilegeProfileList;
            GardenPrivilegeGroup aRosePrivilegeGroup;
            for (RosePrivilegeProfile aRosePrivilegeProfile : aRosePrivilegeProfileList){
                aRosePrivilegeGroup = aRosePrivilegeProfile.getType();
                userPrivilegeProfileList = targetUserPrivilegeProfileMap.get(aRosePrivilegeGroup);
                if (userPrivilegeProfileList == null){
                    userPrivilegeProfileList = new ArrayList<>();
                }
                userPrivilegeProfileList.add(new UserPrivilegeProfile(aRosePrivilegeProfile,
                        getTargetAccountProfile().isAuthorized(aRosePrivilegeProfile)));
                targetUserPrivilegeProfileMap.put(aRosePrivilegeGroup, userPrivilegeProfileList);
            }
        }
        return targetUserPrivilegeProfileMap;
    } 
    
    public List<GardenPrivilegeGroup> getUserPrivilegeGroupList() {
        return new ArrayList<>(getTargetUserPrivilegeProfileMap().keySet());
    }
    
    public List<UserPrivilegeProfile> retrieveTargetUserPrivilegeProfileList(GardenPrivilegeGroup privilegeGroup) {
        List<UserPrivilegeProfile> userPrivilegeProfileList = getTargetUserPrivilegeProfileMap().get(privilegeGroup);
        if (userPrivilegeProfileList == null){
            userPrivilegeProfileList = new ArrayList<>();
        }
        return userPrivilegeProfileList;
    }

    private List<UserPrivilegeProfile> retrieveAllTargetUserPrivilegeProfileList() {
        List<GardenPrivilegeGroup> aRosePrivilegeGroupList = getUserPrivilegeGroupList();
        List<UserPrivilegeProfile> result = new ArrayList<>();
        for (GardenPrivilegeGroup aRosePrivilegeGroup : aRosePrivilegeGroupList){
            result.addAll(retrieveTargetUserPrivilegeProfileList(aRosePrivilegeGroup));
        }
        return result;
    }
    
    public String storeTargetUserPrivilegeProfileList(){
        try {
            getBusinessEJB().deleteAuthorizedPrivilegesByAccountUuid(targetAccountProfile.getAccountEntity().getAccountUuid());
            targetAccountProfile.authorizePrivilegeList(
                getBusinessEJB().storeAuthorizedPrivilegesForAccount(targetAccountProfile.getAccountEntity().getAccountUuid(),
                                                                     retrieveAllTargetUserPrivilegeProfileList()));
            setWebMessage(RoseText.getText("OperationSucceeded_T") + ": " 
                    + ZcaCalendar.convertToMMddyyyyHHmmss(new Date(), "-", " @ ", ":"));
        } catch (Exception ex) {
            Logger.getLogger(RoseAccountProfileBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    protected RoseAccountProfile createRoseAccountProfileInstance(){
        return new RoseAccountProfile();
    }

    public RoseAccountProfile getTargetAccountProfile() {
        if (targetAccountProfile == null){
            targetAccountProfile = createRoseAccountProfileInstance();
        }
        return targetAccountProfile;
    }

    public void setTargetAccountProfile(RoseAccountProfile targetAccountProfile) {
        this.targetAccountProfile = targetAccountProfile;
    }

    @Override
    public String getRosePageTopic() {
        return RoseText.getText("CustomerProfile");
    }

    @Override
    public GardenEntityType getEntityType() {
        return GardenEntityType.ACCOUNT;
    }

    @Override
    public String getRequestedEntityUuid() {
        return getTargetAccountProfile().getAccountEntity().getAccountUuid();
    }

    @Override
    public String getTargetReturnWebPath() {
        HashMap<String, String> params = new HashMap<>();
        params.put(getRoseParamKeys().getAccountUuidParamKey(), getTargetAccountProfile().getAccountEntity().getAccountUuid());
        return getTargetReturnWebPageName() + RoseWebUtils.constructWebQueryString(params, true);
    }

    @Override
    public RoseUserProfile getTargetUserProfile() {
        return getTargetAccountProfile().getUserProfile();
    }

    @Override
    public void setTargetUserProfile(RoseUserProfile targetUserProfile) {
        this.getTargetAccountProfile().setUserProfile(targetUserProfile);
    }

    /**
     * The current user account status is valid AND successfully logged in (i.e. authenticated)
     * @return 
     */
    public boolean isValidAuthenticatedStatus() {
        return getTargetAccountProfile().isAuthenticated() && getTargetAccountProfile().isValidAccount();
    }

    public boolean isEmployed() {
        return getTargetAccountProfile() instanceof EmployeeAccountProfile;
    }
    
    public boolean isBusinessOwner(){
        if (getTargetAccountProfile() instanceof EmployeeAccountProfile){
            return getRoseSettings().getBusinessOwnerProfile().getAccountEntity().getAccountUuid().equalsIgnoreCase(getTargetAccountProfile().getAccountEntity().getAccountUuid());
        }
        return false;
    }

    @Override
    public String requestNewCorporateTaxFiling() {
        HashMap<String, String> webParams = new HashMap<>();
        webParams.put(getRoseParamKeys().getViewPurposeParamKey(), getRoseParamValues().getCreateNewEntityParamValue());
        webParams.put(getRoseParamKeys().getCustomerUuidParamKey(), getTargetAccountProfile().getAccountEntity().getAccountUuid());
        return RosePageName.TaxcorpCaseMgtPage.name() + RoseWebUtils.constructWebQueryString(webParams, true);
    }

    @Override
    public String requestNewIndividualTaxFiling() {
        HashMap<String, String> webParams = new HashMap<>();
        webParams.put(getRoseParamKeys().getViewPurposeParamKey(), getRoseParamValues().getCreateNewEntityParamValue());
        webParams.put(getRoseParamKeys().getCustomerUuidParamKey(), getTargetAccountProfile().getAccountEntity().getAccountUuid());
        return RosePageName.TaxpayerCaseMgtPage.name() + RoseWebUtils.constructWebQueryString(webParams, true);
    }
    
    /**
     * This is used by employees to create a new account which could be a customer or employee.
     * @return 
     */
    @Override
    public String storeTargetPersonalProfile(){
        RoseAccountProfile currentTargetAccountProfile = getTargetAccountProfile();
        //Login name has to be unique for new entities
        HashMap<String, Object> params = new HashMap<>();
        if (super.isForCreateNewEntity()){
            params.put("loginName", currentTargetAccountProfile.getAccountEntity().getLoginName());
            try {
                G01Account aG01Account = getBusinessEJB().findEntityByNamedQuery(G01Account.class, "G01Account.findByLoginName", params);
                if (aG01Account != null){
                    RoseJsfUtils.setGlobalErrorFacesMessage(RoseText.getText("RedundantLoginNameFound_T"));
                    return null;
                }
            } catch (NonUniqueEntityException ex) {
                Logger.getLogger(RoseAccountProfileBean.class.getName()).log(Level.SEVERE, null, ex);
                RoseJsfUtils.setGlobalErrorFacesMessage(RoseText.getText("RedundantLoginNameFound_T"));
                return null;
            }
        }
        //check redundancy
        if (isForCreateNewEntity() && (checkUserRedundancy(currentTargetAccountProfile))){
            //Redundant records were found. Now, prompt "continue" or "send me the recovering code"
            return null;
        }
        
        try {

            validateAccountProfile(currentTargetAccountProfile);

            if (currentTargetAccountProfile instanceof EmployeeAccountProfile){
                currentTargetAccountProfile.getUserProfile().getUserEntity().setEntityStatus(GardenEntityStatus.EMPLOYEE.value());
            }else{
                currentTargetAccountProfile.getUserProfile().getUserEntity().setEntityStatus(GardenEntityStatus.ONLINE_CUSTOMER.value());
            }
            //set the current account status
            currentTargetAccountProfile.getAccountEntity().setAccountStatus(GardenAccountStatus.Valid.name());

            //store registration data
            getBusinessEJB().storeRoseAccountProfile(currentTargetAccountProfile);

            if (this.isForCreateNewEntity()){
                if (getRoseSettings().isXmppDisabled()){
                    currentTargetAccountProfile.setXmppDisabled(true);
                }else{
                    currentTargetAccountProfile.setXmppDisabled(false);
                    //create a XMPP account
                    AbstractXMPPConnection conn = RoseXmppUtils.createAbstractXMPPConnection();
                    RoseXmppUtils.createXmppAccount(RoseXmppUtils.createAbstractXMPPConnection(), this.getTargetAccountProfile().getXmppAccountEntity());
                    if (conn.isConnected()){
                        conn.disconnect();
                    }
                }
            }
            
            //return target...
            if (this.isForTaxcorpCase()){
                return requestNewCorporateTaxFiling();
            }else if (this.isForTaxpayerCase()){
                return requestNewIndividualTaxFiling();
            }else{
                super.setRequestedViewPurpose(null);//make isForCreateNewEntity false
                RoseJsfUtils.setGlobalSuccessfulOperationMessage();
                return null;
            }

        } catch (ZcaEntityValidationException ex) {
            RoseJsfUtils.setGlobalFatalFacesMessage(ex.getMessage());
        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException ex) {
            RoseJsfUtils.setGlobalFatalFacesMessage(RoseText.getText("SystemError") + ": " + ex.getMessage());
        }
        return null;
    }
    
    /**
     * This method will encrypt account's password into encrypted password
     * @param currentTargetAccountProfile
     * @throws ZcaEntityValidationException 
     */
    protected void validateAccountProfile(RoseAccountProfile currentTargetAccountProfile) throws ZcaEntityValidationException {
        
        //account: account must be associated with a concrete use profile and their UUID is the same
        G01Account accountEntity = currentTargetAccountProfile.getAccountEntity();
        if (ZcaValidator.isNullEmpty(accountEntity.getAccountUuid())){
            accountEntity.setAccountUuid(GardenData.generateUUIDString());
        }
        if (ZcaValidator.isNotNullEmpty(accountEntity.getPassword())){
            accountEntity.setEncryptedPassword(RoseWebCipher.getSingleton().encrypt(accountEntity.getPassword()));
            //accountEntity.setPassword(null);
        }else{
            throw new ZcaEntityValidationException(RoseText.getText("Password") + ": " + RoseText.getText("FieldRequired_T"));
        }
        //G01Employee
        if (currentTargetAccountProfile instanceof EmployeeAccountProfile){
            EmployeeAccountProfile aEmployeeAccountProfile = (EmployeeAccountProfile)currentTargetAccountProfile;
            aEmployeeAccountProfile.getEmployeeEntity().setEmployeeAccountUuid(accountEntity.getAccountUuid());
        }
        if (getRoseSettings().isXmppDisabled()){
            currentTargetAccountProfile.setXmppDisabled(true);
        }else{
            currentTargetAccountProfile.setXmppDisabled(false);
            //XMPP-account
            G01XmppAccount aG01XmppAccount = new G01XmppAccount();
            aG01XmppAccount.setXmppAccountUuid(accountEntity.getAccountUuid());
            aG01XmppAccount.setEncryptedPassword(accountEntity.getEncryptedPassword());
            aG01XmppAccount.setLoginName(accountEntity.getLoginName());
            aG01XmppAccount.setPassword(accountEntity.getPassword());
            currentTargetAccountProfile.setXmppAccountEntity(aG01XmppAccount);
        }
        //reconcile security QnA and set account UUID for them
        currentTargetAccountProfile.getSecurityQnaProfile01().reconcile(accountEntity.getAccountUuid());
        currentTargetAccountProfile.getSecurityQnaProfile02().reconcile(accountEntity.getAccountUuid());
        currentTargetAccountProfile.getSecurityQnaProfile03().reconcile(accountEntity.getAccountUuid());
        
        //user: user uuid is the same as the account
        validateUserProfile(currentTargetAccountProfile.getUserProfile());
        G01User userEntity = currentTargetAccountProfile.getUserProfile().getUserEntity();
        userEntity.setUserUuid(accountEntity.getAccountUuid());
    }
    
    public String constructDeadlineSsnKey(TaxpayerCaseProfile aTaxpayerCaseProfile){
        return ZcaCalendar.convertToMMddyyyy(aTaxpayerCaseProfile.getTaxpayerCaseEntity().getDeadline(), "-") 
                + aTaxpayerCaseProfile.getPrimaryTaxpayerProfile().getRoseUserProfile().getUserEntity().getSsn();
    }

    public HashMap<String, TaxpayerCaseProfile> getHistoricalTaxpayerCaseProfileStorage() {
        if ((historicalTaxpayerCaseProfileStorage == null) || (historicalTaxpayerCaseProfileStorage.isEmpty())){
            List<TaxpayerCaseProfile> aTaxpayerCaseProfileList = getTaxpayerEJB().findTaxpayerCaseProfileListByCustomerUuid(getTargetAccountProfile().getAccountEntity().getAccountUuid());
            for (TaxpayerCaseProfile aTaxpayerCaseProfile : aTaxpayerCaseProfileList){
                historicalTaxpayerCaseProfileStorage.put(constructDeadlineSsnKey(aTaxpayerCaseProfile), aTaxpayerCaseProfile);
            }
        }
        return historicalTaxpayerCaseProfileStorage;
    }

    public HashMap<String, TaxcorpCaseProfile> getHistoricalTaxcorpCaseProfileStorage() {
        if ((historicalTaxcorpCaseProfileStorage == null) || (historicalTaxcorpCaseProfileStorage.isEmpty())){
            List<TaxcorpCaseProfile> aTaxcorpCaseProfileList = getTaxcorpEJB().findTaxcorpCaseProfileListByCustomerUuid(getTargetAccountProfile().getAccountEntity().getAccountUuid());
            for (TaxcorpCaseProfile aTaxcorpCaseProfile : aTaxcorpCaseProfileList){
                historicalTaxcorpCaseProfileStorage.put(aTaxcorpCaseProfile.getTaxcorpCaseEntity().getEinNumber(), aTaxcorpCaseProfile);
            }
        }
        return historicalTaxcorpCaseProfileStorage;
    }
    
    public boolean checkTaxpayerCaseRedundancy(String deadlineSSN){
        return getHistoricalTaxpayerCaseProfileStorage().containsKey(deadlineSSN);
    }
    
    public boolean checkTaxcorpCaseRedundancy(String einNumber){
        return getHistoricalTaxcorpCaseProfileStorage().containsKey(einNumber);
    }

    @Override
    public List<TaxpayerCaseProfile> getHistoricalTaxpayerCaseProfileList() {
        List<TaxpayerCaseProfile> result = new ArrayList<>(getHistoricalTaxpayerCaseProfileStorage().values());
        Collections.sort(result, (TaxpayerCaseProfile o1, TaxpayerCaseProfile o2) -> o1.getTaxpayerCaseEntity().getDeadline().compareTo(o2.getTaxpayerCaseEntity().getDeadline())*(-1));
        return result;
    }
    
    /**
     * Only retrieve the latest year's taxpayer cases for the customer
     * @return 
     */
    public List<TaxpayerCaseProfile> getCustomerLatestYearTaxpayerCaseProfileList(){
        List<TaxpayerCaseProfile> aTaxpayerCaseProfileList = getHistoricalTaxpayerCaseProfileList();
        
        List<TaxpayerCaseProfile> result = new ArrayList<>();
        GregorianCalendar deadline = new GregorianCalendar();
        Integer latestYear = null;
        for (TaxpayerCaseProfile aTaxpayerCaseProfile : aTaxpayerCaseProfileList){
            deadline.setTime(aTaxpayerCaseProfile.getTaxpayerCaseEntity().getDeadline());
            if (latestYear == null){  //only hold the top one which is "latest year"
                latestYear = deadline.get(Calendar.YEAR);
            }
            if (deadline.get(Calendar.YEAR) == latestYear){
                result.add(aTaxpayerCaseProfile);
            }else{
                break;
            }
        }
        return result;
    }

    @Override
    public List<TaxcorpCaseProfile> getHistoricalTaxcorpCaseProfileList() {
        List<TaxcorpCaseProfile> result = new ArrayList<>(getHistoricalTaxcorpCaseProfileStorage().values());
        Collections.sort(result, (TaxcorpCaseProfile o1, TaxcorpCaseProfile o2) -> o1.getTaxcorpCaseEntity().getUpdated().compareTo(o2.getTaxcorpCaseEntity().getUpdated())*(-1));
        return result;
    }
    
    public void cloneTaxpayerCases(){
        List<G01User> customers = super.getBusinessEJB().findAllCustomerUsers();
        System.out.println("Total customers: " + customers.size());
        List<TaxpayerCaseProfile> aTaxpayerCaseProfileList;
        int count = 0;
        TaxpayerCaseProfile oldTaxpayerCaseProfile;
        TaxpayerCaseProfile newTaxpayerCaseProfile;
        for (G01User customer : customers){
            aTaxpayerCaseProfileList = preprocessDirtyDataForClone(getTaxpayerEJB().findTaxpayerCaseProfileListByCustomerUuid(customer.getUserUuid()));
            if (!aTaxpayerCaseProfileList.isEmpty()){
                Collections.sort(aTaxpayerCaseProfileList, (TaxpayerCaseProfile o1, TaxpayerCaseProfile o2) -> o1.getTaxpayerCaseEntity().getDeadline().compareTo(o2.getTaxpayerCaseEntity().getDeadline())*(-1));
                System.out.println(">>> ("+(++count)+") clone data for customer SSN: "+ customer.getSsn());
                oldTaxpayerCaseProfile = aTaxpayerCaseProfileList.get(0);
                newTaxpayerCaseProfile = new TaxpayerCaseProfile();
                //clone
                newTaxpayerCaseProfile.cloneTaxpayerCaseProfile(oldTaxpayerCaseProfile);
                newTaxpayerCaseProfile.setSpouseRequired(oldTaxpayerCaseProfile.isSpouseRequired());
                newTaxpayerCaseProfile.getTaxpayerCaseEntity().setDeadline(getRoseSettings().getNextIndividualTaxFilingDeadlineDate());
                //save
                try {
                    prepareTargetTaxpayerCaseProfileForPersistency(newTaxpayerCaseProfile);
                    getTaxpayerEJB().storeTargetTaxpayerCaseProfile(newTaxpayerCaseProfile);
                    getBusinessEJB().storeBusinessCaseBillProfileList(newTaxpayerCaseProfile.getBusinessCaseBillProfileList());
                    getBusinessEJB().storeDocumentRequirementProfileList(newTaxpayerCaseProfile.getDocumentRequirementprofileList());
                    //refresh
                    getRoseUserSession().getHistoricalTaxpayerCaseProfileStorage().clear();
                } catch (Exception ex) {
                    //RoseJsfUtils.setGlobalErrorFacesMessage(ex.getMessage());
                    System.out.println("BAD CUSTOMER: " + customer.getFirstName() + " " + customer.getLastName() 
                            + " ["+customer.getSsn()+"]" + " - " + customer.getUserUuid());
                }
            }
        }
        System.out.println("Completed clone task: taxpayers/customers: " + count + "/" + customers.size());
    }

    private List<TaxpayerCaseProfile> preprocessDirtyDataForClone(List<TaxpayerCaseProfile> aTaxpayerCaseProfileList) {
        if (aTaxpayerCaseProfileList == null){
            return new ArrayList();
        }
        Date today = new Date();
        List<TaxpayerCaseProfile> result = new ArrayList<>();
        for (TaxpayerCaseProfile aTaxpayerCaseProfile : aTaxpayerCaseProfileList){
            if ((aTaxpayerCaseProfile.getPrimaryTaxpayerProfile() != null) 
                    && (ZcaValidator.isNotNullEmpty(aTaxpayerCaseProfile.getPrimaryTaxpayerProfile().getTaxpayerInfoEntity().getTaxpayerUserUuid())))
            {
                if (today.before(aTaxpayerCaseProfile.getTaxpayerCaseEntity().getDeadline())){  //found 2019 case which has been added
                    result = new ArrayList<>();
                    break;
                }else{
                    result.add(aTaxpayerCaseProfile);
                }
            }
        }
        return result;
    }
    
    public void prepareTargetTaxpayerCaseProfileForPersistency(TaxpayerCaseProfile newTaxpayerCaseProfile) throws Exception {
        //G01TaxpayerCase
        prepareTaxpayerCaseEntityForPersistency(newTaxpayerCaseProfile);
        //G01TaxpayerInfo
        prepareTaxpayerProfileForPersistency(newTaxpayerCaseProfile, newTaxpayerCaseProfile.getPrimaryTaxpayerProfile(), TaxpayerRelationship.PRIMARY_TAXPAYER);
        if (newTaxpayerCaseProfile.isSpouseRequired()){
            prepareTaxpayerProfileForPersistency(newTaxpayerCaseProfile, newTaxpayerCaseProfile.getSpouseProfile(), TaxpayerRelationship.SPOUSE_TAXPAYER);
        }else{
            newTaxpayerCaseProfile.setSpouseProfile(new TaxpayerInfoProfile());
        }
        List<TaxpayerInfoProfile> aTaxpayerInfoProfileList = newTaxpayerCaseProfile.getDependantProfileList();
        for (TaxpayerInfoProfile aTaxpayerInfoProfile : aTaxpayerInfoProfileList){
            prepareTaxpayerProfileForPersistency(newTaxpayerCaseProfile, aTaxpayerInfoProfile, TaxpayerRelationship.convertEnumValueToType(aTaxpayerInfoProfile.getTaxpayerInfoEntity().getRelationships()));
        }
        //G01Location
        preparePrimaryLocationProfileForPersistency(newTaxpayerCaseProfile);
//        //G01ContactInfo
//        preparePrimaryContactInfoProfileForPersistency();
        //G01PersonalProperty
        List<PersonalPropertyProfile> aPersonalPropertyProfileList = newTaxpayerCaseProfile.getPersonalPropertyProfileList();
        for (PersonalPropertyProfile aPersonalPropertyProfile : aPersonalPropertyProfileList){
            preparePersonalPropertyProfileListForPersistency(newTaxpayerCaseProfile, aPersonalPropertyProfile);
        }
        //G01PersonalBusinessProperty
        List<PersonalBusinessPropertyProfile> aPersonalBusinessPropertyProfileList = newTaxpayerCaseProfile.getPersonalBusinessPropertyProfileList();
        for (PersonalBusinessPropertyProfile aPersonalPropertyProfile : aPersonalBusinessPropertyProfileList){
            preparePersonalPropertyBusinessProfileListForPersistency(newTaxpayerCaseProfile, aPersonalPropertyProfile);
        }
        //G01TlcLicense
        List<TlcLicenseProfile> aTlcLicenseProfileList = newTaxpayerCaseProfile.getTlcLicenseProfileList();
        for (TlcLicenseProfile aTlcLicenseProfile : aTlcLicenseProfileList){
            prepareTlcLicenseProfileListForPersistency(newTaxpayerCaseProfile, aTlcLicenseProfile);
        }
        //BusinessCaseBillProfileList
        List<BusinessCaseBillProfile> aBusinessCaseBillProfileList = newTaxpayerCaseProfile.getBusinessCaseBillProfileList();
        if (aBusinessCaseBillProfileList != null){
            G01Bill aG01Bill;
            for (BusinessCaseBillProfile aBusinessCaseBillProfile :aBusinessCaseBillProfileList){
                aBusinessCaseBillProfile.setAgent(super.getRoseUserSession().getTargetEmployeeAccountProfile());
                aG01Bill = aBusinessCaseBillProfile.getBillEntity();
                if (aG01Bill != null){
                    if (ZcaValidator.isNullEmpty(aG01Bill.getBillUuid())){
                        aG01Bill.setBillUuid(GardenData.generateUUIDString());
                    }
                    aG01Bill.setEmployeeUuid(aBusinessCaseBillProfile.getAgent().getAccountEntity().getAccountUuid());
                    aG01Bill.setEntityUuid(newTaxpayerCaseProfile.getTaxpayerCaseEntity().getTaxpayerCaseUuid());
                }
            }//for
        }
        //DocumentRequirementProfileList
        List<DocumentRequirementProfile> aDocumentRequirementProfileList = newTaxpayerCaseProfile.getDocumentRequirementprofileList();
        if (aDocumentRequirementProfileList != null){
            G01DocumentRequirement aG01DocumentRequirement;
            for (DocumentRequirementProfile aDocumentRequirementProfile :aDocumentRequirementProfileList){
                aG01DocumentRequirement = aDocumentRequirementProfile.getDocumentRequirementEntity();
                if (aG01DocumentRequirement != null){
                    if (ZcaValidator.isNullEmpty(aG01DocumentRequirement.getDocumentUuid())){
                        aG01DocumentRequirement.setDocumentUuid(GardenData.generateUUIDString());
                    }
                    aG01DocumentRequirement.setEntityUuid(newTaxpayerCaseProfile.getTaxpayerCaseEntity().getTaxpayerCaseUuid());
                }
            }
        }
        newTaxpayerCaseProfile.getTaxpayerCaseEntity().setLatestLogUuid("");
        newTaxpayerCaseProfile.setBrandNew(false);
    }

    /**
     * G01PersonalBusinessProperty
     */
    private void preparePersonalPropertyBusinessProfileListForPersistency(TaxpayerCaseProfile newTaxpayerCaseProfile, PersonalBusinessPropertyProfile aPersonalBusinessPropertyProfile) throws Exception {
        G01PersonalBusinessProperty aG01PersonalBusinessProperty = aPersonalBusinessPropertyProfile.getPersonalBusinessPropertyEntity();
        if (ZcaValidator.isNullEmpty(aG01PersonalBusinessProperty.getBusinessPropertyName())){
            //throw new Exception(RoseText.getText("BusinessPropertyName") + " - " + RoseText.getText("FieldRequired_T"));
            aG01PersonalBusinessProperty.setBusinessPropertyName("N/A");
        }
        if (ZcaValidator.isNullEmpty(aG01PersonalBusinessProperty.getPersonalBusinessPropertyUuid())){
            aG01PersonalBusinessProperty.setPersonalBusinessPropertyUuid(GardenData.generateUUIDString());
        }
        aG01PersonalBusinessProperty.setTaxpayerCaseUuid(newTaxpayerCaseProfile.getTaxpayerCaseEntity().getTaxpayerCaseUuid());
        aG01PersonalBusinessProperty.setEntityStatus(null);
    }

    /**
     * G01PersonalProperty
     */
    private void preparePersonalPropertyProfileListForPersistency(TaxpayerCaseProfile newTaxpayerCaseProfile, PersonalPropertyProfile aPersonalPropertyProfile) throws Exception {
        G01PersonalProperty aG01PersonalProperty = aPersonalPropertyProfile.getPersonalPropertyEntity();
        if (ZcaValidator.isNullEmpty(aG01PersonalProperty.getPropertyType())){
            //throw new Exception(RoseText.getText("PropertyType") + " - " + RoseText.getText("FieldRequired_T"));
            aG01PersonalProperty.setPropertyType(GardenPropertyType.RESIDENTIAL_SINGLE_FAMILY.value());
        }
        if (ZcaValidator.isNullEmpty(aG01PersonalProperty.getPersonalPropertyUuid())){
            aG01PersonalProperty.setPersonalPropertyUuid(GardenData.generateUUIDString());
        }
        aG01PersonalProperty.setTaxpayerCaseUuid(newTaxpayerCaseProfile.getTaxpayerCaseEntity().getTaxpayerCaseUuid());
        aG01PersonalProperty.setEntityStatus(null);
    }

    /**
     * G01TaxpayerCase persistency preparation
     * @throws Exception 
     */
    private void prepareTaxpayerCaseEntityForPersistency(TaxpayerCaseProfile newTaxpayerCaseProfile) throws Exception {
        G01TaxpayerCase aG01TaxpayerCase = newTaxpayerCaseProfile.getTaxpayerCaseEntity();
        aG01TaxpayerCase.setAgreementUuid(GardenAgreement.TaxpayerCaseAgreement.value());
        aG01TaxpayerCase.setCustomerAccountUuid(newTaxpayerCaseProfile.getCustomerProfile().getAccountEntity().getAccountUuid());
        if (aG01TaxpayerCase.getDeadline() == null){
            throw new Exception(RoseText.getText("Deadline") + " - " + RoseText.getText("FieldRequired_T"));
        }
        if (ZcaValidator.isNullEmpty(aG01TaxpayerCase.getFederalFilingStatus())){
            //throw new Exception(RoseText.getText("FederalFilingStatus") + " - " + RoseText.getText("FieldRequired_T"));
            aG01TaxpayerCase.setFederalFilingStatus(TaxpayerFederalFilingStatus.Single.value());
        }
        if (ZcaValidator.isNullEmpty(aG01TaxpayerCase.getTaxpayerCaseUuid())){
            aG01TaxpayerCase.setTaxpayerCaseUuid(GardenData.generateUUIDString());
        }
        if (ZcaValidator.isNullEmpty(aG01TaxpayerCase.getEmployeeAccountUuid())){
            if (getRoseUserSession().isEmployed()){
                aG01TaxpayerCase.setEmployeeAccountUuid(getRoseUserSession().getTargetAccountProfile().getAccountEntity().getAccountUuid());
            }else{
                aG01TaxpayerCase.setEmployeeAccountUuid(getRoseSettings().getBusinessOwnerProfile().getAccountEntity().getAccountUuid());
            }
        }
        aG01TaxpayerCase.setEntityStatus(null);
    }
    
    /**
     * G01TaxpayerInfo persistency preparation
     * @param aTaxpayerInfoProfile 
     */
    private void prepareTaxpayerProfileForPersistency(TaxpayerCaseProfile newTaxpayerCaseProfile, TaxpayerInfoProfile aTaxpayerInfoProfile, TaxpayerRelationship relationships) throws Exception {
//        if ((relationships == null) || (TaxpayerRelationship.UNKNOWN.equals(relationships))){
//            throw new Exception(RoseText.getText("Relationship") + " - " + RoseText.getText("FieldRequired_T"));
//        }
        if (relationships == null){
            relationships = TaxpayerRelationship.UNKNOWN;
        }
        G01TaxpayerInfo aG01TaxpayerInfo = aTaxpayerInfoProfile.getTaxpayerInfoEntity();
        if (ZcaValidator.isNullEmpty(aG01TaxpayerInfo.getTaxpayerUserUuid())){
            aG01TaxpayerInfo.setTaxpayerUserUuid(GardenData.generateUUIDString());
        }
        aG01TaxpayerInfo.setRelationships(relationships.value());
        aG01TaxpayerInfo.setTaxpayerCaseUuid(newTaxpayerCaseProfile.getTaxpayerCaseEntity().getTaxpayerCaseUuid());
        aG01TaxpayerInfo.setEntityStatus(null);
        
        G01User aG01User = aTaxpayerInfoProfile.getRoseUserProfile().getUserEntity();
        aG01User.setEntityStatus(GardenEntityStatus.RECORDED_FOR_TAXPAYER_CASE.value());
        if (ZcaValidator.isNullEmpty(aG01User.getFirstName())){
            //throw new Exception(RoseText.getText("Taxpayer") + " " + RoseText.getText("FirstName") + " - " + RoseText.getText("FieldRequired_T"));
            aG01User.setFirstName("N/A");
        }
        if (ZcaValidator.isNullEmpty(aG01User.getLastName())){
            //throw new Exception(RoseText.getText("Taxpayer") + " " + RoseText.getText("LastName") + " - " + RoseText.getText("FieldRequired_T"));
            aG01User.setLastName("N/A");
        }
        /**
         * NOTICE: every year, a customer do tax filing. And the taxpayer's information (e.g. the part stored by G01User) 
         * might be changed. So, it demands a copy of user information every year. It is NOT necessary to check redundancy 
         * here as long as, in the current year, the primary taxpayer is not rededunant, which is done by checkCaseUniqueness
         */
        aG01User.setUserUuid(aG01TaxpayerInfo.getTaxpayerUserUuid());
    }

    /**
     * G01Location
     */
    private void preparePrimaryLocationProfileForPersistency(TaxpayerCaseProfile newTaxpayerCaseProfile) {
        G01Location aG01Location = newTaxpayerCaseProfile.getPrimaryLocationProfile().getLocationEntity();
        if (ZcaValidator.isNullEmpty(aG01Location.getLocationUuid())){
            aG01Location.setLocationUuid(GardenData.generateUUIDString());
        }
        aG01Location.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
        aG01Location.setEntityUuid(newTaxpayerCaseProfile.getTaxpayerCaseEntity().getTaxpayerCaseUuid());
        aG01Location.setEntityStatus(null);
    }

    /**
     * G01TlcLicense
     */
    private void prepareTlcLicenseProfileListForPersistency(TaxpayerCaseProfile newTaxpayerCaseProfile, TlcLicenseProfile aTlcLicenseProfile) {
        G01TlcLicense aG01TlcLicense = aTlcLicenseProfile.getTlcLicenseEntity();
        if (ZcaValidator.isNullEmpty(aG01TlcLicense.getDriverUuid())){
            aG01TlcLicense.setDriverUuid(GardenData.generateUUIDString());
        }
        aG01TlcLicense.setTaxpayerCaseUuid(newTaxpayerCaseProfile.getTaxpayerCaseEntity().getTaxpayerCaseUuid());
        aG01TlcLicense.setEntityStatus(null);
    }
}
