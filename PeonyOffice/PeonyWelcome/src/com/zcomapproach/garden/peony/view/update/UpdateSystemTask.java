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

package com.zcomapproach.garden.peony.view.update;

import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.commons.nio.ZcaNio;
import com.zcomapproach.garden.peony.kernel.services.PeonyBusinessService;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.persistence.constant.TaxFilingType;
import com.zcomapproach.garden.persistence.entity.G02TaxFilingCase;
import com.zcomapproach.garden.persistence.peony.PeonyTaxFilingCase;
import com.zcomapproach.garden.persistence.peony.PeonyTaxFilingCaseList;
import com.zcomapproach.garden.rest.GardenRestParams;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class UpdateSystemTask extends Task<Boolean>{
    
    private String logFilePath;

    @Override
    protected Boolean call() throws Exception {
        Date fromDate = ZcaCalendar.createDate(2019, 8, 20, 0, 0, 0);
        Date toDate = ZcaCalendar.createDate(2020, 8, 29, 0, 0, 0);
        PeonyTaxFilingCaseList recordList = Lookup.getDefault().lookup(PeonyBusinessService.class)
                        .getPeonyBusinessRestClient().findEntity_XML(PeonyTaxFilingCaseList.class, 
                                GardenRestParams.Business.findPeonyTaxFilingCaseListByTypeAndDateRangeRestParams(TaxFilingType.PAYROLL_TAX.value(), 
                                        Long.toString(fromDate.getTime()), Long.toString(toDate.getTime())));
        if (recordList == null){
            updateMessage("[Reason] Cannot find any record.");
            return false;
        }
        
        List<PeonyTaxFilingCase> records = recordList.getPeonyTaxFilingCaseList();
        logFilePath = "" + ZcaUtils.generateRandomSecretCode(8) + ".txt";
        ZcaNio.createNewRegularFile(logFilePath);
        for (PeonyTaxFilingCase record : records){
            appendTextLineToFile(">>> Record - " + record.getTaxFilingCase().getTaxFilingUuid(), logFilePath);
            saveRecord(modifyUtcDates(record.getTaxFilingCase(), fromDate, toDate));
        }
        
        return true;
    }

    private G02TaxFilingCase modifyUtcDates(G02TaxFilingCase record, Date fromDate, Date toDate) throws IOException{
        if ((record.getUpdated().after(fromDate) && record.getUpdated().before(toDate))
                || (record.getCreated().after(fromDate) && record.getCreated().before(toDate)))
        {
            record.setCompleted(modifyUtcDatesHelper(" - getCompleted", record.getCompleted()));
            record.setDeadline(modifyUtcDatesHelper(" - getDeadline", record.getDeadline()));
            record.setExtension(modifyUtcDatesHelper(" - getExtension", record.getExtension()));
            record.setPickupOrEfile(modifyUtcDatesHelper(" - getPickupOrEfile", record.getPickupOrEfile()));
            record.setPrepared(modifyUtcDatesHelper(" - getPrepared", record.getPrepared()));
            record.setReceived(modifyUtcDatesHelper(" - getReceived", record.getReceived()));
            return record;
        }
        return null;
    }

    private Date modifyUtcDatesHelper(String field, Date date) throws IOException {
        Date result = null;
        if (date == null){
            appendTextLineToFile(field + " - NULL", logFilePath);
            appendTextLineToFile(field + " > NULL", logFilePath);
        }else{
            appendTextLineToFile(field + " - " + date.getTime() + " = " + ZcaCalendar.convertToMMddyyyyHHmmss(date, "-", "@", ":"), logFilePath);
            result = ZcaCalendar.changeUtcDateToLocal(date);
            appendTextLineToFile(field + " > " + result.getTime() + " = " + ZcaCalendar.convertToMMddyyyyHHmmss(result, "-", "@", ":"), logFilePath);
        }
        return result;
    }

    private void saveRecord(G02TaxFilingCase record) throws IOException {
        try {
            G02TaxFilingCase aG02TaxFilingCase = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                    .storeEntity_XML(G02TaxFilingCase.class, GardenRestParams.Business.storeTaxFilingCaseRestParams(), record);
            if (aG02TaxFilingCase == null){
                appendTextLineToFile(" - failed to save it.", logFilePath);
            }else{
                appendTextLineToFile(" - succeeded to save it.", logFilePath);
            }
        } catch (Exception ex) {
            //Exceptions.printStackTrace(ex);
            appendTextLineToFile(" - failed due to technical error. " + ex.getMessage(), logFilePath);
        }
    }

    private void appendTextLineToFile(String textLine, String logFilePath) throws IOException {
        PeonyFaceUtils.publishMessageOntoOutputWindow(textLine);
        ZcaNio.appendTextLineToFile(textLine, logFilePath);
    }

    @Override
    protected void failed() {
        PeonyFaceUtils.publishMessageOntoOutputWindowWithErrorPopup("Cannot run this update because of technical errors." + getMessage());
    }

    @Override
    protected void succeeded() {
        try {
            Boolean result = get();
            if (result){
                PeonyFaceUtils.publishMessageOntoOutputWindowWithInformationPopup("Completed this update." + getMessage());
            }else{
                PeonyFaceUtils.publishMessageOntoOutputWindowWithErrorPopup("Failed to run this update." + getMessage());
            }
        } catch (InterruptedException | ExecutionException ex) {
            //Exceptions.printStackTrace(ex);
            PeonyFaceUtils.publishMessageOntoOutputWindowWithErrorPopup("InterruptedException or ExecutionException raised: " + getMessage());
        }
    }

}
