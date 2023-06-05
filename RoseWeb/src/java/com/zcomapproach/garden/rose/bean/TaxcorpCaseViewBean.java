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

import com.zcomapproach.garden.data.constant.GardenBooleanValue;
import com.zcomapproach.commons.persistent.exception.NonUniqueEntityException;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G01DocumentRequirement;
import com.zcomapproach.garden.persistence.entity.G01TaxcorpCase;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.garden.rose.RosePageName;
import com.zcomapproach.garden.rose.data.profile.DocumentRequirementProfile;
import com.zcomapproach.garden.rose.data.profile.BusinessCaseBillProfile;
import com.zcomapproach.garden.rose.data.profile.RoseArchivedFileTypeProfile;
import com.zcomapproach.garden.rose.data.profile.TaxcorpCaseProfile;
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
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author zhijun98
 */
@Named(value = "taxcorpCaseViewBean")
@ViewScoped
public class TaxcorpCaseViewBean extends AbstractBusinessCaseViewBean implements ITaxcorpCaseDataBean {
    
    private String requestedCustomerUuid;
    private String requestedTaxcorpCaseUuid;
    
    private TaxcorpCaseProfile targetTaxcorpCaseProfile;

    private boolean taxcorpCaseBasicInformationUpdateDemanded;
    
    private boolean taxcorpContactorsUpdateDemanded;

    public TaxcorpCaseViewBean() {
        this.targetTaxcorpCaseProfile = new TaxcorpCaseProfile();
    }

    public List<TaxcorpCaseProfile> getCustomerHistoricalTaxcorpCaseProfileList() {
        List<TaxcorpCaseProfile> result = getTaxcorpEJB().findTaxcorpCaseProfileListByCustomerUuid(this.getRequestedCustomerUuid());
        Collections.sort(result, (TaxcorpCaseProfile o1, TaxcorpCaseProfile o2) -> o1.getTaxcorpCaseEntity().getUpdated().compareTo(o2.getTaxcorpCaseEntity().getUpdated())*(-1));
        return result;
    }

    @Override
    public boolean isTaxcorpContactorsUpdateDemanded() {
        return taxcorpContactorsUpdateDemanded;
    }

    public void setTaxcorpContactorsUpdateDemanded(boolean taxcorpContactorsUpdateDemanded) {
        this.taxcorpContactorsUpdateDemanded = taxcorpContactorsUpdateDemanded;
    }

    @Override
    public void openTaxcorpContactorsDataEntry() {
        setTaxcorpContactorsUpdateDemanded(true);
    }

    @Override
    public void closeTaxcorpContactorsDataEntry() {
        setTaxcorpContactorsUpdateDemanded(false);
    }

    @Override
    public void addNewTaxcorpRepresentativeProfileDataEntry() {
        openTaxcorpContactorsDataEntry();
        getTargetTaxcorpCaseProfile().addNewTaxcorpRepresentativeProfileDataEntry();
    }
    
    @Override
    public boolean isTaxcorpCaseBasicInformationUpdateDemanded() {
        return taxcorpCaseBasicInformationUpdateDemanded;
    }

    public void setTaxcorpCaseBasicInformationUpdateDemanded(boolean taxcorpCaseBasicInformationUpdateDemanded) {
        this.taxcorpCaseBasicInformationUpdateDemanded = taxcorpCaseBasicInformationUpdateDemanded;
    }

    @Override
    public void openTaxcorpCaseBasicInformationDataEntry() {
        setTaxcorpCaseBasicInformationUpdateDemanded(true);
    }

    @Override
    public void closeTaxcorpCaseBasicInformationDataEntry() {
        setTaxcorpCaseBasicInformationUpdateDemanded(false);
    }

    @Override
    public void saveTargetTaxcorpCaseBasicProfile() {
        setTaxcorpCaseBasicInformationUpdateDemanded(false);
    }

    @Override
    public String getRosePageTopic() {
        if (isForCreateNewEntity()){
            return RoseText.getText("RequestNewCorporateTax");
        }else{
            return RoseText.getText("YourCorporateTax");
        }
    }

    @Override
    public String getTopicIconAwesomeName() {
        if (isForCreateNewEntity()){
            return "calculator";    //fa-calculator
        }else{
            return "check-square-o";    //fa-check-square-o
        }
    }
    
    @Override
    public List<BusinessCaseBillProfile> getBusinessCaseBillProfileListOfTargetBusinessCase() {
        return this.getTargetTaxcorpCaseProfile().getBusinessCaseBillProfileList();
    }

    @Override
    public void storeDocumentRequirementProfile(String documentUuid) {
        storeDocumentRequirementProfileHelper(documentUuid, getTargetTaxcorpCaseProfile().getDocumentRequirementprofileList());
    }

    @Override
    public void deleteDocumentRequirementProfile(String documentUuid) {
        deleteDocumentRequirementProfileHelper(documentUuid, getTargetTaxcorpCaseProfile().getDocumentRequirementprofileList());
    }

    @Override
    public void storeTargetDocumentRequirementProfile() {
        try{
            G01DocumentRequirement aG01DocumentRequirement = getTargetDocumentRequirementProfile().getDocumentRequirementEntity();
            if (ZcaValidator.isNullEmpty(aG01DocumentRequirement.getDocumentUuid())){
                aG01DocumentRequirement.setDocumentUuid(GardenData.generateUUIDString());
            }
            if (ZcaValidator.isNullEmpty(aG01DocumentRequirement.getServiceTagUuid())){
                aG01DocumentRequirement.setServiceTagUuid(GardenData.generateUUIDString());
            }
            aG01DocumentRequirement.setEntityType(GardenEntityType.TAXCORP_CASE.name());
            aG01DocumentRequirement.setEntityUuid(targetTaxcorpCaseProfile.getTaxcorpCaseEntity().getTaxcorpCaseUuid());
            if (aG01DocumentRequirement.getQuantity() == null){
                aG01DocumentRequirement.setQuantity(0);
            }
            if (ZcaValidator.isNullEmpty(aG01DocumentRequirement.getFileDemanded())){
                aG01DocumentRequirement.setFileDemanded(GardenBooleanValue.Yes.value());
            }
            storeTargetDocumentRequirementProfileHelper(getTargetDocumentRequirementProfile(), targetTaxcorpCaseProfile.getDocumentRequirementprofileList());
            setTargetDocumentRequirementProfile(new DocumentRequirementProfile());
            RoseJsfUtils.setGlobalSuccessfulOperationMessage();
        } catch (Exception ex) {
            //Logger.getLogger(TaxpayerCaseViewBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalSystemErrorFacesMessage(ex.getMessage());
        }
    }

    @Override
    public List<DocumentRequirementProfile> getAllDocumentRequirementProfileList() {
        List<DocumentRequirementProfile> result = getTargetTaxcorpCaseProfile().getAllDocumentRequirementProfileList();
        if (result == null){
            result = new ArrayList<>();
//            result = constructAllDocumentRequirementProfileList(getTargetTaxcorpCaseProfile().getTaxcorpCaseEntity().getTaxcorpCaseUuid(), GardenEntityType.TAXCORP_CASE);
            getTargetTaxcorpCaseProfile().setAllDocumentRequirementProfileList(result);
        }
        return result;
    }

    @Override
    public List<DocumentRequirementProfile> getDocumentRequirementProfileList() {
        return this.getTargetTaxcorpCaseProfile().getDocumentRequirementprofileList();
    }

    @Override
    public List<RoseArchivedFileTypeProfile> getRoseArchivedFileTypeProfileList() {
        return RoseDataAgent.loadIntoRoseArchivedFileTypeProfiles(this.getTargetTaxcorpCaseProfile().getUploadedArchivedDocumentList());
    }

    public String getPrintableViewPageLink(){
        HashMap<String, String> params = new HashMap<>();
        params.put(getRoseParamKeys().getTaxcorpCaseUuidParamKey(), 
                this.getTargetTaxcorpCaseProfile().getTaxcorpCaseEntity().getTaxcorpCaseUuid());
        return RoseJsfUtils.getRootWebPath()+ RoseWebUtils.BUSINESS_FOLDER + "/" 
                + RosePageName.TaxcorpCasePrintablePage.name() + RoseWebUtils.JSF_EXT 
                + RoseWebUtils.constructWebQueryString(params, false);
    }

    public String getRequestedCustomerUuid() {
        return requestedCustomerUuid;
    }

    public void setRequestedCustomerUuid(String requestedCustomerUuid) {
        if ((ZcaValidator.isNotNullEmpty(requestedCustomerUuid)) && isForCreateNewEntity()) {
            targetTaxcorpCaseProfile.setCustomerProfile(getBusinessEJB().findRoseAccountProfileByAccountUserUuid(requestedCustomerUuid));
        }
        this.requestedCustomerUuid = requestedCustomerUuid;
    }

    public String getRequestedTaxcorpCaseUuid() {
        return requestedTaxcorpCaseUuid;
    }

    public void setRequestedTaxcorpCaseUuid(String requestedTaxcorpCaseUuid) {
        if ((ZcaValidator.isNotNullEmpty(requestedTaxcorpCaseUuid)) && 
                (!requestedTaxcorpCaseUuid.equalsIgnoreCase(targetTaxcorpCaseProfile.getTaxcorpCaseEntity().getTaxcorpCaseUuid())))
        {
            TaxcorpCaseProfile aTaxcorpCaseProfile = getTaxcorpEJB().findTaxcorpCaseProfileByTaxcorpCaseUuid(requestedTaxcorpCaseUuid);
            if (aTaxcorpCaseProfile != null){
                targetTaxcorpCaseProfile = aTaxcorpCaseProfile;
                //setRequestedViewPurpose(RoseWebParamValue.UPDATE_EXISTING_ENTITY.value());
                this.requestedCustomerUuid = aTaxcorpCaseProfile.getCustomerProfile().getAccountEntity().getAccountUuid();
                this.requestedTaxcorpCaseUuid = requestedTaxcorpCaseUuid;
                
                setSelectedEmployeeProfileForCaseAssignment(getBusinessEJB().findEmployeeAccountProfileByAccountUserUuid(
                        targetTaxcorpCaseProfile.getTaxcorpCaseEntity().getEmployeeAccountUuid()));
                        
            }
        }
    }
    
    @Override
    public void assignSelectedEmployeeProfileToTargetCase(){
        targetTaxcorpCaseProfile.getTaxcorpCaseEntity().setEmployeeAccountUuid(getSelectedEmployeeProfileForCaseAssignment().getAccountEntity().getAccountUuid());
        try {
            getTaxcorpEJB().storeEntityByUuid(G01TaxcorpCase.class, targetTaxcorpCaseProfile.getTaxcorpCaseEntity(),
                    targetTaxcorpCaseProfile.getTaxcorpCaseEntity().getTaxcorpCaseUuid(), G01DataUpdaterFactory.getSingleton().getG01TaxcorpCaseUpdater());
            RoseJsfUtils.setGlobalSuccessfulOperationMessage();
        } catch (Exception ex) {
            //Logger.getLogger(TaxcorpCaseViewBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalFailedOperationMessage(ex.getMessage());
        }
    }

    @Override
    public TaxcorpCaseProfile getTargetTaxcorpCaseProfile() {
        return targetTaxcorpCaseProfile;
    }

    public void setTargetTaxcorpCaseProfile(TaxcorpCaseProfile targetTaxcorpCaseProfile) {
        this.targetTaxcorpCaseProfile = targetTaxcorpCaseProfile;
    }

    @Override
    public GardenEntityType getEntityType() {
        return GardenEntityType.TAXCORP_CASE;
    }

    @Override
    public String getRequestedEntityUuid() {
        return this.getTargetTaxcorpCaseProfile().getTaxcorpCaseEntity().getTaxcorpCaseUuid();
    }

    @Override
    public String getTargetReturnWebPath() {
        HashMap<String, String> params = new HashMap<>();
        params.put(getRoseParamKeys().getTaxcorpCaseUuidParamKey(), getRequestedEntityUuid());
        return getTargetReturnWebPageName() + RoseWebUtils.constructWebQueryString(params, true);
    }
    
    public void storeTargetTaxcorpCaseProfile(){
        //do nothing for view-only
    }

    @Override
    public void saveTaxcorpRepresentativeProfileList() {
        //do nothing
    }

    @Override
    public void populateAccountProfileAsContactor() {
        //do nothing
    }
    
    public boolean checkTargetTaxcorpCaseUniqueness(G01TaxcorpCase targetTaxcorpCase){
        if (!isForCreateNewEntity()){  //skip it for existing case
            return false;
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("einNumber", targetTaxcorpCase.getEinNumber());
        try {
            G01TaxcorpCase aG01TaxcorpCase = getTaxcorpEJB().findEntityByNamedQuery(G01TaxcorpCase.class, "G01TaxcorpCase.findByEinNumber", params);
            if (aG01TaxcorpCase != null){
                RoseJsfUtils.setGlobalFatalFacesMessage(RoseText.getText("TaxpayerCaseRedundancy_T"));
                return true;
            }
        } catch (NonUniqueEntityException ex) {
            //Logger.getLogger(TaxcorpCaseViewBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalFatalFacesMessage(RoseText.getText("TaxpayerCaseRedundancy_T"));
            return true;
        }
        return false;
    }

}
