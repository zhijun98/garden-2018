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

import com.zcomapproach.garden.persistence.constant.GardenDiscountType;
import com.zcomapproach.garden.persistence.entity.G01Bill;
import com.zcomapproach.garden.persistence.entity.G01Payment;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.garden.rose.data.profile.BusinessCaseBillProfile;
import com.zcomapproach.garden.rose.data.profile.BusinessCasePaymentProfile;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Beans which support specific web pages
 * @author zhijun98
 */
public abstract class AbstractBusinessCaseViewBean extends AbstractRoseViewBean implements IBusinessBillPaymentInvoiceBean{
    
    private boolean displayBusinessCaseBillDataEntryPanelDemanded;
    private BusinessCaseBillProfile targetBusinessCaseBillProfile;
    private boolean displayBusinessCasePaymentDataEntryPanelDemanded;
    private BusinessCasePaymentProfile targetBusinessCasePaymentProfile;
    
    public abstract void assignSelectedEmployeeProfileToTargetCase();

    public BusinessCaseBillProfile getTargetBusinessCaseBillProfile() {
        if (targetBusinessCaseBillProfile == null){
            targetBusinessCaseBillProfile = new BusinessCaseBillProfile();
        }
        return targetBusinessCaseBillProfile;
    }

    public void setTargetBusinessCaseBillProfile(BusinessCaseBillProfile targetBusinessCaseBillProfile) {
        this.targetBusinessCaseBillProfile = targetBusinessCaseBillProfile;
    }

    public BusinessCasePaymentProfile getTargetBusinessCasePaymentProfile() {
        if (targetBusinessCasePaymentProfile == null){
            targetBusinessCasePaymentProfile = new BusinessCasePaymentProfile();
        }
        return targetBusinessCasePaymentProfile;
    }

    public void setTargetBusinessCasePaymentProfile(BusinessCasePaymentProfile targetBusinessCasePaymentProfile) {
        this.targetBusinessCasePaymentProfile = targetBusinessCasePaymentProfile;
    }

    @Override
    public boolean isDisplayBusinessCaseBillDataEntryPanelDemanded() {
        return displayBusinessCaseBillDataEntryPanelDemanded;
    }

    @Override
    public boolean isDisplayBusinessCasePaymentDataEntryPanelDemanded() {
        return displayBusinessCasePaymentDataEntryPanelDemanded;
    }
    
    @Override
    public void displayBusinessCaseBillDataEntryPanel(){
        displayBusinessCaseBillDataEntryPanelDemanded = true;
        targetBusinessCaseBillProfile = new BusinessCaseBillProfile();
        hideBusinessCasePaymentDataEntryPanel();
    }
    
    private BusinessCaseBillProfile findBusinessCaseBillProfile(String billUuid, List<BusinessCaseBillProfile> aBusinessCaseBillProfileList){
        for (BusinessCaseBillProfile aBusinessCaseBillProfile : aBusinessCaseBillProfileList){
            if (aBusinessCaseBillProfile.getBillEntity().getBillUuid().equalsIgnoreCase(billUuid)){
                return aBusinessCaseBillProfile;
            }
        }
        return null;
    }
    
    @Override
    public void displayBusinessCaseBillDataEntryPanelForEdit(String billUuid){
        if (ZcaValidator.isNullEmpty(billUuid)){
            return;
        }
        BusinessCaseBillProfile aBusinessCaseBillProfile = findBusinessCaseBillProfile(billUuid, 
                                                                                       getBusinessCaseBillProfileListOfTargetBusinessCase());
        if (aBusinessCaseBillProfile != null){
            targetBusinessCaseBillProfile = aBusinessCaseBillProfile;
            displayBusinessCaseBillDataEntryPanelDemanded = true;
            hideBusinessCasePaymentDataEntryPanel();
        }
    }
    
    @Override
    public void hideBusinessCaseBillDataEntryPanel(){
        displayBusinessCaseBillDataEntryPanelDemanded = false;
    }
    
    /**
     * Display businessCasePaymentDataEntryPanel for adding a new payment entry 
     * @param billUuid 
     */
    @Override
    public void displayBusinessCasePaymentDataEntryPanel(String billUuid){
        if (ZcaValidator.isNullEmpty(billUuid)){
            return;
        }
        BusinessCaseBillProfile aBusinessCaseBillProfile = findBusinessCaseBillProfile(billUuid, 
                                                                                       getBusinessCaseBillProfileListOfTargetBusinessCase());
        if (aBusinessCaseBillProfile != null){
            targetBusinessCaseBillProfile = aBusinessCaseBillProfile;
            targetBusinessCasePaymentProfile = new BusinessCasePaymentProfile();
            targetBusinessCasePaymentProfile.getPaymentEntity().setBillUuid(billUuid);
            displayBusinessCasePaymentDataEntryPanelDemanded = true;
            hideBusinessCaseBillDataEntryPanel();
        }
    }

    @Override
    public void displayBusinessCasePaymentDataEntryPanelForEdit(String billUuid, String paymentUuid) {
        displayBusinessCasePaymentDataEntryPanel(billUuid);
        if (ZcaValidator.isNotNullEmpty(paymentUuid)){
            List<BusinessCasePaymentProfile> aBusinessCasePaymentProfileList = targetBusinessCaseBillProfile.getBusinessCasePaymentProfileList();
            for (BusinessCasePaymentProfile aBusinessCasePaymentProfile : aBusinessCasePaymentProfileList){
                if (paymentUuid.equalsIgnoreCase(aBusinessCasePaymentProfile.getPaymentEntity().getPaymentUuid())){
                    targetBusinessCasePaymentProfile = aBusinessCasePaymentProfile;
                    break;
                }
            }//for
        }
    }
    
    @Override
    public void hideBusinessCasePaymentDataEntryPanel(){
        displayBusinessCasePaymentDataEntryPanelDemanded = false;
    }
    
    @Override
    public void saveTargetBusinessCaseBillProfile(){
        BusinessCaseBillProfile aRoseBillProfile = getTargetBusinessCaseBillProfile();
        aRoseBillProfile.getBillEntity().setEntityType(getEntityType().name());
        aRoseBillProfile.getBillEntity().setEntityUuid(getRequestedEntityUuid());
        aRoseBillProfile.getBillEntity().setEmployeeUuid(getRoseUserSession().getTargetAccountProfile().getAccountEntity().getAccountUuid());
        try {
            G01Bill aG01Bill = aRoseBillProfile.getBillEntity();
            if (ZcaValidator.isNullEmpty(aG01Bill.getBillUuid())){
                aG01Bill.setBillUuid(GardenData.generateUUIDString());
            }
            if (aG01Bill.getBillTotal() == null){
                throw new Exception(RoseText.getText("Price") + " - " + RoseText.getText("FieldRequired_T"));
            }
            if (ZcaValidator.isNullEmpty(aG01Bill.getBillDiscountType())){
                aG01Bill.setBillDiscountType(GardenDiscountType.PERCENTAGE.value());
            }
            if (ZcaValidator.isNotNullEmpty(aG01Bill.getBillContent())){
                if (aG01Bill.getBillContent().length() > 450){
                    throw new Exception(RoseText.getText("Description") + " - " + RoseText.getText("MaxLengthRequired_T") + ": 450");
                }
            }
            if (aG01Bill.getBillDatetime() == null){
                if (aG01Bill.getCreated() == null){
                    aG01Bill.setBillDatetime(new Date());
                }else{
                    aG01Bill.setBillDatetime(aG01Bill.getCreated());
                }
            }
            getBusinessEJB().storeEntityByUuid(G01Bill.class, aG01Bill, aG01Bill.getBillUuid(), 
                    G01DataUpdaterFactory.getSingleton().getG01BillUpdater());
            
            //add into the existing list
            BusinessCaseBillProfile aBusinessCaseBillProfile = findBusinessCaseBillProfile(aG01Bill.getBillUuid(), 
                                                                                       getBusinessCaseBillProfileListOfTargetBusinessCase());
            if (aBusinessCaseBillProfile == null){
                getBusinessCaseBillProfileListOfTargetBusinessCase().add(aRoseBillProfile);
            }
            
            //clean the target and close the panel
            this.setTargetBusinessCaseBillProfile(new BusinessCaseBillProfile());
            this.setTargetBusinessCasePaymentProfile(new BusinessCasePaymentProfile());
            this.hideBusinessCaseBillDataEntryPanel();
            
            RoseJsfUtils.setGlobalSuccessfulOperationMessage();
        } catch (Exception ex) {
            //Logger.getLogger(AbstractBusinessCaseViewBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalSystemErrorFacesMessage(ex.getMessage());
        }
    }

    @Override
    public void deleteBusinessCaseBillProfile(String billUuid) {
        List<BusinessCaseBillProfile> aBusinessCaseBillProfileList = getBusinessCaseBillProfileListOfTargetBusinessCase();
        BusinessCaseBillProfile aBusinessCaseBillProfile = findBusinessCaseBillProfile(billUuid, 
                                                                                       aBusinessCaseBillProfileList);
        try {
            getBusinessEJB().deleteBusinessCaseBillProfile(aBusinessCaseBillProfile);
        
            aBusinessCaseBillProfileList.remove(aBusinessCaseBillProfile);
            this.setTargetBusinessCaseBillProfile(new BusinessCaseBillProfile());
            this.setTargetBusinessCasePaymentProfile(new BusinessCasePaymentProfile());
        } catch (Exception ex) {
            //Logger.getLogger(AbstractBusinessCaseViewBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalSystemErrorFacesMessage(ex.getMessage());
        }
    }
    
    @Override
    public void deleteBusinessCasePaymentProfile(String billUuid, String paymentUuid){
        try {
            BusinessCaseBillProfile aBusinessCaseBillProfile = findBusinessCaseBillProfile(billUuid, 
                                                                                           getBusinessCaseBillProfileListOfTargetBusinessCase());
            if (aBusinessCaseBillProfile != null){
                List<BusinessCasePaymentProfile> aBusinessCasePaymentProfileList = aBusinessCaseBillProfile.getBusinessCasePaymentProfileList();
                Integer index = null;
                G01Payment aG01Payment;
                for (int i = 0; i < aBusinessCasePaymentProfileList.size(); i++){
                    aG01Payment = aBusinessCasePaymentProfileList.get(i).getPaymentEntity();
                    if (aG01Payment.getPaymentUuid().equalsIgnoreCase(paymentUuid)){
                        index = i;
                        //delete entity here...
                        getBusinessEJB().deleteEntityByUuid(G01Payment.class, paymentUuid);
                        break;
                    }
                }
                if (index != null){
                    aBusinessCasePaymentProfileList.remove(index.intValue());
                }
                RoseJsfUtils.setGlobalSuccessfulOperationMessage();
            }
        } catch (Exception ex) {
            //Logger.getLogger(AbstractBusinessCaseViewBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalSystemErrorFacesMessage(ex.getMessage());
        }
    }
    
    @Override
    public void saveTargetBusinessCasePaymentProfile(){
        BusinessCasePaymentProfile aRosePaymentProfile = this.getTargetBusinessCasePaymentProfile();
        aRosePaymentProfile.getPaymentEntity().setEmployeeUuid(getRoseUserSession().getTargetAccountProfile().getAccountEntity().getAccountUuid());
        try {
            //validate...
            G01Payment aG01Payment = aRosePaymentProfile.getPaymentEntity();
            if (ZcaValidator.isNullEmpty(aG01Payment.getPaymentUuid())){
                aG01Payment.setPaymentUuid(GardenData.generateUUIDString());
                //a new payment item
                this.getTargetBusinessCaseBillProfile().getBusinessCasePaymentProfileList().add(aRosePaymentProfile);
            }
            if (ZcaValidator.isNullEmpty(aG01Payment.getBillUuid())){
                throw new Exception("Cannot save this payment without its bill information");
            }
            if (ZcaValidator.isNullEmpty(aG01Payment.getPaymentType())){
                throw new Exception(RoseText.getText("PaymentMethod") + " - " + RoseText.getText("FieldRequired_T"));
            }
            if (ZcaValidator.isNotNullEmpty(aG01Payment.getPaymentMemo())){
                if (aG01Payment.getPaymentMemo().length() > 450){
                    throw new Exception(RoseText.getText("Memo") + " - " + RoseText.getText("MaxLengthRequired_T") + ": 450");
                }
            }
            if (aG01Payment.getPaymentPrice() == null){
                throw new Exception(RoseText.getText("Paid") + " - " + RoseText.getText("FieldRequired_T"));
            }
            
            //save...
            aRosePaymentProfile = getBusinessEJB().storeBusinessCasePaymentProfile(aRosePaymentProfile);
            
            //clean the target and close the panel
            this.setTargetBusinessCaseBillProfile(new BusinessCaseBillProfile());
            this.setTargetBusinessCasePaymentProfile(new BusinessCasePaymentProfile());
            this.hideBusinessCasePaymentDataEntryPanel();
            
            RoseJsfUtils.setGlobalSuccessfulOperationMessage();
        } catch (Exception ex) {
            Logger.getLogger(TaxcorpCaseViewBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalSystemErrorFacesMessage(ex.getMessage());
        }
    }
}
