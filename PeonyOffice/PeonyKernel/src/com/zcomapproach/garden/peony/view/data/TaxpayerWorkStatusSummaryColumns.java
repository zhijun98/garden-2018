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

import com.zcomapproach.garden.persistence.constant.GardenTaxpayerCaseStatus;
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
public enum TaxpayerWorkStatusSummaryColumns {
    SSN("SSN"),
    TAXPAYER("Taxpayer"),
    SPOUSE("Spouse"),
    FEDERAL_FILING_STATUS("Status"),
    CONTACT("Contact"),
    RESIDENCY("Residency"),
    BALANCE_AND_TOTAL("Balance/Total"),
    CONFIRMED_AND_DEPOSIT("Confirmed/Deposited"),
    
    RECEIVE("Receive"),
    SCAN("Scan"),
////    DOCUMENTS("Wait Doc"),
    PREPARE("Prepare"),
////    REQUEST_CPA("Request Review"),
    CPA_REVIEW("CPA Approved"),
    SIGNATURE_AND_PAYMENT("Signature/Payment"),
    EFILE_READY("eFile Ready"),
    EFILE_COMPLETED("eFile Completed"),
    PICKUP_READY("Pickup Ready"),
    PICKUP_COMPLETED("Pickup Completed"),
////    CLOSED_AND_NEXT("Closed/Next"),
////    CLOSED_AND_NO_NEXT("Closed/No-Next"),
    
    MEMO("Memo"),
    UNKNOWN("Unknown"); 
    /**
     * Help filter
     * @return 
     */
    public static List<String> getEnumDateColumnValueList() {
        List<String> result = new ArrayList<>();
        
        return result;
    }

    /**
     * Help filter
     * @return 
     */
    public static List<String> getEnumTextColumnValueList() {
        List<String> result = new ArrayList<>();
        
        result.add(SSN.value);
        result.add(TAXPAYER.value);
        result.add(SPOUSE.value);
        result.add(FEDERAL_FILING_STATUS.value);
        result.add(CONTACT.value);
        result.add(RESIDENCY.value);
        result.add(BALANCE_AND_TOTAL.value);
        result.add(CONFIRMED_AND_DEPOSIT.value);
        
        result.add(RECEIVE.value);
        result.add(SCAN.value);
////        result.add(DOCUMENTS.value);
        result.add(PREPARE.value);
////        result.add(REQUEST_CPA.value);
        result.add(CPA_REVIEW.value);
        result.add(SIGNATURE_AND_PAYMENT.value);
        result.add(EFILE_READY.value);
        result.add(EFILE_COMPLETED.value);
        result.add(PICKUP_READY.value);
        result.add(PICKUP_COMPLETED.value);
////        result.add(CLOSED_AND_NEXT.value);
////        result.add(CLOSED_AND_NO_NEXT.value);
        
        result.add(MEMO.value);
        
        return result;
    }
    
    private static final HashMap<TaxpayerWorkStatusSummaryColumns, String> paramNotes = new HashMap<>();
    static{
        paramNotes.put(SSN, "Primary taxpayer's SSN");
        paramNotes.put(TAXPAYER, "Primary taxpayer's name");
        paramNotes.put(SPOUSE, "Spouse name");
        paramNotes.put(FEDERAL_FILING_STATUS, "Federal filing status");
        paramNotes.put(CONTACT, "Taxpayer's contact information");
        paramNotes.put(RESIDENCY, "Federal tax residency/non-residency");
        paramNotes.put(BALANCE_AND_TOTAL, "Balance & Total");
        paramNotes.put(CONFIRMED_AND_DEPOSIT, "Payment's details");
        
        paramNotes.put(RECEIVE, GardenTaxpayerCaseStatus.SETUP_TAXPAYER_CASE.value());
        paramNotes.put(SCAN, GardenTaxpayerCaseStatus.SCANNED_DOCUMENTS.value());
////        paramNotes.put(DOCUMENTS, GardenTaxpayerCaseStatus.WAITING_FOR_MORE_DOCUMENTS.value());
        paramNotes.put(PREPARE, GardenTaxpayerCaseStatus.PREPARE_TAX_MATERIAL.value());
////        paramNotes.put(REQUEST_CPA, GardenTaxpayerCaseStatus.REQUEST_CPA_REVIEW.value());
        paramNotes.put(CPA_REVIEW, GardenTaxpayerCaseStatus.CPA_APPROVED_REVIEW.value());
        paramNotes.put(SIGNATURE_AND_PAYMENT, GardenTaxpayerCaseStatus.NOTIFY_FOR_SIGNATURE_AND_PAYMENT.value());
        paramNotes.put(EFILE_READY, GardenTaxpayerCaseStatus.READY_FOR_EFILE.value());
        paramNotes.put(EFILE_COMPLETED, GardenTaxpayerCaseStatus.EFILED_AND_COMPLETED.value());
        paramNotes.put(PICKUP_READY, GardenTaxpayerCaseStatus.READY_FOR_PICKUP.value());
        paramNotes.put(PICKUP_COMPLETED, GardenTaxpayerCaseStatus.PICKUP_BY_CUSTOMER.value());
////        paramNotes.put(CLOSED_AND_NEXT, GardenTaxpayerCaseStatus.CLOSED_AND_NEXT.value());
////        paramNotes.put(CLOSED_AND_NO_NEXT, GardenTaxpayerCaseStatus.CLOSED_AND_NO_NEXT.value());
        
        paramNotes.put(MEMO, "This is the latest taxpayer's memo. For more conversation in Taxpayer's memo, see details of the taxpayer case");
    }
    
    private static final HashMap<TaxpayerWorkStatusSummaryColumns, String> paramDescriptions = new HashMap<>();
    static{
        paramDescriptions.put(SSN, "primaryTaxpayerSsn");
        paramDescriptions.put(TAXPAYER, "taxpayerName");
        paramDescriptions.put(SPOUSE, "spouseName");
        paramDescriptions.put(FEDERAL_FILING_STATUS, "federalFilingStatus");
        paramDescriptions.put(CONTACT, "contactInfo");
        paramDescriptions.put(RESIDENCY, "taxpayerResidencyMemo");
        paramDescriptions.put(BALANCE_AND_TOTAL, "balanceTotalText");
        paramDescriptions.put(CONFIRMED_AND_DEPOSIT, "confirmedDepositedText");
        
        paramDescriptions.put(RECEIVE, "setupTaxpayerCaseStatus");
        paramDescriptions.put(SCAN, "taxpayerCaseScanStatus");
////        paramDescriptions.put(DOCUMENTS, "taxpayerCaseWaitDocStatus");
        paramDescriptions.put(PREPARE, "prepareTaxMaterialStatus");
////        paramDescriptions.put(REQUEST_CPA, "requestCpaStatus");
        paramDescriptions.put(CPA_REVIEW, "cpaApprovedStatus");
        paramDescriptions.put(SIGNATURE_AND_PAYMENT, "signatureAndPaymentStatus");
        paramDescriptions.put(EFILE_READY, "readyForEfileStatus");
        paramDescriptions.put(EFILE_COMPLETED, "efiledAndCompletedStatus");
        paramDescriptions.put(PICKUP_READY, "readyForPickupStatus");
        paramDescriptions.put(PICKUP_COMPLETED, "pickupByCustomerStatus");
////        paramDescriptions.put(CLOSED_AND_NEXT, "closedAndNextStatus");
////        paramDescriptions.put(CLOSED_AND_NO_NEXT, "closedAndNoNextStatus");
        
        paramDescriptions.put(MEMO, "taxpayerCaseLatestMemo");
        paramDescriptions.put(UNKNOWN, "");
    }

    private static HashMap<TaxpayerWorkStatusSummaryColumns, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static List<String> getParamDescriptionList(boolean includeUnknownValue){
        List<String> result = new ArrayList<>(getParamDescriptions().values());
        if (!includeUnknownValue){
            result.remove(UNKNOWN.value());
        }
        return result;
    }
    
    public static String getParamDescription(TaxpayerWorkStatusSummaryColumns name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<TaxpayerWorkStatusSummaryColumns, String> paramValues = new HashMap<>();

    private synchronized static HashMap<TaxpayerWorkStatusSummaryColumns, String> getParamValues(){
        if (paramValues.isEmpty()){
            TaxpayerWorkStatusSummaryColumns[] valueArray = TaxpayerWorkStatusSummaryColumns.values();
            if (valueArray != null){
                for (TaxpayerWorkStatusSummaryColumns valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(TaxpayerWorkStatusSummaryColumns name){
        return getParamValues().get(name);
    }
    
    public static TaxpayerWorkStatusSummaryColumns convertParamValueToType(String webParamValue){
        HashMap<TaxpayerWorkStatusSummaryColumns, String> localParamValues = getParamValues();
        Set<TaxpayerWorkStatusSummaryColumns> keys = localParamValues.keySet();
        Iterator<TaxpayerWorkStatusSummaryColumns> itr = keys.iterator();
        TaxpayerWorkStatusSummaryColumns result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<TaxpayerWorkStatusSummaryColumns> getColumnsForTaxFilingCaseSearchResultList(boolean includeUnknownValue) {
        List<TaxpayerWorkStatusSummaryColumns> result = new ArrayList<>();
        TaxpayerWorkStatusSummaryColumns[] valueArray = TaxpayerWorkStatusSummaryColumns.values();
        if (valueArray != null){
            for (TaxpayerWorkStatusSummaryColumns valueObj : valueArray){
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
        TaxpayerWorkStatusSummaryColumns[] valueArray = TaxpayerWorkStatusSummaryColumns.values();
        if (valueArray != null){
            for (TaxpayerWorkStatusSummaryColumns valueObj : valueArray){
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
        TaxpayerWorkStatusSummaryColumns[] valueArray = TaxpayerWorkStatusSummaryColumns.values();
        if (valueArray != null){
            for (TaxpayerWorkStatusSummaryColumns valueObj : valueArray){
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
    public static TaxpayerWorkStatusSummaryColumns convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static TaxpayerWorkStatusSummaryColumns convertEnumValueToType(String value, boolean allowNullReturned){
        TaxpayerWorkStatusSummaryColumns result = null;
        if (value != null){
            TaxpayerWorkStatusSummaryColumns[] valueArray = TaxpayerWorkStatusSummaryColumns.values();
            for (TaxpayerWorkStatusSummaryColumns valueObj : valueArray){
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
    public static TaxpayerWorkStatusSummaryColumns convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static TaxpayerWorkStatusSummaryColumns convertEnumNameToType(String name, boolean allowNullReturned){
        TaxpayerWorkStatusSummaryColumns result = null;
        if (name != null){
            TaxpayerWorkStatusSummaryColumns[] valueArray = TaxpayerWorkStatusSummaryColumns.values();
            for (TaxpayerWorkStatusSummaryColumns valueObj : valueArray){
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
    public static TaxpayerWorkStatusSummaryColumns convertStringToType(String name){
        TaxpayerWorkStatusSummaryColumns result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public static TaxpayerWorkStatusSummaryColumns convertIndexToType(Integer enumIndex) {
        return convertIndexToType(enumIndex, false);
    }

    public static TaxpayerWorkStatusSummaryColumns convertIndexToType(Integer enumIndex, boolean allowNullReturned) {
        try{
            return TaxpayerWorkStatusSummaryColumns.values()[enumIndex];
        }catch (Exception ex){
            if (allowNullReturned){
                return null;
            }else{
                return UNKNOWN;
            }
        }
    }

    private final String value;
    TaxpayerWorkStatusSummaryColumns(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
