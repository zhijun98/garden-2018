/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zcomapproach.zcadocmaster.face.worker;

import com.zcomapproach.zcaglobals.commons.ZCalendarGlobal;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Thread-safe
 * @author Zhijun Zhang
 */
public class ZcaDocTask {
    private String sourcePath;
    private String backupPath;
    private boolean backupPathSuffixRequired;
    
    private final ArrayList<Integer> dayOfWeekList = new ArrayList<Integer>();
    
    private int hourOfDay;
    private int minute;
    private int second;
    
    private boolean overwriteRequired = true;
    
    private GregorianCalendar duedTimePoint = null;
    
    public synchronized ArrayList<Integer> getDayOfWeekList() {
        return dayOfWeekList;
    }

    public synchronized boolean isOverwriteRequired() {
        return overwriteRequired;
    }

    public synchronized void setOverwriteRequired(boolean overwriteRequired) {
        this.overwriteRequired = overwriteRequired;
    }

    public synchronized int getHourOfDay() {
        return hourOfDay;
    }

    public synchronized void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public synchronized int getMinute() {
        return minute;
    }

    public synchronized void setMinute(int minute) {
        this.minute = minute;
    }

    public synchronized int getSecond() {
        return second;
    }

    public synchronized void setSecond(int second) {
        this.second = second;
    }

    public synchronized String getSourcePath() {
        return sourcePath;
    }

    public synchronized void setSourcePath(String targetPath) {
        this.sourcePath = targetPath;
    }

    public synchronized String getBackupPath() {
        return backupPath;
    }

    public synchronized void setBackupPath(String destinationPath) {
        this.backupPath = destinationPath;
    }

    public boolean isBackupPathSuffixRequired() {
        return backupPathSuffixRequired;
    }

    public void setBackupPathSuffixRequired(boolean backupPathSuffixRequired) {
        this.backupPathSuffixRequired = backupPathSuffixRequired;
    }

    @Override
    public synchronized String toString() {
        return "Source from: " + sourcePath;
    }

    /**
     * The nearest time-point for running this task according to the scheduled time-point
     * @return - it could be NULL if this task was not scheduled for any day-of-week
     */
    public synchronized GregorianCalendar getDueTimePoint() {
        if (duedTimePoint == null){
            setNextDueTimePoint();
        }
        return duedTimePoint;
    }

    public synchronized void setNextDueTimePoint() {
        if ((getDayOfWeekList() == null) || (getDayOfWeekList().isEmpty())){
            duedTimePoint = null;
            return;
        }
        duedTimePoint = ZCalendarGlobal.getNextDueTimePoint(getDayOfWeekList(), getHourOfDay(), getMinute(), getSecond());
    }

    public String getScheduledDayOfWeekDescription() {
        String result = "";
        ArrayList<Integer> days = getDayOfWeekList();
            for (Integer day : days){
                result += ZCalendarGlobal.convertToCalendarDayOfWeek(day) + "; ";
            }
        return result;
    }
}
