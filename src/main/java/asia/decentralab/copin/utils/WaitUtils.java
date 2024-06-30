package asia.decentralab.copin.utils;

import asia.decentralab.copin.browser.Driver;
import asia.decentralab.copin.config.Constant;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitUtils {
    private static final WebDriver driver = Driver.getDriver();

    public static WebDriverWait waiting() {
        return new WebDriverWait(driver, Duration.ofSeconds(Constant.WAIT_TIMEOUT_SECONDS));
    }
}
