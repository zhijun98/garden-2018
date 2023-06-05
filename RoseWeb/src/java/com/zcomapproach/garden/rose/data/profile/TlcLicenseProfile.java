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

import com.zcomapproach.garden.persistence.entity.G01TlcLicense;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;

/**
 *
 * @author zhijun98
 */
public class TlcLicenseProfile extends AbstractRoseEntityProfile{

    private G01TlcLicense tlcLicenseEntity;

    public TlcLicenseProfile() {
        this.tlcLicenseEntity = new G01TlcLicense();
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof TlcLicenseProfile)){
            return;
        }
        TlcLicenseProfile srcTlcLicenseProfile = (TlcLicenseProfile)srcProfile;
        G01DataUpdaterFactory.getSingleton().getG01TlcLicenseUpdater().cloneEntity(srcTlcLicenseProfile.getTlcLicenseEntity(), 
                                                                                  this.getTlcLicenseEntity());
    }

    public G01TlcLicense getTlcLicenseEntity() {
        return tlcLicenseEntity;
    }

    public void setTlcLicenseEntity(G01TlcLicense tlcLicenseEntity) {
        this.tlcLicenseEntity = tlcLicenseEntity;
    }
    
    @Override
    public String getProfileName() {
        return tlcLicenseEntity.getDriverUuid();
    }

    @Override
    public String getProfileDescriptiveName() {
        return tlcLicenseEntity.getDriverUuid();
    }

    @Override
    protected String getProfileUuid() {
        return tlcLicenseEntity.getDriverUuid();
    }

}
