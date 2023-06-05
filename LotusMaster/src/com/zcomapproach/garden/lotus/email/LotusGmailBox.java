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

package com.zcomapproach.garden.lotus.email;

import com.zcomapproach.garden.email.ISpamRuleManager;
import com.zcomapproach.garden.email.OnlineEmailBox;
import com.zcomapproach.garden.persistence.entity.G02Employee;
import java.util.Properties;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

/**
 *
 * @author zhijun98
 */
public class LotusGmailBox extends OnlineEmailBox{

    private static final Logger logger = Logger.getLogger(LotusGmailBox.class.getName());

    /**
     * Self-discipline: do not change the items in skipMsgIds by LotusGmailBox
     */
    private final ConcurrentSkipListSet<Long> skipMsgIds;
    
    public LotusGmailBox(ConcurrentSkipListSet<Long> skipMsgIds, Properties emailProp, G02Employee currentLoginEmployee, int message_loading_threshold, ISpamRuleManager spamRuleManager) {
        super(emailProp, currentLoginEmployee, message_loading_threshold, spamRuleManager);
        this.skipMsgIds = skipMsgIds;
    }

    public LotusGmailBox(ConcurrentSkipListSet<Long> skipMsgIds, Properties emailProp, String emailBoxTitle, G02Employee currentLoginEmployee, int message_loading_thresh_hold, ISpamRuleManager spamRuleManager) {
        super(emailProp, emailBoxTitle, currentLoginEmployee, message_loading_thresh_hold, spamRuleManager);
        this.skipMsgIds = skipMsgIds;
    }
    
    public void retrieveGmailsFromGoogle(String emailSystemRootPath) throws MessagingException {
        if (isParsingEmailMessagesStopped()){
            return;
        }
        //session
        Session emailSession = Session.getDefaultInstance(getEmailProp(), null);    //shared by SMTP/IMAP/POP3
        //store
        Store emailStore = emailSession.getStore(getGardenImapStoreProtocal());
        //connecting
        logger.log(Level.INFO, "Connecting to gmail server for message retrieval...");
        emailStore.connect(getGardenImapServer(), getGardenMailBoxAddess(), getGardenMailBoxPassword());
        //folder - inbox
        Folder emailFolder = emailStore.getFolder("Inbox");
        if (emailFolder == null){
            return;
        }
        logger.log(Level.INFO, "Open gmail inbox folder for message retrieval...");
        //loading the existing emails in the mail box
////        int newMessageCount;
        Message[] messages;
        if (!isClosingGardenEmailBox()){
            try {
                emailFolder.open(Folder.READ_ONLY);
                messages = emailFolder.getMessages();
                logger.log(Level.INFO, "Retrieve mime-messages from the mail folder...");
                if (messages != null){
////                    int loadedMessageCount = getGardenEmailFolder(GardenEmailFolderName.convertFromServerFolderNameToGardenFolderName(emailFolder.getFullName()))
////                            .getSerializedMessageCount();
////                    newMessageCount = messages.length - loadedMessageCount;
////                    if (newMessageCount > 0){
                        Logger.getLogger(LotusGmailBox.class.getName()).log(Level.INFO, "Parsing retrieved all the Gmail messages from Google...");
                        parseReceivedEmailMessages(messages, super.getMessageLoadingThreshold(), 
                                getCurrentLoginEmployee().getEmployeeAccountUuid(), 
                                getGardenMailBoxAddess(), 
                                emailSystemRootPath, false, skipMsgIds);
////                    }
                }
            } catch (MessagingException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        
        emailFolder.close(true);
        emailStore.close();
    }
    
}
