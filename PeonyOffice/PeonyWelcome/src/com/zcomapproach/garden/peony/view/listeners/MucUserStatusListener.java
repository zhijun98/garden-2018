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
import org.jivesoftware.smackx.muc.UserStatusListener;

/**
 *
 * @author zhijun98
 */
public class MucUserStatusListener implements UserStatusListener {

    @Override
    public void kicked(String actor, String reason) {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucUserStatusListener::kicked happened.");
    }

    @Override
    public void voiceGranted() {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucUserStatusListener::voiceGranted happened.");
    }

    @Override
    public void voiceRevoked() {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucUserStatusListener::voiceRevoked happened.");
    }

    @Override
    public void banned(String actor, String reason) {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucUserStatusListener::banned happened.");
    }

    @Override
    public void membershipGranted() {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucUserStatusListener::membershipGranted happened.");
    }

    @Override
    public void membershipRevoked() {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucUserStatusListener::membershipRevoked happened.");
    }

    @Override
    public void moderatorGranted() {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucUserStatusListener::moderatorGranted happened.");
    }

    @Override
    public void moderatorRevoked() {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucUserStatusListener::moderatorRevoked happened.");
    }

    @Override
    public void ownershipGranted() {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucUserStatusListener::ownershipGranted happened.");
    }

    @Override
    public void ownershipRevoked() {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucUserStatusListener::ownershipRevoked happened.");
    }

    @Override
    public void adminGranted() {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucUserStatusListener::adminGranted happened.");
    }

    @Override
    public void adminRevoked() {
        PeonyFaceUtils.publishMessageOntoOutputWindow(">>> MucUserStatusListener::adminRevoked happened.");
    }

}
