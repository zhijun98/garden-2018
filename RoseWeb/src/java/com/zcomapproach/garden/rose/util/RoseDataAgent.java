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

package com.zcomapproach.garden.rose.util;

import com.zcomapproach.commons.ZcaText;
import com.zcomapproach.garden.persistence.constant.GardenArchivedFileType;
import com.zcomapproach.garden.persistence.constant.BusinessContactorRole;
import com.zcomapproach.garden.persistence.entity.G01Location;
import com.zcomapproach.garden.persistence.entity.G01TaxcorpCase;
import com.zcomapproach.garden.persistence.entity.G01User;
import com.zcomapproach.garden.rose.data.profile.RoseArchivedDocumentProfile;
import com.zcomapproach.garden.rose.data.profile.RoseArchivedFileTypeProfile;
import com.zcomapproach.garden.rose.data.profile.RoseLocationProfile;
import com.zcomapproach.garden.rose.data.profile.RoseUserProfile;
import com.zcomapproach.garden.rose.data.profile.TaxcorpCaseProfile;
import com.zcomapproach.garden.rose.data.profile.TaxcorpRepresentativeProfile;
import com.zcomapproach.commons.ZcaValidator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author zhijun98
 */
public class RoseDataAgent {
    
    /**
     * Load aRoseArchivedDocumentProfileList into a list of RoseArchivedFileTypeProfile
     * @param aRoseArchivedDocumentProfileList
     * @return 
     */
    public static List<RoseArchivedFileTypeProfile> loadIntoRoseArchivedFileTypeProfiles(List<RoseArchivedDocumentProfile> aRoseArchivedDocumentProfileList){
        List<RoseArchivedFileTypeProfile> result = new ArrayList<>();
        if ((aRoseArchivedDocumentProfileList != null) && (!aRoseArchivedDocumentProfileList.isEmpty())){
            Collections.sort(aRoseArchivedDocumentProfileList, (RoseArchivedDocumentProfile o1, RoseArchivedDocumentProfile o2) -> {
                try{
                    return o1.getFileCustomName().compareToIgnoreCase(o2.getFileCustomName());
                }catch (Exception ex){
                    return 0;
                }
            });

            HashMap<GardenArchivedFileType, RoseArchivedFileTypeProfile> typeContainer = new HashMap<>();
            GardenArchivedFileType type;
            RoseArchivedFileTypeProfile aRoseArchivedFileTypeProfile;
            for (RoseArchivedDocumentProfile aRoseArchivedDocumentProfile : aRoseArchivedDocumentProfileList){
                type = GardenArchivedFileType.convertEnumValueToType(aRoseArchivedDocumentProfile.getArchivedDocumentEntity().getFileStatus());
                aRoseArchivedFileTypeProfile = typeContainer.get(type);
                if (aRoseArchivedFileTypeProfile == null){
                    aRoseArchivedFileTypeProfile = new RoseArchivedFileTypeProfile();
                    aRoseArchivedFileTypeProfile.setArchivedDocumentType(type);
                    typeContainer.put(type, aRoseArchivedFileTypeProfile);
                }
                aRoseArchivedFileTypeProfile.getArchivedDocumentProfileList().add(aRoseArchivedDocumentProfile);
            }//for

            result = new ArrayList<>(typeContainer.values());
            Collections.sort(result, (RoseArchivedFileTypeProfile o1, RoseArchivedFileTypeProfile o2) -> {
                try{
                    return o1.getArchivedDocumentType().value().compareToIgnoreCase(o2.getArchivedDocumentType().value());
                }catch (Exception ex){
                    return 0;
                }
            });
        }//if
        return result;
    }
    
    public static String retrieveUserName(G01User userEntity){
        if (userEntity == null){
            return "";
        }
        return ZcaText.denullize(userEntity.getLastName()) + ", " + ZcaText.denullize(userEntity.getFirstName());
    }
    
    /**
     * 
     * @param aTaxcorpCaseProfile - expected to be ready-for-use instance
     */
    public static void fixTaxcorpLocationAndContactData(TaxcorpCaseProfile aTaxcorpCaseProfile){
        if (aTaxcorpCaseProfile == null){
            return;
        }
        RoseUserProfile owner = null;
        //find the owner from representative
        List<TaxcorpRepresentativeProfile> aTaxcorpRepresentativeProfileList = aTaxcorpCaseProfile.getTaxcorpRepresentativeProfileList();
        if (aTaxcorpRepresentativeProfileList != null){
            for (TaxcorpRepresentativeProfile aTaxcorpRepresentativeProfile : aTaxcorpRepresentativeProfileList){
                if (BusinessContactorRole.TAXCORP_OWNER.value().equalsIgnoreCase(aTaxcorpRepresentativeProfile.getTaxcorpRepresentativeEntity().getRoleInCorp())){
                    owner = aTaxcorpRepresentativeProfile;
                    break;
                }
            }
        }
        //populate owner's location and contact
        if (owner != null){
            fixTaxcorpLocationAndContactDataHelper(owner, aTaxcorpCaseProfile.getTaxcorpCaseEntity());
        }
        //try to use customer's user-profile to cover the data
        fixTaxcorpLocationAndContactDataHelper(aTaxcorpCaseProfile.getCustomerProfile().getUserProfile(), 
                                               aTaxcorpCaseProfile.getTaxcorpCaseEntity());
    }

    public static void populateUserProfile(TaxcorpRepresentativeProfile aTaxcorpRepresentativeProfile, RoseUserProfile aRoseUserProfile) {
        aTaxcorpRepresentativeProfile.setBrandNew(aRoseUserProfile.isBrandNew());
        aTaxcorpRepresentativeProfile.setEmail(aRoseUserProfile.getEmail());
        aTaxcorpRepresentativeProfile.setFax(aRoseUserProfile.getFax());
        aTaxcorpRepresentativeProfile.setHomePhone(aRoseUserProfile.getHomePhone());
        aTaxcorpRepresentativeProfile.setMobilePhone(aRoseUserProfile.getMobilePhone());
        aTaxcorpRepresentativeProfile.setWorkPhone(aRoseUserProfile.getWorkPhone());
        aTaxcorpRepresentativeProfile.setUserEntity(aRoseUserProfile.getUserEntity());
        if ((aRoseUserProfile.getUserContactInfoProfileList() != null) && (!aRoseUserProfile.getUserContactInfoProfileList().isEmpty())){
            aTaxcorpRepresentativeProfile.setUserContactInfoProfileList(aRoseUserProfile.getUserContactInfoProfileList());
        }
        //currently (05-16-2018) location is not used for contactor although here it also set it up
        if ((aRoseUserProfile.getUserLocationProfileList() != null) && (!aRoseUserProfile.getUserLocationProfileList().isEmpty())){
            aTaxcorpRepresentativeProfile.setUserLocationProfileList(aRoseUserProfile.getUserLocationProfileList());
        }
    }

    private static void fixTaxcorpLocationAndContactDataHelper(RoseUserProfile owner, G01TaxcorpCase aG01TaxcorpCase) {
        //contacts
        if (ZcaValidator.isNullEmpty(aG01TaxcorpCase.getCorporateEmail())){
            aG01TaxcorpCase.setCorporateEmail(owner.getEmail());
        }
        if (ZcaValidator.isNullEmpty(aG01TaxcorpCase.getCorporateFax())){
            aG01TaxcorpCase.setCorporateFax(owner.getFax());
        }
        if (ZcaValidator.isNullEmpty(aG01TaxcorpCase.getCorporatePhone())){
            aG01TaxcorpCase.setCorporatePhone(owner.getWorkPhone());
            if (ZcaValidator.isNullEmpty(aG01TaxcorpCase.getCorporatePhone())){
                aG01TaxcorpCase.setCorporatePhone(owner.getMobilePhone());
                if (ZcaValidator.isNullEmpty(aG01TaxcorpCase.getCorporatePhone())){
                    aG01TaxcorpCase.setCorporatePhone(owner.getHomePhone());
                }
            }
        }
        if (ZcaValidator.isNullEmpty(aG01TaxcorpCase.getCorporateWebPresence())){
            aG01TaxcorpCase.setCorporateWebPresence(owner.getWeChat());
        }

        //location
        List<RoseLocationProfile> aRoseLocationProfileList = owner.getUserLocationProfileList();
        G01Location aG01Location = null;
        if ((aRoseLocationProfileList != null) && (aRoseLocationProfileList.size() > 0)){
            aG01Location = aRoseLocationProfileList.get(0).getLocationEntity();
        }
        if (aG01Location != null){
            if (ZcaValidator.isNullEmpty(aG01TaxcorpCase.getTaxcorpCountry())){
                aG01TaxcorpCase.setTaxcorpCountry(aG01Location.getCountry());
            }
            if (ZcaValidator.isNullEmpty(aG01TaxcorpCase.getTaxcorpAddress())){
                aG01TaxcorpCase.setTaxcorpAddress(aG01Location.getLocalAddress());
            }
            if (ZcaValidator.isNullEmpty(aG01TaxcorpCase.getTaxcorpCity())){
                aG01TaxcorpCase.setTaxcorpCity(aG01Location.getCityName());
            }
            if (ZcaValidator.isNullEmpty(aG01TaxcorpCase.getTaxcorpStateCounty())){
                aG01TaxcorpCase.setTaxcorpStateCounty(aG01Location.getStateCounty());
            }
            if (ZcaValidator.isNullEmpty(aG01TaxcorpCase.getTaxcorpState())){
                aG01TaxcorpCase.setTaxcorpState(aG01Location.getStateName());
            }
            if (ZcaValidator.isNullEmpty(aG01TaxcorpCase.getTaxcorpZip())){
                aG01TaxcorpCase.setTaxcorpZip(aG01Location.getZipCode());
            }
        }
    }

}
