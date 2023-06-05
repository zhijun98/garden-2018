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

import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G01DocumentRequirement;
import com.zcomapproach.garden.persistence.entity.G01Log;
import com.zcomapproach.garden.persistence.entity.G01TaxpayerCase;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.garden.rose.RosePageName;
import com.zcomapproach.garden.rose.data.profile.DocumentRequirementProfile;
import com.zcomapproach.garden.rose.data.profile.RoseAccountProfile;
import com.zcomapproach.garden.rose.data.profile.BusinessCaseBillProfile;
import com.zcomapproach.garden.rose.data.profile.RoseArchivedFileTypeProfile;
import com.zcomapproach.garden.rose.data.profile.TaxpayerCaseProfile;
import com.zcomapproach.garden.rose.data.profile.TaxpayerInfoProfile;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseDataAgent;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.garden.rose.util.RoseWebUtils;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author zhijun98
 */
@Named(value = "taxpayerCaseViewBean")
@ViewScoped
public class TaxpayerCaseViewBean extends AbstractBusinessCaseViewBean {
    
    private String requestedCustomerUuid;
    private String requestedTaxpayerCaseUuid;
    
    /**
     * The historical profiles for the customer (i.e. requestedCustomerUuid) help quickly input data into targetTaxpayerCaseProfile
     */
    private TaxpayerCaseProfile selectedHistoricalTaxpayerCaseProfile;
    
    private TaxpayerCaseProfile targetTaxpayerCaseProfile;
    
    /**
     * Creates a new instance of PersonalTaxFilingBean
     */
    public TaxpayerCaseViewBean() {
        targetTaxpayerCaseProfile = new TaxpayerCaseProfile();
    }

    @Override
    public GardenEntityType getEntityType() {
        return GardenEntityType.TAXPAYER_CASE;
    }

    @Override
    public String getRequestedEntityUuid() {
        return getTargetTaxpayerCaseProfile().getTaxpayerCaseEntity().getTaxpayerCaseUuid();
    }

    @Override
    public List<BusinessCaseBillProfile> getBusinessCaseBillProfileListOfTargetBusinessCase() {
        return this.getTargetTaxpayerCaseProfile().getBusinessCaseBillProfileList();
    }

    @Override
    public String getTargetReturnWebPath() {
        HashMap<String, String> params = new HashMap<>();
        params.put(getRoseParamKeys().getTaxpayerCaseUuidParamKey(), getRequestedEntityUuid());
        return getTargetReturnWebPageName() + RoseWebUtils.constructWebQueryString(params, true);
    }

    public String getPrintableViewPageLink(){
        HashMap<String, String> params = new HashMap<>();
        params.put(getRoseParamKeys().getTaxpayerCaseUuidParamKey(), this.getTargetTaxpayerCaseProfile().getTaxpayerCaseEntity().getTaxpayerCaseUuid());
        return RoseJsfUtils.getRootWebPath()+ RoseWebUtils.BUSINESS_FOLDER + "/" + RosePageName.TaxpayerCasePrintablePage.name() + RoseWebUtils.JSF_EXT + RoseWebUtils.constructWebQueryString(params, false);
    }
    
    public boolean isEmptyOtherInforationPanel(){
        return targetTaxpayerCaseProfile.getDependantProfileList().isEmpty()
                && targetTaxpayerCaseProfile.getPersonalBusinessPropertyProfileList().isEmpty()
                && targetTaxpayerCaseProfile.getPersonalPropertyProfileList().isEmpty()
                && targetTaxpayerCaseProfile.getTlcLicenseProfileList().isEmpty();
    }

    public TaxpayerCaseProfile getSelectedHistoricalTaxpayerCaseProfile() {
        return selectedHistoricalTaxpayerCaseProfile;
    }

    public void setSelectedHistoricalTaxpayerCaseProfile(TaxpayerCaseProfile selectedHistoricalTaxpayerCaseProfile) {
        this.selectedHistoricalTaxpayerCaseProfile = selectedHistoricalTaxpayerCaseProfile;
    }
    
    public void populateDataFromSelectedHistoricalTaxpayerCaseProfile(){
        
        targetTaxpayerCaseProfile.cloneTaxpayerCaseProfile(selectedHistoricalTaxpayerCaseProfile);
        
        targetTaxpayerCaseProfile.setSpouseRequired(selectedHistoricalTaxpayerCaseProfile.isSpouseRequired());
        /**
         * BUT...keep the deadline
         */
        targetTaxpayerCaseProfile.getTaxpayerCaseEntity().setDeadline(getRoseSettings().getNextIndividualTaxFilingDeadlineDate());
    }
    
    public void checkCaseUniqueness(){
        checkCaseUniquenessHelper();
    }
    
    boolean checkCaseUniquenessHelper(){
        if (!isForCreateNewEntity()){  //skip it for existing case
            return false;
        }
        if (getRoseUserSession().checkTaxpayerCaseRedundancy(getRoseUserSession().constructDeadlineSsnKey(targetTaxpayerCaseProfile))){
            RoseJsfUtils.setGlobalFatalFacesMessage(RoseText.getText("TaxpayerCaseRedundancy_T"));
            return true;
        }
        return false;
    }

    @Override
    public void storeDocumentRequirementProfile(String documentUuid) {
        storeDocumentRequirementProfileHelper(documentUuid, getTargetTaxpayerCaseProfile().getDocumentRequirementprofileList());
    }

    @Override
    public void deleteDocumentRequirementProfile(String documentUuid) {
        deleteDocumentRequirementProfileHelper(documentUuid, getTargetTaxpayerCaseProfile().getDocumentRequirementprofileList());
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
            aG01DocumentRequirement.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
            aG01DocumentRequirement.setEntityUuid(targetTaxpayerCaseProfile.getTaxpayerCaseEntity().getTaxpayerCaseUuid());
            storeTargetDocumentRequirementProfileHelper(getTargetDocumentRequirementProfile(), targetTaxpayerCaseProfile.getDocumentRequirementprofileList());
            setTargetDocumentRequirementProfile(new DocumentRequirementProfile());
            RoseJsfUtils.setGlobalSuccessfulOperationMessage();
        } catch (Exception ex) {
            //Logger.getLogger(TaxpayerCaseViewBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalSystemErrorFacesMessage(ex.getMessage());
        }
    }

    @Override
    public List<DocumentRequirementProfile> getAllDocumentRequirementProfileList() {
        List<DocumentRequirementProfile> result = getTargetTaxpayerCaseProfile().getAllDocumentRequirementProfileList();
        if ((result == null) || (result.isEmpty())){
            result = constructAllDocumentRequirementProfileList(getTargetTaxpayerCaseProfile().getTaxpayerCaseEntity().getTaxpayerCaseUuid(), GardenEntityType.TAXPAYER_CASE);
            getTargetTaxpayerCaseProfile().setAllDocumentRequirementProfileList(result);
        }
        return result;
    }


    @Override
    public List<DocumentRequirementProfile> getDocumentRequirementProfileList() {
        return this.getTargetTaxpayerCaseProfile().getDocumentRequirementprofileList();
    }

    @Override
    public List<RoseArchivedFileTypeProfile> getRoseArchivedFileTypeProfileList() {
        return RoseDataAgent.loadIntoRoseArchivedFileTypeProfiles(this.getTargetTaxpayerCaseProfile().getUploadedArchivedDocumentList());
    }
    
    public boolean isSpouseRequired() {
        return targetTaxpayerCaseProfile.isSpouseRequired();
    }

    public void setSpouseRequired(boolean spouseRequired) {
        targetTaxpayerCaseProfile.setSpouseRequired(spouseRequired);
    }

    public String getRequestedTaxpayerCaseUuid() {
        return requestedTaxpayerCaseUuid;
    }

    public void setRequestedTaxpayerCaseUuid(String requestedTaxpayerCaseUuid) {
        if ((ZcaValidator.isNotNullEmpty(requestedTaxpayerCaseUuid)) && 
                (!requestedTaxpayerCaseUuid.equalsIgnoreCase(targetTaxpayerCaseProfile.getTaxpayerCaseEntity().getTaxpayerCaseUuid())))
        {
            TaxpayerCaseProfile aTaxpayerCaseProfile = getTaxpayerEJB().findTaxpayerCaseProfileByTaxpayerCaseUuid(requestedTaxpayerCaseUuid);
            if (aTaxpayerCaseProfile != null){
                targetTaxpayerCaseProfile = aTaxpayerCaseProfile;
                //setRequestedViewPurpose(RoseWebParamValue.UPDATE_EXISTING_ENTITY.value());
                this.requestedCustomerUuid = aTaxpayerCaseProfile.getCustomerProfile().getAccountEntity().getAccountUuid();
                this.requestedTaxpayerCaseUuid = requestedTaxpayerCaseUuid;
                
                setSelectedEmployeeProfileForCaseAssignment(getBusinessEJB().findEmployeeAccountProfileByAccountUserUuid(
                        targetTaxpayerCaseProfile.getTaxpayerCaseEntity().getEmployeeAccountUuid()));
            }
        }
    }
    
    @Override
    public void assignSelectedEmployeeProfileToTargetCase(){
        targetTaxpayerCaseProfile.getTaxpayerCaseEntity().setEmployeeAccountUuid(getSelectedEmployeeProfileForCaseAssignment().getAccountEntity().getAccountUuid());
        try {
            getTaxpayerEJB().storeEntityByUuid(G01TaxpayerCase.class, targetTaxpayerCaseProfile.getTaxpayerCaseEntity(),
                    targetTaxpayerCaseProfile.getTaxpayerCaseEntity().getTaxpayerCaseUuid(), G01DataUpdaterFactory.getSingleton().getG01TaxpayerCaseUpdater());
            RoseJsfUtils.setGlobalSuccessfulOperationMessage();
        } catch (Exception ex) {
            //Logger.getLogger(TaxpayerCaseViewBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalFailedOperationMessage(ex.getMessage());
        }
    }

    public String getRequestedCustomerUuid() {
        return requestedCustomerUuid;
    }
    
    @Override
    public String getTopicIconAwesomeName(){
        if (isForCreateNewEntity()){
            return "calculator";    //fa-calculator
        }else{
            return "check-square-o";    //fa-check-square-o
        }
    }

    @Override
    public String getRosePageTopic() {
        if (isForCreateNewEntity()){
            return RoseText.getText("NewTaxpayer") + " - " + RoseText.getText("Deadline") + ": " 
                    + ZcaCalendar.convertToMMddyyyy(getRoseSettings().getNextIndividualTaxFilingDeadlineDate(), "-");
        }else{
            return RoseText.getText("YourIndividualTax") + " - " + targetTaxpayerCaseProfile.getProfileDescriptiveName();
        }
    }

    /**
     * Customer, who requests to process a tax filing case. This customer is not necessary to be the primary taxpayer
     * @param requestedCustomerUuid 
     */
    public void setRequestedCustomerUuid(String requestedCustomerUuid) {
        if ((ZcaValidator.isNotNullEmpty(requestedCustomerUuid)) && isForCreateNewEntity()) {
            RoseAccountProfile accountProfile= getBusinessEJB().findRoseAccountProfileByAccountUserUuid(requestedCustomerUuid);
            targetTaxpayerCaseProfile.setCustomerProfile(accountProfile);
            targetTaxpayerCaseProfile.setPrimaryTaxpayerProfile(TaxpayerInfoProfile.createTaxpayerInfoProfileInstanceByAccountProfile(accountProfile));
            targetTaxpayerCaseProfile.getTaxpayerCaseEntity().setDeadline(getRoseSettings().getNextIndividualTaxFilingDeadlineDate());
        }
        this.requestedCustomerUuid = requestedCustomerUuid;
    }

    public TaxpayerCaseProfile getTargetTaxpayerCaseProfile() {
        return targetTaxpayerCaseProfile;
    }

    public void setTargetTaxpayerCaseProfile(TaxpayerCaseProfile targetTaxpayerCaseProfile) {
        this.targetTaxpayerCaseProfile = targetTaxpayerCaseProfile;
    }
    
    public String storeTargetTaxpayerCaseProfile(){
        //do nothing for view-only
        return null;
    }

    private boolean validateTargetLogEntity() {
        G01Log aG01Log = this.getTargetLogEntity();
        if (ZcaValidator.isNullEmpty(this.getSelectedLogMsg())){
            RoseJsfUtils.setGlobalErrorFacesMessage(RoseText.getText("SelectWorkStatus")+ ": " + RoseText.getText("FieldRequired_T"));
            return false;
        }else{
            aG01Log.setLogMsg(getSelectedLogMsg());
        }
        if (ZcaValidator.isNullEmpty(aG01Log.getLogUuid())){
            aG01Log.setLogUuid(GardenData.generateUUIDString());
        }
        aG01Log.setEntityUuid(getTargetTaxpayerCaseProfile().getTaxpayerCaseEntity().getTaxpayerCaseUuid());
        aG01Log.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
        aG01Log.setOperatorAccountUuid(getRoseUserSession().getTargetAccountProfile().getAccountEntity().getAccountUuid());
        aG01Log.setTimestamp(new Date());
        
        this.getTargetTaxpayerCaseProfile().setLatestLogEntity(aG01Log);
        this.getTargetTaxpayerCaseProfile().getTaxpayerCaseEntity().setLatestLogUuid(aG01Log.getLogUuid());
        
        if (ZcaValidator.isNullEmpty(getTargetTaxpayerCaseProfile().getTaxpayerCaseEntity().getEmployeeAccountUuid())){
            this.getTargetTaxpayerCaseProfile().getTaxpayerCaseEntity().setEmployeeAccountUuid(getRoseUserSession().getTargetAccountProfile().getAccountEntity().getAccountUuid());
        }
        
        return true;
    }
    
    @Override
    public String saveTargetLogEntity(){
        if (validateTargetLogEntity()){
            try {
                //save log
                getRuntimeEJB().storeEntityByUuid(G01Log.class, getTargetLogEntity(), getTargetLogEntity().getLogUuid(), 
                        G01DataUpdaterFactory.getSingleton().getG01LogUpdater());
                //save latest log uuid
                getRuntimeEJB().storeEntityByUuid(G01TaxpayerCase.class, getTargetTaxpayerCaseProfile().getTaxpayerCaseEntity(), 
                        getTargetTaxpayerCaseProfile().getTaxpayerCaseEntity().getTaxpayerCaseUuid(), G01DataUpdaterFactory.getSingleton().getG01TaxpayerCaseUpdater());
                RoseJsfUtils.setGlobalSuccessfulOperationMessage();
                return getTargetReturnWebPath();
            } catch (Exception ex) {
                //Logger.getLogger(AbstractRoseComponentBean.class.getName()).log(Level.SEVERE, null, ex);
                RoseJsfUtils.setGlobalErrorFacesMessage(ex.getMessage());
                return null;
            }
        }else{
            return null;
        }
    }
    
}
