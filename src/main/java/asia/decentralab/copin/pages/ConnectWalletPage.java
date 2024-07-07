package asia.decentralab.copin.pages;

import asia.decentralab.copin.browser.Driver;
import asia.decentralab.copin.data.enumdata.DeFiWalletType;
import asia.decentralab.copin.element.Element;
import org.openqa.selenium.By;

public class ConnectWalletPage {
    private final Element shadowRootElement = new Element(By.cssSelector("onboard-v2"));
    private final String dynamicWalletName = "//div[contains(@class,'wallets-container')]//div[contains(text(),'%s')]";
    private final Element connectButton = new Element(By.xpath("//button[text()='Connect']"));
    private final Element confirmButton = new Element(By.xpath("//button[text()='Confirm']"));

    public void connectWallet(DeFiWalletType wallet) {
        shadowRootElement.findShadowElement(String.format(dynamicWalletName, wallet.getValue())).click();
        Driver.switchToWindow(2);
        connectButton.click();
        Driver.switchToWindow(1);
        Driver.switchToWindow(2);
        confirmButton.click();
        Driver.switchToWindow(1);
    }
}
