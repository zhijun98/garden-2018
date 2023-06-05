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

package com.zcomapproach.garden.rose.scheduler;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Startup;
import javax.ejb.Singleton;

/**
 *
 * @author zhijun98
 */
@Startup
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class GardenCacheNonPersistentSchedulerLocker {

    private boolean taxpayerCaseSearchCacheLocked = false;
    private boolean previousYearTaxpayerCaseSearchCache = false;

    @PostConstruct
    public void postConstruct() {
        
    }

    private synchronized boolean isTaxpayerCaseSearchCacheLocked() {
        return taxpayerCaseSearchCacheLocked;
    }

    private synchronized void setTaxpayerCaseSearchCacheLocked(boolean taxpayerCaseSearchCacheLocked) {
        this.taxpayerCaseSearchCacheLocked = taxpayerCaseSearchCacheLocked;
    }

    private synchronized boolean isPreviousYearTaxpayerCaseSearchCache() {
        return previousYearTaxpayerCaseSearchCache;
    }

    private synchronized void setPreviousYearTaxpayerCaseSearchCache(boolean previousYearTaxpayerCaseSearchCache) {
        this.previousYearTaxpayerCaseSearchCache = previousYearTaxpayerCaseSearchCache;
    }
    
    public synchronized boolean lockTaxpayerCaseSearchCache(){
        if (isTaxpayerCaseSearchCacheLocked()){
            return false;
        }
        setTaxpayerCaseSearchCacheLocked(true);
        return true;
    }
    
    public synchronized void unlockTaxpayerCaseSearchCache(){
        setTaxpayerCaseSearchCacheLocked(false);
    }

    public synchronized boolean lockPreviousYearTaxpayerCaseSearchCache() {
        if (isPreviousYearTaxpayerCaseSearchCache()){
            return false;
        }
        setPreviousYearTaxpayerCaseSearchCache(true);
        return true;
    }

    public synchronized void unlockPreviousYearTaxpayerCaseSearchCache() {
        setPreviousYearTaxpayerCaseSearchCache(false);
    }
    
}
