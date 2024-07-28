package asia.decentralab.copin.data.enumdata;

import lombok.Getter;

@Getter
public enum BackgroundColor {
    LIGHT_BLUE("rgb(78, 174, 253)", ""),
    GREY("rgb(119, 126, 144)", "");

    private final String rbg;
    private final String hex;

    BackgroundColor(String rbg, String hex) {
        this.rbg = rbg;
        this.hex = hex;
    }
}
