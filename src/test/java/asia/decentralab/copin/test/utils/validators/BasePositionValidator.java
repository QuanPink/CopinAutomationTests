package asia.decentralab.copin.test.utils.validators;

import asia.decentralab.copin.models.PositionCalculationResult;

import static asia.decentralab.copin.utils.ValidationUtils.*;
import static asia.decentralab.copin.utils.MapUtils.*;

import java.util.List;
import java.util.Map;

public class BasePositionValidator {
    private static final double DEFAULT_TOLERANCE = 0.0001;

    public static void validatePositionFields(Map<String, Object> position) {
        assertNotNull(position.get("id"), "position id should not be null");
        assertNotNull(position.get("account"), "position account should not be null");
        assertNotNull(position.get("protocol"), "position protocol should not be null");
        assertNotNull(position.get("indexToken"), "position indexToken should not be null");
        assertNotNull(position.get("pair"), "position pair should not be null");
        assertNotNull(position.get("status"), "position status should not be null");
        assertNotNull(position.get("isLong"), "position isLong should not be null");
        assertNotNull(position.get("openBlockTime"), "position openBlockTime should not be null");
        assertNotNull(position.get("openBlockNumber"), "position openBlockNumber should not be null");
        assertNotNull(position.get("averagePrice"), "position averagePrice should not be null");
    }

    public static void validatePositionCalculations(Map<String, Object> position, PositionCalculationResult calc) {
        String account = (String) position.get("account");

        double positionSize = getDouble(position, "size");
        assertInRange(positionSize, 0, 100_000_000,
                "position size", account
        );
        assertCloseToValue(calc.size, positionSize,
                DEFAULT_TOLERANCE, "position size calculation"
        );

        double positionAveragePrice = getDouble(position, "averagePrice");
        assertInRange(positionAveragePrice, 0, 1_000_000,
                "protocol average price", account
        );
        assertCloseToValue(calc.avgPrice, positionAveragePrice,
                DEFAULT_TOLERANCE, "protocol average price calculation"
        );

        String status = (String) position.get("status");
        if ("CLOSE".equals(status)) {
            double positionRealisedPnl = getDouble(position, "realisedPnl");
            assertInRange(positionRealisedPnl, -100_000_000, 100_000_000,
                    "position realisedPnl", account
            );
            assertCloseToValue(calc.realisedPnl, positionRealisedPnl,
                    0.01, "position realisedPnl calculation"
            );

            double positionPnl = getDouble(position, "pnl");
            assertInRange(positionPnl, -100_000_000, 100_000_000,
                    "position pnl", account
            );
            assertCloseToValue(calc.pnl, positionPnl,
                    0.01, "position pnl calculation"
            );

            double positionRealisedRoi = getDouble(position, "realisedRoi");
            assertInRange(positionRealisedRoi, -1_000, 10_000,
                    "position realisedRoi", account
            );
            assertCloseToValue(calc.realisedRoi, positionRealisedRoi,
                    0.01, "position realisedRoi  calculation"
            );

            double positionRoi = getDouble(position, "roi");
            assertInRange(positionRoi, -1_000, 10_000,
                    "position roi", account
            );
            assertCloseToValue(calc.roi, positionRoi,
                    0.01, "position roi calculation"
            );
        } else {
            double positionTotalDecreasePnl = getDouble(position, "totalDecreasePnl");
            assertInRange(positionTotalDecreasePnl, -100_000_000, 100_000_000,
                    "position totalDecreasePnl", account
            );
            assertCloseToValue(calc.realisedPnl, positionTotalDecreasePnl,
                    DEFAULT_TOLERANCE, "position totalDecreasePnl calculation"
            );
        }

        assertEquals(calc.orderCount, getInt(position, "orderCount"),
                "position orderCount"
        );

        assertEquals(calc.orderIncreaseCount, getInt(position, "orderIncreaseCount"),
                "position orderIncreaseCount"
        );

        assertEquals(calc.orderDecreaseCount, getInt(position, "orderDecreaseCount"),
                "position orderDecreaseCount"
        );

        double positionFee = getDouble(position, "fee");
        assertInRange(positionFee, -100_000_000, 100_000_000,
                "position fee", account
        );
        assertCloseToValue(calc.fee, positionFee,
                DEFAULT_TOLERANCE, "position fee"
        );
    }

    public static void validatePositionBusinessRules(Map<String, Object> position,
                                                     List<Map<String, Object>> orders,
                                                     PositionCalculationResult calc) {
        validatePositionFields(position);

        assertFalse(orders.isEmpty(), "Orders list cannot be empty");

        Map<String, Object> firstOrder = orders.get(0);
        String firstOrderType = (String) firstOrder.get("type");
        assertEquals(firstOrder.get("isOpen"), true, "First order should be OPEN");
        boolean isValidFirstOrderType = "OPEN".equals(firstOrderType) || "INCREASE".equals(firstOrderType);
        assertTrue(isValidFirstOrderType,
                String.format("First order type should be OPEN or INCREASE. Found: type=%s", firstOrderType));
        assertEquals(calc.isOpenCount, 1, "Should have exactly one OPEN order");

        Boolean positionIsLong = (Boolean) position.get("isLong");
        for (Map<String, Object> order : orders) {
            String orderType = (String) order.get("type");

            if ("MARGIN_TRANSFERRED".equals(orderType)) {
                continue;
            }

            assertEquals(order.get("isLong"), positionIsLong,
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
            assertEquals(isClose, true,
                    String.format("Closed position requires last order to have isClose=true. " +
                            "Found: type=%s, isClose=%s", lastOrderType, isClose));

            assertTrue(isValidClosingOrderType,
                    String.format("Closed position requires last order type to be DECREASE, CLOSE, or LIQUIDATE. " +
                            "Found: type=%s", lastOrderType));

            assertNotNull(position.get("closeBlockTime"),
                    "Closed position must have closeBlockTime");

            assertNotNull(position.get("closeBlockNumber"),
                    "Closed position must have closeBlockNumber");
        } else {
            if (isValidClosingOrderType) {
                Boolean lastOrderIsClose = (Boolean) lastOrder.get("isClose");
                assertNotNull(lastOrderIsClose,
                        "Order type " + lastOrderType + " must have isClose field");

                assertEquals(lastOrderIsClose, false,
                        "Open position with " + lastOrderType + " order must have isClose=false");
            }
        }
    }
}