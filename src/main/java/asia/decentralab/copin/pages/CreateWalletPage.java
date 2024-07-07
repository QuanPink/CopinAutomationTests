package asia.decentralab.copin.pages;

import asia.decentralab.copin.browser.Driver;
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
    private final Element closeCreateWalletButton = new Element(By.xpath("//button[@class='Buttons__Button-sc-1e374vj-0 kEDphR']//*[name()='svg']"));
    private final Element invalidWalletNotification = new Element(By.xpath("//div[@class='Toastify']//div[@class = 'Toastify__toast-body']//div[@display='inline-block']"));

    @Step("Create wallet")
    public void createWallet(Wallets.Wallet wallet) {
        apiKeyTextbox.enter(wallet.getApiKey());
        secretKeyTextbox.enter(wallet.getSecretKey());
        if (!wallet.getPassPhrase().isEmpty()) {
            passPhraseTextbox.enter(wallet.getPassPhrase());
        }
        walletNameTextbox.enter(wallet.getWalletName());
        createWalletButton.click();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Driver.refreshPage();
    }

    @Step("Verify error message is displayed")
    public boolean isErrorMessageDisplayed(String messageContent) {
        return invalidWalletNotification.getText().equals(messageContent);
    }

    @Step("Close Create Wallet popup")
    public void closeCreateWalletPopup() {
        closeCreateWalletButton.click();
    }
}
