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
 * Refer to TaxFilingCaseSearchResult
 * @author zhijun98
 */
public enum TaxcorpTaxFilingCaseSearchResultColumns {

    COMPANY_NAME("Company Name"),
    TAX_STATE("Tax State"),
    TAX_FILING_TEXT("Tax Filing"),
    TAX_FILING_MEMO("Tax Filing's Memo"),
    DEADLINE("Deadline"),
    EXTENSION("Extension"),
    STATUS("Status"),
    PREPARED("Prepared"),
    RECEIEVED("Received"),
    COMPLETED("Completed"),
    EXT_EFILED("Ext. eFiled"),
    EFILED("Tax eFiled"),
    CONTACT_INFORMATION("Contact Information"),
    BALANCE("Balance"),
    UNKNOWN("Unknown"); //special usage//special usage//special usage//special usage

    public static List<String> getEnumDateColumnValueList() {
        List<String> result = new ArrayList<>();
        
        result.add(DEADLINE.value);
        result.add(EXTENSION.value);
        result.add(PREPARED.value);
        result.add(RECEIEVED.value);
        result.add(COMPLETED.value);
        result.add(EFILED.value);
        result.add(EXT_EFILED.value);
        
        return result;
    }

    public static List<String> getEnumTextColumnValueList() {
        List<String> result = new ArrayList<>();
        
        result.add(COMPANY_NAME.value);
        result.add(TAX_STATE.value);
        result.add(TAX_FILING_TEXT.value);
        result.add(TAX_FILING_MEMO.value);
        result.add(CONTACT_INFORMATION.value);
        result.add(BALANCE.value);
        
        return result;
    }
    
    private static final HashMap<TaxcorpTaxFilingCaseSearchResultColumns, String> paramDescriptions = new HashMap<>();
    static{
        paramDescriptions.put(COMPANY_NAME, "companyName");
        paramDescriptions.put(TAX_STATE, "taxState");
        paramDescriptions.put(TAX_FILING_TEXT, "taxFilingText");
        paramDescriptions.put(TAX_FILING_MEMO, "taxFilingMemo");
        paramDescriptions.put(DEADLINE, "deadline");
        paramDescriptions.put(EXTENSION, "extension");
        paramDescriptions.put(STATUS, "taxcorpStatus");
        paramDescriptions.put(RECEIEVED, "receivedDate");
        paramDescriptions.put(PREPARED, "preparedDate");
        paramDescriptions.put(COMPLETED, "completedDate");
        paramDescriptions.put(EFILED, "eFiledDate");
        paramDescriptions.put(EXT_EFILED, "extensionEFiledDate");
        paramDescriptions.put(CONTACT_INFORMATION, "contactInformation");
        paramDescriptions.put(BALANCE, "balanceStatus");
        paramDescriptions.put(UNKNOWN, "");
    }

    private static HashMap<TaxcorpTaxFilingCaseSearchResultColumns, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static List<String> getParamDescriptionList(boolean includeUnknownValue){
        List<String> result = new ArrayList<>(getParamDescriptions().values());
        if (!includeUnknownValue){
            result.remove(UNKNOWN.value());
        }
        return result;
    }
    
    public static String getParamDescription(TaxcorpTaxFilingCaseSearchResultColumns name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<TaxcorpTaxFilingCaseSearchResultColumns, String> paramValues = new HashMap<>();

    private synchronized static HashMap<TaxcorpTaxFilingCaseSearchResultColumns, String> getParamValues(){
        if (paramValues.isEmpty()){
            TaxcorpTaxFilingCaseSearchResultColumns[] valueArray = TaxcorpTaxFilingCaseSearchResultColumns.values();
            if (valueArray != null){
                for (TaxcorpTaxFilingCaseSearchResultColumns valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(TaxcorpTaxFilingCaseSearchResultColumns name){
        return getParamValues().get(name);
    }
    
    public static TaxcorpTaxFilingCaseSearchResultColumns convertParamValueToType(String webParamValue){
        HashMap<TaxcorpTaxFilingCaseSearchResultColumns, String> localParamValues = getParamValues();
        Set<TaxcorpTaxFilingCaseSearchResultColumns> keys = localParamValues.keySet();
        Iterator<TaxcorpTaxFilingCaseSearchResultColumns> itr = keys.iterator();
        TaxcorpTaxFilingCaseSearchResultColumns result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<TaxcorpTaxFilingCaseSearchResultColumns> getColumnsForTaxFilingCaseSearchResultList(boolean includeUnknownValue) {
        List<TaxcorpTaxFilingCaseSearchResultColumns> result = new ArrayList<>();
        TaxcorpTaxFilingCaseSearchResultColumns[] valueArray = TaxcorpTaxFilingCaseSearchResultColumns.values();
        if (valueArray != null){
            for (TaxcorpTaxFilingCaseSearchResultColumns valueObj : valueArray){
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
        TaxcorpTaxFilingCaseSearchResultColumns[] valueArray = TaxcorpTaxFilingCaseSearchResultColumns.values();
        if (valueArray != null){
            for (TaxcorpTaxFilingCaseSearchResultColumns valueObj : valueArray){
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
        TaxcorpTaxFilingCaseSearchResultColumns[] valueArray = TaxcorpTaxFilingCaseSearchResultColumns.values();
        if (valueArray != null){
            for (TaxcorpTaxFilingCaseSearchResultColumns valueObj : valueArray){
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
    public static TaxcorpTaxFilingCaseSearchResultColumns convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static TaxcorpTaxFilingCaseSearchResultColumns convertEnumValueToType(String value, boolean allowNullReturned){
        TaxcorpTaxFilingCaseSearchResultColumns result = null;
        if (value != null){
            TaxcorpTaxFilingCaseSearchResultColumns[] valueArray = TaxcorpTaxFilingCaseSearchResultColumns.values();
            for (TaxcorpTaxFilingCaseSearchResultColumns valueObj : valueArray){
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
    public static TaxcorpTaxFilingCaseSearchResultColumns convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static TaxcorpTaxFilingCaseSearchResultColumns convertEnumNameToType(String name, boolean allowNullReturned){
        TaxcorpTaxFilingCaseSearchResultColumns result = null;
        if (name != null){
            TaxcorpTaxFilingCaseSearchResultColumns[] valueArray = TaxcorpTaxFilingCaseSearchResultColumns.values();
            for (TaxcorpTaxFilingCaseSearchResultColumns valueObj : valueArray){
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
    public static TaxcorpTaxFilingCaseSearchResultColumns convertStringToType(String name){
        TaxcorpTaxFilingCaseSearchResultColumns result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public static TaxcorpTaxFilingCaseSearchResultColumns convertIndexToType(Integer enumIndex) {
        return convertIndexToType(enumIndex, false);
    }

    public static TaxcorpTaxFilingCaseSearchResultColumns convertIndexToType(Integer enumIndex, boolean allowNullReturned) {
        try{
            return TaxcorpTaxFilingCaseSearchResultColumns.values()[enumIndex];
        }catch (Exception ex){
            if (allowNullReturned){
                return null;
            }else{
                return UNKNOWN;
            }
        }
    }

    private final String value;
    TaxcorpTaxFilingCaseSearchResultColumns(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
