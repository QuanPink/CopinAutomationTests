package asia.decentralab.copin.test;

import asia.decentralab.copin.config.BaseUrlConfig;
import asia.decentralab.copin.config.endpoints.TraderStatisticByProtocolReq;
import asia.decentralab.copin.data.enumdata.SourceValue;
import asia.decentralab.copin.data.enumdata.TimeValue;
import asia.decentralab.copin.model.TraderStatistics;
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
    private TraderStatistics kwentaStatisticData;

    @BeforeClass
    public void setUp() {
        super.setup();
        homePage.goToTraderExplorerPage();
    }

    @AfterMethod
    public void afterEachTest() {
        homePage.goToTraderExplorerPage();
    }

    @Test(description = "Check trader's statistical information is correct on the Trader Explorer screen")
    public void pmg016TraderStatisticIsCorrectOnTheTraderExplorerScreen() {
        homePage = new HomePage();
        traderExplorerPage = new TraderExplorerPage();

        TraderStatisticByProtocolReq traderStatisticByProtocolPayload = new TraderStatisticByProtocolReq(
                BaseUrlConfig.PROD_BASE_URL, SourceValue.GNS_API.getValue(), TimeValue.DAYS_30_API.getValue());

        Response response = APIUtils.sendPostRequest(
                traderStatisticByProtocolPayload.getBaseUrl(),
                traderStatisticByProtocolPayload.getApiEndpoints().getPath(),
                traderStatisticByProtocolPayload.getApiEndpoints().getRequestDetails());
        kwentaStatisticData = JsonUtils.fromJson(response.getBody().asString(), TraderStatistics.class);

        traderExplorerPage.displayAllStatisticsFields();
        Assert.assertTrue(traderExplorerPage.isStatisticTraderDisplayCorrect(kwentaStatisticData));
    }
}
