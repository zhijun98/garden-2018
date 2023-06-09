/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zcomapproach.garden.peony.admin.actions;

import com.zcomapproach.garden.peony.admin.PeonyAdminServiceProvider;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Edit",
        id = "com.zcomapproach.garden.peony.admin.actions.PeonyAdminServiceAction"
)
@ActionRegistration(
        iconBase = "com/zcomapproach/garden/peony/resources/images/folder_database.png",
        displayName = "#CTL_PeonyAdminServiceAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = -100)
    ,
  @ActionReference(path = "Toolbars/File", position = 3000)
})
@Messages("CTL_PeonyAdminServiceAction=Peony Admin Service")
public final class PeonyAdminServiceAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        PeonyAdminServiceProvider.launchDocMasterWindow();
    }
}
