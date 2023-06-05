/*
 * Copyright 2018 ZComApproach Inc.
 *
 * Licensed under multiple open source licenses involved in the project (the "Licenses".name();
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

import com.zcomapproach.garden.rose.RosePageName;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author zhijun98
 */
@Named(value = "rosePages")
@ApplicationScoped
public class RosePagesBean extends AbstractRoseBean{
    
    public String getWelcomePageName(){
        return RosePageName.WelcomePage.name();
    }
    
    public String getRegisterPageName(){
        return RosePageName.RegisterPage.name();
    }
    
    public String getLoginPageName(){
        return RosePageName.LoginPage.name();
    }
    
    public String getRedeemCredentialsPageName(){
        return RosePageName.RedeemCredentialsPage.name();
    }
    
    public String getAccountConfirmationPageName(){
        return RosePageName.AccountConfirmationPage.name();
    }
    
    public String getComposeEmailPageName(){
        return RosePageName.ComposeEmailPage.name();
    }
    
    public String getContactUsPageName(){
        return RosePageName.ContactUsPage.name();
    }
    
    public String getUploadDocumentPageName(){
        return RosePageName.UploadDocumentPage.name();
    }
    
    public String getRoseDocumentMgtPageName(){
        return RosePageName.RoseDocumentMgtPage.name();
    }
    
    public String getRoseContactPageName(){
        return RosePageName.RoseContactPage.name();
    }
    
    public String getTaxcorpFilingContactPageName(){
        return RosePageName.TaxcorpFilingContactPage.name();
    }
    
    public String getWebPostPublishPageName(){
        return RosePageName.WebPostPublishPage.name();
    }
    
    public String getWebPostPublishOptionsPageName(){
        return RosePageName.WebPostPublishOptionsPage.name();
    }
    
    public String getEditWebPostPageName(){
        return RosePageName.EditWebPostPage.name();
    }
    
    public String getHistoricalWebPostListPageName(){
        return RosePageName.HistoricalWebPostListPage.name();
    }
    
    public String getPublicPostPageName(){
        return RosePageName.PublicPostPage.name();
    }
    
    public String getBusinessHomePageName(){
        return RosePageName.BusinessHomePage.name();
    }
    
    public String getBusinessEmailBoxPageName(){
        return RosePageName.BusinessEmailBoxPage.name();
    }
    
    public String getPrintableBusinessEmailBoxPageName(){
        return RosePageName.PrintableBusinessEmailBoxPage.name();
    }
    
    public String getSentEmailBoxPageName(){
        return RosePageName.SentEmailBoxPage.name();
    }
    
    public String getPrintableSentEmailBoxPageName(){
        return RosePageName.PrintableSentEmailBoxPage.name();
    }
    
    public String getSearchEnginePageName(){
        return RosePageName.SearchEnginePage.name();
    }
    
    public String getSearchTaxcorpFilingResultPageName(){
        return RosePageName.SearchTaxcorpFilingResultPage.name();
    }
    
    public String getSearchTaxpayerResultPageName(){
        return RosePageName.SearchTaxpayerResultPage.name();
    }
    
    public String getRoseInvoicePageName(){
        return RosePageName.RoseInvoicePage.name();
    }
    
    public String getRoseInvoicePrintablePageName(){
        return RosePageName.RoseInvoicePrintablePage.name();
    }
    
    public String getEmployeeProfileListPageName(){
        return RosePageName.EmployeeProfileListPage.name();
    }
    
    public String getEmployeeProfilePageName(){
        return RosePageName.EmployeeProfilePage.name();
    }
    
    public String getWorkTeamListPageName(){
        return RosePageName.WorkTeamListPage.name();
    }
    
    public String getWorkTeamProfilePageName(){
        return RosePageName.WorkTeamProfilePage.name();
    }
    
    public String getClientProfileListPageName(){
        return RosePageName.ClientProfileListPage.name();
    }
    
    public String getClientProfilePageName(){
        return RosePageName.ClientProfilePage.name();
    }
    
    public String getUserProfileListPageName(){
        return RosePageName.UserProfileListPage.name();
    }
    
    public String getUserProfilePageName(){
        return RosePageName.UserProfilePage.name();
    }
    
    public String getCustomerHomePageName(){
        return RosePageName.CustomerHomePage.name();
    }
    
    public String getTaxcorpCasePageName(){
        return RosePageName.TaxcorpCasePage.name();
    }
    
    public String getTaxcorpCaseMgtPageName(){
        return RosePageName.TaxcorpCaseMgtPage.name();
    }
    
    public String getTaxFilingTypeProfileListPageName(){
        return RosePageName.TaxFilingTypeProfileListPage.name();
    }
    
    public String getTaxcorpCaseViewPageName(){
        return RosePageName.TaxcorpCaseViewPage.name();
    }
    
    public String getTaxcorpCaseConfirmPageName(){
        return RosePageName.TaxcorpCaseConfirmPage.name();
    }
    
    public String getTaxcorpCasePrintablePageName(){
        return RosePageName.TaxcorpCasePrintablePage.name();
    }
    
    public String getTaxpayerCasePageName(){
        return RosePageName.TaxpayerCasePage.name();
    }
    
    public String getTaxpayerCaseMgtPageName(){
        return RosePageName.TaxpayerCaseMgtPage.name();
    }
    
    public String getTaxpayerCasePrintablePageName(){
        return RosePageName.TaxpayerCasePrintablePage.name();
    }
    
    public String getTaxpayerCaseViewPageName(){
        return RosePageName.TaxpayerCaseViewPage.name();
    }
    
    public String getTaxpayerCaseConfirmPageName(){
        return RosePageName.TaxpayerCaseConfirmPage.name();
    }
    
    public String getEmployeeTaskListPageName(){
        return RosePageName.EmployeeTaskListPage.name();
    }
    
    public String getHistoricalTaxpayerCasesPageName(){
        return RosePageName.HistoricalTaxpayerCasesPage.name();
    }
    
    public String getHistoricalTaxcorpCasesPageName(){
        return RosePageName.HistoricalTaxcorpCasesPage.name();
    }
    
    public String getYourAccountPageName(){
        return RosePageName.YourAccountPage.name();
    }

}
