package asia.decentralab.copin.test.api;

import asia.decentralab.copin.models.PositionCalculationResult;
import asia.decentralab.copin.test.base.BaseApiTest;
import asia.decentralab.copin.test.utils.validators.BaseOrderValidator;
import asia.decentralab.copin.test.utils.validators.BasePositionValidator;
import asia.decentralab.copin.utils.ValidationUtils;
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

        ValidationUtils.assertNotNull(position, "Position should not be null");
        ValidationUtils.assertFalse(orders.isEmpty(), "Position should have orders");

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
        ValidationUtils.assertInRange(
                ValidationUtils.getDoubleValue(order, "collateralDeltaNumber"),
                0,
                100_000_000,
                "collateralDeltaNumber"
        );

        ValidationUtils.assertInRange(
                ValidationUtils.getDoubleValue(order, "collateralNumber"),
                0,
                100_000_000,
                "collateralNumber"
        );

        ValidationUtils.assertInRange(
                ValidationUtils.getDoubleValue(order, "leverage"),
                0.0000000001,
                1_000,
                "leverage"
        );

        if (isOpen) {
            double collateralDelta = ValidationUtils.getDoubleValue(order, "collateralDeltaNumber");
            double collateral = ValidationUtils.getDoubleValue(order, "collateralNumber");
            double sizeDelta = ValidationUtils.getDoubleValue(order, "sizeDeltaNumber");
            double size = ValidationUtils.getDoubleValue(order, "sizeNumber");

            ValidationUtils.assertCloseToValue(collateralDelta, collateral, 0.01, "OPEN order collateral consistency");
            ValidationUtils.assertCloseToValue(sizeDelta, size, 0.01, "OPEN order size consistency");
        }

        if (isClose) {
            double finalCollateral = ValidationUtils.getDoubleValue(order, "collateralNumber");
            double finalSize = ValidationUtils.getDoubleValue(order, "sizeNumber");

            ValidationUtils.assertEquals(finalCollateral, 0, "CLOSE order should result in zero collateral");
            ValidationUtils.assertEquals(finalSize, 0, "CLOSE order should result in zero size");
        }
    }

    private void validateGmxV2PositionCalculations(Map<String, Object> position, PositionCalculationResult calc) {
        BasePositionValidator.validatePositionCalculations(position, calc);

        double tolerance = 0.01; // 1%

        double positionCollateral = ValidationUtils.getDoubleValue(position, "collateral");
        ValidationUtils.assertInRange(
                positionCollateral,
                0,
                100_000_000,
                "collateral position consistency"
        );
        ValidationUtils.assertCloseToValue(
                positionCollateral,
                calc.collateral,
                tolerance,
                "position collateral"
        );

        double positionLeverage = ValidationUtils.getDoubleValue(position, "leverage");
        ValidationUtils.assertInRange(
                positionLeverage,
                0,
                1_000,
                "leverage position consistency"
        );
        ValidationUtils.assertCloseToValue(
                positionLeverage,
                calc.leverage,
                tolerance,
                "position leverage"
        );

//        String status = (String) position.get("status");
//        if ("CLOSE".equals(status)) {
//            double positionLastCollateral = ValidationUtils.getDoubleValue(position, "lastCollateral");
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
//            double positionLastSize = ValidationUtils.getDoubleValue(position, "lastSize");
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
//                    ValidationUtils.getDoubleValue(position, "lastSize") == 0,
//                    "GMX V2 open position should have positive lastSize"
//            );
//
//            ValidationUtils.assertTrue(
//                    ValidationUtils.getDoubleValue(position, "lastCollateral") == 0,
//                    "GMX V2 open position should have positive lastCollateral"
//            );
//        } else {
//            ValidationUtils.assertTrue(
//                    ValidationUtils.getDoubleValue(position, "lastSize") > 0,
//                    "GMX V2 open position should have positive lastSize"
//            );
//
//            ValidationUtils.assertTrue(
//                    ValidationUtils.getDoubleValue(position, "lastCollateral") > 0,
//                    "GMX V2 open position should have positive lastCollateral"
//            );
//        }
    }
}