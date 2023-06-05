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

import com.zcomapproach.commons.ZcaRegex;
import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.peony.view.events.PeonyDocumentTagReadyForSave;
import com.zcomapproach.garden.peony.view.events.PeonyDocumentTagReadyForDelete;
import com.zcomapproach.garden.persistence.entity.G02DocumentTag;
import com.zcomapproach.garden.persistence.peony.PeonyDocumentTag;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javax.swing.JOptionPane;

/**
 *
 * @author zhijun98
 */
public class DocumentTagDataEntryController extends PeonyFaceController{
    @FXML
    private GridPane topGridPane;
    @FXML
    private VBox documentTagVBox;
    @FXML
    private Label documentTagDataEntryLabel;
    @FXML
    private TextField documentTagNameTextField;
    @FXML
    private TextField documentQuantityTextField;
    @FXML
    private TextArea documentTagTextArea;
    @FXML
    private Button saveButton;
    @FXML
    private Button closeButton;
    @FXML
    private Button deleteButton;

    private final PeonyDocumentTag targetPeonyDocumentTag;

    public DocumentTagDataEntryController(PeonyDocumentTag targetPeonyDocumentTag) {
        if (targetPeonyDocumentTag == null){
            targetPeonyDocumentTag = new PeonyDocumentTag();
        }
        this.targetPeonyDocumentTag = targetPeonyDocumentTag;
    }

    @Override
    protected void decoratePeonyFaceAfterLoadingHelper(Scene scene) {
        documentTagTextArea.prefHeightProperty().bind(documentTagVBox.heightProperty().subtract(topGridPane.heightProperty()));
    }

    public PeonyDocumentTag getTargetPeonyDocumentTag() {
        return targetPeonyDocumentTag;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        documentTagDataEntryLabel.setText("Tag Attributes");
        //tag name
        PeonyFaceUtils.initializeTextField(documentTagNameTextField, 
                targetPeonyDocumentTag.getDocumentTag().getDocumentTagName(), 
                "", "e.g. W2-form, 1099c-from...", this);
        //quantity
        String qty = "";
        if (targetPeonyDocumentTag.getDocumentTag().getDocumentQuantity() != null){
            qty = targetPeonyDocumentTag.getDocumentTag().getDocumentQuantity().toString();
        }
        PeonyFaceUtils.initializeTextField(documentQuantityTextField, qty, qty, 
                "Quantity of documents with such a tag", this);
        //mmeo
        PeonyFaceUtils.initializeTextArea(documentTagTextArea, 
                targetPeonyDocumentTag.getDocumentTag().getMemo(), null, 
                "Short memo on this tag which may have at most 250 characters.", this);
        
        saveButton.setOnAction((ActionEvent event) -> {
            //uuid
            G02DocumentTag aG02DocumentTag = targetPeonyDocumentTag.getDocumentTag();
            if (ZcaValidator.isNullEmpty(aG02DocumentTag.getDocumentTagUuid())){
                aG02DocumentTag.setDocumentTagUuid(ZcaUtils.generateUUIDString());
            }
            //document name
            if (ZcaValidator.isNullEmpty(documentTagNameTextField.getText())){
                documentTagNameTextField.setStyle("-fx-border-color: #ff0000;");
                PeonyFaceUtils.displayErrorMessageDialog("Document-name cannot be empty.");
                return;
            }
            aG02DocumentTag.setDocumentTagName(documentTagNameTextField.getText());
            //quantity
            String qtyText = documentQuantityTextField.getText();
            if ((ZcaValidator.isNullEmpty(qtyText) || (!ZcaRegex.isNumberString(qtyText)))){
                documentQuantityTextField.setStyle("-fx-border-color: #ff0000;");
                PeonyFaceUtils.displayErrorMessageDialog("Quantity cannot be empty or zero.");
                return;
            }
            aG02DocumentTag.setDocumentQuantity(Integer.parseInt(qtyText));
            //memo
            String memo = documentTagTextArea.getText();
            if (ZcaValidator.isNotNullEmpty(memo) && (memo.length() > 250)){
                documentTagTextArea.setStyle("-fx-border-color: #ff0000;");
                PeonyFaceUtils.displayErrorMessageDialog("Memo can only have at most 250 characters.");
                return;
            }
            aG02DocumentTag.setMemo(memo);
            
            setDataEntryChanged(false);
            
            super.broadcastPeonyFaceEventHappened(new PeonyDocumentTagReadyForSave(targetPeonyDocumentTag));
            super.broadcastPeonyFaceEventHappened(new CloseDialogRequest());
        });
        
        closeButton.setOnAction((ActionEvent event) -> {
            super.broadcastPeonyFaceEventHappened(new CloseDialogRequest());
        });
        
        deleteButton.setVisible(ZcaValidator.isNotNullEmpty(targetPeonyDocumentTag.getDocumentTag().getDocumentTagUuid()));
        deleteButton.setOnAction((ActionEvent event) -> {
            if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to delete this file tag?") == JOptionPane.YES_OPTION){
                super.broadcastPeonyFaceEventHappened(new PeonyDocumentTagReadyForDelete(targetPeonyDocumentTag));
                super.broadcastPeonyFaceEventHappened(new CloseDialogRequest());
            }
        });
    }

}
