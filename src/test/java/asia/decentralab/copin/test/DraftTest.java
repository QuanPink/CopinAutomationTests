package asia.decentralab.copin.test;

import asia.decentralab.copin.browser.Driver;
import asia.decentralab.copin.config.Constant;
import asia.decentralab.copin.config.DeFiWallets;
import asia.decentralab.copin.data.enumdata.DeFiWalletType;
import asia.decentralab.copin.pages.BasePage;
import asia.decentralab.copin.pages.ConnectWalletPage;
import asia.decentralab.copin.utils.JsonUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DraftTest extends BaseTest {

    @BeforeClass
    public void setup() {
        super.setup();
        BasePage basePage = new BasePage();
        ConnectWalletPage loginPage = new ConnectWalletPage();

        DeFiWallets deFiWallets = JsonUtils.readJsonFile(Constant.DE_FI_WALLETS_FILE_PATH, DeFiWallets.class);
        DeFiWallets.Wallet trustWallet = deFiWallets.getTrustWallet();

        basePage.setupTrustWallet(trustWallet.getSecretRecoveryPhrase(), trustWallet.getPassword());
        Driver.refreshPage();
        basePage.goToConnectWalletPage();
        loginPage.connectWallet(DeFiWalletType.TRUST_WALLET);
    }

    @Test
    public void createInvalidWallet() {

    }

//    @Test(description = "Test Call API")
//    public void callApi() {
//        ProtocolData protocolData = JsonUtils.readJsonFile(Constant.GMX_DATA_FILE_PATH, ProtocolData.class);
//        String response = APIUtils.sendRequest(protocolData);
//        System.out.println("API response: " + response);
//    }
//
//    @DataProvider(name = "listOfTrader")
//    public Object[][] dpMethod() {
//        Positions positions = JsonUtils.readJsonFile(Constant.POSITIONS_FILE_PATH, Positions.class);
//        Positions.Position closedPosition = positions.getClosePosition();
//        Positions.Position liquidatePosition = positions.getLiquidatePosition();
//        return new Object[][]{{closedPosition}, {liquidatePosition}};
//    }
//
//    @Test(dataProvider = "listOfTrader")
//    public void myTest(Positions.Position position) {
//        System.out.println("Tx Hash of the position : " + position.getTxHash());
//    }
}