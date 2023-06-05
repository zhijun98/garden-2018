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

import com.zcomapproach.garden.persistence.entity.G01PostSection;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;

/**
 *
 * @author zhijun98
 */
public class RosePostSectionProfile extends AbstractRoseEntityProfile{
    
    private G01PostSection postSectionEntity;
    private boolean confirmDeletion;

    public RosePostSectionProfile() {
        postSectionEntity = new G01PostSection();
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof RosePostSectionProfile)){
            return;
        }
        RosePostSectionProfile srcRosePostSectionProfile = (RosePostSectionProfile)srcProfile;
        G01DataUpdaterFactory.getSingleton().getG01PostSectionUpdater().cloneEntity(srcRosePostSectionProfile.getPostSectionEntity(), this.getPostSectionEntity());
    }

    @Override
    public String getProfileName() {
        return postSectionEntity.getPostSectionUuid();
    }

    @Override
    public String getProfileDescriptiveName() {
        return postSectionEntity.getWebPostUuid() + ": " + getProfileName();
    }

    @Override
    protected String getProfileUuid() {
        return postSectionEntity.getWebPostUuid() + "::" + getProfileName();
    }

    public boolean isConfirmDeletion() {
        return confirmDeletion;
    }

    public void setConfirmDeletion(boolean confirmDeletion) {
        this.confirmDeletion = confirmDeletion;
    }

    public G01PostSection getPostSectionEntity() {
        return postSectionEntity;
    }

    public void setPostSectionEntity(G01PostSection postSectionEntity) {
        this.postSectionEntity = postSectionEntity;
    }

}
