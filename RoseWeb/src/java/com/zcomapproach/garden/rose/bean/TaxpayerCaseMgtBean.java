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

import com.zcomapproach.garden.rose.data.profile.TaxpayerCaseProfile;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.commons.ZcaCalendar;
import java.util.Collections;
import java.util.List;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author zhijun98
 */
@Named(value = "taxpayerCaseMgtBean")
@ViewScoped
public class TaxpayerCaseMgtBean extends TaxpayerCaseBean{
    
    @Override
    public String getRosePageTopic() {
        if (isForCreateNewEntity()){
            return RoseText.getText("StartNewIndividualTaxFiling") + " - " + RoseText.getText("Deadline") + ": " 
                    + ZcaCalendar.convertToMMddyyyy(getRoseSettings().getNextIndividualTaxFilingDeadlineDate(), "-");
        }else{
            return RoseText.getText("CustomerIndividualTax") + " - " + getTargetTaxpayerCaseProfile().getProfileDescriptiveName();
        }
    }

    public List<TaxpayerCaseProfile> getCustomerHistoricalTaxpayerCaseProfileList() {
        List<TaxpayerCaseProfile> result = getTaxpayerEJB().findTaxpayerCaseProfileListByCustomerUuid(this.getRequestedCustomerUuid());
        Collections.sort(result, (TaxpayerCaseProfile o1, TaxpayerCaseProfile o2) -> o1.getTaxpayerCaseEntity().getDeadline().compareTo(o2.getTaxpayerCaseEntity().getDeadline())*(-1));
        return result;
    }
}
