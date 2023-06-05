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
 *
 * @author zhijun98
 */
public enum TaxpayerCaseStatusReportColumns {

    SSN("SSN"),
    TAXPAYER("Taxpayer"),
    SPOUSE("Spouse"),
    FEDERAL_FILING_STATUS("Status"),
    DEADLINE("Deadline"),
    EXTENSION("Extension"),
    RESIDENCY("Residency"),
    RECEIVE("Receive"),
    PAYMENT("Payment"),
    SCAN("Scan"),
    PREPARE("Prepare"),
    REVIEW("Manager Review"),
    CPA_REVIEW("CPA Review"),
    NOTIFY("Notify"),
    PICKUP("Pickup"),
    SIGN("Sign"),
    EFILE("eFile"),
    CONTACT("Contact"),
    MEMO("Memo"),
    UNKNOWN("Unknown"); //special usage//special usage
    
    /**
     * notes on the column name to describe the corresponding names in the old excel file
     */
    private static final HashMap<TaxpayerCaseStatusReportColumns, String> paramNotes = new HashMap<>();
    static{
        paramNotes.put(TAXPAYER, "Primary taxpayer's name in the google's excel report");
        paramNotes.put(SPOUSE, "Spouse name in the google's excel report");
        paramNotes.put(DEADLINE, "Deadline of taxpayer case");
        paramNotes.put(EXTENSION, "Extension of taxpayer case");
        paramNotes.put(SSN, "[SSN] in the excel report");
        paramNotes.put(FEDERAL_FILING_STATUS, "[Type] in the google's excel report");
        paramNotes.put(RESIDENCY, "[State] in the google's excel report");
        paramNotes.put(RECEIVE, "[Recieve] & [REC] in the google's excel report. It corresponds to the work status: Customer or agent set up a taxpayer case.");
        paramNotes.put(PAYMENT, "[PMT] in the google's excel report. It simply shows current bill's total and balance status.");
        paramNotes.put(SCAN, "[扫描] in the google's excel report. It corresponds to the work status: Scanned customer documents.");
        paramNotes.put(PREPARE, "[Prepare] & [PRE] in the google's excel report. It corresponds to the work status: Prepare materials for tax.");
        paramNotes.put(REVIEW, "[Review] & [By Whom] in the google's excel report. It corresponds to the work status: Manager reviewed.");
        paramNotes.put(CPA_REVIEW, "[Review] & [By Whom] in the google's excel report. It corresponds to the work status: CPA approved review.");
        paramNotes.put(NOTIFY, "[通知] in the google's excel report. It corresponds to the work status: Notify customers of signature and payment");
        paramNotes.put(PICKUP, "[P/U] in the google's excel report. It corresponds to the work status: Pickup documents by customers");
        paramNotes.put(SIGN, "[签字页] in the google's excel report. It corresponds to the work status: Customer signed for e-filing");
        paramNotes.put(EFILE, "[EFILE] in the google's excel report. It corresponds to the work status: E-filed and completed");
        paramNotes.put(CONTACT, "[联系方式] in the google's excel report. It's the contact information recorded in the taxpayer case.");
        paramNotes.put(MEMO, "[Memo] in the google's excel report. It contains the latest memo recorded in the taxpayer case. As for other memos, refer to the details of the taxpayer case.");
    }
    
    public static GardenTaxpayerCaseStatus getCorrespondingStatus(TaxpayerCaseStatusReportColumns statusColumnType){
    
        switch (statusColumnType){
            case RECEIVE:
                return GardenTaxpayerCaseStatus.SETUP_TAXPAYER_CASE;
            case SCAN:
                return GardenTaxpayerCaseStatus.SCANNED_DOCUMENTS;
            case PREPARE:
                return GardenTaxpayerCaseStatus.PREPARE_TAX_MATERIAL;
            case REVIEW:
                return GardenTaxpayerCaseStatus.CPA_APPROVED_EXTENSION;
            case CPA_REVIEW:
                return GardenTaxpayerCaseStatus.CPA_APPROVED_REVIEW;
            case NOTIFY:
                return GardenTaxpayerCaseStatus.NOTIFY_FOR_SIGNATURE_AND_PAYMENT;
            case PICKUP:
                return GardenTaxpayerCaseStatus.PICKUP_BY_CUSTOMER;
            case SIGN:
                return GardenTaxpayerCaseStatus.CUSTOMER_SIGNED_FOR_EFILING;
            case EFILE:
                return GardenTaxpayerCaseStatus.EFILED_AND_COMPLETED;
            default:
                return GardenTaxpayerCaseStatus.UNKNOWN;
        }
    }
    
    /**
     * corresponding method signature
     */
    private static final HashMap<TaxpayerCaseStatusReportColumns, String> paramDescriptions = new HashMap<>();
    static{
        paramDescriptions.put(SSN, "primaryTaxpayerSsn");
        paramDescriptions.put(TAXPAYER, "taxpayerName");
        paramDescriptions.put(SPOUSE, "spouseName");
        paramDescriptions.put(FEDERAL_FILING_STATUS, "federalFilingStatus");
        paramDescriptions.put(DEADLINE, "deadline");
        paramDescriptions.put(EXTENSION, "extension");
        paramDescriptions.put(RESIDENCY, "taxpayerResidencyMemo");
        paramDescriptions.put(RECEIVE, "receiveTaxpayerCaseStatus");
        paramDescriptions.put(PAYMENT, "balanceTotalText");
        paramDescriptions.put(SCAN, "taxpayerCaseScanStatus");
        paramDescriptions.put(PREPARE, "prepareTaxMaterialStatus");
        paramDescriptions.put(REVIEW, "manangerReviewStatus");
        paramDescriptions.put(CPA_REVIEW, "cpaApprovedStatus");
        paramDescriptions.put(NOTIFY, "notifySignatureAndPaymentStatus");
        paramDescriptions.put(PICKUP, "pickupByCustomerStatus");
        paramDescriptions.put(SIGN, "customerSignatureStatus");
        paramDescriptions.put(EFILE, "efileStatus");
        paramDescriptions.put(CONTACT, "contactInfo");
        paramDescriptions.put(MEMO, "taxpayerCaseMemoHistory");
        paramDescriptions.put(UNKNOWN, "");
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
        result.add(DEADLINE.value);
        result.add(EXTENSION.value);
        result.add(RESIDENCY.value);
        result.add(RECEIVE.value);
        result.add(PAYMENT.value);
        result.add(SCAN.value);
        result.add(PREPARE.value);
        result.add(REVIEW.value);
        result.add(CPA_REVIEW.value);
        result.add(NOTIFY.value);
        result.add(PICKUP.value);
        result.add(SIGN.value);
        result.add(EFILE.value);
        result.add(CONTACT.value);
        result.add(MEMO.value);
        
        return result;
    }
    
    /**
     * Help filter
     * @return 
     */
    public static List<String> getEnumDateColumnValueList() {
        List<String> result = new ArrayList<>();
        
        return result;
    }
    
    private static HashMap<TaxpayerCaseStatusReportColumns, String> getParamNotes(){
        return paramNotes;
    }
    
    
    public static List<String> getParamNoteList(){
        return new ArrayList<>(getParamNotes().values());
    }
    
    public static String getParamNote(TaxpayerCaseStatusReportColumns name){
        return getParamNotes().get(name);
    }
    
    private static HashMap<TaxpayerCaseStatusReportColumns, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    
    public static List<String> getParamDescriptionList(){
        return new ArrayList<>(getParamDescriptions().values());
    }
    
    public static String getParamDescription(TaxpayerCaseStatusReportColumns name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<TaxpayerCaseStatusReportColumns, String> paramValues = new HashMap<>();

    private synchronized static HashMap<TaxpayerCaseStatusReportColumns, String> getParamValues(){
        if (paramValues.isEmpty()){
            TaxpayerCaseStatusReportColumns[] valueArray = TaxpayerCaseStatusReportColumns.values();
            if (valueArray != null){
                for (TaxpayerCaseStatusReportColumns valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(TaxpayerCaseStatusReportColumns name){
        return getParamValues().get(name);
    }
    
    public static TaxpayerCaseStatusReportColumns convertParamValueToType(String webParamValue){
        HashMap<TaxpayerCaseStatusReportColumns, String> localParamValues = getParamValues();
        Set<TaxpayerCaseStatusReportColumns> keys = localParamValues.keySet();
        Iterator<TaxpayerCaseStatusReportColumns> itr = keys.iterator();
        TaxpayerCaseStatusReportColumns result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<TaxpayerCaseStatusReportColumns> getTaxpayerCaseStatusColumnsList(boolean includeUnknownValue) {
        List<TaxpayerCaseStatusReportColumns> result = new ArrayList<>();
        TaxpayerCaseStatusReportColumns[] valueArray = TaxpayerCaseStatusReportColumns.values();
        if (valueArray != null){
            for (TaxpayerCaseStatusReportColumns valueObj : valueArray){
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
        TaxpayerCaseStatusReportColumns[] valueArray = TaxpayerCaseStatusReportColumns.values();
        if (valueArray != null){
            for (TaxpayerCaseStatusReportColumns valueObj : valueArray){
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
        TaxpayerCaseStatusReportColumns[] valueArray = TaxpayerCaseStatusReportColumns.values();
        if (valueArray != null){
            for (TaxpayerCaseStatusReportColumns valueObj : valueArray){
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
    public static TaxpayerCaseStatusReportColumns convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static TaxpayerCaseStatusReportColumns convertEnumValueToType(String value, boolean allowNullReturned){
        TaxpayerCaseStatusReportColumns result = null;
        if (value != null){
            TaxpayerCaseStatusReportColumns[] valueArray = TaxpayerCaseStatusReportColumns.values();
            for (TaxpayerCaseStatusReportColumns valueObj : valueArray){
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
    public static TaxpayerCaseStatusReportColumns convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static TaxpayerCaseStatusReportColumns convertEnumNameToType(String name, boolean allowNullReturned){
        TaxpayerCaseStatusReportColumns result = null;
        if (name != null){
            TaxpayerCaseStatusReportColumns[] valueArray = TaxpayerCaseStatusReportColumns.values();
            for (TaxpayerCaseStatusReportColumns valueObj : valueArray){
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
    public static TaxpayerCaseStatusReportColumns convertStringToType(String name){
        TaxpayerCaseStatusReportColumns result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public static TaxpayerCaseStatusReportColumns convertIndexToType(Integer enumIndex) {
        return convertIndexToType(enumIndex, false);
    }

    public static TaxpayerCaseStatusReportColumns convertIndexToType(Integer enumIndex, boolean allowNullReturned) {
        try{
            return TaxpayerCaseStatusReportColumns.values()[enumIndex];
        }catch (Exception ex){
            if (allowNullReturned){
                return null;
            }else{
                return UNKNOWN;
            }
        }
    }

    private final String value;
    TaxpayerCaseStatusReportColumns(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
