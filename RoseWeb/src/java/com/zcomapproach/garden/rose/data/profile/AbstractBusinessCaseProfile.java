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

import com.zcomapproach.garden.persistence.entity.G01Agreement;
import com.zcomapproach.garden.persistence.entity.G01Log;
import com.zcomapproach.garden.persistence.entity.G01User;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaText;
import com.zcomapproach.commons.ZcaValidator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author zhijun98
 */
public abstract class AbstractBusinessCaseProfile extends AbstractRoseEntityProfile{
    
    private RoseAccountProfile customerProfile;    //who own this case
    private EmployeeAccountProfile agentProfile;    //who process this case
    private G01Agreement agreementEntity;   //optional: currently use GardenAgreement
    private List<DocumentRequirementProfile> documentRequirementprofileList;
    private List<BusinessCaseBillProfile> businessCaseBillProfileList;
    private List<RoseArchivedDocumentProfile> uploadedArchivedDocumentList;
    
    private G01Log latestLogEntity;
    private G01User latestLogEmployeeUserRecord;
    
    //optional
    private List<DocumentRequirementProfile> allDocumentRequirementProfileList;
    
    public AbstractBusinessCaseProfile() {
        this.customerProfile = new RoseAccountProfile();
        this.agreementEntity = new G01Agreement();  //zzj todo: reserved for the future
        this.documentRequirementprofileList = new ArrayList<>();
        
        //optional, late binding
        this.allDocumentRequirementProfileList = new ArrayList<>();
        this.businessCaseBillProfileList = new ArrayList<>();
        
        this.latestLogEntity = new G01Log();
        this.latestLogEmployeeUserRecord = new G01User();
    }

    public EmployeeAccountProfile getAgentProfile() {
        if (agentProfile == null){
            agentProfile = new EmployeeAccountProfile();
        }
        return agentProfile;
    }

    public void setAgentProfile(EmployeeAccountProfile agentProfile) {
        this.agentProfile = agentProfile;
    }

    public G01Log getLatestLogEntity() {
        return latestLogEntity;
    }

    public void setLatestLogEntity(G01Log latestLogEntity) {
        if (latestLogEntity != null){
            this.latestLogEntity = latestLogEntity;
        }
    }

    public G01User getLatestLogEmployeeUserRecord() {
        return latestLogEmployeeUserRecord;
    }

    public void setLatestLogEmployeeUserRecord(G01User latestLogEmployeeUserRecord) {
        this.latestLogEmployeeUserRecord = latestLogEmployeeUserRecord;
    }
    
    public String getLatestLogMessage(){
        String result = getLatestLogEntity().getLogMsg();
        if (ZcaValidator.isNotNullEmpty(getLatestLogEntity().getOperatorMessage())
                && (!getLatestLogEntity().getOperatorMessage().equalsIgnoreCase(result)))
        {
            result += ": " + getLatestLogEntity().getOperatorMessage();
        }
        if (getLatestLogEntity().getTimestamp() != null){
            result += " ("+ this.getCustomerProfile().getUserProfile().getProfileName() + " - "
                    + ZcaCalendar.convertToMMddyyyyHHmmss(getLatestLogEntity().getTimestamp(), "-", " @ ", ":") + ")";
        }
        return result;
    }
    
    public String getLatestLogEmployeeName(){
        G01User user = getLatestLogEmployeeUserRecord();
        if (user == null){
            return "Employee: N/A";
        }
        String result = ZcaText.denullize(user.getLastName()) + ", " + ZcaText.denullize(user.getFirstName());
        if (", ".equalsIgnoreCase(result)){
            return "Employee: N/A";
        }
        return "Employee: " + result;
    }
    
    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof AbstractBusinessCaseProfile)){
            return;
        }
        AbstractBusinessCaseProfile srcAbstractBusinessCaseProfile = (AbstractBusinessCaseProfile)srcProfile;
        //customerProfile
        getCustomerProfile().cloneProfile(srcAbstractBusinessCaseProfile.getCustomerProfile());
        //businessCaseBillProfileList
        List<BusinessCaseBillProfile> srcBusinessCaseBillProfileList = srcAbstractBusinessCaseProfile.getBusinessCaseBillProfileList();
        getBusinessCaseBillProfileList().clear();
        BusinessCaseBillProfile aBusinessCaseBillProfile;
        for (BusinessCaseBillProfile srcG01DocumentRequirement : srcBusinessCaseBillProfileList){
            aBusinessCaseBillProfile = new BusinessCaseBillProfile();
            aBusinessCaseBillProfile.cloneProfile(srcG01DocumentRequirement);
            getBusinessCaseBillProfileList().add(aBusinessCaseBillProfile);
        }
        //documentRequirementEntityList
        List<DocumentRequirementProfile> srcDocumentRequirementProfileList = srcAbstractBusinessCaseProfile.getDocumentRequirementprofileList();
        getDocumentRequirementprofileList().clear();
        DocumentRequirementProfile aDocumentRequirementProfile;
        for (DocumentRequirementProfile srcG01DocumentRequirement : srcDocumentRequirementProfileList){
            aDocumentRequirementProfile = new DocumentRequirementProfile();
            aDocumentRequirementProfile.cloneProfile(srcG01DocumentRequirement);
            getDocumentRequirementprofileList().add(aDocumentRequirementProfile);
        }
        //uploadedArchivedDocumentList
        List<RoseArchivedDocumentProfile> srcRoseArchivedDocumentProfileList = srcAbstractBusinessCaseProfile.getUploadedArchivedDocumentList();
        getUploadedArchivedDocumentList().clear();
        RoseArchivedDocumentProfile aRoseArchivedDocumentProfile;
        for (RoseArchivedDocumentProfile srcRoseArchivedDocumentProfile : srcRoseArchivedDocumentProfileList){
            aRoseArchivedDocumentProfile = new RoseArchivedDocumentProfile();
            aRoseArchivedDocumentProfile.cloneProfile(srcRoseArchivedDocumentProfile);
            getUploadedArchivedDocumentList().add(aRoseArchivedDocumentProfile);
        }
    
    }

    public List<DocumentRequirementProfile> getAllDocumentRequirementProfileList() {
        return allDocumentRequirementProfileList;
    }

    public void setAllDocumentRequirementProfileList(List<DocumentRequirementProfile> allDocumentRequirementProfileList) {
        this.allDocumentRequirementProfileList = allDocumentRequirementProfileList;
    }

    public List<BusinessCaseBillProfile> getBusinessCaseBillProfileList() {
        if (businessCaseBillProfileList == null){
            businessCaseBillProfileList = new ArrayList<>();
        }
        if (!businessCaseBillProfileList.isEmpty()){
            Collections.sort(businessCaseBillProfileList, (BusinessCaseBillProfile o1, BusinessCaseBillProfile o2) -> {
                try{
                    return o1.getBillEntity().getBillDatetime().compareTo(o2.getBillEntity().getBillDatetime())*(-1);
                }catch(Exception ex){
                    return 0;
                }
            });
        }
        return businessCaseBillProfileList;
    }

    public void setBusinessCaseBillProfileList(List<BusinessCaseBillProfile> businessCaseBillProfileList) {
        this.businessCaseBillProfileList = businessCaseBillProfileList;
    }

    public RoseAccountProfile getCustomerProfile() {
        return customerProfile;
    }

    public void setCustomerProfile(RoseAccountProfile customerProfile) {
        this.customerProfile = customerProfile;
    }

    public G01Agreement getAgreementEntity() {
        return agreementEntity;
    }

    public void setAgreementEntity(G01Agreement agreementEntity) {
        this.agreementEntity = agreementEntity;
    }

    public List<DocumentRequirementProfile> getDocumentRequirementprofileList() {
        return documentRequirementprofileList;
    }

    public void setDocumentRequirementprofileList(List<DocumentRequirementProfile> documentRequirementprofileList) {
        this.documentRequirementprofileList = documentRequirementprofileList;
    }

    public List<RoseArchivedDocumentProfile> getUploadedArchivedDocumentList() {
        return uploadedArchivedDocumentList;
    }

    public void setUploadedArchivedDocumentList(List<RoseArchivedDocumentProfile> uploadedArchivedDocumentList) {
        this.uploadedArchivedDocumentList = uploadedArchivedDocumentList;
    }

}
