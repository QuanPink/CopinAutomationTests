package asia.decentralab.copin.data.enumdata;

public enum Token {
    BTC("BTC"),
    ETH("ETH"),
    LINK("LINK"),
    UNI("UNI");

    private final String value;

    Token(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}