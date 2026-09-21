package com.jfsoftwareservices.pages;

import com.jfsoftwareservices.config.ConfigReader;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class OrdersPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final String URL_FRAGMENT = "#/dashboard/myorders";

    @FindBy(css = "tbody")
    private WebElement ordersTable;

    @FindBy(css = "tbody tr")
    private List<WebElement> rows;

    @FindBy(css = ".col-text")
    private WebElement orderIdDetails;

    public OrdersPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public OrdersPage navigateTo() {
        driver.get(ConfigReader.baseUrl() + URL_FRAGMENT);
        return waitUntilLoaded();
    }

    // Confirms URL + one anchor element are ready — not a guarantee that
    // every element on the page has rendered. Action methods below wait on
    // their own targets individually.
    public OrdersPage waitUntilLoaded() {
        wait.until(ExpectedConditions.urlContains(URL_FRAGMENT));
        wait.until(ExpectedConditions.visibilityOf(ordersTable));
        return this;
    }

    public WebElement findOrderRow(String orderId) {
        wait.until(ExpectedConditions.visibilityOf(ordersTable));

        List<WebElement> matching = rows.stream()
                .filter(row -> row.findElements(By.tagName("th")).stream()
                        .anyMatch(th -> th.getText().contains(orderId)))
                .toList();

        if (matching.isEmpty()) {
            throw new IllegalStateException(
                    "Order \"" + orderId + "\" not found in order history.");
        }

        return matching.get(0);
    }

    public OrderDetailsPage selectOrder(String orderId) {
        WebElement row = findOrderRow(orderId);
        wait.until(driver -> row.findElement(By.tagName("button")).isDisplayed());
        row.findElement(By.tagName("button")).click();
        return new OrderDetailsPage(driver).waitUntilLoaded();
    }
}