package asia.decentralab.copin.pages;

import asia.decentralab.copin.element.Element;
import asia.decentralab.copin.model.CopyTrade;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class CopyTradeManagementPage extends BasePage {
    private final Element walletNameButton = new Element(By.xpath("//div[@width='[object Object]']/preceding-sibling::div//button"));
    private final Element removeCopyTradeButton = new Element(By.xpath("//button[contains(@class,'Dropdown__DropdownItem')][contains(.,'Remove')]"));

    private final String dynamicRunCheckbox = "//table//tr[contains(.,'%s')]/td[@data-table-key='status']//span[@class='slider round']";
    private final String dynamicTraderAddress = "//table//tr[contains(.,'%s')]/td[@data-table-key='account']//a/div[2]";
    private final String dynamicMargin = "//table//tr[contains(.,'%s')]/td[@data-table-key='volume']/div";
    private final String dynamicLeverage = "//table//tr[contains(.,'%s')]/td[@data-table-key='leverage']/div";
    private final String dynamicWalletNameDropdownItem = "//button[text()='%s'][contains(@class,'DropdownItem')]";
    private final String dynamicOptionButton = "//table//tr[contains(.,'%s')]/td[@data-table-key='id']//button[contains(@class,'Dropdown__ToggleButton')]";

    public void openWalletsDropdown() {
        walletNameButton.click();
    }

    public void switchWallet(String walletName) {
        openWalletsDropdown();
        new Element(By.xpath(String.format(dynamicWalletNameDropdownItem, walletName))).click();
    }

    public boolean isCopyTradeInformationCorrect(CopyTrade copyTrade) {
        boolean runStatus = new Element(By.xpath(String.format(dynamicRunCheckbox,
                copyTrade.getLabelCopyTradeName()))).isToggleSelected();
        String traderAddress = new Element(By.xpath(String.format(dynamicTraderAddress,
                copyTrade.getLabelCopyTradeName()))).getAttribute("data-trader-copy-deleted");
        String margin = new Element(By.xpath(String.format(dynamicMargin,
                copyTrade.getLabelCopyTradeName()))).getText().replaceAll("[^\\d.]", "");
        String leverage = new Element(By.xpath(String.format(dynamicLeverage,
                copyTrade.getLabelCopyTradeName()))).getText().replace("x", "");
        return (runStatus == copyTrade.isRunStatus()) &&
                (traderAddress.equals(copyTrade.getTraderAddress()))
                && Integer.parseInt(margin) == copyTrade.getMargin()
                && (leverage.equals(copyTrade.getLeverage().replace("x", "")));
    }

    @Step("Delete Copy trade")
    public void deleteCopyTrade(CopyTrade copyTrade) {
        new Element(By.xpath(String.format(dynamicOptionButton, copyTrade.getLabelCopyTradeName()))).click();
        removeCopyTradeButton.click();
        clickConfirmButton();
        waitForConfirmPopupNotDisplay();
    }

    @Step("Turn off the copy trade")
    public void turnOffCopyTrade(CopyTrade copyTrade) {
        new Element(By.xpath(String.format(dynamicRunCheckbox, copyTrade.getLabelCopyTradeName()))).click();
        clickConfirmButton();
        waitForNotificationMessageNotExist();
    }
}
