package asia.decentralab.copin.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class EnvironmentConfig {
    private static final Properties props = new Properties();
    private static EnvironmentConfig instance;

    private EnvironmentConfig() {
        try {
            // Read file .env if existing
            if (Files.exists(Paths.get(".env"))) {
                props.load(new FileInputStream(".env"));
            } else {
                System.out.println(".env file not found. Using default values.");
            }

            // Record with System Properties if any
            props.putAll(System.getProperties());
        } catch (IOException e) {
            System.err.println("Failed to load environment config: " + e.getMessage());
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
        return props.getProperty("WEB_BASE_URL", "https://copin.io");
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
        System.out.println("=== Environment Configuration ===");
        System.out.println("API_BASE_URL: " + getApiBaseUrl());
        System.out.println("WEB_BASE_URL: " + getWebBaseUrl());
        System.out.println("DEFAULT_BROWSER: " + getBrowser());
        System.out.println("HEADLESS_MODE: " + isHeadless());
        System.out.println("DEFAULT_TIMEOUT: " + getDefaultTimeout());
        System.out.println("IMPLICIT_WAIT: " + getImplicitWait());
        System.out.println("RETRY_COUNT: " + getRetryCount());
        System.out.println("ENVIRONMENT: " + getEnvironment());
        System.out.println("SCREENSHOT_ON_FAILURE: " + isScreenshotOnFailure());
        System.out.println("================================");
    }
}