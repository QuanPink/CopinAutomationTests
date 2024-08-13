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
        return new Object[][]{{"EQUATION_ARB"}, {"GMX"}, {"GMX_V2"}, {"GNS"}, {"HMX_ARB"}, {"LEVEL_ARB"}, {"MUX_ARB"},
                {"MYX_ARB"}, {"VELA_ARB"}, {"YFX_ARB"}, {"AVANTIS_BASE"}, {"SYNTHETIX_V3"}, {"LOGX_BLAST"},
                {"APOLLOX_BNB"}, {"LEVEL_BNB"}, {"KTX_MANTLE"}, {"LOGX_MODE"}, {"CYBERDEX"}, {"DEXTORO"}, {"KWENTA"},
                {"POLYNOMIAL"}, {"GNS_POLY"}};
    }

    @Test(dataProvider = "Protocol list")
    public void statisticTraderAreCorrect(String protocol) throws Exception {
        String[] timeValues = {"D7", "D15", "D30", "D60"};
        for (String timeValue : timeValues) {
            TraderStatisticByProtocolReq traderStatisticByProtocolPayload = new TraderStatisticByProtocolReq(
                    BaseUrlConfig.PROD_BASE_URL, protocol, timeValue);

            Response responseTraderStatistic = APIUtils.sendPostRequest(
                    traderStatisticByProtocolPayload.getBaseUrl(),
                    traderStatisticByProtocolPayload.getApiEndpoints().getPath(),
                    traderStatisticByProtocolPayload.getApiEndpoints().getRequestDetails());

            TraderStatistics traderStatistics = JsonUtils.fromJson(responseTraderStatistic.getBody().asString(), TraderStatistics.class);

            if (traderStatistics.getData() == null || traderStatistics.getData().isEmpty()) {
                throw new Exception("No position data available for protocol: " + protocol);
            }

            for (TraderStatistics.TraderStatistic data : traderStatistics.getData()) {
                int totalTrade = 0,
                        totalOrder = 0,
                        totalWin = 0,
                        totalLose = 0,
                        totalLiquidation = 0;
                double orderPositionRatio = 0,
                        winRate = 0,
                        totalVolume = 0,
                        avgVolume = 0,
                        totalLeverage = 0,
                        avgLeverage = 0,
                        minLeverage = 0,
                        maxLeverage = 0,
                        realisedPnl = 0,
                        realisedTotalGain = 0,
                        realisedTotalLoss = 0,
                        realisedProfitRate = 0,
                        realisedProfitLossRatio = 0,
                        realisedGainLossRatio = 0,
                        realisedRoi = 0,
                        realisedAvgRoi = 0,
                        totalLong = 0,
                        totalShort = 0,
                        longRate = 0,
                        totalLiquidationAmount = 0,
                        totalFee = 0,
                        totalGainFee = 0,
                        totalLossFee = 0,
                        totalDuration = 0,
                        avgDuration = 0,
                        minDuration = 0,
                        maxDuration = 0,
                        realisedMaxDrawDown = 0,
                        realisedMaxDrawDownPnl = 0;
                Double realisedMaxRoi = null;

                PositionsByTraderReq positionByTraderPayload = new PositionsByTraderReq(BaseUrlConfig.PROD_BASE_URL, protocol, data.getAccount());

                Response responsePositionByTrader = APIUtils.sendPostRequest(
                        positionByTraderPayload.getBaseUrl(),
                        positionByTraderPayload.getApiEndpoints().getPath(),
                        positionByTraderPayload.getApiEndpoints().getRequestDetails());

                PositionStatistics positionStatistics = JsonUtils.fromJson(responsePositionByTrader.getBody().asString(), PositionStatistics.class);

                for (PositionStatistics.Position position : positionStatistics.getData()) {
                    int number = getNumberOfDays(timeValue);

                    Instant now = Instant.now();
                    LocalDate today = now.atZone(ZoneOffset.UTC).toLocalDate();
                    Instant startTime = today.atStartOfDay(ZoneOffset.UTC).toInstant();
                    Instant endTime = startTime.minus(number, ChronoUnit.DAYS);

                    Instant closeBlockTime = Instant.parse(position.getCloseBlockTime());
                    if (closeBlockTime.isAfter(endTime) && !closeBlockTime.isAfter(startTime)) {
                        totalTrade++;
                        totalOrder += position.getOrderCount();

                        if (position.isWin()) {
                            totalWin++;
                        } else {
                            totalLose++;
                        }

                        totalVolume += position.getSize();
                        totalLeverage += position.getLeverage();

                        if (maxLeverage < position.getLeverage()) {
                            maxLeverage = position.getLeverage();
                        }
                        if (minLeverage == 0) {
                            if (maxLeverage >= position.getLeverage()) {
                                minLeverage = position.getLeverage();
                            }
                        } else {
                            if (minLeverage > position.getLeverage()) {
                                minLeverage = position.getLeverage();
                            }
                        }

                        realisedPnl += position.getRealisedPnl();

                        if (position.getRealisedPnl() > 0) {
                            realisedTotalGain += position.getRealisedPnl();
                            totalGainFee += position.getFee();
                        } else {
                            realisedTotalLoss += position.getRealisedPnl();
                            totalLossFee += position.getFee();
                        }

                        realisedRoi += position.getRealisedRoi();
                        if (realisedMaxRoi == null) {
                            realisedMaxRoi = position.getRealisedRoi();
                        } else {
                            if (realisedMaxRoi < position.getRealisedRoi()) {
                                realisedMaxRoi = position.getRealisedRoi();
                            }
                        }

                        if (position.isLong()) {
                            totalLong++;
                        } else {
                            totalShort++;
                        }

                        if (position.isLiquidate() && !position.isWin()) {
                            totalLiquidation++;
                            totalLiquidationAmount += position.getRealisedPnl();
                        }

                        totalFee += position.getFee();

                        totalDuration += position.getDurationInSecond();
                        if (maxDuration < position.getDurationInSecond()) {
                            maxDuration = position.getDurationInSecond();
                        }
                        if (minDuration == 0) {
                            if (maxDuration >= position.getDurationInSecond()) {
                                minDuration = position.getDurationInSecond();
                            }
                        } else {
                            if (minDuration > position.getDurationInSecond()) {
                                minDuration = position.getDurationInSecond();
                            }
                        }

                        if (realisedMaxDrawDown > position.getRealisedRoi()) {
                            realisedMaxDrawDown = position.getRealisedRoi();
                        }

                        if (realisedMaxDrawDownPnl > position.getRealisedPnl()) {
                            realisedMaxDrawDownPnl = position.getRealisedPnl();
                        }
                    }
                }

                if (totalTrade != 0) {
                    orderPositionRatio = (double) totalOrder / totalTrade;
                    winRate = ((double) totalWin / (totalWin + totalLose)) * 100;
                    avgVolume = totalVolume / totalTrade;
                    avgLeverage = totalLeverage / totalTrade;

                    if (realisedTotalLoss == 0) {
                        realisedGainLossRatio = realisedTotalGain;
                    } else {
                        realisedGainLossRatio = realisedTotalGain / Math.abs(realisedTotalLoss);
                    }

                    if (realisedTotalGain + Math.abs(realisedTotalLoss) != 0) {
                        realisedProfitRate = (realisedTotalGain / (realisedTotalGain + Math.abs(realisedTotalLoss))) * 100;
                    }

                    if (totalWin != 0 && totalLose != 0 && realisedTotalLoss / totalLose != 0) {
                        realisedProfitLossRatio = realisedTotalGain / totalWin / (realisedTotalLoss / totalLose);
                    }

                    realisedAvgRoi = realisedRoi / totalTrade;

                    longRate = (totalLong / totalTrade) * 100;
                    avgDuration = totalDuration / totalTrade;
                }

                double equivalentTotalVolume = 0.02 * totalVolume;
                double equivalentAvgVolume = 0.02 * avgVolume;
                double equivalentAvgLeverage = 0.02 * avgLeverage;
                double equivalentRealisedPnl = 0.02 * Math.abs(realisedPnl);
                double equivalentRealisedTotalGain = 0.02 * Math.abs(realisedTotalGain);
                double equivalentRealisedTotalLoss = 0.02 * Math.abs(realisedTotalLoss);
                double equivalentRealisedProfitRate = 0.02 * Math.abs(realisedProfitRate);
                double equivalentRealisedProfitLossRatio = 0.02 * Math.abs(realisedProfitLossRatio);
                double equivalentRealisedGainLossRatio = 0.02 * Math.abs(realisedGainLossRatio);
                double equivalentRealisedAvgRoi = 0.02 * Math.abs(realisedAvgRoi);
                double equivalentTotalFee = 0.02 * Math.abs(totalFee);
                double equivalentAvgDuration = 0.02 * avgDuration;
                double equivalentRealisedMaxDrawDown = 0.02 * Math.abs(realisedMaxDrawDown);
                double equivalentRealisedMaxDrawDownPnl = 0.02 * Math.abs(realisedMaxDrawDownPnl);

                Assert.assertEquals(totalTrade, data.getTotalTrade(),
                        "Total trade incorrect: " + totalTrade + "-" + data.getTotalTrade());
                Assert.assertTrue(totalTrade >= 0, "Total trade less than 0: " + totalTrade);

                Assert.assertEquals(orderPositionRatio, data.getOrderPositionRatio(), 0.01,
                        "Order Position Ratio incorrect: " + orderPositionRatio + "-" + data.getOrderPositionRatio());
                Assert.assertTrue(orderPositionRatio >= 0,
                        "Order Position Ratio less than 0: " + orderPositionRatio);

                Assert.assertEquals(totalWin, data.getTotalWin(),
                        "Total Win incorrect: " + totalWin + "-" + data.getTotalWin());
                Assert.assertTrue(totalWin >= 0, "Total Win less than 0: " + totalWin);

                Assert.assertEquals(totalLose, data.getTotalLose(),
                        "Total Lose incorrect: " + totalLose + "-" + data.getTotalLose());
                Assert.assertTrue(totalLose >= 0, "Total Lose less than 0: " + totalLose);

                Assert.assertEquals(winRate, data.getWinRate(), 0.01,
                        "Win rate incorrect: " + winRate + "-" + data.getWinRate());
                Assert.assertTrue(winRate >= 0, "Win rate less than 0: " + winRate);

                Assert.assertEquals(totalVolume, data.getTotalVolume(), equivalentTotalVolume,
                        "Total volume incorrect: " + totalVolume + "-" + data.getTotalVolume());
                Assert.assertTrue(totalVolume >= 0, "Total volume less than 0: " + winRate);

                Assert.assertEquals(avgVolume, data.getAvgVolume(), equivalentAvgVolume,
                        "Avg volume incorrect: " + avgVolume + "-" + data.getAvgVolume());
                Assert.assertTrue(avgVolume >= 0, "Avg volume less than 0: " + avgVolume);

                Assert.assertEquals(avgLeverage, data.getAvgLeverage(), equivalentAvgLeverage,
                        "Avg leverage incorrect: " + avgLeverage + "-" + data.getAvgLeverage());
                Assert.assertTrue(avgLeverage >= 0, "Avg leverage less than 0: " + avgLeverage);

                Assert.assertEquals(minLeverage, data.getMinLeverage(),
                        "Min leverage incorrect: " + minLeverage + "-" + data.getMinLeverage());
                Assert.assertTrue(minLeverage >= 0, "Min leverage less than 0: " + minLeverage);

                Assert.assertEquals(maxLeverage, data.getMaxLeverage(),
                        "Max leverage incorrect: " + maxLeverage + "-" + data.getMaxLeverage());

                Assert.assertEquals(realisedPnl, data.getRealisedPnl(), equivalentRealisedPnl,
                        "Realised Pnl incorrect: " + realisedPnl + "-" + data.getRealisedPnl());

                Assert.assertEquals(realisedTotalGain, data.getRealisedTotalGain(), equivalentRealisedTotalGain,
                        "Realised Total Gain incorrect: " + realisedTotalGain + "-" + data.getRealisedTotalGain());
                Assert.assertTrue(realisedTotalGain >= 0, "Realised Total Gain less than 0: " + realisedTotalGain);

                Assert.assertEquals(realisedTotalLoss, data.getRealisedTotalLoss(), equivalentRealisedTotalLoss,
                        "Realised Total Loss incorrect: " + realisedTotalLoss + "-" + data.getRealisedTotalLoss());
                Assert.assertTrue(realisedTotalLoss <= 0, "Realised Total Loss great than 0: " + realisedTotalLoss);

                Assert.assertEquals(realisedProfitRate, data.getRealisedProfitRate(), equivalentRealisedProfitRate,
                        "Realised Profit Rate incorrect: " + realisedProfitRate + "-" + data.getRealisedProfitRate());
                Assert.assertTrue(realisedProfitRate >= 0, "Realised Profit Rate less than 0: " + realisedProfitRate);

                Assert.assertEquals(realisedProfitLossRatio, data.getRealisedProfitLossRatio(), equivalentRealisedProfitLossRatio,
                        "Realised Profit Loss Ratio incorrect: " + realisedProfitLossRatio + "-" + data.getRealisedProfitLossRatio());

                Assert.assertEquals(realisedGainLossRatio, data.getRealisedGainLossRatio(), equivalentRealisedGainLossRatio,
                        "Realised Gain Loss Ratio incorrect: " + realisedGainLossRatio + "-" + data.getRealisedGainLossRatio());
                Assert.assertTrue(realisedGainLossRatio >= 0, "Realised Gain Loss Ratio less than 0: " + realisedGainLossRatio);

                Assert.assertEquals(realisedAvgRoi, data.getRealisedAvgRoi(), equivalentRealisedAvgRoi,
                        "Realised Avg Roi incorrect: " + realisedAvgRoi + "-" + data.getRealisedAvgRoi());

                Assert.assertEquals(realisedMaxRoi, data.getRealisedMaxRoi(),
                        "Realised Max Roi incorrect: " + realisedMaxRoi + "-" + data.getRealisedMaxRoi());

                Assert.assertEquals(longRate, data.getLongRate(),
                        "Long Rate incorrect: " + longRate + "-" + data.getLongRate());
                Assert.assertTrue(longRate >= 0, "Long Rate less than 0: " + longRate);

                Assert.assertEquals(totalLiquidation, data.getTotalLiquidation(),
                        "Total Liquidation incorrect: " + totalLiquidation + "-" + data.getTotalLiquidation());
                Assert.assertTrue(totalLiquidation >= 0, "Total Liquidation less than 0: " + totalLiquidation);

                Assert.assertEquals(totalFee, data.getTotalFee(), equivalentTotalFee,
                        "Total Fee incorrect: " + totalFee + "-" + data.getTotalFee());
                Assert.assertTrue(totalFee >= 0, "Total Fee less than 0: " + totalFee);

                Assert.assertEquals(avgDuration, data.getAvgDuration(), equivalentAvgDuration,
                        "Avg Duration incorrect: " + avgDuration + "-" + data.getAvgDuration());
                Assert.assertTrue(avgDuration >= 0, "Avg Duration less than 0: " + avgDuration);

                Assert.assertEquals(minDuration, data.getMinDuration(),
                        "Min Duration incorrect: " + minDuration + "-" + data.getMinDuration());
                Assert.assertTrue(minDuration >= 0, "Min Duration less than 0: " + minDuration);

                Assert.assertEquals(maxDuration, data.getMaxDuration(),
                        "Max Duration incorrect: " + maxDuration + "-" + data.getMaxDuration());

                Assert.assertEquals(realisedMaxDrawDown, data.getRealisedMaxDrawdown(), equivalentRealisedMaxDrawDown,
                        "Realised Max Draw Down incorrect: " + realisedMaxDrawDown + "-" + data.getRealisedMaxDrawdown());

                Assert.assertEquals(realisedMaxDrawDownPnl, data.getRealisedMaxDrawdownPnl(), equivalentRealisedMaxDrawDownPnl,
                        "Realised Max Draw Down Pnl incorrect: " + realisedMaxDrawDownPnl + "-" + data.getRealisedMaxDrawdownPnl());
            }
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
            default:
                return 60;
        }
    }
}
