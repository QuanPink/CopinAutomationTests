package asia.decentralab.copin.models;

import java.util.ArrayList;
import java.util.List;

public class TraderStatisticCalculationResult {
    // Count metrics
    public int totalTrade = 0;
    public int totalOrder = 0;
    public int totalWin = 0;
    public int totalLose = 0;
    public int totalLong = 0;
    public int totalShort = 0;
    public int totalLiquidation = 0;

    // Ratio metrics
    public double orderPositionRatio = 0;
    public double winRate = 0;
    public double longRate = 0;

    // Volume metrics
    public double totalVolume = 0;
    public double avgVolume = 0;
    public double totalLongVolume = 0;
    public double totalShortVolume = 0;
    public double avgLongVolume = 0;
    public double avgShortVolume = 0;

    // Leverage metrics
    public double totalLeverage = 0;
    public double avgLeverage = 0;
    public double minLeverage = Double.MAX_VALUE;
    public double maxLeverage = 0;

    // PnL metrics
    public double realisedPnl = 0;
    public double pnl = 0;
    public double realisedTotalGain = 0;
    public double realisedTotalLoss = 0;
    public double realisedLongPnl = 0;
    public double realisedShortPnl = 0;
    public double longPnl = 0;
    public double shortPnl = 0;

    // Profit metrics
    public double realisedProfitRate = 0;
    public double realisedProfitLossRatio = 0;
    public double realisedGainLossRatio = 0;

    // Fee metrics
    public double totalFee = 0;
    public double totalGainFee = 0;
    public double totalLossFee = 0;

    // ROI metrics
    public double realisedRoi = 0;
    public double roi = 0;
    public double realisedAvgRoi = 0;
    public double realisedMaxRoi = -Double.MAX_VALUE;
    public double realisedMaxDrawdown = 0;
    public double maxDrawdown = 0;
    public double realisedMaxDrawdownPnl = 0;
    public double maxDrawdownPnl = 0;

    // Liquidation metrics
    public double totalLiquidationAmount = 0;

    // Duration metrics
    public double totalDuration = 0;
    public double avgDuration = 0;
    public double minDuration = Double.MAX_VALUE;
    public double maxDuration = 0;

    // Streak metrics
    public int currentStreak = 0;
    public int winStreak = 0;
    public int loseStreak = 0;
    public int maxWinStreak = 0;
    public int maxLoseStreak = 0;
    public Boolean isCurrentStreakWin = null;

    // Advanced ratios
    public double realisedSharpeRatio = 0;
    public double realisedSortinoRatio = 0;
    public double sharpeRatio = 0;
    public double sortinoRatio = 0;

    // For internal calculation
    public List<Double> dailyReturnsBeforeFee = new ArrayList<>();
    public List<Double> dailyReturns = new ArrayList<>();

    // Labels for categorization
    public List<String> realisedStatisticLabels = new ArrayList<>();
    public List<String> statisticLabels = new ArrayList<>();
}
