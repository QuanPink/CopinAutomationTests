package asia.decentralab.copin.test;

import asia.decentralab.copin.config.Constant;
import asia.decentralab.copin.data.enumdata.WalletType;
import asia.decentralab.copin.model.CopyTrade;
import asia.decentralab.copin.model.Wallets;
import asia.decentralab.copin.pages.CreateWalletPage;
import asia.decentralab.copin.pages.HomePage;
import asia.decentralab.copin.pages.WalletManagementPage;
import asia.decentralab.copin.utils.JsonUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CreateCopyTradeTests extends BaseTest {

    private HomePage homePage;
    private WalletManagementPage walletManagementPage;
    private CreateWalletPage createWalletPage;
    private Wallets.Wallet bingXExchange;
    private CopyTrade copyTrade;
    private WalletType bingXWallet;

    @BeforeClass
    public void setup() {
        super.setup();
        homePage = new HomePage();
        walletManagementPage = new WalletManagementPage();
        createWalletPage = new CreateWalletPage();

        Wallets wallets = JsonUtils.readJsonFile(Constant.WALLETS_FILE_PATH, Wallets.class);
        bingXExchange = wallets.getBingXExchange();
        bingXWallet = WalletType.BINGX;
        copyTrade = new CopyTrade(bingXWallet, bingXExchange.getWalletName(), true);
    }

    @AfterMethod
    public void afterEachTest() {
        homePage.goToHomePage();
    }

    @Test(description = "Check user is able to create copytrade on GMX from Trader Profile")
    public void tmg048CreateCopyTrade() {
        //login
        homePage.goToWalletManagement();
        walletManagementPage.goToCreateWalletPage(bingXWallet);
        createWalletPage.createWallet(bingXExchange);
        //walletManagementPage.
    }
}
