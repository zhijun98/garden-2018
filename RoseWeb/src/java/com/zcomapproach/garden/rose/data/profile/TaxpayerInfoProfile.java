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

import com.zcomapproach.garden.persistence.entity.G01TaxpayerInfo;
import com.zcomapproach.garden.persistence.entity.G01User;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.commons.ZcaValidator;

/**
 *
 * @author zhijun98
 */
public class TaxpayerInfoProfile extends AbstractRoseEntityProfile{
    
    private G01TaxpayerInfo taxpayerInfoEntity;
    private RoseUserProfile roseUserProfile;

    public TaxpayerInfoProfile() {
        this.taxpayerInfoEntity = new G01TaxpayerInfo();
        this.roseUserProfile = new RoseUserProfile();
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof TaxpayerInfoProfile)){
            return;
        }
        TaxpayerInfoProfile srcTaxpayerInfoProfile = (TaxpayerInfoProfile)srcProfile;
        //taxpayerInfoEntity
        G01DataUpdaterFactory.getSingleton().getG01TaxpayerInfoUpdater().cloneEntity(srcTaxpayerInfoProfile.getTaxpayerInfoEntity(), 
                                                                                    this.getTaxpayerInfoEntity());
        //roseUserProfile
        getRoseUserProfile().cloneProfile(srcTaxpayerInfoProfile.getRoseUserProfile());
    }
    
    public static TaxpayerInfoProfile createTaxpayerInfoProfileInstanceByAccountProfile(RoseAccountProfile accountProfile){
        TaxpayerInfoProfile aTaxpayerInfoProfile = new TaxpayerInfoProfile();
        G01TaxpayerInfo taxpayer = new G01TaxpayerInfo();
        aTaxpayerInfoProfile.setTaxpayerInfoEntity(taxpayer);
        taxpayer.setEmail(accountProfile.retrieveEmail());
        taxpayer.setHomePhone(accountProfile.retireveHomePhone());
        taxpayer.setFax(accountProfile.retireveFax());
        taxpayer.setMobilePhone(accountProfile.retireveMobilePhone());
        taxpayer.setWorkPhone(accountProfile.retireveWorkPhone());
        RoseUserProfile aRoseUserProfile = new RoseUserProfile();
        RoseUserProfile theRoseUserProfile = accountProfile.getUserProfile();
        aRoseUserProfile.setAccountEntity(null);
        aRoseUserProfile.setEmail(theRoseUserProfile.getEmail());
        aRoseUserProfile.setFax(theRoseUserProfile.getFax());
        aRoseUserProfile.setHomePhone(theRoseUserProfile.getHomePhone());
        aRoseUserProfile.setMobilePhone(theRoseUserProfile.getMobilePhone());
        G01User theG01User = theRoseUserProfile.getUserEntity();
        G01User aG01User = new G01User();
        aG01User.setBirthday(theG01User.getBirthday());
        aG01User.setCitizenship(theG01User.getCitizenship());
        aG01User.setFirstName(theG01User.getFirstName());
        aG01User.setGender(theG01User.getGender());
        aG01User.setLastName(theG01User.getLastName());
        aG01User.setMemo(theG01User.getMemo());
        aG01User.setMiddleName(theG01User.getMiddleName());
        aG01User.setOccupation(theG01User.getOccupation());
        aG01User.setSsn(theG01User.getSsn());
        aRoseUserProfile.setUserEntity(aG01User);
        aRoseUserProfile.setWeChat(theRoseUserProfile.getWeChat());
        aRoseUserProfile.setWorkPhone(theRoseUserProfile.getWorkPhone());
        aTaxpayerInfoProfile.setRoseUserProfile(aRoseUserProfile);
        
        return aTaxpayerInfoProfile;
    }

    public G01TaxpayerInfo getTaxpayerInfoEntity() {
        return taxpayerInfoEntity;
    }

    public void setTaxpayerInfoEntity(G01TaxpayerInfo taxpayerInfoEntity) {
        this.taxpayerInfoEntity = taxpayerInfoEntity;
    }

    public RoseUserProfile getRoseUserProfile() {
        return roseUserProfile;
    }

    public void setRoseUserProfile(RoseUserProfile roseUserProfile) {
        this.roseUserProfile = roseUserProfile;
    }

    @Override
    public String getProfileName() {
        return taxpayerInfoEntity.getTaxpayerUserUuid();
    }

    @Override
    public String getProfileDescriptiveName() {
        return taxpayerInfoEntity.getTaxpayerUserUuid();
    }

    @Override
    protected String getProfileUuid() {
        return taxpayerInfoEntity.getTaxpayerUserUuid();
    }

}
