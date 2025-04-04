package asia.decentralab.copin.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BaseElement {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected By locator;

    public BaseElement(WebDriver driver, By locator) {
        this.driver = driver;
        this.locator = locator;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public WebElement getElement() {
        return driver.findElement(locator);
    }

    public void waitForVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public boolean isDisplayed() {
        try {
            return getElement().isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getText() {
        return getElement().getText();
    }
}