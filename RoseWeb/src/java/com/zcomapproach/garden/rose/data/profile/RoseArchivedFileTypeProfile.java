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

import com.zcomapproach.garden.persistence.constant.GardenArchivedFileType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zhijun98
 */
public class RoseArchivedFileTypeProfile extends AbstractRoseProfile{
    
    private GardenArchivedFileType archivedDocumentType;
    private List<RoseArchivedDocumentProfile> archivedDocumentProfileList;

    public RoseArchivedFileTypeProfile() {
        this.archivedDocumentType = GardenArchivedFileType.UNKNOWN;
        this.archivedDocumentProfileList = new ArrayList<>();
    }

    public GardenArchivedFileType getArchivedDocumentType() {
        if (archivedDocumentType == null){
            archivedDocumentType = GardenArchivedFileType.UNKNOWN;
        }
        return archivedDocumentType;
    }

    public void setArchivedDocumentType(GardenArchivedFileType archivedDocumentType) {
        this.archivedDocumentType = archivedDocumentType;
    }

    public List<RoseArchivedDocumentProfile> getArchivedDocumentProfileList() {
        if ( this.archivedDocumentProfileList == null){
            this.archivedDocumentProfileList = new ArrayList<>();
        }
        return archivedDocumentProfileList;
    }

    public void setArchivedDocumentProfileList(List<RoseArchivedDocumentProfile> archivedDocumentProfileList) {
        this.archivedDocumentProfileList = archivedDocumentProfileList;
    }

    @Override
    public String getProfileName() {
        return  getArchivedDocumentType().value();
    }

    @Override
    public String getProfileDescriptiveName() {
        return  getArchivedDocumentType().value() + " - total: " + getArchivedDocumentProfileList().size();
    }

    @Override
    protected String getProfileUuid() {
        return  getArchivedDocumentType().name();
    }

}
