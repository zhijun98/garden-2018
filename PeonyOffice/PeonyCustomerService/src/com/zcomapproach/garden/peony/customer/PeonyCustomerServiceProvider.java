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

package com.zcomapproach.garden.peony.customer;

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.peony.kernel.services.PeonyCustomerService;
import com.zcomapproach.garden.peony.kernel.PeonyServiceProvider;
import com.zcomapproach.garden.peony.kernel.rest.PeonyRestClient;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.persistence.entity.G02TulipUser;
import com.zcomapproach.garden.persistence.peony.PeonyAccount;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.garden.rest.GardenWebResoureRoot;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author zhijun98
 */
@ServiceProvider(service = PeonyCustomerService.class)
public class PeonyCustomerServiceProvider extends PeonyServiceProvider implements PeonyCustomerService {
    private final PeonyRestClient peonyCustomerRestClient = new PeonyRestClient(GardenWebResoureRoot.CUSTOMER);

    @Override
    public PeonyRestClient getPeonyCustomerRestClient() {
        return peonyCustomerRestClient;
    }

    @Override
    public void launchPeonyCustomerServiceTopComponent() {
        if (SwingUtilities.isEventDispatchThread()){
            launchPeonyCustomerServiceTopComponentHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    launchPeonyCustomerServiceTopComponentHelper();
                }
            });
        }
    }
    
    private void launchPeonyCustomerServiceTopComponentHelper(){
        TopComponent tc = WindowManager.getDefault().findTopComponent("PeonyCustomerServiceTopComponent");
        if (tc instanceof PeonyCustomerServiceTopComponent){
            PeonyCustomerServiceTopComponent peonyCustomerServiceTopComponent = (PeonyCustomerServiceTopComponent)tc;
            peonyCustomerServiceTopComponent.launchPeonyTopComponent(peonyCustomerServiceTopComponent.getName());
            peonyCustomerServiceTopComponent.displayPeonyTopComponent();
        }
    }

    @Override
    public void launchTulipCustomerProfile(final G02TulipUser tulipUser) {
        if (tulipUser == null){
            return;
        }
        
        if (SwingUtilities.isEventDispatchThread()){
            launchTulipCustomerProfileHelper(tulipUser);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    launchTulipCustomerProfileHelper(tulipUser);
                }
            });
        }
    }
    
    private void launchTulipCustomerProfileHelper(final G02TulipUser tulipUser){
        TulipCustomerProfileTopComponent aTulipCustomerProfileTopComponent = new TulipCustomerProfileTopComponent();
        aTulipCustomerProfileTopComponent.launchTulipCustomerProfileTopComponent(tulipUser);
    }

    @Override
    public void launchCustomerProfileWindowByAccountUuid(final String accountUuid) {
        SwingWorker displayCustomerProfile = new SwingWorker<PeonyAccount, Void>(){
            @Override
            protected PeonyAccount doInBackground() throws Exception {
                if (ZcaValidator.isNullEmpty(accountUuid)){
                    return null;
                }
                try{
                    PeonyAccount customerAccount = Lookup.getDefault().lookup(PeonyCustomerService.class).getPeonyCustomerRestClient()
                            .findEntity_XML(PeonyAccount.class, GardenRestParams.Customer.findCustomerByAccountUuidRestParams(accountUuid));
                    return customerAccount;
                }catch (Exception ex){
                    //Exceptions.printStackTrace(ex);
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    PeonyAccount customerAccount = get();
                    if (customerAccount == null){
                        PeonyFaceUtils.displayInformationMessageDialog("Start working on new customer profile ...");
                        customerAccount = new PeonyAccount();
                    }
                    CustomerProfileTopComponent aCustomerProfileTopComponent = new CustomerProfileTopComponent();
                    aCustomerProfileTopComponent.initializeCustomerProfileTopComponent(customerAccount);
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        displayCustomerProfile.execute();
    }

    @Override
    public void closeService() {
        
    }

}
