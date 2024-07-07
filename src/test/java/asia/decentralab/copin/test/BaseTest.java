package asia.decentralab.copin.test;

import asia.decentralab.copin.config.Config;
import asia.decentralab.copin.config.Constant;
import asia.decentralab.copin.utils.JsonUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import static asia.decentralab.copin.browser.Driver.*;

public class BaseTest {

    @BeforeClass
    public void setup() {
        Config config = JsonUtils.readJsonFile(Constant.CONFIG_BROWSER_FILE_PATH, Config.class);
        openBrowser(config);
        closeWindowExtensions(2);
        navigate(config.getBaseUrl());
    }

    @AfterClass
    public void tearDown() {
        closeBrowser();
    }
}
