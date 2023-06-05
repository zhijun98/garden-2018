/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zcomapproach.garden.peony.email.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author yinlu
 */
public enum PeonyEmailMessageType {

    ALL("All"),
    UNREAD("Unread"),
    READ("Read"),
    ASSIGNED("Assigned"),
    NOTED("Noted"),
    ATTACHED("Attached"),
    UNKNOWN("Unknown"); //special usage
    
    private static final HashMap<PeonyEmailMessageType, String> paramDescriptions = new HashMap<>();
    static{
        //todo: add description for params
        paramDescriptions.put(UNKNOWN, "");
    }

    private static HashMap<PeonyEmailMessageType, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static List<String> getParamDescriptionList(){
        return new ArrayList<>(getParamDescriptions().values());
    }
    
    public static String getParamDescription(PeonyEmailMessageType name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<PeonyEmailMessageType, String> paramValues = new HashMap<>();

    private synchronized static HashMap<PeonyEmailMessageType, String> getParamValues(){
        if (paramValues.isEmpty()){
            PeonyEmailMessageType[] valueArray = PeonyEmailMessageType.values();
            if (valueArray != null){
                for (PeonyEmailMessageType valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(PeonyEmailMessageType name){
        return getParamValues().get(name);
    }
    
    public static PeonyEmailMessageType convertParamValueToType(String webParamValue){
        HashMap<PeonyEmailMessageType, String> localParamValues = getParamValues();
        Set<PeonyEmailMessageType> keys = localParamValues.keySet();
        Iterator<PeonyEmailMessageType> itr = keys.iterator();
        PeonyEmailMessageType result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<PeonyEmailMessageType> getPeonyEmailMessageTypeList(boolean includeUnknownValue) {
        List<PeonyEmailMessageType> result = new ArrayList<>();
        PeonyEmailMessageType[] valueArray = PeonyEmailMessageType.values();
        if (valueArray != null){
            for (PeonyEmailMessageType valueObj : valueArray){
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
        PeonyEmailMessageType[] valueArray = PeonyEmailMessageType.values();
        if (valueArray != null){
            for (PeonyEmailMessageType valueObj : valueArray){
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
        PeonyEmailMessageType[] valueArray = PeonyEmailMessageType.values();
        if (valueArray != null){
            for (PeonyEmailMessageType valueObj : valueArray){
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
    public static PeonyEmailMessageType convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static PeonyEmailMessageType convertEnumValueToType(String value, boolean allowNullReturned){
        PeonyEmailMessageType result = null;
        if (value != null){
            PeonyEmailMessageType[] valueArray = PeonyEmailMessageType.values();
            for (PeonyEmailMessageType valueObj : valueArray){
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
    public static PeonyEmailMessageType convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static PeonyEmailMessageType convertEnumNameToType(String name, boolean allowNullReturned){
        PeonyEmailMessageType result = null;
        if (name != null){
            PeonyEmailMessageType[] valueArray = PeonyEmailMessageType.values();
            for (PeonyEmailMessageType valueObj : valueArray){
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
    public static PeonyEmailMessageType convertStringToType(String name){
        PeonyEmailMessageType result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public static PeonyEmailMessageType convertIndexToType(Integer enumIndex) {
        return convertIndexToType(enumIndex, false);
    }

    public static PeonyEmailMessageType convertIndexToType(Integer enumIndex, boolean allowNullReturned) {
        try{
            return PeonyEmailMessageType.values()[enumIndex];
        }catch (Exception ex){
            if (allowNullReturned){
                return null;
            }else{
                return UNKNOWN;
            }
        }
    }

    private final String value;
    PeonyEmailMessageType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
