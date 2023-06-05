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

package com.zcomapproach.garden.rose.data.profile;

import com.zcomapproach.garden.persistence.entity.G01TaxpayerCase;
import com.zcomapproach.garden.persistence.entity.G01User;
import com.zcomapproach.commons.ZcaCalendar;

/**
 *
 * @author zhijun98
 */
public class TaxpayerCaseConciseProfile extends AbstractEntityConciseProfile{
    
    private G01TaxpayerCase taxpayerCase;
    private G01User customer;

    public G01TaxpayerCase getTaxpayerCase() {
        if (taxpayerCase == null){
            taxpayerCase = new G01TaxpayerCase();
        }
        return taxpayerCase;
    }

    public void setTaxpayerCase(G01TaxpayerCase taxpayerCase) {
        this.taxpayerCase = taxpayerCase;
    }

    public G01User getCustomer() {
        if (customer == null){
            customer = new G01User();
        }
        return customer;
    }

    public void setCustomer(G01User customer) {
        this.customer = customer;
    }

    @Override
    public String getProfileName() {
        return getCustomer().getFirstName() + getCustomer().getLastName() 
                + ": " + ZcaCalendar.convertToMMddyyyy(getTaxpayerCase().getDeadline(), "-");
    }

    @Override
    public String getProfileDescriptiveName() {
        return getProfileName();
    }

    @Override
    protected String getProfileUuid() {
        return getTaxpayerCase().getTaxpayerCaseUuid();
    }

}
