package asia.decentralab.copin.test;

import asia.decentralab.copin.BaseTest;
import asia.decentralab.copin.data.enumdata.SourceValue;
import asia.decentralab.copin.data.enumdata.StatisticValue;
import asia.decentralab.copin.data.enumdata.TimeValue;
import asia.decentralab.copin.pages.HomePage;
import org.testng.annotations.*;

public class FilterTests extends BaseTest {

    @Test()
    public void tcIDShortSummary(){
        HomePage homePage = new HomePage();
        homePage.filterTraderStatistic(StatisticValue.PNL, TimeValue.DAYS_7, SourceValue.GMX_V2);
    }
}
