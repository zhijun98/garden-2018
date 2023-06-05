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

package com.zcomapproach.garden.peony.email.data;

import com.zcomapproach.garden.email.ISpamRuleManager;
import com.zcomapproach.garden.email.OnlineEmailBox;
import com.zcomapproach.garden.email.data.GardenEmailFolderName;
import com.zcomapproach.garden.email.data.GoogleEmailProperties;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.persistence.entity.G02Employee;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

/**
 * @deprecated 
 * @author zhijun98
 */
public class GoogleEmailBox extends OnlineEmailBox{

    public GoogleEmailBox(G02Employee currentLoginEmployee, ISpamRuleManager spamRuleManager) {
        super(new GoogleEmailProperties(), currentLoginEmployee, PeonyProperties.getSingleton().getMessageLoadingThreshhold(), spamRuleManager);
    }

    public GoogleEmailBox(String emailBoxTitle, G02Employee currentLoginEmployee, ISpamRuleManager spamRuleManager) {
        super(new GoogleEmailProperties(), emailBoxTitle, currentLoginEmployee, PeonyProperties.getSingleton().getMessageLoadingThreshhold(), spamRuleManager);
    }
    
    @Override
    protected void loadGardenEmailsFromServer(String emailSystemRootPath) throws MessagingException {
        //session
        Session emailSession = Session.getDefaultInstance(getEmailProp(), null);    //shared by SMTP/IMAP/POP3
        //store
        Store emailStore = emailSession.getStore(getGardenImapStoreProtocal());
        try{
            emailStore.connect(getGardenImapServer(), getGardenMailBoxAddess(), getGardenMailBoxPassword());
        }catch (MessagingException ex){
            //sometimes, it may fail to connect because of any connection reasons. e.g. MailConnectException. in this case, simply quietly return
            return;
        }
        //folder - inbox
        Folder emailFolder = emailStore.getFolder("Inbox");
        if (emailFolder == null){
            return;
        }
        //loading the existing emails in the mail box
        int newMessageCount;
        Message[] messages;
        if (!isClosingGardenEmailBox()){
            try {
                emailFolder.open(Folder.READ_ONLY);
                messages = emailFolder.getMessages();
                if (messages != null){
                    int loadedMessageCount = getGardenEmailFolder(GardenEmailFolderName.convertFromServerFolderNameToGardenFolderName(emailFolder.getFullName()))
                            .getSerializedMessageCount();
                    newMessageCount = messages.length - loadedMessageCount;
                    if (newMessageCount > 0){
                        parseReceivedEmailMessages(messages, newMessageCount, 
                                getCurrentLoginEmployee().getEmployeeAccountUuid(), 
                                getGardenMailBoxAddess(), 
                                emailSystemRootPath, loadedMessageCount==0);
                    }
                }
            } catch (MessagingException ex) {
                Logger.getLogger(OnlineEmailBox.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        emailFolder.close(true);
        emailStore.close();
    }
}
