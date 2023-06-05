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

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.peony.view.events.PeonyDocumentTagReadyForDelete;
import com.zcomapproach.garden.peony.view.events.PeonyDocumentTagReadyForSave;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.entity.G02DocumentTag;
import com.zcomapproach.garden.persistence.peony.PeonyDocumentTag;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.garden.persistence.peony.data.PeonyPredefinedDocumentTag;
import com.zcomapproach.garden.persistence.peony.data.PeonyPredefinedDocumentTagType;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.openide.util.Exceptions;

/**
 *
 * @author zhijun98
 */
public class TaxpayerServiceTagProfileController extends PeonyTaxpayerServiceController implements PeonyFaceEventListener {
    
    @FXML
    private ScrollPane tagProfileScrollPane;
    
    @FXML
    private VBox serviceTagProfileVBox;

    private FlowPane customTagTypeFlowPane;
    
    private final PeonyTaxpayerCase targetPeonyTaxpayerCase;
    private final HashMap<String, PeonyDocumentTag> targetPeonyDocumentTagStorage = new HashMap<>();    //without OTHERS
    private final ArrayList<PeonyDocumentTag> othersPeonyDocumentTags = new ArrayList<>();

    public TaxpayerServiceTagProfileController(PeonyTaxpayerCase targetPeonyTaxpayerCase) {
        super(targetPeonyTaxpayerCase);
        this.targetPeonyTaxpayerCase = targetPeonyTaxpayerCase;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        List<G02DocumentTag>aG02DocumentTagList = targetPeonyTaxpayerCase.getDocumentTagList();
        for (G02DocumentTag aG02DocumentTag : aG02DocumentTagList){
            if (PeonyPredefinedDocumentTag.OTHERS.value().equalsIgnoreCase(aG02DocumentTag.getDocumentTagName())){
                othersPeonyDocumentTags.add(new PeonyDocumentTag(aG02DocumentTag));
            }else{
                targetPeonyDocumentTagStorage.put(aG02DocumentTag.getDocumentTagName(), new PeonyDocumentTag(aG02DocumentTag));
            }
        }
        List<PeonyPredefinedDocumentTagType> tageTypes = PeonyPredefinedDocumentTagType.getPeonyPredefinedDocumentTagTypeList(false);
        for (PeonyPredefinedDocumentTagType tagType : tageTypes){
            VBox tagTypeVBox = new VBox();
            tagTypeVBox.getStyleClass().add("peony-secondary-header");
            tagTypeVBox.setAlignment(Pos.CENTER_LEFT);
            Label tagTypeLabel = new Label(tagType.value());
            tagTypeLabel.getStyleClass().add("peony-regular-title-label");
            tagTypeVBox.getChildren().add(tagTypeLabel);
            serviceTagProfileVBox.getChildren().add(tagTypeVBox);
            if (PeonyPredefinedDocumentTagType.CUSTOM.equals(tagType)){
                initializeCustomServiceTagEntries();
            }else{
                initializeServiceTagEntries(tagType);
            }
        }
        tagProfileScrollPane.setVvalue(0.0);
    }

    private void initializeServiceTagEntries(PeonyPredefinedDocumentTagType tagType) {
        List<PeonyPredefinedDocumentTag> tags = PeonyPredefinedDocumentTag.getPeonyPredefinedDocumentTagListByOrder(tagType);
        TaxpayerServiceTagController aTaxpayerServiceTagController;
        PeonyDocumentTag aPeonyDocumentTag;
        try {
            FlowPane tagTypeFlowPane = new FlowPane();
            for (PeonyPredefinedDocumentTag tag : tags){
                aPeonyDocumentTag = targetPeonyDocumentTagStorage.get(tag.value());
                if (aPeonyDocumentTag == null){
                    aPeonyDocumentTag = PeonyPredefinedDocumentTag.createPredefinedPeonyDocumentTag(tag);
                    aPeonyDocumentTag.getDocumentTag().setDocumentQuantity(0);
                }
                aTaxpayerServiceTagController = new TaxpayerServiceTagController(aPeonyDocumentTag, false);
                aTaxpayerServiceTagController.addPeonyFaceEventListener(this);
                aTaxpayerServiceTagController.addPeonyFaceEventListenerList(this.getPeonyFaceEventListenerList());
                tagTypeFlowPane.getChildren().add(aTaxpayerServiceTagController.loadFxml());
            }
            serviceTagProfileVBox.getChildren().add(tagTypeFlowPane);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        getCachedThreadPoolExecutorService().submit(new Task<Void>(){
            @Override
            protected Void call() throws Exception {
                Thread.sleep(100);
                return null;
            }
            @Override
            protected void succeeded() {
                tagProfileScrollPane.setVvalue(tagProfileScrollPane.getVmin()); //go to the top
            }
        });
    }
    
    private void initializeCustomServiceTagEntries() {
        if (customTagTypeFlowPane != null){
            return;
        }
        
        TaxpayerServiceTagController aTaxpayerServiceTagController;
        
        try {
            customTagTypeFlowPane = new FlowPane();
            for (PeonyDocumentTag otherPeonyDocumentTag : othersPeonyDocumentTags){
                aTaxpayerServiceTagController = new TaxpayerServiceTagController(otherPeonyDocumentTag, false);
                aTaxpayerServiceTagController.addPeonyFaceEventListener(this);
                aTaxpayerServiceTagController.addPeonyFaceEventListenerList(this.getPeonyFaceEventListenerList());
                Pane aPane = aTaxpayerServiceTagController.loadFxml();
                aPane.setId(otherPeonyDocumentTag.getDocumentTag().getDocumentTagUuid());
                customTagTypeFlowPane.getChildren().add(aPane);
            }
            //Add-New-Other-Tags
            aTaxpayerServiceTagController = new TaxpayerServiceTagController(null, true);
            aTaxpayerServiceTagController.addPeonyFaceEventListener(this);
            aTaxpayerServiceTagController.addPeonyFaceEventListenerList(this.getPeonyFaceEventListenerList());
            customTagTypeFlowPane.getChildren().add(aTaxpayerServiceTagController.loadFxml());
            
            serviceTagProfileVBox.getChildren().add(customTagTypeFlowPane);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        getCachedThreadPoolExecutorService().submit(new Task<Void>(){
            @Override
            protected Void call() throws Exception {
                Thread.sleep(100);
                return null;
            }
            @Override
            protected void succeeded() {
                tagProfileScrollPane.setVvalue(tagProfileScrollPane.getVmin()); //go to the top
            }
        });
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof PeonyDocumentTagReadyForSave){
            handlePeonyDocumentTagReadyForSave((PeonyDocumentTagReadyForSave)event);
        }else if (event instanceof PeonyDocumentTagReadyForDelete){
            handlePeonyDocumentTagReadyForDelete((PeonyDocumentTagReadyForDelete)event);
        }else{
            this.broadcastPeonyFaceEventHappened(event);
        }
    }

    private void handlePeonyDocumentTagReadyForSave(PeonyDocumentTagReadyForSave peonyDocumentTagReadyForSave) {
        final PeonyDocumentTag aPeonyDocumentTag = peonyDocumentTagReadyForSave.getPeonyDocumentTag();
        if (aPeonyDocumentTag == null){
            return;
        }
        if (Platform.isFxApplicationThread()){
            handlePeonyDocumentTagReadyForSaveHelper(aPeonyDocumentTag);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    handlePeonyDocumentTagReadyForSaveHelper(aPeonyDocumentTag);
                }
            });
        }
    }

    private void handlePeonyDocumentTagReadyForSaveHelper(PeonyDocumentTag otherPeonyDocumentTag) {
        String id = otherPeonyDocumentTag.getDocumentTag().getDocumentTagUuid();
        Node node = findCustomNode(id);
        if (node != null){
            return;
        }
        TaxpayerServiceTagController aTaxpayerServiceTagController = new TaxpayerServiceTagController(otherPeonyDocumentTag, false);
        aTaxpayerServiceTagController.addPeonyFaceEventListener(this);
        aTaxpayerServiceTagController.addPeonyFaceEventListenerList(this.getPeonyFaceEventListenerList());
        try {
            Pane aPane = aTaxpayerServiceTagController.loadFxml();
            aPane.setId(id);
            customTagTypeFlowPane.getChildren().add(0, aPane);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void handlePeonyDocumentTagReadyForDelete(PeonyDocumentTagReadyForDelete peonyDocumentTagReadyForDelete) {
        final PeonyDocumentTag aPeonyDocumentTag = peonyDocumentTagReadyForDelete.getPeonyDocumentTag();
        if (aPeonyDocumentTag == null){
            return;
        }
        if (Platform.isFxApplicationThread()){
            handlePeonyDocumentTagReadyForDeleteHelper(aPeonyDocumentTag);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    handlePeonyDocumentTagReadyForDeleteHelper(aPeonyDocumentTag);
                }
            });
        }
    }

    private void handlePeonyDocumentTagReadyForDeleteHelper(PeonyDocumentTag aPeonyDocumentTag) {
        if ((aPeonyDocumentTag == null) || (aPeonyDocumentTag.getDocumentTag() == null)){
            return;
        }
        String id = aPeonyDocumentTag.getDocumentTag().getDocumentTagUuid();
        if (ZcaValidator.isNullEmpty(id)){
            return;
        }
        customTagTypeFlowPane.getChildren().remove(findCustomNode(id));
    }
    
    private Node findCustomNode(String id){
        Node culprit = null;
        List<Node> nodes = customTagTypeFlowPane.getChildren();
        for (Node node : nodes){
            if (id.equalsIgnoreCase(node.getId())){
                culprit = node;
                break;
            }
        }
        return culprit;
    }

}
