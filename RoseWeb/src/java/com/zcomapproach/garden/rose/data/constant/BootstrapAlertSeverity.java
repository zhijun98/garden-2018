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

package com.zcomapproach.garden.rose.data.constant;

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
public enum BootstrapAlertSeverity {
    
    SUCCESS("success"),
    DANGER("danger"),
    WARNING("warning"),
    INFO("info"),
    UNKNOWN("Unknown"); //special usage
    
    private static final HashMap<BootstrapAlertSeverity, String> paramDescriptions = new HashMap<>();
    static{
        paramDescriptions.put(UNKNOWN, "");
    }

    private static HashMap<BootstrapAlertSeverity, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static String getParamDescription(BootstrapAlertSeverity name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<BootstrapAlertSeverity, String> paramValues = new HashMap<>();

    private synchronized static HashMap<BootstrapAlertSeverity, String> getParamValues(){
        if (paramValues.isEmpty()){
            BootstrapAlertSeverity[] valueArray = BootstrapAlertSeverity.values();
            if (valueArray != null){
                for (BootstrapAlertSeverity valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(BootstrapAlertSeverity name){
        return getParamValues().get(name);
    }
    
    public static BootstrapAlertSeverity convertParamValueToType(String webParamValue){
        HashMap<BootstrapAlertSeverity, String> localParamValues = getParamValues();
        Set<BootstrapAlertSeverity> keys = localParamValues.keySet();
        Iterator<BootstrapAlertSeverity> itr = keys.iterator();
        BootstrapAlertSeverity result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<BootstrapAlertSeverity> getBootstrapAlertSeverityList(boolean includeUnknownValue) {
        List<BootstrapAlertSeverity> result = new ArrayList<>();
        BootstrapAlertSeverity[] valueArray = BootstrapAlertSeverity.values();
        if (valueArray != null){
            for (BootstrapAlertSeverity valueObj : valueArray){
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
        BootstrapAlertSeverity[] valueArray = BootstrapAlertSeverity.values();
        if (valueArray != null){
            for (BootstrapAlertSeverity valueObj : valueArray){
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
        BootstrapAlertSeverity[] valueArray = BootstrapAlertSeverity.values();
        if (valueArray != null){
            for (BootstrapAlertSeverity valueObj : valueArray){
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
     * if not matched, UNKNOWN will be returned but not NULL.
     * @param value
     * @return 
     */
    public static BootstrapAlertSeverity convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static BootstrapAlertSeverity convertEnumValueToType(String value, boolean allowNullReturned){
        BootstrapAlertSeverity result = null;
        if (value != null){
            BootstrapAlertSeverity[] valueArray = BootstrapAlertSeverity.values();
            for (BootstrapAlertSeverity valueObj : valueArray){
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
     * if not matched, UNKNOWN will be returned but not NULL
     * @param name
     * @return 
     */
    public static BootstrapAlertSeverity convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static BootstrapAlertSeverity convertEnumNameToType(String name, boolean allowNullReturned){
        BootstrapAlertSeverity result = null;
        if (name != null){
            BootstrapAlertSeverity[] valueArray = BootstrapAlertSeverity.values();
            for (BootstrapAlertSeverity valueObj : valueArray){
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
    public static BootstrapAlertSeverity convertStringToType(String name){
        BootstrapAlertSeverity result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public static BootstrapAlertSeverity convertIndexToType(Integer enumIndex) {
        return convertIndexToType(enumIndex, false);
    }

    public static BootstrapAlertSeverity convertIndexToType(Integer enumIndex, boolean allowNullReturned) {
        try{
            return BootstrapAlertSeverity.values()[enumIndex];
        }catch (Exception ex){
            if (allowNullReturned){
                return null;
            }else{
                return UNKNOWN;
            }
        }
    }

    private final String value;
    BootstrapAlertSeverity(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
