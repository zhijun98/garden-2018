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
import com.zcomapproach.garden.email.GardenEmailMessage;
import com.zcomapproach.garden.peony.kernel.services.PeonyEmailService;
import com.zcomapproach.garden.peony.utils.PeonyDataUtils;
import com.zcomapproach.garden.persistence.entity.G02OfflineEmail;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class PeonyRelatedCommunicationController extends PeonyEntityOwnerFaceController {
    @FXML
    private Label pageTitleLabel;
    @FXML
    private VBox tabRootVBox;
    @FXML
    private GridPane topTitleGridPane;
    @FXML
    private HBox topButtonsHBox;
    
    private final List<G02OfflineEmail> targetOfflineEmailList;

    public PeonyRelatedCommunicationController(List<G02OfflineEmail> targetOfflineEmailList, Object targetOwner) {
        super(targetOwner);
        this.targetOfflineEmailList = targetOfflineEmailList;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /**
         * initialize email communications
         */
        Label titleLabel;
        if ((targetOfflineEmailList == null) || (targetOfflineEmailList.isEmpty())){
            titleLabel = new Label("No any records.");
            VBox.setMargin(titleLabel, new Insets(25, 10, 15, 10));
            tabRootVBox.getChildren().add(titleLabel);
        }else{
            titleLabel = new Label("Related Emails");
            VBox.setMargin(titleLabel, new Insets(25, 10, 15, 10));
            tabRootVBox.getChildren().add(titleLabel);
            tabRootVBox.getChildren().add(new Separator());
            GardenEmailMessage aGardenEmailMessage;
            JFXButton emailTagButton;
            for (G02OfflineEmail aG02OfflineEmail : targetOfflineEmailList){
                aGardenEmailMessage = PeonyDataUtils.convertToGardenEmailMessageByDeserialization(aG02OfflineEmail);
                if (aGardenEmailMessage != null){
                    emailTagButton = new JFXButton(aGardenEmailMessage.retrieveEmailHeadline());
                    VBox.setMargin(emailTagButton, new Insets(2, 10, 2, 10));
                    emailTagButton.setOnAction((ActionEvent event) -> {
                        final String offlineEmailUuid = aG02OfflineEmail.getOfflineEmailUuid();
                        Lookup.getDefault().lookup(PeonyEmailService.class).displayOfflineEmailTopComponentByOfflineEmailUuid(offlineEmailUuid);
                    });
                    tabRootVBox.getChildren().add(emailTagButton);
                }
            }
        }
    }

}
