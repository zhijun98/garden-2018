/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zcomapproach.zcadocmaster.events;

/**
 *
 * @author Zhijun Zhang, date & time: Mar 20, 2014 - 2:25:48 PM
 */
public class ZcaDocMessageEvent extends ZcaDocMasterEvent implements IZcaDocMessageEvent{

    private boolean loggingRequired;

    public ZcaDocMessageEvent(String eventMessage, boolean loggingRequired) {
        super(eventMessage);
        this.loggingRequired = loggingRequired;
    }

    @Override
    public boolean isLoggingRequired() {
        return loggingRequired;
    }

    @Override
    public void setLoggingRequired(boolean loggingRequired) {
        this.loggingRequired = loggingRequired;
    }
    
}
