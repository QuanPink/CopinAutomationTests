package asia.decentralab.copin.pages;

import asia.decentralab.copin.element.Element;
import asia.decentralab.copin.model.CopyTrade;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class CreateCopytradePage extends BasePage {

    private final Element labelTextbox = new Element(By.xpath("//input[@name='title']"));
    private final Element copyWalletCombobox = new Element(By.xpath(
            "//div[contains(@class,'select__value-container')]/div[contains(@class,'select__input-container')]/preceding-sibling::div[contains(@class,'select__single-value')]"));
    private final Element exchangeDropdown = new Element(By.xpath("//div[contains(@class,'select__single-value')]//div[text()='%s']"));
    private final Element marginTextbox = new Element(By.xpath("//div[text()='Margin']/parent::div/parent::div/following-sibling::div/input"));
    private final Element copyAllRadioButton = new Element(By.xpath("//input[@name='copyAll']/following-sibling::span"));
    private final Element excludeRadioButton = new Element(By.xpath("//input[@name='hasExclude']/following-sibling::span"));
    private final Element tradingPairTextbox = new Element(By.xpath("//div[contains(@class,'value-container--is-multi select')]"));
    private final Element deleteAllTradingPair = new Element(By.xpath("//div[contains(@class,'value-container--is-multi select')]/following-sibling::div"));
    private final Element leverageSlideBar = new Element(By.xpath("//div[@class='rc-slider-step']"));
    private final Element reverseRadioButton = new Element(By.xpath("//input[@name='reverseCopy']/following-sibling::span"));
    private final Element stopLossAndTakeProfitExpandButton = new Element(By.xpath("//div[text()='Stop Loss / Take Profit']/parent::div"));
    private final Element stopLossTextbox = new Element(By.xpath("//div[text()='Stop Loss (Recommended)']/parent::div/parent::div/following-sibling::div/input"));
    private final Element takeProfitTextbox = new Element(By.xpath("//div[text()='Take Profit']/parent::div/parent::div/following-sibling::div/input"));
    private final Element advanceSettingsExpandButton = new Element(By.xpath("//div[text()='Advance Settings']/parent::div"));
    private final Element maxMarginPerPositionTextbox = new Element(By.xpath("//div[text()='Max Margin Per Position']/parent::div/parent::div/following-sibling::div/input"));
    private final Element marginProtectionTextbox = new Element(By.xpath("//div[text()='Margin Protection']/parent::div/parent::div/following-sibling::div/input"));
    private final Element skipLowerLeverageRadioButton = new Element(By.xpath("//input[@name='skipLowLeverage']/following-sibling::span"));
    private final Element lowerLeverageTextbox = new Element(By.xpath("//div[text()='Low Leverage']/parent::div/parent::div/following-sibling::div/input"));
    private final Element skipLowerCollateralRadioButton = new Element(By.xpath("//input[@name='skipLowCollateral']/following-sibling::span"));
    private final Element lowerCollateralTextbox = new Element(By.xpath("//div[text()='Low Collateral']/parent::div/parent::div/following-sibling::div/input"));
    private final Element termsCheckbox= new Element(By.xpath("//div[@class='checkbox']"));
    private final Element copyTradeButton= new Element(By.xpath("//button[normalize-space()='Copy Trade']"));

    private final String dynamicDropdownItem = "//div[contains(@class,'select__menu') AND contains(@id,'react-select')]//div[text()='%s']";

    @Step("Create copy trade")
    public void createCopyTrade(CopyTrade copyTrade) {
        labelTextbox.enter(copyTrade.getLabelCopyTradeName());
        exchangeDropdown.click();
        new Element(By.xpath(String.format(dynamicDropdownItem, copyTrade.getCopyWalletType().getShortName()))).click();
        copyWalletCombobox.click();;
        new Element(By.xpath(String.format(dynamicDropdownItem, copyTrade.getCopyWalletName()))).click();
        marginTextbox.enter(String.valueOf(copyTrade.getMargin()));
        if (copyTrade.isFollowTrader()) {
            copyAllRadioButton.click();
        }
        tradingPairTextbox.enter(copyTrade.getTradingPair().toString());
        leverageSlideBar.enter(String.valueOf(copyTrade.getLeverage()));
        if (copyTrade.isReverseCopy()) {
            reverseRadioButton.click();
        }
        stopLossAndTakeProfitExpandButton.click();
        stopLossTextbox.enter(copyTrade.getStopLoss());
        takeProfitTextbox.enter(copyTrade.getTakeProfit());
        advanceSettingsExpandButton.click();
        maxMarginPerPositionTextbox.enter(String.valueOf(copyTrade.getMaxMarginPerPosition()));
        marginProtectionTextbox.enter(String.valueOf(copyTrade.getMarginProtection()));
        if (copyTrade.isSkipLowerLeveragePosition()) {
            skipLowerLeverageRadioButton.click();
            lowerLeverageTextbox.enter(String.valueOf(copyTrade.getLowLeverage()));
        }
        if (copyTrade.isSkipLowerCollateralPosition()) {
            skipLowerCollateralRadioButton.click();
            lowerCollateralTextbox.enter(String.valueOf(copyTrade.getLowCollateral()));
        }
        termsCheckbox.click();
        copyTradeButton.click();
    }
}
