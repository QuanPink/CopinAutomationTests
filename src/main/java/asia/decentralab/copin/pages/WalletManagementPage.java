package asia.decentralab.copin.pages;

import asia.decentralab.copin.browser.Driver;
import asia.decentralab.copin.data.enumdata.WalletType;
import asia.decentralab.copin.element.Element;
import asia.decentralab.copin.model.Wallets;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class WalletManagementPage extends BasePage {
    private final String dynamicCreateWalletButton = "//div[div[div[text()='%s']]]/button[contains(.,'Create')]/*[local-name()='svg']";
    private final String dynamicExchangeNameLabel = "//div[@id='WALLETS']//div[text()='%s']";
    private final String dynamicWalletItemName =
            "//div[@id='WALLETS']/div/div/div/div[contains(.,'%s')]/div[2]/div/div//div[@aria-label='display component'][text()='%s']";
    private final String dynamicDeleteWalletButton =
            "//div[@id='WALLETS']/div/div/div/div[contains(.,'%s')]/div[2]/div/div[contains(.,'%s')]//div[@role='button'][2]";
//    private final String exchangeDropdownCollapse = "//div[@id='WALLETS']//div[text()='%s']/../../../../../following-sibling::div[contains(@class,'HwLED')]";
//    private final String exchangeDropdownExpand = "//div[@id='WALLETS']//div[text()='%s']/../../../../../following-sibling::div[contains(@class,'iHzYcO')]";


    @Step("Go to create wallet page")
    public void goToCreateWalletPage(WalletType wallet) {
        new Element(By.xpath(String.format(dynamicCreateWalletButton, wallet.getWalletName()))).click();
    }

    @Step("Expand Wallet detail")
    public void expandWalletDetail(WalletType wallet) {
        new Element(By.xpath(String.format(dynamicExchangeNameLabel, wallet.getWalletName()))).click();
        Driver.delay(1); //wait for the Wallet expand
    }

    @Step("Verify wallet information is correct")
    public boolean isWalletInformationCorrect(WalletType walletType, Wallets.Wallet wallet) {
        return new Element(By.xpath(String.format(dynamicWalletItemName,
                walletType.getWalletName(), wallet.getWalletName()))).isDisplayed();
    }

    @Step("Delete Wallet")
    public void deleteWallet(WalletType type, Wallets.Wallet wallet) {
        waitForNotificationMessageNotExist();
        new Element(By.xpath(String.format(dynamicDeleteWalletButton,
                type.getWalletName(), wallet.getWalletName()))).click();
        clickConfirmButton();
        waitForConfirmPopupNotDisplay();
        waitForNotificationMessageNotExist();
    }
}
