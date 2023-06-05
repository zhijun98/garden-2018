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

import com.zcomapproach.garden.persistence.entity.G01WebPost;
import com.zcomapproach.garden.persistence.updater.G01DataUpdaterFactory;
import com.zcomapproach.commons.ZcaValidator;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zhijun98
 */
public class RoseWebPostProfile extends AbstractRoseEntityProfile{
    private String profileTitle;
    private G01WebPost webPostEntity;
    private List<RosePostSectionProfile> postSectionProfileList;
    
    private RoseAccountProfile authorProfile;

    public RoseWebPostProfile() {
        webPostEntity = new G01WebPost();
        postSectionProfileList = new ArrayList<>();
        authorProfile = new RoseAccountProfile();
    }

    public String getProfileTitle() {
        return profileTitle;
    }

    public void setProfileTitle(String profileTitle) {
        this.profileTitle = profileTitle;
    }

    @Override
    public void cloneProfile(AbstractRoseEntityProfile srcProfile) {
        if (!(srcProfile instanceof RoseWebPostProfile)){
            return;
        }
        RoseWebPostProfile srcRoseWebPostProfile = (RoseWebPostProfile)srcProfile;
        
        G01DataUpdaterFactory.getSingleton().getG01WebPostUpdater().cloneEntity(srcRoseWebPostProfile.getWebPostEntity(), this.getWebPostEntity());
        
        List<RosePostSectionProfile> srcRosePostSectionProfileList = srcRoseWebPostProfile.getPostSectionProfileList();
        this.getPostSectionProfileList().clear();
        RosePostSectionProfile destRosePostSectionProfile;
        for (RosePostSectionProfile srcRosePostSectionProfile : srcRosePostSectionProfileList){
            destRosePostSectionProfile = new RosePostSectionProfile();
            destRosePostSectionProfile.cloneProfile(srcRosePostSectionProfile);
            getPostSectionProfileList().add(destRosePostSectionProfile);
        }
    }

    public RoseAccountProfile getAuthorProfile() {
        return authorProfile;
    }

    public void setAuthorProfile(RoseAccountProfile authorProfile) {
        this.authorProfile = authorProfile;
    }

    public G01WebPost getWebPostEntity() {
        return webPostEntity;
    }

    public void setWebPostEntity(G01WebPost webPostEntity) {
        this.webPostEntity = webPostEntity;
    }

    public List<RosePostSectionProfile> getPostSectionProfileList() {
        return postSectionProfileList;
    }

    public void setPostSectionProfileList(List<RosePostSectionProfile> postSectionProfileList) {
        this.postSectionProfileList = postSectionProfileList;
    }
    
    public String getEntirePostContent(){
        String result = "";
        if (postSectionProfileList != null){
            for (RosePostSectionProfile aRosePostSectionProfile : postSectionProfileList){
                result = result + aRosePostSectionProfile.getPostSectionEntity().getPostContent() + "<p/>";
            }
        }
        if (ZcaValidator.isNullEmpty(result)){
            result = "Please go to the web post management to set up the agreement content";
        }
        return result;
    }

    /**
     * Title of the web post
     * @return 
     */
    @Override
    public String getProfileName() {
        return getWebPostEntity().getPostTitle();
    }

    /**
     * Brief on the web post
     * @return 
     */
    @Override
    public String getProfileDescriptiveName() {
        return getWebPostEntity().getPostBrief();
    }

    @Override
    protected String getProfileUuid() {
        return getWebPostEntity().getWebPostUuid();
    }

}
