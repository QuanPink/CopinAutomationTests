package asia.decentralab.copin.test;

import asia.decentralab.copin.config.Config;
import asia.decentralab.copin.config.Constant;
import asia.decentralab.copin.utils.JsonUtils;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import static asia.decentralab.copin.browser.Driver.*;

public class BaseTest {

    @BeforeSuite
    public void setup() {
        Config config = JsonUtils.readJsonFile(Constant.CONFIG_BROWSER_FILE_PATH, Config.class);
        openBrowser(config);
        navigate(config.getBaseUrl());
    }

    @AfterSuite
    public void tearDown() {
        closeBrowser();
    }
}