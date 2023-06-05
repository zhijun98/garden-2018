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

package com.zcomapproach.garden.rose.data.profile;

import com.zcomapproach.garden.persistence.entity.G01DocumentRequirement;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;

/**
 *
 * @author zhijun98
 */
public class DocumentRequirementProfile extends AbstractRoseEntityProfile{
    
    private String qtyCSS;
    private G01DocumentRequirement documentRequirementEntity;

    public DocumentRequirementProfile() {
        documentRequirementEntity = new G01DocumentRequirement();
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof DocumentRequirementProfile)){
            return;
        }
        DocumentRequirementProfile srcDocumentRequirementProfile = (DocumentRequirementProfile)srcProfile;
        //archivedDocumentEntity
        G01DataUpdaterFactory.getSingleton().getG01DocumentRequirementUpdater().cloneEntity(srcDocumentRequirementProfile.getDocumentRequirementEntity(), 
                                                                                                this.getDocumentRequirementEntity());
    }
    
    public String getQtyCSS(){
        if ((documentRequirementEntity.getQuantity() == null) || (documentRequirementEntity.getQuantity() == 0)){
            qtyCSS = "width: 80px";
        }else{
            qtyCSS = "width: 80px; background-color: green; color: yellow";
        }
        return qtyCSS;
    }

    public G01DocumentRequirement getDocumentRequirementEntity() {
        return documentRequirementEntity;
    }

    public void setDocumentRequirementEntity(G01DocumentRequirement documentRequirementEntity) {
        this.documentRequirementEntity = documentRequirementEntity;
    }

    @Override
    public String getProfileName() {
        return documentRequirementEntity.getDocumentUuid();
    }

    @Override
    public String getProfileDescriptiveName() {
        return documentRequirementEntity.getDocumentUuid();
    }

    @Override
    protected String getProfileUuid() {
        return documentRequirementEntity.getDocumentUuid();
    }

}
