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

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

/**
 *
 * @author zhijun98
 */
@Named(value = "roseEmailValidator")
@RequestScoped
public class RoseEmailValidator implements Validator{

    private static final String EMAIL_REGEX = ".+@.+\\.[a-z]+"; //loose check
    
    /**
     * This method does not validate Null-Empty.
     * @param emailAddress
     * @return  - empty or NULL emailAddress will return true but not false
     */
    public static boolean validateEmail(String emailAddress){
        // Let required="true" do its job.
        if (ZcaValidator.isNullEmpty(emailAddress)){
            return true;
        }
        
        Pattern mask = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = mask.matcher(emailAddress.trim());    //TRIM is imporant

        return matcher.matches();
    }
    
    /**
     * Creates a new instance of RoseEmailValidator
     */
    public RoseEmailValidator() {
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String emailAddress = (String) value;
        if (!validateEmail(emailAddress)) {
            String msg = RoseText.getText("BadEmailPattern_T");
            throw new ValidatorException(RoseJsfUtils.createFacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
        }
    }

}
