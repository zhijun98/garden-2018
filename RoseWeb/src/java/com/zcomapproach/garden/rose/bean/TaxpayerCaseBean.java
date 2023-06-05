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
import com.zcomapproach.garden.persistence.constant.GardenEntityStatus;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.constant.GardenPropertyType;
import com.zcomapproach.garden.persistence.constant.TaxpayerFederalFilingStatus;
import com.zcomapproach.garden.persistence.constant.TaxpayerRelationship;
import com.zcomapproach.garden.persistence.entity.G01Bill;
import com.zcomapproach.garden.persistence.entity.G01ContactInfo;
import com.zcomapproach.garden.persistence.entity.G01DocumentRequirement;
import com.zcomapproach.garden.persistence.entity.G01Location;
import com.zcomapproach.garden.persistence.entity.G01PersonalBusinessProperty;
import com.zcomapproach.garden.persistence.entity.G01PersonalProperty;
import com.zcomapproach.garden.persistence.entity.G01TaxpayerCase;
import com.zcomapproach.garden.persistence.entity.G01TaxpayerInfo;
import com.zcomapproach.garden.persistence.entity.G01TlcLicense;
import com.zcomapproach.garden.persistence.entity.G01User;
import com.zcomapproach.garden.rose.RosePageName;
import com.zcomapproach.garden.rose.data.RoseWebParamValue;
import com.zcomapproach.garden.rose.data.profile.BusinessCaseBillProfile;
import com.zcomapproach.garden.rose.data.profile.DocumentRequirementProfile;
import com.zcomapproach.garden.rose.data.profile.PersonalBusinessPropertyProfile;
import com.zcomapproach.garden.rose.data.profile.PersonalPropertyProfile;
import com.zcomapproach.garden.rose.data.profile.TaxpayerInfoProfile;
import com.zcomapproach.garden.rose.data.profile.TlcLicenseProfile;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.garden.rose.util.RoseWebUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import java.util.List;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author zhijun98
 */
@Named(value = "taxpayerCaseBean")
@ViewScoped
public class TaxpayerCaseBean extends TaxpayerCaseViewBean {
    
    public String deleteTargetTaxpayerCaseProfile(){
        deleteTargetTaxpayerCaseProfile(getTargetTaxpayerCaseProfile().getTaxpayerCaseEntity().getTaxpayerCaseUuid());
        return RosePageName.BusinessHomePage.name() + RoseWebUtils.constructWebQueryString(null, true);
    }
    
    @Override
    public String storeTargetTaxpayerCaseProfile(){
        if (checkCaseUniquenessHelper()){
            return null;
        }
        try {
            prepareTargetTaxpayerCaseProfileForPersistency();
            getTaxpayerEJB().storeTargetTaxpayerCaseProfile(getTargetTaxpayerCaseProfile());
            getBusinessEJB().storeBusinessCaseBillProfileList(getTargetTaxpayerCaseProfile().getBusinessCaseBillProfileList());
            getBusinessEJB().storeDocumentRequirementProfileList(getTargetTaxpayerCaseProfile().getDocumentRequirementprofileList());
            //refresh
            getRoseUserSession().getHistoricalTaxpayerCaseProfileStorage().clear();
        } catch (Exception ex) {
            RoseJsfUtils.setGlobalErrorFacesMessage(ex.getMessage());
            return null;
        }
        setRequestedViewPurpose(RoseWebParamValue.UPDATE_EXISTING_ENTITY.value());
        
        //getTargetTaxpayerCaseProfile().
        RoseJsfUtils.setGlobalSuccessfulOperationMessage();
        return null;
    }

    public void prepareTargetTaxpayerCaseProfileForPersistency() throws Exception {
        //G01TaxpayerCase
        prepareTaxpayerCaseEntityForPersistency();
        //G01TaxpayerInfo
        prepareTaxpayerProfileForPersistency(getTargetTaxpayerCaseProfile().getPrimaryTaxpayerProfile(), TaxpayerRelationship.PRIMARY_TAXPAYER);
        if (getTargetTaxpayerCaseProfile().isSpouseRequired()){
            prepareTaxpayerProfileForPersistency(getTargetTaxpayerCaseProfile().getSpouseProfile(), TaxpayerRelationship.SPOUSE_TAXPAYER);
        }else{
            getTargetTaxpayerCaseProfile().setSpouseProfile(new TaxpayerInfoProfile());
        }
        List<TaxpayerInfoProfile> aTaxpayerInfoProfileList = getTargetTaxpayerCaseProfile().getDependantProfileList();
        for (TaxpayerInfoProfile aTaxpayerInfoProfile : aTaxpayerInfoProfileList){
            prepareTaxpayerProfileForPersistency(aTaxpayerInfoProfile, TaxpayerRelationship.convertEnumValueToType(aTaxpayerInfoProfile.getTaxpayerInfoEntity().getRelationships()));
        }
        //G01Location
        preparePrimaryLocationProfileForPersistency();
//        //G01ContactInfo
//        preparePrimaryContactInfoProfileForPersistency();
        //G01PersonalProperty
        List<PersonalPropertyProfile> aPersonalPropertyProfileList = getTargetTaxpayerCaseProfile().getPersonalPropertyProfileList();
        for (PersonalPropertyProfile aPersonalPropertyProfile : aPersonalPropertyProfileList){
            preparePersonalPropertyProfileListForPersistency(aPersonalPropertyProfile);
        }
        //G01PersonalBusinessProperty
        List<PersonalBusinessPropertyProfile> aPersonalBusinessPropertyProfileList = getTargetTaxpayerCaseProfile().getPersonalBusinessPropertyProfileList();
        for (PersonalBusinessPropertyProfile aPersonalPropertyProfile : aPersonalBusinessPropertyProfileList){
            preparePersonalPropertyBusinessProfileListForPersistency(aPersonalPropertyProfile);
        }
        //G01TlcLicense
        List<TlcLicenseProfile> aTlcLicenseProfileList = getTargetTaxpayerCaseProfile().getTlcLicenseProfileList();
        for (TlcLicenseProfile aTlcLicenseProfile : aTlcLicenseProfileList){
            prepareTlcLicenseProfileListForPersistency(aTlcLicenseProfile);
        }
        //BusinessCaseBillProfileList
        List<BusinessCaseBillProfile> aBusinessCaseBillProfileList = getTargetTaxpayerCaseProfile().getBusinessCaseBillProfileList();
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
                    aG01Bill.setEntityUuid(getTargetTaxpayerCaseProfile().getTaxpayerCaseEntity().getTaxpayerCaseUuid());
                }
            }//for
        }
        //DocumentRequirementProfileList
        List<DocumentRequirementProfile> aDocumentRequirementProfileList = getTargetTaxpayerCaseProfile().getDocumentRequirementprofileList();
        if (aDocumentRequirementProfileList != null){
            G01DocumentRequirement aG01DocumentRequirement;
            for (DocumentRequirementProfile aDocumentRequirementProfile :aDocumentRequirementProfileList){
                aG01DocumentRequirement = aDocumentRequirementProfile.getDocumentRequirementEntity();
                if (aG01DocumentRequirement != null){
                    if (ZcaValidator.isNullEmpty(aG01DocumentRequirement.getDocumentUuid())){
                        aG01DocumentRequirement.setDocumentUuid(GardenData.generateUUIDString());
                    }
                    aG01DocumentRequirement.setEntityUuid(getTargetTaxpayerCaseProfile().getTaxpayerCaseEntity().getTaxpayerCaseUuid());
                }
            }
        }
        
        getTargetTaxpayerCaseProfile().setBrandNew(false);
    }

    /**
     * G01TaxpayerInfo persistency preparation
     * @param aTaxpayerInfoProfile 
     */
    private void prepareTaxpayerProfileForPersistency(TaxpayerInfoProfile aTaxpayerInfoProfile, TaxpayerRelationship relationships) throws Exception {
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
        aG01TaxpayerInfo.setTaxpayerCaseUuid(getTargetTaxpayerCaseProfile().getTaxpayerCaseEntity().getTaxpayerCaseUuid());
        aG01TaxpayerInfo.setEntityStatus(null);
        
        G01User aG01User = aTaxpayerInfoProfile.getRoseUserProfile().getUserEntity();
        aG01User.setEntityStatus(GardenEntityStatus.RECORDED_FOR_TAXPAYER_CASE.value());
        if (ZcaValidator.isNullEmpty(aG01User.getFirstName())){
            aG01User.setFirstName("N/A");
            //throw new Exception(RoseText.getText("Taxpayer") + " " + RoseText.getText("FirstName") + " - " + RoseText.getText("FieldRequired_T"));
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
     * G01TaxpayerCase persistency preparation
     * @throws Exception 
     */
    private void prepareTaxpayerCaseEntityForPersistency() throws Exception {
        G01TaxpayerCase aG01TaxpayerCase = getTargetTaxpayerCaseProfile().getTaxpayerCaseEntity();
        aG01TaxpayerCase.setAgreementUuid(GardenAgreement.TaxpayerCaseAgreement.value());
        aG01TaxpayerCase.setCustomerAccountUuid(getTargetTaxpayerCaseProfile().getCustomerProfile().getAccountEntity().getAccountUuid());
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
     * G01Location
     */
    private void preparePrimaryLocationProfileForPersistency() {
        G01Location aG01Location = getTargetTaxpayerCaseProfile().getPrimaryLocationProfile().getLocationEntity();
        if (ZcaValidator.isNullEmpty(aG01Location.getLocationUuid())){
            aG01Location.setLocationUuid(GardenData.generateUUIDString());
        }
        aG01Location.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
        aG01Location.setEntityUuid(getTargetTaxpayerCaseProfile().getTaxpayerCaseEntity().getTaxpayerCaseUuid());
        aG01Location.setEntityStatus(null);
    }

    /**
     * G01ContactInfo
     */
    private void preparePrimaryContactInfoProfileForPersistency() throws Exception {
        G01ContactInfo aG01ContactInfo = getTargetTaxpayerCaseProfile().getPrimaryContactInfoProfile().getContactInfoEntity();
        if (ZcaValidator.isNullEmpty(aG01ContactInfo.getContactInfoUuid())){
            aG01ContactInfo.setContactInfoUuid(GardenData.generateUUIDString());
        }
        if (ZcaValidator.isNullEmpty(aG01ContactInfo.getContactType())){
            throw new Exception(RoseText.getText("ContactMethod") + " - " + RoseText.getText("FieldRequired_T"));
        }
        if (ZcaValidator.isNullEmpty(aG01ContactInfo.getContactInfo())){
            throw new Exception(RoseText.getText("ContactInfoData") + " - " + RoseText.getText("FieldRequired_T"));
        }
        aG01ContactInfo.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
        aG01ContactInfo.setEntityUuid(getTargetTaxpayerCaseProfile().getTaxpayerCaseEntity().getTaxpayerCaseUuid());
        aG01ContactInfo.setEntityStatus(null);
    }

    /**
     * G01PersonalProperty
     */
    private void preparePersonalPropertyProfileListForPersistency(PersonalPropertyProfile aPersonalPropertyProfile) throws Exception {
        G01PersonalProperty aG01PersonalProperty = aPersonalPropertyProfile.getPersonalPropertyEntity();
        if (ZcaValidator.isNullEmpty(aG01PersonalProperty.getPropertyType())){
            //throw new Exception(RoseText.getText("PropertyType") + " - " + RoseText.getText("FieldRequired_T"));
            aG01PersonalProperty.setPropertyType(GardenPropertyType.RESIDENTIAL_SINGLE_FAMILY.value());
        }
        if (ZcaValidator.isNullEmpty(aG01PersonalProperty.getPersonalPropertyUuid())){
            aG01PersonalProperty.setPersonalPropertyUuid(GardenData.generateUUIDString());
        }
        aG01PersonalProperty.setTaxpayerCaseUuid(getTargetTaxpayerCaseProfile().getTaxpayerCaseEntity().getTaxpayerCaseUuid());
        aG01PersonalProperty.setEntityStatus(null);
    }

    /**
     * G01PersonalBusinessProperty
     */
    private void preparePersonalPropertyBusinessProfileListForPersistency(PersonalBusinessPropertyProfile aPersonalBusinessPropertyProfile) throws Exception {
        G01PersonalBusinessProperty aG01PersonalBusinessProperty = aPersonalBusinessPropertyProfile.getPersonalBusinessPropertyEntity();
        if (ZcaValidator.isNullEmpty(aG01PersonalBusinessProperty.getBusinessPropertyName())){
            //throw new Exception(RoseText.getText("BusinessPropertyName") + " - " + RoseText.getText("FieldRequired_T"));
            aG01PersonalBusinessProperty.setBusinessPropertyName("N/A");
        }
        if (ZcaValidator.isNullEmpty(aG01PersonalBusinessProperty.getPersonalBusinessPropertyUuid())){
            aG01PersonalBusinessProperty.setPersonalBusinessPropertyUuid(GardenData.generateUUIDString());
        }
        aG01PersonalBusinessProperty.setTaxpayerCaseUuid(getTargetTaxpayerCaseProfile().getTaxpayerCaseEntity().getTaxpayerCaseUuid());
        aG01PersonalBusinessProperty.setEntityStatus(null);
    }

    /**
     * G01TlcLicense
     */
    private void prepareTlcLicenseProfileListForPersistency(TlcLicenseProfile aTlcLicenseProfile) {
        G01TlcLicense aG01TlcLicense = aTlcLicenseProfile.getTlcLicenseEntity();
        if (ZcaValidator.isNullEmpty(aG01TlcLicense.getDriverUuid())){
            aG01TlcLicense.setDriverUuid(GardenData.generateUUIDString());
        }
        aG01TlcLicense.setTaxpayerCaseUuid(getTargetTaxpayerCaseProfile().getTaxpayerCaseEntity().getTaxpayerCaseUuid());
        aG01TlcLicense.setEntityStatus(null);
    }
}
