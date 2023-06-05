/*
 * Copyright 2017 ZComApproach Inc.
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

import com.zcomapproach.garden.persistence.entity.G01Log;
import java.util.HashMap;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author zhijun98
 */
@FacesConverter(value = "gardenLogEntityConverter")
public class GardenLogEntityConverter implements Converter {

    private static final HashMap<String, G01Log> logStorage = new HashMap<>();

    @Override
    public Object getAsObject(FacesContext ctx, UIComponent component, String logUuid) {
        return logStorage.get(logUuid);
    } 
    
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object logObj) {
        //use as <option value=""/>
        if (logObj instanceof G01Log){
            
            G01Log aGardenWorkStatus = (G01Log)logObj;
            logStorage.put(aGardenWorkStatus.getLogUuid(), aGardenWorkStatus);
            
            return aGardenWorkStatus.getLogUuid();
        }else{
            return null;
        }
    }

}
