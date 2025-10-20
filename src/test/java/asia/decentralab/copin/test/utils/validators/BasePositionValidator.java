package asia.decentralab.copin.test.utils.validators;

import asia.decentralab.copin.models.PositionCalculationResult;
import asia.decentralab.copin.utils.ValidationUtils;

import java.util.List;
import java.util.Map;

public class BasePositionValidator {
    private static final double DEFAULT_TOLERANCE = 0.0001;

    public static void validatePositionFields(Map<String, Object> position) {
        ValidationUtils.assertNotNull(position.get("id"), "position id should not be null");
        ValidationUtils.assertNotNull(position.get("account"), "position account should not be null");
        ValidationUtils.assertNotNull(position.get("protocol"), "position protocol should not be null");
        ValidationUtils.assertNotNull(position.get("indexToken"), "position indexToken should not be null");
        ValidationUtils.assertNotNull(position.get("pair"), "position pair should not be null");
        ValidationUtils.assertNotNull(position.get("status"), "position status should not be null");
        ValidationUtils.assertNotNull(position.get("isLong"), "position isLong should not be null");
        ValidationUtils.assertNotNull(position.get("openBlockTime"), "position openBlockTime should not be null");
        ValidationUtils.assertNotNull(position.get("openBlockNumber"), "position openBlockNumber should not be null");
        ValidationUtils.assertNotNull(position.get("averagePrice"), "position averagePrice should not be null");
    }

    public static void validatePositionCalculations(Map<String, Object> position, PositionCalculationResult calc) {

        double positionSize = ValidationUtils.getDoubleValue(position, "size");
        ValidationUtils.assertInRange(
                positionSize,
                0,  // min
                100_000_000,  // max
                "position size"
        );
        ValidationUtils.assertCloseToValue(
                positionSize,
                calc.size,
                DEFAULT_TOLERANCE,
                "position size calculation"
        );

        double positionAveragePrice = ValidationUtils.getDoubleValue(position, "averagePrice");
        ValidationUtils.assertInRange(
                positionAveragePrice,
                0,
                1_000_000,
                "protocol average price"
        );
        ValidationUtils.assertCloseToValue(
                positionAveragePrice,
                calc.avgPrice,
                DEFAULT_TOLERANCE,
                "protocol average price calculation"
        );

        String status = (String) position.get("status");
        if ("CLOSE".equals(status)) {
            double positionRealisedPnl =  ValidationUtils.getDoubleValue(position, "realisedPnl");
            ValidationUtils.assertInRange(
                    positionRealisedPnl,
                    -100_000_000,
                    100_000_000,
                    "position realisedPnl"
            );
            ValidationUtils.assertCloseToValue(
                    positionRealisedPnl,
                    calc.realisedPnl,
                    DEFAULT_TOLERANCE,
                    "position realisedPnl calculation"
            );

            double positionPnl = ValidationUtils.getDoubleValue(position, "pnl");
            ValidationUtils.assertInRange(
                    positionPnl,
                    -100_000_000,
                    100_000_000,
                    "position pnl"
            );
            ValidationUtils.assertCloseToValue(
                    positionPnl,
                    calc.pnl,
                    DEFAULT_TOLERANCE,
                    "position pnl calculation"
            );

            double positionRealisedRoi =  ValidationUtils.getDoubleValue(position, "realisedRoi");
            ValidationUtils.assertInRange(
                    positionRealisedRoi,
                    -1_000,
                    10_000,
                    "position realisedRoi"
            );
            ValidationUtils.assertCloseToValue(
                    positionRealisedRoi,
                    calc.realisedRoi,
                    DEFAULT_TOLERANCE,
                    "position realisedRoi  calculation"
            );

            double positionRoi = ValidationUtils.getDoubleValue(position, "roi");
            ValidationUtils.assertInRange(
                    positionRoi,
                    -1_000,
                    10_000,
                    "position roi"
            );
            ValidationUtils.assertCloseToValue(
                    positionRoi,
                    calc.roi,
                    DEFAULT_TOLERANCE,
                    "position roi calculation"
            );
        } else {
            double  positionTotalDecreasePnl = ValidationUtils.getDoubleValue(position, "totalDecreasePnl");
            ValidationUtils.assertInRange(
                    positionTotalDecreasePnl,
                    -100_000_000,
                    100_000_000,
                    "position totalDecreasePnl"
            );
            ValidationUtils.assertCloseToValue(
                    positionTotalDecreasePnl,
                    calc.realisedPnl,
                    DEFAULT_TOLERANCE,
                    "position totalDecreasePnl calculation"
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

        double positionFee =  ValidationUtils.getDoubleValue(position, "fee");
        ValidationUtils.assertInRange(
                positionFee,
                -100_000_000,
                100_000_000,
                "position fee"
        );
        ValidationUtils.assertCloseToValue(
                positionFee,
                calc.fee,
                DEFAULT_TOLERANCE,
                "position fee"
        );
    }

    public static void validatePositionBusinessRules(Map<String, Object> position,
                                                     List<Map<String, Object>> orders,
                                                     PositionCalculationResult calc) {
        validatePositionFields(position);

        ValidationUtils.assertFalse(orders.isEmpty(), "Orders list cannot be empty");

        Map<String, Object> firstOrder = orders.get(0);
        String firstOrderType = (String) firstOrder.get("type");
        ValidationUtils.assertEquals(firstOrder.get("isOpen"), true, "First order should be OPEN");
        boolean isValidFirstOrderType = "OPEN".equals(firstOrderType) || "INCREASE".equals(firstOrderType);
        ValidationUtils.assertTrue(isValidFirstOrderType,
                String.format("First order type should be OPEN or INCREASE. Found: type=%s", firstOrderType));
        ValidationUtils.assertEquals(calc.isOpenCount, 1, "Should have exactly one OPEN order");

        Boolean positionIsLong = (Boolean) position.get("isLong");
        for (Map<String, Object> order : orders) {
            String orderType = (String) order.get("type");

            if ("MARGIN_TRANSFERRED".equals(orderType)) {
                continue;
            }

            ValidationUtils.assertEquals(
                    order.get("isLong"),
                    positionIsLong,
                    "All orders must have same isLong as position"
            );
        }

        Map<String, Object> lastOrder = orders.get(orders.size() - 1);
        String lastOrderType = (String) lastOrder.get("type");
        boolean isClose = Boolean.TRUE.equals(lastOrder.get("isClose"));

        String status = (String) position.get("status");
        boolean isValidClosingOrderType = "DECREASE".equals(lastOrderType) ||
                "CLOSE".equals(lastOrderType) ||
                "LIQUIDATE".equals(lastOrderType);

        if ("CLOSE".equals(status)) {
            ValidationUtils.assertEquals(isClose, true,
                    String.format("Closed position requires last order to have isClose=true. " +
                            "Found: type=%s, isClose=%s", lastOrderType, isClose));

            ValidationUtils.assertTrue(isValidClosingOrderType,
                    String.format("Closed position requires last order type to be DECREASE, CLOSE, or LIQUIDATE. " +
                            "Found: type=%s", lastOrderType));

            ValidationUtils.assertNotNull(position.get("closeBlockTime"),
                    "Closed position must have closeBlockTime");

            ValidationUtils.assertNotNull(position.get("closeBlockNumber"),
                    "Closed position must have closeBlockNumber");
        } else {
            if (isValidClosingOrderType) {
                Boolean lastOrderIsClose = (Boolean) lastOrder.get("isClose");
                ValidationUtils.assertNotNull(lastOrderIsClose,
                        "Order type " + lastOrderType + " must have isClose field");

                ValidationUtils.assertEquals(lastOrderIsClose, false,
                        "Open position with " + lastOrderType + " order must have isClose=false");
            }
        }
    }
}