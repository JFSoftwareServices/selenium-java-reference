package com.jfsoftwareservices.pages;

import com.jfsoftwareservices.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {

    private final WebDriver driver;

    @FindBy(xpath = "//button[normalize-space()='Login']")
    private WebElement signInButton;

    @FindBy(xpath = "//input[@placeholder='email@example.com' or @name='userEmail']")
    private WebElement usernameField;

    @FindBy(xpath = "//input[@placeholder='enter your passsword' or @name='userPassword']")
    private WebElement passwordField;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    /**
     * Navigates to the login/client entry point (redirects to dashboard if already
     * authenticated).
     */
    public void goTo() {
        driver.get(ConfigReader.baseUrl() + "/client");
    }

    public void login(String username, String password) {
        usernameField.clear();
        usernameField.sendKeys(username);
        passwordField.clear();
        passwordField.sendKeys(password);
        signInButton.click();
    }

    public WebElement errorAlert() {
        return driver.findElement(By.xpath("//*[@role='alert' and contains(., 'Incorrect email or password')]"));
    }

    public WebElement loginHeading() {
        return driver.findElement(By.xpath("//h1[normalize-space()='Log in'] | //h2[normalize-space()='Log in']"));
    }
}