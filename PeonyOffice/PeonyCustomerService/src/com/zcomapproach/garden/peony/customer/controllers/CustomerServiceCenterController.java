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

import com.zcomapproach.garden.peony.customer.data.PeonyTulipUserTreeItemData;
import com.zcomapproach.garden.peony.kernel.services.PeonyCustomerService;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.persistence.peony.PeonyTulipUser;
import com.zcomapproach.garden.persistence.peony.PeonyTulipUserList;
import com.zcomapproach.garden.rest.GardenRestParams;
import com.zcomapproach.garden.util.GardenSorter;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import org.openide.util.Lookup;

/**
 * Then main panel displayed on the explorer-side on Peony
 * @author zhijun98
 */
public class CustomerServiceCenterController extends PeonyCustomerServiceController{
    @FXML
    private TreeView<PeonyTulipUserTreeItemData> customerTreeView;
    private TreeItem<PeonyTulipUserTreeItemData> customerTreeRoot;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeCustomerTreeView();
    }

    private void initializeCustomerTreeView() {
        customerTreeRoot = new TreeItem<>(new PeonyTulipUserTreeItemData("Tulip Mobile Customers"));
        customerTreeRoot.setExpanded(true);
        customerTreeView.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            if (event.getClickCount() == 2){
                TreeItem<PeonyTulipUserTreeItemData> selectedItem = customerTreeView.getSelectionModel().getSelectedItem();
                if ((selectedItem == null) || (selectedItem.getValue() == null)){
                    return;
                }
                if (selectedItem.getValue().getTulipUser() != null){
                    Lookup.getDefault().lookup(PeonyCustomerService.class).launchTulipCustomerProfile(selectedItem.getValue().getTulipUser());
                }
            }
        });
        this.getCachedThreadPoolExecutorService().submit(new LoadCustomerTreeViewTask());
    }
    
    private class LoadCustomerTreeViewTask extends Task<List<PeonyTulipUser>> {
        @Override
        protected List<PeonyTulipUser> call() throws Exception {
            PeonyTulipUserList aPeonyTulipUserList = Lookup.getDefault().lookup(PeonyCustomerService.class).getPeonyCustomerRestClient()
                    .findEntity_XML(PeonyTulipUserList.class, GardenRestParams.Customer.retrieveSimpleCustomerListRestParams());
            if (aPeonyTulipUserList == null){
                return null;
            }
            GardenSorter.sortPeonyTulipUserByUsername(aPeonyTulipUserList.getPeonyTulipUserList(), true);
            return aPeonyTulipUserList.getPeonyTulipUserList();
        }

        @Override
        protected void failed() {
            PeonyFaceUtils.displayErrorMessageDialog("Cannot display tulip customers. " + getMessage());
        }

        @Override
        protected void succeeded() {
            try {
                List<PeonyTulipUser> result = get();
                
                customerTreeRoot.getChildren().clear();
                if (result == null){
                    customerTreeRoot.getChildren().add(new TreeItem<>(new PeonyTulipUserTreeItemData("No Records")));
                }else{
                    for (PeonyTulipUser aPeonyTulipUser : result){
                        customerTreeRoot.getChildren().add(new TreeItem<>(new PeonyTulipUserTreeItemData(aPeonyTulipUser.getTulipUser())));
                    }
                }
                customerTreeView.setRoot(customerTreeRoot);
                customerTreeView.refresh();
            } catch (InterruptedException | ExecutionException ex) {
                //Exceptions.printStackTrace(ex);
                PeonyFaceUtils.publishMessageOntoOutputWindow("[Tech Exception] Cannot display tulip customers. " + getMessage());
            }
        }
    }
}
