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

package com.zcomapproach.garden.peony.search;

import com.zcomapproach.garden.peony.kernel.services.PeonySearchService;
import com.zcomapproach.garden.peony.kernel.PeonyServiceProvider;
import com.zcomapproach.garden.persistence.peony.data.PeonySearchResult;
import javax.swing.SwingUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author zhijun98
 */
@ServiceProvider(service = PeonySearchService.class)
public class PeonySearchServiceProvider extends PeonyServiceProvider implements PeonySearchService{
    
    private PeonySearchResultTopComponent peonySearchPaneTopComponent;

    @Override
    public void openPeonySearchEnginePane(){
        if (SwingUtilities.isEventDispatchThread()){
            openPeonySearchEnginePaneHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    openPeonySearchEnginePaneHelper();
                }
            });
        }
    }
    
    private void openPeonySearchEnginePaneHelper(){
        if (peonySearchPaneTopComponent == null){
            peonySearchPaneTopComponent = new PeonySearchResultTopComponent();
            peonySearchPaneTopComponent.setName("Peony Search Engine");
            peonySearchPaneTopComponent.initializePeonySearchResultTopComponent(null);
        }
        peonySearchPaneTopComponent.open();
        peonySearchPaneTopComponent.requestActive();
    }
    
    @Override
    public void openPeonySearchResultPane(final PeonySearchResult searchResultObj){
        if (searchResultObj == null){
            return;
        }
        if (SwingUtilities.isEventDispatchThread()){
            openPeonySearchResultTopComponentHelper(searchResultObj);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    openPeonySearchResultTopComponentHelper(searchResultObj);
                }
            });
        }
    }
    
    private void openPeonySearchResultTopComponentHelper(final PeonySearchResult searchResultObj){
        PeonySearchResultTopComponent peonySearchResultTopComponent = new PeonySearchResultTopComponent();
        peonySearchResultTopComponent.initializePeonySearchResultTopComponent(searchResultObj);
        peonySearchResultTopComponent.open();
        peonySearchResultTopComponent.requestActive();
    }

    @Override
    public void closeService() {
        
    }
    
}
