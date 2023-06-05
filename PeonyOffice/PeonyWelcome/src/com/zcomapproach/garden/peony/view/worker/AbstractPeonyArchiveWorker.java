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

import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.garden.peony.kernel.services.PeonyBusinessService;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.garden.rest.data.GardenRestStringList;
import com.zcomapproach.garden.util.GardenEnvironment;
import com.zcomapproach.garden.util.GardenHttpClientUtils;
import com.zcomapproach.commons.nio.ZcaNio;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import org.apache.commons.io.FilenameUtils;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public abstract class AbstractPeonyArchiveWorker implements Runnable {
    
    /**
     * Stop this woker
     */
    public abstract void stopWorker();
    
    /**
     * Synchronize the files in localArchiveRootFolder/localArchiveBackupRootFolder with ones in the server
     * 
     * @param localArchiveRootFolder
     * @param localArchiveBackupRootFolder
     * @throws InterruptedException 
     */
    public void synchronizeArchiveFiles(String localArchiveRootFolder, String localArchiveBackupRootFolder) throws InterruptedException{
        /**
         * (1) Retrive server-side archive file list
         * (2) If local file is not in the server-side list, upload it to the server.
         * (3) if some server-side  archive file does not exist locally, download it to the server.
         */
        List<String> serverSideArchivedFileWithSizeList = retrievePeonyArchivedFileNameWithSizeList();
        if (serverSideArchivedFileWithSizeList != null){
            TreeSet<String> synchronizedFileNames = new TreeSet<>();
            String synchronizedFileName;
            for (String serverSideFileNameWithSize : serverSideArchivedFileWithSizeList){
                synchronizedFileName = synchronizeWithLocalFile(localArchiveRootFolder, localArchiveBackupRootFolder, serverSideFileNameWithSize);
                if (synchronizedFileName != null){
                    synchronizedFileNames.add(synchronizedFileName);
                }
            }//for-loop

            if (!synchronizedFileNames.isEmpty()){
                List<String> localSideArchivedFullFilePathAndNameList = retrieveLocalSideArchivedFullFilePathAndNameList(localArchiveRootFolder);
                if (localSideArchivedFullFilePathAndNameList != null){
                    for (String localSideArchivedFullFilePathAndName : localSideArchivedFullFilePathAndNameList){
                        if (!synchronizedFileNames.contains(FilenameUtils.getName(localSideArchivedFullFilePathAndName))){
                            synchronizedFileName = uploadOntoServer(localSideArchivedFullFilePathAndName);
                            if (synchronizedFileName != null){
                                synchronizedFileNames.add(synchronizedFileName);
                            }
                            Thread.sleep(500);
                        }
                    }//for-loop
                }
            }
        }
    }

    private List<String> retrievePeonyArchivedFileNameWithSizeList() {
        try {
            GardenRestStringList aGardenRestStringList = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                    .findEntity_XML(GardenRestStringList.class, GardenRestParams.Business.retrievePeonyArchivedFileNameWithSizeListRestParams());
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
     * (1) if the file does not exist locally, return the "filename" without "_bytesize";
     * (2) if it exists locally, compare the files' byte size to see if they are equal to each other;
     * (2a) if they are equal to each other, put the "filename" without "_bytesize" into synchronizedFileNames, and finally return null;
     * (2b) if they are NOT equal to each other, do 3a or 3b. 
     * (3a) if the local-size is larger, upload the local file to overwrite the one on the server , and then put the "filename" without "_bytesize" into synchronizedFileNames, and finally return null;
     * (3b) if the server-size is larger, return the "filename" without "_bytesize";
     * @param localArchiveRootFolder - the root folder which contains the file in serverSideFileNameWithSize
     * @param localArchiveBackupRootFolder
     * @param serverSideFileNameWithSize - "filename_bytesize"
     * @return - serverSideFileName which is synchronized
     */
    private String synchronizeWithLocalFile(String localArchiveRootFolder, String localArchiveBackupRootFolder, String serverSideFileNameWithSize) {
        String serverSideFileName;
        try{
            String[] tokens =  serverSideFileNameWithSize.split(GardenEnvironment.underscore_delimiter);
            String serverSideFileSizeText = tokens[(tokens.length-1)];
            long serverSideFileSize = Long.parseLong(serverSideFileSizeText);
            serverSideFileName = serverSideFileNameWithSize.substring(0, serverSideFileNameWithSize.length() - (serverSideFileSizeText.length() + 1));

            //get the corresponding local file's path
            Path targetLocalFileFullPathName = Paths.get(localArchiveRootFolder).resolve(serverSideFileName);
            File targetLocalFile = targetLocalFileFullPathName.toFile();
            if (ZcaNio.isValidFile(targetLocalFile)){
                if (targetLocalFile.length() != serverSideFileSize){
                    if (targetLocalFile.length() > serverSideFileSize){
                        //the local file is better than the server-side one because its content may have more data.
                        GardenHttpClientUtils.uploadFileToRoseWeb(PeonyProperties.getSingleton().getRoseUploadPostUrl(),
                                                                  PeonyProperties.getSingleton().getCurrentLoginEmployee().getAccount().getAccountUuid(),
                                                                  targetLocalFile.getAbsolutePath());    //it may raise IOException
                    }else{
                        //the server-side file is better than the local-side one because the server-side file's content may have more data.
                        downloadFromServer(localArchiveRootFolder, localArchiveBackupRootFolder, serverSideFileName);
                    }
                    Thread.sleep(500);
                }
            }else{
                //the server-side file was not download before.
                downloadFromServer(localArchiveRootFolder, localArchiveBackupRootFolder, serverSideFileName);
                Thread.sleep(500);
            }
        }catch(Exception ex){
            PeonyFaceUtils.publishMessageOntoOutputWindow("Exception - Cannot synchronize the server-side file to the local: " + serverSideFileNameWithSize);
            serverSideFileName = null;
        }
        return serverSideFileName;
    }
    
    /**
     * if locally a file with the same name exists, it should be overwritten.
     * @param serverSideFileName 
     */
    private void downloadFromServer(String localArchiveRootFolder, String localArchiveBackupRootFolder, String serverSideFileName) throws IOException {
        //back up the existing local one if it is there
        Path targetLocalFileFullPathName = Paths.get(localArchiveRootFolder).resolve(serverSideFileName);
        File targetLocalFile = targetLocalFileFullPathName.toFile();
        if (ZcaNio.isValidFile(targetLocalFile)){
            //backup the existing file into backup folder with a backup file name, and then delete the existing one
            Path targetLocalFileFullPathNameBk = Paths.get(localArchiveBackupRootFolder).resolve(serverSideFileName);
            ZcaNio.copyFile(targetLocalFile.getAbsolutePath(), targetLocalFileFullPathNameBk + GardenEnvironment.underscore_delimiter + ZcaUtils.generateUUIDString());
            ZcaNio.deleteFile(targetLocalFile);
        }
        //download the copy from the server
        GardenHttpClientUtils.downloadArchiveFileFromRoseWeb(PeonyProperties.getSingleton().getRoseDownloadGetUrl(),
                                PeonyProperties.getSingleton().getCurrentLoginEmployee().getAccount().getAccountUuid(),
                                serverSideFileName, targetLocalFileFullPathName.toFile());
    }

    private List<String> retrieveLocalSideArchivedFullFilePathAndNameList(String localArchiveRootFolder) {
        List<String> result = new ArrayList<>();
        Path localArchiveRoot = Paths.get(localArchiveRootFolder);
        if (!Files.isDirectory(localArchiveRoot)){
            return result;
        }
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(localArchiveRoot)) {
            for (Path filePath : directoryStream) {
                if (Files.isRegularFile(filePath)){
                    result.add(filePath.toAbsolutePath().toString());
                }
            }//for-loop
        } catch (IOException ex) {}
        return result;
    }

    /**
     * 
     * @param localSideArchivedFullFilePathAndName
     * @return - the file name which is uploaded onto the server 
     */
    private String uploadOntoServer(String localSideArchivedFullFilePathAndName) {
        String synchronizedFileName;
        try {
            GardenHttpClientUtils.uploadFileToRoseWeb(PeonyProperties.getSingleton().getRoseUploadPostUrl(),
                    PeonyProperties.getSingleton().getCurrentLoginEmployee().getAccount().getAccountUuid(),
                    localSideArchivedFullFilePathAndName);    //it may raise IOException
            synchronizedFileName = FilenameUtils.getName(localSideArchivedFullFilePathAndName);
        } catch (IOException ex) {
            //Exceptions.printStackTrace(ex);
            PeonyFaceUtils.publishMessageOntoOutputWindow("IOException - Cannot uploaded to the server: " + localSideArchivedFullFilePathAndName);
            synchronizedFileName = null;
        }
        return synchronizedFileName;
    }
}
