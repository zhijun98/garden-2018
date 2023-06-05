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

import com.jfoenix.controls.JFXButton;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.commons.exceptions.validation.ZcaParamException;
import com.zcomapproach.garden.peony.view.actions.PeonyMessagingAgent;
import com.zcomapproach.garden.peony.view.controllers.control.PeonyMessageBox;
import com.zcomapproach.garden.peony.view.data.MessagingDirection;
import com.zcomapproach.garden.peony.view.events.CloseDialogRequest;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.jivesoftware.smack.SmackException;

/**
 *
 * @author zhijun98
 */
public class PeonyMessagingBoardController extends PeonyWelcomeServiceController {
    
    @FXML
    private Label messagingWarningLabel;
    @FXML
    private ScrollPane conversationBoardScrollPane;
    @FXML
    private VBox messageBoardVBox;
    @FXML
    private TextField messageInputTextField;
    @FXML
    private JFXButton sendButton;
    @FXML
    private JFXButton resetButton;
    @FXML
    private JFXButton closeButton;

    private final Tab tabOwner;
    private final String messagingWarning;
    private final ObservableList<Node> messageBubbles;
    
    private final PeonyMessagingAgent messagingAgent;

    public PeonyMessagingBoardController(Tab tabOwner, String messagingWarning, PeonyMessagingAgent messagingAgent) {
        this.tabOwner = tabOwner;
        this.messagingWarning = messagingWarning;
        this.messageBubbles = FXCollections.observableArrayList();
        this.messagingAgent = messagingAgent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        messagingWarningLabel.setText(getMessagingWarning());
        
        messageBoardVBox.getChildren().addListener((ListChangeListener<Node>) change -> {
            getSingleExecutorService().submit(new Task<Object>(){
                @Override
                protected Object call() throws Exception {
                    Thread.sleep(100);
                    return null;
                }

                @Override
                protected void succeeded() {
                    PeonyMessageBox aPeonyMessageBox = (PeonyMessageBox)(messageBoardVBox.getChildren().get(messageBoardVBox.getChildren().size()-1));
                    aPeonyMessageBox.setPrefHeight(aPeonyMessageBox.getDisplayedText().getBoundsInLocal().getHeight());                    
                    conversationBoardScrollPane.setVvalue(1);
                }
            });
        });
        
        messageInputTextField.setOnKeyPressed((KeyEvent event) -> {
            if(event.getCode() == KeyCode.ENTER) {
                if (ZcaValidator.isNotNullEmpty(messageInputTextField.getText())){
                    sendMessage(messageInputTextField.getText()); //Exceptions.printStackTrace(ex);
                    messageInputTextField.setText("");
                }
            }
        });
        
        sendButton.setOnAction(event-> {
            if (ZcaValidator.isNotNullEmpty(messageInputTextField.getText())){
                sendMessage(messageInputTextField.getText()); //Exceptions.printStackTrace(ex);
                messageInputTextField.setText("");
            }
        });
        
        resetButton.setOnAction(event-> {
            messageInputTextField.setText("");
        });
        
        closeButton.setOnAction(event-> {
            broadcastPeonyFaceEventHappened(new CloseDialogRequest());
        });
    }

    public Tab getTabOwner() {
        return tabOwner;
    }

    protected String getMessagingWarning() {
        return messagingWarning;
    }

    protected ObservableList<Node> getMessageBubbles() {
        return messageBubbles;
    }

    public void receiveMessage(final String message){
        if (Platform.isFxApplicationThread()){
            receiveMessageHelper(message);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    receiveMessageHelper(message);
                }
            });
        }
    }
    protected void receiveMessageHelper(final String message){
        messageBoardVBox.getChildren().add(new PeonyMessageBox(message, MessagingDirection.LEFT));
    }

    public void sendMessage(final String message){
        if (Platform.isFxApplicationThread()){
            sendMessageHeper(message);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    sendMessageHeper(message);
                }
            });
        }
    }
    
    private void sendMessageHeper(final String message){
        try {
            messagingAgent.sendMessage(message);
            messageBoardVBox.getChildren().add(new PeonyMessageBox(message, MessagingDirection.RIGHT));
        } catch (ZcaParamException | SmackException.NotConnectedException ex) {
            messageBoardVBox.getChildren().add(new PeonyMessageBox("[Failed messaging: lost connection] " + message, MessagingDirection.RIGHT));
            //Exceptions.printStackTrace(ex);
        }
    }
    
}
