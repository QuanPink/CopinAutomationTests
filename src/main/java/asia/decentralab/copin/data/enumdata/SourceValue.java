package asia.decentralab.copin.data.enumdata;

import lombok.Getter;

@Getter
public enum SourceValue {
    GMX("GMX"),
    KWENTA("Kwenta"),
    POLYNOMIAL("Polynomial"),
    GMX_V2("GMX V2"),
    GTRADE("gTrade"),
    LEVEL("Level"),
    IGNORE("");

    private final String value;

    SourceValue(String value) {
        this.value = value;
    }
}
