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

package com.zcomapproach.garden.peony.view.controllers;

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.email.GardenCommType;
import com.zcomapproach.garden.email.GardenOperationType;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.kernel.data.PeonySmsContactor;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.persistence.entity.G02CommAssignment;
import com.zcomapproach.garden.persistence.entity.G02CommAssignmentTarget;
import com.zcomapproach.garden.persistence.entity.G02CommAssignmentTargetPK;
import com.zcomapproach.garden.persistence.peony.PeonyCommAssignment;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.rest.GardenRestParams;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javax.swing.JOptionPane;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class PeonySmsPaneController extends PeonyFaceController{
    @FXML
    private AnchorPane rootAnchorPane;
    @FXML
    private VBox contactorsBox;
    @FXML
    private TextArea smsMessageTextArea;
    @FXML
    private Button sendSmsButton;
    @FXML
    private Button cancelSmsButton;
    
    private final HashMap<PeonySmsContactor, CheckBox> peonySmsContactors;

    public PeonySmsPaneController(List<PeonySmsContactor> aPeonySmsContactorList) {
        this.peonySmsContactors = new HashMap<>();
        if (aPeonySmsContactorList != null){
            CheckBox aCheckBox;
            for (PeonySmsContactor aPeonySmsContactor : aPeonySmsContactorList){
                aCheckBox = new CheckBox();
                aCheckBox.setText(aPeonySmsContactor.getTextLine());
                peonySmsContactors.put(aPeonySmsContactor, aCheckBox);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Collection<CheckBox> aCheckBoxCollection = peonySmsContactors.values();
        for (CheckBox aCheckBox : aCheckBoxCollection){
            aCheckBox.setSelected(true);
            contactorsBox.getChildren().add(aCheckBox);
        }
        
        smsMessageTextArea.setPromptText("SMS message cannot be empty or lengthy (max 140 characters).");
        
        sendSmsButton.setOnAction((ActionEvent event) -> {
            sendSms();
        });
        
        cancelSmsButton.setOnAction((ActionEvent event) -> {
            broadcastPeonyFaceEventHappened(new CloseDialogRequest(true));
        });
    }

    private void sendSms() {
        if (peonySmsContactors.isEmpty()){
            PeonyFaceUtils.displayMessageDialog(PeonyLauncher.mainFrame, "No contactor(s) available for SMS.", "Error", JOptionPane.ERROR_MESSAGE);
            broadcastPeonyFaceEventHappened(new CloseDialogRequest(true));
        }else{
            Task<Boolean> sendSmsTask = new Task<Boolean>(){
                @Override
                protected Boolean call() throws Exception {
                    String smsMessage = smsMessageTextArea.getText();
                    if ((ZcaValidator.isNullEmpty(smsMessage)) || (smsMessage.length() > 140)){
                        updateMessage("SMS message cannot be empty or lengthy (max 140 characters).");
                        return false;
                    }else{
                        try {
                            if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to send out this SMS message?") == JOptionPane.YES_OPTION){
                                //prepare PeonyCommAssignment for logging this SMS
                                PeonyCommAssignment targetPeonyCommAssignment = new PeonyCommAssignment();
                                targetPeonyCommAssignment.assignEntityUuid();
                                PeonyEmployee currentLoginEmployee = PeonyProperties.getSingleton().getCurrentLoginEmployee();
                                String peonyLoginEmployeeUuid = currentLoginEmployee.getAccount().getAccountUuid();
                                //PeonyCommAssignment::G02CommAssignment
                                G02CommAssignment aG02CommAssignment = targetPeonyCommAssignment.getCommAssignment();
                                aG02CommAssignment.setDescription(smsMessage);
                                aG02CommAssignment.setCommType(GardenCommType.SMS.name());
                                aG02CommAssignment.setEmployeeUuid(peonyLoginEmployeeUuid);
                                aG02CommAssignment.setOperationType(GardenOperationType.SMS_MESSAGING.name());
                                //PeonyCommAssignment::List<G02CommArchive> - SMS has no archive
                                //PeonyCommAssignment::List<G02CommAssignmentTarget> - record receipients
                                List<G02CommAssignmentTarget> aG02CommAssignmentTargetList = targetPeonyCommAssignment.getCommAssignmentTargets();
                                //loop over every contactor for sending SMS
                                Set<PeonySmsContactor> aPeonySmsContactorSet = peonySmsContactors.keySet();
                                Iterator<PeonySmsContactor> itr = aPeonySmsContactorSet.iterator();
                                CheckBox aCheckBox;
                                PeonySmsContactor aPeonySmsContactor;
                                G02CommAssignmentTarget aG02CommAssignmentTarget;
                                String commUuid = aG02CommAssignment.getCommUuid();
                                int count = 0;
                                while(itr.hasNext()){
                                    aPeonySmsContactor = itr.next();
                                    aCheckBox = peonySmsContactors.get(aPeonySmsContactor);
                                    if (aCheckBox.isSelected()){
                                        //send SMS to every contactor in the list
                                        Lookup.getDefault().lookup(PeonyManagementService.class).getPeonyManagementRestClient()
                                                .requestOperation_XML(GardenRestParams.Management.sendSmsRestParams(aPeonySmsContactor.getMobileNumber(), smsMessage));
                                        count++;
                                        
                                        aG02CommAssignmentTarget = new G02CommAssignmentTarget();
                                        G02CommAssignmentTargetPK pkid = new G02CommAssignmentTargetPK();
                                        pkid.setCommUuid(commUuid);
                                        pkid.setTargetEntityUuid(aPeonySmsContactor.getEntityUuid());
                                        aG02CommAssignmentTarget.setG02CommAssignmentTargetPK(pkid);
                                        aG02CommAssignmentTarget.setTargetEntityType(aPeonySmsContactor.getEntityType());
                                        aG02CommAssignmentTarget.setTargetMemo(aPeonySmsContactor.getContactorName() + ": " + aPeonySmsContactor.getMobileNumber());
                                        aG02CommAssignmentTargetList.add(aG02CommAssignmentTarget);
                                    }
                                    Thread.sleep(1000); //fulfill Amazon's requirements
                                }//while-loop
                                
                                if (count == 0){
                                    updateMessage("Please check contactors for SMS. No SMS message sent.");
                                    return false;
                                }else{
                                    String msg = "SMS message sent to " + count + " selected contactor(s).";
                                    //save targetPeonyCommAssignment for logging
                                    PeonyCommAssignment thePeonyCommAssignment = Lookup.getDefault().lookup(PeonyManagementService.class).getPeonyManagementRestClient()
                                            .storeEntity_XML(PeonyCommAssignment.class, GardenRestParams.Management.storePeonyCommAssignmentRestParams(), targetPeonyCommAssignment);
                                    if (thePeonyCommAssignment == null){
                                        updateMessage(msg + " But cannot log it in the database.");
                                        return false;
                                    }
                                    updateMessage(msg);
                                }
                            }
                        } catch (Exception ex) {
                            Exceptions.printStackTrace(ex);
                            updateMessage("SMS failed. " + ex.getMessage());
                            return false;
                        }
                    }
                    return true;
                }

                @Override
                protected void succeeded() {
                    try {
                        boolean result = get();
                        if (result){
                            PeonyFaceUtils.displayInformationMessageDialog(getMessage());
                            broadcastPeonyFaceEventHappened(new CloseDialogRequest(true));
                        }else{
                            PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        PeonyFaceUtils.displayErrorMessageDialog(ex.getMessage());
                        broadcastPeonyFaceEventHappened(new CloseDialogRequest(true));
                    }
                }
            };
            this.getCachedThreadPoolExecutorService().submit(sendSmsTask);
        }
    }

}
