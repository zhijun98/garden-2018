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

package com.zcomapproach.garden.peony.taxpayer;

import com.zcomapproach.garden.peony.kernel.PeonyServiceProvider;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import com.zcomapproach.garden.peony.kernel.rest.PeonyRestClient;
import com.zcomapproach.garden.peony.taxpayer.dialogs.PeonyTaxpayerCaseDialog;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.garden.rest.GardenWebResoureRoot;
import java.awt.Dimension;
import javax.swing.SwingUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author zhijun98
 */
@ServiceProvider(service = PeonyTaxpayerService.class)
public class PeonyTaxpayerServiceProvider extends PeonyServiceProvider implements PeonyTaxpayerService {

    private final PeonyRestClient peonyTaxpayerRestClient;
    
    public PeonyTaxpayerServiceProvider() {
        this.peonyTaxpayerRestClient = new PeonyRestClient(GardenWebResoureRoot.TAXPAYER);
    }
    
    @Override
    public PeonyRestClient getPeonyTaxpayerRestClient() {
        return peonyTaxpayerRestClient;
    }

    @Override
    public void launchPeonyTaxpayerCaseDialog(final PeonyTaxpayerCase peonyTaxpayerCase) {
        if (SwingUtilities.isEventDispatchThread()){
            launchlaunchPeonyTaxpayerCaseDialogHelper(peonyTaxpayerCase);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    launchlaunchPeonyTaxpayerCaseDialogHelper(peonyTaxpayerCase);
                }
            });
        }
    }

    private void launchlaunchPeonyTaxpayerCaseDialogHelper(final PeonyTaxpayerCase peonyTaxpayerCase) {
        PeonyTaxpayerCaseDialog aPeonyTaxpayerCaseDialog = new PeonyTaxpayerCaseDialog(null, true);
        aPeonyTaxpayerCaseDialog.launchPeonyTaxpayerCaseDialog(peonyTaxpayerCase.getTaxpayerCaseTitle(false), peonyTaxpayerCase);
        aPeonyTaxpayerCaseDialog.setSize(new Dimension(800, 600));
    }
    
    @Override
    public void launchPeonyTaxpayerCaseTopComponentByTaxpayerCaseUuid(final String taxpayerCaseUuid) {
        super.getExecutorCachedService().submit(new Runnable(){
            @Override
            public void run() {
                try {
                    launchPeonyTaxpayerCaseTopComponent(getPeonyTaxpayerRestClient().findEntity_XML(PeonyTaxpayerCase.class,
                            GardenRestParams.Taxpayer.findBasicPeonyTaxpayerCaseByTaxpayerCaseUuidRestParams(taxpayerCaseUuid)));
                } catch (Exception ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Cannot find the taxpayer case!");
                }
            }
        });
    }

    @Override
    public void launchPeonyTaxpayerCaseTopComponent(final PeonyTaxpayerCase targetPeonyTaxpayerCase) {
        if (SwingUtilities.isEventDispatchThread()){
            launchTaxpayerCaseTopComponentHelper(targetPeonyTaxpayerCase);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    launchTaxpayerCaseTopComponentHelper(targetPeonyTaxpayerCase);
                }
            });
        }
    }

    private void launchTaxpayerCaseTopComponentHelper(final PeonyTaxpayerCase targetPeonyTaxpayerCase) {
        PeonyTaxpayerCaseTopComponent aPeonyTaxpayerCaseTopComponent = new PeonyTaxpayerCaseTopComponent();
        aPeonyTaxpayerCaseTopComponent.launchPeonyTaxpayerCaseTopComponent(targetPeonyTaxpayerCase);
    }

    @Override
    public void closeService() {
    }
    
}
