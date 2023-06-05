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

package com.zcomapproach.garden.peony.view.data;

import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G02CommAssignment;
import com.zcomapproach.garden.persistence.entity.G02CommAssignmentTarget;
import com.zcomapproach.garden.persistence.peony.PeonyAccount;
import com.zcomapproach.commons.ZcaCalendar;
import java.util.Objects;

/**
 *
 * @author zhijun98
 */
public class PeonyCommAssignmentTreeItemData extends PeonyTreeItemData{
    
    private final PeonyAccount peonyEmployeeAccount;
    
    public PeonyCommAssignmentTreeItemData(Object treeItemData, PeonyAccount peonyEmployeeAccount, Status treeItemDataMode) {
        super(treeItemData, treeItemDataMode);
        this.peonyEmployeeAccount = peonyEmployeeAccount;
    }

    public PeonyAccount getPeonyEmployeeAccount() {
        return peonyEmployeeAccount;
    }

    public G02CommAssignment getG02CommAssignment() {
        if (getTreeItemData() instanceof G02CommAssignment){
            return (G02CommAssignment)getTreeItemData();
        }else{
            return null;
        }
    }

    public G02CommAssignmentTarget getG02CommAssignmentTarget() {
        if (getTreeItemData() instanceof G02CommAssignmentTarget){
            return (G02CommAssignmentTarget)getTreeItemData();
        }else{
            return null;
        }
    }
    
    @Override
    public String toString() {
        Object data = getTreeItemData();
        String result = "SMS Records";
        if (peonyEmployeeAccount != null){
            result = peonyEmployeeAccount.getPeonyUserFullName() + ": ";
        }
        if (data instanceof G02CommAssignment){
            G02CommAssignment aG02CommAssignment = (G02CommAssignment)data;
            result += aG02CommAssignment.getDescription();
            result += " ("+ZcaCalendar.convertToMMddyyyyHHmmss(aG02CommAssignment.getCreated(), "-", "@", ":")+")";
            
        }else if (data instanceof G02CommAssignmentTarget){
            G02CommAssignmentTarget aG02CommAssignmentTarget = (G02CommAssignmentTarget)data;
            result = "Contact: " + aG02CommAssignmentTarget.getTargetMemo();
            result += " ("+GardenEntityType.getParamDescription(GardenEntityType.convertEnumValueToType(aG02CommAssignmentTarget.getTargetEntityType()))+")";
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.getTreeItemData());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PeonyCommAssignmentTreeItemData other = (PeonyCommAssignmentTreeItemData) obj;
        if (!Objects.equals(this.getTreeItemData() , other.getTreeItemData() )) {
            return false;
        }
        return true;
    }

}
