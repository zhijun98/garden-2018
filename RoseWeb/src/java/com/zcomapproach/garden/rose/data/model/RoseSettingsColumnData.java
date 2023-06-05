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

import com.zcomapproach.garden.util.GardenData;

/**
 *
 * @author zhijun98
 */
public class RoseSettingsColumnData {

    private String dataID;  //GUI may use it as JSF component ID, e.g. defaultSystemPropertiesPanel.xhtml
//    private String predifinedPropertyName;
    private String propertyReadableName;
    private String propertyValue;
    private String propertyDescription;

    public RoseSettingsColumnData() {
        this.dataID = GardenData.generateUUIDString();
    }

    public String getDataID() {
        return dataID;
    }

    public void setDataID(String dataID) {
        this.dataID = dataID;
    }
    
//    private void initialiPropertyReadableName(String propertyReadableName){
//        if (ZcaValidator.isNullEmpty(predifinedPropertyName)){
//            GardenSettingsProperty prop = GardenSettingsProperty.convertEnumValueToType(propertyReadableName);
//            if(GardenSettingsProperty.UNKNOWN.equals(prop)){
//                predifinedPropertyName = GardenData.generateUUIDString();
//            }
//            predifinedPropertyName =  prop.value();
//        }
//    }

    public String getPropertyReadableName() {
        return propertyReadableName;
    }

    public void setPropertyReadableName(String propertyReadableName) {
//        initialiPropertyReadableName(propertyReadableName);
        this.propertyReadableName = propertyReadableName;
    }

//    public String getPredifinedPropertyName() {
//        initialiPropertyReadableName(getPropertyReadableName());
//        return predifinedPropertyName;
//    }
    
    public String getPropertyValue() {
        return propertyValue;

    }
    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getPropertyDescription() {
        return propertyDescription;
    }

    public void setPropertyDescription(String propertyDescription) {
        this.propertyDescription = propertyDescription;
    }
}
