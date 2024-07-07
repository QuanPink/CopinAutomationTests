package asia.decentralab.copin.data.enumdata;

public enum WalletType {
    BINGX("BingX Exchange", "BingX"),
    BITGET("Bitget Exchange", "Bitget"),
    OKX("OKX Exchange", "OKX"),
    GATE("Gate Exchange", "Gate"),
    BYBIT("Bybit Exchange", "Bybit");

    private String walletName;
    private String shortName;

    WalletType(String walletName, String shortName) {
        this.walletName = walletName;
        this.shortName = shortName;
    }

    public String getWalletName() {
        return walletName;
    }

    public String getShortName() {
        return shortName;
    }
}
