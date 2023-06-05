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

package com.zcomapproach.garden.peony.taxcorp.dialogs;

import com.zcomapproach.garden.peony.taxcorp.controllers.TaxcorpProfileController;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.dialogs.PeonyFaceDialog;
import com.zcomapproach.garden.persistence.peony.PeonyTaxcorpCase;
import java.awt.Frame;

/**
 *
 * @author zhijun98
 */
public class PeonyTaxcorpCaseDialog extends PeonyFaceDialog{
    
    private PeonyTaxcorpCase targetTaxcorpCase;

    public PeonyTaxcorpCaseDialog(Frame parent, boolean modal) {
        super(parent, modal);
    }

    @Override
    protected PeonyFaceController createPeonyFaceController() {
        if (getPeonyFaceController() == null){
            TaxcorpProfileController aPeonyFaceController =  new TaxcorpProfileController(targetTaxcorpCase);
            aPeonyFaceController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
            return aPeonyFaceController;
        }
        return getPeonyFaceController();
    }

    /**
     * this method disabled users to call directly from this dialog instance
     */
    @Override
    public void launchPeonyDialog(final String dialogTitle) {
        throw new UnsupportedOperationException("Not supported for this dialog instance.");
    }

    public void launchPeonyTaxcorpCaseDialog(final String dialogTitle, final PeonyTaxcorpCase targetTaxcorpCase)
    {
        this.targetTaxcorpCase = targetTaxcorpCase;
        super.launchPeonyDialog(dialogTitle);
    }

}
