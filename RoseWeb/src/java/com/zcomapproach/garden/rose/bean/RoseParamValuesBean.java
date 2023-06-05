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
import com.zcomapproach.garden.rose.data.RoseWebParamValue;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 * Settings of Rose web
 * @author zhijun98
 */
@Named(value = "roseParamValues")
@ApplicationScoped
public class RoseParamValuesBean extends AbstractRoseBean{
    
    public String getCreateNewEntityParamValue(){
        return RoseWebParamValue.CREATE_NEW_ENTITY.value();
    }
    
    public String getUpdateExistingEntityParamValue(){
        return RoseWebParamValue.UPDATE_EXISTING_ENTITY.value();
    }
    
    public String getDeleteExistingEntityParamValue(){
        return RoseWebParamValue.DELETE_EXISTING_ENTITY.value();
    }
    
    public String getDisplayActiveEntitiesParamValue(){
        return RoseWebParamValue.DISPLAY_ACTIVE_ENTITIES.value();
    }
    
    public String getDisplayAllEntitiesParamValue(){
        return RoseWebParamValue.DISPLAY_ALL_ENTITIES.value();
    }
    
    public String getPayrollTaxParamValue(){
        return RoseWebParamValue.PAYROLL_TAX.value();
    }
    
    public String getSalesTaxParamValue(){
        return RoseWebParamValue.SALES_TAX.value();
    }
    
    public String getTaxReturnParamValue(){
        return RoseWebParamValue.TAX_RETURN.value();
    }
    
    public String getTaxpayerCaseEntityTypeParamValue(){
        return GardenEntityType.TAXPAYER_CASE.value();
    }
    
    public String getTaxcorpCaseEntityTypeParamValue(){
        return GardenEntityType.TAXCORP_CASE.value();
    }
    
    public String getSearchTaxcorpFilingParamValue(){
        return GardenEntityType.SEARCH_TAXCORP_FILING.value();
    }
    
    public String getSearchTaxcorpCaseParamValue(){
        return GardenEntityType.SEARCH_TAXCORP_CASE.value();
    }
    
    public String getSearchTaxpayerCaseParamValue(){
        return GardenEntityType.SEARCH_TAXPAYER_CASE.value();
    }
    
    public String getSearchUserProfileParamValue(){
        return GardenEntityType.SEARCH_USER_PROFILE.value();
    }
    
    public String getUserEntityTypeParamValue(){
        return GardenEntityType.USER.value();
    }
    
    public String getAcountEntityTypeParamValue(){
        return GardenEntityType.ACCOUNT.value();
    }
}
