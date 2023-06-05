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

package com.zcomapproach.garden.rose.validator;

import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.commons.ZcaRegex;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

/**
 *
 * cooperate with RoseUsCurrencyConverter
 * @author zhijun98
 */
@Named(value = "roseUsCurrencyValidator")
@RequestScoped
public class RoseUsCurrencyValidator implements Validator{

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if ((value == null) || (value.toString().trim().equalsIgnoreCase(""))){
            return;
        }
        if (!ZcaRegex.isUsCurrencyString(value.toString())){
            throw new ValidatorException(RoseJsfUtils.createFacesErrorMessage("US Currency Format: $123.45 or 123.45"));
        }
    }

}
