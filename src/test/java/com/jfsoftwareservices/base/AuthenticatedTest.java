package com.jfsoftwareservices.base;

import com.jfsoftwareservices.api.AuthApi;
import com.jfsoftwareservices.pages.DashboardPage;
import com.jfsoftwareservices.pages.LoginPage;

import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeMethod;

/**
 * Base class for journeys that don't specifically test the login flow.
 * A test extending this class starts already logged in — authentication
 * is done via a direct API call (bypassing the UI login form) and the
 * resulting session token is injected into local storage before the
 * test body runs.
 */
public abstract class AuthenticatedTest extends BaseTest {

    // Authenticates via a direct API call instead of the UI login form,
    // since the UI form is slow to render in this environment and this
    // class exists specifically to skip it. Lands on the login URL without
    // waiting for the form (navigateWithoutWaiting()), injects the session
    // token into local storage, then confirms authentication succeeded by
    // navigating to the Dashboard — a page only reachable when logged in.
    @BeforeMethod(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void authenticate() {
        AuthApi.Session session = new AuthApi().login(baseUrl, testUserEmail, testUserPassword);

        new LoginPage(driver).navigateWithoutWaiting();

        ((JavascriptExecutor) driver).executeScript(
                "window.localStorage.setItem('token', arguments[0]);"
                        + "window.localStorage.setItem('userId', arguments[1]);",
                session.token(), session.userId());

        new DashboardPage(driver).navigateTo();
    }
}