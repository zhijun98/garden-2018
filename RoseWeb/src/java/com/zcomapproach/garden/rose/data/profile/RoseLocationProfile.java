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

import com.zcomapproach.garden.persistence.constant.GardenPreference;
import com.zcomapproach.garden.persistence.entity.G01Location;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaValidator;

/**
 *
 * @author zhijun98
 */
public class RoseLocationProfile extends AbstractRoseEntityProfile{
    
    private G01Location locationEntity;

    public RoseLocationProfile() {
        this.locationEntity = new G01Location();
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof RoseLocationProfile)){
            return;
        }
        RoseLocationProfile srcRoseLocationProfile = (RoseLocationProfile)srcProfile;
        G01DataUpdaterFactory.getSingleton().getG01LocationUpdater().cloneEntity(srcRoseLocationProfile.getLocationEntity(), this.getLocationEntity());
    }

    @Override
    public String getProfileName() {
        return locationEntity.getLocalAddress();
    }

    @Override
    public String getProfileDescriptiveName() {
        return locationEntity.getLocalAddress() 
                + (ZcaValidator.isNullEmpty(locationEntity.getCityName()) ? "": ", " + locationEntity.getCityName())
                + (ZcaValidator.isNullEmpty(locationEntity.getStateCounty()) ? "": ", " + locationEntity.getStateCounty())
                + (ZcaValidator.isNullEmpty(locationEntity.getStateName()) ? "": ", " + locationEntity.getStateName())
                + (ZcaValidator.isNullEmpty(locationEntity.getZipCode()) ? "": " " + locationEntity.getZipCode());
    }

    @Override
    protected String getProfileUuid() {
        return getProfileDescriptiveName();
    }

    public G01Location getLocationEntity() {
        return locationEntity;
    }

    public void setLocationEntity(G01Location locationEntity) {
        this.locationEntity = locationEntity;
    }
    
    public String getAddressForWebFace(){
        return locationEntity.getLocalAddress() + ", " 
                + locationEntity.getCityName() + ", " 
                + locationEntity.getStateName() + " "
                + locationEntity.getZipCode();
    }
    
    public String getMemoForWebFace(){
        String result = getPreferenceValue();
        if (ZcaValidator.isNotNullEmpty(this.locationEntity.getShortMemo())){
            if (ZcaValidator.isNullEmpty(result)){
                result = RoseText.getText("Memo") + ": " + this.locationEntity.getShortMemo();
            }else{
                result = RoseText.getText("Memo") + ": " + this.locationEntity.getShortMemo() + " - " + result;
            }
        }
        if (ZcaValidator.isNullEmpty(result)){
            result = ZcaCalendar.convertToMMddyyyyHHmmss(locationEntity.getUpdated(), "-", " ", ":");
        }
        return result;
    }

    public String getPreferenceValue(){
        GardenPreference pref = GardenPreference.convertIndexToType(locationEntity.getPreferencePriority());
        if (GardenPreference.UNKNOWN.equals(pref)){
            return "";
        }else{
            return pref.value();
        }
    }

    public void setPreferenceValue(String preferenceValue){
        locationEntity.setPreferencePriority(GardenPreference.convertEnumValueToType(preferenceValue).ordinal());
    }

}
