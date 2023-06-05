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

package com.zcomapproach.garden.peony.view.update;

import com.zcomapproach.garden.data.GardenFlower;
import com.zcomapproach.garden.peony.kernel.services.PeonyBusinessService;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.persistence.entity.GardenUpdateManager;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.garden.rest.data.GardenRestStringList;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.garden.util.GardenHttpClientUtils;
import com.zcomapproach.commons.nio.ZcaNio;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * This is how to refactor Peony's archive system: (09-28-2019)
 * 
 * (1) g02_archived_file will not use of the field "file_path" anymore. The 
 * archived file's location will be encoded in the Peony implementation. (after 
 * everything done, the table's file_path column can be dropped.)<p>
 * (2) transfer all the g02_archived_document's records, which recorded 2018 
 * tax season's uploaded files, into g02_archived_file (done in the RoseWeb 
 * project)<p>
 * (3) copy all the files(2019), which were recorded by file_path in the g02_archived_file, 
 * into the archive root folder. MEANWHILE, upload them onto RoseWeb server. (done 
 * in this task which is not very time-consuming because there are only 40-50 files)<p>
 * (4) download all the physical files of g02_archived_document from RoseWeb-archive 
 * into the local archive root folder without any subfolder. (done in this task 
 * which is very time-consuming because there are only 5000-more files)<p>
 * (5) refactor the implementation to avoid using of g02_archived_file's file_path. 
 * And also, whenever archive a new file, Peony not only archives it locally but 
 * also upload it to RoseWeb server. In addition, don't use file_path anymore and 
 * encode the file_path in the new implementation. <p>
 * (6) after run this task in the Lu-Yin's office, authorize this button's privilege 
 * only to GardenMaster and change the implementation so as to backup the archived 
 * files at the local development environment automatically based on the schedule.
 * <p>
 * In practice, when one Peony client is launched, it will do (1) try to lock the 
 * PeonyUpdateArchive09302019.log file in the public shared archive folder. (2) if 
 * lock failed, it will give up the update for the current session. (3) if lock 
 * successfully, it will start upading archive.
 * <p>
 * As update, all the processed file will be logged into PeonyUpdateArchive09302019.log 
 * so that other Peony clients are able to know which files have been processed as they 
 * are launched next time.
 * <p>
 * @author zhijun98
 */
public class PeonyUpdateArchive09302019 implements Runnable {

    /**
     * This is time-consuming and the thread may sleep periodically for performance
     */
    @Override
    public void run() {
        GardenUpdateManager aGardenUpdateManager = new GardenUpdateManager();
        aGardenUpdateManager.setGardenUpdateUuid(ZcaUtils.generateUUIDString());
        aGardenUpdateManager.setClientStatus("Update archive documents and files for 2018/2019 records");
        aGardenUpdateManager.setClientTimestamp(new Date());
        aGardenUpdateManager.setClientUuid(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
        aGardenUpdateManager.setCreated(aGardenUpdateManager.getClientTimestamp());
        aGardenUpdateManager.setFlowerName(GardenFlower.PEONY.name());
        aGardenUpdateManager.setGardenUpdateDeadline(ZcaCalendar.createDate(2019, Calendar.OCTOBER, 15, 23, 59, 59));
        aGardenUpdateManager.setGardenUpdateFlag("PeonyUpdateArchive09302019");
        /**
         * check if it need clients to help update or not
         */
        GardenUpdateManager lock = lockGardenUpdateManager(aGardenUpdateManager);
        if (lock == null){
            return;
        }
        if ((lock.getGardenUpdateDeadline() != null) 
                && (lock.getGardenUpdateDeadline().before(new Date())))
        {
            return;
        }
        try {
            /**
             * (3) copy all the files(2019), which were recorded by file_path in the g02_archived_file, 
             * into the archive root folder. MEANWHILE, upload them onto RoseWeb server. 
             */
            List<String> fileFullPathNameList = retrieve2019ArchivedFilePathNameListFromRoseWeb();
            if (fileFullPathNameList != null){
                //try to transfer all the 2019 files
                for (String fileFullPathName : fileFullPathNameList){
                    transfer2019ArchivedFile(fileFullPathName);
                }//for-loop
            }
            /**
             * (4) download all the physical files of g02_archived_document from RoseWeb-archive 
             * into the local archive root folder without any subfolder.
             */
            List<String> fileNameList = retrieve2018ArchivedDocumentFileNameListFromRoseWeb();
            if (fileNameList != null){
                //try to transfer all the 2018 files
                for (String fileName : fileNameList){
                    transfer2018ArchivedDocument(fileName);
                }//for-loop
            }
        } catch (InterruptedException ex) {
        }
        unlockGardenUpdateManager(aGardenUpdateManager);
    }

    /**
     * Retrieve paths of all the current files archived in the local office's shared garden folder from RoseWeb
     * @return - a list of full path with file-names (including extension)
     */
    private List<String> retrieve2019ArchivedFilePathNameListFromRoseWeb() {
        try {
            GardenRestStringList aGardenRestStringList = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                    .findEntity_XML(GardenRestStringList.class, GardenRestParams.Business.findG02ArchivedFilePathListForUpdate09302019RestParams());
            if (aGardenRestStringList == null){
                return new ArrayList<>();
            }
            return aGardenRestStringList.getStringDataList();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            return new ArrayList<>();
        }
    }

    /**
     * (1) Copy-paste the filePathName into the archive root folder. <p>
     * (2) Upload the physical file onto RoseWeb server
     * @param filePathName 
     */
    private void transfer2019ArchivedFile(String uploadingFileFullPathName) throws InterruptedException {
        String loggedFileName = FilenameUtils.getName(uploadingFileFullPathName);
        Path targetFileFullPathName = Paths.get(PeonyProperties.getSingleton().getArchivedDocumentsFolder()).resolve(loggedFileName);
        if (!ZcaNio.isValidFile(targetFileFullPathName.toFile())){   //not archived yet
            if (ZcaNio.isValidFile(uploadingFileFullPathName)){
                try {
                    GardenHttpClientUtils.uploadFileToRoseWeb(PeonyProperties.getSingleton().getRoseUploadPostUrl(),
                                                              PeonyProperties.getSingleton().getCurrentLoginEmployee().getAccount().getAccountUuid(),
                                                              uploadingFileFullPathName);    //it may raise IOException
                    ZcaNio.copyFile(uploadingFileFullPathName, 
                                       targetFileFullPathName.toFile().getAbsolutePath());
                    Thread.sleep(1000);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    private void transfer2018ArchivedDocument(String fileName) throws InterruptedException {
        Path targetFileFullPathName = Paths.get(PeonyProperties.getSingleton().getArchivedDocumentsFolder()).resolve(fileName);
        if (!ZcaNio.isValidFile(targetFileFullPathName.toFile())){
            try {
                //not archived yet
                GardenHttpClientUtils.downloadArchiveFileFromRoseWeb(PeonyProperties.getSingleton().getRoseDownloadGetUrl(),
                                        PeonyProperties.getSingleton().getCurrentLoginEmployee().getAccount().getAccountUuid(),
                                        fileName, targetFileFullPathName.toFile());
                Thread.sleep(1000);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private List<String> retrieve2018ArchivedDocumentFileNameListFromRoseWeb() {
        try {
            GardenRestStringList aGardenRestStringList = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                    .findEntity_XML(GardenRestStringList.class, GardenRestParams.Business.findG02ArchivedDocumentFileNameListForUpdate09302019RestParams());
            if (aGardenRestStringList == null){
                return new ArrayList<>();
            }
            return aGardenRestStringList.getStringDataList();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            return new ArrayList<>();
        }
    }
    
    private GardenUpdateManager lockGardenUpdateManager(GardenUpdateManager aGardenUpdateManager) {
        try {
            return Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                    .storeEntity_XML(GardenUpdateManager.class, GardenRestParams.Business.lockGardenUpdateManagerRestParams(), aGardenUpdateManager);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    private void unlockGardenUpdateManager(GardenUpdateManager aGardenUpdateManager) {
        try {
            Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                    .storeEntity_XML(GardenUpdateManager.class, GardenRestParams.Business.unlockGardenUpdateManagerRestParams(), aGardenUpdateManager);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
