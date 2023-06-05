/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zcomapproach.garden.peony.admin.zcadocmaster.events;

import com.zcomapproach.garden.peony.admin.zcadocmaster.face.worker.ZcaDocTask;

/**
 *
 * @author Zhijun Zhang, date & time: Mar 20, 2014 - 2:39:22 PM
 */
public class ZcaDocValidationWorkerEvent extends ZcaDocSwingWorkerEvent{

    public ZcaDocValidationWorkerEvent(String eventMessage) {
        super(eventMessage);
    }

    public ZcaDocValidationWorkerEvent(ZcaDocTask aZcaDocTask) {
        super(aZcaDocTask);
    }

    public ZcaDocValidationWorkerEvent(ZcaDocTask aZcaDocTask, String eventMessage) {
        super(aZcaDocTask, eventMessage);
    }

}
