package asia.decentralab.copin.test;

import asia.decentralab.copin.config.BaseUrlConfig;
import asia.decentralab.copin.config.endpoints.PositionsByTraderReq;
import asia.decentralab.copin.config.endpoints.TraderStatisticByProtocolReq;
import asia.decentralab.copin.model.PositionStatistics;
import asia.decentralab.copin.model.TraderStatistics;
import asia.decentralab.copin.utils.APIUtils;
import asia.decentralab.copin.utils.JsonUtils;
import io.restassured.response.Response;
import org.apache.commons.lang3.function.TriConsumer;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class TraderStatisticTests {

    @DataProvider(name = "Protocol list")
    public Object[][] protocols() {
        String[] protocols = {"EQUATION_ARB", "GMX", "GMX_V2", "GNS", "HMX_ARB", "LEVEL_ARB", "MUX_ARB", "MYX_ARB",
                "VELA_ARB", "YFX_ARB", "AVANTIS_BASE", "SYNTHETIX_V3", "LOGX_BLAST", "APOLLOX_BNB", "LEVEL_BNB",
                "KTX_MANTLE", "LOGX_MODE", "CYBERDEX", "DEXTORO", "KWENTA", "POLYNOMIAL", "GNS_POLY", "ROLLIE_SCROLL",
                "KILOEX_OPBNB", "MUMMY_FANTOM", "MORPHEX_FANTOM"};
        String[] timeValues = {"D7", "D15", "D30", "D60"};

        Object[][] data = new Object[protocols.length * timeValues.length][2];
        int index = 0;
        for (String protocol : protocols) {
            for (String timeValue : timeValues) {
                data[index++] = new Object[]{protocol, timeValue};
            }
        }
        return data;
    }

    @Test(dataProvider = "Protocol list")
    public void statisticTraderAreCorrect(String protocol, String timeValue) throws Exception {
        TraderStatisticByProtocolReq traderStatisticByProtocolPayload = new TraderStatisticByProtocolReq(
                BaseUrlConfig.PROD_BASE_URL, protocol, timeValue);

        Response responseTraderStatistic = APIUtils.sendPostRequest(
                traderStatisticByProtocolPayload.getBaseUrl(),
                traderStatisticByProtocolPayload.getApiEndpoints().getPath(),
                traderStatisticByProtocolPayload.getApiEndpoints().getRequestDetails());

        TraderStatistics traderStatistics = JsonUtils.fromJson(responseTraderStatistic.getBody().asString(),
                TraderStatistics.class);

        if (traderStatistics.getData() == null || traderStatistics.getData().isEmpty()) {
            throw new Exception("No position data available for protocol: " + protocol);
        }

        Instant now = Instant.now();
        LocalDate today = now.atZone(ZoneOffset.UTC).toLocalDate();
        Instant endTime = today.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant startTime = endTime.minus(getNumberOfDays(timeValue), ChronoUnit.DAYS);

        for (TraderStatistics.TraderStatistic data : traderStatistics.getData()) {
            Stats stats = new Stats();
            String account = data.getAccount();
            PositionsByTraderReq positionByTraderPayload = new PositionsByTraderReq(BaseUrlConfig.PROD_BASE_URL,
                    protocol, account);

            Response responsePositionByTrader = APIUtils.sendPostRequest(
                    positionByTraderPayload.getBaseUrl(),
                    positionByTraderPayload.getApiEndpoints().getPath(),
                    positionByTraderPayload.getApiEndpoints().getRequestDetails());

            PositionStatistics positionStatistics = JsonUtils.fromJson(responsePositionByTrader.getBody().asString(),
                    PositionStatistics.class);

            for (PositionStatistics.Position position : positionStatistics.getData()) {
                updateStats(stats, position, startTime, endTime);
            }
            stats.calculateStats();
            verifyStatistics(stats, data, protocol, timeValue, account);
        }
    }

    private int getNumberOfDays(String timeValue) {
        switch (timeValue) {
            case "D7":
                return 7;
            case "D15":
                return 15;
            case "D30":
                return 30;
            case "D60":
                return 60;
            default:
                return 0;
        }
    }

    private static class Stats {
        int totalTrade = 0;
        int totalOrder = 0;
        double orderPositionRatio = 0;
        double winRate = 0;
        int totalWin = 0;
        int totalLose = 0;
        double totalVolume = 0;
        double avgVolume = 0;
        double totalLeverage = 0;
        double avgLeverage = 0;
        double minLeverage = Double.MAX_VALUE;
        double maxLeverage = 0;
        double realisedPnl = 0;
        double realisedTotalGain = 0;
        double realisedTotalLoss = 0;
        double realisedProfitRate = 0;
        double realisedProfitLossRatio = 0;
        double realisedGainLossRatio = 0;
        double totalFee = 0;
        double totalGainFee = 0;
        double totalLossFee = 0;
        double realisedRoi = 0;
        double realisedAvgRoi = 0;
        double realisedMaxRoi = 0;
        int totalLong = 0;
        double longRate = 0;
        int totalLiquidation = 0;
        double totalLiquidationAmount = 0;
        double totalDuration = 0;
        double avgDuration = 0;
        double minDuration = Double.MAX_VALUE;
        double maxDuration = 0;
        double realisedMaxDrawDown = 0;
        double realisedMaxDrawDownPnl = 0;

        void calculateStats() {
            if (totalTrade != 0) {
                orderPositionRatio = (double) totalOrder / totalTrade;
                winRate = ((double) totalWin / (totalWin + totalLose)) * 100;
                avgVolume = totalVolume / totalTrade;
                avgLeverage = totalLeverage / totalTrade;

                realisedGainLossRatio = realisedTotalLoss == 0 ?
                        realisedTotalGain :
                        realisedTotalGain / Math.abs(realisedTotalLoss);

                realisedProfitRate = (realisedTotalGain + Math.abs(realisedTotalLoss) != 0) ?
                        (realisedTotalGain / (realisedTotalGain + Math.abs(realisedTotalLoss))) * 100 :
                        0;

                realisedProfitLossRatio = (totalWin != 0 && totalLose != 0 && realisedTotalLoss / totalLose != 0) ?
                        (realisedTotalGain / totalWin) / (realisedTotalLoss / totalLose) :
                        0;

                realisedAvgRoi = realisedRoi / totalTrade;

                longRate = (totalLong / (double) totalTrade) * 100;
                avgDuration = totalDuration / totalTrade;
            }
        }
    }

    private void updateStats(Stats stats, PositionStatistics.Position position, Instant startTime, Instant endTime) {
        Instant closeBlockTime = Instant.parse(position.getCloseBlockTime());
        if (!closeBlockTime.isBefore(startTime) && closeBlockTime.isBefore(endTime)) {
            stats.totalTrade++;
            stats.totalOrder += position.getOrderCount();
            stats.totalWin += position.isWin() ? 1 : 0;
            stats.totalLose += position.isWin() ? 0 : 1;
            stats.totalVolume += position.getSize();
            stats.totalLeverage += position.getLeverage();
            stats.maxLeverage = Math.max(stats.maxLeverage, position.getLeverage());
            stats.minLeverage = Math.min(stats.minLeverage, position.getLeverage());
            stats.realisedPnl += position.getRealisedPnl();
            stats.realisedTotalGain += position.getRealisedPnl() > 0 ? position.getRealisedPnl() : 0;
            stats.realisedTotalLoss += position.getRealisedPnl() <= 0 ? position.getRealisedPnl() : 0;
            stats.totalGainFee += position.getRealisedPnl() > 0 ? position.getFee() : 0;
            stats.totalLossFee += position.getRealisedPnl() <= 0 ? position.getFee() : 0;
            stats.realisedRoi += position.getRealisedRoi();
            stats.realisedMaxRoi = Math.max(stats.realisedMaxRoi, position.getRealisedRoi());
            stats.totalLong += position.isLong() ? 1 : 0;
            stats.totalLiquidation += position.isLiquidate() && !position.isWin() ? 1 : 0;
            stats.totalLiquidationAmount += position.isLiquidate() && !position.isWin() ? position.getRealisedPnl() : 0;
            stats.totalFee += position.getFee();
            stats.totalDuration += position.getDurationInSecond();
            stats.maxDuration = Math.max(stats.maxDuration, position.getDurationInSecond());
            stats.minDuration = Math.min(stats.minDuration, position.getDurationInSecond());
            stats.realisedMaxDrawDown = Math.min(stats.realisedMaxDrawDown, position.getRealisedRoi());
            stats.realisedMaxDrawDownPnl = Math.min(stats.realisedMaxDrawDownPnl, position.getRealisedPnl());
        }
    }

    private void verifyStatistics(Stats stats, TraderStatistics.TraderStatistic data, String protocol, String timeValue, String account) {
        double equivalentFactor = 0.02;
        String errorMessage = timeValue + "-" + protocol + "-" + account;

        // Helper functions
        TriConsumer<Number, Number, String> assertEqual = (expected, actual, fieldName) ->
                Assert.assertEquals(expected.doubleValue(), actual.doubleValue(), equivalentFactor,
                        String.format("%s mismatch: %s { %s / %s }", fieldName, errorMessage, expected, actual));

        TriConsumer<Number, Number, String> assertNonNegative = (value, expected, fieldName) -> {
            Assert.assertTrue(value.doubleValue() >= 0,
                    String.format("%s less than 0: %s { %s / %s }", fieldName, errorMessage, value, expected));
        };

        TriConsumer<Double, Double, String> assertWithFactor = (expected, actual, fieldName) ->
                Assert.assertEquals(expected, actual, Math.abs(expected * equivalentFactor),
                        String.format("%s mismatch: %s { %s / %s }", fieldName, errorMessage, expected, actual));

        // Assert fields with direct equals
        assertEqual.accept(stats.totalTrade, data.getTotalTrade(), "Total Trade");
        assertNonNegative.accept(stats.totalTrade, data.getTotalTrade(), "Total Trade");

        assertEqual.accept(stats.orderPositionRatio, data.getOrderPositionRatio(), "Order Position Ratio");
        assertNonNegative.accept(stats.orderPositionRatio, data.getOrderPositionRatio(), "Order Position Ratio");

        assertEqual.accept(stats.totalWin, data.getTotalWin(), "Total Win");
        assertNonNegative.accept(stats.totalWin, data.getTotalWin(), "Total Win");

        assertEqual.accept(stats.totalLose, data.getTotalLose(), "Total Lose");
        assertNonNegative.accept(stats.totalLose, data.getTotalLose(), "Total Lose");

        assertEqual.accept(stats.winRate, data.getWinRate(), "Win Rate");
        assertNonNegative.accept(stats.winRate, data.getWinRate(), "Win Rate");

        // Assert fields with tolerance using factor
        assertWithFactor.accept(stats.totalVolume, data.getTotalVolume(), "Total Volume");
        assertNonNegative.accept(stats.totalVolume, data.getTotalVolume(), "Total Volume");

        assertWithFactor.accept(stats.avgVolume, data.getAvgVolume(), "Avg Volume");
        assertNonNegative.accept(stats.avgVolume, data.getAvgVolume(), "Avg Volume");

        assertWithFactor.accept(stats.realisedPnl, data.getRealisedPnl(), "Realised Pnl");

        assertWithFactor.accept(stats.realisedTotalGain, data.getRealisedTotalGain(), "Realised Total Gain");
        assertNonNegative.accept(stats.realisedTotalGain, data.getRealisedTotalGain(), "Realised Total Gain");

        assertEqual.accept(stats.realisedTotalLoss, data.getRealisedTotalLoss(), "Realised Total Loss");
        assertNonNegative.accept(-stats.realisedTotalLoss, -data.getRealisedTotalLoss(), "Realised Total Loss");

        assertWithFactor.accept(stats.realisedProfitRate, data.getRealisedProfitRate(), "Realised Profit Rate");
        assertNonNegative.accept(stats.realisedProfitRate, data.getRealisedProfitRate(), "Realised Profit Rate");

        assertWithFactor.accept(stats.realisedProfitLossRatio, data.getRealisedProfitLossRatio(), "Realised Profit Loss Ratio");

        assertWithFactor.accept(stats.realisedGainLossRatio, data.getRealisedGainLossRatio(), "Realised Gain Loss Ratio");
        assertNonNegative.accept(stats.realisedGainLossRatio, data.getRealisedGainLossRatio(), "Realised Gain Loss Ratio");

        assertWithFactor.accept(stats.realisedAvgRoi, data.getRealisedAvgRoi(), "Realised Avg Roi");
        assertEqual.accept(stats.realisedMaxRoi, data.getRealisedMaxRoi(), "Realised Max Roi");

        assertEqual.accept(stats.longRate, data.getLongRate(), "Long Rate");
        assertNonNegative.accept(stats.longRate, data.getLongRate(), "Long Rate");

        assertEqual.accept(stats.totalLiquidation, data.getTotalLiquidation(), "Total Liquidation");
        assertNonNegative.accept(stats.totalLiquidation, data.getTotalLiquidation(), "Total Liquidation");

        assertWithFactor.accept(stats.totalFee, data.getTotalFee(), "Total Fee");
        assertNonNegative.accept(stats.totalFee, data.getTotalFee(), "Total Fee");

        assertWithFactor.accept(stats.avgDuration, data.getAvgDuration(), "Avg Duration");
        assertNonNegative.accept(stats.avgDuration, data.getAvgDuration(), "Avg Duration");

        assertEqual.accept(stats.minDuration, data.getMinDuration(), "Min Duration");
        assertNonNegative.accept(stats.minDuration, data.getMinDuration(), "Min Duration");

        assertEqual.accept(stats.maxDuration, data.getMaxDuration(), "Max Duration");

        assertWithFactor.accept(stats.realisedMaxDrawDown, data.getRealisedMaxDrawdown(), "Realised Max Draw Down");
        assertWithFactor.accept(stats.realisedMaxDrawDownPnl, data.getRealisedMaxDrawdownPnl(), "Realised Max Draw Down Pnl");

        // Conditional checks for protocols
        if (!(protocol.equals("SYNTHETIX_V3") || protocol.equals("HMX_ARB"))) {
            assertWithFactor.accept(stats.avgLeverage, data.getAvgLeverage(), "Avg Leverage");
            assertNonNegative.accept(stats.avgLeverage, data.getAvgLeverage(), "Avg Leverage");

            assertEqual.accept(stats.minLeverage, data.getMinLeverage(), "Min Leverage");
            assertNonNegative.accept(stats.minLeverage, data.getMinLeverage(), "Min Leverage");

            assertEqual.accept(stats.maxLeverage, data.getMaxLeverage(), "Max Leverage");
        }
    }
}
