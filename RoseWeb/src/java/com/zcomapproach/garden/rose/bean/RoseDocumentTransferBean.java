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

package com.zcomapproach.garden.rose.bean;

import com.zcomapproach.garden.exception.NoAttachmentException;
import com.zcomapproach.garden.persistence.constant.GardenUploadedFileExtension;
import com.zcomapproach.garden.rose.data.profile.RoseArchivedDocumentProfile;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import com.zcomapproach.commons.nio.ZcaNio;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.MimetypesFileTypeMap;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author zhijun98
 */
@Named(value = "roseDocumentTransferBean")
@ViewScoped
public class RoseDocumentTransferBean extends RoseDocumentBean{
    
    private static final int MAX_SIZE = 1000*1000*50;

    private UploadedFile uploadedFile;
    
    private StreamedContent targetDownloadedFile;

    @Override
    public String getTopicIconAwesomeName() {
        return "upload";
    }

    public String getWebLinkForDownloadFile(){
         return RoseJsfUtils.getRootWebPath() + getTargetDownloadedFileName();
    }
    
    /**
     * 
     * @return 
     */
    public String downloadFileFromWebLink() {
        if (ZcaValidator.isNotNullEmpty(getTargetDownloadedFileName())) {
            FileInputStream input= null;
            try {
                File downloadFile = new File(getRoseSettings().getArchivedFileLocation() + File.separator + getTargetDownloadedFileName());
                FacesContext facesContext = FacesContext.getCurrentInstance();
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
                response.setHeader("Content-Disposition", "attachment;filename="+getTargetDownloadedFileName());
                response.setContentLength((int) downloadFile.length());
                int i= 0;
                input = new FileInputStream(downloadFile);  
                byte[] buffer = new byte[1024];
                while ((i = input.read(buffer)) != -1) {  
                    response.getOutputStream().write(buffer);  
                    response.getOutputStream().flush();  
                }               
                facesContext.responseComplete();
                facesContext.renderResponse();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(input != null) {
                        input.close();
                    }
                } catch(IOException ex) {
                    Logger.getLogger(RoseDocumentTransferBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } 
        return null;
    }
    
    public StreamedContent getTargetDownloadedFile() {
        if ((targetDownloadedFile == null) && (ZcaValidator.isNotNullEmpty(getTargetDownloadedFileName()))){
            if (isReadyForDownloadWebLink()){
                try {
                    File initialFile = new File(getRoseSettings().getArchivedFileLocation() + File.separator + getTargetDownloadedFileName());
                    if (ZcaNio.isValidFile(initialFile)){
                        InputStream stream = new FileInputStream(initialFile);
                        targetDownloadedFile = new DefaultStreamedContent(stream, 
                                                                          new MimetypesFileTypeMap().getContentType(initialFile), 
                                                                          getTargetDownloadedFileName());
                    }
                } catch (Exception ex) {
                    Logger.getLogger(RoseDocumentTransferBean.class.getName()).log(Level.SEVERE, null, ex);
                    targetDownloadedFile = null;
                }
            }
        }
        return targetDownloadedFile;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }
    
    public String uploadFile(){
        if (uploadedFile == null){
            RoseJsfUtils.setGlobalErrorFacesMessage(RoseText.getText("UploadFiles") + " - " + RoseText.getText("FieldRequired_T"));
        }else{
            if (ZcaValidator.isNotNullEmpty(getTargetGardenArchivedDocument().getFileCustomName())){
                getTargetGardenArchivedDocument().setFileCustomName(getTargetGardenArchivedDocument().getFileCustomName().trim());
                if (!ZcaNio.isValidRoseUploadFileName(getTargetGardenArchivedDocument().getFileCustomName())){
                    RoseJsfUtils.setGlobalErrorFacesMessage("The custom file name you gave is not valid. "
                            + "Only alphanumeric, underscore or/and file extension (ZIP/PDF/DOC/DOCX/XLS/XLSX/CVS/TXT/JPG/JPEG/GIF/PNG) are permitted");
                    return null;
                }
            }
            uploadFileHelper(GardenData.generateUUIDString());
            //for refresh
            setTargetTaxpayerCaseProfile(null);
            setTargetTaxcorpCaseProfile(null);
        }
        return null;
    }
    
    /**
     * 
     * @param fileNameCode
     * @param archivedFileStatus
     * @return - "fileNameCode.ext"
     */
    private String uploadFileHelper(String fileNameCode){
        String fileName = null;
        if (validateUploadedFile(uploadedFile)){
            try {
                if (getRoseSettings().isUploadFilePermitted()){
                    String fileLocation = getRoseSettings().getArchivedFileLocation();

                    //save the file onto the disk
                    File savedFile = saveUploadedFileOnServerSide(fileNameCode, FilenameUtils.getExtension(uploadedFile.getFileName()), fileLocation);
                    //save the file information into the garden
                    RoseArchivedDocumentProfile aGardenArchivedDocumentProfile = storeTargetGardenArchivedDocumentHelper(fileNameCode, savedFile.getAbsolutePath());
                    //email it with attachment
                    fileName = FilenameUtils.getName(savedFile.getAbsolutePath());
                    getRuntimeEJB().emailBackupArchivedDocumentAsynchronously(aGardenArchivedDocumentProfile, 
                                                                            savedFile, 
                                                                            getRoseUserSession().getTargetAccountProfile().retrieveEmail(), 
                                                                            fileName, //c583e51cz2b78z4654z9b13zd79f352d7650.pdf
                                                                            RoseJsfUtils.isProduction(), 
                                                                            getRoseSettings().isEmailDisabled());
                    RoseJsfUtils.setGlobalSuccessfulOperationMessage();
                }else{
                    RoseJsfUtils.setGlobalErrorFacesMessage("File uploaded is not permitted.");
                }
            } catch (Exception ex) {
                Logger.getLogger(RoseDocumentTransferBean.class.getName()).log(Level.SEVERE, null, ex);
                RoseJsfUtils.setGlobalErrorFacesMessage(ex.getMessage());
            }
        }
        return fileName;
    }

    /**
     * 
     * @param uploadedFile - cannot be NULL. this method does not validate this parameter against NULL value
     * @return 
     */
    protected boolean validateUploadedFile(UploadedFile uploadedFile) {
        boolean result = true;
        if (uploadedFile.getSize() > MAX_SIZE){
            RoseJsfUtils.setGlobalErrorFacesMessage("Max file size: 50.0 MB. Your file is too big.");
            result = false;
        }else{
            String fileName = uploadedFile.getFileName();
            if (ZcaValidator.isNotNullEmpty(fileName)){
                String fileExt = FilenameUtils.getExtension(fileName);
                if (!validateUploadedFileExtension(fileExt)){
                    RoseJsfUtils.setGlobalErrorFacesMessage("Please select a valid file for upload.");
                    result = false;
                }
            }
        }
        return result;
    }

    /**
     * 
     * @param fileName
     * @param fileExt
     * @param fileLocation - a folder string ended with "/" 
     * @return  
     * @throws com.zcomapproach.garden.exception.NoAttachmentException  
     */
    protected File saveUploadedFileOnServerSide(String fileName, String fileExt, String fileLocation) throws Exception{
        if (uploadedFile.getSize() == 0){
            throw new NoAttachmentException("Please choose file for upload");
        }
        
        File theFile = new File(fileLocation + File.separator + fileName + "." + fileExt);
        
        BufferedOutputStream stream=null;
        try {
            byte[] bytes=null;
            bytes = uploadedFile.getContents();
            stream = new BufferedOutputStream(new FileOutputStream(theFile));
            stream.write(bytes);
            stream.close();
        } catch (FileNotFoundException ex) {
            throw ex;
        } catch (IOException ex) {
            throw ex;
        } finally {
            try {
                stream.close();
            } catch (IOException ex) {
                throw ex;
            }
        }
        return theFile;
    }

    private boolean validateUploadedFileExtension(String fileExt) {
        return (fileExt != null) && (GardenUploadedFileExtension.PDF.value().equalsIgnoreCase(fileExt) 
                                        || GardenUploadedFileExtension.ZIP.value().equalsIgnoreCase(fileExt)
                                        || GardenUploadedFileExtension.DOC.value().equalsIgnoreCase(fileExt)
                                        || GardenUploadedFileExtension.DOCX.value().equalsIgnoreCase(fileExt)
                                        || GardenUploadedFileExtension.XLS.value().equalsIgnoreCase(fileExt)
                                        || GardenUploadedFileExtension.XLSX.value().equalsIgnoreCase(fileExt)
                                        || GardenUploadedFileExtension.CVS.value().equalsIgnoreCase(fileExt)
                                        || GardenUploadedFileExtension.TXT.value().equalsIgnoreCase(fileExt)
                                        || GardenUploadedFileExtension.JPG.value().equalsIgnoreCase(fileExt)
                                        || GardenUploadedFileExtension.JPEG.value().equalsIgnoreCase(fileExt)
                                        || GardenUploadedFileExtension.GIF.value().equalsIgnoreCase(fileExt)
                                        || GardenUploadedFileExtension.PNG.value().equalsIgnoreCase(fileExt));
    }
}
