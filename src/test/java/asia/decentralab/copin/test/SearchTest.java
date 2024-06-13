package asia.decentralab.copin.test;

import asia.decentralab.copin.config.Constant;
import asia.decentralab.copin.data.ProtocolData;
import asia.decentralab.copin.data.TraderData;
import asia.decentralab.copin.pages.HomePage;
import asia.decentralab.copin.utils.APIUtils;
import asia.decentralab.copin.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SearchTest extends BaseTest {
    private HomePage homePage;
    private TraderData traderData;

    @BeforeClass
    public void setup() {
        super.setup();
        homePage = new HomePage();
        traderData = JsonUtils.readJsonFile(Constant.TRADER_FILE_PATH, TraderData.class);
    }

//    @AfterMethod
//    public void afterEachTest() {
//        homePage.goToHomePage();
//    }

    @Test(description = "Search valid trader")
    public void tc001SearchValidTrader() {
        homePage.searchTrader(traderData.validAddress);
        Assert.assertTrue(homePage.isSearchResultValid(traderData.validAddress));
    }

//    @Test(description = "Search inValid trader")
//    public void tc002SearchInvalidTrader() {
//        homePage.searchTrader(traderData.inValidAddress);
//        Assert.assertTrue(homePage.isNoResultsMessageTraderDisplayed());
//    }
//
//    @Test(description = "Search valid txHash")
//    public void tc003SearchValidTxHash() {
//        homePage.searchTrader(traderData.validTxHash);
//        Assert.assertTrue(homePage.isSearchResultTxHash(traderData.validTxHash));
//    }
//
//    @Test(description = "Search inValid txHash")
//    public void tc004SearchInvalidTxHash() {
//        homePage.searchTrader(traderData.inValidTxHash);
//        Assert.assertTrue(homePage.isNoResultsMessageTxHashDisplayed());
//    }
//
//    @Test(description = "Test Call API")
//    public void callApi(){
//        ProtocolData protocolData = JsonUtils.readJsonFile(Constant.GMX_DATA_FILE_PATH, ProtocolData.class);
//        try {
//            String response = APIUtils.sendRequest(protocolData);
//            System.out.println("API response: " + response);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
