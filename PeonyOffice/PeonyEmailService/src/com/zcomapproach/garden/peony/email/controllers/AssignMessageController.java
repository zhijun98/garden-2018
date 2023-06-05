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
import com.zcomapproach.garden.email.data.GardenEmailMessageTag;
import com.zcomapproach.garden.peony.email.data.PeonyEmailTreeItemData;
import com.zcomapproach.garden.peony.email.events.PeonyEmailTagListCreated;
import com.zcomapproach.garden.peony.kernel.services.PeonyKernelService;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxcorpService;
import com.zcomapproach.garden.peony.kernel.services.PeonyTaxpayerService;
import com.zcomapproach.garden.peony.resources.images.PeonyGraphic;
import com.zcomapproach.garden.peony.security.PeonyPrivilege;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyDataUtils;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.dialogs.PeonyEmployeeProfileDialog;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.peony.view.events.PeonyEmployeeSelected;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.events.PeonyTaxcorpCaseSelected;
import com.zcomapproach.garden.peony.view.events.PeonyTaxpayerCaseListSelected;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G02EmailAtt;
import com.zcomapproach.garden.persistence.entity.G02EmailAttPK;
import com.zcomapproach.garden.persistence.entity.G02EmailTag;
import com.zcomapproach.garden.persistence.entity.G02EmailTaxcase;
import com.zcomapproach.garden.persistence.entity.G02EmailTaxcasePK;
import com.zcomapproach.garden.persistence.entity.G02Memo;
import com.zcomapproach.garden.persistence.entity.G02OfflineEmail;
import com.zcomapproach.garden.persistence.entity.G02TaxcorpCase;
import com.zcomapproach.garden.persistence.peony.PeonyEmailAtt;
import com.zcomapproach.garden.persistence.peony.PeonyEmailTag;
import com.zcomapproach.garden.persistence.peony.PeonyEmailTagList;
import com.zcomapproach.garden.persistence.peony.PeonyEmailTaxcase;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmail;
import com.zcomapproach.garden.persistence.peony.PeonyTaxcorpCase;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.garden.util.GardenData;
import com.zcomapproach.commons.nio.ZcaNio;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class AssignMessageController extends PeonyEmailServiceController implements PeonyFaceEventListener{
    @FXML
    private Label selectedEmailsLabel;
    
    @FXML
    private GridPane assignSelectedEmailsGridPane;
    
    @FXML
    private ListView<String> attentionListView;
    
    @FXML
    private ListView<String> selectedTaxCaseListView;
    
    @FXML
    private TextArea notesTextArea;
    
    @FXML
    private Button attentionButton;
    
    @FXML
    private Button taxCaseButton;
    
    @FXML
    private Button assignButton;
    
    @FXML
    private Button closeButton;
    
    private final HashMap<String, PeonyEmployee> employeeStorage = new HashMap<>();
    private final HashMap<String, Object> taxcaseStorage = new HashMap<>();
    
    private final List<TreeItem<PeonyEmailTreeItemData>> selectedEmailTreeItems;
    private final PeonyEmailTag targetEmailTag;
    private final String onlineMailBoxAddress;  //reserved for the future

    public AssignMessageController(String onlineMailBoxAddress, List<TreeItem<PeonyEmailTreeItemData>> selectedEmailTreeItems, PeonyEmailTag targetEmailTag) {
        this.targetEmailTag = targetEmailTag;
        this.selectedEmailTreeItems = selectedEmailTreeItems;
        this.onlineMailBoxAddress = onlineMailBoxAddress;
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        if (event instanceof PeonyEmployeeSelected){
            handlePeonyEmployeeSelected((PeonyEmployeeSelected)event);
        }else if (event instanceof PeonyTaxpayerCaseListSelected){
            List<PeonyTaxpayerCase> aPeonyTaxpayerCaseList = ((PeonyTaxpayerCaseListSelected)event).getSelectedPeonyTaxpayerCaseList();
            for (PeonyTaxpayerCase aPeonyTaxpayerCase : aPeonyTaxpayerCaseList){
                addSelectedTaxcase(aPeonyTaxpayerCase);
            }
        }else if (event instanceof PeonyTaxcorpCaseSelected){
            addSelectedTaxcase(((PeonyTaxcorpCaseSelected)event).getSelectedPeonyTaxcorpCase());
        }
    }

    private void addSelectedTaxcase(final Object taxcaseObj) {
        if (taxcaseObj == null){
            return;
        }
        if (Platform.isFxApplicationThread()){
            addSelectedTaxcaseHelper(taxcaseObj);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    addSelectedTaxcaseHelper(taxcaseObj);
                }
            });
        }
    }
    private void addSelectedTaxcaseHelper(final Object taxcaseObj) {
        List<String> elements = new ArrayList<>();
        String key = constructTaxcaseTextLine(taxcaseObj);
        if (!taxcaseStorage.containsKey(key)){
            elements.add(key);
            taxcaseStorage.put(key, taxcaseObj);
        }
        selectedTaxCaseListView.getItems().addAll(elements);
    
    }

    private void handlePeonyEmployeeSelected(final PeonyEmployeeSelected peonyEmployeeSelected) {
        if (peonyEmployeeSelected == null){
            return;
        }
        List<PeonyEmployee> aPeonyEmployeeList = peonyEmployeeSelected.getSelectedPeonyEmployeeList();
        if ((aPeonyEmployeeList == null) || (aPeonyEmployeeList.isEmpty())){
            return;
        }
        if (Platform.isFxApplicationThread()){
            addSelectedEmployees(aPeonyEmployeeList);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    addSelectedEmployees(aPeonyEmployeeList);
                }
            });
        }
    }
    
    private void addSelectedEmployees(List<PeonyEmployee> peonyEmployeeSelected) {
        List<String> elements = new ArrayList<>();
        String key;
        for (PeonyEmployee aPeonyEmployee : peonyEmployeeSelected){
            key = aPeonyEmployee.getTextLine();
            if (!employeeStorage.containsKey(key)){
                elements.add(key);
                employeeStorage.put(key, aPeonyEmployee);
            }
        }//for-loop
        attentionListView.getItems().addAll(elements);
    }

    private void removeSelectedTaxCase(Object taxcaseObj) {
        if (taxcaseObj == null){
            return;
        }
        String key = constructTaxcaseTextLine(taxcaseObj);
        if (taxcaseStorage.containsKey(key)){
            taxcaseStorage.remove(key);
            selectedTaxCaseListView.getItems().remove(key);
        }
    }
    
    private void removeSelectedEmployee(PeonyEmployee aPeonyEmployee) {
        if (aPeonyEmployee == null){
            return;
        }
        String key = aPeonyEmployee.getTextLine();
        if (employeeStorage.containsKey(key)){
            employeeStorage.remove(key);
            attentionListView.getItems().remove(key);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeSelectedEmailsLabel();
        initializeAttentionListView();
        initializeSelectedTaxCaseListView();
        initializeNotesTextArea();
        initializeFunctionalButtons();
    }

    private void initializeSelectedEmailsLabel() {
        
        selectedEmailsLabel.maxWidthProperty().bind(assignSelectedEmailsGridPane.widthProperty().add(-60));
        selectedEmailsLabel.maxHeightProperty().bind(assignSelectedEmailsGridPane.heightProperty().add(-60));
        
        String result = null;
        GardenEmailMessage aGardenEmailMessage;
        for (TreeItem<PeonyEmailTreeItemData> aSelectedEmailTreeItem : selectedEmailTreeItems){
            aGardenEmailMessage = aSelectedEmailTreeItem.getValue().emitPeonyEmailMessage();
            if (aGardenEmailMessage != null){
                if (result == null){
                    result = "> " + aGardenEmailMessage.retrieveEmailHeadline();
                }else{
                    result += ZcaNio.lineSeparator() + "> " + aGardenEmailMessage.retrieveEmailHeadline();
                }
            }
        }
        selectedEmailsLabel.setText(result);
    }

    private void initializeNotesTextArea() {
        /**
         * initialize values
         */
        if (targetEmailTag != null){
            notesTextArea.setText(targetEmailTag.getEmailTag().getTagMemo());
            notesTextArea.setEditable(false);
        }
    }

    private void initializeFunctionalButtons() {
        attentionButton.setGraphic(PeonyGraphic.getImageView("add.png"));
        attentionButton.setOnAction((ActionEvent event) -> {
            displayPeonyEmployeePickerDialog();
        });
        taxCaseButton.setGraphic(PeonyGraphic.getImageView("add.png"));
        taxCaseButton.setOnAction((ActionEvent event) -> {
            displayPeonyTaxCasePickerDialog();
        });
        assignButton.setGraphic(PeonyGraphic.getImageView("mail_to_friend.png"));
        assignButton.setOnAction((ActionEvent event) -> {
            executeAssignment();
        });
        closeButton.setGraphic(PeonyGraphic.getImageView("cancel.png"));
        closeButton.setOnAction((ActionEvent event) -> {
            broadcastPeonyFaceEventHappened(new CloseDialogRequest(true));
        });
        /**
         * initialize values
         */
        if (targetEmailTag != null){
            attentionButton.setDisable(true);
            taxCaseButton.setDisable(true);
            assignButton.setDisable(true);
        }
    }
    
    private void initializeAttentionListView() {
        final ContextMenu contextMenu = new ContextMenu();
        
        MenuItem viewMenuItem = new MenuItem("View");
        viewMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                viewPeonyEmployeeProfile();
            }
            private void viewPeonyEmployeeProfile() {
                PeonyEmployee aPeonyEmployee = employeeStorage.get(attentionListView.getSelectionModel().getSelectedItem());
                if (aPeonyEmployee == null){
                    PeonyFaceUtils.displayErrorMessageDialog("Please select one employee from the list.");
                    return;
                }
                if (aPeonyEmployee.getEmployeeInfo().getEmployeeAccountUuid().equalsIgnoreCase(PeonyProperties.getSingleton().getCurrentLoginUserUuid())){
                    viewPeonyEmployeeProfileHelper(aPeonyEmployee);
                }else{
                    if (PeonyProperties.getSingleton().isPrivilegeAuthorized(PeonyPrivilege.MANAGE_EMPLOYEE_PROFILES)){
                        viewPeonyEmployeeProfileHelper(aPeonyEmployee);
                    }else{
                        PeonyFaceUtils.displayWarningMessageDialog("You are not authorized to view other employee's profile.");
                    }
                }
            }

            private void viewPeonyEmployeeProfileHelper(PeonyEmployee aPeonyEmployee) {
                PeonyEmployeeProfileDialog aPeonyEmployeeProfileDialog = new PeonyEmployeeProfileDialog(null, true);
                aPeonyEmployeeProfileDialog.launchPeonyEmployeeProfileDialog(aPeonyEmployee.getTextLine(), aPeonyEmployee);
            }
        });
        contextMenu.getItems().add(viewMenuItem);
        MenuItem addMenuItem = new MenuItem("Add");
        addMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                displayPeonyEmployeePickerDialog();
            }
        });
        contextMenu.getItems().add(addMenuItem);
        MenuItem removeMenuItem = new MenuItem("Remove");
        removeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                removeSelectedEmployee(employeeStorage.get(attentionListView.getSelectionModel().getSelectedItem()));
            }
        });
        contextMenu.getItems().add(removeMenuItem);
        attentionListView.setContextMenu(contextMenu);
        /**
         * initialize values
         */
        if (targetEmailTag != null){
            List<PeonyEmailAtt> aPeonyEmailAttList = targetEmailTag.getPeonyEmailAttList();
            if (aPeonyEmailAttList != null){
                List<PeonyEmployee> peonyEmployeeSelectedList = new ArrayList<>();
                for (PeonyEmailAtt aPeonyEmailAtt : aPeonyEmailAttList){
                    peonyEmployeeSelectedList.add(aPeonyEmailAtt.getPeonyEmployee());
                }
                addSelectedEmployees(peonyEmployeeSelectedList);
            }
        }
    }

    private void initializeSelectedTaxCaseListView(){
        final ContextMenu contextMenu = new ContextMenu();
        MenuItem viewMenuItem = new MenuItem("View");
        viewMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                viewSelectedTaxcaseProfile();
            }
            private void viewSelectedTaxcaseProfile() {
                Object taxcaseObj = taxcaseStorage.get(selectedTaxCaseListView.getSelectionModel().getSelectedItem());
                if (taxcaseObj instanceof PeonyTaxpayerCase){
                    Lookup.getDefault().lookup(PeonyTaxpayerService.class).launchPeonyTaxpayerCaseDialog((PeonyTaxpayerCase)taxcaseObj);
                }else if (taxcaseObj instanceof PeonyTaxcorpCase){
                    Lookup.getDefault().lookup(PeonyTaxcorpService.class).launchPeonyTaxcorpCaseDialog((PeonyTaxcorpCase)taxcaseObj);
                }else{
                    PeonyFaceUtils.displayErrorMessageDialog("Please select one taxcase from the list.");
                    return;
                }
            }
        });
        contextMenu.getItems().add(viewMenuItem);
        MenuItem addMenuItem = new MenuItem("Add");
        addMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                displayPeonyTaxCasePickerDialog();
            }
        });
        contextMenu.getItems().add(addMenuItem);
        MenuItem removeMenuItem = new MenuItem("Remove");
        removeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                removeSelectedTaxCase(taxcaseStorage.get(selectedTaxCaseListView.getSelectionModel().getSelectedItem()));
            }
        });
        contextMenu.getItems().add(removeMenuItem);
        selectedTaxCaseListView.setContextMenu(contextMenu);
        /**
         * initialize values
         */
        if (targetEmailTag != null){
            List<PeonyEmailTaxcase> aPeonyEmailTaxcaseList = targetEmailTag.getPeonyEmailTaxcaseList();
            if (aPeonyEmailTaxcaseList != null){
                for (PeonyEmailTaxcase aPeonyEmailTaxcase : aPeonyEmailTaxcaseList){
                    addSelectedTaxcase(aPeonyEmailTaxcase.getPeonyTaxcorpCase());
                    addSelectedTaxcase(aPeonyEmailTaxcase.getPeonyTaxpayerCase());
                }
            }
        }
    
    }

    private void displayPeonyEmployeePickerDialog() {
        List<PeonyFaceEventListener> peonyFaceEventListeners = new ArrayList<>();
        peonyFaceEventListeners.add(this);
        Lookup.getDefault().lookup(PeonyKernelService.class).displayPeonyEmployeePickerDialog("Select Employees for Attention: ", peonyFaceEventListeners);
    }

    private void displayPeonyTaxCasePickerDialog() {
        List<PeonyFaceEventListener> peonyFaceEventListeners = new ArrayList<>();
        peonyFaceEventListeners.add(this);
        Lookup.getDefault().lookup(PeonyKernelService.class).displayPeonyTaxCasePickerDialog("Select Tax Cases: ", peonyFaceEventListeners);
    }

    private String constructTaxcaseTextLine(Object taxcaseObj) {
        if (taxcaseObj instanceof PeonyTaxpayerCase){
            PeonyTaxpayerCase aPeonyTaxpayerCase = (PeonyTaxpayerCase)taxcaseObj;
            if (aPeonyTaxpayerCase.getTaxpayerCase().getExtension() == null){
                return "Taxpayer Case ["+ZcaCalendar.convertToMMddyyyy(aPeonyTaxpayerCase.getTaxpayerCase().getDeadline(), "-")+"]: " 
                        + aPeonyTaxpayerCase.getTaxpayerInfoSsnLine();
            }else{
                return "Taxpayer Case ["+ZcaCalendar.convertToMMddyyyy(aPeonyTaxpayerCase.getTaxpayerCase().getDeadline(), "-")
                        +"/"+ZcaCalendar.convertToMMddyyyy(aPeonyTaxpayerCase.getTaxpayerCase().getExtension(), "-")+"]: " 
                        + aPeonyTaxpayerCase.getTaxpayerInfoSsnLine();
            }
        }else if (taxcaseObj instanceof PeonyTaxcorpCase){
            PeonyTaxcorpCase aPeonyTaxcorpCase = (PeonyTaxcorpCase)taxcaseObj;
            G02TaxcorpCase aG02TaxcorpCase = aPeonyTaxcorpCase.getTaxcorpCase();
            if (aG02TaxcorpCase == null){
                return null;
            }else{
                return aG02TaxcorpCase.getCorporateName() + " ["+aG02TaxcorpCase.getEinNumber()+"]";
            }
        }else{
            return null;
        }
    }

    private void executeAssignment() {
        Task<PeonyEmailTagList> assignEmailsTask = new Task<PeonyEmailTagList>(){
            @Override
            protected PeonyEmailTagList call() throws Exception {
                /**
                 * selected employees....
                 */
                List<PeonyEmployee> selectedEmployees = new ArrayList<>(employeeStorage.values());
                if (selectedEmployees.isEmpty()){
                    selectedEmployees.add(PeonyProperties.getSingleton().getCurrentLoginEmployee());
                }
                /**
                 * selected taxcases...
                 */
                List<Object> selectedTaxcases = new ArrayList<>(taxcaseStorage.values());
                /**
                 * notes of this assignments
                 */
                String notes = notesTextArea.getText();
                if (ZcaValidator.isNullEmpty(notes) || (notes.length() > 450)){
                    updateMessage("Please give some comments on this assignment with max 450 characters.");
                    return null;
                }
                /**
                 * PeonyEmailTagList for selected email items
                 */
                PeonyEmailTagList aPeonyEmailTagList = new PeonyEmailTagList();
                PeonyEmailTag aPeonyEmailTag;
                for (TreeItem<PeonyEmailTreeItemData> aSelectedEmailTreeItem : selectedEmailTreeItems){
                    //post it onto the public message board and hook tags onto the email message
                    aPeonyEmailTag = createEmailAssignedTag(aSelectedEmailTreeItem, selectedEmployees, selectedTaxcases, notes);
                    if (aPeonyEmailTag != null){
                        aPeonyEmailTagList.getPeonyEmailTagList().add(aPeonyEmailTag);
                    }
                }
                if (aPeonyEmailTagList.getPeonyEmailTagList().isEmpty()){
                    updateMessage("No selected items valid for this operation.");
                    return null;
                }
                try {
                    Lookup.getDefault().lookup(PeonyManagementService.class).getPeonyManagementRestClient().storeEntity_XML(
                            PeonyEmailTagList.class, GardenRestParams.Management.storePeonyEmailTagListRestParams(), aPeonyEmailTagList);
                } catch (Exception ex) {
                    //Exceptions.printStackTrace(ex);
                    updateMessage("Cannot assign it because of technical issues on the server. " + ex.getMessage());
                }
                return aPeonyEmailTagList;
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyEmailTagList aPeonyEmailTagList = get();
                    if ((aPeonyEmailTagList == null) || (aPeonyEmailTagList.getPeonyEmailTagList().isEmpty())){
                        PeonyFaceUtils.displayErrorMessageDialog("Failed. " + getMessage());
                    }else{
                        PeonyFaceUtils.displayInformationMessageDialog("Successfully assign email(s) to the selected targets.");
                        broadcastPeonyFaceEventHappened(new PeonyEmailTagListCreated(aPeonyEmailTagList, selectedEmailTreeItems));
                        broadcastPeonyFaceEventHappened(new CloseDialogRequest());
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("This operation failed. " + ex.getMessage());
                }
            }

            private PeonyEmailTag createEmailAssignedTag(TreeItem<PeonyEmailTreeItemData> aSelectedItem, 
                                                         List<PeonyEmployee> selectedEmployees, 
                                                         List<Object> selectedTaxcases, String notes) 
            {
                GardenEmailMessage aGardenEmailMessage = aSelectedItem.getValue().emitPeonyEmailMessage();
                if (aGardenEmailMessage == null){
                    return null;
                }
                PeonyOfflineEmail aPeonyOfflineEmail = aSelectedItem.getValue().getAssociatedPeonyOfflineEmail();
                if (aPeonyOfflineEmail == null){
                    return null;
                }
                G02OfflineEmail aG02OfflineEmail = aPeonyOfflineEmail.getOfflineEmail();
                PeonyEmailTag aPeonyEmailTag = new PeonyEmailTag();
                
                G02EmailTag aG02EmailTag = aPeonyEmailTag.getEmailTag();
                aG02EmailTag.setOfflineEmailUuid(aG02OfflineEmail.getOfflineEmailUuid());
                aG02EmailTag.setEntityType(GardenEntityType.OFFLINE_EMAIL_MESSAGE.name());
                aG02EmailTag.setEntityUuid(aG02OfflineEmail.getOfflineEmailUuid());
                aG02EmailTag.setEntityComboUuid(aG02OfflineEmail.getMailboxAddress()+aG02OfflineEmail.getMsgId());
                aG02EmailTag.setTagName(GardenEmailMessageTag.ASSIGNED_EMAIL.name());
                aG02EmailTag.setTagMemo(notes);
                aG02EmailTag.setTagUuid(GardenData.generateUUIDString());
                String memoText = "Critical Email - ";
                /**
                 * Attentions
                 */
                if ((selectedEmployees != null) && (!selectedEmployees.isEmpty())){
                    memoText += "Attention: ";
                    G02EmailAtt aG02EmailAtt;
                    G02EmailAttPK pkid;
                    List<PeonyEmailAtt> aPeonyEmailAttList = aPeonyEmailTag.getPeonyEmailAttList();
                    PeonyEmailAtt aPeonyEmailAtt;
                    for (PeonyEmployee aPeonyEmployee : selectedEmployees){
                        aG02EmailAtt = new G02EmailAtt();
                        pkid = new G02EmailAttPK();
                        pkid.setAttUserId(aPeonyEmployee.getAccount().getAccountUuid());
                        pkid.setTagUuid(aG02EmailTag.getTagUuid());
                        aG02EmailAtt.setG02EmailAttPK(pkid);
                        
                        aPeonyEmailAtt = new PeonyEmailAtt();
                        aPeonyEmailAtt.setG02EmailAtt(aG02EmailAtt);
                        aPeonyEmailAtt.setPeonyEmployee(aPeonyEmployee);
                        aPeonyEmailAttList.add(aPeonyEmailAtt);
                        
                        memoText += PeonyDataUtils.createFullName(aPeonyEmployee.getFirstName(), aPeonyEmployee.getLastName()) + "; ";
                    }//for
                }
                /**
                 * Taxcases
                 */
                if ((selectedTaxcases != null) && (!selectedTaxcases.isEmpty())){
                    G02EmailTaxcase aG02EmailTaxcase;
                    G02EmailTaxcasePK pkid;
                    List<PeonyEmailTaxcase> aPeonyEmailTaxcaseList = aPeonyEmailTag.getPeonyEmailTaxcaseList();
                    PeonyEmailTaxcase aPeonyEmailTaxcase;
                    String entityUuid;
                    String entityType;
                    memoText += "Related Tax Cases: ";
                    for (Object aSelectedTaxcase : selectedTaxcases){
                        aPeonyEmailTaxcase = new PeonyEmailTaxcase();
                        if (aSelectedTaxcase instanceof PeonyTaxpayerCase){
                            entityUuid = ((PeonyTaxpayerCase)aSelectedTaxcase).getTaxpayerCase().getTaxpayerCaseUuid();
                            entityType = GardenEntityType.TAXPAYER_CASE.name();
                            aPeonyEmailTaxcase.setPeonyTaxpayerCase(((PeonyTaxpayerCase)aSelectedTaxcase));
                            
                            memoText += ((PeonyTaxpayerCase)aSelectedTaxcase).getTaxpayerCaseTitle(false) + "; ";
                        }else if (aSelectedTaxcase instanceof PeonyTaxcorpCase){
                            entityUuid = ((PeonyTaxcorpCase)aSelectedTaxcase).getTaxcorpCase().getTaxcorpCaseUuid();
                            entityType = GardenEntityType.TAXCORP_CASE.name();
                            aPeonyEmailTaxcase.setPeonyTaxcorpCase(((PeonyTaxcorpCase)aSelectedTaxcase));
                            
                            memoText += ((PeonyTaxcorpCase)aSelectedTaxcase).getTaxcorpCaseTitle()+ "; ";
                        }else{
                            entityUuid = null;
                            entityType = null;
                        }
                        if ((entityUuid != null) && (entityType != null)){
                            aG02EmailTaxcase = new G02EmailTaxcase();
                            pkid = new G02EmailTaxcasePK();

                            pkid.setTaxcaseEntityUuid(entityUuid);
                            pkid.setTagUuid(aG02EmailTag.getTagUuid());
                            aG02EmailTaxcase.setG02EmailTaxcasePK(pkid);
                            aG02EmailTaxcase.setTaxcaseEntityType(entityType);
                            aPeonyEmailTaxcase.setG02EmailTaxcase(aG02EmailTaxcase);
                            aPeonyEmailTaxcaseList.add(aPeonyEmailTaxcase);
                        }
                    }//for
                }
                if (ZcaValidator.isNotNullEmpty(notes)){
                    memoText += "Notes: " + notes;
                }
                /**
                 * publish this email tag onto the public message board
                 */
                aPeonyEmailTag.getMemoList().add(createG02Memo(aG02EmailTag.getTagUuid(), memoText, null));
                return aPeonyEmailTag;
            }

            private G02Memo createG02Memo(String emailTagUuid, String memoText, String initialMemoUuid) {
                G02Memo aG02Memo = new G02Memo();
                aG02Memo.setEntityStatus(GardenEntityType.PUBLIC_BOARD.value());
                aG02Memo.setEntityType(GardenEntityType.EMAIL_TAG.name());
                aG02Memo.setEntityUuid(emailTagUuid);
                aG02Memo.setInitialMemoUuid(initialMemoUuid);
                if (memoText.length() > 450){
                    memoText = memoText.substring(0, 440) + "......";
                }
                aG02Memo.setMemo(memoText);
                aG02Memo.setMemoUuid(GardenData.generateUUIDString());
                aG02Memo.setOperatorAccountUuid(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
                aG02Memo.setTimestamp(new Date());
                return aG02Memo;
            }
        };
        assignEmailsTask.run();
    }
}
