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

package com.zcomapproach.garden.rose.onlineupdate;

////////import com.zcomapproach.garden.data.GardenFlower;
////////import com.zcomapproach.garden.data.GardenSettingsProperty;
////////import com.zcomapproach.garden.data.constant.GardenAgreement;
////////import com.zcomapproach.garden.exception.EntityValidationException;
////////import com.zcomapproach.garden.exception.NonUniqueEntityException;
////////import com.zcomapproach.garden.persistence.GardenJpaUtils;
////////import com.zcomapproach.garden.persistence.constant.GardenContactType;
////////import com.zcomapproach.garden.persistence.constant.GardenDiscountType;
////////import com.zcomapproach.garden.persistence.constant.GardenEmploymentStatus;
////////import com.zcomapproach.garden.persistence.constant.GardenEntityStatus;
////////import com.zcomapproach.garden.persistence.constant.GardenEntityType;
////////import com.zcomapproach.garden.persistence.constant.GardenPreference;
////////import com.zcomapproach.garden.persistence.constant.GardenWorkTitle;
////////import com.zcomapproach.garden.persistence.constant.TaxcorpContactorRole;
////////import com.zcomapproach.garden.persistence.constant.TaxpayerFederalFilingStatus;
////////import com.zcomapproach.garden.persistence.constant.TaxpayerRelationship;
////////import com.zcomapproach.garden.persistence.entity.G01Account;
////////import com.zcomapproach.garden.persistence.entity.G01ArchivedDocument;
////////import com.zcomapproach.garden.persistence.entity.G01Bill;
////////import com.zcomapproach.garden.persistence.entity.G01ContactInfo;
////////import com.zcomapproach.garden.persistence.entity.G01DocumentRequirement;
////////import com.zcomapproach.garden.persistence.entity.G01Employee;
////////import com.zcomapproach.garden.persistence.entity.G01Location;
////////import com.zcomapproach.garden.persistence.entity.G01Log;
////////import com.zcomapproach.garden.persistence.entity.G01Payment;
////////import com.zcomapproach.garden.persistence.entity.G01PersonalBusinessProperty;
////////import com.zcomapproach.garden.persistence.entity.G01PersonalProperty;
////////import com.zcomapproach.garden.persistence.entity.G01PostSection;
////////import com.zcomapproach.garden.persistence.entity.G01ServiceTag;
////////import com.zcomapproach.garden.persistence.entity.G01SystemProperty;
////////import com.zcomapproach.garden.persistence.entity.G01SystemPropertyPK;
////////import com.zcomapproach.garden.persistence.entity.G01TaxFiling;
////////import com.zcomapproach.garden.persistence.entity.G01TaxFilingType;
////////import com.zcomapproach.garden.persistence.entity.G01TaxFilingTypePK;
////////import com.zcomapproach.garden.persistence.entity.G01TaxcorpCase;
////////import com.zcomapproach.garden.persistence.entity.G01TaxcorpRepresentative;
////////import com.zcomapproach.garden.persistence.entity.G01TaxcorpRepresentativePK;
////////import com.zcomapproach.garden.persistence.entity.G01TaxpayerCase;
////////import com.zcomapproach.garden.persistence.entity.G01TaxpayerInfo;
////////import com.zcomapproach.garden.persistence.entity.G01TlcLicense;
////////import com.zcomapproach.garden.persistence.entity.G01User;
////////import com.zcomapproach.garden.persistence.entity.G01WebPost;
////////import com.zcomapproach.garden.persistence.entity.G01WorkTeam;
////////import com.zcomapproach.garden.persistence.entity.G01WorkTeamHasEmployee;
////////import com.zcomapproach.garden.persistence.entity.G01WorkTeamHasEmployeePK;
////////import com.zcomapproach.garden.persistence.entity.GardenAccount;
////////import com.zcomapproach.garden.persistence.entity.GardenArchivedDocument;
////////import com.zcomapproach.garden.persistence.entity.GardenBusinessProperty;
////////import com.zcomapproach.garden.persistence.entity.GardenDocumentRequirement;
////////import com.zcomapproach.garden.persistence.entity.GardenEmployee;
////////import com.zcomapproach.garden.persistence.entity.GardenOnlineRequest;
////////import com.zcomapproach.garden.persistence.entity.GardenOwner;
////////import com.zcomapproach.garden.persistence.entity.GardenPayment;
////////import com.zcomapproach.garden.persistence.entity.GardenPerson;
////////import com.zcomapproach.garden.persistence.entity.GardenPersonalProperty;
////////import com.zcomapproach.garden.persistence.entity.GardenPersonalTaxCase;
////////import com.zcomapproach.garden.persistence.entity.GardenPostSection;
////////import com.zcomapproach.garden.persistence.entity.GardenServiceTag;
////////import com.zcomapproach.garden.persistence.entity.GardenTaxation;
////////import com.zcomapproach.garden.persistence.entity.GardenTaxationExtension;
////////import com.zcomapproach.garden.persistence.entity.GardenTaxcorp;
////////import com.zcomapproach.garden.persistence.entity.GardenTaxcorpPerson;
////////import com.zcomapproach.garden.persistence.entity.GardenTaxpayerInfo;
////////import com.zcomapproach.garden.persistence.entity.GardenTeam;
////////import com.zcomapproach.garden.persistence.entity.GardenTeamHasEmployee;
////////import com.zcomapproach.garden.persistence.entity.GardenTlcLicense;
////////import com.zcomapproach.garden.persistence.entity.GardenWebPost;
////////import com.zcomapproach.garden.persistence.entity.GardenWorkStatus;
////////import com.zcomapproach.garden.persistence.updater.GardenDataUpdaterFactory;
import com.zcomapproach.garden.rose.persistence.AbstractDataServiceEJB;
////////import com.zcomapproach.garden.rose.persistence.RoseRuntimeEJB;
////////import com.zcomapproach.garden.rose.validator.RoseEmailValidator;
////////import com.zcomapproach.commons.ZcaValidator;
////////import java.util.Collections;
////////import java.util.HashMap;
////////import java.util.List;
////////import java.util.logging.Level;
////////import java.util.logging.Logger;
import javax.ejb.Stateless;
////////import javax.persistence.EntityManager;
////////import javax.transaction.HeuristicMixedException;
////////import javax.transaction.HeuristicRollbackException;
////////import javax.transaction.NotSupportedException;
////////import javax.transaction.RollbackException;
////////import javax.transaction.SystemException;
////////import javax.transaction.UserTransaction;

/**
 *
 * @author zhijun98
 */
@Stateless
public class GardenDataTransfer_G01 extends AbstractDataServiceEJB {

    public void process() {
////////        Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, "Garden data transfer is launched.");
////////        process_01();
////////        process_02();
////////        process_03();
////////        Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, "Garden data transfer is completed.");
    }
    
////////    private void process_01() {
////////        EntityManager em = null;
////////        UserTransaction utx =  getUserTransaction();
////////        try {
////////            utx.begin();
////////            em = getEntityManager();
////////            
////////            transferGardenOwner(em);
////////            transferGardenPerson(em);
////////            
////////            transferGardenTeam(em);
////////            transferGardenTeamHasEmployee(em);
////////            
////////            transferGardenWebPost(em);
////////            transferGardenPostSection(em);
////////            
////////            transferGardenServiceTag(em);
////////            transferGardenArchivedDocument(em);
////////            transferGardenDocumentRequirement(em);
////////            
////////            transferGardenPersonalTaxCase(em);
////////            transferGardenBusinessProperty(em);
////////            transferGardenPersonalProperty(em);
////////            transferGardenTlcLicense(em);
////////            
////////            utx.commit();
////////            
////////        } catch (EntityValidationException | NonUniqueEntityException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
////////            Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex);
////////            try {
////////                utx.rollback();
////////            } catch (IllegalStateException | SecurityException | SystemException ex1) {
////////                Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex1);
////////            }
////////        } finally {
////////            if (em != null) {
////////                em.close();
////////            }
////////        }
////////        
////////    }
////////    
////////    private void process_02() {
////////        EntityManager em = null;
////////        UserTransaction utx =  getUserTransaction();
////////        try {
////////            utx.begin();
////////            em = getEntityManager();
////////            
////////            transferGardenAccount(em);
////////            transferGardenEmployee(em);
////////            transferGardenPayment(em);
////////            
////////            transferGardenTaxcorp(em);
////////            
////////            utx.commit();
////////            
////////        } catch (EntityValidationException | NonUniqueEntityException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
////////            Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex);
////////            try {
////////                utx.rollback();
////////            } catch (IllegalStateException | SecurityException | SystemException ex1) {
////////                Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex1);
////////            }
////////        } finally {
////////            if (em != null) {
////////                em.close();
////////            }
////////        }
////////        
////////    }
////////    
////////    private void process_03() {
////////        EntityManager em = null;
////////        UserTransaction utx =  getUserTransaction();
////////        try {
////////            utx.begin();
////////            em = getEntityManager();
////////            
////////            transferGardenTaxation(em);
////////            
////////            utx.commit();
////////            
////////        } catch (EntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
////////            Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex);
////////            try {
////////                utx.rollback();
////////            } catch (IllegalStateException | SecurityException | SystemException ex1) {
////////                Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, null, ex1);
////////            }
////////        } finally {
////////            if (em != null) {
////////                em.close();
////////            }
////////        }
////////        
////////    }
////////    
////////    private void transferGardenOwner(EntityManager em) throws EntityValidationException {
////////        Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenOwner....");
////////        List<GardenOwner> aGardenOwnerList = GardenJpaUtils.findAll(em, GardenOwner.class);
////////        GardenOwner aGardenOwner = aGardenOwnerList.get(0);
////////        transferGardenOwnerHelper(em, aGardenOwner, GardenSettingsProperty.WEB_OWNER_ADDRESS, aGardenOwner.getAddress()+", "+aGardenOwner.getCityName()+ ", "
////////                +aGardenOwner.getStateName()+" " +aGardenOwner.getZipCode()+", " + aGardenOwner.getCountry());
////////        transferGardenOwnerHelper(em, aGardenOwner, GardenSettingsProperty.WEB_OWNER_EMAIL, aGardenOwner.getEmail());
////////        transferGardenOwnerHelper(em, aGardenOwner, GardenSettingsProperty.WEB_OWNER_NAME, aGardenOwner.getOwnerName());
////////        transferGardenOwnerHelper(em, aGardenOwner, GardenSettingsProperty.WEB_OWNER_PHONE, aGardenOwner.getPhone());
////////        transferGardenOwnerHelper(em, aGardenOwner, GardenSettingsProperty.WEB_OWNER_FAX, aGardenOwner.getFax());
////////        transferGardenOwnerHelper(em, aGardenOwner, GardenSettingsProperty.WEB_PAGE_TITLE, "欢迎光临"+aGardenOwner.getOwnerName());
////////        transferGardenOwnerHelper(em, aGardenOwner, GardenSettingsProperty.WEB_SITE_BRAND, "LYCPA");
////////        
////////    }
////////    
////////    private void transferGardenOwnerHelper(EntityManager em, GardenOwner aGardenOwner, GardenSettingsProperty aGardenSettingsProperty, String value) throws EntityValidationException{
////////        Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenOwnerHelper....");
////////        G01SystemProperty aG01SystemProperty = new G01SystemProperty();
////////        G01SystemPropertyPK pkid = new G01SystemPropertyPK();
////////        pkid.setFlowerName(GardenFlower.ROSE.name());
////////        pkid.setPropertyName(aGardenSettingsProperty.value());
////////        aG01SystemProperty.setG01SystemPropertyPK(pkid);
////////        aG01SystemProperty.setCreated(aGardenOwner.getCreated());
////////        aG01SystemProperty.setDescription(aGardenOwner.getMemo());
////////        aG01SystemProperty.setPropertyValue(value);
////////        aG01SystemProperty.setUpdated(aGardenOwner.getUpdated());
////////        GardenJpaUtils.storeEntity(em, G01SystemProperty.class, aG01SystemProperty, aG01SystemProperty.getG01SystemPropertyPK(), 
////////                GardenDataUpdaterFactory.getSingleton().getG01SystemPropertyUpdater());
////////    
////////    }
////////    
////////    private void transferGardenEmployee(EntityManager em) throws EntityValidationException {
////////        Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenEmployee....");
////////        List<GardenEmployee> aGardenEmployeeList = GardenJpaUtils.findAll(em, GardenEmployee.class);
////////        G01Employee aG01Employee;
////////        G01User aG01User;
////////        for (GardenEmployee aGardenEmployee : aGardenEmployeeList){
////////            aG01Employee = new G01Employee();
////////            aG01Employee.setEmployedDate(aGardenEmployee.getEmployedDate());
////////            aG01Employee.setEmployeeAccountUuid(aGardenEmployee.getEmployeeAccountUuid());
////////            aG01Employee.setEmploymentStatus(GardenEmploymentStatus.FULL_TIME_EMPLOYEE.value());
////////            //aG01Employee.setEntityStatus(entityStatus);
////////            aG01Employee.setMemo(aGardenEmployee.getMemo());
////////            aG01Employee.setWorkEmail(aGardenEmployee.getWorkEmail());
////////            //aG01Employee.setWorkPhone(workPhone);
////////            aG01Employee.setWorkTitle(aGardenEmployee.getJobTitle());
////////            aG01Employee.setCreated(aGardenEmployee.getCreated());
////////            aG01Employee.setUpdated(aGardenEmployee.getUpdated());
////////            
////////            GardenJpaUtils.storeEntity(em, G01Employee.class, aG01Employee, aG01Employee.getEmployeeAccountUuid(), 
////////                    GardenDataUpdaterFactory.getSingleton().getG01EmployeeUpdater());
////////            
////////            aG01User = GardenJpaUtils.findById(em, G01User.class, aG01Employee.getEmployeeAccountUuid());
////////            if (aG01User != null){
////////                aG01User.setEntityStatus(GardenEntityStatus.EMPLOYEE.value());
////////                GardenJpaUtils.storeEntity(em, G01User.class, aG01User, aG01User.getUserUuid(), 
////////                        GardenDataUpdaterFactory.getSingleton().getG01UserUpdater());
////////            }
////////        }
////////    }
////////    
////////    private void transferGardenAccount(EntityManager em) throws EntityValidationException {
////////        Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenAccount....");
////////        List<GardenAccount> aGardenAccountList = GardenJpaUtils.findAll(em, GardenAccount.class);
////////        G01Account aG01Account;
////////        G01User aG01User;
////////        int total = aGardenAccountList.size();
////////        int count = 0;
////////        
////////        for (GardenAccount aGardenAccount : aGardenAccountList){
////////            count++;
////////            Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenAccount....[{0}/{1}]", new Object[]{count, total});
////////            aG01Account = new G01Account();
////////            aG01Account.setLoginName(aGardenAccount.getAccountEmail());
////////            aG01Account.setAccountConfirmationCode(aGardenAccount.getAccountStatusCode());
////////            if ((ZcaValidator.isNullEmpty(aGardenAccount.getAccountEmail()))
////////                    || (RoseEmailValidator.validateEmail(aGardenAccount.getAccountEmail()))){
////////                aG01Account.setAccountEmail(aGardenAccount.getAccountEmail());
////////            }else{
////////                aG01Account.setAccountEmail(GardenData.generateRandomSecretCode(8)+"@");
////////            }
////////            aG01Account.setAccountStatus(aGardenAccount.getAccountStatus());
////////            aG01Account.setAccountUuid(aGardenAccount.getAccountPersonUuid());
////////            aG01Account.setEncryptedPassword(aGardenAccount.getEncryptedPassword());
////////            //aG01Account.setEntityStatus(entityStatus);
////////            //aG01Account.setMobilePhone(mobilePhone);
////////            aG01Account.setPassword(aGardenAccount.getPassword());
////////            //aG01Account.setWebLanguage(webLanguage);
////////            aG01Account.setCreated(aGardenAccount.getCreated());
////////            aG01Account.setUpdated(aGardenAccount.getUpdated());
////////            
////////            GardenJpaUtils.storeEntity(em, G01Account.class, aG01Account, aG01Account.getAccountUuid(), 
////////                    GardenDataUpdaterFactory.getSingleton().getG01AccountUpdater());
////////            
////////            aG01User = GardenJpaUtils.findById(em, G01User.class, aG01Account.getAccountUuid());
////////            if (aG01User != null){
////////                if (aG01Account.getAccountEmail().length() != 36){
////////                    aG01User.setEntityStatus(GardenEntityStatus.ONLINE_CUSTOMER.value());
////////                    GardenJpaUtils.storeEntity(em, G01User.class, aG01User, aG01User.getUserUuid(), 
////////                            GardenDataUpdaterFactory.getSingleton().getG01UserUpdater());
////////                }
////////            }
////////        }
////////    }
////////    
////////    private void transferGardenPerson(EntityManager em) throws EntityValidationException {
////////        Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenPerson....");
////////        List<GardenPerson> aGardenPersonList = GardenJpaUtils.findAll(em, GardenPerson.class);
////////        G01User aG01User;
////////        G01Location aG01Location;
////////        int total = aGardenPersonList.size();
////////        int count = 0;
////////        for (GardenPerson aGardenPerson : aGardenPersonList){
////////            count++;
////////            Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenPerson....[{0}/{1}]", new Object[]{count, total});
////////            aG01User = new G01User();
////////            aG01User.setBirthday(aGardenPerson.getBirthday());
////////            aG01User.setCitizenship(aGardenPerson.getCitizenship());
////////            //aG01User.setEntityStatus(entityStatus);
////////            aG01User.setFirstName(aGardenPerson.getFirstName());
////////            //aG01User.setGender(gender);
////////            aG01User.setLastName(aGardenPerson.getLastName());
////////            aG01User.setMemo(aGardenPerson.getMemo());
////////            aG01User.setMiddleName(aGardenPerson.getMiddleName());
////////            aG01User.setOccupation(aGardenPerson.getOccupation());
////////            aG01User.setSsn(aGardenPerson.getSsn());
////////            aG01User.setUserUuid(aGardenPerson.getPersonUuid());
////////            aG01User.setCreated(aGardenPerson.getCreated());
////////            aG01User.setUpdated(aGardenPerson.getUpdated());
////////            GardenJpaUtils.storeEntity(em, G01User.class, aG01User, aG01User.getUserUuid(), 
////////                    GardenDataUpdaterFactory.getSingleton().getG01UserUpdater());
////////            
////////            transferGardenPersonHelper_ContactInfo(em, aGardenPerson, GardenContactType.EMAIL);
////////            transferGardenPersonHelper_ContactInfo(em, aGardenPerson, GardenContactType.MOBILE_PHONE);
////////            transferGardenPersonHelper_ContactInfo(em, aGardenPerson, GardenContactType.WECHAT);
////////            
////////            if (ZcaValidator.isNotNullEmpty(aGardenPerson.getAddress())){
////////                aG01Location = new G01Location();
////////                aG01Location.setCityName(aGardenPerson.getCityName());
////////                aG01Location.setCountry(aGardenPerson.getCountry());
////////                aG01Location.setCreated(aGardenPerson.getCreated());
////////                //aG01Location.setEntityStatus(entityStatus);
////////                aG01Location.setEntityType(GardenEntityType.USER.name());
////////                aG01Location.setEntityUuid(aGardenPerson.getPersonUuid());
////////                aG01Location.setLocalAddress(aGardenPerson.getAddress());
////////                aG01Location.setLocationUuid(GardenData.generateUUIDString());
////////                aG01Location.setPreferencePriority(GardenPreference.MORE_PREFERED.ordinal());
////////                //aG01Location.setShortMemo(shortMemo);
////////                aG01Location.setStateCounty(aGardenPerson.getStateCounty());
////////                aG01Location.setStateName(aGardenPerson.getStateName());
////////                aG01Location.setUpdated(aGardenPerson.getUpdated());
////////                aG01Location.setZipCode(aGardenPerson.getZipCode());
////////                GardenJpaUtils.storeEntity(em, G01Location.class, aG01Location, aG01Location.getLocationUuid(), 
////////                        GardenDataUpdaterFactory.getSingleton().getG01LocationUpdater());
////////            }
////////        }//for
////////    }
////////
////////    private void transferGardenPersonHelper_ContactInfo(EntityManager em, GardenPerson aGardenPerson, GardenContactType gardenContactType) throws EntityValidationException {
////////        //Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenPersonHelper_ContactInfo....");
////////        String contactInfo = null;
////////        switch(gardenContactType){
////////            case EMAIL:
////////                contactInfo = aGardenPerson.getEmail();
////////                break;
////////            case MOBILE_PHONE:
////////                contactInfo = aGardenPerson.getPhone();
////////                break;
////////            case WECHAT:
////////                contactInfo = aGardenPerson.getWechatId();
////////                break;
////////            default:
////////                return;
////////        }
////////        if (ZcaValidator.isNotNullEmpty(contactInfo)){
////////            G01ContactInfo aG01ContactInfo = new G01ContactInfo();
////////            aG01ContactInfo.setContactInfo(contactInfo);
////////            aG01ContactInfo.setContactInfoUuid(GardenData.generateUUIDString());
////////            aG01ContactInfo.setContactType(gardenContactType.value());
////////            aG01ContactInfo.setCreated(aGardenPerson.getCreated());
////////            //aG01ContactInfo.setEntityStatus(entityStatus);
////////            aG01ContactInfo.setEntityType(GardenEntityType.USER.name());
////////            aG01ContactInfo.setEntityUuid(aGardenPerson.getPersonUuid());
////////            aG01ContactInfo.setPreferencePriority(GardenPreference.MORE_PREFERED.ordinal());
////////            //aG01ContactInfo.setShortMemo(shortMemo);
////////            aG01ContactInfo.setUpdated(aGardenPerson.getUpdated());
////////            GardenJpaUtils.storeEntity(em, G01ContactInfo.class, aG01ContactInfo, aG01ContactInfo.getContactInfoUuid(), 
////////                    GardenDataUpdaterFactory.getSingleton().getG01ContactInfoUpdater());
////////        }
////////    }
////////
////////    private void transferGardenServiceTag(EntityManager em) throws EntityValidationException {
////////        Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenServiceTag....");
////////        List<GardenServiceTag> aGardenServiceTagList = GardenJpaUtils.findAll(em, GardenServiceTag.class);
////////        G01ServiceTag tag;
////////        for (GardenServiceTag aGardenServiceTag : aGardenServiceTagList){
////////            tag = new G01ServiceTag();
////////            tag.setCreated(aGardenServiceTag.getCreated());
////////            tag.setDefaultUnitPrice(aGardenServiceTag.getDefaultUnitPrice());
////////            tag.setDescription(aGardenServiceTag.getDescription());
////////            tag.setEntityType(TypeConverter.convertWorkTargetToGardenEntityType(aGardenServiceTag.getWorkTarget()).name());
////////            tag.setFileDemanded(aGardenServiceTag.getFileDemanded());
////////            tag.setSampleFileName(aGardenServiceTag.getSampleFileName());
////////            tag.setServiceTagName(aGardenServiceTag.getServiceTagName());
////////            tag.setServiceTagUuid(aGardenServiceTag.getServiceTagUuid());
////////            tag.setUpdated(aGardenServiceTag.getUpdated());
////////            GardenJpaUtils.storeEntity(em, G01ServiceTag.class, tag, tag.getServiceTagUuid(), 
////////                    GardenDataUpdaterFactory.getSingleton().getG01ServiceTagUpdater());
////////        }
////////    }
////////
////////    private void transferGardenArchivedDocument(EntityManager em) throws EntityValidationException {
////////        Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenArchivedDocument....");
////////        List<GardenArchivedDocument> aGardenArchivedDocumentList = GardenJpaUtils.findAll(em, GardenArchivedDocument.class);
////////        G01ArchivedDocument aG01ArchivedDocument;
////////        for (GardenArchivedDocument aGardenArchivedDocument : aGardenArchivedDocumentList){
////////            aG01ArchivedDocument = new G01ArchivedDocument();
////////            aG01ArchivedDocument.setArchivedDocumentUuid(aGardenArchivedDocument.getArchivedDocumentUuid());
////////            aG01ArchivedDocument.setDownloadClient(aGardenArchivedDocument.getDownloadClient());
////////            //aG01ArchivedDocument.setEntityStatus(entityStatus);
////////            aG01ArchivedDocument.setEntityUuid(aGardenArchivedDocument.getTargetUuid());
////////            aG01ArchivedDocument.setFileCustomName(aGardenArchivedDocument.getFileCustomName());
////////            aG01ArchivedDocument.setFileLocation(aGardenArchivedDocument.getFileLocation());
////////            aG01ArchivedDocument.setFileName(aGardenArchivedDocument.getFileName());
////////            aG01ArchivedDocument.setFileStatus(aGardenArchivedDocument.getFileStatus());
////////            aG01ArchivedDocument.setFileTimestamp(aGardenArchivedDocument.getFileTimestamp());
////////            aG01ArchivedDocument.setMemo(aGardenArchivedDocument.getMemo());
////////            aG01ArchivedDocument.setProviderUuid(aGardenArchivedDocument.getProviderUuid());
////////            aG01ArchivedDocument.setEntityType(GardenEntityType.TAXPAYER_CASE.name());  //only taxpayer in the old garden
////////            aG01ArchivedDocument.setCreated(aGardenArchivedDocument.getCreated());
////////            aG01ArchivedDocument.setUpdated(aGardenArchivedDocument.getUpdated());
////////            GardenJpaUtils.storeEntity(em, G01ArchivedDocument.class, aG01ArchivedDocument, aG01ArchivedDocument.getArchivedDocumentUuid(), 
////////                    GardenDataUpdaterFactory.getSingleton().getG01ArchivedDocumentUpdater());
////////        }
////////    }
////////
////////    private void transferGardenDocumentRequirement(EntityManager em) throws EntityValidationException {
////////        Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenDocumentRequirement....");
////////        List<GardenDocumentRequirement> aGardenDocumentRequirementList = GardenJpaUtils.findAll(em, GardenDocumentRequirement.class);
////////        G01DocumentRequirement aG01DocumentRequirement;
////////        for (GardenDocumentRequirement aGardenDocumentRequirement : aGardenDocumentRequirementList){
////////            aG01DocumentRequirement = new G01DocumentRequirement();
////////            aG01DocumentRequirement.setArchivedDocumentUuid(aGardenDocumentRequirement.getArchivedDocumentUuid());
////////            aG01DocumentRequirement.setDescription(aGardenDocumentRequirement.getDescription());
////////            aG01DocumentRequirement.setDocumentUuid(aGardenDocumentRequirement.getDocumentUuid());
////////            aG01DocumentRequirement.setEntityUuid(aGardenDocumentRequirement.getCaseUuid());
////////            aG01DocumentRequirement.setFileDemanded(aGardenDocumentRequirement.getFileDemanded());
////////            aG01DocumentRequirement.setQuantity(aGardenDocumentRequirement.getQuantity());
////////            aG01DocumentRequirement.setServiceTagName(aGardenDocumentRequirement.getServiceTagName());
////////            aG01DocumentRequirement.setServiceTagUuid(aGardenDocumentRequirement.getServiceTagUuid());
////////            aG01DocumentRequirement.setUnitPrice(aGardenDocumentRequirement.getUnitPrice());
////////            //aG01ArchivedDocument.setEntityStatus(entityStatus);
////////            aG01DocumentRequirement.setEntityType(GardenEntityType.TAXPAYER_CASE.name());  //only taxpayer in the old garden
////////            aG01DocumentRequirement.setCreated(aGardenDocumentRequirement.getCreated());
////////            aG01DocumentRequirement.setUpdated(aGardenDocumentRequirement.getUpdated());
////////            GardenJpaUtils.storeEntity(em, G01DocumentRequirement.class, aG01DocumentRequirement, aG01DocumentRequirement.getDocumentUuid(), 
////////                    GardenDataUpdaterFactory.getSingleton().getG01DocumentRequirementUpdater());
////////        }
////////    }
////////
////////    private void transferGardenBusinessProperty(EntityManager em) throws EntityValidationException {
////////        Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenBusinessProperty....");
////////        List<GardenBusinessProperty> aGardenBusinessPropertyList = GardenJpaUtils.findAll(em, GardenBusinessProperty.class);
////////        G01PersonalBusinessProperty aG01PersonalBusinessProperty;
////////        for (GardenBusinessProperty aGardenBusinessProperty : aGardenBusinessPropertyList){
////////            aG01PersonalBusinessProperty = new G01PersonalBusinessProperty();
////////            aG01PersonalBusinessProperty.setPersonalBusinessPropertyUuid(aGardenBusinessProperty.getPropertyUuid());
////////            aG01PersonalBusinessProperty.setBusinessDescription(aGardenBusinessProperty.getBusinessDescription());
////////            aG01PersonalBusinessProperty.setBusinessPropertyName(aGardenBusinessProperty.getBusinessPropertyName());
////////            aG01PersonalBusinessProperty.setBusinessAddress(aGardenBusinessProperty.getBusinessAddress());
////////            aG01PersonalBusinessProperty.setBusinessDescription(aGardenBusinessProperty.getBusinessDescription());
////////            aG01PersonalBusinessProperty.setBusinessEin(aGardenBusinessProperty.getBusinessEin());
////////            aG01PersonalBusinessProperty.setBusinessOwnership(aGardenBusinessProperty.getBusinessOwnership());
////////            aG01PersonalBusinessProperty.setGrossReceiptsSales(aGardenBusinessProperty.getGrossReceiptsSales());
////////            aG01PersonalBusinessProperty.setCostOfGoodsSold(aGardenBusinessProperty.getCostOfGoodsSold());
////////            aG01PersonalBusinessProperty.setOtherIncome(aGardenBusinessProperty.getOtherIncome());
////////            aG01PersonalBusinessProperty.setExpenseCableInternet(aGardenBusinessProperty.getExpenseCableInternet());
////////            aG01PersonalBusinessProperty.setExpenseCarAndTruck(aGardenBusinessProperty.getExpenseCarAndTruck());
////////            aG01PersonalBusinessProperty.setExpenseCommissions(aGardenBusinessProperty.getExpenseCommissions());
////////            aG01PersonalBusinessProperty.setExpenseContractLabor(aGardenBusinessProperty.getExpenseContractLabor());
////////            aG01PersonalBusinessProperty.setExpenseOffice(aGardenBusinessProperty.getExpenseOffice());
////////            aG01PersonalBusinessProperty.setExpenseRentLease(aGardenBusinessProperty.getExpenseRentLease());
////////            aG01PersonalBusinessProperty.setExpenseTelephone(aGardenBusinessProperty.getExpenseTelephone());
////////            aG01PersonalBusinessProperty.setExpenseTravelMeals(aGardenBusinessProperty.getExpenseTravelMeals());
////////            aG01PersonalBusinessProperty.setExpenseAdvertising(aGardenBusinessProperty.getExpenseAdvertising());
////////            aG01PersonalBusinessProperty.setExpenseInsurance(aGardenBusinessProperty.getExpenseInsurance());
////////            aG01PersonalBusinessProperty.setExpenseOthers(aGardenBusinessProperty.getExpenseOthers());
////////            aG01PersonalBusinessProperty.setExpenseProfServices(aGardenBusinessProperty.getExpenseProfServices());
////////            aG01PersonalBusinessProperty.setExpenseRepairs(aGardenBusinessProperty.getExpenseRepairs());
////////            aG01PersonalBusinessProperty.setExpenseSupplies(aGardenBusinessProperty.getExpenseSupplies());
////////            aG01PersonalBusinessProperty.setExpenseUtilities(aGardenBusinessProperty.getExpenseUtilities());
////////            aG01PersonalBusinessProperty.setTaxpayerCaseUuid(aGardenBusinessProperty.getCaseUuid());
////////            aG01PersonalBusinessProperty.setMemo(aGardenBusinessProperty.getMemo());
////////            //aG01ArchivedDocument.setEntityStatus(entityStatus);
////////            aG01PersonalBusinessProperty.setCreated(aGardenBusinessProperty.getCreated());
////////            aG01PersonalBusinessProperty.setUpdated(aGardenBusinessProperty.getUpdated());
////////            
////////            GardenJpaUtils.storeEntity(em, G01PersonalBusinessProperty.class, aG01PersonalBusinessProperty, aG01PersonalBusinessProperty.getPersonalBusinessPropertyUuid(), 
////////                    GardenDataUpdaterFactory.getSingleton().getG01PersonalBusinessPropertyUpdater());
////////        }
////////    }
////////
////////    private void transferGardenTlcLicense(EntityManager em) throws EntityValidationException {
////////        Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenTlcLicense....");
////////        List<GardenTlcLicense> aGardenTlcLicenseList = GardenJpaUtils.findAll(em, GardenTlcLicense.class);
////////        G01TlcLicense aG01TlcLicense;
////////        for (GardenTlcLicense aGardenTlcLicense : aGardenTlcLicenseList){
////////            aG01TlcLicense = new G01TlcLicense();
////////            
////////            aG01TlcLicense.setDriverUuid(aGardenTlcLicense.getDriverUuid());
////////            aG01TlcLicense.setTaxpayerCaseUuid(aGardenTlcLicense.getCaseUuid());
////////            aG01TlcLicense.setAccessories(aGardenTlcLicense.getAccessories());
////////            aG01TlcLicense.setBusinessMiles(aGardenTlcLicense.getBusinessMiles());
////////            aG01TlcLicense.setCarWash(aGardenTlcLicense.getCarWash());
////////            aG01TlcLicense.setDateInService(aGardenTlcLicense.getDateInService());
////////            aG01TlcLicense.setDepreciation(aGardenTlcLicense.getDepreciation());
////////            aG01TlcLicense.setGarageRent(aGardenTlcLicense.getGarageRent());
////////            aG01TlcLicense.setGas(aGardenTlcLicense.getGas());
////////            aG01TlcLicense.setInsurance(aGardenTlcLicense.getInsurance());
////////            aG01TlcLicense.setLeasePayment(aGardenTlcLicense.getLeasePayment());
////////            aG01TlcLicense.setMaintenance(aGardenTlcLicense.getMaintenance());
////////            aG01TlcLicense.setMeals(aGardenTlcLicense.getMeals());
////////            aG01TlcLicense.setMileageRate(aGardenTlcLicense.getMileageRate());
////////            aG01TlcLicense.setNumberOfSeats(aGardenTlcLicense.getNumberOfSeats());
////////            aG01TlcLicense.setOil(aGardenTlcLicense.getOil());
////////            aG01TlcLicense.setOver600lbs(aGardenTlcLicense.getOver600lbs());
////////            aG01TlcLicense.setParking(aGardenTlcLicense.getParking());
////////            aG01TlcLicense.setRadioRepair(aGardenTlcLicense.getRadioRepair());
////////            aG01TlcLicense.setRegistrationFee(aGardenTlcLicense.getRegistrationFee());
////////            aG01TlcLicense.setRepairs(aGardenTlcLicense.getRepairs());
////////            aG01TlcLicense.setServiceFee(aGardenTlcLicense.getServiceFee());
////////            aG01TlcLicense.setTelephone(aGardenTlcLicense.getTelephone());
////////            aG01TlcLicense.setTires(aGardenTlcLicense.getTires());
////////            aG01TlcLicense.setTlcLicense(aGardenTlcLicense.getTlcLicense());
////////            aG01TlcLicense.setTolls(aGardenTlcLicense.getTolls());
////////            aG01TlcLicense.setTotalMiles(aGardenTlcLicense.getTotalMiles());
////////            aG01TlcLicense.setUniform(aGardenTlcLicense.getUniform());
////////            aG01TlcLicense.setVehicleModel(aGardenTlcLicense.getVehicleModel());
////////            aG01TlcLicense.setVehicleType(aGardenTlcLicense.getVehicleType());
////////            aG01TlcLicense.setMemo(aGardenTlcLicense.getMemo());
////////            //aG01PostSection.setEntityStatus(entityStatus);
////////            
////////            aG01TlcLicense.setCreated(aGardenTlcLicense.getCreated());
////////            aG01TlcLicense.setUpdated(aGardenTlcLicense.getUpdated());
////////            
////////            GardenJpaUtils.storeEntity(em, G01TlcLicense.class, aG01TlcLicense, aG01TlcLicense.getDriverUuid(), 
////////                    GardenDataUpdaterFactory.getSingleton().getG01TlcLicenseUpdater());
////////        }
////////    }
////////
////////    private void transferGardenPersonalProperty(EntityManager em) throws EntityValidationException {
////////        Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenPersonalProperty....");
////////        List<GardenPersonalProperty> aGardenPersonalPropertyList = GardenJpaUtils.findAll(em, GardenPersonalProperty.class);
////////        G01PersonalProperty aG01PersonalProperty;
////////        for (GardenPersonalProperty aGardenPersonalProperty : aGardenPersonalPropertyList){
////////            aG01PersonalProperty = new G01PersonalProperty();
////////            
////////            aG01PersonalProperty.setPersonalPropertyUuid(aGardenPersonalProperty.getPropertyUuid());
////////            
////////            aG01PersonalProperty.setDateOnImprovement(aGardenPersonalProperty.getDateOnImprovement());
////////            aG01PersonalProperty.setDateOnService(aGardenPersonalProperty.getDateOnService());
////////            aG01PersonalProperty.setExpenseAdvertising(aGardenPersonalProperty.getExpenseAdvertising());
////////            aG01PersonalProperty.setExpenseAutoAndTravel(aGardenPersonalProperty.getExpenseAutoAndTravel());
////////            aG01PersonalProperty.setExpenseCleaning(aGardenPersonalProperty.getExpenseCleaning());
////////            aG01PersonalProperty.setExpenseCommissions(aGardenPersonalProperty.getExpenseCommissions());
////////            aG01PersonalProperty.setExpenseDepreciation(aGardenPersonalProperty.getExpenseDepreciation());
////////            aG01PersonalProperty.setExpenseInsurance(aGardenPersonalProperty.getExpenseInsurance());
////////            aG01PersonalProperty.setExpenseMgtFee(aGardenPersonalProperty.getExpenseMgtFee());
////////            aG01PersonalProperty.setExpenseMorgageInterest(aGardenPersonalProperty.getExpenseMorgageInterest());
////////            aG01PersonalProperty.setExpenseOthers(aGardenPersonalProperty.getExpenseOthers());
////////            aG01PersonalProperty.setExpenseProfServices(aGardenPersonalProperty.getExpenseProfServices());
////////            aG01PersonalProperty.setExpenseReTaxes(aGardenPersonalProperty.getExpenseReTaxes());
////////            aG01PersonalProperty.setExpenseRepairs(aGardenPersonalProperty.getExpenseRepairs());
////////            aG01PersonalProperty.setExpenseSupplies(aGardenPersonalProperty.getExpenseSupplies());
////////            aG01PersonalProperty.setExpenseUtilities(aGardenPersonalProperty.getExpenseUtilities());
////////            aG01PersonalProperty.setExpenseWaterSewer(aGardenPersonalProperty.getExpenseWaterSewer());
////////            aG01PersonalProperty.setImprovementCost(aGardenPersonalProperty.getImprovementCost());
////////            aG01PersonalProperty.setIncomeRentsReceieved(aGardenPersonalProperty.getIncomeRentsReceieved());
////////            aG01PersonalProperty.setPercentageOfOwnership(aGardenPersonalProperty.getPercentageOfOwnership());
////////            aG01PersonalProperty.setPercentageOfRentalUse(aGardenPersonalProperty.getPercentageOfRentalUse());
////////            aG01PersonalProperty.setPropertyAddress(aGardenPersonalProperty.getPropertyAddress());
////////            aG01PersonalProperty.setPropertyType(aGardenPersonalProperty.getPropertyType());
////////            aG01PersonalProperty.setPurchasePrice(aGardenPersonalProperty.getPurchasePrice());
////////            aG01PersonalProperty.setTaxpayerCaseUuid(aGardenPersonalProperty.getCaseUuid());
////////            aG01PersonalProperty.setMemo(aGardenPersonalProperty.getMemo());
////////            //aG01PersonalProperty.setEntityStatus(entityStatus);
////////            aG01PersonalProperty.setCreated(aGardenPersonalProperty.getCreated());
////////            aG01PersonalProperty.setUpdated(aGardenPersonalProperty.getUpdated());
////////            
////////            GardenJpaUtils.storeEntity(em, G01PersonalProperty.class, aG01PersonalProperty, aG01PersonalProperty.getPersonalPropertyUuid(), 
////////                    GardenDataUpdaterFactory.getSingleton().getG01PersonalPropertyUpdater());
////////        }
////////    }
////////
////////    private void transferGardenWebPost(EntityManager em) throws EntityValidationException {
////////        Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenWebPost....");
////////        List<GardenWebPost> aGardenWebPostList = GardenJpaUtils.findAll(em, GardenWebPost.class);
////////        G01WebPost aG01WebPost;
////////        for (GardenWebPost aGardenWebPost : aGardenWebPostList){
////////            aG01WebPost = new G01WebPost();
////////            
////////            aG01WebPost.setWebPostUuid(aGardenWebPost.getWebPostUuid());
////////            
////////            aG01WebPost.setAuthorAccountUuid(aGardenWebPost.getPostAuthorUuid());
////////            aG01WebPost.setPostBrief(aGardenWebPost.getPostBrief());
////////            aG01WebPost.setPostPurpose(aGardenWebPost.getPostPurpose());
////////            aG01WebPost.setPostTitle(aGardenWebPost.getPostTitle());
////////            //aG01WebPost.setEntityStatus(entityStatus);
////////            
////////            aG01WebPost.setCreated(aGardenWebPost.getCreated());
////////            aG01WebPost.setUpdated(aGardenWebPost.getUpdated());
////////            
////////            GardenJpaUtils.storeEntity(em, G01WebPost.class, aG01WebPost, aG01WebPost.getWebPostUuid(), 
////////                    GardenDataUpdaterFactory.getSingleton().getG01WebPostUpdater());
////////        }
////////    }
////////
////////    private void transferGardenPostSection(EntityManager em) throws EntityValidationException {
////////        Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenPostSection....");
////////        List<GardenPostSection> aGardenPostSectionList = GardenJpaUtils.findAll(em, GardenPostSection.class);
////////        G01PostSection aG01PostSection;
////////        for (GardenPostSection aGardenPostSection : aGardenPostSectionList){
////////            aG01PostSection = new G01PostSection();
////////            
////////            aG01PostSection.setPostSectionUuid(aGardenPostSection.getPostSectionUuid());
////////            
////////            aG01PostSection.setPostContent(aGardenPostSection.getPostContent());
////////            aG01PostSection.setWebPostUuid(aGardenPostSection.getWebPostUuid());
////////            //aG01PostSection.setEntityStatus(entityStatus);
////////            
////////            aG01PostSection.setCreated(aGardenPostSection.getCreated());
////////            aG01PostSection.setUpdated(aGardenPostSection.getUpdated());
////////            
////////            GardenJpaUtils.storeEntity(em, G01PostSection.class, aG01PostSection, aG01PostSection.getPostSectionUuid(), 
////////                    GardenDataUpdaterFactory.getSingleton().getG01PostSectionUpdater());
////////        }
////////    }
////////
////////    private void transferGardenTeam(EntityManager em) throws EntityValidationException {
////////        Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenTeam....");
////////        List<GardenTeam> aGardenTeamList = GardenJpaUtils.findAll(em, GardenTeam.class);
////////        G01WorkTeam aG01WorkTeam;
////////        for (GardenTeam aGardenTeam : aGardenTeamList){
////////            aG01WorkTeam = new G01WorkTeam();
////////            
////////            aG01WorkTeam.setWorkTeamUuid(aGardenTeam.getTeamUuid());
////////            
////////            aG01WorkTeam.setDescription(aGardenTeam.getDescription());
////////            aG01WorkTeam.setTeamName(aGardenTeam.getTeamName());
////////            //aG01PostSection.setEntityStatus(entityStatus);
////////            
////////            aG01WorkTeam.setCreated(aGardenTeam.getCreated());
////////            aG01WorkTeam.setUpdated(aGardenTeam.getUpdated());
////////            
////////            GardenJpaUtils.storeEntity(em, G01WorkTeam.class, aG01WorkTeam, aG01WorkTeam.getWorkTeamUuid(), 
////////                    GardenDataUpdaterFactory.getSingleton().getG01WorkTeamUpdater());
////////        }
////////    }
////////
////////    private void transferGardenPersonalTaxCase(EntityManager em) throws EntityValidationException, NonUniqueEntityException {
////////        Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenPersonalTaxCase....");
////////        List<GardenPersonalTaxCase> aGardenPersonalTaxCaseList = GardenJpaUtils.findAll(em, GardenPersonalTaxCase.class);
////////        G01TaxpayerCase aG01TaxpayerCase;
////////        GardenTaxpayerInfo primaryGardenTaxpayerInfo;
////////        int total = aGardenPersonalTaxCaseList.size();
////////        int count = 0;
////////        for (GardenPersonalTaxCase aGardenPersonalTaxCase : aGardenPersonalTaxCaseList){
////////            count++;
////////            Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenPersonalTaxCase....[{0}/{1}]", new Object[]{count, total});
////////            aG01TaxpayerCase = new G01TaxpayerCase();
////////            aG01TaxpayerCase.setTaxpayerCaseUuid(aGardenPersonalTaxCase.getCaseUuid());
////////            aG01TaxpayerCase.setAgreementSignature(aGardenPersonalTaxCase.getAgreementSignature());
////////            aG01TaxpayerCase.setAgreementSignatureTimestamp(aGardenPersonalTaxCase.getAgreementSignatureTimestamp());
////////            aG01TaxpayerCase.setAgreementUuid(GardenAgreement.TaxpayerCaseAgreement.value());
////////            aG01TaxpayerCase.setBankAccountNumber(aGardenPersonalTaxCase.getBankAccountNumber());
////////            aG01TaxpayerCase.setBankRoutingNumber(aGardenPersonalTaxCase.getBankRoutingNumber());
////////            aG01TaxpayerCase.setCustomerAccountUuid(aGardenPersonalTaxCase.getCustomerPersonUuid());
////////            if (ZcaValidator.isNullEmpty(aGardenPersonalTaxCase.getAgentEmployeeUuid())){
////////                aG01TaxpayerCase.setEmployeeAccountUuid(this.getBusinessOwner(em).getEmployeeAccountUuid());
////////            }else{
////////                aG01TaxpayerCase.setEmployeeAccountUuid(aGardenPersonalTaxCase.getAgentEmployeeUuid());
////////            }
////////            aG01TaxpayerCase.setDeadline(aGardenPersonalTaxCase.getDeadline());
////////            primaryGardenTaxpayerInfo = transferGardenTaxpayerInfo(em, aGardenPersonalTaxCase);
////////            if ((primaryGardenTaxpayerInfo == null) || (ZcaValidator.isNullEmpty(primaryGardenTaxpayerInfo.getMarriageStatus()))){
////////                aG01TaxpayerCase.setFederalFilingStatus(TaxpayerFederalFilingStatus.UNKNOWN.value());
////////            }else{
////////                aG01TaxpayerCase.setFederalFilingStatus(primaryGardenTaxpayerInfo.getMarriageStatus());
////////            }
////////            aG01TaxpayerCase.setMemo(aGardenPersonalTaxCase.getMemo());
////////            //aG01TaxpayerCase.setEntityStatus(aGardenPersonalTaxCase.getEntityStatus());
////////            //aG01TaxpayerCase.setExtensionDate(aGardenPersonalTaxCase.getExtensionDate());
////////            //aG01TaxpayerCase.setExtensionMemo(aGardenPersonalTaxCase.getExtensionMemo());
////////            aG01TaxpayerCase.setCreated(aGardenPersonalTaxCase.getCreated());
////////            aG01TaxpayerCase.setUpdated(aGardenPersonalTaxCase.getUpdated());
////////            
////////            aG01TaxpayerCase.setLatestLogUuid(transferGardenPersonalTaxCase_workStatus(em, aGardenPersonalTaxCase));
////////
////////            GardenJpaUtils.storeEntity(em, G01TaxpayerCase.class, aG01TaxpayerCase, aG01TaxpayerCase.getTaxpayerCaseUuid(), 
////////                    GardenDataUpdaterFactory.getSingleton().getG01TaxpayerCaseUpdater());
////////            
////////            if (primaryGardenTaxpayerInfo != null){
////////                transferGardenPersonalTaxCase_ContactInfo(em, primaryGardenTaxpayerInfo, aGardenPersonalTaxCase, GardenContactType.EMAIL);
////////                transferGardenPersonalTaxCase_ContactInfo(em, primaryGardenTaxpayerInfo, aGardenPersonalTaxCase, GardenContactType.MOBILE_PHONE);
////////                transferGardenPersonalTaxCase_ContactInfo(em, primaryGardenTaxpayerInfo, aGardenPersonalTaxCase, GardenContactType.WECHAT);
////////            }
////////            
////////            if (aGardenPersonalTaxCase.getInvoiceTotal() != null){
////////                G01Bill aG01Bill = new G01Bill();
////////                aG01Bill.setBillContent(aGardenPersonalTaxCase.getInvoiceContent());
////////                if (aGardenPersonalTaxCase.getInvoiceDatetime() == null){
////////                    aG01Bill.setBillDatetime(aGardenPersonalTaxCase.getUpdated());
////////                }else{
////////                    aG01Bill.setBillDatetime(aGardenPersonalTaxCase.getInvoiceDatetime());
////////                }
////////                aG01Bill.setBillDiscount(aGardenPersonalTaxCase.getInvoiceDiscount());
////////                aG01Bill.setBillDiscountType(aGardenPersonalTaxCase.getInvoiceDiscountType());
////////                aG01Bill.setBillStatus(aGardenPersonalTaxCase.getInvoiceStatus());
////////                aG01Bill.setBillTotal(aGardenPersonalTaxCase.getInvoiceTotal());
////////                aG01Bill.setBillUuid(GardenData.generateUUIDString());
////////                aG01Bill.setCreated(aGardenPersonalTaxCase.getCreated());
////////                aG01Bill.setUpdated(aGardenPersonalTaxCase.getUpdated());
////////                aG01Bill.setEntityStatus(GardenEntityStatus.RECORDED_FOR_TAXPAYER_CASE.value());
////////                aG01Bill.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
////////                aG01Bill.setEntityUuid(aGardenPersonalTaxCase.getCaseUuid());
////////                if (ZcaValidator.isNullEmpty(aGardenPersonalTaxCase.getAgentEmployeeUuid())){
////////                    aG01Bill.setEmployeeUuid(this.getBusinessOwner(em).getEmployeeAccountUuid());
////////                }else{
////////                    aG01Bill.setEmployeeUuid(aGardenPersonalTaxCase.getAgentEmployeeUuid());
////////                }
////////                GardenJpaUtils.storeEntity(em, G01Bill.class, aG01Bill, aG01Bill.getBillUuid(), 
////////                        GardenDataUpdaterFactory.getSingleton().getG01BillUpdater());
////////            }
////////        }
////////    }
////////    
////////    private String transferGardenPersonalTaxCase_workStatus(EntityManager em, GardenPersonalTaxCase aGardenPersonalTaxCase) throws NonUniqueEntityException, EntityValidationException {
////////        String latestLogUuid = null;
////////        //Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenTaxcorp_workStatus....");
////////        HashMap<String, Object> params = new HashMap<>();
////////        params.put("workTargetUuid", aGardenPersonalTaxCase.getCaseUuid());
////////        List<GardenWorkStatus> aGardenWorkStatusList = GardenJpaUtils.findEntityListByNamedQuery(em, 
////////                GardenWorkStatus.class, "GardenWorkStatus.findByWorkTargetUuid", params);
////////        if (!aGardenWorkStatusList.isEmpty()){
////////            Collections.sort(aGardenWorkStatusList, (GardenWorkStatus o1, GardenWorkStatus o2) -> {
////////                try{
////////                    return o1.getUpdated().compareTo(o2.getUpdated())*(-1);
////////                }catch (Exception ex){
////////                    return 0;
////////                }
////////            });
////////            G01Log aG01Log;
////////            String employeeUuid = null;
////////            for (GardenWorkStatus aGardenWorkStatus : aGardenWorkStatusList){
////////                if (latestLogUuid == null){
////////                    latestLogUuid = aGardenWorkStatus.getWorkStatusUuid();  //only keep the latest log for taxpayer
////////                }
////////                aG01Log = new G01Log();
////////                
////////                aG01Log.setEntityType(employeeUuid);
////////                aG01Log.setEntityUuid(aGardenWorkStatus.getWorkTargetUuid());
////////                aG01Log.setLogMsg(aGardenWorkStatus.getWorkStatus());
////////                aG01Log.setLogUuid(aGardenWorkStatus.getWorkStatusUuid());
////////                if (ZcaValidator.isNotNullEmpty(aGardenWorkStatus.getAgentUuid())){
////////                    employeeUuid = aGardenWorkStatus.getAgentUuid();
////////                }
////////                aG01Log.setOperatorAccountUuid(aGardenWorkStatus.getAgentUuid());
////////                aG01Log.setOperatorMessage(aGardenWorkStatus.getStatusMessage());
////////                aG01Log.setTimestamp(aGardenWorkStatus.getUpdated());
////////                
////////                GardenJpaUtils.storeEntity(em, G01Log.class, aG01Log, aG01Log.getLogUuid(), 
////////                        GardenDataUpdaterFactory.getSingleton().getG01LogUpdater());
////////            }
////////        }
////////        return latestLogUuid;
////////    }
////////
////////    private void transferGardenPersonalTaxCase_ContactInfo(EntityManager em, 
////////                                                           GardenTaxpayerInfo primaryGardenTaxpayerInfo, 
////////                                                           GardenPersonalTaxCase aGardenPersonalTaxCase,
////////                                                           GardenContactType gardenContactType) throws EntityValidationException 
////////    {
////////        //Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenPersonalTaxCase_ContactInfo....");
////////        String contactInfo = null;
////////        switch(gardenContactType){
////////            case EMAIL:
////////                contactInfo = aGardenPersonalTaxCase.getContactEmail();
////////                break;
////////            case MOBILE_PHONE:
////////                contactInfo = aGardenPersonalTaxCase.getContactPhone();
////////                break;
////////            case WECHAT:
////////                contactInfo = aGardenPersonalTaxCase.getContactWechatId();
////////                break;
////////            default:
////////                return;
////////        }
////////        if (ZcaValidator.isNotNullEmpty(contactInfo)){
////////            G01ContactInfo aG01ContactInfo = new G01ContactInfo();
////////            aG01ContactInfo.setContactInfo(contactInfo);
////////            aG01ContactInfo.setContactInfoUuid(GardenData.generateUUIDString());
////////            aG01ContactInfo.setContactType(gardenContactType.value());
////////            aG01ContactInfo.setCreated(aGardenPersonalTaxCase.getCreated());
////////            aG01ContactInfo.setEntityStatus(GardenEntityStatus.RECORDED_FOR_TAXPAYER_CASE.value());
////////            aG01ContactInfo.setEntityType(GardenEntityType.USER.name());
////////            aG01ContactInfo.setEntityUuid(primaryGardenTaxpayerInfo.getTaxpayerUuid());
////////            aG01ContactInfo.setPreferencePriority(GardenPreference.MORE_PREFERED.ordinal());
////////            //aG01ContactInfo.setShortMemo(shortMemo);
////////            aG01ContactInfo.setUpdated(aGardenPersonalTaxCase.getUpdated());
////////            GardenJpaUtils.storeEntity(em, G01ContactInfo.class, aG01ContactInfo, aG01ContactInfo.getContactInfoUuid(), 
////////                    GardenDataUpdaterFactory.getSingleton().getG01ContactInfoUpdater());
////////        }
////////    }
////////    
////////    private GardenEmployee getBusinessOwner(EntityManager em) throws NonUniqueEntityException{
////////        HashMap<String, Object> params = new HashMap<>();
////////        params.put("jobTitle", GardenWorkTitle.BUSINESS_OWNER.value());
////////        return GardenJpaUtils.findEntityByNamedQuery(em, GardenEmployee.class, "GardenEmployee.findByJobTitle", params);
////////    }
////////
////////    private void transferGardenTeamHasEmployee(EntityManager em) throws EntityValidationException, NonUniqueEntityException {
////////        Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenTeamHasEmployee....");
////////        List<GardenTeamHasEmployee> aGardenTeamHasEmployeeList = GardenJpaUtils.findAll(em, GardenTeamHasEmployee.class);
////////        G01WorkTeamHasEmployee aG01WorkTeamHasEmployee;
////////        G01WorkTeamHasEmployeePK pkid;
////////        for (GardenTeamHasEmployee aGardenTeamHasEmployee : aGardenTeamHasEmployeeList){
////////            aG01WorkTeamHasEmployee = new G01WorkTeamHasEmployee();
////////            pkid = new G01WorkTeamHasEmployeePK();
////////            pkid.setEmployeeUuid(aGardenTeamHasEmployee.getGardenTeamHasEmployeePK().getEmployeeAccountUuid());
////////            pkid.setWorkTeamUuid(aGardenTeamHasEmployee.getGardenTeamHasEmployeePK().getTeamUuid());
////////            aG01WorkTeamHasEmployee.setG01WorkTeamHasEmployeePK(pkid);
////////            
////////            aG01WorkTeamHasEmployee.setTimestamp(aGardenTeamHasEmployee.getUpdated());
////////            aG01WorkTeamHasEmployee.setTitleInTeam(aGardenTeamHasEmployee.getRoleInTeam());
////////            
////////            aG01WorkTeamHasEmployee.setOperatorAccountUuid(getBusinessOwner(em).getEmployeeAccountUuid());
////////            
////////            
////////            GardenJpaUtils.storeEntity(em, G01WorkTeamHasEmployee.class, aG01WorkTeamHasEmployee, aG01WorkTeamHasEmployee.getG01WorkTeamHasEmployeePK(), 
////////                    GardenDataUpdaterFactory.getSingleton().getG01WorkTeamHasEmployeeUpdater());
////////        }
////////    }
////////
////////    /**
////////     * 
////////     * @param aGardenPersonalTaxCase
////////     * @return - the GardenTaxpayerInfo whose FederalFilingStatus is for aGardenPersonalTaxCase
////////     */
////////    private GardenTaxpayerInfo transferGardenTaxpayerInfo(EntityManager em, GardenPersonalTaxCase aGardenPersonalTaxCase) throws EntityValidationException {
////////        //Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenTaxpayerInfo....");
////////        GardenTaxpayerInfo result = null;
////////        HashMap<String, Object> params = new HashMap<>();
////////        params.put("caseUuid", aGardenPersonalTaxCase.getCaseUuid());
////////        List<GardenTaxpayerInfo> aGardenTaxpayerInfoList = GardenJpaUtils.findEntityListByNamedQuery(em, GardenTaxpayerInfo.class, 
////////                "GardenTaxpayerInfo.findByCaseUuid", params);
////////        G01TaxpayerInfo aG01TaxpayerInfo;
////////        for (GardenTaxpayerInfo aGardenTaxpayerInfo : aGardenTaxpayerInfoList){
////////            aG01TaxpayerInfo = new G01TaxpayerInfo();
////////            aG01TaxpayerInfo.setCreated(aGardenTaxpayerInfo.getCreated());
////////            //aG01TaxpayerInfo.setEmail(email);
////////            //aG01TaxpayerInfo.setEntityStatus(entityStatus);
////////            //aG01TaxpayerInfo.setFax(fax);
////////            //aG01TaxpayerInfo.setHomePhone(homePhone);
////////            aG01TaxpayerInfo.setLengthOfLivingTogether(aGardenTaxpayerInfo.getLengthOfLivingTogether());
////////            aG01TaxpayerInfo.setMemo(aGardenTaxpayerInfo.getMemo());
////////            //aG01TaxpayerInfo.setMobilePhone(mobilePhone);
////////            aG01TaxpayerInfo.setRelationships(aGardenTaxpayerInfo.getRelationship());
////////            aG01TaxpayerInfo.setTaxpayerCaseUuid(aGardenTaxpayerInfo.getCaseUuid());
////////            aG01TaxpayerInfo.setTaxpayerUserUuid(aGardenTaxpayerInfo.getTaxpayerUuid());
////////            aG01TaxpayerInfo.setUpdated(aGardenTaxpayerInfo.getUpdated());
////////            //aG01TaxpayerInfo.setWorkPhone(workPhone);
////////            transferGardenTaxpayerInfoHelper_G01User(em, aGardenTaxpayerInfo);
////////            
////////            if (TaxpayerRelationship.PRIMARY_TAXPAYER.value().equalsIgnoreCase(aGardenTaxpayerInfo.getRelationship())){
////////                if (ZcaValidator.isNotNullEmpty(aGardenTaxpayerInfo.getMarriageStatus())){
////////                    result = aGardenTaxpayerInfo;
////////                }
////////            }else{
////////                if (result == null){
////////                    result = aGardenTaxpayerInfo;
////////                }
////////            }
////////            GardenJpaUtils.storeEntity(em, G01TaxpayerInfo.class, aG01TaxpayerInfo, aG01TaxpayerInfo.getTaxpayerUserUuid(), 
////////                    GardenDataUpdaterFactory.getSingleton().getG01TaxpayerInfoUpdater());
////////        }
////////        return result;
////////    }
////////
////////    private void transferGardenTaxpayerInfoHelper_G01User(EntityManager em, GardenTaxpayerInfo aGardenTaxpayerInfo) throws EntityValidationException {
////////        //Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenTaxpayerInfoHelper_G01User....");
////////        G01User aG01User = new G01User();
////////        aG01User.setBirthday(aGardenTaxpayerInfo.getBirthday());
////////        aG01User.setCitizenship(aGardenTaxpayerInfo.getCitizenship());
////////        aG01User.setEntityStatus(GardenEntityStatus.RECORDED_FOR_TAXPAYER_CASE.value());
////////        aG01User.setFirstName(aGardenTaxpayerInfo.getFirstName());
////////        //aG01User.setGender(gender);
////////        aG01User.setLastName(aGardenTaxpayerInfo.getLastName());
////////        aG01User.setMemo(aGardenTaxpayerInfo.getMemo());
////////        aG01User.setMiddleName(aGardenTaxpayerInfo.getMiddleName());
////////        aG01User.setOccupation(aGardenTaxpayerInfo.getOccupation());
////////        aG01User.setSsn(aGardenTaxpayerInfo.getSsn());
////////        aG01User.setUserUuid(aGardenTaxpayerInfo.getTaxpayerUuid());
////////        aG01User.setCreated(aGardenTaxpayerInfo.getCreated());
////////        aG01User.setUpdated(aGardenTaxpayerInfo.getUpdated());
////////        GardenJpaUtils.storeEntity(em, G01User.class, aG01User, aG01User.getUserUuid(), 
////////                GardenDataUpdaterFactory.getSingleton().getG01UserUpdater());
////////
////////        if (ZcaValidator.isNotNullEmpty(aGardenTaxpayerInfo.getAddress())){
////////            G01Location aG01Location = new G01Location();
////////            aG01Location.setCityName(aGardenTaxpayerInfo.getCityName());
////////            aG01Location.setCountry(aGardenTaxpayerInfo.getCountry());
////////            aG01Location.setCreated(aGardenTaxpayerInfo.getCreated());
////////            //aG01Location.setEntityStatus();
////////            aG01Location.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
////////            aG01Location.setEntityUuid(aGardenTaxpayerInfo.getTaxpayerUuid());
////////            aG01Location.setLocalAddress(aGardenTaxpayerInfo.getAddress());
////////            aG01Location.setLocationUuid(GardenData.generateUUIDString());
////////            aG01Location.setPreferencePriority(GardenPreference.MORE_PREFERED.ordinal());
////////            //aG01Location.setShortMemo(shortMemo);
////////            aG01Location.setStateCounty(aGardenTaxpayerInfo.getStateCounty());
////////            aG01Location.setStateName(aGardenTaxpayerInfo.getStateName());
////////            aG01Location.setUpdated(aGardenTaxpayerInfo.getUpdated());
////////            aG01Location.setZipCode(aGardenTaxpayerInfo.getZipCode());
////////            GardenJpaUtils.storeEntity(em, G01Location.class, aG01Location, aG01Location.getLocationUuid(), 
////////                    GardenDataUpdaterFactory.getSingleton().getG01LocationUpdater());
////////        }
////////    }
////////
////////    private void transferGardenPayment(EntityManager em) throws EntityValidationException, NonUniqueEntityException {
////////        Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenPayment....");
////////        
////////        List<GardenPayment> aGardenPaymentList = GardenJpaUtils.findAll(em, GardenPayment.class);
////////        Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenPayment....");
////////        G01Payment aG01Payment;
////////        String billUuid;
////////        for (GardenPayment aGardenPayment : aGardenPaymentList){
////////            billUuid = transferGardenPaymentHelper_GetBillUuid(em, aGardenPayment);
////////            if (ZcaValidator.isNotNullEmpty(billUuid)){
////////                aG01Payment = new G01Payment();
////////                aG01Payment.setBillUuid(billUuid);
////////                aG01Payment.setEmployeeUuid(aGardenPayment.getAgentUuid());
////////                aG01Payment.setEntityStatus(GardenEntityStatus.RECORDED_FOR_TAXPAYER_CASE.value());
////////                aG01Payment.setPaymentDate(aGardenPayment.getPaymentDate());
////////                aG01Payment.setPaymentMemo(aGardenPayment.getPaymentMemo());
////////                aG01Payment.setPaymentPrice(aGardenPayment.getPaymentPrice());
////////                aG01Payment.setPaymentType(aGardenPayment.getPaymentType());
////////                aG01Payment.setPaymentUuid(aGardenPayment.getPaymentUuid());
////////                aG01Payment.setCreated(aGardenPayment.getCreated());
////////                aG01Payment.setUpdated(aGardenPayment.getUpdated());
////////                GardenJpaUtils.storeEntity(em, G01Payment.class, aG01Payment, aG01Payment.getPaymentUuid(), 
////////                        GardenDataUpdaterFactory.getSingleton().getG01PaymentUpdater());
////////            }
////////        }
////////    }
////////
////////    private String transferGardenPaymentHelper_GetBillUuid(EntityManager em, GardenPayment aGardenPayment) throws NonUniqueEntityException, EntityValidationException {
////////        HashMap<String, Object> params = new HashMap<>();
////////        params.put("entityUuid", aGardenPayment.getCaseUuid());
////////        List<G01Bill> aG01BillList = GardenJpaUtils.findEntityListByNamedQuery(em, G01Bill.class, "G01Bill.findByEntityUuid", params);
////////        if (aG01BillList.isEmpty()){
////////            G01Bill aG01Bill = new G01Bill();
////////            aG01Bill.setBillContent(aGardenPayment.getPaymentMemo());
////////            if (aGardenPayment.getPaymentDate() == null){
////////                aG01Bill.setBillDatetime(aGardenPayment.getUpdated());
////////            }else{
////////                aG01Bill.setBillDatetime(aGardenPayment.getPaymentDate());
////////            }
////////            aG01Bill.setBillDiscount(0F);
////////            aG01Bill.setBillDiscountType(GardenDiscountType.DOLLAR.value());
////////            aG01Bill.setBillStatus(null);
////////            aG01Bill.setBillTotal(aGardenPayment.getPaymentPrice());
////////            aG01Bill.setBillUuid(GardenData.generateUUIDString());
////////            aG01Bill.setCreated(aGardenPayment.getCreated());
////////            aG01Bill.setUpdated(aGardenPayment.getUpdated());
////////            aG01Bill.setEntityStatus(GardenEntityStatus.RECORDED_FOR_TAXPAYER_CASE.value());
////////            aG01Bill.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
////////            aG01Bill.setEntityUuid(aGardenPayment.getCaseUuid());
////////            if (ZcaValidator.isNullEmpty(aGardenPayment.getAgentUuid())){
////////                aG01Bill.setEmployeeUuid(this.getBusinessOwner(em).getEmployeeAccountUuid());
////////            }else{
////////                aG01Bill.setEmployeeUuid(aGardenPayment.getAgentUuid());
////////            }
////////            GardenJpaUtils.storeEntity(em, G01Bill.class, aG01Bill, aG01Bill.getBillUuid(), 
////////                    GardenDataUpdaterFactory.getSingleton().getG01BillUpdater());
////////            return aG01Bill.getBillUuid();
////////        }else{
////////            return aG01BillList.get(0).getBillUuid();
////////        }
////////    }
////////
////////    private void transferGardenTaxcorp(EntityManager em) throws EntityValidationException, NonUniqueEntityException {
////////        Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenTaxcorp....");
////////        List<GardenTaxcorp> aGardenTaxcorpList = GardenJpaUtils.findAll(em, GardenTaxcorp.class);
////////        G01TaxcorpCase aG01TaxcorpCase;
////////        String customerUuid;
////////        int total = aGardenTaxcorpList.size();
////////        int count = 0;
////////        for (GardenTaxcorp aGardenTaxcorp : aGardenTaxcorpList){
////////            count++;
////////            Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenTaxcorp....[{0}/{1}]", new Object[]{count, total});
////////            customerUuid = transferGardenTaxcorp_contactors(em, aGardenTaxcorp);
////////            if (ZcaValidator.isNullEmpty(customerUuid)){
////////                customerUuid = GardenData.generateUUIDString();
////////            }
////////            
////////            transferGardenTaxcorp_location(em, customerUuid, aGardenTaxcorp);
////////            
////////            if (ZcaValidator.isNullEmpty(aGardenTaxcorp.getEinNumber())){
////////                aGardenTaxcorp.setEinNumber("00-0000000");
////////            }
////////            aG01TaxcorpCase = new G01TaxcorpCase();
////////            //aG01TaxcorpCase.setAgreementSignature(agreementSignature);
////////            //aG01TaxcorpCase.setAgreementSignatureTimestamp(agreementSignatureTimestamp);
////////            aG01TaxcorpCase.setAgreementUuid(GardenAgreement.TaxcorpCaseAgreement.value());
////////            aG01TaxcorpCase.setBusinessPurpose(aGardenTaxcorp.getBusinessPurpose());
////////            aG01TaxcorpCase.setBusinessStatus(aGardenTaxcorp.getBusinessStatus());
////////            aG01TaxcorpCase.setBusinessType(aGardenTaxcorp.getBusinessType());
////////            aG01TaxcorpCase.setCorporateEmail(aGardenTaxcorp.getEmail());
////////            aG01TaxcorpCase.setCorporateFax(aGardenTaxcorp.getFax());
////////            aG01TaxcorpCase.setCorporateName(aGardenTaxcorp.getTaxcorpName());
////////            aG01TaxcorpCase.setCorporatePhone(aGardenTaxcorp.getPhone());
////////            aG01TaxcorpCase.setCorporateWebPresence(aGardenTaxcorp.getWebPresence());
////////            aG01TaxcorpCase.setCreated(aGardenTaxcorp.getCreated());
////////            aG01TaxcorpCase.setCustomerAccountUuid(customerUuid);
////////            aG01TaxcorpCase.setDosDate(aGardenTaxcorp.getDosDate());
////////            aG01TaxcorpCase.setEinNumber(aGardenTaxcorp.getEinNumber());
////////            aG01TaxcorpCase.setEmployeeAccountUuid(transferGardenTaxcorp_workStatus(em, aGardenTaxcorp));
////////            //aG01TaxcorpCase.setEntityStatus(entityStatus);
////////            aG01TaxcorpCase.setMemo(aGardenTaxcorp.getMemo());
////////            aG01TaxcorpCase.setTaxcorpCaseUuid(aGardenTaxcorp.getTaxcorpUuid());
////////            aG01TaxcorpCase.setTaxcorpState(aGardenTaxcorp.getStateName());
////////            aG01TaxcorpCase.setUpdated(aGardenTaxcorp.getUpdated());
////////
////////            aG01TaxcorpCase.setCreated(aGardenTaxcorp.getCreated());
////////            aG01TaxcorpCase.setUpdated(aGardenTaxcorp.getUpdated());
////////
////////            GardenJpaUtils.storeEntity(em, G01TaxcorpCase.class, aG01TaxcorpCase, aG01TaxcorpCase.getTaxcorpCaseUuid(),
////////                    GardenDataUpdaterFactory.getSingleton().getG01TaxcorpCaseUpdater());
////////        }//for
////////    }
////////
////////    /**
////////     * 
////////     * @param em
////////     * @param aGardenTaxcorp
////////     * @return - customer-uuid for aGardenTaxcorp
////////     */
////////    private String transferGardenTaxcorp_contactors(EntityManager em, GardenTaxcorp aGardenTaxcorp) throws NonUniqueEntityException, EntityValidationException {
////////        //Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenTaxcorp_contactors....");
////////        HashMap<String, Object> params = new HashMap<>();
////////        params.put("taxcorpUuid", aGardenTaxcorp.getTaxcorpUuid());
////////        List<GardenTaxcorpPerson> aGardenTaxcorpPersonList = GardenJpaUtils.findEntityListByNamedQuery(em, 
////////                GardenTaxcorpPerson.class, "GardenTaxcorpPerson.findByTaxcorpUuid", params);
////////        if (aGardenTaxcorpPersonList.isEmpty()){
////////            return aGardenTaxcorp.getPrimaryContactorUuid();
////////        }else{
////////            G01TaxcorpRepresentative aG01TaxcorpRepresentative;
////////            G01TaxcorpRepresentativePK pkid;
////////            String customerUuid = null;
////////            boolean foundTaxcorpOwner = false;
////////            for (GardenTaxcorpPerson aGardenTaxcorpPerson : aGardenTaxcorpPersonList){
////////                aG01TaxcorpRepresentative = new G01TaxcorpRepresentative();
////////                pkid = new G01TaxcorpRepresentativePK();
////////                pkid.setRepresentativeUserUuid(aGardenTaxcorpPerson.getGardenTaxcorpPersonPK().getPersonUuid());
////////                pkid.setTaxcorpCaseUuid(aGardenTaxcorpPerson.getGardenTaxcorpPersonPK().getTaxcorpUuid());
////////                aG01TaxcorpRepresentative.setCreated(aGardenTaxcorpPerson.getCreated());
////////                //aG01TaxcorpRepresentative.setEntityStatus(entityStatus);
////////                aG01TaxcorpRepresentative.setG01TaxcorpRepresentativePK(pkid);
////////                aG01TaxcorpRepresentative.setMemo(aGardenTaxcorpPerson.getDescription());
////////                aG01TaxcorpRepresentative.setRoleInCorp(aGardenTaxcorpPerson.getRoleInCorp());
////////                if (TaxcorpContactorRole.TAXCORP_OWNER.value().equalsIgnoreCase(aGardenTaxcorpPerson.getRoleInCorp())){
////////                    foundTaxcorpOwner = true;
////////                    customerUuid = aGardenTaxcorpPerson.getGardenTaxcorpPersonPK().getPersonUuid();
////////                }else if (!foundTaxcorpOwner){
////////                    if (TaxcorpContactorRole.PRIMARY_CONTACTOR.value().equalsIgnoreCase(aGardenTaxcorpPerson.getRoleInCorp())){
////////                        customerUuid = aGardenTaxcorpPerson.getGardenTaxcorpPersonPK().getPersonUuid();
////////                    }else{
////////                        if (ZcaValidator.isNullEmpty(customerUuid)){
////////                            customerUuid = aGardenTaxcorpPerson.getGardenTaxcorpPersonPK().getPersonUuid();
////////                        }
////////                    }
////////                }
////////                aG01TaxcorpRepresentative.setUpdated(aGardenTaxcorpPerson.getUpdated());
////////                
////////                
////////                GardenJpaUtils.storeEntity(em, G01TaxcorpRepresentative.class, aG01TaxcorpRepresentative, aG01TaxcorpRepresentative.getG01TaxcorpRepresentativePK(), 
////////                        GardenDataUpdaterFactory.getSingleton().getG01TaxcorpRepresentativeUpdater());
////////            }
////////            if (ZcaValidator.isNullEmpty(customerUuid)){
////////                customerUuid = aGardenTaxcorp.getPrimaryContactorUuid();
////////                if (ZcaValidator.isNullEmpty(customerUuid)){
////////                    //try to get it from garden_online_request
////////                    params.clear();
////////                    params.put("targetEntityUuid", aGardenTaxcorp.getTaxcorpUuid());
////////                    List<GardenOnlineRequest> aGardenOnlineRequestList = GardenJpaUtils.findEntityListByNamedQuery(em, 
////////                            GardenOnlineRequest.class, "GardenOnlineRequest.findByTargetEntityUuid", params);
////////                    for (GardenOnlineRequest aGardenOnlineRequest : aGardenOnlineRequestList){
////////                        if (ZcaValidator.isNotNullEmpty(customerUuid)){
////////                            break;
////////                        }else{
////////                            customerUuid = aGardenOnlineRequest.getAccountPersonUuid();
////////                        }
////////                    }
////////                }
////////            }
////////            return customerUuid;
////////        }
////////    }
////////
////////    /**
////////     * 
////////     * @param em
////////     * @param aGardenTaxcorp
////////     * @return - employeeUuid for aGardenTaxcorp
////////     */
////////    private String transferGardenTaxcorp_workStatus(EntityManager em, GardenTaxcorp aGardenTaxcorp) throws NonUniqueEntityException, EntityValidationException {
////////        //Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenTaxcorp_workStatus....");
////////        HashMap<String, Object> params = new HashMap<>();
////////        params.put("workTargetUuid", aGardenTaxcorp.getTaxcorpUuid());
////////        List<GardenWorkStatus> aGardenWorkStatusList = GardenJpaUtils.findEntityListByNamedQuery(em, 
////////                GardenWorkStatus.class, "GardenWorkStatus.findByWorkTargetUuid", params);
////////        if (aGardenWorkStatusList.isEmpty()){
////////            return this.getBusinessOwner(em).getEmployeeAccountUuid();
////////        }else{
////////            Collections.sort(aGardenWorkStatusList, (GardenWorkStatus o1, GardenWorkStatus o2) -> {
////////                try{
////////                    return o1.getUpdated().compareTo(o2.getUpdated())*(-1);
////////                }catch (Exception ex){
////////                    return 0;
////////                }
////////            });
////////            G01Log aG01Log;
////////            String employeeUuid = null;
////////            for (GardenWorkStatus aGardenWorkStatus : aGardenWorkStatusList){
////////                aG01Log = new G01Log();
////////                
////////                aG01Log.setEntityType(employeeUuid);
////////                aG01Log.setEntityUuid(aGardenWorkStatus.getWorkTargetUuid());
////////                aG01Log.setLogMsg(aGardenWorkStatus.getWorkStatus());
////////                aG01Log.setLogUuid(aGardenWorkStatus.getWorkStatusUuid());
////////                if (ZcaValidator.isNotNullEmpty(aGardenWorkStatus.getAgentUuid())){
////////                    employeeUuid = aGardenWorkStatus.getAgentUuid();
////////                }
////////                aG01Log.setOperatorAccountUuid(aGardenWorkStatus.getAgentUuid());
////////                aG01Log.setOperatorMessage(aGardenWorkStatus.getStatusMessage());
////////                aG01Log.setTimestamp(aGardenWorkStatus.getUpdated());
////////                
////////                GardenJpaUtils.storeEntity(em, G01Log.class, aG01Log, aG01Log.getLogUuid(), 
////////                        GardenDataUpdaterFactory.getSingleton().getG01LogUpdater());
////////            }
////////            if (ZcaValidator.isNullEmpty(employeeUuid)){
////////                employeeUuid = this.getBusinessOwner(em).getEmployeeAccountUuid();
////////            }
////////            return employeeUuid;
////////        }
////////    }
////////
////////    private void transferGardenTaxcorp_location(EntityManager em, String customerUuid, GardenTaxcorp aGardenTaxcorp) throws EntityValidationException {
////////        G01Location aG01Location = new G01Location();
////////        aG01Location.setCityName(aGardenTaxcorp.getCityName());
////////        aG01Location.setCountry(aGardenTaxcorp.getCountry());
////////        aG01Location.setCreated(aGardenTaxcorp.getCreated());
////////        //aG01Location.setEntityStatus(customerUuid);
////////        aG01Location.setEntityType(GardenEntityType.TAXCORP_CASE.name());
////////        aG01Location.setEntityUuid(customerUuid);
////////        aG01Location.setLocalAddress(aGardenTaxcorp.getAddress());
////////        aG01Location.setLocationUuid(GardenData.generateUUIDString());
////////        aG01Location.setPreferencePriority(GardenPreference.MORE_PREFERED.ordinal());
////////        String memo = null;
////////        if (ZcaValidator.isNotNullEmpty(aGardenTaxcorp.getMemo())){
////////            if (aGardenTaxcorp.getMemo().length() > 250){
////////                memo = aGardenTaxcorp.getMemo().substring(0, 250);
////////            }
////////        }
////////        aG01Location.setShortMemo(memo);
////////        //aG01Location.setStateCounty();
////////        aG01Location.setStateName(aGardenTaxcorp.getStateName());
////////        aG01Location.setUpdated(aGardenTaxcorp.getUpdated());
////////        aG01Location.setZipCode(aGardenTaxcorp.getZipCode());
////////        GardenJpaUtils.storeEntity(em, G01Location.class, aG01Location, aG01Location.getLocationUuid(), 
////////                GardenDataUpdaterFactory.getSingleton().getG01LocationUpdater());
////////        
////////    }
////////
////////    private String cleanFisical(String period){
////////        if (ZcaValidator.isNotNullEmpty(period)){
////////            if (period.contains("Fisical")){
////////                period = period.replace("Fisical", "Fiscal");
////////            }
////////        }
////////        return period;
////////    }
////////    
////////    private void transferGardenTaxation(EntityManager em) throws EntityValidationException {
////////        try{
////////            Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenTaxation....");
////////            List<GardenTaxation> aGardenTaxationList = GardenJpaUtils.findAll(em, GardenTaxation.class);
////////            G01TaxFilingType aG01TaxFilingType;
////////            G01TaxFilingTypePK pkid;
////////            G01TaxFiling aG01TaxFiling;
////////            List<GardenTaxationExtension> aGardenTaxationExtensionList;
////////            HashMap<String, Object> params = new HashMap<>();
////////            int total = aGardenTaxationList.size();
////////            int count = 0;
////////            for (GardenTaxation aGardenTaxation : aGardenTaxationList){
////////                count++;
////////                Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.INFO, ">>>transferGardenTaxation....[{0}/{1}]", new Object[]{count, total});
////////                aG01TaxFiling = new G01TaxFiling();
////////                aG01TaxFiling.setTaxFilingUuid(GardenData.generateUUIDString());
////////                aG01TaxFiling.setDeadline(aGardenTaxation.getDeadline());
////////                //aG01TaxFiling.setEntityStatus(aGardenTaxation.getEntityStatus());
////////                aG01TaxFiling.setMemo(aGardenTaxation.getMemo());
////////                aG01TaxFiling.setReceivedDate(aGardenTaxation.getReceivedDate());
////////                aG01TaxFiling.setReceivedMemo(aGardenTaxation.getReceivedMemo());
////////                aG01TaxFiling.setPreparedDate(aGardenTaxation.getPreparedDate());
////////                aG01TaxFiling.setPreparedMemo(aGardenTaxation.getPreparedMemo());
////////                aG01TaxFiling.setCompletedDate(aGardenTaxation.getCompletedDate());
////////                aG01TaxFiling.setCompletedMemo(aGardenTaxation.getCompletedMemo());
////////                aG01TaxFiling.setPickupDate(aGardenTaxation.getPickupDate());
////////                aG01TaxFiling.setPickupMemo(aGardenTaxation.getPickupMemo());
////////                aG01TaxFiling.setTaxFilingPeriod(aGardenTaxation.getTaxPeriod());
////////                aG01TaxFiling.setTaxFilingType(cleanFisical(aGardenTaxation.getTaxType()));
////////                aG01TaxFiling.setTaxcorpCaseUuid(aGardenTaxation.getTargetEntityUuid());
////////
////////                params.put("taxationUuid", aGardenTaxation.getTaxationUuid());
////////                aGardenTaxationExtensionList = GardenJpaUtils.findEntityListByNamedQuery(em, 
////////                        GardenTaxationExtension.class, "GardenTaxationExtension.findByTaxationUuid", params);
////////                if (!aGardenTaxationExtensionList.isEmpty()){
////////                    Collections.sort(aGardenTaxationExtensionList, (GardenTaxationExtension o1, GardenTaxationExtension o2) -> o1.getUpdated().compareTo(o2.getUpdated())*(-1));
////////                    aG01TaxFiling.setExtensionDate(aGardenTaxationExtensionList.get(0).getExtendedDeadline());
////////                    aG01TaxFiling.setExtensionMemo(aGardenTaxationExtensionList.get(0).getDescription());
////////                }
////////
////////                GardenJpaUtils.storeEntity(em, G01TaxFiling.class, aG01TaxFiling, aG01TaxFiling.getTaxFilingUuid(), 
////////                        GardenDataUpdaterFactory.getSingleton().getG01TaxFilingUpdater());
////////
////////                aG01TaxFilingType = new G01TaxFilingType();
////////                pkid = new G01TaxFilingTypePK();
////////                pkid.setTaxFilingPeriod(aGardenTaxation.getTaxPeriod());
////////                pkid.setTaxFilingType(cleanFisical(aGardenTaxation.getTaxType()));
////////                pkid.setTaxcorpCaseUuid(aGardenTaxation.getTargetEntityUuid());
////////                aG01TaxFilingType.setG01TaxFilingTypePK(pkid);
////////                //aG01TaxFilingType.setEntityStatus(entityStatus);
////////                aG01TaxFilingType.setCreated(aGardenTaxation.getCreated());
////////                aG01TaxFilingType.setUpdated(aGardenTaxation.getUpdated());
////////
////////                GardenJpaUtils.storeEntity(em, G01TaxFilingType.class, aG01TaxFilingType, aG01TaxFilingType.getG01TaxFilingTypePK(), 
////////                        GardenDataUpdaterFactory.getSingleton().getG01TaxFilingTypeUpdater());
////////
////////            }
////////        }catch(Exception ex){
////////            Logger.getLogger(RoseRuntimeEJB.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
////////            throw ex;
////////        }
////////        
////////    }
    
}
