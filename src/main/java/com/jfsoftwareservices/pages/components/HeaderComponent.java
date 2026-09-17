package com.jfsoftwareservices.pages.components;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class HeaderComponent {

    private final WebDriverWait wait;

    @FindBy(css = "button[routerlink='/dashboard/']")
    private WebElement homeLink;

    @FindBy(css = "button[routerlink='/dashboard/myorders']")
    private WebElement ordersButton;

    @FindBy(css = "button[routerlink='/dashboard/cart']")
    private WebElement cartButton;

    @FindBy(xpath = "//button[normalize-space()='Sign Out']")
    private WebElement signOutButton;

    @FindBy(css = "#sidebar")
    private WebElement sidebar;

    @FindBy(xpath = "//*[self::h1 or self::h2][normalize-space()='Your Orders']")
    private WebElement yourOrdersHeading;

    @FindBy(xpath = "//*[self::h1 or self::h2][normalize-space()='My Cart']")
    private WebElement myCartHeading;

    public HeaderComponent(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        PageFactory.initElements(driver, this);
    }

    public void goHome() {
        homeLink.click();

        wait.until(ExpectedConditions.urlMatches(".*#/dashboard/dash$"));
        wait.until(ExpectedConditions.visibilityOf(sidebar));
    }

    public void navigateToOrders() {
        ordersButton.click();

        wait.until(ExpectedConditions.urlMatches(".*#/dashboard/myorders$"));
        wait.until(ExpectedConditions.visibilityOf(yourOrdersHeading));
    }

    public void navigateToCart() {
        cartButton.click();

        wait.until(ExpectedConditions.urlMatches(".*#/dashboard/cart$"));
        wait.until(ExpectedConditions.visibilityOf(myCartHeading));
    }

    public void signOut() {
        signOutButton.click();
    }

    public void verifyLoggedIn() {
        assertThat(
                wait.until(ExpectedConditions.visibilityOf(signOutButton)).isDisplayed()).isTrue();
    }
}
