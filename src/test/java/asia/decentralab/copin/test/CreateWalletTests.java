package asia.decentralab.copin.test;

import asia.decentralab.copin.browser.Driver;
import asia.decentralab.copin.config.Constant;
import asia.decentralab.copin.config.DeFiWallets;
import asia.decentralab.copin.data.enumdata.DeFiWalletType;
import asia.decentralab.copin.data.enumdata.WalletType;
import asia.decentralab.copin.model.Wallets;
import asia.decentralab.copin.pages.ConnectWalletPage;
import asia.decentralab.copin.pages.CreateWalletPage;
import asia.decentralab.copin.pages.HomePage;
import asia.decentralab.copin.pages.WalletManagementPage;
import asia.decentralab.copin.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CreateWalletTests extends BaseTest {
    private HomePage homePage;
    private WalletManagementPage walletManagementPage;
    private CreateWalletPage createWalletPage;
    private Wallets.Wallet bingXExchange;
    private Wallets.Wallet invalidBingXExchange;
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
        connectWalletPage = new ConnectWalletPage();

        Wallets wallets = JsonUtils.readJsonFile(Constant.WALLETS_FILE_PATH, Wallets.class);
        bingXExchange = wallets.getBingXExchange();
        invalidBingXExchange = wallets.getInvalidBingXExchange();
        bingXWallet = WalletType.BINGX;

        deFiWallets = JsonUtils.readJsonFile(Constant.DE_FI_WALLETS_FILE_PATH, DeFiWallets.class);
        trustWallet = deFiWallets.getTrustWallet();
        homePage.setupTrustWallet(trustWallet.getSecretRecoveryPhrase(), trustWallet.getPassword());
        Driver.refreshPage();
        homePage.goToConnectWalletPage();
        connectWalletPage.connectWallet(DeFiWalletType.TRUST_WALLET);
    }

    @AfterMethod
    public void afterEachTest() {
        homePage.goToHomePage();
    }

    @Test(description = "Check user is not able to import BingX wallet with invalid API key")
    public void wmg002CreateInvalidBingXWallet() {
        homePage.goToWalletManagement();
        walletManagementPage.goToCreateWalletPage(bingXWallet);
        createWalletPage.createWallet(invalidBingXExchange);
        Assert.assertTrue(createWalletPage.isErrorMessageDisplayed(Constant.INVALID_API_KEY_MESSAGE));
        createWalletPage.closeCreateWalletPopup();
    }

    @Test(description = "Check user is able to create BingX wallet with valid API key")
    public void wmg004CreateBingXWallet() {
        homePage.goToWalletManagement();
        walletManagementPage.goToCreateWalletPage(bingXWallet);
        createWalletPage.createWallet(bingXExchange);
        walletManagementPage.expandWalletDetail(bingXWallet);
        Assert.assertTrue(walletManagementPage.isWalletInformationCorrect(WalletType.BINGX, bingXExchange));
        walletManagementPage.deleteWallet(bingXWallet, bingXExchange);
    }
}
