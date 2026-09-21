package com.jfsoftwareservices.pages;

import com.jfsoftwareservices.config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import static org.openqa.selenium.support.ui.ExpectedConditions.urlContains;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final String URL_FRAGMENT = "client/#/auth/login";

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

    public LoginPage navigateTo() {
        driver.get(ConfigReader.baseUrl() + URL_FRAGMENT);
        return waitUntilLoaded();
    }

    // Navigates to the login URL without waiting for the form to render.
    // Use this when you don't need the UI to be interactive yet (e.g.
    // AuthenticatedTest, which authenticates via API and only needs the
    // browser to be on the app's origin so localStorage can be set).
    public LoginPage navigateWithoutWaiting() {
        driver.get(ConfigReader.baseUrl() + URL_FRAGMENT);
        return this;
    }

    // Confirms URL + one anchor element are ready — not a guarantee that
    // every element on the page has rendered. Action methods below wait on
    // their own targets individually.
    public LoginPage waitUntilLoaded() {
        wait.until(urlContains(URL_FRAGMENT));
        wait.until(visibilityOf(usernameField));
        return this;
    }

    // Does not return the next page — login can succeed (call goToDashboard())
    // or fail (call getErrorMessage()), so the caller decides which applies.
    public LoginPage login(String username, String password) {
        wait.until(visibilityOf(usernameField)).sendKeys(username);
        wait.until(visibilityOf(passwordField)).sendKeys(password);
        wait.until(elementToBeClickable(signInButton)).click();
        return this;
    }

    public DashboardPage goToDashboard() {
        return new DashboardPage(driver).waitUntilLoaded();
    }

    public String getErrorMessage() {
        return wait.until(visibilityOf(errorAlert)).getText();
    }
}