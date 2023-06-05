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

package com.zcomapproach.garden.rose.bean;

import com.zcomapproach.garden.persistence.constant.GardenPrivilege;
import com.zcomapproach.garden.rose.data.profile.RoseAccountProfile;
import com.zcomapproach.garden.rose.data.profile.RosePrivilegeProfile;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author zhijun98
 */
@Named(value = "rosePrivileges")
@ApplicationScoped
public class RosePrivilegesBean {
    /**
     * All available privileges in the system 
     */
    private List<RosePrivilegeProfile> allRosePrivilegeProfiles;
    
    @PostConstruct
    public void constructRosePrivileges(){
        if ((allRosePrivilegeProfiles == null) || (allRosePrivilegeProfiles.isEmpty())){
            allRosePrivilegeProfiles = new ArrayList<>();
            GardenPrivilege[] valueArray = GardenPrivilege.values();
            if (valueArray != null){
                for (GardenPrivilege valueObj : valueArray){
                    if ((!(valueObj.equals(GardenPrivilege.UNKNOWN))) 
                            && (!(valueObj.equals(GardenPrivilege.SUPER_POWER))))
                    {
                        allRosePrivilegeProfiles.add(new RosePrivilegeProfile(valueObj, GardenPrivilege.getGardenPrivilegeGroup(valueObj)));
                    }
                }//for
            }//if
        }
    }
    
    public List<RosePrivilegeProfile> getAllRosePrivilegeProfiles() {
        return allRosePrivilegeProfiles;
    }
    
    /**
     * Check if aRoseAccountProfile is authorized to control privileges
     * @param aRoseAccountProfile
     * @return 
     */
    public boolean checkSuperPowerAuthorized(RoseAccountProfile aRoseAccountProfile){
        if (aRoseAccountProfile == null){
            return false;
        }
        return aRoseAccountProfile.isAuthorized(GardenPrivilege.SUPER_POWER);
    }
    
    public boolean checkViewEmployeeAuthorized(RoseAccountProfile aRoseAccountProfile){
        if (aRoseAccountProfile == null){
            return false;
        }
        return aRoseAccountProfile.isAuthorized(GardenPrivilege.VIEW_EMPLOYEE);
    }
    
    public boolean checkDeleteEmployeeAuthorized(RoseAccountProfile aRoseAccountProfile){
        if (aRoseAccountProfile == null){
            return false;
        }
        return aRoseAccountProfile.isAuthorized(GardenPrivilege.DELETE_EMPLOYEE);
    }
    
    public boolean checkSaveEmployeeAuthorized(RoseAccountProfile aRoseAccountProfile){
        if (aRoseAccountProfile == null){
            return false;
        }
        return aRoseAccountProfile.isAuthorized(GardenPrivilege.SAVE_EMPLOYEE);
    }
    
    public boolean checkViewCustomerAuthorized(RoseAccountProfile aRoseAccountProfile){
        if (aRoseAccountProfile == null){
            return false;
        }
        return aRoseAccountProfile.isAuthorized(GardenPrivilege.VIEW_CUSTOMER);
    }
    
    public boolean checkDeleteCustomerAuthorized(RoseAccountProfile aRoseAccountProfile){
        if (aRoseAccountProfile == null){
            return false;
        }
        return aRoseAccountProfile.isAuthorized(GardenPrivilege.DELETE_CUSTOMER);
    }
    
    public boolean checkSaveCustomerAuthorized(RoseAccountProfile aRoseAccountProfile){
        if (aRoseAccountProfile == null){
            return false;
        }
        return aRoseAccountProfile.isAuthorized(GardenPrivilege.SAVE_CUSTOMER);
    }
    
    public boolean checkViewUserAuthorized(RoseAccountProfile aRoseAccountProfile){
        if (aRoseAccountProfile == null){
            return false;
        }
        return aRoseAccountProfile.isAuthorized(GardenPrivilege.VIEW_USER);
    }
    
    public boolean checkDeleteUserAuthorized(RoseAccountProfile aRoseAccountProfile){
        if (aRoseAccountProfile == null){
            return false;
        }
        return aRoseAccountProfile.isAuthorized(GardenPrivilege.DELETE_USER);
    }
    
    public boolean checkSaveUserAuthorized(RoseAccountProfile aRoseAccountProfile){
        if (aRoseAccountProfile == null){
            return false;
        }
        return aRoseAccountProfile.isAuthorized(GardenPrivilege.SAVE_USER);
    }
    
}
