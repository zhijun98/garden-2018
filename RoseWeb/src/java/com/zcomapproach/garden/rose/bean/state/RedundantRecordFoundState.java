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

/**
 * When user record and or its account were created, based on the input information, Rose may find some records has 
 * exactly same data, e.g., phone, email, ssn, etc. It is very possible such information already stored in the database. 
 * In this case, give a chance for user to find theire records or recover their records by emailing/SMS the secrete code. 
 * This class contains the state of redundant records found
 *
 * @author zhijun98
 */
public abstract class RedundantRecordFoundState {

    /**
     * if true, it means user chose to store the current record anyway. If no, it means user chose to give up current input 
     * and possibly to find/recover the existing record.
     */
    private boolean ignoreRedundancy = false;

    /**
     * The managed-bean, which contains this state. may use of this method to decide if display redundancy control panel 
     * for users to make decision on "ignoreRedundancy".
     * @return 
     */
    public abstract boolean isRedundantRecordFound();
    
    public boolean isIgnoreRedundancy() {
        return ignoreRedundancy;
    }

    public void setIgnoreRedundancy(boolean ignoreRedundancy) {
        this.ignoreRedundancy = ignoreRedundancy;
    }

}
