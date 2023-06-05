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

package com.zcomapproach.garden.peony;

import com.zcomapproach.garden.data.constant.GardenSupportedLanguage;
import com.zcomapproach.garden.peony.kernel.services.PeonySecurityService;
import com.zcomapproach.garden.peony.settings.PeonyProperties;
import com.zcomapproach.garden.peony.settings.PeonyPropertiesKey;
import com.zcomapproach.garden.peony.utils.PeonyFaceUtils;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Locale;
import javafx.application.Platform;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.openide.modules.OnStart;
import org.openide.util.Lookup;
import org.openide.windows.WindowManager;

/**
 * Peony office launcher:
 * <br>
 * Go to the following location before build the installer:<br>
 * C:\Program Files\NetBeans 8.2\harness\etc<br>
 * And then open app.conf file to modify the following line with favorite values:<br>
 * default_options="--branding ${branding.token} -J-Xms64m -J-Xmx2048m"
 * <br>
 * @author zhijun98
 */
@OnStart
public class PeonyLauncher implements Runnable{
    
    public static  Frame mainFrame;
    
    @Override
    public void run() {
        
        Platform.setImplicitExit(false);
        
        initializeMainFrame();
        
        initializePeonyByPersistentProperties();
        
        disableActionBarsForLogoutMode();
        
        Lookup.getDefault().lookup(PeonySecurityService.class).displayLoginDialog();
        
    }

    private void initializeMainFrame() {
        if (SwingUtilities.isEventDispatchThread()){
            initializeMainFrameHelper();
        }else{
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run() {
                    initializeMainFrameHelper();
                }
            });
        }
    }

    private void initializeMainFrameHelper() {
        mainFrame = WindowManager.getDefault().getMainWindow();
        mainFrame.addWindowListener(new WindowListener(){
            @Override
            public void windowOpened(WindowEvent e) {
                if (PeonyProperties.getSingleton().isDevelopmentMode()){
                    mainFrame.setTitle("Peony Office [Debug Mode]");
                }else{
                    mainFrame.setTitle("Peony Office 2018");
                }
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });
    }
    
    /**
     * initialize Peony-office's basic settings, e.g. Locale, by means of persistent properties
     */
    public static void initializePeonyByPersistentProperties(){
        
        /**
         * This TimeZone setup is critical because it synchronized the local JVM 
         * TimeZone with the server's TimeZone 
         */
        //ZcaCalendar.initializeUtcConverter();
        //java.util.TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        
        initializeLocaleByPersistentProperties();
    }

    private static void initializeLocaleByPersistentProperties() {
        GardenSupportedLanguage lang = GardenSupportedLanguage.convertEnumValueToType(PeonyProperties.getSingleton().getProperty(PeonyPropertiesKey.LANGUAGE.value()));
        if (lang == null){
            lang = GardenSupportedLanguage.UNKNOWN;
        }
        switch(lang){
            case CHINESE:
                setupLocale("zh", "CN");
                break;
            case ENGLISH:
                setupLocale("en", "US");
                break;
            default:
                setupLocale("zh", "CN");
        }
    }
    
    private static void setupLocale(String language, String region){
        Locale.setDefault(new Locale(language, region));
        System.getProperty("user.language", language);
        System.getProperty("user.region", region);
    }
    
    public static void disableActionBarsForLogoutMode() {
        enableActionBarsHelper(false);
    }

    public static void enableActionBarsForLoginMode() {
        enableActionBarsHelper(true);
    }

    private static void enableActionBarsHelper(final boolean value) {
        WindowManager.getDefault().invokeWhenUIReady(new Runnable(){
            @Override
            public void run() {
                mainFrame = WindowManager.getDefault().getMainWindow();
                if (mainFrame instanceof JFrame){
                    PeonyFaceUtils.setMenuBarEnabled(((JFrame)mainFrame).getJMenuBar(), value);
                }
                PeonyFaceUtils.setToolbarPoolVisible(value);
            }
        });
    }

//    public static void startPeonyUpdateExcutorService(PeonyEmployee loginAccount) {
//        Lookup.getDefault().lookup(PeonyWelcomeService.class).startPeonyUpdateExcutorService(loginAccount);
//    }
//
//    public static void stopPeonyUpdateExcutorService() {
//        Lookup.getDefault().lookup(PeonyWelcomeService.class).stopPeonyUpdateExcutorService();
//    }
    
}
