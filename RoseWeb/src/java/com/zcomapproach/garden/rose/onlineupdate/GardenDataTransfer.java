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

import com.zcomapproach.garden.data.GardenFlower;
import com.zcomapproach.garden.data.GardenFlowerOwner;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.commons.persistent.exception.NonUniqueEntityException;
import com.zcomapproach.garden.guard.GardenMaster;
import com.zcomapproach.garden.guard.RoseWebCipher;
import com.zcomapproach.garden.guard.TechnicalController;
import com.zcomapproach.garden.persistence.GardenJpaUtils;
import com.zcomapproach.garden.persistence.constant.GardenTaxFilingStatus;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.constant.GardenWorkTitle;
import com.zcomapproach.garden.persistence.constant.SystemSettingsPurpose;
import com.zcomapproach.garden.persistence.constant.TaxFilingPeriod;
import com.zcomapproach.garden.persistence.constant.TaxFilingType;
import com.zcomapproach.garden.persistence.constant.TaxcorpBusinessStatus;
import com.zcomapproach.garden.persistence.constant.TaxpayerRelationship;
import com.zcomapproach.garden.persistence.entity.G01Account;
import com.zcomapproach.garden.persistence.entity.G01AccountBk;
import com.zcomapproach.garden.persistence.entity.G01ArchivedDocument;
import com.zcomapproach.garden.persistence.entity.G01ArchivedDocumentBk;
import com.zcomapproach.garden.persistence.entity.G01Bill;
import com.zcomapproach.garden.persistence.entity.G01ChatMessage;
import com.zcomapproach.garden.persistence.entity.G01ContactEntity;
import com.zcomapproach.garden.persistence.entity.G01ContactInfo;
import com.zcomapproach.garden.persistence.entity.G01ContactMessage;
import com.zcomapproach.garden.persistence.entity.G01DocumentRequirement;
import com.zcomapproach.garden.persistence.entity.G01Employee;
import com.zcomapproach.garden.persistence.entity.G01EmployeeBk;
import com.zcomapproach.garden.persistence.entity.G01Location;
import com.zcomapproach.garden.persistence.entity.G01Log;
import com.zcomapproach.garden.persistence.entity.G01Payment;
import com.zcomapproach.garden.persistence.entity.G01PersonalBusinessProperty;
import com.zcomapproach.garden.persistence.entity.G01PersonalProperty;
import com.zcomapproach.garden.persistence.entity.G01PostSection;
import com.zcomapproach.garden.persistence.entity.G01Property;
import com.zcomapproach.garden.persistence.entity.G01Schedule;
import com.zcomapproach.garden.persistence.entity.G01SecurityQna;
import com.zcomapproach.garden.persistence.entity.G01ServiceTag;
import com.zcomapproach.garden.persistence.entity.G01SystemProperty;
import com.zcomapproach.garden.persistence.entity.G01TaxFiling;
import com.zcomapproach.garden.persistence.entity.G01TaxFilingType;
import com.zcomapproach.garden.persistence.entity.G01TaxFilingTypePK;
import com.zcomapproach.garden.persistence.entity.G01TaxcorpCase;
import com.zcomapproach.garden.persistence.entity.G01TaxcorpCaseBk;
import com.zcomapproach.garden.persistence.entity.G01TaxcorpRepresentative;
import com.zcomapproach.garden.persistence.entity.G01TaxpayerCase;
import com.zcomapproach.garden.persistence.entity.G01TaxpayerCaseBk;
import com.zcomapproach.garden.persistence.entity.G01TaxpayerInfo;
import com.zcomapproach.garden.persistence.entity.G01TlcLicense;
import com.zcomapproach.garden.persistence.entity.G01User;
import com.zcomapproach.garden.persistence.entity.G01UserBk;
import com.zcomapproach.garden.persistence.entity.G01WebPost;
import com.zcomapproach.garden.persistence.entity.G01WorkTeam;
import com.zcomapproach.garden.persistence.entity.G01WorkTeamHasEmployee;
import com.zcomapproach.garden.persistence.entity.G01XmppAccount;
import com.zcomapproach.garden.persistence.entity.G02Account;
import com.zcomapproach.garden.persistence.entity.G02AccountBk;
import com.zcomapproach.garden.persistence.entity.G02ArchivedDocument;
import com.zcomapproach.garden.persistence.entity.G02ArchivedDocumentBk;
import com.zcomapproach.garden.persistence.entity.G02ArchivedFile;
import com.zcomapproach.garden.persistence.entity.G02Bill;
import com.zcomapproach.garden.persistence.entity.G02BusinessContactor;
import com.zcomapproach.garden.persistence.entity.G02ChatMessage;
import com.zcomapproach.garden.persistence.entity.G02ContactEntity;
import com.zcomapproach.garden.persistence.entity.G02ContactEntityPK;
import com.zcomapproach.garden.persistence.entity.G02ContactInfo;
import com.zcomapproach.garden.persistence.entity.G02ContactMessage;
import com.zcomapproach.garden.persistence.entity.G02DeadlineExtension;
import com.zcomapproach.garden.persistence.entity.G02DeprecatedDocumentRequirement;
import com.zcomapproach.garden.persistence.entity.G02DeprecatedProperty;
import com.zcomapproach.garden.persistence.entity.G02DeprecatedServiceTag;
import com.zcomapproach.garden.persistence.entity.G02Employee;
import com.zcomapproach.garden.persistence.entity.G02EmployeeBk;
import com.zcomapproach.garden.persistence.entity.G02Location;
import com.zcomapproach.garden.persistence.entity.G02Log;
import com.zcomapproach.garden.persistence.entity.G02Memo;
import com.zcomapproach.garden.persistence.entity.G02OfflineEmail;
import com.zcomapproach.garden.persistence.entity.G02Payment;
import com.zcomapproach.garden.persistence.entity.G02PersonalBusinessProperty;
import com.zcomapproach.garden.persistence.entity.G02PersonalProperty;
import com.zcomapproach.garden.persistence.entity.G02PostSection;
import com.zcomapproach.garden.persistence.entity.G02Schedule;
import com.zcomapproach.garden.persistence.entity.G02SecurityQna;
import com.zcomapproach.garden.persistence.entity.G02SecurityQnaPK;
import com.zcomapproach.garden.persistence.entity.G02SystemProperty;
import com.zcomapproach.garden.persistence.entity.G02SystemPropertyPK;
import com.zcomapproach.garden.persistence.entity.G02SystemSettings;
import com.zcomapproach.garden.persistence.entity.G02SystemSettingsPK;
import com.zcomapproach.garden.persistence.entity.G02TaxFilingCase;
import com.zcomapproach.garden.persistence.entity.G02TaxFilingStatus;
import com.zcomapproach.garden.persistence.entity.G02TaxFilingStatusPK;
import com.zcomapproach.garden.persistence.entity.G02TaxcorpCase;
import com.zcomapproach.garden.persistence.entity.G02TaxcorpCaseBk;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerCase;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerCaseBk;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerInfo;
import com.zcomapproach.garden.persistence.entity.G02TlcLicense;
import com.zcomapproach.garden.persistence.entity.G02User;
import com.zcomapproach.garden.persistence.entity.G02UserBk;
import com.zcomapproach.garden.persistence.entity.G02WebPost;
import com.zcomapproach.garden.persistence.entity.G02WorkTeam;
import com.zcomapproach.garden.persistence.entity.G02WorkTeamHasEmployee;
import com.zcomapproach.garden.persistence.entity.G02WorkTeamHasEmployeePK;
import com.zcomapproach.garden.persistence.entity.G02XmppAccount;
import com.zcomapproach.garden.persistence.peony.data.PeonyLogName;
import com.zcomapproach.garden.persistence.updater.G02DataUpdaterFactory;
import com.zcomapproach.garden.rose.persistence.AbstractDataServiceEJB;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import com.zcomapproach.garden.util.GardenEnvironment;
import com.zcomapproach.commons.nio.ZcaNio;
import com.zcomapproach.garden.persistence.constant.JobAssignmentStatus;
import com.zcomapproach.garden.persistence.entity.G02DailyReport;
import com.zcomapproach.garden.persistence.entity.G02DocumentTag;
import com.zcomapproach.garden.persistence.entity.G02JobAssignment;
import com.zcomapproach.garden.persistence.peony.data.PeonyPredefinedDocumentTagType;
import com.zcomapproach.garden.util.GardenSorter;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author zhijun98
 */
@Stateless
public class GardenDataTransfer extends AbstractDataServiceEJB {
    
    private static String businessOwnerAccountUuid;
    private static String initializeBusinessOwnerAccountUuid(EntityManager em) throws Exception{
        if (businessOwnerAccountUuid == null){
            HashMap<String, Object> params = new HashMap<>();
            params.put("workTitle", GardenWorkTitle.BUSINESS_OWNER.value());
            G01Employee aG01Employee = GardenJpaUtils.findEntityByNamedQuery(em, G01Employee.class, "G01Employee.findByWorkTitle", params);
            businessOwnerAccountUuid = aG01Employee.getEmployeeAccountUuid();
        }
        return businessOwnerAccountUuid;
    }
    
    public void generateClientList(){
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        
        File file = new File(GardenData.generateUUIDString() + ".txt");
        System.out.println(file.getAbsolutePath());
        try {
            utx.begin();
            em = getEntityManager();
            
            List<G01TaxpayerInfo> aG01TaxpayerInfoList = GardenJpaUtils.findAll(em, G01TaxpayerInfo.class);
            HashMap<String, G01User> aG01UserMap = new HashMap<>();
            G01User aG01User;
            for (G01TaxpayerInfo aG01TaxpayerInfo : aG01TaxpayerInfoList){
                if (TaxpayerRelationship.PRIMARY_TAXPAYER.value().equalsIgnoreCase(aG01TaxpayerInfo.getRelationships())){
                    aG01User = GardenJpaUtils.findById(em, G01User.class, aG01TaxpayerInfo.getTaxpayerUserUuid());
                    if ((aG01User != null) && (ZcaValidator.isNotNullEmpty(aG01User.getSsn()))){
                        aG01User.setMemo(aG01TaxpayerInfo.getEntityStatus());
                        aG01UserMap.put(aG01User.getSsn(), aG01User);
                    }
                }
            }
            Collection<G01User> aG01UserCollection = aG01UserMap.values();
            int id = 0;
            for (G01User g01User : aG01UserCollection){
                ZcaNio.appendTextLineToFile((++id) + "\t" + g01User.getFirstName()+ "\t" + g01User.getLastName() + "\t" + g01User.getSsn() + "\t" + g01User.getMemo(), file.getAbsolutePath());
            }
            
//            
//            List<G01TaxcorpCase> aG01TaxcorpCaseList = GardenJpaUtils.findAll(em, G01TaxcorpCase.class);
//            int id = 0;
//            for (G01TaxcorpCase aG01TaxcorpCase : aG01TaxcorpCaseList){
//                ZcaNio.appendTextLineToFile((++id) + "\t" + aG01TaxcorpCase.getCorporateName() + "\t" + aG01TaxcorpCase.getEinNumber() + "\t" + aG01TaxcorpCase.getBusinessType(), file.getAbsolutePath());
//            }

        
//        
            System.out.println("commiting...");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        System.out.println("Database query completed.");
    
    }

    public void process01() {
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
            transferAccount(em);
            
            System.out.println("transferAccount...");
            
            transferAccountBk(em);
            
            System.out.println("transferAccountBk...");
            
            transferArchivedDocument(em);
            
            System.out.println("transferArchivedDocument...");
            
            transferArchivedDocumentBk(em);
            
            System.out.println("transferArchivedDocumentBk...");
            
            transferBill(em);
            
            System.out.println("transferBill...");
            
//            transferBusinessVehicle(em);
//            
//            System.out.println("transferBusinessVehicle...");
            
            transferChatMessage(em);
            
            System.out.println("transferChatMessage...");
            
            transferContactEntity(em);
            
            System.out.println("transferContactEntity...");
            
            transferContactInfo(em);
            
            System.out.println("transferContactInfo...");
            
            transferContactMessage(em);
            
            System.out.println("transferContactMessage...");
            
            transferDeprecatedDocumentRequirement(em);
            
            System.out.println("transferDeprecatedDocumentRequirement...");
            
            transferEmployee(em);
            
            System.out.println("transferEmployee...");
            
            transferEmployeeBk(em);
            
            System.out.println("transferEmployeeBk...");
            
            transferLocation(em);
            
            System.out.println("transferLocation...");
            
            transferLog(em);
            
            System.out.println("transferLog...");
            
            transferPayment(em);
            
            System.out.println("transferPayment...");
            
            transferPersonalBusinessProperty(em);
            
            System.out.println("transferPersonalBusinessProperty...");
            
            transferPersonalProperty(em);
            
            System.out.println("transferPersonalProperty...");
            
            transferPostSection(em);
            
            System.out.println("transferPostSection...");
            
            transferDeprecatedProperty(em);
            
            System.out.println("transferDeprecatedProperty...");
            
            transferSchedule(em);
            
            System.out.println("transferSchedule...");
            
            transferSecurityQna(em);
            
            System.out.println("transferSecurityQna...");
            
            transferDeprecatedServiceTag(em);
            
            System.out.println("transferDeprecatedServiceTag...");
            
            transferSystemProperty(em);
            
            System.out.println("transferSystemProperty...");
            
//            transferTaxFiling(em);
//            
//            System.out.println("transferTaxFiling...");
//            
//            transferTaxcorpCase(em);
//            
//            System.out.println("transferTaxcorpCase...");
//            
//            transferTaxcorpCaseBk(em);
//            
//            System.out.println("transferTaxcorpCaseBk...");
//            
//            transferTaxcorpRepresentative(em);
//            
//            System.out.println("transferTaxcorpRepresentative...");
//            
//            transferTaxpayerCase(em);
//            
//            System.out.println("transferTaxpayerCase...");
//            
//            transferTaxpayerCaseBk(em);
//            
//            System.out.println("transferTaxpayerCaseBk...");
//            
//            transferTaxpayerInfo(em);
//            
//            System.out.println("transferTaxpayerInfo...");
//            
//            transferTlcLicense(em);
//            
//            System.out.println("transferTlcLicense...");
//            
//            transferUser(em);
//            
//            System.out.println("transferUser...");
//            
//            transferUserBk(em);
//            
//            System.out.println("transferUserBk...");
//            
//            transferWebPost(em);
//            
//            System.out.println("transferWebPost...");
//            
//            transferWorkRole(em);
//            
//            System.out.println("transferWorkRole...");
//            
//            transferWorkTeam(em);
//            
//            System.out.println("transferWorkTeam...");
//            
//            transferWorkTeamHasEmployee(em);
//            
//            System.out.println("transferWorkTeamHasEmployee...");
//            
//            transferWorkTeamHasWorkRole(em);
//            
//            System.out.println("transferWorkTeamHasWorkRole...");
//            
//            transferXmppAccount(em);
//            
//            System.out.println("transferXmppAccount...");
            
            System.out.println("commiting...");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        System.out.println("Database update completed.");
    }

    public void process02() {
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
//            
//            transferAccount(em);
//            
//            System.out.println("transferAccount...");
//            
//            transferAccountBk(em);
//            
//            System.out.println("transferAccountBk...");
//            
//            transferArchivedDocument(em);
//            
//            System.out.println("transferArchivedDocument...");
//            
//            transferArchivedDocumentBk(em);
//            
//            System.out.println("transferArchivedDocumentBk...");
//            
//            transferBill(em);
//            
//            System.out.println("transferBill...");
//            
////            transferBusinessVehicle(em);
////            
////            System.out.println("transferBusinessVehicle...");
//            
//            transferChatMessage(em);
//            
//            System.out.println("transferChatMessage...");
//            
//            transferContactEntity(em);
//            
//            System.out.println("transferContactEntity...");
//            
//            transferContactInfo(em);
//            
//            System.out.println("transferContactInfo...");
//            
//            transferContactMessage(em);
//            
//            System.out.println("transferContactMessage...");
//            
//            transferDeprecatedDocumentRequirement(em);
//            
//            System.out.println("transferDeprecatedDocumentRequirement...");
//            
//            transferEmployee(em);
//            
//            System.out.println("transferEmployee...");
//            
//            transferEmployeeBk(em);
//            
//            System.out.println("transferEmployeeBk...");
//            
//            transferLocation(em);
//            
//            System.out.println("transferLocation...");
//            
//            transferLog(em);
//            
//            System.out.println("transferLog...");
//            
//            transferPayment(em);
//            
//            System.out.println("transferPayment...");
//            
//            transferPersonalBusinessProperty(em);
//            
//            System.out.println("transferPersonalBusinessProperty...");
//            
//            transferPersonalProperty(em);
//            
//            System.out.println("transferPersonalProperty...");
//            
//            transferPostSection(em);
//            
//            System.out.println("transferPostSection...");
//            
//            transferDeprecatedProperty(em);
//            
//            System.out.println("transferDeprecatedProperty...");
//            
//            transferSchedule(em);
//            
//            System.out.println("transferSchedule...");
//            
//            transferSecurityQna(em);
//            
//            System.out.println("transferSecurityQna...");
//            
//            transferDeprecatedServiceTag(em);
//            
//            System.out.println("transferDeprecatedServiceTag...");
//            
//            transferSystemProperty(em);
//            
//            System.out.println("transferSystemProperty...");
//            
            transferTaxFiling(em);
            
            System.out.println("transferTaxFiling...");
            
            transferTaxcorpCase(em);
            
            System.out.println("transferTaxcorpCase...");
            
            transferTaxcorpCaseBk(em);
            
            System.out.println("transferTaxcorpCaseBk...");
            
            transferTaxcorpRepresentative(em);
            
            System.out.println("transferTaxcorpRepresentative...");
            
            transferTaxpayerCase(em);
            
            System.out.println("transferTaxpayerCase...");
            
            transferTaxpayerCaseBk(em);
            
            System.out.println("transferTaxpayerCaseBk...");
            
            transferTaxpayerInfo(em);
            
            System.out.println("transferTaxpayerInfo...");
            
//            transferTlcLicense(em);
//            
//            System.out.println("transferTlcLicense...");
//            
//            transferUser(em);
//            
//            System.out.println("transferUser...");
//            
//            transferUserBk(em);
//            
//            System.out.println("transferUserBk...");
//            
//            transferWebPost(em);
//            
//            System.out.println("transferWebPost...");
//            
//            transferWorkRole(em);
//            
//            System.out.println("transferWorkRole...");
//            
//            transferWorkTeam(em);
//            
//            System.out.println("transferWorkTeam...");
//            
//            transferWorkTeamHasEmployee(em);
//            
//            System.out.println("transferWorkTeamHasEmployee...");
//            
//            transferWorkTeamHasWorkRole(em);
//            
//            System.out.println("transferWorkTeamHasWorkRole...");
//            
//            transferXmppAccount(em);
//            
//            System.out.println("transferXmppAccount...");
            
            System.out.println("commiting...");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        System.out.println("Database update completed.");
    }

    public void process03() {
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
//            transferAccount(em);
//            
//            System.out.println("transferAccount...");
//            
//            transferAccountBk(em);
//            
//            System.out.println("transferAccountBk...");
//            
//            transferArchivedDocument(em);
//            
//            System.out.println("transferArchivedDocument...");
//            
//            transferArchivedDocumentBk(em);
//            
//            System.out.println("transferArchivedDocumentBk...");
//            
//            transferBill(em);
//            
//            System.out.println("transferBill...");
//            
////            transferBusinessVehicle(em);
////            
////            System.out.println("transferBusinessVehicle...");
//            
//            transferChatMessage(em);
//            
//            System.out.println("transferChatMessage...");
//            
//            transferContactEntity(em);
//            
//            System.out.println("transferContactEntity...");
//            
//            transferContactInfo(em);
//            
//            System.out.println("transferContactInfo...");
//            
//            transferContactMessage(em);
//            
//            System.out.println("transferContactMessage...");
//            
//            transferDeprecatedDocumentRequirement(em);
//            
//            System.out.println("transferDeprecatedDocumentRequirement...");
//            
//            transferEmployee(em);
//            
//            System.out.println("transferEmployee...");
//            
//            transferEmployeeBk(em);
//            
//            System.out.println("transferEmployeeBk...");
//            
//            transferLocation(em);
//            
//            System.out.println("transferLocation...");
//            
//            transferLog(em);
//            
//            System.out.println("transferLog...");
//            
//            transferPayment(em);
//            
//            System.out.println("transferPayment...");
//            
//            transferPersonalBusinessProperty(em);
//            
//            System.out.println("transferPersonalBusinessProperty...");
//            
//            transferPersonalProperty(em);
//            
//            System.out.println("transferPersonalProperty...");
//            
//            transferPostSection(em);
//            
//            System.out.println("transferPostSection...");
//            
//            transferDeprecatedProperty(em);
//            
//            System.out.println("transferDeprecatedProperty...");
//            
//            transferSchedule(em);
//            
//            System.out.println("transferSchedule...");
//            
//            transferSecurityQna(em);
//            
//            System.out.println("transferSecurityQna...");
//            
//            transferDeprecatedServiceTag(em);
//            
//            System.out.println("transferDeprecatedServiceTag...");
//            
//            transferSystemProperty(em);
//            
//            System.out.println("transferSystemProperty...");
//            
//            transferTaxFiling(em);
//            
//            System.out.println("transferTaxFiling...");
//            
//            transferTaxcorpCase(em);
//            
//            System.out.println("transferTaxcorpCase...");
//            
//            transferTaxcorpCaseBk(em);
//            
//            System.out.println("transferTaxcorpCaseBk...");
//            
//            transferTaxcorpRepresentative(em);
//            
//            System.out.println("transferTaxcorpRepresentative...");
//            
//            transferTaxpayerCase(em);
//            
//            System.out.println("transferTaxpayerCase...");
//            
//            transferTaxpayerCaseBk(em);
//            
//            System.out.println("transferTaxpayerCaseBk...");
//            
//            transferTaxpayerInfo(em);
//            
//            System.out.println("transferTaxpayerInfo...");
            
            transferTlcLicense(em);
            
            System.out.println("transferTlcLicense...");
            
            transferUser(em);
            
            System.out.println("transferUser...");
            
            transferUserBk(em);
            
            System.out.println("transferUserBk...");
            
            transferWebPost(em);
            
            System.out.println("transferWebPost...");
            
            transferWorkRole(em);
            
            System.out.println("transferWorkRole...");
            
            transferWorkTeam(em);
            
            System.out.println("transferWorkTeam...");
            
            transferWorkTeamHasEmployee(em);
            
            System.out.println("transferWorkTeamHasEmployee...");
            
            transferWorkTeamHasWorkRole(em);
            
            System.out.println("transferWorkTeamHasWorkRole...");
            
            transferXmppAccount(em);
            
            System.out.println("transferXmppAccount...");
            
            System.out.println("committing...");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        System.out.println("Database update completed.");
    }
    
    public void fixG01FisicalTypoIssue(){
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
            System.out.println("fixG01FisicalTypoIssue...");
            
            fixG01FisicalTypoIssueHelper(em);
            
            System.out.println("committing...");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        System.out.println("Database update completed.");
    }
    
    public void recoverChineseMemoForTaxFiling(){
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
            System.out.println("recoverChineseMemoForTaxFiling...(demand G02 database ready)");
            
            recoverChineseMemoForTaxFilingHelper(em);
            
            System.out.println("commit completed");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    private void recoverChineseMemoForTaxFilingHelper(EntityManager em) throws IOException, ClassNotFoundException, ZcaEntityValidationException{
        Path objFilePath = Paths.get("C:\\temp\\constructMemoMapForTaxFilingHelper\\memoMap.obj");
        HashMap<String, String> temp = (HashMap<String, String>)ZcaNio.deserializeObject(objFilePath);
        List<G02TaxFilingCase> aG02TaxFilingCaseList = GardenJpaUtils.findAll(em, G02TaxFilingCase.class);
        String memoText;
        G02Log log;
        int counter = 0;
        for (G02TaxFilingCase aG02TaxFilingCase : aG02TaxFilingCaseList){
            memoText = temp.get(aG02TaxFilingCase.getTaxFilingUuid());
            if (ZcaValidator.isNotNullEmpty(memoText)){
                if (!memoText.equalsIgnoreCase(aG02TaxFilingCase.getMemo())){
                    //save log for other information
                    log = new G02Log();
                    log.setLogUuid(GardenData.generateUUIDString());
                    log.setLogName(PeonyLogName.DELETED_TAX_FILING_MEMO.name());
                    log.setLogMessage("Memo: " + aG02TaxFilingCase.getMemo());
                    log.setOperatorAccountUuid(businessOwnerAccountUuid);
                    log.setEntityType(GardenEntityType.convertEnumNameToType(aG02TaxFilingCase.getEntityType()).name());
                    log.setEntityUuid(aG02TaxFilingCase.getEntityUuid());
                    log.setLoggedEntityUuid(aG02TaxFilingCase.getTaxFilingUuid());
                    log.setLoggedEntityType(GardenEntityType.TAX_FILING_CASE.name());
                    //memo.setEntityStatus();
                    log.setOperatorAccountUuid(businessOwnerAccountUuid);
                    log.setCreated(new Date());

                    GardenJpaUtils.storeEntity(em, G02Log.class, log, log.getLogUuid(),
                            G02DataUpdaterFactory.getSingleton().getG02LogUpdater());

                    memoText = aG02TaxFilingCase.getMemo() + " >>> Old memo: " + memoText;
                    if (memoText.length() > 450){
                        memoText = memoText.replaceAll("?", "");
                        if (memoText.length() > 450){
                            memoText = memoText.substring(0, 400)+ "...";
                            System.out.println(">>> Old memo: " + (++counter) + " - " + aG02TaxFilingCase.getMemo());
                            System.out.println("--- New memo: " + (++counter) + " - " + memoText);
                        }
                    }

                    aG02TaxFilingCase.setMemo(memoText);
                    //save memo...
                    GardenJpaUtils.storeEntity(em, G02TaxFilingCase.class, aG02TaxFilingCase, aG02TaxFilingCase.getTaxFilingUuid(),
                            G02DataUpdaterFactory.getSingleton().getG02TaxFilingCaseUpdater());
                }
            }
        }//for-loop
    }
    
    public void constructMemoMapForTaxFiling(){
    
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
            System.out.println("constructMemoMapForTaxFiling...(demand G02 database ready)");
            
            Path objFilePath = Paths.get("C:\\temp\\constructMemoMapForTaxFilingHelper\\memoMap.obj");
            constructMemoMapForTaxFilingHelper(em, objFilePath);
            
            printOutMemoMapForTaxFilingHelper(objFilePath);
            
            System.out.println("commit completed");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    private void printOutMemoMapForTaxFilingHelper(Path objFilePath){
        try {
            HashMap<String, String> temp = (HashMap<String, String>)ZcaNio.deserializeObject(objFilePath);
            Set<String> keys = temp.keySet();
            for (String key : keys){
                System.out.println(">>> " + key);
                System.out.println("--- " + temp.get(key));
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void constructMemoMapForTaxFilingHelper(EntityManager em, Path objFilePath) {
        List<G02TaxFilingCase> aG02TaxFilingCaseList = GardenJpaUtils.findAll(em, G02TaxFilingCase.class);
        HashMap<String, String> temp = new HashMap<>();
        String memo;
        for (G02TaxFilingCase aG02TaxFilingCase : aG02TaxFilingCaseList){
            memo = aG02TaxFilingCase.getMemo();
            if (ZcaValidator.isNotNullEmpty(memo)){
                temp.put(aG02TaxFilingCase.getTaxFilingUuid(), memo);
            }
        }
        try {
            ZcaNio.serializeObject(temp, objFilePath, true);
        } catch (IOException ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * todo: cannot find any redundant records
     */
    public void fixRedundantMemoText(){
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
            System.out.println("fixRedundantMemoText...(demand G02 database ready)");
            
            fixRedundantMemoTextHelper(em);
            
            System.out.println("committing saving...");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    private void fixRedundantMemoTextHelper(EntityManager em) {
        List<G02TaxFilingCase> aG02TaxFilingCaseList = GardenJpaUtils.findAll(em, G02TaxFilingCase.class);
        String memoText, memoText01, memoText02;
        int half;
        int index = 0;
        for (G02TaxFilingCase aG02TaxFilingCase : aG02TaxFilingCaseList){
            memoText = aG02TaxFilingCase.getMemo();
            if (ZcaValidator.isNotNullEmpty(memoText)){
                half = memoText.length()/2;
                memoText01 = memoText.substring(0, half);
                memoText02 = memoText.substring(half);
//                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + (++index));
//                System.out.println("--- memoText01: " + memoText01);
//                System.out.println("--- memoText02: " + memoText02);
                if (memoText01.equalsIgnoreCase(memoText02)){
                    System.out.println(">>> memoText: " + memoText);
                }
//                System.out.println("------------------------------------------------------");
            }
        }
    }
    
    /**
     * This is only for Taxcorp but not taxpayer.
     */
    public void transferG02TaxFilingCaseFromMemoForTaxcorp(){
        List<G02Memo> deletedMemoList = null;
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
            System.out.println("transferG02TaxFilingCaseFromMemoForTaxcorp...(demand G02 database ready)");
            
            deletedMemoList = transferG02TaxFilingCaseFromMemoForTaxcorpHelper(em);
            
            System.out.println("committing saving...");
            utx.commit();
            
        } catch (Exception ex) {
            deletedMemoList = null;
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        /**
         * DELETE....
         */
        utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
            System.out.println("deleteG01MemoForG02TaxFilingCases...(demand G02 database ready)");
            
            deleteG01MemoForG02TaxFilingCases(em, deletedMemoList);
            
            System.out.println("committing deletion...");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        System.out.println("Database update completed.");
    }
    
    private void deleteG01MemoForG02TaxFilingCases(EntityManager em, List<G02Memo> deletedMemoList){
        if (deletedMemoList.isEmpty()){
            System.out.println("No any memo was deleted completed.");
        }
        for (G02Memo aG02Memo : deletedMemoList){
            System.out.println("delete memo..." + aG02Memo.getMemoUuid());
            GardenJpaUtils.deleteEntity(em, G02Memo.class, aG02Memo.getMemoUuid());
        }
    }
    
    private List<G02Memo> transferG02TaxFilingCaseFromMemoForTaxcorpHelper(EntityManager em) {
        List<G02Memo> deletedMemoList = new ArrayList<>();
        List<G02Memo> aG02MemoList = GardenJpaUtils.findAll(em, G02Memo.class);
        if (aG02MemoList != null){
            aG02MemoList = GardenSorter.sortMemoListByTimestamp(aG02MemoList, false);
            G02TaxFilingCase theG02TaxFilingCase;
            /**
             * Prepare for data structures
             */
            HashMap<String, List<G02Memo>> memoCache = new HashMap<>();
            List<G02TaxFilingCase> aG02TaxFilingCaseList = new ArrayList<>();
            List<G02Memo> memos;
            for (G02Memo aG02Memo : aG02MemoList){
                if (GardenEntityType.TAX_FILING_CASE.name().equalsIgnoreCase(aG02Memo.getEntityType())){
                    theG02TaxFilingCase = GardenJpaUtils.findById(em, G02TaxFilingCase.class, aG02Memo.getEntityUuid());
                    if (theG02TaxFilingCase != null){
                        memos = memoCache.get(theG02TaxFilingCase.getTaxFilingUuid());
                        if (memos == null){
                            memos = new ArrayList();
                        }
                        memos.add(aG02Memo);
                        deletedMemoList.add(aG02Memo);

                        System.out.println("cache memo for tax-filing case..." + theG02TaxFilingCase.getTaxFilingUuid());
                        memoCache.put(theG02TaxFilingCase.getTaxFilingUuid(), memos);
                        aG02TaxFilingCaseList.add(theG02TaxFilingCase);
                    }
                }//if
            }//for-loop
            /**
             * Transfer memos
             */
            G02Log log;
            String memoText;
            for (G02TaxFilingCase aG02TaxFilingCase : aG02TaxFilingCaseList){
                aG02MemoList = memoCache.get(aG02TaxFilingCase.getTaxFilingUuid());
                if ((aG02MemoList == null) || (aG02MemoList.isEmpty())){
                    //do nothing
                }else{
                    System.out.println("transfer memo..." + aG02TaxFilingCase.getTaxFilingUuid());
                    try {
                        for (G02Memo aG02Memo : aG02MemoList){
                            memoText = aG02Memo.getMemo();
                            memoText = memoText.replace("[Dealine Memo]", "");
                            memoText = memoText.trim();
                            //save log for other information
                            log = new G02Log();
                            log.setLogUuid(GardenData.generateUUIDString());
                            log.setLogName(PeonyLogName.UPDATE_TAX_FILING_MEMO.name());
                            log.setLogMessage("Memo: " + memoText);
                            log.setOperatorAccountUuid(aG02Memo.getOperatorAccountUuid());
                            log.setEntityType(GardenEntityType.convertEnumNameToType(aG02TaxFilingCase.getEntityType()).name());
                            log.setEntityUuid(aG02TaxFilingCase.getEntityUuid());
                            log.setLoggedEntityUuid(aG02TaxFilingCase.getTaxFilingUuid());
                            log.setLoggedEntityType(GardenEntityType.TAX_FILING_CASE.name());
                            //memo.setEntityStatus();
                            log.setOperatorAccountUuid(aG02Memo.getOperatorAccountUuid());
                            log.setCreated(aG02Memo.getTimestamp());
                            
                            GardenJpaUtils.storeEntity(em, G02Log.class, log, log.getLogUuid(),
                                    G02DataUpdaterFactory.getSingleton().getG02LogUpdater());
                            //save log...
                            if (ZcaValidator.isNullEmpty(aG02TaxFilingCase.getMemo())){
                                aG02TaxFilingCase.setMemo(memoText);
                            }else{
                                aG02TaxFilingCase.setMemo(aG02TaxFilingCase.getMemo() + " || " + memoText);
                            }
                        }
                        
                        if (aG02TaxFilingCase.getTaxFilingUuid().equalsIgnoreCase("17f1ea6ez06a3z42a0z84e8z9f6b5c487bee")){
                            System.out.println(">>>> " + aG02TaxFilingCase.getMemo());
                            System.out.println(">>>> " + aG02TaxFilingCase.getMemo().length());
                            aG02TaxFilingCase.setMemo("欠费 || called & emailed 09/05/2019 || 等收集所有信息后需重新计算销售税。Grubhub网络订单金额较大，共分四个账号：Sun Lok Kitchen，Wah Fung，Happy Family，NO.1 Flower。其中NO.1 Flower账号为每月一张大额支票（上万）入账。现缺8月份Grubhub网络订单月结单。另外，已问客户提供刷卡机月结单。最后，客户提供的现金收入和小费金额为全月总计，包含店面和网络订单。 09/09/2019");
                        }
                        
                        //save memo...
                        GardenJpaUtils.storeEntity(em, G02TaxFilingCase.class, aG02TaxFilingCase, aG02TaxFilingCase.getTaxFilingUuid(),
                                G02DataUpdaterFactory.getSingleton().getG02TaxFilingCaseUpdater());
                    } catch (ZcaEntityValidationException ex) {
                        Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }//for-loop: save memos with logging
        }
        return deletedMemoList;
    }
    
    /**
     * This is only for Taxcorp but not taxpayer.
     */
    public void transferG02TaxFilingCaseFromExtensionForTaxcorp(){
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
            System.out.println("transferG02TaxFilingCaseFromExtension...(demand G02 database ready)");
            
            transferG02TaxFilingCaseFromExtensionForTaxcorpHelper(em);
            
            System.out.println("committing...");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        System.out.println("Database update completed.");
    }
    
    private void transferG02TaxFilingCaseFromExtensionForTaxcorpHelper(EntityManager em) {
        List<G02DeadlineExtension> aG02DeadlineExtensionList = GardenJpaUtils.findAll(em, G02DeadlineExtension.class);
        if (aG02DeadlineExtensionList != null){
            aG02DeadlineExtensionList = GardenSorter.sortG02DeadlineExtensionList(aG02DeadlineExtensionList, false);
            G02TaxFilingCase aG02TaxFilingCase;
            G02Log log;
            String logMessage;
            for (G02DeadlineExtension aG02DeadlineExtension : aG02DeadlineExtensionList){
                aG02TaxFilingCase = GardenJpaUtils.findById(em, G02TaxFilingCase.class, aG02DeadlineExtension.getEntityUuid());
                if (aG02TaxFilingCase != null){
                    System.out.println("transfer extensions..." + aG02TaxFilingCase.getTaxFilingUuid());
                    //save extension...
                    aG02TaxFilingCase.setExtension(aG02DeadlineExtension.getExtensionDate());
                    //save log for other information
                    log = new G02Log();
                    log.setLogUuid(GardenData.generateUUIDString());
                    log.setLogName(PeonyLogName.STORED_DEADLINE_EXTENSION.name());
                    logMessage = "Extended to " + ZcaCalendar.convertToMMddyyyy(aG02DeadlineExtension.getExtensionDate(), "-");
                    if (ZcaValidator.isNullEmpty(aG02DeadlineExtension.getExtensionMemo())){
                        log.setLogMessage(logMessage);
                    }else{
                        log.setLogMessage(logMessage + " ("+aG02DeadlineExtension.getExtensionMemo()+")");
                    }
                    log.setOperatorAccountUuid(aG02DeadlineExtension.getOperatorAccountUuid());
                    log.setEntityType(GardenEntityType.convertEnumNameToType(aG02TaxFilingCase.getEntityType()).name());
                    log.setEntityUuid(aG02TaxFilingCase.getEntityUuid());
                    log.setLoggedEntityUuid(aG02TaxFilingCase.getTaxFilingUuid());
                    log.setLoggedEntityType(GardenEntityType.TAX_FILING_CASE.name());
                    //memo.setEntityStatus();
                    log.setOperatorAccountUuid(aG02DeadlineExtension.getOperatorAccountUuid());
                    log.setCreated(aG02DeadlineExtension.getTimestamp());
                    //Save all data....
                    try {
                        GardenJpaUtils.storeEntity(em, G02TaxFilingCase.class, aG02TaxFilingCase, aG02TaxFilingCase.getTaxFilingUuid(),
                                G02DataUpdaterFactory.getSingleton().getG02TaxFilingCaseUpdater());

                        GardenJpaUtils.storeEntity(em, G02Log.class, log, log.getLogUuid(),
                                G02DataUpdaterFactory.getSingleton().getG02LogUpdater());

                    } catch (ZcaEntityValidationException ex) {
                        Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
            }
        }
    }
    
    public void transferExtensionsIntoG02TaxpayerCase(){
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
            System.out.println("transferExtensionsIntoG02TaxpayerCase...transfer extensions from G02DeadlineExtension G02TaxFilingCase and G02Memo");
            
            transferExtensionsIntoG02TaxpayerCaseHelper(em);
            
            System.out.println("committing...");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        System.out.println("Database update completed.");
    }
    
    private void transferExtensionsIntoG02TaxpayerCaseHelper(EntityManager em){
        HashMap<String, Object> params = new HashMap<>();
        params.put("entityType", GardenEntityType.TAXPAYER_CASE.name());
        List<G02DeadlineExtension> aG02DeadlineExtensionList = GardenJpaUtils.findEntityListByNamedQuery(em, G02DeadlineExtension.class, "G02DeadlineExtension.findByEntityType", params);
        if (aG02DeadlineExtensionList != null){
            aG02DeadlineExtensionList = GardenSorter.sortG02DeadlineExtensionList(aG02DeadlineExtensionList, false);
            G02TaxpayerCase aG02TaxpayerCase;
            G02Log log;
            String logMessage;
            for (G02DeadlineExtension aG02DeadlineExtension : aG02DeadlineExtensionList){
                aG02TaxpayerCase = GardenJpaUtils.findById(em, G02TaxpayerCase.class, aG02DeadlineExtension.getEntityUuid());
                if (aG02TaxpayerCase == null){
                    G02TaxpayerCaseBk aG02TaxpayerCaseBk = GardenJpaUtils.findById(em, G02TaxpayerCaseBk.class, aG02DeadlineExtension.getEntityUuid());
                    if (aG02TaxpayerCaseBk != null){
                        System.out.println("transfer extensions for BK-taxpayerCaseUuid = " + aG02TaxpayerCaseBk.getTaxpayerCaseUuid());
                        //save extension...
                        aG02TaxpayerCaseBk.setExtension(aG02DeadlineExtension.getExtensionDate());
                        //save log for other information
                        log = new G02Log();
                        log.setLogUuid(GardenData.generateUUIDString());
                        log.setLogName(PeonyLogName.STORED_DEADLINE_EXTENSION.name());
                        logMessage = "Extended to " + ZcaCalendar.convertToMMddyyyy(aG02DeadlineExtension.getExtensionDate(), "-");
                        if (ZcaValidator.isNullEmpty(aG02DeadlineExtension.getExtensionMemo())){
                            log.setLogMessage(logMessage);
                        }else{
                            log.setLogMessage(logMessage + " ("+aG02DeadlineExtension.getExtensionMemo()+")");
                        }
                        log.setOperatorAccountUuid(aG02DeadlineExtension.getOperatorAccountUuid());
                        log.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
                        log.setEntityUuid(aG02TaxpayerCaseBk.getTaxpayerCaseUuid());
                        log.setLoggedEntityUuid(aG02TaxpayerCaseBk.getTaxpayerCaseUuid());
                        log.setLoggedEntityType(GardenEntityType.TAXPAYER_CASE.name());
                        //memo.setEntityStatus();
                        log.setOperatorAccountUuid(aG02DeadlineExtension.getOperatorAccountUuid());
                        log.setCreated(aG02DeadlineExtension.getTimestamp());
                        //Save all data....
                        try {
                            GardenJpaUtils.storeEntity(em, G02TaxpayerCaseBk.class, aG02TaxpayerCaseBk, aG02TaxpayerCaseBk.getTaxpayerCaseUuid(),
                                    G02DataUpdaterFactory.getSingleton().getG02TaxpayerCaseBkUpdater());

                            GardenJpaUtils.storeEntity(em, G02Log.class, log, log.getLogUuid(),
                                    G02DataUpdaterFactory.getSingleton().getG02LogUpdater());

                        } catch (ZcaEntityValidationException ex) {
                            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }else{
                    System.out.println("transfer extensions for taxpayerCaseUuid = " + aG02TaxpayerCase.getTaxpayerCaseUuid());
                    //save extension...
                    aG02TaxpayerCase.setExtension(aG02DeadlineExtension.getExtensionDate());
                    //save log for other information
                    log = new G02Log();
                    log.setLogUuid(GardenData.generateUUIDString());
                    log.setLogName(PeonyLogName.STORED_DEADLINE_EXTENSION.name());
                    logMessage = "Extended to " + ZcaCalendar.convertToMMddyyyy(aG02DeadlineExtension.getExtensionDate(), "-");
                    if (ZcaValidator.isNullEmpty(aG02DeadlineExtension.getExtensionMemo())){
                        log.setLogMessage(logMessage);
                    }else{
                        log.setLogMessage(logMessage + " ("+aG02DeadlineExtension.getExtensionMemo()+")");
                    }
                    log.setOperatorAccountUuid(aG02DeadlineExtension.getOperatorAccountUuid());
                    log.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
                    log.setEntityUuid(aG02TaxpayerCase.getTaxpayerCaseUuid());
                    log.setLoggedEntityUuid(aG02TaxpayerCase.getTaxpayerCaseUuid());
                    log.setLoggedEntityType(GardenEntityType.TAXPAYER_CASE.name());
                    //memo.setEntityStatus();
                    log.setOperatorAccountUuid(aG02DeadlineExtension.getOperatorAccountUuid());
                    log.setCreated(aG02DeadlineExtension.getTimestamp());
                    //Save all data....
                    try {
                        GardenJpaUtils.storeEntity(em, G02TaxpayerCase.class, aG02TaxpayerCase, aG02TaxpayerCase.getTaxpayerCaseUuid(),
                                G02DataUpdaterFactory.getSingleton().getG02TaxpayerCaseUpdater());

                        GardenJpaUtils.storeEntity(em, G02Log.class, log, log.getLogUuid(),
                                G02DataUpdaterFactory.getSingleton().getG02LogUpdater());

                    } catch (ZcaEntityValidationException ex) {
                        Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
            }
        }
    }
    
    public void transferG02TaxFilingCaseFromStatus(){
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
            System.out.println("transferG02TaxFilingCaseFromStatus...(demand G02 database ready)");
            
            transferG02TaxFilingCaseFromStatusHelper(em);
            
            System.out.println("committing...");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        System.out.println("Database update completed.");
    }
    
    private void transferG02TaxFilingCaseFromStatusHelper(EntityManager em) {
        List<G02TaxFilingStatus> aG02TaxFilingStatusList = GardenJpaUtils.findAll(em, G02TaxFilingStatus.class);
        if (aG02TaxFilingStatusList != null){
            G02TaxFilingCase aG02TaxFilingCase;
            G02Log log;
            GardenTaxFilingStatus status;
            for (G02TaxFilingStatus aG02TaxFilingStatus : aG02TaxFilingStatusList){
                aG02TaxFilingCase = GardenJpaUtils.findById(em, G02TaxFilingCase.class, aG02TaxFilingStatus.getG02TaxFilingStatusPK().getTaxFilingUuid());
                if (aG02TaxFilingCase != null){
                    System.out.println("transfer dates..." + aG02TaxFilingCase.getTaxFilingUuid());
                    status = GardenTaxFilingStatus.convertEnumNameToType(aG02TaxFilingStatus.getG02TaxFilingStatusPK().getTaxFilingStatus());
                    switch(status){
                        case RECEIVED:
                            aG02TaxFilingCase.setReceived(aG02TaxFilingStatus.getStatusDate());
                            break;
                        case PREPARED:
                            aG02TaxFilingCase.setPrepared(aG02TaxFilingStatus.getStatusDate());
                            break;
                        case COMPLETED:
                            aG02TaxFilingCase.setCompleted(aG02TaxFilingStatus.getStatusDate());
                            break;
                        case EXT_EFILE:
                            aG02TaxFilingCase.setExtEfile(aG02TaxFilingStatus.getStatusDate());
                            break;
                        case PICKUP:
                            aG02TaxFilingCase.setPickupOrEfile(aG02TaxFilingStatus.getStatusDate());
                            break;
                        default:
                            status = null;
                    }
                    if (status != null){
                        log = new G02Log();
                        log.setLogUuid(GardenData.generateUUIDString());
                        log.setLogName(PeonyLogName.UPDATE_TAX_FILING_STATUS.name());
                        if (ZcaValidator.isNullEmpty(aG02TaxFilingStatus.getStatusMemo())){
                            log.setLogMessage(status.value());
                        }else{
                            log.setLogMessage(status.value() + " ("+aG02TaxFilingStatus.getStatusMemo()+")");
                        }
                        log.setOperatorAccountUuid(aG02TaxFilingStatus.getOperatorAccountUuid());
                        log.setEntityType(GardenEntityType.convertEnumNameToType(aG02TaxFilingCase.getEntityType()).name());
                        log.setEntityUuid(aG02TaxFilingCase.getEntityUuid());
                        log.setLoggedEntityUuid(aG02TaxFilingCase.getTaxFilingUuid());
                        log.setLoggedEntityType(GardenEntityType.TAX_FILING_CASE.name());
                        //memo.setEntityStatus();
                        log.setOperatorAccountUuid(aG02TaxFilingStatus.getOperatorAccountUuid());
                        log.setCreated(aG02TaxFilingStatus.getCreated());
                        //Save all data....
                        try {
                            GardenJpaUtils.storeEntity(em, G02TaxFilingCase.class, aG02TaxFilingCase, aG02TaxFilingCase.getTaxFilingUuid(),
                                    G02DataUpdaterFactory.getSingleton().getG02TaxFilingCaseUpdater());

                            GardenJpaUtils.storeEntity(em, G02Log.class, log, log.getLogUuid(),
                                    G02DataUpdaterFactory.getSingleton().getG02LogUpdater());

                        } catch (ZcaEntityValidationException ex) {
                            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }
    
    public void updateTaxcorpContactorsBirthday(){
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
            System.out.println("updateTaxcorpContactorsBirthday...(demand G02 database ready)");
            
            updateTaxcorpContactorsBirthdayHelper(em);
            
            System.out.println("committing...");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        System.out.println("Database update completed.");
    }

    private void updateTaxcorpContactorsBirthdayHelper(EntityManager em) {
        HashMap<String, Date> birthdayMap = new HashMap<>();
        List<G02User> aG02UserList = GardenJpaUtils.findAll(em, G02User.class);
        for (G02User aG02User : aG02UserList){
            if ((aG02User.getBirthday() != null) && (ZcaValidator.isNotNullEmpty(aG02User.getSsn()))){
                birthdayMap.put(aG02User.getSsn(), aG02User.getBirthday());
            }
        }
        List<G02BusinessContactor> aG02BusinessContactorList = GardenJpaUtils.findAll(em, G02BusinessContactor.class);
        Date birthday;
        for (G02BusinessContactor aG02BusinessContactor : aG02BusinessContactorList){
            birthday = birthdayMap.get(aG02BusinessContactor.getSsn());
            if (birthday != null){
                System.out.println("aG02BusinessContactor: " + aG02BusinessContactor.getSsn());
                aG02BusinessContactor.setBirthday(birthday);
                try {
                    GardenJpaUtils.storeEntity(em, G02BusinessContactor.class, aG02BusinessContactor, aG02BusinessContactor.getBusinessContactorUuid(),
                            G02DataUpdaterFactory.getSingleton().getG02BusinessContactorUpdater());
                } catch (ZcaEntityValidationException ex) {
                    Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void updateTaxFilingEntityFields() {
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
            System.out.println("updateTaxFilingEntityFields...");
            
            updateTaxFilingEntityFieldsHelper(em);
            
            System.out.println("committing...");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        System.out.println("Database update completed.");
    }
    
    private void transferAccountBk(EntityManager em) throws Exception{
        List<G01AccountBk> aG01AccountBkList = GardenJpaUtils.findAll(em, G01AccountBk.class);
        if (aG01AccountBkList != null){
            G02AccountBk aG02AccountBk;
            for (G01AccountBk aG01AccountBk : aG01AccountBkList){
                aG02AccountBk = new G02AccountBk();

                aG02AccountBk.setAccountUuid(aG01AccountBk.getAccountUuid());
                aG02AccountBk.setLoginName(aG01AccountBk.getLoginName());
                aG02AccountBk.setAccountEmail(aG01AccountBk.getAccountEmail());
                aG02AccountBk.setMobilePhone(aG01AccountBk.getMobilePhone());
                aG02AccountBk.setEncryptedPassword(aG01AccountBk.getEncryptedPassword());
                aG02AccountBk.setPassword(aG01AccountBk.getPassword());
                aG02AccountBk.setAccountStatus(aG01AccountBk.getAccountStatus());
                aG02AccountBk.setAccountConfirmationCode(aG01AccountBk.getAccountConfirmationCode());
                aG02AccountBk.setWebLanguage(aG01AccountBk.getWebLanguage());
                aG02AccountBk.setEntityStatus(aG01AccountBk.getEntityStatus());
                aG02AccountBk.setCreated(aG01AccountBk.getCreated());
                aG02AccountBk.setUpdated(aG01AccountBk.getUpdated());

                GardenJpaUtils.storeEntity(em, G02AccountBk.class, aG02AccountBk, aG02AccountBk.getAccountUuid(), G02DataUpdaterFactory.getSingleton().getG02AccountBkUpdater());
            }
        }
    }
    
    private void transferAccount(EntityManager em) throws Exception{
        List<G01Account> aG01AccountList = GardenJpaUtils.findAll(em, G01Account.class);
        if (aG01AccountList != null){
            G02Account aG02Account;
            for (G01Account aG01Account : aG01AccountList){
                aG02Account = new G02Account();

                aG02Account.setAccountUuid(aG01Account.getAccountUuid());
                aG02Account.setLoginName(aG01Account.getLoginName());
                aG02Account.setAccountEmail(aG01Account.getAccountEmail());
                aG02Account.setMobilePhone(aG01Account.getMobilePhone());
                aG02Account.setEncryptedPassword(aG01Account.getEncryptedPassword());
                aG02Account.setPassword(aG01Account.getPassword());
                aG02Account.setAccountStatus(aG01Account.getAccountStatus());
                aG02Account.setAccountConfirmationCode(aG01Account.getAccountConfirmationCode());
                aG02Account.setWebLanguage(aG01Account.getWebLanguage());
                aG02Account.setEntityStatus(aG01Account.getEntityStatus());
                aG02Account.setCreated(aG01Account.getCreated());
                aG02Account.setUpdated(aG01Account.getUpdated());

                GardenJpaUtils.storeEntity(em, G02Account.class, aG02Account, aG02Account.getAccountUuid(), G02DataUpdaterFactory.getSingleton().getG02AccountUpdater());
            }
        }
    }

    private void transferArchivedDocument(EntityManager em) throws Exception {
        List<G01ArchivedDocument> aG01ArchivedDocumentList = GardenJpaUtils.findAll(em, G01ArchivedDocument.class);
        if (aG01ArchivedDocumentList != null){
            G02ArchivedDocument aG02ArchivedDocument;
            for (G01ArchivedDocument aG01ArchivedDocument : aG01ArchivedDocumentList){
                aG02ArchivedDocument = new G02ArchivedDocument();

                aG02ArchivedDocument.setArchivedDocumentUuid(aG01ArchivedDocument.getArchivedDocumentUuid());
                aG02ArchivedDocument.setFileName(aG01ArchivedDocument.getFileName());
                aG02ArchivedDocument.setFileLocation(aG01ArchivedDocument.getFileLocation());
                aG02ArchivedDocument.setFileStatus(aG01ArchivedDocument.getFileStatus());
                aG02ArchivedDocument.setFileTimestamp(aG01ArchivedDocument.getFileTimestamp());
                aG02ArchivedDocument.setProviderUuid(aG01ArchivedDocument.getProviderUuid());
                aG02ArchivedDocument.setEntityType(aG01ArchivedDocument.getEntityType());
                aG02ArchivedDocument.setEntityUuid(aG01ArchivedDocument.getEntityUuid());
                aG02ArchivedDocument.setDownloadClient(aG01ArchivedDocument.getDownloadClient());
                aG02ArchivedDocument.setFileCustomName(aG01ArchivedDocument.getFileCustomName());
                aG02ArchivedDocument.setMemo(aG01ArchivedDocument.getMemo());
                aG02ArchivedDocument.setEntityStatus(aG01ArchivedDocument.getEntityStatus());
                aG02ArchivedDocument.setCreated(aG01ArchivedDocument.getCreated());
                aG02ArchivedDocument.setUpdated(aG01ArchivedDocument.getUpdated());

                GardenJpaUtils.storeEntity(em, G02ArchivedDocument.class, aG02ArchivedDocument, aG02ArchivedDocument.getArchivedDocumentUuid(), G02DataUpdaterFactory.getSingleton().getG02ArchivedDocumentUpdater());
            }
        }
    }
    
    private void transferArchivedDocumentBk(EntityManager em) throws Exception {
        List<G01ArchivedDocumentBk> aG01ArchivedDocumentBkList = GardenJpaUtils.findAll(em, G01ArchivedDocumentBk.class);
        if (aG01ArchivedDocumentBkList != null){
            G02ArchivedDocumentBk aG02ArchivedDocumentBk;
            for (G01ArchivedDocumentBk aG01ArchivedDocumentBk : aG01ArchivedDocumentBkList){
                aG02ArchivedDocumentBk = new G02ArchivedDocumentBk();

                aG02ArchivedDocumentBk.setArchivedDocumentUuid(aG01ArchivedDocumentBk.getArchivedDocumentUuid());
                aG02ArchivedDocumentBk.setFileName(aG01ArchivedDocumentBk.getFileName());
                aG02ArchivedDocumentBk.setFileLocation(aG01ArchivedDocumentBk.getFileLocation());
                aG02ArchivedDocumentBk.setFileStatus(aG01ArchivedDocumentBk.getFileStatus());
                aG02ArchivedDocumentBk.setFileTimestamp(aG01ArchivedDocumentBk.getFileTimestamp());
                aG02ArchivedDocumentBk.setProviderUuid(aG01ArchivedDocumentBk.getProviderUuid());
                aG02ArchivedDocumentBk.setEntityType(aG01ArchivedDocumentBk.getEntityType());
                aG02ArchivedDocumentBk.setEntityUuid(aG01ArchivedDocumentBk.getEntityUuid());
                aG02ArchivedDocumentBk.setDownloadClient(aG01ArchivedDocumentBk.getDownloadClient());
                aG02ArchivedDocumentBk.setFileCustomName(aG01ArchivedDocumentBk.getFileCustomName());
                aG02ArchivedDocumentBk.setMemo(aG01ArchivedDocumentBk.getMemo());
                aG02ArchivedDocumentBk.setEntityStatus(aG01ArchivedDocumentBk.getEntityStatus());
                aG02ArchivedDocumentBk.setCreated(aG01ArchivedDocumentBk.getCreated());
                aG02ArchivedDocumentBk.setUpdated(aG01ArchivedDocumentBk.getUpdated());

                GardenJpaUtils.storeEntity(em, G02ArchivedDocumentBk.class, aG02ArchivedDocumentBk, aG02ArchivedDocumentBk.getArchivedDocumentUuid(), G02DataUpdaterFactory.getSingleton().getG02ArchivedDocumentBkUpdater());
            }
        }
    }

    private void transferBill(EntityManager em) throws Exception {
        List<G01Bill> aG01BillList = GardenJpaUtils.findAll(em, G01Bill.class);
        if (aG01BillList != null){
            G02Bill aG02Bill;
            for (G01Bill aG01Bill : aG01BillList){
                aG02Bill = new G02Bill();

                aG02Bill.setBillUuid(aG01Bill.getBillUuid());
                aG02Bill.setEmployeeUuid(aG01Bill.getEmployeeUuid());
                aG02Bill.setEntityType(aG01Bill.getEntityType());
                aG02Bill.setEntityUuid(aG01Bill.getEntityUuid());
                aG02Bill.setEntityStatus(aG01Bill.getEntityStatus());
                aG02Bill.setBillContent(aG01Bill.getBillContent());
                aG02Bill.setBillDatetime(aG01Bill.getBillDatetime());
                aG02Bill.setBillDiscount(aG01Bill.getBillDiscount());
                aG02Bill.setBillDiscountType(aG01Bill.getBillDiscountType());
                aG02Bill.setBillStatus(aG01Bill.getBillStatus());
                aG02Bill.setBillTotal(aG01Bill.getBillTotal());
                aG02Bill.setCreated(aG01Bill.getCreated());
                aG02Bill.setUpdated(aG01Bill.getUpdated());

                GardenJpaUtils.storeEntity(em, G02Bill.class, aG02Bill, aG02Bill.getBillUuid(), G02DataUpdaterFactory.getSingleton().getG02BillUpdater());
            }
        }
    }

    private void transferChatMessage(EntityManager em) throws Exception  {
        List<G01ChatMessage> aG01ChatMessageList = GardenJpaUtils.findAll(em, G01ChatMessage.class);
        if (aG01ChatMessageList != null){
            G02ChatMessage aG02ChatMessage;
            for (G01ChatMessage aG01ChatMessage : aG01ChatMessageList){
                aG02ChatMessage = new G02ChatMessage();

                aG02ChatMessage.setChatMessageUuid(aG01ChatMessage.getChatMessageUuid());
                aG02ChatMessage.setEntityType(aG01ChatMessage.getEntityType());
                aG02ChatMessage.setEntityUuid(aG01ChatMessage.getEntityUuid());
                aG02ChatMessage.setInitialMessageUuid(aG01ChatMessage.getInitialMessageUuid());
                aG02ChatMessage.setMessage(aG01ChatMessage.getMessage());
                aG02ChatMessage.setTalkerUserUuid(aG01ChatMessage.getTalkerUserUuid());
                aG02ChatMessage.setCreated(aG01ChatMessage.getCreated());
                aG02ChatMessage.setUpdated(aG01ChatMessage.getUpdated());

                GardenJpaUtils.storeEntity(em, G02ChatMessage.class, aG02ChatMessage, aG02ChatMessage.getChatMessageUuid(), G02DataUpdaterFactory.getSingleton().getG02ChatMessageUpdater());
            }
        }
    }

    private void transferContactEntity(EntityManager em) throws Exception {
        List<G01ContactEntity> aG01ContactEntityList = GardenJpaUtils.findAll(em, G01ContactEntity.class);
        if (aG01ContactEntityList != null){
            G02ContactEntity aG02ContactEntity;
            for (G01ContactEntity aG01ContactEntity : aG01ContactEntityList){
                aG02ContactEntity = new G02ContactEntity();
                aG02ContactEntity.setG02ContactEntityPK(new G02ContactEntityPK());
                aG02ContactEntity.getG02ContactEntityPK().setContactMessageUuid(aG01ContactEntity.getG01ContactEntityPK().getContactMessageUuid());
                aG02ContactEntity.getG02ContactEntityPK().setEntityType(aG01ContactEntity.getG01ContactEntityPK().getEntityType());
                aG02ContactEntity.getG02ContactEntityPK().setEntityUuid(aG01ContactEntity.getG01ContactEntityPK().getEntityUuid());
                aG02ContactEntity.setEmailOrSms(aG01ContactEntity.getEmailOrSms());

                GardenJpaUtils.storeEntity(em, G02ContactEntity.class, aG02ContactEntity, aG02ContactEntity.getG02ContactEntityPK(), G02DataUpdaterFactory.getSingleton().getG02ContactEntityUpdater());
            }
        }
    }

    private void transferContactInfo(EntityManager em) throws Exception {
        List<G01ContactInfo> aG01ContactInfoList = GardenJpaUtils.findAll(em, G01ContactInfo.class);
        if (aG01ContactInfoList != null){
            G02ContactInfo aG02ContactInfo;
            for (G01ContactInfo aG01ContactInfo : aG01ContactInfoList){
                aG02ContactInfo = new G02ContactInfo();

                aG02ContactInfo.setContactInfoUuid(aG01ContactInfo.getContactInfoUuid());
                aG02ContactInfo.setEntityType(aG01ContactInfo.getEntityType());
                aG02ContactInfo.setEntityUuid(aG01ContactInfo.getEntityUuid());
                aG02ContactInfo.setContactInfo(aG01ContactInfo.getContactInfo());
                aG02ContactInfo.setContactType(aG01ContactInfo.getContactType());
                aG02ContactInfo.setPreferencePriority(aG01ContactInfo.getPreferencePriority());
                aG02ContactInfo.setShortMemo(aG01ContactInfo.getShortMemo());
                aG02ContactInfo.setEntityStatus(aG01ContactInfo.getEntityStatus());
                aG02ContactInfo.setCreated(aG01ContactInfo.getCreated());
                aG02ContactInfo.setUpdated(aG01ContactInfo.getUpdated());

                GardenJpaUtils.storeEntity(em, G02ContactInfo.class, aG02ContactInfo, aG02ContactInfo.getContactInfoUuid(), G02DataUpdaterFactory.getSingleton().getG02ContactInfoUpdater());
            }
        }
    }

    private void transferContactMessage(EntityManager em) throws Exception {
        List<G01ContactMessage> aG01ContactMessageList = GardenJpaUtils.findAll(em, G01ContactMessage.class);
        if (aG01ContactMessageList != null){
            G02ContactMessage aG02ContactMessage;
            for (G01ContactMessage aG01ContactMessage : aG01ContactMessageList){
                aG02ContactMessage = new G02ContactMessage();

                aG02ContactMessage.setContactMessageUuid(aG01ContactMessage.getContactMessageUuid());
                aG02ContactMessage.setContactTimestamp(aG01ContactMessage.getContactTimestamp());
                aG02ContactMessage.setEmailContent(aG01ContactMessage.getEmailContent());
                aG02ContactMessage.setContactSubject(aG01ContactMessage.getContactSubject());
                aG02ContactMessage.setEmployeeAccountUuid(aG01ContactMessage.getEmployeeAccountUuid());
                aG02ContactMessage.setSmsContent(aG01ContactMessage.getSmsContent());
                
                GardenJpaUtils.storeEntity(em, G02ContactMessage.class, aG02ContactMessage, aG02ContactMessage.getContactMessageUuid(), G02DataUpdaterFactory.getSingleton().getG02ContactMessageUpdater());
            }
        }
    }

    private void transferDeprecatedDocumentRequirement(EntityManager em) throws Exception {
        List<G01DocumentRequirement> aG01DocumentRequirementList = GardenJpaUtils.findAll(em, G01DocumentRequirement.class);
        if (aG01DocumentRequirementList != null){
            G02DeprecatedDocumentRequirement aG02DeprecatedDocumentRequirement;
            for (G01DocumentRequirement aG01DocumentRequirement : aG01DocumentRequirementList){
                aG02DeprecatedDocumentRequirement = new G02DeprecatedDocumentRequirement();

                aG02DeprecatedDocumentRequirement.setDocumentUuid(aG01DocumentRequirement.getDocumentUuid());
                aG02DeprecatedDocumentRequirement.setEntityType(aG01DocumentRequirement.getEntityType());
                aG02DeprecatedDocumentRequirement.setEntityUuid(aG01DocumentRequirement.getEntityUuid());
                aG02DeprecatedDocumentRequirement.setServiceTagUuid(aG01DocumentRequirement.getServiceTagUuid());
                aG02DeprecatedDocumentRequirement.setServiceTagName(aG01DocumentRequirement.getServiceTagName());
                aG02DeprecatedDocumentRequirement.setArchivedDocumentUuid(aG01DocumentRequirement.getArchivedDocumentUuid());
                aG02DeprecatedDocumentRequirement.setUnitPrice(aG01DocumentRequirement.getUnitPrice());
                aG02DeprecatedDocumentRequirement.setQuantity(aG01DocumentRequirement.getQuantity());
                aG02DeprecatedDocumentRequirement.setDescription(aG01DocumentRequirement.getDescription());
                aG02DeprecatedDocumentRequirement.setFileDemanded(aG01DocumentRequirement.getFileDemanded());
                aG02DeprecatedDocumentRequirement.setEntityStatus(aG01DocumentRequirement.getEntityStatus());
                aG02DeprecatedDocumentRequirement.setCreated(aG01DocumentRequirement.getCreated());
                aG02DeprecatedDocumentRequirement.setUpdated(aG01DocumentRequirement.getUpdated());
                
                GardenJpaUtils.storeEntity(em, G02DeprecatedDocumentRequirement.class, aG02DeprecatedDocumentRequirement, 
                        aG02DeprecatedDocumentRequirement.getDocumentUuid(), G02DataUpdaterFactory.getSingleton().getG02DeprecatedDocumentRequirementUpdater());
            }
        }
    }

    private void transferEmployee(EntityManager em) throws Exception {
        List<G01Employee> aG01EmployeeList = GardenJpaUtils.findAll(em, G01Employee.class);
        if (aG01EmployeeList != null){
            G02Employee aG02Employee;
            for (G01Employee aG01Employee : aG01EmployeeList){
                aG02Employee = new G02Employee();

                aG02Employee.setEmployeeAccountUuid(aG01Employee.getEmployeeAccountUuid());
                aG02Employee.setEmployedDate(aG01Employee.getEmployedDate());
                aG02Employee.setEmploymentStatus(aG01Employee.getEmploymentStatus());
                aG02Employee.setMemo(aG01Employee.getMemo());
                aG02Employee.setWorkEmail(aG01Employee.getWorkEmail());
                aG02Employee.setWorkPhone(aG01Employee.getWorkPhone());
                aG02Employee.setWorkTitle(aG01Employee.getWorkTitle());
                aG02Employee.setEntityStatus(aG01Employee.getEntityStatus());
                aG02Employee.setCreated(aG01Employee.getCreated());
                aG02Employee.setUpdated(aG01Employee.getUpdated());

                GardenJpaUtils.storeEntity(em, G02Employee.class, aG02Employee, aG02Employee.getEmployeeAccountUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02EmployeeUpdater());
            }
        }
    }
    
    private void transferEmployeeBk(EntityManager em) throws Exception {
        List<G01EmployeeBk> aG01EmployeeBkList = GardenJpaUtils.findAll(em, G01EmployeeBk.class);
        if (aG01EmployeeBkList != null){
            G02EmployeeBk aG02EmployeeBk;
            for (G01EmployeeBk aG01EmployeeBk : aG01EmployeeBkList){
                aG02EmployeeBk = new G02EmployeeBk();

                aG02EmployeeBk.setEmployeeAccountUuid(aG01EmployeeBk.getEmployeeAccountUuid());
                aG02EmployeeBk.setEmployedDate(aG01EmployeeBk.getEmployedDate());
                aG02EmployeeBk.setEmploymentStatus(aG01EmployeeBk.getEmploymentStatus());
                aG02EmployeeBk.setMemo(aG01EmployeeBk.getMemo());
                aG02EmployeeBk.setWorkEmail(aG01EmployeeBk.getWorkEmail());
                aG02EmployeeBk.setWorkPhone(aG01EmployeeBk.getWorkPhone());
                aG02EmployeeBk.setWorkTitle(aG01EmployeeBk.getWorkTitle());
                aG02EmployeeBk.setEntityStatus(aG01EmployeeBk.getEntityStatus());
                aG02EmployeeBk.setCreated(aG01EmployeeBk.getCreated());
                aG02EmployeeBk.setUpdated(aG01EmployeeBk.getUpdated());

                GardenJpaUtils.storeEntity(em, G02EmployeeBk.class, aG02EmployeeBk, aG02EmployeeBk.getEmployeeAccountUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02EmployeeBkUpdater());
            }
        }
    }
    
    private void transferLocation(EntityManager em) throws Exception {
        List<G01Location> aG01LocationList = GardenJpaUtils.findAll(em, G01Location.class);
        if (aG01LocationList != null){
            G02Location aG02Location;
            for (G01Location aG01Location : aG01LocationList){
                aG02Location = new G02Location();

                aG02Location.setLocationUuid(aG01Location.getLocationUuid());
                aG02Location.setEntityType(aG01Location.getEntityType());
                aG02Location.setEntityUuid(aG01Location.getEntityUuid());
                aG02Location.setCountry(aG01Location.getCountry());
                aG02Location.setLocalAddress(aG01Location.getLocalAddress());
                aG02Location.setCityName(aG01Location.getCityName());
                aG02Location.setStateCounty(aG01Location.getStateCounty());
                aG02Location.setStateName(aG01Location.getStateName());
                aG02Location.setZipCode(aG01Location.getZipCode());
                aG02Location.setPreferencePriority(aG01Location.getPreferencePriority());
                aG02Location.setShortMemo(aG01Location.getShortMemo());
                aG02Location.setEntityStatus(aG01Location.getEntityStatus());
                aG02Location.setCreated(aG01Location.getCreated());
                aG02Location.setUpdated(aG01Location.getUpdated());

                GardenJpaUtils.storeEntity(em, G02Location.class, aG02Location, aG02Location.getLocationUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02LocationUpdater());
            }
        }
    }
    
    public void fixLoggedEntityFields() {
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
            System.out.println("fixLoggedEntityFields...(demand G01/G02 database ready)");
            
            transferLog(em);
            
            System.out.println("committing...");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        System.out.println("Database update completed.");
    }

    private void transferLog(EntityManager em) throws Exception {
        List<G01Log> aG01LogList = GardenJpaUtils.findAll(em, G01Log.class);
        if (aG01LogList != null){
            G02Log aG02Log;
            for (G01Log aG01Log : aG01LogList){
                aG02Log = new G02Log();

                System.out.println("aG01Log.getLogUuid() -> " + aG01Log.getLogUuid());
                
                aG02Log.setLogUuid(aG01Log.getLogUuid());
                aG02Log.setLogName(aG01Log.getLogMsg());
                aG02Log.setEntityType(aG01Log.getEntityType());
                aG02Log.setEntityUuid(aG01Log.getEntityUuid());
                aG02Log.setLoggedEntityType(aG01Log.getEntityType());
                aG02Log.setLoggedEntityUuid(aG01Log.getEntityUuid());
                aG02Log.setLogMessage(aG01Log.getOperatorMessage());
                aG02Log.setOperatorAccountUuid(aG01Log.getOperatorAccountUuid());
                aG02Log.setCreated(aG01Log.getTimestamp());
                
                GardenJpaUtils.storeEntity(em, G02Log.class, aG02Log, aG02Log.getLogUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02LogUpdater());
            }
        }
    }

    private void transferPayment(EntityManager em) throws Exception {
        List<G01Payment> aG01PaymentList = GardenJpaUtils.findAll(em, G01Payment.class);
        if (aG01PaymentList != null){
            G02Payment aG02Payment;
            for (G01Payment aG01Payment : aG01PaymentList){
                aG02Payment = new G02Payment();

                aG02Payment.setPaymentUuid(aG01Payment.getPaymentUuid());
                //aG02Payment.setEntityStatus(aG01Payment.getEntityStatus());
                aG02Payment.setEmployeeUuid(aG01Payment.getEmployeeUuid());
                aG02Payment.setPaymentMemo(aG01Payment.getPaymentMemo());
                aG02Payment.setPaymentPrice(aG01Payment.getPaymentPrice());
                aG02Payment.setPaymentType(aG01Payment.getPaymentType());
                aG02Payment.setPaymentDate(aG01Payment.getPaymentDate());
                aG02Payment.setBillUuid(aG01Payment.getBillUuid());
                aG02Payment.setCreated(aG01Payment.getCreated());
                aG02Payment.setUpdated(aG01Payment.getUpdated());

                GardenJpaUtils.storeEntity(em, G02Payment.class, aG02Payment, aG02Payment.getPaymentUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02PaymentUpdater());
            }
        }
    }

    private void transferPersonalBusinessProperty(EntityManager em) throws Exception {
        List<G01PersonalBusinessProperty> aG01PersonalBusinessPropertyList = GardenJpaUtils.findAll(em, G01PersonalBusinessProperty.class);
        if (aG01PersonalBusinessPropertyList != null){
            G02PersonalBusinessProperty aG02PersonalBusinessProperty;
            for (G01PersonalBusinessProperty aG01PersonalBusinessProperty : aG01PersonalBusinessPropertyList){
                aG02PersonalBusinessProperty = new G02PersonalBusinessProperty();

                aG02PersonalBusinessProperty.setPersonalBusinessPropertyUuid(aG01PersonalBusinessProperty.getPersonalBusinessPropertyUuid());
                aG02PersonalBusinessProperty.setBusinessDescription(aG01PersonalBusinessProperty.getBusinessDescription());
                aG02PersonalBusinessProperty.setBusinessPropertyName(aG01PersonalBusinessProperty.getBusinessPropertyName());
                aG02PersonalBusinessProperty.setBusinessAddress(aG01PersonalBusinessProperty.getBusinessAddress());
                aG02PersonalBusinessProperty.setBusinessDescription(aG01PersonalBusinessProperty.getBusinessDescription());
                aG02PersonalBusinessProperty.setBusinessEin(aG01PersonalBusinessProperty.getBusinessEin());
                aG02PersonalBusinessProperty.setBusinessOwnership(aG01PersonalBusinessProperty.getBusinessOwnership());
                aG02PersonalBusinessProperty.setGrossReceiptsSales(aG01PersonalBusinessProperty.getGrossReceiptsSales());
                aG02PersonalBusinessProperty.setCostOfGoodsSold(aG01PersonalBusinessProperty.getCostOfGoodsSold());
                aG02PersonalBusinessProperty.setOtherIncome(aG01PersonalBusinessProperty.getOtherIncome());
                aG02PersonalBusinessProperty.setExpenseCableInternet(aG01PersonalBusinessProperty.getExpenseCableInternet());
                aG02PersonalBusinessProperty.setExpenseCarAndTruck(aG01PersonalBusinessProperty.getExpenseCarAndTruck());
                aG02PersonalBusinessProperty.setExpenseCommissions(aG01PersonalBusinessProperty.getExpenseCommissions());
                aG02PersonalBusinessProperty.setExpenseContractLabor(aG01PersonalBusinessProperty.getExpenseContractLabor());
                aG02PersonalBusinessProperty.setExpenseOffice(aG01PersonalBusinessProperty.getExpenseOffice());
                aG02PersonalBusinessProperty.setExpenseRentLease(aG01PersonalBusinessProperty.getExpenseRentLease());
                aG02PersonalBusinessProperty.setExpenseTelephone(aG01PersonalBusinessProperty.getExpenseTelephone());
                aG02PersonalBusinessProperty.setExpenseTravelMeals(aG01PersonalBusinessProperty.getExpenseTravelMeals());
                aG02PersonalBusinessProperty.setExpenseAdvertising(aG01PersonalBusinessProperty.getExpenseAdvertising());
                aG02PersonalBusinessProperty.setExpenseInsurance(aG01PersonalBusinessProperty.getExpenseInsurance());
                aG02PersonalBusinessProperty.setExpenseOthers(aG01PersonalBusinessProperty.getExpenseOthers());
                aG02PersonalBusinessProperty.setExpenseProfServices(aG01PersonalBusinessProperty.getExpenseProfServices());
                aG02PersonalBusinessProperty.setExpenseRepairs(aG01PersonalBusinessProperty.getExpenseRepairs());
                aG02PersonalBusinessProperty.setExpenseSupplies(aG01PersonalBusinessProperty.getExpenseSupplies());
                aG02PersonalBusinessProperty.setExpenseUtilities(aG01PersonalBusinessProperty.getExpenseUtilities());
                aG02PersonalBusinessProperty.setTaxpayerCaseUuid(aG01PersonalBusinessProperty.getTaxpayerCaseUuid());
                aG02PersonalBusinessProperty.setMemo(aG01PersonalBusinessProperty.getMemo());
                aG02PersonalBusinessProperty.setEntityStatus(aG01PersonalBusinessProperty.getEntityStatus());
                aG02PersonalBusinessProperty.setCreated(aG01PersonalBusinessProperty.getCreated());
                aG02PersonalBusinessProperty.setUpdated(aG01PersonalBusinessProperty.getUpdated());

                GardenJpaUtils.storeEntity(em, G02PersonalBusinessProperty.class, aG02PersonalBusinessProperty, 
                        aG02PersonalBusinessProperty.getPersonalBusinessPropertyUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02PersonalBusinessPropertyUpdater());
            }
        }
    }

    private void transferPersonalProperty(EntityManager em) throws Exception {
        List<G01PersonalProperty> aG01PersonalPropertyList = GardenJpaUtils.findAll(em, G01PersonalProperty.class);
        if (aG01PersonalPropertyList != null){
            G02PersonalProperty aG02PersonalProperty;
            for (G01PersonalProperty aG01PersonalProperty : aG01PersonalPropertyList){
                aG02PersonalProperty = new G02PersonalProperty();

                aG02PersonalProperty.setPersonalPropertyUuid(aG01PersonalProperty.getPersonalPropertyUuid());
                aG02PersonalProperty.setDateOnImprovement(aG01PersonalProperty.getDateOnImprovement());
                aG02PersonalProperty.setDateOnService(aG01PersonalProperty.getDateOnService());
                aG02PersonalProperty.setExpenseAdvertising(aG01PersonalProperty.getExpenseAdvertising());
                aG02PersonalProperty.setExpenseAutoAndTravel(aG01PersonalProperty.getExpenseAutoAndTravel());
                aG02PersonalProperty.setExpenseCleaning(aG01PersonalProperty.getExpenseCleaning());
                aG02PersonalProperty.setExpenseCommissions(aG01PersonalProperty.getExpenseCommissions());
                aG02PersonalProperty.setExpenseDepreciation(aG01PersonalProperty.getExpenseDepreciation());
                aG02PersonalProperty.setExpenseInsurance(aG01PersonalProperty.getExpenseInsurance());
                aG02PersonalProperty.setExpenseMgtFee(aG01PersonalProperty.getExpenseMgtFee());
                aG02PersonalProperty.setExpenseMorgageInterest(aG01PersonalProperty.getExpenseMorgageInterest());
                aG02PersonalProperty.setExpenseOthers(aG01PersonalProperty.getExpenseOthers());
                aG02PersonalProperty.setExpenseProfServices(aG01PersonalProperty.getExpenseProfServices());
                aG02PersonalProperty.setExpenseReTaxes(aG01PersonalProperty.getExpenseReTaxes());
                aG02PersonalProperty.setExpenseRepairs(aG01PersonalProperty.getExpenseRepairs());
                aG02PersonalProperty.setExpenseSupplies(aG01PersonalProperty.getExpenseSupplies());
                aG02PersonalProperty.setExpenseUtilities(aG01PersonalProperty.getExpenseUtilities());
                aG02PersonalProperty.setExpenseWaterSewer(aG01PersonalProperty.getExpenseWaterSewer());
                aG02PersonalProperty.setImprovementCost(aG01PersonalProperty.getImprovementCost());
                aG02PersonalProperty.setIncomeRentsReceieved(aG01PersonalProperty.getIncomeRentsReceieved());
                aG02PersonalProperty.setPercentageOfOwnership(aG01PersonalProperty.getPercentageOfOwnership());
                aG02PersonalProperty.setPercentageOfRentalUse(aG01PersonalProperty.getPercentageOfRentalUse());
                aG02PersonalProperty.setPropertyAddress(aG01PersonalProperty.getPropertyAddress());
                aG02PersonalProperty.setPropertyType(aG01PersonalProperty.getPropertyType());
                aG02PersonalProperty.setPurchasePrice(aG01PersonalProperty.getPurchasePrice());
                aG02PersonalProperty.setTaxpayerCaseUuid(aG01PersonalProperty.getTaxpayerCaseUuid());
                aG02PersonalProperty.setMemo(aG01PersonalProperty.getMemo());
                aG02PersonalProperty.setEntityStatus(aG01PersonalProperty.getEntityStatus());
                aG02PersonalProperty.setCreated(aG01PersonalProperty.getCreated());
                aG02PersonalProperty.setUpdated(aG01PersonalProperty.getUpdated());

                GardenJpaUtils.storeEntity(em, G02PersonalProperty.class, aG02PersonalProperty, aG02PersonalProperty.getPersonalPropertyUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02PersonalPropertyUpdater());
            }
        }
    }

    private void transferPostSection(EntityManager em) throws Exception {
        List<G01PostSection> aG01PostSectionList = GardenJpaUtils.findAll(em, G01PostSection.class);
        if (aG01PostSectionList != null){
            G02PostSection aG02PostSection;
            for (G01PostSection aG01PostSection : aG01PostSectionList){
                aG02PostSection = new G02PostSection();

                aG02PostSection.setPostSectionUuid(aG01PostSection.getPostSectionUuid());
                aG02PostSection.setEntityStatus(aG01PostSection.getEntityStatus());
                aG02PostSection.setPostContent(aG01PostSection.getPostContent());
                aG02PostSection.setWebPostUuid(aG01PostSection.getWebPostUuid());
                aG02PostSection.setCreated(aG01PostSection.getCreated());
                aG02PostSection.setUpdated(aG01PostSection.getUpdated());

                GardenJpaUtils.storeEntity(em, G02PostSection.class, aG02PostSection, aG02PostSection.getPostSectionUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02PostSectionUpdater());
            }
        }
    }

    private void transferDeprecatedProperty(EntityManager em) throws Exception {
        List<G01Property> aG01PropertyList = GardenJpaUtils.findAll(em, G01Property.class);
        if (aG01PropertyList != null){
            G02DeprecatedProperty aG02DeprecatedProperty;
            for (G01Property aG01Property : aG01PropertyList){
                aG02DeprecatedProperty = new G02DeprecatedProperty();

                aG02DeprecatedProperty.setPropKey(aG01Property.getPropKey());
                aG02DeprecatedProperty.setPropValue(aG01Property.getPropValue());

                GardenJpaUtils.storeEntity(em, G02DeprecatedProperty.class, aG02DeprecatedProperty, aG02DeprecatedProperty.getPropKey(), 
                        G02DataUpdaterFactory.getSingleton().getG02DeprecatedPropertyUpdater());
            }
        }
    }

    private void transferSchedule(EntityManager em) throws Exception {
        List<G01Schedule> aG01ScheduleList = GardenJpaUtils.findAll(em, G01Schedule.class);
        if (aG01ScheduleList != null){
            G02Schedule aG02Schedule;
            for (G01Schedule aG01Schedule : aG01ScheduleList){
                aG02Schedule = new G02Schedule();

                aG02Schedule.setScheduleUuid(aG01Schedule.getScheduleUuid());
                aG02Schedule.setAllDay(aG01Schedule.getEmployeeUuid());
                aG02Schedule.setEmployeeUuid(aG01Schedule.getEmployeeUuid());
                aG02Schedule.setCustomerUuid(aG01Schedule.getCustomerId());
                aG02Schedule.setEditable(aG01Schedule.getEditable());
                aG02Schedule.setEndingDatetime(aG01Schedule.getEndingDatetime());
                aG02Schedule.setRequestDatetime(aG01Schedule.getRequestDatetime());
                aG02Schedule.setScheduleContent(aG01Schedule.getScheduleContent());
                aG02Schedule.setScheduleStatus(aG01Schedule.getScheduleStatus());
                aG02Schedule.setScheduleSubject(aG01Schedule.getScheduleSubject());
                aG02Schedule.setScheduleUrl(aG01Schedule.getScheduleUrl());
                aG02Schedule.setStartingDatetime(aG01Schedule.getStartingDatetime());
                aG02Schedule.setCreated(aG01Schedule.getCreated());
                aG02Schedule.setUpdated(aG01Schedule.getUpdated());

                GardenJpaUtils.storeEntity(em, G02Schedule.class, aG02Schedule, aG02Schedule.getScheduleUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02ScheduleUpdater());
            }
        }
    }

    private void transferSecurityQna(EntityManager em) throws Exception {
        List<G01SecurityQna> aG01SecurityQnaList = GardenJpaUtils.findAll(em, G01SecurityQna.class);
        if (aG01SecurityQnaList != null){
            G02SecurityQna aG02SecurityQna;
            for (G01SecurityQna aG01SecurityQna : aG01SecurityQnaList){
                aG02SecurityQna = new G02SecurityQna();

                aG02SecurityQna.setG02SecurityQnaPK(new G02SecurityQnaPK());
                aG02SecurityQna.getG02SecurityQnaPK().setAccountUuid(aG01SecurityQna.getG01SecurityQnaPK().getAccountUuid());
                aG02SecurityQna.getG02SecurityQnaPK().setSequrityQuestionCode(aG01SecurityQna.getG01SecurityQnaPK().getSequrityQuestionCode());
                aG02SecurityQna.setSequrityAnswer(aG01SecurityQna.getSequrityAnswer());
                aG02SecurityQna.setCreated(aG01SecurityQna.getCreated());
                aG02SecurityQna.setUpdated(aG01SecurityQna.getUpdated());

                GardenJpaUtils.storeEntity(em, G02SecurityQna.class, aG02SecurityQna, aG02SecurityQna.getG02SecurityQnaPK(), 
                        G02DataUpdaterFactory.getSingleton().getG02SecurityQnaUpdater());
            }
        }
    }

    private void transferDeprecatedServiceTag(EntityManager em) throws Exception {
        List<G01ServiceTag> aG01ServiceTagList = GardenJpaUtils.findAll(em, G01ServiceTag.class);
        if (aG01ServiceTagList != null){
            G02DeprecatedServiceTag aG02DeprecatedServiceTag;
            for (G01ServiceTag aG01ServiceTag : aG01ServiceTagList){
                aG02DeprecatedServiceTag = new G02DeprecatedServiceTag();

                aG02DeprecatedServiceTag.setServiceTagUuid(aG01ServiceTag.getServiceTagUuid());
                aG02DeprecatedServiceTag.setServiceTagName(aG01ServiceTag.getServiceTagName());
                aG02DeprecatedServiceTag.setDefaultUnitPrice(aG01ServiceTag.getDefaultUnitPrice());
                aG02DeprecatedServiceTag.setSampleFileName(aG01ServiceTag.getSampleFileName());
                aG02DeprecatedServiceTag.setDescription(aG01ServiceTag.getDescription());
                aG02DeprecatedServiceTag.setFileDemanded(aG01ServiceTag.getFileDemanded());
                aG02DeprecatedServiceTag.setEntityType(aG01ServiceTag.getEntityType());
                aG02DeprecatedServiceTag.setCreated(aG01ServiceTag.getCreated());
                aG02DeprecatedServiceTag.setUpdated(aG01ServiceTag.getUpdated());

                GardenJpaUtils.storeEntity(em, G02DeprecatedServiceTag.class, aG02DeprecatedServiceTag, aG02DeprecatedServiceTag.getServiceTagUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02DeprecatedServiceTagUpdater());
            }
        }
    }

    private void transferSystemProperty(EntityManager em) throws Exception {
        List<G01SystemProperty> aG01SystemPropertyList = GardenJpaUtils.findAll(em, G01SystemProperty.class);
        if (aG01SystemPropertyList != null){
            G02SystemProperty aG02SystemProperty;
            for (G01SystemProperty aG01SystemProperty : aG01SystemPropertyList){
                aG02SystemProperty = new G02SystemProperty();

                aG02SystemProperty.setG02SystemPropertyPK(new G02SystemPropertyPK());
                aG02SystemProperty.getG02SystemPropertyPK().setFlowerName(aG01SystemProperty.getG01SystemPropertyPK().getFlowerName());
                aG02SystemProperty.getG02SystemPropertyPK().setPropertyName(aG01SystemProperty.getG01SystemPropertyPK().getPropertyName());
                aG02SystemProperty.getG02SystemPropertyPK().setFlowerOwner(GardenFlowerOwner.YINLU_CPAC_COM.name());
                aG02SystemProperty.setPropertyValue(aG01SystemProperty.getPropertyValue());
                aG02SystemProperty.setDescription(aG01SystemProperty.getDescription());
                aG02SystemProperty.setCreated(aG01SystemProperty.getCreated());
                aG02SystemProperty.setUpdated(aG01SystemProperty.getUpdated());

                GardenJpaUtils.storeEntity(em, G02SystemProperty.class, aG02SystemProperty, aG02SystemProperty.getG02SystemPropertyPK(), 
                        G02DataUpdaterFactory.getSingleton().getG02SystemPropertyUpdater());
            }
        }
    }

    private void transferTaxFiling(EntityManager em) throws Exception {
        List<G01TaxFiling> aG01TaxFilingList = GardenJpaUtils.findAll(em, G01TaxFiling.class);
        System.out.println("transferTaxFiling......");
        if (aG01TaxFilingList != null){
            G02TaxFilingCase aG02TaxFilingCase;
            int counter = 0;
            for (G01TaxFiling aG01TaxFiling : aG01TaxFilingList){
                aG02TaxFilingCase = new G02TaxFilingCase();

                System.out.println("Record #" + (counter++)+ "/" + aG01TaxFilingList.size() + "......");
                
                aG02TaxFilingCase.setTaxFilingUuid(aG01TaxFiling.getTaxFilingUuid());
                aG02TaxFilingCase.setDeadline(aG01TaxFiling.getDeadline());
                //deprecated: not existing anymore//aG02TaxFilingCase.setEntityStatus(aG01TaxFiling.getEntityStatus());
                //aG02TaxFilingCase.setEntityStatusTimestamp(aG01TaxFiling.getCompletedDate());
                if ("Fisical".equalsIgnoreCase(aG01TaxFiling.getTaxFilingPeriod())){
                    aG02TaxFilingCase.setTaxFilingPeriod("Fiscal"); //typo in legancy
                }else{
                    aG02TaxFilingCase.setTaxFilingPeriod(aG01TaxFiling.getTaxFilingPeriod());
                }
                aG02TaxFilingCase.setTaxFilingType(aG01TaxFiling.getTaxFilingType());
                
                aG02TaxFilingCase.setEntityUuid(aG01TaxFiling.getTaxcorpCaseUuid());
                
                aG02TaxFilingCase.setCreated(aG01TaxFiling.getCreated());
                aG02TaxFilingCase.setUpdated(aG01TaxFiling.getUpdated());
                
                aG02TaxFilingCase.setEntityType(GardenEntityType.TAXCORP_CASE.name());
                /**
                 * G02TaxFiling keep taxcorp's status in three columns: entity-status (i.e. latest status), 
                 * entity-status-timestamp (i.e. latest status' date), and entity-memo (i.e. historical status)
                 */
                loadTaxFilingCaseStatus(em, aG01TaxFiling, aG02TaxFilingCase);

                GardenJpaUtils.storeEntity(em, G02TaxFilingCase.class, aG02TaxFilingCase, aG02TaxFilingCase.getTaxFilingUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02TaxFilingCaseUpdater());
                
                //transfer memos....
                G02Memo aG02Memo;
                if (ZcaValidator.isNotNullEmpty(aG01TaxFiling.getMemo())){
                    aG02Memo = new G02Memo(GardenData.generateUUIDString());
                    aG02Memo.setEntityStatus(GardenEntityType.TAX_FILING_CASE.value());
                    aG02Memo.setEntityType(GardenEntityType.TAX_FILING_CASE.name());
                    aG02Memo.setEntityUuid(aG01TaxFiling.getTaxFilingUuid());
                    aG02Memo.setMemo("[Dealine Memo] " + aG01TaxFiling.getMemo());
                    aG02Memo.setOperatorAccountUuid(businessOwnerAccountUuid);
                    aG02Memo.setTimestamp(new Date());

                    GardenJpaUtils.storeEntity(em, G02Memo.class, aG02Memo, aG02Memo.getMemoUuid(), 
                            G02DataUpdaterFactory.getSingleton().getG02MemoUpdater());
                }
                //transfer extension deadline....
                if (aG01TaxFiling.getExtensionDate() != null){
                    G02DeadlineExtension aG02DeadlineExtension = new G02DeadlineExtension(GardenData.generateUUIDString());
                    aG02DeadlineExtension.setEntityType(GardenEntityType.TAX_FILING_CASE.name());
                    aG02DeadlineExtension.setEntityUuid(aG01TaxFiling.getTaxFilingUuid());
                    aG02DeadlineExtension.setExtensionDate(aG01TaxFiling.getExtensionDate());
                    aG02DeadlineExtension.setExtensionMemo(aG01TaxFiling.getExtensionMemo());
                    aG02DeadlineExtension.setOperatorAccountUuid(businessOwnerAccountUuid);
                    aG02DeadlineExtension.setTimestamp(new Date());

                    GardenJpaUtils.storeEntity(em, G02DeadlineExtension.class, aG02DeadlineExtension, aG02DeadlineExtension.getDeadlineExtensionUuid(), 
                            G02DataUpdaterFactory.getSingleton().getG02DeadlineExtensionUpdater());
                }
            }
        }
    }

    private void updateTaxFilingEntityFieldsHelper(EntityManager em) throws Exception {
        List<G01TaxFiling> aG01TaxFilingList = GardenJpaUtils.findAll(em, G01TaxFiling.class);
        System.out.println("transferTaxFiling......");
        if (aG01TaxFilingList != null){
            G02TaxFilingCase aG02TaxFilingCase;
            int counter = 0;
            for (G01TaxFiling aG01TaxFiling : aG01TaxFilingList){
                aG02TaxFilingCase = new G02TaxFilingCase();

                System.out.println("Record #" + (counter++)+ "/" + aG01TaxFilingList.size() + "......");
                
                aG02TaxFilingCase.setTaxFilingUuid(aG01TaxFiling.getTaxFilingUuid());
                aG02TaxFilingCase.setDeadline(aG01TaxFiling.getDeadline());
                //deprecated: not existing anymore//aG02TaxFilingCase.setEntityStatus(aG01TaxFiling.getEntityStatus());
                //aG02TaxFilingCase.setEntityStatusTimestamp(aG01TaxFiling.getCompletedDate());
                if ("Fisical".equalsIgnoreCase(aG01TaxFiling.getTaxFilingPeriod())){
                    aG02TaxFilingCase.setTaxFilingPeriod("Fiscal"); //typo in legancy
                }else{
                    aG02TaxFilingCase.setTaxFilingPeriod(aG01TaxFiling.getTaxFilingPeriod());
                }
                aG02TaxFilingCase.setTaxFilingType(aG01TaxFiling.getTaxFilingType());
                
                aG02TaxFilingCase.setEntityUuid(aG01TaxFiling.getTaxcorpCaseUuid());
                
                aG02TaxFilingCase.setCreated(aG01TaxFiling.getCreated());
                aG02TaxFilingCase.setUpdated(aG01TaxFiling.getUpdated());
                
                aG02TaxFilingCase.setEntityType(GardenEntityType.TAXCORP_CASE.name());
                /**
                 * G02TaxFiling keep taxcorp's status in three columns: entity-status (i.e. latest status), 
                 * entity-status-timestamp (i.e. latest status' date), and entity-memo (i.e. historical status)
                 */
                loadTaxFilingCaseStatus(em, aG01TaxFiling, aG02TaxFilingCase);

                GardenJpaUtils.storeEntity(em, G02TaxFilingCase.class, aG02TaxFilingCase, aG02TaxFilingCase.getTaxFilingUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02TaxFilingCaseUpdater());
            }
        }
    }

    private void loadTaxFilingCaseStatus(EntityManager em, G01TaxFiling aG01TaxFiling, G02TaxFilingCase aG02TaxFilingCase) throws ZcaEntityValidationException {
        String taxFilingUuid = aG02TaxFilingCase.getTaxFilingUuid();
        if (aG01TaxFiling.getReceivedDate() != null){
            loadTaxFilingCaseStatusHelper(em, 
                                        taxFilingUuid, 
                                        GardenTaxFilingStatus.RECEIVED.name(), 
                                        aG01TaxFiling.getReceivedDate(), 
                                        aG01TaxFiling.getReceivedMemo());
            //deprecated: not existing anymore//aG02TaxFilingCase.setEntityStatus(GardenTaxFilingStatus.RECEIVED.name());
            //deprecated: not existing anymore//aG02TaxFilingCase.setEntityStatusTimestamp(aG01TaxFiling.getReceivedDate());
            aG02TaxFilingCase.setMemo(aG01TaxFiling.getReceivedMemo());
            
        }
        if (aG01TaxFiling.getPreparedDate() != null){
            loadTaxFilingCaseStatusHelper(em, 
                                        taxFilingUuid, 
                                        GardenTaxFilingStatus.PREPARED.name(), 
                                        aG01TaxFiling.getPreparedDate(), 
                                        aG01TaxFiling.getPreparedMemo());
            //deprecated: not existing anymore//aG02TaxFilingCase.setEntityStatus(GardenTaxFilingStatus.PREPARED.name());
            //deprecated: not existing anymore//aG02TaxFilingCase.setEntityStatusTimestamp(aG01TaxFiling.getPreparedDate());
            aG02TaxFilingCase.setMemo(aG01TaxFiling.getPreparedMemo());
        }
        if (aG01TaxFiling.getPickupDate() != null){
            
            loadTaxFilingCaseStatusHelper(em, 
                                        taxFilingUuid, 
                                        GardenTaxFilingStatus.PICKUP.name(), 
                                        aG01TaxFiling.getPickupDate(), 
                                        aG01TaxFiling.getPickupMemo());
            //deprecated: not existing anymore//aG02TaxFilingCase.setEntityStatus(GardenTaxFilingStatus.PICKUP.name());
            //deprecated: not existing anymore//aG02TaxFilingCase.setEntityStatusTimestamp(aG01TaxFiling.getPickupDate());
            aG02TaxFilingCase.setMemo(aG01TaxFiling.getPickupMemo());
        }
        if (aG01TaxFiling.getPickupDate() != null){
            
            loadTaxFilingCaseStatusHelper(em, 
                                        taxFilingUuid, 
                                        GardenTaxFilingStatus.EXT_EFILE.name(), 
                                        aG01TaxFiling.getPickupDate(), 
                                        aG01TaxFiling.getPickupMemo());
            //deprecated: not existing anymore//aG02TaxFilingCase.setEntityStatus(GardenTaxFilingStatus.PICKUP.name());
            //deprecated: not existing anymore//aG02TaxFilingCase.setEntityStatusTimestamp(aG01TaxFiling.getPickupDate());
            aG02TaxFilingCase.setMemo(aG01TaxFiling.getPickupMemo());
        }
        if (aG01TaxFiling.getCompletedDate() != null){
            loadTaxFilingCaseStatusHelper(em, 
                                        taxFilingUuid, 
                                        GardenTaxFilingStatus.COMPLETED.name(), 
                                        aG01TaxFiling.getCompletedDate(), 
                                        aG01TaxFiling.getCompletedMemo());
            //deprecated: not existing anymore//aG02TaxFilingCase.setEntityStatus(GardenTaxFilingStatus.COMPLETED.name());
            //deprecated: not existing anymore//aG02TaxFilingCase.setEntityStatusTimestamp(aG01TaxFiling.getCompletedDate());
            aG02TaxFilingCase.setMemo(aG01TaxFiling.getCompletedMemo());
        }
    }
    
    private void loadTaxFilingCaseStatusHelper(EntityManager em, 
                                               String taxFilingUuid, 
                                               String taxFilingStatus, 
                                               Date statusDate, 
                                               String statusMemo) throws ZcaEntityValidationException
    {
        G02TaxFilingStatus aG02TaxFilingStatus = new G02TaxFilingStatus();
        G02TaxFilingStatusPK pkid = new G02TaxFilingStatusPK();
        pkid.setTaxFilingUuid(taxFilingUuid);
        pkid.setTaxFilingStatus(taxFilingStatus);
        aG02TaxFilingStatus.setG02TaxFilingStatusPK(pkid);
        aG02TaxFilingStatus.setStatusDate(statusDate);
        aG02TaxFilingStatus.setStatusMemo(statusMemo);
        aG02TaxFilingStatus.setOperatorAccountUuid(businessOwnerAccountUuid);
        aG02TaxFilingStatus.setCreated(new Date());
        
        GardenJpaUtils.storeEntity(em, G02TaxFilingStatus.class, aG02TaxFilingStatus, 
                aG02TaxFilingStatus.getG02TaxFilingStatusPK(), 
                G02DataUpdaterFactory.getSingleton().getG02TaxFilingStatusUpdater());
    
    }

    private void transferTaxcorpCase(EntityManager em) throws Exception {
        List<G01TaxcorpCase> aG01TaxcorpCaseList = GardenJpaUtils.findAll(em, G01TaxcorpCase.class);
        System.out.println("transferTaxcorpCase......");
        if (aG01TaxcorpCaseList != null){
            int counter = 0;
            G02TaxcorpCase aG02TaxcorpCase;
            for (G01TaxcorpCase aG01TaxcorpCase : aG01TaxcorpCaseList){
                aG02TaxcorpCase = new G02TaxcorpCase();

                System.out.println("Record #" + (counter++)+ "/" + aG01TaxcorpCaseList.size() + "......");
                
                aG02TaxcorpCase.setTaxcorpCaseUuid(aG01TaxcorpCase.getTaxcorpCaseUuid());
                aG02TaxcorpCase.setAgreementSignature(aG01TaxcorpCase.getAgreementSignature());
                aG02TaxcorpCase.setAgreementSignatureTimestamp(aG01TaxcorpCase.getAgreementSignatureTimestamp());
                aG02TaxcorpCase.setAgreementUuid(aG01TaxcorpCase.getAgreementUuid());
                aG02TaxcorpCase.setBusinessPurpose(aG01TaxcorpCase.getBusinessPurpose());
                aG02TaxcorpCase.setBusinessStatus(aG01TaxcorpCase.getBusinessStatus());
                aG02TaxcorpCase.setBusinessType(aG01TaxcorpCase.getBusinessType());
                aG02TaxcorpCase.setBankAccountNumber(aG01TaxcorpCase.getBankAccountNumber());
                aG02TaxcorpCase.setBankRoutingNumber(aG01TaxcorpCase.getBankRoutingNumber());
                aG02TaxcorpCase.setCorporateName(aG01TaxcorpCase.getCorporateName());
                aG02TaxcorpCase.setCorporateEmail(aG01TaxcorpCase.getCorporateEmail());
                aG02TaxcorpCase.setCorporateFax(aG01TaxcorpCase.getCorporateFax());
                aG02TaxcorpCase.setCorporatePhone(aG01TaxcorpCase.getCorporatePhone());
                aG02TaxcorpCase.setCorporateWebPresence(aG01TaxcorpCase.getCorporateWebPresence());
                aG02TaxcorpCase.setCustomerAccountUuid(aG01TaxcorpCase.getCustomerAccountUuid());
                aG02TaxcorpCase.setDosDate(aG01TaxcorpCase.getDosDate());
                aG02TaxcorpCase.setEinNumber(aG01TaxcorpCase.getEinNumber());
                aG02TaxcorpCase.setTaxcorpCountry(aG01TaxcorpCase.getTaxcorpCountry());
                aG02TaxcorpCase.setTaxcorpAddress(aG01TaxcorpCase.getTaxcorpAddress());
                aG02TaxcorpCase.setTaxcorpCity(aG01TaxcorpCase.getTaxcorpCity());
                aG02TaxcorpCase.setTaxcorpStateCounty(aG01TaxcorpCase.getTaxcorpStateCounty());
                aG02TaxcorpCase.setTaxcorpState(aG01TaxcorpCase.getTaxcorpState());
                aG02TaxcorpCase.setTaxcorpZip(aG01TaxcorpCase.getTaxcorpZip());
                aG02TaxcorpCase.setMemo(aG01TaxcorpCase.getMemo());
                aG02TaxcorpCase.setDba(null);
                aG02TaxcorpCase.setLatestLogUuid(aG01TaxcorpCase.getLatestLogUuid());
                aG02TaxcorpCase.setEntityStatus(aG01TaxcorpCase.getEntityStatus());
                aG02TaxcorpCase.setCreated(aG01TaxcorpCase.getCreated());
                aG02TaxcorpCase.setUpdated(aG01TaxcorpCase.getUpdated());

                if ("Inactive".equalsIgnoreCase(aG02TaxcorpCase.getBusinessStatus())){
                    G02TaxcorpCaseBk aG02TaxcorpCaseBk = new G02TaxcorpCaseBk();
                    aG02TaxcorpCaseBk.setAgreementUuid(aG02TaxcorpCase.getAgreementUuid());
                    aG02TaxcorpCaseBk.setAgreementSignature(aG02TaxcorpCase.getAgreementSignature());
                    aG02TaxcorpCaseBk.setAgreementSignatureTimestamp(aG02TaxcorpCase.getAgreementSignatureTimestamp());
                    aG02TaxcorpCaseBk.setAgreementUuid(aG02TaxcorpCase.getAgreementUuid());
                    aG02TaxcorpCaseBk.setBankAccountNumber(aG02TaxcorpCase.getBankAccountNumber());
                    aG02TaxcorpCaseBk.setBankRoutingNumber(aG02TaxcorpCase.getBankRoutingNumber());
                    aG02TaxcorpCaseBk.setBusinessPurpose(aG02TaxcorpCase.getBusinessPurpose());
                    aG02TaxcorpCaseBk.setBusinessStatus(aG02TaxcorpCase.getBusinessStatus());
                    aG02TaxcorpCaseBk.setBusinessType(aG02TaxcorpCase.getBusinessType());
                    aG02TaxcorpCaseBk.setCorporateName(aG02TaxcorpCase.getCorporateName());
                    aG02TaxcorpCaseBk.setCorporateEmail(aG02TaxcorpCase.getCorporateEmail());
                    aG02TaxcorpCaseBk.setCorporateFax(aG02TaxcorpCase.getCorporateFax());
                    aG02TaxcorpCaseBk.setCorporatePhone(aG02TaxcorpCase.getCorporatePhone());
                    aG02TaxcorpCaseBk.setCorporateWebPresence(aG02TaxcorpCase.getCorporateWebPresence());
                    aG02TaxcorpCaseBk.setCustomerAccountUuid(aG02TaxcorpCase.getCustomerAccountUuid());
                    aG02TaxcorpCaseBk.setDosDate(aG02TaxcorpCase.getDosDate());
                    aG02TaxcorpCaseBk.setEinNumber(aG02TaxcorpCase.getEinNumber());
                    aG02TaxcorpCaseBk.setMemo(aG02TaxcorpCase.getMemo());
                    aG02TaxcorpCaseBk.setDba(aG02TaxcorpCase.getDba());
                    aG02TaxcorpCaseBk.setLatestLogUuid(aG02TaxcorpCase.getLatestLogUuid());
                    aG02TaxcorpCaseBk.setTaxcorpState(aG02TaxcorpCase.getTaxcorpState());
                    aG02TaxcorpCaseBk.setEntityStatus(aG02TaxcorpCase.getEntityStatus());
                    aG02TaxcorpCaseBk.setTaxcorpAddress(aG02TaxcorpCase.getTaxcorpAddress());
                    aG02TaxcorpCaseBk.setTaxcorpCity(aG02TaxcorpCase.getTaxcorpCity());
                    aG02TaxcorpCaseBk.setTaxcorpStateCounty(aG02TaxcorpCase.getTaxcorpStateCounty());
                    aG02TaxcorpCaseBk.setTaxcorpState(aG02TaxcorpCase.getTaxcorpState());
                    aG02TaxcorpCaseBk.setTaxcorpZip(aG02TaxcorpCase.getTaxcorpZip());
                    aG02TaxcorpCaseBk.setTaxcorpCountry(aG02TaxcorpCase.getTaxcorpCountry());
                    aG02TaxcorpCaseBk.setTaxcorpCaseUuid(aG02TaxcorpCase.getTaxcorpCaseUuid());
                    aG02TaxcorpCaseBk.setCreated(aG02TaxcorpCase.getCreated());
                    aG02TaxcorpCaseBk.setUpdated(aG02TaxcorpCase.getUpdated());

                    /**
                     * All the backup entities in the table are in-active
                     */
                    aG02TaxcorpCaseBk.setBusinessStatus(TaxcorpBusinessStatus.FINALIZED.value());
                
                    GardenJpaUtils.storeEntity(em, G02TaxcorpCaseBk.class, aG02TaxcorpCaseBk, aG02TaxcorpCaseBk.getTaxcorpCaseUuid(), 
                            G02DataUpdaterFactory.getSingleton().getG02TaxcorpCaseBkUpdater());
                
                }else{
                    /**
                     * All the entities in this table are active (BK-table is inactive)
                     */
                    aG02TaxcorpCase.setBusinessStatus(TaxcorpBusinessStatus.ACTIVE.value());

                    GardenJpaUtils.storeEntity(em, G02TaxcorpCase.class, aG02TaxcorpCase, aG02TaxcorpCase.getTaxcorpCaseUuid(), 
                            G02DataUpdaterFactory.getSingleton().getG02TaxcorpCaseUpdater());
                }
            }
        }
    }

    private void transferTaxcorpCaseBk(EntityManager em) throws Exception {
        List<G01TaxcorpCaseBk> aG01TaxcorpCaseBkList = GardenJpaUtils.findAll(em, G01TaxcorpCaseBk.class);
        if (aG01TaxcorpCaseBkList != null){
            G02TaxcorpCaseBk aG02TaxcorpCaseBk;
            for (G01TaxcorpCaseBk aG01TaxcorpCaseBk : aG01TaxcorpCaseBkList){
                aG02TaxcorpCaseBk = new G02TaxcorpCaseBk();

                aG02TaxcorpCaseBk.setTaxcorpCaseUuid(aG01TaxcorpCaseBk.getTaxcorpCaseUuid());
                aG02TaxcorpCaseBk.setAgreementSignature(aG01TaxcorpCaseBk.getAgreementSignature());
                aG02TaxcorpCaseBk.setAgreementSignatureTimestamp(aG01TaxcorpCaseBk.getAgreementSignatureTimestamp());
                aG02TaxcorpCaseBk.setAgreementUuid(aG01TaxcorpCaseBk.getAgreementUuid());
                aG02TaxcorpCaseBk.setBusinessPurpose(aG01TaxcorpCaseBk.getBusinessPurpose());
                aG02TaxcorpCaseBk.setBusinessStatus(aG01TaxcorpCaseBk.getBusinessStatus());
                aG02TaxcorpCaseBk.setBusinessType(aG01TaxcorpCaseBk.getBusinessType());
                aG02TaxcorpCaseBk.setBankAccountNumber(aG01TaxcorpCaseBk.getBankAccountNumber());
                aG02TaxcorpCaseBk.setBankRoutingNumber(aG01TaxcorpCaseBk.getBankRoutingNumber());
                aG02TaxcorpCaseBk.setCorporateName(aG01TaxcorpCaseBk.getCorporateName());
                aG02TaxcorpCaseBk.setCorporateEmail(aG01TaxcorpCaseBk.getCorporateEmail());
                aG02TaxcorpCaseBk.setCorporateFax(aG01TaxcorpCaseBk.getCorporateFax());
                aG02TaxcorpCaseBk.setCorporatePhone(aG01TaxcorpCaseBk.getCorporatePhone());
                aG02TaxcorpCaseBk.setCorporateWebPresence(aG01TaxcorpCaseBk.getCorporateWebPresence());
                aG02TaxcorpCaseBk.setCustomerAccountUuid(aG01TaxcorpCaseBk.getCustomerAccountUuid());
                aG02TaxcorpCaseBk.setDosDate(aG01TaxcorpCaseBk.getDosDate());
                aG02TaxcorpCaseBk.setEinNumber(aG01TaxcorpCaseBk.getEinNumber());
                aG02TaxcorpCaseBk.setTaxcorpCountry(aG01TaxcorpCaseBk.getTaxcorpCountry());
                aG02TaxcorpCaseBk.setTaxcorpAddress(aG01TaxcorpCaseBk.getTaxcorpAddress());
                aG02TaxcorpCaseBk.setTaxcorpCity(aG01TaxcorpCaseBk.getTaxcorpCity());
                aG02TaxcorpCaseBk.setTaxcorpStateCounty(aG01TaxcorpCaseBk.getTaxcorpStateCounty());
                aG02TaxcorpCaseBk.setTaxcorpState(aG01TaxcorpCaseBk.getTaxcorpState());
                aG02TaxcorpCaseBk.setTaxcorpZip(aG01TaxcorpCaseBk.getTaxcorpZip());
                aG02TaxcorpCaseBk.setMemo(aG01TaxcorpCaseBk.getMemo());
                aG02TaxcorpCaseBk.setDba(null);
                aG02TaxcorpCaseBk.setLatestLogUuid(aG01TaxcorpCaseBk.getLatestLogUuid());
                aG02TaxcorpCaseBk.setEntityStatus(aG01TaxcorpCaseBk.getEntityStatus());
                aG02TaxcorpCaseBk.setCreated(aG01TaxcorpCaseBk.getCreated());
                aG02TaxcorpCaseBk.setUpdated(aG01TaxcorpCaseBk.getUpdated());

                /**
                 * All the backup entities in the table are in-active
                 */
                aG02TaxcorpCaseBk.setBusinessStatus(TaxcorpBusinessStatus.FINALIZED.value());
                
                GardenJpaUtils.storeEntity(em, G02TaxcorpCaseBk.class, aG02TaxcorpCaseBk, aG02TaxcorpCaseBk.getTaxcorpCaseUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02TaxcorpCaseBkUpdater());
            }
        }
    }

    private void transferTaxcorpRepresentative(EntityManager em) throws Exception {
        List<G01TaxcorpRepresentative> aG01TaxcorpRepresentativeList = GardenJpaUtils.findAll(em, G01TaxcorpRepresentative.class);
        System.out.println("transferTaxcorpRepresentative......");
        if (aG01TaxcorpRepresentativeList != null){
            G02BusinessContactor aG02BusinessContactor;
            G01User aG01User;
            int counter = 0;
            for (G01TaxcorpRepresentative aG01TaxcorpRepresentative : aG01TaxcorpRepresentativeList){
                System.out.println("Record #" + (counter++)+ "/" + aG01TaxcorpRepresentativeList.size() + "......");
                aG01User = GardenJpaUtils.findById(em, G01User.class, aG01TaxcorpRepresentative.getG01TaxcorpRepresentativePK().getRepresentativeUserUuid());
                if (aG01User != null){
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("entityUuid", aG01User.getUserUuid());
                    List<G01ContactInfo> aG01ContactInfoList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                            G01ContactInfo.class, "G01ContactInfo.findByEntityUuid", params);
                    if (aG01ContactInfoList != null){
                        for (G01ContactInfo aG01ContactInfo : aG01ContactInfoList){
                            aG02BusinessContactor = new G02BusinessContactor();
                            aG02BusinessContactor.setBusinessContactorUuid(GardenData.generateUUIDString());
                            aG02BusinessContactor.setContactInfo(aG01ContactInfo.getContactInfo());
                            aG02BusinessContactor.setContactType(aG01ContactInfo.getContactType());
                            aG02BusinessContactor.setFirstName(aG01User.getFirstName());
                            aG02BusinessContactor.setLastName(aG01User.getLastName());
                            if (ZcaValidator.isNullEmpty(aG01User.getSsn())){
                                aG02BusinessContactor.setSsn("n/a");
                            }else{
                                aG02BusinessContactor.setSsn(aG01User.getSsn());
                            }
                            if (aG01User.getBirthday() != null){
                                aG02BusinessContactor.setBirthday(aG01User.getBirthday());
                            }
                            aG02BusinessContactor.setRole(aG01TaxcorpRepresentative.getRoleInCorp());
                            aG02BusinessContactor.setMemo(aG01TaxcorpRepresentative.getMemo());
                            aG02BusinessContactor.setEntityUuid(aG01TaxcorpRepresentative.getG01TaxcorpRepresentativePK().getTaxcorpCaseUuid());
                            aG02BusinessContactor.setEntityType(GardenEntityType.TAXCORP_CASE.name());
                            aG02BusinessContactor.setCreated(new Date());

                            GardenJpaUtils.storeEntity(em, G02BusinessContactor.class, aG02BusinessContactor, aG02BusinessContactor.getBusinessContactorUuid(), 
                                    G02DataUpdaterFactory.getSingleton().getG02BusinessContactorUpdater());
                        }//for
                    }
                }
            }//for
        }
    }

    private void transferTaxpayerCase(EntityManager em) throws Exception {
        List<G01TaxpayerCase> aG01TaxpayerCaseList = GardenJpaUtils.findAll(em, G01TaxpayerCase.class);
        System.out.println("transferTaxpayerCase......");
        if (aG01TaxpayerCaseList != null){
            G02TaxpayerCase aG02TaxpayerCase;
            G02TaxFilingCase aG02TaxFilingCase;
            G02Memo aG02Memo;
            int counter = 0;
            for (G01TaxpayerCase aG01TaxpayerCase : aG01TaxpayerCaseList){
                aG02TaxpayerCase = new G02TaxpayerCase();

                System.out.println("Record #" + (counter++)+ "/" + aG01TaxpayerCaseList.size() + "......");

                aG02TaxpayerCase.setTaxpayerCaseUuid(aG01TaxpayerCase.getTaxpayerCaseUuid());
                aG02TaxpayerCase.setAgreementSignature(aG01TaxpayerCase.getAgreementSignature());
                aG02TaxpayerCase.setAgreementSignatureTimestamp(aG01TaxpayerCase.getAgreementSignatureTimestamp());
                aG02TaxpayerCase.setAgreementUuid(aG01TaxpayerCase.getAgreementUuid());
                aG02TaxpayerCase.setBankAccountNumber(aG01TaxpayerCase.getBankAccountNumber());
                aG02TaxpayerCase.setBankRoutingNumber(aG01TaxpayerCase.getBankRoutingNumber());
                aG02TaxpayerCase.setCustomerAccountUuid(aG01TaxpayerCase.getCustomerAccountUuid());
                aG02TaxpayerCase.setEntityStatus(aG01TaxpayerCase.getEntityStatus());
                aG02TaxpayerCase.setLatestLogUuid(aG01TaxpayerCase.getLatestLogUuid());
                aG02TaxpayerCase.setFederalFilingStatus(aG01TaxpayerCase.getFederalFilingStatus());
        ////        aG02TaxpayerCase.setBankNote();
        ////        aG02TaxpayerCase.setContact();
                aG02TaxpayerCase.setDeadline(aG01TaxpayerCase.getDeadline());
                aG02TaxpayerCase.setCreated(aG01TaxpayerCase.getCreated());
                aG02TaxpayerCase.setUpdated(aG01TaxpayerCase.getUpdated());
                
                GardenJpaUtils.storeEntity(em, G02TaxpayerCase.class, aG02TaxpayerCase, aG02TaxpayerCase.getTaxpayerCaseUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02TaxpayerCaseUpdater());
                
                //transfer deadline
                if (aG01TaxpayerCase.getDeadline() != null){
                    aG02TaxFilingCase = new G02TaxFilingCase();
                    aG02TaxFilingCase.setTaxFilingUuid(GardenData.generateUUIDString());
                    aG02TaxFilingCase.setDeadline(aG01TaxpayerCase.getDeadline());
                    //aG02TaxFilingCase.setEntityStatus(aG01TaxFiling.getEntityStatus());
                    aG02TaxFilingCase.setTaxFilingPeriod(TaxFilingPeriod.YEARLY.value());
                    aG02TaxFilingCase.setTaxFilingType(TaxFilingType.TAX_RETURN.value());

                    aG02TaxFilingCase.setEntityUuid(aG01TaxpayerCase.getTaxpayerCaseUuid());
                    aG02TaxFilingCase.setEntityType(GardenEntityType.TAXPAYER_CASE.name());

                    aG02TaxFilingCase.setCreated(aG01TaxpayerCase.getCreated());
                    aG02TaxFilingCase.setUpdated(aG01TaxpayerCase.getUpdated());

                    GardenJpaUtils.storeEntity(em, G02TaxFilingCase.class, aG02TaxFilingCase, aG02TaxFilingCase.getTaxFilingUuid(), 
                            G02DataUpdaterFactory.getSingleton().getG02TaxFilingCaseUpdater());
                    
                    if (ZcaValidator.isNotNullEmpty(aG01TaxpayerCase.getMemo())){
                        aG02Memo = new G02Memo(GardenData.generateUUIDString());
                        aG02Memo.setEntityStatus(GardenEntityType.TAX_FILING_CASE.value());
                        aG02Memo.setEntityType(GardenEntityType.TAX_FILING_CASE.name());
                        aG02Memo.setEntityUuid(aG02TaxFilingCase.getTaxFilingUuid());
                        aG02Memo.setMemo(aG01TaxpayerCase.getMemo());
                        aG02Memo.setOperatorAccountUuid(businessOwnerAccountUuid);
                        aG02Memo.setTimestamp(new Date());

                        GardenJpaUtils.storeEntity(em, G02Memo.class, aG02Memo, aG02Memo.getMemoUuid(), 
                                G02DataUpdaterFactory.getSingleton().getG02MemoUpdater());
                    }
                    //transfer extension
                    if (aG01TaxpayerCase.getExtensionDate() != null){
                        G02DeadlineExtension aG02DeadlineExtension = new G02DeadlineExtension(GardenData.generateUUIDString());
                        aG02DeadlineExtension.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
                        aG02DeadlineExtension.setEntityUuid(aG01TaxpayerCase.getTaxpayerCaseUuid());
                        aG02DeadlineExtension.setExtensionDate(aG01TaxpayerCase.getExtensionDate());
                        aG02DeadlineExtension.setExtensionMemo(aG01TaxpayerCase.getExtensionMemo());
                        aG02DeadlineExtension.setOperatorAccountUuid(aG01TaxpayerCase.getEmployeeAccountUuid());
                        aG02DeadlineExtension.setTimestamp(aG01TaxpayerCase.getUpdated());

                        GardenJpaUtils.storeEntity(em, G02DeadlineExtension.class, aG02DeadlineExtension, aG02DeadlineExtension.getDeadlineExtensionUuid(), 
                                G02DataUpdaterFactory.getSingleton().getG02DeadlineExtensionUpdater());
                    }
                }
            }
        }
    }

    private void transferTaxpayerCaseBk(EntityManager em) throws Exception {
        List<G01TaxpayerCaseBk> aG01TaxpayerCaseBkList = GardenJpaUtils.findAll(em, G01TaxpayerCaseBk.class);
        System.out.println("transferTaxpayerCaseBk......");
        if (aG01TaxpayerCaseBkList != null){
            G02TaxpayerCaseBk aG02TaxpayerCaseBk;
            G02TaxFilingCase aG02TaxFilingCase;
            G02Memo aG02Memo;
            int counter = 0;
            for (G01TaxpayerCaseBk aG01TaxpayerCaseBk : aG01TaxpayerCaseBkList){
                aG02TaxpayerCaseBk = new G02TaxpayerCaseBk();

                System.out.println("Record #" + (counter++)+ "/" + aG01TaxpayerCaseBkList.size() + "......");

                aG02TaxpayerCaseBk.setTaxpayerCaseUuid(aG01TaxpayerCaseBk.getTaxpayerCaseUuid());
                aG02TaxpayerCaseBk.setAgreementSignature(aG01TaxpayerCaseBk.getAgreementSignature());
                aG02TaxpayerCaseBk.setAgreementSignatureTimestamp(aG01TaxpayerCaseBk.getAgreementSignatureTimestamp());
                aG02TaxpayerCaseBk.setAgreementUuid(aG01TaxpayerCaseBk.getAgreementUuid());
                aG02TaxpayerCaseBk.setBankAccountNumber(aG01TaxpayerCaseBk.getBankAccountNumber());
                aG02TaxpayerCaseBk.setBankRoutingNumber(aG01TaxpayerCaseBk.getBankRoutingNumber());
                aG02TaxpayerCaseBk.setCustomerAccountUuid(aG01TaxpayerCaseBk.getCustomerAccountUuid());
                aG02TaxpayerCaseBk.setEntityStatus(aG01TaxpayerCaseBk.getEntityStatus());
                aG02TaxpayerCaseBk.setLatestLogUuid(aG01TaxpayerCaseBk.getLatestLogUuid());
                aG02TaxpayerCaseBk.setFederalFilingStatus(aG01TaxpayerCaseBk.getFederalFilingStatus());
                aG02TaxpayerCaseBk.setDeadline(aG01TaxpayerCaseBk.getDeadline());
                aG02TaxpayerCaseBk.setCreated(aG01TaxpayerCaseBk.getCreated());
                aG02TaxpayerCaseBk.setUpdated(aG01TaxpayerCaseBk.getUpdated());
                aG02TaxpayerCaseBk.setMemo(aG01TaxpayerCaseBk.getMemo());
                
                GardenJpaUtils.storeEntity(em, G02TaxpayerCaseBk.class, aG02TaxpayerCaseBk, aG02TaxpayerCaseBk.getTaxpayerCaseUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02TaxpayerCaseBkUpdater());
                
                //transfer deadline
                if (aG01TaxpayerCaseBk.getDeadline() != null){
                    aG02TaxFilingCase = new G02TaxFilingCase();
                    aG02TaxFilingCase.setTaxFilingUuid(GardenData.generateUUIDString());
                    aG02TaxFilingCase.setDeadline(aG01TaxpayerCaseBk.getDeadline());
                    //aG02TaxFilingCase.setEntityStatus(aG01TaxFiling.getEntityStatus());
                    aG02TaxFilingCase.setTaxFilingPeriod(TaxFilingPeriod.YEARLY.value());
                    aG02TaxFilingCase.setTaxFilingType(TaxFilingType.TAX_RETURN.value());

                    aG02TaxFilingCase.setEntityUuid(aG01TaxpayerCaseBk.getTaxpayerCaseUuid());
                    aG02TaxFilingCase.setEntityType(GardenEntityType.TAXPAYER_CASE.name());

                    aG02TaxFilingCase.setCreated(aG01TaxpayerCaseBk.getCreated());
                    aG02TaxFilingCase.setUpdated(aG01TaxpayerCaseBk.getUpdated());

                    GardenJpaUtils.storeEntity(em, G02TaxFilingCase.class, aG02TaxFilingCase, aG02TaxFilingCase.getTaxFilingUuid(), 
                            G02DataUpdaterFactory.getSingleton().getG02TaxFilingCaseUpdater());
                    
                    if (ZcaValidator.isNotNullEmpty(aG01TaxpayerCaseBk.getMemo())){
                        aG02Memo = new G02Memo(GardenData.generateUUIDString());
                        aG02Memo.setEntityStatus(GardenEntityType.TAX_FILING_CASE.value());
                        aG02Memo.setEntityType(GardenEntityType.TAX_FILING_CASE.name());
                        aG02Memo.setEntityUuid(aG02TaxFilingCase.getTaxFilingUuid());
                        aG02Memo.setMemo(aG01TaxpayerCaseBk.getMemo());
                        aG02Memo.setOperatorAccountUuid(businessOwnerAccountUuid);
                        aG02Memo.setTimestamp(new Date());

                        GardenJpaUtils.storeEntity(em, G02Memo.class, aG02Memo, aG02Memo.getMemoUuid(), 
                                G02DataUpdaterFactory.getSingleton().getG02MemoUpdater());
                    }
                    //transfer extension
                    if (aG01TaxpayerCaseBk.getExtensionDate() != null){
                        G02DeadlineExtension aG02DeadlineExtension = new G02DeadlineExtension(GardenData.generateUUIDString());
                        aG02DeadlineExtension.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
                        aG02DeadlineExtension.setEntityUuid(aG01TaxpayerCaseBk.getTaxpayerCaseUuid());
                        aG02DeadlineExtension.setExtensionDate(aG01TaxpayerCaseBk.getExtensionDate());
                        aG02DeadlineExtension.setExtensionMemo(aG01TaxpayerCaseBk.getExtensionMemo());
                        aG02DeadlineExtension.setOperatorAccountUuid(aG01TaxpayerCaseBk.getEmployeeAccountUuid());
                        aG02DeadlineExtension.setTimestamp(aG01TaxpayerCaseBk.getUpdated());

                        GardenJpaUtils.storeEntity(em, G02DeadlineExtension.class, aG02DeadlineExtension, aG02DeadlineExtension.getDeadlineExtensionUuid(), 
                                G02DataUpdaterFactory.getSingleton().getG02DeadlineExtensionUpdater());
                    }
                }
            }
        }
    }
    
    private void transferTaxpayerInfo(EntityManager em) throws Exception {
        List<G01TaxpayerInfo> aG01TaxpayerInfoList = GardenJpaUtils.findAll(em, G01TaxpayerInfo.class);
        System.out.println("transferTaxpayerInfo......");
        if (aG01TaxpayerInfoList != null){
            G02TaxpayerInfo aG02TaxpayerInfo;
            int counter = 0;
            G01User user;
            HashMap<String, Object> params = new HashMap<>();
            for (G01TaxpayerInfo aG01TaxpayerInfo : aG01TaxpayerInfoList){
                aG02TaxpayerInfo = new G02TaxpayerInfo();

                System.out.println("Record #" + (counter++)+ "/" + aG01TaxpayerInfoList.size() + "......");

                aG02TaxpayerInfo.setTaxpayerUserUuid(aG01TaxpayerInfo.getTaxpayerUserUuid());
                aG02TaxpayerInfo.setEmail(aG01TaxpayerInfo.getEmail());
                aG02TaxpayerInfo.setFax(aG01TaxpayerInfo.getFax());
                aG02TaxpayerInfo.setHomePhone(aG01TaxpayerInfo.getHomePhone());
                aG02TaxpayerInfo.setLengthOfLivingTogether(aG01TaxpayerInfo.getLengthOfLivingTogether());
                aG02TaxpayerInfo.setMemo(aG01TaxpayerInfo.getMemo());
                aG02TaxpayerInfo.setMobilePhone(aG01TaxpayerInfo.getMobilePhone());
                aG02TaxpayerInfo.setRelationships(aG01TaxpayerInfo.getRelationships());
                aG02TaxpayerInfo.setTaxpayerCaseUuid(aG01TaxpayerInfo.getTaxpayerCaseUuid());
                aG02TaxpayerInfo.setWorkPhone(aG01TaxpayerInfo.getWorkPhone());
                aG02TaxpayerInfo.setEntityStatus(aG01TaxpayerInfo.getEntityStatus());
                aG02TaxpayerInfo.setCreated(aG01TaxpayerInfo.getCreated());
                aG02TaxpayerInfo.setUpdated(aG01TaxpayerInfo.getUpdated());
                
                //user's information
                user = GardenJpaUtils.findById(em, G01User.class, aG01TaxpayerInfo.getTaxpayerUserUuid());
                if (user != null){
                    aG02TaxpayerInfo.setFirstName(user.getFirstName());
                    aG02TaxpayerInfo.setMiddleName(user.getMiddleName());
                    aG02TaxpayerInfo.setLastName(user.getLastName());
                    aG02TaxpayerInfo.setGender(user.getGender());
                    aG02TaxpayerInfo.setBirthday(user.getBirthday());
                    aG02TaxpayerInfo.setSsn(user.getSsn());
                    aG02TaxpayerInfo.setOccupation(user.getOccupation());
                    aG02TaxpayerInfo.setCitizenship(user.getCitizenship());
                    
//                    if (TaxpayerRelationship.PRIMARY_TAXPAYER.value().equalsIgnoreCase(aG01TaxpayerInfo.getRelationships())){
//                        params.put("entityUuid", user.getUserUuid());
//                        List<G01Location> aG01LocationList = GardenJpaUtils.findEntityListByNamedQuery(em, G01Location.class, "G01Location.findByEntityUuid", params);
//                        if ((aG01LocationList != null) && (!aG01LocationList.isEmpty())){
//                            G01Location aG01Location = aG01LocationList.get(0);
//                            G02Location aG02Location = new G02Location();
//
//                            aG02Location.setLocationUuid(GardenData.generateUUIDString());
//                            aG02Location.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
//                            aG02Location.setEntityUuid(aG02TaxpayerInfo.getTaxpayerCaseUuid());
//                            aG02Location.setCountry(aG01Location.getCountry());
//                            aG02Location.setLocalAddress(aG01Location.getLocalAddress());
//                            aG02Location.setCityName(aG01Location.getCityName());
//                            aG02Location.setStateCounty(aG01Location.getStateCounty());
//                            aG02Location.setStateName(aG01Location.getStateName());
//                            aG02Location.setZipCode(aG01Location.getZipCode());
//                            aG02Location.setPreferencePriority(aG01Location.getPreferencePriority());
//                            aG02Location.setShortMemo(aG01Location.getShortMemo());
//                            aG02Location.setEntityStatus(aG01Location.getEntityStatus());
//                            aG02Location.setCreated(aG01Location.getCreated());
//                            aG02Location.setUpdated(aG01Location.getUpdated());
//
//                            GardenJpaUtils.storeEntity(em, G02Location.class, aG02Location, aG02Location.getLocationUuid(), 
//                                    G02DataUpdaterFactory.getSingleton().getG02LocationUpdater());
//                        }
//                    }
                }
                
                GardenJpaUtils.storeEntity(em, G02TaxpayerInfo.class, aG02TaxpayerInfo, aG02TaxpayerInfo.getTaxpayerUserUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02TaxpayerInfoUpdater());
            }
        }
    }

    private void transferTlcLicense(EntityManager em) throws Exception {
        List<G01TlcLicense> aG01TlcLicenseList = GardenJpaUtils.findAll(em, G01TlcLicense.class);
        if (aG01TlcLicenseList != null){
            G02TlcLicense aG02TlcLicense;
            for (G01TlcLicense aG01TlcLicense : aG01TlcLicenseList){
                aG02TlcLicense = new G02TlcLicense();

                aG02TlcLicense.setDriverUuid(aG01TlcLicense.getDriverUuid());
                aG02TlcLicense.setTaxpayerCaseUuid(aG01TlcLicense.getTaxpayerCaseUuid());
                aG02TlcLicense.setAccessories(aG01TlcLicense.getAccessories());
                aG02TlcLicense.setBusinessMiles(aG01TlcLicense.getBusinessMiles());
                aG02TlcLicense.setCarWash(aG01TlcLicense.getCarWash());
                aG02TlcLicense.setDateInService(aG01TlcLicense.getDateInService());
                aG02TlcLicense.setDepreciation(aG01TlcLicense.getDepreciation());
                aG02TlcLicense.setGarageRent(aG01TlcLicense.getGarageRent());
                aG02TlcLicense.setGas(aG01TlcLicense.getGas());
                aG02TlcLicense.setInsurance(aG01TlcLicense.getInsurance());
                aG02TlcLicense.setLeasePayment(aG01TlcLicense.getLeasePayment());
                aG02TlcLicense.setMaintenance(aG01TlcLicense.getMaintenance());
                aG02TlcLicense.setMeals(aG01TlcLicense.getMeals());
                aG02TlcLicense.setMileageRate(aG01TlcLicense.getMileageRate());
                aG02TlcLicense.setNumberOfSeats(aG01TlcLicense.getNumberOfSeats());
                aG02TlcLicense.setOil(aG01TlcLicense.getOil());
                aG02TlcLicense.setOver600lbs(aG01TlcLicense.getOver600lbs());
                aG02TlcLicense.setParking(aG01TlcLicense.getParking());
                aG02TlcLicense.setRadioRepair(aG01TlcLicense.getRadioRepair());
                aG02TlcLicense.setRegistrationFee(aG01TlcLicense.getRegistrationFee());
                aG02TlcLicense.setRepairs(aG01TlcLicense.getRepairs());
                aG02TlcLicense.setServiceFee(aG01TlcLicense.getServiceFee());
                aG02TlcLicense.setTelephone(aG01TlcLicense.getTelephone());
                aG02TlcLicense.setTires(aG01TlcLicense.getTires());
                aG02TlcLicense.setTlcLicense(aG01TlcLicense.getTlcLicense());
                aG02TlcLicense.setTolls(aG01TlcLicense.getTolls());
                aG02TlcLicense.setTotalMiles(aG01TlcLicense.getTotalMiles());
                aG02TlcLicense.setUniform(aG01TlcLicense.getUniform());
                aG02TlcLicense.setVehicleModel(aG01TlcLicense.getVehicleModel());
                aG02TlcLicense.setVehicleType(aG01TlcLicense.getVehicleType());
                aG02TlcLicense.setMemo(aG01TlcLicense.getMemo());
                aG02TlcLicense.setEntityStatus(aG01TlcLicense.getEntityStatus());      
                aG02TlcLicense.setCreated(aG01TlcLicense.getCreated());
                aG02TlcLicense.setUpdated(aG01TlcLicense.getUpdated());
                
                GardenJpaUtils.storeEntity(em, G02TlcLicense.class, aG02TlcLicense, aG02TlcLicense.getDriverUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02TlcLicenseUpdater());
            }
        }
    }

    private void transferUser(EntityManager em) throws Exception {
        List<G01User> aG01UserList = GardenJpaUtils.findAll(em, G01User.class);
        System.out.println("transferUser......");
        if (aG01UserList != null){
            int counter = 0;
            G02User aG02User;
            for (G01User aG01User : aG01UserList){
                aG02User = new G02User();

                System.out.println("Record #" + (counter++)+ "/" + aG01UserList.size() + "......");

                aG02User.setUserUuid(aG01User.getUserUuid());
                aG02User.setBirthday(aG01User.getBirthday());
                aG02User.setFirstName(aG01User.getFirstName());
                aG02User.setGender(aG01User.getGender());
                aG02User.setLastName(aG01User.getLastName());
                aG02User.setMemo(aG01User.getMemo());
                aG02User.setMiddleName(aG01User.getMiddleName());
                aG02User.setSsn(aG01User.getSsn());
                aG02User.setCitizenship(aG01User.getCitizenship());
                aG02User.setOccupation(aG01User.getOccupation());
                aG02User.setEntityStatus(aG01User.getEntityStatus());
                aG02User.setCreated(aG01User.getCreated());
                aG02User.setUpdated(aG01User.getUpdated());
                
                GardenJpaUtils.storeEntity(em, G02User.class, aG02User, aG02User.getUserUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02UserUpdater());
            }
        }
    }

    private void transferUserBk(EntityManager em) throws Exception {
        List<G01UserBk> aG01UserBkList = GardenJpaUtils.findAll(em, G01UserBk.class);
        if (aG01UserBkList != null){
            G02UserBk aG02UserBk;
            for (G01UserBk aG01UserBk : aG01UserBkList){
                aG02UserBk = new G02UserBk();

                aG02UserBk.setUserUuid(aG01UserBk.getUserUuid());
                aG02UserBk.setBirthday(aG01UserBk.getBirthday());
                aG02UserBk.setFirstName(aG01UserBk.getFirstName());
                aG02UserBk.setGender(aG01UserBk.getGender());
                aG02UserBk.setLastName(aG01UserBk.getLastName());
                aG02UserBk.setMemo(aG01UserBk.getMemo());
                aG02UserBk.setMiddleName(aG01UserBk.getMiddleName());
                aG02UserBk.setSsn(aG01UserBk.getSsn());
                aG02UserBk.setCitizenship(aG01UserBk.getCitizenship());
                aG02UserBk.setOccupation(aG01UserBk.getOccupation());
                aG02UserBk.setEntityStatus(aG01UserBk.getEntityStatus());
                aG02UserBk.setCreated(aG01UserBk.getCreated());
                aG02UserBk.setUpdated(aG01UserBk.getUpdated());
                
                GardenJpaUtils.storeEntity(em, G02UserBk.class, aG02UserBk, aG02UserBk.getUserUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02UserBkUpdater());
            }
        }
    }

    private void transferWebPost(EntityManager em) throws Exception {
        List<G01WebPost> aG01WebPostList = GardenJpaUtils.findAll(em, G01WebPost.class);
        if (aG01WebPostList != null){
            G02WebPost aG02WebPost;
            for (G01WebPost aG01WebPost : aG01WebPostList){
                aG02WebPost = new G02WebPost();

                aG02WebPost.setWebPostUuid(aG01WebPost.getWebPostUuid());
                aG02WebPost.setAuthorAccountUuid(aG01WebPost.getAuthorAccountUuid());
                aG02WebPost.setPostBrief(aG01WebPost.getPostBrief());
                aG02WebPost.setPostPurpose(aG01WebPost.getPostPurpose());
                aG02WebPost.setPostTitle(aG01WebPost.getPostTitle());
                aG02WebPost.setEntityStatus(aG01WebPost.getEntityStatus());
                aG02WebPost.setCreated(aG01WebPost.getCreated());
                aG02WebPost.setUpdated(aG01WebPost.getUpdated());
                
                GardenJpaUtils.storeEntity(em, G02WebPost.class, aG02WebPost, aG02WebPost.getWebPostUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02WebPostUpdater());
            }
        }
    }

    private void transferWorkRole(EntityManager em) throws Exception {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void transferWorkTeam(EntityManager em) throws Exception {
        List<G01WorkTeam> aG01WorkTeamList = GardenJpaUtils.findAll(em, G01WorkTeam.class);
        if (aG01WorkTeamList != null){
            G02WorkTeam aG02WorkTeam;
            for (G01WorkTeam aG01WorkTeam : aG01WorkTeamList){
                aG02WorkTeam = new G02WorkTeam();

                aG02WorkTeam.setWorkTeamUuid(aG01WorkTeam.getWorkTeamUuid());
                aG02WorkTeam.setTeamName(aG01WorkTeam.getTeamName());
                aG02WorkTeam.setDescription(aG01WorkTeam.getDescription());
                aG02WorkTeam.setCreated(aG01WorkTeam.getCreated());
                aG02WorkTeam.setUpdated(aG01WorkTeam.getUpdated());
                
                GardenJpaUtils.storeEntity(em, G02WorkTeam.class, aG02WorkTeam, aG02WorkTeam.getWorkTeamUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02WorkTeamUpdater());
            }
        }
    }

    private void transferWorkTeamHasEmployee(EntityManager em) throws Exception {
        List<G01WorkTeamHasEmployee> aG01WorkTeamHasEmployeeList = GardenJpaUtils.findAll(em, G01WorkTeamHasEmployee.class);
        if (aG01WorkTeamHasEmployeeList != null){
            G02WorkTeamHasEmployee aG02WorkTeamHasEmployee;
            for (G01WorkTeamHasEmployee aG01WorkTeamHasEmployee : aG01WorkTeamHasEmployeeList){
                aG02WorkTeamHasEmployee = new G02WorkTeamHasEmployee();

                aG02WorkTeamHasEmployee.setG02WorkTeamHasEmployeePK(new G02WorkTeamHasEmployeePK());
                aG02WorkTeamHasEmployee.getG02WorkTeamHasEmployeePK().setEmployeeUuid(aG01WorkTeamHasEmployee.getG01WorkTeamHasEmployeePK().getEmployeeUuid());
                aG02WorkTeamHasEmployee.getG02WorkTeamHasEmployeePK().setWorkTeamUuid(aG01WorkTeamHasEmployee.getG01WorkTeamHasEmployeePK().getWorkTeamUuid());
                aG02WorkTeamHasEmployee.setTitleInTeam(aG01WorkTeamHasEmployee.getTitleInTeam());
                aG02WorkTeamHasEmployee.setOperatorAccountUuid(aG01WorkTeamHasEmployee.getOperatorAccountUuid());
                aG02WorkTeamHasEmployee.setTimestamp(aG01WorkTeamHasEmployee.getTimestamp());
                
                GardenJpaUtils.storeEntity(em, G02WorkTeamHasEmployee.class, aG02WorkTeamHasEmployee, aG02WorkTeamHasEmployee.getG02WorkTeamHasEmployeePK(), 
                        G02DataUpdaterFactory.getSingleton().getG02WorkTeamHasEmployeeUpdater());
            }
        }
    }

    private void transferWorkTeamHasWorkRole(EntityManager em) throws Exception {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void transferXmppAccount(EntityManager em) throws Exception {
        List<G01XmppAccount> aG01XmppAccountList = GardenJpaUtils.findAll(em, G01XmppAccount.class);
        if (aG01XmppAccountList != null){
            G02XmppAccount aG02XmppAccount;
            for (G01XmppAccount aG01XmppAccount : aG01XmppAccountList){
                aG02XmppAccount = new G02XmppAccount();

                aG02XmppAccount.setXmppAccountUuid(aG01XmppAccount.getXmppAccountUuid());
                aG02XmppAccount.setLoginName(aG01XmppAccount.getLoginName());
                aG02XmppAccount.setEncryptedPassword(aG01XmppAccount.getEncryptedPassword());
                aG02XmppAccount.setPassword(aG01XmppAccount.getPassword());
                aG02XmppAccount.setCreated(aG01XmppAccount.getCreated());
                aG02XmppAccount.setUpdated(aG01XmppAccount.getUpdated());
                
                GardenJpaUtils.storeEntity(em, G02XmppAccount.class, aG02XmppAccount, aG02XmppAccount.getXmppAccountUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02XmppAccountUpdater());
            }
        }
    }

    private void fixG01FisicalTypoIssueHelper(EntityManager em) throws Exception {
        List<G01TaxFilingType> aG01TaxFilingTypeList = GardenJpaUtils.findAll(em, G01TaxFilingType.class);
        G01TaxFilingType theG01TaxFilingType;
        G01TaxFilingTypePK pkid;
        for (G01TaxFilingType aG01TaxFilingType : aG01TaxFilingTypeList){
            if ("Fisical".equalsIgnoreCase(aG01TaxFilingType.getG01TaxFilingTypePK().getTaxFilingPeriod())){
                pkid = new G01TaxFilingTypePK();
                pkid.setTaxFilingPeriod(TaxFilingPeriod.FISCAL.value());
                pkid.setTaxFilingType(aG01TaxFilingType.getG01TaxFilingTypePK().getTaxFilingType());
                pkid.setTaxcorpCaseUuid(aG01TaxFilingType.getG01TaxFilingTypePK().getTaxcorpCaseUuid());
                theG01TaxFilingType = GardenJpaUtils.findById(em, G01TaxFilingType.class, pkid);
                if (theG01TaxFilingType != null){
                    GardenJpaUtils.deleteEntity(em, G01TaxFilingType.class, aG01TaxFilingType.getG01TaxFilingTypePK());
                }
            }
        }//for
    }

    /**
     * after add a new memo column in G02TaxpayerCase, it demands to fill memos from G02TaxFilingCase and G02Memo
     */
    public void transferMemosIntoG02TaxpayerCase() {
        transferFromG02TaxFilingCaseToG02TaxpayerCase();
        transferFromG02MemoToG02TaxpayerCase();
    }
    
    private void transferFromG02TaxFilingCaseToG02TaxpayerCase(){
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
            System.out.println("transferMemosIntoG02TaxpayerCase...after add a new memo column in G02TaxpayerCase, it demands to fill memos from G02TaxFilingCase and G02Memo");
            
            transferFromG02TaxFilingCaseToG02TaxpayerCaseHelper(em);
            
            System.out.println("committing...");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        System.out.println("Database update completed.");
    }

    private void transferFromG02TaxFilingCaseToG02TaxpayerCaseHelper(EntityManager em) throws Exception {
        /**
         * Transfer memo from G02TaxFilingCase
         */
        HashMap<String, Object> params = new HashMap<>();
        params.put("entityType", GardenEntityType.TAXPAYER_CASE.name());
        List<G02TaxFilingCase> aG02TaxFilingCaseList = GardenJpaUtils.findEntityListByNamedQuery(em, G02TaxFilingCase.class, "G02TaxFilingCase.findByEntityType", params);
        G02TaxpayerCase aG02TaxpayerCase;
        G02TaxpayerCaseBk aG02TaxpayerCaseBk;
        for (G02TaxFilingCase aG02TaxFilingCase : aG02TaxFilingCaseList){
            if (ZcaValidator.isNotNullEmpty(aG02TaxFilingCase.getMemo())){
                aG02TaxpayerCase = GardenJpaUtils.findById(em, G02TaxpayerCase.class, aG02TaxFilingCase.getEntityUuid());
                if (aG02TaxpayerCase == null){
                    aG02TaxpayerCaseBk = GardenJpaUtils.findById(em, G02TaxpayerCaseBk.class, aG02TaxFilingCase.getEntityUuid());
                    if (aG02TaxpayerCaseBk != null){
                        aG02TaxpayerCaseBk.setMemo(aG02TaxFilingCase.getMemo());
                        GardenJpaUtils.storeEntity(em, G02TaxpayerCaseBk.class, aG02TaxpayerCaseBk, aG02TaxpayerCaseBk.getTaxpayerCaseUuid(), 
                                                   G02DataUpdaterFactory.getSingleton().getG02TaxpayerCaseBkUpdater());
                        GardenJpaUtils.deleteEntity(em, G02TaxFilingCase.class, aG02TaxFilingCase.getTaxFilingUuid());
                    }
                }else{
                    aG02TaxpayerCase.setMemo(aG02TaxFilingCase.getMemo());
                    GardenJpaUtils.storeEntity(em, G02TaxpayerCase.class, aG02TaxpayerCase, aG02TaxpayerCase.getTaxpayerCaseUuid(), 
                                               G02DataUpdaterFactory.getSingleton().getG02TaxpayerCaseUpdater());
                    GardenJpaUtils.deleteEntity(em, G02TaxFilingCase.class, aG02TaxFilingCase.getTaxFilingUuid());
                }
            }
        }//for
    }
    
    private void transferFromG02MemoToG02TaxpayerCase(){
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
            System.out.println("transferMemosIntoG02TaxpayerCase...after add a new memo column in G02TaxpayerCase, it demands to fill memos from G02TaxFilingCase and G02Memo");
            
            transferFromG02MemoToG02TaxpayerCaseHelper(em);
            
            System.out.println("committing...");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        System.out.println("Database update completed.");
    }

    private void transferFromG02MemoToG02TaxpayerCaseHelper(EntityManager em) throws Exception {
        /**
         * Transfer memo from G02TaxFilingCase "accumulatively"!!!
         */
        HashMap<String, Object> params = new HashMap<>();
        params.put("entityType", GardenEntityType.TAXPAYER_CASE.name());
        List<G02Memo> aG02MemoList = GardenJpaUtils.findEntityListByNamedQuery(em, G02Memo.class, "G02Memo.findByEntityType", params);
        G02TaxpayerCase aG02TaxpayerCase;
        G02TaxpayerCaseBk aG02TaxpayerCaseBk;
        for (G02Memo aG02Memo : aG02MemoList){
            if (ZcaValidator.isNotNullEmpty(aG02Memo.getMemo())){
                aG02TaxpayerCase = GardenJpaUtils.findById(em, G02TaxpayerCase.class, aG02Memo.getEntityUuid());
                if (aG02TaxpayerCase == null){
                    aG02TaxpayerCaseBk = GardenJpaUtils.findById(em, G02TaxpayerCaseBk.class, aG02Memo.getEntityUuid());
                    if (aG02TaxpayerCaseBk != null){
                        if (ZcaValidator.isNullEmpty(aG02TaxpayerCaseBk.getMemo())){
                            aG02TaxpayerCaseBk.setMemo(aG02Memo.getMemo());
                        }else{
                            aG02TaxpayerCaseBk.setMemo(aG02TaxpayerCaseBk.getMemo() + " || " + aG02Memo.getMemo());
                        }
                        GardenJpaUtils.storeEntity(em, G02TaxpayerCaseBk.class, aG02TaxpayerCaseBk, aG02TaxpayerCaseBk.getTaxpayerCaseUuid(), 
                                                   G02DataUpdaterFactory.getSingleton().getG02TaxpayerCaseBkUpdater());
                        GardenJpaUtils.deleteEntity(em, G02Memo.class, aG02Memo.getMemoUuid());
                    }
                }else{
                    if (ZcaValidator.isNullEmpty(aG02TaxpayerCase.getMemo())){
                        aG02TaxpayerCase.setMemo(aG02Memo.getMemo());
                    }else{
                        aG02TaxpayerCase.setMemo(aG02TaxpayerCase.getMemo() + " || " + aG02Memo.getMemo());
                    }
                    aG02TaxpayerCase.setMemo(aG02Memo.getMemo());
                    GardenJpaUtils.storeEntity(em, G02TaxpayerCase.class, aG02TaxpayerCase, aG02TaxpayerCase.getTaxpayerCaseUuid(), 
                                               G02DataUpdaterFactory.getSingleton().getG02TaxpayerCaseUpdater());
                    GardenJpaUtils.deleteEntity(em, G02Memo.class, aG02Memo.getMemoUuid());
                }
            }
        }//for
    }
    
    public void changeFileExtensionToBeLowerCase(){
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
            System.out.println("changeFileExtensionToBeLowerCase...change archived file's extension to be lower case");
            
            changeFileExtensionToBeLowerCaseHelper(em);
            
            System.out.println("committing...");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        System.out.println("Database update completed.");
    }

    private void changeFileExtensionToBeLowerCaseHelper(EntityManager em) {
        Path archiveRootPath = GardenEnvironment.getServerSideArchivedFileRootPath(GardenFlower.PEONY);
        Path archiveBackupRootPath = GardenEnvironment.getServerSideBackupArchivedFileRootPath(GardenFlower.PEONY);
        //process every file according to the database
        List<G02ArchivedFile> aG02ArchivedFileList = GardenJpaUtils.findAll(em, G02ArchivedFile.class);
        String originalFileName;
        for (G02ArchivedFile aG02ArchivedFile : aG02ArchivedFileList){
            if (ZcaValidator.isNotNullEmpty(aG02ArchivedFile.getFileExtension())){
                originalFileName = aG02ArchivedFile.getFileUuid()+ "." + aG02ArchivedFile.getFileExtension();
                if (!originalFileName.equals(originalFileName.toLowerCase())){ //in linux, they are differen
                    try {
                        changeFileExtension(em, aG02ArchivedFile, archiveRootPath.resolve(originalFileName), archiveBackupRootPath.resolve(originalFileName));
                    } catch (Exception ex) {
                        Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        //process every file according to the file system
        String originalFileUuid;
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(archiveRootPath)) {
            for (Path filePath : directoryStream) {
                if (Files.isRegularFile(filePath)){
                    originalFileName = FilenameUtils.getName(filePath.toAbsolutePath().toString());
                    if (!originalFileName.equals(originalFileName.toLowerCase())){ //in linux, they are differen
                        try{
                            changePhysicalFileExtension(filePath, archiveBackupRootPath.resolve(originalFileName));
                        }catch (IOException ex){}
                        originalFileUuid = FilenameUtils.getBaseName(filePath.toAbsolutePath().toString());
                        try{
                            changeG02FileExtension(em, GardenJpaUtils.findById(em, G02ArchivedFile.class, originalFileUuid));
                        }catch (ZcaEntityValidationException ex){}
                    }
                }
            }//for-loop
        } catch (IOException ex) {}
    }
    
    private void changeG02FileExtension(EntityManager em, G02ArchivedFile aG02ArchivedFile) throws ZcaEntityValidationException{
        if (aG02ArchivedFile == null){
            return;
        }
        if (aG02ArchivedFile.getFileExtension() != null){
            aG02ArchivedFile.setFileExtension(aG02ArchivedFile.getFileExtension().toLowerCase());
        }
        GardenJpaUtils.storeEntity(em, G02ArchivedFile.class, aG02ArchivedFile, aG02ArchivedFile.getFileUuid(), 
                G02DataUpdaterFactory.getSingleton().getG02ArchivedFileUpdater());
    
    }
    
    private void changePhysicalFileExtension(Path originalFilePathWithName, Path backupFilePathWithName) throws IOException{
        if (ZcaNio.isValidFile(originalFilePathWithName.toString())){
            ZcaNio.copyFile(originalFilePathWithName.toString(), backupFilePathWithName.toString());
            if (!ZcaNio.isValidFile(originalFilePathWithName.toString().toLowerCase())){ //in linux, they are differen
                ZcaNio.copyFile(originalFilePathWithName.toString(), originalFilePathWithName.toString().toLowerCase());
                ZcaNio.deleteFile(originalFilePathWithName.toString());
            }
        }
    }
    
    private void changeFileExtension(EntityManager em, G02ArchivedFile aG02ArchivedFile, Path originalFilePathWithName, Path backupFilePathWithName) throws ZcaEntityValidationException, IOException{
        //change the physical file's extension
        changePhysicalFileExtension(originalFilePathWithName, backupFilePathWithName);
        //change the file's extension in the database
        changeG02FileExtension(em, aG02ArchivedFile);
    }
    
    public void synchronizePathsForOfflineEmails(){
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
            System.out.println("synchronizePathsForOfflineEmails...Synchronize paths of offline emails and their attachments with employee's paths in system settings");
            
            synchronizePathsForOfflineEmailsHelper(em);
            
            System.out.println("committing...");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        System.out.println("Database update completed.");
    }
    
    private void synchronizePathsForOfflineEmailsHelper(EntityManager em) throws NonUniqueEntityException, ZcaEntityValidationException{
        List<G02Account> employeeAccounts = GardenJpaUtils.findCurrentEmployeeAccountList(em);
        Path emailRootPath;
        HashSet<String> filter = new HashSet<>();
        filter.add("0bda7443z7c4dz46b1za471z954055c9e686");
        for (G02Account employeeAccount : employeeAccounts){
            if (filter.isEmpty()){
                break;
            }
            if (filter.contains(employeeAccount.getAccountUuid())){
                emailRootPath = GardenJpaUtils.findLocalOfficeEmailSerializationRootPath(em, 
                        employeeAccount.getLoginName(), RoseWebCipher.getSingleton().decrypt(employeeAccount.getEncryptedPassword()));
                GardenJpaUtils.synchronizeEmployeePathsForOfflineEmails(em, emailRootPath, employeeAccount, TechnicalController.GOOGLE_EMAIL.value());
            }
            filter.remove(employeeAccount.getAccountUuid());
        }
    }
    
    public void updateG02JobAssignmentStatus(){
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
            System.out.println("updateG02JobAssignmentStatus...start updating");
            
            updateG02JobAssignmentStatusHelper(em);
            
            System.out.println("committing...");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        System.out.println("Database update completed.");
    }
    
    public void updateLocalHostForDevelopment(){
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
            System.out.println("updateLocalHostForDevelopment...update production path to be local paths");
            
            updateLocalHostForDevelopmentHelper(em);
            
            System.out.println("committing...");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        System.out.println("Database update completed.");
    }
    
    private void updateG02JobAssignmentStatusHelper(EntityManager em) throws ZcaEntityValidationException {
        //select assignments
        List<G02DailyReport> reportList = GardenJpaUtils.findAll(em, G02DailyReport.class);
        if (reportList != null){
            TreeSet<String> completedUuids = new TreeSet<>();
            TreeSet<String> inprogressUuids = new TreeSet<>();
            for (G02DailyReport report : reportList){
                if (report.getWorkingProgress() != null){
                    if (report.getWorkingProgress() == 100){
                        completedUuids.add(report.getG02DailyReportPK().getJobAssignmentUuid());
                    }else if (report.getWorkingProgress() > 0){
                        inprogressUuids.add(report.getG02DailyReportPK().getJobAssignmentUuid());
                    }
                }
            }//for-loop
            List<G02JobAssignment> jobList = GardenJpaUtils.findAll(em, G02JobAssignment.class);
            for (G02JobAssignment job : jobList){
                if (completedUuids.contains(job.getJobAssignmentUuid())){
                    job.setAssignmentStatus(JobAssignmentStatus.COMPLETED.value());
                }else if (inprogressUuids.contains(job.getJobAssignmentUuid())){
                    job.setAssignmentStatus(JobAssignmentStatus.IN_PROGRESS.value());
                }else{
                    job.setAssignmentStatus(JobAssignmentStatus.INITIAL.value());
                }
                GardenJpaUtils.storeEntity(em, G02JobAssignment.class, job, job.getJobAssignmentUuid(), 
                                               G02DataUpdaterFactory.getSingleton().getG02JobAssignmentUpdater());
            }//for-loop
        }
    }
    
    private void updateLocalHostForDevelopmentHelper(EntityManager em) throws ZcaEntityValidationException {
        final String serverPath = "\\\\D21YWQP2\\Shared\\Garden\\Peony\\R20190805";
        final String localPath = "C:\\Users\\zhijun98\\PEONY";
        //g02_system_property
        List<G02SystemProperty> aG02SystemPropertyList = GardenJpaUtils.findAll(em, G02SystemProperty.class);
        for (G02SystemProperty aG02SystemProperty : aG02SystemPropertyList){
            if ((aG02SystemProperty.getPropertyValue() != null) && (aG02SystemProperty.getPropertyValue().contains(serverPath))){
                System.out.println(">>> g02_system_property OLD path: " + aG02SystemProperty.getPropertyValue());
                aG02SystemProperty.setPropertyValue(aG02SystemProperty.getPropertyValue().replace(serverPath, localPath));
                GardenJpaUtils.storeEntity(em, G02SystemProperty.class, aG02SystemProperty, aG02SystemProperty.getG02SystemPropertyPK(), 
                                               G02DataUpdaterFactory.getSingleton().getG02SystemPropertyUpdater());
                System.out.println("--- g02_system_property NEW path: " + aG02SystemProperty.getPropertyValue());
            }
        }
        //g02_archived_file
        List<G02ArchivedFile> aG02ArchivedFileList = GardenJpaUtils.findAll(em, G02ArchivedFile.class);
        for (G02ArchivedFile aG02ArchivedFile : aG02ArchivedFileList){
            if ((aG02ArchivedFile.getFilePath()!= null) && (aG02ArchivedFile.getFilePath().contains(serverPath))){
                System.out.println(">>> g02_archived_file OLD path: " + aG02ArchivedFile.getFilePath());
                aG02ArchivedFile.setFilePath(aG02ArchivedFile.getFilePath().replace(serverPath, localPath));
                GardenJpaUtils.storeEntity(em, G02ArchivedFile.class, aG02ArchivedFile, aG02ArchivedFile.getFileUuid(), 
                                               G02DataUpdaterFactory.getSingleton().getG02ArchivedFileUpdater());
                System.out.println("--- g02_archived_file NEW path: " + aG02ArchivedFile.getFilePath());
            }
        }
        //g02_offline_email
        List<G02OfflineEmail> aG02OfflineEmailList = GardenJpaUtils.findAll(em, G02OfflineEmail.class);
        for (G02OfflineEmail aG02OfflineEmail : aG02OfflineEmailList){
            if ((aG02OfflineEmail.getMessagePath()!= null) && (aG02OfflineEmail.getMessagePath().contains(serverPath))){
                System.out.println(">>> g02_offline_email OLD path: " + aG02OfflineEmail.getMessagePath());
                aG02OfflineEmail.setMessagePath(aG02OfflineEmail.getMessagePath().replace(serverPath, localPath));
                GardenJpaUtils.storeEntity(em, G02OfflineEmail.class, aG02OfflineEmail, aG02OfflineEmail.getMsgId(), 
                                               G02DataUpdaterFactory.getSingleton().getG02OfflineEmailUpdater());
                System.out.println("--- g02_offline_email NEW path: " + aG02OfflineEmail.getMessagePath());
            }
        }
    }
    
    public void updateG02SystemSettings(){
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
            System.out.println("updateG02SystemSettings...Update G02SystemSettings for every employees according to data in G02SystemProperties");
            
            updateG02SystemSettingsHelper(em);
            
            System.out.println("committing...");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        System.out.println("Database update completed.");
    }
    
    private void updateG02SystemSettingsHelper(EntityManager em) throws ZcaEntityValidationException {
        //g02_employee
        List<G02Employee> aG02EmployeeList = GardenJpaUtils.findAll(em, G02Employee.class);
        //g02_system_property
        List<G02SystemProperty> aG02SystemPropertyList = GardenJpaUtils.findAll(em, G02SystemProperty.class);
        for (G02SystemProperty aG02SystemProperty : aG02SystemPropertyList){
            //Only PEONY
            if (GardenFlower.PEONY.name().equalsIgnoreCase(aG02SystemProperty.getG02SystemPropertyPK().getFlowerName())){
                updateG02SystemSettingsForPeony(em, aG02SystemProperty, aG02EmployeeList);
            }
            //Only ROSE
            if (GardenFlower.ROSE.name().equalsIgnoreCase(aG02SystemProperty.getG02SystemPropertyPK().getFlowerName())){
                updateG02SystemSettingsForRose(em, aG02SystemProperty);
            }
        }//for-loop
    }

    private void updateG02SystemSettingsForRose(EntityManager em, G02SystemProperty aG02SystemProperty) throws ZcaEntityValidationException {
        G02SystemSettings aG02SystemSettings = new G02SystemSettings();
        G02SystemSettingsPK pkid = new G02SystemSettingsPK();   
        pkid.setFlowerUserUuid(GardenMaster.Master_UUID.value());
        pkid.setPropertyName(aG02SystemProperty.getG02SystemPropertyPK().getPropertyName());
        pkid.setPropertyValue(aG02SystemProperty.getPropertyValue());
        aG02SystemSettings.setG02SystemSettingsPK(pkid);
        aG02SystemSettings.setCreated(aG02SystemProperty.getCreated());
        aG02SystemSettings.setDescription(aG02SystemProperty.getDescription());
        aG02SystemSettings.setPurpose(SystemSettingsPurpose.ROSE_WEB_GLOBAL.name());
        aG02SystemSettings.setFlowerName(aG02SystemProperty.getG02SystemPropertyPK().getFlowerName());
        aG02SystemSettings.setFlowerOwner(aG02SystemProperty.getG02SystemPropertyPK().getFlowerOwner());

        GardenJpaUtils.storeEntity(em, G02SystemSettings.class, aG02SystemSettings, pkid, G02DataUpdaterFactory.getSingleton().getG02SystemSettingsUpdater());
    }

    private void updateG02SystemSettingsForPeony(EntityManager em, G02SystemProperty aG02SystemProperty, List<G02Employee> aG02EmployeeList) throws ZcaEntityValidationException {
        G02SystemSettings aG02SystemSettings;
        G02SystemSettingsPK pkid;
        for (G02Employee aG02Employee : aG02EmployeeList){
            aG02SystemSettings = new G02SystemSettings();
            pkid = new G02SystemSettingsPK();
            pkid.setFlowerUserUuid(aG02Employee.getEmployeeAccountUuid());
            pkid.setPropertyName(aG02SystemProperty.getG02SystemPropertyPK().getPropertyName());
            pkid.setPropertyValue(aG02SystemProperty.getPropertyValue());
            aG02SystemSettings.setG02SystemSettingsPK(pkid);
            aG02SystemSettings.setCreated(aG02SystemProperty.getCreated());
            aG02SystemSettings.setDescription(aG02SystemProperty.getDescription());
            aG02SystemSettings.setPurpose(SystemSettingsPurpose.LOCAL_OFFICE.name());
            aG02SystemSettings.setFlowerName(aG02SystemProperty.getG02SystemPropertyPK().getFlowerName());
            aG02SystemSettings.setFlowerOwner(aG02SystemProperty.getG02SystemPropertyPK().getFlowerOwner());
            
            GardenJpaUtils.storeEntity(em, G02SystemSettings.class, aG02SystemSettings, pkid, G02DataUpdaterFactory.getSingleton().getG02SystemSettingsUpdater());
        }
    }
    
    /**
     * Move G02ArchivedDocument's data in 2018 to G02ArchivedFile table for Peony to display
     */
    public void updateG02ArchivedFile() {
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
            System.out.println("updateG02ArchivedFile...move G02ArchivedDocument's data in 2018 to G02ArchivedFile table for Peony to display");
            
            updateG02ArchivedFileHelper(em);
            
            System.out.println("committing...");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        System.out.println("Database update completed.");
    }

    private void updateG02ArchivedFileHelper(EntityManager em) throws ZcaEntityValidationException {
        List<G02ArchivedDocument> aG02ArchivedDocumentList = GardenJpaUtils.findAll(em, G02ArchivedDocument.class);
        G02ArchivedFile aG02ArchivedFile;
        for (G02ArchivedDocument aG02ArchivedDocument : aG02ArchivedDocumentList){
            aG02ArchivedFile = new G02ArchivedFile();
            aG02ArchivedFile.setCreated(aG02ArchivedDocument.getCreated());
            aG02ArchivedFile.setEntityType(aG02ArchivedDocument.getEntityType());
            aG02ArchivedFile.setEntityUuid(aG02ArchivedDocument.getEntityUuid());
            aG02ArchivedFile.setEntityStatus(aG02ArchivedDocument.getEntityStatus());
            if (ZcaValidator.isNotNullEmpty(aG02ArchivedDocument.getFileName())){
                aG02ArchivedFile.setFileExtension(ZcaNio.parseFileExtension(aG02ArchivedDocument.getFileName()));
                if (aG02ArchivedFile.getFileExtension() != null){
                    aG02ArchivedFile.setFileExtension(aG02ArchivedFile.getFileExtension().toLowerCase());
                }
            }
            if (!"TAXPAYER_CASE".equalsIgnoreCase(aG02ArchivedDocument.getEntityType())){
                aG02ArchivedFile.setFileFaceFolder(aG02ArchivedDocument.getFileStatus());
            }
            aG02ArchivedFile.setFileFaceName(aG02ArchivedDocument.getFileCustomName());
            aG02ArchivedFile.setFileMemo(aG02ArchivedDocument.getMemo());
            aG02ArchivedFile.setFilePath(null); //deprecated (10/06/2019).
            aG02ArchivedFile.setFileTimestamp(aG02ArchivedDocument.getFileTimestamp());
            aG02ArchivedFile.setOperatorAccountUuid(aG02ArchivedDocument.getProviderUuid());
            aG02ArchivedFile.setUpdated(aG02ArchivedDocument.getUpdated());
            aG02ArchivedFile.setFileUuid(aG02ArchivedDocument.getArchivedDocumentUuid());
            
            GardenJpaUtils.storeEntity(em, G02ArchivedFile.class, aG02ArchivedFile, aG02ArchivedFile.getFileUuid(), 
                                           G02DataUpdaterFactory.getSingleton().getG02ArchivedFileUpdater());
        }
    }
    
    public void moveTaxpayerCaseMemo(){
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            initializeBusinessOwnerAccountUuid(em);
            
            System.out.println("moveTaxpayerCaseMemo...copy G02TaxpayerCase's Memo into G02Memo so as to display memo in the forum-like Taxpayer-Memo panel");
            List<G02TaxpayerCase> aG02TaxpayerCaseList = GardenJpaUtils.findAll(em, G02TaxpayerCase.class);
            G02Memo memo;
            for (G02TaxpayerCase aG02TaxpayerCase : aG02TaxpayerCaseList){
                if (ZcaValidator.isNotNullEmpty(aG02TaxpayerCase.getMemo())){
                    memo = new G02Memo();
                    memo.setEntityComboUuid(null);
                    memo.setEntityStatus(GardenEntityType.TAXPAYER_CASE.value());
                    memo.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
                    memo.setEntityUuid(aG02TaxpayerCase.getTaxpayerCaseUuid());
                    memo.setInitialMemoUuid(null);
                    memo.setMemo(aG02TaxpayerCase.getMemo());
                    memo.setMemoDescription(null);
                    memo.setMemoType(null);
                    memo.setMemoUuid(ZcaUtils.generateUUIDString());
                    memo.setOperatorAccountUuid(TechnicalController.Controller_UUID.value());
                    memo.setTimestamp(aG02TaxpayerCase.getUpdated());
                    GardenJpaUtils.storeEntity(em, G02Memo.class, memo, memo.getMemoUuid(), G02DataUpdaterFactory.getSingleton().getG02MemoUpdater());
                    
                    aG02TaxpayerCase.setMemo(null);
                    GardenJpaUtils.storeEntity(em, G02TaxpayerCase.class, aG02TaxpayerCase, aG02TaxpayerCase.getTaxpayerCaseUuid(), G02DataUpdaterFactory.getSingleton().getG02TaxpayerCaseUpdater());
                    System.out.println("Copy memo: taxpayer case uuid = " + aG02TaxpayerCase.getTaxpayerCaseUuid());
                }
            }
            
            System.out.println("committing...");
            utx.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        System.out.println("Database update completed.");
    }
    
    public void mergeRedundantDocumentTags(){
        List<G02DocumentTag> aCulpritDocumentTags = null;
        EntityManager em = null;
        try {
            em = getEntityManager();
            initializeBusinessOwnerAccountUuid(em);
            System.out.println("mergeRedundantDocumentTags...start updating");
            aCulpritDocumentTags = selectCulpritG02DocumentTags(em);
            
        } catch (Exception ex) {
            Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        if (aCulpritDocumentTags != null){
            UserTransaction utx =  getUserTransaction();
            try {
                utx.begin();
                em = getEntityManager();
                for (G02DocumentTag aDocumentTag : aCulpritDocumentTags){
                    System.out.println("delete...aDocumentTag.getDocumentTagUuid()");
                    GardenJpaUtils.deleteEntity(em, G02DocumentTag.class, aDocumentTag.getDocumentTagUuid());
                }
                System.out.println("committing...");
                utx.commit();

            } catch (Exception ex) {
                Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    utx.rollback();
                } catch (IllegalStateException | SecurityException | SystemException ex1) {
                    Logger.getLogger(GardenDataTransfer.class.getName()).log(Level.SEVERE, null, ex1);
                }
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        }
        System.out.println("Database update completed.");
    }

    private List<G02DocumentTag> selectCulpritG02DocumentTags(EntityManager em) {
        String sqlQuery = "SELECT g FROM G02TaxpayerCase g WHERE g.deadline BETWEEN :fromDate AND :toDate ";
        Date fromDate = ZcaCalendar.createDate(2021, 2, 15, 0, 0, 0);
        Date toDate = ZcaCalendar.createDate(2021, 5, 15, 0, 0, 0);
        HashMap<String, Object> params = new HashMap<>();
        params.put("fromDate", fromDate);
        params.put("toDate", toDate);
        List<G02TaxpayerCase> aG02TaxpayerCaseList = GardenJpaUtils.findEntityListByQuery(em, G02TaxpayerCase.class, sqlQuery, params);
        List<G02DocumentTag> result = new ArrayList<>();
        if (aG02TaxpayerCaseList != null){
            for (G02TaxpayerCase aG02TaxpayerCase : aG02TaxpayerCaseList){
                result.addAll(findRedundantDocumentTags(em, aG02TaxpayerCase));
            }
        }
        return result;
    }

    private List<G02DocumentTag> findRedundantDocumentTags(EntityManager em, G02TaxpayerCase aG02TaxpayerCase) {
        List<G02DocumentTag> result = new ArrayList<>();
        HashMap<String, Object> params = new HashMap<>();
        params.put("fileUuid", aG02TaxpayerCase.getTaxpayerCaseUuid());
        List<G02DocumentTag> aG02DocumentTagList = GardenJpaUtils.findEntityListByNamedQuery(em, G02DocumentTag.class, "G02DocumentTag.findByFileUuid", params);
        HashMap<String, List<G02DocumentTag>> aG02DocumentTagMap = new HashMap<>();
        List<G02DocumentTag> tags;
        //ceate aG02DocumentTagMap
        for (G02DocumentTag aG02DocumentTag : aG02DocumentTagList){
            if (!PeonyPredefinedDocumentTagType.CUSTOM.value().equalsIgnoreCase(aG02DocumentTag.getDocumentTagType())){
                tags = aG02DocumentTagMap.get(aG02DocumentTag.getDocumentTagName()+aG02DocumentTag.getDocumentTagType());
                if (tags == null){
                    tags = new ArrayList<>();
                    aG02DocumentTagMap.put(aG02DocumentTag.getDocumentTagName()+aG02DocumentTag.getDocumentTagType(), tags);
                }
                tags.add(aG02DocumentTag);
            }
        }//for-loop
        //find the culprits
        Set<String> keys = aG02DocumentTagMap.keySet();
        Iterator<String> itr = keys.iterator();
        List<G02DocumentTag> redundantTags;
        while (itr.hasNext()){
            redundantTags = aG02DocumentTagMap.get(itr.next());
            if (redundantTags.size() > 1){
                GardenSorter.sortG02DocumentTagByQty(redundantTags, true);
                result.addAll(redundantTags);
                result.remove(redundantTags.get(0));    //keep the one whose qty is the highest
            }
        }
        return result;
    }
}
