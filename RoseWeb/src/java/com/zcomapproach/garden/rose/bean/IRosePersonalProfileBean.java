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

import com.zcomapproach.garden.rose.data.profile.RoseUserProfile;

/**
 *
 * @author zhijun98
 */
public interface IRosePersonalProfileBean extends IRoseComponentBean{
    
    public RoseUserProfile getTargetUserProfile();
    
    public IRoseLocationEntityEditor getLocationEntityEditor();
    
    public IRoseContactInfoEntityEditor getContactInfoEntityEditor();
    
    public boolean isUserProfileUpdateDemanded();
    public void openUserProfileDataEntry();
    public void closeUserProfileDataEntry();
    
    public String getEntityTypeParamValue();
    
    public String getTargetPersonUuid();

    public boolean isForTaxcorpCase();
    
    public boolean isForTaxpayerCase();

    public String storeTargetPersonalProfile();
    
    public String requestNewCorporateTaxFiling();
    
    public String requestNewIndividualTaxFiling();
    
}
