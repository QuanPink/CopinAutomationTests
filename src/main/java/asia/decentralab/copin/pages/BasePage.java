package asia.decentralab.copin.pages;

import asia.decentralab.copin.browser.Driver;
import asia.decentralab.copin.element.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BasePage {
    private final Element homeBtn = new Element(By.xpath("//header//a[@class='navlink-default']//span[normalize-space()='Home']"));
    private final Element traderExplorerBtn = new Element(By.xpath("//header//a[@class='navlink-default']//span[normalize-space()='Traders Explorer']"));
    private final Element openInterestBtn = new Element(By.xpath("//header//a[@class='navlink-default']//span[normalize-space()='Open Interest']"));
    private final Element searchTextbox = new Element(By.xpath("//header//div/input[@placeholder='Search for wallets or transactions']"));
    private final String dynamicResultSearchTrader = "//header//div[contains(@class, 'SearchResult')]//button[@type='button']//a";
    private final String dynamicResultSearchTxHash = "//header//div[contains(@class, 'styled__SearchResult')]//button[not(div[contains(@class, 'base__TextWrapper')])]";
    private final Element resultMessageSearchTrader = new Element(By.xpath("//header//div[contains(@class,'styled__SearchResult')]//div[contains(text(),'No Trader Found')]"));
    private final Element resultMessageSearchTxHash = new Element(By.xpath("//header//div[contains(@class,'styled__SearchResult')]//div[contains(text(),'No Transaction Found')]"));

    public void goToHomePage() {
        homeBtn.click();
    }

    public void goToTraderExplorerPage() {
        traderExplorerBtn.click();
    }

    public void goToOpenInterestPage() {
        openInterestBtn.click();
    }

    public void searchTrader(String value) {
        searchTextbox.enter(value);
    }

    public boolean isSearchResultValid(String value) {
        Element resultsElement = new Element(By.xpath(dynamicResultSearchTrader));
        List<WebElement> results = resultsElement.findElements();

        if (results == null || results.isEmpty()) {
            return false;
        }

        Set<String> uniqueResults = new HashSet<>();
        for (WebElement result : results) {
            String resultValue = result.getAttribute("href");
            if (!resultValue.contains(value) || !uniqueResults.add(resultValue)) {
                return false;
            }
        }
        return true;
    }

    public boolean isSearchResultTxHash(String value) {
        Element resultElement = new Element(By.xpath(dynamicResultSearchTxHash));
        List<WebElement> results = resultElement.findElements();

        if (results == null || results.isEmpty()) {
            return false;
        }

        Set<String> uniqueResults = new HashSet<>();
        for (WebElement result : results) {
            result.click();
            String resultValue = Driver.getDriver().getCurrentUrl();
            if (!resultValue.contains(value) || !uniqueResults.add(resultValue)) {
                return false;
            }
        }
        return true;
    }

    public boolean isNoResultsMessageTraderDisplayed() {
        return resultMessageSearchTrader.isDisplayed();
    }

    public boolean isNoResultsMessageTxHashDisplayed() {
        return resultMessageSearchTxHash.isDisplayed();
    }
}
