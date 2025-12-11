package asia.decentralab.copin.test.api;

import asia.decentralab.copin.models.PositionCalculationResult;
import asia.decentralab.copin.test.base.BaseApiTest;
import asia.decentralab.copin.test.utils.validators.BaseOrderValidator;
import asia.decentralab.copin.test.utils.validators.BasePositionValidator;
import static asia.decentralab.copin.utils.ValidationUtils.*;
import static asia.decentralab.copin.utils.MapUtils.getDouble;
import static asia.decentralab.copin.utils.MapUtils.getInt;
import asia.decentralab.copin.utils.calculators.PositionCalculator;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class GmxV2ArbTest extends BaseApiTest {

    @DataProvider(name = "positionIds")
    public Object[][] getPositionIds() {
        Response positionsResponse = positionApiClient.getPositionsByFilter("gmx_v2", "/position/filter");
        List<String> positionIds = positionsResponse.jsonPath().getList("data.id", String.class);

        return positionIds.stream()
                .map(id -> new Object[]{id})
                .toArray(Object[][]::new);
    }

    @Test(dataProvider = "positionIds")
    @Description("Validate individual position data integrity")
    @Severity(SeverityLevel.CRITICAL)
    public void testPositionsDataIntegrity(String positionId) {
        Response positionDetailResponse = positionApiClient.getPositionDetail("gmx_v2", "/position/detail/{id}", positionId);

        Map<String, Object> position = positionDetailResponse.jsonPath().getMap("");
        List<Map<String, Object>> orders = positionDetailResponse.jsonPath().getList("orders");

        assertNotNull(position, "Position should not be null");
        assertFalse(orders.isEmpty(), "Position should have orders");

        for (Map<String, Object> order : orders) {
            validateGmxV2Order(order);
        }

        // Validate position calculations
        PositionCalculationResult calculations = PositionCalculator.calculatePosition(orders);
        validateGmxV2PositionCalculations(position, calculations);
        validateGmxV2PositionBusinessRules(position, orders, calculations);
    }

    private void validateGmxV2Order(Map<String, Object> order) {
        BaseOrderValidator.validateTradingOrders(order);

        String orderType = (String) order.get("type");
        boolean isOpen = Boolean.TRUE.equals(order.get("isOpen"));
        boolean isClose = Boolean.TRUE.equals(order.get("isClose"));

        switch (orderType) {
            case "OPEN":
            case "INCREASE":
            case "DECREASE":
            case "CLOSE":
            case "LIQUIDATE":
                validateGmxV2TradingOrder(order, isOpen, isClose);
                break;
            case "MARGIN_TRANSFERRED":
            default:
                throw new IllegalArgumentException("Unknown GMX V2 order type: " + orderType);
        }
    }

    private void validateGmxV2TradingOrder(Map<String, Object> order, boolean isOpen, boolean isClose) {
        String account = (String) order.get("account");

        assertInRange(getDouble(order, "collateralDeltaNumber"), 0, 100_000_000,
                "collateralDeltaNumber", account
        );

        assertInRange(getDouble(order, "collateralNumber"), 0, 100_000_000,
                "collateralNumber", account
        );

        assertInRange(getDouble(order, "leverage"), 0.0000000001, 1_000,
                "leverage", account
        );

        if (isOpen) {
            double collateralDelta = getDouble(order, "collateralDeltaNumber");
            double collateral = getDouble(order, "collateralNumber");
            double sizeDelta = getDouble(order, "sizeDeltaNumber");
            double size = getDouble(order, "sizeNumber");

            assertCloseToValue(collateralDelta, collateral, 0.01, "OPEN order collateral consistency", account);
            assertCloseToValue(sizeDelta, size, 0.01, "OPEN order size consistency", account);
        }

        if (isClose) {
            double finalCollateral = getDouble(order, "collateralNumber");
            double finalSize = getDouble(order, "sizeNumber");

            assertEquals(finalCollateral, 0, "CLOSE order should result in zero collateral");
            assertEquals(finalSize, 0, "CLOSE order should result in zero size");
        }
    }

    private void validateGmxV2PositionCalculations(Map<String, Object> position, PositionCalculationResult calc) {
        BasePositionValidator.validatePositionCalculations(position, calc);

        String account = (String) position.get("account");
        double tolerance = 0.01; // 1%

        double positionCollateral = getDouble(position, "collateral");
        assertInRange(positionCollateral, 0, 100_000_000,
                "collateral position consistency", account
        );
        assertCloseToValue(positionCollateral, calc.collateral,
                tolerance, "position collateral", account
        );

        double positionLeverage = getDouble(position, "leverage");
        assertInRange(positionLeverage, 0, 1_000,
                "leverage position consistency", account
        );
        assertCloseToValue(positionLeverage, calc.leverage,
                tolerance, "position leverage", account
        );

//        String status = (String) position.get("status");
//        if ("CLOSE".equals(status)) {
//            double positionLastCollateral = getDouble(position, "lastCollateral");
//            ValidationUtils.assertInRange(
//                    positionLastCollateral,
//                    0,
//                    100_000_000,
//                    "lastCollateral position consistency"
//            );
//            ValidationUtils.assertCloseToValue(
//                    positionLastCollateral,
//                    0,
//                    0.01,
//                    "Closed position lastCollateral should be near zero"
//            );
//
//            double positionLastSize = getDouble(position, "lastSize");
//            ValidationUtils.assertInRange(
//                    positionLastSize,
//                    0,
//                    100_000_000,
//                    "lastSize position consistency"
//            );
//            ValidationUtils.assertCloseToValue(
//                    positionLastSize,
//                    0,
//                    0.01,
//                    "Closed position lastSize should be near zero"
//            );
//        }
    }

    private void validateGmxV2PositionBusinessRules(Map<String, Object> position,
                                                    List<Map<String, Object>> orders,
                                                    PositionCalculationResult calc) {

        BasePositionValidator.validatePositionBusinessRules(position, orders, calc);

//        String status = (String) position.get("status");
//        if ("CLOSE".equals(status)) {
//            ValidationUtils.assertTrue(
//                    getDouble(position, "lastSize") == 0,
//                    "GMX V2 open position should have positive lastSize"
//            );
//
//            ValidationUtils.assertTrue(
//                    getDouble(position, "lastCollateral") == 0,
//                    "GMX V2 open position should have positive lastCollateral"
//            );
//        } else {
//            ValidationUtils.assertTrue(
//                    getDouble(position, "lastSize") > 0,
//                    "GMX V2 open position should have positive lastSize"
//            );
//
//            ValidationUtils.assertTrue(
//                    getDouble(position, "lastCollateral") > 0,
//                    "GMX V2 open position should have positive lastCollateral"
//            );
//        }
    }
}