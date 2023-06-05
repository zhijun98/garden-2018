/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zcomapproach.zcadocmaster.face.worker;

/**
 *
 * @author Zhijun Zhang, date & time: Mar 20, 2014 - 4:25:31 PM
 */
public interface IZcaDocSwingWorker {
    
    public void addZcaDocSwingWorkerListener(IZcaDocSwingWorkerListener listener);
    
    public void removeZcaDocSwingWorkerListener(IZcaDocSwingWorkerListener listener);
    
    public void execute();

    /**
     * Cancel current working task if it is running
     */
    public void cancel();
    
}
