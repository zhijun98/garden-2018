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

import com.zcomapproach.garden.persistence.entity.G01BusinessVehicle;

/**
 *
 * @author zhijun98
 */
public class BusinessVehicleProfile extends AbstractRoseEntityProfile{

    private G01BusinessVehicle businessVehicleEntity;

    public BusinessVehicleProfile() {
        this.businessVehicleEntity = new G01BusinessVehicle();
    }

    public G01BusinessVehicle getBusinessVehicleEntity() {
        return businessVehicleEntity;
    }

    public void setBusinessVehicleEntity(G01BusinessVehicle businessVehicleEntity) {
        this.businessVehicleEntity = businessVehicleEntity;
    }
    
    @Override
    public String getProfileName() {
        return businessVehicleEntity.getVehicleUuid();
    }

    @Override
    public String getProfileDescriptiveName() {
        return businessVehicleEntity.getVehicleUuid();
    }

    @Override
    protected String getProfileUuid() {
        return businessVehicleEntity.getVehicleUuid();
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        //todo zzj: no implementation yet
    }

}
