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

package com.zcomapproach.garden.rose;

import com.zcomapproach.garden.util.GardenData;
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
public enum RosePageName {
    //Public
    WelcomePage(GardenData.generateUUIDString()),
    RegisterPage(GardenData.generateUUIDString()),
    LoginPage(GardenData.generateUUIDString()),
    RedeemCredentialsPage(GardenData.generateUUIDString()),
    AccountConfirmationPage(GardenData.generateUUIDString()),
    ContactUsPage(GardenData.generateUUIDString()),
    PublicPostPage(GardenData.generateUUIDString()),
    //Business
    ComposeEmailPage(GardenData.generateUUIDString()),
    BusinessHomePage(GardenData.generateUUIDString()),
    BusinessEmailBoxPage(GardenData.generateUUIDString()),
    PrintableBusinessEmailBoxPage(GardenData.generateUUIDString()),
    SentEmailBoxPage(GardenData.generateUUIDString()),
    PrintableSentEmailBoxPage(GardenData.generateUUIDString()),
    EmployeeProfileListPage(GardenData.generateUUIDString()),
    EmployeeProfilePage(GardenData.generateUUIDString()),
    UserProfilePage(GardenData.generateUUIDString()),
    UserProfileListPage(GardenData.generateUUIDString()),
    ClientProfilePage(GardenData.generateUUIDString()),
    ClientProfileListPage(GardenData.generateUUIDString()),
    TaxcorpCaseMgtPage(GardenData.generateUUIDString()),
    TaxcorpCasePrintablePage(GardenData.generateUUIDString()),
    TaxcorpCaseConfirmPage(GardenData.generateUUIDString()),
    TaxFilingTypeProfileListPage(GardenData.generateUUIDString()),
    TaxpayerCaseMgtPage(GardenData.generateUUIDString()),
    TaxpayerCasePrintablePage(GardenData.generateUUIDString()),
    TaxpayerCaseConfirmPage(GardenData.generateUUIDString()),
    EmployeeTaskListPage(GardenData.generateUUIDString()),
    SearchEnginePage(GardenData.generateUUIDString()),
    SearchTaxcorpFilingResultPage(GardenData.generateUUIDString()),
    SearchTaxpayerResultPage(GardenData.generateUUIDString()),
    SearchUserResultPage(GardenData.generateUUIDString()),
    SearchTaxcorpResultPage(GardenData.generateUUIDString()),
    SearchTaxcorpBillBalanceResultPage(GardenData.generateUUIDString()),
    WorkTeamListPage(GardenData.generateUUIDString()),
    WorkTeamProfilePage(GardenData.generateUUIDString()),
    //Services
    RoseInvoicePage(GardenData.generateUUIDString()),
    RoseInvoicePrintablePage(GardenData.generateUUIDString()),
    UploadDocumentPage(GardenData.generateUUIDString()),
    RoseDocumentMgtPage(GardenData.generateUUIDString()),
    RoseContactPage(GardenData.generateUUIDString()),
    TaxcorpFilingContactPage(GardenData.generateUUIDString()),
    WebPostPublishPage(GardenData.generateUUIDString()),
    WebPostPublishOptionsPage(GardenData.generateUUIDString()),
    HistoricalWebPostListPage(GardenData.generateUUIDString()),
    EditWebPostPage(GardenData.generateUUIDString()),
    //Customer
    CustomerHomePage(GardenData.generateUUIDString()),
    YourAccountPage(GardenData.generateUUIDString()),
    TaxcorpCasePage(GardenData.generateUUIDString()),
    TaxcorpCaseViewPage(GardenData.generateUUIDString()),
    TaxpayerCasePage(GardenData.generateUUIDString()),
    TaxpayerCaseViewPage(GardenData.generateUUIDString()),
    HistoricalTaxpayerCasesPage(GardenData.generateUUIDString()),
    HistoricalTaxcorpCasesPage(GardenData.generateUUIDString()),
    UNKNOWN("Unknown"); //special usage
    
    private static final HashMap<RosePageName, String> paramDescriptions = new HashMap<>();
    static{
        paramDescriptions.put(WelcomePage, "This page includes Rose setup part");
        paramDescriptions.put(EmployeeTaskListPage, "Task list of a specific employee");
    }

    private static HashMap<RosePageName, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static String getParamDescription(RosePageName name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<RosePageName, String> paramValues = new HashMap<>();

    private synchronized static HashMap<RosePageName, String> getParamValues(){
        if (paramValues.isEmpty()){
            RosePageName[] valueArray = RosePageName.values();
            if (valueArray != null){
                for (RosePageName valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(RosePageName name){
        return getParamValues().get(name);
    }
    
    public static RosePageName convertParamValueToType(String webParamValue){
        HashMap<RosePageName, String> localParamValues = getParamValues();
        Set<RosePageName> keys = localParamValues.keySet();
        Iterator<RosePageName> itr = keys.iterator();
        RosePageName result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<RosePageName> getRosePageNameList(boolean includeUnknownValue) {
        List<RosePageName> result = new ArrayList<>();
        RosePageName[] valueArray = RosePageName.values();
        if (valueArray != null){
            for (RosePageName valueObj : valueArray){
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
        RosePageName[] valueArray = RosePageName.values();
        if (valueArray != null){
            for (RosePageName valueObj : valueArray){
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
        RosePageName[] valueArray = RosePageName.values();
        if (valueArray != null){
            for (RosePageName valueObj : valueArray){
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
    public static RosePageName convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static RosePageName convertEnumValueToType(String value, boolean allowNullReturned){
        RosePageName result = null;
        if (value != null){
            RosePageName[] valueArray = RosePageName.values();
            for (RosePageName valueObj : valueArray){
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
    public static RosePageName convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static RosePageName convertEnumNameToType(String name, boolean allowNullReturned){
        RosePageName result = null;
        if (name != null){
            RosePageName[] valueArray = RosePageName.values();
            for (RosePageName valueObj : valueArray){
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
    public static RosePageName convertStringToType(String name){
        RosePageName result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    private final String value;
    RosePageName(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
