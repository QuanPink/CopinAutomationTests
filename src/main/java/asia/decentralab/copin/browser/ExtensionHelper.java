package asia.decentralab.copin.browser;

import asia.decentralab.copin.config.Constant;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.io.File;

public class ExtensionHelper {
    private static final String trustWalletPath = Constant.TRUST_WALLET_EXTENSION_FILE_PATH;

    public static void addChromeExtensions(ChromeOptions chromeOptions) {
        chromeOptions.addExtensions(new File(trustWalletPath));
    }

    public static void addEdgeExtensions(EdgeOptions edgeOptions) {
        edgeOptions.addExtensions(new File(trustWalletPath));
    }

    public static void addFireFoxExtensions(FirefoxOptions firefoxOptions) {
        FirefoxProfile profile = new FirefoxProfile();
        profile.addExtension(new File(trustWalletPath));
        firefoxOptions.setProfile(profile);
    }
}