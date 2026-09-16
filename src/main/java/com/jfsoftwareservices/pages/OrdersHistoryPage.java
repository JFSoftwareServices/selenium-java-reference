package com.jfsoftwareservices.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class OrdersHistoryPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By ORDERS_TABLE = By.cssSelector("tbody");
    private static final By ROWS = By.cssSelector("tbody tr");
    private static final By ORDER_ID_DETAILS = By.cssSelector(".col-text");

    public OrdersHistoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public WebElement findOrderRow(String orderId) {
        wait.until(ExpectedConditions.presenceOfElementLocated(ORDERS_TABLE));

        List<WebElement> matching = driver.findElements(ROWS).stream()
                .filter(row -> row.findElements(By.tagName("th")).stream()
                        .anyMatch(th -> th.getText().contains(orderId)))
                .toList();

        if (matching.isEmpty()) {
            throw new IllegalStateException("Order \"" + orderId + "\" not found in order history.");
        }

        return matching.get(0);
    }

    public void selectOrder(String orderId) {
        WebElement row = findOrderRow(orderId);
        row.findElement(By.tagName("button")).click();
    }

    public String getOrderId() {
        String raw = driver.findElement(ORDER_ID_DETAILS).getText();
        return raw == null ? null : raw.replace("|", "").trim();
    }
}