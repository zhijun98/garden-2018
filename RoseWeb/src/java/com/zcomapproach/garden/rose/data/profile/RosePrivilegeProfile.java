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

import com.zcomapproach.garden.persistence.constant.GardenPrivilege;
import com.zcomapproach.garden.persistence.constant.GardenPrivilegeGroup;

/**
 *
 * @author zhijun98
 */
public class RosePrivilegeProfile extends AbstractRoseEntityProfile{
    
    private final GardenPrivilege privilege;
    private final GardenPrivilegeGroup type;

    public RosePrivilegeProfile(GardenPrivilege privilege, GardenPrivilegeGroup type) {
        this.privilege = privilege;
        this.type = type;
    }

    public GardenPrivilege getPrivilege() {
        return privilege;
    }

    public GardenPrivilegeGroup getType() {
        return type;
    }

    @Override
    public String getProfileName() {
        return privilege.value();
    }

    @Override
    public String getProfileDescriptiveName() {
        return GardenPrivilege.getParamDescription(privilege);
    }

    @Override
    protected String getProfileUuid() {
        return privilege.name();
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        //no clone
    }

}
