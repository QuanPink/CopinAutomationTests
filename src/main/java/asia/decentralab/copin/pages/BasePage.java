package asia.decentralab.copin.pages;

import asia.decentralab.copin.browser.WebDriverManager;
import asia.decentralab.copin.config.EnvironmentConfig;
import asia.decentralab.copin.elements.BaseElement;
import asia.decentralab.copin.elements.Button;
import asia.decentralab.copin.elements.Input;
import asia.decentralab.copin.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class BasePage {
    protected WebDriver driver;
    protected WaitUtils waitUtils;
    protected EnvironmentConfig config;
    private final By loadingLocator = By.xpath("//div[@id='app_main__wrapper']//div[contains(text(), 'Loading-sc')]");

    public BasePage() {
        this.driver = WebDriverManager.getDriver();
        this.waitUtils = new WaitUtils(driver);
        this.config = EnvironmentConfig.getInstance();
    }

    public void openUrl(String url) {
        driver.get(url);
    }

    public void waitForPageLoad() {
        waitUtils.waitForPageLoad(loadingLocator);
    }

    public void refreshPage() {
        driver.navigate().refresh();
    }

    protected void click(By locator) {
        waitUtils.waitForClickable(locator);
        new Button(driver, locator).click();
    }

    protected void sendKeys(By locator, String text) {
        waitUtils.waitForPresence(locator);
        new Input(driver, locator).type(text);
    }

    protected String getText(By locator) {
        return new BaseElement(driver, locator).getText();
    }

    protected List<String> getTexts(By locator) {
        return new BaseElement(driver, locator).getTexts();
    }
}