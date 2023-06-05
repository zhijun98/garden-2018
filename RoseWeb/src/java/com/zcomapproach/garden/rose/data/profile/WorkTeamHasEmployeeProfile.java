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

import com.zcomapproach.garden.persistence.entity.G01WorkTeamHasEmployee;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;

/**
 *
 * @author zhijun98
 */
public class WorkTeamHasEmployeeProfile extends AbstractRoseEntityProfile {

    private G01WorkTeamHasEmployee workTeamHasEmployeeEntity;
    private EmployeeAccountProfile employeeProfile;

    public WorkTeamHasEmployeeProfile() {
        workTeamHasEmployeeEntity = new G01WorkTeamHasEmployee();
        employeeProfile =  new EmployeeAccountProfile();
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof WorkTeamHasEmployeeProfile)){
            return;
        }
        WorkTeamHasEmployeeProfile srcWorkTeamHasEmployeeProfile = (WorkTeamHasEmployeeProfile)srcProfile;
        
        G01DataUpdaterFactory.getSingleton().getG01WorkTeamHasEmployeeUpdater().cloneEntity(srcWorkTeamHasEmployeeProfile.getWorkTeamHasEmployeeEntity(), 
                                                                                               this.getWorkTeamHasEmployeeEntity());
        employeeProfile.cloneProfile(srcWorkTeamHasEmployeeProfile.getEmployeeProfile());
    }

    public G01WorkTeamHasEmployee getWorkTeamHasEmployeeEntity() {
        return workTeamHasEmployeeEntity;
    }

    public void setWorkTeamHasEmployeeEntity(G01WorkTeamHasEmployee workTeamHasEmployeeEntity) {
        this.workTeamHasEmployeeEntity = workTeamHasEmployeeEntity;
    }

    public EmployeeAccountProfile getEmployeeProfile() {
        if (employeeProfile == null){
            employeeProfile =  new EmployeeAccountProfile();
        }
        return employeeProfile;
    }

    public void setEmployeeProfile(EmployeeAccountProfile employeeProfile) {
        this.employeeProfile = employeeProfile;
    }

    @Override
    public String getProfileName() {
        return getEmployeeProfile().getProfileName();
    }

    @Override
    public String getProfileDescriptiveName() {
        return getEmployeeProfile().getProfileDescriptiveName();
    }

    @Override
    protected String getProfileUuid() {
        return workTeamHasEmployeeEntity.getG01WorkTeamHasEmployeePK().getWorkTeamUuid() 
                + ": " + workTeamHasEmployeeEntity.getG01WorkTeamHasEmployeePK().getEmployeeUuid();
    }
    
    
}
