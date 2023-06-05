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

package com.zcomapproach.garden.peony.view.actions;

import com.zcomapproach.garden.peony.view.controllers.PeonyMessagingBoardController;
import com.zcomapproach.garden.peony.view.dialogs.PeonyTalkingDialog;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 *
 * @author zhijun98
 */
public class PeonyMessagingBoardManager {
    
    private ExecutorService service = Executors.newCachedThreadPool();
    private final TabPane talkerTabPane;
    private final HashMap<String, PeonyMessagingBoardController> messagingBoardTabCache = new HashMap<>();   //key:JID; value: PeonyMessagingBoardController
    private final HashMap<String, PeonyTalkingDialog> messagingBoardDialogCache = new HashMap<>();   //key:JID; value: PeonyMessagingBoardController

    public PeonyMessagingBoardManager(TabPane talkerTabPane) {
        this.talkerTabPane = talkerTabPane;
    }
    
    public synchronized void shutdown(){
        if (service != null){
            service.shutdown();
        }
        service = null;
        
        Collection<PeonyTalkingDialog> aPeonyTalkingDialogList = messagingBoardDialogCache.values();
        for (PeonyTalkingDialog aPeonyTalkingDialog : aPeonyTalkingDialogList){
            aPeonyTalkingDialog.closeDialog(true);
        }
    }

    public synchronized PeonyMessagingBoardController retrievePeonyMessagingBoardController(String jid){
        return messagingBoardTabCache.get(jid);
    }
    
    public synchronized void addPeonyMessagingBoardController(String jid, PeonyMessagingBoardController aPeonyMessagingBoardController){
        messagingBoardTabCache.put(jid, aPeonyMessagingBoardController);
    }

    public synchronized PeonyTalkingDialog retrievePeonyMessagingBoardDialogController(String jid){
        return messagingBoardDialogCache.get(jid);
    }
    
    public synchronized void addPeonyMessagingBoardDialogController(String jid, PeonyTalkingDialog aPeonyTalkingDialog){
        messagingBoardDialogCache.put(jid, aPeonyTalkingDialog);
    }
    
    public synchronized void flashMessagingTabTitle(Tab tab) {
        if (service == null){
            return;
        }
        service.submit(new Runnable(){
            @Override
            public void run() {
                boolean recoverTitle = false;
                for (int i = 0; i < 8; i++){
                    if (talkerTabPane.getSelectionModel().getSelectedItem().equals(tab)){
                        //if the tab is current selected tab, it is not necessary to flash the title
                        recoverTitle = true;
                        break;
                    }
                    if (i % 2 == 0){
                        addTabTitleEffect(tab);
                    }else{
                        removeTabTitleEffect(tab);
                    }
                    try {
                        Thread.sleep(750);
                    } catch (InterruptedException ex) {
                        break;
                    }
                }//for...
                if (recoverTitle){
                    removeTabTitleEffect(tab);
                }else{
                    addTabTitleEffect(tab);
                }
            }
        });
    }

    private void addTabTitleEffect(final Tab tab) {
        if (tab != null){
            if (Platform.isFxApplicationThread()){
                removeTabTitleEffectHelper(tab);
                addTabTitleEffectHelper(tab);
            }else{
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        removeTabTitleEffectHelper(tab);
                        addTabTitleEffectHelper(tab);
                    }
                });
            }
        }
    }
    
    private void addTabTitleEffectHelper(Tab tab){
        tab.setText(tab.getText() + "*");
    }

    public void removeTabTitleEffect(Tab tab) {
        if (tab != null){
            if (Platform.isFxApplicationThread()){
                removeTabTitleEffectHelper(tab);
            }else{
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        removeTabTitleEffectHelper(tab);
                    }
                });
            }
        }
    }
    private void removeTabTitleEffectHelper(Tab tab){
        tab.setText(tab.getText().replace("*", ""));
    }
}
