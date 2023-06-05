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

import com.zcomapproach.garden.rose.data.profile.BusinessCaseBillProfile;
import java.util.List;

/**
 *
 * @author zhijun98
 */
public interface IBusinessBillPaymentInvoiceBean {
    
    public void saveTargetBusinessCaseBillProfile();
    
    public void saveTargetBusinessCasePaymentProfile();
    
    public void deleteBusinessCaseBillProfile(String billUuid);
    
    public void deleteBusinessCasePaymentProfile(String billUuid, String paymentUuid);
    
    public List<BusinessCaseBillProfile> getBusinessCaseBillProfileListOfTargetBusinessCase();
    
    //GUI Parts
    
    public boolean isDisplayBusinessCaseBillDataEntryPanelDemanded();
    public boolean isDisplayBusinessCasePaymentDataEntryPanelDemanded();
    public void displayBusinessCaseBillDataEntryPanel();
    public void displayBusinessCaseBillDataEntryPanelForEdit(String billUuid);
    public void hideBusinessCaseBillDataEntryPanel();
    public void displayBusinessCasePaymentDataEntryPanel(String billUuid);
    public void displayBusinessCasePaymentDataEntryPanelForEdit(String billUuid, String paymentUuid);
    public void hideBusinessCasePaymentDataEntryPanel();
}
