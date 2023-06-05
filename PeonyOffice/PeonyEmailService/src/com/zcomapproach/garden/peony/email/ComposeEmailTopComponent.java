/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zcomapproach.garden.peony.email;

import com.zcomapproach.garden.email.GardenEmailMessage;
import com.zcomapproach.garden.peony.PeonyTopComponent;
import com.zcomapproach.garden.email.data.OfflineMessageStatus;
import com.zcomapproach.garden.peony.email.controllers.ComposeEmailController;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmail;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Non-singleton top component which displays email composer for users.
 */
@TopComponent.Description(
        preferredID = "ComposeEmailTopComponent",
        iconBase = "com/zcomapproach/garden/peony/resources/images/compose.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@Messages({
    "CTL_ComposeEmailTopComponent=Compose Email Window",
    "HINT_ComposeEmailTopComponent=Compose a new email"
})
public final class ComposeEmailTopComponent extends PeonyTopComponent {
    
    private GardenEmailMessage targetGardenEmailMessage;
    private PeonyOfflineEmail targetPeonyOfflineEmail;
    private OfflineMessageStatus purpose;

    public ComposeEmailTopComponent() {
        //initComponents();
        setName(Bundle.CTL_ComposeEmailTopComponent());
        setToolTipText(Bundle.HINT_ComposeEmailTopComponent());
    }
    
    @Override
    public String getTargetTopComponetUuid() {
        if (targetGardenEmailMessage == null){
            return this.getName();
        }else{
            return targetGardenEmailMessage.getFolderFullName() + targetGardenEmailMessage.getEmailMsgUid();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        
    }

    public void initializeComposeEmailTopComponent(GardenEmailMessage targetGardenEmailMessage, PeonyOfflineEmail targetPeonyOfflineEmail, OfflineMessageStatus purpose) {
        if (targetGardenEmailMessage == null){
            targetGardenEmailMessage = new GardenEmailMessage();
        }
        this.targetGardenEmailMessage = targetGardenEmailMessage;
        this.targetPeonyOfflineEmail = targetPeonyOfflineEmail;
        
        this.purpose = purpose;
        
        super.launchPeonyTopComponent(Bundle.CTL_ComposeEmailTopComponent());
    }

    /**
     * this method disabled users to call directly from this top component instance
     */
    @Override
    public void launchPeonyTopComponent(String windowName) {
        throw new UnsupportedOperationException("Not supported for this top component instance.");
    }

    @Override
    protected PeonyFaceController createPeonyFaceController() {
        if (getPeonyFaceController() == null){
            PeonyFaceController aPeonyFaceController = new ComposeEmailController(targetGardenEmailMessage, targetPeonyOfflineEmail, purpose);
            aPeonyFaceController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
            return aPeonyFaceController;
        }
        return getPeonyFaceController();
    }
}
