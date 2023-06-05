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

package com.zcomapproach.garden.peony.view.dialogs;

import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.controllers.PeonyMessagingBoardController;
import java.awt.Frame;
import javax.swing.JDialog;

/**
 *
 * @author zhijun98
 */
public class PeonyTalkingDialog extends PeonyFaceDialog {
    
    private PeonyMessagingBoardController peonyMessagingBoardController;

    public PeonyTalkingDialog(Frame parent, boolean modal) {
        super(parent, modal, JDialog.HIDE_ON_CLOSE);
    }

    @Override
    public void launchPeonyDialog(String dialogTitle, PeonyFaceController peonyFaceController) {
        if (peonyFaceController instanceof PeonyMessagingBoardController){
            peonyMessagingBoardController = (PeonyMessagingBoardController)peonyFaceController;
            peonyMessagingBoardController.addPeonyFaceEventListener(this);
            this.setAlwaysOnTop(false);
        }
        super.launchPeonyDialog(dialogTitle, peonyFaceController); 
    }

    @Override
    protected PeonyFaceController createPeonyFaceController() {
        return peonyMessagingBoardController;
    }

    public void receiveMessage(String message) {
        if (peonyMessagingBoardController != null){
            peonyMessagingBoardController.receiveMessage(message);
        }
    }

}
