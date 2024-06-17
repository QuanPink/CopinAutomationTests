package asia.decentralab.copin.test;

import asia.decentralab.copin.config.Constant;
import asia.decentralab.copin.data.ProtocolData;
import asia.decentralab.copin.utils.APIUtils;
import asia.decentralab.copin.utils.JsonUtils;
import org.testng.annotations.Test;

public class DraftTest {
    @Test(description = "Test Call API")
    public void callApi(){
        ProtocolData protocolData = JsonUtils.readJsonFile(Constant.GMX_DATA_FILE_PATH, ProtocolData.class);
        try {
            String response = APIUtils.sendRequest(protocolData);
            System.out.println("API response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}