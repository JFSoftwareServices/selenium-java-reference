package com.jfsoftwareservices.ui;

import com.jfsoftwareservices.base.BaseTest;
import com.jfsoftwareservices.testdata.DataProviders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Extends {@link BaseTest} directly (not {@code AuthenticatedTest}) because
 * these tests specifically exercise the UI login flow and must start logged
 * out.
 */
public class LoginTest extends BaseTest {

    @BeforeMethod(dependsOnMethods = "setUpDriver")
    public void navigateToLoginPage() {
        pages.loginPage.goTo();
        pages.loginPage.waitForLoginForm();
    }

    @Test(description = "logs in successfully with valid credentials")
    public void logsInSuccessfullyWithValidCredentials() {
        pages.loginPage.login(testUserEmail, testUserPassword);

        pages.headerComponent.verifyLoggedIn();
    }

    @Test(dataProvider = "invalidCredentials", dataProviderClass = DataProviders.class, description = "shows an error with invalid credentials")
    public void showsErrorWithInvalidCredentials(String username, String password) {
        pages.loginPage.login(username, password);

        assertThat(pages.loginPage.getErrorMessage()).isEqualTo("Incorrect email or password.");
    }
} 