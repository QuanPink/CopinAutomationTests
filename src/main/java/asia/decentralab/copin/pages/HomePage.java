package asia.decentralab.copin.pages;

import asia.decentralab.copin.utils.TextUtils;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends BasePage {
    private final By topFilterButton = By.xpath("//div[text()='TOP']/following-sibling::div//button");
    private final By timeFilterButton = By.xpath("//div[text()='IN']/following-sibling::div//button");
    private final By sourceFilterButton = By.xpath("//div[text()='SOURCE']/following-sibling::div//button");
    private final By pnlButton = By.xpath("//button[contains(@class, 'Dropdown__DropdownItem')]//div[text()='PnL']");
    private final By avgRoiButton = By.xpath("//button[contains(@class, 'Dropdown__DropdownItem')]//div[text()='Avg ROI']");
    private final By winRateButton = By.xpath("//button[contains(@class, 'Dropdown__DropdownItem')]//div[text()='Win Rate']");
    private final By pnlValues = By.xpath("//div[contains(@class, 'trader-pnl')]/span");
    private final By roiValues = By.xpath("//div[contains(., 'Avg ROI') and contains(@class, 'jngCCf')]/following-sibling::div/span");
    private final By winRateValues = By.xpath("//div[(text() = 'Win/Trades')]/following-sibling::div");
    private static final String dynamicDaysButton = "//button[contains(@class, 'Dropdown__DropdownItem')]//div[text()='%s days']";
    private final By pnlTimeLabel = By.xpath("//div[contains(@class, 'trader-pnl')]/preceding-sibling::div");

    public void navigateToHomePage() {
        String baseUrl = config.getWebBaseUrl();
        openUrl(baseUrl);
    }

    public String getTopFilterValue() {
        return getText(topFilterButton);
    }

    public String getSourceFilterValue() {
        return getText(sourceFilterButton);
    }

    public void selectPnLFilter() {
        click(topFilterButton);
        click(pnlButton);
    }

    public void selectAvgRoiFilter() {
        click(topFilterButton);
        click(avgRoiButton);
    }

    public void selectWinRateFilter() {
        click(topFilterButton);
        click(winRateButton);
    }

    public String getTimeFilterValue() {
        return getText(timeFilterButton);
    }

    public void selectDaysTimeFilter(String days) {
        click(timeFilterButton);
        String xpathExpression = String.format(dynamicDaysButton, days);
        click(By.xpath(xpathExpression));
    }


    public boolean isPnLSortByDesc() {
        return isDescendingOrder(getNumericValues(pnlValues));
    }

    public boolean isROISortByDesc() {
        return isDescendingOrder(getNumericValues(roiValues));
    }

    public boolean isWinRateSortByDesc() {
        List<String> winRateValueList = getTexts(winRateValues);
        List<Double> numericValues = new ArrayList<>();

        for (String winRateValue : winRateValueList) {
            numericValues.add(TextUtils.extractPercentageValue(winRateValue));
        }

        return isDescendingOrder(numericValues);
    }

    public boolean isFilterByDays(int expectedDays) {
        List<String> timeList = getTexts(pnlTimeLabel);

        for (String timeValue : timeList) {
            try {
                double days = TextUtils.extractNumericValue(timeValue);
                if (days != expectedDays) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return true;
    }

    public List<Double> getNumericValues(By locator) {
        List<String> valueList = getTexts(locator);
        List<Double> numericValues = new ArrayList<>();

        for (String value : valueList) {
            numericValues.add(TextUtils.extractNumericValue(value));
        }
        return numericValues;
    }

    private boolean isDescendingOrder(List<Double> values) {
        for (int i = 0; i < values.size() - 1; i++) {
            if (values.get(i) < values.get(i + 1)) {
                return false;
            }
        }
        return true;
    }
}