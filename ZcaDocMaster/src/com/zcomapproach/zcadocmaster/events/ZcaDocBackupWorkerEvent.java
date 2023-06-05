/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zcomapproach.zcadocmaster.events;

import com.zcomapproach.zcadocmaster.face.worker.ZcaDocTask;

/**
 *
 * @author Zhijun Zhang, date & time: Mar 20, 2014 - 2:38:37 PM
 */
public class ZcaDocBackupWorkerEvent extends ZcaDocSwingWorkerEvent{

    public ZcaDocBackupWorkerEvent(String eventMessage) {
        super(eventMessage);
    }

    public ZcaDocBackupWorkerEvent(ZcaDocTask aZcaDocTask) {
        super(aZcaDocTask);
    }

    public ZcaDocBackupWorkerEvent(ZcaDocTask aZcaDocTask, String eventMessage) {
        super(aZcaDocTask, eventMessage);
    }

}
