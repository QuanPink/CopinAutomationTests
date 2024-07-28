package asia.decentralab.copin.data.enumdata;

import lombok.Getter;

import java.util.Random;

@Getter
public enum LeverageValue {
    LEV_2X("2x"),
    LEV_4X("4x"),
    LEV_6X("6x"),
    LEV_8X("8x"),
    LEV_10X("10x"),
    LEV_12X("12x"),
    LEV_14X("14x"),
    LEV_16X("16x"),
    LEV_18X("18x"),
    LEV_20X("20x"),
    LEV_22X("22x"),
    LEV_24X("24x"),
    LEV_26X("26x"),
    LEV_28X("28x"),
    LEV_30X("30x");

    private final String value;
    private static final Random LEV = new Random();

    LeverageValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LeverageValue randomLeverageValue() {
        LeverageValue[] leverageValues = values();
        return leverageValues[LEV.nextInt(leverageValues.length)];
    }

    @Override
    public String toString() {
        return value;
    }
}
