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

package com.zcomapproach.garden.rose.test.cases;

import com.zcomapproach.garden.email.data.GardenEmailFolderName;
import com.zcomapproach.garden.email.HostMonsterEmailSettings;
import com.zcomapproach.garden.email.rose.RoseEmailUtils;
import com.zcomapproach.garden.rose.test.AbstractRoseTester;
import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;

/**
 *
 * @author zhijun98
 */
public class RoseEmailConnectionTester extends AbstractRoseTester{

    @Override
    protected void launchTestingImpl() throws Exception {
        testConnectionWithSmtp();
        testConnectionWithSmtpSSL();
        testConnectionWithSmtpTLS();
        testConnectionWithPop3();
        testConnectionWithPop3s();
        testConnectionWithImap();
        testConnectionWithImapS();
    }
    
    public static void testConnectionWithSmtp() throws Exception{
        try {
            Session connection = Session.getInstance(RoseEmailUtils.getSmtpProp(), null);
            Transport transport = connection.getTransport("smtp");
            transport.connect(HostMonsterEmailSettings.MailServer.value(), HostMonsterEmailSettings.DeveloperEmail.value(), HostMonsterEmailSettings.Hint.value());
            Thread.sleep(1000);
            transport.close();
        } catch (Exception ex) {
            throw new Exception("Failed SMTP connetion test: " + ex.getMessage());
        }
    
    }
    
    public static void testConnectionWithSmtpSSL() throws Exception{
        try {
            Session connection = Session.getInstance(RoseEmailUtils.getSmtpPropSSL(), null);
            Transport transport = connection.getTransport("smtps");
            transport.connect(HostMonsterEmailSettings.MailServer.value(), HostMonsterEmailSettings.DeveloperEmail.value(), HostMonsterEmailSettings.Hint.value());
            Thread.sleep(1000);
            transport.close();
        } catch (Exception ex) {
            throw new Exception("Failed SMTP-SSL connetion test: " + ex.getMessage());
        }
    
    }
    
    public static void testConnectionWithSmtpTLS() throws Exception{
        try {
            Session connection = Session.getInstance(RoseEmailUtils.getSmtpPropTLS(), null);
            Transport transport = connection.getTransport("smtps");
            transport.connect(HostMonsterEmailSettings.MailServer.value(), HostMonsterEmailSettings.DeveloperEmail.value(), HostMonsterEmailSettings.Hint.value());
            Thread.sleep(1000);
            transport.close();
        } catch (Exception ex) {
            throw new Exception("Failed SMTP-TLS connetion test: " + ex.getMessage());
        }
    
    }
    
    public static void testConnectionWithPop3() throws Exception{
        try {
            //create properties field 
            Session emailSession = Session.getDefaultInstance(RoseEmailUtils.getPop3Prop());
            //create the POP3 store object and connect with the pop server 
            Store store = emailSession.getStore("pop3"); 
            store.connect(HostMonsterEmailSettings.MailServer.value(), HostMonsterEmailSettings.DeveloperEmail.value(), HostMonsterEmailSettings.Hint.value()); 
            //create the folder object and open it 
            Folder emailFolder = store.getFolder(GardenEmailFolderName.GARDEN_INBOX.value()); 
            emailFolder.open(Folder.READ_ONLY);
            Thread.sleep(1000);
            //close the store and folder objects 
            emailFolder.close(false); 
            store.close(); 
        } catch (Exception ex) {
            throw new Exception("Failed POP3 connetion test: " + ex.getMessage());
        }
    }
    
    public static void testConnectionWithPop3s() throws Exception{
        try {
            //create properties field 
            Session emailSession = Session.getDefaultInstance(RoseEmailUtils.getPop3PropS());
            //create the POP3 store object and connect with the pop server 
            Store store = emailSession.getStore("pop3s"); 
            store.connect(HostMonsterEmailSettings.MailServer.value(), HostMonsterEmailSettings.DeveloperEmail.value(), HostMonsterEmailSettings.Hint.value()); 
            //create the folder object and open it 
            Folder emailFolder = store.getFolder(GardenEmailFolderName.GARDEN_INBOX.value()); 
            emailFolder.open(Folder.READ_ONLY);
            Thread.sleep(1000);
            //close the store and folder objects 
            emailFolder.close(false); 
            store.close(); 
        } catch (Exception ex) {
            throw new Exception("Failed POP3s connetion test: " + ex.getMessage());
        }
    }

    public static void testConnectionWithImap() throws Exception{
        try{
            Session session = Session.getDefaultInstance(RoseEmailUtils.getImapProp());
            // connects to the message store
            Store store = session.getStore("imap");
            store.connect(HostMonsterEmailSettings.MailServer.value(), HostMonsterEmailSettings.DeveloperEmail.value(), HostMonsterEmailSettings.Hint.value());

            // opens the inbox folder
            Folder folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_ONLY);
            Thread.sleep(1000);
            // disconnect
            folderInbox.close(false);
            store.close();
        } catch (Exception ex) {
            throw new Exception("Failed IMAP connetion test: " + ex.getMessage());
        }
    }

    public static void testConnectionWithImapS() throws Exception{
        try{
            Session emailSession = Session.getDefaultInstance(RoseEmailUtils.getImapPropS());
            // connects to the message store
            Store store = emailSession.getStore("imaps");
            store.connect(HostMonsterEmailSettings.MailServer.value(), HostMonsterEmailSettings.DeveloperEmail.value(), HostMonsterEmailSettings.Hint.value());

            // opens the inbox folder
            Folder folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_ONLY);
            Thread.sleep(1000);
            // disconnect
            folderInbox.close(false);
            store.close();
        } catch (Exception ex) {
            throw new Exception("Failed IMAPs connetion test: " + ex.getMessage());
        }
    }
    
}
