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
import com.zcomapproach.garden.persistence.entity.G01PersonalBusinessProperty;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.garden.util.GardenData;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zhijun98
 */
public class PersonalBusinessPropertyProfile extends AbstractRoseEntityProfile{

    private G01PersonalBusinessProperty personalBusinessPropertyEntity;
    private List<G01BusinessVehicle> businessVehicleEntityList; //optional: it is reserved for the future

    public PersonalBusinessPropertyProfile() {
        this.personalBusinessPropertyEntity = new G01PersonalBusinessProperty();
        this.businessVehicleEntityList = new ArrayList<>();
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof PersonalBusinessPropertyProfile)){
            return;
        }
        PersonalBusinessPropertyProfile srcPersonalBusinessPropertyProfile = (PersonalBusinessPropertyProfile)srcProfile;
        //personalBusinessPropertyEntity
        G01DataUpdaterFactory.getSingleton().getG01PersonalBusinessPropertyUpdater().cloneEntity(srcPersonalBusinessPropertyProfile.getPersonalBusinessPropertyEntity(), 
                                                                                                this.getPersonalBusinessPropertyEntity());
        //businessVehicleEntityList
        List<G01BusinessVehicle> srcG01BusinessVehicleList = srcPersonalBusinessPropertyProfile.getBusinessVehicleEntityList();
        getBusinessVehicleEntityList().clear();
        G01BusinessVehicle aG01BusinessVehicle;
        for (G01BusinessVehicle srcG01BusinessVehicle : srcG01BusinessVehicleList){
            aG01BusinessVehicle = new G01BusinessVehicle();
            G01DataUpdaterFactory.getSingleton().getG01BusinessVehicleUpdater().cloneEntity(srcG01BusinessVehicle, aG01BusinessVehicle);
            getBusinessVehicleEntityList().add(aG01BusinessVehicle);
        }
    }

    public G01PersonalBusinessProperty getPersonalBusinessPropertyEntity() {
        return personalBusinessPropertyEntity;
    }

    public void setPersonalBusinessPropertyEntity(G01PersonalBusinessProperty personalBusinessPropertyEntity) {
        this.personalBusinessPropertyEntity = personalBusinessPropertyEntity;
    }

    public List<G01BusinessVehicle> getBusinessVehicleEntityList() {
        return businessVehicleEntityList;
    }

    public void setBusinessVehicleEntityList(List<G01BusinessVehicle> businessVehicleEntityList) {
        this.businessVehicleEntityList = businessVehicleEntityList;
    }
    
    public void addNewBusinessVehicleDataEntry(){
        G01BusinessVehicle aG01BusinessVehicle = new G01BusinessVehicle();
        aG01BusinessVehicle.setVehicleUuid(GardenData.generateUUIDString());
        aG01BusinessVehicle.setBusinessPropertyUuid(personalBusinessPropertyEntity.getPersonalBusinessPropertyUuid());
        businessVehicleEntityList.add(aG01BusinessVehicle);
    }
    
    public void removeBusinessVehicle(String vehicleUuid){
        if (vehicleUuid == null){
            return;
        }
        Integer removeIndex = null; 
        for (int i = 0; i < businessVehicleEntityList.size(); i++){
            if (vehicleUuid.equalsIgnoreCase(businessVehicleEntityList.get(i).getVehicleUuid())){
                removeIndex = i;
                break;
            }
        }//for
        if (removeIndex != null){
            businessVehicleEntityList.remove(removeIndex.intValue());
        }
    }
    
    @Override
    public String getProfileName() {
        return personalBusinessPropertyEntity.getPersonalBusinessPropertyUuid();
    }

    @Override
    public String getProfileDescriptiveName() {
        return personalBusinessPropertyEntity.getPersonalBusinessPropertyUuid();
    }

    @Override
    protected String getProfileUuid() {
        return personalBusinessPropertyEntity.getPersonalBusinessPropertyUuid();
    }

}
