/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zcomapproach.zcadocmaster.events;

import com.zcomapproach.zcadocmaster.face.worker.ZcaDocTask;

/**
 *
 * @author Zhijun Zhang, date & time: Mar 20, 2014 - 2:17:52 PM
 */
public interface IZcaDocSwingWorkerEvent extends IZcaDocMasterEvent{

    public ZcaDocTask getaZcaDocTask();

    public void setaZcaDocTask(ZcaDocTask aZcaDocTask);
}
