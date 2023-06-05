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

package com.zcomapproach.garden.peony.security;

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.persistence.peony.data.PeonyLogName;
import com.zcomapproach.garden.peony.security.dialogs.PeonyLoginDialog;
import com.zcomapproach.garden.data.GardenFlower;
import com.zcomapproach.garden.data.GardenFlowerOwner;
import com.zcomapproach.garden.guard.GardenMaster;
import com.zcomapproach.garden.guard.RoseWebCipher;
import com.zcomapproach.garden.peony.PeonyTerminator;
import com.zcomapproach.garden.peony.kernel.services.PeonyChatService;
import org.openide.util.lookup.ServiceProvider;
import com.zcomapproach.garden.peony.kernel.services.PeonySecurityService;
import com.zcomapproach.garden.peony.kernel.PeonyServiceProvider;
import com.zcomapproach.garden.peony.kernel.events.PeonyLoginFailedEvent;
import com.zcomapproach.garden.peony.kernel.events.PeonyLoginSuccessfulEvent;
import com.zcomapproach.garden.peony.kernel.events.PeonyLogoutEvent;
import com.zcomapproach.garden.peony.kernel.events.PeonyServiceEvent;
import com.zcomapproach.garden.peony.kernel.events.ProgressStatusEvent;
import com.zcomapproach.garden.util.GardenData;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import com.zcomapproach.garden.peony.kernel.rest.PeonyRestClient;
import com.zcomapproach.garden.peony.kernel.services.PeonyWelcomeService;
import com.zcomapproach.garden.peony.security.controllers.PeonySettingsPaneController;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.persistence.entity.G02Log;
import com.zcomapproach.garden.persistence.peony.data.PeonySystemSettings;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.garden.rest.GardenWebResoureRoot;
import com.zcomapproach.commons.nio.ZcaNio;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.kernel.services.PeonyCustomerService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;

/**
 * A singleton service provider for Peony security
 * @author zhijun98
 */
@ServiceProvider(service = PeonySecurityService.class)
public class PeonySecurityServiceProvider extends PeonyServiceProvider implements PeonySecurityService{

    private final PeonyRestClient peonySecurityClient = new PeonyRestClient(GardenWebResoureRoot.WELCOME);
    
    private PeonyChatService peonyChatService;
    
    private PeonyLoginDialog peonyLoginDialog;
    
    @Override
    public void displayLoginDialog() {
        if (SwingUtilities.isEventDispatchThread()){
            displayLoginDialogHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    displayLoginDialogHelper();
                }
            });
        }
    }

    private void displayLoginDialogHelper() {
        if (peonyLoginDialog == null){
            peonyLoginDialog = new PeonyLoginDialog(PeonyLauncher.mainFrame, true, this);
            peonyLoginDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            peonyLoginDialog.setTitle("Login");
            peonyLoginDialog.setLocationRelativeTo(PeonyLauncher.mainFrame);
            peonyLoginDialog.pack();
            
            PeonyTerminator.SKIP_CONFIRMATION = false;
            
            addPeonyServiceEventListener(peonyLoginDialog);
        }
        peonyLoginDialog.setVisible(true);
    }

    @Override
    public PeonyRestClient getPeonySecurityRestClient() {
        return peonySecurityClient;
    }
    
    @Override
    protected void broadcastPeonyServiceEventHappened(PeonyServiceEvent event){
        if (event instanceof PeonyLoginSuccessfulEvent){
            PeonyProperties.getSingleton().log(PeonyLogName.EMPLOYEE_LOGIN);
        }else if (event instanceof PeonyLogoutEvent){
            PeonyProperties.getSingleton().log(PeonyLogName.EMPLOYEE_LOGOUT);
        }
        super.broadcastPeonyServiceEventHappened(event);
    }
    
    /**
     * Suggest to use PeonyLocalSettings::log() methods which uses its own threading 
     * mechanisms. This method is time-consuming without threading mechanism
     * 
     * @param log 
     */
    @Override
    public void log(G02Log log){
        if (ZcaValidator.isNullEmpty(log.getLogUuid())){
            log.setLogUuid(GardenData.generateUUIDString());
        }
        try {
            getPeonySecurityRestClient().storeEntity_XML(G02Log.class,
                                                        GardenRestParams.Security.storeLogEntityRestParams(),
                                                        log);
            Path logFilesRootPath = Paths.get(PeonyProperties.getSingleton().getLogFilesFolder())
                    .resolve(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
            if (!Files.exists(logFilesRootPath)){
                ZcaNio.createFolder(logFilesRootPath);
            }
            PeonyLogger.serialize(log, logFilesRootPath);
        } catch (Exception ex) {
            //Exceptions.printStackTrace(ex);
        }
        PeonyFaceUtils.publishMessageOntoOutputWindow(log.getLogMessage());
    }
    
    @Override
    public boolean login(String loginName, String password) throws Exception{
        /**
         * (0) validation of parameters and then check if it is for UN-LOCK peony office. 
         * When PeonyLocalSettings is valid for UNLOCK, it simply verifies the credentials 
         * against the existing credentials in PeonyLocalSettings. 
         */
        if (ZcaValidator.isNullEmpty(loginName) || ZcaValidator.isNullEmpty(password)){
            broadcastPeonyServiceEventHappened(new PeonyLoginFailedEvent(NbBundle.getMessage(PeonySecurityServiceProvider.class, "peony_login_credentials_errmsg")));
            return false;
        }
        if (PeonyProperties.getSingleton().isValidForUnlock()){
            if (RoseWebCipher.getSingleton().encrypt(password).equalsIgnoreCase(PeonyProperties.getSingleton().getCurrentLoginEmployee().getAccount().getEncryptedPassword())){
                broadcastPeonyServiceEventHappened(new PeonyLoginSuccessfulEvent(PeonyProperties.getSingleton().getCurrentLoginEmployee()));
                return true;
            }else{
                broadcastPeonyServiceEventHappened(new PeonyLoginFailedEvent(NbBundle.getMessage(PeonySecurityServiceProvider.class, "peony_login_credentials_errmsg")));
                return false;
            }
        }
        GardenRestParams.sparkGardenRestParams(loginName, password, GardenFlowerOwner.getLicenseKey(GardenFlower.PEONY, GardenFlowerOwner.YINLU_CPA_PC));
        /**
         * configure settings for Peony by GardenMaster in case of the very-first launch
         */
        if (GardenMaster.LOGIN_NAME.value().equalsIgnoreCase(loginName) && GardenMaster.PASSWORD.value().equalsIgnoreCase(password)){
            if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "If this is the very-first launch of Peony, do you want to set up settings here?") == JOptionPane.YES_OPTION){
                String rootPath = PeonyFaceUtils.displayInputDialog(PeonyLauncher.mainFrame, "Please set up the absolute path for Peony settings root.", "Configure Peony Settings");
                if (Files.isDirectory(Paths.get(rootPath))){
                    PeonySettingsPaneController.loadPeonySettingsByRootPath(rootPath);
                    try {
                        PeonySystemSettings peonySystemSettings = new PeonySystemSettings();
                        peonySystemSettings.setSystemSettingsList(PeonyProperties.getSingleton().getG02SystemSettingsList());
                        
                        Lookup.getDefault().lookup(PeonySecurityService.class).getPeonySecurityRestClient()
                                .storeEntity_XML(PeonySystemSettings.class, 
                                                 GardenRestParams.Security.storePeonySystemSettingsRestParams(), peonySystemSettings);
                    } catch (Exception ex) {
                        PeonyFaceUtils.displayErrorMessageDialog("Cannot save the settings you configured. " + ex.getMessage());
                        return false;
                    }
                }else{
                    return !(PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "The path is not valid! Do you want to click LOGIN button again for setup?") == JOptionPane.YES_OPTION);
                }
            }
        }
        /**
         * (1) login and retrieve system settings...
         */
        broadcastPeonyServiceEventHappened(new ProgressStatusEvent(10, "Authenticating user's credentials......"));
        PeonySystemSettings peonySystemSettings = getPeonySecurityRestClient()
                .findEntity_XML(PeonySystemSettings.class, GardenRestParams.Security.retrievePeonySystemSettingsRestParams(GardenFlower.PEONY.name(), 
                                                                   GardenFlowerOwner.YINLU_CPA_PC.name(), 
                                                                   peonyLoginDialog.getLoginName(), 
                                                                   peonyLoginDialog.getPassword(),
                                                                   GardenFlowerOwner.getLicenseKey(GardenFlower.PEONY, GardenFlowerOwner.YINLU_CPA_PC)));
        if (peonySystemSettings == null){
            broadcastPeonyServiceEventHappened(new PeonyLoginFailedEvent(NbBundle.getMessage(PeonySecurityServiceProvider.class, "peony_login_credentials_errmsg")));
            return false;
        }
        /**
         * (2) initialize system settings (i.e. PeonyLocalSettings) and properties (i.e. PeonyProperties)...
         */
        PeonyProperties.getSingleton().initializePeonySystemSettings(peonySystemSettings);
        //use the properties file whose location is defined by the server-side
        PeonyProperties.getSingleton().refreshPropertiesLocation(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
        /**
         * (3) validate login...
         */
        if (PeonyProperties.getSingleton().isValidForUnlock()){
            broadcastPeonyServiceEventHappened(new ProgressStatusEvent(25, "Current user's credentials has been authenticated."));
            
//////            /**
//////             * Launch admin-center if the login user has the authorization
//////             */
//////            Lookup.getDefault().lookup(PeonyAdminService.class).launchPeonyAdminCenterTopComponent();
            
        }else{
            broadcastPeonyServiceEventHappened(new PeonyLoginFailedEvent(NbBundle.getMessage(PeonySecurityServiceProvider.class, "peony_login_credentials_errmsg")));
            return false;
        }
        Thread.sleep(100);
        
////        /**
////         * (4) open email window so as to connect to email service
////         */
////        getExecutorCachedService().submit(new Runnable(){
////            @Override
////            public void run() {
////                try {
////                    Lookup.getDefault().lookup(PeonyEmailService.class).openMailBox();
////                } catch (Exception ex) {
////                    //Exceptions.printStackTrace(ex);
////                }
////            }
////        });
        
        /**
         * (4) open email window so as to connect to email service
         */
        getExecutorCachedService().submit(new Runnable(){
            @Override
            public void run() {
                try {
                    Lookup.getDefault().lookup(PeonyCustomerService.class).launchPeonyCustomerServiceTopComponent();
                } catch (Exception ex) {
                    //Exceptions.printStackTrace(ex);
                }
            }
        });
        
        /**
         * (5) connect to chat server
         */
        getExecutorCachedService().submit(new Runnable(){
            @Override
            public void run() {
                try {
                    Lookup.getDefault().lookup(PeonyWelcomeService.class).launchPeonyTalkerWindow();
                } catch (Exception ex) {
                    //Exceptions.printStackTrace(ex);
                }
            }
        });
        
        Thread.sleep(100);
        
        broadcastPeonyServiceEventHappened(new PeonyLoginSuccessfulEvent(PeonyProperties.getSingleton().getCurrentLoginEmployee()));
        return true;
    }

    @Override
    public void logout() throws Exception {
        broadcastPeonyServiceEventHappened(new PeonyLogoutEvent(PeonyProperties.getSingleton().getCurrentLoginEmployee()));
    }

    @Override
    public void terminate() throws Exception {
        if (SwingUtilities.isEventDispatchThread()){
            terminateHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    terminateHelper();
                }
            });
        }
    }
    
    private void terminateHelper(){
        if (peonyLoginDialog != null){
            peonyLoginDialog.setVisible(false);
        }
        try {
            logout();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void closeService() {
        try {
            terminate();
        } catch (Exception ex) {
            //Exceptions.printStackTrace(ex);
            PeonyFaceUtils.publishMessageOntoOutputWindow("Cannot close PeonySecurityService due to technical reasons.");
        }
    }
}
