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

import com.zcomapproach.garden.data.GardenSettingsProperty;
import com.zcomapproach.garden.persistence.entity.G01SystemProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * RoseSettingsProfile contains all the records of ROSE (i.e. flower_name='ROSE') in gxx_settings table
 * @author zhijun98
 */
public class RoseSettingsProfile extends AbstractRoseEntityProfile {
    /**
     * key: G01SystemProperty.propertyName which coud be GardenSettingsProperty.name or user-defined property name
     * value: G01SystemProperty instance
     */
    private final HashMap<String, G01SystemProperty> roseSettingsRecordStorage;
    
    /**
     * true if there is no any data in the database.
     */
    private boolean brandNewRose;

    public RoseSettingsProfile() {
        this.roseSettingsRecordStorage = new HashMap<>();
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        Logger.getLogger(RoseSettingsProfile.class.getName()).log(Level.SEVERE, "No implementation: " + RoseSettingsProfile.class.getName());
    }

    public List<String> getRoseSettingsPropertyList(){
        return new ArrayList<>(roseSettingsRecordStorage.keySet());
    }
    
    public List<G01SystemProperty> getG01SystemPropertyList(){
        return new ArrayList<>(roseSettingsRecordStorage.values());
    }
    
    /**
     * 
     * @return - true if there is no any data in the database.
     */
    public boolean isBrandNewRose() {
        return brandNewRose;
    }

    public void setBrandNewRose(boolean brandNewRose) {
        this.brandNewRose = brandNewRose;
    }

    /**
     * todo zzj: in the future, if there is upgraded, e.g. G02Settings, this method, especially how to set brandNewRose 
     * should be redefined in the logic
     * 
     * @param roseSettingsList - a collection of g_settings entities; if NULL or empty, brandNewRose will be true
     */
    public void initializeRoseSettings(List<G01SystemProperty> roseSettingsList) {
        roseSettingsRecordStorage.clear();
        if ((roseSettingsList == null) || (roseSettingsList.isEmpty())){
            List<String> propertyNameList = GardenSettingsProperty.getEnumNameList(false);
            for (String propertyName : propertyNameList){
                roseSettingsRecordStorage.put(propertyName, null);
            }
            setBrandNewRose(true);
            return;
        }
        setBrandNewRose(false);
        for (G01SystemProperty settings : roseSettingsList){
            this.roseSettingsRecordStorage.put(settings.getG01SystemPropertyPK().getPropertyName(), settings);
        }
    }
    
    public String getGardenSettingsValue(GardenSettingsProperty settings){
        G01SystemProperty G01SystemProperty = roseSettingsRecordStorage.get(settings.value());
        if (G01SystemProperty == null){
            return GardenSettingsProperty.getPropertyDefaultValue(settings);  //BUT, keep roseSettingsRecordStorage's property value being empty
        }
        return G01SystemProperty.getPropertyValue();
    }

    /**
     * 
     * @param propertyName - GardenSettingsProperty.name()
     * @return - NULL if nothing found 
     */
    public String getGardenSettingsValue(String propertyName) {
        G01SystemProperty aG01SystemProperty = roseSettingsRecordStorage.get(propertyName);
        if (aG01SystemProperty == null){
            return null;
        }
        return aG01SystemProperty.getPropertyValue();
    }

    /**
     * 
     * @param propertyName - GardenSettingsProperty.name()
     * @return 
     */
    public String getGardenSettingsPropertyDescription(String propertyName) {
        G01SystemProperty aG01SystemProperty = roseSettingsRecordStorage.get(propertyName);
        if (aG01SystemProperty == null){
            return null;
        }
        return aG01SystemProperty.getDescription();
    }

    public String getGardenSettingsPropertyDescription(GardenSettingsProperty settings) {
        G01SystemProperty aG01SystemProperty = roseSettingsRecordStorage.get(settings.name());
        if (aG01SystemProperty == null){
            return GardenSettingsProperty.getParamDescription(settings);  //BUT, keep roseSettingsRecordStorage's property description being empty
        }
        return aG01SystemProperty.getDescription();
    }

    @Override
    public String getProfileName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getProfileDescriptiveName() {
        return this.getClass().getName();
    }

    @Override
    protected String getProfileUuid() {
        return this.toString();
    }
    
}
