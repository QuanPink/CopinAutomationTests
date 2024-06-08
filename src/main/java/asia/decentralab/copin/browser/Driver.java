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
                ChromeOptions chromeOptions = new ChromeOptions();
                if (config.isHeadless()) {
                    chromeOptions.addArguments("--headless");
                }
                addBrowserOptions(chromeOptions, config.getBrowserOptions());
                driverInstance = new ChromeDriver(chromeOptions);
                break;
            case EDGE:
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (config.isHeadless()) {
                    edgeOptions.addArguments("--headless");
                }
                addBrowserOptions(edgeOptions, config.getBrowserOptions());
                driverInstance = new EdgeDriver(edgeOptions);
                break;
            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (config.isHeadless()) {
                    firefoxOptions.addArguments("--headless");
                }
                addBrowserOptions(firefoxOptions, config.getBrowserOptions());
                driverInstance = new FirefoxDriver(firefoxOptions);
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browserType);
        }
        driver.set(driverInstance);
    }

    private static void addBrowserOptions(Object options, String[] browserOptions) {
        if (options instanceof ChromeOptions) {
            ChromeOptions chromeOptions = (ChromeOptions) options;
            for (String option : browserOptions) {
                chromeOptions.addArguments(option);
            }
        } else if (options instanceof EdgeOptions) {
            EdgeOptions edgeOptions = (EdgeOptions) options;
            for (String option : browserOptions) {
                edgeOptions.addArguments(option);
            }
        } else if (options instanceof FirefoxOptions) {
            FirefoxOptions firefoxOptions = (FirefoxOptions) options;
            for (String option : browserOptions) {
                firefoxOptions.addArguments(option);
            }
        } else {
            throw new IllegalArgumentException("Unsupported browser options type");
        }
    }

    public static void navigation(Config config) {
        getDriver().get(config.getBaseUrl());
    }

    public static String getTitle() {
        return getDriver().getTitle();
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