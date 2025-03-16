package asia.decentralab.copin.pages;

import asia.decentralab.copin.browser.WebDriverManager;
import asia.decentralab.copin.config.EnvironmentConfig;
import asia.decentralab.copin.elements.Button;
import asia.decentralab.copin.elements.Input;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class BasePage {
    protected WebDriver driver;
    protected EnvironmentConfig config;

    public BasePage() {
        this.driver = WebDriverManager.getDriver();
        this.config = EnvironmentConfig.getInstance();
    }

    protected Button createButton(By locator) {
        return new Button(driver, locator);
    }

    protected Input createInput(By locator) {
        return new Input(driver, locator);
    }

    public void openUrl(String url) {
        driver.get(url);
    }

    public void refreshPage() {
        driver.navigate().refresh();
    }
}