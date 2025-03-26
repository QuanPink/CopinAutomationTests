package asia.decentralab.copin.browser;

import asia.decentralab.copin.config.EnvironmentConfig;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class WebDriverManager {
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final EnvironmentConfig config = EnvironmentConfig.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(WebDriverManager.class);

    private WebDriverManager() {
        // Private constructor
    }

    public static WebDriver getDriver() {
        if (driver.get() == null) {
            initDriver();
        }
        return driver.get();
    }

    public static void initDriver() {
        String browser = config.getBrowser();
        boolean headless = config.isHeadless();

        logger.info("Initializing {} browser (headless: {})", browser, headless);

        // Use BrowserFactory to create a driver
        driver.set(BrowserFactory.createDriver(browser, headless));
        configureDriver();
    }

    private static void configureDriver() {
        WebDriver webDriver = driver.get();

        // Get timeout from config
        int defaultTimeout = config.getDefaultTimeout();
        int implicitWait = config.getImplicitWait();

        // General configuration
        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
        webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(defaultTimeout));
        webDriver.manage().timeouts().scriptTimeout(Duration.ofSeconds(defaultTimeout));

        logger.info("WebDriver configured with timeout: {}s, implicit wait: {}s", defaultTimeout, implicitWait);
    }

    public static void quitDriver() {
        WebDriver webDriver = driver.get();
        if (webDriver != null) {
            logger.info("Quitting WebDriver");
            webDriver.quit();
            driver.remove();
        }
    }
}