package asia.decentralab.copin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PositionStatistics {
    public List<Position> data;

    @Getter
    @Setter
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Position {
        private String id;
        private String synthetixPositionId;
        private String account;
        private String smartAccount;
        private String key;
        private int logId;
        private String indexToken;
        private double collateral;
        private double lastCollateral;
        private double size;
        private double lastSize;
        private double lastSizeInToken;
        private double averagePrice;
        private double fee;
        private double funding;
        private double realisedPnl;
        private double realisedRoi;
        private double roi;
        private double pnl;
        private boolean isLong;
        private double leverage;
        private long openBlockNumber;
        private long closeBlockNumber;
        private double durationInSecond;
        private int orderCount;
        private int orderIncreaseCount;
        private int orderDecreaseCount;
        private boolean isWin;
        private boolean isLiquidate;
        private String status;
        private String openBlockTime;
        private String closeBlockTime;
        private String protocol;
        private List<String> txHashes;
        private String createdAt;
    }
}
