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

import com.zcomapproach.garden.peony.view.dialogs.PeonyFaceDialog;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.taxpayer.controllers.TaxpayerScheduleEController;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.entity.G02PersonalProperty;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import java.awt.Frame;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author zhijun98
 */
public class TaxpayerScheduleEDialog extends PeonyFaceDialog {
    
    private G02PersonalProperty personalProperty;
    private PeonyTaxpayerCase targetPeonyTaxpayerCase;
    /**
     * This is a collection of PeonyFaceEventListeners who listen to the controller 
     * for this dialog but not this dialog itself
     */
    private List<PeonyFaceEventListener> peonyFaceEventListenerList;

    public TaxpayerScheduleEDialog(Frame parent, boolean modal) {
        super(parent, modal);
    }
    
    /**
     * this method disabled users to call directly from this dialog instance
     */
    @Override
    public void launchPeonyDialog(final String dialogTitle) {
        throw new UnsupportedOperationException("Not supported for this dialog instance.");
    }

    public void initializeTaxpayerScheduleEDialog(final String dialogTitle, final PeonyTaxpayerCase targetPeonyTaxpayerCase,
                                                  final G02PersonalProperty personalProperty,
                                                  final List<PeonyFaceEventListener> peonyFaceEventListenerList)
    {
        this.targetPeonyTaxpayerCase = targetPeonyTaxpayerCase;
        this.personalProperty = personalProperty;
        this.peonyFaceEventListenerList = peonyFaceEventListenerList;
        
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

    @Override
    protected PeonyFaceController createPeonyFaceController() {
        if (getPeonyFaceController() == null){
            TaxpayerScheduleEController aPeonyFaceController =  new TaxpayerScheduleEController(personalProperty, targetPeonyTaxpayerCase, true, false, true, null);
            if (peonyFaceEventListenerList != null){
                for (PeonyFaceEventListener peonyFaceEventListener : peonyFaceEventListenerList){
                    aPeonyFaceController.addPeonyFaceEventListener(peonyFaceEventListener);
                }
            }
            return aPeonyFaceController;
        }
        return getPeonyFaceController();
    }
}
