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

package com.zcomapproach.garden.rose;

import com.zcomapproach.garden.util.GardenData;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * (1) add a new web-id in RoseWebIdValue.java
 * (2) expose this web-id in RoseWebIdApplicationBean.java
 * (3) implement boolean-function for this new web-id in RoseWebPageConfig.java
 * (4) go to the web page, add web-id, hook web-id's feature (e.g. collapsed), and AJAX event (e.g. onRoseToggleEvent)
 * @author zhijun98
 */
public enum RoseWebIdValue {
    TaxcorpCase_CustomerProfilePanel("R" + GardenData.generateUUIDString()),
    TaxcorpCase_BasicInformationPanel("R" + GardenData.generateUUIDString()),
    TaxcorpCase_ContactorPanel("R" + GardenData.generateUUIDString()),
    TaxcorpCase_UploadedArchivedDocumentPanel("R" + GardenData.generateUUIDString()),
    TaxcorpCase_TaxationPanel("R" + GardenData.generateUUIDString()),
    UNKNOWN("Unknown"); //special usage
    
    private static final HashMap<RoseWebIdValue, String> paramDescriptions = new HashMap<>();
    static{
        paramDescriptions.put(UNKNOWN, "");
    }

    private static HashMap<RoseWebIdValue, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static String getParamDescription(RoseWebIdValue name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<RoseWebIdValue, String> paramValues = new HashMap<>();

    private synchronized static HashMap<RoseWebIdValue, String> getParamValues(){
        if (paramValues.isEmpty()){
            RoseWebIdValue[] valueArray = RoseWebIdValue.values();
            if (valueArray != null){
                for (RoseWebIdValue valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(RoseWebIdValue name){
        return getParamValues().get(name);
    }
    
    public static RoseWebIdValue convertParamValueToType(String webParamValue){
        HashMap<RoseWebIdValue, String> localParamValues = getParamValues();
        Set<RoseWebIdValue> keys = localParamValues.keySet();
        Iterator<RoseWebIdValue> itr = keys.iterator();
        RoseWebIdValue result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<RoseWebIdValue> getRoseWebIdValueList(boolean includeUnknownValue) {
        List<RoseWebIdValue> result = new ArrayList<>();
        RoseWebIdValue[] valueArray = RoseWebIdValue.values();
        if (valueArray != null){
            for (RoseWebIdValue valueObj : valueArray){
                if (includeUnknownValue){
                    result.add(valueObj);
                }else{
                    if (!(valueObj.equals(UNKNOWN))){
                        result.add(valueObj);
                    }
                }
            }//for
        }//if
        return result;
    }

    public static List<String> getEnumValueList(boolean includeUnknownValue){
        List<String> result = new ArrayList<>();
        RoseWebIdValue[] valueArray = RoseWebIdValue.values();
        if (valueArray != null){
            for (RoseWebIdValue valueObj : valueArray){
                if (includeUnknownValue){
                    result.add(valueObj.value());
                }else{
                    if (!(valueObj.value().equalsIgnoreCase(UNKNOWN.value()))){
                        result.add(valueObj.value());
                    }
                }
            }//for
        }//if
        return result;
    }

    public static List<String> getEnumNameList(boolean includeUnknownName){
        List<String> result = new ArrayList<>();
        RoseWebIdValue[] valueArray = RoseWebIdValue.values();
        if (valueArray != null){
            for (RoseWebIdValue valueObj : valueArray){
                if (includeUnknownName){
                    result.add(valueObj.name());
                }else{
                    if (!(valueObj.name().equalsIgnoreCase(UNKNOWN.name()))){
                        result.add(valueObj.name());
                    }
                }
            }//for
        }//if
        return result;
    }

    /**
     * if no match, UNKNOWN returned.
     * @param value
     * @return 
     */
    public static RoseWebIdValue convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static RoseWebIdValue convertEnumValueToType(String value, boolean allowNullReturned){
        RoseWebIdValue result = null;
        if (value != null){
            RoseWebIdValue[] valueArray = RoseWebIdValue.values();
            for (RoseWebIdValue valueObj : valueArray){
                if (valueObj.value().equalsIgnoreCase(value)){
                    result = valueObj;
                    break;
                }
            }
        }
        if (!allowNullReturned){
            if (result == null){
                result = UNKNOWN;
            }
        }
        return result;
    }

    /**
     * if no match, UNKNOWN returned
     * @param name
     * @return 
     */
    public static RoseWebIdValue convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static RoseWebIdValue convertEnumNameToType(String name, boolean allowNullReturned){
        RoseWebIdValue result = null;
        if (name != null){
            RoseWebIdValue[] valueArray = RoseWebIdValue.values();
            for (RoseWebIdValue valueObj : valueArray){
                if (valueObj.name().equalsIgnoreCase(name)){
                    result = valueObj;
                    break;
                }
            }
        }
        if (!allowNullReturned){
            if (result == null){
                result = UNKNOWN;
            }
        }
        return result;
    }

    /**
     * Try name() first. if it is failed, and then try value() to convert it type
     * @param name
     * @return
     */
    public static RoseWebIdValue convertStringToType(String name){
        RoseWebIdValue result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public static RoseWebIdValue convertIndexToType(Integer enumIndex) {
        return convertIndexToType(enumIndex, false);
    }

    public static RoseWebIdValue convertIndexToType(Integer enumIndex, boolean allowNullReturned) {
        try{
            return RoseWebIdValue.values()[enumIndex];
        }catch (Exception ex){
            if (allowNullReturned){
                return null;
            }else{
                return UNKNOWN;
            }
        }
    }

    private final String value;
    RoseWebIdValue(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
