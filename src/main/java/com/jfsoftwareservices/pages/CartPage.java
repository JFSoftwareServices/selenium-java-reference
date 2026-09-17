package com.jfsoftwareservices.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CartPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(xpath = "//div[contains(@class,'heading')]//h1[normalize-space()='My Cart']")
    private WebElement cartHeading;

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

    public void waitForPageToLoad() {
        wait.until(ExpectedConditions.visibilityOf(cartHeading));
        wait.until(ExpectedConditions.elementToBeClickable(checkoutButton));
    }

    public List<WebElement> productRows() {
        return productRows;
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
        boolean present = getProductNames().stream()
                .anyMatch(name -> name.contains(productName));

        assertThat(present)
                .as("'%s' displayed in cart", productName)
                .isTrue();
    }

    public double getTotalValue() {
        WebElement total = wait.until(
                ExpectedConditions.visibilityOf(totalValue));

        String raw = total.getText()
                .replace("$", "")
                .trim();

        return raw.isBlank() ? 0 : Double.parseDouble(raw);
    }

    public void checkout() {
        wait.until(ExpectedConditions.elementToBeClickable(checkoutButton))
                .click();
    }

    /**
     * Removes a product from the cart.
     * Expects exactly one matching row to be present.
     */
    public void removeFromCart(String productName) {

        List<WebElement> matching = productRows().stream()
                .filter(row -> row.getText().contains(productName))
                .toList();

        assertThat(matching)
                .as("cart row matching '%s'", productName)
                .hasSize(1);

        WebElement row = matching.get(0);

        row.findElement(
                org.openqa.selenium.By.cssSelector("button.btn.btn-danger")).click();

        wait.until(driver1 -> productRows().stream()
                .noneMatch(row1 -> row1.getText().contains(productName)));
    }

    public void verifyCartEmpty() {
        wait.until(ExpectedConditions.visibilityOf(emptyCartMessage));
    }
}
