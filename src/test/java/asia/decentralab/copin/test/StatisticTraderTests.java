package asia.decentralab.copin.test;

import asia.decentralab.copin.data.TraderStatisticsRequest;
import asia.decentralab.copin.model.TraderProtocol;
import asia.decentralab.copin.pages.HomePage;
import asia.decentralab.copin.pages.TraderExplorerPage;
import asia.decentralab.copin.utils.APIUtils;
import asia.decentralab.copin.utils.JsonUtils;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class StatisticTraderTests extends BaseTest {
    private HomePage homePage;
    private TraderExplorerPage traderExplorerPage;
    private TraderProtocol kwentaStatisticData;

    @BeforeClass
    public void setUp() {
        super.setup();
        homePage = new HomePage();
        traderExplorerPage = new TraderExplorerPage();

        TraderStatisticsRequest requestPayload = new TraderStatisticsRequest(
                "https://api.copin.io", "KWENTA", "D30");

        Response response = APIUtils.sendPostRequest(
                requestPayload.getBaseUrl(),
                requestPayload.getApiEndpoints().getPath(),
                requestPayload.getApiEndpoints().getRequestDetails());
        kwentaStatisticData = JsonUtils.fromJson(response.getBody().asString(), TraderProtocol.class);

        homePage.goToTraderExplorerPage();
    }

    @AfterMethod
    public void afterEachTest() {
        homePage.goToTraderExplorerPage();
    }

    @Test(description = "Check trader's statistical information is correct on the Trader Explorer screen")
    public void pmg016TraderStatisticIsCorrectOnTheTraderExplorerScreen() {
        traderExplorerPage.displayAllStatisticsFields();
        Assert.assertTrue(traderExplorerPage.isStatisticTraderDisplayCorrect(kwentaStatisticData));
        ;
    }
}
