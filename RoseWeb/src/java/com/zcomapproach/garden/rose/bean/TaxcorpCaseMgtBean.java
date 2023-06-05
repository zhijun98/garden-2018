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

import com.zcomapproach.garden.persistence.constant.TaxFilingPeriod;
import com.zcomapproach.garden.persistence.constant.TaxFilingType;
import com.zcomapproach.garden.persistence.entity.G01TaxFiling;
import com.zcomapproach.garden.persistence.entity.G01TaxFilingType;
import com.zcomapproach.garden.persistence.entity.G01TaxFilingTypePK;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.garden.rose.RosePageName;
import com.zcomapproach.garden.rose.data.RoseWebParamValue;
import com.zcomapproach.garden.rose.data.profile.EmployeeAccountProfile;
import com.zcomapproach.garden.rose.data.profile.TaxFilingProfile;
import com.zcomapproach.garden.rose.data.profile.TaxFilingTypeProfile;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.garden.rose.util.RoseWebUtils;
import com.zcomapproach.garden.taxation.TaxationDeadline;
import com.zcomapproach.garden.taxation.TaxationSettings;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author zhijun98
 */
@Named(value = "taxcorpCaseMgtBean")
@ViewScoped
public class TaxcorpCaseMgtBean extends TaxcorpCaseBean{
    
    private boolean taxFilingDataEntryPanelPresented;
    private TaxFilingProfile targetNextComingTaxFilingProfile;
    @PersistenceContext(unitName = "RosePU")
    private EntityManager em;
    @Resource
    private javax.transaction.UserTransaction utx;

    public TaxcorpCaseMgtBean() {
    }

    public boolean isTaxFilingDataEntryPanelPresented() {
        return taxFilingDataEntryPanelPresented;
    }

    public void setTaxFilingDataEntryPanelPresented(boolean taxFilingDataEntryPanelPresented) {
        this.taxFilingDataEntryPanelPresented = taxFilingDataEntryPanelPresented;
    }

    public TaxFilingProfile getTargetNextComingTaxFilingProfile() {
        return targetNextComingTaxFilingProfile;
    }

    public void setTargetNextComingTaxFilingProfile(TaxFilingProfile targetNextComingTaxFilingProfile) {
        this.targetNextComingTaxFilingProfile = targetNextComingTaxFilingProfile;
    }
    
    public void editNextComingPayrollTaxFilingProfile(String taxFilingUuid){
        editNextComingTaxFilingProfileHelper(getTargetTaxcorpCaseProfile().getNextComingPayrollTaxFilingProfileList(), taxFilingUuid);
    }
    
    public void editNextComingSalesTaxFilingProfile(String taxFilingUuid){
        editNextComingTaxFilingProfileHelper(getTargetTaxcorpCaseProfile().getNextComingSalesTaxFilingProfileList(), taxFilingUuid);
    }
    
    public void editNextComingTaxReturnFilingProfile(String taxFilingUuid){
        editNextComingTaxFilingProfileHelper(getTargetTaxcorpCaseProfile().getNextComingTaxReturnFilingProfileList(), taxFilingUuid);
    }

    private void editNextComingTaxFilingProfileHelper(List<TaxFilingProfile> aTaxFilingProfileList, String taxFilingUuid){
        targetNextComingTaxFilingProfile = null; 
        for (TaxFilingProfile aTaxFilingProfile : aTaxFilingProfileList){
            if (aTaxFilingProfile.getTaxFilingEntity().getTaxFilingUuid().equalsIgnoreCase(taxFilingUuid)){
                targetNextComingTaxFilingProfile = aTaxFilingProfile;
                break;
            }
        }
        if (targetNextComingTaxFilingProfile != null){
            setTaxFilingDataEntryPanelPresented(true);
        }
    }
    
    public void editTaxcorpTaxFilingProfile(String taxFilingUuid){
        editNextComingTaxFilingProfileHelper(getTargetTaxFilingProfileListHelper(), taxFilingUuid);
    }
    
    public void closeRoseTaxFilingDataEntryPanel(){
        targetNextComingTaxFilingProfile = null;
        setTaxFilingDataEntryPanelPresented(false);
    }
    
    public void storeTargetNextComingTaxFilingProfile(){
        try {
            getTaxcorpEJB().storeEntityByUuid(G01TaxFiling.class, targetNextComingTaxFilingProfile.getTaxFilingEntity(),
                    targetNextComingTaxFilingProfile.getTaxFilingEntity().getTaxFilingUuid(), G01DataUpdaterFactory.getSingleton().getG01TaxFilingUpdater());
//            setWebMessage(RoseText.getText("OperationSucceeded_T") + ": " 
//                    + ZcaCalendar.convertToMMddyyyyHHmmss(new Date(), "-", " @ ", ":"));
            RoseJsfUtils.setGlobalSuccessfulOperationMessage();
        } catch (Exception ex) {
//            setWebMessage(RoseText.getText("SystemError") + ": " + ex.getMessage());
            RoseJsfUtils.setGlobalFailedOperationMessage(ex.getMessage());
        }
    }
    
    @Override
    public String getRosePageTopic() {
        if (isForCreateNewEntity()){
            return RoseText.getText("StartNewCorporateTaxFiling");
        }else{
            return RoseText.getText("CorporateTaxFiling");
        }
    }
    
    public boolean isForPayrollTax(){
        return RoseWebParamValue.PAYROLL_TAX.value().equalsIgnoreCase(getRequestedViewPurpose());
    }
    
    public boolean isForSalesTax(){
        return RoseWebParamValue.SALES_TAX.value().equalsIgnoreCase(getRequestedViewPurpose());
    }
    
    public boolean isForTaxReturn(){
        return RoseWebParamValue.TAX_RETURN.value().equalsIgnoreCase(getRequestedViewPurpose());
    }
    
    private EmployeeAccountProfile selectedEmployeeProfileForTaxFilingAssignment;

    public EmployeeAccountProfile getSelectedEmployeeProfileForTaxFilingAssignment() {
        if (selectedEmployeeProfileForTaxFilingAssignment == null){
            initializeSelectedEmployeeProfileForTaxFilingAssignment();
        }
        return selectedEmployeeProfileForTaxFilingAssignment;
    }

    private void initializeSelectedEmployeeProfileForTaxFilingAssignment() {
        List<G01TaxFilingType> aG01TaxFilingTypeList = getTaxcorpEJB().findTaxFilingTypeEntityListByType(
                this.getTargetTaxcorpCaseProfile().getTaxcorpCaseEntity().getTaxcorpCaseUuid(), 
                getTargetTaxFilingType());
        if ((aG01TaxFilingTypeList == null) || (aG01TaxFilingTypeList.isEmpty())){
            selectedEmployeeProfileForTaxFilingAssignment = super.getSelectedEmployeeProfileForCaseAssignment();
        }else{
            Collections.sort(aG01TaxFilingTypeList, (G01TaxFilingType o1, G01TaxFilingType o2) -> o1.getUpdated().compareTo(o2.getUpdated())*(-1));
            String employeeUuid = null;
            for (G01TaxFilingType aG01TaxFilingType : aG01TaxFilingTypeList){
                employeeUuid = aG01TaxFilingType.getEmployeeAccountUuid();
                if (ZcaValidator.isNotNullEmpty(employeeUuid)){
                    selectedEmployeeProfileForTaxFilingAssignment = getBusinessEJB().findEmployeeAccountProfileByAccountUserUuid(employeeUuid);
                    if (selectedEmployeeProfileForTaxFilingAssignment != null){
                        break;
                    }
                }
            }
            if (selectedEmployeeProfileForTaxFilingAssignment == null){
                selectedEmployeeProfileForTaxFilingAssignment = super.getSelectedEmployeeProfileForCaseAssignment();
            }
        }
    }

    public void setSelectedEmployeeProfileForTaxFilingAssignment(EmployeeAccountProfile selectedEmployeeProfileForTaxFilingAssignment) {
        this.selectedEmployeeProfileForTaxFilingAssignment = selectedEmployeeProfileForTaxFilingAssignment;
    }
    
    public void assignSelectedEmployeeProfileToTargetTaxFiling(){
        List<G01TaxFilingType> aG01TaxFilingTypeList = getTaxcorpEJB().findTaxFilingTypeEntityListByType(
                this.getTargetTaxcorpCaseProfile().getTaxcorpCaseEntity().getTaxcorpCaseUuid(), 
                getTargetTaxFilingType());
        for (G01TaxFilingType aG01TaxFilingType : aG01TaxFilingTypeList){
            aG01TaxFilingType.setEmployeeAccountUuid(getSelectedEmployeeProfileForTaxFilingAssignment().getEmployeeEntity().getEmployeeAccountUuid());
            try {
                getTaxcorpEJB().storeEntityByUuid(G01TaxFilingType.class, aG01TaxFilingType, aG01TaxFilingType.getG01TaxFilingTypePK(), G01DataUpdaterFactory.getSingleton().getG01TaxFilingTypeUpdater());
                RoseJsfUtils.setGlobalSuccessfulOperationMessage();
            }catch (Exception ex) {
                //Logger.getLogger(TaxcorpCaseMgtBean.class.getName()).log(Level.SEVERE, null, ex);
                RoseJsfUtils.setGlobalFailedOperationMessage(ex.getMessage());
            }
        }
    }

    private TaxFilingType getTargetTaxFilingType() {
        RoseWebParamValue type = RoseWebParamValue.convertEnumValueToType(getRequestedViewPurpose());
        switch (type){
            case PAYROLL_TAX:
                return TaxFilingType.PAYROLL_TAX;
            case SALES_TAX:
                return TaxFilingType.SALES_TAX;
            case TAX_RETURN:
                return TaxFilingType.TAX_RETURN;
            default:
                return TaxFilingType.UNKNOWN;
        }
    }
    
    public String getTaxFilingTypeHeader(){
        RoseWebParamValue type = RoseWebParamValue.convertEnumValueToType(getRequestedViewPurpose());
        switch (type){
            case PAYROLL_TAX:
                return RoseText.getText("PayrollTax");
            case SALES_TAX:
                return RoseText.getText("SalesTax");
            case TAX_RETURN:
                return RoseText.getText("TaxReturn");
            default:
                return RoseText.getText("Taxation");
        }
    }
    
    private List<TaxFilingProfile> getTargetTaxFilingProfileListHelper(){
        RoseWebParamValue type = RoseWebParamValue.convertEnumValueToType(getRequestedViewPurpose());
        List<TaxFilingProfile> taxFilingProfileList = new ArrayList<>();
        switch (type){
            case PAYROLL_TAX:
                taxFilingProfileList = this.getTargetTaxcorpCaseProfile().getPayrollTaxFilingProfileList();
                break;
            case SALES_TAX:
                taxFilingProfileList = this.getTargetTaxcorpCaseProfile().getSalesTaxFilingProfileList();
                break;
            case TAX_RETURN:
                taxFilingProfileList = this.getTargetTaxcorpCaseProfile().getTaxReturnFilingProfileList();
                break;
                
        }
        return taxFilingProfileList;
    }
    
    public List<TaxFilingProfile> getTargetTaxFilingProfileList(){
        return getTargetTaxFilingProfileListHelper();
    }

    private GregorianCalendar getTargetLastDeadline(TaxFilingType taxFilingType, TaxFilingPeriod taxFilingPeriod) {
        GregorianCalendar result = null;
        List<TaxFilingProfile> aTaxFilingProfileList = getTargetTaxFilingProfileListHelper();
        if (!aTaxFilingProfileList.isEmpty()){
            //sort by deadline from z to a
            Collections.sort(aTaxFilingProfileList, (TaxFilingProfile o1, TaxFilingProfile o2) -> {
                try{
                    return o1.getTaxFilingEntity().getDeadline().compareTo(o2.getTaxFilingEntity().getDeadline())*(-1);
                }catch(Exception ex){
                    return 0;
                }
            });
            for (TaxFilingProfile aTaxFilingTypeProfile : aTaxFilingProfileList){
                if (taxFilingType.value().equalsIgnoreCase(aTaxFilingTypeProfile.getTaxFilingEntity().getTaxFilingType())
                        && taxFilingPeriod.value().equalsIgnoreCase(aTaxFilingTypeProfile.getTaxFilingEntity().getTaxFilingPeriod()))
                {
                    result = ZcaCalendar.convertToGregorianCalendar(aTaxFilingTypeProfile.getTaxFilingEntity().getDeadline());
                    break;
                }
            }
        }
        return result;
    }
    
    private void removeTaxFilingProfile(String taxFilingUuid){
        List<TaxFilingProfile> taxFilingProfileList = getTargetTaxFilingProfileList();
        Integer index = null;
        for (int i = 0; i < taxFilingProfileList.size(); i++){
            if (taxFilingUuid.equalsIgnoreCase(taxFilingProfileList.get(i).getTaxFilingEntity().getTaxFilingUuid())){
                index = i;
                break;
            }
        }
        if (index != null){
            taxFilingProfileList.remove(index.intValue());
        }
    }
    
    public String deleteTaxFilingEntity(String taxFilingUuid){
        try {
            getTaxcorpEJB().deleteEntityByUuid(G01TaxFiling.class, taxFilingUuid);
            removeTaxFilingProfile(taxFilingUuid);
        } catch (Exception ex) {
            //Logger.getLogger(TaxcorpCaseMgtBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalErrorFacesMessage(ex.getMessage());
            return null;
        }
        return getTaxFilingGeneratorTargetWebPath();
    }
    
    private String getTaxFilingGeneratorTargetWebPath(){
        HashMap<String, String> params = new HashMap<>();
        params.put(getRoseParamKeys().getViewPurposeParamKey(), this.getRequestedViewPurpose());
        params.put(getRoseParamKeys().getTaxcorpCaseUuidParamKey(), this.getRequestedTaxcorpCaseUuid());
        return RosePageName.TaxFilingTypeProfileListPage.name() + RoseWebUtils.constructWebQueryString(params, true);
    }
    
    private void deleteTaxFilingDeadlines(TaxFilingPeriod aTaxFilingPeriod, List<TaxFilingProfile> aTaxFilingProfileList){
        if (!TaxFilingPeriod.UNKNOWN.equals(aTaxFilingPeriod)){
            try {
                GregorianCalendar today = new GregorianCalendar();
                List<TaxFilingProfile> deletedTaxFilingProfileList = new ArrayList<>();
                for (TaxFilingProfile aTaxFilingProfile : aTaxFilingProfileList){
                    if (aTaxFilingPeriod.value().equalsIgnoreCase(aTaxFilingProfile.getTaxFilingEntity().getTaxFilingPeriod())){
                        if (ZcaCalendar.convertToGregorianCalendar(aTaxFilingProfile.getTaxFilingEntity().getDeadline()).after(today)){
                            deletedTaxFilingProfileList.add(aTaxFilingProfile);
                        }
                    }
                }
                getTaxcorpEJB().deleteTaxFilingProfileList(deletedTaxFilingProfileList);
                RoseJsfUtils.setGlobalSuccessfulOperationMessage();
            } catch (Exception ex) {
                RoseJsfUtils.setGlobalErrorFacesMessage(ex.getMessage());
            }
        }
    }
    
    public String generatePayrollTaxDealines(){
        List<String> periods = getTargetTaxcorpCaseProfile().getPayrollTaxFilingPeriods();
        for (String period : periods){
            TaxFilingPeriod aTaxFilingPeriod = TaxFilingPeriod.convertEnumValueToType(period);
            if (!TaxFilingPeriod.UNKNOWN.equals(aTaxFilingPeriod)){
                try {
                    this.getTargetTaxcorpCaseProfile().getPayrollTaxFilingProfileList().addAll(
                            generateTaxFilingProfileList(TaxFilingType.PAYROLL_TAX, aTaxFilingPeriod));
                    RoseJsfUtils.setGlobalSuccessfulOperationMessage();
                } catch (Exception ex) {
                    RoseJsfUtils.setGlobalErrorFacesMessage(ex.getMessage());
                }
            }
        }
        return getTaxFilingGeneratorTargetWebPath();
    }
    
    public String deletePayrollTaxDealines(){
        List<String> periods = getTargetTaxcorpCaseProfile().getPayrollTaxFilingPeriods();
        for (String period : periods){
            deleteTaxFilingDeadlines(TaxFilingPeriod.convertEnumValueToType(period),
                                     getTargetTaxcorpCaseProfile().getPayrollTaxFilingProfileList());
        }
        return getTaxFilingGeneratorTargetWebPath();
    }
    
    public String generateSalesTaxDealines(){
        List<String> periods = getTargetTaxcorpCaseProfile().getSalesTaxFilingPeriods();
        for (String period : periods){
            TaxFilingPeriod aTaxFilingPeriod = TaxFilingPeriod.convertEnumValueToType(period);
            if (!TaxFilingPeriod.UNKNOWN.equals(aTaxFilingPeriod)){
                try {
                    this.getTargetTaxcorpCaseProfile().getSalesTaxFilingProfileList().addAll(
                            generateTaxFilingProfileList(TaxFilingType.SALES_TAX, aTaxFilingPeriod));
                    RoseJsfUtils.setGlobalSuccessfulOperationMessage();
                } catch (Exception ex) {
                    RoseJsfUtils.setGlobalErrorFacesMessage(ex.getMessage());
                }
            }
        }
        return getTaxFilingGeneratorTargetWebPath();
    }
    
    public String deleteSalesTaxDealines(){
        List<String> periods = getTargetTaxcorpCaseProfile().getSalesTaxFilingPeriods();
        for (String period : periods){
            deleteTaxFilingDeadlines(TaxFilingPeriod.convertEnumValueToType(period),
                                     getTargetTaxcorpCaseProfile().getSalesTaxFilingProfileList());
        }
        return getTaxFilingGeneratorTargetWebPath();
    }
    
    public String generateTaxReturnDealines(){
        TaxFilingPeriod aTaxFilingPeriod = TaxFilingPeriod.convertEnumValueToType(getTargetTaxcorpCaseProfile().getCurrentTaxReturnFilingPeriod());
        if (!TaxFilingPeriod.UNKNOWN.equals(aTaxFilingPeriod)){
            try {
                this.getTargetTaxcorpCaseProfile().getTaxReturnFilingProfileList().addAll(
                        generateTaxFilingProfileList(TaxFilingType.TAX_RETURN, aTaxFilingPeriod));
                RoseJsfUtils.setGlobalSuccessfulOperationMessage();
            } catch (Exception ex) {
                RoseJsfUtils.setGlobalErrorFacesMessage(ex.getMessage());
            }
        }
        return getTaxFilingGeneratorTargetWebPath();
    }
    
    public String deleteTaxReturnDealines(){
        List<String> periods = getTargetTaxcorpCaseProfile().getTaxReturnFilingPeriods();
        for (String period : periods){
            deleteTaxFilingDeadlines(TaxFilingPeriod.convertEnumValueToType(period),
                                     getTargetTaxcorpCaseProfile().getTaxReturnFilingProfileList());
        }
        return getTaxFilingGeneratorTargetWebPath();
    }
    
    @Override
    public void populateAccountProfileAsContactor(){
        populateAccountProfileAsContactorHelper(this.getTargetTaxcorpCaseProfile().getCustomerProfile());
    }

    private List<TaxFilingProfile> generateTaxFilingProfileList(TaxFilingType taxFilingType, TaxFilingPeriod taxFilingPeriod) throws Exception {
        if ((taxFilingType == null) || (taxFilingPeriod == null)){
            return null;
        }
        TaxFilingTypeProfile aTaxFilingTypeProfile = constructTaxFilingTypeProfile(taxFilingType, taxFilingPeriod);
        getTaxcorpEJB().storeTaxFilingTypeProfile(aTaxFilingTypeProfile);
        return aTaxFilingTypeProfile.getTaxFilingProfileList();
    }

    private TaxFilingTypeProfile constructTaxFilingTypeProfile(TaxFilingType taxFilingType, TaxFilingPeriod taxFilingPeriod) {
        G01TaxFilingType aG01TaxFilingType = new G01TaxFilingType();
        G01TaxFilingTypePK pkid = new G01TaxFilingTypePK();
        pkid.setTaxFilingPeriod(taxFilingPeriod.value());
        pkid.setTaxFilingType(taxFilingType.value());
        pkid.setTaxcorpCaseUuid(this.getTargetTaxcorpCaseProfile().getTaxcorpCaseEntity().getTaxcorpCaseUuid());
        aG01TaxFilingType.setG01TaxFilingTypePK(pkid);
        
        TaxFilingTypeProfile aTaxFilingTypeProfile = new TaxFilingTypeProfile();
        aTaxFilingTypeProfile.setTaxFilingTypeEntity(aG01TaxFilingType);
        aTaxFilingTypeProfile.setTaxFilingProfileList(constructTaxFilingProfileList(pkid.getTaxcorpCaseUuid(), taxFilingType, taxFilingPeriod));
        
        return aTaxFilingTypeProfile;
    }

    private List<TaxFilingProfile> constructTaxFilingProfileList(String taxcorpCaseUuid, TaxFilingType taxFilingType, TaxFilingPeriod taxFilingPeriod) {
        List<TaxFilingProfile> result = new ArrayList<>();
        List<Date> deadlineList = TaxationDeadline.calculate(this.getTargetTaxcorpCaseProfile().getTaxcorpCaseEntity(), 
                                                            taxFilingType, 
                                                            taxFilingPeriod, 
                                                            ZcaCalendar.convertToGregorianCalendar(getTargetTaxcorpCaseProfile().getTaxcorpCaseEntity().getDosDate()), 
                                                            getTargetLastDeadline(taxFilingType, taxFilingPeriod),
                                                            TaxationSettings.getSingleton());
        if (!deadlineList.isEmpty()){
            TaxFilingProfile aTaxFilingProfile;
            G01TaxFiling aG01TaxFiling;
            for (Date deadline : deadlineList){
                aG01TaxFiling = new G01TaxFiling();
                aG01TaxFiling.setDeadline(deadline);
                aG01TaxFiling.setTaxFilingUuid(GardenData.generateUUIDString());
                aG01TaxFiling.setTaxcorpCaseUuid(taxcorpCaseUuid);
                aG01TaxFiling.setTaxFilingType(taxFilingType.value());
                aG01TaxFiling.setTaxFilingPeriod(taxFilingPeriod.value());
                
                aTaxFilingProfile = new TaxFilingProfile();
                aTaxFilingProfile.setTaxFilingEntity(aG01TaxFiling);
                aTaxFilingProfile.setBrandNew(true);
                result.add(aTaxFilingProfile);
            }
        }
        
        return result;
    }

    public void persist(Object object) {
        try {
            utx.begin();
            em.persist(object);
            utx.commit();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
            throw new RuntimeException(e);
        }
    }

}
