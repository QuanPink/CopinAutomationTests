package asia.decentralab.copin.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

public class EnvironmentConfig {
    private static EnvironmentConfig instance;
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentConfig.class);

    // Initialize configuration by loading from .env and setting to System properties
    static {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            // Get all keys from dotenv
            Set<String> keys = dotenv.entries().stream()
                    .map(DotenvEntry::getKey)
                    .collect(Collectors.toSet());

            // Iterate through all existing environment variables
            for (String key : keys) {
                if (System.getProperty(key) == null) {
                    String value = dotenv.get(key);
                    if (value != null && !value.isEmpty()) {
                        System.setProperty(key, value);
                    }
                }
            }

            logger.info("Environment loaded from .env to System properties");
        } catch (Exception e) {
            logger.error("Failed to load environment from .env: {}", e.getMessage(), e);
        }
    }

    private EnvironmentConfig() {
        // Private constructor
    }

    // Get singleton instance
    public static synchronized EnvironmentConfig getInstance() {
        if (instance == null) {
            instance = new EnvironmentConfig();
        }
        return instance;
    }

    // Get a configuration value from System properties with fallback to default value
    private String getConfigValue(String key, String defaultValue) {
        String value = System.getProperty(key);

        if (value == null || value.trim().isEmpty()) {
            logger.debug("Using default value for {}: {}", key, defaultValue);
            return defaultValue;
        }

        return value;
    }

    // Get an integer configuration value
    private int getIntConfigValue(String key, int defaultValue) {
        String value = getConfigValue(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("Invalid value for {}: {}. Using default: {}. Error: {}",
                    key, value, defaultValue, e.getMessage());
            return defaultValue;
        }
    }

    // Get a boolean configuration value
    private boolean getBooleanConfigValue(String key, boolean defaultValue) {
        String value = getConfigValue(key, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }

    // Browser configuration
    public String getBrowser() {
        return getConfigValue("DEFAULT_BROWSER", "chrome");
    }

    public boolean isHeadless() {
        return getBooleanConfigValue("HEADLESS_MODE", false);
    }

    // URL configuration
    public String getWebBaseUrl() {
        return getConfigValue("WEB_BASE_URL", "https://example.com");
    }

    public String getApiBaseUrl() {
        return getConfigValue("API_BASE_URL", "https://api.example.com");
    }

    // Thread configuration
    public int getThreadCount() {
        return getIntConfigValue("THREAD_COUNT", 1);
    }

    public String getParallelMode() {
        return getConfigValue("PARALLEL_MODE", "methods");
    }

    // Test configuration
    public int getRetryCount() {
        return getIntConfigValue("RETRY_COUNT", 2);
    }

    public String getEnvironment() {
        return getConfigValue("ENVIRONMENT", "dev");
    }

    public int getDefaultTimeout() {
        return getIntConfigValue("DEFAULT_TIMEOUT", 30);
    }

    public int getImplicitWait() {
        return getIntConfigValue("IMPLICIT_WAIT", 10);
    }

    public boolean isScreenshotOnFailure() {
        return getBooleanConfigValue("SCREENSHOT_ON_FAILURE", true);
    }

    // Print all the loaded configurations
    public void printConfig() {
        logger.info("=== Environment Configuration ===");
        logger.info("API_BASE_URL: {}", getApiBaseUrl());
        logger.info("WEB_BASE_URL: {}", getWebBaseUrl());
        logger.info("DEFAULT_BROWSER: {}", getBrowser());
        logger.info("HEADLESS_MODE: {}", isHeadless());
        logger.info("DEFAULT_TIMEOUT: {}s", getDefaultTimeout());
        logger.info("IMPLICIT_WAIT: {}s", getImplicitWait());
        logger.info("RETRY_COUNT: {}", getRetryCount());
        logger.info("THREAD_COUNT: {}", getThreadCount());
        logger.info("PARALLEL_MODE: {}", getParallelMode());
        logger.info("ENVIRONMENT: {}", getEnvironment());
        logger.info("SCREENSHOT_ON_FAILURE: {}", isScreenshotOnFailure());
        logger.info("=================================");
    }
}