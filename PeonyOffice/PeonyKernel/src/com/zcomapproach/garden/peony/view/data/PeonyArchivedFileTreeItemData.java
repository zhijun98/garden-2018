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

package com.zcomapproach.garden.peony.view.data;

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.persistence.peony.PeonyArchivedFile;
import java.io.File;

/**
 *
 * @author zhijun98
 */
public class PeonyArchivedFileTreeItemData extends PeonyTreeItemData {
    /**
     * Item-data's text displayed on the file-system-view, which may be face-folder-name 
     * or face-file-name which came from peonyArchivedFile
     */
    private final String treeItemRepresentativeText;
    /**
     * The entity record of the file
     */
    private final PeonyArchivedFile peonyArchivedFile;
    /**
     * optional: the path of the dragged file which is dragged-and-dropped to be archived
     */
    private File originalFileObjectForArchive;
    
    /**
     * Case 1 - Root: treeItemRepresentativeText = null, peonyArchivedFile = null
     * Case 2 - File: treeItemRepresentativeText = null, peonyArchivedFile = object
     * Case 3 - Folder: treeItemRepresentativeText = name, peonyArchivedFile = null
     * 
     * @param treeItemRepresentativeText
     * @param peonyArchivedFile 
     */
    public PeonyArchivedFileTreeItemData(String treeItemRepresentativeText, PeonyArchivedFile peonyArchivedFile, PeonyTreeItemData.Status status) {
        //super(((peonyArchivedFile == null) ? (new PeonyArchivedFile()) : peonyArchivedFile));   //Object treeItemData;
        super(peonyArchivedFile, status);
        if (ZcaValidator.isNullEmpty(treeItemRepresentativeText)){
            if (peonyArchivedFile == null){
                this.treeItemRepresentativeText = "Peony Archived Files";
            }else{
                if (ZcaValidator.isNullEmpty(peonyArchivedFile.getArchivedFile().getFileFaceName())){
                    this.treeItemRepresentativeText = "UnknownFileName";
                }else{
                    this.treeItemRepresentativeText = peonyArchivedFile.getArchivedFile().getFileFaceName();
                }
            }
        }else{
            this.treeItemRepresentativeText = treeItemRepresentativeText;
        }
        this.peonyArchivedFile = (PeonyArchivedFile)super.getTreeItemData();
        
    }

    public File getOriginalFileObjectForArchive() {
        return originalFileObjectForArchive;
    }

    public void setOriginalFileObjectForArchive(File originalFileObjectForArchive) {
        this.originalFileObjectForArchive = originalFileObjectForArchive;
    }

    public PeonyArchivedFile getPeonyArchivedFile() {
        return peonyArchivedFile;
    }

    public String getTreeItemRepresentativeText() {
        return treeItemRepresentativeText;
    }

    @Override
    public String toString() {
        return treeItemRepresentativeText;
    }

}
