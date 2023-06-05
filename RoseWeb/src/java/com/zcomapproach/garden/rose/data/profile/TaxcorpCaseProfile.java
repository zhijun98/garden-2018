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

import com.zcomapproach.garden.persistence.constant.TaxFilingType;
import com.zcomapproach.garden.persistence.constant.TaxcorpBusinessType;
import com.zcomapproach.garden.persistence.entity.G01TaxcorpCase;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

/**
 *
 * @author zhijun98
 */
public class TaxcorpCaseProfile extends BusinessCaseProfile{
    
    private G01TaxcorpCase taxcorpCaseEntity; //the case
    private List<TaxcorpRepresentativeProfile> taxcorpRepresentativeProfileList;
    private EmployeeAccountProfile payrollTaxFilingEmployeeProfile;
    private List<TaxFilingProfile> payrollTaxFilingProfileList;
    private EmployeeAccountProfile salesTaxFilingEmployeeProfile;
    private List<TaxFilingProfile> salesTaxFilingProfileList;
    private EmployeeAccountProfile taxReturnFilingEmployeeProfile;
    private List<TaxFilingProfile> taxReturnFilingProfileList;
    
    private List<String> payrollTaxFilingPeriods;
    private List<String> salesTaxFilingPeriods;
    private List<String> taxReturnFilingPeriods;   //it possibly has both of Fiscal and Yearly

    private String currentTaxReturnFilingPeriod;

    public TaxcorpCaseProfile() {
        this.taxcorpCaseEntity = new G01TaxcorpCase();
        
        this.payrollTaxFilingPeriods = new ArrayList<>();
        this.salesTaxFilingPeriods = new ArrayList<>();
        this.taxReturnFilingPeriods  = new ArrayList<>();
        
        this.payrollTaxFilingProfileList = new ArrayList<>();
        this.salesTaxFilingProfileList = new ArrayList<>();
        this.taxReturnFilingProfileList = new ArrayList<>();
        
        this.taxcorpRepresentativeProfileList = new ArrayList<>();
        this.taxcorpRepresentativeProfileList.add(createTaxcorpRepresentativeProfile());
        
    }

    public EmployeeAccountProfile getPayrollTaxFilingEmployeeProfile() {
        if (payrollTaxFilingEmployeeProfile == null){
            payrollTaxFilingEmployeeProfile = new EmployeeAccountProfile();
        }
        return payrollTaxFilingEmployeeProfile;
    }

    public void setPayrollTaxFilingEmployeeProfile(EmployeeAccountProfile payrollTaxFilingEmployeeProfile) {
        this.payrollTaxFilingEmployeeProfile = payrollTaxFilingEmployeeProfile;
    }

    public EmployeeAccountProfile getSalesTaxFilingEmployeeProfile() {
        if (salesTaxFilingEmployeeProfile == null){
            salesTaxFilingEmployeeProfile = new EmployeeAccountProfile();
        }
        return salesTaxFilingEmployeeProfile;
    }

    public void setSalesTaxFilingEmployeeProfile(EmployeeAccountProfile salesTaxFilingEmployeeProfile) {
        this.salesTaxFilingEmployeeProfile = salesTaxFilingEmployeeProfile;
    }

    public EmployeeAccountProfile getTaxReturnFilingEmployeeProfile() {
        if (taxReturnFilingEmployeeProfile == null){
            taxReturnFilingEmployeeProfile = new EmployeeAccountProfile();
        }
        return taxReturnFilingEmployeeProfile;
    }

    public void setTaxReturnFilingEmployeeProfile(EmployeeAccountProfile taxReturnFilingEmployeeProfile) {
        this.taxReturnFilingEmployeeProfile = taxReturnFilingEmployeeProfile;
    }
    
    public boolean isReadyForTaxFiling(){
        return ZcaValidator.isNotNullEmpty(getTaxcorpCaseEntity().getTaxcorpCaseUuid());
    }

    public List<TaxFilingProfile> getNextComingPayrollTaxFilingProfileList(){
        return getNextComingTaxFilingProfileListHelper(payrollTaxFilingProfileList, payrollTaxFilingPeriods, TaxFilingType.PAYROLL_TAX);
    }

    public List<TaxFilingProfile> getNextComingSalesTaxFilingProfileList(){
        return getNextComingTaxFilingProfileListHelper(salesTaxFilingProfileList, salesTaxFilingPeriods, TaxFilingType.SALES_TAX);
    }

    public List<TaxFilingProfile> getNextComingTaxReturnFilingProfileList(){
        return getNextComingTaxFilingProfileListHelper(taxReturnFilingProfileList, taxReturnFilingPeriods, TaxFilingType.TAX_RETURN);
    }
    
    private List<TaxFilingProfile> getNextComingTaxFilingProfileListHelper(List<TaxFilingProfile> aTaxFilingProfileList, List<String> periods, TaxFilingType aTaxFilingType){
        List<TaxFilingProfile> result = new ArrayList<>();
        Collections.sort(aTaxFilingProfileList, (TaxFilingProfile o1, TaxFilingProfile o2) -> {
            try{
                return o1.getTaxFilingEntity().getDeadline().compareTo(o2.getTaxFilingEntity().getDeadline());
            }catch(Exception ex){
                return 0;
            }
        });
        
        GregorianCalendar today = new GregorianCalendar();
        switch (aTaxFilingType){
            case PAYROLL_TAX:
            case SALES_TAX:
                today.add(Calendar.DAY_OF_MONTH, -30);
                break;
            case TAX_RETURN:
                today.set(Calendar.YEAR, (today.get(Calendar.YEAR)-1));
                today.set(Calendar.MONTH, Calendar.DECEMBER);
                today.set(Calendar.DAY_OF_MONTH, 31);
                break;
        }
        for (String period : periods){
            for (TaxFilingProfile aTaxFilingProfile : aTaxFilingProfileList){
                if (!ZcaCalendar.convertToGregorianCalendar(aTaxFilingProfile.getTaxFilingEntity().getDeadline()).before(today)){
                    if (aTaxFilingProfile.getTaxFilingEntity().getTaxFilingPeriod().equalsIgnoreCase(period)){
                        result.add(aTaxFilingProfile);
                        break;
                    }
                }
            }
        }
        
        return result;
    }
    
    public boolean isValidForCalendarTax(){
       return true;
    }
    
    public boolean isValidForFiscalTax(){
        TaxcorpBusinessType businessType = TaxcorpBusinessType.convertEnumValueToType(getTaxcorpCaseEntity().getBusinessType());
        switch (businessType){
            case S_CORP:
            case LLC:
            case ESTATE_TRUST:
                return false;
            default:
                return true;
        }
    }
    
    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        super.cloneProfile(srcProfile);
        if (!(srcProfile instanceof TaxcorpCaseProfile)){
            return;
        }
        TaxcorpCaseProfile srcTaxcorpCaseProfile = (TaxcorpCaseProfile)srcProfile;
        //taxcorpCaseEntity
        G01DataUpdaterFactory.getSingleton().getG01TaxcorpCaseUpdater().cloneEntity(srcTaxcorpCaseProfile.getTaxcorpCaseEntity(), 
                                                                                         this.getTaxcorpCaseEntity());
        //taxcorpPayrollTaxFilingProfileList
        List<TaxFilingProfile> srcPayrollTaxFilingProfileList = srcTaxcorpCaseProfile.getPayrollTaxFilingProfileList();
        getPayrollTaxFilingProfileList().clear();
        TaxFilingProfile aTaxFilingProfile;
        for (TaxFilingProfile srcTaxFilingProfile : srcPayrollTaxFilingProfileList){
            aTaxFilingProfile = new TaxFilingProfile();
            aTaxFilingProfile.cloneProfile(srcTaxFilingProfile);
        }
        //taxcorpSalesTaxFilingProfileList
        List<TaxFilingProfile> srcSalesTaxFilingProfileList = srcTaxcorpCaseProfile.getSalesTaxFilingProfileList();
        getSalesTaxFilingProfileList().clear();
        for (TaxFilingProfile srcTaxFilingProfile : srcSalesTaxFilingProfileList){
            aTaxFilingProfile = new TaxFilingProfile();
            aTaxFilingProfile.cloneProfile(srcTaxFilingProfile);
        }
        //taxcorpTaxReturnFilingProfileList
        List<TaxFilingProfile> srcTaxReturnFilingProfileList = srcTaxcorpCaseProfile.getTaxReturnFilingProfileList();
        getTaxReturnFilingProfileList().clear();
        for (TaxFilingProfile srcTaxFilingProfile : srcTaxReturnFilingProfileList){
            aTaxFilingProfile = new TaxFilingProfile();
            aTaxFilingProfile.cloneProfile(srcTaxFilingProfile);
        }
        //taxcorpOwners
        List<TaxcorpRepresentativeProfile> srcTaxcorpRepresentativeProfileList = srcTaxcorpCaseProfile.getTaxcorpRepresentativeProfileList();
        getTaxcorpRepresentativeProfileList().clear();
        TaxcorpRepresentativeProfile aTaxcorpRepresentativeProfile;
        for (TaxcorpRepresentativeProfile srcTaxcorpRepresentativeProfile : srcTaxcorpRepresentativeProfileList){
            aTaxcorpRepresentativeProfile = new TaxcorpRepresentativeProfile();
            aTaxcorpRepresentativeProfile.cloneProfile(srcTaxcorpRepresentativeProfile);
            getTaxcorpRepresentativeProfileList().add(aTaxcorpRepresentativeProfile);
        }
    }
    
    public TaxcorpRepresentativeProfile addNewTaxcorpRepresentativeProfileDataEntry(){
        TaxcorpRepresentativeProfile aTaxcorpRepresentativeProfile = createTaxcorpRepresentativeProfile();
        getTaxcorpRepresentativeProfileList().add(aTaxcorpRepresentativeProfile);
        return aTaxcorpRepresentativeProfile;
    }
    
    public void deleteTaxcorpRepresentativeProfileDataEntry(String userUuid){
        if (ZcaValidator.isNullEmpty(userUuid)){
            return;
        }
        List<TaxcorpRepresentativeProfile> aTaxcorpRepresentativeProfileList = getTaxcorpRepresentativeProfileList();
        Integer index = null;
        for (int i = 0; i < aTaxcorpRepresentativeProfileList.size(); i++){
            if (userUuid.equalsIgnoreCase(aTaxcorpRepresentativeProfileList.get(i).getUserEntity().getUserUuid())){
                index = i;
                break;
            }
        }
        if (index != null){
            getTaxcorpRepresentativeProfileList().remove(index.intValue());
        }
    
    }

    public G01TaxcorpCase getTaxcorpCaseEntity() {
        return taxcorpCaseEntity;
    }

    public void setTaxcorpCaseEntity(G01TaxcorpCase taxcorpCaseEntity) {
        this.taxcorpCaseEntity = taxcorpCaseEntity;
    }

    public List<String> getPayrollTaxFilingPeriods() {
        return payrollTaxFilingPeriods;
    }

    public void setPayrollTaxFilingPeriods(List<String> payrollTaxFilingPeriods) {
        this.payrollTaxFilingPeriods = payrollTaxFilingPeriods;
    }

    public List<String> getSalesTaxFilingPeriods() {
        return salesTaxFilingPeriods;
    }

    public void setSalesTaxFilingPeriods(List<String> salesTaxFilingPeriods) {
        this.salesTaxFilingPeriods = salesTaxFilingPeriods;
    }

    public String getCurrentTaxReturnFilingPeriod() {
        return currentTaxReturnFilingPeriod;
    }

    public void setCurrentTaxReturnFilingPeriod(String currentTaxReturnFilingPeriod) {
        this.currentTaxReturnFilingPeriod = currentTaxReturnFilingPeriod;
    }

    public List<String> getTaxReturnFilingPeriods() {
        return taxReturnFilingPeriods;
    }

    public void setTaxReturnFilingPeriods(List<String> taxReturnFilingPeriods) {
        this.taxReturnFilingPeriods = taxReturnFilingPeriods;
    }

    public List<TaxFilingProfile> getPayrollTaxFilingProfileList() {
        return payrollTaxFilingProfileList;
    }

    public void setPayrollTaxFilingProfileList(List<TaxFilingProfile> payrollTaxFilingProfileList) {
        this.payrollTaxFilingProfileList = payrollTaxFilingProfileList;
    }

    public List<TaxFilingProfile> getSalesTaxFilingProfileList() {
        return salesTaxFilingProfileList;
    }

    public void setSalesTaxFilingProfileList(List<TaxFilingProfile> salesTaxFilingProfileList) {
        this.salesTaxFilingProfileList = salesTaxFilingProfileList;
    }

    public List<TaxFilingProfile> getTaxReturnFilingProfileList() {
        return taxReturnFilingProfileList;
    }

    public void setTaxReturnFilingProfileList(List<TaxFilingProfile> taxReturnFilingProfileList) {
        this.taxReturnFilingProfileList = taxReturnFilingProfileList;
    }
    
    public List<TaxcorpRepresentativeProfile> getTaxcorpRepresentativeProfileList() {
        return taxcorpRepresentativeProfileList;
    }

    public void setTaxcorpRepresentativeProfileList(List<TaxcorpRepresentativeProfile> taxcorpRepresentativeProfileList) {
        this.taxcorpRepresentativeProfileList = taxcorpRepresentativeProfileList;
    }

    @Override
    public String getProfileName() {
        String result = "";
        if (ZcaValidator.isNotNullEmpty(taxcorpCaseEntity.getCorporateName())){
            result += taxcorpCaseEntity.getCorporateName();
            if (ZcaValidator.isNotNullEmpty(taxcorpCaseEntity.getEinNumber())){
                result += " (" + taxcorpCaseEntity.getEinNumber() + ")";
            }
        }
        return result;
    }

    @Override
    public String getProfileDescriptiveName() {
        if (ZcaValidator.isNullEmpty(getProfileName())){
            return getCustomerProfile().getProfileDescriptiveName();
        }else{
            return getProfileName() + ": " + getCustomerProfile().getProfileDescriptiveName();
        }
    }

    @Override
    protected String getProfileUuid() {
        return taxcorpCaseEntity.getTaxcorpCaseUuid();
    }

    private TaxcorpRepresentativeProfile createTaxcorpRepresentativeProfile() {
        TaxcorpRepresentativeProfile aTaxcorpRepresentativeProfile = new TaxcorpRepresentativeProfile();
        
        aTaxcorpRepresentativeProfile.getUserEntity().setUserUuid(GardenData.generateUUIDString());
        aTaxcorpRepresentativeProfile.getUserContactInfoProfileList().add(new RoseContactInfoProfile());
        //aTaxcorpRepresentativeProfile.getUserLocationProfileList().add(new RoseLocationProfile());
        
        return aTaxcorpRepresentativeProfile;
    }

}
