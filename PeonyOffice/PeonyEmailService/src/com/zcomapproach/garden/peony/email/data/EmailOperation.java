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

package com.zcomapproach.garden.peony.email.data;

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
public enum EmailOperation {

    CREATE_FOLDER("Create Folder"),
    MOVE_TO_FOLDER("Move to Folder"),
    DELETE_FOLDER("Delete Folder"),
    ASSIGN_SELECTED_EMAILS("Assign Selected Email(s)"),
    NOTES("Notes on email"),
    VIEW_SELECTED_EMAILS("View Selected Email(s)"),
    SORT_EMAIL("Sort Emails"),
    DELETE_SELECTED_EMAILS("Delete Selected Email(s)"),
    RECONNECT_EMAILBOX("Reconnect Emailbox"),
    UNKNOWN("Unknown"); //special usage
    
    private static final HashMap<EmailOperation, String> paramDescriptions = new HashMap<>();
    static{
        //todo: add description for params
        paramDescriptions.put(UNKNOWN, "");
    }

    private static HashMap<EmailOperation, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static List<String> getParamDescriptionList(){
        return new ArrayList<>(getParamDescriptions().values());
    }
    
    public static String getParamDescription(EmailOperation name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<EmailOperation, String> paramValues = new HashMap<>();

    private synchronized static HashMap<EmailOperation, String> getParamValues(){
        if (paramValues.isEmpty()){
            EmailOperation[] valueArray = EmailOperation.values();
            if (valueArray != null){
                for (EmailOperation valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(EmailOperation name){
        return getParamValues().get(name);
    }
    
    public static EmailOperation convertParamValueToType(String webParamValue){
        HashMap<EmailOperation, String> localParamValues = getParamValues();
        Set<EmailOperation> keys = localParamValues.keySet();
        Iterator<EmailOperation> itr = keys.iterator();
        EmailOperation result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<EmailOperation> getEmailOperationList(boolean includeUnknownValue) {
        List<EmailOperation> result = new ArrayList<>();
        EmailOperation[] valueArray = EmailOperation.values();
        if (valueArray != null){
            for (EmailOperation valueObj : valueArray){
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
        EmailOperation[] valueArray = EmailOperation.values();
        if (valueArray != null){
            for (EmailOperation valueObj : valueArray){
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
        EmailOperation[] valueArray = EmailOperation.values();
        if (valueArray != null){
            for (EmailOperation valueObj : valueArray){
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
    public static EmailOperation convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static EmailOperation convertEnumValueToType(String value, boolean allowNullReturned){
        EmailOperation result = null;
        if (value != null){
            EmailOperation[] valueArray = EmailOperation.values();
            for (EmailOperation valueObj : valueArray){
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
    public static EmailOperation convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static EmailOperation convertEnumNameToType(String name, boolean allowNullReturned){
        EmailOperation result = null;
        if (name != null){
            EmailOperation[] valueArray = EmailOperation.values();
            for (EmailOperation valueObj : valueArray){
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
    public static EmailOperation convertStringToType(String name){
        EmailOperation result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public static EmailOperation convertIndexToType(Integer enumIndex) {
        return convertIndexToType(enumIndex, false);
    }

    public static EmailOperation convertIndexToType(Integer enumIndex, boolean allowNullReturned) {
        try{
            return EmailOperation.values()[enumIndex];
        }catch (Exception ex){
            if (allowNullReturned){
                return null;
            }else{
                return UNKNOWN;
            }
        }
    }

    private final String value;
    EmailOperation(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
