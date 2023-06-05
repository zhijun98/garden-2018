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

import com.zcomapproach.garden.peony.view.events.PeonyEmployeeSelected;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.persistence.peony.PeonyEmployeeList;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import org.openide.util.Lookup;

/**
 * Display all the employees in a list for users to pick. Users may pick multiple.
 * @author zhijun98
 */
public class PeonyEmployeePickerController extends PeonyFaceController{

    @FXML
    private ListView<String> currentEmployeeListView;
    
    @FXML
    private Button selectButton;
    
    @FXML
    private Button closeButton;
    
    private final HashMap<String, PeonyEmployee> employeeStorage = new HashMap<>();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeCurrentEmployeeListView();
        
        selectButton.setOnAction((ActionEvent event) -> {
            List<PeonyEmployee> aPeonyEmployeeList = new ArrayList<>();
            List<String> selectedEmployeeTextLines = currentEmployeeListView.getSelectionModel().getSelectedItems();
            if ((selectedEmployeeTextLines == null) || (selectedEmployeeTextLines.isEmpty())){
                PeonyFaceUtils.displayErrorMessageDialog("Please select one or multiple employees from the list.");
            }else{
                for (String selectedEmployeeTextLine : selectedEmployeeTextLines){
                    aPeonyEmployeeList.add(employeeStorage.get(selectedEmployeeTextLine));
                }
                broadcastPeonyFaceEventHappened(new PeonyEmployeeSelected(aPeonyEmployeeList));
                broadcastPeonyFaceEventHappened(new CloseDialogRequest(true));
            }
        });
        
        closeButton.setOnAction((ActionEvent event) -> {
            broadcastPeonyFaceEventHappened(new CloseDialogRequest(true));
        });
    }

    private void initializeCurrentEmployeeListView() {
        currentEmployeeListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);        
        PeonyEmployeeList aPeonyEmployeeList = Lookup.getDefault().lookup(PeonyManagementService.class).retrievePeonyEmployeeList();
        List<PeonyEmployee> employees = aPeonyEmployeeList.getPeonyEmployeeList();
        String key;
        List<String> employeeTextLineItems = currentEmployeeListView.getItems();
        for (PeonyEmployee employee : employees){
            key = employee.getTextLine();
            employeeStorage.put(key, employee);
            employeeTextLineItems.add(key);
        }
    }

}
