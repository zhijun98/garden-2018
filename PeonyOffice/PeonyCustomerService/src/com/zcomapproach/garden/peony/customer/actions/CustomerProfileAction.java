/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zcomapproach.garden.peony.customer.actions;

import com.zcomapproach.garden.peony.kernel.services.PeonyCustomerService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.util.Lookup;
/**
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
* 
@ActionID(
        category = "Edit",
        id = "com.zcomapproach.garden.peony.customer.actions.CustomerProfileAction"
)
@ActionRegistration(
        iconBase = "com/zcomapproach/garden/peony/resources/images/customer.png",
        displayName = "#CTL_CustomerProfileAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = -100)
    ,
  @ActionReference(path = "Toolbars/File", position = 2500)
})
@Messages("CTL_CustomerProfileAction=Customer Profile")
 * 
 * @author zhijun98
 */
public final class CustomerProfileAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        Lookup.getDefault().lookup(PeonyCustomerService.class).launchCustomerProfileWindowByAccountUuid(null);
    }
}
