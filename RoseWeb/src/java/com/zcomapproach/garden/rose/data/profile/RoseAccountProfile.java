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

import com.zcomapproach.commons.ZcaText;
import com.zcomapproach.garden.guard.SecurityQuestion;
import com.zcomapproach.garden.persistence.constant.GardenAccountStatus;
import com.zcomapproach.garden.persistence.constant.GardenPrivilege;
import com.zcomapproach.garden.persistence.entity.G01Account;
import com.zcomapproach.garden.persistence.entity.G01AuthorizedPrivilege;
import com.zcomapproach.garden.persistence.entity.G01XmppAccount;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.garden.rose.util.RoseDataAgent;
import com.zcomapproach.commons.ZcaValidator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author zhijun98
 */
public class RoseAccountProfile extends AbstractRoseEntityProfile implements IRoseAccountUserEntityProfile{
    
    public static final int TOTAL_OF_QnAs = 3;
    
    /**
     * Login or not
     */
    private boolean authenticated;
    
    private boolean xmppDisabled;
    
    private G01Account accountEntity;
    
    private G01XmppAccount xmppAccountEntity;
    
    private RoseUserProfile userProfile;
    /**
     * This user's authorized GardenPrivilege storage.
     */
    private final HashMap<GardenPrivilege, G01AuthorizedPrivilege> authorizedPrivilegeStorage;
    
    /**
     * user's security-QnA for security confirmation
     */
    private SecurityQnaProfile securityQnaProfile01;
    private SecurityQnaProfile securityQnaProfile02;
    private SecurityQnaProfile securityQnaProfile03;

    public RoseAccountProfile() {
        this.authenticated = false;
        this.xmppDisabled = false;
        this.accountEntity = new G01Account();
        this.xmppAccountEntity = new G01XmppAccount();
        this.userProfile = new RoseUserProfile();
        authorizedPrivilegeStorage = new HashMap<>();
        securityQnaProfile01 = new SecurityQnaProfile();
        securityQnaProfile02 = new SecurityQnaProfile();
        securityQnaProfile03 = new SecurityQnaProfile();
    }

    public boolean isXmppDisabled() {
        return xmppDisabled;
    }

    public void setXmppDisabled(boolean xmppDisabled) {
        this.xmppDisabled = xmppDisabled;
    }

    @Override
    public String getTargetPersonUuid() {
        return this.getAccountEntity().getAccountUuid();
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof RoseAccountProfile)){
            return;
        }
        RoseAccountProfile srcRoseAccountProfile = (RoseAccountProfile)srcProfile;
        
        //account
        G01DataUpdaterFactory.getSingleton().getG01AccountUpdater().cloneEntity(srcRoseAccountProfile.getAccountEntity(), this.getAccountEntity());
        //xmpp-account
        G01DataUpdaterFactory.getSingleton().getG01XmppAccountUpdater().cloneEntity(srcRoseAccountProfile.getXmppAccountEntity(), this.getXmppAccountEntity());
        //privileges
        List<G01AuthorizedPrivilege> srcG01AuthorizedPrivilegeList = srcRoseAccountProfile.getAuthorizedPrivilegeList();
        this.getAuthorizedPrivilegeList().clear();
        G01AuthorizedPrivilege destG01AuthorizedPrivilege;
        for (G01AuthorizedPrivilege srcG01AuthorizedPrivilege : srcG01AuthorizedPrivilegeList){
            destG01AuthorizedPrivilege = new G01AuthorizedPrivilege();
            G01DataUpdaterFactory.getSingleton().getG01AuthorizedPrivilegeUpdater().cloneEntity(srcG01AuthorizedPrivilege, destG01AuthorizedPrivilege);
            this.getAuthorizedPrivilegeList().add(destG01AuthorizedPrivilege);
        }
        //user profile
        this.getUserProfile().cloneProfile(srcRoseAccountProfile.getUserProfile());
        //security QnA
        this.getSecurityQnaProfile01().cloneProfile(srcRoseAccountProfile.getSecurityQnaProfile01());
        this.getSecurityQnaProfile02().cloneProfile(srcRoseAccountProfile.getSecurityQnaProfile02());
        this.getSecurityQnaProfile03().cloneProfile(srcRoseAccountProfile.getSecurityQnaProfile03());
    }
    
    /**
     * Try to get one of available emails from account, user's contact-info etc in this profile.
     * @return - possibly NULL
     */
    public String retrieveEmail(){
        if (ZcaValidator.isNullEmpty(accountEntity.getAccountEmail())){
            return userProfile.getEmail();
        }
        return accountEntity.getAccountEmail();
    }

    public String retireveHomePhone() {
        return userProfile.getHomePhone();
    }

    public String retireveFax() {
        return userProfile.getFax();
    }

    public String retireveMobilePhone() {
        if (ZcaValidator.isNullEmpty(accountEntity.getMobilePhone())){
            return userProfile.getMobilePhone();
        }
        return accountEntity.getMobilePhone();
    }

    public String retireveWorkPhone() {
        return userProfile.getWorkPhone();
    }

    public SecurityQnaProfile getSecurityQnaProfile01() {
        return securityQnaProfile01;
    }

    public void setSecurityQnaProfile01(SecurityQnaProfile securityQnaProfile01) {
        this.securityQnaProfile01 = securityQnaProfile01;
    }

    public SecurityQnaProfile getSecurityQnaProfile02() {
        return securityQnaProfile02;
    }

    public void setSecurityQnaProfile02(SecurityQnaProfile securityQnaProfile02) {
        this.securityQnaProfile02 = securityQnaProfile02;
    }

    public SecurityQnaProfile getSecurityQnaProfile03() {
        return securityQnaProfile03;
    }

    public void setSecurityQnaProfile03(SecurityQnaProfile securityQnaProfile03) {
        this.securityQnaProfile03 = securityQnaProfile03;
    }
    
    public void loadSecurityQnaProfileList(List<SecurityQnaProfile> aSecurityQnaProfileList) {
        if ((aSecurityQnaProfileList == null) || (aSecurityQnaProfileList.size() < TOTAL_OF_QnAs)){
            return;
        }
        setSecurityQnaProfile01(aSecurityQnaProfileList.get(0));
        setSecurityQnaProfile02(aSecurityQnaProfileList.get(1));
        setSecurityQnaProfile03(aSecurityQnaProfileList.get(2));
    }

    public boolean isAuthorized(RosePrivilegeProfile aRosePrivilegeProfile) {
        return isAuthorized(aRosePrivilegeProfile.getPrivilege());
    }
    
    public boolean isAuthorized(String aGardenPrivilege) {
        return isAuthorized(GardenPrivilege.convertParamValueToType(aGardenPrivilege));
    }
    
    public boolean isAuthorized(GardenPrivilege rosePrivilegeName) {
        if (authorizedPrivilegeStorage.containsKey(GardenPrivilege.SUPER_POWER)){
            return true;
        }
        return authorizedPrivilegeStorage.containsKey(rosePrivilegeName);
    }
    
    public void authorizePrivilege(G01AuthorizedPrivilege authorizedPrivilege){
        authorizedPrivilegeStorage.put(GardenPrivilege.convertParamValueToType(authorizedPrivilege.getG01AuthorizedPrivilegePK().getAuthorizedPrivilegeUuid()), 
                                       authorizedPrivilege);
    }

    public void authorizePrivilegeList(List<G01AuthorizedPrivilege> aGardenAccountPrivilegeList){
        if ((aGardenAccountPrivilegeList != null) && (!aGardenAccountPrivilegeList.isEmpty())){
            authorizedPrivilegeStorage.clear();
            for (G01AuthorizedPrivilege aGardenAccountPrivilege : aGardenAccountPrivilegeList){
                authorizePrivilege(aGardenAccountPrivilege);
            }
        }
    }
    
    public void unauthorizePrivilege(GardenPrivilege aGardenPrivilege){
        authorizedPrivilegeStorage.remove(aGardenPrivilege);
    }

    public void unauthorizeAllPrivileges() {
        authorizedPrivilegeStorage.clear();
    }

    public List<G01AuthorizedPrivilege> getAuthorizedPrivilegeList() {
        return new ArrayList<>(authorizedPrivilegeStorage.values());
    }

    @Override
    public String getProfileName() {
        if (accountEntity == null){
            accountEntity = new G01Account(); 
        }
        String name = ZcaText.denullize(accountEntity.getLoginName());
        if (ZcaValidator.isNullEmpty(name)){
            name = "Unknown";
        }
        return name;
    }

    @Override
    public String getProfileDescriptiveName() {
        String userName = RoseDataAgent.retrieveUserName(userProfile.getUserEntity());
        if (ZcaValidator.isNullEmpty(userName)){
            return getProfileName();
        }else{
            return userName + " <" + getProfileName() + ">";
        }
    }

    @Override
    protected String getProfileUuid() {
        if (accountEntity == null){
            return this.toString();
        }else{
            return accountEntity.getAccountUuid();
        }
    }
    
    public List<SecurityQnaProfile> getAllSecurityQnaProfile(){
        List<SecurityQnaProfile> result = new ArrayList<>();
        List<SecurityQuestion> aSecurityQuestionList = SecurityQuestion.getSecurityQuestionsList(false);
        SecurityQnaProfile aSecurityQnaProfile;
        for (SecurityQuestion aSecurityQuestion : aSecurityQuestionList){
            aSecurityQnaProfile = new SecurityQnaProfile(aSecurityQuestion);
            //the followings are used for comparison
            aSecurityQnaProfile.getSecurityQnaEntity().getG01SecurityQnaPK().setAccountUuid(accountEntity.getAccountUuid());
            aSecurityQnaProfile.getSecurityQnaEntity().getG01SecurityQnaPK().setSequrityQuestionCode(aSecurityQuestion.name());
            result.add(aSecurityQnaProfile);
        }
        return result;
    }

    public G01Account getAccountEntity() {
        return accountEntity;
    }

    public void setAccountEntity(G01Account accountEntity) {
        this.accountEntity = accountEntity;
    }

    public G01XmppAccount getXmppAccountEntity() {
        return xmppAccountEntity;
    }

    public void setXmppAccountEntity(G01XmppAccount xmppAccountEntity) {
        this.xmppAccountEntity = xmppAccountEntity;
    }

    public RoseUserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(RoseUserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    /**
     * NOTICE: if this returns true, it only means this account is in the logged-in mode BUT its status may be not valid. Refer to other 
     * account status methods: isValidAccount, isSuspendedAccount, isOfflineTempAccount, isDuplicatedAccount, and isDisabledAccount
     * @return 
     */
    public boolean isAuthenticated() {
        return authenticated;
    }
    
    /**
     * NOTICE: it simply means account is valid BUT it does not claim this account is authenticated
     * @return 
     */
    public boolean isValidAccount(){
        return GardenAccountStatus.Valid.equals(GardenAccountStatus.convertEnumNameToType(accountEntity.getAccountStatus()));
    }
    
    public boolean isSuspendedAccount(){
        return GardenAccountStatus.Suspend.equals(GardenAccountStatus.convertEnumNameToType(accountEntity.getAccountStatus()));
    }
    
    public boolean isOfflineTempAccount(){
        return GardenAccountStatus.Offline.equals(GardenAccountStatus.convertEnumNameToType(accountEntity.getAccountStatus()));
    }
    
    public boolean isDuplicatedAccount(){
        return GardenAccountStatus.Duplicated.equals(GardenAccountStatus.convertEnumNameToType(accountEntity.getAccountStatus()));
    }
    
    public boolean isDisabledAccount(){
        return GardenAccountStatus.Disabled.equals(GardenAccountStatus.convertEnumNameToType(accountEntity.getAccountStatus()));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.accountEntity);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RoseAccountProfile other = (RoseAccountProfile) obj;
        if (!Objects.equals(this.accountEntity, other.accountEntity)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "RoseAccountProfile{" + "accountEntity=" + accountEntity + '}';
    }

}
