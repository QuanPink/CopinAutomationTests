package asia.decentralab.copin.pages;

import asia.decentralab.copin.browser.Driver;
import asia.decentralab.copin.data.enumdata.Token;
import asia.decentralab.copin.element.Element;
import asia.decentralab.copin.model.CopyTrade;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.util.List;

public class CreateCopyTradePage extends BasePage {

    private final Element labelTextbox = new Element(By.xpath("//input[@name='title']"));
    private final Element exchangeDropdown = new Element(By.xpath("//div[div[div[div[text()='Copy Wallet']]]]//div[contains(@class,'select__control')][div[div[div[img]]]]"));
    private final Element copyWalletCombobox = new Element(By.xpath(
            "//div[div[div[div[text()='Copy Wallet']]]]//div[contains(@class,'select__control')][div[div[contains(@class,'select__input-container')]]]"));
    private final Element marginTextbox = new Element(By.xpath("//div[text()='Margin']/parent::div/parent::div/following-sibling::div/input"));
    private final Element copyAllRadioButton = new Element(By.xpath("//input[@name='copyAll']/following-sibling::span"));
    private final Element excludeRadioButton = new Element(By.xpath("//input[@name='hasExclude']/following-sibling::span"));
    private final Element tradingPairTextbox = new Element(By.xpath("//div[contains(@class,'value-container--is-multi select')]"));
    private final Element followTraderRadioButton = new Element(By.xpath("//input[@name='copyAll']/following-sibling::span"));
    private final Element tradingPairCombobox = new Element(By.xpath("//div[div[div[div[div[text()='Trading Pairs']]]]]/following-sibling::div"));
    private final Element excludePairCombobox = new Element(By.xpath("//div[@display='flex']//div[contains(text(),'Select')]/../.."));
    private final Element deleteAllTradingPair = new Element(By.xpath("//div[@display='flex']//div[contains(@class,'value-container--is-multi select')]/following-sibling::div"));
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
    private final Element submitCopyTradeButton = new Element(By.xpath("//button[normalize-space()='Copy Trade']"));
    private final Element copyTraderHeader = new Element(By.xpath("//h5[div[span[text()='Copy Trader']]]"));
    private final Element termsCheckbox = new Element(By.xpath("//input[@name='agreement']"));
    private final Element copyTradeHeader = new Element(By.xpath("//span[text()='Copy Trader']"));

    private final String dynamicDropdownItem = "//div[contains(@class,'select__menu-list')]//div[text()='%s']";
    private final String dynamicLeverageValue = "//div[contains(@class,'SliderWrapper')]//span[contains(@class,'rc-slider-mark-text')][text()='%s']";


    @Step("Fill Copy trade setting")
    public void fillCopyTradeSetting(CopyTrade copyTrade) {
        labelTextbox.enter(copyTrade.getLabelCopyTradeName());
        exchangeDropdown.click();
        new Element(By.xpath(String.format(dynamicDropdownItem, copyTrade.getCopyWalletType().getShortName()))).click();
        copyWalletCombobox.click();
        new Element(By.xpath(String.format(dynamicDropdownItem, copyTrade.getCopyWalletName()))).click();
        marginTextbox.enter(String.valueOf(copyTrade.getMargin()));
        if (copyTrade.isFollowTrader()) {
            followTraderRadioButton.click();
            if (copyTrade.isExcludeToken()) {
                excludeRadioButton.click();
                selectPairs(excludePairCombobox, copyTrade.getTradingPairExclude());
            }
        } else {
            deleteAllTradingPair.click();
            selectPairs(tradingPairCombobox, copyTrade.getSelectedPairList());
        }
        new Element(By.xpath(String.format(dynamicLeverageValue, copyTrade.getLeverage()))).click();
        if (copyTrade.isReverseCopy()) {
            reverseRadioButton.click();
        }
        stopLossAndTakeProfitExpandButton.click();
        termsCheckbox.moveToElement();
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
    }

    private void selectPairs(Element combobox, List<Token> tokenList) {
        combobox.click();
        tokenList.forEach((token) -> {
            Driver.delay(1);
            new Element(By.xpath(String.format(dynamicDropdownItem, token.getValue()))).waitForDisplay();
            new Element(By.xpath(String.format(dynamicDropdownItem, token.getValue()))).click();
            Driver.delay(1);
        });
        copyTradeHeader.click();
    }

    public void clickTermCheckbox() {
        termsCheckbox.click();
    }

    public void clickSubmitCopyTradeButton() {
        submitCopyTradeButton.click();
    }

    @Step("Submit Copy trade setting")
    public void submitCopyTrade(CopyTrade copyTrade) {
        fillCopyTradeSetting(copyTrade);
        clickTermCheckbox();
        clickSubmitCopyTradeButton();
    }

    @Step("Create Copy trade setting")
    public void createCopyTrade(CopyTrade copyTrade) {
        submitCopyTrade(copyTrade);
        copyTraderHeader.waitForNotDisplay();
        waitForNotificationMessageExist();
    }
}
