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

package com.zcomapproach.garden.peony.settings;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * Folders under the folder which is PeonyLocalSettings::getDefaultLocalUserPath()
 * @author zhijun98
 */
public enum PeonyLocalFolderName {

    TEMP("temp"),
    UNKNOWN("Unknown"); //special usage//special usage
    
    private static final HashMap<PeonyLocalFolderName, String> paramDescriptions = new HashMap<>();
    static{
        //todo: add description for params
        paramDescriptions.put(UNKNOWN, "");
    }

    private static HashMap<PeonyLocalFolderName, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static List<String> getParamDescriptionList(){
        return new ArrayList<>(getParamDescriptions().values());
    }
    
    public static String getParamDescription(PeonyLocalFolderName name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<PeonyLocalFolderName, String> paramValues = new HashMap<>();

    private synchronized static HashMap<PeonyLocalFolderName, String> getParamValues(){
        if (paramValues.isEmpty()){
            PeonyLocalFolderName[] valueArray = PeonyLocalFolderName.values();
            if (valueArray != null){
                for (PeonyLocalFolderName valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(PeonyLocalFolderName name){
        return getParamValues().get(name);
    }
    
    public static PeonyLocalFolderName convertParamValueToType(String webParamValue){
        HashMap<PeonyLocalFolderName, String> localParamValues = getParamValues();
        Set<PeonyLocalFolderName> keys = localParamValues.keySet();
        Iterator<PeonyLocalFolderName> itr = keys.iterator();
        PeonyLocalFolderName result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<PeonyLocalFolderName> getPeonyLocalSettingsFolderNameList(boolean includeUnknownValue) {
        List<PeonyLocalFolderName> result = new ArrayList<>();
        PeonyLocalFolderName[] valueArray = PeonyLocalFolderName.values();
        if (valueArray != null){
            for (PeonyLocalFolderName valueObj : valueArray){
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
        PeonyLocalFolderName[] valueArray = PeonyLocalFolderName.values();
        if (valueArray != null){
            for (PeonyLocalFolderName valueObj : valueArray){
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
        PeonyLocalFolderName[] valueArray = PeonyLocalFolderName.values();
        if (valueArray != null){
            for (PeonyLocalFolderName valueObj : valueArray){
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
    public static PeonyLocalFolderName convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static PeonyLocalFolderName convertEnumValueToType(String value, boolean allowNullReturned){
        PeonyLocalFolderName result = null;
        if (value != null){
            PeonyLocalFolderName[] valueArray = PeonyLocalFolderName.values();
            for (PeonyLocalFolderName valueObj : valueArray){
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
    public static PeonyLocalFolderName convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static PeonyLocalFolderName convertEnumNameToType(String name, boolean allowNullReturned){
        PeonyLocalFolderName result = null;
        if (name != null){
            PeonyLocalFolderName[] valueArray = PeonyLocalFolderName.values();
            for (PeonyLocalFolderName valueObj : valueArray){
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
    public static PeonyLocalFolderName convertStringToType(String name){
        PeonyLocalFolderName result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public static PeonyLocalFolderName convertIndexToType(Integer enumIndex) {
        return convertIndexToType(enumIndex, false);
    }

    public static PeonyLocalFolderName convertIndexToType(Integer enumIndex, boolean allowNullReturned) {
        try{
            return PeonyLocalFolderName.values()[enumIndex];
        }catch (Exception ex){
            if (allowNullReturned){
                return null;
            }else{
                return UNKNOWN;
            }
        }
    }

    private final String value;
    PeonyLocalFolderName(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
