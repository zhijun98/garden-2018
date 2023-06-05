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

package com.zcomapproach.garden.peony.search.dialogs;

import com.zcomapproach.garden.peony.search.controllers.PeonySearchResultDateFilterController;
import com.zcomapproach.garden.peony.search.controllers.PeonySearchResultTextFilterController;
import com.zcomapproach.garden.peony.view.PeonyFaceController;
import com.zcomapproach.garden.peony.view.dialogs.PeonyFaceDialog;
import java.awt.Frame;
import java.util.List;

/**
 *
 * @author zhijun98
 */
public class PeonySearchResultFilterDialog extends PeonyFaceDialog{

    private String filterTitle;
    private List<String> filterColumnValueList;
    private boolean dateColumn;   //if the column contains date-data-type 
    
    public PeonySearchResultFilterDialog(Frame parent, boolean modal) {
        super(parent, modal);
    }

    @Override
    protected PeonyFaceController createPeonyFaceController() {
        if (getPeonyFaceController() == null){
            PeonyFaceController aPeonyFaceController;
            if (dateColumn){
                aPeonyFaceController = new PeonySearchResultDateFilterController(filterTitle, filterColumnValueList);
            }else{
                aPeonyFaceController = new PeonySearchResultTextFilterController(filterTitle, filterColumnValueList);
            }
            aPeonyFaceController.addPeonyFaceEventListenerList(getPeonyFaceEventListenerList());
            return aPeonyFaceController;
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

    /**
     * 
     * @param dialogTitle
     * @param filterTitle
     * @param filterColumnValueList - if NULL, it uses "Date-column filter"
     */
    public void launchPeonySearchResultFilterDialog(final String dialogTitle, final String filterTitle, final List<String> filterColumnValueList, final boolean dateColumn)
    {
        this.filterTitle = filterTitle;
        this.filterColumnValueList = filterColumnValueList;
        this.dateColumn = dateColumn;
        super.launchPeonyDialog(dialogTitle);
    }

}
