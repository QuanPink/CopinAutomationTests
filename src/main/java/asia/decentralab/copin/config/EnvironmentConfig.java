package asia.decentralab.copin.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Singleton configuration class that loads environment variables from .env file
 * and provides type-safe access to configuration values.
 */
public final class EnvironmentConfig {
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentConfig.class);
    private static final EnvironmentConfig INSTANCE = new EnvironmentConfig();

    // Configuration keys
    private static final String KEY_BROWSER = "DEFAULT_BROWSER";
    private static final String KEY_HEADLESS = "HEADLESS_MODE";
    private static final String KEY_WEB_BASE_URL = "WEB_BASE_URL";
    private static final String KEY_API_BASE_URL = "API_BASE_URL";
    private static final String KEY_THREAD_COUNT = "THREAD_COUNT";
    private static final String KEY_PARALLEL_MODE = "PARALLEL_MODE";
    private static final String KEY_RETRY_COUNT = "RETRY_COUNT";
    private static final String KEY_DEFAULT_TIMEOUT = "DEFAULT_TIMEOUT";
    private static final String KEY_IMPLICIT_WAIT = "IMPLICIT_WAIT";
    private static final String KEY_SCREENSHOT_ON_FAILURE = "SCREENSHOT_ON_FAILURE";
    private static final String KEY_EMAIL = "EMAIL";
    private static final String KEY_DEFAULT_OTP = "DEFAULT_OTP";
    private static final String KEY_X_API_KEY = "X_API_KEY";
    private static final String KEY_LARK_WEBHOOK_URL = "LARK_WEBHOOK_URL";

    static {
        loadEnvironment();
    }

    private EnvironmentConfig() {
        // Private constructor to prevent instantiation
    }

    public static EnvironmentConfig getInstance() {
        return INSTANCE;
    }

    private static void loadEnvironment() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            Set<String> keys = dotenv.entries().stream()
                    .map(DotenvEntry::getKey)
                    .collect(Collectors.toSet());

            int loadedCount = 0;
            for (String key : keys) {
                String value = dotenv.get(key);
                if (value != null && !value.isEmpty()) {
                    System.setProperty(key, value);
                    loadedCount++;
                }
            }

            logger.info("Loaded {} environment variables from .env", loadedCount);
        } catch (Exception e) {
            logger.error("Failed to load .env file: {}", e.getMessage(), e);
        }
    }

    // ==================== Helper Methods ====================

    private String getString(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

    private int getInt(String key, int defaultValue) {
        String value = getString(key, null);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer for {}: '{}', using default: {}", key, value, defaultValue);
            return defaultValue;
        }
    }

    private boolean getBoolean(String key, boolean defaultValue) {
        String value = getString(key, null);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    // ==================== Browser Configuration ====================

    public String getBrowser() {
        return getString(KEY_BROWSER, "chrome");
    }

    public boolean isHeadless() {
        return getBoolean(KEY_HEADLESS, false);
    }

    // ==================== URL Configuration ====================

    public String getWebBaseUrl() {
        return getString(KEY_WEB_BASE_URL, "https://example.com");
    }

    public String getApiBaseUrl() {
        return getString(KEY_API_BASE_URL, "https://api.example.com");
    }

    // ==================== Thread Configuration ====================

    public int getThreadCount() {
        return getInt(KEY_THREAD_COUNT, 1);
    }

    public String getParallelMode() {
        return getString(KEY_PARALLEL_MODE, "methods");
    }

    // ==================== Test Configuration ====================

    public int getRetryCount() {
        return getInt(KEY_RETRY_COUNT, 2);
    }

    public int getDefaultTimeout() {
        return getInt(KEY_DEFAULT_TIMEOUT, 30);
    }

    public int getImplicitWait() {
        return getInt(KEY_IMPLICIT_WAIT, 10);
    }

    public boolean isScreenshotOnFailure() {
        return getBoolean(KEY_SCREENSHOT_ON_FAILURE, true);
    }

    // ==================== Auth Configuration ====================

    public String getEmail() {
        return getString(KEY_EMAIL, "test@example.com");
    }

    public String getDefaultOtp() {
        return getString(KEY_DEFAULT_OTP, "123456");
    }

    public String getXApiKey() {
        return getString(KEY_X_API_KEY, "");
    }

    // ==================== Notification Configuration ====================

    public String getLarkWebhookUrl() {
        return getString(KEY_LARK_WEBHOOK_URL, "");
    }

    // ==================== Debug ====================

    public void printConfig() {
        logger.info("=== Environment Configuration ===");
        logger.info("API_BASE_URL: {}", getApiBaseUrl());
        logger.info("WEB_BASE_URL: {}", getWebBaseUrl());
        logger.info("BROWSER: {}", getBrowser());
        logger.info("HEADLESS: {}", isHeadless());
        logger.info("TIMEOUT: {}s", getDefaultTimeout());
        logger.info("IMPLICIT_WAIT: {}s", getImplicitWait());
        logger.info("RETRY_COUNT: {}", getRetryCount());
        logger.info("THREAD_COUNT: {}", getThreadCount());
        logger.info("PARALLEL_MODE: {}", getParallelMode());
        logger.info("SCREENSHOT_ON_FAILURE: {}", isScreenshotOnFailure());
        logger.info("=================================");
    }
}