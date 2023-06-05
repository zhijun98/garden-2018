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

package com.zcomapproach.garden.rose.rest.service;

import com.zcomapproach.garden.data.constant.SearchUserCriteria;
import com.zcomapproach.garden.persistence.entity.G02TulipNotification;
import com.zcomapproach.garden.rose.rest.AbstractRestService;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author zhijun98
 */
@Stateless
@Path("customer") 
public class RoseCustomerService extends AbstractRestService{
    
    @PUT
    @Path("storeTulipNotification/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storeTulipNotification(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    G02TulipNotification aG02TulipNotification)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getCustomerEJB().storeTulipNotification(aG02TulipNotification)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("retrievePeonyTulipUserByUuid/{accountName}/{password}/{licenseKey}/{tulipUserUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response retrievePeonyTulipUserByUuid(@PathParam("accountName") String accountName, 
                                            @PathParam("password") String password, 
                                            @PathParam("licenseKey") String licenseKey, 
                                            @PathParam("tulipUserUuid") String tulipUserUuid)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getCustomerEJB().retrievePeonyTulipUserByUuid(tulipUserUuid)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseCustomerService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    @GET
    @Path("retrieveSimpleCustomerList/{accountName}/{password}/{licenseKey}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response retrieveSimpleCustomerList(@PathParam("accountName") String accountName, 
                                            @PathParam("password") String password, 
                                            @PathParam("licenseKey") String licenseKey)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getCustomerEJB().retrieveSimpleCustomerList()
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseCustomerService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findCustomerByAccountUuid/{accountName}/{password}/{licenseKey}/{accountUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findCustomerByAccountUuid(@PathParam("accountName") String accountName, 
                                            @PathParam("password") String password, 
                                            @PathParam("licenseKey") String licenseKey, 
                                            @PathParam("accountUuid") String accountUuid)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getCustomerEJB().findCustomerByAccountUuid(accountUuid)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseCustomerService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("searchCustomerSearchResultListByFeature/{accountName}/{password}/{licenseKey}/{customerFeatureName}/{customerFeatureValue}/{exactMatch}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response searchCustomerSearchResultListByFeature(@PathParam("accountName") String accountName, 
                                                                        @PathParam("password") String password, 
                                                                        @PathParam("licenseKey") String licenseKey, 
                                                                        @PathParam("customerFeatureName") String customerFeatureName, 
                                                                        @PathParam("customerFeatureValue") String customerFeatureValue, 
                                                                        @PathParam("exactMatch") boolean exactMatch)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getCustomerEJB().searchCustomerSearchResultListByFeature(
                                    SearchUserCriteria.convertEnumNameToType(customerFeatureName, false).value(), 
                                    customerFeatureValue, exactMatch)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseCustomerService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
}
