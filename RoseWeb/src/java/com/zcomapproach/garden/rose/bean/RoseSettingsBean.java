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

import com.zcomapproach.garden.data.GardenSettingsProperty;
import com.zcomapproach.garden.data.constant.GardenBooleanValue;
import com.zcomapproach.garden.data.constant.HumanGender;
import com.zcomapproach.garden.data.constant.SearchTaxcorpCriteria;
import com.zcomapproach.garden.data.constant.UState;
import com.zcomapproach.garden.guard.RoseWebCipher;
import com.zcomapproach.garden.persistence.constant.GardenAccountStatus;
import com.zcomapproach.garden.persistence.constant.GardenArchivedFileType;
import com.zcomapproach.garden.persistence.constant.GardenContactType;
import com.zcomapproach.garden.persistence.constant.GardenDiscountType;
import com.zcomapproach.garden.persistence.constant.GardenEmploymentStatus;
import com.zcomapproach.garden.persistence.constant.GardenEntityStatus;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.constant.GardenPayemenType;
import com.zcomapproach.garden.persistence.constant.GardenPreference;
import com.zcomapproach.garden.persistence.constant.GardenPrivilege;
import com.zcomapproach.garden.persistence.constant.GardenPropertyType;
import com.zcomapproach.garden.persistence.constant.GardenTaxpayerCaseStatus;
import com.zcomapproach.garden.persistence.constant.GardenWorkTitle;
import com.zcomapproach.garden.persistence.constant.TaxFilingPeriod;
import com.zcomapproach.garden.persistence.constant.TaxFilingType;
import com.zcomapproach.garden.persistence.constant.TaxcorpBusinessStatus;
import com.zcomapproach.garden.persistence.constant.TaxcorpBusinessType;
import com.zcomapproach.garden.persistence.constant.BusinessContactorRole;
import com.zcomapproach.garden.persistence.constant.TaxpayerFederalFilingStatus;
import com.zcomapproach.garden.persistence.constant.TaxpayerLengthOfLivingTogetherOption;
import com.zcomapproach.garden.persistence.constant.TaxpayerRelationship;
import com.zcomapproach.garden.persistence.entity.G01Account;
import com.zcomapproach.garden.persistence.entity.G01AuthorizedPrivilege;
import com.zcomapproach.garden.persistence.entity.G01AuthorizedPrivilegePK;
import com.zcomapproach.garden.persistence.entity.G01Employee;
import com.zcomapproach.garden.persistence.entity.G01User;
import com.zcomapproach.garden.persistence.entity.G01XmppAccount;
import com.zcomapproach.garden.rose.RosePageName;
import com.zcomapproach.garden.rose.data.constant.CustomerContactMethod;
import com.zcomapproach.garden.rose.data.model.RoseSettingsGridModel;
import com.zcomapproach.garden.rose.data.profile.RoseAccountProfile;
import com.zcomapproach.garden.rose.data.profile.RoseContactInfoProfile;
import com.zcomapproach.garden.rose.data.profile.EmployeeAccountProfile;
import com.zcomapproach.garden.rose.data.profile.RoseLocationProfile;
import com.zcomapproach.garden.rose.data.profile.RoseSettingsProfile;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.garden.rose.util.RoseWebUtils;
import com.zcomapproach.garden.rose.util.RoseXmppUtils;
import com.zcomapproach.garden.taxation.TaxationDeadline;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaText;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.jivesoftware.smack.AbstractXMPPConnection;

/**
 * Settings of Rose web
 * @author zhijun98
 */
@Named(value = "roseSettings")
@ApplicationScoped
public class RoseSettingsBean extends AbstractRoseBean{
    
    /**
     * Current Rose web supports the following locales
     */
    private static final Map<String,Object> locales;
    static{
        locales = new HashMap<>();
        locales.put("English", Locale.ENGLISH);
        locales.put("中文", Locale.CHINESE);
    }
    
    /**
     * Theme for PrimeFaces
     */
    private String primeFacesTheme = "bootstrap";

    /**
     * Theme for BootsFaces. Support switching themes from web.xml. If want to use a custom theme, set BootsFaces_THEME 
     * (i.e. bootsFacesTheme) to "custom". In this case, add the custom theme files manually to JSF page. Typically, the 
     * custom CSS files are a Bootstrap.min.css and a theme.css, so the import looks like this:
     * <p>
     * <h:outputStylesheet library="customthemes" name="bootstrap.min.css"/><br>
     * <h:outputStylesheet library="customthemes" name="Freelancer.css"/>
     */
    private String bootsFacesTheme = "default";
    
    /**
     * Persistent settings of entire Rose site
     */
    private RoseSettingsGridModel roseSettingsGridModel;
    private EmployeeAccountProfile businessOwnerProfile;
    
    /**
     * Web.xhtml settings
     */
    private Boolean xmppDisabled;
    private Boolean emailDisabled;
    private Boolean smsDisabled;
    private Boolean uploadFilePermitted;
    
    public List<String> getSearchTaxcorpCriteriaList(){
        return SearchTaxcorpCriteria.getEnumValueList(false);
    }
    
    public List<String> getAllTaxFilingPeriodList(){
        return TaxFilingPeriod.getEnumValueList(false);  //UNKNOWN is permitted
    }
    
    public List<String> getAllTaxFilingTypeList(){
        return TaxFilingType.getEnumValueList(false);
    }
    
    public List<String> getAllTaxpayerCaseStatusList(){
        return GardenTaxpayerCaseStatus.getEnumValueList(false);
    }

    public List<String> getCustomerContactMethodValueList(){
        return CustomerContactMethod.getEnumValueList(false);
    }
    
    public List<String> getPayemenTypeValueList(){
        return GardenPayemenType.getEnumValueList(false);
    }

    public List<Integer> getDemandedDocumentQuantityConstantValueList(){
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < 25; i++){
            result.add(i);
        }
        return result;
    }

    public List<String> getBooleanStringValues() {
        return GardenBooleanValue.getEnumValueList(false);
    }
    
    public List<String> getBillDiscountTypeValueList(){
        return GardenDiscountType.getEnumValueList(false);
    }
    
    public boolean isXmppDisabled() {
        if (xmppDisabled == null){
            xmppDisabled = "true".equalsIgnoreCase(ZcaText.denullize(FacesContext.getCurrentInstance().getExternalContext()
                    .getInitParameter("com.zcomapproach.garden.rose.settings.NO_XMPP")).toLowerCase());
        }
        return xmppDisabled;
    }
    
    public boolean isEmailDisabled() {
        if (emailDisabled == null){
            emailDisabled = "true".equalsIgnoreCase(ZcaText.denullize(FacesContext.getCurrentInstance().getExternalContext()
                    .getInitParameter("com.zcomapproach.garden.rose.settings.NO_EMAIL")).toLowerCase());
        }
        return emailDisabled;
    }
    
    public boolean isSmsDisabled() {
        if (smsDisabled == null){
            smsDisabled = "true".equalsIgnoreCase(ZcaText.denullize(FacesContext.getCurrentInstance().getExternalContext()
                    .getInitParameter("com.zcomapproach.garden.rose.settings.NO_SMS")).toLowerCase());
        }
        return smsDisabled;
    }

    public boolean isUploadFilePermitted() {
        if (uploadFilePermitted == null){
            uploadFilePermitted = !("true".equalsIgnoreCase(ZcaText.denullize(FacesContext.getCurrentInstance().getExternalContext()
                    .getInitParameter("com.zcomapproach.garden.rose.settings.NO_UPLOADFILE")).toLowerCase()));
        }
        return uploadFilePermitted;
    }
    
    public List<String> getGardenEmploymentStatusValueList(){
        return GardenEmploymentStatus.getEnumValueList(false);
    }
    
    public List<String> getGardenWorkTitleValueList(){
        return GardenWorkTitle.getGardenWorkTitleValueList();
    }
    
    public List<String> getTaxcorpContactorRoleValueList(){
        return BusinessContactorRole.getEnumValueList(false);
    }
    
    public List<String> getBusinessStatusList(){
        return TaxcorpBusinessStatus.getEnumValueList(false);
    }
    
    public List<String> getBusinessTypeList(){
        return TaxcorpBusinessType.getEnumValueList(false);
    }
    
    public List<String> getTaxpayerRelationshipsList(){
        return TaxpayerRelationship.getEnumValueList(false);
    }
    
    public List<String> getTaxpayerFederalFilingStatusList(){
        return TaxpayerFederalFilingStatus.getEnumValueList(false);
    }
    
    public List<String> getGardenPropertyTypeValueList(){
        return GardenPropertyType.getEnumValueList(false);
    }
    
    @PostConstruct
    public void initializeRoseSettings(){
        try {
            getManagementEJB().storeGardenMasterAccount();
            getManagementEJB().storeTechnicalControllerAccount();
        } catch (Exception ex) {
            Logger.getLogger(RoseApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        resetRoseSettings();
    }
    
    @PreDestroy
    public void destroyRoseSettings(){
    
    }
    
    public String getToday(){
        return ZcaCalendar.getFormalToday();
    }
    
    public String getThisYear(){
        return ""+(new GregorianCalendar()).get(Calendar.YEAR);
    }
    
    public List<String> getContactTypeValueList(){
        return GardenContactType.getEnumValueList(false);
    }
    
    public List<String> getPreferenceValueList(){
        return GardenPreference.getEnumValueList(false);
    }
    
    public List<String> getLengthOfLivingTogetherOptionValueList(){
        return TaxpayerLengthOfLivingTogetherOption.getEnumValueList(false);
    }
    
    public List<String> getGardenArchivedFileTypeList(){
        return GardenArchivedFileType.getEnumValueList(false);
    }
    
    /**
     * 
     * @return - USA state name list
     */
    public List<String> getStateNameValueList(){
        List<String> result = UState.getEnumValueList(false);
        result.add(0, UState.NY.value());
        result.add(1, UState.NJ.value());
        result.add(2, UState.CT.value());
        result.add(3, UState.PA.value());
        return result;
    }
    
    public List<String> getHumanGenderValueList(){
        return HumanGender.getEnumValueList(false);
    }
    
    public Map<String, Object> getLocaleMap() {
        return locales;
    }

    public synchronized RoseAccountProfile getBusinessOwnerProfile() {
        return businessOwnerProfile;
    }
    
    public Date getNextIndividualTaxFilingDeadlineDate(){
        return TaxationDeadline.getNextIndividualTaxFilingDeadlineDate(UState.NY);
    }
    
    /**
     * reset RoseSettings unconditionally
     */
    private void resetRoseSettings(){
        roseSettingsGridModel = new RoseSettingsGridModel();
        roseSettingsGridModel.initializeDataModel(getBusinessEJB().retrieveRoseSettingsProfile(), 3);
        
        businessOwnerProfile = getBusinessEJB().findBusinessOwnerAccountProfile();
        if (businessOwnerProfile == null){
            businessOwnerProfile = new EmployeeAccountProfile();
            businessOwnerProfile.getUserProfile().getUserEntity().setEntityStatus(GardenEntityStatus.EMPLOYEE.value());
            businessOwnerProfile.setBrandNew(true);
        }else{
            businessOwnerProfile.setBrandNew(false);
        }
    }

    public RoseSettingsGridModel getRoseSettingsGridModel() {
        return roseSettingsGridModel;
    }

    public void setRoseSettingsGridModel(RoseSettingsGridModel roseSettingsGridModel) {
        this.roseSettingsGridModel = roseSettingsGridModel;
    }

    public synchronized String getPrimeFacesTheme() {
        return primeFacesTheme;
    }

    public synchronized void setPrimeFacesTheme(String primeFacesTheme) {
        this.primeFacesTheme = primeFacesTheme;
    }

    public synchronized String getBootsFacesTheme() {
        return bootsFacesTheme;
    }

    public synchronized void setBootsFacesTheme(String bootsFacesTheme) {
        this.bootsFacesTheme = bootsFacesTheme;
    }
    
    public String getYearlyTaxPeriod(){
        return TaxFilingPeriod.YEARLY.value();
    }
    
    public String getFiscalTaxPeriod(){
        return TaxFilingPeriod.FISCAL.value();
    }
    
    public String getQuarterlyTaxPeriod(){
        return TaxFilingPeriod.QUARTERLY.value();
    }
    
    public String getMonthlyTaxPeriod(){
        return TaxFilingPeriod.MONTHLY.value();
    }
    
    public String getMonthlyNyTaxPeriod(){
        return TaxFilingPeriod.MONTHLY_NY.value();
    }
    
    public String getSemiMonthlyTaxPeriod(){
        return TaxFilingPeriod.SEMI_MONTHLY.value();
    }

    public RoseSettingsProfile getTargetRoseSettingsProfile() {
        return roseSettingsGridModel.getRoseSettingsProfile();
    }
    
    public String storeRoseSetupSettings(){
        try {
            //RoseSettingsProfile: basic settings for Rose web
            RoseSettingsProfile aRoseSettingsProfile = roseSettingsGridModel.retrieveRoseSettingsProfileFromGridModel();
            if (aRoseSettingsProfile == null){
                RoseJsfUtils.setGlobalErrorFacesMessage(RoseText.getText("NoData_T"));
            }else{
                //prepare data so as to make it ready for runtime EJB
                prepareRoseSetupSettings();
                //System properties
                getBusinessEJB().storeG01SystemPropertyList(aRoseSettingsProfile.getG01SystemPropertyList());
                //Business owner's account and user information including address and contact records, which is "businessOwnerProfile"
                getBusinessEJB().storeRoseAccountProfile(businessOwnerProfile);
                businessOwnerProfile.setBrandNew(false);
                
                if (isXmppDisabled()){
                    businessOwnerProfile.setXmppDisabled(true);
                }else{
                    businessOwnerProfile.setXmppDisabled(false);
                    //create a XMPP account
                    AbstractXMPPConnection conn = RoseXmppUtils.createAbstractXMPPConnection();
                    RoseXmppUtils.createXmppAccount(RoseXmppUtils.createAbstractXMPPConnection(), this.businessOwnerProfile.getXmppAccountEntity());
                    if (conn.isConnected()){
                        conn.disconnect();
                    }
                }
                
                resetRoseSettings();
                
                return RoseWebUtils.redirectToRosePage(RosePageName.WelcomePage);
            }
        } catch (Exception ex) {
            Logger.getLogger(RoseSettingsBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalFatalFacesMessage(ex.getMessage());
        }
        
        return null;
    }
    
    /**
     * 
     * @return - true if there is no any data in the database.
     */
    public boolean isBrandNewRose(){
        return roseSettingsGridModel.getRoseSettingsProfile().isBrandNewRose() 
                || businessOwnerProfile.isBrandNew();
    }
    
    public synchronized String getWebPageTitle(){
        return roseSettingsGridModel.getRoseSettingsProfile().getGardenSettingsValue(GardenSettingsProperty.WEB_PAGE_TITLE);
    }
    
    public synchronized String getWebOwnerName() {
        String defaultResult = GardenSettingsProperty.getPropertyDefaultValue(GardenSettingsProperty.WEB_OWNER_NAME);
        String result = roseSettingsGridModel.getRoseSettingsProfile().getGardenSettingsValue(GardenSettingsProperty.WEB_OWNER_NAME);
        if (ZcaValidator.isNullEmpty(result)){
            return defaultResult;
        }else{
            if (!result.contains(defaultResult)){
                result += " / " + defaultResult;
            }
        }
        return result;
    }
    
    public synchronized String getWebOwnerAddress() {
        return roseSettingsGridModel.getRoseSettingsProfile().getGardenSettingsValue(GardenSettingsProperty.WEB_OWNER_ADDRESS);
    }
    
    public synchronized String getWebOwnerPhone() {
        return roseSettingsGridModel.getRoseSettingsProfile().getGardenSettingsValue(GardenSettingsProperty.WEB_OWNER_PHONE);
    }
    
    public synchronized String getWebOwnerFax() {
        return roseSettingsGridModel.getRoseSettingsProfile().getGardenSettingsValue(GardenSettingsProperty.WEB_OWNER_FAX);
    }
    
    public synchronized String getWebOwnerEmail() {
        return roseSettingsGridModel.getRoseSettingsProfile().getGardenSettingsValue(GardenSettingsProperty.WEB_OWNER_EMAIL);
    }
    
    /**
     * This brand is displayed on the left-corner of every web page and also in the SMS message 
     * @return 
     */
    public synchronized String getWebSiteBrand(){
        return roseSettingsGridModel.getRoseSettingsProfile().getGardenSettingsValue(GardenSettingsProperty.WEB_SITE_BRAND);
    }

    private void prepareRoseSetupSettings() {
        //account
        G01Account firstAccountInSystem = businessOwnerProfile.getAccountEntity();
        firstAccountInSystem.setAccountUuid(GardenData.generateUUIDString());
        firstAccountInSystem.setAccountStatus(GardenAccountStatus.Valid.name());
        firstAccountInSystem.setEncryptedPassword(RoseWebCipher.getSingleton().encrypt(firstAccountInSystem.getPassword()));
        firstAccountInSystem.setPassword(null);
        
        if (isXmppDisabled()){
            businessOwnerProfile.setXmppDisabled(true);
        }else{
            businessOwnerProfile.setXmppDisabled(false);
            //XMPP-account
            G01XmppAccount aG01XmppAccount = new G01XmppAccount();
            aG01XmppAccount.setXmppAccountUuid(firstAccountInSystem.getAccountUuid());
            aG01XmppAccount.setEncryptedPassword(firstAccountInSystem.getEncryptedPassword());
            aG01XmppAccount.setLoginName(firstAccountInSystem.getLoginName());
            aG01XmppAccount.setPassword(firstAccountInSystem.getPassword());
            businessOwnerProfile.setXmppAccountEntity(aG01XmppAccount);
        }
        //user
        G01User firstUserInSystem = businessOwnerProfile.getUserProfile().getUserEntity();
        firstUserInSystem.setUserUuid(firstAccountInSystem.getAccountUuid());
        //location
        List<RoseLocationProfile> aRoseLocationProfileList = businessOwnerProfile.getUserProfile().getUserLocationProfileList();
        for (RoseLocationProfile aRoseLocationProfile : aRoseLocationProfileList){
            aRoseLocationProfile.getLocationEntity().setEntityType(GardenEntityType.USER.name());
            aRoseLocationProfile.getLocationEntity().setEntityUuid(firstUserInSystem.getUserUuid());
        }
        //contact-info
        List<RoseContactInfoProfile> aRoseContactInfoProfileList = businessOwnerProfile.getUserProfile().getUserContactInfoProfileList();
        for (RoseContactInfoProfile aRoseContactInfoProfile : aRoseContactInfoProfileList){
            aRoseContactInfoProfile.getContactInfoEntity().setEntityType(GardenEntityType.USER.name());
            aRoseContactInfoProfile.getContactInfoEntity().setEntityUuid(firstUserInSystem.getUserUuid());
        }
        //authorize all the privileges to the business owner
        businessOwnerProfile.unauthorizeAllPrivileges();
        G01AuthorizedPrivilege aG01AuthorizedPrivilege = new G01AuthorizedPrivilege();
        G01AuthorizedPrivilegePK pkid = new G01AuthorizedPrivilegePK();
        pkid.setAuthorizedEntityUuid(firstAccountInSystem.getAccountUuid());
        pkid.setAuthorizedPrivilegeUuid(GardenPrivilege.convertTypeToParamValue(GardenPrivilege.SUPER_POWER));
        aG01AuthorizedPrivilege.setG01AuthorizedPrivilegePK(pkid);
        aG01AuthorizedPrivilege.setAuthorizedEntityType(GardenEntityType.ACCOUNT.name());
        aG01AuthorizedPrivilege.setMemo("Brand-new rose set up");
        aG01AuthorizedPrivilege.setTimestamp(new Date());
        businessOwnerProfile.authorizePrivilege(aG01AuthorizedPrivilege);
        //employee
        G01Employee firstEmployee = businessOwnerProfile.getEmployeeEntity();
        firstEmployee.setEmployedDate(new Date());
        firstEmployee.setEmployeeAccountUuid(firstAccountInSystem.getAccountUuid());
        firstEmployee.setEmploymentStatus(GardenEmploymentStatus.FULL_TIME_EMPLOYEE.value());
        firstEmployee.setMemo(firstUserInSystem.getMemo());
        firstEmployee.setWorkEmail(firstAccountInSystem.getAccountEmail());
        firstEmployee.setWorkPhone(firstAccountInSystem.getMobilePhone());
        firstEmployee.setWorkTitle(GardenWorkTitle.BUSINESS_OWNER.value());
        
        //security questions
        businessOwnerProfile.getSecurityQnaProfile01().getSecurityQnaEntity().getG01SecurityQnaPK().setAccountUuid(firstAccountInSystem.getAccountUuid());
        businessOwnerProfile.getSecurityQnaProfile02().getSecurityQnaEntity().getG01SecurityQnaPK().setAccountUuid(firstAccountInSystem.getAccountUuid());
        businessOwnerProfile.getSecurityQnaProfile03().getSecurityQnaEntity().getG01SecurityQnaPK().setAccountUuid(firstAccountInSystem.getAccountUuid());
    }

    public double getSearchSimilarityThreshhold() {
        return 0.75;
    }
}
