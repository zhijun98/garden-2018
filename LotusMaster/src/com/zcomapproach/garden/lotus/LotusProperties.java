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

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.commons.nio.ZcaNio;
import com.zcomapproach.garden.data.GardenFlower;
import com.zcomapproach.garden.lotus.data.LotusPropKey;
import com.zcomapproach.garden.lotus.data.LotusPropValue;
import com.zcomapproach.commons.AbstractZcaProperties;

/**
 *
 * @author zhijun98
 */
public class LotusProperties extends AbstractZcaProperties{
    
    private static volatile LotusProperties self = null;
    public static LotusProperties getSingleton(){
        LotusProperties selfLocal = LotusProperties.self;
        if (selfLocal == null){
            synchronized (LotusProperties.class) {
                selfLocal = LotusProperties.self;
                if (selfLocal == null){
                    LotusProperties.self = selfLocal = new LotusProperties();
                }
            }
        }
        return selfLocal;
    }

    @Override
    protected synchronized String getDefaultPropFullFilePathName() {
        return ZcaNio.getCurrentAbsolutePath() + ZcaNio.fileSeparator() + GardenFlower.LOTUS.name().toLowerCase();
    }
    
    /**
     * Gmail inbox may have thousands of email which might be too much for clients. 
     * LotusGmailReceiver uses this threshold to control how many emails will be 
     * received for clients. Default is 3000.
     * @return 
     */
    public synchronized int getReceivingMessageLoadingThreshold(){
        String threshold = super.getProperty(LotusPropKey.RECEIVING_GMAIL_THRESHOLD.name());
        int defaultValue = 3000;
        if (ZcaValidator.isNullEmpty(threshold)){
            super.setProperty(LotusPropKey.RECEIVING_GMAIL_THRESHOLD.name(), String.valueOf(defaultValue));
            return defaultValue;
        }
        try{
            return Integer.parseInt(threshold);
        }catch (Exception ex){
            return defaultValue;
        }
    }

    /**
     * 
     * @param isRefreshDemanded - If the properties is manually changed by the outside, 
     * this parameter should be true so that it may refresh the already-loaded properties.
     * @return 
     */
    public synchronized boolean isLotusMasterRunning(boolean isRefreshDemanded) {
        if (isRefreshDemanded) {
            super.refreshProperties();
        }
        return LotusPropValue.RUN.name().equalsIgnoreCase(super.getProperty(LotusPropKey.LOTUS_MASTER.name()));
    }

    /**
     * Change the properties' LotusPropKey.LOTUS_MASTER to be LotusPropValue.RUN
     */
    public synchronized void startLotusMaster() {
        String oldPropValue = super.getProperty(LotusPropKey.LOTUS_MASTER.name());
        super.setProperty(LotusPropKey.LOTUS_MASTER.name(), LotusPropValue.RUN.name());
        broadcastPropertyValueChanged(LotusPropKey.LOTUS_MASTER.name(), oldPropValue, LotusPropValue.RUN.name());
    }

    /**
     * Change the properties' LotusPropKey.LOTUS_MASTER to be LotusPropValue.STOP
     */
    public synchronized void stopLotusMaster() {
        String oldPropValue = super.getProperty(LotusPropKey.LOTUS_MASTER.name());
        super.setProperty(LotusPropKey.LOTUS_MASTER.name(), LotusPropValue.STOP.name());
        broadcastPropertyValueChanged(LotusPropKey.LOTUS_MASTER.name(), oldPropValue, LotusPropValue.STOP.name());
    }

    @Override
    protected void transferSharedPropFromOriginalProp(String originalPropFullFilePathName) {
        
    }

}
