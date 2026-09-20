package com.jfsoftwareservices.ui;

import com.jfsoftwareservices.base.AuthenticatedTest;
import com.jfsoftwareservices.testdata.CountrySearchData;
import com.jfsoftwareservices.testdata.DataProviders;
import com.jfsoftwareservices.testdata.OrderTestData;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Places a fresh order, then confirms it is retrievable from order history -
 * account-mutating.
 */
public class OrderHistoryLookupTest extends AuthenticatedTest {

    @Test(dataProvider = "checkoutJourneys", dataProviderClass = DataProviders.class,
            description = "places an order then finds it in order history")
    public void placesOrderThenFindsItInOrderHistory(OrderTestData order, CountrySearchData country) {
        pages.headerComponent.goHome();
        pages.dashboardPage.addProductToCart(order.productName());

        pages.headerComponent.navigateToCart();
        pages.cartPage.waitForPageToLoad();
        pages.cartPage.checkout();
        pages.ordersReviewPage.waitForPageToLoad();
        pages.ordersReviewPage.searchCountryAndSelect(country.countryCode(), country.countryName());
        pages.ordersReviewPage.placeOrder();
        pages.ordersReviewPage.verifyOrderConfirmation();

        String placedOrderId = pages.ordersReviewPage.getOrderId();
        assertThat(placedOrderId).isNotBlank();

        pages.headerComponent.navigateToOrders();
        pages.ordersHistoryPage.waitForPageToLoad();
        pages.ordersHistoryPage.selectOrder(placedOrderId);

        assertThat(pages.ordersHistoryPage.getOrderId()).isEqualTo(placedOrderId);
    }
}