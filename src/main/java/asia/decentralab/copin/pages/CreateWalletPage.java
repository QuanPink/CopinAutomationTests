package asia.decentralab.copin.pages;

import asia.decentralab.copin.element.Element;
import asia.decentralab.copin.model.Wallets;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class CreateWalletPage {
    private final Element apiKeyTextbox = new Element(By.name("apiKey"));
    private final Element secretKeyTextbox = new Element(By.name("secretKey"));
    private final Element passPhraseTextbox = new Element(By.name("passPhrase"));
    private final Element walletNameTextbox = new Element(By.name("name"));
    private final Element createWalletButton = new Element(By.xpath("//button[contains(text(),'Create')][contains(text(),'Wallet')]"));

    @Step("Create wallet")
    public void createWallet(Wallets.Wallet wallet) {
        apiKeyTextbox.enter(wallet.getApiKey());
        secretKeyTextbox.enter(wallet.getSecretKey());
        if (!wallet.getPassPhrase().equals("")) {
            passPhraseTextbox.enter(wallet.getPassPhrase());
        }
        walletNameTextbox.enter(wallet.getWalletName());
        createWalletButton.click();
    }
}
