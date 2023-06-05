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
package com.zcomapproach.garden.rose.filter;

import com.zcomapproach.garden.rose.RosePageName;
import com.zcomapproach.garden.rose.bean.RoseUserSessionBean;
import com.zcomapproach.garden.rose.util.RoseWebUtils;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Services zone is for customers
 * @author zhijun98
 */
@WebFilter(filterName = "RoseCustomerFilter", urlPatterns = {"/faces/customer/*"})
public class RoseCustomerFilter extends AbstractRosePrivateFilter {

    @Override
    protected void doFilterImpl(RoseUserSessionBean roseUserSessionBean, HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException{
        if (roseUserSessionBean.isValidAuthenticatedStatus()){
            chain.doFilter(request, response);
        }else{
            //not authenticated yet...
            response.sendRedirect(RoseWebUtils.getWebPagePathUnderRoot(request, RosePageName.LoginPage));
        }
    }

}
