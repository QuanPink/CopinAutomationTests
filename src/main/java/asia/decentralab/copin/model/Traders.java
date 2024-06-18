package asia.decentralab.copin.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Traders {
    private TraderDetails traders;

    @Getter
    @Setter
    @ToString
    public static class TraderDetails {
        private Address address;
    }

    @Getter
    @Setter
    @ToString
    public static class Address {
        private String validAddress;
        private String upperCaseAddress;
    }
}