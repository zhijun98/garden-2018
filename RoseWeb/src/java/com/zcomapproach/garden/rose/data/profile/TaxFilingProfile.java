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

import com.zcomapproach.garden.persistence.constant.TaxFilingType;
import com.zcomapproach.garden.persistence.entity.G01TaxFiling;
import com.zcomapproach.garden.persistence.entity.G01TaxcorpCase;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaText;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zhijun98
 */
public class TaxFilingProfile extends AbstractRoseEntityProfile{

    private G01TaxFiling taxFilingEntity;
    
    //optional
    private G01TaxcorpCase taxcorpCaseEntity;
    //optional
    private List<TaxcorpRepresentativeProfile> taxcorpRepresentativeProfileList;

    public TaxFilingProfile() {
        taxFilingEntity = new G01TaxFiling();
        taxcorpCaseEntity = new G01TaxcorpCase();
        taxcorpRepresentativeProfileList = new ArrayList<>();
    }
    
    public boolean isExtensionDisabled(){
        return !TaxFilingType.TAX_RETURN.value().equalsIgnoreCase(getTaxFilingEntity().getTaxFilingType());
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof TaxFilingProfile)){
            return;
        }
        TaxFilingProfile srcTaxFilingProfile = (TaxFilingProfile)srcProfile;
        //taxFilingEntity
        G01DataUpdaterFactory.getSingleton().getG01TaxFilingUpdater().cloneEntity(srcTaxFilingProfile.getTaxFilingEntity(), 
                                                                                                this.getTaxFilingEntity());
    }
    
    public boolean isContactorAvailable(){
        return !taxcorpRepresentativeProfileList.isEmpty();
    }
    
    public String getContactorWebNames(){
        String result = null;
        if (!taxcorpRepresentativeProfileList.isEmpty()){
            for (TaxcorpRepresentativeProfile aTaxcorpRepresentativeProfile : taxcorpRepresentativeProfileList){
                if (result == null){
                    result = aTaxcorpRepresentativeProfile.getProfileName();
                }else{
                    result += "; " + aTaxcorpRepresentativeProfile.getProfileName();
                }
            }
            result = result.trim().substring(0, result.length()-1);
        }
        return ZcaText.denullize(result);
    }

    public List<TaxcorpRepresentativeProfile> getTaxcorpRepresentativeProfileList() {
        return taxcorpRepresentativeProfileList;
    }

    public void setTaxcorpRepresentativeProfileList(List<TaxcorpRepresentativeProfile> taxcorpRepresentativeProfileList) {
        this.taxcorpRepresentativeProfileList = taxcorpRepresentativeProfileList;
    }

    public G01TaxcorpCase getTaxcorpCaseEntity() {
        return taxcorpCaseEntity;
    }

    public void setTaxcorpCaseEntity(G01TaxcorpCase taxcorpCaseEntity) {
        this.taxcorpCaseEntity = taxcorpCaseEntity;
    }

    public G01TaxFiling getTaxFilingEntity() {
        return taxFilingEntity;
    }

    public void setTaxFilingEntity(G01TaxFiling taxFilingEntity) {
        this.taxFilingEntity = taxFilingEntity;
    }

    @Override
    public String getProfileName() {
        if (taxFilingEntity.getExtensionDate() == null){
            return taxFilingEntity.getTaxFilingPeriod() + ": " + ZcaCalendar.convertToMMddyyyy(taxFilingEntity.getDeadline(), "-");
        }else{
            return taxFilingEntity.getTaxFilingPeriod() + ": " + ZcaCalendar.convertToMMddyyyy(taxFilingEntity.getDeadline(), "-")
                    + " >>> " + ZcaCalendar.convertToMMddyyyy(taxFilingEntity.getExtensionDate(), "-");
        }
    }

    @Override
    public String getProfileDescriptiveName() {
        return taxFilingEntity.getTaxFilingType() + " - " + getProfileName();
    }

    @Override
    protected String getProfileUuid() {
        return taxFilingEntity.getTaxFilingUuid();
    }
    
}
