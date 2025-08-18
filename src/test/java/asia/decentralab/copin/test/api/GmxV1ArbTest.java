package asia.decentralab.copin.test.api;

import asia.decentralab.copin.api.clients.FlexibleApiClient;
import asia.decentralab.copin.models.PositionCalculationResult;
import asia.decentralab.copin.test.base.BaseApiTest;
import asia.decentralab.copin.utils.AuthTokenProvider;
import asia.decentralab.copin.utils.calculators.PositionCalculator;
import asia.decentralab.copin.utils.validators.OrderValidator;
import asia.decentralab.copin.utils.validators.PositionValidator;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class GmxV1ArbTest extends BaseApiTest {
    private FlexibleApiClient apiClient;

    @BeforeClass
    public void setUp() {
        apiClient = new FlexibleApiClient();
        String authToken = AuthTokenProvider.getInstance().getToken();
        apiClient.setAuthToken(authToken);
    }

    @Test
    @Description("Validate GMX V1 ARB positions and orders data integrity")
    @Severity(SeverityLevel.CRITICAL)
    public void testPositionsDataIntegrity() {
        // Get positions
        Response positionsResponse = apiClient.getPositionsByFilter("gns", "/position/filter");
        List<String> positionIds = positionsResponse.jsonPath().getList("data.id", String.class);

        Assert.assertFalse(positionIds.isEmpty(), "Should have at least one position");

        // Validate each position
        for (String positionId : positionIds) {
            Response positionDetailResponse = apiClient.getPositionDetail("gns", "/position/detail/{id}", positionId);

            Map<String, Object> position = positionDetailResponse.jsonPath().getMap("");
            List<Map<String, Object>> orders = positionDetailResponse.jsonPath().getList("orders");

            Assert.assertNotNull(position, "Position should not be null");
            Assert.assertFalse(orders.isEmpty(), "Position should have orders");

            // Validate individual orders
            for (Map<String, Object> order : orders) {
                OrderValidator.validateOrderByType(order);
            }

            // Validate position calculations
            PositionCalculationResult calculations = PositionCalculator.calculatePosition(orders);
            PositionValidator.validatePositionCalculations(position, calculations);
            PositionValidator.validatePositionBusinessRules(position, position.get("orders"), calculations);
        }
    }
}