package asia.decentralab.copin.utils.validators;

import asia.decentralab.copin.models.PositionCalculationResult;
import asia.decentralab.copin.utils.ValidationUtils;
import java.util.List;
import java.util.Map;

public class PositionValidator {

    public static void validatePositionFields(Map<String, Object> position) {
        ValidationUtils.assertNotNull(position.get("id"), "position id should not be null");
        ValidationUtils.assertNotNull(position.get("account"), "position account should not be null");
        ValidationUtils.assertNotNull(position.get("indexToken"), "position indexToken should not be null");
        ValidationUtils.assertNotNull(position.get("status"), "position status should not be null");
        ValidationUtils.assertNotNull(position.get("isLong"), "position isLong should not be null");
        ValidationUtils.assertNotNull(position.get("pair"), "position pair should not be null");
        ValidationUtils.assertNotNull(position.get("openBlockTime"), "position openBlockTime should not be null");
    }

    public static void validatePositionCalculations(Map<String, Object> position, PositionCalculationResult calc) {
        double tolerance = 0.01; // 1%

        ValidationUtils.assertCloseToValue(
                ValidationUtils.getDoubleValue(position, "collateral"),
                calc.collateral,
                tolerance,
                "position collateral"
        );

        ValidationUtils.assertCloseToValue(
                ValidationUtils.getDoubleValue(position, "lastCollateral"),
                calc.lastCollateral,
                tolerance,
                "position lastCollateral"
        );

        ValidationUtils.assertCloseToValue(
                ValidationUtils.getDoubleValue(position, "size"),
                calc.size,
                tolerance,
                "position size"
        );

        ValidationUtils.assertCloseToValue(
                ValidationUtils.getDoubleValue(position, "lastSize"),
                calc.lastSize,
                tolerance,
                "position lastSize"
        );

        ValidationUtils.assertCloseToValue(
                ValidationUtils.getDoubleValue(position, "averagePrice"),
                calc.avgPrice,
                tolerance,
                "position average price"
        );

        ValidationUtils.assertCloseToValue(
                ValidationUtils.getDoubleValue(position, "leverage"),
                calc.leverage,
                tolerance,
                "position leverage"
        );

        String status = (String) position.get("status");
        if ("CLOSE".equals(status)) {
            ValidationUtils.assertCloseToValue(
                    ValidationUtils.getDoubleValue(position, "realisedPnl"),
                    calc.realisedPnl,
                    tolerance,
                    "position realisedPnl"
            );

            ValidationUtils.assertCloseToValue(
                    ValidationUtils.getDoubleValue(position, "pnl"),
                    calc.pnl,
                    tolerance,
                    "position pnl"
            );

            ValidationUtils.assertCloseToValue(
                    ValidationUtils.getDoubleValue(position, "realisedRoi"),
                    calc.realisedRoi,
                    tolerance,
                    "position realisedRoi"
            );

            ValidationUtils.assertCloseToValue(
                    ValidationUtils.getDoubleValue(position, "roi"),
                    calc.roi,
                    tolerance,
                    "position roi"
            );
        }else {
            ValidationUtils.assertCloseToValue(
                    ValidationUtils.getDoubleValue(position, "totalDecreasePnl"),
                    calc.realisedPnl,
                    tolerance,
                    "position realisedPnl"
            );
        }

        ValidationUtils.assertEquals(
                ValidationUtils.getIntValue(position, "orderCount"),
                calc.orderCount,
                "position orderCount"
        );

        ValidationUtils.assertEquals(
                ValidationUtils.getIntValue(position, "orderIncreaseCount"),
                calc.orderIncreaseCount,
                "position orderIncreaseCount"
        );

        ValidationUtils.assertEquals(
                ValidationUtils.getIntValue(position, "orderDecreaseCount"),
                calc.orderDecreaseCount,
                "position orderDecreaseCount"
        );

        ValidationUtils.assertCloseToValue(
                ValidationUtils.getDoubleValue(position, "fee"),
                calc.fee,
                tolerance,
                "position fee"
        );

        ValidationUtils.assertCloseToValue(
                ValidationUtils.getDoubleValue(position, "funding"),
                calc.funding,
                tolerance,
                "position funding"
        );
    }

    public static void validatePositionBusinessRules(Map<String, Object> position,
                                                     List<Map<String, Object>> orders,
                                                     PositionCalculationResult calc) {
        validatePositionFields(position);

        // First order should be OPEN
        ValidationUtils.assertEquals(orders.get(0).get("isOpen"), true, "First order should be OPEN");
        ValidationUtils.assertEquals(calc.isOpenCount, 1, "Should have exactly one OPEN order");

        // isLong consistency
        ValidationUtils.assertEquals(
                orders.get(0).get("isLong"),
                position.get("isLong"),
                "isLong should be consistent between first order and position"
        );

        String status = (String) position.get("status");
        if ("CLOSE".equals(status)) {
            ValidationUtils.assertEquals(status, "CLOSE", "Position should have CLOSE status");

            // Last order should be CLOSE or LIQUIDATE
            Map<String, Object> lastOrder = orders.get(orders.size() - 1);
//        String expectedLastOrderType = Boolean.TRUE.equals(position.get("isLiquidate")) ? "LIQUIDATE" : "CLOSE";
//        ValidationUtils.assertEquals(
//                lastOrder.get("type"),
//                expectedLastOrderType,
//                "Last order type should match position liquidation status"
//        );
            ValidationUtils.assertEquals(
                    lastOrder.get("isClose"),
                    true,
                    "Last order should be marked as close"
            );

            // Final values should be zero or near zero
            ValidationUtils.assertCloseToValue(
                    ValidationUtils.getDoubleValue(position, "lastCollateral"),
                    0.0,
                    0.01,
                    "Closed position lastCollateral should be near zero"
            );

            ValidationUtils.assertCloseToValue(
                    ValidationUtils.getDoubleValue(position, "lastSize"),
                    0.0,
                    0.01,
                    "Closed position lastSize should be near zero"
            );
        }else{
            ValidationUtils.assertEquals(status, "OPEN", "Position should have OPEN status");

            // Open position should have positive size and collateral
            ValidationUtils.assertTrue(
                    ValidationUtils.getDoubleValue(position, "lastSize") > 0,
                    "Open position should have positive lastSize"
            );

            ValidationUtils.assertTrue(
                    ValidationUtils.getDoubleValue(position, "lastCollateral") > 0,
                    "Open position should have positive lastCollateral"
            );
        }
    }
}