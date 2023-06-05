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

package com.zcomapproach.garden.peony.view;

import com.zcomapproach.garden.peony.view.worker.PeonyLocalBackup;
import com.zcomapproach.garden.peony.view.worker.PeonyArchiveSynchronizer;
import com.zcomapproach.garden.peony.kernel.PeonyServiceProvider;
import com.zcomapproach.garden.peony.kernel.services.PeonyWelcomeService;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.view.worker.AbstractPeonyArchiveWorker;
import com.zcomapproach.garden.persistence.entity.G02DailyReport;
import com.zcomapproach.garden.persistence.peony.PeonyEmployee;
import com.zcomapproach.garden.persistence.peony.PeonyJob;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author zhijun98
 */
@ServiceProvider(service = PeonyWelcomeService.class)
public class PeonyWelcomeServiceProvider extends PeonyServiceProvider implements PeonyWelcomeService{

    private final static ExecutorService peonyLocalService = Executors.newSingleThreadExecutor();

    @Override
    public void refreshTodayJobPaneForDailyReportUpdated(final G02DailyReport updatedReport) {
        if (SwingUtilities.isEventDispatchThread()){
            refreshTodayJobPaneForDailyReportUpdatedHelper(updatedReport);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    refreshTodayJobPaneForDailyReportUpdatedHelper(updatedReport);
                }
            });
        }
    }
    
    private void refreshTodayJobPaneForDailyReportUpdatedHelper(G02DailyReport updatedReport){
        try{
            TopComponent tc = WindowManager.getDefault().findTopComponent("PeonyTalkerTopComponent");
            if (tc instanceof PeonyTalkerTopComponent){
                ((PeonyTalkerTopComponent)tc).refreshTodayJobPaneForDailyReportUpdated(updatedReport);
            }
        }catch(Exception ex){
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void refreshTodayJobPaneForJobAssignmentUpdated(final PeonyJob updatedPeonyJob) {
        if (SwingUtilities.isEventDispatchThread()){
            refreshTodayJobPaneForJobAssignmentUpdatedHelper(updatedPeonyJob);
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    refreshTodayJobPaneForJobAssignmentUpdatedHelper(updatedPeonyJob);
                }
            });
        }
    }
    
    private void refreshTodayJobPaneForJobAssignmentUpdatedHelper(final PeonyJob updatedPeonyJob) {
        try{
            TopComponent tc = WindowManager.getDefault().findTopComponent("PeonyTalkerTopComponent");
            if (tc instanceof PeonyTalkerTopComponent){
                ((PeonyTalkerTopComponent)tc).refreshTodayJobPaneForJobAssignmentUpdated(updatedPeonyJob);
            }
        }catch(Exception ex){
            Exceptions.printStackTrace(ex);
        }
    }
    
    @Override
    public void launchPeonyTalkerWindow() {
        if (SwingUtilities.isEventDispatchThread()){
            launchPeonyTalkerWindowHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    launchPeonyTalkerWindowHelper();
                }
            });
        }
    }

    private void launchPeonyTalkerWindowHelper() {
        TopComponent tc = WindowManager.getDefault().findTopComponent("PeonyTalkerTopComponent");
        if (tc instanceof PeonyTalkerTopComponent){
            ((PeonyTalkerTopComponent)tc).launchPeonyTopComponent("Welcome to Peony Office 2018!");
            tc.open();
            tc.requestActive();
            
            /**
             * Launch technical controller for the local backup
             */
            launchPeonyLocalBackup();
        }
    }

    private AbstractPeonyArchiveWorker worker;
    private void launchPeonyLocalBackup() {
        if (PeonyEmployee.isTechnicalController(PeonyProperties.getSingleton().getCurrentLoginUserUuid())){
            worker = new PeonyLocalBackup();
        }else{
            worker = new PeonyArchiveSynchronizer();
        }
        peonyLocalService.submit(worker);
    }

    @Override
    public void closeService() {
        if (peonyLocalService != null){
            peonyLocalService.shutdown();
            worker.stopWorker();
        }
    }

}
