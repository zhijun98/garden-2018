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

package com.zcomapproach.garden.rose.data;

import com.zcomapproach.garden.util.GardenData;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * Params are encoded so that web users make no sense on the parameter's key and value. Thus, here, the param items can 
 * be used as param-key or param-value. Refer to RoseParamsBean
 * @author zhijun98
 */
public enum RoseWebParamKey {
    RoseArchivedFileUuidParam(GardenData.generateUUIDString()),
    EmailToParam(GardenData.generateUUIDString()),
    EmailIdParam(GardenData.generateUUIDString()),
    RoseChatMessageUuidParam(GardenData.generateUUIDString()),
    EntityTypeParam(GardenData.generateUUIDString()),
    EntityUuidParam(GardenData.generateUUIDString()),
    RoseBillUuidParam(GardenData.generateUUIDString()),
    ViewPurposeParam(GardenData.generateUUIDString()),
    WebPostUuidParam(GardenData.generateUUIDString()),
    CustomerUuidParam(GardenData.generateUUIDString()),
    AccountUuidParam(GardenData.generateUUIDString()),
    UserUuidParam(GardenData.generateUUIDString()),
    MergingUserUuidParam(GardenData.generateUUIDString()),
    EmployeeAccountUuidParam(GardenData.generateUUIDString()),
    RegistrationConfirmationCodeParam(GardenData.generateUUIDString()),
    TaxpayerCaseUuidParam(GardenData.generateUUIDString()),
    TaxcorpCaseUuidParam(GardenData.generateUUIDString()),
    WorkTeamUuidParam(GardenData.generateUUIDString()),
    UNKNOWN("Unknown"); //special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage//special usage
    
    private static final HashMap<RoseWebParamKey, String> paramValues = new HashMap<>();

    private synchronized static HashMap<RoseWebParamKey, String> getParamValues(){
        if (paramValues.isEmpty()){
            RoseWebParamKey[] valueArray = RoseWebParamKey.values();
            if (valueArray != null){
                for (RoseWebParamKey valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(RoseWebParamKey name){
        return getParamValues().get(name);
    }
    
    public static RoseWebParamKey convertParamValueToType(String webParamValue){
        HashMap<RoseWebParamKey, String> localParamValues = getParamValues();
        Set<RoseWebParamKey> keys = localParamValues.keySet();
        Iterator<RoseWebParamKey> itr = keys.iterator();
        RoseWebParamKey result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<RoseWebParamKey> getRoseWebParamsList(boolean includeUnknownValue) {
        List<RoseWebParamKey> result = new ArrayList<>();
        RoseWebParamKey[] valueArray = RoseWebParamKey.values();
        if (valueArray != null){
            for (RoseWebParamKey valueObj : valueArray){
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
        RoseWebParamKey[] valueArray = RoseWebParamKey.values();
        if (valueArray != null){
            for (RoseWebParamKey valueObj : valueArray){
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
        RoseWebParamKey[] valueArray = RoseWebParamKey.values();
        if (valueArray != null){
            for (RoseWebParamKey valueObj : valueArray){
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
    public static RoseWebParamKey convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static RoseWebParamKey convertEnumValueToType(String value, boolean allowNullReturned){
        RoseWebParamKey result = null;
        if (value != null){
            RoseWebParamKey[] valueArray = RoseWebParamKey.values();
            for (RoseWebParamKey valueObj : valueArray){
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
    public static RoseWebParamKey convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static RoseWebParamKey convertEnumNameToType(String name, boolean allowNullReturned){
        RoseWebParamKey result = null;
        if (name != null){
            RoseWebParamKey[] valueArray = RoseWebParamKey.values();
            for (RoseWebParamKey valueObj : valueArray){
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
    public static RoseWebParamKey convertStringToType(String name){
        RoseWebParamKey result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    private final String value;
    RoseWebParamKey(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
