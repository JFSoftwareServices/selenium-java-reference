package com.jfsoftwareservices.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CartPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By TOTAL_VALUE = By.xpath(
            "//li[contains(@class,'totalRow')][.//*[normalize-space()='Total']]//*[contains(@class,'value')]");
    private static final By PRODUCT_ROWS = By.cssSelector("ul.cartWrap > li");
    private static final By EMPTY_CART_MESSAGE = By
            .xpath("//h1[contains(.,'No Products in Your Cart')] | //h2[contains(.,'No Products in Your Cart')]");
    private static final By CHECKOUT_BUTTON = By.xpath("//button[normalize-space()='Checkout']");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public List<WebElement> productRows() {
        return driver.findElements(PRODUCT_ROWS);
    }

    public int getProductCount() {
        return productRows().size();
    }

    public List<String> getProductNames() {
        return productRows().stream()
                .map(WebElement::getText)
                .toList();
    }

    public void verifyProductIsDisplayed(String productName) {
        boolean present = getProductNames().stream().anyMatch(name -> name.contains(productName));
        assertThat(present).as("'%s' displayed in cart", productName).isTrue();
    }

    public double getTotalValue() {
        WebElement total = wait.until(ExpectedConditions.visibilityOfElementLocated(TOTAL_VALUE));
        String raw = total.getText().replace("$", "").trim();
        return raw.isBlank() ? 0 : Double.parseDouble(raw);
    }

    public void checkout() {
        driver.findElement(CHECKOUT_BUTTON).click();
    }

    /**
     * Removes a product from the cart. Expects exactly one matching row to be
     * present.
     */
    public void removeFromCart(String productName) {
        List<WebElement> matching = productRows().stream()
                .filter(row -> row.getText().contains(productName))
                .toList();

        assertThat(matching).as("cart row matching '%s'", productName).hasSize(1);

        WebElement row = matching.get(0);
        row.findElement(By.cssSelector("button.btn.btn-danger")).click();

        wait.until(driver1 -> productRows().stream().noneMatch(r -> r.getText().contains(productName)));
    }

    public void verifyCartEmpty() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(EMPTY_CART_MESSAGE));
    }
}