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

package com.zcomapproach.garden.rose.data.xmpp;

import org.jivesoftware.smack.AbstractXMPPConnection;

/**
 *
 * @author zhijun98
 */
public class RoseXmppTopicHoster {
    
    private RoseBusinessTopic roseBusinessTopic;
    
    private AbstractXMPPConnection xmppConnection;

    public RoseXmppTopicHoster() {
    }

    public RoseXmppTopicHoster(RoseBusinessTopic roseBusinessTopic, AbstractXMPPConnection xmppConnection) {
        this.roseBusinessTopic = roseBusinessTopic;
        this.xmppConnection = xmppConnection;
    }

    public RoseBusinessTopic getRoseBusinessTopic() {
        return roseBusinessTopic;
    }

    public void setRoseBusinessTopic(RoseBusinessTopic roseBusinessTopic) {
        this.roseBusinessTopic = roseBusinessTopic;
    }

    public AbstractXMPPConnection getXmppConnection() {
        return xmppConnection;
    }
    
    public void setXmppConnection(AbstractXMPPConnection xmppConnection) {
        this.xmppConnection = xmppConnection;
    }
}
