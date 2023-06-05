/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zcomapproach.garden.rose.persistence;

import com.zcomapproach.garden.data.constant.GardenBooleanValue;
import com.zcomapproach.garden.email.data.OfflineMessageStatus;
import com.zcomapproach.garden.persistence.peony.data.QueryNewOfflineEmailCriteria;
import com.zcomapproach.commons.persistent.exception.ZcaEntityValidationException;
import com.zcomapproach.commons.persistent.exception.NonUniqueEntityException;
import com.zcomapproach.garden.guard.GardenMaster;
import com.zcomapproach.garden.guard.TechnicalController;
import com.zcomapproach.garden.guard.RoseWebCipher;
import com.zcomapproach.garden.persistence.G02EntityValidator;
import com.zcomapproach.garden.persistence.query.GardenCommQuery;
import com.zcomapproach.garden.persistence.query.GardenCommQuery.SmsQueryResult;
import com.zcomapproach.garden.persistence.GardenJpaUtils;
import com.zcomapproach.garden.persistence.constant.GardenAccountStatus;
import com.zcomapproach.garden.persistence.constant.GardenEmploymentStatus;
import com.zcomapproach.garden.persistence.entity.G02Account;
import com.zcomapproach.garden.persistence.entity.G02AccountHasPrivilege;
import com.zcomapproach.garden.persistence.entity.G02CommAssignment;
import com.zcomapproach.garden.persistence.entity.G02ContactInfo;
import com.zcomapproach.garden.persistence.entity.G02EmailOperation;
import com.zcomapproach.garden.persistence.entity.G02EmailTag;
import com.zcomapproach.garden.persistence.entity.G02Employee;
import com.zcomapproach.garden.persistence.entity.G02EmployeeBk;
import com.zcomapproach.garden.persistence.entity.G02Location;
import com.zcomapproach.garden.persistence.entity.G02Log;
import com.zcomapproach.garden.persistence.entity.G02OfflineEmail;
import com.zcomapproach.garden.persistence.entity.G02OfflineEmailAttachment;
import com.zcomapproach.garden.persistence.entity.G02User;
import com.zcomapproach.garden.persistence.peony.PeonyAccount;
import com.zcomapproach.garden.persistence.peony.PeonyCommAssignment;
import com.zcomapproach.garden.persistence.peony.PeonyCommAssignmentList;
import com.zcomapproach.garden.persistence.peony.PeonyEmailTag;
import com.zcomapproach.garden.persistence.peony.PeonyEmailTagList;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.persistence.peony.PeonyEmployeeList;
import com.zcomapproach.garden.persistence.peony.PeonyLog;
import com.zcomapproach.garden.persistence.peony.PeonyLogList;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmail;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineEmailList;
import com.zcomapproach.garden.persistence.peony.PeonyOfflineMailbox;
import com.zcomapproach.garden.persistence.peony.PeonyUser;
import com.zcomapproach.garden.persistence.peony.data.PeonyLogName;
import com.zcomapproach.garden.persistence.peony.data.PeonyLogTopic;
import com.zcomapproach.garden.persistence.updater.G02DataUpdaterFactory;
import com.zcomapproach.commons.ZcaValidator;
import com.zcomapproach.garden.persistence.entity.G02DailyReport;
import com.zcomapproach.garden.persistence.entity.G02DailyReportPK;
import com.zcomapproach.garden.persistence.entity.G02JobAssignment;
import com.zcomapproach.garden.persistence.entity.G02Memo;
import com.zcomapproach.garden.persistence.peony.PeonyDailyReport;
import com.zcomapproach.garden.persistence.peony.PeonyDailyReportList;
import com.zcomapproach.garden.persistence.peony.PeonyJob;
import com.zcomapproach.garden.persistence.peony.PeonyJobAssignment;
import com.zcomapproach.garden.persistence.peony.PeonyJobAssignmentList;
import com.zcomapproach.garden.persistence.query.GardenDailyReportQuery;
import com.zcomapproach.garden.persistence.query.GardenDailyReportQuery.GardenDailyReportQueryResult;
import com.zcomapproach.garden.persistence.query.GardenJobAssignmentQuery;
import com.zcomapproach.garden.persistence.query.GardenJobAssignmentQuery.JobAssignmentQueryResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 *
 * @author zhijun98
 */
@Stateless
public class RoseManagementEJB02 extends AbstractDataServiceEJB02 {

    public G02Account findG02AccountByCredentials(String loginName, String loginPassword) {
        G02Account result = null;
        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.findG02AccountByCredentials(em, loginName, loginPassword);
        } catch (NonUniqueEntityException ex) {
            Logger.getLogger(RoseManagementEJB02.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
        } finally {
            em.close();
        }
        
        return result;
    }
    public PeonyOfflineMailbox retrievePeonyOfflineMailbox(QueryNewOfflineEmailCriteria queryNewOfflineEmailCriteria) {
        PeonyOfflineMailbox result = new PeonyOfflineMailbox();

        String employeeAccountUuid = queryNewOfflineEmailCriteria.getEmployeeAccountUuid();
        String employeeEmailAddress = queryNewOfflineEmailCriteria.getEmployeeEmailAddress();
        int batchThreshold = queryNewOfflineEmailCriteria.getBatchThreshold();
        Set<String> filter = queryNewOfflineEmailCriteria.getMsgIdFilterSet();
        /**
         * Get all the related offline email records
         */
        EntityManager em = getEntityManager();
        try {
            String sqlQuery = "SELECT g FROM G02OfflineEmail g "
                    + "WHERE g.ownerUserUuid = :ownerUserUuid "
                    + "AND g.mailboxAddress = :mailboxAddress "
                    + "AND ((g.messageStatus IS NULL) OR (g.messageStatus != :messageStatus))";
            HashMap<String, Object> params = new HashMap<>();
            params.put("ownerUserUuid", employeeAccountUuid);
            params.put("mailboxAddress", employeeEmailAddress);
            params.put("messageStatus", OfflineMessageStatus.DELETED.name());
            List<G02OfflineEmail> aG02OfflineEmailList = GardenJpaUtils.findEntityListByQuery(em, G02OfflineEmail.class, sqlQuery, params);
            if (aG02OfflineEmailList != null){
                /**
                 * sort by timestamp from Z to A before feed into PeonyOfflineMailbox
                 */
                Collections.sort(aG02OfflineEmailList, new Comparator<G02OfflineEmail>(){
                    @Override
                    public int compare(G02OfflineEmail o1, G02OfflineEmail o2) {
                        try{
                            return o1.getCreated().compareTo(o2.getCreated())*(-1);
                        }catch(Exception ex){
                            return 0;
                        }
                    }
                });
                /**
                 * feed into PeonyOfflineMailbox
                 */
                PeonyOfflineEmail aPeonyOfflineEmail;
                int counter = 0;
                for (G02OfflineEmail aG02OfflineEmail : aG02OfflineEmailList){
                    if (batchThreshold <= counter){
                        break;
                    }
                    if (!filter.contains(aG02OfflineEmail.getMsgId())){
                        String entityComboUuid = aG02OfflineEmail.getMailboxAddress()+aG02OfflineEmail.getMsgId();
                        aPeonyOfflineEmail = new PeonyOfflineEmail();
                        aPeonyOfflineEmail.setOfflineEmail(aG02OfflineEmail);
                        /**
                         * NOTICE: this following is purely for performance of query -> G02OfflineEmailAttachment.findByOfflineEmailUuid
                         * Other queries, e.g. email-tags or memo can restore to it also in case of bad performance
                         */
                        if (GardenBooleanValue.Yes.value().equalsIgnoreCase(aG02OfflineEmail.getAttached())){
                            params.clear();
                            params.put("offlineEmailUuid", aG02OfflineEmail.getOfflineEmailUuid());
                            aPeonyOfflineEmail.setOfflineEmailAttachmentList(GardenJpaUtils.findEntityListByNamedQuery(em, 
                                    G02OfflineEmailAttachment.class, "G02OfflineEmailAttachment.findByOfflineEmailUuid", params));
                        }
                        
                        if (GardenBooleanValue.Yes.value().equalsIgnoreCase(aG02OfflineEmail.getTagged())){
                            aPeonyOfflineEmail.setPeonyEmailTagList(GardenJpaUtils.findePeonyEmailTagListByEntityComboUuid(em, entityComboUuid));
                        }
                        
                        if (GardenBooleanValue.Yes.value().equalsIgnoreCase(aG02OfflineEmail.getNoted())){
                            aPeonyOfflineEmail.setPeonyMemoList(GardenJpaUtils.findPeonyMemoListByEntityComboUuid(em, entityComboUuid));
                        }
                        if (OfflineMessageStatus.SEND.value().equalsIgnoreCase(aG02OfflineEmail.getMessageStatus())){
                            result.getSentOfflineEmailList().add(aPeonyOfflineEmail);
                        }else{
                            result.getReceivedOfflineEmailList().add(aPeonyOfflineEmail);
                        }
                        counter++;
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RoseManagementEJB02.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            em.close();
        }
        return result;
    }

    public PeonyOfflineEmail findPeonyOfflineEmailByEmailTagUuid(String emailTagUuid){
        if (ZcaValidator.isNullEmpty(emailTagUuid)){
            return null;
        }
        PeonyOfflineEmail result = null;
        EntityManager em = getEntityManager();
        try {
            G02EmailTag aG02EmailTag = GardenJpaUtils.findById(em, G02EmailTag.class, emailTagUuid);
            if (aG02EmailTag != null){
                result = GardenJpaUtils.findPeonyOfflineEmailByUuid(em, aG02EmailTag.getOfflineEmailUuid());
            }
        } catch (Exception ex) {
            Logger.getLogger(RoseManagementEJB02.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            em.close();
        }
        return result;
    
    }
    
    public PeonyDailyReport findPeonyDailyReportByUuid(String jobAssignmentUuid, String reporterUuid, String reportDate) {
        if (ZcaValidator.isNullEmpty(jobAssignmentUuid) || ZcaValidator.isNullEmpty(reporterUuid) || ZcaValidator.isNullEmpty(reportDate)){
            return null;
        }
        PeonyDailyReport result = null;
        EntityManager em = getEntityManager();
        try {
            G02JobAssignment job = GardenJpaUtils.findById(em, G02JobAssignment.class, jobAssignmentUuid);
            if (job != null){
                G02DailyReport aG02DailyReport;
                G02DailyReportPK pkid = new G02DailyReportPK();
                pkid.setJobAssignmentUuid(jobAssignmentUuid);
                pkid.setReportDate(reportDate);
                pkid.setReporterUuid(reporterUuid);
                aG02DailyReport = GardenJpaUtils.findById(em, G02DailyReport.class, pkid);

                result = new PeonyDailyReport();
                result.setDailyReport(aG02DailyReport);
                result.setHistoricalDailyReports(GardenJpaUtils.findG02DailyReportListByJobAssignmentUuid(em, jobAssignmentUuid));
                result.setJobAssignment(job);
            }
        } catch (Exception ex) {
            Logger.getLogger(RoseManagementEJB02.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            em.close();
        }
        return result;
    
    }
    
    public G02DailyReport findLatestDailyReport(String jobAssignmentUuid, String reporterUuid) {
        if (ZcaValidator.isNullEmpty(jobAssignmentUuid) || ZcaValidator.isNullEmpty(reporterUuid)){
            return null;
        }
        G02DailyReport result = null;
        EntityManager em = getEntityManager();
        try {
            List<G02DailyReport> aG02DailyReportList = GardenJpaUtils.findG02DailyReportListByJobAssignmentUuidAndReporterUuid(em, jobAssignmentUuid, reporterUuid);
            if ((aG02DailyReportList != null) && (!aG02DailyReportList.isEmpty())){
                Collections.sort(aG02DailyReportList, new Comparator<G02DailyReport>(){
                    @Override
                    public int compare(G02DailyReport o1, G02DailyReport o2) {
                        return o1.getUpdated().compareTo(o2.getUpdated());
                    }
                });
                result = aG02DailyReportList.get(0);
            }
        } catch (Exception ex) {
            Logger.getLogger(RoseManagementEJB02.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            em.close();
        }
        return result;
    
    }

    public G02DailyReport findG02DailyReportByUuid(String jobAssignmentUuid, String reporterUuid, String reportDate) {
        if (ZcaValidator.isNullEmpty(jobAssignmentUuid) || ZcaValidator.isNullEmpty(reporterUuid) || ZcaValidator.isNullEmpty(reportDate)){
            return null;
        }
        G02DailyReport result = null;
        EntityManager em = getEntityManager();
        try {
            G02DailyReportPK pkid = new G02DailyReportPK();
            pkid.setJobAssignmentUuid(jobAssignmentUuid);
            pkid.setReportDate(reportDate);
            pkid.setReporterUuid(reporterUuid);
            result = GardenJpaUtils.findById(em, G02DailyReport.class, pkid);
        } catch (Exception ex) {
            Logger.getLogger(RoseManagementEJB02.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            em.close();
        }
        return result;
    }
    
    public PeonyOfflineEmail findPeonyOfflineEmailByUuid(String offlineEmailUuid){
        PeonyOfflineEmail result = null;
        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.findPeonyOfflineEmailByUuid(em, offlineEmailUuid);
        } catch (Exception ex) {
            Logger.getLogger(RoseManagementEJB02.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
        } finally {
            em.close();
        }
        return result;
    }

    /**
     * Three parameters should define only one unique offline message record
     * @param ownerUserUuid
     * @param mailboxAddress
     * @param msgId
     * @return 
     */
    public G02OfflineEmail findG02OfflineEmailByOwnwerAddressMsgId(String ownerUserUuid, String mailboxAddress, String msgId) {
        G02OfflineEmail aG02OfflineEmail = null;
        
        EntityManager em = getEntityManager();
        try {
            String sqlQuery = "SELECT g FROM G02OfflineEmail g WHERE g.ownerUserUuid = :ownerUserUuid AND g.mailboxAddress = :mailboxAddress AND g.msgId = :msgId";
            HashMap<String, Object> params = new HashMap<>();
            params.put("ownerUserUuid", ownerUserUuid);
            params.put("mailboxAddress", mailboxAddress);
            params.put("msgId", msgId);
            
            List<G02OfflineEmail> aG02OfflineEmailList = GardenJpaUtils.findEntityListByQuery(em, G02OfflineEmail.class, sqlQuery, params);
            if ((aG02OfflineEmailList != null) && (!aG02OfflineEmailList.isEmpty())){
                aG02OfflineEmail = aG02OfflineEmailList.get(0);
                if (aG02OfflineEmailList.size() > 1){
                    Logger.getLogger(RoseManagementEJB02.class.getName()).log(Level.SEVERE, null, 
                            new Exception("Three parameters, "+ownerUserUuid+", "+mailboxAddress+", "+msgId+", should not define multiple offline message records!"));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RoseManagementEJB02.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            em.close();
        }
        
        return aG02OfflineEmail;
    }
    
    /**
     * This method simply returns a list of G02OfflineEmail instances embedded in 
     * PeonyOfflineEmailList. There is no other information such as PeonyEmailTag
     * @param employeeAccountUuid
     * @param employeeEmailAddress
     * @return 
     */
    public PeonyOfflineEmailList findPeonyOfflineEmailListByEmployeeAccountUuidAndEmailAddress(String employeeAccountUuid, String employeeEmailAddress) {
        PeonyOfflineEmailList result = new PeonyOfflineEmailList();

        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.findPeonyOfflineEmailListByEmployeeAccountUuidAndEmailAddress(em, employeeAccountUuid, employeeEmailAddress);
        } catch (Exception ex) {
            Logger.getLogger(RoseManagementEJB02.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            em.close();
        }
        return result;
    }
    
    /**
     * Find all the logs of aPeonyLogTopic under the name of employeeAccountUuid
     * 
     * @param aPeonyLogTopic
     * @param employeeAccountUuid
     * @param fromDate
     * @param toDate
     * @return 
     */
    public PeonyLogList findPeonyLogsForTopicByEmployeeByPeriod(PeonyLogTopic aPeonyLogTopic, String employeeAccountUuid, Date fromDate, Date toDate) {
        if ((fromDate == null) || (toDate == null)){
            return findPeonyLogsForTopicByEmployee(aPeonyLogTopic, employeeAccountUuid);
        }
        PeonyLogList result = new PeonyLogList();

        EntityManager em = getEntityManager();
        try {
            List<PeonyLogName> aPeonyLogNameList = PeonyLogTopic.getPeonyLogNameList(aPeonyLogTopic);
            if ((aPeonyLogNameList != null) && (!aPeonyLogNameList.isEmpty())){
                PeonyEmployee aPeonyEmployee = GardenJpaUtils.findPeonyEmployee(em, employeeAccountUuid);
                if (aPeonyEmployee != null){
                    String sqlQuery = "SELECT g FROM G02Log g WHERE g.logName = :logName "
                            + "AND g.operatorAccountUuid = :operatorAccountUuid "
                            + "AND g.created BETWEEN :fromDate AND :toDate ";
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("operatorAccountUuid", employeeAccountUuid);
                    params.put("fromDate", fromDate);
                    params.put("toDate", toDate);
                    List<G02Log> logs = new ArrayList<>();
                    for (PeonyLogName aPeonyLogName : aPeonyLogNameList){
                        params.put("logName", aPeonyLogName.name());
                        logs.addAll(GardenJpaUtils.findEntityListByQuery(em, G02Log.class, sqlQuery, params));
                    }
                    for (G02Log log : logs){
                        result.getLogs().add(new PeonyLog(log, aPeonyEmployee));
                    }
                    result.sortByTimestemp(false);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RoseManagementEJB02.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
        } finally {
            em.close();
        }
        return result;
    }
    
    public PeonyLogList findPeonyLogsForTopicByEmployee(PeonyLogTopic aPeonyLogTopic, String employeeAccountUuid) {
        PeonyLogList result = new PeonyLogList();

        EntityManager em = getEntityManager();
        try {
            List<PeonyLogName> aPeonyLogNameList = PeonyLogTopic.getPeonyLogNameList(aPeonyLogTopic);
            if ((aPeonyLogNameList != null) && (!aPeonyLogNameList.isEmpty())){
                PeonyEmployee aPeonyEmployee = GardenJpaUtils.findPeonyEmployee(em, employeeAccountUuid);
                if (aPeonyEmployee != null){
                    String sqlQuery = "SELECT g FROM G02Log g WHERE g.logName = :logName AND g.operatorAccountUuid = :operatorAccountUuid";
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("operatorAccountUuid", employeeAccountUuid);
                    List<G02Log> logs = new ArrayList<>();
                    for (PeonyLogName aPeonyLogName : aPeonyLogNameList){
                        params.put("logName", aPeonyLogName.name());
                        logs.addAll(GardenJpaUtils.findEntityListByQuery(em, G02Log.class, sqlQuery, params));
                    }
                    for (G02Log log : logs){
                        result.getLogs().add(new PeonyLog(log, aPeonyEmployee));
                    }
                    result.sortByTimestemp(false);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RoseManagementEJB02.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
        } finally {
            em.close();
        }
        return result;
    }
    
    /**
     * Find all the SMS records for a specfic employee
     * @param employeeAccountUuid
     * @param fromDate
     * @param toDate
     * @return 
     */
    public PeonyCommAssignmentList findSmsHistoryListByEmployeeByPeriod(String employeeAccountUuid, Date fromDate, Date toDate) {
        PeonyCommAssignmentList result = new PeonyCommAssignmentList();
        if ((fromDate == null) || (toDate == null)){
            //demand a period
            return result;
        }
        EntityManager em = getEntityManager();
        try {
            PeonyEmployee operator = GardenJpaUtils.findPeonyEmployee(em, employeeAccountUuid);
            if (operator != null){
                List<SmsQueryResult> queryResult = GardenCommQuery.querySmsForEmployeeInPeriod(em, employeeAccountUuid, fromDate, toDate);
                if (queryResult != null){
                    HashMap<String, PeonyCommAssignment> temp = new HashMap<>();
                    PeonyCommAssignment aPeonyCommAssignment;
                    G02CommAssignment aG02CommAssignment;
                    for (SmsQueryResult aSmsQueryResult : queryResult){
                        aG02CommAssignment = aSmsQueryResult.getCommAssignment();
                        aPeonyCommAssignment = temp.get(aG02CommAssignment.getCommUuid());
                        if (aPeonyCommAssignment == null){
                            aPeonyCommAssignment = new PeonyCommAssignment();
                            aPeonyCommAssignment.setOperator(operator);
                            aPeonyCommAssignment.setCommAssignment(aG02CommAssignment);
                            temp.put(aG02CommAssignment.getCommUuid(), aPeonyCommAssignment);
                        }
                        aPeonyCommAssignment.getCommAssignmentTargets().add(aSmsQueryResult.getCommAssignmentTarget());
                    }//for-loop
                    result.getPeonyCommAssignmentList().addAll(new ArrayList<>(temp.values()));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RoseManagementEJB02.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
        } finally {
            em.close();
        }
        return result;
    }

    /**
     * Find PeonyCommAssignmentList which involves employeeAccountUuid. The employee 
     * may be the assigner or receipient
     * @param employeeAccountUuid
     * @return 
     */
    public PeonyCommAssignmentList findPeonyCommAssignmentListByEmployeeAccountUuid(String employeeAccountUuid) {
        PeonyCommAssignmentList result = null;

        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.findPeonyCommAssignmentListForEmployee(em, 
                        GardenJpaUtils.findPeonyEmployee(em, employeeAccountUuid));
        } catch (Exception ex) {
            Logger.getLogger(RoseManagementEJB02.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
        } finally {
            em.close();
        }
        
        return result;
    }

    public PeonyUser findPeonyUserBySsn(String ssn) {
        PeonyUser result = null;

        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.findPeonyUserBySsn(em, ssn);
        } catch (Exception ex) {
            Logger.getLogger(RoseManagementEJB02.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
        } finally {
            em.close();
        }
        
        return result;
    }

    public PeonyEmployee findPeonyEmployeeByCredentials(String loginName, String loginPassword) {
        PeonyEmployee result = null;
        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.findPeonyEmployee(em, GardenJpaUtils.findG02AccountByCredentials(em, loginName, loginPassword));
        } catch (NonUniqueEntityException ex) {
            Logger.getLogger(RoseManagementEJB02.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
        } finally {
            em.close();
        }
        
        return result;
    }

    public G02Employee findG02EmployeeByUuid(String uuid) {
        G02Employee result = null;
        EntityManager em = getEntityManager();
        try {
            result = GardenJpaUtils.findById(em, G02Employee.class, uuid);
        } finally {
            em.close();
        }
        return result;
    }

    public PeonyEmployeeList findPeonyEmployeeList() {
        PeonyEmployeeList result = new PeonyEmployeeList();

        EntityManager em = getEntityManager();
        try {
            List<G02Employee> aG02EmployeeList = GardenJpaUtils.findAll(em, G02Employee.class);
            if (aG02EmployeeList != null){
                List<PeonyEmployee> aPeonyEmployeeList = new ArrayList<>();
                String employeeAccountUuid;
                PeonyEmployee aPeonyEmployee;
                for (G02Employee aG02Employee : aG02EmployeeList){
                    employeeAccountUuid = aG02Employee.getEmployeeAccountUuid();
                    aPeonyEmployee = GardenJpaUtils.findPeonyEmployee(em, employeeAccountUuid);
                    if (!PeonyEmployee.isGardenMaster(employeeAccountUuid)){
                        aPeonyEmployeeList.add(aPeonyEmployee);
                    }
                }
                result.setPeonyEmployeeList(aPeonyEmployeeList);
            }
        } catch (Exception ex) {
            Logger.getLogger(RoseManagementEJB02.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
        } finally {
            em.close();
        }
        
        return result;
    }
    
    /**
     * Find all the job records for a specfic employee in a period
     * @param employeeAccountUuid
     * @param fromDate
     * @param toDate
     * @return 
     */
    public PeonyJobAssignmentList findPeonyJobAssignmentListForEmployeeByPeriod(String employeeAccountUuid, Date fromDate, Date toDate) {
        PeonyJobAssignmentList result = new PeonyJobAssignmentList();
        EntityManager em = getEntityManager();
        try {
            PeonyEmployee aPeonyEmployee = GardenJpaUtils.findPeonyEmployee(em, employeeAccountUuid);
            if (aPeonyEmployee != null){
                List<JobAssignmentQueryResult> queryResult;
                if ((fromDate == null) || (toDate == null)){
                    queryResult = GardenJobAssignmentQuery.queryJobAssignments(em, employeeAccountUuid);
                }else{
                    queryResult = GardenJobAssignmentQuery.queryJobAssignmentsByPeriod(em, employeeAccountUuid, fromDate, toDate);
                }
                if (queryResult != null){
                    HashMap<String, PeonyJobAssignment> tempPeonyJobAssignments = new HashMap<>();
                    PeonyJobAssignment aPeonyJobAssignment;
                    G02JobAssignment aG02JobAssignment;
                    G02User assignerUserInfo;
                    for (JobAssignmentQueryResult aQueryResult : queryResult){
                        aG02JobAssignment = aQueryResult.getJobAssignment();
                        if (aG02JobAssignment != null){
                            aPeonyJobAssignment = tempPeonyJobAssignments.get(aG02JobAssignment.getJobAssignmentUuid());
                            if (aPeonyJobAssignment == null){
                                aPeonyJobAssignment = new PeonyJobAssignment();
                                aPeonyJobAssignment.setJobAssignment(aG02JobAssignment);
                                
                                assignerUserInfo = GardenJpaUtils.findById(em, G02User.class, aG02JobAssignment.getAssignFromUuid());
                                if (assignerUserInfo != null){
                                    aPeonyJobAssignment.setAssignerFullName(assignerUserInfo.getLastName() + ", " + assignerUserInfo.getFirstName());
                                }
                                
                                tempPeonyJobAssignments.put(aG02JobAssignment.getJobAssignmentUuid(), aPeonyJobAssignment);
                            }
                            //Job-memo and its related target tax case
                            aPeonyJobAssignment.setJobMemo(aQueryResult.getJobContent());
                        }
                    }//for-loop
                    result.getPeonyJobAssignmentList().addAll(new ArrayList<>(tempPeonyJobAssignments.values()));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RoseManagementEJB02.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
        } finally {
            em.close();
        }
        return result;
    }
    
    public PeonyDailyReportList findPeonyDailyReportListForEmployeeByPeriod(String employeeAccountUuid, Date fromDate, Date toDate) {
        PeonyDailyReportList result = new PeonyDailyReportList();
        EntityManager em = getEntityManager();
        try {
            PeonyEmployee aPeonyEmployee = GardenJpaUtils.findPeonyEmployee(em, employeeAccountUuid);
            if (aPeonyEmployee != null){
                List<GardenDailyReportQueryResult> queryResults;
                if ((fromDate == null) || (toDate == null)){
                    throw new Exception("Cannot query for unlimited period");
                }else{
                    queryResults = GardenDailyReportQuery.queryPeonyDailyReportsByPeriod(em, employeeAccountUuid, fromDate, toDate);
                }
                List<PeonyDailyReport> aPeonyDailyReportList = new ArrayList<>();
                PeonyDailyReport aPeonyDailyReport;
                for (GardenDailyReportQueryResult queryResult : queryResults){
                    aPeonyDailyReport = new PeonyDailyReport();
                    
                    aPeonyDailyReport.setDailyReport(queryResult.getDailyReport());
                    aPeonyDailyReport.setJobAssignment(queryResult.getJobAssignment());
                    aPeonyDailyReport.setJobContent(GardenJpaUtils.findById(em, G02Memo.class, queryResult.getJobAssignment().getJobUuid()));
                    
                    aPeonyDailyReportList.add(aPeonyDailyReport);
                }
                result.setPeonyDailyReportList(aPeonyDailyReportList);
            }
        } catch (Exception ex) {
            Logger.getLogger(RoseManagementEJB02.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
        } finally {
            em.close();
        }
        return result;
    }
    
    public PeonyDailyReportList findPeonyDailyReportListForEmployeeByReportDate(String employeeAccountUuid, String reportDate) {
        PeonyDailyReportList result = new PeonyDailyReportList();
        EntityManager em = getEntityManager();
        try {
            PeonyEmployee aPeonyEmployee = GardenJpaUtils.findPeonyEmployee(em, employeeAccountUuid);
            if (aPeonyEmployee != null){
                if (ZcaValidator.isNotNullEmpty(reportDate)){
                    List<GardenDailyReportQueryResult> queryResultList = GardenDailyReportQuery.queryPeonyDailyReportsByReportDate(em, employeeAccountUuid, reportDate);
                    if (queryResultList != null){
                        List<PeonyDailyReport> aPeonyDailyReportList = new ArrayList<>();
                        PeonyDailyReport aPeonyDailyReport;
                        for (GardenDailyReportQueryResult queryResult : queryResultList){
                            aPeonyDailyReport = new PeonyDailyReport();
                            aPeonyDailyReport.setDailyReport(queryResult.getDailyReport());
                            aPeonyDailyReport.setJobAssignment(queryResult.getJobAssignment());
                            aPeonyDailyReport.setJobContent(GardenJpaUtils.findById(em, G02Memo.class, queryResult.getJobAssignment().getJobUuid()));
                            aPeonyDailyReportList.add(aPeonyDailyReport);
                        }
                        result.setPeonyDailyReportList(aPeonyDailyReportList);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RoseManagementEJB02.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
        } finally {
            em.close();
        }
        return result;
    }

    public PeonyDailyReportList findPeonyDailyReportListByJobAssignmentUuid(String jobAssignmentUuid) {
        PeonyDailyReportList result = new PeonyDailyReportList();
        EntityManager em = getEntityManager();
        try {
            if (ZcaValidator.isNotNullEmpty(jobAssignmentUuid)){
                List<G02DailyReport> aG02DailyReportList = GardenJpaUtils.findG02DailyReportListByJobAssignmentUuid(em, jobAssignmentUuid);
                if (aG02DailyReportList != null){
                    PeonyJobAssignment aPeonyJobAssignment = new PeonyJobAssignment();
                    G02JobAssignment jobAssignment = GardenJpaUtils.findById(em, G02JobAssignment.class, jobAssignmentUuid);
                    if (jobAssignment != null){
                        aPeonyJobAssignment.setJobMemo(GardenJpaUtils.findById(em, G02Memo.class, jobAssignment.getJobUuid()));
                    }
                    aPeonyJobAssignment.setJobAssignment(jobAssignment);
                    result.setTargetPeonyJobAssignment(aPeonyJobAssignment);
                    //daily reports
                    List<PeonyDailyReport> aPeonyDailyReportList = new ArrayList<>();
                    PeonyDailyReport aPeonyDailyReport;
                    for (G02DailyReport aG02DailyReport : aG02DailyReportList){
                        aPeonyDailyReport = new PeonyDailyReport();
                        aPeonyDailyReport.setDailyReport(aG02DailyReport);
                        //aPeonyDailyReport.setJobAssignment(jobAssignment);
                        //aPeonyDailyReport.setJobContent(jobContent);
                        aPeonyDailyReportList.add(aPeonyDailyReport);
                    }
                    result.setPeonyDailyReportList(aPeonyDailyReportList);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RoseManagementEJB02.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
        } finally {
            em.close();
        }
        return result;
    }
    
    public G02JobAssignment storeG02JobAssignment(G02JobAssignment aG02JobAssignment) throws Exception {
        if (aG02JobAssignment == null){
            throw new Exception("Cannot store aG02JobAssignment because of bad data");
        }      
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            G02EntityValidator.getSingleton().validate(aG02JobAssignment);
            
            GardenJpaUtils.storeEntity(em, G02JobAssignment.class, aG02JobAssignment, aG02JobAssignment.getJobAssignmentUuid(), 
                                        G02DataUpdaterFactory.getSingleton().getG02JobAssignmentUpdater());
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aG02JobAssignment;
    }
    
    public G02DailyReport storeG02DailyReport(G02DailyReport aG02DailyReport) throws Exception {
        if (aG02DailyReport == null){
            throw new Exception("Cannot store aG02DailyReport because of bad data");
        }      
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            G02EntityValidator.getSingleton().validate(aG02DailyReport);
            
            GardenJpaUtils.storeEntity(em, G02DailyReport.class, aG02DailyReport, aG02DailyReport.getG02DailyReportPK(), 
                                        G02DataUpdaterFactory.getSingleton().getG02DailyReportUpdater());
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aG02DailyReport;
    }

    public PeonyJob storePeonyJob(PeonyJob aPeonyJob) throws Exception {
        if ((aPeonyJob == null) || (aPeonyJob.getJobCreator() == null)){
            throw new Exception("Cannot store aPeonyJob because of bad data");
        }
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            G02EntityValidator.getSingleton().validate(aPeonyJob);
            
            GardenJpaUtils.storeEntity(em, G02Memo.class, aPeonyJob.getJobContent(), aPeonyJob.getJobContent().getMemoUuid(), 
                                        G02DataUpdaterFactory.getSingleton().getG02MemoUpdater());
            
            GardenJpaUtils.storeEntity(em, G02Memo.class, aPeonyJob.getJobAcceptorMemo(), aPeonyJob.getJobAcceptorMemo().getMemoUuid(), 
                                        G02DataUpdaterFactory.getSingleton().getG02MemoUpdater());
            if (aPeonyJob.getJobNoteMemo() != null){
                GardenJpaUtils.storeEntity(em, G02Memo.class, aPeonyJob.getJobNoteMemo(), aPeonyJob.getJobNoteMemo().getMemoUuid(), 
                                            G02DataUpdaterFactory.getSingleton().getG02MemoUpdater());
            }
            
            List<PeonyJobAssignment> assignments = aPeonyJob.getJobAssignmentList();
            String jobAssignmentUuid;
            for (PeonyJobAssignment assignment : assignments){
                jobAssignmentUuid = assignment.getJobAssignment().getJobAssignmentUuid();
                GardenJpaUtils.storeEntity(em, G02JobAssignment.class, assignment.getJobAssignment(), jobAssignmentUuid, G02DataUpdaterFactory.getSingleton().getG02JobAssignmentUpdater());
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aPeonyJob;
    }

    public G02Location storeLocation(G02Location aG02Location) throws Exception {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            G02EntityValidator.getSingleton().validate(aG02Location);
            
            G02User aG02User = GardenJpaUtils.findById(em, G02User.class, aG02Location.getEntityUuid());
            if (aG02User == null){
                throw new Exception("Address location is not NOT saved. Please click 'Save Profile' to save this information");
            }else{
                GardenJpaUtils.storeEntity(em, G02Location.class, aG02Location, aG02Location.getLocationUuid(), 
                                            G02DataUpdaterFactory.getSingleton().getG02LocationUpdater());
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aG02Location;
    }

    public G02Location deleteLocation(String locationUuid) throws Exception {
        G02Location aG02Location = null;
        if (ZcaValidator.isNotNullEmpty(locationUuid)){
            //store...
            EntityManager em = null;
            UserTransaction utx =  getUserTransaction();
            try {
                utx.begin();
                em = getEntityManager();

                aG02Location = GardenJpaUtils.deleteEntity(em, G02Location.class, locationUuid);

                utx.commit();
            } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    utx.rollback();
                } catch (IllegalStateException | SecurityException | SystemException ex1) {
                    Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                    throw ex1;
                }
                throw ex;
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        }

        return aG02Location;
    }

    public G02ContactInfo storeContactInfo(G02ContactInfo aG02ContactInfo) throws Exception {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            G02EntityValidator.getSingleton().validate(aG02ContactInfo);
            
            G02User aG02User = GardenJpaUtils.findById(em, G02User.class, aG02ContactInfo.getEntityUuid());
            if (aG02User == null){
                throw new Exception("Address contactInfo is not NOT saved. Please click 'Save Profile' to save this information");
            }else{
                GardenJpaUtils.storeEntity(em, G02ContactInfo.class, aG02ContactInfo, aG02ContactInfo.getContactInfoUuid(), 
                                            G02DataUpdaterFactory.getSingleton().getG02ContactInfoUpdater());
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aG02ContactInfo;
    }

    public G02ContactInfo deleteContactInfo(String contactInfoUuid) throws Exception {
        G02ContactInfo aG02ContactInfo = null;
        if (ZcaValidator.isNotNullEmpty(contactInfoUuid)){
            //store...
            EntityManager em = null;
            UserTransaction utx =  getUserTransaction();
            try {
                utx.begin();
                em = getEntityManager();

                aG02ContactInfo = GardenJpaUtils.deleteEntity(em, G02ContactInfo.class, contactInfoUuid);

                utx.commit();
            } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    utx.rollback();
                } catch (IllegalStateException | SecurityException | SystemException ex1) {
                    Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                    throw ex1;
                }
                throw ex;
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        }

        return aG02ContactInfo;
    }
    
    private void deleteG02AccountHasPrivilegeListByAccountUuid(String accountUuid) throws Exception{
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            HashMap<String, Object> params = new HashMap<>();
            params.put("accountUuid", accountUuid);
            List<G02AccountHasPrivilege> aG02AccountHasPrivilegeList = GardenJpaUtils.findEntityListByNamedQuery(em, 
                    G02AccountHasPrivilege.class, "G02AccountHasPrivilege.findByAccountUuid", params);
            if (aG02AccountHasPrivilegeList != null){
                for (G02AccountHasPrivilege aG02AccountHasPrivilege : aG02AccountHasPrivilegeList){
                    GardenJpaUtils.deleteEntity(em, G02AccountHasPrivilege.class, aG02AccountHasPrivilege.getG02AccountHasPrivilegePK());
                }
            }
            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public PeonyAccount storePeonyAccount(PeonyAccount aPeonyAccount) throws Exception {
        PeonyAccount result = null;
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            G02EntityValidator.getSingleton().validate(aPeonyAccount);
            
            result = GardenJpaUtils.storePeonyAccount(em, aPeonyAccount);
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return result;
    }

    public PeonyEmployee storePeonyEmployeePrivileges(PeonyEmployee aPeonyEmployee) throws Exception {
        try{
            deleteG02AccountHasPrivilegeListByAccountUuid(aPeonyEmployee.getAccount().getAccountUuid());
        }catch (Exception ex){
            throw new ZcaEntityValidationException("Cannot delete old privileges for account. " + ex.getMessage());
        }
        
        PeonyEmployee result = null;
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            G02EntityValidator.getSingleton().validateG02PrivilegeList(aPeonyEmployee.getPrivilegeList());
            
            G02Account account = GardenJpaUtils.findById(em, G02Account.class, aPeonyEmployee.getAccount().getAccountUuid());
            if (account == null){
                throw new ZcaEntityValidationException("Cannot find account for saving privileges");
            }
            
            GardenJpaUtils.storeG02AccountHasPrivilegeList(em, aPeonyEmployee.getAccount(), aPeonyEmployee.getPrivilegeList());
            
            result = aPeonyEmployee;
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return result;
    }
    
    public G02EmailOperation storeG02EmailOperation(G02EmailOperation aG02EmailOperation) throws Exception {
        if (aG02EmailOperation == null){
            return null;
        }
        G02EmailOperation result = null;
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            G02EntityValidator.getSingleton().validate(aG02EmailOperation);
            
            GardenJpaUtils.storeEntity(em, G02EmailOperation.class, aG02EmailOperation, 
                                    aG02EmailOperation.getOperationUuid(), 
                                    G02DataUpdaterFactory.getSingleton().getG02EmailOperationUpdater());
            
            result = aG02EmailOperation;
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return result;
    }
    
    /**
     * This method is used by the online mailbix. If some email were retrieved by 
     * the online mailbox, they will be stored in the database. If it was retrieved 
     * by the online mailbox before, it will be ignored. It does not take the resposibility 
     * of updating the instance. It only records the fact that a mail message was 
     * retrieved by the online mailbox.
     * 
     * @deprecated - this was used by GoogleEmailBox/GoogleEmailBoxController
     * @param aPeonyOfflineEmailList
     * @return
     * @throws Exception 
     */
    public PeonyOfflineEmailList notifyPeonyOfflineEmailListRetrieved(PeonyOfflineEmailList aPeonyOfflineEmailList) throws Exception {
        if ((aPeonyOfflineEmailList == null) || (aPeonyOfflineEmailList.getPeonyOfflineEmailList().isEmpty())){
            return null;
        }
        PeonyOfflineEmailList result = null;
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            result = GardenJpaUtils.notifyPeonyOfflineEmailListRetrieved(em, aPeonyOfflineEmailList);
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return result;
    }
    
    public PeonyOfflineEmailList storePeonyOfflineEmailList(PeonyOfflineEmailList aPeonyOfflineEmailList) throws Exception {
        if ((aPeonyOfflineEmailList == null) || (aPeonyOfflineEmailList.getPeonyOfflineEmailList().isEmpty())){
            return null;
        }
        PeonyOfflineEmailList result = null;
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            List<PeonyOfflineEmail> thePeonyOfflineEmailList = aPeonyOfflineEmailList.getPeonyOfflineEmailList();
            for (PeonyOfflineEmail aPeonyOfflineEmail : thePeonyOfflineEmailList){
                GardenJpaUtils.storePeonyOfflineEmail(em, aPeonyOfflineEmail);
            }//result
            
            result = aPeonyOfflineEmailList;
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return result;
    }

    public PeonyEmailTagList storePeonyEmailTagList(PeonyEmailTagList aPeonyEmailTagList) throws Exception{
        if (aPeonyEmailTagList == null) {
            return null;
        }
        PeonyEmailTagList result = new PeonyEmailTagList();
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            List<PeonyEmailTag> thePeonyEmailTagList = aPeonyEmailTagList.getPeonyEmailTagList();
            for (PeonyEmailTag aPeonyEmailTag : thePeonyEmailTagList){
                aPeonyEmailTag = GardenJpaUtils.storePeonyEmailTagHelper(em, aPeonyEmailTag);
                if (aPeonyEmailTag != null){
                    result.getPeonyEmailTagList().add(aPeonyEmailTag);
                }
            }
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return result;
    }

    public PeonyEmailTag storePeonyEmailTag(PeonyEmailTag aPeonyEmailTag) throws Exception {
        if (aPeonyEmailTag == null) {
            return null;
        }
        PeonyEmailTag result = null;
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            result = GardenJpaUtils.storePeonyEmailTagHelper(em, aPeonyEmailTag);
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            result = null;
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return result;
    }

    /**
     * Update messageStatus of G02OfflineEmail(s) whose fields have ownerUserUuid, 
     * mailboxAddress, and msgId
     * 
     * @param ownerUserUuid
     * @param mailboxAddress
     * @param msgId
     * @param messageStatus
     * @return
     * @throws Exception 
     */
    public G02OfflineEmail requestOfflineEmailStatusUpdate(String ownerUserUuid, String mailboxAddress, String msgId, String messageStatus) throws Exception{
        if (ZcaValidator.isNullEmpty(ownerUserUuid) || ZcaValidator.isNullEmpty(mailboxAddress) || ZcaValidator.isNullEmpty(msgId) || ZcaValidator.isNullEmpty(messageStatus)){
            return null;
        }
        G02OfflineEmail result = null;
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            List<G02OfflineEmail> aG02OfflineEmailList = GardenJpaUtils.findOfflineEmailListByServerSideIdentities(em, ownerUserUuid, mailboxAddress, msgId);
            if ((aG02OfflineEmailList != null) && (!aG02OfflineEmailList.isEmpty())){
                for (G02OfflineEmail aG02OfflineEmail : aG02OfflineEmailList){
                    aG02OfflineEmail.setMessageStatus(messageStatus);
                    GardenJpaUtils.storeEntity(em, G02OfflineEmail.class, aG02OfflineEmail, 
                                               aG02OfflineEmail.getOfflineEmailUuid(), 
                                               G02DataUpdaterFactory.getSingleton().getG02OfflineEmailUpdater());
                }
                result = aG02OfflineEmailList.get(0);
            }
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return result;
    }

    /**
     * Update messageStatus field for the G02OfflineEmail instance whose ID is offlineEmailUuid
     * @param offlineEmailUuid
     * @param messageStatus
     * @return
     * @throws Exception 
     */
    public G02OfflineEmail requestOfflineEmailStatusUpdateByOfflineEmailUuid(String offlineEmailUuid, String messageStatus) throws Exception{
        if (ZcaValidator.isNullEmpty(offlineEmailUuid) || ZcaValidator.isNullEmpty(messageStatus)){
            return null;
        }
        G02OfflineEmail result = null;
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            result = GardenJpaUtils.findById(em, G02OfflineEmail.class, offlineEmailUuid);
            if (result != null){
                result.setMessageStatus(messageStatus);
                GardenJpaUtils.storeEntity(em, G02OfflineEmail.class, result, 
                                           offlineEmailUuid, 
                                           G02DataUpdaterFactory.getSingleton().getG02OfflineEmailUpdater());
            }
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return result;
    }
    
    public G02OfflineEmail storeG02OfflineEmail(G02OfflineEmail aG02OfflineEmail) throws Exception {
        if (aG02OfflineEmail == null){
            return null;
        }
        G02OfflineEmail result = null;
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            G02EntityValidator.getSingleton().validate(aG02OfflineEmail);
            
            GardenJpaUtils.storeEntity(em, G02OfflineEmail.class, aG02OfflineEmail, 
                                    aG02OfflineEmail.getOfflineEmailUuid(), 
                                    G02DataUpdaterFactory.getSingleton().getG02OfflineEmailUpdater());
            
            result = aG02OfflineEmail;
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return result;
    }

    public G02Employee storeG02Employee(G02Employee aG02Employee) throws Exception {
        G02Employee result = null;
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            G02EntityValidator.getSingleton().validate(aG02Employee);
            
            result = GardenJpaUtils.storeEntity(em, G02Employee.class, aG02Employee, 
                    aG02Employee.getEmployeeAccountUuid(), G02DataUpdaterFactory.getSingleton().getG02EmployeeUpdater());
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return result;
    }

    public PeonyEmployee storePeonyEmployee(PeonyEmployee aPeonyEmployee) throws Exception {
        PeonyEmployee result = null;
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            G02EntityValidator.getSingleton().validate(aPeonyEmployee);
            
            result = GardenJpaUtils.storePeonyEmployee(em, aPeonyEmployee);
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return result;
    }

    public PeonyCommAssignmentList storePeonyCommAssignmentList(PeonyCommAssignmentList peonyCommAssignmentList) throws Exception {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            G02EntityValidator.getSingleton().validate(peonyCommAssignmentList);
            List<PeonyCommAssignment> aPeonyCommAssignmentList = peonyCommAssignmentList.getPeonyCommAssignmentList();
            for (PeonyCommAssignment aPeonyCommAssignment : aPeonyCommAssignmentList){
                GardenJpaUtils.storePeonyCommAssignment(em, aPeonyCommAssignment);
            }
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            peonyCommAssignmentList = null;
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return peonyCommAssignmentList;
    }

    public PeonyCommAssignment storePeonyCommAssignment(PeonyCommAssignment aPeonyCommAssignment) throws Exception {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            G02EntityValidator.getSingleton().validate(aPeonyCommAssignment);
            GardenJpaUtils.storePeonyCommAssignment(em, aPeonyCommAssignment);
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            aPeonyCommAssignment = null;
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return aPeonyCommAssignment;
    }

    public G02Employee removeEmployee(Class<G02Employee> aClass, String employeeAccountUuid) throws Exception {
        G02Employee aG02Employee = null;
        if (ZcaValidator.isNotNullEmpty(employeeAccountUuid)){
            //store...
            EntityManager em = null;
            UserTransaction utx =  getUserTransaction();
            try {
                utx.begin();
                em = getEntityManager();

                aG02Employee = GardenJpaUtils.deleteEntity(em, G02Employee.class, employeeAccountUuid);
                if (aG02Employee != null){
                    G02EmployeeBk aG02EmployeeBk = new G02EmployeeBk();
                    
                    aG02EmployeeBk.setCreated(aG02Employee.getCreated());
                    aG02EmployeeBk.setEmployedDate(aG02Employee.getEmployedDate());
                    aG02EmployeeBk.setEmployeeAccountUuid(aG02Employee.getEmployeeAccountUuid());
                    aG02EmployeeBk.setEmploymentStatus(aG02Employee.getEmploymentStatus());
                    aG02EmployeeBk.setEntityStatus(aG02Employee.getEntityStatus());
                    aG02EmployeeBk.setMemo(aG02Employee.getMemo());
                    aG02EmployeeBk.setUpdated(aG02Employee.getUpdated());
                    aG02EmployeeBk.setWorkEmail(aG02Employee.getWorkEmail());
                    aG02EmployeeBk.setWorkPhone(aG02Employee.getWorkPhone());
                    aG02EmployeeBk.setWorkTitle(aG02Employee.getWorkTitle());
                    
                    GardenJpaUtils.storeEntity(em, G02EmployeeBk.class, aG02EmployeeBk, 
                        aG02EmployeeBk.getEmployeeAccountUuid(), G02DataUpdaterFactory.getSingleton().getG02EmployeeBkUpdater());
                }
                utx.commit();
            } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
                try {
                    utx.rollback();
                } catch (IllegalStateException | SecurityException | SystemException ex1) {
                    Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                    throw ex1;
                }
                throw ex;
            } finally {
                if (em != null) {
                    em.close();
                }
            }
        }

        return aG02Employee;
    }
    
    /**
     * RoseSettingsBean launch this method so as to update the information of GardenMaster account
     */
    public void storeGardenMasterAccount() throws Exception {
        
        deleteDeprecatedGardenMasterAccount();
        
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            String uuid = GardenMaster.Master_UUID.value();
            
            G02User aG02User = new G02User();
            aG02User.setUserUuid(uuid);
            aG02User.setFirstName(GardenMaster.FIRST_NAME.value());
            aG02User.setLastName(GardenMaster.LAST_NAME.value());
            
            G02Account aG02Account = new G02Account();
            aG02Account.setAccountUuid(uuid);
            aG02Account.setLoginName(GardenMaster.LOGIN_NAME.value());
            aG02Account.setEncryptedPassword(RoseWebCipher.getSingleton().encrypt(GardenMaster.PASSWORD.value()));
            aG02Account.setAccountStatus(GardenAccountStatus.Special.name());
            
            G02Employee aG02Employee = new G02Employee();
            aG02Employee.setEmployeeAccountUuid(uuid);
            aG02Employee.setEntityStatus(GardenMaster.WORK_TITLE.value());
            aG02Employee.setEmploymentStatus(GardenEmploymentStatus.FULL_TIME_EMPLOYEE.value());
            aG02Employee.setWorkEmail(GardenMaster.WORK_EMAIL.value());
            aG02Employee.setWorkTitle(GardenMaster.WORK_TITLE.value());
            
//            G02Privilege aG02Privilege = new G02Privilege();
//            aG02Privilege.setPrivilegeUuid(uuid);
//            aG02Privilege.setFlowerName("Garden");
//            aG02Privilege.setPrivilege(GardenPrivilege.SUPER_POWER.value());
//            aG02Privilege.setMemo("Master of the entire Garden system");
//            aG02Privilege.setTimestamp(new Date());
//            
//            List<G02Privilege> aG02PrivilegeList = new ArrayList<>();
//            aG02PrivilegeList.add(aG02Privilege);
            
            GardenJpaUtils.storeEntity(em, G02User.class,  aG02User, aG02User.getUserUuid(), 
                    G02DataUpdaterFactory.getSingleton().getG02UserUpdater());
            GardenJpaUtils.storeEntity(em, G02Account.class,  aG02Account, aG02Account.getAccountUuid(), 
                    G02DataUpdaterFactory.getSingleton().getG02AccountUpdater());
            GardenJpaUtils.storeEntity(em, G02Employee.class,  aG02Employee, aG02Employee.getEmployeeAccountUuid(), 
                    G02DataUpdaterFactory.getSingleton().getG02EmployeeUpdater());
//            GardenJpaUtils.storeEntity(em,G02Privilege.class,  aG02Privilege, aG02Privilege.getPrivilegeUuid(), 
//                    G02DataUpdaterFactory.getSingleton().getG02PrivilegeUpdater());
//            GardenJpaUtils.storeG02AccountHasPrivilegeList(em, aG02Account, aG02PrivilegeList);
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    private void deleteDeprecatedGardenMasterAccount() throws Exception {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            String deprecatedUuid = GardenMaster.Master_DEPRECATED_UUID.value();
            G02User aG02User = GardenJpaUtils.findById(em, G02User.class, deprecatedUuid);
            if (aG02User != null){
                GardenJpaUtils.deleteEntity(em, G02User.class, deprecatedUuid);
            }
            G02Account aG02Account = GardenJpaUtils.findById(em, G02Account.class, deprecatedUuid);
            if (aG02Account != null){
                GardenJpaUtils.deleteEntity(em, G02Account.class, deprecatedUuid);
            }
            G02Employee aG02Employee = GardenJpaUtils.findById(em, G02Employee.class, deprecatedUuid);
            if (aG02Employee != null){
                GardenJpaUtils.deleteEntity(em, G02Employee.class, deprecatedUuid);
            }
            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * RoseSettingsBean launch this method so as to update the information of TechnicalController account
     */
    public void storeTechnicalControllerAccount() throws Exception {
        
        deleteDeprecatedTechnicalControllerAccount();
        
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            PeonyEmployee employee = TechnicalController.getEmployeeAccount();
            
            GardenJpaUtils.storeEntity(em, G02User.class,  employee.getUser(), employee.getUser().getUserUuid(), 
                    G02DataUpdaterFactory.getSingleton().getG02UserUpdater());
            GardenJpaUtils.storeEntity(em, G02Account.class,  employee.getAccount(), employee.getAccount().getAccountUuid(), 
                    G02DataUpdaterFactory.getSingleton().getG02AccountUpdater());
            GardenJpaUtils.storeEntity(em, G02Employee.class,  employee.getEmployeeInfo(), employee.getEmployeeInfo().getEmployeeAccountUuid(), 
                    G02DataUpdaterFactory.getSingleton().getG02EmployeeUpdater());
//            GardenJpaUtils.storeEntity(em,G02Privilege.class,  aG02Privilege, aG02Privilege.getPrivilegeUuid(), 
//                    G02DataUpdaterFactory.getSingleton().getG02PrivilegeUpdater());
//            GardenJpaUtils.storeG02AccountHasPrivilegeList(em, aG02Account, aG02PrivilegeList);
            
            utx.commit();
        } catch (ZcaEntityValidationException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    private void deleteDeprecatedTechnicalControllerAccount() throws Exception {
        //store...        
        EntityManager em = null;
        UserTransaction utx =  getUserTransaction();
        try {
            utx.begin();
            em = getEntityManager();
            
            String deprecatedUuid = TechnicalController.Controller_DEPRECATED_UUID.value();
            G02User aG02User = GardenJpaUtils.findById(em, G02User.class, deprecatedUuid);
            if (aG02User != null){
                GardenJpaUtils.deleteEntity(em, G02User.class, deprecatedUuid);
            }
            G02Account aG02Account = GardenJpaUtils.findById(em, G02Account.class, deprecatedUuid);
            if (aG02Account != null){
                GardenJpaUtils.deleteEntity(em, G02Account.class, deprecatedUuid);
            }
            G02Employee aG02Employee = GardenJpaUtils.findById(em, G02Employee.class, deprecatedUuid);
            if (aG02Employee != null){
                GardenJpaUtils.deleteEntity(em, G02Employee.class, deprecatedUuid);
            }
            utx.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException | NotSupportedException ex) {
            Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex);
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                Logger.getLogger(RoseBusinessEJB02.class.getName()).log(Level.SEVERE, null, ex1);
                throw ex1;
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<G02OfflineEmailAttachment> findG02OfflineEmailAttachmentListByOfflineEmailUuid(String offlineEmailUuid) {
        List<G02OfflineEmailAttachment> result = null;
        EntityManager em = getEntityManager();
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("offlineEmailUuid", offlineEmailUuid);
            result = GardenJpaUtils.findEntityListByNamedQuery(em, G02OfflineEmailAttachment.class, "G02OfflineEmailAttachment.findByOfflineEmailUuid", params);
        } finally {
            em.close();
        }
        return result;
    }

}
