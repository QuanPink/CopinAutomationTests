package asia.decentralab.copin.test;

import asia.decentralab.copin.browser.Driver;
import asia.decentralab.copin.config.Constant;
import asia.decentralab.copin.config.DeFiWallets;
import asia.decentralab.copin.data.enumdata.*;
import asia.decentralab.copin.model.CopyTrade;
import asia.decentralab.copin.model.Wallets;
import asia.decentralab.copin.pages.*;
import asia.decentralab.copin.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CreateCopyTradeTests extends BaseTest {

    private HomePage homePage;
    private WalletManagementPage walletManagementPage;
    private CreateWalletPage createWalletPage;
    private CreateCopyTradePage createCopyTradePage;
    private CopyTradeManagementPage copyTradeManagementPage;
    private Wallets.Wallet bingXExchange;
    private CopyTrade copyTrade;
    private WalletType bingXWallet;
    private ConnectWalletPage connectWalletPage;
    private DeFiWallets deFiWallets;
    DeFiWallets.Wallet trustWallet;

    @BeforeClass
    public void setup() {
        super.setup();
        homePage = new HomePage();
        walletManagementPage = new WalletManagementPage();
        createWalletPage = new CreateWalletPage();
        copyTradeManagementPage = new CopyTradeManagementPage();
        connectWalletPage = new ConnectWalletPage();
        createCopyTradePage = new CreateCopyTradePage();

        Wallets wallets = JsonUtils.readJsonFile(Constant.WALLETS_FILE_PATH, Wallets.class);
        bingXExchange = wallets.getBingXExchange();
        bingXWallet = WalletType.BINGX;
        copyTrade = new CopyTrade(bingXWallet, bingXExchange.getWalletName(), true);

        deFiWallets = JsonUtils.readJsonFile(Constant.DE_FI_WALLETS_FILE_PATH, DeFiWallets.class);
        trustWallet = deFiWallets.getTrustWallet();
        homePage.setupTrustWallet(trustWallet.getSecretRecoveryPhrase(), trustWallet.getPassword());
        Driver.refreshPage();
        homePage.goToConnectWalletPage();
        connectWalletPage.connectWallet(DeFiWalletType.TRUST_WALLET);

        homePage.goToWalletManagement();
        walletManagementPage.goToCreateWalletPage(bingXWallet);
        createWalletPage.createWallet(bingXExchange);
        createWalletPage.waitForNotificationMessageNotExist();
        createWalletPage.goToHomePage();
    }

    @AfterMethod
    public void afterEachTest() {
        homePage.goToHomePage();
    }

    @Test(description = "Check user is able to create copytrade on GMX from Homepage")
    public void tmg048CreateCopyTrade() {
        homePage.filterTraderStatistic(StatisticValue.IGNORE, TimeValue.IGNORE, SourceValue.GMX_V2);
        String traderAddress = homePage.openCopyTradePageForRandomTrader();
        copyTrade.setTraderAddress(traderAddress);
        createCopyTradePage.createCopyTrade(copyTrade);
        Assert.assertEquals(createCopyTradePage.getNotificationMessageContent(), Constant.SUCCESS_CREATE_COPY_TRADE_MESSAGE);
        createCopyTradePage.waitForNotificationMessageNotExist();
        createCopyTradePage.goToCopyTradeManagementPage();
        copyTradeManagementPage.switchWallet(copyTrade.getCopyWalletName());
        Assert.assertTrue(copyTradeManagementPage.isCopyTradeInformationCorrect(copyTrade));
        copyTradeManagementPage.deleteCopyTrade(copyTrade);
    }

    @AfterClass
    public void tearDown() {
        homePage.goToWalletManagement();
        walletManagementPage.expandWalletDetail(WalletType.BINGX);
        walletManagementPage.deleteWallet(WalletType.BINGX, bingXExchange);
        super.tearDown();
    }
}
