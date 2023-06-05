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

package com.zcomapproach.garden.peony.view.data;

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
public enum CustomerSearchResultColumns {

    ACCOUNT_EMAIL("Account Email"),
    MOBILE_PHONE("Mobile Phone"),
    FIRST_NAME("First Name"),
    LAST_NAME("Last Name"),
    LOGIN_NAME("Login Name"),
    SSN("SSN"),
    CREATED("Created"),
    UPDATED("Updated"),
    UNKNOWN("Unknown"); //special usage

    public static List<String> getEnumDateColumnValueList() {
        List<String> result = new ArrayList<>();
        
        result.add(UPDATED.value);
        result.add(CREATED.value);
        
        return result;
    }

    public static List<String> getEnumTextColumnValueList() {
        List<String> result = new ArrayList<>();
        
        result.add(FIRST_NAME.value);
        result.add(LAST_NAME.value);
        result.add(LOGIN_NAME.value);
        result.add(SSN.value);
        result.add(ACCOUNT_EMAIL.value);
        result.add(MOBILE_PHONE.value);
        
        return result;
    }
    
    private static final HashMap<CustomerSearchResultColumns, String> paramDescriptions = new HashMap<>();
    static{
        //todo: add description for params
        paramDescriptions.put(ACCOUNT_EMAIL, "accountEmail");
        paramDescriptions.put(MOBILE_PHONE, "mobilePhone");
        paramDescriptions.put(FIRST_NAME, "firstName");
        paramDescriptions.put(LAST_NAME, "lastName");
        paramDescriptions.put(LOGIN_NAME, "loginName");
        paramDescriptions.put(SSN, "ssn");
        paramDescriptions.put(CREATED, "created");
        paramDescriptions.put(UPDATED, "updated");
        paramDescriptions.put(UNKNOWN, "");
    }

    private static HashMap<CustomerSearchResultColumns, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static List<String> getParamDescriptionList(){
        return new ArrayList<>(getParamDescriptions().values());
    }
    
    public static String getParamDescription(CustomerSearchResultColumns name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<CustomerSearchResultColumns, String> paramValues = new HashMap<>();

    private synchronized static HashMap<CustomerSearchResultColumns, String> getParamValues(){
        if (paramValues.isEmpty()){
            CustomerSearchResultColumns[] valueArray = CustomerSearchResultColumns.values();
            if (valueArray != null){
                for (CustomerSearchResultColumns valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(CustomerSearchResultColumns name){
        return getParamValues().get(name);
    }
    
    public static CustomerSearchResultColumns convertParamValueToType(String webParamValue){
        HashMap<CustomerSearchResultColumns, String> localParamValues = getParamValues();
        Set<CustomerSearchResultColumns> keys = localParamValues.keySet();
        Iterator<CustomerSearchResultColumns> itr = keys.iterator();
        CustomerSearchResultColumns result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<CustomerSearchResultColumns> getCustomerSearchResultColumnsList(boolean includeUnknownValue) {
        List<CustomerSearchResultColumns> result = new ArrayList<>();
        CustomerSearchResultColumns[] valueArray = CustomerSearchResultColumns.values();
        if (valueArray != null){
            for (CustomerSearchResultColumns valueObj : valueArray){
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
        CustomerSearchResultColumns[] valueArray = CustomerSearchResultColumns.values();
        if (valueArray != null){
            for (CustomerSearchResultColumns valueObj : valueArray){
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
        CustomerSearchResultColumns[] valueArray = CustomerSearchResultColumns.values();
        if (valueArray != null){
            for (CustomerSearchResultColumns valueObj : valueArray){
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
    public static CustomerSearchResultColumns convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static CustomerSearchResultColumns convertEnumValueToType(String value, boolean allowNullReturned){
        CustomerSearchResultColumns result = null;
        if (value != null){
            CustomerSearchResultColumns[] valueArray = CustomerSearchResultColumns.values();
            for (CustomerSearchResultColumns valueObj : valueArray){
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
    public static CustomerSearchResultColumns convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static CustomerSearchResultColumns convertEnumNameToType(String name, boolean allowNullReturned){
        CustomerSearchResultColumns result = null;
        if (name != null){
            CustomerSearchResultColumns[] valueArray = CustomerSearchResultColumns.values();
            for (CustomerSearchResultColumns valueObj : valueArray){
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
    public static CustomerSearchResultColumns convertStringToType(String name){
        CustomerSearchResultColumns result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public static CustomerSearchResultColumns convertIndexToType(Integer enumIndex) {
        return convertIndexToType(enumIndex, false);
    }

    public static CustomerSearchResultColumns convertIndexToType(Integer enumIndex, boolean allowNullReturned) {
        try{
            return CustomerSearchResultColumns.values()[enumIndex];
        }catch (Exception ex){
            if (allowNullReturned){
                return null;
            }else{
                return UNKNOWN;
            }
        }
    }

    private final String value;
    CustomerSearchResultColumns(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
