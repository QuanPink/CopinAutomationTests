package asia.decentralab.copin.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class TraderProtocol {
    private List<TraderStatistic> data;

    @Getter
    @Setter
    @ToString
    public static class TraderStatistic {
        private String id;
        private String account;
        private int totalTrade;
        private int totalWin;
        private int totalLose;
        private double totalGain;
        private double realisedTotalGain;
        private double totalLoss;
        private double realisedTotalLoss;
        private double totalVolume;
        private double avgVolume;
        private double avgRoi;
        private double realisedAvgRoi;
        private double maxRoi;
        private double realisedMaxRoi;
        private double pnl;
        private double realisedPnl;
        private double maxPnl;
        private double realisedMaxPnl;
        private double realisedMaxDrawdown;
        private double realisedMaxDrawdownPnl;
        private double winRate;
        private double profitRate;
        private double realisedProfitRate;
        private double orderPositionRatio;
        private double profitLossRatio;
        private double realisedProfitLossRatio;
        private double longRate;
        private double gainLossRatio;
        private double realisedGainLossRatio;
        private double avgDuration;
        private double minDuration;
        private double maxDuration;
        private double avgLeverage;
        private double minLeverage;
        private double maxLeverage;
        private int totalLiquidation;
        private double totalLiquidationAmount;
        private int runTimeDays;
        private long lastTradeAtTs;
        private double totalFee;
        private String type;
        private String statisticAt;
        private String lastTradeAt;
        private List<String> indexTokens;
        private String createdAt;
        private boolean isOpenPosition;
        private String protocol;
    }
}