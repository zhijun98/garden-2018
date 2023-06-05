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

import com.zcomapproach.garden.guard.SecurityQuestion;
import com.zcomapproach.garden.persistence.entity.G01SecurityQna;
import com.zcomapproach.garden.persistence.entity.G01SecurityQnaPK;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;

/**
 *
 * @author zhijun98
 */
public class SecurityQnaProfile extends AbstractRoseEntityProfile{
    
    private G01SecurityQna securityQnaEntity;

    private SecurityQuestion securityQuestion;
    
    public SecurityQnaProfile() {
        this.securityQuestion = SecurityQuestion.UNKNOWN;
        this.securityQnaEntity = new G01SecurityQna();
        G01SecurityQnaPK pkid = new G01SecurityQnaPK();
        pkid.setSequrityQuestionCode(securityQuestion.name());
        this.securityQnaEntity.setG01SecurityQnaPK(pkid);
    }

    public SecurityQnaProfile(SecurityQuestion aSecurityQuestion) {
        this.securityQuestion = aSecurityQuestion;
        this.securityQnaEntity = new G01SecurityQna();
        G01SecurityQnaPK pkid = new G01SecurityQnaPK();
        pkid.setSequrityQuestionCode(aSecurityQuestion.name());
        this.securityQnaEntity.setG01SecurityQnaPK(pkid);
        
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof SecurityQnaProfile)){
            return;
        }
        SecurityQnaProfile srcSecurityQnaProfile = (SecurityQnaProfile)srcProfile;
        
        G01DataUpdaterFactory.getSingleton().getG01SecurityQnaUpdater().cloneEntity(srcSecurityQnaProfile.getSecurityQnaEntity(), this.getSecurityQnaEntity());
        this.setSecurityQuestion(srcSecurityQnaProfile.getSecurityQuestion());
    }

    @Override
    public String getProfileName() {
        return securityQuestion.name();
    }

    @Override
    public String getProfileDescriptiveName() {
        return getProfileName() + ": " + securityQuestion.value();
    }

    @Override
    protected String getProfileUuid() {
        return securityQnaEntity.getG01SecurityQnaPK().getAccountUuid() + "::" + securityQnaEntity.getG01SecurityQnaPK().getSequrityQuestionCode();
    }

    public SecurityQuestion getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(SecurityQuestion aSecurityQuestion) {
        this.securityQuestion = aSecurityQuestion;
    }

    public G01SecurityQna getSecurityQnaEntity() {
        return securityQnaEntity;
    }
    
    /**
     * This method use of the entity to initialize the entire profile with reconcilation among data members
     * @param securityQnaEntity 
     */
    public void initializeSecurityQnaProfile(G01SecurityQna securityQnaEntity){
        if (securityQnaEntity != null){
            securityQuestion = SecurityQuestion.convertEnumNameToType(securityQnaEntity.getG01SecurityQnaPK().getSequrityQuestionCode());
        }
        this.securityQnaEntity = securityQnaEntity;
    }

    public void setSecurityQnaEntity(G01SecurityQna securityQnaEntity) {
        initializeSecurityQnaProfile(securityQnaEntity);
    }

    public String getSecurityQuestionValue() {
        return securityQuestion.value();
    }

    public String getSecurityQuestionName() {
        return securityQuestion.name();
    }

    /**
     * Reconcile the internal data members so as to be ready for persistent operations. It will set UUID if there is no one
     * @param accountUuid 
     */
    public void reconcile(String accountUuid) {
        securityQnaEntity.getG01SecurityQnaPK().setAccountUuid(accountUuid);
        securityQnaEntity.getG01SecurityQnaPK().setSequrityQuestionCode(securityQuestion.name());
    }

}
