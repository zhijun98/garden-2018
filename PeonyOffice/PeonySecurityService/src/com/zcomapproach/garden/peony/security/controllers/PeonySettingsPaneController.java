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
package com.zcomapproach.garden.peony.security.controllers;

import com.zcomapproach.garden.data.GardenFlower;
import com.zcomapproach.garden.data.GardenFlowerOwner;
import com.zcomapproach.garden.peony.kernel.services.PeonySecurityService;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.persistence.peony.data.PeonySystemPropName;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.persistence.constant.SystemSettingsPurpose;
import com.zcomapproach.garden.persistence.entity.G02SystemSettings;
import com.zcomapproach.garden.persistence.entity.G02SystemSettingsPK;
import com.zcomapproach.garden.persistence.peony.data.PeonySystemSettings;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.nio.ZcaNio;
import com.zcomapproach.commons.ZcaUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 * 
 * @author zhijun98
 */
public class PeonySettingsPaneController extends PeonySecurityServiceController{
    @FXML
    private TabPane peonySettingsTabPane;
    /**
     * Global
     */
    @FXML
    private Tab globalSystemSettingsTab;
    @FXML
    private TextField archivedDocumentFolderField;
    @FXML
    private Label archivedDocumentFolderTipLabel;
    @FXML
    private Button configureArchivedDocumentFolderButton;
    @FXML
    private TextField logFilesFolderField;
    @FXML
    private Label logFilesFolderTipLabel;
    @FXML
    private Button configurelogFilesFolderButton;
    @FXML
    private TextField peonyPropertiesFolderField;
    @FXML
    private Label peonyPropertiesFolderTipLabel;
    @FXML
    private Button configurePeonyPropertiesFolderButton;
    @FXML
    private TextField emailLoggingFolderField;
    @FXML
    private Label emailLoggingFolderTipLabel;
    @FXML
    private Button configureEmailLoggingFolderButton;
    @FXML
    private TextField smsLoggingFolderField;
    @FXML
    private Label smsLoggingFolderTipLabel;
    @FXML
    private TextField peonyUserTempFolderField;
    @FXML
    private Label peonyUserTempFolderTipLabel;
    @FXML
    private Button configurePeonyUserTempFolderButton;
    @FXML
    private Button configureSmsLoggingFolderButton;
    @FXML
    private Button automaticallyConfigureButton;
    @FXML
    private Button saveConfigurationButton;
    /**
     * Local Peony Settings
     */
    @FXML
    private TextField localUserFolderTextField;
    @FXML
    private Button selectUserFolderButton;
    @FXML
    private Button useDefaultUserFolderButton;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (PeonyProperties.getSingleton().isGardenMaster()){
            initializGlobalSystemSettingsTab();
        }else{
            peonySettingsTabPane.getTabs().remove(globalSystemSettingsTab);
        }
        initializLocalPeonySettingsTab();
    }
    
    private void initializLocalPeonySettingsTab(){
        localUserFolderTextField.setText(PeonyProperties.convertToAbsolutePathString(PeonyProperties.getSingleton().getLocalUserPath()));
        
        selectUserFolderButton.setOnAction((ActionEvent event) -> {
            String downloadFolder = PeonyProperties.convertToAbsolutePathString(PeonyProperties.makeSureFolderReady(requestFolderPath()));
            PeonyProperties.getSingleton().setLocalUserPath(downloadFolder);
            localUserFolderTextField.setText(downloadFolder);
        });
        
        useDefaultUserFolderButton.setOnAction((ActionEvent event) -> {
            localUserFolderTextField.setText(PeonyProperties.convertToAbsolutePathString(PeonyProperties.getSingleton().getDefaultLocalUserPath()));
        });
    }

    private void initializeSystemFolderProperty(final TextField folderfield, 
                                                final Label folderDescriptionLabel, 
                                                final Button configureButton, 
                                                final PeonySystemPropName aPeonySystemPropName) {
        PeonyFaceUtils.initializeTextField(folderfield, 
                PeonyProperties.getSingleton().getPropValue(aPeonySystemPropName), 
                ZcaUtils.getSystemUserHomeFolder() + ZcaNio.fileSeparator() + aPeonySystemPropName.value(), 
                PeonyProperties.getSingleton().getPropDescripiton(aPeonySystemPropName.name()), null);
        folderDescriptionLabel.setText(PeonyProperties.getSingleton().getPropDescripiton(aPeonySystemPropName.name()));
        configureButton.setOnAction((ActionEvent event) -> {
            folderfield.setText(requestFolderPath());
            PeonyProperties.getSingleton().addSystemProperty(createG02SystemSettings(aPeonySystemPropName.name(), folderfield.getText()));
        });
    }
    
    private static G02SystemSettings createG02SystemSettings(String propKey, String propValue){
        G02SystemSettings aG02SystemSettings = new G02SystemSettings();
        G02SystemSettingsPK g02SystemSettingsPK = new G02SystemSettingsPK();
        g02SystemSettingsPK.setPropertyName(propKey);
        g02SystemSettingsPK.setPropertyValue(propValue);
        g02SystemSettingsPK.setFlowerUserUuid(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
        aG02SystemSettings.setG02SystemSettingsPK(g02SystemSettingsPK);
        aG02SystemSettings.setDescription(PeonyProperties.getSingleton().getPropDescripiton(propKey));
        aG02SystemSettings.setPurpose(SystemSettingsPurpose.LOCAL_OFFICE.name());
        aG02SystemSettings.setFlowerName(GardenFlower.PEONY.name());
        aG02SystemSettings.setFlowerOwner(GardenFlowerOwner.YINLU_CPA_PC.name());
        return aG02SystemSettings;
    }

    private void configurePeonySystemSettingsAutomatically() {
        if (Platform.isFxApplicationThread()){
            configurePeonySystemSettingsAutomaticallyHelper();
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    configurePeonySystemSettingsAutomaticallyHelper();
                }
            });
        }
    }
    
    private void configurePeonySystemSettingsAutomaticallyHelper(){
        String rootPath = requestFolderPath();
        PeonyProperties.getSingleton().addSystemProperty(createG02SystemSettings(PeonySystemPropName.PEONY_SETTINGS_ROOT.name(), rootPath));
        
        archivedDocumentFolderField.setText(rootPath + ZcaNio.fileSeparator()+PeonySystemPropName.ARCHIVED_DOCUMENTATION.value());
        logFilesFolderField.setText(rootPath + ZcaNio.fileSeparator()+PeonySystemPropName.LOG_FILES.value());
        peonyPropertiesFolderField.setText(rootPath + ZcaNio.fileSeparator()+PeonySystemPropName.PEONY_PROPERTIES_LOCATION.value());
        emailLoggingFolderField.setText(rootPath + ZcaNio.fileSeparator()+PeonySystemPropName.EMAIL_SERIALIZATION_LOCATION.value());
        smsLoggingFolderField.setText(rootPath + ZcaNio.fileSeparator()+PeonySystemPropName.SMS_LOGGING_LOCATION.value());
        peonyUserTempFolderField.setText(rootPath + ZcaNio.fileSeparator()+PeonySystemPropName.PEONY_USER_TEMP.value());
        
        loadPeonySettingsFromGui();
    }
    
    public static void loadPeonySettingsByRootPath(String rootPath) throws IOException{
        if (!Files.isDirectory(Paths.get(rootPath))){
            throw new IOException("The root path is not a valid folder.");
        }
        PeonyProperties aPeonyLocalSettings = PeonyProperties.getSingleton();
        aPeonyLocalSettings.addSystemProperty(createG02SystemSettings(PeonySystemPropName.PEONY_SETTINGS_ROOT.name(), rootPath));
        
        aPeonyLocalSettings.addSystemProperty(createG02SystemSettings(PeonySystemPropName.ARCHIVED_DOCUMENTATION.name(),
                rootPath + ZcaNio.fileSeparator()+PeonySystemPropName.ARCHIVED_DOCUMENTATION.value()));
        
        aPeonyLocalSettings.addSystemProperty(createG02SystemSettings(PeonySystemPropName.LOG_FILES.name(),
                rootPath + ZcaNio.fileSeparator()+PeonySystemPropName.LOG_FILES.value()));
        
        aPeonyLocalSettings.addSystemProperty(createG02SystemSettings(PeonySystemPropName.PEONY_PROPERTIES_LOCATION.name(),
                rootPath + ZcaNio.fileSeparator()+PeonySystemPropName.PEONY_PROPERTIES_LOCATION.value()));
        
        aPeonyLocalSettings.addSystemProperty(createG02SystemSettings(PeonySystemPropName.EMAIL_SERIALIZATION_LOCATION.name(),
                rootPath + ZcaNio.fileSeparator()+PeonySystemPropName.EMAIL_SERIALIZATION_LOCATION.value()));
        
        aPeonyLocalSettings.addSystemProperty(createG02SystemSettings(PeonySystemPropName.SMS_LOGGING_LOCATION.name(),
                rootPath + ZcaNio.fileSeparator()+PeonySystemPropName.SMS_LOGGING_LOCATION.value()));
        
        aPeonyLocalSettings.addSystemProperty(createG02SystemSettings(PeonySystemPropName.PEONY_USER_TEMP.name(),
                rootPath + ZcaNio.fileSeparator()+PeonySystemPropName.PEONY_USER_TEMP.value()));
    }
    
    /**
     * Load GUI's data into PeonyProperties which will be saved onto the server later
     */
    private void loadPeonySettingsFromGui(){
        PeonyProperties.getSingleton().addSystemProperty(createG02SystemSettings(PeonySystemPropName.ARCHIVED_DOCUMENTATION.name(),
                                                                                archivedDocumentFolderField.getText()));
        PeonyProperties.getSingleton().addSystemProperty(createG02SystemSettings(PeonySystemPropName.LOG_FILES.name(),
                                                                                logFilesFolderField.getText()));
        PeonyProperties.getSingleton().addSystemProperty(createG02SystemSettings(PeonySystemPropName.PEONY_PROPERTIES_LOCATION.name(),
                                                                                peonyPropertiesFolderField.getText()));
        PeonyProperties.getSingleton().addSystemProperty(createG02SystemSettings(PeonySystemPropName.EMAIL_SERIALIZATION_LOCATION.name(),
                                                                                emailLoggingFolderField.getText()));
        PeonyProperties.getSingleton().addSystemProperty(createG02SystemSettings(PeonySystemPropName.SMS_LOGGING_LOCATION.name(),
                                                                                smsLoggingFolderField.getText()));
        PeonyProperties.getSingleton().addSystemProperty(createG02SystemSettings(PeonySystemPropName.PEONY_USER_TEMP.name(),
                                                                                peonyUserTempFolderField.getText()));
    }
    
    private String requestFolderPath(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedFolder = directoryChooser.showDialog(null);
        if (selectedFolder == null){
            PeonyFaceUtils.displayErrorMessageDialog("No folder was selected. Please try it again.");
            return "";
        }
        return selectedFolder.getAbsolutePath();
    }

    private void initializGlobalSystemSettingsTab() {
        initializeSystemFolderProperty(archivedDocumentFolderField, 
                                       archivedDocumentFolderTipLabel, 
                                       configureArchivedDocumentFolderButton, 
                                       PeonySystemPropName.ARCHIVED_DOCUMENTATION);
        archivedDocumentFolderField.setEditable(false);
        
        initializeSystemFolderProperty(logFilesFolderField, 
                                       logFilesFolderTipLabel, 
                                       configurelogFilesFolderButton, 
                                       PeonySystemPropName.LOG_FILES);
        logFilesFolderField.setEditable(false);
        
        initializeSystemFolderProperty(peonyPropertiesFolderField, 
                                       peonyPropertiesFolderTipLabel, 
                                       configurePeonyPropertiesFolderButton, 
                                       PeonySystemPropName.PEONY_PROPERTIES_LOCATION);
        
        peonyPropertiesFolderField.setEditable(false);
        
        initializeSystemFolderProperty(emailLoggingFolderField, 
                                       emailLoggingFolderTipLabel, 
                                       configureEmailLoggingFolderButton, 
                                       PeonySystemPropName.EMAIL_SERIALIZATION_LOCATION);
        
        emailLoggingFolderField.setEditable(false);
        
        initializeSystemFolderProperty(smsLoggingFolderField, 
                                       smsLoggingFolderTipLabel, 
                                       configureSmsLoggingFolderButton, 
                                       PeonySystemPropName.SMS_LOGGING_LOCATION);
        smsLoggingFolderField.setEditable(false);
        
        initializeSystemFolderProperty(peonyUserTempFolderField, 
                                       peonyUserTempFolderTipLabel, 
                                       configurePeonyUserTempFolderButton, 
                                       PeonySystemPropName.PEONY_USER_TEMP);
        peonyUserTempFolderField.setEditable(false);
        
        automaticallyConfigureButton.setOnAction((ActionEvent event) -> {
            configurePeonySystemSettingsAutomatically();
        });
        
        saveConfigurationButton.setOnAction((ActionEvent event) -> {
            Task<Boolean> saveAndCloseTask = new Task<Boolean>(){
                @Override
                protected Boolean call() throws Exception {
                    try {
                        PeonySystemSettings peonySystemSettings = new PeonySystemSettings();
                        peonySystemSettings.setSystemSettingsList(PeonyProperties.getSingleton().getG02SystemSettingsList());
                        
                        Lookup.getDefault().lookup(PeonySecurityService.class).getPeonySecurityRestClient()
                                .storeEntity_XML(PeonySystemSettings.class, 
                                                 GardenRestParams.Security.storePeonySystemSettingsRestParams(), peonySystemSettings);
                        return true;
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                        return false;
                    }
                }

                @Override
                protected void succeeded() {
                    try {
                        if (get()){
                            loadPeonySettingsFromGui();
        
                            //use the properties file whose location is defined by the server-side
                            PeonyProperties.getSingleton().refreshPropertiesLocation(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
            
                            broadcastPeonyFaceEventHappened(new CloseDialogRequest(true));
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                        PeonyFaceUtils.displayWarningMessageDialog("This operation was canceled. " + ex.getMessage());
                    }
                }
            };
            this.getCachedThreadPoolExecutorService().submit(saveAndCloseTask);
            
        });
    }
    
}
