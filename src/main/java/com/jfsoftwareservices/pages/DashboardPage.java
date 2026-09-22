package com.jfsoftwareservices.pages;

import com.jfsoftwareservices.config.ConfigReader;

import com.jfsoftwareservices.pages.components.HeaderComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

public class DashboardPage {
    private final HeaderComponent header;
    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final String URL_FRAGMENT = "/client/#/dashboard/dash";
    private static final By PRODUCT_CARD = By.cssSelector(".card-body");

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        this.header = new HeaderComponent(driver);
    }

    public HeaderComponent header() {
        return header;
    }

    public DashboardPage navigateTo() {
        driver.get(ConfigReader.baseUrl() + URL_FRAGMENT);
        return waitUntilLoaded();
    }

    // Confirms URL + one anchor element are ready — not a guarantee that
    // every element on the page has rendered. Action methods below wait on
    // their own targets individually.
    public DashboardPage waitUntilLoaded() {
        wait.until(ExpectedConditions.urlContains(URL_FRAGMENT));
        wait.until(ExpectedConditions.visibilityOfElementLocated(PRODUCT_CARD));
        return this;
    }

    private List<WebElement> productCards() {
        return driver.findElements(PRODUCT_CARD);
    }

    public WebElement findProduct(String productName) {
        List<WebElement> matching = productCards().stream()
                .filter(card -> card.getText().contains(productName))
                .toList();

        assertThat(matching)
                .as("product card matching '%s'", productName)
                .hasSize(1);

        return matching.getFirst();
    }

    // Waits for the loading spinner to clear rather than the "Product Added"
    // toast, which fades too quickly to reliably catch. Actual success is
    // verified downstream via CartPage.verifyProductIsDisplayed().
    public void addProductToCart(String productName) {
        WebElement product = findProduct(productName);
        WebElement addToCartButton = product.findElement(By.xpath(".//button[normalize-space()='Add To Cart']"));
        wait.until(elementToBeClickable(addToCartButton)).click();
    }
}