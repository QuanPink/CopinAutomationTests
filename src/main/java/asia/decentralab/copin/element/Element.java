package asia.decentralab.copin.element;

import asia.decentralab.copin.utils.WaitUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class Element {
    private final By locator;
    private final WebDriverWait wait = WaitUtils.waiting();

    public Element(By locator) {
        this.locator = locator;
    }

    private WebElement findElement() {
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            throw new RuntimeException("Element not found: " + locator, e);
        }
    }

    public List<WebElement> findElements() {
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        } catch(TimeoutException e) {
            throw new TimeoutException("Elements not found: " + locator);
        }
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

    public void clearValue() {
        findElement().clear();
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
