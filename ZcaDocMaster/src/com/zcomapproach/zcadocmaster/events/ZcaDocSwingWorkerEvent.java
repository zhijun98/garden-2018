/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zcomapproach.zcadocmaster.events;

import com.zcomapproach.zcadocmaster.face.worker.ZcaDocTask;

/**
 *
 * @author Zhijun Zhang, date & time: Mar 20, 2014 - 2:19:08 PM
 */
public abstract class ZcaDocSwingWorkerEvent extends ZcaDocMasterEvent implements IZcaDocSwingWorkerEvent{

    private ZcaDocTask aZcaDocTask;

    public ZcaDocSwingWorkerEvent(String eventMessage) {
        super(eventMessage);
    }

    public ZcaDocSwingWorkerEvent(ZcaDocTask aZcaDocTask) {
        this.aZcaDocTask = aZcaDocTask;
    }

    public ZcaDocSwingWorkerEvent(ZcaDocTask aZcaDocTask, String eventMessage) {
        super(eventMessage);
        this.aZcaDocTask = aZcaDocTask;
    }

    @Override
    public ZcaDocTask getaZcaDocTask() {
        return aZcaDocTask;
    }

    @Override
    public void setaZcaDocTask(ZcaDocTask aZcaDocTask) {
        this.aZcaDocTask = aZcaDocTask;
    }
    
}
