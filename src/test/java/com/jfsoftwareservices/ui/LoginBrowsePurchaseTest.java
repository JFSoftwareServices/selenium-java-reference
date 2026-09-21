package com.jfsoftwareservices.ui;

import com.jfsoftwareservices.base.BaseTest;
import com.jfsoftwareservices.pages.CartPage;
import com.jfsoftwareservices.pages.DashboardPage;
import com.jfsoftwareservices.pages.LoginPage;
import com.jfsoftwareservices.testdata.CountrySearchData;
import com.jfsoftwareservices.testdata.DataProviders;
import com.jfsoftwareservices.testdata.OrderTestData;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginBrowsePurchaseTest extends BaseTest {
    LoginPage loginPage;

    @BeforeMethod(dependsOnMethods = "setUpDriver")
    public void navigateToLoginPage() {
        loginPage = new LoginPage(driver).navigateTo();
    }

    /**
     * Performs a canonical end-user journey.
     */
    @Test(
            dataProvider = "checkoutJourneys",
            dataProviderClass = DataProviders.class,
            description = "logs in, adds a product to cart, and completes checkout"
    )
    public void logsInAddsProductToCartAndCompletesCheckout(
            OrderTestData order,
            CountrySearchData country) {

        DashboardPage dashboardPage = loginPage
                .login(testUserEmail, testUserPassword)
                .goToDashboard();

        dashboardPage.addProductToCart(order.productName());
        dashboardPage.header().waitForCartCount(1);

        CartPage cartPage = dashboardPage
                .header()
                .clickCart();

        cartPage.verifyProductIsDisplayed(order.productName());

        String orderId = cartPage
                .checkout()
                .searchCountry(country.countryCode())
                .selectCountry(country.countryName())
                .verifyEmailIdMatches(testUserEmail)
                .placeOrder()
                .verifyOrderConfirmation()
                .getOrderId();

        assertThat(orderId).isNotBlank();
    }
}