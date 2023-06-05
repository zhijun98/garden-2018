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
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;

/**
 *
 * @author zhijun98
 */
public class PeonyBuddyGroupTreeItemData extends PeonyTreeItemData{
    
    private String groupName;
    private int buddyCount;
    private final Set<String> buddyJidSet = new TreeSet<>();
    
    public PeonyBuddyGroupTreeItemData(String itemTitle) {
        super(itemTitle, Status.UNKNOWN);
    }

    public PeonyBuddyGroupTreeItemData(String groupName, int buddyCount) {
        super(groupName, Status.INITIAL);
        this.groupName = groupName;
        this.buddyCount = buddyCount;
    }

    public PeonyBuddyGroupTreeItemData(RosterGroup rosterGroup) {
        super(rosterGroup, Status.INITIAL);
        
        groupName = rosterGroup.getName();
        buddyCount = rosterGroup.getEntryCount();
        
        List<RosterEntry> aRosterEntryList = rosterGroup.getEntries();
        for (RosterEntry aRosterEntry : aRosterEntryList){
            buddyJidSet.add(aRosterEntry.getUser());
        }
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getBuddyCount() {
        return buddyCount;
    }

    public void setBuddyCount(int buddyCount) {
        this.buddyCount = buddyCount;
    }

    public boolean isMember(String buddyJid){
        return buddyJidSet.contains(buddyJid);
    }
    
    public void addMember(String buddyJid){
        buddyJidSet.add(buddyJid);
    }

    @Override
    public String toString() {
        if (ZcaValidator.isNullEmpty(groupName)){
            return super.getTreeItemData().toString();
        }
        return groupName + " (" + buddyCount + ")";
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.groupName);
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
        final PeonyBuddyGroupTreeItemData other = (PeonyBuddyGroupTreeItemData) obj;
        if (!Objects.equals(this.groupName, other.groupName)) {
            return false;
        }
        return true;
    }
}
