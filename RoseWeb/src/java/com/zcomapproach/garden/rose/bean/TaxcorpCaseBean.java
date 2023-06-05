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
import com.zcomapproach.garden.persistence.G01EntityValidator;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G01ContactInfo;
import com.zcomapproach.garden.persistence.entity.G01Location;
import com.zcomapproach.garden.persistence.entity.G01TaxcorpCase;
import com.zcomapproach.garden.persistence.entity.G01TaxcorpRepresentative;
import com.zcomapproach.garden.persistence.entity.G01TaxcorpRepresentativePK;
import com.zcomapproach.garden.persistence.entity.G01User;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.garden.rose.RosePageName;
import com.zcomapproach.garden.rose.data.RoseWebParamValue;
import com.zcomapproach.garden.rose.data.profile.RoseAccountProfile;
import com.zcomapproach.garden.rose.data.profile.RoseContactInfoProfile;
import com.zcomapproach.garden.rose.data.profile.RoseLocationProfile;
import com.zcomapproach.garden.rose.data.profile.TaxcorpCaseProfile;
import com.zcomapproach.garden.rose.data.profile.TaxcorpRepresentativeProfile;
import com.zcomapproach.garden.rose.util.RoseDataAgent;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.garden.rose.util.RoseWebUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author zhijun98
 */
@Named(value = "taxcorpCaseBean")
@ViewScoped
public class TaxcorpCaseBean extends TaxcorpCaseViewBean{
    
    @Override
    public void populateAccountProfileAsContactor(){
        populateAccountProfileAsContactorHelper(this.getRoseUserSession().getTargetAccountProfile());
    }
    
    protected void populateAccountProfileAsContactorHelper(RoseAccountProfile aRoseAccountProfile){
        TaxcorpCaseProfile aTaxcorpCaseProfile = getTargetTaxcorpCaseProfile();
        TaxcorpRepresentativeProfile aTaxcorpRepresentativeProfile = aTaxcorpCaseProfile.addNewTaxcorpRepresentativeProfileDataEntry();
        RoseDataAgent.populateUserProfile(aTaxcorpRepresentativeProfile, aRoseAccountProfile.getUserProfile());
    }
    
    public String deleteTargetTaxcorpCaseProfile(){
        deleteTargetTaxcorpCaseProfile(getTargetTaxcorpCaseProfile().getTaxcorpCaseEntity().getTaxcorpCaseUuid());
        return RosePageName.BusinessHomePage.name() + RoseWebUtils.constructWebQueryString(null, true);
    }
    
    @Override
    public void saveTargetTaxcorpCaseBasicProfile(){
        if (checkTargetTaxcorpCaseUniqueness(getTargetTaxcorpCaseProfile().getTaxcorpCaseEntity())){
            return;
        }
        try {
            prepareTaxcorpCaseEntityForPersistency();
            
            //validateProfile
            G01EntityValidator aGardenEntityValidator = G01EntityValidator.getSingleton();
            aGardenEntityValidator.validate(getTargetTaxcorpCaseProfile().getTaxcorpCaseEntity());
            
            getTaxcorpEJB().storeEntityByUuid(G01TaxcorpCase.class, getTargetTaxcorpCaseProfile().getTaxcorpCaseEntity(), 
                    getTargetTaxcorpCaseProfile().getTaxcorpCaseEntity().getTaxcorpCaseUuid(), G01DataUpdaterFactory.getSingleton().getG01TaxcorpCaseUpdater());
            
            //refresh
            getRoseUserSession().getHistoricalTaxcorpCaseProfileStorage().clear();
        } catch (Exception ex) {
            RoseJsfUtils.setGlobalFailedOperationMessage(ex.getMessage());
            return;
        }
        setRequestedViewPurpose(RoseWebParamValue.UPDATE_EXISTING_ENTITY.value());
        
        //getTargetTaxpayerCaseProfile().
        RoseJsfUtils.setGlobalSuccessfulOperationMessage();
    }
    
    @Override
    public void saveTaxcorpRepresentativeProfileList(){
        try {
            prepareTaxcorpCaseContactorsForPersistency();
            
            //validateProfile
            validateTaxcorpRepresentativeProfileList(getTargetTaxcorpCaseProfile().getTaxcorpRepresentativeProfileList());
            
            getTaxcorpEJB().storeTaxcorpRepresentativeProfileList(getTargetTaxcorpCaseProfile().getTaxcorpCaseEntity().getTaxcorpCaseUuid(),
                                                                  getTargetTaxcorpCaseProfile().getTaxcorpRepresentativeProfileList());
            //refresh
            getRoseUserSession().getHistoricalTaxcorpCaseProfileStorage().clear();
        } catch (Exception ex) {
            RoseJsfUtils.setGlobalFailedOperationMessage(ex.getMessage());
            return;
        }
        setRequestedViewPurpose(RoseWebParamValue.UPDATE_EXISTING_ENTITY.value());
        
        //getTargetTaxpayerCaseProfile().
        RoseJsfUtils.setGlobalSuccessfulOperationMessage();
    
    }

    private void validateTaxcorpRepresentativeProfileList(List<TaxcorpRepresentativeProfile> aTaxcorpRepresentativeProfileList) throws ZcaEntityValidationException {
        G01EntityValidator aGardenEntityValidator = G01EntityValidator.getSingleton();
        for (TaxcorpRepresentativeProfile aTaxcorpRepresentativeProfile : aTaxcorpRepresentativeProfileList){
            aGardenEntityValidator.validate(aTaxcorpRepresentativeProfile.getTaxcorpRepresentativeEntity());
            aGardenEntityValidator.validate(aTaxcorpRepresentativeProfile.getUserEntity());
            List<RoseContactInfoProfile> aRoseContactInfoProfileList = aTaxcorpRepresentativeProfile.getUserContactInfoProfileList();
            for (RoseContactInfoProfile aRoseContactInfoProfile : aRoseContactInfoProfileList){
                aGardenEntityValidator.validate(aRoseContactInfoProfile.getContactInfoEntity());
            }
            List<RoseLocationProfile> aRoseLocationProfileList = aTaxcorpRepresentativeProfile.getUserLocationProfileList();
            for (RoseLocationProfile aRoseLocationProfile : aRoseLocationProfileList){
                aGardenEntityValidator.validate(aRoseLocationProfile.getLocationEntity());
            }
        }
    }
    
    /**
     * This method stores entire information of a taxcorp which includes taxcorp basic information and its contactors
     * @deprecated - replaced by saveTargetTaxcorpCaseBasicProfile and saveTaxcorpRepresentativeProfileList
     */
    @Override
    public void storeTargetTaxcorpCaseProfile(){
        if (checkTargetTaxcorpCaseUniqueness(getTargetTaxcorpCaseProfile().getTaxcorpCaseEntity())){
            return;
        }
        try {
            prepareTaxcorpCaseEntityForPersistency();
            prepareTaxcorpCaseContactorsForPersistency();
            
            //validateProfile
            G01EntityValidator aGardenEntityValidator = G01EntityValidator.getSingleton();
            aGardenEntityValidator.validate(getTargetTaxcorpCaseProfile().getTaxcorpCaseEntity());
            
            getTaxcorpEJB().storeEntityByUuid(G01TaxcorpCase.class, getTargetTaxcorpCaseProfile().getTaxcorpCaseEntity(), 
                    getTargetTaxcorpCaseProfile().getTaxcorpCaseEntity().getTaxcorpCaseUuid(), G01DataUpdaterFactory.getSingleton().getG01TaxcorpCaseUpdater());
            
            //refresh
            getRoseUserSession().getHistoricalTaxcorpCaseProfileStorage().clear();
        } catch (Exception ex) {
            RoseJsfUtils.setGlobalFailedOperationMessage(ex.getMessage());
            return;
        }
        setRequestedViewPurpose(RoseWebParamValue.UPDATE_EXISTING_ENTITY.value());
        
        //getTargetTaxpayerCaseProfile().
        RoseJsfUtils.setGlobalSuccessfulOperationMessage();
    }

    private void prepareTaxcorpCaseEntityForPersistency() {
        G01TaxcorpCase aG01TaxcorpCase = getTargetTaxcorpCaseProfile().getTaxcorpCaseEntity();
        if (ZcaValidator.isNullEmpty(aG01TaxcorpCase.getTaxcorpCaseUuid())){
            aG01TaxcorpCase.setTaxcorpCaseUuid(GardenData.generateUUIDString());
        }
        if (ZcaValidator.isNullEmpty(aG01TaxcorpCase.getEmployeeAccountUuid())){
            if (getRoseUserSession().isEmployed()){
                aG01TaxcorpCase.setEmployeeAccountUuid(getRoseUserSession().getTargetAccountProfile().getAccountEntity().getAccountUuid());
            }else{
                aG01TaxcorpCase.setEmployeeAccountUuid(getRoseSettings().getBusinessOwnerProfile().getAccountEntity().getAccountUuid());
            }
        }
        aG01TaxcorpCase.setAgreementUuid(GardenAgreement.TaxcorpCaseAgreement.value());
        aG01TaxcorpCase.setCustomerAccountUuid(getTargetTaxcorpCaseProfile().getCustomerProfile().getAccountEntity().getAccountUuid());
    }

    private void prepareTaxcorpCaseContactorsForPersistency() {
        //TaxcorpRepresentativeProfile
        List<TaxcorpRepresentativeProfile> aTaxcorpRepresentativeProfileList = getTargetTaxcorpCaseProfile().getTaxcorpRepresentativeProfileList();
        for (TaxcorpRepresentativeProfile aTaxcorpRepresentativeProfile : aTaxcorpRepresentativeProfileList){
            prepareTaxcorpHasRepresentativeEntityForPersistency(aTaxcorpRepresentativeProfile.getTaxcorpRepresentativeEntity());
            prepareRoseUserProfileForTaxcorpHasRepresentativePersistency(aTaxcorpRepresentativeProfile);
        }
    }
    
    private void prepareTaxcorpHasRepresentativeEntityForPersistency(G01TaxcorpRepresentative taxcorpHasRepresentativeEntity) {
        if (taxcorpHasRepresentativeEntity.getG01TaxcorpRepresentativePK() == null){
            taxcorpHasRepresentativeEntity.setG01TaxcorpRepresentativePK(new G01TaxcorpRepresentativePK());
        }
        G01TaxcorpRepresentativePK pkId = taxcorpHasRepresentativeEntity.getG01TaxcorpRepresentativePK();
        if (ZcaValidator.isNullEmpty(pkId.getRepresentativeUserUuid())){
            pkId.setRepresentativeUserUuid(GardenData.generateUUIDString());
        }
        pkId.setTaxcorpCaseUuid(getTargetTaxcorpCaseProfile().getTaxcorpCaseEntity().getTaxcorpCaseUuid());
    }

    private void prepareRoseUserProfileForTaxcorpHasRepresentativePersistency(TaxcorpRepresentativeProfile aTaxcorpRepresentativeProfile) 
    {
        G01User aG01User = aTaxcorpRepresentativeProfile.getUserEntity();
        aG01User.setUserUuid(aTaxcorpRepresentativeProfile.getTaxcorpRepresentativeEntity().getG01TaxcorpRepresentativePK().getRepresentativeUserUuid());
        List<RoseContactInfoProfile> aRoseContactInfoProfileList = aTaxcorpRepresentativeProfile.getUserContactInfoProfileList();
        G01ContactInfo aG01ContactInfo;
        for (RoseContactInfoProfile aRoseContactInfoProfile : aRoseContactInfoProfileList){
            aG01ContactInfo = aRoseContactInfoProfile.getContactInfoEntity();
            if (ZcaValidator.isNullEmpty(aG01ContactInfo.getContactInfoUuid())){
                aG01ContactInfo.setContactInfoUuid(GardenData.generateUUIDString());
            }
            aG01ContactInfo.setEntityType(GardenEntityType.USER.name());
            aG01ContactInfo.setEntityUuid(aG01User.getUserUuid());
        }
        List<RoseLocationProfile> aRoseLocationProfileList = aTaxcorpRepresentativeProfile.getUserLocationProfileList();
        G01Location aG01Location;
        for (RoseLocationProfile aRoseLocationProfile : aRoseLocationProfileList){
            aG01Location = aRoseLocationProfile.getLocationEntity();
            if (ZcaValidator.isNullEmpty(aG01Location.getLocationUuid())){
                aG01Location.setLocationUuid(GardenData.generateUUIDString());
            }
            aG01Location.setEntityType(GardenEntityType.USER.name());
            aG01Location.setEntityUuid(aG01User.getUserUuid());
        }
    }

}
