package asia.decentralab.copin.pages;

import asia.decentralab.copin.browser.Driver;
import asia.decentralab.copin.element.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BasePage {
    private final Element homeBtn = new Element(By.xpath("//header//a[contains(@class, 'navlink-default')]//span[normalize-space()='Home']"));
    private final Element traderExplorerBtn = new Element(By.xpath("//header//a[@class='navlink-default']//span[normalize-space()='Traders Explorer']"));
    private final Element openInterestBtn = new Element(By.xpath("//header//a[@class='navlink-default']//span[normalize-space()='Open Interest']"));
    private final Element searchTextbox = new Element(By.xpath("//header//div/input[@placeholder='Search for wallets or transactions']"));
    private final Element searchResultBtn = new Element(By.xpath("//header//div[contains(@class, 'styled__SearchResult')]//button[div[contains(text(), 'View All')]]"));
    private final String resultSearchItem = "//header//div[contains(@class, 'SearchResult')]//button[@type='button']//a";
    private final String resultSearchTxHashItemDetail = "//div[contains(@class, 'base__Box')]//button[div[contains(@class, 'base__Flex')]]";
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

    public void clearValueSearch() {
        searchTextbox.clearValue();
    }

    public void viewAllResultSearch() {
        searchResultBtn.click();
    }

    public boolean isNumberSearchResult() {
        Element resultsElement = new Element(By.xpath(resultSearchItem));
        int resultsSize = resultsElement.findElements().size();
        String resultsCount = String.valueOf(resultsSize);
        String resultNumber = searchResultBtn.getText();
        if (!resultNumber.contains(resultsCount)) {
            return false;
        }
        return true;
    }

    public boolean isSearchResultValid(String traderAddress) {
        Element resultsElement = new Element(By.xpath(resultSearchItem));
        List<WebElement> results = resultsElement.findElements();
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

    public boolean isSearchResultTxHash(String txHashPosition) {
        Element resultElement = new Element(By.xpath(resultSearchTxHashItemDetail));
        List<WebElement> results = resultElement.findElements();

        if (results == null || results.isEmpty()) {
            return false;
        }

        Set<String> uniqueResults = new HashSet<>();
        for (WebElement result : results) {
            result.click();
            String resultValue = Driver.getCurrentUrl();
            if (!resultValue.contains(txHashPosition) || !uniqueResults.add(resultValue)) {
                return false;
            }
            Driver.backToPreviousPage();
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
