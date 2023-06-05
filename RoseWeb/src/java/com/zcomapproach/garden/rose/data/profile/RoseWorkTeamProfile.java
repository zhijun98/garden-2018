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

import com.zcomapproach.garden.persistence.entity.G01WorkTeam;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zhijun98
 */
public class RoseWorkTeamProfile extends AbstractRoseEntityProfile{

    private G01WorkTeam workTeamEntity;
    
    /**
     * key: team member's UUID
     */
    private List<WorkTeamHasEmployeeProfile> teamMemberProfileList;

    public RoseWorkTeamProfile() {
        workTeamEntity = new G01WorkTeam();
        teamMemberProfileList = new ArrayList<>();
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof RoseWorkTeamProfile)){
            return;
        }
        RoseWorkTeamProfile srcRoseWorkTeamProfile = (RoseWorkTeamProfile)srcProfile;
        
        G01DataUpdaterFactory.getSingleton().getG01WorkTeamUpdater().cloneEntity(srcRoseWorkTeamProfile.getWorkTeamEntity(), this.getWorkTeamEntity());
        
        List<WorkTeamHasEmployeeProfile> srcWorkTeamHasEmployeeProfileList = srcRoseWorkTeamProfile.getTeamMemberProfileList();
        this.getTeamMemberProfileList().clear();
        WorkTeamHasEmployeeProfile destWorkTeamHasEmployeeProfile;
        for (WorkTeamHasEmployeeProfile srcWorkTeamHasEmployeeProfile : srcWorkTeamHasEmployeeProfileList){
            destWorkTeamHasEmployeeProfile = new WorkTeamHasEmployeeProfile();
            destWorkTeamHasEmployeeProfile.cloneProfile(srcWorkTeamHasEmployeeProfile);
            getTeamMemberProfileList().add(destWorkTeamHasEmployeeProfile);
        }
    }

    public String getPresentativeMemberNames() {
        String presentativeMemberNames = "";
        for (WorkTeamHasEmployeeProfile aWorkTeamHasEmployeeProfile : teamMemberProfileList){
            presentativeMemberNames += aWorkTeamHasEmployeeProfile.getProfileDescriptiveName();
        }
        return presentativeMemberNames;
    }

    public List<WorkTeamHasEmployeeProfile> getTeamMemberProfileList() {
        return teamMemberProfileList;
    }

    public void setTeamMemberProfileList(List<WorkTeamHasEmployeeProfile> teamMemberProfileList) {
        this.teamMemberProfileList = teamMemberProfileList;
    }

    /**
     * 
     * @return - never NULL 
     */
    public G01WorkTeam getWorkTeamEntity() {
        if (workTeamEntity == null){
            workTeamEntity = new G01WorkTeam();
        }
        return workTeamEntity;
    }

    public void setWorkTeamEntity(G01WorkTeam workTeamEntity) {
        this.workTeamEntity = workTeamEntity;
    }
    
    @Override
    public String getProfileName() {
        return getWorkTeamEntity().getTeamName();
    }

    @Override
    public String getProfileDescriptiveName() {
        return getProfileName() + " (" + getWorkTeamEntity().getWorkTeamUuid() + ")";
    }

    @Override
    protected String getProfileUuid() {
        return getWorkTeamEntity().getWorkTeamUuid();
    }
}
