package asia.decentralab.copin.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Traders {
    private TraderAddresses traders;

    @Getter
    @Setter
    @ToString
    public static class TraderAddresses {
        private String validAddress;
        private String inValidAddress;
    }
}
