package com.jfsoftwareservices.ui;

import com.jfsoftwareservices.base.AuthenticatedTest;
import com.jfsoftwareservices.pages.CartPage;
import com.jfsoftwareservices.pages.DashboardPage;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that an authenticated user can access the cart.
 */
public class CartTest extends AuthenticatedTest {

    @Test(description = "views the cart as an authenticated user")
    public void viewsCart() {
        DashboardPage dashboardPage = new DashboardPage(driver);
        CartPage cartPage = dashboardPage.header().navigateToCart();
        assertThat(cartPage.isDisplayed()).isTrue();
    }
}