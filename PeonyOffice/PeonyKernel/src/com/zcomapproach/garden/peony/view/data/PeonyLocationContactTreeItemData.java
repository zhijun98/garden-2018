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

import com.zcomapproach.garden.peony.utils.PeonyDataUtils;
import com.zcomapproach.garden.persistence.entity.G02ContactInfo;
import com.zcomapproach.garden.persistence.entity.G02Location;
import java.util.Objects;

/**
 *
 * @author zhijun98
 */
public class PeonyLocationContactTreeItemData extends PeonyTreeItemData {

    private String treeItemTitle;
    private G02Location location;
    private G02ContactInfo contactInfo;

    public PeonyLocationContactTreeItemData() {
        super("Location & Contact Information", Status.INITIAL);
        this.treeItemTitle = "Location & Contact Information";
    }

    public PeonyLocationContactTreeItemData(String treeItemTitle) {
        super(treeItemTitle, Status.INITIAL);
        this.treeItemTitle = treeItemTitle;
    }

    public PeonyLocationContactTreeItemData(G02Location location) {
        super(location, Status.INITIAL);
        this.location = location;
        this.contactInfo = null;
        this.treeItemTitle = null;
    }

    public PeonyLocationContactTreeItemData(G02ContactInfo contactInfo) {
        super(contactInfo, Status.INITIAL);
        this.location = null;
        this.contactInfo = contactInfo;
        this.treeItemTitle = null;
    }

    public PeonyLocationContactTreeItemData(Object treeItemData, Status treeItemDataMode) {
        super(treeItemData, treeItemDataMode);
        if (treeItemData instanceof G02Location){
            location = (G02Location)treeItemData;
        }else if (treeItemData instanceof G02ContactInfo){
            contactInfo = (G02ContactInfo)treeItemData;
        }else if (treeItemData instanceof String){
            treeItemTitle = treeItemData.toString();
        }
    }

    public String getTreeItemTitle() {
        return treeItemTitle;
    }

    public void setTreeItemTitle(String treeItemTitle) {
        this.treeItemTitle = treeItemTitle;
    }

    public G02Location getLocation() {
        return location;
    }

    public void setLocation(G02Location location) {
        this.location = location;
    }

    public G02ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(G02ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }
    @Override
    public String toString() {
        Object data = getTreeItemData();
        String result = "Location & Contact Information";
        if (data instanceof G02Location){
            return PeonyDataUtils.generateDataTitle(location);
        }else if (data instanceof G02ContactInfo){
            return PeonyDataUtils.generateDataTitle(contactInfo);
        }else if (data instanceof String){
            return treeItemTitle;
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(getTreeItemData());
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
        final PeonyLocationContactTreeItemData other = (PeonyLocationContactTreeItemData) obj;
        if (!Objects.equals(this.getTreeItemData(), other.getTreeItemData())) {
            return false;
        }
        return true;
    }

    /**
     * if the tree-item-data is STRING, the treeItemTitle returns. Otherwise, the entity UUID returns
     * @return 
     */
    public String getTreeItemUuid() {
        Object data = getTreeItemData();
        if (data instanceof G02Location){
            return location.getLocationUuid();
        }else if (data instanceof G02ContactInfo){
            return contactInfo.getContactInfoUuid();
        }else {
            return treeItemTitle;
        }
    }
    
}
