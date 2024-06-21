package asia.decentralab.copin.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Positions {
    private Position openPosition;
    private Position closePosition;
    private Position liquidatePosition;
    private Position invalidPosition;

    @Getter
    @Setter
    @ToString
    public static class Position {
        private String txHash;
    }
}