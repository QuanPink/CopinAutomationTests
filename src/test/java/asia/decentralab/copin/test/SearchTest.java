package asia.decentralab.copin.test;

import asia.decentralab.copin.config.Constant;
import asia.decentralab.copin.model.Positions;
import asia.decentralab.copin.model.Traders;
import asia.decentralab.copin.pages.HomePage;
import asia.decentralab.copin.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SearchTest extends BaseTest {
    private HomePage homePage;
    private Traders traders;
    Positions positions;
    private Positions.Position closedPosition;

    @BeforeClass
    public void setup() {
        super.setup();
        homePage = new HomePage();
        traders = JsonUtils.readJsonFile(Constant.TRADERS_FILE_PATH, Traders.class);
        positions = JsonUtils.readJsonFile(Constant.POSITIONS_FILE_PATH, Positions.class);
        closedPosition = positions.getClosePosition();
    }

    @AfterMethod
    public void afterEachTest() {
        homePage.goToHomePage();
        homePage.clearValueSearch();
    }

    @Test(description = "Search valid trader")
    public void tc001SearchValidTrader() {
        homePage.searchTrader(traders.getTraders().getAddress().getValidAddress());
        Assert.assertTrue(homePage.isNumberSearchResult());
        Assert.assertTrue(homePage.isSearchResultValid(traders.getTraders().getAddress().getValidAddress()));
    }

    @Test(description = "Search inValid trader")
    public void tc002SearchInvalidTrader() {
        homePage.searchTrader(traders.getTraders().getAddress().getUpperCaseAddress());
        Assert.assertTrue(homePage.isNoResultsMessageTraderDisplayed());
    }

    @Test(description = "Search valid txHash")
    public void tc003SearchValidTxHash() {
        homePage.searchTrader(closedPosition.getTxHash());
        Assert.assertTrue(homePage.isNumberSearchResult());
        homePage.viewAllResultSearch();
        Assert.assertTrue(homePage.isSearchResultTxHash(closedPosition.getTxHash()));
    }

    @Test(description = "Search inValid txHash")
    public void tc004SearchInvalidTxHash() {
        homePage.searchTrader(closedPosition.getInvalidTxHash());
        Assert.assertTrue(homePage.isNoResultsMessageTxHashDisplayed());
    }
}
