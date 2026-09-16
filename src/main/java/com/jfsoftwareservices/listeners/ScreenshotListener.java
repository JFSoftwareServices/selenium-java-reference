package com.jfsoftwareservices.listeners;

import com.jfsoftwareservices.driver.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class ScreenshotListener implements ITestListener {

    private static final Logger LOG = LoggerFactory.getLogger(ScreenshotListener.class);
    private static final Path SCREENSHOT_DIR = Path.of("target", "screenshots");

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = DriverFactory.getDriver();
        if (!(driver instanceof TakesScreenshot takesScreenshot)) {
            return;
        }

        try {
            Files.createDirectories(SCREENSHOT_DIR);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            String fileName = "%s-%s.png".formatted(result.getMethod().getMethodName(), timestamp);

            File source = takesScreenshot.getScreenshotAs(OutputType.FILE);
            Path destination = SCREENSHOT_DIR.resolve(fileName);
            Files.copy(source.toPath(), destination);

            LOG.warn("Test '{}' failed - screenshot saved to {}", result.getName(), destination);
        } catch (IOException e) {
            LOG.error("Failed to capture screenshot for '{}'", result.getName(), e);
        }
    }
}