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
public abstract class AbstractRoseEntityProfile extends AbstractRoseProfile {
    
    /**
     * whether or not the main entity in this profile is a brand new instance which is initiated from Java-new but not 
     * from the garden database
     */
    private boolean brandNew = true;
    
    /**
     * clone data from srcProfile to this profile
     * @param srcProfile
     */
    public abstract void cloneProfile(AbstractRoseEntityProfile srcProfile);

    public boolean isBrandNew() {
        return brandNew;
    }

    public void setBrandNew(boolean brandNew) {
        this.brandNew = brandNew;
    }

}
