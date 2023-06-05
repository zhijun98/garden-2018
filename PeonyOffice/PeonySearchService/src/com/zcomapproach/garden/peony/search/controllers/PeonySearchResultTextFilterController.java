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

package com.zcomapproach.garden.peony.search.controllers;

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.garden.peony.view.events.SearchResultColumnTextCriteraCreated;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 *
 * @author zhijun98
 */
public class PeonySearchResultTextFilterController extends PeonySearchServiceController{

    @FXML
    private Label filterTitleLabel;
    @FXML
    private ComboBox<String> filterColumnComboBox;
    @FXML
    private TextField filterValueField;
    @FXML
    private CheckBox exactMatchCheckBox;
    @FXML
    private Button filterButton;
    @FXML
    private Button closeButton;
    
    private final String filterTitle;
    private final List<String> filterColumnValueList;

    public PeonySearchResultTextFilterController(String filterTitle, List<String> filterColumnValueList) {
        this.filterTitle = (ZcaValidator.isNullEmpty(filterTitle)? "Filter for Result" : filterTitle);
        this.filterColumnValueList = filterColumnValueList;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filterTitleLabel.setText(filterTitle);
        
        PeonyFaceUtils.initializeComboBox(filterColumnComboBox, filterColumnValueList, 
                null, null, "Select which column for filter.", false, null);
        PeonyFaceUtils.initializeTextField(filterValueField, null, null, "Keywords used for filiter", false, null);
        PeonyFaceUtils.initializeCheckBox(exactMatchCheckBox, false, "Whether or not exactly match filter's keywords with the column value", false, null);
        
        filterButton.setOnAction((ActionEvent event) -> {
            broadcastPeonyFaceEventHappened(new SearchResultColumnTextCriteraCreated(filterColumnComboBox.getValue(), 
                                                                                     filterValueField.getText(), 
                                                                                     exactMatchCheckBox.isSelected()));
        });
        
        closeButton.setOnAction((ActionEvent event) -> {
            broadcastPeonyFaceEventHappened(new CloseDialogRequest());
        });
    }
    
}
