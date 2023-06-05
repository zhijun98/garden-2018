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

package com.zcomapproach.garden.peony.kernel.services;

import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import java.util.Date;
import java.util.List;

/**
 *
 * @author zhijun98
 */
public interface PeonyKernelService extends PeonyService {
    
    public void displayPeriodSelectionDialog(final String dialogTitle, final List<PeonyFaceEventListener> peonyFaceEventListeners, final Date fromDate, final Date toDate);

    public void displayTaxFilingRecordDeletionDialog(final String dialogTitle, final String taxCaseUuid, final List<PeonyFaceEventListener> peonyFaceEventListeners);
    
    public void displayPeonyEmployeePickerDialog(final String dialogTitle, final List<PeonyFaceEventListener> peonyFaceEventListeners);
    
    public void displayPeonyTaxCasePickerDialog(final String dialogTitle, final List<PeonyFaceEventListener> peonyFaceEventListeners);

}
