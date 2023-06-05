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

package com.zcomapproach.garden.peony.utils;

import com.zcomapproach.commons.ZcaRegex;
import static com.zcomapproach.commons.ZcaRegex.findMatch;
import com.zcomapproach.commons.ZcaText;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.email.GardenEmailMessage;
import com.zcomapproach.garden.email.GardenEmailSerializer;
import com.zcomapproach.garden.peony.kernel.data.PeonySmsContactor;
import com.zcomapproach.garden.persistence.constant.GardenContactType;
import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G02BusinessContactor;
import com.zcomapproach.garden.persistence.entity.G02ContactInfo;
import com.zcomapproach.garden.persistence.entity.G02Location;
import com.zcomapproach.garden.persistence.entity.G02OfflineEmail;
import com.zcomapproach.garden.persistence.entity.G02TaxpayerInfo;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmail;
import com.zcomapproach.garden.persistence.peony.TaxcorpCaseBrief;
import com.zcomapproach.garden.persistence.peony.TaxpayerCaseBrief;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * 
 * @author zhijun98
 */
public class PeonyDataUtils {

    public static String generateDataTitle(TaxpayerCaseBrief aTaxpayerCaseBrief) {
        return "Primary Taxpayer: " + aTaxpayerCaseBrief.getCustomerFirstName() 
                        + " " + aTaxpayerCaseBrief.getCustomerFirstName() 
                        + " ["+ aTaxpayerCaseBrief.getDeadlineText() 
                        + " - " + aTaxpayerCaseBrief.getFederalFilingStatus()+"]";
    }

    public static String generateDataTitle(TaxcorpCaseBrief aTaxcorpCaseBrief) {
        return "Taxcorp name: " + aTaxcorpCaseBrief.getCorporateName() + " ["+aTaxcorpCaseBrief.getEinNumber()+"]";
    }
    
    public static GardenEmailMessage convertToGardenEmailMessageByDeserialization(PeonyOfflineEmail aPeonyOfflineEmail){
        return convertToGardenEmailMessageByDeserialization(aPeonyOfflineEmail.getOfflineEmail());
    }
    
    public static GardenEmailMessage convertToGardenEmailMessageByDeserialization(G02OfflineEmail aG02OfflineEmail){
        if (aG02OfflineEmail == null){
            return null;
        }
        try{
            return GardenEmailSerializer.getSingleton().deserializeToGardenEmailMessage(Paths.get(aG02OfflineEmail.getMessagePath()));
        }catch (Exception ex){
            return null;
        }
    }
    
    public static String translateTechnicalMessage(String msg){
        if (ZcaValidator.isNullEmpty(msg)){
            return "This operation failed. No reason available.";
        }else if (msg.contains("javax.ws.rs.InternalServerErrorException")){
            return "Web server is experiencing technical difficulties.";
        }else if (msg.contains("java.net.ConnectException") || msg.contains("javax.ws.rs.NotFoundException")){
            return "No internet connection. Connection refused.";
        }
        return msg;
    }

    public static boolean isSystemFolderNameFormat(String text) {
        if (text == null){
            return false;
        }
        return ZcaRegex.findMatch(text, "^[A-Za-z0-9]+$");
    }
    
    public static boolean isEinFormat(String text){
        return ZcaRegex.findMatch(text, "^[0-9]{2}-[0-9]{7}$");
    }
    
    public static boolean isSsnFormat(String text){
        if (text == null){
            return false;
        }
        return findMatch(text, "^[0-9]{3}-[0-9]{2}-[0-9]{4}$");
    }
    
    /**
     * 
     * @param firstName
     * @param lastName
     * @return - "lastName, firstName"
     */
    public static String createFullName(String firstName, String lastName){
        lastName = ZcaText.denullize(lastName);
        firstName = ZcaText.denullize(firstName);
        if (lastName.isEmpty()){
            return firstName;
        }else{
            if (firstName.isEmpty()){
                return lastName;
            }else{
                return lastName + ", " + firstName;
            }
        }
    }
    
    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
           InternetAddress emailAddr = new InternetAddress(email);
           emailAddr.validate();
        } catch (AddressException ex) {
           result = false;
        }
        return result;
    }
    
    /**
     * if some of contactors have no email, they will not be included into the result-list.
     * @param taxpayers
     * @return 
     */
    public static List<Address> retrieveEmailAddressListFromBusinessContactorList(List<G02BusinessContactor> contactors) {
        List<Address> result = new ArrayList<>();
        if (contactors != null){
            for (G02BusinessContactor contactor : contactors){
                if (GardenContactType.EMAIL.value().equalsIgnoreCase(contactor.getContactType())){
                    try {
                        result.add(new InternetAddress(contactor.getContactInfo()));
                    } catch (AddressException ex) {
                        //Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * if some of taxpayers have no email, they will not be included into the result-list.
     * @param taxpayers
     * @return 
     */
    public static List<Address> retrieveEmailAddressListFromTaxpayerInfoList(List<G02TaxpayerInfo> taxpayers) {
        List<Address> result = new ArrayList<>();
        if (taxpayers != null){
            String email;
            for (G02TaxpayerInfo taxpayer : taxpayers){
                email = taxpayer.getEmail();
                if (ZcaValidator.isNotNullEmpty(email) && isValidEmailAddress(email)){
                    try {
                        result.add(new InternetAddress(email));
                    } catch (AddressException ex) {
                        //Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * if some of taxpayers have no SMS, they will not be included into the result-list.
     * @param taxpayers
     * @return 
     */
    public static List<PeonySmsContactor> retrievePeonySmsContactorListFromBusinessContactorList(List<G02BusinessContactor> contactors, 
                                                                                                 GardenEntityType aGardenEntityType,
                                                                                                 String entityUuid) 
    {
        if ((contactors == null) || (contactors.isEmpty())){
            return new ArrayList<>();
        }
        List<PeonySmsContactor> aPeonySmsContactorList = new ArrayList<>();
        PeonySmsContactor aPeonySmsContactor;
        for (G02BusinessContactor contactor : contactors){
            if (GardenContactType.MOBILE_PHONE.value().equalsIgnoreCase(contactor.getContactType())){
                aPeonySmsContactor = new PeonySmsContactor();
                aPeonySmsContactor.setContactorName(contactor.getLastName() + "," + contactor.getFirstName());
                aPeonySmsContactor.setEntityType(aGardenEntityType.name());
                aPeonySmsContactor.setEntityUuid(entityUuid);
                aPeonySmsContactor.setMobileNumber(contactor.getContactInfo());
                
                aPeonySmsContactorList.add(aPeonySmsContactor);
            }
        }
        return aPeonySmsContactorList;
    }

    /**
     * if some of taxpayers have no SMS, they will not be included into the result-list.
     * @param taxpayers
     * @return 
     */
    public static List<PeonySmsContactor> retrievePeonySmsContactorListFromTaxpayerInfoList(List<G02TaxpayerInfo> taxpayers) {
        if ((taxpayers == null) || (taxpayers.isEmpty())){
            return new ArrayList<>();
        }
        List<PeonySmsContactor> aPeonySmsContactorList = new ArrayList<>();
        PeonySmsContactor aPeonySmsContactor;
        for (G02TaxpayerInfo taxpayer : taxpayers){
            if (ZcaValidator.isNotNullEmpty(taxpayer.getMobilePhone())){
                aPeonySmsContactor = new PeonySmsContactor();
                aPeonySmsContactor.setContactorName(taxpayer.getLastName() + "," + taxpayer.getFirstName());
                aPeonySmsContactor.setEntityType(GardenEntityType.TAXPAYER_INFO.name());
                aPeonySmsContactor.setEntityUuid(taxpayer.getTaxpayerUserUuid());
                aPeonySmsContactor.setMobileNumber(taxpayer.getMobilePhone());
                
                aPeonySmsContactorList.add(aPeonySmsContactor);
            }
        }
        return aPeonySmsContactorList;
    }

    public static String generateDataTitle(G02ContactInfo contactInfo) {
        String result = "";
        if (contactInfo != null){
            if (ZcaValidator.isNotNullEmpty(contactInfo.getContactType())){
                result += " " + contactInfo.getContactType() + ":";
                result += " " + contactInfo.getContactInfo();
            }
            if (ZcaValidator.isNotNullEmpty(contactInfo.getShortMemo())){
                result += " (" + contactInfo.getShortMemo()+ ")";
            }
            result = result.trim();
        }
        return result;
    }
    
    public static String generateDataTitle(G02Location location){
        String result = "";
        if (location != null){
            if (ZcaValidator.isNotNullEmpty(location.getLocalAddress())){
                result += " " + location.getLocalAddress();
            }
            if (ZcaValidator.isNotNullEmpty(location.getCityName())){
                result += " " + location.getCityName();
            }
            if (ZcaValidator.isNotNullEmpty(location.getStateCounty())){
                result += " " + location.getStateCounty();
            }
            if (ZcaValidator.isNotNullEmpty(location.getStateName())){
                result += " " + location.getStateName();
            }
            if (ZcaValidator.isNotNullEmpty(location.getZipCode())){
                result += " " + location.getZipCode();
            }
            result = result.trim();
        }
        return result;
    }
}
