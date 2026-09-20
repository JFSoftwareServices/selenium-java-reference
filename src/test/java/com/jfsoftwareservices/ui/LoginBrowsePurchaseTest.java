package com.jfsoftwareservices.ui;

import com.jfsoftwareservices.base.BaseTest;
import com.jfsoftwareservices.testdata.CountrySearchData;
import com.jfsoftwareservices.testdata.DataProviders;
import com.jfsoftwareservices.testdata.OrderTestData;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Performs a real UI login (starts logged out, like {@link LoginTest}) and
 * places a real order, so it belongs to the "serial" group - see {@code testng-serial.xml}.
 */
public class LoginBrowsePurchaseTest extends BaseTest {

    @BeforeMethod(dependsOnMethods = "setUpDriver")
    public void goToLogin() {
        pages.loginPage.goTo();
    }

    @Test(dataProvider = "checkoutJourneys", dataProviderClass = DataProviders.class,
            description = "logs in, adds a product to cart, and completes checkout")
    public void logsInAddsToCartAndCompletesCheckout(OrderTestData order, CountrySearchData country) {
        pages.loginPage.login(testUserEmail, testUserPassword);
        pages.headerComponent.verifyLoggedIn();

        pages.dashboardPage.addProductToCart(order.productName());
        pages.headerComponent.navigateToCart();
        pages.cartPage.waitForPageToLoad();
        pages.cartPage.verifyProductIsDisplayed(order.productName());

        pages.cartPage.checkout();

        pages.ordersReviewPage.waitForPageToLoad();
        pages.ordersReviewPage.searchCountry(country.countryCode());
        pages.ordersReviewPage.selectCountry(country.countryName());
        pages.ordersReviewPage.verifyEmailIdMatches(testUserEmail);
        pages.ordersReviewPage.placeOrder();
        pages.ordersReviewPage.verifyOrderConfirmation();

        String orderId = pages.ordersReviewPage.getOrderId();
        assertThat(orderId).isNotBlank();
    }
}