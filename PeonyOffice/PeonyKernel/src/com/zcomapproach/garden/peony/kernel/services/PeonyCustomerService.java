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
import com.zcomapproach.garden.persistence.entity.G02TulipUser;

/**
 *
 * @author zhijun98
 */
public interface PeonyCustomerService extends PeonyService {
    
    /**
     * A web-rest client for customer module
     * @return 
     */
    public PeonyRestClient getPeonyCustomerRestClient();

    /**
     * Display data entry for customer whose account UUID is the parameter
     * @param accountUuid - customer's account-Uuid; if this is NULL, it will treat 
     * it as a new data entry of new customers
     */
    public void launchCustomerProfileWindowByAccountUuid(String accountUuid);

    public void launchPeonyCustomerServiceTopComponent();

    public void launchTulipCustomerProfile(G02TulipUser tulipUser);

}
