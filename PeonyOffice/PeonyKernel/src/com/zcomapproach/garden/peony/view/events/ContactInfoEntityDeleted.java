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

package com.zcomapproach.garden.peony.view.events;

import com.zcomapproach.garden.persistence.entity.G02ContactInfo;

/**
 *
 * @author zhijun98
 */
public class ContactInfoEntityDeleted extends PeonyFaceEvent{
    
    private final G02ContactInfo contactInfo;

    public ContactInfoEntityDeleted(G02ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    /**
     * it may help to check if the employee instance is NEW or OLD by PeonyEmployee::isNewEntity()
     * @return 
     */
    public G02ContactInfo getContactInfo() {
        return contactInfo;
    }
}

