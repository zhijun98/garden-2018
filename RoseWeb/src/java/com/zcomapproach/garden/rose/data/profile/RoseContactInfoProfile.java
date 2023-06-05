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

import com.zcomapproach.garden.persistence.constant.GardenContactType;
import com.zcomapproach.garden.persistence.constant.GardenPreference;
import com.zcomapproach.garden.persistence.entity.G01ContactInfo;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaValidator;

/**
 *
 * @author zhijun98
 */
public class RoseContactInfoProfile extends AbstractRoseEntityProfile{
    
    private G01ContactInfo contactInfoEntity;

    public RoseContactInfoProfile() {
        this.contactInfoEntity = new G01ContactInfo();
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof RoseContactInfoProfile)){
            return;
        }
        RoseContactInfoProfile srcRoseContactInfoProfile = (RoseContactInfoProfile)srcProfile;
        G01DataUpdaterFactory.getSingleton().getG01ContactInfoUpdater().cloneEntity(srcRoseContactInfoProfile.getContactInfoEntity(), this.getContactInfoEntity());
        
    }

    @Override
    public String getProfileName() {
        return contactInfoEntity.getContactType() + ": " + contactInfoEntity.getContactInfo();
    }

    @Override
    public String getProfileDescriptiveName() {
        return getProfileName();    //todo zzj: add who own this contact record
    }

    @Override
    protected String getProfileUuid() {
        return contactInfoEntity.getContactInfoUuid();
    }

    public G01ContactInfo getContactInfoEntity() {
        return contactInfoEntity;
    }

    public void setContactInfoEntity(G01ContactInfo contactInfoEntity) {
        this.contactInfoEntity = contactInfoEntity;
    }
    
    public String getMemoForWebFace(){
        String result = getPreferenceValue();
        if (ZcaValidator.isNotNullEmpty(this.contactInfoEntity.getShortMemo())){
            if (ZcaValidator.isNullEmpty(result)){
                result = RoseText.getText("Memo") + ": " + this.contactInfoEntity.getShortMemo();
            }else{
                result = RoseText.getText("Memo") + ": " + this.contactInfoEntity.getShortMemo() + " - " + result;
            }
        }
        if (ZcaValidator.isNullEmpty(result)){
            result = ZcaCalendar.convertToMMddyyyyHHmmss(contactInfoEntity.getUpdated(), "-", " ", ":");
        }
        return result;
    }

    public String getPreferenceValue(){
        GardenPreference pref = GardenPreference.convertIndexToType(contactInfoEntity.getPreferencePriority());
        if (GardenPreference.UNKNOWN.equals(pref)){
            return "";
        }else{
            return pref.value();
        }
    }

    public void setPreferenceValue(String preferenceValue){
        contactInfoEntity.setPreferencePriority(GardenPreference.convertEnumValueToType(preferenceValue).ordinal());
    }
    
    /**
     * Current target GxxContactInfo's type is Email
     * @return 
     */
    public boolean isEmailType(){
        return GardenContactType.EMAIL.equals(GardenContactType.convertEnumValueToType(contactInfoEntity.getContactType()));
    }
    
    /**
     * Current target GxxContactInfo's type is Phone
     * @return 
     */
    public boolean isPhoneLikeType(){
        GardenContactType type = GardenContactType.convertEnumValueToType(contactInfoEntity.getContactType());
        return GardenContactType.HOME_PHONE.equals(type)
                || GardenContactType.MOBILE_PHONE.equals(type)
                || GardenContactType.LANDLINE_PHONE.equals(type)
                || GardenContactType.WORK_PHONE.equals(type)
                || GardenContactType.FAX.equals(type);
    
    }
    
    /**
     * Current target GxxContactInfo's type is Phone
     * @return 
     */
    public boolean isWeChat(){
        return GardenContactType.WECHAT.equals(GardenContactType.convertEnumValueToType(contactInfoEntity.getContactType()));
    }
    
    /**
     * Current target GxxContactInfo's type is Phone
     * @return 
     */
    public boolean isUnknownType(){
        return GardenContactType.UNKNOWN.equals(GardenContactType.convertEnumValueToType(contactInfoEntity.getContactType()));
    }
    
}
