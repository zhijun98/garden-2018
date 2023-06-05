/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zcomapproach.zcadocmaster.face.worker;

import com.zcomapproach.zcadocmaster.events.IZcaDocMessageEvent;
import com.zcomapproach.zcadocmaster.events.IZcaDocSwingWorkerEvent;
import com.zcomapproach.zcadocmaster.events.ZcaDocMessageEvent;
import com.zcomapproach.zcadocmaster.exceptions.ZcaDocWorkerNotReadyException;
import com.zcomapproach.zcadocmaster.face.IZcaDocTaskOwner;
import java.util.List;

/**
 * At any time, this manager can offer one single worker. If one worker is working, 
 * no worker can be created for callers. 
 * 
 * @author Zhijun Zhang, date & time: Mar 20, 2014 - 4:27:57 PM
 */
public class ZcaDocSwingWorkerManager implements IZcaDocSwingWorkerListener{
    
    private static ZcaDocSwingWorkerManager self;
    static{
        self = null;
    }
    
    public static ZcaDocSwingWorkerManager getSingleton(){
        if (self == null){
            self = new ZcaDocSwingWorkerManager();
        }
        return self;
    }
    
    private IZcaDocSwingWorker workerHandler = null;
    
    private Thread scheduleThread;

    private int checkTimePointInterval;

    public ZcaDocSwingWorkerManager() {
        
        checkTimePointInterval = 1000*60;
    }
    
    public synchronized void startBackupScheduler(final IZcaDocTaskOwner taskOwner) 
    {
        if ((scheduleThread == null) || (!scheduleThread.isAlive())){
            scheduleThread = new Thread(new ZcaDocScheduler(taskOwner));
            scheduleThread.start();
            taskOwner.statusMessagingEventHappened(new ZcaDocMessageEvent("Automatic task worker has been started.", false));
        }
    }

    public synchronized void stopBackupScheduler(final IZcaDocTaskOwner taskOwner) {
        if ((scheduleThread != null) && (scheduleThread.isAlive())){
            cancelCurrentWorker();
            scheduleThread.interrupt();
            scheduleThread = null;
            taskOwner.statusMessagingEventHappened(new ZcaDocMessageEvent("Automatic task worker has been stopped.", false));
        }
    }

    public synchronized int getCheckTimePointInterval() {
        return checkTimePointInterval;
    }

    public synchronized void setCheckTimePointInterval(int checkTimePointInterval) {
        this.checkTimePointInterval = checkTimePointInterval;
    }
    
    public synchronized IZcaDocBackupWorker getZcaDocBackupWorker(List<ZcaDocTask> aZcaDocTaskList, 
                                                                  IZcaDocSwingWorkerListener listener)
            throws ZcaDocWorkerNotReadyException
    {
        if (workerHandler == null){
            workerHandler = new ZcaDocBackupWorker(aZcaDocTaskList, listener);
            workerHandler.addZcaDocSwingWorkerListener(this);
            return (ZcaDocBackupWorker)workerHandler;
        }else{
            throw new ZcaDocWorkerNotReadyException("Some other worker is in memory. Backup-worker cannot be launched.");
        }
        
    }
    
    public synchronized IZcaDocValidationWorker getZcaDocValidationWorker(List<ZcaDocTask> aZcaDocTaskList, IZcaDocSwingWorkerListener lisener)
            throws ZcaDocWorkerNotReadyException
    {
        if (workerHandler == null){
            workerHandler = new ZcaDocValidationWorker(aZcaDocTaskList, lisener);
            workerHandler.addZcaDocSwingWorkerListener(this);
            return (ZcaDocValidationWorker)workerHandler;
        }else{
            throw new ZcaDocWorkerNotReadyException("Some other worker is in memory. Validation-worker cannot be launched.");
        }
    }

    @Override
    public synchronized void statusMessagingEventHappened(IZcaDocMessageEvent event) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public synchronized void swingWorkerDoneEventHappened(IZcaDocSwingWorkerEvent event) {
        workerHandler = null;
    }

    public synchronized void cancelCurrentWorker() {
        if (workerHandler != null){
            workerHandler.cancel();
        }
    }

    private class ZcaDocScheduler implements Runnable{
        
        private IZcaDocTaskOwner taskOwner;
        
        public ZcaDocScheduler(IZcaDocTaskOwner taskOwner) {
            this.taskOwner = taskOwner;
        }
        
        @Override
        public void run() {
            while(true){
                try {
                    Thread.sleep(getCheckTimePointInterval());  //one minutes
                } catch (InterruptedException ex) {
                    break;
                }
                //Retrieve all the tasks whose scheduled time-point past the current time-point.
                List<ZcaDocTask> aZcaDocTaskList = taskOwner.retrieveDuedTaskList();
                if ((aZcaDocTaskList != null) && (!aZcaDocTaskList.isEmpty())){
                    try {
                        IZcaDocBackupWorker aBackupWorker = getZcaDocBackupWorker(aZcaDocTaskList, taskOwner);
                        aBackupWorker.execute();
                    } catch (ZcaDocWorkerNotReadyException ex) {
                        //some work is working, skip it and wait for the next minutes
                    }
                }
            }//while               
        }//run
    }
    
}
