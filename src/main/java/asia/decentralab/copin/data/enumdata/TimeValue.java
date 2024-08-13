package asia.decentralab.copin.data.enumdata;

import lombok.Getter;

@Getter
public enum TimeValue {
    //UI Values
    DAYS_7("7 days"),
    DAYS_14("14 days"),
    DAYS_30("30 days"),
    DAYS_60("60 days"),
    IGNORE(""),

    //API Values
    DAYS_7_API("D7"),
    DAYS_15_API("D15"),
    DAYS_30_API("D30"),
    DAYS_60_API("D60"),
    DAYS_ALL_TIME_API("alltime");

    private final String value;

    TimeValue(String value) {
        this.value = value;
    }
}
