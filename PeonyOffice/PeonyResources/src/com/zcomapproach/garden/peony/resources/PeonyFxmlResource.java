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

package com.zcomapproach.garden.peony.resources;

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
public enum PeonyFxmlResource {
    SpamRulePane("SpamRulePane.fxml"),
    PeonyTaskViewPane("PeonyTaskViewPane.fxml"),
    PeonyManagementPanel("PeonyManagementPanel.fxml"),
    PeonySystemSettingsPane("PeonySystemSettingsPane.fxml"),
    PeonySmsPane("PeonySmsPane.fxml"),
    LocationDataEntryPane("LocationDataEntryPane.fxml"),
    TaxFilingDetailsPanel("TaxFilingDetailsPanel.fxml"),
    BillPaymentRootPane("BillPaymentRootPane.fxml"),
    PaymentDataEntryPane("PaymentDataEntryPane.fxml"),
    UNKNOWN("Unknown"); //special usage//special usage
    
    private static final HashMap<PeonyFxmlResource, String> paramDescriptions = new HashMap<>();
    static{
        paramDescriptions.put(UNKNOWN, "");
    }

    private static HashMap<PeonyFxmlResource, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static String getParamDescription(PeonyFxmlResource name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<PeonyFxmlResource, String> paramValues = new HashMap<>();

    private synchronized static HashMap<PeonyFxmlResource, String> getParamValues(){
        if (paramValues.isEmpty()){
            PeonyFxmlResource[] valueArray = PeonyFxmlResource.values();
            if (valueArray != null){
                for (PeonyFxmlResource valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(PeonyFxmlResource name){
        return getParamValues().get(name);
    }
    
    public static PeonyFxmlResource convertParamValueToType(String webParamValue){
        HashMap<PeonyFxmlResource, String> localParamValues = getParamValues();
        Set<PeonyFxmlResource> keys = localParamValues.keySet();
        Iterator<PeonyFxmlResource> itr = keys.iterator();
        PeonyFxmlResource result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<PeonyFxmlResource> getDesktopFxmlResourceList(boolean includeUnknownValue) {
        List<PeonyFxmlResource> result = new ArrayList<>();
        PeonyFxmlResource[] valueArray = PeonyFxmlResource.values();
        if (valueArray != null){
            for (PeonyFxmlResource valueObj : valueArray){
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
        PeonyFxmlResource[] valueArray = PeonyFxmlResource.values();
        if (valueArray != null){
            for (PeonyFxmlResource valueObj : valueArray){
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
        PeonyFxmlResource[] valueArray = PeonyFxmlResource.values();
        if (valueArray != null){
            for (PeonyFxmlResource valueObj : valueArray){
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
    public static PeonyFxmlResource convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static PeonyFxmlResource convertEnumValueToType(String value, boolean allowNullReturned){
        PeonyFxmlResource result = null;
        if (value != null){
            PeonyFxmlResource[] valueArray = PeonyFxmlResource.values();
            for (PeonyFxmlResource valueObj : valueArray){
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
    public static PeonyFxmlResource convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static PeonyFxmlResource convertEnumNameToType(String name, boolean allowNullReturned){
        PeonyFxmlResource result = null;
        if (name != null){
            PeonyFxmlResource[] valueArray = PeonyFxmlResource.values();
            for (PeonyFxmlResource valueObj : valueArray){
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
    public static PeonyFxmlResource convertStringToType(String name){
        PeonyFxmlResource result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public static PeonyFxmlResource convertIndexToType(Integer enumIndex) {
        return convertIndexToType(enumIndex, false);
    }

    public static PeonyFxmlResource convertIndexToType(Integer enumIndex, boolean allowNullReturned) {
        try{
            return PeonyFxmlResource.values()[enumIndex];
        }catch (Exception ex){
            if (allowNullReturned){
                return null;
            }else{
                return UNKNOWN;
            }
        }
    }

    private final String value;
    PeonyFxmlResource(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
