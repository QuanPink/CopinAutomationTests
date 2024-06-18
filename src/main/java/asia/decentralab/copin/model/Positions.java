package asia.decentralab.copin.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Positions {
    private PositionDetails positions;

    @Getter
    @Setter
    @ToString
    public static class PositionDetails {
        private TxHash openPosition;
        private TxHash closePosition;
        private TxHash liquidatePosition;
    }

    @Getter
    @Setter
    @ToString
    public static class TxHash {
        private String validTxHash;
        private String upperCaseTxHash;
    }
}