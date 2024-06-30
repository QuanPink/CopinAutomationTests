package asia.decentralab.copin.pages;

import asia.decentralab.copin.element.Element;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BasePage {
    private final Element homeButton = new Element(By.xpath("//header//a[contains(@class, 'navlink-default')]//span[normalize-space()='Home']"));
    private final Element traderExplorerButton = new Element(By.xpath("//header//a[@class='navlink-default']//span[normalize-space()='Traders Explorer']"));
    private final Element openInterestButton = new Element(By.xpath("//header//a[@class='navlink-default']//span[normalize-space()='Open Interest']"));
    private final Element searchTextbox = new Element(By.xpath("//header//div/input[@placeholder='Search for wallets or transactions']"));
    private final Element searchResultButton = new Element(By.xpath("//header//div[contains(@class, 'styled__SearchResult')]//button[div[contains(text(), 'View All')]]"));
    private final Element searchResultItem = new Element(By.xpath("//header//div[contains(@class, 'SearchResult')]//button[@type='button']//a"));
    private final Element traderSearchResultMessage = new Element(By.xpath("//header//div[contains(@class,'styled__SearchResult')]//div[contains(text(),'No Trader Found')]"));
    private final Element txHashSearchResultMessage = new Element(By.xpath("//header//div[contains(@class,'styled__SearchResult')]//div[contains(text(),'No Transaction Found')]"));

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
}
