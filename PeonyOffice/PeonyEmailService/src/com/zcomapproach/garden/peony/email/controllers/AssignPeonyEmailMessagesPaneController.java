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

package com.zcomapproach.garden.peony.email.controllers;

import com.zcomapproach.garden.email.GardenEmailMessage;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 *
 * @deprecated - replaced by AssignEmailController
 * @author zhijun98
 */
public class AssignPeonyEmailMessagesPaneController extends PeonyEmailServiceController{
////    @FXML
////    private Label selectedEmailsLabel;
////    @FXML
////    private TextArea taxcorpEinTextArea;
////    @FXML
////    private TextArea taxpayerSsnTextArea;
////    @FXML
////    private FlowPane employeeCheckBoxFlowPane;
////    @FXML
////    private TextArea assignMessageTextArea;
////    @FXML
////    private Button assignButton;
    
////    private final HashMap<PeonyEmployee, CheckBox> employeeCheckBoxMap;

    private final List<GardenEmailMessage> targetEmailMessageList;
    
    public AssignPeonyEmailMessagesPaneController(List<GardenEmailMessage> aPeonyEmailMessageList) {
        this.targetEmailMessageList = aPeonyEmailMessageList;
////        employeeCheckBoxMap = new HashMap<>();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
////        String selectedMessages = getSelectedMessageBriefs();
////        selectedEmailsLabel.setText(selectedMessages);
////        PeonyEmployeeList aPeonyEmployeeList = Lookup.getDefault().lookup(PeonyManagementService.class).retrievePeonyEmployeeList();
////        List<PeonyEmployee> employees = aPeonyEmployeeList.getPeonyEmployeeList();
////        CheckBox aCheckBox;
////        for (PeonyEmployee employee : employees){
////                aCheckBox = new CheckBox();
////                aCheckBox.setPrefWidth(300);
////                aCheckBox.setText(employee.getTextLine());
////                FlowPane.setMargin(aCheckBox, new Insets(2,10,2,10));
////                employeeCheckBoxMap.put(employee, aCheckBox);
////                employeeCheckBoxFlowPane.getChildren().add(aCheckBox);
////        }
////        taxcorpEinTextArea.setPromptText("Taxcorp's EINs: one EIN per line");
////        taxpayerSsnTextArea.setPromptText("Primary taxpayer's SSNs: one SSN per line");
////        assignMessageTextArea.setPromptText("Comments on this assignment (max 450 characters).");
////        
////        assignButton.setOnAction((ActionEvent event) -> {
////            assignEmailsToSelectedEmployees();
////        });
    }
////
////    /**
////     * Assign selected email(s) to employee(s) or some taxpayer-case(s), or some taxcorp-case(s) 
////     */
////    private void assignEmailsToSelectedEmployees() {
////        Task<Boolean> assignEmailsTask = new Task<Boolean>(){
////            @Override
////            protected Boolean call() throws Exception {
////                String comments = assignMessageTextArea.getText();
////                if (ZcaValidator.isNullEmpty(comments) || (comments.length() > 450)){
////                    updateMessage("Please give comments on this assignment with max 450 characters.");
////                    return false;
////                }
////                
////                boolean noAnyAssignment = true;
////                
////////                //hooked taxpayer
////////                List<String> ssnList = new ArrayList<>();
////////                if (ZcaValidator.isNotNullEmpty(taxpayerSsnTextArea.getText())){
////////                    String[] ssnArray = taxpayerSsnTextArea.getText().split("\\n");
////////                    for (String ssn : ssnArray){
////////                        if (ZcaValidator.isNotNullEmpty(ssn)){
////////                            if (!PeonyDataUtils.isEinFormat(ssn)){
////////                                updateMessage("Please modify taxpayer's SSN format: " + ssn);
////////                                return false;
////////                            }
////////                            ssnList.add(ssn);
////////                        }
////////                    }
////////                    if (!ssnList.isEmpty()){
////////                        noAnyAssignment = false;
////////                    }
////////                }
////                
////////                //hooked taxcorp
////////                List<String> einList = new ArrayList<>();
////////                if (ZcaValidator.isNotNullEmpty(taxcorpEinTextArea.getText())){
////////                    String[] einArray = taxcorpEinTextArea.getText().split("\\n");
////////                    for (String ein : einArray){
////////                        if (ZcaValidator.isNotNullEmpty(ein)){
////////                            if (!PeonyDataUtils.isEinFormat(ein)){
////////                                updateMessage("Please modify taxcorp's EIN format: " + ein);
////////                                return false;
////////                            }
////////                            einList.add(ein);
////////                        }
////////                    }
////////                    if (!einList.isEmpty()){
////////                        noAnyAssignment = false;
////////                    }
////////                }
////                
////                //selected employees....
////                List<PeonyEmployee> selectedEmployees = new ArrayList<>();
////                Set<PeonyEmployee> keys = employeeCheckBoxMap.keySet();
////                Iterator<PeonyEmployee> itr = keys.iterator();
////                PeonyEmployee aPeonyEmployee;
////                CheckBox aCheckBox;
////                while(itr.hasNext()){
////                    aPeonyEmployee = itr.next();
////                    aCheckBox = employeeCheckBoxMap.get(aPeonyEmployee);
////                    if (aCheckBox.isSelected()){
////                        selectedEmployees.add(aPeonyEmployee);
////                        noAnyAssignment = false;
////                    }
////                }
////                
////                //validation...
////                if (noAnyAssignment){
////                    updateMessage("Please assign to employee, or/and some Taxcorps, or/and some Taxpayers.");
////                    return false;
////                }
////                //Create the target PeonyCommAssignment for saving
////                PeonyEmployee currentLoginEmployee = PeonyLocalSettings.getSingleton().getCurrentLoginEmployee();
////                String peonyLoginEmployeeUuid = currentLoginEmployee.getAccount().getAccountUuid();
////                String peonyLoginEmployeeEmailAddress = currentLoginEmployee.getWorkEmail();
////                PeonyCommAssignment targetPeonyCommAssignment = new PeonyCommAssignment();
////                targetPeonyCommAssignment.assignEntityUuid();
////                //PeonyCommAssignment::G02CommAssignment
////                G02CommAssignment aG02CommAssignment = targetPeonyCommAssignment.getCommAssignment();
////                aG02CommAssignment.setDescription(assignMessageTextArea.getText());
////                aG02CommAssignment.setCommType(GardenCommType.TASK_EMAIL.name());
////                aG02CommAssignment.setEmployeeUuid(peonyLoginEmployeeUuid);
////                aG02CommAssignment.setOperationType(GardenOperationType.ASSIGN_TASK.name());
////                //PeonyCommAssignment::List<G02CommArchive>
////                String commUuid = aG02CommAssignment.getCommUuid();
////                loadG02CommArchiveList(targetPeonyCommAssignment.getCommArchives(), 
////                                       commUuid, peonyLoginEmployeeUuid, peonyLoginEmployeeEmailAddress,
////                                       PeonyLocalSettings.getSingleton().getEmailSerializationFolder());
////                //PeonyCommAssignment::List<G02CommAssignmentTarget>
////                String receipientEmails = loadG02CommAssignmentTargetList(targetPeonyCommAssignment.getCommAssignmentTargets(),
////                                                                          ssnList, einList, selectedEmployees, commUuid);
////                //Send email...
////                if (receipientEmails != null){
////                    //send notification emails...
////                    sendNotificationEmails(receipientEmails, commUuid);
////                }
////                
////                //Save it...
//////                try{
////                    PeonyCommAssignment thePeonyCommAssignment = Lookup.getDefault().lookup(PeonyManagementService.class).getPeonyManagementRestClient()
////                            .storeEntity_XML(PeonyCommAssignment.class, GardenRestParams.Management.storePeonyCommAssignmentRestParams(), targetPeonyCommAssignment);
////                    if (thePeonyCommAssignment == null){
////                        updateMessage("Cannot assign it because of technical issues on the server");
////                        return false;
////                    }
//////                }catch (Throwable ex){
//////                    Exceptions.printStackTrace(ex);
//////                    updateMessage("Cannot assign it because of technical issues on the server. " + ex.getMessage());
//////                    return false;
//////                }
////                return true;
////            }
////
////            @Override
////            protected void succeeded() {
////                try {
////                    if (get()){
////                        PeonyFaceUtils.displayInformationMessageDialog("Successfully assign email(s) to the selected targets.");
////                        broadcastPeonyFaceEventHappened(new CloseTopComponentRequest());
////                    }else{
////                        PeonyFaceUtils.displayErrorMessageDialog("Failed. " + getMessage());
////                    }
////                } catch (InterruptedException | ExecutionException ex) {
////                    //Exceptions.printStackTrace(ex);
////                    PeonyFaceUtils.displayErrorMessageDialog("This operation failed. " + ex.getMessage());
////                }
////            }
////        };
////        this.getSingleExecutorService().submit(assignEmailsTask);
////    }
//
//    private void sendNotificationEmails(String receipientEmails, String commUuid) {
//        String fromEmailAddress = PeonyLocalSettings.getSingleton().getCurrentLoginEmployee().getEmployeeInfo().getWorkEmail();
//        GardenEmailMessage aGardenEmailMessage = new GardenEmailMessage();
//        try{
//            aGardenEmailMessage.setFolderFullName(GardenEmailFolderName.GARDEN_TASK.value());
//            aGardenEmailMessage.setFromList(GardenEmailUtils.convertRecipientTextToEmailAddressList(fromEmailAddress));
//                aGardenEmailMessage.setToList(GardenEmailUtils.convertRecipientTextToEmailAddressList(receipientEmails));
//            aGardenEmailMessage.setCcList(GardenEmailUtils.convertRecipientTextToEmailAddressList(fromEmailAddress));
//            aGardenEmailMessage.setSubject(GardenEmailUtils.generateEmailAssignmentSubject(commUuid));
//            aGardenEmailMessage.setHtmlContent("<b>You are assigned to process the following email(s) by " + fromEmailAddress + "</b>"
//                    + "<hr/>" + getSelectedMessageBriefsWithHtml()
//                    + "<hr/><b>Message for this assignment:</b><p/>" + assignMessageTextArea.getText() 
//                    + "<hr/>Please check out your task assignment for details.");
//            broadcastPeonyFaceEventHappened(new SendGardenEmailMessageRequest(aGardenEmailMessage));
//        }catch (Exception ex){
//        
//        }
//    }
//
//    private void loadG02CommArchiveList(List<G02CommArchive> aG02CommArchiveList, 
//                                        String commUuid, 
//                                        String peonyLoginEmployeeUuid, 
//                                        String peonyLoginEmployeeEmailAddress, 
//                                        String emailSerializationFolder) 
//    {
//        G02CommArchive aG02CommArchive;
//        G02CommArchivePK pkid;
//        for (GardenEmailMessage aGardenEmailMessage : targetEmailMessageList){
//            //save email message itself but not attachment because the saved email already contains everything including attachment
//            aG02CommArchive = new G02CommArchive();
//            pkid = new G02CommArchivePK();
//            pkid.setCommUuid(commUuid);
//            pkid.setCommArchiveLocation(aGardenEmailMessage
//                    .generateEmailMessageSerializedFileFullPath(peonyLoginEmployeeUuid, peonyLoginEmployeeEmailAddress, emailSerializationFolder).toString());
//            aG02CommArchive.setG02CommArchivePK(pkid);
//            aG02CommArchive.setCommTimestamp(aGardenEmailMessage.retrieveEmailTimestamp());
//            aG02CommArchive.setCommMemo("MsgUid: "+aGardenEmailMessage.getMsgUid());
//            //add the archive into the list
//            aG02CommArchiveList.add(aG02CommArchive);
//        }//for-loop
//    }
//
//    private String loadG02CommAssignmentTargetList(List<G02CommAssignmentTarget> aG02CommAssignmentTargetList, 
//                                                 List<String> ssnList, 
//                                                 List<String> einList, 
//                                                 List<PeonyEmployee> selectedEmployees, 
//                                                 String commUuid) 
//    {
//        String receipientEmails = null;
//        G02CommAssignmentTarget aG02CommAssignmentTarget;
//        G02CommAssignmentTargetPK pkid;
//        //selected taxcorp
//        for (String ein : einList){
//            aG02CommAssignmentTarget = new G02CommAssignmentTarget();
//            pkid = new G02CommAssignmentTargetPK();
//            pkid.setCommUuid(commUuid);
//            pkid.setTargetEntityUuid(ein);
//            aG02CommAssignmentTarget.setG02CommAssignmentTargetPK(pkid);
//            aG02CommAssignmentTarget.setTargetEntityType(GardenEntityType.TAXCORP_CASE.name());
//            aG02CommAssignmentTarget.setTargetMemo(ein);
//            aG02CommAssignmentTargetList.add(aG02CommAssignmentTarget);
//        }
//        //selected taxpayer
//        for (String ssn : ssnList){
//            aG02CommAssignmentTarget = new G02CommAssignmentTarget();
//            pkid = new G02CommAssignmentTargetPK();
//            pkid.setCommUuid(commUuid);
//            pkid.setTargetEntityUuid(ssn);
//            aG02CommAssignmentTarget.setG02CommAssignmentTargetPK(pkid);
//            aG02CommAssignmentTarget.setTargetEntityType(GardenEntityType.TAXPAYER_INFO.name());
//            aG02CommAssignmentTarget.setTargetMemo(ssn);
//            aG02CommAssignmentTargetList.add(aG02CommAssignmentTarget);
//        }
//        //selected emplyees and send notification emails
//        if (!selectedEmployees.isEmpty()){
//            for (PeonyEmployee employee : selectedEmployees){
//                aG02CommAssignmentTarget = new G02CommAssignmentTarget();
//                pkid = new G02CommAssignmentTargetPK();
//                pkid.setCommUuid(commUuid);
//                pkid.setTargetEntityUuid(employee.getEmployeeInfo().getEmployeeAccountUuid());
//                aG02CommAssignmentTarget.setG02CommAssignmentTargetPK(pkid);
//                aG02CommAssignmentTarget.setTargetEntityType(GardenEntityType.EMPLOYEE.name());
//                aG02CommAssignmentTarget.setTargetMemo(employee.getEmployeeInfo().getWorkEmail());
//                aG02CommAssignmentTargetList.add(aG02CommAssignmentTarget);
//                if (receipientEmails == null){
//                    receipientEmails = employee.getWorkEmail();
//                }else{
//                    receipientEmails = receipientEmails + ";" + employee.getWorkEmail();
//                }
//            }//
//        }
//        return receipientEmails;
//    }
//
//    private String getSelectedMessageBriefs() {
//        String result = null;
//        for (GardenEmailMessage aGardenEmailMessage : targetEmailMessageList){
//            if (result == null){
//                result = aGardenEmailMessage.retrieveEmailHeadline();
//            }else{
//                result += ZcaNio.lineSeparator() + aGardenEmailMessage.retrieveEmailHeadline();
//            }
//        }
//        return result;
//    }

    private String getSelectedMessageBriefsWithHtml() {
        String result = "<ul>";
        for (GardenEmailMessage aGardenEmailMessage : targetEmailMessageList){
            result += "<li>"+aGardenEmailMessage.retrieveEmailHeadline()+"</li>";
        }
        result += "</ul>";
        return result;
    }

}
