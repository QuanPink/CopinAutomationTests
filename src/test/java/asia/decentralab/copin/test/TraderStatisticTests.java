package asia.decentralab.copin.test;

import asia.decentralab.copin.config.BaseUrlConfig;
import asia.decentralab.copin.config.endpoints.PositionsByTraderReq;
import asia.decentralab.copin.config.endpoints.TraderStatisticByProtocolReq;
import asia.decentralab.copin.model.PositionStatistics;
import asia.decentralab.copin.model.TraderStatistics;
import asia.decentralab.copin.utils.APIUtils;
import asia.decentralab.copin.utils.JsonUtils;
import io.restassured.response.Response;
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
                "KTX_MANTLE", "LOGX_MODE", "CYBERDEX", "DEXTORO", "KWENTA", "POLYNOMIAL", "GNS_POLY"};
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

        Assert.assertEquals(stats.totalTrade, data.getTotalTrade(), "Total trade incorrect: " + errorMessage);
        Assert.assertTrue(stats.totalTrade >= 0, "Total trade less than 0: " + errorMessage);

        Assert.assertEquals(stats.orderPositionRatio, data.getOrderPositionRatio(), 0.01,
                "Order Position Ratio incorrect: " + errorMessage);
        Assert.assertTrue(stats.orderPositionRatio >= 0,
                "Order Position Ratio less than 0: " + errorMessage);

        Assert.assertEquals(stats.totalWin, data.getTotalWin(), "Total Win incorrect: " + errorMessage);
        Assert.assertTrue(stats.totalWin >= 0, "Total Win less than 0: " + errorMessage);

        Assert.assertEquals(stats.totalLose, data.getTotalLose(), "Total Lose incorrect: " + errorMessage);
        Assert.assertTrue(stats.totalLose >= 0, "Total Lose less than 0: " + errorMessage);

        Assert.assertEquals(stats.winRate, data.getWinRate(), 0.01, "Win rate incorrect: " + errorMessage);
        Assert.assertTrue(stats.winRate >= 0, "Win rate less than 0: " + errorMessage);

        Assert.assertEquals(stats.totalVolume, data.getTotalVolume(), stats.totalVolume * equivalentFactor,
                "Total volume incorrect: " + errorMessage);
        Assert.assertTrue(stats.totalVolume >= 0, "Total volume less than 0: " + errorMessage);

        Assert.assertEquals(stats.avgVolume, data.getAvgVolume(), stats.avgVolume * equivalentFactor,
                "Avg volume incorrect: " + errorMessage);
        Assert.assertTrue(stats.avgVolume >= 0, "Avg volume less than 0: " + errorMessage);

        Assert.assertEquals(stats.realisedPnl, data.getRealisedPnl(), Math.abs(stats.realisedPnl * equivalentFactor),
                "Realised Pnl incorrect: " + errorMessage);

        Assert.assertEquals(stats.realisedTotalGain, data.getRealisedTotalGain(), stats.realisedTotalGain * equivalentFactor,
                "Realised Total Gain incorrect: " + errorMessage);
        Assert.assertTrue(stats.realisedTotalGain >= 0, "Realised Total Gain less than 0: " + errorMessage);

        Assert.assertEquals(stats.realisedTotalLoss, data.getRealisedTotalLoss(), 0.01,
                "Realised Total Loss incorrect: " + errorMessage);
        Assert.assertTrue(stats.realisedTotalLoss <= 0, "Realised Total Loss great than 0: " + errorMessage);

        Assert.assertEquals(stats.realisedProfitRate, data.getRealisedProfitRate(), stats.realisedProfitRate * equivalentFactor,
                "Realised Profit Rate incorrect: " + errorMessage);
        Assert.assertTrue(stats.realisedProfitRate >= 0, "Realised Profit Rate less than 0: " + errorMessage);

        Assert.assertEquals(stats.realisedProfitLossRatio, data.getRealisedProfitLossRatio(),
                Math.abs(stats.realisedProfitLossRatio * equivalentFactor),
                "Realised Profit Loss Ratio incorrect: " + errorMessage);

        Assert.assertEquals(stats.realisedGainLossRatio, data.getRealisedGainLossRatio(),
                Math.abs(stats.realisedGainLossRatio * equivalentFactor),
                "Realised Gain Loss Ratio incorrect: " + errorMessage);
        Assert.assertTrue(stats.realisedGainLossRatio >= 0,
                "Realised Gain Loss Ratio less than 0: " + errorMessage);

        Assert.assertEquals(stats.realisedAvgRoi, data.getRealisedAvgRoi(), Math.abs(stats.realisedAvgRoi * equivalentFactor),
                "Realised Avg Roi incorrect: " + errorMessage);

        Assert.assertEquals(stats.realisedMaxRoi, data.getRealisedMaxRoi(),
                "Realised Max Roi incorrect: " + errorMessage);

        Assert.assertEquals(stats.longRate, data.getLongRate(), "Long Rate incorrect: " + errorMessage);
        Assert.assertTrue(stats.longRate >= 0, "Long Rate less than 0: " + errorMessage);

        Assert.assertEquals(stats.totalLiquidation, data.getTotalLiquidation(),
                "Total Liquidation incorrect: " + errorMessage);
        Assert.assertTrue(stats.totalLiquidation >= 0, "Total Liquidation less than 0: " + errorMessage);

        Assert.assertEquals(stats.totalFee, data.getTotalFee(), stats.totalFee * equivalentFactor,
                "Total Fee incorrect: " + errorMessage);
        Assert.assertTrue(stats.totalFee >= 0, "Total Fee less than 0: " + errorMessage);

        Assert.assertEquals(stats.avgDuration, data.getAvgDuration(), stats.avgDuration * equivalentFactor,
                "Avg Duration incorrect: " + errorMessage);
        Assert.assertTrue(stats.avgDuration >= 0, "Avg Duration less than 0: " + errorMessage);

        Assert.assertEquals(stats.minDuration, data.getMinDuration(), "Min Duration incorrect: " + errorMessage);
        Assert.assertTrue(stats.minDuration >= 0,
                "Min Duration less than 0: " + stats.minDuration + errorMessage);

        Assert.assertEquals(stats.maxDuration, data.getMaxDuration(), "Max Duration incorrect: " + errorMessage);

        Assert.assertEquals(stats.realisedMaxDrawDown, data.getRealisedMaxDrawdown(),
                Math.abs(stats.realisedMaxDrawDown * equivalentFactor),
                "Realised Max Draw Down incorrect: " + errorMessage);

        Assert.assertEquals(stats.realisedMaxDrawDownPnl, data.getRealisedMaxDrawdownPnl(),
                Math.abs(stats.realisedMaxDrawDownPnl * equivalentFactor),
                "Realised Max Draw Down Pnl incorrect: " + errorMessage);

        if (!(protocol.equals("SYNTHETIX_V3") || protocol.equals("HMX_ARB"))) {
            Assert.assertEquals(stats.avgLeverage, data.getAvgLeverage(), stats.avgLeverage * equivalentFactor,
                    "Avg leverage incorrect: " + errorMessage);
            Assert.assertTrue(stats.avgLeverage >= 0, "Avg leverage less than 0: " + errorMessage);

            Assert.assertEquals(stats.minLeverage, data.getMinLeverage(), "Min leverage incorrect: " + errorMessage);
            Assert.assertTrue(stats.minLeverage >= 0, "Min leverage less than 0: " + errorMessage);

            Assert.assertEquals(stats.maxLeverage, data.getMaxLeverage(), "Max leverage incorrect: " + errorMessage);
        }
    }
}
