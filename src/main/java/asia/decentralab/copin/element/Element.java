package asia.decentralab.copin.element;

import asia.decentralab.copin.browser.Driver;
import asia.decentralab.copin.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class Element {
    private final By locator;
    private final WebDriverWait wait;
    private final Actions actions;

    public Element(By locator) {
        this.locator = locator;
        this.wait = WaitUtils.waiting();
        this.actions = new Actions(Driver.getDriver());
    }

    private WebElement findElement() {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            actions.moveToElement(element).perform();
            return element;
        } catch (TimeoutException e) {
            throw new RuntimeException("Element not found: " + locator, e);
        }
    }

    public List<WebElement> findElements() {
        try {
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        } catch (TimeoutException e) {
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
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDisplayed() {
        try {
            return findElement().isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
