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

package com.zcomapproach.garden.rose.util;

import com.zcomapproach.garden.persistence.entity.G01ContactInfo;
import com.zcomapproach.garden.rose.RosePageName;
import com.zcomapproach.garden.rose.bean.RoseUserSessionBean;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.commons.ZcaCalendar;
import com.zcomapproach.commons.ZcaValidator;
import java.util.Date;
import java.util.Locale;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Utils only used in JSF context
 * @author zhijun98
 */
public class RoseJsfUtils {
    
    public static void main(String[] args){
//        //System.out.println(Math.floorDiv(7*(-1), 3));
//        try{
//            int x = 10;
//            int y = 0;
//        
//            int z = x/y;
//        }catch(Exception ex){
//            StringWriter errors = new StringWriter();
//            ex.printStackTrace(new PrintWriter(errors));
//            System.out.print(errors.toString());
//        }
        System.out.println(G01ContactInfo.class.getSimpleName());
    }
    
    public static void setGlobalFailedOperationMessage(String reason) {
        if (ZcaValidator.isNullEmpty(reason)){
            setGlobalFatalFacesMessage(RoseText.getText("OperationFailed_T") + ": " 
                + ZcaCalendar.convertToMMddyyyyHHmmss(new Date(), "-", " @", ":"));
        }else{
            setGlobalFatalFacesMessage(RoseText.getText("OperationFailed_T") + ": " + reason + " (" 
                + ZcaCalendar.convertToMMddyyyyHHmmss(new Date(), "-", " @", ":") + ")");
        }
    }
    
    public static void setGlobalSuccessfulOperationMessage() {
        setGlobalInfoFacesMessage(RoseText.getText("OperationSucceeded_T") + ": " 
                + ZcaCalendar.convertToMMddyyyyHHmmss(new Date(), "-", " @", ":"));
    }

    /**
     * System error: rarely happened error possibly because of dirty data, implementation logical error, etc.  
     * @param message 
     */
    public static void setGlobalSystemErrorFacesMessage(String message) {
        setGlobalFatalFacesMessage(RoseText.getText("SystemError")+ ":" + message + " (" 
                + ZcaCalendar.convertToMMddyyyyHHmmss(new Date(), "-", " @", ":") + ")");
    }
    
    /**
     * Technical error: technically some case can't ever happen but it happened because of implementation logical error.  
     * @param message 
     */
    public static void setGlobalTechnicalErrorFacesMessage(String message) {
        setGlobalFatalFacesMessage(RoseText.getText("TechnicalError")+ ":" + message + " (" 
                + ZcaCalendar.convertToMMddyyyyHHmmss(new Date(), "-", " @", ":") + ")");
    }

    /**
     * Set a message for h:messages that has globalOnly="true"
     * @param message 
     */
    public static void setGlobalFatalFacesMessage(String message) {
        setGlobalFacesMessage(FacesMessage.SEVERITY_FATAL, message);
    }
    
    public static void setGlobalErrorFacesMessage(String message) {
        setGlobalFacesMessage(FacesMessage.SEVERITY_ERROR, message);
    }
    
    public static void setGlobalWarningFacesMessage(String message) {
        setGlobalFacesMessage(FacesMessage.SEVERITY_WARN, message);
    }
    
    public static void setGlobalInfoFacesMessage(String message) {
        setGlobalFacesMessage(FacesMessage.SEVERITY_INFO, message);
    }
    
    private static void setGlobalFacesMessage(FacesMessage.Severity severity, String message) {
        FacesContext.getCurrentInstance().addMessage(null, createFacesMessage(severity, message, message));
    }
    
    public static void setGlobalFacesMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, createFacesMessage(severity, summary, detail));
    }
    
    public static FacesMessage createFacesMessage(FacesMessage.Severity severity, String summary, String detail) {
        if (severity == null){
            severity = FacesMessage.SEVERITY_INFO;
        }
        if (ZcaValidator.isNullEmpty(summary)){
            detail = "No summary information.";
        }
        if (ZcaValidator.isNullEmpty(detail)){
            detail = "No detailed information provided.";
        }
        FacesMessage facesMessage = new FacesMessage();
        facesMessage.setDetail(detail);
        facesMessage.setSummary(summary);
        facesMessage.setSeverity(severity);
        return facesMessage;
    }

    public static FacesMessage createFacesMessage(String msg, FacesMessage.Severity severity) {
        FacesMessage facesMessage = new FacesMessage();
        facesMessage.setDetail(msg);
        facesMessage.setSummary(msg);
        facesMessage.setSeverity(severity);
        return facesMessage;
    }

    public static FacesMessage createFacesErrorMessage(String msg) {
        return createFacesMessage(msg, FacesMessage.SEVERITY_ERROR);
    }
 
    /**
     * This method is hard-coded for production and development modes because of Amazon EC2 environemnt
     * @return the web root path which ends with "/". e.g. "http://localhost:8080/Rose/faces/"
     */
    public static String getRootWebPath(){
        //todo zzj: consider not to hard-code it.
        if (isProduction()){
            return "https://www.zcomapproach.com/RoseWeb/faces/";
        }else{
            return "http://localhost:8080/RoseWeb/faces/";
        }
    }

    public static String getWebPagePathUnderRoot(RosePageName page) {
        return getRootWebPath() + page.name() + RoseWebUtils.JSF_EXT;
    }
    
    public static Locale getCurrentLocale(){
        return FacesContext.getCurrentInstance().getViewRoot().getLocale();
    }
    
    /**
     * 
     * @param localeCode - used for the web presentation for ths current user session
     */
    public static void setLocaleCode(String localeCode) {
        if (ZcaValidator.isNotNullEmpty(localeCode)){
            if (!localeCode.equalsIgnoreCase(FacesContext.getCurrentInstance().getViewRoot().getLocale().toLanguageTag())){
                FacesContext.getCurrentInstance().getViewRoot().setLocale(Locale.forLanguageTag(localeCode));
            }
        }
    }
    
    /**
     * 
     * @param name - the name of the cookie
     * @param value - the value of the cookie
     * @param expiry - an integer specifying the maximum age of the cookie in seconds; if negative, means the cookie is 
     * not stored; if zero, deletes the cookie
     */
    public static void setCookie(String name, String value, int expiry) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        Cookie cookie = null;

        Cookie[] userCookies = request.getCookies();
        if (userCookies != null && userCookies.length > 0 ) {
            for (Cookie userCookie : userCookies) {
                if (userCookie.getName().equals(name)) {
                    cookie = userCookie;
                    break;
                }
            }
        }

        if (cookie != null) {
            cookie.setValue(value);
        } else {
            cookie = new Cookie(name, value);
            cookie.setPath(request.getContextPath());
        }

        cookie.setMaxAge(expiry);

        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        response.addCookie(cookie);
    }

    /**
     * Cookie's expiration is defaulted to be 7 days: zzj todo: consider move it into RoseSettingsProfile
     * @param name
     * @param value 
     */
    public static void setCookie(String name, String value) {
        setCookie(name, value, 7*24*60*60);
    }

    /**
     * 
     * @param name
     * @return 
     */
    public static Cookie getCookie(String name) {

        FacesContext facesContext = FacesContext.getCurrentInstance();

        Cookie cookie = null;

        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        if (request != null){
            Cookie[] userCookies = request.getCookies();
            if (userCookies != null && userCookies.length > 0 ) {
                for (Cookie userCookie : userCookies) {
                    if (userCookie.getName().equals(name)) {
                        cookie = userCookie;
                        return cookie;
                    }
                }
            }
        }
        return null;
    }

    public static boolean isProduction(){
        return "Production".equalsIgnoreCase(FacesContext.getCurrentInstance().getApplication().getProjectStage().toString());
    }
    
    private static HttpSession getCurrentHttpSession(){
        HttpServletRequest request = (HttpServletRequest)(FacesContext.getCurrentInstance().getExternalContext().getRequest());
        return request.getSession(false);
    }

    public static void storeUserSessionForRoseFilters(RoseUserSessionBean currentUserSessionBean) {
        getCurrentHttpSession().setAttribute(RoseUserSessionBean.class.getName(), currentUserSessionBean);
    }

    public static void deleteUserSessionForRoseFilters() {
        getCurrentHttpSession().removeAttribute(RoseUserSessionBean.class.getName());
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }

    public static String getCurrentWebPageName() {
        return FacesContext.getCurrentInstance().getViewRoot().getViewId();
    }
}
