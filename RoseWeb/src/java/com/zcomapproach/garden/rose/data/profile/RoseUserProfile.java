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
import com.zcomapproach.garden.persistence.entity.G01Account;
import com.zcomapproach.garden.persistence.entity.G01ContactInfo;
import com.zcomapproach.garden.persistence.entity.G01User;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.garden.rose.bean.IRoseContactInfoEntityEditor;
import com.zcomapproach.garden.rose.bean.IRoseLocationEntityEditor;
import com.zcomapproach.garden.rose.util.RoseDataAgent;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 *
 * @author zhijun98
 */
public class RoseUserProfile extends AbstractRoseEntityProfile implements IRoseAccountUserEntityProfile, IRoseLocationEntityEditor, IRoseContactInfoEntityEditor{
    
    private G01User userEntity;
    private List<RoseContactInfoProfile> userContactInfoProfileList;
    private List<RoseLocationProfile> userLocationProfileList;
    
    //Optional
    private G01Account accountEntity;
    
    //Optional
    private List<RoseContactInfoProfile> roseContactInfoProfileListForWebContact;
    
    /**
     * A collection of contacts
     */
    private String email;
    private String homePhone;
    private String mobilePhone;
    private String workPhone;
    private String fax;
    private String weChat;

    public RoseUserProfile() {
        this.userEntity = new G01User();
        this.userContactInfoProfileList = new ArrayList<>();
        this.userLocationProfileList = new ArrayList<>();
    }

    public G01Account getAccountEntity() {
        return accountEntity;
    }

    public void setAccountEntity(G01Account accountEntity) {
        this.accountEntity = accountEntity;
    }
    
    public String getContactInfoListForWeb(){
        String result = getProfileName();
        
        List<RoseContactInfoProfile> aRoseContactInfoProfileList = getRoseContactInfoProfileListForWebContact();
        if (!aRoseContactInfoProfileList.isEmpty()){
            result += ": ";
            for (RoseContactInfoProfile aRoseContactInfoProfile : aRoseContactInfoProfileList){
                result += "<" + aRoseContactInfoProfile.getProfileDescriptiveName() + "> ";
            }
        }
        
        return result;
    }

    public List<RoseContactInfoProfile> getRoseContactInfoProfileListForWebContact() {
        if (roseContactInfoProfileListForWebContact == null){
            roseContactInfoProfileListForWebContact = new ArrayList<>();
            TreeSet<String> filter = new TreeSet<>();
            for (RoseContactInfoProfile aRoseContactInfoProfile : userContactInfoProfileList){
                 if ((GardenContactType.EMAIL.value().equalsIgnoreCase(aRoseContactInfoProfile.getContactInfoEntity().getContactType()))
                         || (GardenContactType.MOBILE_PHONE.value().equalsIgnoreCase(aRoseContactInfoProfile.getContactInfoEntity().getContactType())))
                 {
                     roseContactInfoProfileListForWebContact.add(aRoseContactInfoProfile);
                     filter.add(aRoseContactInfoProfile.getContactInfoEntity().getContactInfo());
                 }
            }
            
            RoseContactInfoProfile aRoseContactInfoProfile;
            G01ContactInfo aG01ContactInfo;
            if (ZcaValidator.isNotNullEmpty(email) && (!filter.contains(email))){
                aG01ContactInfo = new G01ContactInfo();
                aG01ContactInfo.setContactType(GardenContactType.EMAIL.value());
                aG01ContactInfo.setContactInfo(email);
                aRoseContactInfoProfile = new RoseContactInfoProfile();
                aRoseContactInfoProfile.setContactInfoEntity(aG01ContactInfo);
                roseContactInfoProfileListForWebContact.add(aRoseContactInfoProfile);
            }
            if (ZcaValidator.isNotNullEmpty(mobilePhone) && (!filter.contains(mobilePhone))){
                aG01ContactInfo = new G01ContactInfo();
                aG01ContactInfo.setContactType(GardenContactType.MOBILE_PHONE.value());
                aG01ContactInfo.setContactInfo(mobilePhone);
                aRoseContactInfoProfile = new RoseContactInfoProfile();
                aRoseContactInfoProfile.setContactInfoEntity(aG01ContactInfo);
                roseContactInfoProfileListForWebContact.add(aRoseContactInfoProfile);
            }
        
        }
        
        return roseContactInfoProfileListForWebContact;
    }

    @Override
    public String getTargetPersonUuid() {
        return this.getUserEntity().getUserUuid();
    }
    
    @Override
    public void deleteLocationDataEntry(String locationUuid){
        List<RoseLocationProfile> aRoseLocationProfileList = getUserLocationProfileList();
        Integer index = null;
        for (int i = 0; i < aRoseLocationProfileList.size(); i++){
            if (locationUuid.equalsIgnoreCase(aRoseLocationProfileList.get(i).getLocationEntity().getLocationUuid())){
                index = i;
                break;
            }
        }
        if (index != null){
            getUserLocationProfileList().remove(index.intValue());
        }
    }

    @Override
    public void addNewLocationDataEntry() {
        List<RoseLocationProfile> aRoseLocationProfileList = getUserLocationProfileList();
            
        RoseLocationProfile aRoseLocationProfile = new RoseLocationProfile();
        aRoseLocationProfile.getLocationEntity().setLocationUuid(GardenData.generateUUIDString());
        aRoseLocationProfile.getLocationEntity().setPreferencePriority(aRoseLocationProfileList.size());
        
        aRoseLocationProfileList.add(aRoseLocationProfile);
    }
    
    @Override
    public void deleteContactInfoDataEntry(String contactInfoUuid){
        List<RoseContactInfoProfile> aRoseContactInfoProfileList = getUserContactInfoProfileList();
        Integer index = null;
        for (int i = 0; i < aRoseContactInfoProfileList.size(); i++){
            if (contactInfoUuid.equalsIgnoreCase(aRoseContactInfoProfileList.get(i).getContactInfoEntity().getContactInfoUuid())){
                index = i;
                break;
            }
        }
        if (index != null){
            getUserContactInfoProfileList().remove(index.intValue());
        }
    }

    @Override
    public void addNewContactInfoDataEntry() {
        List<RoseContactInfoProfile> aRoseContactInfoProfileList = getUserContactInfoProfileList();
        
        RoseContactInfoProfile aRoseContactInfoProfile = new RoseContactInfoProfile();
        aRoseContactInfoProfile.getContactInfoEntity().setContactInfoUuid(GardenData.generateUUIDString());
        aRoseContactInfoProfile.getContactInfoEntity().setPreferencePriority(aRoseContactInfoProfileList.size());
        
        aRoseContactInfoProfileList.add(aRoseContactInfoProfile);
    }

    public String retrieveContactInfo(GardenContactType type) {
        if (type == null){
            return null;
        }
        List<RoseContactInfoProfile> aRoseContactInfoProfileList = getUserContactInfoProfileList();
        for (RoseContactInfoProfile aRoseContactInfoProfile : aRoseContactInfoProfileList){
            if (type.value().equalsIgnoreCase(aRoseContactInfoProfile.getContactInfoEntity().getContactType())){
                return aRoseContactInfoProfile.getContactInfoEntity().getContactInfo();
            }
        }
        return null;
    }

    public String getEmail() {
        if (ZcaValidator.isNullEmpty(email)){
            return retrieveContactInfo(GardenContactType.EMAIL);
        }
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHomePhone() {
        if (ZcaValidator.isNullEmpty(homePhone)){
            return retrieveContactInfo(GardenContactType.HOME_PHONE);
        }
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getMobilePhone() {
        if (ZcaValidator.isNullEmpty(mobilePhone)){
            return retrieveContactInfo(GardenContactType.MOBILE_PHONE);
        }
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getWorkPhone() {
        if (ZcaValidator.isNullEmpty(workPhone)){
            return retrieveContactInfo(GardenContactType.WORK_PHONE);
        }
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getFax() {
        if (ZcaValidator.isNullEmpty(fax)){
            return retrieveContactInfo(GardenContactType.FAX);
        }
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getWeChat() {
        if (ZcaValidator.isNullEmpty(weChat)){
            return retrieveContactInfo(GardenContactType.WECHAT);
        }
        return fax;
    }

    public void setWeChat(String weChat) {
        this.weChat = weChat;
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof RoseUserProfile)){
            return;
        }
        RoseUserProfile srcRoseUserProfile = (RoseUserProfile)srcProfile;
        
        G01DataUpdaterFactory.getSingleton().getG01UserUpdater().cloneEntity(srcRoseUserProfile.getUserEntity(), this.getUserEntity());
        
        List<RoseContactInfoProfile> srcRoseContactInfoProfileList = srcRoseUserProfile.getUserContactInfoProfileList();
        this.getUserContactInfoProfileList().clear();
        RoseContactInfoProfile destRoseContactInfoProfile;
        for (RoseContactInfoProfile srcRoseContactInfoProfile : srcRoseContactInfoProfileList){
            destRoseContactInfoProfile = new RoseContactInfoProfile();
            destRoseContactInfoProfile.cloneProfile(srcRoseContactInfoProfile);
            getUserContactInfoProfileList().add(destRoseContactInfoProfile);
        }
        
        List<RoseLocationProfile> srcRoseLocationProfileList = srcRoseUserProfile.getUserLocationProfileList();
        this.getUserLocationProfileList().clear();
        RoseLocationProfile destRoseLocationProfile;
        for (RoseLocationProfile srcRoseLocationProfile : srcRoseLocationProfileList){
            destRoseLocationProfile = new RoseLocationProfile();
            destRoseLocationProfile.cloneProfile(srcRoseLocationProfile);
            getUserLocationProfileList().add(destRoseLocationProfile);
        }
    }

    @Override
    public String getProfileName() {
        String name = RoseDataAgent.retrieveUserName(userEntity);
        if (ZcaValidator.isNullEmpty(name)){
            name = "Unknown";
        }
        return name;
    }

    @Override
    public String getProfileDescriptiveName() {
        String descriptiveName = getProfileName();
        if (userContactInfoProfileList != null){
            for (RoseContactInfoProfile aRoseContactInfoProfile : userContactInfoProfileList){
                descriptiveName +=" <" + aRoseContactInfoProfile.getContactInfoEntity().getContactType() 
                        + ": " + aRoseContactInfoProfile.getContactInfoEntity().getContactInfo() + ">";
            }
        }
        return descriptiveName;
    }

    /**
     * if 
     * @return 
     */
    @Override
    protected String getProfileUuid() {
        if (userEntity == null){
            return this.toString();
        }else{
            return userEntity.getUserUuid();
        }
    }

    public G01User getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(G01User userEntity) {
        this.userEntity = userEntity;
    }
    
    public RoseContactInfoProfile getPrimaryContactInfoProfile(){
        if (getUserContactInfoProfileList().isEmpty()){
            return null;
        }
        return getUserContactInfoProfileList().get(0);
    }

    public List<RoseContactInfoProfile> getUserContactInfoProfileList() {
        if (userContactInfoProfileList == null){
            userContactInfoProfileList = new ArrayList<>();
        }
        return userContactInfoProfileList;
    }

    public void setUserContactInfoProfileList(List<RoseContactInfoProfile> userContactInfoProfileList) {
        this.userContactInfoProfileList = userContactInfoProfileList;
    }
    
    public RoseLocationProfile getPrimaryLocationProfile(){
        return getUserLocationProfileList().get(0);
    }

    public List<RoseLocationProfile> getUserLocationProfileList() {
        return userLocationProfileList;
    }

    public void setUserLocationProfileList(List<RoseLocationProfile> userLocationProfileList) {
        this.userLocationProfileList = userLocationProfileList;
    }
    
}
