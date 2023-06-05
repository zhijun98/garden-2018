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

package com.zcomapproach.garden.rose.util;

import com.zcomapproach.garden.guard.RoseWebCipher;
import com.zcomapproach.garden.persistence.entity.G01XmppAccount;
import com.zcomapproach.garden.rose.data.xmpp.RoseBusinessTopic;
import com.zcomapproach.commons.ZcaValidator;
import java.io.IOException;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

/**
 *
 * @author zhijun98
 */
public class RoseXmppUtils {
    
    public static final String XMPP_SERVICE_NAME = "localhost";
    public static final String XMPP_HOST = "localhost";
    public static final String XMPP_RESOURCE = "Rose";
    
    /**
     * Create a generic connection instance without open/close
     * @return 
     */
    public static AbstractXMPPConnection createAbstractXMPPConnection(){
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setServiceName(XMPP_SERVICE_NAME)
                .setHost(XMPP_HOST)
                .setResource(XMPP_RESOURCE)
                .setPort(5222).build();
        return new XMPPTCPConnection(config);
    }
    
    /**
     * Create a connection instance for a buddy account without open/close
     * @param buddyName
     * @param password
     * @return 
     */
    public static AbstractXMPPConnection createAbstractXMPPConnection(String buddyName, String password){
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setServiceName(XMPP_SERVICE_NAME)
                .setHost(XMPP_HOST)
                .setResource(XMPP_RESOURCE)
                .setUsernameAndPassword(buddyName, password)
                .setPort(5222).build();
        return new XMPPTCPConnection(config);
    }
    
    /**
     * Open a connection to the XMPP server, and create a buddy account, at last, close the connection. The XMPP account 
     * uses aRoseAccountProfile's loginName and password as its own buddyName and password. If the same account is there, 
     * it will do nothing
     * 
     * @param conn
     * @param xmppAccount 
     */
    public static void createXmppAccount(AbstractXMPPConnection conn, G01XmppAccount xmppAccount) {
        if (xmppAccount == null){
            return;
        }
        try {
            if (!conn.isConnected()){
                conn.connect();
            }
            AccountManager accountManager = AccountManager.getInstance(conn);
            accountManager.sensitiveOperationOverInsecureConnection(true);
            accountManager.createAccount(xmppAccount.getLoginName(), 
                                         RoseWebCipher.getSingleton().decrypt(xmppAccount.getEncryptedPassword()));
        } catch (SmackException | IOException | XMPPException ex) {
            //Logger.getLogger(RoseXmppUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Open a connection to the XMPP server, and create a buddy account, at last, close the connection. The XMPP account 
     * uses aRoseXmppTopic's name and value as its own buddyName and password. If the same aRoseXmppTopic was used, it will 
     * do nothing
     * 
     * @param conn
     * @param aRoseXmppTopic 
     */
    public static void createRoseXmppTopicAccount(AbstractXMPPConnection conn, RoseBusinessTopic aRoseXmppTopic) {
        if (aRoseXmppTopic == null){
            return;
        }
        try {
            if (!conn.isConnected()){
                conn.connect();
            }
            AccountManager accountManager = AccountManager.getInstance(conn);
            accountManager.sensitiveOperationOverInsecureConnection(true);
            accountManager.createAccount(aRoseXmppTopic.name(), 
                                         aRoseXmppTopic.value());
        } catch (SmackException | IOException | XMPPException ex) {
            //Logger.getLogger(RoseXmppUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 
     * @param accountConn - this connection should be able to be connected and logged in. otherwise this method does nothing
     * @param password 
     * @return  
     */
    public static boolean changeXmppAccountPassword(AbstractXMPPConnection accountConn, 
                                                    String password){
        if ((accountConn == null) || (ZcaValidator.isNullEmpty(password))){
            return false;
        }
        try{
            if (!accountConn.isConnected()){
                accountConn.connect();
            }
            if (!accountConn.isAuthenticated()){
                accountConn.login();
            }

            AccountManager accountManager = AccountManager.getInstance(accountConn);
            accountManager.sensitiveOperationOverInsecureConnection(true);
            accountManager.changePassword(password);
        } catch (SmackException | IOException | XMPPException ex) {
            //Logger.getLogger(RoseXmppUtils.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;
    }
    
    /**
     * 
     * @param conn 
     * @param xmppAccount
     * @return - the connection used for login; if anything wrong, NULL returned
     * @throws org.jivesoftware.smack.SmackException
     * @throws java.io.IOException
     * @throws org.jivesoftware.smack.XMPPException
     */
    public static AbstractXMPPConnection loginXmppServer(AbstractXMPPConnection conn, G01XmppAccount xmppAccount) 
            throws SmackException, IOException, XMPPException
    {
        if ((xmppAccount == null) || (conn == null)) {
            return null;
        }
        if (!conn.isConnected()){
            conn.connect();
        }
        if (!conn.isAuthenticated()){
            conn.login();
        }
        return conn;
    }
    
    public static AbstractXMPPConnection loginXmppServer(G01XmppAccount xmppAccount) throws SmackException, IOException, XMPPException{
        if (xmppAccount == null) {
            return null;
        }
        return loginXmppServer(RoseXmppUtils.createAbstractXMPPConnection(xmppAccount.getLoginName(), 
                                                RoseWebCipher.getSingleton().decrypt(xmppAccount.getEncryptedPassword())), 
                               xmppAccount);
    }

}
