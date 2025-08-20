package asia.decentralab.copin.utils.validators;

import asia.decentralab.copin.models.PositionCalculationResult;
import asia.decentralab.copin.utils.ValidationUtils;

import java.util.List;
import java.util.Map;

public class BasePositionValidator {
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
                ValidationUtils.getDoubleValue(position, "size"),
                calc.size,
                tolerance,
                "position size"
        );

        ValidationUtils.assertCloseToValue(
                ValidationUtils.getDoubleValue(position, "averagePrice"),
                calc.avgPrice,
                tolerance,
                "position average price"
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
        } else {
            ValidationUtils.assertCloseToValue(
                    ValidationUtils.getDoubleValue(position, "totalDecreasePnl"),
                    calc.realisedPnl,
                    tolerance,
                    "position totalDecreasePnl"
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
    }

    public static void validatePositionBusinessRules(Map<String, Object> position,
                                                     List<Map<String, Object>> orders,
                                                     PositionCalculationResult calc) {
        validatePositionFields(position);

        ValidationUtils.assertEquals(orders.get(0).get("isOpen"), true, "First order should be OPEN");
        ValidationUtils.assertEquals(calc.isOpenCount, 1, "Should have exactly one OPEN order");

        ValidationUtils.assertEquals(
                orders.get(0).get("isLong"),
                position.get("isLong"),
                "isLong should be consistent between first order and position"
        );

        Map<String, Object> lastOrder = orders.get(orders.size() - 1);
        String lastOrderType = (String) lastOrder.get("type");
        boolean isClose = Boolean.TRUE.equals(lastOrder.get("isClose"));

        ValidationUtils.assertEquals(isClose, true,
                String.format("Closed position requires last order to have isClose=true. " +
                        "Found: type=%s, isClose=%s", lastOrderType, isClose));

        boolean isValidClosingOrderType = "DECREASE".equals(lastOrderType) ||
                "CLOSE".equals(lastOrderType) ||
                "LIQUIDATE".equals(lastOrderType);

        ValidationUtils.assertTrue(isValidClosingOrderType,
                String.format("Closed position requires last order type to be DECREASE, CLOSE, or LIQUIDATE. " +
                        "Found: type=%s", lastOrderType));
    }
}
