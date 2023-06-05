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

import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.garden.data.GardenFlower;
import com.zcomapproach.garden.persistence.GardenJpaUtils;
import com.zcomapproach.garden.persistence.constant.GardenAccountStatus;
import com.zcomapproach.garden.persistence.entity.G02TulipMessage;
import com.zcomapproach.garden.persistence.entity.G02TulipNotification;
import com.zcomapproach.garden.persistence.entity.G02TulipUser;
import com.zcomapproach.garden.persistence.updater.G02DataUpdaterFactory;
import com.zcomapproach.garden.rose.rest.tulip.data.TulipUser;
import com.zcomapproach.garden.rose.rest.tulip.data.TulipUserMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
public class TulipServiceEJB02 extends AbstractDataServiceEJB02{
    
    /**
     * 
     * @param aTulipUser
     * @return
     * @throws Exception 
     */
    public TulipUser registerTulipUserAccount(TulipUser aTulipUser) throws Exception{
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        G02TulipUser aG02TulipUser = null;
        try {
            utx.begin();
            em = getEntityManager();

            aG02TulipUser = GardenJpaUtils.findTulipUserByUsername(em, aTulipUser.getUsername());
            //check if the username has been used.
            if (aG02TulipUser == null){
                aG02TulipUser = new G02TulipUser();
                aG02TulipUser.setConfirmCode(ZcaUtils.generateRandomCode(6).toUpperCase()); //confirm code for new user
                aG02TulipUser.setMobile(aTulipUser.getMobileNumber());
                aG02TulipUser.setPassword(aTulipUser.getPassword());
                aG02TulipUser.setTulipVersion(aTulipUser.getTulipVersion());
                aG02TulipUser.setUsername(aTulipUser.getUsername());
                aG02TulipUser.setTulipUserUuid(ZcaUtils.generateUUIDString());
                aG02TulipUser.setStatus(GardenAccountStatus.Suspend.name());
                GardenJpaUtils.storeEntity(em, G02TulipUser.class, 
                                        aG02TulipUser, 
                                        aG02TulipUser.getTulipUserUuid(), 
                                        G02DataUpdaterFactory.getSingleton().getG02TulipUserUpdater());
            }else{
                aTulipUser.setErrorMsg("Please change a different username because it has been used by others.");
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                //throw ex1;
                aTulipUser.setErrorMsg("Sorry, cannot save user data. Please try it later.");
            }
            aTulipUser.setErrorMsg("Sorry, cannot save user data. " + ex.getMessage());
            aG02TulipUser = null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        if ((!aTulipUser.isErrorRaised()) && (aG02TulipUser != null)){
            aTulipUser.setConfirmCode(aG02TulipUser.getConfirmCode());
            aTulipUser.setUuid(aG02TulipUser.getTulipUserUuid());
        }
        return aTulipUser;
    }

    public TulipUser confirmTulipUserAccount(String uuid, String confirmCode) throws Exception{
        TulipUser result = new TulipUser();
        result.setUuid(uuid);
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        G02TulipUser aG02TulipUser = null;
        try {
            utx.begin();
            em = getEntityManager();

            aG02TulipUser = GardenJpaUtils.findById(em, G02TulipUser.class, uuid);
            //check if the username has been used.
            if ((aG02TulipUser == null) || (ZcaValidator.isNullEmpty(aG02TulipUser.getConfirmCode()))){
                result.setErrorMsg("Cannot find your registration.");
            }else{
                if (aG02TulipUser.getConfirmCode().equalsIgnoreCase(confirmCode)){
                    aG02TulipUser.setConfirmCode(null);
                    aG02TulipUser.setStatus(GardenAccountStatus.Valid.name());
                    GardenJpaUtils.storeEntity(em, G02TulipUser.class, 
                                        aG02TulipUser, 
                                        aG02TulipUser.getTulipUserUuid(), 
                                        G02DataUpdaterFactory.getSingleton().getG02TulipUserUpdater());
                    result.setUsername(aG02TulipUser.getUsername());
                    result.setMobileNumber(aG02TulipUser.getMobile());
                    result.setConfirmCode("");
                }else{
                    result.setErrorMsg("Your confirm-code was wrong.");
                }
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                //throw ex1;
                result.setErrorMsg("Sorry, cannot confirm your registration. Please try it later.");
            }
            result.setErrorMsg("Sorry, cannot confirm your registration. " + ex.getMessage());
            aG02TulipUser = null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        if ((!result.isErrorRaised()) && (aG02TulipUser != null)){
            result.setConfirmCode(aG02TulipUser.getConfirmCode());
            result.setUuid(aG02TulipUser.getTulipUserUuid());
        }
        return result;
    }

    public TulipUser loginTulipUserAccount(String username, String password) throws Exception{
        TulipUser result = new TulipUser();
        result.setUsername(password);
        result.setPassword(username);
        //store...        
        EntityManager em = null;
        G02TulipUser aG02TulipUser = null;
        try {
            em = getEntityManager();

            aG02TulipUser = GardenJpaUtils.findTulipUserByUsername(em, username);
            //check if the username has been used.
            if ((aG02TulipUser == null) || (!aG02TulipUser.getPassword().equalsIgnoreCase(password))){
                result.setErrorMsg("Your credential is wrong.");
            }else{
                result.setMobileNumber(aG02TulipUser.getMobile());
                result.setConfirmCode(aG02TulipUser.getConfirmCode());
                result.setUuid(aG02TulipUser.getTulipUserUuid());
                result.setErrorMsg("");
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        if ((!result.isErrorRaised()) && (aG02TulipUser != null)){
            result.setConfirmCode(aG02TulipUser.getConfirmCode());
            result.setUuid(aG02TulipUser.getTulipUserUuid());
        }
        return result;
    }

    public List<String> retrieveNotifications(String contentReceiverUuid) {
        List<String> result = new ArrayList<>();
        EntityManager em = getEntityManager();
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("contentReceiverUuid", contentReceiverUuid);
            List<G02TulipNotification> aG02TulipNotificationList = GardenJpaUtils.findEntityListByNamedQuery(em, G02TulipNotification.class, "G02TulipNotification.findByContentReceiverUuid", params);
            
            Collections.sort(aG02TulipNotificationList, new Comparator<G02TulipNotification>(){
                @Override
                public int compare(G02TulipNotification o1, G02TulipNotification o2) {
                    try{
                        return o1.getCreated().compareTo(o2.getCreated());
                    }catch (Exception ex){
                        return 0;
                    }
                }
            });
            
            for (G02TulipNotification aG02TulipNotification : aG02TulipNotificationList){
                result.add("[" + ZcaCalendar.convertToMMddyyyyHHmmss(aG02TulipNotification.getCreated(), "-", " @", ":") + "]"
                        + aG02TulipNotification.getContent());
            }
        } finally {
            em.close();
        }
        return result;
    }

    public TulipUserMessage storeTulipUserMessage(TulipUserMessage tulipUserMessage) throws Exception{
        if (tulipUserMessage == null){
            return null;
        }
        if ((ZcaValidator.isNullEmpty(tulipUserMessage.getUserUuid()))
                || (ZcaValidator.isNullEmpty(tulipUserMessage.getUserName()))
                || (ZcaValidator.isNullEmpty(tulipUserMessage.getMessage())))
        {
            tulipUserMessage.setErrorMsg("Invalid data in the message. Cannot post this message.");
            return tulipUserMessage;
        }
        tulipUserMessage.setErrorMsg(null);
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        G02TulipUser aG02TulipUser;
        try {
            utx.begin();
            em = getEntityManager();

            aG02TulipUser = GardenJpaUtils.findById(em, G02TulipUser.class, tulipUserMessage.getUserUuid());
            //check if the username has been used.
            if (aG02TulipUser == null) {
                tulipUserMessage.setErrorMsg("Cannot find your account to post this message.");
            }else{
                G02TulipMessage message = new G02TulipMessage();
                message.setFromName(tulipUserMessage.getUserName());
                message.setMessageFromUuid(tulipUserMessage.getUserUuid());
                message.setMessage(tulipUserMessage.getMessage());
                message.setMessageStatus(GardenFlower.TULIP.name());
                GardenJpaUtils.storeEntity(em, G02TulipMessage.class, message, message.getMessageUuid(), 
                        G02DataUpdaterFactory.getSingleton().getG02TulipMessageUpdater());
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                //throw ex1;
                tulipUserMessage.setErrorMsg("Sorry, cannot post message. Please try it later.");
            }
            tulipUserMessage.setErrorMsg("Sorry, cannot post message. " + ex.getMessage());
            aG02TulipUser = null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return tulipUserMessage;
    }
    
}
