package asia.decentralab.copin.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class EnvironmentConfig {
    private static final Properties props = new Properties();
    private static EnvironmentConfig instance;
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentConfig.class);

    private EnvironmentConfig() {
        try {
            // Read file .env if existing
            if (Files.exists(Paths.get(".env"))) {
                props.load(new FileInputStream(".env"));
            } else {
                logger.info(".env file not found. Using default values.");
            }

            // Record with System Properties if any
            props.putAll(System.getProperties());
        } catch (IOException e) {
            logger.error("Failed to load environment config: {}", e.getMessage());
        }
    }

    public static synchronized EnvironmentConfig getInstance() {
        if (instance == null) {
            instance = new EnvironmentConfig();
        }
        return instance;
    }

    public String getApiBaseUrl() {
        return props.getProperty("API_BASE_URL", "https://api.example.com");
    }

    public String getWebBaseUrl() {
        return props.getProperty("WEB_BASE_URL", "https://example.com");
    }

    public String getBrowser() {
        return props.getProperty("DEFAULT_BROWSER", "chrome");
    }

    public boolean isHeadless() {
        return Boolean.parseBoolean(props.getProperty("HEADLESS_MODE", "false"));
    }

    public int getDefaultTimeout() {
        return Integer.parseInt(props.getProperty("DEFAULT_TIMEOUT", "30"));
    }

    public int getRetryCount() {
        return Integer.parseInt(props.getProperty("RETRY_COUNT", "2"));
    }

    public String getEnvironment() {
        return props.getProperty("ENVIRONMENT", "dev");
    }

    public int getImplicitWait() {
        return Integer.parseInt(props.getProperty("IMPLICIT_WAIT", "10"));
    }

    public boolean isScreenshotOnFailure() {
        return Boolean.parseBoolean(props.getProperty("SCREENSHOT_ON_FAILURE", "true"));
    }

    // The method to print out all the loaded configurations
    public void printConfig() {
        logger.info("=== Environment Configuration ===");
        logger.info("API_BASE_URL: {}", getApiBaseUrl());
        logger.info("WEB_BASE_URL: {}", getWebBaseUrl());
        logger.info("DEFAULT_BROWSER: {}", getBrowser());
        logger.info("HEADLESS_MODE: {}", isHeadless());
        logger.info("DEFAULT_TIMEOUT: {}", getDefaultTimeout());
        logger.info("IMPLICIT_WAIT: {}", getImplicitWait());
        logger.info("RETRY_COUNT: {}", getRetryCount());
        logger.info("ENVIRONMENT: {}", getEnvironment());
        logger.info("SCREENSHOT_ON_FAILURE: {}", isScreenshotOnFailure());
    }
}