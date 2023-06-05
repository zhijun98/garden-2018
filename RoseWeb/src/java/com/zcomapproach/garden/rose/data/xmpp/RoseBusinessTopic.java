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

package com.zcomapproach.garden.rose.data.xmpp;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * Each item is a buddy account in the XMPP server. Item.name is the login name, and item.value is the password
 * @author zhijun98
 */
public enum RoseBusinessTopic {
    
    ROSE_NEW_REGISTRATION("rose_new_registration"),
    UNKNOWN("Unknown"); //special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage
    
    private static final HashMap<RoseBusinessTopic, String> paramDescriptions = new HashMap<>();
    static{
        paramDescriptions.put(UNKNOWN, "");
    }

    private static HashMap<RoseBusinessTopic, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static String getParamDescription(RoseBusinessTopic name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<RoseBusinessTopic, String> paramValues = new HashMap<>();

    private synchronized static HashMap<RoseBusinessTopic, String> getParamValues(){
        if (paramValues.isEmpty()){
            RoseBusinessTopic[] valueArray = RoseBusinessTopic.values();
            if (valueArray != null){
                for (RoseBusinessTopic valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(RoseBusinessTopic name){
        return getParamValues().get(name);
    }
    
    public static RoseBusinessTopic convertParamValueToType(String webParamValue){
        HashMap<RoseBusinessTopic, String> localParamValues = getParamValues();
        Set<RoseBusinessTopic> keys = localParamValues.keySet();
        Iterator<RoseBusinessTopic> itr = keys.iterator();
        RoseBusinessTopic result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<RoseBusinessTopic> getRoseXmppEventList(boolean includeUnknownValue) {
        List<RoseBusinessTopic> result = new ArrayList<>();
        RoseBusinessTopic[] valueArray = RoseBusinessTopic.values();
        if (valueArray != null){
            for (RoseBusinessTopic valueObj : valueArray){
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
        RoseBusinessTopic[] valueArray = RoseBusinessTopic.values();
        if (valueArray != null){
            for (RoseBusinessTopic valueObj : valueArray){
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
        RoseBusinessTopic[] valueArray = RoseBusinessTopic.values();
        if (valueArray != null){
            for (RoseBusinessTopic valueObj : valueArray){
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
    public static RoseBusinessTopic convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static RoseBusinessTopic convertEnumValueToType(String value, boolean allowNullReturned){
        RoseBusinessTopic result = null;
        if (value != null){
            RoseBusinessTopic[] valueArray = RoseBusinessTopic.values();
            for (RoseBusinessTopic valueObj : valueArray){
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
    public static RoseBusinessTopic convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static RoseBusinessTopic convertEnumNameToType(String name, boolean allowNullReturned){
        RoseBusinessTopic result = null;
        if (name != null){
            RoseBusinessTopic[] valueArray = RoseBusinessTopic.values();
            for (RoseBusinessTopic valueObj : valueArray){
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
    public static RoseBusinessTopic convertStringToType(String name){
        RoseBusinessTopic result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public static RoseBusinessTopic convertIndexToType(Integer enumIndex) {
        return convertIndexToType(enumIndex, false);
    }

    public static RoseBusinessTopic convertIndexToType(Integer enumIndex, boolean allowNullReturned) {
        try{
            return RoseBusinessTopic.values()[enumIndex];
        }catch (Exception ex){
            if (allowNullReturned){
                return null;
            }else{
                return UNKNOWN;
            }
        }
    }

    private final String value;
    RoseBusinessTopic(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
