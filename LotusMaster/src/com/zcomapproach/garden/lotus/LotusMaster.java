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

package com.zcomapproach.garden.lotus;

import com.zcomapproach.garden.lotus.agent.GardenDatabaseBackup;
import com.zcomapproach.garden.lotus.agent.ILotusAgent;
import com.zcomapproach.garden.lotus.agent.LotusGmailReceiver;
import java.util.ArrayList;
import java.util.List;

/**
 * LotusMaster is a server dedicated to do daily support and/or maintenance things. 
 * For example, retrieve google emails for the entire system, and Backup Garden 
 * database.
 * <p>
 * sudo nohup java -jar LotusMaster.jar &
 * <p>
 * @author zhijun98
 */
public class LotusMaster {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LotusMaster master = new LotusMaster();
        master.startAgents();
        
        //keep LotusMaster running in the memory until it was stoped by LotusProperties...
        while (LotusProperties.getSingleton().isLotusMasterRunning(true)){
            try {
                Thread.sleep(1000*60);    //one minute
            } catch (InterruptedException ex) {
                //Logger.getLogger(LotusMaster.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }//while
        
        master.stopAgents();
    }

    private final List<ILotusAgent> agents = new ArrayList<>();

    public LotusMaster() {
        agents.add(new LotusGmailReceiver());
        agents.add(new GardenDatabaseBackup());
    }
    
    private void startAgents(){
        LotusProperties.getSingleton().startLotusMaster();
        for (ILotusAgent agent : agents){
            agent.start();
        }
    }
    
    private void stopAgents(){
        //LotusProperties.getSingleton().stopLotusMaster();
        for (ILotusAgent agent : agents){
            agent.stop();
        }
    }
}
