/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zcomapproach.garden.peony.taxcorp.actions;

import com.zcomapproach.garden.peony.kernel.services.PeonyTaxcorpService;
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
        id = "com.zcomapproach.garden.peony.taxcorp.actions.TaxcorpCaseAction"
)
@ActionRegistration(
        iconBase = "com/zcomapproach/garden/peony/resources/images/taxcorp.png",
        displayName = "#CTL_TaxcorpCaseAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = -100)
    ,
  @ActionReference(path = "Toolbars/File", position = 4000)
})
@Messages("CTL_TaxcorpCaseAction=Taxcorp Case")
public final class TaxcorpCaseAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        Lookup.getDefault().lookup(PeonyTaxcorpService.class).launchPeonyTaxcorpCaseTopComponent(null);
    }
}
