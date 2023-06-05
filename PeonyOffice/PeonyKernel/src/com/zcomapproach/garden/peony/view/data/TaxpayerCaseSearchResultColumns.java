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
 * Refer to TaxpayerCaseSearchResult
 * @author zhijun98
 */
public enum TaxpayerCaseSearchResultColumns {

    TAXPAYER_CASE_UUID("Case ID"),
    DEADLINE("Deadline"),
    FEDERAL_FILING_STATUS("Federal Filing Status"),
    STATUS("Work Status"),
    BALANCE_AND_TOTAL("Balance/Total"),
    CONFIRMED_AND_DEPOSIT("Confirmed/Deposited"),
    SSN("SSN"),
    TAXPAYER("Taxpayer"),
    SPOUSE("Spouse"),
    CONTACT("Contact"),
    TAXPAYER_INFORMATION("Taxpayer(s)"),
    OTHER_DEPENDENTS("Others"),
    STATUS_UPDATED("Timestamp"),
    STATUS_OPERATOR("Operator"),
    UNKNOWN("Unknown"); //special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage
    
    /**
     * Help filter
     * @return 
     */
    public static List<String> getEnumDateColumnValueList() {
        List<String> result = new ArrayList<>();
        
        result.add(STATUS_UPDATED.value);
        
        return result;
    }

    /**
     * Help filter
     * @return 
     */
    public static List<String> getEnumTextColumnValueList() {
        List<String> result = new ArrayList<>();
        
        result.add(TAXPAYER_CASE_UUID.value);
        result.add(FEDERAL_FILING_STATUS.value);
        result.add(STATUS.value);
        result.add(SSN.value);
        result.add(STATUS_OPERATOR.value);
        result.add(TAXPAYER.value);
        result.add(SPOUSE.value);
        result.add(CONTACT.value);
        result.add(TAXPAYER_INFORMATION.value);
        result.add(OTHER_DEPENDENTS.value);
        
        return result;
    }
    
    private static final HashMap<TaxpayerCaseSearchResultColumns, String> paramDescriptions = new HashMap<>();
    static{
        paramDescriptions.put(TAXPAYER_CASE_UUID, "taxpayerCaseUuid");
        paramDescriptions.put(DEADLINE, "deadline");
        paramDescriptions.put(STATUS, "latestTaxpayerStatus");
        paramDescriptions.put(BALANCE_AND_TOTAL, "balanceTotalText");
        paramDescriptions.put(CONFIRMED_AND_DEPOSIT, "confirmedDepositedText");
        paramDescriptions.put(FEDERAL_FILING_STATUS, "federalFilingStatus");
        paramDescriptions.put(SSN, "primaryTaxpayerSsn");
        paramDescriptions.put(TAXPAYER, "taxpayerName");
        paramDescriptions.put(SPOUSE, "spouseName");
        paramDescriptions.put(CONTACT, "contactInfo");
        paramDescriptions.put(TAXPAYER_INFORMATION, "taxpayerInformation");
        paramDescriptions.put(OTHER_DEPENDENTS, "dependentInformation");
        paramDescriptions.put(STATUS_UPDATED, "latestTaxpayerStatusTimestamp");
        paramDescriptions.put(STATUS_OPERATOR, "latestTaxpayerStatusOperatorName");
        paramDescriptions.put(UNKNOWN, "");
    }

    private static HashMap<TaxpayerCaseSearchResultColumns, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static List<String> getParamDescriptionList(boolean includeUnknownValue){
        List<String> result = new ArrayList<>(getParamDescriptions().values());
        if (!includeUnknownValue){
            result.remove(UNKNOWN.value());
        }
        return result;
    }
    
    public static String getParamDescription(TaxpayerCaseSearchResultColumns name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<TaxpayerCaseSearchResultColumns, String> paramValues = new HashMap<>();

    private synchronized static HashMap<TaxpayerCaseSearchResultColumns, String> getParamValues(){
        if (paramValues.isEmpty()){
            TaxpayerCaseSearchResultColumns[] valueArray = TaxpayerCaseSearchResultColumns.values();
            if (valueArray != null){
                for (TaxpayerCaseSearchResultColumns valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(TaxpayerCaseSearchResultColumns name){
        return getParamValues().get(name);
    }
    
    public static TaxpayerCaseSearchResultColumns convertParamValueToType(String webParamValue){
        HashMap<TaxpayerCaseSearchResultColumns, String> localParamValues = getParamValues();
        Set<TaxpayerCaseSearchResultColumns> keys = localParamValues.keySet();
        Iterator<TaxpayerCaseSearchResultColumns> itr = keys.iterator();
        TaxpayerCaseSearchResultColumns result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<TaxpayerCaseSearchResultColumns> getColumnsForTaxFilingCaseSearchResultList(boolean includeUnknownValue) {
        List<TaxpayerCaseSearchResultColumns> result = new ArrayList<>();
        TaxpayerCaseSearchResultColumns[] valueArray = TaxpayerCaseSearchResultColumns.values();
        if (valueArray != null){
            for (TaxpayerCaseSearchResultColumns valueObj : valueArray){
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
        TaxpayerCaseSearchResultColumns[] valueArray = TaxpayerCaseSearchResultColumns.values();
        if (valueArray != null){
            for (TaxpayerCaseSearchResultColumns valueObj : valueArray){
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
        TaxpayerCaseSearchResultColumns[] valueArray = TaxpayerCaseSearchResultColumns.values();
        if (valueArray != null){
            for (TaxpayerCaseSearchResultColumns valueObj : valueArray){
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
    public static TaxpayerCaseSearchResultColumns convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static TaxpayerCaseSearchResultColumns convertEnumValueToType(String value, boolean allowNullReturned){
        TaxpayerCaseSearchResultColumns result = null;
        if (value != null){
            TaxpayerCaseSearchResultColumns[] valueArray = TaxpayerCaseSearchResultColumns.values();
            for (TaxpayerCaseSearchResultColumns valueObj : valueArray){
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
    public static TaxpayerCaseSearchResultColumns convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static TaxpayerCaseSearchResultColumns convertEnumNameToType(String name, boolean allowNullReturned){
        TaxpayerCaseSearchResultColumns result = null;
        if (name != null){
            TaxpayerCaseSearchResultColumns[] valueArray = TaxpayerCaseSearchResultColumns.values();
            for (TaxpayerCaseSearchResultColumns valueObj : valueArray){
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
    public static TaxpayerCaseSearchResultColumns convertStringToType(String name){
        TaxpayerCaseSearchResultColumns result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public static TaxpayerCaseSearchResultColumns convertIndexToType(Integer enumIndex) {
        return convertIndexToType(enumIndex, false);
    }

    public static TaxpayerCaseSearchResultColumns convertIndexToType(Integer enumIndex, boolean allowNullReturned) {
        try{
            return TaxpayerCaseSearchResultColumns.values()[enumIndex];
        }catch (Exception ex){
            if (allowNullReturned){
                return null;
            }else{
                return UNKNOWN;
            }
        }
    }

    private final String value;
    TaxpayerCaseSearchResultColumns(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
