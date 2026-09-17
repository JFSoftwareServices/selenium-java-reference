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

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(id = "login")
    private WebElement signInButton;

    @FindBy(id = "userEmail")
    private WebElement usernameField;

    @FindBy(id = "userPassword")
    private WebElement passwordField;

    @FindBy(xpath = "//*[@role='alert' and contains(., 'Incorrect email or password')]")
    private WebElement errorAlert;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void goTo() {
        driver.get(ConfigReader.baseUrl() + "/client");
    }

    public void waitForLoginForm() {
        wait.until(visibilityOf(usernameField));
        wait.until(visibilityOf(passwordField));
        wait.until(elementToBeClickable(signInButton));
    }

    public void login(String username, String password) {
        usernameField.clear();
        usernameField.sendKeys(username);
        passwordField.clear();
        passwordField.sendKeys(password);
        signInButton.click();
    }

    public String getErrorMessage() {
        return wait.until(visibilityOf(errorAlert)).getText();
    }

    public void verifyLoggedOut() {
        wait.until(visibilityOf(usernameField));
        wait.until(visibilityOf(passwordField));
        wait.until(elementToBeClickable(signInButton));
    }
}