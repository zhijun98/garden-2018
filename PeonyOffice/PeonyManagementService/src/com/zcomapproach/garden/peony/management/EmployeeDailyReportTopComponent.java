/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zcomapproach.garden.peony.management;

import com.zcomapproach.garden.peony.PeonyTopComponent;
import com.zcomapproach.garden.peony.management.controllers.EmployeeDailyReportController;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Singleton top component which displays my-daily-report.
 */
@TopComponent.Description(
        preferredID = "EmployeeDailyReportTopComponent",
        iconBase = "com/zcomapproach/garden/peony/resources/images/script-text.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@Messages({
    "CTL_EmployeeDailyReportTopComponent=Employee Daily Report",
    "HINT_EmployeeDailyReportTopComponent=This is my daily report"
})
public final class EmployeeDailyReportTopComponent extends PeonyTopComponent {
    
    private PeonyEmployee targetPeonyEmployee;
    
    private EmployeeDailyReportController employeeDailyReportController;

    public EmployeeDailyReportTopComponent() {
        //initComponents();
        setName(Bundle.CTL_EmployeeDailyReportTopComponent());
        setToolTipText(Bundle.HINT_EmployeeDailyReportTopComponent());

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
     * this method disabled users to call directly from this top component instance
     */
    @Override
    public void launchPeonyTopComponent(String windowName) {
        throw new UnsupportedOperationException("Not supported for this top component instance.");
    }

    public void launchEmployeeDailyReportTopComponent(PeonyEmployee targetPeonyEmployee) {
        this.targetPeonyEmployee = targetPeonyEmployee;
        super.launchPeonyTopComponent("Daily Report Summary" + targetPeonyEmployee.getPeonyUserFullName());
    }


    @Override
    protected PeonyFaceController createPeonyFaceController() {
        if (employeeDailyReportController == null){
            employeeDailyReportController = new EmployeeDailyReportController(targetPeonyEmployee);
        }
        return employeeDailyReportController;
    }

    void refreshEmployeeDailyReport() {
        if (employeeDailyReportController != null){
            employeeDailyReportController.refreshEmployeeDailyReport(targetPeonyEmployee);
        }
    }

}
