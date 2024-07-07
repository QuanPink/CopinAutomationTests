package asia.decentralab.copin.test;

import asia.decentralab.copin.config.Constant;
import asia.decentralab.copin.data.ProtocolData;
import asia.decentralab.copin.pages.HomePage;
import asia.decentralab.copin.utils.APIUtils;
import asia.decentralab.copin.utils.JsonUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class StatisticTraderTests extends BaseTest {
    private HomePage homePage;

    @BeforeClass
    public void setUp() {
        super.setup();
        homePage = new HomePage();

        ProtocolData protocolData = JsonUtils.readJsonFile(Constant.KWENTA_DATA_FILE_PATH, ProtocolData.class);
        System.out.println(protocolData);

        // Gửi yêu cầu API và nhận phản hồi
        String response = APIUtils.sendRequest(protocolData);

        // In ra phản hồi từ API
        System.out.println("API Response: " + response);
    }

    @AfterMethod
    public void afterEachTest() {
        homePage.goToHomePage();
        homePage.clearValueSearch();
    }

    @Test
    public void testStatisticTrader() {

    }
}
