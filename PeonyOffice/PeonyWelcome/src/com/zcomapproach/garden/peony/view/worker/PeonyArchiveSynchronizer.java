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

package com.zcomapproach.garden.peony.view.worker;

import com.zcomapproach.garden.peony.kernel.services.PeonyBusinessService;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.persistence.entity.GardenLock;
import com.zcomapproach.garden.persistence.peony.data.GardenLockName;
import com.zcomapproach.garden.persistence.peony.data.GardenLockStatus;
import com.zcomapproach.garden.rest.GardenRestParams;
import org.openide.util.Lookup;

/**
 * @author zhijun98
 */
public class PeonyArchiveSynchronizer extends AbstractPeonyArchiveWorker {
    
    private GardenLock lock = new GardenLock();
    /**
     * This is time-consuming and the thread may sleep periodically for performance
     */
    @Override
    public void run() {
        /**
         * (1) Create a lock
         */
        //lock.setLockUuid(GardenData.generateUUIDString()); //server-side define this value
        //lock.setLockTimestamp(lockTimestamp); //server-side define this value
        lock.setLockName(GardenLockName.ARCHIVE_SYN.name());
        lock.setLockStatus(GardenLockStatus.TRY_TO_LOCK.name());
        lock.setLockLifetime(1000*60*60*12.0); //12 hours
        lock.setOperatorUserUuid(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
        /**
         * (2) Try to lock...
         */
        lock = tryToLock(lock);
        /**
         * (3) Locked or not
         */
        if ((lock == null) || (!GardenLockStatus.LOCKED.name().equalsIgnoreCase(lock.getLockStatus()))){
            return;
        }
        /**
         * (4) Synchronize archive files between local-side and the server-side
         */
        try {
            synchronizeArchiveFiles(PeonyProperties.getSingleton().getArchivedDocumentsFolder(), PeonyProperties.getSingleton().getArchivedDocumentsBackupFolder());
        } catch (InterruptedException ex) {
            PeonyFaceUtils.publishMessageOntoOutputWindow("Failed to synchronize archive files between the local and the server.");
        }
        /**
         * (5) Unlock
         */
        unlock();
    }

    private GardenLock tryToLock(GardenLock lock) {
        try {
            return Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                    .storeEntity_XML(GardenLock.class, GardenRestParams.Business.tryToLockByGardenRestParams(), lock);
        } catch (Exception ex) {
            PeonyFaceUtils.publishMessageOntoOutputWindow("Failed to lock the connection to Garden database because of techincal exception. " + ex.getMessage());
            return null;
        }
    }

    private void unlock() {
        try {
            Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                    .storeEntity_XML(GardenLock.class, GardenRestParams.Business.unlockByGardenRestParams(), lock);
        } catch (Exception ex) {
            PeonyFaceUtils.publishMessageOntoOutputWindow("Failed to unlock the connection to Garden database because of techincal exception. " + ex.getMessage());
        }
    }

    @Override
    public void stopWorker() {
        unlock();
    }
}
