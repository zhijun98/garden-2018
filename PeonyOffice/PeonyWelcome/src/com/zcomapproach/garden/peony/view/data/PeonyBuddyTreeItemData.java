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

package com.zcomapproach.garden.peony.view.data;

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.commons.xmpp.data.ZcaXmppSettings;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.roster.packet.RosterPacket;

/**
 *
 * @author zhijun98
 */
public class PeonyBuddyTreeItemData extends PeonyTreeItemData{

    private String buddyName;
    private String buddyJid;
    private String buddyFullName;
    private boolean offline;
    private RosterPacket.ItemStatus status;
    private RosterPacket.ItemType type;
    private final Set<String> groupNameSet = new TreeSet<>();
    
    public PeonyBuddyTreeItemData() {
        super(null, Status.UNKNOWN);
        groupNameSet.add(ZcaXmppSettings.ONLINE_GROUP_NAME);
    }

    public PeonyBuddyTreeItemData(String buddyName, String buddyJid, String buddyFullName, boolean offline, RosterPacket.ItemStatus status, RosterPacket.ItemType type) {
        super(buddyName, Status.INITIAL);
        this.buddyJid = buddyJid;
        this.buddyName = ZcaXmppSettings.parseChatUserName(buddyName);
        if (ZcaValidator.isNullEmpty(this.buddyName)){
            this.buddyName = ZcaXmppSettings.parseChatUserName(buddyJid);
        }
        this.buddyFullName = buddyFullName;
        this.offline = offline;
        this.status = status;
        this.type = type;
        groupNameSet.add(ZcaXmppSettings.ONLINE_GROUP_NAME);
    }

    public PeonyBuddyTreeItemData(RosterEntry rosterEntry, String buddyFullName, boolean offline) {
        super(rosterEntry, Status.INITIAL);
        buddyJid = rosterEntry.getUser();
        buddyName = ZcaXmppSettings.parseChatUserName(rosterEntry.getName());
        if (ZcaValidator.isNullEmpty(buddyName)){
            buddyName = ZcaXmppSettings.parseChatUserName(buddyJid);
        }
        this.buddyFullName = buddyFullName;
        this.offline = offline;
        status = rosterEntry.getStatus();
        type = rosterEntry.getType();
        List<RosterGroup> groups = rosterEntry.getGroups();
        for (RosterGroup group : groups){
            groupNameSet.add(group.getName());
        }
        groupNameSet.add(ZcaXmppSettings.ONLINE_GROUP_NAME);
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public String getBuddyName() {
        return buddyName;
    }

    public void setBuddyName(String buddyName) {
        this.buddyName = buddyName;
    }

    public String getBuddyFullName() {
        return buddyFullName;
    }

    public void setBuddyFullName(String buddyFullName) {
        this.buddyFullName = buddyFullName;
    }

    public String getBuddyJid() {
        return buddyJid;
    }

    public void setBuddyJid(String buddyJid) {
        this.buddyJid = buddyJid;
    }

    public RosterPacket.ItemStatus getStatus() {
        return status;
    }

    public void setStatus(RosterPacket.ItemStatus status) {
        this.status = status;
    }

    public RosterPacket.ItemType getType() {
        return type;
    }

    public void setType(RosterPacket.ItemType type) {
        this.type = type;
    }
    
    public boolean isMember(String groupName){
        return groupNameSet.contains(groupName);
    }
    
    public void addMember(String groupName){
        groupNameSet.add(groupName);
    }

    @Override
    public String toString() {
        if (ZcaValidator.isNullEmpty(buddyFullName)){
            if (ZcaValidator.isNullEmpty(buddyName)){
                return ZcaXmppSettings.parseChatUserName(super.getTreeItemData().toString());
            }
            return buddyName;
        }
        return buddyFullName;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + Objects.hashCode(this.buddyJid);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PeonyBuddyTreeItemData other = (PeonyBuddyTreeItemData) obj;
        if (!Objects.equals(this.buddyJid, other.buddyJid)) {
            return false;
        }
        return true;
    }

}
