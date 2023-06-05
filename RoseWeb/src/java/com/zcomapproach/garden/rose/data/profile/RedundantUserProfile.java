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

/**
 *
 * @author zhijun98
 */
public class RedundantUserProfile extends RoseUserProfile{

    /**
     * description on the redundant data of the interested field is redundant
     */
    private String redundantDataDescription;
    
    /**
     * redundant data of the target field 
     */
    private String redundantData;

    public String getRedundantDataDescription() {
        return redundantDataDescription;
    }

    public void setRedundantDataDescription(String redundantDataDescription) {
        this.redundantDataDescription = redundantDataDescription;
    }

    public String getRedundantData() {
        return redundantData;
    }

    public void setRedundantData(String redundantData) {
        this.redundantData = redundantData;
    }
    
}
