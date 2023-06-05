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

import com.zcomapproach.garden.face.control.DigitalClockLabel;
import com.zcomapproach.garden.peony.kernel.services.PeonyBusinessService;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.PeonyMemoListCreated;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.peony.PeonyMemo;
import com.zcomapproach.garden.persistence.peony.data.PeonyMemoFilter;
import com.zcomapproach.garden.persistence.peony.data.PeonyMemoList;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.commons.ZcaCalendar;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class PeonyPublicForumController extends PeonyWelcomeServiceController{
    @FXML
    private VBox titleBox;
    @FXML
    private BorderPane contentBorderPane;
    
    private ScheduledService<PeonyMemoList> scheduledForumService;
    private final PeonyMemoFilter peonyMemoFilter = new PeonyMemoFilter();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titleBox.getChildren().add(new DigitalClockLabel());
        
        Date toDate = ZcaCalendar.covertDateToEnding(new Date());
        Date fromDate = ZcaCalendar.addDates(toDate, -7);
        initializePublicBoardController("Public Message Board", new ArrayList<>(), fromDate, toDate, GardenEntityType.PUBLIC_BOARD);
        if (getPublicBoardController() != null){
            this.addPeonyFaceEventListener(getPublicBoardController()); //listen to this controller's scheduledForumService
            contentBorderPane.setCenter(getPublicBoardController().getRootPane());
        }
        startPublicBoardScheduledService();
    }

    public synchronized void startPublicBoardScheduledService() {
        if (scheduledForumService == null){
            scheduledForumService = new ScheduledService<PeonyMemoList>(){
                @Override
                protected Task<PeonyMemoList> createTask() {
                    return new Task<PeonyMemoList>(){
                        @Override
                        protected PeonyMemoList call() throws Exception {
                            PublicBoardController aPublicBoardController = getPublicBoardController();
                            peonyMemoFilter.setFromDate(aPublicBoardController.getFromDate());
                            peonyMemoFilter.setToDate(aPublicBoardController.getToDate());
                            return Lookup.getDefault().lookup(PeonyBusinessService.class).getPeonyBusinessRestClient().storeEntity_XML(PeonyMemoList.class, 
                                GardenRestParams.Business.retrievePeonyPublicForumMessageListRestParams(), peonyMemoFilter);
                        }

                        @Override
                        protected void failed() {
                            //PeonyFaceUtils.displayErrorMessageDialog("Operation failed due to technical errors. " + getMessage());
                            PeonyFaceUtils.publishMessageOntoOutputWindow("Cannot display messages on the forum. " + getMessage());
                        }

                        @Override
                        protected void succeeded() {
                            try {
                                PeonyMemoList aPeonyMemoList = get();
                                if ((aPeonyMemoList == null) 
                                        || (aPeonyMemoList.getPeonyMemoList() == null) 
                                        || (aPeonyMemoList.getPeonyMemoList().isEmpty()))
                                {
                                    return;
                                }
                                List<PeonyMemo> thePeonyMemoList = aPeonyMemoList.getPeonyMemoList();
                                for (PeonyMemo aPeonyMemo : thePeonyMemoList){
                                    peonyMemoFilter.getFilterMemoUuidSet().add(aPeonyMemo.getMemo().getMemoUuid());
                                }
                                //aPeonyMemoList.
                                broadcastPeonyFaceEventHappened(new PeonyMemoListCreated(thePeonyMemoList));
                            } catch (InterruptedException | ExecutionException ex) {
                                //Exceptions.printStackTrace(ex);
                                PeonyFaceUtils.displayErrorMessageDialog("Cannot display the forum. " + ex.getMessage());
                            }
                        }
                    };
                }
            };
            scheduledForumService.setPeriod(Duration.minutes(1));
            scheduledForumService.start();
        }//if
    }
    
    public synchronized void stopPublicBoardScheduledService() {
        if (scheduledForumService == null){
            scheduledForumService.cancel();
            scheduledForumService = null;
        }
    }
    
}
