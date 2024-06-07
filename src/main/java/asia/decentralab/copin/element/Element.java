package asia.decentralab.copin.element;

import asia.decentralab.copin.browser.Driver;
import asia.decentralab.copin.config.Constant;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Element {
    private final By locator;
    private final WebDriver driver = Driver.getDriver();
    private final WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(Constant.WAIT_TIMEOUT_SECONDS));

    public Element(By locator) {
        this.locator = locator;
    }

    private WebElement findElement() {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public String getText() {
        return findElement().getText();
    }

    public String getValue(String value) {
        return findElement().getAttribute(value);
    }

    public void click() {
        findElement().click();
    }

    public void enter(String value) {
        findElement().sendKeys(value);
    }

    public boolean isEnable() {
        try {
            return findElement().isEnabled();
        } catch (org.openqa.selenium.NoSuchElementException | org.openqa.selenium.StaleElementReferenceException e) {
            return false;
        }
    }

    public boolean isDisplayed() {
        try {
            return findElement().isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException | org.openqa.selenium.StaleElementReferenceException e) {
            return false;
        }
    }
}
