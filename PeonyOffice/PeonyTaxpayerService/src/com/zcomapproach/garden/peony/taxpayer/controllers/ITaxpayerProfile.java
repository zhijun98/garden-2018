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

package com.zcomapproach.garden.peony.taxpayer.controllers;

import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerInfo;

/**
 *
 * @author zhijun98
 */
public interface ITaxpayerProfile {
    
    public void validateProfileData() throws ZcaEntityValidationException;
    
    public void loadTargetTaxpayerInfo() throws ZcaEntityValidationException;
    
    public G02TaxpayerInfo saveProfile() throws Exception;
}
