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

import com.zcomapproach.garden.persistence.entity.G01TaxFiling;
import com.zcomapproach.garden.persistence.entity.G01TaxcorpCase;

/**
 *
 * @author zhijun98
 */
public class TaxcorpFilingConciseProfile extends AbstractEntityConciseProfile {

    private G01TaxcorpCase taxcorpCase;
    private G01TaxFiling taxFiling;
    
    //optional

    public TaxcorpFilingConciseProfile() {
        taxcorpCase = new G01TaxcorpCase();
        taxFiling = new G01TaxFiling();
    }

    public G01TaxcorpCase getTaxcorpCase() {
        if (taxcorpCase == null){
            taxcorpCase = new G01TaxcorpCase();
        }
        return taxcorpCase;
    }

    public void setTaxcorpCase(G01TaxcorpCase taxcorpCase) {
        this.taxcorpCase = taxcorpCase;
    }

    public G01TaxFiling getTaxFiling() {
        return taxFiling;
    }

    public void setTaxFiling(G01TaxFiling taxFiling) {
        this.taxFiling = taxFiling;
    }

    @Override
    public String getProfileName() {
        return getTaxcorpCase().getCorporateName();
    }

    @Override
    public String getProfileDescriptiveName() {
        return getTaxcorpCase().getCorporateName() + " (" + getTaxcorpCase().getEinNumber() + ")";
    }

    @Override
    protected String getProfileUuid() {
        return getTaxcorpCase().getTaxcorpCaseUuid();
    }

}
