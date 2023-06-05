package com.zcomapproach.garden.rose.test;

import com.zcomapproach.garden.rose.test.cases.RoseLoginTester;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 *
 * @author Administrator
 */
public class RoseWebTester extends AbstractRoseWebTester{
    
    @Override
    protected List<IRoseWebTester> getRoseTesters(){
        List<IRoseWebTester> testers = new ArrayList<>();
        testers.add(new RoseLoginTester());
        return testers;
    }

    @Override
    protected List<WebDriver> getWebDrivers() {
        List<WebDriver> drivers = new ArrayList<>();
        drivers.add(new ChromeDriver());
        drivers.add(new FirefoxDriver());
        drivers.add(new EdgeDriver());
        //drivers.add(new InternetExplorerDriver());
        return drivers;
    }

    @Override
    public void launchTestingByWebDriver(WebDriver driver) throws Exception {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
