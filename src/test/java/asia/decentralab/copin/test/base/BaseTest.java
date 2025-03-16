package asia.decentralab.copin.test.base;

import asia.decentralab.copin.browser.WebDriverManager;
import asia.decentralab.copin.config.EnvironmentConfig;
import io.qameta.allure.testng.AllureTestNg;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

@Listeners({AllureTestNg.class})
public class BaseTest {
    protected EnvironmentConfig config = EnvironmentConfig.getInstance();

    @BeforeMethod
    public void setUp() {
        System.out.println("=== Starting test ===");
        config.printConfig();

        // Initialize the WebDriver
        WebDriverManager.initDriver();
    }

    @AfterMethod
    public void tearDown() {
        WebDriverManager.quitDriver();
        System.out.println("=== Test finished ===");
    }
}