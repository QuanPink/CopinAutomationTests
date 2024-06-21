package asia.decentralab.copin.test;

import asia.decentralab.copin.config.Constant;
import asia.decentralab.copin.model.Positions;
import asia.decentralab.copin.model.Traders;
import asia.decentralab.copin.pages.HomePage;
import asia.decentralab.copin.pages.PositionListPage;
import asia.decentralab.copin.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SearchTest extends BaseTest {
    private HomePage homePage;
    private PositionListPage positionListPage;
    private Traders.Trader gmxTrader;
    private Traders.Trader invalidTrader;
    private Positions.Position closedPosition;
    private Positions.Position invalidPosition;

    @BeforeClass
    public void setup() {
        super.setup();
        homePage = new HomePage();
        positionListPage = new PositionListPage();

        Traders traders = JsonUtils.readJsonFile(Constant.TRADERS_FILE_PATH, Traders.class);
        gmxTrader = traders.getGmxTrader();
        invalidTrader = traders.getInvalidTrader();

        Positions positions = JsonUtils.readJsonFile(Constant.POSITIONS_FILE_PATH, Positions.class);
        closedPosition = positions.getClosePosition();
        invalidPosition = positions.getInvalidPosition();
    }

    @AfterMethod
    public void afterEachTest() {
        homePage.goToHomePage();
        homePage.clearValueSearch();
    }

    @Test(description = "Check search results are correct when user search with valid address")
    public void tc001SearchValidTrader() {
        homePage.searchTrader(gmxTrader.getAddress());
        Assert.assertTrue(homePage.isNumberSearchResultsCorrect());
        Assert.assertTrue(homePage.isTraderSearchResultsCorrect(gmxTrader.getAddress()));
    }

    @Test(description = "Check message displayed are correct when user search with invalid address")
    public void tc002SearchInvalidTrader() {
        homePage.searchTrader(invalidTrader.getAddress());
        Assert.assertTrue(homePage.isMessageTraderNotFoundDisplay());
    }

    @Test(description = "Check search results are correct when user search with valid txHash")
    public void tc003SearchValidTxHash() {
        homePage.searchTrader(closedPosition.getTxHash());
        Assert.assertTrue(homePage.isNumberSearchResultsCorrect());
        homePage.viewAllResultSearch();
        Assert.assertTrue(positionListPage.isTxHashSearchResultsCorrect(closedPosition.getTxHash()));
    }

    @Test(description = "Check message displayed are correct when user search with invalid txHash")
    public void tc004SearchInvalidTxHash() {
        homePage.searchTrader(invalidPosition.getTxHash());
        Assert.assertTrue(homePage.isMessageTxHashNotFoundDisplay());
        Assert.assertTrue(false);
    }
}
