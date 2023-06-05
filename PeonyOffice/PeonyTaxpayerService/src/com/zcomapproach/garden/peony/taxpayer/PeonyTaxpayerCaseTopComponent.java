/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zcomapproach.garden.peony.taxpayer;

import com.zcomapproach.garden.peony.PeonyTopComponent;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import com.zcomapproach.garden.peony.taxpayer.controllers.PeonyTaxpayerCaseController;
import com.zcomapproach.garden.peony.taxpayer.controllers.PeonyTaxpayerLaunchController;
import com.zcomapproach.garden.peony.taxpayer.controllers.PeonyTaxpayerServiceController;
import com.zcomapproach.garden.peony.taxpayer.events.RequestToDisplayPeonyTaxpayerCaseList;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.events.CloseTopComponentRequest;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import java.util.List;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Non-singleton top component which displays a taxpayer case.
 */
@TopComponent.Description(
        preferredID = "PeonyTaxpayerCaseTopComponent",
        iconBase = "com/zcomapproach/garden/peony/resources/images/taxpayer.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@Messages({
    "CTL_PeonyTaxpayerCaseTopComponent=Peony Taxpayer Case",
    "HINT_PeonyTaxpayerCaseTopComponent=All the information of a taxpayer case"
})
public final class PeonyTaxpayerCaseTopComponent extends PeonyTopComponent {
    
    private String windowTitle;
    private PeonyTaxpayerCase targetPeonyTaxpayerCase;
    private PeonyTaxpayerServiceController peonyTaxpayerController;
    
    public PeonyTaxpayerCaseTopComponent() {
        //initComponents();
        setName(Bundle.CTL_PeonyTaxpayerCaseTopComponent());
        setToolTipText(Bundle.HINT_PeonyTaxpayerCaseTopComponent());

    }

    /**
     * this method disabled users to call directly from this top component instance
     */
    @Override
    public void launchPeonyTopComponent(String windowName) {
        throw new UnsupportedOperationException("Not supported for this top component instance.");
    }

    public void launchPeonyTaxpayerCaseTopComponent(PeonyTaxpayerCase targetPeonyTaxpayerCase) {
        this.targetPeonyTaxpayerCase = targetPeonyTaxpayerCase;
        if (targetPeonyTaxpayerCase == null){
            this.windowTitle = "Taxpayer Case";
        }else{
            this.windowTitle = targetPeonyTaxpayerCase.getTaxpayerCaseTitle(false);
        }
        super.launchPeonyTopComponent(windowTitle);
    }

    @Override
    protected PeonyFaceController createPeonyFaceController() {
        if (getPeonyFaceController() == null){
            if (targetPeonyTaxpayerCase == null){
                peonyTaxpayerController = new PeonyTaxpayerLaunchController();
                peonyTaxpayerController.addPeonyFaceEventListener(this);
            }else{
                peonyTaxpayerController = new PeonyTaxpayerCaseController(targetPeonyTaxpayerCase);
            }
            peonyTaxpayerController.addPeonyFaceEventListener(this);
            return peonyTaxpayerController;
        }
        return getPeonyFaceController();
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof RequestToDisplayPeonyTaxpayerCaseList){
            handleRequestToDisplayPeonyTaxpayerCaseList((RequestToDisplayPeonyTaxpayerCaseList)event);
        }else{
            super.peonyFaceEventHappened(event);
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
        // TODO add custom code on component closing
    }

    private void handleRequestToDisplayPeonyTaxpayerCaseList(RequestToDisplayPeonyTaxpayerCaseList requestToDisplayPeonyTaxpayerCaseList) {
        if (requestToDisplayPeonyTaxpayerCaseList.getPeonyTaxpayerCaseList() == null){
            return;
        }
        //display top-components for everyone in requestToDisplayPeonyTaxpayerCaseList
        List<PeonyTaxpayerCase> aPeonyTaxpayerCaseList = requestToDisplayPeonyTaxpayerCaseList.getPeonyTaxpayerCaseList().getPeonyTaxpayerCaseList();
        for (PeonyTaxpayerCase aPeonyTaxpayerCase : aPeonyTaxpayerCaseList){
            Lookup.getDefault().lookup(PeonyTaxpayerService.class).launchPeonyTaxpayerCaseTopComponent(aPeonyTaxpayerCase);
        }
        //close this top-component
        if (targetPeonyTaxpayerCase == null){
            super.peonyFaceEventHappened(new CloseTopComponentRequest());
        }
    }
}
