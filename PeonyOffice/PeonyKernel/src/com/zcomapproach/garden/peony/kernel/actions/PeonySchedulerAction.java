/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zcomapproach.garden.peony.kernel.actions;

import com.zcomapproach.garden.peony.kernel.PeonySchedulerTopComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

//@ActionID(
//        category = "Edit",
//        id = "com.zcomapproach.garden.peony.kernel.actions.PeonySchedulerAction"
//)
//@ActionRegistration(
//        iconBase = "com/zcomapproach/garden/peony/resources/images/session_idle_time.png",
//        displayName = "#CTL_PeonySchedulerAction"
//)
//@ActionReferences({
//    @ActionReference(path = "Menu/File", position = -200)
//    ,
//  @ActionReference(path = "Toolbars/File", position = 800)
//})
//@Messages("CTL_PeonySchedulerAction=Peony Scheduler")
public final class PeonySchedulerAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        TopComponent tc = WindowManager.getDefault().findTopComponent("PeonySchedulerTopComponent");
        if (tc instanceof PeonySchedulerTopComponent){
            ((PeonySchedulerTopComponent) tc).launchPeonyTopComponent("Peony Scheduler");
            tc.open();
            tc.requestActive();
        }
    }
}
