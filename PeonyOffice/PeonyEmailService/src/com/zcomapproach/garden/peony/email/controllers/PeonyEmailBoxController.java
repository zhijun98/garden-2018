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

package com.zcomapproach.garden.peony.email.controllers;

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.data.constant.GardenBooleanValue;
import com.zcomapproach.garden.email.GardenEmailBox;
import com.zcomapproach.garden.email.GardenEmailMessage;
import com.zcomapproach.garden.email.GardenEmailSerializer;
import com.zcomapproach.garden.email.data.GardenEmailFolderName;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.PeonyTopComponent;
import com.zcomapproach.garden.peony.email.data.PeonyEmailTreeItemData;
import com.zcomapproach.garden.peony.email.ViewEmailTopComponent;
import com.zcomapproach.garden.peony.email.data.EmailOperation;
import com.zcomapproach.garden.peony.email.data.PeonyEmailTreeItemDataContainer;
import com.zcomapproach.garden.peony.email.events.DeletePeonyEmailMessageEvent;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmail;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmailList;
import com.zcomapproach.garden.rest.GardenRestParams;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javax.mail.MessagingException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public abstract class PeonyEmailBoxController extends PeonyEmailServiceController implements PeonyFaceEventListener{
    
    /**
     * Data structure for messageTreeView to contain all the loaded messages
     */
    private final PeonyEmailTreeItemDataContainer treeItemDataContainer = new PeonyEmailTreeItemDataContainer();
    
    /**
     * The root of messageTreeView
     */
    private final TreeItem<PeonyEmailTreeItemData> messageTreeRoot = new TreeItem<>();
    
    /**
     * keep track of the sorting order of the email list in messageTreeView
     */
    private int lastSortingOrder = 1;
    
    /**
     * This initialization method should be invoked in the constructor
     * @param loadingFxmlFileNameWithoutExtension - define which GUI FXML file is used in the controller
     */
    protected void intializePeonyEmailBoxController(String loadingFxmlFileNameWithoutExtension){
        messageTreeRoot.setValue(new PeonyEmailTreeItemData(PeonyProperties.getSingleton().getCurrentLoginEmployee().getEmployeeInfo().getWorkEmail(), 
                PeonyEmailTreeItemData.EmailItemType.ROOT));
        messageTreeRoot.setGraphic(PeonyGraphic.getImageView("mailbox.png"));
        messageTreeRoot.setExpanded(true);
        
        setLoadingFxmlFileNameWithoutExtension(loadingFxmlFileNameWithoutExtension);
    }

    /**
     * According to emailTagUuid, it find the existing GardenEmailMessage in the current email panel
     * @param emailTagUuid
     * @return 
     */
    public GardenEmailMessage findGardenEmailMessageByEmailTagUuid(String emailTagUuid) {
        return treeItemDataContainer.findGardenEmailMessageByEmailTagUuid(emailTagUuid);
    }

    /**
     * According to offlineEmailUuid, it find the existing GardenEmailMessage in the current email panel
     * @param offlineEmailUuid
     * @return 
     */
    public GardenEmailMessage findGardenEmailMessageByOfflineEmailUuid(String offlineEmailUuid) {
        PeonyEmailTreeItemData aPeonyEmailTreeItemData = treeItemDataContainer.findPeonyEmailTreeItemDataByOfflineEmailUuid(offlineEmailUuid);
        if (aPeonyEmailTreeItemData != null){
            return aPeonyEmailTreeItemData.emitPeonyEmailMessage();
        }
        return null;
    }
    
    public void publishProcessingStatus(String status) {
        PeonyFaceUtils.publishMessageOntoOutputWindow(status);
    }

    public abstract GardenEmailBox getGardenEmailBox();
    
    /**
     * 
     * @param gardenEmailMessage - the message sent out
     * @param peonyOfflineEmail - the associated offline email record for gardenEmailMessage, which could be NULL for online mail box
     */
    public void sendGardenEmailMessage(final GardenEmailMessage gardenEmailMessage, final PeonyOfflineEmail peonyOfflineEmail){
        if (gardenEmailMessage == null){
            return;
        }
        try {
            getGardenEmailBox().sendGardenEmailMessage(gardenEmailMessage, PeonyProperties.getSingleton().getEmailSerializationFolder());
            if (peonyOfflineEmail != null){
                if (!gardenEmailMessage.getAttachmentFileMapData().isEmpty()){
                    peonyOfflineEmail.getOfflineEmail().setAttached(GardenBooleanValue.Yes.value());
                }
                recordOfflineEmail(gardenEmailMessage, peonyOfflineEmail);
            }
        } catch (MessagingException ex) {
            //Exceptions.printStackTrace(ex);
            String msg = "Failed to send message due to technical reasons." + ex.getMessage();
            PeonyFaceUtils.displayErrorMessageDialog(msg);
            PeonyFaceUtils.publishMessageOntoOutputWindow(msg);
        }
    }

    /**
     * After sendGardenEmailMessage by the mail box, its associated PeonyOfflineEmail may be recorded
     * @param gardenEmailMessage
     * @param peonyOfflineEmail 
     */
    protected abstract void recordOfflineEmail(GardenEmailMessage gardenEmailMessage, PeonyOfflineEmail peonyOfflineEmail);
    
    protected abstract void recordPeonyOfflineEmailList(PeonyOfflineEmailList aPeonyOfflineEmailList);
    
    public abstract void deletePeonyEmailMessage(final GardenEmailMessage aPeonyEmailMessage);
    
    public abstract void shutdownEmailServices();
    
    protected abstract TreeView<PeonyEmailTreeItemData> getMessageTreeView();
    
    public void initializePeonyEmailBoxPane(TreeView<PeonyEmailTreeItemData> messageTreeView, Button composeEmailButton, Button spamRuleButton) {
        spamRuleButton.setTooltip(new Tooltip("Create spam rules for mail-box..."));
        spamRuleButton.setGraphic(PeonyGraphic.getImageView("spam.png"));
        spamRuleButton.setOnAction((ActionEvent event) -> {
            PeonyFaceUtils.displayInformationMessageDialog("No implementation yet.");
            //Lookup.getDefault().lookup(PeonyEmailService.class).displaySpamRuleTopComponent();
        });
        
        messageTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        messageTreeView.setRoot(messageTreeRoot);   //hook the root
        
        initializeMessageTreeViewContextMenu(messageTreeView);
        
        initializePeonyEmailBox();
    }
    
    protected abstract void initializePeonyEmailBox();

    /**
     * Find the corresponding PeonyOfflineEmail instance for aPeonyEmailMessage
     * @param aPeonyEmailMessage
     * @return 
     */
    public PeonyOfflineEmail getAssociatePeonyOfflineEmail(GardenEmailMessage aPeonyEmailMessage) {
        return treeItemDataContainer.getAssociatePeonyOfflineEmail(aPeonyEmailMessage);
    }
    
    protected void initializeMessageTreeViewContextMenu(final TreeView<PeonyEmailTreeItemData> messageTreeView){
        ContextMenu messageTreeViewContextMenu = new ContextMenu();
        //view
        MenuItem viewMenuItem = new MenuItem(EmailOperation.VIEW_SELECTED_EMAILS.value());
        viewMenuItem.setOnAction((ActionEvent e) -> {
            openPeonyEmailMessageTabs(messageTreeView, false);
        });
        messageTreeViewContextMenu.getItems().add(viewMenuItem);
        //sort
        MenuItem sortMenuItem = new MenuItem(EmailOperation.SORT_EMAIL.value());
        sortMenuItem.setOnAction((ActionEvent e) -> {
            lastSortingOrder = -1*lastSortingOrder;
            sortLoadedEmailMessagesOnTreeView(messageTreeView, lastSortingOrder);
        });
        messageTreeViewContextMenu.getItems().add(sortMenuItem);
        //Delete
        MenuItem deleteMenuItem = new MenuItem(EmailOperation.DELETE_SELECTED_EMAILS.value());
        deleteMenuItem.setOnAction((ActionEvent e) -> {
            deletePeonyEmailMessages(messageTreeView);
        });
        messageTreeViewContextMenu.getItems().add(deleteMenuItem);
        //Hook the tree view
        messageTreeView.setContextMenu(messageTreeViewContextMenu);
    }

    private void sortLoadedEmailMessagesOnTreeView(TreeView<PeonyEmailTreeItemData> messageTreeView, int lastSortingOrder) {
        this.lastSortingOrder = lastSortingOrder;
        final List<TreeItem<PeonyEmailTreeItemData>> folderItemList = new ArrayList<>(messageTreeView.getRoot().getChildren());
        for (TreeItem<PeonyEmailTreeItemData> folderItem : folderItemList){
            FXCollections.sort(folderItem.getChildren(), new Comparator<TreeItem<PeonyEmailTreeItemData>>(){
                @Override
                public int compare(TreeItem<PeonyEmailTreeItemData> o1, TreeItem<PeonyEmailTreeItemData> o2) {
                    try{
                        return o1.getValue().getSortingEmailMessageUid().compareTo(o2.getValue().getSortingEmailMessageUid())*PeonyEmailBoxController.this.lastSortingOrder;
                    }catch(Exception ex){
                        return 0;
                    }
                }
            });
        }//for-loop
    }
    
    private void deletePeonyEmailMessages(TreeView<PeonyEmailTreeItemData> messageTreeView) {
        if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to delete selected email(s) permanently?") != JOptionPane.YES_OPTION){
            return;
        }
        ObservableList<TreeItem<PeonyEmailTreeItemData>> selectedItems = FXCollections.observableArrayList(messageTreeView.getSelectionModel().getSelectedItems());
        messageTreeView.getSelectionModel().clearSelection();
        if (selectedItems != null){
            for (TreeItem<PeonyEmailTreeItemData> selectedItem : selectedItems){
                deletePeonyEmailMessage(selectedItem.getValue().emitPeonyEmailMessage());
            }
        }
    }
    
    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof DeletePeonyEmailMessageEvent){
            deletePeonyEmailMessage(((DeletePeonyEmailMessageEvent)event).getGardenEmailMessage());
        }
    }
    
    protected void removeEmailMessageTreeItemFromFolder(final GardenEmailMessage aPeonyEmailMessage, 
                                                        final TreeItem<PeonyEmailTreeItemData> messageItem, 
                                                        final TreeItem<PeonyEmailTreeItemData> messageFolderItem)
    {
        if ((aPeonyEmailMessage == null) || (messageItem == null) || (messageFolderItem == null)){
            return;
        }
        if (Platform.isFxApplicationThread()){
            removeEmailMessageTreeItemFromFolderHelper(aPeonyEmailMessage, messageItem, messageFolderItem);
        }else{
            Platform.runLater(() -> {
                removeEmailMessageTreeItemFromFolderHelper(aPeonyEmailMessage, messageItem, messageFolderItem);
            });
        }
    }
    
    private void removeEmailMessageTreeItemFromFolderHelper(final GardenEmailMessage aGardenEmailMessage, 
                                                            final TreeItem<PeonyEmailTreeItemData> messageItem, 
                                                            final TreeItem<PeonyEmailTreeItemData> messageFolderItem)
    {
        //handle attachments
        if (messageItem.getChildren() != null){
            messageItem.getChildren().clear();
        }
        //remove from its folder root
        messageFolderItem.getChildren().remove(messageItem);
        //update folder root's title
        messageFolderItem.setValue(new PeonyEmailTreeItemData(aGardenEmailMessage.getFolderFullName() + " ("+messageFolderItem.getChildren().size()+")",
                PeonyEmailTreeItemData.EmailItemType.FOLDER));
    
        getMessageTreeView().refresh();
    }
    protected abstract void recordGardenEmailMessageFlagChanged(GardenEmailMessage aPeonyEmailMessage);
    
    protected void openPeonyEmailMessageTabs(TreeView<PeonyEmailTreeItemData> messageTreeView, boolean readOnly) {
        
        ObservableList<TreeItem<PeonyEmailTreeItemData>> selectedItems = messageTreeView.getSelectionModel().getSelectedItems();
        if (selectedItems != null){
            PeonyEmployee aPeonyEmployee = PeonyProperties.getSingleton().getCurrentLoginEmployee();
            //find the PeonyEmailMessage instance
            GardenEmailMessage aGardenEmailMessage;
            int thresholdForOpen = 5;
            int count = 0;
            for (TreeItem<PeonyEmailTreeItemData> selectedItem : selectedItems){
                if ((count++) > thresholdForOpen){
                    break;
                }
                aGardenEmailMessage = selectedItem.getValue().emitPeonyEmailMessage();
                if (aGardenEmailMessage != null){
                    //flag SEEN
                    if (!aGardenEmailMessage.isSeenFlag()){
                        //save seenFlag
                        if (!aGardenEmailMessage.isSeenFlag()){
                            aGardenEmailMessage.setSeenFlag(true);
                            GardenEmailSerializer.getSingleton().serializeGardenEmailMessage(aPeonyEmployee.getAccount().getAccountUuid(), 
                                                                             aPeonyEmployee.getWorkEmail(), aGardenEmailMessage, 
                                                                             PeonyProperties.getSingleton().getEmailSerializationFolder(), null);
                        }
                        //Cannot change assignment-task-email
                        if (aGardenEmailMessage.isSentMessage()){
                            selectedItem.setGraphic(new ImageView(PeonyGraphic.getJavaFxImage("sent_msg.png")));
                        }else if (aGardenEmailMessage.isTaskMessage()){
                            selectedItem.setGraphic(new ImageView(PeonyGraphic.getJavaFxImage("urgent_msg.png")));
                        }else{
                            selectedItem.setGraphic(new ImageView(PeonyGraphic.getJavaFxImage("checked_msg.png")));
                        }
                        //brute-force search the attachment tree-item...
                        String attachmentsText = aGardenEmailMessage.retrieveAttachmentFileNames();
                        for (TreeItem<PeonyEmailTreeItemData> aPeonyEmailTreeItemDataItem : selectedItem.getChildren()){
                            if (attachmentsText.equalsIgnoreCase(aPeonyEmailTreeItemDataItem.getValue().getUuid())){
                                aPeonyEmailTreeItemDataItem.setGraphic(new ImageView(PeonyGraphic.getJavaFxImage("attach_green.png")));
                                break;
                            }
                        }//for
                    }
                    //open the corresponding tab
                    openPeonyEmailMessageTabByEDT(aGardenEmailMessage, getAssociatePeonyOfflineEmail(aGardenEmailMessage), readOnly);
                }
            }//for-loop
        }
    }
    
    public void openPeonyEmailMessageTabByEDT(final GardenEmailMessage aPeonyEmailMessage, final PeonyOfflineEmail aPeonyOfflineEmail, final boolean readOnly) {
        if ((aPeonyEmailMessage == null) || (aPeonyOfflineEmail == null)){
            PeonyFaceUtils.displayErrorMessageDialog("Cannot dispay the email message window.");
            return;
        }
        if (SwingUtilities.isEventDispatchThread()){
            openPeonyEmailMessageTabByEDTHelper(aPeonyEmailMessage, aPeonyOfflineEmail, readOnly);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    openPeonyEmailMessageTabByEDTHelper(aPeonyEmailMessage, aPeonyOfflineEmail, readOnly);
                }
            });
        }
    }
    
    private void openPeonyEmailMessageTabByEDTHelper(final GardenEmailMessage aPeonyEmailMessage, final PeonyOfflineEmail aPeonyOfflineEmail, final boolean readOnly) {
        PeonyTopComponent aPeonyTopComponent = PeonyFaceUtils.findPeonyTopComponentFromEditorZone(aPeonyEmailMessage.getFolderFullName() + aPeonyEmailMessage.getEmailMsgUid());
        ViewEmailTopComponent aViewEmailTopComponent= null;
        if (aPeonyTopComponent instanceof ViewEmailTopComponent){
            aViewEmailTopComponent = (ViewEmailTopComponent)aPeonyTopComponent;
        }
        
        if (aViewEmailTopComponent == null){
            aViewEmailTopComponent = new ViewEmailTopComponent();
            aViewEmailTopComponent.addPeonyFaceEventListener(this);
            aViewEmailTopComponent.launchViewEmailTopComponent(aPeonyEmailMessage, aPeonyOfflineEmail, readOnly);
        }else{
            aViewEmailTopComponent.open();
            aViewEmailTopComponent.requestActive();
        }
        aViewEmailTopComponent.setDeleteButtonEnabled(true);
    }
    
    protected PeonyEmailTreeItemDataContainer getTreeItemDataContainer(){
        return treeItemDataContainer;
    }
    
    /**
     * This method handle this case: users request to create a new folder on the email tree.
     * @param messageTreeView
     * @param mailFolderName 
     */
    protected void handleCreateEmailFolderTreeItemRequest(final TreeView<PeonyEmailTreeItemData> messageTreeView, final String mailFolderName){
        Task<TreeItem<PeonyEmailTreeItemData>> createEmailFolderNameTask = new Task<TreeItem<PeonyEmailTreeItemData>>(){
            /**
             * 
             * @return - a TreeView<PeonyEmailTreeItemData> instance which represents aGardenEmailMessage
             * @throws Exception 
             */
            @Override
            protected TreeItem<PeonyEmailTreeItemData> call() throws Exception {
                List<TreeItem<PeonyEmailTreeItemData>> result = new ArrayList<>();
                PeonyEmailTreeItemDataContainer treeItemDataContainer = getTreeItemDataContainer();
                /**
                 * Get the folder-tree-item
                 */
                TreeItem<PeonyEmailTreeItemData> messageFolderItem;
                if (ZcaValidator.isNullEmpty(mailFolderName)){
                    messageFolderItem = treeItemDataContainer.constructEmailMessageFolderTreeItem(GardenEmailFolderName.GARDEN_INBOX.value());
                }else{
                    messageFolderItem = treeItemDataContainer.findEmailMessageFolder(mailFolderName);
                    if (messageFolderItem == null){
                        messageFolderItem = treeItemDataContainer.constructEmailMessageFolderTreeItem(mailFolderName);
                    }
                }
                return messageFolderItem;
            }

            @Override
            protected void failed() {
                /**
                 * todo zzj: how to handle this error?
                 */
            }

            @Override
            protected void succeeded() {
                try {
                    TreeItem<PeonyEmailTreeItemData> messageFolderItem = get();
                    //add the folder onto the tree root
                    if (!messageTreeView.getRoot().getChildren().contains(messageFolderItem)){
                        String folderName = messageFolderItem.getValue().toString();
                        messageTreeView.getRoot().getChildren().add(0, messageFolderItem); 
                        messageFolderItem.setValue(new PeonyEmailTreeItemData(folderName + " ("+messageFolderItem.getChildren().size()+")", 
                                PeonyEmailTreeItemData.EmailItemType.FOLDER));
                    }
                    /**
                     * Email tree
                     */
                    messageTreeView.refresh();
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(createEmailFolderNameTask);
    }

    protected void displayMoveToFolderDialog(final List<TreeItem<PeonyEmailTreeItemData>> movingItems) {
        if (Platform.isFxApplicationThread()){
            displayMoveToFolderDialogHelper(movingItems);
        }else{
            Platform.runLater(() -> {
                displayMoveToFolderDialogHelper(movingItems);
            });
        }
    }
    
    /**
     * Refer to moveMessageItemsFromFolderToFolder
     * @param movingItems 
     */
    private void displayMoveToFolderDialogHelper(final List<TreeItem<PeonyEmailTreeItemData>> movingItems) {
        List<String> dialogData = treeItemDataContainer.getEmailFolderNameListForMove();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(dialogData.get(0), dialogData);
        dialog.setTitle("Select folder");
        dialog.setHeaderText("Which folder should the selected emails move to:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            final String targetEmailFolder = result.get();
            
            if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to move your selected emails to "+targetEmailFolder+"?") != JOptionPane.YES_OPTION){
                return;
            }
            moveEmailItemsToFolder(movingItems, targetEmailFolder, null);
        }
    }
    
    /**
     * 
     * @param movingItems
     * @param targetEmailFolder
     * @param deletedOriginalEmailFolderItemList - after move movingItems into targetEmailFolder, whether or not delete the folders in deletedOriginalEmailFolderList
     */
    private void moveEmailItemsToFolder(List<TreeItem<PeonyEmailTreeItemData>> movingItems, String targetEmailFolder, List<TreeItem<PeonyEmailTreeItemData>> deletedOriginalEmailFolderItemList){
        Task<List<TreeItem<PeonyEmailTreeItemData>>> moveToFolderTask = new Task<List<TreeItem<PeonyEmailTreeItemData>>>(){
            @Override
            protected List<TreeItem<PeonyEmailTreeItemData>> call() throws Exception {
                List<TreeItem<PeonyEmailTreeItemData>> resultMovingItems = new ArrayList<>();
                PeonyOfflineEmail aPeonyOfflineEmail;
                PeonyOfflineEmailList aPeonyOfflineEmailList = new PeonyOfflineEmailList();
                for (TreeItem<PeonyEmailTreeItemData> movingItem : movingItems){
                    if (movingItem.getValue() != null){
                        aPeonyOfflineEmail = movingItem.getValue().getAssociatedPeonyOfflineEmail();
                        if (aPeonyOfflineEmail != null){
                            aPeonyOfflineEmail.getOfflineEmail().setMessageFaceFolder(targetEmailFolder);
                            aPeonyOfflineEmailList.getPeonyOfflineEmailList().add(aPeonyOfflineEmail);
                            resultMovingItems.add(movingItem);
                        }
                    }
                }
                if (aPeonyOfflineEmailList.getPeonyOfflineEmailList().isEmpty()){
                    updateMessage("No valid email item ready for move");
                }else{
                    try {
                        Lookup.getDefault().lookup(PeonyManagementService.class).getPeonyManagementRestClient().storeEntity_XML(
                                PeonyOfflineEmailList.class, GardenRestParams.Management.storePeonyOfflineEmailListRestParams(), aPeonyOfflineEmailList);
                    } catch (Exception ex) {
                        //Exceptions.printStackTrace(ex);
                        updateMessage("Cannot move to the folder for technical errors. " + ex.getMessage());
                        resultMovingItems.clear();
                    }
                }
                return resultMovingItems;
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayErrorMessageDialog("Operation failed. " + getMessage());
            }

            @Override
            protected void succeeded() {
                try {
                    List<TreeItem<PeonyEmailTreeItemData>> resultMovingItems = get();
                    TreeItem<PeonyEmailTreeItemData> toFolder;
                    List<TreeItem<PeonyEmailTreeItemData>> toItems;
                    TreeItem<PeonyEmailTreeItemData> fromFolder;
                    List<TreeItem<PeonyEmailTreeItemData>> fromItems;
                    for (TreeItem<PeonyEmailTreeItemData> movingItem : resultMovingItems){
                        toFolder = treeItemDataContainer.findEmailMessageFolder(targetEmailFolder);
                        toItems = toFolder.getChildren();
                        fromFolder = movingItem.getParent();
                        fromItems = fromFolder.getChildren();
                        //move...
                        fromItems.remove(movingItem);
                        toItems.add(movingItem);
                    }//for
                    PeonyFaceUtils.displayInformationMessageDialog(getMessage());
                    
                    //delete the empty folders which are requested by the caller
                    if (deletedOriginalEmailFolderItemList != null){
                        for (TreeItem<PeonyEmailTreeItemData> deletedFolderItem : deletedOriginalEmailFolderItemList){
                            if (deletedFolderItem != null){
                                messageTreeRoot.getChildren().remove(deletedFolderItem);
                            }
                        }
                    }
                    
                } catch (InterruptedException | ExecutionException ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Operation failed. " + ex.getMessage());
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(moveToFolderTask);
    }

    protected void displayFolderDeletionDialog() {
        if (Platform.isFxApplicationThread()){
            displayFolderDeletionDialogHelper();
        }else{
            Platform.runLater(() -> {
                displayFolderDeletionDialogHelper();
            });
        }
    }
    
    private void displayFolderDeletionDialogHelper() {
        List<String> dialogData = treeItemDataContainer.getEmailFolderNameListForDeletion();
        if (dialogData.isEmpty()){
            PeonyFaceUtils.displayErrorMessageDialog("No deletable folder.");
            return;
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>(dialogData.get(0), dialogData);
        dialog.setTitle("Delete folder");
        dialog.setHeaderText("Which folder should be deleted?");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            final String targetEmailFolder = result.get();
            GardenEmailFolderName aGardenEmailFolderName = GardenEmailFolderName.convertStringToType(targetEmailFolder);
            if (!GardenEmailFolderName.UNKNOWN.equals(aGardenEmailFolderName)){
                PeonyFaceUtils.displayErrorMessageDialog("Cannot delete the reserved folder.");
                return;
            }
            if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to delete your selected folder? Its emails will be moved to Inbox folder after folder deletion") != JOptionPane.YES_OPTION){
                return;
            }
            
            TreeItem<PeonyEmailTreeItemData> deletedFolderItem = treeItemDataContainer.findEmailMessageFolder(targetEmailFolder);
            if (deletedFolderItem != null){
                List<TreeItem<PeonyEmailTreeItemData>> deletedFolderItemList = new ArrayList<>();
                deletedFolderItemList.add(deletedFolderItem);
                moveEmailItemsToFolder(deletedFolderItem.getChildren(), GardenEmailFolderName.GARDEN_INBOX.value(), deletedFolderItemList);
            }
        }
    }

    public abstract void displayAssignEmailDialog(String emailTagUuid);
}
