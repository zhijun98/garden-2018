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

import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.events.CalendarPeriodSelected;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import com.zcomapproach.commons.ZcaCalendar;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author zhijun98
 */
public class PeriodSelectionController extends PeonyFaceController{
    @FXML
    private VBox periodSelectionVBox;
    @FXML
    private GridPane topGridPane;
    @FXML
    private Label selectPeriodBoldedLabel;
    @FXML
    private Label descriptiveLabel;
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private Button selectButton;
    @FXML
    private Button closeButton;

    private final String title;
    private final Date fromDate;
    private final Date toDate;

    public PeriodSelectionController(String title, Date fromDate, Date toDate) {
        this.title = title;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fromDatePicker.setValue(ZcaCalendar.convertToLocalDate(fromDate));
        toDatePicker.setValue(ZcaCalendar.convertToLocalDate(toDate));
        
        descriptiveLabel.setText(title);
        
        selectButton.setOnAction((ActionEvent event) -> {
            broadcastPeonyFaceEventHappened(new CalendarPeriodSelected(
                    ZcaCalendar.covertDateToBeginning(ZcaCalendar.convertToDate(fromDatePicker.getValue())), 
                    ZcaCalendar.covertDateToEnding(ZcaCalendar.convertToDate(toDatePicker.getValue()))));
            broadcastPeonyFaceEventHappened(new CloseDialogRequest());
        });
        
        closeButton.setOnAction((ActionEvent event) -> {
            broadcastPeonyFaceEventHappened(new CloseDialogRequest());
        });
    }

}
