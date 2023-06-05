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

package com.zcomapproach.garden.peony.kernel;

import com.zcomapproach.garden.peony.kernel.services.PeonyKernelService;
import com.zcomapproach.garden.peony.view.dialogs.PeonyEmployeePickerDialog;
import com.zcomapproach.garden.peony.view.dialogs.PeonyTaxCasePickerDialog;
import com.zcomapproach.garden.peony.view.dialogs.PeriodSelectionDialog;
import com.zcomapproach.garden.peony.view.dialogs.TaxFilingRecordDeletionDialog;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import java.util.Date;
import java.util.List;
import javax.swing.SwingUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author zhijun98
 */
@ServiceProvider(service = PeonyKernelService.class)
public class PeonyKernelServiceProvider extends PeonyServiceProvider implements PeonyKernelService{

    @Override
    public void closeService() {
        
    }

    @Override
    public void displayTaxFilingRecordDeletionDialog(final String dialogTitle, final String taxCaseUuid, final List<PeonyFaceEventListener> peonyFaceEventListeners) {
        if (SwingUtilities.isEventDispatchThread()){
            displayTaxFilingRecordDeletionDialogHelper(dialogTitle, taxCaseUuid, peonyFaceEventListeners);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    displayTaxFilingRecordDeletionDialogHelper(dialogTitle, taxCaseUuid, peonyFaceEventListeners);
                }
            });
        }
    }
    
    private void displayTaxFilingRecordDeletionDialogHelper(final String dialogTitle, final String taxCaseUuid, final List<PeonyFaceEventListener> peonyFaceEventListeners) {
        TaxFilingRecordDeletionDialog aTaxFilingRecordDeletionDialog = new TaxFilingRecordDeletionDialog(null, true);
        aTaxFilingRecordDeletionDialog.addPeonyFaceEventListenerList(peonyFaceEventListeners);
        aTaxFilingRecordDeletionDialog.launchTaxFilingRecordDeletionDialog(dialogTitle, taxCaseUuid);
    }

    @Override
    public void displayPeonyTaxCasePickerDialog(final String dialogTitle, final List<PeonyFaceEventListener> peonyFaceEventListeners) {
        if (SwingUtilities.isEventDispatchThread()){
            displayPeonyTaxCasePickerDialogHelper(dialogTitle, peonyFaceEventListeners);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    displayPeonyTaxCasePickerDialogHelper(dialogTitle, peonyFaceEventListeners);
                }
            });
        }
    }
    
    private void displayPeonyTaxCasePickerDialogHelper(String dialogTitle, List<PeonyFaceEventListener> peonyFaceEventListeners) {
        PeonyTaxCasePickerDialog aPeriodSelectionDialog = new PeonyTaxCasePickerDialog(null, true);
        aPeriodSelectionDialog.addPeonyFaceEventListenerList(peonyFaceEventListeners);
        aPeriodSelectionDialog.launchPeonyDialog(dialogTitle);
    }

    @Override
    public void displayPeonyEmployeePickerDialog(final String dialogTitle, final List<PeonyFaceEventListener> peonyFaceEventListeners) {
        if (SwingUtilities.isEventDispatchThread()){
            displayPeonyEmployeePickerDialogHelper(dialogTitle, peonyFaceEventListeners);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    displayPeonyEmployeePickerDialogHelper(dialogTitle, peonyFaceEventListeners);
                }
            });
        }
    }
    
    private void displayPeonyEmployeePickerDialogHelper(String dialogTitle, List<PeonyFaceEventListener> peonyFaceEventListeners) {
        PeonyEmployeePickerDialog aPeriodSelectionDialog = new PeonyEmployeePickerDialog(null, true);
        aPeriodSelectionDialog.addPeonyFaceEventListenerList(peonyFaceEventListeners);
        aPeriodSelectionDialog.launchPeonyDialog(dialogTitle);
    }

    @Override
    public void displayPeriodSelectionDialog(final String dialogTitle, final List<PeonyFaceEventListener> peonyFaceEventListeners, final Date fromDate, final Date toDate) {
        if (SwingUtilities.isEventDispatchThread()){
            displayPeriodSelectionDialogHelper(dialogTitle, peonyFaceEventListeners, fromDate, toDate);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    displayPeriodSelectionDialogHelper(dialogTitle, peonyFaceEventListeners, fromDate, toDate);
                }
            });
        }
    }
    private void displayPeriodSelectionDialogHelper(final String dialogTitle, final List<PeonyFaceEventListener> peonyFaceEventListeners, final Date fromDate, final Date toDate) {
        PeriodSelectionDialog aPeriodSelectionDialog = new PeriodSelectionDialog(null, true);
        aPeriodSelectionDialog.addPeonyFaceEventListenerList(peonyFaceEventListeners);
        aPeriodSelectionDialog.launchPeriodSelectionDialog(dialogTitle, fromDate, toDate);
    }

}
