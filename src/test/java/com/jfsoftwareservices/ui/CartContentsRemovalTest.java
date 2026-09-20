package com.jfsoftwareservices.ui;

import com.jfsoftwareservices.base.AuthenticatedTest;
import com.jfsoftwareservices.testdata.OrderTestData;
import org.testng.annotations.Test;

/**
 * Starts authenticated (see {@link AuthenticatedTest}) and mutates the
 * account's cart.
 */
public class CartContentsRemovalTest extends AuthenticatedTest {

    @Test(description = "adds a product then removes it, leaving the cart empty")
    public void addsThenRemovesProductLeavingCartEmpty() {
        String productName = OrderTestData.DEFAULT.productName();

        pages.headerComponent.goHome();
        pages.dashboardPage.addProductToCart(productName);

        pages.headerComponent.navigateToCart();
        pages.cartPage.waitForPageToLoad();
        pages.cartPage.verifyProductIsDisplayed(productName);

        pages.cartPage.removeFromCart(productName);
        pages.cartPage.verifyCartEmpty();
    }
}
