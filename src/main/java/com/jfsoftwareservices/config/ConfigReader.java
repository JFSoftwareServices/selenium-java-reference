package com.jfsoftwareservices.config;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Central configuration access point.
 * <p>
 * Resolution order for every key is: real process environment variable first
 * (so Docker Compose and GitHub Actions environment variables always win),
 * then the local {@code .env} file (for IntelliJ / day-to-day local runs).
 */
public final class ConfigReader {

    private static final Dotenv DOTENV = Dotenv.configure()
            .ignoreIfMissing()
            .ignoreIfMalformed()
            .load();

    private ConfigReader() {
    }

    public static String get(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            value = DOTENV.get(key);
        }
        return value;
    }

    public static String get(String key, String defaultValue) {
        String value = get(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    public static String baseUrl() {
        return require("BASE_URL");
    }

    public static String testUserEmail() {
        return require("TEST_USER_EMAIL");
    }

    public static String testUserPassword() {
        return require("TEST_USER_PASSWORD");
    }

    /** e.g. "chrome" (default) or "firefox". */
    public static String browser() {
        return get("TEST_BROWSER", "chrome").toLowerCase();
    }

    /**
     * URL of a running Selenium Grid hub, e.g.
     * {@code http://selenium-hub:4444/wd/hub}
     * when running under Docker Compose. Null/blank means "run locally via
     * WebDriverManager", which is also the default for IntelliJ.
     */
    public static String gridUrl() {
        return get("GRID_URL");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(get("HEADLESS", "false"));
    }

   private static String require(String key) {
    String value = get(key);

    if (value == null || value.isBlank()) {
        throw new IllegalStateException(
                "Required configuration '" + key + "' is not set. "
                        + "Set it as an environment variable, or for local development, "
                        + "set it in a .env file (see .env.example).");
    }

    return value;
}
}