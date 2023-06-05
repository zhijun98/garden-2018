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
import com.zcomapproach.garden.persistence.entity.G01Payment;
import com.zcomapproach.garden.persistence.entity.G01TaxcorpCase;
import java.util.List;

/**
 *
 * @author zhijun98
 */
public class TaxcorpBillBalanceProfile extends AbstractEntityConciseProfile {

    private G01TaxcorpCase taxcorpCase;
    private G01Bill bill;
    private List<G01Payment> paymentList;
    
    private double billTotalValue = -1.0;
    private double paidValue = -1.0;
    private double balanceValue = -1.0;

    public G01TaxcorpCase getTaxcorpCase() {
        return taxcorpCase;
    }

    public void setTaxcorpCase(G01TaxcorpCase taxcorpCase) {
        this.taxcorpCase = taxcorpCase;
    }

    public G01Bill getBill() {
        return bill;
    }

    public void setBill(G01Bill bill) {
        this.bill = bill;
    }

    public List<G01Payment> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<G01Payment> paymentList) {
        this.paymentList = paymentList;
    }

    public double getBillTotalValue() {
        if (billTotalValue < 0){
            billTotalValue = bill.getBillTotal().doubleValue();
            Float discount = bill.getBillDiscount();
            if (discount != null){
                if (GardenDiscountType.PERCENTAGE.value().equalsIgnoreCase(bill.getBillDiscountType())){
                    billTotalValue = billTotalValue * (1.0 - discount/100.0);
                }else{
                    billTotalValue = billTotalValue - discount;
                }
            }
        }
        return billTotalValue;
    }

    public double getPaidValue() {
        if (paidValue < 0){
            paidValue = 0.0;
            if (paymentList != null){
                for (G01Payment aG01Payment : paymentList){
                    paidValue += aG01Payment.getPaymentPrice().doubleValue();
                }
            }
        }
        return paidValue;
    }

    public double getBalanceValue() {
        if (balanceValue < 0){
            balanceValue = 0.0;
            balanceValue = getBillTotalValue() - getPaidValue();
        }
        return balanceValue;
    }
    
    @Override
    public String getProfileName() {
        return getTaxcorpCase().getCorporateName();
    }

    @Override
    public String getProfileDescriptiveName() {
        return getTaxcorpCase().getCorporateName() + " (" + getTaxcorpCase().getEinNumber() + ")";
    }

    @Override
    protected String getProfileUuid() {
        return getTaxcorpCase().getTaxcorpCaseUuid() + getBill().getBillUuid();
    }
    
}
