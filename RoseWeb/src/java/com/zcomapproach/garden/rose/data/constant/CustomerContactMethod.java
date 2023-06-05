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
public enum CustomerContactMethod {
    EMAIL_AND_PHONE_MESSAGING("Email and Phone Messaging (SMS)"),
    EMAIL_ONLY("Only Email"),
    PHONE_MESSAGING_ONLY("Only Phone Messaging (SMS)"),
    UNKNOWN("Unknown"); //special usage//special usage//special usage//special usage
    
    private static final HashMap<CustomerContactMethod, String> paramDescriptions = new HashMap<>();
    static{
        //paramDescriptions.put(valueObj, "");
    }

    private static HashMap<CustomerContactMethod, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static String getParamDescription(CustomerContactMethod name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<CustomerContactMethod, String> paramValues = new HashMap<>();

    private synchronized static HashMap<CustomerContactMethod, String> getParamValues(){
        if (paramValues.isEmpty()){
            CustomerContactMethod[] valueArray = CustomerContactMethod.values();
            if (valueArray != null){
                for (CustomerContactMethod valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(CustomerContactMethod name){
        return getParamValues().get(name);
    }
    
    public static CustomerContactMethod convertParamValueToType(String webParamValue){
        HashMap<CustomerContactMethod, String> localParamValues = getParamValues();
        Set<CustomerContactMethod> keys = localParamValues.keySet();
        Iterator<CustomerContactMethod> itr = keys.iterator();
        CustomerContactMethod result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<CustomerContactMethod> getCustomerContactTypeList(boolean includeUnknownValue) {
        List<CustomerContactMethod> result = new ArrayList<>();
        CustomerContactMethod[] valueArray = CustomerContactMethod.values();
        if (valueArray != null){
            for (CustomerContactMethod valueObj : valueArray){
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
        CustomerContactMethod[] valueArray = CustomerContactMethod.values();
        if (valueArray != null){
            for (CustomerContactMethod valueObj : valueArray){
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
        CustomerContactMethod[] valueArray = CustomerContactMethod.values();
        if (valueArray != null){
            for (CustomerContactMethod valueObj : valueArray){
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
    public static CustomerContactMethod convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static CustomerContactMethod convertEnumValueToType(String value, boolean allowNullReturned){
        CustomerContactMethod result = null;
        if (value != null){
            CustomerContactMethod[] valueArray = CustomerContactMethod.values();
            for (CustomerContactMethod valueObj : valueArray){
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
    public static CustomerContactMethod convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static CustomerContactMethod convertEnumNameToType(String name, boolean allowNullReturned){
        CustomerContactMethod result = null;
        if (name != null){
            CustomerContactMethod[] valueArray = CustomerContactMethod.values();
            for (CustomerContactMethod valueObj : valueArray){
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
    public static CustomerContactMethod convertStringToType(String name){
        CustomerContactMethod result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    private final String value;
    CustomerContactMethod(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
