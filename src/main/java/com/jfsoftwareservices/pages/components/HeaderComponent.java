package com.jfsoftwareservices.pages.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class HeaderComponent {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By HOME_LINK = By.cssSelector("button[routerlink='/dashboard']");
    private static final By ORDERS_BUTTON = By.cssSelector("button[routerlink='/dashboard/myorders']");
    private static final By CART_BUTTON = By.cssSelector("button[routerlink='/dashboard/cart']");
    private static final By SIGN_OUT_BUTTON = By.xpath("//button[normalize-space()='Sign Out']");

    public HeaderComponent(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void goHome() {
        driver.findElement(HOME_LINK).click();

        wait.until(ExpectedConditions.urlMatches(".*#/dashboard/dash$"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[self::h1 or self::h2][normalize-space()='Filters']")
        ));
    }

    public void navigateToOrders() {
        driver.findElement(ORDERS_BUTTON).click();

        wait.until(ExpectedConditions.urlMatches(".*#/dashboard/myorders$"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[self::h1 or self::h2][normalize-space()='Your Orders']")
        ));
    }

    public void navigateToCart() {
        driver.findElement(CART_BUTTON).click();

        wait.until(ExpectedConditions.urlMatches(".*#/dashboard/cart$"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[self::h1 or self::h2][normalize-space()='My Cart']")
        ));
    }

    public void signOut() {
        driver.findElement(SIGN_OUT_BUTTON).click();
    }

    public void verifyLoggedIn() {
        assertThat(wait.until(ExpectedConditions.visibilityOfElementLocated(SIGN_OUT_BUTTON)).isDisplayed())
                .isTrue();
    }
}