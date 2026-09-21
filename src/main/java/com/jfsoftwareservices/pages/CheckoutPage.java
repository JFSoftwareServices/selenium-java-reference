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

public class CheckoutPage {

    private final WebDriverWait wait;
    private static final String URL_FRAGMENT = "/#/dashboard/order";

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

    public CheckoutPage(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    // Confirms URL + one anchor element are ready — not a guarantee that
    // every element on the page has rendered. Action methods below wait on
    // their own targets individually.
    public CheckoutPage waitUntilLoaded() {
        wait.until(ExpectedConditions.urlContains(URL_FRAGMENT));
        wait.until(ExpectedConditions.visibilityOf(placeOrderLink));
        return this;
    }

    public CheckoutPage searchCountry(String countryCode) {

        countryCode.chars().forEach(c -> countryField.sendKeys(String.valueOf((char) c)));

        wait.until(ExpectedConditions.visibilityOf(countryResultsDropdown));
        return this;
    }

    public CheckoutPage selectCountry(String countryName) {

        WebElement option = countryResultsDropdown.findElement(
                By.xpath(".//*[normalize-space()='" + countryName + "']"));

        option.click();
        return this;
    }

    public CheckoutPage searchCountryAndSelect(String countryCode, String countryName) {
        searchCountry(countryCode);
        selectCountry(countryName);
        return this;
    }

    public CheckoutPage verifyEmailIdMatches(String expectedEmail) {
        assertThat(emailIdLabel.getText())
                .isEqualTo(expectedEmail);
        return this;
    }

    // Stays on this same page — the confirmation elements (orderConfirmationText,
    // orderIdLabel) render in place here rather than navigating to a new page,
    // so unlike checkout()/goToDashboard() there's no separate page object to
    // return.
    public CheckoutPage placeOrder() {
        wait.until(ExpectedConditions.elementToBeClickable(placeOrderLink))
                .click();
        wait.until(ExpectedConditions.visibilityOf(orderConfirmationText));
        return this;
    }

    public CheckoutPage verifyOrderConfirmation() {
        return verifyOrderConfirmation("THANKYOU FOR THE ORDER.");
    }

    public CheckoutPage verifyOrderConfirmation(String expectedText) {
        WebElement confirmation = wait.until(
                ExpectedConditions.visibilityOf(orderConfirmationText));
        assertThat(confirmation.getText())
                .isEqualTo(expectedText);
        return this;
    }

    public String getOrderId() {
        String raw = orderIdLabel.getText();
        return raw == null
                ? null
                : raw.replace("|", "").trim();
    }
}
