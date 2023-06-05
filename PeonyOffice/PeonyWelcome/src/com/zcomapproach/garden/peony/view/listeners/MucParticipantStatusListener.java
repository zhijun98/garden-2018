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

package com.zcomapproach.garden.peony.view.listeners;

import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;

/**
 *
 * @author zhijun98
 */
public class MucParticipantStatusListener implements ParticipantStatusListener{
    @Override
    public void joined(String participant) {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucParticipantStatusListener::joined happened.");
    }

    @Override
    public void left(String participant) {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucParticipantStatusListener::left happened.");
    }

    @Override
    public void kicked(String participant, String actor, String reason) {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucParticipantStatusListener::kicked happened.");
    }

    @Override
    public void voiceGranted(String participant) {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucParticipantStatusListener::voiceGranted happened.");
    }

    @Override
    public void voiceRevoked(String participant) {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucParticipantStatusListener::voiceRevoked happened.");
    }

    @Override
    public void banned(String participant, String actor, String reason) {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucParticipantStatusListener::banned happened.");
    }

    @Override
    public void membershipGranted(String participant) {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucParticipantStatusListener::membershipGranted happened.");
    }

    @Override
    public void membershipRevoked(String participant) {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucParticipantStatusListener::membershipRevoked happened.");
    }

    @Override
    public void moderatorGranted(String participant) {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucParticipantStatusListener::moderatorGranted happened.");
    }

    @Override
    public void moderatorRevoked(String participant) {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucParticipantStatusListener::moderatorRevoked happened.");
    }

    @Override
    public void ownershipGranted(String participant) {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucParticipantStatusListener::ownershipGranted happened.");
    }

    @Override
    public void ownershipRevoked(String participant) {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucParticipantStatusListener::ownershipRevoked happened.");
    }

    @Override
    public void adminGranted(String participant) {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucParticipantStatusListener::adminGranted happened.");
    }

    @Override
    public void adminRevoked(String participant) {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucParticipantStatusListener::adminRevoked happened.");
    }

    @Override
    public void nicknameChanged(String participant, String newNickname) {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucParticipantStatusListener::nicknameChanged happened.");
    }

}
