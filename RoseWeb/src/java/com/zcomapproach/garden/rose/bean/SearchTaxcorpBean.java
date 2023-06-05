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
import com.zcomapproach.garden.persistence.entity.G01TaxcorpCase;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.garden.rose.RosePageName;
import com.zcomapproach.garden.rose.data.profile.TaxcorpFilingConciseProfile;
import com.zcomapproach.garden.rose.data.profile.TaxFilingProfile;
import com.zcomapproach.garden.rose.data.profile.TaxcorpBillBalanceProfile;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.garden.rose.util.RoseWebUtils;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaValidator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author zhijun98
 */
@Named(value="searchTaxcorpBean")
@SessionScoped
public class SearchTaxcorpBean extends AbstractSearchBean{
    
    private Date selectedSearchDosDateFrom;
    private Date selectedSearchDosDateTo;
    private final List<TaxcorpFilingConciseProfile> searchResultTaxcorpEntityList;
    
    private Date selectedSearchTaxFilingByDeadlineDateFrom;
    private Date selectedSearchTaxFilingByDeadlineDateTo;
    private String selectedTaxFilingType;
    private List<String> selectedTaxFilingPeriods;
    private List<TaxFilingProfile> searchResultTaxFilingProfileList;
    
    private Date selectedSearchBillBalanceDateFrom;
    private Date selectedSearchBillBalanceDateTo;
    private final List<TaxcorpBillBalanceProfile> searchResultTaxcorpBillBalanceProfileList;

    public SearchTaxcorpBean() {
        searchResultTaxcorpEntityList = new ArrayList<>();
        searchResultTaxFilingProfileList = new ArrayList<>();
        searchResultTaxcorpBillBalanceProfileList = new ArrayList<>();
    }

    public Date getSelectedSearchBillBalanceDateFrom() {
        return selectedSearchBillBalanceDateFrom;
    }

    public void setSelectedSearchBillBalanceDateFrom(Date selectedSearchBillBalanceDateFrom) {
        this.selectedSearchBillBalanceDateFrom = selectedSearchBillBalanceDateFrom;
    }

    public Date getSelectedSearchBillBalanceDateTo() {
        return selectedSearchBillBalanceDateTo;
    }

    public void setSelectedSearchBillBalanceDateTo(Date selectedSearchBillBalanceDateTo) {
        this.selectedSearchBillBalanceDateTo = selectedSearchBillBalanceDateTo;
    }

    public List<TaxcorpBillBalanceProfile> getSearchResultTaxcorpBillBalanceProfileList() {
        return searchResultTaxcorpBillBalanceProfileList;
    }

    public List<TaxcorpFilingConciseProfile> getSearchResultTaxcorpEntityList() {
        return searchResultTaxcorpEntityList;
    }

    public List<TaxFilingProfile> getSearchResultTaxFilingProfileList() {
        return searchResultTaxFilingProfileList;
    }

    public String getSelectedTaxFilingType() {
        return selectedTaxFilingType;
    }

    public void setSelectedTaxFilingType(String selectedTaxFilingType) {
        this.selectedTaxFilingType = selectedTaxFilingType;
    }

    public List<String> getSelectedTaxFilingPeriods() {
        return selectedTaxFilingPeriods;
    }

    public void setSelectedTaxFilingPeriods(List<String> selectedTaxFilingPeriods) {
        this.selectedTaxFilingPeriods = selectedTaxFilingPeriods;
    }

    public Date getSelectedSearchDosDateFrom() {
        return selectedSearchDosDateFrom;
    }

    public void setSelectedSearchDosDateFrom(Date selectedSearchDosDateFrom) {
        this.selectedSearchDosDateFrom = ZcaCalendar.covertDateToBeginning(selectedSearchDosDateFrom);
    }

    public Date getSelectedSearchDosDateTo() {
        return selectedSearchDosDateTo;
    }

    public void setSelectedSearchDosDateTo(Date selectedSearchDosDateTo) {
        this.selectedSearchDosDateTo = ZcaCalendar.covertDateToBeginning(selectedSearchDosDateTo);
    }

    public Date getSelectedSearchTaxFilingByDeadlineDateFrom() {
        return selectedSearchTaxFilingByDeadlineDateFrom;
    }

    public void setSelectedSearchTaxFilingByDeadlineDateFrom(Date selectedSearchTaxFilingByDeadlineDateFrom) {
        this.selectedSearchTaxFilingByDeadlineDateFrom = ZcaCalendar.covertDateToBeginning(selectedSearchTaxFilingByDeadlineDateFrom);
    }

    public Date getSelectedSearchTaxFilingByDeadlineDateTo() {
        return selectedSearchTaxFilingByDeadlineDateTo;
    }

    public void setSelectedSearchTaxFilingByDeadlineDateTo(Date selectedSearchTaxFilingByDeadlineDateTo) {
        this.selectedSearchTaxFilingByDeadlineDateTo = ZcaCalendar.covertDateToBeginning(selectedSearchTaxFilingByDeadlineDateTo);
    }
    
    private String getSearchTaxcorpResultPath(){
        return RosePageName.SearchTaxcorpResultPage.name() + RoseWebUtils.constructWebQueryString(null, true);
    }
    
    public void deleteTaxFilingEntity(String taxFilingUuid){
        try {
            getTaxcorpEJB().deleteEntityByUuid(G01TaxFiling.class, taxFilingUuid);
            Integer index = null; 
            for(int i = 0; i < searchResultTaxFilingProfileList.size(); i++){
                if (taxFilingUuid.equalsIgnoreCase(searchResultTaxFilingProfileList.get(i).getTaxFilingEntity().getTaxFilingUuid())){
                    index = i;
                    break;
                }       
            }
            if (index != null){
                searchResultTaxFilingProfileList.remove(index.intValue());
            }
        } catch (Exception ex) {
            //Logger.getLogger(TaxcorpCaseMgtBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalErrorFacesMessage(ex.getMessage());
        }
    }

    @Override
    public String searchByEntityCriteria() {
        searchResultTaxcorpEntityList.clear();
        List<G01TaxcorpCase> aG01TaxcorpCaseList = getBusinessEJB().findAll(G01TaxcorpCase.class);
        
        Double score = null;
        TaxcorpFilingConciseProfile aG01TaxcorpCaseSearchResultProfile;
        for (G01TaxcorpCase aG01TaxcorpCase : aG01TaxcorpCaseList){
            score = isSimiliarEnough(aG01TaxcorpCase);
            if (score > getRoseSettings().getSearchSimilarityThreshhold()){
                aG01TaxcorpCaseSearchResultProfile = new TaxcorpFilingConciseProfile();
                aG01TaxcorpCaseSearchResultProfile.setTaxcorpCase(aG01TaxcorpCase);
                aG01TaxcorpCaseSearchResultProfile.setSimilarityValue(score);
                searchResultTaxcorpEntityList.add(aG01TaxcorpCaseSearchResultProfile);
            }
        }
        if (!searchResultTaxcorpEntityList.isEmpty()){
            Collections.sort(searchResultTaxcorpEntityList, (TaxcorpFilingConciseProfile o1, TaxcorpFilingConciseProfile o2) -> o1.getSimilarityValue().compareTo(o2.getSimilarityValue())*(-1));
        } 
        return getSearchTaxcorpResultPath();
    }

    public String searchByCreatedDateRange() {
        return searchG01TaxcorpCaseByDateRange(getTaxcorpEJB().findG01TaxcorpCaseListByFeaturedDateRangeFields("created", getSelectedSearchCreatedDateFrom(), getSelectedSearchCreatedDateTo()));
    }

    public String searchByUpdatedDateRange() {
        return searchG01TaxcorpCaseByDateRange(getTaxcorpEJB().findG01TaxcorpCaseListByFeaturedDateRangeFields("updated", getSelectedSearchUpdatedDateFrom(), getSelectedSearchUpdatedDateTo()));
    }

    public String searchByDosDateRange() {
        return searchG01TaxcorpCaseByDateRange(getTaxcorpEJB().findG01TaxcorpCaseListByFeaturedDateRangeFields("dosDate", getSelectedSearchDosDateFrom(), getSelectedSearchDosDateTo()));
    }
    
    private String searchG01TaxcorpCaseByDateRange(List<G01TaxcorpCase> aG01TaxcorpCaseList){
        searchResultTaxcorpEntityList.clear();
        TaxcorpFilingConciseProfile aG01TaxcorpCaseSearchResultProfile;
        for (G01TaxcorpCase aG01TaxcorpCase : aG01TaxcorpCaseList){
            aG01TaxcorpCaseSearchResultProfile = new TaxcorpFilingConciseProfile();
            aG01TaxcorpCaseSearchResultProfile.setTaxcorpCase(aG01TaxcorpCase);
            aG01TaxcorpCaseSearchResultProfile.setSimilarityValue(1.00);
            searchResultTaxcorpEntityList.add(aG01TaxcorpCaseSearchResultProfile);
        }
        return getSearchTaxcorpResultPath();
    }
    
    @Override
    public String getTargetReturnWebPath(){
        return RosePageName.SearchEnginePage.name() + RoseWebUtils.constructWebQueryString(null, true);
    }

    public String searchBillBalanceByDateRange() {
        searchResultTaxcorpBillBalanceProfileList.clear();
        searchResultTaxcorpBillBalanceProfileList.addAll(getTaxcorpEJB().findTaxcorpBillBalanceProfileListByDueRange(selectedSearchBillBalanceDateFrom, selectedSearchBillBalanceDateTo));
        return RosePageName.SearchTaxcorpBillBalanceResultPage.name() + RoseWebUtils.constructWebQueryString(null, true);
    }
    
    public String searchTaxFilingByDeadlineRange(){
        TaxFilingType type = TaxFilingType.convertEnumValueToType(selectedTaxFilingType, false);
        TaxFilingPeriod period;
        String result = null;
        if (searchResultTaxFilingProfileList == null){
            searchResultTaxFilingProfileList = new ArrayList<>();
        }
        searchResultTaxFilingProfileList.clear();
        for (String selectedTaxFilingPeriod : selectedTaxFilingPeriods){
            period = TaxFilingPeriod.convertEnumValueToType(selectedTaxFilingPeriod, false);
            switch(type){
                case PAYROLL_TAX:
                case SALES_TAX:
                case TAX_RETURN:
                    searchResultTaxFilingProfileList.addAll(getTaxcorpEJB().findTaxcorpCaseProfileListByDeadlineDateRangeWithTypeAndPeriod(type, period, getSelectedSearchTaxFilingByDeadlineDateFrom(), getSelectedSearchTaxFilingByDeadlineDateTo()));
                    sortSearchResultTaxFilingProfileListByCompanyName();
                    result =  RosePageName.SearchTaxcorpFilingResultPage.name() + RoseWebUtils.constructWebQueryString(null, true);
                    break;
                case ALL_TYPES:
                    searchResultTaxFilingProfileList.addAll(getTaxcorpEJB().findTaxcorpCaseProfileListByDeadlineDateRange(getSelectedSearchTaxFilingByDeadlineDateFrom(), getSelectedSearchTaxFilingByDeadlineDateTo()));
                    sortSearchResultTaxFilingProfileListByCompanyName();
                    result = RosePageName.SearchTaxcorpFilingResultPage.name() + RoseWebUtils.constructWebQueryString(null, true);
                    break;
                default:

            }
        }//for-loop
        return result;
    }
    
    private void sortSearchResultTaxFilingProfileListByCompanyName(){
        if ((searchResultTaxFilingProfileList != null) && (!searchResultTaxFilingProfileList.isEmpty())){
            Collections.sort(searchResultTaxFilingProfileList, (TaxFilingProfile o1, TaxFilingProfile o2) -> {
                try{
                    return o1.getTaxcorpCaseEntity().getCorporateName().compareToIgnoreCase(o2.getTaxcorpCaseEntity().getCorporateName());
                }catch (Exception ex){
                    return 0;
                }
            });
        }
    }
    
    public void onRowEdit(RowEditEvent event) {
        if (event.getObject() instanceof TaxFilingProfile){
            TaxFilingProfile aTaxFilingProfile = (TaxFilingProfile)event.getObject();
            try {
                getTaxcorpEJB().storeEntityByUuid(G01TaxFiling.class, aTaxFilingProfile.getTaxFilingEntity(),
                        aTaxFilingProfile.getTaxFilingEntity().getTaxFilingUuid(), G01DataUpdaterFactory.getSingleton().getG01TaxFilingUpdater());
                RoseJsfUtils.setGlobalSuccessfulOperationMessage();
            } catch (Exception ex) {
                Logger.getLogger(SearchTaxcorpBean.class.getName()).log(Level.SEVERE, null, ex);
                RoseJsfUtils.setGlobalFailedOperationMessage(ex.getMessage());
            }
        }
    }
     
    public void onRowCancel(RowEditEvent event) {
        RoseJsfUtils.setGlobalInfoFacesMessage(RoseText.getText("OperationCanceled") 
                + " (" + ZcaCalendar.convertToMMddyyyyHHmmss(new Date(), "-", " @ ", ":") + ")");
    }
    
    public void handleTaxFilingReceivedDateChange(String taxFilingUuid){
    
    }
    
    public void handleTaxFilingPreparedDateChange(String taxFilingUuid){
    
    }
    
    public void handleTaxFilingCompletedDateChange(String taxFilingUuid){
    
    }
    
    @Override
    public String deleteTargetTaxcorpCaseProfile(String taxcorpCaseUuid){
        if (ZcaValidator.isNullEmpty(taxcorpCaseUuid)){
            return null;
        }
        String result = super.deleteTargetTaxcorpCaseProfile(taxcorpCaseUuid);
        
        if (searchResultTaxFilingProfileList != null){
            Integer index = null;
            int count = 0;
            for (TaxFilingProfile aTaxFilingProfile : searchResultTaxFilingProfileList){
                if (taxcorpCaseUuid.equalsIgnoreCase(aTaxFilingProfile.getTaxcorpCaseEntity().getTaxcorpCaseUuid())){
                    index = count;
                    break;
                }
                count++;
            }
            if (index != null){
                searchResultTaxFilingProfileList.remove(index.intValue());
            }
        }
        
        if (searchResultTaxcorpEntityList != null){
            Integer index = null;
            int count = 0;
            for (TaxcorpFilingConciseProfile aTaxcorpFilingConciseProfile : searchResultTaxcorpEntityList){
                if (taxcorpCaseUuid.equalsIgnoreCase(aTaxcorpFilingConciseProfile.getTaxcorpCase().getTaxcorpCaseUuid())){
                    index = count;
                    break;
                }
                count++;
            }
            if (index != null){
                searchResultTaxcorpEntityList.remove(index.intValue());
            }
        }
        
        return result;
    }
}
