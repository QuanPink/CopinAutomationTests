package asia.decentralab.copin.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class Button extends BaseElement {
    public Button(WebDriver driver, By locator) {
        super(driver, locator);
    }

    public void click() {
        wait.until(ExpectedConditions.elementToBeClickable(locator));
        getElement().click();
    }
}
