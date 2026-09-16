package com.jfsoftwareservices.pages;

import com.jfsoftwareservices.pages.components.HeaderComponent;
import org.openqa.selenium.WebDriver;

/**
 * Central access point for all page objects and components.
 * All objects share the same WebDriver instance provided by the test.
 */
public class Pages {

    public final LoginPage loginPage;
    public final DashboardPage dashboardPage;
    public final OrdersHistoryPage ordersHistoryPage;
    public final OrdersReviewPage ordersReviewPage;
    public final CartPage cartPage;
    public final HeaderComponent headerComponent;

    public Pages(WebDriver driver) {
        this.loginPage = new LoginPage(driver);
        this.dashboardPage = new DashboardPage(driver);
        this.ordersHistoryPage = new OrdersHistoryPage(driver);
        this.ordersReviewPage = new OrdersReviewPage(driver);
        this.cartPage = new CartPage(driver);
        this.headerComponent = new HeaderComponent(driver);
    }
}