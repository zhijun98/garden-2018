/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zcomapproach.garden.peony.taxcorp;

import com.zcomapproach.garden.peony.PeonyTopComponent;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxcorpService;
import com.zcomapproach.garden.peony.taxcorp.controllers.PeonyTaxcorpCaseController;
import com.zcomapproach.garden.peony.taxcorp.controllers.PeonyTaxcorpLaunchController;
import com.zcomapproach.garden.peony.taxcorp.controllers.PeonyTaxcorpServiceController;
import com.zcomapproach.garden.peony.taxcorp.events.RequestToDisplayPeonyTaxcorpCaseList;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.events.CloseTopComponentRequest;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.persistence.peony.PeonyTaxcorpCase;
import java.util.List;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Non-singleton top component which displays a taxcorp case.
 */
@TopComponent.Description(
        preferredID = "PeonyTaxcorpCaseTopComponent",
        iconBase = "com/zcomapproach/garden/peony/resources/images/taxcorp.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@Messages({
    "CTL_PeonyTaxcorpCaseTopComponent=Peony Taxcorp Case",
    "HINT_PeonyTaxcorpCaseTopComponent=All the information of a taxcorp case"
})
public final class PeonyTaxcorpCaseTopComponent extends PeonyTopComponent {
    /**
     * The title of this top component
     */
    private String windowTitle;
    /**
     * This target is null when it is up to users to decide to add or edit taxcorp
     * case. In this case, 
     */
    private PeonyTaxcorpCase targetPeonyTaxcorpCase;
    /**
     * This controller contains the real targetPeonyTaxcorpCase
     */
    private PeonyTaxcorpServiceController peonyTaxcorpController;
    
    public PeonyTaxcorpCaseTopComponent() {
        //initComponents();
        setName(Bundle.CTL_PeonyTaxcorpCaseTopComponent());
        setToolTipText(Bundle.HINT_PeonyTaxcorpCaseTopComponent());
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

    /**
     * this method is disabled to be called directly
     */
    @Override
    public void launchPeonyTopComponent(String windowName) {
        throw new UnsupportedOperationException("Not supported for this top component instance.");
    }

    public void launchPeonyTaxcorpCaseTopComponent(PeonyTaxcorpCase targetPeonyTaxcorpCase) {
        this.targetPeonyTaxcorpCase = targetPeonyTaxcorpCase;
        if (targetPeonyTaxcorpCase == null){
            this.windowTitle = "Taxcorp Case";
        }else{
            this.windowTitle = targetPeonyTaxcorpCase.getTaxcorpCaseTitle();
        }
        super.launchPeonyTopComponent(windowTitle);
    }

    @Override
    protected PeonyFaceController createPeonyFaceController() {
        if (getPeonyFaceController() == null){
            if (targetPeonyTaxcorpCase == null){
                peonyTaxcorpController = new PeonyTaxcorpLaunchController();
                peonyTaxcorpController.addPeonyFaceEventListener(this);
            }else{
                peonyTaxcorpController = new PeonyTaxcorpCaseController(targetPeonyTaxcorpCase);
            }
            peonyTaxcorpController.addPeonyFaceEventListener(this);
            return peonyTaxcorpController;
        }
        return getPeonyFaceController();
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
//        if (event instanceof StoreTargetPeonyTaxcorpCaseRequest){
//            peonyTaxcorpController.storeTargetTaxcorpCase();
        if (event instanceof RequestToDisplayPeonyTaxcorpCaseList){
            handleRequestToDisplayPeonyTaxcorpCaseList(((RequestToDisplayPeonyTaxcorpCaseList)event));
        }else{
            super.peonyFaceEventHappened(event);
        }
    }

    private void handleRequestToDisplayPeonyTaxcorpCaseList(RequestToDisplayPeonyTaxcorpCaseList requestToDisplayPeonyTaxcorpCaseList) {
        if (requestToDisplayPeonyTaxcorpCaseList.getPeonyTaxcorpCaseList() == null){
            return;
        }
        //display top-components for everyone in requestToDisplayPeonyTaxcorpCaseList
        List<PeonyTaxcorpCase> aPeonyTaxcorpCaseList = requestToDisplayPeonyTaxcorpCaseList.getPeonyTaxcorpCaseList().getPeonyTaxcorpCaseList();
        for (PeonyTaxcorpCase aPeonyTaxcorpCase : aPeonyTaxcorpCaseList){
            Lookup.getDefault().lookup(PeonyTaxcorpService.class).launchPeonyTaxcorpCaseTopComponent(aPeonyTaxcorpCase);
        }
        //close this top-component
        if (targetPeonyTaxcorpCase == null){
            super.peonyFaceEventHappened(new CloseTopComponentRequest());
        }
    }
}
