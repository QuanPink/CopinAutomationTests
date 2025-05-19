package asia.decentralab.copin.test.functional;

import asia.decentralab.copin.pages.HomePage;
import asia.decentralab.copin.test.base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class HomePageTest extends BaseTest {

    @Test(priority = 0)
    @Description("TM_002: Kiểm tra giá trị mặc định của bộ lọc Top trên trang chủ")
    @Severity(SeverityLevel.NORMAL)
    public void testDefaultTopFilters() {
        HomePage homePage = new HomePage();
        homePage.navigateToHomePage();

        String defaultTopFilter = homePage.getTopFilterValue();
        Assert.assertEquals(defaultTopFilter, "PNL",
                "Filter sắp xếp mặc định không phải là 'PNL'");
    }

    @Test(priority = 1)
    @Description("TM_003: Kiểm tra giá trị mặc định các bộ lọc Time trên trang chủ")
    @Severity(SeverityLevel.NORMAL)
    public void testDefaultTimeFilters() {
        HomePage homePage = new HomePage();
        homePage.navigateToHomePage();

        String defaultTimeFilter = homePage.getTimeFilterValue();
        Assert.assertEquals(defaultTimeFilter, "30 DAYS",
                "Filter thời gian mặc định không phải là '30 DAYS'");
    }


    @Test(priority = 2)
    @Description("TM_005: Kiểm tra sort theo PnL hoạt động chính xác")
    @Severity(SeverityLevel.NORMAL)
    public void testFilterByPnL() {
        HomePage homePage = new HomePage();
        homePage.navigateToHomePage();

        homePage.selectPnLFilter();
        Assert.assertTrue(homePage.isPnLSortByDesc(),
                "Top trader theo PnL không được sắp xếp đúng");
    }

    @Test(priority = 3)
    @Description("TM_006: Kiểm tra sort theo Avg ROI hoạt động chính xác")
    @Severity(SeverityLevel.NORMAL)
    public void testFilterByROI() {
        HomePage homePage = new HomePage();
        homePage.navigateToHomePage();

        homePage.selectAvgRoiFilter();
        Assert.assertTrue(homePage.isROISortByDesc(),
                "Top trader theo Avg ROI không được sắp xếp đúng");
    }

    @Test(priority = 4)
    @Description("TM_007: Kiểm tra sort theo Win Rate hoạt động chính xác")
    @Severity(SeverityLevel.NORMAL)
    public void testFilterByWinRate() {
        HomePage homePage = new HomePage();
        homePage.navigateToHomePage();

        homePage.selectWinRateFilter();
        Assert.assertTrue(homePage.isWinRateSortByDesc(),
                "Top trader theo Win Rate không được sắp xếp đúng");
    }

    @DataProvider(name = "timeDays")
    public Object[][] timeDays() {
        return new Object[][] {
                {"7", 7},
                {"14", 14},
                {"30", 30},
                {"60", 60}
        };
    }

    @Test(priority = 5, dataProvider = "timeDays")
    @Description("TM_008: Kiểm tra Time filter hoạt động là chính xác")
    @Severity(SeverityLevel.NORMAL)
    public void testFilterByTime(String filterValue, int expectedDays) {
        HomePage homePage = new HomePage();
        homePage.navigateToHomePage();

        homePage.selectDaysTimeFilter(filterValue);
        Assert.assertTrue(homePage.isFilterByDays(expectedDays),
                "Top trader theo Win Rate không được sắp xếp đúng");
    }
}