package asia.decentralab.copin.data.enumdata;

public enum WalletType {
    BINGX("BingX Exchange"),
    BITGET("Bitget Exchange"),
    OKX("OKX Exchange"),
    GATE("Gate Exchange"),
    BYBIT("Bybit Exchange");

    private String walletName;

    WalletType(String walletName) {
        this.walletName = walletName;
    }

    public String getValue() {
        return walletName;
    }
}
