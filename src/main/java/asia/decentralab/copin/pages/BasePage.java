package asia.decentralab.copin.pages;

import asia.decentralab.copin.data.ProtocolData;
import asia.decentralab.copin.data.enumdata.ApiType;
import asia.decentralab.copin.element.Element;
import asia.decentralab.copin.utils.APIUtils;
import org.openqa.selenium.By;

public class BasePage {
    private final Element traderExplorerBtn = new Element(By.xpath("//header//a[@class='navlink-default']//span[normalize-space()='Traders Explorer']"));

    public void goToTraderExplorerPage() {
        traderExplorerBtn.click();
    }

    public String callApi() {
        APIUtils apiUtils = new APIUtils();
        ProtocolData.ApiConfig gmxApiConfig = new ProtocolData.GMXApiConfig();
        try {
            // Convert method from string to ApiType
            ApiType methodType = ApiType.valueOf(gmxApiConfig.getMethod().toUpperCase());

            String response = apiUtils.callApi(
                    gmxApiConfig.getUrl(),
                    methodType,  // Use converted method type
                    gmxApiConfig.getHeaders(),
                    gmxApiConfig.getBody()
            );
            return response;
        } catch (Exception e) {  // Catch all exceptions
            e.printStackTrace();
            return null; // Return null or an appropriate error message in case of an exception
        }
    }
}
