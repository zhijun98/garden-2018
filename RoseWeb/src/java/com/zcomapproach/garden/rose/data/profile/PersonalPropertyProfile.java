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

import com.zcomapproach.garden.persistence.entity.G01PersonalProperty;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;

/**
 *
 * @author zhijun98
 */
public class PersonalPropertyProfile extends AbstractRoseEntityProfile{

    private G01PersonalProperty personalPropertyEntity;

    public PersonalPropertyProfile() {
        this.personalPropertyEntity = new G01PersonalProperty();
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof PersonalPropertyProfile)){
            return;
        }
        PersonalPropertyProfile srcPersonalPropertyProfile = (PersonalPropertyProfile)srcProfile;
        G01DataUpdaterFactory.getSingleton().getG01PersonalPropertyUpdater().cloneEntity(srcPersonalPropertyProfile.getPersonalPropertyEntity(), 
                                                                                        this.getPersonalPropertyEntity());
    }

    public G01PersonalProperty getPersonalPropertyEntity() {
        return personalPropertyEntity;
    }

    public void setPersonalPropertyEntity(G01PersonalProperty personalPropertyEntity) {
        this.personalPropertyEntity = personalPropertyEntity;
    }

    @Override
    public String getProfileName() {
        return personalPropertyEntity.getPersonalPropertyUuid();
    }

    @Override
    public String getProfileDescriptiveName() {
        return personalPropertyEntity.getPersonalPropertyUuid();
    }

    @Override
    protected String getProfileUuid() {
        return personalPropertyEntity.getPersonalPropertyUuid();
    }
    
}
