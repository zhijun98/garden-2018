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

package com.zcomapproach.garden.lotus.persistence;

import com.zcomapproach.commons.persistent.exception.NonUniqueEntityException;
import com.zcomapproach.garden.guard.TechnicalController;
import com.zcomapproach.garden.persistence.GardenJpaUtils;
import com.zcomapproach.garden.persistence.entity.G02Account;
import com.zcomapproach.garden.persistence.entity.G02OfflineEmail;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmail;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 *
 * @author zhijun98
 */
public class LotusGardenStorage {

    private static final Logger logger = Logger.getLogger(LotusGardenStorage.class.getName());
    
    private static volatile LotusGardenStorage self = null;
    public static LotusGardenStorage getSingleton(){
        LotusGardenStorage selfLocal = LotusGardenStorage.self;
        if (selfLocal == null){
            synchronized (LotusGardenStorage.class) {
                selfLocal = LotusGardenStorage.self;
                if (selfLocal == null){
                    LotusGardenStorage.self = selfLocal = new LotusGardenStorage();
                }
            }
        }
        return selfLocal;
    }
    
    private final EntityManagerFactory emf;

    private LotusGardenStorage() {
        emf = Persistence.createEntityManagerFactory("LotusGardenPU");
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }


    public <T> void evictCache(Class<T> aClass) {
        if (aClass != null){
            emf.getCache().evict(aClass);
        }
    }

    public <T> T findById(Class<T> entityClass, Object id) {
        T result = null;
        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.findById(em, entityClass, id);
        } finally {
            em.close();
        }
        return result;
    }

    public <T> List<T> findAll(Class<T> entityClass) {
        List<T> result = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.findAll(em, entityClass);
        } finally {
            em.close();
        }
        return result;
    }

    public <T> List<T> findAll(Class<T> entityClass, int maxResults, int firstResult) {
        List<T> result = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.findAll(em, entityClass, false, maxResults, firstResult);
        } finally {
            em.close();
        }
        return result;
    }

    public <T> List<T> findAll(Class<T> entityClass, boolean all, int maxResults, int firstResult) {
        List<T> result = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.findAll(em, entityClass, all, maxResults, firstResult);
        } finally {
            em.close();
        }
        return result;
    }
    
    public void flush() {
        EntityManager em = getEntityManager();
        EntityTransaction utx = em.getTransaction();
        try {
            utx.begin();
            
            em.flush();
            
            utx.commit();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (Exception re) {
                logger.log(Level.SEVERE, null, ex);
            }
        } finally {
            em.close();
        }
    }
    
    public void refresh(Object obj) {
        EntityManager em = getEntityManager();
        EntityTransaction utx = em.getTransaction();
        try {
            utx.begin();
            
            em.refresh(obj);
            
            utx.commit();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (Exception re) {
                logger.log(Level.SEVERE, null, ex);
            }
        } finally {
            em.close();
        }
    }

    public Path getPeonyClientSideGmailRootPath(String loginName, String password) {
        Path result = null;
        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.findLocalOfficeEmailSerializationRootPath(em, loginName, password);
        } catch (Exception ex) {
            result = null; 
        } finally {
            em.close();
        }
        return result;
    }

    /**
     * Retrieve all the records of current employees who are currently employed by the business  
     * @return 
     */
    public List<G02Account> findCurrentEmployeeAccountList() {
        List<G02Account> result = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            
            result.addAll(GardenJpaUtils.findCurrentEmployeeAccountList(em));
        } finally {
            em.close();
        }
        return result;
    }

    public void publishPeonyGmailListRetrieved(List<PeonyOfflineEmail> aPeonyOfflineEmailList) {
        if ((aPeonyOfflineEmailList == null) || (aPeonyOfflineEmailList.isEmpty())){
            return;
        }
        EntityManager em = getEntityManager();
        EntityTransaction utx = em.getTransaction();
        try {
            utx.begin();
            G02OfflineEmail existingG02OfflineEmail;
            for (PeonyOfflineEmail aPeonyOfflineEmail : aPeonyOfflineEmailList){
                existingG02OfflineEmail = findPeonyOfflineEmailPublished(em, aPeonyOfflineEmail.getOfflineEmail());
                if (existingG02OfflineEmail == null){
                    GardenJpaUtils.storePeonyOfflineEmail(em, aPeonyOfflineEmail);
                }
            }
            utx.commit();
            logger.log(Level.INFO, "Published a PeonyOfflineEmail ... #{0}.", aPeonyOfflineEmailList.get(0).getOfflineEmail().getMsgId());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "An error occurred attempting to store PeonyOfflineEmail for employees.", ex);
            try {
                utx.rollback();
            } catch (Exception re) {
                logger.log(Level.SEVERE, "An error occurred attempting to roll back the transaction.", re);
            }
        } finally {
            em.close();
        }
    }

    /**
     * Lotus will be restarted due to various reasons. Every time, Lotus will retrieve gmails again. Thus, 
     * there will be lots of messages which were loaded before. In this case, it will not be published again 
     * so that their historical records will not be erased.
     * @param em
     * @param aG02OfflineEmail
     * @return 
     */
    private G02OfflineEmail findPeonyOfflineEmailPublished(EntityManager em, G02OfflineEmail aG02OfflineEmail) {
        if (aG02OfflineEmail == null){
            return null;
        }
        String sqlQuery = "SELECT g FROM G02OfflineEmail g WHERE g.ownerUserUuid = :ownerUserUuid "
                + "AND g.mailboxAddress = :mailboxAddress AND g.msgId = :msgId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("ownerUserUuid", aG02OfflineEmail.getOwnerUserUuid());
        params.put("mailboxAddress", aG02OfflineEmail.getMailboxAddress());
        params.put("msgId", aG02OfflineEmail.getMsgId());
        try {
            G02OfflineEmail theG02OfflineEmail = GardenJpaUtils.findEntityByQuery(em,
                    G02OfflineEmail.class, sqlQuery, params);
            return theG02OfflineEmail;
        } catch (NonUniqueEntityException ex) {
            Logger.getLogger(LotusGardenStorage.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
    }

    public ConcurrentSkipListSet<Long> retrieveHistoricalMsgProcessedIds() {
        ConcurrentSkipListSet<Long> result = null;
        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.retrieveHistoricalMsgIdsByOwnerUserUuid(em, TechnicalController.Controller_UUID.value());
        } finally {
            em.close();
        }
        return result;
    }
}
