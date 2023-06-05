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

package com.zcomapproach.garden.peony.taxcorp;

import com.zcomapproach.garden.peony.kernel.PeonyServiceProvider;
import com.zcomapproach.garden.rest.GardenWebResoureRoot;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxcorpService;
import com.zcomapproach.garden.peony.kernel.rest.PeonyRestClient;
import com.zcomapproach.garden.peony.taxcorp.dialogs.PeonyTaxcorpCaseDialog;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.persistence.peony.PeonyTaxcorpCase;
import com.zcomapproach.garden.rest.GardenRestParams;
import java.awt.Dimension;
import javax.swing.SwingUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author zhijun98
 */
@ServiceProvider(service = PeonyTaxcorpService.class)
public class PeonyTaxcorpServiceProvider extends PeonyServiceProvider implements PeonyTaxcorpService {

    private final PeonyRestClient peonyTaxcorpRestClient;

    public PeonyTaxcorpServiceProvider() {
        this.peonyTaxcorpRestClient = new PeonyRestClient(GardenWebResoureRoot.TAXCORP);
    }
    
    @Override
    public PeonyRestClient getPeonyTaxcorpRestClient() {
        return peonyTaxcorpRestClient;
    }

    @Override
    public void launchPeonyTaxcorpCaseTopComponentByEinNumber(String einNumber) {
        super.getExecutorCachedService().submit(new Runnable(){
            @Override
            public void run() {
                try {
                    launchPeonyTaxcorpCaseTopComponent(getPeonyTaxcorpRestClient().findEntity_XML(PeonyTaxcorpCase.class,
                            GardenRestParams.Taxcorp.findPeonyTaxcorpCaseByEinNumberRestParams(einNumber)));
                } catch (Exception ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Cannot find the taxcorp case!");
                }
            }
        });
    }

    @Override
    public void launchPeonyTaxcorpCaseTopComponentByTaxcorpCaseUuid(final String taxcorpCaseUuid) {
        super.getExecutorCachedService().submit(new Runnable(){
            @Override
            public void run() {
                try {
                    launchPeonyTaxcorpCaseTopComponent(getPeonyTaxcorpRestClient().findEntity_XML(PeonyTaxcorpCase.class,
                            GardenRestParams.Taxcorp.findPeonyTaxcorpCaseByTaxcorpCaseUuidRestParams(taxcorpCaseUuid)));
                } catch (Exception ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Cannot find the taxcorp case!");
                }
            }
        });
    }

    @Override
    public void launchPeonyTaxcorpCaseDialog(final PeonyTaxcorpCase peonyTaxcorpCase) {
        if (SwingUtilities.isEventDispatchThread()){
            launchPeonyTaxcorpCaseDialogHelper(peonyTaxcorpCase);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    launchPeonyTaxcorpCaseDialogHelper(peonyTaxcorpCase);
                }
            });
        }
    }
    private void launchPeonyTaxcorpCaseDialogHelper(final PeonyTaxcorpCase peonyTaxcorpCase) {
        PeonyTaxcorpCaseDialog aPeonyTaxcorpCaseDialog = new PeonyTaxcorpCaseDialog(null, true);
        aPeonyTaxcorpCaseDialog.launchPeonyTaxcorpCaseDialog(peonyTaxcorpCase.getTaxcorpCaseTitle(), peonyTaxcorpCase);
        aPeonyTaxcorpCaseDialog.setSize(new Dimension(800, 600));
    }

    @Override
    public void launchPeonyTaxcorpCaseTopComponent(final PeonyTaxcorpCase targetPeonyTaxcorpCase) {
        if (SwingUtilities.isEventDispatchThread()){
            launchTaxcorpCaseTopComponentHelper(targetPeonyTaxcorpCase);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    launchTaxcorpCaseTopComponentHelper(targetPeonyTaxcorpCase);
                }
            });
        }
    }

    private void launchTaxcorpCaseTopComponentHelper(final PeonyTaxcorpCase targetPeonyTaxcorpCase) {
        PeonyTaxcorpCaseTopComponent aPeonyTaxcorpCaseTopComponent = new PeonyTaxcorpCaseTopComponent();
        aPeonyTaxcorpCaseTopComponent.launchPeonyTaxcorpCaseTopComponent(targetPeonyTaxcorpCase);
    }

    @Override
    public void closeService() {
    }
}
