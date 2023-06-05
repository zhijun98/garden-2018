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
package com.zcomapproach.garden.rose.bean;

import com.zcomapproach.garden.aws.RoseSmsSender;
import com.zcomapproach.garden.email.rose.RoseEmailMessage;
import com.zcomapproach.garden.email.rose.RoseAttachmentFile;
import com.zcomapproach.garden.email.rose.RoseEmailSender;
import com.zcomapproach.garden.email.HostMonsterEmailSettings;
import com.zcomapproach.garden.email.data.GardenEmailFolderName;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.commons.persistent.exception.NonUniqueEntityException;
import com.zcomapproach.garden.guard.RoseWebCipher;
import com.zcomapproach.garden.persistence.constant.GardenAccountStatus;
import com.zcomapproach.garden.persistence.constant.GardenEntityStatus;
import com.zcomapproach.garden.persistence.entity.G01Account;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.garden.rose.RosePageName;
import com.zcomapproach.garden.rose.RoseWebIdValue;
import com.zcomapproach.garden.rose.data.RoseWebContants;
import com.zcomapproach.garden.rose.data.RoseWebPageConfig;
import com.zcomapproach.garden.rose.data.profile.EmployeeAccountProfile;
import com.zcomapproach.garden.rose.data.profile.RoseAccountProfile;
import com.zcomapproach.garden.rose.data.profile.TaxcorpFilingConciseProfile;
import com.zcomapproach.garden.rose.data.profile.TaxpayerCaseConciseProfile;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.garden.rose.util.RoseXmppUtils;
import com.zcomapproach.garden.rose.util.RoseWebUtils;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import com.zcomapproach.commons.nio.ZcaNio;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.Cookie;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.primefaces.component.datalist.DataList;
import org.primefaces.event.ToggleEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author zhijun98
 */
@Named(value = "roseUserSession")
@SessionScoped
public class RoseUserSessionBean extends RoseAccountProfileBean{

    private String localeCode; //thread-safe
    
    /**
     * Current user's account profile
     */
    private String targetLoginName;
    private String targetLoginPassword;
    private String targetAccountConfirmationCode;
    
    /**
     * Xmpp connection for authenticated targetAccountProfile
     */
    private AbstractXMPPConnection targetXmppConnection;
    
    /**
     * Data structures to help redeem credentials for users
     */
    private RoseAccountProfile targetForgottenAccountProfile;   //users input which try to redeem the account
    private RoseAccountProfile targetRedeemAccountProfileForUserAnswer;   //users input which try to redeem the account
    //canditates for redeem-accounts according to user's input
    private List<RoseAccountProfile> forgottenAccountProfileList;
    
    /**
     * User's web page configuration
     */
    private final RoseWebPageConfig webPageConfig;
    
    /**
     * Display in the side-panel on the homepage
     */
    private int selectedDaysBeforeDeadlineForTaxpayer = 90;
    private List<TaxpayerCaseConciseProfile> urgentTaxpayerCaseProfileTaskList;
    private int selectedDaysBeforeDeadlineForTaxcorp = 45;
    private List<TaxcorpFilingConciseProfile> urgentTaxcorpCaseConciseProfileList;
    
    private String messageForEmailForwardToEmployees;
    private List<EmployeeAccountProfile> selectedEmployeeAccountProfilesForEmailForward;
    
    private RoseAttachmentFile targetRoseAttachmentFile;
    
    /**
     * Creates a new instance of RoseUserSessionBean
     */
    public RoseUserSessionBean() {
        webPageConfig = new RoseWebPageConfig();
        webPageConfig.collapseWebComponent(RoseWebIdValue.TaxcorpCase_CustomerProfilePanel);
    }
    
    public String generatePrintableViewPageLink(RoseEmailMessage aRoseEmailMessage){
        HashMap<String, String> params = new HashMap<>();
        params.put(getRoseParamKeys().getEmailIdParamKey(), Long.toString(aRoseEmailMessage.getMsgUid()));
        if (GardenEmailFolderName.GARDEN_SENT.value().equalsIgnoreCase(aRoseEmailMessage.getMailFolderName())){
            return RoseJsfUtils.getRootWebPath() + RoseWebUtils.BUSINESS_FOLDER + "/" 
                    + RosePageName.PrintableSentEmailBoxPage.name() + RoseWebUtils.JSF_EXT 
                    + RoseWebUtils.constructWebQueryString(params, false);
//            return "http://localhost:8080/RoseWeb/faces/" + RoseWebUtils.BUSINESS_FOLDER + "/" 
//                    + RosePageName.PrintableSentEmailBoxPage.name() + RoseWebUtils.JSF_EXT 
//                    + RoseWebUtils.constructWebQueryString(params, false);
        }else{
            return RoseJsfUtils.getRootWebPath() + RoseWebUtils.BUSINESS_FOLDER + "/" 
                    + RosePageName.PrintableBusinessEmailBoxPage.name() + RoseWebUtils.JSF_EXT 
                    + RoseWebUtils.constructWebQueryString(params, false);
//            return "http://localhost:8080/RoseWeb/faces/" + RoseWebUtils.BUSINESS_FOLDER + "/" 
//                    + RosePageName.PrintableBusinessEmailBoxPage.name() + RoseWebUtils.JSF_EXT 
//                    + RoseWebUtils.constructWebQueryString(params, false);
        
        }
    }
    
    public synchronized void prepareTargetAttachmentFile(String targetAttachmentFileName){
        this.targetRoseAttachmentFile = this.getTargetBusinessEmailMessage().getAttachmentFile(targetAttachmentFileName);
    }
    
    public synchronized StreamedContent getDownloadedAttachmentFile() {
        StreamedContent targetDownloadedAttachmentFile = null;
        if (targetRoseAttachmentFile == null){
            this.setBootstrapAlertMessage("No attachment file was found.", Level.SEVERE);
        }else{
            try{
                File initialFile = new File(targetRoseAttachmentFile.getFileFullPathName());
                if (ZcaNio.isValidFile(initialFile)){
                    InputStream stream = new FileInputStream(initialFile);
                    targetDownloadedAttachmentFile = new DefaultStreamedContent(stream, 
                                                                                new MimetypesFileTypeMap().getContentType(initialFile), 
                                                                                targetRoseAttachmentFile.getFileOriginalName());
                }else{
                    targetDownloadedAttachmentFile = null;
                    this.setBootstrapAlertMessage("The attachment file is not valid", Level.SEVERE);
                }
            }catch(FileNotFoundException ex){
                targetDownloadedAttachmentFile = null;
                this.setBootstrapAlertMessage(ex.getMessage(), Level.SEVERE);
            }
        }
        return targetDownloadedAttachmentFile;
    }

    public synchronized String getMessageForEmailForwardToEmployees() {
        return messageForEmailForwardToEmployees;
    }

    public synchronized void setMessageForEmailForwardToEmployees(String messageForEmailForwardToEmployees) {
        this.messageForEmailForwardToEmployees = messageForEmailForwardToEmployees;
    }

    public synchronized List<EmployeeAccountProfile> getSelectedEmployeeAccountProfilesForEmailForward() {
        return selectedEmployeeAccountProfilesForEmailForward;
    }

    public synchronized void setSelectedEmployeeAccountProfilesForEmailForward(List<EmployeeAccountProfile> selectedEmployeeAccountProfilesForEmailForward) {
        this.selectedEmployeeAccountProfilesForEmailForward = selectedEmployeeAccountProfilesForEmailForward;
    }
    
    public synchronized void forwardTargetEmailToSelectedEmployees(){
        if ((selectedEmployeeAccountProfilesForEmailForward == null) || (selectedEmployeeAccountProfilesForEmailForward.isEmpty())){
            this.setBootstrapAlertMessage(RoseText.getText("SelectEmployee_T"), Level.SEVERE);
            return;
        }
        if (ZcaValidator.isNullEmpty(messageForEmailForwardToEmployees)){
            this.setBootstrapAlertMessage(RoseText.getText("MessageForEmployee") + ": " + RoseText.getText("FieldRequired_T"), Level.SEVERE);
            return;
        }
        
        RoseEmailMessage targetEmailMessage = this.getTargetBusinessEmailMessage();
        if ((targetEmailMessage == null) || (!targetEmailMessage.isContentLoaded())){
            this.setBootstrapAlertMessage(RoseText.getText("MessageContentLoadedDemanded"), Level.SEVERE);
            return;
        }
        
        List<String> emailTos = new ArrayList<>();
        for (EmployeeAccountProfile aEmployeeAccountProfile : selectedEmployeeAccountProfilesForEmailForward){
            emailTos.add(aEmployeeAccountProfile.getEmployeeEntity().getWorkEmail());
        }
        
        String targetContent = ZcaNio.lineSeparator() + ZcaNio.lineSeparator() 
                                + messageForEmailForwardToEmployees
                                + ZcaNio.lineSeparator() + ZcaNio.lineSeparator() 
                                + "---------------------------------------------------------" 
                                + ZcaNio.lineSeparator() 
                                + targetEmailMessage.getEmailContent();
        
        List<String> replyToList = new ArrayList<>();
        replyToList.add(getTargetEmailFrom());
        getRuntimeEJB().sendEncryptedEmailToRecipients(getTargetEmailFrom(), 
                                                        emailTos, 
                                                        replyToList,
                                                        "Forward: " + targetEmailMessage.getSubject(), 
                                                        targetContent, 
                                                        getRoseSettings().isEmailDisabled());
        
        this.setBootstrapAlertMessage(RoseText.getText("OperationSucceeded_T"), null);
        messageForEmailForwardToEmployees = null;
    }
    
    public void moveEmailIntoGarbage(long msgUid){
        this.getRoseEmailController().moveEmailIntoGarbage(this.getTargetEmployeeAccountProfile(), msgUid);
    }
    
    public void markGarbageMessageForDeletion(long msgUid){
        this.getRoseEmailController().markGarbageMessageForDeletion(this.getTargetEmployeeAccountProfile(), msgUid);
    }
    
    public String markAllGarbageMessagesForDeletion(){
        this.getRoseEmailController().markAllGarbageMessagesForDeletion(this.getTargetEmployeeAccountProfile());
        return RoseWebUtils.redirectToRosePage(RosePageName.BusinessHomePage);
    }
    
    public void rollbackEmailToInbox(long msgUid){
        this.getRoseEmailController().rollbackEmailToInbox(this.getTargetEmployeeAccountProfile(), msgUid);
    }
    
    public boolean isEmailStorageLoaded(){
        return this.getRoseEmailController().isEmailStorageLoaded(this.getTargetEmployeeAccountProfile());
    }
    
    public List<RoseEmailMessage> getRoseInboxEmailList(){
        return this.getRoseEmailController().getRoseInboxEmailList(this.getTargetEmployeeAccountProfile());
    }

    public List<RoseEmailMessage> getRoseGarbageMessageList(){
        return this.getRoseEmailController().getRoseGarbageMessageList(this.getTargetEmployeeAccountProfile());
    }

    public List<RoseEmailMessage> getRoseSentMessageList(){
        return this.getRoseEmailController().getRoseSentMessageList(this.getTargetEmployeeAccountProfile());
    }
    
    public int getEmailBoxRefreshInterval(){
        return this.getRoseEmailController().getEmailBoxRefreshInterval(this.getTargetEmployeeAccountProfile());
    }
    
    public boolean isTargetEmailContentLoaded(){
        RoseEmailMessage aGardenEmailMessage = getTargetBusinessEmailMessage();
        if (aGardenEmailMessage != null){
            return aGardenEmailMessage.isContentLoaded();
        }
        return true;
    }
    
    public RoseEmailMessage getTargetBusinessEmailMessage() {
        return this.getRoseEmailController().getTargetEmailMessageFromManager(this.getTargetEmployeeAccountProfile());
    }
    
    public String getTargetBusinessEmailMessageId(){
        return Long.toString(getTargetBusinessEmailMessage().getMsgUid());
    }
    
    /**
     * 
     * @param msgUid 
     */
    public synchronized void setTargetBusinessEmailMessageId(String msgUid){
        try{
            initializeTargetEmailPage(Long.parseLong(msgUid));
        }catch(NumberFormatException ex){}
    }
    
    private synchronized void initializeTargetEmailPage(long msgUid){
        super.getRoseEmailController().markEmailMessageForContentLoading(this.getTargetEmployeeAccountProfile(), msgUid);
        setEmailMessageForReply(null, this.getEmployeeWorkingEmail());
        setEmailEditorRequired(false);
        RoseEmailMessage aGardenEmailMessage = getRoseEmailController().getTargetEmailMessageFromManager(this.getTargetEmployeeAccountProfile());
        if (aGardenEmailMessage != null) {
            aGardenEmailMessage.setUnread(false);
        }
        this.setWebMessage(null);
    }
    
    public RoseEmailMessage getTargetSentEmailMessage() {
        return this.getRoseEmailController().getTargetSentEmailMessageFromManager(this.getTargetEmployeeAccountProfile());
    }
    
    public String getTargetSentEmailMessageId(){
        return Long.toString(getTargetSentEmailMessage().getMsgUid());
    }
    
    public synchronized void setTargetSentEmailMessageId(String msgUid){
        try{
            initializeTargetSentEmailPage(Long.parseLong(msgUid));
        }catch(NumberFormatException ex){}
    }
    
    private synchronized void initializeTargetSentEmailPage(long msgUid){
//        super.getRoseEmailController().markEmailMessageForContentLoading(this.getTargetEmployeeAccountProfile(), msgUid);
        setEmailMessageForReply(null, this.getEmployeeWorkingEmail());
        setEmailEditorRequired(false);
        RoseEmailMessage aGardenEmailMessage = getRoseEmailController().getTargetSentEmailMessageFromManager(this.getTargetEmployeeAccountProfile(), msgUid);
        if (aGardenEmailMessage == null) {
            this.setWebMessage("Cannot find that email record");
        }else{
            this.setWebMessage(null);
        }
    }
    
    public String getEmployeeWorkingEmail(){
        if (isEmployed()){
            return getTargetEmployeeAccountProfile().getEmployeeEntity().getWorkEmail();
        }else{
            return "N/A";
        }
    }
    
    private boolean emailEditorRequired;

    public boolean isEmailEditorRequired() {
        return emailEditorRequired;
    }

    public void setEmailEditorRequired(boolean emailEditorRequired) {
        this.emailEditorRequired = emailEditorRequired;
    }
    
    public void openEmailEditorPanelForReply(){
        setEmailEditorRequired(true);
        setEmailMessageForReply(this.getRoseEmailController().getTargetEmailMessageFromManager(getTargetEmployeeAccountProfile()), 
                                this.getEmployeeWorkingEmail());
    }
    
    public void closeEmailEditorPanel(){
        setEmailEditorRequired(false);
    }
    
    private int selectedRoseInboxEmailListPageNumber;
    private int selectedRoseGarbageEmailListPageNumber;
    private int selectedRoseSentEmailListPageNumber;

    public int getSelectedRoseInboxEmailListPageNumber() {
        return selectedRoseInboxEmailListPageNumber;
    }

    public void setSelectedRoseInboxEmailListPageNumber(int selectedRoseInboxEmailListPageNumber) {
        this.selectedRoseInboxEmailListPageNumber = selectedRoseInboxEmailListPageNumber;
    }

    public int getSelectedRoseGarbageEmailListPageNumber() {
        return selectedRoseGarbageEmailListPageNumber;
    }

    public void setSelectedRoseGarbageEmailListPageNumber(int selectedRoseGarbageEmailListPageNumber) {
        this.selectedRoseGarbageEmailListPageNumber = selectedRoseGarbageEmailListPageNumber;
    }

    public int getSelectedRoseSentEmailListPageNumber() {
        return selectedRoseSentEmailListPageNumber;
    }

    public void setSelectedRoseSentEmailListPageNumber(int selectedRoseSentEmailListPageNumber) {
        this.selectedRoseSentEmailListPageNumber = selectedRoseSentEmailListPageNumber;
    }
    
    public void onRoseInboxEmailListPageChange(PageEvent event) {
        this.setSelectedRoseInboxEmailListPageNumber(((DataList) event.getSource()).getFirst());  
    }
    
    public void onRoseGarbageEmailListPageChange(PageEvent event) {
        this.setSelectedRoseGarbageEmailListPageNumber(((DataList) event.getSource()).getFirst());  
    }
    
    public void onRoseSentEmailListPageChange(PageEvent event) {
        this.setSelectedRoseSentEmailListPageNumber(((DataList) event.getSource()).getFirst());  
    }
    
    /**
     * Try to load new emails from the email server and meanwhile, if any message was marked as deletion, delete them.
     */
    public void refreshEmailBox(){
        this.getRoseEmailController().refreshEmailStorageForEmployee(this.getTargetEmployeeAccountProfile());
    }
    
    public void loadTargetEmployeeEmail(){
        this.getRoseEmailController().loadTargetEmployeeEmail(this.getTargetEmployeeAccountProfile());
    }
    
    @Override
    public String sendTargetEmail(){
        if (sendTargetEmailHelper()){
           setEmailEditorRequired(false);
        }
        return null;
    }
    
    /**
     * 
     * @return - NULL if the target user is not employee
     */
    public EmployeeAccountProfile getTargetEmployeeAccountProfile(){
        if (getTargetAccountProfile() instanceof EmployeeAccountProfile){
            return (EmployeeAccountProfile)getTargetAccountProfile();
        }
        return null;
    }

    public int getSelectedDaysBeforeDeadlineForTaxpayer() {
        return selectedDaysBeforeDeadlineForTaxpayer;
    }

    public void setSelectedDaysBeforeDeadlineForTaxpayer(int selectedDaysBeforeDeadlineForTaxpayer) {
        this.selectedDaysBeforeDeadlineForTaxpayer = selectedDaysBeforeDeadlineForTaxpayer;
    }

    public int getSelectedDaysBeforeDeadlineForTaxcorp() {
        return selectedDaysBeforeDeadlineForTaxcorp;
    }

    public void setSelectedDaysBeforeDeadlineForTaxcorp(int selectedDaysBeforeDeadlineForTaxcorp) {
        this.selectedDaysBeforeDeadlineForTaxcorp = selectedDaysBeforeDeadlineForTaxcorp;
    }

    public List<TaxcorpFilingConciseProfile> getUrgentTaxcorpCaseConciseProfileList() {
        if (urgentTaxcorpCaseConciseProfileList == null){
            urgentTaxcorpCaseConciseProfileList = getTaxcorpEJB().findUrgentTaxcorpCaseProfileList(selectedDaysBeforeDeadlineForTaxcorp);
        }
        return urgentTaxcorpCaseConciseProfileList;
    }

    public List<TaxpayerCaseConciseProfile> getUrgentTaxpayerCaseProfileTaskList() {
        if (urgentTaxpayerCaseProfileTaskList == null){
            urgentTaxpayerCaseProfileTaskList = getTaxpayerEJB().findUrgentTaxpayerCaseProfileList(selectedDaysBeforeDeadlineForTaxpayer);
        }
        return urgentTaxpayerCaseProfileTaskList;
    }

    public RoseWebPageConfig getWebPageConfig() {
        return webPageConfig;
    }
    
    public void onRoseToggleEvent(ToggleEvent event) {
        if ((event == null) || (event.getComponent() == null)){
            return;
        }
        webPageConfig.toggleWebComponentCollapseExpand(RoseWebIdValue.convertEnumValueToType(event.getComponent().getId()));
    }

    @Override
    public String getRosePageTopic() {
        return RoseText.getText("AccountProfile");
    }

    @Override
    public String getRequestedEntityUuid() {
        return this.getTargetAccountProfile().getAccountEntity().getAccountUuid();
    }
    
    @PostConstruct
    public synchronized void initializeRoseUserSession(){
        initializeLocaleCode(null);
    }
    
    @PreDestroy
    public synchronized void destroyRoseUserSession(){
        if (targetXmppConnection != null){
            targetXmppConnection.disconnect();
            targetXmppConnection = null;
        }
    }
    
    private synchronized void initializeLocaleCode(RoseAccountProfile accountProfile){
        /**
         * todo zzj: if accountProfile is authenticated, it could use of accountProfile's web language field value. Notice 
         * when implement this feature, when user logged in, and change it from the top-nav-bar, the system should also 
         * save it into the garden database but not only the local cookies
         */
        String cookieName = RoseWebContants.ROSE_LOCALE_LANG.value();
        if (accountProfile != null){
            cookieName += accountProfile.getAccountEntity().getAccountUuid();
        }
        Cookie cookie = RoseJsfUtils.getCookie(cookieName);
        if (cookie == null){
            localeCode = Locale.getDefault().getLanguage();
        }else{
            setLocaleCode(cookie.getValue());
            this.handlePreRenderViewEvent();
        }
    
    }

    public synchronized String getLocaleCode() {
        return localeCode;
    }
    
    public synchronized void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }
    
    //value change event listener
    public synchronized void localeCodeChanged(ValueChangeEvent event){
        String newLocaleValue = event.getNewValue().toString();
        //loop country map to compare the locale code
        getRoseSettings().getLocaleMap().entrySet().stream().filter((entry) -> (entry.getValue().toString().equals(newLocaleValue))).forEachOrdered((entry) -> {
            FacesContext.getCurrentInstance().getViewRoot().setLocale((Locale)entry.getValue());
            if (this.isValidAuthenticatedStatus()){
                RoseJsfUtils.setCookie(RoseWebContants.ROSE_LOCALE_LANG.value()+getTargetAccountProfile().getAccountEntity().getAccountUuid(), newLocaleValue);
            }
            RoseJsfUtils.setCookie(RoseWebContants.ROSE_LOCALE_LANG.value(), newLocaleValue);
        });
    }
    
    /**
     * This handler apply settings, such as i18n, for the public. 
     */
    public synchronized void handlePreRenderViewEvent(){
        //apply user's locale settings, which can be done before authentication
        if (ZcaValidator.isNotNullEmpty(localeCode)){
            //only update when localeCode is different from the system's locale
            RoseJsfUtils.setLocaleCode(localeCode);
        }
    }

    public RoseAccountProfile getTargetForgottenAccountProfile() {
        if (targetForgottenAccountProfile == null){
            targetForgottenAccountProfile = new RoseAccountProfile();
        }
        return targetForgottenAccountProfile;
    }

    public void setTargetForgottenAccountProfile(RoseAccountProfile targetForgottenAccountProfile) {
        this.targetForgottenAccountProfile = targetForgottenAccountProfile;
    }

    public RoseAccountProfile getTargetRedeemAccountProfileForUserAnswer() {
        if (targetRedeemAccountProfileForUserAnswer == null){
            targetRedeemAccountProfileForUserAnswer = new RoseAccountProfile();
        }
        return targetRedeemAccountProfileForUserAnswer;
    }

    public void setTargetRedeemAccountProfileForUserAnswer(RoseAccountProfile targetRedeemAccountProfileForUserAnswer) {
        this.targetRedeemAccountProfileForUserAnswer = targetRedeemAccountProfileForUserAnswer;
    }

    public List<RoseAccountProfile> getForgottenAccountProfileList() {
        if (forgottenAccountProfileList == null){
            forgottenAccountProfileList = new ArrayList<>();
        }
        return forgottenAccountProfileList;
    }

    public String getTargetLoginName() {
        return targetLoginName;
    }

    public void setTargetLoginName(String targetLoginName) {
        this.targetLoginName = targetLoginName;
    }

    public String getTargetLoginPassword() {
        return targetLoginPassword;
    }

    public void setTargetLoginPassword(String targetLoginPassword) {
        this.targetLoginPassword = targetLoginPassword;
    }

    public String getTargetAccountConfirmationCode() {
        return targetAccountConfirmationCode;
    }

    public void setTargetAccountConfirmationCode(String targetAccountConfirmationCode) {
        this.targetAccountConfirmationCode = targetAccountConfirmationCode;
    }

    public boolean isSuspendedAuthenticatedStatus() {
        return getTargetAccountProfile().isAuthenticated() && getTargetAccountProfile().isSuspendedAccount();
    }

    public String login(){
        return loginHelper(getBusinessEJB().findAuthenticatedRoseAccountProfile(targetLoginName, targetLoginPassword));
    }

    private String loginHelper(RoseAccountProfile aRoseAccountProfile) {
//////        return RosePageName.WelcomePage.name();
        if (aRoseAccountProfile == null){
            RoseJsfUtils.setGlobalErrorFacesMessage(RoseText.getText("LoginFailed_T"));
            return RosePageName.WelcomePage.name();
        }
        
        if (aRoseAccountProfile.isAuthenticated()){
            if (aRoseAccountProfile.isValidAccount()){
                //current target account-user profile
                setTargetAccountProfile(aRoseAccountProfile);
                //HTTP session for filters
                RoseJsfUtils.storeUserSessionForRoseFilters(this);
                //custom web language
                initializeLocaleCode(aRoseAccountProfile);
                if (getRoseSettings().isXmppDisabled()){
                    aRoseAccountProfile.setXmppDisabled(true);
                }else{
                    aRoseAccountProfile.setXmppDisabled(false);
                    try {
                        //XMPP connection: todo zzj: hook this connection to the topic hoster
                        targetXmppConnection = RoseXmppUtils.loginXmppServer(aRoseAccountProfile.getXmppAccountEntity());
                    } catch (SmackException | IOException | XMPPException ex) {
                        getRuntimeEJB().sendCriticalTechnicalException(ex, getRoseSettings().isEmailDisabled(), getRoseSettings().isSmsDisabled());
                    }
                }
                if (isEmployed()){
                    try {
                        //check out emails
                        String employeeWorkingEmail = ((EmployeeAccountProfile)aRoseAccountProfile).getEmployeeEntity().getWorkEmail();
                        if (ZcaValidator.isNotNullEmpty(employeeWorkingEmail)){
                            getRoseEmailController().launchEmailStorageForEmployee(getTargetEmployeeAccountProfile());
                        }
                    } catch (Exception ex) {
                        //Logger.getLogger(RoseUserSessionBean.class.getName()).log(Level.SEVERE, null, ex);
                        RoseJsfUtils.setGlobalErrorFacesMessage(ex.getMessage());
                        return null;
                    }
                    return RoseWebUtils.BUSINESS_FOLDER + "/" + RoseWebUtils.redirectToRosePage(RosePageName.BusinessHomePage);
                }else{
                    return RoseWebUtils.CUSTOMER_FOLDER + "/" + RoseWebUtils.redirectToRosePage(RosePageName.CustomerHomePage);
                }
            }else{
                //demand confirmation for suspended account
                if (aRoseAccountProfile.isSuspendedAccount()){
                    targetLoginName = aRoseAccountProfile.getAccountEntity().getLoginName();
                    return RoseWebUtils.redirectToRosePage(RosePageName.AccountConfirmationPage);
                }
                RoseJsfUtils.setGlobalErrorFacesMessage(RoseText.getText("BadAccountStatus_T"));
                return null;
            }
        }else{
            Logger.getLogger(RoseUserSessionBean.class.getName()).log(Level.SEVERE, "Account is not authenticated.");
            RoseJsfUtils.setGlobalTechnicalErrorFacesMessage("Account is not authenticated.");
            return null;
        }
    }
    
    public void preLogout(){
        this.getRoseEmailController().destroyRoseEmailManager(getTargetEmployeeAccountProfile());
        
        this.targetLoginName = null;
        this.targetLoginPassword = null;
        getTargetAccountProfile().setAccountEntity(new G01Account());
        getTargetAccountProfile().setAuthenticated(false);
    }
    
    public String logout(){
        
        RoseJsfUtils.deleteUserSessionForRoseFilters();
        
        if (targetXmppConnection != null){
            if (targetXmppConnection.isConnected()){
                //todo zzj: remove this connection from the hoster also
                this.targetXmppConnection.disconnect();
            }
        }
        /**
         * The following redirected-return page caused troubles: after log out from a specific page, it still stays on that 
         * page somehow and not-really redirected to welcome-page. This is a wierd problem. todo zzj: should fix it.
         * 
         * faces-config nav-rule: if remove or not remove "<redirect/>" from WelcomePage, and the following use true, it 
         * caused the same problem as the above-mentioned issue
         */
        //return RoseWebUtils.redirectToRosePage(RosePageName.WelcomePage, true);
        
        /**
         * faces-config nav-rule: if remove "<redirect/>" from WelcomePage, the following will go to the welcome page but 
         * without refresh the top navigation if "<redirect/>" stay there, the error message cannot be displayed when login 
         * has something wrong.
         * 
         * The solutiono is logout command button uses both actionListener and action. Meanwhile, "<redirect/>" is removed 
         * from faces-config.xml
         */
        return RosePageName.WelcomePage.name();
    }
    
    @Override
    public String storeTargetPersonalProfile(){
        if (!this.isValidAuthenticatedStatus()){
            return RosePageName.WelcomePage.name();
        }
        RoseAccountProfile currentTargetAccountProfile = getTargetAccountProfile();
        try {
            validateAccountProfile(currentTargetAccountProfile);
            //store registration data
            getBusinessEJB().storeRoseAccountProfile(currentTargetAccountProfile);

            RoseJsfUtils.setGlobalSuccessfulOperationMessage();
            
        } catch (ZcaEntityValidationException ex) {
            RoseJsfUtils.setGlobalFatalFacesMessage(ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(RoseUserSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalFatalFacesMessage(RoseText.getText("SystemError") + ": " + ex.getMessage());
        }
        return null;
    }
    
    /**
     * Register a web user on behalf of the web user. This does not consider the redundant record issue which would be demanded 
     * by the business management.
     * @return 
     */
    public String registerWebUser(){
        RoseAccountProfile currentTargetAccountProfile = getTargetAccountProfile();
        //Login name has to be unique
        HashMap<String, Object> params = new HashMap<>();
        params.put("loginName", currentTargetAccountProfile.getAccountEntity().getLoginName());
        try {
            G01Account aG01Account = getBusinessEJB().findEntityByNamedQuery(G01Account.class, "G01Account.findByLoginName", params);
            if (aG01Account != null){
                RoseJsfUtils.setGlobalErrorFacesMessage(RoseText.getText("RedundantLoginNameFound_T"));
                return null;
            }
        } catch (NonUniqueEntityException ex) {
            Logger.getLogger(RoseUserSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalSystemErrorFacesMessage(ex.getMessage());
            return null;
        }
        
        try {
            currentTargetAccountProfile.getUserProfile().getUserEntity().setEntityStatus(GardenEntityStatus.ONLINE_CUSTOMER.value());
            //set the current account status
            currentTargetAccountProfile.getAccountEntity().setAccountStatus(GardenAccountStatus.Suspend.name());
            currentTargetAccountProfile.getAccountEntity().setAccountConfirmationCode(GardenData.generateRandomSecretCode(6));

            //validate email/phone which are required only for online web registration
            if (ZcaValidator.isNullEmpty(currentTargetAccountProfile.getAccountEntity().getAccountEmail())){
                throw new ZcaEntityValidationException(RoseText.getText("Email") + ": " + RoseText.getText("FieldRequired_T"));
            }
            if (ZcaValidator.isNullEmpty(currentTargetAccountProfile.getAccountEntity().getMobilePhone())){
                throw new ZcaEntityValidationException(RoseText.getText("Phone") + ": " + RoseText.getText("FieldRequired_T"));
            }
            
            //validate the account profile
            validateAccountProfile(currentTargetAccountProfile);

            //store registration data
            getBusinessEJB().storeRoseAccountProfile(currentTargetAccountProfile);

            //the following is for Rose filter
            RoseJsfUtils.storeUserSessionForRoseFilters(this);

            if (getRoseSettings().isXmppDisabled()){
                currentTargetAccountProfile.setXmppDisabled(true);
            }else{
                currentTargetAccountProfile.setXmppDisabled(false);
                //create a XMPP account
                AbstractXMPPConnection conn = RoseXmppUtils.createAbstractXMPPConnection();
                RoseXmppUtils.createXmppAccount(RoseXmppUtils.createAbstractXMPPConnection(), this.getTargetAccountProfile().getXmppAccountEntity());
                if (conn.isConnected()){
                    conn.disconnect();
                }
            }

            //SMS and send email for confirmation code
            sendConfirmationCodeByMobileSMS(currentTargetAccountProfile);
            sendConfirmationCodeByEncryptedEmail(currentTargetAccountProfile);

            //set it to be authenticated
            currentTargetAccountProfile.setAuthenticated(true);

            //force AccountConfirmationPage have the code being empty
            HashMap<String, String> webParams = new HashMap<>();
            params.put(getRoseParamKeys().getRegistrationConfirmationCodeParamKey(), "");
            return RosePageName.AccountConfirmationPage.name() + RoseWebUtils.constructWebQueryString(webParams, true);
        } catch (ZcaEntityValidationException ex) {
            RoseJsfUtils.setGlobalFatalFacesMessage(ex.getMessage());
        } catch (Exception ex) {
            getRuntimeEJB().sendCriticalTechnicalException(ex, getRoseSettings().isEmailDisabled(), getRoseSettings().isSmsDisabled());
            RoseJsfUtils.setGlobalFatalFacesMessage(RoseText.getText("SystemError") + ": " + ex.getMessage());
        }
        return null;
    }

    private void sendConfirmationCodeByMobileSMS(RoseAccountProfile targetAccountProfile) {
        sendMessageByMobileSMS(targetAccountProfile, 
                               "Your registration confirmation code is " + targetAccountProfile.getAccountEntity().getAccountConfirmationCode());
    }

    private void sendCerdentialsByMobileSMS(RoseAccountProfile targetAccountProfile) {
        sendMessageByMobileSMS(targetAccountProfile, 
                               "Login name [" + targetAccountProfile.getAccountEntity().getLoginName() + "] Password ["
                                    + RoseWebCipher.getSingleton().decrypt(targetAccountProfile.getAccountEntity().getEncryptedPassword())+"]");
    }
    
    private void sendMessageByMobileSMS(RoseAccountProfile targetAccountProfile, String message){
        if (getRoseSettings().isSmsDisabled()){
            String msg = "Registration confirmation code cann't be sent out because current system is not production version.";
            Logger.getLogger(RoseUserSessionBean.class.getName()).log(Level.WARNING, msg);
            //RoseJsfUtils.setGlobalErrorFacesMessage(msg);
            return;
        }
        try {
            RoseSmsSender.sendSmsText(targetAccountProfile.getAccountEntity().getMobilePhone(), 
                                         getRoseSettings().getWebSiteBrand() + ": " + message );
        } catch (Exception ex) {
            getRuntimeEJB().sendCriticalTechnicalExceptionByEmail(ex, getRoseSettings().isSmsDisabled());
        }
    }

    private void sendConfirmationCodeByEncryptedEmail(RoseAccountProfile targetAccountProfile) {
        if (getRoseSettings().isEmailDisabled()){
            String msg = "Registration confirmation code cann't be sent out because current system is not production version.";
            Logger.getLogger(RoseUserSessionBean.class.getName()).log(Level.WARNING, msg);
            //RoseJsfUtils.setGlobalErrorFacesMessage(msg);
            return;
        }
        G01Account accountEntity = targetAccountProfile.getAccountEntity();
        String accountConfirmationAbsoluteWebPath;
        try{
            accountConfirmationAbsoluteWebPath = RoseJsfUtils.getWebPagePathUnderRoot(RosePageName.AccountConfirmationPage);
            HashMap<String, String> params = new HashMap<>();
            params.put(getRoseParamKeys().getRegistrationConfirmationCodeParamKey(), accountEntity.getAccountConfirmationCode());
            accountConfirmationAbsoluteWebPath += RoseWebUtils.constructWebQueryString(params, false);
        }catch(Exception ex){
            Logger.getLogger(RoseUserSessionBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalFatalFacesMessage(ex.getMessage());
            return;
        }
        String subject = "Your Confirmation Code: " + accountEntity.getAccountConfirmationCode();
        String targetContent = "Thanks, " + targetAccountProfile.getUserProfile().getUserEntity().getFirstName() 
                + ", for registering on our web site. " + subject
                + ZcaNio.lineSeparator() + ZcaNio.lineSeparator()
                +"Please click the following web link to confirm your registration: "
                + ZcaNio.lineSeparator() + ZcaNio.lineSeparator()
                + (ZcaValidator.isNotNullEmpty(accountConfirmationAbsoluteWebPath) ? accountConfirmationAbsoluteWebPath 
                : "[Your confirmation code is not available for now. Please contact "+HostMonsterEmailSettings.ServiceEmail.value()+"]")
                + " " + ZcaNio.lineSeparator() + ZcaNio.lineSeparator()
                + "Please go back to our web site to use this code to confirm your registration."
                + ZcaNio.lineSeparator() + ZcaNio.lineSeparator()
                + "Thanks,"
                + ZcaNio.lineSeparator() + ZcaNio.lineSeparator()
                + "WebMaster" + ZcaNio.lineSeparator() 
                + "yinlucpapc.com" + ZcaNio.lineSeparator()
                + ZcaCalendar.convertToMMddyyyy(new Date(), "/");
        try {
            List<String> replyToList = new ArrayList<>();
            replyToList.add(HostMonsterEmailSettings.ServiceEmail.value());
            replyToList.add(HostMonsterEmailSettings.DeveloperEmail.value());
            RoseEmailSender.sendEncryptedEmail(HostMonsterEmailSettings.ServiceEmail.value(),
                                                accountEntity.getAccountEmail(),
                                                replyToList,
                                                subject,
                                                targetContent);
        } catch (Exception ex) {
            getRuntimeEJB().sendCriticalTechnicalExceptionBySms(ex, getRoseSettings().isSmsDisabled());
        }
    }

    private void sendCerdentialsByEncryptedEmail(RoseAccountProfile targetAccountProfile) {
        if (getRoseSettings().isEmailDisabled()){
            String msg = "Registration confirmation code cann't be sent out because current system is not production version.";
            Logger.getLogger(RoseUserSessionBean.class.getName()).log(Level.WARNING, msg);
            //RoseJsfUtils.setGlobalErrorFacesMessage(msg);
            return;
        }
        String subject = "Redeemed record information";
        String targetContent = "Hi, " + targetAccountProfile.getUserProfile().getUserEntity().getFirstName()
                + ZcaNio.lineSeparator() + ZcaNio.lineSeparator()
                +"The redeemed information are the followings: "
                + ZcaNio.lineSeparator() + ZcaNio.lineSeparator()
                + "Login name [" + targetAccountProfile.getAccountEntity().getLoginName() + "]"
                + ZcaNio.lineSeparator()
                + "Password [" + RoseWebCipher.getSingleton().decrypt(targetAccountProfile.getAccountEntity().getEncryptedPassword())+"]"
                + ZcaNio.lineSeparator() + ZcaNio.lineSeparator()
                + "Thanks,"
                + ZcaNio.lineSeparator() + ZcaNio.lineSeparator()
                + "WebMaster" + ZcaNio.lineSeparator() 
                + "yinlucpapc.com" + ZcaNio.lineSeparator()
                + ZcaCalendar.convertToMMddyyyy(new Date(), "/");
        try {
            List<String> replyToList = new ArrayList<>();
            replyToList.add(HostMonsterEmailSettings.ServiceEmail.value());
            replyToList.add(HostMonsterEmailSettings.DeveloperEmail.value());
            RoseEmailSender.sendEncryptedEmail(HostMonsterEmailSettings.ServiceEmail.value(),
                                                targetAccountProfile.getAccountEntity().getAccountEmail(),
                                                replyToList, subject, targetContent);
        } catch (Exception ex) {
            getRuntimeEJB().sendCriticalTechnicalExceptionBySms(ex, getRoseSettings().isSmsDisabled());
        }
    }
    
    public String resendConfirmationCode(){
        if ((ZcaValidator.isNullEmpty(targetLoginName))
                || (ZcaValidator.isNullEmpty(targetLoginPassword)))
        {
            RoseJsfUtils.setGlobalErrorFacesMessage(RoseText.getText("NoData_T"));
            return null;
        }
        RoseAccountProfile currentTargetAccountProfile = getBusinessEJB().findAuthenticatedRoseAccountProfile(targetLoginName, targetLoginPassword);
        if (currentTargetAccountProfile == null){
            RoseJsfUtils.setGlobalErrorFacesMessage(RoseText.getText("LoginFailed_T"));
            return null;
        }
        setTargetAccountProfile(currentTargetAccountProfile);
        
        currentTargetAccountProfile.getAccountEntity().setAccountConfirmationCode(GardenData.generateRandomSecretCode(6));

        try {
            //store registration data
            getBusinessEJB().storeEntityByUuid(G01Account.class, 
                                               currentTargetAccountProfile.getAccountEntity(), 
                                               currentTargetAccountProfile.getAccountEntity().getAccountUuid(),
                                               G01DataUpdaterFactory.getSingleton().getG01AccountUpdater());
        } catch (Exception ex) {
            getRuntimeEJB().sendCriticalTechnicalException(ex, getRoseSettings().isEmailDisabled(), getRoseSettings().isSmsDisabled());
            RoseJsfUtils.setGlobalFatalFacesMessage(RoseText.getText("SystemError") + ": " + ex.getMessage());
            return null;
        }
        
        //SMS and send email for confirmation code
        sendConfirmationCodeByMobileSMS(currentTargetAccountProfile);
        sendConfirmationCodeByEncryptedEmail(currentTargetAccountProfile);
        
        RoseJsfUtils.setGlobalInfoFacesMessage(RoseText.getText("ConfirmationCode_T"));
        return null;
    }
    
    public String confirmAccountRegistration(){
        if ((ZcaValidator.isNullEmpty(targetLoginName))
                || (ZcaValidator.isNullEmpty(targetLoginPassword))
                || (ZcaValidator.isNullEmpty(this.targetAccountConfirmationCode)))
        {
            RoseJsfUtils.setGlobalErrorFacesMessage(RoseText.getText("NoData_T"));
            return null;
        }
        
        RoseAccountProfile aRoseAccountProfile = getBusinessEJB().findAuthenticatedRoseAccountProfile(targetLoginName, targetLoginPassword);
        if (aRoseAccountProfile == null){
            RoseJsfUtils.setGlobalErrorFacesMessage(RoseText.getText("LoginFailed_T"));
            return null;
        }
        if (targetAccountConfirmationCode.equalsIgnoreCase(aRoseAccountProfile.getAccountEntity().getAccountConfirmationCode())){
            aRoseAccountProfile.getAccountEntity().setAccountStatus(GardenAccountStatus.Valid.name());
            aRoseAccountProfile.getAccountEntity().setAccountConfirmationCode("");
            
            try {
                validateAccountProfile(aRoseAccountProfile);
                getBusinessEJB().storeRoseAccountProfile(aRoseAccountProfile);
            } catch (ZcaEntityValidationException ex) {
                RoseJsfUtils.setGlobalFatalFacesMessage(ex.getMessage());
                return null;
            } catch (Exception ex) {
                getRuntimeEJB().sendCriticalTechnicalException(ex, getRoseSettings().isEmailDisabled(), getRoseSettings().isSmsDisabled());
                RoseJsfUtils.setGlobalFatalFacesMessage(RoseText.getText("SystemError") + ": " + ex.getMessage());
                return null;
            }
            
            return loginHelper(aRoseAccountProfile);
        }else{
            RoseJsfUtils.setGlobalErrorFacesMessage(RoseText.getText("WrongCode_T"));
        }
        return null;
    }
    
    public String redeemCredentialsByEmail(){
        String accountEmail = getTargetForgottenAccountProfile().getAccountEntity().getAccountEmail();
        if (ZcaValidator.isNullEmpty(accountEmail)){
            RoseJsfUtils.setGlobalErrorFacesMessage(RoseText.getText("NoData_T"));
            return null;
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("accountEmail", accountEmail);
        
        redeemCredentialsPostProcess(getBusinessEJB().findForgottenAccountProfileListByMobileHint(accountEmail));
        getTargetRedeemAccountProfileForUserAnswer().getAccountEntity().setMobilePhone("********");
        
        return RoseWebUtils.redirectToRosePage(RosePageName.RedeemCredentialsPage, false);
    }
    
    public String redeemCredentialsByMobile(){
        String mobilePhone = getTargetForgottenAccountProfile().getAccountEntity().getMobilePhone();
        if (ZcaValidator.isNullEmpty(mobilePhone)){
            RoseJsfUtils.setGlobalErrorFacesMessage(RoseText.getText("NoData_T"));
            return null;
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("mobilePhone", mobilePhone);
        
        redeemCredentialsPostProcess(getBusinessEJB().findForgottenAccountProfileListByMobileHint(mobilePhone));
        getTargetRedeemAccountProfileForUserAnswer().getAccountEntity().setAccountEmail("****@********.***");
        
        return RoseWebUtils.redirectToRosePage(RosePageName.RedeemCredentialsPage, false);
    }

    private void redeemCredentialsPostProcess(List<RoseAccountProfile> forgottenAccountProfileList) {
        if (forgottenAccountProfileList.isEmpty()){
            getTargetForgottenAccountProfile().setBrandNew(true);
            RoseJsfUtils.setGlobalErrorFacesMessage(RoseText.getText("ForgottenAccountNotFoundForRedeem_T"));
        }else{
            /**
             * It may find multiple account profiles but only provide the first one for users to redeem. Others demand 
             * the business management's help 
             */
            setTargetForgottenAccountProfile(forgottenAccountProfileList.get(0));
            getTargetForgottenAccountProfile().setBrandNew(false);
            
            //prepare targetRedeemAccountProfileForUserAnswer
            getTargetRedeemAccountProfileForUserAnswer().cloneProfile(getTargetForgottenAccountProfile());
            
            getTargetRedeemAccountProfileForUserAnswer().getSecurityQnaProfile01().getSecurityQnaEntity().setSequrityAnswer("");
            getTargetRedeemAccountProfileForUserAnswer().getSecurityQnaProfile02().getSecurityQnaEntity().setSequrityAnswer("");
            getTargetRedeemAccountProfileForUserAnswer().getSecurityQnaProfile03().getSecurityQnaEntity().setSequrityAnswer("");
            
            RoseJsfUtils.setGlobalInfoFacesMessage(RoseText.getText("ForgottenAccountFoundForRedeem_T"));
        }
    }
    
    public String redeemCredentials(){
        boolean result = true;
        result = result && getTargetForgottenAccountProfile().getSecurityQnaProfile01().getSecurityQnaEntity().getSequrityAnswer()
                .equalsIgnoreCase(getTargetForgottenAccountProfile().getSecurityQnaProfile01().getSecurityQnaEntity().getSequrityAnswer());
        result = result && getTargetForgottenAccountProfile().getSecurityQnaProfile02().getSecurityQnaEntity().getSequrityAnswer()
                .equalsIgnoreCase(getTargetForgottenAccountProfile().getSecurityQnaProfile02().getSecurityQnaEntity().getSequrityAnswer());
        result = result && getTargetForgottenAccountProfile().getSecurityQnaProfile03().getSecurityQnaEntity().getSequrityAnswer()
                .equalsIgnoreCase(getTargetForgottenAccountProfile().getSecurityQnaProfile03().getSecurityQnaEntity().getSequrityAnswer());
        if (result){
            //SMS and send email for cerdentials
            try{
                sendCerdentialsByMobileSMS(getTargetForgottenAccountProfile());
                sendCerdentialsByEncryptedEmail(getTargetForgottenAccountProfile());
                
                setTargetRedeemAccountProfileForUserAnswer(null);
                setTargetForgottenAccountProfile(null);
                
            }catch (Exception ex) {
                Logger.getLogger(RoseUserSessionBean.class.getName()).log(Level.SEVERE, null, ex);
                RoseJsfUtils.setGlobalErrorFacesMessage(ex.getMessage());
                return null;
            }
            return RoseWebUtils.redirectToRosePage(RosePageName.LoginPage, true);
        }else{
            RoseJsfUtils.setGlobalErrorFacesMessage(RoseText.getText("SecurityQuestionAnswerWrong_T"));
            return null;
        }
    }
    
    public String cancelRedeemCredentials(){
        setTargetRedeemAccountProfileForUserAnswer(null);
        setTargetForgottenAccountProfile(null);
        
        return RoseWebUtils.redirectToRosePage(RosePageName.RegisterPage, true);
    }
    
    public String fixData(){
////        HashMap<String, Object> params = new HashMap<>();
////        params.put("entityType", "TAXCORP_CASE");
////        List<G01Location> aG01LocationList = getBusinessEJB().findEntityListByNamedQuery(G01Location.class, "G01Location.findByEntityType", params);
////        G01Property aG01Property;
////        for (G01Location aG01Location : aG01LocationList){
////            aG01Property = new G01Property();
////            aG01Property.setPropKey(aG01Location.getLocationUuid());
////            aG01Property.setPropValue(aG01Location.getEntityUuid());
////            try {
////                getBusinessEJB().storeEntityByUuid(G01Property.class, aG01Property, aG01Property.getPropKey(), G01DataUpdaterFactory.getSingleton().getG01PropertyUpdater());
////                Logger.getLogger(RoseUserSessionBean.class.getName()).log(Level.INFO, aG01Property.getPropKey()+"="+aG01Property.getPropValue());
////            } catch (Exception ex) {
////                Logger.getLogger(RoseUserSessionBean.class.getName()).log(Level.SEVERE, null, ex);
////            }
////        }
//        List<G01Property> aG01PropertyList = getBusinessEJB().findAll(G01Property.class);
//        G01Location aG01Location;
//        for (G01Property aG01Property : aG01PropertyList){
//            aG01Location = getBusinessEJB().findEntityByUuid(G01Location.class, aG01Property.getPropKey());
//            if (aG01Location == null){
//                Logger.getLogger(RoseUserSessionBean.class.getName()).log(Level.INFO, "WRONG: {0}", aG01Property.getPropKey());
//            }else{
//                try {
//                    aG01Location.setEntityType(aG01Property.getPropValue());
//                    getBusinessEJB().storeEntityByUuid(G01Location.class, aG01Location, aG01Location.getLocationUuid(), G01DataUpdaterFactory.getSingleton().getG01LocationUpdater());
//                    Logger.getLogger(RoseUserSessionBean.class.getName()).log(Level.INFO, aG01Property.getPropKey()+"="+aG01Property.getPropValue());
//                } catch (Exception ex) {
//                    Logger.getLogger(RoseUserSessionBean.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
        Logger.getLogger(RoseUserSessionBean.class.getName()).log(Level.INFO, "Completed");
        return null;
    }
}
