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

import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.commons.nio.ZcaNio;
import com.zcomapproach.commons.ZcaUtils;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is used only 
 * @author zhijun98
 */
public class PeonyLocalBackup extends AbstractPeonyArchiveWorker{
    /**
     * This is also used as the local Rose server's archive root which is equivalent to GardenEnvironment
     */
    private final Path targetLocalServerSideArchiveRootPath = Paths.get("C:\\garden\\peony\\archive");
    private final Path targetLocalServerSideArchiveBackupRootPath = targetLocalServerSideArchiveRootPath.resolve("backup");
    
    private final AtomicBoolean status = new AtomicBoolean(true);
    
    @Override
    public void run() {
        //only TechnicalController is authorized
        if (!PeonyEmployee.isTechnicalController(PeonyProperties.getSingleton().getCurrentLoginUserUuid())){
            return;
        }
        //only support PC machine
        if (!ZcaUtils.isWindowsPlatform()){
            PeonyFaceUtils.publishMessageOntoOutputWindow("PeonyLocalBackup demands the local machine has Windows platform.");
            return;
        }
        PeonyFaceUtils.publishMessageOntoOutputWindow("PeonyLocalBackup is launched...");
        while(status.get()){
            try {
                if (!ZcaNio.isValidFolder(targetLocalServerSideArchiveRootPath.toFile())){
                    ZcaNio.createFolder(targetLocalServerSideArchiveRootPath);
                }
                if (!ZcaNio.isValidFolder(targetLocalServerSideArchiveBackupRootPath.toFile())){
                    ZcaNio.createFolder(targetLocalServerSideArchiveBackupRootPath);
                }
                synchronizeArchiveFiles(targetLocalServerSideArchiveRootPath.toString(), targetLocalServerSideArchiveBackupRootPath.toString());
                PeonyFaceUtils.publishMessageOntoOutputWindow("PeonyLocalBackup completed local archive file's backup. PeonyLocalBackup will resume its backup after one hour.");
                Thread.sleep(1000*60*60);   //sleep one hour
            } catch (Exception ex) {    //if any exception is raised, the loop will be broken
                PeonyFaceUtils.publishMessageOntoOutputWindow("PeonyLocalBackup stopped its operation for local archive file's backup due to exception(s). " + ex.getMessage());
                break;
            }
        }
        PeonyFaceUtils.publishMessageOntoOutputWindow("PeonyLocalBackup is stopped.");
    }

    @Override
    public void stopWorker() {
        status.set(false);
    }
}
