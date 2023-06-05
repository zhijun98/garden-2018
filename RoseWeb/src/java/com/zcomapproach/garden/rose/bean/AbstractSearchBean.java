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

import com.zcomapproach.garden.data.constant.SearchTaxcorpCriteria;
import com.zcomapproach.garden.data.constant.SearchUserCriteria;
import com.zcomapproach.garden.persistence.entity.G01Account;
import com.zcomapproach.garden.persistence.entity.G01ContactInfo;
import com.zcomapproach.garden.persistence.entity.G01TaxcorpCase;
import com.zcomapproach.garden.persistence.entity.G01User;
import com.zcomapproach.garden.rose.RosePageName;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseWebUtils;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaValidator;
import java.util.Date;
import net.ricecode.similarity.JaroWinklerStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import net.ricecode.similarity.StringSimilarityService;
import net.ricecode.similarity.StringSimilarityServiceImpl;

/**
 *
 * @author zhijun98
 */
public abstract class AbstractSearchBean extends AbstractRoseComponentBean{
    
    private String selectedSearchCriteria;
    private String selectedSearchCriteriaValue;
    private boolean selectedSearchCriteriaExactMatch;
    
    private Date selectedSearchCreatedDateFrom;
    private Date selectedSearchCreatedDateTo;
    
    private Date selectedSearchUpdatedDateFrom;
    private Date selectedSearchUpdatedDateTo;
    
    public abstract String searchByEntityCriteria();

    @Override
    public String getRosePageTopic() {
        return RoseText.getText("Search");
    }

    @Override
    public String getTopicIconAwesomeName() {
        return "search";
    }

    @Override
    public String getRequestedEntityUuid() {
        return null;
    }

    @Override
    public String getTargetReturnWebPath() {
        return RosePageName.SearchEnginePage.name() + RoseWebUtils.constructWebQueryString(null, true);
    }

    public String getSelectedSearchCriteria() {
        return selectedSearchCriteria;
    }

    public void setSelectedSearchCriteria(String selectedSearchCriteria) {
        this.selectedSearchCriteria = selectedSearchCriteria;
    }

    public String getSelectedSearchCriteriaValue() {
        return selectedSearchCriteriaValue;
    }

    public void setSelectedSearchCriteriaValue(String selectedSearchCriteriaValue) {
        this.selectedSearchCriteriaValue = selectedSearchCriteriaValue;
    }

    public boolean isSelectedSearchCriteriaExactMatch() {
        return selectedSearchCriteriaExactMatch;
    }

    public void setSelectedSearchCriteriaExactMatch(boolean selectedSearchCriteriaExactMatch) {
        this.selectedSearchCriteriaExactMatch = selectedSearchCriteriaExactMatch;
    }

    public Date getSelectedSearchCreatedDateFrom() {
        return selectedSearchCreatedDateFrom;
    }

    public void setSelectedSearchCreatedDateFrom(Date selectedSearchCreatedDateFrom) {
        this.selectedSearchCreatedDateFrom = ZcaCalendar.covertDateToBeginning(selectedSearchCreatedDateFrom);
    }

    public Date getSelectedSearchCreatedDateTo() {
        return selectedSearchCreatedDateTo;
    }

    public void setSelectedSearchCreatedDateTo(Date selectedSearchCreatedDateTo) {
        this.selectedSearchCreatedDateTo = ZcaCalendar.covertDateToEnding(selectedSearchCreatedDateTo);
    }

    public Date getSelectedSearchUpdatedDateFrom() {
        return selectedSearchUpdatedDateFrom;
    }

    public void setSelectedSearchUpdatedDateFrom(Date selectedSearchUpdatedDateFrom) {
        this.selectedSearchUpdatedDateFrom = selectedSearchUpdatedDateFrom;
    }

    public Date getSelectedSearchUpdatedDateTo() {
        return selectedSearchUpdatedDateTo;
    }

    public void setSelectedSearchUpdatedDateTo(Date selectedSearchUpdatedDateTo) {
        this.selectedSearchUpdatedDateTo = selectedSearchUpdatedDateTo;
    }
    
    private String getSelectedCriteriaTarget(G01User aG01User) {
        SearchUserCriteria aCritera = SearchUserCriteria.convertEnumValueToType(getSelectedSearchCriteria());
        if (aCritera == null){
            return null;
        }else{
            switch (aCritera){
                case FIRST_NAME:
                    return aG01User.getFirstName();
                case LAST_NAME:
                    return aG01User.getLastName();
                case SSN:
                    return aG01User.getSsn();
            }
            return null;
        }
    }
    
    private String getSelectedCriteriaTarget(G01Account aG01Account) {
        SearchUserCriteria aCritera = SearchUserCriteria.convertEnumValueToType(getSelectedSearchCriteria());
        if (aCritera == null){
            return null;
        }else{
            switch (aCritera){
                case EMAIL:
                    return aG01Account.getAccountEmail();
                case PHONE:
                    return aG01Account.getMobilePhone();
                case LOGIN_NAME:
                    return aG01Account.getLoginName();
            }
            return null;
        }
    }
    
    private String getSelectedCriteriaTarget(G01TaxcorpCase aG01TaxcorpCase) {
        SearchTaxcorpCriteria aCritera = SearchTaxcorpCriteria.convertEnumValueToType(getSelectedSearchCriteria());
        if (aCritera == null){
            return null;
        }else{
            switch (aCritera){
                case ein_number:
                    return aG01TaxcorpCase.getEinNumber();
                case business_type:
                    return aG01TaxcorpCase.getBusinessType();
                case corporate_name:
                    return aG01TaxcorpCase.getCorporateName();
                case corporate_email:
                    return aG01TaxcorpCase.getCorporateEmail();
                case corporate_phone:
                    return aG01TaxcorpCase.getCorporatePhone();
                case corporate_fax:
                    return aG01TaxcorpCase.getCorporateFax();
                case corporate_web_presence:
                    return aG01TaxcorpCase.getCorporateWebPresence();
                case business_purpose:
                    return aG01TaxcorpCase.getBusinessPurpose();
            }
            return null;
        }
    }

    protected double isSimiliarEnough(G01TaxcorpCase aG01TaxcorpCase) {
        return isSimiliarEnoughHelper(getSelectedCriteriaTarget(aG01TaxcorpCase));
    }

    protected double isSimiliarEnough(G01Account aG01Account) {
        return isSimiliarEnoughHelper(getSelectedCriteriaTarget(aG01Account));
    }

    protected double isSimiliarEnough(G01ContactInfo aG01ContactInfo) {
        return isSimiliarEnoughHelper(aG01ContactInfo.getContactInfo());
    }

    protected double isSimiliarEnough(G01User aG01User) {
        return isSimiliarEnoughHelper(getSelectedCriteriaTarget(aG01User));
    }

    private double isSimiliarEnoughHelper(String target) {
        if (ZcaValidator.isNullEmpty(target)){
            return 0.0;
        }
        String searchValue = this.getSelectedSearchCriteriaValue();
        if (ZcaValidator.isNullEmpty(searchValue)){
            return 0.0;
        }
        searchValue = searchValue.toLowerCase();
        target = target.toLowerCase();
        if (target.equals(searchValue)){
            return 1.0;
        }else if (target.equalsIgnoreCase(searchValue)){
            return 0.99999;
        }else{
            if (isSelectedSearchCriteriaExactMatch()){
                return 0.0;
            }
        }
        SimilarityStrategy strategy = new JaroWinklerStrategy();
        StringSimilarityService service = new StringSimilarityServiceImpl(strategy);
        double result = service.score(searchValue, target);
        if (result < getRoseSettings().getSearchSimilarityThreshhold()){
            if (searchValue.contains(target)){
                return getRoseSettings().getSearchSimilarityThreshhold() + 0.00001;
            }else if (target.contains(searchValue)){
                return getRoseSettings().getSearchSimilarityThreshhold() + 0.00001;
            }
        }
        return result;
    }
}
