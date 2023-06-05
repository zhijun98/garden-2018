/*
 * Copyright 2017 ZComApproach Inc.
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

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.persistence.entity.G01ArchivedDocument;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.commons.ZcaCalendar;

/**
 *
 * @author zhijun98
 */
public class RoseArchivedDocumentProfile extends AbstractRoseEntityProfile{
    
    private G01ArchivedDocument archivedDocumentEntity;

    public RoseArchivedDocumentProfile() {
        archivedDocumentEntity = new G01ArchivedDocument();
    }

    public RoseArchivedDocumentProfile(G01ArchivedDocument archivedDocumentEntity) {
        this.archivedDocumentEntity = archivedDocumentEntity;
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof RoseArchivedDocumentProfile)){
            return;
        }
        RoseArchivedDocumentProfile srcRoseArchivedDocumentProfile = (RoseArchivedDocumentProfile)srcProfile;
        //archivedDocumentEntity
        G01DataUpdaterFactory.getSingleton().getG01ArchivedDocumentUpdater().cloneEntity(srcRoseArchivedDocumentProfile.getArchivedDocumentEntity(), 
                                                                                            this.getArchivedDocumentEntity());
    }
    
    public String getFileCustomName(){
        if (archivedDocumentEntity == null){
            return "";
        }else{
            if (ZcaValidator.isNullEmpty(archivedDocumentEntity.getFileCustomName())){
                return archivedDocumentEntity.getFileName();
            }else{
                return archivedDocumentEntity.getFileCustomName();
            }
        }
    }

    public G01ArchivedDocument getArchivedDocumentEntity() {
        return archivedDocumentEntity;
    }

    public void setArchivedDocumentEntity(G01ArchivedDocument archivedDocumentEntity) {
        this.archivedDocumentEntity = archivedDocumentEntity;
    }
    
    public String getFileTimestamp(){
        return ZcaCalendar.convertToMMddyyyy(getArchivedDocumentEntity().getFileTimestamp(), "-");
    }

    @Override
    public String getProfileName() {
        return archivedDocumentEntity.getArchivedDocumentUuid();
    }

    @Override
    public String getProfileDescriptiveName() {
        return archivedDocumentEntity.getArchivedDocumentUuid();
    }

    @Override
    protected String getProfileUuid() {
        return archivedDocumentEntity.getArchivedDocumentUuid();
    }

}
