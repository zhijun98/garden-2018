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

package com.zcomapproach.garden.peony.email;

import com.zcomapproach.garden.peony.kernel.services.PeonyEmailService;
import com.zcomapproach.garden.email.GardenEmailMessage;
import com.zcomapproach.garden.email.GardenEmailSerializer;
import com.zcomapproach.garden.email.GardenEmailUtils;
import com.zcomapproach.garden.peony.kernel.PeonyServiceProvider;
import com.zcomapproach.garden.email.data.OfflineMessageStatus;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import java.util.ArrayList;
import org.openide.util.lookup.ServiceProvider;
import com.zcomapproach.garden.peony.windows.PeonyTaskViewTopComponent;
import com.zcomapproach.garden.persistence.entity.G02CommArchive;
import com.zcomapproach.garden.persistence.peony.PeonyCommAssignment;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmail;
import java.nio.file.Paths;
import java.util.List;
import javafx.scene.control.Tab;
import javax.swing.SwingUtilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author zhijun98
 */
@ServiceProvider(service = PeonyEmailService.class)
public class PeonyEmailServiceProvider extends PeonyServiceProvider implements PeonyEmailService {
    
    private PeonyEmailBoxTopComponent peonyEmailTopComponent;
    private SpamRuleTopComponent spamRuleTopComponent;

    @Override
    public void displayOfflineEmailTopComponentByEmailTagUuid(String emailTagUuid) {
        if (peonyEmailTopComponent != null){
            peonyEmailTopComponent.openPeonyEmailMessageTabByEmailTagUuid(emailTagUuid);
        }
    }

    @Override
    public void displayEmailTagDialogByTagUuid(String emailTagUuid) {
        if (peonyEmailTopComponent != null){
            peonyEmailTopComponent.openPeonyEmailTagDialigByTagUuid(emailTagUuid);
        }
    }

    @Override
    public void displayOfflineEmailTopComponentByOfflineEmailUuid(String offlineEmailUuid) {
        if (peonyEmailTopComponent != null){
            peonyEmailTopComponent.openPeonyEmailMessageTabByOfflineEmailUuid(offlineEmailUuid);
        }
    }

    @Override
    public void displayComposeEmailTopComponent(GardenEmailMessage peonyEmailMessage, final PeonyOfflineEmail peonyOfflineEmail, final OfflineMessageStatus purpose) {
        if (SwingUtilities.isEventDispatchThread()){
            displayComposeEmailTopComponentHelper(peonyEmailMessage, peonyOfflineEmail, purpose);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    displayComposeEmailTopComponentHelper(peonyEmailMessage, peonyOfflineEmail, purpose);
                }
            });
        }
    }
    
    private void displayComposeEmailTopComponentHelper(GardenEmailMessage gardenEmailMessage, PeonyOfflineEmail peonyOfflineEmail, OfflineMessageStatus purpose) {
        if (gardenEmailMessage == null){
            return;
        }
        try{
            ComposeEmailTopComponent aComposeEmailTopComponent = new ComposeEmailTopComponent();
            aComposeEmailTopComponent.addPeonyFaceEventListener(peonyEmailTopComponent);
            aComposeEmailTopComponent.initializeComposeEmailTopComponent(gardenEmailMessage, peonyOfflineEmail, purpose);
        }catch (Exception ex){
            //Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void displaySpamRuleTopComponent() {
        if (SwingUtilities.isEventDispatchThread()){
            popupSpamRuleTopComponentHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    popupSpamRuleTopComponentHelper();
                }
            });
        }
    }

    public SpamRuleTopComponent getExistingSpamRuleTopComponent() {
        TopComponent aTopComponent = PeonyFaceUtils.getExistingTopComponentSingleton("SpamRuleTopComponent");
        if (aTopComponent instanceof SpamRuleTopComponent){
            spamRuleTopComponent = (SpamRuleTopComponent)aTopComponent;
        }
        return spamRuleTopComponent;
    }
    
    private void popupSpamRuleTopComponentHelper() {
        //get it
        if (spamRuleTopComponent == null){
            spamRuleTopComponent = getExistingSpamRuleTopComponent();
        }
        //display it
        if (spamRuleTopComponent == null){
            spamRuleTopComponent = new SpamRuleTopComponent();
            spamRuleTopComponent.launchPeonyTopComponent(spamRuleTopComponent.getName());
        }else{
            spamRuleTopComponent.openTopComponent();
        }
    }
    
    /**
     * Load the emails from the local persistent location and/or the remote mail server
     * 
     * @throws Exception 
     */
    @Override
    public void openMailBox() throws Exception{
        if (SwingUtilities.isEventDispatchThread()){
            openMailBoxHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    openMailBoxHelper();
                }
            });
        }
    }
    
    private void openMailBoxHelper(){
        if (peonyEmailTopComponent == null){
            TopComponent tc = WindowManager.getDefault().findTopComponent("PeonyEmailBoxTopComponent");
            if (tc instanceof PeonyEmailBoxTopComponent){
                peonyEmailTopComponent = (PeonyEmailBoxTopComponent)tc;
            }
        }
        //display it
        if (peonyEmailTopComponent == null){
            peonyEmailTopComponent = new PeonyEmailBoxTopComponent();
        }
        peonyEmailTopComponent.launchPeonyTopComponent(peonyEmailTopComponent.getName());
        peonyEmailTopComponent.displayPeonyTopComponent();
    }

    @Override
    public void closeMailBox() throws Exception {
        if (SwingUtilities.isEventDispatchThread()){
            peonyEmailTopComponent.close();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    /**
                     * Special case: user opened PeonyOffice and then, just after 
                     * a few seconds, the user closed PeonyOffice. In this situation, 
                     * peonyEmailTopComponent, i.e., NULL, could be not built yet. 
                     */
                    if (peonyEmailTopComponent != null){
                        peonyEmailTopComponent.close();
                    }
                }
            });
        }
    }

    @Override
    public void displayPeonyEmailTaskAssignmentWindow(final PeonyCommAssignment aPeonyCommAssignment, final boolean readOnly) {
        if (SwingUtilities.isEventDispatchThread()){
            displayPeonyEmailTaskAssignmentWindowHelper(aPeonyCommAssignment, readOnly);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    displayPeonyEmailTaskAssignmentWindowHelper(aPeonyCommAssignment, readOnly);
                }
            });
        }
    }
    
    private void displayPeonyEmailTaskAssignmentWindowHelper(final PeonyCommAssignment aPeonyCommAssignment, final boolean readOnly) {
        if (aPeonyCommAssignment == null){
            return;
        }
        PeonyTaskViewTopComponent aPeonyTaskViewTopComponent = new PeonyTaskViewTopComponent();
        //tabs
        List<G02CommArchive> aG02CommArchiveList = aPeonyCommAssignment.getCommArchives();
        GardenEmailMessage aGardenEmailMessage; //notice: every aGardenEmailMessage's "from" the same employee
        List<Tab> tabs = new ArrayList<>();
        for (G02CommArchive aG02CommArchive : aG02CommArchiveList){
            aGardenEmailMessage = GardenEmailSerializer.getSingleton()
                    .deserializeToGardenEmailMessage(Paths.get(aG02CommArchive.getG02CommArchivePK().getCommArchiveLocation()));
            tabs.add(constructTaskTab(aGardenEmailMessage, readOnly));
        }
        aPeonyTaskViewTopComponent.initializePeonyTaskViewTopComponent(aPeonyCommAssignment, tabs);
    }
    
    private Tab constructTaskTab(GardenEmailMessage aGardenEmailMessage, boolean readOnly){
        Tab tab = new Tab(GardenEmailUtils.convertAddressListToText(aGardenEmailMessage.getFromList()));
        if (peonyEmailTopComponent != null){
            ViewEmailPanel aViewEmailPanel = new ViewEmailPanel(aGardenEmailMessage, peonyEmailTopComponent.getAssociatePeonyOfflineEmail(aGardenEmailMessage), readOnly);
            aViewEmailPanel.addPeonyFaceEventListener(peonyEmailTopComponent);
            tab.setContent(aViewEmailPanel);
        }
        return tab;
    }

    @Override
    public void closeService() {
        try {
            closeMailBox();
        } catch (Exception ex) {
            //Exceptions.printStackTrace(ex);
            PeonyFaceUtils.publishMessageOntoOutputWindow("Cannot close PeonyEmailService due to technical reasons.");
        }
    }
}
