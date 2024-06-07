package asia.decentralab.copin.data.enumdata;

public enum TimeValue {
    DAYS_7("7 days"),
    DAYS_14("14 days"),
    DAYS_30("30 days"),
    DAYS_60("60 days");

    private final String value;

    TimeValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
