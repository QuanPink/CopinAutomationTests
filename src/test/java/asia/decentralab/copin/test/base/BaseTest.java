package asia.decentralab.copin.test.base;

import asia.decentralab.copin.browser.WebDriverManager;
import asia.decentralab.copin.config.EnvironmentConfig;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {
    protected EnvironmentConfig config = EnvironmentConfig.getInstance();

    @BeforeMethod
    public void setUp() {
        config.printConfig();

        // Initialize the WebDriver
        WebDriverManager.initDriver();
    }

    @AfterMethod
    public void tearDown() {
        WebDriverManager.quitDriver();
    }
}