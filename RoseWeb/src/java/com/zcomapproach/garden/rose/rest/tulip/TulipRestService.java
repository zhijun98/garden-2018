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

package com.zcomapproach.garden.rose.rest.tulip;

import com.zcomapproach.commons.ZcaRegex;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.rose.rest.tulip.data.TulipUser;
import com.zcomapproach.garden.rose.rest.AbstractRestService;
import com.zcomapproach.garden.rose.rest.tulip.data.TulipUserMessage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
@Path("tulip")
public class TulipRestService extends AbstractRestService{
    
    @POST
    @Path("login")
    @Produces({MediaType.APPLICATION_JSON})
    public Response login(TulipUser user) {
        Response result;
        if (user != null) {
            try{
                //register the new account
                user = super.getTulipEJB().loginTulipUserAccount(user.getUsername(), user.getPassword());
                if (user.isErrorRaised()){
                    result = Response.accepted(user).build();
                }else{
                    result = Response.ok(user).build();
                }
            }catch(Exception ex){
                Logger.getLogger(TulipRestService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity("Cannot log into the system for now. Please try it later.").build();
            }
        }else{
            result = Response.serverError().entity("Unexpected request and the server-side refused the operation.").build();
        }
        return result;
    }
    
    @POST
    @Path("signup")
    @Produces({MediaType.APPLICATION_JSON})
    public Response signup(TulipUser user) {
        Response result;
        if (user != null) {
            try{
                if (!ZcaRegex.isValidPhoneNumber(user.getMobileNumber())){
                    user.setErrorMsg("Mobile number is not valid.");
                    result = Response.accepted(user).build();
                }else{
                    //register the new account
                    user = super.getTulipEJB().registerTulipUserAccount(user);
                    if (user.isErrorRaised()){
                        result = Response.accepted(user).build();
                    }else{
                        //send confirm code...(after user confirmed it on the tulip side, the peony office will broadcast it.)
                        super.getRuntimeEJB().sendSmsText(user.getMobileNumber(), 
                                "Yin Lu Accounting Firm: your confirm-code is " + user.getConfirmCode() + ". Thanks for your registration.", false);
                        user.setConfirmCode("");    //prohibit the remote user having it without mobile
                        result = Response.ok(user).build();
                    }
                }
            }catch(Exception ex){
                Logger.getLogger(TulipRestService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity("Cannot register for now. Please try it later.").build();
            }
        }else{
            result = Response.serverError().entity("Unexpected request and the server-side refused the operation.").build();
        }
        return result;
    }
    
    @POST
    @Path("confirm")
    @Produces({MediaType.APPLICATION_JSON})
    public Response confirm(TulipUser user) {
        Response result;
        if (user != null) {
            try{
                //register the new account
                user = super.getTulipEJB().confirmTulipUserAccount(user.getUuid(), user.getConfirmCode());
                if (user.isErrorRaised()){
                    result = Response.accepted(user).build();
                }else{
                    result = Response.ok(user).build();
                }
            }catch(Exception ex){
                Logger.getLogger(TulipRestService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity("Cannot register for now. Please try it later.").build();
            }
        }else{
            result = Response.serverError().entity("Unexpected request and the server-side refused the operation.").build();
        }
        return result;
    }
    
    @POST
    @Path("postMessage")
    @Produces({MediaType.APPLICATION_JSON})
    public Response postMessage(TulipUserMessage tulipUserMessage) {
        Response result;
        if (tulipUserMessage != null) {
            
            
            
            return Response.ok(tulipUserMessage).build();
////            try{
////                //register the new account
////                tulipUserMessage = super.getTulipEJB().storeTulipUserMessage(tulipUserMessage);
////                if (tulipUserMessage.isErrorRaised()){
////                    result = Response.accepted(tulipUserMessage).build();
////                }else{
////                    result = Response.ok(tulipUserMessage).build();
////                }
////            }catch(Exception ex){
////                Logger.getLogger(TulipRestService.class.getName()).log(Level.SEVERE, null, ex);
////                result = Response.serverError().entity("Cannot register for now. Please try it later.").build();
////            }
        }else{
            result = Response.serverError().entity("Unexpected request and the server-side refused the operation.").build();
        }
        return result;
    }
    
    @GET
    @Path("retrieveNotifications/{userUuid}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response retrieveNotifications(@PathParam("userUuid") String userUuid)
    {
        Response result;
        if (ZcaValidator.isNotNullEmpty(userUuid)) {
            try{
                result = Response.ok(super.getTulipEJB().retrieveNotifications(userUuid)).build();
            }catch(Exception ex){
                Logger.getLogger(TulipRestService.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().entity("Cannot retrieve notitification for now. Please try it later.").build();
            }
        }else{
            result = Response.serverError().entity("Unexpected request and the server-side refused the operation.").build();
        }
        return result;
    }

}
