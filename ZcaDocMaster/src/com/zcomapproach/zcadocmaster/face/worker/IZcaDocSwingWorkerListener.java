/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zcomapproach.zcadocmaster.face.worker;

import com.zcomapproach.zcadocmaster.events.IZcaDocMessageEvent;
import com.zcomapproach.zcadocmaster.events.IZcaDocSwingWorkerEvent;

/**
 *
 * @author Zhijun Zhang, date & time: Mar 20, 2014 - 2:07:06 PM
 */
public interface IZcaDocSwingWorkerListener {

    public void statusMessagingEventHappened(IZcaDocMessageEvent event);

    public void swingWorkerDoneEventHappened(IZcaDocSwingWorkerEvent event);
    

}
