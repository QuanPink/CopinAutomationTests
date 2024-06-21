package asia.decentralab.copin.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Traders {
    private Trader gmxTrader;
    private Trader gnsTrader;
    private Trader kwentaTrader;
    private Trader invalidTrader;

    @Getter
    @Setter
    @ToString
    public static class Trader {
        private String address;
    }
}