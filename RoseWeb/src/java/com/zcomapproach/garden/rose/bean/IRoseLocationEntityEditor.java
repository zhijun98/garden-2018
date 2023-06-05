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

package com.zcomapproach.garden.rose.bean;

/**
 * Refer to roseLocationEntityPanel.xhtml
 * @author zhijun98
 */
public interface IRoseLocationEntityEditor{ 
    
    /**
     * Delete a location record from the database, which potentially make web-GUI updated
     * @param locationUuid 
     */
    public void deleteLocationDataEntry(String locationUuid);
    
    /**
     * Prepare a new data entry i.e. G01Location for Web-GUI which would be updated correspondingly
     */
    public void addNewLocationDataEntry();

}
