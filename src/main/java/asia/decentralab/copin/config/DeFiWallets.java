package asia.decentralab.copin.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeFiWallets {
    private Wallet metamask;

    @Getter
    @Setter
    @ToString
    public static class Wallet {
        private String secretRecoveryPhrase;
        private String password;
    }
}
