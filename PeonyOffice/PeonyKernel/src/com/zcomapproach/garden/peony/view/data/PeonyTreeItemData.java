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

import java.util.Objects;

/**
 *
 * @author zhijun98
 */
public class PeonyTreeItemData {
    public static enum Status {
        INITIAL, CREATE, READ, UPADTE, DELETE, UNKNOWN
    }
   
    private Status treeItemDataStatus = Status.UNKNOWN;

    private final Object treeItemData;

    /**
     * 
     * @param treeItemData
     * @param treeItemDataMode - if null, it is PeonyTreeItemData.Status.UNKNOWN
     */
    public PeonyTreeItemData(Object treeItemData, Status treeItemDataMode) {
        if (treeItemDataMode == null){
            treeItemDataMode = Status.UNKNOWN;
        }
        this.treeItemData = treeItemData;
        this.treeItemDataStatus = treeItemDataMode;
    }

    public Object getTreeItemData() {
        return treeItemData;
    }

    public Status getTreeItemDataStatus() {
        return treeItemDataStatus;
    }

    public void setTreeItemDataStatus(Status treeItemDataStatus) {
        this.treeItemDataStatus = treeItemDataStatus;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.treeItemData);
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
        final PeonyTreeItemData other = (PeonyTreeItemData) obj;
        if (!Objects.equals(this.treeItemData, other.treeItemData)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PeonyTreeItemData{" + "treeItemData=" + treeItemData + '}';
    }

}
