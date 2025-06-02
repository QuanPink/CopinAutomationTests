package asia.decentralab.copin.elements;

import asia.decentralab.copin.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class Button extends BaseElement {
    public Button(WebDriver driver, By locator) {
        super(driver, locator);
    }

    public void click() {
        waitUtils.waitForClickable(locator);;
        getElement().click();
    }
}
