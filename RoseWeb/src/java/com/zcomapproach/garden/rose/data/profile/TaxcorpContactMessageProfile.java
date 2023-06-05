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

import com.zcomapproach.garden.persistence.entity.G01TaxcorpCase;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zhijun98
 */
public class TaxcorpContactMessageProfile extends ContactMessageProfile{

    private List<G01TaxcorpCase> taxcorpCaseList;

    public TaxcorpContactMessageProfile() {
        this.taxcorpCaseList = new ArrayList<>();
    }

    public List<G01TaxcorpCase> getTaxcorpCaseList() {
        return taxcorpCaseList;
    }

    public void setTaxcorpCaseList(List<G01TaxcorpCase> taxcorpCaseList) {
        this.taxcorpCaseList = taxcorpCaseList;
    }
    
}
