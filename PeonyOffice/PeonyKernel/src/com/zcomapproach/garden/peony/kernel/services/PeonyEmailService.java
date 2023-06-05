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

import com.zcomapproach.garden.email.GardenEmailMessage;
import com.zcomapproach.garden.email.data.OfflineMessageStatus;
import com.zcomapproach.garden.persistence.peony.PeonyCommAssignment;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmail;

/**
 *
 * @author zhijun98
 */
public interface PeonyEmailService extends PeonyService {

    /**
     * Launch PeonyEmailBoxTopComponent and make connection to the email server
     * @throws Exception 
     */
    public void openMailBox() throws Exception;

    public void closeMailBox() throws Exception;
    
    public void displayPeonyEmailTaskAssignmentWindow(PeonyCommAssignment aPeonyCommAssignment, boolean readOnly);

    /**
     * Display spam-rule top-component window
     */
    public void displaySpamRuleTopComponent();
    
    /**
     * Popup email-composer top-component of peonyEmailMessage
     * 
     * @param peonyEmailMessage
     * @param peonyOfflineEmail
     * @param purpose 
     */
    public void displayComposeEmailTopComponent(final GardenEmailMessage peonyEmailMessage, final PeonyOfflineEmail peonyOfflineEmail, final OfflineMessageStatus purpose);

    /**
     * According to emailTagUuid, its owner, an offline email can be found and displayed.
     * 
     * @param emailTagUuid 
     */
    public void displayOfflineEmailTopComponentByEmailTagUuid(String emailTagUuid);

    /**
     * According to emailTagUuid, display the corresponding dialog for this tag's details
     * @param entityUuid 
     */
    public void displayEmailTagDialogByTagUuid(String entityUuid);

    /**
     * According to offlineEmailUuid, its owner, an offline email can be found and displayed.
     * @param offlineEmailUuid 
     */
    public void displayOfflineEmailTopComponentByOfflineEmailUuid(String offlineEmailUuid);

}
