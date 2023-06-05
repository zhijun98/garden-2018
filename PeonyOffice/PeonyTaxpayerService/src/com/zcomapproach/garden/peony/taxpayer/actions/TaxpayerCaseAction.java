/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zcomapproach.garden.peony.taxpayer.actions;

import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Tools",
        id = "com.zcomapproach.garden.peony.taxpayer.actions.TaxpayerCaseAction"
)
@ActionRegistration(
        iconBase = "com/zcomapproach/garden/peony/resources/images/taxpayer.png",
        displayName = "#CTL_TaxpayerCaseAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = -100)
    ,
  @ActionReference(path = "Toolbars/File", position = 3500)
})
@Messages("CTL_TaxpayerCaseAction=Taxpayer Case")
public final class TaxpayerCaseAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        Lookup.getDefault().lookup(PeonyTaxpayerService.class).launchPeonyTaxpayerCaseTopComponent(null);
    }
}
