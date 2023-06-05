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
package com.zcomapproach.garden.peony.view.actions;

import com.zcomapproach.garden.peony.kernel.services.PeonySecurityService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.concurrent.Task;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "File",
        id = "com.zcomapproach.garden.peony.view.PeonyLogoutAction"
)
@ActionRegistration(
        iconBase = "com/zcomapproach/garden/peony/resources/images/lock_go.png",
        displayName = "#CTL_PeonyLogoutAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 0)
    ,
  @ActionReference(path = "Toolbars/File", position = 5000)
    ,
  @ActionReference(path = "Shortcuts", name = "D-Q")
})
@Messages("CTL_PeonyLogoutAction=Logout")
public final class PeonyLogoutAction implements ActionListener {
    
    private final AtomicBoolean logoutInProgress = new AtomicBoolean(false);

    @Override
    public void actionPerformed(ActionEvent e) {
        synchronized(logoutInProgress){
            if (logoutInProgress.get()){
                return;
            }
            logoutInProgress.set(true);
        }
        new Thread(new Task<Void>(){
            @Override
            protected Void call() throws Exception {
                PeonySecurityService aPeonySecurityService = Lookup.getDefault().lookup(PeonySecurityService.class);
                if (aPeonySecurityService != null){
                    aPeonySecurityService.logout();
                }
                logoutInProgress.set(false);
                return null;
            }
        }).start();
    }
}
