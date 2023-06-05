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
import com.zcomapproach.garden.peony.view.controllers.PeonyEmployeePickerController;
import java.awt.Frame;

/**
 *
 * @author zhijun98
 */
public class PeonyEmployeePickerDialog extends PeonyFaceDialog{

    public PeonyEmployeePickerDialog(Frame parent, boolean modal) {
        super(parent, modal);
    }

    @Override
    protected PeonyFaceController createPeonyFaceController() {
        if (getPeonyFaceController() == null){
            PeonyEmployeePickerController aPeonyFaceController = new PeonyEmployeePickerController();
            /**
             * Listeners to the dialog also listen to the dialog's controllers
             */
            aPeonyFaceController.addPeonyFaceEventListener(this);
            aPeonyFaceController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
            return aPeonyFaceController;
        }
        return getPeonyFaceController();
    }

}
