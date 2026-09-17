package com.jfsoftwareservices.driver;

import com.jfsoftwareservices.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

/**
 * Creates one WebDriver instance per test thread.
 *
 * <p>
 * Supports:
 * <ul>
 * <li>Local Chrome/Chromium</li>
 * <li>Local Firefox</li>
 * <li>Remote Chrome/Chromium via Selenium Grid</li>
 * <li>Remote Firefox via Selenium Grid</li>
 * <li>Headless execution</li>
 * <li>Parallel TestNG execution via ThreadLocal</li>
 * </ul>
 *
 * <p>
 * Browser-specific configuration is handled by the appropriate
 * browser options method, while common WebDriver configuration is
 * applied centrally in {@link #configureDriver(WebDriver)}.
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private static final Duration PAGE_LOAD_TIMEOUT = Duration.ofSeconds(30);

    private static final Dimension WINDOW_SIZE = new Dimension(1920, 1080);

    private DriverFactory() {
        // Utility class
    }

    /**
     * Returns the WebDriver for the current test thread.
     * Creates the driver if one has not already been created.
     */
    public static WebDriver getDriver() {

        WebDriver driver = DRIVER.get();

        if (driver == null) {
            driver = createDriver();
            DRIVER.set(driver);
        }

        return driver;
    }

    /**
     * Quits and removes the WebDriver for the current test thread.
     *
     * <p>
     * Cleanup is deliberately defensive:
     * <ul>
     * <li>Attempts to quit the browser even if the session is already
     * partially broken.</li>
     * <li>Always removes the driver from ThreadLocal.</li>
     * <li>Does not allow a quit failure to prevent ThreadLocal cleanup.</li>
     * </ul>
     */
    public static void quitDriver() {

        WebDriver driver = DRIVER.get();

        if (driver == null) {
            return;
        }

        try {
            driver.quit();
        } catch (Exception e) {
            // Browser/session may already have terminated.
            // Do not allow cleanup failure to mask the original test failure.
        } finally {
            DRIVER.remove();
        }
    }

    /**
     * Creates either a local or remote WebDriver based on configuration.
     */
    private static WebDriver createDriver() {

        String browser = ConfigReader.browser();
        String gridUrl = ConfigReader.gridUrl();

        WebDriver driver = (gridUrl != null && !gridUrl.isBlank())
                ? createRemoteDriver(browser, gridUrl)
                : createLocalDriver(browser);

        configureDriver(driver);

        return driver;
    }

    /**
     * Applies configuration common to all browsers and execution modes.
     *
     * <p>
     * Implicit waits are deliberately not configured.
     * The framework uses explicit waits for synchronisation.
     */
    private static void configureDriver(WebDriver driver) {

        driver.manage()
                .timeouts()
                .pageLoadTimeout(PAGE_LOAD_TIMEOUT);

        driver.manage()
                .window()
                .setSize(WINDOW_SIZE);
    }

    /**
     * Creates a local WebDriver.
     */
    private static WebDriver createLocalDriver(String browser) {

        return switch (browser.toLowerCase()) {

            case "chrome", "chromium" -> {

                WebDriverManager.chromedriver().setup();

                yield new ChromeDriver(
                        chromeOptions());
            }

            case "firefox" -> {

                WebDriverManager.firefoxdriver().setup();

                yield new FirefoxDriver(
                        localFirefoxOptions());
            }

            default -> throw new IllegalArgumentException(
                    "Unsupported TEST_BROWSER: " + browser);
        };
    }

    /**
     * Creates a RemoteWebDriver for Selenium Grid.
     */
    private static WebDriver createRemoteDriver(
            String browser,
            String gridUrl) {

        MutableCapabilities capabilities = switch (browser.toLowerCase()) {

            case "chrome", "chromium" ->
                chromeOptions();

            case "firefox" ->
                firefoxOptions();

            default ->
                throw new IllegalArgumentException(
                        "Unsupported TEST_BROWSER: " + browser);
        };

        try {

            return new RemoteWebDriver(
                    URI.create(gridUrl).toURL(),
                    capabilities);

        } catch (MalformedURLException e) {

            throw new IllegalStateException(
                    "Invalid GRID_URL: " + gridUrl,
                    e);
        }
    }

    /**
     * Creates Chrome/Chromium-specific options.
     */
    private static ChromeOptions chromeOptions() {

        ChromeOptions options = new ChromeOptions();

        options.addArguments(
                "--remote-allow-origins=*",
                "--disable-notifications");

        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless=new");
        }

        return options;
    }

    /**
     * Creates Firefox-specific options.
     */
    private static FirefoxOptions firefoxOptions() {

        FirefoxOptions options = new FirefoxOptions();

        if (ConfigReader.isHeadless()) {
            options.addArguments("-headless");
        }

        return options;
    }

    /**
     * Creates Firefox options for local execution.
     *
     * <p>
     * Debian/Ubuntu environments may provide Firefox ESR as
     * {@code /usr/bin/firefox-esr}. This binary override is intentionally
     * local-only and must not be sent to Selenium Grid Firefox nodes.
     */
    private static FirefoxOptions localFirefoxOptions() {

        FirefoxOptions options = firefoxOptions();

        Path firefoxEsr = Path.of("/usr/bin/firefox-esr");

        if (Files.exists(firefoxEsr)) {
            options.setBinary(firefoxEsr);
        }

        return options;
    }
}
