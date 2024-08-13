package asia.decentralab.copin.data.enumdata;

import lombok.Getter;

@Getter
public enum SourceValue {
    // UI Values
    GMX("GMX"),
    KWENTA("Kwenta"),
    POLYNOMIAL("Polynomial"),
    GMX_V2("GMX V2"),
    GTRADE("gTrade"),
    LEVEL("Level"),
    IGNORE(""),

    // API Values
    GMX_API("GMX"),
    GMX_V2_API("GMX_V2"),
    KWENTA_API("KWENTA"),
    POLYNOMIAL_API("POLYNOMIAL"),
    GNS_API("GNS"),
    GNS_POLY_API("GNS_POLY"),
    LEVEL_ARB_API("LEVEL_ARB"),
    LEVEL_BNB_API("LEVEL_BNB");

    private final String value;

    SourceValue(String value) {
        this.value = value;
    }
}
