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

package com.zcomapproach.garden.peony.settings;

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
public enum PeonyPropertiesKey {
    
    DEVELOPMENT_MODE("development_mode"),
    LOGIN_NAME("login_name"),
    LOGIN_REMEMBER_ME("login_remember_me"),
    LOCAL_USER_FOLDER("local_user_folder"),
    EMAIL_MESSAGE_LOADING_THRESHOLD("email_message_loading_threshold"),
    LANGUAGE("Language"),
    UNKNOWN("Unknown"); //special usage
    
    private static final HashMap<PeonyPropertiesKey, String> paramDescriptions = new HashMap<>();
    static{
        paramDescriptions.put(UNKNOWN, "");
    }

    private static HashMap<PeonyPropertiesKey, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static String getParamDescription(PeonyPropertiesKey name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<PeonyPropertiesKey, String> paramValues = new HashMap<>();

    private synchronized static HashMap<PeonyPropertiesKey, String> getParamValues(){
        if (paramValues.isEmpty()){
            PeonyPropertiesKey[] valueArray = PeonyPropertiesKey.values();
            if (valueArray != null){
                for (PeonyPropertiesKey valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(PeonyPropertiesKey name){
        return getParamValues().get(name);
    }
    
    public static PeonyPropertiesKey convertParamValueToType(String webParamValue){
        HashMap<PeonyPropertiesKey, String> localParamValues = getParamValues();
        Set<PeonyPropertiesKey> keys = localParamValues.keySet();
        Iterator<PeonyPropertiesKey> itr = keys.iterator();
        PeonyPropertiesKey result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<PeonyPropertiesKey> getPeonyPropertiesKeyList(boolean includeUnknownValue) {
        List<PeonyPropertiesKey> result = new ArrayList<>();
        PeonyPropertiesKey[] valueArray = PeonyPropertiesKey.values();
        if (valueArray != null){
            for (PeonyPropertiesKey valueObj : valueArray){
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
        PeonyPropertiesKey[] valueArray = PeonyPropertiesKey.values();
        if (valueArray != null){
            for (PeonyPropertiesKey valueObj : valueArray){
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
        PeonyPropertiesKey[] valueArray = PeonyPropertiesKey.values();
        if (valueArray != null){
            for (PeonyPropertiesKey valueObj : valueArray){
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
    public static PeonyPropertiesKey convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static PeonyPropertiesKey convertEnumValueToType(String value, boolean allowNullReturned){
        PeonyPropertiesKey result = null;
        if (value != null){
            PeonyPropertiesKey[] valueArray = PeonyPropertiesKey.values();
            for (PeonyPropertiesKey valueObj : valueArray){
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
    public static PeonyPropertiesKey convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static PeonyPropertiesKey convertEnumNameToType(String name, boolean allowNullReturned){
        PeonyPropertiesKey result = null;
        if (name != null){
            PeonyPropertiesKey[] valueArray = PeonyPropertiesKey.values();
            for (PeonyPropertiesKey valueObj : valueArray){
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
    public static PeonyPropertiesKey convertStringToType(String name){
        PeonyPropertiesKey result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public static PeonyPropertiesKey convertIndexToType(Integer enumIndex) {
        return convertIndexToType(enumIndex, false);
    }

    public static PeonyPropertiesKey convertIndexToType(Integer enumIndex, boolean allowNullReturned) {
        try{
            return PeonyPropertiesKey.values()[enumIndex];
        }catch (Exception ex){
            if (allowNullReturned){
                return null;
            }else{
                return UNKNOWN;
            }
        }
    }

    private final String value;
    PeonyPropertiesKey(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
