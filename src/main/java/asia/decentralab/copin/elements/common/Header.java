package asia.decentralab.copin.elements.common;

import asia.decentralab.copin.elements.BaseElement;
import asia.decentralab.copin.elements.Button;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Header extends BaseElement {
    // Locators
    private final By logoLocator = By.cssSelector(".navbar-brand");
    private final By traderExplorerLink = By.xpath("//a[text()='TRADER EXPLORER']");
    private final By openInterestLink = By.xpath("//a[text()='OPEN INTEREST']");
    private final By traderBoardLink = By.xpath("//a[text()='TRADER BOARD']");
    private final By searchInput = By.cssSelector(".search-input");
    private final By userProfileButton = By.cssSelector(".user-profile");

    private Button logo;
    private Button traderExplorer;
    private Button openInterest;
    private Button traderBoard;

    public Header(WebDriver driver) {
        super(driver, By.cssSelector("header"));
        initElements();
    }

    private void initElements() {
        logo = new Button(driver, logoLocator);
        traderExplorer = new Button(driver, traderExplorerLink);
        openInterest = new Button(driver, openInterestLink);
        traderBoard = new Button(driver, traderBoardLink);
    }

    public void clickLogo() {
        logo.click();
    }

    public void navigateToTraderExplorer() {
        traderExplorer.click();
    }

    public void navigateToOpenInterest() {
        openInterest.click();
    }

    public void navigateToTraderBoard() {
        traderBoard.click();
    }

    public boolean isUserLoggedIn() {
        try {
            return driver.findElement(userProfileButton).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
