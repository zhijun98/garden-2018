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

package com.zcomapproach.garden.peony.view.dialogs;

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.controllers.DocumentTagDataEntryController;
import com.zcomapproach.garden.persistence.peony.PeonyDocumentTag;
import java.awt.Frame;

/**
 *
 * @author zhijun98
 */
public class DocumentTagDataEntryDialog extends PeonyFaceDialog {
    private String dialogTitle;
    private PeonyDocumentTag targetPeonyDocumentTag;

    public DocumentTagDataEntryDialog(Frame parent, boolean modal) {
        super(parent, modal);
    }
    
    /**
     * this method disabled users to call directly from this dialog instance
     */
    @Override
    public void launchPeonyDialog(final String dialogTitle) {
        throw new UnsupportedOperationException("Not supported for this dialog instance.");
    }
    
    public void launchMemoDataEntryDialog(String dialogTitle, PeonyDocumentTag targetPeonyDocumentTag)
    {
        if (ZcaValidator.isNullEmpty(dialogTitle)){
            this.dialogTitle = "Document Tag";
        }else{
            this.dialogTitle = dialogTitle;
        }
        this.targetPeonyDocumentTag = targetPeonyDocumentTag;
        
        super.launchPeonyDialog(dialogTitle);
    }

    @Override
    protected PeonyFaceController createPeonyFaceController() {
        if (getPeonyFaceController() == null){
            DocumentTagDataEntryController aPeonyFaceController = new DocumentTagDataEntryController(targetPeonyDocumentTag);
            aPeonyFaceController.addPeonyFaceEventListenerList(this.getPeonyFaceEventListenerList());
            return aPeonyFaceController;
        }
        return getPeonyFaceController();
    }

}
