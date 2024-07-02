package asia.decentralab.copin.element;

import asia.decentralab.copin.browser.Driver;
import asia.decentralab.copin.utils.WaitUtils;
import io.github.sukgu.Shadow;
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
    private final Shadow shadow;

    public Element(By locator) {
        this.locator = locator;
        this.wait = WaitUtils.waiting();
        this.actions = new Actions(Driver.getDriver());
        this.shadow = new Shadow(Driver.getDriver());
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
        } catch (TimeoutException e) {
            throw new TimeoutException("Elements not found: " + locator);
        }
    }

    public WebElement findShadowElement(String xpath) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return shadow.findElementByXPath(xpath);
        } catch (TimeoutException e) {
            throw new RuntimeException("Shadow element not found: " + xpath, e);
        }
    }

    public List<WebElement> findShadowElements(String xpath) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return shadow.findElementsByXPath(xpath);
        } catch (TimeoutException e) {
            throw new TimeoutException("Shadow elements not found: " + xpath, e);
        }
    }

    public String getText() {
        return findElement().getText();
    }

    public String getValue(String value) {
        return findElement().getAttribute(value);
    }

    public void click() {
        WebElement element = findElement();
        actions.moveToElement(element).perform();
        element.click();
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
