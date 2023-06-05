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
import com.zcomapproach.garden.peony.view.controllers.PeriodSelectionController;
import java.awt.Frame;
import java.util.Date;

/**
 *
 * @author zhijun98
 */
public class PeriodSelectionDialog extends PeonyFaceDialog {
    
    private String dialogTitle;
    private Date fromDate;
    private Date toDate;
    
    public PeriodSelectionDialog(Frame parent, boolean modal) {
        super(parent, modal);
    }

    /**
     * this method disabled users to call directly from this dialog instance
     */
    @Override
    public void launchPeonyDialog(final String dialogTitle) {
        throw new UnsupportedOperationException("Not supported for this dialog instance.");
    }
    
    public void launchPeriodSelectionDialog(String dialogTitle, Date fromDate, Date toDate){
        this.dialogTitle = dialogTitle;
        this.fromDate = fromDate;
        this.toDate = toDate;
        
        super.launchPeonyDialog(dialogTitle);
    }

    @Override
    protected PeonyFaceController createPeonyFaceController() {
        if (getPeonyFaceController() == null){
            PeonyFaceController aPeonyFaceController = new PeriodSelectionController(dialogTitle, fromDate, toDate);
            /**
             * Listeners to the dialog also listen to the dialog's controllers
             */
            aPeonyFaceController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
            return aPeonyFaceController;
        }
        return getPeonyFaceController();
    }

}
