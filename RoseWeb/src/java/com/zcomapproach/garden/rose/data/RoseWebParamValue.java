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

package com.zcomapproach.garden.rose.data;

import com.zcomapproach.garden.util.GardenData;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author zhijun98
 */
public enum RoseWebParamValue {
    
    CREATE_NEW_ENTITY(GardenData.generateUUIDString()),
    UPDATE_EXISTING_ENTITY(GardenData.generateUUIDString()),
    DELETE_EXISTING_ENTITY(GardenData.generateUUIDString()),
    DISPLAY_ALL_ENTITIES(GardenData.generateUUIDString()),
    DISPLAY_ACTIVE_ENTITIES(GardenData.generateUUIDString()),
    PAYROLL_TAX(GardenData.generateUUIDString()),
    SALES_TAX(GardenData.generateUUIDString()),
    TAX_RETURN(GardenData.generateUUIDString()),
    UNKNOWN("Unknown"); //special usage
    
    private static final HashMap<RoseWebParamValue, String> paramDescriptions = new HashMap<>();
    static{
        paramDescriptions.put(UNKNOWN, "");
    }

    private static HashMap<RoseWebParamValue, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static String getParamDescription(RoseWebParamValue name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<RoseWebParamValue, String> paramValues = new HashMap<>();

    private synchronized static HashMap<RoseWebParamValue, String> getParamValues(){
        if (paramValues.isEmpty()){
            RoseWebParamValue[] valueArray = RoseWebParamValue.values();
            if (valueArray != null){
                for (RoseWebParamValue valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(RoseWebParamValue name){
        return getParamValues().get(name);
    }
    
    public static RoseWebParamValue convertParamValueToType(String webParamValue){
        HashMap<RoseWebParamValue, String> localParamValues = getParamValues();
        Set<RoseWebParamValue> keys = localParamValues.keySet();
        Iterator<RoseWebParamValue> itr = keys.iterator();
        RoseWebParamValue result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<RoseWebParamValue> getRoseWebParamValueList(boolean includeUnknownValue) {
        List<RoseWebParamValue> result = new ArrayList<>();
        RoseWebParamValue[] valueArray = RoseWebParamValue.values();
        if (valueArray != null){
            for (RoseWebParamValue valueObj : valueArray){
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
        RoseWebParamValue[] valueArray = RoseWebParamValue.values();
        if (valueArray != null){
            for (RoseWebParamValue valueObj : valueArray){
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
        RoseWebParamValue[] valueArray = RoseWebParamValue.values();
        if (valueArray != null){
            for (RoseWebParamValue valueObj : valueArray){
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
    public static RoseWebParamValue convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static RoseWebParamValue convertEnumValueToType(String value, boolean allowNullReturned){
        RoseWebParamValue result = null;
        if (value != null){
            RoseWebParamValue[] valueArray = RoseWebParamValue.values();
            for (RoseWebParamValue valueObj : valueArray){
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
    public static RoseWebParamValue convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static RoseWebParamValue convertEnumNameToType(String name, boolean allowNullReturned){
        RoseWebParamValue result = null;
        if (name != null){
            RoseWebParamValue[] valueArray = RoseWebParamValue.values();
            for (RoseWebParamValue valueObj : valueArray){
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
    public static RoseWebParamValue convertStringToType(String name){
        RoseWebParamValue result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public static RoseWebParamValue convertIndexToType(Integer enumIndex) {
        return convertIndexToType(enumIndex, false);
    }

    public static RoseWebParamValue convertIndexToType(Integer enumIndex, boolean allowNullReturned) {
        try{
            return RoseWebParamValue.values()[enumIndex];
        }catch (Exception ex){
            if (allowNullReturned){
                return null;
            }else{
                return UNKNOWN;
            }
        }
    }

    private final String value;
    RoseWebParamValue(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
