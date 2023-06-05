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

import com.zcomapproach.garden.rose.data.RoseWebParamKey;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 * Settings of Rose web
 * @author zhijun98
 */
@Named(value = "roseParamKeys")
@ApplicationScoped
public class RoseParamKeysBean extends AbstractRoseBean{
    
    public String getViewPurposeParamKey(){
        return RoseWebParamKey.ViewPurposeParam.value();
    }
    
    public String getWebPostUuidParamKey(){
        return RoseWebParamKey.WebPostUuidParam.value();
    }
    
    public String getWorkTeamUuidParamKey(){
        return RoseWebParamKey.WorkTeamUuidParam.value();
    }
    
    public String getRoseChatMessageUuidParamKey(){
        return RoseWebParamKey.RoseChatMessageUuidParam.value();
    }
    
    public String getRoseArchivedFileUuidParamKey(){
        return RoseWebParamKey.RoseArchivedFileUuidParam.value();
    }
    
    public String getEntityTypeParamKey(){
        return RoseWebParamKey.EntityTypeParam.value();
    }
    
    public String getEntityUuidParamKey(){
        return RoseWebParamKey.EntityUuidParam.value();
    }
    
    public String getRoseBillUuidParamKey(){
        return RoseWebParamKey.RoseBillUuidParam.value();
    }
    
    public String getEmailToParamKey(){
        return RoseWebParamKey.EmailToParam.value();
    }
    
    public String getEmailIdParamKey(){
        return RoseWebParamKey.EmailIdParam.value();
    }
    
    public String getCustomerUuidParamKey(){
        return RoseWebParamKey.CustomerUuidParam.value();
    }
    
    public String getAccountUuidParamKey(){
        return RoseWebParamKey.AccountUuidParam.value();
    }
    
    public String getUserUuidParamKey(){
        return RoseWebParamKey.UserUuidParam.value();
    }
    
    public String getMergingUserUuidParamKey(){
        return RoseWebParamKey.MergingUserUuidParam.value();
    }
    
    public String getEmployeeAccountUuidParamKey(){
        return RoseWebParamKey.EmployeeAccountUuidParam.value();
    }
    
    public String getRegistrationConfirmationCodeParamKey(){
        return RoseWebParamKey.RegistrationConfirmationCodeParam.value();
    }
    
    public String getTaxpayerCaseUuidParamKey(){
        return RoseWebParamKey.TaxpayerCaseUuidParam.value();
    }
    
    public String getTaxcorpCaseUuidParamKey(){
        return RoseWebParamKey.TaxcorpCaseUuidParam.value();
    }
}
