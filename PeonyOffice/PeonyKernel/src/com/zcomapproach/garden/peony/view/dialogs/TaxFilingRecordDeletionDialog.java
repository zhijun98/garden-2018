/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zcomapproach.garden.peony.view.dialogs;

import com.zcomapproach.garden.peony.view.controllers.TaxFilingRecordDeletionController;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import java.awt.Frame;

/**
 *
 * @author yinlu
 */
public class TaxFilingRecordDeletionDialog extends PeonyFaceDialog {
    
    private String dialogTitle;
    private String targetOwnerEntityUuid;
    
    public TaxFilingRecordDeletionDialog(Frame parent, boolean modal) {
        super(parent, modal);
    }

    /**
     * this method disabled users to call directly from this dialog instance
     */
    @Override
    public void launchPeonyDialog(final String dialogTitle) {
        throw new UnsupportedOperationException("Not supported for this dialog instance.");
    }
    
    public void launchTaxFilingRecordDeletionDialog(final String dialogTitle, final String targetOwnerEntityUuid) {
        this.targetOwnerEntityUuid = targetOwnerEntityUuid;
        super.launchPeonyDialog(dialogTitle);
    }

    @Override
    protected PeonyFaceController createPeonyFaceController() {
        if (getPeonyFaceController() == null){
            PeonyFaceController aPeonyFaceController = new TaxFilingRecordDeletionController(dialogTitle, targetOwnerEntityUuid);
            /**
             * Listeners to the dialog also listen to the dialog's controllers
             */
            aPeonyFaceController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
            return aPeonyFaceController;
        }
        return getPeonyFaceController();
    }
    
}
