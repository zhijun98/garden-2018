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
import com.zcomapproach.garden.email.data.GardenEmailFeature;
import com.zcomapproach.garden.email.data.PeonySpamRules;
import com.zcomapproach.garden.peony.PeonyLauncher;
import com.zcomapproach.garden.peony.controls.PeonyButtonTableCell;
import com.zcomapproach.garden.peony.view.events.PeonySpamRuleAdded;
import com.zcomapproach.garden.peony.view.events.PeonySpamRuleRemoved;
import com.zcomapproach.garden.peony.settings.PeonySpamRuleManager;
import com.zcomapproach.garden.peony.settings.PeonySpamRuleManager.PeonySpamRule;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.util.GardenFaceX;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;

/**
 *
 * @author zhijun98
 */
public class SpamRulePaneController extends PeonyFaceController{
    @FXML
    private ComboBox emailFeatureComboBox;
    @FXML
    private TextField emailFeatureValueField;
    @FXML
    private Button addSpamRuleButton;
    @FXML
    private TableView<PeonySpamRule> spamRuleTableView;
    
    private final String loginEmployeeUuid;

    public SpamRulePaneController(String loginEmployeeUuid) {
        this.loginEmployeeUuid = loginEmployeeUuid;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //emailFeatureComboBox
        PeonyFaceUtils.initializeComboBox(emailFeatureComboBox, 
                GardenEmailFeature.getEnumValueList(false), null, GardenEmailFeature.SUBJECT.value(), 
                "Select email feature to set up a spam rule", this);
        //addSpamRuleButton
        addSpamRuleButton.setOnAction((ActionEvent event) -> {
            String feature = emailFeatureComboBox.getSelectionModel().getSelectedItem().toString();
            if (ZcaValidator.isNullEmpty(feature)){
                PeonyFaceUtils.displayErrorMessageDialog("Please select email feature so as to set up spam rule!");
                return;
            }
            String spamValue = emailFeatureValueField.getText();
            if (ZcaValidator.isNullEmpty(spamValue)){
                PeonyFaceUtils.displayErrorMessageDialog("Please type in spam-text which is used to detect spam emails!");
                return;
            }
            if (spamValue.contains(PeonySpamRules.DELIMITER)){
                PeonyFaceUtils.displayErrorMessageDialog("Spam-text, which is used to detect spam emails, cannot have character(s) '" + PeonySpamRules.DELIMITER + "'");
                return;
            }
            
            PeonySpamRule aPeonySpamRule = new PeonySpamRule(feature, spamValue);
            PeonySpamRuleManager.getSingleton(loginEmployeeUuid).addPeonySpamRule(aPeonySpamRule);
            spamRuleTableView.getItems().add(aPeonySpamRule);
            emailFeatureValueField.setText("");
            
            broadcastPeonyFaceEventHappened(new PeonySpamRuleAdded(aPeonySpamRule));
        });
        //spamRuleTableView
        spamRuleTableView.getColumns().clear();
        TableColumn<PeonySpamRule, String> featureColumn = new TableColumn("Email Feature");
        featureColumn.setCellValueFactory(new PropertyValueFactory("emailFeature"));
        spamRuleTableView.getColumns().add(featureColumn);
        
        TableColumn<PeonySpamRule, String> spamTextColumn = new TableColumn("Spam Text (If the email feature contains the spame text, the email is treated as spam.)");
        spamTextColumn.setCellValueFactory(new PropertyValueFactory("spamText"));
        spamRuleTableView.getColumns().add(spamTextColumn);
        
        TableColumn deleteColumn = new TableColumn("");
        deleteColumn.setCellFactory(PeonyButtonTableCell.<PeonySpamRule>callbackForTableColumn("Delete", (PeonySpamRule aPeonySpamRule) -> {
            if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure display this spam rule?") == JOptionPane.YES_OPTION){
                PeonySpamRuleManager.getSingleton(loginEmployeeUuid).removePeonySpamRule(aPeonySpamRule);
                spamRuleTableView.getItems().remove(aPeonySpamRule);
            
                broadcastPeonyFaceEventHappened(new PeonySpamRuleRemoved(aPeonySpamRule));
            }
            return aPeonySpamRule;
        }));
        spamRuleTableView.getColumns().add(deleteColumn);
        
        spamRuleTableView.setItems(FXCollections.observableArrayList(PeonySpamRuleManager.getSingleton(loginEmployeeUuid).getPeonySpamRuleList()));
        
        spamRuleTableView.getSelectionModel().setCellSelectionEnabled(true);
        spamRuleTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        GardenFaceX.installCopyPasteHandler(spamRuleTableView);
        
    }

}
