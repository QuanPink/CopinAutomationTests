package asia.decentralab.copin.test;

import asia.decentralab.copin.config.BaseUrlConfig;
import asia.decentralab.copin.config.endpoints.PositionsByProtocolReq;
import asia.decentralab.copin.model.PositionStatistics;
import asia.decentralab.copin.utils.APIUtils;
import asia.decentralab.copin.utils.JsonUtils;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.Instant;

public class PositionChecker {
    private final int maxDate = 1;

    @DataProvider(name = "Protocol list")
    public Object[][] protocols() {
        return new Object[][]{{"EQUATION_ARB"}, {"GMX"}, {"GMX_V2"}, {"GNS"}, {"HMX_ARB"}, {"LEVEL_ARB"}, {"MUX_ARB"},
                {"MYX_ARB"}, {"VELA_ARB"}, {"YFX_ARB"}, {"AVANTIS_BASE"}, {"SYNTHETIX_V3"}, {"LOGX_BLAST"},
                {"APOLLOX_BNB"}, {"LEVEL_BNB"}, {"KTX_MANTLE"}, {"LOGX_MODE"}, {"CYBERDEX"}, {"DEXTORO"}, {"KWENTA"},
                {"POLYNOMIAL"}, {"GNS_POLY"}, {"ROLLIE_SCROLL"}, {"KILOEX_OPBNB"}, {"MUMMY_FANTOM"}, {"MORPHEX_FANTOM"}};
    }

    @Test(dataProvider = "Protocol list")
    public void positionCheck(String protocol) throws Exception {
        PositionsByProtocolReq requestPayload = new PositionsByProtocolReq(
                BaseUrlConfig.PROD_BASE_URL, protocol);

        Response response = APIUtils.sendPostRequest(
                requestPayload.getBaseUrl(),
                requestPayload.getApiEndpoints().getPath(),
                requestPayload.getApiEndpoints().getRequestDetails());

        PositionStatistics positionStatistics = JsonUtils.fromJson(response.getBody().asString(), PositionStatistics.class);

        if (positionStatistics.getData() == null || positionStatistics.getData().isEmpty()) {
            throw new Exception("No position data available for protocol: " + protocol);
        }

        String lastTimePositionStr = positionStatistics.getData().get(0).getStatus().equals("OPEN")
                ? positionStatistics.getData().get(0).getOpenBlockTime()
                : positionStatistics.getData().get(0).getCloseBlockTime();

        Instant now = Instant.now();
        Instant lastTimePosition = Instant.parse(lastTimePositionStr);
        Duration duration = Duration.between(lastTimePosition, now);
        if (duration.toDays() > maxDate) {
            throw new Exception("Last trade less than " + maxDate + " days: " + lastTimePositionStr);
        }
    }
}
