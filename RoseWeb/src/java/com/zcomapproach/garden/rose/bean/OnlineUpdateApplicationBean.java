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

import com.zcomapproach.garden.rose.onlineupdate.GardenDataTransfer;
import com.zcomapproach.commons.ZcaCalendar;
import java.util.Date;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 * Settings of Rose web
 * @author zhijun98
 */
@Named(value = "onlineUpdateApp")
@ApplicationScoped
public class OnlineUpdateApplicationBean extends AbstractRoseBean{
    
    @EJB
    private GardenDataTransfer transfer;
    
    private String result = "Click to update database";
    
    public void generateClientList(){
        /**
         * Deprecated
         */
        transfer.generateClientList();
        
        result = "Generated Taxpayer & Taxcorp List! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void transferGardenData01(){
        /**
         * Deprecated
         */
        transfer.process01();
        
        result = "Completed-Step01 database update! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void transferGardenData02(){
        /**
         * Deprecated
         */
        transfer.process02();
        
        result = "Completed-Step02 database update! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void transferGardenData03(){
        /**
         * Deprecated
         */
        transfer.process03();
        
        result = "Completed-Step03 database update! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void updateTaxFilingEntityFields(){
        /**
         * Deprecated
         */
        transfer.updateTaxFilingEntityFields();
        
        result = "Completed-Update Tax Filing Entity Fields! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void fixG01FisicalTypoIssue(){
        /**
         * Deprecated
         */
        transfer.fixG01FisicalTypoIssue();
        
        result = "Completed-Fix G01-Fisical-Typo Issue! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void updateTaxcorpContactorsBirthday(){
        /**
         * Deprecated
         */
        transfer.updateTaxcorpContactorsBirthday();
        
        result = "Completed-Update Taxcorp Contactors' Birthday! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void transferG02TaxFilingCaseFromExtensionForTaxcorp(){
        /**
         * Deprecated
         */
        transfer.transferG02TaxFilingCaseFromExtensionForTaxcorp();
        
        result = "Completed-Transfer g02_deadline_extension into g02_tax_filing_case! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void fixRedundantMemoText(){
        transfer.fixRedundantMemoText();
        
        result = "Completed-fixRedundantMemoText! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void constructMemoMapForTaxFiling(){
        transfer.constructMemoMapForTaxFiling();
        
        result = "Completed-constructMemoMapForTaxFiling! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void recoverChineseMemoForTaxFiling(){
        //transfer.recoverChineseMemoForTaxFiling();
        
        result = "Completed-recoverChineseMemoForTaxFiling! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void transferExtensionsIntoG02TaxpayerCase(){
        transfer.transferExtensionsIntoG02TaxpayerCase();
        
        result = "Completed-transferExtensionsIntoG02TaxpayerCase! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void transferMemosIntoG02TaxpayerCase(){
        transfer.transferMemosIntoG02TaxpayerCase();
        
        result = "Completed-transferMemosIntoG02TaxpayerCase! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void updateG02ArchivedFile(){
        transfer.updateG02ArchivedFile();
        result = "Completed-updateG02ArchivedFile! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void synchronizePathsForOfflineEmails(){
        transfer.synchronizePathsForOfflineEmails();
        
        result = "Completed-synchronizePathsForOfflineEmails! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void updateLocalHostForDevelopment(){
        transfer.updateLocalHostForDevelopment();
        
        result = "Completed-updateLocalHostForDevelopment! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void updateG02JobAssignmentStatus(){
        transfer.updateG02JobAssignmentStatus();
        
        result = "Completed-updateG02JobAssignmentStatus! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void mergeRedundantDocumentTags(){
        transfer.mergeRedundantDocumentTags();
        
        result = "Completed-mergeRedundantDocumentTags! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void moveTaxpayerCaseMemo(){
        transfer.moveTaxpayerCaseMemo();
        
        result = "Completed-moveTaxpayerCaseMemo! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void updateG02SystemSettings(){
        transfer.updateG02SystemSettings();
        
        result = "Completed-updateG02SystemSettings! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void changeFileExtensionToBeLowerCase(){
        transfer.changeFileExtensionToBeLowerCase();
        
        result = "Completed-changeFileExtensionToBeLowerCase! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void transferG02TaxFilingCaseFromMemoForTaxcorp(){
        /**
         * Deprecated
         */
        transfer.transferG02TaxFilingCaseFromMemoForTaxcorp();
        
        result = "Completed-Transfer g02_memo into g02_tax_filing_case! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void fixLoggedEntityFields(){
        /**
         * Deprecated
         */
        transfer.fixLoggedEntityFields();
        
        result = "Fix g01_log-TO-g02_log (logged-entity's UUID and Type were missed! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }
    
    public void transferG02TaxFilingCaseFromStatus(){
        /**
         * Deprecated
         */
        transfer.transferG02TaxFilingCaseFromStatus();
        
        result = "Completed-Transfer g02_tax_filing_status into g02_tax_filing_case! @" + ZcaCalendar.convertToHHmmss(new Date(), ":");
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
