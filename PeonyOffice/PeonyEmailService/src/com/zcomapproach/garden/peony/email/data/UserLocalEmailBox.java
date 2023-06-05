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

package com.zcomapproach.garden.peony.email.data;

import com.zcomapproach.garden.email.GardenEmailMessage;
import com.zcomapproach.garden.email.ISpamRuleManager;
import com.zcomapproach.garden.email.OfflineEmailBox;
import com.zcomapproach.garden.email.OfflineEmailBoxListener;
import com.zcomapproach.garden.email.data.GoogleEmailProperties;
import com.zcomapproach.garden.persistence.entity.G02Employee;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmail;
import com.zcomapproach.garden.persistence.peony.data.QueryNewOfflineEmailCriteria;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.util.Duration;
import javax.mail.MessagingException;

/**
 * Current login user's offline email box from the local storage
 * @author zhijun98
 */
public class UserLocalEmailBox extends OfflineEmailBox{
    
    private final Properties emailProp = new GoogleEmailProperties();
    
    private ScheduledService emailboxScheduledLoadingService;
    
    private final List<OfflineEmailBoxListener> gardenEmailBoxListenerList = new ArrayList<>();
    
    private final ISpamRuleManager spamRuleManager;
    
    private final QueryNewOfflineEmailCriteria queryNewOfflineEmailCriteria;

    /**
     * 
     * @param currentLoginEmployee
     * @param gardenMailBoxAddess - if this null, the email-box will be defined by currentLoginEmployee's work-email
     * @param spamRuleManager 
     */
    public UserLocalEmailBox(G02Employee currentLoginEmployee, String gardenMailBoxAddess, QueryNewOfflineEmailCriteria queryNewOfflineEmailCriteria, ISpamRuleManager spamRuleManager) {
        super(currentLoginEmployee, gardenMailBoxAddess);
        this.spamRuleManager = spamRuleManager;
        this.queryNewOfflineEmailCriteria = queryNewOfflineEmailCriteria;
    }

    public void updateBatchThreshold(int batchThreshold) {
        queryNewOfflineEmailCriteria.setBatchThreshold(batchThreshold);
    }

    /**
     * 
     * @param emailBoxTitle
     * @param currentLoginEmployee
     * @param gardenMailBoxAddess - if this null, the email-box will be defined by currentLoginEmployee's work-email
     * @param spamRuleManager 
     */
    public UserLocalEmailBox(String emailBoxTitle, G02Employee currentLoginEmployee, String gardenMailBoxAddess, QueryNewOfflineEmailCriteria queryNewOfflineEmailCriteria, ISpamRuleManager spamRuleManager) {
        super(emailBoxTitle, currentLoginEmployee, gardenMailBoxAddess);
        this.spamRuleManager = spamRuleManager;
        this.queryNewOfflineEmailCriteria = queryNewOfflineEmailCriteria;
    }
    
    public void addOfflineEmailBoxListener(OfflineEmailBoxListener aOfflineEmailBoxListener) {
        synchronized(gardenEmailBoxListenerList){
            gardenEmailBoxListenerList.remove(aOfflineEmailBoxListener);
            gardenEmailBoxListenerList.add(aOfflineEmailBoxListener);
        }
    }

    public void removeOfflineEmailBoxListener(OfflineEmailBoxListener aProgressStatusListener) {
        synchronized(gardenEmailBoxListenerList){
            gardenEmailBoxListenerList.remove(aProgressStatusListener);
        }
    }

    protected void broadcastProcessingStatus(String status) {
        synchronized(gardenEmailBoxListenerList){
            for (OfflineEmailBoxListener aOfflineEmailBoxListener : gardenEmailBoxListenerList){
                aOfflineEmailBoxListener.publishProcessingStatus(status);
            }
        }
    }

    /**
     * Helper for UserLocalEmailboxScheduledLoadingService
     * @param aPeonyOfflineEmail 
     */
    void broadcastGardenEmailMessageLoaded(PeonyOfflineEmail aPeonyOfflineEmail, GardenEmailMessage aGardenEmailMessage) {
        synchronized(gardenEmailBoxListenerList){
            for (OfflineEmailBoxListener aOfflineEmailBoxListener : gardenEmailBoxListenerList){
                aOfflineEmailBoxListener.peonyOfflineEmailMessageLoaded(aPeonyOfflineEmail, aGardenEmailMessage);
            }
        }
    }
    
    @Override
    public synchronized void setClosingGardenEmailBox(boolean closingGardenEmailBox) {
        super.setClosingGardenEmailBox(closingGardenEmailBox);
        if (emailboxScheduledLoadingService != null){
            if (Platform.isFxApplicationThread()){
                emailboxScheduledLoadingService.cancel();
            }else{
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        emailboxScheduledLoadingService.cancel();
                    }
                });
            }
        }
    }
    
    /**
     * Based on LotusMaster implementation, retrieve all the loaded emails' information, 
     * try to load them from the local. If it cannot find them at the local, download 
     * them from the server and then broadcastGardenEmailMessageLoaded.
     * <p>
     * This method invokes emailBoxScheduledLoadingService which periodically loads offline 
     * emails. The frequency of launching the loading process is once per 45 seconds. If the 
     * previous process is not yet completed, the new one will be dropped.
     * @param emailSystemRootPath
     * @throws MessagingException 
     */
    @Override
    public void loadGardenEmails(final String emailSystemRootPath) throws MessagingException {
        if (emailboxScheduledLoadingService == null){
            emailboxScheduledLoadingService = UserLocalEmailboxLoadingScheduledService.getSingleton(this, queryNewOfflineEmailCriteria);
            emailboxScheduledLoadingService.setPeriod(Duration.seconds(45));
            emailboxScheduledLoadingService.start();
        }
    }
    
    @Override
    protected Properties getEmailProp() {
        //for this local user's mail box, it is used to send emails
        return emailProp;
    }
    
    public void recordGardenEmailMessageFlagChanged(GardenEmailMessage aPeonyEmailMessage) {
    
    }
    
}
