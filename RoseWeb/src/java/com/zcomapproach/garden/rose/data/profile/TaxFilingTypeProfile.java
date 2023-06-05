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

import com.zcomapproach.garden.persistence.entity.G01TaxFilingType;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author zhijun98
 */
public class TaxFilingTypeProfile extends AbstractRoseEntityProfile{

    private G01TaxFilingType taxFilingTypeEntity;
    private List<TaxFilingProfile> taxFilingProfileList;

    public TaxFilingTypeProfile() {
        this.taxFilingTypeEntity = new G01TaxFilingType();
        this.taxFilingProfileList = new ArrayList<>();
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof TaxFilingTypeProfile)){
            return;
        }
        TaxFilingTypeProfile srcTaxFilingTypeProfile = (TaxFilingTypeProfile)srcProfile;
        //taxFilingTypeEntity
        G01DataUpdaterFactory.getSingleton().getG01TaxFilingTypeUpdater().cloneEntity(srcTaxFilingTypeProfile.getTaxFilingTypeEntity(), 
                                                                                         this.getTaxFilingTypeEntity());
        //taxFilingProfileList
        List<TaxFilingProfile> srcTaxFilingProfileList = srcTaxFilingTypeProfile.getTaxFilingProfileList();
        getTaxFilingProfileList().clear();
        TaxFilingProfile aTaxFilingProfile;
        for (TaxFilingProfile srcTaxFilingProfile : srcTaxFilingProfileList){
            aTaxFilingProfile = new TaxFilingProfile();
            aTaxFilingProfile.cloneProfile(srcTaxFilingProfile);
            getTaxFilingProfileList().add(aTaxFilingProfile);
        }
    }

    public G01TaxFilingType getTaxFilingTypeEntity() {
        return taxFilingTypeEntity;
    }

    public void setTaxFilingTypeEntity(G01TaxFilingType taxFilingTypeEntity) {
        this.taxFilingTypeEntity = taxFilingTypeEntity;
    }

    public List<TaxFilingProfile> getTaxFilingProfileList() {
        return taxFilingProfileList;
    }

    public void setTaxFilingProfileList(List<TaxFilingProfile> taxFilingProfileList) {
        Collections.sort(taxFilingProfileList, (TaxFilingProfile o1, TaxFilingProfile o2) -> {
            try{
                return o1.getTaxFilingEntity().getDeadline().compareTo(o2.getTaxFilingEntity().getDeadline())*(-1);
            }catch (Exception ex){
                return 0;
            }
        });
        this.taxFilingProfileList = taxFilingProfileList;
    }
    
    @Override
    public String getProfileName() {
        return taxFilingTypeEntity.getG01TaxFilingTypePK().getTaxcorpCaseUuid() 
                + ": " + taxFilingTypeEntity.getG01TaxFilingTypePK().getTaxFilingType()
                + " " + taxFilingTypeEntity.getG01TaxFilingTypePK().getTaxFilingPeriod();
    }

    @Override
    public String getProfileDescriptiveName() {
        return getProfileName();
    }

    @Override
    protected String getProfileUuid() {
        return getProfileName();
    }

}
