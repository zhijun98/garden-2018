/*
 * Copyright 2018 ZComApproach Inc.
 *
 * Licensed under multiple open source licenses involved in the project (the "Licenses");
 * you may not use this file except in compliance with the Licenses.
 * You may obtain copies of the Licenses at
 *
 *      http://www.zcomapproach.com/licenses
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zcomapproach.garden.peony.taxcorp.controllers;

import com.jfoenix.controls.JFXButton;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.kernel.data.PeonyBusinessContactorTreeItemData;
import com.zcomapproach.garden.peony.kernel.services.PeonyBusinessService;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.taxcorp.dialogs.TaxcorpPersonnelContactorDialog;
import com.zcomapproach.garden.peony.taxcorp.events.BusinessContactorSaved;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.data.PeonyTreeItemData;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.constant.BusinessContactorRole;
import com.zcomapproach.garden.persistence.entity.G02BusinessContactor;
import com.zcomapproach.garden.persistence.peony.PeonyTaxcorpCase;
import com.zcomapproach.garden.rest.GardenRestParams;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javax.swing.JOptionPane;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class TaxcorpContactorListController extends PeonyTaxcorpServiceController implements PeonyFaceEventListener{
    @FXML
    private JFXButton addContactorButton;
    
    @FXML
    private TreeView<PeonyBusinessContactorTreeItemData> contactorsTreeView;
    
    private final TreeSet<String> contactorUuidFilter = new TreeSet<>();
    private TreeItem<PeonyBusinessContactorTreeItemData> contactorsTreeRoot;
    private final HashMap<String, TreeItem<PeonyBusinessContactorTreeItemData>> contactorsMap = new HashMap<>();
    private final PeonyTaxcorpCase targetPeonyTaxcorpCase;

    public TaxcorpContactorListController(PeonyTaxcorpCase targetPeonyTaxcorpCase) {
        super(targetPeonyTaxcorpCase);
        this.targetPeonyTaxcorpCase = targetPeonyTaxcorpCase;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        initiaizeContactorsTreeView();
        
        addContactorButton.setGraphic(PeonyGraphic.getImageView("user_add.png"));
        addContactorButton.setOnAction((ActionEvent event) -> {
            TaxcorpPersonnelContactorDialog aTaxcorpPersonnelContactorDialog = new TaxcorpPersonnelContactorDialog(null, true);
            aTaxcorpPersonnelContactorDialog.addPeonyFaceEventListener(this);
            aTaxcorpPersonnelContactorDialog.launchTaxcorpPersonnelContactorDialog("Taxcorp's Personnel & Contrator", new G02BusinessContactor(), targetPeonyTaxcorpCase);
        });
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof BusinessContactorSaved){
            handleBusinessContactorSaved((BusinessContactorSaved)event);
        }
    }

    private void handleBusinessContactorSaved(final BusinessContactorSaved event) {
        if (Platform.isFxApplicationThread()){
            handleBusinessContactorSavedHelper(event);
        }else{
            Platform.runLater(() -> {
                handleBusinessContactorSavedHelper(event);
            });
        }
    }
    
    private String getContactorMapKey(G02BusinessContactor aG02BusinessContactor){
        return aG02BusinessContactor.getLastName() + ", " + aG02BusinessContactor.getFirstName();
    }
    
    private void handleBusinessContactorSavedHelper(final BusinessContactorSaved event) {
        if (event == null){
            return;
        }
        G02BusinessContactor aG02BusinessContactor = event.getaG02BusinessContactor();
        if (aG02BusinessContactor == null){
            return;
        }
        if (contactorUuidFilter.contains(aG02BusinessContactor.getBusinessContactorUuid())){
            return;
        }
        String name = getContactorMapKey(aG02BusinessContactor); 
        TreeItem<PeonyBusinessContactorTreeItemData> contactorTreeItem = contactorsMap.get(name);
        if (contactorTreeItem == null){
            contactorTreeItem = new TreeItem<>(new PeonyBusinessContactorTreeItemData(name, PeonyTreeItemData.Status.INITIAL));
            contactorsMap.put(name, contactorTreeItem);
            contactorsTreeRoot.getChildren().add(contactorTreeItem);
        }
        contactorTreeItem.getChildren().add(new TreeItem<>(new PeonyBusinessContactorTreeItemData(aG02BusinessContactor, PeonyTreeItemData.Status.INITIAL)));
        contactorUuidFilter.add(aG02BusinessContactor.getBusinessContactorUuid());
        contactorTreeItem.setExpanded(true);
    }

    private void initiaizeContactorsTreeView() {
        contactorsTreeRoot = new TreeItem<>();
        contactorsTreeRoot.setValue(new PeonyBusinessContactorTreeItemData("Contactor List", PeonyTreeItemData.Status.INITIAL));
        contactorsTreeRoot.setGraphic(PeonyGraphic.getImageView("group.png"));
        contactorsTreeRoot.setExpanded(true);
        List<G02BusinessContactor> aG02BusinessContactorList = targetPeonyTaxcorpCase.getBusinessContactorList();
        if (aG02BusinessContactorList != null){
            String name;
            TreeItem<PeonyBusinessContactorTreeItemData> contactorTreeItem;
            for (G02BusinessContactor aG02BusinessContactor : aG02BusinessContactorList){
                if (!contactorUuidFilter.contains(aG02BusinessContactor.getBusinessContactorUuid())){
                    name = this.getContactorMapKey(aG02BusinessContactor); 
                    contactorTreeItem = contactorsMap.get(name);
                    if (contactorTreeItem == null){
                        contactorTreeItem = new TreeItem<>(new PeonyBusinessContactorTreeItemData(name, PeonyTreeItemData.Status.INITIAL));
                        contactorsMap.put(name, contactorTreeItem);
                        contactorsTreeRoot.getChildren().add(contactorTreeItem);
                    }
                    contactorTreeItem.getChildren().add(new TreeItem<>(new PeonyBusinessContactorTreeItemData(aG02BusinessContactor, PeonyTreeItemData.Status.INITIAL)));
                    contactorUuidFilter.add(aG02BusinessContactor.getBusinessContactorUuid());
                    contactorTreeItem.setExpanded(true);
                }
            }//for
        }
        contactorsTreeView.setRoot(contactorsTreeRoot);
        
        initializeContactorsTreeContextMenu();
    }

    private void initializeContactorsTreeContextMenu() {
        ContextMenu messageTreeViewContextMenu = new ContextMenu();
        //view
        MenuItem editMenuItem = new MenuItem("View & Edit");
        editMenuItem.setOnAction((ActionEvent e) -> {
            G02BusinessContactor aG02BusinessContactor = getSelectedContactor();
            if (aG02BusinessContactor == null){
                PeonyFaceUtils.displayErrorMessageDialog("Please select the item which contains contactor information.");
            }else{
                TaxcorpPersonnelContactorDialog aTaxcorpPersonnelContactorDialog = new TaxcorpPersonnelContactorDialog(null, true);
                aTaxcorpPersonnelContactorDialog.addPeonyFaceEventListener(this);
                aTaxcorpPersonnelContactorDialog.launchTaxcorpPersonnelContactorDialog("Taxcorp's Contrator", aG02BusinessContactor, targetPeonyTaxcorpCase);
            }
        });
        messageTreeViewContextMenu.getItems().add(editMenuItem);
        //sort
        MenuItem smsMenuItem = new MenuItem("SMS");
        smsMenuItem.setOnAction((ActionEvent e) -> {
            G02BusinessContactor aG02BusinessContactor = getSelectedContactor();
            if (aG02BusinessContactor == null){
                PeonyFaceUtils.displayErrorMessageDialog("Please select the item which contains contactor information.");
            }else{
                Lookup.getDefault().lookup(PeonyManagementService.class).popupSmsDialogForBusinessContactor(aG02BusinessContactor);
            }
        });
        messageTreeViewContextMenu.getItems().add(smsMenuItem);
        //Delete
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction((ActionEvent e) -> {
            G02BusinessContactor aG02BusinessContactor = getSelectedContactor();
            if (aG02BusinessContactor == null){
                PeonyFaceUtils.displayErrorMessageDialog("Please select the item which contains contactor information.");
            }else{
                deleteBusinessContactor(aG02BusinessContactor);
            }
        });
        messageTreeViewContextMenu.getItems().add(deleteMenuItem);
        //Hook the tree view
        contactorsTreeView.setContextMenu(messageTreeViewContextMenu);
    }
    
    private void deleteBusinessContactor(final G02BusinessContactor aG02BusinessContactor) {
        String msg;
        if (BusinessContactorRole.TAXCORP_OWNER.value().equalsIgnoreCase(aG02BusinessContactor.getRole())){
            msg = "Are you sure to delete this taxcorp's owner? A taxcorp case demands at least one taxcorp owner.";
        }else{
            msg = "Are you sure to delete this contactor?";
        }
        if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, msg) != JOptionPane.YES_OPTION){
            return;
        }
        Task<G02BusinessContactor> removeBusinessContactorTask = new Task<G02BusinessContactor>(){
            @Override
            protected G02BusinessContactor call() throws Exception {
                try{
                    Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                        .deleteEntity_XML(G02BusinessContactor.class, 
                                        GardenRestParams.Business.deleteBusinessContactorRestParams(aG02BusinessContactor.getBusinessContactorUuid()));
                }catch (Exception ex){
                    updateMessage("Deletion failed. " + ex.getMessage());
                    return null;
                }
                return aG02BusinessContactor;
            }

            @Override
            protected void succeeded() {
                try {
                    G02BusinessContactor aG02BusinessContactor = get();
                    if (aG02BusinessContactor == null){
                        String msg = getMessage();
                        if (ZcaValidator.isNotNullEmpty(msg)){
                            PeonyFaceUtils.displayErrorMessageDialog(msg);
                        }
                    }else{
                        targetPeonyTaxcorpCase.getBusinessContactorList().remove(aG02BusinessContactor);
                        TreeItem<PeonyBusinessContactorTreeItemData> contactorTreeItem = contactorsMap.get(getContactorMapKey(aG02BusinessContactor));
                        if (contactorTreeItem == null){
                            return;
                        }
                        List<TreeItem<PeonyBusinessContactorTreeItemData>> items = contactorTreeItem.getChildren();
                        PeonyBusinessContactorTreeItemData contactorTreeItemData;
                        G02BusinessContactor contactor;
                        for (TreeItem<PeonyBusinessContactorTreeItemData> item : items){
                            if (item != null){
                                contactorTreeItemData = item.getValue();
                                contactor = contactorTreeItemData.getBusinessContactor();
                                if ((contactor != null) && (contactor.getBusinessContactorUuid().equalsIgnoreCase(aG02BusinessContactor.getBusinessContactorUuid()))){
                                    contactorTreeItem.getChildren().remove(item);
                                    contactorUuidFilter.remove(aG02BusinessContactor.getBusinessContactorUuid());
                                    break;
                                }
                            }
                        }//for-loop
                        if ((contactorTreeItem.getChildren() == null) || (contactorTreeItem.getChildren().isEmpty())){
                            contactorsTreeRoot.getChildren().remove(contactorTreeItem);
                        }
                        contactorsTreeView.refresh();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(removeBusinessContactorTask);
    }

    private G02BusinessContactor getSelectedContactor() {
        TreeItem<PeonyBusinessContactorTreeItemData> contactorTreeItem = contactorsTreeView.getSelectionModel().getSelectedItem();
        if (contactorTreeItem != null){
            PeonyBusinessContactorTreeItemData aPeonyBusinessContactorTreeItemData = contactorTreeItem.getValue();
            if (aPeonyBusinessContactorTreeItemData != null){
                return aPeonyBusinessContactorTreeItemData.getBusinessContactor();
            }
        }
        return null;
    }

}
