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

package com.zcomapproach.garden.lotus.agent;

import com.zcomapproach.garden.data.GardenFlower;
import com.zcomapproach.garden.email.GardenEmailMessage;
import com.zcomapproach.garden.lotus.email.LotusGmailBox;
import com.zcomapproach.garden.email.OnlineEmailBoxListener;
import com.zcomapproach.garden.email.data.GoogleEmailProperties;
import com.zcomapproach.garden.guard.RoseWebCipher;
import com.zcomapproach.garden.guard.TechnicalController;
import com.zcomapproach.garden.lotus.LotusProperties;
import com.zcomapproach.garden.lotus.persistence.LotusGardenStorage;
import com.zcomapproach.garden.persistence.entity.G02Account;
import com.zcomapproach.garden.persistence.entity.G02Employee;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmail;
import com.zcomapproach.garden.util.GardenEnvironment;
import com.zcomapproach.garden.util.GardenThreadingUtils;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zhijun98
 */
public class LotusGmailReceiver extends LotusAgent implements OnlineEmailBoxListener {

    private static final Logger logger = Logger.getLogger(LotusGmailReceiver.class.getName());

    private static volatile LotusGmailReceiver self = null;
    public static LotusGmailReceiver getSingleton() {
        LotusGmailReceiver selfLocal = LotusGmailReceiver.self;
        if (selfLocal == null){
            synchronized (LotusGmailReceiver.class) {
                selfLocal = LotusGmailReceiver.self;
                if (selfLocal == null){
                    LotusGmailReceiver.self = selfLocal = new LotusGmailReceiver();
                }
            }
        }
        return selfLocal;
    }

    private final ExecutorService retrievingEmailServiceSingleThreadPool;

    private final LotusGmailBox lotusGmailBox;
    
    private final G02Employee technicalController;
    private List<G02Account> currentEmployeeAccountList;
    private final ConcurrentSkipListSet<Long> skipHistoricalMsgIds;
    
    public LotusGmailReceiver() {
        retrievingEmailServiceSingleThreadPool = Executors.newSingleThreadExecutor();
        technicalController = TechnicalController.getEmployeeAccount().getEmployeeInfo();
        skipHistoricalMsgIds = LotusGardenStorage.getSingleton().retrieveHistoricalMsgProcessedIds();
        lotusGmailBox = new LotusGmailBox(skipHistoricalMsgIds, new GoogleEmailProperties(), 
        technicalController, LotusProperties.getSingleton().getReceivingMessageLoadingThreshold(), null);
    }

    @Override
    public void start() {
        logger.log(Level.INFO, "Start LotusGmailReceiver...");
        if (currentEmployeeAccountList == null){
            currentEmployeeAccountList = LotusGardenStorage.getSingleton().findCurrentEmployeeAccountList();
            if (currentEmployeeAccountList == null){
                currentEmployeeAccountList = new ArrayList<>();
            }
        }
        //listen to retrievingEmailServiceSingleThreadPool 
        lotusGmailBox.addOnlineEmailBoxListener(LotusGmailReceiver.this);
        retrievingEmailServiceSingleThreadPool.submit(new Runnable(){
            @Override
            public void run() {
                while(LotusProperties.getSingleton().isLotusMasterRunning(true)){
                    try {
                        logger.log(Level.INFO, "Retrieve Gmail messages from Google...");
                        lotusGmailBox.retrieveGmailsFromGoogle(GardenEnvironment.getServerSideGmailRootPath(GardenFlower.PEONY).toAbsolutePath().toString());
                        logger.log(Level.INFO, "Sleep for a while after gmail retrieval...");
                        if (LotusProperties.getSingleton().isLotusMasterRunning(false)){
                            Thread.sleep(1000*45);  //load emails every 45 seconds
                        }else{
                            break;
                        }
                    } catch (Exception ex) {
                        logger.log(Level.SEVERE, ex.getMessage(), ex);
                        break;
                    }
                }//while-loop
                logger.log(Level.INFO, "Completed the loop of retrieving Gmail messages from Google...");
            }
        });
    }

    @Override
    public void stop() {
        lotusGmailBox.setParsingEmailMessagesStopped(true);
        logger.log(Level.INFO, "Stop LotusGmailReceiver...");
        GardenThreadingUtils.shutdownExecutorService(retrievingEmailServiceSingleThreadPool);
    }
    
    /**
     * Publish the received messages into the garden database so that the remote offline clients may retrieve their own offline messages
     * @param aGardenEmailMessage 
     */
    private void publishGmailMessageReceived(GardenEmailMessage aGardenEmailMessage) {
        if (aGardenEmailMessage == null){
            return;
        }
        String mailFolderFullName = aGardenEmailMessage.getFolderFullName();
        currentEmployeeAccountList.add(TechnicalController.getEmployeeAccount().getAccount());
        Path serverSideGmailRootPath = GardenEnvironment.getServerSideGmailRootPath(GardenFlower.PEONY);
        Path clientSideGmailRootPath;
        List<PeonyOfflineEmail> aPeonyOfflineEmailList = new ArrayList<>();
        for (G02Account aG02Employee : currentEmployeeAccountList){
            if (PeonyEmployee.isTechnicalController(aG02Employee.getAccountUuid())){
                //serverSideGmailRootPath
                aPeonyOfflineEmailList.add(PeonyOfflineEmail.createPeonyOfflineEmailInstance(aG02Employee.getAccountUuid(), 
                        lotusGmailBox.getGardenMailBoxAddess(), aGardenEmailMessage, mailFolderFullName, serverSideGmailRootPath, false));
            }else{
                //clientSideGmailRootPath
                clientSideGmailRootPath = LotusGardenStorage.getSingleton().getPeonyClientSideGmailRootPath(aG02Employee.getLoginName(), 
                        RoseWebCipher.getSingleton().decrypt(aG02Employee.getEncryptedPassword()));
                if (clientSideGmailRootPath != null){
                    aPeonyOfflineEmailList.add(PeonyOfflineEmail.createPeonyOfflineEmailInstance(aG02Employee.getAccountUuid(), 
                            lotusGmailBox.getGardenMailBoxAddess(), aGardenEmailMessage, mailFolderFullName, clientSideGmailRootPath, true));
                }
            }
        }//for-loop
        LotusGardenStorage.getSingleton().publishPeonyGmailListRetrieved(aPeonyOfflineEmailList);
        try{
            skipHistoricalMsgIds.add(Long.parseLong(aGardenEmailMessage.getEmailMsgUid()));
        }catch (Exception ex){
            logger.log(Level.WARNING, 
                    "Cannot add msg-ID into skipHistoricalMsgIds...the msg-ID is {0}", aGardenEmailMessage.getEmailMsgUid());
        }
    }

    @Override
    public void gardenEmailMessageReceived(GardenEmailMessage aGardenEmailMessage) {
        publishGmailMessageReceived(aGardenEmailMessage);
    }

    @Override
    public void gardenEmailMessageLoaded(GardenEmailMessage aGardenEmailMessage) {
        publishGmailMessageReceived(aGardenEmailMessage);
    }

    @Override
    public void gardenEmailMessageSent(GardenEmailMessage aGardenEmailMessage) {
        //nothing to do here in this case
    }

    @Override
    public void gardenEmailMessageFlagged(GardenEmailMessage aGardenEmailMessage) {
        //nothing to do here in this case
    }

    @Override
    public void publishProcessingStatus(String status) {
        //nothing to do here in this case
    }
}
