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

import com.zcomapproach.garden.rose.RoseWebIdValue;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 * Expose RoseWebIdValue.values.
 * <p>
 * (1) add a new web-id in RoseWebIdValue.java
 * (2) expose this web-id in RoseWebIdApplicationBean.java
 * (3) implement boolean-function for this new web-id in RoseWebPageConfig.java
 * (4) go to the web page, add web-id, hook web-id's feature (e.g. collapsed), and AJAX event (e.g. onRoseToggleEvent)
 * @author zhijun98
 */
@Named(value = "roseWebID")
@ApplicationScoped
public class RoseWebIdApplicationBean extends AbstractRoseBean{
    
    public String getTaxcorpCaseCustomerProfilePanelID(){
        return RoseWebIdValue.TaxcorpCase_CustomerProfilePanel.value();
    }
    
    public String getTaxcorpCaseBasicInformationPanelID(){
        return RoseWebIdValue.TaxcorpCase_BasicInformationPanel.value();
    }
    
    public String getTaxcorpCaseContactorPanelID(){
        return RoseWebIdValue.TaxcorpCase_ContactorPanel.value();
    }
    
    public String getTaxcorpCaseUploadedArchivedDocumentPanelID(){
        return RoseWebIdValue.TaxcorpCase_UploadedArchivedDocumentPanel.value();
    }
    
    public String getTaxcorpCaseTaxationPanelID(){
        return RoseWebIdValue.TaxcorpCase_TaxationPanel.value();
    }
}
