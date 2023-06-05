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

package com.zcomapproach.garden.peony.customer.controllers;

import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaUtils;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.peony.kernel.services.PeonyCustomerService;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.persistence.entity.G02TulipNotification;
import com.zcomapproach.garden.persistence.entity.G02TulipUser;
import com.zcomapproach.garden.persistence.peony.PeonyTulipUser;
import com.zcomapproach.garden.rest.GardenRestParams;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.openide.util.Lookup;

/**
 * Display details about Tulip customer, e.g. messages and notifications
 * @author zhijun98
 */
public class TulipCustomerProfileController extends PeonyCustomerServiceController{
    
    @FXML
    private Label pageTitleLabel;
    @FXML
    private Tab notificationsTab;
    @FXML
    private TextArea notificatonTextArea;
    @FXML
    private Button sendNotificationButton;
    @FXML
    private ScrollPane notificationsScrollPane;
    @FXML
    private VBox notificationsBox;
    @FXML
    private Tab messagesTab;
    
    private final G02TulipUser targetTulipUser;

    public TulipCustomerProfileController(G02TulipUser targetTulipUser) {
        this.targetTulipUser = targetTulipUser;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        pageTitleLabel.setText(pageTitleLabel.getText() + " " + targetTulipUser.getUsername());

        notificationsBox.prefWidthProperty().bind(notificationsScrollPane.widthProperty().subtract(8));
        
        this.getCachedThreadPoolExecutorService().submit(new Task<PeonyTulipUser>(){
            @Override
            protected PeonyTulipUser call() throws Exception {
                //PeonyTulipUser
                return Lookup.getDefault().lookup(PeonyCustomerService.class).getPeonyCustomerRestClient()
                    .findEntity_XML(PeonyTulipUser.class, GardenRestParams.Customer.retrievePeonyTulipUserByUuidRestParams(targetTulipUser.getTulipUserUuid()));
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.publishMessageOntoOutputWindowWithErrorPopup("Failed to retrieve Peony tulip-user information. " + getMessage());
            }

            @Override
            protected void succeeded() {
                try {
                    PeonyTulipUser aPeonyTulipUser = get();
                    if (aPeonyTulipUser == null){
                        PeonyFaceUtils.displayWarningMessageDialog("Cannot find this tulip user from the database.");
                    }else{
                        displayTulipNotificationList(aPeonyTulipUser.getNotifications());
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.publishMessageOntoOutputWindowWithErrorPopup("Cannot retrieve Peony tulip-user information. " + ex.getMessage());
                }
            }
        });
        
        sendNotificationButton.setOnAction(evt -> {
            sendNotificaton();
        });
    }

    private void sendNotificaton() {
        this.getCachedThreadPoolExecutorService().submit(new Task<G02TulipNotification>(){
            @Override
            protected G02TulipNotification call() throws Exception {
                if (ZcaValidator.isNullEmpty(notificatonTextArea.getText())){
                    updateMessage("You have to input the content of notification.");
                    throw new Exception(getMessage());
                }
                
                G02TulipNotification aG02TulipNotification = new G02TulipNotification();
                aG02TulipNotification.setContent(notificatonTextArea.getText());
                aG02TulipNotification.setContentCreatorUuid(PeonyProperties.getSingleton().getCurrentLoginUserUuid());
                aG02TulipNotification.setContentReceiverUuid(targetTulipUser.getTulipUserUuid());
                aG02TulipNotification.setCreatorName(PeonyProperties.getSingleton().getCurrentLoginEmployee().getPeonyUserFullName());
                aG02TulipNotification.setReceiverName(targetTulipUser.getUsername());
                aG02TulipNotification.setNotificationUuid(ZcaUtils.generateUUIDString());
                
                G02TulipNotification result = Lookup.getDefault().lookup(PeonyCustomerService.class).getPeonyCustomerRestClient()
                        .storeEntity_XML(G02TulipNotification.class, GardenRestParams.Customer.storeTulipNotificationRestParams(), aG02TulipNotification);
                if (result != null){
                    if (!PeonyProperties.getSingleton().isDevelopmentMode()){
                        //send SMS to every contactor in the list
                        Lookup.getDefault().lookup(PeonyManagementService.class).getPeonyManagementRestClient()
                                .requestOperation_XML(GardenRestParams.Management.sendSmsRestParams(targetTulipUser.getMobile(), 
                                        "Lu Yin Accounting Firm: You have an important notification. Please open your Tulip-app to check it out."));
                    }
                }
                return result;
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.displayErrorMessageDialog("Failed to send notification to tulip-user. " + getMessage());
            }

            @Override
            protected void succeeded() {
                try {
                    displayTulipNotification(get());
                    notificatonTextArea.setText("");
                } catch (InterruptedException | ExecutionException ex) {
                    //Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.publishMessageOntoOutputWindowWithErrorPopup("[Tech Reason] Failed to send notification to tulip-user. " + ex.getMessage());
                }
            }
            
        });
    }

    private void displayTulipNotificationList(final List<G02TulipNotification> aG02TulipNotificationList) {
        if (Platform.isFxApplicationThread()){
            displayTulipNotificationListHelper(aG02TulipNotificationList);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    displayTulipNotificationListHelper(aG02TulipNotificationList);
                }
            });
        }
    }
    
    private void displayTulipNotificationListHelper(final List<G02TulipNotification> aG02TulipNotificationList) {
        if (aG02TulipNotificationList == null){
            return;
        }
        for (G02TulipNotification aG02TulipNotification : aG02TulipNotificationList){
            displayTulipNotification(aG02TulipNotification);
        }
    }

    private void displayTulipNotification(final G02TulipNotification aG02TulipNotification) {
        if (Platform.isFxApplicationThread()){
            displayTulipNotificationHelper(aG02TulipNotification);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    displayTulipNotificationHelper(aG02TulipNotification);
                }
            });
        }
    }
    
    private void displayTulipNotificationHelper(final G02TulipNotification aG02TulipNotification) {
        
        VBox contentBox = new VBox();
        contentBox.getChildren().add(new Label(aG02TulipNotification.getCreatorName() 
                + " [" + ZcaCalendar.convertToMMddyyyyHHmmss(aG02TulipNotification.getCreated(), "-", "@", ":") + "]"));
        contentBox.getChildren().add(new Label(aG02TulipNotification.getContent()));
        notificationsBox.getChildren().add(contentBox);
    }
}
