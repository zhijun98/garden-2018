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

package com.zcomapproach.garden.rose.bean;

import com.zcomapproach.garden.email.rose.RoseEmailMessage;
import com.zcomapproach.garden.rose.bean.state.RoseEmailManager;
import com.zcomapproach.garden.rose.data.profile.EmployeeAccountProfile;
import com.zcomapproach.commons.ZcaValidator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 * Employees' Email central of Rose web
 * @author zhijun98
 */
@Named(value = "roseEmailController")
@ApplicationScoped
public class RoseEmailControllerBean extends AbstractRoseBean{
    
    /**
     * key: employee-id
     * value: employee's RoseEmailManager
     */
    private final HashMap<String, RoseEmailManager> roseEmailManagers = new HashMap<>();
    
    @PostConstruct
    public synchronized void constructEmailCentral(){
        
    }
    
    @PreDestroy
    public synchronized void destroyEmailCentral(){
        
    }

    public int getEmailBoxRefreshInterval(EmployeeAccountProfile employeeAccountProfile) {
        if ((employeeAccountProfile == null) || (ZcaValidator.isNullEmpty(employeeAccountProfile.getTargetPersonUuid()))){
            return 60;
        }
        RoseEmailManager aRoseEmailManager = null;
        synchronized (roseEmailManagers) {
            aRoseEmailManager = this.getRoseEmailManager(employeeAccountProfile);
        }
        if (aRoseEmailManager == null) {
            return 60;
        }else{
            if (aRoseEmailManager.isEmailStorageLoaded()){
                return 30;
            }else{
                return 2;
            }
        }
    }

    public void moveEmailIntoGarbage(EmployeeAccountProfile employeeAccountProfile, long msgUid) {
        if (employeeAccountProfile == null){
            return;
        }
        RoseEmailManager aRoseEmailManager = null;
        synchronized (roseEmailManagers) {
            aRoseEmailManager = this.getRoseEmailManager(employeeAccountProfile);
        }
        if (aRoseEmailManager != null) {
            aRoseEmailManager.moveEmailIntoGarbage(msgUid);
        }
    }

    public void markGarbageMessageForDeletion(EmployeeAccountProfile employeeAccountProfile, long msgUid) {
        if (employeeAccountProfile == null){
            return;
        }
        RoseEmailManager aRoseEmailManager = null;
        synchronized (roseEmailManagers) {
            aRoseEmailManager = roseEmailManagers.get(employeeAccountProfile.getTargetPersonUuid());
        }
        if (aRoseEmailManager != null) {
            aRoseEmailManager.markMessageForDeletion(msgUid);
        }
    }

    public void markAllGarbageMessagesForDeletion(EmployeeAccountProfile employeeAccountProfile) {
        RoseEmailManager aRoseEmailManager = null;
        synchronized (roseEmailManagers) {
            aRoseEmailManager = roseEmailManagers.get(employeeAccountProfile.getTargetPersonUuid());
        }
        if (aRoseEmailManager != null) {
            aRoseEmailManager.markAllGarbageMessagesForDeletion();
        }
    }

    public void rollbackEmailToInbox(EmployeeAccountProfile employeeAccountProfile, long msgUid) {
        if (employeeAccountProfile == null){
            return;
        }
        RoseEmailManager aRoseEmailManager = null;
        synchronized (roseEmailManagers) {
            aRoseEmailManager = this.getRoseEmailManager(employeeAccountProfile);
        }
        if (aRoseEmailManager != null) {
            aRoseEmailManager.rollbackEmailToInbox(msgUid);
        }
    }

    public boolean isEmailStorageLoaded(EmployeeAccountProfile employeeAccountProfile) {
        if (employeeAccountProfile == null){
            return false;
        }
        RoseEmailManager aRoseEmailManager = this.getRoseEmailManager(employeeAccountProfile);
        if (aRoseEmailManager == null){
            return false;
        }
        return aRoseEmailManager.isEmailStorageLoaded();
    }

    public List<RoseEmailMessage> getRoseInboxEmailList(EmployeeAccountProfile employeeAccountProfile) {
        if (employeeAccountProfile == null){
            return new ArrayList<>();
        }
        RoseEmailManager aRoseEmailManager = this.getRoseEmailManager(employeeAccountProfile);
        if (aRoseEmailManager == null){
            return new ArrayList<>();
        }
        return aRoseEmailManager.getRoseInBoxMessageList();
    }

    public List<RoseEmailMessage> getRoseGarbageMessageList(EmployeeAccountProfile employeeAccountProfile) {
        if (employeeAccountProfile == null){
            return new ArrayList<>();
        }
        RoseEmailManager aRoseEmailManager = this.getRoseEmailManager(employeeAccountProfile);
        if (aRoseEmailManager == null){
            return new ArrayList<>();
        }
        return aRoseEmailManager.getRoseGarbageMessageList();
    }

    public List<RoseEmailMessage> getRoseSentMessageList(EmployeeAccountProfile employeeAccountProfile) {
        if (employeeAccountProfile == null){
            return new ArrayList<>();
        }
        RoseEmailManager aRoseEmailManager = this.getRoseEmailManager(employeeAccountProfile);
        if (aRoseEmailManager == null){
            return new ArrayList<>();
        }
        return aRoseEmailManager.getRoseSentMessageList();
    }

    /**
     * This method assume it has loaded the target SENT-message before. Refer to the other method:
     * getTargetSentEmailMessageFromManager(EmployeeAccountProfile employeeAccountProfile, long msgUid) 
     * @param employeeAccountProfile
     * @return 
     */
    public RoseEmailMessage getTargetSentEmailMessageFromManager(EmployeeAccountProfile employeeAccountProfile) {
        if (employeeAccountProfile == null){
            return new RoseEmailMessage();
        }
        synchronized(roseEmailManagers){
            RoseEmailManager aRoseEmailManager = roseEmailManagers.get(employeeAccountProfile.getTargetPersonUuid());
            if (aRoseEmailManager == null){
                try{
                    aRoseEmailManager = new RoseEmailManager(employeeAccountProfile, getArchivedFileLocation());
                    roseEmailManagers.put(employeeAccountProfile.getTargetPersonUuid(), aRoseEmailManager);
                }catch (Exception ex){
                    Logger.getLogger(RoseEmailControllerBean.class.getName()).log(Level.SEVERE, null, ex);
                }
                return new RoseEmailMessage();
            }
            return aRoseEmailManager.getTargetSentEmailMessage();
        }
    }

    public RoseEmailMessage getTargetSentEmailMessageFromManager(EmployeeAccountProfile employeeAccountProfile, long msgUid) {
        if ((employeeAccountProfile == null) || (msgUid <= 0)){
            return null;
        }
        synchronized(roseEmailManagers){
            RoseEmailManager aRoseEmailManager = roseEmailManagers.get(employeeAccountProfile.getTargetPersonUuid());
            if (aRoseEmailManager == null){
                try{
                    aRoseEmailManager = new RoseEmailManager(employeeAccountProfile, getArchivedFileLocation());
                    roseEmailManagers.put(employeeAccountProfile.getTargetPersonUuid(), aRoseEmailManager);
                }catch (Exception ex){
                    Logger.getLogger(RoseEmailControllerBean.class.getName()).log(Level.SEVERE, null, ex);
                }
                return new RoseEmailMessage();
            }else{
                RoseEmailMessage aRoseEmailMessage = aRoseEmailManager.derializeSentEmailMessage(msgUid);
                aRoseEmailManager.setTargetSentEmailMessage(aRoseEmailMessage);
                return aRoseEmailManager.getTargetSentEmailMessage();
            }
        }
    }

    public RoseEmailMessage getTargetEmailMessageFromManager(EmployeeAccountProfile employeeAccountProfile) {
        if (employeeAccountProfile == null){
            return null;
        }
        synchronized(roseEmailManagers){
            RoseEmailManager aRoseEmailManager = roseEmailManagers.get(employeeAccountProfile.getTargetPersonUuid());
            if (aRoseEmailManager == null){
                try{
                    aRoseEmailManager = new RoseEmailManager(employeeAccountProfile, getArchivedFileLocation());
                    roseEmailManagers.put(employeeAccountProfile.getTargetPersonUuid(), aRoseEmailManager);
                }catch (Exception ex){
                    Logger.getLogger(RoseEmailControllerBean.class.getName()).log(Level.SEVERE, null, ex);
                }
                return new RoseEmailMessage();
            }
            return aRoseEmailManager.getTargetEmailMessage();
        }
    }
    
    private RoseEmailManager getRoseEmailManager(EmployeeAccountProfile employeeAccountProfile){
        if ((employeeAccountProfile == null) || (ZcaValidator.isNullEmpty(employeeAccountProfile.getTargetPersonUuid()))){
            return null;
        }
        synchronized(roseEmailManagers){
            RoseEmailManager aRoseEmailManager = roseEmailManagers.get(employeeAccountProfile.getTargetPersonUuid());
            if (aRoseEmailManager == null){
                try{
                    aRoseEmailManager = new RoseEmailManager(employeeAccountProfile, getArchivedFileLocation());
                    roseEmailManagers.put(employeeAccountProfile.getTargetPersonUuid(), aRoseEmailManager);
                }catch (Exception ex){
                    Logger.getLogger(RoseEmailControllerBean.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return aRoseEmailManager;
        }
    }

    public boolean markEmailMessageForContentLoading(EmployeeAccountProfile employeeAccountProfile, long msgUid) {
        if (employeeAccountProfile == null){
            return false;
        }
        
        RoseEmailManager aRoseEmailManager = null;
        synchronized(roseEmailManagers){
            aRoseEmailManager = this.getRoseEmailManager(employeeAccountProfile);
            if (aRoseEmailManager != null){
                return aRoseEmailManager.markTargetEmailMessageForContentLoading(msgUid);
            }
        }
        return false;
    }

    public void launchEmailStorageForEmployee(EmployeeAccountProfile employeeAccountProfile) {
        if ((employeeAccountProfile == null) || (ZcaValidator.isNullEmpty(employeeAccountProfile.getTargetPersonUuid()))){
            return;
        }
        synchronized(roseEmailManagers){    //for checking aRoseEmailManager.isMessageStorageLaunching()
            RoseEmailManager aRoseEmailManager = this.getRoseEmailManager(employeeAccountProfile);
            if (aRoseEmailManager != null){
                //Shut down the email storage in the case when the user session is expired
                aRoseEmailManager.shutdownEmailStorageByManager(false);
                getRuntimeEJB().loadEmailStorageAsynchronously(aRoseEmailManager);
                getRuntimeEJB().loadSentEmailsAsynchronously(aRoseEmailManager);
            }
        }
    }

    public void refreshEmailStorageForEmployee(EmployeeAccountProfile employeeAccountProfile) {
        if ((employeeAccountProfile == null) || (ZcaValidator.isNullEmpty(employeeAccountProfile.getTargetPersonUuid()))){
            return;
        }
        synchronized(roseEmailManagers){    //for checking aRoseEmailManager.isMessageStorageLaunching()
            RoseEmailManager aRoseEmailManager = this.getRoseEmailManager(employeeAccountProfile);
            if ((aRoseEmailManager != null) && (!aRoseEmailManager.isMessageStorageLaunching()) && (!aRoseEmailManager.isMessageDeleting())){
                aRoseEmailManager.setMessageStorageLaunching(true);
                aRoseEmailManager.setMessageDeleting(true);
                getRuntimeEJB().refreshEmailStorageAsynchronously(aRoseEmailManager);
                getRuntimeEJB().refreshSentEmailsAsynchronously(aRoseEmailManager);
            }
        }
    }

    public void loadTargetEmployeeEmail(EmployeeAccountProfile employeeAccountProfile) {
        if ((employeeAccountProfile == null) || (ZcaValidator.isNullEmpty(employeeAccountProfile.getTargetPersonUuid()))){
            return;
        }
        
        synchronized(roseEmailManagers){
            RoseEmailManager aRoseEmailManager = this.getRoseEmailManager(employeeAccountProfile);
            if ((aRoseEmailManager != null) && (!aRoseEmailManager.isTargetMessageContentLoading())
                    && (aRoseEmailManager.getTargetEmailMessage() != null) 
                    && (!aRoseEmailManager.isTargetEmailContentLoaded()) 
                    && (!aRoseEmailManager.isMessageMarkedForContentLoadingEmpty()))
            {
                aRoseEmailManager.setTargetMessageContentLoading(true);
                getRuntimeEJB().parseTargetEmailContentWithAttachmentsAsynchronously(aRoseEmailManager);
            }
        }
    }

    /**
     * destroy and remove employeeAccountProfile's aRoseEmailManager from this controller
     * @param employeeAccountProfile 
     */
    public void destroyRoseEmailManager(EmployeeAccountProfile employeeAccountProfile) {
        if ((employeeAccountProfile == null) || (ZcaValidator.isNullEmpty(employeeAccountProfile.getTargetPersonUuid()))){
            return;
        }
        synchronized(roseEmailManagers){    //for removing aRoseEmailManager from the roseEmailManagers storage
            RoseEmailManager aRoseEmailManager = roseEmailManagers.remove(employeeAccountProfile.getTargetPersonUuid());
            if (aRoseEmailManager != null){
                getRuntimeEJB().shutdownEmailStorageAsynchronously(aRoseEmailManager);
            }
        }
    }

    public void serializeSentEmailMessage(RoseEmailMessage aRoseEmailMessage) {
        RoseEmailManager aRoseEmailManager = getRoseEmailManager(getRoseUserSession().getTargetEmployeeAccountProfile());
        aRoseEmailManager.serializeSentEmailMessage(aRoseEmailMessage);
    }
}
