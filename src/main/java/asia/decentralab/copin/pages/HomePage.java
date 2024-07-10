package asia.decentralab.copin.pages;

import asia.decentralab.copin.data.enumdata.SourceValue;
import asia.decentralab.copin.data.enumdata.StatisticValue;
import asia.decentralab.copin.data.enumdata.TimeValue;
import asia.decentralab.copin.element.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class HomePage extends BasePage {
    /* Filter trader Elements */
    private final Element topDropdownButton = new Element(By.xpath("//div[div[text()='Top']]//button"));
    private final Element inDropdownButton = new Element(By.xpath("//div[div[text()='In']]//button"));
    private final Element sourceDropdownButton = new Element(By.xpath("//div[div[text()='Source']]//button"));
    private final Element protocolLabel = new Element(By.xpath("//div[@id='home__header__wrapper']//button//span"));
    private final Element traderItem = new Element(By.xpath("//div[@id='home__traders__wrapper']/div/a"));

    /* Dynamic Elements */
    private final String dynamicProtocolItem = "//div[contains(@class, 'Dropdown__Menu')]//button[span[text()='%s']]";
    private final String dynamicFilterDropdownItem = "//button[contains(@class,'Dropdown__DropdownItem')][div[text()='%s']]";
    private final String dynamicCopyButton = "//div[@id='home__traders__wrapper']/div/a[%s]//button[text()='Copy']";

    public WebElement getRandomTrader() {
        List<WebElement> traders = traderItem.findElements();
        int randomTrader = ThreadLocalRandom.current().nextInt(1, 12 + 1);
        return traders.get(randomTrader);
    }

    public String getProtocolName() {
        return protocolLabel.getText();
    }

    public void switchSortValue(String value) {
        topDropdownButton.click();
        new Element(By.xpath(String.format(dynamicFilterDropdownItem, value))).click();
    }

    public void switchStatisticTime(String value) {
        inDropdownButton.click();
        new Element(By.xpath(String.format(dynamicFilterDropdownItem, value))).click();
    }

    public void switchProtocol(String value) {
        sourceDropdownButton.click();
        new Element(By.xpath(String.format(dynamicProtocolItem, value))).click();
    }

    public void filterTraderStatistic(StatisticValue top, TimeValue in, SourceValue source) {
        if (top != null) {
            switchSortValue(top.getValue());
        }
        if (in != null) {
            switchStatisticTime(in.getValue());
        }
        if (source != null) {
            switchProtocol(source.getValue());
        }
    }
}