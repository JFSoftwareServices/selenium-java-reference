package com.jfsoftwareservices.ui;

import com.jfsoftwareservices.base.BaseTest;
import com.jfsoftwareservices.pages.LoginPage;
import com.jfsoftwareservices.testdata.DataProviders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Performs canonical negative UI login journeys.
 */
public class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeMethod(dependsOnMethods = "setUpDriver")
    public void navigateToLoginPage() {
        loginPage = new LoginPage(driver).navigateTo();
    }

    @Test(dataProvider = "invalidCredentials", dataProviderClass = DataProviders.class, description = "rejects invalid credentials and displays a login error")
    public void rejectsInvalidCredentials(String username, String password) {
        loginPage.login(username, password);

        assertThat(loginPage.getErrorMessage())
                .isEqualTo("Incorrect email or password.");
    }
}