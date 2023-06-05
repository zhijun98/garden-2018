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
package com.zcomapproach.garden.rose.persistence;

import com.zcomapproach.garden.data.GardenFlower;
import com.zcomapproach.garden.data.constant.GardenBooleanValue;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.persistence.GardenJpaUtils;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G02ArchivedDocument;
import com.zcomapproach.garden.persistence.entity.G02ArchivedFile;
import com.zcomapproach.garden.persistence.entity.G02ArchivedFileBk;
import com.zcomapproach.garden.persistence.entity.G02Bill;
import com.zcomapproach.garden.persistence.entity.G02BillBk;
import com.zcomapproach.garden.persistence.entity.G02BusinessContactor;
import com.zcomapproach.garden.persistence.entity.G02DeadlineExtension;
import com.zcomapproach.garden.persistence.entity.G02DocumentTag;
import com.zcomapproach.garden.persistence.entity.G02Log;
import com.zcomapproach.garden.persistence.entity.G02Memo;
import com.zcomapproach.garden.persistence.entity.G02OfflineEmail;
import com.zcomapproach.garden.persistence.entity.G02Payment;
import com.zcomapproach.garden.persistence.entity.G02TaxFilingCase;
import com.zcomapproach.garden.persistence.entity.G02TaxFilingStatus;
import com.zcomapproach.garden.persistence.entity.G02TaxcorpCase;
import com.zcomapproach.garden.persistence.entity.GardenLock;
import com.zcomapproach.garden.persistence.entity.GardenUpdateManager;
import com.zcomapproach.garden.persistence.peony.PeonyAccount;
import com.zcomapproach.garden.persistence.peony.PeonyArchivedFile;
import com.zcomapproach.garden.persistence.peony.PeonyArchivedFileList;
import com.zcomapproach.garden.persistence.peony.PeonyBillPayment;
import com.zcomapproach.garden.persistence.peony.PeonyBillPaymentList;
import com.zcomapproach.garden.persistence.peony.PeonyDeadlineExtension;
import com.zcomapproach.garden.persistence.peony.PeonyDocumentTag;
import com.zcomapproach.garden.persistence.peony.PeonyMemo;
import com.zcomapproach.garden.persistence.peony.PeonyPayment;
import com.zcomapproach.garden.persistence.peony.PeonyTaxFilingCase;
import com.zcomapproach.garden.persistence.peony.PeonyTaxFilingCaseList;
import com.zcomapproach.garden.persistence.peony.data.PeonyMemoList;
import com.zcomapproach.garden.persistence.peony.data.PeonyMemoFilter;
import com.zcomapproach.garden.persistence.updater.G02DataUpdaterFactory;
import com.zcomapproach.garden.rest.data.GardenRestStringList;
import com.zcomapproach.garden.rose.persistence.singleton.GardenLockAgent;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.persistence.entity.G02User;
import com.zcomapproach.garden.persistence.peony.data.TaxpayerSearchRemainingData;
import com.zcomapproach.garden.util.GardenEnvironment;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author zhijun98
 */
@Stateless
public class RoseBusinessEJB02 extends AbstractDataServiceEJB02 {
    
    public PeonyTaxFilingCaseList findPeonyTaxFilingCaseListByTypeAndDateRange(String taxFilingType, Date fromDate, Date toDate) {
        PeonyTaxFilingCaseList result = new PeonyTaxFilingCaseList();
        if (ZcaValidator.isNotNullEmpty(taxFilingType)){
            EntityManager em = getEntityManager();
            try {
                String sqlQuery = "SELECT g FROM G02TaxFilingCase g WHERE g.taxFilingType = :taxFilingType AND g.deadline BETWEEN :fromDate AND :toDate";
                HashMap<String, Object> params = new HashMap<>();
                params.put("taxFilingType", taxFilingType);
                params.put("fromDate", fromDate);
                params.put("toDate", toDate);
                List<G02TaxFilingCase> aG02TaxFilingCaseList = GardenJpaUtils.findEntityListByQuery(em, G02TaxFilingCase.class, sqlQuery, params);
                if (aG02TaxFilingCaseList != null){
                    for (G02TaxFilingCase aG02TaxFilingCase : aG02TaxFilingCaseList){
                        result.getPeonyTaxFilingCaseList().add(new PeonyTaxFilingCase(aG02TaxFilingCase));
                    }
                }
            } finally {
                em.close();
            }
        }
        return result;
    }

    public PeonyTaxFilingCaseList findPeonyTaxFilingCaseListByTaxcorpCaseUuidAndTypeAndDateRange(String taxcorpCaseUuid, String taxFilingType, Date fromDate, Date toDate) {
        PeonyTaxFilingCaseList result = new PeonyTaxFilingCaseList();
        if ((ZcaValidator.isNotNullEmpty(taxcorpCaseUuid)) && (ZcaValidator.isNotNullEmpty(taxFilingType))){
            EntityManager em = getEntityManager();
            try {
                String sqlQuery = "SELECT g FROM G02TaxFilingCase g WHERE g.taxFilingType = :taxFilingType AND g.entityUuid = :entityUuid AND g.deadline BETWEEN :fromDate AND :toDate";
                HashMap<String, Object> params = new HashMap<>();
                params.put("taxFilingType", taxFilingType);
                params.put("entityUuid", taxcorpCaseUuid);
                params.put("fromDate", fromDate);
                params.put("toDate", toDate);
                List<G02TaxFilingCase> aG02TaxFilingCaseList = GardenJpaUtils.findEntityListByQuery(em, G02TaxFilingCase.class, sqlQuery, params);
                if (aG02TaxFilingCaseList != null){
                    for (G02TaxFilingCase aG02TaxFilingCase : aG02TaxFilingCaseList){
                        result.getPeonyTaxFilingCaseList().add(new PeonyTaxFilingCase(aG02TaxFilingCase));
                    }
                }
            } finally {
                em.close();
            }
        }
        return result;
    }


    public PeonyTaxFilingCaseList findPeonyTaxFilingCaseListByTaxcorpCaseUuidAndType(String taxcorpCaseUuid, String taxFilingType) {
        PeonyTaxFilingCaseList result = new PeonyTaxFilingCaseList();
        if ((ZcaValidator.isNotNullEmpty(taxcorpCaseUuid)) && (ZcaValidator.isNotNullEmpty(taxFilingType))){
            EntityManager em = getEntityManager();
            try {
                String sqlQuery = "SELECT g FROM G02TaxFilingCase g WHERE g.taxFilingType = :taxFilingType AND g.entityUuid = :entityUuid ";
                HashMap<String, Object> params = new HashMap<>();
                params.put("taxFilingType", taxFilingType);
                params.put("entityUuid", taxcorpCaseUuid);
                List<G02TaxFilingCase> aG02TaxFilingCaseList = GardenJpaUtils.findEntityListByQuery(em, G02TaxFilingCase.class, sqlQuery, params);
                if (aG02TaxFilingCaseList != null){
                    for (G02TaxFilingCase aG02TaxFilingCase : aG02TaxFilingCaseList){
                        result.getPeonyTaxFilingCaseList().add(new PeonyTaxFilingCase(aG02TaxFilingCase));
                    }
                }
            } finally {
                em.close();
            }
        }
        return result;
    }

    public PeonyTaxFilingCase findPeonyTaxFilingCase(String taxFilingUuid) {
        PeonyTaxFilingCase result = null;
        if (ZcaValidator.isNotNullEmpty(taxFilingUuid)){
            EntityManager em = getEntityManager();
            try {
                G02TaxFilingCase aG02TaxFilingCase = GardenJpaUtils.findById(em, G02TaxFilingCase.class, taxFilingUuid);
                if (aG02TaxFilingCase != null){
                    result = new PeonyTaxFilingCase(aG02TaxFilingCase);
                }
            } finally {
                em.close();
            }
        }
        return result;
    }

    public PeonyBillPaymentList retrievePeonyBillPaymentListByEntityUuidList(List<String> entityUuids) {
        PeonyBillPaymentList result = new PeonyBillPaymentList();
        EntityManager em = getEntityManager();
        try {
            for (String entityUuid : entityUuids){
                result.getPeonyBillPaymentList().addAll(GardenJpaUtils.findPeonyBillPaymentListByEntityUuid(em, entityUuid));
            }
        } finally {
            em.close();
        }
        return result;
    }
    
    /**
     * 
     * @param entityUuid
     * @return - always not NULL
     */
    public PeonyBillPaymentList findPeonyBillPaymentList(String entityUuid) {
        PeonyBillPaymentList result = new PeonyBillPaymentList();
        if (ZcaValidator.isNotNullEmpty(entityUuid)){
            EntityManager em = getEntityManager();
            try {
                result.setPeonyBillPaymentList(GardenJpaUtils.findPeonyBillPaymentListByEntityUuid(em, entityUuid));
            } finally {
                em.close();
            }
        }
        return result;
    }
    
    /**
     * 
     * @param entityUuid
     * @return - always not NULL
     */
    public PeonyMemoList findPeonyMemoListByEntityUuid(String entityUuid) {
        PeonyMemoList result = new PeonyMemoList();
        if (ZcaValidator.isNotNullEmpty(entityUuid)){
            EntityManager em = getEntityManager();
            try {
                result.setPeonyMemoList(GardenJpaUtils.findPeonyMemoListByEntityUuid(em, entityUuid));
            } finally {
                em.close();
            }
        }
        return result;
    }
    
    /**
     * 
     * @param entityStatus
     * @return - always not NULL
     */
    public PeonyMemoList findPeonyMemoListByEntityStatus(String entityStatus) {
        PeonyMemoList result = new PeonyMemoList();
        if (ZcaValidator.isNotNullEmpty(entityStatus)){
            EntityManager em = getEntityManager();
            try {
                result.setPeonyMemoList(GardenJpaUtils.findPeonyMemoListByEntityStatus(em, entityStatus));
            } finally {
                em.close();
            }
        }
        return result;
    }

    public PeonyMemoList retrievePeonyPublicForumMessageList(PeonyMemoFilter peonyMemoFilter) {
        PeonyMemoList result = new PeonyMemoList();
        EntityManager em = getEntityManager();
        try {
            result.setPeonyMemoList(GardenJpaUtils.findPeonyMemoListByEntityStatus(em, GardenEntityType.PUBLIC_BOARD.value(), peonyMemoFilter));
        } finally {
            em.close();
        }
        return result;
    }

//////    /**
//////     * 
//////     * @param taxFilingUuidList
//////     * @return use of PeonyTaxFilingCaseList data structure to help return memo 
//////     * list for every tex-filing record which only contains its own UUID.  
//////     */
//////    public PeonyTaxFilingCaseList retrievePeonyMemoListByEntityUuidList(List<String> taxFilingUuidList) {
//////        PeonyTaxFilingCaseList result = new PeonyTaxFilingCaseList();
//////        EntityManager em = getEntityManager();
//////        try {
//////            result.setPeonyTaxFilingCaseList(GardenJpaUtils.findPeonyMemoListByEntityUuids(em, taxFilingUuidList));
//////        } finally {
//////            em.close();
//////        }
//////        return result;
//////    }
    
    /**
     * 
     * @param entityType
     * @return - always not NULL
     */
    public PeonyMemoList findPeonyMemoListByEntityType(String entityType) {
        PeonyMemoList result = new PeonyMemoList();
        if (ZcaValidator.isNotNullEmpty(entityType)){
            EntityManager em = getEntityManager();
            try {
                result.setPeonyMemoList(GardenJpaUtils.findPeonyMemoListByEntityType(em, entityType));
            } finally {
                em.close();
            }
        }
        return result;
    }
    
    /**
     * 
     * @param paymentUuid
     * @return - always not NULL
     */
    public G02Payment findPaymentEntity(String paymentUuid) {
        G02Payment result = null;
        if (ZcaValidator.isNotNullEmpty(paymentUuid)){
            EntityManager em = getEntityManager();
            try {
                result = GardenJpaUtils.findById(em, G02Payment.class, paymentUuid);
            } finally {
                em.close();
            }
        }
        return result;
    }
    
    /**
     * 
     * @param deadlineExtensionUuid
     * @return - always not NULL
     */
    public G02DeadlineExtension findDeadlineExtensionEntity(String deadlineExtensionUuid) {
        G02DeadlineExtension result = null;
        if (ZcaValidator.isNotNullEmpty(deadlineExtensionUuid)){
            EntityManager em = getEntityManager();
            try {
                result = GardenJpaUtils.findById(em, G02DeadlineExtension.class, deadlineExtensionUuid);
            } finally {
                em.close();
            }
        }
        return result;
    }
    
    /**
     * 
     * @param memoUuid
     * @return - always not NULL
     */
    public G02Memo findMemoEntity(String memoUuid) {
        G02Memo result = null;
        if (ZcaValidator.isNotNullEmpty(memoUuid)){
            EntityManager em = getEntityManager();
            try {
                result = GardenJpaUtils.findById(em, G02Memo.class, memoUuid);
            } finally {
                em.close();
            }
        }
        return result;
    }
    
    /**
     * 
     * @param billUuid
     * @return - always not NULL
     */
    public G02Bill findBillEntity(String billUuid) {
        G02Bill result = null;
        if (ZcaValidator.isNotNullEmpty(billUuid)){
            EntityManager em = getEntityManager();
            try {
                result = GardenJpaUtils.findById(em, G02Bill.class, billUuid);
            } finally {
                em.close();
            }
        }
        return result;
    }
    
    /**
     * 
     * @param billUuid
     * @return - always not NULL
     */
    public PeonyBillPayment findPeonyBillPayment(String billUuid) {
        PeonyBillPayment result = null;
        if (ZcaValidator.isNotNullEmpty(billUuid)){
            EntityManager em = getEntityManager();
            try {
                result = GardenJpaUtils.findPeonyBillPaymentByBillUuid(em, billUuid);
            } finally {
                em.close();
            }
        }
        return result;
    }
    
    public G02BusinessContactor findBusinessContactor(String businessContactorUuid) throws Exception {
        G02BusinessContactor result = null;
        if (ZcaValidator.isNotNullEmpty(businessContactorUuid)){
            EntityManager em = getEntityManager();
            try {
                result = GardenJpaUtils.findById(em, G02BusinessContactor.class, businessContactorUuid);
            } finally {
                em.close();
            }
        }
        return result;
    
    }

    /**
     * 
     * @param aPeonyDeadlineExtension- whose PeonyAccount is demanded to be filled up with full data for security check
     * @return - null if aPeonyDeadlineExtension cannot be saved successfully (e.g. the PeonyAccount instance cannot be autheticated)
     * @throws Exception 
     */
    public PeonyDeadlineExtension storePeonyDeadlineExtension(PeonyDeadlineExtension aPeonyDeadlineExtension) throws Exception {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            PeonyAccount aPeonyAccount = GardenJpaUtils.findPeonyAccountByAccountUuid(em, aPeonyDeadlineExtension.getOperator().getAccount().getAccountUuid());
            if ((aPeonyAccount != null) && (aPeonyAccount.getAccount().getEncryptedPassword().equalsIgnoreCase(aPeonyDeadlineExtension.getOperator().getAccount().getEncryptedPassword()))){
                GardenJpaUtils.storeEntity(em, G02DeadlineExtension.class, 
                                            aPeonyDeadlineExtension.getDeadlineExtension(), 
                                            aPeonyDeadlineExtension.getDeadlineExtension().getDeadlineExtensionUuid(), 
                                            G02DataUpdaterFactory.getSingleton().getG02DeadlineExtensionUpdater());
            }else{
                aPeonyDeadlineExtension = null;
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aPeonyDeadlineExtension;
    
    }
    
    public PeonyArchivedFile storePeonyArchivedFileHelper(EntityManager em, PeonyArchivedFile aPeonyArchivedFile) throws Exception {
        PeonyAccount aPeonyAccount = GardenJpaUtils.findPeonyAccountByAccountUuid(em, aPeonyArchivedFile.getOperator().getAccount().getAccountUuid());
        if ((aPeonyAccount != null) && (aPeonyAccount.getAccount().getEncryptedPassword().equalsIgnoreCase(aPeonyArchivedFile.getOperator().getAccount().getEncryptedPassword()))){
            GardenJpaUtils.storeEntity(em, G02ArchivedFile.class, 
                                        aPeonyArchivedFile.getArchivedFile(), 
                                        aPeonyArchivedFile.getArchivedFile().getFileUuid(), 
                                        G02DataUpdaterFactory.getSingleton().getG02ArchivedFileUpdater());
            List<PeonyDocumentTag> aPeonyDocumentTagList = aPeonyArchivedFile.getPeonyDocumentTagList();
            for (PeonyDocumentTag aPeonyDocumentTag : aPeonyDocumentTagList){
                GardenJpaUtils.storeEntity(em, G02DocumentTag.class, 
                                            aPeonyDocumentTag.getDocumentTag(), 
                                            aPeonyDocumentTag.getDocumentTag().getDocumentTagUuid(), 
                                            G02DataUpdaterFactory.getSingleton().getG02DocumentTagUpdater());
            }
        }else{
            aPeonyArchivedFile = null;
        }
        return aPeonyArchivedFile;
    }

    public PeonyArchivedFileList storePeonyArchivedFileList(PeonyArchivedFileList aPeonyArchivedFileList) throws Exception {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            List<PeonyArchivedFile> peonyArchivedFiles = aPeonyArchivedFileList.getPeonyArchivedFileList();
            for (PeonyArchivedFile aPeonyArchivedFile : peonyArchivedFiles){
                storePeonyArchivedFileHelper(em, aPeonyArchivedFile);
            }
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aPeonyArchivedFileList;
    }

    public PeonyDocumentTag storePeonyDocumentTag(PeonyDocumentTag aPeonyDocumentTag) throws Exception {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            aPeonyDocumentTag = storePeonyDocumentTagHelper(em, aPeonyDocumentTag);
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aPeonyDocumentTag;
    }
    
    public PeonyDocumentTag storePeonyDocumentTagHelper(EntityManager em, PeonyDocumentTag aPeonyDocumentTag) throws Exception {
        G02DocumentTag aG02DocumentTag = aPeonyDocumentTag.getDocumentTag();
        GardenJpaUtils.storeEntity(em, G02DocumentTag.class, aG02DocumentTag, aG02DocumentTag.getDocumentTagUuid(), 
                                    G02DataUpdaterFactory.getSingleton().getG02DocumentTagUpdater());
        return aPeonyDocumentTag;
    }

    public PeonyArchivedFile storePeonyArchivedFile(PeonyArchivedFile aPeonyArchivedFile) throws Exception {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            aPeonyArchivedFile = storePeonyArchivedFileHelper(em, aPeonyArchivedFile);
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aPeonyArchivedFile;
    }
    
    /**
     * 
     * @param aPeonyBillPayment
     * @return
     * @throws Exception 
     */
    public PeonyBillPayment storePeonyBillPayment(PeonyBillPayment aPeonyBillPayment) throws Exception {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            PeonyAccount aPeonyAccount = GardenJpaUtils.findPeonyAccountByAccountUuid(em, aPeonyBillPayment.getOperator().getAccount().getAccountUuid());
            if ((aPeonyAccount != null) && (aPeonyAccount.getAccount().getEncryptedPassword().equalsIgnoreCase(aPeonyBillPayment.getOperator().getAccount().getEncryptedPassword()))){
                GardenJpaUtils.storeEntity(em, G02Bill.class, 
                                            aPeonyBillPayment.getBill(), 
                                            aPeonyBillPayment.getBill().getBillUuid(), 
                                            G02DataUpdaterFactory.getSingleton().getG02BillUpdater());
                List<PeonyPayment> aPeonyPaymentList = aPeonyBillPayment.getPaymentList();
                for (PeonyPayment aPeonyPayment : aPeonyPaymentList){
                    GardenJpaUtils.storeEntity(em, G02Payment.class, 
                                                aPeonyPayment.getPayment(), 
                                                aPeonyPayment.getPayment().getPaymentUuid(), 
                                                G02DataUpdaterFactory.getSingleton().getG02PaymentUpdater());
                }
            }else{
                aPeonyBillPayment = null;
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aPeonyBillPayment;
    }
    
    /**
     * 
     * @param aPeonyPayment
     * @return
     * @throws Exception 
     */
    public PeonyPayment storePeonyPayment(PeonyPayment aPeonyPayment) throws Exception {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            PeonyAccount aPeonyAccount = GardenJpaUtils.findPeonyAccountByAccountUuid(em, aPeonyPayment.getOperator().getAccount().getAccountUuid());
            if ((aPeonyAccount != null) && (aPeonyAccount.getAccount().getEncryptedPassword().equalsIgnoreCase(aPeonyPayment.getOperator().getAccount().getEncryptedPassword()))){
                GardenJpaUtils.storeEntity(em, G02Payment.class, 
                                            aPeonyPayment.getPayment(), 
                                            aPeonyPayment.getPayment().getPaymentUuid(), 
                                            G02DataUpdaterFactory.getSingleton().getG02PaymentUpdater());
            }else{
                aPeonyPayment = null;
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aPeonyPayment;
    }
    /**
     * 
     * @param aPeonyMemo - whose PeonyAccount is demanded to be filled up with full data for security check
     * @return - null if aPeonyMemo cannot be saved successfully (e.g. the PeonyAccount instance cannot be autheticated)
     * @throws Exception 
     */
    public PeonyMemo storePeonyMemo(PeonyMemo aPeonyMemo) throws Exception {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            PeonyAccount aPeonyAccount = GardenJpaUtils.findPeonyAccountByAccountUuid(em, aPeonyMemo.getOperator().getAccount().getAccountUuid());
            if ((aPeonyAccount != null) && (aPeonyAccount.getAccount().getEncryptedPassword().equalsIgnoreCase(aPeonyMemo.getOperator().getAccount().getEncryptedPassword()))){
                G02Memo aG02Memo = aPeonyMemo.getMemo();
                if (GardenEntityType.OFFLINE_EMAIL_MESSAGE.name().equalsIgnoreCase(aG02Memo.getEntityType())){
                    //update the corresponding email in this case
                    G02OfflineEmail aG02OfflineEmail = GardenJpaUtils.findById(em, G02OfflineEmail.class, aG02Memo.getEntityUuid());
                    if (aG02OfflineEmail == null){
                        aG02Memo = null;
                    }else{
                        aG02OfflineEmail.setNoted(GardenBooleanValue.Yes.value());
                        GardenJpaUtils.storeEntity(em, G02OfflineEmail.class, aG02OfflineEmail, 
                                                aG02OfflineEmail.getOfflineEmailUuid(), 
                                                G02DataUpdaterFactory.getSingleton().getG02OfflineEmailUpdater());
                    }
                }
                if (aG02Memo == null){
                    aPeonyMemo = null;
                }else{
                    GardenJpaUtils.storeEntity(em, G02Memo.class, aG02Memo, aG02Memo.getMemoUuid(), 
                                                G02DataUpdaterFactory.getSingleton().getG02MemoUpdater());
                }
            }else{
                aPeonyMemo = null;
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aPeonyMemo;
    }

    public G02TaxFilingCase storeTaxFilingCase(G02TaxFilingCase aG02TaxFilingCase) throws Exception {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            GardenJpaUtils.storeEntity(em, G02TaxFilingCase.class, 
                                        aG02TaxFilingCase, 
                                        aG02TaxFilingCase.getTaxFilingUuid(), 
                                        G02DataUpdaterFactory.getSingleton().getG02TaxFilingCaseUpdater());
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aG02TaxFilingCase;
    }
    

    public G02TaxFilingStatus storeG02TaxFilingStatus(G02TaxFilingStatus aG02TaxFilingStatus) throws Exception {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            GardenJpaUtils.storeEntity(em, G02TaxFilingStatus.class, 
                                        aG02TaxFilingStatus, 
                                        aG02TaxFilingStatus.getG02TaxFilingStatusPK(), 
                                        G02DataUpdaterFactory.getSingleton().getG02TaxFilingStatusUpdater());
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aG02TaxFilingStatus;
    }
    
    /**
     * if aG02BusinessContactor's taxcorp does not exist in the database yet, this 
     * method will return NULL and give up this operation. 
     * 
     * @param aG02BusinessContactor
     * @return
     * @throws Exception 
     */
    public G02BusinessContactor storeBusinessContactor(G02BusinessContactor aG02BusinessContactor) throws Exception {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            G02TaxcorpCase aG02TaxcorpCase = GardenJpaUtils.findById(em, G02TaxcorpCase.class, aG02BusinessContactor.getEntityUuid());
            if (aG02TaxcorpCase == null){
                aG02BusinessContactor = null; //return NULL and give up this operation
            }else{
                GardenJpaUtils.storeEntity(em, G02BusinessContactor.class, 
                                            aG02BusinessContactor, 
                                            aG02BusinessContactor.getBusinessContactorUuid(), 
                                            G02DataUpdaterFactory.getSingleton().getG02BusinessContactorUpdater());
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aG02BusinessContactor;
    }

    public G02BusinessContactor deleteBusinessContactor(String businessContactorUuid) throws Exception {
        G02BusinessContactor aG02BusinessContactor = null;
        if (ZcaValidator.isNotNullEmpty(businessContactorUuid)){
            //store...
            EntityManager em = null;
            UserTransaction utx =  getUserTransaction();
            try {
                utx.begin();
                em = getEntityManager();

                aG02BusinessContactor = GardenJpaUtils.deleteEntity(em, G02BusinessContactor.class, businessContactorUuid);

                utx.commit();
            } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    utx.rollback();
                } catch (IllegalStateException | SecurityException | SystemException ex1) {
                    Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                    throw ex1;
                }
                throw ex;
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        }

        return aG02BusinessContactor;
    }

    public G02ArchivedFile deleteArchivedFile(String fileUuid) throws Exception {
        G02ArchivedFile aG02ArchivedFile = null;
        if (ZcaValidator.isNotNullEmpty(fileUuid)){
            //store...
            EntityManager em = null;
            UserTransaction utx =  getUserTransaction();
            try {
                utx.begin();
                em = getEntityManager();

                aG02ArchivedFile = GardenJpaUtils.findById(em, G02ArchivedFile.class, fileUuid);
                if (aG02ArchivedFile != null){
                    G02ArchivedFileBk aG02ArchivedFileBk = new G02ArchivedFileBk();
                    aG02ArchivedFileBk.setCreated(aG02ArchivedFile.getCreated());
                    aG02ArchivedFileBk.setEntityStatus(aG02ArchivedFile.getEntityStatus());
                    aG02ArchivedFileBk.setEntityType(aG02ArchivedFile.getEntityType());
                    aG02ArchivedFileBk.setEntityUuid(aG02ArchivedFile.getEntityUuid());
                    aG02ArchivedFileBk.setFileExtension(aG02ArchivedFile.getFileExtension());
                    aG02ArchivedFileBk.setFileFaceFolder(aG02ArchivedFile.getFileFaceFolder());
                    aG02ArchivedFileBk.setFileFaceName(aG02ArchivedFile.getFileFaceName());
                    aG02ArchivedFileBk.setFileMemo(aG02ArchivedFile.getFileMemo());
                    aG02ArchivedFileBk.setFilePath(aG02ArchivedFile.getFilePath());
                    aG02ArchivedFileBk.setFileTimestamp(aG02ArchivedFile.getFileTimestamp());
                    aG02ArchivedFileBk.setFileUuid(aG02ArchivedFile.getFileUuid());
                    aG02ArchivedFileBk.setOperatorAccountUuid(aG02ArchivedFile.getOperatorAccountUuid());
                    aG02ArchivedFileBk.setUpdated(aG02ArchivedFile.getUpdated());
                    
                    GardenJpaUtils.storeEntity(em, G02ArchivedFileBk.class, aG02ArchivedFileBk, aG02ArchivedFileBk.getFileUuid(), G02DataUpdaterFactory.getSingleton().getG02ArchivedFileBkUpdater());
                    GardenJpaUtils.deleteEntity(em, G02ArchivedFile.class, aG02ArchivedFile.getFileUuid());
                }
////                PeonyArchivedFile aPeonyArchivedFile = GardenJpaUtils.findPeonyArchivedFileByFileUuid(em, fileUuid);
////                if (aPeonyArchivedFile != null){
////                    aG02ArchivedFile = aPeonyArchivedFile.getArchivedFile();
////                    GardenJpaUtils.deleteEntity(em, G02ArchivedFile.class, aG02ArchivedFile.getFileUuid());
////                    List<PeonyDocumentTag> aPeonyDocumentTagList = aPeonyArchivedFile.getPeonyDocumentTagList();
////                    for (PeonyDocumentTag aPeonyDocumentTag : aPeonyDocumentTagList){
////                        GardenJpaUtils.deleteEntity(em, G02DocumentTag.class, aPeonyDocumentTag.getDocumentTag().getDocumentTagUuid());
////                    }
////                }
                utx.commit();
            } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    utx.rollback();
                } catch (IllegalStateException | SecurityException | SystemException ex1) {
                    Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                    throw ex1;
                }
                throw ex;
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        }

        return aG02ArchivedFile;
    }
    
    public PeonyDocumentTag deletePeonyDocumentTag(String documentTagUuid) throws Exception {
        G02DocumentTag aG02DocumentTag = null;
        if (ZcaValidator.isNotNullEmpty(documentTagUuid)){
            //store...
            EntityManager em = null;
            UserTransaction utx =  getUserTransaction();
            try {
                utx.begin();
                em = getEntityManager();

                aG02DocumentTag = GardenJpaUtils.deleteEntity(em, G02DocumentTag.class, documentTagUuid);

                utx.commit();
            } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    utx.rollback();
                } catch (IllegalStateException | SecurityException | SystemException ex1) {
                    Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                    throw ex1;
                }
                throw ex;
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        }
        if (aG02DocumentTag == null){
            return null;
        }
        return new PeonyDocumentTag(aG02DocumentTag);
    }

    public G02Memo deleteMemoEntity(String memoUuid) throws Exception {
        G02Memo aG02MemoEntity = null;
        if (ZcaValidator.isNotNullEmpty(memoUuid)){
            //store...
            EntityManager em = null;
            UserTransaction utx =  getUserTransaction();
            try {
                utx.begin();
                em = getEntityManager();

                aG02MemoEntity = GardenJpaUtils.deleteEntity(em, G02Memo.class, memoUuid);

                utx.commit();
            } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    utx.rollback();
                } catch (IllegalStateException | SecurityException | SystemException ex1) {
                    Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                    throw ex1;
                }
                throw ex;
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        }

        return aG02MemoEntity;
    }

    /**
     * This method will define values, e.g. UUID, timestamp, etc. for aGardenLock
     * @param aGardenLock
     * @return
     * @throws Exception 
     */
    public GardenLock tryToLockByGarden(GardenLock aGardenLock) throws Exception{
        GardenLock result;
        //store...
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            result = GardenLockAgent.getSingleton().tryToLock(em, aGardenLock);
            
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }
    public GardenLock unlockByGarden(GardenLock aGardenLock) throws Exception{
        //store...
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            aGardenLock = GardenLockAgent.getSingleton().unlock(em, aGardenLock);
            
            utx.commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return aGardenLock;
    }

    public GardenUpdateManager lockGardenUpdateManager(GardenUpdateManager aGardenUpdateManager) throws Exception{
        if ((aGardenUpdateManager == null) || (ZcaValidator.isNullEmpty(aGardenUpdateManager.getGardenUpdateUuid()))){
            return aGardenUpdateManager;
        }
        //store...
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            /**
             * Find the lock according to the flag
             */
            HashMap<String, Object> params = new HashMap<>();
            params.put("gardenUpdateFlag", aGardenUpdateManager.getGardenUpdateFlag());
            GardenUpdateManager theGardenUpdateManager = GardenJpaUtils.findEntityByNamedQuery(em, GardenUpdateManager.class, "GardenUpdateManager.findByGardenUpdateFlag", params);
            /**
             * Validate the situation for locking
             */
            if (theGardenUpdateManager != null){
                if (!theGardenUpdateManager.getClientUuid().equalsIgnoreCase(aGardenUpdateManager.getClientUuid())){
                    //other did lock it before, but check if it is valid
                    if (ZcaValidator.isNotNullEmpty(theGardenUpdateManager.getClientStatus())){
                        if (theGardenUpdateManager.getGardenUpdateDeadline() == null){
                            aGardenUpdateManager = null;    //it is valid lock
                        }else{
                            if (theGardenUpdateManager.getGardenUpdateDeadline().after(new Date())){
                                aGardenUpdateManager = null;    //it is valid lock
                            }
                        }
                    }
                }
                //respect the existing one to keep the flag as unique lock
                if (aGardenUpdateManager != null){
                    aGardenUpdateManager.setGardenUpdateUuid(theGardenUpdateManager.getGardenUpdateUuid()); 
                }
            }
            if (aGardenUpdateManager != null){
                //lock it by non-NULL client status
                if (ZcaValidator.isNullEmpty(aGardenUpdateManager.getClientStatus())){
                    aGardenUpdateManager.setClientStatus("locked");
                }
                aGardenUpdateManager.setClientTimestamp(new Date());
                aGardenUpdateManager.setCreated(aGardenUpdateManager.getClientTimestamp());
                GardenJpaUtils.storeEntity(em, GardenUpdateManager.class, aGardenUpdateManager, aGardenUpdateManager.getGardenUpdateUuid(), G02DataUpdaterFactory.getSingleton().getGardenUpdateManagerUpdater());
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return aGardenUpdateManager;
    }

    /**
     * 
     * @param aGardenUpdateManager
     * @return - if NULL, it failed to unlock by updating the client status being NULL
     * @throws Exception 
     */
    public GardenUpdateManager unlockGardenUpdateManager(GardenUpdateManager aGardenUpdateManager) throws Exception {
        if ((aGardenUpdateManager == null) || (ZcaValidator.isNullEmpty(aGardenUpdateManager.getClientUuid()))){
            return aGardenUpdateManager;
        }
        //store...
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            HashMap<String, Object> params = new HashMap<>();
            params.put("clientUuid", aGardenUpdateManager.getClientUuid());   //not equal
            GardenUpdateManager theGardenUpdateManager = GardenJpaUtils.findEntityByNamedQuery(em, GardenUpdateManager.class, "GardenUpdateManager.findByClientUuid", params);
            if (theGardenUpdateManager == null){
                aGardenUpdateManager = null;    //cannot find the lock
            }else{
                aGardenUpdateManager.setGardenUpdateUuid(theGardenUpdateManager.getGardenUpdateUuid()); //make sure it is the same lock
                aGardenUpdateManager.setClientStatus(null); //unlock!
                GardenJpaUtils.storeEntity(em, GardenUpdateManager.class, aGardenUpdateManager, aGardenUpdateManager.getGardenUpdateUuid(), G02DataUpdaterFactory.getSingleton().getGardenUpdateManagerUpdater());
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return aGardenUpdateManager;
    }

    public G02Payment deletePayment(String paymentUuid) throws Exception {
        G02Payment aG02Payment = null;
        if (ZcaValidator.isNotNullEmpty(paymentUuid)){
            //store...
            EntityManager em = null;
            UserTransaction utx =  getUserTransaction();
            try {
                utx.begin();
                em = getEntityManager();

                aG02Payment = GardenJpaUtils.deleteEntity(em, G02Payment.class, paymentUuid);

                utx.commit();
            } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    utx.rollback();
                } catch (IllegalStateException | SecurityException | SystemException ex1) {
                    Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                    throw ex1;
                }
                throw ex;
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        }

        return aG02Payment;
    }
    
    /**
     * if the bill has no any payment associated, such a bill will be permenantly 
     * deleted from the database. Otherwise, it will be moved into g02_bill_bk
     * @param billUuid
     * @return
     * @throws Exception 
     */
    public G02Bill deleteBill(String billUuid) throws Exception {
        G02Bill aG02Bill = null;
        if (ZcaValidator.isNotNullEmpty(billUuid)){
            //store...
            EntityManager em = null;
            UserTransaction utx =  getUserTransaction();
            try {
                utx.begin();
                em = getEntityManager();

                aG02Bill = GardenJpaUtils.findById(em, G02Bill.class, billUuid);
                if (aG02Bill != null){
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("billUuid", billUuid);
                    List<G02Payment> aG02PaymentList = GardenJpaUtils.findEntityListByNamedQuery(em, G02Payment.class, "G02Payment.findByBillUuid", params);
                    if ((aG02PaymentList != null) && (!aG02PaymentList.isEmpty())){
                        //moved into g02_bill_bk
                        G02BillBk aG02BillBk = new G02BillBk();
                        aG02BillBk.setBillContent(aG02Bill.getBillContent());
                        aG02BillBk.setBillDatetime(aG02Bill.getBillDatetime());
                        aG02BillBk.setBillDiscount(aG02Bill.getBillDiscount());
                        aG02BillBk.setBillDiscountType(aG02Bill.getBillDiscountType());
                        aG02BillBk.setBillStatus(aG02Bill.getBillStatus());
                        aG02BillBk.setBillTotal(aG02Bill.getBillTotal());
                        aG02BillBk.setBillUuid(aG02Bill.getBillUuid());
                        aG02BillBk.setCreated(aG02Bill.getCreated());
                        aG02BillBk.setEmployeeUuid(aG02Bill.getEmployeeUuid());
                        aG02BillBk.setEntityStatus(aG02Bill.getEntityStatus());
                        aG02BillBk.setEntityType(aG02Bill.getEntityType());
                        aG02BillBk.setEntityUuid(aG02Bill.getEntityUuid());
                        aG02BillBk.setUpdated(aG02Bill.getUpdated());
                        
                        GardenJpaUtils.storeEntity(em, G02BillBk.class, aG02BillBk, aG02BillBk.getBillUuid(), 
                                G02DataUpdaterFactory.getSingleton().getG02BillBkUpdater());
                    }
                    //delete the bill permenantly if (1) it has no payments are associated OR (2) is moved into g02_bill_bk
                    aG02Bill = GardenJpaUtils.deleteEntity(em, G02Bill.class, billUuid);
                }
                utx.commit();
            } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    utx.rollback();
                } catch (IllegalStateException | SecurityException | SystemException ex1) {
                    Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                    throw ex1;
                }
                throw ex;
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        }

        return aG02Bill;
    }

    public G02TaxFilingCase deleteTaxFilingCaseByTaxFilingUuid(String taxFilingUuid){
        G02TaxFilingCase result = null;
        if (ZcaValidator.isNullEmpty(taxFilingUuid)){
            return result;
        }
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            result = GardenJpaUtils.findById(em, G02TaxFilingCase.class, taxFilingUuid);
            if (result != null){
                GardenJpaUtils.deleteEntity(em, G02TaxFilingCase.class, result.getTaxFilingUuid());
                //delete logs...
                List<G02Log> logs = GardenJpaUtils.findLogListByLoggedEntityUuid(em, result.getTaxFilingUuid());
                if (logs != null){
                    for (G02Log log : logs){
                        GardenJpaUtils.deleteEntity(em, G02Log.class, log.getLogUuid());
                    }
                }
            }
            
            utx.commit();
            
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);   
            }
        } finally {
            em.close();
        }
        
        return result;
    }
    
    public PeonyTaxFilingCaseList deleteTaxFilingCases(String taxCaseUuid, String taxFilingType, String taxFilingPeriod, Date fromDate, Date toDate, boolean forceDeletion) {
        PeonyTaxFilingCaseList result = new PeonyTaxFilingCaseList();
        if ((fromDate == null) || (toDate == null)){
            return result;
        }
        
        if (!forceDeletion){
            //Cannot delete the old ones
            Date today = new Date();
            if (fromDate.before(today)){
                fromDate = today;
            }
        }
        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            String sqlQuery = "SELECT g FROM G02TaxFilingCase g WHERE g.entityUuid = :entityUuid "
                    + "AND g.taxFilingType = :taxFilingType AND g.taxFilingPeriod = :taxFilingPeriod "
                    + "AND g.deadline BETWEEN :fromDate AND :toDate ";
            HashMap<String, Object> params = new HashMap<>();
            params.put("taxFilingType", taxFilingType);
            //System.out.println(">>> taxFilingType = " + taxFilingType);
            params.put("taxFilingPeriod", taxFilingPeriod);
            //System.out.println(">>> taxFilingPeriod = " + taxFilingPeriod);
            params.put("entityUuid", taxCaseUuid);
            //System.out.println(">>> entityUuid = " + taxCaseUuid);
            params.put("fromDate", fromDate);
            //System.out.println(">>> fromDate = " + GardenCalendar.convertToMMddyyyy(fromDate, "-"));
            params.put("toDate", toDate);
            //System.out.println(">>> toDate = " + GardenCalendar.convertToMMddyyyy(toDate, "-"));
            
            List<G02TaxFilingCase> aG02TaxFilingCaseList = GardenJpaUtils.findEntityListByQuery(em, G02TaxFilingCase.class, sqlQuery, params);
            
            //System.out.println(">>> aG02TaxFilingCaseList.size() = " + aG02TaxFilingCaseList.size());
            
            if (aG02TaxFilingCaseList != null){
                List<G02Log> logs;
                PeonyTaxFilingCase aPeonyTaxFilingCase;
                for (G02TaxFilingCase aG02TaxFilingCase : aG02TaxFilingCaseList){
                    aPeonyTaxFilingCase = new PeonyTaxFilingCase();
                    aPeonyTaxFilingCase.setTaxFilingCase(aG02TaxFilingCase);
                    result.getPeonyTaxFilingCaseList().add(aPeonyTaxFilingCase);
                    GardenJpaUtils.deleteEntity(em, G02TaxFilingCase.class, aG02TaxFilingCase.getTaxFilingUuid());
                    //delete logs...
                    logs = GardenJpaUtils.findLogListByLoggedEntityUuid(em, aG02TaxFilingCase.getTaxFilingUuid());
                    if (logs != null){
                        for (G02Log log : logs){
                            GardenJpaUtils.deleteEntity(em, G02Log.class, log.getLogUuid());
                        }
                    }
                }//for
            }
            utx.commit();
            
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);   
            }
        } finally {
            em.close();
        }
        return result;
    }
    
    /**
     * This is used by two methods: retrieveArchivedFileNameListForBackup and archiveFileBackupCompleted
     */
    private final String backupCompletedStatus = "backup-completed";
    
    /**
     * 
     * file names but not from the database
     * @return 
     */
    public GardenRestStringList retrieveArchivedFileNameListForBackup() {
        GardenRestStringList result = new GardenRestStringList();
        EntityManager em = getEntityManager();
        try {
            String sqlQuery = "SELECT g FROM G02ArchivedFile g WHERE g.entityStatus IS NULL OR g.entityStatus <> :entityStatus";
            HashMap<String, Object> params = new HashMap<>();
            params.put("entityStatus", backupCompletedStatus);
            List<G02ArchivedFile> aG02ArchivedFileList = GardenJpaUtils.findEntityListByQuery(em, G02ArchivedFile.class, sqlQuery, params);
            if (aG02ArchivedFileList != null){
                for (G02ArchivedFile aG02ArchivedFile : aG02ArchivedFileList){
                    result.getStringDataList().add(aG02ArchivedFile.getFileUuid() + "." + aG02ArchivedFile.getFileExtension());
                }//for
            }
        } finally {
            em.close();
        }
        return result;
    }
    
    public GardenRestStringList retrievePeonyArchivedFileNameWithSizeList() {
        GardenRestStringList result = new GardenRestStringList();
        Path archiveRoot = GardenEnvironment.getServerSideArchivedFileRootPath(GardenFlower.PEONY);
        if (!Files.isDirectory(archiveRoot)){
            return result;
        }
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(archiveRoot)) {
            for (Path filePath : directoryStream) {
                if (Files.isRegularFile(filePath)){
                    result.getStringDataList().add(FilenameUtils.getName(filePath.toAbsolutePath().toString()) + GardenEnvironment.underscore_delimiter + filePath.toFile().length());
                }
            }//for-loop
        } catch (IOException ex) {}
        return result;
    }

    /**backupCompletedStatus
     * @param backupArchivedFileName 
     */
    public void archiveFileBackupCompleted(String backupArchivedFileName){
        if (ZcaValidator.isNullEmpty(backupArchivedFileName)){
            return;
        }
        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            G02ArchivedFile aG02ArchivedFile = GardenJpaUtils.findById(em, G02ArchivedFile.class, FilenameUtils.getBaseName(backupArchivedFileName));
            if (aG02ArchivedFile != null){
                aG02ArchivedFile.setEntityStatus(backupCompletedStatus);
                GardenJpaUtils.storeEntity(em, G02ArchivedFile.class, aG02ArchivedFile, aG02ArchivedFile.getFileUuid(), 
                                           G02DataUpdaterFactory.getSingleton().getG02ArchivedFileUpdater());
            }
            
            utx.commit();
            
        } catch (ZcaEntityValidationException | NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);   
            }
        } finally {
            em.close();
        }
////        Path archivePath = GardenEnvironment.getServerSideArchivedFileRootPath(GardenFlower.PEONY);
////        if (!Files.isDirectory(archivePath)){
////            return;
////        }
////        Path archiveBackupPath = GardenEnvironment.getServerSideDeprecatedArchivedFileRootPath(GardenFlower.PEONY);
////        if (!Files.isDirectory(archiveBackupPath)){
////            try {
////                ZcaNio.createFolder(archiveBackupPath);
////            } catch (IOException ex) {
////                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
////                return;
////            }
////        }
////        Path srcFilePath = archivePath.resolve(backupArchivedFileName);
////        if (Files.isRegularFile(srcFilePath)){
////            try {
////                Path dstFilePath = archiveBackupPath.resolve(backupArchivedFileName);
////                if (Files.isRegularFile(dstFilePath)){
////                    throw new IOException("A physical file @" + srcFilePath + " exists in the archive-backup folder. Cannot copy-paste-delete operation.");
////                }else{
////                    ZcaNio.copyFile(srcFilePath.toFile(), dstFilePath.toFile());
////                    ZcaNio.deleteFile(srcFilePath.toFile());
////                }
////            } catch (IOException ex) {
////                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
////                return;
////            }
////        }
    }
    
    /**
     * Only for Peony update: PeonyUpdateArchive09302019
     * @return 
     */
    public GardenRestStringList findG02ArchivedFilePathListForUpdate09302019() {
        GardenRestStringList result = new GardenRestStringList();
        EntityManager em = getEntityManager();
        try {
            String sqlQuery = "SELECT g FROM G02ArchivedFile g WHERE g.filePath IS NOT NULL";
            HashMap<String, Object> params = new HashMap<>();
            List<G02ArchivedFile> aG02ArchivedFileList = GardenJpaUtils.findEntityListByQuery(em, G02ArchivedFile.class, sqlQuery, params);
            for (G02ArchivedFile aG02ArchivedFile : aG02ArchivedFileList){
                if (ZcaValidator.isNotNullEmpty(aG02ArchivedFile.getFilePath())){
                    result.getStringDataList().add(aG02ArchivedFile.getFilePath() + "." + aG02ArchivedFile.getFileExtension());
                }
            }
        } finally {
            em.close();
        }
        return result;
    }

    /**
     * Only for Peony update: PeonyUpdateArchive09302019
     * @return 
     */
    public GardenRestStringList findG02ArchivedDocumentFileNameListForUpdate09302019() {
        GardenRestStringList result = new GardenRestStringList();
        EntityManager em = getEntityManager();
        try {
            List<G02ArchivedDocument> aG02ArchivedDocumentList = GardenJpaUtils.findAll(em, G02ArchivedDocument.class);
            for (G02ArchivedDocument aG02ArchivedDocument : aG02ArchivedDocumentList){
                result.getStringDataList().add(aG02ArchivedDocument.getFileName()); //e.g. 000f4f2bz7c6az41eczadc6z1110cbd69af9.pdf
            }
        } finally {
            em.close();
        }
        return result;
    }
}
