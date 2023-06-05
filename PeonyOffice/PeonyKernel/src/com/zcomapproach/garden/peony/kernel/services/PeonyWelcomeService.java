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

import com.zcomapproach.garden.persistence.entity.G02DailyReport;
import com.zcomapproach.garden.persistence.peony.PeonyJob;

/**
 *
 * @author zhijun98
 */
public interface PeonyWelcomeService extends PeonyService {

    /**
     * Display talker's top-component window in the PeonyOffice
     */
    public void launchPeonyTalkerWindow();

    public void refreshTodayJobPaneForDailyReportUpdated(final G02DailyReport updatedReport);
    public void refreshTodayJobPaneForJobAssignmentUpdated(final PeonyJob updatedPeonyJob);
    
}
