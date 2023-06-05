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

package com.zcomapproach.garden.peony.security;

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
public enum PeonyPrivilege {
    CENTRAL_ADMIN("Central Admin"),
    BUSINESS_CENTER("Business Center"),
    MANAGE_EMPLOYEE_PROFILES("Manage Employee Profiles"),
    MANAGE_EMPLOYEE_PRIVILEGES("Manage Employee Privileges"),
    VIEW_EMPLOYEE_WORK_LOGS("View Employee Work Logs"),
    CONFIRM_PAYMENT_DEPOSIT("Confirm Payment Deposit"),
    TAX_FILING_GENERATE("Generate Tax Filing"),
    TAX_FILING_DELETE("Delete Tax Filing"),
    TAX_FILING_ERASE_ALL("Erase All Tax Filing"),
    TAX_DEADLINE_UPDATE("Tax Deadline Update"),
    TAX_EXTENSION_UPDATE("Tax Extension Update"),
    FINALIZE_TAXPAYER("Finalize Taxpayer"),
    ROLLBACK_TAXPAYER("Rollback Taxpayer"),
    FINALIZE_TAXCORP("Finalize Taxcorp"),
    ROLLBACK_TAXCORP("Rollback Taxcorp"),
    ASSIGN_TAXPAYER_JOB("Assign Taxpayer Job"),
    ASSIGN_TAXCORP_JOB("Assign Taxcorp Job"),
    CONTROL_DAILY_REPORT("Daily Report Control"),
    UNKNOWN("Unknown"); //special usage

    private static final HashMap<PeonyPrivilege, PeonyPrivilegeGroup> paramGroups = new HashMap<>();
    static{
        paramGroups.put(BUSINESS_CENTER, PeonyPrivilegeGroup.SUPER_POWER);
        paramGroups.put(CONTROL_DAILY_REPORT, PeonyPrivilegeGroup.SUPER_POWER);
        paramGroups.put(MANAGE_EMPLOYEE_PRIVILEGES, PeonyPrivilegeGroup.SUPER_POWER);
        paramGroups.put(CENTRAL_ADMIN, PeonyPrivilegeGroup.MANAGEMENT);
        paramGroups.put(MANAGE_EMPLOYEE_PROFILES, PeonyPrivilegeGroup.MANAGEMENT);
        paramGroups.put(VIEW_EMPLOYEE_WORK_LOGS, PeonyPrivilegeGroup.MANAGEMENT);
        paramGroups.put(CONFIRM_PAYMENT_DEPOSIT, PeonyPrivilegeGroup.BUSINESS);
        paramGroups.put(TAX_DEADLINE_UPDATE, PeonyPrivilegeGroup.BUSINESS);
        paramGroups.put(TAX_EXTENSION_UPDATE, PeonyPrivilegeGroup.BUSINESS);
        paramGroups.put(FINALIZE_TAXPAYER, PeonyPrivilegeGroup.TAXPAYER);
        paramGroups.put(ROLLBACK_TAXPAYER, PeonyPrivilegeGroup.TAXPAYER);
        paramGroups.put(ASSIGN_TAXPAYER_JOB, PeonyPrivilegeGroup.TAXPAYER);
        paramGroups.put(ASSIGN_TAXCORP_JOB, PeonyPrivilegeGroup.TAXCORP);
        paramGroups.put(FINALIZE_TAXCORP, PeonyPrivilegeGroup.TAXCORP);
        paramGroups.put(ROLLBACK_TAXCORP, PeonyPrivilegeGroup.TAXCORP);
        paramGroups.put(TAX_FILING_GENERATE, PeonyPrivilegeGroup.TAXCORP);
        paramGroups.put(TAX_FILING_DELETE, PeonyPrivilegeGroup.TAXCORP);
        paramGroups.put(TAX_FILING_ERASE_ALL, PeonyPrivilegeGroup.TAXCORP);
    }

    private static HashMap<PeonyPrivilege, PeonyPrivilegeGroup> getParamGroups(){
        return paramGroups;
    }

    public static HashMap<PeonyPrivilegeGroup, List<PeonyPrivilege>> getGroupedPrivileges(){
        HashMap<PeonyPrivilegeGroup, List<PeonyPrivilege>> result = new HashMap<>();
        Set<PeonyPrivilege> keys = paramGroups.keySet();
        Iterator<PeonyPrivilege> itr = keys.iterator();
        PeonyPrivilegeGroup group;
        PeonyPrivilege privilege;
        List<PeonyPrivilege> privilegeList;
        while(itr.hasNext()){
            privilege = itr.next();
            group = paramGroups.get(privilege);
            privilegeList = result.get(group);
            if (privilegeList == null){
                privilegeList = new ArrayList<>();
                result.put(group, privilegeList);
            }
            privilegeList.add(privilege);
        }
        return result;
    }
    
    public static List<PeonyPrivilegeGroup> getParamGroupList(){
        return new ArrayList<>(getParamGroups().values());
    }
    
    public static PeonyPrivilegeGroup getParamGroup(PeonyPrivilege name){
        return getParamGroups().get(name);
    }

    private static final HashMap<PeonyPrivilege, String> paramDescriptions = new HashMap<>();
    static{
        paramDescriptions.put(CENTRAL_ADMIN, "Access to central admin window so as to execute admin-operations for Peony office");
        paramDescriptions.put(BUSINESS_CENTER, "Access to business center so as to monitor the business status of the accounting "
                + "firm. Only executive senior managers can be authorized.");
        paramDescriptions.put(MANAGE_EMPLOYEE_PROFILES, "Manage employee profiles: CRUD on all the employee profiles.");
        paramDescriptions.put(MANAGE_EMPLOYEE_PRIVILEGES, "Manage employee privileges: CRUD on privileges for every "
                + "employee profile. PREREQUISITE: Manage Employee Profiles");
        paramDescriptions.put(VIEW_EMPLOYEE_WORK_LOGS, "View any employee's daily work logs");
        paramDescriptions.put(CONFIRM_PAYMENT_DEPOSIT, "Only executive senior managers are authorized to confirm whether "
                + "or not the payment's deposit has been completed.");
        paramDescriptions.put(TAX_FILING_GENERATE, "Only executive senior managers are authorized to generate tax filing records for taxcorps.");
        paramDescriptions.put(TAX_FILING_DELETE, "Only executive senior managers are authorized to delete tax filing records for taxcorps. But any record which is before today will NOT be deleted.");
        paramDescriptions.put(TAX_FILING_ERASE_ALL, "This deletion is dangerous. It authorizes users to erase all the relevant records of tax "
                + "filing in the database even if they are historical records, possibly associated memo records");
        paramDescriptions.put(TAX_DEADLINE_UPDATE, "Updating tax deadline is not usual. This privilege is usualy for rare technical situations");
        paramDescriptions.put(TAX_EXTENSION_UPDATE, "Updating tax extension date demands to be authorized.");
        paramDescriptions.put(FINALIZE_TAXPAYER, "Finalizing Taxpayer demands to be authorized.");
        paramDescriptions.put(FINALIZE_TAXCORP, "Finalizing Taxcorp demands to be authorized.");
        paramDescriptions.put(ROLLBACK_TAXPAYER, "Rolling-back Taxpayer demands to be authorized.");
        paramDescriptions.put(ROLLBACK_TAXCORP, "Rolling-back Taxcorp demands to be authorized.");
        paramDescriptions.put(ASSIGN_TAXPAYER_JOB, "Assign jobs for taxpayer cases.");
        paramDescriptions.put(ASSIGN_TAXCORP_JOB, "Assign jobs for taxcorp cases.");
        paramDescriptions.put(CONTROL_DAILY_REPORT, "Fully control daily job reports.");
        
        paramDescriptions.put(UNKNOWN, "");
    }

    private static HashMap<PeonyPrivilege, String> getParamDescriptions(){
        return paramDescriptions;
    }
    
    public static List<String> getParamDescriptionList(){
        return new ArrayList<>(getParamDescriptions().values());
    }
    
    public static String getParamDescription(PeonyPrivilege name){
        return getParamDescriptions().get(name);
    }
    
    private static final HashMap<PeonyPrivilege, String> paramValues = new HashMap<>();

    private synchronized static HashMap<PeonyPrivilege, String> getParamValues(){
        if (paramValues.isEmpty()){
            PeonyPrivilege[] valueArray = PeonyPrivilege.values();
            if (valueArray != null){
                for (PeonyPrivilege valueObj : valueArray){
                    paramValues.put(valueObj, UUID.randomUUID().toString());
                }//for
            }//if
        }
        return paramValues;
    }
    
    public static String convertTypeToParamValue(PeonyPrivilege name){
        return getParamValues().get(name);
    }
    
    public static PeonyPrivilege convertParamValueToType(String webParamValue){
        HashMap<PeonyPrivilege, String> localParamValues = getParamValues();
        Set<PeonyPrivilege> keys = localParamValues.keySet();
        Iterator<PeonyPrivilege> itr = keys.iterator();
        PeonyPrivilege result;
        while(itr.hasNext()){
            result = itr.next();
            if (localParamValues.get(result).equalsIgnoreCase(webParamValue)){
                return result;
            }
        }
        return UNKNOWN;
    }

    public static List<PeonyPrivilege> getPeonyPrivilegeList(boolean includeUnknownValue) {
        List<PeonyPrivilege> result = new ArrayList<>();
        PeonyPrivilege[] valueArray = PeonyPrivilege.values();
        if (valueArray != null){
            for (PeonyPrivilege valueObj : valueArray){
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
        PeonyPrivilege[] valueArray = PeonyPrivilege.values();
        if (valueArray != null){
            for (PeonyPrivilege valueObj : valueArray){
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
        PeonyPrivilege[] valueArray = PeonyPrivilege.values();
        if (valueArray != null){
            for (PeonyPrivilege valueObj : valueArray){
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
    public static PeonyPrivilege convertEnumValueToType(String value){
        return convertEnumValueToType(value, false);
    }

    public static PeonyPrivilege convertEnumValueToType(String value, boolean allowNullReturned){
        PeonyPrivilege result = null;
        if (value != null){
            PeonyPrivilege[] valueArray = PeonyPrivilege.values();
            for (PeonyPrivilege valueObj : valueArray){
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
    public static PeonyPrivilege convertEnumNameToType(String name){
        return convertEnumNameToType(name, false);
    }

    public static PeonyPrivilege convertEnumNameToType(String name, boolean allowNullReturned){
        PeonyPrivilege result = null;
        if (name != null){
            PeonyPrivilege[] valueArray = PeonyPrivilege.values();
            for (PeonyPrivilege valueObj : valueArray){
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
    public static PeonyPrivilege convertStringToType(String name){
        PeonyPrivilege result = convertEnumNameToType(name);
        if (result.equals(UNKNOWN)){
            result = convertEnumValueToType(name);
        }
        return result;
    }

    public static PeonyPrivilege convertIndexToType(Integer enumIndex) {
        return convertIndexToType(enumIndex, false);
    }

    public static PeonyPrivilege convertIndexToType(Integer enumIndex, boolean allowNullReturned) {
        try{
            return PeonyPrivilege.values()[enumIndex];
        }catch (Exception ex){
            if (allowNullReturned){
                return null;
            }else{
                return UNKNOWN;
            }
        }
    }

    private final String value;
    PeonyPrivilege(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
