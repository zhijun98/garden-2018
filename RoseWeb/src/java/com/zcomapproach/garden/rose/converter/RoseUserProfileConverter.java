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

package com.zcomapproach.garden.rose.converter;

import com.zcomapproach.garden.rose.data.profile.RoseUserProfile;
import java.util.HashMap;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author zhijun98
 */
@FacesConverter(value = "roseUserProfileConverter")
public class RoseUserProfileConverter implements Converter {

    private static final HashMap<String, RoseUserProfile> profileStorage = new HashMap<>();

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent component, String userUuid) {
        return profileStorage.get(userUuid);
    } 
    
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object roseUserProfileObj) {
        //use as itemValue
        if (roseUserProfileObj instanceof RoseUserProfile){
            RoseUserProfile aRoseUserProfile = (RoseUserProfile)roseUserProfileObj;
            profileStorage.put(aRoseUserProfile.getUserEntity().getUserUuid(), aRoseUserProfile);
            return aRoseUserProfile.getUserEntity().getUserUuid();
        }else{
            return null;
        }
    }

}
