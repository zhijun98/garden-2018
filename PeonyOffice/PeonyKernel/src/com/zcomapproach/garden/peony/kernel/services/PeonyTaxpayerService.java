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
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;

/**
 *
 * @author zhijun98
 */
public interface PeonyTaxpayerService extends PeonyService {
    
    /**
     * A web-rest client for taxpayer
     * @return 
     */
    public PeonyRestClient getPeonyTaxpayerRestClient();
    
    /**
     * Launch a TopComponent window for a specific taxpayer case which may be a 
     * brand-new case
     * @param targetPeonyTaxpayerCase
     */
    public void launchPeonyTaxpayerCaseTopComponent(PeonyTaxpayerCase targetPeonyTaxpayerCase);

    /**
     * Launch a TopComponent window for a specific taxpayer case whose UUID is 
     * taxpayerCaseUuid.
     * @param taxpayerCaseUuid 
     */
    public void launchPeonyTaxpayerCaseTopComponentByTaxpayerCaseUuid(String taxpayerCaseUuid);

    public void launchPeonyTaxpayerCaseDialog(final PeonyTaxpayerCase peonyTaxpayerCase);

}
