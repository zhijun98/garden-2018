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
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.data.constant.GardenGendar;
import com.zcomapproach.garden.data.constant.GardenSupportedLanguage;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.guard.RoseWebCipher;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.utils.PeonyDataUtils;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.data.PeonyLocationContactTreeItemData;
import com.zcomapproach.garden.peony.view.dialogs.ContactDataEntryDialog;
import com.zcomapproach.garden.peony.view.dialogs.LocationDataEntryDialog;
import com.zcomapproach.garden.peony.view.events.ContactInfoEntityDeleted;
import com.zcomapproach.garden.peony.view.events.ContactInfoEntitySaved;
import com.zcomapproach.garden.peony.view.events.LocationEntityDeleted;
import com.zcomapproach.garden.peony.view.events.LocationEntitySaved;
import com.zcomapproach.garden.peony.view.events.PeonyAccountSaved;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.G02EntityValidator;
import com.zcomapproach.garden.persistence.constant.GardenAccountStatus;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G02Account;
import com.zcomapproach.garden.persistence.entity.G02ContactInfo;
import com.zcomapproach.garden.persistence.entity.G02Location;
import com.zcomapproach.garden.persistence.entity.G02User;
import com.zcomapproach.garden.persistence.peony.PeonyAccount;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.ZcaCalendar;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class PeonyAccountController extends PeonyFaceController implements PeonyFaceEventListener{
    @FXML
    private Label titleLabel;
    @FXML
    private JFXTextField loginNameField;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private JFXPasswordField confirmPasswordField;
    @FXML
    private JFXComboBox<String> languageComboBox;
    @FXML
    private JFXTextField emailField;
    @FXML
    private JFXTextField mobileField;
    @FXML
    private JFXTextField firstNameField;
    @FXML
    private JFXTextField lastNameField;
    @FXML
    private JFXTextField ssnField;
    @FXML
    private JFXComboBox<String> genderComboBox;
    @FXML
    private DatePicker birthDatePicker;
    @FXML
    private JFXTextField occupationField;
    @FXML
    private JFXTextField citizenshipField;
    @FXML
    private JFXTextField memoField;
    @FXML
    private JFXButton saveTargetAccountButton;
    @FXML
    private ScrollPane accountUserScrollPane;
    @FXML
    private AnchorPane accountUserAnchorPane;
    @FXML
    private ScrollPane locationContactScrollPane;
    @FXML
    private AnchorPane locationContactAnchorPane;
    @FXML
    private TreeView<PeonyLocationContactTreeItemData> locationContactTreeView;
    
    private final TreeItem<PeonyLocationContactTreeItemData> locationRoot = new TreeItem<>(new PeonyLocationContactTreeItemData("Location:"));
    private final TreeItem<PeonyLocationContactTreeItemData> contactInfoRoot = new TreeItem<>(new PeonyLocationContactTreeItemData("Contact:"));

    private final PeonyAccount targetPeonyAccount;

    public PeonyAccountController(PeonyAccount targetPeonyAccount) {
        this.targetPeonyAccount = targetPeonyAccount;
    }
    
    public void setTitleLabel(String title){
        //titleLabel.setText("Primary Taxpayer's Address");
        if (Platform.isFxApplicationThread()){
            titleLabel.setText(title);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    titleLabel.setText(title);
                }
            });
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeAccountDataEntryPane();
        initializeUserDataEntryPane();
        initializeLocationContactTreeView();
        
        accountUserAnchorPane.prefWidthProperty().bind(accountUserScrollPane.widthProperty());
        accountUserAnchorPane.prefHeightProperty().bind(accountUserScrollPane.heightProperty());
        locationContactAnchorPane.prefWidthProperty().bind(locationContactScrollPane.widthProperty());
        locationContactAnchorPane.prefHeightProperty().bind(locationContactScrollPane.heightProperty());
        
        saveTargetAccountButton.setGraphic(PeonyGraphic.getImageView("database_save.png"));
        saveTargetAccountButton.setOnAction((ActionEvent event) -> {
            if (ZcaValidator.isNullEmpty(passwordField.getText())){
                PeonyFaceUtils.displayErrorMessageDialog("Please give a password.");
                return;
            }
            
            if (!passwordField.getText().equalsIgnoreCase(confirmPasswordField.getText())){
                PeonyFaceUtils.displayErrorMessageDialog("Please confirm your password. It has to match the original password.");
                return;
            }
            
            saveTargetAccount();
        });
    }

    private void initializeAccountDataEntryPane() {
        G02Account targetAccount = targetPeonyAccount.getAccount();
        PeonyFaceUtils.initializeTextField(loginNameField, targetAccount.getLoginName(), null, "Login name", this);
        PeonyFaceUtils.initializeTextField(passwordField, targetAccount.getPassword(), null, "Password", this);
        PeonyFaceUtils.initializeTextField(confirmPasswordField, null, null, "Confirm the password", this);
        PeonyFaceUtils.initializeComboBox(languageComboBox, GardenSupportedLanguage.getEnumValueList(false), targetAccount.getWebLanguage(), null, "Prefered language", this);
        PeonyFaceUtils.initializeTextField(emailField, targetAccount.getAccountEmail(), null, "Account Email", this);
        PeonyFaceUtils.initializeTextField(mobileField, targetAccount.getMobilePhone(), null, "Mobile phone", this);
    }

    private void initializeUserDataEntryPane() {
        G02User targetUser = targetPeonyAccount.getUser();
        PeonyFaceUtils.initializeTextField(firstNameField, targetUser.getFirstName(), null, "First name", this);
        PeonyFaceUtils.initializeTextField(lastNameField, targetUser.getLastName(), null, "Last name", this);
        PeonyFaceUtils.initializeTextField(ssnField, targetUser.getSsn(), null, "Social security number", this);
        PeonyFaceUtils.initializeComboBox(genderComboBox, GardenGendar.getEnumValueList(false), targetUser.getGender(), null, "Gender", this);
        PeonyFaceUtils.initializeDatePicker(birthDatePicker, ZcaCalendar.convertToLocalDate(targetUser.getBirthday()), null, "Birthday", this);
        PeonyFaceUtils.initializeTextField(occupationField, targetUser.getOccupation(), null, "Occupation", this);
        PeonyFaceUtils.initializeTextField(citizenshipField, targetUser.getCitizenship(), "USA", "Nationality", this);
        PeonyFaceUtils.initializeTextField(memoField, targetUser.getMemo(), null, "Memo, max 450 characters", this);
    }
    
    @Override
    protected void resetDataEntryStyleHelper() {
        loginNameField.setStyle(null);
        passwordField.setStyle(null);
        confirmPasswordField.setStyle(null);
        languageComboBox.setStyle(null);
        emailField.setStyle(null);
        mobileField.setStyle(null);
        firstNameField.setStyle(null);
        lastNameField.setStyle(null);
        ssnField.setStyle(null);
        genderComboBox.setStyle(null);
        birthDatePicker.setStyle(null);
        occupationField.setStyle(null);
        citizenshipField.setStyle(null);
        memoField.setStyle(null);
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof LocationEntitySaved){
            handleLocationEntitySaved((LocationEntitySaved)event);
        }else if (event instanceof LocationEntityDeleted){
            handleLocationEntityDeleted((LocationEntityDeleted)event);
        }else if (event instanceof ContactInfoEntitySaved){
            handleContactInfoEntitySaved((ContactInfoEntitySaved)event);
        }else if (event instanceof ContactInfoEntityDeleted){
            handleContactInfoEntityDeleted((ContactInfoEntityDeleted)event);
        }
    }
    
    private void handleLocationEntitySaved(final LocationEntitySaved locationEntitySaved) {
        G02Location aG02Location = locationEntitySaved.getLocation();
        if (aG02Location == null){
            return;
        }
        if (Platform.isFxApplicationThread()){
            handleLocationEntitySavedHelper(aG02Location);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    handleLocationEntitySavedHelper(aG02Location);
                }
            });
        }
    }
    private void handleLocationEntitySavedHelper(G02Location aG02Location) {
        if (targetPeonyAccount != null){
            if (findG02LocationFromTarget(aG02Location.getLocationUuid()) == null){
                targetPeonyAccount.getLocationList().add(aG02Location);
                locationRoot.getChildren().add(new TreeItem<>(new PeonyLocationContactTreeItemData(aG02Location)));
            }
            locationContactTreeView.refresh();
        }
    }
    
    private void handleLocationEntityDeleted(final LocationEntityDeleted locationEntitySaved) {
        G02Location aG02Location = locationEntitySaved.getLocation();
        if (aG02Location == null){
            return;
        }
        if (Platform.isFxApplicationThread()){
            handleLocationEntityDeletedHelper(aG02Location);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    handleLocationEntityDeletedHelper(aG02Location);
                }
            });
        }
    }
    private void handleLocationEntityDeletedHelper(G02Location aG02Location) {
        if (targetPeonyAccount != null){
            if (findG02LocationFromTarget(aG02Location.getLocationUuid()) != null){
                targetPeonyAccount.getLocationList().remove(aG02Location);
                locationRoot.getChildren().remove(findTreeItemFromRoot(aG02Location.getLocationUuid(), locationRoot));
                locationContactTreeView.refresh();
            }
        }
    }
    
    private G02Location findG02LocationFromTarget(String locationUuid){
        List<G02Location> aG02LocationList = targetPeonyAccount.getLocationList();
        for (G02Location aG02Location : aG02LocationList){
            if (aG02Location.getLocationUuid().equalsIgnoreCase(locationUuid)){
                return aG02Location;
            }
        }
        return null;
    }
    
    private void handleContactInfoEntitySaved(final ContactInfoEntitySaved contactInfoEntitySaved) {
        G02ContactInfo aG02ContactInfo = contactInfoEntitySaved.getContactInfo();
        if (aG02ContactInfo == null){
            return;
        }
        if (Platform.isFxApplicationThread()){
            handleContactInfoEntitySavedHelper(aG02ContactInfo);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    handleContactInfoEntitySavedHelper(aG02ContactInfo);
                }
            });
        }
    }
    private void handleContactInfoEntitySavedHelper(G02ContactInfo aG02ContactInfo) {
        if (targetPeonyAccount != null){
            if (findG02ContactInfoFromTarget(aG02ContactInfo.getContactInfoUuid()) == null){
                targetPeonyAccount.getContactInfoList().add(aG02ContactInfo);
                contactInfoRoot.getChildren().add(new TreeItem<>(new PeonyLocationContactTreeItemData(aG02ContactInfo)));
            }
            locationContactTreeView.refresh();
        }
    }
    
    private void handleContactInfoEntityDeleted(final ContactInfoEntityDeleted contactInfoEntitySaved) {
        G02ContactInfo aG02ContactInfo = contactInfoEntitySaved.getContactInfo();
        if (aG02ContactInfo == null){
            return;
        }
        if (Platform.isFxApplicationThread()){
            handleContactInfoEntityDeletedHelper(aG02ContactInfo);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    handleContactInfoEntityDeletedHelper(aG02ContactInfo);
                }
            });
        }
    }
    private void handleContactInfoEntityDeletedHelper(G02ContactInfo aG02ContactInfo) {
        if (targetPeonyAccount != null){
            if (findG02ContactInfoFromTarget(aG02ContactInfo.getContactInfoUuid()) != null){
                targetPeonyAccount.getContactInfoList().remove(aG02ContactInfo);
                contactInfoRoot.getChildren().remove(findTreeItemFromRoot(aG02ContactInfo.getContactInfoUuid(), contactInfoRoot));
                locationContactTreeView.refresh();
            }
        }
    }
    
    private G02ContactInfo findG02ContactInfoFromTarget(String contactInfoUuid){
        List<G02ContactInfo> aG02ContactInfoList = targetPeonyAccount.getContactInfoList();
        for (G02ContactInfo aG02ContactInfo : aG02ContactInfoList){
            if (aG02ContactInfo.getContactInfoUuid().equalsIgnoreCase(contactInfoUuid)){
                return aG02ContactInfo;
            }
        }
        return null;
    }

    private TreeItem findTreeItemFromRoot(String uuid, TreeItem<PeonyLocationContactTreeItemData> root) {
        List<TreeItem<PeonyLocationContactTreeItemData>> items = root.getChildren();
        for (TreeItem<PeonyLocationContactTreeItemData> item : items){
            if (uuid.equalsIgnoreCase(item.getValue().getTreeItemUuid())){
                return item;
            }
        }
        return null;
    }
    
    private void collectTargetAccountData() throws ZcaEntityValidationException{
        G02Account targetAccount = targetPeonyAccount.getAccount();
        if (ZcaValidator.isNullEmpty(targetAccount.getAccountUuid())){
            targetAccount.setAccountUuid(ZcaUtils.generateUUIDString());
        }
        targetAccount.setAccountStatus(GardenAccountStatus.Valid.name());
        targetAccount.setLoginName(loginNameField.getText());
        targetAccount.setPassword(passwordField.getText());
        targetAccount.setEncryptedPassword(RoseWebCipher.getSingleton().encrypt(targetAccount.getPassword()));
        targetAccount.setWebLanguage(languageComboBox.getValue());
        targetAccount.setAccountEmail(emailField.getText());
        targetAccount.setMobilePhone(mobileField.getText());
                    
        G02EntityValidator.getSingleton().validate(targetAccount);
    }
    
    private void collectTargetUserData() throws ZcaEntityValidationException{
        G02User targetUser = targetPeonyAccount.getUser();
        if (ZcaValidator.isNullEmpty(targetUser.getUserUuid())){
            targetUser.setUserUuid(ZcaUtils.generateUUIDString());
        }
        targetUser.setFirstName(firstNameField.getText());
        targetUser.setLastName(lastNameField.getText());
        targetUser.setSsn(ssnField.getText());
        targetUser.setGender(genderComboBox.getValue());
        targetUser.setBirthday(ZcaCalendar.convertToDate(birthDatePicker.getValue()));
        targetUser.setOccupation(occupationField.getText());
        targetUser.setCitizenship(citizenshipField.getText());
        targetUser.setMemo(memoField.getText());
                    
        G02EntityValidator.getSingleton().validate(targetUser);
    }

    private void saveTargetAccount() {
        Task<PeonyAccount> savePeonyAccountProfileTask = new Task<PeonyAccount>(){
            @Override
            protected PeonyAccount call() throws Exception {
                try{
                    collectTargetAccountData();
                    collectTargetUserData();
                    
                    /**
                     * Location and contact here...
                     */
                    
                }catch (ZcaEntityValidationException ex){
                    highlightBadEntityField(ex);
                    updateMessage(ex.getMessage());
                    return null;
                }
                PeonyAccount aPeonyAccount = null;
                try{
                    G02EntityValidator.getSingleton().validate(targetPeonyAccount);
                    //WS-REST: demands the accurate class in the request
                    if (targetPeonyAccount instanceof PeonyEmployee){
                        aPeonyAccount = Lookup.getDefault().lookup(PeonyManagementService.class).getPeonyManagementRestClient()
                                .storeEntity_XML(PeonyEmployee.class, GardenRestParams.Management.storePeonyEmployeeRestParams(), (PeonyEmployee)targetPeonyAccount);
                    }else{
                        aPeonyAccount = Lookup.getDefault().lookup(PeonyManagementService.class).getPeonyManagementRestClient()
                                .storeEntity_XML(PeonyAccount.class, GardenRestParams.Management.storePeonyAccountRestParams(), targetPeonyAccount);
                    }
                }catch (Exception ex){
                    updateMessage(ex.getMessage());
                }
                return aPeonyAccount;
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyAccount aPeonyAccount = get();
                    if (aPeonyAccount == null){
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }else{
                        resetDataEntryStyle();
                        setDataEntryChanged(false);
                        boolean forNewEntity = targetPeonyAccount.isNewEntity();
                        
                        targetPeonyAccount.setNewEntity(false);
                        
                        PeonyFaceUtils.displayInformationMessageDialog("This profile "+targetPeonyAccount.getPeonyUserFullName()+" is saved.");
                        
                        broadcastPeonyFaceEventHappened(new PeonyAccountSaved(targetPeonyAccount, forNewEntity));
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("This operation failed. " + ex.getMessage());
                }
            }
            
        };
        this.getCachedThreadPoolExecutorService().submit(savePeonyAccountProfileTask);
    }

    private void initializeLocationContactTreeView() {
        TreeItem<PeonyLocationContactTreeItemData> locationContactTreeRoot = new TreeItem<>(new PeonyLocationContactTreeItemData());
        
        //Location...
        List<G02Location> aG02LocationList = targetPeonyAccount.getLocationList();
        if (aG02LocationList != null){
            for (G02Location aG02Location : aG02LocationList){
                locationRoot.getChildren().add(new TreeItem<>(new PeonyLocationContactTreeItemData(aG02Location)));
            }
        }
        locationRoot.setExpanded(true);
        locationContactTreeRoot.getChildren().add(locationRoot);
        
        //Contact Info....
        List<G02ContactInfo> aG02ContactInfoList = targetPeonyAccount.getContactInfoList();
        if (aG02ContactInfoList != null){
            for (G02ContactInfo aG02ContactInfo : aG02ContactInfoList){
                contactInfoRoot.getChildren().add(new TreeItem<>(new PeonyLocationContactTreeItemData(aG02ContactInfo)));
            }
        }
        contactInfoRoot.setExpanded(true);
        locationContactTreeRoot.getChildren().add(contactInfoRoot);
        
        locationContactTreeRoot.setExpanded(true);
        locationContactTreeView.setRoot(locationContactTreeRoot);
        
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addLocationMenuItem = new MenuItem("Add Location");
        addLocationMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addPeonyLocationContactTreeItemData();
            }
        });
        contextMenu.getItems().add(addLocationMenuItem);
        
        MenuItem addContactInfoMenuItem = new MenuItem("Add Contact");
        addContactInfoMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addPeonyContactInfoContactTreeItemData();
            }
        });
        contextMenu.getItems().add(addContactInfoMenuItem);
        
        MenuItem editMenuItem = new MenuItem("Edit Selected Item");
        editMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                editPeonyLocationContactTreeItemData();
            }
        });
        contextMenu.getItems().add(editMenuItem);
        
        MenuItem deleteMenuItem = new MenuItem("Delete Selected Item");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deletePeonyLocationContactTreeItemData();
            }
        });
        contextMenu.getItems().add(deleteMenuItem);
        
        locationContactTreeView.setContextMenu(contextMenu);
    }

    private void addPeonyLocationContactTreeItemData() {
        popupAddressLocationDataEntryDialog();
    }

    private void addPeonyContactInfoContactTreeItemData() {
        popupContactInfoDataEntryDialog();
    }

    private void editPeonyLocationContactTreeItemData() {
        if (SwingUtilities.isEventDispatchThread()){
            displayDialogForPeonyLocationContactTreeItemDataHelper(false);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    displayDialogForPeonyLocationContactTreeItemDataHelper(false);
                }
            });
        }
    }
    
    private void displayDialogForPeonyLocationContactTreeItemDataHelper(boolean forDelete){
        TreeItem<PeonyLocationContactTreeItemData> treeItemData = locationContactTreeView.getSelectionModel().getSelectedItem();
        if ((treeItemData == null) || (treeItemData.getValue() == null) || (treeItemData.getValue().getTreeItemTitle() != null)){
            PeonyFaceUtils.displayErrorMessageDialog("Please select a location address or contact record.");
        }else if (treeItemData.getValue().getLocation() != null){
            launchAddressLocationDataEntryDialog(treeItemData.getValue().getLocation(), forDelete);
        }else if (treeItemData.getValue().getContactInfo() != null){
            launchContactDataEntryDialog(treeItemData.getValue().getContactInfo(), forDelete);
        }
    }

    private void deletePeonyLocationContactTreeItemData() {
        if (SwingUtilities.isEventDispatchThread()){
            displayDialogForPeonyLocationContactTreeItemDataHelper(true);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    displayDialogForPeonyLocationContactTreeItemDataHelper(true);
                }
            });
        }
    }

    private void popupAddressLocationDataEntryDialog() {
        if (SwingUtilities.isEventDispatchThread()){
            popupAddressLocationDataEntryDialogHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    popupAddressLocationDataEntryDialogHelper();
                }
            });
        }
    }
    private void popupAddressLocationDataEntryDialogHelper() {
        if (targetPeonyAccount == null){
            return;
        }
        G02Location aG02Location = new G02Location();
        aG02Location.setLocationUuid(ZcaUtils.generateUUIDString());
        if (targetPeonyAccount instanceof PeonyEmployee){
            aG02Location.setEntityType(GardenEntityType.EMPLOYEE.name());
        }else{
            aG02Location.setEntityType(GardenEntityType.ACCOUNT.name());
        }
        aG02Location.setEntityUuid(targetPeonyAccount.getAccount().getAccountUuid());
        launchAddressLocationDataEntryDialog(aG02Location, false);
    }
    
    private void launchAddressLocationDataEntryDialog(G02Location aG02Location, boolean forDelete){
        LocationDataEntryDialog locationDataEntryDialog = new LocationDataEntryDialog(null, true);
        locationDataEntryDialog.addPeonyFaceEventListener(this);
        String dialogTitle;
        if(targetPeonyAccount.getUser() == null){
            dialogTitle = "Address Information:";
        }else{
            dialogTitle = "Address:" + PeonyDataUtils.createFullName(targetPeonyAccount.getUser().getFirstName(), targetPeonyAccount.getUser().getLastName());
        }
        locationDataEntryDialog.launchAddressLocationDataEntryDialog(dialogTitle, GardenEntityType.ACCOUNT, 
                targetPeonyAccount.getAccount().getAccountUuid(), aG02Location, !forDelete, forDelete, true);
    
    }
    
    private void popupContactInfoDataEntryDialog() {
        if (SwingUtilities.isEventDispatchThread()){
            popupContactInfoDataEntryDialogHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    popupContactInfoDataEntryDialogHelper();
                }
            });
        }
    }

    private void popupContactInfoDataEntryDialogHelper() {
        if (targetPeonyAccount == null){
            return;
        }
        G02ContactInfo aG02ContactInfo = new G02ContactInfo();
        aG02ContactInfo.setContactInfoUuid(ZcaUtils.generateUUIDString());
        aG02ContactInfo.setPreferencePriority(0);
        
        if (targetPeonyAccount instanceof PeonyEmployee){
            aG02ContactInfo.setEntityType(GardenEntityType.EMPLOYEE.name());
        }else{
            aG02ContactInfo.setEntityType(GardenEntityType.ACCOUNT.name());
        }
        
        if (targetPeonyAccount.getAccount() != null){
            aG02ContactInfo.setEntityUuid(targetPeonyAccount.getAccount().getAccountUuid());
        }
        launchContactDataEntryDialog(aG02ContactInfo, false);
    }
    
    private void launchContactDataEntryDialog(G02ContactInfo aG02ContactInfo, boolean forDelete){
        ContactDataEntryDialog contactDataEntryDialog = new ContactDataEntryDialog(null, true);
        contactDataEntryDialog.addPeonyFaceEventListener(this);
        String dialogTitle;
        if(targetPeonyAccount.getUser() == null){
            dialogTitle = "Contact Information:";
        }else{
            dialogTitle = "Contact: " + PeonyDataUtils.createFullName(targetPeonyAccount.getUser().getFirstName(), targetPeonyAccount.getUser().getLastName());
        }
        contactDataEntryDialog.launchContactDataEntryDialog(dialogTitle, GardenEntityType.ACCOUNT, 
                targetPeonyAccount.getAccount().getAccountUuid(), aG02ContactInfo, !forDelete, forDelete, true);
    
    }

}
