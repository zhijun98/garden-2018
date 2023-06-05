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
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.kernel.services.PeonyBusinessService;
import com.zcomapproach.garden.peony.kernel.services.PeonySecurityService;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.peony.view.events.PeonyMemoDeleted;
import com.zcomapproach.garden.peony.view.events.PeonyMemoSaved;
import com.zcomapproach.garden.peony.view.events.PeonyTaxFilingCaseUpdated;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G02Log;
import com.zcomapproach.garden.persistence.entity.G02Memo;
import com.zcomapproach.garden.persistence.entity.G02TaxFilingCase;
import com.zcomapproach.garden.persistence.peony.PeonyMemo;
import com.zcomapproach.garden.persistence.peony.PeonyTaxFilingCase;
import com.zcomapproach.garden.persistence.peony.data.PeonyLogName;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.garden.util.GardenData;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.swing.JOptionPane;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class MemoDataEntryController extends PeonyEntityOwnerFaceController{
    @FXML
    private VBox memoVBox;
    @FXML
    private Label memoDataEntryLabel;
    @FXML
    private HBox functionalButtonHBox;
    @FXML
    private GridPane topGridPane;
    @FXML
    private TextArea memoTextArea;

    private final boolean saveBtnRequired;
    private final boolean deleteBtnRequired;
    private final boolean closeBtnRequired;
    private final String memoTitle;
    private final PeonyMemo targetMemo;

    public MemoDataEntryController(String memoTitle, 
                                   PeonyMemo targetMemo, 
                                   boolean saveBtnRequired, 
                                   boolean deleteBtnRequired, 
                                   boolean closeBtnRequired,
                                   Object targetOwner) 
    {
        super(targetOwner);
        this.memoTitle = memoTitle;
        this.saveBtnRequired = saveBtnRequired;
        this.deleteBtnRequired = deleteBtnRequired;
        this.closeBtnRequired = closeBtnRequired;
        if (targetMemo == null){
            targetMemo = new PeonyMemo();
        }
        this.targetMemo = targetMemo;
    }

    @Override
    protected void decoratePeonyFaceAfterLoadingHelper(Scene scene) {
        memoTextArea.prefHeightProperty().bind(memoVBox.heightProperty().subtract(topGridPane.heightProperty()));
    }

    private boolean isSaveBtnRequired() {
        return saveBtnRequired;
    }

    private boolean isDeleteBtnRequired() {
        return deleteBtnRequired;
    }

    private boolean isCloseBtnRequired() {
        return closeBtnRequired;
    }

    public PeonyMemo getTargetMemo() {
        return targetMemo;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        memoDataEntryLabel.setText(memoTitle);
        
        PeonyFaceUtils.initializeTextArea(memoTextArea, targetMemo.getMemo().getMemo(), targetMemo.getMemo().getMemo(), "Memo content: at most 450 characters", false, this);
        
        if (isDeleteBtnRequired()){
            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction((ActionEvent event) -> {
                runDeletePeonyMemoTask();
                broadcastPeonyFaceEventHappened(new CloseDialogRequest());
            });
            functionalButtonHBox.getChildren().add(deleteButton);
            
            memoTextArea.setDisable(true);
        }
        if (isSaveBtnRequired()){
            Button saveButton = new Button("Save");
            saveButton.setOnAction((ActionEvent event) -> {
                if ((memoTextArea.getText() == null) 
                        || (memoTextArea.getText().trim().length() == 0) 
                        || (memoTextArea.getText().trim().length() >450))
                {
                    PeonyFaceUtils.displayErrorMessageDialog("Memo content is required and may have at most 450 characters");
                    memoTextArea.setStyle("-fx-border-color: #ff0000;");
                    return;
                }
                if (getTargetOwner() instanceof PeonyTaxFilingCase){
                    runSaveMemoForPeonyTaxFilingCaseTask((PeonyTaxFilingCase)getTargetOwner());
                }else{
                    runSavePeonyMemoTask();
                }
                broadcastPeonyFaceEventHappened(new CloseDialogRequest());
            });
            functionalButtonHBox.getChildren().add(saveButton);
            
            memoTextArea.setDisable(false);
        }
        
        if (!(isDeleteBtnRequired() || isSaveBtnRequired())){
            memoTextArea.setDisable(true);
        }
        
        if (isCloseBtnRequired()){
            Button closeButton = new Button("Close");
            closeButton.setOnAction((ActionEvent event) -> {
                broadcastPeonyFaceEventHappened(new CloseDialogRequest());
            });
            functionalButtonHBox.getChildren().add(closeButton);
        }
    }

    private void runSaveMemoForPeonyTaxFilingCaseTask(PeonyTaxFilingCase aPeonyTaxFilingCase) {
        Task<PeonyTaxFilingCase> saveMemoForPeonyTaxFilingCaseTask = new Task<PeonyTaxFilingCase>(){
            @Override
            protected PeonyTaxFilingCase call() throws Exception {
                String newMemo = memoTextArea.getText();
                String oldMemo = aPeonyTaxFilingCase.getMemo();
                //update memo with newMemo
                aPeonyTaxFilingCase.setMemo(newMemo);
                //create log for old memo...
                G02Log log = new G02Log();
                log.setLogUuid(GardenData.generateUUIDString());
                log.setLogName(PeonyLogName.DELETED_TAX_FILING_MEMO.name());
                log.setLogMessage("Deleted memo : " + oldMemo);
                log.setOperatorAccountUuid(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
                log.setCreated(new Date());
                log.setLoggedEntityType(GardenEntityType.TAX_FILING_CASE.name());
                log.setLoggedEntityUuid(aPeonyTaxFilingCase.getTaxFilingCase().getTaxFilingUuid());
                log.setEntityType(GardenEntityType.convertEnumNameToType(aPeonyTaxFilingCase.getTaxFilingCase().getEntityType()).name());
                log.setEntityUuid(aPeonyTaxFilingCase.getTaxFilingCase().getEntityUuid());
                log.setEntityDescription(null);
                //save log
                Lookup.getDefault().lookup(PeonySecurityService.class).log(log);
                
                //create log for new memo...
                log = new G02Log();
                log.setLogUuid(GardenData.generateUUIDString());
                log.setLogName(PeonyLogName.UPDATE_TAX_FILING_MEMO.name());
                log.setLogMessage("Updated memo to be: " + newMemo);
                log.setOperatorAccountUuid(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
                log.setCreated(new Date());
                log.setLoggedEntityType(GardenEntityType.TAX_FILING_CASE.name());
                log.setLoggedEntityUuid(aPeonyTaxFilingCase.getTaxFilingCase().getTaxFilingUuid());
                log.setEntityType(GardenEntityType.convertEnumNameToType(aPeonyTaxFilingCase.getTaxFilingCase().getEntityType()).name());
                log.setEntityUuid(aPeonyTaxFilingCase.getTaxFilingCase().getEntityUuid());
                log.setEntityDescription(null);
                //save log
                Lookup.getDefault().lookup(PeonySecurityService.class).log(log);
                
                //Save the tax-filing- case
                G02TaxFilingCase aG02TaxFilingCase = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                                .storeEntity_XML(G02TaxFilingCase.class, GardenRestParams.Business.storeTaxFilingCaseRestParams(), aPeonyTaxFilingCase.getTaxFilingCase());
                
                memoTextArea.setStyle(null);
                setDataEntryChanged(false);
                
                if (aG02TaxFilingCase == null){
                    updateMessage("Technical error was raised. Cannot save this memo.");
                    return null;
                }
                return aPeonyTaxFilingCase;
                
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayErrorMessageDialog("Technical error was raised. Cannot save this memo. " + getMessage());
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyTaxFilingCase aPeonyTaxFilingCase = get();
                    if (aPeonyTaxFilingCase == null){
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }else{
                        broadcastPeonyFaceEventHappened(new PeonyTaxFilingCaseUpdated(aPeonyTaxFilingCase));
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Technical error was raised. Cannot save this memo. " + ex.getMessage());
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(saveMemoForPeonyTaxFilingCaseTask);
    }

    private void runSavePeonyMemoTask() {
        Task<PeonyMemo> savePeonyMemoTask = new Task<PeonyMemo>(){
            @Override
            protected PeonyMemo call() throws Exception {
                targetMemo.setOperator(PeonyProperties.getSingleton().getCurrentLoginEmployee());
                G02Memo memo = targetMemo.getMemo();
                memo.setEntityStatus(getTargetEntityType().value());
                memo.setEntityType(getTargetEntityType().name());
                memo.setEntityUuid(getTargetEntityUuid());
                //memo.setInitialMemoUuid(parentMemoUuid); //this was defined by targetMemo from the outsider
                memo.setMemo(memoTextArea.getText());
                if (ZcaValidator.isNullEmpty(memo.getMemoUuid())){
                    memo.setMemoUuid(GardenData.generateUUIDString());
                }
                memo.setOperatorAccountUuid(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
                memo.setTimestamp(new Date());
                
                PeonyMemo result = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                            .storeEntity_XML(PeonyMemo.class, GardenRestParams.Business.storePeonyMemoRestParams(), targetMemo);
                
                if (result == null){
                    updateMessage("The server side cannot delete this record for some technical reason.");
                }else{
                    /**
                     * comment-out: too many logs
                     */
//                    G02Log log = createNewG02LogInstance(PeonyLogName.STORED_MEMO);
//                    log.setLoggedEntityType(GardenEntityType.MEMO.name());
//                    log.setLoggedEntityUuid(targetMemo.getMemo().getMemoUuid());
//                    PeonyProperties.getSingleton().log(log);
                }
                memoTextArea.setStyle(null);
                setDataEntryChanged(false);
                
                return result;
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayErrorMessageDialog("Technical error was raised. Cannot save this memo.");
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyMemo memo = get();
                    if (memo == null){
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }else{
                        broadcastPeonyFaceEventHappened(new PeonyMemoSaved(MemoDataEntryController.this.getTargetOwner(), memo));
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Technical error was raised. Cannot save this memo.");
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(savePeonyMemoTask);
    }

    private void runDeletePeonyMemoTask() {
        Task<G02Memo> deletePeonyMemoTask = new Task<G02Memo>(){
            @Override
            protected G02Memo call() throws Exception {
                if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure want to delete this memo?") != JOptionPane.YES_OPTION){
                    return null;
                }
                G02Memo result = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                            .deleteEntity_XML(G02Memo.class, GardenRestParams.Business.deleteMemoEntityRestParams(targetMemo.getMemo().getMemoUuid()));
                
                if (result == null){
                    updateMessage("The server side cannot delete this record for some technical reason.");
                }else{
                    G02Log log = createNewG02LogInstance(PeonyLogName.DELETED_MEMO);
                    log.setLoggedEntityType(GardenEntityType.MEMO.name());
                    log.setLoggedEntityUuid(targetMemo.getMemo().getMemoUuid());
                    PeonyProperties.getSingleton().log(log);
                }
                
                return result;
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayErrorMessageDialog("Technical error was raised. Cannot delete this memo.");
            }

            @Override
            protected void succeeded() {
                try {
                    G02Memo memo = get();
                    if (memo == null){
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }else{
                        broadcastPeonyFaceEventHappened(new PeonyMemoDeleted(targetMemo));
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("Technical error was raised. Cannot save this memo.");
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(deletePeonyMemoTask);
    }

}
