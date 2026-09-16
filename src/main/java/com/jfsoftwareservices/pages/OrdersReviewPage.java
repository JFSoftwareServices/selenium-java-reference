package com.jfsoftwareservices.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class OrdersReviewPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By COUNTRY_FIELD = By.cssSelector("input[placeholder='Select Country']");
    private static final By COUNTRY_RESULTS_DROPDOWN = By.cssSelector(".ta-results");
    private static final By EMAIL_ID_LABEL = By.cssSelector(".user__name label");
    private static final By SUBMIT_LINK = By.xpath("//a[contains(., 'PLACE ORDER')]");
    private static final By ORDER_CONFIRMATION_TEXT = By.cssSelector(".hero-primary");
    private static final By ORDER_ID_LABEL = By.cssSelector(".em-spacer-1 .ng-star-inserted");

    public OrdersReviewPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void searchCountry(String countryCode) {
        WebElement field = driver.findElement(COUNTRY_FIELD);
        countryCode.chars().forEach(c ->
                field.sendKeys(String.valueOf((char) c))
        );
        wait.until(ExpectedConditions.visibilityOfElementLocated(COUNTRY_RESULTS_DROPDOWN));
    }

    public void selectCountry(String countryName) {
        WebElement option = driver.findElement(COUNTRY_RESULTS_DROPDOWN)
                .findElement(By.xpath(".//*[normalize-space()='" + countryName + "']"));
        option.click();
    }

    public void searchCountryAndSelect(String countryCode, String countryName) {
        searchCountry(countryCode);
        selectCountry(countryName);
    }

    public void verifyEmailIdMatches(String expectedEmail) {
        assertThat(driver.findElement(EMAIL_ID_LABEL).getText()).isEqualTo(expectedEmail);
    }

    public void submit() {
        driver.findElement(SUBMIT_LINK).click();
    }

    public void verifyOrderConfirmation() {
        verifyOrderConfirmation("Thankyou for the order.");
    }

    public void verifyOrderConfirmation(String expectedText) {
        WebElement confirmation = wait.until(ExpectedConditions.visibilityOfElementLocated(ORDER_CONFIRMATION_TEXT));
        assertThat(confirmation.getText()).isEqualTo(expectedText);
    }

    public String getOrderId() {
        String raw = driver.findElement(ORDER_ID_LABEL).getText();
        return raw == null ? null : raw.replace("|", "").trim();
    }
}