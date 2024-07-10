package asia.decentralab.copin.pages;

import asia.decentralab.copin.browser.Driver;
import asia.decentralab.copin.config.Constant;
import asia.decentralab.copin.data.enumdata.UserDropdownItem;
import asia.decentralab.copin.element.Element;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BasePage {
    /* Menu elements */
    private final Element homeButton = new Element(By.xpath(
            "//header//a[contains(@class, 'navlink-default')]//span[normalize-space()='Home']"));
    private final Element traderExplorerButton = new Element(By.xpath(
            "//header//a[@class='navlink-default']//span[normalize-space()='Traders Explorer']"));
    private final Element openInterestButton = new Element(By.xpath(
            "//header//a[@class='navlink-default']//span[normalize-space()='Open Interest']"));

    /* Search elements */
    private final Element searchTextbox = new Element(By.xpath(
            "//header//div/input[@placeholder='Search for wallets or transactions']"));
    private final Element searchResultButton = new Element(By.xpath(
            "//header//div[contains(@class, 'styled__SearchResult')]//button[div[contains(text(), 'View All')]]"));
    private final Element searchResultItem = new Element(By.xpath(
            "//header//div[contains(@class, 'SearchResult')]//button[@type='button']//a"));
    private final Element traderSearchResultMessage = new Element(By.xpath(
            "//header//div[contains(@class,'styled__SearchResult')]//div[contains(text(),'No Trader Found')]"));
    private final Element txHashSearchResultMessage = new Element(By.xpath(
            "//header//div[contains(@class,'styled__SearchResult')]//div[contains(text(),'No Transaction Found')]"));

    /* Trust Wallet elements */
    private final Element connectWalletButton = new Element(By.id("login_button__id"));
    private final Element importWalletButton = new Element(By.xpath(
            "//div[@role='button']//p[contains(text(),'Import or recover wallet')]"));
    private final Element passwordTextbox = new Element(By.xpath(
            "//div[contains(p,'New password')]//input[@type = 'password']"));
    private final Element confirmPasswordTextbox = new Element(By.xpath(
            "//div[contains(p,'Confirm new password')]//input[@type = 'password']"));
    private final Element termsOfUseCheckbox = new Element(By.xpath(
            "//input[@type='checkbox']"));
    private final Element submitButton = new Element(By.xpath(
            "//button[@type='submit']"));
    private final Element secretPhraseTextbox = new Element(By.xpath(
            "//input[@type='password']"));
    private final Element nextButton = new Element(By.xpath("//button[@type='submit']"));
    private final Element noThanksButton = new Element(By.xpath("//button[@type='button']/p[text()='No thanks']"));

    /* User dropdown menu*/
    private final Element userAddressBtn = new Element(By.xpath(
            "//button[contains(@class,'Dropdown__ToggleButton')]//div[contains(text(),'0x')]"));
    private final Element walletNotificationMessage = new Element(By.xpath(
            "//div[@class='Toastify']//div[@class = 'Toastify__toast-body']//div[@display='inline-block']"));

    /* Dynamic Element*/
    private final String dynamicUserDropdownItem = "//button[contains(@class,'Dropdown__DropdownItem')]//div[text()='%s']";

    @Step("Go to Home page")
    public void goToHomePage() {
        homeButton.click();
    }

    @Step("Go to Trader Explorer page")
    public void goToTraderExplorerPage() {
        traderExplorerButton.click();
    }

    @Step("Go to Open Interest page")
    public void goToOpenInterestPage() {
        openInterestButton.click();
    }

    @Step("Go to Connect Wallet page")
    public void goToConnectWalletPage() {
        connectWalletButton.click();
    }

    public void selectUserDropdownMenu(UserDropdownItem menuText) {
        new Element(By.xpath(String.format(dynamicUserDropdownItem, menuText.getValue()))).click();
    }

    @Step("Go to Wallet Management")
    public void goToWalletManagement() {
        userAddressBtn.click();
        selectUserDropdownMenu(UserDropdownItem.WALLET_MANAGEMENT);
    }

    @Step("Search trader")
    public void searchTrader(String value) {
        searchTextbox.enter(value);
    }

    @Step("Clear search keyword")
    public void clearValueSearch() {
        searchTextbox.clearValue();
    }

    @Step("View all search result")
    public void viewAllResultSearch() {
        searchResultButton.click();
    }

    @Step("Check the number search results are correct")
    public boolean isNumberSearchResultsCorrect() {
        int resultsSize = searchResultItem.findElements().size();
        String resultsCount = String.valueOf(resultsSize);
        String resultNumber = searchResultButton.getText();
        return resultNumber.contains(resultsCount);
    }

    @Step("Check the trader search results are correct")
    public boolean isTraderSearchResultsCorrect(String traderAddress) {
        List<WebElement> results = searchResultItem.findElements();
        if (results == null || results.isEmpty()) {
            return false;
        }

        Set<String> uniqueResults = new HashSet<>();
        for (WebElement result : results) {
            String resultValue = result.getAttribute("href");
            if (!resultValue.contains(traderAddress) || !uniqueResults.add(resultValue)) {
                return false;
            }
        }
        return true;
    }

    @Step("Check the message not find trader displayed")
    public boolean isMessageTraderNotFoundDisplay() {
        return traderSearchResultMessage.isDisplayed();
    }

    @Step("Check the message not find txHash displayed")
    public boolean isMessageTxHashNotFoundDisplay() {
        return txHashSearchResultMessage.isDisplayed();
    }

    @Step("Set up Trust Wallet account")
    public void setupTrustWallet(String secretRecoveryPhrase, String password) {
        Driver.openNewWindow();
        Driver.switchToWindow(2);
        Driver.navigate(Constant.TRUST_WALLET_URL_PATH);
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

    public void waitForWalletNotificationMessageExist() {
        walletNotificationMessage.waitForDisplay();
    }

    public void waitForWalletNotificationMessageNotExist() {
        walletNotificationMessage.waitForNotDisplay();
    }

    public String getWalletNotificationMessageContent() {
        return walletNotificationMessage.getText();
    }
}
