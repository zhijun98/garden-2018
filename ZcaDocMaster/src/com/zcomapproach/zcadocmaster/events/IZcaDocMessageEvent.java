/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zcomapproach.zcadocmaster.events;

/**
 *
 * @author Zhijun Zhang, date & time: Mar 20, 2014 - 2:26:10 PM
 */
public interface IZcaDocMessageEvent extends IZcaDocMasterEvent{
    
    public boolean isLoggingRequired();

    public void setLoggingRequired(boolean loggingRequired);
    
}
