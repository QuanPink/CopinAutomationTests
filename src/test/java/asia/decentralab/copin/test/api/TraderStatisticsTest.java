package asia.decentralab.copin.test.api;

import asia.decentralab.copin.models.TraderStatisticCalculationResult;
import asia.decentralab.copin.test.base.BaseApiTest;
import asia.decentralab.copin.utils.ValidationUtils;
import asia.decentralab.copin.utils.calculators.TraderStatisticCalculator;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static asia.decentralab.copin.utils.ValidationUtils.getDoubleValue;
import static asia.decentralab.copin.utils.ValidationUtils.getIntValue;

public class TraderStatisticsTest extends BaseApiTest {
    private static final double TOLERANCE = 0.01;
    private final TraderStatisticCalculator calculator = new TraderStatisticCalculator();

    @DataProvider(name = "protocolList")
    public Object[][] protocols() {
        String[] protocols = {"APOLLOX_BNB", "APOLLOX_BASE", "AVANTIS_BASE", "BMX_BASE", "DYDX", "ELFI_ARB",
                "FULCROM_CRONOS", "GMX_SOL", "GMX_V2", "GMX_V2_AVAX", "GNS", "GNS_BASE", "GNS_APE", "GNS_POLY",
                "HMX_ARB", "JUPITER", "KILOEX_OPBNB", "KILOEX_BNB", "KILOEX_BASE", "KILOEX_MANTA", "MUX_ARB",
                "OSTIUM_ARB", "POLYNOMIAL_L2"};
        String[] timeValues = {"D1", "D7", "D15", "D30", "D60"};
        Object[][] data = new Object[protocols.length * timeValues.length][2];
        int index = 0;
        for (String protocol : protocols) {
            for (String timeValue : timeValues) {
                data[index++] = new Object[]{protocol, timeValue};
            }
        }
        return data;
    }

    @Test(dataProvider = "protocolList")
    public void traderStatisticTest(String protocol, String timeValue) {
        Response traderStatisticResponse = traderStatisticApiClient.getTraderStatisticByProtocol(
                protocol, timeValue, "/position/statistic-v2/filter"
        );

        List<Map<String, Object>> traderStatistics = traderStatisticResponse.jsonPath().getList("data");

        ValidationUtils.assertNotNull(traderStatistics,
                "Response should contain 'data' field");

        ValidationUtils.assertTrue(!traderStatistics.isEmpty(),
                String.format("Trader statistics should not be empty for protocol: %s, timeValue: %s",
                        protocol, timeValue));

        for (Map<String, Object> traderStatistic : traderStatistics) {
            testSingleTraderStatistic(protocol, traderStatistic, timeValue);
        }
    }

    private void testSingleTraderStatistic(String protocol,
                                           Map<String, Object> expectedStatistic,
                                           String timeValue) {
        String account = (String) expectedStatistic.get("account");

        // Get all positions for this account
        Response positionResponse = positionApiClient
                .getPositionsByAccountAndProtocol(protocol, account, "/position/filter");

        List<Map<String, Object>> positions = positionResponse
                .jsonPath().getList("data");

        if (positions == null || positions.isEmpty()) {
            return;
        }

        // Calculate statistics
        TraderStatisticCalculationResult calculatedStats = calculator.calculationTraderStatistic(positions, timeValue);

        // Assert metrics
        assertMetrics(calculatedStats, expectedStatistic, account);
    }

    private void assertMetrics(TraderStatisticCalculationResult actual,
                               Map<String, Object> expected,
                               String account) {

        // Exact matches
        ValidationUtils.assertInRange(actual.totalTrade, 0, 100_000, "totalTrade ");
        ValidationUtils.assertEquals(actual.totalTrade, getIntValue(expected, "totalTrade"),
                "Total trades mismatch for " + account);

        ValidationUtils.assertInRange(actual.totalWin, 0, 100_000, "totalWin");
        ValidationUtils.assertEquals(actual.totalWin, getIntValue(expected, "totalWin"),
                "Total wins mismatch for " + account);

        ValidationUtils.assertInRange(actual.totalLose, 0, 100_000, "totalLose");
        ValidationUtils.assertEquals(actual.totalLose, getIntValue(expected, "totalLose"),
                "Total losses mismatch for " + account);

        ValidationUtils.assertInRange(actual.totalLiquidation, 0, 100_000, "totalLiquidation");
        ValidationUtils.assertEquals(actual.totalLiquidation, getIntValue(expected, "totalLiquidation"),
                "Total liquidations mismatch for " + account);

        // Tolerance-based assertions
        ValidationUtils.assertInRange(actual.winRate, 0, 100, "winRate");
        ValidationUtils.assertCloseToValue(actual.winRate, getDoubleValue(expected, "winRate"),
                TOLERANCE, "Win rate " + account);

        ValidationUtils.assertCloseToValue(actual.orderPositionRatio, 0, 100, "winRate");
        ValidationUtils.assertCloseToValue(actual.orderPositionRatio, getDoubleValue(expected, "orderPositionRatio"),
                TOLERANCE, "Order position ratio " + account);

        ValidationUtils.assertInRange(actual.totalVolume, 0, 10_000_000_000L, "totalVolume");
        ValidationUtils.assertCloseToValue(actual.totalVolume, getDoubleValue(expected, "totalVolume"),
                getEquivalentTolerance(actual.totalVolume), "Total volume " + account);

        ValidationUtils.assertInRange(actual.totalLongVolume, 0, 10_000_000_000L, "totalLongVolume");
        ValidationUtils.assertCloseToValue(actual.totalLongVolume, getDoubleValue(expected, "totalLongVolume"),
                getEquivalentTolerance(actual.totalLongVolume), "Total Long volume " + account);

        ValidationUtils.assertInRange(actual.totalShortVolume, 0, 10_000_000_000L, "totalShortVolume");
        ValidationUtils.assertCloseToValue(actual.totalShortVolume, getDoubleValue(expected, "totalShortVolume"),
                getEquivalentTolerance(actual.totalShortVolume), "Total Short volume " + account);

        ValidationUtils.assertInRange(actual.avgVolume, 0, 10_000_000_000L, "avgVolume");
        ValidationUtils.assertCloseToValue(actual.avgVolume, getDoubleValue(expected, "avgVolume"),
                getEquivalentTolerance(actual.avgVolume), "Average volume " + account);

        ValidationUtils.assertInRange(actual.avgLeverage, 0, 10_000, "avgLeverage");
        ValidationUtils.assertCloseToValue(actual.avgLeverage, getDoubleValue(expected, "avgLeverage"),
                TOLERANCE, "Average leverage " + account);

        ValidationUtils.assertInRange(actual.minLeverage, 0, 10_000, "minLeverage");
        ValidationUtils.assertCloseToValue(actual.minLeverage, getDoubleValue(expected, "minLeverage"),
                TOLERANCE, "Min leverage " + account);
        ValidationUtils.assertInRange(actual.maxLeverage, actual.minLeverage, 10_000, "maxLeverage");
        ValidationUtils.assertCloseToValue(actual.maxLeverage, getDoubleValue(expected, "maxLeverage"),
                TOLERANCE, "Max leverage " + account);

        ValidationUtils.assertInRange(actual.longRate, 0, 100, "maxLeverage");
        ValidationUtils.assertCloseToValue(actual.longRate, getDoubleValue(expected, "longRate"),
                TOLERANCE, "Long rate " + account);

        ValidationUtils.assertInRange(actual.realisedPnl, -1_000_000_000, 1_000_000_000, "realisedPnl");
        ValidationUtils.assertCloseToValue(actual.realisedPnl, getDoubleValue(expected, "realisedPnl"),
                getEquivalentTolerance(actual.realisedPnl), "Realised PnL " + account);

        ValidationUtils.assertInRange(actual.realisedLongPnl, -1_000_000_000, 1_000_000_000, "realisedLongPnl");
        ValidationUtils.assertCloseToValue(actual.realisedLongPnl, getDoubleValue(expected, "realisedLongPnl"),
                getEquivalentTolerance(actual.realisedLongPnl), "Realised Long PnL " + account);

        ValidationUtils.assertInRange(actual.realisedShortPnl, -1_000_000_000, 1_000_000_000,  "realisedShortPnl");
        ValidationUtils.assertCloseToValue(actual.realisedShortPnl, getDoubleValue(expected, "realisedShortPnl"),
                getEquivalentTolerance(actual.realisedShortPnl), "Realised short PnL " + account);

        ValidationUtils.assertInRange(actual.realisedTotalGain, 0, 1_000_000_000,  "realisedTotalGain");
        ValidationUtils.assertCloseToValue(actual.realisedTotalGain, getDoubleValue(expected, "realisedTotalGain"),
                getEquivalentTolerance(actual.realisedTotalGain), "Total gain " + account);

        ValidationUtils.assertInRange(actual.realisedTotalLoss, -1_000_000_000, 0,  "realisedTotalLoss");
        ValidationUtils.assertCloseToValue(actual.realisedTotalLoss, getDoubleValue(expected, "realisedTotalLoss"),
                getEquivalentTolerance(actual.realisedTotalLoss), "Total loss " + account);

        ValidationUtils.assertInRange(actual.realisedProfitRate, -100, 100, "realisedProfitRate");
        ValidationUtils.assertCloseToValue(actual.realisedProfitRate, getDoubleValue(expected, "realisedProfitRate"),
                getEquivalentTolerance(actual.realisedProfitRate), "Realised Profit rate " + account);

        ValidationUtils.assertInRange(actual.realisedProfitLossRatio, -100, 100, "realisedProfitLossRatio");
        ValidationUtils.assertCloseToValue(actual.realisedProfitLossRatio, getDoubleValue(expected, "realisedProfitLossRatio"),
                getEquivalentTolerance(actual.realisedProfitLossRatio), "Realised Profit loss ratio " + account);

        ValidationUtils.assertInRange(actual.realisedGainLossRatio, -100, 100, "realisedGainLossRatio");
        ValidationUtils.assertCloseToValue(actual.realisedGainLossRatio, getDoubleValue(expected, "realisedGainLossRatio"),
                getEquivalentTolerance(actual.realisedGainLossRatio), "Realised Gain loss ratio " + account);

        ValidationUtils.assertInRange(actual.realisedMaxDrawdown, -1000, 0, "realisedMaxDrawdown");
        ValidationUtils.assertCloseToValue(actual.realisedMaxDrawdown, getDoubleValue(expected, "realisedMaxDrawdown"),
                getEquivalentTolerance(actual.realisedMaxDrawdown), "Realised Max Drawdown " + account);

        ValidationUtils.assertInRange(actual.realisedMaxDrawdownPnl, -1_000_000_000, 0, "realisedMaxDrawdownPnl");
        ValidationUtils.assertCloseToValue(actual.realisedMaxDrawdownPnl, getDoubleValue(expected, "realisedMaxDrawdownPnl"),
                getEquivalentTolerance(actual.realisedMaxDrawdownPnl), "Realised Max Drawdown Pnl " + account);

        ValidationUtils.assertInRange(actual.totalFee, -1_000_000, 1_000_000, "totalFee");
        ValidationUtils.assertCloseToValue(actual.totalFee, getDoubleValue(expected, "totalFee"),
                getEquivalentTolerance(actual.totalFee), "Total fee " + account);

        ValidationUtils.assertInRange(actual.avgDuration, actual.minDuration, 126_230_400_000L, "avgDuration");
        ValidationUtils.assertCloseToValue(actual.avgDuration, getDoubleValue(expected, "avgDuration"),
                getEquivalentTolerance(actual.avgDuration), "Average duration " + account);

        ValidationUtils.assertInRange(actual.maxLeverage, actual.minDuration, 126_230_400_000L, "maxLeverage");
        ValidationUtils.assertCloseToValue(actual.maxDuration, getDoubleValue(expected, "maxDuration"),
                getEquivalentTolerance(actual.maxDuration), "Max duration " + account);

        ValidationUtils.assertInRange(actual.minDuration, 0, 126_230_400_000L, "minDuration");
        ValidationUtils.assertCloseToValue(actual.minDuration, getDoubleValue(expected, "minDuration"),
                getEquivalentTolerance(actual.minDuration), "Min duration " + account);

        // Streak assertions
        ValidationUtils.assertInRange(actual.winStreak, 0, 1_000, "winStreak");
        ValidationUtils.assertEquals(actual.winStreak, getIntValue(expected, "winStreak"),
                "Win streak mismatch for " + account);

        ValidationUtils.assertInRange(actual.loseStreak, 0, 1_000, "loseStreak");
        ValidationUtils.assertEquals(actual.loseStreak, getIntValue(expected, "loseStreak"),
                "Lose streak mismatch for " + account);

        ValidationUtils.assertInRange(actual.maxWinStreak, 0, 1_000, "maxWinStreak");
        ValidationUtils.assertEquals(actual.maxWinStreak, getIntValue(expected, "maxWinStreak"),
                "Max win streak mismatch for " + account);

        ValidationUtils.assertInRange(actual.maxLoseStreak,  0, 1_000, "maxLoseStreak");
        ValidationUtils.assertEquals(actual.maxLoseStreak, getIntValue(expected, "maxLoseStreak"),
                "Max lose streak mismatch for " + account);

        // Advanced ratios (continued)
        if (actual.totalTrade > 1) {
            ValidationUtils.assertCloseToValue(actual.realisedSharpeRatio, 0, 100, "realisedSharpeRatio");
            ValidationUtils.assertCloseToValue(actual.realisedSharpeRatio, getDoubleValue(expected, "realisedSharpeRatio"),
                    TOLERANCE, "Realised sharpe ratio");

            ValidationUtils.assertInRange(actual.realisedSortinoRatio, 0,100,  "realisedSortinoRatio");
            ValidationUtils.assertCloseToValue(actual.realisedSortinoRatio, getDoubleValue(expected, "realisedSortinoRatio"),
                    TOLERANCE, "Realised sortino ratio");

            ValidationUtils.assertCloseToValue(actual.sharpeRatio, 0, 100, "sharpeRatio");
            ValidationUtils.assertCloseToValue(actual.sharpeRatio, getDoubleValue(expected, "sharpeRatio"),
                    TOLERANCE, "Sharpe ratio");

            ValidationUtils.assertInRange(actual.sortinoRatio, 0,100,  "sortinoRatio");
            ValidationUtils.assertCloseToValue(actual.sortinoRatio, getDoubleValue(expected, "sortinoRatio"),
                    TOLERANCE, "Sortino ratio");
        }

        // Labels assertion
        List<String> expectedRealisedLabels = (List<String>) expected.get("realisedStatisticLabels");
        assertLabels(actual.realisedStatisticLabels, expectedRealisedLabels, account);

        List<String> expectedLabels = (List<String>) expected.get("statisticLabels");
        assertLabels(actual.statisticLabels, expectedLabels, account);
    }

    private void assertLabels(List<String> actual, List<String> expected, String account) {
        Collections.sort(actual);
        Collections.sort(expected);
        ValidationUtils.assertEquals(actual, expected,
                String.format("Labels mismatch for %s", account));
    }

    private double getEquivalentTolerance(double value) {
        return TOLERANCE * Math.abs(value);
    }
}
