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

import com.zcomapproach.garden.persistence.entity.G01User;

/**
 *
 * @author zhijun98
 */
public class RoseUserConciseProfile extends AbstractEntityConciseProfile{
    
    private G01User g01User;

    public RoseUserConciseProfile() {
    }

    public G01User getG01User() {
        if (g01User == null){
            g01User = new G01User();
        }
        return g01User;
    }

    public void setG01User(G01User g01User) {
        this.g01User = g01User;
    }

    @Override
    public String getProfileName() {
        return getG01User().getFirstName() + " " + getG01User().getLastName();
    }

    @Override
    public String getProfileDescriptiveName() {
        return getProfileName() + " ("+this.getSimilarityValue()+")";
    }

    @Override
    protected String getProfileUuid() {
        return getG01User().getUserUuid();
    }

}
