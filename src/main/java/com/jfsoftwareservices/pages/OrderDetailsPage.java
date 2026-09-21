package com.jfsoftwareservices.pages;

import com.jfsoftwareservices.config.ConfigReader;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import java.time.Duration;

public class OrderDetailsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final String URL_FRAGMENT = "#/dashboard/order-details";

    @FindBy(css = ".email-wrapper")
    private WebElement orderConfirmation;

    @FindBy(css = ".col-text")
    private WebElement orderIdDetail;

    public OrderDetailsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public OrderDetailsPage navigateTo() {
        driver.get(ConfigReader.baseUrl() + URL_FRAGMENT);
        return waitUntilLoaded();
    }

    // Confirms URL + one anchor element are ready — not a guarantee that
    // every element on the page has rendered. Action methods below wait on
    // their own targets individually.
    public OrderDetailsPage waitUntilLoaded() {
        wait.until(ExpectedConditions.urlContains(URL_FRAGMENT));
        wait.until(ExpectedConditions.visibilityOf(orderConfirmation));
        return this;
    }

    public String getOrderId() {
        String raw = wait.until(visibilityOf(orderIdDetail)).getText();
        return raw == null ? null : raw.replace("|", "").trim();
    }
}
