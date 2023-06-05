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
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.commons.ZcaCalendar;

/**
 *
 * @author zhijun98
 */
public class RoseContactMessageProfile extends AbstractRoseEntityProfile{
    
    private G01ContactMessage contactMessageEntity;

    public RoseContactMessageProfile() {
        contactMessageEntity = new G01ContactMessage();
    }

    public G01ContactMessage getContactMessageEntity() {
        return contactMessageEntity;
    }

    public void setContactMessageEntity(G01ContactMessage contactMessageEntity) {
        this.contactMessageEntity = contactMessageEntity;
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof RoseContactMessageProfile)){
            return;
        }
        RoseContactMessageProfile srcRoseContactMessageProfile = (RoseContactMessageProfile)srcProfile;
        //chatMessageEntity
        G01DataUpdaterFactory.getSingleton().getG01ContactMessageUpdater().cloneEntity(srcRoseContactMessageProfile.getContactMessageEntity(), this.getContactMessageEntity());
    }

    @Override
    public String getProfileName() {
        return getContactMessageEntity().getContactSubject();
    }

    @Override
    public String getProfileDescriptiveName() {
        return getProfileName() + ": " 
                + ZcaCalendar.convertToMMddyyyyHHmmss(getContactMessageEntity().getContactTimestamp(), "-", " @ ", ":");
    }

    @Override
    protected String getProfileUuid() {
        return getContactMessageEntity().getContactMessageUuid();
    }

    
    
}
