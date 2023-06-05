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

package com.zcomapproach.garden.rose.data;

/**
 * Support dynamically NavLink generation
 * @author zhijun98
 */
public class BootsFacesNavLink {
    
    private String linkTextValue;
    private String linkHref;

    public BootsFacesNavLink() {
    }

    public BootsFacesNavLink(String linkTextValue, String linkHref) {
        this.linkTextValue = linkTextValue;
        this.linkHref = linkHref;
    }

    public String getLinkTextValue() {
        return linkTextValue;
    }

    public void setLinkTextValue(String linkTextValue) {
        this.linkTextValue = linkTextValue;
    }

    public String getLinkHref() {
        return linkHref;
    }

    public void setLinkHref(String linkHref) {
        this.linkHref = linkHref;
    }

}
