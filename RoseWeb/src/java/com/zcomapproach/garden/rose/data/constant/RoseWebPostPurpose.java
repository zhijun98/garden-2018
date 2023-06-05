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
public enum RoseWebPostPurpose {
    PUBLIC_WEB_POST("Public Web Post"),
    HOME_MAIN_POST("Homepage Main Paper"),
    HOME_HEADING_POST("Homepage Heading Post"),
    PERSONAL_TAX_CASE_AGREEMENT("Personal Tax Case Agreement"),
    CHASE_QUICK_PAY("Chase Quick-Pay"),
    PAY_BY_UPLOADED_CHECK("Pay by Uploaded Check"),
    UNKNOWN("Unknown"); //special usage

    private final String value;
    RoseWebPostPurpose(String value) {
        this.value = value;
    }
    
    private static final HashMap<RoseWebPostPurpose, String> paramDescriptions = new HashMap<>();
    static{
        paramDescriptions.put(PUBLIC_WEB_POST, 
                "Public web post is published as paper for web users to read.");
        paramDescriptions.put(HOME_MAIN_POST, 
                "Homepage content is published as the main content on the default web page.");
        paramDescriptions.put(HOME_HEADING_POST, 
                "Homepage heading posts are three heading posts published under the main content on the default web page.");
        paramDescriptions.put(PERSONAL_TAX_CASE_AGREEMENT, 
                "Personal tax case agreement is published as the agreement content for customer to accept with signature.");
        paramDescriptions.put(CHASE_QUICK_PAY, 
                "Introduction to Chase Quick-Pay method for customers.");
        paramDescriptions.put(PAY_BY_UPLOADED_CHECK, 
                "Description on how to pay by uploaded Check for customers.");
    }

    private static HashMap<RoseWebPostPurpose, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static String getParamDescription(RoseWebPostPurpose name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<RoseWebPostPurpose, String> paramValues = new HashMap<>();

    private synchronized static HashMap<RoseWebPostPurpose, String> getParamValues(){
        if (paramValues.isEmpty()){
            RoseWebPostPurpose[] valueArray = RoseWebPostPurpose.values();
            if (valueArray != null){
                for (RoseWebPostPurpose valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(RoseWebPostPurpose name){
        return getParamValues().get(name);
    }
    
    public static RoseWebPostPurpose convertParamValueToType(String webParamValue){
        HashMap<RoseWebPostPurpose, String> localParamValues = getParamValues();
        Set<RoseWebPostPurpose> keys = localParamValues.keySet();
        Iterator<RoseWebPostPurpose> itr = keys.iterator();
        RoseWebPostPurpose result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<RoseWebPostPurpose> getGardenWebPostTagList(boolean includeUnknownValue) {
        List<RoseWebPostPurpose> result = new ArrayList<>();
        RoseWebPostPurpose[] valueArray = RoseWebPostPurpose.values();
        if (valueArray != null){
            for (RoseWebPostPurpose valueObj : valueArray){
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
        RoseWebPostPurpose[] valueArray = RoseWebPostPurpose.values();
        if (valueArray != null){
            for (RoseWebPostPurpose valueObj : valueArray){
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
        RoseWebPostPurpose[] valueArray = RoseWebPostPurpose.values();
        if (valueArray != null){
            for (RoseWebPostPurpose valueObj : valueArray){
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
     * if not matched, UNKNOWN will not be returned. Instead, NULL returned.
     * @param value
     * @return 
     */
    public static RoseWebPostPurpose convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static RoseWebPostPurpose convertEnumValueToType(String value, boolean allowNullReturned){
        RoseWebPostPurpose result = null;
        if (value != null){
            RoseWebPostPurpose[] valueArray = RoseWebPostPurpose.values();
            for (RoseWebPostPurpose valueObj : valueArray){
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
     * if not matched, UNKNOWN will not be returned. Instead, NULL returned.
     * @param name
     * @return 
     */
    public static RoseWebPostPurpose convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static RoseWebPostPurpose convertEnumNameToType(String name, boolean allowNullReturned){
        RoseWebPostPurpose result = null;
        if (name != null){
            RoseWebPostPurpose[] valueArray = RoseWebPostPurpose.values();
            for (RoseWebPostPurpose valueObj : valueArray){
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
    public static RoseWebPostPurpose convertStringToType(String name){
        RoseWebPostPurpose result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public String value() {
        return value;
    }
}
