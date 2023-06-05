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
import com.zcomapproach.garden.peony.kernel.services.PeonyBusinessService;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G02Memo;
import com.zcomapproach.garden.persistence.peony.PeonyCommAssignment;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.persistence.peony.PeonyMemo;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.garden.util.GardenData;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class PeonyTaskViewPaneController extends PeonyFaceController{
    @FXML
    private TabPane assignedEmailTabPane;
    @FXML
    private FlowPane whoAssignFlowPane;
    @FXML
    private FlowPane whoWasAssignedFlowPane;
    @FXML
    private FlowPane descriptionFlowPane;
    @FXML
    private ListView<PeonyMemo> historicalMemoListView;
    @FXML
    private TextArea memoTextArea;
    @FXML
    private Button addMemoButton;
    
    private final PeonyCommAssignment targetPeonyCommAssignment;
    private final List<Tab> taskTabList;
    
    public PeonyTaskViewPaneController(PeonyCommAssignment targetPeonyCommAssignment, List<Tab> taskTabList) {
        this.targetPeonyCommAssignment = targetPeonyCommAssignment;
        if (taskTabList == null){
            taskTabList = new ArrayList<>();
        }
        this.taskTabList = taskTabList;
    }

    public PeonyCommAssignment getTargetPeonyCommAssignment() {
        return targetPeonyCommAssignment;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //assignedEmailTabPane
        assignedEmailTabPane.getTabs().clear();
        assignedEmailTabPane.getTabs().addAll(taskTabList);
        //whoAssignFlowPane
        Label whoAssignedLabel = new Label(targetPeonyCommAssignment.getOperator().getEmployeeInfo().getWorkEmail());
        whoAssignFlowPane.getChildren().add(whoAssignedLabel);
        //whoWasAssignedFlowPane
        Label whoWasAssignedLabel = new Label(targetPeonyCommAssignment.retrieveReciepientListText());
        whoWasAssignedFlowPane.getChildren().add(whoWasAssignedLabel);
        //descriptionFlowPane
        Label descriptionLabel = new Label(targetPeonyCommAssignment.getCommAssignment().getDescription());
        descriptionFlowPane.getChildren().add(descriptionLabel);
        
        //historicalMemoListView
        historicalMemoListView.setItems(FXCollections.observableArrayList(targetPeonyCommAssignment.getCommMemos()));
        
        //memoTextArea
        memoTextArea.setText("");
        memoTextArea.setPromptText("Please add your memo with at most 450 characters");
        
        //addMemoButton
        addMemoButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                String memo = memoTextArea.getText();
                if ((ZcaValidator.isNullEmpty(memo)) || (memo.length() > 450)){
                    PeonyFaceUtils.displayErrorMessageDialog("Please add your memo with at most 450 characters.");
                }else{
                    addMemo(memo);
                }
            }
        });
    }

    private void addMemo(String memo) {
        Task<Boolean> addMemoTask = new Task<Boolean>(){
            @Override
            protected Boolean call() throws Exception {
                PeonyEmployee loginEmployee = PeonyProperties.getSingleton().getCurrentLoginEmployee();
                PeonyMemo thePeonyMemo = new PeonyMemo();
                //operator
                thePeonyMemo.setOperator(loginEmployee);
                //memo
                G02Memo aG02Memo = thePeonyMemo.getMemo();
                aG02Memo.setMemo(memo);
                aG02Memo.setEntityStatus(GardenEntityType.EMAIL_ASSIGNMENT.value());
                aG02Memo.setEntityType(GardenEntityType.EMAIL_ASSIGNMENT.name());
                aG02Memo.setEntityUuid(targetPeonyCommAssignment.getCommAssignment().getCommUuid());
                aG02Memo.setMemoUuid(GardenData.generateUUIDString());
                aG02Memo.setOperatorAccountUuid(loginEmployee.getAccount().getAccountUuid());
                aG02Memo.setTimestamp(new Date());
                thePeonyMemo.setMemo(aG02Memo);
                //saving...
                PeonyMemo aPeonyMemo;
                try {
                    aPeonyMemo = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                            .storeEntity_XML(PeonyMemo.class, GardenRestParams.Business.storePeonyMemoRestParams(), thePeonyMemo);

                    if (aPeonyMemo == null){
                        updateMessage("Failed to save memo. ");
                        return false;
                    }else{
                        targetPeonyCommAssignment.getCommMemos().add(thePeonyMemo);
                        updateMessage("Successfully saved memo. (" + ZcaCalendar.convertToMMddyyyyHHmmss(new Date(), "-", "@", ":") + ")");
                        return true;
                    }
                } catch (Exception ex) {
                    updateMessage("Failed to save memo. " + ex.getMessage());
                    return false;
                }
            } 

            @Override
            protected void succeeded() {
                try {
                    if (get()){
                        memoTextArea.setText("");
                        historicalMemoListView.setItems(FXCollections.observableArrayList(targetPeonyCommAssignment.getCommMemos()));
                        PeonyFaceUtils.displayInformationMessageDialog(getMessage());
                    }else{
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("[TECH ERR] " + ex.getMessage());
                 }
            }
        };
        getCachedThreadPoolExecutorService().submit(addMemoTask);
    }

}
