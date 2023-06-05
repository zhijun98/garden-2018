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

package com.zcomapproach.garden.peony.customer.data;

import com.zcomapproach.commons.ZcaText;
import com.zcomapproach.garden.peony.view.data.PeonyTreeItemData;
import com.zcomapproach.garden.persistence.entity.G02TulipUser;
import java.util.Objects;

/**
 *
 * @author zhijun98
 */
public class PeonyTulipUserTreeItemData extends PeonyTreeItemData{

    private final String treeItemTitle;
    private final G02TulipUser tulipUser;
    
    public PeonyTulipUserTreeItemData(String treeItemTitle) {
        super(treeItemTitle, Status.INITIAL);
        this.tulipUser = null;
        this.treeItemTitle = treeItemTitle;
    }
    
    public PeonyTulipUserTreeItemData(G02TulipUser tulipUser) {
        super(tulipUser, Status.INITIAL);
        this.tulipUser = tulipUser;
        this.treeItemTitle = null;
    }
    
    public String getTreeItemTitle() {
        return treeItemTitle;
    }

    public G02TulipUser getTulipUser() {
        return tulipUser;
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
        final PeonyTulipUserTreeItemData other = (PeonyTulipUserTreeItemData) obj;
        if (tulipUser == null){
            if (!Objects.equals(this.treeItemTitle, other.treeItemTitle)) {
                return false;
            }
        }else{
            if (!Objects.equals(this.tulipUser, other.tulipUser)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        if (tulipUser != null){
            return tulipUser.getUsername() + " ("+tulipUser.getMobile()+")";
        }else{
            return ZcaText.denullize(treeItemTitle);
        }
    }

}
