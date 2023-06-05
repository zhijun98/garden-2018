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

package com.zcomapproach.garden.rose.servlet;

import com.zcomapproach.garden.data.GardenFlower;
import com.zcomapproach.garden.data.GardenWebParamKey;
import com.zcomapproach.garden.data.GardenWebParamValue;
import com.zcomapproach.garden.persistence.entity.G02Employee;
import com.zcomapproach.garden.rose.persistence.RoseManagementEJB02;
import com.zcomapproach.garden.rose.servlet.data.RoseDownloadUploadParamData;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.commons.nio.ZcaNio;
import com.zcomapproach.garden.util.GardenData;
import com.zcomapproach.garden.util.GardenEnvironment;
import com.zcomapproach.commons.nio.ZcaNio;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

/**
 * Refer to https://examples.javacodegeeks.com/enterprise-java/servlet/java-servlet-file-download-file-upload-example/
 * Sample project: "C:\Users\zhijun98\Documents\Sandbox\Java Servlet File Download and File Upload Example"
 * <p>
 * @author zhijun98
 */
@WebServlet(description = "Classic upload file channel for clients", urlPatterns = { "/roseFileUploadServlet" })
//@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 50, maxFileSize = 1024 * 1024 * 100, maxRequestSize = 1024 * 1024 * 250)
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 50)
public class RoseFileUploadServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(RoseFileUploadServlet.class.getName());
    private static final long serialVersionUID = 1L;
    
    @EJB
    private RoseManagementEJB02 mgtEjb;

    /***** This Method Is Called By The Servlet Container To Process A 'POST' Request *****/
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleRequest(request, response);
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Collection<Part> parts = request.getParts();
        GardenWebParamValue purpose = findRoseUploadPurpose(parts);
        switch (purpose){
            case ARCHIVE_FILE:
                processArchiveFile(parts);
                return;
            default:
                //skip
        }
    }
    
    private void processArchiveFile(Collection<Part> parts) {
        RoseDownloadUploadParamData aProcessArchiveFileParam = extractRoseUploadParamValues(parts);
        if (validateCredentials(aProcessArchiveFileParam.getRoseCode())){
            String archiveFullFilePathName = GardenEnvironment.constructServerSideArchiveFilePathName(aProcessArchiveFileParam.getFlower(), aProcessArchiveFileParam.getFileName());
            if (ZcaValidator.isNotNullEmpty(archiveFullFilePathName) && (aProcessArchiveFileParam.getFileData() != null)){
                /**
                 * backup the existing file if it exists and then delete it
                 */
                if (ZcaNio.isValidFile(archiveFullFilePathName)){
                    try {
                        Path backupFolderPath = GardenEnvironment.getServerSideArchivedFileRootPath(aProcessArchiveFileParam.getFlower()).resolve("backup");
                        if (!ZcaNio.isValidFolder(backupFolderPath.toFile())){
                            ZcaNio.createFolder(backupFolderPath);
                        }
                        Path targetLocalFileFullPathNameBk = backupFolderPath.resolve(FilenameUtils.getName(archiveFullFilePathName));
                        //change name for backup
                        ZcaNio.copyFile(archiveFullFilePathName, targetLocalFileFullPathNameBk.toFile().getAbsolutePath() + GardenEnvironment.underscore_delimiter + GardenData.generateUUIDString());
                        ZcaNio.deleteFile(archiveFullFilePathName);
                    } catch (IOException ex) {
                        Logger.getLogger(RoseFileUploadServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                /**
                 * Write the uploaded file onto the disk
                 */
                try {
                    InputStream inputStream = aProcessArchiveFileParam.getFileData().getInputStream();
                    InputStream in = new BufferedInputStream(inputStream);
                    FileOutputStream out;

                    out = new FileOutputStream(archiveFullFilePathName);

                    byte[] data = new byte[1024];
                    int length;
                    while ((length = in.read(data)) != -1){
                        out.write(data,0,length);
                    }
                    out.flush();
                    out.close();
                } catch (IOException ioObj) {
                    logger.log(Level.SEVERE, ioObj.getMessage());
                }
            }
        }
    }

    private boolean validateCredentials(String userUuid) {
        if (ZcaValidator.isNotNullEmpty(userUuid)){
            G02Employee aG02Employee = mgtEjb.findG02EmployeeByUuid(userUuid);
            return aG02Employee != null;
        }
        return false;
    }

    
    private static GardenWebParamValue findRoseUploadPurpose(Collection<Part> parts){
        for (Part part : parts){
            if (GardenWebParamKey.PURPOSE.name().equalsIgnoreCase(part.getName())){
                StringWriter writer = new StringWriter();
                return GardenWebParamValue.convertEnumNameToType(extractRoseUploadParamValuesHelper(part, writer));
            }
        }
        return GardenWebParamValue.UNKNOWN;
    }

    private static RoseDownloadUploadParamData extractRoseUploadParamValues(Collection<Part> parts) {
        RoseDownloadUploadParamData aProcessArchiveFileParam = new RoseDownloadUploadParamData();
        StringWriter writer;
        for (Part part : parts){
            writer = new StringWriter();
            if (GardenWebParamKey.FILE_NAME.name().equalsIgnoreCase(part.getName())){
                aProcessArchiveFileParam.setFileName(extractRoseUploadParamValuesHelper(part, writer));
            }else if (GardenWebParamKey.FLOWER_NAME.name().equalsIgnoreCase(part.getName())){
                aProcessArchiveFileParam.setFlower(GardenFlower.convertEnumNameToType(extractRoseUploadParamValuesHelper(part, writer), true));
            }else if (GardenWebParamKey.PURPOSE.name().equalsIgnoreCase(part.getName())){
                aProcessArchiveFileParam.setPurpose(GardenWebParamValue.convertEnumNameToType(extractRoseUploadParamValuesHelper(part, writer), true));
            }else if (GardenWebParamKey.GMAIL_UUID.name().equalsIgnoreCase(part.getName())){
                aProcessArchiveFileParam.setGmailUuid(extractRoseUploadParamValuesHelper(part, writer));
            }else if (GardenWebParamKey.GMAIL_ATT_UUID.name().equalsIgnoreCase(part.getName())){
                aProcessArchiveFileParam.setGmailAttUuid(extractRoseUploadParamValuesHelper(part, writer));
            }else if (GardenWebParamKey.GMAIL_ADDRESS.name().equalsIgnoreCase(part.getName())){
                aProcessArchiveFileParam.setGmailAddress(extractRoseUploadParamValuesHelper(part, writer));
            }else if (GardenWebParamKey.ROSE_CODE.name().equalsIgnoreCase(part.getName())){
                aProcessArchiveFileParam.setRoseCode(extractRoseUploadParamValuesHelper(part, writer));
            }else if (GardenWebParamKey.FILE_DATA.name().equalsIgnoreCase(part.getName())){
                aProcessArchiveFileParam.setFileData(part);
            }
        }
        return aProcessArchiveFileParam;
    }

    private static String extractRoseUploadParamValuesHelper(Part part, StringWriter writer) {
        try {
            IOUtils.copy(part.getInputStream(), writer, StandardCharsets.UTF_8);
            return writer.toString();
        } catch (IOException ex) {
            Logger.getLogger(RoseFileUploadServlet.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
