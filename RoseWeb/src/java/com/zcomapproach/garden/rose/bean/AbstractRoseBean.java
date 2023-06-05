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

import com.zcomapproach.garden.rose.data.constant.BootstrapAlertSeverity;
import com.zcomapproach.garden.rose.persistence.RoseBusinessEJB;
import com.zcomapproach.garden.rose.persistence.RoseManagementEJB02;
import com.zcomapproach.garden.rose.persistence.RoseRuntimeEJB;
import com.zcomapproach.garden.rose.persistence.RoseTaxcorpEJB;
import com.zcomapproach.garden.rose.persistence.RoseTaxpayerEJB;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenEnvironment;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.inject.Inject;

/**
 * Root of Rose CDI/managed beans
 * @author zhijun98
 */
public class AbstractRoseBean implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @Inject 
    private RoseUserSessionBean roseUserSession;
    
    @Inject
    private RoseXmppTopicsBean roseXmppTopics;
    
    @EJB
    private RoseRuntimeEJB runtimeEJB;
    
    @EJB
    private RoseBusinessEJB businessEJB;
    
    @EJB
    private RoseTaxpayerEJB taxpayerEJB;
    
    @EJB
    private RoseTaxcorpEJB taxcorpEJB;
    
    @EJB
    private RoseManagementEJB02 managementEJB;
    
    /**
     * A generic purpose to display a quick message on the web
     */
    private String webMessage;
    private String webMessageSeverity;
    
    /**
     * the location for uploaded files from customers or agents
     */
    private String archivedFileLocation;

    public String getWebMessage() {
        return webMessage;
    }

    public void setWebMessage(String webMessage) {
        this.webMessage = webMessage;
    }

    public String getWebMessageSeverity() {
        if (ZcaValidator.isNullEmpty(webMessageSeverity)){
            webMessageSeverity = BootstrapAlertSeverity.INFO.value();
        }
        return webMessageSeverity;
    }

    public void setWebMessageSeverity(String webMessageSeverity) {
        this.webMessageSeverity = webMessageSeverity;
    }

    public RoseManagementEJB02 getManagementEJB() {
        return managementEJB;
    }
    
    /**
     * 
     * @param webMessage
     * @param severityLevel - null means SUCCESS;
     */
    public void setBootstrapAlertMessage(String webMessage, Level severityLevel){
        this.webMessage = webMessage;
        if (severityLevel == null){
            this.webMessageSeverity = BootstrapAlertSeverity.SUCCESS.value();
        }else if (Level.SEVERE.getName().equalsIgnoreCase(severityLevel.getName())){
            this.webMessageSeverity = BootstrapAlertSeverity.DANGER.value();
        }else if (Level.WARNING.getName().equalsIgnoreCase(severityLevel.getName())){
            this.webMessageSeverity = BootstrapAlertSeverity.WARNING.value();
        }else{
            this.webMessageSeverity = BootstrapAlertSeverity.INFO.value();
        }
    }
    
    public boolean isWebMessagePresented(){
        return ZcaValidator.isNotNullEmpty(webMessage);
    }

    public RoseUserSessionBean getRoseUserSession() {
        return roseUserSession;
    }

    public void setRoseUserSession(RoseUserSessionBean roseUserSession) {
        this.roseUserSession = roseUserSession;
    }

    public RoseXmppTopicsBean getRoseXmppTopics() {
        return roseXmppTopics;
    }

    public void setRoseXmppTopics(RoseXmppTopicsBean roseXmppTopics) {
        this.roseXmppTopics = roseXmppTopics;
    }

    public RoseRuntimeEJB getRuntimeEJB() {
        return runtimeEJB;
    }

    public RoseBusinessEJB getBusinessEJB() {
        return businessEJB;
    }

    public RoseTaxpayerEJB getTaxpayerEJB() {
        return taxpayerEJB;
    }

    public RoseTaxcorpEJB getTaxcorpEJB() {
        return taxcorpEJB;
    }
    
    /**
     * 
     * @param value - filterBy
     * @param filter - filter value input by users
     * @param locale
     * @return 
     */
    public boolean filterByContainedText(Object value, Object filter, Locale locale){
        if (filter == null){
            return true;
        }
        if (value == null){
            return  false;
        }
        return value.toString().toLowerCase().contains(filter.toString().toLowerCase());
    }
    
    public boolean filterByDate(Object value, Object filter, Locale locale){
        if (filter == null){
            return true;
        }
        if (value == null){
            return  false;
        }
        return value.toString().toLowerCase().contains(filter.toString().toLowerCase());
    }
    
    /**
     * This is the central place where the archived files will be stored
     * @return
     * @throws IOException 
     */
    public String getArchivedFileLocation() throws IOException {
        if (ZcaValidator.isNullEmpty(archivedFileLocation)){
            archivedFileLocation = GardenEnvironment.retrieveGardenArchiveStorageLocation();
            
            if ((archivedFileLocation == null) || (!(new File(archivedFileLocation)).isDirectory())){
                throw new IOException("Cannot find archve folder");
            }
        }
        return archivedFileLocation;
    }
}
