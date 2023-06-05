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

import com.zcomapproach.garden.peony.taxpayer.controllers.CloneHistoricalTaxpayerCaseController;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.dialogs.PeonyFaceDialog;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import java.awt.Frame;
import java.util.Date;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * When a customer starts a new year's tax-return, there were previous years' data 
 * but no data for this new year's. In this case, this dialog jump out and ask users 
 * to choose which previous year's data should be used for this new year's tax return
 * @author zhijun98
 */
public class CloneLegancyTaxpayerCaseDialog extends PeonyFaceDialog {
    
    private String expectedSsn;
    private Date expectedDeadline;
    private List<PeonyTaxpayerCase> legancyTaxReturnList;

    public CloneLegancyTaxpayerCaseDialog(Frame parent, boolean modal) {
        super(parent, modal);
    }

    @Override
    protected PeonyFaceController createPeonyFaceController() {
        if (getPeonyFaceController() == null){
            return new CloneHistoricalTaxpayerCaseController(expectedSsn, expectedDeadline, legancyTaxReturnList);
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
    
    public void launchCloneLegancyTaxpayerCaseDialog(String dialogTitle, String expectedSsn, Date expectedDeadline, List<PeonyTaxpayerCase> legancyTaxReturnList){
        this.expectedSsn = expectedSsn;
        this.expectedDeadline = expectedDeadline;
        this.legancyTaxReturnList = legancyTaxReturnList;
        
        super.launchPeonyDialog(dialogTitle);
        
        if (SwingUtilities.isEventDispatchThread()){
            setTitle(dialogTitle);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    setTitle(dialogTitle);
                }
            });
        }
    
    }
}
