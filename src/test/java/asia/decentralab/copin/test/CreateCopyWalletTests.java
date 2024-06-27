package asia.decentralab.copin.test;

import asia.decentralab.copin.config.Constant;
import asia.decentralab.copin.data.enumdata.WalletType;
import asia.decentralab.copin.model.Wallets;
import asia.decentralab.copin.pages.CreateWalletPage;
import asia.decentralab.copin.pages.HomePage;
import asia.decentralab.copin.pages.WalletManagementPage;
import asia.decentralab.copin.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CreateCopyWalletTests extends BaseTest {
    private HomePage homePage;
    private WalletManagementPage walletManagementPage;
    private CreateWalletPage createWalletPage;
    private Wallets.Wallet bingXExchange;

    @BeforeClass
    public void setup() {
        super.setup();
        homePage = new HomePage();
        walletManagementPage = new WalletManagementPage();
        createWalletPage = new CreateWalletPage();

        Wallets wallets = JsonUtils.readJsonFile(Constant.WALLETS_FILE_PATH, Wallets.class);
        bingXExchange = wallets.getBingXExchange();
    }

    @AfterMethod
    public void afterEachTest() {
        homePage.goToHomePage();
    }

    @Test(description = "Check user is able to create BingX wallet with valid API key")
    public void wmg004CreateBingXWallet() {
        //login
        homePage.goToWalletManagement();
        walletManagementPage.goToCreateWalletPage(WalletType.BINGX);
        createWalletPage.createWallet(bingXExchange);
        walletManagementPage.expandWalletDetail(WalletType.BINGX);
        Assert.assertTrue(walletManagementPage.isWalletInformationCorrect(WalletType.BINGX, bingXExchange));
        walletManagementPage.deleteWallet(WalletType.BINGX, bingXExchange);
    }
}
