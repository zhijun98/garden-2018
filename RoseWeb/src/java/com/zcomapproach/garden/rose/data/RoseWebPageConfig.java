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

package com.zcomapproach.garden.rose.data;

import com.zcomapproach.garden.rose.RoseWebIdValue;
import java.util.HashMap;

/**
 * Contains a user's web page configuration, which is hold per user-session
 * <p>
 * (1) add a new web-id in RoseWebIdValue.java
 * (2) expose this web-id in RoseWebIdApplicationBean.java
 * (3) implement boolean-function for this new web-id in RoseWebPageConfig.java
 * (4) go to the web page, add web-id, hook web-id's feature (e.g. collapsed), and AJAX event (e.g. onRoseToggleEvent)
 * @author zhijun98
 */
public class RoseWebPageConfig {
    
    private final HashMap<RoseWebIdValue, Boolean> webComponentCollapseExpand = new HashMap<>();
    
    private boolean getWebComponentCollapsed(RoseWebIdValue id) {
        if (webComponentCollapseExpand.containsKey(id)){
            return webComponentCollapseExpand.get(id);
        }
        return false;
    }

    private void setWebComponentCollapsed(RoseWebIdValue id, boolean value) {
        webComponentCollapseExpand.put(id, value);
    }
    
    public void toggleWebComponentCollapseExpand(RoseWebIdValue id){
        if (id == null){
            return;
        }
        setWebComponentCollapsed(id, !getWebComponentCollapsed(id));
    }

    public void collapseWebComponent(RoseWebIdValue id) {
        if (id == null){
            return;
        }
        setWebComponentCollapsed(id, true);
    }

    public void expandWebComponent(RoseWebIdValue id) {
        if (id == null){
            return;
        }
        setWebComponentCollapsed(id, false);
    }

    public boolean isTaxcorpCaseCustomerProfilePanelCollapsed(){
        return getWebComponentCollapsed(RoseWebIdValue.TaxcorpCase_CustomerProfilePanel);
    }

    public boolean isTaxcorpCaseBasicInformationPanelCollapsed(){
        return getWebComponentCollapsed(RoseWebIdValue.TaxcorpCase_BasicInformationPanel);
    }

    public boolean isTaxcorpCaseContactorPanelCollapsed(){
        return getWebComponentCollapsed(RoseWebIdValue.TaxcorpCase_ContactorPanel);
    }

    public boolean isTaxcorpCaseUploadedArchivedDocumentPanelCollapsed(){
        return getWebComponentCollapsed(RoseWebIdValue.TaxcorpCase_UploadedArchivedDocumentPanel);
    }

    public boolean isTaxcorpCaseTaxationPanelCollapsed(){
        return getWebComponentCollapsed(RoseWebIdValue.TaxcorpCase_TaxationPanel);
    }

}
