package asia.decentralab.copin.model;

import asia.decentralab.copin.data.enumdata.Token;
import asia.decentralab.copin.data.enumdata.WalletType;
import asia.decentralab.copin.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@ToString
public class CopyTrade {
    private String labelCopyTradeName;
    private WalletType copyWalletType;
    private String copyWalletName;
    private int margin;
    private boolean isFollowTrader;
    private boolean isExcludeToken;
    private List<Token> tokenList;
    private List<Token> tradingPair;
    private int leverage;
    private boolean isReverseCopy;
    private String stopLoss;
    private String takeProfit;
    private int maxMarginPerPosition;
    private int marginProtection;
    private boolean isSkipLowerLeveragePosition;
    private int lowLeverage;
    private boolean isSkipLowerCollateralPosition;
    private int lowCollateral;

    public CopyTrade(WalletType walletType, String copyWalletName) {
        this.labelCopyTradeName = "CopyTrade_" + StringUtils.generateRandomString();
        this.copyWalletType = walletType;
        this.copyWalletName = copyWalletName;
        this.margin = ThreadLocalRandom.current().nextInt(1, 10000 + 1);
        this.isFollowTrader = false;
        this.tokenList = Arrays.asList(Token.BTC,Token.UNI);
        this.leverage = ThreadLocalRandom.current().nextInt(2, 50 + 1);
        this.isReverseCopy = false;
        this.stopLoss = "50 %ROI";
        this.takeProfit = "20 %ROI";
        this.maxMarginPerPosition = 100;
        this.marginProtection = 10;
        this.isSkipLowerLeveragePosition = false;
        this.isSkipLowerCollateralPosition = false;
    }

    public CopyTrade(WalletType walletType, String copyWalletName, boolean isFollowTrader) {
        this.labelCopyTradeName = "CopyTrade_" + StringUtils.generateRandomString();
        this.copyWalletType = walletType;
        this.copyWalletName = copyWalletName;
        this.margin = ThreadLocalRandom.current().nextInt(1, 10000 + 1);
        this.isFollowTrader = true;
        this.isExcludeToken = true;
        this.tradingPair = Arrays.asList(Token.LINK, Token.UNI);
        this.leverage = ThreadLocalRandom.current().nextInt(2, 50 + 1);
        this.isReverseCopy = true;
        this.stopLoss = "50 USD";
        this.takeProfit = "20 USD";
        this.maxMarginPerPosition = 0;
        this.marginProtection = 0;
        this.isSkipLowerLeveragePosition = true;
        this.lowLeverage = ThreadLocalRandom.current().nextInt(2, 100 + 1);
        this.isSkipLowerCollateralPosition = false;
        this.lowCollateral = ThreadLocalRandom.current().nextInt(1, 10000 + 1);
    }
}
