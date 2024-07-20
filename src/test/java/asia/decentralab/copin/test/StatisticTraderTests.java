package asia.decentralab.copin.test;

import asia.decentralab.copin.config.Constant;
import asia.decentralab.copin.data.ProtocolData;
import asia.decentralab.copin.model.Protocol;
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
    private Protocol protocol;

    @BeforeClass
    public void setUp() {
        super.setup();
        homePage = new HomePage();
        traderExplorerPage = new TraderExplorerPage();

        ProtocolData protocolData = JsonUtils.readJsonFile(Constant.KWENTA_DATA_FILE_PATH, ProtocolData.class);
        String jsonResponse = APIUtils.sendRequest(protocolData);
        protocol = JsonUtils.fromJson(jsonResponse, Protocol.class);

        homePage.goToTraderExplorerPage();
    }

    @AfterMethod
    public void afterEachTest() {
        homePage.goToTraderExplorerPage();
    }

    @Test(description = "Check trader's statistical information is correct on the Trader Explorer screen")
    public void pmg016TraderStatisticIsCorrectOnTheTraderExplorerScreen() {
        traderExplorerPage.displayAllStatisticsFields();
        Assert.assertTrue(traderExplorerPage.isStatisticTraderDisplayCorrect(protocol));
        ;
    }
}
