package com.jfsoftwareservices.base;

import com.jfsoftwareservices.api.AuthApi;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeMethod;

/**
 * Base class for journeys that don't specifically test the login flow.
 * A test extending this class starts already logged in.
 */
public abstract class AuthenticatedTest extends BaseTest {

    @BeforeMethod(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void authenticate() {
        AuthApi.Session session = new AuthApi().login(baseUrl, testUserEmail, testUserPassword);

        driver.get(baseUrl + "/client");

        ((JavascriptExecutor) driver).executeScript(
                "window.localStorage.setItem('token', arguments[0]);"
                        + "window.localStorage.setItem('userId', arguments[1]);",
                session.token(), session.userId());

        pages.loginPage.goTo();
    }
}
