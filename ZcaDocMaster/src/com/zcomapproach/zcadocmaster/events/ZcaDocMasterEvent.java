/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zcomapproach.zcadocmaster.events;

import com.zcomapproach.zcaglobals.events.ZEvent;

/**
 *
 * @author Zhijun Zhang, date & time: Mar 20, 2014 - 2:19:44 PM
 */
public abstract class ZcaDocMasterEvent extends ZEvent{

    public ZcaDocMasterEvent() {
    }

    public ZcaDocMasterEvent(String eventMessage) {
        super(eventMessage);
    }

}
