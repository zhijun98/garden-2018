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

package com.zcomapproach.garden.lotus.agent;

/**
 *
 * @author zhijun98
 */
public class GardenDatabaseBackup extends LotusAgent {

    private static volatile GardenDatabaseBackup self = null;
    public static GardenDatabaseBackup getSingleton() {
        GardenDatabaseBackup selfLocal = GardenDatabaseBackup.self;
        if (selfLocal == null){
            synchronized (GardenDatabaseBackup.class) {
                selfLocal = GardenDatabaseBackup.self;
                if (selfLocal == null){
                    GardenDatabaseBackup.self = selfLocal = new GardenDatabaseBackup();
                }
            }
        }
        return selfLocal;
    }

    @Override
    public void start() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stop() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
