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

package com.zcomapproach.garden.peony.resources.css;

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
public enum PeonyFontFileName {

    SimSun("simsun.ttc"),
    MingLiU("mingliu.ttc"),
    MSJH("msjh.ttc"),
    MSYH("msyh.ttc"),
    UNKNOWN("Unknown");
    
    private static final HashMap<PeonyFontFileName, String> paramDescriptions = new HashMap<>();
    static{
        paramDescriptions.put(UNKNOWN, "");
        paramDescriptions.put(MingLiU, "MingLiU");
        paramDescriptions.put(MSJH, "MicrosoftJhengHeiRegular");
        paramDescriptions.put(MSYH, "MicrosoftYaHei");
        paramDescriptions.put(SimSun, "SimSun");
    }

    private static HashMap<PeonyFontFileName, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static List<String> getParamDescriptionList(){
        return new ArrayList<>(getParamDescriptions().values());
    }
    
    public static String getParamDescription(PeonyFontFileName name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<PeonyFontFileName, String> paramValues = new HashMap<>();

    private synchronized static HashMap<PeonyFontFileName, String> getParamValues(){
        if (paramValues.isEmpty()){
            PeonyFontFileName[] valueArray = PeonyFontFileName.values();
            if (valueArray != null){
                for (PeonyFontFileName valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(PeonyFontFileName name){
        return getParamValues().get(name);
    }
    
    public static PeonyFontFileName convertParamValueToType(String webParamValue){
        HashMap<PeonyFontFileName, String> localParamValues = getParamValues();
        Set<PeonyFontFileName> keys = localParamValues.keySet();
        Iterator<PeonyFontFileName> itr = keys.iterator();
        PeonyFontFileName result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<PeonyFontFileName> getPeonyFontFileNameList(boolean includeUnknownValue) {
        List<PeonyFontFileName> result = new ArrayList<>();
        PeonyFontFileName[] valueArray = PeonyFontFileName.values();
        if (valueArray != null){
            for (PeonyFontFileName valueObj : valueArray){
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
        PeonyFontFileName[] valueArray = PeonyFontFileName.values();
        if (valueArray != null){
            for (PeonyFontFileName valueObj : valueArray){
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
        PeonyFontFileName[] valueArray = PeonyFontFileName.values();
        if (valueArray != null){
            for (PeonyFontFileName valueObj : valueArray){
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
    public static PeonyFontFileName convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static PeonyFontFileName convertEnumValueToType(String value, boolean allowNullReturned){
        PeonyFontFileName result = null;
        if (value != null){
            PeonyFontFileName[] valueArray = PeonyFontFileName.values();
            for (PeonyFontFileName valueObj : valueArray){
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
    public static PeonyFontFileName convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static PeonyFontFileName convertEnumNameToType(String name, boolean allowNullReturned){
        PeonyFontFileName result = null;
        if (name != null){
            PeonyFontFileName[] valueArray = PeonyFontFileName.values();
            for (PeonyFontFileName valueObj : valueArray){
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
    public static PeonyFontFileName convertStringToType(String name){
        PeonyFontFileName result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public static PeonyFontFileName convertIndexToType(Integer enumIndex) {
        return convertIndexToType(enumIndex, false);
    }

    public static PeonyFontFileName convertIndexToType(Integer enumIndex, boolean allowNullReturned) {
        try{
            return PeonyFontFileName.values()[enumIndex];
        }catch (Exception ex){
            if (allowNullReturned){
                return null;
            }else{
                return UNKNOWN;
            }
        }
    }

    private final String value;
    PeonyFontFileName(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
