package com.jfsoftwareservices.ui;

import com.jfsoftwareservices.base.AuthenticatedTest;
import org.testng.annotations.Test;

/**
 * Only reads session state and does not mutate account data, so it can run
 * safely as part of the default parallel suite.
 */
public class SignOutSessionBoundaryTest extends AuthenticatedTest {

    @Test(groups = "parallel", description = "signing out returns to the login page and blocks direct dashboard access")
    public void signOutReturnsToLoginAndBlocksDirectDashboardAccess() {
        pages.headerComponent.verifyLoggedIn();

        pages.headerComponent.signOut();
        pages.loginPage.verifyLoggedOut();

        driver.get(baseUrl + "/client/dashboard/dash");
        pages.loginPage.verifyLoggedOut();
    }
}
