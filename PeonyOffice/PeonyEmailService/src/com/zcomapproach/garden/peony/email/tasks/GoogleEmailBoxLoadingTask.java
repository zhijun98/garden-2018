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

package com.zcomapproach.garden.peony.email.tasks;

import com.zcomapproach.garden.peony.email.data.GoogleEmailBox;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import javax.mail.MessagingException;
import org.openide.util.Exceptions;

/**
 * Load emails from the mail box on the server-side
 * @author zhijun98
 */
public class GoogleEmailBoxLoadingTask implements Runnable{
    
    /**
     * Owner on which this task working
     */
    private final GoogleEmailBox googleEmailBox;

    public GoogleEmailBoxLoadingTask(GoogleEmailBox googleEmailBox) {
        this.googleEmailBox = googleEmailBox;
    }

    @Override
    public void run() {
        String emailSystemRootPath = PeonyProperties.getSingleton().getEmailSerializationFolder();
        while (!googleEmailBox.isClosingGardenEmailBox()){
            try {
                googleEmailBox.loadGardenEmails(emailSystemRootPath);
                Thread.sleep(1000);
                googleEmailBox.processFlaggedGardenEmails(emailSystemRootPath);
                Thread.sleep(1000*15);
            } catch (MessagingException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InterruptedException ex) {
                googleEmailBox.setClosingGardenEmailBox(true);
                break;
            }
        }//while-loop
    }
    
}
