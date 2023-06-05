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

import com.zcomapproach.garden.persistence.entity.G01Payment;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import java.util.Date;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author zhijun98
 */
public class BusinessCasePaymentProfile extends AbstractRoseEntityProfile {
    
    private G01Payment paymentEntity;

    private EmployeeAccountProfile agent;
    
    public BusinessCasePaymentProfile() {
        this.paymentEntity = new G01Payment();
    }

    public EmployeeAccountProfile getAgent() {
        return agent;
    }

    public void setAgent(EmployeeAccountProfile agent) {
        this.agent = agent;
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof BusinessCasePaymentProfile)){
            return;
        }
        BusinessCasePaymentProfile srcRosePaymentProfile = (BusinessCasePaymentProfile)srcProfile;
        //archivedDocumentEntity
        G01DataUpdaterFactory.getSingleton().getG01PaymentUpdater().cloneEntity(srcRosePaymentProfile.getPaymentEntity(), 
                                                                                                this.getPaymentEntity());
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
        return paymentEntity.getPaymentUuid();
    }

    public G01Payment getPaymentEntity() {
        return paymentEntity;
    }

    public void setPaymentEntity(G01Payment paymentEntity) {
        this.paymentEntity = paymentEntity;
    }

}
