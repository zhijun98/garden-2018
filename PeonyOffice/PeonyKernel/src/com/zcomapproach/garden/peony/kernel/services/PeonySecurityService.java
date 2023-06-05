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

package com.zcomapproach.garden.peony.kernel.services;

import com.zcomapproach.garden.peony.kernel.rest.PeonyRestClient;
import com.zcomapproach.garden.persistence.entity.G02Log;

/**
 *
 * @author zhijun98
 */
public interface PeonySecurityService extends PeonyService {
    
    /**
     * When Peony display welcome top component, this method is invoked to display dialog. 
     */
    public void displayLoginDialog();
    
    /**
     * Login: authenticate the current user with its authorized services, e.g. web, email, chat, etc. If login failed, 
     * exception will be raised for reasons. Otherwise, it will be quietly completed. The caller may listen to the login 
     * status and result.
     * 
     * @param userName
     * @param password
     * @return 
     * @throws java.lang.Exception - some technical exception, e.g. ConnectionException
     */
    public boolean login(String userName, String password) throws Exception;
    
    /**
     * Logout: this is NOT traditional "logout". Instead, this is equivalent to "lock the application for current login user".
     * 
     * @throws Exception 
     */
    public void logout() throws Exception;
    
    /**
     * logout and cutoff web client connection 
     * 
     * @throws Exception 
     */
    public void terminate() throws Exception;
    
    /**
     * A web-rest client for welcome
     * @return 
     */
    public PeonyRestClient getPeonySecurityRestClient();
    
    /**
     * Suggest to use PeonyLocalSettings::log() methods which uses its own threading 
     * mechanisms. This method is time-consuming without threading mechanism
     * 
     * @param log 
     */
    public void log(G02Log log);
    
}
