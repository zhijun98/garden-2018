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

import com.zcomapproach.garden.persistence.entity.G01Employee;

/**
 * EmployeeAccountProfile is a RoseAccountProfile
 * @author zhijun98
 */
public class EmployeeAccountProfile extends RoseAccountProfile{

    private G01Employee employeeEntity;

    public EmployeeAccountProfile() {
        this.employeeEntity = new G01Employee();
    }

    public G01Employee getEmployeeEntity() {
        return employeeEntity;
    }

    public void setEmployeeEntity(G01Employee employeeEntity) {
        this.employeeEntity = employeeEntity;
    }

    @Override
    public String getProfileName() {
        return this.getUserProfile().getProfileName();
    }

    @Override
    public String getProfileDescriptiveName() {
        return getUserProfile().getProfileDescriptiveName();
    }
    
}
