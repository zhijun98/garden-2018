/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zcomapproach.zcadocmaster.face.worker;

import com.zcomapproach.zcadocmaster.events.ZcaDocBackupWorkerEvent;
import com.zcomapproach.zcaglobals.commons.ZCalendarGlobal;
import com.zcomapproach.zcaglobals.commons.ZPlatformGlobal;
import com.zcomapproach.zcaglobals.debug.ZLogFileHandler;
import com.zcomapproach.zcaglobals.nio.NioPathTreeSummary;
import com.zcomapproach.zcaglobals.nio.ZNioUtilities;
import java.io.File;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zhijun Zhang
 */
class ZcaDocBackupWorker extends ZcaDocSwingWorker implements IZcaDocBackupWorker {
    
    public ZcaDocBackupWorker(List<ZcaDocTask> aZcaDocTaskList, IZcaDocSwingWorkerListener lisener) {
        super(aZcaDocTaskList, lisener);
        super.setLoggingMessageRequired(true);
    }

    @Override
    NioPathTreeSummary proccessZcaDocTaskInbackground(ZcaDocTask aZcaDocTask) {
        
        prepareZcaDocTaskBeforeBackup(aZcaDocTask);
        
        String backupPathString = aZcaDocTask.getBackupPath();
        if (aZcaDocTask.isBackupPathSuffixRequired()){
            backupPathString = backupPathString + "_" + ZCalendarGlobal.getCurrentMMddyyyyHHmmss();
        }
        
        publish("- Copy & paste everything into " 
                + backupPathString);
        NioPathTreeSummary aNioPathTreeSummary;
        try{
            /**
             * Execute BACKUP....
             */
            aNioPathTreeSummary = ZNioUtilities.backupFolderRecursively(new File(aZcaDocTask.getSourcePath()), 
                                                new File(backupPathString), aZcaDocTask.isOverwriteRequired(),
                                                true, true, true, getNioPathTreeListenerList());
            aNioPathTreeSummary.setOperationSuccessful(true);
            aNioPathTreeSummary.setOperationMessage("- Completed backup task.");
            
            /**
             * Update next time-point for execution
             */
            aZcaDocTask.setNextDueTimePoint();
            
            return aNioPathTreeSummary;
        }catch (Exception ex){
            Logger.getLogger(ZcaDocBackupWorker.class.getName()).log(Level.SEVERE, null, ex);
            aNioPathTreeSummary = new NioPathTreeSummary();
            aNioPathTreeSummary.setOperationSuccessful(false);
            aNioPathTreeSummary.setOperationMessage("ZNioUtilities.backupFolderRecursively raised exceptions: " + ex.getMessage());
            return aNioPathTreeSummary;
        }
    }

    @Override
    protected void done() {
        try {
            String result = get();
            processMessage(ZCalendarGlobal.getDataTimeString(new GregorianCalendar()) + ": "
                    + result + ZPlatformGlobal.lineSeparator() + ZPlatformGlobal.lineSeparator());
        } catch (InterruptedException ex) {
            Logger.getLogger(ZcaDocBackupWorker.class.getName()).log(Level.SEVERE, null, ex);
            ZLogFileHandler.getSingleton().publish(ZLogFileHandler.createSevereLogRecord(ex));
        } catch (ExecutionException ex) {
            Logger.getLogger(ZcaDocBackupWorker.class.getName()).log(Level.SEVERE, null, ex);
            ZLogFileHandler.getSingleton().publish(ZLogFileHandler.createSevereLogRecord(ex));
        }
        fireSwingWorkerDoneEvent(new ZcaDocBackupWorkerEvent("The entire backup task list has been completed."));
    }

    /**
     * 
     * @param aZcaDocTask 
     */
    private void prepareZcaDocTaskBeforeBackup(ZcaDocTask aZcaDocTask) {
        
    }
    
}
