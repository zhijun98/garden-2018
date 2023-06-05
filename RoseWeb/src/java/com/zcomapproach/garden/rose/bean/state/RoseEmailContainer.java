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

import com.zcomapproach.garden.email.rose.RoseEmailMessage;
import com.zcomapproach.garden.email.data.GardenEmailFolderName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

/**
 *
 * @author zhijun98
 */
class RoseEmailContainer {
    /**
     * All the retrieved email messages including valid/invalid emails that are NULL in this data structure
     */
    private final HashMap<Long, RoseEmailMessage> emailMessageStore;
    /**
     * Storage for messages categorized by folders.
     */
    private final HashMap<GardenEmailFolderName, List<RoseEmailMessage>> messagesCategorizedByFolders;
    /**
     * Set of message ids which is labeled by deletion.
     */
    private final TreeSet<Long> messageIdsForDeletion;
    /**
     * Set of message ids for loading message content and attachments.
     */
    private final TreeSet<Long> messageIdsForContentLoading;

    RoseEmailContainer() {
        emailMessageStore = new HashMap<>();
        //set up messagesByFolders
        messagesCategorizedByFolders = new HashMap<>();
        List<GardenEmailFolderName> aRoseEmailFolderList = GardenEmailFolderName.getGardenEmailFolderNameList(false);
        for (GardenEmailFolderName aRoseEmailFolder : aRoseEmailFolderList){
            messagesCategorizedByFolders.put(aRoseEmailFolder, new ArrayList<>());
        }
        messageIdsForDeletion = new TreeSet<>();
        messageIdsForContentLoading = new TreeSet<>();
    }

    public HashMap<Long, RoseEmailMessage> getEmailMessageStore() {
        return emailMessageStore;
    }

    public HashMap<GardenEmailFolderName, List<RoseEmailMessage>> getMessagesCategorizedByFolders() {
        return messagesCategorizedByFolders;
    }

    public TreeSet<Long> getMessageIdsForDeletion() {
        return messageIdsForDeletion;
    }

    public TreeSet<Long> getMessageIdsForContentLoading() {
        return messageIdsForContentLoading;
    }

}
