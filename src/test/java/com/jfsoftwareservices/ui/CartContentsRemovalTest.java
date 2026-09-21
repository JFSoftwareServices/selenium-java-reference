package com.jfsoftwareservices.ui;

import com.jfsoftwareservices.base.BaseTest;
import com.jfsoftwareservices.pages.CartPage;
import com.jfsoftwareservices.pages.DashboardPage;
import com.jfsoftwareservices.pages.LoginPage;
import com.jfsoftwareservices.testdata.OrderTestData;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Performs a canonical end-user journey for removing a product from the cart.
 */
public class CartContentsRemovalTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeMethod(dependsOnMethods = "setUpDriver")
    public void navigateToLoginPage() {
        loginPage = new LoginPage(driver).navigateTo();
    }

    @Test(description = "logs in, adds a product to cart, removes it, and verifies the cart is empty")
    public void logsInAddsThenRemovesProductLeavingCartEmpty() {

        String productName = OrderTestData.DEFAULT.productName();

        DashboardPage dashboardPage = loginPage
                .login(testUserEmail, testUserPassword)
                .goToDashboard();

        dashboardPage.addProductToCart(productName);
        dashboardPage.header().waitForCartCount(1);

        CartPage cartPage = dashboardPage
                .header()
                .clickCart();

        cartPage.verifyProductIsDisplayed(productName);

        cartPage
                .removeFromCart(productName)
                .verifyCartEmpty();
    }
}
