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

package com.zcomapproach.garden.rose.data.model;

import com.zcomapproach.garden.data.GardenFlower;
import com.zcomapproach.garden.data.GardenSettingsProperty;
import com.zcomapproach.garden.persistence.entity.G01SystemProperty;
import com.zcomapproach.garden.persistence.entity.G01SystemPropertyPK;
import com.zcomapproach.garden.rose.data.profile.RoseSettingsProfile;
import com.zcomapproach.commons.ZcaValidator;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zhijun98
 */
public class RoseSettingsGridModel {

    private RoseSettingsProfile roseSettingsProfile;
    
    private List<RoseSettingsRowData> roseSettingsRowDataList;

    public RoseSettingsGridModel() {
    }
    
    /**
     * 
     * @param roseSettingsProfile
     * @param columnNumber - the number of columns in the grid
     */
    public void initializeDataModel(RoseSettingsProfile roseSettingsProfile, int columnNumber){
        this.roseSettingsProfile = roseSettingsProfile;
        roseSettingsRowDataList = new ArrayList<>();
        List<String> propNameList = roseSettingsProfile.getRoseSettingsPropertyList();
        if ((propNameList == null)||(propNameList.isEmpty())){
            return;
        }
        int totalPropNameNumber = propNameList.size();
        int rowNumber = (-1)*Math.floorDiv(totalPropNameNumber*(-1), columnNumber);
        RoseSettingsRowData rowData;
        RoseSettingsColumnData columnData;
        int propNameIndex = 0;

        String propName;
        GardenSettingsProperty prop;
        for (int i = 0; i < rowNumber; i++){
            rowData = new RoseSettingsRowData();
            for (int j = 0; j < columnNumber; j++){
                if (totalPropNameNumber <= propNameIndex){
                    break;
                }
                propName = propNameList.get(propNameIndex);

                columnData = new RoseSettingsColumnData();

                columnData.setPropertyDescription(roseSettingsProfile.getGardenSettingsPropertyDescription(propName));
                if (ZcaValidator.isNullEmpty(columnData.getPropertyDescription())){
                    columnData.setPropertyDescription(roseSettingsProfile.getGardenSettingsPropertyDescription(GardenSettingsProperty.convertEnumNameToType(propName)));
                }

                prop = GardenSettingsProperty.convertEnumNameToType(propName);
                if (GardenSettingsProperty.UNKNOWN.equals(prop)){
                    columnData.setPropertyReadableName(propName);
                }else{
                    columnData.setPropertyReadableName(prop.value());
                }
                columnData.setPropertyValue(roseSettingsProfile.getGardenSettingsValue(propName));

                rowData.getRoseSettingsColumnDataList().add(columnData);
                propNameIndex++;
            }
            roseSettingsRowDataList.add(rowData);
        }
    
    }

    public List<RoseSettingsRowData> getRoseSettingsRowDataList() {
        return roseSettingsRowDataList;
    }

    public RoseSettingsProfile getRoseSettingsProfile() {
        return roseSettingsProfile;
    }

    /**
     * Retrieve a collection of G01SystemProperty instances for storage. This method will do validation on the entity instance
     * @return 
     */
    public RoseSettingsProfile retrieveRoseSettingsProfileFromGridModel() {
        List<G01SystemProperty> aG01SystemPropertyList = new ArrayList<>();
        List<RoseSettingsColumnData> aRoseSettingsColumnDataList;
        G01SystemProperty aG01SystemProperty;
        G01SystemPropertyPK pkId;
        GardenSettingsProperty prop;
        for(RoseSettingsRowData aRoseSettingsRowData : roseSettingsRowDataList){
            aRoseSettingsColumnDataList = aRoseSettingsRowData.getRoseSettingsColumnDataList();
            for (RoseSettingsColumnData aRoseSettingsColumnData : aRoseSettingsColumnDataList){
                aG01SystemProperty = new G01SystemProperty();
                //PKID
                pkId = new G01SystemPropertyPK();
                pkId.setFlowerName(GardenFlower.ROSE.name());
                prop = GardenSettingsProperty.convertEnumValueToType(aRoseSettingsColumnData.getPropertyReadableName(), true);
                if (GardenSettingsProperty.UNKNOWN.equals(prop)){
                    pkId.setPropertyName(aRoseSettingsColumnData.getPropertyReadableName());
                }else{
                    pkId.setPropertyName(prop.name());
                }
                aG01SystemProperty.setG01SystemPropertyPK(pkId);
                //prop-value
                aG01SystemProperty.setPropertyValue(aRoseSettingsColumnData.getPropertyValue());
                //description
                aG01SystemProperty.setDescription(aRoseSettingsColumnData.getPropertyDescription());
                //add into the list
                aG01SystemPropertyList.add(aG01SystemProperty);
            }
        }
        
        roseSettingsProfile.initializeRoseSettings(aG01SystemPropertyList);
        
        return roseSettingsProfile;
    }
    
}
