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

package com.zcomapproach.garden.peony.email.events;

import com.zcomapproach.garden.email.GardenEmailMessage;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmail;

/**
 *
 * @author zhijun98
 */
public class SendGardenEmailMessageRequest extends PeonyFaceEvent{

    private final GardenEmailMessage gardenEmailMessage;
    private final PeonyOfflineEmail peonyOfflineEmail;

    public SendGardenEmailMessageRequest(GardenEmailMessage gardenEmailMessage, PeonyOfflineEmail peonyOfflineEmail) {
        this.gardenEmailMessage = gardenEmailMessage;
        this.peonyOfflineEmail = peonyOfflineEmail;
    }

    public GardenEmailMessage getGardenEmailMessage() {
        return gardenEmailMessage;
    }

    public PeonyOfflineEmail getPeonyOfflineEmail() {
        return peonyOfflineEmail;
    }
    
}
