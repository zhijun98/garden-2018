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
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @deprecated - removed
 * @author zhijun98
 */
//@WebFilter(filterName = "RosePublicFilter", urlPatterns = {"/*"})
public class RosePublicFilter implements Filter {
    
    private static final boolean debug = false;

    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 
    private FilterConfig filterConfig = null;
    
    public RosePublicFilter() {
    }    
    
    private void doBeforeProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("RosePublicFilter:DoBeforeProcessing");
        }

        // Write code here to process the request and/or response before
        // the rest of the filter chain is invoked.
        // For example, a logging filter might log items on the request object,
        // such as the parameters.
        /*
	for (Enumeration en = request.getParameterNames(); en.hasMoreElements(); ) {
	    String name = (String)en.nextElement();
	    String values[] = request.getParameterValues(name);
	    int n = values.length;
	    StringBuffer buf = new StringBuffer();
	    buf.append(name);
	    buf.append("=");
	    for(int i=0; i < n; i++) {
	        buf.append(values[i]);
	        if (i < n-1)
	            buf.append(",");
	    }
	    log(buf.toString());
	}
         */
    }    
    
    private void doAfterProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("RosePublicFilter:DoAfterProcessing");
        }

        // Write code here to process the request and/or response after
        // the rest of the filter chain is invoked.
        // For example, a logging filter might log the attributes on the
        // request object after the request has been processed. 
        /*
	for (Enumeration en = request.getAttributeNames(); en.hasMoreElements(); ) {
	    String name = (String)en.nextElement();
	    Object value = request.getAttribute(name);
	    log("attribute: " + name + "=" + value.toString());

	}
         */
        // For example, a filter might append something to the response.
        /*
	PrintWriter respOut = new PrintWriter(response.getWriter());
	respOut.println("<P><B>This has been appended by an intrusive filter.</B>");
         */
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        
        if (debug) {
            log("RosePublicFilter:doFilter()");
        }
        
        doBeforeProcessing(request, response);
        
        Throwable problem = null;
        try {
            HttpServletRequest req =(HttpServletRequest)request;
            HttpServletResponse res = (HttpServletResponse)response;
            //check authentication of current user
            HttpSession session = req.getSession(true);

            Object obj = session.getAttribute(RoseUserSessionBean.class.getName());
            if (obj instanceof RoseUserSessionBean){
                RoseUserSessionBean userSession = (RoseUserSessionBean)obj;
                if (userSession.isValidAuthenticatedStatus()){
                    //filter out some pages which are not necessary for logged-in users
                    doFilterForValidAuthenticated(req, res, chain);
                }else if (userSession.isSuspendedAuthenticatedStatus()){
                    if ((req.getRequestURL().toString()).endsWith(RosePageName.AccountConfirmationPage.name()+ RoseWebUtils.JSF_EXT)){
                        chain.doFilter(req, res);
                    }else{
                        res.sendRedirect(RoseWebUtils.getWebPagePathUnderRoot(req, RosePageName.AccountConfirmationPage));
                    }
                }else{
                    doFilterForNotAuthenticated(req, res, chain);
                }
            }else{
                doFilterForNotAuthenticated(req, res, chain);
            }
        } catch (Throwable t) {
            // If an exception is thrown somewhere down the filter chain,
            // we still want to execute our after processing, and then
            // rethrow the problem after that.
            problem = t;
            t.printStackTrace();
        }
        
        doAfterProcessing(request, response);

        // If there was a problem, we want to rethrow it if it is
        // a known type, otherwise log it.
        if (problem != null) {
            if (problem instanceof ServletException) {
                throw (ServletException) problem;
            }
            if (problem instanceof IOException) {
                throw (IOException) problem;
            }
            sendProcessingError(problem, response);
        }
    }

    private void doFilterForValidAuthenticated(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException{
        boolean redirectToHome = false;
        if ((request.getRequestURL().toString()).endsWith(RosePageName.AccountConfirmationPage.name()+ RoseWebUtils.JSF_EXT)
                || (request.getRequestURL().toString()).endsWith(RosePageName.RedeemCredentialsPage.name()+ RoseWebUtils.JSF_EXT)
                || (request.getRequestURL().toString()).endsWith(RosePageName.LoginPage.name()+ RoseWebUtils.JSF_EXT)
                || (request.getRequestURL().toString()).endsWith(RosePageName.RegisterPage.name()+ RoseWebUtils.JSF_EXT))
        {
            redirectToHome = true;
        
        }
        if (redirectToHome){
            response.sendRedirect(RoseWebUtils.getWebPagePathUnderRoot(request, RosePageName.WelcomePage));
        }else{
            chain.doFilter(request, response);
        }
    }

    private void doFilterForNotAuthenticated(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException{
        if ((request.getRequestURL().toString()).endsWith(RosePageName.AccountConfirmationPage.name()+ RoseWebUtils.JSF_EXT)){
            response.sendRedirect(RoseWebUtils.getWebPagePathUnderRoot(request, RosePageName.WelcomePage));
        }else{
            chain.doFilter(request, response);
        }
    }

    /**
     * Return the filter configuration object for this filter.
     * @return 
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    @Override
    public void destroy() {        
    }

    /**
     * Init method for this filter
     */
    public void init(FilterConfig filterConfig) {        
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (debug) {                
                log("RosePublicFilter:Initializing filter");
            }
        }
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("RosePublicFilter()");
        }
        StringBuffer sb = new StringBuffer("RosePublicFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }
    
    private void sendProcessingError(Throwable t, ServletResponse response) {
        String stackTrace = getStackTrace(t);        
        
        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);                
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");                
                pw.print(stackTrace);                
                pw.print("</pre></body>\n</html>"); //NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        }
    }
    
    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }
    
    public void log(String msg) {
        filterConfig.getServletContext().log(msg);        
    }
    
}
