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

package com.zcomapproach.garden.peony;

import com.zcomapproach.garden.peony.kernel.services.PeonyService;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import java.util.Collection;
import javafx.application.Platform;
import org.openide.modules.OnStop;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
@OnStop
public class PeonyTerminator implements Runnable {
    
    /**
     * todo zzj: is it proper to skip confirmation dialog before exit?
     */
    public static boolean SKIP_CONFIRMATION = true;

    private static void terminatePeonySecurityService() {
        
        Collection<? extends PeonyService> allPeonyServices = Lookup.getDefault().lookupAll(PeonyService.class);
        if (allPeonyServices != null){
            for (PeonyService peonyService : allPeonyServices){
                peonyService.closeService();
            }
        }
        
        PeonyProperties.getSingleton().terminate();
    }

    @Override
    public void run() {
        terminatePeonySecurityService();
        Platform.exit();
    }
}
