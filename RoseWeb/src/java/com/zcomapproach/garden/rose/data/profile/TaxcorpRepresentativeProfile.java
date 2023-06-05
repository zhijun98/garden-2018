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

import com.zcomapproach.garden.persistence.entity.G01TaxcorpRepresentative;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;

/**
 *
 * @author zhijun98
 */
public class TaxcorpRepresentativeProfile extends RoseUserProfile{

    private G01TaxcorpRepresentative taxcorpRepresentativeEntity;
    
    public TaxcorpRepresentativeProfile() {
        this.taxcorpRepresentativeEntity = new G01TaxcorpRepresentative();
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof TaxcorpRepresentativeProfile)){
            return;
        }
        TaxcorpRepresentativeProfile srcTaxcorpRepresentativeProfile = (TaxcorpRepresentativeProfile)srcProfile;
        //taxcorpHasRepresentativeEntity
        G01DataUpdaterFactory.getSingleton().getG01TaxcorpRepresentativeUpdater().cloneEntity(srcTaxcorpRepresentativeProfile.getTaxcorpRepresentativeEntity(), 
                                                                                                 this.getTaxcorpRepresentativeEntity());
        //userProfile
        super.cloneProfile(srcProfile);
    }

    public G01TaxcorpRepresentative getTaxcorpRepresentativeEntity() {
        return taxcorpRepresentativeEntity;
    }

    public void setTaxcorpRepresentativeEntity(G01TaxcorpRepresentative taxcorpRepresentativeEntity) {
        this.taxcorpRepresentativeEntity = taxcorpRepresentativeEntity;
    }

//    public RoseUserProfile getUserProfile() {
//        return userProfile;
//    }
//
//    public void setUserProfile(RoseUserProfile userProfile) {
//        this.userProfile = userProfile;
//    }

    @Override
    public String getProfileName() {
        return super.getProfileName();
    }

    @Override
    public String getProfileDescriptiveName() {
        return super.getProfileDescriptiveName() + " (" + taxcorpRepresentativeEntity.getRoleInCorp() + ")";
    }

    @Override
    protected String getProfileUuid() {
        return taxcorpRepresentativeEntity.getG01TaxcorpRepresentativePK().getTaxcorpCaseUuid()
                + taxcorpRepresentativeEntity.getG01TaxcorpRepresentativePK().getRepresentativeUserUuid();
    }
}
