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

package com.zcomapproach.garden.peony.kernel;

import com.zcomapproach.garden.peony.kernel.events.PeonyServiceEvent;
import com.zcomapproach.garden.peony.kernel.listeners.PeonyServiceEventListener;
import com.zcomapproach.garden.peony.kernel.services.PeonyService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author zhijun98
 */
public abstract class PeonyServiceProvider implements PeonyService {

    /**
     * This single thread keep the task-order
     */
    private final ExecutorService executorCachedService = Executors.newCachedThreadPool();
    
    /**
     * Listeners to this TopComponent
     */
    private final List<PeonyServiceEventListener> peonyFaceEventListenerList = new ArrayList<>();

    public ExecutorService getExecutorCachedService() {
        return executorCachedService;
    }
    
    @Override
    public void addPeonyServiceEventListener(PeonyServiceEventListener listener){
        synchronized(peonyFaceEventListenerList){
            peonyFaceEventListenerList.remove(listener);
            peonyFaceEventListenerList.add(listener);
        }
    }
    
    @Override
    public void removePeonyServiceEventListener(PeonyServiceEventListener listener){
        synchronized(peonyFaceEventListenerList){
            peonyFaceEventListenerList.remove(listener);
        }
    }
    
    protected void broadcastPeonyServiceEventHappened(PeonyServiceEvent event){
        synchronized(peonyFaceEventListenerList){
            for (PeonyServiceEventListener aPeonyFaceControllerEventListener : peonyFaceEventListenerList){
                executorCachedService.submit(new Runnable(){
                    @Override
                    public void run() {
                        aPeonyFaceControllerEventListener.peonyServiceEventHappened(event);
                    }
                });
            }
        }
    }
    
}
