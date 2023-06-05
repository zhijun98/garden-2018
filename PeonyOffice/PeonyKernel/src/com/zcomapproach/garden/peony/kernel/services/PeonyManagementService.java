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

import com.zcomapproach.garden.peony.kernel.rest.PeonyRestClient;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.entity.G02BusinessContactor;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerInfo;
import com.zcomapproach.garden.persistence.peony.PeonyDailyReport;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.persistence.peony.PeonyEmployeeList;
import com.zcomapproach.garden.persistence.peony.PeonyLog;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author zhijun98
 */
public interface PeonyManagementService extends PeonyService {

    public PeonyRestClient getPeonyManagementRestClient();
    
    public void popupSmsDialogForBusinessContactor(G02BusinessContactor contactor);
    
    public void popupSmsDialogForBusinessContactors(List<G02BusinessContactor> contactors);
    
    public void popupSmsDialogForTaxpayers(List<G02TaxpayerInfo> taxpayerInfos);

    /**
     * if it is triggered first-time, it is time-consuming
     * @return 
     */
    public HashMap<String, PeonyEmployee> retrievePeonyEmployeeSet();
    
    public List<PeonyEmployee> retrievePeonyEmployees();
    
    public PeonyEmployeeList retrievePeonyEmployeeList();
    
    /**
     * if retrievePeonyEmployeeList() was never triggered, this method would be 
     * time-consuming at the first time.
     * @param employeeUuid
     * @return 
     */
    public PeonyEmployee retrievePeonyEmployee(String employeeUuid);

    public String retrievePeonyEmployeeFullName(String employeeUuid);

    /**
     * Launch a window or dialog to display the logged-entity in aPeonyLog
     * @param aPeonyLog 
     */
    public void launchLoggedEntityWindow(PeonyLog aPeonyLog);

    /**
     * Launch a window or doalog to display the entity that contains the logged-entity
     * @param aPeonyLog 
     */
    public void launchEntityOwnerWindow(PeonyLog aPeonyLog);
    
    public void launchEmployeeDailyReportTopComponent(PeonyEmployee targetPeonyEmployee);

    public void displayEmployeeWorkLogsTopComponent(final PeonyEmployee peonyEmployee);
    public void displayEmployeeDataEntryTopComponent(final PeonyEmployee aPeonyEmployee);

    public void displayMyDailyReportTopComponent();

    public void displayPeonyDailyReportEditor(final PeonyDailyReport aPeonyDailyReport, List<PeonyFaceEventListener> listeners);

    public void displayDailyReportHistoryByJobAssignmentUuid(final String jobAssignmentUuid);

    public String retrieveXmppAccountFullName(String xmppLoginName);

    public void refreshMyDailyReport();
}
