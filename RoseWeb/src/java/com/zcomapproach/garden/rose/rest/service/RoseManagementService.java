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

import com.zcomapproach.garden.data.GardenFlower;
import com.zcomapproach.garden.data.GardenFlowerOwner;
import com.zcomapproach.garden.persistence.peony.data.QueryNewOfflineEmailCriteria;
import com.zcomapproach.garden.exception.GardenEntityNotFound;
import com.zcomapproach.garden.persistence.constant.GardenContactType;
import com.zcomapproach.garden.persistence.entity.G02BusinessContactor;
import com.zcomapproach.garden.persistence.entity.G02ContactInfo;
import com.zcomapproach.garden.persistence.entity.G02EmailOperation;
import com.zcomapproach.garden.persistence.entity.G02Employee;
import com.zcomapproach.garden.persistence.entity.G02Location;
import com.zcomapproach.garden.persistence.entity.G02OfflineEmail;
import com.zcomapproach.garden.persistence.entity.G02User;
import com.zcomapproach.garden.persistence.peony.PeonyAccount;
import com.zcomapproach.garden.persistence.peony.PeonyCommAssignment;
import com.zcomapproach.garden.persistence.peony.PeonyCommAssignmentList;
import com.zcomapproach.garden.persistence.peony.PeonyEmailTag;
import com.zcomapproach.garden.persistence.peony.PeonyEmailTagList;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmailList;
import com.zcomapproach.garden.persistence.peony.data.PeonyLogTopic;
import com.zcomapproach.garden.rose.rest.AbstractRestService;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.persistence.entity.G02DailyReport;
import com.zcomapproach.garden.persistence.entity.G02JobAssignment;
import com.zcomapproach.garden.persistence.peony.PeonyJob;
import com.zcomapproach.commons.ZcaRegex;
import java.util.Date;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
@Path("management")    //refer to RoseWebResoureRoot.value()
public class RoseManagementService extends AbstractRestService{
    
    @GET
    @Path("retrievePeonyEmployeeList/{flowerName}/{flowerOwner}/{licenseKey}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response retrievePeonyEmployeeList(@PathParam("flowerName") String flowerName, 
                                            @PathParam("flowerOwner") String flowerOwner,
                                            @PathParam("licenseKey") String licenseKey) 
    {
        GardenFlower.convertEnumNameToType(flowerName);
        GardenFlowerOwner.convertEnumNameToType(flowerOwner);
        if (GardenFlowerOwner.getLicenseKey(
                GardenFlower.convertEnumNameToType(flowerName), 
                GardenFlowerOwner.convertEnumNameToType(flowerOwner)).equalsIgnoreCase(licenseKey))
        {
            try {
                return Response.ok(getManagementEJB().findPeonyEmployeeList()).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                return Response.serverError().entity(ex.getMessage()).build();
            }   
        }else{
            return Response.serverError().entity("Bad license key for authetication and authorization.").build();
        }
    }
    
    @DELETE
    @Path("deleteEmployee/{accountName}/{password}/{licenseKey}/{employeeAccountUuid}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteEmployee(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    @PathParam("employeeAccountUuid") String employeeAccountUuid)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(getManagementEJB().removeEmployee(G02Employee.class, employeeAccountUuid)).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @DELETE
    @Path("deleteContactInfo/{accountName}/{password}/{licenseKey}/{contactInfoUuid}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteContactInfo(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    @PathParam("contactInfoUuid") String contactInfoUuid)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(getManagementEJB().deleteContactInfo(contactInfoUuid)).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storePeonyCommAssignment/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePeonyCommAssignment(@PathParam("accountName") String accountName, 
                                                @PathParam("password") String password, 
                                                @PathParam("licenseKey") String licenseKey, 
                                                PeonyCommAssignment aPeonyCommAssignment)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getManagementEJB().storePeonyCommAssignment(aPeonyCommAssignment)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storePeonyCommAssignmentList/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePeonyCommAssignmentList(@PathParam("accountName") String accountName, 
                                                @PathParam("password") String password, 
                                                @PathParam("licenseKey") String licenseKey, 
                                                PeonyCommAssignmentList aPeonyCommAssignmentList)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getManagementEJB().storePeonyCommAssignmentList(aPeonyCommAssignmentList)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    /**
     * @deprecated - this was used by GoogleEmailBox/GoogleEmailBoxController
     * @param accountName
     * @param password
     * @param licenseKey
     * @param aPeonyOfflineEmailList
     * @return 
     */
    @PUT
    @Path("notifyPeonyOfflineEmailListRetrieved/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response notifyPeonyOfflineEmailListRetrieved(@PathParam("accountName") String accountName, 
                                                        @PathParam("password") String password, 
                                                        @PathParam("licenseKey") String licenseKey, 
                                                        PeonyOfflineEmailList aPeonyOfflineEmailList)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getManagementEJB().notifyPeonyOfflineEmailListRetrieved(aPeonyOfflineEmailList)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storePeonyOfflineEmailList/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePeonyOfflineEmailList(@PathParam("accountName") String accountName, 
                                                @PathParam("password") String password, 
                                                @PathParam("licenseKey") String licenseKey, 
                                                PeonyOfflineEmailList aPeonyOfflineEmailList)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getManagementEJB().storePeonyOfflineEmailList(aPeonyOfflineEmailList)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storeG02EmailOperation/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storeG02EmailOperation(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        G02EmailOperation aG02EmailOperation)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getManagementEJB().storeG02EmailOperation(aG02EmailOperation)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("requestOfflineEmailStatusUpdate/{accountName}/{password}/{licenseKey}/{ownerUserUuid}/{mailboxAddress}/{msgId}/{messageStatus}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response requestOfflineEmailStatusUpdate(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        @PathParam("ownerUserUuid") String ownerUserUuid, 
                                        @PathParam("mailboxAddress") String mailboxAddress, 
                                        @PathParam("msgId") String msgId, 
                                        @PathParam("messageStatus") String messageStatus)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getManagementEJB().requestOfflineEmailStatusUpdate(ownerUserUuid, mailboxAddress, msgId, messageStatus)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("requestOfflineEmailStatusUpdateByOfflineEmailUuid/{accountName}/{password}/{licenseKey}/{offlineEmailUuid}/{messageStatus}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response requestOfflineEmailStatusUpdateByOfflineEmailUuid(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        @PathParam("offlineEmailUuid") String offlineEmailUuid, 
                                        @PathParam("messageStatus") String messageStatus)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getManagementEJB().requestOfflineEmailStatusUpdateByOfflineEmailUuid(offlineEmailUuid, messageStatus)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storeG02OfflineEmail/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storeG02OfflineEmail(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        G02OfflineEmail aG02OfflineEmail)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getManagementEJB().storeG02OfflineEmail(aG02OfflineEmail)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storeG02DailyReport/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storeG02DailyReport(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    G02DailyReport aG02DailyReport)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getManagementEJB().storeG02DailyReport(aG02DailyReport)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storeG02JobAssignment/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storeG02JobAssignment(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    G02JobAssignment aG02JobAssignment)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getManagementEJB().storeG02JobAssignment(aG02JobAssignment)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storePeonyJob/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePeonyJob(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    PeonyJob aPeonyJob)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getManagementEJB().storePeonyJob(aPeonyJob)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storePeonyEmailTag/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePeonyEmailTag(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    PeonyEmailTag aPeonyEmailTag)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getManagementEJB().storePeonyEmailTag(aPeonyEmailTag)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storePeonyEmailTagList/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePeonyEmailTagList(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    PeonyEmailTagList aPeonyEmailTagList)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getManagementEJB().storePeonyEmailTagList(aPeonyEmailTagList)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storePeonyEmployeePrivileges/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePeonyEmployeePrivileges(@PathParam("accountName") String accountName, 
                                                @PathParam("password") String password, 
                                                @PathParam("licenseKey") String licenseKey, 
                                                PeonyEmployee aPeonyEmployee)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getManagementEJB().storePeonyEmployeePrivileges(aPeonyEmployee)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storeG02Employee/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storeG02Employee(@PathParam("accountName") String accountName, 
                                @PathParam("password") String password, 
                                @PathParam("licenseKey") String licenseKey, 
                                G02Employee aG02Employee)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getManagementEJB().storeG02Employee(aG02Employee)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storePeonyEmployee/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePeonyEmployee(@PathParam("accountName") String accountName, 
                                @PathParam("password") String password, 
                                @PathParam("licenseKey") String licenseKey, 
                                PeonyEmployee aPeonyEmployee)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getManagementEJB().storePeonyEmployee(aPeonyEmployee)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storePeonyAccount/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePeonyAccount(@PathParam("accountName") String accountName, 
                                @PathParam("password") String password, 
                                @PathParam("licenseKey") String licenseKey, 
                                PeonyAccount aPeonyAccount)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getManagementEJB().storePeonyAccount(aPeonyAccount)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storeContactInfo/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storeContactInfo(@PathParam("accountName") String accountName, 
                                @PathParam("password") String password, 
                                @PathParam("licenseKey") String licenseKey, 
                                G02ContactInfo aG02ContactInfo)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getManagementEJB().storeContactInfo(aG02ContactInfo)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @DELETE
    @Path("deleteLocation/{accountName}/{password}/{licenseKey}/{locationUuid}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteLocation(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    @PathParam("locationUuid") String locationUuid)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(getManagementEJB().deleteLocation(locationUuid)).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storeLocation/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storeLocation(@PathParam("accountName") String accountName, 
                                @PathParam("password") String password, 
                                @PathParam("licenseKey") String licenseKey, 
                                G02Location aG02Location)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getManagementEJB().storeLocation(aG02Location)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("retrievePeonyOfflineMailbox/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response retrievePeonyOfflineMailbox(@PathParam("accountName") String accountName, 
                                            @PathParam("password") String password, 
                                            @PathParam("licenseKey") String licenseKey, 
                                            QueryNewOfflineEmailCriteria queryNewOfflineEmailCriteria)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getManagementEJB().retrievePeonyOfflineMailbox(queryNewOfflineEmailCriteria)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findPeonyEmployeeList/{accountName}/{password}/{licenseKey}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyEmployeeList(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getManagementEJB().findPeonyEmployeeList()).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }

    @GET
    @Path("findUserByUuid/{accountName}/{password}/{licenseKey}/{userUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findUserByUuid(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        @PathParam("userUuid") String userUuid) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getManagementEJB().findEntityByUuid(G02User.class, userUuid)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }

    @GET
    @Path("findPeonyOfflineEmailByEmailTagUuid/{accountName}/{password}/{licenseKey}/{emailTagUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyOfflineEmailByEmailTagUuid(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        @PathParam("emailTagUuid") String emailTagUuid) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getManagementEJB().findPeonyOfflineEmailByEmailTagUuid(emailTagUuid)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }

    @GET
    @Path("findPeonyOfflineEmailByUuid/{accountName}/{password}/{licenseKey}/{offlineEmailUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyOfflineEmailByUuid(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        @PathParam("offlineEmailUuid") String offlineEmailUuid) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getManagementEJB().findPeonyOfflineEmailByUuid(offlineEmailUuid)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findPeonyOfflineEmailListByEmployeeAccountUuidAndEmailAddress/{accountName}/{password}/{licenseKey}/{employeeAccountUuid}/{employeeEmailAddress}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyOfflineEmailListByEmployeeAccountUuidAndEmailAddress(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        @PathParam("employeeAccountUuid") String employeeAccountUuid, 
                                        @PathParam("employeeEmailAddress") String employeeEmailAddress) 
            throws GardenEntityNotFound 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getManagementEJB().findPeonyOfflineEmailListByEmployeeAccountUuidAndEmailAddress(employeeAccountUuid, employeeEmailAddress)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    /**
     * This method simply returns a list of G02OfflineEmail instances embedded in 
     * PeonyOfflineEmailList. There is no other information such as PeonyEmailTag
     * 
     * @param accountName
     * @param password
     * @param licenseKey
     * @param peonyLogTopicName
     * @param employeeAccountUuid
     * @param fromDateValue - 0 or negative number, the period will be ignored.
     * @param toDateValue - 0 or negative number, the period will be ignored.
     * @return
     * @throws GardenEntityNotFound 
     */
    @GET
    @Path("findPeonyLogsForTopicByEmployeeByPeriod/{accountName}/{password}/{licenseKey}/{peonyLogTopicName}/{employeeAccountUuid}/{fromDateValue}/{toDateValue}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyLogsForTopicByEmployeeByPeriod(@PathParam("accountName") String accountName, 
                                                            @PathParam("password") String password, 
                                                            @PathParam("licenseKey") String licenseKey, 
                                                            @PathParam("peonyLogTopicName") String peonyLogTopicName, 
                                                            @PathParam("employeeAccountUuid") String employeeAccountUuid,
                                                            @PathParam("fromDateValue") String fromDateValue,
                                                            @PathParam("toDateValue") String toDateValue) 
            throws GardenEntityNotFound 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                Date fromDate = null;
                if (ZcaRegex.isNumberString(fromDateValue)){
                    fromDate = new Date(Long.parseLong(fromDateValue));
                }
                Date toDate = null;
                if (ZcaRegex.isNumberString(toDateValue)){
                    toDate= new Date(Long.parseLong(toDateValue));
                }
                result = Response.ok(getManagementEJB()
                        .findPeonyLogsForTopicByEmployeeByPeriod(
                                PeonyLogTopic.convertEnumNameToType(peonyLogTopicName), 
                                employeeAccountUuid, fromDate, toDate)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findSmsHistoryListByEmployeeByPeriod/{accountName}/{password}/{licenseKey}/{employeeAccountUuid}/{fromDateValue}/{toDateValue}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findSmsHistoryListByEmployeeByPeriod(@PathParam("accountName") String accountName, 
                                                            @PathParam("password") String password, 
                                                            @PathParam("licenseKey") String licenseKey, 
                                                            @PathParam("employeeAccountUuid") String employeeAccountUuid,
                                                            @PathParam("fromDateValue") String fromDateValue,
                                                            @PathParam("toDateValue") String toDateValue) 
            throws GardenEntityNotFound 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                Date fromDate = null;
                if (ZcaRegex.isNumberString(fromDateValue)){
                    fromDate = new Date(Long.parseLong(fromDateValue));
                }
                Date toDate = null;
                if (ZcaRegex.isNumberString(toDateValue)){
                    toDate= new Date(Long.parseLong(toDateValue));
                }
                result = Response.ok(getManagementEJB()
                        .findSmsHistoryListByEmployeeByPeriod(employeeAccountUuid, fromDate, toDate)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findPeonyJobAssignmentListForEmployeeByPeriod/{accountName}/{password}/{licenseKey}/{employeeAccountUuid}/{fromDateValue}/{toDateValue}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyJobAssignmentListForEmployeeByPeriod(@PathParam("accountName") String accountName, 
                                                @PathParam("password") String password, 
                                                @PathParam("licenseKey") String licenseKey, 
                                                @PathParam("employeeAccountUuid") String employeeAccountUuid,
                                                @PathParam("fromDateValue") String fromDateValue,
                                                @PathParam("toDateValue") String toDateValue) 
            throws GardenEntityNotFound 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                Date fromDate = null;
                if (ZcaRegex.isNumberString(fromDateValue)){
                    fromDate = new Date(Long.parseLong(fromDateValue));
                }
                Date toDate = null;
                if (ZcaRegex.isNumberString(toDateValue)){
                    toDate= new Date(Long.parseLong(toDateValue));
                }
                result = Response.ok(getManagementEJB()
                        .findPeonyJobAssignmentListForEmployeeByPeriod(employeeAccountUuid, fromDate, toDate)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findG02DailyReportByUuid/{accountName}/{password}/{licenseKey}/{jobAssignmentUuid}/{reporterUuid}/{reportDate}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findG02DailyReportByUuid(@PathParam("accountName") String accountName, 
                                                @PathParam("password") String password, 
                                                @PathParam("licenseKey") String licenseKey, 
                                                @PathParam("jobAssignmentUuid") String jobAssignmentUuid, 
                                                @PathParam("reporterUuid") String reporterUuid, 
                                                @PathParam("reportDate") String reportDate) 
            throws GardenEntityNotFound 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getManagementEJB()
                        .findG02DailyReportByUuid(jobAssignmentUuid, reporterUuid, reportDate)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findPeonyDailyReportByUuid/{accountName}/{password}/{licenseKey}/{jobAssignmentUuid}/{reporterUuid}/{reportDate}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyDailyReportByUuid(@PathParam("accountName") String accountName, 
                                                @PathParam("password") String password, 
                                                @PathParam("licenseKey") String licenseKey, 
                                                @PathParam("jobAssignmentUuid") String jobAssignmentUuid, 
                                                @PathParam("reporterUuid") String reporterUuid, 
                                                @PathParam("reportDate") String reportDate) 
            throws GardenEntityNotFound 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getManagementEJB()
                        .findPeonyDailyReportByUuid(jobAssignmentUuid, reporterUuid, reportDate)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findLatestDailyReport/{accountName}/{password}/{licenseKey}/{jobAssignmentUuid}/{reporterUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findLatestDailyReport(@PathParam("accountName") String accountName, 
                                                @PathParam("password") String password, 
                                                @PathParam("licenseKey") String licenseKey, 
                                                @PathParam("jobAssignmentUuid") String jobAssignmentUuid, 
                                                @PathParam("reporterUuid") String reporterUuid) 
            throws GardenEntityNotFound 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getManagementEJB()
                        .findLatestDailyReport(jobAssignmentUuid, reporterUuid)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findPeonyDailyReportListForEmployeeByReportDate/{accountName}/{password}/{licenseKey}/{employeeAccountUuid}/{reportDate}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyDailyReportListForEmployeeByReportDate(@PathParam("accountName") String accountName, 
                                                @PathParam("password") String password, 
                                                @PathParam("licenseKey") String licenseKey, 
                                                @PathParam("employeeAccountUuid") String employeeAccountUuid, 
                                                @PathParam("reportDate") String reportDate) 
            throws GardenEntityNotFound 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getManagementEJB()
                        .findPeonyDailyReportListForEmployeeByReportDate(employeeAccountUuid, reportDate)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findPeonyDailyReportListForEmployeeByPeriod/{accountName}/{password}/{licenseKey}/{employeeAccountUuid}/{fromDateValue}/{toDateValue}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyDailyReportListForEmployeeByPeriod(@PathParam("accountName") String accountName, 
                                                @PathParam("password") String password, 
                                                @PathParam("licenseKey") String licenseKey, 
                                                @PathParam("employeeAccountUuid") String employeeAccountUuid,
                                                @PathParam("fromDateValue") String fromDateValue,
                                                @PathParam("toDateValue") String toDateValue) 
            throws GardenEntityNotFound 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                Date fromDate = null;
                if (ZcaRegex.isNumberString(fromDateValue)){
                    fromDate = new Date(Long.parseLong(fromDateValue));
                }
                Date toDate = null;
                if (ZcaRegex.isNumberString(toDateValue)){
                    toDate= new Date(Long.parseLong(toDateValue));
                }
                result = Response.ok(getManagementEJB()
                        .findPeonyDailyReportListForEmployeeByPeriod(employeeAccountUuid, fromDate, toDate)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findPeonyDailyReportListByJobAssignmentUuid/{accountName}/{password}/{licenseKey}/{jobAssignmentUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyDailyReportListByJobAssignmentUuid(@PathParam("accountName") String accountName, 
                                                @PathParam("password") String password, 
                                                @PathParam("licenseKey") String licenseKey, 
                                                @PathParam("jobAssignmentUuid") String jobAssignmentUuid) 
            throws GardenEntityNotFound 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getManagementEJB()
                        .findPeonyDailyReportListByJobAssignmentUuid(jobAssignmentUuid)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findPeonyJobAssignmentListForEmployee/{accountName}/{password}/{licenseKey}/{employeeAccountUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyJobAssignmentListForEmployee(@PathParam("accountName") String accountName, 
                                                @PathParam("password") String password, 
                                                @PathParam("licenseKey") String licenseKey, 
                                                @PathParam("employeeAccountUuid") String employeeAccountUuid) 
            throws GardenEntityNotFound 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getManagementEJB()
                        .findPeonyJobAssignmentListForEmployeeByPeriod(employeeAccountUuid, null, null)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findPeonyCommAssignmentListByEmployeeAccountUuid/{accountName}/{password}/{licenseKey}/{employeeAccountUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyCommAssignmentListByEmployeeAccountUuid(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        @PathParam("employeeAccountUuid") String employeeAccountUuid) 
            throws GardenEntityNotFound 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getManagementEJB().findPeonyCommAssignmentListByEmployeeAccountUuid(employeeAccountUuid)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }

    @GET
    @Path("findPeonyUserBySsn/{accountName}/{password}/{licenseKey}/{ssn}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyUserBySsn(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        @PathParam("ssn") String ssn) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getManagementEJB().findPeonyUserBySsn(ssn)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }

    /**
     * @deprecated - noboday use this
     * @param accountName
     * @param password
     * @param licenseKey
     * @param businessContactorUuid
     * @param smsMessage
     * @return
     * @throws GardenEntityNotFound 
     */
    @GET
    @Path("sendSmsForBusinessContactor/{accountName}/{password}/{licenseKey}/{businessContactorUuid}/{smsMessage}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response sendSmsForBusinessContactor(@PathParam("accountName") String accountName, 
                                            @PathParam("password") String password, 
                                            @PathParam("licenseKey") String licenseKey, 
                                            @PathParam("businessContactorUuid") String businessContactorUuid, 
                                            @PathParam("smsMessage") String smsMessage) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                G02BusinessContactor aG02BusinessContactor = getBusinessEJB().findBusinessContactor(businessContactorUuid);
                if (aG02BusinessContactor == null){
                    throw new Exception("No record for this contactor. This operation failed.");
                }else if (!GardenContactType.MOBILE_PHONE.value().equalsIgnoreCase(aG02BusinessContactor.getContactType())){
                    throw new Exception("This contactor has no mobile-phone record. This operation failed.");
                }else if ((ZcaValidator.isNullEmpty(smsMessage)) || (smsMessage.length() > 140)){
                    throw new Exception("SMS message cannot be empty or lengthy (max 140 characters).");
                }
                getRuntimeEJB().sendSmsText(aG02BusinessContactor.getContactInfo(), smsMessage, false);
                result = Response.ok().build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }

    @GET
    @Path("sendSms/{accountName}/{password}/{licenseKey}/{mobileNumber}/{smsMessage}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response sendSms(@PathParam("accountName") String accountName, 
                                            @PathParam("password") String password, 
                                            @PathParam("licenseKey") String licenseKey, 
                                            @PathParam("mobileNumber") String mobileNumber, 
                                            @PathParam("smsMessage") String smsMessage) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                if (ZcaValidator.isNullEmpty(mobileNumber)){
                    throw new Exception("This contactor has no mobile-phone record. This operation failed.");
                }else if ((ZcaValidator.isNullEmpty(smsMessage)) || (smsMessage.length() > 140)){
                    throw new Exception("SMS message cannot be empty or lengthy (max 140 characters).");
                }
                getRuntimeEJB().sendSmsText(mobileNumber, smsMessage, false);
                result = Response.ok().build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
}
