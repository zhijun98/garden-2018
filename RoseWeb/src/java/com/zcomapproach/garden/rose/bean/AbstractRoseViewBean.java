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

import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.persistence.constant.GardenEntityStatus;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G01ArchivedDocument;
import com.zcomapproach.garden.persistence.entity.G01DocumentRequirement;
import com.zcomapproach.garden.persistence.entity.G01ServiceTag;
import com.zcomapproach.garden.rose.data.profile.DocumentRequirementProfile;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import com.zcomapproach.commons.nio.ZcaNio;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.MimetypesFileTypeMap;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * Beans which support specific web pages
 * @author zhijun98
 */
public abstract class AbstractRoseViewBean extends AbstractRoseComponentBean implements IRoseArchivedDocumentManager{
    
    private boolean viewOnly = true;
    
    private DocumentRequirementProfile targetDocumentRequirementProfile;
    private String targetArchivedDocumentUuidForDownload;
    private boolean documentRequirementProfileListSetupDemanded;

    public boolean isViewOnly() {
        return viewOnly;
    }

    public void setViewOnly(boolean viewOnly) {
        this.viewOnly = viewOnly;
    }

    @Override
    public DocumentRequirementProfile getTargetDocumentRequirementProfile() {
        if (targetDocumentRequirementProfile == null){
            targetDocumentRequirementProfile = new DocumentRequirementProfile();
        }
        return targetDocumentRequirementProfile;
    }

    @Override
    public void setTargetDocumentRequirementProfile(DocumentRequirementProfile targetDocumentRequirementProfile) {
        this.targetDocumentRequirementProfile = targetDocumentRequirementProfile;
    }
    
    private boolean displayDocumentRequirementProfileDataEntryPanelDemanded;

    @Override
    public boolean isDisplayDocumentRequirementProfileDataEntryPanelDemanded() {
        return displayDocumentRequirementProfileDataEntryPanelDemanded;
    }
    
    @Override
    public void hideDocumentRequirementProfileDataEntryPanel(){
        displayDocumentRequirementProfileDataEntryPanelDemanded = false;
    }
    
    @Override
    public void displayDocumentRequirementProfileDataEntryPanelForEdit(String documentUuid){
        if (ZcaValidator.isNullEmpty(documentUuid)){
            return;
        }
        DocumentRequirementProfile aDocumentRequirementProfile = findDocumentRequirementProfileFromListForEdit(documentUuid);
        if (aDocumentRequirementProfile != null){
            targetDocumentRequirementProfile = aDocumentRequirementProfile;
            displayDocumentRequirementProfileDataEntryPanelDemanded = true;
        }
    }
    
    /**
     * Find the DocumentRequirementProfile for edit
     * @param documentUuid
     * @return 
     */
    
    private DocumentRequirementProfile findDocumentRequirementProfileFromListForEdit(String documentUuid){
        List<DocumentRequirementProfile> aDocumentRequirementProfileList = getDocumentRequirementProfileList();
        for (DocumentRequirementProfile aBusinessCaseBillProfile : aDocumentRequirementProfileList){
            if (aBusinessCaseBillProfile.getDocumentRequirementEntity().getDocumentUuid().equalsIgnoreCase(documentUuid)){
                return aBusinessCaseBillProfile;
            }
        }
        return null;
    }
    
    @Override
    public void saveTargetDocumentRequirementProfile(){
        if ((targetDocumentRequirementProfile != null) && (targetDocumentRequirementProfile.getDocumentRequirementEntity() != null)
                && (ZcaValidator.isNotNullEmpty(targetDocumentRequirementProfile.getDocumentRequirementEntity().getDocumentUuid())))
        {
            try {
                getBusinessEJB().storeDocumentRequirementProfile(targetDocumentRequirementProfile);
            } catch (Exception ex) {
                //Logger.getLogger(AbstractRoseViewBean.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.hideDocumentRequirementProfileDataEntryPanel();
        }
    }

    protected void deleteDocumentRequirementProfileHelper(String documentUuid, List<DocumentRequirementProfile> aDocumentRequirementprofileList) {
        List<DocumentRequirementProfile> allDocumentRequirementProfileList = getAllDocumentRequirementProfileList();
        for (DocumentRequirementProfile aDocumentRequirementProfile : allDocumentRequirementProfileList){
            if (aDocumentRequirementProfile.getDocumentRequirementEntity().getDocumentUuid().equalsIgnoreCase(documentUuid)){
                //delete DocumentRequirementProfile
                try {
                    getBusinessEJB().deleteDocumentRequirementProfileAndServiceTag(aDocumentRequirementProfile);
                    RoseJsfUtils.setGlobalSuccessfulOperationMessage();
                } catch (Exception ex) {
                    //Logger.getLogger(AbstractRoseViewBean.class.getName()).log(Level.SEVERE, null, ex);
                    RoseJsfUtils.setGlobalInfoFacesMessage(RoseText.getText("SystemError") + ": " + ex.getMessage());
                }
                aDocumentRequirementprofileList.remove(aDocumentRequirementProfile);
                allDocumentRequirementProfileList.remove(aDocumentRequirementProfile);
                break;
            }
        }
    }

    protected void storeDocumentRequirementProfileHelper(String documentUuid, List<DocumentRequirementProfile> aDocumentRequirementprofileList) {
        List<DocumentRequirementProfile> allDocumentRequirementProfileList = getAllDocumentRequirementProfileList();
        for (DocumentRequirementProfile aDocumentRequirementProfile : allDocumentRequirementProfileList){
            if (aDocumentRequirementProfile.getDocumentRequirementEntity().getDocumentUuid().equalsIgnoreCase(documentUuid)){
                try {
                    if ((aDocumentRequirementProfile.getDocumentRequirementEntity().getQuantity() != null)
                            && (aDocumentRequirementProfile.getDocumentRequirementEntity().getQuantity() == 0))
                    {
                        getBusinessEJB().deleteEntityByUuid(G01DocumentRequirement.class, aDocumentRequirementProfile.getDocumentRequirementEntity().getDocumentUuid());
                    }else{
                        getBusinessEJB().storeDocumentRequirementProfile(aDocumentRequirementProfile);
                    }
                    RoseJsfUtils.setGlobalSuccessfulOperationMessage();
                } catch (Exception ex) {
                    //Logger.getLogger(AbstractRoseViewBean.class.getName()).log(Level.SEVERE, null, ex);
                    RoseJsfUtils.setGlobalInfoFacesMessage(RoseText.getText("SystemError") + ": " + ex.getMessage());
                }
                aDocumentRequirementprofileList.remove(aDocumentRequirementProfile);
                if ((aDocumentRequirementProfile.getDocumentRequirementEntity().getQuantity() != null)
                        && (aDocumentRequirementProfile.getDocumentRequirementEntity().getQuantity() > 0))
                {
                    aDocumentRequirementprofileList.add(aDocumentRequirementProfile);
                }
                break;
            }
        }
    }
    
    protected void storeTargetDocumentRequirementProfileHelper(DocumentRequirementProfile targetDocumentRequirementProfile, 
                                                               List<DocumentRequirementProfile> aDocumentRequirementprofileList) throws Exception
    {
        try {
            //validation
            G01DocumentRequirement aG01DocumentRequirement = targetDocumentRequirementProfile.getDocumentRequirementEntity();
            if (ZcaValidator.isNullEmpty(aG01DocumentRequirement.getServiceTagName())){
                throw new Exception(RoseText.getText("ServiceTagName") + ": " + RoseText.getText("FieldRequired_T"));
            }else{
                if (aG01DocumentRequirement.getServiceTagName().length() > 45){
                    throw new Exception(RoseText.getText("ServiceTagName") + " - " + RoseText.getText("MaxLengthRequired_T") + ": 45");
                }
            }
            if (ZcaValidator.isNotNullEmpty(aG01DocumentRequirement.getDescription())){
                if (aG01DocumentRequirement.getDescription().length() > 450){
                    throw new Exception(RoseText.getText("Description") + " - " + RoseText.getText("MaxLengthRequired_T") + ": 450");
                }
            }
            
            //store document requirement
            if ((aG01DocumentRequirement.getQuantity() != null)
                    && (aG01DocumentRequirement.getQuantity() > 0))
            {
                getBusinessEJB().storeDocumentRequirementProfile(targetDocumentRequirementProfile);
                aDocumentRequirementprofileList.add(targetDocumentRequirementProfile);
            }
//            //store service tag
//            G01ServiceTag aG01ServiceTag = new G01ServiceTag();
//            aG01ServiceTag.setEntityType(aG01DocumentRequirement.getEntityType());
//            aG01ServiceTag.setFileDemanded(aG01DocumentRequirement.getFileDemanded());
//            aG01ServiceTag.setServiceTagName(aG01DocumentRequirement.getServiceTagName());
//            aG01ServiceTag.setServiceTagUuid(aG01DocumentRequirement.getServiceTagUuid());
//            aG01ServiceTag.setDefaultUnitPrice(aG01DocumentRequirement.getUnitPrice());
//            aG01ServiceTag.setDescription(aG01DocumentRequirement.getDescription());
//            getBusinessEJB().storeEntityByUuid(G01ServiceTag.class, aG01ServiceTag, aG01ServiceTag.getServiceTagUuid(), 
//                    G01DataUpdaterFactory.getSingleton().getG01ServiceTagUpdater());
//            
//            getAllDocumentRequirementProfileList().add(targetDocumentRequirementProfile);
//            
//            Collections.sort(getAllDocumentRequirementProfileList(), (DocumentRequirementProfile o1, DocumentRequirementProfile o2) -> {
//                try{
//                    return o1.getDocumentRequirementEntity().getServiceTagName().compareToIgnoreCase(o1.getDocumentRequirementEntity().getServiceTagName());
//                }catch (Exception ex){
//                    return 0;
//                }
//            });
            
            RoseJsfUtils.setGlobalSuccessfulOperationMessage();
        } catch (Exception ex) {
            //Logger.getLogger(AbstractRoseViewBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalInfoFacesMessage(RoseText.getText("SystemError") + ": " + ex.getMessage());
        }
    }

    /**
     * All the available document-rquirement-profiles for the target, e.g. taxpayer-case or taxcorp-case. Some target may 
     * have no any requirment. Employees may change quality of G01ArchivedDocument to define if the target is demanded to 
     * provide the corresponding documents.
     * @param entityUuid
     * @param entityType
     * @return 
     */
    protected List<DocumentRequirementProfile> constructAllDocumentRequirementProfileList(String entityUuid, GardenEntityType entityType) {
        //initialize current required document profiles from the database first for entityUuid
        List<DocumentRequirementProfile> result = getBusinessEJB().findRoseDocumentRequirementProfileListByFeaturedField("entityUuid", 
                                                                                entityUuid, "G01DocumentRequirement.findByEntityUuid");
        //prepare filter structure 
        HashSet<String> tagUuidSet = new HashSet<>();
        for (DocumentRequirementProfile aDocumentRequirementProfile : result){
            tagUuidSet.add(aDocumentRequirementProfile.getDocumentRequirementEntity().getServiceTagUuid());
        }
        //prepare service tags by the help of tagUuidSet
        List<G01ServiceTag> allTags = getBusinessEJB().findAll(G01ServiceTag.class);
        List<G01ServiceTag> otherTags = new ArrayList<>();
        for (G01ServiceTag tag : allTags){
            if (!tagUuidSet.contains(tag.getServiceTagUuid())){
                if (tag.getEntityType().equalsIgnoreCase(entityType.name())){
                    otherTags.add(tag);
                }
            }
        }
        //then, initialize all the other required document profiles according to g01_service_tag in case 
        for (G01ServiceTag tag : otherTags){
            result.add(createDocumentRequirementProfileInstance(tag, entityUuid));
        }
        if (!result.isEmpty()){
            Collections.sort(result, (DocumentRequirementProfile o1, DocumentRequirementProfile o2) -> {
                try{
                    return o1.getDocumentRequirementEntity().getServiceTagName().compareTo(o2.getDocumentRequirementEntity().getServiceTagName());
                }catch(Exception ex){
                    return 0;
                }
            });
        }
        return result;
    }

    private DocumentRequirementProfile createDocumentRequirementProfileInstance(G01ServiceTag tag, String entityUuid) {
        G01DocumentRequirement docReq = new G01DocumentRequirement();
        docReq.setDocumentUuid(GardenData.generateUUIDString());
        docReq.setDescription(tag.getDescription());
        docReq.setEntityType(tag.getEntityType());
        docReq.setEntityUuid(entityUuid);
        docReq.setFileDemanded(tag.getFileDemanded());
        docReq.setServiceTagName(tag.getServiceTagName());
        docReq.setServiceTagUuid(tag.getServiceTagUuid());
        docReq.setUnitPrice(tag.getDefaultUnitPrice());
        
        DocumentRequirementProfile aDocumentRequirementProfile = new DocumentRequirementProfile();
        aDocumentRequirementProfile.setDocumentRequirementEntity(docReq);
        aDocumentRequirementProfile.setBrandNew(true);
        
        return aDocumentRequirementProfile;
    }
    
    @Override
    public void setupTargetDownloadedFile(String archivedDocumentUuid){
        this.targetArchivedDocumentUuidForDownload = archivedDocumentUuid;
    }
    
    /**
     * Support downloading a specific archived document 
     * @return 
     */
    @Override
    public StreamedContent getDownloadedArchivedDocument() {
        StreamedContent targetDownloadedFile = null;
        if (ZcaValidator.isNotNullEmpty(targetArchivedDocumentUuidForDownload)){
            try {
                G01ArchivedDocument doc = getRuntimeEJB().findEntityByUuid(G01ArchivedDocument.class, targetArchivedDocumentUuidForDownload);
                if (doc == null){
                    RoseJsfUtils.setGlobalWarningFacesMessage(RoseText.getText("NoArchivedDoc"));
                }else{
                    File initialFile = new File(getRoseSettings().getArchivedFileLocation() + File.separator + doc.getFileName());
                    if (ZcaNio.isValidFile(initialFile)){
                        InputStream stream = new FileInputStream(initialFile);
                        targetDownloadedFile = new DefaultStreamedContent(stream, 
                                                                          new MimetypesFileTypeMap().getContentType(initialFile), 
                                                                          doc.getFileName());
                    }else{
                        RoseJsfUtils.setGlobalWarningFacesMessage(RoseText.getText("NoArchivedDoc"));
                    }
                }
            } catch (Exception ex) {
                RoseJsfUtils.setGlobalWarningFacesMessage(ex.getMessage());
                targetDownloadedFile = null;
            }
        }
        return targetDownloadedFile;
    }

    @Override
    public void switchDocumentRequirementProfileListPanel() {
        setDocumentRequirementProfileListSetupDemanded(!isDocumentRequirementProfileListSetupDemanded());
    }

    @Override
    public boolean isDocumentRequirementProfileListSetupDemanded() {
        return documentRequirementProfileListSetupDemanded;
    }

    public void setDocumentRequirementProfileListSetupDemanded(boolean documentRequirementProfileListSetupDemanded) {
        this.documentRequirementProfileListSetupDemanded = documentRequirementProfileListSetupDemanded;
    }
    
    public void deleteTaxcorpCaseRecords(String taxcorpCaseUuid){
        try {
            GardenEntityStatus entityStatus;
            if (getRoseUserSession().isEmployed()){
                entityStatus = GardenEntityStatus.DELETED_BY_AGENT;
            }else{
                entityStatus = GardenEntityStatus.DELETED_BY_CUSTOMER;
            }
            getTaxcorpEJB().markTaxcorpCaseRecordsEntityStatus(taxcorpCaseUuid, entityStatus);
            //refresh...
            getRoseUserSession().getHistoricalTaxcorpCaseProfileStorage().clear();
        } catch (ZcaEntityValidationException | IllegalStateException | SecurityException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException ex) {
            Logger.getLogger(TaxcorpCaseBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalInfoFacesMessage(RoseText.getText("SystemError") + ": " + ex.getMessage());
            return;
        }
        RoseJsfUtils.setGlobalSuccessfulOperationMessage();
    }
    
    /**
     * 
     * @param taxpayerCaseUuid 
     */
    public void deleteTaxpayerCaseRecords(String taxpayerCaseUuid){
        try {
            GardenEntityStatus entityStatus;
            if (getRoseUserSession().isEmployed()){
                entityStatus = GardenEntityStatus.DELETED_BY_AGENT;
            }else{
                entityStatus = GardenEntityStatus.DELETED_BY_CUSTOMER;
            }
            getTaxpayerEJB().markTaxpayerCaseRecordsEntityStatus(taxpayerCaseUuid, entityStatus);
            //refresh...
            getRoseUserSession().getHistoricalTaxpayerCaseProfileStorage().clear();
        } catch (ZcaEntityValidationException | IllegalStateException | SecurityException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException ex) {
            Logger.getLogger(TaxpayerCaseBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalInfoFacesMessage(RoseText.getText("SystemError") + ": " + ex.getMessage());
            return;
        }
        RoseJsfUtils.setGlobalSuccessfulOperationMessage();
    }
    
}
