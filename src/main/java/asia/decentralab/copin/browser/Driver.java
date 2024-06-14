package asia.decentralab.copin.browser;

import asia.decentralab.copin.config.Config;
import asia.decentralab.copin.data.enumdata.BrowserType;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class Driver {
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static void openBrowser(Config config) {
        BrowserType browserType = BrowserType.valueOf(config.getBrowser().toUpperCase());
        WebDriver driverInstance;
        switch (browserType) {
            case CHROME:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = BrowserOptionsHelper.setChromeOptions();
                if (config.isHeadless()) {
                    chromeOptions.addArguments("--headless");
                }
                driverInstance = new ChromeDriver(chromeOptions);
                break;
            case EDGE:
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = BrowserOptionsHelper.setEdgeOptions();
                if (config.isHeadless()) {
                    edgeOptions.addArguments("--headless");
                }
                driverInstance = new EdgeDriver(edgeOptions);
                break;
            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = BrowserOptionsHelper.setFirefoxOptions();
                if (config.isHeadless()) {
                    firefoxOptions.addArguments("--headless");
                }
                driverInstance = new FirefoxDriver(firefoxOptions);
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browserType);
        }
        driver.set(driverInstance);
    }

    public static void navigate(String path) {
        getDriver().get(path);
    }

    public static String getTitle() {
        return getDriver().getTitle();
    }

    public static String getCurrentUrl() {
        return getDriver().getCurrentUrl();
    }

    public static void backToPreviousPage() {
        getDriver().navigate().back();
    }

    public static void refreshPage() {
        getDriver().navigate().refresh();
    }

    public static void closeBrowser() {
        if (getDriver() != null) {
            getDriver().quit();
        }
    }

    public static WebDriver getDriver() {
        return driver.get();
    }
}