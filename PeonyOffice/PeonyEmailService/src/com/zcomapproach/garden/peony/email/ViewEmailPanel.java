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

package com.zcomapproach.garden.peony.email;

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.email.GardenEmailMessage;
import com.zcomapproach.garden.email.GardenEmailUtils;
import com.zcomapproach.garden.peony.email.events.DeletePeonyEmailMessageEvent;
import com.zcomapproach.garden.email.data.OfflineMessageStatus;
import com.zcomapproach.garden.peony.kernel.services.PeonyEmailService;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.entity.G02OfflineEmailAttachment;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmail;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.nio.ZcaNio;
import com.zcomapproach.garden.peony.PeonyLauncher;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.web.WebView;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.windows.WindowManager;

/**
 *
 * @author zhijun98
 */
public class ViewEmailPanel extends AnchorPane{

    private final Label emailFromLabel;
    private final Label emailToListLabel;
    private final Label emailCcListLabel;
    private final Label emailSubjectLabel;
    private final Label emailTimestampLabel;
    private final Button replyButton;
    private final Button replyAllButton;
    private final Button forwardButton;
    private final Button deleteButton;
    private final GridPane topZone;
    
    private StackPane emailContentStackPane;
    private WebView emailContentWebView;
    private Label emailAttachementLabel;
    private Button emailAttachementButton;
    private GardenEmailMessage targetGardenEmailMessage;
    private final PeonyOfflineEmail targetPeonyOfflineEmail;
    private final boolean readOnly;
    
    /**
     * This single thread keep the task-order
     */
    private final ExecutorService singleExecutorService = Executors.newSingleThreadExecutor();
    private final List<PeonyFaceEventListener> peonyFaceEventListenerList = new ArrayList<>();
    
    public ViewEmailPanel(GardenEmailMessage targetGardenEmailMessage, PeonyOfflineEmail targetPeonyOfflineEmail, boolean readOnly){
        this.targetGardenEmailMessage = targetGardenEmailMessage;
        this.targetPeonyOfflineEmail = targetPeonyOfflineEmail;
        this.readOnly = readOnly;
        /**
         * Top of border pane
         */
        topZone = new GridPane();
        BorderPane.setAlignment(topZone, Pos.CENTER);
        topZone.prefHeightProperty().setValue(150.0);
        topZone.minHeightProperty().setValue(150.0);
        topZone.maxHeightProperty().setValue(150.0);
        //columns
        ColumnConstraints leftColumnConstraints = new ColumnConstraints();
        leftColumnConstraints.halignmentProperty().setValue(HPos.RIGHT);
        leftColumnConstraints.hgrowProperty().setValue(Priority.NEVER);
        leftColumnConstraints.prefWidthProperty().setValue(75.0);
        leftColumnConstraints.maxWidthProperty().setValue(75.0);
        leftColumnConstraints.minWidthProperty().setValue(75.0);
        ColumnConstraints rightColumnConstraints = new ColumnConstraints();
        rightColumnConstraints.hgrowProperty().setValue(Priority.SOMETIMES);
        rightColumnConstraints.minWidthProperty().setValue(10.0);
        rightColumnConstraints.prefWidthProperty().setValue(240.0);
        topZone.getColumnConstraints().add(leftColumnConstraints);
        topZone.getColumnConstraints().add(rightColumnConstraints);
        //rows
        RowConstraints aRowConstraints;
        for (int i = 0; i < 6; i++){
            aRowConstraints = new RowConstraints();
            aRowConstraints.minHeightProperty().setValue(10.0);
            aRowConstraints.prefHeightProperty().setValue(30.0);
            aRowConstraints.vgrowProperty().setValue(Priority.SOMETIMES);
            topZone.getRowConstraints().add(aRowConstraints);
        }
        //labels in left colum
        topZone.add(createTopZoneLabelForLeftColumn("From:"), 0, 0);
        topZone.add(createTopZoneLabelForLeftColumn("To:"), 0, 1);
        topZone.add(createTopZoneLabelForLeftColumn("Cc:"), 0, 2);
        topZone.add(createTopZoneLabelForLeftColumn("Subject:"), 0, 3);
        topZone.add(createTopZoneLabelForLeftColumn("Attachment:"), 0, 4);
        topZone.add(createTopZoneLabelForLeftColumn("Date/Time:"), 0, 5);
        //labels in right colum
        emailFromLabel = createTopZoneLabelForRightColumn(GardenEmailUtils.convertAddressListToText(targetGardenEmailMessage.getFromList()));
        topZone.add(emailFromLabel, 1, 0);
        emailToListLabel = createTopZoneLabelForRightColumn(GardenEmailUtils.convertAddressListToText(targetGardenEmailMessage.getToList()));
        topZone.add(emailToListLabel, 1, 1);
        emailCcListLabel = createTopZoneLabelForRightColumn(GardenEmailUtils.convertAddressListToText(targetGardenEmailMessage.getCcList()));
        topZone.add(emailCcListLabel, 1, 2);
        emailSubjectLabel = createTopZoneLabelForRightColumn(targetGardenEmailMessage.getSubject());
        topZone.add(emailSubjectLabel, 1, 3);

        installEmailAttachementControl();

        emailTimestampLabel = createTopZoneLabelForRightColumn(ZcaCalendar.convertToMMddyyyyHHmmss(targetGardenEmailMessage.retrieveEmailTimestamp(), "-", " @", ":"));
        topZone.add(emailTimestampLabel, 1, 5);


        /**
         * Bottom of border pane
         */
        FlowPane bottomZone = new FlowPane();
        bottomZone.alignmentProperty().setValue(Pos.CENTER);
        bottomZone.prefHeightProperty().setValue(30.0);
        BorderPane.setAlignment(bottomZone, Pos.CENTER);
        replyButton = createBottomButton(OfflineMessageStatus.REPLY.value(), new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                Lookup.getDefault().lookup(PeonyEmailService.class).displayComposeEmailTopComponent(targetGardenEmailMessage, targetPeonyOfflineEmail, OfflineMessageStatus.REPLY);
            }
        });
        bottomZone.getChildren().add(replyButton);
        replyAllButton = createBottomButton(OfflineMessageStatus.REPLY_ALL.value(), new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                Lookup.getDefault().lookup(PeonyEmailService.class).displayComposeEmailTopComponent(targetGardenEmailMessage, targetPeonyOfflineEmail, OfflineMessageStatus.REPLY_ALL);
            }
        });
        bottomZone.getChildren().add(replyAllButton);
        forwardButton = createBottomButton(OfflineMessageStatus.FORWARD.value(), new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                Lookup.getDefault().lookup(PeonyEmailService.class).displayComposeEmailTopComponent(targetGardenEmailMessage, targetPeonyOfflineEmail, OfflineMessageStatus.FORWARD);
            }
        });
        bottomZone.getChildren().add(forwardButton);
        deleteButton = createBottomButton("Delete", new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to delete this email permanently?") == JOptionPane.YES_OPTION){
                    broadcastPeonyFaceEventHappened(new DeletePeonyEmailMessageEvent(targetGardenEmailMessage));
                }
            }
        });
        bottomZone.getChildren().add(deleteButton);
        /**
         * Center of border pane
         */
        ScrollPane centerZone = new ScrollPane();
        BorderPane.setAlignment(centerZone, Pos.CENTER);
        if (Platform.isFxApplicationThread()){
            emailContentStackPane = new StackPane();
            emailContentStackPane.prefWidthProperty().bind(this.widthProperty().subtract(20));
            //emailContentStackPane.setStyle("-fx-border-color: blue");
            emailContentWebView = new WebView();
            emailContentWebView.getEngine().loadContent(targetGardenEmailMessage.tryToGetHtmlEmailConent());
            StackPane.setMargin(emailContentWebView, new Insets(2, 10, 2, 10));
            emailContentStackPane.getChildren().add(emailContentWebView);
            centerZone.setContent(emailContentStackPane);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    emailContentWebView = new WebView();
                    emailContentWebView.getEngine().loadContent(targetGardenEmailMessage.tryToGetHtmlEmailConent());
                    centerZone.setContent(emailContentWebView);
                }
            });
        }
        /**
         * Layout with border pane for tab root
         */
        BorderPane borderPane = new BorderPane();
        borderPane.prefHeightProperty().setValue(287.0);
        borderPane.prefWidthProperty().setValue(318.0);
        borderPane.layoutXProperty().setValue(91.0);
        borderPane.layoutYProperty().setValue(50.0);
        AnchorPane.setTopAnchor(borderPane, 0.0);
        AnchorPane.setBottomAnchor(borderPane, 0.0);
        AnchorPane.setLeftAnchor(borderPane, 0.0);
        AnchorPane.setRightAnchor(borderPane, 0.0);
        borderPane.setTop(topZone);
        borderPane.setBottom(bottomZone);
        borderPane.setCenter(centerZone);
        /**
         * Tab root
         */
        prefHeightProperty().setValue(180.0);
        prefWidthProperty().setValue(200.0);
        maxHeight(Double.MAX_VALUE);
        maxWidth(Double.MAX_VALUE);
        minHeight(0);
        minWidth(0);
        getChildren().add(borderPane);
    }

    private Button createBottomButton(String text, EventHandler<ActionEvent> eventHandler){
        Button aBtn = new Button(text);
        FlowPane.setMargin(aBtn, new Insets(0, 1, 0, 1));
        aBtn.setOnAction(eventHandler);
        aBtn.setDisable(readOnly);
        return aBtn;
    }

    private Label createTopZoneLabelForLeftColumn(String text){
        Label aLabel = new Label(text);
        GridPane.setMargin(aLabel, new Insets(0,5,0,0));
        return aLabel;
    }

    private Label createTopZoneLabelForRightColumn(String text){
        Label aLabel = new Label(text);
        aLabel.setTextFill(Paint.valueOf("BLUE"));
        return aLabel;
    }

    private void installEmailAttachementControl() {
        String attachmentFileNames = targetGardenEmailMessage.retrieveAttachmentFileNames();
        if (ZcaValidator.isNullEmpty(attachmentFileNames)){
            topZone.getChildren().remove(emailAttachementButton);
            if (emailAttachementLabel == null){
                emailAttachementLabel = createTopZoneLabelForRightColumn(attachmentFileNames);
            }
            topZone.add(emailAttachementLabel, 1, 4);
        }else{
            topZone.getChildren().remove(emailAttachementLabel);
            if (emailAttachementButton == null){
                emailAttachementButton = new Button(attachmentFileNames);
                emailAttachementButton.setOnAction((ActionEvent e) -> {
                    saveEmailAttachement();
                });
                emailAttachementButton.setTextFill(Paint.valueOf("BLUE"));
            }
            topZone.add(emailAttachementButton, 1, 4);
        }
    }

    private void saveEmailAttachement() {
        if (SwingUtilities.isEventDispatchThread()){
            saveEmailAttachementHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    saveEmailAttachementHelper();
                }
            });
        }
    }

    private void saveEmailAttachementHelper() {
        /**
         * User select the destination path
         */
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a Folder");
        fileChooser.setApproveButtonText("Save To");
        String saveLocation = PeonyFaceUtils.getFolderByFileChooser(fileChooser, PeonyLauncher.mainFrame, null).getAbsolutePath();
        if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, "Are you sure to save attachment(s) into " + saveLocation + "?") != JOptionPane.YES_OPTION){
            return;
        }
        String srcFilePath;
        String dstFilePath;
        List<G02OfflineEmailAttachment> aG02OfflineEmailAttachmentList = targetPeonyOfflineEmail.getOfflineEmailAttachmentList();
        for (G02OfflineEmailAttachment aG02OfflineEmailAttachment : aG02OfflineEmailAttachmentList){
            srcFilePath = aG02OfflineEmailAttachment.getFilePath();
            if (ZcaValidator.isNotNullEmpty(srcFilePath)){
                try {
                    dstFilePath = saveLocation + ZcaNio.fileSeparator()+ aG02OfflineEmailAttachment.getOriginalFileName();
                    if (ZcaNio.isValidFile(dstFilePath)){
                        if (PeonyFaceUtils.displayConfirmDialog(PeonyLauncher.mainFrame, dstFilePath + " exists in the system. Are you sure to overwrite it?") != JOptionPane.YES_OPTION){
                            break;
                        }
                    }
                    ZcaNio.copyFile(srcFilePath, dstFilePath);
                    PeonyFaceUtils.displayInformationMessageDialog("Saved attachment to: " + dstFilePath);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }//for-loop
        }
    }

    public void updateGardenEmailMessage(GardenEmailMessage aPeonyEmailMesage) {
        if (aPeonyEmailMesage != null){
            targetGardenEmailMessage = aPeonyEmailMesage;
        }
        try{
            emailFromLabel.textProperty().setValue(GardenEmailUtils.convertAddressListToText(targetGardenEmailMessage.getFromList()));
            emailToListLabel.textProperty().setValue(GardenEmailUtils.convertAddressListToText(targetGardenEmailMessage.getToList()));
            emailCcListLabel.textProperty().setValue(GardenEmailUtils.convertAddressListToText(targetGardenEmailMessage.getCcList()));
            emailTimestampLabel.textProperty().setValue(ZcaCalendar.convertToMMddyyyyHHmmss(targetGardenEmailMessage.retrieveEmailTimestamp(), "-", " @", ":"));
            emailContentWebView.getEngine().loadContent(targetGardenEmailMessage.tryToGetHtmlEmailConent());

            installEmailAttachementControl();
        }catch (Exception ex){
            //do nothing: null pointer exception
        }
    }

    void setDeleteButtonEnabled(final boolean value) {
        if (Platform.isFxApplicationThread()){
            deleteButton.setDisable(!value);
        }else{
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    deleteButton.setDisable(!value);
                }
            });
        }
    }

    public List<PeonyFaceEventListener> getPeonyFaceEventListenerList() {
        synchronized(peonyFaceEventListenerList){
            return new ArrayList<>(peonyFaceEventListenerList);
        }
    }
    
    public void addPeonyFaceEventListenerList(List<PeonyFaceEventListener> listeners){
        if ((listeners == null) || (listeners.isEmpty())){
            return;
        }
        synchronized(peonyFaceEventListenerList){
            for (PeonyFaceEventListener listener : listeners){
                addPeonyFaceEventListener(listener);
            }
        }
    }
    
    public void addPeonyFaceEventListener(PeonyFaceEventListener listener){
        synchronized(peonyFaceEventListenerList){
            peonyFaceEventListenerList.remove(listener);
            peonyFaceEventListenerList.add(listener);
        }
    }
    
    public void removePeonyFaceEventListener(PeonyFaceEventListener listener){
        synchronized(peonyFaceEventListenerList){
            peonyFaceEventListenerList.remove(listener);
        }
    }
    
    /**
     * Broadcase event to listners sequentially in a single thread service 
     * @param event 
     */
    protected void broadcastPeonyFaceEventHappened(PeonyFaceEvent event){
        synchronized(peonyFaceEventListenerList){
            for (PeonyFaceEventListener aPeonyFaceControllerEventListener : peonyFaceEventListenerList){
                singleExecutorService.submit(new Runnable(){
                    @Override
                    public void run() {
                        aPeonyFaceControllerEventListener.peonyFaceEventHappened(event);
                    }
                });
            }
        }
    }
}
