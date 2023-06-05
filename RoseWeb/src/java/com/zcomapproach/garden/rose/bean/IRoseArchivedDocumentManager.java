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

import com.zcomapproach.garden.rose.data.profile.DocumentRequirementProfile;
import com.zcomapproach.garden.rose.data.profile.RoseArchivedFileTypeProfile;
import java.util.List;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author zhijun98
 */
public interface IRoseArchivedDocumentManager {
    
    /**
     * Prepare the target document so as to be ready for download
     * @param archivedDocumentUuid 
     */
    public void setupTargetDownloadedFile(String archivedDocumentUuid);
    
    /**
     * Get the downloading target file itself
     * @return 
     */
    public StreamedContent getDownloadedArchivedDocument();
    
    /**
     * 
     * @return - A list of RoseArchivedDocumentProfiles which are associated with this view
     */
    public List<RoseArchivedFileTypeProfile> getRoseArchivedFileTypeProfileList();
    
    /**
     * 
     * @return - a list of document-requirement profiles for customers 
     */
    public List<DocumentRequirementProfile> getDocumentRequirementProfileList();
    
    /**
     * Change GUI part so that employees may set up demanded documents for customers
     */
    public void switchDocumentRequirementProfileListPanel();
    
    public boolean isDocumentRequirementProfileListSetupDemanded();
    
    /**
     * All the available document-rquirement-profiles for the target, e.g. taxpayer-case or taxcorp-case. Some target may 
     * have no any requirment. Employees may change quality of G01ArchivedDocument to define if the target is demanded to 
     * provide the corresponding documents.
     * @return 
     */
    public List<DocumentRequirementProfile> getAllDocumentRequirementProfileList();
    
    public void storeDocumentRequirementProfile(String documentUuid);
    
    public void deleteDocumentRequirementProfile(String documentUuid);
    
    public void saveTargetDocumentRequirementProfile();
    
    public boolean isDisplayDocumentRequirementProfileDataEntryPanelDemanded();
    public void hideDocumentRequirementProfileDataEntryPanel();
    public void displayDocumentRequirementProfileDataEntryPanelForEdit(String documentUuid);
    
    public DocumentRequirementProfile getTargetDocumentRequirementProfile();

    public void setTargetDocumentRequirementProfile(DocumentRequirementProfile targetDocumentRequirementProfile);
    
    public void storeTargetDocumentRequirementProfile();

}
