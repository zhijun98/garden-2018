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

package com.zcomapproach.garden.rose.bean;

import com.zcomapproach.garden.persistence.entity.G01PostSection;
import com.zcomapproach.garden.persistence.entity.G01WebPost;
import com.zcomapproach.garden.rose.data.constant.RoseWebPostPurpose;
import com.zcomapproach.garden.rose.data.profile.RosePostSectionProfile;
import com.zcomapproach.garden.rose.data.profile.RoseWebPostProfile;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author zhijun98
 */
@Named(value = "roseWebPost")
@ApplicationScoped
public class RoseWebPostApplicationBean extends AbstractRoseBean {
    /**
     * The main web paper for users. 
     */
    private RoseWebPostProfile homepagePostProfile;
    private RoseWebPostProfile homepagePostHeading01Profile;
    private RoseWebPostProfile homepagePostHeading02Profile;
    private RoseWebPostProfile homepagePostHeading03Profile;
    
    private RoseWebPostProfile caseAgreementProfile;
    private RoseWebPostProfile chaseQuickPayIntroductionProfile;
    private RoseWebPostProfile payByUploadedCheckDescriptionProfile;
    
    private List<RoseWebPostProfile> publicWebPostProfileList;
    
    private List<RoseWebPostProfile> homepageHeadingPostProfileList;
    
    @PostConstruct
    public synchronized void initializeWebPosts(){
        loadHomepagePostProfile();
        loadHomepageHeadingPostProfileList();
    }
    
    public synchronized void resetWebPostProfiles(RoseWebPostPurpose aGardenWebPostPurpose){
        switch(aGardenWebPostPurpose){
            case HOME_MAIN_POST:
                loadHomepagePostProfile();
                break;
            case HOME_HEADING_POST:
                loadHomepageHeadingPostProfileList();
                break;
            case PERSONAL_TAX_CASE_AGREEMENT:
                caseAgreementProfile = null;
                break;
            case CHASE_QUICK_PAY:
                chaseQuickPayIntroductionProfile = null;
                break;
            case PAY_BY_UPLOADED_CHECK:
                payByUploadedCheckDescriptionProfile = null;
                break;
            case UNKNOWN:
                //do nothing
                break;
            default:
                if (publicWebPostProfileList != null){
                    publicWebPostProfileList.clear();
                }
        }
    }

    private synchronized void loadHomepagePostProfile() {
        String slogan = "Our mission is to help our business clients make distinctive, lasting, and substantial improvements "
                + "in their business performance and customer loyalty. Our customizable online system empower our partner to "
                + "build a great firm that attracts, develops, excites, and retains not only customers but also valuable employees. "
                + "We believe we will be successful if our clients are successful.";
        List<RoseWebPostProfile> aRoseWebPostProfileList = getBusinessEJB().findGardenWebPostProfileList(RoseWebPostPurpose.HOME_MAIN_POST);
        if (aRoseWebPostProfileList.isEmpty()){
            //no any web post which is used for the home page from the database
            homepagePostProfile = new RoseWebPostProfile();
            homepagePostProfile.getWebPostEntity().setPostTitle("Welcome to Garden system!");
            homepagePostProfile.getWebPostEntity().setPostBrief(slogan);
        }else{
            homepagePostProfile = aRoseWebPostProfileList.get(0);
        }
    }

    private synchronized void loadHomepageHeadingPostProfileList() {
        String slogan = "Our mission is to help our business clients make distinctive, lasting, and substantial improvements "
                + "in their business performance and customer loyalty. Our customizable online system empower our partner to "
                + "build a great firm that attracts, develops, excites, and retains not only customers but also valuable employees. "
                + "We believe we will be successful if our clients are successful.";
        homepageHeadingPostProfileList = getBusinessEJB().findGardenWebPostProfileList(RoseWebPostPurpose.HOME_HEADING_POST);
        if (homepageHeadingPostProfileList.size() >= 1){
            homepagePostHeading01Profile = homepageHeadingPostProfileList.get(0);
        }else{
            homepagePostHeading01Profile = new RoseWebPostProfile();
            //homepagePostHeading01Profile.getWebPostEntity().setWebPostUuid(GardenData.generateUUIDString());
            homepagePostHeading01Profile.getWebPostEntity().setPostTitle("Welcome to Garden system!");
            homepagePostHeading01Profile.getWebPostEntity().setPostBrief(slogan);
        }
        if (homepageHeadingPostProfileList.size() >= 2){
            homepagePostHeading02Profile = homepageHeadingPostProfileList.get(1);
        }else{
            homepagePostHeading02Profile = new RoseWebPostProfile();
            //homepagePostHeading02Profile.getWebPostEntity().setWebPostUuid(GardenData.generateUUIDString());
            homepagePostHeading02Profile.getWebPostEntity().setPostTitle("Welcome to Garden system!");
            homepagePostHeading02Profile.getWebPostEntity().setPostBrief(slogan);
        }
        if (homepageHeadingPostProfileList.size() >= 3){
            homepagePostHeading03Profile = homepageHeadingPostProfileList.get(2);
        }else{
            homepagePostHeading03Profile = new RoseWebPostProfile();
            //homepagePostHeading03Profile.getWebPostEntity().setWebPostUuid(GardenData.generateUUIDString());
            homepagePostHeading03Profile.getWebPostEntity().setPostTitle("Welcome to Garden system!");
            homepagePostHeading03Profile.getWebPostEntity().setPostBrief(slogan);
        }
    }

    public synchronized RoseWebPostProfile getHomepagePostProfile() {
        return homepagePostProfile;
    }

    public synchronized void setHomepagePostProfile(RoseWebPostProfile homepagePostProfile) {
        this.homepagePostProfile = homepagePostProfile;
    }

    public synchronized RoseWebPostProfile getHomepagePostHeading01Profile() {
        return homepagePostHeading01Profile;
    }

    public synchronized void setHomepagePostHeading01Profile(RoseWebPostProfile homepagePostHeading01Profile) {
        this.homepagePostHeading01Profile = homepagePostHeading01Profile;
    }

    public synchronized RoseWebPostProfile getHomepagePostHeading02Profile() {
        return homepagePostHeading02Profile;
    }

    public synchronized void setHomepagePostHeading02Profile(RoseWebPostProfile homepagePostHeading02Profile) {
        this.homepagePostHeading02Profile = homepagePostHeading02Profile;
    }

    public synchronized RoseWebPostProfile getHomepagePostHeading03Profile() {
        return homepagePostHeading03Profile;
    }

    public synchronized void setHomepagePostHeading03Profile(RoseWebPostProfile homepagePostHeading03Profile) {
        this.homepagePostHeading03Profile = homepagePostHeading03Profile;
    }
    
    public synchronized RoseWebPostProfile getCaseAgreementProfile() {
        if (caseAgreementProfile == null){
            caseAgreementProfile = retrieveWebPostProfileSingletonByPurpose(RoseWebPostPurpose.PERSONAL_TAX_CASE_AGREEMENT);
        }
        return caseAgreementProfile;
    }

    public synchronized RoseWebPostProfile getChaseQuickPayIntroductionProfile() {
        if (chaseQuickPayIntroductionProfile == null){
            chaseQuickPayIntroductionProfile = retrieveWebPostProfileSingletonByPurpose(RoseWebPostPurpose.CHASE_QUICK_PAY);
        }
        return chaseQuickPayIntroductionProfile;
    }

    public synchronized RoseWebPostProfile getPayByUploadedCheckDescriptionProfile() {
        if (payByUploadedCheckDescriptionProfile == null){
            payByUploadedCheckDescriptionProfile = retrieveWebPostProfileSingletonByPurpose(RoseWebPostPurpose.PAY_BY_UPLOADED_CHECK);
        }
        return payByUploadedCheckDescriptionProfile;
    }

    public List<RoseWebPostProfile> getPublicWebPostProfileList() {
        if ((publicWebPostProfileList == null) || (publicWebPostProfileList.isEmpty())){
            publicWebPostProfileList = getBusinessEJB().findGardenWebPostProfileList(RoseWebPostPurpose.PUBLIC_WEB_POST);
            if (publicWebPostProfileList == null){
                publicWebPostProfileList = new ArrayList<>();
            }
        }
        return publicWebPostProfileList;
    }

    public List<RoseWebPostProfile> getHomepageHeadingPostProfileList() {
        if ((homepageHeadingPostProfileList == null) || (homepageHeadingPostProfileList.isEmpty())){
            homepageHeadingPostProfileList = getBusinessEJB().findGardenWebPostProfileList(RoseWebPostPurpose.HOME_HEADING_POST);
            if (homepageHeadingPostProfileList == null){
                homepageHeadingPostProfileList = new ArrayList<>();
            }
        }
        return homepageHeadingPostProfileList;
    }
    
    private synchronized RoseWebPostProfile retrieveWebPostProfileSingletonByPurpose(RoseWebPostPurpose aRoseWebPostPurpose){
        RoseWebPostProfile result = null;
        HashMap<String, Object> params = new HashMap<>();
        params.put("postPurpose", aRoseWebPostPurpose.value());
        List<G01WebPost> aG01WebPostList = getBusinessEJB().findEntityListByNamedQuery(G01WebPost.class, "G01WebPost.findByPostPurpose", params);
        //it expects only one record there. So only use the first one in the list
        if ((aG01WebPostList != null) && (!aG01WebPostList.isEmpty())){
            result = getBusinessEJB().retrieveGardenWebPostProfile(aG01WebPostList.get(0).getWebPostUuid());
        }else{
            result = new RoseWebPostProfile();
            RosePostSectionProfile aGardenPostSectionProfile = new RosePostSectionProfile();
            aGardenPostSectionProfile.setPostSectionEntity(new G01PostSection());
            aGardenPostSectionProfile.getPostSectionEntity().setPostContent("Please go to Web-Post to set up this content.");
            result.getPostSectionProfileList().add(aGardenPostSectionProfile);
        }
        return result;
    }
    
    public String deleteWebPostProfile(String webPostUuid){
        G01WebPost aGardenWebPost = null;
        try {
            aGardenWebPost = getBusinessEJB().deleteWebPostByUuid(webPostUuid);
        } catch (Exception ex) {
            Logger.getLogger(RoseWebPostApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalInfoFacesMessage(ex.getMessage());
            return null;
        }
        if (aGardenWebPost != null){
            resetWebPostProfiles(RoseWebPostPurpose.convertEnumValueToType(aGardenWebPost.getPostPurpose()));
        }
        RoseJsfUtils.setGlobalInfoFacesMessage("Deletion operation is successful.");
        return null;
    }

}
