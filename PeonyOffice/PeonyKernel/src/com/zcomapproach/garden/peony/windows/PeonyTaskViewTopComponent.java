/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zcomapproach.garden.peony.windows;

import com.zcomapproach.garden.peony.PeonyTopComponent;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.controllers.PeonyTaskViewPaneController;
import com.zcomapproach.garden.persistence.peony.PeonyCommAssignment;
import com.zcomapproach.commons.ZcaCalendar;
import java.util.List;
import javafx.scene.control.Tab;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Non-singleton top component which displays a specific task content.
 */
@TopComponent.Description(
        preferredID = "PeonyTaskViewTopComponent",
        iconBase = "com/zcomapproach/garden/peony/resources/images/pin_circle.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@Messages({
    "CTL_PeonyTaskViewTopComponent=PeonyTaskView Window",
    "HINT_PeonyTaskViewTopComponent=This is a PeonyTaskView window"
})
public final class PeonyTaskViewTopComponent extends PeonyTopComponent {
    
    private PeonyCommAssignment targetPeonyCommAssignment;
    private List<Tab> targetTaskTabList;

    public PeonyTaskViewTopComponent() {
        //initComponents();
        setName(Bundle.CTL_PeonyTaskViewTopComponent());
        setToolTipText(Bundle.HINT_PeonyTaskViewTopComponent());

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
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
        // TODO add custom code on component closing
    }

    public void initializePeonyTaskViewTopComponent(final PeonyCommAssignment targetPeonyCommAssignment, final List<Tab> targetTaskTabList) {
        //GardenEmailSerializer.getSingleton().deserializeToGardenEmailMessage(messageDataFilePath);
        if (targetPeonyCommAssignment == null){
            return;
        }
        
        this.targetPeonyCommAssignment = targetPeonyCommAssignment;
        this.targetTaskTabList = targetTaskTabList;
        
        super.launchPeonyTopComponent("Task: " +ZcaCalendar.convertToMMddyyyyHHmmss(targetPeonyCommAssignment.getCommAssignment().getCreated(), "-", " ", ":"));
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
            return new PeonyTaskViewPaneController(targetPeonyCommAssignment, targetTaskTabList);
        }
        return getPeonyFaceController();
    }
}
