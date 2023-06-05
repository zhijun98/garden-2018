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

package com.zcomapproach.garden.peony.taxpayer.controllers;

import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.garden.peony.kernel.services.PeonyManagementService;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.peony.view.events.PeonyFaceEvent;
import com.zcomapproach.garden.peony.view.listeners.PeonyFaceEventListener;
import com.zcomapproach.garden.persistence.entity.G02Log;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.garden.util.GardenSorter;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author zhijun98
 */
public class TaxpayerWorkStatusController extends PeonyTaxpayerServiceController implements PeonyFaceEventListener{
    
    @FXML
    private TreeView<String> workStatusTreeView;
    @FXML
    private Button refreshButton;
    
    private TreeItem<String> workStatusTreeRoot;
    
    private final PeonyTaxpayerCase targetPeonyTaxpayerCase;

    public TaxpayerWorkStatusController(PeonyTaxpayerCase targetPeonyTaxpayerCase) {
        super(targetPeonyTaxpayerCase);
        this.targetPeonyTaxpayerCase = targetPeonyTaxpayerCase;
    }

    @Override
    public void peonyFaceEventHappened(PeonyFaceEvent event) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshButton.setVisible(false);    //todo zzj: it is not available yet
        refreshButton.setOnAction(evt -> {
            loadWorkStatusTreeView(true);
        });
        loadWorkStatusTreeView(false);
    }
        
    private void loadWorkStatusTreeView(final boolean isRefresh){
        this.getCachedThreadPoolExecutorService().submit(new Task<TreeItem<String>>(){
            @Override
            protected TreeItem<String> call() throws Exception {
                TreeItem<String> root = new TreeItem("Work Status Changing History");
                if (isRefresh){
                    
                }else{
                    List<G02Log> statusList = targetPeonyTaxpayerCase.getTaxpayerCaseWorkStatusList();
                    GardenSorter.sortLogListByTimestamp(statusList, true);
                    PeonyEmployee employee;
                    for (G02Log status : statusList){
                        employee = Lookup.getDefault().lookup(PeonyManagementService.class).retrievePeonyEmployee(status.getOperatorAccountUuid());
                        if (employee == null){
                            root.getChildren().add(new TreeItem<>(status.getLogMessage() + "(Ex-employee - "+ZcaCalendar.convertToMMddyyyyHHmmss(status.getCreated())+")"));
                        }else{
                            root.getChildren().add(new TreeItem<>(status.getLogMessage() + "("+employee.getPeonyUserFullName()+" - "+ZcaCalendar.convertToMMddyyyyHHmmss(status.getCreated())+")"));
                        }
                    }
                }
                return root;
            }

            @Override
            protected void failed() {
                PeonyFaceUtils.publishMessageOntoOutputWindow("Cannot initialize work-status tree due to technical error.");
            }

            @Override
            protected void succeeded() {
                try {
                    workStatusTreeRoot = get();
                    workStatusTreeView.setRoot(null);
                    workStatusTreeView.setRoot(workStatusTreeRoot);
                    workStatusTreeRoot.setExpanded(true);
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                    PeonyFaceUtils.publishMessageOntoOutputWindow("Cannot successfully get the root of work-status tree due to technical error.");
                }
            }
        });
    }

}
