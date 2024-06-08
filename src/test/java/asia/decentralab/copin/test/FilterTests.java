package asia.decentralab.copin.test;

import asia.decentralab.copin.BaseTest;
import asia.decentralab.copin.data.ProtocolData;
import asia.decentralab.copin.data.enumdata.SourceValue;
import asia.decentralab.copin.data.enumdata.StatisticValue;
import asia.decentralab.copin.data.enumdata.TimeValue;
import asia.decentralab.copin.pages.HomePage;
import asia.decentralab.copin.utils.APIUtils;
import org.testng.annotations.*;

import java.io.IOException;

public class FilterTests extends BaseTest {

    @Test()
    public void tcIDShortSummary(){
        HomePage homePage = new HomePage();
        APIUtils apiUtils = new APIUtils();
        ProtocolData.ApiConfig gmxApiConfig = new ProtocolData.GMXApiConfig();

        homePage.filterTraderStatistic(StatisticValue.PNL, TimeValue.DAYS_7, SourceValue.GMX_V2);

        try {
            String response = apiUtils.callApi(
                    gmxApiConfig.getUrl(),
                    gmxApiConfig.getMethod(),
                    gmxApiConfig.getHeaders(),
                    gmxApiConfig.getBody()
            );

            System.out.println(response);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
