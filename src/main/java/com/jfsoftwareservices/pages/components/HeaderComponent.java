package com.jfsoftwareservices.pages.components;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

import com.jfsoftwareservices.pages.CartPage;
import com.jfsoftwareservices.pages.DashboardPage;
import com.jfsoftwareservices.pages.LoginPage;
import com.jfsoftwareservices.pages.OrdersPage;

public class HeaderComponent {
    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(css = "button[routerlink='/dashboard/']")
    private WebElement homebutton;

    @FindBy(css = "button[routerlink='/dashboard/myorders']")
    private WebElement ordersButton;

    @FindBy(css = "button[routerlink='/dashboard/cart']")
    private WebElement cartButton;

    @FindBy(css = "button[routerlink='/dashboard/cart'] label")
    private WebElement cartCount;

    @FindBy(xpath = "//button[normalize-space()='Sign Out']")
    private WebElement signOutButton;

    @FindBy(css = "#sidebar")
    private WebElement sidebar;

    @FindBy(xpath = "//*[self::h1 or self::h2][normalize-space()='Your Orders']")
    private WebElement yourOrdersHeading;

    @FindBy(xpath = "//*[self::h1 or self::h2][normalize-space()='My Cart']")
    private WebElement myCartHeading;

    public HeaderComponent(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        PageFactory.initElements(driver, this);
        this.driver = driver;
    }

    public DashboardPage navigateToDashBoard() {
        wait.until(elementToBeClickable(homebutton)).click();
        waitForNavigation(".*#/dashboard/dash$", sidebar);
        return new DashboardPage(driver);
    }

    public OrdersPage navigateToOrders() {
        wait.until(elementToBeClickable(ordersButton)).click();
        waitForNavigation(".*#/dashboard/myorders$", yourOrdersHeading);
        return new OrdersPage(driver);
    }

    public CartPage navigateToCart() {
        wait.until(elementToBeClickable(cartButton)).click();
        waitForNavigation(".*#/dashboard/cart$", myCartHeading);
        return new CartPage(driver);
    }

    // Returns LoginPage — sign out always lands there, so unlike loginAs()
    // there's no ambiguity about which page comes next.
    public LoginPage signOut() {
        wait.until(elementToBeClickable(signOutButton)).click();
        return new LoginPage(driver).waitUntilLoaded();
    }

    public void verifyLoggedIn() {
        assertThat(wait.until(ExpectedConditions.visibilityOf(signOutButton)).isDisplayed()).isTrue();
    }

    public void waitForCartCount(int expectedCount) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.textToBePresentInElement(cartCount, Integer.toString(expectedCount)));
    }

    // Confirms URL + one anchor element are ready after each nav click — not
    // a guarantee the whole destination page has rendered.
    private void waitForNavigation(String urlPattern, WebElement anchorElement) {
        wait.until(ExpectedConditions.urlMatches(urlPattern));
        wait.until(ExpectedConditions.visibilityOf(anchorElement));
    }

    public CartPage clickCart() {
        wait.until(ExpectedConditions.elementToBeClickable(cartButton)).click();
        return new CartPage(driver);
    }
}
