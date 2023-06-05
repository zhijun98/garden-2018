/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zcomapproach.garden.peony.search.actions;

import com.zcomapproach.garden.peony.kernel.services.PeonySearchService;
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
        id = "com.zcomapproach.garden.peony.management.PeonySearchAction"
)
@ActionRegistration(
        iconBase = "com/zcomapproach/garden/peony/resources/images/table_tab_search.png",
        displayName = "#CTL_PeonySearchAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 100)
    ,
  @ActionReference(path = "Toolbars/File", position = 1000)
    ,
  @ActionReference(path = "Shortcuts", name = "D-F")
})
@Messages("CTL_PeonySearchAction=Search")
public final class PeonySearchAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        Lookup.getDefault().lookup(PeonySearchService.class).openPeonySearchEnginePane();
    }
}
