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

package com.zcomapproach.garden.rose.bean.state;

import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G01ChatMessage;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.garden.rose.bean.AbstractRoseComponentBean;
import com.zcomapproach.garden.rose.data.profile.RoseChatMessageProfile;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zhijun98
 */
public class RoseMessagingState {
    
    private final AbstractRoseComponentBean ownerBean;
    private final GardenEntityType entityType;
    private final String entityUuid;
    /**
     * Messaging: Talk-to-Us part
     */
    private String requestedTargetReplyMessageUuid;
    private String targetChatMessage;
    private RoseChatMessageProfile targetReplyMessageProfile;
    private RoseChatMessageTreeNode targetChatMessagingTopicRoot;

    public RoseMessagingState(AbstractRoseComponentBean ownerBean, GardenEntityType entityType, String entityUuid) {
        this.ownerBean = ownerBean;
        this.entityType = entityType;
        this.entityUuid = entityUuid;
    }

    public String getRequestedTargetReplyMessageUuid() {
        return requestedTargetReplyMessageUuid;
    }

    public void setRequestedTargetReplyMessageUuid(String requestedTargetReplyMessageUuid) {
        if (ZcaValidator.isNotNullEmpty(requestedTargetReplyMessageUuid)){
            targetReplyMessageProfile = ownerBean.getRuntimeEJB().findGardenMessageProfileByUuid(requestedTargetReplyMessageUuid);
        }
        this.requestedTargetReplyMessageUuid = requestedTargetReplyMessageUuid;
    }
    
    public boolean isForReplyMessage(){
        return (targetReplyMessageProfile != null);
    }

    public RoseChatMessageProfile getTargetReplyMessageProfile() {
        return targetReplyMessageProfile;
    }

    public void setTargetReplyMessageProfile(RoseChatMessageProfile targetReplyMessageProfile) {
        this.targetReplyMessageProfile = targetReplyMessageProfile;
    }
    
    public String getTargetChatMessage() {
        return targetChatMessage;
    }

    public void setTargetChatMessage(String targetChatMessage) {
        this.targetChatMessage = targetChatMessage;
    }

    private RoseChatMessageTreeNode retrieveParentNode(String messageUuid, 
                                                     HashMap<String, RoseChatMessageProfile> aGardenMessageProfileMap,
                                                     HashMap<String, RoseChatMessageTreeNode> treeNodeMap) 
    {
        RoseChatMessageProfile aGardenMessageProfile = aGardenMessageProfileMap.get(messageUuid);
        String parentMessageUuid = aGardenMessageProfile.getChatMessageEntity().getInitialMessageUuid();
        if (ZcaValidator.isNullEmpty(parentMessageUuid)){
            return targetChatMessagingTopicRoot;
        }else{
            RoseChatMessageTreeNode parentNode = treeNodeMap.get(parentMessageUuid);
            if (parentNode == null){
                RoseChatMessageProfile parentGardenMessageProfile = aGardenMessageProfileMap.get(parentMessageUuid);
                if (parentGardenMessageProfile == null){
                    //data is not constistent because it expects NON-NULL
                    return targetChatMessagingTopicRoot;
                }
                parentNode = new RoseChatMessageTreeNode(parentGardenMessageProfile, 
                        retrieveParentNode(parentMessageUuid, aGardenMessageProfileMap, treeNodeMap));
                parentNode.setExpanded(true);
                treeNodeMap.put(parentMessageUuid, parentNode);
            }
            return parentNode;
        }
    }

    public RoseChatMessageTreeNode getTargetChatMessagingTopicRootForBusinessPublicBoard() {
        if ((targetChatMessagingTopicRoot == null)||(targetChatMessagingTopicRoot.getChildCount() == 0)){
            refreshTargetGardenMessagingTopicRoot(GardenEntityType.BUSINESS_PUBLIC_BOARD, null);
        }
        return targetChatMessagingTopicRoot;
    }
    
    public RoseChatMessageTreeNode getTargetChatMessagingTopicRoot() {
        if ((targetChatMessagingTopicRoot == null)||(targetChatMessagingTopicRoot.getChildCount() == 0)){
            refreshTargetGardenMessagingTopicRoot(entityType, entityUuid);
        }
        return targetChatMessagingTopicRoot;
    }
    
    /**
     * This method supports polling from the front-end pages
     * @param entityType
     */
    private void refreshTargetGardenMessagingTopicRoot(GardenEntityType entityType, String entityUuid){
        //create a root
        targetChatMessagingTopicRoot = new RoseChatMessageTreeNode(new RoseChatMessageProfile(), null);
        //get all chat-messages which oriented to entityType and entityUuid
        HashMap<String, RoseChatMessageProfile> aGardenMessageProfileMap = null;
        if (ZcaValidator.isNullEmpty(entityUuid)){
            aGardenMessageProfileMap = ownerBean.getRuntimeEJB()
                    .findRoseChatMessageProfileMapByEnityType(entityType);
        }else{
            aGardenMessageProfileMap = ownerBean.getRuntimeEJB()
                    .findRoseChatMessageProfileMapByEnityTypeUuid(entityType, entityUuid);
        }
        if ((aGardenMessageProfileMap != null) && (!aGardenMessageProfileMap.isEmpty())){
            //create first topic-message-level
            HashMap<String, RoseChatMessageTreeNode> treeNodeMap = new HashMap<>();
            RoseChatMessageTreeNode aNode;
            List<RoseChatMessageProfile> aGardenMessageProfileList = new ArrayList<>(aGardenMessageProfileMap.values());
            Collections.sort(aGardenMessageProfileList, 
                    (RoseChatMessageProfile o1, RoseChatMessageProfile o2) -> 
                            o2.getChatMessageEntity().getCreated().compareTo(o1.getChatMessageEntity().getCreated()));
            for (RoseChatMessageProfile aGardenMessageProfile : aGardenMessageProfileList){
                aNode = treeNodeMap.get(aGardenMessageProfile.getChatMessageEntity().getChatMessageUuid());
                if (aNode == null){
                    aNode = new RoseChatMessageTreeNode(aGardenMessageProfile, 
                                                          retrieveParentNode(aGardenMessageProfile.getChatMessageEntity().getChatMessageUuid(), 
                                                                             aGardenMessageProfileMap, 
                                                                             treeNodeMap));
                    aNode.setExpanded(true);
                    treeNodeMap.put(aGardenMessageProfile.getChatMessageEntity().getChatMessageUuid(), aNode);
                }
            }
        }
        targetChatMessagingTopicRoot.setExpanded(true);
    }
    
    /**
     * Store current talker's chat-message
     * @return 
     */
    public String storeTargetTalkerMessage(){
        return storeTypedTalkerMessage(entityType);
    }

    /**
     * 
     * @param entityType
     * @return 
     */
    public String storeTypedTalkerMessage(GardenEntityType entityType) {
        if (GardenEntityType.UNKNOWN.equals(entityType)){
            return null;
        }
        if (ZcaValidator.isNullEmpty(targetChatMessage)){
            return null;
        }
        
        G01ChatMessage aG01ChatMessage = new G01ChatMessage();
        aG01ChatMessage.setChatMessageUuid(GardenData.generateUUIDString());
        aG01ChatMessage.setEntityType(entityType.name());
        aG01ChatMessage.setEntityUuid(entityUuid);
        if (targetReplyMessageProfile != null){
            aG01ChatMessage.setInitialMessageUuid(targetReplyMessageProfile.getChatMessageEntity().getChatMessageUuid());
        }
        aG01ChatMessage.setMessage(targetChatMessage);
        aG01ChatMessage.setTalkerUserUuid(ownerBean.getRoseUserSession().getTargetAccountProfile().getAccountEntity().getAccountUuid());
        
        try {
            ownerBean.getRuntimeEJB().storeEntityByUuid(G01ChatMessage.class, aG01ChatMessage, aG01ChatMessage.getChatMessageUuid(), 
                    G01DataUpdaterFactory.getSingleton().getG01ChatMessageUpdater());
            RoseJsfUtils.setGlobalInfoFacesMessage("Your message is added.");
            //reset the state
            targetChatMessagingTopicRoot = null;
            targetReplyMessageProfile = null;
            targetChatMessage = null;
            requestedTargetReplyMessageUuid = null;
        } catch (Exception ex) {
            Logger.getLogger(RoseMessagingState.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalErrorFacesMessage(ex.getMessage());
        }
        return null;
    }
}
