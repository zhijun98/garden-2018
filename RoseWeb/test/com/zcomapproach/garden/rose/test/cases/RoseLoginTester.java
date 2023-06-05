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

package com.zcomapproach.garden.rose.test.cases;

import com.zcomapproach.garden.rose.test.AbstractRoseWebTester;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Login: type in "login-name and password" and then click "login" button
 * @author zhijun98
 */
public class RoseLoginTester extends AbstractRoseWebTester {

    @Override
    public void launchTestingByWebDriver(WebDriver driver) throws Exception {
        testLoginFailed(driver);
        testEmployeeLoginSucceeded(driver);
    }
    
    public static void testEmployeeLoginSucceeded(WebDriver driver) throws Exception {
        /**
         * Login successful case for employee
         */
        driver.findElement(By.id("ezRosePageTopNavbar:loginForm:loginEmail")).clear();
        driver.findElement(By.id("ezRosePageTopNavbar:loginForm:loginEmail")).sendKeys("kitty.lu@yinlucpapc.com");
        driver.findElement(By.id("ezRosePageTopNavbar:loginForm:loginPassword")).clear();
        driver.findElement(By.id("ezRosePageTopNavbar:loginForm:loginPassword")).sendKeys("Yinlu1967%");
        driver.findElement(By.id("ezRosePageTopNavbar:loginForm:loginBtn")).click();
        Thread.sleep(1000);
        //test if the current user's profile name is diplayed after login
        if (!driver.findElement(By.id("ezRosePageTopNavbar:loginForm:employeeProfileName")).isDisplayed()){
            throw new Error("Expect employeeProfileName being displayed. But it is not presented.");
        }
    }
    
    public static void testLoginFailed(WebDriver driver) throws Exception {
        /**
         * Login failed case
         */
        driver.findElement(By.id("ezRosePageTopNavbar:loginForm:loginEmail")).clear();
        driver.findElement(By.id("ezRosePageTopNavbar:loginForm:loginEmail")).sendKeys("kitty.lu@yinlucpapc.com");
        driver.findElement(By.id("ezRosePageTopNavbar:loginForm:loginPassword")).clear();
        driver.findElement(By.id("ezRosePageTopNavbar:loginForm:loginPassword")).sendKeys("Yinlu1967%_ERROR");
        driver.findElement(By.id("ezRosePageTopNavbar:loginForm:loginBtn")).click();
        Thread.sleep(100);
        //test if it got error message displayed
        Assert.assertEquals("Login failed because login name and/or password are wrong.", 
                            driver.findElement(By.className("ui-growl-title")).getText());
    
    }
}
