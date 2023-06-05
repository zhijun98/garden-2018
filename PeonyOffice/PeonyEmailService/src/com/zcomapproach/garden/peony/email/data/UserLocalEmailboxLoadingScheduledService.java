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

package com.zcomapproach.garden.peony.email.data;

import com.zcomapproach.garden.persistence.peony.data.QueryNewOfflineEmailCriteria;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;

/**
 *
 * @author zhijun98
 */
public class UserLocalEmailboxLoadingScheduledService extends ScheduledService<Void>{

    private static volatile UserLocalEmailboxLoadingScheduledService self = null;
    public static UserLocalEmailboxLoadingScheduledService getSingleton(final UserLocalEmailBox targetUserLocalEmailBox, final QueryNewOfflineEmailCriteria queryNewOfflineEmailCriteria) {
        UserLocalEmailboxLoadingScheduledService selfLocal = UserLocalEmailboxLoadingScheduledService.self;
        if (selfLocal == null){
            synchronized (UserLocalEmailboxLoadingScheduledService.class) {
                selfLocal = UserLocalEmailboxLoadingScheduledService.self;
                if (selfLocal == null){
                    UserLocalEmailboxLoadingScheduledService.self = selfLocal = new UserLocalEmailboxLoadingScheduledService(targetUserLocalEmailBox, queryNewOfflineEmailCriteria);
                }
            }
        }
        return selfLocal;
    }

    private final UserLocalEmailBox targetUserLocalEmailBox;
    private final QueryNewOfflineEmailCriteria queryNewOfflineEmailCriteria;
    
    private UserLocalEmailboxLoadingScheduledService(UserLocalEmailBox targetUserLocalEmailBox, QueryNewOfflineEmailCriteria queryNewOfflineEmailCriteria) {
        this.targetUserLocalEmailBox = targetUserLocalEmailBox;
        this.queryNewOfflineEmailCriteria = queryNewOfflineEmailCriteria;
    }

    @Override
    protected Task<Void> createTask() {
        return new UserLocalEmailBoxLoadingTask(targetUserLocalEmailBox, queryNewOfflineEmailCriteria);
    }
}
