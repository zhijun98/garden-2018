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
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;

/**
 *
 * @author zhijun98
 */
public abstract class PeonyMessagingAgent {

    public void sendMessage(String message) throws ZcaParamException, SmackException.NotConnectedException {
        sendXmppMessage(createXmppMessage(message));
    }
    
    public abstract Message createXmppMessage(String message) throws ZcaParamException;
    
    public abstract void sendXmppMessage(Message message) throws ZcaParamException, SmackException.NotConnectedException;

}
