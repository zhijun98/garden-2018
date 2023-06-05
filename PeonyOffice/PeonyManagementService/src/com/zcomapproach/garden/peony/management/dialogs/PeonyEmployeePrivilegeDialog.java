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
package com.zcomapproach.garden.peony.management.dialogs;

import com.zcomapproach.garden.peony.view.dialogs.PeonyFaceDialog;
import com.zcomapproach.garden.peony.management.controllers.PeonyEmployeePrivilegePaneController;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;

/**
 *
 * @author zhijun98
 */
public class PeonyEmployeePrivilegeDialog extends PeonyFaceDialog {
    private PeonyEmployee targetPeonyEmployee;
    
    /**
     * Creates new form PeonyEmployeePrivilegeDialog
     */
    public PeonyEmployeePrivilegeDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }
    
    /**
     * this method disabled users to call directly from this dialog instance
     */
    @Override
    public void launchPeonyDialog(final String dialogTitle) {
        throw new UnsupportedOperationException("Not supported for this dialog instance.");
    }
    
    public void launchEmployeePrivilegeDialog(final String dialogTitle, final PeonyEmployee targetPeonyEmployee){
        this.targetPeonyEmployee = targetPeonyEmployee;
        setName("Employee: " + targetPeonyEmployee.getPeonyUserFullName());
        super.launchPeonyDialog(dialogTitle);
    }

    @Override
    protected PeonyFaceController createPeonyFaceController() {
        if (getPeonyFaceController() == null){
            return new PeonyEmployeePrivilegePaneController(targetPeonyEmployee);
        }
        return getPeonyFaceController();
    }
}
