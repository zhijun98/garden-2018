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
 * Refer to TaxcorpCaseSearchResult
 * @author zhijun98
 */
public enum TaxcorpCaseSearchResultColumns {

    COMPANY_NAME("Company Name"),
    TAX_STATE("Tax State"),
    BUSINESS_TYPE("Business Type"),
    EIN_NUMBER("EIN Number"),
    DOS_DATE("DOS Date"),
    STATUS("Status"),
    CONTACT_INFORMATION("Contact Information"),
    UNKNOWN("Unknown"); 

    public static List<String> getEnumDateColumnValueList() {
        List<String> result = new ArrayList<>();
        
        result.add(DOS_DATE.value);
        
        return result;
    }

    public static List<String> getEnumTextColumnValueList() {
        List<String> result = new ArrayList<>();
        
        result.add(COMPANY_NAME.value);
        result.add(TAX_STATE.value);
        result.add(BUSINESS_TYPE.value);
        result.add(EIN_NUMBER.value);
        result.add(CONTACT_INFORMATION.value);
        result.add(STATUS.value);
        
        return result;
    }
    
    private static final HashMap<TaxcorpCaseSearchResultColumns, String> paramDescriptions = new HashMap<>();
    static{
        paramDescriptions.put(COMPANY_NAME, "companyName");
        paramDescriptions.put(TAX_STATE, "taxState");
        paramDescriptions.put(BUSINESS_TYPE, "businessType");
        paramDescriptions.put(EIN_NUMBER, "einNumber");
        paramDescriptions.put(DOS_DATE, "dosDateText");
        paramDescriptions.put(STATUS, "taxcorpStatus");
        paramDescriptions.put(CONTACT_INFORMATION, "contactInformation");
        paramDescriptions.put(UNKNOWN, "");
    }

    private static HashMap<TaxcorpCaseSearchResultColumns, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static List<String> getParamDescriptionList(boolean includeUnknownValue){
        List<String> result = new ArrayList<>(getParamDescriptions().values());
        if (!includeUnknownValue){
            result.remove(UNKNOWN.value());
        }
        return result;
    }
    
    public static String getParamDescription(TaxcorpCaseSearchResultColumns name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<TaxcorpCaseSearchResultColumns, String> paramValues = new HashMap<>();

    private synchronized static HashMap<TaxcorpCaseSearchResultColumns, String> getParamValues(){
        if (paramValues.isEmpty()){
            TaxcorpCaseSearchResultColumns[] valueArray = TaxcorpCaseSearchResultColumns.values();
            if (valueArray != null){
                for (TaxcorpCaseSearchResultColumns valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(TaxcorpCaseSearchResultColumns name){
        return getParamValues().get(name);
    }
    
    public static TaxcorpCaseSearchResultColumns convertParamValueToType(String webParamValue){
        HashMap<TaxcorpCaseSearchResultColumns, String> localParamValues = getParamValues();
        Set<TaxcorpCaseSearchResultColumns> keys = localParamValues.keySet();
        Iterator<TaxcorpCaseSearchResultColumns> itr = keys.iterator();
        TaxcorpCaseSearchResultColumns result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<TaxcorpCaseSearchResultColumns> getColumnsForTaxFilingCaseSearchResultList(boolean includeUnknownValue) {
        List<TaxcorpCaseSearchResultColumns> result = new ArrayList<>();
        TaxcorpCaseSearchResultColumns[] valueArray = TaxcorpCaseSearchResultColumns.values();
        if (valueArray != null){
            for (TaxcorpCaseSearchResultColumns valueObj : valueArray){
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
        TaxcorpCaseSearchResultColumns[] valueArray = TaxcorpCaseSearchResultColumns.values();
        if (valueArray != null){
            for (TaxcorpCaseSearchResultColumns valueObj : valueArray){
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
        TaxcorpCaseSearchResultColumns[] valueArray = TaxcorpCaseSearchResultColumns.values();
        if (valueArray != null){
            for (TaxcorpCaseSearchResultColumns valueObj : valueArray){
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
    public static TaxcorpCaseSearchResultColumns convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static TaxcorpCaseSearchResultColumns convertEnumValueToType(String value, boolean allowNullReturned){
        TaxcorpCaseSearchResultColumns result = null;
        if (value != null){
            TaxcorpCaseSearchResultColumns[] valueArray = TaxcorpCaseSearchResultColumns.values();
            for (TaxcorpCaseSearchResultColumns valueObj : valueArray){
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
    public static TaxcorpCaseSearchResultColumns convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static TaxcorpCaseSearchResultColumns convertEnumNameToType(String name, boolean allowNullReturned){
        TaxcorpCaseSearchResultColumns result = null;
        if (name != null){
            TaxcorpCaseSearchResultColumns[] valueArray = TaxcorpCaseSearchResultColumns.values();
            for (TaxcorpCaseSearchResultColumns valueObj : valueArray){
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
    public static TaxcorpCaseSearchResultColumns convertStringToType(String name){
        TaxcorpCaseSearchResultColumns result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public static TaxcorpCaseSearchResultColumns convertIndexToType(Integer enumIndex) {
        return convertIndexToType(enumIndex, false);
    }

    public static TaxcorpCaseSearchResultColumns convertIndexToType(Integer enumIndex, boolean allowNullReturned) {
        try{
            return TaxcorpCaseSearchResultColumns.values()[enumIndex];
        }catch (Exception ex){
            if (allowNullReturned){
                return null;
            }else{
                return UNKNOWN;
            }
        }
    }

    private final String value;
    TaxcorpCaseSearchResultColumns(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
