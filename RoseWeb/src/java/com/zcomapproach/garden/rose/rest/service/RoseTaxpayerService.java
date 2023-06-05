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

import com.zcomapproach.garden.data.constant.SearchTaxpayerCriteria;
import com.zcomapproach.garden.data.constant.TaxpayerEntityDateField;
import com.zcomapproach.garden.exception.GardenEntityNotFound;
import com.zcomapproach.garden.persistence.entity.G02PersonalBusinessProperty;
import com.zcomapproach.garden.persistence.entity.G02PersonalProperty;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerInfo;
import com.zcomapproach.garden.persistence.entity.G02TlcLicense;
import com.zcomapproach.garden.persistence.peony.PeonyTaxpayerCase;
import com.zcomapproach.garden.rose.rest.AbstractRestService;
import com.zcomapproach.commons.ZcaRegex;
import com.zcomapproach.garden.persistence.peony.data.TaxpayerCaseSearchResultList;
import com.zcomapproach.garden.persistence.peony.data.TaxpayerCaseStatusCacheResult;
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
@Path("taxpayer")    //refer to RoseWebResoureRoot.value()
public class RoseTaxpayerService extends AbstractRestService{
    
    @GET
    @Path("cloneAllTaxpayerCases/{accountName}/{password}/{licenseKey}/{deadlineValue}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response cloneAllTaxpayerCases(@PathParam("accountName") String accountName, 
                                         @PathParam("password") String password, 
                                         @PathParam("licenseKey") String licenseKey, 
                                         @PathParam("deadlineValue") String deadlineValue) 
            throws GardenEntityNotFound 
    {
        
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                Date deadline = null;
                if (ZcaRegex.isNumberString(deadlineValue)){
                    deadline = new Date(Long.parseLong(deadlineValue));
                }
                //cloneMissedLocationForTaxpayer
                //result = Response.ok(getTaxpayerEJB().cloneMissedLocationForTaxpayer(deadline)).build();
                result = Response.ok(getTaxpayerEJB().cloneAllTaxpayerCases(deadline)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findTaxpayerInfoBySsn/{accountName}/{password}/{licenseKey}/{ssn}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findTaxpayerInfoBySsn(@PathParam("accountName") String accountName, 
                                                    @PathParam("password") String password, 
                                                    @PathParam("licenseKey") String licenseKey, 
                                                    @PathParam("ssn") String ssn) 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getTaxpayerEJB().findTaxpayerInfoBySsn(ssn)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findBasicPeonyTaxpayerCaseListByPrimaryTaxpayerSsn/{accountName}/{password}/{licenseKey}/{primarySsn}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findBasicPeonyTaxpayerCaseListByPrimaryTaxpayerSsn(@PathParam("accountName") String accountName, 
                                                                            @PathParam("password") String password, 
                                                                            @PathParam("licenseKey") String licenseKey, 
                                                                            @PathParam("primarySsn") String taxpayerSsn) 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getTaxpayerEJB().findBasicPeonyTaxpayerCaseListByPrimaryTaxpayerSsn(taxpayerSsn)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("loadWorkStatusForPeonyTaxpayerCase/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response loadWorkStatusForPeonyTaxpayerCase(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    PeonyTaxpayerCase aPeonyTaxpayerCase)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().loadWorkStatusForPeonyTaxpayerCase(aPeonyTaxpayerCase)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("loadTagsAndMemosForPeonyTaxpayerCase/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response loadTagsAndMemosForPeonyTaxpayerCase(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    PeonyTaxpayerCase aPeonyTaxpayerCase)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().loadTagsAndMemosForPeonyTaxpayerCase(aPeonyTaxpayerCase)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("loadOtherInformationForPeonyTaxpayerCase/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response loadOtherInformationForPeonyTaxpayerCase(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    PeonyTaxpayerCase aPeonyTaxpayerCase)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().loadOtherInformationForPeonyTaxpayerCase(aPeonyTaxpayerCase)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("loadBillAndPaymentsForPeonyTaxpayerCase/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response loadBillAndPaymentsForPeonyTaxpayerCase(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    PeonyTaxpayerCase aPeonyTaxpayerCase)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().loadBillAndPaymentsForPeonyTaxpayerCase(aPeonyTaxpayerCase)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("loadArchiveAndFilesForPeonyTaxpayerCase/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response loadArchiveAndFilesForPeonyTaxpayerCase(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    PeonyTaxpayerCase aPeonyTaxpayerCase)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().loadArchiveAndFilesForPeonyTaxpayerCase(aPeonyTaxpayerCase)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findPeonyTaxpayerCaseListByPrimaryTaxpayerSsn/{accountName}/{password}/{licenseKey}/{primarySsn}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyTaxpayerCaseListByPrimaryTaxpayerSsn(@PathParam("accountName") String accountName, 
                                                                            @PathParam("password") String password, 
                                                                            @PathParam("licenseKey") String licenseKey, 
                                                                            @PathParam("primarySsn") String taxpayerSsn) 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getTaxpayerEJB().findPeonyTaxpayerCaseListByPrimaryTaxpayerSsn(taxpayerSsn)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findPeonyTaxpayerCaseListByTaxpayerSsn/{accountName}/{password}/{licenseKey}/{primarySsn}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyTaxpayerCaseListByTaxpayerSsn(@PathParam("accountName") String accountName, 
                                                                            @PathParam("password") String password, 
                                                                            @PathParam("licenseKey") String licenseKey, 
                                                                            @PathParam("primarySsn") String taxpayerSsn) 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getTaxpayerEJB().findPeonyTaxpayerCaseListByTaxpayerSsn(taxpayerSsn)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findTaxpayerCaseByTaxpayerCaseUuid/{accountName}/{password}/{licenseKey}/{taxpayerCaseUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findTaxpayerCaseByTaxpayerCaseUuid(@PathParam("accountName") String accountName, 
                                                       @PathParam("password") String password, 
                                                       @PathParam("licenseKey") String licenseKey, 
                                                       @PathParam("taxpayerCaseUuid") String taxpayerCaseUuid) 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getTaxpayerEJB().findTaxpayerCaseByTaxpayerCaseUuid(taxpayerCaseUuid)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxpayerService.class.getName()).log(Level.SEVERE, null, ex);
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
     * @param taxpayerCaseUuid
     * @return - if not found, NULL returned
     */
    @GET
    @Path("findPeonyTaxpayerCaseByTaxpayerCaseUuid/{accountName}/{password}/{licenseKey}/{taxpayerCaseUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyTaxpayerCaseByTaxpayerCaseUuid(@PathParam("accountName") String accountName, 
                                                            @PathParam("password") String password, 
                                                            @PathParam("licenseKey") String licenseKey, 
                                                            @PathParam("taxpayerCaseUuid") String taxpayerCaseUuid) 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getTaxpayerEJB().findPeonyTaxpayerCaseByTaxpayerCaseUuid(taxpayerCaseUuid)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxpayerService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findBasicPeonyTaxpayerCaseByTaxpayerCaseUuid/{accountName}/{password}/{licenseKey}/{taxpayerCaseUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findBasicPeonyTaxpayerCaseByTaxpayerCaseUuid(@PathParam("accountName") String accountName, 
                                                            @PathParam("password") String password, 
                                                            @PathParam("licenseKey") String licenseKey, 
                                                            @PathParam("taxpayerCaseUuid") String taxpayerCaseUuid) 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getTaxpayerEJB().findBasicPeonyTaxpayerCaseByTaxpayerCaseUuid(taxpayerCaseUuid)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxpayerService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("searchTaxpayerCaseSearchResultListByDateRange/{accountName}/{password}/{licenseKey}/{taxpayerDateFeature}/{fromTimeValue}/{toTimeValue}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response searchTaxpayerCaseSearchResultListByDateRange(@PathParam("accountName") String accountName, 
                                                                @PathParam("password") String password, 
                                                                @PathParam("licenseKey") String licenseKey, 
                                                                @PathParam("taxpayerDateFeature") String taxpayerDateFeature, 
                                                                @PathParam("fromTimeValue") long fromTimeValue, 
                                                                @PathParam("toTimeValue") long toTimeValue)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().searchTaxpayerCaseSearchResultListByDateRange(
                                    TaxpayerEntityDateField.convertEnumNameToType(taxpayerDateFeature, false).value(), 
                                    new Date(fromTimeValue), new Date(toTimeValue))
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxpayerService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("retrieveTaxpayerCaseSearchCacheResultListByDateRange/{accountName}/{password}/{licenseKey}/{taxpayerDateFeature}/{fromTimeValue}/{toTimeValue}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response retrieveTaxpayerCaseSearchCacheResultListByDateRange(@PathParam("accountName") String accountName, 
                                                                @PathParam("password") String password, 
                                                                @PathParam("licenseKey") String licenseKey, 
                                                                @PathParam("taxpayerDateFeature") String taxpayerDateFeature, 
                                                                @PathParam("fromTimeValue") long fromTimeValue, 
                                                                @PathParam("toTimeValue") long toTimeValue)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().retrieveTaxpayerCaseSearchCacheResultListByDateRange(
                                    TaxpayerEntityDateField.convertEnumNameToType(taxpayerDateFeature, false).value(), 
                                    new Date(fromTimeValue), new Date(toTimeValue))
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxpayerService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("searchTaxpayerCaseSearchResultListByDateRangeAndLatestWorkStatus/{accountName}/{password}/{licenseKey}/{taxpayerDateFeature}/{fromTimeValue}/{toTimeValue}/{workStatusForSearch}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response searchTaxpayerCaseSearchResultListByDateRangeAndLatestWorkStatus(@PathParam("accountName") String accountName, 
                                                                @PathParam("password") String password, 
                                                                @PathParam("licenseKey") String licenseKey, 
                                                                @PathParam("taxpayerDateFeature") String taxpayerDateFeature, 
                                                                @PathParam("fromTimeValue") long fromTimeValue, 
                                                                @PathParam("toTimeValue") long toTimeValue, 
                                                                @PathParam("workStatusForSearch") String workStatusForSearch)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().searchTaxpayerCaseSearchResultListByDateRangeAndLatestWorkStatus(
                                    TaxpayerEntityDateField.convertEnumNameToType(taxpayerDateFeature, false).value(), 
                                    new Date(fromTimeValue), new Date(toTimeValue), workStatusForSearch)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxpayerService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("retrieveTaxpayerCaseSearchCacheResultListByDateRangeAndLatestWorkStatus/{accountName}/{password}/{licenseKey}/{taxpayerDateFeature}/{fromTimeValue}/{toTimeValue}/{workStatusForSearch}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response retrieveTaxpayerCaseSearchCacheResultListByDateRangeAndLatestWorkStatus(@PathParam("accountName") String accountName, 
                                                                @PathParam("password") String password, 
                                                                @PathParam("licenseKey") String licenseKey, 
                                                                @PathParam("taxpayerDateFeature") String taxpayerDateFeature, 
                                                                @PathParam("fromTimeValue") long fromTimeValue, 
                                                                @PathParam("toTimeValue") long toTimeValue, 
                                                                @PathParam("workStatusForSearch") String workStatusForSearch)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().retrieveTaxpayerCaseSearchCacheResultListByDateRangeForSpecificWorkStatus(
                                    TaxpayerEntityDateField.convertEnumNameToType(taxpayerDateFeature, false).value(), 
                                    new Date(fromTimeValue), new Date(toTimeValue), workStatusForSearch)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxpayerService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("retrieveTaxpayerCaseStatusCacheResultListByDateRangeAndLatestWorkStatus/{accountName}/{password}/{licenseKey}/{taxpayerDateFeature}/{fromTimeValue}/{toTimeValue}/{workStatusForSearch}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response retrieveTaxpayerCaseStatusCacheResultListByDateRangeAndLatestWorkStatus(@PathParam("accountName") String accountName, 
                                                                @PathParam("password") String password, 
                                                                @PathParam("licenseKey") String licenseKey, 
                                                                @PathParam("taxpayerDateFeature") String taxpayerDateFeature, 
                                                                @PathParam("fromTimeValue") long fromTimeValue, 
                                                                @PathParam("toTimeValue") long toTimeValue, 
                                                                @PathParam("workStatusForSearch") String workStatusForSearch)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().retrieveTaxpayerCaseStatusCacheResultListByDateRangeForSpecificWorkStatus(
                                    TaxpayerEntityDateField.convertEnumNameToType(taxpayerDateFeature, false).value(), 
                                    new Date(fromTimeValue), new Date(toTimeValue), workStatusForSearch)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxpayerService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("searchTaxpayerCaseSearchCacheResultListByFeatureFromCache/{accountName}/{password}/{licenseKey}/{taxpayerFeatureName}/{taxpayerFeatureValue}/{exactMatch}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response searchTaxpayerCaseSearchCacheResultListByFeatureFromCache(@PathParam("accountName") String accountName, 
                                                                        @PathParam("password") String password, 
                                                                        @PathParam("licenseKey") String licenseKey, 
                                                                        @PathParam("taxpayerFeatureName") String taxpayerFeatureName, 
                                                                        @PathParam("taxpayerFeatureValue") String taxpayerFeatureValue, 
                                                                        @PathParam("exactMatch") boolean exactMatch)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().searchTaxpayerCaseSearchCacheResultListByFeatureFromCache(
                                    SearchTaxpayerCriteria.convertEnumNameToType(taxpayerFeatureName, false).value(), 
                                    taxpayerFeatureValue, exactMatch)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxpayerService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }

    @GET
    @Path("generatePeonyTaxFilingCaseListForTaxpayer/{accountName}/{password}/{licenseKey}/{taxpayerUuid}/{taxFilingType}/{taxFilingPeriod}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response generatePeonyTaxFilingCaseListForTaxpayer(@PathParam("accountName") String accountName, 
                                                            @PathParam("password") String password, 
                                                            @PathParam("licenseKey") String licenseKey, 
                                                            @PathParam("taxpayerUuid") String taxpayerUuid, 
                                                            @PathParam("taxFilingType") String taxFilingType, 
                                                            @PathParam("taxFilingPeriod") String taxFilingPeriod)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getTaxpayerEJB().generatePeonyTaxReturnFilingCaseListForTaxpayer(taxpayerUuid)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxpayerService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    
    }
    
    @GET
    @Path("findTaxpayerCaseBriefListByPeriod/{accountName}/{password}/{licenseKey}/{fromDateValue}/{toDateValue}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findTaxpayerCaseBriefListByPeriod(@PathParam("accountName") String accountName, 
                                                    @PathParam("password") String password, 
                                                    @PathParam("licenseKey") String licenseKey, 
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
                result = Response.ok(getTaxpayerEJB().findTaxpayerCaseBriefListByDeadlinePeriod(fromDate, toDate)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storePersonalProperty/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePersonalProperty(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    G02PersonalProperty aG02PersonalProperty)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().storePersonalProperty(aG02PersonalProperty)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storeTaxpayerCaseStatusCacheResult/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storeTaxpayerCaseStatusCacheResult(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    TaxpayerCaseStatusCacheResult aTaxpayerCaseStatusCacheResult)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().storeTaxpayerCaseStatusCacheResult(aTaxpayerCaseStatusCacheResult)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storeTaxpayerCaseStatusCacheResultWithStatusLog/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storeTaxpayerCaseStatusCacheResultWithStatusLog(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    TaxpayerCaseStatusCacheResult aTaxpayerCaseStatusCacheResult)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().storeTaxpayerCaseStatusCacheResultWithStatusLog(aTaxpayerCaseStatusCacheResult, false)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("removeStatusLogFromTaxpayerCaseStatusCacheResult/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response removeStatusLogFromTaxpayerCaseStatusCacheResult(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    TaxpayerCaseStatusCacheResult aTaxpayerCaseStatusCacheResult)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().storeTaxpayerCaseStatusCacheResultWithStatusLog(aTaxpayerCaseStatusCacheResult, true)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storePersonalBusinessProperty/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePersonalBusinessProperty(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    G02PersonalBusinessProperty aG02PersonalBusinessProperty)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().storePersonalBusinessProperty(aG02PersonalBusinessProperty)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storeTlcLicense/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storeTlcLicense(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    G02TlcLicense aG02TlcLicense)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().storeTlcLicense(aG02TlcLicense)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storeTaxpayerInfo/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storeTaxpayerInfo(@PathParam("accountName") String accountName, 
                                    @PathParam("password") String password, 
                                    @PathParam("licenseKey") String licenseKey, 
                                    G02TaxpayerInfo aG02TaxpayerInfo)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().storeTaxpayerInfo(aG02TaxpayerInfo)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("retrieveDataForTaxpayerCaseSearchResultByTaxpayerCaseUuid/{accountName}/{password}/{licenseKey}/{fromDateValue}/{toDateValue}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response retrieveDataForTaxpayerCaseSearchResultByTaxpayerCaseUuid(@PathParam("accountName") String accountName, 
                                                    @PathParam("password") String password, 
                                                    @PathParam("licenseKey") String licenseKey, 
                                                    @PathParam("taxpayerCaseUuid") String taxpayerCaseUuid) 
            throws GardenEntityNotFound 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getTaxpayerEJB().retrieveDataForTaxpayerCaseSearchResultByTaxpayerCaseUuid(taxpayerCaseUuid)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    /**
     * @param accountName
     * @param password
     * @param licenseKey
     * @param taxpayerCaseSearchResultList
     * @return 
     */
    @PUT
    @Path("retrieveTaxpayerInfoListForTaxpayerCaseSearchResultList/{accountName}/{password}/{licenseKey}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response retrieveTaxpayerInfoListForTaxpayerCaseSearchResultList(@PathParam("accountName") String accountName, 
                                                                @PathParam("password") String password, 
                                                                @PathParam("licenseKey") String licenseKey, 
                                                                TaxpayerCaseSearchResultList taxpayerCaseSearchResultList)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().retrieveTaxpayerInfoListForTaxpayerCaseSearchResultList(taxpayerCaseSearchResultList)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    /**
     * @deprecated - performance issue
     * @param accountName
     * @param password
     * @param licenseKey
     * @param taxpayerCaseSearchResultList
     * @return 
     */
    @PUT
    @Path("retrieveDataForTaxpayerCaseSearchResultList/{accountName}/{password}/{licenseKey}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response retrieveDataForTaxpayerCaseSearchResultList(@PathParam("accountName") String accountName, 
                                                                @PathParam("password") String password, 
                                                                @PathParam("licenseKey") String licenseKey, 
                                                                TaxpayerCaseSearchResultList taxpayerCaseSearchResultList)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().retrieveDataForTaxpayerCaseSearchResultList(taxpayerCaseSearchResultList)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    /**
     * @deprecated - performance issue
     * @param accountName
     * @param password
     * @param licenseKey
     * @param taxpayerCaseSearchResultList
     * @return 
     */
    @PUT
    @Path("retrieveWorkStatusLogListForTaxpayerCaseSearchResultList/{accountName}/{password}/{licenseKey}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response retrieveWorkStatusLogListForTaxpayerCaseSearchResultList(@PathParam("accountName") String accountName, 
                                                                @PathParam("password") String password, 
                                                                @PathParam("licenseKey") String licenseKey, 
                                                                TaxpayerCaseSearchResultList taxpayerCaseSearchResultList)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().retrieveWorkStatusLogListForTaxpayerCaseSearchResultList(taxpayerCaseSearchResultList)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    /**
     * @deprecated - performance issue
     * @param accountName
     * @param password
     * @param licenseKey
     * @param taxpayerCaseSearchResultList
     * @return 
     */
    @PUT
    @Path("retrieveTaxpayerMemoListForTaxpayerCaseSearchResultList/{accountName}/{password}/{licenseKey}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response retrieveTaxpayerMemoListForTaxpayerCaseSearchResultList(@PathParam("accountName") String accountName, 
                                                                @PathParam("password") String password, 
                                                                @PathParam("licenseKey") String licenseKey, 
                                                                TaxpayerCaseSearchResultList taxpayerCaseSearchResultList)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().retrieveTaxpayerMemoListForTaxpayerCaseSearchResultList(taxpayerCaseSearchResultList)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    /**
     * @deprecated - performance issue
     * @param accountName
     * @param password
     * @param licenseKey
     * @param taxpayerCaseSearchResultList
     * @return 
     */
    @PUT
    @Path("retrievePeonyBillPaymentListForTaxpayerCaseSearchResultList/{accountName}/{password}/{licenseKey}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response retrievePeonyBillPaymentListForTaxpayerCaseSearchResultList(@PathParam("accountName") String accountName, 
                                                                @PathParam("password") String password, 
                                                                @PathParam("licenseKey") String licenseKey, 
                                                                TaxpayerCaseSearchResultList taxpayerCaseSearchResultList)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().retrievePeonyBillPaymentListForTaxpayerCaseSearchResultList(taxpayerCaseSearchResultList)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storePeonyTaxpayerCase/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePeonyTaxpayerCase(@PathParam("accountName") String accountName, 
                                            @PathParam("password") String password, 
                                            @PathParam("licenseKey") String licenseKey, 
                                            PeonyTaxpayerCase aPeonyTaxpayerCase)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().storePeonyTaxpayerCase(aPeonyTaxpayerCase)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @PUT
    @Path("storePeonyTaxpayerCaseBasicInformation/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePeonyTaxpayerCaseBasicInformation(@PathParam("accountName") String accountName, 
                                                        @PathParam("password") String password, 
                                                        @PathParam("licenseKey") String licenseKey, 
                                                        PeonyTaxpayerCase aPeonyTaxpayerCase)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().storePeonyTaxpayerCaseBasicInformation(aPeonyTaxpayerCase)
                         ).build();
            }catch(Exception ex){
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
     * @param personalPropertyUUid
     * @return - if deletion is successful, the entity contails NULL. Otherwise 
     * (i.e. failed), a G02TaxpayerCase instance whose UUID is personalPropertyUUid 
     * will be embedded in the entity
     */
    @DELETE
    @Path("deletePersonalProperty/{accountName}/{password}/{licenseKey}/{personalPropertyUUid}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deletePersonalProperty(@PathParam("accountName") String accountName, 
                                            @PathParam("password") String password, 
                                            @PathParam("licenseKey") String licenseKey, 
                                            @PathParam("personalPropertyUUid") String personalPropertyUUid)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().deletePersonalProperty(personalPropertyUUid)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxpayerService.class.getName()).log(Level.SEVERE, null, ex);
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
     * @param tlcLicenseUUid
     * @return - if deletion is successful, the entity contails NULL. Otherwise 
     * (i.e. failed), a G02TaxpayerCase instance whose UUID is tlcLicenseUUid 
     * will be embedded in the entity
     */
    @DELETE
    @Path("deleteTlcLicense/{accountName}/{password}/{licenseKey}/{tlcLicenseUUid}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteTlcLicense(@PathParam("accountName") String accountName, 
                                            @PathParam("password") String password, 
                                            @PathParam("licenseKey") String licenseKey, 
                                            @PathParam("tlcLicenseUUid") String tlcLicenseUUid)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().deleteTlcLicense(tlcLicenseUUid)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxpayerService.class.getName()).log(Level.SEVERE, null, ex);
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
     * @param personalBusinessPropertyUUid
     * @return - if deletion is successful, the entity contails NULL. Otherwise 
     * (i.e. failed), a G02TaxpayerCase instance whose UUID is personalBusinessPropertyUUid 
     * will be embedded in the entity
     */
    @DELETE
    @Path("deletePersonalBusinessProperty/{accountName}/{password}/{licenseKey}/{personalBusinessPropertyUUid}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deletePersonalBusinessProperty(@PathParam("accountName") String accountName, 
                                            @PathParam("password") String password, 
                                            @PathParam("licenseKey") String licenseKey, 
                                            @PathParam("personalBusinessPropertyUUid") String personalBusinessPropertyUUid)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().deletePersonalBusinessProperty(personalBusinessPropertyUUid)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxpayerService.class.getName()).log(Level.SEVERE, null, ex);
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
     * @param taxpayerUserUuid
     * @return - if deletion is successful, the entity contails NULL. Otherwise 
     * (i.e. failed), a G02TaxpayerCase instance whose UUID is taxpayerUserUuid 
     * will be embedded in the entity
     */
    @DELETE
    @Path("deleteTaxpayerInfo/{accountName}/{password}/{licenseKey}/{taxpayerUserUuid}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteTaxpayerInfo(@PathParam("accountName") String accountName, 
                                            @PathParam("password") String password, 
                                            @PathParam("licenseKey") String licenseKey, 
                                            @PathParam("taxpayerUserUuid") String taxpayerUserUuid)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().deleteTaxpayerInfo(taxpayerUserUuid)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxpayerService.class.getName()).log(Level.SEVERE, null, ex);
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
     * @param taxpayerCaseUuid
     * @return - if deletion is successful, the entity contails NULL. Otherwise 
     * (i.e. failed), a G02TaxpayerCase instance whose UUID is taxpayerCaseUuid 
     * will be embedded in the entity
     */
    @DELETE
    @Path("deleteTaxpayerCase/{accountName}/{password}/{licenseKey}/{taxpayerCaseUuid}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteTaxpayerCase(@PathParam("accountName") String accountName, 
                                            @PathParam("password") String password, 
                                            @PathParam("licenseKey") String licenseKey, 
                                            @PathParam("taxpayerCaseUuid") String taxpayerCaseUuid)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxpayerEJB().removeTaxpayerCase(taxpayerCaseUuid)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxpayerService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
}
