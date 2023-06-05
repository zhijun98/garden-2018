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

import com.zcomapproach.garden.peony.kernel.data.PeonySmsContactor;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.controllers.PeonySmsPaneController;
import java.awt.Frame;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author zhijun98
 */
public class PeonySmsDialog extends PeonyFaceDialog {

    private List<PeonySmsContactor> peonySmsContactorList;
    
    public PeonySmsDialog(Frame parent, boolean modal) {
        super(parent, modal);
    }
    
    /**
     * this method disabled users to call directly from this dialog instance
     */
    @Override
    public void launchPeonyDialog(final String dialogTitle) {
        throw new UnsupportedOperationException("Not supported for this dialog instance.");
    }
    
    public void launchPeonySmsDialog(String dialogTitle, List<PeonySmsContactor> peonySmsContactorList){
        this.peonySmsContactorList = peonySmsContactorList;
        
        super.launchPeonyDialog(dialogTitle);
        
        if (SwingUtilities.isEventDispatchThread()){
            setTitle(dialogTitle);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    setTitle(dialogTitle);
                }
            });
        }
    
    }

    @Override
    protected PeonyFaceController createPeonyFaceController() {
        if (getPeonyFaceController() == null){
            return new PeonySmsPaneController(peonySmsContactorList);
        }
        return getPeonyFaceController();
    }

}
