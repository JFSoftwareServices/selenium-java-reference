package com.jfsoftwareservices.pages;

import com.jfsoftwareservices.config.ConfigReader;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

public class CartPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final String URL_FRAGMENT = "/#/dashboard/cart";

    @FindBy(xpath = "//div[contains(@class,'heading')]//h1[normalize-space()='My Cart']")
    private WebElement cartHeading;

    @FindBy(css = ".cartWrap")
    private WebElement cart;

    @FindBy(xpath = "//li[contains(@class,'totalRow')][.//*[normalize-space()='Total']]//*[contains(@class,'value')]")
    private WebElement totalValue;

    @FindBy(css = "ul.cartWrap > li")
    private List<WebElement> productRows;

    @FindBy(xpath = "//h1[contains(.,'No Products in Your Cart')] | //h2[contains(.,'No Products in Your Cart')]")
    private WebElement emptyCartMessage;

    @FindBy(xpath = "//button[normalize-space()='Checkout']")
    private WebElement checkoutButton;

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public CartPage navigateTo() {
        driver.get(ConfigReader.baseUrl() + URL_FRAGMENT);
        return waitUntilLoaded();
    }

    // Confirms the expected URL and cart are ready.
    // This does not guarantee that every element on the page has rendered.
    // Action methods wait for their own target elements individually.
    public CartPage waitUntilLoaded() {
        wait.until(urlContains(URL_FRAGMENT));
        wait.until(visibilityOf(cart));
        return this;
    }

    public List<WebElement> productRows() {
        return productRows;
    }

    public int getProductCount() {
        return productRows().size();
    }

    public void verifyProductIsDisplayed(String productName) {
        WebElement product = wait.until(
                visibilityOfElementLocated(
                        By.xpath("//h3[normalize-space()='" + productName + "']")));
        assertThat(product.isDisplayed()).isTrue();
    }

    public double getTotalValue() {
        WebElement total = wait.until(
                visibilityOf(totalValue));

        String raw = total.getText()
                .replace("$", "")
                .trim();

        return raw.isBlank() ? 0 : Double.parseDouble(raw);
    }

    public CheckoutPage checkout() {
        wait.until(elementToBeClickable(checkoutButton)).click();
        return new CheckoutPage(driver).waitUntilLoaded();
    }

    /**
     * Removes a product from the cart.
     * Expects exactly one matching row to be present.
     */
    public CartPage removeFromCart(String productName) {
        List<WebElement> matching = productRows().stream()
                .filter(row -> row.getText().contains(productName))
                .toList();

        assertThat(matching)
                .as("cart row matching '%s'", productName)
                .hasSize(1);

        WebElement row = matching.get(0);

        row.findElement(By.cssSelector("button.btn.btn-danger")).click();

        wait.until(driver1 -> productRows().stream()
                .noneMatch(row1 -> row1.getText().contains(productName)));

        return this;
    }

    public void verifyCartEmpty() {
        wait.until(visibilityOf(emptyCartMessage));
    }

    public boolean isDisplayed() {
        wait.until(visibilityOf(cartHeading));
        return cartHeading.isDisplayed();
    }
}