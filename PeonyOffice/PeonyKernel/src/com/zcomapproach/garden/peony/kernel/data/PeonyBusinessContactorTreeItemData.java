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

package com.zcomapproach.garden.peony.kernel.data;

import com.zcomapproach.garden.peony.view.data.PeonyTreeItemData;
import com.zcomapproach.garden.persistence.entity.G02BusinessContactor;

/**
 *
 * @author zhijun98
 */
public class PeonyBusinessContactorTreeItemData extends PeonyTreeItemData{
    
    private G02BusinessContactor businessContactor;

    public PeonyBusinessContactorTreeItemData(Object treeItemData, Status treeItemDataMode) {
        super(treeItemData, treeItemDataMode);
        if (treeItemData instanceof G02BusinessContactor){
            businessContactor = (G02BusinessContactor)treeItemData;
        }
    }

    public G02BusinessContactor getBusinessContactor() {
        return businessContactor;
    }

    @Override
    public String toString() {
        if (businessContactor == null){
            Object obj = getTreeItemData();
            if (obj instanceof String) {
                return obj.toString();
            }else{
                return "Unknown Value";
            }
        }else{
            return businessContactor.getContactType() + ": " + businessContactor.getContactInfo();
        }
    }

}
