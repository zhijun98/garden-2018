/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zcomapproach.zcadocmaster;

import com.zcomapproach.zcadocmaster.face.worker.ZcaDocTask;
import com.zcomapproach.zcaglobals.commons.ZDataGlobal;
import com.zcomapproach.zcaglobals.ZProperties;
import com.zcomapproach.zcaglobals.commons.data.ZCommonTerm;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Zhijun Zhang, date & time: Mar 16, 2014 - 8:28:58 PM
 */
public class ZcaDocFrameProperties extends ZProperties{
    
    private final String delimiter01 = "<>";
    private final String delimiter02 = ",";

    public ZcaDocFrameProperties(String propFileName) {
        super(propFileName);
    }

    public void removeZcaDocTaskList(int lastIndexOfPersistentTask) {
        for (int i = 0; i < lastIndexOfPersistentTask; i++){
            removeProperties(Integer.toString(i));
        }
    }

    public void saveZcaDocTaskList(List<ZcaDocTask> aZcaDocTaskList) {
        if (aZcaDocTaskList == null){
            return;
        }
        ZcaDocTask aZcaDocTask;
        for (int i = 0; i < aZcaDocTaskList.size(); i++){
            aZcaDocTask = aZcaDocTaskList.get(i);
            super.setProperty(Integer.toString(i), aZcaDocTask.getSourcePath() 
                    + delimiter01 + aZcaDocTask.getBackupPath()
                    + delimiter01 + serializeDayOfWeekList(aZcaDocTask.getDayOfWeekList())
                    + delimiter01 + aZcaDocTask.getHourOfDay()
                    + delimiter01 + aZcaDocTask.getMinute()
                    + delimiter01 + aZcaDocTask.getSecond()
                    + delimiter01 + ((aZcaDocTask.isBackupPathSuffixRequired()) ? ZCommonTerm.YES.toString() : ZCommonTerm.NO.toString())
                    + delimiter01 + ((aZcaDocTask.isOverwriteRequired()) ? ZCommonTerm.YES.toString() : ZCommonTerm.NO.toString()));
        }
    }

    public ZcaDocTask getZcaDocTask(int index) {
        String value = super.getProperty(Integer.toString(index));
        if (ZDataGlobal.isEmptyNullString(value)){
            return null;
        }else{
            String[] folderPathes = value.split(delimiter01);
            if ((folderPathes == null) || (folderPathes.length != 8)) {
                return null;
            }else{
                ZcaDocTask aZcaDocTask = new ZcaDocTask();
                aZcaDocTask.setSourcePath(folderPathes[0]);
                aZcaDocTask.setBackupPath(folderPathes[1]);
                deserializeDayOfWeekList(aZcaDocTask, folderPathes[2]);
                aZcaDocTask.setHourOfDay(ZDataGlobal.convertToInteger(folderPathes[3]));
                aZcaDocTask.setMinute(ZDataGlobal.convertToInteger(folderPathes[4]));
                aZcaDocTask.setSecond(ZDataGlobal.convertToInteger(folderPathes[5]));
                if (ZCommonTerm.YES.toString().equalsIgnoreCase(folderPathes[6])){
                    aZcaDocTask.setBackupPathSuffixRequired(true);
                }else{
                    aZcaDocTask.setBackupPathSuffixRequired(false);
                }
                if (ZCommonTerm.YES.toString().equalsIgnoreCase(folderPathes[7])){
                    aZcaDocTask.setOverwriteRequired(true);
                }else{
                    aZcaDocTask.setOverwriteRequired(false);
                }
                return aZcaDocTask;
            }
        }
    }

    private String serializeDayOfWeekList(ArrayList<Integer> dayOfWeekList) {
        if (dayOfWeekList.isEmpty()){
            return "";
        }else{
            String result = ""+ dayOfWeekList.get(0);
            for (int i = 1; i < dayOfWeekList.size(); i++){
                result = result + delimiter02 + dayOfWeekList.get(i);
            }
            return result;
        }
    }

    private void deserializeDayOfWeekList(ZcaDocTask aZcaDocTask, String valueList) {
        if (ZDataGlobal.isEmptyNullString(valueList)){
            return;
        }
        String[] valueArray = valueList.split(delimiter02);
        for (String value : valueArray){
            if (ZDataGlobal.isNonEmptyNullString(value)){
                aZcaDocTask.getDayOfWeekList().add(ZDataGlobal.convertToInteger(value));
            }
        }
    }

}
