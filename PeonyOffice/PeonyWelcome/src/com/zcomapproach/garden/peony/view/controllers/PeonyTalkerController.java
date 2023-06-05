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

package com.zcomapproach.garden.peony.view.controllers;

import com.jfoenix.controls.JFXButton;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.commons.xmpp.ZcaXmppUtils;
import com.zcomapproach.commons.xmpp.data.ZcaXmppAccount;
import com.zcomapproach.commons.xmpp.data.ZcaXmppSettings;
import com.zcomapproach.garden.guard.RoseWebCipher;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxcorpService;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.actions.BuddyMessagingAgent;
import com.zcomapproach.garden.peony.view.actions.MultiUserMessagingAgent;
import com.zcomapproach.garden.peony.view.actions.PeonyMessagingBoardManager;
import com.zcomapproach.garden.peony.view.data.PeonyBuddyGroupTreeItemData;
import com.zcomapproach.garden.peony.view.data.PeonyBuddyTreeItemData;
import com.zcomapproach.garden.peony.view.data.PeonyTreeItemData;
import com.zcomapproach.garden.peony.view.dialogs.PeonyJobAssignmentDialog;
import com.zcomapproach.garden.peony.view.dialogs.PeonyTalkingDialog;
import com.zcomapproach.garden.peony.view.events.PeonyDailyReportSaved;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.listeners.MucParticipantStatusListener;
import com.zcomapproach.garden.peony.view.listeners.MucUserStatusListener;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.peony.view.update.UpdateSystemTask;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.constant.JobAssignmentStatus;
import com.zcomapproach.garden.persistence.entity.G02DailyReport;
import com.zcomapproach.garden.persistence.entity.G02DailyReportPK;
import com.zcomapproach.garden.persistence.entity.G02JobAssignment;
import com.zcomapproach.garden.persistence.entity.G02Memo;
import com.zcomapproach.garden.persistence.entity.G02XmppAccount;
import com.zcomapproach.garden.persistence.peony.PeonyDailyReport;
import com.zcomapproach.garden.persistence.peony.PeonyDailyReportList;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.persistence.peony.PeonyEmployeeList;
import com.zcomapproach.garden.persistence.peony.PeonyJob;
import com.zcomapproach.garden.persistence.peony.PeonyJobAssignment;
import com.zcomapproach.garden.persistence.peony.PeonyJobAssignmentList;
import com.zcomapproach.garden.rest.GardenRestParams;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PresenceListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.RosterLoadedListener;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class PeonyTalkerController extends PeonyWelcomeServiceController implements PeonyFaceEventListener{
    
    private static final List<String> _peonyXmppPredefinedChatRoomJidList = new ArrayList<>();
    private static final String company = "luyincpa";
    private static final String customers = "luyincpacustomers";
    static{
        _peonyXmppPredefinedChatRoomJidList.add(ZcaXmppSettings.createChatRoomJid(company));
        _peonyXmppPredefinedChatRoomJidList.add(ZcaXmppSettings.createChatRoomJid(customers));
    }
    private static String getPresentableChatRoomName(String chatRoomName){
        if (company.equalsIgnoreCase(chatRoomName)){
            return "Lu Yin CPA PC";
        }
        if (customers.equalsIgnoreCase(chatRoomName)){
            return "Customers";
        }
        return chatRoomName;
    }
    /**
     * The name of predefined user account in the operfire server
     * @return 
     */
    private static final String _peonyXmppUserName = "peony_xxxx_user";
    /**
     * The password of predefined user account in the operfire server
     * @return 
     */
    private static final String _peonyXmppUserPassword = "XXXXXXXXXXXXX";
    
    @FXML
    private ScrollPane jobAssignedToOthersScrollPane;
    @FXML
    private VBox jobAssignedToOthersVBox;
    @FXML
    private FlowPane jobAssignedToOthersFlowPane;
    
    @FXML
    private ScrollPane followUpJobsScrollPane;
    @FXML
    private VBox followUpJobsVBox;
    @FXML
    private FlowPane followUpJobsFlowPane;
    
    @FXML
    private JFXButton myDailyReportsButton;
    @FXML
    private JFXButton assignJobsButton;
    @FXML
    private Label dateLabel;
    
    @FXML
    private ScrollPane leftHistorialJobsScrollPane;
    @FXML
    private VBox leftHistorialJobsVBox;
    @FXML
    private FlowPane leftHistorialJobsFlowPane;
    
    @FXML
    private ScrollPane rightTodayJobsScrollPane;
    @FXML
    private VBox rightTodayJobsVBox;
    @FXML
    private FlowPane rightTodayJobsFlowPane;
    
    @FXML
    private TabPane talkerTabPane;
    @FXML
    private Label currentTalkerNameLabel;
    @FXML
    private HBox buddyGroupButtonHBox;
    @FXML
    private TreeView<PeonyTreeItemData> rosterTreeView;
    
    @FXML
    private Button updateButton;
    
    private TreeItem<PeonyTreeItemData> rosterTreeRoot;
    private final HashMap<String, TreeItem<PeonyTreeItemData>> groupItemsCache;
    private final HashMap<String, PeonyBuddyTreeItemData> buddyItemsCache;
    private final TreeItem<PeonyTreeItemData> onlineGroupItem;
    private final TreeItem<PeonyTreeItemData> offlineGroupItem;
    
    private PeonyMessagingBoardManager manager;
    private PeonyEmployee currentEmployee;
    
    private final HashMap<String, JobAssignmentNode> jobAssignmentStorage = new HashMap<>();
    
    private final Thread todayJobPaneRefresher;
    
    private AbstractXMPPConnection targetXmppConnection;
    private ReconnectionManager targetReconnectionManager;
    private ChatManager targetChatManager;
    private MultiUserChatManager targetMultiUserChatManager;

    public PeonyTalkerController() {
        groupItemsCache = new HashMap<>();
        buddyItemsCache = new HashMap<>();
        
        onlineGroupItem = new TreeItem<>(new PeonyBuddyGroupTreeItemData(ZcaXmppSettings.ONLINE_GROUP_NAME), PeonyGraphic.getImageView("online_group24.png"));
        onlineGroupItem.setExpanded(true);
        groupItemsCache.put(ZcaXmppSettings.ONLINE_GROUP_NAME, onlineGroupItem);
        
        offlineGroupItem = new TreeItem<>(new PeonyBuddyGroupTreeItemData(ZcaXmppSettings.OFFLINE_GROUP_NAME), PeonyGraphic.getImageView("offline_group24.png"));
        offlineGroupItem.setExpanded(true);
        groupItemsCache.put(ZcaXmppSettings.OFFLINE_GROUP_NAME, offlineGroupItem);
        
        todayJobPaneRefresher = new Thread(new Runnable(){
            @Override
            public void run() {
                while (true){
                    getCachedThreadPoolExecutorService().submit(new TodayJobPaneRefreshTask());
                    try {
                        //refresh today's job list every minutes
                        Thread.sleep(1000*60);
                    } catch (InterruptedException ex) {
                        break;
                    }
                }
            }
        });
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        if (PeonyProperties.getSingleton().getCurrentLoginEmployee().getAccount().getLoginName().equalsIgnoreCase("zhijun98")){
            updateButton.setVisible(true);
            updateButton.setOnAction(evt -> {
                this.getCachedThreadPoolExecutorService().submit(new UpdateSystemTask());
            });
        }else{
            updateButton.setVisible(false);
        }
        
        
        manager = new PeonyMessagingBoardManager(talkerTabPane);
        currentEmployee = PeonyProperties.getSingleton().getCurrentLoginEmployee();
        dateLabel.setText(ZcaCalendar.convertToMMddyyyy(new Date(), "-"));
        
        initializeMyJobsPart();
        
        initializePeonyTalkerPart();
        
        PeonyFaceUtils.publishMessageOntoOutputWindow(TimeZone.getDefault().getDisplayName());
        PeonyFaceUtils.publishMessageOntoOutputWindow(Locale.getDefault().getDisplayName());
        PeonyFaceUtils.publishMessageOntoOutputWindow(ZcaCalendar.convertToMMddyyyyHHmmss(new Date(), "-", "@", ":"));
    }

    public void startTalker() {
        
    }

    public void stopTalker() {
        
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof PeonyDailyReportSaved){
            handlePeonyDailyReportSaved((PeonyDailyReportSaved)event);
        }
    }

    private PeonyMessagingBoardController initializeBuddyTalkingRootPane(AnchorPane anchorPane, 
            String messagingBoardTitle, String buddyJid, Chat targetChat, Tab tabOwner)
    {
        PeonyMessagingBoardController aPeonyMessagingBoardController = new PeonyMessagingBoardController(
                tabOwner, messagingBoardTitle, new BuddyMessagingAgent(targetChat, targetXmppConnection.getUser(), buddyJid));
        aPeonyMessagingBoardController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
        
        if ((anchorPane != null) && (tabOwner != null)){
            Pane aPane;
            try {
                aPane = aPeonyMessagingBoardController.loadFxml();
                AnchorPane.setTopAnchor(aPane, 0.0);
                AnchorPane.setRightAnchor(aPane, 0.0);
                AnchorPane.setBottomAnchor(aPane, 0.0);
                AnchorPane.setLeftAnchor(aPane, 0.0);
                anchorPane.getChildren().add(aPane);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return aPeonyMessagingBoardController;
    }

    private PeonyMessagingBoardController initializeMultiUserMessagingBoardAnchorPane(AnchorPane anchorPane, 
            String messagingBoardTitle, String targetMultiUserChatRoomJid, MultiUserChat multiUserChat, Tab tabOwner){
        PeonyMessagingBoardController aPeonyMessagingBoardController = new PeonyMessagingBoardController(tabOwner, 
                messagingBoardTitle, new MultiUserMessagingAgent(multiUserChat, targetMultiUserChatRoomJid));
        aPeonyMessagingBoardController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
        Pane aPane;
        try {
            aPane = aPeonyMessagingBoardController.loadFxml();
            AnchorPane.setTopAnchor(aPane, 0.0);
            AnchorPane.setRightAnchor(aPane, 0.0);
            AnchorPane.setBottomAnchor(aPane, 0.0);
            AnchorPane.setLeftAnchor(aPane, 0.0);
            anchorPane.getChildren().add(aPane);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return aPeonyMessagingBoardController;
    }
    
    public void displayTalkingBoard(final String fromBuddyJid) {
        displayTalkingBoard(fromBuddyJid, null);
    }

    public void displayTalkingBoard(final String fromBuddyJid, final String message) {
        String buddyFullName = Lookup.getDefault().lookup(PeonyManagementService.class).retrieveXmppAccountFullName(ZcaXmppSettings.parseChatUserName(fromBuddyJid));
        if (ZcaValidator.isNullEmpty(buddyFullName)){
            displayTalkingTab(fromBuddyJid, "Customer - "+fromBuddyJid, message);
        }else{
            displayTalkingDialog(fromBuddyJid, buddyFullName, message);
        }
    }

    private void displayTalkingDialog(final String fromBuddyJid, final String buddyFullName, final String message) {
        if (SwingUtilities.isEventDispatchThread()){
            displayTalkingDialogHelper(fromBuddyJid, buddyFullName, message);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    displayTalkingDialogHelper(fromBuddyJid, buddyFullName, message);
                }
            });
        }
    }
    
    private void displayTalkingDialogHelper(final String fromBuddyJid, final String buddyFullName, final String message) {
        if (ZcaValidator.isNullEmpty(fromBuddyJid)){
            return;
        }
        PeonyTalkingDialog aPeonyTalkingDialog = manager.retrievePeonyMessagingBoardDialogController(fromBuddyJid);
        if (aPeonyTalkingDialog == null){
            
            PeonyMessagingBoardController aPeonyBuddyMessagingBoardController = initializeBuddyTalkingRootPane(null, 
                    "Talk to " + buddyFullName + "...", fromBuddyJid, targetChatManager.createChat(fromBuddyJid), null);
            
            aPeonyTalkingDialog = new PeonyTalkingDialog(PeonyLauncher.mainFrame, false);
            aPeonyTalkingDialog.launchPeonyDialog(buddyFullName, aPeonyBuddyMessagingBoardController);
            
            manager.addPeonyMessagingBoardDialogController(fromBuddyJid, aPeonyTalkingDialog);
        }else{
            aPeonyTalkingDialog.setVisible(true);
        }
        //display message if necessary
        if (ZcaValidator.isNotNullEmpty(message)){
            aPeonyTalkingDialog.receiveMessage(message);
        }
    }

    private void displayTalkingTab(final String fromBuddyJid, final String buddyFullName, final String message) {
        if (Platform.isFxApplicationThread()){
            displayTalkingTabHelper(fromBuddyJid, buddyFullName, message);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    displayTalkingTabHelper(fromBuddyJid, buddyFullName, message);
                }
            });
        }
    }
    
    private void displayTalkingTabHelper(final String fromBuddyJid, final String buddyFullName, final String message) {
        if (ZcaValidator.isNullEmpty(fromBuddyJid)){
            return;
        }
        
        //String buddyFullName = Lookup.getDefault().lookup(PeonyManagementService.class).retrieveXmppAccountFullName(ZcaXmppSettings.parseChatUserName(fromBuddyJid));
        AnchorPane rootPane = new AnchorPane();
        //get the tab owner
        PeonyMessagingBoardController aPeonyBuddyMessagingBoardController = manager.retrievePeonyMessagingBoardController(fromBuddyJid);
        Tab tab;
        if (aPeonyBuddyMessagingBoardController == null){
            tab = new Tab(buddyFullName);
            aPeonyBuddyMessagingBoardController = initializeBuddyTalkingRootPane(rootPane, 
                    "Talk to " + buddyFullName + "...", fromBuddyJid, targetChatManager.createChat(fromBuddyJid), tab);
            manager.addPeonyMessagingBoardController(fromBuddyJid, aPeonyBuddyMessagingBoardController);
            tab.setContent(rootPane);
            tab.setClosable(true);
        }else{
            tab = aPeonyBuddyMessagingBoardController.getTabOwner();
        }
        //make sure tab being on the TabPane
        if (!talkerTabPane.getTabs().contains(tab)){
            talkerTabPane.getTabs().add(tab);
            if (message == null){
                //it came from double-click
                talkerTabPane.getSelectionModel().select(tab);
            }
        }
        manager.flashMessagingTabTitle(tab);
        //display message if necessary
        if (ZcaValidator.isNotNullEmpty(message)){
            aPeonyBuddyMessagingBoardController.receiveMessage(message);
        }
    }

    private void displayMutliUserTalkingTab(final String multiUserChatRoomJid) {
        displayMutliUserTalkingTab(multiUserChatRoomJid, null, null);
    }

    private void displayMutliUserTalkingTab(final String multiUserChatRoomJid, final String fromUserName, final String message) {
        if (Platform.isFxApplicationThread()){
            displayMutliUserTalkingTabHelper(multiUserChatRoomJid, fromUserName, message);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    displayMutliUserTalkingTabHelper(multiUserChatRoomJid, fromUserName, message);
                }
            });
        }
    }
    
    private void displayMutliUserTalkingTabHelper(final String multiUserChatRoomJid, final String fromUserName, final String message) {
        if (ZcaValidator.isNullEmpty(multiUserChatRoomJid)){
            return;
        }
        String multiUserChatRoomName = ZcaXmppSettings.parseChatRoomName(multiUserChatRoomJid);
        AnchorPane rootPane = new AnchorPane();
        //get the tab owner
        PeonyMessagingBoardController aPeonyMultiUserMessagingBoardController = manager.retrievePeonyMessagingBoardController(multiUserChatRoomJid);
        Tab tab;
        if (aPeonyMultiUserMessagingBoardController == null){
            tab = new Tab(getPresentableChatRoomName(multiUserChatRoomName));
            try {
                aPeonyMultiUserMessagingBoardController = initializeMultiUserMessagingBoardAnchorPane(rootPane,
                        "Chat room: " + getPresentableChatRoomName(multiUserChatRoomName), multiUserChatRoomJid, joinMultiUserChat(multiUserChatRoomJid), tab);
            } catch (SmackException.NoResponseException | XMPPErrorException | SmackException.NotConnectedException ex) {
                //Exceptions.printStackTrace(ex);
                PeonyFaceUtils.publishMessageOntoOutputWindow("Something wrong while current talker is joing the conference room: " + multiUserChatRoomName);
                return; //quit from the initialization function
            }
            manager.addPeonyMessagingBoardController(multiUserChatRoomJid, aPeonyMultiUserMessagingBoardController);
            tab.setContent(rootPane);
            tab.setClosable(false);
        }else{
            tab = aPeonyMultiUserMessagingBoardController.getTabOwner();
        }
        //make sure tab being on the TabPane
        if (!talkerTabPane.getTabs().contains(tab)){
            talkerTabPane.getTabs().add(tab);
        }
        manager.flashMessagingTabTitle(tab);
        //display message if necessary
        if (ZcaValidator.isNotNullEmpty(message)){
            aPeonyMultiUserMessagingBoardController.receiveMessage(
                    Lookup.getDefault().lookup(PeonyManagementService.class).retrieveXmppAccountFullName(ZcaXmppSettings.parseChatUserName(fromUserName))+ ": " +message);
        }
    }

    private void addBuddy() {
        PeonyFaceUtils.displayNoImplementationDialog();
    }

    private void addGroup() {
        PeonyFaceUtils.displayNoImplementationDialog();
    }

    private void removeFromGroupTreeItemOwners(PeonyBuddyTreeItemData aPeonyBuddyTreeItemData) {
        ObservableList<TreeItem<PeonyTreeItemData>> groupTreeItems = rosterTreeRoot.getChildren();
        for (TreeItem<PeonyTreeItemData> groupTreeItem : groupTreeItems){
            TreeItem<PeonyTreeItemData> buddyTreeItem = findBuddyTreeItemFromGroup(groupTreeItem, aPeonyBuddyTreeItemData);
            if (buddyTreeItem != null){
                groupTreeItem.getChildren().remove(buddyTreeItem);
            }
        }
    }

    private void addIntoGroupTreeItemOwners(PeonyBuddyTreeItemData aPeonyBuddyTreeItemData) {
        Set<String> groupNames = groupItemsCache.keySet();
        TreeItem<PeonyTreeItemData> buddyTreeItem;
        TreeItem<PeonyTreeItemData> groupTreeItem;
        for (String groupName : groupNames){
            if (aPeonyBuddyTreeItemData.isMember(groupName)){
                groupTreeItem = groupItemsCache.get(groupName);
                buddyTreeItem = findBuddyTreeItemFromGroup(groupTreeItem, aPeonyBuddyTreeItemData);
                if (buddyTreeItem == null){
                    groupTreeItem.getChildren().add(new TreeItem<>(aPeonyBuddyTreeItemData, PeonyGraphic.getImageView("user.png")));
                }
            }
        }
    }

    private TreeItem<PeonyTreeItemData> findBuddyTreeItemFromGroup(TreeItem<PeonyTreeItemData> groupTreeItem, PeonyBuddyTreeItemData aPeonyBuddyTreeItemData) {
        ObservableList<TreeItem<PeonyTreeItemData>> buddyTreeItems = groupTreeItem.getChildren();
        for (TreeItem<PeonyTreeItemData> buddyTreeItem : buddyTreeItems){
            if (aPeonyBuddyTreeItemData.equals(buddyTreeItem.getValue())){
                return buddyTreeItem;
            }
        }
        return null;
    }

    private MultiUserChat joinMultiUserChat(String multiUserChatRoomJid) throws SmackException.NoResponseException, XMPPErrorException, SmackException.NotConnectedException {

        MultiUserChat aMultiUserChat = targetMultiUserChatManager.getMultiUserChat(multiUserChatRoomJid);
        //send the default registration form
        aMultiUserChat.sendRegistrationForm(aMultiUserChat.getRegistrationForm());
        aMultiUserChat.join(ZcaXmppSettings.createChatUserJid(PeonyProperties.getSingleton().getCurrentXmppAccount().getLoginName()));
        aMultiUserChat.addInvitationRejectionListener(new InvitationRejectionListener(){
            @Override
            public void invitationDeclined(String invitee, String reason) {
                PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MultiUserChat::InvitationRejectionListener::invitationDeclined happened");
            }
        });
        aMultiUserChat.addMessageListener(new MessageListener(){
            @Override
            public void processMessage(Message message) {
                PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MultiUserChat::MessageListener::processMessage happened. " + message.getBody() );
            }
        });
        aMultiUserChat.addParticipantListener(new PresenceListener(){
            @Override
            public void processPresence(Presence presence) {
                PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MultiUserChat::PresenceListener::processPresence happened");
            }
        });
        aMultiUserChat.addParticipantStatusListener(new MucParticipantStatusListener());
        aMultiUserChat.addUserStatusListener(new MucUserStatusListener());
        aMultiUserChat.addSubjectUpdatedListener(new SubjectUpdatedListener(){
            @Override
            public void subjectUpdated(String subject, String from) {
                PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MultiUserChat::SubjectUpdatedListener::subjectUpdated happened");
            }
        });
        return aMultiUserChat;
    }

    private void initializePeonyTalkerPart() {
        currentTalkerNameLabel.setText(currentEmployee.getLoginName());
        
        //Add buddy
        Button btn = new Button();
        btn.setTooltip(new Tooltip("Add Buddy"));
        btn.setGraphic(PeonyGraphic.getImageView("user_add.png"));
        btn.setOnAction((ActionEvent event) -> {
            addBuddy();
        });
        buddyGroupButtonHBox.getChildren().add(btn);btn = new Button();
        
        //Add Group
        btn.setTooltip(new Tooltip("Add Group"));
        btn.setGraphic(PeonyGraphic.getImageView("group_edit.png"));
        btn.setOnAction((ActionEvent event) -> {
            addGroup();
        });
        buddyGroupButtonHBox.getChildren().add(btn);
        
        rosterTreeView.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() == 2){
                    TreeItem<PeonyTreeItemData> aBuddyTreeItem = rosterTreeView.getSelectionModel().getSelectedItem();
                    if (aBuddyTreeItem != null){
                        if (aBuddyTreeItem.getParent().equals(offlineGroupItem)){
                            PeonyFaceUtils.publishMessageOntoOutputWindow("The selected buddy is offline. He/she may not receive your message.");
                        }
                        PeonyTreeItemData treeItemData = aBuddyTreeItem.getValue();
                        if (treeItemData instanceof PeonyBuddyTreeItemData){
                            displayTalkingBoard(((PeonyBuddyTreeItemData)treeItemData).getBuddyJid());
                        }
                    }
                }
            }
        });
        
        /**
         * initialize the talker connection to the remote server
         */
        this.getCachedThreadPoolExecutorService().submit(new PeonyTalkerInitializer());
    }

    private void initializeMyJobsPart() {
        
        jobAssignedToOthersVBox.prefWidthProperty().bind(jobAssignedToOthersScrollPane.widthProperty().subtract(8));
        jobAssignedToOthersVBox.minHeightProperty().bind(jobAssignedToOthersFlowPane.heightProperty());
        jobAssignedToOthersFlowPane.prefWidthProperty().bind(jobAssignedToOthersScrollPane.widthProperty().subtract(10));
        
        followUpJobsVBox.prefWidthProperty().bind(followUpJobsScrollPane.widthProperty().subtract(8));
        followUpJobsVBox.minHeightProperty().bind(followUpJobsFlowPane.heightProperty());
        followUpJobsFlowPane.prefWidthProperty().bind(followUpJobsScrollPane.widthProperty().subtract(10));
        
        leftHistorialJobsVBox.prefWidthProperty().bind(leftHistorialJobsScrollPane.widthProperty().subtract(8));
        leftHistorialJobsVBox.minHeightProperty().bind(leftHistorialJobsFlowPane.heightProperty());
        leftHistorialJobsFlowPane.prefWidthProperty().bind(leftHistorialJobsScrollPane.widthProperty().subtract(10));
        
        rightTodayJobsVBox.prefWidthProperty().bind(rightTodayJobsScrollPane.widthProperty().subtract(8));
        rightTodayJobsVBox.minHeightProperty().bind(rightTodayJobsFlowPane.heightProperty());
        rightTodayJobsFlowPane.prefWidthProperty().bind(rightTodayJobsScrollPane.widthProperty().subtract(10));
        
        
        myDailyReportsButton.setOnAction((ActionEvent event) -> {
            //display daily report
            Lookup.getDefault().lookup(PeonyManagementService.class).displayMyDailyReportTopComponent();
        });
        
        assignJobsButton.setOnAction((ActionEvent event) -> {
            PeonyJobAssignmentDialog aPeonyJobAssignmentDialog = new PeonyJobAssignmentDialog(null, true);
            aPeonyJobAssignmentDialog.addPeonyFaceEventListener(this);
            aPeonyJobAssignmentDialog.launchPeonyJobAssignmentDialog("Assign Job", null);
        });
        
        this.getCachedThreadPoolExecutorService().submit(new PeonyMyJobListInitializer());
    }

    private synchronized void startTodayJobPaneRefresher() {
        if (todayJobPaneRefresher != null){
            if (!todayJobPaneRefresher.isAlive()){
                todayJobPaneRefresher.start();
            }
        }
    }

    public synchronized void stopTodayJobPaneRefresher() {
        if (todayJobPaneRefresher != null){
            todayJobPaneRefresher.interrupt();
        }
    }

    public void refreshPeonyDailyReport(final PeonyDailyReport dailyReport){
        if (Platform.isFxApplicationThread()){
            refreshReportDescriptionHelper(dailyReport);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    refreshReportDescriptionHelper(dailyReport);
                }
            });
        }
    }
    
    private void refreshReportDescriptionHelper(final PeonyDailyReport dailyReport){
        if (dailyReport == null){
            return;
        }
        if ((dailyReport.getHistoricalDailyReports() != null) && (!dailyReport.getHistoricalDailyReports().isEmpty())){
            refreshReportDescriptionHelper(dailyReport.getSortedHistoricalDailyReports().get(0));
        }
    }

    public void refreshReportDescription(final G02DailyReport report){
        if (Platform.isFxApplicationThread()){
            refreshReportDescriptionHelper(report);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    refreshReportDescriptionHelper(report);
                }
            });
        }
    }
    private void refreshReportDescriptionHelper(final G02DailyReport report){
        if ((report == null) || (report.getG02DailyReportPK() == null)){
            return;
        }
        JobAssignmentNode aJobAssignmentNode = jobAssignmentStorage.get(report.getG02DailyReportPK().getJobAssignmentUuid());
        if (aJobAssignmentNode == null){
            return;
        }
        aJobAssignmentNode.report = report;
        String reportDescription = report.getWorkingLocation() + ": ";
        reportDescription += report.getWorkingProgress()+ "% / " + report.getWorkingHours() + " hours";
        if (ZcaValidator.isNotNullEmpty(report.getWorkingDescription())){
            reportDescription += " (" + report.getWorkingDescription() + ")";
        }
        aJobAssignmentNode.updateReportDescription(reportDescription);
    }
    
    private void handlePeonyDailyReportSaved(PeonyDailyReportSaved g02DailyReportSaved) {
        if (g02DailyReportSaved == null){
            return;
        }
        try{
            String status = g02DailyReportSaved.getPeonyDailyReport().getJobAssignment().getAssignmentStatus();
            if ((JobAssignmentStatus.COMPLETED.value().equalsIgnoreCase(status)) 
                || (JobAssignmentStatus.FOLLOW_UP.value().equalsIgnoreCase(status)))
            {
                moveCompletedJobNode(g02DailyReportSaved.getPeonyDailyReport());
            }else{
                refreshPeonyDailyReport(g02DailyReportSaved.getPeonyDailyReport());
            }
        }catch (Exception ex){
        
        }
    }
    
    private void moveCompletedJobNode(final PeonyDailyReport peonyDailyReport) {
        if (Platform.isFxApplicationThread()){
            moveCompletedJobNodeHelper(peonyDailyReport);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    moveCompletedJobNodeHelper(peonyDailyReport);
                }
            });
        }
    }
    
    private Node findTargetNode(FlowPane aJobAssignmentFlowPane, String jobAssignmentUuid){
        List<Node> nodes = aJobAssignmentFlowPane.getChildren();
        for (Node node : nodes){
            if (node.getId().equalsIgnoreCase(jobAssignmentUuid)){
                return node;
            }
        }
        return null;
    }
    
    private void moveCompletedJobNodeHelper(final PeonyDailyReport peonyDailyReport) {
        G02JobAssignment aJobAssignment = peonyDailyReport.getJobAssignment();
        
        rightTodayJobsFlowPane.getChildren().remove(findTargetNode(rightTodayJobsFlowPane, aJobAssignment.getJobAssignmentUuid()));
        
        if (JobAssignmentStatus.COMPLETED.value().equalsIgnoreCase(aJobAssignment.getAssignmentStatus())){
            if (!doesFlowPanelContainJobAssignmentNode(leftHistorialJobsFlowPane, peonyDailyReport.getJobAssignment().getJobAssignmentUuid())){
                leftHistorialJobsFlowPane.getChildren().add(0, constructPreviousJobAssignmentNode(peonyDailyReport.getJobContent(), peonyDailyReport.getJobAssignment().getJobAssignmentUuid(),
                        Lookup.getDefault().lookup(PeonyManagementService.class).retrievePeonyEmployeeFullName(peonyDailyReport.getJobAssignment().getAssignFromUuid()), false));
            }
        }else if (JobAssignmentStatus.FOLLOW_UP.value().equalsIgnoreCase(aJobAssignment.getAssignmentStatus())){
            if (!doesFlowPanelContainJobAssignmentNode(followUpJobsFlowPane, peonyDailyReport.getJobAssignment().getJobAssignmentUuid())){
                followUpJobsFlowPane.getChildren().add(0, constructPreviousJobAssignmentNode(peonyDailyReport.getJobContent(), peonyDailyReport.getJobAssignment().getJobAssignmentUuid(),
                        Lookup.getDefault().lookup(PeonyManagementService.class).retrievePeonyEmployeeFullName(peonyDailyReport.getJobAssignment().getAssignFromUuid()), true));
            }
        }else{
        
        }
    
    }
    
    private boolean doesFlowPanelContainJobAssignmentNode(FlowPane aFlowPane, String jobAssignmentUuid) {
        List<Node> nodes = aFlowPane.getChildren();
        for (Node node : nodes){
            if (jobAssignmentUuid.equalsIgnoreCase(node.getId())){
                return true;
            }
        }
        return false;
    }
    
    public void refreshTodayJobPaneForJobAssignmentUpdated(final PeonyJob updatedPeonyJob) {
        if (Platform.isFxApplicationThread()){
            refreshTodayJobPaneForJobAssignmentUpdatedHelper(updatedPeonyJob);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    refreshTodayJobPaneForJobAssignmentUpdatedHelper(updatedPeonyJob);
                }
            });
        }
    }
    private void refreshTodayJobPaneForJobAssignmentUpdatedHelper(final PeonyJob updatedPeonyJob) {
        if (updatedPeonyJob == null){
            return;
        }
        getCachedThreadPoolExecutorService().submit(new TodayJobPaneRefreshTask());
    }

    private void presentPeonyJobAssignmentListHelper(List<PeonyJobAssignment> aPeonyJobAssignmentList) {
        if ((aPeonyJobAssignmentList != null) && (!aPeonyJobAssignmentList.isEmpty())){
            Collections.sort(aPeonyJobAssignmentList, new Comparator<PeonyJobAssignment>(){
                @Override
                public int compare(PeonyJobAssignment o1, PeonyJobAssignment o2) {
                    try{
                        return o1.getJobAssignment().getAssignmentTimestamp().compareTo(o2.getJobAssignment().getAssignmentTimestamp())*(-1);
                    }catch (Exception ex){
                        return 0;
                    }
                }
            });
            
            G02JobAssignment aJobAssignment;
            for (PeonyJobAssignment aPeonyJobAssignment : aPeonyJobAssignmentList){
                aJobAssignment = aPeonyJobAssignment.getJobAssignment();
                if (!jobAssignmentStorage.containsKey(aJobAssignment.getJobAssignmentUuid())){
                    JobAssignmentNode node = constructJobAssignmentNode(aPeonyJobAssignment);
                    if (JobAssignmentStatus.COMPLETED.value().equalsIgnoreCase(aJobAssignment.getAssignmentStatus())){
                        leftHistorialJobsFlowPane.getChildren().add(constructPreviousJobAssignmentNode(aPeonyJobAssignment.getJobMemo(), 
                                aPeonyJobAssignment.getJobAssignment().getJobAssignmentUuid(), aPeonyJobAssignment.getAssignerFullName(), false));
                    }else if (JobAssignmentStatus.FOLLOW_UP.value().equalsIgnoreCase(aJobAssignment.getAssignmentStatus())){
                        followUpJobsFlowPane.getChildren().add(constructPreviousJobAssignmentNode(aPeonyJobAssignment.getJobMemo(), 
                                aPeonyJobAssignment.getJobAssignment().getJobAssignmentUuid(), aPeonyJobAssignment.getAssignerFullName(), true));
                    }else if (PeonyProperties.getSingleton().getCurrentLoginUserUuid().equalsIgnoreCase(aJobAssignment.getAssignToUuid())){
                        rightTodayJobsFlowPane.getChildren().add(node.getBaseNodeHelper(false));
                        getCachedThreadPoolExecutorService().submit(new QueryDailyReportTask(aPeonyJobAssignment.getJobAssignment()));
                    }else{
                        jobAssignedToOthersFlowPane.getChildren().add(node.getBaseNodeHelper(true));
                    }
                    jobAssignmentStorage.put(aJobAssignment.getJobAssignmentUuid(), node);
                }
            }
        }
    }

    private class TodayJobPaneRefreshTask extends Task<PeonyJobAssignmentList>{

        @Override
        protected PeonyJobAssignmentList call() throws Exception {
            try {
                return Lookup.getDefault().lookup(PeonyManagementService.class)
                        .getPeonyManagementRestClient().findEntity_XML(PeonyJobAssignmentList.class, 
                                GardenRestParams.Management.findPeonyJobAssignmentListForEmployeeByPeriodRestParams(
                                        PeonyProperties.getSingleton().getCurrentLoginUserUuid(), 
                                        String.valueOf(ZcaCalendar.setTimePart(new Date(), 0, 0, 0).getTime()), 
                                        String.valueOf(ZcaCalendar.setTimePart(new Date(), 23, 59, 59).getTime())));
            } catch (Exception ex) {
                PeonyFaceUtils.publishMessageOntoOutputWindow("Failed to retrieve today's job assignment list. " + ex.getMessage());
                return null;
            }
        }

        @Override
        protected void failed() {
            PeonyFaceUtils.publishMessageOntoOutputWindow("Today's job assignment list failed. " + getMessage());
        }

        @Override
        protected void succeeded() {
            try {
                PeonyJobAssignmentList result = get();
                if (result != null){
                    presentPeonyJobAssignmentListHelper(result.getPeonyJobAssignmentList());
                }
            } catch (InterruptedException | ExecutionException ex) {
                //Exceptions.printStackTrace(ex);
                PeonyFaceUtils.displayErrorMessageDialog("InterruptedException or ExecutionException: " + getMessage());
            }
        }
    
    }
    
    private class PeonyMyJobListInitializer extends Task<PeonyJobAssignmentList>{

        @Override
        protected PeonyJobAssignmentList call() throws Exception {
            try {
                return Lookup.getDefault().lookup(PeonyManagementService.class)
                        .getPeonyManagementRestClient().findEntity_XML(PeonyJobAssignmentList.class, 
                                GardenRestParams.Management.findPeonyJobAssignmentListForEmployeeRestParams(
                                        PeonyProperties.getSingleton().getCurrentLoginUserUuid()));
            } catch (Exception ex) {
                updateMessage("Failed to retrieve the job assignment list. " + ex.getMessage());
                return null;
            }
            
        }

        @Override
        protected void failed() {
            PeonyFaceUtils.displayErrorMessageDialog("Peony job assignment list failed. " + getMessage());
        }

        @Override
        protected void succeeded() {
            try {
                PeonyJobAssignmentList result = get();
                if (result != null){
                    presentPeonyJobAssignmentListHelper(result.getPeonyJobAssignmentList());
                    startTodayJobPaneRefresher();
                }
            } catch (InterruptedException | ExecutionException ex) {
                //Exceptions.printStackTrace(ex);
                PeonyFaceUtils.displayErrorMessageDialog("InterruptedException or ExecutionException: " + getMessage());
            }
        }
    }
    
    private Node constructPreviousJobAssignmentNode(final G02Memo aG02JobMemo, final String jobAssignmentUuid, final String assignerFullName, final boolean isFollowUp){
        VBox baseNodeVBox = new VBox();
        
        baseNodeVBox.setId(jobAssignmentUuid);
        
        baseNodeVBox.setMaxWidth(300.0);
        baseNodeVBox.setPrefWidth(300.0);
        baseNodeVBox.setMinWidth(300.0);
        baseNodeVBox.setPadding(new Insets(10, 5, 10, 5));
        baseNodeVBox.setSpacing(5.0);
        baseNodeVBox.setStyle("-fx-border-color: gray;");
        
        Label memoLabel;
        if (aG02JobMemo == null){
            memoLabel = new Label("Exception: cannot find the memo for this unknown job assignment.");
            memoLabel.setWrapText(true);
            baseNodeVBox.getChildren().add(memoLabel);
        }else{
            //title
            memoLabel = new Label(aG02JobMemo.getMemoDescription());
            memoLabel.setStyle("-fx-font-weight: bolder; -fx-font-size: 16;");
            memoLabel.setWrapText(true);
            baseNodeVBox.getChildren().add(memoLabel);
            
            //memo timestamp
            memoLabel = new Label("Assigned by: " + assignerFullName);
            memoLabel.setStyle("-fx-font-style: italic;");
            baseNodeVBox.getChildren().add(memoLabel);
            
            //timestamp & details
            FlowPane flowPane = new FlowPane();
            flowPane.setHgap(2.0);
            flowPane.setVgap(2.0);
            //memo
            memoLabel = new Label(ZcaCalendar.convertToMMddyyyyHHmmss(aG02JobMemo.getTimestamp(), "-", "@", ":"));
            memoLabel.setStyle("-fx-font-style: italic;");
            flowPane.getChildren().add(memoLabel);
            //Link
            JFXButton button = new JFXButton("[Details]");
            button.setStyle("-fx-text-fill: blue;");
            final String entityUuid = aG02JobMemo.getEntityUuid();
            final String entityType = aG02JobMemo.getEntityType();
            button.setOnAction((ActionEvent event) -> {
                if (GardenEntityType.TAXPAYER_CASE.name().equalsIgnoreCase(entityType)){
                    Lookup.getDefault().lookup(PeonyTaxpayerService.class).launchPeonyTaxpayerCaseTopComponentByTaxpayerCaseUuid(entityUuid);
                }else if (GardenEntityType.TAXCORP_CASE.name().equalsIgnoreCase(entityType)){
                    Lookup.getDefault().lookup(PeonyTaxcorpService.class).launchPeonyTaxcorpCaseTopComponentByTaxcorpCaseUuid(entityUuid);
                }
            });
            flowPane.getChildren().add(button);
            
            button = new JFXButton("[History]");
            button.setStyle("-fx-text-fill: blue;");
            button.setOnAction((ActionEvent event) -> {
                Lookup.getDefault().lookup(PeonyManagementService.class).displayDailyReportHistoryByJobAssignmentUuid(jobAssignmentUuid);
            });
            flowPane.getChildren().add(button);
            
            baseNodeVBox.getChildren().add(flowPane);
            baseNodeVBox.getChildren().add(new Separator());
            
            if (isFollowUp){
                Button reportButton = new Button("Archive");
                reportButton.getStyleClass().add("peony-success-small-button");
                reportButton.setOnAction((ActionEvent event) -> {
                    if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, 
                            "Are you sure not to follow it up anymore?") == JOptionPane.YES_OPTION)
                    {
                        archiveFollowUpJobAssignment(jobAssignmentUuid);
                    }
                });
                flowPane.getChildren().add(reportButton);
            }
        }

        return baseNodeVBox;
    }
    
    private void archiveFollowUpJobAssignment(String jobAssignmentUuid){
        this.getCachedThreadPoolExecutorService().submit(new Task<PeonyJobAssignment>(){
            @Override
            protected PeonyJobAssignment call() throws Exception {
                JobAssignmentNode aJobAssignmentNode = jobAssignmentStorage.get(jobAssignmentUuid);
                PeonyJobAssignment aPeonyJobAssignment = null;
                if ((aJobAssignmentNode != null) && (aJobAssignmentNode.aPeonyJobAssignment != null)){
                    aPeonyJobAssignment = aJobAssignmentNode.aPeonyJobAssignment;
                    //update status
                    aPeonyJobAssignment.getJobAssignment().setAssignmentStatus(JobAssignmentStatus.COMPLETED.value());
                    //save its status
                    Lookup.getDefault().lookup(PeonyManagementService.class).getPeonyManagementRestClient()
                            .storeEntity_XML(G02JobAssignment.class, 
                                    GardenRestParams.Management.storeG02JobAssignmentRestParams(), 
                                    aPeonyJobAssignment.getJobAssignment());
                }
                return aPeonyJobAssignment;
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayErrorMessageDialog("Peony failed to archive this job assignment. " + getMessage());
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyJobAssignment result = get();
                    if (result == null){
                        PeonyFaceUtils.displayErrorMessageDialog("Peony failed to archive this job assignment for now. Please try it later. " + getMessage());
                    }else{
                        //remove from the follow-up panel
                        followUpJobsFlowPane.getChildren().remove(findTargetNode(followUpJobsFlowPane, result.getJobAssignment().getJobAssignmentUuid()));
                        //insert into the historical panel
                        leftHistorialJobsFlowPane.getChildren().add(0, constructPreviousJobAssignmentNode(result.getJobMemo(), result.getJobAssignment().getJobAssignmentUuid(),
                            Lookup.getDefault().lookup(PeonyManagementService.class).retrievePeonyEmployeeFullName(result.getJobAssignment().getAssignFromUuid()), false));
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("InterruptedException or ExecutionException: " + getMessage());
                }
            }
        });
    }
    
    private class JobAssignmentNode {
        
        private final PeonyJobAssignment aPeonyJobAssignment;
        private final FlowPane reportFlowPane;
        
        private G02DailyReport report;

        public JobAssignmentNode(PeonyJobAssignment aPeonyJobAssignment) {
            this.aPeonyJobAssignment = aPeonyJobAssignment;
            this.reportFlowPane = new FlowPane();
        }
        
        private void updateReportDescription(final String reportDescription){
            if (ZcaValidator.isNotNullEmpty(reportDescription)){
                if (Platform.isFxApplicationThread()){
                    updateReportDescriptionHelper(reportDescription);
                }else{
                    Platform.runLater(new Runnable(){
                        @Override
                        public void run() {
                            updateReportDescriptionHelper(reportDescription);
                        }
                    });
                }
            }
        }
        private void updateReportDescriptionHelper(final String reportDescription){
            Node reportButton = reportFlowPane.getChildren().get(0);
            reportFlowPane.getChildren().clear();
            reportFlowPane.getChildren().add(reportButton);
            Label reportDescriptionLable = new Label(reportDescription);
            reportDescriptionLable.setStyle("-fx-text-fill: #009933; -fx-font-size: 14");
            reportDescriptionLable.setWrapText(true);
            reportDescriptionLable.setMaxSize(225, Double.MAX_VALUE);
            reportFlowPane.getChildren().add(reportDescriptionLable);
        }
        
        private Node getBaseNodeHelper(boolean isForOthers){
            VBox baseNodeVBox = new VBox();
            baseNodeVBox.setId(aPeonyJobAssignment.getJobAssignment().getJobAssignmentUuid());
            baseNodeVBox.setMaxWidth(350.0);
            baseNodeVBox.setPrefWidth(350.0);
            baseNodeVBox.setMinWidth(350.0);
            baseNodeVBox.setPadding(new Insets(10, 15, 10, 15));
            baseNodeVBox.setSpacing(5.0);
            baseNodeVBox.setStyle("-fx-border-color: #009933;");
            
            final G02Memo targetG02JobMemo = aPeonyJobAssignment.getJobMemo();
            Label memoLabel;
            if (targetG02JobMemo == null){
                memoLabel = new Label("Exception: cannot find the memo for this unknown job assignment.");
                memoLabel.setWrapText(true);
                baseNodeVBox.getChildren().add(memoLabel);
            }else{
                //title
                memoLabel = new Label(targetG02JobMemo.getMemoDescription());
                memoLabel.setStyle("-fx-font-weight: bolder; -fx-font-size: 14;");
                memoLabel.setWrapText(true);
                baseNodeVBox.getChildren().add(memoLabel);

                FlowPane flowPane = new FlowPane();
                flowPane.setHgap(2.0);
                flowPane.setVgap(2.0);
                //memo assigner
                memoLabel = new Label("Assigned by " + aPeonyJobAssignment.getAssignerFullName());
                //memoLabel.setStyle("-fx-font-style: italic;");
                flowPane.getChildren().add(memoLabel);
                //Link
                JFXButton detailsButton = new JFXButton("[Details]");
                detailsButton.setStyle("-fx-text-fill: blue;");
                final String entityUuid = targetG02JobMemo.getEntityUuid();
                final String entityType = targetG02JobMemo.getEntityType();
                detailsButton.setOnAction((ActionEvent event) -> {
                    if (GardenEntityType.TAXPAYER_CASE.name().equalsIgnoreCase(entityType)){
                        Lookup.getDefault().lookup(PeonyTaxpayerService.class).launchPeonyTaxpayerCaseTopComponentByTaxpayerCaseUuid(entityUuid);
                    }else if (GardenEntityType.TAXCORP_CASE.name().equalsIgnoreCase(entityType)){
                        Lookup.getDefault().lookup(PeonyTaxcorpService.class).launchPeonyTaxcorpCaseTopComponentByTaxcorpCaseUuid(entityUuid);
                    }
                });
                flowPane.getChildren().add(detailsButton);

                baseNodeVBox.getChildren().add(flowPane);
                
                //memo timestamp
                flowPane = new FlowPane();
                flowPane.setHgap(2.0);
                flowPane.setVgap(2.0);
                memoLabel = new Label(ZcaCalendar.convertToMMddyyyyHHmmss(targetG02JobMemo.getTimestamp(), "-", "@", ":"));
                memoLabel.setStyle("-fx-font-style: italic;");
                flowPane.getChildren().add(memoLabel);
                //history button: only for "myself"
                if (!isForOthers){
                    JFXButton historyButton = new JFXButton("[History]");
                    historyButton.setStyle("-fx-text-fill: blue;");
                    historyButton.setOnAction((ActionEvent event) -> {
                        Lookup.getDefault().lookup(PeonyManagementService.class).displayDailyReportHistoryByJobAssignmentUuid(aPeonyJobAssignment.getJobAssignment().getJobAssignmentUuid());
                    });
                    flowPane.getChildren().add(historyButton);
                }
                baseNodeVBox.getChildren().add(flowPane);
                
                baseNodeVBox.getChildren().add(new Separator());

                //memo
                memoLabel = new Label(targetG02JobMemo.getMemo());
                memoLabel.setStyle("-fx-font-size: 14;");
                memoLabel.setWrapText(true);
                baseNodeVBox.getChildren().add(memoLabel);
                
                
                baseNodeVBox.getChildren().add(new Separator());
                //Daily report
                reportFlowPane.setHgap(5.0);
                reportFlowPane.setVgap(5.0);
                if (isForOthers){
                    Button refreshButton = new Button("Refresh Status");
                    refreshButton.getStyleClass().add("peony-success-small-button");
                    refreshButton.setOnAction((ActionEvent event) -> {
                        getCachedThreadPoolExecutorService().submit(new RefreshMyAssignedJobStatucTask(aPeonyJobAssignment, reportFlowPane, report));
                    });
                    reportFlowPane.getChildren().add(refreshButton);
                    String status = "0.0%";
                    final Label defaultLabel = new Label(status);
                    defaultLabel.setStyle("-fx-text-fill: #ff6600; -fx-font-size: 14");
                    reportFlowPane.getChildren().add(defaultLabel);
                    baseNodeVBox.getChildren().add(reportFlowPane);
                    //retrieve the working status
                    getCachedThreadPoolExecutorService().submit(new RefreshMyAssignedJobStatucTask(aPeonyJobAssignment, reportFlowPane, report));
                }else{
                    //only display the reportFlowPane for the job to "myself"
                    Button reportButton = new Button("Report");
                    reportButton.getStyleClass().add("peony-success-small-button");
                    reportButton.setOnAction((ActionEvent event) -> {
                        displayPeonyDailyReportEditor(aPeonyJobAssignment.getJobAssignment().getJobAssignmentUuid());
                    });
                    reportFlowPane.getChildren().add(reportButton);
                    Label defaultLabel = new Label("not reported yet");
                    defaultLabel.setStyle("-fx-text-fill: #ff6600; -fx-font-size: 14");
                    reportFlowPane.getChildren().add(defaultLabel);
                    baseNodeVBox.getChildren().add(reportFlowPane);
                }
            }
            return baseNodeVBox;
        }
    }
    
    private class RefreshMyAssignedJobStatucTask extends Task<G02DailyReport>{
        
        private final PeonyJobAssignment aPeonyJobAssignment;
        private final FlowPane reportFlowPane;
        private final G02DailyReport report;

        public RefreshMyAssignedJobStatucTask(PeonyJobAssignment aPeonyJobAssignment, FlowPane reportFlowPane, G02DailyReport report) {
            this.aPeonyJobAssignment = aPeonyJobAssignment;
            this.reportFlowPane = reportFlowPane;
            this.report = report;
        }
        
        @Override
        protected G02DailyReport call() throws Exception {
            try {
                return Lookup.getDefault().lookup(PeonyManagementService.class)
                        .getPeonyManagementRestClient().findEntity_XML(G02DailyReport.class, 
                                GardenRestParams.Management.findLatestDailyReportRestParams(
                                        aPeonyJobAssignment.getJobAssignment().getJobAssignmentUuid(),
                                        aPeonyJobAssignment.getJobAssignment().getAssignToUuid()));
            } catch (Exception ex) {
                PeonyFaceUtils.publishMessageOntoOutputWindow("Failed to retrieve latest daily reports. " + ex.getMessage());
                return null;
            }
        }
        @Override
        protected void succeeded() {
            try {
                G02DailyReport result = get();
                if (result != null){
                    Node refreshButton = reportFlowPane.getChildren().get(0);
                    reportFlowPane.getChildren().clear();
                    reportFlowPane.getChildren().add(refreshButton);
                    Label reportDescriptionLable = new Label("Status: " + result.getWorkingProgress()+ "% / " + report.getWorkingHours() + " hours");
                    reportDescriptionLable.setStyle("-fx-text-fill: #009933; -fx-font-size: 14");
                    reportDescriptionLable.setWrapText(true);
                    reportDescriptionLable.setMaxSize(225, Double.MAX_VALUE);
                    reportFlowPane.getChildren().add(reportDescriptionLable);
                }
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
    private void displayPeonyDailyReportEditor(String jobAssignmentUuid){
        this.getCachedThreadPoolExecutorService().submit(new Task<PeonyDailyReport>(){
            @Override
            protected PeonyDailyReport call() throws Exception {
                JobAssignmentNode aJobAssignmentNode = jobAssignmentStorage.get(jobAssignmentUuid);
                if (aJobAssignmentNode == null){
                    return null;
                }
                G02JobAssignment jobAssignment = aJobAssignmentNode.aPeonyJobAssignment.getJobAssignment();
                if (jobAssignment == null){
                    return null;
                }
                G02Memo jobContent = aJobAssignmentNode.aPeonyJobAssignment.getJobMemo();
                PeonyDailyReport aPeonyDailyReport = new PeonyDailyReport();
                String todayReportDate = ZcaCalendar.convertToMMddyyyy(new Date(), "-");
                if ((aJobAssignmentNode.report == null) || (!todayReportDate.equalsIgnoreCase(aJobAssignmentNode.report.getG02DailyReportPK().getReportDate()))){
                    G02DailyReport dailyReport = new G02DailyReport();
                    G02DailyReportPK g02DailyReportPK = new G02DailyReportPK();
                    g02DailyReportPK.setJobAssignmentUuid(jobAssignment.getJobAssignmentUuid());
                    g02DailyReportPK.setReportDate(todayReportDate);
                    g02DailyReportPK.setReporterUuid(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
                    aPeonyDailyReport.getDailyReport().setG02DailyReportPK(g02DailyReportPK);
                    if (aJobAssignmentNode.report != null){
                        //it is the case: !todayReportDate.equalsIgnoreCase(aJobAssignmentNode.report.getG02DailyReportPK().getReportDate())
                        if (aJobAssignmentNode.report.getWorkingProgress() != null){
                            dailyReport.setWorkingProgress(aJobAssignmentNode.report.getWorkingProgress());
                        }
                    }
                    aPeonyDailyReport.setDailyReport(dailyReport);
                }else{
                    aPeonyDailyReport.setDailyReport(aJobAssignmentNode.report);
                }
                aPeonyDailyReport.setJobAssignment(jobAssignment);
                aPeonyDailyReport.setJobContent(jobContent);
                //historical reports
                aPeonyDailyReport.setHistoricalDailyReports(new ArrayList<>());
                PeonyDailyReportList aPeonyDailyReportList = Lookup.getDefault().lookup(PeonyManagementService.class)
                            .getPeonyManagementRestClient().findEntity_XML(PeonyDailyReportList.class, 
                                    GardenRestParams.Management.findPeonyDailyReportListByJobAssignmentUuidRestParams(jobAssignment.getJobAssignmentUuid()));
                List<PeonyDailyReport> reports = aPeonyDailyReportList.getPeonyDailyReportList();
                if (reports != null){
                    for (PeonyDailyReport report : reports){
                        aPeonyDailyReport.getHistoricalDailyReports().add(report.getDailyReport());
                    }
                }
                return aPeonyDailyReport;
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayErrorMessageDialog("Cannot display this operation dialog. " + getMessage());
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyDailyReport aPeonyDailyReport = get();
                    List<PeonyFaceEventListener> listeners = new ArrayList<>();
                    listeners.add(PeonyTalkerController.this);
                    Lookup.getDefault().lookup(PeonyManagementService.class).displayPeonyDailyReportEditor(aPeonyDailyReport, listeners);
                } catch (InterruptedException | ExecutionException ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("InterruptedException or ExecutionException: " + getMessage());
                }
            }
        });
    }
    
    private JobAssignmentNode constructJobAssignmentNode(final PeonyJobAssignment aPeonyJobAssignment){
        return new JobAssignmentNode(aPeonyJobAssignment);
    }
    
    private class QueryDailyReportTask extends Task<PeonyDailyReport>{
        private final G02JobAssignment jobAssignment;

        public QueryDailyReportTask(G02JobAssignment jobAssignment) {
            this.jobAssignment = jobAssignment;
        }

        @Override
        protected PeonyDailyReport call() throws Exception {
            try {
                return Lookup.getDefault().lookup(PeonyManagementService.class)
                        .getPeonyManagementRestClient().findEntity_XML(PeonyDailyReport.class, 
                                GardenRestParams.Management.findPeonyDailyReportByUuidRestParams(
                                        jobAssignment.getJobAssignmentUuid(),
                                        PeonyProperties.getSingleton().getCurrentLoginUserUuid(), 
                                        ZcaCalendar.convertToMMddyyyy(new Date(), "-")));
            } catch (Exception ex) {
                PeonyFaceUtils.publishMessageOntoOutputWindow("Failed to retrieve today's job assignment list. " + ex.getMessage());
                return null;
            }
        }

        @Override
        protected void failed() {
            PeonyFaceUtils.displayErrorMessageDialog("Peony failed to retrieve this daily report: " + jobAssignment.getJobAssignmentUuid());
        }

        @Override
        protected void succeeded() {

            try {
                refreshPeonyDailyReport(get());
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    
    }
    
    private class PeonyTalkerInitializer extends Task<List<Object>>{
        @Override
        protected List<Object> call() throws Exception {
            
            prepareCurrentXmppAccountReadiness();
            
            initializeTargetXmppConnection();
            
            List<Object> result = new ArrayList<>();
            result.add(initializeMultiUserChatRooms());
            result.add(constructRosterTreeRoot());
            return result;
        }

        @Override
        protected void failed() {
            PeonyFaceUtils.displayErrorMessageDialog("Peony talker failed to initialize talker. " + getMessage());
        }

        @Override
        protected void succeeded() {
            try {
                List<Object> result = get();
                for (Object obj : result){
                    if (obj instanceof TreeItem){
                        resetRosterTreeRoot();
                    }
                }
                talkerTabPane.getSelectionModel().selectedItemProperty().addListener(
                    new ChangeListener<Tab>() {
                        @Override
                        public void changed(ObservableValue<? extends Tab> ov, Tab oldTab, Tab newTab) {
                            manager.removeTabTitleEffect(newTab);
                        }
                    }
                );
            } catch (InterruptedException | ExecutionException ex) {
                //Exceptions.printStackTrace(ex);
                PeonyFaceUtils.displayErrorMessageDialog(getMessage());
            }
        }

        /**
         * Make sure the current Peony login user having a valid XMPP account which 
         * automatcially becomes other Peony's users buddy
         * @throws SmackException
         * @throws IOException
         * @throws XMPPException
         * @throws Exception 
         */
        private void prepareCurrentXmppAccountReadiness() throws SmackException, IOException, XMPPException, Exception {
            G02XmppAccount aXmppAccount = currentEmployee.getXmppAccount();
            //check if current account is XMPP-based
            if (aXmppAccount == null){
                //create one new XMPP account
                ZcaXmppAccount aZcaXmppAccount = PeonyProperties.getSingleton().getCurrentXmppAccount();
                try{
                    ZcaXmppUtils.createGardenXmppAccount(aZcaXmppAccount, new ZcaXmppSettings(_peonyXmppUserName, _peonyXmppUserPassword));
                }catch (XMPPException ex){
                    if (ex instanceof XMPPErrorException){
                        XMPPError error = ((XMPPErrorException)ex).getXMPPError();
                        if (XMPPError.Condition.conflict.equals(error.getCondition())
                                && XMPPError.Type.CANCEL.equals(error.getType()))
                        {
                            PeonyFaceUtils.publishMessageOntoOutputWindow("XMPP account " + aZcaXmppAccount.getLoginName() + " has been existing.");
                        }else{
                            throw ex;
                        }
                    }else{
                        throw ex;
                    }
                }
                //store it into the system
                aXmppAccount = new G02XmppAccount();
                aXmppAccount.setLoginName(aZcaXmppAccount.getLoginName());
                aXmppAccount.setPassword(aZcaXmppAccount.getPassword());
                aXmppAccount.setEncryptedPassword(RoseWebCipher.getSingleton().encrypt(aZcaXmppAccount.getPassword()));
                aXmppAccount.setXmppAccountUuid(currentEmployee.getEmployeeInfo().getEmployeeAccountUuid());
                currentEmployee.setXmppAccount(aXmppAccount);
                Lookup.getDefault().lookup(PeonyManagementService.class).getPeonyManagementRestClient()
                        .storeEntity_XML(PeonyEmployee.class, GardenRestParams.Management.storePeonyEmployeeRestParams(), currentEmployee);
            }
        }

        private TreeItem<PeonyTreeItemData> constructRosterTreeRoot() {
            rosterTreeRoot = new TreeItem<>(new PeonyBuddyGroupTreeItemData("Peony Talker"), PeonyGraphic.getImageView("talker24.png"));
            //initialize buddy list
            Roster roster = Roster.getInstanceFor(targetXmppConnection);
            roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
            roster.addRosterListener(new RosterListener(){
                @Override
                public void entriesAdded(Collection<String> addresses) {
                    PeonyFaceUtils.publishMessageOntoOutputWindow(">>> roster::entriesAdded happened");
                }

                @Override
                public void entriesUpdated(Collection<String> addresses) {
                    PeonyFaceUtils.publishMessageOntoOutputWindow(">>> roster::entriesUpdated happened");
                }

                @Override
                public void entriesDeleted(Collection<String> addresses) {
                    PeonyFaceUtils.publishMessageOntoOutputWindow(">>> roster::entriesDeleted happened");
                }

                @Override
                public void presenceChanged(Presence presence) {
                    String remoteUserName = ZcaXmppSettings.parseChatUserName(presence.getFrom());
                    PeonyFaceUtils.publishMessageOntoOutputWindow(">>> roster::presenceChanged happened: " + remoteUserName);
                    PeonyBuddyTreeItemData aPeonyBuddyTreeItemData = buddyItemsCache.get(remoteUserName);
                    if (aPeonyBuddyTreeItemData != null){
                        removeFromGroupTreeItemOwners(aPeonyBuddyTreeItemData);
                        if (Presence.Type.unavailable.equals(presence.getType())){
                            offlineGroupItem.getChildren().add(new TreeItem<>(aPeonyBuddyTreeItemData, PeonyGraphic.getImageView("user_gray.png")));
                        }else{
                            addIntoGroupTreeItemOwners(aPeonyBuddyTreeItemData);
                        }
                    }
                }
            });
            roster.addRosterLoadedListener(new RosterLoadedListener(){
                @Override
                public void onRosterLoaded(Roster roster) {
                    PeonyFaceUtils.publishMessageOntoOutputWindow(">>> roster::onRosterLoaded happened");
                }
            });
            Collection<RosterEntry> entries = roster.getEntries();
            for (RosterEntry entry : entries) {
                addPeonyBuddyTreeItemData(entry, roster.getPresence(entry.getUser()));
            }
            
            //make sure to be added as buddy to other employees' XMPP account
            PeonyEmployeeList peonyEmployeeList = Lookup.getDefault().lookup(PeonyManagementService.class).retrievePeonyEmployeeList();
            if (peonyEmployeeList != null){
                List<PeonyEmployee> aPeonyEmployeeList = peonyEmployeeList.getPeonyEmployeeList();
                G02XmppAccount aXmppAccount;
                String userJid;
                for (PeonyEmployee aPeonyEmployee : aPeonyEmployeeList){
                    aXmppAccount = aPeonyEmployee.getXmppAccount();
                    if ((aXmppAccount != null) && (!buddyItemsCache.containsKey(aXmppAccount.getLoginName())) 
                            && (!currentEmployee.getEmployeeInfo().getEmployeeAccountUuid().equalsIgnoreCase(aXmppAccount.getXmppAccountUuid())))
                    {
                        try {
                            //request to add roster here
                            userJid = aXmppAccount.getLoginName()+"@"+targetXmppConnection.getServiceName();
                            roster.createEntry(userJid, aXmppAccount.getLoginName(), null);
                            addPeonyBuddyTreeItemData(roster.getEntry(userJid), roster.getPresence(userJid));
                        } catch (SmackException.NotLoggedInException | SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException ex) {
                            //Exceptions.printStackTrace(ex);
                            PeonyFaceUtils.publishMessageOntoOutputWindow("Something wrong while current talker is adding a buddy: " + aXmppAccount.getLoginName());
                        }
                    }
                }
            }
            rosterTreeRoot.setExpanded(true);
            return rosterTreeRoot;
        }

        private List<TreeItem<PeonyTreeItemData>> initializeMultiUserChatRooms() {
            //todo zzj: initialize these items
            List<TreeItem<PeonyTreeItemData>> result = new ArrayList<>();
            for (String chatRoomJid : _peonyXmppPredefinedChatRoomJidList){
                displayMutliUserTalkingTab(chatRoomJid);
            }
            
            return result;
        }

        private void addPeonyBuddyTreeItemData(RosterEntry rosterEntry, Presence rosterEntryPresence) {
            if (buddyItemsCache.containsKey(rosterEntry.getName())){
                return;
            }
            Presence.Type rosterEntryPresenceType = ((rosterEntryPresence == null) ? Presence.Type.unavailable : rosterEntryPresence.getType());
            PeonyBuddyTreeItemData thePeonyBuddyTreeItemData;
            ImageView theBuddyTreeItemIcon;
            String buddyFullName = Lookup.getDefault().lookup(PeonyManagementService.class).retrieveXmppAccountFullName(rosterEntry.getName());
            if (Presence.Type.unavailable.equals(rosterEntryPresenceType)){
                thePeonyBuddyTreeItemData = new PeonyBuddyTreeItemData(rosterEntry, buddyFullName, true); 
                theBuddyTreeItemIcon = PeonyGraphic.getImageView("user_gray.png");
            }else{
                thePeonyBuddyTreeItemData = new PeonyBuddyTreeItemData(rosterEntry, buddyFullName, false);
                theBuddyTreeItemIcon = PeonyGraphic.getImageView("user.png");
            }
            buddyItemsCache.put(thePeonyBuddyTreeItemData.getBuddyName(), thePeonyBuddyTreeItemData);
            
            TreeItem<PeonyTreeItemData> groupItem;
            List<RosterGroup> rosterGroups = rosterEntry.getGroups();
            if ((rosterGroups == null) || (rosterGroups.isEmpty())){
                if (thePeonyBuddyTreeItemData.isOffline()){
                    offlineGroupItem.getChildren().add(new TreeItem<>(thePeonyBuddyTreeItemData, theBuddyTreeItemIcon));
                }else{
                    onlineGroupItem.getChildren().add(new TreeItem<>(thePeonyBuddyTreeItemData, theBuddyTreeItemIcon));
                }
            }else{
                if (thePeonyBuddyTreeItemData.isOffline()){
                    offlineGroupItem.getChildren().add(new TreeItem<>(thePeonyBuddyTreeItemData, theBuddyTreeItemIcon));
                }else{
                    for (RosterGroup rosterGroup : rosterGroups){
                        groupItem = groupItemsCache.get(rosterGroup.getName());
                        if (groupItem == null){
                            groupItem = new TreeItem<>(new PeonyBuddyGroupTreeItemData(rosterGroup), PeonyGraphic.getImageView("online_group24.png"));
                            groupItem.getChildren().add(new TreeItem<>(thePeonyBuddyTreeItemData, theBuddyTreeItemIcon));
                            groupItem.setExpanded(true);
                            rosterTreeRoot.getChildren().add(groupItem);
                        }
                        groupItemsCache.put(rosterGroup.getName(), groupItem);
                    }
                }
            }
        }

        /**
         * Initialize the XMPP connection to the remote server and set up automatic connecting
         * @throws SmackException
         * @throws IOException
         * @throws XMPPException 
         */
        private void initializeTargetXmppConnection() throws SmackException, IOException, XMPPException {
            
            try {
                if ((targetXmppConnection != null) && (targetXmppConnection.isConnected())){
                    targetXmppConnection.disconnect();
                }
                targetXmppConnection = ZcaXmppUtils.createConnectionInstance(PeonyProperties.getSingleton().getCurrentXmppSettings());
                targetXmppConnection.connect().login();
                
                //automatically reconnect to the server if lost connection
                targetReconnectionManager = ReconnectionManager.getInstanceFor(targetXmppConnection);
                targetReconnectionManager.enableAutomaticReconnection();
                targetChatManager = ChatManager.getInstanceFor(targetXmppConnection);
                targetMultiUserChatManager = MultiUserChatManager.getInstanceFor(targetXmppConnection);
                
                //listen to the traffic
                targetXmppConnection.addSyncStanzaListener(new StanzaListener(){
                    @Override
                    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                        /**
                         * packet could be Message, Presence, or IQ if it is not NULL 
                         */
                        if (packet instanceof Message){
                            Message message = ((Message)packet);
                            String msg = message.getBody();
                            String tabTitle = packet.getFrom();
                            if ((ZcaValidator.isNotNullEmpty(msg)) && (ZcaValidator.isNotNullEmpty(tabTitle))){
                                switch (message.getType()){
                                    case chat:
                                        if (!tabTitle.startsWith(PeonyProperties.getSingleton().getCurrentXmppAccount().getLoginName())){
                                            tabTitle = ZcaXmppSettings.parseChatUserJidWithoutResource(tabTitle);
                                            displayTalkingBoard(tabTitle, msg);
                                        }
                                        break;
                                    case groupchat:
                                        String fromUserName = ZcaXmppSettings.parseUserNameForCharRoom(tabTitle);
                                        if (!fromUserName.equalsIgnoreCase(PeonyProperties.getSingleton().getCurrentXmppAccount().getLoginName())){
                                            tabTitle = ZcaXmppSettings.parseChatRoomJidWithoutResource(tabTitle);
                                            displayMutliUserTalkingTab(tabTitle, fromUserName, msg);
                                        }
                                        break;
                                    default:
                                        //do nothing
                                }
                            }
                        }
                    }
                }, 
                new StanzaFilter(){
                    @Override
                    public boolean accept(Stanza stanza) {
                        /**
                         * Here, implement for filter: e.g. spam protection
                         */
                        //PeonyFaceUtils.publishMessageOntoOutputWindow("### StanzaFilter:: accept ........................................");
                        //PeonyFaceUtils.publishMessageOntoOutputWindow("packet.getFrom() - " + stanza.getFrom());
                        //PeonyFaceUtils.publishMessageOntoOutputWindow("packet.getTo() - " + stanza.getTo());
                        //PeonyFaceUtils.publishMessageOntoOutputWindow("packet.toString() - " + stanza.toString());
                        return true;
                    }
                });
                
            } catch (SmackException | IOException | XMPPException ex) {
                //Exceptions.printStackTrace(ex);
                PeonyFaceUtils.publishMessageOntoOutputWindow("Cannot log into the Peony talker.");
                throw ex;
            }
        }

        private void resetRosterTreeRoot() {
            //display retrieved groups with buddies.
            rosterTreeRoot.getChildren().remove(onlineGroupItem);
            rosterTreeRoot.getChildren().add(0, onlineGroupItem);
            rosterTreeRoot.getChildren().remove(offlineGroupItem);
            rosterTreeRoot.getChildren().add(offlineGroupItem);
            rosterTreeView.setRoot(rosterTreeRoot);
        }
    }

    @Override
    public void close() {
        if ((targetXmppConnection != null) && (targetXmppConnection.isConnected())){
            targetXmppConnection.disconnect();
        }
        targetXmppConnection = null;
        
        manager.shutdown();
    }
}
