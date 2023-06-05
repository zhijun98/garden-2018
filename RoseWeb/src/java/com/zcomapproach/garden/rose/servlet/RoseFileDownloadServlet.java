package com.zcomapproach.garden.rose.servlet;

import com.zcomapproach.garden.data.GardenFlower;
import com.zcomapproach.garden.data.GardenWebParamKey;
import com.zcomapproach.garden.data.GardenWebParamValue;
import com.zcomapproach.garden.guard.TechnicalController;
import com.zcomapproach.garden.persistence.entity.G02OfflineEmail;
import com.zcomapproach.garden.persistence.entity.G02OfflineEmailAttachment;
import com.zcomapproach.garden.rose.persistence.RoseManagementEJB02;
import com.zcomapproach.garden.rose.servlet.data.RoseDownloadUploadParamData;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import com.zcomapproach.garden.util.GardenEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(description = "Classic download file channel for clients", urlPatterns = { "/roseFileDownloadServlet" })
public class RoseFileDownloadServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(RoseFileDownloadServlet.class.getName());
    private static final long serialVersionUID = 1L;
    public static int BUFFER_SIZE = 1024 * 100;

    @EJB
    private RoseManagementEJB02 mgtEjb;
    
	/***** This Method Is Called By The Servlet Container To Process A 'GET' Request *****/
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handleRequest(request, response);
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RoseDownloadUploadParamData aRoseDownloadUploadParamData = parseRoseDownloadUploadParamData(request, response);
        String downloadinFilePath;
        switch (aRoseDownloadUploadParamData.getPurpose()){
            case ARCHIVE_FILE:
                downloadinFilePath = validateFilePathForArchiveFileDownload(aRoseDownloadUploadParamData);
                break;
            case GMAIL_PERSISTENT_FILE:
                downloadinFilePath = validateFilePathForGmailPersistentFileDownload(aRoseDownloadUploadParamData);
                break;
            case GMAIL_PERSISTENT_ATT:
                downloadinFilePath = validateFilePathForGmailPersistentAttachmentDownload(aRoseDownloadUploadParamData);
                break;
            default:
                downloadinFilePath = GardenData.generateUUIDString(); //make the file path/name always-invalid
        }
        

        File file = new File(downloadinFilePath);
        if (file.exists()) {
            OutputStream outStream = null;
            FileInputStream inputStream = null;
            /**** Setting The Content Attributes For The Response Object ****/
            String mimeType = "application/octet-stream";
            response.setContentType(mimeType);
            /**** Setting The Headers For The Response Object ****/
            String headerKey = "Content-Disposition";
            String headerValue = String.format("attachment; filename=\"%s\"", file.getName());
            response.setHeader(headerKey, headerValue);
            try {
                /**** Get The Output Stream Of The Response ****/
                outStream = response.getOutputStream();
                inputStream = new FileInputStream(file);
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = -1;
                /**** Write Each Byte Of Data Read From The Input Stream Write Each Byte Of Data  Read From The Input Stream Into The Output Stream ****/
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, bytesRead);
                }				
            } catch(IOException ioExObj) {
                    System.out.println("Exception While Performing The I/O Operation?= " + ioExObj.getMessage());
            } finally {				
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outStream != null) {
                    outStream.flush();
                    outStream.close();
                }
            }
        } else {
            /***** Set Response Content Type *****/
            response.setContentType("text/html");
            /***** Print The Response *****/
            response.getWriter().println("Cannot find "+ aRoseDownloadUploadParamData.getFileName());
        }
    }

    private RoseDownloadUploadParamData parseRoseDownloadUploadParamData(HttpServletRequest request, HttpServletResponse response) {
        RoseDownloadUploadParamData result = new RoseDownloadUploadParamData();
        result.setGmailAddress(request.getParameter(GardenWebParamKey.GMAIL_ADDRESS.name()));
        result.setGmailUuid(request.getParameter(GardenWebParamKey.GMAIL_UUID.name()));
        result.setGmailAttUuid(request.getParameter(GardenWebParamKey.GMAIL_ATT_UUID.name()));
        result.setRoseCode(request.getParameter(GardenWebParamKey.ROSE_CODE.name()));
        result.setFlower(GardenFlower.convertEnumNameToType(request.getParameter(GardenWebParamKey.FLOWER_NAME.name()), true));
        result.setFileName(request.getParameter(GardenWebParamKey.FILE_NAME.name()));
        result.setPurpose(GardenWebParamValue.convertEnumNameToType(request.getParameter(GardenWebParamKey.PURPOSE.name()), true));
        return result;
    }

    private String validateFilePathForArchiveFileDownload(RoseDownloadUploadParamData aRoseDownloadUploadParamData) {
        String filePath = GardenData.generateUUIDString(); //make the file path/name always-invalid
        //validate flower name...
        if ((aRoseDownloadUploadParamData.getFlower() == null) || (ZcaValidator.isNullEmpty(aRoseDownloadUploadParamData.getFlower().name()))){
            return filePath;
        }
        //validate userUuid...
        if (ZcaValidator.isNullEmpty(aRoseDownloadUploadParamData.getRoseCode())){
            return filePath;
        }else{
            if (mgtEjb.findG02EmployeeByUuid(aRoseDownloadUploadParamData.getRoseCode()) == null){
                return filePath;
            }
        }
        //validate file name...
        if (ZcaValidator.isNullEmpty(aRoseDownloadUploadParamData.getFileName())){
            return filePath;
        }
        //calculate filePath....
        filePath = GardenEnvironment.constructServerSideArchiveFilePathName(aRoseDownloadUploadParamData.getFlower(), aRoseDownloadUploadParamData.getFileName());
        if (ZcaValidator.isNullEmpty(filePath)){
            return GardenData.generateUUIDString(); //make the file path/name always-invalid
        }
        return filePath;
    }

    private String validateFilePathForGmailPersistentFileDownload(RoseDownloadUploadParamData aRoseDownloadUploadParamData) {
        String filePath = GardenData.generateUUIDString(); //make the file path/name always-invalid
        //validate flower name...
        if ((aRoseDownloadUploadParamData.getFlower() == null) || (ZcaValidator.isNullEmpty(aRoseDownloadUploadParamData.getFlower().name()))){
            return filePath;
        }
        //validate userUuid...
        if (ZcaValidator.isNullEmpty(aRoseDownloadUploadParamData.getRoseCode())){
            return filePath;
        }else{
            if (mgtEjb.findG02EmployeeByUuid(aRoseDownloadUploadParamData.getRoseCode()) == null){
                return filePath;
            }
        }
        //validate gmail addess and UUID...
        if ((ZcaValidator.isNullEmpty(aRoseDownloadUploadParamData.getGmailAddress())) || (ZcaValidator.isNullEmpty(aRoseDownloadUploadParamData.getGmailUuid()))){
            return filePath;
        }
        G02OfflineEmail controllerG02OfflineEmail = mgtEjb.findG02OfflineEmailByOwnwerAddressMsgId(TechnicalController.Controller_UUID.value(), 
                aRoseDownloadUploadParamData.getGmailAddress(), aRoseDownloadUploadParamData.getGmailUuid());
        if (controllerG02OfflineEmail == null){
            return filePath;
        }
        return controllerG02OfflineEmail.getMessagePath();
    }

    private String validateFilePathForGmailPersistentAttachmentDownload(RoseDownloadUploadParamData aRoseDownloadUploadParamData) {
        String attFilePath = GardenData.generateUUIDString(); //make the file path/name always-invalid
        //validate flower name...
        if ((aRoseDownloadUploadParamData.getFlower() == null) || (ZcaValidator.isNullEmpty(aRoseDownloadUploadParamData.getFlower().name()))){
            return attFilePath;
        }
        //validate userUuid...
        if (ZcaValidator.isNullEmpty(aRoseDownloadUploadParamData.getRoseCode())){
            return attFilePath;
        }else{
            if (mgtEjb.findG02EmployeeByUuid(aRoseDownloadUploadParamData.getRoseCode()) == null){
                return attFilePath;
            }
        }
        //validate gmail addess and UUID...
        if ((ZcaValidator.isNullEmpty(aRoseDownloadUploadParamData.getGmailAddress())) || (ZcaValidator.isNullEmpty(aRoseDownloadUploadParamData.getGmailUuid()))){
            return attFilePath;
        }
        G02OfflineEmail controllerG02OfflineEmail = mgtEjb.findG02OfflineEmailByOwnwerAddressMsgId(TechnicalController.Controller_UUID.value(), 
                aRoseDownloadUploadParamData.getGmailAddress(), aRoseDownloadUploadParamData.getGmailUuid());
        if (controllerG02OfflineEmail == null){
            return attFilePath;
        }
        List<G02OfflineEmailAttachment> controllerG02OfflineEmailAttachmentList = mgtEjb.findG02OfflineEmailAttachmentListByOfflineEmailUuid(controllerG02OfflineEmail.getOfflineEmailUuid());
        if ((controllerG02OfflineEmailAttachmentList == null) || (controllerG02OfflineEmailAttachmentList.isEmpty())){
            return attFilePath;
        }
        //validate gmail attachment's UUID...
        if ((ZcaValidator.isNullEmpty(aRoseDownloadUploadParamData.getGmailAttUuid()))){
            return attFilePath;
        }
        G02OfflineEmailAttachment aG02OfflineEmailAttachment = mgtEjb.findEntityByUuid(G02OfflineEmailAttachment.class, aRoseDownloadUploadParamData.getGmailAttUuid());
        if (aG02OfflineEmailAttachment == null){
            return attFilePath;
        }
        //find the attFilePath
        for (G02OfflineEmailAttachment controllerG02OfflineEmailAttachment : controllerG02OfflineEmailAttachmentList){
            if (controllerG02OfflineEmailAttachment.getOriginalFileName().equalsIgnoreCase(aG02OfflineEmailAttachment.getOriginalFileName())){
                return controllerG02OfflineEmailAttachment.getFilePath();   //find it
            }
        }
        return attFilePath;
    }
}
