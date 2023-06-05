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

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.peony.kernel.services.PeonyEmailService;
import com.zcomapproach.garden.peony.kernel.services.PeonyKernelService;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.data.PublicBoardMessageItem;
import com.zcomapproach.garden.peony.view.dialogs.MemoDataEntryDialog;
import com.zcomapproach.garden.peony.view.events.CalendarPeriodSelected;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.events.PeonyMemoCreated;
import com.zcomapproach.garden.peony.view.events.PeonyMemoDeleted;
import com.zcomapproach.garden.peony.view.events.PeonyMemoListCreated;
import com.zcomapproach.garden.peony.view.events.PeonyMemoSaved;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.peony.PeonyMemo;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.garden.util.GardenSorter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.openide.util.Lookup;

/**
 * A shared component for users to publish memo related to some entity 
 * @author zhijun98
 */
public class PublicBoardController extends PeonyEntityOwnerFaceController implements PeonyFaceEventListener{
    @FXML
    private VBox publicBoardVBox;
    @FXML
    private Button periodButton;
    @FXML
    private Button memoButton;
    @FXML
    private Label publcBoardLabel;
    @FXML
    private TreeView<PublicBoardMessageItem> publicBoardTreeView;
    @FXML
    private GridPane topGridPane;
    
    private Date fromDate;
    private Date toDate;
    
    private TreeItem<PublicBoardMessageItem> publicBoardTreeRoot;
    private TreeItem<PublicBoardMessageItem> getPublicBoardTreeRoot(){
        if (publicBoardTreeRoot == null){
            publicBoardTreeRoot = publicBoardTreeView.getRoot();
        }
        return publicBoardTreeRoot;
    }
    
    private final String publicBoardTitle;
    private final List<PeonyMemo> peonyMemoList;
    /**
     * key: PublicBoardMessageItem::memoUuid
 value: TreeItem<PublicBoardMessageItemData>
     */
    private final HashMap<String, TreeItem<PublicBoardMessageItem>> treeItemStorage = new HashMap<>();

    /**
     * This controller is designed for a expected-reasonable-small list of PeonyMemo, consider the performance
     * 
     * @param publicBoardTitle
     * @param peonyMemoList
     * @param fromDate
     * @param toDate
     * @param targetOwner 
     */
    public PublicBoardController(String publicBoardTitle, 
                                List<PeonyMemo> peonyMemoList, 
                                Date fromDate, 
                                Date toDate,
                                Object targetOwner) 
    {
        super(targetOwner);
        this.publicBoardTitle = publicBoardTitle;
        if (peonyMemoList == null){
            peonyMemoList = new ArrayList<>();
        }
        this.peonyMemoList = peonyMemoList;
        if ((fromDate == null) || (toDate == null)){
            fromDate = new Date();
            toDate = fromDate;
        }
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof PeonyMemoListCreated){
            handlePeonyMemoListCreated((PeonyMemoListCreated)event);
        }else if (event instanceof PeonyMemoCreated){
            handlePeonyMemoCreated((PeonyMemoCreated)event);
        }else if (event instanceof PeonyMemoSaved){
            handlePeonyMemoSaved((PeonyMemoSaved)event);
        }else if (event instanceof PeonyMemoDeleted){
            handlePeonyMemoDeleted((PeonyMemoDeleted)event);
        }else if (event instanceof CalendarPeriodSelected){
            handleCalendarPeriodSelected((CalendarPeriodSelected)event);
        }
    }
    
    private void handleCalendarPeriodSelected(final CalendarPeriodSelected event){
        fromDate = event.getFrom();
        toDate = event.getTo();
        PeonyFaceUtils.displayInformationMessageDialog("The period is changed: from " 
                + ZcaCalendar.convertToMMddyyyy(fromDate, "-") + " to " + ZcaCalendar.convertToMMddyyyy(toDate, "-"));
    }

    private void handlePeonyMemoCreated(PeonyMemoCreated peonyMemoCreated) {
        PeonyMemoListCreated peonyMemoListCreated = new PeonyMemoListCreated(null);
        peonyMemoListCreated.getPeonyMemoList().add(peonyMemoCreated.getPeonyMemo());
        handlePeonyMemoListCreated(peonyMemoListCreated);
    }

    private void handlePeonyMemoListCreated(final PeonyMemoListCreated peonyMemoListCreated) {
        if (Platform.isFxApplicationThread()){
            handlePeonyMemoListCreatedHelper(peonyMemoListCreated.getPeonyMemoList());
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    handlePeonyMemoListCreatedHelper(peonyMemoListCreated.getPeonyMemoList());
                }
            });
        }
    }
    

    private void handlePeonyMemoListCreatedHelper(List<PeonyMemo> peonyMemoList) {
        if ((peonyMemoList == null) || (peonyMemoList.isEmpty())){
            return;
        }
        GardenSorter.sortPeonyMemoListByTimestamp(peonyMemoList, false);
        this.peonyMemoList.addAll(peonyMemoList);
        for (PeonyMemo memo : peonyMemoList){
            publishPeonyMemoHelper(memo);
        }
    }

    private void handlePeonyMemoSaved(final PeonyMemoSaved peonyMemoSaved) {
        if (Platform.isFxApplicationThread()){
            handlePeonyMemoSavedHelper(peonyMemoSaved);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    handlePeonyMemoSavedHelper(peonyMemoSaved);
                }
            });
        }
    }
    private void handlePeonyMemoSavedHelper(PeonyMemoSaved peonyMemoSaved){
        if (peonyMemoSaved.getPeonyMemo() == null){
            return;
        }
        
        if (getPublicBoardTreeRoot().getChildren().isEmpty()){
            PublicBoardMessageItem aPublicBoardMessageItem = new PublicBoardMessageItem("Historical Memo List", false);
            aPublicBoardMessageItem.setRootItem(true);
            getPublicBoardTreeRoot().setValue(aPublicBoardMessageItem);
        }
        
        peonyMemoList.remove(peonyMemoSaved.getPeonyMemo());
        peonyMemoList.add(peonyMemoSaved.getPeonyMemo());
        
        GardenSorter.sortPeonyMemoListByTimestamp(peonyMemoList, false);
        
        publishPeonyMemoHelper(peonyMemoSaved.getPeonyMemo());
        
        publicBoardTreeView.refresh();
        
        broadcastPeonyFaceEventHappened(peonyMemoSaved);
        
    }

    private void handlePeonyMemoDeleted(PeonyMemoDeleted peonyMemoDeleted) {
        removePeonyMemoTreeItem(peonyMemoDeleted.getPeonyMemo());
        broadcastPeonyFaceEventHappened(peonyMemoDeleted);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /**
         * publcBoardLabel
         */
        publcBoardLabel.setText(publicBoardTitle);
        /**
         * publicBoardTreeView
         */
        publicBoardTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        TreeItem<PublicBoardMessageItem> root = new TreeItem<>();
        publicBoardTreeView.setRoot(root);
        
        
        GardenSorter.sortPeonyMemoListByTimestamp(peonyMemoList, false);
        
        for (PeonyMemo memo : peonyMemoList){
            publishPeonyMemoHelper(memo);
        }
        
        //setup the root
        PublicBoardMessageItem aPublicBoardMessageItem;
        if (ZcaValidator.isNullEmpty(publicBoardTitle)){
            aPublicBoardMessageItem = new PublicBoardMessageItem("Memo List", false);
        }else{
            aPublicBoardMessageItem = new PublicBoardMessageItem(publicBoardTitle, false);
        }
        aPublicBoardMessageItem.setRootItem(true);
        root.setValue(aPublicBoardMessageItem);
        
        publicBoardTreeView.setContextMenu(createContextMenu());
        getPublicBoardTreeRoot().setExpanded(true);
        
        /**
         * periodButton
         */
        periodButton.setGraphic(PeonyGraphic.getImageView("calendar.png"));
        periodButton.setOnAction((ActionEvent event) -> {
            List<PeonyFaceEventListener> peonyFaceEventListeners = new ArrayList<>();
            peonyFaceEventListeners.add(this);
            Lookup.getDefault().lookup(PeonyKernelService.class).displayPeriodSelectionDialog("Select Period: " + publicBoardTitle, peonyFaceEventListeners, fromDate, toDate);
        });
        
        /**
         * memoButton
         */
        memoButton.setGraphic(PeonyGraphic.getImageView("application_edit.png"));
        memoButton.setOnAction((ActionEvent event) -> {
//            publicBoardTreeView.scrollTo(getPublicBoardTreeRoot().getChildren().size()-1);
//            publicBoardTreeView.getSelectionModel().select(15);
            displayMemoDataEntryDialog("Memo: " + publicBoardTitle, null, true, false, true, getTargetOwner());
        });
    }

    /**
     * 
     * @param dialogTitle
     * @param aPeonyMemo
     * @param saveBtnRequired
     * @param deleteBtnRequired
     * @param closeBtnRequired
     * @param targetOwner - the entity own this memo
     */
    public void displayMemoDataEntryDialog(String dialogTitle, 
                                        PeonyMemo aPeonyMemo, 
                                        boolean saveBtnRequired, 
                                        boolean deleteBtnRequired, 
                                        boolean closeBtnRequired,
                                        Object targetOwner) 
    {
        if (aPeonyMemo == null){
            aPeonyMemo = new PeonyMemo();
            aPeonyMemo.setOperator(PeonyProperties.getSingleton().getCurrentLoginEmployee());
            aPeonyMemo.getMemo().setOperatorAccountUuid(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
        }
        MemoDataEntryDialog aMemoDataEntryDialog = new MemoDataEntryDialog(null, true);
        aMemoDataEntryDialog.addPeonyFaceEventListener(this);
        aMemoDataEntryDialog.launchMemoDataEntryDialog(dialogTitle, aPeonyMemo, saveBtnRequired, deleteBtnRequired, closeBtnRequired, targetOwner);
    }

    @Override
    protected void decoratePeonyFaceAfterLoadingHelper(Scene scene) {
////        publicBoardTreeView.prefHeightProperty().bind(publicBoardVBox.heightProperty().subtract(topGridPane.heightProperty().subtract(50)));
////        publicBoardTreeView.prefWidthProperty().bind(publicBoardVBox.widthProperty());
    }
    
    private PeonyMemo findPeonyMemo(String memoUuid){
        PeonyMemo memo = null;
        for (PeonyMemo aPeonyMemo : peonyMemoList){
            if (aPeonyMemo.getMemo().getMemoUuid().equalsIgnoreCase(memoUuid)){
                memo = aPeonyMemo;
                break;
            }
        }
        return memo;
    }

    private void removePeonyMemoTreeItem(final PeonyMemo peonyMemo) {
        if (Platform.isFxApplicationThread()){
            removePeonyMemoTreeItemHelper(peonyMemo);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    removePeonyMemoTreeItemHelper(peonyMemo);
                }
            });
        }
    }
    private void removePeonyMemoTreeItemHelper(final PeonyMemo peonyMemo) {
        TreeItem<PublicBoardMessageItem> aTreeItem = treeItemStorage.get(peonyMemo.getMemo().getMemoUuid());
        TreeItem<PublicBoardMessageItem> parentTreeItem = treeItemStorage.get(peonyMemo.getMemo().getInitialMemoUuid());
        if (parentTreeItem == null){
            getPublicBoardTreeRoot().getChildren().remove(aTreeItem);
        }else{
            parentTreeItem.getChildren().remove(aTreeItem);
        }
        publicBoardTreeView.refresh();
    }

    @Override
    public void publishPeonyMemo(final PeonyMemo peonyMemo) {
        if (Platform.isFxApplicationThread()){
            publishPeonyMemoHelper(peonyMemo);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    publishPeonyMemoHelper(peonyMemo);
                }
            });
        }
    }
    
    private TreeItem<PublicBoardMessageItem> publishPeonyMemoHelper(PeonyMemo memo){
        if (memo == null){
            return null;
        }
        //TreeItem<PublicBoardMessageItemData> publicBoardTreeRoot = getPublicBoardTreeRoot();
        TreeItem<PublicBoardMessageItem> parentTreeItem;
        TreeItem<PublicBoardMessageItem> aTreeItem = treeItemStorage.get(memo.getMemo().getMemoUuid());
        if (aTreeItem == null){
            aTreeItem = new TreeItem<>();
            aTreeItem.setValue(new PublicBoardMessageItem(memo, ZcaValidator.isNullEmpty(memo.getMemo().getInitialMemoUuid())));
            if (ZcaValidator.isNullEmpty(memo.getMemo().getInitialMemoUuid())){
                getPublicBoardTreeRoot().getChildren().add(0, aTreeItem);
            }else{
                parentTreeItem = publishPeonyMemoHelper(findPeonyMemo(memo.getMemo().getInitialMemoUuid()));
                if (parentTreeItem == null){
                    getPublicBoardTreeRoot().getChildren().add(0, aTreeItem);
                }else{
                    parentTreeItem.getChildren().add(0, aTreeItem);
                }
            }
            if (GardenEntityType.JOB_ASSIGNMENT.name().equalsIgnoreCase(memo.getMemo().getMemoType())){
                aTreeItem.setGraphic(PeonyGraphic.getImageView("red_star.png"));
            }else if (PeonyProperties.getSingleton().getCurrentLoginUserUuid().equalsIgnoreCase(memo.getMemo().getOperatorAccountUuid())){
                aTreeItem.setGraphic(PeonyGraphic.getImageView("user_comment.png"));
            }else{
                aTreeItem.setGraphic(PeonyGraphic.getImageView("comment.png"));
            }
            treeItemStorage.put(memo.getMemo().getMemoUuid(), aTreeItem);
        }
        aTreeItem.setExpanded(true);
        return aTreeItem;
    }

    private ContextMenu createContextMenu() {
        ContextMenu aContextMenu = new ContextMenu();
        //Reply to this Memo
        MenuItem replyMemoMenuItem = new MenuItem("Reply this item");
        replyMemoMenuItem.setOnAction((ActionEvent e) -> {
            replyMemoMenuItemHelper(publicBoardTreeView.getSelectionModel().getSelectedItem());
        });
        aContextMenu.getItems().add(replyMemoMenuItem);
        //Edit Memo
        MenuItem updateMemoMenuItem = new MenuItem("Update this item");
        updateMemoMenuItem.setOnAction((ActionEvent e) -> {
            editMemoMenuItemHelper(publicBoardTreeView.getSelectionModel().getSelectedItem());
        });
        aContextMenu.getItems().add(updateMemoMenuItem);
        //Delete Memo
        MenuItem deleteMemoMenuItem = new MenuItem("Delete this item");
        deleteMemoMenuItem.setOnAction((ActionEvent e) -> {
            deleteMemoMenuItemHelper(publicBoardTreeView.getSelectionModel().getSelectedItem());
        });
        aContextMenu.getItems().add(deleteMemoMenuItem);
        //View the related email
        MenuItem viewRelatedEmailMenuItem = new MenuItem("View related email");
        viewRelatedEmailMenuItem.setOnAction((ActionEvent e) -> {
            viewMemoMenuItemHelper(publicBoardTreeView.getSelectionModel().getSelectedItem());
        });
        aContextMenu.getItems().add(viewRelatedEmailMenuItem);
        
        //View the related email
        MenuItem viewEmailTagMenuItem = new MenuItem("View email tag");
        viewEmailTagMenuItem.setOnAction((ActionEvent e) -> {
            viewEmailTagMenuItemHelper(publicBoardTreeView.getSelectionModel().getSelectedItem());
        });
        aContextMenu.getItems().add(viewEmailTagMenuItem);
        
        return aContextMenu;
    }
    
    private void viewEmailTagMenuItemHelper(TreeItem<PublicBoardMessageItem> selectedItem) {
        if ((selectedItem == null) || (getPublicBoardTreeRoot().equals(selectedItem))){
            PeonyFaceUtils.displayErrorMessageDialog("Please select the memo you want to view its related details.");
            return;
        }
        PeonyMemo aPeonyMemo = (PeonyMemo)selectedItem.getValue().getTreeItemData();
        if (GardenEntityType.EMAIL_TAG.name().equalsIgnoreCase(aPeonyMemo.getMemo().getEntityType())){
            Lookup.getDefault().lookup(PeonyEmailService.class).displayEmailTagDialogByTagUuid(aPeonyMemo.getMemo().getEntityUuid());
        }else{
            PeonyFaceUtils.displayWarningMessageDialog("No related detailes available for this message.");
        }
    }
    
    private void viewMemoMenuItemHelper(TreeItem<PublicBoardMessageItem> selectedItem) {
        if ((selectedItem == null) || (getPublicBoardTreeRoot().equals(selectedItem))){
            PeonyFaceUtils.displayErrorMessageDialog("Please select the memo you want to view its related details.");
            return;
        }
        PeonyMemo aPeonyMemo = (PeonyMemo)selectedItem.getValue().getTreeItemData();
        if (GardenEntityType.EMAIL_TAG.name().equalsIgnoreCase(aPeonyMemo.getMemo().getEntityType())){
            Lookup.getDefault().lookup(PeonyEmailService.class).displayOfflineEmailTopComponentByEmailTagUuid(aPeonyMemo.getMemo().getEntityUuid());
        }else if (GardenEntityType.OFFLINE_EMAIL_MESSAGE.name().equalsIgnoreCase(aPeonyMemo.getMemo().getEntityType())){
            Lookup.getDefault().lookup(PeonyEmailService.class).displayOfflineEmailTopComponentByOfflineEmailUuid(aPeonyMemo.getMemo().getEntityUuid());
        }else{
            PeonyFaceUtils.displayWarningMessageDialog("No related emails available for this message.");
        }
    }

    private void replyMemoMenuItemHelper(TreeItem<PublicBoardMessageItem> selectedItem) {
        if ((selectedItem == null) || (getPublicBoardTreeRoot().equals(selectedItem))){
            PeonyFaceUtils.displayErrorMessageDialog("Please select the memo you want to reply.");
            return;
        }
        MemoDataEntryDialog aMemoDataEntryDialog = new MemoDataEntryDialog(null, true);
        aMemoDataEntryDialog.addPeonyFaceEventListener(this);
        PeonyMemo aPeonyMemo = new PeonyMemo();
        aPeonyMemo.setOperator(PeonyProperties.getSingleton().getCurrentLoginEmployee());
        aPeonyMemo.getMemo().setOperatorAccountUuid(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
        aPeonyMemo.getMemo().setInitialMemoUuid(((PeonyMemo)selectedItem.getValue().getTreeItemData()).getMemo().getMemoUuid());
        aMemoDataEntryDialog.launchMemoDataEntryDialog("Memo: " + publicBoardTitle, 
                aPeonyMemo, true, false, true, getTargetOwner());
    }

    private void editMemoMenuItemHelper(TreeItem<PublicBoardMessageItem> selectedItem) {
        if ((selectedItem == null) || (getPublicBoardTreeRoot().equals(selectedItem))){
            PeonyFaceUtils.displayErrorMessageDialog("Please select the memo you want to update.");
            return;
        }
        if (!selectedItem.getChildren().isEmpty()){
            PeonyFaceUtils.displayErrorMessageDialog("You cannot update the selected item because the item has been replied by others.");
            return;
        }
        //check if the current login user can update this memo whose owner should be the same as the current login user.
        PeonyMemo aPeonyMemo = (PeonyMemo)selectedItem.getValue().getTreeItemData();
        if (PeonyProperties.getSingleton().getCurrentLoginUserUuid().equalsIgnoreCase(aPeonyMemo.getOperator().getAccount().getAccountUuid())){
            MemoDataEntryDialog aMemoDataEntryDialog = new MemoDataEntryDialog(null, true);
            aMemoDataEntryDialog.addPeonyFaceEventListener(this);
            aMemoDataEntryDialog.launchMemoDataEntryDialog("Update:", aPeonyMemo, true, false, true, getTargetOwner());
        }else{
            PeonyFaceUtils.displayErrorMessageDialog("You cannot update the selected item because you are not its author.");
        }
    }

    private void deleteMemoMenuItemHelper(TreeItem<PublicBoardMessageItem> selectedItem) {
        if ((selectedItem == null) || (getPublicBoardTreeRoot().equals(selectedItem))){
            PeonyFaceUtils.displayErrorMessageDialog("Please select the item you want to delete.");
            return;
        }
        if (!selectedItem.getChildren().isEmpty()){
            PeonyFaceUtils.displayErrorMessageDialog("You cannot delete this item because the item has been replied by others.");
            return;
        }
        //check if the current login user can update this memo whose owner should be the same as the current login user.
        PeonyMemo aPeonyMemo = (PeonyMemo)selectedItem.getValue().getTreeItemData();
        if (PeonyProperties.getSingleton().getCurrentLoginUserUuid().equalsIgnoreCase(aPeonyMemo.getOperator().getAccount().getAccountUuid())){
            MemoDataEntryDialog aMemoDataEntryDialog = new MemoDataEntryDialog(null, true);
            aMemoDataEntryDialog.addPeonyFaceEventListener(this);
            aMemoDataEntryDialog.launchMemoDataEntryDialog("Confirm Deletion:", aPeonyMemo, false, true, true, getTargetOwner());
        }else{
            PeonyFaceUtils.displayErrorMessageDialog("You cannot delete the selected item.");
        }
    }
    
    public void decoratePublcBoardTitle(final String publcBoardTitleStyleClass){
        List<String> publcBoardTitleStyleClassList = new ArrayList<>();
        publcBoardTitleStyleClassList.add(publcBoardTitleStyleClass);
        decoratePublcBoardTitle(publcBoardTitleStyleClassList);
    }
    
    public void decoratePublcBoardTitle(final List<String> publcBoardTitleStyleClassList){
        if (Platform.isFxApplicationThread()){
            decoratePublcBoardTitleHelper(publcBoardTitleStyleClassList);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    decoratePublcBoardTitleHelper(publcBoardTitleStyleClassList);
                }
            });
        }
    }
    
    private void decoratePublcBoardTitleHelper(List<String> publcBoardTitleStyleClassList){
        for (String publcBoardTitleStyleClass : publcBoardTitleStyleClassList){
            publcBoardLabel.getStyleClass().add(publcBoardTitleStyleClass);
        }
    }

}
