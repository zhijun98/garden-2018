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

package com.zcomapproach.garden.peony.email.data;

import com.zcomapproach.garden.email.GardenEmailBox;
import com.zcomapproach.garden.email.GardenEmailFolder;
import com.zcomapproach.garden.email.GardenEmailMessage;
import com.zcomapproach.garden.peony.view.data.PeonyTreeItemData;
import com.zcomapproach.garden.persistence.peony.PeonyEmailTag;
import com.zcomapproach.garden.persistence.peony.PeonyMemo;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmail;
import java.util.Objects;

/**
 *
 * @author zhijun98
 */
public class PeonyEmailTreeItemData extends PeonyTreeItemData{
    public static enum EmailItemType {ROOT, FOLDER, EMAIL, ATTACHEMNT, TAG, NOTE};

    /**
     * if Object::treeItemData is PeonyOfflineEmail, this optional field hold its associated gardenEmailMessage
     */
    private PeonyOfflineEmail associatedPeonyOfflineEmail;
    
    private EmailItemType emailItemType;
    
    public PeonyEmailTreeItemData(Object treeItemData, EmailItemType emailItemType) {
        super(treeItemData, PeonyTreeItemData.Status.UNKNOWN);
        this.emailItemType = emailItemType;
    }

    public EmailItemType getEmailItemType() {
        return emailItemType;
    }

    public void setEmailItemType(EmailItemType emailItemType) {
        this.emailItemType = emailItemType;
    }

    public PeonyOfflineEmail getAssociatedPeonyOfflineEmail() {
        return associatedPeonyOfflineEmail;
    }

    public void setAssociatedPeonyOfflineEmail(PeonyOfflineEmail associatedPeonyOfflineEmail) {
        this.associatedPeonyOfflineEmail = associatedPeonyOfflineEmail;
    }

    public PeonyEmailTag emitPeonyEmailTag() {
        if (getTreeItemData() instanceof PeonyEmailTag){
            return (PeonyEmailTag)getTreeItemData();
        }
        return null;
    }

    public PeonyMemo emitPeonyMemo() {
        if (getTreeItemData() instanceof PeonyMemo){
            return (PeonyMemo)getTreeItemData();
        }
        return null;
    }

    /**
     * 
     * @return - If this data is not PeonyEmailMessage, NULL returned
     */
    public GardenEmailMessage emitPeonyEmailMessage(){
        if (getTreeItemData() instanceof GardenEmailMessage){
            return (GardenEmailMessage)getTreeItemData();
        }
        return null;
    }
    
    /**
     * This method helps to sort messages in the TreeView
     * if its holded data is not a PeonyEmailMessage, it returns a negative number
     * @return 
     */
    public String getSortingEmailMessageUid(){
        GardenEmailMessage aGardenEmailMessage = emitPeonyEmailMessage();
        if (aGardenEmailMessage != null){
            return aGardenEmailMessage.getEmailMsgUid();
        }
        return "";
    }
    
    public String getUuid(){
        GardenEmailMessage aGardenEmailMessage = emitPeonyEmailMessage();
        Object obj = getTreeItemData();
        if (obj == null){
            return "";
        }
        if (aGardenEmailMessage != null){
            return aGardenEmailMessage.getEmailMsgUid();
        }else if (obj instanceof GardenEmailFolder){
            return ((GardenEmailFolder)obj).getFolderName();
        }else if (obj instanceof GardenEmailBox){
            return ((GardenEmailBox)obj).getEmailBoxTitle();
        }else if (obj instanceof PeonyEmailTag){
            return ((PeonyEmailTag)obj).getEmailTag().getTagUuid();
        }else if (obj instanceof PeonyMemo){
            return ((PeonyMemo)obj).getMemo().getMemoUuid();
        }
        return obj.toString();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.getTreeItemData());
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
        final PeonyEmailTreeItemData other = (PeonyEmailTreeItemData) obj;
        return Objects.equals(this.getTreeItemData(), other.getTreeItemData());
    }

    @Override
    public String toString() {
        GardenEmailMessage aGardenEmailMessage = emitPeonyEmailMessage();
        Object obj = getTreeItemData();
        if (aGardenEmailMessage != null){
            return aGardenEmailMessage.retrieveEmailHeadline();
        }else if (obj instanceof GardenEmailFolder){
            return ((GardenEmailFolder)obj).getFolderName() + " ("+((GardenEmailFolder)obj).getSerializedMessageCount()+")";
        }else if (obj instanceof GardenEmailBox){
            return ((GardenEmailBox)obj).getEmailBoxTitle();
        }else if (obj instanceof PeonyEmailTag){
            return ((PeonyEmailTag)obj).getDescriptiveText();
        }else if (obj instanceof PeonyMemo){
            return ((PeonyMemo)obj).getBriefPresentativeText();
        }else if (obj instanceof String){
            return (String)obj;
        }
        return "PeonyEmailTreeItemData{" + "getTreeItemData()=" + obj + '}';
    }
}
