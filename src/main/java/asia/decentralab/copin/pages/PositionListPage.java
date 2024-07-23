package asia.decentralab.copin.pages;

import asia.decentralab.copin.browser.Driver;
import asia.decentralab.copin.element.Element;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PositionListPage extends BasePage {
    /* Result search Elements */
    private final Element resultSearchTxHashItemDetail = new Element(By.xpath(
            "//div[contains(@class, 'base__Box')]//button[div[contains(@class, 'base__Flex')]]"));

    @Step("Check the txHash search results correct")
    public boolean isTxHashSearchResultsCorrect(String txHashPosition) {
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
}
