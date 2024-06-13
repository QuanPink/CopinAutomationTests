package asia.decentralab.copin.test;

import asia.decentralab.copin.browser.Driver;
import asia.decentralab.copin.config.Constant;
import asia.decentralab.copin.data.ProtocolData;
import asia.decentralab.copin.data.TraderData;
import asia.decentralab.copin.data.enumdata.HttpMethod;
import asia.decentralab.copin.pages.BasePage;
import asia.decentralab.copin.utils.APIUtils;
import asia.decentralab.copin.utils.APIUtils;
import asia.decentralab.copin.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SearchTest extends BaseTest {
//    private TraderData traderData;
//    private BasePage basePage;
//
//    @BeforeClass
//    public void setup() {
//        basePage = new BasePage();
//        traderData = JsonUtils.readJsonFile(Constant.TRADER_FILE_PATH, TraderData.class);
//    }

    @AfterMethod
    public void afterEachTest() {
        Driver.refreshPage();
    }

    @Test(description = "Search valid trader")
    public void tc001SearchValidTrader() {
        BasePage basePage = new BasePage();
        TraderData traderData = JsonUtils.readJsonFile(Constant.TRADER_FILE_PATH, TraderData.class);
        basePage.searchTrader(traderData.validAddress);
        Assert.assertTrue(basePage.isSearchResultValid(traderData.validAddress));
    }

    @Test(description = "Search inValid trader")
    public void tc002SearchInvalidTrader() {
        BasePage basePage = new BasePage();
        TraderData traderData = JsonUtils.readJsonFile(Constant.TRADER_FILE_PATH, TraderData.class);
        basePage.searchTrader(traderData.inValidAddress);
        Assert.assertTrue(basePage.isNoResultsMessageTraderDisplayed());
    }

    @Test(description = "Search valid txHash")
    public void tc003SearchValidTxHash() {
        BasePage basePage = new BasePage();
        TraderData traderData = JsonUtils.readJsonFile(Constant.TRADER_FILE_PATH, TraderData.class);
        basePage.searchTrader(traderData.validTxHash);
        Assert.assertTrue(basePage.isSearchResultTxHash(traderData.validTxHash));
        basePage.goToHomePage();
    }

    @Test(description = "Search inValid txHash")
    public void tc004SearchInvalidTxHash() {
        BasePage basePage = new BasePage();
        TraderData traderData = JsonUtils.readJsonFile(Constant.TRADER_FILE_PATH, TraderData.class);
        basePage.searchTrader(traderData.inValidTxHash);
        Assert.assertTrue(basePage.isNoResultsMessageTxHashDisplayed());
    }

    @Test(description = "Test Call API")
    public void callApi(){
        ProtocolData protocolData = JsonUtils.readJsonFile(Constant.GMX_DATA_FILE_PATH, ProtocolData.class);
        try {
            String response = APIUtils.sendRequest(protocolData);
            System.out.println("API response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
