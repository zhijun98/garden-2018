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
public enum TaxpayerCaseSearchCacheResultColumns {

    DEADLINE("Deadline"),
    EXTENSION("Extension"),
    FEDERAL_FILING_STATUS("Federal Filing Status"),
    SSN("SSN"),
    TAXPAYER("Taxpayer"),
    SPOUSE("Spouse"),
    CONTACT("Contact"),
    STATUS("Work Status"),
    STATUS_UPDATED("Timestamp"),
    STATUS_OPERATOR("Operator"),
    BALANCE_AND_TOTAL("Balance/Total"),
    CONFIRMED_AND_DEPOSIT("Confirmed/Deposited"),
    UNKNOWN("Unknown"); //special usage
    
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
        
        result.add(FEDERAL_FILING_STATUS.value);
        result.add(STATUS.value);
        result.add(SSN.value);
        result.add(STATUS_OPERATOR.value);
        result.add(TAXPAYER.value);
        result.add(SPOUSE.value);
        result.add(CONTACT.value);
        
        return result;
    }
    
    private static final HashMap<TaxpayerCaseSearchCacheResultColumns, String> paramDescriptions = new HashMap<>();
    static{
        paramDescriptions.put(DEADLINE, "deadline");
        paramDescriptions.put(EXTENSION, "extension");
        paramDescriptions.put(FEDERAL_FILING_STATUS, "federalFilingStatus");
        paramDescriptions.put(SSN, "primaryTaxpayerSsn");
        paramDescriptions.put(TAXPAYER, "taxpayerName");
        paramDescriptions.put(SPOUSE, "spouseName");
        paramDescriptions.put(CONTACT, "contactInfo");
        paramDescriptions.put(STATUS, "latestTaxpayerStatus");
        paramDescriptions.put(STATUS_UPDATED, "latestTaxpayerStatusTimestamp");
        paramDescriptions.put(STATUS_OPERATOR, "latestTaxpayerStatusOperatorName");
        paramDescriptions.put(BALANCE_AND_TOTAL, "balanceTotalText");
        paramDescriptions.put(CONFIRMED_AND_DEPOSIT, "confirmedDepositedText");
    }

    private static HashMap<TaxpayerCaseSearchCacheResultColumns, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static List<String> getParamDescriptionList(){
        return new ArrayList<>(getParamDescriptions().values());
    }
    
    public static String getParamDescription(TaxpayerCaseSearchCacheResultColumns name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<TaxpayerCaseSearchCacheResultColumns, String> paramValues = new HashMap<>();

    private synchronized static HashMap<TaxpayerCaseSearchCacheResultColumns, String> getParamValues(){
        if (paramValues.isEmpty()){
            TaxpayerCaseSearchCacheResultColumns[] valueArray = TaxpayerCaseSearchCacheResultColumns.values();
            if (valueArray != null){
                for (TaxpayerCaseSearchCacheResultColumns valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(TaxpayerCaseSearchCacheResultColumns name){
        return getParamValues().get(name);
    }
    
    public static TaxpayerCaseSearchCacheResultColumns convertParamValueToType(String webParamValue){
        HashMap<TaxpayerCaseSearchCacheResultColumns, String> localParamValues = getParamValues();
        Set<TaxpayerCaseSearchCacheResultColumns> keys = localParamValues.keySet();
        Iterator<TaxpayerCaseSearchCacheResultColumns> itr = keys.iterator();
        TaxpayerCaseSearchCacheResultColumns result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<TaxpayerCaseSearchCacheResultColumns> getTaxpayerCaseSearchCacheResultColumnsList(boolean includeUnknownValue) {
        List<TaxpayerCaseSearchCacheResultColumns> result = new ArrayList<>();
        TaxpayerCaseSearchCacheResultColumns[] valueArray = TaxpayerCaseSearchCacheResultColumns.values();
        if (valueArray != null){
            for (TaxpayerCaseSearchCacheResultColumns valueObj : valueArray){
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
        TaxpayerCaseSearchCacheResultColumns[] valueArray = TaxpayerCaseSearchCacheResultColumns.values();
        if (valueArray != null){
            for (TaxpayerCaseSearchCacheResultColumns valueObj : valueArray){
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
        TaxpayerCaseSearchCacheResultColumns[] valueArray = TaxpayerCaseSearchCacheResultColumns.values();
        if (valueArray != null){
            for (TaxpayerCaseSearchCacheResultColumns valueObj : valueArray){
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
    public static TaxpayerCaseSearchCacheResultColumns convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static TaxpayerCaseSearchCacheResultColumns convertEnumValueToType(String value, boolean allowNullReturned){
        TaxpayerCaseSearchCacheResultColumns result = null;
        if (value != null){
            TaxpayerCaseSearchCacheResultColumns[] valueArray = TaxpayerCaseSearchCacheResultColumns.values();
            for (TaxpayerCaseSearchCacheResultColumns valueObj : valueArray){
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
    public static TaxpayerCaseSearchCacheResultColumns convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static TaxpayerCaseSearchCacheResultColumns convertEnumNameToType(String name, boolean allowNullReturned){
        TaxpayerCaseSearchCacheResultColumns result = null;
        if (name != null){
            TaxpayerCaseSearchCacheResultColumns[] valueArray = TaxpayerCaseSearchCacheResultColumns.values();
            for (TaxpayerCaseSearchCacheResultColumns valueObj : valueArray){
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
    public static TaxpayerCaseSearchCacheResultColumns convertStringToType(String name){
        TaxpayerCaseSearchCacheResultColumns result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public static TaxpayerCaseSearchCacheResultColumns convertIndexToType(Integer enumIndex) {
        return convertIndexToType(enumIndex, false);
    }

    public static TaxpayerCaseSearchCacheResultColumns convertIndexToType(Integer enumIndex, boolean allowNullReturned) {
        try{
            return TaxpayerCaseSearchCacheResultColumns.values()[enumIndex];
        }catch (Exception ex){
            if (allowNullReturned){
                return null;
            }else{
                return UNKNOWN;
            }
        }
    }

    private final String value;
    TaxpayerCaseSearchCacheResultColumns(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
