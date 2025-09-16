package asia.decentralab.copin.test.api;

import asia.decentralab.copin.models.PositionCalculationResult;
import asia.decentralab.copin.test.base.BaseApiTest;
import asia.decentralab.copin.utils.ValidationUtils;
import asia.decentralab.copin.utils.calculators.PositionCalculator;
import asia.decentralab.copin.utils.validators.BaseOrderValidator;
import asia.decentralab.copin.utils.validators.BasePositionValidator;
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
            validateGmxV2Order(order);
        }

        // Validate position calculations
        PositionCalculationResult calculations = PositionCalculator.calculatePosition(orders);
        validateGmxV2PositionCalculations(position, calculations);
        validateGmxV2PositionBusinessRules(position, orders, calculations);
    }

    private void validateGmxV2Order(Map<String, Object> order) {
        // Use base validation first
        BaseOrderValidator.validateTradingOrders(order);

        // GMX V2 specific validations
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
                validateGmxV2MarginTransferOrder(order);
                break;
            default:
                throw new IllegalArgumentException("Unknown GMX V2 order type: " + orderType);
        }
    }

    /**
     * GMX V2 specific trading order validation
     */
    private void validateGmxV2TradingOrder(Map<String, Object> order, boolean isOpen, boolean isClose) {
        // GMX V2 specific constraints
        ValidationUtils.assertInRange(
                ValidationUtils.getDoubleValue(order, "collateralDeltaNumber"),
                0, 10000000, "GMX V2 collateralDeltaNumber"
        );

        ValidationUtils.assertInRange(
                ValidationUtils.getDoubleValue(order, "collateralNumber"),
                0, 10000000, "GMX V2 collateralNumber"
        );

        // GMX V2 specific validation for OPEN orders
        if (isOpen) {
            double collateralDelta = ValidationUtils.getDoubleValue(order, "collateralDeltaNumber");
            double collateral = ValidationUtils.getDoubleValue(order, "collateralNumber");
            double sizeDelta = ValidationUtils.getDoubleValue(order, "sizeDeltaNumber");
            double size = ValidationUtils.getDoubleValue(order, "sizeNumber");

            ValidationUtils.assertCloseToValue(collateralDelta, collateral, 0.01, "GMX V2 OPEN order collateral consistency");
            ValidationUtils.assertCloseToValue(sizeDelta, size, 0.01, "GMX V2 OPEN order size consistency");
        }

        // GMX V2 specific validation for CLOSE orders
        if (isClose) {
            double finalCollateral = ValidationUtils.getDoubleValue(order, "collateralNumber");
            double finalSize = ValidationUtils.getDoubleValue(order, "sizeNumber");

            ValidationUtils.assertEquals(finalCollateral, 0.0, "GMX V2 CLOSE order should result in zero collateral");
            ValidationUtils.assertEquals(finalSize, 0.0, "GMX V2 CLOSE order should result in zero size");
        }

        // GMX V2 leverage constraints
        ValidationUtils.assertInRange(
                ValidationUtils.getDoubleValue(order, "leverage"),
                0, 100, "GMX V2 leverage should be within reasonable range"
        );
    }

    /**
     * GMX V2 specific margin transfer order validation
     */
    private void validateGmxV2MarginTransferOrder(Map<String, Object> order) {
        ValidationUtils.assertInRange(
                ValidationUtils.getDoubleValue(order, "collateralDeltaNumber"),
                -1000000000, 1000000000, "GMX V2 collateralDeltaNumber for margin transfer"
        );

        ValidationUtils.assertInRange(
                ValidationUtils.getDoubleValue(order, "collateralNumber"),
                0, 1000000000, "GMX V2 collateralNumber"
        );
    }

    /**
     * GMX V2 specific position calculations validation
     */
    private void validateGmxV2PositionCalculations(Map<String, Object> position, PositionCalculationResult calc) {
        // Use base validation first
        BasePositionValidator.validatePositionCalculations(position, calc);

        // GMX V2 specific validations
        double tolerance = 0.01; // 1%

        // Additional GMX V2 specific checks
        ValidationUtils.assertCloseToValue(
                ValidationUtils.getDoubleValue(position, "collateral"),
                calc.collateral,
                tolerance,
                "GMX V2 position collateral"
        );

        ValidationUtils.assertCloseToValue(
                ValidationUtils.getDoubleValue(position, "leverage"),
                calc.leverage,
                tolerance,
                "GMX V2 position leverage"
        );

        // GMX V2 specific business rules for closed positions
        String status = (String) position.get("status");
        if ("CLOSE".equals(status)) {
            // Additional validation for closed GMX V2 positions
            ValidationUtils.assertCloseToValue(
                    ValidationUtils.getDoubleValue(position, "lastCollateral"),
                    0.0,
                    0.01,
                    "GMX V2 closed position lastCollateral should be near zero"
            );

            ValidationUtils.assertCloseToValue(
                    ValidationUtils.getDoubleValue(position, "lastSize"),
                    0.0,
                    0.01,
                    "GMX V2 closed position lastSize should be near zero"
            );
        }
    }

    /**
     * GMX V2 specific position business rules validation
     */
    private void validateGmxV2PositionBusinessRules(Map<String, Object> position,
                                                    List<Map<String, Object>> orders,
                                                    PositionCalculationResult calc) {
        // Use base validation first
        BasePositionValidator.validatePositionBusinessRules(position, orders, calc);

        // GMX V2 specific business rules
        String status = (String) position.get("status");

        if ("CLOSE".equals(status)) {
            // GMX V2 specific validation for closed positions
            Map<String, Object> lastOrder = orders.get(orders.size() - 1);
            String lastOrderType = (String) lastOrder.get("type");
            boolean isClose = Boolean.TRUE.equals(lastOrder.get("isClose"));

            ValidationUtils.assertEquals(isClose, true,
                    String.format("GMX V2 closed position requires last order to have isClose=true. " +
                            "Found: type=%s, isClose=%s", lastOrderType, isClose));

            boolean isValidClosingOrderType = "DECREASE".equals(lastOrderType) ||
                    "CLOSE".equals(lastOrderType) ||
                    "LIQUIDATE".equals(lastOrderType);

            ValidationUtils.assertTrue(isValidClosingOrderType,
                    String.format("GMX V2 closed position requires last order type to be DECREASE, CLOSE, or LIQUIDATE. " +
                            "Found: type=%s", lastOrderType));
        } else {
            // GMX V2 specific validation for open positions
            ValidationUtils.assertEquals(status, "OPEN", "GMX V2 position should have OPEN status");

            ValidationUtils.assertTrue(
                    ValidationUtils.getDoubleValue(position, "lastSize") > 0,
                    "GMX V2 open position should have positive lastSize"
            );

            ValidationUtils.assertTrue(
                    ValidationUtils.getDoubleValue(position, "lastCollateral") > 0,
                    "GMX V2 open position should have positive lastCollateral"
            );
        }

        // GMX V2 specific validation: Check for reasonable order sequencing
        validateGmxV2OrderSequencing(orders);
    }

    /**
     * GMX V2 specific order sequencing validation
     */
    private void validateGmxV2OrderSequencing(List<Map<String, Object>> orders) {
        if (orders.isEmpty()) return;

        // First order must be OPEN
        Map<String, Object> firstOrder = orders.get(0);
        ValidationUtils.assertTrue(
                Boolean.TRUE.equals(firstOrder.get("isOpen")),
                "GMX V2 first order must be OPEN"
        );

        // Check that we don't have multiple OPEN orders
        long openOrderCount = orders.stream()
                .mapToLong(order -> Boolean.TRUE.equals(order.get("isOpen")) ? 1 : 0)
                .sum();

        ValidationUtils.assertEquals(openOrderCount, 1L, "GMX V2 should have exactly one OPEN order");

        // Validate order types sequence
        for (int i = 0; i < orders.size(); i++) {
            Map<String, Object> order = orders.get(i);
            String orderType = (String) order.get("type");

            if (i == 0) {
                ValidationUtils.assertTrue(
                        "OPEN".equals(orderType),
                        "GMX V2 first order must be OPEN type"
                );
            }
        }
    }
}