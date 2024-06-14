package asia.decentralab.copin.test;

import asia.decentralab.copin.config.Config;
import asia.decentralab.copin.config.Constant;
import asia.decentralab.copin.utils.JsonUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import static asia.decentralab.copin.browser.Driver.*;

public class BaseTest {
    private Config config;

    @BeforeSuite
    public void initConfig() {
        config = JsonUtils.readJsonFile(Constant.CONFIG_BROWSER_FILE_PATH, Config.class);
    }

    @BeforeClass
    public void setup() {
        openBrowser(config);
        navigate(config.getBaseUrl());
    }

    @BeforeClass
    public void tearDown() {
        closeBrowser();
    }
}