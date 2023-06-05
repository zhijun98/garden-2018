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

import com.zcomapproach.garden.persistence.constant.GardenArchivedFileType;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.constant.GardenUploadedFileExtension;
import com.zcomapproach.garden.persistence.entity.G01ArchivedDocument;
import com.zcomapproach.garden.persistence.entity.G01ArchivedDocumentBk;
import com.zcomapproach.garden.persistence.entity.G01DocumentRequirement;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.garden.rose.RosePageName;
import com.zcomapproach.garden.rose.data.profile.DocumentRequirementProfile;
import com.zcomapproach.garden.rose.data.profile.RoseArchivedDocumentProfile;
import com.zcomapproach.garden.rose.data.profile.RoseArchivedFileTypeProfile;
import com.zcomapproach.garden.rose.data.profile.TaxcorpCaseProfile;
import com.zcomapproach.garden.rose.data.profile.TaxpayerCaseProfile;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseDataAgent;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.garden.rose.util.RoseWebUtils;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import com.zcomapproach.commons.nio.ZcaNio;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author zhijun98
 */
@Named(value = "roseDocumentBean")
@ViewScoped
public class RoseDocumentBean extends AbstractRoseViewBean{
    
    private String requestedArchivedFileUuid;
    
    private G01ArchivedDocument targetGardenArchivedDocument;

    /**
     * Optionally
     */
    private TaxpayerCaseProfile targetTaxpayerCaseProfile;
    private TaxcorpCaseProfile targetTaxcorpCaseProfile;
    
    public boolean isReadyForDownloadWebLink(){
        boolean result = false;
        if (ZcaValidator.isNotNullEmpty(getTargetDownloadedFileName())) {
            try {
                //check flag file
                result = ZcaNio.isValidFile(new File(getRoseSettings().getArchivedFileLocation() + File.separator
                        + getTargetDownloadedFileName() + "." + GardenUploadedFileExtension.FLAG.value()));
            } catch (IOException ex) {
                Logger.getLogger(RoseDocumentTransferBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    public String getRequestedArchivedFileUuid() {
        return requestedArchivedFileUuid;
    }

    public void setRequestedArchivedFileUuid(String requestedArchivedFileUuid) {
        if (ZcaValidator.isNotNullEmpty(requestedArchivedFileUuid)){
            //initialize targetGardenArchivedDocument
            targetGardenArchivedDocument = getBusinessEJB().findEntityByUuid(G01ArchivedDocument.class, requestedArchivedFileUuid);
            if (targetGardenArchivedDocument != null){
                super.setRequestedEntityType(GardenEntityType.convertEnumNameToType(targetGardenArchivedDocument.getEntityType()).value());
                super.setRequestedEntityUuid(targetGardenArchivedDocument.getEntityUuid());
            }
//            //grab the physical file onto the disk by email
//            if ((targetGardenArchivedDocument != null) && (!isReadyForDownloadWebLink())){
//                try {
//                    getRuntimeEJB().fetchEmailAttachment(getRoseSettings().getArchivedFileLocation(), 
//                                                          targetGardenArchivedDocument.getFileName());
//                } catch (IOException ex) {
//                    //Logger.getLogger(RoseDocumentTransferBean.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
        }
        this.requestedArchivedFileUuid = requestedArchivedFileUuid;
    }

    public G01ArchivedDocument getTargetGardenArchivedDocument() {
        if (targetGardenArchivedDocument == null){
            targetGardenArchivedDocument = new G01ArchivedDocument();
        }
        return targetGardenArchivedDocument;
    }

    public void setTargetGardenArchivedDocument(G01ArchivedDocument targetGardenArchivedDocument) {
        this.targetGardenArchivedDocument = targetGardenArchivedDocument;
    }
    
    private TaxpayerCaseProfile getTargetTaxpayerCaseProfile() {
        if (targetTaxpayerCaseProfile == null){
            targetTaxpayerCaseProfile = getTaxpayerEJB().findTaxpayerCaseProfileByTaxpayerCaseUuid(getRequestedEntityUuid());
        }
        return targetTaxpayerCaseProfile;
    }

    public void setTargetTaxpayerCaseProfile(TaxpayerCaseProfile targetTaxpayerCaseProfile) {
        this.targetTaxpayerCaseProfile = targetTaxpayerCaseProfile;
    }

    public TaxcorpCaseProfile getTargetTaxcorpCaseProfile() {
        if (targetTaxcorpCaseProfile == null){
            targetTaxcorpCaseProfile = getTaxcorpEJB().findTaxcorpCaseProfileByTaxcorpCaseUuid(getRequestedEntityUuid());
        }
        return targetTaxcorpCaseProfile;
    }

    public void setTargetTaxcorpCaseProfile(TaxcorpCaseProfile targetTaxcorpCaseProfile) {
        this.targetTaxcorpCaseProfile = targetTaxcorpCaseProfile;
    }
    
    public String getRoseDocumentTransferHeader(){
        switch(getEntityType()){
            case TAXPAYER_CASE:
                if (ZcaValidator.isNullEmpty(getTargetGardenArchivedDocument().getFileStatus())){
                    getTargetGardenArchivedDocument().setFileStatus(GardenArchivedFileType.TAXPAYER_DOCUMENT.value());
                }
                TaxpayerCaseProfile aTaxpayerCaseProfile = getTargetTaxpayerCaseProfile();
                if (aTaxpayerCaseProfile != null){
                    return aTaxpayerCaseProfile.getProfileDescriptiveName();
                }
                break;
            case TAXCORP_CASE:
                if (ZcaValidator.isNullEmpty(getTargetGardenArchivedDocument().getFileStatus())){
                    getTargetGardenArchivedDocument().setFileStatus(GardenArchivedFileType.TAXCORP_DOCUMENT.value());
                }
                TaxcorpCaseProfile aTaxcorpCaseProfile = getTargetTaxcorpCaseProfile();
                if (aTaxcorpCaseProfile != null){
                    return aTaxcorpCaseProfile.getProfileDescriptiveName();
                }
                break;
        }
        return RoseText.getText("Upload");
    }
    
    @Override
    public String getRosePageTopic(){
        String result = RoseText.getText("UploadFiles");
        switch(getEntityType()){
            case TAXPAYER_CASE:
                TaxpayerCaseProfile aTaxpayerCaseProfile = getTargetTaxpayerCaseProfile();
                if (aTaxpayerCaseProfile != null){
                    result = result + ": " + RoseText.getText("PersonalFamilyTaxFiling") 
                            + " - " + aTaxpayerCaseProfile.getProfileDescriptiveName();
                }
                break;
            case TAXCORP_CASE:
                TaxcorpCaseProfile aTaxcorpCaseProfile = getTargetTaxcorpCaseProfile();
                if (aTaxcorpCaseProfile != null){
                    result = result + ": " + RoseText.getText("CorporateTaxFiling") 
                            + " - " + aTaxcorpCaseProfile.getProfileDescriptiveName();
                }
                break;
        }
        return result;
    }

    @Override
    public String getTopicIconAwesomeName() {
        return "file";
    }
    
    @Override
    public String getTargetReturnWebPath() {
        HashMap<String, String> params = new HashMap<>();
        params.put(getRoseParamKeys().getEntityTypeParamKey(), getRequestedEntityType());
        params.put(getRoseParamKeys().getEntityUuidParamKey(), getRequestedEntityUuid());
        return getTargetReturnWebPageName() + RoseWebUtils.constructWebQueryString(params, true);
    }

    public String getGoBackWebPath(){
        HashMap<String, String> params = new HashMap<>();
        if ((ZcaValidator.isNotNullEmpty(getRequestedEntityUuid())) && (targetTaxpayerCaseProfile != null)){
            params.put(getRoseParamKeys().getTaxpayerCaseUuidParamKey(), getRequestedEntityUuid());
            if (getRoseUserSession().isEmployed()){
                return RoseJsfUtils.getRootWebPath()+ RoseWebUtils.BUSINESS_FOLDER + "/" + RosePageName.TaxpayerCaseMgtPage.name() + RoseWebUtils.JSF_EXT + RoseWebUtils.constructWebQueryString(params, true);
            }else{
                return RoseJsfUtils.getRootWebPath()+ RoseWebUtils.CUSTOMER_FOLDER + "/" + RosePageName.TaxpayerCaseViewPage.name() + RoseWebUtils.JSF_EXT + RoseWebUtils.constructWebQueryString(params, true);
            }
        }
        if ((ZcaValidator.isNotNullEmpty(getRequestedEntityUuid())) && (targetTaxcorpCaseProfile != null)){
            params.put(getRoseParamKeys().getTaxcorpCaseUuidParamKey(), getRequestedEntityUuid());
            if (getRoseUserSession().isEmployed()){
                return RoseJsfUtils.getRootWebPath()+ RoseWebUtils.BUSINESS_FOLDER + "/" + RosePageName.TaxcorpCaseMgtPage.name() + RoseWebUtils.JSF_EXT + RoseWebUtils.constructWebQueryString(params, true);
            }else{
                return RoseJsfUtils.getRootWebPath()+ RoseWebUtils.CUSTOMER_FOLDER + "/" + RosePageName.TaxcorpCaseViewPage.name() + RoseWebUtils.JSF_EXT + RoseWebUtils.constructWebQueryString(params, true);
            }
        }
        return  RosePageName.WelcomePage.name();
    }

    public String getTargetDownloadedFileName(){
        if (targetGardenArchivedDocument == null){
            return "Not available";
        }else{
            return targetGardenArchivedDocument.getFileName();
        }
    }

    public String getTargetDownloadedFileCustomName(){
        if (targetGardenArchivedDocument == null){
            return "Not available";
        }else{
            if (ZcaValidator.isNullEmpty(targetGardenArchivedDocument.getFileCustomName())){
                return targetGardenArchivedDocument.getFileName();
            }else{
                return targetGardenArchivedDocument.getFileCustomName();
            }
        }
    }
    
    public String getTargetDownloadedFileTimestamp(){
        if (targetGardenArchivedDocument == null){
            return "Not available";
        }else{
            return ZcaCalendar.convertToMMddyyyyHHmmss(targetGardenArchivedDocument.getFileTimestamp(), "-", " @ ", ":");
        }
    }

    @Override
    public List<RoseArchivedFileTypeProfile> getRoseArchivedFileTypeProfileList() {
        if (targetTaxpayerCaseProfile != null){
            return RoseDataAgent.loadIntoRoseArchivedFileTypeProfiles(targetTaxpayerCaseProfile.getUploadedArchivedDocumentList());
        }else if (targetTaxcorpCaseProfile != null){
            return RoseDataAgent.loadIntoRoseArchivedFileTypeProfiles(targetTaxcorpCaseProfile.getUploadedArchivedDocumentList());
        }else{
            return new ArrayList<>();   //zzj todo: implement for other cases
        }
    }

    @Override
    public List<DocumentRequirementProfile> getDocumentRequirementProfileList() {
        if (targetTaxpayerCaseProfile != null){
            return targetTaxpayerCaseProfile.getDocumentRequirementprofileList();
        }else if (targetTaxcorpCaseProfile != null){
            return targetTaxcorpCaseProfile.getDocumentRequirementprofileList();
        }else{
            return new ArrayList<>();   //zzj todo: implement for other cases
        }
    }

    @Override
    public List<DocumentRequirementProfile> getAllDocumentRequirementProfileList() {
        if (targetTaxpayerCaseProfile != null){
            if (targetTaxpayerCaseProfile.getAllDocumentRequirementProfileList().isEmpty()){
                targetTaxpayerCaseProfile.setAllDocumentRequirementProfileList(
                        constructAllDocumentRequirementProfileList(targetTaxpayerCaseProfile.getTaxpayerCaseEntity().getTaxpayerCaseUuid(), 
                                                                   GardenEntityType.TAXPAYER_CASE));
            }
            return targetTaxpayerCaseProfile.getAllDocumentRequirementProfileList();
        }else if (targetTaxcorpCaseProfile != null){
            if (targetTaxcorpCaseProfile.getAllDocumentRequirementProfileList().isEmpty()){
                targetTaxcorpCaseProfile.setAllDocumentRequirementProfileList(
                        constructAllDocumentRequirementProfileList(targetTaxcorpCaseProfile.getTaxcorpCaseEntity().getTaxcorpCaseUuid(), 
                                                                   GardenEntityType.TAXCORP_CASE));
            }
            return targetTaxcorpCaseProfile.getAllDocumentRequirementProfileList();
        }else{
            return new ArrayList<>();   //zzj todo: implement for other cases
        }
    }

    @Override
    public void storeDocumentRequirementProfile(String documentUuid) {
        if (targetTaxpayerCaseProfile != null){
            storeDocumentRequirementProfileHelper(documentUuid, targetTaxpayerCaseProfile.getDocumentRequirementprofileList());
        }else if (targetTaxcorpCaseProfile != null){
            storeDocumentRequirementProfileHelper(documentUuid, targetTaxcorpCaseProfile.getDocumentRequirementprofileList());
        }else{
        }
    }

    @Override
    public void deleteDocumentRequirementProfile(String documentUuid) {
        if (targetTaxpayerCaseProfile != null){
            deleteDocumentRequirementProfileHelper(documentUuid, targetTaxpayerCaseProfile.getDocumentRequirementprofileList());
        }else if (targetTaxcorpCaseProfile != null){
            deleteDocumentRequirementProfileHelper(documentUuid, targetTaxcorpCaseProfile.getDocumentRequirementprofileList());
        }else{
        }
    }

    @Override
    public void storeTargetDocumentRequirementProfile() {
        G01DocumentRequirement aG01DocumentRequirement = getTargetDocumentRequirementProfile().getDocumentRequirementEntity();
        if (ZcaValidator.isNullEmpty(aG01DocumentRequirement.getDocumentUuid())){
            aG01DocumentRequirement.setDocumentUuid(GardenData.generateUUIDString());
        }
        if (ZcaValidator.isNullEmpty(aG01DocumentRequirement.getServiceTagUuid())){
            aG01DocumentRequirement.setServiceTagUuid(GardenData.generateUUIDString());
        }
        
        try {
            if (targetTaxpayerCaseProfile != null){
                aG01DocumentRequirement.setEntityType(GardenEntityType.TAXPAYER_CASE.name());
                aG01DocumentRequirement.setEntityUuid(targetTaxpayerCaseProfile.getTaxpayerCaseEntity().getTaxpayerCaseUuid());
                storeTargetDocumentRequirementProfileHelper(getTargetDocumentRequirementProfile(), targetTaxpayerCaseProfile.getDocumentRequirementprofileList());
                RoseJsfUtils.setGlobalSuccessfulOperationMessage();
            }else if (targetTaxcorpCaseProfile != null){
                aG01DocumentRequirement.setEntityType(GardenEntityType.TAXCORP_CASE.name());
                aG01DocumentRequirement.setEntityUuid(targetTaxcorpCaseProfile.getTaxcorpCaseEntity().getTaxcorpCaseUuid());
                storeTargetDocumentRequirementProfileHelper(getTargetDocumentRequirementProfile(), targetTaxcorpCaseProfile.getDocumentRequirementprofileList());
                RoseJsfUtils.setGlobalSuccessfulOperationMessage();
            }else{
            }
        } catch (Exception ex) {
            //Logger.getLogger(RoseDocumentTransferBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalFatalFacesMessage(ex.getMessage());
        }
        
        setTargetDocumentRequirementProfile(new DocumentRequirementProfile());
    }

    private String getGoBackWebPathForTargetGardenArchivedDocumentCrud(){
        HashMap<String, String> params = new HashMap<>();
        if ((ZcaValidator.isNotNullEmpty(getRequestedEntityUuid())) && (targetTaxpayerCaseProfile != null)){
            params.put(getRoseParamKeys().getTaxpayerCaseUuidParamKey(), getRequestedEntityUuid());
            if (getRoseUserSession().isEmployed()){
                return RosePageName.TaxpayerCaseMgtPage.name() + RoseWebUtils.constructWebQueryString(params, true);
            }else{
                return RosePageName.TaxpayerCaseViewPage.name() + RoseWebUtils.constructWebQueryString(params, true);
            }
        }
        if ((ZcaValidator.isNotNullEmpty(getRequestedEntityUuid())) && (targetTaxcorpCaseProfile != null)){
            params.put(getRoseParamKeys().getTaxcorpCaseUuidParamKey(), getRequestedEntityUuid());
            if (getRoseUserSession().isEmployed()){
                return RosePageName.TaxcorpCaseMgtPage.name() + RoseWebUtils.constructWebQueryString(params, true);
            }else{
                return RosePageName.TaxcorpCaseViewPage.name() + RoseWebUtils.constructWebQueryString(params, true);
            }
        }
        return  RosePageName.WelcomePage.name();
    }
    
    public String deleteTargetGardenArchivedDocument(){
        G01ArchivedDocument aG01ArchivedDocument = getTargetGardenArchivedDocument();
        G01ArchivedDocumentBk aG01ArchivedDocumentBk = new G01ArchivedDocumentBk();
        aG01ArchivedDocumentBk.setFileName(aG01ArchivedDocument.getFileName());
        aG01ArchivedDocumentBk.setFileLocation(aG01ArchivedDocument.getFileLocation());
        aG01ArchivedDocumentBk.setFileStatus(aG01ArchivedDocument.getFileStatus());
        aG01ArchivedDocumentBk.setFileTimestamp(aG01ArchivedDocument.getFileTimestamp());
        aG01ArchivedDocumentBk.setProviderUuid(aG01ArchivedDocument.getProviderUuid());
        aG01ArchivedDocumentBk.setEntityType(aG01ArchivedDocument.getEntityType());
        aG01ArchivedDocumentBk.setEntityUuid(aG01ArchivedDocument.getEntityUuid());
        aG01ArchivedDocumentBk.setDownloadClient(aG01ArchivedDocument.getDownloadClient());
        aG01ArchivedDocumentBk.setFileCustomName(aG01ArchivedDocument.getFileCustomName());
        aG01ArchivedDocumentBk.setMemo(aG01ArchivedDocument.getMemo());
        aG01ArchivedDocumentBk.setEntityStatus(aG01ArchivedDocument.getEntityStatus());
        aG01ArchivedDocumentBk.setArchivedDocumentUuid(aG01ArchivedDocument.getArchivedDocumentUuid());
        aG01ArchivedDocumentBk.setCreated(aG01ArchivedDocument.getCreated());
        aG01ArchivedDocumentBk.setUpdated(aG01ArchivedDocument.getUpdated());
        
        try {
            getBusinessEJB().storeEntityByUuid(G01ArchivedDocumentBk.class, aG01ArchivedDocumentBk,
                    aG01ArchivedDocumentBk.getArchivedDocumentUuid(),
                    G01DataUpdaterFactory.getSingleton().getG01ArchivedDocumentBkUpdater());
            getBusinessEJB().deleteEntityByUuid(G01ArchivedDocument.class, aG01ArchivedDocument.getArchivedDocumentUuid());
            //delete physical file from the disk
            String savedFileName = aG01ArchivedDocument.getFileLocation() + aG01ArchivedDocument.getFileName();
            if (ZcaNio.isValidFile(savedFileName)){
                try{
                    ZcaNio.deleteFile(savedFileName);
                }catch(IOException ex){
                
                }
            }
            
            RoseJsfUtils.setGlobalSuccessfulOperationMessage();
        } catch (Exception ex) {
            //Logger.getLogger(RoseDocumentBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalFatalFacesMessage(ex.getMessage());
        }
        
        return getGoBackWebPathForTargetGardenArchivedDocumentCrud();
    }

    public String storeTargetGardenArchivedDocument(){
        try {
            storeTargetGardenArchivedDocumentHelper(null, null);
            RoseJsfUtils.setGlobalSuccessfulOperationMessage();
        } catch (Exception ex) {
            //Logger.getLogger(RoseDocumentBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalFatalFacesMessage(ex.getMessage());
        }
        return getGoBackWebPathForTargetGardenArchivedDocumentCrud();
    }
    
    protected RoseArchivedDocumentProfile storeTargetGardenArchivedDocumentHelper(String archivedFileUuid, String savedFileName) throws Exception {

        G01ArchivedDocument aG01ArchivedDocument = getTargetGardenArchivedDocument();
        if (archivedFileUuid != null){
            aG01ArchivedDocument.setArchivedDocumentUuid(archivedFileUuid);
        }
        aG01ArchivedDocument.setEntityType(GardenEntityType.convertEnumValueToType(getRequestedEntityType()).name());
        aG01ArchivedDocument.setEntityUuid(getRequestedEntityUuid());
        //aG01ArchivedDocument.setFileStatus(null);
        if (savedFileName != null){
            aG01ArchivedDocument.setFileName(FilenameUtils.getName(savedFileName));
            aG01ArchivedDocument.setFileLocation(FilenameUtils.getFullPath(savedFileName));
        }
        if (aG01ArchivedDocument.getFileTimestamp() == null){
            aG01ArchivedDocument.setFileTimestamp(new Date());
        }
        aG01ArchivedDocument.setProviderUuid(this.getRoseUserSession().getTargetAccountProfile().getAccountEntity().getAccountUuid());
        
        getBusinessEJB().storeEntityByUuid(G01ArchivedDocument.class, aG01ArchivedDocument, 
                                           aG01ArchivedDocument.getArchivedDocumentUuid(), 
                                           G01DataUpdaterFactory.getSingleton().getG01ArchivedDocumentUpdater());
        
        RoseArchivedDocumentProfile aGardenArchivedDocumentProfile = new RoseArchivedDocumentProfile();
        aGardenArchivedDocumentProfile.setArchivedDocumentEntity(aG01ArchivedDocument);
        return aGardenArchivedDocumentProfile;
    }

}
