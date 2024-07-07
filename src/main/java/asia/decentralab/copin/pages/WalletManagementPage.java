package asia.decentralab.copin.pages;

import asia.decentralab.copin.data.enumdata.WalletType;
import asia.decentralab.copin.element.Element;
import asia.decentralab.copin.model.Wallets;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class WalletManagementPage extends BasePage {

    private final Element confirmDeleteButton = new Element(By.xpath("//div[@role='dialog']//button[text()='Confirm']"));

    private final String dynamicCreateWalletButton = "//div[div[div[text()='%s']]]/button[contains(.,'Create')]/*[local-name()='svg']";
    private final String dynamicGroupNameLabel = "//div[@id='WALLETS']//div[text()='%s']";
    private final String dynamicWalletItemName =
            "//div[@id='WALLETS']/div/div/div/div[contains(.,'%s')]/div[2]/div/div//div[@aria-label='display component'][text()='%s']";
    private final String dynamicDeleteWalletButton =
            "//div[@id='WALLETS']/div/div/div/div[contains(.,'%s')]/div[2]/div/div[contains(.,'%s')]//div[@role='button'][2]";

    @Step("Go to create wallet page")
    public void goToCreateWalletPage(WalletType wallet) {
        new Element(By.xpath(String.format(dynamicCreateWalletButton, wallet.getWalletName()))).click();
    }

    @Step("Expand Wallet detail")
    public void expandWalletDetail(WalletType wallet) {
        new Element(By.xpath(String.format(dynamicGroupNameLabel, wallet.getWalletName()))).click();
    }

    @Step("Verify wallet information is correct")
    public boolean isWalletInformationCorrect(WalletType walletType, Wallets.Wallet wallet) {
        return new Element(By.xpath(String.format(dynamicWalletItemName,
                walletType.getWalletName(), wallet.getWalletName()))).isDisplayed();
    }

    @Step("Delete Wallet")
    public void deleteWallet(WalletType type, Wallets.Wallet wallet) {
        waitForWalletNotificationMessageNotExist();
        new Element(By.xpath(String.format(dynamicDeleteWalletButton,
                type.getWalletName(), wallet.getWalletName()))).click();
        confirmDeleteButton.click();
        confirmDeleteButton.waitForNotDisplay();
        waitForWalletNotificationMessageNotExist();
    }
}
