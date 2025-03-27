package asia.decentralab.copin.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class WaitUtils {
    private final WebDriverWait wait;
    private static final Logger logger = LoggerFactory.getLogger(WaitUtils.class);

    public WaitUtils(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void waitForVisible(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public void waitForClickable(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void waitForPresence(By locator) {
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public void waitForInvisibility(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public void waitForPageLoad(By loadingLocator) {
        try {
            waitForPresence(loadingLocator);
            waitForInvisibility(loadingLocator);
        } catch (TimeoutException e) {
            logger.info("Page load timeout or loading element not found");
        }
    }
}
