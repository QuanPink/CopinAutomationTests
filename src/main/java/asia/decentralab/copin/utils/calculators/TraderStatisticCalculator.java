package asia.decentralab.copin.utils.calculators;

import asia.decentralab.copin.models.TraderStatisticCalculationResult;
import asia.decentralab.copin.utils.ValidationUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TraderStatisticCalculator {
    // Thresholds for labels
    private static final double[] VOLUME_TIERS = {1000, 10000, 100000, 1000000, 10000000, 100000000};
    private static final double[] PNL_TIERS = {-1000000, -100000, -10000, 0, 10000, 100000, 1000000};

    public TraderStatisticCalculationResult calculationTraderStatistic(
            List<Map<String, Object>> positions,
            String timeValue) {

        TraderStatisticCalculationResult result = new TraderStatisticCalculationResult();

        if (positions == null || positions.isEmpty()) {
            return result;
        }

        // Parse timeValue to get number of days
        int days = parseTimeValueToDays(timeValue);

        // Calculate time range
        TimeRange timeRange = calculateTimeRange(days);
        long startTimeMillis = timeRange.startTimeMillis;
        long endTimeMillis = timeRange.endTimeMillis;

        // Process current streak (first positions regardless of time)
        processCurrentStreak(positions, result);

        // Process positions within timeframe
        int tempStreak = 0;
        Boolean tempIsWin = null;

        for (Map<String, Object> position : positions) {
            String closeBlockTimeStr = (String) position.get("closeBlockTime");
            if (closeBlockTimeStr == null) continue;

            long positionTimeMillis = Instant.parse(closeBlockTimeStr).toEpochMilli();

            // Check if within time range
            if (positionTimeMillis < startTimeMillis || positionTimeMillis >= endTimeMillis) {
                continue;
            }

            processPosition(position, result);

            // Streak calculation
            boolean isWin = ValidationUtils.getBooleanValue(position, "isWin");
            if (tempIsWin == null || tempIsWin == isWin) {
                tempStreak++;
            } else {
                updateMaxStreak(result, tempStreak, tempIsWin);
                tempStreak = 1;
            }
            tempIsWin = isWin;
        }

        fixInfinityValues(result);

        if (tempIsWin != null) {
            updateMaxStreak(result, tempStreak, tempIsWin);
        }

        if (result.totalTrade == 0) {
            return result;
        }

        // Calculate averages
        result.orderPositionRatio = (double) result.totalOrder / result.totalTrade;
        result.avgVolume = result.totalVolume / result.totalTrade;
        result.avgLeverage = result.totalLeverage / result.totalTrade;
        result.avgDuration = result.totalDuration / result.totalTrade;
        result.realisedAvgRoi = result.realisedRoi / result.totalTrade;

        // Calculate rates
        result.winRate = ((double) result.totalWin / result.totalTrade) * 100;
        result.longRate = ((double) result.totalLong / result.totalTrade) * 100;

        // Average volumes
        if (result.totalLong > 0) {
            result.avgLongVolume = result.totalLongVolume / result.totalLong;
        }

        if (result.totalShort > 0) {
            result.avgShortVolume = result.totalShortVolume / result.totalShort;
        }

        // Gain/Loss ratios
        if (result.realisedTotalLoss == 0) {
            result.realisedGainLossRatio = 0;
        } else {
            result.realisedGainLossRatio = result.realisedTotalGain / Math.abs(result.realisedTotalLoss);
        }

        // Profit rate
        double totalGainLoss = result.realisedTotalGain + Math.abs(result.realisedTotalLoss);
        if (totalGainLoss != 0) {
            result.realisedProfitRate = (result.realisedTotalGain / totalGainLoss) * 100;
        }

        // Profit/Loss ratio (average win / average loss)
        if (result.totalWin > 0 && result.totalLose > 0 && result.realisedTotalLoss != 0) {
            double avgWin = result.realisedTotalGain / result.totalWin;
            double avgLoss = Math.abs(result.realisedTotalLoss) / result.totalLose;
            if (avgLoss != 0) {
                result.realisedProfitLossRatio = avgWin / avgLoss;
            }
        }

        // Calculate advanced ratios
        calculateRatios(result.dailyReturns, result, true);
        calculateRatios(result.dailyReturnsBeforeFee, result, false);

        // Generate labels
        generateStatisticLabels(result);

        return result;
    }

    private void processPosition(Map<String, Object> position,
                                 TraderStatisticCalculationResult result) {

        // Extract position data
        boolean isWin = ValidationUtils.getBooleanValue(position, "isWin");
        boolean isLong = ValidationUtils.getBooleanValue(position, "isLong");
        boolean isLiquidate = ValidationUtils.getBooleanValue(position, "isLiquidate");
        double size = ValidationUtils.getDoubleValue(position, "size");
        double leverage = ValidationUtils.getDoubleValue(position, "leverage");
        double realisedPnl = ValidationUtils.getDoubleValue(position, "realisedPnl");
        double pnl = ValidationUtils.getDoubleValue(position, "pnl");
        double realisedRoi = ValidationUtils.getDoubleValue(position, "realisedRoi");
        double roi = ValidationUtils.getDoubleValue(position, "roi");
        double fee = ValidationUtils.getDoubleValue(position, "fee");
        long duration = ValidationUtils.getLongValue(position, "durationInSecond");
        int orderCount = ValidationUtils.getIntValue(position, "orderCount");

        result.totalTrade++;
        result.totalOrder += orderCount;

        if (isWin) {
            result.totalWin++;
        } else {
            result.totalLose++;
        }

        result.totalVolume += size;

        result.totalLeverage += leverage;
        result.minLeverage = Math.min(leverage, result.minLeverage);
        result.maxLeverage = Math.max(leverage, result.maxLeverage);

        result.realisedPnl += realisedPnl;
        result.pnl += pnl;
        if (realisedPnl > 0) {
            result.realisedTotalGain += realisedPnl;
            result.totalGainFee += fee;
        } else {
            result.realisedTotalLoss += realisedPnl;
            result.totalLossFee += fee;
        }

        result.realisedRoi += realisedRoi;
        result.roi += roi;
        result.realisedMaxRoi = Math.max(realisedRoi, result.realisedMaxRoi);
        result.realisedMaxDrawdown = Math.min(realisedRoi, result.realisedMaxDrawdown);
        result.realisedMaxDrawdownPnl = Math.min(realisedPnl, result.realisedMaxDrawdown);

        if (isLong) {
            result.totalLong++;
            result.totalLongVolume += size;
            result.realisedLongPnl += realisedPnl;
            result.longPnl += pnl;
        } else {
            result.totalShort++;
            result.totalShortVolume += size;
            result.realisedShortPnl += realisedPnl;
            result.shortPnl += pnl;
        }

        if (isLiquidate && !isWin) {
            result.totalLiquidation++;
            result.totalLiquidationAmount += realisedPnl;
        }

        result.totalFee += fee;
        result.totalDuration += duration;
        result.minDuration = Math.min(duration, result.minDuration);
        result.maxDuration = Math.max(duration, result.maxDuration);

        result.dailyReturnsBeforeFee.add(roi);
        result.dailyReturns.add(realisedRoi);
    }

    private void fixInfinityValues(TraderStatisticCalculationResult result) {
        if (result.minLeverage == Double.MAX_VALUE) {
            result.minLeverage = 0;
        }
        if (result.minDuration == Long.MAX_VALUE) {
            result.minDuration = 0;
        }
        if (result.realisedMaxRoi == -Double.MAX_VALUE) {
            result.realisedMaxRoi = 0;
        }
    }

    /**
     * Process current streak (first few positions)
     */
    private void processCurrentStreak(List<Map<String, Object>> positions,
                                      TraderStatisticCalculationResult result) {
        if (positions.isEmpty()) return;

        for (int i = 0; i < positions.size(); i++) {
            boolean isWin = ValidationUtils.getBooleanValue(positions.get(i), "isWin");

            if (i == 0) {
                result.isCurrentStreakWin = isWin;
                result.currentStreak = 1;
            } else if (isWin == result.isCurrentStreakWin) {
                result.currentStreak++;
            } else {
                break;
            }
        }

        // Set win/lose streak based on current streak
        if (result.isCurrentStreakWin) {
            result.winStreak = result.currentStreak;
        } else {
            result.loseStreak = result.currentStreak;
        }
    }

    /**
     * Generate statistic labels
     */
    private void generateStatisticLabels(TraderStatisticCalculationResult result) {
        result.realisedStatisticLabels.clear();
        result.statisticLabels.clear();

        // Volume Tier
        int volumeTierIndex = -1;
        for (int i = 0; i < VOLUME_TIERS.length; i++) {
            if (result.avgVolume < VOLUME_TIERS[i]) {
                volumeTierIndex = i;
                break;
            }
        }
        result.realisedStatisticLabels.add("VOLUME_TIER" + (volumeTierIndex == -1 ? 7 : volumeTierIndex + 1));
        result.statisticLabels.add("VOLUME_TIER" + (volumeTierIndex == -1 ? 7 : volumeTierIndex + 1));

        // PnL Tier
        int realisedPnlTierIndex = -1;
        int pnlTierIndex = -1;
        for (int i = 0; i < PNL_TIERS.length; i++) {
            if (result.realisedPnl < PNL_TIERS[i]) {
                realisedPnlTierIndex = i;
                break;
            }
        }

        for (int i = 0; i < PNL_TIERS.length; i++) {
            if (result.pnl < PNL_TIERS[i]) {
                pnlTierIndex = i;
                break;
            }
        }
        result.realisedStatisticLabels.add("PNL_TIER" + (realisedPnlTierIndex == -1 ? 8 : realisedPnlTierIndex + 1));
        result.statisticLabels.add("PNL_TIER" + (pnlTierIndex == -1 ? 8 : pnlTierIndex + 1));

        // Trader Type
        String traderType = "POSITION_TRADER";
        if (result.avgDuration < 3600) {
            traderType = "SCALPER";
        } else if (result.avgDuration < 86400) {
            traderType = "DAY_TRADER";
        } else if (result.avgDuration < 604800) {
            traderType = "SWING_TRADER";
        }
        result.realisedStatisticLabels.add(traderType);
        result.statisticLabels.add(traderType);

        // Risk Profile
        if (result.avgLeverage > 25 && result.realisedMaxDrawdown < -60) {
            result.realisedStatisticLabels.add("HIGH_RISK");
            result.statisticLabels.add("HIGH_RISK");
        } else if (result.avgLeverage < 5 && result.realisedMaxDrawdown > -30) {
            result.realisedStatisticLabels.add("LOW_RISK");
            result.statisticLabels.add("LOW_RISK");
        }

        // Market Bias
        if (result.totalTrade > 1) {
            if (result.longRate >= 90) {
                result.realisedStatisticLabels.add("BULLISH");
                result.statisticLabels.add("BULLISH");
            } else if (result.longRate <= 10) {
                result.realisedStatisticLabels.add("BEARISH");
                result.statisticLabels.add("BEARISH");
            }
        }
    }

    private void calculateRatios(List<Double> returns,
                                 TraderStatisticCalculationResult result,
                                 boolean isRealised) {
        if (returns.size() <= 1) {
            return;
        }

        double avgDailyReturn = returns.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);

        // Sharpe Ratio
        double variance = returns.stream()
                .mapToDouble(ret -> Math.pow(ret - avgDailyReturn, 2))
                .sum() / (returns.size() - 1);

        if (variance > 0) {
            double sharpe = avgDailyReturn / Math.sqrt(variance);
            if (isRealised) {
                result.realisedSharpeRatio = sharpe;
            } else {
                result.sharpeRatio = sharpe;
            }
        }

        // Sortino Ratio
        List<Double> downsideReturns = returns.stream()
                .filter(ret -> ret < 0)
                .collect(Collectors.toList());

        if (!downsideReturns.isEmpty()) {
            double downsideVariance = downsideReturns.stream()
                    .mapToDouble(ret -> Math.pow(ret - avgDailyReturn, 2))
                    .sum() / (returns.size() - 1);

            if (downsideVariance > 0) {
                double sortino = avgDailyReturn / Math.sqrt(downsideVariance);
                if (isRealised) {
                    result.realisedSortinoRatio = sortino;
                } else {
                    result.sortinoRatio = sortino;
                }
            }
        }
    }

    /**
     * Update max streak values
     */
    private void updateMaxStreak(TraderStatisticCalculationResult result,
                                 int tempStreak,
                                 boolean tempIsWin) {
        if (tempIsWin) {
            result.maxWinStreak = Math.max(result.maxWinStreak, tempStreak);
        } else {
            result.maxLoseStreak = Math.max(result.maxLoseStreak, tempStreak);
        }
    }

    // Helper class for time range
    private static class TimeRange {
        final long startTimeMillis;
        final long endTimeMillis;

        TimeRange(long startTimeMillis, long endTimeMillis) {
            this.startTimeMillis = startTimeMillis;
            this.endTimeMillis = endTimeMillis;
        }
    }

    private int parseTimeValueToDays(String timeValue) {
        if (timeValue == null || timeValue.isEmpty()) {
            return 0; // ALL_TIME
        }

        // Remove "D" or "d" prefix and parse number
        String numberPart = timeValue.replaceFirst("^[Dd]", "");

        try {
            return Integer.parseInt(numberPart);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private TimeRange calculateTimeRange(int days) {
        if (days == 0) {
            // ALL_TIME
            return new TimeRange(0L, Long.MAX_VALUE);
        }

        // Get current time in UTC
        Instant now = Instant.now();

        // Set to beginning of today (00:00:00) UTC
        Instant endTime = now.truncatedTo(ChronoUnit.DAYS);
        long endTimeMillis = endTime.toEpochMilli();

        // Go back 'days' days
        Instant startTime = endTime.minus(days, ChronoUnit.DAYS);
        long startTimeMillis = startTime.toEpochMilli();

        return new TimeRange(startTimeMillis, endTimeMillis);
    }
}