package asia.decentralab.copin.element;

import asia.decentralab.copin.browser.Driver;
import asia.decentralab.copin.data.enumdata.BackgroundColor;
import io.github.sukgu.Shadow;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

public class Element {
    private final By locator;
    private final Actions actions = new Actions(Driver.getDriver());
    private final Shadow shadow = new Shadow(Driver.getDriver());

    public Element(By locator) {
        this.locator = locator;
    }

    private WebElement findElement() {
        try {
            return Driver.getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            throw new RuntimeException("Element not found: " + locator, e);
        }
    }

    private WebElement findElement(int second) {
        try {
            return Driver.getWait(second).until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            throw new RuntimeException("Element not found: " + locator, e);
        }
    }

    public List<WebElement> findElements() {
        try {
            return Driver.getWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        } catch (TimeoutException e) {
            throw new RuntimeException("Elements not found: " + locator);
        }
    }

    public WebElement findShadowElement(String xpath) {
        try {
            Driver.getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
            return shadow.findElementByXPath(xpath);
        } catch (TimeoutException e) {
            throw new RuntimeException("Shadow element not found: " + xpath, e);
        }
    }

    public List<WebElement> findShadowElements(String xpath) {
        try {
            Driver.getWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
            return shadow.findElementsByXPath(xpath);
        } catch (TimeoutException e) {
            throw new RuntimeException("Shadow elements not found: " + xpath);
        }
    }

    public String getText() {
        return findElement().getText();
    }

    public String getValue(String attributeName) {
        return findElement().getAttribute(attributeName);
    }

    public void click() {
        WebElement element = findElement();
        actions.moveToElement(element).perform();
        element.click();
    }

    public void enter(String value) {
        WebElement element = findElement();
        actions.moveToElement(element).perform();
        element.sendKeys(value);
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

    public boolean isDisplayed(int timeout) {
        try {
            return findElement(timeout).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void waitForDisplay() {
        Driver.getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public void waitForNotDisplay() {
        Driver.getWait().until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public void moveToElement() {
        WebElement element = findElement();
        actions.moveToElement(element).perform();
    }

    public void setCheckBoxValue(boolean desiredValue) {
        WebElement element = findElement();
        if (element.isSelected() != desiredValue) {
            element.click();
        }
    }

    public String getAttribute(String attribute) {
        try {
            return findElement().getAttribute(attribute);
        } catch (Exception e) {
            System.out.println("Attribute not found " + attribute + " from: " + e.getMessage());
            return null;
        }
    }

    public String getBackgroundColor() {
        String computedStylePropertyScript = "return window.document.defaultView"
                + ".getComputedStyle(arguments[0],null).getPropertyValue(arguments[1]);";
        return ((JavascriptExecutor) Driver.getDriver()).executeScript(
                computedStylePropertyScript, findElement(), "background-color").toString();
    }

    public boolean isToggleSelected() {
        return getBackgroundColor().equals(BackgroundColor.LIGHT_BLUE.getRbg());
    }
}
