package asia.decentralab.copin.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Positions {
    private PositionsTxHash positions;

    @Getter
    @Setter
    @ToString
    public static class PositionsTxHash {
        private String validTxHash;
        private String inValidTxHash;
    }
}