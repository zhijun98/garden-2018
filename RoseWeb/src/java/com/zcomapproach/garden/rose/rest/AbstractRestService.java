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

package com.zcomapproach.garden.rose.rest;

import com.zcomapproach.garden.data.GardenFlower;
import com.zcomapproach.garden.data.GardenFlowerOwner;
import com.zcomapproach.garden.guard.RoseWebCipher;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.rose.persistence.RoseBusinessEJB02;
import com.zcomapproach.garden.rose.persistence.RoseCustomerEJB02;
import com.zcomapproach.garden.rose.persistence.RoseManagementEJB02;
import com.zcomapproach.garden.rose.persistence.RoseRuntimeEJB02;
import com.zcomapproach.garden.rose.persistence.RoseTaxcorpEJB02;
import com.zcomapproach.garden.rose.persistence.RoseTaxpayerEJB02;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.rose.persistence.TulipServiceEJB02;
import java.util.HashMap;
import javax.ejb.EJB;
import javax.ws.rs.core.Response;

/**
 *
 * @author zhijun98
 */
public abstract class AbstractRestService {
    @EJB
    private TulipServiceEJB02 tulipEJB;
    
    @EJB
    private RoseRuntimeEJB02 runtimeEJB;
    
    @EJB
    private RoseManagementEJB02 managementEJB;
    
    @EJB
    private RoseBusinessEJB02 businessEJB;

    @EJB
    private RoseCustomerEJB02 customerEJB;
    
    @EJB
    private RoseTaxpayerEJB02 taxpayerEJB;
    
    @EJB
    private RoseTaxcorpEJB02 taxcorpEJB;
    /**
     * todo-zzj: is it proper to do it here in this way? This static storage hold rest-user's credentials, which was loaded 
     * once, for performance efficiency in REST-service access. If rest-user's credentials (account name and password with 
     * optional licensekey) were verified, it exists in this storage. Key is the rest-user's account login name, value is 
     * the corresponding G02Account instance.
     * <p>
     * Currently, all rest-users are employees. The consumed memory size is very limited.
     */
    private static final HashMap<String, PeonyEmployee> restUserCredentials = new HashMap<>();
    
    /**
     * 
     * @param flowerName
     * @param flowerOwner
     * @param licenseKey
     * @return 
     */
    protected Response verifyFlowerCredentials(String flowerName, String flowerOwner, String licenseKey){
        Response result = null;
        if (ZcaValidator.isNullEmpty(flowerName)){
            result = Response.status(Response.Status.NOT_ACCEPTABLE).entity("Bad flower name.").build();
        }else if (ZcaValidator.isNullEmpty(flowerOwner)){
            result = Response.status(Response.Status.NOT_ACCEPTABLE).entity("Bad flower owner.").build();
        }else if (ZcaValidator.isNullEmpty(licenseKey)){
            result = Response.status(Response.Status.NOT_ACCEPTABLE).entity("Bad license key for authetication and authorization.").build();
        }
        PeonyEmployee symbolEmployee = null;
        if (result == null){
            GardenFlower aGardenFlower = GardenFlower.convertEnumNameToType(flowerName);
            GardenFlowerOwner aGardenFlowerOwner = GardenFlowerOwner.convertEnumNameToType(flowerOwner);
            if (GardenFlowerOwner.getLicenseKey(aGardenFlower, aGardenFlowerOwner).equalsIgnoreCase(licenseKey)){
                symbolEmployee = new PeonyEmployee();
            }
        }
        if (symbolEmployee == null){
            result = Response.status(Response.Status.NOT_ACCEPTABLE).entity("Bad flower credentials.").build();
        }else{
            result = Response.ok(symbolEmployee).build();
        }
        return result;
    }

    /**
     * 
     * @param loginName
     * @param password
     * @param licenseKey - only verified as verifing loginName and password
     * @return 
     */
    protected Response verifyCredentials(String loginName, String password, String licenseKey){
        Response result = null;
        if (ZcaValidator.isNullEmpty(loginName)){
            result = Response.status(Response.Status.NOT_ACCEPTABLE).entity("Bad login credentials.").build();
        }else if (ZcaValidator.isNullEmpty(password)){
            result = Response.status(Response.Status.NOT_ACCEPTABLE).entity("Bad login credentials.").build();
        }else if (ZcaValidator.isNullEmpty(licenseKey)){
            result = Response.status(Response.Status.NOT_ACCEPTABLE).entity("Bad license key for authetication and authorization.").build();
        }
        PeonyEmployee aPeonyLoginEmployee = null;
        if (result == null){
            synchronized (restUserCredentials){
                if (restUserCredentials.containsKey(loginName)){
                    aPeonyLoginEmployee = restUserCredentials.get(loginName);
                    if (!RoseWebCipher.getSingleton().encrypt(password).equalsIgnoreCase(aPeonyLoginEmployee.getAccount().getEncryptedPassword())){
                        aPeonyLoginEmployee = null;
                    }
                }else{
                    aPeonyLoginEmployee = managementEJB.findPeonyEmployeeByCredentials(loginName, password);
                    if ((aPeonyLoginEmployee != null) && (aPeonyLoginEmployee.getEmployeeInfo() != null)){
                        restUserCredentials.put(loginName, aPeonyLoginEmployee);
                    }
                }
            }
        }
        if ((aPeonyLoginEmployee == null) || (aPeonyLoginEmployee.getEmployeeInfo() == null)){
            result = Response.status(Response.Status.NOT_ACCEPTABLE).entity("Bad login credentials.").build();
        }else{
            result = Response.ok(aPeonyLoginEmployee).build();
        }
        return result;
    }

    protected RoseBusinessEJB02 getBusinessEJB() {
        return businessEJB;
    }

    protected RoseCustomerEJB02 getCustomerEJB() {
        return customerEJB;
    }

    protected RoseTaxpayerEJB02 getTaxpayerEJB() {
        return taxpayerEJB;
    }

    protected RoseTaxcorpEJB02 getTaxcorpEJB() {
        return taxcorpEJB;
    }

    protected RoseManagementEJB02 getManagementEJB() {
        return managementEJB;
    }

    public RoseRuntimeEJB02 getRuntimeEJB() {
        return runtimeEJB;
    }

    public TulipServiceEJB02 getTulipEJB() {
        return tulipEJB;
    }

}
