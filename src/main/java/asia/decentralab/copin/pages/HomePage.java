package asia.decentralab.copin.pages;

import asia.decentralab.copin.elements.BaseElement;
import org.openqa.selenium.By;

public class HomePage extends BasePage {
    private final By sortFilterButton = By.xpath("//div[text()='TOP']/following-sibling::div//button");
    private final By timeFilterButton = By.xpath("//div[text()='IN']/following-sibling::div//button");
    private final By sourceFilterButton = By.xpath("//div[text()='SOURCE']/following-sibling::div//button");

    public void navigateToHomePage() {
        String baseUrl = config.getWebBaseUrl();
        openUrl(baseUrl);
    }

    public String getActiveSortFilterValue() {
        BaseElement activeSortFilter = new BaseElement(driver, sortFilterButton);
        return activeSortFilter.getText();
    }

    public String getTimeFilterValue() {
        BaseElement timeFilter = new BaseElement(driver, timeFilterButton);
        return timeFilter.getText();
    }

    public String getSourceFilterValue() {
        BaseElement sourceFilter = new BaseElement(driver, sourceFilterButton);
        return sourceFilter.getText();
    }

    public String getProtocolCount() {
        BaseElement protocolCount = new BaseElement(driver, By.cssSelector(".protocol-count"));
        return protocolCount.getText();
    }
}