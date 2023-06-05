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
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import com.zcomapproach.garden.peony.taxpayer.data.TaxpayerContactType;
import com.zcomapproach.garden.peony.taxpayer.events.TaxpayerInfoContactChanged;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerInfo;
import com.zcomapproach.garden.rest.GardenRestParams;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javax.swing.JOptionPane;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class TaxpayerContactInfoController extends PeonyTaxpayerServiceController {
    @FXML
    private Label taxpayerNameLabel;
    @FXML
    private ComboBox<String> contactTypeComboBox;
    @FXML
    private TextField contactDataField;
    @FXML
    private Button saveButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button closeButton;
    
    private final G02TaxpayerInfo targetTaxpayerInfo;
    
    

    public TaxpayerContactInfoController(G02TaxpayerInfo targetTaxpayerInfo) {
        super(targetTaxpayerInfo);
        this.targetTaxpayerInfo = targetTaxpayerInfo;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //taxpayerNameLabel
        taxpayerNameLabel.setText(targetTaxpayerInfo.getLastName() + " " + targetTaxpayerInfo.getFirstName());
        //contactTypeComboBox
        PeonyFaceUtils.initializeComboBox(contactTypeComboBox, TaxpayerContactType.getEnumValueList(false), 
                                          null, null, "Contact method, e.g., email, phone, we-chat etc.", this);
        
        contactTypeComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                TaxpayerContactType type = TaxpayerContactType.convertEnumValueToType(newValue);
                switch (type){
                    case EMAIL:
                        contactDataField.setText(targetTaxpayerInfo.getEmail());
                        break;
                    case PHONE:
                        contactDataField.setText(targetTaxpayerInfo.getMobilePhone());
                        break;
                    case WECHAT:
                        contactDataField.setText(targetTaxpayerInfo.getSocialNetworkId());
                        break;
                    default:
                        break;
                }
            }
        });
        
        if (ZcaValidator.isNotNullEmpty(targetTaxpayerInfo.getMobilePhone())){
            contactTypeComboBox.getSelectionModel().select(TaxpayerContactType.PHONE.value());
            contactDataField.setText(targetTaxpayerInfo.getMobilePhone());
        }else if (ZcaValidator.isNotNullEmpty(targetTaxpayerInfo.getEmail())){
            contactTypeComboBox.getSelectionModel().select(TaxpayerContactType.EMAIL.value());
            contactDataField.setText(targetTaxpayerInfo.getEmail());
        }else if (ZcaValidator.isNotNullEmpty(targetTaxpayerInfo.getSocialNetworkId())){
            contactTypeComboBox.getSelectionModel().select(TaxpayerContactType.WECHAT.value());
            contactDataField.setText(targetTaxpayerInfo.getSocialNetworkId());
        }
        
        //addPersonnelAndContactButton
        saveButton.setOnAction((ActionEvent event) -> {
            saveTargetContactInfo();
        });
        
        deleteButton.setOnAction((ActionEvent event) -> {
            if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to delete this contact information?") == JOptionPane.YES_OPTION){
                deleteTargetContactInfo();
            }
        });
        
        closeButton.setOnAction((ActionEvent event) -> {
            setDataEntryChanged(false);
            broadcastPeonyFaceEventHappened(new CloseDialogRequest());
        });
    }
    
    private void saveTargetContactInfo() {
        Task<G02TaxpayerInfo> addBusinessContactorTask = new Task<G02TaxpayerInfo>(){
            @Override
            protected G02TaxpayerInfo call() throws Exception {
                if (ZcaValidator.isNullEmpty(contactTypeComboBox.getSelectionModel().getSelectedItem())){
                    updateMessage("Contact information is demanded.");
                    return null;
                }
                
                if (ZcaValidator.isNullEmpty(contactDataField.textProperty().getValue())){
                    updateMessage("Contact information is demanded.");
                    return null;
                }
                
                TaxpayerContactType type = TaxpayerContactType.convertEnumValueToType(contactTypeComboBox.getSelectionModel().getSelectedItem());
                switch (type){
                    case EMAIL:
                        targetTaxpayerInfo.setEmail(contactDataField.getText());
                        break;
                    case PHONE:
                        targetTaxpayerInfo.setMobilePhone(contactDataField.getText());
                        break;
                    case WECHAT:
                        targetTaxpayerInfo.setSocialNetworkId(contactDataField.getText());
                        break;
                    default:
                        break;
                }
                
                G02TaxpayerInfo aG02TaxpayerInfo = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient().storeEntity_XML(
                        G02TaxpayerInfo.class, GardenRestParams.Taxpayer.storeTaxpayerInfoRestParams(), targetTaxpayerInfo);
                setDataEntryChanged(false);
                if (aG02TaxpayerInfo == null){
                    updateMessage("Failed to save this data entry.");
                    return null;
                }else{
                    updateMessage("Successfully saved this data entry.");
                }
                
                return aG02TaxpayerInfo;
            }

            @Override
            protected void succeeded() {
                try {
                    G02TaxpayerInfo aG02TaxpayerInfo = get();
                    if (aG02TaxpayerInfo != null){
                        broadcastPeonyFaceEventHappened(new TaxpayerInfoContactChanged(targetTaxpayerInfo));
                        broadcastPeonyFaceEventHappened(new CloseDialogRequest());
                    }
                    String msg = getMessage();
                    if (ZcaValidator.isNotNullEmpty(msg)){
                        PeonyFaceUtils.displayInformationMessageDialog(msg);
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(addBusinessContactorTask);
    }
    
    private void deleteTargetContactInfo() {
        Task<G02TaxpayerInfo> addBusinessContactorTask = new Task<G02TaxpayerInfo>(){
            @Override
            protected G02TaxpayerInfo call() throws Exception {
                if (ZcaValidator.isNullEmpty(contactTypeComboBox.getSelectionModel().getSelectedItem())){
                    updateMessage("Contact type for deletion is demanded.");
                    return null;
                }
                
                TaxpayerContactType type = TaxpayerContactType.convertEnumValueToType(contactTypeComboBox.getSelectionModel().getSelectedItem());
                switch (type){
                    case EMAIL:
                        targetTaxpayerInfo.setEmail("");
                        break;
                    case PHONE:
                        targetTaxpayerInfo.setMobilePhone("");
                        break;
                    case WECHAT:
                        targetTaxpayerInfo.setSocialNetworkId("");
                        break;
                    default:
                        break;
                }
                
                G02TaxpayerInfo aG02TaxpayerInfo = Lookup.getDefault().lookup(PeonyTaxpayerService.class).getPeonyTaxpayerRestClient().storeEntity_XML(
                        G02TaxpayerInfo.class, GardenRestParams.Taxpayer.storeTaxpayerInfoRestParams(), targetTaxpayerInfo);
                setDataEntryChanged(false);
                if (aG02TaxpayerInfo == null){
                    updateMessage("Failed to delete this data entry.");
                    return null;
                }else{
                    updateMessage("Successfully deleted this data entry.");
                }
                
                return aG02TaxpayerInfo;
            }

            @Override
            protected void succeeded() {
                try {
                    G02TaxpayerInfo aG02TaxpayerInfo = get();
                    if (aG02TaxpayerInfo != null){
                        broadcastPeonyFaceEventHappened(new TaxpayerInfoContactChanged(targetTaxpayerInfo));
                        broadcastPeonyFaceEventHappened(new CloseDialogRequest());
                    }
                    String msg = getMessage();
                    if (ZcaValidator.isNotNullEmpty(msg)){
                        PeonyFaceUtils.displayInformationMessageDialog(msg);
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        getCachedThreadPoolExecutorService().submit(addBusinessContactorTask);
    }

}
