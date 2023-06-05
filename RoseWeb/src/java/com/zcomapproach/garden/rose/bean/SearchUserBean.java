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

import com.zcomapproach.garden.data.constant.SearchUserCriteria;
import com.zcomapproach.garden.persistence.constant.GardenContactType;
import com.zcomapproach.garden.persistence.entity.G01Account;
import com.zcomapproach.garden.persistence.entity.G01ContactInfo;
import com.zcomapproach.garden.persistence.entity.G01User;
import com.zcomapproach.garden.rose.RosePageName;
import com.zcomapproach.garden.rose.data.profile.RoseUserConciseProfile;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.garden.rose.util.RoseWebUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author zhijun98
 */
@Named(value="searchUserBean")
@SessionScoped
public class SearchUserBean extends AbstractSearchBean{
    
    private final List<RoseUserConciseProfile> searchResultUserEntityList;

    public SearchUserBean() {
        searchResultUserEntityList = new ArrayList<>();
    }

    public List<String> getSearchUserCriteriaList(){
        return SearchUserCriteria.getEnumValueList(false);
    }

    private String getSearchUserResultPath(){
        return RosePageName.SearchUserResultPage.name() + RoseWebUtils.constructWebQueryString(null, true);
    }
    
    @Override
    public String searchByEntityCriteria() {
        searchResultUserEntityList.clear();
        SearchUserCriteria aCritera = SearchUserCriteria.convertEnumValueToType(getSelectedSearchCriteria());
        if (aCritera == null){
            RoseJsfUtils.setGlobalFailedOperationMessage(RoseText.getText("SearchUserCriteria")+": "+RoseText.getText("FieldRequired_T"));
            return null;
        }else{
            HashMap<String, RoseUserConciseProfile> result = null;
            switch (aCritera){
                case FIRST_NAME:
                case LAST_NAME:
                case SSN:
                    result = searchCustomerUsersByEntityCriteria();
                    break;
                case EMAIL:
                case PHONE:
                    result = searchByEntityCriteriaFromContactInfo(aCritera);
                    break;
                case LOGIN_NAME:
                    result = searchByEntityCriteriaFromAccount();
                    break;
            }
            if (result != null){
                searchResultUserEntityList.addAll(new ArrayList<>(result.values()));
                if (!searchResultUserEntityList.isEmpty()){
                    Collections.sort(searchResultUserEntityList, (RoseUserConciseProfile o1, RoseUserConciseProfile o2) -> o1.getSimilarityValue().compareTo(o2.getSimilarityValue())*(-1));
                }
            }
        }
        return getSearchUserResultPath();
    }
    
    private HashMap<String, RoseUserConciseProfile> searchByEntityCriteriaFromAccount() {
        HashMap<String, RoseUserConciseProfile> result = new HashMap<>();
        List<G01Account> aG01AccountList = getBusinessEJB().findAll(G01Account.class);
        RoseUserConciseProfile aG01UserSearchResultProfile;
        G01User aG01User;
        Double score;
        for (G01Account aG01Account : aG01AccountList){
            score = isSimiliarEnough(aG01Account);
            if (score > 0.75){
                aG01User = getRuntimeEJB().findEntityByUuid(G01User.class, aG01Account.getAccountUuid());
                if (aG01User != null){
                    aG01UserSearchResultProfile = new RoseUserConciseProfile();
                    aG01UserSearchResultProfile.setG01User(aG01User);
                    aG01UserSearchResultProfile.setSimilarityValue(score);
                    result.put(aG01Account.getAccountUuid(), aG01UserSearchResultProfile);
                }
            }
        }
        return result;
    }
    
    private HashMap<String, RoseUserConciseProfile> searchCustomerUsersByEntityCriteria() {
        HashMap<String, RoseUserConciseProfile> result = new HashMap<>();
        List<G01User> aG01UserList = getBusinessEJB().findAllCustomerUsers();
        RoseUserConciseProfile aG01UserSearchResultProfile;
        Double score;
        for (G01User aG01User : aG01UserList){
            score = isSimiliarEnough(aG01User);
            if (score > 0.75){
                aG01UserSearchResultProfile = new RoseUserConciseProfile();
                aG01UserSearchResultProfile.setG01User(aG01User);
                aG01UserSearchResultProfile.setSimilarityValue(score);
                result.put(aG01User.getUserUuid(), aG01UserSearchResultProfile);
            }
        }
        return result;
    }
    
    /**
     * @deprecated - replaced by searchCustomersByEntityCriteria
     * @return 
     */
    private HashMap<String, RoseUserConciseProfile> searchByEntityCriteriaFromUser() {
        HashMap<String, RoseUserConciseProfile> result = new HashMap<>();
        List<G01User> aG01UserList = getBusinessEJB().findAll(G01User.class);
        RoseUserConciseProfile aG01UserSearchResultProfile;
        Double score;
        for (G01User aG01User : aG01UserList){
            score = isSimiliarEnough(aG01User);
            if (score > 0.75){
                aG01UserSearchResultProfile = new RoseUserConciseProfile();
                aG01UserSearchResultProfile.setG01User(aG01User);
                aG01UserSearchResultProfile.setSimilarityValue(score);
                result.put(aG01User.getUserUuid(), aG01UserSearchResultProfile);
            }
        }
        return result;
    }
    
    private HashMap<String, RoseUserConciseProfile> searchByEntityCriteriaFromContactInfo(SearchUserCriteria aCritera) {
        HashMap<String, RoseUserConciseProfile> result = new HashMap<>();
        List<G01ContactInfo> aG01ContactInfoList = new ArrayList<>();
        String sqlQuery = "SELECT g FROM G01ContactInfo g WHERE g.contactType = :contactType";
        HashMap<String, Object> params = new HashMap<>();
        switch (aCritera){
            case EMAIL:
                params.put("contactType", GardenContactType.EMAIL.value());
                aG01ContactInfoList = getBusinessEJB().findEntityListByQuery(G01ContactInfo.class, sqlQuery, params);
                break;
            case PHONE:
                params.put("contactType", GardenContactType.MOBILE_PHONE.value());
                aG01ContactInfoList.addAll(getBusinessEJB().findEntityListByQuery(G01ContactInfo.class, sqlQuery, params));
                params.put("contactType", GardenContactType.FAX.value());
                aG01ContactInfoList.addAll(getBusinessEJB().findEntityListByQuery(G01ContactInfo.class, sqlQuery, params));
                params.put("contactType", GardenContactType.HOME_PHONE.value());
                aG01ContactInfoList.addAll(getBusinessEJB().findEntityListByQuery(G01ContactInfo.class, sqlQuery, params));
                params.put("contactType", GardenContactType.LANDLINE_PHONE.value());
                aG01ContactInfoList.addAll(getBusinessEJB().findEntityListByQuery(G01ContactInfo.class, sqlQuery, params));
                break;
        }
        if (!aG01ContactInfoList.isEmpty()){
            RoseUserConciseProfile aG01UserSearchResultProfile;
            G01User aG01User;
            Double score;
            for (G01ContactInfo aG01ContactInfo : aG01ContactInfoList){
                score = isSimiliarEnough(aG01ContactInfo);
                if (score > 0.75){
                    aG01User = getRuntimeEJB().findEntityByUuid(G01User.class, aG01ContactInfo.getEntityUuid());
                    if (aG01User != null){
                        aG01UserSearchResultProfile = new RoseUserConciseProfile();
                        aG01UserSearchResultProfile.setG01User(aG01User);
                        aG01UserSearchResultProfile.setSimilarityValue(score);
                        result.put(aG01ContactInfo.getContactInfoUuid(), aG01UserSearchResultProfile);
                    }
                }
            }
        }
        return result;
    }

    public List<RoseUserConciseProfile> getSearchResultUserEntityList() {
        return searchResultUserEntityList;
    }

    public String searchByCreatedDateRange() {
        return searchG01UserByDateRange(getBusinessEJB().findG01UserListByFeaturedDateRangeFields("created", 
                getSelectedSearchCreatedDateFrom(), getSelectedSearchCreatedDateTo()));
    }

    public String searchByUpdatedDateRange() {
        return searchG01UserByDateRange(getBusinessEJB().findG01UserListByFeaturedDateRangeFields("updated", 
                getSelectedSearchUpdatedDateFrom(), getSelectedSearchUpdatedDateTo()));

    }
    
    private String searchG01UserByDateRange(List<G01User> aG01UserList){
        searchResultUserEntityList.clear();
        RoseUserConciseProfile aG01UserSearchResultProfile;
        for (G01User aG01User : aG01UserList){
            aG01UserSearchResultProfile = new RoseUserConciseProfile();
            aG01UserSearchResultProfile.setG01User(aG01User);
            aG01UserSearchResultProfile.setSimilarityValue(1.00);
            searchResultUserEntityList.add(aG01UserSearchResultProfile);
        }
        return getSearchUserResultPath();
    }
    
}
