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

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.util.GardenData;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * cooperate with RoseUsCurrencyValidator
 * @author zhijun98
 */
@FacesConverter(value = "roseUsCurrencyConverter")
public class RoseUsCurrencyConverter implements Converter {

    /**
     * 
     * @param context
     * @param component
     * @param value - from Web-GUI
     * @return 
     */
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (ZcaValidator.isNullEmpty(value)){
            return null;
        }
        try{ 
            double data = GardenData.convertToDouble(value.replace("$", "").replace(",", ""));
            if (data > 0){
                return GardenData.convertToUsCurrency(data);
            }else{
                return "-" + GardenData.convertToUsCurrency(Math.abs(data));
            }
        }catch (Exception ex){
            return value;
        }
    }

    /**
     * 
     * @param context
     * @param component
     * @param value - from System
     * @return 
     */
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null){
            return null;
        }
        return value.toString();
    }
}
