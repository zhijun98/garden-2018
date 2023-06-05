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

package com.zcomapproach.garden.peony.taxpayer.controllers;

import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.peony.view.events.PeonyDocumentTagReadyForDelete;
import com.zcomapproach.garden.peony.view.events.PeonyDocumentTagReadyForSave;
import com.zcomapproach.garden.persistence.entity.G02DocumentTag;
import com.zcomapproach.garden.persistence.peony.PeonyDocumentTag;
import com.zcomapproach.garden.persistence.peony.data.PeonyPredefinedDocumentTag;
import com.zcomapproach.garden.persistence.peony.data.PeonyPredefinedDocumentTagType;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;

/**
 *
 * @author zhijun98
 */
public class TaxpayerServiceTagController extends PeonyTaxpayerServiceController{
    @FXML
    private Label tagLabel;
    @FXML
    private ComboBox<Integer> qtyComboBox;
    @FXML
    private TextField memoTextField;
    @FXML
    private Button saveButton;
    
    private final PeonyDocumentTag targetPeonyDocumentTag;
    private final boolean forNewOthers;

    /**
     * 
     * @param targetPeonyDocumentTag - if this is NULL, forNewOthers is forced to be true
     * @param forNewOthers 
     */
    public TaxpayerServiceTagController(PeonyDocumentTag targetPeonyDocumentTag, boolean forNewOthers) {
        super(targetPeonyDocumentTag);
        if ((targetPeonyDocumentTag == null) || (forNewOthers)){
            targetPeonyDocumentTag = new PeonyDocumentTag();
            targetPeonyDocumentTag.getDocumentTag().setDocumentTagUuid(ZcaUtils.generateUUIDString());
            targetPeonyDocumentTag.getDocumentTag().setDocumentTagName(PeonyPredefinedDocumentTag.OTHERS.value());
            targetPeonyDocumentTag.getDocumentTag().setDocumentTagType(PeonyPredefinedDocumentTagType.CUSTOM.value());
            forNewOthers = true;
        }
        this.targetPeonyDocumentTag = targetPeonyDocumentTag;
        this.forNewOthers = forNewOthers;
    }

    public PeonyDocumentTag getTargetPeonyDocumentTag() {
        return targetPeonyDocumentTag;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (forNewOthers){
            qtyComboBox.setValue(1);
            qtyComboBox.setEditable(false);
            
            saveButton.setText("");
            saveButton.setTooltip(new Tooltip("Add a new service tag"));
            saveButton.setGraphic(PeonyGraphic.getImageView("add.png"));
        }else{
            populateDocumentTag(targetPeonyDocumentTag.getDocumentTag());
            saveButton.setText("");
            saveButton.setTooltip(new Tooltip("Save this service tag"));
            saveButton.setGraphic(PeonyGraphic.getImageView("page_save.png"));
        }
        saveButton.setOnAction(evt -> {
            savePeonyDocumentTagReady();
        });
    }

    private void savePeonyDocumentTagReady() {
        G02DocumentTag aDocumentTag = targetPeonyDocumentTag.getDocumentTag();
        aDocumentTag.setDocumentQuantity(qtyComboBox.getValue());
        aDocumentTag.setMemo(memoTextField.getText());
        if (qtyComboBox.getValue() == 0){
            broadcastPeonyFaceEventHappened(new PeonyDocumentTagReadyForDelete(targetPeonyDocumentTag));
            //broadcastPeonyFaceEventHappened(new CloseDialogRequest());
            return; //it requests to be deleted
        }else{
            String memo = memoTextField.getText();
            if (ZcaValidator.isNotNullEmpty(memo)){
                if (!memo.equalsIgnoreCase(aDocumentTag.getMemo())){
                    if (aDocumentTag.getDocumentQuantity() == 0){
                        PeonyFaceUtils.displayWarningMessageDialog("Please select the quantity for this tag before type in the memo.");
                    }else{
                        aDocumentTag.setMemo(memo);
                        highlightTagLabel(aDocumentTag);
                    }
                }
            }
            broadcastPeonyFaceEventHappened(new PeonyDocumentTagReadyForSave(new PeonyDocumentTag(targetPeonyDocumentTag.getDocumentTag())));
            if(forNewOthers){
                resetForNewOthersAfterSave(aDocumentTag);
            }
            //broadcastPeonyFaceEventHappened(new CloseDialogRequest());
        }
    }

    private void resetForNewOthersAfterSave(G02DocumentTag aDocumentTag) {
        if (forNewOthers){
            targetPeonyDocumentTag.setDocumentTag(new G02DocumentTag());
            targetPeonyDocumentTag.getDocumentTag().setDocumentTagUuid(ZcaUtils.generateUUIDString());
            targetPeonyDocumentTag.getDocumentTag().setDocumentTagName(PeonyPredefinedDocumentTag.OTHERS.value());
            targetPeonyDocumentTag.getDocumentTag().setDocumentTagType(PeonyPredefinedDocumentTagType.CUSTOM.value());
            memoTextField.setText("");
        }else{
            highlightTagLabel(aDocumentTag);
        }
    }

    private void populateDocumentTag(G02DocumentTag tag) {
        tagLabel.setText(tag.getDocumentTagName());
        for (int i = 0; i < 25; i++){
            qtyComboBox.getItems().add(i);
        }
        qtyComboBox.setValue(tag.getDocumentQuantity());
        memoTextField.setText(tag.getMemo());
        highlightTagLabel(tag);
    }
    
    private void highlightTagLabel(G02DocumentTag tag){
        if (tag.getDocumentQuantity() == null){
            return;
        }
        if (tag.getDocumentQuantity() > 0){
            tagLabel.setTextFill(Color.RED);
        }else{
            tagLabel.setTextFill(Color.BLACK);
        }
    }

}
