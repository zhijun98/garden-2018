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
import com.zcomapproach.garden.peony.view.controllers.LocationDataEntryController;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G02Location;
import java.awt.Frame;

/**
 *
 * @author zhijun98
 */
public class LocationDataEntryDialog extends PeonyFaceDialog {
    
    private G02Location targetLocation;
    private GardenEntityType entityType;
    private String entityUuid;
    private boolean saveBtnRequired;
    private boolean deleteBtnRequired;
    private boolean cancelBtnRequired;

    public LocationDataEntryDialog(Frame parent, boolean modal) {
        super(parent, modal);
    }
    
    /**
     * this method disabled users to call directly from this dialog instance
     */
    @Override
    public void launchPeonyDialog(final String dialogTitle) {
        throw new UnsupportedOperationException("Not supported for this dialog instance.");
    }

    public void launchAddressLocationDataEntryDialog(final String dialogTitle, 
                                                     final GardenEntityType entityType, 
                                                     final String entityUuid, 
                                                     final G02Location targetLocation, 
                                                     final boolean saveBtnRequired, 
                                                     final boolean deleteBtnRequired, 
                                                     final boolean cancelBtnRequired)
    {
        this.targetLocation = targetLocation;
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
            LocationDataEntryController aPeonyFaceController =  new LocationDataEntryController(targetLocation, entityType, entityUuid, saveBtnRequired, deleteBtnRequired, cancelBtnRequired);
            aPeonyFaceController.addPeonyFaceEventListenerList(this.getPeonyFaceEventListenerList());
            return aPeonyFaceController;
        }
        return getPeonyFaceController();
    }
    
}
