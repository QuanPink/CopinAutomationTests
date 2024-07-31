package asia.decentralab.copin.pages;

import asia.decentralab.copin.data.enumdata.StatisticValue;
import asia.decentralab.copin.element.Element;
import asia.decentralab.copin.model.TraderProtocol;
import asia.decentralab.copin.utils.NumberFormatter;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class TraderExplorerPage extends BasePage {
    /* Page element */
    private final Element customizeColumnButton = new Element(By.xpath(
            "//div[contains(@class, 'ListTradersSection__TabletWrapper')]//button//div[text()='Customize Column']"));
    private final Element statisticFieldCheckbox = new Element(By.xpath(
            "//div[contains(@class,'Dropdown__Menu')]//input[@type='checkbox']"));
    private final Element pageButton = new Element(By.xpath("(//div[contains(@class, 'ListTradersSection__TabletWrapper')]//button)[3]"));
    private final Element loadingIcon = new Element(By.xpath("//div[@id='trader-table']//div[contains(@class,'Loading-sc')]"));

    /* Dynamic element*/
    private final String pageLimitButton = "";
    private final String dynamicStatisticItem = "//div[not(@id='trader-table')]/table//th[.//div[text()='%s']]";
    private final String dynamicTraderItem = String.format(
            "//div[@id='trader-table']/table//tr//a[contains(@href,'%%s')]/ancestor::tr/td[count(%s/preceding-sibling::th)+1]",
            dynamicStatisticItem);

    @Step("Show all statistics fields")
    public void displayAllStatisticsFields() {
        customizeColumnButton.click();
        List<WebElement> statisticFields = statisticFieldCheckbox.findElements();
        for (WebElement statisticField : statisticFields) {
            if (!statisticField.isSelected()) {
                statisticField.click();
            }
        }
        customizeColumnButton.click();
    }

    @Step("Verify all statistics are accurate")
    public boolean isStatisticTraderDisplayCorrect(TraderProtocol protocol) {
        int numberTrader = 0;
        for (TraderProtocol.TraderStatistic data : protocol.getData()) {
            if (numberTrader == 20) {
                pageButton.click();
                numberTrader = 0;
                loadingIcon.waitForNotDisplay();
            }

            for (StatisticValue statisticValue : StatisticValue.values()) {
                if (statisticValue.equals(StatisticValue.MARKETS) || statisticValue.equals(StatisticValue.IGNORE)) {
                    continue;
                }

                String value = getStatisticValue(data, statisticValue);
                String textValue;

                if (value.equals("a minute ago")) {
                    textValue = "1";
                } else if (value.equals("a hour ago")) {
                    textValue = "1";
                } else if (value.equals("a day ago")) {
                    textValue = "1";
                } else if (value.equals("a month ago")) {
                    textValue = "1";
                } else if (value.equals("a year ago")) {
                    textValue = "1";
                } else {
                    textValue = value;
                }

                String expectedTextValue;
                if (isDurationStatisticCorrect(statisticValue)) {
                    expectedTextValue = (String) getExpectedStatisticValue(data, statisticValue);
                } else {
                    Number numberValue = statisticValue == StatisticValue.L_S_RATE
                            ? NumberFormatter.extractNumberFromString(textValue, 0)
                            : NumberFormatter.parseStringToNumber(textValue);
                    Number expectedValue = (Number) getExpectedStatisticValue(data, statisticValue);

                    if (numberValue.doubleValue() != expectedValue.doubleValue()) {
                        return false;
                    }
                    continue;
                }

                if (!textValue.replace(",", "").equals(expectedTextValue)) {
                    return false;
                }
            }
            numberTrader++;
        }
        return true;
    }

    private Object getExpectedStatisticValue(TraderProtocol.TraderStatistic data, StatisticValue statisticValue) {
        switch (statisticValue) {
            case RUNTIME_ALL:
                return data.getRunTimeDays();
            case LAST_TRADE:
                return (double) Math.abs(NumberFormatter.calculateTimeBetweenNowAndTimestamp(data.getLastTradeAtTs()));
            case PNL:
                return getRoundedValue(data.getRealisedPnl());
            case TOTAL_GAIN:
                return getRoundedValue(data.getRealisedTotalGain());
            case TOTAL_LOSS:
                return getRoundedValue(data.getRealisedTotalLoss());
            case TOTAL_PAID_FEES:
                return getRoundedValue(data.getTotalFee());
            case TOTAL_VOLUME:
                return getRoundedValue(data.getTotalVolume());
            case AVG_VOLUME:
                return getRoundedValue(data.getAvgVolume());
            case AVG_ROI:
                return NumberFormatter.roundToDecimalPlaces(data.getRealisedAvgRoi(), 2);
            case MAX_ROI:
                return NumberFormatter.roundToDecimalPlaces(data.getRealisedMaxRoi(), 2);
            case TRADES:
                return data.getTotalTrade();
            case WINS:
                return data.getTotalWin();
            case LOSES:
                return data.getTotalLose();
            case LIQUIDATIONS:
                return data.getTotalLiquidation();
            case WIN_RATE:
                return NumberFormatter.roundToDecimalPlaces(data.getWinRate(), 2);
            case PROFIT_RATE:
                return NumberFormatter.roundToDecimalPlaces(data.getRealisedProfitRate(), 2);
            case L_S_RATE:
                return NumberFormatter.roundToDecimalPlaces(data.getLongRate(), 0);
            case ORDER_POS_RATIO:
                return NumberFormatter.roundToDecimalPlaces(data.getOrderPositionRatio(), 1);
            case PNL_RATIO:
                return NumberFormatter.roundToDecimalPlaces(data.getRealisedProfitLossRatio(), 1);
            case PROFIT_FACTOR:
                return NumberFormatter.roundToDecimalPlaces(data.getRealisedGainLossRatio(), 1);
            case AVG_LEVERAGE:
                int number = Math.abs(data.getAvgLeverage()) < 0.1 ? 2
                        : 1;
                return NumberFormatter.roundToDecimalPlaces(data.getAvgLeverage(), number);
            case MAX_LEVERAGE:
                int number1 = Math.abs(data.getMaxLeverage()) < 0.1 ? 2
                        : 1;
                return NumberFormatter.roundToDecimalPlaces(data.getMaxLeverage(), number1);
            case MIN_LEVERAGE:
                int number2 = Math.abs(data.getMinLeverage()) < 0.1 ? 2
                        : 1;
                return NumberFormatter.roundToDecimalPlaces(data.getMinLeverage(), number2);
            case AVG_DURATION:
                return NumberFormatter.convertSecondsToHumanReadable(data.getAvgDuration());
            case MAX_DURATION:
                return NumberFormatter.convertSecondsToHumanReadable(data.getMaxDuration());
            case MIN_DURATION:
                return NumberFormatter.convertSecondsToHumanReadable(data.getMinDuration());
            case MAX_DRAW_DOWN:
                return NumberFormatter.roundToDecimalPlaces(data.getRealisedMaxDrawdown(), 2);
            case MAX_DRAW_DOWN_PNL:
                return getRoundedValue(data.getRealisedMaxDrawdownPnl());
            default:
                throw new IllegalArgumentException("Unexpected statistic value: " + statisticValue);
        }
    }

    private String getStatisticValue(TraderProtocol.TraderStatistic data, StatisticValue statisticValue) {
        String currentStatistic = statisticValue.getValue();
        Element element = new Element(By.xpath(String.format(dynamicStatisticItem, currentStatistic)));
        element.moveToElement();

        if (element.isDisplayed()) {
            Element statisticField = new Element(By.xpath(String.format(dynamicTraderItem, data.getAccount(), currentStatistic)));
            return statisticField.getText();
        }
        throw new RuntimeException(currentStatistic + " not displayed");
    }

    private Number getRoundedValue(double value) {
        int decimalPlaces = Math.abs(value) < 0.01 ? 5
                : Math.abs(value) < 1 ? 2
                : 0;
        return NumberFormatter.roundToDecimalPlaces(value, decimalPlaces);
    }

    private boolean isDurationStatisticCorrect(StatisticValue statisticValue) {
        return statisticValue == StatisticValue.AVG_DURATION
                || statisticValue == StatisticValue.MIN_DURATION
                || statisticValue == StatisticValue.MAX_DURATION;
    }
}
