package asia.decentralab.copin.pages;

import asia.decentralab.copin.browser.Driver;
import asia.decentralab.copin.element.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class LoginPage {
    // setup trustWallet
    private final Element importWalletButton = new Element(By.xpath("//div[@role='button']//p[contains(text(),'Import or recover wallet')]"));
    private final Element passwordTextbox = new Element(By.xpath("//div[contains(p,'New password')]//input[@type = 'password']"));
    private final Element confirmPasswordTextbox = new Element(By.xpath("//div[contains(p,'Confirm new password')]//input[@type = 'password']"));
    private final Element termsOfUseCheckbox = new Element(By.xpath("//input[@type='checkbox']"));
    private final Element submitButton = new Element(By.xpath("//button[@type='submit']"));
    private final Element secretPhraseTextbox = new Element(By.xpath("//input[@type='password']"));
    private final Element nextButton = new Element(By.xpath("//button[@type='submit']"));
    private final Element noThanksButton = new Element(By.xpath("//button[@type='button']/p[text()='No thanks']"));
    private final Element connectButton = new Element(By.xpath("//div[@data-tooltip-id='button-tooltip-3']/button[@type='button']"));
    private final Element confirmButton = new Element(By.xpath("//div[@data-tooltip-id='button-tooltip-2']/button[@type='button']"));

    // login site
    private final Element connectWalletButton = new Element(By.id("login_button__id"));
    private final Element shadowRootElement = new Element(By.cssSelector("onboard-v2"));
    private final String walletButtonContainerSelector = "section .wallet-button-container .name";

    public void connectWallet(String secretRecoveryPhrase, String password) {
        Driver.openNewWindow();
        Driver.switchToWindow(2);
        Driver.navigate("chrome-extension://egjidjbpglichdcondbcbdnbeeppgdph/home.html#/onboarding/");
        setupTrustWallet(secretRecoveryPhrase, password);

        Driver.refreshPage();
        connectWalletButton.click();

        handleWalletConnection();
    }

    private void setupTrustWallet(String secretRecoveryPhrase, String password) {
        importWalletButton.click();
        passwordTextbox.enter(password);
        confirmPasswordTextbox.enter(password);
        termsOfUseCheckbox.click();
        submitButton.click();

        String[] words = secretRecoveryPhrase.split(" ");
        List<WebElement> secretPhraseElements = secretPhraseTextbox.findElements();
        for (int i = 0; i < secretPhraseElements.size(); i++) {
            secretPhraseElements.get(i).sendKeys(words[i]);
        }

        nextButton.click();
        noThanksButton.click();
        Driver.closeWindow();
        Driver.switchToWindow(1);
    }

    private void handleWalletConnection() {
        List<WebElement> elements = shadowRootElement.findShadowElements(walletButtonContainerSelector);
        for (WebElement element : elements) {
            if (element.getText().contains("Trust Wallet")) {
                element.click();
                break;
            }
        }

        Driver.switchToWindow(2);
        connectButton.click();
        Driver.switchToWindow(1);
        Driver.switchToWindow(2);
        confirmButton.click();
        Driver.switchToWindow(1);
    }
}
