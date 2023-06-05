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

import com.zcomapproach.garden.data.constant.SearchTaxcorpCriteria;
import com.zcomapproach.garden.data.constant.TaxcorpEntityDateField;
import com.zcomapproach.garden.exception.GardenEntityNotFound;
import com.zcomapproach.garden.persistence.constant.TaxFilingPeriod;
import com.zcomapproach.garden.persistence.constant.TaxFilingType;
import com.zcomapproach.garden.persistence.peony.PeonyTaxcorpCase;
import com.zcomapproach.garden.persistence.peony.data.TaxcorpTaxFilingCaseSearchResultList;
import com.zcomapproach.garden.rose.rest.AbstractRestService;
import com.zcomapproach.commons.ZcaRegex;
import java.util.Date;
import java.util.HashMap;
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
@Path("taxcorp")    //refer to RoseWebResoureRoot.value()
public class RoseTaxcorpService extends AbstractRestService{
    
    /**
     * 
     * @param accountName
     * @param password
     * @param licenseKey
     * @param taxcorpCaseUuid
     * @param finalizedDateValue
     * @return - if deletion is successful, the entity contails NULL. Otherwise 
     * (i.e. failed), a G02TaxcorpCase instance whose UUID is taxcorpCaseUuid 
     * will be embedded in the entity
     */
    @DELETE
    @Path("finalizeTaxcorpCase/{accountName}/{password}/{licenseKey}/{taxcorpCaseUuid}/{finalizedDateValue}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response finalizeTaxcorpCase(@PathParam("accountName") String accountName, 
                                            @PathParam("password") String password, 
                                            @PathParam("licenseKey") String licenseKey, 
                                            @PathParam("taxcorpCaseUuid") String taxcorpCaseUuid, 
                                            @PathParam("finalizedDateValue") long finalizedDateValue)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                Date finalizedDate = new Date();
                finalizedDate.setTime(finalizedDateValue);
                result = Response.ok(
                            getTaxcorpEJB().finalizeTaxcorpCase(taxcorpCaseUuid, finalizedDate)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @DELETE
    @Path("rollbackFinalizedTaxcorpCase/{accountName}/{password}/{licenseKey}/{taxcorpCaseUuid}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response rollbackFinalizedTaxcorpCase(@PathParam("accountName") String accountName, 
                                            @PathParam("password") String password, 
                                            @PathParam("licenseKey") String licenseKey, 
                                            @PathParam("taxcorpCaseUuid") String taxcorpCaseUuid)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxcorpEJB().rollbackFinalizedTaxcorpCase(taxcorpCaseUuid)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    /**
     * The client-side should guaranttee sufficient data for G02TaxcorpCase/Customer/Personnel 
     * for this operation. Otherwise, this service will do nothing quietly
     * 
     * @param accountName
     * @param password
     * @param licenseKey
     * @param aPeonyTaxcorpCase - client-side should guaranttee sufficient data for G02TaxcorpCase/Customer/Personnel for this operation
     * @return - if this operation failed, NULL returned
     */
    @PUT
    @Path("storePeonyTaxcorpCaseBasicInformationAndPersonnel/{accountName}/{password}/{licenseKey}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response storePeonyTaxcorpCaseBasicInformationAndPersonnel(@PathParam("accountName") String accountName, 
                                                                @PathParam("password") String password, 
                                                                @PathParam("licenseKey") String licenseKey, 
                                                                PeonyTaxcorpCase aPeonyTaxcorpCase)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxcorpEJB().storePeonyTaxcorpCaseBasicInformationAndPersonnel(aPeonyTaxcorpCase)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("searchTaxcorpCaseSearchResultListByDateRange/{accountName}/{password}/{licenseKey}/{taxcorpDateFeature}/{fromTimeValue}/{toTimeValue}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response searchTaxcorpCaseSearchResultListByDateRange(@PathParam("accountName") String accountName, 
                                                                        @PathParam("password") String password, 
                                                                        @PathParam("licenseKey") String licenseKey, 
                                                                        @PathParam("taxcorpDateFeature") String taxcorpDateFeature, 
                                                                        @PathParam("fromTimeValue") long fromTimeValue, 
                                                                        @PathParam("toTimeValue") long toTimeValue)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                Date fromDate = new Date();
                fromDate.setTime(fromTimeValue);
                Date toDate = new Date();
                toDate.setTime(toTimeValue);
                
                result = Response.ok(
                            getTaxcorpEJB().searchTaxcorpCaseSearchResultListByDateRange(
                                    TaxcorpEntityDateField.convertEnumNameToType(taxcorpDateFeature, false).value(), 
                                    fromDate, toDate)
                         ).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("findTaxcorpCaseBriefListByBillPeriod/{accountName}/{password}/{licenseKey}/{fromDateValue}/{toDateValue}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findTaxcorpCaseBriefListByBillPeriod(@PathParam("accountName") String accountName, 
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
                result = Response.ok(getTaxcorpEJB().findTaxcorpCaseBriefListByBillPeriod(fromDate, toDate)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("searchTaxcorpTaxFilingCaseSearchResultListByDeadlineRange/{accountName}/{password}/{licenseKey}/{taxFilingTypeName}/{taxFilingPeriodName}/{fromTimeValue}/{toTimeValue}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response searchTaxcorpTaxFilingCaseSearchResultListByDeadlineRange(@PathParam("accountName") String accountName, 
                                                                        @PathParam("password") String password, 
                                                                        @PathParam("licenseKey") String licenseKey, 
                                                                        @PathParam("taxFilingTypeName") String taxFilingTypeName, 
                                                                        @PathParam("taxFilingPeriodName") String taxFilingPeriodName, 
                                                                        @PathParam("fromTimeValue") long fromTimeValue, 
                                                                        @PathParam("toTimeValue") long toTimeValue)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                String taxFilingType = TaxFilingType.convertEnumNameToType(taxFilingTypeName, false).value();
                String taxFilingPeriod = TaxFilingPeriod.convertEnumNameToType(taxFilingPeriodName, false).value();
                Date fromDate = new Date();
                fromDate.setTime(fromTimeValue);
                Date toDate = new Date();
                toDate.setTime(toTimeValue);
                
                HashMap<String, PeonyTaxcorpCase> cache = new HashMap<>();
                //search by deadline
                TaxcorpTaxFilingCaseSearchResultList resultTaxcorpTaxFilingCaseSearchResultList = getTaxcorpEJB()
                        .searchTaxcorpTaxFilingCaseSearchResultListByDeadlineRange(taxFilingType, taxFilingPeriod, fromDate, toDate, cache);
////                //search by extension for tax-return
////                TaxcorpTaxFilingCaseSearchResultList aTaxcorpTaxFilingCaseSearchResultList = getTaxcorpEJB()
////                        .searchTaxcorpTaxFilingCaseSearchResultListByExtensionDateRange(taxFilingType, taxFilingPeriod, fromDate, toDate, cache);
////                if (aTaxcorpTaxFilingCaseSearchResultList != null){
////                    resultTaxcorpTaxFilingCaseSearchResultList.getTaxcorpTaxFilingCaseSearchResultList().addAll(aTaxcorpTaxFilingCaseSearchResultList.getTaxcorpTaxFilingCaseSearchResultList());
////                }
                cache.clear();
                result = Response.ok(resultTaxcorpTaxFilingCaseSearchResultList).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("searchFinalizedTaxcorpTaxFilingCaseSearchResultListByDeadlineRange/{accountName}/{password}/{licenseKey}/{taxFilingTypeName}/{taxFilingPeriodName}/{fromTimeValue}/{toTimeValue}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response searchFinalizedTaxcorpTaxFilingCaseSearchResultListByDeadlineRange(@PathParam("accountName") String accountName, 
                                                                        @PathParam("password") String password, 
                                                                        @PathParam("licenseKey") String licenseKey, 
                                                                        @PathParam("taxFilingTypeName") String taxFilingTypeName, 
                                                                        @PathParam("taxFilingPeriodName") String taxFilingPeriodName, 
                                                                        @PathParam("fromTimeValue") long fromTimeValue, 
                                                                        @PathParam("toTimeValue") long toTimeValue)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                String taxFilingType = TaxFilingType.convertEnumNameToType(taxFilingTypeName, false).value();
                String taxFilingPeriod = TaxFilingPeriod.convertEnumNameToType(taxFilingPeriodName, false).value();
                Date fromDate = new Date();
                fromDate.setTime(fromTimeValue);
                Date toDate = new Date();
                toDate.setTime(toTimeValue);
                
                HashMap<String, PeonyTaxcorpCase> cache = new HashMap<>();
                //search by deadline
                TaxcorpTaxFilingCaseSearchResultList resultTaxcorpTaxFilingCaseSearchResultList = getTaxcorpEJB()
                        .searchFinalizedTaxcorpTaxFilingCaseSearchResultListByDeadlineRange(taxFilingType, taxFilingPeriod, fromDate, toDate, cache);
////                //search by extension for tax-return
////                TaxcorpTaxFilingCaseSearchResultList aTaxcorpTaxFilingCaseSearchResultList = getTaxcorpEJB()
////                        .searchTaxcorpTaxFilingCaseSearchResultListByExtensionDateRange(taxFilingType, taxFilingPeriod, fromDate, toDate, cache);
////                if (aTaxcorpTaxFilingCaseSearchResultList != null){
////                    resultTaxcorpTaxFilingCaseSearchResultList.getTaxcorpTaxFilingCaseSearchResultList().addAll(aTaxcorpTaxFilingCaseSearchResultList.getTaxcorpTaxFilingCaseSearchResultList());
////                }
                cache.clear();
                result = Response.ok(resultTaxcorpTaxFilingCaseSearchResultList).build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
    @GET
    @Path("searchTaxcorpCaseSearchResultListByFeature/{accountName}/{password}/{licenseKey}/{taxcorpFeatureName}/{taxcorpFeatureValue}/{exactMatch}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response searchTaxcorpCaseSearchResultListByFeature(@PathParam("accountName") String accountName, 
                                                                        @PathParam("password") String password, 
                                                                        @PathParam("licenseKey") String licenseKey, 
                                                                        @PathParam("taxcorpFeatureName") String taxcorpFeatureName, 
                                                                        @PathParam("taxcorpFeatureValue") String taxcorpFeatureValue, 
                                                                        @PathParam("exactMatch") boolean exactMatch)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxcorpEJB().searchTaxcorpCaseSearchResultListByFeature(
                                    SearchTaxcorpCriteria.convertEnumNameToType(taxcorpFeatureName, false).value(), 
                                    taxcorpFeatureValue, exactMatch)
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
     * @param taxcorpCaseUuid
     * @return - if not found, NULL returned
     */
    @GET
    @Path("findTaxcorpCaseByTaxcorpCaseUuid/{accountName}/{password}/{licenseKey}/{taxcorpCaseUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findTaxcorpCaseByTaxcorpCaseUuid(@PathParam("accountName") String accountName, 
                                                    @PathParam("password") String password, 
                                                    @PathParam("licenseKey") String licenseKey, 
                                                    @PathParam("taxcorpCaseUuid") String taxcorpCaseUuid) 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getTaxcorpEJB().findTaxcorpCaseByTaxcorpCaseUuid(taxcorpCaseUuid)).build();
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
     * @param taxcorpCaseUuid
     * @return - if not found, NULL returned
     */
    @GET
    @Path("findPeonyTaxcorpCaseByTaxcorpCaseUuid/{accountName}/{password}/{licenseKey}/{taxcorpCaseUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyTaxcorpCaseByTaxcorpCaseUuid(@PathParam("accountName") String accountName, 
                                                    @PathParam("password") String password, 
                                                    @PathParam("licenseKey") String licenseKey, 
                                                    @PathParam("taxcorpCaseUuid") String taxcorpCaseUuid) 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getTaxcorpEJB().findPeonyTaxcorpCaseByTaxcorpCaseUuid(taxcorpCaseUuid)).build();
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
     * @param ein
     * @return - if not found, NULL returned
     */
    @GET
    @Path("findTaxcorpCaseByEinNumber/{accountName}/{password}/{licenseKey}/{ein}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findTaxcorpCaseByEinNumber(@PathParam("accountName") String accountName, 
                                                    @PathParam("password") String password, 
                                                    @PathParam("licenseKey") String licenseKey, 
                                                    @PathParam("ein") String ein) 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getTaxcorpEJB().findTaxcorpCaseByEinNumber(ein)).build();
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
     * @param ein
     * @return - if not found, NULL returned
     */
    @GET
    @Path("findPeonyTaxcorpCaseByEinNumber/{accountName}/{password}/{licenseKey}/{ein}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyTaxcorpCaseByEinNumber(@PathParam("accountName") String accountName, 
                                                    @PathParam("password") String password, 
                                                    @PathParam("licenseKey") String licenseKey, 
                                                    @PathParam("ein") String ein) 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getTaxcorpEJB().findPeonyTaxcorpCaseByEinNumber(ein)).build();
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
     * @param corpName
     * @return - if not found, NULL returned
     */
    @GET
    @Path("findPeonyTaxcorpCaseListByCorpName/{accountName}/{password}/{licenseKey}/{corpName}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response findPeonyTaxcorpCaseListByCorpName(@PathParam("accountName") String accountName, 
                                                    @PathParam("password") String password, 
                                                    @PathParam("licenseKey") String licenseKey, 
                                                    @PathParam("corpName") String corpName) 
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getTaxcorpEJB().findPeonyTaxcorpCaseListByCorpName(corpName)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }

    @GET
    @Path("generatePeonyTaxFilingCaseListForTaxcorp/{accountName}/{password}/{licenseKey}/{taxcorpUuid}/{taxFilingType}/{taxFilingPeriod}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response generatePeonyTaxFilingCaseListForTaxcorp(@PathParam("accountName") String accountName, 
                                                            @PathParam("password") String password, 
                                                            @PathParam("licenseKey") String licenseKey, 
                                                            @PathParam("taxcorpUuid") String taxcorpUuid, 
                                                            @PathParam("taxFilingType") String taxFilingType, 
                                                            @PathParam("taxFilingPeriod") String taxFilingPeriod)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try {
                result = Response.ok(getTaxcorpEJB().generatePeonyTaxFilingCaseListForTaxcorp(taxcorpUuid, taxFilingType, taxFilingPeriod)).build();
            } catch (Exception ex) {
                //Logger.getLogger(RoseTaxcorpService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    
    }
    
    /**
     * This is equivalent to finalize Taxcorp case at "today"
     * @param accountName
     * @param password
     * @param licenseKey
     * @param taxcorpCaseUuid
     * @return - if deletion is successful, the entity contails NULL. Otherwise 
     * (i.e. failed), a G02TaxcorpCase instance whose UUID is taxpayerCaseUuid 
     * will be embedded in the entity
     */
    @DELETE
    @Path("deleteTaxcorpCase/{accountName}/{password}/{licenseKey}/{taxcorpCaseUuid}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteTaxcorpCase(@PathParam("accountName") String accountName, 
                                            @PathParam("password") String password, 
                                            @PathParam("licenseKey") String licenseKey, 
                                            @PathParam("taxcorpCaseUuid") String taxcorpCaseUuid)
    {
        Response result = super.verifyCredentials(accountName, password, licenseKey);
        
        if (result.getStatus() == Response.Status.OK.getStatusCode()){
            try{
                result = Response.ok(
                            getTaxcorpEJB().finalizeTaxcorpCase(taxcorpCaseUuid, new Date()))
                        .build();
            }catch(Exception ex){
                //Logger.getLogger(RoseTaxpayerService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity(ex.getMessage()).build();
            }
        }
        return result;
    }
    
}
