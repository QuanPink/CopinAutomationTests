package asia.decentralab.copin.browser;

import asia.decentralab.copin.config.EnvironmentConfig;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class WebDriverManager {
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final EnvironmentConfig config = EnvironmentConfig.getInstance();

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

        System.out.println("Initializing " + browser + " browser (headless: " + headless + ")");

        // Sử dụng BrowserFactory để tạo driver
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

        System.out.println("WebDriver configured with timeout: " + defaultTimeout + "s, implicit wait: " + implicitWait + "s");
    }

    public static void quitDriver() {
        WebDriver webDriver = driver.get();
        if (webDriver != null) {
            System.out.println("Quitting WebDriver");
            webDriver.quit();
            driver.remove();
        }
    }
}