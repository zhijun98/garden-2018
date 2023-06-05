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

package com.zcomapproach.garden.rose.data.profile;

import com.zcomapproach.garden.persistence.constant.GardenDiscountType;
import com.zcomapproach.garden.persistence.entity.G01Bill;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaText;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zhijun98
 */
public class BusinessCaseBillProfile extends AbstractRoseEntityProfile {
    
    private G01Bill billEntity;
    private List<BusinessCasePaymentProfile> businessCasePaymentProfileList;

    private EmployeeAccountProfile agent;

    public BusinessCaseBillProfile() {
        this.billEntity = new G01Bill();
        this.businessCasePaymentProfileList = new ArrayList<>();
    }
    public EmployeeAccountProfile getAgent() {
        return agent;
    }

    public void setAgent(EmployeeAccountProfile agent) {
        this.agent = agent;
    }
    
    public List<BusinessCasePaymentProfile> getBusinessCasePaymentProfileList() {
        return businessCasePaymentProfileList;
    }

    public void setBusinessCasePaymentProfileList(List<BusinessCasePaymentProfile> businessCasePaymentProfileList) {
        this.businessCasePaymentProfileList = businessCasePaymentProfileList;
    }
    
    public double getFinalBalanceValueForWeb(){
        return getBillFinalTotalForWeb() - getTotalPaidPriceValueForWeb();
    }
    
    public double getBillTotalForWeb(){
        G01Bill aG01Bill = getBillEntity();
        if ((aG01Bill == null) || (aG01Bill.getBillTotal() == null)){
            return 0.0;
        }
        return aG01Bill.getBillTotal().doubleValue();
    }
    
    public String getBillDiscountForWeb(){
        G01Bill aG01Bill = getBillEntity();
        if ((aG01Bill == null) || (aG01Bill.getBillDiscount()== null)){
            return "N/A";
        }
        GardenDiscountType type = GardenDiscountType.convertEnumValueToType(aG01Bill.getBillDiscountType());
        switch(type){
            case PERCENTAGE:
                return aG01Bill.getBillDiscount() + aG01Bill.getBillDiscountType();
            case DOLLAR:
                return aG01Bill.getBillDiscount() + " " + aG01Bill.getBillDiscountType();
            default:
                return aG01Bill.getBillDiscount() + "(Unknown Unit)";
        }
        
    }
    
    public double getTotalPaidPriceValueForWeb(){
        List<BusinessCasePaymentProfile> aBusinessCasePaymentProfileList = getBusinessCasePaymentProfileList();
        double result = 0.0;
        for (BusinessCasePaymentProfile aGardenPaymentProfile : aBusinessCasePaymentProfileList){
            result = result + aGardenPaymentProfile.getPaymentEntity().getPaymentPrice().doubleValue();
        }
        return result;
    }
    
    public String getBillUuidForWeb(){
        return ZcaText.denullize(billEntity.getBillUuid()).toUpperCase();
    }
    
    public String getBillDatetimeForWeb(){
        if (billEntity.getBillDatetime() == null){
            return ZcaCalendar.convertToMMddyyyy(billEntity.getCreated(), "-");
        }else{
            return ZcaCalendar.convertToMMddyyyy(billEntity.getBillDatetime(), "-");
        }
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof BusinessCaseBillProfile)){
            return;
        }
        BusinessCaseBillProfile srcRoseBillProfile = (BusinessCaseBillProfile)srcProfile;
        //archivedDocumentEntity
        G01DataUpdaterFactory.getSingleton().getG01BillUpdater().cloneEntity(srcRoseBillProfile.getBillEntity(), 
                                                                                                this.getBillEntity());
        //businessCasePaymentProfileList
        List<BusinessCasePaymentProfile> srcBusinessCasePaymentProfileList = srcRoseBillProfile.getBusinessCasePaymentProfileList();
        getBusinessCasePaymentProfileList().clear();
        BusinessCasePaymentProfile aBusinessCasePaymentProfile;
        for (BusinessCasePaymentProfile srcG01DocumentRequirement : srcBusinessCasePaymentProfileList){
            aBusinessCasePaymentProfile = new BusinessCasePaymentProfile();
            aBusinessCasePaymentProfile.cloneProfile(srcG01DocumentRequirement);
            getBusinessCasePaymentProfileList().add(aBusinessCasePaymentProfile);
        }
    }
    
    public String getBillDiscountTypeValue(){
        String result = GardenDiscountType.convertEnumNameToType(getBillEntity().getBillDiscountType(), false).value();
        if (GardenDiscountType.UNKNOWN.value().equalsIgnoreCase(result)){
            result = "";
        }
        return result;
    }
    
    public double getBillFinalTotalForWeb(){
        G01Bill aG01Bill = getBillEntity();
        if ((aG01Bill == null) || (aG01Bill.getBillTotal() == null)){
            return 0.0;
        }
        GardenDiscountType type = GardenDiscountType.convertEnumValueToType(aG01Bill.getBillDiscountType());
        switch(type){
            case DOLLAR:
                if (aG01Bill.getBillDiscount() == null){
                    return aG01Bill.getBillTotal().doubleValue();
                }else{
                    return aG01Bill.getBillTotal().doubleValue() - aG01Bill.getBillDiscount();
                }
            case PERCENTAGE:
                if (aG01Bill.getBillDiscount() == null){
                    return aG01Bill.getBillTotal().doubleValue();
                }else{
                    return aG01Bill.getBillTotal().doubleValue()*(100 - aG01Bill.getBillDiscount())/100;
                }
            default:
                return 0.0;    
        }
    }

    @Override
    public String getProfileName() {
        return getProfileUuid();
    }

    @Override
    public String getProfileDescriptiveName() {
        return getProfileName();
    }

    @Override
    protected String getProfileUuid() {
        return billEntity.getBillUuid();
    }

    public G01Bill getBillEntity() {
        return billEntity;
    }

    public void setBillEntity(G01Bill billEntity) {
        this.billEntity = billEntity;
    }

}
