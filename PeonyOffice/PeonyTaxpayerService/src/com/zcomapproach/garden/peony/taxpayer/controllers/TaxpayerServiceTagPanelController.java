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

package com.zcomapproach.garden.peony.taxpayer.controllers;

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.peony.kernel.services.PeonyBusinessService;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.taxpayer.dialogs.TaxpayerServiceTagProfileDialog;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.data.PeonyDocumentTagTreeItemData;
import com.zcomapproach.garden.peony.view.data.PeonyTreeItemData;
import com.zcomapproach.garden.peony.view.events.PeonyDocumentTagReadyForDelete;
import com.zcomapproach.garden.peony.view.events.PeonyDocumentTagReadyForSave;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.entity.G02DocumentTag;
import com.zcomapproach.garden.persistence.peony.PeonyDocumentTag;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.garden.persistence.peony.data.PeonyPredefinedDocumentTag;
import com.zcomapproach.garden.persistence.peony.data.PeonyPredefinedDocumentTagType;
import com.zcomapproach.garden.rest.GardenRestParams;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class TaxpayerServiceTagPanelController extends PeonyTaxpayerServiceController implements PeonyFaceEventListener {
    @FXML
    private Button updateTagsButton;
    @FXML
    private TreeView<PeonyDocumentTagTreeItemData> fileTagTreeView;
    
    //private TreeItem<PeonyDocumentTagTreeItemData> fileTagTreeRootItem;
    //private final HashMap<String, TreeItem<PeonyDocumentTagTreeItemData>> fileTagTreeTypeItemStorage = new HashMap<>();
    
    private final PeonyTaxpayerCase targetPeonyTaxpayerCase;
    
    public TaxpayerServiceTagPanelController(PeonyTaxpayerCase targetPeonyTaxpayerCase) {
        super(targetPeonyTaxpayerCase);
        this.targetPeonyTaxpayerCase = targetPeonyTaxpayerCase;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        updateTagsButton.setGraphic(PeonyGraphic.getImageView("note_edit.png"));
        updateTagsButton.setOnAction((ActionEvent actionEvent) -> {
            displayTaxpayerServiceTagProfileDialog();
        });
        
        initializeFileTagTreeView();
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof PeonyDocumentTagReadyForSave){
            handlePeonyDocumentTagReadyForSave((PeonyDocumentTagReadyForSave)event);
        }else if (event instanceof PeonyDocumentTagReadyForDelete){
            handlePeonyDocumentTagReadyForDelete((PeonyDocumentTagReadyForDelete)event);
        }
    }
    
    private void handlePeonyDocumentTagReadyForSave(final PeonyDocumentTagReadyForSave event) {
        if (Platform.isFxApplicationThread()){
            handlePeonyDocumentTagReadyForSaveHelper(event);
        }else{
            Platform.runLater(() -> {
                handlePeonyDocumentTagReadyForSaveHelper(event);
            });
        }
    }
    
    private void handlePeonyDocumentTagReadyForSaveHelper(PeonyDocumentTagReadyForSave event) {
        if (event.getPeonyDocumentTag() != null){
            final PeonyDocumentTag aPeonyDocumentTag = event.getPeonyDocumentTag();
            if (aPeonyDocumentTag == null){
                return;
            }
            aPeonyDocumentTag.getDocumentTag().setFileUuid(getTargetEntityUuid());
            Task<PeonyDocumentTag> savePeonyDocumentTagTask = new Task<PeonyDocumentTag>(){
                @Override
                protected PeonyDocumentTag call() throws Exception {
                    PeonyDocumentTag tag = Lookup.getDefault().lookup(PeonyBusinessService.class)
                        .getPeonyBusinessRestClient().storeEntity_XML(PeonyDocumentTag.class, 
                                                                      GardenRestParams.Business.storePeonyDocumentTagRestParams(), 
                                                                      aPeonyDocumentTag);
                    if ((tag != null) && (tag.getDocumentTag() != null)){
                        if (getDocumentTagContainedByTarget(tag.getDocumentTag().getDocumentTagUuid()) == null){
                            targetPeonyTaxpayerCase.getDocumentTagList().add(tag.getDocumentTag());
                        }
                    }
                    return tag;
                }

                @Override
                protected void succeeded() {
                    try {
                        PeonyDocumentTag tag = get();
                        TreeItem<PeonyDocumentTagTreeItemData> fileTagTreeRootItem = fileTagTreeView.getRoot();
                        fileTagTreeRootItem.getChildren().clear();
                        List<G02DocumentTag> aG02DocumentTagList = sortDocumentTagList(targetPeonyTaxpayerCase.getDocumentTagList());
                        TreeItem<PeonyDocumentTagTreeItemData> typeTreeItem;
                        for (G02DocumentTag aG02DocumentTag : aG02DocumentTagList){
                            typeTreeItem = getServiceTagTypeTreeItem(fileTagTreeRootItem, aG02DocumentTag.getDocumentTagType());
                            if (typeTreeItem != null){
                                typeTreeItem.getChildren().add(new TreeItem<>(new PeonyDocumentTagTreeItemData(new PeonyDocumentTag(aG02DocumentTag), null, PeonyTreeItemData.Status.INITIAL)));
                            }
                        }
                        fileTagTreeRootItem.setExpanded(true);
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            };
            getCachedThreadPoolExecutorService().submit(savePeonyDocumentTagTask);
        }
    }
    
        
    private void handlePeonyDocumentTagReadyForDelete(final PeonyDocumentTagReadyForDelete event) {
        if (Platform.isFxApplicationThread()){
            handlePeonyDocumentTagReadyForDeleteHelper(event);
        }else{
            Platform.runLater(() -> {
                handlePeonyDocumentTagReadyForDeleteHelper(event);
            });
        }
    }
    
    private void handlePeonyDocumentTagReadyForDeleteHelper(PeonyDocumentTagReadyForDelete event) {
        if (event.getPeonyDocumentTag() != null){
            final PeonyDocumentTag aPeonyDocumentTag = event.getPeonyDocumentTag();
            if (aPeonyDocumentTag == null){
                return;
            }
            Task<PeonyDocumentTag> deletePeonyDocumentTagTask = new Task<PeonyDocumentTag>(){
                @Override
                protected PeonyDocumentTag call() throws Exception {
                    return Lookup.getDefault().lookup(PeonyBusinessService.class)
                        .getPeonyBusinessRestClient().deleteEntity_XML(PeonyDocumentTag.class, 
                                GardenRestParams.Business.deletePeonyDocumentTagRestParams(aPeonyDocumentTag.getDocumentTag().getDocumentTagUuid()));
                }

                @Override
                protected void succeeded() {
                    try {
                        PeonyDocumentTag aPeonyDocumentTag = get();
                        if (aPeonyDocumentTag != null){
                            G02DocumentTag aG02DocumentTag = getDocumentTagContainedByTarget(aPeonyDocumentTag.getDocumentTag().getDocumentTagUuid());
                            if (aG02DocumentTag != null){
                                targetPeonyTaxpayerCase.getDocumentTagList().remove(aG02DocumentTag);
                                removeFileTagTreeItemFromRoot(aG02DocumentTag.getDocumentTagUuid());
                            }
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            
            };
            getCachedThreadPoolExecutorService().submit(deletePeonyDocumentTagTask);
        }
    }
    
    private void initializeFileTagTreeView(){
        
        TreeItem<PeonyDocumentTagTreeItemData> fileTagTreeRootItem = new TreeItem<>(new PeonyDocumentTagTreeItemData("Service Tags:"));
        List<G02DocumentTag> aG02DocumentTagList = sortDocumentTagList(targetPeonyTaxpayerCase.getDocumentTagList());
        TreeItem<PeonyDocumentTagTreeItemData> typeTreeItem;
        for (G02DocumentTag aG02DocumentTag : aG02DocumentTagList){
            typeTreeItem = getServiceTagTypeTreeItem(fileTagTreeRootItem, aG02DocumentTag.getDocumentTagType());
            if (typeTreeItem != null){
                typeTreeItem.getChildren().add(new TreeItem<>(new PeonyDocumentTagTreeItemData(new PeonyDocumentTag(aG02DocumentTag), null, PeonyTreeItemData.Status.INITIAL)));
            }
        }
        fileTagTreeRootItem.setExpanded(true);
        fileTagTreeView.setRoot(fileTagTreeRootItem);
        
        ContextMenu aContextMenu = new ContextMenu();
        
        initializeAddServiceTagMenu(aContextMenu);
        
        MenuItem aMenuItem = new MenuItem("Edit Service Tag");
        aMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                displayTaxpayerServiceTagProfileDialog();
            }
        });
        aContextMenu.getItems().add(aMenuItem);
        
        aMenuItem = new MenuItem("Delete Service Tag");
        aMenuItem.setOnAction((ActionEvent e) -> {
////            try{
////                if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to delete this file tag?") == JOptionPane.YES_OPTION){
////                    handlePeonyDocumentTagReadyForDelete(new PeonyDocumentTagReadyForDelete(fileTagTreeView.getSelectionModel().getSelectedItem().getValue().getTargetPeonyDocumentTag()));
////                }
////            }catch (Exception ex){
////                    PeonyFaceUtils.displayErrorMessageDialog("Please select a tag for deletion.");
////            }
            displayTaxpayerServiceTagProfileDialog();
        });
        aContextMenu.getItems().add(aMenuItem);
        fileTagTreeView.setContextMenu(aContextMenu);
    }

    private TreeItem<PeonyDocumentTagTreeItemData>  getServiceTagTypeTreeItem(TreeItem<PeonyDocumentTagTreeItemData> fileTagTreeRootItem, String tagType){
        if (ZcaValidator.isNullEmpty(tagType)){
            return fileTagTreeRootItem;
        }
        TreeItem<PeonyDocumentTagTreeItemData> typeTreeItem = findTagTypeTreeItem(fileTagTreeRootItem, tagType);
        if (typeTreeItem == null){
            typeTreeItem = new TreeItem<>(new PeonyDocumentTagTreeItemData(tagType));
            fileTagTreeRootItem.getChildren().add(typeTreeItem);
        }
        typeTreeItem.setExpanded(true);
        return typeTreeItem;
    }
    
    private TreeItem<PeonyDocumentTagTreeItemData> findTagTypeTreeItem (TreeItem<PeonyDocumentTagTreeItemData> fileTagTreeRootItem, String tagType){
        List<TreeItem<PeonyDocumentTagTreeItemData>> items = fileTagTreeRootItem.getChildren();
        for (TreeItem<PeonyDocumentTagTreeItemData> item : items){
            if (tagType.equalsIgnoreCase(item.getValue().getTreeItemTitle())){
                return item;
            }
        }
        return null;
    }
    
    private TreeItem<PeonyDocumentTagTreeItemData> findFileTagItemFromTreeViewByTagName(String tagName) {
        TreeItem<PeonyDocumentTagTreeItemData> fileTagTreeRootItem = fileTagTreeView.getRoot();
        if (ZcaValidator.isNotNullEmpty(tagName)){
            List<TreeItem<PeonyDocumentTagTreeItemData>> items = fileTagTreeRootItem.getChildren();
            List<TreeItem<PeonyDocumentTagTreeItemData>> subItems;
            PeonyDocumentTag aPeonyDocumentTag;
            for (TreeItem<PeonyDocumentTagTreeItemData> item : items){
                aPeonyDocumentTag = item.getValue().getTargetPeonyDocumentTag();
                if (aPeonyDocumentTag == null){
                    subItems = item.getChildren();
                    for (TreeItem<PeonyDocumentTagTreeItemData> subItem : subItems){
                        aPeonyDocumentTag = subItem.getValue().getTargetPeonyDocumentTag();
                        if (tagName.equalsIgnoreCase(aPeonyDocumentTag.getDocumentTag().getDocumentTagName())){
                            return subItem; //find it
                        }
                    }//for-loop
                }else{
                    if (tagName.equalsIgnoreCase(aPeonyDocumentTag.getDocumentTag().getDocumentTagName())){
                        return item; //find it
                    }
                }
            }//for-loop
        }
        return null;
    }
    
    private TreeItem<PeonyDocumentTagTreeItemData> removeFileTagTreeItemFromRootHelper(TreeItem<PeonyDocumentTagTreeItemData> fileTagTreeRootItem, String documentTagUuid) {
        TreeItem<PeonyDocumentTagTreeItemData> culprit = null;
        if (ZcaValidator.isNotNullEmpty(documentTagUuid)){
            List<TreeItem<PeonyDocumentTagTreeItemData>> items = fileTagTreeRootItem.getChildren();
            List<TreeItem<PeonyDocumentTagTreeItemData>> subItems;
            PeonyDocumentTag aPeonyDocumentTag;
            for (TreeItem<PeonyDocumentTagTreeItemData> item : items){
                aPeonyDocumentTag = item.getValue().getTargetPeonyDocumentTag();
                if (aPeonyDocumentTag == null){
                    subItems = item.getChildren();
                    for (TreeItem<PeonyDocumentTagTreeItemData> subItem : subItems){
                        aPeonyDocumentTag = subItem.getValue().getTargetPeonyDocumentTag();
                        if (documentTagUuid.equalsIgnoreCase(aPeonyDocumentTag.getDocumentTag().getDocumentTagUuid())){
                            culprit = subItem;
                            break;
                        }
                    }//for-loop
                    if (culprit != null){
                        subItems.remove(culprit);
                        break;
                    }
                }else{
                    if (documentTagUuid.equalsIgnoreCase(aPeonyDocumentTag.getDocumentTag().getDocumentTagUuid())){
                        culprit = item;
                        break;
                    }
                }
            }//for-loop
        }
        return culprit;
    }
    
    private void removeFileTagTreeItemFromRoot(String documentTagUuid) {
        TreeItem<PeonyDocumentTagTreeItemData> fileTagTreeRootItem = fileTagTreeView.getRoot();
        TreeItem<PeonyDocumentTagTreeItemData> culprit = removeFileTagTreeItemFromRootHelper(fileTagTreeRootItem, documentTagUuid);
        if (culprit != null){
            fileTagTreeRootItem.getChildren().remove(culprit);
        }
        fileTagTreeView.refresh();
    }
    
    private G02DocumentTag getDocumentTagContainedByTarget(String documentTagUuid) {
        List<G02DocumentTag> aG02DocumentTagList = targetPeonyTaxpayerCase.getDocumentTagList();
        for (G02DocumentTag aG02DocumentTag : aG02DocumentTagList){
            if (aG02DocumentTag.getDocumentTagUuid().equalsIgnoreCase(documentTagUuid)){
                return aG02DocumentTag;
            }
        }
        return null;
    }
    
    private void initializeAddServiceTagMenu(ContextMenu aContextMenu) {
        List<PeonyPredefinedDocumentTagType> tageTypes = PeonyPredefinedDocumentTagType.getPeonyPredefinedDocumentTagTypeListByOrder();
        List<PeonyPredefinedDocumentTag> tags; 
        Menu typeMenu = new Menu("Add Service Tag");
        Menu aMenu;
        MenuItem aMenuItem;
        for (PeonyPredefinedDocumentTagType tagType : tageTypes){
            aMenu = new Menu(tagType.value());
            tags = PeonyPredefinedDocumentTag.getPeonyPredefinedDocumentTagListByOrder(tagType);
            for (PeonyPredefinedDocumentTag tag : tags){
                aMenuItem = new MenuItem(tag.value());
                aMenuItem.setOnAction((ActionEvent e) -> {
                    /**
                     * if it has been in the tree, it simply returns
                     */
                    if (findFileTagItemFromTreeViewByTagName(tag.value()) != null){
                        PeonyFaceUtils.displayWarningMessageDialog("This service tag exists in the list.");
                        return;
                    }
                    handlePeonyDocumentTagReadyForSave(new PeonyDocumentTagReadyForSave(PeonyPredefinedDocumentTag.createPredefinedPeonyDocumentTag(tag)));
                });
                aMenu.getItems().add(aMenuItem);
            }
            typeMenu.getItems().add(aMenu);
        }
        aContextMenu.getItems().add(typeMenu);
    }

    private void displayTaxpayerServiceTagProfileDialog() {
        TaxpayerServiceTagProfileDialog aTaxpayerServiceTagProfileDialog = new TaxpayerServiceTagProfileDialog(null, true);
        aTaxpayerServiceTagProfileDialog.addPeonyFaceEventListener(this);
        aTaxpayerServiceTagProfileDialog.launchTaxpayerServiceTagEditorDialog("Edit Service Tags", targetPeonyTaxpayerCase);
    }
}
