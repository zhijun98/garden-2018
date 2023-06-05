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

package com.zcomapproach.garden.peony.taxpayer.dialogs;

import com.zcomapproach.garden.peony.taxpayer.controllers.PeonyTaxpayerCaseController;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.dialogs.PeonyFaceDialog;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import java.awt.Frame;

/**
 *
 * @author zhijun98
 */
public class PeonyTaxpayerCaseDialog extends PeonyFaceDialog{
    
    private PeonyTaxpayerCase targetTaxpayerCase;

    public PeonyTaxpayerCaseDialog(Frame parent, boolean modal) {
        super(parent, modal);
    }

    @Override
    protected PeonyFaceController createPeonyFaceController() {
        if (getPeonyFaceController() == null){
            PeonyTaxpayerCaseController aPeonyFaceController =  new PeonyTaxpayerCaseController(targetTaxpayerCase);
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

    public void launchPeonyTaxpayerCaseDialog(final String dialogTitle, final PeonyTaxpayerCase targetTaxpayerCase)
    {
        this.targetTaxpayerCase = targetTaxpayerCase;
        super.launchPeonyDialog(dialogTitle);
    }

}
