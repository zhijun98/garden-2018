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

import com.zcomapproach.garden.rose.bean.state.RoseChatMessageTreeNode;
import com.zcomapproach.garden.rose.data.profile.RoseChatMessageProfile;

/**
 *
 * @author zhijun98
 */
public interface IRoseChatMessaging {

    public boolean isForReplyMessage();
    
    public RoseChatMessageProfile getTargetReplyMessageProfile();
    
    public String getRequestedTargetReplyMessageUuid();

    public void setRequestedTargetReplyMessageUuid(String requestedTargetReplyMessageUuid);
    
    public String getTargetChatMessage();

    public void setTargetChatMessage(String targetGardenMessage);
    
    public String storeTargetTalkerMessage();
    
    public String storeTalkerMessageForBusinessPublicBoard();
    
    public RoseChatMessageTreeNode getTargetChatMessagingTopicRoot();
    
    public RoseChatMessageTreeNode getTargetChatMessagingTopicRootForBusinessPublicBoard();
    
    public String getTargetReturnWebPath();
    
}
