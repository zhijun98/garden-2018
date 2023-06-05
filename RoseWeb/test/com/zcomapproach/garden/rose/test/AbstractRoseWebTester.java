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

package com.zcomapproach.garden.rose.test;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 *
 * @author zhijun98
 */
public abstract class AbstractRoseWebTester extends AbstractRoseTester implements IRoseWebTester {

    @Override
    protected void launchTestingImpl() throws Exception {
        List<IRoseWebTester> testers = getRoseTesters();
        List<WebDriver> webDrivers = getWebDrivers();
        for (WebDriver webDriver : webDrivers){
            testByWebDriver(webDriver, testers);
        }
    }
    
    private void testByWebDriver(WebDriver driver, List<IRoseWebTester> testers) throws Exception{
        driver.get(RoseTestSettings.ROSE_WEB_HOMEPAGE.value());
        
        for (IRoseWebTester tester : testers){
            tester.launchTestingByWebDriver(driver);
        }
        
        driver.quit();
    }
    
    protected List<IRoseWebTester> getRoseTesters(){
        List<IRoseWebTester> testers = new ArrayList<>();
        testers.add(this);
        return testers;
    }

    
    protected List<WebDriver> getWebDrivers() {
        List<WebDriver> drivers = new ArrayList<>();
        drivers.add(new ChromeDriver());
        //drivers.add(new FirefoxDriver());
        //drivers.add(new EdgeDriver());
        //drivers.add(new InternetExplorerDriver());
        return drivers;
    }

}
