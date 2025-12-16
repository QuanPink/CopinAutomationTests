package asia.decentralab.copin.test.base;

import asia.decentralab.copin.api.clients.PositionApiClient;
import asia.decentralab.copin.api.clients.TraderStatisticApiClient;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.path.json.config.JsonPathConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class BaseApiTest {
    private static final Logger logger = LoggerFactory.getLogger(BaseApiTest.class);
    protected PositionApiClient positionApiClient;
    protected TraderStatisticApiClient traderStatisticApiClient;

    @BeforeClass
    public void setUpApiTest() {
        logger.info("Setting up API test suite");

        RestAssured.config = RestAssured.config()
                .jsonConfig(JsonConfig.jsonConfig()
                        .numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE));

        // Initialize API client
        positionApiClient = new PositionApiClient();
        traderStatisticApiClient = new TraderStatisticApiClient();
        logger.info("API client initialized");
    }

    @AfterClass
    public void tearDownApiTest() {
        logger.info("API test suite completed");
    }
}
