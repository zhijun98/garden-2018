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

package com.zcomapproach.garden.rose.data.profile;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author zhijun98
 */
public abstract class AbstractRoseProfile implements Serializable{
    
    public abstract String getProfileName();
    
    public abstract String getProfileDescriptiveName();
    
    /**
     * This Uuid is used in hashCode() and equals()
     * @return 
     */
    protected abstract String getProfileUuid();


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(getProfileUuid());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractRoseEntityProfile other = (AbstractRoseEntityProfile) obj;
        return Objects.equals(this.getProfileUuid(), other.getProfileUuid());
    }
}
