package com.jfsoftwareservices.driver;

import com.jfsoftwareservices.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
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
 * Creates one {@link WebDriver} per test thread.
 * <p>
 * Two modes, selected purely by configuration (no test code changes needed):
 * <ul>
 *   <li><b>Local</b> (default, used by IntelliJ / plain {@code mvn test}):
 *       WebDriverManager resolves a matching driver binary and launches a
 *       real local browser.</li>
 *   <li><b>Grid</b> (used by Docker Compose / Jenkins / AWS EC2): when
 *       {@code GRID_URL} is set, a {@link RemoteWebDriver} is created against
 *       the Selenium Grid hub instead, so the same test code runs unchanged
 *       against containerised chrome/firefox nodes.</li>
 * </ul>
 * A {@link ThreadLocal} backs every driver so TestNG's thread-count-driven
 * parallel execution (see {@code testng-parallel.xml}) never shares a
 * browser session across threads.
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final Duration IMPLICIT_WAIT = Duration.ofSeconds(2);
    private static final Duration PAGE_LOAD_TIMEOUT = Duration.ofSeconds(30);

    private DriverFactory() {
    }

    public static WebDriver getDriver() {
        if (DRIVER.get() == null) {
            DRIVER.set(createDriver());
        }
        return DRIVER.get();
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }

    private static WebDriver createDriver() {
        String browser = ConfigReader.browser();
        String gridUrl = ConfigReader.gridUrl();

        WebDriver driver = (gridUrl != null && !gridUrl.isBlank())
                ? createRemoteDriver(browser, gridUrl)
                : createLocalDriver(browser);

        driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT);
        driver.manage().timeouts().pageLoadTimeout(PAGE_LOAD_TIMEOUT);
        driver.manage().window().maximize();

        return driver;
    }

    private static WebDriver createLocalDriver(String browser) {
        return switch (browser) {
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                yield new FirefoxDriver(localFirefoxOptions());
            }
            case "chrome" -> {
                WebDriverManager.chromedriver().setup();
                yield new ChromeDriver(chromeOptions());
            }
            default -> throw new IllegalArgumentException("Unsupported TEST_BROWSER: " + browser);
        };
    }

    private static WebDriver createRemoteDriver(String browser, String gridUrl) {
        MutableCapabilities capabilities = switch (browser) {
            case "firefox" -> firefoxOptions();
            case "chrome" -> chromeOptions();
            default -> throw new IllegalArgumentException("Unsupported TEST_BROWSER: " + browser);
        };

        try {
            // Selenium 4's RemoteWebDriver auto-negotiates the W3C protocol
            // with the hub, so no separate DesiredCapabilities wiring is needed.
            return new RemoteWebDriver(URI.create(gridUrl).toURL(), capabilities);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid GRID_URL: " + gridUrl, e);
        }
    }

    private static ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");
        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        return options;
    }

    private static FirefoxOptions firefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        if (ConfigReader.isHeadless()) {
            options.addArguments("-headless");
        }
        return options;
    }

    /**
     * Local-only: Debian/Ubuntu's {@code firefox-esr} apt package (installed
     * by {@code .devcontainer/install-browsers.sh}) provides a binary named
     * {@code firefox-esr}, not {@code firefox} - which is what Selenium looks
     * for by default. This override must stay local-only: applying it to
     * {@link #firefoxOptions()} would send it as a capability to a Selenium
     * Grid node too, where {@code selenium/node-firefox} ships a binary
     * actually named {@code firefox} and the override would break it.
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