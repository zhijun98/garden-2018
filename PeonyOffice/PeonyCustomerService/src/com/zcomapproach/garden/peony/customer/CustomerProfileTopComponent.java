/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zcomapproach.garden.peony.customer;

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.peony.PeonyTopComponent;
import com.zcomapproach.garden.peony.view.events.PeonyAccountDeleted;
import com.zcomapproach.garden.peony.view.events.PeonyAccountSaved;
import com.zcomapproach.garden.peony.view.events.CloseTopComponentRequest;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.controllers.PeonyPersonalProfileController;
import com.zcomapproach.garden.persistence.peony.PeonyAccount;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Non-singleton top component which displays a specific customer profile.
 */
@TopComponent.Description(
        preferredID = "CustomerProfileTopComponent",
        iconBase = "com/zcomapproach/garden/peony/resources/images/customer.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@Messages({
    "CTL_CustomerProfileTopComponent=CustomerProfile Window",
    "HINT_CustomerProfileTopComponent=This is a CustomerProfile window"
})
public final class CustomerProfileTopComponent extends PeonyTopComponent {
    
    /**
     * Every customer has a formal web account. Some legacy data may have fake account.
     */
    private PeonyAccount targetCustomer;

    public CustomerProfileTopComponent() {
        //initComponents();
        setName(Bundle.CTL_CustomerProfileTopComponent());
        setToolTipText(Bundle.HINT_CustomerProfileTopComponent());

    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof PeonyAccountSaved){
            PeonyFaceUtils.displayInformationMessageDialog("Customer profile is saved.");
        }else if (event instanceof PeonyAccountDeleted){
            PeonyFaceUtils.displayInformationMessageDialog("Customer profile is deleted.");
            super.peonyFaceEventHappened(new CloseTopComponentRequest());
        }else{
            super.peonyFaceEventHappened(event);
        }
    }
    
    public void initializeCustomerProfileTopComponent(PeonyAccount targetCustomer){
        this.targetCustomer = targetCustomer;
        if (ZcaValidator.isNullEmpty(targetCustomer.getPeonyUserFullName())){
            super.launchPeonyTopComponent("Customer");
        }else{
            super.launchPeonyTopComponent("Customer: " + targetCustomer.getPeonyUserFullName());
        }
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
            return new PeonyPersonalProfileController(targetCustomer);
        }
        return getPeonyFaceController();
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
        // TODO add custom code on component closing
    }
}
