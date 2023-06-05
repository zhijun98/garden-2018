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

import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.persistence.entity.G01PostSection;
import com.zcomapproach.garden.persistence.entity.G01WebPost;
import com.zcomapproach.garden.rose.RosePageName;
import com.zcomapproach.garden.rose.data.constant.RoseWebPostPurpose;
import com.zcomapproach.garden.rose.data.profile.RosePostSectionProfile;
import com.zcomapproach.garden.rose.data.profile.RoseWebPostProfile;
import com.zcomapproach.garden.rose.data.profile.RoseWebPostPurposeProfile;
import com.zcomapproach.garden.exception.RoseWebRefreshDemanded;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.garden.rose.util.RoseWebUtils;
import com.zcomapproach.garden.util.GardenData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author zhijun98
 */
@Named(value = "roseWebPostControllerBean")
@ViewScoped
public class RoseWebPostControllerBean extends AbstractRoseComponentBean {
    
    @Inject
    private RoseWebPostApplicationBean roseWebPostAppBean;
    
    private String targetWebPostUuid;
    
    private RoseWebPostProfile targetWebPostProfile;
    
    private HashMap<RoseWebPostPurpose, List<RoseWebPostProfile>> roseWebPostProfileStorage;
            
    private List<RoseWebPostPurposeProfile> gardenWebPostPurposeProfileList;
    
    @PostConstruct
    public synchronized void constructRoseWeb(){
        initializeRoseWebPostProfileStorage();
    }
    

    @Override
    public String getRosePageTopic() {
        return RoseText.getText("WebPostPublish");
    }

    @Override
    public String getTopicIconAwesomeName() {
        return "edit";
    }

    @Override
    public String getTargetReturnWebPath() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<RoseWebPostProfile> getPublicWebPostProfileList() {
//        List<RoseWebPostProfile> publicWebPostProfileList = null;
//        if (publicWebPostProfileList == null){
//            publicWebPostProfileList = getBusinessEJB().findGardenWebPostProfileList(RoseWebPostPurpose.PUBLIC_WEB_POST);
//            publicWebPostProfileStorage.put(RoseWebPostPurpose.PUBLIC_WEB_POST, publicWebPostProfileList);
//        }
        return roseWebPostProfileStorage.get(RoseWebPostPurpose.PUBLIC_WEB_POST);
    }
    
    public List<RoseWebPostProfile> getAllRoseWebPostProfileList() {
        if ((roseWebPostProfileStorage == null) || (roseWebPostProfileStorage.isEmpty())){
            initializeRoseWebPostProfileStorage();
        }
        List<RoseWebPostProfile> result = new ArrayList<>();
        List<List<RoseWebPostProfile>> webPostStorage = new ArrayList<>(roseWebPostProfileStorage.values());
        for (List<RoseWebPostProfile> webPostList : webPostStorage){
            result.addAll(webPostList);
        }
        return result;
    }

    private void initializeRoseWebPostProfileStorage() {
        roseWebPostProfileStorage = getBusinessEJB().retrieveGardenWebPostProfileStorage();
    }
    
    public String getTargetWebPostUuid() {
        return targetWebPostUuid;
    }

    public void setTargetWebPostUuid(String targetWebPostUuid) {
        if (targetWebPostUuid != null){
            RoseWebPostProfile aGardenWebPostProfile = getBusinessEJB().retrieveGardenWebPostProfile(targetWebPostUuid);
            if (ZcaValidator.isNotNullEmpty(aGardenWebPostProfile.getWebPostEntity().getWebPostUuid())){
                if ((targetWebPostProfile == null) 
                        || (!targetWebPostUuid.equalsIgnoreCase(targetWebPostProfile.getWebPostEntity().getWebPostUuid())))
                {
                    setTargetWebPostProfile(aGardenWebPostProfile);
                }
            }
        }
        this.targetWebPostUuid = targetWebPostUuid;
    }

    public RoseWebPostProfile getTargetWebPostProfile() {
        if (targetWebPostProfile == null){
            targetWebPostProfile = new RoseWebPostProfile();
            G01WebPost aGardenWebPost = targetWebPostProfile.getWebPostEntity();
            aGardenWebPost.setAuthorAccountUuid(getRoseUserSession().getTargetAccountProfile().getAccountEntity().getAccountUuid());
            aGardenWebPost.setWebPostUuid(GardenData.generateUUIDString());
            //a default content demanded by the front: WebPostPublishPage.xhtml
            G01PostSection aGardenPostSection = new G01PostSection();
            aGardenPostSection.setPostSectionUuid(GardenData.generateUUIDString()); 
            targetWebPostProfile.getPostSectionProfileList().add(createNewGardenPostSectionProfile(aGardenPostSection, true));
            targetWebPostProfile.setBrandNew(true);
        }
        return targetWebPostProfile;
    }

    public void setTargetWebPostProfile(RoseWebPostProfile targetWebPostProfile) {
        this.targetWebPostProfile = targetWebPostProfile;
    }
    
    private RosePostSectionProfile createNewGardenPostSectionProfile(G01PostSection aGardenPostSection, boolean createAsNew){
        RosePostSectionProfile aGardenPostSectionProfile = new RosePostSectionProfile();
        aGardenPostSectionProfile.setPostSectionEntity(aGardenPostSection);
        aGardenPostSectionProfile.setBrandNew(createAsNew);
        return aGardenPostSectionProfile;
    }
    
    public void addMoreContentSection(ActionEvent actionEvent) {
        G01PostSection aGardenPostSection = new G01PostSection();
        //demanded by the front: WebPostPublishPage.xhtml
        aGardenPostSection.setPostSectionUuid(GardenData.generateUUIDString()); 
        targetWebPostProfile.getPostSectionProfileList().add(createNewGardenPostSectionProfile(aGardenPostSection, true));
    }
    
    /**
     * Only remove RosePostSectionProfile with postSectionUuid and "AddAsNew"
     * @param postSectionUuid
     * @return 
     */
    public String deleteContentSection(String postSectionUuid){
        List<RosePostSectionProfile> aGardenPostSectionProfileList = targetWebPostProfile.getPostSectionProfileList();
        if (aGardenPostSectionProfileList.size() == 1){
            RoseJsfUtils.setGlobalErrorFacesMessage("You cannot delete this content. One web post should have at least one content section.");
            return null;
        }
        List<RosePostSectionProfile> theGardenPostSectionProfileList = new ArrayList<>();
        for (RosePostSectionProfile aGardenPostSectionProfile : aGardenPostSectionProfileList){
            if (aGardenPostSectionProfile.getPostSectionEntity().getPostSectionUuid().equalsIgnoreCase(postSectionUuid)){
                if (!aGardenPostSectionProfile.isBrandNew()){
                    try {
                        //delete it from the garden
                        getBusinessEJB().deleteEntityByUuid(G01PostSection.class, postSectionUuid);
                    } catch (Exception ex) {
                        Logger.getLogger(RoseWebPostControllerBean.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }else{
                theGardenPostSectionProfileList.add(aGardenPostSectionProfile);
            }
        }
        targetWebPostProfile.setPostSectionProfileList(theGardenPostSectionProfileList);
        HashMap<String, String> params = new HashMap<>();
        params.put(this.getRoseParamKeys().getWebPostUuidParamKey(), this.getTargetWebPostUuid());
        return RosePageName.WebPostPublishPage.name() + RoseWebUtils.constructWebQueryString(params, true);
    
    }

    public String getTargetWebPostPurpose() {
        return getTargetWebPostProfile().getWebPostEntity().getPostPurpose();
    }

    public void setTargetWebPostPurpose(String targetWebPostPurpose) {
        RoseWebPostPurpose aGardenWebPostPurpose = RoseWebPostPurpose.convertParamValueToType(targetWebPostPurpose);
        if (RoseWebPostPurpose.UNKNOWN.equals(aGardenWebPostPurpose)){
            aGardenWebPostPurpose = RoseWebPostPurpose.convertStringToType(targetWebPostPurpose);
        }
        switch (aGardenWebPostPurpose){
            case HOME_MAIN_POST:
            case PERSONAL_TAX_CASE_AGREEMENT:
            case CHASE_QUICK_PAY:
            case PAY_BY_UPLOADED_CHECK:
                if (targetWebPostProfile.isBrandNew()){
                    initializeSystemInternalWebPostsWithSingleTopic(aGardenWebPostPurpose);
                }
                break;
            default:
        }
        getTargetWebPostProfile().getWebPostEntity().setPostPurpose(aGardenWebPostPurpose.value());
    }
    
    /**
     * 
     * @param aGardenWebPostPurpose 
     */
    private void initializeSystemInternalWebPostsWithSingleTopic(RoseWebPostPurpose aGardenWebPostPurpose){
        HashMap<String, Object> params = new HashMap<>();
        params.put("postPurpose", aGardenWebPostPurpose.value());
        List<G01WebPost> aGardenWebPostList = getBusinessEJB().findEntityListByNamedQuery(G01WebPost.class, "G01WebPost.findByPostPurpose", params);
        //it expects only one record there. So only use the first one in the list
        if ((aGardenWebPostList != null) && (!aGardenWebPostList.isEmpty())){
            setTargetWebPostUuid(aGardenWebPostList.get(0).getWebPostUuid());
        }       
    }
    
    public List<String> getRoseWebPostPurposeValueList(){
        return RoseWebPostPurpose.getEnumValueList(false);
    }
    
    public List<RoseWebPostPurposeProfile> getGardenWebPostPurposeProfileList(){
        if (gardenWebPostPurposeProfileList == null){
            gardenWebPostPurposeProfileList = initializeGardenWebPostPurposeProfileList();
        }
        return gardenWebPostPurposeProfileList;
    }

    private List<RoseWebPostPurposeProfile> initializeGardenWebPostPurposeProfileList() {
        List<RoseWebPostPurposeProfile> result = new ArrayList<>();
        List<RoseWebPostPurpose> aGardenWebPostPurposeList = RoseWebPostPurpose.getGardenWebPostTagList(false);
        for (RoseWebPostPurpose aGardenWebPostPurpose : aGardenWebPostPurposeList){
            switch(aGardenWebPostPurpose){
                case HOME_MAIN_POST:
                    result.add(new RoseWebPostPurposeProfile(RoseText.getText("HomepageMainPaper"), aGardenWebPostPurpose));
                    break;
                case HOME_HEADING_POST:
                    result.add(new RoseWebPostPurposeProfile(RoseText.getText("HomepageHeadingPost"), aGardenWebPostPurpose));
                    break;
                case PERSONAL_TAX_CASE_AGREEMENT:
                    result.add(new RoseWebPostPurposeProfile(RoseText.getText("AgreementContent"), aGardenWebPostPurpose));
                    break;
                case CHASE_QUICK_PAY:
                    result.add(new RoseWebPostPurposeProfile(RoseText.getText("ChaseQuickPay"), aGardenWebPostPurpose));
                    break;
                case PAY_BY_UPLOADED_CHECK:
                    result.add(new RoseWebPostPurposeProfile(RoseText.getText("UploadYourCheck"), aGardenWebPostPurpose));
                    break;
                default:
                    result.add(new RoseWebPostPurposeProfile(RoseText.getText("PublicWebPost"), aGardenWebPostPurpose));
            }
        }
        return result;
    }
    
    public String copyPastAsPublicWebPostProfile(){
        RoseWebPostPurpose oldGardenWebPostPurpose = RoseWebPostPurpose.convertEnumValueToType(targetWebPostProfile.getWebPostEntity().getPostPurpose());
        if (RoseWebPostPurpose.PUBLIC_WEB_POST.equals(oldGardenWebPostPurpose)){
            RoseJsfUtils.setGlobalErrorFacesMessage("This web post has been a public web post.");
            return null;
        }
        RoseWebPostProfile aGardenWebPostProfile = new RoseWebPostProfile();
        aGardenWebPostProfile.cloneProfile(targetWebPostProfile);
        aGardenWebPostProfile.getWebPostEntity().setPostPurpose(RoseWebPostPurpose.PUBLIC_WEB_POST.value());
        publishWebPostProfile(aGardenWebPostProfile);
        return null;
    }
    
    public String publishTargetWebPostProfile(){
        return publishWebPostProfile(targetWebPostProfile);
    }
    
    private String publishWebPostProfile(RoseWebPostProfile aGardenWebPostProfile){
        try {
            if (validateGardenWebProfileProfile(aGardenWebPostProfile)){
                try {
                    RoseWebPostPurpose aGardenWebPostPurpose = RoseWebPostPurpose.convertEnumValueToType(aGardenWebPostProfile.getWebPostEntity().getPostPurpose());
                    //store the new one...
                    getBusinessEJB().storeGardenWebPostProfile(aGardenWebPostProfile);
                    //refresh cached posts
                    roseWebPostAppBean.resetWebPostProfiles(aGardenWebPostPurpose);
                } catch (Exception ex) {
                    Logger.getLogger(RoseWebPostControllerBean.class.getName()).log(Level.SEVERE, null, ex);
                    RoseJsfUtils.setGlobalErrorFacesMessage(ex.getMessage());
                    return null;
                }
            }else{
                return null;
            }
        } catch (RoseWebRefreshDemanded ex) {
            //Logger.getLogger(RoseWebPostControllerBean.class.getName()).log(Level.SEVERE, null, ex);
            return RosePageName.WelcomePage.name() + RoseWebUtils.constructWebQueryString(null, true);
        }
        RoseJsfUtils.setGlobalInfoFacesMessage("Publish operation is successful.");
        return RosePageName.HistoricalWebPostListPage.name() + RoseWebUtils.constructWebQueryString(null, true);
    }

    private boolean validateGardenWebProfileProfile(RoseWebPostProfile aGardenWebPostProfile) throws RoseWebRefreshDemanded {
        if (ZcaValidator.isNullEmpty(aGardenWebPostProfile.getWebPostEntity().getWebPostUuid())){
            aGardenWebPostProfile.getWebPostEntity().setWebPostUuid(GardenData.generateUUIDString());
        }
        List<RosePostSectionProfile> aGardenPostSectionProfileList = aGardenWebPostProfile.getPostSectionProfileList();
        List<RosePostSectionProfile> theGardenPostSectionProfileList = new ArrayList<>();
        for (RosePostSectionProfile aGardenPostSectionProfile : aGardenPostSectionProfileList){
            if (ZcaValidator.isNotNullEmpty(aGardenPostSectionProfile.getPostSectionEntity().getPostContent())){
                theGardenPostSectionProfileList.add(aGardenPostSectionProfile);
                if (ZcaValidator.isNullEmpty(aGardenPostSectionProfile.getPostSectionEntity().getPostSectionUuid())){
                    aGardenPostSectionProfile.getPostSectionEntity().setPostSectionUuid(GardenData.generateUUIDString());
                }
                aGardenPostSectionProfile.getPostSectionEntity().setWebPostUuid(aGardenWebPostProfile.getWebPostEntity().getWebPostUuid());
            }
        }
        if (theGardenPostSectionProfileList.isEmpty()){
            RoseJsfUtils.setGlobalErrorFacesMessage("No post content is provided.");
            return false;
        }else{
            G01WebPost aGardenWebPost = aGardenWebPostProfile.getWebPostEntity();
            if (ZcaValidator.isNullEmpty(aGardenWebPost.getPostPurpose()) 
                    || (RoseWebPostPurpose.UNKNOWN.value().equalsIgnoreCase(aGardenWebPost.getPostPurpose())))
            {
                throw new RoseWebRefreshDemanded();
            }
            aGardenWebPost.setAuthorAccountUuid(getRoseUserSession().getTargetAccountProfile().getAccountEntity().getAccountUuid());
            aGardenWebPostProfile.setPostSectionProfileList(aGardenPostSectionProfileList);
            return true;
        }
    }
    
}
