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

package com.zcomapproach.garden.peony.security;

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.persistence.entity.G02Log;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zhijun98
 */
public class PeonyLogger {

    /**
     * 
     * @param log
     * @param logFilesRootPath - the root path where every log in logs stay. this 
     * physical path should be ready-for-use OR creatable. 
     * In addition, this path
     * @return 
     */
    public static boolean serialize(G02Log log, Path logFilesRootPath ){
        if ((log == null) || (ZcaValidator.isNullEmpty(log.getLogUuid())) || (logFilesRootPath == null)){
            return false;
        }
        try{
            if (Files.isRegularFile(logFilesRootPath)){
                Files.delete(logFilesRootPath); //this is for update-case: some serialieed file demands update itself 
            }
            try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(logFilesRootPath.resolve(log.getLogUuid()).toFile()))){
                out.writeObject(log);
            }
            return true;
        }catch(IOException ex){
            Logger.getLogger(PeonyLogger.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return false;
        }
    }
    
    /**
     * 
     * @param logs
     * @param logFilesRootPath - the root path where every log in logs stay
     * @return 
     */
    public static boolean serialize(List<G02Log> logs, Path logFilesRootPath ){
        if ((logs == null) || (logFilesRootPath == null)){
            return false;
        }
        boolean result = true;
        for (G02Log log : logs){
            result = result && serialize(log, logFilesRootPath.resolve(log.getLogUuid()));
        }
        return result;
    }

    /**
     * 
     * @param logFilePath - this physical path should exist in the file system
     */
    public static G02Log deserialize(Path logFilePath ){
        if (logFilePath == null){
            return null;
        }
        G02Log aG02Log = null;
        
        try{
            if (Files.isRegularFile(logFilePath)){
                try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(logFilePath.toFile()))){
                    aG02Log = (G02Log)in.readObject();
                }
            }
        }catch(IOException | ClassNotFoundException ex){
            Logger.getLogger(PeonyLogger.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            aG02Log = null;
        }

        return aG02Log;
    }
    
}
