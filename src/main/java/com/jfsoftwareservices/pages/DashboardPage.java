package com.jfsoftwareservices.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DashboardPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private List<WebElement> productCards() {
        return driver.findElements(By.cssSelector(".card-body"));
    }

    public WebElement findProduct(String productName) {
        List<WebElement> matching = productCards().stream()
                .filter(card -> card.getText().contains(productName))
                .toList();

        assertThat(matching)
                .as("product card matching '%s'", productName)
                .hasSize(1);

        return matching.get(0);
    }

    public void addProductToCart(String productName) {
        WebElement product = findProduct(productName);
        product.findElement(By.xpath(".//button[normalize-space()='Add To Cart']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@role='alert'][contains(., 'Product Added To Cart')]")));
    }
}