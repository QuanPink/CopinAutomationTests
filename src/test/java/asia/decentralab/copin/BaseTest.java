package asia.decentralab.copin;

import asia.decentralab.copin.data.enumdata.BrowserType;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import static asia.decentralab.copin.browser.Driver.*;

public class BaseTest {

    @BeforeClass
    public void setup() {
        openBrowser(BrowserType.CHROME, "latest");
        navigation("https://app.copin.io");
    }

    @AfterClass
    public void tearDown() {
        closeBrowser();
    }
}