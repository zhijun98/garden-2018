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

package com.zcomapproach.garden.peony.view.dialogs;

import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.controllers.ContactDataEntryController;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G02ContactInfo;
import java.awt.Frame;

/**
 *
 * @author zhijun98
 */
public class ContactDataEntryDialog extends PeonyFaceDialog{
    
    private G02ContactInfo targetContactInfo;
    private GardenEntityType entityType;
    private String entityUuid;
    private boolean saveBtnRequired;
    private boolean deleteBtnRequired;
    private boolean cancelBtnRequired;
    
    public ContactDataEntryDialog(Frame parent, boolean modal) {
        super(parent, modal);
    }

    /**
     * this method disabled users to call directly from this dialog instance
     */
    @Override
    public void launchPeonyDialog(final String dialogTitle) {
        throw new UnsupportedOperationException("Not supported for this dialog instance.");
    }

    public void launchContactDataEntryDialog(final String dialogTitle, 
                                            final GardenEntityType entityType, 
                                            final String entityUuid, 
                                            final G02ContactInfo targetContactInfo, 
                                            final boolean saveBtnRequired, 
                                            final boolean deleteBtnRequired, 
                                            final boolean cancelBtnRequired)
    {
        this.targetContactInfo = targetContactInfo;
        this.entityType = entityType;
        this.entityUuid = entityUuid;
        this.saveBtnRequired = saveBtnRequired;
        this.deleteBtnRequired = deleteBtnRequired;
        this.cancelBtnRequired = cancelBtnRequired;
        
        super.launchPeonyDialog(dialogTitle);
    }

    @Override
    protected PeonyFaceController createPeonyFaceController() {
        if (getPeonyFaceController() == null){
            ContactDataEntryController aPeonyFaceController =  new ContactDataEntryController(targetContactInfo, entityType, entityUuid, saveBtnRequired, deleteBtnRequired, cancelBtnRequired);
            aPeonyFaceController.addPeonyFaceEventListenerList(this.getPeonyFaceEventListenerList());
            return aPeonyFaceController;
        }
        return getPeonyFaceController();
    }
    
}
