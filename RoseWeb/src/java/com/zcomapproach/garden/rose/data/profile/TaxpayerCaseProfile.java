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

package com.zcomapproach.garden.rose.data.profile;

import com.zcomapproach.garden.persistence.constant.GardenEntityStatus;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G01Bill;
import com.zcomapproach.garden.persistence.entity.G01DocumentRequirement;
import com.zcomapproach.garden.persistence.entity.G01TaxpayerCase;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.garden.util.GardenData;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author zhijun98
 */
public class TaxpayerCaseProfile extends BusinessCaseProfile{
    
    private G01TaxpayerCase taxpayerCaseEntity; //the case
    private TaxpayerInfoProfile primaryTaxpayerProfile;    //the pimary taxpayer
    private TaxpayerInfoProfile spouseProfile;
    private List<TaxpayerInfoProfile> dependantProfileList;
    private RoseLocationProfile primaryLocationProfile;
    private RoseContactInfoProfile primaryContactInfoProfile;
    private List<PersonalPropertyProfile> personalPropertyProfileList;
    private List<PersonalBusinessPropertyProfile> personalBusinessPropertyProfileList;
    private List<TlcLicenseProfile> tlcLicenseProfileList;
    
    private boolean spouseRequired; //Help GUI how to display spouse panel

    public TaxpayerCaseProfile() {
        this.taxpayerCaseEntity = new G01TaxpayerCase();
        this.primaryTaxpayerProfile = new TaxpayerInfoProfile();
        this.spouseProfile = new TaxpayerInfoProfile();
        this.dependantProfileList = new ArrayList<>();
        this.primaryLocationProfile = new RoseLocationProfile();
        this.primaryContactInfoProfile = new RoseContactInfoProfile();
        this.personalPropertyProfileList = new ArrayList<>();
        this.personalBusinessPropertyProfileList = new ArrayList<>();
        this.tlcLicenseProfileList = new ArrayList<>();
    }
    
    /**
     * Only clone TaxpayerCaseProfile itself without parent-class
     * @param srcTaxpayerCaseProfile 
     */
    public void cloneTaxpayerCaseProfile(TaxpayerCaseProfile srcTaxpayerCaseProfile){
        this.setCustomerProfile(srcTaxpayerCaseProfile.getCustomerProfile());
        //taxpayerCaseEntity
        G01DataUpdaterFactory.getSingleton().getG01TaxpayerCaseUpdater().cloneEntity(srcTaxpayerCaseProfile.getTaxpayerCaseEntity(), this.getTaxpayerCaseEntity());
        //primaryTaxpayerProfile
        getPrimaryTaxpayerProfile().cloneProfile(srcTaxpayerCaseProfile.getPrimaryTaxpayerProfile());
        //spouseProfile
        getSpouseProfile().cloneProfile(srcTaxpayerCaseProfile.getSpouseProfile());
        //dependantProfileList
        List<TaxpayerInfoProfile> srcTaxpayerInfoProfileList = srcTaxpayerCaseProfile.getDependantProfileList();
        getDependantProfileList().clear();
        TaxpayerInfoProfile aTaxpayerInfoProfile;
        for (TaxpayerInfoProfile srcTaxpayerInfoProfile : srcTaxpayerInfoProfileList){
            aTaxpayerInfoProfile = new TaxpayerInfoProfile();
            aTaxpayerInfoProfile.cloneProfile(srcTaxpayerInfoProfile);
            getDependantProfileList().add(aTaxpayerInfoProfile);
        }
        //primaryLocationProfile
        getPrimaryLocationProfile().cloneProfile(srcTaxpayerCaseProfile.getPrimaryLocationProfile());
        //primaryContactInfoProfile
        getPrimaryContactInfoProfile().cloneProfile(srcTaxpayerCaseProfile.getPrimaryContactInfoProfile());
        //personalPropertyProfileList
        List<PersonalPropertyProfile> srcPersonalPropertyProfileList = srcTaxpayerCaseProfile.getPersonalPropertyProfileList();
        getPersonalPropertyProfileList().clear();
        PersonalPropertyProfile aPersonalPropertyProfile;
        for (PersonalPropertyProfile srcPersonalPropertyProfile : srcPersonalPropertyProfileList){
            aPersonalPropertyProfile = new PersonalPropertyProfile();
            aPersonalPropertyProfile.cloneProfile(srcPersonalPropertyProfile);
            getPersonalPropertyProfileList().add(aPersonalPropertyProfile);
        }
        //personalBusinessPropertyProfileList
        List<PersonalBusinessPropertyProfile> srcPersonalBusinessPropertyProfileList = srcTaxpayerCaseProfile.getPersonalBusinessPropertyProfileList();
        getPersonalBusinessPropertyProfileList().clear();
        PersonalBusinessPropertyProfile aPersonalBusinessPropertyProfile;
        for (PersonalBusinessPropertyProfile srcPersonalBusinessPropertyProfile : srcPersonalBusinessPropertyProfileList){
            aPersonalBusinessPropertyProfile = new PersonalBusinessPropertyProfile();
            aPersonalBusinessPropertyProfile.cloneProfile(srcPersonalBusinessPropertyProfile);
            getPersonalBusinessPropertyProfileList().add(aPersonalBusinessPropertyProfile);
        }
        //tlcLicenseProfileList
        List<TlcLicenseProfile> srcTlcLicenseProfileList = srcTaxpayerCaseProfile.getTlcLicenseProfileList();
        getTlcLicenseProfileList().clear();
        TlcLicenseProfile aTlcLicenseProfile;
        for (TlcLicenseProfile srcTlcLicenseProfile : srcTlcLicenseProfileList){
            aTlcLicenseProfile = new TlcLicenseProfile();
            aTlcLicenseProfile.cloneProfile(srcTlcLicenseProfile);
            getTlcLicenseProfileList().add(aTlcLicenseProfile);
        }
        //documentRequirementprofileList
        List<DocumentRequirementProfile> srcDocumentRequirementProfileList = srcTaxpayerCaseProfile.getDocumentRequirementprofileList();
        if (srcDocumentRequirementProfileList != null){
            getDocumentRequirementprofileList().clear();
            DocumentRequirementProfile aDocumentRequirementProfile;
            G01DocumentRequirement aG01DocumentRequirement; G01DocumentRequirement srcG01DocumentRequirement;
            for (DocumentRequirementProfile srcDocumentRequirementProfile : srcDocumentRequirementProfileList){
                aDocumentRequirementProfile = new DocumentRequirementProfile();
                srcG01DocumentRequirement = srcDocumentRequirementProfile.getDocumentRequirementEntity();
                if (srcG01DocumentRequirement != null){
                    aG01DocumentRequirement = new G01DocumentRequirement();
                    aG01DocumentRequirement.setDocumentUuid(GardenData.generateUUIDString());
                    //aG01DocumentRequirement.setEntityUuid("");
                    //aG01DocumentRequirement.setEntityStatus(entityStatus);
                    //aG01DocumentRequirement.setArchivedDocumentUuid("");
                    aG01DocumentRequirement.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
                    aG01DocumentRequirement.setDescription(srcG01DocumentRequirement.getDescription());
                    aG01DocumentRequirement.setFileDemanded(srcG01DocumentRequirement.getFileDemanded());
                    aG01DocumentRequirement.setQuantity(srcG01DocumentRequirement.getQuantity());
                    aG01DocumentRequirement.setServiceTagName(srcG01DocumentRequirement.getServiceTagName());
                    aG01DocumentRequirement.setServiceTagUuid(srcG01DocumentRequirement.getServiceTagUuid());
                    aG01DocumentRequirement.setUnitPrice(srcG01DocumentRequirement.getUnitPrice());
                    aG01DocumentRequirement.setCreated(new Date());
                    aG01DocumentRequirement.setUpdated(new Date());
                    
                    aDocumentRequirementProfile.setDocumentRequirementEntity(aG01DocumentRequirement);
                }
                getDocumentRequirementprofileList().add(aDocumentRequirementProfile);
            }
        }
        //BusinessCaseBillProfileList
        List<BusinessCaseBillProfile> srcBusinessCaseBillProfileList = srcTaxpayerCaseProfile.getBusinessCaseBillProfileList();
        if (srcBusinessCaseBillProfileList != null){
            getBusinessCaseBillProfileList().clear();
            BusinessCaseBillProfile aBusinessCaseBillProfile;
            G01Bill aG01Bill; G01Bill srcG01Bill;
            for (BusinessCaseBillProfile srcBusinessCaseBillProfile :srcBusinessCaseBillProfileList){
                aBusinessCaseBillProfile = new BusinessCaseBillProfile();
                aBusinessCaseBillProfile.setAgent(srcBusinessCaseBillProfile.getAgent());
                srcG01Bill = srcBusinessCaseBillProfile.getBillEntity();
                if (srcG01Bill != null){
                    aG01Bill = new G01Bill();
                    aG01Bill.setBillUuid(GardenData.generateUUIDString());
                    aG01Bill.setBillContent(srcG01Bill.getBillContent());
                    aG01Bill.setBillDatetime(new Date());
                    aG01Bill.setBillDiscount(srcG01Bill.getBillDiscount());
                    aG01Bill.setBillDiscountType(srcG01Bill.getBillDiscountType());
                    aG01Bill.setBillStatus(srcG01Bill.getBillStatus());
                    aG01Bill.setBillTotal(srcG01Bill.getBillTotal());
                    aG01Bill.setCreated(new Date());
                    aG01Bill.setEmployeeUuid(srcG01Bill.getEmployeeUuid());
                    aG01Bill.setEntityStatus(GardenEntityStatus.RECORDED_FOR_TAXPAYER_CASE.value());
                    aG01Bill.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
                    //aG01Bill.setEntityUuid(entityUuid);
                    aBusinessCaseBillProfile.setBillEntity(aG01Bill);
                }
                getBusinessCaseBillProfileList().add(aBusinessCaseBillProfile);
            }//for
        }
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        super.cloneProfile(srcProfile);
        if (!(srcProfile instanceof TaxpayerCaseProfile)){
            return;
        }
        cloneTaxpayerCaseProfile((TaxpayerCaseProfile)srcProfile);
    }
    
    public void addNewDependantDataEntry(){
        TaxpayerInfoProfile aTaxpayerInfoProfile = new TaxpayerInfoProfile();
        String userUuid = GardenData.generateUUIDString();
        aTaxpayerInfoProfile.getTaxpayerInfoEntity().setTaxpayerUserUuid(userUuid);
        aTaxpayerInfoProfile.getRoseUserProfile().getUserEntity().setUserUuid(userUuid);
        getDependantProfileList().add(aTaxpayerInfoProfile);
    }
    
    public void deleteDependant(String taxpayerUserUuid){
        List<TaxpayerInfoProfile> aTaxpayerInfoProfileList = getDependantProfileList();
        Integer index = null;
        for (int i = 0; i < aTaxpayerInfoProfileList.size(); i++){
            if (taxpayerUserUuid.equalsIgnoreCase(aTaxpayerInfoProfileList.get(i).getTaxpayerInfoEntity().getTaxpayerUserUuid())){
                index = i;
                break;
            }
        }
        if (index != null){
            getDependantProfileList().remove(index.intValue());
        }
    }
    
    public void addNewTLCLicenseDataEntry(){
        TlcLicenseProfile aTlcLicenseProfile = new TlcLicenseProfile();
        aTlcLicenseProfile.getTlcLicenseEntity().setDriverUuid(GardenData.generateUUIDString());
        getTlcLicenseProfileList().add(aTlcLicenseProfile);
    }
    
    public void deleteTLCLicense(String driverUuid){
        List<TlcLicenseProfile> aTlcLicenseProfileList = getTlcLicenseProfileList();
        Integer index = null;
        for (int i = 0; i < aTlcLicenseProfileList.size(); i++){
            if (driverUuid.equalsIgnoreCase(aTlcLicenseProfileList.get(i).getTlcLicenseEntity().getDriverUuid())){
                index = i;
                break;
            }
        }
        if (index != null){
            getTlcLicenseProfileList().remove(index.intValue());
        }
    }
    
    public void addNewPersonalPropertyDataEntry(){
        PersonalPropertyProfile aPersonalPropertyProfile = new PersonalPropertyProfile();
        aPersonalPropertyProfile.getPersonalPropertyEntity().setPersonalPropertyUuid(GardenData.generateUUIDString());
        getPersonalPropertyProfileList().add(aPersonalPropertyProfile);
    }
    
    public void deletePersonalProperty(String personalPropertyUuid){
        List<PersonalPropertyProfile> aPersonalPropertyProfileList = getPersonalPropertyProfileList();
        Integer index = null;
        for (int i = 0; i < aPersonalPropertyProfileList.size(); i++){
            if (personalPropertyUuid.equalsIgnoreCase(aPersonalPropertyProfileList.get(i).getPersonalPropertyEntity().getPersonalPropertyUuid())){
                index = i;
                break;
            }
        }
        if (index != null){
            getPersonalPropertyProfileList().remove(index.intValue());
        }
    }
    
    public void addNewPersonalBusinessPropertyDataEntry(){
        PersonalBusinessPropertyProfile aPersonalBusinessPropertyProfile = new PersonalBusinessPropertyProfile();
        aPersonalBusinessPropertyProfile.getPersonalBusinessPropertyEntity().setPersonalBusinessPropertyUuid(GardenData.generateUUIDString());
        getPersonalBusinessPropertyProfileList().add(aPersonalBusinessPropertyProfile);
    }
    
    public void deletePersonalBusinessProperty(String personalBusinessPropertyUuid){
        List<PersonalBusinessPropertyProfile> aPersonalBusinessPropertyProfileList = getPersonalBusinessPropertyProfileList();
        Integer index = null;
        for (int i = 0; i < aPersonalBusinessPropertyProfileList.size(); i++){
            if (personalBusinessPropertyUuid.equalsIgnoreCase(aPersonalBusinessPropertyProfileList.get(i).getPersonalBusinessPropertyEntity().getPersonalBusinessPropertyUuid())){
                index = i;
                break;
            }
        }
        if (index != null){
            getPersonalBusinessPropertyProfileList().remove(index.intValue());
        }
    }
    
    public String getDependantProfileNames(){
        String result = "";
        for (TaxpayerInfoProfile aTaxpayerInfoProfile : dependantProfileList){
            result += aTaxpayerInfoProfile.getRoseUserProfile().getProfileName() + "; ";
        }
        if (result.isEmpty()){
            result = "N/A";
        }
        return result;
    }

    public boolean isSpouseRequired() {
        return spouseRequired;
    }

    public void setSpouseRequired(boolean spouseRequired) {
        this.spouseRequired = spouseRequired;
    }

    public RoseLocationProfile getPrimaryLocationProfile() {
        return primaryLocationProfile;
    }

    public void setPrimaryLocationProfile(RoseLocationProfile primaryLocationProfile) {
        this.primaryLocationProfile = primaryLocationProfile;
    }

    public List<TaxpayerInfoProfile> getDependantProfileList() {
        return dependantProfileList;
    }

    public void setDependantProfileList(List<TaxpayerInfoProfile> dependantProfileList) {
        this.dependantProfileList = dependantProfileList;
    }

    public RoseContactInfoProfile getPrimaryContactInfoProfile() {
        return primaryContactInfoProfile;
    }

    public void setPrimaryContactInfoProfile(RoseContactInfoProfile primaryContactInfoProfile) {
        this.primaryContactInfoProfile = primaryContactInfoProfile;
    }

    public G01TaxpayerCase getTaxpayerCaseEntity() {
        return taxpayerCaseEntity;
    }

    public void setTaxpayerCaseEntity(G01TaxpayerCase taxpayerCaseEntity) {
        this.taxpayerCaseEntity = taxpayerCaseEntity;
    }

    public TaxpayerInfoProfile getPrimaryTaxpayerProfile() {
        return primaryTaxpayerProfile;
    }

    public void setPrimaryTaxpayerProfile(TaxpayerInfoProfile primaryTaxpayerProfile) {
        this.primaryTaxpayerProfile = primaryTaxpayerProfile;
    }

    public TaxpayerInfoProfile getSpouseProfile() {
        return spouseProfile;
    }

    public void setSpouseProfile(TaxpayerInfoProfile spouseProfile) {
        this.spouseProfile = spouseProfile;
    }

    public List<PersonalPropertyProfile> getPersonalPropertyProfileList() {
        return personalPropertyProfileList;
    }

    public void setPersonalPropertyProfileList(List<PersonalPropertyProfile> personalPropertyProfileList) {
        this.personalPropertyProfileList = personalPropertyProfileList;
    }

    public List<PersonalBusinessPropertyProfile> getPersonalBusinessPropertyProfileList() {
        return personalBusinessPropertyProfileList;
    }

    public void setPersonalBusinessPropertyProfileList(List<PersonalBusinessPropertyProfile> personalBusinessPropertyProfileList) {
        this.personalBusinessPropertyProfileList = personalBusinessPropertyProfileList;
    }

    public List<TlcLicenseProfile> getTlcLicenseProfileList() {
        return tlcLicenseProfileList;
    }

    public void setTlcLicenseProfileList(List<TlcLicenseProfile> tlcLicenseProfileList) {
        this.tlcLicenseProfileList = tlcLicenseProfileList;
    }

    @Override
    public String getProfileName() {
        return taxpayerCaseEntity.getTaxpayerCaseUuid();
    }

    @Override
    public String getProfileDescriptiveName() {
        if ((taxpayerCaseEntity.getDeadline() != null) 
                && (this.getCustomerProfile() != null))
        {
            if (primaryTaxpayerProfile == null){
                return RoseText.getText("PrimaryTaxpayer") 
                        + ": " + getCustomerProfile().getUserProfile().getProfileName() 
                        + " (" + RoseText.getText("Deadline") + " @ "
                        + ZcaCalendar.convertToMMddyyyy(taxpayerCaseEntity.getDeadline(), "-") + ")";
            }else{
                return RoseText.getText("PrimaryTaxpayer") 
                        + ": " + primaryTaxpayerProfile.getRoseUserProfile().getProfileName() 
                        + " (" + RoseText.getText("Deadline") + " @ "
                        + ZcaCalendar.convertToMMddyyyy(taxpayerCaseEntity.getDeadline(), "-") + ")";
            }
        }
        return taxpayerCaseEntity.getTaxpayerCaseUuid();
    }

    @Override
    protected String getProfileUuid() {
        return taxpayerCaseEntity.getTaxpayerCaseUuid();
    }

}
