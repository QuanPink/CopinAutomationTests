package asia.decentralab.copin.test.api;

import asia.decentralab.copin.models.PositionCalculationResult;
import asia.decentralab.copin.test.base.BaseApiTest;
import asia.decentralab.copin.utils.calculators.PositionCalculator;
import asia.decentralab.copin.utils.validators.OrderValidator;
import asia.decentralab.copin.utils.validators.PositionValidator;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class GmxV2ArbTest extends BaseApiTest {

    @DataProvider(name = "positionIds")
    public Object[][] getPositionIds() {
        Response positionsResponse = apiClient.getPositionsByFilter("gmx_v2", "/position/filter");
        List<String> positionIds = positionsResponse.jsonPath().getList("data.id", String.class);

        return positionIds.stream()
                .map(id -> new Object[]{id})
                .toArray(Object[][]::new);
    }

    @Test(dataProvider = "positionIds")
    @Description("Validate individual position data integrity")
    @Severity(SeverityLevel.CRITICAL)
    public void testPositionsDataIntegrity(String positionId) {
        Response positionDetailResponse = apiClient.getPositionDetail("gmx_v2", "/position/detail/{id}", positionId);

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
        PositionValidator.validatePositionBusinessRules(position, orders, calculations);
    }
}