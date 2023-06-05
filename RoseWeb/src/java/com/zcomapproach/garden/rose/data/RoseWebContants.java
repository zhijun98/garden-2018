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

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * This enum data contains constant values which have their own meaning in their usage in Rose. The UUID code should not 
 * be changed anytime since it may cause inconsistent in Rose-logic and they are used in the persistent layer, e.g., cookies 
 * or database. if changed, the Rose-logic layer cannot understand their meaning anymore.
 * 
 * @author zhijun98
 */
public enum RoseWebContants {
        
    ROSE_LOCALE_LANG("9c19c6de0a1fc0406c0a4d3025156d54db21"),
    UNKNOWN("Unknown"); //special usage//special usage//special usage//special usage
    
    private static final HashMap<RoseWebContants, String> paramDescriptions = new HashMap<>();
    static{
        paramDescriptions.put(ROSE_LOCALE_LANG, "This value is used as cookie key to help Rose know user's locale");
    }

    private static HashMap<RoseWebContants, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static String getParamDescription(RoseWebContants name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<RoseWebContants, String> paramValues = new HashMap<>();

    private synchronized static HashMap<RoseWebContants, String> getParamValues(){
        if (paramValues.isEmpty()){
            RoseWebContants[] valueArray = RoseWebContants.values();
            if (valueArray != null){
                for (RoseWebContants valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(RoseWebContants name){
        return getParamValues().get(name);
    }
    
    public static RoseWebContants convertParamValueToType(String webParamValue){
        HashMap<RoseWebContants, String> localParamValues = getParamValues();
        Set<RoseWebContants> keys = localParamValues.keySet();
        Iterator<RoseWebContants> itr = keys.iterator();
        RoseWebContants result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<RoseWebContants> getRoseContantList(boolean includeUnknownValue) {
        List<RoseWebContants> result = new ArrayList<>();
        RoseWebContants[] valueArray = RoseWebContants.values();
        if (valueArray != null){
            for (RoseWebContants valueObj : valueArray){
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
        RoseWebContants[] valueArray = RoseWebContants.values();
        if (valueArray != null){
            for (RoseWebContants valueObj : valueArray){
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
        RoseWebContants[] valueArray = RoseWebContants.values();
        if (valueArray != null){
            for (RoseWebContants valueObj : valueArray){
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
     * if no match, UNKNOWN will not be returned. Instead, NULL returned.
     * @param value
     * @return 
     */
    public static RoseWebContants convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static RoseWebContants convertEnumValueToType(String value, boolean allowNullReturned){
        RoseWebContants result = null;
        if (value != null){
            RoseWebContants[] valueArray = RoseWebContants.values();
            for (RoseWebContants valueObj : valueArray){
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
    public static RoseWebContants convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static RoseWebContants convertEnumNameToType(String name, boolean allowNullReturned){
        RoseWebContants result = null;
        if (name != null){
            RoseWebContants[] valueArray = RoseWebContants.values();
            for (RoseWebContants valueObj : valueArray){
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
    public static RoseWebContants convertStringToType(String name){
        RoseWebContants result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    private final String value;
    RoseWebContants(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
