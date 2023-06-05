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

import com.zcomapproach.garden.rose.data.profile.BusinessCaseProfile;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.rose.RosePageName;
import com.zcomapproach.garden.rose.data.profile.AbstractBusinessCaseProfile;
import com.zcomapproach.garden.rose.data.profile.BusinessCaseBillProfile;
import com.zcomapproach.garden.rose.data.profile.DocumentRequirementProfile;
import com.zcomapproach.garden.rose.data.profile.RoseArchivedFileTypeProfile;
import com.zcomapproach.garden.rose.data.profile.TaxcorpCaseProfile;
import com.zcomapproach.garden.rose.data.profile.TaxpayerCaseProfile;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.garden.rose.util.RoseWebUtils;
import com.zcomapproach.commons.ZcaValidator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author zhijun98
 */
@Named(value = "roseInvoiceBean")
@ViewScoped
public class RoseInvoiceBean extends AbstractBusinessCaseViewBean {
    
    private String requestedBillUuid;
    
    /**
     * Optional associated cases
     */
    private TaxcorpCaseProfile taxcorpCaseProfile;
    private TaxpayerCaseProfile taxpayerCaseProfile;
    
    public String getTargetEntityTypeParamValue(){
        if (getTaxcorpCaseProfile() != null){
            return GardenEntityType.TAXCORP_CASE.value();
        }else if (getTaxpayerCaseProfile() != null){
            return GardenEntityType.TAXPAYER_CASE.value();
        }else{
            return null;
        }
    }
    
    public String getTargetEntityUuid(){
        if (getTaxcorpCaseProfile() != null){
            return getTaxcorpCaseProfile().getTaxcorpCaseEntity().getTaxcorpCaseUuid();
        }else if (getTaxpayerCaseProfile() != null){
            return getTaxpayerCaseProfile().getTaxpayerCaseEntity().getTaxpayerCaseUuid();
        }else{
            return null;
        }
    }

    public String getRequestedBillUuid() {
        return requestedBillUuid;
    }

    public void setRequestedBillUuid(String requestedBillUuid) {
        if (ZcaValidator.isNotNullEmpty(requestedBillUuid)){
            BusinessCaseBillProfile aBillProfile = getBusinessEJB().findBusinessCaseBillProfileByBillUuid(requestedBillUuid);
            if (aBillProfile == null){
                aBillProfile = new BusinessCaseBillProfile();
            }else{
                GardenEntityType aGardenEntityType = GardenEntityType.convertEnumNameToType(aBillProfile.getBillEntity().getEntityType());
                switch(aGardenEntityType){
                    case TAXPAYER_CASE:
                        taxpayerCaseProfile = getTaxpayerEJB().findTaxpayerCaseProfileByTaxpayerCaseUuid(aBillProfile.getBillEntity().getEntityUuid());
                        break;
                    case TAXCORP_CASE:
                        taxcorpCaseProfile = getTaxcorpEJB().findTaxcorpCaseProfileByTaxcorpCaseUuid(aBillProfile.getBillEntity().getEntityUuid());
                        break;
                }
            }
            setTargetBusinessCaseBillProfile(aBillProfile);
        }
        this.requestedBillUuid = requestedBillUuid;
    }

    public TaxcorpCaseProfile getTaxcorpCaseProfile() {
        return taxcorpCaseProfile;
    }

    public void setTaxcorpCaseProfile(TaxcorpCaseProfile taxcorpCaseProfile) {
        this.taxcorpCaseProfile = taxcorpCaseProfile;
    }

    public TaxpayerCaseProfile getTaxpayerCaseProfile() {
        return taxpayerCaseProfile;
    }

    public void setTaxpayerCaseProfile(TaxpayerCaseProfile taxpayerCaseProfile) {
        this.taxpayerCaseProfile = taxpayerCaseProfile;
    }
    
    public AbstractBusinessCaseProfile getTargetBusinessCaseProfile(){
        if (getTaxcorpCaseProfile() != null){
            return getTaxcorpCaseProfile();
        }else if (getTaxpayerCaseProfile() != null){
            return getTaxpayerCaseProfile();
        }else{
            return new BusinessCaseProfile();
        }
    }

    @Override
    public void assignSelectedEmployeeProfileToTargetCase() {
    }

    @Override
    public String getRosePageTopic() {
        if (getTaxcorpCaseProfile() != null){
            return getTaxcorpCaseProfile().getProfileDescriptiveName();
        }else if (getTaxpayerCaseProfile() != null){
            return getTaxpayerCaseProfile().getProfileDescriptiveName();
        }else{
            return RoseText.getText("Invoice");
        }
    }

    @Override
    public String getTopicIconAwesomeName() {
        return "file-text-o";
    }

    @Override
    public String getTargetReturnWebPath() {
        HashMap<String, String> params = new HashMap<>();
        params.put(getRoseParamKeys().getRoseBillUuidParamKey(), getRequestedBillUuid());
        return getTargetReturnWebPageName() + RoseWebUtils.constructWebQueryString(params, true);
    }

    public String getGoBackWebPath(){
        HashMap<String, String> params = new HashMap<>();
        if (getTaxcorpCaseProfile() != null){
            params.put(getRoseParamKeys().getTaxcorpCaseUuidParamKey(), getTaxcorpCaseProfile().getTaxcorpCaseEntity().getTaxcorpCaseUuid());
            if (getRoseUserSession().isEmployed()){
                return RoseJsfUtils.getRootWebPath()+ RoseWebUtils.BUSINESS_FOLDER + "/" + RosePageName.TaxcorpCaseMgtPage.name() + RoseWebUtils.JSF_EXT + RoseWebUtils.constructWebQueryString(params, true);
            }else{
                return RoseJsfUtils.getRootWebPath()+ RoseWebUtils.CUSTOMER_FOLDER + "/" + RosePageName.TaxcorpCaseViewPage.name() + RoseWebUtils.JSF_EXT + RoseWebUtils.constructWebQueryString(params, true);
            }
        }else if (getTaxpayerCaseProfile() != null){
            params.put(getRoseParamKeys().getTaxpayerCaseUuidParamKey(), getTaxpayerCaseProfile().getTaxpayerCaseEntity().getTaxpayerCaseUuid());
            if (getRoseUserSession().isEmployed()){
                return RoseJsfUtils.getRootWebPath()+ RoseWebUtils.BUSINESS_FOLDER + "/" + RosePageName.TaxpayerCaseMgtPage.name() + RoseWebUtils.JSF_EXT + RoseWebUtils.constructWebQueryString(params, true);
            }else{
                return RoseJsfUtils.getRootWebPath()+ RoseWebUtils.CUSTOMER_FOLDER + "/" + RosePageName.TaxpayerCaseViewPage.name() + RoseWebUtils.JSF_EXT + RoseWebUtils.constructWebQueryString(params, true);
            }
        }
        return getTargetReturnWebPath();
    }

    public String getPrintableViewPageLink(){
        HashMap<String, String> params = new HashMap<>();
        params.put(getRoseParamKeys().getRoseBillUuidParamKey(), getRequestedBillUuid());
        return RoseJsfUtils.getRootWebPath()+ RoseWebUtils.SERVICES_FOLDER + "/" 
                + RosePageName.RoseInvoicePrintablePage.name() + RoseWebUtils.JSF_EXT 
                + RoseWebUtils.constructWebQueryString(params, false);
    }

    @Override
    public List<RoseArchivedFileTypeProfile> getRoseArchivedFileTypeProfileList() {
        return new ArrayList<>();
    }

    @Override
    public List<DocumentRequirementProfile> getDocumentRequirementProfileList() {
        return new ArrayList<>();
    }

    @Override
    public List<DocumentRequirementProfile> getAllDocumentRequirementProfileList() {
        return new ArrayList<>();
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
    public List<BusinessCaseBillProfile> getBusinessCaseBillProfileListOfTargetBusinessCase() {
        if (getTaxcorpCaseProfile() != null){
            return getTaxcorpCaseProfile().getBusinessCaseBillProfileList();
        }else if (getTaxpayerCaseProfile() != null){
            return getTaxpayerCaseProfile().getBusinessCaseBillProfileList();
        }else{
            return new ArrayList<>();
        }
    }

}
