/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zcomapproach.garden.peony.business.actions;

import com.zcomapproach.garden.peony.business.PeonyBusinessCenterTopComponent;
import com.zcomapproach.garden.peony.security.PeonyPrivilege;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ActionID(
        category = "Edit",
        id = "com.zcomapproach.garden.peony.business.actions.PeonyBusinessCenterAction"
)
@ActionRegistration(
        iconBase = "com/zcomapproach/garden/peony/resources/images/chart_line.png",
        displayName = "#CTL_PeonyBusinessCenterAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = -100)
    ,
  @ActionReference(path = "Toolbars/File", position = 900)
})
@Messages("CTL_PeonyBusinessCenterAction=Peony Business Center")
public final class PeonyBusinessCenterAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        if (PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.BUSINESS_CENTER)){
            TopComponent tc = WindowManager.getDefault().findTopComponent("PeonyBusinessCenterTopComponent");
            if (tc instanceof PeonyBusinessCenterTopComponent){
                ((PeonyBusinessCenterTopComponent) tc).launchPeonyTopComponent("Peony Business Center");
                tc.open();
                tc.requestActive();
            }
        }else{
            PeonyFaceUtils.displayErrorMessageDialog("You are not authorized. Please contact administrator to solve it.");
        }
    }
}
