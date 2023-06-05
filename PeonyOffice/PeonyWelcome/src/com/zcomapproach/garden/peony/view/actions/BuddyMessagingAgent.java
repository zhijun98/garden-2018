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

package com.zcomapproach.garden.peony.view.actions;

import com.zcomapproach.commons.exceptions.validation.ZcaParamException;
import com.zcomapproach.commons.xmpp.ZcaXmppUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.packet.Message;

/**
 *
 * @author zhijun98
 */
public class BuddyMessagingAgent extends PeonyMessagingAgent {
    
    private final Chat targetChat;
    private final String fromJid;
    private final String toJid;

    public BuddyMessagingAgent(Chat targetChat, String fromJid, String toJid) {
        this.targetChat = targetChat;
        this.fromJid = fromJid;
        this.toJid = toJid;
    }

    @Override
    public Message createXmppMessage(String message) throws ZcaParamException{
        return ZcaXmppUtils.createBuddyMessage(fromJid, toJid, message);
    }

    @Override
    public void sendXmppMessage(Message message) throws ZcaParamException, SmackException.NotConnectedException {
        if (message == null){
            throw new ZcaParamException();
        }
        targetChat.sendMessage(message);
    }
}
