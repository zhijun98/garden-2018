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

package com.zcomapproach.garden.peony.taxcorp.events;

import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.persistence.peony.PeonyTaxcorpCaseList;

/**
 *
 * @author zhijun98
 */
public class RequestToDisplayPeonyTaxcorpCaseList extends PeonyFaceEvent{

    private final PeonyTaxcorpCaseList peonyTaxcorpCaseList;
    
    public RequestToDisplayPeonyTaxcorpCaseList(PeonyTaxcorpCaseList peonyTaxcorpCaseList) {
        this.peonyTaxcorpCaseList = peonyTaxcorpCaseList;
    }

    public PeonyTaxcorpCaseList getPeonyTaxcorpCaseList() {
        return peonyTaxcorpCaseList;
    }

}
