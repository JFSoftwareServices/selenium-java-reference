package com.jfsoftwareservices.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class OrdersHistoryPage {

    private final WebDriverWait wait;

    @FindBy(css = "tbody")
    private WebElement ordersTable;

    @FindBy(css = "tbody tr")
    private List<WebElement> rows;

    @FindBy(css = ".col-text")
    private WebElement orderIdDetails;

    public OrdersHistoryPage(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        PageFactory.initElements(driver, this);
    }

    public void waitForPageToLoad() {
        wait.until(ExpectedConditions.visibilityOf(ordersTable));
    }

    public WebElement findOrderRow(String orderId) {
        wait.until(ExpectedConditions.visibilityOf(ordersTable));

        List<WebElement> matching = rows.stream()
                .filter(row -> row.findElements(By.tagName("th")).stream()
                        .anyMatch(th -> th.getText().contains(orderId)))
                .toList();

        if (matching.isEmpty()) {
            throw new IllegalStateException(
                    "Order \"" + orderId + "\" not found in order history."
            );
        }

        return matching.get(0);
    }

    public void selectOrder(String orderId) {
        WebElement row = findOrderRow(orderId);

        wait.until(driver ->
                row.findElement(By.tagName("button")).isDisplayed()
        );

        row.findElement(By.tagName("button")).click();
    }

    public String getOrderId() {
        String raw = wait.until(
                ExpectedConditions.visibilityOf(orderIdDetails)
        ).getText();

        return raw == null ? null : raw.replace("|", "").trim();
    }
}