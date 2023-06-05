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

package com.zcomapproach.garden.rose.data.profile;

import com.zcomapproach.garden.persistence.entity.G01TaxFiling;
import com.zcomapproach.garden.persistence.entity.G01TaxcorpCase;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zhijun98
 */
public class TaxcorpContactProfile extends BusinessCaseProfile{

    private G01TaxcorpCase taxcorpCase;
    private List<G01TaxFiling> taxFilingList;
    private List<TaxcorpRepresentativeProfile> taxcorpRepresentativeProfiles;
    private List<ContactMessageProfile> contactMessageProfileList;
    
    private List<TaxcorpRepresentativeProfile> targetSelectedContactorProfiles;

    public TaxcorpContactProfile() {
        this.taxcorpCase = new G01TaxcorpCase();
        this.taxFilingList = new ArrayList<>();
        this.taxcorpRepresentativeProfiles = new ArrayList<>();
        this.contactMessageProfileList = new ArrayList<>();
        
        this.targetSelectedContactorProfiles = new ArrayList<>();
    }

    @Override
    public String getProfileName() {
        return getTaxcorpCase().getCorporateName();
    }

    @Override
    public String getProfileDescriptiveName() {
        return getTaxcorpCase().getCorporateName() + " ("+getTaxcorpCase().getEinNumber()+")";
    }

    @Override
    protected String getProfileUuid() {
        return getTaxcorpCase().getTaxcorpCaseUuid();
    }

    public G01TaxcorpCase getTaxcorpCase() {
        return taxcorpCase;
    }

    public void setTaxcorpCase(G01TaxcorpCase taxcorpCase) {
        this.taxcorpCase = taxcorpCase;
    }

    public List<G01TaxFiling> getTaxFilingList() {
        return taxFilingList;
    }

    public void setTaxFilingList(List<G01TaxFiling> taxFilingList) {
        this.taxFilingList = taxFilingList;
    }

    public List<ContactMessageProfile> getContactMessageProfileList() {
        return contactMessageProfileList;
    }

    public void setContactMessageProfileList(List<ContactMessageProfile> contactMessageProfileList) {
        this.contactMessageProfileList = contactMessageProfileList;
    }

    public List<TaxcorpRepresentativeProfile> getTaxcorpRepresentativeProfiles() {
        return taxcorpRepresentativeProfiles;
    }

    public void setTaxcorpRepresentativeProfiles(List<TaxcorpRepresentativeProfile> taxcorpRepresentativeProfiles) {
        this.taxcorpRepresentativeProfiles = taxcorpRepresentativeProfiles;
    }

    public List<TaxcorpRepresentativeProfile> getTargetSelectedContactorProfiles() {
        return targetSelectedContactorProfiles;
    }

    public void setTargetSelectedContactorProfiles(List<TaxcorpRepresentativeProfile> targetSelectedContactorProfiles) {
        this.targetSelectedContactorProfiles = targetSelectedContactorProfiles;
    }

}
