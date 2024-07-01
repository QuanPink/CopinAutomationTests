package asia.decentralab.copin.pages;

import asia.decentralab.copin.browser.Driver;
import asia.decentralab.copin.element.Element;
import asia.decentralab.copin.utils.WaitUtils;
import io.github.sukgu.Shadow;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.Set;

public class LoginPage {
    //setup metamask
    private final Element termsOfUseCheckbox = new Element(By.xpath("//input[@id='onboarding__terms-checkbox']"));
    private final Element importWalletButton = new Element(By.xpath("//button[@data-testid='onboarding-import-wallet']"));
    private final Element iAgreeButton = new Element(By.xpath("//button[@data-testid='metametrics-i-agree']"));
    private final Element secretRecoveryPhraseTextbox = new Element(By.xpath("//input[@type='password']"));
    private final Element confirmSecretRecoveryPhraseButton = new Element(By.xpath("//button[@data-testid='import-srp-confirm']"));
    private final Element newPasswordTextbox = new Element(By.xpath("//input[@data-testid='create-password-new']"));
    private final Element confirmPasswordTextbox = new Element(By.xpath("//input[@data-testid='create-password-confirm']"));
    private final Element termsCreatePasswordCheckbox = new Element(By.xpath("//input[@data-testid='create-password-terms']"));
    private final Element importMyWalletButton = new Element(By.xpath("//button[@data-testid='create-password-import']"));
    private final Element gotItButton = new Element(By.xpath("//button[@data-testid='onboarding-complete-done']"));
    private final Element nextGuidButton = new Element(By.xpath("//button[@data-testid='pin-extension-next']"));
    private final Element doneGuidButton = new Element(By.xpath("//button[@data-testid='pin-extension-done']"));
    private final Element enableButton = new Element(By.xpath("//button[contains(text(),'Enable')]"));

    //login site
    private final Element connectWalletButton = new Element(By.id("login_button__id"));
    private final Element unlockPasswordTextbox = new Element(By.xpath("//input[@data-testid='unlock-password']"));
    private final Element unLockButton = new Element(By.xpath("//button[@data-testid='unlock-submit']"));
    private final Element nextButton = new Element(By.xpath("//button[@data-testid='page-container-footer-next']"));
    private final Element confirmButton = new Element(By.xpath("//button[contains(text(),'Confirm')]"));
    private final Element signButton = new Element(By.xpath("//button[contains(text(),'Sign')]"));
    private final Element confirmLoginButton = new Element(By.xpath("//button[contains(text(),'Confirm')]"));

    public void setupMetamaskAccount(String secretRecoveryPhrase, String password) throws InterruptedException {
        Thread.sleep(5000);
        Set<String> windowHandles = Driver.getDriver().getWindowHandles();

        if (windowHandles.size() > 2) {
            Driver.switchToWindow(3);
        } else {
            Driver.switchToWindow(2);
        }
        termsOfUseCheckbox.click();
        importWalletButton.click();
        iAgreeButton.click();

        String[] words = secretRecoveryPhrase.split(" ");
        List<WebElement> secretRecoveryPhraseElements = secretRecoveryPhraseTextbox.findElements();
        for (int i = 0; i < secretRecoveryPhraseElements.size(); i++) {
            secretRecoveryPhraseElements.get(i).sendKeys(words[i]);
        }
        confirmSecretRecoveryPhraseButton.click();

        newPasswordTextbox.enter(password);
        confirmPasswordTextbox.enter(password);
        termsCreatePasswordCheckbox.click();
        importMyWalletButton.click();
        gotItButton.click();
        nextGuidButton.click();
        doneGuidButton.click();
        enableButton.click();
    }

    public void connectWallet() throws InterruptedException {
        connectWalletButton.click();

        Shadow shadow = new Shadow(Driver.getDriver());
        WaitUtils.waiting().until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("onboard-v2")));
        List<WebElement> elements = shadow.findElements("section .wallet-button-container .name");
        for (WebElement element : elements) {
            if (element.getText().contains("MetaMask")) {
                element.click();
                break;
            }
        }
        confirmLoginMetaMask();
    }

    public void confirmLoginMetaMask() throws InterruptedException {
        Thread.sleep(5000);
        Set<String> windowHandles = Driver.getDriver().getWindowHandles();

        if (windowHandles.size() > 2) {
            Driver.switchToWindow(3);
            print();
        } else {
            Driver.switchToWindow(2);
            print();
            System.out.println(Driver.getDriver().getCurrentUrl() + " " + Driver.getDriver().getTitle());
        }
        System.out.println(Driver.getDriver().getCurrentUrl() + " " + Driver.getDriver().getTitle());
//        if (!nextButton.isDisplayed()) {
//            unlockPasswordTextbox.enter("123123123");
//            unLockButton.click();
//        }

//        WebElement nextButtonssss = Driver.getDriver().findElement(By.xpath("//button[@data-testid='page-container-footer-next']"));
//
//        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) Driver.getDriver();
//        javascriptExecutor.executeScript("arguments[0].click();", nextButtonssss);

        nextButton.click();
        confirmButton.click();
        Thread.sleep(10000);
        signButton.click();
        Driver.switchToWindow(1);
    }

    public void print() {
        Set<String> windowHandles = Driver.getDriver().getWindowHandles();
        for (String handle : windowHandles) {
            Driver.getDriver().switchTo().window(handle);

            String url = Driver.getDriver().getCurrentUrl();
            String title = Driver.getDriver().getTitle();

            System.out.println("Window Handle: " + handle);
            System.out.println("Title: " + title);
            System.out.println("URL: " + url);
        }
    }
}