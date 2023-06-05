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

package com.zcomapproach.garden.peony.kernel.rest;

import com.zcomapproach.garden.exception.GardenWebAuthenticationFailed;
import com.zcomapproach.garden.exception.GardenWebServiceException;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import com.zcomapproach.garden.rest.GardenWebResoureRoot;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.openide.util.Exceptions;

/**
 *
 * @author zhijun98
 */
public class PeonyRestClient {

    private static final Logger logger = Logger.getLogger(PeonyRestClient.class.getName());
    
    //private static final String BASE_URI = "http://localhost:8080/RoseWeb/rest";
    private static final String BASE_URI = "https://www.zcomapproach.com/RoseWeb/rest";
    private static final String BASE_DEV_URI = "http://localhost:8080/RoseWeb/rest";
    
    private static int debugMsg = 0;
    
    private final GardenWebResoureRoot peonyRestRoot;
    
    public PeonyRestClient(GardenWebResoureRoot peonyRestRoot) {
        this.peonyRestRoot = peonyRestRoot;
    }
    
    private Client getWebClient(){
        Client webClient;
        try{
            if (PeonyProperties.getSingleton().isDevelopmentMode()){
                webClient = javax.ws.rs.client.ClientBuilder.newClient();
                if ((debugMsg++) < 8){  
                    logger.log(Level.INFO, ">>> Peony Offlice is running in the debug mode....");
                }
            }else{
                webClient = javax.ws.rs.client.ClientBuilder.newBuilder().sslContext(getSSLContext()).build();
            }
        }catch (Throwable ex){
            Logger.getLogger(PeonyRestClient.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            webClient = null;
        }
        return webClient;
    }
    
    private WebTarget getWebTarget(Client webClient){
        WebTarget webTarget;
        try{
            if (PeonyProperties.getSingleton().isDevelopmentMode()){
                webTarget = webClient.target(BASE_DEV_URI).path(peonyRestRoot.value());
            }else{
                webTarget = webClient.target(BASE_URI).path(peonyRestRoot.value());
            }
        }catch (Throwable ex){
            Logger.getLogger(PeonyRestClient.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            webTarget = null;
        }
        return webTarget;
    }

    public <T> T deleteEntity_XML(Class<T> responseType, String restParams) throws Exception {
        T result = null;
        Response response = null;
        Client webClient = getWebClient();
        if (webClient != null){
            try{
                WebTarget resource = getWebTarget(webClient);
                resource = resource.path(restParams);
                response = resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).delete();
                if (response.getStatus() == Response.Status.OK.getStatusCode()){
                    result = response.readEntity(responseType);
                }else if ((response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) 
                        || (response.getStatus() == Response.Status.NOT_ACCEPTABLE.getStatusCode()))
                {
                    throw new GardenWebAuthenticationFailed(response.readEntity(String.class));
                }else{
                    throw new GardenWebServiceException(response.readEntity(String.class));
                }
            }catch (GardenWebServiceException ex){  //possibly GardenWebAuthenticationFailed
                closeResponse(response);
                throw ex;
            }catch (Throwable ex){
                closeResponse(response);
                throw new Exception(ex);
            }finally{
                closeResponse(response);
            }
            webClient.close();
        }
        return result;
    }

    /**
     * This helps pass object data to the server-side
     * @param <T>
     * @param responseType
     * @param restParams
     * @param entityObject
     * @return
     * @throws Exception 
     * 
     */
    private <T> T putEntity_XML(Class<T> responseType, String restParams, Object entityObject) throws Exception {
        T result = null;
        Response response = null;
        Client webClient = getWebClient();
        
        if (webClient != null){
            try{
                WebTarget resource = getWebTarget(webClient);
                resource = resource.path(restParams);
                response = resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).put(Entity.entity(entityObject, MediaType.APPLICATION_XML));
                if (response.getStatus() == Response.Status.OK.getStatusCode()){
                    result = response.readEntity(responseType);
                }else if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode())
                {
                    PeonyFaceUtils.publishMessageOntoOutputWindow("REST-WEB ERROR: " + Response.Status.NOT_FOUND.getStatusCode());
                    throw new GardenWebAuthenticationFailed(response.readEntity(String.class));
                }else if (response.getStatus() == Response.Status.NOT_ACCEPTABLE.getStatusCode())
                {
                    PeonyFaceUtils.publishMessageOntoOutputWindow("REST-WEB ERROR: " + Response.Status.NOT_ACCEPTABLE.getStatusCode());
                    throw new GardenWebAuthenticationFailed(response.readEntity(String.class));
                }else{
                    PeonyFaceUtils.publishMessageOntoOutputWindow("REST-WEB ERROR: " + response.getStatus());
                    throw new GardenWebServiceException(response.readEntity(String.class));
                }
            }catch (GardenWebServiceException ex){  //possibly GardenWebAuthenticationFailed
                closeResponse(response);
                throw ex;
            }catch (Throwable ex){
                closeResponse(response);
                throw new Exception(ex);
            }finally{
                closeResponse(response);
            }
            webClient.close();
        }
        
        return result;
    }
    
    public <T> T storeEntity_XML(Class<T> responseType, String restParams, Object entityObject) throws Exception {
        return putEntity_XML(responseType, restParams, entityObject);
    }
    
    public <T> T retrieveEntity_XML(Class<T> responseType, String restParams, Object entityObject) throws Exception {
        return putEntity_XML(responseType, restParams, entityObject);
    }
    
    public <T> T findEntity_XML(Class<T> responseType, String restParams) throws Exception {
        T result = null;
        Response response = null;
        Client webClient = getWebClient();
        if (webClient != null){
            try{
                WebTarget resource = getWebTarget(webClient);
                resource = resource.path(restParams);
                response = resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get();
                if (response.getStatus() == Response.Status.OK.getStatusCode()){
                    result = response.readEntity(responseType);
                }else if ((response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) 
                        || (response.getStatus() == Response.Status.NOT_ACCEPTABLE.getStatusCode()))
                {
                    throw new GardenWebAuthenticationFailed(response.readEntity(String.class));
                }else{
                    throw new GardenWebServiceException(response.readEntity(String.class));
                }
            }catch (GardenWebServiceException ex){  //possibly GardenWebAuthenticationFailed
                closeResponse(response);
                throw ex;
            }catch (Throwable ex){
                closeResponse(response);
                throw new Exception(ex);
            }finally{
                closeResponse(response);
            }
            webClient.close();
        }
        return result;
    }
    
    public boolean requestOperation_XML(String restParams) throws Exception{
        boolean result = false;
        Response response = null;
        Client webClient = getWebClient();
        if (webClient != null){
            try{
                WebTarget resource = getWebTarget(webClient);
                resource = resource.path(restParams);
                response = resource.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get();
                if (response.getStatus() == Response.Status.OK.getStatusCode()){
                    result = true;
                }else if ((response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) 
                        || (response.getStatus() == Response.Status.NOT_ACCEPTABLE.getStatusCode()))
                {
                    throw new GardenWebAuthenticationFailed(response.readEntity(String.class));
                }else{
                    throw new GardenWebServiceException(response.readEntity(String.class));
                }
            }catch (GardenWebServiceException ex){  //possibly GardenWebAuthenticationFailed
                closeResponse(response);
                throw ex;
            }catch (Throwable ex){
                closeResponse(response);
                throw new Exception(ex);
            }finally{
                closeResponse(response);
            }
            webClient.close();
        }
        return result;
    }
    
    private void closeResponse(Response response){
        if (response != null){
            response.close();
        }
    }

    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
                return true;
            }
        };
    }

    private SSLContext getSSLContext() {
        // for alternative implementation checkout org.glassfish.jersey.SslConfigurator
        javax.net.ssl.TrustManager x509 = new javax.net.ssl.X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {
                //return;
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {
                //return;
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("SSL");
            ctx.init(null, new javax.net.ssl.TrustManager[]{x509}, null);
        } catch (java.security.GeneralSecurityException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ctx;
    }
    
}
