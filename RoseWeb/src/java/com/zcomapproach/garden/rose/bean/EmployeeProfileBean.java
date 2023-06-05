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

import com.zcomapproach.garden.persistence.entity.G01Employee;
import com.zcomapproach.garden.persistence.entity.G01TaxcorpCase;
import com.zcomapproach.garden.persistence.entity.G01TaxcorpCaseBk;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.garden.rose.data.profile.EmployeeAccountProfile;
import com.zcomapproach.garden.rose.data.profile.RoseAccountProfile;
import com.zcomapproach.garden.rose.data.profile.TaxpayerCaseConciseProfile;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.commons.ZcaValidator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author zhijun98
 */
@Named(value = "employeeProfileBean")
@ViewScoped
public class EmployeeProfileBean extends RoseAccountProfileBean {

    /**
     * Taxpayer tasks for this target employee
     */
    private List<TaxpayerCaseConciseProfile> employeeTaxpayerCaseEntityList;
    
    /**
     * Taxcorp tasks for this target employee
     */
    private List<G01TaxcorpCase> employeeTaxcorpCaseEntityList;
    
    private List<G01TaxcorpCaseBk> removedTaxcorpCaseEntityList;
    
    @Override
    public String deleteTargetTaxpayerCaseProfile(String taxpayerCaseUuid){
        if (ZcaValidator.isNullEmpty(taxpayerCaseUuid)){
            return null;
        }
        String result = super.deleteTargetTaxpayerCaseProfile(taxpayerCaseUuid);
        Integer index = null;
        int count = 0;
        for (TaxpayerCaseConciseProfile aTaxpayerCaseConciseProfile : employeeTaxpayerCaseEntityList){
            if (taxpayerCaseUuid.equalsIgnoreCase(aTaxpayerCaseConciseProfile.getTaxpayerCase().getTaxpayerCaseUuid())){
                index = count;
                break;
            }
            count++;
        }
        if (index != null){
            employeeTaxpayerCaseEntityList.remove(index.intValue());
        }
        return result;
    }
    
    @Override
    public String deleteTargetTaxcorpCaseProfile(String taxcorpCaseUuid){
        if (ZcaValidator.isNullEmpty(taxcorpCaseUuid)){
            return null;
        }
        String result = super.deleteTargetTaxcorpCaseProfile(taxcorpCaseUuid);
        Integer index = null;
        int count = 0;
        for (G01TaxcorpCase aG01TaxcorpCase : employeeTaxcorpCaseEntityList){
            if (taxcorpCaseUuid.equalsIgnoreCase(aG01TaxcorpCase.getTaxcorpCaseUuid())){
                index = count;
                break;
            }
            count++;
        }
        if (index != null){
            employeeTaxcorpCaseEntityList.remove(index.intValue());
        }
        return result;
    }

    public List<TaxpayerCaseConciseProfile> getEmployeeTaxpayerCaseEntityList() {
        if (employeeTaxpayerCaseEntityList == null){
            employeeTaxpayerCaseEntityList = getTaxpayerEJB().findTaxpayerCaseConciseProfileListByEmployeeUuid(getTargetAccountProfile().getAccountEntity().getAccountUuid());
        }
        return employeeTaxpayerCaseEntityList;
    }

    public List<G01TaxcorpCase> getEmployeeTaxcorpCaseEntityList() {
        if (employeeTaxcorpCaseEntityList == null){
            employeeTaxcorpCaseEntityList = getTaxcorpEJB().findTaxcorpCaseTaskListByEmployeeUuid(getTargetAccountProfile().getAccountEntity().getAccountUuid());
        }
        return employeeTaxcorpCaseEntityList;
    }

    public List<G01TaxcorpCaseBk> getRemovedTaxcorpCaseEntityList() {
        if (removedTaxcorpCaseEntityList == null){
            removedTaxcorpCaseEntityList = getTaxcorpEJB().findAll(G01TaxcorpCaseBk.class);
            if ((removedTaxcorpCaseEntityList != null) && (!removedTaxcorpCaseEntityList.isEmpty())){
                Collections.sort(removedTaxcorpCaseEntityList, (G01TaxcorpCaseBk o1, G01TaxcorpCaseBk o2) -> {
                    try{
                        return o1.getUpdated().compareTo(o2.getUpdated())*(-1);
                    }catch (Exception ex){
                        return 0;
                    }
                });
            }
        }
        return removedTaxcorpCaseEntityList;
    }
    
    public void rollbackTaxcorpCaseEntity(String taxcorpCaseUuid){
        if (ZcaValidator.isNullEmpty(taxcorpCaseUuid)){
            return;
        }
        G01TaxcorpCaseBk aG01TaxcorpCaseBk = getTaxcorpEJB().findEntityByUuid(G01TaxcorpCaseBk.class, taxcorpCaseUuid);
        if (aG01TaxcorpCaseBk != null){
            G01TaxcorpCase aG01TaxcorpCase = new G01TaxcorpCase();
            aG01TaxcorpCase.setTaxcorpCaseUuid(aG01TaxcorpCaseBk.getTaxcorpCaseUuid());
            aG01TaxcorpCase.setAgreementSignature(aG01TaxcorpCaseBk.getAgreementSignature());
            aG01TaxcorpCase.setAgreementSignatureTimestamp(aG01TaxcorpCaseBk.getAgreementSignatureTimestamp());
            aG01TaxcorpCase.setAgreementUuid(aG01TaxcorpCaseBk.getAgreementUuid());
            aG01TaxcorpCase.setBusinessPurpose(aG01TaxcorpCaseBk.getBusinessPurpose());
            aG01TaxcorpCase.setBusinessStatus(aG01TaxcorpCaseBk.getBusinessStatus());
            aG01TaxcorpCase.setBusinessType(aG01TaxcorpCaseBk.getBusinessType());
            aG01TaxcorpCase.setBankAccountNumber(aG01TaxcorpCaseBk.getBankAccountNumber());
            aG01TaxcorpCase.setBankRoutingNumber(aG01TaxcorpCaseBk.getBankRoutingNumber());
            aG01TaxcorpCase.setCorporateName(aG01TaxcorpCaseBk.getCorporateName());
            aG01TaxcorpCase.setCorporateEmail(aG01TaxcorpCaseBk.getCorporateEmail());
            aG01TaxcorpCase.setCorporateFax(aG01TaxcorpCaseBk.getCorporateFax());
            aG01TaxcorpCase.setCorporatePhone(aG01TaxcorpCaseBk.getCorporatePhone());
            aG01TaxcorpCase.setCorporateWebPresence(aG01TaxcorpCaseBk.getCorporateWebPresence());
            aG01TaxcorpCase.setCustomerAccountUuid(aG01TaxcorpCaseBk.getCustomerAccountUuid());
            aG01TaxcorpCase.setEmployeeAccountUuid(aG01TaxcorpCaseBk.getEmployeeAccountUuid());
            aG01TaxcorpCase.setDosDate(aG01TaxcorpCaseBk.getDosDate());
            aG01TaxcorpCase.setEinNumber(aG01TaxcorpCaseBk.getEinNumber());
            aG01TaxcorpCase.setMemo(aG01TaxcorpCaseBk.getMemo());
            aG01TaxcorpCase.setTaxcorpCountry(aG01TaxcorpCaseBk.getTaxcorpCountry());
            aG01TaxcorpCase.setTaxcorpAddress(aG01TaxcorpCaseBk.getTaxcorpAddress());
            aG01TaxcorpCase.setTaxcorpCity(aG01TaxcorpCaseBk.getTaxcorpCity());
            aG01TaxcorpCase.setTaxcorpStateCounty(aG01TaxcorpCaseBk.getTaxcorpStateCounty());
            aG01TaxcorpCase.setTaxcorpState(aG01TaxcorpCaseBk.getTaxcorpState());
            aG01TaxcorpCase.setTaxcorpZip(aG01TaxcorpCaseBk.getTaxcorpZip());
            aG01TaxcorpCase.setTaxcorpCountry(aG01TaxcorpCaseBk.getTaxcorpCountry());
            aG01TaxcorpCase.setLatestLogUuid(aG01TaxcorpCaseBk.getLatestLogUuid());
            aG01TaxcorpCase.setEntityStatus(aG01TaxcorpCaseBk.getEntityStatus());
            aG01TaxcorpCase.setCreated(aG01TaxcorpCaseBk.getCreated());
            aG01TaxcorpCase.setUpdated(aG01TaxcorpCaseBk.getUpdated());
            
            try {
                getTaxcorpEJB().storeEntityByUuid(G01TaxcorpCase.class, aG01TaxcorpCase, aG01TaxcorpCase.getTaxcorpCaseUuid(), G01DataUpdaterFactory.getSingleton().getG01TaxcorpCaseUpdater());
                removedTaxcorpCaseEntityList.remove(aG01TaxcorpCaseBk);
            } catch (Exception ex) {
                //Logger.getLogger(EmployeeProfileBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public String getRequestedEmployeeAccountUuid() {
        return getRequestUserUuid();
    }

    public void setRequestedEmployeeAccountUuid(String requestedEmployeeAccountUuid) {
        setRequestUserUuid(requestedEmployeeAccountUuid);
        if (ZcaValidator.isNotNullEmpty(requestedEmployeeAccountUuid)){
            RoseAccountProfile aRoseAccountProfile = getBusinessEJB().findRoseAccountProfileByAccountUserUuid(requestedEmployeeAccountUuid);
            if (aRoseAccountProfile != null){
                checkUserRedundancy(aRoseAccountProfile);
            }
            super.setTargetAccountProfile(aRoseAccountProfile);
        }
    }
    
    @Override
    public String getRosePageTopic() {
        return RoseText.getText("EmployeeProfile");
    }
    
    public EmployeeAccountProfile getTargetEmployeeAccountProfile(){
        return (EmployeeAccountProfile)super.getTargetAccountProfile();
    }
    
    @Override
    protected RoseAccountProfile createRoseAccountProfileInstance(){
        return new EmployeeAccountProfile();
    }
    
    public void deleteEmployeeAccountProfile(String employeeAccountUuid){
        if (!getRoseSettings().getBusinessOwnerProfile().getAccountEntity().getAccountUuid().equalsIgnoreCase(employeeAccountUuid)){
            try {
                //only delete the G01Employee
                getBusinessEJB().deleteEmployee(employeeAccountUuid);
            } catch (Exception ex) {
                //Logger.getLogger(EmployeeProfileBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
