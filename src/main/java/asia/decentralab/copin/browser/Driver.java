package asia.decentralab.copin.browser;

import asia.decentralab.copin.config.Config;
import asia.decentralab.copin.config.Constant;
import asia.decentralab.copin.data.enumdata.BrowserType;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
                ExtensionHelper.addChromeExtensions(chromeOptions);
                driverInstance = new ChromeDriver(chromeOptions);
                break;
            case EDGE:
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = BrowserOptionsHelper.setEdgeOptions();
                if (config.isHeadless()) {
                    edgeOptions.addArguments("--headless");
                }
                ExtensionHelper.addEdgeExtensions(edgeOptions);
                driverInstance = new EdgeDriver(edgeOptions);
                break;
            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = BrowserOptionsHelper.setFirefoxOptions();
                if (config.isHeadless()) {
                    firefoxOptions.addArguments("--headless");
                }
                ExtensionHelper.addFireFoxExtensions(firefoxOptions);
                driverInstance = new FirefoxDriver(firefoxOptions);
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browserType);
        }
        driverInstance.manage().deleteAllCookies();
        driver.set(driverInstance);
    }

    public static void navigate(String path) {
        getDriver().get(path);
        getDriver().manage().window().maximize();
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

    public static void closeWindow() {
        if (getDriver() != null) {
            getDriver().close();
        }
    }

    public static void closeBrowser() {
        if (getDriver() != null) {
            getDriver().quit();
        }
    }

    public static WebDriverWait getWait() {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(Constant.WAIT_TIMEOUT_SECONDS));
    }

    public static void openNewWindow() {
        ((JavascriptExecutor) getDriver()).executeScript("window.open()");
    }

    public static void switchToWindow(int windowNumber) {
        getWait().until(ExpectedConditions.numberOfWindowsToBe(windowNumber));
        Set<String> windows = getDriver().getWindowHandles();

        List<String> windowList = new ArrayList<>(windows);

        if (!windowList.isEmpty()) {
            getDriver().switchTo().window(windowList.get(windowList.size() - 1));
        } else {
            throw new IllegalStateException("No windows available to switch to.");
        }
    }

    public static void closeAdditionalWindows(int windowNumber) {
        getWait().until(ExpectedConditions.numberOfWindowsToBe(windowNumber));
        Set<String> windows = getDriver().getWindowHandles();

        List<String> windowList = new ArrayList<>(windows);

        for (int i = windowList.size() - 1; i >= 0; i--) {
            String window = windowList.get(i);
            getDriver().switchTo().window(window);
            if (getDriver().getWindowHandles().size() > 1) {
                closeWindow();
            }
        }
    }

    public static WebDriver getDriver() {
        return driver.get();
    }
}