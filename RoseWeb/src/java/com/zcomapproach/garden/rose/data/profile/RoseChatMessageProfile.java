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

package com.zcomapproach.garden.rose.data.profile;

import com.zcomapproach.garden.persistence.entity.G01ChatMessage;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.commons.ZcaCalendar;

/**
 *
 * @author zhijun98
 */
public class RoseChatMessageProfile extends AbstractRoseEntityProfile{
    private RoseUserProfile talker;
    private G01ChatMessage chatMessageEntity;

    public RoseChatMessageProfile() {
        talker = new RoseUserProfile();
        chatMessageEntity = new G01ChatMessage();
    }
    
    public String getChatMessageUuid(){
        return chatMessageEntity.getChatMessageUuid();
    }

    public void setChatMessageEntity(G01ChatMessage chatMessageEntity) {
        this.chatMessageEntity = chatMessageEntity;
    }

    public G01ChatMessage getChatMessageEntity() {
        return chatMessageEntity;
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof RoseChatMessageProfile)){
            return;
        }
        RoseChatMessageProfile srcRoseChatMessageProfile = (RoseChatMessageProfile)srcProfile;
        //chatMessageEntity
        G01DataUpdaterFactory.getSingleton().getG01ChatMessageUpdater().cloneEntity(srcRoseChatMessageProfile.getChatMessageEntity(), this.getChatMessageEntity());
        //talker profile
        this.getTalker().cloneProfile(srcRoseChatMessageProfile.getTalker());
    }

    public RoseUserProfile getTalker() {
        return talker;
    }

    public void setTalker(RoseUserProfile talker) {
        this.talker = talker;
    }

    @Override
    public String getProfileName() {
        return talker.getProfileName() + ": " + chatMessageEntity.getMessage();
    }

    @Override
    public String getProfileDescriptiveName() {
        return talker.getProfileDescriptiveName() + ": " + chatMessageEntity.getMessage();
    }

    @Override
    protected String getProfileUuid() {
        return talker.getProfileUuid() + ": "+ chatMessageEntity.getChatMessageUuid();
    }

    public String getMessageLine(){
        if (chatMessageEntity == null){
            return "";
        }
        String messageLine = chatMessageEntity.getMessage();
        if (talker == null){
            messageLine = messageLine + " (";
        }else{
            messageLine = messageLine + " (" + talker.getUserEntity().getFirstName() + " " + talker.getUserEntity().getLastName() + ": ";
        }
        messageLine = messageLine + ZcaCalendar.convertToMMddyyyyHHmmss(chatMessageEntity.getCreated(), "-", "@", ":") + ")";
        return messageLine;
    }

}
