package asia.decentralab.copin.data.enumdata;

public enum StatisticValue {
    PNL("PnL"),
    AVG_ROI("Avg ROI"),
    WIN_RATE("Win Rate");

    private final String value;

    StatisticValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
