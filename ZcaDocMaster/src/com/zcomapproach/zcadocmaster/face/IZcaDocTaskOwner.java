/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zcomapproach.zcadocmaster.face;

import com.zcomapproach.zcadocmaster.face.worker.IZcaDocSwingWorkerListener;
import com.zcomapproach.zcadocmaster.face.worker.ZcaDocTask;
import java.util.List;

/**
 * Owner of all the ZcaDocTasks, which is a IZcaDocSwingWorkerListener at the same time
 * @author Zhijun Zhang, date & time: Mar 20, 2014 - 8:39:42 PM
 */
public interface IZcaDocTaskOwner extends IZcaDocSwingWorkerListener{

    /**
     * Retrieve all the tasks whose scheduled time-point past the current time-point.
     * @return 
     */
    public List<ZcaDocTask> retrieveDuedTaskList();

}
