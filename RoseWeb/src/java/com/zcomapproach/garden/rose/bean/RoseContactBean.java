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

import com.zcomapproach.garden.email.HostMonsterEmailSettings;
import com.zcomapproach.garden.email.rose.RoseEmailMessage;
import com.zcomapproach.garden.email.rose.RoseEmailUtils;
import com.zcomapproach.garden.exception.NoAttachmentException;
import com.zcomapproach.garden.persistence.constant.GardenContactType;
import com.zcomapproach.garden.rose.data.profile.*;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G01Account;
import com.zcomapproach.garden.rose.RosePageName;
import com.zcomapproach.garden.rose.data.constant.CustomerContactMethod;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.garden.rose.util.RoseWebUtils;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import com.zcomapproach.commons.nio.ZcaNio;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author zhijun98
 */
@Named(value = "roseContactBean")
@ViewScoped
public class RoseContactBean extends RoseDocumentEmailBean {
    
    private List<RoseUserProfile> availableContactorProfileList;
    
    private String requestedBillUuid;
    
    private String targetSelectedContactMethod;
    private List<RoseUserProfile> targetSelectedContactorProfiles;
    private String targetMessageForSMS;
    private boolean displayMessageForSMS;
    private String targetContactSubject;
    private String targetMessageForEmail;
    private boolean displayMessageForEmail;
    
    @Inject
    private SearchTaxpayerBean searchTaxpayerBean;
    
    @Inject
    private SearchUserBean searchUserBean;
    
    @Inject
    private SearchTaxcorpBean searchTaxcorpBean;
    
    private BusinessCaseProfile targetBusinessCaseProfile;

    public RoseContactBean() {
        this.targetSelectedContactorProfiles = new ArrayList<>();
        this.availableContactorProfileList = new ArrayList<>();
    }

    public String getRequestedBillUuid() {
        return requestedBillUuid;
    }

    public BusinessCaseProfile getTargetBusinessCaseProfile() {
        if (targetBusinessCaseProfile == null){
            targetBusinessCaseProfile = new BusinessCaseProfile();
        }
        return targetBusinessCaseProfile;
    }

    public void setTargetBusinessCaseProfile(BusinessCaseProfile targetBusinessCaseProfile) {
        this.targetBusinessCaseProfile = targetBusinessCaseProfile;
    }

    public void setRequestedBillUuid(String requestedBillUuid) {
        if (ZcaValidator.isNotNullEmpty(requestedBillUuid)){
            BusinessCaseBillProfile aBillProfile = getBusinessEJB().findBusinessCaseBillProfileByBillUuid(requestedBillUuid);
            if (aBillProfile != null){
                GardenEntityType aGardenEntityType = GardenEntityType.convertEnumNameToType(aBillProfile.getBillEntity().getEntityType());
                switch(aGardenEntityType){
                    case TAXPAYER_CASE:
                        targetBusinessCaseProfile = getTaxpayerEJB().findTaxpayerCaseProfileByTaxpayerCaseUuid(aBillProfile.getBillEntity().getEntityUuid());
                        break;
                    case TAXCORP_CASE:
                        targetBusinessCaseProfile = getTaxcorpEJB().findTaxcorpCaseProfileByTaxcorpCaseUuid(aBillProfile.getBillEntity().getEntityUuid());
                        break;
                }
                if (targetBusinessCaseProfile != null){
                    
                    displayMessageForSMS = false;
                    targetMessageForSMS = null;
                    
                    targetSelectedContactMethod = CustomerContactMethod.EMAIL_ONLY.value();
                    
                    displayMessageForEmail = true;
//                    targetContactSubject = RoseText.getText("InvoiceTo").toUpperCase() + ": " + targetBusinessCaseProfile.getProfileName()
//                            + " - " + RoseText.getText("DateTime").toUpperCase() + ": " + aBillProfile.getBillDatetimeForWeb();
                    targetContactSubject = RoseText.getText("Invoice").toUpperCase() + " - " 
                            + RoseText.getText("DateTime").toUpperCase() + ": " + aBillProfile.getBillDatetimeForWeb();
                    
                    //invoice email content
                    String line = "-------------------------------------------------------------";
                    targetMessageForEmail = RoseText.getText("Hi") + ", " 
                            + targetBusinessCaseProfile.getCustomerProfile().getUserProfile().getProfileName() + ":"
                            + ZcaNio.lineSeparator() + ZcaNio.lineSeparator()
                            + RoseText.getText("Invoice_Email_T")
                            + ZcaNio.lineSeparator() + line + ZcaNio.lineSeparator()
                            + RoseText.getText("InvoiceTo").toUpperCase() + ": " + targetBusinessCaseProfile.getProfileName()
                            + ZcaNio.lineSeparator()
                            + RoseText.getText("DateTime").toUpperCase() + ": " + aBillProfile.getBillDatetimeForWeb()
                            + ZcaNio.lineSeparator() + line + ZcaNio.lineSeparator()
                            + RoseText.getText("Content").toUpperCase()
                            + ZcaNio.lineSeparator() + line + ZcaNio.lineSeparator()
                            + aBillProfile.getBillEntity().getBillContent()
//                            + ZcaNio.lineSeparator() + line + ZcaNio.lineSeparator()
//                            + RoseText.getText("Paid").toUpperCase()
                            + ZcaNio.lineSeparator() + line + ZcaNio.lineSeparator();
                    //payment list
                    Locale locale = new Locale("en", "US");      
                    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
//                    List<BusinessCasePaymentProfile> aBusinessCasePaymentProfileList = aBillProfile.getBusinessCasePaymentProfileList();
//                    if (aBusinessCasePaymentProfileList.isEmpty()){
//                        targetMessageForEmail += RoseText.getText("NoPaymentConfirmed");
//                        targetMessageForEmail += ZcaNio.lineSeparator() + line + ZcaNio.lineSeparator();
//                    }else{
//                        for (BusinessCasePaymentProfile aBusinessCasePaymentProfile : aBusinessCasePaymentProfileList){
//                            targetMessageForEmail += RoseText.getText("PaymentMethod") + ": " 
//                                    + aBusinessCasePaymentProfile.getPaymentEntity().getPaymentType() + ZcaNio.lineSeparator();
//                            targetMessageForEmail += RoseText.getText("Paid") + ": " 
//                                    + currencyFormatter.format(aBusinessCasePaymentProfile.getPaymentEntity().getPaymentPrice().doubleValue()) 
//                                    + ZcaNio.lineSeparator();
//                            targetMessageForEmail += RoseText.getText("PaymentDate") + ": " 
//                                    + ZcaCalendar.convertToMMddyyyy(aBusinessCasePaymentProfile.getPaymentEntity().getPaymentDate(), "-") 
//                                    + ZcaNio.lineSeparator();
////                            targetMessageForEmail += RoseText.getText("ConfirmedByAgent") + ": " 
////                                    + aBusinessCasePaymentProfile.getAgent().getProfileDescriptiveName() + ZcaNio.lineSeparator();
//                            if (ZcaValidator.isNotNullEmpty(aBusinessCasePaymentProfile.getPaymentEntity().getPaymentMemo())){
//                                targetMessageForEmail += RoseText.getText("Memo") + ": " 
//                                        + aBusinessCasePaymentProfile.getPaymentEntity().getPaymentMemo() + ZcaNio.lineSeparator();
//                            }
//                            targetMessageForEmail += line + ZcaNio.lineSeparator();
//                        }
//                    }
                    targetMessageForEmail += RoseText.getText("Summary").toUpperCase();
                    targetMessageForEmail += ZcaNio.lineSeparator() + line + ZcaNio.lineSeparator();
                    targetMessageForEmail += RoseText.getText("Price") + ": " 
                            + currencyFormatter.format(aBillProfile.getBillTotalForWeb()) + ZcaNio.lineSeparator();
                    targetMessageForEmail += RoseText.getText("Discount") + ": " 
                            + aBillProfile.getBillDiscountForWeb() + ZcaNio.lineSeparator();
                    targetMessageForEmail += RoseText.getText("DiscountedPrice") + ": " 
                            + currencyFormatter.format(aBillProfile.getBillFinalTotalForWeb()) + ZcaNio.lineSeparator();
                    targetMessageForEmail += RoseText.getText("CustomerPaid") + ": " 
                            + currencyFormatter.format(aBillProfile.getTotalPaidPriceValueForWeb()) + ZcaNio.lineSeparator();
                    targetMessageForEmail += RoseText.getText("Balance") + ": " 
                            + currencyFormatter.format(aBillProfile.getFinalBalanceValueForWeb()) + ZcaNio.lineSeparator();
                    targetMessageForEmail += line + ZcaNio.lineSeparator();
                    targetMessageForEmail += RoseText.getText("BalanceDue").toUpperCase() + ": " 
                            + currencyFormatter.format(aBillProfile.getFinalBalanceValueForWeb()) + ZcaNio.lineSeparator();
                    targetMessageForEmail += line + ZcaNio.lineSeparator() + ZcaNio.lineSeparator();
                    targetMessageForEmail += RoseText.getText("Notice") + " - " + RoseText.getText("PaymentMethod") + ZcaNio.lineSeparator();
                    targetMessageForEmail += line + ZcaNio.lineSeparator();
                    targetMessageForEmail += "1. You can CHASE-QUICK-PAY to us. Our CHASE-QUICK-PAY account is: YIN6688LU@GMAIL.COM" + ZcaNio.lineSeparator();
                    targetMessageForEmail += "2. You can write a check to: YIN LU CPA P.C. and email us the photo copies of the front/back of your check";
                    targetMessageForEmail += ZcaNio.lineSeparator() + ZcaNio.lineSeparator();
                    targetMessageForEmail += RoseText.getText("ThankYou_T") + ZcaNio.lineSeparator();
                    targetMessageForEmail += ZcaNio.lineSeparator() + ZcaNio.lineSeparator();
                    targetMessageForEmail += getRoseSettings().getWebOwnerName() + ZcaNio.lineSeparator();
                    targetMessageForEmail += getRoseSettings().getWebOwnerAddress() + ZcaNio.lineSeparator();
                    targetMessageForEmail += getRoseSettings().getWebOwnerPhone() + ZcaNio.lineSeparator();
                    if (ZcaValidator.isNotNullEmpty(getRoseSettings().getWebOwnerFax())){
                        targetMessageForEmail += getRoseSettings().getWebOwnerFax() + ZcaNio.lineSeparator();
                    }
                    targetMessageForEmail += ZcaCalendar.convertToMMddyyyy(new Date(), "-") + ZcaNio.lineSeparator();
                    
                }
            }
        }
        this.requestedBillUuid = requestedBillUuid;
    }

    protected SearchTaxcorpBean getSearchTaxcorpBean() {
        return searchTaxcorpBean;
    }

    @Override
    public String getRosePageTopic() {
        return RoseText.getText("Contact") + ": " + RoseText.getText("EmailAndSMS");
    }

    @Override
    public String getTopicIconAwesomeName() {
        return "paper-plane";
    }

    @Override
    public String getTargetReturnWebPath() {
        String targetWebPath = "/" + RoseWebUtils.BUSINESS_FOLDER + "/";
        HashMap<String, String> params = new HashMap<>();
        switch(getEntityType()){
            case ACCOUNT:
                params.put(getRoseParamKeys().getCustomerUuidParamKey(), getRequestedEntityUuid());
                targetWebPath += RosePageName.ClientProfilePage.name() + RoseWebUtils.constructWebQueryString(params, true);
                break;
            case USER:
                params.put(getRoseParamKeys().getUserUuidParamKey(), getRequestedEntityUuid());
                targetWebPath += RosePageName.UserProfilePage.name() + RoseWebUtils.constructWebQueryString(params, true);
                break;
            case TAXPAYER_CASE:
                params.put(getRoseParamKeys().getTaxpayerCaseUuidParamKey(), getRequestedEntityUuid());
                targetWebPath += RosePageName.TaxpayerCaseMgtPage.name() + RoseWebUtils.constructWebQueryString(params, true);
                break;
            case TAXCORP_CASE:
                params.put(getRoseParamKeys().getTaxcorpCaseUuidParamKey(), getRequestedEntityUuid());
                targetWebPath += RosePageName.TaxcorpCaseMgtPage.name() + RoseWebUtils.constructWebQueryString(params, true);
                break;
            case SEARCH_TAXPAYER_CASE:
                targetWebPath += RosePageName.SearchTaxpayerResultPage.name() + RoseWebUtils.constructWebQueryString(null, true);
                break;
            case SEARCH_USER_PROFILE:
                targetWebPath += RosePageName.SearchUserResultPage.name() + RoseWebUtils.constructWebQueryString(null, true);
                break;
            case SEARCH_TAXCORP_CASE:
                targetWebPath += RosePageName.SearchTaxcorpResultPage.name() + RoseWebUtils.constructWebQueryString(null, true);
                break;
            default:
                //do nothing
        }
        return targetWebPath;
    }
    
    public String cancelWebPage(){
        return getTargetReturnWebPath();
    }

    @Override
    public void setRequestedEntityType(String requestedEntityType) {
        super.setRequestedEntityType(requestedEntityType);
        //initialize availableContactorProfileList
        availableContactorProfileList.clear();
        switch(getEntityType()){
            case SEARCH_TAXPAYER_CASE:
                intializeAvailableContactorProfileListForSearchTaxpayerCases(); 
                break;
            case SEARCH_USER_PROFILE:
                intializeAvailableContactorProfileListForSearchUserProfiles(); 
                break;
            case SEARCH_TAXCORP_CASE:
                intializeAvailableContactorProfileListForSearchTaxcorpCases(); 
                break;
            default:
                //do nothing
        }
        //checkContactorProfile each item in availableContactorProfileList
        checkContactorProfile(availableContactorProfileList);
    }

    @Override
    public void setRequestedEntityUuid(String requestedEntityUuid) {
        if (ZcaValidator.isNotNullEmpty(requestedEntityUuid)){
            //initialize availableContactorProfileList
            availableContactorProfileList.clear();
            switch(getEntityType()){
                case ACCOUNT:
                case USER:
                    intializeAvailableContactorProfileListForPerson(requestedEntityUuid); 
                    break;
                case TAXPAYER_CASE:
                    intializeAvailableContactorProfileListForTaxpayer(requestedEntityUuid);
                    break;
                case TAXCORP_CASE:
                    intializeAvailableContactorProfileListForTaxcorp(requestedEntityUuid);
                    break;
                default:
                    //do nothing
            }
            //checkContactorProfile each item in availableContactorProfileList
            checkContactorProfile(availableContactorProfileList);
        }
        super.setRequestedEntityUuid(requestedEntityUuid);
    }
    
    private void intializeAvailableContactorProfileListForTaxcorp(String requestEntityUuid){
        TaxcorpCaseProfile aTaxcorpCaseProfile = getTaxcorpEJB().findTaxcorpCaseProfileByTaxcorpCaseUuid(requestEntityUuid);
        if (aTaxcorpCaseProfile != null){
            getAvailableContactorProfileList().addAll(aTaxcorpCaseProfile.getTaxcorpRepresentativeProfileList());
        }
    }
    
    private void intializeAvailableContactorProfileListForSearchTaxcorpCases(){
        getAvailableContactorProfileList().addAll(getTaxcorpEJB().findTaxcorpRepresentativeProfileListByTaxcorpEntityList(searchTaxcorpBean.getSearchResultTaxcorpEntityList()));
    }
    
    private void intializeAvailableContactorProfileListForPerson(String requestEntityUuid){
        availableContactorProfileList.add(getBusinessEJB().findRoseUserProfileByUserUuid(requestEntityUuid));
    }
    
    private void intializeAvailableContactorProfileListForTaxpayer(String requestEntityUuid){
        intializeAvailableContactorProfileListForTaxpayerHelper(getTaxpayerEJB().findTaxpayerCaseProfileByTaxpayerCaseUuid(requestEntityUuid));
    }
    
    private void intializeAvailableContactorProfileListForTaxpayerHelper(TaxpayerCaseProfile aTaxpayerCaseProfile){
        if (aTaxpayerCaseProfile != null){
            availableContactorProfileList.add(aTaxpayerCaseProfile.getCustomerProfile().getUserProfile());
            List<TaxpayerInfoProfile> aTaxpayerInfoProfileList = aTaxpayerCaseProfile.getDependantProfileList();
            for (TaxpayerInfoProfile aTaxpayerInfoProfile : aTaxpayerInfoProfileList){
                availableContactorProfileList.add(aTaxpayerInfoProfile.getRoseUserProfile());
            }
        }
    }
    
    private void intializeAvailableContactorProfileListForSearchTaxpayerCases(){
        List<TaxpayerCaseConciseProfile> aTaxpayerCaseSearchResultProfileList = searchTaxpayerBean.getSearchResultTaxpayerProfileList();
        for (TaxpayerCaseConciseProfile aTaxpayerCaseProfile : aTaxpayerCaseSearchResultProfileList){
            intializeAvailableContactorProfileListForTaxpayer(aTaxpayerCaseProfile.getTaxpayerCase().getTaxpayerCaseUuid());
        }
    }
    
    private void intializeAvailableContactorProfileListForSearchUserProfiles(){
        availableContactorProfileList.addAll(getBusinessEJB().findRoseUserProfileByUserEntities(searchUserBean.getSearchResultUserEntityList()));
    }

    /**
     * If a rose-user profile has no any contact info after remedy its contact info from account, it will be removed from the list
     * @param aRoseUserProfileList 
     */
    protected void checkContactorProfile(List<RoseUserProfile> aRoseUserProfileList) {
        List<RoseUserProfile> badRoseUserProfileList = new ArrayList<>();
        for (RoseUserProfile aRoseUserProfile : aRoseUserProfileList){
            if (!checkContactorProfileHelper(aRoseUserProfile)){
                badRoseUserProfileList.add(aRoseUserProfile);
            }
        }
        for (RoseUserProfile badRoseUserProfile : badRoseUserProfileList){
            aRoseUserProfileList.remove(badRoseUserProfile);
        }
    }

    private boolean checkContactorProfileHelper(RoseUserProfile aRoseUserProfile) {
        boolean result = false;
        List<RoseContactInfoProfile> aRoseContactInfoProfileList = aRoseUserProfile.getUserContactInfoProfileList();
        G01Account aG01Account = getBusinessEJB().findEntityByUuid(G01Account.class, aRoseUserProfile.getUserEntity().getUserUuid());
        for (RoseContactInfoProfile aRoseContactInfoProfile : aRoseContactInfoProfileList){
             if (GardenContactType.EMAIL.value().equalsIgnoreCase(aRoseContactInfoProfile.getContactInfoEntity().getContactType())){
                 result = true;
             }
             if (GardenContactType.MOBILE_PHONE.value().equalsIgnoreCase(aRoseContactInfoProfile.getContactInfoEntity().getContactType())){
                 result = true;
             }
        }
        if (aG01Account != null){
            aRoseUserProfile.setEmail(aG01Account.getAccountEmail());
            result = true;
        }
        if ((aG01Account != null) && (ZcaValidator.isNotNullEmpty(aG01Account.getMobilePhone()))){
            aRoseUserProfile.setMobilePhone(aG01Account.getMobilePhone());
            result = true;
        }
        return result;
    }
    
    public String sendEmailAndSmsToContactor(){
        sendEmailAndSmsToContactorHelper();
        return null;
    }
    
    protected boolean sendEmailAndSmsToContactorHelper(){
        if (ZcaValidator.isNullEmpty(getTargetMessageForSMS()) 
                && ZcaValidator.isNullEmpty(getTargetMessageForEmail()))
        {
            RoseJsfUtils.setGlobalErrorFacesMessage("No any message for email and/or SMS.");
            return false;
        }
        try {
            if (CustomerContactMethod.EMAIL_AND_PHONE_MESSAGING.value().equalsIgnoreCase(getTargetSelectedContactMethod())){
                sendTargetMessageForEmail();
                sendTargetMessageForSMS();
                RoseJsfUtils.setGlobalSuccessfulOperationMessage();
                return true;
            }else if (CustomerContactMethod.EMAIL_ONLY.value().equalsIgnoreCase(getTargetSelectedContactMethod())){
                sendTargetMessageForEmail();
                RoseJsfUtils.setGlobalSuccessfulOperationMessage();
                return true;
            }else if (CustomerContactMethod.PHONE_MESSAGING_ONLY.value().equalsIgnoreCase(getTargetSelectedContactMethod())){
                sendTargetMessageForSMS();
                RoseJsfUtils.setGlobalSuccessfulOperationMessage();
                return true;
            }else{
                RoseJsfUtils.setGlobalErrorFacesMessage("No valid contact method");
            }
        } catch (Exception ex) {
            Logger.getLogger(RoseContactBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalErrorFacesMessage(ex.getMessage());
        }
        return false;
    }

    private void sendTargetMessageForEmail() throws Exception{
        ArrayList<String> emailToList = new ArrayList<>();
        String emailTo;
        List<RoseUserProfile> aTargetSelectedContactorProfiles = getTargetSelectedContactorProfiles();
        for (RoseUserProfile aRoseUserProfile : aTargetSelectedContactorProfiles){
            emailTo = aRoseUserProfile.getEmail();
            if (ZcaValidator.isNotNullEmpty(emailTo)){
                emailToList.add(emailTo);
            }
        }
        
        List<String> replyToList = new ArrayList<>();
        String replyToListString = "";
        replyToList.add(HostMonsterEmailSettings.ServiceEmail.value());
        replyToListString += HostMonsterEmailSettings.ServiceEmail.value();
        if (!HostMonsterEmailSettings.ServiceEmail.value().equalsIgnoreCase(getTargetEmailFrom())){
            replyToList.add(getTargetEmailFrom());
            replyToListString += ";"+getTargetEmailFrom();
        }
        String sentAttachmentFileName = null;
        try {
            UploadedFile uploadedFile = getUploadedFile();
            if ((uploadedFile != null) && validateUploadedFile(getUploadedFile())){
                String fileLocation = getRoseSettings().getArchivedFileLocation();
                try{
                    sentAttachmentFileName = uploadedFile.getFileName();
                    //save the file onto the disk
                    File savedFile = saveUploadedFileOnServerSide(GardenData.generateUUIDString(), 
                            FilenameUtils.getExtension(sentAttachmentFileName), fileLocation);
                    getRuntimeEJB().sendEncryptedEmailInBatchWithAttachment(HostMonsterEmailSettings.ServiceEmail.value(), 
                                                                            emailToList, replyToList, getTargetContactSubject(), 
                                                                            getTargetMessageForEmail(), 
                                                                            savedFile, true, getRoseSettings().isEmailDisabled());
                }catch (Exception ex){
                    if (ex instanceof NoAttachmentException){
                        getRuntimeEJB().sendEncryptedEmailInBatch(HostMonsterEmailSettings.ServiceEmail.value(), 
                                                                    emailToList, replyToList, getTargetContactSubject(), 
                                                                    getTargetMessageForEmail(), 
                                                                    getRoseSettings().isEmailDisabled());
                    }else{
                        throw ex;
                    }
                }
            }else{
                getRuntimeEJB().sendEncryptedEmailInBatch(HostMonsterEmailSettings.ServiceEmail.value(), 
                                                            emailToList, replyToList, getTargetContactSubject(), 
                                                            getTargetMessageForEmail(), 
                                                            getRoseSettings().isEmailDisabled());
            }
            
            /**
             * Serialize this sent-message
             */
            RoseEmailMessage aRoseEmailMessage = new RoseEmailMessage();
            //aRoseEmailMessage.setBccList(bccList);
            //aRoseEmailMessage.setCcList(ccList);
            aRoseEmailMessage.setContentLoaded(true);
            aRoseEmailMessage.setFrom(getTargetEmailFrom());
            aRoseEmailMessage.setFromPerson(getTargetEmailFrom());
            aRoseEmailMessage.setHtmlContent(getTargetMessageForEmail());
            aRoseEmailMessage.setImap4Message(true);
            //aRoseEmailMessage.setMailFolderName(GardenEmailFolderName.GARDEN_SENT.value());
            //aRoseEmailMessage.setMessageNumber(0);
            //aRoseEmailMessage.setMsgUid(0);
            //aRoseEmailMessage.setPlainContent(targetContent);
            aRoseEmailMessage.setReceivedDate(null);
            aRoseEmailMessage.setReplyTo(replyToListString);
            aRoseEmailMessage.setSentDate(new Date());
            aRoseEmailMessage.setSubject(getTargetContactSubject());
            try {
                aRoseEmailMessage.setToList(RoseEmailUtils.convertToEmailAddressList(emailToList));
            } catch (Exception ex) {
                Logger.getLogger(RoseDocumentEmailBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            aRoseEmailMessage.setSentAttachmentFileName(sentAttachmentFileName);
            aRoseEmailMessage.setUnread(false);
            getRoseEmailController().serializeSentEmailMessage(aRoseEmailMessage);
            
        } catch (Exception ex) {
            Logger.getLogger(RoseDocumentEmailBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalErrorFacesMessage(ex.getMessage());
        }
    }

    private void sendTargetMessageForSMS() throws Exception{
        if (getTargetMessageForSMS().length() > 160){
            RoseJsfUtils.setGlobalErrorFacesMessage("Operation canceled: SMS may have max 160 characters.");
            return;
        }
        ArrayList<String> phoneNumberList = new ArrayList<>();
        String phoneNumber;
        List<RoseUserProfile> aTargetSelectedContactorProfiles = getTargetSelectedContactorProfiles();
        for (RoseUserProfile aRoseUserProfile : aTargetSelectedContactorProfiles){
            phoneNumber = aRoseUserProfile.getMobilePhone();
            if (ZcaValidator.isNotNullEmpty(phoneNumber)){
                phoneNumberList.add("+" + phoneNumber);
            }
        }
        getRuntimeEJB().sendSmsText(phoneNumberList, getTargetMessageForSMS(), getRoseSettings().isSmsDisabled());
    }
    
    public void handleCheckAll(){
        getTargetSelectedContactorProfiles().clear();
        getTargetSelectedContactorProfiles().addAll(new ArrayList<>(availableContactorProfileList));
    }
    
    public void handleUncheckAll(){
        getTargetSelectedContactorProfiles().clear();
    }
    
    public void handleContactMethodChangeEvent(){
        if (CustomerContactMethod.EMAIL_AND_PHONE_MESSAGING.value().equalsIgnoreCase(targetSelectedContactMethod)){
            displayMessageForSMS = true;
            displayMessageForEmail = true;
        }else if (CustomerContactMethod.EMAIL_ONLY.value().equalsIgnoreCase(targetSelectedContactMethod)){
            displayMessageForSMS = false;
            displayMessageForEmail = true;
        }else if (CustomerContactMethod.PHONE_MESSAGING_ONLY.value().equalsIgnoreCase(targetSelectedContactMethod)){
            displayMessageForSMS = true;
            displayMessageForEmail = false;
        }else{
            displayMessageForSMS = false;
            displayMessageForEmail = false;
        }
    }
    
    protected Integer getEmailOrSmsIntegerValueForPersistency(){
        if (CustomerContactMethod.EMAIL_AND_PHONE_MESSAGING.value().equalsIgnoreCase(targetSelectedContactMethod)){
            return 2;
        }else if (CustomerContactMethod.EMAIL_ONLY.value().equalsIgnoreCase(targetSelectedContactMethod)){
            return 0;
        }else if (CustomerContactMethod.PHONE_MESSAGING_ONLY.value().equalsIgnoreCase(targetSelectedContactMethod)){
            return 1;
        }else{
            return -1;
        }
    }
    
    public String getTargetMessageForSMS() {
        return targetMessageForSMS;
    }

    public void setTargetMessageForSMS(String targetMessageForSMS) {
        this.targetMessageForSMS = targetMessageForSMS;
    }

    public boolean isDisplayMessageForSMS() {
        if (!displayMessageForSMS){
            System.out.println("");
        }
        return displayMessageForSMS;
    }

    public void setDisplayMessageForSMS(boolean displayMessageForSMS) {
        this.displayMessageForSMS = displayMessageForSMS;
    }

    public String getTargetContactSubject() {
        if (ZcaValidator.isNullEmpty(targetContactSubject)){
            targetContactSubject = "Notification from Yin-Lu CPA";
        }
        return targetContactSubject;
    }

    public void setTargetContactSubject(String targetContactSubject) {
        this.targetContactSubject = targetContactSubject;
    }

    public String getTargetMessageForEmail() {
        return targetMessageForEmail;
    }

    public void setTargetMessageForEmail(String targetMessageForEmail) {
        this.targetMessageForEmail = targetMessageForEmail;
    }

    public boolean isDisplayMessageForEmail() {
        if (!displayMessageForEmail){
            System.out.println("");
        }
        return displayMessageForEmail;
    }

    public void setDisplayMessageForEmail(boolean displayMessageForEmail) {
        this.displayMessageForEmail = displayMessageForEmail;
    }

    public String getTargetSelectedContactMethod() {
        return targetSelectedContactMethod;
    }

    public void setTargetSelectedContactMethod(String targetSelectedContactMethod) {
        this.targetSelectedContactMethod = targetSelectedContactMethod;
    }

    public List<RoseUserProfile> getAvailableContactorProfileList() {
        if (availableContactorProfileList == null){
            this.availableContactorProfileList = new ArrayList<>();
        }
        return availableContactorProfileList;
    }

    public void setAvailableContactorProfileList(List<RoseUserProfile> availableContactorProfileList) {
        this.availableContactorProfileList = availableContactorProfileList;
    }

    public void setEntityType(GardenEntityType entityType) {
        setRequestedEntityType(entityType.value());
    }

    public List<RoseUserProfile> getTargetSelectedContactorProfiles() {
        if (targetSelectedContactorProfiles == null){
            this.targetSelectedContactorProfiles = new ArrayList<>();
        }
        return targetSelectedContactorProfiles;
    }

    public void setTargetSelectedContactorProfiles(List<RoseUserProfile> targetSelectedContactorProfiles) {
        this.targetSelectedContactorProfiles = targetSelectedContactorProfiles;
    }

}
