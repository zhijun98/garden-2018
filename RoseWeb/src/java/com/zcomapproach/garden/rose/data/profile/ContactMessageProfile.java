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

package com.zcomapproach.garden.rose.data.profile;

import com.zcomapproach.garden.persistence.entity.G01ContactMessage;
import com.zcomapproach.garden.persistence.entity.G01User;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.garden.rose.util.RoseDataAgent;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaValidator;

/**
 *
 * @author zhijun98
 */
public class ContactMessageProfile extends AbstractRoseEntityProfile{

    private G01ContactMessage contactMessageEntity;
    private G01User employeeUserEntity;

    public ContactMessageProfile() {
        this.contactMessageEntity = new G01ContactMessage();
        this.employeeUserEntity = new G01User();
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof ContactMessageProfile)){
            return;
        }
        ContactMessageProfile srcContactMessageProfile = (ContactMessageProfile)srcProfile;
        //contactMessageEntity
        G01DataUpdaterFactory.getSingleton().getG01ContactMessageUpdater().cloneEntity(srcContactMessageProfile.getContactMessageEntity(), 
                                                                                                this.getContactMessageEntity());
        //employeeUserEntity
        G01DataUpdaterFactory.getSingleton().getG01UserUpdater().cloneEntity(srcContactMessageProfile.getEmployeeUserEntity(), 
                                                                                                this.getEmployeeUserEntity());
    }

    @Override
    public String getProfileName() {
        if (contactMessageEntity == null){
            return "Unknown";
        }
        return contactMessageEntity.getContactSubject() + " ["+ getMessageTimestamp() +"]";
    }

    @Override
    public String getProfileDescriptiveName() {
        String userName = getMessageEmployeeName();
        if (ZcaValidator.isNullEmpty(userName)){
            return getProfileName();
        }else{
            return getProfileName().replace("]", " by " + userName + "]");
        }
    }

    @Override
    protected String getProfileUuid() {
        return getContactMessageEntity().getContactMessageUuid();
    }
    
    public String getMessageEmployeeName(){
        return RoseDataAgent.retrieveUserName(employeeUserEntity);
    }
    
    public String getMessageTimestamp(){
        return ZcaCalendar.convertToMMddyyyyHHmmss(contactMessageEntity.getContactTimestamp(), "-", " @ ", ":");
    }

    public G01ContactMessage getContactMessageEntity() {
        return contactMessageEntity;
    }

    public void setContactMessageEntity(G01ContactMessage contactMessageEntity) {
        this.contactMessageEntity = contactMessageEntity;
    }

    public G01User getEmployeeUserEntity() {
        return employeeUserEntity;
    }

    public void setEmployeeUserEntity(G01User employeeUserEntity) {
        this.employeeUserEntity = employeeUserEntity;
    }
    
}
