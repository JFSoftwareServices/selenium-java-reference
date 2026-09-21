package com.jfsoftwareservices.base;

import com.jfsoftwareservices.config.ConfigReader;
import com.jfsoftwareservices.driver.DriverFactory;
import com.jfsoftwareservices.listeners.ScreenshotListener;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

/**
 * Shared driver/page lifecycle for every UI test class.
 * <p>
 * By default a fresh, unauthenticated browser session is created for each
 * test method (safe for TestNG's thread-count-driven parallel execution -
 * see {@code testng-parallel.xml}). {@link AuthenticatedTest} extends this
 * to additionally seed an authenticated session. Test classes needing a
 * logged-out starting state (e.g. {@code LoginTest}) can rely on this
 * guarantee directly, without any explicit session-clearing of their own.
 */
@Listeners(ScreenshotListener.class)
public abstract class BaseTest {

    protected WebDriver driver;
    protected String baseUrl;
    protected String testUserEmail;
    protected String testUserPassword;

    @BeforeMethod(alwaysRun = true)
    public void setUpDriver() {
        baseUrl = ConfigReader.baseUrl();
        testUserEmail = ConfigReader.testUserEmail();
        testUserPassword = ConfigReader.testUserPassword();

        driver = DriverFactory.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownDriver() {
        DriverFactory.quitDriver();
    }
}