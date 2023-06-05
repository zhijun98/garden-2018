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

package com.zcomapproach.garden.peony.email.dialogs;

import com.zcomapproach.garden.peony.email.controllers.AssignMessageController;
import com.zcomapproach.garden.peony.email.data.PeonyEmailTreeItemData;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.dialogs.PeonyFaceDialog;
import com.zcomapproach.garden.persistence.peony.PeonyEmailTag;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TreeItem;

/**
 *
 * @author zhijun98
 */
public class AssignEmailDialog extends PeonyFaceDialog {

    private String onlineMailBoxAddress;
    private List<TreeItem<PeonyEmailTreeItemData>> selectedEmailTreeItems;
    private PeonyEmailTag targetEmailTag;
    private AssignMessageController assignEmailController;
    
    public AssignEmailDialog(Frame parent, boolean modal) {
        super(parent, modal);
    }
    
    @Override
    protected void decorateDialogHelper(){
        this.setSize(new Dimension(600, 400));
    }

    @Override
    protected PeonyFaceController createPeonyFaceController() {
        if (getPeonyFaceController() == null){
            assignEmailController =  new AssignMessageController(onlineMailBoxAddress, selectedEmailTreeItems, targetEmailTag);
            assignEmailController.addPeonyFaceEventListener(this);
            assignEmailController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
            return assignEmailController;
        }
        return getPeonyFaceController();
    }

    /**
     * this method disabled users to call directly from this dialog instance
     */
    @Override
    public void launchPeonyDialog(final String dialogTitle) {
        throw new UnsupportedOperationException("Not supported for this dialog instance.");
    }

    public void launchAssignEmailDialog(final String dialogTitle, final String onlineMailBoxAddress, final List<TreeItem<PeonyEmailTreeItemData>> selectedEmailTreeItems) {
        this.selectedEmailTreeItems = selectedEmailTreeItems;
        this. onlineMailBoxAddress = onlineMailBoxAddress;
        super.launchPeonyDialog(dialogTitle);
    }

    public void launchAssignEmailDialogForTag(String dialogTitle, String onlineMailBoxAddress, TreeItem<PeonyEmailTreeItemData> tagTreeItem) {
        this.targetEmailTag = tagTreeItem.getValue().emitPeonyEmailTag();
        this.selectedEmailTreeItems = new ArrayList<>();
        selectedEmailTreeItems.add(tagTreeItem.getParent());
        this.onlineMailBoxAddress = onlineMailBoxAddress;
        super.launchPeonyDialog(dialogTitle);
    }

}
