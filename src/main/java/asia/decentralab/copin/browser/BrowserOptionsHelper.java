package asia.decentralab.copin.browser;

import asia.decentralab.copin.config.BrowserOptionsConfig;
import asia.decentralab.copin.config.Constant;
import asia.decentralab.copin.utils.JsonUtils;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

public class BrowserOptionsHelper {
    private static final BrowserOptionsConfig browserOptionsConfig = JsonUtils.readJsonFile(
            Constant.BROWSER_OPTIONS_FILE_PATH, BrowserOptionsConfig.class);

    public static ChromeOptions setChromeOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        for (String option : browserOptionsConfig.getChromeOptions()) {
            chromeOptions.addArguments(option);
        }
        return chromeOptions;
    }

    public static EdgeOptions setEdgeOptions() {
        EdgeOptions edgeOptions = new EdgeOptions();
        for (String option : browserOptionsConfig.getChromeOptions()) {
            edgeOptions.addArguments(option);
        }
        return edgeOptions;
    }

    public static FirefoxOptions setFirefoxOptions() {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        for (String option : browserOptionsConfig.getChromeOptions()) {
            firefoxOptions.addArguments(option);
        }
        return firefoxOptions;
    }
}
