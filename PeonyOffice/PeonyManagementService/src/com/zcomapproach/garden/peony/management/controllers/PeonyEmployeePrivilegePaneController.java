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

package com.zcomapproach.garden.peony.management.controllers;

import com.zcomapproach.garden.data.GardenFlower;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.security.PeonyPrivilege;
import com.zcomapproach.garden.peony.security.PeonyPrivilegeGroup;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.persistence.G02EntityValidator;
import com.zcomapproach.garden.persistence.entity.G02Privilege;
import com.zcomapproach.garden.persistence.entity.G02PrivilegePK;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.rest.GardenRestParams;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class PeonyEmployeePrivilegePaneController extends PeonyManagementServiceController{
    @FXML
    private Label titleLabel;
    @FXML
    private TabPane employeePrivilegesTabPane;
    @FXML
    private Button savePrivilegesButton;
    @FXML
    private Button closeButton;
    
    private final PeonyEmployee targetPeonyEmployee;
    
    private final HashMap<PeonyPrivilege, CheckBox> privilegeCheckBoxStorage = new HashMap<>();

    public PeonyEmployeePrivilegePaneController(PeonyEmployee targetPeonyEmployee) {
        this.targetPeonyEmployee = targetPeonyEmployee;
    }
    
    public void setTitleLabel(String title){
        //titleLabel.setText("Person Data Entry");
        if (Platform.isFxApplicationThread()){
            titleLabel.setText(title);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    titleLabel.setText(title);
                }
            });
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        savePrivilegesButton.setOnAction((ActionEvent event) -> {
            savePeonyEmployeePrivileges();
        });
        closeButton.setOnAction((ActionEvent event) -> {
            broadcastPeonyFaceEventHappened(new CloseDialogRequest(true));
        });
        
        addPeonyPrivilegeNodes();
    }

    private void addPeonyPrivilegeNodes() {
        privilegeCheckBoxStorage.clear();
        
        HashMap<PeonyPrivilegeGroup, VBox> tabVBoxStorage = new HashMap<>();
        HashMap<PeonyPrivilegeGroup, List<PeonyPrivilege>> privilegeStorage = PeonyPrivilege.getGroupedPrivileges();
        Set<PeonyPrivilegeGroup> groups = privilegeStorage.keySet();
        Iterator<PeonyPrivilegeGroup> itr = groups.iterator();
        PeonyPrivilegeGroup group;
        Tab groupTab;
        VBox groupVbox;
        List<PeonyPrivilege> aPeonyPrivilegeList;
        employeePrivilegesTabPane.getTabs().clear();
        while (itr.hasNext()){
            group = itr.next();
            if (isAccessibleGroup(group)){
                aPeonyPrivilegeList = privilegeStorage.get(group);
                if ((aPeonyPrivilegeList != null) && (!aPeonyPrivilegeList.isEmpty())){
                    groupVbox = tabVBoxStorage.get(group);
                    if (groupVbox == null){
                        groupVbox = new VBox();
                        groupVbox.setPadding(new Insets(10, 10, 10, 10));
                        groupVbox.setSpacing(5);
                        groupTab = new Tab(group.value());
                        groupTab.setContent(groupVbox);
                        employeePrivilegesTabPane.getTabs().add(groupTab);
                        tabVBoxStorage.put(group, groupVbox);
                    }
                    for (PeonyPrivilege aPeonyPrivilege : aPeonyPrivilegeList){
                        groupVbox.getChildren().add(constructEmployeePrivilegeVBox(aPeonyPrivilege));
                    }
                }
            }
        }//while-loop
    }
    
    private VBox constructEmployeePrivilegeVBox(PeonyPrivilege aPeonyPrivilege){
        VBox vBox = new VBox();
        CheckBox aCheckBox = new CheckBox();
            
        aCheckBox.setId(aPeonyPrivilege.name());
        aCheckBox.setText(aPeonyPrivilege.value());
        aCheckBox.setStyle("-fx-font-weight: bolder");
        aCheckBox.setSelected(targetPeonyEmployee.isPrivilegeAuthorized(GardenFlower.PEONY.name(), aPeonyPrivilege.name()));
        VBox.setMargin(aCheckBox, new Insets(5, 0, 1, 0));
        vBox.getChildren().add(aCheckBox);
        //Description
        Label aLabel = new Label(PeonyPrivilege.getParamDescription(aPeonyPrivilege));
        VBox.setMargin(aLabel, new Insets(2, 0, 5, 20));
        vBox.getChildren().add(aLabel);
        
        privilegeCheckBoxStorage.put(aPeonyPrivilege, aCheckBox);
    
        return vBox;
    }

    private void loadEmployeePrivileges() {
        List<G02Privilege> privilegeList = new ArrayList<>();
        List<CheckBox> aCheckBoxList = new ArrayList<>(privilegeCheckBoxStorage.values());
        G02Privilege aG02Privilege; G02PrivilegePK pkid;
        for (CheckBox aCheckBox : aCheckBoxList){
            if (aCheckBox.isSelected()){
                aG02Privilege = new G02Privilege();
                pkid = new G02PrivilegePK();
                pkid.setFlowerName(GardenFlower.PEONY.name());
                pkid.setPrivilegeName(aCheckBox.getId());
                aG02Privilege.setG02PrivilegePK(pkid);
                aG02Privilege.setDescription(PeonyPrivilege.getParamDescription(PeonyPrivilege.convertEnumNameToType(aCheckBox.getId())));
                privilegeList.add(aG02Privilege);
            }
        }
        targetPeonyEmployee.setPrivilegeList(privilegeList);
    }

    private void savePeonyEmployeePrivileges() {
        Task<PeonyEmployee> savePeonyEmployeePrivilegesTask = new Task<PeonyEmployee>(){
            @Override
            protected PeonyEmployee call() throws Exception {
                loadEmployeePrivileges();
                PeonyEmployee aPeonyEmployee = null;
                try{
                    G02EntityValidator.getSingleton().validate(targetPeonyEmployee);
                    aPeonyEmployee = Lookup.getDefault().lookup(PeonyManagementService.class).getPeonyManagementRestClient()
                            .storeEntity_XML(PeonyEmployee.class, GardenRestParams.Management.storePeonyEmployeePrivilegesRestParams(), targetPeonyEmployee);
                }catch (Exception ex){
                    updateMessage(ex.getMessage());
                }
                return aPeonyEmployee;
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyEmployee aPeonyEmployee = get();
                    if (aPeonyEmployee == null){
                        PeonyFaceUtils.displayErrorMessageDialog(getMessage());
                    }else{
                        PeonyFaceUtils.displayInformationMessageDialog("Privileges of the employee "+targetPeonyEmployee.getPeonyUserFullName()+" are saved.");
                        broadcastPeonyFaceEventHappened(new CloseDialogRequest(true));
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.displayErrorMessageDialog("This operation failed. " + ex.getMessage());
                }
            }
            
        };
        this.getCachedThreadPoolExecutorService().submit(savePeonyEmployeePrivilegesTask);
    }

    private boolean isAccessibleGroup(PeonyPrivilegeGroup group) {
        if (PeonyPrivilegeGroup.SUPER_POWER.equals(group)){
            //only super users may access to this group
            return PeonyProperties.getSingleton().isSuperUser();
        }
        return true;
    }
}
