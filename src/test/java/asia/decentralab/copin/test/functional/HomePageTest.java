package asia.decentralab.copin.test.functional;

import asia.decentralab.copin.pages.HomePage;
import asia.decentralab.copin.test.base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HomePageTest extends BaseTest {

    @Test(priority = 0)
    @Description("Kiểm tra giá trị mặc định của các bộ lọc trên trang chủ")
    @Severity(SeverityLevel.NORMAL)
    public void testDefaultFilters() {
        HomePage homePage = new HomePage();

        homePage.navigateToHomePage();

        String defaultSortFilter = homePage.getActiveSortFilterValue();
        Assert.assertEquals(defaultSortFilter, "PNL", "Filter sắp xếp mặc định không phải là 'PNL'");

        String defaultTimeFilter = homePage.getTimeFilterValue();
        Assert.assertEquals(defaultTimeFilter, "30 DAYS", "Filter thời gian mặc định không phải là '30 DAYS'");
    }
}