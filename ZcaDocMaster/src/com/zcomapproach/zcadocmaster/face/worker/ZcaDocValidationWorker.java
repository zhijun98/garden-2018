/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zcomapproach.zcadocmaster.face.worker;

import com.zcomapproach.zcadocmaster.events.ZcaDocMessageEvent;
import com.zcomapproach.zcadocmaster.events.ZcaDocValidationWorkerEvent;
import com.zcomapproach.zcaglobals.commons.ZCalendarGlobal;
import com.zcomapproach.zcaglobals.commons.ZPlatformGlobal;
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
 * @author Zhijun Zhang, date & time: Mar 19, 2014 - 6:14:24 PM
 */
class ZcaDocValidationWorker extends ZcaDocSwingWorker implements IZcaDocValidationWorker {
    public ZcaDocValidationWorker(List<ZcaDocTask> aZcaDocTaskList, IZcaDocSwingWorkerListener lisener) {
        super(aZcaDocTaskList, lisener);
    }

    @Override
    NioPathTreeSummary proccessZcaDocTaskInbackground(ZcaDocTask aZcaDocTask) {
        publish("- Process validation of " + aZcaDocTask.getSourcePath());
        NioPathTreeSummary aNioPathTreeSummary;
        try{
            aNioPathTreeSummary = ZNioUtilities.validateFolderRecursively(new File(aZcaDocTask.getSourcePath()), 
                                                                          getNioPathTreeListenerList());
            aNioPathTreeSummary.setOperationSuccessful(true);
            aNioPathTreeSummary.setOperationMessage("- Completed validation task.");
            return aNioPathTreeSummary;
        }catch (Exception ex){
            Logger.getLogger(ZcaDocBackupWorker.class.getName()).log(Level.SEVERE, null, ex);
            aNioPathTreeSummary = new NioPathTreeSummary();
            aNioPathTreeSummary.setOperationSuccessful(false);
            aNioPathTreeSummary.setOperationMessage("ZNioUtilities.validateFolderRecursively raised exceptions: " + ex.getMessage());
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
        } catch (ExecutionException ex) {
            Logger.getLogger(ZcaDocBackupWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
        fireSwingWorkerDoneEvent(new ZcaDocValidationWorkerEvent("The entire backup task list has been completed."));
    }

}
