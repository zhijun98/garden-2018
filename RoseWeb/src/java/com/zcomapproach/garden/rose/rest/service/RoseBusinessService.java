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

import com.zcomapproach.garden.exception.GardenEntityNotFound;
import com.zcomapproach.garden.persistence.entity.G02BusinessContactor;
import com.zcomapproach.garden.persistence.entity.G02TaxFilingCase;
import com.zcomapproach.garden.persistence.entity.G02TaxFilingStatus;
import com.zcomapproach.garden.persistence.entity.GardenLock;
import com.zcomapproach.garden.persistence.entity.GardenUpdateManager;
import com.zcomapproach.garden.persistence.peony.PeonyArchivedFile;
import com.zcomapproach.garden.persistence.peony.PeonyArchivedFileList;
import com.zcomapproach.garden.persistence.peony.PeonyBillPayment;
import com.zcomapproach.garden.persistence.peony.PeonyDeadlineExtension;
import com.zcomapproach.garden.persistence.peony.PeonyDocumentTag;
import com.zcomapproach.garden.persistence.peony.PeonyMemo;
import com.zcomapproach.garden.persistence.peony.PeonyPayment;
import com.zcomapproach.garden.persistence.peony.PeonyTaxFilingCase;
import com.zcomapproach.garden.persistence.peony.PeonyTaxFilingCaseList;
import com.zcomapproach.garden.persistence.peony.data.PeonyMemoFilter;
import com.zcomapproach.garden.rest.data.GardenRestStringList;
import com.zcomapproach.garden.rose.rest.AbstractRestService;
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
@Path("business")    //refer to RoseWebResoureRoot.value()
public class RoseBusinessService extends AbstractRestService{
    
    @DELETE
    @Path("deleteTaxFilingCases/{accountName}/{password}/{licenseKey}/{taxCaseUuid}/{taxFilingType}/{taxFilingPeriod}/{fromDateValue}/{toDateValue}/{forceDeletion}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteTaxFilingCases(@PathParam("accountName") String accountName, 
                                            @PathParam("password") String password, 
                                            @PathParam("licenseKey") String licenseKey, 
                                            @PathParam("taxCaseUuid") String taxCaseUuid,   //e.g. taxcorp or taxpayer
                                            @PathParam("taxFilingType") String taxFilingType,
                                            @PathParam("taxFilingPeriod") String taxFilingPeriod,
                                            @PathParam("fromDateValue") String fromDateValue,
                                            @PathParam("toDateValue") String toDateValue, 
                                            @PathParam("forceDeletion") boolean forceDeletion)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                Date fromDate = null;
                if (ZcaRegex.isNumberString(fromDateValue)){
                    fromDate = new Date(Long.parseLong(fromDateValue));
                }
                Date toDate = null;
                if (ZcaRegex.isNumberString(toDateValue)){
                    toDate= new Date(Long.parseLong(toDateValue));
                }
                result = Response.ok(getBusinessEJB().deleteTaxFilingCases(taxCaseUuid, taxFilingType, taxFilingPeriod, fromDate, toDate, forceDeletion)).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @DELETE
    @Path("deleteTaxFilingCaseByTaxFilingUuid/{accountName}/{password}/{licenseKey}/{taxFilingUuid}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteTaxFilingCaseByTaxFilingUuid(@PathParam("accountName") String accountName, 
                                                        @PathParam("password") String password, 
                                                        @PathParam("licenseKey") String licenseKey, 
                                                        @PathParam("taxFilingUuid") String taxFilingUuid)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(getBusinessEJB().deleteTaxFilingCaseByTaxFilingUuid(taxFilingUuid)).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findPeonyTaxFilingCase/{accountName}/{password}/{licenseKey}/{taxFilingUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyTaxFilingCase(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        @PathParam("taxFilingUuid") String taxFilingUuid) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                PeonyTaxFilingCase aPeonyTaxFilingCase = getBusinessEJB().findPeonyTaxFilingCase(taxFilingUuid);
                if (aPeonyTaxFilingCase == null){
                    aPeonyTaxFilingCase = new PeonyTaxFilingCase(); //empty case
                }
                result = Response.ok(aPeonyTaxFilingCase).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findPeonyTaxFilingCaseListByTaxcorpCaseUuidAndType/{accountName}/{password}/{licenseKey}/{taxcorpCaseUuid}/{taxFilingType}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyTaxFilingCaseListByTaxcorpCaseUuidAndType(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        @PathParam("taxcorpCaseUuid") String taxcorpCaseUuid, 
                                        @PathParam("taxFilingType") String taxFilingType) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                PeonyTaxFilingCaseList aPeonyTaxFilingCaseList = getBusinessEJB().findPeonyTaxFilingCaseListByTaxcorpCaseUuidAndType(taxcorpCaseUuid, taxFilingType);
                if (aPeonyTaxFilingCaseList == null){
                    aPeonyTaxFilingCaseList = new PeonyTaxFilingCaseList(); //empty case
                }
                result = Response.ok(aPeonyTaxFilingCaseList).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findPeonyTaxFilingCaseListByTypeAndDateRange/{accountName}/{password}/{licenseKey}/{taxFilingType}/{fromDateValue}/{toDateValue}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyTaxFilingCaseListByTypeAndDateRange(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        @PathParam("taxFilingType") String taxFilingType,
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
                PeonyTaxFilingCaseList aPeonyTaxFilingCaseList = getBusinessEJB().findPeonyTaxFilingCaseListByTypeAndDateRange(taxFilingType, fromDate, toDate);
                if (aPeonyTaxFilingCaseList == null){
                    aPeonyTaxFilingCaseList = new PeonyTaxFilingCaseList(); //empty case
                }
                result = Response.ok(aPeonyTaxFilingCaseList).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findPeonyTaxFilingCaseListByTaxcorpCaseUuidAndTypeAndDateRange/{accountName}/{password}/{licenseKey}/{taxcorpCaseUuid}/{taxFilingType}/{fromDateValue}/{toDateValue}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyTaxFilingCaseListByTaxcorpCaseUuidAndTypeAndDateRange(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        @PathParam("taxcorpCaseUuid") String taxcorpCaseUuid, 
                                        @PathParam("taxFilingType") String taxFilingType,
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
                PeonyTaxFilingCaseList aPeonyTaxFilingCaseList = getBusinessEJB().findPeonyTaxFilingCaseListByTaxcorpCaseUuidAndTypeAndDateRange(taxcorpCaseUuid, taxFilingType, fromDate, toDate);
                if (aPeonyTaxFilingCaseList == null){
                    aPeonyTaxFilingCaseList = new PeonyTaxFilingCaseList(); //empty case
                }
                result = Response.ok(aPeonyTaxFilingCaseList).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findMemoEntity/{accountName}/{password}/{licenseKey}/{memoUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findMemoEntity(@PathParam("accountName") String accountName, 
                            @PathParam("password") String password, 
                            @PathParam("licenseKey") String licenseKey, 
                            @PathParam("memoUuid") String memoUuid) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getBusinessEJB().findMemoEntity(memoUuid)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findBillEntity/{accountName}/{password}/{licenseKey}/{billUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findBillEntity(@PathParam("accountName") String accountName, 
                            @PathParam("password") String password, 
                            @PathParam("licenseKey") String licenseKey, 
                            @PathParam("billUuid") String billUuid) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getBusinessEJB().findBillEntity(billUuid)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findDeadlineExtensionEntity/{accountName}/{password}/{licenseKey}/{deadlineExtensionUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findDeadlineExtensionEntity(@PathParam("accountName") String accountName, 
                            @PathParam("password") String password, 
                            @PathParam("licenseKey") String licenseKey, 
                            @PathParam("deadlineExtensionUuid") String deadlineExtensionUuid) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getBusinessEJB().findDeadlineExtensionEntity(deadlineExtensionUuid)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findPaymentEntity/{accountName}/{password}/{licenseKey}/{paymentUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPaymentEntity(@PathParam("accountName") String accountName, 
                            @PathParam("password") String password, 
                            @PathParam("licenseKey") String licenseKey, 
                            @PathParam("paymentUuid") String paymentUuid) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getBusinessEJB().findPaymentEntity(paymentUuid)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findPeonyBillPayment/{accountName}/{password}/{licenseKey}/{billUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyBillPayment(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        @PathParam("billUuid") String billUuid) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getBusinessEJB().findPeonyBillPayment(billUuid)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("retrievePeonyBillPaymentListByEntityUuidList/{accountName}/{password}/{licenseKey}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response retrievePeonyBillPaymentListByEntityUuidList(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        GardenRestStringList entityUuidList) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
               result = Response.ok(getBusinessEJB().retrievePeonyBillPaymentListByEntityUuidList(entityUuidList.getStringDataList())).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findPeonyMemoListByEntityUuid/{accountName}/{password}/{licenseKey}/{entityUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyMemoListByEntityUuid(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        @PathParam("entityUuid") String entityUuid) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getBusinessEJB().findPeonyMemoListByEntityUuid(entityUuid)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findPeonyMemoListByEntityStatus/{accountName}/{password}/{licenseKey}/{entityStatus}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyMemoListByEntityStatus(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        @PathParam("entityStatus") String entityStatus) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getBusinessEJB().findPeonyMemoListByEntityStatus(entityStatus)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("retrievePeonyPublicForumMessageList/{accountName}/{password}/{licenseKey}/{entityStatus}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response retrievePeonyPublicForumMessageList(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        PeonyMemoFilter peonyMemoFilter) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getBusinessEJB().retrievePeonyPublicForumMessageList(peonyMemoFilter)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
////    /**
////     * 
////     * @param accountName
////     * @param password
////     * @param licenseKey
////     * @param taxFilingUuidList
////     * @return this response contains a PeonyTaxFilingCaseList of which is used to help return memo 
////     * list for every tex-filing record (only contains its own UUID).  
////     * @throws GardenEntityNotFound 
////     */
////    @PUT
////    @Path("retrievePeonyMemoListByEntityUuidList/{accountName}/{password}/{licenseKey}")
////    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
////    public Response retrievePeonyMemoListByEntityUuidList(@PathParam("accountName") String accountName, 
////                                        @PathParam("password") String password, 
////                                        @PathParam("licenseKey") String licenseKey, 
////                                        GardenRestStringList taxFilingUuidList) 
////            throws GardenEntityNotFound 
////    {
////        
////        Response result = super.verifyCredentials(accountName, password, licenseKey);
////        
////        if (result.getStatus() == Response.Status.OK.getStatusCode()){
////            try {
////               result = Response.ok(getBusinessEJB().retrievePeonyMemoListByEntityUuidList(taxFilingUuidList.getStringDataList())).build();
////            } catch (Exception ex) {
////                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
////                result = Response.serverError().entity(ex.getMessage()).build();
////            }
////        }
////        return result;
////    }
    
    @GET
    @Path("findPeonyMemoListByEntityType/{accountName}/{password}/{licenseKey}/{entityType}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyMemoListByEntityType(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        @PathParam("entityType") String entityType) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getBusinessEJB().findPeonyMemoListByEntityType(entityType)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }

    @GET
    @Path("findG02ArchivedDocumentFileNameListForUpdate09302019/{accountName}/{password}/{licenseKey}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findG02ArchivedDocumentFileNameListForUpdate09302019(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getBusinessEJB().findG02ArchivedDocumentFileNameListForUpdate09302019()).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }

    @GET
    @Path("findG02ArchivedFilePathListForUpdate09302019/{accountName}/{password}/{licenseKey}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findG02ArchivedFilePathListForUpdate09302019(@PathParam("accountName") String accountName, 
                                                                @PathParam("password") String password, 
                                                                @PathParam("licenseKey") String licenseKey) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getBusinessEJB().findG02ArchivedFilePathListForUpdate09302019()).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }

    @GET
    @Path("retrieveArchivedFileNameListForBackup/{accountName}/{password}/{licenseKey}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response retrieveArchivedFileNameListForBackup(@PathParam("accountName") String accountName, 
                                                                @PathParam("password") String password, 
                                                                @PathParam("licenseKey") String licenseKey) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getBusinessEJB().retrieveArchivedFileNameListForBackup()).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }

    @GET
    @Path("retrievePeonyArchivedFileNameWithSizeList/{accountName}/{password}/{licenseKey}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response retrievePeonyArchivedFileNameWithSizeList(@PathParam("accountName") String accountName, 
                                                                @PathParam("password") String password, 
                                                                @PathParam("licenseKey") String licenseKey) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getBusinessEJB().retrievePeonyArchivedFileNameWithSizeList()).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }

    /**
     * 
     * @param accountName
     * @param password
     * @param licenseKey
     * @param backupArchivedFileName
     * @return
     * @throws GardenEntityNotFound 
     */
    @GET
    @javax.ws.rs.Path("archiveFileBackupCompleted/{accountName}/{password}/{licenseKey}/{backupArchivedFileName}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response archiveFileBackupCompleted(@PathParam("accountName") String accountName, 
                                            @PathParam("password") String password, 
                                            @PathParam("licenseKey") String licenseKey, 
                                            @PathParam("backupArchivedFileName") String backupArchivedFileName) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                getBusinessEJB().archiveFileBackupCompleted(backupArchivedFileName);
                result = Response.ok().build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    /**
     * Find all the bills associated with entityUuid (e.g. taxcorpCaseUuid)
     * @param accountName
     * @param password
     * @param licenseKey
     * @param entityUuid
     * @return
     * @throws GardenEntityNotFound 
     */
    @GET
    @Path("findPeonyBillPaymentList/{accountName}/{password}/{licenseKey}/{entityUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyBillPaymentList(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        @PathParam("entityUuid") String entityUuid) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getBusinessEJB().findPeonyBillPaymentList(entityUuid)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("tryToLockByGarden/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response tryToLockByGarden(@PathParam("accountName") String accountName, 
                                            @PathParam("password") String password, 
                                            @PathParam("licenseKey") String licenseKey, 
                                            GardenLock aGardenLock)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getBusinessEJB().tryToLockByGarden(aGardenLock)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("unlockByGarden/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response unlockByGarden(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    GardenLock aGardenLock)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getBusinessEJB().unlockByGarden(aGardenLock)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("lockGardenUpdateManager/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response lockGardenUpdateManager(@PathParam("accountName") String accountName, 
                                            @PathParam("password") String password, 
                                            @PathParam("licenseKey") String licenseKey, 
                                            GardenUpdateManager aGardenUpdateManager)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
////                result = Response.ok(
////                            getBusinessEJB().lockGardenUpdateManager(aGardenUpdateManager)
////                         ).build();
                result = Response.ok(null).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("unlockGardenUpdateManager/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response unlockGardenUpdateManager(@PathParam("accountName") String accountName, 
                                                                @PathParam("password") String password, 
                                                                @PathParam("licenseKey") String licenseKey, 
                                                                GardenUpdateManager aGardenUpdateManager)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getBusinessEJB().unlockGardenUpdateManager(aGardenUpdateManager)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storeBusinessContactor/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storeBusinessContactor(@PathParam("accountName") String accountName, 
                                                                @PathParam("password") String password, 
                                                                @PathParam("licenseKey") String licenseKey, 
                                                                G02BusinessContactor aG02BusinessContactor)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getBusinessEJB().storeBusinessContactor(aG02BusinessContactor)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storeTaxFilingCase/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storeTaxFilingCase(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        G02TaxFilingCase aG02TaxFilingCase)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getBusinessEJB().storeTaxFilingCase(aG02TaxFilingCase)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    @PUT
    @Path("storeG02TaxFilingStatus/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storeG02TaxFilingStatus(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        G02TaxFilingStatus aG02TaxFilingStatus)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getBusinessEJB().storeG02TaxFilingStatus(aG02TaxFilingStatus)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storePeonyDocumentTag/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePeonyDocumentTag(@PathParam("accountName") String accountName, 
                                @PathParam("password") String password, 
                                @PathParam("licenseKey") String licenseKey, 
                                PeonyDocumentTag aPeonyDocumentTag)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getBusinessEJB().storePeonyDocumentTag(aPeonyDocumentTag)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storePeonyMemo/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePeonyMemo(@PathParam("accountName") String accountName, 
                                @PathParam("password") String password, 
                                @PathParam("licenseKey") String licenseKey, 
                                PeonyMemo aPeonyMemo)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getBusinessEJB().storePeonyMemo(aPeonyMemo)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storePeonyDeadlineExtension/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePeonyDeadlineExtension(@PathParam("accountName") String accountName, 
                                                @PathParam("password") String password, 
                                                @PathParam("licenseKey") String licenseKey, 
                                                PeonyDeadlineExtension aPeonyDeadlineExtension)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getBusinessEJB().storePeonyDeadlineExtension(aPeonyDeadlineExtension)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @DELETE
    @Path("deleteBusinessContactor/{accountName}/{password}/{licenseKey}/{businessContactorUuid}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteBusinessContactor(@PathParam("accountName") String accountName, 
                                            @PathParam("password") String password, 
                                            @PathParam("licenseKey") String licenseKey, 
                                            @PathParam("businessContactorUuid") String businessContactorUuid)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getBusinessEJB().deleteBusinessContactor(businessContactorUuid)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @DELETE
    @Path("deletePeonyDocumentTag/{accountName}/{password}/{licenseKey}/{documentTagUuid}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deletePeonyDocumentTag(@PathParam("accountName") String accountName, 
                                            @PathParam("password") String password, 
                                            @PathParam("licenseKey") String licenseKey, 
                                            @PathParam("documentTagUuid") String documentTagUuid)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getBusinessEJB().deletePeonyDocumentTag(documentTagUuid)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @DELETE
    @Path("deleteArchivedFile/{accountName}/{password}/{licenseKey}/{fileUuid}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteArchivedFile(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        @PathParam("fileUuid") String fileUuid)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getBusinessEJB().deleteArchivedFile(fileUuid)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storePeonyArchivedFileList/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePeonyArchivedFileList(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        PeonyArchivedFileList aPeonyArchivedFileList)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getBusinessEJB().storePeonyArchivedFileList(aPeonyArchivedFileList)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storePeonyArchivedFile/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePeonyArchivedFile(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        PeonyArchivedFile aPeonyArchivedFile)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getBusinessEJB().storePeonyArchivedFile(aPeonyArchivedFile)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storePeonyBillPayment/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePeonyBillPayment(@PathParam("accountName") String accountName, 
                                        @PathParam("password") String password, 
                                        @PathParam("licenseKey") String licenseKey, 
                                        PeonyBillPayment aPeonyBillPayment)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getBusinessEJB().storePeonyBillPayment(aPeonyBillPayment)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storePeonyPayment/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePeonyPayment(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    PeonyPayment aPeonyPayment)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getBusinessEJB().storePeonyPayment(aPeonyPayment)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @DELETE
    @Path("deleteMemoEntity/{accountName}/{password}/{licenseKey}/{memoEntityUuid}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteMemoEntity(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    @PathParam("memoEntityUuid") String memoUuid)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(getBusinessEJB().deleteMemoEntity(memoUuid)).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @DELETE
    @Path("deletePayment/{accountName}/{password}/{licenseKey}/{paymentUuid}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deletePayment(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    @PathParam("paymentUuid") String paymentUuid)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(getBusinessEJB().deletePayment(paymentUuid)).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @DELETE
    @Path("deleteBill/{accountName}/{password}/{licenseKey}/{billUuid}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteBill(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    @PathParam("billUuid") String billUuid)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(getBusinessEJB().deleteBill(billUuid)).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
}
