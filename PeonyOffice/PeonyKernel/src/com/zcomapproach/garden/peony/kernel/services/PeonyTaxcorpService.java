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

package com.zcomapproach.garden.peony.kernel.services;

import com.zcomapproach.garden.peony.kernel.rest.PeonyRestClient;
import com.zcomapproach.garden.persistence.peony.PeonyTaxcorpCase;

/**
 *
 * @author zhijun98
 */
public interface PeonyTaxcorpService extends PeonyService {
    
    /**
     * A web-rest client for taxcorp
     * @return 
     */
    public PeonyRestClient getPeonyTaxcorpRestClient();

    /**
     * Launch a TopComponent window for a specific taxcorp case whose UUID is taxcorpCaseUuid.
     * @param taxcorpCaseUuid 
     */
    public void launchPeonyTaxcorpCaseTopComponentByTaxcorpCaseUuid(String taxcorpCaseUuid);
    
    public void launchPeonyTaxcorpCaseTopComponentByEinNumber(String einNumber);
    
    /**
     * Launch a TopComponent window for a specific taxcorp case which may be a brand-new case
     * @param targetPeonyTaxcorpCase
     */
    public void launchPeonyTaxcorpCaseTopComponent(final PeonyTaxcorpCase targetPeonyTaxcorpCase);

    public void launchPeonyTaxcorpCaseDialog(final PeonyTaxcorpCase peonyTaxcorpCase);

}
