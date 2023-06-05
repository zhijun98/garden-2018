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

package com.zcomapproach.garden.lotus.data;

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
public enum LotusPropValue {

    RUN("run"),
    STOP("stop"),
    UNKNOWN("Unknown"); //special usage
    
    private static final HashMap<LotusPropValue, String> paramDescriptions = new HashMap<>();
    static{
        //todo: add description for params
        paramDescriptions.put(UNKNOWN, "");
    }

    private static HashMap<LotusPropValue, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static List<String> getParamDescriptionList(){
        return new ArrayList<>(getParamDescriptions().values());
    }
    
    public static String getParamDescription(LotusPropValue name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<LotusPropValue, String> paramValues = new HashMap<>();

    private synchronized static HashMap<LotusPropValue, String> getParamValues(){
        if (paramValues.isEmpty()){
            LotusPropValue[] valueArray = LotusPropValue.values();
            if (valueArray != null){
                for (LotusPropValue valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(LotusPropValue name){
        return getParamValues().get(name);
    }
    
    public static LotusPropValue convertParamValueToType(String webParamValue){
        HashMap<LotusPropValue, String> localParamValues = getParamValues();
        Set<LotusPropValue> keys = localParamValues.keySet();
        Iterator<LotusPropValue> itr = keys.iterator();
        LotusPropValue result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<LotusPropValue> getLotusPropValueList(boolean includeUnknownValue) {
        List<LotusPropValue> result = new ArrayList<>();
        LotusPropValue[] valueArray = LotusPropValue.values();
        if (valueArray != null){
            for (LotusPropValue valueObj : valueArray){
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
        LotusPropValue[] valueArray = LotusPropValue.values();
        if (valueArray != null){
            for (LotusPropValue valueObj : valueArray){
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
        LotusPropValue[] valueArray = LotusPropValue.values();
        if (valueArray != null){
            for (LotusPropValue valueObj : valueArray){
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
    public static LotusPropValue convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static LotusPropValue convertEnumValueToType(String value, boolean allowNullReturned){
        LotusPropValue result = null;
        if (value != null){
            LotusPropValue[] valueArray = LotusPropValue.values();
            for (LotusPropValue valueObj : valueArray){
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
    public static LotusPropValue convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static LotusPropValue convertEnumNameToType(String name, boolean allowNullReturned){
        LotusPropValue result = null;
        if (name != null){
            LotusPropValue[] valueArray = LotusPropValue.values();
            for (LotusPropValue valueObj : valueArray){
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
     * Try all the ways to get the type itself. But name() will be tried first. if it is failed, then try value() to convert it type
     * @param name
     * @return
     */
    public static LotusPropValue convertStringToType(String name){
        LotusPropValue result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public static LotusPropValue convertIndexToType(Integer enumIndex) {
        return convertIndexToType(enumIndex, false);
    }

    public static LotusPropValue convertIndexToType(Integer enumIndex, boolean allowNullReturned) {
        try{
            return LotusPropValue.values()[enumIndex];
        }catch (Exception ex){
            if (allowNullReturned){
                return null;
            }else{
                return UNKNOWN;
            }
        }
    }

    private final String value;
    LotusPropValue(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
