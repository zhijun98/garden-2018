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
import com.zcomapproach.garden.persistence.entity.G01ContactEntity;
import com.zcomapproach.garden.persistence.entity.G01ContactEntityPK;
import com.zcomapproach.garden.persistence.entity.G01ContactMessage;
import com.zcomapproach.garden.rose.RosePageName;
import com.zcomapproach.garden.rose.data.profile.ContactMessageProfile;
import com.zcomapproach.garden.rose.data.profile.TaxcorpContactMessageProfile;
import com.zcomapproach.garden.rose.data.profile.TaxcorpContactProfile;
import com.zcomapproach.garden.rose.data.profile.TaxcorpRepresentativeProfile;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.garden.rose.util.RoseWebUtils;
import com.zcomapproach.garden.util.GardenData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author zhijun98
 */
@Named(value = "taxcorpFilingContactBean")
@ViewScoped
public class TaxcorpFilingContactBean extends RoseContactBean {
    
    /**
     * Targets which will be displayed in the dialog
     */
    private ContactMessageProfile targetContactMessage;
    private TaxcorpContactProfile targetTaxcorpContactProfile;
    
    private List<TaxcorpContactProfile> allTaxcorpContactProfileList;

    public TaxcorpFilingContactBean() {
        allTaxcorpContactProfileList = new ArrayList<>();
        
        targetContactMessage = new TaxcorpContactMessageProfile();
    }
    
    public void handleTaxcorpDetailClicked(TaxcorpContactProfile targetTaxcorpContactProfile){
        this.targetTaxcorpContactProfile = targetTaxcorpContactProfile;
    }

    public TaxcorpContactProfile getTargetTaxcorpContactProfile() {
        return targetTaxcorpContactProfile;
    }

    public void setTargetTaxcorpContactProfile(TaxcorpContactProfile targetTaxcorpContactProfile) {
        this.targetTaxcorpContactProfile = targetTaxcorpContactProfile;
    }
    
    public void handleContactMessageClicked(ContactMessageProfile targetContactMessage){
        this.targetContactMessage = targetContactMessage;
    }

    public ContactMessageProfile getTargetContactMessage() {
        return targetContactMessage;
    }

    public void setTargetContactMessage(ContactMessageProfile targetContactMessage) {
        this.targetContactMessage = targetContactMessage;
    }

    @Override
    public void setRequestedEntityType(String requestedEntityType) {
        super.setRequestedEntityType(requestedEntityType);
        //initialize getAvailableContactorProfileList()
        getAvailableContactorProfileList().clear();
        switch(getEntityType()){
            case SEARCH_TAXCORP_FILING:
                intializeAllTaxcorpFilingConciseProfiles(); 
                break;
            default:
                //do nothing
        }
        //checkContactorProfile each item in getAvailableContactorProfileList()
        checkContactorProfile(getAvailableContactorProfileList());
    }

    @Override
    public String getTargetReturnWebPath() {
        String targetWebPath = "/" + RoseWebUtils.BUSINESS_FOLDER + "/";
        switch(getEntityType()){
            case SEARCH_TAXCORP_FILING:
                targetWebPath += RosePageName.SearchTaxcorpFilingResultPage.name() + RoseWebUtils.constructWebQueryString(null, true);
                break;
            default:
                //do nothing
        }
        return targetWebPath;
    }

    private void intializeAllTaxcorpFilingConciseProfiles() {
        //getAvailableContactorProfileList().addAll(getTaxcorpEJB().findTaxcorpRepresentativeProfileListByTaxFilingProfileList(searchTaxcorpBean.getSearchResultTaxFilingProfileList()));
        if (allTaxcorpContactProfileList.isEmpty()){
            allTaxcorpContactProfileList.addAll(getTaxcorpEJB().findTaxcorpContactProfileListByTaxFilingProfileList(getSearchTaxcorpBean().getSearchResultTaxFilingProfileList()));
            Collections.sort(allTaxcorpContactProfileList, (TaxcorpContactProfile o1, TaxcorpContactProfile o2) -> o1.getTaxcorpCase().getCorporateName().compareToIgnoreCase(o2.getTaxcorpCase().getCorporateName()));
        }
    }

    public List<TaxcorpContactProfile> getAllTaxcorpContactProfileList() {
        if (allTaxcorpContactProfileList == null){
            allTaxcorpContactProfileList = new ArrayList<>();
        }
        return allTaxcorpContactProfileList;
    }

    public void setAllTaxcorpContactProfileList(List<TaxcorpContactProfile> allTaxcorpContactProfileList) {
        this.allTaxcorpContactProfileList = allTaxcorpContactProfileList;
    }
    
    @Override
    public String sendEmailAndSmsToContactor(){
        getTargetSelectedContactorProfiles().clear();
        
        String contactMessageUuid = GardenData.generateUUIDString();
        G01ContactMessage aG01ContactMessage = new G01ContactMessage();
        aG01ContactMessage.setContactMessageUuid(contactMessageUuid);
        aG01ContactMessage.setContactSubject(super.getTargetContactSubject());
        aG01ContactMessage.setEmailContent(getTargetMessageForEmail());
        aG01ContactMessage.setContactTimestamp(new Date());
        aG01ContactMessage.setEmployeeAccountUuid(getRoseUserSession().getTargetAccountProfile().getTargetPersonUuid());
        aG01ContactMessage.setSmsContent(super.getTargetMessageForSMS());
        
        List<G01ContactEntity> aG01ContactEntityList = new ArrayList<>();
        G01ContactEntity aG01ContactEntity;
        G01ContactEntityPK pkid;
        List<TaxcorpRepresentativeProfile> aTaxcorpRepresentativeProfileList;
        for (TaxcorpContactProfile aTaxcorpContactProfile : allTaxcorpContactProfileList){
            aTaxcorpRepresentativeProfileList = aTaxcorpContactProfile.getTargetSelectedContactorProfiles();
            if (!aTaxcorpRepresentativeProfileList.isEmpty()){
                aG01ContactEntity = new G01ContactEntity();
                pkid = new G01ContactEntityPK();
                pkid.setContactMessageUuid(contactMessageUuid);
                pkid.setEntityType(GardenEntityType.TAXCORP_CASE.name());
                pkid.setEntityUuid(aTaxcorpContactProfile.getTaxcorpCase().getTaxcorpCaseUuid());
                aG01ContactEntity.setG01ContactEntityPK(pkid);
                aG01ContactEntity.setEmailOrSms(getEmailOrSmsIntegerValueForPersistency());
                aG01ContactEntityList.add(aG01ContactEntity);
                getTargetSelectedContactorProfiles().addAll(aTaxcorpRepresentativeProfileList);
            }
            
        }
        
        if (getTargetSelectedContactorProfiles().isEmpty()){
            RoseJsfUtils.setGlobalErrorFacesMessage(RoseText.getText("ContactorList") + " - " + RoseText.getText("FieldRequired_T"));
            return null;
        }
        
        if (super.sendEmailAndSmsToContactorHelper()){
            try {
                getTaxcorpEJB().storeTaxFilingContactMessage(aG01ContactMessage, aG01ContactEntityList);
            } catch (Exception ex) {
                //Logger.getLogger(TaxcorpFilingContactBean.class.getName()).log(Level.SEVERE, null, ex);
                RoseJsfUtils.setGlobalErrorFacesMessage(ex.getMessage());
            }
        }
        
        return null;
    }
    
    @Override
    public void handleCheckAll(){
        for (TaxcorpContactProfile aTaxcorpContactProfile : allTaxcorpContactProfileList){
            aTaxcorpContactProfile.getTargetSelectedContactorProfiles().clear();
            aTaxcorpContactProfile.getTargetSelectedContactorProfiles().addAll(new ArrayList<>(aTaxcorpContactProfile.getTaxcorpRepresentativeProfiles()));
        }
    }
    
    @Override
    public void handleUncheckAll(){
        for (TaxcorpContactProfile aTaxcorpContactProfile : allTaxcorpContactProfileList){
            aTaxcorpContactProfile.getTargetSelectedContactorProfiles().clear();
        }
    }
}
