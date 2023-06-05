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

import com.zcomapproach.garden.persistence.constant.GardenEntityType;
import com.zcomapproach.garden.persistence.entity.G01Log;
import com.zcomapproach.garden.rose.bean.state.RoseChatMessageTreeNode;
import com.zcomapproach.garden.rose.bean.state.RoseMessagingState;
import com.zcomapproach.garden.rose.data.RoseWebParamValue;
import com.zcomapproach.garden.rose.data.profile.EmployeeAccountProfile;
import com.zcomapproach.garden.rose.data.profile.RoseChatMessageProfile;
import com.zcomapproach.garden.rose.i18n.RoseText;
import com.zcomapproach.garden.rose.util.RoseJsfUtils;
import com.zcomapproach.commons.ZcaValidator;
import java.util.List;
import javax.inject.Inject;
import org.primefaces.extensions.model.layout.LayoutOptions;

/**
 * JSF-managed beans which support the front-end JSF web pages
 * @author zhijun98
 */
public abstract class AbstractRoseComponentBean extends AbstractRoseBean implements IRoseComponentBean, IRoseChatMessaging{
    
    private String requestedEntityType; 
    private String requestedEntityUuid;
    
    @Inject
    private RoseApplicationBean roseApp;
    
    @Inject
    private RoseSettingsBean roseSettings;
    
    @Inject
    private RoseEmailControllerBean roseEmailController;
    
    @Inject
    private RosePrivilegesBean rosePrivileges;
    
    @Inject
    private RosePagesBean rosePages;
    
    @Inject
    private RoseParamKeysBean roseParamKeys;
    
    @Inject
    private RoseParamValuesBean roseParamValues;

    /**
     * Support message board
     */
    private RoseMessagingState messagingState;
    
    /**
     * Purpose for this view: optional
     */
    private String requestedViewPurpose;
    
    private String selectedLogMsg;
    private G01Log targetLogEntity;
    
    private EmployeeAccountProfile selectedEmployeeProfileForCaseAssignment;
    
    public EmployeeAccountProfile getSelectedEmployeeProfileForCaseAssignment() {
        return selectedEmployeeProfileForCaseAssignment;
    }

    public void setSelectedEmployeeProfileForCaseAssignment(EmployeeAccountProfile selectedEmployeeProfileForCaseAssignment) {
        this.selectedEmployeeProfileForCaseAssignment = selectedEmployeeProfileForCaseAssignment;
    }
    
    /**
     * 
     * @return GardenEntityType.BUSINESS_PUBLIC_BOARD.name();
     */
    public String getBusinessPublicBoardEntityType(){
        return GardenEntityType.BUSINESS_PUBLIC_BOARD.name();
    }

    public G01Log getTargetLogEntity() {
        if (targetLogEntity == null){
            targetLogEntity = new G01Log();
            targetLogEntity.setEntityType(this.getEntityType().name());
            targetLogEntity.setEntityUuid(this.getRequestedEntityUuid());
        }
        return targetLogEntity;
    }

    public String getSelectedLogMsg() {
        return selectedLogMsg;
    }

    public void setSelectedLogMsg(String selectedLogMsg) {
        if (ZcaValidator.isNotNullEmpty(selectedLogMsg)){
            this.selectedLogMsg = selectedLogMsg;
        }
    }

    public void setTargetLogEntity(G01Log targetLogEntity) {
        this.targetLogEntity = targetLogEntity;
    }
    
    public String saveTargetLogEntity(){
        return null;
    }

    /**
     * Refer to template-parameter: rosePageTopic
     * @return 
     */
    public abstract String getRosePageTopic();
    
    /**
     * Refer to template-parameter: topicIconAwesome
     * @return 
     */
    public abstract String getTopicIconAwesomeName();

    public String getRequestedViewPurpose() {
        return requestedViewPurpose;
    }

    public void setRequestedViewPurpose(String requestedViewPurpose) {
        this.requestedViewPurpose = requestedViewPurpose;
    }
    
    @Override
    public boolean isForCreateNewEntity(){
        return RoseWebParamValue.CREATE_NEW_ENTITY.value().equalsIgnoreCase(requestedViewPurpose);
    }
    
    public boolean isForUpdateExistingEntity(){
        return RoseWebParamValue.UPDATE_EXISTING_ENTITY.value().equalsIgnoreCase(requestedViewPurpose); 
    }
    
    public boolean isForDeleteExistingEntity(){
        return RoseWebParamValue.DELETE_EXISTING_ENTITY.value().equalsIgnoreCase(requestedViewPurpose); 
    }
    
    public String getConfirmWarningMessage(){
        if (isForDeleteExistingEntity()){
            return RoseText.getText("ConfirmDeletionMessage_T");
        }else{
            return RoseText.getText("ConfirmWarningMessage_T");
        }
    }
    
    public RoseMessagingState getMessagingState() {
        if (messagingState == null){
            messagingState = new RoseMessagingState(this, getEntityType(), getRequestedEntityUuid());
        }
        return messagingState;
    }
    
    public String getEntityTypeParamValue(){
        return getEntityType().value();
    }
    
    public GardenEntityType getEntityType(){
        return GardenEntityType.convertEnumValueToType(requestedEntityType);
    }

    public String getRequestedEntityType() {
        if (requestedEntityType == null){
            return GardenEntityType.UNKNOWN.value();
        }
        return requestedEntityType;
    }

    public void setRequestedEntityType(String requestedEntityType) {
        this.requestedEntityType = requestedEntityType;
    }

    public String getRequestedEntityUuid() {
        return requestedEntityUuid;
    }

    public void setRequestedEntityUuid(String requestedEntityUuid) {
        this.requestedEntityUuid = requestedEntityUuid;
    }
    
    public String getTargetReturnWebPageName(){
        return RoseJsfUtils.getCurrentWebPageName();
    }
    
    @Override
    public abstract String getTargetReturnWebPath();

    @Override
    public boolean isForReplyMessage() {
        return getMessagingState().isForReplyMessage();
    }

    @Override
    public RoseChatMessageProfile getTargetReplyMessageProfile() {
        return getMessagingState().getTargetReplyMessageProfile();
    }

    @Override
    public String getRequestedTargetReplyMessageUuid() {
        return getMessagingState().getRequestedTargetReplyMessageUuid();
    }

    @Override
    public void setRequestedTargetReplyMessageUuid(String requestedTargetReplyMessageUuid) {
        getMessagingState().setRequestedTargetReplyMessageUuid(requestedTargetReplyMessageUuid);
    }

    @Override
    public String getTargetChatMessage() {
        return getMessagingState().getTargetChatMessage();
    }

    @Override
    public void setTargetChatMessage(String targetGardenMessage) {
        getMessagingState().setTargetChatMessage(targetGardenMessage);
    }

    @Override
    public String storeTargetTalkerMessage() {
        return getMessagingState().storeTargetTalkerMessage();
    }

    @Override
    public String storeTalkerMessageForBusinessPublicBoard() {
        return getMessagingState().storeTypedTalkerMessage(GardenEntityType.BUSINESS_PUBLIC_BOARD);
    }

    @Override
    public RoseChatMessageTreeNode getTargetChatMessagingTopicRoot() {
        return getMessagingState().getTargetChatMessagingTopicRoot();
    }

    @Override
    public RoseChatMessageTreeNode getTargetChatMessagingTopicRootForBusinessPublicBoard() {
        return getMessagingState().getTargetChatMessagingTopicRootForBusinessPublicBoard();
    }
    
    public List<EmployeeAccountProfile> getEmployeeAccountProfileList(){
        return getBusinessEJB().findCurrentEmployeeAccountProfileList();
    }

    public RoseApplicationBean getRoseApp() {
        return roseApp;
    }

    public RosePagesBean getRosePages() {
        return rosePages;
    }

    public RoseParamKeysBean getRoseParamKeys() {
        return roseParamKeys;
    }

    public RoseParamValuesBean getRoseParamValues() {
        return roseParamValues;
    }

    public RoseSettingsBean getRoseSettings() {
        return roseSettings;
    }

    public RoseEmailControllerBean getRoseEmailController() {
        return roseEmailController;
    }

    public RosePrivilegesBean getRosePrivileges() {
        return rosePrivileges;
    }
    
    public String deleteTargetTaxcorpCaseProfile(String taxcorpCaseUuid){
        try {
            getTaxcorpEJB().deleteTaxcorpCaseEntity(taxcorpCaseUuid);
            RoseJsfUtils.setGlobalSuccessfulOperationMessage();
        } catch (Exception ex) {
            //Logger.getLogger(TaxcorpCaseViewBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalFailedOperationMessage("Cannot delete it");
        }
        return null;
    }
    
    public String deleteTargetTaxpayerCaseProfile(String taxpayerCaseUuid){
        try {
            getTaxpayerEJB().deleteTaxpayerCaseEntity(taxpayerCaseUuid);
            RoseJsfUtils.setGlobalSuccessfulOperationMessage();
        } catch (Exception ex) {
            //Logger.getLogger(TaxcorpCaseViewBean.class.getName()).log(Level.SEVERE, null, ex);
            RoseJsfUtils.setGlobalFailedOperationMessage("Cannot delete it. " + ex.getMessage());
        }
        return null;
    }
}
