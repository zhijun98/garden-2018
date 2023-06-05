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

import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.commons.nio.ZcaNio;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

/**
 *
 * @author zhijun98
 */
class PeonyUpdateArchive09302019Data {

    /**
     * Don't access The followings 
     */
    private static RandomAccessFile sharedFlagFile;
    private static RandomAccessFile sharedLogFile;
    private static HashSet<String> filenamesInArchiveFolder;

    static HashSet<String> getFilenamesInArchiveFolder() throws IOException{
        if (filenamesInArchiveFolder == null){
            //all the file names of physical files stored in the archive folder 
            if (filenamesInArchiveFolder == null){
                filenamesInArchiveFolder = ZcaNio.getAllFileNamesFromFolder(Paths.get(PeonyProperties.getSingleton().getArchivedDocumentsFolder()));
            }
        }
        return filenamesInArchiveFolder;
    }

    static RandomAccessFile getSharedFlagFile() throws IOException {
        if (sharedFlagFile == null){
            Path flagFilePath = Paths.get(PeonyProperties.getSingleton().getArchivedDocumentsFolder()).resolve("PeonyUpdateArchive09302019.flag");
            try {
                sharedFlagFile = new RandomAccessFile(flagFilePath.toFile(), "rw");
            } catch (FileNotFoundException ex) {
                //create a brand new...
                ZcaNio.createNewRegularFile(flagFilePath.toFile());
                sharedFlagFile = new RandomAccessFile(flagFilePath.toFile(), "rw");
            }
        }
        return sharedFlagFile;
    }

    static RandomAccessFile getSharedLogFile() throws IOException {
        if (sharedLogFile == null){
            Path logFilePath = Paths.get(PeonyProperties.getSingleton().getArchivedDocumentsFolder()).resolve("PeonyUpdateArchive09302019.log");
            try {
                sharedLogFile = new RandomAccessFile(logFilePath.toFile(), "rw");
            } catch (FileNotFoundException ex) {
                //create a brand new...
                ZcaNio.createNewRegularFile(logFilePath.toFile());
                sharedLogFile = new RandomAccessFile(logFilePath.toFile(), "rw");
            }
        }
        return sharedLogFile;
    }

}
