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

package com.zcomapproach.garden.rose.bean;

import com.zcomapproach.garden.data.GardenFlower;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.rose.data.xmpp.RoseBusinessTopic;
import com.zcomapproach.garden.rose.data.xmpp.RoseXmppTopicHoster;
import com.zcomapproach.garden.rose.util.RoseXmppUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

/**
 *
 * @author zhijun98
 */
@Named(value = "roseXmppTopics")
@ApplicationScoped
public class RoseXmppTopicsBean extends AbstractRoseComponentBean{
    
    private final HashMap<RoseBusinessTopic, RoseXmppTopicHoster> topicHosters=  new HashMap<>();
    
    @PostConstruct
    public synchronized void constructRoseWeb(){
        openRoseXmppTopicConnections();
    }
    
    @PreDestroy
    public synchronized void destroyRoseWeb(){
        closeRoseXmppTopicConnections();
    }

    @Override
    public GardenEntityType getEntityType() {
        return GardenEntityType.XMPP_TOPICS;
    }

    @Override
    public String getRequestedEntityUuid() {
        return GardenEntityType.XMPP_TOPICS.value();
    }

    @Override
    public String getRosePageTopic() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTopicIconAwesomeName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTargetReturnWebPath() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * If a topic was openned, it will be closed
     */
    private void openRoseXmppTopicConnections() {
        
        List<RoseBusinessTopic> aRoseXmppTopicList = RoseBusinessTopic.getRoseXmppEventList(false);
        List<RoseXmppTopicHoster> hosterList = new ArrayList<>();
        AbstractXMPPConnection aXMPPConnection;
        RoseXmppTopicHoster aRoseXmppTopicHoster;
        for (RoseBusinessTopic aRoseXmppTopic : aRoseXmppTopicList){
            aXMPPConnection = RoseXmppUtils.createAbstractXMPPConnection();
            //create account
            RoseXmppUtils.createRoseXmppTopicAccount(aXMPPConnection, aRoseXmppTopic);
            try {
                //login...
                aXMPPConnection.login(aRoseXmppTopic.name(), aRoseXmppTopic.value(), GardenFlower.ROSE.value());
            } catch (XMPPException | SmackException | IOException ex) {
                getRuntimeEJB().sendCriticalTechnicalException(ex, getRoseSettings().isEmailDisabled(), getRoseSettings().isSmsDisabled());
            }
            aRoseXmppTopicHoster = new RoseXmppTopicHoster(aRoseXmppTopic, aXMPPConnection);
            //todo zzj: hook all the listners to this topic here
            hosterList.add(aRoseXmppTopicHoster);
        }
        synchronized(topicHosters){
            closeRoseXmppTopicConnections();
            for (RoseXmppTopicHoster hoster : hosterList){
                topicHosters.put(hoster.getRoseBusinessTopic(), hoster);
            }
        }
    }

    private void closeRoseXmppTopicConnections() {
        List<RoseXmppTopicHoster> hosterList = null;
        //clear the topicConnections
        synchronized(topicHosters){
            if (!topicHosters.isEmpty()){
                hosterList = new ArrayList<>(topicHosters.values());
                topicHosters.clear();
            }
        }
        //close the connections
        for (RoseXmppTopicHoster hoster : hosterList){
            if (!hoster.getXmppConnection().isConnected()){
                hoster.getXmppConnection().disconnect();
            }
        }
    }
}
