package asia.decentralab.copin.elements;

import asia.decentralab.copin.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class BaseElement {
    protected WebDriver driver;
    protected WaitUtils waitUtils;
    protected By locator;

    public BaseElement(WebDriver driver, By locator) {
        this.driver = driver;
        this.locator = locator;
        this.waitUtils = new WaitUtils(driver);
    }

    public WebElement getElement() {
        waitUtils.waitForVisible(locator);
        return driver.findElement(locator);
    }

    public List<WebElement> getElements() {
        waitUtils.waitForVisible(locator);
        return driver.findElements(locator);
    }

    public String getText() {
        return getElement().getText();
    }

    public List<String> getTexts() {
        List<WebElement> elements = getElements();
        return elements.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public boolean isDisplayed() {
        try {
            return getElement().isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean isEnabled() {
        return getElement().isEnabled();
    }

    public String getAttribute(String name) {
        return getElement().getAttribute(name);
    }
}