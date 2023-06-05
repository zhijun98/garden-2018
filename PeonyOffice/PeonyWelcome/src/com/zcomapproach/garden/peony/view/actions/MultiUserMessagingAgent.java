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
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.packet.MUCUser;

/**
 *
 * @author zhijun98
 */
public class MultiUserMessagingAgent extends PeonyMessagingAgent {
    private final MultiUserChat targetMultiUserChat;
    private final String targetMultiUserChatRoomJid;
    private final MUCUser privateUser;

    public MultiUserMessagingAgent(MultiUserChat targetMultiUserChat, String targetMultiUserChatRoomJid) {
        this(targetMultiUserChat, targetMultiUserChatRoomJid, null);
    }

    public MultiUserMessagingAgent(MultiUserChat targetMultiUserChat, String targetMultiUserChatRoomJid, MUCUser privateUser) {
        this.targetMultiUserChat = targetMultiUserChat;
        this.targetMultiUserChatRoomJid = targetMultiUserChatRoomJid;
        this.privateUser = privateUser;
    }

    @Override
    public Message createXmppMessage(String message) throws ZcaParamException {
        return ZcaXmppUtils.createMultiUserMessage(targetMultiUserChatRoomJid, message, privateUser);
    }

    @Override
    public void sendXmppMessage(Message message) throws ZcaParamException, SmackException.NotConnectedException {
        if (message == null){
            throw new ZcaParamException();
        }
        targetMultiUserChat.sendMessage(message);
    }

}
