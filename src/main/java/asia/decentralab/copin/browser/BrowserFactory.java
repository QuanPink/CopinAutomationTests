package asia.decentralab.copin.browser;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrowserFactory {
    private static final Logger logger = LoggerFactory.getLogger(BrowserFactory.class);

    public static WebDriver createDriver(String browserType, boolean headless) {
        if (browserType == null) {
            logger.info("Browser type is null. Using Chrome as default.");
            return createChromeDriver(headless);
        }

        switch (browserType.toLowerCase()) {
            case "chrome":
                return createChromeDriver(headless);
            case "firefox":
                return createFirefoxDriver(headless);
            case "edge":
                return createEdgeDriver(headless);
            default:
                logger.info("Browser type '{}' not recognized. Using Chrome as default.", browserType);
                return createChromeDriver(headless);
        }
    }

    private static WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        if (headless) {
            options.addArguments("--headless=new");
        }

        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");

        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();

        if (headless) {
            options.addArguments("--headless");
        }

        return new FirefoxDriver(options);
    }

    private static WebDriver createEdgeDriver(boolean headless) {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();

        if (headless) {
            options.addArguments("--headless");
        }

        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-notifications");

        return new EdgeDriver(options);
    }
}