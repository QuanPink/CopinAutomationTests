package asia.decentralab.copin.pages;

import asia.decentralab.copin.browser.Driver;
import asia.decentralab.copin.element.Element;
import io.qameta.allure.Step;
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
    private final Element resultSearchItem = new Element(By.xpath("//header//div[contains(@class, 'SearchResult')]//button[@type='button']//a"));
    private final Element resultSearchTxHashItemDetail = new Element(By.xpath("//div[contains(@class, 'base__Box')]//button[div[contains(@class, 'base__Flex')]]"));
    private final Element resultMessageSearchTrader = new Element(By.xpath("//header//div[contains(@class,'styled__SearchResult')]//div[contains(text(),'No Trader Found')]"));
    private final Element resultMessageSearchTxHash = new Element(By.xpath("//header//div[contains(@class,'styled__SearchResult')]//div[contains(text(),'No Transaction Found')]"));

    @Step("Go to Home page")
    public void goToHomePage() {
        homeBtn.click();
    }

    @Step("Go to Trader Explorer page")
    public void goToTraderExplorerPage() {
        traderExplorerBtn.click();
    }

    @Step("Go to Open Interest page")
    public void goToOpenInterestPage() {
        openInterestBtn.click();
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
        searchResultBtn.click();
    }

    @Step("Check number result search")
    public boolean isNumberSearchResult() {
        int resultsSize = resultSearchItem.findElements().size();
        String resultsCount = String.valueOf(resultsSize);
        String resultNumber = searchResultBtn.getText();
        return resultNumber.contains(resultsCount);
    }

    @Step("Check result search trader")
    public boolean isSearchResultValid(String traderAddress) {
        List<WebElement> results = resultSearchItem.findElements();
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

    @Step("Check result search txHash")
    public boolean isSearchResultTxHash(String txHashPosition) {
        List<WebElement> results = resultSearchTxHashItemDetail.findElements();
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

    @Step("Check message search trader")
    public boolean isNoResultsMessageTraderDisplayed() {
        return resultMessageSearchTrader.isDisplayed();
    }

    @Step("Check message search txHash")
    public boolean isNoResultsMessageTxHashDisplayed() {
        return resultMessageSearchTxHash.isDisplayed();
    }
}
