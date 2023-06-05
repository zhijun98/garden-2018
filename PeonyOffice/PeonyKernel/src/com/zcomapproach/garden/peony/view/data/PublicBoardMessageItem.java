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

import com.zcomapproach.garden.persistence.peony.IPeonyBoardItemData;
import java.util.Objects;

/**
 *
 * @author zhijun98
 */
public class PublicBoardMessageItem {

    private final IPeonyBoardItemData treeItemData;
    
    private String presentativeText = "Public Message Board";
    
    private final boolean topicItem;
    private boolean rootItem;

    public PublicBoardMessageItem(String presentativeText, boolean topicItem) {
        this.presentativeText = presentativeText;
        this.treeItemData  = null;
        this.topicItem = topicItem;
    }

    public PublicBoardMessageItem(IPeonyBoardItemData treeItemData, boolean topicItem) {
        this.treeItemData = treeItemData;
        this.topicItem = topicItem;
    }

    public boolean isTopicItem() {
        return topicItem;
    }

    public boolean isRootItem() {
        return rootItem;
    }

    public void setRootItem(boolean rootItem) {
        this.rootItem = rootItem;
    }

    public IPeonyBoardItemData getTreeItemData() {
        return treeItemData;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.treeItemData);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PublicBoardMessageItem other = (PublicBoardMessageItem) obj;
        return Objects.equals(this.treeItemData, other.treeItemData);
    }

    public String getPresentativeText() {
        return presentativeText;
    }

    public void setPresentativeText(String presentativeText) {
        this.presentativeText = presentativeText;
    }

    @Override
    public String toString() {
//////        if (treeItemData instanceof String){
//////            return treeItemData.toString();
//////        }else if (treeItemData instanceof PeonyMemo){
//////            return ((PeonyMemo)treeItemData).getPresentativeText();
//////        }
//////        return "Public Board";
        if (treeItemData == null){
            return getPresentativeText();
        }else{
            return treeItemData.getPresentativeText();
        }
    }

}
