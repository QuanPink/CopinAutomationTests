package asia.decentralab.copin.pages;

import asia.decentralab.copin.element.Element;
import org.openqa.selenium.By;

public class BasePage {
    private final Element traderExplorerBtn = new Element(By.xpath("//header//a[@class='navlink-default']//span[normalize-space()='Traders Explorer']"));

    public void goToTraderExplorerPage() {
        traderExplorerBtn.click();
    }
}
