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
import com.jfoenix.controls.JFXTextField;
import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.data.constant.UState;
import com.zcomapproach.garden.exception.EntityField;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.view.events.LocationEntityDeleted;
import com.zcomapproach.garden.peony.view.events.LocationEntitySaved;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.persistence.G02EntityValidator;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G02Location;
import com.zcomapproach.garden.persistence.updater.G02DataUpdaterFactory;
import com.zcomapproach.garden.rest.GardenRestParams;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javax.swing.JOptionPane;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class LocationDataEntryController extends PeonyFaceController{
    @FXML
    private Label titleLabel;
    @FXML
    private JFXTextField addressField;
    @FXML
    private JFXTextField cityField;
    @FXML
    private JFXTextField countyField;
    @FXML
    private JFXComboBox<String> stateComboBox;
    @FXML
    private JFXTextField zipCodeField;
    @FXML
    private JFXTextField countryField;
    @FXML
    private JFXTextField memoField;
    /**
     * Functional HBox: save, cancel and delete buttons
     */
    @FXML
    private HBox functionalHBox;
    
    private JFXButton saveButton;   //used by this controller to save itself locally 
    private JFXButton cancelButton; //used for dialog
    private JFXButton deleteButton; //used by this controller to delete itself locally
    private final boolean saveBtnRequired;
    private final boolean deleteBtnRequired;
    private final boolean cancelBtnRequired;
    
    private final G02Location targetLocation;
    private final GardenEntityType entityType;
    private final String entityUuid;

    /**
     * 
     * @param targetLocation
     * @param entityType
     * @param entityUuid
     * @param saveBtnRequired - used by this controller to save itself locally 
     * @param deleteBtnRequired - used by this controller to delete itself locally
     * @param cancelBtnRequired - used for dialog
     */
    public LocationDataEntryController(G02Location targetLocation, 
                                        GardenEntityType entityType, 
                                        String entityUuid, 
                                        boolean saveBtnRequired, 
                                        boolean deleteBtnRequired, 
                                        boolean cancelBtnRequired) 
    {
        if (targetLocation == null){
            targetLocation = new G02Location();
        }
        this.targetLocation = targetLocation;
        this.entityType = entityType;
        this.entityUuid = entityUuid;
        this.saveBtnRequired = saveBtnRequired;
        this.deleteBtnRequired = deleteBtnRequired;
        this.cancelBtnRequired = cancelBtnRequired;
    }
    
    private boolean isSaveBtnRequired() {
        return saveBtnRequired;
    }

    private boolean isDeleteBtnRequired() {
        return deleteBtnRequired;
    }

    private boolean isCancelBtnRequired() {
        return cancelBtnRequired;
    }
    
    private void initializeSaveButton(){
        if (saveButton == null){
            saveButton = new JFXButton("Save");
            saveButton.getStyleClass().add("peony-primary-small-button");
            saveButton.setOnAction((ActionEvent actionEvent) -> {
                saveTargetLocation();
            });
        }
        //display it or not...
        if (isSaveBtnRequired()){
            functionalHBox.getChildren().add(saveButton);
        }else{
            functionalHBox.getChildren().remove(saveButton);
        }
    }
    
    /**
     * Save the target location on the server side and then broadcast this event
     */
    private void saveTargetLocation() {
        Task<Boolean> saveTargetLocationTask = new Task<Boolean>(){
            @Override
            protected Boolean call() throws Exception {
                try {
                    validateTargetLocation();
                    return (Lookup.getDefault().lookup(PeonyManagementService.class)
                            .getPeonyManagementRestClient().storeEntity_XML(G02Location.class, 
                                    GardenRestParams.Management.storeLocationRestParams(), targetLocation) 
                            != null);
                } catch (Exception ex) {
                    if (ex instanceof ZcaEntityValidationException){
                        highlightBadEntityField((ZcaEntityValidationException)ex);
                    }
                    updateMessage("This operation failed. " + ex.getMessage());
                    return false;
                }
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayErrorMessageDialog("Failed to save it because of the technical issue. " + getMessage());
            }

            @Override
            protected void succeeded() {
                try {
                    boolean result = get();
                    if (result){
                        resetDataEntry();
                        resetDataEntryStyle();
                        setDataEntryChanged(false);
                        PeonyFaceUtils.displayInformationMessageDialog("Successfully saved this address location.");
                        broadcastPeonyFaceEventHappened(new LocationEntitySaved(targetLocation));
                        broadcastPeonyFaceEventHappened(new CloseDialogRequest(true));
                    }else{
                        PeonyFaceUtils.displayErrorMessageDialog("Failed to save this address location because of the server-side issue.");
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Failed to complete this operation." + ex.getMessage());
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(saveTargetLocationTask);
    }
    
    private void initializeCancelButton(){
        if (cancelButton == null){
            cancelButton = new JFXButton("Cancel");
            cancelButton.getStyleClass().add("peony-primary-small-button");
            cancelButton.setOnAction((ActionEvent actionEvent) -> {
                if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to close this location dialog?") == JOptionPane.YES_OPTION){
                    broadcastPeonyFaceEventHappened(new CloseDialogRequest(true));
                }
            });
        }
        //display it or not...
        if (isCancelBtnRequired()){
            functionalHBox.getChildren().add(cancelButton);
        }else{
            functionalHBox.getChildren().remove(cancelButton);
        }
    }
    
    private void initializeDeleteButton(){
        if (deleteButton == null){
            deleteButton = new JFXButton("Delete");
            deleteButton.getStyleClass().add("peony-primary-small-button");
            deleteButton.setOnAction((ActionEvent actionEvent) -> {
                if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to delete this location?", "Confirm") == JOptionPane.YES_OPTION){
                    deleteTargetLocation();
                }
            });
        }
        //display it or not...
        if (isDeleteBtnRequired()){
            functionalHBox.getChildren().add(deleteButton);
        }else{
            functionalHBox.getChildren().remove(deleteButton);
        }
    }

    private void deleteTargetLocation() {
        Task<Boolean> deleteLocationTask = new Task<Boolean>(){
            @Override
            protected Boolean call() throws Exception {
                try{
                    return (Lookup.getDefault().lookup(PeonyManagementService.class).getPeonyManagementRestClient()
                        .deleteEntity_XML(G02Location.class, 
                                        GardenRestParams.Management.deleteLocationRestParams(targetLocation.getLocationUuid())) != null);
                }catch (Exception ex){
                    updateMessage("Deletion failed. " + ex.getMessage());
                    return false;
                }
            }
            @Override
            protected void succeeded() {
                try {
                    boolean result = get();
                    if (result){
                        resetDataEntry();
                        resetDataEntryStyle();
                        setDataEntryChanged(false);
                        PeonyFaceUtils.displayInformationMessageDialog("Successfully deleted the address location.");
                        broadcastPeonyFaceEventHappened(new LocationEntityDeleted(targetLocation));
                        broadcastPeonyFaceEventHappened(new CloseDialogRequest(true));
                    }else{
                        PeonyFaceUtils.displayErrorMessageDialog("The server side cannot delete this record for some technical reason.");
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Technical exception raised. " + ex.getMessage());
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(deleteLocationTask);
    }
    
    public void displayDeleteButton(){
        if (Platform.isFxApplicationThread()){
            deleteButton.setVisible(true);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    deleteButton.setVisible(true);
                }
            });
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titleLabel.setText("Address Location");
        initializeLocationDataFields();
        initializeSaveButton();
        initializeCancelButton();
        initializeDeleteButton();
    }
    
    private void initializeLocationDataFields(){
        PeonyFaceUtils.initializeTextField(addressField, targetLocation.getLocalAddress(), null, "Address", this);
        PeonyFaceUtils.initializeTextField(cityField, targetLocation.getCityName(), null, "City", this);
        PeonyFaceUtils.initializeTextField(countyField, targetLocation.getStateCounty(), null, "County name in the state", this);
        PeonyFaceUtils.initializeComboBox(stateComboBox, UState.getEnumValueList(false), targetLocation.getStateName(), UState.NY.value(), "State", this);
        PeonyFaceUtils.initializeTextField(zipCodeField, targetLocation.getZipCode(), null, "Zip", this);
        PeonyFaceUtils.initializeTextField(countryField, targetLocation.getCountry(), "USA", "Nationality", this);
        PeonyFaceUtils.initializeTextField(memoField, targetLocation.getShortMemo(), null, "Memo, max 250 characters", this);
    }

    void refreshDataEntry(final G02Location aG02Location) {
        if (Platform.isFxApplicationThread()){
            refreshDataEntryHelper(aG02Location);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    refreshDataEntryHelper(aG02Location);
                }
            });
        }
    }
    
    private void refreshDataEntryHelper(final G02Location aG02Location) {
        G02DataUpdaterFactory.getSingleton().getG02LocationUpdater().cloneEntity(aG02Location, targetLocation);
        initializeLocationDataFields();
    }

    /**
     * Load the inputs from the GUI into targetLocation with validation
     * @throws ZcaEntityValidationException 
     */
    public void validateTargetLocation() throws ZcaEntityValidationException {
        targetLocation.setLocalAddress(addressField.getText());
        targetLocation.setCityName(cityField.getText());
        targetLocation.setStateCounty(countyField.getText());
        targetLocation.setStateName(stateComboBox.getValue());
        targetLocation.setZipCode(zipCodeField.getText());
        targetLocation.setCountry(countryField.getText());
        targetLocation.setShortMemo(memoField.getText());
        targetLocation.setPreferencePriority(0);
        //respect the existing one...
        if (ZcaValidator.isNullEmpty(targetLocation.getLocationUuid())){
            targetLocation.setLocationUuid(ZcaUtils.generateUUIDString());
        }
        targetLocation.setEntityUuid(entityUuid);
        targetLocation.setEntityType(entityType.name());
        
        try{
            G02EntityValidator.getSingleton().validate(targetLocation);
        }catch(ZcaEntityValidationException ex){
            highlightBadEntityField(ex);
            throw ex;
        }
    }

    @Override
    protected Node getBadEntityField(EntityField entityFiled) {
        switch (entityFiled){
            case CityName:
                return cityField;
            case Country:
                return countyField;
            case EntityStatus:
                return null;
            case EntityType:
                return null;
            case EntityUuid:
                return null;
            case RecordUUID:
                return null;
            case LocalAddress:
                return addressField;
            case Memo:
                return memoField;
            case StateCounty:
                return countyField;
            case StateName:
                return stateComboBox;
            case ZipCode:
                return zipCodeField;
            default:
                return null;
        }
    }

    @Override
    protected void resetDataEntryHelper() {
        cityField.setText(null);
        countyField.setText(null);
        addressField.setText(null);
        memoField.setText(null);
        countyField.setText(null);
        stateComboBox.setValue(null);
        zipCodeField.setText(null);
    }

    @Override
    protected void resetDataEntryStyleHelper() {
        cityField.setStyle(null);
        countyField.setStyle(null);
        addressField.setStyle(null);
        memoField.setStyle(null);
        countyField.setStyle(null);
        stateComboBox.setStyle(null);
        zipCodeField.setStyle(null);
    }
    
}
