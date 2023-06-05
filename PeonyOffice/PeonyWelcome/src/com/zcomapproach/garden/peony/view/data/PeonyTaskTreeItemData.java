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

import com.zcomapproach.garden.email.GardenEmailMessage;
import com.zcomapproach.garden.persistence.peony.PeonyCommAssignment;
import java.util.Objects;

/**
 *
 * @author zhijun98
 */
public class PeonyTaskTreeItemData {

    private final Object treeItemData;

    public PeonyTaskTreeItemData(Object treeItemData) {
        this.treeItemData = treeItemData;
    }
    
    public String getUuid(){
        if (treeItemData instanceof GardenEmailMessage){
            return ((GardenEmailMessage)treeItemData).getEmailMsgUid();
        }
        return treeItemData.toString();
    }
    
    public GardenEmailMessage getGardenEmailMessage(){
        if (treeItemData instanceof GardenEmailMessage){
            return (GardenEmailMessage)treeItemData;
        }
        return null;
    } 
    
    /**
     * 
     * @return - null if this tree-item does not contain PeonyCommAssignment
     */
    public PeonyCommAssignment getPeonyCommAssignment(){
        if (treeItemData instanceof PeonyCommAssignment){
            return (PeonyCommAssignment)treeItemData;
        }
        return null;
    }

    @Override
    public String toString() {
        if (treeItemData instanceof GardenEmailMessage){
            return ((GardenEmailMessage)treeItemData).retrieveEmailHeadline();
        }
        return treeItemData.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.treeItemData);
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
        final PeonyTaskTreeItemData other = (PeonyTaskTreeItemData) obj;
        return Objects.equals(this.treeItemData, other.treeItemData);
    }

}
