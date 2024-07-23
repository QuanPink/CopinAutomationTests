package asia.decentralab.copin.test;

import asia.decentralab.copin.config.Constant;
import asia.decentralab.copin.data.ApiRequestData;
import asia.decentralab.copin.model.TraderProtocol;
import asia.decentralab.copin.pages.HomePage;
import asia.decentralab.copin.pages.TraderExplorerPage;
import asia.decentralab.copin.utils.APIUtils;
import asia.decentralab.copin.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class StatisticTraderTests extends BaseTest {
    private HomePage homePage;
    private TraderExplorerPage traderExplorerPage;
    private TraderProtocol kwentaData;

    @BeforeClass
    public void setUp() {
        super.setup();
        homePage = new HomePage();
        traderExplorerPage = new TraderExplorerPage();

        ApiRequestData apiRequestData = JsonUtils.readJsonFile(Constant.KWENTA_DATA_FILE_PATH, ApiRequestData.class);
        String jsonResponse = APIUtils.sendRequest(apiRequestData);
        kwentaData = JsonUtils.fromJson(jsonResponse, TraderProtocol.class);

        homePage.goToTraderExplorerPage();
    }

    @AfterMethod
    public void afterEachTest() {
        homePage.goToTraderExplorerPage();
    }

    @Test(description = "Check trader's statistical information is correct on the Trader Explorer screen")
    public void pmg016TraderStatisticIsCorrectOnTheTraderExplorerScreen() {
        traderExplorerPage.displayAllStatisticsFields();
        Assert.assertTrue(traderExplorerPage.isStatisticTraderDisplayCorrect(kwentaData));
        ;
    }
}
