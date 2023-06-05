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

package com.zcomapproach.garden.peony.settings;

import com.zcomapproach.commons.AbstractZcaProperties;
import com.zcomapproach.commons.ZcaText;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.persistence.peony.data.PeonySystemPropName;
import com.zcomapproach.garden.data.GardenFlower;
import com.zcomapproach.garden.data.GardenFlowerOwner;
import com.zcomapproach.garden.guard.TechnicalController;
import com.zcomapproach.garden.peony.kernel.services.PeonySecurityService;
import com.zcomapproach.garden.peony.security.PeonyPrivilege;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.persistence.constant.SystemSettingsPurpose;
import com.zcomapproach.garden.persistence.entity.G02Log;
import com.zcomapproach.garden.persistence.entity.G02SystemSettings;
import com.zcomapproach.garden.persistence.entity.G02SystemSettingsPK;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.persistence.peony.data.PeonyLogName;
import com.zcomapproach.garden.persistence.peony.data.PeonySystemSettings;
import com.zcomapproach.garden.util.GardenData;
import com.zcomapproach.commons.nio.ZcaNio;
import com.zcomapproach.garden.util.GardenThreadingUtils;
import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.commons.data.values.ZcaBooleanValue;
import com.zcomapproach.commons.xmpp.ZcaXmppAccountAttribute;
import com.zcomapproach.commons.xmpp.data.ZcaXmppAccount;
import com.zcomapproach.commons.xmpp.data.ZcaXmppSettings;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 * This singleton is the centralized place which contains the critical information 
 * shared by the modules in Peony, e.g. current login account's privileges
 * 
 * @todo zzj: consider to merge this class into PeonyProperties
 * 
 * @author zhijun98
 */
public final class PeonyProperties extends AbstractZcaProperties{

    private static volatile PeonyProperties self = null;

    public static PeonyProperties getSingleton() {
        PeonyProperties selfLocal = PeonyProperties.self;
        if (selfLocal == null){
            synchronized (PeonyProperties.class) {
                selfLocal = PeonyProperties.self;
                if (selfLocal == null){
                    PeonyProperties.self = selfLocal = new PeonyProperties();
                }
            }
        }
        return selfLocal;
    }

    /**
     * key = propKey; value = propValue/propDescription
     */
    private final HashMap<String, String[]> props = new HashMap<>();

    private final ExecutorService loggingService = Executors.newFixedThreadPool(5);
    
    private PeonySystemSettings peonySystemSettings;
    
    private ZcaXmppAccount currentXmppAccount;
    
    private ZcaXmppSettings currentXmppSettings;
    
    //private final HashMap<String, PeonyJobAssignment> currentPeonyJobAssignmentStorage = new HashMap<>();

    public PeonyProperties() {
    }

    /**
     * Properties file name without extension, e.g. ".properties" is included
     * @return 
     */
    @Override
    protected String getDefaultPropFullFilePathName() {
        return getDefaultPropFullFilePath() + ZcaNio.fileSeparator() + GardenFlower.PEONY.name().toLowerCase();
    }
    
    private String getDefaultPropFullFilePath(){
        return ZcaUtils.getSystemUserHomeFolder() + ZcaNio.fileSeparator() + GardenFlower.PEONY.name();
    }
    
    public Path getPeonyPropertiesPath(String loginEmployeeUuid){
        try {
            return Paths.get(getPeonyPropertiesLocation(loginEmployeeUuid));
        } catch (Exception ex) {
            //Exceptions.printStackTrace(ex);
            return Paths.get(getDefaultPropFullFilePath() + ZcaNio.fileSeparator() + ZcaText.denullize(loginEmployeeUuid));
        }
    }
    
    /**
     * Generate the Peony properties file's Location based on the current PeonyProperties
     * 
     * @param loginEmployeeUuid
     * @return
     * @throws Exception 
     */
    private String getPeonyPropertiesLocation(String loginEmployeeUuid) throws Exception{
        if (ZcaValidator.isNullEmpty(loginEmployeeUuid) || ZcaValidator.isNullEmpty(PeonyProperties.getSingleton().getPeonyPropertiesFileFolder())){
            throw new Exception("PeonySettings has not been configured or Peony not login yet.");
        }
        return PeonyProperties.getSingleton().getPeonyPropertiesFileFolder()
                + ZcaNio.fileSeparator() + loginEmployeeUuid;
    }

    @Override
    protected void transferSharedPropFromOriginalProp(String originalPropFullFilePathName) {
        try {
            Properties originalProp = super.loadProperties(originalPropFullFilePathName);
            if (originalProp != null){
                setProperty(PeonyPropertiesKey.DEVELOPMENT_MODE.value(), originalProp.getProperty(PeonyPropertiesKey.DEVELOPMENT_MODE.value()));
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * @param loginEmployeeUuid 
     */
    public void refreshPropertiesLocation(String loginEmployeeUuid) {
        try{
            refreshProperties(getPeonyPropertiesLocation(loginEmployeeUuid)
                    + ZcaNio.fileSeparator() + GardenFlower.PEONY.name().toLowerCase()); //file-name without ".properties" extension
        }catch(Exception ex){
            Logger.getLogger(PeonyProperties.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public boolean isDevelopmentMode() {
        return ZcaBooleanValue.YES.value().equalsIgnoreCase(getProperty(PeonyPropertiesKey.DEVELOPMENT_MODE.value()));
    }

    public void setDevelopmentMode(ZcaBooleanValue yesOrNo) {
        if (yesOrNo == null){
            return;
        }
        setProperty(PeonyPropertiesKey.DEVELOPMENT_MODE.value(), yesOrNo.value());
    }

    public String getRoseUploadPostUrl(){
        if (isDevelopmentMode()){
            return "http://localhost:8080/RoseWeb/roseFileUploadServlet";
        }else{
            return "https://www.zcomapproach.com/RoseWeb/roseFileUploadServlet";
        }
    }

    private String getRoseProductionDownloadGetUrl(){
        return "https://www.zcomapproach.com/RoseWeb/roseFileDownloadServlet?";
    }
    
    /**
     * It depends on the local system to return the URL
     * @return 
     */
    public String getRoseDownloadGetUrl(){
        if (isDevelopmentMode()){
            return "http://localhost:8080/RoseWeb/roseFileDownloadServlet?";
        }else{
            return getRoseProductionDownloadGetUrl();
        }
    }
    
//////    /**
//////     * If a member in currentPeonyJobAssignmentList has been loaded before, it will 
//////     * replace the one in the internal storage.
//////     * 
//////     * @param currentPeonyJobAssignmentList 
//////     */
//////    public synchronized void loadCurrentPeonyJobAssignmentList(PeonyJobAssignmentList currentPeonyJobAssignmentList) {
//////        if (currentPeonyJobAssignmentList == null){
//////            return;
//////        }
//////        List<PeonyJobAssignment> aPeonyJobAssignmentList = currentPeonyJobAssignmentList.getPeonyJobAssignmentList();
//////        if (aPeonyJobAssignmentList != null){
//////            for (PeonyJobAssignment aPeonyJobAssignment : aPeonyJobAssignmentList){
//////                addCurrentPeonyJobAssignment(aPeonyJobAssignment);
//////            }
//////        }
//////    }

//////    public synchronized List<PeonyJobAssignment> getCurrentPeonyJobAssignments() {
//////        return new ArrayList<>(currentPeonyJobAssignmentStorage.values());
//////    }
//////    
//////    /**
//////     * 
//////     * @param aPeonyJobAssignment
//////     * @return - if successful, aPeonyJobAssignment itself will be returned
//////     */
//////    public synchronized PeonyJobAssignment addCurrentPeonyJobAssignment(PeonyJobAssignment aPeonyJobAssignment) {
//////        if (aPeonyJobAssignment == null){
//////            return null;
//////        }
//////        return currentPeonyJobAssignmentStorage.put(aPeonyJobAssignment.getJobAssignment().getJobAssignmentUuid(), aPeonyJobAssignment);
//////    }
//////    
//////    public synchronized PeonyJobAssignment removeCurrentPeonyJobAssignment(PeonyJobAssignment aPeonyJobAssignment) {
//////        if (aPeonyJobAssignment == null){
//////            return null;
//////        }
//////        return currentPeonyJobAssignmentStorage.remove(aPeonyJobAssignment.getJobAssignment().getJobAssignmentUuid());
//////    }

    public synchronized void terminate() {
        GardenThreadingUtils.shutdownExecutorService(loggingService, 5, TimeUnit.SECONDS);
    }
    
    public synchronized boolean isValidForUnlock() {
        return (peonySystemSettings != null) && peonySystemSettings.hasBeenLaunchedByLogin();
    }
    
    public synchronized PeonyEmployee getCurrentLoginEmployee() {
        if (peonySystemSettings == null){
            return null;
        }
        return peonySystemSettings.getLoginEmployee();
    }

    public void log(final G02Log aG02Log) {
        if (aG02Log == null){
            return;
        }
        loggingService.submit(new Runnable(){
            @Override
            public void run() {
                Lookup.getDefault().lookup(PeonySecurityService.class).log(aG02Log);
            }
        });
    }

    public void log(final PeonyLogName aPeonyLogName) {
        if (aPeonyLogName == null){
            return;
        }
        Lookup.getDefault().lookup(PeonySecurityService.class).log(createNewG02LogInstance(aPeonyLogName));
    }

    public synchronized PeonySystemSettings getPeonySystemSettings() {
        return peonySystemSettings;
    }

    public synchronized void setPeonySystemSettings(PeonySystemSettings peonySystemSettings) {
        this.peonySystemSettings = peonySystemSettings;
    }

    public synchronized String getCurrentLoginUserUuid() {
        if ((peonySystemSettings == null) || (peonySystemSettings.getLoginEmployee() == null)){
            return null;
        }
        return peonySystemSettings.getLoginEmployee().getAccount().getAccountUuid();
    }
    
    /**
     * 
     * @return - GardenMaster.GOOGLE_EMAIL.value() in the current implementation
     */
    public synchronized String getOfflineEmailAddress() {
        return TechnicalController.GOOGLE_EMAIL.value();
    }
    
    public synchronized int getMessageLoadingThreshhold(){
        String max = PeonyProperties.getSingleton().getProperty(PeonyPropertiesKey.EMAIL_MESSAGE_LOADING_THRESHOLD.value());
        if (ZcaValidator.isNullEmpty(max)){
            PeonyProperties.getSingleton().setProperty(PeonyPropertiesKey.EMAIL_MESSAGE_LOADING_THRESHOLD.value(), "1500");
            return 1500;
        }
        try{
            return Integer.parseInt(max);
        }catch (Exception ex){
            PeonyProperties.getSingleton().setProperty(PeonyPropertiesKey.EMAIL_MESSAGE_LOADING_THRESHOLD.value(), "1500");
            return 1500;
        }
    }
    
    public synchronized String getPeonySettingsRoot() {
        return getPropValue(PeonySystemPropName.PEONY_SETTINGS_ROOT);
    }

    public synchronized void setPeonySettingsRoot(String peonySettingsRoot) {
        setPeonySystemProperty(PeonySystemPropName.PEONY_SETTINGS_ROOT.name(), 
                               peonySettingsRoot, 
                               PeonySystemPropName.getParamDescription(PeonySystemPropName.PEONY_SETTINGS_ROOT));
    }

    /**
     * This is the location where all the archived files are stored.
     * @return 
     */
    public synchronized String getArchivedDocumentsFolder() {
        String result = getPropValue(PeonySystemPropName.ARCHIVED_DOCUMENTATION);
        makeSureLocationReady(result);
        return result;
    }
    
    public synchronized String getArchivedDocumentsBackupFolder() {
        Path path = Paths.get(getArchivedDocumentsFolder());
        path = path.resolve("backup");
        makeSureLocationReady(path.toString());
        return path.toString();
    }
    
    public synchronized String getArchivedDocumentsDeprecatedFolder() {
        Path path = Paths.get(getArchivedDocumentsFolder());
        path = path.resolve("deprecated");
        makeSureLocationReady(path.toString());
        return path.toString();
    }

    public synchronized void setArchivedDocumentsFolder(String archivedDocumentsFolder) {
        setPeonySystemProperty(PeonySystemPropName.ARCHIVED_DOCUMENTATION.name(), 
                               archivedDocumentsFolder, 
                               PeonySystemPropName.getParamDescription(PeonySystemPropName.ARCHIVED_DOCUMENTATION));
    }

    public synchronized String getLogFilesFolder() {
        String result = getPropValue(PeonySystemPropName.LOG_FILES);
        makeSureLocationReady(result);
        return result;
    }

    public synchronized void setLogFilesFolder(String logFilesFolder) {
        setPeonySystemProperty(PeonySystemPropName.LOG_FILES.name(), 
                               logFilesFolder, 
                               PeonySystemPropName.getParamDescription(PeonySystemPropName.LOG_FILES));
    }

    public synchronized String getPeonyPropertiesFileFolder() {
        String result = getPropValue(PeonySystemPropName.PEONY_PROPERTIES_LOCATION);
        makeSureLocationReady(result);
        return result;
    }

    public synchronized void setPeonyPropertiesFileFolder(String peonyPropertiesFileLocation) {
        setPeonySystemProperty(PeonySystemPropName.PEONY_PROPERTIES_LOCATION.name(), 
                               peonyPropertiesFileLocation, 
                               PeonySystemPropName.getParamDescription(PeonySystemPropName.PEONY_PROPERTIES_LOCATION));
    }

    public synchronized String getEmailSerializationFolder() {
        String result = getPropValue(PeonySystemPropName.EMAIL_SERIALIZATION_LOCATION);
        makeSureLocationReady(result);
        return result;
    }

    public synchronized void setEmailSerializationFolder(String emailSerializationLocation) {
        setPeonySystemProperty(PeonySystemPropName.EMAIL_SERIALIZATION_LOCATION.name(), 
                               emailSerializationLocation, 
                               PeonySystemPropName.getParamDescription(PeonySystemPropName.EMAIL_SERIALIZATION_LOCATION));
    }

    public synchronized String getSmsLoggineLocation() {
        String result = getPropValue(PeonySystemPropName.SMS_LOGGING_LOCATION);
        makeSureLocationReady(result);
        return result;
    }

    public synchronized void setSmsLoggineLocation(String smsLoggineLocation) {
        setPeonySystemProperty(PeonySystemPropName.SMS_LOGGING_LOCATION.name(), 
                               smsLoggineLocation, 
                               PeonySystemPropName.getParamDescription(PeonySystemPropName.SMS_LOGGING_LOCATION));
    }

    /**
     * the
     * @return 
     */
    public synchronized String getPeonyUserTempLocation() {
        String result = getPropValue(PeonySystemPropName.PEONY_USER_TEMP);
        makeSureLocationReady(result);
        return result;
    }

    public synchronized void setPeonyUserTempLocation(String peonyUserTempLocation) {
        setPeonySystemProperty(PeonySystemPropName.PEONY_USER_TEMP.name(), 
                               peonyUserTempLocation, 
                               PeonySystemPropName.getParamDescription(PeonySystemPropName.PEONY_USER_TEMP));
    }

    public synchronized void setPeonySystemProperty(String propName, String propValue) {
        PeonySystemPropName aPeonySystemPropName = PeonySystemPropName.convertEnumNameToType(propName);
        switch (aPeonySystemPropName){
            case ARCHIVED_DOCUMENTATION:
                setArchivedDocumentsFolder(propValue);
                break;
            case LOG_FILES:
                setLogFilesFolder(propValue);
                break;
            case PEONY_PROPERTIES_LOCATION:
                setPeonyPropertiesFileFolder(propValue);
                break;
            case EMAIL_SERIALIZATION_LOCATION:
                setEmailSerializationFolder(propValue);
                break;
            case SMS_LOGGING_LOCATION:
                setSmsLoggineLocation(propValue);
                break;
            case PEONY_SETTINGS_ROOT:
                setPeonySettingsRoot(propValue);
                break;
            case PEONY_USER_TEMP://
                setPeonyUserTempLocation(propValue);
                break;
        }
    }
    
    private static void makeSureLocationReady(String location){
        File folderLocation = new File(location);
        if (!folderLocation.exists()){
            try {
                if (!PeonyProperties.getSingleton().isDevelopmentMode()){
                    ZcaNio.createFolder(folderLocation.getAbsolutePath());
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private synchronized void setPeonySystemProperty(String propName, String propValue, String description) {
        String[] propArray = {propValue, description};
        props.put(propName, propArray);
    }
    
    public synchronized List<G02SystemSettings> getG02SystemSettingsList(){
        List<G02SystemSettings> result = new ArrayList<>();
        
        Set<String> keySet = props.keySet();
        Iterator<String> itr = keySet.iterator();
        G02SystemSettings aG02SystemSettings;
        G02SystemSettingsPK aG02SystemSettingsPK;
        Date today = new Date();
        String[] propArray;
        String propName;
        while (itr.hasNext()){
            propName = itr.next();
            propArray = props.get(propName);
            aG02SystemSettings = new G02SystemSettings();
            aG02SystemSettingsPK = new G02SystemSettingsPK();
            aG02SystemSettingsPK.setPropertyName(propName);
            aG02SystemSettingsPK.setPropertyValue(propArray[0]);
            aG02SystemSettingsPK.setFlowerUserUuid(getCurrentLoginUserUuid());
            aG02SystemSettings.setDescription(propArray[1]);
            aG02SystemSettings.setPurpose(SystemSettingsPurpose.LOCAL_OFFICE.name());
            aG02SystemSettings.setG02SystemSettingsPK(aG02SystemSettingsPK);
            aG02SystemSettings.setFlowerName(GardenFlower.PEONY.name());
            aG02SystemSettings.setFlowerOwner(GardenFlowerOwner.YINLU_CPA_PC.name());
            
            aG02SystemSettings.setUpdated(today);
            aG02SystemSettings.setCreated(today);
            
            result.add(aG02SystemSettings);
        }
        return result;
    }
    
    public synchronized boolean isEmpty(){
        return props.isEmpty();
    }
    
    public synchronized void addSystemProperty(G02SystemSettings aG02SystemSettings){
        if (aG02SystemSettings == null){
            return;
        }
        String[] propArray = {aG02SystemSettings.getG02SystemSettingsPK().getPropertyValue(), aG02SystemSettings.getDescription()};
        props.put(aG02SystemSettings.getG02SystemSettingsPK().getPropertyName(), propArray);
    }
    
    public synchronized String getPropDescripiton(String propName){
        String[] propArray = props.get(propName);
        if (propArray == null){
            //default...
            return PeonySystemPropName.getParamDescription(PeonySystemPropName.convertEnumNameToType(propName));
        }else{
            return propArray[1];
        }
    }
    
    public synchronized String getPropValue(PeonySystemPropName prop){
        if (prop == null){
            return null;
        }
        return getPropValue(prop.name());
    }
    
    private synchronized String getPropValue(String propName){
        String[] propArray = props.get(propName);
        if (propArray == null){
            PeonySystemPropName aPeonySystemPropName = PeonySystemPropName.convertEnumNameToType(propName);
            //default...
            String aFolderPath = ZcaUtils.getSystemUserHomeFolder() + ZcaNio.fileSeparator() 
                    + PeonySystemPropName.PEONY_SETTINGS_ROOT.value();
            if (!PeonySystemPropName.PEONY_SETTINGS_ROOT.equals(aPeonySystemPropName)){
                aFolderPath += ZcaNio.fileSeparator() + aPeonySystemPropName.value();
            }
            setPeonySystemProperty(propName, aFolderPath, PeonySystemPropName.getParamDescription(aPeonySystemPropName));
            return aFolderPath;
        }else{
            return propArray[0];
        }
    }

    /**
     * The maximum number of editor windows can be opened by users 
     * @return 
     */
    public synchronized int getOpenedEditorWindowsThreshold() {
        return 28;
    }

    public synchronized void initializePeonySystemSettings(PeonySystemSettings peonySystemSettings) {
        setPeonySystemSettings(peonySystemSettings);
        List<G02SystemSettings> aG02SystemSettingsList = peonySystemSettings.getSystemSettingsList();
        for (G02SystemSettings aG02SystemSettings : aG02SystemSettingsList){
            setPeonySystemProperty(aG02SystemSettings.getG02SystemSettingsPK().getPropertyName(), aG02SystemSettings.getG02SystemSettingsPK().getPropertyValue());
        }
    }
    
    public synchronized boolean isPrivilegeAuthorized(PeonyPrivilege aPeonyPrivilege){
        if ((aPeonyPrivilege == null) || (getCurrentLoginEmployee() == null)){
            return false;
        }
        return getCurrentLoginEmployee().isPrivilegeAuthorized(GardenFlower.PEONY.name(), aPeonyPrivilege.name());
    }

    public boolean isGardenMaster() {
        if (getCurrentLoginEmployee() == null){
            return false;
        }
        return PeonyEmployee.isGardenMaster(getCurrentLoginUserUuid());
    }

    public boolean isTechnicalController() {
        if (getCurrentLoginEmployee() == null){
            return false;
        }
        return PeonyEmployee.isTechnicalController(getCurrentLoginUserUuid());
    }

    /**
     * If such a path was not set up or not physically existing, it will be created.
     * This is one of Peony local settings. This method assume Peony has been logged in
     * @return - if the task described-above failed, NULL returns.
     */
    public Path getLocalUserPath() {
        String userFolder = PeonyProperties.getSingleton().getProperty(PeonyPropertiesKey.LOCAL_USER_FOLDER.value());
        if (ZcaValidator.isNullEmpty(userFolder)){
            return getDefaultLocalUserPath();
        }else{
            return makeSureFolderReady(userFolder);
        }
    }

    public Path getLocalUserTempPath() {
        return makeSureFolderReady(getLocalUserPath().resolve(PeonyLocalFolderName.TEMP.value()).toString());
    }

    /**
     * This method assume Peony has been logged in
     * @param userFolder - it should physically exist
     */
    public void setLocalUserPath(String userFolder) {
        if (ZcaValidator.isNullEmpty(userFolder)){
            return;
        }
        if (Files.isDirectory(Paths.get(userFolder))){
            PeonyProperties.getSingleton().setProperty(PeonyPropertiesKey.LOCAL_USER_FOLDER.value(), userFolder);
        }
    }

    /**
     * update the properties and use default user folder 
     * @return 
     */
    public Path getDefaultLocalUserPath() {
        PeonyProperties.getSingleton().setProperty(PeonyPropertiesKey.LOCAL_USER_FOLDER.value(), 
                PeonyProperties.getSingleton().getPeonyPropertiesPath(getCurrentLoginUserUuid()).toString());
//                        + ZcaNio.fileSeparator() +PeonyLocalSettingsFolderName.TEMP.name());
        return makeSureFolderReady(PeonyProperties.getSingleton().getProperty(PeonyPropertiesKey.LOCAL_USER_FOLDER.value()));
    }

    /**
     * Create the folder if its physical folder does not exist
     * @param folder
     * @return 
     */
    public static Path makeSureFolderReady(String folder) {
        
        if (ZcaValidator.isNullEmpty(folder) || folder.contains(".")){
            Logger.getLogger(PeonyProperties.class.getName()).log(Level.WARNING, "The folder parameter seems not to be a folder.");
            return null;
        }
        
        
        Path userPath = Paths.get(folder);
        if (Files.isRegularFile(userPath)){
            return userPath;
        }
        if (Files.isDirectory(userPath)){
            return userPath;
        }else{
            try {
                ZcaNio.createFolder(userPath);
                return userPath;
            } catch (IOException ex) {
                //Exceptions.printStackTrace(ex);
                PeonyFaceUtils.displayErrorMessageDialog("[Fatal Error] Cannot create settings folder.");
            }
            return null;
        }
    }
    
    public static String convertToAbsolutePathString(Path aPath){
        if (aPath == null){
            return "";
        }else{
            return aPath.toAbsolutePath().toString();
        }
    }

    /**
     * Helper method to quickly create a new log entity instance which includes 
     * basic information, such as UUID and current-employee-UUID
     * 
     * @param aPeonyLogName - cannot be NULL. it's used to automatically display 
     * log-records in the logging panel
     * @return 
     */
    public G02Log createNewG02LogInstance(PeonyLogName aPeonyLogName) {
        if (aPeonyLogName == null){
            return null;
        }
        G02Log log = new G02Log();
        
        log.setLogUuid(GardenData.generateUUIDString());
        log.setLogName(aPeonyLogName.name());
        log.setLogMessage(aPeonyLogName.value());
        log.setOperatorAccountUuid(this.getCurrentLoginUserUuid());
        log.setCreated(new Date());
        
        log.setLoggedEntityType(null);
        log.setLoggedEntityUuid(null);
        log.setEntityUuid(null);
        log.setEntityType(null);
        log.setEntityDescription(null);
        return log;
    }

    public List<String> getExistingServiceTagNames() {
        if (peonySystemSettings == null){
            return new ArrayList<>();
        }
        return peonySystemSettings.getExistingServiceTagNames();
    }

    /**
     * Get a XMPP user account instance for creation
     * @return 
     */
    public ZcaXmppAccount getCurrentXmppAccount() {
        if (currentXmppAccount == null){
            PeonyEmployee employee = getCurrentLoginEmployee();
            currentXmppAccount = new ZcaXmppAccount();
            currentXmppAccount.setLoginName(employee.getLoginName());
            currentXmppAccount.setPassword(employee.getAccount().getPassword());
            Map<String, String> attributes = new HashMap<>();
            attributes.put(ZcaXmppAccountAttribute.NAME.value(), employee.getPeonyUserFullName());
            attributes.put(ZcaXmppAccountAttribute.FIRST.value(), employee.getPeonyUserFirstName());
            attributes.put(ZcaXmppAccountAttribute.LAST.value(), employee.getPeonyUserLastName());
            if (ZcaValidator.isNullEmpty(employee.getWorkEmail())){
                attributes.put(ZcaXmppAccountAttribute.EMAIL.value(), 
                        (employee.getPeonyUserFirstName() + "." + employee.getPeonyUserLastName() + "@yinlucpapc.com").toLowerCase());
            }else{
                attributes.put(ZcaXmppAccountAttribute.EMAIL.value(), employee.getWorkEmail());
            }
            attributes.put(ZcaXmppAccountAttribute.MISC.value(), GardenFlower.PEONY.value());
            currentXmppAccount.setAttributes(attributes);
        }
        return currentXmppAccount;
    }

    /**
     * Get XMPP settings for the current Peony user's login
     * @return 
     */
    public ZcaXmppSettings getCurrentXmppSettings() {
        if (currentXmppSettings == null){
            currentXmppSettings = new ZcaXmppSettings(
                getCurrentLoginEmployee().getXmppAccount().getLoginName(), 
                getCurrentLoginEmployee().getXmppAccount().getPassword());
        }
        return currentXmppSettings;
    }

    public boolean isSuperUser() {
        return ("zhijun98".equalsIgnoreCase(getCurrentLoginEmployee().getLoginName()) || "kitty".equalsIgnoreCase(getCurrentLoginEmployee().getLoginName()));
    }
}
