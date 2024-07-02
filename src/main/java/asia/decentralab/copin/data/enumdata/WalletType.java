package asia.decentralab.copin.data.enumdata;

import lombok.Getter;

@Getter
public enum WalletType {
    WALLET_CONNECT("WalletConnect"),
    METAMASK("MetaMask"),
    BRAVE_WALLET("Brave Wallet"),
    COIN98_WALLET("Coin98 Wallet"),
    COINBASE_WALLET("CoinBase Wallet"),
    TRUST_WALLET("Trust Wallet");

    private final String value;

    WalletType(String value) {
        this.value = value;
    }
}
