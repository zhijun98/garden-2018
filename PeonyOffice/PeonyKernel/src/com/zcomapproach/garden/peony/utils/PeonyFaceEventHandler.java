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

package com.zcomapproach.garden.peony.utils;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/**
 *
 * @author zhijun98
 */
public class PeonyFaceEventHandler {

    /**
     * 
     * @param aDialog -  if NULL, nothing happened in the method
     * @param disposeDialog 
     */
    public static void handleCloseDialogOwnerRequested(final JDialog aDialog, final boolean disposeDialog) {
        if (aDialog == null){
            return;
        }
        if (SwingUtilities.isEventDispatchThread()){
            handleCloseDialogOwnerRequestedHelper(aDialog, disposeDialog);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    handleCloseDialogOwnerRequestedHelper(aDialog, disposeDialog);
                }
            });
        }
    }

    private static void handleCloseDialogOwnerRequestedHelper(JDialog aDialog, boolean disposeDialog) {
        aDialog.setVisible(false);
        if (disposeDialog){
            aDialog.dispose();
        }
    }

}
