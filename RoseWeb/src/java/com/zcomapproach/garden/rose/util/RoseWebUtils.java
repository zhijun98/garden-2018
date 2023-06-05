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

import com.zcomapproach.garden.rose.RosePageName;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;

/**
 * Utils for common cases in the web, but not demands JSF context
 * @author zhijun98
 */
public class RoseWebUtils {
    
    public static final String JSF_EXT = ".xhtml";
    public static final String CUSTOMER_FOLDER = "customer";
    public static final String BUSINESS_FOLDER = "business";
    public static final String SERVICES_FOLDER = "services";
    
    public static void main(String[] args){
//        System.out.println(GardenPreference.convertIndexToType(0).value());
//        System.out.println(GardenPreference.convertIndexToType(1).value());
//        System.out.println(GardenPreference.convertIndexToType(2).value());
//        System.out.println(GardenPreference.convertIndexToType(3).value());
//        System.out.println(GardenPreference.convertIndexToType(4).value());
//        System.out.println(GardenPreference.convertIndexToType(5).value());
//        System.out.println(GardenPreference.convertIndexToType(6).value());
//        System.out.println(GardenPreference.convertIndexToType(7).value());
//        System.out.println(GardenPreference.convertIndexToType(8).value());
    }
    
    /**
     * 
     * @param page
     * @return - e.g. WelcomePage.xhtml?faces-redirect=true
     */
    public static String redirectToRosePage(RosePageName page){
        return page.name() + RoseWebUtils.constructWebQueryString(null, true);
    }
    
    /**
     * 
     * @param page
     * @param redirected - if true, return e.g. WelcomePage.xhtml?faces-redirect=true; if false, return e.g. WelcomePage.xhtml
     * @return
     */
    public static String redirectToRosePage(RosePageName page, boolean redirected){
        return page.name() + RoseWebUtils.constructWebQueryString(null, redirected);
    }
    
    /**
     * 
     * @param params - key, value (if null, it will be ignored)
     * @param redirected
     * @return
     */
    public static String constructWebQueryString(HashMap<String, String> params, boolean redirected){
        String result = "";
        
        if (params != null){
            for (String key : params.keySet()){
                result += constructWebParams(key, params.get(key));
            }
        }
        
        if (redirected){
            result += "&" + getJsfRedirectParamPair();
        }
        
        if (result.length() > 0){
            result = "?" + result.substring(1);//get rid of the leading "&"
        }
        
        return result;
    }
    
    private static String constructWebParams(String key, String value){
        return "&" + key + "=" + value;
    }
    
    private static String getJsfRedirectParamPair(){
        return "faces-redirect=true";
    }
    
    /**
     * This method In Amazone-EC-2 environment, this request always returns "localhost"-like path. If the request came from 
     * Filter or Servlet, it retains the public domain name. This method works well outside of JSF-context.
     * 
     * @param request
     * @param page
     * @return 
     */
    public static String getWebPagePathUnderRoot(HttpServletRequest request, RosePageName page){
        return getRoseRootPath(request) + page.name() + RoseWebUtils.JSF_EXT;
    }
    
    /**
     * 
     * @param request
     * @return -  e.g. "https://www.zcomapproach.com/Rose/faces/"
     */
    public static String getRoseRootPath(HttpServletRequest request){
        return request.getScheme()                  //"http"
                + "://" + request.getServerName()   //"localhost" 
                + ":" + request.getServerPort()     //"8080"
                + request.getContextPath()          //"/RoseWeb"
                + request.getServletPath()          //"/faces"
                +"/";
    }

}
