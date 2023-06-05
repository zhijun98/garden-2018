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

package com.zcomapproach.garden.peony.view.data.validation;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * @deprecated - replaced by improved G02EntityValidator with improved ZcaEntityValidationException
 * @author zhijun98
 */
public enum PeonyBillFieldRequirement {

    BillContent("Bill Content - it canot be empty and max 450 characters"),
    BillPrice("Bill Price - it demands a digit number"), 
    BillDiscount("Bill Discount - it demands a digit number"), 
    BillDiscountType("Bill Discount Type - it demands a selection"), 
    BillDueDate("Bill Due-Date: it cannot be empty."),
    UNKNOWN("Unknown"); //special usage
    
    private static final HashMap<PeonyBillFieldRequirement, String> paramDescriptions = new HashMap<>();
    static{
        //todo: add description for params
        paramDescriptions.put(UNKNOWN, "");
    }

    private static HashMap<PeonyBillFieldRequirement, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static List<String> getParamDescriptionList(){
        return new ArrayList<>(getParamDescriptions().values());
    }
    
    public static String getParamDescription(PeonyBillFieldRequirement name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<PeonyBillFieldRequirement, String> paramValues = new HashMap<>();

    private synchronized static HashMap<PeonyBillFieldRequirement, String> getParamValues(){
        if (paramValues.isEmpty()){
            PeonyBillFieldRequirement[] valueArray = PeonyBillFieldRequirement.values();
            if (valueArray != null){
                for (PeonyBillFieldRequirement valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(PeonyBillFieldRequirement name){
        return getParamValues().get(name);
    }
    
    public static PeonyBillFieldRequirement convertParamValueToType(String webParamValue){
        HashMap<PeonyBillFieldRequirement, String> localParamValues = getParamValues();
        Set<PeonyBillFieldRequirement> keys = localParamValues.keySet();
        Iterator<PeonyBillFieldRequirement> itr = keys.iterator();
        PeonyBillFieldRequirement result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<PeonyBillFieldRequirement> getPeonyBillFieldRequirementList(boolean includeUnknownValue) {
        List<PeonyBillFieldRequirement> result = new ArrayList<>();
        PeonyBillFieldRequirement[] valueArray = PeonyBillFieldRequirement.values();
        if (valueArray != null){
            for (PeonyBillFieldRequirement valueObj : valueArray){
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
        PeonyBillFieldRequirement[] valueArray = PeonyBillFieldRequirement.values();
        if (valueArray != null){
            for (PeonyBillFieldRequirement valueObj : valueArray){
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
        PeonyBillFieldRequirement[] valueArray = PeonyBillFieldRequirement.values();
        if (valueArray != null){
            for (PeonyBillFieldRequirement valueObj : valueArray){
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
    public static PeonyBillFieldRequirement convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static PeonyBillFieldRequirement convertEnumValueToType(String value, boolean allowNullReturned){
        PeonyBillFieldRequirement result = null;
        if (value != null){
            PeonyBillFieldRequirement[] valueArray = PeonyBillFieldRequirement.values();
            for (PeonyBillFieldRequirement valueObj : valueArray){
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
    public static PeonyBillFieldRequirement convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static PeonyBillFieldRequirement convertEnumNameToType(String name, boolean allowNullReturned){
        PeonyBillFieldRequirement result = null;
        if (name != null){
            PeonyBillFieldRequirement[] valueArray = PeonyBillFieldRequirement.values();
            for (PeonyBillFieldRequirement valueObj : valueArray){
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
    public static PeonyBillFieldRequirement convertStringToType(String name){
        PeonyBillFieldRequirement result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public static PeonyBillFieldRequirement convertIndexToType(Integer enumIndex) {
        return convertIndexToType(enumIndex, false);
    }

    public static PeonyBillFieldRequirement convertIndexToType(Integer enumIndex, boolean allowNullReturned) {
        try{
            return PeonyBillFieldRequirement.values()[enumIndex];
        }catch (Exception ex){
            if (allowNullReturned){
                return null;
            }else{
                return UNKNOWN;
            }
        }
    }

    private final String value;
    PeonyBillFieldRequirement(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
