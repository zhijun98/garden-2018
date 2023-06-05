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

package com.zcomapproach.garden.peony.business;

import com.zcomapproach.garden.peony.kernel.services.PeonyBusinessService;
import com.zcomapproach.garden.peony.kernel.PeonyServiceProvider;
import com.zcomapproach.garden.peony.kernel.rest.PeonyRestClient;
import com.zcomapproach.garden.rest.GardenWebResoureRoot;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author zhijun98
 */
@ServiceProvider(service = PeonyBusinessService.class)
public class PeonyBusinessServiceProvider extends PeonyServiceProvider implements PeonyBusinessService{

    private final PeonyRestClient peonyBusinessRestClient;

    public PeonyBusinessServiceProvider() {
        this.peonyBusinessRestClient = new PeonyRestClient(GardenWebResoureRoot.BUSINESS);
    }
    
    @Override
    public PeonyRestClient getPeonyBusinessRestClient() {
        return peonyBusinessRestClient;
    }

    @Override
    public void closeService() {
        
    }

}
