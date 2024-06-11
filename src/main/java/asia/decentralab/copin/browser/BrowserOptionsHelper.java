package asia.decentralab.copin.browser;

import asia.decentralab.copin.config.BrowserOptionsConfig;
import asia.decentralab.copin.config.Config;
import asia.decentralab.copin.config.Constant;
import asia.decentralab.copin.utils.JsonUtils;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

public class BrowserOptionsHelper {
    public static void setBrowserOptions(Object options, Config config) {
        BrowserOptionsConfig browserOptionsConfig = JsonUtils.readJsonFile(Constant.BROWSER_OPTIONS_FILE_PATH, BrowserOptionsConfig.class);

        if (options instanceof ChromeOptions) {
            ChromeOptions chromeOptions = (ChromeOptions) options;
            for (String option : browserOptionsConfig.getChromeOptions()) {
                chromeOptions.addArguments(option);
            }
            if (config.isHeadless()) {
                chromeOptions.addArguments(browserOptionsConfig.getHeadless());
            }
        } else if (options instanceof EdgeOptions) {
            EdgeOptions edgeOptions = (EdgeOptions) options;
            for (String option : browserOptionsConfig.getEdgeOptions()) {
                edgeOptions.addArguments(option);
            }
            if (config.isHeadless()) {
                edgeOptions.addArguments(browserOptionsConfig.getHeadless());
            }
        } else if (options instanceof FirefoxOptions) {
            FirefoxOptions firefoxOptions = (FirefoxOptions) options;
            for (String option : browserOptionsConfig.getFirefoxOptions()) {
                firefoxOptions.addArguments(option);
            }
            if (config.isHeadless()) {
                firefoxOptions.addArguments(browserOptionsConfig.getHeadless());
            }
        } else {
            throw new IllegalArgumentException("Unsupported browser options type: " + options);
        }
    }
}
