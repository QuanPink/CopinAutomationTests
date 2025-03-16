package asia.decentralab.copin.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Input extends BaseElement {
    public Input(WebDriver driver, By locator) {
        super(driver, locator);
    }

    public void type(String text) {
        WebElement element = getElement();
        element.clear();
        element.sendKeys(text);
    }
}