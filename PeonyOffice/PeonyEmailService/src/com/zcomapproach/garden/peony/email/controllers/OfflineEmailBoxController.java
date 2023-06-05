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
import com.zcomapproach.garden.email.GardenEmailBox;
import com.zcomapproach.garden.email.GardenEmailMessage;
import com.zcomapproach.garden.email.OfflineEmailBoxListener;
import com.zcomapproach.garden.email.data.GardenEmailFolderName;
import com.zcomapproach.garden.peony.email.data.PeonyEmailTreeItemData;
import com.zcomapproach.garden.peony.email.data.EmailOperation;
import com.zcomapproach.garden.peony.email.data.UserLocalEmailBox;
import com.zcomapproach.garden.peony.email.dialogs.AssignEmailDialog;
import com.zcomapproach.garden.peony.email.dialogs.WriteNotesEmailDialog;
import com.zcomapproach.garden.peony.email.events.PeonyEmailTagListCreated;
import com.zcomapproach.garden.peony.email.events.PeonyMemoListSaved;
import com.zcomapproach.garden.email.data.OfflineMessageStatus;
import com.zcomapproach.garden.guard.TechnicalController;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.email.data.PeonyEmailMessageType;
import com.zcomapproach.garden.peony.email.data.PeonyEmailTreeItemDataContainer;
import com.zcomapproach.garden.peony.kernel.services.PeonyEmailService;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.settings.PeonySpamRuleManager;
import com.zcomapproach.garden.peony.utils.PeonyDataUtils;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.peony.PeonyEmailTag;
import com.zcomapproach.garden.persistence.peony.PeonyEmailTagList;
import com.zcomapproach.garden.persistence.peony.PeonyMemo;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmail;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmailList;
import com.zcomapproach.garden.persistence.peony.data.PeonyMemoList;
import com.zcomapproach.garden.persistence.peony.data.QueryNewOfflineEmailCriteria;
import com.zcomapproach.garden.rest.GardenRestParams;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javax.mail.MessagingException;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class OfflineEmailBoxController extends PeonyEmailBoxController implements OfflineEmailBoxListener, PeonyFaceEventListener{
    @FXML
    private TreeView<PeonyEmailTreeItemData> messageTreeView;
    
    @FXML
    private Button composeEmailButton;
    
    @FXML
    private Button spamRuleButton;
    
    @FXML
    private ComboBox<String> emailTypeComboBox;
    
    @FXML
    private ComboBox<String> batchThresholdComboBox;
    
    private UserLocalEmailBox offlineEmailBox;
    
    private final HashMap<String, TreeItem<PeonyEmailTreeItemData>> tagPublishedStorage = new HashMap<>();
    
    public OfflineEmailBoxController() {
        super.intializePeonyEmailBoxController("PeonyEmailBoxPane");
    }

    @Override
    public GardenEmailBox getGardenEmailBox() {
        return offlineEmailBox;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializePeonyEmailBoxPane(messageTreeView, composeEmailButton, spamRuleButton);
        
        messageTreeView.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() == 2){
                    TreeItem<PeonyEmailTreeItemData> item = messageTreeView.getSelectionModel().getSelectedItem();
                    PeonyEmailTreeItemData itemData = item.getValue();
                    if (itemData != null){
                        switch (itemData.getEmailItemType()){
                            case EMAIL:
                                openPeonyEmailMessageTabs(messageTreeView, false);
                                break;
                            case ATTACHEMNT:
                                messageTreeView.getSelectionModel().select(item.getParent());
                                openPeonyEmailMessageTabs(messageTreeView, false);
                                break;
                            case NOTE:
                                displayWriteNotesEmailDialogForNote(item);
                                break;
                            case TAG:
                                displayAssignEmailDialogForTag(item);
                                break;
                        }
                    }
                }
            }
        });
        
        PeonyFaceUtils.initializeComboBox(emailTypeComboBox, 
                PeonyEmailMessageType.getEnumValueList(false), 
                PeonyEmailMessageType.ALL.value(), 
                PeonyEmailMessageType.ALL.value(), 
                "Display a specific type of messages...", false, this);
        
        ArrayList<String> thresholdValues = new ArrayList<>();
        thresholdValues.add(""+100);
        thresholdValues.add(""+250);
        thresholdValues.add(""+500);
        thresholdValues.add(""+750);
        thresholdValues.add(""+1000);
        PeonyFaceUtils.initializeComboBox(batchThresholdComboBox, thresholdValues, 
                thresholdValues.get(0), thresholdValues.get(0), 
                "Threshold: the maximal number of messages loaded from the server every loading period", false, this);
    }
    
    private PeonyEmailMessageType getPeonyEmailMessageType(){
        return PeonyEmailMessageType.convertEnumValueToType(emailTypeComboBox.getValue());
    }
    
    /**
     * Load offline emails by offlineEmailBox
     */
    @Override
    protected void initializePeonyEmailBox(){
        QueryNewOfflineEmailCriteria queryNewOfflineEmailCriteria = new QueryNewOfflineEmailCriteria();
        try{
            queryNewOfflineEmailCriteria.setBatchThreshold(Integer.parseInt(batchThresholdComboBox.getValue()));
        }catch (Exception ex){
            queryNewOfflineEmailCriteria.setBatchThreshold(100);
        }
        offlineEmailBox = new UserLocalEmailBox(PeonyProperties.getSingleton().getCurrentLoginEmployee().getEmployeeInfo(), 
                TechnicalController.GOOGLE_EMAIL.value(), queryNewOfflineEmailCriteria, PeonySpamRuleManager.getSingleton(PeonyProperties.getSingleton().getCurrentLoginUserUuid()));
        offlineEmailBox.addOfflineEmailBoxListener(this);
        
        try {
            if (!offlineEmailBox.isClosingGardenEmailBox()){
                offlineEmailBox.loadGardenEmails(PeonyProperties.getSingleton().getEmailSerializationFolder());
            }
        } catch (MessagingException ex) {
            //Exceptions.printStackTrace(ex);
        }
        emailTypeComboBox.setOnAction((ActionEvent event) -> {
            this.getSingleExecutorService().submit(filterMessageTreeViewByTypeTask(getPeonyEmailMessageType()));
        });
        batchThresholdComboBox.setOnAction((ActionEvent event) -> {
            offlineEmailBox.updateBatchThreshold(Integer.parseInt(batchThresholdComboBox.getValue()));
        });
    }

    /**
     * Display messages whose PeonyEmailMessageType is the same as aPeonyEmailMessageType
     * @param messageTreeView
     * @param aPeonyEmailMessageType 
     */
    private Task<HashMap<String, List<TreeItem<PeonyEmailTreeItemData>>>> filterMessageTreeViewByTypeTask(final PeonyEmailMessageType aPeonyEmailMessageType) {
        Task<HashMap<String, List<TreeItem<PeonyEmailTreeItemData>>>> task = new Task<HashMap<String, List<TreeItem<PeonyEmailTreeItemData>>>>(){
            @Override
            protected HashMap<String, List<TreeItem<PeonyEmailTreeItemData>>> call() throws Exception {
                return getTreeItemDataContainer().filterByPeonyEmailMessageType(aPeonyEmailMessageType);
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayErrorMessageDialog("Failed to filter the mail list. " + getMessage());
            }

            @Override
            protected void succeeded() {
                try {
                    HashMap<String, List<TreeItem<PeonyEmailTreeItemData>>> result = get();
                    Set<String> folderFaceNameKeys = result.keySet();
                    Iterator<String> itr = folderFaceNameKeys.iterator();
                    String folderFaceName;
                    TreeItem<PeonyEmailTreeItemData> folderItem;
                    while(itr.hasNext()){
                        folderFaceName = itr.next();
                        folderItem = getTreeItemDataContainer().findEmailMessageFolder(folderFaceName);
                        if (folderItem != null){
                            folderItem.getChildren().clear();
                            folderItem.getChildren().addAll(result.get(folderFaceName));
                        }
                    }//while
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                messageTreeView.refresh();
            }
            
        };
        return task;
    }
    
    @Override
    protected void initializeMessageTreeViewContextMenu(final TreeView<PeonyEmailTreeItemData> messageTreeView){
        super.initializeMessageTreeViewContextMenu(messageTreeView);
        ContextMenu messageTreeViewContextMenu = messageTreeView.getContextMenu();
        if (messageTreeViewContextMenu != null){
            
            messageTreeViewContextMenu.getItems().add(new SeparatorMenuItem());
            
            //Create folder
            MenuItem viewMenuItem = new MenuItem(EmailOperation.CREATE_FOLDER.value());
            viewMenuItem.setOnAction((ActionEvent e) -> {
                String folderName = PeonyFaceUtils.displayInputDialog(PeonyLauncher.mainFrame, "Please give a new folder name:", "Create new email folder");
                if (ZcaValidator.isNullEmpty(folderName)){
                    return;
                }
                if ((PeonyDataUtils.isSystemFolderNameFormat(folderName)) 
                        && (GardenEmailFolderName.convertEnumValueToType(folderName, true) == null))
                {
                    handleCreateEmailFolderTreeItemRequest(messageTreeView, folderName);
                }else{
                    PeonyFaceUtils.displayErrorMessageDialog("Failed to create the folder because the folder name is not valid or reserved.");
                }
            });
            messageTreeViewContextMenu.getItems().add(viewMenuItem);
            
            viewMenuItem = new MenuItem(EmailOperation.MOVE_TO_FOLDER.value());
            viewMenuItem.setOnAction((ActionEvent e) -> {
                displayMoveToFolderDialog(messageTreeView.getSelectionModel().getSelectedItems());
            });
            messageTreeViewContextMenu.getItems().add(viewMenuItem);
            
            viewMenuItem = new MenuItem(EmailOperation.DELETE_FOLDER.value());
            viewMenuItem.setOnAction((ActionEvent e) -> {
                displayFolderDeletionDialog();
            });
            messageTreeViewContextMenu.getItems().add(viewMenuItem);
            
            messageTreeViewContextMenu.getItems().add(new SeparatorMenuItem());
            
            //Assign
            MenuItem assignMenuItem = new MenuItem(EmailOperation.ASSIGN_SELECTED_EMAILS.value());
            assignMenuItem.setOnAction((ActionEvent e) -> {
                assignPeonyEmailMessages(messageTreeView.getSelectionModel().getSelectedItems());
            });
            messageTreeViewContextMenu.getItems().add(assignMenuItem);
            
            //Memo: todo zzi - it seems this feature is useless
////            MenuItem notesMenuItem = new MenuItem(EmailOperation.NOTES.value());
////            notesMenuItem.setOnAction((ActionEvent e) -> {
////                writeNotesForPeonyEmailMessages(messageTreeView.getSelectionModel().getSelectedItems());
////            });
////            messageTreeViewContextMenu.getItems().add(notesMenuItem);
        }
    }
    
    private void writeNotesForPeonyEmailMessages(final ObservableList<TreeItem<PeonyEmailTreeItemData>> selectedEmailTreeItems) {
        if ((selectedEmailTreeItems == null) || (selectedEmailTreeItems.isEmpty())){
            PeonyFaceUtils.displayErrorMessageDialog("Please select emails for assignment.");
            return;
        }
        if (SwingUtilities.isEventDispatchThread()){
            writeNotesForPeonyEmailMessagesHelper(selectedEmailTreeItems);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    writeNotesForPeonyEmailMessagesHelper(selectedEmailTreeItems);
                }
            });
        }
    }
    
    private void writeNotesForPeonyEmailMessagesHelper(final ObservableList<TreeItem<PeonyEmailTreeItemData>> selectedEmailTreeItems) {
        List<TreeItem<PeonyEmailTreeItemData>> aSelectedEmailTreeItemList = new ArrayList<>();
        GardenEmailMessage aPeonyEmailMessage;
        PeonyEmailTreeItemData aPeonyNoteTreeItemData;
        TreeItem<PeonyEmailTreeItemData> noteTreeItem = null;
        for (TreeItem<PeonyEmailTreeItemData> selectedItem : selectedEmailTreeItems){
            aPeonyNoteTreeItemData = selectedItem.getValue();
            aPeonyEmailMessage = aPeonyNoteTreeItemData.emitPeonyEmailMessage();
            if (aPeonyEmailMessage == null){
                if (PeonyEmailTreeItemData.EmailItemType.NOTE.equals(aPeonyNoteTreeItemData.getEmailItemType())){
                    noteTreeItem = selectedItem;
                }
            }else{
                aSelectedEmailTreeItemList.add(selectedItem);
            }
        }//for
        
        if (aSelectedEmailTreeItemList.isEmpty()){
            displayWriteNotesEmailDialogForNote(noteTreeItem);
        }else{
            WriteNotesEmailDialog writeNotesEmailDialog = new WriteNotesEmailDialog(null, true);
            writeNotesEmailDialog.addPeonyFaceEventListener(this);
            writeNotesEmailDialog.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
            writeNotesEmailDialog.launchWriteNotesEmailDialog("Write Notes for Email(s):", getGardenEmailBox().getGardenMailBoxAddess(), aSelectedEmailTreeItemList);
        }
    }
    
    private void displayWriteNotesEmailDialogForNote(TreeItem<PeonyEmailTreeItemData> noteTreeItem){
        if ((noteTreeItem == null) || (noteTreeItem.getParent() == null) || (noteTreeItem.getParent().getValue() == null)
                || (!PeonyEmailTreeItemData.EmailItemType.EMAIL.equals(noteTreeItem.getParent().getValue().getEmailItemType())))
        {
            PeonyFaceUtils.displayErrorMessageDialog("Please select which emails for notes or a specific note for view.");
        }else{
            WriteNotesEmailDialog writeNotesEmailDialog = new WriteNotesEmailDialog(null, true);
            writeNotesEmailDialog.addPeonyFaceEventListener(this);
            writeNotesEmailDialog.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
            writeNotesEmailDialog.launchWriteNotesEmailDialogForNote("Note for Email:", getGardenEmailBox().getGardenMailBoxAddess(), noteTreeItem);
        }
    }
    
    private void assignPeonyEmailMessages(final ObservableList<TreeItem<PeonyEmailTreeItemData>> selectedEmailTreeItems) {
        if ((selectedEmailTreeItems == null) || (selectedEmailTreeItems.isEmpty())){
            PeonyFaceUtils.displayErrorMessageDialog("Please select emails for assignment.");
            return;
        }
        if (SwingUtilities.isEventDispatchThread()){
            assignPeonyEmailMessagesHelper(selectedEmailTreeItems);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    assignPeonyEmailMessagesHelper(selectedEmailTreeItems);
                }
            });
        }
    }
    
    private void assignPeonyEmailMessagesHelper(final ObservableList<TreeItem<PeonyEmailTreeItemData>> selectedEmailTreeItems) {
        List<TreeItem<PeonyEmailTreeItemData>> aSelectedEmailTreeItemList = new ArrayList<>();
        GardenEmailMessage aPeonyEmailMessage;
        PeonyEmailTreeItemData aPeonyNoteTreeItemData;
        boolean illegalForAssignment = false;
        TreeItem<PeonyEmailTreeItemData> tagTreeItem = null;
        for (TreeItem<PeonyEmailTreeItemData> selectedItem : selectedEmailTreeItems){
            aPeonyNoteTreeItemData = selectedItem.getValue();
            aPeonyEmailMessage = aPeonyNoteTreeItemData.emitPeonyEmailMessage();
            if (aPeonyEmailMessage == null){
                if (PeonyEmailTreeItemData.EmailItemType.TAG.equals(aPeonyNoteTreeItemData.getEmailItemType())){
                    tagTreeItem = selectedItem;
                }
            }else{
                illegalForAssignment = aPeonyEmailMessage.isSentMessage()
                                    || aPeonyEmailMessage.isTaskMessage()
                                    || aPeonyEmailMessage.isSpamMessage();
                if (illegalForAssignment){
                    break;
                }
                aSelectedEmailTreeItemList.add(selectedItem);
            }
        }//for
        if (illegalForAssignment){
            PeonyFaceUtils.displayErrorMessageDialog("Cannot assign emails in TASK or SPAM or SENT folder.");
        }else{
            if (aSelectedEmailTreeItemList.isEmpty()){
                displayAssignEmailDialogForTag(tagTreeItem);
            }else{
                displayAssignEmailDialogForEmail(aSelectedEmailTreeItemList);
            }
        }
    }
    private void displayAssignEmailDialogForEmail(List<TreeItem<PeonyEmailTreeItemData>> aSelectedEmailTreeItemList) {
        if ((aSelectedEmailTreeItemList == null) || (aSelectedEmailTreeItemList.isEmpty())){
            PeonyFaceUtils.displayErrorMessageDialog("Please select which emails for assignment");
        }else{
            AssignEmailDialog assignEmailDialog = new AssignEmailDialog(null, true);
            assignEmailDialog.addPeonyFaceEventListener(this);
            assignEmailDialog.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
            assignEmailDialog.launchAssignEmailDialog("Assign Email:", getGardenEmailBox().getGardenMailBoxAddess(), aSelectedEmailTreeItemList);
        }
    }

    @Override
    public void displayAssignEmailDialog(String emailTagUuid) {
        TreeItem<PeonyEmailTreeItemData> tagTreeItem = tagPublishedStorage.get(emailTagUuid);
        if (tagTreeItem == null){
            PeonyFaceUtils.displayWarningMessageDialog("Cannot find the email tag. Please try it later.");
        }else{
            displayAssignEmailDialogForTag(tagTreeItem);
        }
    }

    private void displayAssignEmailDialogForTag(final TreeItem<PeonyEmailTreeItemData> tagTreeItem) {
        if (SwingUtilities.isEventDispatchThread()){
            displayAssignEmailDialogForTagHelper(tagTreeItem);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    displayAssignEmailDialogForTagHelper(tagTreeItem);
                }
            });
        }
    }
    private void displayAssignEmailDialogForTagHelper(final TreeItem<PeonyEmailTreeItemData> tagTreeItem) {
        if ((tagTreeItem == null) || (tagTreeItem.getParent() == null) || (tagTreeItem.getParent().getValue() == null)
                || (!PeonyEmailTreeItemData.EmailItemType.EMAIL.equals(tagTreeItem.getParent().getValue().getEmailItemType())))
        {
            PeonyFaceUtils.displayErrorMessageDialog("Please select specific assignment-tags for assignment");
        }else{
            AssignEmailDialog assignEmailDialog = new AssignEmailDialog(null, true);
            assignEmailDialog.addPeonyFaceEventListener(this);
            assignEmailDialog.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
            assignEmailDialog.launchAssignEmailDialogForTag("Assign Email:", getGardenEmailBox().getGardenMailBoxAddess(), tagTreeItem);
        }
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof PeonyEmailTagListCreated){
            handlePeonyEmailTagListCreated((PeonyEmailTagListCreated)event);
        }else if (event instanceof PeonyMemoListSaved){
            handlePeonyMemoListSaved((PeonyMemoListSaved)event);
        }else {
            super.peonyFaceEventHappened(event);
        }
    }
    
    private void handlePeonyMemoListSaved(final PeonyMemoListSaved peonyMemoListSavedEvent){
        if (Platform.isFxApplicationThread()){
            handlePeonyMemoListSavedHelper(peonyMemoListSavedEvent);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    handlePeonyMemoListSavedHelper(peonyMemoListSavedEvent);
                }
            });
        }
    }
    
    private void handlePeonyMemoListSavedHelper(final PeonyMemoListSaved peonyMemoListSavedEvent){
        List<TreeItem<PeonyEmailTreeItemData>> selectedEmailTreeItems = peonyMemoListSavedEvent.getSelectedEmailTreeItems();
        PeonyMemoList aPeonyMemoList = peonyMemoListSavedEvent.getPeonyMemoList();
        List<PeonyMemo> peonyMemos = aPeonyMemoList.getPeonyMemoList();
        TreeItem<PeonyEmailTreeItemData> targetMemoItem;
        for (TreeItem<PeonyEmailTreeItemData> selectedEmailTreeItem : selectedEmailTreeItems){
            for (PeonyMemo aPeonyMemo : peonyMemos){
                targetMemoItem = new TreeItem<>(new PeonyEmailTreeItemData(aPeonyMemo, PeonyEmailTreeItemData.EmailItemType.NOTE));
                targetMemoItem.setGraphic(new ImageView(PeonyGraphic.getJavaFxImage("note_edit.png")));
                selectedEmailTreeItem.getChildren().add(targetMemoItem);
                selectedEmailTreeItem.setExpanded(true);
            }//for
        }//for
    }

    private void handlePeonyEmailTagListCreated(PeonyEmailTagListCreated peonyEmailTagListCreatedEvent) {
        if (Platform.isFxApplicationThread()){
            handlePeonyEmailTagListCreatedHelper(peonyEmailTagListCreatedEvent);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    handlePeonyEmailTagListCreatedHelper(peonyEmailTagListCreatedEvent);
                }
            });
        }
    }
    
    private void handlePeonyEmailTagListCreatedHelper(PeonyEmailTagListCreated peonyEmailTagListCreatedEvent){
        List<TreeItem<PeonyEmailTreeItemData>> selectedEmailTreeItems = peonyEmailTagListCreatedEvent.getSelectedEmailTreeItems();
        PeonyEmailTagList aPeonyEmailTagList = peonyEmailTagListCreatedEvent.getPeonyEmailTagList();
        List<PeonyEmailTag> peonyEmailTags = aPeonyEmailTagList.getPeonyEmailTagList();
        TreeItem<PeonyEmailTreeItemData> targetEmaiLTagItem;
        for (TreeItem<PeonyEmailTreeItemData> selectedEmailTreeItem : selectedEmailTreeItems){
            for (PeonyEmailTag aPeonyEmailTag : peonyEmailTags){
                if (!tagPublishedStorage.containsKey(aPeonyEmailTag.getEmailTag().getTagUuid())){
                    targetEmaiLTagItem = new TreeItem<>(new PeonyEmailTreeItemData(aPeonyEmailTag, PeonyEmailTreeItemData.EmailItemType.TAG));
                    targetEmaiLTagItem.setGraphic(new ImageView(PeonyGraphic.getJavaFxImage("red_star.png")));
                    selectedEmailTreeItem.getChildren().add(targetEmaiLTagItem);
                    selectedEmailTreeItem.setExpanded(true);
                    tagPublishedStorage.put(aPeonyEmailTag.getEmailTag().getTagUuid(), targetEmaiLTagItem);
                }
            }//for
        }//for
    }

    /**
     * if any parameter is null, this broadcasting will be dropped
     * @param aPeonyOfflineEmail
     * @param aGardenEmailMessage 
     */
    @Override
    public void peonyOfflineEmailMessageLoaded(PeonyOfflineEmail aPeonyOfflineEmail, GardenEmailMessage aGardenEmailMessage) {
        if (aPeonyOfflineEmail == null){
            return;
        }
        
        if (aGardenEmailMessage == null){
            return;
        }
        
        populateOfflineEmailMessage(messageTreeView, aPeonyOfflineEmail, aGardenEmailMessage);
    }

    /**
     * @param messageTreeView
     * @param aPeonyOfflineEmail
     * @param index 
     */
    private void populateOfflineEmailMessage(final TreeView<PeonyEmailTreeItemData> messageTreeView, 
                                             final PeonyOfflineEmail aPeonyOfflineEmail, 
                                             final GardenEmailMessage aGardenEmailMessage) 
    { 
        //Single thread for this task
        getSingleExecutorService().submit(createPopulateOfflineEmailMessageTask(messageTreeView, aPeonyOfflineEmail, aGardenEmailMessage));
    }
    
    /**
     * @param messageTreeView
     * @param aPeonyOfflineEmail
     * @param index
     * @return 
     */
    private Task<List<TreeItem<PeonyEmailTreeItemData>>> createPopulateOfflineEmailMessageTask(final TreeView<PeonyEmailTreeItemData> messageTreeView, 
                                                                                               final PeonyOfflineEmail aPeonyOfflineEmail, 
                                                                                               final GardenEmailMessage aGardenEmailMessage)
    {
        
        Task<List<TreeItem<PeonyEmailTreeItemData>>> populateEmailMessageTask = new Task<List<TreeItem<PeonyEmailTreeItemData>>>(){
            /**
             * 
             * @return - a TreeView<PeonyEmailTreeItemData> instance which represents aGardenEmailMessage
             * @throws Exception 
             */
            @Override
            protected List<TreeItem<PeonyEmailTreeItemData>> call() throws Exception {
                List<TreeItem<PeonyEmailTreeItemData>> result = new ArrayList<>();
                PeonyEmailTreeItemDataContainer treeItemDataContainer = getTreeItemDataContainer();
                /**
                 * Get the folder-tree-item
                 */
                TreeItem<PeonyEmailTreeItemData> messageFolderItem;
                String mailFolderName = aGardenEmailMessage.getFolderFullName();
                if (ZcaValidator.isNullEmpty(mailFolderName)){
                    messageFolderItem = treeItemDataContainer.constructEmailMessageFolderTreeItem(GardenEmailFolderName.GARDEN_INBOX.value());
                }else{
                    messageFolderItem = treeItemDataContainer.findEmailMessageFolder(mailFolderName);
                    if (messageFolderItem == null){
                        messageFolderItem = treeItemDataContainer.constructEmailMessageFolderTreeItem(mailFolderName);
                    }
                }
                TreeItem<PeonyEmailTreeItemData> emailTreeItemData = treeItemDataContainer.constructEmailMessageTreeItem(aGardenEmailMessage, messageFolderItem, -1);
                emailTreeItemData.getValue().setAssociatedPeonyOfflineEmail(aPeonyOfflineEmail);
                /**
                 * Email tags
                 */
                List<PeonyEmailTag> aPeonyEmailTagList = aPeonyOfflineEmail.getPeonyEmailTagList();
                if (aPeonyEmailTagList != null){
                    for (PeonyEmailTag aPeonyEmailTag : aPeonyEmailTagList){
                        if (!tagPublishedStorage.containsKey(aPeonyEmailTag.getEmailTag().getTagUuid())){
                            tagPublishedStorage.put(aPeonyEmailTag.getEmailTag().getTagUuid(), 
                                    treeItemDataContainer.constructEmailTagTreeItem(aPeonyEmailTag, emailTreeItemData));
                        }
                    }
                }
                /**
                 * Email memos
                 */
                List<PeonyMemo> aPeonyMemoList = aPeonyOfflineEmail.getPeonyMemoList();
                if (aPeonyMemoList != null){
                    for (PeonyMemo aPeonyMemo : aPeonyMemoList){
                        treeItemDataContainer.constructOfflineEmailMemoTreeItem(aPeonyMemo, emailTreeItemData);
                    }
                }
                result.add(messageFolderItem);
                result.add(emailTreeItemData);
                return result;
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
                    List<TreeItem<PeonyEmailTreeItemData>> result = get();
                    if (result.size() == 2){
                        TreeItem<PeonyEmailTreeItemData> messageFolderItem = result.get(0);
                        TreeItem<PeonyEmailTreeItemData> emailTreeItem = result.get(1);
                        
                        //loading the new message (for offline, there is only loading-case)
                        messageFolderItem.getChildren().add(emailTreeItem);
                        
                        String folderName = aGardenEmailMessage.getFolderFullName();
                        //add the folder onto the tree root
                        if (!messageTreeView.getRoot().getChildren().contains(messageFolderItem)){
                            if (GardenEmailFolderName.GARDEN_INBOX.value().equalsIgnoreCase(folderName)){
                                messageTreeView.getRoot().getChildren().add(0, messageFolderItem);   //make sure "Inbox" being the first one
                            }else{
                                messageTreeView.getRoot().getChildren().add(messageFolderItem);
                            }
                        }
                        messageFolderItem.setValue(
                                new PeonyEmailTreeItemData(folderName + " ("+messageFolderItem.getChildren().size()+")", 
                                        PeonyEmailTreeItemData.EmailItemType.FOLDER));
                        
                        /**
                         * Email tree
                         */
                        messageTreeView.refresh();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        
        return populateEmailMessageTask;
    }

    @Override
    protected void recordOfflineEmail(GardenEmailMessage gardenEmailMessage, PeonyOfflineEmail peonyOfflineEmail) {
        if ((peonyOfflineEmail == null) || (peonyOfflineEmail.getOfflineEmail() == null) 
                || (ZcaValidator.isNullEmpty(peonyOfflineEmail.getOfflineEmail().getOfflineEmailUuid())))
        {
            return;
        }
        PeonyOfflineEmailList aPeonyOfflineEmailList = new PeonyOfflineEmailList();
        aPeonyOfflineEmailList.getPeonyOfflineEmailList().add(peonyOfflineEmail);
        recordPeonyOfflineEmailList(aPeonyOfflineEmailList);
    }
    
    @Override
    protected void recordPeonyOfflineEmailList(PeonyOfflineEmailList aPeonyOfflineEmailList) {
        if ((aPeonyOfflineEmailList == null) || (aPeonyOfflineEmailList.getPeonyOfflineEmailList() == null)){
            return;
        }
        try {
            Lookup.getDefault().lookup(PeonyManagementService.class).getPeonyManagementRestClient().storeEntity_XML(
                    PeonyOfflineEmailList.class, GardenRestParams.Management.storePeonyOfflineEmailListRestParams(), aPeonyOfflineEmailList);
        } catch (Exception ex) {
            //Exceptions.printStackTrace(ex);
        }
    }

    @Override
    protected TreeView<PeonyEmailTreeItemData> getMessageTreeView() {
        return messageTreeView;
    }
    
    @Override
    public void deletePeonyEmailMessage(final GardenEmailMessage aPeonyEmailMessage){
        if (aPeonyEmailMessage == null){
            return;
        }
        Task<List<TreeItem<PeonyEmailTreeItemData>>> deletePeonyEmailMessageTask = new Task<List<TreeItem<PeonyEmailTreeItemData>>>(){
            @Override
            protected List<TreeItem<PeonyEmailTreeItemData>> call() throws Exception {
                //change its status to be DELETED
                Lookup.getDefault().lookup(PeonyManagementService.class).getPeonyManagementRestClient().requestOperation_XML(GardenRestParams.Management.requestOfflineEmailStatusUpdateRestParams(PeonyProperties.getSingleton().getCurrentLoginUserUuid(), 
                                PeonyProperties.getSingleton().getOfflineEmailAddress(), 
                                aPeonyEmailMessage.getEmailMsgUid(), OfflineMessageStatus.DELETED.name()));
                /**
                 * Remove GUI parts for deletion: tree-items with its tabs if there. For memory and disk parts, it is processed until 
                 * PeonyEmailServiceListener::peonyEmailMessageDeleted event is broadcasted
                 */
                PeonyFaceUtils.closePeonyTopComponent(aPeonyEmailMessage.getFolderFullName() + aPeonyEmailMessage.getEmailMsgUid());

                List<TreeItem<PeonyEmailTreeItemData>> result = new ArrayList<>();
                TreeItem<PeonyEmailTreeItemData> messageFolderItem;
                String folderName = aPeonyEmailMessage.getFolderFullName();
                if (ZcaValidator.isNullEmpty(folderName)){
                    messageFolderItem = getTreeItemDataContainer().findEmailMessageFolder(GardenEmailFolderName.GARDEN_INBOX.value());
                }else{
                    messageFolderItem = getTreeItemDataContainer().findEmailMessageFolder(folderName);
                }
                //get the message item
                TreeItem<PeonyEmailTreeItemData> messageItem = getTreeItemDataContainer().getEmailMessageItem(messageFolderItem, aPeonyEmailMessage);
                
                result.add(messageItem);
                result.add(messageFolderItem);
                return result;
            }

            @Override
            protected void failed() {
                Exceptions.printStackTrace(getException());
            }

            @Override
            protected void succeeded() {
                try {
                    List<TreeItem<PeonyEmailTreeItemData>> result = get();
                    //remove tree-item from the view
                    removeEmailMessageTreeItemFromFolder(aPeonyEmailMessage, result.get(0), result.get(1));
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(deletePeonyEmailMessageTask);
    }

    @Override
    protected void recordGardenEmailMessageFlagChanged(GardenEmailMessage aPeonyEmailMessage) {
        offlineEmailBox.recordGardenEmailMessageFlagChanged(aPeonyEmailMessage);
    }

    @Override
    public void shutdownEmailServices() {
        if (offlineEmailBox != null){
            offlineEmailBox.setClosingGardenEmailBox(true);
        }
        getCachedThreadPoolExecutorService().shutdown();
    }
    
    @Override
    public void initializePeonyEmailBoxPane(TreeView<PeonyEmailTreeItemData> messageTreeView, Button composeEmailButton, Button spamRuleButton) {
        composeEmailButton.setTooltip(new Tooltip("Compose a new email..."));
        composeEmailButton.setGraphic(PeonyGraphic.getImageView("compose.png"));
        composeEmailButton.setOnAction((ActionEvent event) -> {
            //todo zzj: give the new GardenEmailMessage some default values here
            Lookup.getDefault().lookup(PeonyEmailService.class).displayComposeEmailTopComponent(new GardenEmailMessage(), new PeonyOfflineEmail(), OfflineMessageStatus.SEND);
        });
        
        spamRuleButton.setVisible(false);   //no spam rule button for offline type
        
        super.initializePeonyEmailBoxPane(messageTreeView, composeEmailButton, spamRuleButton);
    
    }
}
