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

import com.zcomapproach.garden.rose.data.constant.RoseWebPostPurpose;


/**
 *
 * @author zhijun98
 */
public class RoseWebPostPurposeProfile {
    
    private final String gardenWebPostPurposeWebLabel;
    private final RoseWebPostPurpose gardenWebPostPurpose;

    public RoseWebPostPurposeProfile(String gardenWebPostPurposeWebLabel, RoseWebPostPurpose gardenWebPostPurpose) {
        this.gardenWebPostPurposeWebLabel = gardenWebPostPurposeWebLabel;
        this.gardenWebPostPurpose = gardenWebPostPurpose;
    }

    public RoseWebPostPurpose getGardenWebPostPurpose() {
        return gardenWebPostPurpose;
    }
    
    public String getGardenWebPostPurposeParamValue(){
        return RoseWebPostPurpose.convertTypeToParamValue(gardenWebPostPurpose);
    }
    
    public String getGardenWebPostPurposeDescription(){
        return RoseWebPostPurpose.getParamDescription(gardenWebPostPurpose);
    }
    
    public String getGardenWebPostPurposeWebLabel(){
        return gardenWebPostPurposeWebLabel;
    }

}
