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

package com.zcomapproach.garden.peony.admin;

import com.zcomapproach.garden.peony.admin.zcadocmaster.DocMasterTopComponent;
import com.zcomapproach.garden.peony.kernel.PeonyServiceProvider;
import com.zcomapproach.garden.peony.kernel.rest.PeonyRestClient;
import com.zcomapproach.garden.peony.kernel.services.PeonyAdminService;
import com.zcomapproach.garden.peony.security.PeonyPrivilege;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.rest.GardenWebResoureRoot;
import javax.swing.SwingUtilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author zhijun98
 */
public class PeonyAdminServiceProvider extends PeonyServiceProvider implements PeonyAdminService{
    
    public static void launchPeonyAdminServiceWindow() {
        if (SwingUtilities.isEventDispatchThread()){
            launchPeonyAdminServiceWindowHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    launchPeonyAdminServiceWindowHelper();
                }
            });
        }
    }
    
    private static void launchPeonyAdminServiceWindowHelper() {
        if (PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.CENTRAL_ADMIN)){
            TopComponent tc = WindowManager.getDefault().findTopComponent("PeonyAdminServiceTopComponent");
            if (tc instanceof PeonyAdminServiceTopComponent){
                ((PeonyAdminServiceTopComponent)tc).launchPeonyTopComponent("Central Admin");
            }
        }else{
            PeonyFaceUtils.displayErrorMessageDialog("You are not authorized. Please contact administrator to solve it.");
        }
    }

    public static void launchDocMasterWindow() {
        if (SwingUtilities.isEventDispatchThread()){
            launchDocMasterWindowHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    launchDocMasterWindowHelper();
                }
            });
        }
    }

    private static void launchDocMasterWindowHelper() {
        if (PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.CENTRAL_ADMIN)){
            TopComponent tc = WindowManager.getDefault().findTopComponent("DocMasterTopComponent");
            if (tc instanceof DocMasterTopComponent){
                tc.open();
                tc.requestActive();
            }
        }else{
            PeonyFaceUtils.displayErrorMessageDialog("You are not authorized. Please contact administrator to solve it.");
        }
    }

    private final PeonyRestClient peonyAdminRestClient;
    
    public PeonyAdminServiceProvider() {
        this.peonyAdminRestClient = new PeonyRestClient(GardenWebResoureRoot.MANAGEMENT);
    }
    
    @Override
    public PeonyRestClient getPeonyAdminRestClient() {
        return peonyAdminRestClient;
    }

    @Override
    public void closeService() {
        
    }

    @Override
    public void launchPeonyAdminServiceTopComponent() {
        launchPeonyAdminServiceWindow();
    }

}
