package asia.decentralab.copin.model;

import asia.decentralab.copin.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Wallets {
    private Wallet bingXExchange;
    private Wallet bitgetExchange;
    private Wallet bybitExchange;
    private Wallet okxExchange;
    private Wallet gateExchange;


    @Getter
    @Setter
    @ToString
    public static class Wallet {
        private String apiKey;
        private String secretKey;
        private String passPhrase;
        private String walletName;

        public String getWalletName() {
            return walletName + StringUtils.generateRandomString();
        }
    }
}
