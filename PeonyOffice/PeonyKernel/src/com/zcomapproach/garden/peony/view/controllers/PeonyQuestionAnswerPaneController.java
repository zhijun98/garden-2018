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
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.PeonyQuestionAnswered;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author zhijun98
 */
public class PeonyQuestionAnswerPaneController extends PeonyFaceController{
    @FXML
    private Label questionTitleLabel;
    @FXML
    private Label questionContentLabel;
    @FXML
    private Label questionNoteLabel;
    @FXML
    private TextField answerTextField;
    @FXML
    private Button answerButton;
    @FXML
    private Button closeButton;

    private final String questionTitle;
    private final String questionContent;
    private final String questionNote;

    public PeonyQuestionAnswerPaneController(String questionTitle, String questionContent, String questionNote) {
        this.questionTitle = questionTitle;
        this.questionContent = questionContent;
        this.questionNote = questionNote;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        questionTitleLabel.setText(questionTitle);
        questionContentLabel.setText(questionContent);
        if (ZcaValidator.isNullEmpty(questionNote)){
            questionNoteLabel.setVisible(false);
        }else{
            questionNoteLabel.setVisible(true);
            questionNoteLabel.setText(questionNote);
        }
        
        answerTextField.setOnKeyPressed((KeyEvent event) -> {
            if(event.getCode() == KeyCode.ENTER) {
                answer();
            }
        });
        
        answerButton.setOnAction((ActionEvent event) -> {
            answer();
        });
        
        closeButton.setOnAction((ActionEvent event) -> {
            broadcastPeonyFaceEventHappened(new CloseDialogRequest());
        });
    }
    
    private void answer(){
        if (ZcaValidator.isNullEmpty(answerTextField.getText())){
            PeonyFaceUtils.displayErrorMessageDialog("Please give your input.");
        }else{
            broadcastPeonyFaceEventHappened(new PeonyQuestionAnswered(answerTextField.getText()));
            broadcastPeonyFaceEventHappened(new CloseDialogRequest());
        }
    }

}
