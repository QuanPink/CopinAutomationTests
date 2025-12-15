package asia.decentralab.copin.test.api;

import asia.decentralab.copin.models.TraderStatisticCalculationResult;
import asia.decentralab.copin.test.base.BaseApiTest;
import asia.decentralab.copin.utils.calculators.TraderStatisticCalculator;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static asia.decentralab.copin.utils.MapUtils.getDouble;
import static asia.decentralab.copin.utils.MapUtils.getInt;
import static asia.decentralab.copin.utils.ValidationUtils.*;

public class TraderStatisticsTest extends BaseApiTest {
    private static final double TOLERANCE = 0.01;
    private final TraderStatisticCalculator calculator = new TraderStatisticCalculator();

    @DataProvider(name = "protocolList", parallel = true)
    public Object[][] protocols() {
        String[] protocols = {"APOLLOX_BNB"};
        String[] timeValues = {"D1"};
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

        assertNotNull(traderStatistics,
                "Response should contain 'data' field");

        assertTrue(!traderStatistics.isEmpty(),
                String.format("Trader statistics should not be empty for protocol: %s, timeValue: %s",
                        protocol, timeValue));

        for (Map<String, Object> traderStatistic : traderStatistics) {
            testSingleTraderStatistic(protocol, traderStatistic, timeValue);
        }
    }

    private static final Set<String> SKIP_LEVERAGE_PROTOCOLS = Set.of("DYDX", "POLYNOMIAL_L2", "HMX_ARB");

    private void testSingleTraderStatistic(String protocol, Map<String, Object> expectedStatistic, String timeValue) {
        String account = (String) expectedStatistic.get("account");

        // Get all positions for this account
        Response positionResponse = positionApiClient
                .getPositionsByAccountAndProtocol(protocol, account, "/position/filter");

        List<Map<String, Object>> positions = positionResponse.jsonPath().getList("data");

        assertNotNull(positions,
                String.format("Positions data should not be null for account: %s, protocol: %s", account, protocol));

        assertTrue(!positions.isEmpty(),
                String.format("Positions should not be empty for account: %s, protocol: %s", account, protocol));

        // Calculate statistics
        TraderStatisticCalculationResult calculatedStats = calculator.calculationTraderStatistic(positions, timeValue);

        // Assert metrics
        assertMetrics(protocol, calculatedStats, expectedStatistic, account);
    }

    private void assertMetrics(String protocol, TraderStatisticCalculationResult expected, Map<String, Object> actual,
                               String account) {

        // Exact matches
        assertInRange(expected.totalTrade, 0, 100_000, "totalTrade ", account);
        assertEquals(expected.totalTrade, getInt(actual, "totalTrade"),
                "Total trades", account);

        assertInRange(expected.totalWin, 0, 100_000, "totalWin", account);
        assertEquals(expected.totalWin, getInt(actual, "totalWin"),
                "Total wins", account);

        assertInRange(expected.totalLose, 0, 100_000, "totalLose", account);
        assertEquals(expected.totalLose, getInt(actual, "totalLose"),
                "Total losses", account);

        assertInRange(expected.totalLiquidation, 0, 100_000, "totalLiquidation", account);
        assertEquals(expected.totalLiquidation, getInt(actual, "totalLiquidation"),
                "Total liquidations", account);

        // Tolerance-based assertions
        assertInRange(expected.winRate, 0, 100, "winRate", account);
        assertCloseToValue(expected.winRate, getDouble(actual, "winRate"),
                TOLERANCE, "Win rate ", account);

        assertInRange(expected.orderPositionRatio, 0, 9000, "orderPositionRatio", account);
        assertCloseToValue(expected.orderPositionRatio, getDouble(actual, "orderPositionRatio"),
                TOLERANCE, "Order position ratio " , account);

        assertInRange(expected.totalVolume, 0, 10_000_000_000L, "totalVolume", account);
        assertCloseToValue(expected.totalVolume, getDouble(actual, "totalVolume"),
                getEquivalentTolerance(expected.totalVolume), "Total volume " , account);

        assertInRange(expected.totalLongVolume, 0, 10_000_000_000L, "totalLongVolume", account);
        assertCloseToValue(expected.totalLongVolume, getDouble(actual, "totalLongVolume"),
                getEquivalentTolerance(expected.totalLongVolume), "Total Long volume " , account);

        assertInRange(expected.totalShortVolume, 0, 10_000_000_000L, "totalShortVolume", account);
        assertCloseToValue(expected.totalShortVolume, getDouble(actual, "totalShortVolume"),
                getEquivalentTolerance(expected.totalShortVolume), "Total Short volume " , account);

        assertInRange(expected.avgVolume, 0, 10_000_000_000L, "avgVolume", account);
        assertCloseToValue(expected.avgVolume, getDouble(actual, "avgVolume"),
                getEquivalentTolerance(expected.avgVolume), "Average volume " , account);

        if (!SKIP_LEVERAGE_PROTOCOLS.contains(protocol)) {
            assertInRange(expected.avgLeverage, 0, 10_000, "avgLeverage", account);
            assertCloseToValue(expected.avgLeverage, getDouble(actual, "avgLeverage"),
                    TOLERANCE, "Average leverage " , account);

            assertInRange(expected.minLeverage, 0, 10_000, "minLeverage", account);
            assertCloseToValue(expected.minLeverage, getDouble(actual, "minLeverage"),
                    TOLERANCE, "Min leverage " , account);
            assertInRange(expected.maxLeverage, expected.minLeverage, 10_000, "maxLeverage", account);
            assertCloseToValue(expected.maxLeverage, getDouble(actual, "maxLeverage"),
                    TOLERANCE, "Max leverage " , account);
        }

        assertInRange(expected.longRate, 0, 100, "longRate", account);
        assertCloseToValue(expected.longRate, getDouble(actual, "longRate"),
                TOLERANCE, "Long rate " , account);

        assertInRange(expected.realisedPnl, -1_000_000_000, 1_000_000_000, "realisedPnl", account);
        assertCloseToValue(expected.realisedPnl, getDouble(actual, "realisedPnl"),
                getEquivalentTolerance(expected.realisedPnl), "Realised PnL " , account);

        assertInRange(expected.realisedLongPnl, -1_000_000_000, 1_000_000_000, "realisedLongPnl", account);
        assertCloseToValue(expected.realisedLongPnl, getDouble(actual, "realisedLongPnl"),
                getEquivalentTolerance(expected.realisedLongPnl), "Realised Long PnL " , account);

        assertInRange(expected.realisedShortPnl, -1_000_000_000, 1_000_000_000, "realisedShortPnl", account);
        assertCloseToValue(expected.realisedShortPnl, getDouble(actual, "realisedShortPnl"),
                getEquivalentTolerance(expected.realisedShortPnl), "Realised short PnL " , account);

        assertInRange(expected.realisedTotalGain, 0, 1_000_000_000, "realisedTotalGain", account);
        assertCloseToValue(expected.realisedTotalGain, getDouble(actual, "realisedTotalGain"),
                getEquivalentTolerance(expected.realisedTotalGain), "Total gain " , account);

        assertInRange(expected.realisedTotalLoss, -1_000_000_000, 0, "realisedTotalLoss", account);
        assertCloseToValue(expected.realisedTotalLoss, getDouble(actual, "realisedTotalLoss"),
                getEquivalentTolerance(expected.realisedTotalLoss), "Total loss " , account);

        assertInRange(expected.realisedProfitRate, -100, 100, "realisedProfitRate", account);
        assertCloseToValue(expected.realisedProfitRate, getDouble(actual, "realisedProfitRate"),
                getEquivalentTolerance(expected.realisedProfitRate), "Realised Profit rate " , account);

        assertInRange(expected.realisedProfitLossRatio, 0, 1_000_000_000, "realisedProfitLossRatio", account);
        assertCloseToValue(expected.realisedProfitLossRatio, getDouble(actual, "realisedProfitLossRatio"),
                getEquivalentTolerance(expected.realisedProfitLossRatio), "Realised Profit loss ratio " , account);

        assertInRange(expected.realisedGainLossRatio, 0, 1_000_000_000, "realisedGainLossRatio", account);
        assertCloseToValue(expected.realisedGainLossRatio, getDouble(actual, "realisedGainLossRatio"),
                getEquivalentTolerance(expected.realisedGainLossRatio), "Realised Gain loss ratio " , account);

        assertInRange(expected.realisedMaxDrawdown, -1000, 0, "realisedMaxDrawdown", account);
        assertCloseToValue(expected.realisedMaxDrawdown, getDouble(actual, "realisedMaxDrawdown"),
                getEquivalentTolerance(expected.realisedMaxDrawdown), "Realised Max Drawdown " , account);

        assertInRange(expected.realisedMaxDrawdownPnl, -1_000_000_000, 0, "realisedMaxDrawdownPnl", account);
        assertCloseToValue(expected.realisedMaxDrawdownPnl, getDouble(actual, "realisedMaxDrawdownPnl"),
                getEquivalentTolerance(expected.realisedMaxDrawdownPnl), "Realised Max Drawdown Pnl " , account);

        assertInRange(expected.totalFee, -10_000_000, 10_000_000, "totalFee", account);
        assertCloseToValue(expected.totalFee, getDouble(actual, "totalFee"),
                getEquivalentTolerance(expected.totalFee), "Total fee " , account);

        assertInRange(expected.avgDuration, expected.minDuration, 126_230_400_000L, "avgDuration", account);
        assertCloseToValue(expected.avgDuration, getDouble(actual, "avgDuration"),
                getEquivalentTolerance(expected.avgDuration), "Average duration " , account);

        assertInRange(expected.maxDuration, expected.minDuration, 126_230_400_000L, "maxDuration", account);
        assertCloseToValue(expected.maxDuration, getDouble(actual, "maxDuration"),
                getEquivalentTolerance(expected.maxDuration), "Max duration " , account);

        assertInRange(expected.minDuration, 0, 126_230_400_000L, "minDuration", account);
        assertCloseToValue(expected.minDuration, getDouble(actual, "minDuration"),
                getEquivalentTolerance(expected.minDuration), "Min duration " , account);

        // Streak assertions
        assertInRange(expected.maxWinStreak, 0, 1_000, "maxWinStreak", account);
        assertEquals(expected.maxWinStreak, getInt(actual, "maxWinStreak"),
                "Max win streak", account);

        assertInRange(expected.maxLoseStreak, 0, 1_000, "maxLoseStreak", account);
        assertEquals(expected.maxLoseStreak, getInt(actual, "maxLoseStreak"),
                "Max lose streak", account);

        assertInRange(expected.winStreak, 0, 1_000, "winStreak", account);
        assertEquals(expected.winStreak, getInt(actual, "winStreak"),
                "Win streak", account);

        assertInRange(expected.loseStreak, 0, 1_000, "loseStreak", account);
        assertEquals(expected.loseStreak, getInt(actual, "loseStreak"),
                "Lose streak", account);

        // Advanced ratios (continued)
        if (expected.totalTrade > 1) {
            assertInRange(expected.realisedSharpeRatio, -100, 100, "realisedSharpeRatio", account);
            assertCloseToValue(expected.realisedSharpeRatio, getDouble(actual, "realisedSharpeRatio"),
                    TOLERANCE, "Realised sharpe ratio", account);

            assertInRange(expected.realisedSortinoRatio, -100, 100, "realisedSortinoRatio", account);
            assertCloseToValue(expected.realisedSortinoRatio, getDouble(actual, "realisedSortinoRatio"),
                    TOLERANCE, "Realised sortino ratio", account);

            assertInRange(expected.sharpeRatio, -100, 100, "sharpeRatio", account);
            assertCloseToValue(expected.sharpeRatio, getDouble(actual, "sharpeRatio"),
                    TOLERANCE, "Sharpe ratio", account);

            assertInRange(expected.sortinoRatio, -100, 100, "sortinoRatio", account);
            assertCloseToValue(expected.sortinoRatio, getDouble(actual, "sortinoRatio"),
                    TOLERANCE, "Sortino ratio", account);
        }

        // Labels assertion
        List<String> expectedRealisedLabels = (List<String>) actual.get("realisedStatisticLabels");
        assertLabels(expected.realisedStatisticLabels, expectedRealisedLabels, "Realised Labels", account);

        List<String> expectedLabels = (List<String>) actual.get("statisticLabels");
        assertLabels(expected.statisticLabels, expectedLabels,"Labels", account);
    }

    private void assertLabels(List<String> expected, List<String> actual, String fieldName, String account) {
        Collections.sort(actual);
        Collections.sort(expected);
        assertEquals(expected, actual,
                String.format("%s mismatch. Expected: %s, Actual: %s, Account: %s", fieldName, expected, actual, account));
    }

    private double getEquivalentTolerance(double value) {
        return TOLERANCE * Math.abs(value);
    }
}
