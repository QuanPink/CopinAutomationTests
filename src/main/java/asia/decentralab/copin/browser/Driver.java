package asia.decentralab.copin.browser;

import asia.decentralab.copin.config.Constant;
import asia.decentralab.copin.data.enumdata.BrowserType;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.concurrent.TimeUnit;

public class Driver {
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static void openBrowser(BrowserType type, String version) {
        WebDriver driverInstance;
        switch (type) {
            case CHROME:
                WebDriverManager.chromedriver().browserVersion(version).setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                driverInstance = new ChromeDriver(chromeOptions);
                break;
            case EDGE:
                WebDriverManager.edgedriver().browserVersion(version).setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                driverInstance = new EdgeDriver(edgeOptions);
                break;
            case FIREFOX:
                WebDriverManager.firefoxdriver().browserVersion(version).setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                driverInstance = new FirefoxDriver(firefoxOptions);
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser or version: " + type + " " + version);
        }
        driver.set(driverInstance);
    }

    public static void navigation(String url) {
        getDriver().get(url);
        getDriver().manage().window().maximize();
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