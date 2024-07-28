package asia.decentralab.copin.model;

import asia.decentralab.copin.data.enumdata.LeverageValue;
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
    private List<Token> selectedPairList;
    private List<Token> tradingPairExclude;
    private String leverage;
    private boolean isReverseCopy;
    private String stopLoss;
    private String takeProfit;
    private int maxMarginPerPosition;
    private int marginProtection;
    private boolean isSkipLowerLeveragePosition;
    private int lowLeverage;
    private boolean isSkipLowerCollateralPosition;
    private int lowCollateral;
    private String traderAddress;
    private boolean runStatus;

    public CopyTrade(WalletType walletType, String copyWalletName) {
        this.labelCopyTradeName = "CopyTrade_" + StringUtils.generateRandomString();
        this.copyWalletType = walletType;
        this.copyWalletName = copyWalletName;
        this.margin = ThreadLocalRandom.current().nextInt(1, 999);
        this.selectedPairList = Arrays.asList(Token.BTC, Token.UNI);
        this.isFollowTrader = false;
        this.isExcludeToken = false;
        this.tradingPairExclude = Arrays.asList(Token.ETH, Token.LINK);
        this.leverage = LeverageValue.randomLeverageValue().getValue();
        this.isReverseCopy = false;
        this.stopLoss = "50 %ROI";
        this.takeProfit = "20 %ROI";
        this.maxMarginPerPosition = 100;
        this.marginProtection = 10;
        this.isSkipLowerLeveragePosition = false;
        this.lowLeverage = ThreadLocalRandom.current().nextInt(2, 100 + 1);
        this.isSkipLowerCollateralPosition = false;
        this.lowCollateral = ThreadLocalRandom.current().nextInt(1, 10000 + 1);
        this.runStatus = true;
    }

    public CopyTrade(WalletType walletType, String copyWalletName, boolean isFollowTrader) {
        this.labelCopyTradeName = "CopyTrade_" + StringUtils.generateRandomString();
        this.copyWalletType = walletType;
        this.copyWalletName = copyWalletName;
        this.margin = ThreadLocalRandom.current().nextInt(1, 10000 + 1);
        this.selectedPairList = Arrays.asList(Token.LINK, Token.UNI);
        this.isFollowTrader = true;
        this.isExcludeToken = true;
        this.tradingPairExclude = Arrays.asList(Token.LINK, Token.UNI);
        this.leverage = LeverageValue.randomLeverageValue().getValue();
        this.isReverseCopy = true;
        this.stopLoss = "50 USD";
        this.takeProfit = "20 USD";
        this.maxMarginPerPosition = ThreadLocalRandom.current().nextInt(1, 10000 + 1);
        this.marginProtection = ThreadLocalRandom.current().nextInt(1, 20 + 1);
        this.isSkipLowerLeveragePosition = false;
        this.lowLeverage = ThreadLocalRandom.current().nextInt(2, 100 + 1);
        this.isSkipLowerCollateralPosition = false;
        this.lowCollateral = ThreadLocalRandom.current().nextInt(1, 10000 + 1);
        this.runStatus = true;
    }
}
