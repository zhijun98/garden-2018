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

package com.zcomapproach.garden.rose.test;

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
public enum RoseTestSettings {
    
    ROSE_WEB_HOMEPAGE("http://localhost:8080/RoseWeb"),
    
    CHROME_DRIVER("C:\\WebDrivers\\chromedriver.exe"),
    FIREFOX_DRIVER("C:\\WebDrivers\\geckodriver.exe"),
    MS_EDGE_DRIVER("C:\\WebDrivers\\MicrosoftWebDriver.exe"),
    MS_IE_DRIVER("C:\\WebDrivers\\IEDriverServer.exe"),
    UNKNOWN("Unknown"); //special usage//special usage
    
    public static void applySystemSettingsForSupportedWebDrivers(){
        applySystemSettingsForEdge();
        applySystemSettingsForIE();
        applySystemSettingsForFireFox();
        applySystemSettingsForChrome();
    }
    
    public static void applySystemSettingsForEdge(){
        System.setProperty("webdriver.edge.driver", MS_EDGE_DRIVER.value());
    }
    
    public static void applySystemSettingsForIE(){
        System.setProperty("webdriver.ie.driver", MS_IE_DRIVER.value());
    }
    
    public static void applySystemSettingsForFireFox(){
        System.setProperty("webdriver.gecko.driver", FIREFOX_DRIVER.value());
    }
    
    public static void applySystemSettingsForChrome(){
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER.value());
    }
    
    private static final HashMap<RoseTestSettings, String> paramDescriptions = new HashMap<>();
    static{
        paramDescriptions.put(UNKNOWN, "");
    }

    private static HashMap<RoseTestSettings, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static String getParamDescription(RoseTestSettings name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<RoseTestSettings, String> paramValues = new HashMap<>();

    private synchronized static HashMap<RoseTestSettings, String> getParamValues(){
        if (paramValues.isEmpty()){
            RoseTestSettings[] valueArray = RoseTestSettings.values();
            if (valueArray != null){
                for (RoseTestSettings valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(RoseTestSettings name){
        return getParamValues().get(name);
    }
    
    public static RoseTestSettings convertParamValueToType(String webParamValue){
        HashMap<RoseTestSettings, String> localParamValues = getParamValues();
        Set<RoseTestSettings> keys = localParamValues.keySet();
        Iterator<RoseTestSettings> itr = keys.iterator();
        RoseTestSettings result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<RoseTestSettings> getRoseWebTestSettingsList(boolean includeUnknownValue) {
        List<RoseTestSettings> result = new ArrayList<>();
        RoseTestSettings[] valueArray = RoseTestSettings.values();
        if (valueArray != null){
            for (RoseTestSettings valueObj : valueArray){
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
        RoseTestSettings[] valueArray = RoseTestSettings.values();
        if (valueArray != null){
            for (RoseTestSettings valueObj : valueArray){
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
        RoseTestSettings[] valueArray = RoseTestSettings.values();
        if (valueArray != null){
            for (RoseTestSettings valueObj : valueArray){
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
    public static RoseTestSettings convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static RoseTestSettings convertEnumValueToType(String value, boolean allowNullReturned){
        RoseTestSettings result = null;
        if (value != null){
            RoseTestSettings[] valueArray = RoseTestSettings.values();
            for (RoseTestSettings valueObj : valueArray){
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
    public static RoseTestSettings convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static RoseTestSettings convertEnumNameToType(String name, boolean allowNullReturned){
        RoseTestSettings result = null;
        if (name != null){
            RoseTestSettings[] valueArray = RoseTestSettings.values();
            for (RoseTestSettings valueObj : valueArray){
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
    public static RoseTestSettings convertStringToType(String name){
        RoseTestSettings result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public static RoseTestSettings convertIndexToType(Integer enumIndex) {
        return convertIndexToType(enumIndex, false);
    }

    public static RoseTestSettings convertIndexToType(Integer enumIndex, boolean allowNullReturned) {
        try{
            return RoseTestSettings.values()[enumIndex];
        }catch (Exception ex){
            if (allowNullReturned){
                return null;
            }else{
                return UNKNOWN;
            }
        }
    }

    private final String value;
    RoseTestSettings(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
