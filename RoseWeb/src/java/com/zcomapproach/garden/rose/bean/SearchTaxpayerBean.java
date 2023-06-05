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

import com.zcomapproach.garden.rose.RosePageName;
import com.zcomapproach.garden.rose.data.profile.TaxpayerCaseConciseProfile;
import com.zcomapproach.garden.rose.util.RoseWebUtils;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaValidator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author zhijun98
 */
@Named(value="searchTaxpayerBean")
@SessionScoped
public class SearchTaxpayerBean extends AbstractSearchBean{
    
    private Date selectedSearchDosDateFrom;
    private Date selectedSearchDosDateTo;
    
    private List<TaxpayerCaseConciseProfile> searchResultTaxpayerProfileList;
    private Date selectedTaxpayerCaseDeadline;
    
    private String selectedTaxpayerCaseStatus;

    public SearchTaxpayerBean() {
        searchResultTaxpayerProfileList = new ArrayList<>();
    }

    public Date getSelectedTaxpayerCaseDeadline() {
        return selectedTaxpayerCaseDeadline;
    }

    public void setSelectedTaxpayerCaseDeadline(Date selectedTaxpayerCaseDeadline) {
        this.selectedTaxpayerCaseDeadline = selectedTaxpayerCaseDeadline;
    }

    public String getSelectedTaxpayerCaseStatus() {
        return selectedTaxpayerCaseStatus;
    }

    public void setSelectedTaxpayerCaseStatus(String selectedTaxpayerCaseStatus) {
        this.selectedTaxpayerCaseStatus = selectedTaxpayerCaseStatus;
    }

    public List<TaxpayerCaseConciseProfile> getSearchResultTaxpayerProfileList() {
        return searchResultTaxpayerProfileList;
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
    
    private String getSearchTaxpayerResultPath(){
        return RosePageName.SearchTaxpayerResultPage.name() + RoseWebUtils.constructWebQueryString(null, true);
    }

    /**
     * Search by work status
     * @return 
     */
    @Override
    public String searchByEntityCriteria() {
        searchResultTaxpayerProfileList = getTaxpayerEJB().findG01TaxpayerCaseListByWorkStatus(selectedTaxpayerCaseStatus, selectedTaxpayerCaseDeadline);
        return getSearchTaxpayerResultPath();
    }

    public String searchByCreatedDateRange() {
        searchResultTaxpayerProfileList = getTaxpayerEJB().findG01TaxpayerCaseListByFeaturedDateRangeFields("created", getSelectedSearchCreatedDateFrom(), getSelectedSearchCreatedDateTo());
        return getSearchTaxpayerResultPath();
    }

    public String searchByUpdatedDateRange() {
        searchResultTaxpayerProfileList = getTaxpayerEJB().findG01TaxpayerCaseListByFeaturedDateRangeFields("updated", getSelectedSearchUpdatedDateFrom(), getSelectedSearchUpdatedDateTo());
        return getSearchTaxpayerResultPath();
    }
    
    @Override
    public String getTargetReturnWebPath(){
        return RosePageName.SearchEnginePage.name() + RoseWebUtils.constructWebQueryString(null, true);
    }
    
    @Override
    public String deleteTargetTaxpayerCaseProfile(String taxpayerCaseUuid){
        if (ZcaValidator.isNullEmpty(taxpayerCaseUuid)){
            return null;
        }
        String result = super.deleteTargetTaxpayerCaseProfile(taxpayerCaseUuid);
        
        if (searchResultTaxpayerProfileList != null){
            Integer index = null;
            int count = 0;
            for (TaxpayerCaseConciseProfile aTaxpayerCaseConciseProfile : searchResultTaxpayerProfileList){
                if (taxpayerCaseUuid.equalsIgnoreCase(aTaxpayerCaseConciseProfile.getTaxpayerCase().getTaxpayerCaseUuid())){
                    index = count;
                    break;
                }
                count++;
            }
            if (index != null){
                searchResultTaxpayerProfileList.remove(index.intValue());
            }
        }
        
        return result;
    }
    
}
