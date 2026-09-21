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

/**
 * Performs a canonical end-user journey for placing an order
 * and retrieving it from order history.
 */
public class OrderHistoryLookupTest extends BaseTest {

        private LoginPage loginPage;

        @BeforeMethod(dependsOnMethods = "setUpDriver")
        public void navigateToLoginPage() {
                loginPage = new LoginPage(driver).navigateTo();
        }

        @Test(dataProvider = "checkoutJourneys", dataProviderClass = DataProviders.class, description = "logs in, places an order, and retrieves it from order history")
        public void logsInPlacesOrderThenFindsItInOrderHistory(
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

                String placedOrderId = cartPage
                                .checkout()
                                .searchCountryAndSelect(country.countryCode(), country.countryName())
                                .verifyEmailIdMatches(testUserEmail)
                                .placeOrder()
                                .verifyOrderConfirmation()
                                .getOrderId();

                assertThat(placedOrderId).isNotBlank();

                String historyOrderId = dashboardPage
                                .header()
                                .navigateToOrders()
                                .selectOrder(placedOrderId)
                                .getOrderId();

                assertThat(historyOrderId).isEqualTo(placedOrderId);
        }
}