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

package com.zcomapproach.garden.rose.bean.state;

import com.zcomapproach.garden.rose.data.profile.RoseChatMessageProfile;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author zhijun98
 */
public class RoseChatMessageTreeNode extends DefaultTreeNode {

    private RoseChatMessageProfile gardenMessageProfile;

    public RoseChatMessageTreeNode(RoseChatMessageProfile gardenMessageProfile, TreeNode parent) {
        super(gardenMessageProfile, parent);
        this.gardenMessageProfile = gardenMessageProfile;
    }

    public RoseChatMessageProfile getGardenMessageProfile() {
        return gardenMessageProfile;
    }

    public void setGardenMessageProfile(RoseChatMessageProfile gardenMessageProfile) {
        super.setData(gardenMessageProfile);
        this.gardenMessageProfile = gardenMessageProfile;
    }
    
    public String getMessageLine(){
        return gardenMessageProfile.getMessageLine();
    }

}
