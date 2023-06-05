/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zcomapproach.garden.peony.admin.zcadocmaster.exceptions;

import com.zcomapproach.commons.exceptions.validation.ZcaComponentNotReadyException;

/**
 *
 * @author Zhijun Zhang, date & time: Mar 20, 2014 - 4:41:13 PM
 */
public class ZcaDocWorkerNotReadyException extends ZcaComponentNotReadyException{

    public ZcaDocWorkerNotReadyException() {
    }

    public ZcaDocWorkerNotReadyException(String msg) {
        super(msg);
    }

}
