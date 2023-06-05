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
package com.zcomapproach.garden.peony.view;

import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.PeonyTerminator;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import javax.swing.JOptionPane;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbBundle;

public class Installer extends ModuleInstall {

    @Override
    public boolean closing() {
        if (PeonyTerminator.SKIP_CONFIRMATION){
            return true;
        }
        if (PeonyProperties.getSingleton().isGardenMaster()){
            PeonyFaceUtils.displayWarningMessageDialog("GardenMaster: this is Peony server. If shut it down, all the Peony clients may not work in the expected way.");
        }
        return PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, NbBundle.getMessage(Installer.class, "peony_exit_confirmation_msg")) == JOptionPane.YES_OPTION;
    }

}
