/*
 * Copyright 2017 ZComApproach Inc.
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

import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.commons.persistent.exception.NonUniqueEntityException;
import com.zcomapproach.garden.persistence.GardenJpaUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.metamodel.EntityType;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import com.zcomapproach.commons.persistent.IZcaEntityUpdater;

/**
 * CRUD services for database except the special table GardenSystemLog
 * @author zhijun98
 */
@TransactionManagement(TransactionManagementType.BEAN)
public abstract class AbstractDataServiceEJB02 {
    
    @Resource
    private UserTransaction _utx;
    
    @PersistenceUnit(unitName="RosePU") 
    private EntityManagerFactory _emf;

    public AbstractDataServiceEJB02() {
    }
    
    protected UserTransaction getUserTransaction() {
        return _utx;
    }

    public EntityManager getEntityManager() {
        return _emf.createEntityManager();
    }
    
    public void evictClassListFromPersistency(List<Class> classNameList){
        EntityManager em = getEntityManager();
        try {
            evictClassListFromPersistencyHelper(em, classNameList);
        } finally {
            em.close();
        }
    }
    
    public void evictClassFromPersistency(Class className){
        EntityManager em = getEntityManager();
        try {
            evictClassFromPersistencyHelper(em, className);
        } finally {
            em.close();
        }
    }
    
    protected void evictClassListFromPersistencyHelper(EntityManager em, List<Class> classNameList){
        for (Class className : classNameList){
            evictClassFromPersistencyHelper(em, className);
        }
    }
    
    protected void evictClassFromPersistencyHelper(EntityManager em, Class className){
        em.getEntityManagerFactory().getCache().evict(className);
    }
    
    public void evictEntityListFromPersistency(HashMap<Class, Object> entities){
        EntityManager em = getEntityManager();
        try {
            evictEntityListFromPersistencyHelper(em, entities);
        } finally {
            em.close();
        }
    }
    
    public void evictEntityFromPersistency(Entry<Class, Object> entity){
        EntityManager em = getEntityManager();
        try {
            evictEntityFromPersistencyHelper(em, entity);
        } finally {
            em.close();
        }
    }
    
    protected void evictEntityListFromPersistencyHelper(EntityManager em, HashMap<Class, Object> entities){
        Set<Entry<Class, Object>> entitySet = entities.entrySet();
        for (Entry<Class, Object> entity : entitySet){
            evictEntityFromPersistencyHelper(em, entity);
        }
    }
    
    protected void evictEntityFromPersistencyHelper(EntityManager em, Entry<Class, Object> entity){
        em.getEntityManagerFactory().getCache().evict(entity.getKey(), entity.getValue());
    }
    
    protected ArrayList<EntityType> getEntityTypeList() {
        ArrayList<EntityType> aEntityTypeList = new ArrayList<>(); 
        Set<EntityType<?>> entityTypes = _emf.getMetamodel().getEntities();
        for (EntityType entityType : entityTypes){
            aEntityTypeList.add(entityType);
        }
        return aEntityTypeList;
    }

    protected TreeSet<String> getEntityClassNameSet() {
        TreeSet<String> aEntityClassNameSet = new TreeSet<>();
        Set<EntityType<?>> entityTypes = _emf.getMetamodel().getEntities();
        for (EntityType entityType : entityTypes){
            aEntityClassNameSet.add(entityType.getName());
        }
        return aEntityClassNameSet;
    }
    
    public <T> T findEntityByNamedQuery(Class<T> className, String namedQuery, HashMap<String, Object> params) throws NonUniqueEntityException {
        EntityManager em = getEntityManager();
        T entity = null;
        try {
            entity = GardenJpaUtils.findEntityByNamedQuery(em, className, namedQuery, params);
        } finally {
            em.close();
        }
        return entity;
    }
    
    public <T> List<T> findEntityListByNamedQuery(Class<T> className, String namedQuery, HashMap<String, Object> params) {
        EntityManager em = getEntityManager();
        List<T> result = null;
        try {
            result = GardenJpaUtils.findEntityListByNamedQuery(em, className, namedQuery, params);
        } finally {
            em.close();
        }
        return result;
    }
    
    public <T> List<T> findEntityListByQuery(Class<T> className, String sqlQuery, HashMap<String, Object> params) {
        EntityManager em = getEntityManager();
        List<T> result = null;
        try {
            result = GardenJpaUtils.findEntityListByQuery(em, className, sqlQuery, params);
        } finally {
            em.close();
        }
        return result;
    }

    /**
     * 
     * @param <T>
     * @param className
     * @param id
     * @return - NULL if the ID does not exist
     */
    public <T> T findEntityByUuid(Class<T> className, Object id) {
        EntityManager em = getEntityManager();
        T entity = null;
        try {
            entity = GardenJpaUtils.findById(em, className, id);
        } finally {
            em.close();
        }
        return entity;
    }

    /**
     * 
     * @param <T>
     * @param className
     * @return a list of the results or empty list
     */
    public <T> List<T> findAll(Class<T> className) {
        List<T> result = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            //GardenAccount
            result = GardenJpaUtils.findAll(em, className);
            if (result == null){
                result = new ArrayList<>();
            }
        } finally {
            em.close();
        }
        return result;
    }

    public <T> void storeEntityList(Class<T> entityClass, List<T> entityObjList, IZcaEntityUpdater<T> entityUpdater) throws Exception {
        if ((entityObjList == null) || entityObjList.isEmpty()){
            return;
        }
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();

            for (T entityObj : entityObjList){
                GardenJpaUtils.storeEntity(em, entityClass, entityObj, entityUpdater.getEntityKey(entityObj), entityUpdater);
            }

            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(AbstractDataServiceEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(AbstractDataServiceEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public <T> T storeEntityByUuid(Class<T> entityClass, T entityObj, Object entityKey, IZcaEntityUpdater<T> entityUpdater) throws Exception {
        T entity = null;
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();

            entity = GardenJpaUtils.storeEntity(em, entityClass, entityObj, entityKey, entityUpdater);

            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(AbstractDataServiceEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(AbstractDataServiceEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return entity;
    }

    public <T> T deleteEntityByUuid(Class<T> className, Object entityKey) throws Exception {
        T entity = null;
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();

            entity = GardenJpaUtils.deleteEntity(em, className, entityKey);

            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(AbstractDataServiceEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(AbstractDataServiceEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return entity;
    }
    
}
