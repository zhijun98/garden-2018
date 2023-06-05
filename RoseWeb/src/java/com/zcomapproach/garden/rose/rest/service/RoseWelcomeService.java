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

import com.zcomapproach.garden.persistence.entity.G02Log;
import com.zcomapproach.garden.persistence.peony.PeonyLogList;
import com.zcomapproach.garden.persistence.peony.data.PeonySystemSettings;
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
@Path("welcome")    //refer to RoseWebResoureRoot.value()
public class RoseWelcomeService extends AbstractRestService{
    
    @GET
    @Path("verifyAccount/{accountName}/{password}/{licenseKey}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response verifyAccount(@PathParam("accountName") String accountName, @PathParam("password") String password, @PathParam("licenseKey") String licenseKey) {
        return super.verifyCredentials(accountName, password, licenseKey);
    }
    
    @GET
    @Path("retrievePeonySystemSettings/{flowerName}/{flowerOwner}/{accountName}/{password}/{licenseKey}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response retrievePeonySystemSettings(@PathParam("flowerName") String flowerName, 
                                                    @PathParam("flowerOwner") String flowerOwner,
                                                    @PathParam("accountName") String accountName, 
                                                    @PathParam("password") String password,
                                                    @PathParam("licenseKey") String licenseKey)
    {
        Response result = super.verifyFlowerCredentials(flowerName, flowerOwner, licenseKey);
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getRuntimeEJB().findPeonySystemSettings(accountName, password)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storePeonySystemSettings/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePeonySystemSettings(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    PeonySystemSettings aPeonySystemSettings)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getRuntimeEJB().storePeonySystemSettings(aPeonySystemSettings)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storePeonyLogList/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePeonyLogList(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    PeonyLogList aPeonyLogList)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getRuntimeEJB().storePeonyLogList(aPeonyLogList)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storeLogEntity/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storeLogEntity(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    G02Log log)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getRuntimeEJB().storeLogEntity(log)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
}
