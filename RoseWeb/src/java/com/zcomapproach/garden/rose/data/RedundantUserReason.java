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
 *
 * @author zhijun98
 */
public enum RedundantUserReason {
    
    REUNDANT_EMAIL("Redundant Email"),
    REUNDANT_PHONE("Redundant Phone"),
    REUNDANT_FAX("Redundant Fax"),
    REUNDANT_WECHAT("Redundant WeChat"),
    REUNDANT_SSN("Redundant SSN Number"),
    UNKNOWN("Unknown"); //special usage
    
    private static final HashMap<RedundantUserReason, String> paramDescriptions = new HashMap<>();
    static{
        paramDescriptions.put(UNKNOWN, "");
    }

    private static HashMap<RedundantUserReason, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static String getParamDescription(RedundantUserReason name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<RedundantUserReason, String> paramValues = new HashMap<>();

    private synchronized static HashMap<RedundantUserReason, String> getParamValues(){
        if (paramValues.isEmpty()){
            RedundantUserReason[] valueArray = RedundantUserReason.values();
            if (valueArray != null){
                for (RedundantUserReason valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(RedundantUserReason name){
        return getParamValues().get(name);
    }
    
    public static RedundantUserReason convertParamValueToType(String webParamValue){
        HashMap<RedundantUserReason, String> localParamValues = getParamValues();
        Set<RedundantUserReason> keys = localParamValues.keySet();
        Iterator<RedundantUserReason> itr = keys.iterator();
        RedundantUserReason result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<RedundantUserReason> getRedundantUserReasonList(boolean includeUnknownValue) {
        List<RedundantUserReason> result = new ArrayList<>();
        RedundantUserReason[] valueArray = RedundantUserReason.values();
        if (valueArray != null){
            for (RedundantUserReason valueObj : valueArray){
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
        RedundantUserReason[] valueArray = RedundantUserReason.values();
        if (valueArray != null){
            for (RedundantUserReason valueObj : valueArray){
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
        RedundantUserReason[] valueArray = RedundantUserReason.values();
        if (valueArray != null){
            for (RedundantUserReason valueObj : valueArray){
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
    public static RedundantUserReason convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static RedundantUserReason convertEnumValueToType(String value, boolean allowNullReturned){
        RedundantUserReason result = null;
        if (value != null){
            RedundantUserReason[] valueArray = RedundantUserReason.values();
            for (RedundantUserReason valueObj : valueArray){
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
    public static RedundantUserReason convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static RedundantUserReason convertEnumNameToType(String name, boolean allowNullReturned){
        RedundantUserReason result = null;
        if (name != null){
            RedundantUserReason[] valueArray = RedundantUserReason.values();
            for (RedundantUserReason valueObj : valueArray){
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
    public static RedundantUserReason convertStringToType(String name){
        RedundantUserReason result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public static RedundantUserReason convertIndexToType(Integer enumIndex) {
        return convertIndexToType(enumIndex, false);
    }

    public static RedundantUserReason convertIndexToType(Integer enumIndex, boolean allowNullReturned) {
        try{
            return RedundantUserReason.values()[enumIndex];
        }catch (Exception ex){
            if (allowNullReturned){
                return null;
            }else{
                return UNKNOWN;
            }
        }
    }

    private final String value;
    RedundantUserReason(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
