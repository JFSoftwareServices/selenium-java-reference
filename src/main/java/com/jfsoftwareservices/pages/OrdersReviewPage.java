package com.jfsoftwareservices.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class OrdersReviewPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(css = "input[placeholder='Select Country']")
    private WebElement countryField;

    @FindBy(css = ".ta-results")
    private WebElement countryResultsDropdown;

    @FindBy(css = ".user__name label")
    private WebElement emailIdLabel;

    @FindBy(xpath = "//a[normalize-space()='Place Order']")
    private WebElement placeOrderLink;

    @FindBy(css = ".hero-primary")
    private WebElement orderConfirmationText;

    @FindBy(css = ".em-spacer-1 .ng-star-inserted")
    private WebElement orderIdLabel;

    public OrdersReviewPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        PageFactory.initElements(driver, this);
    }

    public void waitForPageToLoad() {
        wait.until(ExpectedConditions.urlContains("/#/dashboard/order"));
        wait.until(ExpectedConditions.elementToBeClickable(placeOrderLink));
    }

    public void searchCountry(String countryCode) {

        countryCode.chars().forEach(c ->
                countryField.sendKeys(String.valueOf((char) c))
        );

        wait.until(ExpectedConditions.visibilityOf(countryResultsDropdown));
    }

    public void selectCountry(String countryName) {

        WebElement option = countryResultsDropdown.findElement(
                By.xpath(".//*[normalize-space()='" + countryName + "']")
        );

        option.click();
    }

    public void searchCountryAndSelect(String countryCode, String countryName) {
        searchCountry(countryCode);
        selectCountry(countryName);
    }

    public void verifyEmailIdMatches(String expectedEmail) {
        assertThat(emailIdLabel.getText())
                .isEqualTo(expectedEmail);
    }

    public void placeOrder() {
        wait.until(ExpectedConditions.elementToBeClickable(placeOrderLink))
                .click();
    }

    public void verifyOrderConfirmation() {
        verifyOrderConfirmation("THANKYOU FOR THE ORDER.");
    }

    public void verifyOrderConfirmation(String expectedText) {

        WebElement confirmation = wait.until(
                ExpectedConditions.visibilityOf(orderConfirmationText)
        );

        assertThat(confirmation.getText())
                .isEqualTo(expectedText);
    }

    public String getOrderId() {
        String raw = orderIdLabel.getText();

        return raw == null
                ? null
                : raw.replace("|", "").trim();
    }
}
