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
import com.zcomapproach.garden.persistence.entity.G02DocumentTag;
import com.zcomapproach.garden.persistence.peony.PeonyArchivedFile;
import com.zcomapproach.garden.persistence.peony.PeonyDocumentTag;

/**
 *
 * @author zhijun98
 */
public class PeonyDocumentTagTreeItemData extends PeonyArchivedFileTreeItemData {

    private final PeonyDocumentTag targetPeonyDocumentTag;
    
    private String treeItemTitle;

    public PeonyDocumentTagTreeItemData(String treeItemTitle) {
        super(null, null, PeonyTreeItemData.Status.UNKNOWN);
        if (ZcaValidator.isNullEmpty(treeItemTitle)){
            treeItemTitle = "N/A";
        }
        this.treeItemTitle = treeItemTitle;
        this.targetPeonyDocumentTag = null;
    }

    public PeonyDocumentTagTreeItemData(PeonyDocumentTag targetPeonyDocumentTag, PeonyArchivedFile peonyArchivedFile, PeonyTreeItemData.Status status) {
        super(null, peonyArchivedFile, status);
        this.targetPeonyDocumentTag = targetPeonyDocumentTag;
    }

    public PeonyDocumentTag getTargetPeonyDocumentTag() {
        return targetPeonyDocumentTag;
    }

    public String getTreeItemTitle() {
        return treeItemTitle;
    }

    public void setTreeItemTitle(String treeItemTitle) {
        this.treeItemTitle = treeItemTitle;
    }
    
    public String getPeonyDocumentTagRepresentativeText() {
        if (targetPeonyDocumentTag == null){
            return treeItemTitle;
        }
        G02DocumentTag aG02DocumentTag = targetPeonyDocumentTag.getDocumentTag();
        if (aG02DocumentTag == null){
            return "N/A";
        }
        String result = "";
        if (ZcaValidator.isNotNullEmpty(aG02DocumentTag.getDocumentTagName())){
            result += aG02DocumentTag.getDocumentTagName();
        }
        if (aG02DocumentTag.getDocumentQuantity() != null){
            result += " (Qty: " + aG02DocumentTag.getDocumentQuantity() + ")";
        }
        if (ZcaValidator.isNotNullEmpty(aG02DocumentTag.getMemo())){
            result += ": " + aG02DocumentTag.getMemo();
        }
        return result.trim();
    }
    
    @Override
    public String toString() {
        return getPeonyDocumentTagRepresentativeText();
    }

}
