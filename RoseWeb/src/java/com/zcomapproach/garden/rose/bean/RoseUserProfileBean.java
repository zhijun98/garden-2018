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

import com.zcomapproach.commons.ZcaText;
import com.zcomapproach.garden.exception.BadEntityException;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.persistence.constant.GardenEntityStatus;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G01Account;
import com.zcomapproach.garden.persistence.entity.G01ContactInfo;
import com.zcomapproach.garden.persistence.entity.G01Location;
import com.zcomapproach.garden.persistence.entity.G01User;
import com.zcomapproach.garden.rose.RosePageName;
import com.zcomapproach.garden.rose.bean.state.RedundantUserProfileFoundState;
import com.zcomapproach.garden.rose.data.RoseWebParamValue;
import com.zcomapproach.garden.rose.data.profile.DocumentRequirementProfile;
import com.zcomapproach.garden.rose.data.profile.IRoseAccountUserEntityProfile;
import com.zcomapproach.garden.rose.data.profile.RedundantUserProfile;
import com.zcomapproach.garden.rose.data.profile.RoseArchivedFileTypeProfile;
import com.zcomapproach.garden.rose.data.profile.RoseContactInfoProfile;
import com.zcomapproach.garden.rose.data.profile.RoseLocationProfile;
import com.zcomapproach.garden.rose.data.profile.RoseUserProfile;
import com.zcomapproach.garden.rose.data.profile.TaxcorpCaseProfile;
import com.zcomapproach.garden.rose.data.profile.TaxpayerCaseProfile;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseDataAgent;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.garden.rose.util.RoseWebUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author zhijun98
 */
@Named(value = "userProfileBean")
@ViewScoped
public class RoseUserProfileBean extends RoseDocumentEmailBean implements IRosePersonalProfileBean, IRedundantUserProfileFoundBean{
    
    //request parameter value
    private String requestUserUuid;
    
    private RoseUserProfile targetUserProfile;
    
    private final RedundantUserProfileFoundState redundantUserProfileFoundState;
    
    private boolean userProfileUpdateDemanded;
    
    public RoseUserProfileBean() {
        this.redundantUserProfileFoundState = new RedundantUserProfileFoundState();
    }

    @Override
    public boolean isUserProfileUpdateDemanded() {
        return userProfileUpdateDemanded;
    }

    public void setUserProfileUpdateDemanded(boolean userProfileUpdateDemanded) {
        this.userProfileUpdateDemanded = userProfileUpdateDemanded;
    }

    @Override
    public void openUserProfileDataEntry() {
        setUserProfileUpdateDemanded(true);
    }

    @Override
    public void closeUserProfileDataEntry() {
        setUserProfileUpdateDemanded(false);
    }

    @Override
    public String getCurrentUserUuidForMerging() {
        return ZcaText.denullize(getTargetUserProfile().getUserEntity().getUserUuid());
    }
    
    public G01Account getTargetUserAccountEntity(){
        G01Account aG01Account = getTargetUserProfile().getAccountEntity();
        if (aG01Account == null){
            aG01Account = getBusinessEJB().findEntityByUuid(G01Account.class, getTargetUserProfile().getUserEntity().getUserUuid());
            getTargetUserProfile().setAccountEntity(aG01Account);
        }
        return aG01Account;
    }
    
    public List<TaxpayerCaseProfile> getHistoricalInvolvedTaxpayerCaseProfileList(){
        List<TaxpayerCaseProfile> result = getTaxpayerEJB().findInvolvedTaxpayerCaseProfileListByUserUuid(getTargetUserProfile().getUserEntity().getUserUuid());
        if (!result.isEmpty()){
            Collections.sort(result, (TaxpayerCaseProfile o1, TaxpayerCaseProfile o2) -> o1.getTaxpayerCaseEntity().getDeadline().compareTo(o2.getTaxpayerCaseEntity().getDeadline())*(-1));
        }
        
        return result;
    }

    public List<TaxpayerCaseProfile> getHistoricalTaxpayerCaseProfileList() {
        List<TaxpayerCaseProfile> result = getTaxpayerEJB().findTaxpayerCaseProfileListByCustomerUuid(getTargetUserProfile().getUserEntity().getUserUuid());
        if (!result.isEmpty()){
            Collections.sort(result, (TaxpayerCaseProfile o1, TaxpayerCaseProfile o2) -> o1.getTaxpayerCaseEntity().getDeadline().compareTo(o2.getTaxpayerCaseEntity().getDeadline())*(-1));
        }
        return result;
    }

    public List<TaxcorpCaseProfile> getHistoricalInvolvedTaxcorpCaseProfileList() {
        List<TaxcorpCaseProfile> result = getTaxcorpEJB().findTaxcorpCaseProfileListByContactorUuid(getTargetUserProfile().getUserEntity().getUserUuid());
        if (!result.isEmpty()){
            Collections.sort(result, (TaxcorpCaseProfile o1, TaxcorpCaseProfile o2) -> o1.getTaxcorpCaseEntity().getUpdated().compareTo(o2.getTaxcorpCaseEntity().getUpdated())*(-1));
        }
        return result;
    }

    public List<TaxcorpCaseProfile> getHistoricalTaxcorpCaseProfileList() {
        List<TaxcorpCaseProfile> result = getTaxcorpEJB().findTaxcorpCaseProfileListByCustomerUuid(getTargetUserProfile().getUserEntity().getUserUuid());
        if (!result.isEmpty()){
            Collections.sort(result, (TaxcorpCaseProfile o1, TaxcorpCaseProfile o2) -> o1.getTaxcorpCaseEntity().getUpdated().compareTo(o2.getTaxcorpCaseEntity().getUpdated())*(-1));
        }
        return result;
    }

    @Override
    public void storeDocumentRequirementProfile(String documentUuid) {
    }

    @Override
    public void deleteDocumentRequirementProfile(String documentUuid) {
    }

    @Override
    public void storeTargetDocumentRequirementProfile() {
    }

    @Override
    public List<DocumentRequirementProfile> getAllDocumentRequirementProfileList() {
        return new ArrayList<>();
    }

    @Override
    public List<RoseArchivedFileTypeProfile> getRoseArchivedFileTypeProfileList() {
        return RoseDataAgent.loadIntoRoseArchivedFileTypeProfiles(getBusinessEJB().findRoseArchivedDocumentProfileListByFeaturedField("providerUuid", 
                                                                                    getTargetUserProfile().getUserEntity().getUserUuid(), 
                                                                                    "G01ArchivedDocument.findByProviderUuid"));
    }

    @Override
    public List<DocumentRequirementProfile> getDocumentRequirementProfileList() {
        return getBusinessEJB().findRoseDocumentRequirementProfileListByFeaturedField("entityUuid", 
                                                                                    getTargetUserProfile().getUserEntity().getUserUuid(), 
                                                                                    "G01DocumentRequirement.findByEntityUuid");
    }

    @Override
    public String getTargetPersonUuid() {
        return getTargetUserProfile().getUserEntity().getUserUuid();
    }
    
    @Override
    public boolean isForCreateNewEntity() {
        return super.isForCreateNewEntity() 
                || isForTaxcorpCase()   //to create a new taxcorp case
                || isForTaxpayerCase(); //to create a new taxpayer case
    }
    
    @Override
    public boolean isForTaxcorpCase() {
        return GardenEntityType.TAXCORP_CASE.value().equalsIgnoreCase(getRequestedViewPurpose());
    }
    
    @Override
    public boolean isForTaxpayerCase() {
        return GardenEntityType.TAXPAYER_CASE.value().equalsIgnoreCase(getRequestedViewPurpose());
    }

    @Override
    public String getRosePageTopic() {
        return RoseText.getText("UserProfile");
    }

    @Override
    public String getTopicIconAwesomeName() {
        return "user";
    }

    @Override
    public GardenEntityType getEntityType() {
        return GardenEntityType.USER;
    }

    @Override
    public String getRequestedEntityUuid() {
        return getTargetUserProfile().getUserEntity().getUserUuid();
    }

    @Override
    public String getTargetReturnWebPath() {
        HashMap<String, String> params = new HashMap<>();
        params.put(getRoseParamKeys().getUserUuidParamKey(), getTargetUserProfile().getUserEntity().getUserUuid());
        return getTargetReturnWebPageName() + RoseWebUtils.constructWebQueryString(params, true);
    }

    public String getRequestUserUuid() {
        return requestUserUuid;
    }

    public void setRequestUserUuid(String requestUserUuid) {
        if (ZcaValidator.isNotNullEmpty(requestUserUuid)){
            RoseUserProfile aRoseUserProfile = getBusinessEJB().findRoseUserProfileByUserUuid(requestUserUuid);
            if (aRoseUserProfile != null){
                setTargetUserProfile(aRoseUserProfile);
                checkUserRedundancy(aRoseUserProfile);
            }
        }
        this.requestUserUuid = requestUserUuid;
    }

    @Override
    public IRoseLocationEntityEditor getLocationEntityEditor() {
        return getTargetUserProfile();
    }

    @Override
    public IRoseContactInfoEntityEditor getContactInfoEntityEditor() {
        return getTargetUserProfile();
    }

    @Override
    public RoseUserProfile getTargetUserProfile() {
        if (targetUserProfile == null){
            targetUserProfile = new RoseUserProfile();
        }
        return targetUserProfile;
    }

    public void setTargetUserProfile(RoseUserProfile targetUserProfile) {
        this.targetUserProfile = targetUserProfile;
    }
    
    @Override
    public boolean isRedundantUserProfileFound(){
        return redundantUserProfileFoundState.isRedundantRecordFound();
    }

    @Override
    public void ignoreRedundancyDuringRegistration() {
        redundantUserProfileFoundState.setIgnoreRedundancy(true);
    }
    
    @Override
    public List<RedundantUserProfile> getRedundantUserProfileList(){
        return redundantUserProfileFoundState.getRedundantUserProfileList();
    }

    @Override
    public void populateRedundantUserProfile(String userUuid) {
        if (!getRoseUserSession().isEmployed()){
            return;
        }
        this.ignoreRedundancyDuringRegistration();
        this.setRequestUserUuid(userUuid);
    }

    @Override
    public boolean isPopulatingFeatureDemanded() {
        return this.isForCreateNewEntity();
    }
    
    /**
     * Check if there are redundant user records which are similar to targetAccountUserProfile
     * @param targetAccountUserProfile
     * @return - TRUE if redundancy was found
     */
    protected boolean checkUserRedundancy(IRoseAccountUserEntityProfile targetAccountUserProfile){
        if (!getRoseUserSession().isEmployed()){
            return false;
        }
        redundantUserProfileFoundState.setTargetAccountUserProfile(targetAccountUserProfile);
        if (redundantUserProfileFoundState.isIgnoreRedundancy()){ //whether or not ignoreRedun
            redundantUserProfileFoundState.getRedundantUserProfileList().clear();
        }else{
            try {
                redundantUserProfileFoundState.setRedundantUserProfileList(getBusinessEJB().findRedundantUserProfilesBySSN(targetAccountUserProfile));
            } catch (BadEntityException ex) {
                Logger.getLogger(RoseAccountProfileBean.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return !redundantUserProfileFoundState.getRedundantUserProfileList().isEmpty();
    }
    
    public List<G01User> getUserEntityList(){
        if (RoseWebParamValue.DISPLAY_ALL_ENTITIES.value().equalsIgnoreCase(getRequestedViewPurpose())
                && getRosePrivileges().checkSuperPowerAuthorized(getRoseUserSession().getTargetAccountProfile()))
        {
            return getBusinessEJB().findAllUserEntityList();
        }else{
            return getBusinessEJB().findActiveUserEntityList();
        }
    }

    @Override
    public String requestNewCorporateTaxFiling() {
        String customerUuid = getTargetUserProfile().getUserEntity().getUserUuid();
        G01Account account = getBusinessEJB().findEntityByUuid(G01Account.class, customerUuid);
        HashMap<String, String> webParams = new HashMap<>();
        webParams.put(getRoseParamKeys().getCustomerUuidParamKey(), customerUuid);
        if (account == null){
            webParams.put(getRoseParamKeys().getViewPurposeParamKey(), getRoseParamValues().getTaxcorpCaseEntityTypeParamValue());
            return RosePageName.ClientProfilePage.name() + RoseWebUtils.constructWebQueryString(webParams, true);
        }else{
            webParams.put(getRoseParamKeys().getViewPurposeParamKey(), getRoseParamValues().getCreateNewEntityParamValue());
            return RosePageName.TaxcorpCaseMgtPage.name() + RoseWebUtils.constructWebQueryString(webParams, true);
        }
    }

    @Override
    public String requestNewIndividualTaxFiling() {
        String customerUuid = getTargetUserProfile().getUserEntity().getUserUuid();
        G01Account account = getBusinessEJB().findEntityByUuid(G01Account.class, customerUuid);
        HashMap<String, String> webParams = new HashMap<>();
        webParams.put(getRoseParamKeys().getCustomerUuidParamKey(), customerUuid);
        if (account == null){
            webParams.put(getRoseParamKeys().getViewPurposeParamKey(), getRoseParamValues().getTaxpayerCaseEntityTypeParamValue());
            return RosePageName.ClientProfilePage.name() + RoseWebUtils.constructWebQueryString(webParams, true);
        }else{
            webParams.put(getRoseParamKeys().getViewPurposeParamKey(), getRoseParamValues().getCreateNewEntityParamValue());
            return RosePageName.TaxpayerCaseMgtPage.name() + RoseWebUtils.constructWebQueryString(webParams, true);
        }
    }
    
    @Override
    public String storeTargetPersonalProfile(){
        RoseUserProfile currentTargetUserProfile = this.getTargetUserProfile();
        //check redundancy
        if (checkUserRedundancy(currentTargetUserProfile)){
            //Redundant records were found. Now, prompt "continue" or "send me the recovering code"
            return null;
        }
        
        try {

            validateUserProfile(currentTargetUserProfile);
            
            //store registration data
            getBusinessEJB().storeRoseUserProfile(currentTargetUserProfile);

            super.setRequestedViewPurpose(null);//make isForCreateNewEntity false

            RoseJsfUtils.setGlobalSuccessfulOperationMessage();

        } catch (ZcaEntityValidationException ex) {
            RoseJsfUtils.setGlobalFatalFacesMessage(ex.getMessage());
        } catch (Exception ex) {
            RoseJsfUtils.setGlobalFatalFacesMessage(RoseText.getText("SystemError") + ": " + ex.getMessage());
        }
        return null;
    }
    
    /**
     * @deprecated 
     * @param userUuid 
     */
    public void deleteUserProfile(String userUuid){
        try {
            GardenEntityStatus entityStatus;
            if (getRoseUserSession().isEmployed()){
                entityStatus = GardenEntityStatus.DELETED_BY_AGENT;
            }else{
                entityStatus = GardenEntityStatus.DELETED_BY_CUSTOMER;
            }
            if (!getRoseSettings().getBusinessOwnerProfile().getAccountEntity().getAccountUuid().equalsIgnoreCase(userUuid)){
                getBusinessEJB().deleteRoseUserProfile(userUuid, entityStatus);
            }
        } catch (Exception ex) {
            Logger.getLogger(EmployeeProfileBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * @deprecated 
     * @param userUuid
     * @return 
     */
    public String eraseUserProfile(String userUuid){
        try {
            getBusinessEJB().deleteEntityByUuid(G01User.class, userUuid);
            HashMap<String, Object> params = new HashMap<>();
            params.put("entityUuid", userUuid);
            List<G01Location> aG01LocationList = getBusinessEJB().findEntityListByNamedQuery(G01Location.class, "G01Location.findByEntityUuid", params);
            for (G01Location aG01Location : aG01LocationList){
                getBusinessEJB().deleteEntityByUuid(G01Location.class, aG01Location.getLocationUuid());
            }
            List<G01ContactInfo> aG01ContactInfoList = getBusinessEJB().findEntityListByNamedQuery(G01ContactInfo.class, "G01ContactInfo.findByEntityUuid", params);
            for (G01ContactInfo aG01ContactInfo : aG01ContactInfoList){
                getBusinessEJB().deleteEntityByUuid(G01ContactInfo.class, aG01ContactInfo.getContactInfoUuid());
            }
            return RosePageName.UserProfileListPage.name() + RoseWebUtils.constructWebQueryString(null, true);
        } catch (Exception ex) {
            Logger.getLogger(RoseUserProfileBean.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    protected void validateUserProfile(RoseUserProfile currentTargetUserProfile) throws ZcaEntityValidationException {
        //user: user uuid is the same as the account
        G01User userEntity = currentTargetUserProfile.getUserEntity();
        
        //location
        List<RoseLocationProfile> aRoseLocationProfileList = currentTargetUserProfile.getUserLocationProfileList();
        for (RoseLocationProfile aRoseLocationProfile : aRoseLocationProfileList){
            if (ZcaValidator.isNullEmpty(aRoseLocationProfile.getLocationEntity().getLocationUuid())){
                aRoseLocationProfile.getLocationEntity().setLocationUuid(GardenData.generateUUIDString());
            }
            aRoseLocationProfile.getLocationEntity().setEntityType(GardenEntityType.USER.name());
            aRoseLocationProfile.getLocationEntity().setEntityUuid(userEntity.getUserUuid());
        }
        
        //contact-info
        List<RoseContactInfoProfile> aRoseContactInfoProfileList = currentTargetUserProfile.getUserContactInfoProfileList();
        for (RoseContactInfoProfile aRoseContactInfoProfile : aRoseContactInfoProfileList){
            if (ZcaValidator.isNullEmpty(aRoseContactInfoProfile.getContactInfoEntity().getContactInfoUuid())){
                aRoseContactInfoProfile.getContactInfoEntity().setContactInfoUuid(GardenData.generateUUIDString());
            }
            aRoseContactInfoProfile.getContactInfoEntity().setEntityType(GardenEntityType.USER.name());
            aRoseContactInfoProfile.getContactInfoEntity().setEntityUuid(userEntity.getUserUuid());
        }
    }
}
