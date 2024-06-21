package asia.decentralab.copin.test;

import asia.decentralab.copin.config.Constant;
import asia.decentralab.copin.data.ProtocolData;
import asia.decentralab.copin.model.Positions;
import asia.decentralab.copin.utils.APIUtils;
import asia.decentralab.copin.utils.JsonUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DraftTest {
//    @Test(description = "Test Call API")
//    public void callApi(){
//        ProtocolData protocolData = JsonUtils.readJsonFile(Constant.GMX_DATA_FILE_PATH, ProtocolData.class);
//        try {
//            String response = APIUtils.sendRequest(protocolData);
//            System.out.println("API response: " + response);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    @DataProvider(name = "listOfTrader")
//    public Object[][] dpMethod(){
//        Positions positions = JsonUtils.readJsonFile(Constant.POSITIONS_FILE_PATH, Positions.class);
//        Positions.Position closedPosition = positions.getClosePosition();
//        Positions.Position liquidatePosition = positions.getLiquidatePosition();
//        return new Object[][] {{closedPosition}, {liquidatePosition}};
//    }
//
//    @Test (dataProvider = "listOfTrader")
//    public void myTest (Positions.Position position) {
//        System.out.println("Tx Hash of the position : " + position.getTxHash());
//    }
}