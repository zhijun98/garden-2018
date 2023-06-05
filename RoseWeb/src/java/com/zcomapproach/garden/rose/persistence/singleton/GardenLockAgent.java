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

package com.zcomapproach.garden.rose.persistence.singleton;

import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.commons.persistent.exception.NonUniqueEntityException;
import com.zcomapproach.garden.persistence.GardenJpaUtils;
import com.zcomapproach.garden.persistence.entity.G02Employee;
import com.zcomapproach.garden.persistence.entity.GardenLock;
import com.zcomapproach.garden.persistence.peony.data.GardenLockStatus;
import com.zcomapproach.garden.persistence.updater.G02DataUpdaterFactory;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import java.util.Date;
import java.util.HashMap;
import javax.persistence.EntityManager;

/**
 * Thread-safe: this class guaranttees that there is only one connection to Garden database
 * @author zhijun98
 */
public class GardenLockAgent {

    private static volatile GardenLockAgent self = null;

    public static GardenLockAgent getSingleton() {
        GardenLockAgent selfLocal = GardenLockAgent.self;
        if (selfLocal == null){
            synchronized (GardenLockAgent.class) {
                selfLocal = GardenLockAgent.self;
                if (selfLocal == null){
                    GardenLockAgent.self = selfLocal = new GardenLockAgent();
                }
            }
        }
        return selfLocal;
    }

    public synchronized GardenLock tryToLock(EntityManager em, GardenLock aGardenLock) throws NonUniqueEntityException, ZcaEntityValidationException, ZcaEntityValidationException {
        if ((aGardenLock == null) 
                || (ZcaValidator.isNullEmpty(aGardenLock.getLockName())) 
                || (ZcaValidator.isNullEmpty(aGardenLock.getOperatorUserUuid())) 
                || (aGardenLock.getLockLifetime() == null)) {
            return null;
        }
        
        /**
         * Validate the employee
         */
        G02Employee employee = GardenJpaUtils.findById(em, G02Employee.class, aGardenLock.getOperatorUserUuid());
        if (employee == null){
            return null;
        }
        
        /**
         * Find the lock according to the flag
         */
        HashMap<String, Object> params = new HashMap<>();
        params.put("lockName", aGardenLock.getLockName());
        GardenLock existingGardenLock = GardenJpaUtils.findEntityByNamedQuery(em, GardenLock.class, "GardenLock.findByLockName", params);
        //make sure UUID there
        if (ZcaValidator.isNullEmpty(aGardenLock.getLockUuid())){
            aGardenLock.setLockUuid(GardenData.generateUUIDString());
        }
        /**
         * Validate the situation for locking
         */
        if (existingGardenLock != null){
            if (aGardenLock.getOperatorUserUuid().equalsIgnoreCase(existingGardenLock.getOperatorUserUuid())){
                aGardenLock.setLockUuid(existingGardenLock.getLockUuid());   //use the existing UUID
            }else{
                if (isLockExpired(existingGardenLock)){
                    //No need to keep the expired record: existingGardenLock 
                    GardenJpaUtils.deleteEntity(em, GardenLock.class, existingGardenLock.getLockUuid());
                }else{
                    //cannot lock by aGardenLock because if the existingGardenLock
                    aGardenLock = null;
                }
            }
        }
        //Lock it if aGardenLock is good in valiation
        if (aGardenLock != null){
            aGardenLock.setLockTimestamp(new Date());
            aGardenLock.setLockStatus(GardenLockStatus.LOCKED.name());
            GardenJpaUtils.storeEntity(em, GardenLock.class, aGardenLock, aGardenLock.getLockUuid(), G02DataUpdaterFactory.getSingleton().getGardenLockUpdater());
        }
        return aGardenLock;
    }

    private synchronized boolean isLockExpired(GardenLock existingGardenLock) {
        return ((new Date()).getTime() - existingGardenLock.getLockTimestamp().getTime()) > existingGardenLock.getLockLifetime();
    }

    public synchronized GardenLock unlock(EntityManager em, GardenLock aGardenLock) {
        GardenJpaUtils.deleteEntity(em, GardenLock.class, aGardenLock.getLockUuid());
        aGardenLock.setLockTimestamp(new Date());
        aGardenLock.setLockStatus(GardenLockStatus.UNLOCKED.name());
        return aGardenLock;
    }
}
