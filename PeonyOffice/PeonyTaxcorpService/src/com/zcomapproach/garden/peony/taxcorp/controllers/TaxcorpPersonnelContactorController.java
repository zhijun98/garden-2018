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

package com.zcomapproach.garden.peony.taxcorp.controllers;

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.peony.kernel.services.PeonyBusinessService;
import com.zcomapproach.garden.peony.taxcorp.events.BusinessContactorSaved;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.persistence.constant.BusinessContactorRole;
import com.zcomapproach.garden.persistence.constant.GardenContactType;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G02BusinessContactor;
import com.zcomapproach.garden.persistence.peony.PeonyTaxcorpCase;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.garden.util.GardenData;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class TaxcorpPersonnelContactorController extends PeonyTaxcorpServiceController {
    @FXML
    private ComboBox<String> contactorRoleComboBox;
    @FXML
    private TextField contactorFirstNameField;
    @FXML
    private TextField contactorLastNameField;
    @FXML
    private TextField contactorSsnField;
    @FXML
    private TextField contactorNationalityField;
    @FXML
    private DatePicker contactorBirthdayDatePicker;
    @FXML
    private ComboBox<String> contactTypeComboBox;
    @FXML
    private TextField contactDataField;
    @FXML
    private TextArea memoTextArea;
    @FXML
    private Button saveButton;
    @FXML
    private Button closeButton;
    
    private final PeonyTaxcorpCase targetPeonyTaxcorpCase;
    
    private final G02BusinessContactor targetBusinessContactor;

    public TaxcorpPersonnelContactorController(G02BusinessContactor targetBusinessContactor, PeonyTaxcorpCase targetPeonyTaxcorpCase) {
        super(targetPeonyTaxcorpCase);
        this.targetBusinessContactor = targetBusinessContactor;
        this.targetPeonyTaxcorpCase = targetPeonyTaxcorpCase;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /**
         * Contactor's data entry
         */
        //contactorRoleComboBox
        PeonyFaceUtils.initializeComboBox(contactorRoleComboBox, BusinessContactorRole.getEnumValueList(false), 
                                          targetBusinessContactor.getRole(), BusinessContactorRole.TAXCORP_OWNER.value(), 
                                          "Taxcorp's owner, primary contactor, or contactor", this);
        //contactorNameField
        PeonyFaceUtils.initializeTextField(contactorFirstNameField, targetBusinessContactor.getFirstName(), 
                                           null, "First name of the person in this corporate.", this);
        PeonyFaceUtils.initializeTextField(contactorLastNameField, targetBusinessContactor.getLastName(), 
                                           null, "Last name of the person in this corporate.", this);
        //contactorSsnField
        PeonyFaceUtils.initializeTextField(contactorSsnField, targetBusinessContactor.getSsn(), 
                                           null, "SSN, if available, of the person in this corporate.", this);
        //contactorNationalityField
        PeonyFaceUtils.initializeTextField(contactorNationalityField, targetBusinessContactor.getNationality(), 
                                           null, "Nationality, if available, of the person in this corporate.", this);
        //birthdayDatePicker
        if (targetBusinessContactor.getBirthday() == null){
            PeonyFaceUtils.initializeDatePicker(contactorBirthdayDatePicker, null, 
                                               null, "Birthday, if available, of the person in this corporate.", this);
        }else{
            PeonyFaceUtils.initializeDatePicker(contactorBirthdayDatePicker, ZcaCalendar.convertToLocalDate(targetBusinessContactor.getBirthday()), 
                                               null, "Birthday, if available, of the person in this corporate.", this);
        }
        //contactTypeComboBox
        PeonyFaceUtils.initializeComboBox(contactTypeComboBox, GardenContactType.getEnumValueList(false), 
                                          targetBusinessContactor.getContactType(), GardenContactType.MOBILE_PHONE.value(), 
                                          "Contact method, e.g., mobile-phone, email, home-phone, etc.", this);
        //contactDataField
        PeonyFaceUtils.initializeTextField(contactDataField, targetBusinessContactor.getContactInfo(), null, 
                                           "Contact information, e.g. phone number, correspoding to the selected contact type.", this);
        
        PeonyFaceUtils.initializeTextArea(memoTextArea, targetBusinessContactor.getMemo(), null, "Memo on this contact methods", this);
        //addPersonnelAndContactButton
        saveButton.setOnAction((ActionEvent event) -> {
            saveTargetBusinessContactor();
        });
        
        closeButton.setOnAction((ActionEvent event) -> {
            clearNewBusinessContactorDataEntry();
            broadcastPeonyFaceEventHappened(new CloseDialogRequest());
        });
    }
    
    private void saveTargetBusinessContactor() {
        Task<G02BusinessContactor> addBusinessContactorTask = new Task<G02BusinessContactor>(){
            @Override
            protected G02BusinessContactor call() throws Exception {
                
                if ((ZcaValidator.isNullEmpty(contactorFirstNameField.textProperty().getValue())) 
                        || (ZcaValidator.isNullEmpty(contactorLastNameField.textProperty().getValue())))
                {
                    updateMessage("First and last name are demanded.");
                    return null;
                }
                
                if (ZcaValidator.isNullEmpty(contactorRoleComboBox.getSelectionModel().getSelectedItem())){
                    updateMessage("Contactor type is demanded and, at least, one taxcorp owner should be added.");
                    return null;
                }
                
                if (ZcaValidator.isNullEmpty(contactTypeComboBox.getSelectionModel().getSelectedItem())){
                    updateMessage("Contact information is demanded.");
                    return null;
                }
                
                if (ZcaValidator.isNullEmpty(contactDataField.textProperty().getValue())){
                    updateMessage("Contact information is demanded.");
                    return null;
                }
                if (ZcaValidator.isNullEmpty(targetBusinessContactor.getBusinessContactorUuid())){
                    targetBusinessContactor.setBusinessContactorUuid(GardenData.generateUUIDString());
                }
                targetBusinessContactor.setSsn(contactorSsnField.textProperty().getValue());
                targetBusinessContactor.setNationality(contactorNationalityField.textProperty().getValue());
                if (contactorBirthdayDatePicker.getValue() != null){
                    targetBusinessContactor.setBirthday(ZcaCalendar.convertToDate(contactorBirthdayDatePicker.getValue()));
                }
                targetBusinessContactor.setFirstName(contactorFirstNameField.textProperty().getValue());
                targetBusinessContactor.setLastName(contactorLastNameField.textProperty().getValue());
                targetBusinessContactor.setContactInfo(contactDataField.textProperty().getValue());
                targetBusinessContactor.setContactType(contactTypeComboBox.getSelectionModel().getSelectedItem());
                targetBusinessContactor.setEntityType(GardenEntityType.TAXCORP_CASE.name());
                targetBusinessContactor.setEntityUuid(targetPeonyTaxcorpCase.getTaxcorpCase().getTaxcorpCaseUuid());
                targetBusinessContactor.setRole(contactorRoleComboBox.getSelectionModel().getSelectedItem());
                targetBusinessContactor.setMemo(memoTextArea.textProperty().getValue());
                
                G02BusinessContactor theG02BusinessContactor = Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient()
                                                                .storeEntity_XML(G02BusinessContactor.class, 
                                                                                GardenRestParams.Business.storeBusinessContactorRestParams(), 
                                                                                targetBusinessContactor);
                if (theG02BusinessContactor == null){
                    updateMessage("The contactor is added in the list WITHOUT saving yet. You need click SAVE-TAXCORP button to save it.");
                    targetPeonyTaxcorpCase.getBusinessContactorList().add(targetBusinessContactor);
                }else{
                    updateMessage("The contactor has been successfully added in the list.");
                }
                
                return targetBusinessContactor;
            }

            @Override
            protected void succeeded() {
                try {
                    G02BusinessContactor aG02BusinessContactor = get();
                    if (aG02BusinessContactor != null){
                        clearNewBusinessContactorDataEntry();
                        broadcastPeonyFaceEventHappened(new BusinessContactorSaved(aG02BusinessContactor));
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

    private void clearNewBusinessContactorDataEntry() {
        contactorSsnField.setStyle(null);
        contactorNationalityField.setStyle(null);
        contactorFirstNameField.setStyle(null);
        contactorLastNameField.setStyle(null);
        contactDataField.setStyle(null);
        contactTypeComboBox.getSelectionModel().clearSelection();
        contactorRoleComboBox.getSelectionModel().clearSelection();
        memoTextArea.setStyle(null);
        
        setDataEntryChanged(false);
    }
    
}
