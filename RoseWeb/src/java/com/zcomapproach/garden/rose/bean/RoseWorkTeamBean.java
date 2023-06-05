/*
 * Copyright 2017 ZComApproach Inc.
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

import com.zcomapproach.garden.persistence.entity.G01WorkTeamHasEmployee;
import com.zcomapproach.garden.persistence.entity.G01WorkTeamHasEmployeePK;
import com.zcomapproach.garden.rose.RosePageName;
import com.zcomapproach.garden.rose.data.profile.EmployeeAccountProfile;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.rose.data.profile.RoseWorkTeamProfile;
import com.zcomapproach.garden.rose.data.profile.WorkTeamHasEmployeeProfile;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.garden.rose.util.RoseWebUtils;
import com.zcomapproach.garden.util.GardenData;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author zhijun98
 */
@Named(value = "roseWorkTeamBean")
@RequestScoped
public class RoseWorkTeamBean extends AbstractRoseComponentBean{
    
    private String requestedWorkTeamUuid;
    
    private RoseWorkTeamProfile targetWorkTeamProfile;
    private List<EmployeeAccountProfile> selectedEmployeeAccountProfileList;
    
    /**
     * Creates a new instance of RoseWorkTeamProfile
     */
    public RoseWorkTeamBean() {
        targetWorkTeamProfile = new RoseWorkTeamProfile();
    }

    private boolean validateTargetWorkTeamProfile() {
        if (ZcaValidator.isNullEmpty(targetWorkTeamProfile.getWorkTeamEntity().getWorkTeamUuid())){
            targetWorkTeamProfile.getWorkTeamEntity().setWorkTeamUuid(GardenData.generateUUIDString());
        }
        targetWorkTeamProfile.setTeamMemberProfileList(constructTeamMemberProfileList());
        return true;
    }

    private List<WorkTeamHasEmployeeProfile> constructTeamMemberProfileList() {
        List<WorkTeamHasEmployeeProfile> result = new ArrayList<>();
        WorkTeamHasEmployeeProfile aWorkTeamHasEmployeeProfile;
        G01WorkTeamHasEmployee aG01WorkTeamHasEmployee;
        G01WorkTeamHasEmployeePK pkId;
        for(EmployeeAccountProfile aEmployeeAccountProfile : selectedEmployeeAccountProfileList){
            //aG01WorkTeamHasEmployee
            pkId = new G01WorkTeamHasEmployeePK();
            pkId.setEmployeeUuid(aEmployeeAccountProfile.getEmployeeEntity().getEmployeeAccountUuid());
            pkId.setWorkTeamUuid(targetWorkTeamProfile.getWorkTeamEntity().getWorkTeamUuid());
            aG01WorkTeamHasEmployee = new G01WorkTeamHasEmployee();
            aG01WorkTeamHasEmployee.setG01WorkTeamHasEmployeePK(pkId);
            aG01WorkTeamHasEmployee.setTimestamp(new Date());
            aG01WorkTeamHasEmployee.setOperatorAccountUuid(getRoseUserSession().getTargetAccountProfile().getAccountEntity().getAccountUuid());
            //aWorkTeamHasEmployeeProfile
            aWorkTeamHasEmployeeProfile = new WorkTeamHasEmployeeProfile();
            aWorkTeamHasEmployeeProfile.setBrandNew(this.isForCreateNewEntity());
            aWorkTeamHasEmployeeProfile.setEmployeeProfile(aEmployeeAccountProfile);
            aWorkTeamHasEmployeeProfile.setWorkTeamHasEmployeeEntity(aG01WorkTeamHasEmployee);
            
            result.add(aWorkTeamHasEmployeeProfile);
        }
        
        return result;
    }

    @Override
    public String getRosePageTopic() {
        return RoseText.getText("WorkTeam");
    }

    @Override
    public String getTopicIconAwesomeName() {
        return "users";
    }

    @Override
    public String getTargetReturnWebPath() {
        return RosePageName.WorkTeamListPage.name() + RoseWebUtils.constructWebQueryString(null, true);
    }

    public List<EmployeeAccountProfile> getSelectedEmployeeAccountProfileList() {
        return selectedEmployeeAccountProfileList;
    }

    public void setSelectedEmployeeAccountProfileList(List<EmployeeAccountProfile> selectedEmployeeAccountProfileList) {
        this.selectedEmployeeAccountProfileList = selectedEmployeeAccountProfileList;
    }

    public String getRequestedWorkTeamUuid() {
        return requestedWorkTeamUuid;
    }

    public void setRequestedWorkTeamUuid(String requestedWorkTeamUuid) {
        if (ZcaValidator.isNotNullEmpty(requestedWorkTeamUuid)){
            try {
                selectedEmployeeAccountProfileList = new ArrayList<>();
                targetWorkTeamProfile = getBusinessEJB().findRoseWorkTeamProfileByWorkTeamUuid(requestedWorkTeamUuid);
                if (targetWorkTeamProfile == null){
                    targetWorkTeamProfile = new RoseWorkTeamProfile();
                    this.setRequestedViewPurpose(getRoseParamValues().getCreateNewEntityParamValue());
                    targetWorkTeamProfile.setBrandNew(true);
                }else{
                    this.setRequestedViewPurpose(getRoseParamValues().getUpdateExistingEntityParamValue());
                    targetWorkTeamProfile.setBrandNew(false);
                    List<WorkTeamHasEmployeeProfile> aWorkTeamHasEmployeeProfileList = targetWorkTeamProfile.getTeamMemberProfileList();
                    for(WorkTeamHasEmployeeProfile aWorkTeamHasEmployeeProfile : aWorkTeamHasEmployeeProfileList){
                        selectedEmployeeAccountProfileList.add(aWorkTeamHasEmployeeProfile.getEmployeeProfile());
                    }
                }
            } catch (Exception ex) {
                //Logger.getLogger(RoseWorkTeamBean.class.getName()).log(Level.SEVERE, null, ex);
                RoseJsfUtils.setGlobalErrorFacesMessage(ex.getMessage());
            }
        }
        this.requestedWorkTeamUuid = requestedWorkTeamUuid;
    }

    public List<RoseWorkTeamProfile> getAllRoseWorkTeamProfiles() {
        return getBusinessEJB().findAllRoseWorkTeamProfiles();
    }

    public List<WorkTeamHasEmployeeProfile> getTargetSelectedGardenEmployeeProfiles() {
        return getTargetWorkTeamProfile().getTeamMemberProfileList();
    }

    public void setTargetSelectedGardenEmployeeProfiles(List<WorkTeamHasEmployeeProfile> targetSelectedGardenEmployeeProfiles) {
        this.getTargetWorkTeamProfile().setTeamMemberProfileList(targetSelectedGardenEmployeeProfiles);
    }

    public RoseWorkTeamProfile getTargetWorkTeamProfile() {
        return targetWorkTeamProfile;
    }

    public void setTargetWorkTeamProfile(RoseWorkTeamProfile targetWorkTeamProfile) {
        this.targetWorkTeamProfile = targetWorkTeamProfile;
    }
    
    public String storeTargetWorkTeamProfile(){
        if (validateTargetWorkTeamProfile()){
            //RoseWorkTeamProfile
            try {
                getBusinessEJB().storeRoseWorkTeamProfile(targetWorkTeamProfile);
                RoseJsfUtils.setGlobalSuccessfulOperationMessage();
            } catch (Exception ex) {
                //Logger.getLogger(RoseWorkTeamBean.class.getName()).log(Level.SEVERE, null, ex);
                RoseJsfUtils.setGlobalErrorFacesMessage(ex.getMessage());
            }
        }
        return null;
    }
}
