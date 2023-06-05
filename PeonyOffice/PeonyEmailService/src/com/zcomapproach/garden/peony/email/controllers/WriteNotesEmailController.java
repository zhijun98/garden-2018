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

package com.zcomapproach.garden.peony.email.controllers;

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.email.GardenEmailMessage;
import com.zcomapproach.garden.peony.email.data.PeonyEmailTreeItemData;
import com.zcomapproach.garden.peony.email.events.PeonyMemoListSaved;
import com.zcomapproach.garden.peony.kernel.services.PeonyBusinessService;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G02Memo;
import com.zcomapproach.garden.persistence.entity.G02OfflineEmail;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.persistence.peony.PeonyMemo;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmail;
import com.zcomapproach.garden.persistence.peony.data.PeonyMemoList;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.garden.util.GardenData;
import com.zcomapproach.commons.nio.ZcaNio;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class WriteNotesEmailController extends PeonyEmailServiceController{
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Label selectedEmailsLabel;
    @FXML
    private TextArea writeNotesTextArea;
    @FXML
    private Button saveButton;
    @FXML
    private Button closeButton;
    
    private final List<TreeItem<PeonyEmailTreeItemData>> selectedEmailTreeItems;
    
    private final HashMap<PeonyEmployee, CheckBox> employeeCheckBoxMap;
    private final PeonyMemo targetNote;
    private final String onlineMailBoxAddress;

    public WriteNotesEmailController(String onlineMailBoxAddress, List<TreeItem<PeonyEmailTreeItemData>> selectedEmailTreeItems, PeonyMemo targetNote) {
        this.selectedEmailTreeItems = selectedEmailTreeItems;
        this.targetNote = targetNote;
        this.onlineMailBoxAddress = onlineMailBoxAddress;
        employeeCheckBoxMap = new HashMap<>();
    }
    
    private double dynamicRootPaneheight = 100;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String selectedMessages = getSelectedMessageBriefs();
        selectedEmailsLabel.setText(selectedMessages);
        
        dynamicRootPaneheight += selectedEmailsLabel.getHeight();
        
        if ((targetNote == null) || (targetNote.getMemo() == null)){
            writeNotesTextArea.setPromptText("Write notes for selected emails (max 450 characters).");
        }else{
            writeNotesTextArea.setText(targetNote.getMemo().getMemo());
            writeNotesTextArea.setEditable(false);
            saveButton.setDisable(true);
        }
        dynamicRootPaneheight += writeNotesTextArea.getPrefHeight();
        
        dynamicRootPaneheight += 50; //button zone
        
        rootPane.setPrefHeight(dynamicRootPaneheight);
        
        saveButton.setOnAction((ActionEvent event) -> {
            writeNotesForSelectedEmails();
        });
        
        closeButton.setOnAction((ActionEvent event) -> {
            broadcastPeonyFaceEventHappened(new CloseDialogRequest(true));
        });
    }

    public int getSuggestedRootPaneWidth() {
        return 610;
    }

    public int getSuggestedRootPaneHeight() {
        return (int)dynamicRootPaneheight;
    }

    private String getSelectedMessageBriefs() {
        String result = null;
        GardenEmailMessage aGardenEmailMessage;
        for (TreeItem<PeonyEmailTreeItemData> aSelectedEmailTreeItem : selectedEmailTreeItems){
            aGardenEmailMessage = aSelectedEmailTreeItem.getValue().emitPeonyEmailMessage();
            if (aGardenEmailMessage != null){
                if (result == null){
                    result = aGardenEmailMessage.retrieveEmailHeadline();
                }else{
                    result += ZcaNio.lineSeparator() + aGardenEmailMessage.retrieveEmailHeadline();
                }
            }
        }
        return result;
    }


    /**
     * Assign selected emails to employees: (1) post it onto the public message 
     * board; (2) hook tags onto the email message.
     */
    private void writeNotesForSelectedEmails() {
        Task<PeonyMemoList> assignEmailsTask = new Task<PeonyMemoList>(){
            @Override
            protected PeonyMemoList call() throws Exception {
                PeonyMemoList result = new PeonyMemoList();
                String notes = writeNotesTextArea.getText();
                if (ZcaValidator.isNullEmpty(notes) || (notes.length() > 450)){
                    updateMessage("Please write notes on emails with max 450 characters.");
                    return result;
                }
                
                PeonyEmployee currentPeonyEmployee = PeonyProperties.getSingleton().getCurrentLoginEmployee();
                PeonyMemo peonyNote;
                PeonyOfflineEmail aPeonyOfflineEmail;
                G02OfflineEmail aG02OfflineEmail;
                for (TreeItem<PeonyEmailTreeItemData> selectedEmailTreeItem : selectedEmailTreeItems){
                    aPeonyOfflineEmail = selectedEmailTreeItem.getValue().getAssociatedPeonyOfflineEmail();
                    peonyNote = new PeonyMemo();
                    peonyNote.setOperator(currentPeonyEmployee);
                    aG02OfflineEmail = aPeonyOfflineEmail.getOfflineEmail();
                    G02Memo memo = peonyNote.getMemo();
                    //memo.setEntityStatus(getTargetEntityType().value());
                    memo.setEntityType(GardenEntityType.OFFLINE_EMAIL_MESSAGE.name());
                    memo.setEntityStatus(GardenEntityType.PUBLIC_BOARD.value());
                    memo.setEntityUuid(aG02OfflineEmail.getOfflineEmailUuid());
                    memo.setEntityComboUuid(aG02OfflineEmail.getMailboxAddress()+aG02OfflineEmail.getMsgId());
                    memo.setMemo("[Email Note] " + notes);
                    if (ZcaValidator.isNullEmpty(memo.getMemoUuid())){
                        memo.setMemoUuid(GardenData.generateUUIDString());
                    }
                    memo.setOperatorAccountUuid(currentPeonyEmployee.getAccount().getAccountUuid());
                    memo.setTimestamp(new Date());
                    try {
                        PeonyMemo aPeonyMemo = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                                .storeEntity_XML(PeonyMemo.class, GardenRestParams.Business.storePeonyMemoRestParams(), peonyNote);
                        if (aPeonyMemo != null){
                            result.getPeonyMemoList().add(aPeonyMemo);
                        }
                    } catch (Exception ex) {
                        //Exceptions.printStackTrace(ex);
                        updateMessage("Cannot write notes for emails because of technical issues on the server. " + ex.getMessage());
                    }
                }//for
                return result;
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyMemoList aPeonyMemoListList = get();
                    if (aPeonyMemoListList.getPeonyMemoList().isEmpty()){
                        PeonyFaceUtils.displayErrorMessageDialog("Failed to write notes for emails. " + getMessage());
                    }else{
                        PeonyFaceUtils.displayInformationMessageDialog("Successfully write notes for email(s).");
                        broadcastPeonyFaceEventHappened(new PeonyMemoListSaved(aPeonyMemoListList, selectedEmailTreeItems));
                        broadcastPeonyFaceEventHappened(new CloseDialogRequest());
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("This operation failed. " + ex.getMessage());
                }
            }
        };
        assignEmailsTask.run();
    }
}
