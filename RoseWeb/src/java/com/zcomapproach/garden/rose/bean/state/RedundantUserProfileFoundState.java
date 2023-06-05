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

import com.zcomapproach.garden.rose.data.profile.IRoseAccountUserEntityProfile;
import com.zcomapproach.garden.rose.data.profile.RedundantUserProfile;
import java.util.ArrayList;
import java.util.List;

/**
 * When user record and or its account were created, based on the input information, Rose may find some records has 
 * exactly same data, e.g., phone, email, ssn, etc. It is very possible such information already stored in the database. 
 * In this case, give a chance for user to find theire records or recover their records by emailing/SMS the secrete code. 
 * This class contains the state of redundant records found
 *
 * @author zhijun98
 */
public class RedundantUserProfileFoundState extends RedundantRecordFoundState{
    
    /**
     * which profile contains the same records as data in the items of redundantUserProfileList
     */
    private IRoseAccountUserEntityProfile targetAccountUserProfile = null;
    
    private List<RedundantUserProfile> redundantUserProfileList = new ArrayList<>();

    public IRoseAccountUserEntityProfile getTargetAccountUserProfile() {
        return targetAccountUserProfile;
    }

    public void setTargetAccountUserProfile(IRoseAccountUserEntityProfile targetAccountUserProfile) {
        this.targetAccountUserProfile = targetAccountUserProfile;
    }

    public List<RedundantUserProfile> getRedundantUserProfileList() {
        return redundantUserProfileList;
    }

    public void setRedundantUserProfileList(List<RedundantUserProfile> redundantUserProfileList) {
        this.redundantUserProfileList = redundantUserProfileList;
    }

    @Override
    public void setIgnoreRedundancy(boolean ignoreRedundancy) {
        redundantUserProfileList.clear();
        super.setIgnoreRedundancy(ignoreRedundancy);
    }
    
    @Override
    public boolean isRedundantRecordFound() {
        return (!redundantUserProfileList.isEmpty()) && (!isIgnoreRedundancy());
    }

}
