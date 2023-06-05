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

package com.zcomapproach.garden.rose.persistence;

import com.zcomapproach.garden.data.constant.SearchUserCriteria;
import com.zcomapproach.garden.persistence.GardenJpaUtils;
import com.zcomapproach.garden.persistence.entity.G02Account;
import com.zcomapproach.garden.persistence.entity.G02User;
import com.zcomapproach.garden.persistence.peony.PeonyAccount;
import com.zcomapproach.garden.persistence.peony.data.CustomerSearchResult;
import com.zcomapproach.garden.persistence.peony.data.CustomerSearchResultList;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import static com.zcomapproach.garden.persistence.GardenJpaUtils.findById;
import com.zcomapproach.garden.persistence.entity.G02TulipNotification;
import com.zcomapproach.garden.persistence.entity.G02TulipUser;
import com.zcomapproach.garden.persistence.entity.G02XmppAccount;
import com.zcomapproach.garden.persistence.peony.PeonyTulipUser;
import com.zcomapproach.garden.persistence.peony.PeonyTulipUserList;
import com.zcomapproach.garden.persistence.updater.G02DataUpdaterFactory;
import com.zcomapproach.garden.util.GardenData;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 *
 * @author zhijun98
 */
@Stateless
public class RoseCustomerEJB02 extends AbstractDataServiceEJB02 {

    public PeonyAccount findCustomerByAccountUuid(String accountUuid) {
        PeonyAccount result = null;

        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.findPeonyAccountByAccountUuid(em, accountUuid);
        } catch (Exception ex) {
            Logger.getLogger(RoseCustomerEJB02.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
        } finally {
            em.close();
        }
        
        return result;
    }

    public CustomerSearchResultList searchCustomerSearchResultListByFeature(String customerFeature, 
                                                                            String customerFeatureValue, 
                                                                            boolean exactMatch) 
    {
        CustomerSearchResultList result = new CustomerSearchResultList();
        result.setCustomerFeature(customerFeature);
        result.setCustomerFeatureValue(customerFeatureValue);
        if (ZcaValidator.isNullEmpty(customerFeature) || ZcaValidator.isNullEmpty(customerFeatureValue)){
            return result;
        }
        EntityManager em = getEntityManager();
        try {
            List<G02Account> aG02AccountList = GardenJpaUtils.findAll(em, G02Account.class);
            if (aG02AccountList != null){
                CustomerSearchResult aCustomerSearchResult;
                String persistentCustomerFeatureValue;
                double score;
                String userUuid;
                for (G02Account aG02Account : aG02AccountList){
                    userUuid = aG02Account.getAccountUuid();
                    aCustomerSearchResult = new CustomerSearchResult();
                    aCustomerSearchResult.setAccount(aG02Account);
                    aCustomerSearchResult.setXmppAccount(findById(em, G02XmppAccount.class, userUuid));
                    aCustomerSearchResult.setUser(GardenJpaUtils.findById(em, G02User.class, userUuid));
                    if (aCustomerSearchResult.getUser() != null){
                        persistentCustomerFeatureValue = retrievePersistentCustomerFeatureValue(customerFeature, aCustomerSearchResult);
                        if (ZcaValidator.isNotNullEmpty(persistentCustomerFeatureValue)){
                            score = GardenData.calculateSimilarityByJaroWinklerStrategy(persistentCustomerFeatureValue, customerFeatureValue, exactMatch, 0.75);
                            if (score > 0.75){
                                aCustomerSearchResult.setContactInfoList(GardenJpaUtils.findContactInfoListByUserUuid(em, userUuid));
                                aCustomerSearchResult.setLocationList(GardenJpaUtils.findLocationListByUserUuid(em, userUuid));
                                aCustomerSearchResult.setTaxcorpCaseBriefList(GardenJpaUtils.findTaxcorpCaseBriefListByUserUuid(em, userUuid));
                                aCustomerSearchResult.setTaxyerCaseBriefList(GardenJpaUtils.findTaxyerCaseBriefListByUserUuid(em, userUuid));
                                aCustomerSearchResult.setNewEntity(false);
                                aCustomerSearchResult.setPrivilegeList(new ArrayList<>());  //customer has no any privileges so far (01/27/2019)
                                result.getCustomerSearchResultList().add(aCustomerSearchResult);
                            }
                        }
                    }
                }//for-loop
            }
        } finally {
            em.close();
        }
        return result;
    }
    
    private String retrievePersistentCustomerFeatureValue(String customerFeature, CustomerSearchResult aCustomerSearchResult) {
        SearchUserCriteria aCritera = SearchUserCriteria.convertEnumValueToType(customerFeature);
        if (aCritera != null){
            switch (aCritera){
                case EMAIL:
                    return aCustomerSearchResult.getAccountEmail();
                case FIRST_NAME:
                    return aCustomerSearchResult.getFirstName();
                case LAST_NAME:
                    return aCustomerSearchResult.getLastName();
                case LOGIN_NAME:
                    return aCustomerSearchResult.getLoginName();
                case PHONE:
                    return aCustomerSearchResult.getMobilePhone();
                case SSN:
                    return aCustomerSearchResult.getSsn();
            }
        }
        return null;
    }

    public PeonyTulipUserList retrieveSimpleCustomerList() {
        PeonyTulipUserList result = new PeonyTulipUserList();
        
        EntityManager em = getEntityManager();
        try {
            List<G02TulipUser> aG02TulipUserList = GardenJpaUtils.findAll(em, G02TulipUser.class);
            if (aG02TulipUserList != null){
                PeonyTulipUser aPeonyTulipUser;
                for (G02TulipUser aG02TulipUser : aG02TulipUserList){
                    aPeonyTulipUser = new PeonyTulipUser();
                    aPeonyTulipUser.setTulipUser(aG02TulipUser);
                    result.getPeonyTulipUserList().add(aPeonyTulipUser);
                }//for-loop
            }
        } finally {
            em.close();
        }
        return result;
    }

    public PeonyTulipUser retrievePeonyTulipUserByUuid(String tulipUserUuid) {
        PeonyTulipUser result = null;
        if (ZcaValidator.isNotNullEmpty(tulipUserUuid)){
            EntityManager em = getEntityManager();
            try {
                G02TulipUser aG02TulipUser = GardenJpaUtils.findById(em, G02TulipUser.class, tulipUserUuid);
                if (aG02TulipUser != null){
                    result = new PeonyTulipUser();
                    result.setTulipUser(aG02TulipUser);
                    result.setMessages(GardenJpaUtils.findTulipMessagesForTulipUser(em, tulipUserUuid));
                    result.setNotifications(GardenJpaUtils.findTulipNotificationsForTulipUser(em, tulipUserUuid));
                    //result.setRelatedEntityList(GardenJpaUtils.findTulipRelatedEntityListForTulipUser(em, tulipUserUuid));
                }
            } finally {
                em.close();
            }
        }//if
        return result;
    }

    public G02TulipNotification storeTulipNotification(G02TulipNotification aG02TulipNotification) throws Exception{
        if (aG02TulipNotification == null){
            return null;
        }
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();

            GardenJpaUtils.storeEntity(em, G02TulipNotification.class, aG02TulipNotification, aG02TulipNotification.getNotificationUuid(), G02DataUpdaterFactory.getSingleton().getG02TulipNotificationUpdater());

            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return aG02TulipNotification;
    }
}
