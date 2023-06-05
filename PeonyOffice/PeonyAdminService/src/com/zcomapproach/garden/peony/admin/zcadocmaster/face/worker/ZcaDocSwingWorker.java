/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zcomapproach.garden.peony.admin.zcadocmaster.face.worker;

import com.zcomapproach.garden.peony.admin.zcadocmaster.events.IZcaDocMessageEvent;
import com.zcomapproach.garden.peony.admin.zcadocmaster.events.IZcaDocSwingWorkerEvent;
import com.zcomapproach.garden.peony.admin.zcadocmaster.events.ZcaDocMessageEvent;
import com.zcomapproach.garden.util.nio.INioPathTreeEventListener;
import com.zcomapproach.garden.util.nio.NioPathTreeSummary;
import com.zcomapproach.garden.util.nio.events.INioPathTreeEvent;
import com.zcomapproach.zcaglobals.commons.ZCalendarGlobal;
import java.io.File;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.SwingWorker;

/**
 *
 * @author Zhijun Zhang, date & time: Mar 19, 2014 - 6:16:47 PM
 */
abstract class ZcaDocSwingWorker extends SwingWorker<String, Object> implements IZcaDocSwingWorker, INioPathTreeEventListener{
    
    private final ArrayList<INioPathTreeEventListener> nioPathTreeListenerList = new ArrayList<INioPathTreeEventListener>();
    private final ArrayList<IZcaDocSwingWorkerListener> swingWorkerListenerList = new ArrayList<IZcaDocSwingWorkerListener>();

    private final List<ZcaDocTask> aZcaDocTaskList;
    
    private boolean loggingMessageRequired = false;
    
    private boolean cancelingWorker = false;

    public ZcaDocSwingWorker(List<ZcaDocTask> aZcaDocTaskList, IZcaDocSwingWorkerListener lisener) {
        this.aZcaDocTaskList = aZcaDocTaskList;
        nioPathTreeListenerList.add(this);
        if (lisener != null){
            addZcaDocSwingWorkerListener(lisener);
        }
    }

    @Override
    public synchronized void cancel() {
        cancelingWorker = true;
    }

    private synchronized boolean isCancelingWorker() {
        return cancelingWorker;
    }

    @Override
    public boolean cancelingStatusCallback() {
        return isCancelingWorker();
    }
    
    @Override
    public synchronized void addZcaDocSwingWorkerListener(IZcaDocSwingWorkerListener listener){
        if (!swingWorkerListenerList.contains(listener)){
            swingWorkerListenerList.add(listener);
        }
    }
    
    @Override
    public synchronized void removeZcaDocSwingWorkerListener(IZcaDocSwingWorkerListener listener){
        swingWorkerListenerList.remove(listener);
    }
    
    synchronized void fireStatusMessagingEvent(IZcaDocMessageEvent event){
        for (IZcaDocSwingWorkerListener listener : swingWorkerListenerList){
            listener.statusMessagingEventHappened(event);
        }
    }
    
    synchronized void fireSwingWorkerDoneEvent(IZcaDocSwingWorkerEvent event){
        for (IZcaDocSwingWorkerListener listener : swingWorkerListenerList){
            listener.swingWorkerDoneEventHappened(event);
        }
    }

    synchronized boolean isLoggingMessageRequired() {
        return loggingMessageRequired;
    }

    synchronized void setLoggingMessageRequired(boolean loggingMessageRequired) {
        this.loggingMessageRequired = loggingMessageRequired;
    }

    synchronized ArrayList<INioPathTreeEventListener> getNioPathTreeListenerList() {
        return nioPathTreeListenerList;
    }
    
    @Override
    public void nioPathTreeEventHappened(INioPathTreeEvent event) {
        publish(event.getMessage());
    }

    @Override
    protected String doInBackground() throws Exception {
        int total = aZcaDocTaskList.size();
        if (total == 0){
            return "It failed to execute anything because no selected task was made!";
        }else{
            String result = "The task list is executed!";
            ZcaDocTask aZcaDocTask;
            boolean isCanceled = false;
            for (int i = 0; i < total; i++){
                if (isCancelingWorker()){
                    isCanceled = true;
                    result = "The task list is cancelled! " + i + "/" + total + " have been completed.";
                    break;
                }
                aZcaDocTask = aZcaDocTaskList.get(i);
                
                publish("[Task-" + (i+1) + " of " + total + "] "
                        + "Working on " + aZcaDocTask.getSourcePath());
                
                publish(proccessZcaDocTaskInbackground(aZcaDocTask));
                
                Thread.sleep(50);
            }
            if (isCanceled){
                delayTaskDueTimePoint();
            }
            return result;
        }
    }
    
    abstract NioPathTreeSummary proccessZcaDocTaskInbackground(ZcaDocTask aZcaDocTask);

    @Override
    protected void process(List<Object> chunks) {
        for (Object obj : chunks){
            processMessage(obj);
        }
    }
    
    void processMessage(Object obj){
        if (obj instanceof NioPathTreeSummary){
            NioPathTreeSummary aNioPathTreeSummary = (NioPathTreeSummary)obj;
            if (aNioPathTreeSummary.isOperationSuccessful()){
                processMessageHelper("SUMMARY:=========================================================");
                GregorianCalendar t = new GregorianCalendar();
                t.setTimeInMillis(aNioPathTreeSummary.getPathLastModified().toMillis());
                processMessageHelper("Last Modified: " + ZCalendarGlobal.getDataTimeString(t));
                processMessageHelper("Source Folder: " + aNioPathTreeSummary.getTargetPath());
                processMessageHelper("Subfolders: " + (aNioPathTreeSummary.getFolderCount()-1));
                processMessageHelper("Files: " + aNioPathTreeSummary.getFileCount());
                processMessageHelper("Size: " + aNioPathTreeSummary.getTotalSize() + " bytes");
                ArrayList<File> badFileList = aNioPathTreeSummary.getUnaccessiblePathFileList();
                if (badFileList.isEmpty()){
                    processMessageHelper("Unaccessible: 0 files");
                }else{
                    processMessageHelper("Total of Unaccessible: " + badFileList.size() + " files");
                    for (File aFile : badFileList){
                        processMessageHelper("Unaccessible: " + aFile.getAbsolutePath());
                    }
                }
                processMessageHelper("-----------------------------------------------------------------");
            }else{
                processMessageHelper(aNioPathTreeSummary.getOperationMessage());
            }
        }else{
            processMessageHelper(obj.toString(), false);
        }
    }
    
    private void processMessageHelper(String msg){
        processMessageHelper(msg, loggingMessageRequired);
    }
    
    private void processMessageHelper(String msg, boolean loggingRequired){
        fireStatusMessagingEvent(new ZcaDocMessageEvent(msg, loggingRequired));
    }

    /**
     * Reset due-time-point of all the current processing tasks to be the next time.
     * One reason to do it is "Canceling" operation.
     */
    private void delayTaskDueTimePoint() {
        for (ZcaDocTask aZcaDocTask : aZcaDocTaskList) {
            aZcaDocTask.setNextDueTimePoint();
        }
    }
}
